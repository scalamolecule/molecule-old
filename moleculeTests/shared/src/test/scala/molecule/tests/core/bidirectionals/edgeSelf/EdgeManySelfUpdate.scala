package molecule.tests.core.bidirectionals.edgeSelf

import molecule.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.datomic.api.in1_out3._
import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.base.facade.Conn
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeManySelfUpdate extends AsyncTestSuite {


  //  class Setup extends BidirectionalSetup {
  //    val knownBy = m(Person.name_(?).Knows.*(Knows.weight.Person.name))
  //    val ann     = Person.name("Ann").save.eid
  //
  //    // Separate edges
  //    val Seq(
  //    knowsBen,
  //    knowsDon,
  //    knowsGil,
  //    knowsHan,
  //    knowsJoe,
  //    knowsRon,
  //    knowsTom
  //    ): Seq[Long] = Knows.weight.Person.name.insert(List(
  //      (2, "Ben"),
  //      (3, "Don"),
  //      (4, "Gil"),
  //      (5, "Han"),
  //      (6, "Joe"),
  //      (7, "Ron"),
  //      (8, "Tom")
  //    )).eids.grouped(3).map(_.head).toSeq
  //    } yield ()
  //    }
  val knownBy = m(Person.name_(?).Knows.*(Knows.weight.Person.name))

  def testData(implicit conn: Conn, ec: ExecutionContext): Future[Seq[Long]] = {
    for {
      tx1 <- Person.name("Ann").save

      // Separate edges
      tx2 <- Knows.weight.Person.name.insert(List(
        (2, "Ben"),
        (3, "Don"),
        (4, "Gil"),
        (5, "Han"),
        (6, "Joe"),
        (7, "Ron"),
        (8, "Tom")
      ))
    } yield tx1.eid +: tx2.eids.grouped(3).map(_.head).toSeq
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "add edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // vararg
        _ <- Person(ann).knows.assert(knowsBen, knowsDon).update

        // Seq
        _ <- Person(ann).knows.assert(Seq(knowsGil)).update

        // Empty list of edges has no effect
        _ <- Person(ann).knows.assert(Seq()).update

        _ <- knownBy("Ann").get === List(List((2, "Ben"), (3, "Don"), (4, "Gil")))
        _ <- knownBy("Ben").get === List(List((2, "Ann")))
        _ <- knownBy("Don").get === List(List((3, "Ann")))
        _ <- knownBy("Gil").get === List(List((4, "Ann")))
      } yield ()
    }


    "replace edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon, knowsGil, knowsTom).update

        _ <- knownBy("Ann").get === List(List((2, "Ben"), (3, "Don"), (4, "Gil"), (8, "Tom")))
        _ <- knownBy("Ben").get === List(List((2, "Ann")))
        _ <- knownBy("Don").get === List(List((3, "Ann")))
        _ <- knownBy("Gil").get === List(List((4, "Ann")))
        _ <- knownBy("Tom").get === List(List((8, "Ann")))

        // Replace who Ann knows
        _ <- Person(ann).knows.replace(knowsBen -> knowsHan, knowsDon -> knowsJoe).update
        _ <- Person(ann).knows.replace(Seq(knowsGil -> knowsRon)).update

        // All friends have been replaced
        _ <- knownBy("Ann").get === List(List((5, "Han"), (6, "Joe"), (7, "Ron"), (8, "Tom")))
        _ <- knownBy("Han").get === List(List((5, "Ann")))
        _ <- knownBy("Joe").get === List(List((6, "Ann")))
        _ <- knownBy("Ron").get === List(List((7, "Ann")))
        _ <- knownBy("Tom").get === List(List((8, "Ann"))) // Hasn't been replace by empty Seq

        // Replace with empty Seq has no effect
        _ <- Person(ann).knows.replace(Seq()).update
        _ <- knownBy("Ann").get === List(List((5, "Han"), (6, "Joe"), (7, "Ron"), (8, "Tom")))
      } yield ()
    }


    "remove edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon, knowsGil, knowsTom).update

        _ <- knownBy("Ann").get === List(List((2, "Ben"), (3, "Don"), (4, "Gil"), (8, "Tom")))
        _ <- knownBy("Ben").get === List(List((2, "Ann")))
        _ <- knownBy("Don").get === List(List((3, "Ann")))
        _ <- knownBy("Gil").get === List(List((4, "Ann")))
        _ <- knownBy("Tom").get === List(List((8, "Ann")))


        // Remove who Ann knows
        _ <- Person(ann).knows.retract(knowsBen, knowsDon).update

        // All friends have been replaced
        _ <- knownBy("Ann").get === List(List((4, "Gil"), (8, "Tom")))
        _ <- knownBy("Ben").get === List()
        _ <- knownBy("Don").get === List()
        _ <- knownBy("Gil").get === List(List((4, "Ann")))
        _ <- knownBy("Tom").get === List(List((8, "Ann")))

        // Remove Seq of edges
        _ <- Person(ann).knows.retract(Seq(knowsGil)).update

        // All friends have been replaced
        _ <- knownBy("Ann").get === List(List((8, "Tom")))
        _ <- knownBy("Ben").get === List()
        _ <- knownBy("Don").get === List()
        _ <- knownBy("Gil").get === List()
        _ <- knownBy("Tom").get === List(List((8, "Ann")))

        // Remove empty Seq of edges has no effect
        _ <- Person(ann).knows.retract(Seq()).update

        // All friends have been replaced
        _ <- knownBy("Ann").get === List(List((8, "Tom")))
      } yield ()
    }


    "apply edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon, knowsGil).update

        _ <- knownBy("Ann").get === List(List((2, "Ben"), (3, "Don"), (4, "Gil")))
        _ <- knownBy("Ben").get === List(List((2, "Ann")))
        _ <- knownBy("Don").get === List(List((3, "Ann")))
        _ <- knownBy("Gil").get === List(List((4, "Ann")))
        _ <- knownBy("Tom").get === List()


        // State who Ann knows now
        _ <- Person(ann).knows(knowsBen, knowsTom).update

        // Ben remains, Tom added
        _ <- knownBy("Ann").get === List(List((2, "Ben"), (8, "Tom")))
        _ <- knownBy("Ben").get === List(List((2, "Ann")))
        _ <- knownBy("Don").get === List()
        _ <- knownBy("Gil").get === List()
        _ <- knownBy("Tom").get === List(List((8, "Ann")))

        // Apply Seq of edges
        _ <- Person(ann).knows(Seq(knowsHan)).update

        _ <- knownBy("Ann").get === List(List((5, "Han")))
        _ <- knownBy("Ben").get === List()
        _ <- knownBy("Don").get === List()
        _ <- knownBy("Gil").get === List()
        _ <- knownBy("Han").get === List(List((5, "Ann")))
        _ <- knownBy("Tom").get === List()

        // Applying empty Seq retracts all edges from Ann!
        _ <- Person(ann).knows(Seq()).update

        _ <- knownBy("Ann").get === List()
        _ <- knownBy("Han").get === List()


        // Applying no values removes all!

        _ <- Person(ann).Knows.weight(7).Person.name("Bob").update
        _ <- knownBy("Ann").get === List(List((7, "Bob")))

        _ <- Person(ann).knows().update
        _ <- knownBy("Ann").get === List()
      } yield ()
    }


    "retract edge" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon).update

        _ <- knownBy("Ann").get === List(List((2, "Ben"), (3, "Don")))
        _ <- knownBy("Ben").get === List(List((2, "Ann")))
        _ <- knownBy("Don").get === List(List((3, "Ann")))

        // Retract single edge
        _ <- knowsBen.retract

        _ <- knownBy("Ann").get === List(List((3, "Don")))
        _ <- knownBy("Ben").get === List()
        _ <- knownBy("Don").get === List(List((3, "Ann")))
      } yield ()
    }


    "retract base/target entity" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon).update

        _ <- knownBy("Ann").get === List(List((2, "Ben"), (3, "Don")))
        _ <- knownBy("Ben").get === List(List((2, "Ann")))
        _ <- knownBy("Don").get === List(List((3, "Ann")))

        // Retract base entity
        _ <- ann.retract

        // All knowing to/from Ann retracted
        _ <- knownBy("Ann").get === List()
        _ <- knownBy("Ben").get === List()
        _ <- knownBy("Don").get === List()
      } yield ()
    }


    "no nested in update molecules" - bidirectional { implicit conn =>
      //      for {
      //  Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData
      //
      //    // Can't update multiple values of cardinality-one attribute `name`
      //    (Person(ann).Knows.weight(7).Person.name("Joe", "Liz").update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
      //      "\n  Person ... name(Joe, Liz)"
      //
      //    // As with save molecules nesting is not allowed in update molecules
      ////    (Person(ann).Knows.*(Knows.weight(4)).Person.name("Joe").update must throwA[VerifyModelException])
      //    (Person(ann).Knows.*(Knows.weight(4)).name("Joe").update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Knows`."
      //
      ////    (Person(ann).Knows.*(Knows.weight(4)).person(42L).update must throwA[VerifyModelException])
      //    (Person(ann).Knows.*(Knows.weight(4)).e(42L).update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Knows`."
      //
      //    (Person(ann).Knows.*(Knows.weight(4).Person.name("Joe")).update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Knows`."
      //
      //    (Person(ann).Knows.*(Knows.weight(4).person(42L)).update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Knows`."
      //
      //    // Note that an edge always have only one target entity.
      //    // So we can't add multiple (won't compile)
      //    // Person(ann).Knows.weight(6).person(42L, 43L).update
      //
      //    // Each edge has only 1 target entity so we can't use nested structures on the target namespace
      //    // (Person.name("Ben").Knows.weight(7).Person.*(Person.name("Joe")).update
      //    //                                                   ^ nesting of edge target namespace not available
      //  }   yield ()
    }
  }
}
