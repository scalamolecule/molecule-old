package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.lang.{Long => jLong}
import java.util.{Collections, Date, UUID, ArrayList => jArrayList, Comparator => jComparator, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.marshalling.unpack.UnpackTypes
import molecule.core.marshalling.attrIndexes._
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import moleculeTests.tests.core.base.schema.CoreTestSchema
import scala.util.control.NonFatal


object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn



        //        _ <- m(Ref1.str1.Nss * Ns.int) insert List(
        //          ("A", List(1, 2)),
        //          ("B", List(3, 4))
        //        )
        //
        //        //        _ <- m(Ref1.str1.Nss * Ns.int).inspectGet
        //        //
        //        //        _ <- m(Ref1.str1.Nss * Ns.int).get.map(_ ==> List(
        //        //          ("A", List(1, 2)),
        //        //          ("B", List(3, 4))
        //        //        ))
        //
        //        data <- conn.qRaw(
        //          """[:find  ?sort0 ?sort1 ?b ?d
        //            | :where [?a :Ref1/str1 ?b]
        //            |        [?a :Ref1/nss ?c]
        //            |        [?c :Ns/int ?d]
        //            |        [(identity ?a) ?sort0]
        //            |        [(identity ?c) ?sort1]]""".stripMargin
        //        )
        //
        //        _ = {
        //          data.forEach(row => println(row))
        //
        //          object testing extends Nested2pack {
        //            val rows = new java.util.ArrayList(data)
        //            rows.sort(this)
        //
        //            val last = data.size
        //            // todo sort rows
        //            val it   = rows.iterator
        //
        //            row = it.next
        //            do {
        //              topRow = row
        //              e0 = row.get(0)
        //
        //              // level 0
        //              packOneString(row, 2)
        //
        //              // level 1
        //              do {
        //                packOne(row, 3)
        //                if (it.hasNext)
        //                  row = it.next
        //                i += 1
        //              } while (i < last && row.get(0) == e0)
        //              // level 0 post
        //            } while (i < last)
        //          }
        //          testing
        //
        //          println(sb)
        //        }


        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(1)) insert List(
        //          ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa"))),
        //          ("B", List((Some(13), None, "b"))),
        //          ("C", List((None, Some(14), "c"))),
        //          ("D", List((None, None, "d"))),
        //          ("E", List())
        //        )
        //
        //        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).inspectGet
        //        //        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
        //        //          ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa")), 1),
        //        //          ("B", List((Some(13), None, "b")), 1),
        //        //          ("C", List((None, Some(14), "c")), 1),
        //        //          ("D", List((None, None, "d")), 1),
        //        //          ("E", List(), 1)
        //        //        ))
        //
        //        data <- conn.qRaw(
        //          """[:find  ?sort0 ?sort1 ?b
        //            |        (pull ?c__1 [(limit :Ref1/int1 nil)])
        //            |        (pull ?e__2 [(limit :Ref2/int2 nil)])
        //            |        ?g ?i
        //            | :where [?a :Ns/str ?b]
        //            |        [?a :Ns/refs1 ?c]
        //            |        [(identity ?c) ?c__1]
        //            |        [?c :Ref1/ref2 ?e]
        //            |        [(identity ?e) ?e__2]
        //            |        [?e :Ref2/str2 ?g ?tx]
        //            |        [?tx :Ref3/int3 ?i]
        //            |        [(identity ?a) ?sort0]
        //            |        [(identity ?c) ?sort1]]""".stripMargin
        //        )
        //
        //        _ = {
        //          data.forEach(row => println(row))
        //
        //          if (!data.isEmpty) {
        //            val rows = new java.util.ArrayList(data)
        //            val last = data.size
        //            // todo sort rows
        //            val it   = rows.iterator
        //
        //            row = it.next
        //            do {
        //              topRow = row
        //              e0 = row.get(0)
        //
        //              // level 0
        //              packOneString(row, 2)
        //
        //              // level 1
        //              do {
        //                packOptOne(row, 3)
        //                packOptOne(row, 4)
        //                packOneString(row, 5)
        //                if (it.hasNext)
        //                  row = it.next
        //                i += 1
        //              } while (i < last && row.get(0) == e0)
        //
        //              // level 0 post
        //              packOne(topRow, 6)
        //
        //            } while (i < last)
        //          }
        //          println(sb)
        //        }


        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(7777).Ref4.int4_(8888)) insert List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa"))),
        //          //          ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa"))),
        //          ("B", List((Some(13), None, "b"))),
        //          //          ("C", List((None, Some(14), "c"))),
        //          //          ("D", List((None, None, "d"))),
        //          //          ("E", List())
        //        )
        //
        //        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3.Ref4.int4).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, 8888),
        //          //                    ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa")), 7777, 8888),
        //          ("B", List((Some(13), None, "b")), 7777, 8888),
        //          //          ("C", List((None, Some(14), "c")), 7777, 8888),
        //          //          ("D", List((None, None, "d")), 7777, 8888),
        //          //          ("E", List(), 7777, 8888)
        //        ))
        //
        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3.Ref4.int4).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, 8888),
        //          //          ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa")), 7777, 8888),
        //          ("B", List((Some(13), None, "b")), 7777, 8888),
        //          //          ("C", List((None, Some(14), "c")), 7777, 8888),
        //          //          ("D", List((None, None, "d")), 7777, 8888),
        //          //          ("E", List(), 7777, 8888)
        //        ))


        //        _ = m(Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(7777).Ref4.int4_(8888)))
        //        _ = m(Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4_(7777) + Ref3.int3_(8888)))

        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4_(7777) + Ref3.int3_(8888)) insert List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa"))),
        //          ("B", List((Some(13), None, "b"))),
        //          ("C", List((None, Some(14), "c"))),
        //          ("D", List((None, None, "d"))),
        //          ("E", List())
        //        )
        //
        //        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4 + Ref3.int3).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, 8888),
        //          ("B", List((Some(13), None, "b")), 7777, 8888),
        //          ("C", List((None, Some(14), "c")), 7777, 8888),
        //          ("D", List((None, None, "d")), 7777, 8888),
        //          ("E", List(), 7777, 8888)
        //        ))
        //
        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4 + Ref3.int3).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, 8888),
        //          ("B", List((Some(13), None, "b")), 7777, 8888),
        //          ("C", List((None, Some(14), "c")), 7777, 8888),
        //          ("D", List((None, None, "d")), 7777, 8888),
        //        ))

        //                _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4_(7777) + Ref3.int3_(8888).str3_("meta")) insert List(
        //                  ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa"))),
        //                  ("B", List((Some(13), None, "b"))),
        //                  ("C", List((None, Some(14), "c"))),
        //                  ("D", List((None, None, "d"))),
        //                  ("E", List())
        //                )
        //
        //                _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4 + Ref3.int3.str3).get.map(_.sortBy(_._1) ==> List(
        //                  ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, (8888, "meta")),
        //                  ("B", List((Some(13), None, "b")), 7777, (8888, "meta")),
        //                  ("C", List((None, Some(14), "c")), 7777, (8888, "meta")),
        //                  ("D", List((None, None, "d")), 7777, (8888, "meta")),
        //                  ("E", List(), 7777, (8888, "meta"))
        //                ))
        //
        //                _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4 + Ref3.int3.str3).get.map(_.sortBy(_._1) ==> List(
        //                  ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, (8888, "meta")),
        //                  ("B", List((Some(13), None, "b")), 7777, (8888, "meta")),
        //                  ("C", List((None, Some(14), "c")), 7777, (8888, "meta")),
        //                  ("D", List((None, None, "d")), 7777, (8888, "meta")),
        //                ))

