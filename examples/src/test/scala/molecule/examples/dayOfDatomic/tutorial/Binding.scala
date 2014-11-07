//package molecule
//package examples.dayOfDatomic.tutorial
//import molecule.examples.dayOfDatomic.schema._
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//
//class Binding extends DayOfAtomicSpec {
//
//  "Binding" >> {
//      import molecule.examples.dayOfDatomic.dsl.socialNews._
//      implicit val conn = load(SocialNewsSchema.tx, "Binding")
//
//      // Input molecules
//      val person = m(User.firstName(?).lastName(?))
//      val firstN = m(User.firstName(?))
//
//      // Bind vars
//      person("John", "Doe").get(1) ===("John", "Doe")
//
//      // Bind tuple
//      person(("John", "Doe")).get(1) ===("John", "Doe")
//
//      // Bind collection
//      firstN(List("John", "Jane", "Phineas")).get === List("John", "Jane", "Phineas")
//
//      // Bind relation
//      person(List(("John", "Doe"), ("Jane", "Doe"))).get === List(("John", "Doe"), ("Jane", "Doe"))
//
//
//      // Binding queries
//
//      // Find all the Stewart first names
//      firstN("Stewart") === List()
//
//      // Find all the Stewart or Stuart first names
//      firstN("Stewart", "Stuart") === List()
//
//      // Find all the Stewart/Stuart as either first name or last name
//      person(("Stewart", "Stuart"), ("Stewart", "Stuart")) === List()
//
//      // Find only the Smalley Stuarts
//      person("Stewart", "Stuart") === List()
//
//      // Same query above, but with map (tuple) form
//      person(("Stewart", "Stuart")) === List()
//    }
//}