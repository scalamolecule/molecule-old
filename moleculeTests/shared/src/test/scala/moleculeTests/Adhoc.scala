package moleculeTests

import molecule.core.exceptions.MoleculeException
import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out11._
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


//      val inputMolecule1 = m(Ns.enum.enums_.not(?)) // or m(Ns.enum.enums.!=(?))
//      val inputMolecule2 = m(Ns.enum.enums.not(?)) // or m(Ns.enum.enums.!=(?))
      val inputMolecule3 = m(Ns.enums.not(?)) // or m(Ns.enum.enums.!=(?))
      val all           = List(
        (enum1, Set(enum1, enum2, enum3)),
        (enum2, Set(enum2, enum3, enum4)),
        (enum3, Set(enum3, enum4, enum5))
      )



      for {
        _ <- Ns.enum.enums$ insert List(
          (enum1, Some(Set(enum1, enum2))),
          (enum2, Some(Set(enum2, enum3))),
          (enum3, Some(Set(enum3, enum4))),
          (enum4, Some(Set(enum4, enum5))),
          (enum5, Some(Set(enum4, enum5, enum6))),
          (enum6, None)
        )
//        _ <- inputMolecule1(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
//        _ <- inputMolecule2(Nil).get.map(_ ==> all)
        _ <- inputMolecule3(Nil).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5)))

      } yield ()

      //      Ns.str_.int$.insert.apply(Some(1)).flatMap{res =>
      //        Future(println("res: " + res))
      //      }.recover{rec =>
      //        println("rec: " + rec)
      //      }


    }
  }
}
