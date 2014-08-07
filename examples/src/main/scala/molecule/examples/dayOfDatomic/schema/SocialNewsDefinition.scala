package molecule.examples.dayOfDatomic.schema
import molecule.dsl.schemaDefinition._

// http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html

@InOut(3, 8)
trait SocialNewsDefinition {

  trait Story {
    val title = oneString.fullTextSearch.indexed
    val url   = oneString.uniqueIdentity
  }

  trait Comment extends Node {
    val author = one[User]
    val text   = oneString
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
}
