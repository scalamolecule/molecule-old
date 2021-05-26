package moleculeTests.tests.core.bidirectionals.edgeOther

import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.datomic.api.in1_out4._
import molecule.core.ops.exception.VerifyModelException
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EdgeManyOtherInsert extends AsyncTestSuite {

  val animalsCloseTo = m(Person.name_(?).CloseTo.*(CloseTo.weight.Animal.name))
  val personsCloseTo = m(Animal.name_(?).CloseTo.*(CloseTo.weight.Person.name))

  lazy val tests = Tests {

    "base/edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          // Insert molecules allow nested data structures. So we can conveniently
          // insert 2 entities each connected to 2 target entites via property edges
          _ <- Person.name.CloseTo.*(CloseTo.weight.Animal.name) insert List(("Ann", List((7, "Gus"), (6, "Leo"))))

          _ <- animalsCloseTo("Ann").get === List(List((7, "Gus"), (6, "Leo")))
          _ <- personsCloseTo("Gus").get === List(List((7, "Ann")))
          _ <- personsCloseTo("Leo").get === List(List((6, "Ann")))
        } yield ()
      }


      "existing targets" - bidirectional { implicit conn =>
        for {
          tx <- Animal.name.insert("Gus", "Leo")
          List(gus, leo) = tx.eids

          // Insert 2 Persons and connect them with existing Persons
          _ <- Person.name.CloseTo.*(CloseTo.weight.animal) insert List(("Ann", List((7, gus), (6, leo))))

          _ <- animalsCloseTo("Ann").get === List(List((7, "Gus"), (6, "Leo")))
          _ <- personsCloseTo("Gus").get === List(List((7, "Ann")))
          _ <- personsCloseTo("Leo").get === List(List((6, "Ann")))
        } yield ()

        //    "nested edge only not allowed" - bidirectional { implicit conn =>
        //
        //      // Can't save nested edges without including target entity
        //      (Person.name.CloseTo.*(CloseTo.weight).Animal.name insert List(
        //        ("Ann", List(7, 8), "Gus")
        //      ) must throwA[VerifyModelException]).message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
        //        s"[noNestedEdgesWithoutTarget]  Nested edge ns `CloseTo` should link to " +
        //        s"target ns within the nested group of attributes."
        //    }
      }
    }


    "base + edge/target" - {

      "new targets" - bidirectional { implicit conn =>
        for {

          // Create edges to new target entities
          tx <- CloseTo.weight.Animal.name.insert(List((7, "Gus"), (8, "Leo")))
          Seq(closeToGus, closeToLeo) = tx.eids.grouped(3).map(_.head).toSeq

          // Connect base entity to edges
          _ <- Person.name.closeTo.insert("Ann", Set(closeToGus, closeToLeo))

          // Bidirectional property edges have been inserted
          _ <- animalsCloseTo("Ann").get.map(_.head ==> List((7, "Gus"), (8, "Leo")))
          _ <- personsCloseTo("Gus").get.map(_.head ==> List((7, "Ann")))
          _ <- personsCloseTo("Leo").get.map(_.head ==> List((8, "Ann")))
        } yield ()
      }

      "existing targets" - bidirectional { implicit conn =>
        for {

          tx1 <- Animal.name.insert("Gus", "Leo")
          Seq(gus, leo) = tx1.eids

          // Create edges to existing target entities
          tx2 <- CloseTo.weight.animal.insert(List((7, gus), (8, leo)))
          Seq(closeToGus, closeToLeo) = tx2.eids.grouped(3).map(_.head).toSeq

          // Connect base entity to edges
          _ <- Person.name.closeTo.insert("Ann", Set(closeToGus, closeToLeo))

          // Bidirectional property edges have been inserted
          _ <- animalsCloseTo("Ann").get.map(_.head ==> List((7, "Gus"), (8, "Leo")))
          _ <- personsCloseTo("Gus").get.map(_.head ==> List((7, "Ann")))
          _ <- personsCloseTo("Leo").get.map(_.head ==> List((8, "Ann")))
        } yield ()
      }
    }

    // Edge consistency checks
//
//    "base - edge - <missing target>" - bidirectional { implicit conn =>
//      for {
//
//        // Can't allow edge without ref to target entity
//        (Person.name.CloseTo.weight.insert must throwA[VerifyModelException])
//        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
//          s"[edgeComplete]  Missing target namespace after edge namespace `CloseTo`."
//      } yield ()
//    }
//
//    "<missing base> - edge - <missing target>" - bidirectional { implicit conn =>
//      for {
//
//        // Edge always have to have a ref to a target entity
//        (CloseTo.weight.insert must throwA[VerifyModelException])
//        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
//          s"[edgeComplete]  Missing target namespace somewhere after edge property `CloseTo/weight`."
//      } yield ()
//    }
  }
}
