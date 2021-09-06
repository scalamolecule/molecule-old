package moleculeTests.tests.core.nested

import molecule.datomic.api.in3_out11.m
import molecule.datomic.api.out6._
import moleculeTests.Adhoc.core
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object NestedAttrs1 extends AsyncTestSuite {

  lazy val tests = Tests {

    "String" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.str$) insert List(
          ("A", List((1, Some("a")), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.str$).get.map(_ ==> List(
          ("A", List((1, Some("a")), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.str).get.map(_ ==> List(
          ("A", List((1, "a")))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.str_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.str$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some("a")), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.str).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, "a"))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.str_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))


        // Getting the first object only
        _ <- m(Ref1.str1.Nss * Ns.int.str$).getObj.collect { case o =>
          o.str1 ==> "A"
          o.Nss(0).int ==> 1
          o.Nss(0).str$ ==> Some("a")
          o.Nss(1).int ==> 2
          o.Nss(1).str$ ==> None
        }

        // Getting list of objects
        _ <- m(Ref1.str1.Nss * Ns.int.str$).getObjs.collect { case List(o) =>
          o.str1 ==> "A"
          o.Nss(0).int ==> 1
          o.Nss(0).str$ ==> Some("a")
          o.Nss(1).int ==> 2
          o.Nss(1).str$ ==> None
        }
        _ <- m(Ref1.str1.Nss * Ns.int.str).getObjs.collect { case List(o) =>
          o.str1 ==> "A"
          o.Nss(0).int ==> 1
          o.Nss(0).str ==> "a"
        }
        _ <- m(Ref1.str1.Nss * Ns.int.str_).getObjs.collect { case List(o) =>
          o.str1 ==> "A"
          o.Nss(0).int ==> 1
        }

        _ <- m(Ref1.str1.Nss *? Ns.int.str$).getObjs.collect { case List(o1, o2) =>
          o1.str1 ==> "A"
          o1.Nss(0).int ==> 1
          o1.Nss(0).str$ ==> Some("a")
          o1.Nss(1).int ==> 2
          o1.Nss(1).str$ ==> None
          o2.str1 ==> "B"
          o2.Nss ==> Nil
        }
        _ <- m(Ref1.str1.Nss *? Ns.int.str).getObjs.collect { case List(o1, o2) =>
          o1.str1 ==> "A"
          o1.Nss(0).int ==> 1
          o1.Nss(0).str ==> "a"
          o2.str1 ==> "B"
          o2.Nss ==> Nil
        }
        _ <- m(Ref1.str1.Nss *? Ns.int.str_).getObjs.collect { case List(o1, o2) =>
          o1.str1 ==> "A"
          o1.Nss(0).int ==> 1
          o2.str1 ==> "B"
          o2.Nss ==> Nil
        }


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
      } yield ()
    }


    "Enum" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.enum$) insert List(
          ("A", List((1, Some("enum1")), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.enum$).get.map(_ ==> List(
          ("A", List((1, Some("enum1")), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.enum).get.map(_ ==> List(
          ("A", List((1, "enum1")))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.enum_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.enum$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some("enum1")), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.enum).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, "enum1"))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.enum_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }


    "Int" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.str.int$) insert List(
          ("A", List(("a", Some(10)), ("aa", None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.str.int$).get.map(_ ==> List(
          ("A", List(("a", Some(10)), ("aa", None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.str.int).get.map(_ ==> List(
          ("A", List(("a", 10)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.str.int_).get.map(_ ==> List(
          ("A", List("a"))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.str.int$).get.map(_.sortBy(_._1) ==> List(
          ("A", List(("a", Some(10)), ("aa", None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.str.int).get.map(_.sortBy(_._1) ==> List(
          ("A", List(("a", 10))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.str.int_).get.map(_.sortBy(_._1) ==> List(
          ("A", List("a")),
          ("B", List())
        ))
      } yield ()
    }


    "Long" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.long$) insert List(
          ("A", List((1, Some(10L)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.long$).get.map(_ ==> List(
          ("A", List((1, Some(10L)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.long).get.map(_ ==> List(
          ("A", List((1, 10L)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.long_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.long$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(10L)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.long).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, 10L))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.long_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }

    "ref" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.ref1$) insert List(
          ("A", List((1, Some(42L)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.ref1$).get.map(_ ==> List(
          ("A", List((1, Some(42L)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.ref1).get.map(_ ==> List(
          ("A", List((1, 42L)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.ref1_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.ref1$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(42L)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.ref1).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, 42L))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.ref1_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }


    "Double" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.double$) insert List(
          ("A", List((1, Some(1.1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.double$).get.map(_ ==> List(
          ("A", List((1, Some(1.1)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.double).get.map(_ ==> List(
          ("A", List((1, 1.1)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.double_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.double$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(1.1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.double).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, 1.1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.double_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }


    "Boolean" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.bool$) insert List(
          ("A", List((1, Some(true)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.bool$).get.map(_ ==> List(
          ("A", List((1, Some(true)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bool).get.map(_ ==> List(
          ("A", List((1, true)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bool_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.bool$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(true)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.bool).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, true))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.bool_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }


    "Date" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.date$) insert List(
          ("A", List((1, Some(date1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.date$).get.map(_ ==> List(
          ("A", List((1, Some(date1)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.date).get.map(_ ==> List(
          ("A", List((1, date1)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.date_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.date$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(date1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.date).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, date1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.date_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }


    "UUID" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.uuid$) insert List(
          ("A", List((1, Some(uuid1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.uuid$).get.map(_ ==> List(
          ("A", List((1, Some(uuid1)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uuid).get.map(_ ==> List(
          ("A", List((1, uuid1)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uuid_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.uuid$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(uuid1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.uuid).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, uuid1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.uuid_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }


    "URI" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.uri$) insert List(
          ("A", List((1, Some(uri1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.uri$).get.map(_ ==> List(
          ("A", List((1, Some(uri1)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uri).get.map(_ ==> List(
          ("A", List((1, uri1)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.uri_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.uri$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(uri1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.uri).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, uri1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.uri_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }


    "BigInt" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.bigInt$) insert List(
          ("A", List((1, Some(bigInt1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.bigInt$).get.map(_ ==> List(
          ("A", List((1, Some(bigInt1)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigInt).get.map(_ ==> List(
          ("A", List((1, bigInt1)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigInt_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.bigInt$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(bigInt1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.bigInt).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, bigInt1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.bigInt_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }


    "BigDecimal" - core { implicit conn =>
      for {
        _ <- m(Ref1.str1.Nss * Ns.int.bigDec$) insert List(
          ("A", List((1, Some(bigDec1)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss * Ns.int.bigDec$).get.map(_ ==> List(
          ("A", List((1, Some(bigDec1)), (2, None)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigDec).get.map(_ ==> List(
          ("A", List((1, bigDec1)))
        ))
        _ <- m(Ref1.str1.Nss * Ns.int.bigDec_).get.map(_ ==> List(
          ("A", List(1))
        ))

        _ <- m(Ref1.str1.Nss *? Ns.int.bigDec$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, Some(bigDec1)), (2, None))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.bigDec).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, bigDec1))),
          ("B", List())
        ))
        _ <- m(Ref1.str1.Nss *? Ns.int.bigDec_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(1)),
          ("B", List())
        ))
      } yield ()
    }
  }
}