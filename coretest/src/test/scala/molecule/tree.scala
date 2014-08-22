//package molecule
//package examples.dayOfDatomic
//import molecule.examples
//import datomic.Peer
//import datomic.Util.list
//import molecule.examples.dayOfDatomic.dsl.productsOrder._
//import molecule.examples.dayOfDatomic.dsl.socialNews._
//import molecule.examples.dayOfDatomic.schema.SocialNewsSchema
//import scala.collection.JavaConversions._
//import scala.language.existentials
//
//class DayOfDatomic extends DayOfAtomicSpec {
//
//  "Query tour (trees)" >> {
//
//    // http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html
//
//    // 1-2. Make db
//    implicit val conn = load(SocialNewsSchema.tx, "SocialNews")
//
//    // Add Stories
//    val List(s1, s2, s3) = Story.title.url insert List(
//      ("Teach Yourself Programming in Ten Years", "http://norvig.com/21-days.html"),
//      ("Clojure Rationale", "http://clojure.org/rationale"),
//      ("Beating the Averages", "http://www.paulgraham.com/avg.html")
//    )
//
//    // Add Users
//    val List(stu, ed) = User.firstName.lastName.email insert List(
//      ("stu", "Halloway", "stuarthalloway@datomic.com"),
//      ("ed", "Itor", "editor@example")
//    )
//
//    // Created entity ids are simply Long values
//    (s1, s2, s3) === (17592186045418L, 17592186045419L, 17592186045420L)
//    (stu, ed) === (17592186045422L, 17592186045423L)
//
//    // 3. Finding All Users with a first name
//    User.firstName.ids === List(stu, ed)
//
//    // 4. Finding a specific user
//    User.email("editor@example").ids.head === ed
//
//
//    // Add comments ..............................
//
//    // Users can Comment on a Story and on other Comments
//
//    // 1. When a Story is retracted all its Comments should also be retracted
//    //    - Comments must be subComponents of Story
//    //    - SubComments must be subComponents of parent Comment (a tree of Comments)
//
//
//    // One Comment to a Story
//    // Each thread of Comments is a tree with the first comment to the story is a root node
//    // having the story as its parent
//    // - basically a tree of Comments
//    // We treat a Comment as a node in a tree.
//    // Stu's first Comment to Story 1
//    // Here we insert the data "manually", attribute by attribute and then associate the Comment to the Story
//    //    val c1 = Comment.author(stu).text("blah 1") insertAndConnectTo s1
//    //    val c1 = Comment.parent(s1).author(stu).text("blah 1").insert.head
//
//    //        val c1 = Story(s1).comments(Comment.author(stu).text("blah 1")).insert.head
//
//    //    val c1 = Story(s1).Comments.author(stu).text("blah 1").insert.head
//
//
//    val c1 = Comment.parent(s1).author(stu).text("blah 1").insert.head
//
//    // Use an insertNode as a template to ease inserting multiple nodes...
//    //    val comment = Comment.author.text insertSub
//    //    val comment = Comment.parent.author.text insert
//    //    val comment = Story.eid.comments(Comment.author.text) insert
//    //    val comment = Story.eid.Comments.author.text insert
//
//    //    val comment = Story.e.Comment.author.text insert
//    //
//    //    val comment1 = Story.e.Comments.author.text insert
//    //
//    //    val comments = Story.e.comments(Comment.author.text) insert
//    //
//    //    val comment2 = Comment.e.Comment.author.text insert
//
//    //    val comment = e ~ Comment.author.text insert
//
//
//    //    val comment1 = Story.Comments.parent.author.text insert
//
//    val insertComment = Comment.parent.author.text insert
//
//
//    // Ed's Comment to Stu's first Comment
//    // (Associated entity id `c1` is supplied in second parameter list
//    //    val c2 = comment(c1, Seq((ed, "blahh 2")))
//    //    val c2 = comment(c1, Seq((ed, "blahh 2")))
//    val c2 = insertComment(c1, ed, "blahh 2") head
//
//    //    val c2 = comment(ed, "blahh 2")(c1)
//
//    // Etc...
//    val c3 = insertComment(c2, stu, "blah 3") head
//    val c4 = insertComment(c3, ed, "blahh 4") head
//
//
//    // Story 2 comments
//    val c5 = insertComment(s2, ed, "blahh 5") head
//    val c6 = insertComment(c5, stu, "blah 6") head
//
//    // Story 3 comments
//    val c7 = insertComment(s3, ed, "blahh 7") head
//    val c8 = insertComment(c7, stu, "blah 8") head
//    // Stu comments on his own comment
//    val c9 = insertComment(c8, stu, "blah 9") head
//
//    // Story 2 again - a second thread of comments. This time Stu starts
//    val c10 = insertComment(s2, stu, "blah 10") head
//    val c11 = insertComment(c10, ed, "blahh 11") head
//    val c12 = insertComment(c11, stu, "blah 12") head
//
//    // New Comment ids (a second entity is created for each association)
//    List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) === List(
//      17592186045425L, 17592186045427L, 17592186045429L, 17592186045431L, 17592186045433L, 17592186045435L,
//      17592186045437L, 17592186045439L, 17592186045441L, 17592186045443L, 17592186045445L, 17592186045447L
//    )
//
//    // 5. Find Ed and Stu's comments:
//    Comment.e.Author.email("editor@example").get.sorted === List(c2, c4, c5, c7, c11)
//    Comment.e.Author.email("stuarthalloway@datomic.com").get.sorted === List(c1, c3, c6, c8, c9, c10, c12)
//
//    // 6. Returning an Aggregate of Comments of some Author
//    Comment(count).Author.email("editor@example").get.head === 5
//    Comment(count).Author.email("stuarthalloway@datomic.com").get.head === 7
//
//    Comment.Author.email("editor@example").size === 5
//    Comment.Author.email("stuarthalloway@datomic.com").size === 7
//
//    // Or we could read the size of the (un-aggregated) result set of Comment entity ids
//    Comment.e.Author.email("editor@example").size === 5
//
//
//    // 7. Have we commented on people? (Multiple joins)
//    // We can think of people as User's with an email and see if any Comments point to such entities
//    Comment.parent(User.email).size === 0
//
//
//
//    // Comments to story2
//
//    // All comments to story 2 (s2 is the root of the tree)
//    Comment.tree(s2).e.get === List(c5, c6, c10, c11, c12)
//    Comment.root(s2).e.get === List(c5, c6, c10, c11, c12)
//
//    // Initial commentes to story 2
//    Comment.tree(s2, 1).e.get === List(c5, c10)
//    Comment.parent(s2).e.get === List(c5, c10)
//
//    // Comments on comments
//    Comment.tree(s2, 2).e.get === List(c6, c11)
//    Comment.grandParent(s2).e.get === List(c6, c11)
//
//    // Comments on comments to comments
//    Comment.tree(s2, 3).e.get === List(c12)
//
//    // All sub-comments
//    Comment.tree(s2, +2).e.get === List(c6, c11, c12)
//
//    // Stu's comments to story2
//    Comment.tree(s2).e.Author.email("stuarthalloway@datomic.com").get === List(c6, c10, c12)
//
//    // Stu's initial comments to story2
//    Comment.tree(s2, 1).e.Author.email("stuarthalloway@datomic.com").get === List(c6)
//
//    // Stu's secondary comments (depth 2)
//    Comment.tree(s2, 2).e.Author.email("stuarthalloway@datomic.com").get === List(c10)
//
//    // Stu's primary and secondary comments (depth 1 to 2)
//    Comment.tree(s2, 1 - 2).e.Author.email("stuarthalloway@datomic.com").get === List(c6, c10)
//
//    // Stu's comments on subcomments to story2 (depth 3)
//    Comment.tree(s2, 3).e.Author.email("stuarthalloway@datomic.com").get === List(c12)
//
//    // Stu's subcomments to story2 (depth 2 and below)
//    Comment.tree(s2, +2).e.Author.email("stuarthalloway@datomic.com").get === List(c10, c12)
//
//
//
//
//
//    // A Story's initial Comments
//    Story(s2).comments.get === List(c5, c10)
//
//    // more correct...
//    Story(s2).comments.depth(1).get === List(c5, c10)
//    Story(s2).comments.getAll === List(c5, c6, c10, c11, c12)
//
//
//
//    // A Story's Comment trees
//    Story(s2).comments.tree === Map(
//      c5 -> Map(
//        c6 -> null
//      ),
//      c10 -> Map(
//        c11 -> Map(
//          c12 -> null
//        )
//      )
//    )
//
//    Story(s2).comments.tree === Map(
//      c5 -> Map(
//        c6 -> null
//      ),
//      c10 -> Map(
//        c11 -> Map(
//          c12 -> null
//        )
//      )
//    )
//
//
//
//    Orchestra.name("GSO").Musicians.name
//
//    Musician.name.playsIn(Orchestra.name("GSO")).plays(Instruments.Strings.DoubleBass)
//
//
//
//
//  }
//
//}