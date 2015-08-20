package molecule.examples.mbrainz
import datomic.{Util, Peer}
import molecule._
import molecule.examples.mbrainz.dsl.mBrainz._
import molecule.util.MoleculeSpec

import scala.language.postfixOps

// Start Datomic transactor first:
// /PATH/TO/datomic/datomic-pro-0.9.5078/bin/transactor /PATH/TO/mbrainz-1968-1973/dev-transactor-template.properties
// ~/lib/datomic/datomic-pro-0.9.5206 $ bin/datomic restore-db file:///Users/mg/lib/datomic/datomic-pro-0.9.5206/mbrainz-1968-1973 datomic:dev://localhost:4334/mbrainz-1968-1973
// Remember to add -Xmx2g -server to IDE compiler settings ("Additional build process VM options
// Also, ensure the same java version is used in IDE

class MBrainz extends MoleculeSpec {

  implicit val conn = Peer.connect("datomic:free://localhost:4334/mbrainz-1968-1973")


  "Queries" >> {

    // What are the titles of all the tracks John Lennon played on?
    Track.name.Artists.name_("John Lennon").get(3) === List("Baby's Heartbeat", "John & Yoko", "Nutopian International Anthem")

    // What are the titles, album names, and release years of John Lennon's tracks?
    Release.year.name.Media.Tracks.name.Artists.name_("John Lennon").get(5) === List(
      (1969, "Unfinished Music No. 3: Wedding Album", "Amsterdam"),
      (1973, "Some Time in New York City", "Sunday Bloody Sunday"),
      (1973, "Some Time in New York City", "The Luck of the Irish"),
      (1971, "Power to the People", "Open Your Box"),
      (1969, "Live Peace in Toronto 1969", "Cold Turkey")
    )

    // What are the titles, album names, and release years of the John Lennon tracks released before or during 1970?
    Release.year.<=(1970).name.Media.Tracks.name.Artists.name_("John Lennon").get(5) === List(
      (1969, "Unfinished Music No. 3: Wedding Album", "Amsterdam"),
      (1969, "Live Peace in Toronto 1969", "Cold Turkey"),
      (1970, "Instant Karma! / Who Has Seen the Wind?", "Instant Karma!"),
      (1969, "Unfinished Music No. 2: Life With the Lions", "Two Minutes Silence"),
      (1969, "Unfinished Music No. 2: Life With the Lions", "Baby's Heartbeat")
    )

    // What are the titles, artists, album names, and release years of all tracks having the word "always" in their titles?
    Release.year.name.Media.Tracks.name.contains("always").Artists.name.get(5) === List(
      (1972, "You'll Always Be a Friend", "You'll Always Be a Friend", "Hot Chocolate"),
      (1971, "Hot Rocks 1964-1971", "You Can’t Always Get What You Want", "The Rolling Stones"),
      (1972, "Always on My Mind / That Ain't Right", "Always on My Mind", "Brenda Lee"),
      (1970, "Check Out Your Mind!", "You'll Always Be Mine", "The Impressions"),
      (1968, "Signed, Sealed and Delivered", "I Want to Be With You Always", "Lefty Frizzell"))

    // Who collaborated with one of the Beatles?
    // Repeated attributes are translated to transitive lookups

    val tracks = List("It's All Over Now, Baby Blue", "Mary Ann", "Your True Love", "California", "One Too Many Mornings", "Copper Kettle", "Early Mornin’ Rain", "Down Along the Cove", "Knockin' on Heaven's Door", "Sign on the Window", "Rainy Day Women Nos. 12 & 35", "Sarah Jane", "The Boxer", "Three Angels", "Tonight I'll Be Staying Here With You", "River Theme", "Dear Landlord", "Baby Please Don't Go", "Farewell", "Working on a Guru", "I Ain't Got No Home", "George Jackson (acoustic version)", "Quinn the Eskimo (The Mighty Quinn)", "It Takes a Lot to Laugh / It Takes a Train to Cry", "I Forgot More Than You’ll Ever Know", "The Wicked Messenger", "Father of Night", "Blue Moon", "Poor Lazarus", "The Ballad of Ira Hayes", "Cupid", "Wigwam", "Just Like a Woman", "Billy 7", "Yesterday", "Song to Woody", "Billy 4", "Mr. Tambourine Man", "Billy 1", "Just Like Tom Thumb's Blues", "I Threw It All Away", "Went to See the Gypsy", "Drifter's Escape", "Woogie Boogie", "To Be Alone With You",
      "Turkey Chase", "Minstrel Boy (live)", "Grasshoppers in My Pillow", "Maggie's Farm", "Tell Me That It Isn't True", "Nashville Skyline Rag", "In Search of Little Sadie", "Mama, You Been on My Mind", "My Back Pages", "Tomorrow Is a Long Time", "John Wesley Harding", "Belle Isle", "On the Road Again", "The Death of Emmett Till", "Cantina Theme (Workin' for the Law)", "A Hard Rain's Gonna Fall", "You Ain't Goin' Nowhere", "Candy Man", "Man of Constant Sorrow", "Ghost Riders in the Sky", "I Dreamed I Saw St. Augustine", "Stuck Inside of Mobile With the Memphis Blues Again", "Watching the River Flow", "Blowin' in the Wind", "All I Have to Do Is Dream", "As I Went Out One Morning", "Peggy Day", "It Hurts Me Too", "Mr. Bojangles", "Girl From the North Country", "She Belongs to Me", "Nowhere to Go (Everytime Somebody Comes to Town)", "Final Theme", "Da Doo Ron Ron", "Day of the Locusts", "I Pity the Poor Immigrant", "Can’t Help Falling in Love", "Days of 49", "Lay Lady Lay",
      "One Too Many Mornings (instrumental)", "Alberta #1", "A Hard Rain's A-Gonna Fall", "Alberta #2", "Time Passes Slowly", "The Ballad of Frankie Lee and Judas Priest", "A Fool Such as I", "One More Weekend", "Winterlude", "The Mighty Quinn (Quinn the Eskimo) (live)", "Main Title Theme (Billy)", "If Not for You", "One More Night", "Only a Hobo", "Honey, Just Allow Me One More Chance"
      , "Outlaw Blues"
      , "Matchbox"
      , "Country Pie"
      , "I Am a Lonesome Hobo"
      , "Spanish Is the Loving Tongue"
      , "Positively 4th Street"
      , "Bunkhouse Theme"
      , "Big Yellow Taxi"
      , "Subterranean Homesick Blues"
      , "I Shall Be Released"
      , "I'm Goin' Fishin' (Fishin' Blues)"
      , "I'll Be Your Baby Tonight"
      , "Take a Message to Mary"
      , "The Man in Me"
      , "Love Minus Zero/No Limit"
      , "Bob Dylan's 115th Dream"
      , "I Don't Believe You"
      , "If Dogs Run Free"
      , "Gates of Eden"
      , "Like a Rolling Stone (live)"
      , "My Swamp's Gonna Catch on Fire (Telephone Line) (Las Vegas Blues)"
      , "All the Tired Horses"
      , "I'd Have You Anytime"
      , "Living the Blues"
      , "Lily of the West"
      , "Don't Think Twice, It's Alright (instrumental)"
      , "New Morning"
      , "When I Paint My Masterpiece"
      , "All I Really Want to Do"
      , "Let It Be Me"
      , "Little Sadie"
      , "She Belongs to Me (live)"
      , "It's Alright, Ma (I'm Only Bleeding)"
      , "Don't Think Twice, It's All Right"
      , "The Grand Coulee Dam"
      , "Crash on the Levee (Down in the Flood)"
      , "George Jackson (big band version)"
      , "All Along the Watchtower"
      , "Gotta Travel On"
      , "Take Me as I Am (or Let Me Go)"
      , "Can't Help Falling in Love")





    val george = Track.e.name.Artists.name_("George Harrison").get.sorted //.foreach(println)
    println("==============================================")
    val bob = Track.e.name.Artists.name_("Bob Dylan").get.sorted //.foreach(println)
    println("==============================================")

//    bob.foreach { b =>
//      george.foreach { g =>
//        if (b._1 == g._1 && b._2 == g._2)
//          println(g)
//      }
//    }

    import scala.collection.JavaConversions._
    import scala.collection.JavaConverters._

    val q1 =
      """
            [:find  ?a ?b ?d
                  :where [?a :track/name "All I Have to Do Is Dream"]
                         [?a :track/name ?b]
                         [?a :track/artists ?c]
                         [?c :artist/name ?d]]
      """
//    val q1 =
//      """
//            [:find  ?a ?b ?d
//                  :where [?a :track/name "All I Have to Do Is Dream"]
//                         [?a :track/name ?b]
//                         [?a :track/artists ?c]
//                         [?c :artist/name ?d]]
//      """

    //    Peer.q(q1, conn.db) map println
    val rules =
      """[;; Given ?t bound to track entity-ids, binds ?r to the corresponding
        | ;; set of album release entity-ids
        | [(track-release ?t ?r)
        |  [?m :medium/tracks ?t]
        |  [?r :release/media ?m]]
        |
        | ;; Supply track entity-ids as ?t, and the other parameters will be
        | ;; bound to the corresponding information about the tracks
        | [(track-info ?t ?track-name ?artist-name ?album ?year)
        |  [?t :track/name    ?track-name]
        |  [?t :track/artists ?a]
        |  [?a :artist/name   ?artist-name]
        |  (track-release ?t ?r)
        |  [?r :release/name  ?album]
        |  [?r :release/year  ?year]]
        |
        | ;; Supply ?a (artist entity-ids) and and integer ?max track duration,
        | ;; and ?t, ?len will be bound to track entity-ids and lengths
        | ;; (respectively) of tracks shorter than the given ?max
        | [(short-track ?a ?t ?len ?max)
        |  [?t :track/artists ?a]
        |  [?t :track/duration ?len]
        |  [(< ?len ?max)]]
        |
        | ;; Fulltext search on track.  Supply the query string ?q, and ?track
        | ;; will be bound to entity-ids of tracks whose title matches the
        | ;; search.
        | [(track-search ?q ?track)
        |  [(fulltext $ :track/name ?q) [[?track ?tname]]]]
        |
        | ;; Generic transitive network walking, used by collaboration network
        | ;; rule below
        |
        | ;; Supply:
        | ;; ?e1 -- an entity-id
        | ;; ?attr -- an attribute ident
        | ;; and ?e2 will be bound to entity-ids such that ?e1 and ?e2 are both
        | ;; values of the given attribute for some entity (?x)
        | [(transitive-net-1 ?attr ?e1 ?e2)
        |  [?x ?attr ?e1]
        |  [?x ?attr ?e2]
        |  [(!= ?e1 ?e2)]]
        |
        | ;; Same as transitive-net-1, but search one more level of depth.  We
        | ;; define this rule twice, once for each case, and the rule
        | ;; represents the union of the two cases:
        | ;; - The entities are directly related via the attribute
        | ;; - The entities are related to the given depth (in this case 2) via the attribute
        | [(transitive-net-2 ?attr ?e1 ?e2)
        |  (transitive-net-1 ?attr ?e1 ?e2)]
        | [(transitive-net-2 ?attr ?e1 ?e2)
        |  (transitive-net-1 ?attr ?e1 ?x)
        |  (transitive-net-1 ?attr ?x ?e2)
        |  [(!= ?e1 ?e2)]]
        |
        | ;; Same as transitive-net-2 but to depth 3
        | [(transitive-net-3 ?attr ?e1 ?e2)
        |  (transitive-net-1 ?attr ?e1 ?e2)]
        | [(transitive-net-3 ?attr ?e1 ?e2)
        |  (transitive-net-2 ?attr ?e1 ?x)
        |  (transitive-net-2 ?attr ?x ?e2)
        |  [(!= ?e1 ?e2)]]
        |
        | ;; Same as transitive-net-2 but to depth 4
        | [(transitive-net-4 ?attr ?e1 ?e2)
        |  (transitive-net-1 ?attr ?e1 ?e2)]
        | [(transitive-net-4 ?attr ?e1 ?e2)
        |  (transitive-net-3 ?attr ?e1 ?x)
        |  (transitive-net-3 ?attr ?x ?e2)
        |  [(!= ?e1 ?e2)]]
        |
        | ;; Artist collaboration graph-walking rules, based on generic
        | ;; graph-walk rule above
        |
        | ;; Supply an artist name as ?artist-name-1, an ?artist-name-2 will be
        | ;; bound to the names of artists who directly collaborated with the
        | ;; artist(s) having that name
        | [(collab ?artist-name-1 ?artist-name-2)
        |  [?a1 :artist/name ?artist-name-1]
        |  (transitive-net-1 :track/artists ?a1 ?a2)
        |  [?a2 :artist/name ?artist-name-2]]
        |
        | ;; Alias for collab
        | [(collab-net-1 ?artist-name-1 ?artist-name-2)
        |  (collab ?artist-name-1 ?artist-name-2)]
        |
        | ;; Collaboration network walk to depth 2
        | [(collab-net-2 ?artist-name-1 ?artist-name-2)
        |  [?a1 :artist/name ?artist-name-1]
        |  (transitive-net-2 :track/artists ?a1 ?a2)
        |  [?a2 :artist/name ?artist-name-2]]
        |
        | ;; Collaboration network walk to depth 3
        | [(collab-net-3 ?artist-name-1 ?artist-name-2)
        |  [?a1 :artist/name ?artist-name-1]
        |  (transitive-net-3 :track/artists ?a1 ?a2)
        |  [?a2 :artist/name ?artist-name-2]]
        |
        | ;; Collaboration network walk to depth 4
        | [(collab-net-4 ?artist-name-1 ?artist-name-2)
        |  [?a1 :artist/name ?artist-name-1]
        |  (transitive-net-4 :track/artists ?a1 ?a2)
        |  [?a2 :artist/name ?artist-name-2]]]""".stripMargin

//      """[:find ?aname2
//    val args = List("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr")
//    val allInputs = Seq(conn.db) ++ Util.list(Util.list("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr"))
    Peer.q(
      """[:find ?aname ?aname2 ?trackname
        | :in $ [?aname ...]
        | :where
        | [?a1 :artist/name ?aname]
        |  [?x :track/artists ?a1]
        |  [?x :track/artists ?a2]
        |  [(!= ?a1 ?a2)]
        |  [?a2 :artist/name ?aname2]
        |  [?x :track/name ?trackname]
        |
        | ]""".stripMargin,
      conn.db
      ,Util.list("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr")
    ).toList map println

//      """["John Lennon" "Paul McCartney" "George Harrison" "Ringo Starr"]"""
//    map println

    Track.e.name("All I Have to Do Is Dream").Artists.name.debug


    //    Track.name.Artists.name("Bob Dylan" and "George Harrison").get.sorted.foreach(println)
    Track.name.Artists.name_("Bob Dylan").name_("George Harrison").debug

    Track.name.Artists.name("Bob Dylan" or "George Harrison").debug
    Track.name(tracks).Artists.name.debug
    Track.name.Artists.name_("Bob Dylan").debug
    Track.name("As I Went Out One Morning").Artists.name.debug
    Track.Artists.name("George Harrison").name.debug
    Track.Artists.name("Ringo Starr").debug
    Track.Artists.name("Ringo Starr").name.debug
    Track.Artists.name("George Harrison").name.name.debug
    Track.Artists.name("Ringo Starr").name.name.debug
    Track.Artists.name("Paul McCartney").name.name.debug
    Track.Artists.name("John Lennon").name.name.debug
    Track.Artists.name("John Lennon").name.debug
    Track.Artists.name("The Plastic Ono Band").name.debug
    Track.Artists.name("Yoko Ono").name.debug
    Track.Artists.name("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr").name.name.debug
    Track.Artists.name("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr").name.name.name.debug
    //    Track.Artists.name("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr").name.name.name.get === 7

    Track.Artists.name("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr").name.get === List(
      ("John Lennon", "The Plastic Ono Band"),
      ("George Harrison", "Bob Dylan"),
      ("John Lennon", "Yoko Ono"),
      ("George Harrison", "Ravi Shankar"),
      ("Paul McCartney", "Linda McCartney"))

    // Who directly collaborated with George Harrison,
    Track.Artists.name_("George Harrison").name.get === List("Bob Dylan", "Ravi Shankar")
    // .. or collaborated with one of his collaborators?
    Track.Artists.name_("George Harrison").name_.name.get === List("Ali Akbar Khan")

    // Parameterized input molecule for direct collaborators
    val collaborators = m(Track.Artists.name_(?).name)

    // George Harrison's collaborators
    val collabs1 = collaborators("George Harrison").get
    collabs1 === List("Bob Dylan", "Ravi Shankar")

    // George Harrison's collaborators collaborators (includes George...)
    collaborators(collabs1).get === List("George Harrison", "Ali Akbar Khan")


    // Which artists have songs that might be covers of The Who (or vice versa)?

    // 2-step querying:
    // First get songs of The Who
    val whoSongs = Track.name.!=("Outro", "[outro]", "Intro", "[intro]").Artists.name_("The Who").get
    // Then get songs with same titles by other artists (using output from first query)
    Track.name(whoSongs).Artists.name.!=("The Who").get(5) === List(
      ("The Last Time", "The Rolling Stones"),
      ("Overture", "Lionel Bart"),
      ("Sensation", "London Symphony Orchestra"),
      ("Miracle Cure", "London Symphony Orchestra"),
      ("Sensation", "Neon Rose")
    )
  }

