package moleculeTests.tests.core.build.flat

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object OptMany extends AsyncTestSuite {

  lazy val tests = Tests {

    "String" - core { implicit conn =>
      for {
        _ <- Ns.int.strs$ insert List((1, None), (2, Some(Set("a"))))

        _ <- Ns.int.strs$.get.map(_ ==> List((1, None), (2, Some(Set("a")))))
        _ <- Ns.int.strs$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.strs$(Some(Set("a"))).get.map(_ ==> List((2, Some(Set("a")))))

        _ <- Ns.int.strs$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.strs$ ==> None
          o2.int ==> 2
          o2.strs$ ==> Some(Set("a"))
        }
        _ <- Ns.int.strs$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.strs$ ==> None
        }
        _ <- Ns.int.strs$(Some(Set("a"))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.strs$ ==> Some(Set("a"))
        }
        _ <- Ns.int.strs.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.strs ==> Set("a")
        }

        _ <- Ns.int.strs$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "strs$": null
            |      },
            |      {
            |        "int": 2,
            |        "strs$": [
            |          "a"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.strs$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "strs$": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.strs$(Some(Set("a"))).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "strs$": [
            |          "a"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Int" - core { implicit conn =>
      for {
        _ <- Ns.int.ints$ insert List((1, None), (2, Some(Set(20))))

        _ <- Ns.int.ints$.get.map(_ ==> List((1, None), (2, Some(Set(20)))))
        _ <- Ns.int.ints$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.ints$(Some(Set(20))).get.map(_ ==> List((2, Some(Set(20)))))

        _ <- Ns.int.ints$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.ints$ ==> None
          o2.int ==> 2
          o2.ints$ ==> Some(Set(20))
        }
        _ <- Ns.int.ints$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.ints$ ==> None
        }
        _ <- Ns.int.ints$(Some(Set(20))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.ints$ ==> Some(Set(20))
        }
        _ <- Ns.int.ints.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.ints ==> Set(20)
        }

        _ <- Ns.int.ints$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "ints$": null
            |      },
            |      {
            |        "int": 2,
            |        "ints$": [
            |          20
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.ints$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "ints$": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.ints$(Some(Set(20))).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "ints$": [
            |          20
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Long" - core { implicit conn =>
      for {
        _ <- Ns.int.longs$ insert List((1, None), (2, Some(Set(20L))))

        _ <- Ns.int.longs$.get.map(_ ==> List((1, None), (2, Some(Set(20L)))))
        _ <- Ns.int.longs$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.longs$(Some(Set(20L))).get.map(_ ==> List((2, Some(Set(20L)))))

        _ <- Ns.int.longs$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.longs$ ==> None
          o2.int ==> 2
          o2.longs$ ==> Some(Set(20L))
        }
        _ <- Ns.int.longs$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.longs$ ==> None
        }
        _ <- Ns.int.longs$(Some(Set(20L))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.longs$ ==> Some(Set(20L))
        }
        _ <- Ns.int.longs.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.longs ==> Set(20L)
        }

        _ <- Ns.int.longs$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "longs$": null
            |      },
            |      {
            |        "int": 2,
            |        "longs$": [
            |          20
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.longs$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "longs$": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.longs$(Some(Set(20L))).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "longs$": [
            |          20
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Double" - core { implicit conn =>
      for {
        _ <- Ns.int.doubles$ insert List((1, None), (2, Some(Set(2.2))))

        _ <- Ns.int.doubles$.get.map(_ ==> List((1, None), (2, Some(Set(2.2)))))
        _ <- Ns.int.doubles$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.doubles$(Some(Set(2.2))).get.map(_ ==> List((2, Some(Set(2.2)))))

        _ <- Ns.int.doubles$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.doubles$ ==> None
          o2.int ==> 2
          o2.doubles$ ==> Some(Set(2.2))
        }
        _ <- Ns.int.doubles$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.doubles$ ==> None
        }
        _ <- Ns.int.doubles$(Some(Set(2.2))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.doubles$ ==> Some(Set(2.2))
        }
        _ <- Ns.int.doubles.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.doubles ==> Set(2.2)
        }

        _ <- Ns.int.doubles$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "doubles$": null
            |      },
            |      {
            |        "int": 2,
            |        "doubles$": [
            |          2.2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.doubles$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "doubles$": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.doubles$(Some(Set(2.2))).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "doubles$": [
            |          2.2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Boolean" - core { implicit conn =>
      for {
        _ <- Ns.int.bools$ insert List((1, None), (2, Some(Set(false))))

        _ <- Ns.int.bools$.get.map(_ ==> List((1, None), (2, Some(Set(false)))))
        _ <- Ns.int.bools$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.bools$(Some(Set(false))).get.map(_ ==> List((2, Some(Set(false)))))

        _ <- Ns.int.bools$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.bools$ ==> None
          o2.int ==> 2
          o2.bools$ ==> Some(Set(false))
        }
        _ <- Ns.int.bools$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.bools$ ==> None
        }
        _ <- Ns.int.bools$(Some(Set(false))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bools$ ==> Some(Set(false))
        }
        _ <- Ns.int.bools.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bools ==> Set(false)
        }

        _ <- Ns.int.bools$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "bools$": null
            |      },
            |      {
            |        "int": 2,
            |        "bools$": [
            |          false
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.bools$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "bools$": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.bools$(Some(Set(false))).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "bools$": [
            |          false
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Date" - core { implicit conn =>
      for {
        _ <- Ns.int.dates$ insert List((1, None), (2, Some(Set(date1))))

        _ <- Ns.int.dates$.get.map(_ ==> List((1, None), (2, Some(Set(date1)))))
        _ <- Ns.int.dates$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.dates$(Some(Set(date1))).get.map(_ ==> List((2, Some(Set(date1)))))

        _ <- Ns.int.dates$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.dates$ ==> None
          o2.int ==> 2
          o2.dates$ ==> Some(Set(date1))
        }
        _ <- Ns.int.dates$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.dates$ ==> None
        }
        _ <- Ns.int.dates$(Some(Set(date1))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.dates$ ==> Some(Set(date1))
        }
        _ <- Ns.int.dates.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.dates ==> Set(date1)
        }

        _ <- Ns.int.dates$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "dates$": null
            |      },
            |      {
            |        "int": 2,
            |        "dates$": [
            |          "2001-07-01"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.dates$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "dates$": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.dates$(Some(Set(date1))).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "dates$": [
            |          "2001-07-01"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "UUID" - core { implicit conn =>
      for {
        _ <- Ns.int.uuids$ insert List((1, None), (2, Some(Set(uuid1))))

        _ <- Ns.int.uuids$.get.map(_ ==> List((1, None), (2, Some(Set(uuid1)))))
        _ <- Ns.int.uuids$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.uuids$(Some(Set(uuid1))).get.map(_ ==> List((2, Some(Set(uuid1)))))

        _ <- Ns.int.uuids$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.uuids$ ==> None
          o2.int ==> 2
          o2.uuids$ ==> Some(Set(uuid1))
        }
        _ <- Ns.int.uuids$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.uuids$ ==> None
        }
        _ <- Ns.int.uuids$(Some(Set(uuid1))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.uuids$ ==> Some(Set(uuid1))
        }
        _ <- Ns.int.uuids.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.uuids ==> Set(uuid1)
        }

        _ <- Ns.int.uuids$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uuids$$": null
             |      },
             |      {
             |        "int": 2,
             |        "uuids$$": [
             |          "$uuid1"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.uuids$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uuids$$": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.uuids$(Some(Set(uuid1))).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "uuids$$": [
             |          "$uuid1"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "URI" - core { implicit conn =>
      for {
        _ <- Ns.int.uris$ insert List((1, None), (2, Some(Set(uri1))))

        _ <- Ns.int.uris$.get.map(_ ==> List((1, None), (2, Some(Set(uri1)))))
        _ <- Ns.int.uris$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.uris$(Some(Set(uri1))).get.map(_ ==> List((2, Some(Set(uri1)))))

        _ <- Ns.int.uris$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.uris$ ==> None
          o2.int ==> 2
          o2.uris$ ==> Some(Set(uri1))
        }
        _ <- Ns.int.uris$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.uris$ ==> None
        }
        _ <- Ns.int.uris$(Some(Set(uri1))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.uris$ ==> Some(Set(uri1))
        }
        _ <- Ns.int.uris.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.uris ==> Set(uri1)
        }

        _ <- Ns.int.uris$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uris$$": null
             |      },
             |      {
             |        "int": 2,
             |        "uris$$": [
             |          "$uri1"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.uris$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uris$$": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.uris$(Some(Set(uri1))).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "uris$$": [
             |          "$uri1"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "BigInt" - core { implicit conn =>
      for {
        _ <- Ns.int.bigInts$ insert List((1, None), (2, Some(Set(bigInt1))))

        _ <- Ns.int.bigInts$.get.map(_ ==> List((1, None), (2, Some(Set(bigInt1)))))
        _ <- Ns.int.bigInts$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.bigInts$(Some(Set(bigInt1))).get.map(_ ==> List((2, Some(Set(bigInt1)))))

        _ <- Ns.int.bigInts$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.bigInts$ ==> None
          o2.int ==> 2
          o2.bigInts$ ==> Some(Set(bigInt1))
        }
        _ <- Ns.int.bigInts$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.bigInts$ ==> None
        }
        _ <- Ns.int.bigInts$(Some(Set(bigInt1))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bigInts$ ==> Some(Set(bigInt1))
        }
        _ <- Ns.int.bigInts.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bigInts ==> Set(bigInt1)
        }

        _ <- Ns.int.bigInts$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "bigInts$$": null
             |      },
             |      {
             |        "int": 2,
             |        "bigInts$$": [
             |          $bigInt1
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.bigInts$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "bigInts$$": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.bigInts$(Some(Set(bigInt1))).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "bigInts$$": [
             |          $bigInt1
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "BigDecimal" - core { implicit conn =>
      for {
        _ <- Ns.int.bigDecs$ insert List((1, None), (2, Some(Set(bigDec1))))

        _ <- Ns.int.bigDecs$.get.map(_ ==> List((1, None), (2, Some(Set(bigDec1)))))
        _ <- Ns.int.bigDecs$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.bigDecs$(Some(Set(bigDec1))).get.map(_ ==> List((2, Some(Set(bigDec1)))))

        _ <- Ns.int.bigDecs$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.bigDecs$ ==> None
          o2.int ==> 2
          o2.bigDecs$ ==> Some(Set(bigDec1))
        }
        _ <- Ns.int.bigDecs$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.bigDecs$ ==> None
        }
        _ <- Ns.int.bigDecs$(Some(Set(bigDec1))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bigDecs$ ==> Some(Set(bigDec1))
        }
        _ <- Ns.int.bigDecs.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bigDecs ==> Set(bigDec1)
        }

        _ <- Ns.int.bigDecs$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "bigDecs$$": null
             |      },
             |      {
             |        "int": 2,
             |        "bigDecs$$": [
             |          $bigDec1
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.bigDecs$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "bigDecs$$": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.bigDecs$(Some(Set(bigDec1))).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "bigDecs$$": [
             |          $bigDec1
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "Enum" - core { implicit conn =>
      for {
        _ <- Ns.int.enums$ insert List((1, None), (2, Some(Set(enum1))))

        _ <- Ns.int.enums$.get.map(_ ==> List((1, None), (2, Some(Set(enum1)))))
        _ <- Ns.int.enums$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.enums$(Some(Set(enum1))).get.map(_ ==> List((2, Some(Set(enum1)))))

        _ <- Ns.int.enums$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.enums$ ==> None
          o2.int ==> 2
          o2.enums$ ==> Some(Set(enum1))
        }
        _ <- Ns.int.enums$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.enums$ ==> None
        }
        _ <- Ns.int.enums$(Some(Set(enum1))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.enums$ ==> Some(Set(enum1))
        }
        _ <- Ns.int.enums.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.enums ==> Set(enum1)
        }

        _ <- Ns.int.enums$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "enums$$": null
             |      },
             |      {
             |        "int": 2,
             |        "enums$$": [
             |          "$enum1"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.enums$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "enums$$": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.enums$(Some(Set(enum1))).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "enums$$": [
             |          "$enum1"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "RefAttr" - core { implicit conn =>
      for {
        refId <- Ref1.int1(1).save.map(_.eid)
        _ <- Ns.int.refs1$ insert List((1, None), (2, Some(Set(refId))))

        _ <- Ns.int.refs1$.get.map(_ ==> List((1, None), (2, Some(Set(refId)))))
        _ <- Ns.int.refs1$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.refs1$(Some(Set(refId))).get.map(_ ==> List((2, Some(Set(refId)))))

        _ <- Ns.int.refs1$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.refs1$ ==> None
          o2.int ==> 2
          o2.refs1$ ==> Some(Set(refId))
        }
        _ <- Ns.int.refs1$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.refs1$ ==> None
        }
        _ <- Ns.int.refs1$(Some(Set(refId))).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.refs1$ ==> Some(Set(refId))
        }
        _ <- Ns.int.refs1.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.refs1 ==> Set(refId)
        }

        _ <- Ns.int.refs1$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "refs1$$": null
             |      },
             |      {
             |        "int": 2,
             |        "refs1$$": [
             |          $refId
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.refs1$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "refs1$$": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.refs1$(Some(Set(refId))).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "refs1$$": [
             |          $refId
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }
  }
}
