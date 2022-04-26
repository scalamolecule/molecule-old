package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.annotation.nowarn

object CursorPaginationTplNested extends AsyncTestSuite {
  val x = ""


  @nowarn lazy val tests = Tests {


    "Nested - forward asc" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, List(51, 52)),
        )

        // Page 1
        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (1, List(11, 12)),
            (2, List(21, 22)),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, List(31, 32)),
            (4, List(41, 42)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (5, List(51, 52)),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, List(31, 32)),
            (4, List(41, 42)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (1, List(11, 12)),
            (2, List(21, 22)),
          )
          more ==> 0
        }
      } yield ()
    }

    "Nested - forward desc" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, List(51, 52)),
        )

        // Page 1
        cursor <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (5, List(51, 52)),
            (4, List(41, 42)),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, List(31, 32)),
            (2, List(21, 22)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (1, List(11, 12)),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, List(31, 32)),
            (2, List(21, 22)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (5, List(51, 52)),
            (4, List(41, 42)),
          )
          more ==> 0
        }
      } yield ()
    }

    "Nested - backward asc" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, List(51, 52)),
        )

        // Page 1
        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, "").map { case (page, cursor, more) =>
          page ==> List(
            (4, List(41, 42)),
            (5, List(51, 52)),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (2, List(21, 22)),
            (3, List(31, 32)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (1, List(11, 12)),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (2, List(21, 22)),
            (3, List(31, 32)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, _, more) =>
          page ==> List(
            (4, List(41, 42)),
            (5, List(51, 52)),
          )
          more ==> 0
        }
      } yield ()
    }

    "Nested - backward desc" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, List(51, 52)),
        )

        // Page 1
        cursor <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, "").map { case (page, cursor, more) =>
          page ==> List(
            (2, List(21, 22)),
            (1, List(11, 12)),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (4, List(41, 42)),
            (3, List(31, 32)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (5, List(51, 52)),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (4, List(41, 42)),
            (3, List(31, 32)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, _, more) =>
          page ==> List(
            (2, List(21, 22)),
            (1, List(11, 12)),
          )
          more ==> 0
        }
      } yield ()
    }


    "Optional nested - forward asc" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, Nil),
        )

        // Page 1
        cursor <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (1, List(11, 12)),
            (2, List(21, 22)),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, List(31, 32)),
            (4, List(41, 42)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (5, Nil),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, List(31, 32)),
            (4, List(41, 42)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (1, List(11, 12)),
            (2, List(21, 22)),
          )
          more ==> 0
        }
      } yield ()
    }

    "Optional nested - forward desc" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, Nil),
        )

        // Page 1
        cursor <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (5, Nil),
            (4, List(41, 42)),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, List(31, 32)),
            (2, List(21, 22)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (1, List(11, 12)),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, List(31, 32)),
            (2, List(21, 22)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (5, Nil),
            (4, List(41, 42)),
          )
          more ==> 0
        }
      } yield ()
    }

    "Optional nested - backward asc" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, Nil),
        )

        // Page 1
        cursor <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2, "").map { case (page, cursor, more) =>
          page ==> List(
            (4, List(41, 42)),
            (5, Nil),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (2, List(21, 22)),
            (3, List(31, 32)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (1, List(11, 12)),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (2, List(21, 22)),
            (3, List(31, 32)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2, cursor).map { case (page, _, more) =>
          page ==> List(
            (4, List(41, 42)),
            (5, Nil),
          )
          more ==> 0
        }
      } yield ()
    }

    "Optional nested - backward desc" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, Nil),
        )

        // Page 1
        cursor <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2, "").map { case (page, cursor, more) =>
          page ==> List(
            (2, List(21, 22)),
            (1, List(11, 12)),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (4, List(41, 42)),
            (3, List(31, 32)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (5, Nil),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (4, List(41, 42)),
            (3, List(31, 32)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2, cursor).map { case (page, _, more) =>
          page ==> List(
            (2, List(21, 22)),
            (1, List(11, 12)),
          )
          more ==> 0
        }
      } yield ()
    }
  }
}
