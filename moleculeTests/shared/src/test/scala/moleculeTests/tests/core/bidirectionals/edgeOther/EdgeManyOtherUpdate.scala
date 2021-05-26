package moleculeTests.tests.core.bidirectionals.edgeOther

import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.datomic.api.in1_out4._
import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeManyOtherUpdate extends AsyncTestSuite {

  val animalsCloseTo = m(Person.name_(?).CloseTo.*(CloseTo.weight.Animal.name))
  val personsCloseTo = m(Animal.name_(?).CloseTo.*(CloseTo.weight.Person.name))

  def testData(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Long]] = {
    for {
      tx1 <- Person.name("Ann").save

      // Separate edges
      tx2 <- CloseTo.weight.Animal.name.insert(List(
        (2, "Bob"),
        (3, "Dot"),
        (4, "Gus"),
        (5, "Hip"),
        (6, "Max"),
        (7, "Pix"),
        (8, "Zoe")
      ))
    } yield {
      tx1.eid +: tx2.eids.grouped(3).map(_.head).toSeq
    }
  }


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "add edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, closeToBob, closeToDot, closeToGus, _, _, _, _) <- testData

        // vararg
        _ <- Person(ann).closeTo.assert(closeToBob, closeToDot).update

        // Seq
        _ <- Person(ann).closeTo.assert(Seq(closeToGus)).update

        // Empty list of edges has no effect
        _ <- Person(ann).closeTo.assert(Seq()).update

        _ <- animalsCloseTo("Ann").get === List(List((2, "Bob"), (3, "Dot"), (4, "Gus")))
        _ <- personsCloseTo("Bob").get === List(List((2, "Ann")))
        _ <- personsCloseTo("Dot").get === List(List((3, "Ann")))
        _ <- personsCloseTo("Gus").get === List(List((4, "Ann")))
      } yield ()
    }


    "replace edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, closeToBob, closeToDot, closeToGus, closeToHip, closeZoeax, closeToPix, closeToZoe) <- testData

        // current friends
        _ <- Person(ann).closeTo.assert(closeToBob, closeToDot, closeToGus, closeToZoe).update

        _ <- animalsCloseTo("Ann").get === List(List((2, "Bob"), (3, "Dot"), (4, "Gus"), (8, "Zoe")))
        _ <- personsCloseTo("Bob").get === List(List((2, "Ann")))
        _ <- personsCloseTo("Dot").get === List(List((3, "Ann")))
        _ <- personsCloseTo("Gus").get === List(List((4, "Ann")))
        _ <- personsCloseTo("Zoe").get === List(List((8, "Ann")))

        // Replace who Ann closeTo
        _ <- Person(ann).closeTo.replace(closeToBob -> closeToHip, closeToDot -> closeZoeax).update
        _ <- Person(ann).closeTo.replace(Seq(closeToGus -> closeToPix)).update

        // All friends have been replaced
        _ <- animalsCloseTo("Ann").get === List(List((5, "Hip"), (6, "Max"), (7, "Pix"), (8, "Zoe")))
        _ <- personsCloseTo("Hip").get === List(List((5, "Ann")))
        _ <- personsCloseTo("Max").get === List(List((6, "Ann")))
        _ <- personsCloseTo("Pix").get === List(List((7, "Ann")))
        _ <- personsCloseTo("Zoe").get === List(List((8, "Ann"))) // Hasn't been replace by empty Seq

        // Replace with empty Seq has no effect
        _ <- Person(ann).closeTo.replace(Seq()).update
        _ <- animalsCloseTo("Ann").get === List(List((5, "Hip"), (6, "Max"), (7, "Pix"), (8, "Zoe")))
      } yield ()
    }


    "remove edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, closeToBob, closeToDot, closeToGus, _, _, _, closeToZoe) <- testData

        // current friends
        _ <- Person(ann).closeTo.assert(closeToBob, closeToDot, closeToGus, closeToZoe).update

        _ <- animalsCloseTo("Ann").get === List(List((2, "Bob"), (3, "Dot"), (4, "Gus"), (8, "Zoe")))
        _ <- personsCloseTo("Bob").get === List(List((2, "Ann")))
        _ <- personsCloseTo("Dot").get === List(List((3, "Ann")))
        _ <- personsCloseTo("Gus").get === List(List((4, "Ann")))
        _ <- personsCloseTo("Zoe").get === List(List((8, "Ann")))


        // Remove who Ann closeTo
        _ <- Person(ann).closeTo.retract(closeToBob, closeToDot).update

        // All friends have been replaced
        _ <- animalsCloseTo("Ann").get === List(List((4, "Gus"), (8, "Zoe")))
        _ <- personsCloseTo("Bob").get === List()
        _ <- personsCloseTo("Dot").get === List()
        _ <- personsCloseTo("Gus").get === List(List((4, "Ann")))
        _ <- personsCloseTo("Zoe").get === List(List((8, "Ann")))

        // Remove Seq of edges
        _ <- Person(ann).closeTo.retract(Seq(closeToGus)).update

        // All friends have been replaced
        _ <- animalsCloseTo("Ann").get === List(List((8, "Zoe")))
        _ <- personsCloseTo("Bob").get === List()
        _ <- personsCloseTo("Dot").get === List()
        _ <- personsCloseTo("Gus").get === List()
        _ <- personsCloseTo("Zoe").get === List(List((8, "Ann")))

        // Remove empty Seq of edges has no effect
        _ <- Person(ann).closeTo.retract(Seq()).update

        // All friends have been replaced
        _ <- animalsCloseTo("Ann").get === List(List((8, "Zoe")))
      } yield ()
    }


    "apply edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, closeToBob, closeToDot, closeToGus, closeToHip, _, _, closeToZoe) <- testData

        // current friends
        _ <- Person(ann).closeTo.assert(closeToBob, closeToDot, closeToGus).update

        _ <- animalsCloseTo("Ann").get === List(List((2, "Bob"), (3, "Dot"), (4, "Gus")))
        _ <- personsCloseTo("Bob").get === List(List((2, "Ann")))
        _ <- personsCloseTo("Dot").get === List(List((3, "Ann")))
        _ <- personsCloseTo("Gus").get === List(List((4, "Ann")))
        _ <- personsCloseTo("Zoe").get === List()


        // State who Ann closeTo now
        _ <- Person(ann).closeTo(closeToBob, closeToZoe).update

        // Bob remains, Zoe added
        _ <- animalsCloseTo("Ann").get === List(List((2, "Bob"), (8, "Zoe")))
        _ <- personsCloseTo("Bob").get === List(List((2, "Ann")))
        _ <- personsCloseTo("Dot").get === List()
        _ <- personsCloseTo("Gus").get === List()
        _ <- personsCloseTo("Zoe").get === List(List((8, "Ann")))

        // Apply Seq of edges
        _ <- Person(ann).closeTo(Seq(closeToHip)).update

        _ <- animalsCloseTo("Ann").get === List(List((5, "Hip")))
        _ <- personsCloseTo("Bob").get === List()
        _ <- personsCloseTo("Dot").get === List()
        _ <- personsCloseTo("Gus").get === List()
        _ <- personsCloseTo("Hip").get === List(List((5, "Ann")))
        _ <- personsCloseTo("Zoe").get === List()

        // Applying empty Seq retracts all edges from Ann!
        _ <- Person(ann).closeTo(Seq()).update

        _ <- animalsCloseTo("Ann").get === List()
        _ <- personsCloseTo("Hip").get === List()


        // Applying no values removes all!

        _ <- Person(ann).CloseTo.weight(7).Animal.name("Bob").update
        _ <- animalsCloseTo("Ann").get === List(List((7, "Bob")))

        _ <- Person(ann).closeTo().update
        _ <- animalsCloseTo("Ann").get === List()
      } yield ()
    }


    "retract edge" - bidirectional { implicit conn =>
      for {
        Seq(ann, closeToBob, closeToDot, _, _, _, _, _) <- testData

        // current friends
        _ <- Person(ann).closeTo.assert(closeToBob, closeToDot).update

        _ <- animalsCloseTo("Ann").get === List(List((2, "Bob"), (3, "Dot")))
        _ <- personsCloseTo("Bob").get === List(List((2, "Ann")))
        _ <- personsCloseTo("Dot").get === List(List((3, "Ann")))

        // Retract single edge
        _ <- closeToBob.map(_.retract)

        _ <- animalsCloseTo("Ann").get === List(List((3, "Dot")))
        _ <- personsCloseTo("Bob").get === List()
        _ <- personsCloseTo("Dot").get === List(List((3, "Ann")))
      } yield ()
    }


    "retract base/target entity" - bidirectional { implicit conn =>
      for {
        Seq(ann, closeToBob, closeToDot, _, _, _, _, _) <- testData

        // current friends
        _ <- Person(ann).closeTo.assert(closeToBob, closeToDot).update

        _ <- animalsCloseTo("Ann").get === List(List((2, "Bob"), (3, "Dot")))
        _ <- personsCloseTo("Bob").get === List(List((2, "Ann")))
        _ <- personsCloseTo("Dot").get === List(List((3, "Ann")))

        // Retract base entity
        _ <- ann.map(_.retract)

        // All knowing to/from Ann retracted
        _ <- animalsCloseTo("Ann").get === List()
        _ <- personsCloseTo("Bob").get === List()
        _ <- personsCloseTo("Dot").get === List()
      } yield ()
    }


    "no nested in update molecules" - bidirectional { implicit conn =>
      //      for {

      //    // Can't update multiple values of cardinality-one attribute `name`
      //    (Person(ann).CloseTo.weight(7).Animal.name("Max", "Liz").update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
      //      "\n  Animal ... name(Max, Liz)"

      // As with save molecules nesting is not allowed in update molecules
      //    (Person(ann).CloseTo.*(CloseTo.weight(4)).Animal.name("Max").update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[update_onlyOneNs]  Update molecules can't have nested data structures like `CloseTo`."
      //
      //    (Person(ann).CloseTo.*(CloseTo.weight(4)).animal(42L).update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[update_onlyOneNs]  Update molecules can't have nested data structures like `CloseTo`."
      //
      //    (Person(ann).CloseTo.*(CloseTo.weight(4).Animal.name("Max")).update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[update_onlyOneNs]  Update molecules can't have nested data structures like `CloseTo`."
      //
      //    (Person(ann).CloseTo.*(CloseTo.weight(4).animal(42L)).update must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[update_onlyOneNs]  Update molecules can't have nested data structures like `CloseTo`."

      // Note that an edge always have only one target entity.
      // So we can't add multiple (won't compile)
      // Person(ann).CloseTo.weight(6).animal(42L, 43L).update

      // Each edge has only 1 target entity so we can't use nested structures on the target namespace
      // (Person.name("Bob").CloseTo.weight(7).Animal.*(Animal.name("Max")).update
      //                                                ^ nesting of edge target namespace not available
      //  } yield ()
    }
  }
}
