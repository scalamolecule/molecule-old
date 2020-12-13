package molecule.examples.dayOfDatomic

import molecule.examples.ExampleSpec
import molecule.datomic.api.in2_out3._
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic.schema._
import molecule.datomic.peer.facade.Datomic_Peer._


class Binding extends ExampleSpec {

//  implicit val conn = recreateDbFrom(SocialNewsSchema, "Binding")

  class Setup extends SocialNewsSetup {
    // Input molecules returning only the entity id (`e`).
    // (Underscore-suffixed attribute names are not returned in the result set)
    val personFirst = m(User.e.firstName_(?))
    val person      = m(User.e.firstName_(?).lastName_(?))

    // Get inserted entity ids
    val List(stewartBrand, johnStewart, stuartSmalley, stuartHalloway) = User.firstName.lastName insert List(
      ("Stewart", "Brand"),
      ("John", "Stewart"),
      ("Stuart", "Smalley"),
      ("Stuart", "Halloway")
    ) eids
  }


  "Binding queries" in new Setup {

    // Find all the Stewart first names
    personFirst("Stewart").get === List(stewartBrand)

    // Find all the Stewart or Stuart first names
    personFirst("Stewart" or "Stuart").get.sorted ===
      List(stewartBrand, stuartSmalley, stuartHalloway).sorted

    personFirst("Stewart", "Stuart").get.sorted ===
      List(stewartBrand, stuartSmalley, stuartHalloway).sorted

    // Find all the Stewart/Stuart as either first name or last name
    User.e.a.v_("Stewart").get.sortBy(_._2) === List(
      (stewartBrand, ":User/firstName"),
      (johnStewart, ":User/lastName"),
    )

    // Find only the Smalley Stuarts
    person("Stuart", "Smalley").get === List(stuartSmalley)
  }


  "Binding (continued..)" in new Setup {

    // Bind vars
    person("John", "Stewart").get === List(johnStewart)

    // Bind tuple
    person(("John", "Stewart")).get === List(johnStewart)

    // Bind collection
    personFirst(List("John", "Stuart")).get === List(johnStewart, stuartSmalley, stuartHalloway)

    // Bind relation
    person(("John", "Stewart"), ("Stuart", "Halloway")).get === List(johnStewart, stuartHalloway)
    person(("John", "Stewart"), ("Stuart", "Hallowey")).get === List(johnStewart)
  }
}