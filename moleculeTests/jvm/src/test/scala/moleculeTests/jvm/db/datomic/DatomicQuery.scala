package moleculeTests.jvm.db.datomic

import java.util.UUID
import molecule.core.util.Executor._
import molecule.datomic.api.out2._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


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

    "card-one" - core { implicit futConn =>
      for {
        conn <- futConn
        _ <- Ns.str insert "a"
        _ <- Ns.int insert 1
        _ <- Ns.long insert 1L
        _ <- Ns.double insert 1.1
        _ <- Ns.bool insert true
        _ <- Ns.date insert date1
        _ <- Ns.uuid insert uuid1
        _ <- Ns.uri insert uri1
        _ <- Ns.bigInt insert bigInt1
        _ <- Ns.bigDec insert bigDec1
        _ <- Ns.enumm insert "enum1"
        _ <- Ns.ref1 insert 42L

        _ <- conn.query("[:find ?v :where [_ :Ns/str ?v]]").map(_ ==> List(List("a")))
        _ <- conn.query("[:find ?v :where [_ :Ns/int ?v]]").map(_ ==> List(List(1)))
        _ <- conn.query("[:find ?v :where [_ :Ns/long ?v]]").map(_ ==> List(List(1L)))
        _ <- conn.query("[:find ?v :where [_ :Ns/double ?v]]").map(_ ==> List(List(1.1)))
        _ <- conn.query("[:find ?v :where [_ :Ns/bool ?v]]").map(_ ==> List(List(true)))
        _ <- conn.query("[:find ?v :where [_ :Ns/date ?v]]").map(_ ==> List(List(date1)))
        _ <- conn.query("[:find ?v :where [_ :Ns/uuid ?v]]").map(_ ==> List(List(uuid1)))
        _ <- conn.query("[:find ?v :where [_ :Ns/uri ?v]]").map(_ ==> List(List(uri1)))
        _ <- conn.query("[:find ?v :where [_ :Ns/bigInt ?v]]").map(_ ==> List(List(bigInt1)))
        _ <- conn.query("[:find ?v :where [_ :Ns/bigDec ?v]]").map(_ ==> List(List(bigDec1)))
        _ <- conn.query("[:find ?v :where [_ :Ns/enumm ?v]]").map(_ ==> List(List("enum1")))
        _ <- conn.query("[:find ?v :where [_ :Ns/ref1 ?v]]").map(_ ==> List(List(42L)))
      } yield ()
    }

    "card-many" - core { implicit futConn =>
      for {
        conn <- futConn
        _ <- Ns.strs insert Set("a")
        _ <- Ns.ints insert Set(1)
        _ <- Ns.longs insert Set(1L)
        _ <- Ns.doubles insert Set(1.1)
        _ <- Ns.bools insert Set(true)
        _ <- Ns.dates insert Set(date1)
        _ <- Ns.uuids insert Set(uuid1)
        _ <- Ns.uris insert Set(uri1)
        _ <- Ns.bigInts insert Set(bigInt1)
        _ <- Ns.bigDecs insert Set(bigDec1)
        _ <- Ns.enums insert Set("enum1")
        _ <- Ns.refs1 insert Set(42L)

        _ <- conn.query("[:find ?v :where [_ :Ns/strs ?v]]").map(_ ==> List(List(Set("a"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/ints ?v]]").map(_ ==> List(List(Set(1))))
        _ <- conn.query("[:find ?v :where [_ :Ns/longs ?v]]").map(_ ==> List(List(Set(1L))))
        _ <- conn.query("[:find ?v :where [_ :Ns/doubles ?v]]").map(_ ==> List(List(Set(1.1))))
        _ <- conn.query("[:find ?v :where [_ :Ns/bools ?v]]").map(_ ==> List(List(Set(true))))
        _ <- conn.query("[:find ?v :where [_ :Ns/dates ?v]]").map(_ ==> List(List(Set(date1))))
        _ <- conn.query("[:find ?v :where [_ :Ns/uuids ?v]]").map(_ ==> List(List(Set(uuid1))))
        _ <- conn.query("[:find ?v :where [_ :Ns/uris ?v]]").map(_ ==> List(List(Set(uri1))))
        _ <- conn.query("[:find ?v :where [_ :Ns/bigInts ?v]]").map(_ ==> List(List(Set(bigInt1))))
        _ <- conn.query("[:find ?v :where [_ :Ns/bigDecs ?v]]").map(_ ==> List(List(Set(bigDec1))))
        _ <- conn.query("[:find ?v :where [_ :Ns/enumms ?v]]").map(_ ==> List(List(Set("enum1"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/refs1 ?v]]").map(_ ==> List(List(Set(42L))))
      } yield ()
    }

    "card-map" - core { implicit futConn =>
      for {
        conn <- futConn
        _ <- Ns.strMap insert Map("a" -> "b")
        _ <- Ns.intMap insert Map("a" -> 1)
        _ <- Ns.longMap insert Map("a" -> 1L)
        _ <- Ns.doubleMap insert Map("a" -> 1.1)
        _ <- Ns.boolMap insert Map("a" -> true)
        _ <- Ns.dateMap insert Map("a" -> date1)
        _ <- Ns.uuidMap insert Map("a" -> uuid1)
        _ <- Ns.uriMap insert Map("a" -> uri1)
        _ <- Ns.bigIntMap insert Map("a" -> bigInt1)
        _ <- Ns.bigDecMap insert Map("a" -> bigDec1)

        _ <- conn.query("[:find ?v :where [_ :Ns/strMap ?v]]").map(_ ==> List(List(Set("a@a"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/intMap ?v]]").map(_ ==> List(List(Set("a@1"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/longMap ?v]]").map(_ ==> List(List(Set("a@1"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/doubleMap ?v]]").map(_ ==> List(List(Set("a@1.1"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/boolMap ?v]]").map(_ ==> List(List(Set("a@true"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/dateMap ?v]]").map(_ ==> List(List(Set(s"a@${date2str(date1)}"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/uuidMap ?v]]").map(_ ==> List(List(Set(s"a@$uuid1"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/uriMap ?v]]").map(_ ==> List(List(Set(s"a@$uri1"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/bigIntMap ?v]]").map(_ ==> List(List(Set(s"a@$bigInt1"))))
        _ <- conn.query("[:find ?v :where [_ :Ns/bigDecMap ?v]]").map(_ ==> List(List(Set(s"a@$bigDec1"))))
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

        // Note that only find-rel elements are allowed in Datomic Client :find
        // :find (pull ?e [*])
        // :find [(pull ?e [*]) ...]  // not allowed in datomic client
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
          ).map(_.sortBy(_.head.toString) ==> List(
            List(":Release/name" -> "Heartbreaker / Bring It On Home"),
            List(":Release/name" -> "Houses of the Holy"),
            List(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do"),
            List(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Led Zeppelin IV"),
            List(":Release/name" -> "Led Zeppelin IV"),
            List(":Release/name" -> "Led Zeppelin"),
            List(":Release/name" -> "Led Zeppelin"),
            List(":Release/name" -> "Led Zeppelin"),
            List(":Release/name" -> "Whole Lotta Love / Living Loving Maid"),
          ))
        } else {
          conn.query(
            """[:find (pull ?e pattern)
              |       :in $ ?artist pattern
              |       :where [?e :release/artists ?artist]]""".stripMargin,
            ledZeppelin,
            "[:Release/name]"
          ).map(_.sortBy(_.head.toString) ==> List(
            List(":Release/name" -> "Heartbreaker / Bring It On Home"),
            List(":Release/name" -> "Houses of the Holy"),
            List(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do"),
            List(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin II"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Led Zeppelin III"),
            List(":Release/name" -> "Led Zeppelin IV"),
            List(":Release/name" -> "Led Zeppelin IV"),
            List(":Release/name" -> "Led Zeppelin"),
            List(":Release/name" -> "Led Zeppelin"),
            List(":Release/name" -> "Led Zeppelin"),
            List(":Release/name" -> "Whole Lotta Love / Living Loving Maid"),
          ))
        }
//        } else Future.unit
      } yield ()
    }
  }
}
