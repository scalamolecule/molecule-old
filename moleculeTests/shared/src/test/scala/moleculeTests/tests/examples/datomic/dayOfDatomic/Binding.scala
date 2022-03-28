package moleculeTests.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.in2_out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.SocialNews._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Binding extends AsyncTestSuite {

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      tx <- User.firstName.lastName insert List(
        ("Stewart", "Brand"),
        ("John", "Stewart"),
        ("Stuart", "Smalley"),
        ("Stuart", "Halloway"))
    } yield tx.eids
  }

  // Input molecules returning only the entity id (`e`).
  // (Underscore-suffixed attribute names are not returned in the result set)
  val personFirst = m(User.e.a1.firstName_(?))
  val person      = m(User.e.a1.firstName_(?).lastName_(?))

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Binding queries" - socialNews { implicit conn =>
      for {
        List(stewartBrand, johnStewart, stuartSmalley, stuartHalloway) <- data

        // Find all the Stewart first names
        _ <- personFirst("Stewart").get.map(_ ==> List(stewartBrand))

        // Find all the Stewart or Stuart first names
        _ <- personFirst("Stewart" or "Stuart").get.map(_ ==>
          List(stewartBrand, stuartSmalley, stuartHalloway).sorted)

        _ <- personFirst("Stewart", "Stuart").get.map(_ ==>
          List(stewartBrand, stuartSmalley, stuartHalloway).sorted)

        // Find all the Stewart/Stuart as either first name or last name
        _ <- User.e.a.v_("Stewart").get.map(_.sortBy(_._2) ==> List(
          (stewartBrand, ":User/firstName"),
          (johnStewart, ":User/lastName"),
        ))

        // Find only the Smalley Stuarts
        _ <- person("Stuart", "Smalley").get.map(_ ==> List(stuartSmalley))
      } yield ()
    }


    "Binding (continued..)" - socialNews { implicit conn =>
      for {
        List(stewartBrand, johnStewart, stuartSmalley, stuartHalloway) <- data

        // Bind vars
        _ <- person("John", "Stewart").get.map(_ ==> List(johnStewart))

        // Bind tuple
        _ <- person(("John", "Stewart")).get.map(_ ==> List(johnStewart))

        // Bind collection
        _ <- personFirst(List("John", "Stuart")).get.map(_ ==>
          List(johnStewart, stuartSmalley, stuartHalloway).sorted)

        // Bind relation
        _ <- person(("John", "Stewart"), ("Stuart", "Halloway")).get.map(_ ==>
          List(johnStewart, stuartHalloway).sorted)

        _ <- person(("John", "Stewart"), ("Stuart", "Hallowey")).get.map(_ ==> List(johnStewart))
      } yield ()
    }
  }
}