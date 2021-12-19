package moleculeTests.jvm.db.datomic

import java.util.UUID
import datomic.Util
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._

/*
  https://github.com/Datomic/day-of-datomic/blob/master/tutorial/pull.clj
  https://docs.datomic.com/on-prem/pull.html
*/

object DatomicPull extends AsyncTestSuite {

  val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")

  lazy val tests = Tests {

    "raw pull" - mbrainz { implicit conn =>
      for {
        // Pull raw java.util.Map's with clojure.lang.Keyword -> <data> pairs
        conn <- conn
        db <- conn.db
        _ <- db.pull(
          "[:Artist/name :Artist/gid]",
          Util.list(Util.read(":Artist/gid"), ledZeppelinUUID)
        ).map(_ ==> Util.map(
          Util.read(":Artist/name"), "Led Zeppelin",
          Util.read(":Artist/gid"), ledZeppelinUUID
        ))
      } yield ()
    }
  }
}