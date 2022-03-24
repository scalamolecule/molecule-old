package moleculeTests

import molecule.core.util.Executor._
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.api.out18._
//import molecule.datomic.base.marshalling.Nested2packed
import moleculeTests.setup.AsyncTestSuite
import utest._
//import molecule.core.marshalling.ast.nodes._
import java.util.{Comparator, Date, UUID, Collection => jCollection, List => jList, Map => jMap}

object Adhoc extends AsyncTestSuite with Helpers with JavaUtil {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      for {
        conn <- futConn

        //
        //        _ = {
        //          val obj = Obj("", "Ns", false, List(
        //            Prop("Ns_str", "str", "String", 1, "One", None),
        //            Obj("Ns__Refs1", "Refs1", true, List(
        //              Prop("Ref1_int1", "int1", "Int", 1, "One", None))),
        //            Obj("Tx_", "Tx", false, List(
        //              Obj("Ref3_", "Ref3", false, List(
        //                Prop("Ref3_str3", "str3", "String", 1, "One", None)))))))
        //
        //          val obj2 = Obj("", "Ns", false, List(
        //            Prop("Ns_str", "str", "String", 1, "One", None),
        //            Obj("Ns__Refs1", "Refs1", true, List(
        //              Prop("Ref1_int1", "int1", "Int", 1, "One", None))),
        //            Prop("Ref3_str3", "str3", "String", 1, "One", None),
        //          ))
        //
        //          val obj3 = Obj("", "Ns", false, List(
        //            Prop("Ns_str", "str", "String", 1, "One", None),
        //            Obj("Ns__Refs1", "Refs1", true, List(
        //              Prop("Ref1_int1", "int1", "Int", 1, "One", None))),
        //            Obj("Ref3_", "Ref3", false, List(
        //              Prop("Ref3_str3", "str3", "String", 1, "One", None))),
        //          ))
        //
        //          val sortedRows: jCollection[jList[AnyRef]] = javaList(
        //            javaList2(17592186045447L, 17592186045448L, "a", 1, "X"),
        //            javaList2(17592186045447L, 17592186045449L, "a", 2, "X"),
        //            javaList2(17592186045450L, 17592186045451L, "b", 1, "X"),
        //            javaList2(17592186045450L, 17592186045452L, "b", 2, "X"),
        //            javaList2(17592186045455L, 17592186045456L, "a", 1, "Y"),
        //            javaList2(17592186045455L, 17592186045457L, "a", 2, "Y"),
        //            javaList2(17592186045458L, 17592186045459L, "b", 1, "Y"),
        //            javaList2(17592186045458L, 17592186045460L, "b", 2, "Y")
        //          ).asInstanceOf[jCollection[jList[AnyRef]]]
        //
        //          val packed = Nested2packed(obj, sortedRows, 1).getPacked
        //
        //          packed ==> 7
        //        }


        //        _ <- Ns.str.a2.Refs1.*(Ref1.int1.a1).Tx(Ref3.str3.a1).get.map(_ ==> List(
        //          ("a", List(1, 2), "X"),
        //          ("b", List(1, 2), "X"),
        //          ("a", List(1, 2), "Y"),
        //          ("b", List(1, 2), "Y"),
        //        ))


