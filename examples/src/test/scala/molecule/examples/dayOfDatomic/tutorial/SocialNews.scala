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