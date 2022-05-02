package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.annotation.nowarn

// Pagination for the json api is basically implemented as for tuples. So we only test a
// few things here. All tuple tests should be convertible to json tests.

object CursorPaginationJson extends AsyncTestSuite {
  val x = ""

  @nowarn lazy val tests = Tests {

    "Basic" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3, 4, 5)

        _ <- Ns.int.a1.getJson.map(_ ==>
          """{
            |  "totalCount": 5,
            |  "limit"     : 5,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1
            |      },
            |      {
            |        "int": 2
            |      },
            |      {
            |        "int": 3
            |      },
            |      {
            |        "int": 4
            |      },
            |      {
            |        "int": 5
            |      }
            |    ]
            |  }
            |}""".stripMargin
        )

        c <- Ns.int.a1.getJson(2, x).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : 2,
               |  "more"  : 3,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 1
               |      },
               |      {
               |        "int": 2
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 3
          cursor
        }

        c <- Ns.int.a1.getJson(2, c).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : 2,
               |  "more"  : 1,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 3
               |      },
               |      {
               |        "int": 4
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 1
          cursor
        }

        c <- Ns.int.a1.getJson(2, c).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : 2,
               |  "more"  : 0,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 5
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 0
          cursor
        }

        c <- Ns.int.a1.getJson(-2, c).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : -2,
               |  "more"  : 2,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 3
               |      },
               |      {
               |        "int": 4
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 2
          cursor
        }

        _ <- Ns.int.a1.getJson(-2, c).map { case (json, cursor, more) =>
          json ==>
            s"""{
               |  "cursor": "$cursor",
               |  "limit" : -2,
               |  "more"  : 0,
               |  "data": {
               |    "Ns": [
               |      {
               |        "int": 1
               |      },
               |      {
               |        "int": 2
               |      }
               |    ]
               |  }
               |}""".stripMargin
          more ==> 0
        }
      } yield ()
    }


    "Time" - {

      "AsOf" - core { implicit conn =>
        for {
          t <- Ns.int.insert(1, 2, 3, 4, 5).map(_.t)
          _ <- Ns.int(6).save
          
          _ <- Ns.int.a1.getJsonAsOf(t).map(_ ==>
            """{
              |  "totalCount": 5,
              |  "limit"     : 5,
              |  "offset"    : 0,
              |  "data": {
              |    "Ns": [
              |      {
              |        "int": 1
              |      },
              |      {
              |        "int": 2
              |      },
              |      {
              |        "int": 3
              |      },
              |      {
              |        "int": 4
              |      },
              |      {
              |        "int": 5
              |      }
              |    ]
              |  }
              |}""".stripMargin
          )

          c <- Ns.int.a1.getJsonAsOf(t, 2, x).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 3,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 1
                 |      },
                 |      {
                 |        "int": 2
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 3
            cursor
          }

          c <- Ns.int.a1.getJsonAsOf(t, 2, c).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 1,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 3
                 |      },
                 |      {
                 |        "int": 4
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 1
            cursor
          }

          c <- Ns.int.a1.getJsonAsOf(t, 2, c).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 0,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 5
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 0
            cursor
          }

          c <- Ns.int.a1.getJsonAsOf(t, -2, c).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : -2,
                 |  "more"  : 2,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 3
                 |      },
                 |      {
                 |        "int": 4
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 2
            cursor
          }

          _ <- Ns.int.a1.getJsonAsOf(t, -2, c).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : -2,
                 |  "more"  : 0,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 1
                 |      },
                 |      {
                 |        "int": 2
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 0
          }
        } yield ()
      }

      
      "Since" - core { implicit conn =>
        for {
          t <- Ns.int(6).save.map(_.t)
          _ <- Ns.int.insert(1, 2, 3, 4, 5)
          
          _ <- Ns.int.a1.getJsonSince(t).map(_ ==>
            """{
              |  "totalCount": 5,
              |  "limit"     : 5,
              |  "offset"    : 0,
              |  "data": {
              |    "Ns": [
              |      {
              |        "int": 1
              |      },
              |      {
              |        "int": 2
              |      },
              |      {
              |        "int": 3
              |      },
              |      {
              |        "int": 4
              |      },
              |      {
              |        "int": 5
              |      }
              |    ]
              |  }
              |}""".stripMargin
          )

          c <- Ns.int.a1.getJsonSince(t, 2, x).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 3,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 1
                 |      },
                 |      {
                 |        "int": 2
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 3
            cursor
          }

          c <- Ns.int.a1.getJsonSince(t, 2, c).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 1,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 3
                 |      },
                 |      {
                 |        "int": 4
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 1
            cursor
          }

          c <- Ns.int.a1.getJsonSince(t, 2, c).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 0,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 5
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 0
            cursor
          }

          c <- Ns.int.a1.getJsonSince(t, -2, c).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : -2,
                 |  "more"  : 2,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 3
                 |      },
                 |      {
                 |        "int": 4
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 2
            cursor
          }

          _ <- Ns.int.a1.getJsonSince(t, -2, c).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : -2,
                 |  "more"  : 0,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 1
                 |      },
                 |      {
                 |        "int": 2
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 0
          }
        } yield ()
      }

      
      "With" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2, 3)

          moreData = Ns.int.getInsertStmts(4, 5)
          _ <- Ns.int.a1.getJsonWith(moreData).map(_ ==>
            """{
              |  "totalCount": 5,
              |  "limit"     : 5,
              |  "offset"    : 0,
              |  "data": {
              |    "Ns": [
              |      {
              |        "int": 1
              |      },
              |      {
              |        "int": 2
              |      },
              |      {
              |        "int": 3
              |      },
              |      {
              |        "int": 4
              |      },
              |      {
              |        "int": 5
              |      }
              |    ]
              |  }
              |}""".stripMargin
          )

          c <- Ns.int.a1.getJsonWith(2, x, moreData).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 3,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 1
                 |      },
                 |      {
                 |        "int": 2
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 3
            cursor
          }

          c <- Ns.int.a1.getJsonWith(2, c, moreData).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 1,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 3
                 |      },
                 |      {
                 |        "int": 4
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 1
            cursor
          }

          c <- Ns.int.a1.getJsonWith(2, c, moreData).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : 2,
                 |  "more"  : 0,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 5
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 0
            cursor
          }

          c <- Ns.int.a1.getJsonWith(-2, c, moreData).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : -2,
                 |  "more"  : 2,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 3
                 |      },
                 |      {
                 |        "int": 4
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 2
            cursor
          }

          _ <- Ns.int.a1.getJsonWith(-2, c, moreData).map { case (json, cursor, more) =>
            json ==>
              s"""{
                 |  "cursor": "$cursor",
                 |  "limit" : -2,
                 |  "more"  : 0,
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "int": 1
                 |      },
                 |      {
                 |        "int": 2
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
            more ==> 0
          }
        } yield ()
      }

      // Pagination for getHistory is not implemented.
    }
  }
}