package molecule.tests.core.bidirectionals.edgeOther

import molecule.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.datomic.api.in1_out3._
import molecule.datomic.base.facade.Conn
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeOneOtherUpdate extends AsyncTestSuite {

  val favoriteAnimalOf = m(Person.name_(?).Favorite.weight.Animal.name)
  val favoritePersonOf = m(Animal.name_(?).Favorite.weight.Person.name)

  def getAnn(implicit conn: Conn, ec: ExecutionContext): Future[Long] = {
    for {
      tx <- Person.name("Ann").save
    } yield {
      tx.eid
    }
  }


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "apply edge to new target" - bidirectional { implicit conn =>
      for {
        ann <- getAnn

        // New edge and new target entity
        _ <- Person(ann).Favorite.weight(5).Animal.name("Rex").update

        // Ann and Rex favorite each other
        _ <- favoriteAnimalOf("Ann").get === List((5, "Rex"))
        _ <- favoritePersonOf("Rex").get === List((5, "Ann"))
        _ <- favoritePersonOf("Zup").get === List() // Zup doesn't exist yet

        // Ann now favorite Zup
        _ <- Person(ann).Favorite.weight(8).Animal.name("Zup").update

        // Both bidirectional edges have been added from/to Ann
        _ <- favoriteAnimalOf("Ann").get === List((8, "Zup"))
        _ <- favoritePersonOf("Rex").get === List()
        _ <- favoritePersonOf("Zup").get === List((8, "Ann"))

        // Even though Ann now favorite Zup, Rex still exists
        _ <- Person.name.get.map(_.sorted ==> List("Ann"))
        _ <- Animal.name.get.map(_.sorted ==> List("Rex", "Zup"))
      } yield ()
    }

    "apply edge to existing target" - bidirectional { implicit conn =>
      for {
        ann <- getAnn

        tx <- Animal.name.insert("Rex", "Zup")
        List(rex, zup) = tx.eids

        _ <- Person(ann).Favorite.weight(5).animal(rex).update

        _ <- favoriteAnimalOf("Ann").get === List((5, "Rex"))
        _ <- favoritePersonOf("Rex").get === List((5, "Ann"))
        _ <- favoritePersonOf("Zup").get === List()

        // Ann now favorite Zup
        _ <- Person(ann).Favorite.weight(8).animal(zup).update

        _ <- favoriteAnimalOf("Ann").get === List((8, "Zup"))
        _ <- favoritePersonOf("Rex").get === List()
        _ <- favoritePersonOf("Zup").get === List((8, "Ann"))
      } yield ()
    }

    "retract edge" - bidirectional { implicit conn =>
      for {
        ann <- getAnn

        tx <- Person.name("Ann").Favorite.weight(5).Animal.name("Rex").save
        List(_, annRex, _, _) = tx.eids

        _ <- favoriteAnimalOf("Ann").get === List((5, "Rex"))
        _ <- favoritePersonOf("Rex").get === List((5, "Ann"))

        // Retract edge
        _ <- annRex.retract

        // Divorce complete

        _ <- Person.name.Favorite.weight.Animal.name.get.map(_.sorted ==> List())
      } yield ()
    }

    "retract base/target entity" - bidirectional { implicit conn =>
      for {
        ann <- getAnn

        tx <- Person.name("Ann").Favorite.weight(5).Animal.name("Rex").save
        List(_, _, _, rex) = tx.eids

        _ <- favoriteAnimalOf("Ann").get === List((5, "Rex"))
        _ <- favoritePersonOf("Rex").get === List((5, "Ann"))

        // Retract base entity with single edge
        _ <- rex.retract

        // Ann becomes widow
        _ <- Person.name("Ann").get === List("Ann")
        _ <- Animal.name("Rex").get === List()

        _ <- favoriteAnimalOf("Ann").get === List()
        _ <- favoritePersonOf("Rex").get === List()
      } yield ()
    }
  }
}
