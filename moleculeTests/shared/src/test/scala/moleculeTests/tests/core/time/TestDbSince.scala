package moleculeTests.tests.core.time

import molecule.datomic.api.out1._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import moleculeTests.tests.core.time.domain.Crud
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

  //  // Seems like a bug that we can't apply filter to with-db with peer-server
  //  // respect base setting
  //  tests match {
  //    case 1 =>
  //    case 3 =>
  //    case _ => tests = 13
  //    }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "since: input types" - core { implicit conn =>
      for {
        (txR1, txR2, txR3, e2, e3) <- data
        // Ensure to match dates beeing at least 1 ms apart
        //    Thread.sleep(1)
        txR4 <- Ns.int(4).save
        txR5 <- Ns.int(5).save

        // Live state
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4, 5))

        // since tx report
        _ <- conn.map(_.testDbSince(txR1))
        _ <- Ns.int.get.map(_ ==> List(2, 3, 4, 5))

        // since t
        _ <- conn.map(_.testDbSince(txR2.t))
        _ <- Ns.int.get.map(_ ==> List(3, 4, 5))

        // since tx
        _ <- conn.map(_.testDbSince(txR3.tx))
        _ <- Ns.int.get.map(_ ==> List(4, 5))

        // since date
        _ <- conn.map(_.testDbSince(txR4.inst))
        _ <- Ns.int.get.map(_ ==> List(5))

        // Live state unaffected
        _ <- conn.map(_.useLiveDb)
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4, 5))
      } yield ()
    }


    "since: operations" - core { implicit conn =>
      for {
        (txR1, txR2, txR3, e2, e3) <- data

        // Live state
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))

        // Use state accumulated since tx1 (exclusive) as a test db "branch"
        _ <- conn.map(_.testDbSince(txR1))

        // Test state since tx1 (exclusive)
        _ <- Ns.int.get.map(_.sorted ==> List(2, 3))

        // Test operations:

        // Save
        _ <- Ns.int(4).save
        _ <- Ns.int.get.map(_.sorted ==> List(2, 3, 4))

        // Insert
        _ <- Ns.int insert List(5, 6)
        _ <- Ns.int.get.map(_.sorted ==> List(2, 3, 4, 5, 6))

        // Update
        _ <- Ns(e2).int(0).update
        _ <- Ns.int.get.map(_.sorted ==> List(0, 3, 4, 5, 6))

        // Retract
        _ <- e3.map(_.retract)
        _ <- Ns.int.get.map(_.sorted ==> List(0, 4, 5, 6))

        // Live state unaffected
        _ <- conn.map(_.useLiveDb)
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))
      } yield ()
    }


    "since: testing domain" - core { implicit conn =>
      val crud = Crud
      for {
        (txR1, txR2, txR3, e2, e3) <- data

        // Some domain object

        // Live state
        _ <- crud.read.map(_ ==> List(1, 2, 3))

        // Use state accumulated since tx1 (exclusive) as a test db "branch"
        _ <- conn.map(_.testDbSince(txR1))

        // Test state since tx1 (exclusive)
        _ <- crud.read.map(_ ==> List(2, 3))

        // Update test db through domain process
        _ <- crud.create(4, 5)

        // Test db now has 4 values
        _ <- crud.read.map(_ ==> List(2, 3, 4, 5))

        // Mutate test db through domain object
        _ <- crud.delete(1) // not from test db
        _ <- crud.delete(3, 4) // from test db

        // Updated test state
        _ <- crud.read.map(_ ==> List(2, 5))

        // Discard test db and go back to live db
        _ <- conn.map(_.useLiveDb)

        // Live state unchanged
        _ <- crud.read.map(_ ==> List(1, 2, 3))
      } yield ()
    }
  }
}