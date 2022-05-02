package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.pagination.cursor.CursorPaginationObj.{core, x}
import utest._
import scala.annotation.nowarn

// Pagination for the nested object api is basically implemented as for nested tuples.
// So we only test a few things here. All tuple tests should be convertible to object tests.

object CursorPaginationObjNested extends AsyncTestSuite {
  val x = ""

  @nowarn lazy val tests = Tests {

    "Basic" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, List(41, 42)),
          (5, List(51, 52)),
        )

        c <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(2, x).collect { case (List(o1, o2), c, 3) =>
          o1.int ==> 1
          o1.Refs1(0).int1 ==> 11
          o1.Refs1(1).int1 ==> 12
          o2.int ==> 2
          o2.Refs1(0).int1 ==> 21
          o2.Refs1(1).int1 ==> 22
          c
        }

        c <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(2, c).collect { case (List(o3, o4), c, 1) =>
          o3.int ==> 3
          o3.Refs1(0).int1 ==> 31
          o3.Refs1(1).int1 ==> 32
          o4.int ==> 4
          o4.Refs1(0).int1 ==> 41
          o4.Refs1(1).int1 ==> 42
          c
        }

        c <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(2, c).collect { case (List(o5), c, 0) =>
          o5.int ==> 5
          o5.Refs1(0).int1 ==> 51
          o5.Refs1(1).int1 ==> 52
          c
        }

        c <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(-2, c).collect { case (List(o3, o4), c, 2) =>
          o3.int ==> 3
          o3.Refs1(0).int1 ==> 31
          o3.Refs1(1).int1 ==> 32
          o4.int ==> 4
          o4.Refs1(0).int1 ==> 41
          o4.Refs1(1).int1 ==> 42
          c
        }

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(-2, c).collect { case (List(o1, o2), _, 0) =>
          o1.int ==> 1
          o1.Refs1(0).int1 ==> 11
          o1.Refs1(1).int1 ==> 12
          o2.int ==> 2
          o2.Refs1(0).int1 ==> 21
          o2.Refs1(1).int1 ==> 22
          ()
        }
      } yield ()
    }
  }
}