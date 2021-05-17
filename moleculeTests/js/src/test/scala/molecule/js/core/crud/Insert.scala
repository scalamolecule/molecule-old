package molecule.js.core.crud

import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy}
import molecule.datomic.api.in1_out14._
import molecule.setup.core.CoreData
import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Insert extends TestSuite with CoreData {

  lazy val tests = Tests {
    implicit val conn = Conn_Js.inMem(CoreTestSchema)

    test("Card one, single elements") {
      for {
        _ <- Ns.str.int.long.double.bool.date.uuid.uri.bigInt.bigDec.enum.insertAsync(
          "a", 1, 1L, 1.1, true, date1, uuid1, uri1, bigInt1, bigDec1, "enum1"
        )
        res <- Ns.str.int.long.double.bool.date.uuid.uri.bigInt.bigDec.enum.getAsync
      } yield {
        res ==> List(
          ("a", 1, 1L, 1.1, true, date1, uuid1, uri1, bigInt1, bigDec1, "enum1")
        )
      }
    }

    test("Card one, multiple tuples") {
      for {
        _ <- Ns.str.int.long.double.bool.date.uuid.uri.bigInt.bigDec.enum insertAsync List(
          ("a", 1, 1L, 1.1, true, date1, uuid1, uri1, bigInt1, bigDec1, "enum1"),
          ("b", 2, 2L, 2.2, false, date2, uuid2, uri2, bigInt2, bigDec2, "enum2")
        )
        res <- Ns.str.int.long.double.bool.date.uuid.uri.bigInt.bigDec.enum.getAsync
      } yield {
        res.sortBy(_._1) ==> List(
          ("a", 1, 1L, 1.1, true, date1, uuid1, uri1, bigInt1, bigDec1, "enum1"),
          ("b", 2, 2L, 2.2, false, date2, uuid2, uri2, bigInt2, bigDec2, "enum2"),
        )
      }
    }

    test("Card many, single elements") {
      for {
        _ <- Ns.strs.ints.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs.enums.insertAsync(
          Set("a", "b"),
          Set(1, 2),
          Set(10L, 20L),
          Set(10.1, 20.2),
          Set(true, false),
          Set(date1, date2),
          Set(uuid1, uuid2),
          Set(uri1, uri2),
          Set(bigInt1, bigInt2),
          Set(bigDec1, bigDec2),
          Set("enum1", "enum2")
        )
        res <- Ns.strs.ints.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs.enums.getAsync
      } yield {
        res ==> List(
          (
            Set("a", "b"),
            Set(1, 2),
            Set(10L, 20L),
            Set(10.1, 20.2),
            Set(true, false),
            Set(date1, date2),
            Set(uuid1, uuid2),
            Set(uri1, uri2),
            Set(bigInt1, bigInt2),
            Set(bigDec1, bigDec2),
            Set("enum1", "enum2")
          )
        )
      }
    }

    test("Card many, multiple tuples") {
      for {
        txr <- Ns.strs.ints.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs.enums insertAsync List(
          (
            Set("a", "b"),
            Set(1, 2),
            Set(10L, 20L),
            Set(10.1, 20.2),
            Set(true, false),
            Set(date1, date2),
            Set(uuid1, uuid2),
            Set(uri1, uri2),
            Set(bigInt1, bigInt2),
            Set(bigDec1, bigDec2),
            Set("enum1", "enum2")
          ),
          (
            Set("c", "d"),
            Set(3, 4),
            Set(30L, 40L),
            Set(30.3, 40.4),
            Set(true, false),
            Set(date3, date4),
            Set(uuid3, uuid4),
            Set(uri3, uri4),
            Set(bigInt3, bigInt4),
            Set(bigDec3, bigDec4),
            Set("enum3", "enum4")
          )
        )
        // Lookup separate entity ids
        List(e1, e2) = txr.eids
        res1 <- Ns.e.strs.ints.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs.enums.getAsync
        // Cardinality many values coagulate into single Set's
        res2 <- Ns.strs.ints.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs.enums.getAsync
      } yield {
        res1 ==> List(
          (
            e1,
            Set("a", "b"),
            Set(1, 2),
            Set(10L, 20L),
            Set(10.1, 20.2),
            Set(true, false),
            Set(date1, date2),
            Set(uuid1, uuid2),
            Set(uri1, uri2),
            Set(bigInt1, bigInt2),
            Set(bigDec1, bigDec2),
            Set("enum1", "enum2")
          ),
          (
            e2,
            Set("c", "d"),
            Set(3, 4),
            Set(30L, 40L),
            Set(30.3, 40.4),
            Set(true, false),
            Set(date3, date4),
            Set(uuid3, uuid4),
            Set(uri3, uri4),
            Set(bigInt3, bigInt4),
            Set(bigDec3, bigDec4),
            Set("enum3", "enum4")
          )
        )
        res2 ==> List(
          (
            Set("a", "b", "c", "d"),
            Set(1, 2, 3, 4),
            Set(10L, 20L, 30L, 40L),
            Set(10.1, 20.2, 30.3, 40.4),
            Set(true, false),
            Set(date1, date2, date3, date4),
            Set(uuid1, uuid2, uuid3, uuid4),
            Set(uri1, uri2, uri3, uri4),
            Set(bigInt1, bigInt2, bigInt3, bigInt4),
            Set(bigDec1, bigDec2, bigDec3, bigDec4),
            Set("enum1", "enum2", "enum3", "enum4"),
          )
        )
      }
    }

    test("Card map, single elements") {
      for {
        _ <- Ns.strMap.intMap.longMap.doubleMap.boolMap.dateMap.uuidMap.uriMap.bigIntMap.bigDecMap.insertAsync(
          Map("a" -> "a"),
          Map("a" -> 1),
          Map("a" -> 10L),
          Map("a" -> 10.1),
          Map("a" -> true),
          Map("a" -> date1),
          Map("a" -> uuid1),
          Map("a" -> uri1),
          Map("a" -> bigInt1),
          Map("a" -> bigDec1),
        )
        res <- Ns.strMap.intMap.longMap.doubleMap.boolMap.dateMap.uuidMap.uriMap.bigIntMap.bigDecMap.getAsync
      } yield {
        res ==> List(
          (
            Map("a" -> "a"),
            Map("a" -> 1),
            Map("a" -> 10L),
            Map("a" -> 10.1),
            Map("a" -> true),
            Map("a" -> date1),
            Map("a" -> uuid1),
            Map("a" -> uri1),
            Map("a" -> bigInt1),
            Map("a" -> bigDec1),
          )
        )
      }
    }

    test("Card map, multiple tuples") {
      for {
        txr <- Ns.strMap.intMap.longMap.doubleMap.boolMap.dateMap.uuidMap.uriMap.bigIntMap.bigDecMap insertAsync List(
          (
            Map("a" -> "a"),
            Map("a" -> 1),
            Map("a" -> 10L),
            Map("a" -> 10.1),
            Map("a" -> true),
            Map("a" -> date1),
            Map("a" -> uuid1),
            Map("a" -> uri1),
            Map("a" -> bigInt1),
            Map("a" -> bigDec1),
          ),
          (
            Map("b" -> "b"),
            Map("b" -> 2),
            Map("b" -> 20L),
            Map("b" -> 20.2),
            Map("b" -> false),
            Map("b" -> date2),
            Map("b" -> uuid2),
            Map("b" -> uri2),
            Map("b" -> bigInt2),
            Map("b" -> bigDec2),
          )
        )
        // Lookup separate entity ids
        List(e1, e2) = txr.eids
        res1 <- Ns.e.strMap.intMap.longMap.doubleMap.boolMap.dateMap.uuidMap.uriMap.bigIntMap.bigDecMap.getAsync
        // Cardinality many values coagulate into single Maps (Sets)
        res2 <- Ns.strMap.intMap.longMap.doubleMap.boolMap.dateMap.uuidMap.uriMap.bigIntMap.bigDecMap.getAsync
      } yield {
        res1 ==> List(
          (
            e1,
            Map("a" -> "a"),
            Map("a" -> 1),
            Map("a" -> 10L),
            Map("a" -> 10.1),
            Map("a" -> true),
            Map("a" -> date1),
            Map("a" -> uuid1),
            Map("a" -> uri1),
            Map("a" -> bigInt1),
            Map("a" -> bigDec1),
          ),
          (
            e2,
            Map("b" -> "b"),
            Map("b" -> 2),
            Map("b" -> 20L),
            Map("b" -> 20.2),
            Map("b" -> false),
            Map("b" -> date2),
            Map("b" -> uuid2),
            Map("b" -> uri2),
            Map("b" -> bigInt2),
            Map("b" -> bigDec2),
          )
        )
        res2 ==> List(
          (
            Map("a" -> "a", "b" -> "b"),
            Map("a" -> 1, "b" -> 2),
            Map("a" -> 10L, "b" -> 20L),
            Map("a" -> 10.1, "b" -> 20.2),
            Map("a" -> true, "b" -> false),
            Map("a" -> date1, "b" -> date2),
            Map("a" -> uuid1, "b" -> uuid2),
            Map("a" -> uri1, "b" -> uri2),
            Map("a" -> bigInt1, "b" -> bigInt2),
            Map("a" -> bigDec1, "b" -> bigDec2),
          )
        )
      }
    }
  }
}
