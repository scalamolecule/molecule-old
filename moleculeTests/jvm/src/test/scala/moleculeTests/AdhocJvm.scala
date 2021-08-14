//package moleculeTests
//
//import java.util
//import molecule.datomic.api.in3_out11._
//import moleculeTests.setup.AsyncTestSuite
//import moleculeTests.tests.core.base.dsl.CoreTest._
//import utest._
//import scala.concurrent.{ExecutionContext, Future}
//import java.util.{Collections, Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
//import molecule.core.ast.elements._
//import molecule.datomic.base.marshalling.NestedOptRows2packed
//import molecule.core.marshalling.unpack.UnpackTypes
//import molecule.core.marshalling.attrIndexes._
//import molecule.core.util.Helpers
//import molecule.datomic.base.facade.Conn
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.collection.mutable.ListBuffer
//import molecule.core.util.testing.expectCompileError
//import molecule.datomic.base.transform.Model2Query
//import moleculeTests.tests.core.base.schema.CoreTestSchema
//import scala.util.control.NonFatal
//
//object AdhocJvm extends AsyncTestSuite with Helpers with UnpackTypes {
//
//
//  lazy val tests = Tests {
//
//    //            "vs2tpl" - {
//    //              val vs = List(
//    //                "A",
//    //                "◄", // end of string
//    //
//    //                "11",
//    //                "12",
//    //                "a",
//    //                "◄", // end of string
//    //
//    //                "110",
//    //                "120",
//    //                "aa",
//    //                "◄", // end of string
//    //
//    //                "◄", // end of subs
//    //
//    //                "1",
//    //
//    //                //        "B",
//    //                //        "◄",
//    //                //        "13",
//    //                //        "◄",
//    //                //        "b",
//    //                //        "◄",
//    //                //        "◄",
//    //                //        "1",
//    //                //
//    //                //        "C",
//    //                //        "◄",
//    //                //        "◄",
//    //                //        "14",
//    //                //        "c",
//    //                //        "◄",
//    //                //        "◄",
//    //                //        "1",
//    //                //
//    //                //        "D",
//    //                //        "◄",
//    //                //        "◄",
//    //                //        "◄",
//    //                //        "d",
//    //                //        "◄",
//    //                //        "◄",
//    //                //        "1",
//    //                //
//    //                //        "E",
//    //                //        "◄",
//    //                //        "◄◄",
//    //                //        "1"
//    //              ).iterator
//    //
//    //              def sub = {
//    //                v = vs.next()
//    //                if (v == "◄◄") {
//    //                  Nil
//    //                } else {
//    //                  //          val buf = new ListBuffer[(Option[Int], Option[Int], String)]
//    //                  val buf = new ListBuffer[Product]
//    //                  first = true
//    //                  do {
//    //                    buf.append(Tuple1(unpackOptOneInt(v)))
//    //    //                buf.append(
//    //    //                  (
//    //    //                    unpackOptOneInt(v),
//    //    ////                    unpackOptOneInt(vs.next()),
//    //    ////                    unpackOneString(vs.next(), vs)
//    //    //                  )
//    //    //                )
//    //                    v = vs.next()
//    //                  } while (v != "◄")
//    //                  buf.toList
//    //                }
//    //              }
//    //
//    //              (unpackOneString(vs.next(), vs), sub, unpackOneInt(vs.next()))
//    //    //            .asInstanceOf[(String, List[(Option[Int], Option[Int], String)], Int)] ==> 42
//    //                .asInstanceOf[(String, List[Option[Int]], Int)] ==> 42
//    //            }
//
//
//    "core" - core { implicit futConn =>
//      for {
//        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
//        conn <- futConn
//
//
//        //        _ <- Ns.str.Refs1.*(Ref1.int1).Tx(Ref3.int3_(7)) insert List(
//        //          (
//        //            """multi
//        //              |line""".stripMargin, List(1, 2)),
//        //          ("B", Nil)
//        //        )
//        //
//        //
//        //
//        //
//        //        //        //        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3).get.map(_ ==> List(
//        //        //        //          (true, List("a", "b"), 1)
//        //        //        //        ))
//        //        //
//        //        //        //        _ <- Ns.str.Refs1.*?(Ref1.int1).Tx(Ref3.int3).inspectGet
//        //        _ <- Ns.str.Refs1.*?(Ref1.int1).Tx(Ref3.int3).get.map(_ ==> List(
//        //          ("""multi
//        //             |line""".stripMargin, List(1, 2), 7),
//        //          ("B", Nil, 7)
//        //        ))
//        //
//        //        rows <- conn.qRaw(
//        //          """[:find  ?b
//        //            |        (pull ?a__1 [
//        //            |          {(:Ns/refs1 :limit nil) [
//        //            |            (:Ref1/int1 :limit nil)]}]) ?c
//        //            | :where [(identity ?a) ?a__1]
//        //            |        [?a :Ns/str ?b ?tx]
//        //            |        [?tx :Ref3/int3 ?c]]""".stripMargin
//        //        )
//        //        _ = {
//        //          rows.forEach(row => println(row))
//        //
//        //          val indexes = Indexes("Ns", 2, List(
//        //            AttrIndex("str", 0, 0, 0, false),
//        //            Indexes("Refs1", 2, List(
//        //              AttrIndex("int1", 1, 1, 1, false))),
//        //            Indexes("Tx", 1, List(
//        //              Indexes("Ref3", 1, List(
//        //                AttrIndex("int3", 1, 1, 0, true)))))))
//        //
//        //          val str = NestedOptRows2String(rows, 10, 10, 10L, Nil, Nil, indexes).get
//        //
//        //          println("=========================================")
//        //          println(str)
//        //        }
//
//        /*
//--------------------------------------------------------------------------
//OUTPUTS:
//1: [A  {:Ns/refs1 [{:Ref1/int1 1} {:Ref1/int1 2}]}  7]
//2: [B  null  7]
//(showing up to 500 rows)
//--------------------------------------------------------------------------
//
//Result:
//[["Multi\nline" {:Ns/refs1 [{:Ref1/int1 1} {:Ref1/int1 2}]} 7]]
//{:Ns/refs1 [{:Ref1/int1 1} {:Ref1/int1 2}]}
//class clojure.lang.PersistentArrayMap
//[{:Ref1/int1 1} {:Ref1/int1 2}]
//class clojure.lang.PersistentVector
//{:Ref1/int1 1}
//{:Ref1/int1 1}
//class clojure.lang.PersistentArrayMap
//1
//
//BuilderObj("", "Ns", 2, List(
//  BuilderProp("Ns_str", "str", "String", <cast>, <json>, None),
//  BuilderObj("Ns__Refs1", "Refs1", 2, List(
//    BuilderProp("Ref1_int1", "int1", "Int", <cast>, <json>, None))),
//  BuilderObj("Tx_", "Tx", 1, List(
//    BuilderObj("Ref3_", "Ref3", 1, List(
//      BuilderProp("Ref3_int3", "int3", "Int", <cast>, <json>, None)))))))
//------------------------------------------------
//Indexes("Ns", 2, List(
//  AttrIndex("str", 0, 0, 0, false),
//  Indexes("Refs1", 2, List(
//    AttrIndex("int1", 1, 1, 1, false))),
//  Indexes("Tx", 1, List(
//    Indexes("Ref3", 1, List(
//      AttrIndex("int3", 1, 1, 0, true)))))))
//======================================================
//
//Multi     // start of row ------------------------
//line
//◄         // end of string
//1
//2
//◄         // end of multiple int1
//7
//
//B         // start of row ------------------------
//◄         // end of string
//◄         // end of multiple int1 (none)
//7
//
//
//
//\u25ba = ◄
//https://en.wikipedia.org/wiki/List_of_Unicode_characters
//
//         */
//
//
//        //        _ <- Ns.str.int insert List(("a", 1), ("b", 2))
//        //
//        //        _ <- Ns.str.int.get.map(_ ==> List(("a", 1), ("b", 2)))
//
//
//        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(1)) inspectInsert List(
//        //          ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa"))),
//        //          ("B", List((Some(13), None, "b"))),
//        //          ("C", List((None, Some(14), "c"))),
//        //          ("D", List((None, None, "d"))),
//        //          ("E", List())
//        //        )
//        //                        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).inspectGet
//
//        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(1)) insert List(
//          ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa"))),
//          ("B", List((Some(13), None, "b"))),
//          ("C", List((None, Some(14), "c"))),
//          ("D", List((None, None, "d"))),
//          ("E", List())
//        )
//
//
////        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
////          ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa")), 1),
////          ("B", List((Some(13), None, "b")), 1),
////          ("C", List((None, Some(14), "c")), 1),
////          ("D", List((None, None, "d")), 1),
////          ("E", List(), 1)
////        ))
//
//        rows <- conn.qRaw(
//          """[:find  ?b
//            |        (pull ?a__1 [
//            |          {(:Ns/refs1 :limit nil) [
//            |            (:Ref1/int1 :limit nil :default "__none__")
//            |            {(:Ref1/ref2 :limit nil :default "__none__") [
//            |              (:Ref2/int2 :limit nil :default "__none__")
//            |              (:Ref2/str2 :limit nil)]}]}]) ?c
//            | :where [(identity ?a) ?a__1]
//            |        [?a :Ns/str ?b ?tx]
//            |        [?tx :Ref3/int3 ?c]]""".stripMargin
//        )
//        _ = {
//          rows.forEach(row => println(row))
//
//          val indexes = Indexes("Ns", 2, List(
//            AttrIndex("str", 0, 0, 1, false),
//            Indexes("Refs1", 2, List(
//              AttrIndex("int1$", 14, 11, 1, false),
//              Indexes("Ref2", 1, List(
//                AttrIndex("int2$", 14, 11, 0, false),
//                AttrIndex("str2", 0, 0, 0, false))))),
//            Indexes("Tx", 1, List(
//              Indexes("Ref3", 1, List(
//                AttrIndex("int3", 1, 1, 0, true)))))))
//
//          val str = NestedOptRows2packed(rows, 10, 10, 10L, Nil, Nil, indexes).getPacked
//
//          println("=========================================" + str)
//        }
//
//        /*
//[:find  ?b
//        (pull ?a__1 [
//          {(:Ns/refs1 :limit nil) [
//            (:Ref1/int1 :limit nil :default "__none__")
//            {(:Ref1/ref2 :limit nil :default "__none__") [
//              (:Ref2/int2 :limit nil :default "__none__")
//              (:Ref2/str2 :limit nil)]}]}]) ?c
// :where [(identity ?a) ?a__1]
//        [?a :Ns/str ?b ?tx]
//        [?tx :Ref3/int3 ?c]]
//--------------------------------------------------------------------------
//OUTPUTS:
//1: [A  {:Ns/refs1 [{:Ref1/int1 11, :Ref1/ref2 {:Ref2/int2 12, :Ref2/str2 "a"}}]}  1]
//2: [B  {:Ns/refs1 [{:Ref1/int1 13, :Ref1/ref2 {:Ref2/int2 "__none__", :Ref2/str2 "b"}}]}  1]
//3: [C  {:Ns/refs1 [{:Ref1/int1 "__none__", :Ref1/ref2 {:Ref2/int2 14, :Ref2/str2 "c"}}]}  1]
//4: [D  {:Ns/refs1 [{:Ref1/int1 "__none__", :Ref1/ref2 {:Ref2/int2 "__none__", :Ref2/str2 "d"}}]}  1]
//5: [E  null  1]
//(showing up to 500 rows)
//--------------------------------------------------------------------------
//
//Result:
//[["A" {:Ns/refs1 [{:Ref1/int1 11, :Ref1/ref2 {:Ref2/int2 12, :Ref2/str2 "a"}}]} 1] ["B" {:Ns/refs1 [{:Ref1/int1 13, :Ref1/ref2 {:Ref2/int2 "__none__", :Ref2/str2 "b"}}]} 1] ["C" {:Ns/refs1 [{:Ref1/int1 "__none__", :Ref1/ref2 {:Ref2/int2 14, :Ref2/str2 "c"}}]} 1] ["D" {:Ns/refs1 [{:Ref1/int1 "__none__", :Ref1/ref2 {:Ref2/int2 "__none__", :Ref2/str2 "d"}}]} 1]]
//{:Ns/refs1 [{:Ref1/int1 11, :Ref1/ref2 {:Ref2/int2 12, :Ref2/str2 "a"}}]}
//class clojure.lang.PersistentArrayMap
//[{:Ref1/int1 11, :Ref1/ref2 {:Ref2/int2 12, :Ref2/str2 "a"}}]
//class clojure.lang.PersistentVector
//{:Ref1/int1 11, :Ref1/ref2 {:Ref2/int2 12, :Ref2/str2 "a"}}
//{:Ref1/int1 11, :Ref1/ref2 {:Ref2/int2 12, :Ref2/str2 "a"}}
//class clojure.lang.PersistentArrayMap
//11
//
//BuilderObj("", "Ns", 2, List(
//  BuilderProp("Ns_str", "str", "String", <cast>, <json>, None),
//  BuilderObj("Ns__Refs1", "Refs1", 2, List(
//    BuilderProp("Ref1_int1_", "int1$", "Option[Int]", <cast>, <json>, None),
//    BuilderObj("Ref1__Ref2", "Ref2", 1, List(
//      BuilderProp("Ref2_int2_", "int2$", "Option[Int]", <cast>, <json>, None),
//      BuilderProp("Ref2_str2", "str2", "String", <cast>, <json>, None))))),
//  BuilderObj("Tx_", "Tx", 1, List(
//    BuilderObj("Ref3_", "Ref3", 1, List(
//      BuilderProp("Ref3_int3", "int3", "Int", <cast>, <json>, None)))))))
//------------------------------------------------
//Indexes("Ns", 2, List(
//  AttrIndex("str", 0, 0, 1, false),
//  Indexes("Refs1", 2, List(
//    AttrIndex("int1$", 14, 11, 1, false),
//    Indexes("Ref2", 1, List(
//      AttrIndex("int2$", 14, 11, 0, false),
//      AttrIndex("str2", 0, 0, 0, false))))),
//  Indexes("Tx", 1, List(
//    Indexes("Ref3", 1, List(
//      AttrIndex("int3", 1, 1, 0, true)))))))
//=========================================
//10
//10
//10
//-----------
//A
//◄
//11
//12
//a
//◄
//110
//120
//aa
//◄
//◄
//1
//-----------
//B
//◄
//13
//◄
//b
//◄
//◄
//1
//-----------
//C
//◄
//◄
//14
//c
//◄
//◄
//1
//-----------
//D
//◄
//◄
//◄
//d
//◄
//◄
//1
//-----------
//E
//◄
//◄◄
//1
//-----------
//
//         */
//
//        //        _ <- Ns.str.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.int2.str2$)) insert List(
//        //          ("A", List(
//        //            (1, Some("a1"), List(
//        //              (11, Some("a11")),
//        //              (12, None)
//        //            )),
//        //            (2, None, List(
//        //              (21, Some("a21")),
//        //              (22, None)
//        //            )),
//        //            (3, Some("a3"), List()),
//        //            (4, None, List())
//        //          )),
//        //          ("B", List())
//        //        )
//        //
//        //        _ <- Ns.str.Refs1.*?(Ref1.int1.str1$.Refs2.*?(Ref2.int2.str2$)).get.map(_.sortBy(_._1) ==> List(
//        //          ("A", List(
//        //            (1, Some("a1"), List(
//        //              (11, Some("a11")),
//        //              (12, None)
//        //            )),
//        //            (2, None, List(
//        //              (21, Some("a21")),
//        //              (22, None)
//        //            )),
//        //            (3, Some("a3"), List()),
//        //            (4, None, List())
//        //          )),
//        //          ("B", List())
//        //        ))
//        //
//        //                rows <- conn.qRaw(
//        //                  """[:find  ?b
//        //                    |        (pull ?a__1 [
//        //                    |          {(:Ns/refs1 :limit nil) [
//        //                    |            (:Ref1/int1 :limit nil)
//        //                    |            (:Ref1/str1 :limit nil :default "__none__")
//        //                    |            {(:Ref1/refs2 :limit nil :default "__none__") [
//        //                    |              (:Ref2/int2 :limit nil)
//        //                    |              (:Ref2/str2 :limit nil :default "__none__")]}]}])
//        //                    | :where [(identity ?a) ?a__1] [?a :Ns/str ?b]]""".stripMargin
//        //                )
//        //                _ = {
//        //                  rows.forEach(row => println(row))
//        //
//        //                  val indexes = Indexes("Ns", 2, List(
//        //                    AttrIndex("str", 0, 0, 0, false),
//        //                    Indexes("Refs1", 2, List(
//        //                      AttrIndex("int1", 1, 1, 1, false),
//        //                      AttrIndex("str1$", 13, 10, 1, false),
//        //                      Indexes("Refs2", 2, List(
//        //                        AttrIndex("int2", 1, 1, 0, false),
//        //                        AttrIndex("str2$", 13, 10, 0, false)))))))
//        //
//        //                  val str = NestedOptRows2packed(rows, 10, 10, 10L, Nil, Nil, indexes).getPacked
//        //
//        //                  println("=========================================" + str)
//        //                }
//
//      } yield ()
//    }
//
//
//    //    "adhoc" - products { implicit conn =>
//    //      import moleculeTests.tests.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
//    //
//    //      for {
//    //
//    //      } yield ()
//    //    }
//
//
//    //    "mbrainz" - mbrainz { implicit conn =>
//    //      import moleculeTests.tests.examples.datomic.mbrainz.dsl.MBrainz._
//    //      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
//    //
//    //      for {
//    //        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
//    //        mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
//    //        darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
//    //        dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
//    //        concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
//    //        dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
//    //        ghostRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
//    //        gb <- Country.e.name_("United Kingdom").get
//    //        georgeHarrison <- Artist.e.name_("George Harrison").get
//    //        bobDylan <- Artist.e.name_("Bob Dylan").get
//    //
//    //
//    //      } yield ()
//    //    }
//
//
//    //    "adhoc" - bidirectional { implicit conn =>
//    //    for {
//    //      _ <- Future(1 ==> 1) // dummy to start monad chain if needed
//    //
//    //    } yield ()
//    //  }
//  }
//}
