//package molecule
//package examples.dayOfDatomic.tutorial
////import scala.language.reflectiveCalls
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule.examples.dayOfDatomic.SocialNewsSchema.{User, Story}
//import molecule.examples.dayOfDatomic.samples._
//
//class SocialNews extends DayOfAtomicSpec with Generators {
//
//  "Social news" >> {
//    implicit val conn = init("social-news", "social-news.edn")
//
//    val threeStories = Story.url
//    val johnData = User.email("john@example.com").firstName("John").lastName("Doe")
//
//    add(johnData UpVotes threeStories)
//
//    change(User
//      .email("john@example.com") // find User by unique identity `email`
//      .firstName("Johnathan") // new data
//    )
//
//    val john = User.email("john@example.com")
//
//    // John upvotes Paul Graham story
//    val story = save(john UpVotes Story.url("http://www.paulgraham.com/avg.html")).get(Story)
//    m(john.UpVotes).count === 3
//
//    // John regrets last upvote
//    retract(john UpVotes story)
//    m(john.UpVotes).count === 2
//
//    // John regrets all upvotes
//    retract(john.UpVotes)
//    m(john.UpVotes).count === 0
//
//    // Todo: 10 new users, some with upvotes
//
//
//    generateUsersWithSomeUpvotes(conn, "user", 10)
//
//    // How many users now?
//    m(User).count === 11 // including John
//
//    // How many upvoters? (UpVotes mandatory)
//    m(User.UpVotes).count
//
//    // Users and optional upvotes
//    m(User.UpVotes(maybe)).get
//  }
//}

              // Social News =======================================================================================

    //          // (Adding what the Query Tour hasn't already covered)
    //
    //          // Add underscore to attribute name to _not_ return it's value (and keep it as a search attribute)
    //          // Here we get all Story ids (entities having a url value)
    //          val allStories = Story.url_.e.get
    //
    //          // Add John and let him upvote all stories
    //          val john = User.email.firstName.lastName.upVotes insert List(
    //            ("john@example.com", "John", "Doe", allStories.toSet)
    //          ) id
    //
    //          // Update John's first name
    //          User.firstName("Jonathan").update(john)
    //
    //          // John regrets upvoting Paul Graham story (`s3`)
    //          User.upVotes.remove(s3).update(john)
    //
    //          // John now has only 2 upvotes
    //          User(john).upVotes.get.head.size === 2
    //
    //          // John skips all upvotes
    //          User.upVotes().update(john)
    //
    //          // John has no upvotes any longer
    //          User(john).upVotes.get.head.size === 0
    //
    //
    //          // Let Stuart upvote a story
    //          User.upVotes(s1).update(stu)
    //
    //          // How many users are there?
    //          User.email.size === 3
    //
    //          // How many users have upvoted something? (Stuart)
    //          User.email.upVotes_.get.head.size === 1
    //
    //          // Users and optional upvotes
    //          // Cardinality many attribute upVotes might return an empty set
    //          User.email.upVotes.get === List(
    //            ("stuarthalloway@datomic.com", Set(s1)),
    //            ("editor@example.com", Set()),
    //            ("john@example.com", Set())
    //          )