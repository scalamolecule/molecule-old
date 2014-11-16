package molecule
package examples.dayOfDatomic
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
      ("stu", "Halloway", "stuarthalloway@datomic.com"),
      ("ed", "Itor", "editor@example")
    ) ids

    // Add comments
    // Input Molecule act as a template to insert data
    //  val addComment0 = Parent.e.Comments.author.text.debug
    val addComment = Parent.e.Comments.author.text.insert
    //    Parent.e.Comments.author.text.debug

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

    //  "Query tour + Social News" >> {

            // Created entity ids are simply Long values
        (s1, s2, s3) ===(17592186045418L, 17592186045419L, 17592186045420L)
        (stu, ed) ===(17592186045422L, 17592186045423L)
        (c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) ===(
          17592186045425L, 17592186045428L, 17592186045431L, 17592186045434L,
          17592186045437L, 17592186045440L, 17592186045443L, 17592186045446L,
          17592186045449L, 17592186045452L, 17592186045455L, 17592186045458L)


        // 3. Finding All Users with a first name
        User.firstName.ids === List(stu, ed)

        // 4. Finding a specific user
        User.email("editor@example").ids.head === ed

        // 5. Finding a User's Comments
        //            Comment.e.Author.email_("editor@example").debug
        Comment.e.Author.email_("editor@example").get.sorted === List(c2, c4, c5, c7, c11)
        Comment.e.Author.email_("stuarthalloway@datomic.com").get.sorted === List(c1, c3, c6, c8, c9, c10, c12)

        Comment.e.text.Author.email_("editor@example").firstName.get.sorted === List(
          (c2, "blah 2", "ed"),
          (c4, "blah 4", "ed"),
          (c5, "blah 5", "ed"),
          (c7, "blah 7", "ed"),
          (c11, "blah 11", "ed")
        )
//
//
//    // 6. Returning an Aggregate of Comments of some Author
//    Comment.e(count).Author.email_("editor@example").get.head === 5
//
//    // Or we could simply read the size of the (un-aggregated) result (todo: measure performance differences)
//    Comment.Author.email("editor@example").size === 5
//
//
//    // 7. Have people commented on other people? (Multiple joins)
////    Parent(User.email).Comments.author.debug
//    Parent.e.Comments.author.text.debug
//    Parent.e.Comments.author.debug
////    Parent(Story.title).Comments.author_.debug
////    Parent(Story.title).Comments.author.get === 7
////    Parent(Comment.text).Comments.author.debug
////    Parent(User.email).Comments.author.size === 0



    //    //    [?a :user/email]
    //    //    [?a :parent/comments ?b]
    //    //    [?b :comment/author]
    //
    //
    //
    //
    //    // hyper edge ???!!!
    //    // Parent is both a User and a Story...
    //    Parent(
    //      User.email)(
    //        Story.title).Comments.author.size === 3
    //
    //    Parent.apply(User.email).apply(Story.title).Comments.author.size === 3
    //
    //
    //
    //    // or
    //    Parent.apply(User.email_(count)).Comments.author_.get === 3
    //    Parent.apply(User.email(count)).Comments.author_.get === 3
    //    Parent.apply(User.email).Comments.author_.get === 3
    //    Parent.apply(User.email_).Comments.author(count).get === 3
    //
    //
    //
    //
    //    //    m(Parent.e.Comments.author_(count) ~ User.e.email_).get === 3
    //
    //    m(Parent.e(User.email).Comments.author_(count)).get === 3
    //
    //    Parent.e(User.email).Comments.author.size === 3
    //
    //
    //
    //
    //
    //    Comment(User.email).author.get === 3
    //
    //    Comment.author(User.email).get === 3

    //    m(Comment.author_. ~ User.email_).get === 3
    //    Parent.Comments.e.debug === 3
    //    User.e(asA) Parent.Comments.author.debug === 3
    //    Parent.e.Comment.debug === 3
    //    Parent.e.comment.debug === 3
    //    Parent.comment.e.debug === 3
    ////    Parent.e.debug === 3
    ////    Parent.e.a.get === 3

    //    [?comment :comment/author]
    //    [?commentable :comments ?comment]
    //    [?commentable :user/email]

    //    [?a :comment/author]
    //    [?b :parent/comments ?a]
    //    [?b :user/email]

    //    [?a :comment/author]
    //    [?b :parent/comments ?a]
    //    [?b :user/email]

    // Schema-aware joins .........................

    // 8. A Schema Query


    //    // Attributes of entities having comments
    //    Parent.a.Comments.get.sorted === List(
    //      ":comment/author",
    //      ":comment/text",
    //      ":comment/tree_",
    //      ":parent/comment",
    //      ":story/title",
    //      ":story/url"
    //    )

    // Attributes of stories having comments
    //    Story.a.Comments.text_.get === List(
    //      ":story/title",
    //      ":story/url"
    //    )
    //
    //    // Attributes of comments having a sub-comment
    //    Comment.a.Comment.text_.get === List(
    //      ":comment/text",
    //      ":comment/author",
    //      ":comment/tree_"
    //    )
    //
    //
    //          // Entities ...................................
    //
    //          // 9-11. Finding an entity ID - An implicit Entity
    //          // Since we can implicitly convert an entity ID to an entity we'll call the id `editor`
    //          val editor = User.e.email_("editor@example.com").get.head
    //
    //          // 12. Requesting an Attribute value
    //          editor(":user/firstName") === Some("Edward")
    //          // this one ??
    //          User(editor).firstName.first === "Edward"
    //
    //          // 13. Touching an entity
    //          // Get all attributes/values of this entity. Sub-component values are recursively retrieved
    //          editor.touch === Map(
    //            ":db/id" -> 17592186045423L,
    //            ":user/firstName" -> "Edward",
    //            ":user/lastName" -> "Itor",
    //            ":user/email" -> "editor@example.com"
    //          )
    //
    //          // 14. Navigating backwards
    //          // The editors comments (Comments pointing to the Editor entity)
    //          editor(":comment/_author") === List(c2, c4, c5, c7, c11)
    //
    //          // .. almost same as: (here, only matching data is returned)
    //          // Comments of editor
    //          Comment.e.author_(editor).get === List(c2, c4, c5, c7, c11)
    //
    //          // 15. Navigating Deeper
    //          // The editors comments' comments
    //          editor(":comment/_author")(":comment/tree_") === List(c6, c8, c12)
    //          Comment.author_(editor).Comment.e.get === List(c6, c8, c12)
    //
    //          // Comments that Editor commented on
    //          Comment.e.Comment.author_(editor).get === List(c6, c8, c12)
    //
    //
    //          // Time travel ....................................
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
    //            ("editor@example", Set()),
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