package molecule.examples.dayOfDatomic
import molecule.api.in1_out3._
import molecule.util.MoleculeSpec


class AttributeGroups extends MoleculeSpec {

  "Attribute groups" in new SocialNewsSetup {

    // Find all attributes in the story namespace
    // todo
//    Schema.ns_("story").a.get === List(":story/title", ":story/url")
//    Schema.a.ns_("story").get === List(":story/title", ":story/url")

//    // Create a reusable rule
//    val attrInNs = m(Schema.a.ns_(?))
//
//    // Find all attributes in story namespace, using the rule
//    attrInNs("story").get === List(":story/title", ":story/url")

    // Find all entities possessing *any* story attribute (the 3 stories)
//    Schema.e.ns_("story").get.sorted === List(s1, s2, s3)

    ok
  }
}