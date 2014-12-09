package molecule
package examples.dayOfDatomic
import datomic.Peer
import molecule.examples.dayOfDatomic.dsl.socialNews._
//import molecule.examples.dayOfDatomic.schema._
import molecule.util.MoleculeSpec
//import org.specs2.specification.Scope
import scala.language.{existentials, postfixOps}

class QueryTour extends MoleculeSpec {

  // http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html

//  "Queries and joins" in new SocialNewsSetup {
//
//    // Created entity ids are simply Long values
//    (s1, s2, s3) ===(17592186045418L, 17592186045419L, 17592186045420L)
//    (stu, ed) ===(17592186045422L, 17592186045423L)
//    (c1, c2, c3, c4,
//      c5, c6, c7, c8,
//      c9, c10, c11, c12) ===(
//      17592186045425L, 17592186045427L, 17592186045429L, 17592186045431L,
//      17592186045433L, 17592186045435L, 17592186045437L, 17592186045439L,
//      17592186045441L, 17592186045443L, 17592186045445L, 17592186045447L)
//
//
//    // 3. Finding All Users with a first name
//    User.e.firstName_.get === List(stu, ed)
//
//
//    // 4. Finding a specific user
//    User.e.email_("editor@example.com").get.head === ed
//
//
//    // 5. Finding a User's Comments
//    Comment.e.Author.email_("editor@example.com").get.sorted === List(c2, c4, c5, c7, c11)
//    Comment.e.Author.email_("stuarthalloway@datomic.com").get.sorted === List(c1, c3, c6, c8, c9, c10, c12)
//
//    Comment.e.text.Author.email_("editor@example.com").firstName.get.sorted === List(
//      (c2, "blah 2", "Ed"),
//      (c4, "blah 4", "Ed"),
//      (c5, "blah 5", "Ed"),
//      (c7, "blah 7", "Ed"),
//      (c11, "blah 11", "Ed")
//    )
//
//
//    // 6. Returning an Aggregate of Comments of some Author
//    Comment.e(count).Author.email_("editor@example.com").get.head === 5
//
//    // Or we could simply read the size of the (un-aggregated) result (todo: measure performance differences)
//    Comment.e.Author.email("editor@example.com").get.size === 5
//
//
//    // 7. Have people commented on other people? (Multiple joins)
//    Parent(User.email_).Comment.author.get.size === 0
//
//    // Sanity check: 2 people are commenting on stories
//    Parent(Story.title_).Comment.author.get.size === 2
//  }
//
//
//  "Schema-aware joins" in new SocialNewsSetup {
//
//    // 8. A Schema Query
//
//    // Attributes of all entities having comments
//    Parent.a.Comment.get.sorted === List(
//      ":comment/author",
//      ":comment/text",
//      ":parent/comment",
//      ":story/title",
//      ":story/url"
//    )
//
//    // Attributes of stories having comments
//    Parent(Story.a.title_).Comment.get.sorted === List(
//      ":parent/comment",
//      ":story/title",
//      ":story/url"
//    )
//
//    // Attributes of comments having a sub-comment
//    Parent(Comment.a.text_).Comment.get.sorted === List(
//      ":comment/author",
//      ":comment/text",
//      ":parent/comment"
//    )
//  }


