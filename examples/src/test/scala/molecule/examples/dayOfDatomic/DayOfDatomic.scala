package molecule
package examples.dayOfDatomic
import datomic.Peer
import datomic.Util.list
import molecule.examples.dayOfDatomic.dsl.productsOrder._
//import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic.schema._
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
    val List(chocolateId, whiskyId) = Product.description insert("Expensive Chocolate", "Cheap Whisky") ids


    // Insert nested data .................................

    // Model of Order with multiple LineItems
    // 3 LineItem attributes are treated as one Tuple3 of data
    //      val order = m(Order.lineItems(LineItem.product.price.quantity))
    val order = m(Order.LineItems.product.price.quantity)
    //    val order = m(Order.e.LineItems.product.price.quantity)
    //    val order = m(Order(1L).LineItems.product.price.quantity)


    val order2 = Order

    // Make order with two line items and return created entity id
    val orderId = order.insert(List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).ids.last
    //    val orderId = order insert List((1L, chocolateId, 48.00, 1), (1L, whiskyId, 38.00, 2)) last
    //    val orderId = order insert List((1L, chocolateId, 48.00, 1), (1L, whiskyId, 38.00, 2)) last

    //    val orderId1 = order1.insert List (
    //      (chocolateId, 48.00, 1),
    //      (whiskyId, 38.00, 2)
    //      ) last
    //      Order.id("id1").LineItems.product.price.quantity.insert
    //
    //    Order.LineItems.product.price.quantity.insert

    // Find id of order with chocolate
    val orderIdFound = Order.e.LineItems.Product.description_("Expensive Chocolate").get.head
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


  "Query tour + Social News" >> {
    import molecule.examples.dayOfDatomic.dsl.socialNews._

    // http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html

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

    // Created entity ids are simply Long values
    (s1, s2, s3) ===(17592186045418L, 17592186045419L, 17592186045420L)
    (stu, ed) ===(17592186045422L, 17592186045423L)

    // 3. Finding All Users with a first name
    User.firstName.ids === List(stu, ed)

    // 4. Finding a specific user
    User.email("editor@example").ids.head === ed


    // Add comments ..............................

    // Users can:

    // 1. Comment on a Story
    val storyComment = Story.e.Comments.author.text insert

    // 2. Comment on a Comment
    val subComment = Comment.e.Comment.author.text insert

    // Sub-comments form hierarchical trees of Comment nodes having the
    // previous comment as parent and the initial Story Comment as root

    // Insert Stu's first comment to story 1 and return the id of this comment
    val c1 = storyComment(s1, stu, "blah 1") id

    // Ed's Comment to Stu's first Comment
    val c2 = subComment(c1, ed, "blah 2") id

    // More sub-comments
    val c3 = subComment(c2, stu, "blah 3") id
    val c4 = subComment(c3, ed, "blah 4") id

    // Story 2 comments
    val c5 = storyComment(s2, ed, "blah 5") id
    val c6 = subComment(c5, stu, "blah 6") id

    // Story 3 comments
    val c7 = storyComment(s3, ed, "blah 7") id
    val c8 = subComment(c7, stu, "blah 8") id
    // Stu comments on his own comment
    val c9 = subComment(c8, stu, "blah 9") id

    // Story 2 again - a second thread of comments. This time Stu starts
    val c10 = storyComment(s2, stu, "blah 10") id
    val c11 = subComment(c10, ed, "blah 11") id
    val c12 = subComment(c11, stu, "blah 12") id

    // New Comment ids (a second entity is created for each association)
    List(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) === List(
      17592186045425L, 17592186045427L, 17592186045429L, 17592186045431L, 17592186045433L, 17592186045435L,
      17592186045437L, 17592186045439L, 17592186045441L, 17592186045443L, 17592186045445L, 17592186045447L
    )

    // 5. Finding a User's Comments
    Comment.e.Author.email_("editor@example").get.sorted === List(c2, c4, c5, c7, c11)

    Comment.e.text.Author.email_("editor@example").firstName.tpls === List(
      (c2, "blah 2", "Ed"),
      (c4, "blah 4", "Ed"),
      (c5, "blah 5", "Ed"),
      (c7, "blah 7", "Ed"),
      (c11, "blah 11", "Ed")
    )


    // 6. Returning an Aggregate of Comments of some Author
    Comment(count).Author.email_("editor@example").get.head === 5

    // Or we could read the size of the (un-aggregated) result set of Comment entity ids
    Comment.e.Author.email("editor@example").size === 5
    Comment.Author.email("editor@example").size === 5


    // 7. Have people commented on other people? (Multiple joins)

    // Molecule - or any database system - won't prevent us from programmatically inserting an
    // id for for an unintentional entity. We could for instance have referenced the id of Ed
    // by mistake instead of supplying an id of a story:
    val unintentionalComment = storyComment(ed, stu, "blah 13") id

    // With Molecule you can express the intentions of your domain with the words of your
    // domain so that such unintentional mistakes are more unlikely to happen. Our
    // definition of our insert molecule `storyComment` clearly expects a Story id.

    // If one needs to find out if our database has been polluted with unintentional
    // references one would have to explore this with Datalog queries since Molecule only
    // supports intentional query combinations.


    // Schema-aware joins .........................

    // 8. A Schema Query

    // Attributes of stories having comments
    Story.a.Comments.text_.get === List(
      ":story/title",
      ":story/url"
    )

    // Attributes of comments having a sub-comment
    Comment.a.Comment.text_.get === List(
      ":comment/text",
      ":comment/author",
      ":comment/tree_"
    )


    // Entities ...................................

    // 9-11. Finding an entity ID - An implicit Entity
    // Since we can implicitly convert an entity ID to an entity we'll call the id `editor`
    val editor = User.e.email_("editor@example.com").get.head

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
    // Comments of editor
    Comment.e.author_(editor).get === List(c2, c4, c5, c7, c11)

    // 15. Navigating Deeper
    // The editors comments' comments
    editor(":comment/_author")(":comment/tree_") === List(c6, c8, c12)
    Comment.author_(editor).Comment.e.get === List(c6, c8, c12)

    // Comments that Editor commented on
    Comment.e.Comment.author_(editor).get === List(c6, c8, c12)


    // Time travel ....................................

    // 16. Querying for a Transaction
    val tx = User(ed).firstName_.txInstant

    // 17. Converting Transacting to T
    val t = Peer.toT(tx)

    // Query for relative system time
    User(ed).firstName_.txT === t

    // 18. Getting Tx Instant
    val txInstant = User(ed).firstName_.txInstant.get.last

    // 19. Going back in Time
    User(ed).firstName.asOf(t - 1).get.head === "Ed"


    // Auditing .......................................

    // 20. Querying Across All time
    User(ed).firstName.txT.txAdded.history.tpls === List(
      ("Ed", 123456789L, true),
      ("Ed", 123456789L, false),
      ("Edward", 123456789L, true)
    )

    // 21. Querying Plain Java Data
    // Not supported by Molecule


    // Social News =======================================================================================

    // (Adding what the Query Tour hasn't already covered)

    // Add underscore to attribute name to _not_ return it's value (and keep it as a search attribute)
    // Here we get all Story ids (entities having a url value)
    val allStories = Story.url_.e.get

    // Add John and let him upvote all stories
    val john = User.email.firstName.lastName.upVotes insert List(
      ("john@example.com", "John", "Doe", allStories.toSet)
    ) id

    // Update John's first name
    User(john).firstName("Jonathan").update
    // or
    User.firstName("Jonathan").update(john)

    // John regrets upvoting Paul Graham story (`s3`)
    User(john).upVotes.remove(s3).update

    // John now has only 2 upvotes
    User(john).upVotes.get.head.size === 2

    // John skips all upvotes
    User(john).upVotes().update

    // John has no upvotes any longer
    User(john).upVotes.get.head.size === 0


    // Let Stuart upvote a story
    User(stu).upVotes(s1).update

    // How many users are there?
    User.email.size === 3

    // How many users have upvoted something? (Stuart)
    User.email.upVotes_.get.head.size === 1

    // Users and optional upvotes
    // Cardinality many attribute upVotes might return an empty set
    User.email.upVotes.tpls === List(
      ("stuarthalloway@datomic.com", Set(s1)),
      ("editor@example", Set()),
      ("john@example.com", Set())
    )


    // Provenance =======================================================================================

    val ecURL = "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"

    // Add data associated with transaction data
    val tx1 = Story.title.url.tx(Source.user_(stu)) insert List(
      ("ElastiCache in 6 minutes", ecURL),
      ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html")
    )
    val storyId = tx1.id

    // Ed fixes the spelling error
    Story(storyId).title("ElastiCache in 5 minutes").tx(Source.user(ed)) update

    // Title now
    Story.url_(ecURL).title.get.head === "ElastiCache in 5 minutes"

    // Title before
    Story.url_(ecURL).title.asOf(tx1.inst).get.head === "ElastiCache in 6 minutes"

    // Who changed the title and when?
    Story.url_(ecURL).title.txInstant.txAdded.tx(User.email).history.tpls === List(
      ("5 feb", true, "ed@itor.com")
    )

    // Entire history of story entity
    Story(storyId).a.v.txInstant.txAdded.history.tpls === List(
      ("title", "ElastiCache in 6 minutes", "date...", true)
    )
  }


  "Binding" >> {
    import molecule.examples.dayOfDatomic.dsl.socialNews._
    implicit val conn = load(SocialNewsSchema.tx, "Binding")

    // Input molecules
    val person = m(User.firstName(?).lastName(?))
    val firstN = m(User.firstName(?))

    // Bind vars
    person("John", "Doe").tpl(1) ===("John", "Doe")

    // Bind tuple
    person(("John", "Doe")).tpl(1) ===("John", "Doe")

    // Bind collection
    firstN(List("John", "Jane", "Phineas")).get === List("John", "Jane", "Phineas")

    // Bind relation
    person(List(("John", "Doe"), ("Jane", "Doe"))).tpls === List(("John", "Doe"), ("Jane", "Doe"))


    // Binding queries

    // Find all the Stewart first names
    firstN("Stewart") === List()

    // Find all the Stewart or Stuart first names
    firstN("Stewart", "Stuart") === List()

    // Find all the Stewart/Stuart as either first name or last name
    person(("Stewart", "Stuart"), ("Stewart", "Stuart")) === List()

    // Find only the Smalley Stuarts
    person("Stewart", "Stuart") === List()

    // Same query above, but with map (tuple) form
    person(("Stewart", "Stuart")) === List()
  }


  "Graph" >> {
    import molecule.examples.dayOfDatomic.dsl.graph._
    implicit val conn = load(GraphSchema.tx, "Graph")

    // See http://docs.neo4j.org/chunked/stable/cypher-cookbook-hyperedges.html

    // User 1's Role in Group 2
    User.name_("User1").RoleInGroup.Group.name_("Group2")._RoleInGroup.Role.name.get.head === "Role1"

    // User 1's Roles in all Groups
    User.name_("User1").RoleInGroup.Role.name._RoleInGroup.Group.name.tpls === List(
      ("Role1", "Group2"),
      ("Role2", "Group1")
    )
  }


  "Aggregates" >> {
    import molecule.examples.dayOfDatomic.dsl.aggregates._
    implicit val conn = load(AggregatesSchema.tx, "Aggregates")

    // Insert data
    Obj.name.meanRadius
      .tx(Data.source_("http://en.wikipedia.org/wiki/List_of_Solar_System_objects_by_size")) insert Seq(
      ("Sun", 696000.0),
      ("Jupiter", 69911.0),
      ("Saturn", 58232.0),
      ("Uranus", 25362.0),
      ("Neptune", 24622.0),
      ("Earth", 6371.0),
      ("Venus", 6051.8),
      ("Mars", 3390.0),
      ("Ganymede", 2631.2),
      ("Titan", 2576.0),
      ("Mercury", 2439.7),
      ("Callisto", 2410.3),
      ("Io", 1821.5),
      ("Moon", 1737.1),
      ("Europa", 1561.0),
      ("Triton", 1353.4),
      ("Eris", 1163.0)
    )

    // How many objects
    Obj.name(count) === 17
    Obj.name.size === 17

    // Largest radius
    Obj.meanRadius(max) === 696000.0
    Obj.meanRadius.get.max === 696000.0

    // Smallest radius
    Obj.meanRadius(min) === 696000.0
    Obj.meanRadius.get.min === 696000.0

    // Average radius
    Obj.meanRadius(avg) === 696000.0

    // Median radius
    Obj.meanRadius(median) === 696000.0

    // Standard deviation
    Obj.meanRadius(stddev) === 696000.0

    // random solar system object
    Obj.meanRadius(rand) === 696000.0

    // Smallest 3
    Obj.meanRadius(min(3)) === List(1, 2, 3)
    Obj.meanRadius.get.sorted.take(3) === List(1, 2, 3)

    // Largest 3
    Obj.meanRadius(max(3)) === List(1, 2, 3)
    Obj.meanRadius.get.sorted.reverse.take(3) === List(1, 2, 3)

    // 5 random (duplicates possible)
    Obj.meanRadius(rand(5)) === List(1, 2, 3, 4, 5)

    // 5 samples (no duplicates)
    Obj.meanRadius(sample(5)) === List(1, 2, 3, 4, 5)

    // What is the average length of a schema name?
    Obj.e.
      m(e.ident.name(count)).avg

    // ...and the mode(s) ?
    m(e.ident.name(count)).modes

    // How many attributes and value types does this; schema use ?
    m(e(count).ident(countDistinct))
  }


  "Attribute groups" >> {
    implicit val conn = init("Attribute groups", "social-news.edn")

    // Find all attributes in the story namespace
    m(e.valueType.ident.ns("story")).get(e)

    // Create a reusable rule
    val rules = m(e.valueType.ident.ns(?))

    // Find all attributes in story namespace, using the rule
    rules("story").get(a)
    m(a.rules("story"))

    // Find all entities possessing *any* story attribute
    m(e.a(rules("story")))
  }
}

