package molecule.examples.dayOfDatomic
import molecule._
import molecule.dsl.arity._
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.util.MoleculeSpec


class Provenance extends MoleculeSpec {

  "Transaction meta data" in new SocialNewsSetup {

    val ecURL = "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"

    // Add transaction meta data
    val tx1 = Story.title.url.tx_(Source.user_(stu).usecase_("MyUseCase")) insert List(
      ("ElastiCache in 6 minutes", ecURL),
      ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html")
    )
    tx1.ids === List(17592186045449L, 17592186045450L, 13194139534344L)
    val storyId = tx1.id

    storyId === 17592186045449L

    // Ed fixes the spelling error
    Story(storyId).title("ElastiCache in 5 minutes").tx_(Source.user(ed)).update

    // Title now
    Story.url_(ecURL).title.get.head === "ElastiCache in 5 minutes"

    // Title before
    Story.url_(ecURL).title.asOf(tx1.inst).get.head === "ElastiCache in 6 minutes"

    // Who changed the title and when?
    Story.url_(ecURL).title.tx.op.tx_.apply(Source.User.email_).history.get.reverse === List(
      ("ElastiCache in 6 minutes", 13194139534344L, true),
      ("ElastiCache in 6 minutes", 13194139534347L, false),
      ("ElastiCache in 5 minutes", 13194139534347L, true))

    // (un-comment and run test to see data with current date...)
    // Story.e.url_(ecURL).title.tx.txInstant.txAdded.tx(Source.User.email_).history.get.reverse === List(
    //   (17592186045449,ElastiCache in 6 minutes,13194139534344,Sun Nov 30 01:21:55 CET 2014,true),
    //   (17592186045449,ElastiCache in 6 minutes,13194139534347,Sun Nov 30 01:21:55 CET 2014,false),
    //   (17592186045449,ElastiCache in 5 minutes,13194139534347,Sun Nov 30 01:21:55 CET 2014,true))


    // Entire history of Story entity
    Story(storyId).a.v.tx.op.history.get.reverse === List(
      (":story/title", "ElastiCache in 6 minutes", 13194139534344L, true),
      (":story/title", "ElastiCache in 6 minutes", 13194139534347L, false),
      (":story/title", "ElastiCache in 5 minutes", 13194139534347L, true),
      (":story/url", "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html", 13194139534344L, true)
    )
  }
}

