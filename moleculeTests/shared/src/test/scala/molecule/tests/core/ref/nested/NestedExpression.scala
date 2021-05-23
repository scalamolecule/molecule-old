package molecule.tests.core.ref.nested

import molecule.core.util.testing.expectCompileError
import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out2._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


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

        _ <- m(Ns.int.>(1).Refs1 * Ref1.int1).get === List(
          (2, List(2, 3)),
        )
        _ <- m(Ns.int.>(1).Refs1 *? Ref1.int1).get.map(_.sortBy(_._1) ==> List(
          (2, List(2, 3)),
          (3, List())
        ))

        // Using variable
        _ <- m(Ns.int.>(one).Refs1 * Ref1.int1).get === List(
          (2, List(2, 3)),
        )
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

        _ <- m(Ns.int.Refs1 * Ref1.int1(1)).get === List(
          (1, List(1)),
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1(2)).get.map(_.sortBy(_._1) ==> List(
          (1, List(2)),
          (2, List(2)),
        ))

        _ <- m(Ns.int.Refs1 * Ref1.int1.>(1)).get.map(_.sortBy(_._1) ==> List(
          (1, List(2)),
          (2, List(2, 3))
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.>(2)).get === List(
          (2, List(3))
        )

        // Using variable
        _ <- m(Ns.int.Refs1 * Ref1.int1(one)).get === List(
          (1, List(1)),
        )
        _ <- m(Ns.int.Refs1 * Ref1.int1.>(one)).get.map(_.sortBy(_._1) ==> List(
          (1, List(2)),
          (2, List(2, 3))
        ))

        //    // Expressions not allowed in optional nested structures
        //    expectCompileError(
        //      "m(Ns.int.Refs1 *? Ref1.int1(1))",
        //      "molecule.core.transform.exception.Dsl2ModelException: " +
        //        "Expressions not allowed in optional nested structures. " +
        //        """Found: Atom("Ref1", "int1", "Int", 1, Eq(Seq(1)), None, Seq(), Seq())""")
      } yield ()
    }
  }
}