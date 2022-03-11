package moleculeTests.tests.core.sorting

import molecule.core.util.Executor._
import molecule.datomic.api.in1_out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

object SortNested extends AsyncTestSuite {

  lazy val tests = Tests {

//    "Nested" - core { implicit conn =>
//      for {
//        _ <- Ns.str.Refs1.*(Ref1.int1) insert List(
//          ("A", List(1, 2)),
//          ("B", List(1, 2)),
//        )
//
//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).get.map(_ ==> List(
//          ("A", List(1, 2)),
//          ("B", List(1, 2)),
//        ))
//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.d1).get.map(_ ==> List(
//          ("A", List(2, 1)),
//          ("B", List(2, 1)),
//        ))
//        _ <- Ns.str.d1.Refs1.*(Ref1.int1.a1).get.map(_ ==> List(
//          ("B", List(1, 2)),
//          ("A", List(1, 2)),
//        ))
//        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d1).get.map(_ ==> List(
//          ("B", List(2, 1)),
//          ("A", List(2, 1)),
//        ))
//
//
//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "A",
//            |        "Refs1": [
//            |          {
//            |            "int1": 1
//            |          },
//            |          {
//            |            "int1": 2
//            |          }
//            |        ]
//            |      },
//            |      {
//            |        "str": "B",
//            |        "Refs1": [
//            |          {
//            |            "int1": 1
//            |          },
//            |          {
//            |            "int1": 2
//            |          }
//            |        ]
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin
//        )
//
//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.d1).getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "A",
//            |        "Refs1": [
//            |          {
//            |            "int1": 2
//            |          },
//            |          {
//            |            "int1": 1
//            |          }
//            |        ]
//            |      },
//            |      {
//            |        "str": "B",
//            |        "Refs1": [
//            |          {
//            |            "int1": 2
//            |          },
//            |          {
//            |            "int1": 1
//            |          }
//            |        ]
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin
//        )
//
//        _ <- Ns.str.d1.Refs1.*(Ref1.int1.a1).getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "B",
//            |        "Refs1": [
//            |          {
//            |            "int1": 1
//            |          },
//            |          {
//            |            "int1": 2
//            |          }
//            |        ]
//            |      },
//            |      {
//            |        "str": "A",
//            |        "Refs1": [
//            |          {
//            |            "int1": 1
//            |          },
//            |          {
//            |            "int1": 2
//            |          }
//            |        ]
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin
//        )
//
//        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d1).getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "B",
//            |        "Refs1": [
//            |          {
//            |            "int1": 2
//            |          },
//            |          {
//            |            "int1": 1
//            |          }
//            |        ]
//            |      },
//            |      {
//            |        "str": "A",
//            |        "Refs1": [
//            |          {
//            |            "int1": 2
//            |          },
//            |          {
//            |            "int1": 1
//            |          }
//            |        ]
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin
//        )
//
//
//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
//          o1.str ==> "A"
//          o1.Refs1.head.int1 ==> 1
//          o1.Refs1.last.int1 ==> 2
//
//          o2.str ==> "B"
//          o2.Refs1.head.int1 ==> 1
//          o2.Refs1.last.int1 ==> 2
//        }
//
//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
//          o1.str ==> "A"
//          o1.Refs1.last.int1 ==> 2
//          o1.Refs1.head.int1 ==> 1
//
//          o2.str ==> "B"
//          o2.Refs1.last.int1 ==> 2
//          o2.Refs1.head.int1 ==> 1
//        }
//
//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
//          o2.str ==> "B"
//          o2.Refs1.head.int1 ==> 1
//          o2.Refs1.last.int1 ==> 2
//
//          o1.str ==> "A"
//          o1.Refs1.head.int1 ==> 1
//          o1.Refs1.last.int1 ==> 2
//        }
//
//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
//          o2.str ==> "B"
//          o2.Refs1.last.int1 ==> 2
//          o2.Refs1.head.int1 ==> 1
//
//          o1.str ==> "A"
//          o1.Refs1.last.int1 ==> 2
//          o1.Refs1.head.int1 ==> 1
//        }
//      } yield ()
//    }


    "Nested optional" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1) insert List(
          ("A", List(1, 2)),
          ("B", List(1, 2)),
          ("C", List()),
        )

        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.a1).get.map(_ ==> List(
          ("A", List(1, 2)),
          ("B", List(1, 2)),
          ("C", List()),
        ))
        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.d1).get.map(_ ==> List(
          ("A", List(2, 1)),
          ("B", List(2, 1)),
          ("C", List()),
        ))
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.a1).get.map(_ ==> List(
          ("C", List()),
          ("B", List(1, 2)),
          ("A", List(1, 2)),
        ))
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.d1).get.map(_ ==> List(
          ("C", List()),
          ("B", List(2, 1)),
          ("A", List(2, 1)),
        ))


        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.a1).getJson.map(_ ==>
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

        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.d1).getJson.map(_ ==>
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

        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.a1).getJson.map(_ ==>
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

        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.d1).getJson.map(_ ==>
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


        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o1.str ==> "A"
          o1.Refs1.head.int1 ==> 1
          o1.Refs1.last.int1 ==> 2

          o2.str ==> "B"
          o2.Refs1.head.int1 ==> 1
          o2.Refs1.last.int1 ==> 2
        }

        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o1.str ==> "A"
          o1.Refs1.last.int1 ==> 2
          o1.Refs1.head.int1 ==> 1

          o2.str ==> "B"
          o2.Refs1.last.int1 ==> 2
          o2.Refs1.head.int1 ==> 1
        }

        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o2.str ==> "B"
          o2.Refs1.head.int1 ==> 1
          o2.Refs1.last.int1 ==> 2

          o1.str ==> "A"
          o1.Refs1.head.int1 ==> 1
          o1.Refs1.last.int1 ==> 2
        }

        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o2.str ==> "B"
          o2.Refs1.last.int1 ==> 2
          o2.Refs1.head.int1 ==> 1

          o1.str ==> "A"
          o1.Refs1.last.int1 ==> 2
          o1.Refs1.head.int1 ==> 1
        }
      } yield ()
    }

  }
}
