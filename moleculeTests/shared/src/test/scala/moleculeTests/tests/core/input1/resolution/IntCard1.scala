package moleculeTests.tests.core.input1.resolution

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object IntCard1 extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.int$ insert List(
      (str1, Some(int1)),
      (str2, Some(int2)),
      (str3, Some(int3)),
      (str4, None)
    )
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Mandatory" - {

      "Eq" - core { implicit conn =>
        val inputMolecule = m(Ns.int(?))
        for {
          _ <- oneData

          // Can return other mandatory attribute values having missing tacit attribute value
          _ <- Ns.str.int_().get.map(_ ==> List(str4))
          _ <- Ns.str.int_(Nil).get.map(_ ==> List(str4))
          _ <- Ns.str.int$(None).get.map(_ ==> List((str4, None)))

          // Can't return mandatory attribute value that is missing
          _ <- Ns.str.int().get.map(_ ==> Nil)
          // Ns.str.int(Nil).get.map(_ ==> Nil) // not allowed to compile (mandatory/Nil is contradictive)
          // same as
          _ <- inputMolecule(Nil).get.map(_ ==> Nil)
          _ <- inputMolecule(List(int1)).get.map(_ ==> List(int1))
          _ <- inputMolecule(List(int1, int1)).get.map(_ ==> List(int1))
          _ <- inputMolecule(List(int1, int2)).get.map(_ ==> List(int1, int2))
        } yield ()
      }

      "!=" - core { implicit conn =>
        val inputMolecule = m(Ns.int.not(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3))
          _ <- inputMolecule(List(int1)).get.map(_ ==> List(int2, int3))
          _ <- inputMolecule(List(int1, int2)).get.map(_ ==> List(int3))

        } yield ()
      }

      ">" - core { implicit conn =>
        val inputMolecule = m(Ns.int.>(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3))
          _ <- inputMolecule(List(int2)).get.map(_ ==> List(int3))
          _ <- inputMolecule(List(int2, int3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      ">=" - core { implicit conn =>
        val inputMolecule = m(Ns.int.>=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3))
          _ <- inputMolecule(List(int2)).get.map(_ ==> List(int2, int3))
          _ <- inputMolecule(List(int2, int3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      "<" - core { implicit conn =>
        val inputMolecule = m(Ns.int.<(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3))
          _ <- inputMolecule(List(int2)).get.map(_ ==> List(int1))

          _ <- inputMolecule(List(int2, int3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      "<=" - core { implicit conn =>
        val inputMolecule = m(Ns.int.<=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3))
          _ <- inputMolecule(List(int2)).get.map(_ ==> List(int1, int2))

          _ <- inputMolecule(List(int2, int3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }
    }


    "Tacit" - {

      "Eq" - core { implicit conn =>
        val inputMolecule = m(Ns.str.int_(?))
        for {
          _ <- oneData

          // Can't return mandatory attribute value that is missing
          _ <- Ns.str.int().get.map(_ ==> Nil)
          // Ns.str.int(Nil).get.map(_ ==> Nil) // not allowed to compile (mandatory/Nil is contradictive)

          // Can return other mandatory attribute values having missing tacit attribute value
          _ <- Ns.str.int$(None).get.map(_ ==> List((str4, None)))
          _ <- Ns.str.int_().get.map(_ ==> List(str4))
          _ <- Ns.str.int_(Nil).get.map(_ ==> List(str4))
          // same as
          _ <- inputMolecule(Nil).get.map(_ ==> List(str4))
          _ <- inputMolecule(List(int1)).get.map(_ ==> List(str1))
          _ <- inputMolecule(List(int1, int2)).get.map(_ ==> List(str1, str2))
        } yield ()
      }

      "!=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.int_.not(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(int1)).get.map(_ ==> List(str2, str3))
          _ <- inputMolecule(List(int1, int2)).get.map(_ ==> List(str3))
        } yield ()
      }

      ">" - core { implicit conn =>
        val inputMolecule = m(Ns.str.int_.>(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(int2)).get.map(_ ==> List(str3))
          _ <- inputMolecule(List(int2, int3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      ">=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.int_.>=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(int2)).get.map(_ ==> List(str2, str3))
          _ <- inputMolecule(List(int2, int3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      "<" - core { implicit conn =>
        val inputMolecule = m(Ns.str.int_.<(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(int2)).get.map(_ ==> List(str1))
          _ <- inputMolecule(List(int2, int3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }

      "<=" - core { implicit conn =>
        val inputMolecule = m(Ns.str.int_.<=(?))
        for {
          _ <- oneData
          _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
          _ <- inputMolecule(List(int2)).get.map(_ ==> List(str1, str2))
          _ <- inputMolecule(List(int2, int3)).get
            .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }
        } yield ()
      }
    }
  }
}