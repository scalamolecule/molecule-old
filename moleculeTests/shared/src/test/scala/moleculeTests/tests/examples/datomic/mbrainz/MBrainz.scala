package moleculeTests.tests.examples.datomic.mbrainz

import molecule.datomic.api.in1_out4._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
import utest._
import molecule.core.util.Executor._
import scala.concurrent.Future
import scala.language.postfixOps


// See instructions in examples/README_pro.md to setup testing mbrainz

object MBrainz extends AsyncTestSuite {

  lazy val tests = Tests {

    "Data" - mbrainz { implicit conn =>
      for {
        // What are the titles of all the tracks John Lennon played on? (showing 5)
        _ <- Track.name.Artists.name_("John Lennon").get.map(_.sorted.take(5) ==> List(
          "Aisumasen (I'm Sorry)",
          "Amsterdam",
          "Angela",
          "Attica State",
          "Au"
          // etc...
        ))

        // What are the titles, album names, and release years of John Lennon's tracks?
        _ <- Release.year.a1.name.a2.Media.Tracks.name.a3.Artists.name_("John Lennon")
          .get(5).map(_ ==> List(
          (1969, "Live Peace in Toronto 1969", "Blue Suede Shoes"),
          (1969, "Live Peace in Toronto 1969", "Cold Turkey"),
          (1969, "Live Peace in Toronto 1969", "Dizzy Miss Lizzy"),
          (1969, "Live Peace in Toronto 1969", "Don't Worry Kyoko (Mummy's Only Looking for Her Hand in the Snow)"),
          (1969, "Live Peace in Toronto 1969", "Give Peace a Chance"),
        ))

        // What are the titles, album names, and release years of the John Lennon tracks released before or during 1970?
        _ <- Release.year.<=(1970).name.Media.Tracks.name.a1.Artists.name_("John Lennon")
          .get(5).map(_ ==> List(
          (1969, "Unfinished Music No. 3: Wedding Album", "Amsterdam"),
          (1969, "Unfinished Music No. 2: Life With the Lions", "Baby's Heartbeat"),
          (1969, "Live Peace in Toronto 1969", "Blue Suede Shoes"),
          (1969, "Unfinished Music No. 2: Life With the Lions", "Cambridge 1969"),
          (1969, "Live Peace in Toronto 1969", "Cold Turkey"),
        ))

        _ <- if (system == SystemPeer) {
          // What are the titles, artists, album names, and release years of all tracks having the word "always" in their titles?
          Release.year.name.Media.Tracks.name.contains("always").a1.Artists.name
            .get(5).map(_ ==> List(
            (1971, "Rita Coolidge", "(I Always Called Them) Mountains", "Rita Coolidge"),
            (1968, "Ridin' High", "(There's) Always Something There to Remind Me", "Martha Reeves and The Vandellas"),
            (1968, "Feliciano!", "(There's) Always Something There to Remind Me", "José Feliciano"),
            (1973, "In a Gospel Way", "A Man I Always Wanted to Meet", "George Jones"),
            (1973, "A Little Touch of Schmilsson in the Night", "Always", "Harry Nilsson"),
          ))
        } else Future.unit

        // Gender distribution
        _ <- Artist.gender_("male").e(count).get.map(_ ==> List(1325))
        _ <- Artist.gender_("female").e(count).get.map(_ ==> List(309))
        _ <- Artist.gender_("other").e(count).get.map(_ ==> List(1))
      } yield ()
    }


    //    // Todo: model as graph with bidirectional relationships
    //    "Collaboration" - mbrainz { implicit conn =>
    //      for {
    //        // Who collaborated with one of the Beatles?
    //        // Repeated attributes was translated to transitive lookups - model graph instead... todo
    //
    //        _ <- Track.Artists.name("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr").name.get.map(_ ==> List(
    //          ("John Lennon", "The Plastic Ono Band"),
    //          ("George Harrison", "Bob Dylan"),
    //          ("John Lennon", "Yoko Ono"),
    //          ("George Harrison", "Ravi Shankar"),
    //          ("Paul McCartney", "Linda McCartney")
    //        ))
    //
    //        // Who directly collaborated with George Harrison,
    //        _ <- Track.Artists.name_("George Harrison").name.get.map(_ ==> List("Bob Dylan", "Ravi Shankar"))
    //        // .. or collaborated with one of his collaborators?
    //        _ <- Track.Artists.name_("George Harrison").name_.name.get.map(_ ==> List("Ali Akbar Khan"))
    //
    //        // Parameterized input molecule for direct collaborators
    //        collaborators = m(Track.Artists.name_(?).name)
    //
    //        // George Harrison's collaborators
    //
    //        collabs1 <- collaborators("George Harrison").get.map(_.toSeq)
    //        _ = collabs1 ==> List("Bob Dylan", "Ravi Shankar")
    //
    //        // George Harrison's collaborators collaborators (includes George...)
    //        _ <- collaborators(collabs1).get.map(_ ==> List("George Harrison", "Ali Akbar Khan"))
    //      } yield ()
    //    }

    "2-step querying" - mbrainz { implicit conn =>
      for {
        // Which artists have songs that might be covers of The Who (or vice versa)?

        // 2-step querying:
        // First get songs of The Who
        whoSongs <- Track.name.not("Outro", "[outro]", "Intro", "[intro]").Artists.name_("The Who").get
        // Then get songs with same titles by other artists (using output from first query)
        // Note that we use the long list of whoSongs as input instead of applying
        // it directly to the track name attribute which would explode the query.
        _ <- m(Track.name(?).Artists.name.not("The Who"))(whoSongs).get(5).map(_ ==> List(
          ("The Last Time", "The Rolling Stones"),
          ("Overture", "Lionel Bart"),
          ("Sensation", "London Symphony Orchestra"),
          ("Miracle Cure", "London Symphony Orchestra"),
          ("Sensation", "Neon Rose")
        ))
      } yield ()
    }
  }
}