package molecule.examples.dayOfDatomic

import datomic.Peer
import datomic.Util.list
import molecule._
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
import scala.collection.JavaConversions._
import scala.language.existentials

// http://blog.datomic.com/2013/05/a-whirlwind-tour-of-datomic-query_16.html

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


  "ProductsAndOrders (components)" >> {

    // See: http://blog.datomic.com/2013/06/component-entities.html

    import molecule.examples.dayOfDatomic.dsl.productsOrder._
    import molecule.examples.dayOfDatomic.schema.ProductsOrderSchema

    // Make db
    implicit val conn = load(ProductsOrderSchema.tx)

    // Insert 2 products
    val List(chocolateId, whiskyId) = Product.description.insert("Expensive Chocolate", "Cheap Whisky")

    // Model of order with multiple line items
    // One-to-Many relationship where line items are subcomponents of the order
    //    val order = m(Order.lineItems(LineItem.product.price.quantity))
    val order = m(Order * LineItem.product.price.quantity)

    // Make order with two line items and return created entity id
    val orderId = order insert List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2)) last

    // Find id of order with chocolate
    val orderIdFound = Order.eid.LineItems.Product.description("Expensive Chocolate").get.head

    orderId === orderIdFound

    // Touch entity
    // Get all attributes/values of this entity subcomponent values are recursively retrieved
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






  }

  //    val order1 = m(Order.lineItems)
  //    val order2 = m(Order.LineItems.Product.description("hej"))
  //    val order = m(Order * LineItem.product.price.quantity)


  //
  //    "Query tour" >> {
  //      import molecule.examples.dayOfDatomic.dsl.socialNews._
  //      import molecule.examples.dayOfDatomic.schema.SocialNewsSchema
  //
  //      // Make db
  //      implicit val conn = load(SocialNewsSchema.tx)
  //
  //      // Add data -----------------------------------------------
  //
  //      // Stories
  //      val List(s1, s2, s3) = Story.title.url.insert(List(
  //        ("Teach Yourself Programming in Ten Years", "http://norvig.com/21-days.html"),
  //        ("Clojure Rationale", "http://clojure.org/rationale"),
  //        ("Beating the Averages", "http://www.paulgraham.com/avg.html")
  //      ))
  //
  //      // Users
  //      val List(stu, ed) = User.firstName.lastName.email.insert(List(
  //        ("stu", "Halloway", "stuarthalloway@datomic.com"),
  //        ("ed", "Itor", "editor@example")
  //      ))
  //
  //      // Created entity ids are simply Long values
  //      (s1, s2, s3) ===(17592186045418L, 17592186045419L, 17592186045420L)
  //      (stu, ed) ===(17592186045422L, 17592186045423L)
  //
  //
  //          // Each story has one or more threads of comments so that the
  //          // - first comment of a has story as parent
  //          // - subsequent comments has the previous comment as parent
  //          // When retracting a story, all comments will be recursively retracted
  //
  //          // We use a "template molecule" as a model for inserting data
  //          val comment = m(Comment.body.Parent.eid.Author.eid)
  //
  //          // Story 1 comments
  //          val c1 = comment.insert("blah 1", s1, stu).head // get created comment entity id
  //          val c2 = comment.insert("blah 2", c1, ed).head
  //          val c3 = comment.insert("blah 3", c2, ed).head
  //          val c4 = comment.insert("blah 4", c3, stu).head
  //
  //          // Story 2 comments
  //          val c5 = comment.insert("blah 5", s2, ed).head
  //          val c6 = comment.insert("blah 6", c5, stu).head
  //
  //          // Story 3 comments
  //          val c7 = comment.insert("blah 7", s3, ed).head
  //          val c8 = comment.insert("blah 8", c7, stu).head
  //          val c9 = comment.insert("blah 9", c8, stu).head
  //
  //          // Story 2 again - a second thread of comments. This time Stu starts
  //          val c10 = comment.insert("blah 10", s2, stu).head
  //          val c11 = comment.insert("blah 11", c10, ed).head
  //          val c12 = comment.insert("blah 12", c11, stu).head
  //
  //          //    // We use a "template molecule" as a model for inserting data
  //          //    val comment = m(Comment.body.Context.Author)
  //          //
  //          //    // Story 1 comments
  //          //    val c1 = comment.insert("blah 1", s1, stu).head // get created comment entity id
  //          //    val c2 = comment.insert("blah 2", c1, ed).head
  //          //    val c3 = comment.insert("blah 3", c2, ed).head
  //          //    val c4 = comment.insert("blah 4", c3, stu).head
  //          //
  //          //    // Story 2 comments
  //          //    val c5 = comment.insert("blah 5", s2, ed).head
  //          //    val c6 = comment.insert("blah 6", c5, stu).head
  //          //
  //          //    // Story 3 comments
  //          //    val c7 = comment.insert("blah 7", s3, ed).head
  //          //    val c8 = comment.insert("blah 8", c7, stu).head
  //          //    val c9 = comment.insert("blah 9", c8, stu).head
  //          //
  //          //    // Story 2 again - a second thread of comments. This time Stu starts
  //          //    val c10 = comment.insert("blah 10", s2, stu).head
  //          //    val c11 = comment.insert("blah 11", c10, ed).head
  //          //    val c12 = comment.insert("blah 12", c11, stu).head
  //
  //
  //          // Queries -----------------------------------------------
  //
  //          // Find all users
  //          User.firstName.ids === List(ed, stu)
  //
  //          // Finding a specific user
  //          User.email("editor@example").ids.head === ed
  //
  //          // "Find a User's Comments"
  //          // Email value is not returned since it's constant across the result set
  //          // Only eid (for "entity id") of comments are returned in this case:
  //          User.email("editor@example").Comment.eid.get === List(17592186045422L)
  //      //    Author.email("editor@example").Comment.eid.get.head === 17592186045422L
  //          // Note how "Author" is like an alias for the User namespace
  //          // That's why we can build `Author.email` since email is in the User namespace
  //
  //          // Since relationships are bi-directional we could also ask the other way around:
  //          // "Find Comments of a User"
  //          Comment.eid.Author.email("editor@example").get.head === 17592186045422L
  //
  //
  //      ok
  //    }
  //
  //
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


