//package molecule.examples.dayOfDatomic
//
//import molecule.ast.definition._
//
//
//  trait Story {
//    val title = oneString.fullTextSearch. ~ indexed
//    val url   = string ~ uniqueIdentity
//    val slug  = string
//  }
//
//  val comments = {
//    trait Comment {
//      val body   = string
//      val Author = One[User]
//    }
//  }
//
//  trait User {
//    val firstName    = string ~ indexed
//    val lastName     = string ~ indexed
//    val email        = string ~ indexed ~ uniqueIdentity
//    val passwordHash = string
//    val UpVotes      = Many[Story]
//  }
//
//  trait Publish {
//    val at = instant ~ indexed
//  }
//
//  trait Tx {
//    val user = oneRef
//  }
//}