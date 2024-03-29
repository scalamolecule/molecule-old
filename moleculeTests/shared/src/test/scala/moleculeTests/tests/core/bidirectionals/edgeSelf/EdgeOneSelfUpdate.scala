package moleculeTests.tests.core.bidirectionals.edgeSelf

import molecule.datomic.api.in1_out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeOneSelfUpdate extends AsyncTestSuite {

  val loveOf = m(Person.name_(?).Loves.weight.Person.name)

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "apply edge to new target" - bidirectional { implicit conn =>
      for {
        ann <- Person.name("Ann").save.map(_.eid)

        // New edge and new target entity
        _ <- Person(ann).Loves.weight(5).Person.name("Ben").update

        // Ann and Ben loves each other
        _ <- loveOf("Ann").get.map(_ ==> List((5, "Ben")))
        _ <- loveOf("Ben").get.map(_ ==> List((5, "Ann")))
        _ <- loveOf("Joe").get.map(_ ==> List()) // Joe doesn't exist yet

        // Ann now loves Joe
        _ <- Person(ann).Loves.weight(8).Person.name("Joe").update

        // Both bidirectional edges have been added from/to Ann
        _ <- loveOf("Ann").get.map(_ ==> List((8, "Joe")))
        _ <- loveOf("Ben").get.map(_ ==> List())
        _ <- loveOf("Joe").get.map(_ ==> List((8, "Ann")))

        // Even though Ann now loves Joe, Ben still exists
        _ <- Person.name.a1.get.map(_ ==> List("Ann", "Ben", "Joe"))
      } yield ()
    }


    "apply edge to existing target" - bidirectional { implicit conn =>
      for {
        ann <- Person.name("Ann").save.map(_.eid)
        List(ben, joe) <- Person.name.insert("Ben", "Joe").map(_.eids)

        _ <- Person(ann).Loves.weight(5).person(ben).update

        _ <- loveOf("Ann").get.map(_ ==> List((5, "Ben")))
        _ <- loveOf("Ben").get.map(_ ==> List((5, "Ann")))
        _ <- loveOf("Joe").get.map(_ ==> List())

        // Ann now loves Joe
        _ <- Person(ann).Loves.weight(8).person(joe).update

        _ <- loveOf("Ann").get.map(_ ==> List((8, "Joe")))
        _ <- loveOf("Ben").get.map(_ ==> List())
        _ <- loveOf("Joe").get.map(_ ==> List((8, "Ann")))
      } yield ()
    }


    "retract edge" - bidirectional { implicit conn =>
      for {
        _ <- Person.name("Ann").save.map(_.eid)
        List(_, annBen, _, _) <- Person.name("Ann").Loves.weight(5).Person.name("Ben").save.map(_.eids)

        _ <- loveOf("Ann").get.map(_ ==> List((5, "Ben")))
        _ <- loveOf("Ben").get.map(_ ==> List((5, "Ann")))

        // Retract edge
        _ <- annBen.retract

        // Divorce complete
        _ <- loveOf("Ann").get.map(_ ==> List())
        _ <- loveOf("Ben").get.map(_ ==> List())
      } yield ()
    }


    "retract base/target entity" - bidirectional { implicit conn =>
      for {
        _ <- Person.name("Ann").save.map(_.eid)

        List(_, _, _, ben) <- Person.name("Ann").Loves.weight(5).Person.name("Ben").save.map(_.eids)

        _ <- loveOf("Ann").get.map(_ ==> List((5, "Ben")))
        _ <- loveOf("Ben").get.map(_ ==> List((5, "Ann")))

        // Retract base entity with single edge
        _ <- ben.retract

        // Ann becomes widow
        _ <- Person.name("Ann").get.map(_ ==> List("Ann"))
        _ <- Person.name("Ben").get.map(_ ==> List())

        _ <- loveOf("Ann").get.map(_ ==> List())
        _ <- loveOf("Ben").get.map(_ ==> List())
      } yield ()
    }
  }
}
