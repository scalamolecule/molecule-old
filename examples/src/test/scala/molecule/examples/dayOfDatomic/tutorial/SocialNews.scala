package molecule
package examples.dayOfDatomic.tutorial
//import scala.language.reflectiveCalls
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic._

class SocialNews extends DayOfAtomicSpec {

  "Social news" in new SocialNewsSetup {

    // Add underscore to attribute name to _not_ return it's value (and keep it as a search attribute)
    // Here we get all Story ids (entities having a url value)
    val allStories = Story.url_.e.get

    // Add John and let him upvote all stories
    val john = User.email.firstName.lastName.upVotes insert List(
      ("john@example.com", "John", "Doe", allStories.toSet)
    ) id

    // Update John's first name
    User(john).firstName("Jonathan").update

    // John regrets upvoting Paul Graham story (`s3`)
    User(john).upVotes.remove(s3).debug
    User(john).upVotes.remove(s3).update

    // John now has only 2 upvotes
    User(john).upVotes.get.head.size === 2

    // John skips all upvotes
    User(john).upVotes().update

    // John now has no upvotes
    User(john).upVotes.get.head.size === 0


    // Let Stuart upvote a story
    User(stu).upVotes(s1).update

    // How many users are there?
    User.email.get.length === 3

    // How many users have upvoted something? (Stuart)
    User.email.upVotes_.get.head.size === 1

    // Users and optional upvotes
    // Cardinality many attribute upVotes might return an empty set
    User.email.upVotes.get === List(
      ("stuarthalloway@datomic.com", Set(s1)),
      ("editor@example.com", Set()),
      ("john@example.com", Set())
    )
  }
}
