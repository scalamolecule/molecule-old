package moleculeTests.tests.core.json

import molecule.datomic.api.out11._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object JsonNested extends AsyncTestSuite {

  lazy val tests = Tests {

    "Nested, 1 level" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1 * Ref1.int1 insert List(
          ("a", List(1)),
          ("b", List(2, 3)),
          ("c", Nil)
        )

        // Flat
        _ <- Ns.str.Refs1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1": 1
            |        }
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": {
            |          "int1": 2
            |        }
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": {
            |          "int1": 3
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // NestedOpt
        _ <- Ns.str.Refs1.*?(Ref1.int1).getJson.map(_ ==>
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
            |      },
            |      {
            |        "str": "c",
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

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


    "Nested, 2 levels" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)) insert List(
          (
            "a",
            List(
              (1, List(10)
              )
            )
          ),
          (
            "b",
            List(
              (2, List(20)),
              (3, List(30, 31))
            )
          )
        )

        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 1,
            |            "Refs2": [
            |              {
            |                "int2": 10
            |              }
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1": 2,
            |            "Refs2": [
            |              {
            |                "int2": 20
            |              }
            |            ]
            |          },
            |          {
            |            "int1": 3,
            |            "Refs2": [
            |              {
            |                "int2": 30
            |              },
            |              {
            |                "int2": 31
            |              }
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Nested enum after ref" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.enum1) insert List(("a", List("enum11")))
        _ <- m(Ns.str.Refs1 * Ref1.enum1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "enum1": "enum11"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Nested ref without attribute" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.int1.Ref2.int2) insert List(
          ("a", List((11, 12))),
          ("b", List((21, 22))))


        _ <- m(Ns.str.Refs1 * Ref1.int1_.Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "Ref2": {
            |              "int2": 12
            |            }
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "Ref2": {
            |              "int2": 22
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // We can omit tacit attribute between Ref1 and Ref2
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "Ref2": {
            |              "int2": 12
            |            }
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "Ref2": {
            |              "int2": 22
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Intermediate references without attributes" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2) insert List(
          ("a", List(10, 20)),
          ("b", List(30))
        )
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "Ref2": {
            |              "int2": 10
            |            }
            |          },
            |          {
            |            "Ref2": {
            |              "int2": 20
            |            }
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "Ref2": {
            |              "int2": 30
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Intermediate references with optional attributes" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2) insert List(
          ("a", List((Some(1), 10), (None, 20))),
          ("b", List((Some(3), 30)))
        )

        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1$": 1,
            |            "Ref2": {
            |              "int2": 10
            |            }
            |          },
            |          {
            |            "int1$": null,
            |            "Ref2": {
            |              "int2": 20
            |            }
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1$": 3,
            |            "Ref2": {
            |              "int2": 30
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Optional attribute, flat card-many ref" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.int1$.Refs2 * Ref2.int2) insert List(
          ("a", Some(2), List(20)),
          ("b", None, List(10, 11))
        )

        // mandatory int1
        _ <- m(Ns.str.Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1": 2,
            |          "Refs2": [
            |            {
            |              "int2": 20
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // optional int1$
        _ <- m(Ns.str.Refs1.int1$.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1$": 2,
            |          "Refs2": [
            |            {
            |              "int2": 20
            |            }
            |          ]
            |        }
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": {
            |          "int1$": null,
            |          "Refs2": [
            |            {
            |              "int2": 10
            |            },
            |            {
            |              "int2": 11
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // relationship exists despite not having attribute values
        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "Refs2": [
            |            {
            |              "int2": 20
            |            }
            |          ]
            |        }
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": {
            |          "Refs2": [
            |            {
            |              "int2": 10
            |            },
            |            {
            |              "int2": 11
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Optional attribute, card-one ref" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Ref1.int1$.Refs2 * Ref2.int2) insert List(
          ("a", Some(2), List(20)),
          ("b", None, List(10, 11))
        )

        // mandatory int1
        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 2,
            |          "Refs2": [
            |            {
            |              "int2": 20
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // optional int1$
        _ <- m(Ns.str.Ref1.int1$.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1$": 2,
            |          "Refs2": [
            |            {
            |              "int2": 20
            |            }
            |          ]
            |        }
            |      },
            |      {
            |        "str": "b",
            |        "Ref1": {
            |          "int1$": null,
            |          "Refs2": [
            |            {
            |              "int2": 10
            |            },
            |            {
            |              "int2": 11
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // relationship exists despite not having attribute values
        _ <- m(Ns.str.Ref1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "Refs2": [
            |            {
            |              "int2": 20
            |            }
            |          ]
            |        }
            |      },
            |      {
            |        "str": "b",
            |        "Ref1": {
            |          "Refs2": [
            |            {
            |              "int2": 10
            |            },
            |            {
            |              "int2": 11
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "One - one" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.int2) insert List(("a", 1, List(2)))

        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1,
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ns.str.Ref1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "One - one - many" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.ints2) insert List(("a", 1, List(Set(2, 3))))

        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.ints2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1,
            |          "Refs2": [
            |            {
            |              "ints2": [
            |                3,
            |                2
            |              ]
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Many - one" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.int1.Refs2 * Ref2.int2) insert List(("a", 1, List(2)))

        _ <- m(Ns.str.Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1": 1,
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Flat ManyRef simple" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.int1.Refs2.int2.insert("a", 1, 2)

        _ <- Ns.str.Refs1.int1.Refs2.int2.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1": 1,
            |          "Refs2": {
            |            "int2": 2
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ns.str.Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1": 1,
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ns.str_("a").Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "Refs1": {
            |          "int1": 1,
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ns.str_("a").Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "Refs1": {
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Flat ManyRef + many with extra attrs" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.*(Ref1.int1.str1.Refs2 * Ref2.int2.str2)) insert List(
          ("a", List(
            (1, "x", List((11, "xx"), (12, "xxx"))),
            (2, "y", List((21, "yy"), (22, "yyy")))))
        )

        _ <- m(Ns.str.Refs1.int1.str1.Refs2 * Ref2.int2.str2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1": 1,
            |          "str1": "x",
            |          "Refs2": [
            |            {
            |              "int2": 11,
            |              "str2": "xx"
            |            },
            |            {
            |              "int2": 12,
            |              "str2": "xxx"
            |            }
            |          ]
            |        }
            |      },
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1": 2,
            |          "str1": "y",
            |          "Refs2": [
            |            {
            |              "int2": 21,
            |              "str2": "yy"
            |            },
            |            {
            |              "int2": 22,
            |              "str2": "yyy"
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "None - one" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2) insert List(("a", List(2)))

        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "Refs2": [
            |            {
            |              "int2": 2
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Flat ManyRef + many" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)) insert List(
          ("a", List(
            (1, List(11, 12)),
            (2, List(21, 22)))),
          ("b", List(
            (3, List(31, 32)),
            (4, List(41, 42))))
        )

        // Fully nested
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 1,
            |            "Refs2": [
            |              {
            |                "int2": 11
            |              },
            |              {
            |                "int2": 12
            |              }
            |            ]
            |          },
            |          {
            |            "int1": 2,
            |            "Refs2": [
            |              {
            |                "int2": 21
            |              },
            |              {
            |                "int2": 22
            |              }
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1": 3,
            |            "Refs2": [
            |              {
            |                "int2": 31
            |              },
            |              {
            |                "int2": 32
            |              }
            |            ]
            |          },
            |          {
            |            "int1": 4,
            |            "Refs2": [
            |              {
            |                "int2": 41
            |              },
            |              {
            |                "int2": 42
            |              }
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        // Ordering only stable with Peer
        _ <- if (system == SystemPeer) {
          for {
            // Semi-nested A
            _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.int2).getJson.map(_ ==>
              """{
                |  "data": {
                |    "Ns": [
                |      {
                |        "str": "a",
                |        "Refs1": [
                |          {
                |            "int1": 1,
                |            "Refs2": {
                |              "int2": 11
                |            }
                |          },
                |          {
                |            "int1": 1,
                |            "Refs2": {
                |              "int2": 12
                |            }
                |          },
                |          {
                |            "int1": 2,
                |            "Refs2": {
                |              "int2": 22
                |            }
                |          },
                |          {
                |            "int1": 2,
                |            "Refs2": {
                |              "int2": 21
                |            }
                |          }
                |        ]
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": [
                |          {
                |            "int1": 3,
                |            "Refs2": {
                |              "int2": 32
                |            }
                |          },
                |          {
                |            "int1": 3,
                |            "Refs2": {
                |              "int2": 31
                |            }
                |          },
                |          {
                |            "int1": 4,
                |            "Refs2": {
                |              "int2": 41
                |            }
                |          },
                |          {
                |            "int1": 4,
                |            "Refs2": {
                |              "int2": 42
                |            }
                |          }
                |        ]
                |      }
                |    ]
                |  }
                |}""".stripMargin)

            // Semi-nested A without intermediary attr `int1`
            _ <- Ns.str.Refs1.*(Ref1.Refs2.int2).getJson.map(_ ==>
              """{
                |  "data": {
                |    "Ns": [
                |      {
                |        "str": "a",
                |        "Refs1": [
                |          {
                |            "Refs2": {
                |              "int2": 11
                |            }
                |          },
                |          {
                |            "Refs2": {
                |              "int2": 12
                |            }
                |          },
                |          {
                |            "Refs2": {
                |              "int2": 22
                |            }
                |          },
                |          {
                |            "Refs2": {
                |              "int2": 21
                |            }
                |          }
                |        ]
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": [
                |          {
                |            "Refs2": {
                |              "int2": 31
                |            }
                |          },
                |          {
                |            "Refs2": {
                |              "int2": 32
                |            }
                |          },
                |          {
                |            "Refs2": {
                |              "int2": 41
                |            }
                |          },
                |          {
                |            "Refs2": {
                |              "int2": 42
                |            }
                |          }
                |        ]
                |      }
                |    ]
                |  }
                |}""".stripMargin)


            // Semi-nested B
            _ <- Ns.str.Refs1.int1.Refs2.*(Ref2.int2).getJson.map(_ ==>
              """{
                |  "data": {
                |    "Ns": [
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "int1": 1,
                |          "Refs2": [
                |            {
                |              "int2": 11
                |            },
                |            {
                |              "int2": 12
                |            }
                |          ]
                |        }
                |      },
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "int1": 2,
                |          "Refs2": [
                |            {
                |              "int2": 21
                |            },
                |            {
                |              "int2": 22
                |            }
                |          ]
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "int1": 3,
                |          "Refs2": [
                |            {
                |              "int2": 31
                |            },
                |            {
                |              "int2": 32
                |            }
                |          ]
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "int1": 4,
                |          "Refs2": [
                |            {
                |              "int2": 41
                |            },
                |            {
                |              "int2": 42
                |            }
                |          ]
                |        }
                |      }
                |    ]
                |  }
                |}""".stripMargin)


            // Semi-nested B without intermediary attr `int1`
            _ <- Ns.str.Refs1.Refs2.*(Ref2.int2).getJson.map(_ ==>
              """{
                |  "data": {
                |    "Ns": [
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "Refs2": [
                |            {
                |              "int2": 11
                |            },
                |            {
                |              "int2": 12
                |            },
                |            {
                |              "int2": 21
                |            },
                |            {
                |              "int2": 22
                |            }
                |          ]
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "Refs2": [
                |            {
                |              "int2": 31
                |            },
                |            {
                |              "int2": 32
                |            },
                |            {
                |              "int2": 41
                |            },
                |            {
                |              "int2": 42
                |            }
                |          ]
                |        }
                |      }
                |    ]
                |  }
                |}""".stripMargin)


            // Tacit filter
            _ <- m(Ns.str_("a").Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
              """{
                |  "data": {
                |    "Ns": [
                |      {
                |        "Refs1": {
                |          "int1": 1,
                |          "Refs2": [
                |            {
                |              "int2": 11
                |            },
                |            {
                |              "int2": 12
                |            }
                |          ]
                |        }
                |      },
                |      {
                |        "Refs1": {
                |          "int1": 2,
                |          "Refs2": [
                |            {
                |              "int2": 21
                |            },
                |            {
                |              "int2": 22
                |            }
                |          ]
                |        }
                |      }
                |    ]
                |  }
                |}""".stripMargin)

            // Tacit filters
            _ <- m(Ns.str_("a").Refs1.int1_(2).Refs2 * Ref2.int2).getJson.map(_ ==>
              """{
                |  "data": {
                |    "Ns": [
                |      {
                |        "Refs1": {
                |          "Refs2": [
                |            {
                |              "int2": 21
                |            },
                |            {
                |              "int2": 22
                |            }
                |          ]
                |        }
                |      }
                |    ]
                |  }
                |}""".stripMargin)


            // Flat
            _ <- m(Ns.str.Refs1.int1.Refs2.int2).getJson.map(_ ==>
              """{
                |  "data": {
                |    "Ns": [
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "int1": 2,
                |          "Refs2": {
                |            "int2": 21
                |          }
                |        }
                |      },
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "int1": 2,
                |          "Refs2": {
                |            "int2": 22
                |          }
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "int1": 4,
                |          "Refs2": {
                |            "int2": 42
                |          }
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "int1": 4,
                |          "Refs2": {
                |            "int2": 41
                |          }
                |        }
                |      },
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "int1": 1,
                |          "Refs2": {
                |            "int2": 12
                |          }
                |        }
                |      },
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "int1": 1,
                |          "Refs2": {
                |            "int2": 11
                |          }
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "int1": 3,
                |          "Refs2": {
                |            "int2": 31
                |          }
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "int1": 3,
                |          "Refs2": {
                |            "int2": 32
                |          }
                |        }
                |      }
                |    ]
                |  }
                |}""".stripMargin)


            // Flat without intermediary attr `int1`
            res <- m(Ns.str.Refs1.Refs2.int2).getJson.map(_ ==>
              """{
                |  "data": {
                |    "Ns": [
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "Refs2": {
                |            "int2": 21
                |          }
                |        }
                |      },
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "Refs2": {
                |            "int2": 22
                |          }
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "Refs2": {
                |            "int2": 41
                |          }
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "Refs2": {
                |            "int2": 42
                |          }
                |        }
                |      },
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "Refs2": {
                |            "int2": 11
                |          }
                |        }
                |      },
                |      {
                |        "str": "a",
                |        "Refs1": {
                |          "Refs2": {
                |            "int2": 12
                |          }
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "Refs2": {
                |            "int2": 31
                |          }
                |        }
                |      },
                |      {
                |        "str": "b",
                |        "Refs1": {
                |          "Refs2": {
                |            "int2": 32
                |          }
                |        }
                |      }
                |    ]
                |  }
                |}""".stripMargin)
          } yield res
        } else Future.unit
      } yield ()
    }


    "Back ref" - {

      "Nested" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1) insert List(("book", "John", List("Marc")))

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1).getJson.map(_ ==>
            """{
              |  "data": {
              |    "Ns": [
              |      {
              |        "str": "book",
              |        "Ref1": {
              |          "str1": "John"
              |        },
              |        "Refs1": [
              |          {
              |            "str1": "Marc"
              |          }
              |        ]
              |      }
              |    ]
              |  }
              |}""".stripMargin)

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1).getJson.map(_ ==>
            """{
              |  "data": {
              |    "Ns": [
              |      {
              |        "str": "book",
              |        "Ref1": {
              |          "str1": "John"
              |        },
              |        "Refs1": {
              |          "str1": "Marc"
              |        }
              |      }
              |    ]
              |  }
              |}""".stripMargin)
        } yield ()
      }

      "Nested + adjacent" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1.Refs2.str2) insert List(
            ("book", "John", List(("Marc", "Musician")))
          )

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1.Refs2.str2).getJson.map(_ ==>
            """{
              |  "data": {
              |    "Ns": [
              |      {
              |        "str": "book",
              |        "Ref1": {
              |          "str1": "John"
              |        },
              |        "Refs1": [
              |          {
              |            "str1": "Marc",
              |            "Refs2": {
              |              "str2": "Musician"
              |            }
              |          }
              |        ]
              |      }
              |    ]
              |  }
              |}""".stripMargin)

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1.Refs2.str2).getJson.map(_ ==>
            """{
              |  "data": {
              |    "Ns": [
              |      {
              |        "str": "book",
              |        "Ref1": {
              |          "str1": "John"
              |        },
              |        "Refs1": {
              |          "str1": "Marc",
              |          "Refs2": {
              |            "str2": "Musician"
              |          }
              |        }
              |      }
              |    ]
              |  }
              |}""".stripMargin)
        } yield ()
      }

      "Nested + nested" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * (Ref1.str1.Refs2 * Ref2.str2)) insert List(
            ("book", "John", List(("Marc", List("Musician"))))
          )

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * (Ref1.str1.Refs2 * Ref2.str2)).getJson.map(_ ==>
            """{
              |  "data": {
              |    "Ns": [
              |      {
              |        "str": "book",
              |        "Ref1": {
              |          "str1": "John"
              |        },
              |        "Refs1": [
              |          {
              |            "str1": "Marc",
              |            "Refs2": [
              |              {
              |                "str2": "Musician"
              |              }
              |            ]
              |          }
              |        ]
              |      }
              |    ]
              |  }
              |}""".stripMargin)

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1.Refs2.str2).getJson.map(_ ==>
            """{
              |  "data": {
              |    "Ns": [
              |      {
              |        "str": "book",
              |        "Ref1": {
              |          "str1": "John"
              |        },
              |        "Refs1": {
              |          "str1": "Marc",
              |          "Refs2": {
              |            "str2": "Musician"
              |          }
              |        }
              |      }
              |    ]
              |  }
              |}""".stripMargin)
        } yield ()
      }
    }


    "Applied eid" - core { implicit conn =>
      for {
        eid <- Ns.str.Refs1.*(Ref1.int1).insert("a", List(1, 2)).map(_.eid)
        _ <- Ns(eid).Refs1.*(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
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
            |}""".stripMargin)
      } yield ()
    }


    "Implicit initial namespace" - core { implicit conn =>
      for {
        List(ref1a, _, _, _, _, _) <- Ref1.str1.Refs2.*(Ref2.str2).insert(List(
          ("r1a", List("r2a", "r2b")),
          ("r1b", List("r2c", "r2d")) // <-- will not be referenced from Ns
        )).map(_.eids)

        // Both Ns entities reference the same Ref1 entity
        _ <- Ns.str.refs1 insert List(
          ("a", Set(ref1a)),
          ("b", Set(ref1a))
        )

        // Without Ns
        _ <- Ref1.str1.Refs2.*(Ref2.str2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "r1a",
            |        "Refs2": [
            |          {
            |            "str2": "r2a"
            |          },
            |          {
            |            "str2": "r2b"
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "r1b",
            |        "Refs2": [
            |          {
            |            "str2": "r2c"
            |          },
            |          {
            |            "str2": "r2d"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // With Ns
        // "Implicit" reference from Ns to Ref1 (without any attributes) implies that
        // some Ns entity is referencing some Ref1 entity.
        // This excludes "r1b" since no Ns entities reference it.
        _ <- Ns.Refs1.str1.Refs2.*(Ref2.str2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "Refs1": {
            |          "str1": "r1a",
            |          "Refs2": [
            |            {
            |              "str2": "r2a"
            |            },
            |            {
            |              "str2": "r2b"
            |            }
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
