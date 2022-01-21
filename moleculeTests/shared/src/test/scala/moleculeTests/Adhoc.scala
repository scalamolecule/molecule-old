package moleculeTests

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.Helpers
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.in3_out18._
import molecule.datomic.base.api.Datom
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.transform.Model2Query
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.Future
import scala.util.{Failure, Success}


object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "core" - core { implicit futConn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      for {
        conn <- futConn

        _ <- Ns.int.long insert List(
          (2, 3),
          (1, 4),
          (1, 3),
        )

        _ <- Ns.int.d2.long.a1.inspectGet

        _ <- Ns.int.a1.long.a2.get.map(_ ==> List(
          (1, 3),
          (1, 4),
          (2, 3),
        ))
        _ <- Ns.int.a1.long.d2.get.map(_ ==> List(
          (1, 4),
          (1, 3),
          (2, 3),
        ))
        _ <- Ns.int.d1.long.a2.get.map(_ ==> List(
          (2, 3),
          (1, 3),
          (1, 4),
        ))
        _ <- Ns.int.d1.long.d2.get.map(_ ==> List(
          (2, 3),
          (1, 4),
          (1, 3),
        ))

        _ <- Ns.int.a2.long.a1.get.map(_ ==> List(
          (1, 3),
          (2, 3),
          (1, 4),
        ))
        _ <- Ns.int.a2.long.d1.get.map(_ ==> List(
          (1, 4),
          (1, 3),
          (2, 3),
        ))
        _ <- Ns.int.d2.long.a1.get.map(_ ==> List(
          (2, 3),
          (1, 3),
          (1, 4),
        ))
        _ <- Ns.int.d2.long.d1.get.map(_ ==> List(
          (1, 4),
          (2, 3),
          (1, 3),
        ))

        //        _ <- Ns.int.d1.inspectGet
        //        _ <- Ns.int.d1.get.map(_ ==> List(3, 2, 1))

        //        _ <- Ns.int.get(2).map(_ ==> List(1, 3))
        //        _ <- Ns.int.a1.get(2).map(_ ==> List(1, 2))
        //        _ <- Ns.int.d1.get(2).map(_ ==> List(3, 2))

        //        _ <- Ns.int.get(2, 2).map(_ ==> List(2))
        //        _ <- Ns.int.a1.get(2, 2).map(_ ==> List(3))
        //        _ <- Ns.int.d1.get(2, 2).map(_ ==> List(1))


      } yield ()
    }

  }
}