//    lazy val tx = Util.list(
//      Util.map(":db/id", Peer.tempid(":db.part/user"),
//        ":order/lineItems", Util.list(
//          Util.map(":lineItem/product", chocolateId: java.lang.Long,
//            ":lineItem/price", 48.00.asInstanceOf[Object],
//            ":lineItem/quantity", 1.asInstanceOf[Object]),
//          Util.map(":lineItem/product", whiskyId: java.lang.Long,
//            ":lineItem/price", 38.00.asInstanceOf[Object],
//            ":lineItem/quantity", 2.asInstanceOf[Object])
//        )))

//    val id1 = Peer.tempid(":db.part/user")
//    val id2 = Peer.tempid(":db.part/user")
//    val id3 = Peer.tempid(":db.part/user")

//    val tx21 = s"""List(
//            |  List(  :db/add,   $id1,   :lineItem/product      ,   ${chocolateId: java.lang.Long}      )
//            |  List(  :db/add,   $id1,   :lineItem/price        ,   ${48.00.asInstanceOf[Object]}       )
//            |  List(  :db/add,   $id1,   :lineItem/quantity     ,   ${1.asInstanceOf[Object]}           )
//            |
//            |  List(  :db/add,   $id2,   :lineItem/product      ,   ${whiskyId: java.lang.Long}         )
//            |  List(  :db/add,   $id2,   :lineItem/price        ,   ${38.00.asInstanceOf[Object]}       )
//            |  List(  :db/add,   $id2,   :lineItem/quantity     ,   ${2.asInstanceOf[Object]}           )
//            |
//            |  List(  :db/add,   $id3,   :order/lineItems       ,   $id1           )
//            |  List(  :db/add,   $id3,   :order/lineItems       ,   $id2           )
//            |)""".stripMargin

//    val tx2 = Util.list(
//      Util.list(":db/add", id1, ":lineItem/product", chocolateId: java.lang.Long),
//      Util.list(":db/add", id1, ":lineItem/price", 48.00.asInstanceOf[Object]),
//      Util.list(":db/add", id1, ":lineItem/quantity", 1.asInstanceOf[Object]),
//      Util.list(":db/add", id2, ":lineItem/product", whiskyId: java.lang.Long),
//      Util.list(":db/add", id2, ":lineItem/price", 38.00.asInstanceOf[Object]),
//      Util.list(":db/add", id2, ":lineItem/quantity", 2.asInstanceOf[Object]),
//      Util.list(":db/add", id3, ":order/lineItems", id1),
//      Util.list(":db/add", id3, ":order/lineItems", id2)
//    )
//    conn.transact(tx2).get()
//    Map(
//      :db/id -> 17592186045421,
//      :lineItem/product -> List(
//        Map(
//          :db/id -> 17592186045418,
//          :product/description -> Expensive Chocolate)),
//    :lineItem/quantity -> 1, :lineItem/price -> 48.0)

//    orderId.touch === Map(
//      ":db/id" -> 17592186045421L,
//      ":order/lineItems" -> List(
//        Map(
//          ":db/id" -> 17592186045422L,
//          ":lineItem/product" -> List(Map(":db/id" -> 17592186045418L, ":product/description" -> "Expensive Chocolate")),
//          ":lineItem/quantity" -> 1,
//          ":lineItem/price" -> 48.0),
//        Map(
//          ":db/id" -> 17592186045423L,
//          ":lineItem/product" -> List(Map(":db/id" -> 17592186045419L, ":product/description" -> "Cheap Whisky")),
//          ":lineItem/quantity" -> 2,
//          ":lineItem/price" -> 38.0)
//      ))





















