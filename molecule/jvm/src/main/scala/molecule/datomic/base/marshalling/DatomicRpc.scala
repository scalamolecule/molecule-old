package molecule.datomic.base.marshalling

import java.util
import boopickle.Default._
import cats.implicits._
import datomic.{Peer, Util}
import molecule.core.marshalling._
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{DateHandling, Helpers}
import molecule.datomic.client.facade.{Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo.datomicProtocol
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.collection.mutable

object DatomicRpc extends MoleculeRpc with DateHandling with Helpers {

  println("############## DatomnicRpc ###############")

  // (datalogQuery, inputs) => raw data
  type QueryExecutor = (String, Seq[AnyRef]) => util.Collection[util.List[AnyRef]]

  // todo: better caching strategy?
  val queryExecutorCache = mutable.Map.empty[String, QueryExecutor]

  private def cahedOrNew(dbKey: String)(cacheDb: => QueryExecutor): QueryExecutor = {
    queryExecutorCache.get(dbKey) match {
      case Some(queryExecutor) =>
        println("Found cached " + dbKey)
        queryExecutor
      case _                   =>
        val t = TimerPrint("getCachedQueryExecutor")
        println("Connecting to " + dbKey)
        val queryExecutor = cacheDb
        println("Connection time: " + thousands(t.delta) + " ms")
        queryExecutorCache(dbKey) = queryExecutor
        queryExecutor
    }
  }

  private def getCachedQueryExecutor(dbProxy: DbProxy): Future[QueryExecutor] = Future(
    dbProxy match {
      case DatomicInMemProxy(edns) =>
        cahedOrNew("DatomicInMemProxy") {
          val db = Datomic_Peer.recreateDbFromEdn(edns).db.getDatomicDb
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            Peer.q(datalogQuery, db +: inputs: _*)
          }
        }

      case DatomicPeerProxy(protocol, dbIdentifier, edns) =>
        cahedOrNew(s"DatomicPeerProxy $protocol $dbIdentifier") {
          if (datomicProtocol != protocol) {
            throw new RuntimeException(
              s"\nProject is built with datomic `$datomicProtocol` protocol and " +
                s"cannot serve supplied `$protocol` protocol. " +
                s"\nPlease change the build setup or your ConnProxy protocol"
            )
          }
          val db = protocol match {
            case "mem" => Datomic_Peer.recreateDbFromEdn(edns).db.getDatomicDb
            case prot  => Datomic_Peer.connect(prot, dbIdentifier).db.getDatomicDb
          }
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            Peer.q(datalogQuery, db +: inputs: _*)
          }
        }

      case DatomicDevLocalProxy(system, storageDir, dbName, schema)    =>
        cahedOrNew(s"DatomicDevLocalProxy $system $storageDir $dbName") {
          val conn = Datomic_DevLocal(system, storageDir).connect(dbName)
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            conn.qRaw(conn.db, datalogQuery, inputs)
          }
        }
      case DatomicPeerServerProxy(accessKey, secret, endpoint, dbName) =>
        cahedOrNew(s"DatomicPeerServerProxy $accessKey <secret> $endpoint $dbName") {
          val conn = Datomic_PeerServer(accessKey, secret, endpoint).connect(dbName)
          (datalogQuery: String, inputs: Seq[AnyRef]) => {
            conn.qRaw(conn.db, datalogQuery, inputs)
          }
        }
    }
  )

  def queryAsync(
    dbProxy: DbProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows: Int,
    indexes: List[(Int, Int, Int, Int)]
  ): Future[Either[String, QueryResult]] = {
    getCachedQueryExecutor(dbProxy).map { queryExecutor =>
      try {
        println(s"\n---- Querying Datomic... --------------------\n" + datalogQuery)
        val t           = TimerPrint("DatomicPeerQueryExecutor")
        val inputs      = rules +: extractInputs(l ++ ll ++ lll)
        val allRows     = queryExecutor(datalogQuery, inputs)
        val queryTime   = t.delta
        val rowCountAll = allRows.size
        val rowCount    = if (maxRows == -1 || rowCountAll < maxRows) rowCountAll else maxRows

        println("Query time : " + thousands(queryTime) + " ms")
        println("rowCountAll: " + rowCountAll)
        println("maxRows    : " + (if (maxRows == -1) "all" else maxRows))
        println("rowCount   : " + rowCount)

        // todo remove - don't loop over all data for this!
        //        allRows.asScala.take(10).foreach(row => log.info(row.toString))

        if (rowCount == 0)
          Left("Empty result set")
        else {
          val queryResult = Rows2QueryResult(
            allRows, rowCountAll, rowCount, queryTime, indexes
          ).get

          //          println("QueryResult: " + queryResult)
          println("Rows2QueryResult took " + t.ms)
          println("Sending data to client... Total server time: " + t.msTotal)
          Right(queryResult)
        }
      } catch {
        case t: Throwable =>
          Left("Error from executing query in DatomicRpc: " + t.getMessage)
      }
    }
  }


  def extractInputs(lists: Seq[(Int, AnyRef)]): Seq[Object] = {
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
