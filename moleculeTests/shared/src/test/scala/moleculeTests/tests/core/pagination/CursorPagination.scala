package moleculeTests.tests.core.pagination

import molecule.core.util.Executor._
import molecule.datomic.api.in1_out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

object CursorPagination extends AsyncTestSuite {


  // todo...

  lazy val tests = Tests {

    // General problems with offset pagination (for any db system):

    "previous data changed" - core { implicit conn =>
      for {
        r1 <- Ns.int.insert(1, 3, 5, 7)

        // Use empty string as cursor for initial fetch of page 1
        cursor1 = ""

//        cursor2 <- Ns.int.a1.get(2, cursor1).map { case (cursor2, page1) =>
//          page1 ==> List(1, 3)
//          cursor2
//        }
//
//        // Data added before next page is fetched
//        r2 <- Ns.int(2).save
//
//        _ <- Ns.int.getSince(r1.t).map(_ ==> List(2))

//        // Continue from where we were regardless of added 2
//        _ <- Ns.int.a1.get(2, cursor2).map { case (cursor3, page2) =>
//          page2 ==> List(5, 7)
//          cursor3
//        }
//
//        // Offset pagination would have shown 3 again since it sorts anew and would skip 1 and 2
//        _ <- Ns.int.a1.get(2, 2).map(_._2 ==> List(3, 5))
//
//        // If data is added after the cursor point, it is included in the following fetch
//        _ <- Ns.int(6).save
//        _ <- Ns.int.a1.get(2, cursor2).map { case (cursor3, page2) =>
//          page2 ==> List(5, 6)
//          cursor3
//        }
      } yield ()
    }

//    "Skipped data" - core { implicit conn =>
//      for {
//
//        eids <- Ns.int.insert(1, 2, 3, 4).map(_.eids)
//
//        _ <- Ns.int.a1.get(2, 0).map(_._2 ==> List(1, 2))
//
//        // First row (1) retracted before next page is fetched
//        _ <- eids.head.retract
//
//        // 3 is never shown!
//        _ <- Ns.int.a1.get(2, 2).map(_._2 ==> List(4))
//      } yield ()
//    }
  }
}
