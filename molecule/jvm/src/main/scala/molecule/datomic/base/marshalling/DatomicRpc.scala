package molecule.datomic.base.marshalling

import java.util
import boopickle.Default._
import cats.implicits._
import datomic.{Peer, Util}
import molecule.core.marshalling._
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{DateHandling, Helpers}
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.client.facade.{Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo.datomicProtocol
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DatomicRpc extends MoleculeRpc with DateHandling with Helpers {

  println("############## DatomicRpc ###############")

  def transactAsync(
    dbProxy: DbProxy,
    stmtsEdn: String
  ): Future[Either[String, TxReport]] = Future {
    try {
      println("€€€")
      println(getCachedConn(dbProxy).getClass)

      Right(getCachedConn(dbProxy).transact(stmtsEdn))
    } catch {
      case t: Throwable =>
        Left("Error from executing transaction in DatomicRpc: " + t.getMessage)
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
      log(s"\n---- Querying Datomic... --------------------\n" + datalogQuery)
      log("Query time : " + thousands(queryTime) + " ms")
      log("rowCountAll: " + rowCountAll)
      log("maxRows    : " + (if (maxRows == -1) "all" else maxRows))
      log("rowCount   : " + rowCount)

      if (rowCount == 0) {
        log.print
        Left("Empty result set")
      } else {
        val queryResult = Rows2QueryResult(
          allRows, rowCountAll, rowCount, queryTime, indexes
        ).get

        //          println("QueryResult: " + queryResult)
        log("Rows2QueryResult took " + t.ms)
        log("Sending data to client... Total server time: " + t.msTotal)
        log.print
        Right(queryResult)
      }
    } catch {
      case t: Throwable =>
        Left("Error from executing query in DatomicRpc: " + t.getMessage)
    }
  }


  // Helpers ----------------------------------------------------------------

  // (datalogQuery, inputs) => raw data
  type QueryExecutor = (String, Seq[AnyRef]) => util.Collection[util.List[AnyRef]]

  // todo: better caching strategy?
  val queryExecutorCache = mutable.Map.empty[String, QueryExecutor]
  val connCache          = mutable.Map.empty[String, Conn]

  private def cacheConn(dbKey: String)
                       (makeConn: => Conn): Conn = {
    connCache.get(dbKey) match {
      case Some(conn) => conn
      case _          =>
        val t    = TimerPrint("cacheConn")
        val conn = makeConn
        println(s"---- Connected to $dbKey in " + thousands(t.delta) + " ms")
        connCache(dbKey) = conn
        conn
    }
  }

  private def getCachedConn(dbProxy: DbProxy): Conn = dbProxy match {
    case DatomicInMemProxy(edns, dbIdentifier) =>
      cacheConn("DatomicInMemProxy" + dbIdentifier) {
        //          Datomic_Peer.connect("mem", dbIdentifier)
        Datomic_Peer.recreateDbFromEdn(edns)
      }

    case DatomicPeerProxy(protocol, dbIdentifier, edns) =>
      cacheConn(s"DatomicPeerProxy $protocol $dbIdentifier") {
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
      }

    case DatomicDevLocalProxy(system, storageDir, dbName, schema)    =>
      cacheConn(s"DatomicDevLocalProxy $system $storageDir $dbName") {
        Datomic_DevLocal(system, storageDir).connect(dbName)
      }
    case DatomicPeerServerProxy(accessKey, secret, endpoint, dbName) =>
      cacheConn(s"DatomicPeerServerProxy $accessKey <secret> $endpoint $dbName") {
        Datomic_PeerServer(accessKey, secret, endpoint).connect(dbName)
      }
  }


  private def cacheQueryExecutor(dbKey: String)
                                (makeQueryExecutor: => QueryExecutor): QueryExecutor = {
    queryExecutorCache.get(dbKey) match {
      case Some(queryExecutor) => queryExecutor
      case _                   =>
        val t             = TimerPrint("cacheQueryExecutor")
        val queryExecutor = makeQueryExecutor
        println(s"---- Connected to $dbKey in " + thousands(t.delta) + " ms")
        queryExecutorCache(dbKey) = queryExecutor
        queryExecutor
    }
  }

  private def getCachedQueryExecutor(dbProxy: DbProxy): QueryExecutor = dbProxy match {
    case DatomicInMemProxy(edns, dbIdentifier) =>
      cacheQueryExecutor("DatomicInMemProxy") {
        val db = getCachedConn(dbProxy).db.getDatomicDb
        (datalogQuery: String, inputs: Seq[AnyRef]) => {
          Peer.q(datalogQuery, db +: inputs: _*)
        }
      }

    case DatomicPeerProxy(protocol, dbIdentifier, edns) =>
      cacheQueryExecutor(s"DatomicPeerProxy $protocol $dbIdentifier") {
        val db = getCachedConn(dbProxy).db.getDatomicDb
        (datalogQuery: String, inputs: Seq[AnyRef]) => {
          Peer.q(datalogQuery, db +: inputs: _*)
        }
      }

    case DatomicDevLocalProxy(system, storageDir, dbName, schema)    =>
      cacheQueryExecutor(s"DatomicDevLocalProxy $system $storageDir $dbName") {
        val conn = getCachedConn(dbProxy)
        (datalogQuery: String, inputs: Seq[AnyRef]) => {
          conn.qRaw(conn.db, datalogQuery, inputs)
        }
      }
    case DatomicPeerServerProxy(accessKey, secret, endpoint, dbName) =>
      cacheQueryExecutor(s"DatomicPeerServerProxy $accessKey <secret> $endpoint $dbName") {
        val conn = getCachedConn(dbProxy)
        (datalogQuery: String, inputs: Seq[AnyRef]) => {
          conn.qRaw(conn.db, datalogQuery, inputs)
        }
      }
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
        cast(pair.asInstanceOf[(String, String)])

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
  private def cast(pair: (String, String)): Object = pair match {
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
