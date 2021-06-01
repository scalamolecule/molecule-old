package moleculeTests.tests.core.bidirectionals.edgeSelf

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EdgeOneSelfInsert extends AsyncTestSuite {

  val loveOf = m(Person.name_(?).Loves.weight.Person.name)

  lazy val tests = Tests {
    "base/edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          // Insert 1 pair of entities with bidirectional property edge between them
          _ <- Person.name.Loves.weight.Person.name.insert("Ann", 7, "Ben")

          // Bidirectional property edge has been inserted
          _ <- loveOf("Ann").get.map(_ ==> List((7, "Ben")))
          _ <- loveOf("Ben").get.map(_ ==> List((7, "Ann")))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx <- Person.name.insert("Ben")
          ben = tx.eid

          // Insert Ann with bidirectional property edge to existing Ben
          _ <- Person.name.Loves.weight.person.insert("Ann", 7, ben)

          // Bidirectional property edge has been inserted
          _ <- loveOf("Ann").get.map(_ ==> List((7, "Ben")))
          _ <- loveOf("Ben").get.map(_ ==> List((7, "Ann")))
        } yield ()
      }
    }

    "base + edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          // Create edges and Ben
          tx <- Loves.weight.Person.name.insert(7, "Ben")
          List(lovesBen, benLoves, ben) = tx.eids

          /*
            Ben and bidirectional edges created

                  (-->)  lovesBen  -->
            (Ann)                       Ben
                  (<--)  benLoves  <--
          */

          // lovesBen edge points to Ben
          _ <- lovesBen.touchMax(1).map(_ ==> Map(
            ":db/id" -> lovesBen,
            ":Loves/person" -> ben,
            ":Loves/weight" -> 7,
            ":molecule_Meta/otherEdge" -> benLoves // To be able to find the other edge later
          ))

          // Ben points to edge benLoves
          _ <- ben.touchMax(1).map(_ ==> Map(
            ":db/id" -> ben,
            ":Person/loves" -> benLoves,
            ":Person/name" -> "Ben"
          ))

          // benLoves edge is ready to point back to a base entity (Ann)
          _ <- benLoves.touchMax(1).map(_ ==> Map(
            ":db/id" -> benLoves,
            ":Loves/weight" -> 7,
            ":molecule_Meta/otherEdge" -> lovesBen // To be able to find the other edge later
          ))

          // Two edges pointing to and from Ben
          _ <- Loves.e.weight_(7).Person.name_("Ben").get.map(_ ==> List(lovesBen))
          _ <- Person.name_("Ben").Loves.weight_(7).e.get.map(_ ==> List(benLoves))

          // Base entity Ann points to one of the edges (doesn't matter which of them - Molecule connects Ann to both)
          _ <- Person.name.loves.insert("Ann", lovesBen)

          // Ann loves Ben and Ben loves Ann - that is 70% love
          _ <- Person.name.Loves.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ben", 7, "Ann")
          ))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx1 <- Person.name.insert("Ben")
          ben = tx1.eid

          // Create edges to existing Ben
          tx2 <- Loves.weight.person.insert(7, ben)
          List(annLovesBen, benLovesAnn) = tx2.eids

          // Base entity Ann points to one of the edges (doesn't matter which of them - Molecule connects Ann to both)
          _ <- Person.name.loves.insert("Ann", benLovesAnn)

          // Ann loves Ben and Ben loves Ann - that is 70% love
          _ <- Person.name.Loves.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ben", 7, "Ann")
          ))
        } yield ()
      }
    }

    "base/edge - <missing target>" - bidirectional { implicit conn =>
      // Can't allow edge without ref to target entity
      Person.name.Loves.weight.insert("Don", 5).recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace after edge namespace `Loves`."
      }
    }

    "<missing base> - edge - <missing target>" - bidirectional { implicit conn =>
      // Edge always have to have a ref to a target entity
      Loves.weight.insert(5).recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace somewhere after edge property `Loves/weight`."
      }
    }
  }
}