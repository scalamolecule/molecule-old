package molecule.examples.dayOfDatomic.schema
import molecule.dsl.schemaDefinition._

// http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html



@InOut(2, 5)
trait SocialNewsDefinition {

  trait Story {
    val title    = oneString.fullTextSearch.indexed
    val url      = oneString.uniqueIdentity
  }

  trait Parent {
    val comment = one[Comment].subComponent
//    val comments = many[Comment].subComponents
  }

  trait Comment {
    val author  = one[User]
    val text    = oneString
  }

  trait User {
    val firstName    = oneString.indexed
    val lastName     = oneString.indexed
    val email        = oneString.uniqueIdentity
    val passwordHash = oneString
    val upVotes      = many[Story]
  }

  trait Publish {
    val at = oneDate.indexed
  }

  trait Source {
    val user = one[User]
  }
}

//@InOut(2, 5)
//trait SocialNewsDefinition {
//
//  trait Story {
//    val title    = oneString.fullTextSearch.indexed
//    val url      = oneString.uniqueIdentity
//    val comments = many[Comment].subComponents
//  }
//
//
////  trait Comment extends Tree {
//  trait Comment {
//    val comment = many[Comment].subComponents
//    val author  = one[User]
//    val text    = oneString
//  }
//
//  trait User {
//    val firstName    = oneString.indexed
//    val lastName     = oneString.indexed
//    val email        = oneString.uniqueIdentity
//    val passwordHash = oneString
//    val upVotes      = many[Story]
//  }
//
//  trait Publish {
//    val at = oneDate.indexed
//  }
//
//  trait Source {
//    val user = one[User]
//  }
//}


//@InOut(2, 5)
//trait SocialNewsDefinition {
//
//  trait Story {
//    val title = oneString.fullTextSearch.indexed
//    val url   = oneString.uniqueIdentity
//    //        val comments = many[Comment].subComponents
//  }
//
//    trait Parent  {
////      val story   = many[Story]._subComponents
//      val comment = many[Comment].subComponents
//    }
//
//  trait Comment {
////    val parent = _many[Comment, Story]
////    val parent = _many[Comment]._many[Story].subComponents
//    val author = one[User]
//    val text   = oneString
//  }
//
//  trait User {
//    val firstName    = oneString.indexed
//    val lastName     = oneString.indexed
//    val email        = oneString.uniqueIdentity
//    val passwordHash = oneString
//    val upVotes      = many[Story]
//  }
//
//  trait Publish {
//    val at = oneDate.indexed
//  }
//
//  trait Source {
//    val user = one[User]
//  }
//}
//
//@InOut(2, 5)
//trait SocialNewsDefinition {
//
//  trait Story {
//    val title    = oneString.fullTextSearch.indexed
//    val url      = oneString.uniqueIdentity
//    val comments = many[Comment].subComponents
//  }
//
//  trait Comment extends Tree {
//    val author = one[User]
//    val text   = oneString
//  }
//
//  trait User {
//    val firstName    = oneString.indexed
//    val lastName     = oneString.indexed
//    val email        = oneString.uniqueIdentity
//    val passwordHash = oneString
//    val upVotes      = many[Story]
//  }
//
//  trait Publish {
//    val at = oneDate.indexed
//  }
//
//  trait Source {
//    val user = one[User]
//  }
//}


//@InOut(3, 8)
//trait SocialNewsDefinition {
//
//  trait Story {
//    val title    = oneString.fullTextSearch.indexed
//    val url      = oneString.uniqueIdentity
//    val comments = many[Comment].trees
//  }
//
//  trait Comment {
//    val author = one[User]
//    val text   = oneString
//  }
//
//  trait User {
//    val firstName    = oneString.indexed
//    val lastName     = oneString.indexed
//    val email        = oneString.uniqueIdentity
//    val passwordHash = oneString
//    val upVotes      = many[Story]
//  }
//
//  trait Publish {
//    val at = oneDate.indexed
//  }
//}
//
//@InOut(3, 8)
//trait SocialNewsDefinition {
//
//  trait Story {
//    val title    = oneString.fullTextSearch.indexed
//    val url      = oneString.uniqueIdentity
//    val comments = many[Comment].subComponents
//  }
//
//  trait Comment {
//    val parent = one[Comment].subComponent
//    val author = one[User]
//    val text   = oneString
//  }
//
//  trait User {
//    val firstName    = oneString.indexed
//    val lastName     = oneString.indexed
//    val email        = oneString.uniqueIdentity
//    val passwordHash = oneString
//    val upVotes      = many[Story]
//  }
//
//  trait Publish {
//    val at = oneDate.indexed
//  }
//}

//@InOut(3, 8)
//trait SocialNewsDefinition {
//
//  trait Story {
//    val title    = oneString.fullTextSearch.indexed
//    val url      = oneString.uniqueIdentity
//    val comments = many[Comment].subComponents
//  }
//
//  trait Comment extends Tree {
//    val author = one[User]
//    val text   = oneString
//  }
//
//  trait User {
//    val firstName    = oneString.indexed
//    val lastName     = oneString.indexed
//    val email        = oneString.uniqueIdentity
//    val passwordHash = oneString
//    val upVotes      = many[Story]
//  }
//
//  trait Publish {
//    val at = oneDate.indexed
//  }
//}

//@InOut(3, 8)
//trait SocialNewsDefinition {
//
//  trait Story {
//    val title = oneString.fullTextSearch.indexed
//    val url   = oneString.uniqueIdentity
//  }
//
//  trait Comment extends SubComponentOf2[Story, Comment]   {
//    val author = one[User]
//    val text   = oneString
//  }
//
//  trait User {
//    val firstName    = oneString.indexed
//    val lastName     = oneString.indexed
//    val email        = oneString.uniqueIdentity
//    val passwordHash = oneString
//    val upVotes      = many[Story]
//  }
//
//  trait Publish {
//    val at = oneDate.indexed
//  }
//}
