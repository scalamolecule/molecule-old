package moleculeTests.tests.core.bidirectionals.edgeOther

import molecule.datomic.api.in1_out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeOneOtherUpdate extends AsyncTestSuite {

  val favoriteAnimalOf = m(Person.name_(?).Favorite.weight.Animal.name)
  val favoritePersonOf = m(Animal.name_(?).Favorite.weight.Person.name)

  def getAnn(implicit conn: Future[Conn], ec: ExecutionContext): Future[Long] = {
    Person.name("Ann").save.map(_.eid)
  }


  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "apply edge to new target" - bidirectional { implicit conn =>
      for {
        ann <- getAnn

        // New edge and new target entity
        _ <- Person(ann).Favorite.weight(5).Animal.name("Rex").update

        // Ann and Rex favorite each other
        _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((5, "Rex")))
        _ <- favoritePersonOf("Rex").get.map(_ ==> List((5, "Ann")))
        _ <- favoritePersonOf("Zup").get.map(_ ==> List()) // Zup doesn't exist yet

        // Ann now favorite Zup
        _ <- Person(ann).Favorite.weight(8).Animal.name("Zup").update

        // Both bidirectional edges have been added from/to Ann
        _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((8, "Zup")))
        _ <- favoritePersonOf("Rex").get.map(_ ==> List())
        _ <- favoritePersonOf("Zup").get.map(_ ==> List((8, "Ann")))

        // Even though Ann now favorite Zup, Rex still exists
        _ <- Person.name.get.map(_ ==> List("Ann"))
        _ <- Animal.name.a1.get.map(_ ==> List("Rex", "Zup"))
      } yield ()
    }

    "apply edge to existing target" - bidirectional { implicit conn =>
      for {
        ann <- getAnn
        List(rex, zup) <- Animal.name.insert("Rex", "Zup").map(_.eids)

        _ <- Person(ann).Favorite.weight(5).animal(rex).update

        _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((5, "Rex")))
        _ <- favoritePersonOf("Rex").get.map(_ ==> List((5, "Ann")))
        _ <- favoritePersonOf("Zup").get.map(_ ==> List())

        // Ann now favorite Zup
        _ <- Person(ann).Favorite.weight(8).animal(zup).update

        _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((8, "Zup")))
        _ <- favoritePersonOf("Rex").get.map(_ ==> List())
        _ <- favoritePersonOf("Zup").get.map(_ ==> List((8, "Ann")))
      } yield ()
    }

    "retract edge" - bidirectional { implicit conn =>
      for {
        List(ann, annRex, rexAnn, rex) <- Person.name("Ann").Favorite.weight(5).Animal.name("Rex").save.map(_.eids)

        _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((5, "Rex")))
        _ <- favoritePersonOf("Rex").get.map(_ ==> List((5, "Ann")))

        // Retract edge
        _ <- annRex.retract

        // Divorce complete

        _ <- Person.name.Favorite.weight.Animal.name.get.map(_ ==> Nil)
      } yield ()
    }

    "retract base/target entity" - bidirectional { implicit conn =>
      for {
        List(ann, annRex, rexAnn, rex) <- Person.name("Ann").Favorite.weight(5).Animal.name("Rex").save.map(_.eids)

        _ <- favoriteAnimalOf("Ann").get.map(_ ==> List((5, "Rex")))
        _ <- favoritePersonOf("Rex").get.map(_ ==> List((5, "Ann")))

        // Retract base entity with single edge
        _ <- rex.retract

        // Ann becomes widow
        _ <- Person.name("Ann").get.map(_ ==> List("Ann"))
        _ <- Animal.name("Rex").get.map(_ ==> List())

        _ <- favoriteAnimalOf("Ann").get.map(_ ==> List())
        _ <- favoritePersonOf("Rex").get.map(_ ==> List())
      } yield ()
    }
  }
}
