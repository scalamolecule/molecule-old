package moleculeTests.tests.core.sorting

import molecule.core.util.Executor._
import molecule.datomic.api.in1_out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.sorting.SortAttrs.core
import moleculeTests.tests.core.sorting.SortComposites.core
import utest._

object SortNested extends AsyncTestSuite {

  lazy val tests = Tests {

    "Basic" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1) insert List(
          ("A", List(1, 2)),
          ("B", List(1, 2)),
        )

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).get.map(_ ==> List(
          ("A", List(1, 2)),
          ("B", List(1, 2)),
        ))
        _ <- Ns.str.a1.Refs1.*(Ref1.int1.d1).get.map(_ ==> List(
          ("A", List(2, 1)),
          ("B", List(2, 1)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.a1).get.map(_ ==> List(
          ("B", List(1, 2)),
          ("A", List(1, 2)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d1).get.map(_ ==> List(
          ("B", List(2, 1)),
          ("A", List(2, 1)),
        ))


        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "A",
            |        "Refs1": [
            |          {
            |            "int1": 1
            |          },
            |          {
            |            "int1": 2
            |          }
            |        ]
            |      },
            |      {
            |        "str": "B",
            |        "Refs1": [
            |          {
            |            "int1": 1
            |          },
            |          {
            |            "int1": 2
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.d1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "A",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str": "B",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- Ns.str.d1.Refs1.*(Ref1.int1.a1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "B",
            |        "Refs1": [
            |          {
            |            "int1": 1
            |          },
            |          {
            |            "int1": 2
            |          }
            |        ]
            |      },
            |      {
            |        "str": "A",
            |        "Refs1": [
            |          {
            |            "int1": 1
            |          },
            |          {
            |            "int1": 2
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "B",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str": "A",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )


        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o1.str ==> "A"
          o1.Refs1.head.int1 ==> 1
          o1.Refs1.last.int1 ==> 2

          o2.str ==> "B"
          o2.Refs1.head.int1 ==> 1
          o2.Refs1.last.int1 ==> 2
        }

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o1.str ==> "A"
          o1.Refs1.last.int1 ==> 2
          o1.Refs1.head.int1 ==> 1

          o2.str ==> "B"
          o2.Refs1.last.int1 ==> 2
          o2.Refs1.head.int1 ==> 1
        }

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o2.str ==> "B"
          o2.Refs1.head.int1 ==> 1
          o2.Refs1.last.int1 ==> 2

          o1.str ==> "A"
          o1.Refs1.head.int1 ==> 1
          o1.Refs1.last.int1 ==> 2
        }

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o2.str ==> "B"
          o2.Refs1.last.int1 ==> 2
          o2.Refs1.head.int1 ==> 1

          o1.str ==> "A"
          o1.Refs1.last.int1 ==> 2
          o1.Refs1.head.int1 ==> 1
        }
      } yield ()
    }


    "Options" - core { implicit conn =>
      for {
        _ <- Ns.int.str$.Refs1.*(Ref1.int1.str1$) insert List(
          (1, Some("A"), List(
            (1, Some("a")),
            (1, Some("b")),
            (2, Some("a")),
            (2, Some("b")),
            (2, None),
          )),
          (2, Some("B"), List(
            (1, Some("a")),
            (1, Some("b")),
            (2, Some("a")),
            (2, Some("b")),
          )),
          (3, Some("C"), List()),
          (4, None, List()),
        )

        // a1 *? a1/a2
        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.a1.str1$.a2).get.map(_ ==> List(
          (4, None, List()),
          (1, Some("A"), List(
            (1, Some("a")),
            (1, Some("b")),
            (2, None),
            (2, Some("a")),
            (2, Some("b")),
          )),
          (2, Some("B"), List(
            (1, Some("a")),
            (1, Some("b")),
            (2, Some("a")),
            (2, Some("b")),
          )),
          (3, Some("C"), List()),
        ))

        // a1 *? a1/d2
        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.a1.str1$.d2).get.map(_ ==> List(
          (4, None, List()),
          (1, Some("A"), List(
            (1, Some("b")),
            (1, Some("a")),
            (2, Some("b")),
            (2, Some("a")),
            (2, None),
          )),
          (2, Some("B"), List(
            (1, Some("b")),
            (1, Some("a")),
            (2, Some("b")),
            (2, Some("a")),
          )),
          (3, Some("C"), List()),
        ))

        // a1 *? d1/a2
        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.d1.str1$.a2).get.map(_ ==> List(
          (4, None, List()),
          (1, Some("A"), List(
            (2, None),
            (2, Some("a")),
            (2, Some("b")),
            (1, Some("a")),
            (1, Some("b")),
          )),
          (2, Some("B"), List(
            (2, Some("a")),
            (2, Some("b")),
            (1, Some("a")),
            (1, Some("b")),
          )),
          (3, Some("C"), List()),
        ))

        // a1 *? d1/d2
        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.d1.str1$.d2).get.map(_ ==> List(
          (4, None, List()),
          (1, Some("A"), List(
            (2, Some("b")),
            (2, Some("a")),
            (2, None),
            (1, Some("b")),
            (1, Some("a")),
          )),
          (2, Some("B"), List(
            (2, Some("b")),
            (2, Some("a")),
            (1, Some("b")),
            (1, Some("a")),
          )),
          (3, Some("C"), List()),
        ))

        // d1 *? d1/d2
        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d1.str1$.d2).get.map(_ ==> List(
          (3, Some("C"), List()),
          (2, Some("B"), List(
            (2, Some("b")),
            (2, Some("a")),
            (1, Some("b")),
            (1, Some("a")),
          )),
          (1, Some("A"), List(
            (2, Some("b")),
            (2, Some("a")),
            (2, None),
            (1, Some("b")),
            (1, Some("a")),
          )),
          (4, None, List()),
        ))

        // d1 *? d2/d1
        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d2.str1$.d1).get.map(_ ==> List(
          (3, Some("C"), List()),
          (2, Some("B"), List(
            (2, Some("b")),
            (1, Some("b")),
            (2, Some("a")),
            (1, Some("a")),
          )),
          (1, Some("A"), List(
            (2, Some("b")),
            (1, Some("b")),
            (2, Some("a")),
            (1, Some("a")),
            (2, None),
          )),
          (4, None, List()),
        ))

        // d1 *? d2/d1
        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d2.str1$.d1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "str$": "C",
            |        "Refs1": []
            |      },
            |      {
            |        "int": 2,
            |        "str$": "B",
            |        "Refs1": [
            |          {
            |            "int1": 1,
            |            "str1$": "a"
            |          },
            |          {
            |            "int1": 1,
            |            "str1$": "b"
            |          },
            |          {
            |            "int1": 2,
            |            "str1$": "a"
            |          },
            |          {
            |            "int1": 2,
            |            "str1$": "b"
            |          }
            |        ]
            |      },
            |      {
            |        "int": 1,
            |        "str$": "A",
            |        "Refs1": [
            |          {
            |            "int1": 1,
            |            "str1$": "a"
            |          },
            |          {
            |            "int1": 1,
            |            "str1$": "b"
            |          },
            |          {
            |            "int1": 2,
            |            "str1$": "a"
            |          },
            |          {
            |            "int1": 2,
            |            "str1$": "b"
            |          },
            |          {
            |            "int1": 2,
            |            "str1$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int": 4,
            |        "str$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d2.str1$.d1).getObjs.collect { case List(o1, o2, o3, o4) =>
          o1.int ==> 3
          o1.str$ ==> Some("C")
          o1.Refs1 ==> Nil

          o2.int ==> 2
          o2.str$ ==> Some("B")
          o2.Refs1(0).int1 ==> 2
          o2.Refs1(0).str1$ ==> Some("b")
          o2.Refs1(1).int1 ==> 1
          o2.Refs1(1).str1$ ==> Some("b")
          o2.Refs1(2).int1 ==> 2
          o2.Refs1(2).str1$ ==> Some("a")
          o2.Refs1(3).int1 ==> 1
          o2.Refs1(3).str1$ ==> Some("a")

          o3.int ==> 1
          o3.str$ ==> Some("A")
          o3.Refs1(0).int1 ==> 2
          o3.Refs1(0).str1$ ==> Some("b")
          o3.Refs1(1).int1 ==> 1
          o3.Refs1(1).str1$ ==> Some("b")
          o3.Refs1(2).int1 ==> 2
          o3.Refs1(2).str1$ ==> Some("a")
          o3.Refs1(3).int1 ==> 1
          o3.Refs1(3).str1$ ==> Some("a")
          o3.Refs1(4).int1 ==> 2
          o3.Refs1(4).str1$ ==> None

          o4.int ==> 4
          o4.str$ ==> None
          o4.Refs1 ==> Nil
        }

        // d1(expr) *? d2/d1
        _ <- Ns.int.str.>("A").d1.Refs1.*?(Ref1.int1.d2.str1$.d1).get.map(_ ==> List(
          (3, "C", List()),
          (2, "B", List(
            (2, Some("b")),
            (1, Some("b")),
            (2, Some("a")),
            (1, Some("a")),
          ))
        ))

        // expr+d1 * a2/d1(expr)
        _ <- Ns.int.<=(2).str.d1.Refs1.*(Ref1.int1.a2.str1.>=("a").d1).get.map(_ ==> List(
          (2, "B", List(
            (1, "b"),
            (2, "b"),
            (1, "a"),
            (2, "a"),
          )),
          (1, "A", List(
            (1, "b"),
            (2, "b"),
            (1, "a"),
            (2, "a"),
          )),
        ))
      } yield ()
    }


    "2 sub levels" - core { implicit conn =>
      for {
        _ <- Ns.int.str$.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.str2)) insert List(
          (1, Some("A"), List(
            (1, Some("a"), List("x", "y")),
            (1, Some("b"), List("x", "y")),
            (2, Some("a"), List("x", "y")),
            (2, Some("b"), Nil),
            (2, None, List("x", "y")),
          )),
          (2, Some("B"), List(
            (1, Some("a"), List("x", "y")),
            (1, Some("b"), List("x", "y")),
            (2, Some("a"), List("x", "y")),
            (2, Some("b"), List("x", "y")),
          )),
          (3, Some("C"), Nil),
          (4, None, Nil),
        )


        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.d2.str1$.a1.Refs2.*?(Ref2.str2.d1)).get.map(_ ==> List(
          (4, None, Nil),
          (1, Some("A"), List(
            (2, None, List("y", "x")),
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (2, Some("b"), Nil),
            (1, Some("b"), List("y", "x")),
          )),
          (2, Some("B"), List(
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (2, Some("b"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
          (3, Some("C"), Nil),
        ))

        _ <- Ns.int.a1.str$.Refs1.*(Ref1.int1.d2.str1$.a1.Refs2.*(Ref2.str2.d1)).get.map(_ ==> List(
          (1, Some("A"), List(
            (2, None, List("y", "x")),
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
          (2, Some("B"), List(
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (2, Some("b"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
        ))

        _ <- Ns.int.str$.a1.Refs1.*(Ref1.int1.d2.str1$.a1.Refs2.*(Ref2.str2.d1)).get.map(_ ==> List(
          (1, Some("A"), List(
            (2, None, List("y", "x")),
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
          (2, Some("B"), List(
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (2, Some("b"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
        ))
      } yield ()
    }


    "Optional nested top attribute types" - {

      "String" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2)

          _ <- Ns.int.a1.getObjs.collect { case List(o1, o2) =>
            o1.int ==> 1
            o2.int ==> 2
          }
          _ <- Ns.int.d1.getObjs.collect { case List(o1, o2) =>
            o1.int ==> 2
            o2.int ==> 1
          }
        } yield ()
      }
    }
  }
}
