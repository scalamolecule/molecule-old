package moleculeTests

import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Adhoc extends AsyncTestSuite {


  lazy val tests = Tests {

    "adhoc" - core { implicit conn =>
      Ns.str_.int$.insert(Some(1)).onComplete {
        case Failure(exc) => exc.getMessage ==> "auch"
        case Success(res) => throw new RuntimeException("Unexpected success with result: " + res)
      }


      //      compileError("m(Ns.str$)").check(
      //        "molecule.core.ops.exception.VerifyRawModelException: " +
      //          "Molecule has only optional attributes. Please add one or more mandatory/tacit attributes.")

      //      for {
      //        _ <- Ns.str_.int$.insert(Some(1)).onComplete{
      //          case Failure(exc) => exc.getMessage ==> "auch"
      //        }
      //
      //
      //      } yield ()
    }
  }
}
