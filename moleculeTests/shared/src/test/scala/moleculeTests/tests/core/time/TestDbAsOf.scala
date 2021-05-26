package moleculeTests.tests.core.time

import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out2._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.time.domain.Counter
import utest._
import scala.concurrent.{ExecutionContext, Future}


object TestDbAsOf extends AsyncTestSuite {

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      txR1 <- Ns.int(1).save
      txR2 <- Ns.int(2).save
      txR3 <- Ns.int(3).save
      e1 = txR1.eid
      e2 = txR2.eid
      e3 = txR3.eid
    } yield {
      (txR1, txR2, txR3, e1, e2, e3, Counter.apply(e1))
    }
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "as of now" - core { implicit conn =>
      for {
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        // Live state
        _ <- Ns.int.get === List(1, 2, 3)

        // Use current state as a test db "branch"
        _ <- conn.map(_.testDbAsOfNow)

        // Test state is currently same as live state
        _ <- Ns.int.get === List(1, 2, 3)

        // Test operations:

        // Save
        _ <- Ns.int(4).save
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3, 4))

        // Insert
        _ <- Ns.int insert List(5, 6)
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3, 4, 5, 6))

        // Update
        _ <- Ns(e2).int(0).update
        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 3, 4, 5, 6))

        // Retract
        _ <- e3.map(_.retract)
        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 4, 5, 6))

        // Live state unchanged
        _ <- conn.map(_.useLiveDb)
        _ <- Ns.int.get === List(1, 2, 3)
      } yield ()
    }


    "as of: input types" - core { implicit conn =>
      for {
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        //    Thread.sleep(10)
        txR4 <- Ns.int(4).save
        //    Thread.sleep(10)
        _ <- Ns.int(5).save

        // Original state
        _ <- Ns.int.get === List(1, 2, 3, 4, 5)

        // as of tx report
        _ <- conn.map(_.testDbAsOf(txR1))
        _ <- Ns.int.get === List(1)

        // as of t
        _ <- conn.map(_.testDbAsOf(txR2.t))
        _ <- Ns.int.get === List(1, 2)

        // as of tx
        _ <- conn.map(_.testDbAsOf(txR3.tx))
        _ <- Ns.int.get === List(1, 2, 3)

        // as of date
        _ <- conn.map(_.testDbAsOf(txR4.inst))
        _ <- Ns.int.get === List(1, 2, 3, 4)

        // Original state unaffected
        _ <- conn.map(_.useLiveDb)
        _ <- Ns.int.get === List(1, 2, 3, 4, 5)
      } yield ()
    }


    "as of: operations" - core { implicit conn =>
      for {
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        // Live state
        _ <- Ns.int.get === List(1, 2, 3)

        // Use state as of tx 2 as a test db "branch"
        _ <- conn.map(_.testDbAsOf(txR2))

        // Test state
        _ <- Ns.int.get === List(1, 2)

        // Test operations:

        // Save
        _ <- Ns.int(4).save
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 4))

        // Insert
        _ <- Ns.int insert List(5, 6)
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 4, 5, 6))

        // Update
        _ <- Ns(e2).int(0).update
        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 4, 5, 6))

        // Retract
        _ <- e1.map(_.retract)
        _ <- Ns.int.get.map(_.sorted ==> List(0, 4, 5, 6))

        // Live state unchanged
        _ <- conn.map(_.useLiveDb)
        _ <- Ns.int.get === List(1, 2, 3)
      } yield ()
    }


    "domain: as of now" - core { implicit conn =>
      for {
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        // Live state
        _ <- counter.value === 1
        _ <- Ns.int.get === List(1, 2, 3)

        // Use current state as a test db "branch"
        _ <- conn.map(_.testDbAsOfNow)

        // Test state is same as live state
        // Notice that the test db value propagates to our domain object
        // through the implicit conn parameter.
        _ <- counter.value === 1
        _ <- Ns.int.get === List(1, 2, 3)

        // Update test db through domain process
        _ <- counter.incr

        // Updated test state
        _ <- counter.value === 11
        _ <- Ns.int.get === List(2, 3, 11)

        // Discard test db and go back to live db
        _ <- conn.map(_.useLiveDb)

        // Live state unchanged
        _ <- counter.value === 1
        _ <- Ns.int.get === List(1, 2, 3)
      } yield ()
    }


    "domain: as of tx" - core { implicit conn =>
      for {
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        // Live state
        _ <- counter.value === 1
        _ <- Ns.int.get === List(1, 2, 3)

        // Use state as of tx 2 as a test db "branch"
        _ <- conn.map(_.testDbAsOf(txR2))
        _ <- Ns.int.get === List(1, 2)

        // Test state is now as of tx1!
        // Notice that the test db value propagates to our domain object
        // through the implicit conn parameter.
        _ <- counter.value === 1

        // Update test db twice through domain process
        _ <- counter.incr === 11
        _ <- counter.incr === 21

        // Updated test state
        _ <- counter.value === 21
        _ <- Ns.int.get === List(2, 21)

        // Discard test db and go back to live db
        _ <- conn.map(_.useLiveDb)

        // Live state unchanged
        _ <- counter.value === 1
        _ <- Ns.int.get === List(1, 2, 3)
      } yield ()
    }
  }
}