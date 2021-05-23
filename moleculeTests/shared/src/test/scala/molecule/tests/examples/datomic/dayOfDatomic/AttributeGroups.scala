package molecule.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.out2._
import molecule.datomic.peer.facade.Datomic_Peer._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object AttributeGroups extends AsyncTestSuite {

  lazy val tests = Tests {

  "Attribute groups" - socialNews { implicit conn =>

    // Find all attributes in the story namespace
    // todo
//    Schema.ns_("story").a.get === List(":Story/title", ":Story/url")
//    Schema.a.ns_("story").get === List(":Story/title", ":Story/url")

//    // Create a reusable rule
//    val attrInNs = m(Schema.a.ns_(?))
//
//    // Find all attributes in story namespace, using the rule
//    attrInNs("story").get === List(":Story/title", ":Story/url")

    // Find all entities possessing *any* story attribute (the 3 stories)
//    Schema.e.ns_("Story").get.map(_.sorted ==> List(s1, s2, s3))

  }
  }
}