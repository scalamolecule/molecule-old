package moleculeTests.tests.core.time

import molecule.datomic.api.in1_out6._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object GetHistory extends AsyncTestSuite {

  //  // Since peer-server accumulates data across tests, we don't test history
  //  // here, although it works fine on a fresh/stable db
  //  tests match {
  //    case 1 =>
  //    case 3 =>
  //    case _ => tests = 13
  //  }

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      // First entity - 3 transactions
      tx1 <- Ns.str("a").int(1).save
      e1 = tx1.eid
      t1 = tx1.t

      // Adding time separations to ensure that transactions are not within the
      // same millisecond. Only necessary if using Dates. For precision, use t or tx.
      tx2 <- Ns(e1).str("b").update
      t2 = tx2.t

      tx3 <- Ns(e1).int(2).update
      t3 = tx3.t

      // Second entity - 2 transactions

      tx4 <- Ns.str("x").int(4).save
      e2 = tx4.eid
      t4 = tx4.t

      tx5 <- Ns(e2).int(5).update
      t5 = tx5.t
    } yield {
      (tx1, e1, t1, tx2, t2, tx3, t3, tx4, e2, t4, tx5, t5)
    }
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "1 entity, 1 attr" - core { implicit conn =>
      val genericInputMolecule = m(Ns(?).str.t.op)
      for {
        (tx1, e1, t1, tx2, t2, tx3, t3, tx4, e2, t4, tx5, t5) <- data

        // Current values are always the last asserted value
        _ <- Ns(e1).int.op_(true).get.map(_ ==> List(2))
        _ <- Ns(e1).int.op_(false).get.map(_ ==> List())

        // str updated at t2
        _ <- Ns(e1).str.t.op.getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          ("a", t1, true), // "a" asserted
          ("a", t2, false), // "a" retracted
          ("b", t2, true) // "b" asserted
        ))

        _ <- genericInputMolecule(e1).getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          ("a", t1, true), // "a" asserted
          ("a", t2, false), // "a" retracted
          ("b", t2, true) // "b" asserted
        ))

        // int updated at t3
        _ <- Ns(e1).int.t.op.getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          (1, t1, true), // 1 asserted
          (1, t3, false), // 1 retracted
          (2, t3, true) // 2 asserted
        ))

        // int history with entity
        _ <- Ns.e.int.t.op.getHistory.map(_.sortBy(t => (t._2, t._3, t._4)) ==> List(
          // e1
          (e1, 1, t1, true),
          (e1, 1, t3, false),
          (e1, 2, t3, true),
          //e2
          (e2, 4, t4, true),
          (e2, 4, t5, false),
          (e2, 5, t5, true)
        ))

        // int values over time
        _ <- Ns(e1).int.getHistory.map(_ ==> List(1, 2))

        // Asserted int values of entity e1 over time
        _ <- Ns(e1).int.op_(true).getHistory.map(_ ==> List(1, 2))

        // Retracted int values of entity e1 over time
        _ <- Ns(e1).int.op_(false).getHistory.map(_ ==> List(1))
      } yield ()
    }


    "Multiple domain attrs" - core { implicit conn =>
      for {
        (tx1, e1, t1, tx2, t2, tx3, t3, tx4, e2, t4, tx5, t5) <- data

        // Mixing the "timeline" of two user-defined "domain" attributes gives
        // us some redundant repetition from unified attribute values.
        // To illustrate, let's revisit the str datoms:

        // str updated at t2
        _ <- Ns(e1).str.t.op.getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          ("a", t1, true), // "a" asserted
          ("a", t2, false), // "a" retracted
          ("b", t2, true) // "b" asserted
        ))

        // Adding the int attribute will cause its historic values 1 and 2 to repeatedly
        // be unified with each str value from above so that we get 3 x 2 datoms:
        _ <- Ns(e1).str.t.op.int.getHistory.map(_.sortBy(t => (t._2, t._3, t._4)) ==> List(
          ("a", t1, true, 1),
          ("a", t1, true, 2),

          ("a", t2, false, 1),
          ("a", t2, false, 2),

          ("b", t2, true, 1),
          ("b", t2, true, 2)
        ))

        // Without a given entity, this approach quickly explodes and becomes useless:
        _ <- Ns.str.t.op.int.getHistory.map(_.sortBy(t => (t._2, t._3, t._4)) ==> List(
          ("a", t1, true, 1),
          ("a", t1, true, 2),

          ("a", t2, false, 1),
          ("a", t2, false, 2),

          ("b", t2, true, 1),
          ("b", t2, true, 2),

          // Note how str("x") was never retracted and stays the same for both int values
          ("x", t4, true, 4),
          ("x", t4, true, 5)
        ))

        // Additional attributes are better used to filter the result
        // "str operations on enties having had an int value 1"
        _ <- Ns.str.t.op.int(1).getHistory.map(_.sortBy(t => (t._2, t._3, t._4)) ==> List(
          ("a", t1, true, 1),
          ("a", t2, false, 1),
          ("b", t2, true, 1)
        ))

        // ..and even better as tacit attributes
        // "str operations on entities having had an int value of 1"
        _ <- Ns.str.t.op.int_(1).getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          ("a", t1, true),
          ("a", t2, false),
          ("b", t2, true)
        ))

        // Giving the int value 5 we get to the second entity
        // "str operations on entities having had an int value of 5"
        _ <- Ns.str.t.op.int_(5).getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          ("x", t4, true)
        ))


        // This is not so useful. So instead, we might want to use the int
        // attribute to filter

        // Reversing the attributes we get to the first entity via a or b:
        // "int operations on entities having had an int value of a"
        _ <- Ns.int.t.op.str_("a").getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          (1, t1, true),
          (1, t3, false),
          (2, t3, true)
        ))

        // "int operations on entities having had a str value of b"
        _ <- Ns.int.t.op.str_("b").getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          (1, t1, true),
          (1, t3, false),
          (2, t3, true)
        ))

        // Getting historic operations on second entity via str value x
        // "int operations on entities having had a str value of x"
        _ <- Ns.int.t.op.str_("x").getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          (4, t4, true),
          (4, t5, false),
          (5, t5, true)
        ))

        // Order of attributes is free.
        // All generic attributes always relate to the previous domain attribute (`int` here)
        _ <- Ns.str_("x").int.t.op.getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          (4, t4, true),
          (4, t5, false),
          (5, t5, true)
        ))
      } yield ()
    }


    "Multiple attrs" - core { implicit conn =>
      for {
        (tx1, e1, t1, tx2, t2, tx3, t3, tx4, e2, t4, tx5, t5) <- data

        // We _can_ combine multiple attrs with generic attributes in a history
        // query but then two individual attribute history "timelines" of changes
        // are unified which is quite little use:
        _ <- Ns(e1).str.t.op.int.t.op.getHistory.map(_.sortBy(t => (t._2, t._1, t._5, t._6)) ==> List(
          ("a", t1, true, 1, t1, true),
          ("a", t1, true, 1, t3, false),
          ("a", t1, true, 2, t3, true),
          ("a", t2, false, 1, t1, true),
          ("a", t2, false, 1, t3, false),
          ("a", t2, false, 2, t3, true),
          ("b", t2, true, 1, t1, true),
          ("b", t2, true, 1, t3, false),
          ("b", t2, true, 2, t3, true),
        ))
      } yield ()
    }


    "Entity history" - core { implicit conn =>
      for {
        (tx1, e1, t1, tx2, t2, tx3, t3, tx4, e2, t4, tx5, t5) <- data

        // Instead of building a history of an entity with multiple fixed attributes
        // as a molecule we can also look for _any_ attribute involved in an entity's history:

        // All attribute assertions/retractions of entity e1
        _ <- Ns(e1).a.v.t.op.getHistory.map(_.sortBy(t => (t._1, t._3, t._4)) ==> List(
          (":Ns/int", 1, t1, true),
          (":Ns/int", 1, t3, false),
          (":Ns/int", 2, t3, true),
          (":Ns/str", "a", t1, true),
          (":Ns/str", "a", t2, false),
          (":Ns/str", "b", t2, true),
        ))

        // All attribute assertions of entity e1
        _ <- Ns(e1).a.v.t.op(true).getHistory.map(_.sortBy(t => (t._1, t._3, t._4)) ==> List(
          (":Ns/int", 1, t1, true),
          (":Ns/int", 2, t3, true),
          (":Ns/str", "a", t1, true),
          (":Ns/str", "b", t2, true),
        ))

        // All attribute retractions of entity e1
        _ <- Ns(e1).a.v.t.op(false).getHistory.map(_.sortBy(t => (t._1, t._3, t._4)) ==> List(
          (":Ns/int", 1, t3, false),
          (":Ns/str", "a", t2, false),
        ))

        // All attribute assertions/retractions of entity e1 at t2
        _ <- Ns(e1).a.v.t(t2).op.getHistory.map(_.sortBy(t => t._4) ==> List(
          // str value was updated from "a" to "b"
          (":Ns/str", "a", t2, false),
          (":Ns/str", "b", t2, true)
        ))

        // All attribute retractions of entity e1 at t2
        _ <- Ns(e1).a.v.t(t2).op(false).getHistory.map(_ ==> List(
          // str value "a" was retracted at t2
          (":Ns/str", "a", t2, false)
        ))

        // All attribute assertions of entity e1 at t2
        _ <- Ns(e1).a.v.t(t2).op(true).getHistory.map(_ ==> List(
          // str value "b" was asserted at t2
          (":Ns/str", "b", t2, true)
        ))

        // All attribute assertions with value "a" of entity e1
        _ <- Ns(e1).a.v("a").t.op.getHistory.map(_.sortBy(_._3) ==> List(
          (":Ns/str", "a", t1, true),
          (":Ns/str", "a", t2, false)
        ))

        // All attribute assertions with value "a" of entity e1 at t2
        _ <- Ns(e1).a.v("a").t(t2).op.getHistory.map(_ ==> List(
          (":Ns/str", "a", t2, false)
        ))

        // All attribute assertions with value "a" of entity e1
        _ <- Ns(e1).a.v(2).t.op.getHistory.map(_ ==> List(
          (":Ns/int", 2, t3, true)
        ))
      } yield ()
    }


    "Tacit generic attrs" - core { implicit conn =>
      for {
        (tx1, e1, t1, tx2, t2, tx3, t3, tx4, e2, t4, tx5, t5) <- data

        // Entities with retractions
        _ <- Ns.e.a.v.t.op_(false).getHistory.map(_.sortBy(_._4) ==> List(
          (e1, ":Ns/str", "a", t2),
          (e1, ":Ns/int", 1, t3),
          (e2, ":Ns/int", 4, t5)
        ))

        // Transaction dates
        date2 = tx2.inst
        date3 = tx3.inst
        date5 = tx5.inst

        _ <- Ns.e.a.v.txInstant.op_(false).getHistory.map(_.sortBy(t => (t._2, t._4)) ==> List(
          (e1, ":Ns/int", 1, date3),
          (e2, ":Ns/int", 4, date5),
          (e1, ":Ns/str", "a", date2)
        ))

        // Entities involved in transaction t2
        // Note how the transaction itself is included
        _ <- Ns.e.a.v.t_(t2).op.getHistory.map(_.sortBy(_._4) ==> List(
          (e1, ":Ns/str", "a", false),
          (e1, ":Ns/str", "b", true)
        ))

        // Using transaction date
        // Entities involved in transaction as of date2
        _ <- Ns.e.a.v.txInstant_(date2).op.getHistory.map(_.sortBy(_._4) ==> List(
          (e1, ":Ns/str", "a", false),
          (e1, ":Ns/str", "b", true)
        ))
      } yield ()
    }
  }
}