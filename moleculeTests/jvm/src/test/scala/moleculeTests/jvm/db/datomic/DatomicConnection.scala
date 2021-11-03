package moleculeTests.jvm.db.datomic

import molecule.datomic.api.out1._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object DatomicConnection extends AsyncTestSuite {

  lazy val tests = Tests {

    "sync" - core { implicit futConn =>
      for {
        conn <- futConn

        // Initial data
        _ <- Ns.int(1).save

        // Synchronization flag set on connection
        _ = conn.sync

        // Query against synchronized db
        _ <- Ns.int.get.map(_ ==> List(1))

        // Subsequent query is against normal live db
        _ <- Ns.int.get.map(_ ==> List(1))
      } yield ()
    }


    "sync(t)" - core { implicit futConn =>
      for {
        conn <- futConn

        // Some time t
        t <- Ns.int(1).save.map(_.t)

        // Synchronization flag set on connection
        _ = conn.sync(t)

        // Query against synchronized db having all transactions completed
        // up to and including time t
        _ <- Ns.int.get.map(_ ==> List(1))

        // Subsequent query is against normal live db
        _ <- Ns.int.get.map(_ ==> List(1))
      } yield ()
    }


    "syncIndex(t)" - core { implicit futConn =>
      for {
        conn <- futConn

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


    "syncSchema(t)" - core { implicit futConn =>
      for {
        conn <- futConn

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


    "syncExcise(t)" - core { implicit futConn =>
      for {
        conn <- futConn

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
  }
}