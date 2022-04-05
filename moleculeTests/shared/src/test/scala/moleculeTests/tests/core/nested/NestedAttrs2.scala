package moleculeTests.tests.core.nested

import molecule.datomic.api.out6._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object NestedAttrs2 extends AsyncTestSuite {

  lazy val tests = Tests {

    "strs" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$) insert List(
          (1, List((11, Some(Set("a", "b"))), (12, None))),
          (2, List())
        )

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.strs1$).get.map(_ ==> List(
          (1, List((11, Some(Set("a", "b"))), (12, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$).get.map(_ ==> List(
          (1, List((11, Some(Set("a", "b"))), (12, None))),
        ))

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.strs1).get.map(_ ==> List(
          (1, List((11, Set("a", "b")))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1).get.map(_ ==> List(
          (1, List((11, Set("a", "b")))),
        ))

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.strs1_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "strs1$": [
            |              "a",
            |              "b"
            |            ]
            |          },
            |          {
            |            "int1": 12,
            |            "strs1$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "strs1$": [
            |              "a",
            |              "b"
            |            ]
            |          },
            |          {
            |            "int1": 12,
            |            "strs1$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "strs1": [
            |              "a",
            |              "b"
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "strs1": [
            |              "a",
            |              "b"
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "enums" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1$) insert List(
          (1, List((11, Some(Set("enum10", "enum11"))), (12, None))),
          (2, List())
        )

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.enums1$).get.map(_ ==> List(
          (1, List((11, Some(Set("enum10", "enum11"))), (12, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1$).get.map(_ ==> List(
          (1, List((11, Some(Set("enum10", "enum11"))), (12, None)))
        ))

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.enums1).get.map(_ ==> List(
          (1, List((11, Set("enum10", "enum11")))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1).get.map(_ ==> List(
          (1, List((11, Set("enum10", "enum11"))))
        ))

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.enums1_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1_).get.map(_ ==> List(
          (1, List(11))
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "enums1$": [
            |              "enum10",
            |              "enum11"
            |            ]
            |          },
            |          {
            |            "int1": 12,
            |            "enums1$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "enums1$": [
            |              "enum10",
            |              "enum11"
            |            ]
            |          },
            |          {
            |            "int1": 12,
            |            "enums1$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "enums1": [
            |              "enum10",
            |              "enum11"
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "enums1": [
            |              "enum10",
            |              "enum11"
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "refs" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2$) insert List(
          (1, List((11, Some(Set(111L, 112L))), (12, None))),
          (2, List())
        )

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.refs2$).get.map(_ ==> List(
          (1, List((11, Some(Set(111L, 112L))), (12, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2$).get.map(_ ==> List(
          (1, List((11, Some(Set(111L, 112L))), (12, None)))
        ))

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.refs2).get.map(_ ==> List(
          (1, List((11, Set(111L, 112L)))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2).get.map(_ ==> List(
          (1, List((11, Set(111L, 112L))))
        ))

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.refs2_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2_).get.map(_ ==> List(
          (1, List(11))
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "refs2$": [
            |              111,
            |              112
            |            ]
            |          },
            |          {
            |            "int1": 12,
            |            "refs2$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "refs2$": [
            |              111,
            |              112
            |            ]
            |          },
            |          {
            |            "int1": 12,
            |            "refs2$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "refs2": [
            |              111,
            |              112
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "refs2": [
            |              111,
            |              112
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "ints" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.ints1$) insert List(
          (1, List((11, Some(Set(111, 112))), (12, None))),
          (2, List())
        )

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.ints1$).get.map(_ ==> List(
          (1, List((11, Some(Set(111, 112))), (12, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.ints1$).get.map(_ ==> List(
          (1, List((11, Some(Set(111, 112))), (12, None))),
        ))

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.ints1).get.map(_ ==> List(
          (1, List((11, Set(111, 112)))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.ints1).get.map(_ ==> List(
          (1, List((11, Set(111, 112)))),
        ))

        _ <- m(Ns.int.a1.Refs1 *? Ref1.int1.ints1_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.ints1_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.ints1$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "ints1$": [
            |              111,
            |              112
            |            ]
            |          },
            |          {
            |            "int1": 12,
            |            "ints1$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.ints1$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "ints1$": [
            |              111,
            |              112
            |            ]
            |          },
            |          {
            |            "int1": 12,
            |            "ints1$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ns.int.Refs1 *? Ref1.int1.ints1).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "ints1": [
            |              111,
            |              112
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.ints1).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11,
            |            "ints1": [
            |              111,
            |              112
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ns.int.Refs1 *? Ref1.int1.ints1_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int": 2,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.ints1_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "longs" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.longs$) insert List(
          (1, List((11, Some(Set(111L, 112L))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.longs$).get.map(_ ==> List(
          (1, List((11, Some(Set(111L, 112L))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.longs$).get.map(_ ==> List(
          (1, List((11, Some(Set(111L, 112L))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.longs).get.map(_ ==> List(
          (1, List((11, Set(111L, 112L)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.longs).get.map(_ ==> List(
          (1, List((11, Set(111L, 112L)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.longs_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.longs_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.longs$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "longs$": [
            |              111,
            |              112
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "longs$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.longs$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "longs$": [
            |              111,
            |              112
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "longs$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.longs).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "longs": [
            |              111,
            |              112
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.longs).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "longs": [
            |              111,
            |              112
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.longs_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.longs_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "doubles" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.doubles$) insert List(
          (1, List((11, Some(Set(1.1, 1.2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.doubles$).get.map(_ ==> List(
          (1, List((11, Some(Set(1.1, 1.2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.doubles$).get.map(_ ==> List(
          (1, List((11, Some(Set(1.1, 1.2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.doubles).get.map(_ ==> List(
          (1, List((11, Set(1.1, 1.2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.doubles).get.map(_ ==> List(
          (1, List((11, Set(1.1, 1.2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.doubles_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.doubles_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.doubles$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "doubles$": [
            |              1.1,
            |              1.2
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "doubles$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.doubles$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "doubles$": [
            |              1.1,
            |              1.2
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "doubles$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.doubles).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "doubles": [
            |              1.1,
            |              1.2
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.doubles).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "doubles": [
            |              1.2,
            |              1.1
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.doubles_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.doubles_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "bools" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.bools$) insert List(
          (1, List((11, Some(Set(true, false))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bools$).get.map(_ ==> List(
          (1, List((11, Some(Set(true, false))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bools$).get.map(_ ==> List(
          (1, List((11, Some(Set(true, false))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bools).get.map(_ ==> List(
          (1, List((11, Set(true, false)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bools).get.map(_ ==> List(
          (1, List((11, Set(true, false)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bools_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bools_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bools$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bools$": [
            |              false,
            |              true
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "bools$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bools$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bools$": [
            |              false,
            |              true
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "bools$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bools).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bools": [
            |              false,
            |              true
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bools).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bools": [
            |              true,
            |              false
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bools_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bools_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "dates" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.dates$) insert List(
          (1, List((11, Some(Set(date1, date2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.dates$).get.map(_ ==> List(
          (1, List((11, Some(Set(date1, date2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.dates$).get.map(_ ==> List(
          (1, List((11, Some(Set(date1, date2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.dates).get.map(_ ==> List(
          (1, List((11, Set(date1, date2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.dates).get.map(_ ==> List(
          (1, List((11, Set(date1, date2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.dates_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.dates_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.dates$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "dates$": [
            |              "2001-07-01",
            |              "2002-01-01"
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "dates$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.dates$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "dates$": [
            |              "2001-07-01",
            |              "2002-01-01"
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "dates$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.dates).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "dates": [
            |              "2001-07-01",
            |              "2002-01-01"
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.dates).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "dates": [
            |              "2001-07-01",
            |              "2002-01-01"
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.dates_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.dates_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "uuids" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.uuids$) insert List(
          (1, List((11, Some(Set(uuid1))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uuids$).get.map(_ ==> List(
          (1, List((11, Some(Set(uuid1))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uuids$).get.map(_ ==> List(
          (1, List((11, Some(Set(uuid1))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uuids).get.map(_ ==> List(
          (1, List((11, Set(uuid1)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uuids).get.map(_ ==> List(
          (1, List((11, Set(uuid1)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uuids_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uuids_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uuids$).getJson.map(_ ==>
          s"""{
             |  "totalCount": 2,
             |  "limit"     : 2,
             |  "offset"    : 0,
             |  "data": {
             |    "Ref1": [
             |      {
             |        "int1": 1,
             |        "Nss": [
             |          {
             |            "int": 11,
             |            "uuids$$": [
             |              "$uuid1"
             |            ]
             |          },
             |          {
             |            "int": 12,
             |            "uuids$$": null
             |          }
             |        ]
             |      },
             |      {
             |        "int1": 2,
             |        "Nss": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.uuids$).getJson.map(_ ==>
          s"""{
             |  "totalCount": 1,
             |  "limit"     : 1,
             |  "offset"    : 0,
             |  "data": {
             |    "Ref1": [
             |      {
             |        "int1": 1,
             |        "Nss": [
             |          {
             |            "int": 11,
             |            "uuids$$": [
             |              "$uuid1"
             |            ]
             |          },
             |          {
             |            "int": 12,
             |            "uuids$$": null
             |          }
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uuids).getJson.map(_ ==>
          s"""{
             |  "totalCount": 2,
             |  "limit"     : 2,
             |  "offset"    : 0,
             |  "data": {
             |    "Ref1": [
             |      {
             |        "int1": 1,
             |        "Nss": [
             |          {
             |            "int": 11,
             |            "uuids": [
             |              "$uuid1"
             |            ]
             |          }
             |        ]
             |      },
             |      {
             |        "int1": 2,
             |        "Nss": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.uuids).getJson.map(_ ==>
          s"""{
             |  "totalCount": 1,
             |  "limit"     : 1,
             |  "offset"    : 0,
             |  "data": {
             |    "Ref1": [
             |      {
             |        "int1": 1,
             |        "Nss": [
             |          {
             |            "int": 11,
             |            "uuids": [
             |              "$uuid1"
             |            ]
             |          }
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uuids_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.uuids_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "uris" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.uris$) insert List(
          (1, List((11, Some(Set(uri1, uri2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uris$).get.map(_ ==> List(
          (1, List((11, Some(Set(uri1, uri2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uris$).get.map(_ ==> List(
          (1, List((11, Some(Set(uri1, uri2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uris).get.map(_ ==> List(
          (1, List((11, Set(uri1, uri2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uris).get.map(_ ==> List(
          (1, List((11, Set(uri1, uri2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uris_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uris_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uris$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "uris$": [
            |              "uri1",
            |              "uri2"
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "uris$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.uris$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "uris$": [
            |              "uri1",
            |              "uri2"
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "uris$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uris).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "uris": [
            |              "uri1",
            |              "uri2"
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.uris).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "uris": [
            |              "uri1",
            |              "uri2"
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uris_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.uris_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "bigInts" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.bigInts$) insert List(
          (1, List((11, Some(Set(bigInt1, bigInt2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigInts$).get.map(_ ==> List(
          (1, List((11, Some(Set(bigInt1, bigInt2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigInts$).get.map(_ ==> List(
          (1, List((11, Some(Set(bigInt1, bigInt2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigInts).get.map(_ ==> List(
          (1, List((11, Set(bigInt1, bigInt2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigInts).get.map(_ ==> List(
          (1, List((11, Set(bigInt1, bigInt2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigInts_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigInts_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigInts$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bigInts$": [
            |              "1",
            |              "2"
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "bigInts$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bigInts$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bigInts$": [
            |              "1",
            |              "2"
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "bigInts$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigInts).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bigInts": [
            |              "1",
            |              "2"
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bigInts).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bigInts": [
            |              "1",
            |              "2"
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigInts_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bigInts_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "bigDecs" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecs$) insert List(
          (1, List((11, Some(Set(bigDec1, bigDec2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecs$).get.map(_ ==> List(
          (1, List((11, Some(Set(bigDec1, bigDec2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecs$).get.map(_ ==> List(
          (1, List((11, Some(Set(bigDec1, bigDec2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecs).get.map(_ ==> List(
          (1, List((11, Set(bigDec1, bigDec2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecs).get.map(_ ==> List(
          (1, List((11, Set(bigDec1, bigDec2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecs_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecs_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecs$).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bigDecs$": [
            |              "1.0",
            |              "2.0"
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "bigDecs$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecs$).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bigDecs$": [
            |              "1.0",
            |              "2.0"
            |            ]
            |          },
            |          {
            |            "int": 12,
            |            "bigDecs$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecs).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bigDecs": [
            |              "1.0",
            |              "2.0"
            |            ]
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecs).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11,
            |            "bigDecs": [
            |              "2.0",
            |              "1.0"
            |            ]
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecs_).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "int1": 2,
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecs_).getJson.map(_ ==>
          """{
            |  "totalCount": 1,
            |  "limit"     : 1,
            |  "offset"    : 0,
            |  "data": {
            |    "Ref1": [
            |      {
            |        "int1": 1,
            |        "Nss": [
            |          {
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }
  }
}