package moleculeTests.tests.core.nested

import molecule.core.util.Executor._
import molecule.datomic.api.out6._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object NestedAttrs1 extends AsyncTestSuite {

  lazy val tests = Tests {

    "String" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.str$) insert List(
          ("A", List((1, Some("a")), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.str$).get.map(_ ==> List(
          ("A", List((1, Some("a")), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.str$).get.map(_ ==> List(
          ("A", List((1, Some("a")), (2, None)))
        ))


        _ <- m(Ref1.str1.a1.Nss *? Ns.int.str).get.map(_ ==> List(
          ("A", List((1, "a"))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.str).get.map(_ ==> List(
          ("A", List((1, "a")))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.str_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.str_).get.map(_ ==> List(
          ("A", List(1))
        ))


        _ <- m(Ref1.str1.Nss *? Ns.int.str$).getObjs.collect { case List(o1, o2) =>
          o1.str1 ==> "A"
          o1.Nss(0).int ==> 1
          o1.Nss(0).str$ ==> Some("a")
          o1.Nss(1).int ==> 2
          o1.Nss(1).str$ ==> None
          o2.str1 ==> "B"
          o2.Nss ==> Nil
        }
        _ <- m(Ref1.str1.Nss * Ns.int.str$).getObj.collect { case o =>
          o.str1 ==> "A"
          o.Nss(0).int ==> 1
          o.Nss(0).str$ ==> Some("a")
          o.Nss(1).int ==> 2
          o.Nss(1).str$ ==> None
        }

        _ <- m(Ref1.str1.Nss *? Ns.int.str).getObjs.collect { case List(o1, o2) =>
          o1.str1 ==> "A"
          o1.Nss(0).int ==> 1
          o1.Nss(0).str ==> "a"
          o2.str1 ==> "B"
          o2.Nss ==> Nil
        }
        _ <- m(Ref1.str1.Nss * Ns.int.str).getObjs.collect { case List(o) =>
          o.str1 ==> "A"
          o.Nss(0).int ==> 1
          o.Nss(0).str ==> "a"
        }

        _ <- m(Ref1.str1.Nss *? Ns.int.str_).getObjs.collect { case List(o1, o2) =>
          o1.str1 ==> "A"
          o1.Nss(0).int ==> 1
          o2.str1 ==> "B"
          o2.Nss ==> Nil
        }
        _ <- m(Ref1.str1.Nss * Ns.int.str_).getObjs.collect { case List(o) =>
          o.str1 ==> "A"
          o.Nss(0).int ==> 1
        }


        _ <- m(Ref1.str1.Nss *? Ns.int.str$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "str$": "a"
            |          },
            |          {
            |            "int": 2,
            |            "str$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.str$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "str$": "a"
            |          },
            |          {
            |            "int": 2,
            |            "str$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.str).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "str": "a"
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.str).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "str": "a"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.str_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.str_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Enum" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.enumm$) insert List(
          ("A", List((1, Some("enum1")), (2, None))),
          ("B", List())
        )
        _ <- m(Ref1.str1.a1.Nss *? Ns.int.enumm$).get.map(_ ==> List(
          ("A", List((1, Some("enum1")), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.enumm$).get.map(_ ==> List(
          ("A", List((1, Some("enum1")), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.enumm).get.map(_ ==> List(
          ("A", List((1, "enum1"))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.enumm).get.map(_ ==> List(
          ("A", List((1, "enum1")))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.enumm_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.enumm_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.enumm$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "enumm$": "enum1"
            |          },
            |          {
            |            "int": 2,
            |            "enumm$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.enumm$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "enumm$": "enum1"
            |          },
            |          {
            |            "int": 2,
            |            "enumm$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.enumm).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "enumm": "enum1"
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.enumm).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "enumm": "enum1"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.enumm_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.enumm_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Int" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.str.int$) insert List(
          ("A", List(("x", Some(11)), ("y", None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.str.int$).get.map(_ ==> List(
          ("A", List(("x", Some(11)), ("y", None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.str.int$).get.map(_ ==> List(
          ("A", List(("x", Some(11)), ("y", None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.str.int).get.map(_ ==> List(
          ("A", List(("x", 11))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.str.int).get.map(_ ==> List(
          ("A", List(("x", 11)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.str.int_).get.map(_ ==> List(
          ("A", List("x")),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.str.int_).get.map(_ ==> List(
          ("A", List("x"))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.str.int$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "str": "x",
            |            "int$": 11
            |          },
            |          {
            |            "str": "y",
            |            "int$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.str.int$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "str": "x",
            |            "int$": 11
            |          },
            |          {
            |            "str": "y",
            |            "int$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.str.int).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "str": "x",
            |            "int": 11
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.str.int).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "str": "x",
            |            "int": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.str.int_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "str": "x"
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.str.int_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "str": "x"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Long" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.long$) insert List(
          ("A", List((1, Some(11L)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.long$).get.map(_ ==> List(
          ("A", List((1, Some(11L)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.long$).get.map(_ ==> List(
          ("A", List((1, Some(11L)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.long).get.map(_ ==> List(
          ("A", List((1, 11L))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.long).get.map(_ ==> List(
          ("A", List((1, 11L)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.long_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.long_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.long$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "long$": 11
            |          },
            |          {
            |            "int": 2,
            |            "long$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.long$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "long$": 11
            |          },
            |          {
            |            "int": 2,
            |            "long$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.long).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "long": 11
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.long).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "long": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.long_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.long_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "ref" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.ref1$) insert List(
          ("A", List((1, Some(11L)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.ref1$).get.map(_ ==> List(
          ("A", List((1, Some(11L)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.ref1$).get.map(_ ==> List(
          ("A", List((1, Some(11L)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.ref1).get.map(_ ==> List(
          ("A", List((1, 11L))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.ref1).get.map(_ ==> List(
          ("A", List((1, 11L)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.ref1_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.ref1_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.ref1$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "ref1$": 11
            |          },
            |          {
            |            "int": 2,
            |            "ref1$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.ref1$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "ref1$": 11
            |          },
            |          {
            |            "int": 2,
            |            "ref1$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.ref1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "ref1": 11
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.ref1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "ref1": 11
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.ref1_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.ref1_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Double" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.double$) insert List(
          ("A", List((1, Some(1.1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.double$).get.map(_ ==> List(
          ("A", List((1, Some(1.1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.double$).get.map(_ ==> List(
          ("A", List((1, Some(1.1)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.double).get.map(_ ==> List(
          ("A", List((1, 1.1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.double).get.map(_ ==> List(
          ("A", List((1, 1.1)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.double_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.double_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.double$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "double$": 1.1
            |          },
            |          {
            |            "int": 2,
            |            "double$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.double$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "double$": 1.1
            |          },
            |          {
            |            "int": 2,
            |            "double$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.double).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "double": 1.1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.double).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "double": 1.1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.double_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.double_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Boolean" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.bool$) insert List(
          ("A", List((1, Some(true)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bool$).get.map(_ ==> List(
          ("A", List((1, Some(true)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bool$).get.map(_ ==> List(
          ("A", List((1, Some(true)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bool).get.map(_ ==> List(
          ("A", List((1, true))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bool).get.map(_ ==> List(
          ("A", List((1, true)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bool_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bool_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.bool$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bool$": true
            |          },
            |          {
            |            "int": 2,
            |            "bool$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bool$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bool$": true
            |          },
            |          {
            |            "int": 2,
            |            "bool$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.bool).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bool": true
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bool).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bool": true
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.bool_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bool_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Date" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.date$) insert List(
          ("A", List((1, Some(date1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.date$).get.map(_ ==> List(
          ("A", List((1, Some(date1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.date$).get.map(_ ==> List(
          ("A", List((1, Some(date1)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.date).get.map(_ ==> List(
          ("A", List((1, date1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.date).get.map(_ ==> List(
          ("A", List((1, date1)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.date_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.date_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.date$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "date$": "2001-07-01"
            |          },
            |          {
            |            "int": 2,
            |            "date$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.date$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "date$": "2001-07-01"
            |          },
            |          {
            |            "int": 2,
            |            "date$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.date).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "date": "2001-07-01"
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.date).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "date": "2001-07-01"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.date_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.date_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "UUID" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.uuid$) insert List(
          ("A", List((1, Some(uuid1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.uuid$).get.map(_ ==> List(
          ("A", List((1, Some(uuid1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uuid$).get.map(_ ==> List(
          ("A", List((1, Some(uuid1)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.uuid).get.map(_ ==> List(
          ("A", List((1, uuid1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uuid).get.map(_ ==> List(
          ("A", List((1, uuid1)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.uuid_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uuid_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.uuid$).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ref1": [
             |      {
             |        "str1": "A",
             |        "Nss": [
             |          {
             |            "int": 1,
             |            "uuid$$": "$uuid1"
             |          },
             |          {
             |            "int": 2,
             |            "uuid$$": null
             |          }
             |        ]
             |      },
             |      {
             |        "str1": "B",
             |        "Nss": []
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.uuid$).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ref1": [
             |      {
             |        "str1": "A",
             |        "Nss": [
             |          {
             |            "int": 1,
             |            "uuid$$": "$uuid1"
             |          },
             |          {
             |            "int": 2,
             |            "uuid$$": null
             |          }
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.uuid).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ref1": [
             |      {
             |        "str1": "A",
             |        "Nss": [
             |          {
             |            "int": 1,
             |            "uuid": "$uuid1"
             |          }
             |        ]
             |      },
             |      {
             |        "str1": "B",
             |        "Nss": []
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.uuid).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ref1": [
             |      {
             |        "str1": "A",
             |        "Nss": [
             |          {
             |            "int": 1,
             |            "uuid": "$uuid1"
             |          }
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.uuid_).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ref1": [
             |      {
             |        "str1": "A",
             |        "Nss": [
             |          {
             |            "int": 1
             |          }
             |        ]
             |      },
             |      {
             |        "str1": "B",
             |        "Nss": []
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.uuid_).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ref1": [
             |      {
             |        "str1": "A",
             |        "Nss": [
             |          {
             |            "int": 1
             |          }
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "URI" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.uri$) insert List(
          ("A", List((1, Some(uri1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.uri$).get.map(_ ==> List(
          ("A", List((1, Some(uri1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uri$).get.map(_ ==> List(
          ("A", List((1, Some(uri1)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.uri).get.map(_ ==> List(
          ("A", List((1, uri1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uri).get.map(_ ==> List(
          ("A", List((1, uri1)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.uri_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uri_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.uri$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "uri$": "uri1"
            |          },
            |          {
            |            "int": 2,
            |            "uri$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.uri$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "uri$": "uri1"
            |          },
            |          {
            |            "int": 2,
            |            "uri$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.uri).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "uri": "uri1"
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.uri).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "uri": "uri1"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.uri_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.uri_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "BigInt" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.bigInt$) insert List(
          ("A", List((1, Some(bigInt1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bigInt$).get.map(_ ==> List(
          ("A", List((1, Some(bigInt1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigInt$).get.map(_ ==> List(
          ("A", List((1, Some(bigInt1)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bigInt).get.map(_ ==> List(
          ("A", List((1, bigInt1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigInt).get.map(_ ==> List(
          ("A", List((1, bigInt1)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bigInt_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigInt_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.bigInt$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bigInt$": "1"
            |          },
            |          {
            |            "int": 2,
            |            "bigInt$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bigInt$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bigInt$": "1"
            |          },
            |          {
            |            "int": 2,
            |            "bigInt$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.bigInt).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bigInt": "1"
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bigInt).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bigInt": "1"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.bigInt_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bigInt_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "BigDecimal" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.bigDec$) insert List(
          ("A", List((1, Some(bigDec1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bigDec$).get.map(_ ==> List(
          ("A", List((1, Some(bigDec1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigDec$).get.map(_ ==> List(
          ("A", List((1, Some(bigDec1)), (2, None)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bigDec).get.map(_ ==> List(
          ("A", List((1, bigDec1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigDec).get.map(_ ==> List(
          ("A", List((1, bigDec1)))
        ))

        _ <- m(Ref1.str1.a1.Nss *? Ns.int.bigDec_).get.map(_ ==> List(
          ("A", List(1)),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigDec_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.bigDec$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bigDec$": "1.0"
            |          },
            |          {
            |            "int": 2,
            |            "bigDec$": null
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bigDec$).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bigDec$": "1.0"
            |          },
            |          {
            |            "int": 2,
            |            "bigDec$": null
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss *? Ns.int.bigDec).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bigDec": "1.0"
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bigDec).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1,
            |            "bigDec": "1.0"
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- m(Ref1.str1.Nss *? Ns.int.bigDec_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str1": "B",
            |        "Nss": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- m(Ref1.str1.Nss * Ns.int.bigDec_).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ref1": [
            |      {
            |        "str1": "A",
            |        "Nss": [
            |          {
            |            "int": 1
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