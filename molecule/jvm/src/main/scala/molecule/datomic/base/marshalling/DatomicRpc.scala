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
import molecule.datomic.base.facade.{Conn, TxReportRPC}
import molecule.datomic.client.facade.{Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo.datomicProtocol
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DatomicRpc extends MoleculeRpc
  with DateHandling with DateStrLocal with Helpers with ClojureBridge {


  def transactAsync(
    dbProxy: DbProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[Either[String, TxReportRPC]] = Future {
    try {
      //      println("transact " + dbProxy.uuid + "\n" + stmtsEdn)
      println(stmtsEdn)
      val txReport = getCachedConn(dbProxy).transactRaw(javaStmts(stmtsEdn, uriAttrs))
      Right(
        TxReportRPC(
          txReport.eids, txReport.t, txReport.tx, txReport.inst, txReport.toString
        )
      )
    } catch {
      case t: Throwable =>
        Left("Error from executing transaction in DatomicRpc: " + t.toString)
    }
  }

  def queryAsync(
    dbProxy: DbProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows: Int,
    indexes: List[(Int, Int, Int, Int)]
  ): Future[Either[String, QueryResult]] = Future {
    val log = new log
    try {
      val t           = TimerPrint("DatomicRpc")
      val inputs      = rules +: unmarshallInputs(l ++ ll ++ lll)
      val allRows     = getCachedQueryExecutor(dbProxy)(datalogQuery, inputs)
      val rowCountAll = allRows.size
      val rowCount    = if (maxRows == -1 || rowCountAll < maxRows) rowCountAll else maxRows

      val queryTime = t.delta
      //      log(s"\n---- Querying Datomic... --------------------")
      //      log(datalogQuery)
      //      log(qTime(queryTime) + "  " + datalogQuery)
      log(datalogQuery + "         " + qTime(queryTime))
      //      log("dbProxy uuid: " + dbProxy.uuid)
      //      log("Query time  : " + thousands(queryTime) + " ms")
      //      log("rowCountAll : " + rowCountAll)
      //      log("maxRows     : " + (if (maxRows == -1) "all" else maxRows))
      //      log("rowCount    : " + rowCount)

      if (rowCount == 0) {
        log.print
        Left("Empty result set")
      } else {
        val queryResult = Rows2QueryResult(
          allRows, rowCountAll, rowCount, queryTime, indexes
        ).get

        // log("QueryResult: " + queryResult)
        //        log("Rows2QueryResult took " + t.ms)
        //        log("Sending data to client... Total server time: " + t.msTotal)
        log.print
        Right(queryResult)
      }
    } catch {
      case t: Throwable =>
        Left("Error from executing query in DatomicRpc: " + t.getMessage)
    }
  }

  def getAttrValuesAsync(
    dbProxy: DbProxy,
    datalogQuery: String,
    card: Int,
    tpe: String
  ): Future[List[String]] = Future {
    var vs   = List.empty[String]
    val rows = getCachedQueryExecutor(dbProxy)(datalogQuery, Nil).iterator()
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

  def clearCache: Future[Boolean] = Future {
    connCache.clear()
    queryExecutorCache.clear()
    true
  }

  // Cache Conn and QueryExecutors ---------------------------------------------

  // (datalogQuery, inputs) => raw data
  type QueryExecutor = (String, Seq[AnyRef]) => util.Collection[util.List[AnyRef]]

  // todo: proper caching
  val connCache          = mutable.Map.empty[String, Conn]
  val queryExecutorCache = mutable.Map.empty[String, QueryExecutor]

  private def getCachedConn(dbProxy: DbProxy): Conn = {
    connCache.getOrElse(dbProxy.uuid, {
      val conn = dbProxy match {
        case DatomicInMemProxy(edns, _) =>
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
      //      println("@@@@@ cached conn         : " + dbProxy.uuid + "   " + connCache.size)
      conn
    })
  }

  private def getCachedQueryExecutor(dbProxy: DbProxy): QueryExecutor = {
    queryExecutorCache.getOrElse(dbProxy.uuid, {
      val queryExecutor = dbProxy match {
        case _: DatomicInMemProxy =>
          val conn = getCachedConn(dbProxy)
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            Peer.q(datalogQuery, conn.db.getDatomicDb +: inputs: _*)
          }

        case _: DatomicPeerProxy =>
          val conn = getCachedConn(dbProxy)
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            Peer.q(datalogQuery, conn.db.getDatomicDb +: inputs: _*)
          }

        case _: DatomicDevLocalProxy =>
          val conn = getCachedConn(dbProxy)
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            conn.qRaw(conn.db, datalogQuery, inputs)
          }

        case _: DatomicPeerServerProxy =>
          val conn = getCachedConn(dbProxy)
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            conn.qRaw(conn.db, datalogQuery, inputs)
          }
      }
      queryExecutorCache(dbProxy.uuid) = queryExecutor
      //      println("@@@@@ cached queryExecutor: " + dbProxy.uuid + "   " + queryExecutorCache.size)
      queryExecutor
    })
  }


  // Helpers -------------------------------------------------

  def javaStmts(edn: String, uriAttrs: Set[String]): jList[AnyRef] = {
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
    case ("Float", v)      => v.toDouble.asInstanceOf[Object]
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