  //  "Transitive tests" >> {
  //    "No Bonds" >> {
  //      Artist.name.name.debug === 7
  //
  //      Artist.sortName.name.name.debug === 7
  //      Artist.name.sortName.name.debug === 7
  //      Artist.name.name.sortName.debug === 7
  //    }
  //
  //    "One Bond" >> {
  //      Track.Artists.name.name.debug === 7
  //
  //      Track.Artists.sortName.name.name.debug === 7
  //      Track.Artists.name.sortName.name.debug === 7
  //      Track.Artists.name.name.sortName.debug === 7
  //    }
  //
  //    "Two Bond" >> {
  //      Track.Artists.sortName.name._Track.Artists.name.debug === 7
  //      Track.Artists.name.sortName._Track.Artists.name.debug === 7
  //      Track.Artists.name._Track.Artists.sortName.name.debug === 7
  //      Track.Artists.name._Track.Artists.name.sortName.debug === 7
  //
  //      Track.Artists.sortName.name._Track.Artists.name.debug === 7
  //      Track.Artists.name.sortName._Track.Artists.name.debug === 7
  //      Track.Artists.name._Track.Artists.sortName.name.debug === 7
  //      Track.Artists.name._Track.Artists.name.sortName.debug === 7
  //
  //      //    Track.Artists.name.name.debug === 7
  //
  //      Track.Artists.name("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr").name.debug === 7
  //    }
  //  }
}