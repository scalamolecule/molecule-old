package molecule.tests.crud

import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy}
import molecule.datomic.api.in1_out14._
import molecule.setup.core.CoreData
import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Save extends TestSuite with CoreData {

  lazy val tests = Tests {
    implicit val conn = Conn_Js(DatomicInMemProxy(CoreTestSchema.datomicPeer))

    test("Card one") {
      for {
        Right(tx) <- Ns
          .str("a")
          .int(1)
          .float(1.1f)
          .long(1L)
          .double(1.1)
          .bool(true)
          .date(date1)
          .uuid(uuid1)
          .uri(uri1)
          .bigInt(bigInt1)
          .bigDec(bigDec1)
          .enum("enum1")
          .saveAsync
        res <- Ns.e.str.int.float.long.double.bool.date.uuid.uri.bigInt.bigDec.enum.getAsync
      } yield {
        res ==> Right(List(
          (tx.eid, "a", 1, 1.1f, 1L, 1.1, true, date1, uuid1, uri1, bigInt1, bigDec1, "enum1")
        ))
      }
    }

    test("Card many") {
      for {
        Right(tx) <- Ns
          .strs("a", "b")
          .ints(1, 2)
          .floats(1.1f, 2.2f)
          .longs(10L, 20L)
          .doubles(10.1, 20.2)
          .bools(true, false)
          .dates(date1, date2)
          .uuids(uuid1, uuid2)
          .uris(uri1, uri2)
          .bigInts(bigInt1, bigInt2)
          .bigDecs(bigDec1, bigDec2)
          .enums("enum1", "enum2")
          .saveAsync
        res <- Ns.e.strs.ints.floats.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs.enums.getAsync
      } yield {
        res ==> Right(List((
          tx.eid,
          Set("a", "b"),
          Set(1, 2),
          Set(1.1f, 2.2f),
          Set(10L, 20L),
          Set(10.1, 20.2),
          Set(true, false),
          Set(date1, date2),
          Set(uuid1, uuid2),
          Set(uri1, uri2),
          Set(bigInt1, bigInt2),
          Set(bigDec1, bigDec2),
          Set("enum1", "enum2")
        )))
      }
    }

    test("Card map") {
      for {
        Right(tx) <- Ns
          .strMap(Map("a" -> "a"))
          .intMap(Map("a" -> 1))
          .floatMap(Map("a" -> 1.1f))
          .longMap(Map("a" -> 10L))
          .doubleMap(Map("a" -> 10.1))
          .boolMap(Map("a" -> true))
          .dateMap(Map("a" -> date1))
          .uuidMap(Map("a" -> uuid1))
          .uriMap(Map("a" -> uri1))
          .bigIntMap(Map("a" -> bigInt1))
          .bigDecMap(Map("a" -> bigDec1))
          .saveAsync
        res <- Ns.e.strMap.intMap.floatMap.longMap.doubleMap.boolMap.dateMap.uuidMap.uriMap.bigIntMap.bigDecMap.getAsync
      } yield {
        res ==> Right(List((
          tx.eid,
          Map("a" -> "a"),
          Map("a" -> 1),
          Map("a" -> 1.1f),
          Map("a" -> 10L),
          Map("a" -> 10.1),
          Map("a" -> true),
          Map("a" -> date1),
          Map("a" -> uuid1),
          Map("a" -> uri1),
          Map("a" -> bigInt1),
          Map("a" -> bigDec1)
        )))
      }
    }

    test("Ref") {
      for {
        _ <- Ns.int(1).Ref1.int1(2).saveAsync
        res <- Ns.int.Ref1.int1.getAsync
      } yield {
        res ==> Right(List((1, 2)))
      }
    }
  }
}
