package molecule.datomic.base.marshalling

import java.io.StringReader
import java.util
import java.util.{Collections, Date, List => jList, Set => jSet}
import boopickle.Default._
import cats.implicits._
import datomic.Util._
import datomic.{Peer, Util}
import datomicClient.ClojureBridge
import molecule.core.marshalling._
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{DateHandling, Helpers}
import molecule.datomic.base.facade.{Conn, Conn_Datomic213, TxReportRPC}
import molecule.datomic.client.facade.{Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo.datomicProtocol
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal

object DatomicRpc extends MoleculeRpc
  with DateHandling with DateStrLocal with Helpers with ClojureBridge {

  // Cache Conn and QueryExecutors ---------------------------------------------

  // (datalogQuery, inputs) => raw data
  type QueryExecutor = (String, Seq[AnyRef]) => Future[util.Collection[util.List[AnyRef]]]

  // todo: proper caching
  val connCache          = mutable.Map.empty[String, Future[Conn]]
  val queryExecutorCache = mutable.Map.empty[String, Future[QueryExecutor]]


  private def getCachedConn(
    dbProxy: DbProxy
  ): Future[Conn] = {
    connCache.getOrElse(
      dbProxy.uuid,
      {
        val conn = dbProxy match {
          case DatomicInMemProxy(edns, _, _) =>
            //          Datomic_Peer.connect("mem", dbIdentifier)
            Datomic_Peer.recreateDbFromEdn(edns)

          case DatomicPeerProxy(protocol, dbIdentifier, _, _) =>
            if (datomicProtocol != protocol) {
              throw new RuntimeException(
                s"\nProject is built with datomic `$datomicProtocol` protocol and " +
                  s"cannot serve supplied `$protocol` protocol. " +
                  s"\nPlease change the build setup or your ConnProxy protocol"
              )
            }
            protocol match {
              case "dev" | "free" => Datomic_Peer.connect(protocol, dbIdentifier)
              case "mem"          => throw new IllegalArgumentException(
                "Please connect with `DatomicInMemProxy` to get an in-memory db.")
            }

          case DatomicDevLocalProxy(system, storageDir, dbName, _, _) =>
            Datomic_DevLocal(system, storageDir).connect(dbName)

          case DatomicPeerServerProxy(accessKey, secret, endpoint, dbName, _) =>
            Datomic_PeerServer(accessKey, secret, endpoint).connect(dbName)
        }
        connCache(dbProxy.uuid) = conn
        conn
      }
    )
  }

  private def getCachedQueryExecutor(
    dbProxy: DbProxy
  ): Future[QueryExecutor] = {
    queryExecutorCache.getOrElse(
      dbProxy.uuid,
      {
        val queryExecutor = getCachedConn(dbProxy).map { conn =>
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            conn.qRaw(conn.db, datalogQuery, inputs)
          }
        }
        queryExecutorCache(dbProxy.uuid) = queryExecutor
        queryExecutor
      }
    )
  }


  // Api ---------------------------------------------

  def transact(
    dbProxy: DbProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReportRPC] = {
    println(stmtsEdn)
    for {
      conn <- getCachedConn(dbProxy)
      txReport <- conn.transactRaw(javaStmts(stmtsEdn, uriAttrs))
    } yield TxReportRPC(
      txReport.eids, txReport.t, txReport.tx, txReport.inst, txReport.toString
    )
  }

  def query(
    dbProxy: DbProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows: Int,
    indexes: List[(Int, Int, Int, Int)]
  ): Future[QueryResult] = {
    val log                 = new log
    val t                   = TimerPrint("DatomicRpc")
    val inputs: Seq[Object] = rules +: unmarshallInputs(l ++ ll ++ lll)
    for {
      queryEx <- getCachedQueryExecutor(dbProxy)
      allRows <- queryEx(datalogQuery, inputs)
    } yield {
      val rowCountAll = allRows.size
      val rowCount    = if (maxRows == -1 || rowCountAll < maxRows) rowCountAll else maxRows

      val queryTime = t.delta
      log(datalogQuery + "         " + qTime(queryTime))
      //      log(s"\n---- Querying Datomic... --------------------")
      //      log(datalogQuery)
      //      log(qTime(queryTime) + "  " + datalogQuery)
      //      log("dbProxy uuid: " + dbProxy.uuid)
      //      log("Query time  : " + thousands(queryTime) + " ms")
      //      log("rowCountAll : " + rowCountAll)
      //      log("maxRows     : " + (if (maxRows == -1) "all" else maxRows))
      //      log("rowCount    : " + rowCount)

      val queryResult = Rows2QueryResult(
        allRows, rowCountAll, rowCount, queryTime, indexes
      ).get

      // log("QueryResult: " + queryResult)
      //        log("Rows2QueryResult took " + t.ms)
      //        log("Sending data to client... Total server time: " + t.msTotal)
      log.print
      queryResult
    }
  }


  def getAttrValues(
    dbProxy: DbProxy,
    datalogQuery: String,
    card: Int,
    tpe: String
  ): Future[List[String]] = {
    var vs = List.empty[String]
    for {
      queryEx <- getCachedQueryExecutor(dbProxy)
      rows0 <- queryEx(datalogQuery, Nil)
    } yield {
      val rows = rows0.iterator
      if (rows.hasNext) {
        val rawValue = rows.next().get(0)
        // marshall raw value. Dates need to be standardized
        card match {
          case 1 => rawValue match {
            case d: Date => vs = List(date2strLocal(d))
            case v       => vs = List(v.toString)
          }
          case 2 => tpe match {
            case "Date" => rawValue.asInstanceOf[jSet[_]].forEach(v =>
              vs = vs :+ date2strLocal(v.asInstanceOf[Date])
            )
            case _      => rawValue.asInstanceOf[jSet[_]].forEach(v =>
              vs = vs :+ v.toString
            )
          }
          case 3 => rawValue.asInstanceOf[jSet[_]].forEach(v => vs = vs :+ v.toString)
        }
        vs
      } else Nil
    }
  }

  def entityAttrKeys(
    dbProxy: DbProxy,
    eid: Long
  ): Future[List[String]] = {
    val query = s"[:find ?a1 :where [$eid ?a _][?a :db/ident ?a1]]"
    var list  = List.empty[String]
    for {
      queryEx <- getCachedQueryExecutor(dbProxy)
      rows <- queryEx(query, Nil)
    } yield {
      rows.forEach { row =>
        list = row.get(0).toString :: list
      }
      list.sorted
    }
  }


  // Helpers -------------------------------------------------

  def javaStmts(
    edn: String,
    uriAttrs: Set[String]
  ): jList[AnyRef] = {
    val stmts = readAll(new StringReader(edn)).get(0).asInstanceOf[jList[AnyRef]]
    if (uriAttrs.isEmpty) {
      stmts
    } else {
      // Necessary for `readString` to encode uri
      require("clojure.core.async")
      def uri(s: AnyRef): AnyRef = readString(s"""#=(new java.net.URI "$s")""")
      val stmtsSize = stmts.size()
      val newStmts  = new util.ArrayList[jList[_]](stmtsSize)
      stmts.forEach { stmtRaw =>
        val stmt = stmtRaw.asInstanceOf[jList[AnyRef]]
        if (uriAttrs.contains(stmt.get(2).toString)) {
          val uriStmt = stmt.get(0).toString match {
            case ":db/add"    => list(stmt.get(0), stmt.get(1), stmt.get(2), uri(stmt.get(3)))
            case ":db.fn/cas" => list(stmt.get(0), stmt.get(1), stmt.get(2), uri(stmt.get(3)), uri(stmt.get(4)))
            case _            => stmt
          }
          newStmts.add(uriStmt)
        } else {
          newStmts.add(stmt)
        }
      }
      Collections.unmodifiableList(newStmts)
    }
  }

  def qTime(queryTime: Long) = {
    val indents = 5 - queryTime.toString.length
    " " * indents + thousands(queryTime) + " ms"
  }

  private def unmarshallInputs(lists: Seq[(Int, AnyRef)]): Seq[Object] = {
    lists.sortBy(_._1).map(_._2).map {
      case l: Seq[_] => Util.list(l.map {
        case l2: Seq[_] =>
          Util.list(l2.map(v => cast(v.asInstanceOf[(String, String)])): _*)

        case pair@(_: String, _: String) =>
          cast(pair.asInstanceOf[(String, String)])

        case _ => sys.error("Unexpected input values")
      }: _*)

      case pair@(_: String, _: String) =>
        cast(pair)

      case _ =>
        sys.error("Unexpected input values")
    }
  }


  // Todo: this works but seems like a hack that would be nice to avoid although
  //  the impact of a few input variables is negible.
  // To avoid type combination explosions from multiple inputs of various types
  // to be transferred with autowire/boopickle, we cast all input variable values
  // as String on the client and then cast them back to their original type here
  // and pass them as Object's to Datomic.
  def cast(pair: (String, String)): Object = pair match {
    case ("String", v)     => v.asInstanceOf[Object]
    case ("Int", v)        => v.toInt.asInstanceOf[Object]
    case ("Long", v)       => v.toLong.asInstanceOf[Object]
    case ("Double", v)     => v.toDouble.asInstanceOf[Object]
    case ("BigInt", v)     => BigInt.apply(v).asInstanceOf[Object]
    case ("BigDecimal", v) => BigDecimal(v).asInstanceOf[Object]
    case ("Boolean", v)    => v.toBoolean.asInstanceOf[Object]
    case ("Date", v)       => str2date(v).asInstanceOf[Object]
    case ("UUID", v)       => java.util.UUID.fromString(v).asInstanceOf[Object]
    case ("URI", v)        => new java.net.URI(v).asInstanceOf[Object]
    case _                 => sys.error("Unexpected input pair to cast")
  }
}
