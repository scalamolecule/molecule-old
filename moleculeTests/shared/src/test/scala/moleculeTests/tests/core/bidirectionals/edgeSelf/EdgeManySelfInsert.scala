package moleculeTests.tests.core.bidirectionals.edgeSelf

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EdgeManySelfInsert extends AsyncTestSuite {

  val knownBy = m(Person.name_(?).Knows.*(Knows.weight.Person.name))

  lazy val tests = Tests {

    "base/edge/target" - {

      "new targets" - bidirectional { implicit conn =>
        for {
          // Create Ann with multiple edges to new target entities Ben and Joe
          _ <- Person.name.Knows.*(Knows.weight.Person.name).insert("Ann", List((7, "Ben"), (8, "Joe")))

          // Bidirectional property edges have been inserted
          _ <- knownBy("Ann").get.map(_.head ==> List((7, "Ben"), (8, "Joe")))
          _ <- knownBy("Ben").get.map(_.head ==> List((7, "Ann")))
          _ <- knownBy("Joe").get.map(_.head ==> List((8, "Ann")))
        } yield ()
      }

      "existing targets" - bidirectional { implicit conn =>
        for {
          tx <- Person.name.insert("Ben", "Joe")
          Seq(ben, joe) = tx.eids

          // Create Ann with multiple edges to existing target entities Ben and Joe
          _ <- Person.name.Knows.*(Knows.weight.person).insert("Ann", List((7, ben), (8, joe)))

          // Bidirectional property edges have been inserted
          _ <- knownBy("Ann").get.map(_.head ==> List((7, "Ben"), (8, "Joe")))
          _ <- knownBy("Ben").get.map(_.head ==> List((7, "Ann")))
          _ <- knownBy("Joe").get.map(_.head ==> List((8, "Ann")))
        } yield ()
      }

      "nested edge only not allowed" - bidirectional { implicit conn =>
        // Can't save nested edges without including target entity
        (Person.name.Knows.*(Knows.weight).name insert List(("Ben", List(7, 8), "Joe"))).recover {
          case VerifyModelException(err) =>
            err ==> s"[noNestedEdgesWithoutTarget]  Nested edge ns `Knows` should link to " +
              s"target ns within the nested group of attributes."
        }
      }
    }

    "base + edge/target" - {

      "new targets" - bidirectional { implicit conn =>
        for {
          // Create edges to new target entities
          tx <- Knows.weight.Person.name.insert(List((7, "Ben"), (8, "Joe")))
          Seq(knowsBen, knowsJoe) = tx.eids.grouped(3).map(_.head).toSeq

          // Connect base entity to edges
          _ <- Person.name.knows.insert("Ann", Set(knowsBen, knowsJoe))

          // Bidirectional property edges have been inserted
          _ <- knownBy("Ann").get.map(_.head ==> List((7, "Ben"), (8, "Joe")))
          _ <- knownBy("Ben").get.map(_.head ==> List((7, "Ann")))
          _ <- knownBy("Joe").get.map(_.head ==> List((8, "Ann")))
        } yield ()
      }

      "existing targets" - bidirectional { implicit conn =>
        for {
          tx1 <- Person.name.insert("Ben", "Joe")
          Seq(ben, joe) = tx1.eids

          // Create edges to existing target entities
          tx2 <- Knows.weight.person.insert(List((7, ben), (8, joe)))
          Seq(knowsBen, knowsJoe) = tx2.eids.grouped(3).map(_.head).toSeq

          // Connect base entity to edges
          _ <- Person.name.knows.insert("Ann", Set(knowsBen, knowsJoe))

          // Bidirectional property edges have been inserted
          _ <- knownBy("Ann").get.map(_.head ==> List((7, "Ben"), (8, "Joe")))
          _ <- knownBy("Ben").get.map(_.head ==> List((7, "Ann")))
          _ <- knownBy("Joe").get.map(_.head ==> List((8, "Ann")))
        } yield ()
      }
    }

    // Edge consistency checks

    "base/edge - <missing target>" - bidirectional { implicit conn =>
      // Can't allow edge without ref to target entity
      Person.name.Knows.weight.insert("Don", 5).recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace after edge namespace `Knows`."
      }
    }

    "<missing base> - edge - <missing target>" - bidirectional { implicit conn =>
      // Edge always have to have a ref to a target entity
      Knows.weight.insert(5).recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace somewhere after edge property `Knows/weight`."
      }
    }
  }
}
