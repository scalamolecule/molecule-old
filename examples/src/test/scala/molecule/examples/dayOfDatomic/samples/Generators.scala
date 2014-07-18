//package molecule
//package examples.dayOfDatomic.samples
//import molecule.examples.dayOfDatomic.dsl.socialNews._
//import datomic._
//
//trait Generators {
//
//  def generateUsersWithSomeUpvotes(conn: Connection, emailPrefix: String, n: Int) = {
//    (1 to n).map { i =>
//      val someStories = ???
//      add(User.email(s"$emailPrefix-$i@example.com") UpVotes someStories)
//    }
//
//  }
//}