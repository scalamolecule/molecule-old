package moleculeTests

import molecule.core.util.Executor._
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.api.out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

object Adhoc extends AsyncTestSuite with Helpers with JavaUtil {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>

      for {
        conn <- futConn

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


        // d1 *? d2/d1
        _ <- Ns.int.str$.d1.Refs1.*?(Ref1.int1.d2.str1$.d1).getJson.map(_ ==>
          """{
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

      } yield ()
    }
  }
}
