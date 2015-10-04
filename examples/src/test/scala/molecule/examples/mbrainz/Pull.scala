//package molecule.examples.mbrainz
//
//import datomic.{Util, Peer}
//
//import java.util.UUID._
//import molecule._
//import molecule.examples.mbrainz.dsl.mBrainz._
//import molecule.util.MoleculeSpec
//import scala.language.postfixOps
//
//import java.util.UUID
//
///*
//  Start Datomic transactor first (replace first part of path to where you have the datomic download)
//  ~/lib/datomic/datomic-pro-0.9.5206/bin/transactor ~/lib/datomic/datomic-pro-0.9.5206/mbrainz-1968-1973/dev-transactor-template.properties
//  ~/lib/datomic/datomic-pro-0.9.5206/bin/datomic restore-db file:///Users/mg/lib/datomic/datomic-pro-0.9.5206/mbrainz-1968-1973 datomic:dev://localhost:4334/mbrainz-1968-1973
//  Remember to add -Xmx2g -server to IDE compiler settings ("Additional build process VM options)
//  Also, ensure the same java version is used in IDE
//
//  See:
//  http://blog.datomic.com/2014/10/datomic-pull.html
//  http://docs.datomic.com/pull.html#recursive-specifications
//*/
//
//class Pull extends MoleculeSpec {
//
//  implicit val conn = Peer.connect("datomic:free://localhost:4334/mbrainz-1968-1973")
//  import Util._
//
//
//  val ledZeppelin_           = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
//  val mccartney_             = UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")
//  val darkSideOfTheMoon_     = UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")
//  val dylanHarrisonSessions_ = UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")
//  val concertForBanglaDesh_  = UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")
//
//  val ledZeppelin           = list(":artist/gid", ledZeppelin_)
//  val mccartney             = list(":artist/gid", mccartney_)
//  val darkSideOfTheMoon     = list(":release/gid", darkSideOfTheMoon_)
//  val dylanHarrisonSessions = list(":release/gid", dylanHarrisonSessions_)
//  val concertForBanglaDesh  = list(":release/gid", concertForBanglaDesh_)
//
//  val db = conn.db()
//
//  import scala.collection.JavaConversions._
//  import scala.collection.JavaConverters._
//
//
//  "Pull attribute name" >> {
//    db.pull("[:artist/name :artist/startYear]", ledZeppelin).toList map println
//    Artist.gid_(ledZeppelin_).name.startYear.one ===("Led Zeppelin", 1968)
//  }
//
//  "Pull attribute name" >> {
//    db.pull("[:artist/_country]", "country/GB").toList.get(0)._2.asInstanceOf[clojure.lang.PersistentVector].asScala
//      .size === Artist.e.Country.name_("United Kingdom").get.size
//  }
//
//  "Component Defaults" >> {
//    db.pull("[:release/media]", darkSideOfTheMoon).toList foreach println
//    /*
//      (:release/media,
//      [{ :db/id 17592186121277,
//         :medium/format {:db/id 17592186045741},
//         :medium/position 1,
//         :medium/trackCount 10,
//         :medium/tracks
//          [
//           {:db/id 17592186121278,
//            :track/duration 68346,
//            :track/name "Speak to Me",
//            :track/position 1,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121279,
//            :track/duration 168720,
//            :track/name "Breathe",
//            :track/position 2,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121280,
//            :track/duration 230600,
//            :track/name "On the Run",
//            :track/position 3,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121281,
//            :track/duration 409600,
//            :track/name "Time",
//            :track/position 4,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121282,
//            :track/duration 284133,
//            :track/name "The Great Gig in the Sky",
//            :track/position 5,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121283,
//            :track/duration 382746,
//            :track/name "Money",
//            :track/position 6,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121284,
//            :track/duration 469853,
//            :track/name "Us and Them",
//            :track/position 7,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121285,
//            :track/duration 206213,
//            :track/name "Any Colour You Like",
//            :track/position 8,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121286,
//            :track/duration 226933,
//            :track/name "Brain Damage",
//            :track/position 9,
//            :track/artists [{:db/id 17592186046909}]}
//           {:db/id 17592186121287,
//            :track/duration 131546,
//            :track/name "Eclipse",
//            :track/position 10,
//            :track/artists [{:db/id 17592186046909}]}
//          ]
//      }])
//    */
//
//    Release.gid_(darkSideOfTheMoon_).Media.format.position.trackCount.Tracks.duration.position.name.get.sortBy(_._5) === List(
//      ("vinyl12", 1, 10, 68346, 1, "Speak to Me"),
//      ("vinyl12", 1, 10, 168720, 2, "Breathe"),
//      ("vinyl12", 1, 10, 230600, 3, "On the Run"),
//      ("vinyl12", 1, 10, 409600, 4, "Time"),
//      ("vinyl12", 1, 10, 284133, 5, "The Great Gig in the Sky"),
//      ("vinyl12", 1, 10, 382746, 6, "Money"),
//      ("vinyl12", 1, 10, 469853, 7, "Us and Them"),
//      ("vinyl12", 1, 10, 206213, 8, "Any Colour You Like"),
//      ("vinyl12", 1, 10, 226933, 9, "Brain Damage"),
//      ("vinyl12", 1, 10, 131546, 10, "Eclipse")
//    )
//  }
//
//
//  "Reverse Component Lookup" >> {
//
//    val dylanHarrisonCD: Object = Peer.query(
//      "[:find ?medium ." +
//        " :in $ ?release" +
//        " :where [?release :release/media ?medium]]",
//      db, dylanHarrisonSessions)
//
//    println(db.pull("[:release/_media]", dylanHarrisonCD))
//    // {:release/_media {:db/id 17592186063798}}
//    println("=========")
//
//    val dylanHarrisonCD2 = Release.gid_(dylanHarrisonSessions_).media.one.toList.head
//    Release.e.media_(dylanHarrisonCD2).one === 17592186063798L
//  }
//
//  "Map specifications" >> {
//
//    val ghostRiders: Long = Peer.query(
//      "[:find ?track ." +
//        " :in $ ?release ?trackno" +
//        " :where" +
//        " [?release :release/media ?medium]" +
//        " [?medium :medium/tracks ?track]" +
//        " [?track :track/position ?trackno]]",
//      db, dylanHarrisonSessions, 11L.asInstanceOf[Object])
//
//    println(db.pull("[:track/name {:track/artists [:db/id :artist/name]}]", ghostRiders))
//    /*
//      {:track/artists [
//        {:db/id 17592186048186, :artist/name "Bob Dylan"}
//        {:db/id 17592186049854, :artist/name "George Harrison"}],
//       :track/name "Ghost Riders in the Sky"}
//    */
//
//    Release.gid_(dylanHarrisonSessions_).Media.Tracks.position_(11).name.Artists.e.name.get === List(
//      ("Ghost Riders in the Sky", 17592186049854L, "George Harrison"),
//      ("Ghost Riders in the Sky", 17592186048186L, "Bob Dylan"))
//  }
//
//  "Nested Map specifications" >> {
//
//    println(db.pull(
//      "[{:release/media" +
//        "  [{:medium/tracks" +
//        "    [:track/name {:track/artists [:artist/name]}]}]}]",
//      concertForBanglaDesh))
//    /*
//    ;; result
//     [{:medium/tracks
//       [{:track/artists
//         [{:artist/name "Ravi Shankar"} {:artist/name "George Harrison"}],
//         :track/name "George Harrison / Ravi Shankar Introduction"}
//        {:track/artists [{:artist/name "Ravi Shankar"}],
//         :track/name "Bangla Dhun"}]}
//      {:medium/tracks
//       [{:track/artists [{:artist/name "George Harrison"}],
//         :track/name "Wah-Wah"}
//        {:track/artists [{:artist/name "George Harrison"}],
//         :track/name "My Sweet Lord"}
//        {:track/artists [{:artist/name "George Harrison"}],
//         :track/name "Awaiting on You All"}
//        {:track/artists [{:artist/name "Billy Preston"}],
//         :track/name "That's the Way God Planned It"}]
//       ...]}
//     */
//
//    Release.gid_(concertForBanglaDesh_).Media.Tracks.name.Artists.name.get === List(
//      ("While My Guitar Gently Weeps", "George Harrison"),
//      ("George Harrison / Ravi Shankar Introduction", "George Harrison"),
//      ("Introduction of the Band", "George Harrison"),
//      ("It Takes a Lot to Laugh / It Takes a Train to Cry", "Bob Dylan"),
//      ("A Hard Rain's Gonna Fall", "Bob Dylan"),
//      ("That's the Way God Planned It", "Billy Preston"),
//      ("My Sweet Lord", "George Harrison"),
//      ("Bangla Desh", "George Harrison"),
//      ("It Don't Come Easy", "Ringo Starr"),
//      ("Awaiting on You All", "George Harrison"),
//      ("Beware of Darkness", "George Harrison"),
//      ("Blowin' in the Wind", "Bob Dylan"),
//      ("Bangla Dhun", "Ravi Shankar"),
//      ("George Harrison / Ravi Shankar Introduction", "Ravi Shankar"),
//      ("Wah-Wah", "George Harrison"),
//      ("Mr. Tambourine Man", "Bob Dylan"),
//      ("Jumpin' Jack Flash / Youngblood", "Leon Russell"),
//      ("Just Like a Woman", "Bob Dylan"),
//      ("Something", "George Harrison"),
//      ("Here Comes the Sun", "George Harrison"))
//  }
//
//  "Wildcard specification" >> {
//    //    println(db.pull("[*]", concertForBanglaDesh))
//    println("==========================")
//    println(Release.gid_(concertForBanglaDesh_).e.one.touch)
//
//    /*
//      Map(
//        :release/country -> :country/US,
//        :release/day -> 20,
//        :release/name -> The Concert for Bangla Desh,
//        :release/media -> List(
//          Map(
//            :medium/trackCount -> 2,
//            :db/id -> 17592186072026,
//            :medium/tracks -> List(
//              Map(
//                :track/artists -> List(
//                  Map(
//                    :artist/endMonth -> 11,
//                    :artist/country -> :country/GB,
//                    :artist/endYear -> 2001,
//                    :artist/type -> :artist.type/person,
//                    :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0,
//                    :artist/name -> George Harrison,
//                    :artist/gender -> :artist.gender/male,
//                    :artist/sortName -> Harrison, George,
//                    :artist/endDay -> 29,
//                    :db/id -> 17592186049854,
//                    :artist/startMonth -> 2,
//                    :artist/startDay -> 24,
//                    :artist/startYear -> 1943)),
//                :track/position -> 2,
//                :track/name -> Bangla Desh,
//                :db/id -> 17592186072028,
//                :track/duration -> 254000),
//              Map(
//                :track/artists -> List(
//                  Map(
//                    :artist/endMonth -> 11,
//                    :artist/country -> :country/GB,
//                    :artist/endYear -> 2001,
//                    :artist/type -> :artist.type/person,
//                    :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0,
//                    :artist/name -> George Harrison,
//                    :artist/gender -> :artist.gender/male,
//                    :artist/sortName -> Harrison, George,
//                    :artist/endDay -> 29,
//                    :db/id -> 17592186049854,
//                    :artist/startMonth -> 2,
//                    :artist/startDay -> 24,
//                    :artist/startYear -> 1943)),
//                :track/position -> 1,
//                :track/name -> Something,
//                :db/id -> 17592186072027,
//                :track/duration -> 185000)),
//            :medium/position -> 2,
//            :medium/format -> :medium.format/vinyl),
//          Map(
//            :medium/trackCount -> 2,
//            :db/id -> 17592186072004,
//            :medium/tracks -> List(
//              Map(
//                :track/artists -> List(
//                  Map(
//                    :artist/country -> :country/IN,
//                    :artist/type -> :artist.type/person,
//                    :artist/gid -> 697f8b9f-0454-40f2-bba2-58f35668cdbe,
//                    :artist/name -> Ravi Shankar,
//                    :artist/gender -> :artist.gender/male,
//                    :artist/sortName -> Shankar, Ravi,
//                    :db/id -> 17592186048829,
//                    :artist/startMonth -> 4,
//                    :artist/startDay -> 7,
//                    :artist/startYear -> 1920)),
//                :track/position -> 2,
//                :track/name -> Bangla Dhun,
//                :db/id -> 17592186072006,
//                :track/duration -> 979000),
//              Map(
//                :track/artists -> List(
//                  Map(
//                    :artist/endMonth -> 11,
//                    :artist/country -> :country/GB,
//                    :artist/endYear -> 2001,
//                    :artist/type -> :artist.type/person,
//                    :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0,
//                    :artist/name -> George Harrison,
//                    :artist/gender -> :artist.gender/male,
//                    :artist/sortName -> Harrison, George,
//                    :artist/endDay -> 29,
//                    :db/id -> 17592186049854,
//                    :artist/startMonth -> 2,
//                    :artist/startDay -> 24,
//                    :artist/startYear -> 1943),
//                  Map(
//                    :artist/country -> :country/IN,
//                    :artist/type -> :artist.type/person,
//                    :artist/gid -> 697f8b9f-0454-40f2-bba2-58f35668cdbe,
//                    :artist/name -> Ravi Shankar,
//                    :artist/gender -> :artist.gender/male,
//                    :artist/sortName -> Shankar, Ravi,
//                    :db/id -> 17592186048829,
//                    :artist/startMonth -> 4,
//                    :artist/startDay -> 7,
//                    :artist/startYear -> 1920)),
//                :track/position -> 1,
//                :track/name -> George Harrison / Ravi Shankar Introduction,
//                :db/id -> 17592186072005,
//                :track/duration -> 376000)),
//            :medium/position -> 1,
//            :medium/format -> :medium.format/vinyl12),
//          Map(
//            :medium/trackCount -> 5,
//            :db/id -> 17592186072020, :medium/tracks -> List(Map(:track/artists -> List(Map(:artist/country -> :country/US, :artist/type -> :artist.type/person, :artist/gid -> 72c536dc-7137-4477-a521-567eeb840fa8, :artist/name -> Bob Dylan, :artist/gender -> :artist.gender/male, :artist/sortName -> Dylan, Bob, :db/id -> 17592186048186, :artist/startMonth -> 5, :artist/startDay -> 24, :artist/startYear -> 1941)), :track/position -> 3, :track/name -> Blowin' in the Wind, :db/id -> 17592186072023, :track/duration -> 214000), Map(:track/artists -> List(Map(:artist/country -> :country/US, :artist/type -> :artist.type/person, :artist/gid -> 72c536dc-7137-4477-a521-567eeb840fa8, :artist/name -> Bob Dylan, :artist/gender -> :artist.gender/male, :artist/sortName -> Dylan, Bob, :db/id -> 17592186048186, :artist/startMonth -> 5, :artist/startDay -> 24, :artist/startYear -> 1941)), :track/position -> 4, :track/name -> Mr. Tambourine Man, :db/id -> 17592186072024, :track/duration -> 246000), Map(:track/artists -> List(Map(:artist/country -> :country/US, :artist/type -> :artist.type/person, :artist/gid -> 72c536dc-7137-4477-a521-567eeb840fa8, :artist/name -> Bob Dylan, :artist/gender -> :artist.gender/male, :artist/sortName -> Dylan, Bob, :db/id -> 17592186048186, :artist/startMonth -> 5, :artist/startDay -> 24, :artist/startYear -> 1941)), :track/position -> 5, :track/name -> Just Like a Woman, :db/id -> 17592186072025, :track/duration -> 254000), Map(:track/artists -> List(Map(:artist/country -> :country/US, :artist/type -> :artist.type/person, :artist/gid -> 72c536dc-7137-4477-a521-567eeb840fa8, :artist/name -> Bob Dylan, :artist/gender -> :artist.gender/male, :artist/sortName -> Dylan, Bob, :db/id -> 17592186048186, :artist/startMonth -> 5, :artist/startDay -> 24, :artist/startYear -> 1941)), :track/position -> 2, :track/name -> It Takes a Lot to Laugh / It Takes a Train to Cry, :db/id -> 17592186072022, :track/duration -> 174000), Map(:track/artists -> List(Map(:artist/country -> :country/US, :artist/type -> :artist.type/person, :artist/gid -> 72c536dc-7137-4477-a521-567eeb840fa8, :artist/name -> Bob Dylan, :artist/gender -> :artist.gender/male, :artist/sortName -> Dylan, Bob, :db/id -> 17592186048186, :artist/startMonth -> 5, :artist/startDay -> 24, :artist/startYear -> 1941)), :track/position -> 1, :track/name -> A Hard Rain's Gonna Fall, :db/id -> 17592186072021, :track/duration -> 304000)), :medium/position -> 4, :medium/format -> :medium.format/vinyl), Map(:medium/trackCount -> 4, :db/id -> 17592186072007, :medium/tracks -> List(Map(:track/artists -> List(Map(:artist/endMonth -> 6, :artist/country -> :country/US, :artist/endYear -> 2006, :artist/type -> :artist.type/person, :artist/gid -> 8a7cf497-dc5c-4523-932d-3fcbc9a69d38, :artist/name -> Billy Preston, :artist/gender -> :artist.gender/male, :artist/sortName -> Preston, Billy, :artist/endDay -> 6, :db/id -> 17592186046812, :artist/startMonth -> 9, :artist/startDay -> 2, :artist/startYear -> 1946)), :track/position -> 4, :track/name -> That's the Way God Planned It, :db/id -> 17592186072011, :track/duration -> 245000), Map(:track/artists -> List(Map(:artist/endMonth -> 11, :artist/country -> :country/GB, :artist/endYear -> 2001, :artist/type -> :artist.type/person, :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0, :artist/name -> George Harrison, :artist/gender -> :artist.gender/male, :artist/sortName -> Harrison, George, :artist/endDay -> 29, :db/id -> 17592186049854, :artist/startMonth -> 2, :artist/startDay -> 24, :artist/startYear -> 1943)), :track/position -> 1, :track/name -> Wah-Wah, :db/id -> 17592186072008, :track/duration -> 195000), Map(:track/artists -> List(Map(:artist/endMonth -> 11, :artist/country -> :country/GB, :artist/endYear -> 2001, :artist/type -> :artist.type/person, :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0, :artist/name -> George Harrison, :artist/gender -> :artist.gender/male, :artist/sortName -> Harrison, George, :artist/endDay -> 29, :db/id -> 17592186049854, :artist/startMonth -> 2, :artist/startDay -> 24, :artist/startYear -> 1943)), :track/position -> 3, :track/name -> Awaiting on You All, :db/id -> 17592186072010, :track/duration -> 157000), Map(:track/artists -> List(Map(:artist/endMonth -> 11, :artist/country -> :country/GB, :artist/endYear -> 2001, :artist/type -> :artist.type/person, :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0, :artist/name -> George Harrison, :artist/gender -> :artist.gender/male, :artist/sortName -> Harrison, George, :artist/endDay -> 29, :db/id -> 17592186049854, :artist/startMonth -> 2, :artist/startDay -> 24, :artist/startYear -> 1943)), :track/position -> 2, :track/name -> My Sweet Lord, :db/id -> 17592186072009, :track/duration -> 256000)), :medium/position -> 3, :medium/format -> :medium.format/vinyl), Map(:medium/trackCount -> 2, :db/id -> 17592186072017, :medium/tracks -> List(Map(:track/artists -> List(Map(:artist/endMonth -> 11, :artist/country -> :country/GB, :artist/endYear -> 2001, :artist/type -> :artist.type/person, :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0, :artist/name -> George Harrison, :artist/gender -> :artist.gender/male, :artist/sortName -> Harrison, George, :artist/endDay -> 29, :db/id -> 17592186049854, :artist/startMonth -> 2, :artist/startDay -> 24, :artist/startYear -> 1943)), :track/position -> 2, :track/name -> Here Comes the Sun, :db/id -> 17592186072019, :track/duration -> 171000), Map(:track/artists -> List(Map(:artist/country -> :country/US, :artist/type -> :artist.type/person, :artist/gid -> d4cc0ed5-0f76-4188-9e14-2a23ca12188a, :artist/name -> Leon Russell, :artist/gender -> :artist.gender/male, :artist/sortName -> Russell, Leon, :db/id -> 17592186047711, :artist/startMonth -> 4, :artist/startDay -> 2, :artist/startYear -> 1942)), :track/position -> 1, :track/name -> Jumpin' Jack Flash / Youngblood, :db/id -> 17592186072018, :track/duration -> 551000)), :medium/position -> 6, :medium/format -> :medium.format/vinyl), Map(:medium/trackCount -> 4, :db/id -> 17592186072012, :medium/tracks -> List(Map(:track/artists -> List(Map(:artist/country -> :country/GB, :artist/type -> :artist.type/person, :artist/gid -> 300c4c73-33ac-4255-9d57-4e32627f5e13, :artist/name -> Ringo Starr, :artist/gender -> :artist.gender/male, :artist/sortName -> Starr, Ringo, :db/id -> 17592186046867, :artist/startMonth -> 7, :artist/startDay -> 7, :artist/startYear -> 1940)), :track/position -> 1, :track/name -> It Don't Come Easy, :db/id -> 17592186072013, :track/duration -> 158000), Map(:track/artists -> List(Map(:artist/endMonth -> 11, :artist/country -> :country/GB, :artist/endYear -> 2001, :artist/type -> :artist.type/person, :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0, :artist/name -> George Harrison, :artist/gender -> :artist.gender/male, :artist/sortName -> Harrison, George, :artist/endDay -> 29, :db/id -> 17592186049854, :artist/startMonth -> 2, :artist/startDay -> 24, :artist/startYear -> 1943)), :track/position -> 2, :track/name -> Beware of Darkness, :db/id -> 17592186072014, :track/duration -> 206000), Map(:track/artists -> List(Map(:artist/endMonth -> 11, :artist/country -> :country/GB, :artist/endYear -> 2001, :artist/type -> :artist.type/person, :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0, :artist/name -> George Harrison, :artist/gender -> :artist.gender/male, :artist/sortName -> Harrison, George, :artist/endDay -> 29, :db/id -> 17592186049854, :artist/startMonth -> 2, :artist/startDay -> 24, :artist/startYear -> 1943)), :track/position -> 3, :track/name -> Introduction of the Band, :db/id -> 17592186072015, :track/duration -> 180000), Map(:track/artists -> List(Map(:artist/endMonth -> 11, :artist/country -> :country/GB, :artist/endYear -> 2001, :artist/type -> :artist.type/person, :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0, :artist/name -> George Harrison, :artist/gender -> :artist.gender/male, :artist/sortName -> Harrison, George, :artist/endDay -> 29, :db/id -> 17592186049854, :artist/startMonth -> 2, :artist/startDay -> 24, :artist/startYear -> 1943)), :track/position -> 4, :track/name -> While My Guitar Gently Weeps, :db/id -> 17592186072016, :track/duration -> 279000)), :medium/position -> 5, :medium/format -> :medium.format/vinyl)), :release/year -> 1971, :release/artists -> List(Map(:artist/endMonth -> 11, :artist/country -> :country/GB, :artist/endYear -> 2001, :artist/type -> :artist.type/person, :artist/gid -> 42a8f507-8412-4611-854f-926571049fa0, :artist/name -> George Harrison, :artist/gender -> :artist.gender/male, :artist/sortName -> Harrison, George, :artist/endDay -> 29, :db/id -> 17592186049854, :artist/startMonth -> 2, :artist/startDay -> 24, :artist/startYear -> 1943)), :release/artistCredit -> George Harrison, :db/id -> 17592186072003, :release/month -> 12, :release/status -> Official, :release/gid -> f3bdff34-9a85-4adc-a014-922eef9cdaa5)
//    */
//
//    ok
//  }
//
//  "Wildcard + map specification" >> {
//    val ghostRiders = Release.gid_(dylanHarrisonSessions_).Media.Tracks.position_(11).e.one
//    println(db.pull("[* {:track/artists [:artist/name]}]", ghostRiders))
//    /*
//      {
//        :db/id 17592186063810,
//        :track/duration 218506,
//        :track/name "Ghost Riders in the Sky",
//        :track/position 11,
//        :track/artists [
//          {:artist/name "Bob Dylan"}
//          {:artist/name "George Harrison"}
//        ]
//      }
//    */
//
//    Track(ghostRiders).duration.name.position.Artists.name.get === List(
//      (218506, "Ghost Riders in the Sky", 11, "George Harrison"),
//      (218506, "Ghost Riders in the Sky", 11, "Bob Dylan"))
//  }
//
//  "Default expression" >> {
//    println(db.pull("[:artist/name (default :artist/endYear 0)]", mccartney))
//    // {:artist/endYear 0, :artist/name "Paul McCartney"}
//
//    Artist.gid_(mccartney_).name.maybe(Artist.endYear) === List(
//      ("Paul McCartney", None)
//    )
//  }
//
//  "Default expression with different type" >> {
//    println(db.pull("[:artist/name (default :artist/endYear \"N/A\")]", mccartney))
//    // {:artist/endYear "N/A", :artist/name "Paul McCartney"}
//
//    // We would pattern match on the endYear option and supply any adequate value...
//    Artist.gid_(mccartney_).name.maybe(Artist.endYear) === List(
//      ("Paul McCartney", None)
//    )
//    ok
//  }
//
//  "Absent attributes are omitted from results" >> {
//    println(db.pull("[:artist/name :died-in-1966?]", mccartney))
//    // {:artist/name "Paul McCartney"}
//
//    Artist.gid_(mccartney_).name.maybe(Script.name) === List(
//      ("Paul McCartney", None)
//    )
//  }
//
//  "Limit plus subspec" >> {
//    println(db.pull("[{(limit :track/_artists 10) [:track/name]}]", ledZeppelin))
//    /*
//      ;; result
//      {:track/_artists
//       [{:track/name "Whole Lotta Love"}
//        {:track/name "What Is and What Should Never Be"}
//        {:track/name "The Lemon Song"}
//        {:track/name "Thank You"}
//        {:track/name "Heartbreaker"}
//        {:track/name "Living Loving Maid (She's Just a Woman)"}
//        {:track/name "Ramble On"}
//        {:track/name "Moby Dick"}
//        {:track/name "Bring It on Home"}
//        {:track/name "Whole Lotta Love"}]}
//     */
//
//    Track.name.Artists.gid_(ledZeppelin_).get(10) === List(
//      "D'yer Mak'er",
//      "The Song Remains the Same",
//      "The Crunge",
//      "The Ocean",
//      "You Shook Me",
//      "Whole Lotta Love",
//      "When the Levee Breaks",
//      "Heartbreaker",
//      "I Can't Quit You Baby",
//      "No Quarter")
//  }
//
//  "No limit" >> {
//    println(db.pull("[{(limit :track/_artists nil) [:track/name]}]", ledZeppelin))
//
//    Track.name.Artists.gid_(ledZeppelin_).get.size === 48
//  }
//
//  "Pull expression in query" >> {
//    println(Peer.query(
//      "[:find [(pull ?e [:release/name]) ...]" +
//        " :in $ ?artist" +
//        " :where [?e :release/artists ?artist]]",
//      db, ledZeppelin))
//    /*
//    Hmm... seems there are some duplicates from the Datomic pull:
//
//      [{:release/name "Immigrant Song / Hey Hey What Can I Do"}
//       {:release/name "Heartbreaker / Bring It On Home"}
//       {:release/name "Led Zeppelin III"}
//       {:release/name "Immigrant Song / Hey Hey What Can I Do"}
//       {:release/name "Led Zeppelin II"}
//       {:release/name "Led Zeppelin IV"}
//       {:release/name "Led Zeppelin"}
//       {:release/name "Led Zeppelin III"}
//       {:release/name "Whole Lotta Love / Living Loving Maid"}
//       {:release/name "Led Zeppelin II"}
//       {:release/name "Led Zeppelin"}
//       {:release/name "Houses of the Holy"}
//       {:release/name "Led Zeppelin III"}
//       {:release/name "Led Zeppelin"}
//       {:release/name "Led Zeppelin II"}
//       {:release/name "Led Zeppelin II"}
//       {:release/name "Led Zeppelin IV"}]
//    */
//
//    Release.name.Artists.gid_(ledZeppelin_).get === List(
//      "Led Zeppelin III",
//      "Led Zeppelin II",
//      "Led Zeppelin",
//      "Led Zeppelin IV",
//      "Whole Lotta Love / Living Loving Maid",
//      "Houses of the Holy",
//      "Heartbreaker / Bring It On Home",
//      "Immigrant Song / Hey Hey What Can I Do")
//  }
//
//  "Dynamic pattern input" >> {
//    "" + Peer.query(
//      "[:find [(pull ?e pattern) ...]" +
//        " :in $ ?artist pattern" +
//        " :where [?e :release/artists ?artist]]",
//      db, ledZeppelin, "[:release/name]") ===
//      """[{:release/name "Immigrant Song / Hey Hey What Can I Do"} {:release/name "Heartbreaker / Bring It On Home"} {:release/name "Led Zeppelin III"} {:release/name "Immigrant Song / Hey Hey What Can I Do"} {:release/name "Led Zeppelin II"} {:release/name "Led Zeppelin IV"} {:release/name "Led Zeppelin"} {:release/name "Led Zeppelin III"} {:release/name "Whole Lotta Love / Living Loving Maid"} {:release/name "Led Zeppelin II"} {:release/name "Led Zeppelin"} {:release/name "Houses of the Holy"} {:release/name "Led Zeppelin III"} {:release/name "Led Zeppelin"} {:release/name "Led Zeppelin II"} {:release/name "Led Zeppelin II"} {:release/name "Led Zeppelin IV"}]"""
//
//    // Molecule has no dynamic attributes (all is static).
//    // But we can make an input molecule waiting for a uuid:
//    val releases = m(Release.name.Artists.gid_(?))
//
//    releases(ledZeppelin_).get === List(
//      "Led Zeppelin III",
//      "Led Zeppelin II",
//      "Led Zeppelin",
//      "Led Zeppelin IV",
//      "Whole Lotta Love / Living Loving Maid",
//      "Houses of the Holy",
//      "Heartbreaker / Bring It On Home",
//      "Immigrant Song / Hey Hey What Can I Do")
//  }
//
////  "Recursion" >> {
////
////  }
//}