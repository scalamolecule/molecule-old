package molecule
package examples.dayOfDatomic
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic.schema.SocialNewsSchema
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
import scala.language.existentials

class semantics extends DayOfAtomicSpec {

  "Query tour (trees)" >> {

    // http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html

    // 1-2. Make db
    implicit val conn = load(SocialNewsSchema.tx, "SocialNews")

    // Add Stories
    val List(s1, s2, s3) = Story.title.url insert List(
      ("Teach Yourself Programming in Ten Years", "http://norvig.com/21-days.html"),
      ("Clojure Rationale", "http://clojure.org/rationale"),
      ("Beating the Averages", "http://www.paulgraham.com/avg.html")
    )

    // Add Users
    val List(stu, ed, marc) = User.firstName.lastName.email insert List(
      ("stu", "Halloway", "stuarthalloway@datomic.com"),
      ("ed", "Itor", "editor@example"),
      ("Marc", "Grue", "marc@grue")
    )

    // Created entity ids are simply Long values
    (s1, s2, s3) === (17592186045418L, 17592186045419L, 17592186045420L)
    (stu, ed) === (17592186045422L, 17592186045423L)

    // 3. Finding All Users with a first name
    User.firstName.ids === List(stu, ed)

    // 4. Finding a specific user
    User.email("editor@example").ids.head === ed


    // Add comments ..............................

    // Users can:

    // 1. Comment on a Story
    val storyComment = Story.eid.Comments.author.text insert

    // 2. Comment on a Comment
    val subComment = Comment._parent.author.text insert

    // Sub-comments form hierarchical trees of Comment nodes having the
    // previous comment as parent and the initial Story Comment as root

    // Insert Stu's first comment to story 1 and return the id of this comment
    val c1 = storyComment(s1, stu, "blah 1") head

    // Ed's Comment to Stu's first Comment
    val c2 = subComment(c1, ed, "blah 2") head

    // More sub-comments
    val c3 = subComment(c2, stu, "blah 3") head
    val c4 = subComment(c3, ed, "blah 4") head

    // Story 2 comments
    val c5 = storyComment(s2, ed, "blah 5") head
    val c6 = subComment(c5, stu, "blah 6") head

    // Story 3 comments
    val c7 = storyComment(s3, ed, "blah 7") head
    val c8 = subComment(c7, stu, "blah 8") head
    // Stu comments on his own comment
    val c9 = subComment(c8, stu, "blah 9") head

    // Story 2 again - a second thread of comments. This time Stu starts
    val c10 = storyComment(s2, stu, "blah 10") head
    val c11 = subComment(c10, ed, "blah 11") head
    val c12 = subComment(c11, stu, "blah 12") head

    // New Comment ids (a second entity is created for each association)
    List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) === List(
      17592186045425L, 17592186045427L, 17592186045429L, 17592186045431L, 17592186045433L, 17592186045435L,
      17592186045437L, 17592186045439L, 17592186045441L, 17592186045443L, 17592186045445L, 17592186045447L
    )

    // 5. Finding a User's Comments
    // Semantically we can take two approaches:
    // a) "Comments of Ed" (sql-style: "find Comment where email = "editor@example")
    Comment.eid.Author.email("editor@example").get.sorted === List(c2, c4, c5, c7, c11)
    // b) "Ed's Comments"
    User.email("editor@example")._Comments.eid.get.sorted === List(c2, c4, c5, c7, c11)

    // Mumbo-jumbo
    // Comments of Author and his comments (the same!)
    Comment.eid.Author.email("editor@example")._Comments.eid.get.sorted === List(c2, c4, c5, c7, c11)

    // Users Comments Author's (the same!)
    User.email("editor@example")._Comments.Author.eid.get.sorted === List(c2, c4, c5, c7, c11)



    // 6. Returning an Aggregate of Comments of some Author
    Comment(count).Author.email("editor@example").get.head === 5
    Comment.Author.email("editor@example").size === 5

    User.email("editor@example")._Comments.eid.size === 5


    // Or we could read the size of the (un-aggregated) result set of Comment entity ids
    Comment.eid.Author.email("editor@example").size === 5


    // 7. Have people commented on other people? (Multiple joins)
    // We can think of people as User's with an email and see if any Comments point to such entities

    (User.email ~ Comment.author).size === 0

