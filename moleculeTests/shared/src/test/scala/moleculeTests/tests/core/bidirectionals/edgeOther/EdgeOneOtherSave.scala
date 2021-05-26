package moleculeTests.tests.core.bidirectionals.edgeOther

import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.datomic.api.in1_out3._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EdgeOneOtherSave extends AsyncTestSuite {

  val favoriteAnimalOf = m(Person.name_(?).Favorite.weight.Animal.name)
  val favoritePersonOf = m(Animal.name_(?).Favorite.weight.Person.name)

  lazy val tests = Tests {

    "base/edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          _ <- Person.name("Ann").Favorite.weight(7).Animal.name("Rex").save

          // Bidirectional property edges have been saved
          _ <- favoriteAnimalOf("Ann").get === List((7, "Rex"))
          _ <- favoritePersonOf("Rex").get === List((7, "Ann"))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {

          tx <- Animal.name.insert("Rex")
          rex = tx.eid

          // Save Ann with weighed relationship to existing Rex
          _ <- Person.name("Ann").Favorite.weight(7).animal(rex).save

          // Ann and Rex each others favorite with a weight of 7
          _ <- favoriteAnimalOf("Ann").get === List((7, "Rex"))
        } yield ()
      }
    }


    "base + edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          tx <- Favorite.weight(7).Animal.name("Rex").save
          favoriteRex = tx.eid

          _ <- Person.name("Ann").favorite(favoriteRex).save

          _ <- favoriteAnimalOf("Ann").get === List((7, "Rex"))
          _ <- favoritePersonOf("Rex").get === List((7, "Ann"))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {

          tx <- Animal.name.insert("Rex")
          rex = tx.eid

          tx2 <- Favorite.weight(7).animal(rex).save
          favoriteRex = tx2.eid

          _ <- Person.name("Ann").favorite(favoriteRex).save

          _ <- favoriteAnimalOf("Ann").get === List((7, "Rex"))
          _ <- favoritePersonOf("Rex").get === List((7, "Ann"))
        } yield ()
      }
    }
  }
}