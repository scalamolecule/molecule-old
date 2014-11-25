//package molecule
//package examples.dayOfDatomic.tutorial
////import scala.language.reflectiveCalls
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule._
//import molecule.examples.dayOfDatomic.SocialNewsSchema.{User, Story}
//
//class Provenance extends DayOfAtomicSpec {
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
//}


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