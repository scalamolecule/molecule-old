package moleculeTests.tests.core.time

import molecule.core.util.JavaUtil
import molecule.datomic.api.out1._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object GetSince extends AsyncTestSuite with JavaUtil {

  lazy val tests = Tests {

    "List" - {

      "Appended" - core { implicit conn =>
        for {
          tx1 <- Ns.int(1).save
          tx2 <- Ns.int(2).save
          tx3 <- Ns.int(3).save

          // Current values
          _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

          // Exclussive tx1 value
          _ <- Ns.int.getSince(tx1).map(_ ==> List(2, 3))
          _ <- Ns.int.getSince(tx2).map(_ ==> List(3))
          _ <- Ns.int.getSince(tx3).map(_ ==> List())
        } yield ()
      }


      "Appended and updated" - core { implicit conn =>
        for {
          tx1 <- Ns.int(1).save
          tx2 <- Ns.int(2).save
          tx3 <- Ns.int(3).save

          e2 = tx2.eid
          tx4 <- Ns(e2).int(4).update

          // Current values
          _ <- Ns.int.get.map(_ ==> List(1, 3, 4))

          _ <- Ns.int.getSince(tx1).map(_ ==> List(3, 4))
          _ <- Ns.int.getSince(tx2).map(_ ==> List(3, 4))
          _ <- Ns.int.getSince(tx3).map(_ ==> List(4))
          _ <- Ns.int.getSince(tx4).map(_ ==> List())
        } yield ()
      }


      "Appended and retracted" - core { implicit conn =>
        for {
          tx1 <- Ns.int(1).save
          tx2 <- Ns.int(2).save
          tx3 <- Ns.int(3).save
          tx4 <- Ns.int(4).save
          tx5 <- tx2.eid.map(_.retract)

          // Current values
          _ <- Ns.int.get.map(_ ==> List(1, 3, 4))

          _ <- Ns.int.getSince(tx1).map(_ ==> List(3, 4))
          _ <- Ns.int.getSince(tx2).map(_ ==> List(3, 4))
          _ <- Ns.int.getSince(tx3).map(_ ==> List(4))
          _ <- Ns.int.getSince(tx4).map(_ ==> List())
        } yield ()
      }
    }


    "Iterable" - {

      "t" - core { implicit conn =>
        for {
          // 3 transaction times `t`
          tx1 <- Ns.str("Ann").save
          tx2 <- Ns.str("Ben").save
          tx3 <- Ns.str("Cay").save

          t1 = tx1.t
          t2 = tx2.t
          t3 = tx3.t

          // Current values as Iterable
          _ <- Ns.str.getIterable.map(_.iterator.toList ==> List("Ann", "Ben", "Cay"))

          // Ben and Cay added since transaction time t1
          _ <- Ns.str.getIterableSince(t1).map(_.iterator.toList ==> List("Ben", "Cay"))

          // Cay added since transaction time t2
          _ <- Ns.str.getIterableSince(t2).map(_.iterator.toList ==> List("Cay"))

          // Nothing added since transaction time t3
          _ <- Ns.str.getIterableSince(t3).map(_.iterator.toList ==> Nil)
        } yield ()
      }


      "tx report" - core { implicit conn =>
        for {
          // Get tx reports for 3 transactions
          tx1 <- Ns.str("Ann").save
          tx2 <- Ns.str("Ben").save
          tx3 <- Ns.str("Cay").save

          // Current values
          _ <- Ns.str.getIterable.map(_.iterator.toList ==> List("Ann", "Ben", "Cay"))

          // Ben and Cay added since tx1
          _ <- Ns.str.getIterableSince(tx1).map(_.iterator.toList ==> List("Ben", "Cay"))

          // Cay added since tx2
          _ <- Ns.str.getIterableSince(tx2).map(_.iterator.toList ==> List("Cay"))

          // Nothing added since tx3
          _ <- Ns.str.getIterableSince(tx3).map(_.iterator.toList ==> Nil)
        } yield ()
      }


      "date" - core { implicit conn =>
        for {
          // Transact 3 times (`inst` retrieves transaction time from tx report)
          date1 <- Ns.str("Ann").save.map(_.inst)
          //      Thread.sleep(5)
          date2 <- Ns.str("Ben").save.map(_.inst)
          //      Thread.sleep(5)
          date3 <- Ns.str("Cay").save.map(_.inst)

          // Current values
          _ <- Ns.str.getIterable.map(_.iterator.toList ==> List("Ann", "Ben", "Cay"))

          // Ben and Cay added since human time 1
          _ <- Ns.str.getIterableSince(date1).map(_.iterator.toList ==> List("Ben", "Cay"))

          // Cay added since human time 2
          _ <- Ns.str.getIterableSince(date2).map(_.iterator.toList ==> List("Cay"))

          // Nothing added since human time 3
          _ <- Ns.str.getIterableSince(date3).map(_.iterator.toList ==> Nil)
        } yield ()
      }
    }


    "Raw" - {

      "t" - core { implicit conn =>
        for {
          // 3 transaction times `t`
          tx1 <- Ns.str("Ann").save
          tx2 <- Ns.str("Ben").save
          tx3 <- Ns.str("Cay").save

          t1 = tx1.t
          t2 = tx2.t
          t3 = tx3.t

          // Current values as Iterable
          _ <- Ns.str.get.map(_ ==> List("Ann", "Ben", "Cay"))

          // Ben and Cay added since transaction time t1
          _ <- Ns.str.getRawSince(t1).map { raw =>
            raw.strs ==> List("Ben", "Cay")
          }

          // Cay added since transaction time t2
          _ <- Ns.str.getRawSince(t2).map(_.strs ==> List("Cay"))

          // Nothing added since transaction time t3
          _ <- Ns.str.getRawSince(t3).map(_.strs ==> Nil)
        } yield ()
      }


      "tx report" - core { implicit conn =>
        for {
          // Get tx reports for 3 transactions
          tx1 <- Ns.str("Ann").save
          tx2 <- Ns.str("Ben").save
          tx3 <- Ns.str("Cay").save

          t1 = tx1.t
          t2 = tx2.t
          t3 = tx3.t

          // Current values
          _ <- Ns.str.get.map(_ ==> List("Ann", "Ben", "Cay"))

          // Ben and Cay added since tx1
          _ <- Ns.str.getRawSince(tx1).map { raw =>
            raw.strs ==> List("Ben", "Cay")
          }

          // Cay added since tx2
          _ <- Ns.str.getRawSince(tx2).map(_.strs ==> List("Cay"))

          // Nothing added since tx3
          _ <- Ns.str.getRawSince(tx3).map(_.strs ==> Nil)
        } yield ()
      }


      "date" - core { implicit conn =>
        for {
          // Transact 3 times (`inst` retrieves transaction time from tx report)
          date1 <- Ns.str("Ann").save.map(_.inst)
          //      Thread.sleep(5)
          date2 <- Ns.str("Ben").save.map(_.inst)
          //      Thread.sleep(5)
          date3 <- Ns.str("Cay").save.map(_.inst)

          // Current values
          _ <- Ns.str.getRaw.map(_.strs ==> List("Ann", "Ben", "Cay"))

          // Ben and Cay added since human time 1
          _ <- Ns.str.getRawSince(date1).map(_.strs ==> List("Ben", "Cay"))

          // Cay added since human time 2
          _ <- Ns.str.getRawSince(date2).map(_.strs ==> List("Cay"))

          // Nothing added since human time 3
          _ <- Ns.str.getRawSince(date3).map(_.strs ==> Nil)
        } yield ()
      }
    }
  }
}