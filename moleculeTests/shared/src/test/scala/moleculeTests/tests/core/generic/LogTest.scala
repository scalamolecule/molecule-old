package moleculeTests.tests.core.generic

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.out5._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object LogTest extends AsyncTestSuite {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      // Generally use `t` or `tx` to identify transaction and `txInstant` only to get
      // the wall clock time since Date's are only precise to ms.

      // First entity
      txR1 <- Ns.str("a").int(1).save
      tx1 = txR1.tx
      e1 = txR1.eid
      t1 = txR1.t
      d1 = txR1.inst

      txR2 <- Ns(e1).str("b").update
      tx2 = txR2.tx
      t2 = txR2.t
      d2 = txR2.inst

      txR3 <- Ns(e1).int(2).update
      tx3 = txR3.tx
      t3 = txR3.t
      d3 = txR3.inst

      // Second entity
      txR4 <- Ns.str("x").int(4).save
      tx4 = txR4.tx
      e2 = txR4.eid
      t4 = txR4.t
      d4 = txR4.inst

      txR5 <- Ns(e2).int(5).update
      tx5 = txR5.tx
      t5 = txR5.t
      d5 = txR5.inst

      // Relationship
      txR6 <- Ref1.str1("hello").save
      tx6 = txR6.tx
      t6 = txR6.t
      d6 = txR6.inst
      e3 = txR6.eid

      // e2 points to e3
      txR7 <- Ns(e2).ref1(e3).update
      tx7 = txR7.tx
      t7 = txR7.t
      d7 = txR7.inst

      // Cardinality-many attributes
      // 6, 7, 8
      txR8 <- Ns.ints(6, 7, 8).save
      t8 = txR8.t
      e4 = txR8.eid

      // 6, 70, 80
      txR9 <- Ns(e4).ints.replace(7 -> 70, 8 -> 80).update
      t9 = txR9.t

      // 70, 80
      txR10 <- Ns(e4).ints.retract(6).update
      t10 = txR10.t

      // 70, 80, 90
      txR11 <- Ns(e4).ints.assert(60).update
      t11 = txR11.t

      // e2 now points to e4
      txR12 <- Ns(e2).ref1(e4).update
      tx12 = txR12.tx
      t12 = txR12.t
      d12 = txR12.inst

      // e1 also points to e4
      txR13 <- Ns(e2).refs1(e4).update
      tx13 = txR13.tx
      t13 = txR13.t
      d13 = txR13.inst

      // Inline descriptions taken from the manual:
      // https://docs.datomic.com/on-prem/indexes.html
    } yield {
      (
        (tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5),
        (tx6, t6, d6, e3, tx7, t7, d7),
        (t8, e4, t9, t10, t11, tx12, t12, d12, tx13, t13, d13)
      )
    }
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Basics" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

        // Apply attribute name and `from` + `until` value range arguments

        // Datoms with attribute :Ns/int having a value between 2 until 6 (not included)
        _ <- Log(Some(tx1), Some(tx2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        _ <- Log(Some(tx1), Some(tx3)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true),

          (t2, tx2, ":db/txInstant", d2, true),
          (t2, e1, ":Ns/str", "b", true),
          (t2, e1, ":Ns/str", "a", false)
        ))


        _ <- Log(Some(tx1), Some(tx4)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true),

          (t2, tx2, ":db/txInstant", d2, true),
          (t2, e1, ":Ns/str", "b", true),
          (t2, e1, ":Ns/str", "a", false),

          (t3, tx3, ":db/txInstant", d3, true),
          (t3, e1, ":Ns/int", 2, true),
          (t3, e1, ":Ns/int", 1, false)
        ))

        _ <- Log(Some(tx2), Some(tx4)).t.e.a.v.op.get.map(_ ==> List(
          (t2, tx2, ":db/txInstant", d2, true),
          (t2, e1, ":Ns/str", "b", true),
          (t2, e1, ":Ns/str", "a", false),

          (t3, tx3, ":db/txInstant", d3, true),
          (t3, e1, ":Ns/int", 2, true),
          (t3, e1, ":Ns/int", 1, false)
        ))
      } yield ()
    }

    "Grouped" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

        // Resembling the original structure of the Datomic Log
        _ <- Log(Some(tx1), Some(tx4)).t.e.a.v.op.get.map(_.groupBy(_._1) ==> Map(
          t1 -> List(
            (t1, tx1, ":db/txInstant", d1, true),
            (t1, e1, ":Ns/str", "a", true),
            (t1, e1, ":Ns/int", 1, true)
          ),
          t2 -> List(
            (t2, tx2, ":db/txInstant", d2, true),
            (t2, e1, ":Ns/str", "b", true),
            (t2, e1, ":Ns/str", "a", false)
          ),
          t3 -> List(
            (t3, tx3, ":db/txInstant", d3, true),
            (t3, e1, ":Ns/int", 2, true),
            (t3, e1, ":Ns/int", 1, false)
          )
        ))

      } yield ()
    }

    "Args" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3), _, _, _) <- testData

        // t - t
        _ <- Log(Some(t1), Some(t2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        // t - tx
        _ <- Log(Some(t1), Some(tx2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        // t - txInstant
        _ <- Log(Some(t1), Some(d2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))


        // tx - t
        _ <- Log(Some(tx1), Some(t2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        // tx - tx
        _ <- Log(Some(tx1), Some(tx2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        // tx - txInstant
        _ <- Log(Some(tx1), Some(d2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))


        // txInstant - t
        _ <- Log(Some(d1), Some(t2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        // txInstant - tx
        _ <- Log(Some(d1), Some(tx2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        // txInstant - tx
        _ <- Log(Some(d1), Some(d2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        // Applying values to Schema attributes not allowed
        _ = compileError("m(Log.e(e1).a.v.t)").check("",
          "molecule.core.transform.exception.Dsl2ModelException: Log attributes not allowed to have values applied.\n" +
            "Log only accepts range arguments: `Log(from, until)`.")


        _ <- Log(Some(tx1), Some("unexpected string")).tx.get.recover { case MoleculeException(err, _) =>
          err ==> "Args to Log can only be t, tx or txInstant of type Int/Long/Date. " +
            s"Found: List($tx1, unexpected string)"
        }

        _ <- Log(Some(t1), Some("unexpected string")).t.get.recover { case MoleculeException(err, _) =>
          err ==> "Args to Log can only be t, tx or txInstant of type Int/Long/Date. " +
            s"Found: List($t1, unexpected string)"
        }
      } yield ()
    }

    "Start/End" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5),
        (tx6, t6, d6, e3, tx7, t7, d7),
        (t8, e4, t9, t10, t11, tx12, t12, d12, tx13, t13, d13)) <- testData

        // tx12 (inclusive) - end
        _ <- Log(Some(t12), None).t.e.a.v.op.get.map(_ ==> List(
          (t12, tx12, ":db/txInstant", d12, true),
          (t12, e2, ":Ns/ref1", e4, true),
          (t12, e2, ":Ns/ref1", e3, false),

          (t13, tx13, ":db/txInstant", d13, true),
          (t13, e2, ":Ns/refs1", e4, true)
        ))

        // Single parameter is `from`
        from <- Log(Some(tx12), None).t.get
        _ <- Log(Some(tx12)).t.get.map(_ ==> from)

        _ <- if (system == SystemPeer) {
          for {
            // Start - t3 (exclusive)
            // Includes all Datomic database bootstrapping and schema transactions
            _ <- Log(None, Some(tx3)).t.get.map(_.size ==> 396)

            // Start - end !! The whole database!
            _ <- Log(None, None).t.get.map(_.size ==> 427)
            // Same as this shortcut
            res <- Log().t.get.map(_.size ==> 427)
          } yield res
        } else if (system == SystemDevLocal) {
          for {
            // Start - t3 (exclusive)
            // Includes all Datomic database bootstrapping and schema transactions
            _ <- Log(None, Some(tx3)).t.get.map(_.size ==> 552)

            // Start - end !! The whole database!
            _ <- Log(None, None).t.get.map(_.size ==> 583)
            // Same as this shortcut
            res <- Log().t.get.map(_.size ==> 583)
          } yield res
        } else Future.unit
      } yield ()
    }

    "Queries" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5),
        (tx6, t6, d6, e3, tx7, t7, d7),
        (t8, e4, t9, t10, t11, tx12, t12, d12, tx13, t13, d13)) <- testData

        // Number of transactions between tx1 and tx12
        _ <- Log(Some(tx1), Some(tx12)).t.get.map(_.distinct.size ==> 11)


        // Entities involved in tx1-tx2
        _ <- Log(Some(tx1), Some(tx3)).e.get.map(_.distinct.sorted ==> List(tx1, tx2, e1).sorted)

        // Entities involved in tx1-tx4
        _ <- Log(Some(tx1), Some(tx5)).e.get.map(_.distinct.sorted ==> List(tx1, tx2, tx3, tx4, e1, e2).sorted)


        // Attributes involved in transactions tx1-tx2
        _ <- Log(Some(tx1), Some(tx3)).a.get.map(_.distinct.sorted ==> List(":Ns/int", ":Ns/str", ":db/txInstant"))

        // Attributes involved in transactions tx1-tx6
        _ <- Log(Some(tx1), Some(tx7)).a.get.map(_.distinct.sorted ==> List(":Ns/int", ":Ns/str", ":Ref1/str1", ":db/txInstant"))


        // Values asserted in transactions tx1-tx2
        _ <- Log(Some(tx1), Some(tx3)).v.get.map(_.distinct ==> List(d1, "a", 1, d2, "b"))

        // Values asserted in transactions tx1-tx3
        _ <- Log(Some(tx1), Some(tx4)).v.get.map(_.distinct ==> List(d1, "a", 1, d2, "b", d3, 2))


        // Assertions and retractions in transactions tx1-tx2
        _ <- Log(Some(tx1), Some(tx3)).op.get.map(_ ==> List(true, true, true, true, true, false))

        // Number of assertions and retractions in transactions tx1-tx2
        _ <- Log(Some(tx1), Some(tx3)).op.get.map(_.groupBy {
          case true  => "assertions"
          case false => "retractions"
        }.toList.sortBy(_._1).map { case (k, vs) => k -> vs.length } ==> List("assertions" -> 5, "retractions" -> 1))

        // Number of assertions and retractions in transactions tx1-tx13
        _ <- Log(Some(tx1), Some(tx13)).op.get.map(_.groupBy {
          case true  => "assertions"
          case false => "retractions"
        }.toList.sortBy(_._1).map { case (k, vs) => k -> vs.length } ==> List("assertions" -> 28, "retractions" -> 7))


        // Transactions from wall clock time d1-d2
        _ <- Log(Some(d1), Some(d3)).tx.get.map(_.distinct.sorted ==> List(tx1, tx2))

        // Same as quering the history for all transactions within the time range
        _ <- Ns.tx.txInstant_.>=(d1).txInstant_.<(d3).getHistory.map(_.sorted ==> List(tx1, tx2))
      } yield ()
    }

    "History" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3), _,
        (tx6, t6, d6, e3, tx7, t7, d7), _) <- testData

        // Since we get the Log from the Connection and not the Database,
        // any time filter getter will make no difference

        _ <- Log(Some(tx1), Some(tx2)).t.e.a.v.op.get.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        _ <- Log(Some(tx1), Some(tx2)).t.e.a.v.op.getHistory.map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))

        _ <- Log(Some(tx1), Some(tx2)).t.e.a.v.op.getAsOf(t7).map(_ ==> List(
          (t1, tx1, ":db/txInstant", d1, true),
          (t1, e1, ":Ns/str", "a", true),
          (t1, e1, ":Ns/int", 1, true)
        ))
      } yield ()
    }
  }
}