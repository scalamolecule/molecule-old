package moleculeTests.jvm.db.datomic

import molecule.datomic.api.out1._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._


object DatomicSync extends AsyncTestSuite {

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
  }
}