package moleculeTests.tests.core.build.flat

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object OptOne extends AsyncTestSuite {

  lazy val tests = Tests {

    "String" - core { implicit conn =>
      val data = List((1, None), (2, Some("a")))
      for {
        _ <- Ns.int.str$ insert data

        _ <- Ns.int.str$.get.map(_ ==> data)
        // Optional apply
        _ <- Ns.int.str$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.str$(Some("a")).get.map(_ ==> List((2, Some("a"))))

        _ <- Ns.int.str$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.str$ ==> None
          o2.int ==> 2
          o2.str$ ==> Some("a")
        }
        _ <- Ns.int.str$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.str$ ==> None
        }
        _ <- Ns.int.str$(Some("a")).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.str$ ==> Some("a")
        }
        _ <- Ns.int.str.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.str ==> "a"
        }

        _ <- Ns.int.str$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "str": null
            |      },
            |      {
            |        "int": 2,
            |        "str": "a"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.str$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "str": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.str$(Some("a")).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "str": "a"
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Int" - core { implicit conn =>
      for {
        _ <- Ns.long.int$ insert List((1L, None), (2L, Some(20)))

        _ <- Ns.long.int$.get.map(_ ==> List((1L, None), (2L, Some(20))))
        _ <- Ns.long.int$(None).get.map(_ ==> List((1L, None)))
        _ <- Ns.long.int$(Some(20)).get.map(_ ==> List((2L, Some(20))))

        _ <- Ns.long.int$.getObjs.collect { case List(o1, o2) =>
          o1.long ==> 1L
          o1.int$ ==> None
          o2.long ==> 2L
          o2.int$ ==> Some(20)
        }
        _ <- Ns.long.int$(None).getObjs.collect { case List(o) =>
          o.long ==> 1L
          o.int$ ==> None
        }
        _ <- Ns.long.int$(Some(20)).getObjs.collect { case List(o) =>
          o.long ==> 2L
          o.int$ ==> Some(20)
        }
        _ <- Ns.long.int.getObjs.collect { case List(o) =>
          o.long ==> 2L
          o.int ==> 20
        }

        _ <- Ns.long.int$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 1,
            |        "int": null
            |      },
            |      {
            |        "long": 2,
            |        "int": 20
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.long.int$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 1,
            |        "int": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.long.int$(Some(20)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 2,
            |        "int": 20
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Long" - core { implicit conn =>
      for {
        _ <- Ns.int.long$ insert List((1, None), (2, Some(20L)))

        _ <- Ns.int.long$.get.map(_ ==> List((1, None), (2, Some(20L))))
        _ <- Ns.int.long$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.long$(Some(20L)).get.map(_ ==> List((2, Some(20L))))

        _ <- Ns.int.long$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.long$ ==> None
          o2.int ==> 2
          o2.long$ ==> Some(20L)
        }
        _ <- Ns.int.long$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.long$ ==> None
        }
        _ <- Ns.int.long$(Some(20L)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.long$ ==> Some(20L)
        }
        _ <- Ns.int.long.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.long ==> 20L
        }

        _ <- Ns.int.long$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "long": null
            |      },
            |      {
            |        "int": 2,
            |        "long": 20
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.long$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "long": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.long$(Some(20L)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "long": 20
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Double" - core { implicit conn =>
      for {
        _ <- Ns.int.double$ insert List((1, None), (2, Some(2.2)))

        _ <- Ns.int.double$.get.map(_ ==> List((1, None), (2, Some(2.2))))
        _ <- Ns.int.double$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.double$(Some(2.2)).get.map(_ ==> List((2, Some(2.2))))

        _ <- Ns.int.double$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.double$ ==> None
          o2.int ==> 2
          o2.double$ ==> Some(2.2)
        }
        _ <- Ns.int.double$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.double$ ==> None
        }
        _ <- Ns.int.double$(Some(2.2)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.double$ ==> Some(2.2)
        }
        _ <- Ns.int.double.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.double ==> 2.2
        }

        _ <- Ns.int.double$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "double": null
            |      },
            |      {
            |        "int": 2,
            |        "double": 2.2
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.double$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "double": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.double$(Some(2.2)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "double": 2.2
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Boolean" - core { implicit conn =>
      for {
        _ <- Ns.int.bool$ insert List((1, None), (2, Some(false)))

        _ <- Ns.int.bool$.get.map(_ ==> List((1, None), (2, Some(false))))
        _ <- Ns.int.bool$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.bool$(Some(false)).get.map(_ ==> List((2, Some(false))))

        _ <- Ns.int.bool$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.bool$ ==> None
          o2.int ==> 2
          o2.bool$ ==> Some(false)
        }
        _ <- Ns.int.bool$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.bool$ ==> None
        }
        _ <- Ns.int.bool$(Some(false)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bool$ ==> Some(false)
        }
        _ <- Ns.int.bool.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bool ==> false
        }

        _ <- Ns.int.bool$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "bool": null
            |      },
            |      {
            |        "int": 2,
            |        "bool": false
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.bool$(None).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "bool": null
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.bool$(Some(false)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "bool": false
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Date" - core { implicit conn =>
      for {
        _ <- Ns.int.date$ insert List((1, None), (2, Some(date1)))

        _ <- Ns.int.date$.get.map(_ ==> List((1, None), (2, Some(date1))))
        _ <- Ns.int.date$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.date$(Some(date1)).get.map(_ ==> List((2, Some(date1))))

        _ <- Ns.int.date$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.date$ ==> None
          o2.int ==> 2
          o2.date$ ==> Some(date1)
        }
        _ <- Ns.int.date$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.date$ ==> None
        }
        _ <- Ns.int.date$(Some(date1)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.date$ ==> Some(date1)
        }
        _ <- Ns.int.date.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.date ==> date1
        }

        _ <- Ns.int.date$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "date": null
             |      },
             |      {
             |        "int": 2,
             |        "date": "2001-07-01"
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.date$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "date": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.date$(Some(date1)).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "date": "2001-07-01"
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "UUID" - core { implicit conn =>
      for {
        _ <- Ns.int.uuid$ insert List((1, None), (2, Some(uuid1)))

        _ <- Ns.int.uuid$.get.map(_ ==> List((1, None), (2, Some(uuid1))))
        _ <- Ns.int.uuid$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.uuid$(Some(uuid1)).get.map(_ ==> List((2, Some(uuid1))))

        _ <- Ns.int.uuid$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.uuid$ ==> None
          o2.int ==> 2
          o2.uuid$ ==> Some(uuid1)
        }
        _ <- Ns.int.uuid$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.uuid$ ==> None
        }
        _ <- Ns.int.uuid$(Some(uuid1)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.uuid$ ==> Some(uuid1)
        }
        _ <- Ns.int.uuid.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.uuid ==> uuid1
        }

        _ <- Ns.int.uuid$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uuid": null
             |      },
             |      {
             |        "int": 2,
             |        "uuid": "$uuid1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.uuid$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uuid": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.uuid$(Some(uuid1)).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "uuid": "$uuid1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "URI" - core { implicit conn =>
      for {
        _ <- Ns.int.uri$ insert List((1, None), (2, Some(uri1)))

        _ <- Ns.int.uri$.get.map(_ ==> List((1, None), (2, Some(uri1))))
        _ <- Ns.int.uri$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.uri$(Some(uri1)).get.map(_ ==> List((2, Some(uri1))))

        _ <- Ns.int.uri$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.uri$ ==> None
          o2.int ==> 2
          o2.uri$ ==> Some(uri1)
        }
        _ <- Ns.int.uri$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.uri$ ==> None
        }
        _ <- Ns.int.uri$(Some(uri1)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.uri$ ==> Some(uri1)
        }
        _ <- Ns.int.uri.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.uri ==> uri1
        }

        _ <- Ns.int.uri$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uri": null
             |      },
             |      {
             |        "int": 2,
             |        "uri": "$uri1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.uri$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "uri": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.uri$(Some(uri1)).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "uri": "$uri1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "BigInt" - core { implicit conn =>
      for {
        _ <- Ns.int.bigInt$ insert List((1, None), (2, Some(bigInt1)))

        _ <- Ns.int.bigInt$.get.map(_ ==> List((1, None), (2, Some(bigInt1))))
        _ <- Ns.int.bigInt$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.bigInt$(Some(bigInt1)).get.map(_ ==> List((2, Some(bigInt1))))

        _ <- Ns.int.bigInt$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.bigInt$ ==> None
          o2.int ==> 2
          o2.bigInt$ ==> Some(bigInt1)
        }
        _ <- Ns.int.bigInt$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.bigInt$ ==> None
        }
        _ <- Ns.int.bigInt$(Some(bigInt1)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bigInt$ ==> Some(bigInt1)
        }
        _ <- Ns.int.bigInt.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bigInt ==> bigInt1
        }

        _ <- Ns.int.bigInt$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "bigInt": null
             |      },
             |      {
             |        "int": 2,
             |        "bigInt": $bigInt1
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.bigInt$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "bigInt": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.bigInt$(Some(bigInt1)).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "bigInt": $bigInt1
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "BigDecimal" - core { implicit conn =>
      for {
        _ <- Ns.int.bigDec$ insert List((1, None), (2, Some(bigDec1)))

        _ <- Ns.int.bigDec$.get.map(_ ==> List((1, None), (2, Some(bigDec1))))
        _ <- Ns.int.bigDec$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.bigDec$(Some(bigDec1)).get.map(_ ==> List((2, Some(bigDec1))))

        _ <- Ns.int.bigDec$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.bigDec$ ==> None
          o2.int ==> 2
          o2.bigDec$ ==> Some(bigDec1)
        }
        _ <- Ns.int.bigDec$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.bigDec$ ==> None
        }
        _ <- Ns.int.bigDec$(Some(bigDec1)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bigDec$ ==> Some(bigDec1)
        }
        _ <- Ns.int.bigDec.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.bigDec ==> bigDec1
        }

        _ <- Ns.int.bigDec$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "bigDec": null
             |      },
             |      {
             |        "int": 2,
             |        "bigDec": $bigDec1
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.bigDec$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "bigDec": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.bigDec$(Some(bigDec1)).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "bigDec": $bigDec1
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "Enum" - core { implicit conn =>
      for {
        _ <- Ns.int.enum$ insert List((1, None), (2, Some(enum1)))

        _ <- Ns.int.enum$.get.map(_ ==> List((1, None), (2, Some(enum1))))
        _ <- Ns.int.enum$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.enum$(Some(enum1)).get.map(_ ==> List((2, Some(enum1))))

        _ <- Ns.int.enum$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.enum$ ==> None
          o2.int ==> 2
          o2.enum$ ==> Some(enum1)
        }
        _ <- Ns.int.enum$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.enum$ ==> None
        }
        _ <- Ns.int.enum$(Some(enum1)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.enum$ ==> Some(enum1)
        }
        _ <- Ns.int.enum.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.enum ==> enum1
        }

        _ <- Ns.int.enum$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "enum": null
             |      },
             |      {
             |        "int": 2,
             |        "enum": "$enum1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.enum$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "enum": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.enum$(Some(enum1)).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "enum": "$enum1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "RefAttr" - core { implicit conn =>
      for {
        refId <- Ref1.int1(1).save.map(_.eid)
        _ <- Ns.int.ref1$ insert List((1, None), (2, Some(refId)))

        _ <- Ns.int.ref1$.get.map(_ ==> List((1, None), (2, Some(refId))))
        _ <- Ns.int.ref1$(None).get.map(_ ==> List((1, None)))
        _ <- Ns.int.ref1$(Some(refId)).get.map(_ ==> List((2, Some(refId))))

        _ <- Ns.int.ref1$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.ref1$ ==> None
          o2.int ==> 2
          o2.ref1$ ==> Some(refId)
        }
        _ <- Ns.int.ref1$(None).getObjs.collect { case List(o) =>
          o.int ==> 1
          o.ref1$ ==> None
        }
        _ <- Ns.int.ref1$(Some(refId)).getObjs.collect { case List(o) =>
          o.int ==> 2
          o.ref1$ ==> Some(refId)
        }
        _ <- Ns.int.ref1.getObjs.collect { case List(o) =>
          o.int ==> 2
          o.ref1 ==> refId
        }

        _ <- Ns.int.ref1$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "ref1": null
             |      },
             |      {
             |        "int": 2,
             |        "ref1": $refId
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.ref1$(None).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 1,
             |        "ref1": null
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int.ref1$(Some(refId)).getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 2,
             |        "ref1": $refId
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }
  }
}
