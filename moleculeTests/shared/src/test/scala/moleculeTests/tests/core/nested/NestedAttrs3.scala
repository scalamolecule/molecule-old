package moleculeTests.tests.core.nested

import molecule.datomic.api.out6._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object NestedAttrs3 extends AsyncTestSuite {

  lazy val tests = Tests {

    "strMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.strMap$) insert List(
          (1, List((11, Some(Map("a" -> "aa", "b" -> "bb"))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.strMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> "aa", "b" -> "bb"))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.strMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> "aa", "b" -> "bb"))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.strMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> "aa", "b" -> "bb")))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.strMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> "aa", "b" -> "bb")))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.strMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.strMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.strMap$).getJson.map(_ ==>
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
            |            "strMap$": {
            |              "a": "aa",
            |              "b": "bb"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "strMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.strMap$).getJson.map(_ ==>
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
            |            "strMap$": {
            |              "a": "aa",
            |              "b": "bb"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "strMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.strMap).getJson.map(_ ==>
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
            |            "strMap": {
            |              "a": "aa",
            |              "b": "bb"
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.strMap).getJson.map(_ ==>
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
            |            "strMap": {
            |              "b": "bb",
            |              "a": "aa"
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.strMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.strMap_).getJson.map(_ ==>
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


    "intMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.intMap$) insert List(
          (1, List((11, Some(Map("a" -> 111, "b" -> 112))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.intMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> 111, "b" -> 112))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.intMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> 111, "b" -> 112))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.intMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> 111, "b" -> 112)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.intMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> 111, "b" -> 112)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.intMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.intMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.intMap$).getJson.map(_ ==>
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
            |            "intMap$": {
            |              "a": 111,
            |              "b": 112
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "intMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.intMap$).getJson.map(_ ==>
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
            |            "intMap$": {
            |              "a": 111,
            |              "b": 112
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "intMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.intMap).getJson.map(_ ==>
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
            |            "intMap": {
            |              "a": 111,
            |              "b": 112
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.intMap).getJson.map(_ ==>
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
            |            "intMap": {
            |              "b": 112,
            |              "a": 111
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.intMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.intMap_).getJson.map(_ ==>
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


    "longMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.longMap$) insert List(
          (1, List((11, Some(Map("a" -> 111L, "b" -> 112L))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.longMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> 111L, "b" -> 112L))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.longMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> 111L, "b" -> 112L))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.longMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> 111L, "b" -> 112L)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.longMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> 111L, "b" -> 112L)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.longMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.longMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.longMap$).getJson.map(_ ==>
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
            |            "longMap$": {
            |              "a": 111,
            |              "b": 112
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "longMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.longMap$).getJson.map(_ ==>
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
            |            "longMap$": {
            |              "a": 111,
            |              "b": 112
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "longMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.longMap).getJson.map(_ ==>
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
            |            "longMap": {
            |              "a": 111,
            |              "b": 112
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.longMap).getJson.map(_ ==>
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
            |            "longMap": {
            |              "b": 112,
            |              "a": 111
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.longMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.longMap_).getJson.map(_ ==>
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


    "doubleMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.doubleMap$) insert List(
          (1, List((11, Some(Map("a" -> 1.1, "b" -> 1.2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.doubleMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> 1.1, "b" -> 1.2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.doubleMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> 1.1, "b" -> 1.2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.doubleMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> 1.1, "b" -> 1.2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.doubleMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> 1.1, "b" -> 1.2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.doubleMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.doubleMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.doubleMap$).getJson.map(_ ==>
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
            |            "doubleMap$": {
            |              "a": 1.1,
            |              "b": 1.2
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "doubleMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.doubleMap$).getJson.map(_ ==>
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
            |            "doubleMap$": {
            |              "a": 1.1,
            |              "b": 1.2
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "doubleMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.doubleMap).getJson.map(_ ==>
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
            |            "doubleMap": {
            |              "a": 1.1,
            |              "b": 1.2
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.doubleMap).getJson.map(_ ==>
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
            |            "doubleMap": {
            |              "b": 1.2,
            |              "a": 1.1
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.doubleMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.doubleMap_).getJson.map(_ ==>
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


    "boolMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.boolMap$) insert List(
          (1, List((11, Some(Map("a" -> true, "b" -> false))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.boolMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> true, "b" -> false))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.boolMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> true, "b" -> false))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.boolMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> true, "b" -> false)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.boolMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> true, "b" -> false)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.boolMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.boolMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.boolMap$).getJson.map(_ ==>
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
            |            "boolMap$": {
            |              "a": true,
            |              "b": false
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "boolMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.boolMap$).getJson.map(_ ==>
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
            |            "boolMap$": {
            |              "a": true,
            |              "b": false
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "boolMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.boolMap).getJson.map(_ ==>
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
            |            "boolMap": {
            |              "a": true,
            |              "b": false
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.boolMap).getJson.map(_ ==>
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
            |            "boolMap": {
            |              "b": false,
            |              "a": true
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.boolMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.boolMap_).getJson.map(_ ==>
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


    "dateMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.dateMap$) insert List(
          (1, List((11, Some(Map("a" -> date1, "b" -> date2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.dateMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> date1, "b" -> date2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.dateMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> date1, "b" -> date2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.dateMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> date1, "b" -> date2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.dateMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> date1, "b" -> date2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.dateMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.dateMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.dateMap$).getJson.map(_ ==>
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
            |            "dateMap$": {
            |              "a": "2001-07-01",
            |              "b": "2002-01-01"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "dateMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.dateMap$).getJson.map(_ ==>
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
            |            "dateMap$": {
            |              "a": "2001-07-01",
            |              "b": "2002-01-01"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "dateMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.dateMap).getJson.map(_ ==>
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
            |            "dateMap": {
            |              "a": "2001-07-01",
            |              "b": "2002-01-01"
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.dateMap).getJson.map(_ ==>
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
            |            "dateMap": {
            |              "a": "2001-07-01",
            |              "b": "2002-01-01"
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.dateMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.dateMap_).getJson.map(_ ==>
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


    "uuidMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.uuidMap$) insert List(
          (1, List((11, Some(Map("a" -> uuid1))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uuidMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> uuid1))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uuidMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> uuid1))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uuidMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> uuid1)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uuidMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> uuid1)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uuidMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uuidMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uuidMap$).getJson.map(_ ==>
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
             |            "uuidMap$$": {
             |              "a": "$uuid1"
             |            }
             |          },
             |          {
             |            "int": 12,
             |            "uuidMap$$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.uuidMap$).getJson.map(_ ==>
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
             |            "uuidMap$$": {
             |              "a": "$uuid1"
             |            }
             |          },
             |          {
             |            "int": 12,
             |            "uuidMap$$": null
             |          }
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uuidMap).getJson.map(_ ==>
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
             |            "uuidMap": {
             |              "a": "$uuid1"
             |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.uuidMap).getJson.map(_ ==>
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
             |            "uuidMap": {
             |              "a": "$uuid1"
             |            }
             |          }
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uuidMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.uuidMap_).getJson.map(_ ==>
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


    "uriMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.uriMap$) insert List(
          (1, List((11, Some(Map("a" -> uri1, "b" -> uri2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uriMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> uri1, "b" -> uri2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uriMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> uri1, "b" -> uri2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uriMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> uri1, "b" -> uri2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uriMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> uri1, "b" -> uri2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uriMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uriMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uriMap$).getJson.map(_ ==>
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
            |            "uriMap$": {
            |              "a": "uri1",
            |              "b": "uri2"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "uriMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.uriMap$).getJson.map(_ ==>
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
            |            "uriMap$": {
            |              "a": "uri1",
            |              "b": "uri2"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "uriMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uriMap).getJson.map(_ ==>
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
            |            "uriMap": {
            |              "a": "uri1",
            |              "b": "uri2"
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.uriMap).getJson.map(_ ==>
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
            |            "uriMap": {
            |              "b": "uri2",
            |              "a": "uri1"
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.uriMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.uriMap_).getJson.map(_ ==>
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


    "bigIntMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.bigIntMap$) insert List(
          (1, List((11, Some(Map("a" -> bigInt1, "b" -> bigInt2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigIntMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> bigInt1, "b" -> bigInt2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigIntMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> bigInt1, "b" -> bigInt2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigIntMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> bigInt1, "b" -> bigInt2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigIntMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> bigInt1, "b" -> bigInt2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigIntMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigIntMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigIntMap$).getJson.map(_ ==>
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
            |            "bigIntMap$": {
            |              "a": "1",
            |              "b": "2"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "bigIntMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.bigIntMap$).getJson.map(_ ==>
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
            |            "bigIntMap$": {
            |              "a": "1",
            |              "b": "2"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "bigIntMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigIntMap).getJson.map(_ ==>
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
            |            "bigIntMap": {
            |              "a": "1",
            |              "b": "2"
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.bigIntMap).getJson.map(_ ==>
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
             |            "bigIntMap": {
             |              "b": "2",
             |              "a": "1"
             |            }
             |          }
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigIntMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.bigIntMap_).getJson.map(_ ==>
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


    "bigDecMap" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecMap$) insert List(
          (1, List((11, Some(Map("a" -> bigDec1, "b" -> bigDec2))), (12, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> bigDec1, "b" -> bigDec2))), (12, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecMap$).get.map(_ ==> List(
          (1, List((11, Some(Map("a" -> bigDec1, "b" -> bigDec2))), (12, None))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> bigDec1, "b" -> bigDec2)))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecMap).get.map(_ ==> List(
          (1, List((11, Map("a" -> bigDec1, "b" -> bigDec2)))),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecMap_).get.map(_ ==> List(
          (1, List(11)),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecMap_).get.map(_ ==> List(
          (1, List(11)),
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecMap$).getJson.map(_ ==>
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
            |            "bigDecMap$": {
            |              "a": "1.0",
            |              "b": "2.0"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "bigDecMap$": null
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
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecMap$).getJson.map(_ ==>
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
            |            "bigDecMap$": {
            |              "a": "1.0",
            |              "b": "2.0"
            |            }
            |          },
            |          {
            |            "int": 12,
            |            "bigDecMap$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecMap).getJson.map(_ ==>
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
            |            "bigDecMap": {
            |              "a": "1.0",
            |              "b": "2.0"
            |            }
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
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecMap).getJson.map(_ ==>
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
            |            "bigDecMap": {
            |              "b": "2.0",
            |              "a": "1.0"
            |            }
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDecMap_).getJson.map(_ ==>
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
        _ <- m(Ref1.int1.Nss * Ns.int.bigDecMap_).getJson.map(_ ==>
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