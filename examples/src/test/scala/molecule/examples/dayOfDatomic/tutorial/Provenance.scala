package molecule
package examples.dayOfDatomic.tutorial
import molecule._
import molecule.examples.dayOfDatomic.SocialNewsSetup
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec


class Provenance extends DayOfAtomicSpec {

  "Provenance" in new SocialNewsSetup {

    val ecURL = "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"

    // Add transaction meta data
//    Story.title.url.tx(Source.user)._model === 7
    Story.title.url.tx(Source.user_(123L))._model === 7
//    Story.title.url.tx(Source.user_(stu))._model === 7
//    val tx1     = Story.title.url.tx(Source.user_(stu)) insert List(
//      ("ElastiCache in 6 minutes", ecURL),
//      ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html")
//    )
//    val storyId = tx1.id
//
//    // Ed fixes the spelling error
//    Story(storyId).title("ElastiCache in 5 minutes").tx(Source.user(ed)).update
//
//    // Title now
//    Story.url_(ecURL).title.get.head === "ElastiCache in 5 minutes"
//
//    // Title before
//    Story.url_(ecURL).title.asOf(tx1.inst).get.head === "ElastiCache in 6 minutes"

    // Who changed the title and when?
//    Story.url_(ecURL).title.txInstant.txAdded.tx(User.email).history.get === List(
//      ("5 feb", true, "ed@itor.com")
//    )

    // Entire history of story entity
//    Story(storyId).a.v.txInstant.txAdded.history.get === List(
//      ("title", "ElastiCache in 6 minutes", "date...", true)
//    )
  }
}