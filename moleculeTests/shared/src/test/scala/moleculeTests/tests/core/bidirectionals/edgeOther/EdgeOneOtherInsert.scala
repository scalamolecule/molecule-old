package moleculeTests.tests.core.bidirectionals.edgeOther

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EdgeOneOtherInsert extends AsyncTestSuite {

  val favoriteAnimalOf = m(Person.name_(?).Favorite.weight.Animal.name)
  val favoritePersonOf = m(Animal.name_(?).Favorite.weight.Person.name)

  lazy val tests = Tests {

    "base/edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          _ <- Person.name.Favorite.weight.Animal.name.insert("Ann", 7, "Rex")

          // Bidirectional property edge has been inserted
          // Note how we query differently from each end
          _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((7, "Rex")))
          _ <- favoritePersonOf("Rex").get.map(_ ==> List((7, "Ann")))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          ann <- Person.name.insert("Ann").map(_.eid)

          // We can insert from the other end as well
          _ <- Animal.name.Favorite.weight.person.insert("Rex", 7, ann)

          // Bidirectional property edge has been inserted
          _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((7, "Rex")))
          _ <- favoritePersonOf("Rex").get.map(_ ==> List((7, "Ann")))
        } yield ()
      }
    }


    "base + edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          // Create edges to/from Rex
          favoriteRex <- Favorite.weight.Animal.name.insert(7, "Rex").map(_.eid)

          // Base entity Ann points to one of the edges (doesn't matter which of them - Molecule connects Ann to both)

          _ <- Person.name.favorite.insert("Ann", favoriteRex)

          // Ann loves Rex and Rex loves Ann - that is 70% love
          _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((7, "Rex")))
          _ <- favoritePersonOf("Rex").get.map(_ ==> List((7, "Ann")))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          rex <- Animal.name.insert("Rex").map(_.eid)

          // Create edges to existing Rex
          List(favoriteRex, rexFavorite) <- Favorite.weight.animal.insert(7, rex).map(_.eids)

          // Base entity Ann points to one of the edges (doesn't matter which of them - Molecule connects Ann to both)
          _ <- Person.name.favorite.insert("Ann", rexFavorite)

          // Ann loves Rex and Rex loves Ann - that is 70% love
          _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((7, "Rex")))
          _ <- favoritePersonOf("Rex").get.map(_ ==> List((7, "Ann")))

        } yield ()
      }
    }


    "base/edge - <missing target>" - bidirectional { implicit conn =>
      // Can't allow edge without ref to target entity
      Person.name.Favorite.weight.insert("Don", 5)
        .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace after edge namespace `Favorite`."
      }
    }

    "<missing base> - edge - <missing target>" - bidirectional { implicit conn =>
      // Edge always have to have a ref to a target entity
      Favorite.weight.insert(5)
        .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
        err ==> s"[edgeComplete]  Missing target namespace somewhere after edge property `Favorite/weight`."
      }
    }
  }
}
