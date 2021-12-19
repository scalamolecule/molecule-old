package moleculeTests.tests.core.bidirectionals.edgeOther

import molecule.datomic.api.in1_out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import molecule.core.util.Executor._

object EdgeOneOtherSave extends AsyncTestSuite {

  val favoriteAnimalOf = m(Person.name_(?).Favorite.weight.Animal.name)
  val favoritePersonOf = m(Animal.name_(?).Favorite.weight.Person.name)

  lazy val tests = Tests {

    "base/edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          _ <- Person.name("Ann").Favorite.weight(7).Animal.name("Rex").save

          // Bidirectional property edges have been saved
          _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((7, "Rex")))
          _ <- favoritePersonOf("Rex").get.map(_ ==> List((7, "Ann")))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          rex <- Animal.name.insert("Rex").map(_.eid)

          // Save Ann with weighed relationship to existing Rex
          _ <- Person.name("Ann").Favorite.weight(7).animal(rex).save

          // Ann and Rex each others favorite with a weight of 7
          _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((7, "Rex")))
        } yield ()
      }
    }


    "base + edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          favoriteRex <- Favorite.weight(7).Animal.name("Rex").save.map(_.eid)

          _ <- Person.name("Ann").favorite(favoriteRex).save

          _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((7, "Rex")))
          _ <- favoritePersonOf("Rex").get.map(_ ==> List((7, "Ann")))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          rex <- Animal.name("Rex").save.map(_.eid)
          favoriteRex <- Favorite.weight(7).animal(rex).save.map(_.eid)

          _ <- Person.name("Ann").favorite(favoriteRex).save

          _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((7, "Rex")))
          _ <- favoritePersonOf("Rex").get.map(_ ==> List((7, "Ann")))
        } yield ()
      }
    }
  }
}