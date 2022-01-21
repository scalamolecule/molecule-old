package moleculeTests.tests.core.nested

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out2._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object NestedExpression extends AsyncTestSuite {

  val one = 1

  lazy val tests = Tests {

    "Before nested" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1) insert List(
          (1, List(1, 2)),
          (2, List(2, 3)),
          (3, List())
        )

        _ <- m(Ns.int.>(1).Refs1 * Ref1.int1).get.map(_ ==> List(
          (2, List(2, 3)),
        ))
        _ <- m(Ns.int.>(1).Refs1 *? Ref1.int1).get.map(_.sortBy(_._1) ==> List(
          (2, List(2, 3)),
          (3, List())
        ))

        // Using variable
        _ <- m(Ns.int.>(one).Refs1 * Ref1.int1).get.map(_ ==> List(
          (2, List(2, 3))
        ))
        _ <- m(Ns.int.>(one).Refs1 *? Ref1.int1).get.map(_.sortBy(_._1) ==> List(
          (2, List(2, 3)),
          (3, List())
        ))
      } yield ()
    }


    "In nested" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1) insert List(
          (1, List(1, 2)),
          (2, List(2, 3)),
          (3, List())
        )

        _ <- m(Ns.int.Refs1 * Ref1.int1(1)).get.map(_ ==> List(
          (1, List(1)),
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1(2)).get.map(_.sortBy(_._1) ==> List(
          (1, List(2)),
          (2, List(2))
        ))

        _ <- m(Ns.int.Refs1 * Ref1.int1.>(1)).get.map(_.sortBy(_._1) ==> List(
          (1, List(2)),
          (2, List(2, 3))
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.>(2)).get.map(_ ==> List(
          (2, List(3))
        ))

        // Using variable
        _ <- m(Ns.int.Refs1 * Ref1.int1(one)).get.map(_ ==> List(
          (1, List(1))
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.>(one)).get.map(_.sortBy(_._1) ==> List(
          (1, List(2)),
          (2, List(2, 3))
        ))
      } yield ()
    }


    "Expression only before optional nested structure" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1) insert List(
          (1, List(2, 3))
        )

        // Expression before optional nested structure ok
        _ <- m(Ns.int(1).Refs1 *? Ref1.int1).get.map(_ ==> List(
          (1, List(2, 3))
        ))

        // Expressions within optional nested structure not allowed
        _ = expectCompileError(
          "m(Ns.int.Refs1 *? Ref1.int1(2))",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Expressions not allowed within optional nested structures. " +
            """Found: Atom("Ref1", "int1", "Int", 1, Eq(Seq(2)), None, Seq(), Seq(), "")"""
        )
      } yield ()
    }
  }
}
