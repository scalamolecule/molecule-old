package moleculeTests.tests.core.pagination.offset

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

/**
 * Using the same database as of a time point for all pages guarantees uniqueness
 * at the expense of not including changes in-between pages.
 *
 * So, if intermediate changes are not that important and strict uniqueness is,
 * then this is an option. A product catalogue could be an example, whereas a
 * list of comments on a high-churn website might not be.
 */
object AsOfPagination extends AsyncTestSuite {


  lazy val tests = Tests {

    "Stable asOf db" - core { implicit futConn =>
      for {
        conn <- futConn

        _ <- Ns.int.insert(1, 3, 5, 7, 9)

        // Get point in time `t` to base the snapshot on
        db <- conn.db
        t <- db.basisT

        // Initial page is loaded with `get` and offset 0 to get the initial time t for subsequent page loads
        _ <- Ns.int.a1.getAsOf(t, 2, 0).map { case (data, totalCount) =>
          data ==> List(1, 3)

          val limit       = 2
          val offset      = 0
          val isFirstPage = offset == 0
          isFirstPage ==> true

          val isLastPage = (limit + offset) >= totalCount
          isLastPage ==> false
          t
        }

        // Changes won't affect subsequent pagination when using immutable database asOf first page load
        _ <- Ns.int.insert(2, 4, 6, 8)
        _ <- Ns.int.a1.getAsOf(t, 2, 2).map { case (data, totalCount) =>
          // No even numbers
          data ==> List(5, 7)

          val limit       = 2
          val offset      = 2
          val isFirstPage = offset == 0
          isFirstPage ==> false

          val isLastPage = (limit + offset) >= totalCount
          isLastPage ==> false
        }

        _ <- Ns.int.a1.getAsOf(t, 2, 4).map { case (data, totalCount) =>
          data ==> List(9)

          val limit       = 2
          val offset      = 4
          val isFirstPage = offset == 0
          isFirstPage ==> false

          val isLastPage = (limit + offset) >= totalCount
          isLastPage ==> true
        }
      } yield ()
    }
  }
}
