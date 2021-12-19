package moleculeTests.jvm.db.datomic.peer

import java.util.Date
import molecule.datomic.api.out1._
import molecule.datomic.peer.facade.Conn_Peer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._

/**
 * @see https://docs.datomic.com/on-prem/javadoc/index.html
 */
object ConnectionOps extends AsyncTestSuite {

  lazy val tests = Tests {

    "requestIndex" - corePeerOnly { implicit futConn =>
      for {
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])

        // Schedule immediate indexing job
        scheduled = conn.requestIndex

        // Indexing job has been successfully scheduled
        _ = scheduled ==> true

      } yield ()
    }

    "syncIndex(t)" - corePeerOnly { implicit futConn =>
      for {
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])

        // Some time t
        t <- Ns.int(1).save.map(_.t)

        // Synchronization flag set on connection
        _ = conn.syncIndex(t)

        // Query against synchronized db guaranteed to have been indexed through time t
        _ <- Ns.int.get.map(_ ==> List(1))

        // Subsequent query is against normal live db
        _ <- Ns.int.get.map(_ ==> List(1))
      } yield ()
    }


    "syncSchema(t)" - corePeerOnly { implicit futConn =>
      for {
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])

        // Some time t
        t <- Ns.int(1).save.map(_.t)

        // Synchronization flag set on connection
        _ = conn.syncSchema(t)

        // Query against synchronized db guaranteed to be aware of all schema changes up to time t
        _ <- Ns.int.get.map(_ ==> List(1))

        // Subsequent query is against normal live db
        _ <- Ns.int.get.map(_ ==> List(1))
      } yield ()
    }


    "syncExcise(t)" - corePeerOnly { implicit futConn =>
      for {
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])

        // Some time t
        t <- Ns.int(1).save.map(_.t)

        // Synchronization flag set on connection
        _ = conn.syncExcise(t)

        // Query against synchronized db guaranteed to be aware of all excisions up to time t
        _ <- Ns.int.get.map(_ ==> List(1))

        // Subsequent query is against normal live db
        _ <- Ns.int.get.map(_ ==> List(1))
      } yield ()
    }


    "gcStorage" - corePeerOnly { implicit futConn =>
      for {
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])

        // Reclaim storage garbage older than now
        now = new Date()
        _ = conn.gcStorage(now)

      } yield ()
    }


    "release" - corePeerOnly { implicit futConn =>
      for {
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])

        // Request the release of resources associated with this connection.
        _ = conn.release()

      } yield ()
    }
  }
}