//        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
//          .Tx(Ref2.str2_("b").int2_(5).Ref3.str3_("c") + Ns.int_(6).bool_(true)) insert List(
//          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil))),
//          ("B", Nil)
//        )
//
//        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
//          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool)).inspectGet
//
//        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
//          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool)).get.map(_ ==> List(
//          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), ("b", 5, "c"), (6, true)),
//          ("B", Nil, ("b", 5, "c"), (6, true))
//        ))


//        _ <- Ns.str.Refs1.*(Ref1.int1).Tx(Ref2.str2_("b")) insert List(
//          ("A", List(1, 2)),
//          ("B", Nil)
//        )
//
////        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
////          .Tx(Ref2.str2)).inspectGet
//
//        _ <- m(Ns.str.Refs1.*?(Ref1.int1)).get.map(_ ==> List(
//          ("A", List(1, 2)),
//          ("B", Nil)
//        ))
//
//        _ <- m(Ns.str.Refs1.*?(Ref1.int1).Tx(Ref2.str2)).get.map(_ ==> List(
//          ("A", List(1, 2), "b"),
//          ("B", Nil, "b")
//        ))





//        _ <- Ns.str.Refs1.*(Ref1.int1.str1.Refs2.*(Ref2.int2)) insert List(
//          ("A", List((1, "a", List(3, 4)), (11, "aa", Nil))),
//          ("B", Nil)
//        )
//        _ <- Ns.str.Refs1.*?(Ref1.int1.str1.Refs2.*?(Ref2.int2)).inspectGet
/*
Obj("", "Ns", false, List(
  Prop("Ns_str", "str", "String", <cast>, <json>, None),
  Obj("Ns__Refs1", "Refs1", true, List(
    Prop("Ref1_int1", "int1", "Int", <cast>, <json>, None),
    Prop("Ref1_str1", "str1", "String", <cast>, <json>, None),
    Obj("Ref1__Refs2", "Refs2", true, List(
      Prop("Ref2_int2", "int2", "Int", <cast>, <json>, None)))))))

1: [A  {
  :Ns/refs1 [
    {
      :Ref1/int1 1,
      :Ref1/str1 "a",
      :Ref1/refs2 [{:Ref2/int2 3} {:Ref2/int2 4}]
    } {
      :Ref1/int1 11,
      :Ref1/str1 "aa",
      :Ref1/refs2 "__none__"}]}]
2: [B  null]

{
  val it = row.iterator();
  scala.Tuple2(castNestedOptOne[String](it), it.next match {
    case null => Nil
    case (last @ _) => {
      val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]];
      val it = extractFlatValues(list, 2, scala.collection.immutable.List(), scala.collection.immutable.List(), true);
      val buf = new scala.collection.mutable.ListBuffer[Any]();
      while$1(){
        if (it.hasNext)
          {
            buf.addOne(scala.Tuple3(castNestedOptOneInt(it), castNestedOptOne[String](it), it.next match {
              case "__none__" => Nil
              case (last @ _) => {
                val list = last.asInstanceOf[jList[Any]];
                val it = extractFlatValues(list, 1, scala.collection.immutable.List(), scala.collection.immutable.List(), false);
                val buf = new scala.collection.mutable.ListBuffer[Any]();
                while$2(){
                  if (it.hasNext)
                    {
                      buf.addOne(castNestedOptOneInt(it));
                      while$2()
                    }
                  else
                    ()
                };
                buf.toList
              }
            }));
            while$1()
          }
        else
          ()
      };
      buf.toList
    }
  })
}
 */

        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.str2.Refs3.*(Ref3.int3)) insert List(
          ("A", List((1, "a", List(3, 4)), (11, "aa", Nil))),
          ("B", Nil)
        )
