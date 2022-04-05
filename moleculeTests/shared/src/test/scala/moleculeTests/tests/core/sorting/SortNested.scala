package moleculeTests.tests.core.sorting

import molecule.core.util.Executor._
import molecule.datomic.api.in1_out7._
import moleculeTests.Adhoc.{bigDec1, bigDec2, bigInt1, bigInt2, date1, date2, uri1, uri2, uuid1, uuid2}
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.sorting.SortAttrs.core
import moleculeTests.tests.core.sorting.SortComposites.core
import utest._


object SortNested extends AsyncTestSuite {

  lazy val tests = Tests {

    "OptNested type, mandatory" - core { implicit conn =>
      for {
        _ <- Ref1.int1.Nss.*(Ns.str) insert List((1, List("a", "b")))
        _ <- Ref1.int1_(1).Nss.*?(Ns.str.a1).get.map(_ ==> List(List("a", "b")))
        _ <- Ref1.int1_(1).Nss.*?(Ns.str.d1).get.map(_ ==> List(List("b", "a")))

        _ <- Ref1.int1.Nss.*(Ns.enumm) insert List((2, List("enum1", "enum2")))
        _ <- Ref1.int1_(2).Nss.*?(Ns.enumm.a1).get.map(_ ==> List(List("enum1", "enum2")))
        _ <- Ref1.int1_(2).Nss.*?(Ns.enumm.d1).get.map(_ ==> List(List("enum2", "enum1")))

        _ <- Ref1.int1.Nss.*(Ns.int) insert List((3, List(1, 2)))
        _ <- Ref1.int1_(3).Nss.*?(Ns.int.a1).get.map(_ ==> List(List(1, 2)))
        _ <- Ref1.int1_(3).Nss.*?(Ns.int.d1).get.map(_ ==> List(List(2, 1)))

        _ <- Ref1.int1.Nss.*(Ns.long) insert List((4, List(10L, 20L)))
        _ <- Ref1.int1_(4).Nss.*?(Ns.long.a1).get.map(_ ==> List(List(10L, 20L)))
        _ <- Ref1.int1_(4).Nss.*?(Ns.long.d1).get.map(_ ==> List(List(20L, 10L)))

        _ <- Ref1.int1.Nss.*(Ns.ref1) insert List((5, List(10L, 20L)))
        _ <- Ref1.int1_(5).Nss.*?(Ns.ref1.a1).get.map(_ ==> List(List(10L, 20L)))
        _ <- Ref1.int1_(5).Nss.*?(Ns.ref1.d1).get.map(_ ==> List(List(20L, 10L)))

        _ <- Ref1.int1.Nss.*(Ns.double) insert List((6, List(1.1, 2.2)))
        _ <- Ref1.int1_(6).Nss.*?(Ns.double.a1).get.map(_ ==> List(List(1.1, 2.2)))
        _ <- Ref1.int1_(6).Nss.*?(Ns.double.d1).get.map(_ ==> List(List(2.2, 1.1)))

        _ <- Ref1.int1.Nss.*(Ns.bool) insert List((7, List(true, false)))
        _ <- Ref1.int1_(7).Nss.*?(Ns.bool.a1).get.map(_ ==> List(List(false, true)))
        _ <- Ref1.int1_(7).Nss.*?(Ns.bool.d1).get.map(_ ==> List(List(true, false)))

        _ <- Ref1.int1.Nss.*(Ns.date) insert List((8, List(date1, date2)))
        _ <- Ref1.int1_(8).Nss.*?(Ns.date.a1).get.map(_ ==> List(List(date1, date2)))
        _ <- Ref1.int1_(8).Nss.*?(Ns.date.d1).get.map(_ ==> List(List(date2, date1)))

        _ <- Ref1.int1.Nss.*(Ns.uuid) insert List((9, List(uuid1, uuid2)))
        _ <- Ref1.int1_(9).Nss.*?(Ns.uuid.a1).get.map(_ ==> List(List(uuid1, uuid2)))
        _ <- Ref1.int1_(9).Nss.*?(Ns.uuid.d1).get.map(_ ==> List(List(uuid2, uuid1)))

        _ <- Ref1.int1.Nss.*(Ns.uri) insert List((10, List(uri1, uri2)))
        _ <- Ref1.int1_(10).Nss.*?(Ns.uri.a1).get.map(_ ==> List(List(uri1, uri2)))
        _ <- Ref1.int1_(10).Nss.*?(Ns.uri.d1).get.map(_ ==> List(List(uri2, uri1)))

        _ <- Ref1.int1.Nss.*(Ns.bigInt) insert List((11, List(bigInt1, bigInt2)))
        _ <- Ref1.int1_(11).Nss.*?(Ns.bigInt.a1).get.map(_ ==> List(List(bigInt1, bigInt2)))
        _ <- Ref1.int1_(11).Nss.*?(Ns.bigInt.d1).get.map(_ ==> List(List(bigInt2, bigInt1)))

        _ <- Ref1.int1.Nss.*(Ns.bigDec) insert List((12, List(bigDec1, bigDec2)))
        _ <- Ref1.int1_(12).Nss.*?(Ns.bigDec.a1).get.map(_ ==> List(List(bigDec1, bigDec2)))
        _ <- Ref1.int1_(12).Nss.*?(Ns.bigDec.d1).get.map(_ ==> List(List(bigDec2, bigDec1)))
      } yield ()
    }


    "OptNested type, optional" - core { implicit conn =>
      for {
        _ <- Ref1.int1.Nss.*(Ns.int.str$) insert List((1, List(
          (1, Some("a")),
          (2, Some("b")),
          (3, None))))
        _ <- Ref1.int1_(1).Nss.*?(Ns.int.a2.str$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some("a")),
          (2, Some("b")))))
        _ <- Ref1.int1_(1).Nss.*?(Ns.int.d2.str$.d1).get.map(_ ==> List(List(
          (2, Some("b")),
          (1, Some("a")),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.int.enumm$) insert List((2, List(
          (1, Some("enum1")),
          (2, Some("enum2")),
          (3, None))))
        _ <- Ref1.int1_(2).Nss.*?(Ns.int.a2.enumm$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some("enum1")),
          (2, Some("enum2")))))
        _ <- Ref1.int1_(2).Nss.*?(Ns.int.d2.enumm$.d1).get.map(_ ==> List(List(
          (2, Some("enum2")),
          (1, Some("enum1")),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.str.int$) insert List((3, List(
          ("a", Some(1)),
          ("b", Some(2)),
          ("c", None))))
        _ <- Ref1.int1_(3).Nss.*?(Ns.str.a2.int$.a1).get.map(_ ==> List(List(
          ("c", None),
          ("a", Some(1)),
          ("b", Some(2)))))
        _ <- Ref1.int1_(3).Nss.*?(Ns.str.d2.int$.d1).get.map(_ ==> List(List(
          ("b", Some(2)),
          ("a", Some(1)),
          ("c", None))))

        _ <- Ref1.int1.Nss.*(Ns.int.long$) insert List((4, List(
          (1, Some(10L)),
          (2, Some(20L)),
          (3, None))))
        _ <- Ref1.int1_(4).Nss.*?(Ns.int.a2.long$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some(10L)),
          (2, Some(20L)))))
        _ <- Ref1.int1_(4).Nss.*?(Ns.int.d2.long$.d1).get.map(_ ==> List(List(
          (2, Some(20L)),
          (1, Some(10L)),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.int.ref1$) insert List((5, List(
          (1, Some(10L)),
          (2, Some(20L)),
          (3, None))))
        _ <- Ref1.int1_(5).Nss.*?(Ns.int.a2.ref1$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some(10L)),
          (2, Some(20L)))))
        _ <- Ref1.int1_(5).Nss.*?(Ns.int.d2.ref1$.d1).get.map(_ ==> List(List(
          (2, Some(20L)),
          (1, Some(10L)),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.int.double$) insert List((6, List(
          (1, Some(1.1)),
          (2, Some(2.2)),
          (3, None))))
        _ <- Ref1.int1_(6).Nss.*?(Ns.int.a2.double$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some(1.1)),
          (2, Some(2.2)))))
        _ <- Ref1.int1_(6).Nss.*?(Ns.int.d2.double$.d1).get.map(_ ==> List(List(
          (2, Some(2.2)),
          (1, Some(1.1)),
          (3, None))))

        //        // Todo: a bug in Datomic pulls nil/null instead of the false value
        //        _ <- Ref1.int1.Nss.*(Ns.int.bool$) insert List((7, List(
        //          (1, Some(false)),
        //          (2, Some(true)),
        //          (3, None))))
        //        _ <- Ref1.int1_(7).Nss.*?(Ns.int.a2.bool$.a1).get.map(_ ==> List(List(
        //          (3, None),
        //          (1, Some(false)),
        //          (2, Some(true)))))
        //        _ <- Ref1.int1_(7).Nss.*?(Ns.int.d2.bool$.d1).get.map(_ ==> List(List(
        //          (2, Some(true)),
        //          (1, Some(false)),
        //          (3, None))))

        // .. optional `true` value works as expected
        _ <- Ref1.int1.Nss.*(Ns.int.bool$) insert List((7, List(
          (2, Some(true)),
          (3, None))))
        _ <- Ref1.int1_(7).Nss.*?(Ns.int.a2.bool$.a1).get.map(_ ==> List(List(
          (3, None),
          (2, Some(true)))))
        _ <- Ref1.int1_(7).Nss.*?(Ns.int.d2.bool$.d1).get.map(_ ==> List(List(
          (2, Some(true)),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.int.date$) insert List((8, List(
          (1, Some(date1)),
          (2, Some(date2)),
          (3, None))))
        _ <- Ref1.int1_(8).Nss.*?(Ns.int.a2.date$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some(date1)),
          (2, Some(date2)))))
        _ <- Ref1.int1_(8).Nss.*?(Ns.int.d2.date$.d1).get.map(_ ==> List(List(
          (2, Some(date2)),
          (1, Some(date1)),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.int.uuid$) insert List((9, List(
          (1, Some(uuid1)),
          (2, Some(uuid2)),
          (3, None))))
        _ <- Ref1.int1_(9).Nss.*?(Ns.int.a2.uuid$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some(uuid1)),
          (2, Some(uuid2)))))
        _ <- Ref1.int1_(9).Nss.*?(Ns.int.d2.uuid$.d1).get.map(_ ==> List(List(
          (2, Some(uuid2)),
          (1, Some(uuid1)),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.int.uri$) insert List((10, List(
          (1, Some(uri1)),
          (2, Some(uri2)),
          (3, None))))
        _ <- Ref1.int1_(10).Nss.*?(Ns.int.a2.uri$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some(uri1)),
          (2, Some(uri2)))))
        _ <- Ref1.int1_(10).Nss.*?(Ns.int.d2.uri$.d1).get.map(_ ==> List(List(
          (2, Some(uri2)),
          (1, Some(uri1)),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.int.bigInt$) insert List((11, List(
          (1, Some(bigInt1)),
          (2, Some(bigInt2)),
          (3, None))))
        _ <- Ref1.int1_(11).Nss.*?(Ns.int.a2.bigInt$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some(bigInt1)),
          (2, Some(bigInt2)))))
        _ <- Ref1.int1_(11).Nss.*?(Ns.int.d2.bigInt$.d1).get.map(_ ==> List(List(
          (2, Some(bigInt2)),
          (1, Some(bigInt1)),
          (3, None))))

        _ <- Ref1.int1.Nss.*(Ns.int.bigDec$) insert List((12, List(
          (1, Some(bigDec1)),
          (2, Some(bigDec2)),
          (3, None))))
        _ <- Ref1.int1_(12).Nss.*?(Ns.int.a2.bigDec$.a1).get.map(_ ==> List(List(
          (3, None),
          (1, Some(bigDec1)),
          (2, Some(bigDec2)))))
        _ <- Ref1.int1_(12).Nss.*?(Ns.int.d2.bigDec$.d1).get.map(_ ==> List(List(
          (2, Some(bigDec2)),
          (1, Some(bigDec1)),
          (3, None))))
      } yield ()
    }


    "Basic" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1) insert List(
          ("A", List(1, 2)),
          ("B", List(1, 2)),
        )

        _ <- Ns.str.Refs1.*(Ref1.int1).get.map(_ ==> List(
          ("A", List(1, 2)),
          ("B", List(1, 2)),
        ))

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).get.map(_ ==> List(
          ("A", List(1, 2)),
          ("B", List(1, 2)),
        ))
        _ <- Ns.str.a1.Refs1.*(Ref1.int1.d1).get.map(_ ==> List(
          ("A", List(2, 1)),
          ("B", List(2, 1)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.a1).get.map(_ ==> List(
          ("B", List(1, 2)),
          ("A", List(1, 2)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d1).get.map(_ ==> List(
          ("B", List(2, 1)),
          ("A", List(2, 1)),
        ))

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "A",
            |        "Refs1": [
            |          {
            |            "int1": 1
            |          },
            |          {
            |            "int1": 2
            |          }
            |        ]
            |      },
            |      {
            |        "str": "B",
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
            |}""".stripMargin
        )

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.d1).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "A",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str": "B",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- Ns.str.d1.Refs1.*(Ref1.int1.a1).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "B",
            |        "Refs1": [
            |          {
            |            "int1": 1
            |          },
            |          {
            |            "int1": 2
            |          }
            |        ]
            |      },
            |      {
            |        "str": "A",
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
            |}""".stripMargin
        )

        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d1).getJson.map(_ ==>
          """{
            |  "totalCount": 2,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "B",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str": "A",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )


        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o1.str ==> "A"
          o1.Refs1.head.int1 ==> 1
          o1.Refs1.last.int1 ==> 2

          o2.str ==> "B"
          o2.Refs1.head.int1 ==> 1
          o2.Refs1.last.int1 ==> 2
        }

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o1.str ==> "A"
          o1.Refs1.last.int1 ==> 2
          o1.Refs1.head.int1 ==> 1

          o2.str ==> "B"
          o2.Refs1.last.int1 ==> 2
          o2.Refs1.head.int1 ==> 1
        }

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o2.str ==> "B"
          o2.Refs1.head.int1 ==> 1
          o2.Refs1.last.int1 ==> 2

          o1.str ==> "A"
          o1.Refs1.head.int1 ==> 1
          o1.Refs1.last.int1 ==> 2
        }

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getObjs.collect { case List(o1, o2) =>
          o2.str ==> "B"
          o2.Refs1.last.int1 ==> 2
          o2.Refs1.head.int1 ==> 1

          o1.str ==> "A"
          o1.Refs1.last.int1 ==> 2
          o1.Refs1.head.int1 ==> 1
        }
      } yield ()
    }


    "Options" - core { implicit conn =>
      for {
        _ <- Ns.int.str$.Refs1.*(Ref1.int1.str1$) insert List(
          (1, Some("A"), List(
            (1, Some("a")),
            (1, Some("b")),
            (2, Some("a")),
            (2, Some("b")),
            (2, None),
          )),
          (2, Some("B"), List(
            (1, Some("a")),
            (1, Some("b")),
            (2, Some("a")),
            (2, Some("b")),
          )),
          (3, Some("C"), List()),
          (4, None, List()),
        )

        // a1 *? a1/a2
        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.a1.str1$.a2).get.map(_ ==> List(
          (4, None, List()),
          (1, Some("A"), List(
            (1, Some("a")),
            (1, Some("b")),
            (2, None),
            (2, Some("a")),
            (2, Some("b")),
          )),
          (2, Some("B"), List(
            (1, Some("a")),
            (1, Some("b")),
            (2, Some("a")),
            (2, Some("b")),
          )),
          (3, Some("C"), List()),
        ))

        // a1 *? a1/d2
        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.a1.str1$.d2).get.map(_ ==> List(
          (4, None, List()),
          (1, Some("A"), List(
            (1, Some("b")),
            (1, Some("a")),
            (2, Some("b")),
            (2, Some("a")),
            (2, None),
          )),
          (2, Some("B"), List(
            (1, Some("b")),
            (1, Some("a")),
            (2, Some("b")),
            (2, Some("a")),
          )),
          (3, Some("C"), List()),
        ))

        // a1 *? d1/a2
        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.d1.str1$.a2).get.map(_ ==> List(
          (4, None, List()),
          (1, Some("A"), List(
            (2, None),
            (2, Some("a")),
            (2, Some("b")),
            (1, Some("a")),
            (1, Some("b")),
          )),
          (2, Some("B"), List(
            (2, Some("a")),
            (2, Some("b")),
            (1, Some("a")),
            (1, Some("b")),
          )),
          (3, Some("C"), List()),
        ))

        // a1 *? d1/d2
        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.d1.str1$.d2).get.map(_ ==> List(
          (4, None, List()),
          (1, Some("A"), List(
            (2, Some("b")),
            (2, Some("a")),
            (2, None),
            (1, Some("b")),
            (1, Some("a")),
          )),
          (2, Some("B"), List(
            (2, Some("b")),
            (2, Some("a")),
            (1, Some("b")),
            (1, Some("a")),
          )),
          (3, Some("C"), List()),
        ))

        // d1 *? d1/d2
        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d1.str1$.d2).get.map(_ ==> List(
          (3, Some("C"), List()),
          (2, Some("B"), List(
            (2, Some("b")),
            (2, Some("a")),
            (1, Some("b")),
            (1, Some("a")),
          )),
          (1, Some("A"), List(
            (2, Some("b")),
            (2, Some("a")),
            (2, None),
            (1, Some("b")),
            (1, Some("a")),
          )),
          (4, None, List()),
        ))

        // d1 *? d2/d1
        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d2.str1$.d1).get.map(_ ==> List(
          (3, Some("C"), List()),
          (2, Some("B"), List(
            (2, Some("b")),
            (1, Some("b")),
            (2, Some("a")),
            (1, Some("a")),
          )),
          (1, Some("A"), List(
            (2, Some("b")),
            (1, Some("b")),
            (2, Some("a")),
            (1, Some("a")),
            (2, None),
          )),
          (4, None, List()),
        ))

        // d1 *? d2/d1
        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d2.str1$.d1).getJson.map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 4,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "str$": "C",
            |        "Refs1": []
            |      },
            |      {
            |        "int": 2,
            |        "str$": "B",
            |        "Refs1": [
            |          {
            |            "int1": 2,
            |            "str1$": "b"
            |          },
            |          {
            |            "int1": 1,
            |            "str1$": "b"
            |          },
            |          {
            |            "int1": 2,
            |            "str1$": "a"
            |          },
            |          {
            |            "int1": 1,
            |            "str1$": "a"
            |          }
            |        ]
            |      },
            |      {
            |        "int": 1,
            |        "str$": "A",
            |        "Refs1": [
            |          {
            |            "int1": 2,
            |            "str1$": "b"
            |          },
            |          {
            |            "int1": 1,
            |            "str1$": "b"
            |          },
            |          {
            |            "int1": 2,
            |            "str1$": "a"
            |          },
            |          {
            |            "int1": 1,
            |            "str1$": "a"
            |          },
            |          {
            |            "int1": 2,
            |            "str1$": null
            |          }
            |        ]
            |      },
            |      {
            |        "int": 4,
            |        "str$": null,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d2.str1$.d1).getObjs.collect { case List(o1, o2, o3, o4) =>
          o1.int ==> 3
          o1.str$ ==> Some("C")
          o1.Refs1 ==> Nil

          o2.int ==> 2
          o2.str$ ==> Some("B")
          o2.Refs1(0).int1 ==> 2
          o2.Refs1(0).str1$ ==> Some("b")
          o2.Refs1(1).int1 ==> 1
          o2.Refs1(1).str1$ ==> Some("b")
          o2.Refs1(2).int1 ==> 2
          o2.Refs1(2).str1$ ==> Some("a")
          o2.Refs1(3).int1 ==> 1
          o2.Refs1(3).str1$ ==> Some("a")

          o3.int ==> 1
          o3.str$ ==> Some("A")
          o3.Refs1(0).int1 ==> 2
          o3.Refs1(0).str1$ ==> Some("b")
          o3.Refs1(1).int1 ==> 1
          o3.Refs1(1).str1$ ==> Some("b")
          o3.Refs1(2).int1 ==> 2
          o3.Refs1(2).str1$ ==> Some("a")
          o3.Refs1(3).int1 ==> 1
          o3.Refs1(3).str1$ ==> Some("a")
          o3.Refs1(4).int1 ==> 2
          o3.Refs1(4).str1$ ==> None

          o4.int ==> 4
          o4.str$ ==> None
          o4.Refs1 ==> Nil
        }

        // d1(expr) *? d2/d1
        _ <- Ns.int.str.>("A").d1.Refs1.*?(Ref1.int1.d2.str1$.d1).get.map(_ ==> List(
          (3, "C", List()),
          (2, "B", List(
            (2, Some("b")),
            (1, Some("b")),
            (2, Some("a")),
            (1, Some("a")),
          ))
        ))

        // expr+d1 * a2/d1(expr)
        _ <- Ns.int.<=(2).str.d1.Refs1.*(Ref1.int1.a2.str1.>=("a").d1).get.map(_ ==> List(
          (2, "B", List(
            (1, "b"),
            (2, "b"),
            (1, "a"),
            (2, "a"),
          )),
          (1, "A", List(
            (1, "b"),
            (2, "b"),
            (1, "a"),
            (2, "a"),
          )),
        ))
      } yield ()
    }


    "2 sub levels" - core { implicit conn =>
      for {
        _ <- Ns.int.str$.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.str2)) insert List(
          (1, Some("A"), List(
            (1, Some("a"), List("x", "y")),
            (1, Some("b"), List("x", "y")),
            (2, Some("a"), List("x", "y")),
            (2, Some("b"), Nil),
            (2, None, List("x", "y")),
          )),
          (2, Some("B"), List(
            (1, Some("a"), List("x", "y")),
            (1, Some("b"), List("x", "y")),
            (2, Some("a"), List("x", "y")),
            (2, Some("b"), List("x", "y")),
          )),
          (3, Some("C"), Nil),
          (4, None, Nil),
        )


        _ <- Ns.int.str$.a1.Refs1.*?(Ref1.int1.d2.str1$.a1.Refs2.*?(Ref2.str2.d1)).get.map(_ ==> List(
          (4, None, Nil),
          (1, Some("A"), List(
            (2, None, List("y", "x")),
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (2, Some("b"), Nil),
            (1, Some("b"), List("y", "x")),
          )),
          (2, Some("B"), List(
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (2, Some("b"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
          (3, Some("C"), Nil),
        ))

        _ <- Ns.int.a1.str$.Refs1.*(Ref1.int1.d2.str1$.a1.Refs2.*(Ref2.str2.d1)).get.map(_ ==> List(
          (1, Some("A"), List(
            (2, None, List("y", "x")),
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
          (2, Some("B"), List(
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (2, Some("b"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
        ))

        _ <- Ns.int.str$.a1.Refs1.*(Ref1.int1.d2.str1$.a1.Refs2.*(Ref2.str2.d1)).get.map(_ ==> List(
          (1, Some("A"), List(
            (2, None, List("y", "x")),
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
          (2, Some("B"), List(
            (2, Some("a"), List("y", "x")),
            (1, Some("a"), List("y", "x")),
            (2, Some("b"), List("y", "x")),
            (1, Some("b"), List("y", "x")),
          )),
        ))
      } yield ()
    }
  }
}
