package moleculeTests.tests.core.input1.resolution

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object EnumCard2tacit extends AsyncTestSuite {

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.enumm.enums$ insert List(
      (enum1, Some(Set(enum1, enum2))),
      (enum2, Some(Set(enum2, enum3))),
      (enum3, Some(Set(enum3, enum4))),
      (enum4, Some(Set(enum4, enum5))),
      (enum5, Some(Set(enum4, enum5, enum6))),
      (enum6, None),
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Eq" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums_(?))
      for {
        _ <- manyData
        _ <- Ns.enumm.enums$(None).get.map(_ ==> List(("enum6", None)))

        // Note semantic differences:

        // Can return other mandatory attribute values having missing tacit attribute value
        _ <- Ns.enumm.enums_().get.map(_ ==> List(enum6))
        _ <- Ns.enumm.enums_(Nil).get.map(_ ==> List(enum6))
        _ <- Ns.enumm.enums$(None).get.map(_ ==> List((enum6, None)))

        // Can't return mandatory attribute value that is missing
        _ <- Ns.enumm.enums().get.map(_ ==> Nil)
        // Ns.enumm.enums(Nil).get.map(_ ==> Nil) // not allowed to compile (mandatory/Nil is contradictive)
        // same as
        _ <- inputMolecule(Nil).get.map(_ ==> List(enum6))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(enum6))

        // Values of 1 Set match values of 1 card-many attribute at a time

        _ <- inputMolecule(List(Set(enum1))).get.map(_ ==> List(enum1))
        _ <- inputMolecule(List(Set(enum2))).get.map(_.sorted ==> List(enum1, enum2))
        _ <- inputMolecule(List(Set(enum3))).get.map(_.sorted ==> List(enum2, enum3))

        _ <- inputMolecule(List(Set(enum1, enum1))).get.map(_ ==> List(enum1))
        _ <- inputMolecule(List(Set(enum1, enum2))).get.map(_ ==> List(enum1))
        _ <- inputMolecule(List(Set(enum1, enum3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum2, enum3))).get.map(_ ==> List(enum2))
        _ <- inputMolecule(List(Set(enum4, enum5))).get.map(_.sorted ==> List(enum4, enum5))

        // Values of each Set matches values of 1 card-many attributes respectively

        _ <- inputMolecule(List(Set(enum1, enum2), Set[String]())).get.map(_ ==> List(enum1))
        _ <- inputMolecule(List(Set(enum1), Set(enum1))).get.map(_ ==> List(enum1))
        _ <- inputMolecule(List(Set(enum1), Set(enum2))).get.map(_.sorted ==> List(enum1, enum2))
        _ <- inputMolecule(List(Set(enum1), Set(enum3))).get.map(_.sorted ==> List(enum1, enum2, enum3))

        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get.map(_.sorted ==> List(enum1, enum2, enum3))
        _ <- inputMolecule(List(Set(enum1), Set(enum2, enum3))).get.map(_.sorted ==> List(enum1, enum2))
        _ <- inputMolecule(List(Set(enum1), Set(enum2), Set(enum3))).get.map(_.sorted ==> List(enum1, enum2, enum3))

        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3, enum4))).get.map(_.sorted ==> List(enum1, enum3))
      } yield ()
    }

    "!=" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums_.not(?)) // or m(Ns.enumm.enums_.!=(?))
      for {
        _ <- Ns.enumm.enums insert List(
          (enum1, Set(enum1, enum2, enum3)),
          (enum2, Set(enum2, enum3, enum4)),
          (enum3, Set(enum3, enum4, enum5))
        )

        _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
        _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3))
        _ <- inputMolecule(List(Set(enum1))).get.map(_.sorted ==> List(enum2, enum3))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(enum3))
        _ <- inputMolecule(List(Set(enum3))).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(enum1, enum2))).get.map(_.sorted ==> List(enum2, enum3))
        _ <- inputMolecule(List(Set(enum1, enum3))).get.map(_.sorted ==> List(enum2, enum3))
        _ <- inputMolecule(List(Set(enum2, enum3))).get.map(_ ==> List(enum3))

        _ <- inputMolecule(List(Set(enum1), Set(enum1))).get.map(_.sorted ==> List(enum2, enum3))
        _ <- inputMolecule(List(Set(enum1), Set(enum2))).get.map(_ ==> List(enum3))
        _ <- inputMolecule(List(Set(enum1), Set(enum3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum1), Set(enum4))).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum2, enum3))).get.map(_ ==> List(enum3))
        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum4, enum5))).get.map(_ ==> List(enum2))
      } yield ()
    }

    ">" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums_.>(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
        _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

        // (enum3, enum4), (enum4, enum5), (enum4, enum5, enum6)
        _ <- inputMolecule(List(Set(enum2))).get.map(_.sorted ==> List(enum2, enum3, enum4, enum5))

        _ <- inputMolecule(List(Set(enum2, enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    ">=" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums_.>=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
        _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

        // (enum2, enum4), (enum3, enum4), (enum4, enum5), (enum4, enum5, enum6)
        _ <- inputMolecule(List(Set(enum2))).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

        _ <- inputMolecule(List(Set(enum2, enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    "<" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums_.<(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
        _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(enum1))

        _ <- inputMolecule(List(Set(enum2, enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    "<=" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums_.<=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
        _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

        _ <- inputMolecule(List(Set(enum2))).get.map(_.sorted ==> List(enum1, enum2))

        _ <- inputMolecule(List(Set(enum2, enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }
  }
}
