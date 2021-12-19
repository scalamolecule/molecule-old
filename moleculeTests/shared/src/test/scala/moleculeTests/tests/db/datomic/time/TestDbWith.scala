package moleculeTests.tests.db.datomic.time

import molecule.datomic.api.out1._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.tests.db.datomic.time.domain.Crud
import utest._
import scala.concurrent.{ExecutionContext, Future}


object TestDbWith extends AsyncTestSuite {

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      List(e1, e2, e3) <- Ns.int.insert(1, 2, 3).map(_.eids)
    } yield {
      (e1, e2, e3)
    }
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "with single tx" - core { implicit futConn =>
      for {
        conn <- futConn
        (e1, e2, e3) <- data

        // Current live state
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // Use current state with extra save tx as a test db "branch"
        _ <- conn.testDbWith(Ns.int(4).getSaveStmts)

        // Adjusted test state to work on
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4))

        // Update
        _ <- Ns(e1).int(0).update
        _ <- Ns.int.get.map(_.sorted ==> List(0, 2, 3, 4))

        // Retract
        _ <- e2.retract
        _ <- Ns.int.get.map(_.sorted ==> List(0, 3, 4))

        // Save
        _ <- Ns.int(5).save
        _ <- Ns.int.get.map(_.sorted ==> List(0, 3, 4, 5))

        // Discard test db and go back to live db
        _ = conn.useLiveDb()

        // Current live state is correctly unchanged
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))
      } yield ()
    }


    "with multiple txs" - core { implicit futConn =>
      for {
        conn <- futConn
        (e1, e2, e3) <- data

        // Current live state
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))

        // Apply multiple transaction statements to get a
        // test db in a certain state
        _ <- conn.testDbWith(
          // --> List(1, 2, 3, 4)
          Ns.int(4).getSaveStmts,

          // --> List(1, 2, 3, 4, 5, 6)
          Ns.int getInsertStmts List(5, 6),

          // --> List(0, 2, 3, 4, 5, 6)
          Ns(e1).int(0).getUpdateStmts,

          // --> List(0, 3, 4, 5, 6)
          e2.getRetractStmts
        )

        // Adjusted test state to work on
        _ <- Ns.int.get.map(_.sorted ==> List(0, 3, 4, 5, 6))


        // 3 -> 7
        _ <- Ns(e3).int(7).update

        // Updated test state
        _ <- Ns.int.get.map(_.sorted ==> List(0, 4, 5, 6, 7))

        // add 8
        _ <- Ns.int(8).save
        _ <- Ns.int.get.map(_.sorted ==> List(0, 4, 5, 6, 7, 8))

        // remove entity 1 (value 0)
        _ <- Ns(e1).int().update
        _ <- Ns.int.get.map(_.sorted ==> List(4, 5, 6, 7, 8))

        // retract entity 3 (value 7)
        _ <- e3.retract
        _ <- Ns.int.get.map(_.sorted ==> List(4, 5, 6, 8))

        // Etc...

        // Discard test db and go back to live db
        _ = conn.useLiveDb()

        // Current live state is correctly unchanged
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))
      } yield ()
    }


    "with multiple modularized txs" - core { implicit futConn =>
      for {
        conn <- futConn
        (e1, e2, e3) <- data

        // Current live state
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))

        save = Ns.int(4).getSaveStmts
        insert = Ns.int getInsertStmts List(5, 6)
        update = Ns(e1).int(0).getUpdateStmts
        retract = e2.getRetractStmts

        // Apply a set of saved modularized transactions to get a
        // test db in a certain state.
        // Could be combined/ordered flexibly to bring the test db in various states.
        _ <- conn.testDbWith(
          save,
          insert,
          update,
          retract
        )

        // Adjusted test state to work on
        _ <- Ns.int.get.map(_.sorted ==> List(0, 3, 4, 5, 6))

        // 3 -> 7
        _ <- Ns(e3).int(7).update
        _ <- Ns.int.get.map(_.sorted ==> List(0, 4, 5, 6, 7))

        // add 8
        _ <- Ns.int(8).save
        _ <- Ns.int.get.map(_.sorted ==> List(0, 4, 5, 6, 7, 8))

        // remove entity 1 (value 0)
        _ <- Ns(e1).int().update
        _ <- Ns.int.get.map(_.sorted ==> List(4, 5, 6, 7, 8))

        // retract entity 3 (value 7)
        _ <- e3.retract
        _ <- Ns.int.get.map(_.sorted ==> List(4, 5, 6, 8))

        // Etc...

        // Discard test db and go back to live db
        _ = conn.useLiveDb()

        // Current live state is correctly unchanged
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))
      } yield ()
    }


    "Molecules in domain objects" - {

      "with single tx" - core { implicit futConn =>
        // Some domain object
        val crud = Crud
        for {
          conn <- futConn
          (e1, e2, e3) <- data

          // Current live state
          _ <- crud.read.map(_ ==> List(1, 2, 3))

          // Use state with extra save tx as a test db "branch"
          _ <- conn.testDbWith(Ns.int(4).getSaveStmts)

          // Adjusted test state to work on
          _ <- crud.read.map(_ ==> List(1, 2, 3, 4))

          // Update test db
          _ <- crud.update(1 -> 0)

          // Updated test state
          _ <- crud.read.map(_ ==> List(0, 2, 3, 4))

          // Discard test db and go back to live db
          _ = conn.useLiveDb()

          // Current live state is correctly unchanged
          _ <- crud.read.map(_ ==> List(1, 2, 3))
        } yield ()
      }


      "with multiple txs" - core { implicit futConn =>
        // Some domain object
        val crud = Crud
        for {
          conn <- futConn
          (e1, e2, e3) <- data

          // Current live state
          _ <- crud.read.map(_ ==> List(1, 2, 3))

          // Apply a set of transactions to get a
          // test db in a certain state
          _ <- conn.testDbWith(
            // --> List(1, 2, 3, 4)
            Ns.int(4).getSaveStmts,

            // --> List(1, 2, 3, 4, 5, 6)
            Ns.int getInsertStmts List(5, 6),

            // --> List(0, 2, 3, 4, 5, 6)
            Ns(e1).int(0).getUpdateStmts,

            // --> List(0, 3, 4, 5, 6)
            e2.getRetractStmts
          )

          // Adjusted test state to work on
          _ <- crud.read.map(_ ==> List(0, 3, 4, 5, 6))


          // 3 -> 7
          _ <- crud.update(3 -> 7)

          // Updated test state
          _ <- crud.read.map(_ ==> List(0, 4, 5, 6, 7))

          // add 8
          _ <- crud.create(8)
          _ <- crud.read.map(_ ==> List(0, 4, 5, 6, 7, 8))

          // remove value 0
          _ <- crud.delete(0)
          _ <- crud.read.map(_ ==> List(4, 5, 6, 7, 8))

          // retract entity 3 (value 7)
          // We can mix domain updates with local changes here
          _ <- e3.retract
          _ <- crud.read.map(_ ==> List(4, 5, 6, 8))

          // Test updating non-existing value
          _ <- crud.update(2 -> 42)
            .map(_ ==> "Unexpected success").recover { case e: IllegalArgumentException =>
            e.getMessage ==> "Old number (2) doesn't exist in db."
          }

          // Etc...

          // Discard test db and go back to live db
          _ = conn.useLiveDb()

          // Current live state is correctly unchanged
          _ <- crud.read.map(_ ==> List(1, 2, 3))
        } yield ()
      }
    }
  }
}