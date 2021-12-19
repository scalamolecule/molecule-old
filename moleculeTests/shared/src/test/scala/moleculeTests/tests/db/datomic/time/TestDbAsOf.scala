package moleculeTests.tests.db.datomic.time

import molecule.datomic.api.out2._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.tests.db.datomic.time.domain.Counter
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
      (txR1, txR2, txR3, e1, e2, e3, Counter(e1))
    }
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "as of now" - core { implicit futConn =>
      for {
        conn <- futConn
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        // Live state
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // Use current state as a test db "branch"
        _ <- conn.testDbAsOfNow

        // Test state is currently same as live state
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

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
        _ <- e3.retract
        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 4, 5, 6))

        // Live state unchanged
        _ = conn.useLiveDb()
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
      } yield ()
    }


    "as of: input types" - core { implicit futConn =>
      for {
        conn <- futConn
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        txR4 <- Ns.int(4).save
        _ <- Ns.int(5).save

        // Original state
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4, 5))

        // as of tx report
        _ <- conn.testDbAsOf(txR1)
        _ <- Ns.int.get.map(_ ==> List(1))

        // as of t
        _ <- conn.testDbAsOf(txR2.t)
        _ <- Ns.int.get.map(_ ==> List(1, 2))

        // as of tx
        _ <- conn.testDbAsOf(txR3.tx)
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // as of date
        _ <- conn.testDbAsOf(txR4.txInstant)
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4))

        // Original state unaffected
        _ = conn.useLiveDb()
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4, 5))
      } yield ()
    }


    "as of: operations" - core { implicit futConn =>
      for {
        conn <- futConn
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        // Live state
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // Use state as of tx 2 as a test db "branch"
        _ <- conn.testDbAsOf(txR2)

        // Test state
        _ <- Ns.int.get.map(_ ==> List(1, 2))

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
        _ <- e1.retract
        _ <- Ns.int.get.map(_.sorted ==> List(0, 4, 5, 6))

        // Live state unchanged - and we can continue updating
        _ = conn.useLiveDb()
        _ <- Ns.int(7).save
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3, 7))
      } yield ()
    }


    "domain: as of now" - core { implicit futConn =>
      for {
        conn <- futConn
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        // Live state
        _ <- counter.value.map(_ ==> 1)
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // Use current state as a test db "branch"
        _ <- conn.testDbAsOfNow

        // Test state is same as live state
        // Notice that the test db value propagates to our domain object
        // through the implicit conn parameter.
        _ <- counter.value.map(_ ==> 1)
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // Update test db through domain process
        _ <- counter.incr

        // Updated test state
        _ <- counter.value.map(_ ==> 11)
        _ <- Ns.int.get.map(_ ==> List(2, 3, 11))

        // Discard test db and go back to live db
        _ = conn.useLiveDb()

        // Live state unchanged
        _ <- counter.value.map(_ ==> 1)
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
      } yield ()
    }


    "domain: as of tx" - core { implicit futConn =>
      for {
        conn <- futConn
        (txR1, txR2, txR3, e1, e2, e3, counter) <- data

        // Live state
        _ <- counter.value.map(_ ==> 1)
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // Use state as of tx 2 as a test db "branch"
        _ <- conn.testDbAsOf(txR2)
        _ <- Ns.int.get.map(_ ==> List(1, 2))

        // Test state is now as of tx1!
        // Notice that the test db value propagates to our domain object
        // through the implicit conn parameter.
        _ <- counter.value.map(_ ==> 1)

        // Update test db twice through domain process
        _ <- counter.incr.map(_ ==> 11)
        _ <- counter.incr.map(_ ==> 21)

        // Updated test state
        _ <- counter.value.map(_ ==> 21)
        _ <- Ns.int.get.map(_ ==> List(2, 21))

        // Discard test db and go back to live db
        _ = conn.useLiveDb()

        // Live state unchanged
        _ <- counter.value.map(_ ==> 1)
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
      } yield ()
    }
  }
}