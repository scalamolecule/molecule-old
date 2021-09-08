package molecule.datomic.base.marshalling

import java.io.StringReader
import java.util
import java.util.{Collections, Date, List => jList, Set => jSet}
import datomic.Util
import datomic.Util._
import datomicClient.ClojureBridge
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling._
import molecule.core.marshalling.nodes.Obj
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{DateHandling, Helpers}
import molecule.datomic.base.facade._
import molecule.datomic.client.facade.{Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo.datomicProtocol
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

object DatomicRpc extends MoleculeRpc
  with DateHandling with DateStrLocal with Helpers with ClojureBridge with Serializations {

  // Necessary for `readString` to encode uri in transactions
  require("clojure.core.async")


  // Api ---------------------------------------------

  def transact(
    connProxy: ConnProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReportRPC] = {
    for {
      conn <- getConn(connProxy)

      _ = println(stmtsEdn)

      txReport <- conn.transactRaw(getJavaStmts(stmtsEdn, uriAttrs))
    } yield {
      TxReportRPC(
        txReport.eids, txReport.t, txReport.tx, txReport.inst, txReport.toString
      )
    }
  }

  def query2packed(
    connProxy: ConnProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows0: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]]
  ): Future[String] = try {
    val log    = new log
    val t      = TimerPrint("DatomicRpc")
    val inputs = unmarshallInputs(l ++ ll ++ lll)
    val allInputs = if (rules.nonEmpty) rules ++ inputs else inputs
    for {
      conn <- getConn(connProxy)
      allRows <- conn.qRaw(conn.db, datalogQuery, allInputs)
    } yield {
      val rowCountAll = allRows.size
      val maxRows     = if (maxRows0 == -1 || rowCountAll < maxRows0) rowCountAll else maxRows0
      val queryTime   = t.delta
      val space       = " " * (70 - datalogQuery.split('\n').last.length)
      val time        = qTime(queryTime)
      val timeRight   = " " * (8 - time.length) + time

      log("###### DatomicRpc #########################################")
      log(datalogQuery + space + timeRight)
      if(allInputs.nonEmpty) log(allInputs.mkString("Inputs:\n", "\n", ""))
      //      log(datalogQuery + space + timeRight + "  " + conn.asInstanceOf[Conn_Peer].peerConn.db)
      //      log(s"\n---- Querying Datomic... --------------------")
      //      log(datalogQuery)
      //      log(qTime(queryTime) + "  " + datalogQuery)
      //      log("connProxy uuid: " + connProxy.uuid)
      //      log("Query time  : " + thousands(queryTime) + " ms")
      //      log("rowCountAll : " + rowCountAll)
      //      log("maxRows     : " + (if (maxRows == -1) "all" else maxRows))
      //      log("rowCount    : " + rowCount)

      //      val allRows2 = allRows
      //      val it       = allRows2.iterator()
      //      //            it.next()
      //      val v        = it.next().get(0)
      //      println(s"v1: $v  ${v.getClass}")

      //      val rows = new jArrayList(allRows).subList(0, maxRows)

      log("-------------------------------")
      log(obj.toString)
      log("-------------------------------")
      allRows.forEach(row => log(row.toString))

      val packed = if (isOptNested)
        OptNested2packed(obj, allRows, maxRows, refIndexes, tacitIndexes).getPacked
      else if (nestedLevels == 0)
        Flat2packed(obj, allRows, maxRows).getPacked
      else
        Nested2packed(obj, allRows, nestedLevels).getPacked

      log("-------------------------------" + packed)
      log.print
      //        log("Sending data to client... Total server time: " + t.msTotal)
      packed
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }


  def getAttrValues(
    connProxy: ConnProxy,
    datalogQuery: String,
    card: Int,
    tpe: String
  ): Future[List[String]] = {
    var vs = List.empty[String]
    for {
      conn <- getConn(connProxy)
      rows0 <- conn.qRaw(conn.db, datalogQuery, Nil)
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
    connProxy: ConnProxy,
    eid: Long
  ): Future[List[String]] = {
    val datalogQuery = s"[:find ?a1 :where [$eid ?a _][?a :db/ident ?a1]]"
    var list         = List.empty[String]
    for {
      conn <- getConn(connProxy)
      rows <- conn.qRaw(conn.db, datalogQuery, Nil)
    } yield {
      rows.forEach { row =>
        list = row.get(0).toString :: list
      }
      list.sorted
    }
  }

  def retract(
    connProxy: ConnProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReport] = {
    println(stmtsEdn)
    for {
      conn <- getConn(connProxy)
      txReport <- conn.transactRaw(getJavaStmts(stmtsEdn, uriAttrs))
    } yield TxReportRPC(
      txReport.eids, txReport.t, txReport.tx, txReport.inst, txReport.toString
    )
  }


  // Cached Conn ---------------------------------------------

  private val connCache = mutable.Map.empty[String, Future[Conn]]

  private def getConn(
    connProxy: ConnProxy
  ): Future[Conn] = {
    var msg     = s"--- " + connProxy.uuid.take(5) + "  " + connCache.keySet.map(_.take(5))
    val futConn = connCache.getOrElse(
      connProxy.uuid,
      {
        msg = s"============= Conn CACHING ============= " + connProxy.uuid.take(5) + "  " + connCache.keySet.map(_.take(5))
        connProxy match {
          case DatomicInMemProxy(schemaPeer, _, _, _, _, _) =>
            //            println("==============================================")
            Datomic_Peer.recreateDbFromEdn(schemaPeer)

          case DatomicPeerProxy(protocol, dbIdentifier, _, _, _, _, _) =>
            if (datomicProtocol != protocol) {
              throw new RuntimeException(
                s"\nProject is built with datomic `$datomicProtocol` protocol and " +
                  s"cannot serve supplied `$protocol` protocol. " +
                  s"\nPlease change the build setup or your Conn_Js protocol"
              )
            }
            protocol match {
              case "dev" | "free" => Datomic_Peer.connect(protocol, dbIdentifier)
              case "mem"          => throw new IllegalArgumentException(
                "Please connect with `DatomicInMemProxy` to get an in-memory db.")
            }

          case DatomicDevLocalProxy(system, storageDir, dbName, _, _, _, _, _) =>
            Datomic_DevLocal(system, storageDir).connect(dbName)

          case DatomicPeerServerProxy(accessKey, secret, endpoint, dbName, _, _, _, _, _) =>
            Datomic_PeerServer(accessKey, secret, endpoint).connect(dbName)
        }
      }
    )

    val futConnTimeAdjusted = futConn.map { conn =>
      conn.updateAdhocDbView(connProxy.adhocDbView)
      conn.updateTestDbView(connProxy.testDbView, connProxy.testDbStatus)
      conn
    }
    connCache(connProxy.uuid) = futConnTimeAdjusted

    //    val c = Await.result(futConnTimeAdjusted, 10.seconds)
    //    println("----- " + c.asInstanceOf[Conn_Peer].peerConn.db)
    //    println("----- " + connProxy.testDbStatus + "  " + connProxy.testDbView + "  " + c.asInstanceOf[Conn_Peer].peerConn.db)
    //    println("----- " + c.connProxy.testDbStatus + "  " + c.connProxy.testDbView + "   " + c.asInstanceOf[Conn_Peer].peerConn.db)
    //    msg = msg + "\n" + c._adhocDbView
    //    msg = msg + "\n" + c._adhocDbView
    //    msg = msg + "  " + c.asInstanceOf[Conn_Peer].peerConn.db
    //    println(msg)
    //    println("----- " + c._adhocDbView)
    //    println("----- ")
    futConnTimeAdjusted
  }


  // Helpers -------------------------------------------------

  def getJavaStmts(
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): jList[AnyRef] = {
    val stmts = readAll(new StringReader(stmtsEdn)).get(0).asInstanceOf[jList[AnyRef]]
    if (uriAttrs.isEmpty) {
      stmts
    } else {
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

  def qTime(queryTime: Long): String = {
    val indents = 5 - queryTime.toString.length
    " " * indents + thousands(queryTime) + " ms"
  }

  private def unmarshallInputs(lists: Seq[(Int, Any)]): Seq[Object] = {
    lists.sortBy(_._1).map(_._2).map {
      case l: Seq[_] =>
        Util.list(l.collect {
          case l2: Seq[_] =>
            Util.list(l2.collect {
              case (tpe: String, v: String) => castRightOfPair(tpe, v)
            }: _*)

          case (tpe: String, v: String) => castRightOfPair(tpe, v)
        }: _*)

      case (tpe: String, v: String) => castRightOfPair(tpe, v)
      case _                        => throw MoleculeException("Unexpected input values")
    }
  }


  // To avoid type combination explosions from multiple inputs of various types
  // to be transferred with autowire/boopickle, we cast all input variable values
  // as String on the client and then cast them back to their original type here
  // and pass them as Object's to Datomic.
  def castRightOfPair(tpe: String, v: String): Object = {
    (tpe, v) match {
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
      case _                 => throw MoleculeException(s"Unexpected input pair to cast: ($tpe, $v)")
    }
  }
}
