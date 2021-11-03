package moleculeTests.tests.core.input1.resolution

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object EnumCard1 extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.enumm$ insert List(
      (str1, Some(enum1)),
      (str2, Some(enum2)),
      (str3, Some(enum3)),
      (str4, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Mandatory" - {

      "Eq" - core { implicit conn =>
        val inputMolecule = m(Ns.enumm(?))
        for {
          _ <- oneData
          // Can return other mandatory attribute values having missing tacit attribute value
          _ <- Ns.str.enumm_().get.map(_ ==> List(str4))
          _ <- Ns.str.enumm_(Nil).get.map(_ ==> List(str4))
          _ <- Ns.str.enumm$(None).get.map(_ ==> List((str4, None)))

          // Can't return mandatory attribute value that is missing
          _ <- Ns.str.enumm().get.map(_ ==> Nil)
          // Ns.str.enumm(Nil).get.map(_ ==> Nil) // not allowed to compile (mandatory/Nil is contradictive)
          // same as
          _ <- inputMolecule(Nil).get.map(_ ==> Nil)
          _ <- inputMolecule(List(enum1)).get.map(_ ==> List(enum1))
          _ <- inputMolecule(List(enum1, enum1)).get.map(_ ==> List(enum1))
          _ <- inputMolecule(List(enum1, enum2)).get.map(_ ==> List(enum2, enum1))

          // Varargs
          _ <- inputMolecule(enum1).get.map(_ ==> List(enum1))
          _ <- inputMolecule(enum1, enum2).get.map(_ ==> List(enum2, enum1))

          // `or`
          _ <- inputMolecule(enum1 or enum2).get.map(_ ==> List(enum2, enum1))
        } yield ()
      }

      "!=" - core { implicit conn =>
        val inputMolecule = m(Ns.enumm.not(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum1)).get.map(_.sorted ==> List(enum2, enum3))
          _ <- inputMolecule(List(enum1, enum1)).get.map(_.sorted ==> List(enum2, enum3))
          _ <- inputMolecule(List(enum1, enum2)).get.map(_.sorted ==> List(enum3))
        } yield ()
      }

      ">" - core { implicit conn =>
        val inputMolecule = m(Ns.enumm.>(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum3))

          _ <- inputMolecule(List(enum2, enum3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      ">=" - core { implicit conn =>
        val inputMolecule = m(Ns.enumm.>=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum2, enum3))
          _ <- inputMolecule(List(enum2, enum3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      "<" - core { implicit conn =>
        val inputMolecule = m(Ns.enumm.<(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum2)).get.map(_ ==> List(enum1))
          _ <- inputMolecule(List(enum2, enum3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      "<=" - core { implicit conn =>
        val inputMolecule = m(Ns.enumm.<=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum1, enum2))
          _ <- inputMolecule(List(enum2, enum3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }
    }


    "Tacit" - {

      "Eq" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enumm_(?))
        for {
          _ <- oneData

          // Can't return mandatory attribute value that is missing
          _ <- Ns.str.enumm().get.map(_ ==> Nil)
          // Ns.str.enumm(Nil).get.map(_ ==> Nil) // not allowed to compile (mandatory/Nil is contradictive)

          // Can return other mandatory attribute values having missing tacit attribute value
          _ <- Ns.str.enumm_().get.map(_ ==> List(str4))
          _ <- Ns.str.enumm_(Nil).get.map(_ ==> List(str4))
          _ <- Ns.str.enumm$(None).get.map(_ ==> List((str4, None)))
          // same as
          _ <- inputMolecule(Nil).get.map(_ ==> List(str4))
          _ <- inputMolecule(List(enum1)).get.map(_ ==> List(str1))
          _ <- inputMolecule(List(enum1, enum1)).get.map(_ ==> List(str1))
          _ <- inputMolecule(List(enum1, enum2)).get.map(_ ==> List(str1, str2))
        } yield ()
      }

      "!=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enumm_.not(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
          _ <- inputMolecule(List(enum1)).get.map(_.sorted ==> List(str2, str3))
          _ <- inputMolecule(List(enum1, enum1)).get.map(_.sorted ==> List(str2, str3))
          _ <- inputMolecule(List(enum1, enum2)).get.map(_ ==> List(str3))
        } yield ()
      }

      ">" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enumm_.>(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(enum2)).get.map(_ ==> List(str3))

          _ <- inputMolecule(List(enum2, enum3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      ">=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enumm_.>=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(enum2)).get.map(_ ==> List(str2, str3))
          _ <- inputMolecule(List(enum2, enum3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      "<" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enumm_.<(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(enum2)).get.map(_ ==> List(str1))
          _ <- inputMolecule(List(enum2, enum3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      "<=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enumm_.<=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(enum2)).get.map(_ ==> List(str1, str2))
          _ <- inputMolecule(List(enum2, enum3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }
    }
  }
}