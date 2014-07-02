//package molecule
//package examples.dayOfDatomic.tutorial
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule.examples.dayOfDatomic.SocialNewsSchema.User
//
//class Binding extends DayOfAtomicSpec {
//
//  "Binding" >> {
//    implicit val conn = init("binding")
//
//    // Bind vars
//    m(User.firstName(?).lastName(?))("John", "Doe").get === List("John", "Doe")
//
//    // Bind tuples
//    m(User.firstName(?).lastName(?))(("John", "Doe")).get === ("John", "Doe")
//
//    // Bind a collection
//    m(User.firstName(?))(List("John", "Jane", "Phineas")).get === List("John", "Jane", "Phineas")
//
//    // Bind
//    m(User.firstName(?))(List(("John", "Doe"), ("Jane", "Doe"))).get === ("John", "Doe")
//  }
//}