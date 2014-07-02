//package molecule.examples.dayOfDatomic
//
////import scala.language.reflectiveCalls
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule.examples.dayOfDatomic.SocialNewsSchema.{User, Story}
//import molecule.examples.dayOfDatomic.samples._
//import datomic.Peer
//import datomic.Util._
//import molecule._
//
//class DayOfDatomic extends DayOfAtomicSpec with Generators {
//
//  "Hello World" >> {
//    implicit val conn = init("hello-world")
//
//    // Transaction input is data
//    val tempid = Peer.tempid(":db.part/user")
//    val txresult = conn.transact(list(list(":db/add", tempid, ":db/doc", "Hello world")))
//
//    // Transaction result is data
//    txresult.get().toString.take(26) === "{:db-before datomic.db.Db@" // etc...
//
//    val dbVal = conn.db()
//
//    // Query input is data
//    val qresult = Peer.q( """[:find ?e :where [?e :db/doc "Hello world"]]""", dbVal)
//
//    // Query result is data
//    dbVal.entity(qresult.toList.head.head).get(":db/id") === 17592186045417L
//
//    // Schema itself is data
//    dbVal.entity(":db/doc").get(":db/id") === 61
//  }
//
//  "Social news" >> {
//    implicit val conn = init("social-news", "social-news.edn")
//
//    val threeStories = Story.url
//    val johnData = User.email("john@example.com").firstName("John").lastName("Doe")
//
//    add(johnData UpVotes threeStories)
//
//    change(User
//      .email("john@example.com") // find User by unique identity `email`
//      .firstName("Johnathan") // new data
//    )
//
//    val john = User.email("john@example.com")
//
//    // John upvotes Paul Graham story
//    val story = save(john UpVotes Story.url("http://www.paulgraham.com/avg.html")).get(Story)
//    m(john.UpVotes).count === 3
//
//    // John regrets last upvote
//    retract(john UpVotes story)
//    m(john.UpVotes).count === 2
//
//    // John regrets all upvotes
//    retract(john.UpVotes)
//    m(john.UpVotes).count === 0
//
//    // Todo: 10 new users, some with upvotes
//
//
//    generateUsersWithSomeUpvotes(conn, "user", 10)
//
//    // How many users now?
//    m(User).count === 11 // including John
//
//    // How many upvoters? (UpVotes mandatory)
//    m(User.UpVotes).count
//
//    // Users and optional upvotes
//    m(User.UpVotes(maybe)).get
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
//}