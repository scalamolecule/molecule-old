package moleculeTests.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.SocialNews._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

// http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html

object QueryTour extends AsyncTestSuite {

  lazy val tests = Tests {

    "Queries and joins" - socialNews { implicit conn =>
      for {
        (txR2, tx1, s1, s2, s3, stu, ed, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) <- SocialNews.data

        // 3. Finding All Users with a first name
        _ <- User.e.firstName_.get.map(_.sorted ==> List(stu, ed).sorted)


        // 4. Finding a specific user
        _ <- User.e.email_("editor@example.com").get.map(_.head ==> ed)


        // 5. Finding a User's Comments
        _ <- Comment.e.Author.email_("editor@example.com").get.map(_.sorted ==> List(c2, c4, c5, c7, c11))
        _ <- Comment.e.Author.email_("stuarthalloway@datomic.com").get.map(_.sorted ==> List(c1, c3, c6, c8, c9, c10, c12))

        _ <- Comment.e.text.Author.email_("editor@example.com").firstName.get.map(_.sorted ==> List(
          (c2, "blah 2", "Ed"),
          (c4, "blah 4", "Ed"),
          (c5, "blah 5", "Ed"),
          (c7, "blah 7", "Ed"),
          (c11, "blah 11", "Ed")
        ))


        // 6. Returning an Aggregate of Comments of some Author
        _ <- Comment.e(count).Author.email_("editor@example.com").get.map(_.head ==> 5)

        // Or we could simply read the size of the (un-aggregated) result (todo: measure performance differences)
        _ <- Comment.e.Author.email("editor@example.com").get.map(_.size ==> 5)


        // 7. Have people commented on other people?
        // Composite molecule with a User.email (a User) sharing entity id with Parent that points to a Comment author
        _ <- m(User.email_ + Parent.Comment.author).get.map(_.size ==> 0)

        // Sanity check: 2 people are commenting on stories
        // Composite molecule with a Story.title (a Story) sharing entity id with Parent that points to a Comment author
        _ <- m(Story.title_ + Parent.Comment.author).get.map(_.size ==> 2)
      } yield ()
    }


    "Schema-aware joins" - socialNews { implicit conn =>
      for {
        (txR2, tx1, s1, s2, s3, stu, ed, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) <- SocialNews.data
        // 8. A Schema Query

        // Attributes of all entities having comments
        commentIds <- Parent.e.comment_.get
        _ <- Parent(commentIds).a.get.map(_.sorted ==> List(
          ":Comment/author",
          ":Comment/text",
          ":Parent/comment",
          ":Story/title",
          ":Story/url"
        ))

        // Attributes of stories having comments
        storiesWithComments <- m(Story.e.title_ + Parent.comment_).get
        _ <- Story(storiesWithComments).a.get.map(_ ==> List(
          ":Story/url",
          ":Story/title",
          ":Parent/comment",
        ))

        // Attributes of comments having a sub-comment
        commentsWithSubComments <- m(Comment.e.text_ + Parent.comment_).get
        _ <- Comment(commentsWithSubComments).a.get.map(_.sorted ==> List(
          ":Comment/author",
          ":Comment/text",
          ":Parent/comment"
        ))
      } yield ()
    }


    "Entities" - socialNews { implicit conn =>
      for {
        (txR2, tx1, s1, s2, s3, stu, ed, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) <- SocialNews.data

        // 9-11. Finding an entity ID - An implicit Entity
        // Since we can implicitly convert an entity ID to an entity we'll call the id `editor`
        editors <- User.e.email_("editor@example.com").get
        editor = editors.head


        // 12. Requesting an Attribute value
        _ <- User(editor).firstName.get.map(_ ==> List("Ed"))


        // 13. Touching an entity
        // Get all attributes/values of this entity. Sub-component values are recursively retrieved
        _ <- editor.graph.map(_ ==> Map(
          ":db/id" -> ed,
          ":User/email" -> "editor@example.com",
          ":User/firstName" -> "Ed",
          ":User/lastName" -> "Itor"
        ))


        // 14. Navigating backwards

        // The editors comments (Comments pointing to the Editor entity)
        _ <- Comment.e.author_(editor).get.map(_.sorted ==> List(c2, c4, c5, c7, c11))


        // 15. Navigating Deeper

        // Comments to the editors comments
        _ <- m(Comment.author_(editor) + Parent.comment).get.map(_.sorted ==> List(c3, c6, c8, c12))

        // Editors comments and responses (note that c4 wasn't commented on)
        _ <- m(Comment.author_(editor) + Parent.e.comment).get.map(_.sorted ==> List(
          (c2, c3),
          (c5, c6),
          (c7, c8),
          (c11, c12)
        ))

        // Editors comments having responses (c4 not commented on)
        _ <- m(Comment.author_(editor) + Parent.e.comment_).get.map(_.sorted ==> List(c2, c5, c7, c11))
      } yield ()
    }


    "Time travel" - socialNews { implicit conn =>
      for {
        (txR2, tx1, s1, s2, s3, stu, ed, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) <- SocialNews.data

        // Initial creation of Ed
        tx2 = txR2.tx

        // Update Ed's first name
        txR3 <- m(User(ed).firstName("Edward")).update


        (tx3, t3, tx3Instant) <- User(ed).firstName_.tx.t.txInstant.get.map { res =>
          val (tx, t, txInstant) = res.head
          // 16. Querying for a Transaction
          tx ==> txR3.tx

          // 17. Converting Transaction to T
          t ==> txR3.t
          // On the jvm side you can convert with the Peer
          // datomic.Peer.toT(tx3)

          // 18. Getting a Tx Instant
          txInstant ==> txR3.txInstant

          (tx, t, txInstant)
        }

        // 19. Going back in Time

        // Current name
        _ <- User(ed).firstName.getAsOf(t3).map(_.head ==> "Edward")

        // Name before change (from previous transaction)
        _ <- User(ed).firstName.getAsOf(t3 - 1).map(_.head ==> "Ed")

        // We can use the transaction entity id or time also
        _ <- User(ed).firstName.getAsOf(tx3).map(_.head ==> "Edward")
        _ <- User(ed).firstName.getAsOf(tx3Instant).map(_.head ==> "Edward")


        // Auditing ................................................................

        // 20. Querying Across All time (sort by transactions)
        _ <- User(ed).firstName.tx.op.getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          ("Ed", tx2, true),
          ("Ed", tx3, false),
          ("Edward", tx3, true)
        ))

        // 21. Querying Plain Java Data - not supported by Molecule
      } yield ()
    }
  }
}