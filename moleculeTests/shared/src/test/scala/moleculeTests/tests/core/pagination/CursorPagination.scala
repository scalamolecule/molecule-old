package moleculeTests.tests.core.pagination

import molecule.core.exceptions.MoleculeException
import molecule.core.util.Executor._
import molecule.datomic.api.in1_out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.pagination.OffsetPagination.core
import utest._


object CursorPagination extends AsyncTestSuite {


  // todo...

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
  }
}
