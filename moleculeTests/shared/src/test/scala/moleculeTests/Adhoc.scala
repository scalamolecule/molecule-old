package moleculeTests

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Adhoc extends AsyncTestSuite {

  lazy val tests = Tests {

    //    "adhoc" - bidirectional { implicit conn =>
    "adhoc" - core { implicit conn =>


      for {
        //        _ <- Future(1 ==> 1)
        _ <- Future(2 ==> 2)

        _ <- m(Ns.str.Refs1 * Ref1.int1.Ref2.int2) insert List(
          ("a", List((1, 10))),
          ("b", List((3, 30)))
        )

        _ <- m(Ns.str.Refs1 * Ref1.int1.Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 1,
            |            "Ref2": {
            |              "int2": 10
            |            }
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1": 3,
            |            "Ref2": {
            |              "int2": 30
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        /*
final override def jsonBranch0(
  sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder
): StringBuilder = branch(0, sb, jsonOneQuoted(sb, "str", row, 2), "Refs1", leaf);

final override def jsonLeaf1(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(1, sb, {
  jsonOne(sb, "int1", row, 3);
  {
    sb.append(",");
    sb.append(indent(2));
    jsonOne(sb, "int2", row, 4)
  }
})


final override def jsonBranch0(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(0, sb, jsonOneQuoted(sb, "str", row, 2), "Refs1", leaf);
final override def jsonLeaf1(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(1, sb, {
  <empty>;
  jsonOne(sb, "int1", row, 3);
  {
    sb.append(",");
    sb.append(indent(2))
  };
  {
    quote(sb, "Ref2");
    sb.append(": {");
    sb.append(indent(1));
    <empty>;
    jsonOne(sb, "int2", row, 4);
    sb.append(indent(1))
  }
})
};

Obj("", "Ns", 2, List(
  Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None)))
---
Obj("Ns__Refs1", "Refs1", 2, List(
  Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
  Obj("Ref1__Ref2", "Ref2", 1, List(
    Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None)))))
------------------------------------------------
List(
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1258/1344378021@6edd2a41),
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1254/1321777823@76edf444,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1254/1321777823@469c5b9))
======================================================
         */



        /*
Obj("", "Ns", 2, List(
  Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None),

  Obj("Ns__Refs1", "Refs1", 2, List(
    Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
    Obj("Ref1__Ref2", "Ref2", 1, List(
      Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None)))))))
------------------------------------------------
List(
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1258/1099748511@5eaf191f),
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1254/1462400302@ddf8890,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1254/1462400302@7b6faa0a))
======================================================

        _ <- m(Ns.str.Refs1 * Ref1.int1.Ref2.int2).getJson.map(_ ==>



Obj("", "Ns", 2, List(
  Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None),
  Obj("Ns__Refs1", "Refs1", 2, List(
    Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
    Obj("Ref1__Ref2", "Ref2", 1, List(
      Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None),
      Obj("Ref2__Refs3", "Refs3", 2, List(
        Prop("Ref3_int3", "int3", "Int", <cast-lambda>, <json-lambda>, None),
        Obj("Ref3__Ref4", "Ref4", 1, List(
          Prop("Ref4_int4", "int4", "Int", <cast-lambda>, <json-lambda>, None)))))))))))
------------------------------------------------
List(
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1257/1409415405@4530538a),
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1253/980466178@d25d8d1,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1253/980466178@35f240a5),
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1253/980466178@56af1f75,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1253/980466178@1144ba29))
======================================================

        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3.Ref4.int4))).get


Obj("", "Ns", 2, List(
  Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None),
  Obj("Ns__Refs1", "Refs1", 2, List(
    Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
    Obj("Ref1__Ref2", "Ref2", 1, List(
      Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None))),
    Obj("Ref1__Refs2", "Refs2", 2, List(
      Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None),
      Obj("Ref2__Ref3", "Ref3", 1, List(
        Prop("Ref3_int3", "int3", "Int", <cast-lambda>, <json-lambda>, None)))))))))
------------------------------------------------
List(
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1253/1791147004@79b947b1),
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1249/719393503@be71ff3,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1249/719393503@4b8cba0d),
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1249/719393503@58a0ca4d,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1249/719393503@6b179a27))
======================================================

        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2._Ref1.Refs2.*(Ref2.int2.Ref3.int3))).get

Obj("", "Ns", 2, List(
  Prop("Ns_str", "str", "String", null, null, None),

  Obj("Ns__Refs1", "Refs1", 2, List(
    Prop("Ref1_int1", "int1", "Int", null, null, None),
    Obj("Ref1__Ref2", "Ref2", 1, List(
      Prop("Ref2_int2", "int2", "Int", null, null, None))),

    Obj("Ref1__Refs2", "Refs2", 2, List(
      Prop("Ref2_int2", "int2", "Int", null, null, None),
      Obj("Ref2__Ref3", "Ref3", 1, List(
        Prop("Ref3_int3", "int3", "Int", null, null, None)))))))))





Obj("", "Ns", 2, List(
  Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None),
  Obj("Ns__Ref1", "Ref1", 1, List(
    Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
    Obj("Ref1__Ref2", "Ref2", 1, List(
      Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None))))),

  Obj("Ns__Refs1", "Refs1", 2, List(
    Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
    Obj("Ref1__Ref2", "Ref2", 1, List(
      Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None),

      Obj("Ref2__Refs3", "Refs3", 2, List(
        Prop("Ref3_int3", "int3", "Int", <cast-lambda>, <json-lambda>, None),
        Obj("Ref3__Ref4", "Ref4", 1, List(
          Prop("Ref4_int4", "int4", "Int", <cast-lambda>, <json-lambda>, None)))))))))))
------------------------------------------------
List(
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1255/1017891572@35d6177d,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1251/1073828387@2579dbdc,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1251/1073828387@1e9708fa),
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1251/1073828387@3a493f39,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1251/1073828387@3fa1aaf2),
  List(
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1251/1073828387@546d21e1,
    molecule.core.macros.lambdaTrees.LambdaJsonTypes$$Lambda$1251/1073828387@729b334e))
======================================================

        _ <- m(Ns.str.Ref1.int1.Ref2.int2._Ref1._Ns.Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3.Ref4.int4))).get
         */
        //        _ <- m(Ns.str.Ref1.int1.Refs2.*(Ref2.int2.Ref3.int3.Refs4.*(Ref4.int4))).get
        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2._Ref1.Refs2.*(Ref2.int2.Ref3.int3))).get

        //        _ <- m(Ns.str.Ref1.int1.Ref2.int2._Ref1._Ns.Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3.Ref4.int4))).get


        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3))) insert List(
        //          (
        //            "a",
        //            List(
        //              (1, 10, List(100))
        //            )
        //          ),
        //          (
        //            "b",
        //            List(
        //              (2, 20, List(200, 201))
        //            )
        //          )
        //        )
        /*
[warn] Obj("", "", 0, List(
[warn]   Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None),
[warn]   Obj("Ns__Refs1", "Refs1", 2, List(
[warn]     Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
[warn]     Obj("Ref1__Ref2", "Ref2", 1, List(
[warn]       Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None),
[warn]       Obj("Ref2__Refs3", "Refs3", 2, List(
[warn]         Prop("Ref3_int3", "int3", "Int", <cast-lambda>, <json-lambda>, None)))))))))


Obj("", "Ns", 2, List(
  Prop("Ns_int", "int", "Int", <cast-lambda>, <json-lambda>, None),
  Obj("Ns__Ref1", "Ref1", 1, List(
    Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
    Obj("Ref1__Ref2", "Ref2", 1, List(
      Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None))),
    Prop("Ref1_str1", "str1", "String", <cast-lambda>, <json-lambda>, None))),
  Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None)))
======================================================

        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.str.getJson.map(_ ==>

         */

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2.Ref3.str3))) insert List(
        //          (
        //            "a",
        //            List(
        //              (1, List((10, "aa")))
        //            )
        //          ),
        //          (
        //            "b",
        //            List(
        //              (2, List((20, "bb"), (21, "bc")))
        //            )
        //          )
        //        )
        /*
[warn] Obj("", "", 0, List(
[warn]   Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None),
[warn]   Obj("Ns__Refs1", "Refs1", 2, List(
[warn]     Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
[warn]     Obj("Ref1__Refs2", "Refs2", 2, List(
[warn]       Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None),
[warn]       Obj("Ref2__Ref3", "Ref3", 1, List(
[warn]         Prop("Ref3_str3", "str3", "String", <cast-lambda>, <json-lambda>, None)))))))))
         */
        //
        //
        //
        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3.Ref4.str4))) insert List(
        //          (
        //            "a",
        //            List(
        //              (1, 10, List(
        //                (100, "aaa")))
        //            )
        //          ),
        //          (
        //            "b",
        //            List(
        //              (2, 20, List(
        //                (200, "bbb"),
        //                (201, "bbc")))
        //            )
        //          )
        //        )
        /*
[warn] Obj("", "", 0, List(
[warn]   Prop("Ns_str", "str", "String", <cast-lambda>, <json-lambda>, None),
[warn]   Obj("Ns__Refs1", "Refs1", 2, List(
[warn]     Prop("Ref1_int1", "int1", "Int", <cast-lambda>, <json-lambda>, None),
[warn]     Obj("Ref1__Ref2", "Ref2", 1, List(
[warn]       Prop("Ref2_int2", "int2", "Int", <cast-lambda>, <json-lambda>, None),
[warn]       Obj("Ref2__Refs3", "Refs3", 2, List(
[warn]         Prop("Ref3_int3", "int3", "Int", <cast-lambda>, <json-lambda>, None),
[warn]         Obj("Ref3__Ref4", "Ref4", 1, List(
[warn]           Prop("Ref4_str4", "str4", "String", <cast-lambda>, <json-lambda>, None)))))))))))
         */


        //        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2) insert List(
        //          ("a", List((Some(1), 10), (None, 20))),
        //          ("b", List((Some(3), 30)))
        //        )
        //
        //        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2).getJson.map(_ ==>
        //          """{
        //            |  "data": {
        //            |    "Ns": [
        //            |      {
        //            |        "str": "a",
        //            |        "Refs1: [
        //            |          {
        //            |            "int1": 1,
        //            |            "Ref2": {
        //            |              "int2": 10
        //            |            }
        //            |          },
        //            |          {
        //            |            "int1": null,
        //            |            "Ref2": {
        //            |              "int2": 20
        //            |            }
        //            |          }
        //            |        ]
        //            |      },
        //            |      {
        //            |        "str": "b",
        //            |        "Refs1: [
        //            |          {
        //            |            "int1": 3,
        //            |            "Ref2": {
        //            |              "int2": 30
        //            |            }
        //            |          }
        //            |        ]
        //            |      }
        //            |    ]
        //            |  }
        //            |}""".stripMargin)
        //
        //        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2).getJson.map(_ ==>
        //          """{
        //            |  "data": {
        //            |    "Ns": [
        //            |      {
        //            |        "str": "a",
        //            |        "Refs1": [
        //            |          {
        //            |            "int1": 1,
        //            |            "int2": 10
        //            |          },
        //            |          {
        //            |            "int1": null,
        //            |            "int2": 20
        //            |          }
        //            |        ]
        //            |      },
        //            |      {
        //            |        "str": "b",
        //            |        "Refs1": [
        //            |          {
        //            |            "int1": 3,
        //            |            "int2": 30
        //            |          }
        //            |        ]
        //            |      }
        //            |    ]
        //            |  }
        //            |}
        //            |
        //            |""".stripMargin)

      } yield ()
    }


  }
}
