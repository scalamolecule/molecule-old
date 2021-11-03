package moleculeTests.jvm.db.datomic

import java.util.UUID
import molecule.datomic.api.out2._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object DatomicQuery extends AsyncTestSuite {

  lazy val tests = Tests {

    "Basic" - core { implicit futConn =>
      for {
        conn <- futConn

        _ <- Ns.str.int insert List(("a", 1), ("b", 2))

        // Scala List of rows/Lists returned
        _ <- conn.query("[:find ?s :where [_ :Ns/str ?s]]").map(_ ==> List(
          List("a"), // row 1
          List("b"), // row 2
        ))

        _ <- conn.query("[:find ?i :where [_ :Ns/int ?i]]").map(_ ==> List(List(1), List(2)))

        _ <- conn.query("[:find ?s ?i :where [?e :Ns/str ?s] [?e :Ns/int ?i]]")
          .map(_.sortBy(_.toString) /* simple order guarantee hack for Any types */ ==> List(
            List("a", 1),
            List("b", 2)
          ))
      } yield ()
    }


    "Input" - core { implicit futConn =>
      for {
        conn <- futConn

        _ <- Ns.str.int insert List(("a", 1), ("b", 2))

        _ <- conn.query(
          "[:find ?i :in $ ?s :where [?e :Ns/str ?s] [?e :Ns/int ?i]]",
          "a"
        ).map(_ ==> List(List(1)))

        _ <- conn.query(
          "[:find ?s :in $ ?i :where [?e :Ns/str ?s] [?e :Ns/int ?i]]",
          1
        ).map(_ ==> List(List("a")))

        _ <- conn.query(
          "[:find ?s ?i :in $ ?s ?i :where [?e :Ns/str ?s] [?e :Ns/int ?i]]",
          "a", 1
        ).map(_ ==> List(List("a", 1)))

        _ <- conn.query(
          "[:find ?s ?i :in $ ?s ?i :where [?e :Ns/str ?s] [?e :Ns/int ?i]]",
          "a", 42
        ).map(_ ==> Nil)
      } yield ()
    }


    "Dynamic pattern input" - mbrainz { implicit futConn =>
      import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
      for {
        conn <- futConn

        // Note that only find-rel elements are allowed in client :find
        // :find (pull ?e [*])
        // :find [(pull ?e [*]) ...]  // not allowed in client
        // See question and answer: https://clojurians-log.clojureverse.org/datomic/2018-04-04
        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get.map(_.head)

        _ <- if (system == SystemPeer) {
          // Using a dynamic query
          conn.query(
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
          ))
        } else Future.unit

        //        _ <-
        //          conn.query(
        //            // Dynamic query
        //            """[:find [(pull ?e pattern) ...]
        //              |       :in $ ?artist pattern
        //              |       :where [?e :release/artists ?artist]]""".stripMargin,
        //            ledZeppelin,
        //            "[:Release/name]"
        //          ).map(_ ==> List(
        //            List(":Release/name" -> "Led Zeppelin II"),
        //            List(":Release/name" -> "Led Zeppelin II"),
        //            List(":Release/name" -> "Led Zeppelin II"),
        //            List(":Release/name" -> "Led Zeppelin II"),
        //            List(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do"),
        //            List(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do"),
        //            List(":Release/name" -> "Houses of the Holy"),
        //            List(":Release/name" -> "Heartbreaker / Bring It On Home"),
        //            List(":Release/name" -> "Led Zeppelin III"),
        //            List(":Release/name" -> "Led Zeppelin III"),
        //            List(":Release/name" -> "Led Zeppelin III"),
        //            List(":Release/name" -> "Whole Lotta Love / Living Loving Maid"),
        //            List(":Release/name" -> "Led Zeppelin IV"),
        //            List(":Release/name" -> "Led Zeppelin IV"),
        //            List(":Release/name" -> "Led Zeppelin"),
        //            List(":Release/name" -> "Led Zeppelin"),
        //            List(":Release/name" -> "Led Zeppelin"),
        //          ))
      } yield ()
    }
  }
}
