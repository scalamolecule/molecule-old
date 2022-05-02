package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.annotation.nowarn

// Pagination for the nested json api is basically implemented as for nested tuples.
// So we only test a few things here. All tuple tests should be convertible to object tests.

object CursorPaginationJsonNested extends AsyncTestSuite {
  val x = ""

  @nowarn lazy val tests = Tests {

    "Basic" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, List(51, 52)),
        )

        c <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(2, x).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : 2,
               |  "more"  : 3,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 1,
               |        "Refs1": [
               |          {
               |            "int1": 11
               |          },
               |          {
               |            "int1": 12
               |          }
               |        ]
               |      },
               |      {
               |        "int": 2,
               |        "Refs1": [
               |          {
               |            "int1": 21
               |          },
               |          {
               |            "int1": 22
               |          }
               |        ]
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 3
          cursor
        }

        c <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(2, c).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : 2,
               |  "more"  : 1,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 3,
               |        "Refs1": [
               |          {
               |            "int1": 31
               |          },
               |          {
               |            "int1": 32
               |          }
               |        ]
               |      },
               |      {
               |        "int": 4,
               |        "Refs1": [
               |          {
               |            "int1": 41
               |          },
               |          {
               |            "int1": 42
               |          }
               |        ]
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 1
          cursor
        }

        c <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(2, c).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : 2,
               |  "more"  : 0,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 5,
               |        "Refs1": [
               |          {
               |            "int1": 51
               |          },
               |          {
               |            "int1": 52
               |          }
               |        ]
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 0
          cursor
        }

        c <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(-2, c).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : -2,
               |  "more"  : 2,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 3,
               |        "Refs1": [
               |          {
               |            "int1": 31
               |          },
               |          {
               |            "int1": 32
               |          }
               |        ]
               |      },
               |      {
               |        "int": 4,
               |        "Refs1": [
               |          {
               |            "int1": 41
               |          },
               |          {
               |            "int1": 42
               |          }
               |        ]
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 2
          cursor
        }

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(-2, c).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : -2,
               |  "more"  : 0,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 1,
               |        "Refs1": [
               |          {
               |            "int1": 11
               |          },
               |          {
               |            "int1": 12
               |          }
               |        ]
               |      },
               |      {
               |        "int": 2,
               |        "Refs1": [
               |          {
               |            "int1": 21
               |          },
               |          {
               |            "int1": 22
               |          }
               |        ]
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 0
        }
      } yield ()
    }
  }
}