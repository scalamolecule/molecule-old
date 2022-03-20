package moleculeTests

import molecule.core.util.Executor._
import molecule.core.util.Helpers
import molecule.datomic.api.out8._
import moleculeTests.setup.AsyncTestSuite
import utest._


object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      for {
        conn <- futConn



        _ <- Ns.int.str insert List(
          (1, "b"),
          (1, "a"),
          (2, "c"),
        )
        _ <- Ns.int.d1.str.a2.get.map(_ ==> List(
          (2, "c"),
          (1, "a"),
          (1, "b"),
        ))
        _ <- Ns.int.d1.str.d2.get.map(_ ==> List(
          (2, "c"),
          (1, "b"),
          (1, "a"),
        ))




      } yield ()
    }
  }
}
