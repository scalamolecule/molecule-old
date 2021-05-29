package moleculeTests

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out11._
import molecule.datomic.api.out5.m
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Adhoc extends AsyncTestSuite {


  lazy val tests = Tests {

    "adhoc" - core { implicit conn =>
      //      Ns.str_.int$.insert(Some(1)).onComplete {
      //        case Failure(exc) =>
      //          println("A " + exc)
      //          exc.getMessage ==> "auchx"
      //        case Success(res) =>
      //          println("B")
      //          throw new RuntimeException("Unexpected success with result: " + res)
      //      }

      //      m(Ns.str_.int$).insert(Some(1)).value ==> Some(Failure(
      //        new VerifyModelException("[noTacitAttrs]  Tacit attributes like `str_` not allowed in insert molecules.")))

      //      Some(Failure(molecule.core.ops.exception.VerifyModelException: [noTacitAttrs]  Tacit attributes like `str_` not allowed in insert molecules.)) !=
      //      Some(Failure(molecule.core.ops.exception.VerifyModelException: [noTacitAttrs]  Tacit attributes like `str_` not allowed in insert molecules.))

      //            intercept[VerifyModelException] {
      //              m(Ns.str_.int$) //.insert
      //            }

      //      m(Ns.str_.int$).insert(Some(1)).recover { case VerifyModelException(msg) =>
      //        msg ==> "[noTacitAttrs]  Tacit attributes like `str_` not allowed in insert molecules."
      //      }


      //
      //      m(Ns.str_.int$).insert(Some(1)).recover { case VerifyModelException(msg) =>
      //        msg ==> "[noTacitAttrs]  Tacit attributes like `str_` not allowed in insert molecules."
      //      }

      for {
        tx <- Ns.uuidMap(str1 -> uuid3).save
        eid = tx.eid
        _ <- Ns(eid).uuidMap(str1 -> uuid1, str1 -> uuid2).update
        _ <- Ns.uuidMap.get.map(_ ==> List(Map(str1 -> uuid1)))
      } yield ()

      //      Ns.str_.int$.insert.apply(Some(1)).flatMap{res =>
      //        Future(println("res: " + res))
      //      }.recover{rec =>
      //        println("rec: " + rec)
      //      }


    }
  }
}
