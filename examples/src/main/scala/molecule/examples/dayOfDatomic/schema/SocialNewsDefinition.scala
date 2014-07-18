package molecule.examples.dayOfDatomic.schema
import molecule.dsl.schemaDefinition._


@InOut(3, 8)
trait SocialNewsDefinition {

  trait Story {
    val title    = oneString.fullTextSearch.indexed
    val url      = oneString.uniqueIdentity
    val slug     = oneString
    val comments = many[Comment].components
  }

  trait Comment {
    val parent = one[Comment]
    val body   = oneString
    val author = one[User]
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