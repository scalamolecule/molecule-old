package moleculeTests.tests.core.nested

import molecule.core.util.Executor._
import molecule.datomic.api.out6._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object OptNestedAttrsLevel0 extends AsyncTestSuite {

  lazy val tests = Tests {

    "str" - core { implicit conn =>
      for {
        _ <- Ns.int.str.Refs1.*?(Ref1.int1) insert List((1, "a", Nil))
        _ <- Ns.int.strs.Refs1.*?(Ref1.int1) insert List((2, Set("a"), Nil))
        _ <- Ns.int.strMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> "aa"), Nil))
        _ <- Ns.int.str$.Refs1.*?(Ref1.int1) insert List((4, Some("a"), Nil), (5, None, Nil))
        _ <- Ns.int.strs$.Refs1.*?(Ref1.int1) insert List((6, Some(Set("a")), Nil), (7, None, Nil))
        _ <- Ns.int.strMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> "aa")), Nil), (9, None, Nil))

        _ <- Ns.int(1).str.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, "a", Nil)))
        _ <- Ns.int(2).strs.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set("a"), Nil)))
        _ <- Ns.int(3).strMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> "aa"), Nil)))
        _ <- Ns.int(4, 5).str$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some("a"), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).strs$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set("a")), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).strMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> "aa")), Nil), (9, None, Nil)))

        _ <- Ns.int(1).str.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "str": "a",
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).strs.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "strs": [
            |          "a"
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(3).strMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "strMap": {
            |          "a": "aa"
            |        },
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(4, 5).str$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "str$": "a",
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "str$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).strs$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "strs$": [
            |          "a"
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "strs$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(8, 9).strMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 8,
            |        "strMap$": {
            |          "a": "aa"
            |        },
            |        "Refs1": []
            |      },
            |      {
            |        "int": 9,
            |        "strMap$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "enum" - core { implicit conn =>
      for {
        _ <- Ns.int.enumm.Refs1.*?(Ref1.int1) insert List((1, "enum1", Nil))
        _ <- Ns.int.enums.Refs1.*?(Ref1.int1) insert List((2, Set("enum1"), Nil))
        _ <- Ns.int.enumm$.Refs1.*?(Ref1.int1) insert List((4, Some("enum1"), Nil), (5, None, Nil))
        _ <- Ns.int.enums$.Refs1.*?(Ref1.int1) insert List((6, Some(Set("enum1")), Nil), (7, None, Nil))

        _ <- Ns.int(1).enumm.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, "enum1", Nil)))
        _ <- Ns.int(2).enums.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set("enum1"), Nil)))
        _ <- Ns.int(4, 5).enumm$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some("enum1"), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).enums$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set("enum1")), Nil), (7, None, Nil)))

        _ <- Ns.int(1).enumm.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "enumm": "enum1",
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).enums.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "enums": [
            |          "enum1"
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- Ns.int(4, 5).enumm$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "enumm$": "enum1",
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "enumm$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).enums$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "enums$": [
            |          "enum1"
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "enums$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "int" - core { implicit conn =>
      for {
        _ <- Ns.long.int.Refs1.*?(Ref1.int1) insert List((1, 11, Nil))
        _ <- Ns.long.ints.Refs1.*?(Ref1.int1) insert List((2, Set(11), Nil))
        _ <- Ns.long.intMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> 11), Nil))
        _ <- Ns.long.int$.Refs1.*?(Ref1.int1) insert List((4, Some(11), Nil), (5, None, Nil))
        _ <- Ns.long.ints$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(11)), Nil), (7, None, Nil))
        _ <- Ns.long.intMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> 11)), Nil), (9, None, Nil))

        _ <- Ns.long(1).int.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, 11, Nil)))
        _ <- Ns.long(2).ints.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(11), Nil)))
        _ <- Ns.long(3).intMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> 11), Nil)))
        _ <- Ns.long(4, 5).int$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(11), Nil), (5, None, Nil)))
        _ <- Ns.long(6, 7).ints$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(11)), Nil), (7, None, Nil)))
        _ <- Ns.long(8, 9).intMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> 11)), Nil), (9, None, Nil)))

        _ <- Ns.long(1).int.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 1,
            |        "int": 11,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.long(2).ints.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 2,
            |        "ints": [
            |          11
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.long(3).intMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 3,
            |        "intMap": {
            |          "a": 11
            |        },
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.long(4, 5).int$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 4,
            |        "int$": 11,
            |        "Refs1": []
            |      },
            |      {
            |        "long": 5,
            |        "int$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.long(6, 7).ints$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 6,
            |        "ints$": [
            |          11
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "long": 7,
            |        "ints$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.long(8, 9).intMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 8,
            |        "intMap$": {
            |          "a": 11
            |        },
            |        "Refs1": []
            |      },
            |      {
            |        "long": 9,
            |        "intMap$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "long" - core { implicit conn =>
      for {
        _ <- Ns.int.long.Refs1.*?(Ref1.int1) insert List((1, 11L, Nil))
        _ <- Ns.int.longs.Refs1.*?(Ref1.int1) insert List((2, Set(11L), Nil))
        _ <- Ns.int.longMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> 11L), Nil))
        _ <- Ns.int.long$.Refs1.*?(Ref1.int1) insert List((4, Some(11L), Nil), (5, None, Nil))
        _ <- Ns.int.longs$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(11L)), Nil), (7, None, Nil))
        _ <- Ns.int.longMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> 11L)), Nil), (9, None, Nil))

        _ <- Ns.int(1).long.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, 11L, Nil)))
        _ <- Ns.int(2).longs.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(11L), Nil)))
        _ <- Ns.int(3).longMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> 11L), Nil)))
        _ <- Ns.int(4, 5).long$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(11L), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).longs$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(11L)), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).longMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> 11L)), Nil), (9, None, Nil)))

        _ <- Ns.int(1).long.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "long": 11,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).longs.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "longs": [
            |          11
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(3).longMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "longMap": {
            |          "a": 11
            |        },
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(4, 5).long$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "long$": 11,
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "long$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).longs$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "longs$": [
            |          11
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "longs$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(8, 9).longMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 8,
            |        "longMap$": {
            |          "a": 11
            |        },
            |        "Refs1": []
            |      },
            |      {
            |        "int": 9,
            |        "longMap$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }

    "ref" - core { implicit conn =>
      for {
        _ <- Ns.int.ref1.Refs1.*?(Ref1.int1) insert List((1, 11L, Nil))
        _ <- Ns.int.refs1.Refs1.*?(Ref1.int1) insert List((2, Set(11L), Nil))
        _ <- Ns.int.ref1$.Refs1.*?(Ref1.int1) insert List((4, Some(11L), Nil), (5, None, Nil))
        _ <- Ns.int.refs1$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(11L)), Nil), (7, None, Nil))

        _ <- Ns.int(1).ref1.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, 11L, Nil)))
        _ <- Ns.int(2).refs1.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(11L), Nil)))
        _ <- Ns.int(4, 5).ref1$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(11L), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).refs1$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(11L)), Nil), (7, None, Nil)))

        _ <- Ns.int(1).ref1.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "ref1": 11,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).refs1.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "refs1": [
            |          11
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(4, 5).ref1$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "ref1$": 11,
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "ref1$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).refs1$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "refs1$": [
            |          11
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "refs1$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "double" - core { implicit conn =>
      for {
        _ <- Ns.int.double.Refs1.*?(Ref1.int1) insert List((1, 1.1, Nil))
        _ <- Ns.int.doubles.Refs1.*?(Ref1.int1) insert List((2, Set(1.1), Nil))
        _ <- Ns.int.doubleMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> 1.1), Nil))
        _ <- Ns.int.double$.Refs1.*?(Ref1.int1) insert List((4, Some(1.1), Nil), (5, None, Nil))
        _ <- Ns.int.doubles$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(1.1)), Nil), (7, None, Nil))
        _ <- Ns.int.doubleMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> 1.1)), Nil), (9, None, Nil))

        _ <- Ns.int(1).double.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, 1.1, Nil)))
        _ <- Ns.int(2).doubles.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(1.1), Nil)))
        _ <- Ns.int(3).doubleMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> 1.1), Nil)))
        _ <- Ns.int(4, 5).double$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(1.1), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).doubles$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(1.1)), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).doubleMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> 1.1)), Nil), (9, None, Nil)))

        _ <- Ns.int(1).double.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "double": 1.1,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).doubles.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "doubles": [
            |          1.1
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(3).doubleMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "doubleMap": {
            |          "a": 1.1
            |        },
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(4, 5).double$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "double$": 1.1,
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "double$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).doubles$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "doubles$": [
            |          1.1
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "doubles$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(8, 9).doubleMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 8,
            |        "doubleMap$": {
            |          "a": 1.1
            |        },
            |        "Refs1": []
            |      },
            |      {
            |        "int": 9,
            |        "doubleMap$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "bool" - core { implicit conn =>
      for {
        _ <- Ns.int.bool.Refs1.*?(Ref1.int1) insert List((1, true, Nil))
        _ <- Ns.int.bools.Refs1.*?(Ref1.int1) insert List((2, Set(true), Nil))
        _ <- Ns.int.boolMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> true), Nil))
        _ <- Ns.int.bool$.Refs1.*?(Ref1.int1) insert List((4, Some(true), Nil), (5, None, Nil))
        _ <- Ns.int.bools$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(true)), Nil), (7, None, Nil))
        _ <- Ns.int.boolMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> true)), Nil), (9, None, Nil))

        _ <- Ns.int(1).bool.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, true, Nil)))
        _ <- Ns.int(2).bools.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(true), Nil)))
        _ <- Ns.int(3).boolMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> true), Nil)))
        _ <- Ns.int(4, 5).bool$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(true), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).bools$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(true)), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).boolMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> true)), Nil), (9, None, Nil)))

        _ <- Ns.int(1).bool.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "bool": true,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).bools.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "bools": [
            |          true
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(3).boolMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "boolMap": {
            |          "a": true
            |        },
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(4, 5).bool$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "bool$": true,
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "bool$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).bools$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "bools$": [
            |          true
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "bools$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(8, 9).boolMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 8,
            |        "boolMap$": {
            |          "a": true
            |        },
            |        "Refs1": []
            |      },
            |      {
            |        "int": 9,
            |        "boolMap$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "date" - core { implicit conn =>
      for {
        _ <- Ns.int.date.Refs1.*?(Ref1.int1) insert List((1, date1, Nil))
        _ <- Ns.int.dates.Refs1.*?(Ref1.int1) insert List((2, Set(date1), Nil))
        _ <- Ns.int.dateMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> date1), Nil))
        _ <- Ns.int.date$.Refs1.*?(Ref1.int1) insert List((4, Some(date1), Nil), (5, None, Nil))
        _ <- Ns.int.dates$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(date1)), Nil), (7, None, Nil))
        _ <- Ns.int.dateMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> date1)), Nil), (9, None, Nil))

        _ <- Ns.int(1).date.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, date1, Nil)))
        _ <- Ns.int(2).dates.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(date1), Nil)))
        _ <- Ns.int(3).dateMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> date1), Nil)))
        _ <- Ns.int(4, 5).date$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(date1), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).dates$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(date1)), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).dateMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> date1)), Nil), (9, None, Nil)))

        _ <- Ns.int(1).date.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "date": "2001-07-01",
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).dates.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "dates": [
            |          "2001-07-01"
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(3).dateMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "dateMap": {
            |          "a": "2001-07-01"
            |        },
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(4, 5).date$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "date$": "2001-07-01",
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "date$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).dates$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "dates$": [
            |          "2001-07-01"
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "dates$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(8, 9).dateMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 8,
            |        "dateMap$": {
            |          "a": "2001-07-01"
            |        },
            |        "Refs1": []
            |      },
            |      {
            |        "int": 9,
            |        "dateMap$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "uuid" - core { implicit conn =>
      for {
        _ <- Ns.int.uuid.Refs1.*?(Ref1.int1) insert List((1, uuid1, Nil))
        _ <- Ns.int.uuids.Refs1.*?(Ref1.int1) insert List((2, Set(uuid1), Nil))
        _ <- Ns.int.uuidMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> uuid1), Nil))
        _ <- Ns.int.uuid$.Refs1.*?(Ref1.int1) insert List((4, Some(uuid1), Nil), (5, None, Nil))
        _ <- Ns.int.uuids$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(uuid1)), Nil), (7, None, Nil))
        _ <- Ns.int.uuidMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> uuid1)), Nil), (9, None, Nil))

        _ <- Ns.int(1).uuid.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, uuid1, Nil)))
        _ <- Ns.int(2).uuids.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(uuid1), Nil)))
        _ <- Ns.int(3).uuidMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> uuid1), Nil)))
        _ <- Ns.int(4, 5).uuid$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(uuid1), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).uuids$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(uuid1)), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).uuidMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> uuid1)), Nil), (9, None, Nil)))

        _ <- Ns.int(1).uuid.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uuid": "$uuid1",
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(2).uuids.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "uuids": [
             |          "$uuid1"
             |        ],
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(3).uuidMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 3,
             |        "uuidMap": {
             |          "a": "$uuid1"
             |        },
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(4, 5).uuid$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 4,
             |        "uuid$$": "$uuid1",
             |        "Refs1": []
             |      },
             |      {
             |        "int": 5,
             |        "uuid$$": null,
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(6, 7).uuids$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 6,
             |        "uuids$$": [
             |          "$uuid1"
             |        ],
             |        "Refs1": []
             |      },
             |      {
             |        "int": 7,
             |        "uuids$$": null,
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(8, 9).uuidMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 8,
             |        "uuidMap$$": {
             |          "a": "$uuid1"
             |        },
             |        "Refs1": []
             |      },
             |      {
             |        "int": 9,
             |        "uuidMap$$": null,
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
      } yield ()
    }


    "uri" - core { implicit conn =>
      for {
        _ <- Ns.int.uri.Refs1.*?(Ref1.int1) insert List((1, uri1, Nil))
        _ <- Ns.int.uris.Refs1.*?(Ref1.int1) insert List((2, Set(uri1), Nil))
        _ <- Ns.int.uriMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> uri1), Nil))
        _ <- Ns.int.uri$.Refs1.*?(Ref1.int1) insert List((4, Some(uri1), Nil), (5, None, Nil))
        _ <- Ns.int.uris$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(uri1)), Nil), (7, None, Nil))
        _ <- Ns.int.uriMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> uri1)), Nil), (9, None, Nil))

        _ <- Ns.int(1).uri.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, uri1, Nil)))
        _ <- Ns.int(2).uris.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(uri1), Nil)))
        _ <- Ns.int(3).uriMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> uri1), Nil)))
        _ <- Ns.int(4, 5).uri$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(uri1), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).uris$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(uri1)), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).uriMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> uri1)), Nil), (9, None, Nil)))

        _ <- Ns.int(1).uri.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uri": "$uri1",
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(2).uris.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "uris": [
             |          "$uri1"
             |        ],
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(3).uriMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 3,
             |        "uriMap": {
             |          "a": "$uri1"
             |        },
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(4, 5).uri$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 4,
             |        "uri$$": "$uri1",
             |        "Refs1": []
             |      },
             |      {
             |        "int": 5,
             |        "uri$$": null,
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(6, 7).uris$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 6,
             |        "uris$$": [
             |          "$uri1"
             |        ],
             |        "Refs1": []
             |      },
             |      {
             |        "int": 7,
             |        "uris$$": null,
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
        _ <- Ns.int(8, 9).uriMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 8,
             |        "uriMap$$": {
             |          "a": "$uri1"
             |        },
             |        "Refs1": []
             |      },
             |      {
             |        "int": 9,
             |        "uriMap$$": null,
             |        "Refs1": []
             |      }
             |    ]
             |  }
             |}""".stripMargin
        )
      } yield ()
    }


    "bigInt" - core { implicit conn =>
      for {
        _ <- Ns.int.bigInt.Refs1.*?(Ref1.int1) insert List((1, bigInt1, Nil))
        _ <- Ns.int.bigInts.Refs1.*?(Ref1.int1) insert List((2, Set(bigInt1), Nil))
        _ <- Ns.int.bigIntMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> bigInt1), Nil))
        _ <- Ns.int.bigInt$.Refs1.*?(Ref1.int1) insert List((4, Some(bigInt1), Nil), (5, None, Nil))
        _ <- Ns.int.bigInts$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(bigInt1)), Nil), (7, None, Nil))
        _ <- Ns.int.bigIntMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> bigInt1)), Nil), (9, None, Nil))

        _ <- Ns.int(1).bigInt.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, bigInt1, Nil)))
        _ <- Ns.int(2).bigInts.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(bigInt1), Nil)))
        _ <- Ns.int(3).bigIntMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> bigInt1), Nil)))
        _ <- Ns.int(4, 5).bigInt$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(bigInt1), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).bigInts$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(bigInt1)), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).bigIntMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> bigInt1)), Nil), (9, None, Nil)))

        _ <- Ns.int(1).bigInt.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "bigInt": "1",
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).bigInts.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "bigInts": [
            |          "1"
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(3).bigIntMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "bigIntMap": {
            |          "a": "1"
            |        },
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(4, 5).bigInt$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "bigInt$": "1",
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "bigInt$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).bigInts$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "bigInts$": [
            |          "1"
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "bigInts$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(8, 9).bigIntMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 8,
            |        "bigIntMap$": {
            |          "a": "1"
            |        },
            |        "Refs1": []
            |      },
            |      {
            |        "int": 9,
            |        "bigIntMap$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }


    "bigDec" - core { implicit conn =>
      for {
        _ <- Ns.int.bigDec.Refs1.*?(Ref1.int1) insert List((1, bigDec1, Nil))
        _ <- Ns.int.bigDecs.Refs1.*?(Ref1.int1) insert List((2, Set(bigDec1), Nil))
        _ <- Ns.int.bigDecMap.Refs1.*?(Ref1.int1) insert List((3, Map("a" -> bigDec1), Nil))
        _ <- Ns.int.bigDec$.Refs1.*?(Ref1.int1) insert List((4, Some(bigDec1), Nil), (5, None, Nil))
        _ <- Ns.int.bigDecs$.Refs1.*?(Ref1.int1) insert List((6, Some(Set(bigDec1)), Nil), (7, None, Nil))
        _ <- Ns.int.bigDecMap$.Refs1.*?(Ref1.int1) insert List((8, Some(Map("a" -> bigDec1)), Nil), (9, None, Nil))

        _ <- Ns.int(1).bigDec.Refs1.*?(Ref1.int1).get.map(_ ==> List((1, bigDec1, Nil)))
        _ <- Ns.int(2).bigDecs.Refs1.*?(Ref1.int1).get.map(_ ==> List((2, Set(bigDec1), Nil)))
        _ <- Ns.int(3).bigDecMap.Refs1.*?(Ref1.int1).get.map(_ ==> List((3, Map("a" -> bigDec1), Nil)))
        _ <- Ns.int(4, 5).bigDec$.Refs1.*?(Ref1.int1).get.map(_ ==> List((4, Some(bigDec1), Nil), (5, None, Nil)))
        _ <- Ns.int(6, 7).bigDecs$.Refs1.*?(Ref1.int1).get.map(_ ==> List((6, Some(Set(bigDec1)), Nil), (7, None, Nil)))
        _ <- Ns.int(8, 9).bigDecMap$.Refs1.*?(Ref1.int1).get.map(_ ==> List((8, Some(Map("a" -> bigDec1)), Nil), (9, None, Nil)))

        _ <- Ns.int(1).bigDec.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "bigDec": "1.0",
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(2).bigDecs.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "bigDecs": [
            |          "1.0"
            |        ],
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(3).bigDecMap.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "bigDecMap": {
            |          "a": "1.0"
            |        },
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(4, 5).bigDec$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "bigDec$": "1.0",
            |        "Refs1": []
            |      },
            |      {
            |        "int": 5,
            |        "bigDec$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(6, 7).bigDecs$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "bigDecs$": [
            |          "1.0"
            |        ],
            |        "Refs1": []
            |      },
            |      {
            |        "int": 7,
            |        "bigDecs$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
        _ <- Ns.int(8, 9).bigDecMap$.Refs1.*?(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 8,
            |        "bigDecMap$": {
            |          "a": "1.0"
            |        },
            |        "Refs1": []
            |      },
            |      {
            |        "int": 9,
            |        "bigDecMap$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )
      } yield ()
    }
  }
}