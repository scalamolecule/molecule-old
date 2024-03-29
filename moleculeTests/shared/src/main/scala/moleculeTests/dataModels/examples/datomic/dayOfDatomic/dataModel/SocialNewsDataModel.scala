package moleculeTests.dataModels.examples.datomic.dayOfDatomic.dataModel

import molecule.core.data.model._

// http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html


@InOut(2, 5)
object SocialNewsDataModel {

  trait Story {
    val title = oneString.fulltext.index
    val url   = oneString.uniqueIdentity
  }

  trait Parent {
    val comment = one[Comment].isComponent
  }

  trait Comment {
    val author = one[User]
    val text   = oneString
  }

  trait User {
    val firstName    = oneString.index
    val lastName     = oneString.index
    val email        = oneString.uniqueIdentity
    val passwordHash = oneString
    val upVotes      = many[Story]
  }

  trait Publish {
    val at = oneDate.index
  }

  trait MetaData {
    val user    = one[User]
    val usecase = oneString
  }
}