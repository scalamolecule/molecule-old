package moleculeTests.tests.core.bidirectionals.edgeSelf

import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.datomic.api.in1_out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeOneSelfUpdate extends AsyncTestSuite {

  val loveOf = m(Person.name_(?).Loves.weight.Person.name)

  def getAnn(implicit conn: Future[Conn], ec: ExecutionContext): Future[Long] = {
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
        _ <- Person(ann).Loves.weight(5).Person.name("Ben").update

        // Ann and Ben loves each other
        _ <- loveOf("Ann").get === List((5, "Ben"))
        _ <- loveOf("Ben").get === List((5, "Ann"))
        _ <- loveOf("Joe").get === List() // Joe doesn't exist yet

        // Ann now loves Joe
        _ <- Person(ann).Loves.weight(8).Person.name("Joe").update

        // Both bidirectional edges have been added from/to Ann
        _ <- loveOf("Ann").get === List((8, "Joe"))
        _ <- loveOf("Ben").get === List()
        _ <- loveOf("Joe").get === List((8, "Ann"))

        // Even though Ann now loves Joe, Ben still exists
        _ <- Person.name.get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
      } yield ()
    }


    "apply edge to existing target" - bidirectional { implicit conn =>
      for {
        ann <- getAnn

        tx <- Person.name.insert("Ben", "Joe")
        List(ben, joe) = tx.eids

        _ <- Person(ann).Loves.weight(5).person(ben).update

        _ <- loveOf("Ann").get === List((5, "Ben"))
        _ <- loveOf("Ben").get === List((5, "Ann"))
        _ <- loveOf("Joe").get === List()

        // Ann now loves Joe
        _ <- Person(ann).Loves.weight(8).person(joe).update

        _ <- loveOf("Ann").get === List((8, "Joe"))
        _ <- loveOf("Ben").get === List()
        _ <- loveOf("Joe").get === List((8, "Ann"))
      } yield ()
    }


    "retract edge" - bidirectional { implicit conn =>
      for {
        ann <- getAnn

        tx <- Person.name("Ann").Loves.weight(5).Person.name("Ben").save
        List(_, annBen, _, _) = tx.eids

        _ <- loveOf("Ann").get === List((5, "Ben"))
        _ <- loveOf("Ben").get === List((5, "Ann"))

        // Retract edge
        _ <- annBen.map(_.retract)

        // Divorce complete
        _ <- loveOf("Ann").get === List()
        _ <- loveOf("Ben").get === List()
      } yield ()
    }


    "retract base/target entity" - bidirectional { implicit conn =>
      for {
        ann <- getAnn

        tx <- Person.name("Ann").Loves.weight(5).Person.name("Ben").save
        List(_, _, _, ben) = tx.eids

        _ <- loveOf("Ann").get === List((5, "Ben"))
        _ <- loveOf("Ben").get === List((5, "Ann"))

        // Retract base entity with single edge
        _ <- ben.map(_.retract)

        // Ann becomes widow
        _ <- Person.name("Ann").get === List("Ann")
        _ <- Person.name("Ben").get === List()

        _ <- loveOf("Ann").get === List()
        _ <- loveOf("Ben").get === List()
      } yield ()
    }
  }
}
