package moleculeTests.tests.db.datomic.time

import molecule.datomic.api.out1._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.tests.db.datomic.time.domain.Crud
import utest._
import scala.concurrent.{ExecutionContext, Future}


object TestDbSince extends AsyncTestSuite {

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      txR1 <- Ns.int(1).save
      txR2 <- Ns.int(2).save
      txR3 <- Ns.int(3).save
      e2 = txR2.eid
      e3 = txR3.eid
    } yield {
      (txR1, txR2, txR3, e2, e3)
    }
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "since: input types" - core { implicit futConn =>
      for {
        conn <- futConn
        (txR1, txR2, txR3, e2, e3) <- data
        // Ensure to match dates beeing at least 1 ms apart
        //    Thread.sleep(1)
        txR4 <- Ns.int(4).save
        txR5 <- Ns.int(5).save

        // Live state
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4, 5))

        // since tx report
        _ <- conn.testDbSince(txR1)
        _ <- Ns.int.get.map(_ ==> List(2, 3, 4, 5))

        // since t
        _ <- conn.testDbSince(txR2.t)
        _ <- Ns.int.get.map(_ ==> List(3, 4, 5))

        // since tx
        _ <- conn.testDbSince(txR3.tx)
        _ <- Ns.int.get.map(_ ==> List(4, 5))

        // since date
        _ <- conn.testDbSince(txR4.txInstant)
        _ <- Ns.int.get.map(_ ==> List(5))

        // Live state unaffected
        _ = conn.useLiveDb()
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4, 5))
      } yield ()
    }


    "since: operations" - core { implicit futConn =>
      for {
        conn <- futConn
        (txR1, txR2, txR3, e2, e3) <- data

        // Live state
        _ <- Ns.int.a1.get.map(_ ==> List(1, 2, 3))

        // Use state accumulated since tx1 (exclusive) as a test db "branch"
        _ <- conn.testDbSince(txR1)

        // Test state since tx1 (exclusive)
        _ <- Ns.int.a1.get.map(_ ==> List(2, 3))

        // Test operations:

        // Save
        _ <- Ns.int(4).save
        _ <- Ns.int.a1.get.map(_ ==> List(2, 3, 4))

        // Insert
        _ <- Ns.int insert List(5, 6)
        _ <- Ns.int.a1.get.map(_ ==> List(2, 3, 4, 5, 6))

        // Update
        _ <- Ns(e2).int(0).update
        _ <- Ns.int.a1.get.map(_ ==> List(0, 3, 4, 5, 6))

        // Retract
        _ <- e3.retract
        _ <- Ns.int.a1.get.map(_ ==> List(0, 4, 5, 6))

        // Live state unaffected
        _ = conn.useLiveDb()
        _ <- Ns.int.a1.get.map(_ ==> List(1, 2, 3))
      } yield ()
    }


    "since: testing domain" - core { implicit futConn =>
      // Some domain object
      val crud = Crud
      for {
        conn <- futConn
        (txR1, txR2, txR3, e2, e3) <- data

        // Live state
        _ <- crud.read.map(_ ==> List(1, 2, 3))

        // Use state accumulated since tx1 (exclusive) as a test db "branch"
        _ <- conn.testDbSince(txR1)

        // Test state since tx1 (exclusive)
        _ <- crud.read.map(_ ==> List(2, 3))

        // Add data through domain
        _ <- crud.create(4, 5)

        // Test db now has 4 values
        _ <- crud.read.map(_ ==> List(2, 3, 4, 5))

        // Update data through domain
        _ <- crud.update(5 -> 6)

        // Test values have updated
        _ <- crud.read.map(_ ==> List(2, 3, 4, 6))

        // Retract data through domain
        _ <- crud.delete(1) // not from test db
        _ <- crud.delete(3, 4) // from test db

        // Updated test state
        _ <- crud.read.map(_ ==> List(2, 6))

        // Discard test db and go back to live db
        _ = conn.useLiveDb()

        // Live state unchanged
        _ <- crud.read.map(_ ==> List(1, 2, 3))
      } yield ()
    }
  }
}