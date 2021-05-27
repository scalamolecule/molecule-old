package moleculeTests

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._

object Adhoc extends AsyncTestSuite {


  lazy val tests = Tests {

    "adhoc" - core { implicit conn =>
      //      Ns.str_.int$.insert(Some(1)).onComplete {
      //        case Failure(exc) => exc.getMessage ==> "auch"
      //        case Success(res) => throw new RuntimeException("Unexpected success with result: " + res)
      //      }


      //      compileError("m(Ns.str$)").check(
      //        "molecule.core.ops.exception.VerifyRawModelException: " +
      //          "Molecule has only optional attributes. Please add one or more mandatory/tacit attributes.")

//      for {
//        _ <- Ns.ints(1, 2).save
//        _ <- Ns.ints(min).get === List(Set(1))
//        //              _ <- Ns.str_.int$.insert(Some(1)).onComplete{
//        //                case Failure(exc) => exc.getMessage ==> "auch"
//        //              }
//      } yield ()
    }
  }
}