//        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.str2.Refs3.*?(Ref3.int3)).inspectGet
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.str2.Refs3.*?(Ref3.int3)).get.map(_ ==> List(
          ("A", List((1, "a", List(3, 4)), (11, "aa", Nil))),
          ("B", Nil)
        ))

/*
Obj("", "Ns", false, List(
  Prop("Ns_str", "str", "String", <cast>, <json>, None),
  Obj("Ns__Refs1", "Refs1", true, List(
    Prop("Ref1_int1", "int1", "Int", <cast>, <json>, None),
    Obj("Ref1__Ref2", "Ref2", false, List(
      Prop("Ref2_str2", "str2", "String", <cast>, <json>, None),
      Obj("Ref2__Refs3", "Refs3", true, List(
        Prop("Ref3_int3", "int3", "Int", <cast>, <json>, None)))))))))

1: [A  {
  :Ns/refs1 [
    {
      :Ref1/int1 1,
      :Ref1/ref2 {
        :Ref2/str2 "a",
        :Ref2/refs3 [{:Ref3/int3 3} {:Ref3/int3 4}]
      }
    } {
      :Ref1/int1 11,
      :Ref1/ref2 {
        :Ref2/str2 "aa",
        :Ref2/refs3 "__none__"}}]}]
2: [B  null]

{
  val it = row.iterator();
  scala.Tuple2(castNestedOptOne[String](it), it.next match {
    case null => Nil
    case (last @ _) => {
      val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]];
      val it = extractFlatValues(list, 2, scala.collection.immutable.List(1), scala.collection.immutable.List(), false);
      val buf = new scala.collection.mutable.ListBuffer[Any]();
      while$1(){
        if (it.hasNext)
          {
            buf.addOne(scala.Tuple3(castNestedOptOneInt(it), castNestedOptOne[String](it), it.next match {
              case null => Nil
              case (last @ _) => {
                val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]];
                val it = extractFlatValues(list, 1, scala.collection.immutable.List(), scala.collection.immutable.List(), false);
                val buf = new scala.collection.mutable.ListBuffer[Any]();
                while$2(){
                  if (it.hasNext)
                    {
                      buf.addOne(castNestedOptOneInt(it));
                      while$2()
                    }
                  else
                    ()
                };
                buf.toList
              }
            }));
            while$1()
          }
        else
          ()
      };
      buf.toList
    }
  })
}
 */



