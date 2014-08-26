package molecule
package examples.dayOfDatomic
import datomic.Peer
import datomic.Util.list
import molecule.examples.dayOfDatomic.dsl.productsOrder._
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic.schema.{ProductsOrderSchema, SocialNewsSchema}
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
import scala.collection.JavaConversions._
import scala.language.existentials

class DayOfDatomic extends DayOfAtomicSpec {

  "Hello World" >> {

    // Transaction input is data
    val tempid = Peer.tempid(":db.part/user")
    val txData = list(list(
      ":db/add", tempid,
      ":db/doc", "Hello world"))
    val conn = load(txData, "hello-world")

    // Transaction result is data
    val txresult = conn.transact(txData)
    txresult.get().toString.take(26) === "{:db-before datomic.db.Db@" // etc...

    // Database is a value
    val dbValue = conn.db()

    // Query input is data
    val qresult = Peer.q( """[:find ?e :where [?e :db/doc "Hello world"]]""", dbValue)

    // Query result is data
    val id = qresult.toList.head.head

    // Entity is a navigable view over data
    dbValue.entity(id).get(":db/id") === 17592186045417L

    // Schema itself is data
    dbValue.entity(":db/doc").get(":db/id") === 62
  }


  "ProductsAndOrders (nested data)" >> {

    // See: http://blog.datomic.com/2013/06/component-entities.html

    // Make db
    implicit val conn = load(ProductsOrderSchema.tx, "Orders")

    // Insert 2 products
    val List(chocolateId, whiskyId) = Product.description.insert("Expensive Chocolate", "Cheap Whisky")


    // Insert nested data .................................

    // Model of Order with multiple LineItems
    // 3 LineItem attributes are treated as one Tuple3 of data
    val order = m(Order.lineItems(LineItem.product.price.quantity))


    val order2 = Order

    // Make order with two line items and return created entity id
    val orderId = order insert List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2)) last

    //    val orderId1 = order1.insert List (
    //      (chocolateId, 48.00, 1),
    //      (whiskyId, 38.00, 2)
    //      ) last
    //      Order.id("id1").LineItems.product.price.quantity.insert
    //
    //    Order.LineItems.product.price.quantity.insert

    // Find id of order with chocolate
    val orderIdFound = Order.eid.LineItems.Product.description_("Expensive Chocolate").get.head
    orderIdFound === orderId


    // Touch entity ................................

    // Get all attributes/values of this entity. Sub-component values are recursively retrieved
    orderId.touch === Map(
      ":db/id" -> 17592186045423L,
      ":order/lineItems" -> List(
        Map(
          ":db/id" -> 17592186045421L,
          ":lineItem/product" -> List(Map(":db/id" -> chocolateId, ":product/description" -> "Expensive Chocolate")),
          ":lineItem/quantity" -> 1,
          ":lineItem/price" -> 48.0),
        Map(
          ":db/id" -> 17592186045422L,
          ":lineItem/product" -> List(Map(":db/id" -> whiskyId, ":product/description" -> "Cheap Whisky")),
          ":lineItem/quantity" -> 2,
          ":lineItem/price" -> 38.0)
      ))

    // Retract nested data ............................

    // Retract entity - all subcomponents/lineItems are retracted
    orderId.retract

    // The products are still there
    Product.description("Expensive Chocolate" or "Cheap Whisky").ids === List(chocolateId, whiskyId)
  }


  "Query tour" >> {

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
    val List(stu, ed) = User.firstName.lastName.email insert List(
      ("stu", "Halloway", "stuarthalloway@datomic.com"),
      ("ed", "Itor", "editor@example")
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
    // a) "Comments of Ed"
    //    sql-style: "find Comment where email = "editor@example"
    Comment.eid.Author.email_("editor@example").get.sorted === List(c2, c4, c5, c7, c11)
    // b) "Ed's Comments"
    //    Using a "back-reference" from user to the comments he/she wrote
    User.email_("editor@example")._AuthorComment.eid.get.sorted === List(c2, c4, c5, c7, c11)

    User.email_("editor@example")._authorComment.get.sorted === List(c2, c4, c5, c7, c11)

    User.email_("editor@example").firstName._authorComment.tpls.head === List(
      ("Ed", Set(c2, c4, c5, c7, c11))
    )

    m(User.email_("editor@example").firstName._AuthorComment.eid.text).tpls === List(
      ("Ed", c2, "blah 2"),
      ("Ed", c4, "blah 4"),
      ("Ed", c5, "blah 5"),
      ("Ed", c7, "blah 7"),
      ("Ed", c11, "blah 11")
    )

    User.email_("editor@example").firstName._author(Comment.eid.text.author).tpls.head === List(
      ("Ed", List((c2, "blah 2"), (c4, "blah 4"), (c5, "blah 5"), (c7, "blah 7"), (c11, "blah 11")))
    )


    // 6. Returning an Aggregate of Comments of some Author
    Comment(count).Author.email_("editor@example").get.head === 5
    User.email_("editor@example")._authorComment.size === 5

    // Or we could read the size of the (un-aggregated) result set of Comment entity ids
    Comment.eid.Author.email("editor@example").size === 5
    Comment.Author.email("editor@example").size === 5


    // 7. Have people commented on other people? (Multiple joins)

    // Imagine we accidentally supplied a user-id (`ed`) as a parent id to our storyComment insert molecule:
    val c13 = storyComment(ed, stu, "blah 13") head

    // We don't want Comment on Users, so let's see if we got some by mistake:

    // Find Comments to supposed Stories (but Users in reality)
    // `User.email` used to find entities with a User.email attribute (== User entities)
    Comment.text._Story(User.email)
    // Any Sub-Comments accidentally about Users?
    Comment.text._parent(User.email)


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
    Comment.eid.author(editor).get === List(c2, c4, c5, c7, c11)
    // Or navigating the relationship in the opposite direction
    User(editor)._Comments.eid.get === List(c2, c4, c5, c7, c11)


    // 15. Navigating Deeper
    // The editors comments' comments
    editor(":comment/_author")(":comment/tree_") === List(c6, c8, c12)

    Comment.author(editor).Comment.eid.get === List(c6, c8, c12)

    User(editor)._Comments.Comment.eid.get === List(c6, c8, c12)


    // Time travel ....................................

    // 16. Querying for a Transaction
    val tx = User(ed).firstName_.tx

    // 17. Converting Transacting to T
    val t = Peer.toT(tx)

    // Query for relative system time
    User(ed).firstName_.t === t

    // 18. Getting Tx Instant
    val txInstant = User(ed).firstName_.txInstant.get.last

    // 19. Going back in Time
    User(ed).firstName.asOf(t - 1).get.head === "Ed"


    // Auditing .......................................





//
//    Orchestra.name("GSO").Musicians.name
//
//    Musician.name.playsIn(Orchestra.name("GSO")).plays(Instruments.Strings.DoubleBass)

  }


  //  "Social news" >> {
  //
  //    //    // Get ids from inserted users
  //    //    val ids = User.firstName.lastName.email.insert(List(
  //    //      ("stu", "Halloway", "stuarthalloway@datomic.com"),
  //    //      ("ed", "Itor", "editor@example")
  //    //    ))
  //    //    val (stu, ed) = (ids(0), ids(1))
  //    //
  //    //    User.firstName.ids === List(stu, ed)
  //    //    User.firstName.ids === List(17592186045422L, 17592186045423L)
  //    //
  //    //    // Finding a specific user
  //    //    User.email("editor@example").ids.head === ed
  //    //
  //    //    // Finding a users comments
  //    //    Comment.eid.User.email("editor@example").get === List(ed, stu)
  //
  //
  //
  //    ok
  //
  //
  //    //    implicit val conn = init("social-news", "social-news.edn")
  //
  //    //    val threeStories = Story.url
  //    //    val johnData = User.email("john@example.com").firstName("John").lastName("Doe")
  //    //
  //    //    add(johnData UpVotes threeStories)
  //    //
  //    //    change(User
  //    //      .email("john@example.com") // find User by unique identity `email`
  //    //      .firstName("Johnathan") // new data
  //    //    )
  //    //
  //    //    val john = User.email("john@example.com")
  //    //
  //    //    // John upvotes Paul Graham story
  //    //    val story = save(john UpVotes Story.url("http://www.paulgraham.com/avg.html")).get(Story)
  //    //    m(john.UpVotes).count === 3
  //    //
  //    //    // John regrets last upvote
  //    //    retract(john UpVotes story)
  //    //    m(john.UpVotes).count === 2
  //    //
  //    //    // John regrets all upvotes
  //    //    retract(john.UpVotes)
  //    //    m(john.UpVotes).count === 0
  //    //
  //    //    // Todo: 10 new users, some with upvotes
  //    //
  //    //
  //    //    generateUsersWithSomeUpvotes(conn, "user", 10)
  //    //
  //    //    // How many users now?
  //    //    m(User).count === 11 // including John
  //    //
  //    //    // How many upvoters? (UpVotes mandatory)
  //    //    m(User.UpVotes).count
  //    //
  //    //    // Users and optional upvotes
  //    //    m(User.UpVotes(maybe)).get
  //  }
  //
  //  "Provenance" >> {
  //    implicit val conn = init("provenance", "social-news.edn", "provenance.edn")
  //
  //    val ecURL = "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"
  //
  //    // Add data
  //    val tx1 = add(Story.title.url)(List(
  //      ("ElastiCache in 6 minutes", ecURL),
  //      ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html")),
  //      User.email("stuarthalloway@datomic.com"))
  //
  //    // Change (upsert) data
  //    change(
  //      Story
  //        .url(ecURL) // find Story by unique identity `url`
  //        .title("ElastiCache in 5 minutes"), // new data
  //      User.email("editor@example.com") // tx data
  //    )
  //
  //    change(Story(ecURL).title("ElastiCache in 5 minutes"), User.email("editor@example.com"))
  //
  //    // Title now
  //    m(Story.url(ecURL).title).get === "ElastiCache in 5 minutes"
  //    m(Story(ecURL).title).get === "ElastiCache in 5 minutes"
  //
  //    // Title before
  //    m(Story.url(ecURL).title).asOf(tx1.inst).get === "ElastiCache in 6 minutes"
  //
  //    m(Story(ecURL).title).asOf(tx1.inst).get === "ElastiCache in 6 minutes"
  //
  //    // Who changed the title and when?
  //    val storyChanges = m(Story
  //      .url(ecURL)
  //      .title.Tx.inst.added // get title changes
  //      .User.email // get who
  //    ).get
  //
  //    println("Story changes:\n" + storyChanges)
  //
  //    // Entity history of ElastiCache entity
  //    m(e(storyChanges.ent) // find entity
  //      .a // attribute
  //      .v // value
  //      .tx.added.inst // added, instant
  //    ).history
  //  }
  //
  //
  //  "Binding" >> {
  //    implicit val conn = init("binding")
  //
  //    // Bind vars
  //    m(User.firstName(?).lastName(?))("John", "Doe").get === List("John", "Doe")
  //
  //    // Bind tuples
  //    m(User.firstName(?).lastName(?))(("John", "Doe")).get === ("John", "Doe")
  //
  //    // Bind a collection
  //    m(User.firstName(?))(List("John", "Jane", "Phineas")).get === List("John", "Jane", "Phineas")
  //
  //    // Bind
  //    m(User.firstName(?))(List(("John", "Doe"), ("Jane", "Doe"))).get === ("John", "Doe")
  //  }
  //
  //
  //  "Binding queries" >> {
  //    implicit val conn = init("Attribute groups", "social-news.edn")
  //
  //    // Find all the Stewart first names
  //    m(User.firstName(?))("Stewart")
  //
  //    // Find all the Stewart or Stuart first names
  //    m(User.firstName(?))("Stewart", "Stuart")
  //
  //    // Find all the Stewart/Stuart as either first name or last name
  //    m(User.firstName(?).lastName(?))(("Stewart", "Stuart"),("Stewart", "Stuart"))
  //
  //    // Find only the Smalley Stuarts
  //    m(User.firstName(?).lastName(?))("Stewart", "Stuart")
  //
  //    // Same query above, but with map (tuple) form
  //    m(User.firstName(?).lastName(?))(("Stewart", "Stuart"))
  //  }
  //
  //
  //  "Graph" >> {
  //    implicit val conn = init("graph", "graph.edn")
  //
  //    // Roles of User1 in Group2
  //    m(User("User1").Groups("Group2").Roles)
  //
  //    // Roles of User1 in all groups
  //    m(User("User1").Groups.Roles)
  //  }
  //
  //
  //  "Aggregates" >> {
  //    implicit val conn = init("aggregates", "bigger-than-pluto.edn")
  //
  //    // how many objects are there?
  //    m(Obj).count
  //
  //    // largest radius?
  //    m(Obj.meanRadius(max))
  //
  //    // Smallest radius
  //    m(Obj.meanRadius(min))
  //
  //
  //    // Average radius
  //    m(Obj.meanRadius(avg))
  //
  //    // Median radius
  //    m(Obj.meanRadius(median))
  //
  //    // stddev
  //    m(Obj.meanRadius(stddev))
  //
  //    // random solar system object
  //    m(Obj(rand))
  //    // or
  //    m(Obj).rand
  //
  //    // smallest 3
  //    m(Obj.meanRadius min 3)
  //    m(Obj.meanRadius).asc.take(3)
  //
  //    // largest 3
  //    m(Obj.meanRadius max 3)
  //    m(Obj.meanRadius).desc.take(3)
  //
  //    // 5 random (duplicates possible)
  //    m(Obj(rand, 5))
  //
  //    // Choose 5, no duplicates
  //    m(Obj(sample, 5))
  //
  //    // What is the average length of a schema name?
  //    m(e.ident.name(count)).avg
  //
  //    // ...and the mode(s) ?
  //    m(e.ident.name(count)).modes
  //
  //    // How many attributes and value types does this; schema use ?
  //    m(e(count).ident(countDistinct))
  //  }
  //
  //
  //  "Attribute groups" >> {
  //    implicit val conn = init("Attribute groups", "social-news.edn")
  //
  //    // Find all attributes in the story namespace
  //    m(e.valueType.ident.ns("story")).get(e)
  //
  //    // Create a reusable rule
  //    val rules = m(e.valueType.ident.ns(?))
  //
  //    // Find all attributes in story namespace, using the rule
  //    rules("story").get(a)
  //    m(a.rules("story"))
  //
  //    // Find all entities possessing *any* story attribute
  //    m(e.a(rules("story")))
  //  }
  //
  //  "Components" >> {
  //
  //    // See Components.groovy
  //
  //
  //
  //  }
}

