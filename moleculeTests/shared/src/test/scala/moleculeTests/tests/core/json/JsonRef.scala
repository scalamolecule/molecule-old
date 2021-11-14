package moleculeTests.tests.core.json

import molecule.datomic.api.out11._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object JsonRef extends AsyncTestSuite {

  lazy val tests = Tests {

    "Ref card 1" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.int1 insert List(("a", 1), ("b", 2))

        _ <- Ns.str.Ref1.int1.getJson.map { result =>
          val orderings  = List(
            ("a", 1, "b", 2),
            ("b", 2, "a", 1)
          )
          val variations = orderings.map { case (s1, i1, s2, i2) =>
            s"""{
               |  "data": {
               |    "Ns": [
               |      {
               |        "str": "$s1",
               |        "Ref1": {
               |          "int1": $i1
               |        }
               |      },
               |      {
               |        "str": "$s2",
               |        "Ref1": {
               |          "int1": $i2
               |        }
               |      }
               |    ]
               |  }
               |}""".stripMargin
          }
          variations.contains(result) ==> true
        }
      } yield ()
    }


    "Ref card 2" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.*(Ref1.int1)) insert List(
          ("a", List(1)),
          ("b", List(2, 3))
        )

        // Flat
        _ <- Ns.str.Refs1.int1.getJson.map { result =>
          val orderings  = List(
            ("a", "b", "b", 1, 2, 3),
            ("b", "a", "b", 2, 1, 3),
          )
          val variations = orderings.map {
            case (a, b, c, d, e, f) =>
              s"""{
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "str": "$a",
                 |        "Refs1": {
                 |          "int1": $d
                 |        }
                 |      },
                 |      {
                 |        "str": "$b",
                 |        "Refs1": {
                 |          "int1": $e
                 |        }
                 |      },
                 |      {
                 |        "str": "$c",
                 |        "Refs1": {
                 |          "int1": $f
                 |        }
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
          }
          if (!variations.contains(result)) {
            println(result)
          }
          variations.contains(result) ==> true
        }

        // Nested
        _ <- Ns.str.Refs1.*(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 3
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Ref / ref attr" - core { implicit conn =>
      for {
        List(_, refA, _, refB) <- Ns.int.Ref1.str1 insert List(
          (1, "a"),
          (2, "b")
        ) map (_.eids)

        // Ref namespace
        _ <- Ns.int.Ref1.str1.getJson.map { result =>
          val variations = List(
            (1, "a", 2, "b"),
            (2, "b", 1, "a")
          ).map { case (i1, s1, i2, s2) =>
            s"""{
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": $i1,
               |        "Ref1": {
               |          "str1": "$s1"
               |        }
               |      },
               |      {
               |        "int": $i2,
               |        "Ref1": {
               |          "str1": "$s2"
               |        }
               |      }
               |    ]
               |  }
               |}""".stripMargin
          }
          variations.contains(result) ==> true
        }

        // Ref attr
        _ <- Ns.int.ref1.getJson.map { result =>
          val variations = List(
            (1, refA, 2, refB),
            (2, refB, 1, refA),
          ).map { case (i1, r1, i2, r2) =>
            s"""{
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": $i1,
               |        "ref1": $r1
               |      },
               |      {
               |        "int": $i2,
               |        "ref1": $r2
               |      }
               |    ]
               |  }
               |}""".stripMargin
          }
          variations.contains(result) ==> true
        }
      } yield ()
    }


    "ref/backref" - core { implicit conn =>
      for {
        _ <- Ns.int(0).str("a")
          .Ref1.int1(1).str1("b")
          .Refs2.int2(22)._Ref1
          .Ref2.int2(2).str2("c")._Ref1._Ns
          .Refs1.int1(11)
          .save

        _ <- Ns.int.Ref1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.int1._Ns.str.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1
            |        },
            |        "str": "a"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.int1._Ns.Refs1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1
            |        },
            |        "Refs1": {
            |          "int1": 11
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          },
            |          "str1": "b"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.Ref2.int2._Ref1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "Ref2": {
            |            "int2": 2
            |          },
            |          "int1": 1
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.Refs2.int2.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          },
            |          "Refs2": {
            |            "int2": 22
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.Ref2.int2._Ref1.Refs2.int2.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "Ref2": {
            |            "int2": 2
            |          },
            |          "Refs2": {
            |            "int2": 22
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.str.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          },
            |          "str1": "b"
            |        },
            |        "str": "a"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1._Ns.str.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          }
            |        },
            |        "str": "a"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.Ref2.int2._Ref1._Ns.str.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "Ref2": {
            |            "int2": 2
            |          }
            |        },
            |        "str": "a"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.Refs1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          },
            |          "str1": "b"
            |        },
            |        "Refs1": {
            |          "int1": 11
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1._Ns.Refs1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          }
            |        },
            |        "Refs1": {
            |          "int1": 11
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.Ref1.Ref2.int2._Ref1._Ns.Refs1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 0,
            |        "Ref1": {
            |          "Ref2": {
            |            "int2": 2
            |          }
            |        },
            |        "Refs1": {
            |          "int1": 11
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.Ref1.Ref2.int2._Ref1._Ns.Refs1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "Ref1": {
            |          "Ref2": {
            |            "int2": 2
            |          }
            |        },
            |        "Refs1": {
            |          "int1": 11
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }
  }
}