        //        _ = {
        //          val obj = Obj("", "Ns", false, List(
        //            Prop("Ns_str", "str", "String", 1, "One", None),
        //            Obj("Ns__Refs1", "Refs1", true, List(
        //              Prop("Ref1_int1", "int1", "Int", 1, "One", None),
        //              Obj("Ref1__Ref2", "Ref2", false, List(
        //                Prop("Ref2_enum2", "enum2", "enum", 1, "One", None),
        //                Prop("Ref2_str2", "str2", "String", 1, "One", None),
        //                Obj("Ref2__Refs3", "Refs3", true, List(
        //                  Prop("Ref3_int3", "int3", "Int", 1, "One", None))))))),
        //            Obj("Tx_", "Tx", false, List(
        //              Obj("Ref2_", "Ref2", false, List(
        //                Prop("Ref2_str2", "str2", "String", 1, "One", None),
        //                Prop("Ref2_int2", "int2", "Int", 1, "One", None),
        //                Obj("Ref2__Ref3", "Ref3", false, List(
        //                  Prop("Ref3_str3", "str3", "String", 1, "One", None))))),
        //              Obj("Ns_", "Ns", false, List(
        //                Prop("Ns_int", "int", "Int", 1, "One", None),
        //                Prop("Ns_bool", "bool", "Boolean", 1, "One", None)))))))
        //
        //          val sortedRows: jCollection[jList[AnyRef]] = javaList(
        //            javaList2(17592186045447L, 17592186045448L, 17592186045450L, "A", 1, "enum20", "a", 10, "x1", 111, "x2", 222, true),
        //            javaList2(17592186045447L, 17592186045448L, 17592186045451L, "A", 1, "enum20", "a", 11, "x1", 111, "x2", 222, true),
        //            javaList2(17592186045447L, 17592186045452L, 17592186045454L, "A", 2, "enum21", "b", 20, "x1", 111, "x2", 222, true),
        //            javaList2(17592186045447L, 17592186045452L, 17592186045455L, "A", 2, "enum21", "b", 21, "x1", 111, "x2", 222, true),
        //            javaList2(17592186045461L, 17592186045462L, 17592186045464L, "C", 4, "enum20", "d", 40, "y1", 333, "y2", 444, false),
        //            javaList2(17592186045461L, 17592186045462L, 17592186045465L, "C", 4, "enum20", "d", 41, "y1", 333, "y2", 444, false),
        //          ).asInstanceOf[jCollection[jList[AnyRef]]]
        //
        //          val packed = Nested2packed(obj, sortedRows, 1).getPacked
        //
        //          packed ==> 7
        //        }


        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2_("x1").int2_(111).Ref3.str3_("x2") + Ns.int_(222).bool_(true))) insert List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil))),
          ("B", Nil)
        )
        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2_("y1").int2_(333).Ref3.str3_("y2") + Ns.int_(444).bool_(false))) insert List(
          ("C", List(
            (4, "enum20", "d", List(40, 41)))),
        )

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))) insert List(
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41)))),
        //        )

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2_("y1").int2_(333).Ref3.str3_("y2") + Ns.int_(444).bool_(false))) insert List(
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41)))),
        //        )

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2_("x1").int2_(111).Ref3.str3_("x2") + Ns.int_(222).bool_(true))) insert List(
        //          ("A", List(
        //            (1, "enum20", "a", List(10, 11)),
        //            (2, "enum21", "b", List(20, 21)),
        //          )),
        //        )

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2_("x1").int2_(111).Ref3.str3_("x2") + Ns.int_(222).bool_(true))) insert List(
        //          ("A", List(
        //            (1, 6, "a", List(10, 11)),
        //            (2, 7, "b", List(20, 21)),
        //          )),
        //        )

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.Refs3.*(Ref3.int3))) insert List(
        ////        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.str2.Refs3.*(Ref3.int3))) insert List(
        //          ("A", List(
        //            (1, "enum20",  List(10, 11)),
        //            (2, "enum21",  List(20, 21)),
        //          )),
        //        )

        ////        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.Refs3.*(Ref3.int3))) insert List(
        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2)) insert List(
        //          ("A", List(
        //            (1, "enum20"),
        //            (2, "enum21"),
        //          )),
        //        )
        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2_("y1").int2_(333).Ref3.str3_("y2") + Ns.int_(444).bool_(false))) insert List(
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41)))),
        //        )

        // Ref2.str2 ...............................

        // asc
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.str2.a1.Refs3.*?(Ref3.int3)).get.map(_ ==> List(
        //          ("C", List(
        //            (4, "d", List(40, 41)))),
        //        ))
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.a1.Refs3.*?(Ref3.int3)).get.map(_ ==> List(
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41)))),
        //        ))

        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.a1.Refs3.*?(Ref3.int3)).get.map(_ ==> List(
        ////        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.str2.a1.Refs3.*?(Ref3.int3)).get.map(_ ==> List(
        //          ("A", List(
        //            (1, "enum20",  List(10, 11)),
        //            (2, "enum21",  List(20, 21)),
        //          ))))
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.a1.Refs3.*?(Ref3.int3)).get.map(_ ==> List(
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.a1).get.map(_ ==> List(
        //          ("A", List(
        //            (1, "enum20"),
        //            (2, "enum21"),
        //          ))))
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.a1)
        //          .get.map(_ ==> List(
        //          ("A", List(
        //            (1, "enum20", "a", List(10, 11)),
        //            (2, "enum21", "b", List(20, 21)),
        //          ))
        //        ))

        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.a1)
        //          .get.map(_ ==> List(
        //          ("A", List(
        //            (1, 6, "a"),
        //            (2, 7, "b"),
        //          ))
        //        ))

        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2_.str2.a1.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "a", List(10, 11)),
            (2, "b", List(20, 21)),
            (3, "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

//        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.a1.Refs3.*?(Ref3.int3))
//          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
//          ("A", List(
//            (1, "enum20", "a", List(10, 11)),
//            (2, "enum21", "b", List(20, 21)),
//            (3, "enum22", "c", Nil)),
//            ("x1", 111, "x2"), (222, true)),
//          ("B", Nil,
//            ("x1", 111, "x2"), (222, true)),
//          ("C", List(
//            (4, "enum20", "d", List(40, 41))),
//            ("y1", 333, "y2"), (444, false)),
//        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.a1.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //          ("A", List(
        //            (1, "enum20", "a", List(10, 11)),
        //            (2, "enum21", "b", List(20, 21))),
        //            ("x1", 111, "x2"), (222, true)),
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41))),
        //            ("y1", 333, "y2"), (444, false)),
        //        ))
        //
        //        // desc
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.d1.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //          ("A", List(
        //            (3, "enum22", "c", Nil),
        //            (2, "enum21", "b", List(20, 21)),
        //            (1, "enum20", "a", List(10, 11))),
        //            ("x1", 111, "x2"), (222, true)),
        //          ("B", Nil,
        //            ("x1", 111, "x2"), (222, true)),
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41))),
        //            ("y1", 333, "y2"), (444, false)),
        //        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.d1.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //          ("A", List(
        //            (2, "enum21", "b", List(20, 21)),
        //            (1, "enum20", "a", List(10, 11))),
        //            ("x1", 111, "x2"), (222, true)),
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41))),
        //            ("y1", 333, "y2"), (444, false)),
        //        ))
        //
        //        // + previous
        //        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41))),
        //            ("y1", 333, "y2"), (444, false)),
        //          ("B", Nil,
        //            ("x1", 111, "x2"), (222, true)),
        //          ("A", List(
        //            (3, "enum22", "c", Nil),
        //            (2, "enum21", "b", List(20, 21)),
        //            (1, "enum20", "a", List(10, 11))),
        //            ("x1", 111, "x2"), (222, true)),
        //        ))
        //        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //          ("C", List(
        //            (4, "enum20", "d", List(40, 41))),
        //            ("y1", 333, "y2"), (444, false)),
        //          ("A", List(
        //            (2, "enum21", "b", List(20, 21)),
        //            (1, "enum20", "a", List(10, 11))),
        //            ("x1", 111, "x2"), (222, true)),
        //        ))

      } yield ()
    }
  }
}
