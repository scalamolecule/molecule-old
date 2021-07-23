package moleculeTests.tests.core.nested

import molecule.datomic.api.out6._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object NestedAttrs2 extends AsyncTestSuite {

  lazy val tests = Tests {

    "strs" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$) insert List(
          (1, List((1, Some(Set("a", "b"))), (2, None))),
          (2, List())
        )

        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$).get.map(_ ==> List(
          (1, List((1, Some(Set("a", "b"))), (2, None))),
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1).get.map(_ ==> List(
          (1, List((1, Set("a", "b")))),
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1_).get.map(_ ==> List(
          (1, List(1)),
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(Set("a", "b"))), (2, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Set("a", "b")))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "enums" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1$) insert List(
          (1, List((1, Some(Set("enum10", "enum11"))), (2, None))),
          (2, List())
        )

        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1$).get.map(_ ==> List(
          (1, List((1, Some(Set("enum10", "enum11"))), (2, None)))
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1).get.map(_ ==> List(
          (1, List((1, Set("enum10", "enum11"))))
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.enums1_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(Set("enum10", "enum11"))), (2, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Set("enum10", "enum11")))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "refs" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2$) insert List(
          (1, List((1, Some(Set(42L, 43L))), (2, None))),
          (2, List())
        )

        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2$).get.map(_ ==> List(
          (1, List((1, Some(Set(42L, 43L))), (2, None)))
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2).get.map(_ ==> List(
          (1, List((1, Set(42L, 43L))))
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.refs2_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(Set(42L, 43L))), (2, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Set(42L, 43L)))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }
  }
}