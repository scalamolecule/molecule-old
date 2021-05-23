package molecule.tests.core.input1.resolution

import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import molecule.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object EnumCard1 extends AsyncTestSuite {

  def oneData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.str.enum$ insert List(
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
        val inputMolecule = m(Ns.enum(?))
        for {
          _ <- oneData
          // Can return other mandatory attribute values having missing tacit attribute value
          _ <- Ns.str.enum_().get === List(str4)
          _ <- Ns.str.enum_(Nil).get === List(str4)
          _ <- Ns.str.enum$(None).get === List((str4, None))

          // Can't return mandatory attribute value that is missing
          _ <- Ns.str.enum().get === Nil
          // Ns.str.enum(Nil).get === Nil // not allowed to compile (mandatory/Nil is contradictive)
          // same as
          _ <- inputMolecule(Nil).get === Nil
          _ <- inputMolecule(List(enum1)).get === List(enum1)
          _ <- inputMolecule(List(enum1, enum1)).get === List(enum1)
          _ <- inputMolecule(List(enum1, enum2)).get === List(enum2, enum1)

          // Varargs
          _ <- inputMolecule(enum1).get === List(enum1)
          _ <- inputMolecule(enum1, enum2).get === List(enum2, enum1)

          // `or`
          _ <- inputMolecule(enum1 or enum2).get === List(enum2, enum1)
        } yield ()
      }

      "!=" - core { implicit conn =>
        val inputMolecule = m(Ns.enum.not(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum1)).get.map(_.sorted ==> List(enum2, enum3))
          _ <- inputMolecule(List(enum1, enum1)).get.map(_.sorted ==> List(enum2, enum3))
          _ <- inputMolecule(List(enum1, enum2)).get.map(_.sorted ==> List(enum3))
        } yield ()
      }

      ">" - core { implicit conn =>
        val inputMolecule = m(Ns.enum.>(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum3))

          //(_ <- inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
          // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
          // "Can't apply multiple values to comparison function."
        } yield ()
      }

      ">=" - core { implicit conn =>
        val inputMolecule = m(Ns.enum.>=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum2, enum3))
          //(_ <- inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
          // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
          // "Can't apply multiple values to comparison function."
        } yield ()
      }

      "<" - core { implicit conn =>
        val inputMolecule = m(Ns.enum.<(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum2)).get === List(enum1)
          //(_ <- inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
          // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
          // "Can't apply multiple values to comparison function."
        } yield ()
      }

      "<=" - core { implicit conn =>
        val inputMolecule = m(Ns.enum.<=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
          _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum1, enum2))
          //(_ <- inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
          // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
          // "Can't apply multiple values to comparison function."
        } yield ()
      }
    }


    "Tacit" - {

      "Eq" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enum_(?))
        for {
          _ <- oneData

          // Can't return mandatory attribute value that is missing
          _ <- Ns.str.enum().get === Nil
          // Ns.str.enum(Nil).get === Nil // not allowed to compile (mandatory/Nil is contradictive)

          // Can return other mandatory attribute values having missing tacit attribute value
          _ <- Ns.str.enum_().get === List(str4)
          _ <- Ns.str.enum_(Nil).get === List(str4)
          _ <- Ns.str.enum$(None).get === List((str4, None))
          // same as
          _ <- inputMolecule(Nil).get === List(str4)
          _ <- inputMolecule(List(enum1)).get === List(str1)
          _ <- inputMolecule(List(enum1, enum1)).get === List(str1)
          _ <- inputMolecule(List(enum1, enum2)).get === List(str1, str2)
        } yield ()
      }

      "!=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enum_.not(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
          _ <- inputMolecule(List(enum1)).get.map(_.sorted ==> List(str2, str3))
          _ <- inputMolecule(List(enum1, enum1)).get.map(_.sorted ==> List(str2, str3))
          _ <- inputMolecule(List(enum1, enum2)).get === List(str3)
        } yield ()
      }

      ">" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enum_.>(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get === List(str1, str2, str3)
          _ <- inputMolecule(List(enum2)).get === List(str3)

          //(_ <- inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
          // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
          // "Can't apply multiple values to comparison function."
        } yield ()
      }

      ">=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enum_.>=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get === List(str1, str2, str3)
          _ <- inputMolecule(List(enum2)).get === List(str2, str3)
          //(_ <- inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
          // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
          // "Can't apply multiple values to comparison function."
        } yield ()
      }

      "<" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enum_.<(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get === List(str1, str2, str3)
          _ <- inputMolecule(List(enum2)).get === List(str1)
          //(_ <- inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
          // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
          // "Can't apply multiple values to comparison function."
        } yield ()
      }

      "<=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.enum_.<=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get === List(str1, str2, str3)
          _ <- inputMolecule(List(enum2)).get === List(str1, str2)
          //(_ <- inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
          // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
          // "Can't apply multiple values to comparison function."
        } yield ()
      }
    }
  }
}