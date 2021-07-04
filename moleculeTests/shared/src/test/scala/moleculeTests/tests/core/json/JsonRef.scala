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
//object JsonRef extends AsyncTestSuite {
//
//  lazy val tests = Tests {
//
//    "Ref card 1" - core { implicit conn =>
//      for {
//        _ <- Ns.str.Ref1.int1 insert List(("a", 1), ("b", 2))
//
//        _ <- Ns.str.Ref1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "a"
//            |        "Ref1": {
//            |          "int1": 1
//            |        }
//            |      },
//            |      {
//            |        "str": "b"
//            |        "Ref1": {
//            |          "int1": 2
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//      } yield ()
//    }
//
//
//    "Ref card 2" - core { implicit conn =>
//      for {
//        _ <- m(Ns.str.Refs1.*(Ref1.int1)) insert List(
//          ("a", List(1)),
//          ("b", List(2, 3))
//        )
//
//        // Flat
//        _ <- Ns.str.Refs1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "a",
//            |        "Refs1": {
//            |          "int1": 1
//            |        }
//            |      },
//            |      {
//            |        "str": "b",
//            |        "Refs1": {
//            |          "int1": 2
//            |        }
//            |      },
//            |      {
//            |        "str": "b"
//            |        "Refs1": {
//            |          "int1": 3
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        // Nested
//        _ <- Ns.str.Refs1.*(Ref1.int1).getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "a",
//            |        "Refs1": [
//            |          {
//            |            "int1": 1
//            |          }
//            |        ]
//            |      },
//            |      {
//            |        "str": "b",
//            |        "Refs1": [
//            |          {
//            |            "int1": 2
//            |          },
//            |          {
//            |            "int1": 3
//            |          }
//            |        ]
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//      } yield ()
//    }
//
//
//    "Ref card 2" - core { implicit conn =>
//      for {
//        _ <- m(Ns.str.Refs1.*(Ref1.int1)) insert List(
//          ("a", List(1)),
//          ("b", List(2, 3))
//        )
//
//        // Flat
//        _ <- Ns.str.Refs1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 1
//            |        "Refs1": {
//            |          "str1": "a"
//            |        }
//            |      },
//            |      {
//            |        "int": 2
//            |        "Refs1": {
//            |          "str1": "b"
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.str.Refs1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "a"
//            |        "Refs1": [
//            |          {
//            |            "int1": 1
//            |          }
//            |        ]
//            |      },
//            |      {
//            |        "str": "b"
//            |        "Refs1": [
//            |          {
//            |            "int1": 2
//            |          },
//            |          {
//            |            "int1": 3
//            |          }
//            |        ]
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//      } yield ()
//    }
//
//
//    "Ref / ref attr" - core { implicit conn =>
//      for {
//        List(_, refA, _, refB) <- Ns.int.Ref1.str1 insert List(
//          (1, "a"),
//          (2, "b")
//        ) map(_.eids)
//
//        // Ref namespace
//        _ <- Ns.int.Ref1.str1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 1
//            |        "Ref1": {
//            |          "str1": "a"
//            |        }
//            |      },
//            |      {
//            |        "int": 2
//            |        "Ref1": {
//            |          "str1": "b"
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//
//        // Ref attr
//        _ <- Ns.str.ref1.getJson.map(_ ==>
//          s"""{
//             |  "data": {
//             |    "Ns": [
//             |      {
//             |        "int": 1
//             |        "ref1": $refA
//             |      },
//             |      {
//             |        "int": 2
//             |        "ref1": $refB
//             |      }
//             |    ]
//             |  }
//             |}""".stripMargin)
//      } yield ()
//    }
//
//
//    "ref/backref" - core { implicit conn =>
//      for {
//        _ <- Ns.int(0).str("a")
//          .Ref1.int1(1).str1("b")
//          .Refs2.int2(22)._Ref1
//          .Ref2.int2(2).str2("c")._Ref1._Ns
//          .Refs1.int1(11)
//          .save
//
//        _ <- Ns.int.Ref1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.int1._Ns.str.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1
//            |        },
//            |        "str": "a"
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.int1._Ns.Refs1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1
//            |        },
//            |        "Refs1": {
//            |          "int1": 11
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1,
//            |          "Ref2": {
//            |            "int2": 2
//            |          },
//            |          "str1": "b"
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.Ref2.int2._Ref1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "Ref2": {
//            |            "int2": 2
//            |          },
//            |          "int1": 1
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.Refs2.int2.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1,
//            |          "Ref2": {
//            |            "int2": 2
//            |          },
//            |          "Refs2": {
//            |            "int2": 22
//            |          }
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.Ref2.int2._Ref1.Refs2.int2.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "Ref2": {
//            |            "int2": 2
//            |          },
//            |          "Refs2": {
//            |            "int2": 22
//            |          }
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.str.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1,
//            |          "Ref2": {
//            |            "int2": 2
//            |          },
//            |          "str1": "b"
//            |        },
//            |        "str": "a"
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1._Ns.str.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1,
//            |          "Ref2": {
//            |            "int2": 2
//            |          }
//            |        },
//            |        "str": "a"
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.Ref2.int2._Ref1._Ns.str.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "Ref2": {
//            |            "int2": 2
//            |          }
//            |        },
//            |        "str": "a"
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.Refs1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1,
//            |          "Ref2": {
//            |            "int2": 2
//            |          },
//            |          "str1": "b"
//            |        },
//            |        "Refs1": {
//            |          "int1": 11
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1._Ns.Refs1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "int1": 1,
//            |          "Ref2": {
//            |            "int2": 2
//            |          }
//            |        },
//            |        "Refs1": {
//            |          "int1": 11
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.int.Ref1.Ref2.int2._Ref1._Ns.Refs1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "int": 0,
//            |        "Ref1": {
//            |          "Ref2": {
//            |            "int2": 2
//            |          }
//            |        },
//            |        "Refs1": {
//            |          "int1": 11
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//        _ <- Ns.Ref1.Ref2.int2._Ref1._Ns.Refs1.int1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "Ref1": {
//            |          "Ref2": {
//            |            "int2": 2
//            |          }
//            |        },
//            |        "Refs1": {
//            |          "int1": 11
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//      } yield ()
//    }
//  }
//}
