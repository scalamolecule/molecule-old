package molecule
package examples.dayOfDatomic
import molecule.ast.model._
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic.schema._
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
import org.specs2.specification.Scope
import scala.language.existentials
//package examples.dayOfDatomic
//import molecule.examples.dayOfDatomic.schema._
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import datomic.Peer
//import datomic.Util.list
//import molecule.examples.dayOfDatomic.dsl.productsOrder._
//import molecule.examples.dayOfDatomic.dsl.socialNews._
//import scala.collection.JavaConversions._
//import scala.language.existentials
//import org.specs2.specification.Scope

class QueryTour extends DayOfAtomicSpec {

  // http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html

  class Setup extends Scope with DatomicFacade {

    // 1-2. Make db
    implicit val conn = load(SocialNewsSchema.tx, "SocialNews")

    // Add Stories
    val List(s1, s2, s3) = Story.title.url insert List(
      ("Teach Yourself Programming in Ten Years", "http://norvig.com/21-days.html"),
      ("Clojure Rationale", "http://clojure.org/rationale"),
      ("Beating the Averages", "http://www.paulgraham.com/avg.html")
    ) ids

    // Add Users
    val List(stu, ed) = User.firstName.lastName.email insert List(
      ("Stu", "Halloway", "stuarthalloway@datomic.com"),
      ("Ed", "Itor", "editor@example.com")
    ) ids

    // Add comments
    // Input Molecule act as a template to insert data
    //  val addComment0 = Parent.e.Comments.author.text.debug
    val addComment = Parent.e.Comments.author.text.insert

    // Insert Stu's first comment to story 1 and return the id of this comment
    // (Parent s1 is a Story)
    val c1 = addComment(s1, stu, "blah 1") id

    // Ed's Comment to Stu's first Comment
    // (Parent c1 is a Comment)
    val c2 = addComment(c1, ed, "blah 2") id
    // More sub-comments
    val c3 = addComment(c2, stu, "blah 3") id
    val c4 = addComment(c3, ed, "blah 4") id

    // Story 2 comments
    val c5 = addComment(s2, ed, "blah 5") id
    val c6 = addComment(c5, stu, "blah 6") id

    // Story 3 comments
    val c7 = addComment(s3, ed, "blah 7") id
    val c8 = addComment(c7, stu, "blah 8") id
    // Stu comments on his own comment
    val c9 = addComment(c8, stu, "blah 9") id

    // Story 2 again - a second thread of comments. This time Stu starts
    val c10 = addComment(s2, stu, "blah 10") id
    val c11 = addComment(c10, ed, "blah 11") id
    val c12 = addComment(c11, stu, "blah 12") id
  }

