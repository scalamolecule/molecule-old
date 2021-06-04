package moleculeTests

import molecule.core.exceptions.MoleculeException
import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object Adhoc extends AsyncTestSuite {


  lazy val tests = Tests {

//    "adhoc" - bidirectional { implicit conn =>
    "adhoc" - core { implicit conn =>

      for {
        _ <- Ns.int(1).save
        _ <- Ns.int.get.map(_ ==> List(1))
      } yield ()
    }
  }
}
