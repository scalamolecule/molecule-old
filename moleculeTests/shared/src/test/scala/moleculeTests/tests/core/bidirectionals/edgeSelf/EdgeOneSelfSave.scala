package moleculeTests.tests.core.bidirectionals.edgeSelf

import molecule.datomic.api.out3._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EdgeOneSelfSave extends AsyncTestSuite {

  lazy val tests = Tests {

    "base/edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          /*
            When a "property edge" is created, Molecule automatically creates a reverse reference in the opposite direction:

            Ann --> annLovesBen (7) -->  Ben
              \                         /
                <-- benLovesAnn (7) <--

            This allow us to query from Ann to Ben and Ben to Ann in a uniform way.

            So we get 4 entities:
          */
          tx <- Person.name("Ann").Loves.weight(7).Person.name("Ben").save
          List(ann, annLovesBen, benLovesAnn, ben) = tx.eids

          // Bidirectional property edges have been saved
          _ <- Person.name.Loves.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            // Reverse edge:
            ("Ben", 7, "Ann")
          ))

          _ <- ann.touchMax(1).map(_ ==> Map(
            ":db/id" -> ann,
            ":Person/loves" -> annLovesBen,
            ":Person/name" -> "Ann"
          ))

          _ <- ben.touchMax(1).map(_ ==> Map(
            ":db/id" -> ben,
            ":Person/loves" -> benLovesAnn,
            ":Person/name" -> "Ben"
          ))

          _ <- ann.touchMax(2).map(_ ==> Map(
            ":db/id" -> ann,
            ":Person/loves" -> Map(
              ":db/id" -> annLovesBen,
              ":Loves/person" -> ben,
              ":Loves/weight" -> 7,
              ":molecule_Meta/otherEdge" -> benLovesAnn),
            ":Person/name" -> "Ann"
          ))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx <- Person.name.insert("Ben")
          ben = tx.eid

          // Save Ann with weighed relationship to existing Ben
          _ <- Person.name("Ann").Loves.weight(7).person(ben).save

          // Ann and Ben know each other with a weight of 7
          _ <- Person.name.Loves.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ben", 7, "Ann")
          ))
        } yield ()
      }
    }


    "base + edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          /*
            Create love edges to/from Ben
            Ben and bidirectional edges created

                  (-->)  lovesBen  -->
            (Ann)                       Ben
                  (<--)  benLoves  <--
          */
          tx <- Loves.weight(7).Person.name("Ben").save
          List(lovesBen, benLoves, ben) = tx.eids

          // lovesBen edge points to Ben
          _ <- ben.touchMax(1).map(_ ==> Map(
            ":db/id" -> ben,
            ":Person/loves" -> benLoves,
            ":Person/name" -> "Ben"
          ))

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

          // Base entity Ann points to one of the edges (doesn't matter which of them)
          tx <- Person.name("Ann").loves(lovesBen).save
          ann = tx.eid

          _ <- ben.touchMax(1).map(_ ==> Map(
            ":db/id" -> ben,
            ":Person/loves" -> benLoves,
            ":Person/name" -> "Ben"
          ))

          // Narcissistic tendencies not allowed
          _ <- Person(ann).loves(ann).update.recover { case Model2TransactionException(err) =>
            err ==> s"[valueStmts:biEdgeRefAttr]  Current entity and referenced entity ids can't be the same."
          }

          // Ann and Ben know each other with a weight of 7
          _ <- Person.name.Loves.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ben", 7, "Ann")
          ))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx <- Person.name.insert("Ben")
          ben = tx.eid

          // Create edges to existing Ben
          tx2 <- Loves.weight.person.insert(7, ben)
          List(annLovesBen, benLovesAnn) = tx2.eids

          // Base entity Ann points to one of the edges (doesn't matter which of them - Molecule connects Ann to both)
          _ <- Person.name("Ann").loves(benLovesAnn).save

          // Ann loves Ben and Ben loves Ann - that is 70% love
          _ <- Person.name.Loves.weight.Person.name.get.map(_.sorted ==> List(
            ("Ann", 7, "Ben"),
            ("Ben", 7, "Ann")
          ))
        } yield ()
      }
    }
  }
}