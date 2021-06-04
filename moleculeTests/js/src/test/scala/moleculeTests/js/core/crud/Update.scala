//package moleculeTests.js.core.crud
//
//import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy}
//import molecule.datomic.api.in1_out14._
//import moleculeTests.setup.core.CoreData
//import moleculeTests.tests.core.base.dsl.CoreTest._
//import moleculeTests.tests.core.base.schema.CoreTestSchema
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//
//object Update extends TestSuite with CoreData {
//
//  def getConn = Conn_Js.inMem(CoreTestSchema)
//
//  lazy val tests = Tests {
//
//    test("Card one") {
//      test("types") {
//        implicit val conn = getConn
//        for {
//          eid <- Ns
//            .str("a")
//            .int(1)
//            .long(1L)
//            .double(1.1)
//            .bool(true)
//            .date(date1)
//            .uuid(uuid1)
//            .uri(uri1)
//            .bigInt(bigInt1)
//            .bigDec(bigDec1)
//            .enum("enum1")
//            .save.map(_.eid)
//          _ <- Ns(eid)
//            .str("b")
//            .int(2)
//            .long(2L)
//            .double(2.2)
//            .bool(false)
//            .date(date2)
//            .uuid(uuid2)
//            .uri(uri2)
//            .bigInt(bigInt2)
//            .bigDec(bigDec2)
//            .enum("enum2")
//            .update
//          res <- Ns.e.str.int.long.double.bool.date.uuid.uri.bigInt.bigDec.enum.get.map(_ ==> List(
//            (eid, "b", 2, 2L, 2.2, false, date2
//              , uuid2, uri2, bigInt2, bigDec2, "enum2")
//          ))
//        } yield ()
//      }
//
//      test("apply") {
//        implicit val conn = getConn
//        for {
//          // Initial value
//          eid <- Ns.int(1).save.map(_.eid)
//          _ <- Ns.int.get.map(_ ==> List(1))
//
//          // Apply new value
//          _ <- Ns(eid).int(2).update
//          _ <- Ns.int.get.map(_ ==> List(2))
//
//          // Apply empty value (retract)
//          _ <- Ns(eid).int().update
//          _ <- Ns.int.get.map(_ ==> Nil)
//        } yield ()
//      }
//    }
//
//    test("Card many") {
//      implicit val conn = getConn
//      for {
//        eid <- Ns
//          .strs("a", "b")
//          .ints(1, 2)
//          .longs(10L, 20L)
//          .doubles(10.1, 20.2)
//          .bools(true, false)
//          .dates(date1, date2)
//          .uuids(uuid1, uuid2)
//          .uris(uri1, uri2)
//          .bigInts(bigInt1, bigInt2)
//          .bigDecs(bigDec1, bigDec2)
//          .enums("enum1", "enum2")
//          .save.map(_.eid)
//        _ <- Ns.e.strs.ints.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs.enums.get.map {
//          _ ==> List((
//            eid,
//            Set("a", "b"),
//            Set(1, 2),
//            Set(10L, 20L),
//            Set(10.1, 20.2),
//            Set(true, false),
//            Set(date1, date2),
//            Set(uuid1, uuid2),
//            Set(uri1, uri2),
//            Set(bigInt1, bigInt2),
//            Set(bigDec1, bigDec2),
//            Set("enum1", "enum2")
//          ))
//        }
//
//      } yield ()
//    }
//
//    test("Card map") {
//      implicit val conn = getConn
//      for {
//        eid <- Ns
//          .strMap(Map("a" -> "a"))
//          .intMap(Map("a" -> 1))
//          .longMap(Map("a" -> 10L))
//          .doubleMap(Map("a" -> 10.1))
//          .boolMap(Map("a" -> true))
//          .dateMap(Map("a" -> date1))
//          .uuidMap(Map("a" -> uuid1))
//          .uriMap(Map("a" -> uri1))
//          .bigIntMap(Map("a" -> bigInt1))
//          .bigDecMap(Map("a" -> bigDec1))
//          .save.map(_.eid)
//        _ <- Ns.e.strMap.intMap.longMap.doubleMap.boolMap.dateMap.uuidMap.uriMap.bigIntMap.bigDecMap.get.map {
//          _ ==> List((
//            eid,
//            Map("a" -> "a"),
//            Map("a" -> 1),
//            Map("a" -> 10L),
//            Map("a" -> 10.1),
//            Map("a" -> true),
//            Map("a" -> date1),
//            Map("a" -> uuid1),
//            Map("a" -> uri1),
//            Map("a" -> bigInt1),
//            Map("a" -> bigDec1)
//          ))
//        }
//
//      } yield ()
//    }
//
//    test("Ref") {
//      implicit val conn = getConn
//      for {
//        _ <- Ns.int(1).Ref1.int1(2).save
//        _ <- Ns.int.Ref1.int1.get.map(_ ==> List((1, 2)))
//      } yield ()
//    }
//  }
//}
