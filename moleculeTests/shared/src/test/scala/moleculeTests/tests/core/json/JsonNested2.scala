package moleculeTests.tests.core.json

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object JsonNested2 extends AsyncTestSuite {

  lazy val tests = Tests {

    "0" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(10, 11))
        )
        _ <- Ns.int.Refs1.*(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 10
            |          },
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "1" - core { implicit conn =>
      for {
        _ <- Ns.int.str.Refs1.*(Ref1.int1.str1) insert List(
          (1, "a", List(
            (10, "aa"),
            (11, "bb")
          ))
        )
        _ <- Ns.int.str.Refs1.*(Ref1.int1.str1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 10,
            |            "str1": "aa"
            |          },
            |          {
            |            "int1": 11,
            |            "str1": "bb"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "2" - core { implicit conn =>
      for {

        _ <- Ns.int.str.Refs1.*(Ref1.int1.Ref2.str2) insert List(
          (1, "a", List(
            (10, "aa"),
            (11, "bb")
          ))
        )
        _ <- Ns.int.str.Refs1.*(Ref1.int1.Ref2.str2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 10,
            |            "Ref2": {
            |              "str2": "aa"
            |            }
            |          },
            |          {
            |            "int1": 11,
            |            "Ref2": {
            |              "str2": "bb"
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


      } yield ()
    }

    "3" - core { implicit conn =>
      for {
        _ <- Ns.int.str.Refs1.*(Ref1.Ref2.int2.str2) insert List(
          (1, "a", List(
            (10, "aa"),
            (11, "bb")
          ))
        )
        _ <- Ns.int.str.Refs1.*(Ref1.Ref2.int2.str2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "Ref2": {
            |              "int2": 10,
            |              "str2": "aa"
            |            }
            |          },
            |          {
            |            "Ref2": {
            |              "int2": 11,
            |              "str2": "bb"
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

      } yield ()
    }

    "4" - core { implicit conn =>
      for {
        // 1 Ref on level 0
        _ <- Ns.int.Ref1.str1.Refs2.*(Ref2.int2) insert List(
          (1, "a", List(10, 11))
        )
        _ <- Ns.int.Ref1.str1.Refs2.*(Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Ref1": {
            |          "str1": "a",
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

    "5" - core { implicit conn =>
      for {

        // 2 Refs on level 0
        _ <- Ns.int.Ref1.str1.Ref2.int2.Refs3.*(Ref3.int3) insert List(
          (1, "a", 2, List(10, 11))
        )
        _ <- Ns.int.Ref1.str1.Ref2.int2.Refs3.*(Ref3.int3).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Ref1": {
            |          "str1": "a",
            |          "Ref2": {
            |            "int2": 2,
            |            "Refs3": [
            |              {
            |                "int3": 10
            |              },
            |              {
            |                "int3": 11
            |              }
            |            ]
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)


      } yield ()
    }

    "6" - core { implicit conn =>
      for {
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


      } yield ()
    }

    "7" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.str1.Refs2.*(Ref2.int2)) insert List(
          ("a", List(
            (10, "aa", List(101, 111)))
          ),
          (
            "b", List(
            (20, "bb", List(201, 211)),
            (22, "cc", List(221, 231)))
          )
        )

        _ <- Ns.str.Refs1.*(Ref1.int1.str1.Refs2.*(Ref2.int2)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 10,
            |            "str1": "aa",
            |            "Refs2": [
            |              {
            |                "int2": 101
            |              },
            |              {
            |                "int2": 111
            |              }
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1": 20,
            |            "str1": "bb",
            |            "Refs2": [
            |              {
            |                "int2": 201
            |              },
            |              {
            |                "int2": 211
            |              }
            |            ]
            |          },
            |          {
            |            "int1": 22,
            |            "str1": "cc",
            |            "Refs2": [
            |              {
            |                "int2": 221
            |              },
            |              {
            |                "int2": 231
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

    "8" - core { implicit conn =>
      for {

        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3)) insert List(
          ("a", List(
            (10, 11, "aa", List(101, 111)))
          ),
          (
            "b", List(
            (20, 21, "bb", List(201, 211)),
            (22, 23, "cc", List(221, 231)))
          )
        )

        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 10,
            |            "Ref2": {
            |              "int2": 11,
            |              "str2": "aa",
            |              "Refs3": [
            |                {
            |                  "int3": 101
            |                },
            |                {
            |                  "int3": 111
            |                }
            |              ]
            |            }
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1": 20,
            |            "Ref2": {
            |              "int2": 21,
            |              "str2": "bb",
            |              "Refs3": [
            |                {
            |                  "int3": 201
            |                },
            |                {
            |                  "int3": 211
            |                }
            |              ]
            |            }
            |          },
            |          {
            |            "int1": 22,
            |            "Ref2": {
            |              "int2": 23,
            |              "str2": "cc",
            |              "Refs3": [
            |                {
            |                  "int3": 221
            |                },
            |                {
            |                  "int3": 231
            |                }
            |              ]
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


      } yield ()
    }

    "9" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3.Ref4.int4)) insert List(
          (
            "a", List(
            (10, 11, List(
              (101, 102),
              (111, 112)))
          )),
          (
            "b", List(
            (20, 21, List(
              (201, 202),
              (211, 212))),
            (22, 23, List(
              (221, 222),
              (231, 232)))
          ))
        )

        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3.Ref4.int4)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 10,
            |            "Ref2": {
            |              "int2": 11,
            |              "Refs3": [
            |                {
            |                  "int3": 101,
            |                  "Ref4": {
            |                    "int4": 102
            |                  }
            |                },
            |                {
            |                  "int3": 111,
            |                  "Ref4": {
            |                    "int4": 112
            |                  }
            |                }
            |              ]
            |            }
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1": 20,
            |            "Ref2": {
            |              "int2": 21,
            |              "Refs3": [
            |                {
            |                  "int3": 201,
            |                  "Ref4": {
            |                    "int4": 202
            |                  }
            |                },
            |                {
            |                  "int3": 211,
            |                  "Ref4": {
            |                    "int4": 212
            |                  }
            |                }
            |              ]
            |            }
            |          },
            |          {
            |            "int1": 22,
            |            "Ref2": {
            |              "int2": 23,
            |              "Refs3": [
            |                {
            |                  "int3": 221,
            |                  "Ref4": {
            |                    "int4": 222
            |                  }
            |                },
            |                {
            |                  "int3": 231,
            |                  "Ref4": {
            |                    "int4": 232
            |                  }
            |                }
            |              ]
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


      } yield ()
    }

    "10" - core { implicit conn =>
      for {

        _ <- Ns.str.Ref1.int1.Refs2.*(Ref2.int2) insert List(
          ("a", 1, List(10, 11))
        )

        _ <- Ns.str.Ref1.int1.Refs2.*(Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1,
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

    "11" - core { implicit conn =>
      for {
        // Back ref on level 0
        _ <- Ns.str.Ref1.int1._Ns.Refs1.*(Ref1.int1) insert List(
          ("a", 1, List(10, 11))
        )

        _ <- Ns.str.Ref1.int1._Ns.Refs1.*(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1
            |        },
            |        "Refs1": [
            |          {
            |            "int1": 10
            |          },
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

      } yield ()
    }

    "12" - core { implicit conn =>
      for {

        _ <- Ns.str.Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3) insert List(
          ("a", 1, 2, List(10, 11))
        )
        _ <- Ns.str.Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2,
            |            "Refs3": [
            |              {
            |                "int3": 10
            |              },
            |              {
            |                "int3": 11
            |              }
            |            ]
            |          }
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

      } yield ()
    }

    "13" - core { implicit conn =>
      for {

        _ <- Ns.str.Ref1.int1.Ref2.int2._Ref1.Refs2.*(Ref2.int2) insert List(
          ("a", 1, 2, List(10, 11))
        )
        _ <- Ns.str.Ref1.int1.Ref2.int2._Ref1.Refs2.*(Ref2.int2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          },
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

    "14" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.int1.Ref2.int2._Ref1._Ns.Refs1.*(Ref1.int1) insert List(
          ("a", 1, 2, List(10, 11))
        )
        _ <- Ns.str.Ref1.int1.Ref2.int2._Ref1._Ns.Refs1.*(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          }
            |        },
            |        "Refs1": [
            |          {
            |            "int1": 10
            |          },
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


      } yield ()
    }

    "15" - core { implicit conn =>
      for {
        // Back - ref in nested -------------------------------


        // 1 step back to depth 0
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2._Ref1.Refs2.*(Ref2.int2)) insert List(
          (
            "a",
            List(
              (1, 10, List(11, 12)),
              (2, 20, List(21, 22))
            )
          )
        )
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2._Ref1.Refs2.*(Ref2.int2)).getJson.map(_ ==>
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
            |            },
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
            |            "Ref2": {
            |              "int2": 20
            |            },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)


      } yield ()
    }

    "16" - core { implicit conn =>
      for {
        // 2 steps back to depth0
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Ref3.int3._Ref2._Ref1.Refs2.*(Ref2.int2)) insert List(
          (
            "a",
            List(
              (1, 10, 100, List(11, 12)),
              (2, 20, 200, List(21, 22))
            )
          )
        )
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Ref3.int3._Ref2._Ref1.Refs2.*(Ref2.int2)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 1,
            |            "Ref2": {
            |              "int2": 10,
            |              "Ref3": {
            |                "int3": 100
            |              }
            |            },
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
            |            "Ref2": {
            |              "int2": 20,
            |              "Ref3": {
            |                "int3": 200
            |              }
            |            },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)


      } yield ()
    }

    "17" - core { implicit conn =>
      for {
        // 1 step back to depth 1
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Ref3.int3._Ref2.Refs3.*(Ref3.int3)) insert List(
          (
            "a",
            List(
              (1, 10, 100, List(101, 102)),
              (2, 20, 200, List(201, 202))
            )
          )
        )
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.Ref3.int3._Ref2.Refs3.*(Ref3.int3)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 1,
            |            "Ref2": {
            |              "int2": 10,
            |              "Ref3": {
            |                "int3": 100
            |              },
            |              "Refs3": [
            |                {
            |                  "int3": 101
            |                },
            |                {
            |                  "int3": 102
            |                }
            |              ]
            |            }
            |          },
            |          {
            |            "int1": 2,
            |            "Ref2": {
            |              "int2": 20,
            |              "Ref3": {
            |                "int3": 200
            |              },
            |              "Refs3": [
            |                {
            |                  "int3": 201
            |                },
            |                {
            |                  "int3": 202
            |                }
            |              ]
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "18" - core { implicit conn =>
      for {
        // Back ref on last level
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2.Ref3.int3._Ref2.str2)) insert List(
          ("a", List(
            (1, List(
              (10, 100, "aa"),
              (11, 111, "bb"),
            )))
          )
        )
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2.Ref3.int3._Ref2.str2)).getJson.map(_ ==>
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
            |                "int2": 10,
            |                "Ref3": {
            |                  "int3": 100
            |                },
            |                "str2": "aa"
            |              },
            |              {
            |                "int2": 11,
            |                "Ref3": {
            |                  "int3": 111
            |                },
            |                "str2": "bb"
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


    "19" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.int1.Ref2.int2._Ref1._Ns
          .Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3.Ref4.int4)) insert List(
          (
            "a", 1, 2, List(
            (10, 11, List(
              (101, 102),
              (111, 112)))
          ))
        )

        _ <- Ns.str.Ref1.int1.Ref2.int2._Ref1._Ns
          .Refs1.*(Ref1.int1.Ref2.int2.Refs3.*(Ref3.int3.Ref4.int4)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1,
            |          "Ref2": {
            |            "int2": 2
            |          }
            |        },
            |        "Refs1": [
            |          {
            |            "int1": 10,
            |            "Ref2": {
            |              "int2": 11,
            |              "Refs3": [
            |                {
            |                  "int3": 101,
            |                  "Ref4": {
            |                    "int4": 102
            |                  }
            |                },
            |                {
            |                  "int3": 111,
            |                  "Ref4": {
            |                    "int4": 112
            |                  }
            |                }
            |              ]
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }
  }
}
