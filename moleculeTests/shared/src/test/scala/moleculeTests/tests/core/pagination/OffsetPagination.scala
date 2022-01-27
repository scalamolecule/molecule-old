package moleculeTests.tests.core.pagination

import molecule.core.exceptions.MoleculeException
import molecule.core.util.Executor._
import molecule.datomic.api.out2._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.math.ceil


object OffsetPagination extends AsyncTestSuite {


  lazy val tests = Tests {

    "limit" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        // Simply taking the first 2 rows
        // Note that only the data is returned (without a total count of rows)
        // asc
        _ <- Ns.int.a1.get(2).map(_ ==> List(1, 2))

        // Using limit as above is more efficient than taking subset of all data
        _ <- Ns.int.a1.get.map(_.take(2) ==> List(1, 2))

        // desc
        _ <- Ns.int.d1.get(2).map(_ ==> List(3, 2))
      } yield ()
    }


    "Total count, page number" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        limit = 2

        // Add offset to fetch total count too

        // Page 1
        offset0 = 0
        _ <- Ns.int.get(limit, offset0).map {
          // Tuple of (totalCount, pageRows) returned
          case (totalCount, data) =>
            totalCount ==> 3
            data ==> List(1, 2)

            // Calculate page numbers from totalCount and limit size:
            val totalPages = (totalCount / limit.toDouble).ceil.round
            totalPages ==> 2

            // Calculate current page number from offset and limit
            val page = offset0 / limit + 1
            page ==> 1
        }

        // Page 2
        offset2 = 2
        _ <- Ns.int.get(limit, offset2).map {
          case (totalCount, data) =>
            totalCount ==> 3
            data ==> List(3)
            val page = offset2 / limit + 1
            page ==> 2
        }
      } yield ()
    }


    "Limit + offset" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        // Page 1, asc
        _ <- Ns.int.a1.get(2, 0).map(_._2 ==> List(1, 2))

        // Page 2, asc
        _ <- Ns.int.a1.get(2, 2).map(_._2 ==> List(3))

        // Page 1, desc
        _ <- Ns.int.d1.get(2, 0).map(_._2 ==> List(3, 2))

        // Page 2, desc
        _ <- Ns.int.d1.get(2, 2).map(_._2 ==> List(1))
      } yield ()
    }


    "Offset beyond total count" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        // Empty result set returned for offset exceeding total count
        _ <- Ns.int.a1.get(2, 4).map(_ ==> (3, Nil))
      } yield ()
    }


    "Aggregate type changes" - core { implicit conn =>
      for {
        _ <- Ns.str.insert("a", "a", "b", "c")

        // Empty result set returned for offset exceeding total count
        _ <- Ns.str.str.apply(count).get.map(_ ==> List(("a", 2), ("b", 1), ("c", 1) ))
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
