//package molecule.examples.dayOfDatomic.tutorial
//
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule.examples.dayOfDatomic.samples._
//
//class AttributeGroups extends DayOfAtomicSpec with Generators {
//
//  "Attribute groups" >> {
//    implicit val conn = init("Attribute groups", "social-news.edn")
//
//    // Find all attributes in the story namespace
//    m(e.valueType.ident.ns("story")).get(e)
//
//    // Create a reusable rule
//    val rules = m(e.valueType.ident.ns(?))
//
//    // Find all attributes in story namespace, using the rule
//    rules("story").get(a)
//    m(a.rules("story"))
//
//    // Find all entities possessing *any* story attribute
//    m(e.a(rules("story")))
//  }
//}