    Comment._parent(User.email).size === 0

    Comment._Story

    import molecule.examples.seattle.dsl.seattle._


    // Namespace
    Comment
    // Comment() ??
    Comment(c1)

    // Attribute, cardinality one
    Comment.text
    Comment.text()
    Comment.text("v1")
    Comment.text("v1" or "v2")
    Comment.text("v1", "v2")
    Comment.text(Seq("v1", "v2"))

    // Attribute, cardinality many
    Community.category
    Community.category()
    Community.category("v1")
    Community.category("v1" or "v2")
    Community.category("v1", "v2")
    Community.category(Seq("v1", "v2"))
    Community.category("oldValue1" -> "newValue1")
    Community.category("oldValue1" -> "newValue1", "oldValue2" -> "newValue2")

    // Input attribute, cardinality one
    val name = m(Community.name(?))
    name.apply("v1")
    name.apply("v1" or "v2")
    name.apply("v1", "v2")
    name.apply(Seq("v1", "v2"))

    // Input attribute. cardinality many
    val cat = m(Community.category(?))
    cat.apply(Set("v1"))
    cat.apply(Set("v1", "v2", "v3", "v4"))
    cat.apply(Seq(Set("v1", "v2", "v3"), Set("v4")))
    cat.apply(Set("v1", "v2", "v3") or Set("v4"))

    // Multiple cardinality one input atributes
    val nameUrl = Community.name(?).url(?)
    nameUrl.apply("n1" and "u1")
    nameUrl.apply("n1", "u1")
    nameUrl.apply(("n1" and "u1") or ("n2" and "u2"))
    nameUrl.apply(("n1", "u1"), ("n2", "u2"))
    nameUrl.apply(Seq(("n1", "u1"), ("n2", "u2")))
    nameUrl.apply(Seq("n1", "n2"), Seq("u1", "u2"))

    // Multiple input atributes (mixed cardinality)
    val nameCat = Community.name(?).category(?)
    nameCat.apply("n1" and Set("c1"))
    nameCat.apply("n1", Set("c1"))
    nameCat.apply(("n1" and Set("c1")) or ("n2" and Set("c2")))
    nameCat.apply(("n1", Set("c1")), ("n2", Set("c2")))
    nameCat.apply(Seq(("n1", Set("c1")), ("n2", Set("c2"))))
    nameCat.apply(Seq("n1", "n2"), Seq(Set("c1"), Set("c2")))

    // enums...

    // 1-N
    // Stay in Comment namespace
    Comment.author
    Comment.author(ed)
    // Shift to Author/User namespace
    Comment.Author
    Comment.Author(ed)

    // N-M
    // Stay in Story namespace
    Story.comments
    Story.comments.apply(c1)
    Story.comments.apply(c1, c2)
    // Shift to Comment namespace
    Story.Comments
    Story.Comments.apply(c1)
    Story.Comments.eid.apply(c1)
    Story.Comments.eid.apply(c1, c2)


    // Trees
    Comment._parent
    Comment._parent(c1)
    Comment.tree(c1)
    Comment.tree(c1, 1)


    /** Stay in same namespace ***************************************************************/

    // A lower case relation name (holding Long ids to the referenced entity) is treated as
    // any other attribute of the namespace.

    // All comment authors
    Comment.author === List(stu, ed)

    Comment.author.text === List((stu, "blah 1"), (ed, "blah 2")) // etc (all returned)

    // Constrain some attribute value
    // Authors writing "blah 2" in a Comment
    Comment.author.text("blah 2") === List(ed)


    // Comments by Ed
    // Note how we stay in the Comment namespace
    Comment.author(ed).eid === List(c2, c4, c5, c7, c11)
    Comment.author(ed).text === List("blah 2", "blah 4", "blah 5", "blah 7", "blah 11")
    Comment.author(ed).text.contains("7") === List("blah 7")


    // Author of comment 1
    Comment(c1).author === List(stu)

    // Double constraints might collide
    // A comment only has one author, so constraining `author` doesn't make sense...
    // Comment 1 by Ed - doesn't exist! Stu wrote the first comment
    Comment(c1).eid.author(ed) === List()
    Comment(c1).eid.author(stu) === List(c1)


    /** Shift to next namespace ***************************************************************/

