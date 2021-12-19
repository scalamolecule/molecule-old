package moleculeTests.tests.core.json

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object JsonComposite extends AsyncTestSuite {

  lazy val tests = Tests {

    "1 + 1" - core { implicit conn =>
      for {
        _ <- Ref2.int2 + Ns.int insert Seq(
          // Two rows of data
          (1, 11),
          (2, 22)
        )

        _ <- m(Ref2.int2 + Ns.int).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 2
            |        },
            |        "Ns": {
            |          "int": 22
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 1
            |        },
            |        "Ns": {
            |          "int": 11
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "1 + 2" - core { implicit conn =>
      for {
        _ <- Ref2.int2 + Ns.int.str insert Seq(
          // Two rows of data
          (1, (11, "aa")),
          (2, (22, "bb"))
        )

        _ <- m(Ref2.int2 + Ns.int.str).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 1
            |        },
            |        "Ns": {
            |          "int": 11,
            |          "str": "aa"
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 2
            |        },
            |        "Ns": {
            |          "int": 22,
            |          "str": "bb"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "2 + 1" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ns.int insert Seq(
          // Two rows of data
          ((1, "a"), 11),
          ((2, "b"), 22)
        )

        _ <- m(Ref2.int2.str2 + Ns.int).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 2,
            |          "str2": "b"
            |        },
            |        "Ns": {
            |          "int": 22
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 1,
            |          "str2": "a"
            |        },
            |        "Ns": {
            |          "int": 11
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "2 + 2" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ns.int.str insert Seq(
          ((1, "a"), (11, "aa")),
          ((2, "b"), (22, "bb"))
        )

        _ <- m(Ref2.int2.str2 + Ns.int.str).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 1,
            |          "str2": "a"
            |        },
            |        "Ns": {
            |          "int": 11,
            |          "str": "aa"
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 2,
            |          "str2": "b"
            |        },
            |        "Ns": {
            |          "int": 22,
            |          "str": "bb"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "2 + 3 (2 + 1tx)" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ref1.int1.str1.Tx(Ns.str_("Tx meta data")) insert Seq(
          ((1, "a"), (11, "aa")),
          ((2, "b"), (22, "bb"))
        )

        _ <- m(Ref2.int2.str2 + Ref1.int1.str1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 1,
            |          "str2": "a"
            |        },
            |        "Ref1": {
            |          "int1": 11,
            |          "str1": "aa"
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 2,
            |          "str2": "b"
            |        },
            |        "Ref1": {
            |          "int1": 22,
            |          "str1": "bb"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // .. including transaction meta data
        _ <- m(Ref2.int2.str2 + Ref1.int1.str1.Tx(Ns.str)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 1,
            |          "str2": "a"
            |        },
            |        "Ref1": {
            |          "int1": 11,
            |          "str1": "aa",
            |          "Tx": {
            |            "Ns": {
            |              "str": "Tx meta data"
            |            }
            |          }
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 2,
            |          "str2": "b"
            |        },
            |        "Ref1": {
            |          "int1": 22,
            |          "str1": "bb",
            |          "Tx": {
            |            "Ns": {
            |              "str": "Tx meta data"
            |            }
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "2 + 3 (2 + 2tx with ref)" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ref1.int1.str1.Tx(Ns.str_("Tx meta data").Ref1.int1_(42)) insert Seq(
          ((1, "a"), (11, "aa")),
          ((2, "b"), (22, "bb"))
        )

        // Note how ref attr in tx meta data has both a `tx` and `ref1` prefix
        _ <- m(Ref2.int2.str2 + Ref1.int1.str1.Tx(Ns.str.Ref1.int1)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 1,
            |          "str2": "a"
            |        },
            |        "Ref1": {
            |          "int1": 11,
            |          "str1": "aa",
            |          "Tx": {
            |            "Ns": {
            |              "str": "Tx meta data",
            |              "Ref1": {
            |                "int1": 42
            |              }
            |            }
            |          }
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 2,
            |          "str2": "b"
            |        },
            |        "Ref1": {
            |          "int1": 22,
            |          "str1": "bb",
            |          "Tx": {
            |            "Ns": {
            |              "str": "Tx meta data",
            |              "Ref1": {
            |                "int1": 42
            |              }
            |            }
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Card-one ref" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ns.int.Ref1.str1 insert Seq(
          ((1, "a"), (11, "aa")),
          ((2, "b"), (22, "bb"))
        )

        _ <- m(Ref2.int2.str2 + Ns.int.Ref1.str1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 1,
            |          "str2": "a"
            |        },
            |        "Ns": {
            |          "int": 11,
            |          "Ref1": {
            |            "str1": "aa"
            |          }
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 2,
            |          "str2": "b"
            |        },
            |        "Ns": {
            |          "int": 22,
            |          "Ref1": {
            |            "str1": "bb"
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Card-many ref - one value" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ns.int.Refs1.str1 insert Seq(
          ((1, "a"), (11, "aa")),
          ((2, "b"), (22, "bb"))
        )

        _ <- m(Ref2.int2.str2 + Ns.int.Refs1.str1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 1,
            |          "str2": "a"
            |        },
            |        "Ns": {
            |          "int": 11,
            |          "Refs1": {
            |            "str1": "aa"
            |          }
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 2,
            |          "str2": "b"
            |        },
            |        "Ns": {
            |          "int": 22,
            |          "Refs1": {
            |            "str1": "bb"
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Card-many ref - one value 2" - core { implicit conn =>
      for {
        List(_, r1a, _, r1b) <- (Ref2.int2.str2 + Ns.Refs1.int1).insert(Seq(
          ((1, "a"), 11),
          ((2, "b"), 22)
        )).map(_.eids)

        _ <- m(Ref2.int2.str2 + Ns.Refs1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 2,
            |          "str2": "b"
            |        },
            |        "Ns": {
            |          "Refs1": {
            |            "int1": 22
            |          }
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 1,
            |          "str2": "a"
            |        },
            |        "Ns": {
            |          "Refs1": {
            |            "int1": 11
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref2.int2.str2 + Ns.refs1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "composite": [
             |      {
             |        "Ref2": {
             |          "int2": 1,
             |          "str2": "a"
             |        },
             |        "Ns": {
             |          "refs1": [
             |            $r1a
             |          ]
             |        }
             |      },
             |      {
             |        "Ref2": {
             |          "int2": 2,
             |          "str2": "b"
             |        },
             |        "Ns": {
             |          "refs1": [
             |            $r1b
             |          ]
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }
  }
}