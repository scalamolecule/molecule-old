package moleculeTests.tests.core.runtime

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.out3._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object EntityMap extends AsyncTestSuite {

  lazy val tests = Tests {

    // See also molecule.examples.datomic.dayOfDatomic.ProductsAndOrders

    "touch api" - core { implicit conn =>
      for {
        List(eid, refId) <- Ns.str.int.Ref1.str1.insert("Ben", 42, "Hollywood Rd").map(_.eids)

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


    "Card one attr" - core { implicit conn =>
      for {
        e1 <- Ns.str("a").save.map(_.eid)
        _ <- e1.touch.map(_ ==> Map(":db/id" -> e1, ":Ns/str" -> "a"))

        e2 <- Ns.int(1).save.map(_.eid)
        _ <- e2.touch.map(_ ==> Map(":db/id" -> e2, ":Ns/int" -> 1))

        e3 <- Ns.long(1L).save.map(_.eid)
        _ <- e3.touch.map(_ ==> Map(":db/id" -> e3, ":Ns/long" -> 1L))

        e4 <- Ns.double(1.1).save.map(_.eid)
        _ <- e4.touch.map(_ ==> Map(":db/id" -> e4, ":Ns/double" -> 1.1))

        e5 <- Ns.bool(true).save.map(_.eid)
        _ <- e5.touch.map(_ ==> Map(":db/id" -> e5, ":Ns/bool" -> true))

        e6 <- Ns.bigInt(bigInt1).save.map(_.eid)
        _ <- e6.touch.map(_ ==> Map(":db/id" -> e6, ":Ns/bigInt" -> bigInt1))

        e7 <- Ns.bigDec(bigDec1).save.map(_.eid)
        _ <- e7.touch.map(_ ==> Map(":db/id" -> e7, ":Ns/bigDec" -> bigDec1))

        e8 <- Ns.date(date1).save.map(_.eid)
        _ <- e8.touch.map(_ ==> Map(":db/id" -> e8, ":Ns/date" -> date1))

        e9 <- Ns.uuid(uuid1).save.map(_.eid)
        _ <- e9.touch.map(_ ==> Map(":db/id" -> e9, ":Ns/uuid" -> uuid1))

        e10 <- Ns.uri(uri1).save.map(_.eid)
        _ <- e10.touch.map(_ ==> Map(":db/id" -> e10, ":Ns/uri" -> uri1))

        e11 <- Ns.enum("enum1").save.map(_.eid)
        _ <- e11.touch.map(_ ==> Map(":db/id" -> e11, ":Ns/enum" -> ":Ns.enum/enum1"))
      } yield ()
    }

    "Card many attr" - core { implicit conn =>
      for {
        e1 <- Ns.strs("a", "b").save.map(_.eid)
        _ <- e1.touch.map(_ ==> Map(":db/id" -> e1, ":Ns/strs" -> List("a", "b")))

        e2 <- Ns.ints(1, 2).save.map(_.eid)
        _ <- e2.touch.map(_ ==> Map(":db/id" -> e2, ":Ns/ints" -> List(1, 2)))

        e3 <- Ns.longs(1L, 2L).save.map(_.eid)
        _ <- e3.touch.map(_ ==> Map(":db/id" -> e3, ":Ns/longs" -> List(1L, 2L)))

        e4 <- Ns.doubles(1.1, 2.2).save.map(_.eid)
        _ <- e4.touch.map(_ ==> Map(":db/id" -> e4, ":Ns/doubles" -> List(1.1, 2.2)))

        e5 <- Ns.bools(true, false).save.map(_.eid)
        _ <- e5.touch.map(_ ==> Map(":db/id" -> e5, ":Ns/bools" -> List(false, true)))

        e6 <- Ns.dates(date1, date2).save.map(_.eid)
        _ <- e6.touch.map(_ ==> Map(":db/id" -> e6, ":Ns/dates" -> List(date1, date2)))

        e7 <- Ns.uuids(uuid1, uuid2).save.map(_.eid)
        _ <- e7.touch.map(_ ==> Map(":db/id" -> e7, ":Ns/uuids" -> List(uuid1, uuid2)))

        e8 <- Ns.uris(uri1, uri2).save.map(_.eid)
        _ <- e8.touch.map(_ ==> Map(":db/id" -> e8, ":Ns/uris" -> List(uri1, uri2)))

        e9 <- Ns.bigInts(bigInt1, bigInt2).save.map(_.eid)
        _ <- e9.touch.map(_ ==> Map(":db/id" -> e9, ":Ns/bigInts" -> List(bigInt1, bigInt2)))

        e10 <- Ns.bigDecs(bigDec1, bigDec2).save.map(_.eid)
        _ <- e10.touch.map(_ ==> Map(":db/id" -> e10, ":Ns/bigDecs" -> List(bigDec1, bigDec2)))

        e11 <- Ns.enums(enum1, enum2).save.map(_.eid)
        _ <- e11.touch.map(_ ==> Map(":db/id" -> e11, ":Ns/enums" -> List(":Ns.enums/enum1", ":Ns.enums/enum2")))
      } yield ()
    }

    "Map attr" - core { implicit conn =>
      for {
        e1 <- Ns.strMap("a" -> "aa", "b" -> "bb").save.map(_.eid)
        _ <- e1.touch.map(_ ==> Map(":db/id" -> e1, ":Ns/strMap" -> List("a@aa", "b@bb")))

        e2 <- Ns.intMap("a" -> 1, "b" -> 2).save.map(_.eid)
        _ <- e2.touch.map(_ ==> Map(":db/id" -> e2, ":Ns/intMap" -> List("a@1", "b@2")))

        e3 <- Ns.longMap("a" -> 1L, "b" -> 2L).save.map(_.eid)
        _ <- e3.touch.map(_ ==> Map(":db/id" -> e3, ":Ns/longMap" -> List("a@1", "b@2")))

        e4 <- Ns.doubleMap("a" -> 1.1, "b" -> 2.2).save.map(_.eid)
        _ <- e4.touch.map(_ ==> Map(":db/id" -> e4, ":Ns/doubleMap" -> List("a@1.1", "b@2.2")))

        e5 <- Ns.boolMap("a" -> true, "b" -> false).save.map(_.eid)
        _ <- e5.touch.map(_ ==> Map(":db/id" -> e5, ":Ns/boolMap" -> List("a@true", "b@false")))

        e6 <- Ns.dateMap("a" -> date1, "b" -> date2).save.map(_.eid)
        _ <- e6.touch.map(_ ==> Map(":db/id" -> e6, ":Ns/dateMap" -> List("a@2001-07-01", "b@2002-01-01")))

        e7 <- Ns.uuidMap("a" -> uuid1, "b" -> uuid2).save.map(_.eid)
        _ <- e7.touch.map(_ ==> Map(":db/id" -> e7, ":Ns/uuidMap" -> List(s"a@$uuid1", s"b@$uuid2")))

        e8 <- Ns.uriMap("a" -> uri1, "b" -> uri2).save.map(_.eid)
        _ <- e8.touch.map(_ ==> Map(":db/id" -> e8, ":Ns/uriMap" -> List(s"a@$uri1", s"b@$uri2")))

        e9 <- Ns.bigIntMap("a" -> bigInt1, "b" -> bigInt2).save.map(_.eid)
        _ <- e9.touch.map(_ ==> Map(":db/id" -> e9, ":Ns/bigIntMap" -> List(s"a@1", s"b@2")))

        e10 <- Ns.bigDecMap("a" -> bigDec1, "b" -> bigDec2).save.map(_.eid)
        _ <- e10.touch.map(_ ==> Map(":db/id" -> e10, ":Ns/bigDecMap" -> List(s"a@1.0", s"b@2.0")))
      } yield ()
    }


    "Ref, card one" - core { implicit conn =>
      for {
        // Note that testing small ref values will fetch attribute names with matching ids in the
        // the Datomic database! In this case, the internal Datomic attribute :db/add has id 1:
        e <- Ns.ref1(1L).save.map(_.eid)
        _ <- e.touch.map(_ ==> Map(":db/id" -> e, ":Ns/ref1" -> ":db/add"))

        // Testing ref id that is not in the database will return a failed future with a MoleculeException:
        e2 <- Ns.ref1(12345L).save.map(_.eid)
        _ <- e2.touch.recover { case MoleculeException(msg, _) =>
          msg ==> "Entity id `12345` not found in database."
        }

        // Touching a real ref id
        refId <- Ns.str("data").save.map(_.eid)
        e3 <- Ns.ref1(refId).save.map(_.eid)
        _ <- e3.touch.map(_ ==> Map(":db/id" -> e3, ":Ns/ref1" -> Map(":db/id" -> refId, ":Ns/str" -> "data")))

        // Fetching only 1 level of data will return the ref id only
        _ <- e3.touchMax(1).map(_ ==> Map(":db/id" -> e3, ":Ns/ref1" -> refId))
      } yield ()
    }


    "Ref, card many" - core { implicit conn =>
      for {
        List(e1, r1, r2) <- Ns.str.Refs1.*(Ref1.int1).insert(List(
          ("a", List(1, 2))
        )).map(_.eids)

        _ <- e1.touch.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/refs1" -> List(
            Map(
              ":db/id" -> r1,
              ":Ref1/int1" -> 1
            ),
            Map(
              ":db/id" -> r2,
              ":Ref1/int1" -> 2
            )
          ),
          ":Ns/str" -> "a"
        ))

        _ <- e1.touchMax(1).map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/refs1" -> List(r1, r2),
          ":Ns/str" -> "a"
        ))
      } yield ()
    }
  }
}
