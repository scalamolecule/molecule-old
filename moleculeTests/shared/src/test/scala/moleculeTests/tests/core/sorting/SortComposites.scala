package moleculeTests.tests.core.sorting

import molecule.core.util.Executor._
import molecule.datomic.api.out4._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SortComposites extends AsyncTestSuite {

  lazy val tests = Tests {

    "1 + 1" - core { implicit conn =>
      for {
        _ <- (Ns.str + Ref1.int1) insert List(
          ("A", 1),
          ("A", 2),
          ("B", 1),
          ("B", 2),
        )

        _ <- (Ns.str.a1 + Ref1.int1.a1).get.map(_ ==> List(
          ("A", 1),
          ("A", 2),
          ("B", 1),
          ("B", 2),
        ))
        _ <- (Ns.str.a1 + Ref1.int1.d1).get.map(_ ==> List(
          ("A", 2),
          ("A", 1),
          ("B", 2),
          ("B", 1),
        ))
        _ <- (Ns.str.d1 + Ref1.int1.a1).get.map(_ ==> List(
          ("B", 1),
          ("B", 2),
          ("A", 1),
          ("A", 2),
        ))
        _ <- (Ns.str.d1 + Ref1.int1.d1).get.map(_ ==> List(
          ("B", 2),
          ("B", 1),
          ("A", 2),
          ("A", 1),
        ))
      } yield ()
    }


    "1 + 2" - core { implicit conn =>
      for {
        _ <- (Ns.str + Ref1.int1) insert List(
          ("A", 1),
          ("A", 2),
          ("B", 1),
          ("B", 2),
        )
      } yield ()
    }


    "2 + 1" - core { implicit conn =>
      for {
        _ <- (Ns.str + Ref1.int1) insert List(
          ("A", 1),
          ("A", 2),
          ("B", 1),
          ("B", 2),
        )
      } yield ()
    }


    "2 + 2" - core { implicit conn =>
      for {
        _ <- (Ns.str + Ref1.int1) insert List(
          ("A", 1),
          ("A", 2),
          ("B", 1),
          ("B", 2),
        )
      } yield ()
    }
  }
}
