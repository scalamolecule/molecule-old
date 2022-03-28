package moleculeTests.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.out4._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.SocialNews._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object SocialNews extends AsyncTestSuite {

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      // Add Stories
      txr1 <- Story.title.url insert List(
        ("Teach Yourself Programming in Ten Years", "http://norvig.com/21-days.html"),
        ("Clojure Rationale", "http://clojure.org/rationale"),
        ("Beating the Averages", "http://www.paulgraham.com/avg.html")
      )

      List(s1, s2, s3) = txr1.eids
      tx1 = txr1.tx

      // Add Users
      txR2 <- User.firstName.lastName.email insert List(
        ("Stu", "Halloway", "stuarthalloway@datomic.com"),
        ("Ed", "Itor", "editor@example.com")
      )
      List(stu, ed) = txR2.eids

      // Add comments
      // Input Molecule act as a template to insert data
      addComment = Parent.e.Comment.author.text.insert

      // Insert Stu's first comment to story 1 and return the id of this comment
      // (Parent s1 is a Story)
      txR1 <- addComment(s1, stu, "blah 1")
      c1 = txR1.eid

      // Ed's Comment to Stu's first Comment
      // (Parent c1 is a Comment)
      txr2 <- addComment(c1, ed, "blah 2")
      c2 = txr2.eid

      // More sub-comments
      txR3 <- addComment(c2, stu, "blah 3")
      c3 = txR3.eid
      txR4 <- addComment(c3, ed, "blah 4")
      c4 = txR4.eid

      // Story 2 comments
      txR5 <- addComment(s2, ed, "blah 5")
      c5 = txR5.eid
      txR6 <- addComment(c5, stu, "blah 6")
      c6 = txR6.eid

      // Story 3 comments
      txR7 <- addComment(s3, ed, "blah 7")
      c7 = txR7.eid
      txR8 <- addComment(c7, stu, "blah 8")
      c8 = txR8.eid

      // Stu comments on his own comment
      txR9 <- addComment(c8, stu, "blah 9")
      c9 = txR9.eid

      // Story 2 again - a second thread of comments. This time Stu starts
      txR10 <- addComment(s2, stu, "blah 10")
      c10 = txR10.eid
      txR11 <- addComment(c10, ed, "blah 11")
      c11 = txR11.eid

      txR12 <- addComment(c11, stu, "blah 12")
      c12 = txR12.eid
    } yield {
      (txR2, tx1, s1, s2, s3, stu, ed, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12)
    }
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Social News" - socialNews { implicit conn =>
      for {
        (txR2, tx1, s1, s2, s3, stu, ed, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) <- data

        // Add underscore to attribute name to _not_ return it's value (and keep it as a search attribute)
        // Here we get all Story ids (entities having a url value)
        allStories <- Story.url_.e.get

        // Add John and let him upvote all stories
        john <- User.email.firstName.lastName.upVotes insert List(
          ("john@example.com", "John", "Doe", allStories.toSet)
        ) map(_.eid)

        // Users with upvotes
        _ <- User.email.upVotes.get.map(_ ==> List(("john@example.com", Set(s1, s2, s3))))

        // Update John's first name
        _ <- User(john).firstName("Jonathan").update

        // John regrets upvoting Paul Graham story (`s3`)
        eids <- Story.e.url_("http://www.paulgraham.com/avg.html").get
        paulGrahamStory = eids.head
        _ <- User(john).upVotes.retract(paulGrahamStory).update
        // or:
        //        _ <- Story.e.url_("http://www.paulgraham.com/avg.html").get.map { res =>
        //          User(john).upVotes.retract(res.head).update
        //        }

        // John now has only 2 upvotes
        _ <- User(john).upVotes.get.map(_.head.size ==> 2)

        // Only John's 2 upvotes exist
        _ <- User.email.upVotes.get.map(_ ==> List(("john@example.com", Set(s1, s2))))

        // Retract all John's upvotes
        _ <- User(john).upVotes().update

        // John now has no upvotes
        _ <- User(john).upVotes.get.map(_.size ==> 0)

        // No Users with upvotes anymore
        _ <- User.email.upVotes.get.map(_ ==> List())

        // Let Stuart upvote a story
        _ <- User(stu).upVotes(paulGrahamStory).update

        // Current Users and upvotes
        _ <- User.email.upVotes.get.map(_.head ==> ("stuarthalloway@datomic.com", Set(paulGrahamStory)))

        // Current upVotes
        _ <- User.email.a1.upVotes$.get.map(_ ==> List(
          ("editor@example.com", None),
          ("john@example.com", None),
          ("stuarthalloway@datomic.com", Some(Set(paulGrahamStory))),
        ))
      } yield ()
    }
  }
}