    // All Authors (Users who wrote a Comment)
    Comment.Author.eid === List(stu, ed)

    // Same result as
    Comment.author === List(stu, ed)

    // Without the Comment prefix to Author, we would get all Users (also those who didn't comment):
    User.eid === List(stu, ed, marc)

    // Comments of Author with firstName "stu"
    Comment.eid.Author.firstName("stu") === List(c1, c3, c6, c8, c9, c10, c12)


    /** Back-references ***************************************************************/

    // Ed's comments
    User(ed)._Comments.eid.get.sorted === List(c2, c4, c5, c7, c11)

    // Author who wrote "7" in a Comment
    Comment.text.contains("7").Author.eid.tpls.sorted === List(ed)

    // All Comments of Author who wrote "7" in a Comment
    Comment.text.contains("7").Author._Comments.eid.tpls.sorted === List(c2, c4, c5, c7, c11)




    // Other Comments of Author who wrote "7" in a Comment
    Comment.text_(contains("7")).Author._Comments.eid.text_.!(contains("7")).get.sorted === List(c2, c4, c5, c11)


    Comment.Author(ed) === List(c2, c4, c5, c7, c11)


    /** Trees ***************************************************************/

    Comment._parent.author === List(
      (c2, ed),
      (c3, stu),
      (c4, ed),
      (c6, stu),
      (c8, stu),
      (c9, stu),
      (c11, ed),
      (c12, stu)
    )


    (User.email <-: Comment.author).size === 0

    User._Comments.Author.name


    Comment.author._Comments.User.email

    Comment._Comment
    //    Story.Comments.
    User._Comments.Author


    User.Comment.User

    User._Comments.author

    Comment._parent(User.email).author

    Comment ~ User

    Comment.about(User.email)


    // Which Authors commented on Ed's Comments?
    Comment.author(ed).Comment.author

    // Which Stories have Ed commented on?

    // Ed's Comments' Stories
    Comment.author(ed).Story.title
    Comment.author(ed)._Story.title
    Comment.author(ed)._Stories.title

    // Stories with Ed's comment
    Story.title.Comments.author(ed)

    User._Comments.author(ed)


    // Ed's Comments
    User(ed)._Comments.text

    // Comments by Ed
    Comment.text.Author(ed)







    (User.eid ~ Comment.Author.eid).size === 0

    Comment.eid._parent(User.email).author.size === 0


    Comment.eid._parent(User.email("lkjlj")).author.size === 0

    Comment.eid._parent(User.email).author.size === 0

    // Schema-aware joins .........................

    // 8. A Schema Query
    Comment.Parent.attr.get === List(
      ":story/title",
      ":story/url",
      ":comment/text",
      ":comment/author",
      ":comment/tree_"
    )

    Comment.Parent.ns.get === List(
      ":story",
      ":comment"
    )


    // Entities ...................................

    // 9-11. Finding an entity ID - An implicit Entity
    // Since we can implicitly convert an entity ID to an entity we'll call the id `editor`
    val editor = User.eid.email("editor@example.com").get.head

    // 12. Requesting an Attribute value
    editor(":user/firstName") === Some("Edward")
    // this one ??
    User(editor).firstName.first === "Edward"

    // 13. Touching an entity
    // Get all attributes/values of this entity. Sub-component values are recursively retrieved
    editor.touch === Map(
      ":db/id" -> 17592186045423L,
      ":user/firstName" -> "Edward",
      ":user/lastName" -> "Itor",
      ":user/email" -> "editor@example.com"
    )

    // 14. Navigating backwards
    // The editors comments (Comments pointing to the Editor entity)
    editor(":comment/_author") === List(c2, c4, c5, c7, c11)

    // .. almost same as: (here, only matching data is returned)
    Comment.eid.Author(editor).get === List(c2, c4, c5, c7, c11)


    // 15. Navigating Deeper
    // The editors coments' comments
    editor(":comment/_author")(":comments") === List(c6, c8, c12)

    Comment.author(editor).Comment.eid.get === List(c6, c8, c12)




    Comment.author(editor)._Comment.eid.get === List(c6, c8, c12)



    // Time travel ....................................






    Orchestra.name("GSO").Musicians.name

    Musician.name.playsIn(Orchestra.name("GSO")).plays(Instruments.Strings.DoubleBass)

  }

}