//        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3)).Tx(Ref2.str2_("b")) insert List(
//          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil))),
//          ("B", Nil)
//        )
////
////        _ <- m(Ns.str.Refs1.*?(Ref1.int1)).get.map(_ ==> List(
////          ("A", List(1, 11)),
////          ("B", Nil)
////        ))
////
////        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2)).get.map(_ ==> List(
////          ("A", List((1, 2, "a"), (11, 22, "aa"))),
////          ("B", Nil)
////        ))
//
//        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))).inspectGet
//        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))).get.map(_ ==> List(
//          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil))),
//          ("B", Nil)
//        ))
//
//        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3)).Tx(Ref2.str2)).get.map(_ ==> List(
//          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), "b"),
//          ("B", Nil, "b")
//        ))



//        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3)).int) insert List(
//          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), 7),
//          ("B", Nil, 8)
//        )
//
////        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
////          .Tx(Ref2.str2)).inspectGet
//
//        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3)).int).get.map(_ ==> List(
//          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), 7),
//          ("B", Nil, 8)
//        ))

        //                _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //                  .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //                  ("A", List((1, 2, "a", List(3, 4))), ("b", 5, "c"), (6, true))
        //                ))


        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2_("b").int2_(5).Ref3.str3_("c") + Ns.int_(6).bool_(true))) insert List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil))),
        //          ("B", Nil)
        //        )
        //
        //        _ <- m(Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool)).getJson.map(_ ==>
        //          """{
        //            |  "data": {
        //            |    "Ns": [
        //            |      {
        //            |        "str": "A",
        //            |        "Refs1": [
        //            |          {
        //            |            "int1": 1,
        //            |            "Ref2": {
        //            |              "int2": 2,
        //            |              "str2": "a"
        //            |            },
        //            |            "Refs3": [
        //            |              {
        //            |                "int3": 3
        //            |              },
        //            |              {
        //            |                "int3": 4
        //            |              }
        //            |            ]
        //            |          }
        //            |        ],
        //            |        "Tx": {
        //            |          "Ref2": {
        //            |            "str2": "b",
        //            |            "int2": 5,
        //            |            "Ref3": {
        //            |              "str3": "c"
        //            |            }
        //            |          },
        //            |          "Ns": {
        //            |            "int": 6,
        //            |            "bool": true
        //            |          }
        //            |        }
        //            |      },
        //            |      {
        //            |        "str": "B",
        //            |        "Refs1": [
        //            |          {
        //            |            "int1": 1,
        //            |            "Ref2": {
        //            |              "int2": 2,
        //            |              "str2": "a"
        //            |            },
        //            |            "Refs3": [
        //            |              {
        //            |                "int3": 3
        //            |              },
        //            |              {
        //            |                "int3": 4
        //            |              }
        //            |            ]
        //            |          }
        //            |        ],
        //            |        "Tx": {
        //            |          "Ref2": {
        //            |            "str2": "b",
        //            |            "int2": 5,
        //            |            "Ref3": {
        //            |              "str3": "c"
        //            |            }
        //            |          },
        //            |          "Ns": {
        //            |            "int": 6,
        //            |            "bool": true
        //            |          }
        //            |        }
        //            |      }
        //            |    ]
        //            |  }
        //            |}""".stripMargin
        //        )
        //
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).getJson.map(_ ==>
        //          """{
        //            |  "data": {
        //            |    "Ns": [
        //            |      {
        //            |        "str": "A",
        //            |        "Refs1": [
        //            |          {
        //            |            "int1": 1,
        //            |            "Ref2": {
        //            |              "int2": 2,
        //            |              "str2": "a"
        //            |            },
        //            |            "Refs3": [
        //            |              {
        //            |                "int3": 3
        //            |              },
        //            |              {
        //            |                "int3": 4
        //            |              }
        //            |            ]
        //            |          }
        //            |        ],
        //            |        "Tx": {
        //            |          "Ref2": {
        //            |            "str2": "b",
        //            |            "int2": 5,
        //            |            "Ref3": {
        //            |              "str3": "c"
        //            |            }
        //            |          },
        //            |          "Ns": {
        //            |            "int": 6,
        //            |            "bool": true
        //            |          }
        //            |        }
        //            |      }
        //            |    ]
        //            |  }
        //            |}""".stripMargin
        //        )


        //                _ = m(Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4_(7777) + Ref3.int3_(8888).str3_("meta")))

        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4_(7777) + Ref3.int3_(8888).str3_("meta")) insert List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa"))),
        //          ("B", List((Some(13), None, "b"))),
        //          ("C", List((None, Some(14), "c"))),
        //          ("D", List((None, None, "d"))),
        //          ("E", List())
        //        )

        //        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4 + Ref3.int3.str3).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, (8888, "meta")),
        //          ("B", List((Some(13), None, "b")), 7777, (8888, "meta")),
        //          ("C", List((None, Some(14), "c")), 7777, (8888, "meta")),
        //          ("D", List((None, None, "d")), 7777, (8888, "meta")),
        //          ("E", List(), 7777, (8888, "meta"))
        //        ))
        //
        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4 + Ref3.int3.str3).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, (8888, "meta")),
        //          ("B", List((Some(13), None, "b")), 7777, (8888, "meta")),
        //          ("C", List((None, Some(14), "c")), 7777, (8888, "meta")),
        //          ("D", List((None, None, "d")), 7777, (8888, "meta")),
        //        ))

        //        _ <- Ns.str.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.int2.str2$)).Tx(Ref3.int3_(7777)) insert List(
        //          ("A", List(
        //            (1, Some("a1"), List(
        //              (11, Some("a11")),
        //              (12, None)
        //            )),
        //            (2, None, List(
        //              (21, Some("a21")),
        //              (22, None)
        //            )),
        //            (3, Some("a3"), List()),
        //            (4, None, List())
        //          )),
        //          ("B", List(
        //            (5, Some("a5"), List(
        //              (51, Some("a51")),
        //              (52, None)
        //            ))
        //          )),
        //          ("C", Nil)
        //        )
        //
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.str1$.Refs2.*?(Ref2.int2.str2$)).Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List(
        //            (1, Some("a1"), List(
        //              (11, Some("a11")),
        //              (12, None)
        //            )),
        //            (2, None, List(
        //              (21, Some("a21")),
        //              (22, None)
        //            )),
        //            (3, Some("a3"), List()),
        //            (4, None, List())
        //          ), 7777),
        //          ("B", List(
        //            (5, Some("a5"), List(
        //              (51, Some("a51")),
        //              (52, None)
        //            ))
        //          ), 7777),
        //          ("C", Nil, 7777)
        //        ))
        //
        //        _ <- Ns.str.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.int2.str2$)).Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List(
        //            (1, Some("a1"), List(
        //              (11, Some("a11")),
        //              (12, None)
        //            )),
        //            (2, None, List(
        //              (21, Some("a21")),
        //              (22, None)
        //            ))
        //          ), 7777),
        //          ("B", List(
        //            (5, Some("a5"), List(
        //              (51, Some("a51")),
        //              (52, None)
        //            ))
        //          ), 7777)
        //        ))


        //        _ <- m(Ns.str.Ref1.int1 + Ref2.int2.str2) insert List(
        //          (("A", 1), (11, "a")),
        //          (("B", 2), (22, "b"))
        //        )
        //        _ <- m(Ns.str.Ref1.int1 + Ref2.int2.str2).get.map(_.sortBy(_._1) ==> List(
        //          (("A", 1), (11, "a")),
        //          (("B", 2), (22, "b"))
        //        ))

        //        _ <- m(Ref2.int2.str2 + Ns.str.int) insert List(
        //          ((1, "a"), ("aa", 11)),
        //          ((2, "b"), ("bb", 22))
        //        )
        //
        //        _ <- m(Ref2.int2.str2 + Ns.str.int).get.map(_.sorted ==> List(
        //          ((1, "a"), ("aa", 11)),
        //          ((2, "b"), ("bb", 22))
        //        ))


      } yield ()
    }


    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.tests.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }


    //    "mbrainz" - mbrainz { implicit conn =>
    //      import moleculeTests.tests.examples.datomic.mbrainz.dsl.MBrainz._
    //      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
    //
    //      for {
    //        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
    //        mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
    //        darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
    //        dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
    //        concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
    //        dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
    //        ghostRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
    //        gb <- Country.e.name_("United Kingdom").get
    //        georgeHarrison <- Artist.e.name_("George Harrison").get
    //        bobDylan <- Artist.e.name_("Bob Dylan").get
    //
    //
    //      } yield ()
    //    }


    //    "adhoc" - bidirectional { implicit conn =>
    //    for {
    //      _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //    } yield ()
    //  }
  }
}
