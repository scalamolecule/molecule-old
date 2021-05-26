package moleculeTests.tests.core.bidirectionals.edgeOther

import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.datomic.api.in1_out4._
import molecule.core.ops.exception.VerifyModelException
import moleculeTests.setup.AsyncTestSuite
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
          _ <- favoriteAnimalOf("Ann").get === List((7, "Rex"))
          _ <- favoritePersonOf("Rex").get === List((7, "Ann"))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx <- Person.name.insert("Ann")
          ann = tx.eid

          // We can insert from the other end as well
          _ <- Animal.name.Favorite.weight.person.insert("Rex", 7, ann)

          // Bidirectional property edge has been inserted
          _ <- favoriteAnimalOf("Ann").get === List((7, "Rex"))
          _ <- favoritePersonOf("Rex").get === List((7, "Ann"))
        } yield ()
      }
    }


    "base + edge/target" - {

      "new target" - bidirectional { implicit conn =>
        for {
          // Create edges to/from Rex
          tx <- Favorite.weight.Animal.name.insert(7, "Rex")
          favoriteRex = tx.eid

          // Base entity Ann points to one of the edges (doesn't matter which of them - Molecule connects Ann to both)

          _ <- Person.name.favorite.insert("Ann", favoriteRex)

          // Ann loves Rex and Rex loves Ann - that is 70% love
          _ <- favoriteAnimalOf("Ann").get === List((7, "Rex"))
          _ <- favoritePersonOf("Rex").get === List((7, "Ann"))
        } yield ()
      }

      "existing target" - bidirectional { implicit conn =>
        for {
          tx <- Animal.name.insert("Rex")
          rex = tx.eid

          // Create edges to existing Rex
          tx2 <- Favorite.weight.animal.insert(7, rex)
          List(favoriteRex, rexFavorite) = tx2.eids

          // Base entity Ann points to one of the edges (doesn't matter which of them - Molecule connects Ann to both)
          _ <- Person.name.favorite.insert("Ann", rexFavorite)

          // Ann loves Rex and Rex loves Ann - that is 70% love
          _ <- favoriteAnimalOf("Ann").get === List((7, "Rex"))
          _ <- favoritePersonOf("Rex").get === List((7, "Ann"))

        } yield ()
      }
    }


    "base/edge - <missing target>" - bidirectional { implicit conn =>
      //      for {
      //    // Can't allow edge without ref to target entity
      //    (Person.name.Favorite.weight.insert must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[edgeComplete]  Missing target namespace after edge namespace `Favorite`."
      //    } yield ()
    }

    "<missing base> - edge - <missing target>" - bidirectional { implicit conn =>
      //      for {
      //    // Edge always have to have a ref to a target entity
      //    (Favorite.weight.insert must throwA[VerifyModelException])
      //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
      //      s"[edgeComplete]  Missing target namespace somewhere after edge property `Favorite/weight`."
      //  } yield ()
    }
  }
}
