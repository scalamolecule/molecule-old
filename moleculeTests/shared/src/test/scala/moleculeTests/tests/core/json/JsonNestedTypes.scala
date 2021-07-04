//package moleculeTests.tests.core.json
//
//import molecule.datomic.api.out11._
//import molecule.datomic.base.util.SystemPeerServer
//import moleculeTests.setup.AsyncTestSuite
//import moleculeTests.tests.core.base.dsl.CoreTest._
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//
//object JsonNestedTypes extends AsyncTestSuite {
//
//  lazy val tests = Tests {
//
//    "value" - core { implicit conn =>
//      for {
//        _ <- Ns.str.Refs1.*(Ref1.int1).insert(str1, Seq(1))
//        _ <- Ns.int.Refs1.*(Ref1.int1).insert(1, Seq(1))
//        _ <- Ns.long.Refs1.*(Ref1.int1).insert(1L, Seq(1))
//        _ <- Ns.double.Refs1.*(Ref1.int1).insert(1.0, Seq(1))
//        _ <- Ns.bool.Refs1.*(Ref1.int1).insert(true, Seq(1))
//        _ <- Ns.date.Refs1.*(Ref1.int1).insert(date1, Seq(1))
//        _ <- Ns.uuid.Refs1.*(Ref1.int1).insert(uuid1, Seq(1))
//        _ <- Ns.uri.Refs1.*(Ref1.int1).insert(uri1, Seq(1))
//        _ <- Ns.enum.Refs1.*(Ref1.int1).insert(enum1, Seq(1))
//
//        _ <- Ns.str.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.str": "a", "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 1, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.long.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.long": 1, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.double.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.double": 1.0, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.bool.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.bool": true, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.date.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.date": "2001-07-01", "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- Ns.uuid.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.uuid": "$uuid1", "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- Ns.uri.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.uri": "$uri1", "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- Ns.enum.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.enum": "enum1", "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//      } yield ()
//    }
//
//
//    "set" - core { implicit conn =>
//      for {
//        _ <- Ns.strs.Refs1.*(Ref1.int1).insert(Set("", "a", "b"), Seq(1))
//        _ <- Ns.ints.Refs1.*(Ref1.int1).insert(Set(1, 2), Seq(1))
//        _ <- Ns.longs.Refs1.*(Ref1.int1).insert(Set(1L, 2L), Seq(1))
//        _ <- Ns.doubles.Refs1.*(Ref1.int1).insert(Set(1.0, 2.0), Seq(1))
//        _ <- Ns.bools.Refs1.*(Ref1.int1).insert(Set(true, false), Seq(1))
//        _ <- Ns.dates.Refs1.*(Ref1.int1).insert(Set(date1, date2), Seq(1))
//        _ <- Ns.uuids.Refs1.*(Ref1.int1).insert(Set(uuid1), Seq(1))
//        _ <- Ns.uris.Refs1.*(Ref1.int1).insert(Set(uri1, uri2), Seq(1))
//        _ <- Ns.enums.Refs1.*(Ref1.int1).insert(Set(enum1, enum2), Seq(1))
//        _ <- Ns.bigInts.Refs1.*(Ref1.int1).insert(Set(bigInt1, bigInt2), Seq(1))
//        _ <- Ns.bigDecs.Refs1.*(Ref1.int1).insert(Set(bigDec1, bigDec2), Seq(1))
//
//        _ <- Ns.strs.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.strs": ["", "a", "b"], "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.ints.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.ints": [1, 2], "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.longs.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.longs": [1, 2], "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.doubles.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.doubles": [2.0, 1.0], "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.bools.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.bools": [true, false], "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.dates.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.dates": ["2001-07-01", "2002-01-01"], "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- Ns.uuids.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.uuids": ["$uuid1"], "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- if (system == SystemPeerServer) {
//          Ns.uris.Refs1.*(Ref1.int1).getJson.map(_ ==>
//            s"""[
//               |{"Ns.uris": ["$uri2", "$uri1"], "Ns.refs1": [
//               |   {"Ref1.int1": 1}]}
//               |]""".stripMargin)
//        } else {
//          Ns.uris.Refs1.*(Ref1.int1).getJson.map(_ ==>
//            s"""[
//               |{"Ns.uris": ["$uri1", "$uri2"], "Ns.refs1": [
//               |   {"Ref1.int1": 1}]}
//               |]""".stripMargin)
//        }
//
//        _ <- Ns.enums.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.enums": ["$enum1", "$enum2"], "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- Ns.bigInts.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.bigInts": [1, 2], "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.bigDecs.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.bigDecs": [2.0, 1.0], "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//      } yield ()
//    }
//
//
//    "map" - core { implicit conn =>
//      for {
//        _ <- Ns.strMap.Refs1.*(Ref1.int1).insert(Map("a" -> "A", "b" -> "B"), Seq(1))
//        _ <- Ns.intMap.Refs1.*(Ref1.int1).insert(Map("a" -> 1, "b" -> 2), Seq(1))
//        _ <- Ns.longMap.Refs1.*(Ref1.int1).insert(Map("a" -> 1L, "b" -> 2L), Seq(1))
//        _ <- Ns.doubleMap.Refs1.*(Ref1.int1).insert(Map("a" -> 1.0, "b" -> 2.0), Seq(1))
//        _ <- Ns.boolMap.Refs1.*(Ref1.int1).insert(Map("a" -> true, "b" -> false), Seq(1))
//        _ <- Ns.dateMap.Refs1.*(Ref1.int1).insert(Map("a" -> date1, "b" -> date2), Seq(1))
//        _ <- Ns.uuidMap.Refs1.*(Ref1.int1).insert(Map("a" -> uuid1), Seq(1))
//        _ <- Ns.uriMap.Refs1.*(Ref1.int1).insert(Map("a" -> uri1, "b" -> uri2), Seq(1))
//        _ <- Ns.bigIntMap.Refs1.*(Ref1.int1).insert(Map("a" -> bigInt1, "b" -> bigInt2), Seq(1))
//        _ <- Ns.bigDecMap.Refs1.*(Ref1.int1).insert(Map("a" -> bigDec1, "b" -> bigDec2), Seq(1))
//
//        _ <- Ns.strMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.strMap": {"b": "B", "a": "A"}, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.intMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.intMap": {"b": 2, "a": 1}, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.longMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.longMap": {"b": 2, "a": 1}, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.doubleMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.doubleMap": {"b": 2.0, "a": 1.0}, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.boolMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.boolMap": {"b": false, "a": true}, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.dateMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.dateMap": {"a": "2001-07-01", "b": "2002-01-01"}, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- Ns.uuidMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.uuidMap": {"a": "$uuid1"}, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- Ns.uriMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.uriMap": {"b": "$uri2", "a": "$uri1"}, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]}
//             |]""".stripMargin)
//
//        _ <- Ns.bigIntMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.bigIntMap": {"b": 2, "a": 1}, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//
//        _ <- Ns.bigDecMap.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.bigDecMap": {"b": 2.0, "a": 1.0}, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]}
//            |]""".stripMargin)
//      } yield ()
//    }
//
//
//    "Optional value" - core { implicit conn =>
//      for {
//        _ <- Ns.int.str$.Refs1.*(Ref1.int1).insert(List((1, None, Seq(1)), (1, Some(""), Seq(2)), (1, Some("c"), Seq(3))))
//        _ <- Ns.long.int$.Refs1.*(Ref1.int1).insert(List((2, None, Seq(1)), (2, Some(2), Seq(2))))
//        _ <- Ns.int.long$.Refs1.*(Ref1.int1).insert(List((3, None, Seq(1)), (3, Some(20L), Seq(2))))
//        _ <- Ns.int.double$.Refs1.*(Ref1.int1).insert(List((5, None, Seq(1)), (5, Some(2.0), Seq(2))))
//        _ <- Ns.int.bool$.Refs1.*(Ref1.int1).insert(List((6, None, Seq(1)), (6, Some(true), Seq(2)), (6, Some(false), Seq(3))))
//        _ <- Ns.int.date$.Refs1.*(Ref1.int1).insert(List((7, None, Seq(1)), (7, Some(date2), Seq(2))))
//        _ <- Ns.int.uuid$.Refs1.*(Ref1.int1).insert(List((8, None, Seq(1)), (8, Some(uuid2), Seq(2))))
//        _ <- Ns.int.uri$.Refs1.*(Ref1.int1).insert(List((9, None, Seq(1)), (9, Some(uri2), Seq(2))))
//        _ <- Ns.int.enum$.Refs1.*(Ref1.int1).insert(List((10, None, Seq(1)), (10, Some(enum2), Seq(2))))
//        _ <- Ns.int.bigInt$.Refs1.*(Ref1.int1).insert(List((11, None, Seq(1)), (11, Some(bigInt2), Seq(2))))
//        _ <- Ns.int.bigDec$.Refs1.*(Ref1.int1).insert(List((12, None, Seq(1)), (12, Some(bigDec2), Seq(2))))
//
//        _ <- Ns.int(1).str$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 1, "Ns.str": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 1, "Ns.str": "", "Ns.refs1": [
//            |   {"Ref1.int1": 2}]},
//            |{"Ns.int": 1, "Ns.str": "c", "Ns.refs1": [
//            |   {"Ref1.int1": 3}]}
//            |]""".stripMargin)
//
//        _ <- Ns.long(2L).int$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.long": 2, "Ns.int": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.long": 2, "Ns.int": 2, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(3).long$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 3, "Ns.long": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 3, "Ns.long": 20, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(5).double$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 5, "Ns.double": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 5, "Ns.double": 2.0, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(6).bool$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 6, "Ns.bool": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 6, "Ns.bool": true, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]},
//            |{"Ns.int": 6, "Ns.bool": false, "Ns.refs1": [
//            |   {"Ref1.int1": 3}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(7).date$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 7, "Ns.date": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 7, "Ns.date": "2002-01-01", "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(8).uuid$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 8, "Ns.uuid": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 8, "Ns.uuid": "$uuid2", "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(9).uri$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 9, "Ns.uri": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 9, "Ns.uri": "$uri2", "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(10).enum$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 10, "Ns.enum": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 10, "Ns.enum": "$enum2", "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(11).bigInt$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 11, "Ns.bigInt": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 11, "Ns.bigInt": $bigInt2, "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(12).bigDec$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 12, "Ns.bigDec": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 12, "Ns.bigDec": $bigDec2, "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//      } yield ()
//    }
//
//
//    "Optional set" - core { implicit conn =>
//      for {
//        _ <- Ns.int.strs$.Refs1.*(Ref1.int1).insert(List((1, None, Seq(1)), (1, Some(Set("", "b")), Seq(2))))
//        _ <- Ns.long.ints$.Refs1.*(Ref1.int1).insert(List((2, None, Seq(1)), (2, Some(Set(1, 2)), Seq(2))))
//        _ <- Ns.int.longs$.Refs1.*(Ref1.int1).insert(List((3, None, Seq(1)), (3, Some(Set(21L, 22L)), Seq(2))))
//        _ <- Ns.int.doubles$.Refs1.*(Ref1.int1).insert(List((5, None, Seq(1)), (5, Some(Set(1.0, 2.0)), Seq(2))))
//        _ <- Ns.int.bools$.Refs1.*(Ref1.int1).insert(List((6, None, Seq(1)), (6, Some(Set(true, false)), Seq(2))))
//        _ <- Ns.int.dates$.Refs1.*(Ref1.int1).insert(List((7, None, Seq(1)), (7, Some(Set(date1, date2)), Seq(2))))
//        _ <- Ns.int.uuids$.Refs1.*(Ref1.int1).insert(List((8, None, Seq(1)), (8, Some(Set(uuid1, uuid2)), Seq(2))))
//        _ <- Ns.int.uris$.Refs1.*(Ref1.int1).insert(List((9, None, Seq(1)), (9, Some(Set(uri1, uri2)), Seq(2))))
//        _ <- Ns.int.enums$.Refs1.*(Ref1.int1).insert(List((10, None, Seq(1)), (10, Some(Set(enum1, enum2)), Seq(2))))
//        _ <- Ns.int.bigInts$.Refs1.*(Ref1.int1).insert(List((11, None, Seq(1)), (11, Some(Set(bigInt1, bigInt2)), Seq(2))))
//        _ <- Ns.int.bigDecs$.Refs1.*(Ref1.int1).insert(List((12, None, Seq(1)), (12, Some(Set(bigDec1, bigDec2)), Seq(2))))
//
//        _ <- Ns.int(1).strs$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 1, "Ns.strs": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 1, "Ns.strs": ["", "b"], "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.long(2L).ints$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.long": 2, "Ns.ints": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.long": 2, "Ns.ints": [1, 2], "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(3).longs$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 3, "Ns.longs": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 3, "Ns.longs": [21, 22], "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(5).doubles$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 5, "Ns.doubles": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 5, "Ns.doubles": [1.0, 2.0], "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        //    if (system == DatomicPeer) {
//        //      // Bug, where only one boolean value is returned
//        //      _ <- Ns.int(6).bools$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//        //        """[
//        //          |{"Ns.int": 6, "Ns.bools": null, "Ns.refs1": [
//        //          |   {"Ref1.int1": 1}]},
//        //          |{"Ns.int": 6, "Ns.bools": [true], "Ns.refs1": [
//        //          |   {"Ref1.int1": 2}]}
//        //          |]""".stripMargin)
//        //    } else {
//        // Correct output
//        _ <- Ns.int(6).bools$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 6, "Ns.bools": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 6, "Ns.bools": [false, true], "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//        //    }
//
//        _ <- Ns.int(7).dates$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 7, "Ns.dates": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 7, "Ns.dates": ["2001-07-01", "2002-01-01"], "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(8).uuids$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 8, "Ns.uuids": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 8, "Ns.uuids": ["$uuid1", "$uuid2"], "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(9).uris$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 9, "Ns.uris": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 9, "Ns.uris": ["$uri1", "$uri2"], "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(10).enums$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 10, "Ns.enums": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 10, "Ns.enums": ["$enum1", "$enum2"], "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(11).bigInts$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 11, "Ns.bigInts": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 11, "Ns.bigInts": [$bigInt1, $bigInt2], "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(12).bigDecs$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 12, "Ns.bigDecs": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 12, "Ns.bigDecs": [$bigDec1, $bigDec2], "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//      } yield ()
//    }
//
//
//    "Optional map" - core { implicit conn =>
//      for {
//        _ <- Ns.int.strMap$.Refs1.*(Ref1.int1).insert(List((1, None, Seq(1)), (1, Some(Map("a" -> "A", "b" -> "B")), Seq(2))))
//        _ <- Ns.long.intMap$.Refs1.*(Ref1.int1).insert(List((2, None, Seq(1)), (2, Some(Map("a" -> 1, "b" -> 2)), Seq(2))))
//        _ <- Ns.int.longMap$.Refs1.*(Ref1.int1).insert(List((3, None, Seq(1)), (3, Some(Map("a" -> 1L, "b" -> 2L)), Seq(2))))
//        _ <- Ns.int.doubleMap$.Refs1.*(Ref1.int1).insert(List((5, None, Seq(1)), (5, Some(Map("a" -> 1.0, "b" -> 2.0)), Seq(2))))
//        _ <- Ns.int.boolMap$.Refs1.*(Ref1.int1).insert(List((6, None, Seq(1)), (6, Some(Map("a" -> true, "b" -> false)), Seq(2))))
//        _ <- Ns.int.dateMap$.Refs1.*(Ref1.int1).insert(List((7, None, Seq(1)), (7, Some(Map("a" -> date1, "b" -> date2)), Seq(2))))
//        _ <- Ns.int.uuidMap$.Refs1.*(Ref1.int1).insert(List((8, None, Seq(1)), (8, Some(Map("a" -> uuid1, "b" -> uuid2)), Seq(2))))
//        _ <- Ns.int.uriMap$.Refs1.*(Ref1.int1).insert(List((9, None, Seq(1)), (9, Some(Map("a" -> uri1, "b" -> uri2)), Seq(2))))
//        _ <- Ns.int.bigIntMap$.Refs1.*(Ref1.int1).insert(List((11, None, Seq(1)), (11, Some(Map("a" -> bigInt1, "b" -> bigInt2)), Seq(2))))
//        _ <- Ns.int.bigDecMap$.Refs1.*(Ref1.int1).insert(List((12, None, Seq(1)), (12, Some(Map("a" -> bigDec1, "b" -> bigDec2)), Seq(2))))
//
//        _ <- Ns.int.apply(1).strMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 1, "Ns.strMap": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 1, "Ns.strMap": {"a": "A", "b": "B"}, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.long(2L).intMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.long": 2, "Ns.intMap": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.long": 2, "Ns.intMap": {"a": 1, "b": 2}, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(3).longMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 3, "Ns.longMap": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 3, "Ns.longMap": {"a": 1, "b": 2}, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(5).doubleMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 5, "Ns.doubleMap": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 5, "Ns.doubleMap": {"a": 1.0, "b": 2.0}, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(6).boolMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """[
//            |{"Ns.int": 6, "Ns.boolMap": null, "Ns.refs1": [
//            |   {"Ref1.int1": 1}]},
//            |{"Ns.int": 6, "Ns.boolMap": {"a": true, "b": false}, "Ns.refs1": [
//            |   {"Ref1.int1": 2}]}
//            |]""".stripMargin)
//
//        _ <- Ns.int(7).dateMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 7, "Ns.dateMap": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 7, "Ns.dateMap": {"a": "2001-07-01", "b": "2002-01-01"}, "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(8).uuidMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 8, "Ns.uuidMap": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 8, "Ns.uuidMap": {"a": "$uuid1", "b": "$uuid2"}, "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(9).uriMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 9, "Ns.uriMap": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 9, "Ns.uriMap": {"a": "$uri1", "b": "$uri2"}, "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(11).bigIntMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 11, "Ns.bigIntMap": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 11, "Ns.bigIntMap": {"a": $bigInt1, "b": $bigInt2}, "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//
//        _ <- Ns.int(12).bigDecMap$.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          s"""[
//             |{"Ns.int": 12, "Ns.bigDecMap": null, "Ns.refs1": [
//             |   {"Ref1.int1": 1}]},
//             |{"Ns.int": 12, "Ns.bigDecMap": {"a": $bigDec1, "b": $bigDec2}, "Ns.refs1": [
//             |   {"Ref1.int1": 2}]}
//             |]""".stripMargin)
//      } yield ()
//    }
//  }
//}