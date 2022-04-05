package molecule.datomic.base.marshalling

import java.io.StringReader
import java.lang.{Long => jLong}
import java.net.URI
import java.util
import java.util.{Collections, Date, UUID, List => jList, Collection => jCollection}
import datomic.Util._
import datomic.{Util, Datom => PeerDatom}
import datomicClient.ClojureBridge
import datomicScala.client.api.{Datom => ClientDatom}
import molecule.core.dto.SchemaAttr
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling._
import molecule.core.marshalling.ast._
import molecule.core.marshalling.ast.nodes.Obj
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{Helpers, JavaConversions, Quoted}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade._
import molecule.datomic.base.marshalling.packers.{PackDatoms, PackEntityGraph}
import molecule.datomic.base.marshalling.sorting.{SortDatoms_Client, SortDatoms_Peer, SortRows}
import molecule.datomic.base.util.JavaHelpers
import molecule.datomic.client.facade.{Conn_Client, DatomicDb_Client, Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.{Conn_Peer, DatomicDb_Peer, Datomic_Peer}
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

case class DatomicRpc()(implicit ec: ExecutionContext) extends MoleculeRpc
  with JavaHelpers with DateStrLocal
  with Helpers with ClojureBridge
  with PackEntityGraph with Quoted
  with BooPicklers
  with PackBase
  with JavaConversions
  with SchemaOps {

  // Api ---------------------------------------------

  def transact(
    connProxy: ConnProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReportRPC] = {
    for {
      conn <- getConn(connProxy)
      //      _ = println("DatomicRpc.transact -------\n" + stmtsEdn)
      javaStmts = getJavaStmts(stmtsEdn, uriAttrs)

      _ = javaStmts.forEach(s => println(s))

      txReport <- conn.transact(javaStmts)
    } yield txReportRPC(txReport)
  }

  def retract(
    connProxy: ConnProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReport] = {
    println(stmtsEdn)
    for {
      conn <- getConn(connProxy)
      javaStmts = getJavaStmts(stmtsEdn, uriAttrs)
      txReport <- conn.transact(javaStmts)
    } yield txReportRPC(txReport)
  }

  private def txReportRPC(txReport: TxReport): TxReportRPC = {
    TxReportRPC(txReport.t, txReport.tx, txReport.txInstant, txReport.eids, txReport.txData, txReport.toString)
  }

  def schemaHistoryQuery2packed(
    connProxy: ConnProxy,
    datalogQuery: String,
    obj: Obj,
    schemaAttrs: Seq[SchemaAttr],
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[(Int, String)] = {
    for {
      conn <- getConn(connProxy)
      rawRows <- QuerySchemaHistory(conn).fetchSchemaHistory(schemaAttrs, datalogQuery)
    } yield {
      val rows = if (sortCoordinates.nonEmpty) SortRows(rawRows, sortCoordinates).get else rawRows
      (rawRows.size, Flat2packed(obj, rows, -1, 0).getPacked)
    }
  }

  def query2packed(
    connProxy: ConnProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, String, String)],
    ll: Seq[(Int, String, Seq[String])],
    lll: Seq[(Int, String, Seq[Seq[String]])],
    limit: Int,
    offset: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[(Int, String)] = Future {
    try {
      val log = new log
      val t   = TimerPrint("DatomicRpc")

      //    println("------------------------------")
      //      println("================================================================================")
      //      println(datalogQuery)
      //      if (rules.nonEmpty) {
      //        println("Rules:")
      //        rules foreach println
      //      }
      //      println("l  : " + l)
      //      println("ll : " + ll)
      //      println("lll: " + lll)

      val inputs    = unmarshallInputs(l ++ ll ++ lll)
      val allInputs = if (rules.nonEmpty) rules ++ inputs else inputs
      for {
        conn <- getConn(connProxy)
        rawRows <- conn.rawQuery(datalogQuery, allInputs)
      } yield {
        val flatTotalCount = rawRows.size
        //        val maxRows      = if (limit == -1 || flatRowCount < limit) flatRowCount else limit
        val queryTime      = t.delta
        val space          = " " * (70 - datalogQuery.split('\n').last.length)
        val time           = qTime(queryTime)
        val timeRight      = " " * (8 - time.length) + time

        log("================================================================================")
        log(datalogQuery + space + timeRight)
        if (allInputs.nonEmpty)
          log(allInputs.mkString("Inputs:\n", "\n", ""))

        //      log(datalogQuery + space + timeRight + "  " + conn.asInstanceOf[Conn_Peer].peerConn.db)
        //      log(s"\n---- Querying Datomic... --------------------")
        //      log(datalogQuery)
        //      log(qTime(queryTime) + "  " + datalogQuery)
        //      log("connProxy uuid: " + connProxy.uuid)
        //      log("Query time  : " + thousands(queryTime) + " ms")
        //        log("flatTotalCount: " + flatTotalCount)
        //        log("limit         : " + limit)
        //        log("offset        : " + offset)
        //        log("maxRows     : " + (if (maxRows == -1) "all" else maxRows))

        log("-------------------------------")
        //      log(obj.toString)
        //      log("-------------------------------")
        //      log(refIndexes.mkString("\n"))
        //      log("-------------------------------")
        //      log(tacitIndexes.mkString("\n"))
        //      log("-------------------------------")
        rawRows.forEach(row => log(row.toString))
        //        log("-------------------------------")
        //        sortCoordinates.foreach(level => log(level.mkString("List(\n  ", ",\n  ", ")")))
        log.print()

        val (totalCount, packed) = if (flatTotalCount == 0) {
          (0, "")
        } else if (isOptNested) {
          // Optional nested
          if (offset > flatTotalCount) {
            (flatTotalCount, "")
          } else {
            val sortedRows = if (sortCoordinates.nonEmpty && sortCoordinates.head.nonEmpty)
              SortRows(rawRows, sortCoordinates).get else rawRows
            (
              flatTotalCount,
              OptNested2packed(obj, sortedRows, limit, offset, refIndexes, tacitIndexes, sortCoordinates).getPacked
            )
          }

        } else if (nestedLevels == 0) {
          // Flat
          if (offset > flatTotalCount) {
            (flatTotalCount, "")
          } else {
            val sortedRows = if (sortCoordinates.flatten.nonEmpty) SortRows(rawRows, sortCoordinates).get else rawRows
            (flatTotalCount, Flat2packed(obj, sortedRows, limit, offset).getPacked)
          }

        } else {
          // Nested
          val sortedRows = SortRows(rawRows, sortCoordinates).get
          Nested2packed(obj, sortedRows, flatTotalCount, limit, offset, nestedLevels).getRowCountAndPacked
        }

        //        println(s"-------------------------------" + packed)
        //        log("Sending data to client... Total server time: " + t.msTotal)
        (totalCount, packed)
      }
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }.flatten


  def index2packed(
    connProxy: ConnProxy,
    api: String,
    index: String,
    indexArgs: IndexArgs,
    attrs: Seq[String],
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[(Int, String)] = {
    var totalCount = 0
    for {
      conn <- getConn(connProxy)
      db <- conn.db
      packer = PackDatoms(conn, db, attrs, index, indexArgs)
      sb <- {
        api match {
          case "datoms" =>
            db match {
              case db: DatomicDb_Peer        =>
                val datomicIndex = index match {
                  case "EAVT" => datomic.Database.EAVT
                  case "AEVT" => datomic.Database.AEVT
                  case "AVET" => datomic.Database.AVET
                  case "VAET" => datomic.Database.VAET
                }
                db.datoms(datomicIndex, packer.args: _*).flatMap { datoms =>
                  if (sortCoordinates.nonEmpty) {
                    SortDatoms_Peer(conn, db, attrs, sortCoordinates, datoms.iterator).getPacked.map {
                      case (count, packed) =>
                        totalCount = count
                        packed
                    }
                  } else {
                    val datom2packed = packer.getPeerDatom2packed(None)
                    datoms.asScala.foldLeft(Future(new StringBuffer())) {
                      case (sbFut, datom) => sbFut.flatMap { sb =>
                        totalCount += 1
                        datom2packed(sb, datom)
                      }
                    }
                  }
                }
              case adhocDb: DatomicDb_Client =>
                val datomicIndex = index match {
                  case "EAVT" => ":eavt"
                  case "AEVT" => ":aevt"
                  case "AVET" => ":avet"
                  case "VAET" => ":vaet"
                }
                adhocDb.datoms(datomicIndex, packer.args).flatMap { datoms =>
                  if (sortCoordinates.nonEmpty) {
                    SortDatoms_Client(conn, db, attrs, sortCoordinates, datoms.iterator).getPacked.map {
                      case (count, packed) =>
                        totalCount = count
                        packed
                    }
                  } else {
                    val datom2packed = packer.getClientDatom2packed(None)
                    datoms.iterator.asScala.foldLeft(Future(new StringBuffer())) {
                      case (sbFut, datom) => sbFut.flatMap { sb =>
                        totalCount += 1
                        datom2packed(sb, datom)
                      }
                    }
                  }
                }
            }

          case "indexRange" =>
            db match {
              case db: DatomicDb_Peer   =>
                val datom2packed = packer.getPeerDatom2packed(None)
                val startValue   = if (indexArgs.v.isEmpty) null else castTpeV(indexArgs.tpe, indexArgs.v)
                val endValue     = if (indexArgs.v2.isEmpty) null else castTpeV(indexArgs.tpe2, indexArgs.v2)
                db.indexRange(indexArgs.a, startValue, endValue).flatMap { datoms =>
                  if (sortCoordinates.nonEmpty) {
                    SortDatoms_Peer(conn, db, attrs, sortCoordinates, datoms.iterator).getPacked.map {
                      case (count, packed) =>
                        totalCount = count
                        packed
                    }
                  } else {
                    datoms.asScala.foldLeft(Future(new StringBuffer())) {
                      case (sbFut, datom) => sbFut.flatMap { sb =>
                        totalCount += 1
                        datom2packed(sb, datom)
                      }
                    }
                  }
                }
              case db: DatomicDb_Client =>
                val datom2packed = packer.getClientDatom2packed(None)
                val startValue   = if (indexArgs.v.isEmpty) None else Some(castTpeV(indexArgs.tpe, indexArgs.v))
                val endValue     = if (indexArgs.v2.isEmpty) None else Some(castTpeV(indexArgs.tpe2, indexArgs.v2))
                db.indexRange(indexArgs.a, startValue, endValue).flatMap { datoms =>
                  if (sortCoordinates.nonEmpty) {
                    SortDatoms_Client(conn, db, attrs, sortCoordinates, datoms.iterator).getPacked.map {
                      case (count, packed) =>
                        totalCount = count
                        packed
                    }
                  } else {
                    datoms.iterator.asScala.foldLeft(Future(new StringBuffer())) {
                      case (sbFut, datom) => sbFut.flatMap { sb =>
                        totalCount += 1
                        datom2packed(sb, datom)
                      }
                    }
                  }
                }
            }

          case "txRange" =>
            // Flatten transaction datoms to uniform tuples return type
            conn match {
              case conn: Conn_Peer =>
                val from  = if (indexArgs.v.isEmpty) null else castTpeV(indexArgs.tpe, indexArgs.v)
                val until = if (indexArgs.v2.isEmpty) null else castTpeV(indexArgs.tpe2, indexArgs.v2)
                if (sortCoordinates.nonEmpty) {
                  val datoms = new util.ArrayList[PeerDatom]()
                  conn.peerConn.log.txRange(from, until).asScala.foreach {
                    txMap => datoms.addAll(txMap.get(datomic.Log.DATA).asInstanceOf[jList[PeerDatom]])
                  }
                  SortDatoms_Peer(conn, db, attrs, sortCoordinates, datoms.iterator).getPacked.map {
                    case (count, packed) =>
                      totalCount = count
                      packed
                  }
                } else {
                  conn.peerConn.log.txRange(from, until).asScala.foldLeft(Future(new StringBuffer())) {
                    case (sbFut, txMap) =>
                      val datom2packed = packer
                        .getPeerDatom2packed(Some(txMap.get(datomic.Log.T).asInstanceOf[Long]))
                      txMap.get(datomic.Log.DATA).asInstanceOf[jList[PeerDatom]].asScala.foldLeft(sbFut) {
                        case (sbFut, datom) => sbFut.flatMap { sb =>
                          totalCount += 1
                          datom2packed(sb, datom)
                        }
                      }
                  }
                }

              case conn: Conn_Client =>
                val from  = if (indexArgs.v.isEmpty) None else Some(castTpeV(indexArgs.tpe, indexArgs.v))
                val until = if (indexArgs.v2.isEmpty) None else Some(castTpeV(indexArgs.tpe2, indexArgs.v2))
                if (sortCoordinates.nonEmpty) {
                  val datoms = new util.ArrayList[ClientDatom]()
                  conn.clientConn.txRange(from, until).foreach {
                    case (_, txDatoms) => txDatoms.foreach(d => datoms.add(d))
                  }
                  SortDatoms_Client(conn, db, attrs, sortCoordinates, datoms.iterator).getPacked.map {
                    case (count, packed) =>
                      totalCount = count
                      packed
                  }
                } else {
                  conn.clientConn.txRange(from, until).foldLeft(Future(new StringBuffer())) {
                    case (sbFut, (t, datoms)) =>
                      val datom2packed: (StringBuffer, ClientDatom) => Future[StringBuffer] =
                        packer.getClientDatom2packed(Some(t))
                      datoms.foldLeft(sbFut) {
                        case (sbFut, datom) => sbFut.flatMap { sb =>
                          totalCount += 1
                          datom2packed(sb, datom)
                        }
                      }
                  }
                }
            }
        }
      }
    } yield {
      //      val s = sb.toString
      //      println("--------------- packed ----------------" + s)
      //      s
      (totalCount, sb.toString)
    }
  }


  // Schema ---------------------------------------------

  // Presuming a datalog query returning rows of single values.
  // Card-many attributes should therefore not be returned as Sets.
  def getAttrValues(
    connProxy: ConnProxy,
    datalogQuery: String,
    card: Int,
    tpe: String
  ): Future[List[String]] = {
    for {
      conn <- getConn(connProxy)
      rows0 <- conn.rawQuery(datalogQuery)
    } yield {
      val cast = if (tpe == "Date" && card != 3)
        (v: Any) => date2str(v.asInstanceOf[Date])
      else
        (v: Any) => v.toString
      var vs   = List.empty[String]
      rows0.forEach(row => vs = vs :+ cast(row.get(0)))
      vs
    }
  }

  def getEntityAttrKeys(
    connProxy: ConnProxy,
    query: String
  ): Future[List[String]] = {
    var list = List.empty[String]
    for {
      conn <- getConn(connProxy)
      rows <- conn.rawQuery(query)
    } yield {
      rows.forEach { row =>
        list = row.get(0).toString :: list
      }
      list.sorted
    }
  }

  final def getEnumHistory(connProxy: ConnProxy)
  : Future[List[(String, Int, Long, Date, String, Boolean)]] = {
    for {
      conn <- getConn(connProxy)
      res <- getEnumHistory_(conn)
    } yield res
  }


  // Entity api ---------------------------------------------------

  final def rawValue(connProxy: ConnProxy, eid: Long, attr: String): Future[String] = {
    getDatomicEntity(connProxy, eid)
      .flatMap(_.rawValue(attr))
      .map(res => entityList2packed(List(attr -> res)))
  }

  final def asMap(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.asMap(depth, maxDepth)).map(entityMap2packed)
  }

  final def asList(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.asList(depth, maxDepth)).map(entityList2packed)
  }


  final def attrs(connProxy: ConnProxy, eid: Long): Future[List[String]] = {
    getDatomicEntity(connProxy, eid).flatMap(_.attrs)
  }


  final def apply(connProxy: ConnProxy, eid: Long, attr: String): Future[String] = {
    getDatomicEntity(connProxy, eid)
      .flatMap(_.apply[Any](attr))
      .map(_.fold("")(v => entityMap2packed(Map(attr -> v))))
  }

  final def apply(connProxy: ConnProxy, eid: Long, attrs: List[String]): Future[List[String]] = {
    val attr1 :: attr2 :: moreAttrs = attrs
    getDatomicEntity(connProxy, eid)
      .flatMap(_.apply(attr1, attr2, moreAttrs: _*))
      .map { optValues =>
        attrs.zip(optValues).map { case (attr, optV) =>
          optV.fold("")(v => entityMap2packed(Map(attr -> v)))
        }
      }
  }

  final def graphDepth(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String] = {
    // Use list to guarantee order of attributes for packing
    getDatomicEntity(connProxy, eid).flatMap(_.asList(1, maxDepth)).map(entityList2packed)
  }

  final def graphCode(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.graphCode(maxDepth))
  }

  private def getDatomicEntity(connProxy: ConnProxy, eid: Any): Future[DatomicEntity] = {
    for {
      conn <- getConn(connProxy)
      db <- conn.db
    } yield db.entity(conn, eid)
  }


  // Connection pool ---------------------------------------------

  // todo - this is primitive, is a more correct implementation needed?
  private val connectionPool = mutable.HashMap.empty[String, Future[Conn_Jvm]]

  def clearConnPool: Future[Unit] = Future {
    //    println(s"Connection pool with ${connectionPool.size} connections cleared.")
    connectionPool.clear()
  }

  private def getConn(connProxy: ConnProxy): Future[Conn_Jvm] = {
    val futConn             = connectionPool.getOrElse(connProxy.uuid, getFreshConn(connProxy))
    val futConnTimeAdjusted = futConn.map { conn =>
      conn.updateAdhocDbView(connProxy.adhocDbView)
      conn.updateTestDbView(connProxy.testDbView, connProxy.testDbStatus)
      conn
    }
    connectionPool(connProxy.uuid) = futConnTimeAdjusted
    futConnTimeAdjusted
  }

  private def printStackTrace(exc: Throwable): Unit = {
    println(exc)
    exc.getStackTrace.toList.foreach(println)
    println("----")
  }

  private def getFreshConn(connProxy: ConnProxy): Future[Conn_Jvm] = connProxy match {
    case proxy@DatomicPeerProxy(protocol, dbIdentifier, schema, _, _, _, _, _, _) =>
      protocol match {
        case "mem" =>
          Datomic_Peer.recreateDbFromEdn(proxy, schema)
            .recoverWith { case exc =>
              printStackTrace(exc)
              Future.failed[Conn_Jvm](MoleculeException(exc.getMessage))
            }

        case "free" | "dev" | "pro" =>
          Datomic_Peer.connect(proxy, protocol, dbIdentifier)
            .recoverWith { case exc =>
              printStackTrace(exc)
              Future.failed[Conn_Jvm](MoleculeException(exc.getMessage))
            }

        case other =>
          Future.failed(MoleculeException(
            s"\nCan't serve Peer protocol `$other`."
          ))
      }

    case proxy@DatomicDevLocalProxy(protocol, system, storageDir, dbName, schema, _, _, _, _, _, _) =>
      val devLocal = Datomic_DevLocal(system, storageDir)
      protocol match {
        case "mem" =>
          devLocal.recreateDbFromEdn(schema, proxy)
            .recoverWith { case exc =>
              printStackTrace(exc)
              Future.failed[Conn_Jvm](MoleculeException(exc.getMessage))
            }

        case "dev" | "pro" =>
          devLocal.connect(proxy, dbName)
            .recoverWith { case exc =>
              printStackTrace(exc)
              Future.failed[Conn_Jvm](MoleculeException(exc.getMessage))
            }

        case other =>
          Future.failed(MoleculeException(
            s"\nCan't serve DevLocal protocol `$other`."
          ))
      }

    case proxy@DatomicPeerServerProxy(accessKey, secret, endpoint, dbName, _, _, _, _, _, _, _) =>
      Datomic_PeerServer(accessKey, secret, endpoint).connect(proxy, dbName)
        .recoverWith { case exc => Future.failed[Conn_Jvm](MoleculeException(exc.getMessage)) }
  }


  // Helpers -------------------------------------------------

  def basisT(connProxy: ConnProxy): Future[Long] = {
    for {
      conn <- getConn(connProxy)
      db <- conn.db
      t <- db.basisT
    } yield t
  }

  // Unmarshall to Datomic java types
  private def unmarshallInputs(lists: Seq[(Int, String, Any)]): Seq[Object] = {
    lists.sortBy(_._1).map {
      case (_, tpe, rawValue) =>
        val cast = tpe match {
          case "String"     => if (isEnum(rawValue)) (v: String) => getEnum(v) else (v: String) => v
          case "Int"        => (v: String) => new java.lang.Long(v)
          case "Long"       => (v: String) => new java.lang.Long(v)
          case "Double"     => (v: String) => new java.lang.Double(v)
          case "Boolean"    => (v: String) => v.toBoolean.asInstanceOf[Object]
          case "Date"       => (v: String) => str2date(v).asInstanceOf[Object]
          case "URI"        => (v: String) => new java.net.URI(v).asInstanceOf[Object]
          case "UUID"       => (v: String) => java.util.UUID.fromString(v).asInstanceOf[Object]
          case "BigInt"     => (v: String) => new java.math.BigInteger(v).asInstanceOf[Object]
          case "BigDecimal" => (v: String) => new java.math.BigDecimal(v).asInstanceOf[Object]
          case "Any"        => (s: String) =>
            val v = s.drop(10)
            s.take(10) match {
              case "String    " => if (isEnum(v)) getEnum(v) else v
              case "Int       " => new java.lang.Long(v)
              case "Long      " => new java.lang.Long(v)
              case "Double    " => new java.lang.Double(v)
              case "Boolean   " => v.toBoolean.asInstanceOf[Object]
              case "Date      " => str2date(v).asInstanceOf[Object]
              case "URI       " => new URI(v).asInstanceOf[Object]
              case "UUID      " => UUID.fromString(v).asInstanceOf[Object]
              case "BigInt    " => new java.math.BigInteger(v).asInstanceOf[Object]
              case "BigDecimal" => new java.math.BigDecimal(v).asInstanceOf[Object]
            }
          case _            => throw MoleculeException(s"Unexpected type to cast: $tpe")
        }

        rawValue match {
          case l: Seq[_] =>
            Util.list(l.collect {
              case l2: Seq[_] =>
                val Seq(k, v2: String) = l2
                Util.list(k.toString.asInstanceOf[Object], cast(v2))

              case v1: String => cast(v1)
            }: _*)

          case v: String => cast(v)
          case _         => throw MoleculeException("Unexpected input values")
        }
    }
  }

  // Necessary for `readString` to encode uri in transactions
  require("clojure.core.async")

  def getJavaStmts(
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): jList[AnyRef] = {
    val stmts = readAll(new StringReader(stmtsEdn)).get(0).asInstanceOf[jList[AnyRef]]
    if (uriAttrs.isEmpty) {
      stmts
    } else {
      def uri(s: AnyRef): AnyRef = {
        // Depends on requiring clojure.core.async
        readString(s"""#=(new java.net.URI "$s")""")
      }
      val stmtsSize = stmts.size()
      val newStmts  = new util.ArrayList[jList[_]](stmtsSize)
      stmts.forEach { stmtRaw =>
        val stmt = stmtRaw.asInstanceOf[jList[AnyRef]]
        if (uriAttrs.contains(stmt.get(2).toString)) {
          val uriStmt = stmt.get(0).toString match {
            case ":db/add"     => list(stmt.get(0), stmt.get(1), stmt.get(2), uri(stmt.get(3)))
            case ":db/retract" => list(stmt.get(0), stmt.get(1), stmt.get(2), uri(stmt.get(3)))
            case ":db.fn/cas"  => list(stmt.get(0), stmt.get(1), stmt.get(2), uri(stmt.get(3)), uri(stmt.get(4)))
            case _             => stmt
          }
          newStmts.add(uriStmt)
        } else {
          newStmts.add(stmt)
        }
      }
      Collections.unmodifiableList(newStmts)
    }
  }

  def qTime(queryTime: Long): String = {
    val indents = 5 - queryTime.toString.length
    " " * indents + thousands(queryTime) + " ms"
  }
}
