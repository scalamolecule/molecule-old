package moleculeTests.setup

import molecule.core.data.SchemaTransaction
import molecule.core.marshalling.DatomicPeerServerProxy
import molecule.core.util.testing.TxCount._
import molecule.core.util.testing.TxCount.schema._
import molecule.datomic.api.out3._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.client.facade.{Conn_Client, Datomic_PeerServer}
import scala.concurrent.{ExecutionContext, Future}

// Hack to "empty" Peer Server database by retracting previous datoms (except schema transaction datoms).
// Needed since we can't recreate the database without restarting the Peer Server which is not an option
// before each test.

object CleanPeerServer {

  def getCleanPeerServerConn(
    peerServer: Datomic_PeerServer,
    dbIdentifier: String,
    schema: SchemaTransaction,
    proxy: DatomicPeerServerProxy
  )(implicit ec: ExecutionContext): Future[Conn_Client] = Future {
    val futDataConn    = peerServer.connect(dbIdentifier)
    val futTxCountConn = peerServer.connect("m_txCount")
    for {
      dataConn <- futDataConn
      // Using synchronous api since we are within a Future here
      log = dataConn.clientConn.txRange(Some(1000), Some(1002))
      cleanPeerServerConn <- if (log.isEmpty) {
        println(s"Installing fresh Peer Server schema...")
        for {
          // Transact schema
          _ <- dataConn.transact(schema.datomicClient.head)
          _ <- if (schema.datomicClient.size > 1) dataConn.transact(schema.datomicClient(1)) else Future.unit
          _ <- if (schema.datomicClient.size > 2) dataConn.transact(schema.datomicClient(2)) else Future.unit
          newBasistT <- dataConn.db.t.map(_ + 1)

          // Transact tx counter schema
          txCountConn <- futTxCountConn
          _ <- txCountConn.transact(TxCountSchema.datomicClient.head) // no partitions/aliases
          _ <- TxCount.db(dbIdentifier).basisT(newBasistT).save(futTxCountConn, ec)
        } yield {
          dataConn
        }

      } else {

        // Retract non-schema datoms since last basis t in Peer Server database
        (for {
          (eid, lastBasisT) <- TxCount.e.db_(dbIdentifier).basisT.get(futTxCountConn, ec).map(_.head)
          datoms <- Log(Some(lastBasisT)).e.a.v.get(futDataConn, ec)
          cleanupRetractions = {
            datoms.flatMap {
              case (_, ":db/txInstant", _)            => None // Can't change tx time
              case (_, ":TxCount/basisT", _)          => None // Keep basisT
              case (tx, a, v) if tx < 17000000000000L => Some(Retract(tx, a, v))
              case (e, _, _)                          => Some(RetractEntity(e))
            }
          }
        } yield {
          val retractionCount = cleanupRetractions.size
          if (retractionCount > 1000)
            throw new RuntimeException(s"Unexpectedly gathered $retractionCount retractions...")
          //          println(cleanupRetractions.mkString(s"--- $lastBasisT ---\n", "\n", ""))

          if (retractionCount == 0) {
            Future(dataConn)
          } else {
            for {
              newBasisT <- dataConn.transact(Future(cleanupRetractions)).map(_.t + 1)
              _ <- TxCount(eid).basisT(newBasisT).update(futTxCountConn, ec)
            } yield {
              dataConn
            }
          }
        }).flatten
      }
    } yield {
      cleanPeerServerConn.connProxy = proxy
      cleanPeerServerConn
    }
  }.flatten
}
