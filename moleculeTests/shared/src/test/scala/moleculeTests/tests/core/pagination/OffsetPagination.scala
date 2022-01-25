package moleculeTests.tests.core.pagination

import molecule.core.exceptions.MoleculeException
import molecule.core.util.Executor._
import molecule.datomic.api.out2._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object OffsetPagination extends AsyncTestSuite {


  lazy val tests = Tests {

    "limit" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        // Simply taking the first 2 rows
        // Note that only the data is returned (without a total count of rows)
        _ <- Ns.int.a1.get(2).map(_ ==> List(1, 2))
        _ <- Ns.int.d1.get(2).map(_ ==> List(3, 2))

        // Same as
        _ <- Ns.int.a1.get.map(_.take(2) ==> List(1, 2))
        _ <- Ns.int.d1.get.map(_.take(2) ==> List(3, 2))
      } yield ()
    }


    "Limit + offset" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        // Tuple of (totalCount, pageRows) returned
        // Calculate page numbers from totalCount and limit size:
        // val total pages = ceil(totalCount / 2) = ceil(3 / 2) = 2
        _ <- Ns.int.a1.get(2, 0).map(_ ==> (3, List(1, 2)))
        _ <- Ns.int.a1.get(2, 2).map(_._2 ==> List(3))

        _ <- Ns.int.d1.get(2, 0).map(_._2 ==> List(3, 2))
        _ <- Ns.int.d1.get(2, 2).map(_._2 ==> List(1))
      } yield ()
    }


    "Offset beyond total count" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        // Empty result set returned
        _ <- Ns.int.a1.get(2, 4).map(_ ==> (3, Nil))
        _ <- Ns.int.d1.get(2, 4).map(_ ==> (3, Nil))
      } yield ()
    }


    "Wrong offset/limit" - core { implicit conn =>
      for {
        _ <- Ns.int.a1.get(0, 10)
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Limit has to be a positive number. Found: 0"
          }

        _ <- Ns.int.a1.get(10, -10)
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Offset has to be >= 0. Found: -10"
          }
      } yield ()
    }


    // Problems with offset pagination:

    "Re-seen data" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 3, 5)

        _ <- Ns.int.a1.get(2, 0).map(_._2 ==> List(1, 3))

        // Data added before next page is fetched
        _ <- Ns.int(2).save

        // 3 is shown again!
        _ <- Ns.int.a1.get(2, 2).map(_._2 ==> List(3, 5))
      } yield ()
    }

    "Skipped data" - core { implicit conn =>
      for {

        eids <- Ns.int.insert(1, 2, 3, 4).map(_.eids)

        _ <- Ns.int.a1.get(2, 0).map(_._2 ==> List(1, 2))

        // First row (1) retracted before next page is fetched
        _ <- eids.head.retract

        // 3 is never shown!
        _ <- Ns.int.a1.get(2, 2).map(_._2 ==> List(4))
      } yield ()
    }
  }
}