  "Entities" in new SocialNewsSetup {

//    // 9-11. Finding an entity ID - An implicit Entity
//    // Since we can implicitly convert an entity ID to an entity we'll call the id `editor`
//    val editor: Long = User.e.email_("editor@example.com").get.head
//
//
//    // 12. Requesting an Attribute value
//    editor(":user/firstName") === Some("Ed")
//
//    // Or as query
//    User(editor).firstName.get.head === "Ed"
//
//
//    // 13. Touching an entity
//    // Get all attributes/values of this entity. Sub-component values are recursively retrieved
//    editor.touch === Map(
//      ":db/id" -> 17592186045423L,
//      ":user/email" -> "editor@example.com",
//      ":user/firstName" -> "Ed",
//      ":user/lastName" -> "Itor"
//    )
//
//
//    // 14. Navigating backwards
//
//    // The editors comments (Comments pointing to the Editor entity)
//    editor(":comment/_author") === Some(List(c2, c4, c5, c7, c11))
//
//    // .. almost same as: (here, only matching data is returned)
//    // Comments of editor
//    Comment.e.author_(editor).get.sorted === List(c2, c4, c5, c7, c11)
//
//
//    // 15. Navigating Deeper with entity api
//
//    // Todo: using the entity api like this is clumsy. Find a better way...
//
//    // Comments to the editors comments (with mapping)
//    editor(":comment/_author").get.asInstanceOf[List[Long]]
//      .flatMap(_(":parent/comment")).asInstanceOf[List[Map[String, Any]]]
//      .map(_.head._2) === List(c3, c6, c8, c12)
//
//    // Comments to the editors comments (with `for` loop)
//    (for {
//      editorComment <- editor(":comment/_author").get.asInstanceOf[List[Long]]
//      editorCommentComment <- editorComment(":parent/comment").asInstanceOf[Option[Map[String, Any]]]
//    } yield editorCommentComment.head._2) === List(c3, c6, c8, c12)

    // Comments to the editors comments (with query)


    //    import datomic._
    //    Peer.q( s"""
    //[:find ?d
    //:where
    //   [?a :parent/comment ?b]
    //   [?b :comment/author 17592186045423]
    //   [?a :parent/comment ?d]]
    //           """, conn.db) === 77


    //    (c3, c6, c8, c12) ===(17592186045429L, 17592186045435L, 17592186045439L, 17592186045447L)

//        Parent(Comment.author_(17592186045423L)).comment.debug
//    Parent(Comment.author_(17592186045423L)).comment.get.sorted === List(c3, c6, c8, c12)
    /*
    [:find ?c
     :where
       [?a :comment/author 17592186045423]
       [?a :parent/comment ?c]]

       Warning:scalac: ##30: Dsl2Model
       1          List(
         1          Group(
           1          Bond(,,)
           2          Atom(comment,author_,Long,1,Eq(List(17592186045423L)),None,List()))
         2          Atom(parent,comment,Long,1,VarValue,None,List()))
       ======================================================
     */

    //    (c3, c6, c8, c12) ===(17592186045429L, 17592186045435L, 17592186045439L, 17592186045447L)

        Parent(Parent.Comment.author_(17592186045423L)).comment.debug
    /*
    [:find ?d
     :where
       [?a :parent/comment ?b]
       [?b :comment/author 17592186045423]
       [?a :parent/comment ?d]]

       Warning:scalac: ##30: Dsl2Model
       1          List(
         1          Group(
           1          Bond(,,)
           2          Bond(parent,comment,comment)
           3          Atom(comment,author_,Long,1,Eq(List(17592186045423L)),None,List()))
         2          Atom(parent,comment,Long,1,VarValue,None,List()))
       ======================================================
     */

    //    Comment.e.author_(editor).debug
    //    Parent.comment.Comment.author_(editor).debug
    ////    Parent.e.Comment.author_(editor).Parent_comment.comment.debug
    //    Parent.e.Comment.author_(editor).debug
    //    Parent.e.Comment.apply(Comment.author_(editor)).debug
    //    Parent.comment.apply(Parent.Comment.author_(editor)).debug
    //    Parent.apply(Parent.Comment.e.author_(editor)).comment.debug
    //    Parent(Parent.Comment.e.author_(editor)).comment_.get.sorted === List(c3, c6, c8, c12)

    //    Parent.comment.Comment.author
//    Parent(Comment.author_(editor)).comment.get.sorted === List(c3, c6, c8, c12)

    ok
  }


//  "Time travel" in new SocialNewsSetup {
//
//    // Update Ed's first name
//    m(User(ed).firstName("Edward")).update
//
//
//    // 16. Querying for a Transaction
//    val tx = User(ed).firstName_.tx.get.head
//    tx === 13194139534344L
//
//
//    // 17. Converting Transacting to T
//    val t = Peer.toT(tx)
//    t === 1032
//
//    // Or query for relative system time directly
//    User(ed).firstName_.txT.get.head === t
//
//
//    // 18. Getting a Tx Instant
//    val txInstant = User(ed).firstName_.txInstant.get.last
//
//
//    // 19. Going back in Time
//
//    // Current name
//    User(ed).firstName.asOf(t).get.head === "Edward"
//
//    // Name before change (from previous transaction)
//    User(ed).firstName.asOf(t - 1).get.head === "Ed"
//
//    // We can use the transaction value also
//    User(ed).firstName.asOf(tx).get.head === "Edward"
//    User(ed).firstName.asOf(tx - 1).get.head === "Ed"
//
//
//    // Auditing ................................................................
//
//    // 20. Querying Across All time
//    User(ed).firstName.tx.op.history.get.reverse === List(
//      ("Ed", 13194139534317L, true),
//      ("Ed", 13194139534344L, false),
//      ("Edward", 13194139534344L, true)
//    )
//
//    // 21. Querying Plain Java Data - not supported by Molecule
//  }
}