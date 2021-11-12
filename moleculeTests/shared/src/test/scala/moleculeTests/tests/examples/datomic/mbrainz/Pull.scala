package moleculeTests.tests.examples.datomic.mbrainz

import java.util.UUID
import molecule.datomic.api.out8._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer}
import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}

/*
  https://github.com/Datomic/day-of-datomic/blob/master/tutorial/pull.clj
  https://docs.datomic.com/on-prem/pull.html
*/

object Pull extends AsyncTestSuite {

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
    for {
      ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
      mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
      darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
      dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
      concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
      dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
      ghostRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
      gb <- Country.e.name_("United Kingdom").get
      georgeHarrison <- Artist.e.name_("George Harrison").get
      bobDylan <- Artist.e.name_("Bob Dylan").get

    } yield {
      (
        ledZeppelinUUID,
        ledZeppelin.head,
        mccartney.head,
        darkSideOfTheMoon.head,
        dylanHarrisonSessions.head,
        concertForBangladesh.head,
        dylanHarrisonCd.head,
        ghostRiders.head,
        gb.head,
        georgeHarrison.head,
        bobDylan.head,
      )
    }
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "query" - mbrainz { implicit conn =>
      for {
        (ledZeppelinUUID,
        ledZeppelin,
        mccartney,
        darkSideOfTheMoon,
        dylanHarrisonSessions,
        concertForBangladesh,
        dylanHarrisonCd,
        ghostRiders,
        gb,
        georgeHarrison,
        bobDylan) <- data

        // Get typed results with queries

        // Attribute name
        _ <- Artist(ledZeppelin).name.startYear.get.map(_.head ==> ("Led Zeppelin", 1968))
        _ <- Artist(ledZeppelin).country.get.map(_.head ==> gb)

        // Reverse lookup (with country id/name)
        _ <- Artist.e.country(gb).get.map(_.size ==> 482)
        _ <- Artist.e.Country.name_("United Kingdom").get.map(_.size ==> 482)

        // Component defaults
        _ <- Release.e_(darkSideOfTheMoon).media.get.map { res =>
          val darkSideMedia = res.head
          /*
          // Getting the darkSideMedia entity graph pulls related tracks since
          // :medium/tracks is a component attribute: (commented out for brevity)

          darkSideMedia.graph.map(_ ==> Map(
            ":db/id" -> 927987813949629L,
            ":Medium/format" -> ":Medium.format/vinyl12",
            ":Medium/position" -> 1,
            ":Medium/trackCount" -> 10,
            ":Medium/tracks" -> Set(
              Map(
                ":Track/name" -> "Us and Them",
                ":track/artistCredit" -> "Pink Floyd",
                ":Track/artists" -> Set(Map(
                  ":Artist/gid" -> "83d91898-7763-47d7-b03b-b92132375c47",
                  ":Artist/country" -> ":country/GB",
                  ":Artist/type" -> ":Artist.type/group",
                  ":Artist/sortName" -> "Pink Floyd",
                  ":Artist/startYear" -> 1964,
                  ":db/id" -> 646512837142669L,
                  ":Artist/name" -> "Pink Floyd")),
                ":Track/position" -> 7,
                ":db/id" -> 927987813949630L,
                ":Track/duration" -> 469853),
              Map(
                ":Track/name" -> "Speak to Me",
                ":track/artistCredit" -> "Pink Floyd",
                ":Track/artists" -> Set(Map(
                  ":Artist/gid" -> "83d91898-7763-47d7-b03b-b92132375c47",
                  ":Artist/country" -> ":country/GB",
                  ":Artist/type" -> ":Artist.type/group",
                  ":Artist/sortName" -> "Pink Floyd",
                  ":Artist/startYear" -> 1964,
                  ":db/id" -> 646512837142669L,
                  ":Artist/name" -> "Pink Floyd")),
                ":Track/position" -> 1,
                ":db/id" -> 927987813949631L,
                ":Track/duration" -> 68346),
              Map(
                ":Track/name" -> "Time",

                etc...
          ))
          */
        }

        // Reverse component lookup
        _ <- Release.e.media_(dylanHarrisonCd).get.map(_.head ==> dylanHarrisonSessions)
        _ <- Release.e.Media.e_(dylanHarrisonCd).get.map(_.head ==> dylanHarrisonSessions)

        // Map specification
        _ <- Track(ghostRiders).name.Artists.*(Artist.e.name).get.map(_.map(t => (t._1, t._2.sortBy(_._2))) ==> List(
          ("Ghost Riders in the Sky", List(
            (bobDylan, "Bob Dylan"),
            (georgeHarrison, "George Harrison"),
          ))
        ))

        // Nested map specification
        _ <- Release(concertForBangladesh).Media.*(
          Medium.position.Tracks.*(
            Track.name.Artists.*(
              Artist.name))).get
          // sort output
          .map(_.map(medium =>
            medium.sortBy(_._1).map(tracks =>
              tracks._2.sortBy(_._1))) ==>
            List(
              List(
                List(
                  ("Bangla Dhun", List("Ravi Shankar")),
                  ("George Harrison / Ravi Shankar Introduction", List("Ravi Shankar", "George Harrison"))),
                List(
                  ("Bangla Desh", List("George Harrison")),
                  ("Something", List("George Harrison"))),
                List(
                  ("Awaiting on You All", List("George Harrison")),
                  ("My Sweet Lord", List("George Harrison")),
                  ("That's the Way God Planned It", List("Billy Preston")),
                  ("Wah-Wah", List("George Harrison"))),
                List(
                  ("A Hard Rain's Gonna Fall", List("Bob Dylan")),
                  ("Blowin' in the Wind", List("Bob Dylan")),
                  ("It Takes a Lot to Laugh / It Takes a Train to Cry", List("Bob Dylan")),
                  ("Just Like a Woman", List("Bob Dylan")),
                  ("Mr. Tambourine Man", List("Bob Dylan"))),
                List(
                  ("Beware of Darkness", List("George Harrison")),
                  ("Introduction of the Band", List("George Harrison")),
                  ("It Don't Come Easy", List("Ringo Starr")),
                  ("While My Guitar Gently Weeps", List("George Harrison"))),
                List(
                  ("Here Comes the Sun", List("George Harrison")),
                  ("Jumpin' Jack Flash / Youngblood", List("Leon Russell"))))))


        // Wildcard specification
        //     concertForBangladeshEid.graph.map(_ ==> Map(<big data graph for Bangladesh concert>))

        // Wildcard + map specification
        // Using optional attributes
        _ <- Track(ghostRiders).name.position$.duration$.artistCredit$.Artists.*(Artist.name).get.map(_ ==>
          (if (system == SystemDevLocal) {
            // dev-local sample db set has no artistCredit data
            List(("Ghost Riders in the Sky", Some(11), Some(218506), None,
              List("Bob Dylan", "George Harrison")))
          } else {
            List(("Ghost Riders in the Sky", Some(11), Some(218506), Some("Bob Dylan & George Harrison"),
              List("George Harrison", "Bob Dylan")))
          })
        )

        // Default option (applying to None)
        _ <- Artist(mccartney).name.endYear$.get
          .map(_.map(t => (t._1, t._2.getOrElse(0))).head ==> ("Paul McCartney", 0))


        // Default option with different type
        _ <- Artist(mccartney).name.endYear$.get
          .map(_.map(t => (t._1, t._2.getOrElse("N/A"))).head ==> ("Paul McCartney", "N/A"))


        // Absent attributes can't compile
        // Artist.name.diedIn1996.get


        // Explicit limit (applied on result)
        _ <- if (system == SystemDevLocal)
          Track.e.Artists.e_(ledZeppelin).get(10).map(_ ==> List(
            27505382880529729L, 71402285107811038L, 27505382880529730L, 71402285107811037L, 27505382880529731L,
            71402285107811040L, 27505382880529732L, 71402285107811039L, 27505382880529733L, 27505382880529734L))
        else
          Track.e.Artists.e_(ledZeppelin).get(10).map(_ ==> List(
            1029142883684579L, 1029142883684580L, 1029142883684577L, 1029142883684578L, 1029142883688203L,
            1029142883688204L, 1029142883688202L, 1029142883688207L, 1029142883688208L, 1029142883688205L))


        // Limit + subspec
        _ <- Track.name.Artists.e_(ledZeppelin).get.map(_.sorted.take(10) ==> List(
          "Babe I'm Gonna Leave You",
          "Black Dog",
          "Black Mountain Side",
          "Bring It On Home",
          "Bring It on Home",
          "Bron-Yr-Aur Stomp",
          "Celebration Day",
          "Communication Breakdown",
          "D'yer Mak'er",
          "Dancing Days"
        ))


        // Limit + subspec + :as option
        // Since attribute names are not returned, this is not relevant in this context


        // No limit
        _ <- Track.e.Artists.e_(ledZeppelin).get.map(_.size ==> 128)


        // Empty results
        // Non-existing attributes not compilable in molecule


        // Pull expression in query (here, only distinct results are returned)
        _ <- Release.name.Artists.e_(ledZeppelin).get.map(_.sorted ==> List(
          "Heartbreaker / Bring It On Home",
          "Houses of the Holy",
          "Immigrant Song / Hey Hey What Can I Do",
          "Led Zeppelin",
          "Led Zeppelin II",
          "Led Zeppelin III",
          "Led Zeppelin IV",
          "Whole Lotta Love / Living Loving Maid",
        ))


        // Dynamic pattern input

        // Note that only find-rel elements are allowed in client :find
        // :find (pull ?e [*])
        // :find [(pull ?e [*]) ...]  // not allowed in client
        // See question and answer: https://clojurians-log.clojureverse.org/datomic/2018-04-04

        _ <- if (!isJsPlatform && system == SystemPeer) {
          // Using a dynamic query
          conn.flatMap(_.query(
            """[:find [(pull ?e pattern) ...]
              |       :in $ ?artist pattern
              |       :where [?e :release/artists ?artist]]""".stripMargin,
            ledZeppelin,
            "[:Release/name]"
          ).map(_ ==> List(
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do"),
            List(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do"),
            List(":Release/name" -> "Houses of the Holy"),
            List(":Release/name" -> "Heartbreaker / Bring It On Home"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Whole Lotta Love / Living Loving Maid"),
            List(":Release/name" -> "Led Zeppelin IV"),
            List(":Release/name" -> "Led Zeppelin IV"),
            List(":Release/name" -> "Led Zeppelin"),
            List(":Release/name" -> "Led Zeppelin"),
            List(":Release/name" -> "Led Zeppelin"),
          )))
        } else Future.unit
      } yield ()
    }
  }
}