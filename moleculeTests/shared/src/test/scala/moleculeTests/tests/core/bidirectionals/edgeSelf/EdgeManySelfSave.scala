package moleculeTests.tests.core.bidirectionals.edgeSelf

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EdgeManySelfSave extends AsyncTestSuite {

  val knownBy = m(Person.name_(?).Knows.weight.Person.name)

  lazy val tests = Tests {

    "base/edge/target" - {

      "no nesting in save molecules" - bidirectional { implicit conn =>
        for {
          // Insert entities, each having one or more connected entities with relationship properties
          tx <- Person.name.insert("Ben")
          ben = tx.eid
          _ <- Person.name("Ben").Knows.*(Knows.weight(7).person(ben)).save.recover {
            case VerifyModelException(err) =>
              err ==> s"[noNested]  Nested data structures not allowed in save molecules"
          }
        } yield ()
      }

      // Since we can't nest in save-molecules, saves will be the same for cardinality one/many,
      // and the following two test will be the same as for tests in EdgeOneSelfSave

      "new target" - bidirectional { implicit conn =>
        for {
          _ <- Person.name("Ann").Knows.weight(7).Person.name("Ben").save

          // Ann and Ben know each other with a weight of 7
          _ <- Person.name.Knows.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ben", 7, "Ann")
          ))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx <- Person.name.insert("Ben")
          ben = tx.eid

          // Save Ann with weighed relationship to existing Ben
          _ <- Person.name("Ann").Knows.weight(7).person(ben).save

          // Ann and Ben know each other with a weight of 7
          _ <- Person.name.Knows.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ben", 7, "Ann")
          ))
        } yield ()
      }
    }


    "base + edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          // Create edges to new target entities
          tx1 <- Knows.weight(7).Person.name("Ben").save
          tx2 <- Knows.weight(8).Person.name("Joe").save

          knowsBen = tx1.eid
          knowsJoe = tx2.eid

          // Connect multiple edges
          _ <- Person.name("Ann").knows(knowsBen, knowsJoe).save

          // Ann and Ben know each other with a weight of 7
          _ <- Person.name.Knows.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ann", 8, "Joe"),
            ("Ben", 7, "Ann"),
            ("Joe", 8, "Ann")
          ))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx <- Person.name.insert("Ben", "Joe")
          List(ben, joe) = tx.eids

          // Create edges to existing target entities
          tx1 <- Knows.weight(7).person(ben).save
          tx2 <- Knows.weight(8).person(joe).save
          knowsBen = tx1.eid
          knowsJoe = tx2.eid

          // Connect multiple edges
          _ <- Person.name("Ann").knows(knowsBen, knowsJoe).save

          // Ann and Ben know each other with a weight of 7
          _ <- Person.name.Knows.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ann", 8, "Joe"),
            ("Ben", 7, "Ann"),
            ("Joe", 8, "Ann")
          ))
        } yield ()
      }
    }

    // Edge consistency checks.
    // Any edge should always be connected to both a base and a target entity.
    "base/edge - <missing target>" - bidirectional { implicit conn =>
      // Can't save edge missing the target namespace (`Person`)
      // The edge needs to be complete at all times to preserve consistency.
      Person.name("Ann").Knows.weight(5).save.recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace after edge namespace `Knows`."
      }
    }

    "<missing base> - edge - <missing target>" - bidirectional { implicit conn =>
      Knows.weight(7).save.recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace somewhere after edge property `Knows/weight`."
      }
    }
  }
}
