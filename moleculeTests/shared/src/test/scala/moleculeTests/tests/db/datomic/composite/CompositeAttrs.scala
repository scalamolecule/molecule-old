package moleculeTests.tests.db.datomic.composite

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out6._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object CompositeAttrs extends AsyncTestSuite {

  lazy val tests = Tests {

    "tacits" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2$ + Ref1.int1.str1$ + Ns.int.str$ insert Seq(
          ((1, Some("a")), (11, Some("aa")), (111, Some("aaa"))),
          ((2, Some("b")), (22, Some("bb")), (222, None)),
          ((3, Some("c")), (33, None), (333, None)),
          ((4, None), (44, None), (444, None)),
        )

        // Same namespace

        _ <- Ref2.int2.a1.str2.get.map(_ ==> List(
          (1, "a"),
          (2, "b"),
          (3, "c")
        ))
        // When 1 + 1 attribute, this outcome will be the same
        _ <- (Ref2.int2.a1 + Ref2.str2).get.map(_ ==> List(
          (1, "a"),
          (2, "b"),
          (3, "c")
        ))

        _ <- Ref2.int2.a1.get.map(_ ==> List(1, 2, 3, 4))

        _ <- (Ref2.int2.a1 + Ref2.str2_).get.map(_ ==> List(1, 2, 3))
        // Order irrelevant
        _ <- (Ref2.str2_ + Ref2.int2.a1).get.map(_ ==> List(1, 2, 3))

        _ <- (Ref1.int1.a1 + Ref1.str1_).get.map(_ ==> List(11, 22))

        _ <- (Ns.int + Ns.str_).get.map(_ ==> List(111))


        // 2 namespaces, 1 tacit

        _ <- (Ref2.int2.a1 + Ref1.str1_).get.map(_ ==> List(1, 2))
        _ <- (Ref2.int2 + Ns.str_).get.map(_ ==> List(1))
        _ <- (Ref1.int1 + Ns.str_).get.map(_ ==> List(11))


        // 3 namespaces, 2 tacits

        _ <- (Ref2.int2 + Ref1.str1_ + Ns.str_).get.map(_ ==> List(1))
        _ <- (Ref2.str2_ + Ref1.int1 + Ns.str_).get.map(_ ==> List(11))
        _ <- (Ref2.str2_ + Ref1.str1_ + Ns.int.a1).get.map(_ ==> List(111, 222))


        // 3 namespaces, 3 tacits, 4 composite parts (to test second `+` method)

        _ <- (Ref2.int2 + Ref1.str1_ + Ns.int_ + Ns.str_).get.map(_ ==> List(1))
        _ <- (Ref2.str2_ + Ref1.int1 + Ns.int_ + Ns.str_).get.map(_ ==> List(11))
        _ <- (Ref2.str2_ + Ref1.str1_ + Ns.int + Ns.str_).get.map(_ ==> List(111))
        _ <- (Ref2.str2_ + Ref1.str1_ + Ns.int_ + Ns.str).get.map(_ ==> List("aaa"))
      } yield ()
    }

    "Duplicate attributes" - {

      "Duplicate attrs/refs with same entity on top-level not allowed" - core { implicit conn =>
        // 1 + 1 attr
        expectCompileError("""m(Ns.int + Ns.int)""",
          "molecule.core.ops.exception.VerifyRawModelException: " +
            "Composite molecules can't contain the same attribute more than once. Found multiple instances of `:Ns/int`")

        // 0 + 2 attr
        expectCompileError("""m(Ns.int + Ns.str.str)""",
          "molecule.core.ops.exception.VerifyRawModelException: " +
            "Composite molecules can't contain the same attribute more than once. Found multiple instances of `:Ns/str`")

        // 1 + 1 ref
        expectCompileError("""m(Ns.bool.Ref1.int1 + Ns.str.Ref1.int1)""",
          "molecule.core.ops.exception.VerifyRawModelException: " +
            "Composite molecules can't contain the same ref more than once. Found multiple instances of `:Ns/ref1`")

        // 0 + 2 attr after backref
        expectCompileError("""m(Ns.int + Ref1.int1.Ref2.int2._Ref1.int1)""",
          "molecule.core.ops.exception.VerifyRawModelException: " +
            "Composite molecules can't contain the same attribute more than once. Found multiple instances of `:Ref1/int1`")

        // 0 + 2 ref after backref
        expectCompileError("""m(Ns.int + Ref1.int1.Ref2.int2._Ref1.str1.Ref2.str2)""",
          "molecule.core.ops.exception.VerifyRawModelException: " +
            "Composite molecules can't contain the same ref more than once. Found multiple instances of `:Ref1/ref2`")
      }

      "Duplicate attrs/refs with same entity within sub-molecule not allowed" - core { implicit conn =>
        // 2 attr
        expectCompileError("""m(Ref1.int1 + Ns.int.Ref1.int1.int1)""",
          "molecule.core.ops.exception.VerifyRawModelException: " +
            "Composite sub-molecules can't contain the same attribute more than once. Found multiple instances of `:Ref1/int1`")

        // 2 attr after backref
        expectCompileError("""m(Ref1.int1 + Ns.int.Ref1.int1.Ref2.int2._Ref1.int1)""",
          "molecule.core.ops.exception.VerifyRawModelException: " +
            "Composite sub-molecules can't contain the same attribute more than once. Found multiple instances of `:Ref1/int1`")

        // 2 ref
        expectCompileError("""m(Ref1.int1 + Ns.int.Ref1.int1.Ref2.int2._Ref1.str1.Ref2.str2)""",
          "molecule.core.ops.exception.VerifyRawModelException: " +
            "Composite sub-molecules can't contain the same ref more than once. Found multiple instances of `:Ref1/ref2`")

      }

      "Twice on different levels ok" - core { implicit conn =>
        for {
          // Okay to repeat attribute in _referenced_ namespace
          List(e1, r11, e2, r22) <- Ref1.int1 + Ns.str.Ref1.int1 insert Seq(
            (1, ("aa", 11)),
            (2, ("bb", 22))
          ) map(_.eids)

          _ <- Ns.str.a1.get.map(_ ==> Seq("aa", "bb"))
          _ <- Ns.str.a1.Ref1.int1.get.map(_ ==> Seq(("aa", 11), ("bb", 22)))

          // Note how 4 Ref1.int1 values have been inserted!
          _ <- Ref1.int1.a1.get.map(_ ==> Seq(1, 2, 11, 22))

          // Composite query
          _ <- (Ref1.int1.a1 + Ns.str.Ref1.int1).get.map(_ ==> Seq(
            (1, ("aa", 11)),
            (2, ("bb", 22))
          ))
        } yield ()
      }
    }
  }
}