package moleculeTests.tests.core.time

import molecule.core.util.JavaUtil
import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object GetAsOf extends AsyncTestSuite with JavaUtil {

  lazy val tests = Tests {

    "t (from history)" - core { implicit conn =>
      for {
        tx1 <- Ns.str.int insert List(
          ("Ben", 42),
          ("Liz", 37),
        )
        List(ben, liz) = tx1.eids
        tx2 <- Ns(ben).int(43).update
        tx3 <- ben.retract

        // See history of Ben
        _ <- Ns(ben).int.tx.op.getHistory.map(_.sortBy(r => (r._2, r._3)) ==> List(
          (42, tx1.tx, true), // Insert:  42 asserted
          (42, tx2.tx, false), // Update:  42 retracted
          (43, tx2.tx, true), //          43 asserted
          (43, tx3.tx, false) // Retract: 43 retracted
        ))

        // Data after insertion
        _ <- Ns.str.int.getAsOf(tx1.t).map(_ ==> List(
          ("Liz", 37),
          ("Ben", 42)
        ))

        // Data after update
        _ <- Ns.str.int.getAsOf(tx2.t).map(_ ==> List(
          ("Liz", 37),
          ("Ben", 43) // Ben now 43
        ))

        // Data after retraction
        _ <- Ns.str.int.getAsOf(tx3.t).map(_ ==> List(
          ("Liz", 37) // Ben gone
        ))
      } yield ()
    }


    "t (from tx)" - core { implicit conn =>
      for {
        // tx1: initial insert
        tx1 <- Ns.int(1).save
        eid = tx1.eid

        // tx2: update
        tx2 <- Ns(eid).int(2).update

        t1 = tx1.t
        t2 = tx2.t

        // Value as of tx1
        _ <- Ns(eid).int.getAsOf(t1).map(_ ==> List(1))

        // Current value
        _ <- Ns(eid).int.get.map(_ ==> List(2))

        // Value as of tx2 (current value)
        _ <- Ns(eid).int.getAsOf(t2).map(_ ==> List(2))
      } yield ()
    }


    "tx entity" - core { implicit conn =>
      for {
        eid <- Ns.int(1).save.map(_.eid)

        // Get tx entity of last transaction involving Ns.int
        tx1 <- Ns.int_.tx.get.map(_.head)

        _ <- Ns(eid).int(2).update

        // Get tx entity of last transaction involving Ns.int
        tx2 <- Ns.int_.tx.get.map(_.head)

        // Value as of tx1
        _ <- Ns(eid).int.getAsOf(tx1).map(_ ==> List(1))

        // Current value
        _ <- Ns(eid).int.get.map(_ ==> List(2))

        // Value as of tx2 (current value)
        _ <- Ns(eid).int.getAsOf(tx2).map(_ ==> List(2))
      } yield ()
    }


    "tx report" - core { implicit conn =>
      for {
        // Insert (tx report 1)
        tx1 <- Ns.str.int insert List(
          ("Ben", 42),
          ("Liz", 37),
        )
        ben = tx1.eid

        // Update (tx report 2)
        tx2 <- Ns(ben).int(43).update

        // Retract (tx report 3)
        tx3 <- ben.retract

        _ <- Ns.str.int.getAsOf(tx1).map(_ ==> List(
          ("Liz", 37),
          ("Ben", 42)
        ))

        _ <- Ns.str.int.getAsOf(tx2).map(_ ==> List(
          ("Liz", 37),
          ("Ben", 43) // Ben now 43
        ))

        _ <- Ns.str.int.getAsOf(tx3).map(_ ==> List(
          ("Liz", 37) // Ben gone
        ))
      } yield ()
    }


    "Date" - core { implicit conn =>
      val beforeInsert = new java.util.Date
      // create delays between transactions to allow dates to be separate by at leas 1 ms
      delay
      for {
        // Insert
        tx1 <- Ns.str.int insert List(("Ben", 42), ("Liz", 37))
        ben = tx1.eid
        afterInsert = tx1.inst

        _ = delay

        // Update
        afterUpdate <- Ns(ben).int(43).update.map(_.inst)
        _ = delay

        // Retract
        afterRetract <- ben.retract.map(_.inst)

        // Let retraction register before querying
        // (Peer is fast, and dates are only precise by the ms)
        //    Thread.sleep(10)

        // No data yet before insert
        _ <- Ns.str.int.getAsOf(beforeInsert).map(_ ==> Nil)

        _ <- Ns.str.int.getAsOf(afterInsert).map(_ ==> List(
          ("Liz", 37),
          ("Ben", 42)
        ))

        _ <- Ns.str.int.getAsOf(afterUpdate).map(_ ==> List(
          ("Liz", 37),
          ("Ben", 43) // Ben now 43
        ))

        _ <- Ns.str.int.getAsOf(afterRetract).map(_ ==> List(
          ("Liz", 37) // Ben gone
        ))
      } yield ()
    }


    "Iterable" - core { implicit conn =>
      for {
        // Call getIterableAsOf for large result sets to maximize runtime performance.
        // Data is lazily type-casted on each call to `next` on the iterator.

        // Insert (tx report 1)
        tx1 <- Ns.str.int insert List(
          ("Ben", 42),
          ("Liz", 37),
        )
        ben = tx1.eid

        // Update (tx report 2)
        tx2 <- Ns(ben).int(43).update

        // Retract (tx report 3)
        tx3 <- ben.retract

        _ <- Ns.str.int.getIterableAsOf(tx1).map { iterable =>
          val it = iterable.iterator
          it.next() ==> ("Liz", 37)
          it.next() ==> ("Ben", 42)
        }

        // Type casting lazily performed with each call to `next`

        _ <- Ns.str.int.getIterableAsOf(tx2).map { iterable =>
          val it = iterable.iterator
          it.next() ==> ("Liz", 37)
          it.next() ==> ("Ben", 43) // Ben now 43
        }

        // Type casting lazily performed with each call to `next`

        _ <- Ns.str.int.getIterableAsOf(tx3).map { iterable =>
          val it = iterable.iterator
          it.next() ==> ("Liz", 37)
          it.hasNext ==> false // Ben gone
        }
      } yield ()
    }


    "Raw" - core { implicit conn =>
      for {
        // Insert (tx report 1)
        tx1 <- Ns.str.int insert List(
          ("Ben", 42),
          ("Liz", 37),
        )
        ben = tx1.eid

        // Update (tx report 2)
        tx2 <- Ns(ben).int(43).update

        // Retract (tx report 3)
        tx3 <- ben.retract

        _ <- Ns.str.int.getRawAsOf(tx1).map(_.strInts ==> List(("Liz", 37), ("Ben", 42)))
        _ <- Ns.str.int.getRawAsOf(tx2).map(_.strInts ==> List(("Liz", 37), ("Ben", 43))) // Ben now 43
        _ <- Ns.str.int.getRawAsOf(tx3).map(_.strInts ==> List(("Liz", 37))) // Ben gone
      } yield ()
    }
  }
}