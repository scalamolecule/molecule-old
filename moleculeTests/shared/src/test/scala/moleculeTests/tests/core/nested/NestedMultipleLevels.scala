package moleculeTests.tests.core.nested

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object NestedMultipleLevels extends AsyncTestSuite {

  lazy val tests = Tests {

    "Optional/mandatory" - core { implicit conn =>
      // Can't mix mandatory/optional nested structures

      expectCompileError("""m(Ns.str.Refs1.*?(Ref1.int1.Refs2.*(Ref2.int2)))""",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "Optional nested structure can't be mixed with mandatory nested structure.")

      expectCompileError("""m(Ns.str.Refs1.*(Ref1.int1.Refs2.*?(Ref2.int2)))""",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "Optional nested structure can't be mixed with mandatory nested structure.")
    }


    "2 levels" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)) insert List(
          ("a", List(
            (1, List(11, 12)),
            (2, List())
          )),
          ("b", List())
        )

        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)).get.map(_ ==> List(
          ("a", List(
            (1, List(11, 12))
          ))
        ))

        _ <- Ns.str.Refs1.*?(Ref1.int1.Refs2.*?(Ref2.int2)).get.map(_.sortBy(_._1) ==> List(
          ("a", List(
            (1, List(11, 12)),
            (2, List())
          )),
          ("b", List())
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.Refs2.*?(Ref2.int2)).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 1,
            |            "Refs2": [
            |              {
            |                "int2": 11
            |              },
            |              {
            |                "int2": 12
            |              }
            |            ]
            |          },
            |          {
            |            "int1": 2,
            |            "Refs2": []
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.str.Refs1.*?(Ref1.int1).get.map(_.sortBy(_._1) ==> List(
          ("a", List(1, 2)),
          ("b", List())
        ))

        _ <- Ref1.int1.Refs2.*?(Ref2.int2).get.map(_.sortBy(_._1) ==> List(
          (1, List(11, 12)),
          (2, List())
        ))
      } yield ()
    }


    "2 levels, opt attrs" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.int2.str2$)) insert List(
          ("A", List(
            (1, Some("a1"), List(
              (11, Some("a11")),
              (12, None)
            )),
            (2, None, List(
              (21, Some("a21")),
              (22, None)
            )),
            (3, Some("a3"), List()),
            (4, None, List())
          )),
          ("B", List())
        )

        // optional - optional
        _ <- Ns.str.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.int2.str2$)).get.map(_ ==> List(
          ("A", List(
            (1, Some("a1"), List(
              (11, Some("a11")),
              (12, None)
            )),
            (2, None, List(
              (21, Some("a21")),
              (22, None)
            ))
          ))
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1$.Refs2.*?(Ref2.int2.str2$)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, Some("a1"), List(
              (11, Some("a11")),
              (12, None)
            )),
            (2, None, List(
              (21, Some("a21")),
              (22, None)
            )),
            (3, Some("a3"), List()),
            (4, None, List())
          )),
          ("B", List())
        ))

        // optional - mandatory
        _ <- Ns.str.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.int2.str2)).get.map(_ ==> List(
          ("A", List(
            (1, Some("a1"), List(
              (11, "a11")
            )),
            (2, None, List(
              (21, "a21")
            ))
          ))
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1$.Refs2.*?(Ref2.int2.str2)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, Some("a1"), List(
              (11, "a11")
            )),
            (2, None, List(
              (21, "a21")
            )),
            (3, Some("a3"), List()),
            (4, None, List())
          )),
          ("B", List())
        ))

        // optional - tacit
        _ <- Ns.str.Refs1.*(Ref1.int1.str1$.Refs2.*(Ref2.int2.str2_)).get.map(_ ==> List(
          ("A", List(
            (1, Some("a1"), List(11)),
            (2, None, List(21))
          ))
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1$.Refs2.*?(Ref2.int2.str2_)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, Some("a1"), List(11)),
            (2, None, List(21)),
            (3, Some("a3"), List()),
            (4, None, List())
          )),
          ("B", List())
        ))

        // mandatory - optional
        _ <- Ns.str.Refs1.*(Ref1.int1.str1.Refs2.*(Ref2.int2.str2$)).get.map(_ ==> List(
          ("A", List(
            (1, "a1", List(
              (11, Some("a11")),
              (12, None)
            ))
          ))
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1.Refs2.*?(Ref2.int2.str2$)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, "a1", List(
              (11, Some("a11")),
              (12, None)
            )),
            (3, "a3", List())
          )),
          ("B", List())
        ))

        // mandatory - mandatory
        _ <- Ns.str.Refs1.*(Ref1.int1.str1.Refs2.*(Ref2.int2.str2)).get.map(_ ==> List(
          ("A", List(
            (1, "a1", List(
              (11, "a11")
            ))
          ))
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1.Refs2.*?(Ref2.int2.str2)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, "a1", List(
              (11, "a11")
            )),
            (3, "a3", List())
          )),
          ("B", List())
        ))

        // mandatory - tacit
        _ <- Ns.str.Refs1.*(Ref1.int1.str1.Refs2.*(Ref2.int2.str2_)).get.map(_ ==> List(
          ("A", List(
            (1, "a1", List(11))
          ))
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1.Refs2.*?(Ref2.int2.str2_)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, "a1", List(11)),
            (3, "a3", List())
          )),
          ("B", List())
        ))

        // tacit - optional
        _ <- Ns.str.Refs1.*(Ref1.int1.str1_.Refs2.*(Ref2.int2.str2$)).get.map(_ ==> List(
          ("A", List(
            (1, List(
              (11, Some("a11")),
              (12, None)
            ))
          ))
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1_.Refs2.*?(Ref2.int2.str2$)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, List(
              (11, Some("a11")),
              (12, None)
            )),
            (3, List())
          )),
          ("B", List())
        ))

        // tacit - mandatory
        _ <- Ns.str.Refs1.*(Ref1.int1.str1_.Refs2.*(Ref2.int2.str2)).get.map(_ ==> List(
          ("A", List(
            (1, List(
              (11, "a11")
            ))
          ))
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1_.Refs2.*?(Ref2.int2.str2)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, List(
              (11, "a11")
            )),
            (3, List())
          )),
          ("B", List())
        ))

        // tacit - tacit
        _ <- Ns.str.Refs1.*(Ref1.int1.str1_.Refs2.*(Ref2.int2.str2_)).get.map(_ ==> List(
          ("A", List(
            (1, List(11)),
          )),
        ))
        _ <- Ns.str.Refs1.*?(Ref1.int1.str1_.Refs2.*?(Ref2.int2.str2_)).get.map(_.sortBy(_._1) ==> List(
          ("A", List(
            (1, List(11)),
            (3, List())
          )),
          ("B", List())
        ))
      } yield ()
    }


    "3 levels" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2.Refs3.*(Ref3.int3))) insert List(
          ("a", List(
            (1, List(
              (11, List(111, 112)),
              (12, List())
            )),
            (2, List())
          )),
          ("b", List())
        )

        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2.Refs3.*(Ref3.int3))).get.map(_ ==> List(
          ("a", List(
            (1, List(
              (11, List(111, 112))
            ))
          ))
        ))

        _ <- Ns.str.Refs1.*?(Ref1.int1.Refs2.*?(Ref2.int2.Refs3.*?(Ref3.int3))).get.map(_.sortBy(_._1) ==> List(
          ("a", List(
            (1, List(
              (11, List(111, 112)),
              (12, List())
            )),
            (2, List())
          )),
          ("b", List())
        ))
      } yield ()
    }
  }
}