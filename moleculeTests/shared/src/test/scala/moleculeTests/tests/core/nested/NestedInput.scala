package moleculeTests.tests.core.nested

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.in3_out9._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object NestedInput extends AsyncTestSuite {

  lazy val tests = Tests {

    "Input in optional nested not allowed" - core { implicit conn =>

      expectCompileError("""m(Ns.int(?).Refs1 *? Ref1.int1)""",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "Input not allowed in optional nested structures. " +
          """Found: Atom("Ns", "int", "Int", 1, Qm, None, Seq(), Seq())"""
      )

      expectCompileError("""m(Ns.int.Refs1 *? Ref1.int1(?))""",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "Input not allowed in optional nested structures. " +
          """Found: Atom("Ref1", "int1", "Int", 1, Qm, None, Seq(), Seq())"""
      )
    }

    "1 input on a level" - core { implicit conn =>
      for {
        _ <- (Ns.int.Refs1 * (Ref1.int1.Refs2 * Ref2.int2)) insert List(
          (1, List(
            (11, List(111, 112)),
            (12, List(122, 123)))),
          (5, List(
            (55, List(555, 556)),
            (56, List(566, 567))))
        )

        _ <- (Ns.int.Refs1 * (Ref1.int1.Refs2 * Ref2.int2)).get.map(_ ==> List(
          (1, List((11, List(111, 112)), (12, List(122, 123)))),
          (5, List((55, List(555, 556)), (56, List(566, 567))))
        ))

        // 1 + 0 + 0
        // Note that we need to call `m` explicitly on input molecules
        _ <- m(Ns.int(?).Refs1 * (Ref1.int1.Refs2 * Ref2.int2)).apply(1).get.map(_ ==> List(
          (1, List((11, List(111, 112)), (12, List(122, 123))))
        ))
        // 0 + 1 + 0
        _ <- m(Ns.int.Refs1 * (Ref1.int1(?).Refs2 * Ref2.int2)).apply(11).get.map(_ ==> List(
          (1, List((11, List(111, 112))))
        ))
        // 0 + 0 + 1
        _ <- m(Ns.int.Refs1 * (Ref1.int1.Refs2 * Ref2.int2(?))).apply(111).get.map(_ ==> List(
          (1, List((11, List(111))))
        ))

        // 1 + 1 + 0
        _ <- m(Ns.int(?).Refs1 * (Ref1.int1(?).Refs2 * Ref2.int2)).apply(1, 11).get.map(_ ==> List(
          (1, List((11, List(111, 112))))
        ))
        // 1 + 0 + 1
        _ <- m(Ns.int(?).Refs1 * (Ref1.int1.Refs2 * Ref2.int2(?))).apply(1, 111).get.map(_ ==> List(
          (1, List((11, List(111))))
        ))
        // 0 + 1 + 1
        _ <- m(Ns.int.Refs1 * (Ref1.int1(?).Refs2 * Ref2.int2(?))).apply(11, 111).get.map(_ ==> List(
          (1, List((11, List(111))))
        ))

        // 1 + 1 + 1
        _ <- m(Ns.int(?).Refs1 * (Ref1.int1(?).Refs2 * Ref2.int2(?))).apply(1, 11, 111).get.map(_ ==> List(
          (1, List((11, List(111))))
        ))
      } yield ()
    }

    "2 inputs on a level" - core { implicit conn =>
      for {
        _ <- (Ns.int.str.Refs1 * (Ref1.int1.str1.Refs2 * Ref2.int2.str2)) insert List(
          (1, "a", List(
            (11, "aa", List((111, "aaa"), (112, "aab"))),
            (12, "ab", List((122, "abb"), (123, "abc"))))),
          (5, "b", List(
            (55, "ee", List((555, "eee"), (556, "eef"))),
            (56, "ef", List((566, "eff"), (567, "efg")))))
        )

        // 2 + 0 + 0
        _ <- m(Ns.int(?).str(?).Refs1 * (Ref1.int1.str1.Refs2 * Ref2.int2.str2)).apply(1, "a").get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa"), (112, "aab"))),
            (12, "ab", List((122, "abb"), (123, "abc")))))
        ))

        // 0 + 2 + 0
        _ <- m(Ns.int.str.Refs1 * (Ref1.int1(?).str1(?).Refs2 * Ref2.int2.str2)).apply(11, "aa").get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa"), (112, "aab")))))
        ))

        // 0 + 0 + 2
        _ <- m(Ns.int.str.Refs1 * (Ref1.int1.str1.Refs2 * Ref2.int2(?).str2(?))).apply(111, "aaa").get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa")))))
        ))


        // 2 + 1 + 0
        _ <- m(Ns.int(?).str(?).Refs1 * (Ref1.int1(?).str1.Refs2 * Ref2.int2.str2)).apply(1, "a", 11).get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa"), (112, "aab")))))
        ))
        // 2 + 0 + 1
        _ <- m(Ns.int(?).str(?).Refs1 * (Ref1.int1.str1.Refs2 * Ref2.int2(?).str2)).apply(1, "a", 111).get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa")))))
        ))

        // 1 + 2 + 0
        _ <- m(Ns.int(?).str.Refs1 * (Ref1.int1(?).str1(?).Refs2 * Ref2.int2.str2)).apply(1, 11, "aa").get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa"), (112, "aab")))))
        ))
        // 0 + 2 + 1
        _ <- m(Ns.int.str.Refs1 * (Ref1.int1(?).str1(?).Refs2 * Ref2.int2(?).str2)).apply(11, "aa", 111).get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa")))))
        ))

        // 1 + 0 + 2
        _ <- m(Ns.int(?).str.Refs1 * (Ref1.int1.str1.Refs2 * Ref2.int2(?).str2(?))).apply(1, 111, "aaa").get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa")))))
        ))
        // 0 + 1 + 2
        _ <- m(Ns.int.str.Refs1 * (Ref1.int1(?).str1.Refs2 * Ref2.int2(?).str2(?))).apply(11, 111, "aaa").get.map(_ ==> List(
          (1, "a", List(
            (11, "aa", List((111, "aaa")))))
        ))
      } yield ()
    }

    "3 inputs on a level" - core { implicit conn =>
      for {
        _ <- (Ns.int.str.enumm.Refs1 * (Ref1.int1.str1.enum1.Refs2 * Ref2.int2.str2.enum2)) insert List(
          (1, "a", "enum1", List(
            (11, "aa", "enum11", List((111, "aaa", "enum21"), (112, "aab", "enum22"))),
            (12, "ab", "enum12", List((122, "abb", "enum21"), (123, "abc", "enum22")))))
        )

        // 3 + 0 + 0
        _ <- m(Ns.int(?).str(?).enumm(?).Refs1 * (Ref1.int1.str1.enum1.Refs2 * Ref2.int2.str2.enum2)).apply(1, "a", "enum1").get.map(_ ==> List(
          (1, "a", "enum1", List(
            (11, "aa", "enum11", List((111, "aaa", "enum21"), (112, "aab", "enum22"))),
            (12, "ab", "enum12", List((122, "abb", "enum21"), (123, "abc", "enum22")))))
        ))
        // 0 + 3 + 0
        _ <- m(Ns.int.str.enumm.Refs1 * (Ref1.int1(?).str1(?).enum1(?).Refs2 * Ref2.int2.str2.enum2)).apply(11, "aa", "enum11").get.map(_ ==> List(
          (1, "a", "enum1", List(
            (11, "aa", "enum11", List((111, "aaa", "enum21"), (112, "aab", "enum22")))))
        ))
        // 0 + 0 + 3
        _ <- m(Ns.int.str.enumm.Refs1 * (Ref1.int1.str1.enum1.Refs2 * Ref2.int2(?).str2(?).enum2(?))).apply(111, "aaa", "enum21").get.map(_ ==> List(
          (1, "a", "enum1", List(
            (11, "aa", "enum11", List((111, "aaa", "enum21")))))
        ))
      } yield ()
    }
  }
}