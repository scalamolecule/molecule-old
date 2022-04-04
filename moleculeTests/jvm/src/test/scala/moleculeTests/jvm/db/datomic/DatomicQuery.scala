package moleculeTests.jvm.db.datomic

import molecule.core.util.Executor._
import molecule.datomic.api.out2._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

/** Simple raw datomic queries
 *
 * This is only intended to do simple checks in jvm tests of for instance schema changes. So it is not thoroughly
 * tested with more complex datomic queries.
 */
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
        _ <- conn.query("[:find ?v :where [_ :Ns/ref1 ?v]]").map(_ ==> List(List(42L)))

        // Raw enum values are returned as entity ids
        // _ <- conn.query("[:find ?v :where [_ :Ns/enumm ?v]]").map(_ ==> List(List(17592186045418L)))
      } yield ()
    }


    "card-many" - core { implicit futConn =>
      for {
        conn <- futConn
        _ <- Ns.strs insert Set("a", "b")
        _ <- Ns.ints insert Set(1, 2)
        _ <- Ns.longs insert Set(1L, 2L)
        _ <- Ns.doubles insert Set(1.1, 2.2)
        _ <- Ns.bools insert Set(true, false)
        _ <- Ns.dates insert Set(date1, date2)
        _ <- Ns.uuids insert Set(uuid1, uuid2)
        _ <- Ns.uris insert Set(uri1, uri2)
        _ <- Ns.bigInts insert Set(bigInt1, bigInt2)
        _ <- Ns.bigDecs insert Set(bigDec1, bigDec2)
        _ <- Ns.refs1 insert Set(42L, 43L)

        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/strs ?v]]").map(_ ==> List(List(Set("a", "b"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/ints ?v]]").map(_ ==> List(List(Set(1, 2))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/longs ?v]]").map(_ ==> List(List(Set(1L, 2L))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/doubles ?v]]").map(_ ==> List(List(Set(1.1, 2.2))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/bools ?v]]").map(_ ==> List(List(Set(true, false))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/dates ?v]]").map(_ ==> List(List(Set(date1, date2))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/uuids ?v]]").map(_ ==> List(List(Set(uuid1, uuid2))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/uris ?v]]").map(_ ==> List(List(Set(uri1, uri2))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/bigInts ?v]]").map(_ ==> List(List(Set(bigInt1, bigInt2))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/bigDecs ?v]]").map(_ ==> List(List(Set(bigDec1, bigDec2))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/refs1 ?v]]").map(_ ==> List(List(Set(42L, 43L))))
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

        // Card-map values are returned as the undecoded string
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/strMap ?v]]").map(_ ==> List(List(Set("a@b"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/intMap ?v]]").map(_ ==> List(List(Set("a@1"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/longMap ?v]]").map(_ ==> List(List(Set("a@1"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/doubleMap ?v]]").map(_ ==> List(List(Set("a@1.1"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/boolMap ?v]]").map(_ ==> List(List(Set("a@true"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/dateMap ?v]]").map(_ ==> List(List(Set(s"a@${date2str(date1)}"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/uuidMap ?v]]").map(_ ==> List(List(Set(s"a@$uuid1"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/uriMap ?v]]").map(_ ==> List(List(Set(s"a@$uri1"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/bigIntMap ?v]]").map(_ ==> List(List(Set(s"a@$bigInt1"))))
        _ <- conn.query("[:find (distinct ?v) :where [_ :Ns/bigDecMap ?v]]").map(_ ==> List(List(Set(s"a@$bigDec1"))))
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
      for {
        conn <- futConn
        _ <- conn.query(
          """[:find (pull ?e pattern)
            |       :in $ pattern
            |       :where [?e :Release/artists [:Artist/gid #uuid "678d88b2-87b0-403b-b63d-5da7465aecc3"]]]""".stripMargin,
          "[:Release/name]" // dynamic pattern
        ).map(_.sortBy(_.head.toString) ==> List(
          List(Map(":Release/name" -> "Heartbreaker / Bring It On Home")),
          List(Map(":Release/name" -> "Houses of the Holy")),
          List(Map(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do")),
          List(Map(":Release/name" -> "Immigrant Song / Hey Hey What Can I Do")),
          List(Map(":Release/name" -> "Led Zeppelin II")),
          List(Map(":Release/name" -> "Led Zeppelin II")),
          List(Map(":Release/name" -> "Led Zeppelin II")),
          List(Map(":Release/name" -> "Led Zeppelin II")),
          List(Map(":Release/name" -> "Led Zeppelin III")),
          List(Map(":Release/name" -> "Led Zeppelin III")),
          List(Map(":Release/name" -> "Led Zeppelin III")),
          List(Map(":Release/name" -> "Led Zeppelin IV")),
          List(Map(":Release/name" -> "Led Zeppelin IV")),
          List(Map(":Release/name" -> "Led Zeppelin")),
          List(Map(":Release/name" -> "Led Zeppelin")),
          List(Map(":Release/name" -> "Led Zeppelin")),
          List(Map(":Release/name" -> "Whole Lotta Love / Living Loving Maid")),
        ))
      } yield ()
    }
  }
}