  "Query tour + Social News" in new Setup {

    // Created entity ids are simply Long values
    (s1, s2, s3) ===(17592186045418L, 17592186045419L, 17592186045420L)
    (stu, ed) ===(17592186045422L, 17592186045423L)
    (c1, c2, c3, c4,
      c5, c6, c7, c8,
      c9, c10, c11, c12) ===(
      17592186045425L, 17592186045427L, 17592186045429L, 17592186045431L,
      17592186045433L, 17592186045435L, 17592186045437L, 17592186045439L,
      17592186045441L, 17592186045443L, 17592186045445L, 17592186045447L)

    // 3. Finding All Users with a first name
    User.e.firstName_.get === List(stu, ed)

    // 4. Finding a specific user
    User.e.email_("editor@example.com").get.head === ed

    // 5. Finding a User's Comments
    Comment.e.Author.email_("editor@example.com").get.sorted === List(c2, c4, c5, c7, c11)
    Comment.e.Author.email_("stuarthalloway@datomic.com").get.sorted === List(c1, c3, c6, c8, c9, c10, c12)

    Comment.e.text.Author.email_("editor@example.com").firstName.get.sorted === List(
      (c2, "blah 2", "Ed"),
      (c4, "blah 4", "Ed"),
      (c5, "blah 5", "Ed"),
      (c7, "blah 7", "Ed"),
      (c11, "blah 11", "Ed")
    )

    // 6. Returning an Aggregate of Comments of some Author
    Comment.e.apply(count).Author.email_("editor@example.com").get.head === 5

    // Or we could simply read the size of the (un-aggregated) result (todo: measure performance differences)
    Comment.e.Author.email("editor@example.com").get.size === 5


    // 7. Have people commented on other people? (Multiple joins)
    Parent(User.email_).Comments.author.get.size === 0

    // Sanity check: 2 people are commenting on stories
    Parent(Story.title_).Comments.author.get.size === 2


    // Schema-aware joins .........................

    // 8. A Schema Query

    // Attributes of all entities having comments
    Parent.a.Comments.get.sorted === List(
      ":comment/author",
      ":comment/text",
      ":parent/comments",
      ":story/title",
      ":story/url"
    )

    // Attributes of stories having comments
    Parent(Story.a.title_).Comments.get.sorted === List(
      ":parent/comments",
      ":story/title",
      ":story/url"
    )

    // Attributes of comments having a sub-comment
    Parent(Comment.a.text_).Comments.get.sorted === List(
      ":comment/author",
      ":comment/text",
      ":parent/comments"
    )


    // Entities ...................................

    // 9-11. Finding an entity ID - An implicit Entity
    // Since we can implicitly convert an entity ID to an entity we'll call the id `editor`
    val editor: Long = User.e.email_("editor@example.com").get.head

    // 12. Requesting an Attribute value
    //    editor(":user/firstName") === Some("Ed")

    // Or as query
    User(editor).firstName.get.head === "Ed"


    // 13. Touching an entity
    // Get all attributes/values of this entity. Sub-component values are recursively retrieved
    editor.touch === Map(
      ":db/id" -> 17592186045423L,
      ":user/email" -> "editor@example.com",
      ":user/firstName" -> "Ed",
      ":user/lastName" -> "Itor"
    )

    // 14. Navigating backwards
    // The editors comments (Comments pointing to the Editor entity)
    editor(":comment/_author") === Some(List(c2, c4, c5, c7, c11))

    // .. almost same as: (here, only matching data is returned)
    // Comments of editor
    Comment.e.author_(editor).get.sorted === List(c2, c4, c5, c7, c11)

    // 15. Navigating Deeper
    // Comments to the editors comments
    editor(":comment/_author").get.asInstanceOf[List[Long]]
      .flatMap(_(":parent/comments")).asInstanceOf[List[Map[String, Any]]]
      .map(_.head._2) === List(c3, c6, c8, c12)

    // .. or use `for` loop
    (for {
      editorComment <- editor(":comment/_author").get.asInstanceOf[List[Long]]
      editorCommentComment <- editorComment(":parent/comments").asInstanceOf[Option[Map[String, Any]]]
    } yield editorCommentComment.head._2) === List(c3, c6, c8, c12)

    // .. or make query
    Parent(Comment.author_(editor)).comments.get.sorted === List(c3, c6, c8, c12)

    // Time travel ....................................
    //
    //          // 16. Querying for a Transaction
    //          val tx = User(ed).firstName_.txInstant
    //
    //          // 17. Converting Transacting to T
    //          val t = Peer.toT(tx)
    //
    //          // Query for relative system time
    //          User(ed).firstName_.txT === t
    //
    //          // 18. Getting Tx Instant
    //          val txInstant = User(ed).firstName_.txInstant.get.last
    //
    //          // 19. Going back in Time
    //          User(ed).firstName.asOf(t - 1).get.head === "Ed"
    //
    //
    //          // Auditing .......................................
    //
    //          // 20. Querying Across All time
    //          User(ed).firstName.txT.txAdded.history.get === List(
    //            ("Ed", 123456789L, true),
    //            ("Ed", 123456789L, false),
    //            ("Edward", 123456789L, true)
    //          )
    //
    //          // 21. Querying Plain Java Data
    //          // Not supported by Molecule
    //
    //
    //          // Social News =======================================================================================
    //
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
    //
    //
    //          // Provenance =======================================================================================
    //
    //          val ecURL = "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"
    //
    //          // Add data associated with transaction data
    //          val tx1 = Story.title.url.tx(Source.user_(stu)) insert List(
    //            ("ElastiCache in 6 minutes", ecURL),
    //            ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html")
    //          )
    //          val storyId = tx1.id
    //
    //          // Ed fixes the spelling error
    //          Story(storyId).title("ElastiCache in 5 minutes").tx(Source.user(ed)) update
    //
    //          // Title now
    //          Story.url_(ecURL).title.get.head === "ElastiCache in 5 minutes"
    //
    //          // Title before
    //          Story.url_(ecURL).title.asOf(tx1.inst).get.head === "ElastiCache in 6 minutes"
    //
    //          // Who changed the title and when?
    //          Story.url_(ecURL).title.txInstant.txAdded.tx(User.email).history.get === List(
    //            ("5 feb", true, "ed@itor.com")
    //          )
    //
    //          // Entire history of story entity
    //          Story(storyId).a.v.txInstant.txAdded.history.get === List(
    //            ("title", "ElastiCache in 6 minutes", "date...", true)
    //          )
    ok
  }

}


    //    import datomic.Peer
    //    import scala.collection.JavaConversions._
    //    import scala.collection.JavaConverters._
    //
    //      Peer.q(
    //        """
    //          |[:find ?a
    //          | :where
    //          |   [?a :comment/author 17592186045423]]
    //        """.stripMargin, conn.db).map(_.get(0)) === 7