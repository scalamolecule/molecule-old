package moleculeTests.tests.core.bidirectionals.edgeOther

import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
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

          _ <- animalsCloseTo("Ann").get.map(_ ==> List(List((7, "Gus"), (6, "Leo"))))
          _ <- personsCloseTo("Gus").get.map(_ ==> List(List((7, "Ann"))))
          _ <- personsCloseTo("Leo").get.map(_ ==> List(List((6, "Ann"))))
        } yield ()
      }


      "existing targets" - bidirectional { implicit conn =>
        for {
          List(gus, leo) <- Animal.name.insert("Gus", "Leo").map(_.eids)

          // Insert 2 Persons and connect them with existing Persons
          _ <- Person.name.CloseTo.*(CloseTo.weight.animal) insert List(("Ann", List((7, gus), (6, leo))))

          _ <- animalsCloseTo("Ann").get.map(_ ==> List(List((7, "Gus"), (6, "Leo"))))
          _ <- personsCloseTo("Gus").get.map(_ ==> List(List((7, "Ann"))))
          _ <- personsCloseTo("Leo").get.map(_ ==> List(List((6, "Ann"))))
        } yield ()
      }
    }


    "base + edge/target" - {

      "new targets" - bidirectional { implicit conn =>
        for {
          // Create edges to new target entities
          tx <- CloseTo.weight.Animal.name.insert(List((7, "Gus"), (8, "Leo")))
          List(closeToGus, _, _, closeToLeo, _, _) = tx.eids

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
          List(gus, leo) <- Animal.name.insert("Gus", "Leo").map(_.eids)

          // Create edges to existing target entities
          List(
          closeToGus, _, // gus edges
          closeToLeo, _ // leo edges
          ) <- CloseTo.weight.animal.insert(List((7, gus), (8, leo))).map(_.eids)

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

    "base - edge - <missing target>" - bidirectional { implicit conn =>
      // Can't allow edge without ref to target entity
      Person.name.CloseTo.weight.insert("Don", 5).recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace after edge namespace `CloseTo`."
      }
    }

    "<missing base> - edge - <missing target>" - bidirectional { implicit conn =>
      // Edge always have to have a ref to a target entity
      CloseTo.weight.insert(5).recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace somewhere after edge property `CloseTo/weight`."
      }
    }
  }
}
