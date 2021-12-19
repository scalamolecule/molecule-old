package moleculeTests

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.Helpers
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.in3_out12._
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

        _ <- Ns.int(1).save
        _ <- Ns.int.get.map(_ ==> List(1))




      } yield ()
    }
  }
}
