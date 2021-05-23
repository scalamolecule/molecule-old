package molecule.tests.core.bidirectionals.edgeOther

import molecule.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.datomic.api.in1_out5._
import molecule.core.ops.exception.VerifyModelException
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object EdgeManyOtherSave extends AsyncTestSuite {

  val animalsCloseTo = m(Person.name_(?).CloseTo.*(CloseTo.weight.Animal.name))
  val personsCloseTo = m(Animal.name_(?).CloseTo.*(CloseTo.weight.Person.name))

  lazy val tests = Tests {

    "base/edge/target" - {

      //    "no nesting in save molecules" - bidirectional { implicit conn =>
      //
      //      (Person.name("Ann").CloseTo.*(CloseTo.weight(7)).save must throwA[VerifyModelException])
      //        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //        s"[noNested]  Nested data structures not allowed in save molecules"
      //
      //      // Insert entities, each having one or more connected entities with relationship properties
      //      val rex = Animal.name.insert("Rex").eid
      //      (Person.name("Rex").CloseTo.*(CloseTo.weight(7).animal(rex)).save must throwA[VerifyModelException])
      //        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //        s"[noNested]  Nested data structures not allowed in save molecules"
      //    }


      // Since we can't nest in save-molecules, saves will be the same for cardinality one/many,
      // and the following two test will be the same as for tests in EdgeOneOtherSave

      "new target" - bidirectional { implicit conn =>
        for {
          _ <- Person.name("Ann").CloseTo.weight(7).Animal.name("Rex").save

          // Bidirectional property edges have been saved
          _ <- animalsCloseTo("Ann").get === List(List((7, "Rex")))
          _ <- personsCloseTo("Rex").get === List(List((7, "Ann")))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx <- Animal.name.insert("Rex")
          rex = tx.eid

          // Save Ann with weighed relationship to existing Rex
          _ <- Person.name("Ann").CloseTo.weight(7).animal(rex).save

          // Ann and Rex each others CloseTo with a weight of 7
          _ <- animalsCloseTo("Ann").get === List(List((7, "Rex")))
          _ <- personsCloseTo("Rex").get === List(List((7, "Ann")))
        } yield ()
      }
    }

    "base + edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          // Create edges to new target entities
          tx1 <- CloseTo.weight(7).Animal.name("Gus").save
          closeToGus = tx1.eid
          tx2 <- CloseTo.weight(8).Animal.name("Leo").save
          closeToLeo = tx2.eid

          // Connect multiple edges
          _ <- Person.name("Ann").closeTo(closeToGus, closeToLeo).save

          // Ann and Gus know each other with a weight of 7
          _ <- animalsCloseTo("Ann").get.map(_.sorted) === List(List((7, "Gus"), (8, "Leo")))
          _ <- personsCloseTo("Gus").get === List(List((7, "Ann")))
          _ <- personsCloseTo("Leo").get === List(List((8, "Ann")))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {

          tx1 <- Animal.name.insert("Gus", "Leo")
          List(gus, leo) = tx1.eids

          // Create edges to existing target entities
          tx2 <- CloseTo.weight(7).animal(gus).save
          closeToGus = tx2.eid
          tx3 <- CloseTo.weight(8).animal(leo).save
          closeToLeo = tx3.eid

          // Connect multiple edges
          _ <- Person.name("Ann").closeTo(closeToGus, closeToLeo).save

          // Ann and Gus know each other with a weight of 7
          _ <- animalsCloseTo("Ann").get === List(List((7, "Gus"), (8, "Leo")))
          _ <- personsCloseTo("Gus").get === List(List((7, "Ann")))
          _ <- personsCloseTo("Leo").get === List(List((8, "Ann")))
        } yield ()
      }
    }

    // Edge consistency checks.

    //  "base - edge - <missing target>" - bidirectional { implicit conn =>
    //
    //    // Can't allow edge without ref to target entity
    //    (Person.name("Gus").CloseTo.weight(5).save must throwA[VerifyModelException])
    //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
    //      s"[edgeComplete]  Missing target namespace after edge namespace `CloseTo`."
    //    } yield ()
    //    }
    //
    //"<missing base> - edge - <missing target>" - bidirectional {   implicit conn =>
    //      for {
    //
    //    // Edge always have to have a ref to a target entity
    //    (CloseTo.weight(7).save must throwA[VerifyModelException])
    //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
    //      s"[edgeComplete]  Missing target namespace somewhere after edge property `CloseTo/weight`."
    //  }
    //
    //  }
  }
}
