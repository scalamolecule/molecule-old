package molecule.examples.mbrainz
import datomic.Peer
import molecule._
import molecule.examples.mbrainz.dsl.mBrainz._
import molecule.util.MoleculeSpec
import scala.language.postfixOps

class MBrainz extends MoleculeSpec {

  implicit val conn = Peer.connect("datomic:free://localhost:4334/mbrainz-1968-1973")

  "Queries" >> {

    // What are the titles of all the tracks John Lennon played on?
    Track.name.Artists.name_("John Lennon").get(3) === List("Meat City", "Intuition", "Dizzy Miss Lizzy")

    // What are the titles, album names, and release years of John Lennon's tracks?
    Release.year.name.Media.Tracks.name.Artists.name_("John Lennon").get(5) === List(
      (1971, "Happy Xmas (War Is Over)", "Listen, the Snow Is Falling"),
      (1969, "Live Peace in Toronto 1969", "Dizzy Miss Lizzy"),
      (1972, "Some Time in New York City", "Sunday Bloody Sunday"),
      (1971, "Imagine", "How Do You Sleep?"),
      (1969, "Live Peace in Toronto 1969", "John John (Let's Hope for Peace)")
    )

    // What are the titles, album names, and release years of the John Lennon tracks released before or during 1970?
    Release.year.<=(1970).name.Media.Tracks.name.Artists.name_("John Lennon").get(5) === List(
      (1969, "Unfinished Music No. 3: Wedding Album", "Don't Worry Kyoko (Mummy's Only Looking for Her Hand in the Snow)"),
      (1969, "Live Peace in Toronto 1969", "Dizzy Miss Lizzy"),
      (1969, "Unfinished Music No. 3: Wedding Album", "Who Has Seen the Wind?"),
      (1969, "Live Peace in Toronto 1969", "John John (Let's Hope for Peace)"),
      (1970, "John Lennon/Plastic Ono Band", "Hold On")
    )

    // What are the titles, artists, album names, and release years of all tracks having the word "always" in their titles?
    Release.year.name.Media.Tracks.name.contains("always").Artists.name.get(5) === List(
      (1972, "Orange", "Once an Orange, Always an Orange", "Al Stewart"),
      (1969, "Always Something There", "Always Something There", "Stanley Turrentine"),
      (1972, "Pledging My Love", "I Will Always Love You", "John Holt"),
      (1972, "Phantasmagoria", "Once a Ghost, Always a Ghost", "Curved Air"),
      (1970, "You Always Hurt the One You Love", "You Always Hurt the One You Love", "Hank Thompson"))

    // Who collaborated with one of the Beatles?
    // Repeated attributes are translated to transitive lookups
    Track.Artists.name("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr").name.get === List(
      ("John Lennon", "The Plastic Ono Band"),
      ("George Harrison", "Ravi Shankar"),
      ("John Lennon", "Yoko Ono"),
      ("George Harrison", "Bob Dylan"),
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

    // First get songs of The Who
    val whoSongs = Track.name.!=("Outro", "[outro]", "Intro", "[intro]").Artists.name_("The Who").get
    // Then get songs with same titles by other artists
    Track.name(whoSongs).Artists.name.!=("The Who").get(5) === List(
      ("Amazing Journey", "London Symphony Orchestra"),
      ("Shakin' All Over", "Suzi Quatro"),
      ("Overture", "Jule Styne"),
      ("Tommy Can You Hear Me?", "London Symphony Orchestra"),
      ("Underture", "London Symphony Orchestra")
    )
  }
}