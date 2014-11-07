//package molecule
//package examples.dayOfDatomic.tutorial
//
//import molecule._
//import molecule.examples.dayOfDatomic.schema.SocialNewsSchema
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule.examples.dayOfDatomic.samples._
//
//class AttributeGroups extends DayOfAtomicSpec {
//
//  "Attribute groups" >> {
//    import molecule.schemas.Db
//    implicit val conn = load(SocialNewsSchema.tx, "Attribute groups")
//
//    // Find all attributes in the story namespace
//    Db.a.ns_("story").get === List("title", "url", "comments")
//
//    // Create a reusable rule
//    val attrInNs = m(Db.a.ns_(?))
//
//    // Find all attributes in story namespace, using the rule
//    attrInNs("story").get === List("title", "url", "comments")
//
//    // Find all entities possessing *any* story attribute
//    Db.e.a_.ns_("story").get === List()
//  }
//
////  "Attribute groups" >> {
////    implicit val conn = init("Attribute groups", "social-news.edn")
////
////    // Find all attributes in the story namespace
////    m(e.valueType.ident.ns("story")).get(e)
////
////    // Create a reusable rule
////    val rules = m(e.valueType.ident.ns(?))
////
////    // Find all attributes in story namespace, using the rule
////    rules("story").get(a)
////    m(a.rules("story"))
////
////    // Find all entities possessing *any* story attribute
////    m(e.a(rules("story")))
////  }
//}