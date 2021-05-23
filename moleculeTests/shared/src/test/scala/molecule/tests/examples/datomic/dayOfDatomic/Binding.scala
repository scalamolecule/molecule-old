package molecule.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.in2_out3._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.tests.examples.datomic.dayOfDatomic.dsl.SocialNews._
import molecule.datomic.peer.facade.Datomic_Peer._
import molecule.setup.AsyncTestSuite
import molecule.tests.core.base.dsl.CoreTest.Ns
import molecule.tests.core.input1.expression.Input1Int.{int1, int2, int3, str1, str2, str3, str4}
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Binding extends AsyncTestSuite {

  def data(implicit conn: Conn, ec: ExecutionContext) = {
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
  val personFirst = m(User.e.firstName_(?))
  val person      = m(User.e.firstName_(?).lastName_(?))

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Binding queries" - socialNews { implicit conn =>
      for {
        List(stewartBrand, johnStewart, stuartSmalley, stuartHalloway) <- data

        // Find all the Stewart first names
        _ <- personFirst("Stewart").get === List(stewartBrand)

        // Find all the Stewart or Stuart first names
        _ <- personFirst("Stewart" or "Stuart").get.map(_.sorted ==>
          List(stewartBrand, stuartSmalley, stuartHalloway).sorted)

        _ <- personFirst("Stewart", "Stuart").get.map(_.sorted ==>
          List(stewartBrand, stuartSmalley, stuartHalloway).sorted)

        // Find all the Stewart/Stuart as either first name or last name
        _ <- User.e.a.v_("Stewart").get.map(_.sortBy(_._2) ==> List(
          (stewartBrand, ":User/firstName"),
          (johnStewart, ":User/lastName"),
        ))

        // Find only the Smalley Stuarts
        _ <- person("Stuart", "Smalley").get === List(stuartSmalley)
      } yield ()
    }


    "Binding (continued..)" - socialNews { implicit conn =>
      for {
        List(stewartBrand, johnStewart, stuartSmalley, stuartHalloway) <- data

        // Bind vars
        _ <- person("John", "Stewart").get === List(johnStewart)

        // Bind tuple
        _ <- person(("John", "Stewart")).get === List(johnStewart)

        // Bind collection
        _ <- personFirst(List("John", "Stuart")).get.map(_.sorted ==>
          List(johnStewart, stuartSmalley, stuartHalloway).sorted)

        // Bind relation
        _ <- person(("John", "Stewart"), ("Stuart", "Halloway")).get.map(_.sorted ==>
          List(johnStewart, stuartHalloway).sorted)

        _ <- person(("John", "Stewart"), ("Stuart", "Hallowey")).get === List(johnStewart)
      } yield ()
    }
  }
}