package molecule.datomic.base.marshalling

import java.io.StringReader
import java.util
import java.util.concurrent.atomic.AtomicReference
import java.util.{Collections, Date, List => jList, Set => jSet}
import boopickle.Default._
import cats.implicits._
import datomic.{Database, Util}
import datomic.Util._
import datomicScala.client.api.sync.Db
import datomicClient.ClojureBridge
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling._
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{DateHandling, Helpers}
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.facade._
import molecule.datomic.client.facade.{Conn_Client, Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.{Conn_Peer, DatomicDb_Peer, Datomic_Peer, TxReport_Peer}
import moleculeBuildInfo.BuildInfo.datomicProtocol
import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.control.NonFatal

object DatomicRpc extends MoleculeRpc
  with DateHandling with DateStrLocal with Helpers with ClojureBridge {

  // Necessary for `readString` to encode uri in transactions
  require("clojure.core.async")


  // Api ---------------------------------------------

  def transact(
    dbProxy: DbProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReportRPC] = {
    for {
      conn <- getConn(dbProxy)
//      _ = println(stmtsEdn + "  " + conn.db.getDatomicDb)
      _ = println(stmtsEdn)
      txReport <- conn.transactRaw(getJavaStmts(stmtsEdn, uriAttrs))
    } yield {
//      println(stmtsEdn + "  " + conn.db.getDatomicDb)
//      connCache.set(connCache.get() + (dbProxy.uuid -> Future(conn)))

      TxReportRPC(
        txReport.eids, txReport.t, txReport.tx, txReport.inst, txReport.toString
      )
    }
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
  ): Future[QueryResult] = try {
    val log       = new log
    val t         = TimerPrint("DatomicRpc")
    val inputs    = unmarshallInputs(l ++ ll ++ lll)
    val allInputs = if (rules.nonEmpty) rules +: inputs else inputs
//    println("B   " + dbProxy.adhocDbView + "  " + dbProxy.testDbView)
    for {
      conn <- getConn(dbProxy)

//      _ = println("B   " + dbProxy.adhocDbView + "  " + dbProxy.testDbView)
//      _ = println("B   " + conn.dbProxy.adhocDbView + "  " + conn.dbProxy.testDbView)

      allRows <- conn.qRaw(conn.db, datalogQuery, allInputs)
    } yield {
      val rowCountAll = allRows.size
      val rowCount    = if (maxRows == -1 || rowCountAll < maxRows) rowCountAll else maxRows

      val queryTime = t.delta

      val space     = " " * (70 - datalogQuery.split('\n').last.length)
      val time      = qTime(queryTime)
      val timeRight = " " * (8 - time.length) + time
//      log(datalogQuery + space + timeRight)
      log(datalogQuery + space + timeRight + "  " + conn.db.getDatomicDb)
      log.print
      //      log(s"\n---- Querying Datomic... --------------------")
      //      log(datalogQuery)
      //      log(qTime(queryTime) + "  " + datalogQuery)
      //      log("dbProxy uuid: " + dbProxy.uuid)
      //      log("Query time  : " + thousands(queryTime) + " ms")
      //      log("rowCountAll : " + rowCountAll)
      //      log("maxRows     : " + (if (maxRows == -1) "all" else maxRows))
      //      log("rowCount    : " + rowCount)

      //      val allRows2 = allRows
      //      val it       = allRows2.iterator()
      //      //            it.next()
      //      val v        = it.next().get(0)
      //      println(s"v1: $v  ${v.getClass}")

      val queryResult = Rows2QueryResult(
        allRows, rowCountAll, rowCount, queryTime, indexes
      ).get
      // log("QueryResult: " + queryResult)
      //        log("Rows2QueryResult took " + t.ms)
      //        log("Sending data to client... Total server time: " + t.msTotal)
      queryResult
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }


  def getAttrValues(
    dbProxy: DbProxy,
    datalogQuery: String,
    card: Int,
    tpe: String
  ): Future[List[String]] = {
    var vs = List.empty[String]
    for {
      conn <- getConn(dbProxy)
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
    dbProxy: DbProxy,
    eid: Long
  ): Future[List[String]] = {
    val datalogQuery = s"[:find ?a1 :where [$eid ?a _][?a :db/ident ?a1]]"
    var list         = List.empty[String]
    for {
      conn <- getConn(dbProxy)
      rows <- conn.qRaw(conn.db, datalogQuery, Nil)
    } yield {
      rows.forEach { row =>
        list = row.get(0).toString :: list
      }
      list.sorted
    }
  }

  def retract(
    dbProxy: DbProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReport] = {
    println(stmtsEdn)
    for {
      conn <- getConn(dbProxy)
      txReport <- conn.transactRaw(getJavaStmts(stmtsEdn, uriAttrs))
    } yield TxReportRPC(
      txReport.eids, txReport.t, txReport.tx, txReport.inst, txReport.toString
    )
  }


  // Cache Conn and QueryExecutors ---------------------------------------------

  // (datalogQuery, inputs) => raw data
  private type QueryExecutor = (String, Seq[AnyRef]) => Future[util.Collection[util.List[AnyRef]]]

  private val connCache = new AtomicReference(Map.empty[String, Future[Conn]])

  private def getConn(
    dbProxy: DbProxy
  ): Future[Conn] = {
    var msg     = s"--- " + dbProxy.adhocDbView + "  " + dbProxy.testDbView
    val futConn = connCache.get.getOrElse(
      dbProxy.uuid,
      {
        msg = s"============= Conn CACHING ============= "
        dbProxy match {
          case DatomicInMemProxy(schemaPeer, _, _, _, _, _) =>
            println("==============================================")
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
      conn.updateAdhocDbView(dbProxy.adhocDbView)
      conn.updateTestDbView(dbProxy.testDbView, dbProxy.testDbStatus)
      conn
    }
    connCache.set(connCache.get() + (dbProxy.uuid -> futConnTimeAdjusted))

//    println(msg)
    val c = Await.result(futConnTimeAdjusted, 10.seconds)
    println("----- " + c.asInstanceOf[Conn_Peer].peerConn.db)
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

        case _ => throw MoleculeException("Unexpected input values")
      }: _*)

      case pair@(_: String, _: String) =>
        cast(pair)

      case _ =>
        throw MoleculeException("Unexpected input values")
    }
  }


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
    case _                 =>
      throw MoleculeException("Unexpected input pair to cast: " + pair)
  }
}
