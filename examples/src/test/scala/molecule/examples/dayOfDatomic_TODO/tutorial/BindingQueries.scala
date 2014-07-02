//package molecule.examples.dayOfDatomic.tutorial
//
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule.examples.dayOfDatomic.samples._
//
//class BindingQueries extends DayOfAtomicSpec with Generators {
//
//  "Binding queries" >> {
//    implicit val conn = init("Attribute groups", "social-news.edn")
//
//    // Find all the Stewart first names
//    m(User.firstName(?))("Stewart")
//
//    // Find all the Stewart or Stuart first names
//    m(User.firstName(?))("Stewart", "Stuart")
//
//    // Find all the Stewart/Stuart as either first name or last name
//    m(User.firstName(?).lastName(?))(("Stewart", "Stuart"),("Stewart", "Stuart"))
//
//    // Find only the Smalley Stuarts
//    m(User.firstName(?).lastName(?))("Stewart", "Stuart")
//
//    // Same query above, but with map (tuple) form
//    m(User.firstName(?).lastName(?))(("Stewart", "Stuart"))
//  }
//}