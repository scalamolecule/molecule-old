package moleculeTests.tests.core.runtime

import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EntityAPI extends AsyncTestSuite {

  lazy val tests = Tests {

    // See also molecule.examples.datomic.dayOfDatomic.ProductsAndOrders

    "touch Map" - core { implicit conn =>
      for {
        tx <- Ns.str.int.Ref1.str1.insert("Ben", 42, "Hollywood Rd")
        List(eid, refId) = tx.eids

        _ <- eid.touch.map(_ ==> Map(
          ":db/id" -> eid,
          ":Ns/int" -> 42,
          ":Ns/ref1" -> Map(
            ":db/id" -> refId,
            ":Ref1/str1" -> "Hollywood Rd"),
          ":Ns/str" -> "Ben"
        ))

        _ <- eid.touchQuoted.map(_ ==>
          s"""Map(
             |  ":db/id" -> ${eid}L,
             |  ":Ns/int" -> 42,
             |  ":Ns/ref1" -> Map(
             |    ":db/id" -> ${refId}L,
             |    ":Ref1/str1" -> "Hollywood Rd"),
             |  ":Ns/str" -> "Ben")""".stripMargin)

        // Level

        _ <- eid.touchMax(2).map(_ ==> Map(
          ":db/id" -> eid,
          ":Ns/int" -> 42,
          ":Ns/ref1" -> Map(":db/id" -> refId, ":Ref1/str1" -> "Hollywood Rd"),
          ":Ns/str" -> "Ben"
        ))
        _ <- eid.touchMax(1).map(_ ==> Map(
          ":db/id" -> eid,
          ":Ns/int" -> 42,
          ":Ns/ref1" -> refId,
          ":Ns/str" -> "Ben"
        ))

        _ <- eid.touchQuotedMax(1).map(_ ==>
          s"""Map(
             |  ":db/id" -> ${eid}L,
             |  ":Ns/int" -> 42,
             |  ":Ns/ref1" -> ${refId}L,
             |  ":Ns/str" -> "Ben")""".stripMargin)
      } yield ()
    }

    "touch List" - core { implicit conn =>
      for {
        tx <- Ns.str.int.Ref1.str1.insert("Ben", 42, "Hollywood Rd")
        List(eid, refId) = tx.eids

        _ <- eid.touchList.map(_ ==> List(
          ":db/id" -> eid,
          ":Ns/int" -> 42,
          ":Ns/ref1" -> List((":db/id", refId), (":Ref1/str1", "Hollywood Rd")),
          ":Ns/str" -> "Ben"
        ))

        _ <- eid.touchListQuoted.map(_ ==>
          s"""List(
             |  ":db/id" -> ${eid}L,
             |  ":Ns/int" -> 42,
             |  ":Ns/ref1" -> List(
             |    ":db/id" -> ${refId}L,
             |    ":Ref1/str1" -> "Hollywood Rd"),
             |  ":Ns/str" -> "Ben")""".stripMargin)

        _ <- eid.touchListMax(3).map(_ ==> List(
          ":db/id" -> eid,
          ":Ns/int" -> 42,
          ":Ns/ref1" -> List((":db/id", refId), (":Ref1/str1", "Hollywood Rd")),
          ":Ns/str" -> "Ben"
        ))
        _ <- eid.touchListMax(1).map(_ ==> List(
          ":db/id" -> eid,
          ":Ns/int" -> 42,
          ":Ns/ref1" -> refId,
          ":Ns/str" -> "Ben"
        ))

        _ <- eid.touchListQuotedMax(1).map(_ ==>
          s"""List(
             |  ":db/id" -> ${eid}L,
             |  ":Ns/int" -> 42,
             |  ":Ns/ref1" -> ${refId}L,
             |  ":Ns/str" -> "Ben")""".stripMargin)
      } yield ()
    }

    "one" - core { implicit conn =>
      for {
        tx1 <- Ns.str("a").save
        e1 = tx1.eid
        _ <- e1.touchList.map(_ ==> List(":db/id" -> e1, ":Ns/str" -> "a"))

        tx2 <- Ns.int(1).save
        e2 = tx2.eid
        _ <- e2.touchList.map(_ ==> List(":db/id" -> e2, ":Ns/int" -> 1))

        tx3 <- Ns.long(1L).save
        e3 = tx3.eid
        _ <- e3.touchList.map(_ ==> List(":db/id" -> e3, ":Ns/long" -> 1L))

        tx5 <- Ns.double(1.1).save
        e5 = tx5.eid
        _ <- e5.touchList.map(_ ==> List(":db/id" -> e5, ":Ns/double" -> 1.1))

        tx6 <- Ns.bool(true).save
        e6 = tx6.eid
        _ <- e6.touchList.map(_ ==> List(":db/id" -> e6, ":Ns/bool" -> true))

        tx7 <- Ns.bigInt(bigInt1).save
        e7 = tx7.eid
        _ <- e7.touchList.map(_ ==> List(":db/id" -> e7, ":Ns/bigInt" -> bigInt1))

        tx8 <- Ns.bigDec(bigDec1).save
        e8 = tx8.eid
        _ <- e8.touchList.map(_ ==> List(":db/id" -> e8, ":Ns/bigDec" -> bigDec1))

        tx9 <- Ns.date(date1).save
        e9 = tx9.eid
        _ <- e9.touchList.map(_ ==> List(":db/id" -> e9, ":Ns/date" -> date1))

        tx10 <- Ns.uuid(uuid1).save
        e10 = tx10.eid
        _ <- e10.touchList.map(_ ==> List(":db/id" -> e10, ":Ns/uuid" -> uuid1))

        tx11 <- Ns.uri(uri1).save
        e11 = tx11.eid
        _ <- e11.touchList.map(_ ==> List(":db/id" -> e11, ":Ns/uri" -> uri1))

        tx12 <- Ns.enum("enum1").save
        e12 = tx12.eid
        _ <- e12.touchList.map(_ ==> List(":db/id" -> e12, ":Ns/enum" -> ":Ns.enum/enum1"))
      } yield ()
    }

    "many" - core { implicit conn =>
      for {
        tx1 <- Ns.strs("a", "b").save
        e1 = tx1.eid
        _ <- e1.touchList.map(_ ==> List(":db/id" -> e1, ":Ns/strs" -> List("a", "b")))

        tx2 <- Ns.ints(1, 2).save
        e2 = tx2.eid
        _ <- e2.touchList.map(_ ==> List(":db/id" -> e2, ":Ns/ints" -> List(1, 2)))

        tx3 <- Ns.longs(1L, 2L).save
        e3 = tx3.eid
        _ <- e3.touchList.map(_ ==> List(":db/id" -> e3, ":Ns/longs" -> List(1, 2)))

        tx5 <- Ns.doubles(1.1, 2.2).save
        e5 = tx5.eid
        _ <- e5.touchList.map(_ ==> List(":db/id" -> e5, ":Ns/doubles" -> List(1.1, 2.2)))

        tx6 <- Ns.bools(true, false).save
        e6 = tx6.eid
        _ <- e6.touchList.map(_ ==> List(":db/id" -> e6, ":Ns/bools" -> List(false, true)))

        tx7 <- Ns.dates(date1, date2).save
        e7 = tx7.eid
        _ <- e7.touchList.map(_ ==> List(":db/id" -> e7, ":Ns/dates" -> List(date1, date2)))

        tx8 <- Ns.uuids(uuid1, uuid2).save
        e8 = tx8.eid
        _ <- e8.touchList.map(_ ==> List(":db/id" -> e8, ":Ns/uuids" -> List(uuid1, uuid2)))

        tx9 <- Ns.uris(uri1, uri2).save
        e9 = tx9.eid
        _ <- e9.touchList.map(_ ==> List(":db/id" -> e9, ":Ns/uris" -> List(uri1, uri2)))

        tx10 <- Ns.bigInts(bigInt1, bigInt2).save
        e10 = tx10.eid
        _ <- e10.touchList.map(_ ==> List(":db/id" -> e10, ":Ns/bigInts" -> List(bigInt1, bigInt2)))

        tx11 <- Ns.bigDecs(bigDec1, bigDec2).save
        e11 = tx11.eid
        _ <- e11.touchList.map(_ ==> List(":db/id" -> e11, ":Ns/bigDecs" -> List(bigDec1, bigDec2)))

        tx12 <- Ns.enums(enum1, enum2).save
        e12 = tx12.eid
        _ <- e12.touchList.map(_ ==> List(":db/id" -> e12, ":Ns/enums" -> List(":Ns.enums/enum1", ":Ns.enums/enum2")))
      } yield ()
    }

    "map" - core { implicit conn =>
      for {
        tx1 <- Ns.strMap("a" -> "aa", "b" -> "bb").save
        e1 = tx1.eid
        _ <- e1.touchList.map(_ ==> List(":db/id" -> e1, ":Ns/strMap" -> List("a@aa", "b@bb")))

        tx2 <- Ns.intMap("a" -> 1, "b" -> 2).save
        e2 = tx2.eid
        _ <- e2.touchList.map(_ ==> List(":db/id" -> e2, ":Ns/intMap" -> List("a@1", "b@2")))

        tx3 <- Ns.longMap("a" -> 1L, "b" -> 2L).save
        e3 = tx3.eid
        _ <- e3.touchList.map(_ ==> List(":db/id" -> e3, ":Ns/longMap" -> List("a@1", "b@2")))

        tx5 <- Ns.doubleMap("a" -> 1.1, "b" -> 2.2).save
        e5 = tx5.eid
        _ <- e5.touchList.map(_ ==> List(":db/id" -> e5, ":Ns/doubleMap" -> List("a@1.1", "b@2.2")))

        tx6 <- Ns.boolMap("a" -> true, "b" -> false).save
        e6 = tx6.eid
        _ <- e6.touchList.map(_ ==> List(":db/id" -> e6, ":Ns/boolMap" -> List("a@true", "b@false")))

        tx7 <- Ns.dateMap("a" -> date1, "b" -> date2).save
        e7 = tx7.eid
        _ <- e7.touchList.map(_ ==> List(":db/id" -> e7, ":Ns/dateMap" -> List("a@2001-07-01", "b@2002-01-01")))

        tx8 <- Ns.uuidMap("a" -> uuid1, "b" -> uuid2).save
        e8 = tx8.eid
        _ <- e8.touchList.map(_ ==> List(":db/id" -> e8, ":Ns/uuidMap" -> List(s"a@$uuid1", s"b@$uuid2")))

        tx9 <- Ns.uriMap("a" -> uri1, "b" -> uri2).save
        e9 = tx9.eid
        _ <- e9.touchList.map(_ ==> List(":db/id" -> e9, ":Ns/uriMap" -> List(s"a@$uri1", s"b@$uri2")))

        tx10 <- Ns.bigIntMap("a" -> bigInt1, "b" -> bigInt2).save
        e10 = tx10.eid
        _ <- e10.touchList.map(_ ==> List(":db/id" -> e10, ":Ns/bigIntMap" -> List(s"a@$bigInt1", s"b@$bigInt2")))

        tx11 <- Ns.bigDecMap("a" -> bigDec1, "b" -> bigDec2).save
        e11 = tx11.eid
        _ <- e11.touchList.map(_ ==> List(":db/id" -> e11, ":Ns/bigDecMap" -> List(s"a@$bigDec1", s"b@$bigDec2")))
      } yield ()
    }

    "apply typed" - core { implicit conn =>
      for {
        tx <- Ns.str.int.Ref1.str1.insert("Ben", 42, "Hollywood Rd")
        List(eid, refId) = tx.eids
        strTyped: Option[String] <- eid[String](":Ns/str")
        untyped: Option[Any] <- eid(":Ns/str")

        // Level 1
        _ = strTyped ==> Some("Ben")
        _ <- eid[Int](":Ns/int").map(_ ==> Some(42))

        // Level 2
        _ <- refId[String](":Ref1/str1").map(_ ==> Some("Hollywood Rd"))

        // Non-existing attribute returns None
        _ <- eid(":Ns/non-existing-attribute").map(_ ==> None)
      } yield ()
    }

    "apply untyped" - core { implicit conn =>
      for {
        tx <- Ns.str.int.Ref1.str1.insert("Ben", 42, "Hollywood Rd")
        List(eid, refId) = tx.eids

        _ <- eid(":Ns/str", ":Ns/int", ":Ns/ref1").map(_ ==> List(
          Some("Ben"),
          Some(42),
          Some(
            Map(
              ":db/id" -> refId,
              ":Ref1/str1" -> "Hollywood Rd"
            )
          )
        ))

        // Type ascription is still unchecked since it is eliminated by erasure
        // so we suppress compile warnings emitted
        List(
        optName: Option[String]@unchecked,
        optAddress: Option[Map[String, Any]]@unchecked
        ) <- eid(
          ":Ns/str",
          ":Ns/ref1"
        )

        name: String = optName.getOrElse("no name")

        address: Map[String, Any]@unchecked = optAddress.getOrElse(Map.empty[String, Any])
        street: String = address.getOrElse(":Ref1/str1", "no street").asInstanceOf[String]

        _ = name ==> "Ben"
        _ = street ==> "Hollywood Rd"
      } yield ()
    }

    "Reverse lookup" - core { implicit conn =>
      for {
        tx <- Ref1.int1(10).save
        r = tx.eid

        // 3 entities pointing to r
        tx2 <- Ns.int.ref1.insert(List((1, r), (2, r), (3, r)))
        eids: List[Long] = tx2.eids.sorted

        // get entities pointing to r
        _ <- r[Long](":Ns/_ref1").map(_ ==> Some(eids))

        // Alternatively we can get the entities type safely with a query
        _ <- Ns.e.ref1_(r).get.map(_.sorted ==> eids)
      } yield ()
    }
  }
}
