package moleculeTests.tests.core.input1.resolution

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object EnumCard2coalesce extends AsyncTestSuite {

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.enum.enums$ insert List(
      (enum1, Some(Set(enum1, enum2))),
      (enum2, Some(Set(enum2, enum3))),
      (enum3, Some(Set(enum3, enum4))),
      (enum4, Some(Set(enum4, enum5))),
      (enum5, Some(Set(enum4, enum5, enum6))),
      (enum6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Eq" - core { implicit conn =>
      val inputMolecule = m(Ns.enums(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(enum1))).get.map(_ ==> List(Set(enum1, enum2)))
        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(Set(enum1, enum2, enum3))) // (enum1, enum2) + (enum2, enum3
        _ <- inputMolecule(List(Set(enum3))).get.map(_ ==> List(Set(enum2, enum3, enum4))) // (enum2, enum3) + (enum3, enum4

        _ <- inputMolecule(List(Set(enum1, enum2))).get.map(_ ==> List(Set(enum1, enum2)))
        _ <- inputMolecule(List(Set(enum1, enum3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum2, enum3))).get.map(_ ==> List(Set(enum2, enum3)))
        _ <- inputMolecule(List(Set(enum4, enum5))).get.map(_ ==> List(Set(enum4, enum5, enum6))) // (enum4, enum5) + (enum4, enum5, enum6

        _ <- inputMolecule(List(Set(enum1), Set(enum1))).get.map(_ ==> List(Set(enum1, enum2)))
        _ <- inputMolecule(List(Set(enum1), Set(enum2))).get.map(_ ==> List(Set(enum1, enum2, enum3))) // (enum1, enum2) + (enum2, enum3)
        _ <- inputMolecule(List(Set(enum1), Set(enum3))).get.map(_ ==> List(Set(enum1, enum4, enum3, enum2))) // (enum1, enum2) + (enum2, enum3) + (enum3, enum4)
        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4))) // (enum1, enum2) + (enum2, enum3) + (enum3, enum4)

        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4))) // (enum1, enum2) + (enum2, enum3) + (enum3, enum4)
        _ <- inputMolecule(List(Set(enum1), Set(enum2, enum3))).get.map(_ ==> List(Set(enum1, enum3, enum2))) // (enum1, enum2) + (enum2, enum3)
        _ <- inputMolecule(List(Set(enum1), Set(enum2), Set(enum3))).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4))) // (enum1, enum2) + (enum2, enum3) + (enum3, enum4)
      } yield ()
    }

    "!=" - core { implicit conn =>
      val inputMolecule = m(Ns.enums.not(?)) // or m(Ns.enums.!=(?))
      for {
        _ <- Ns.enum.enums insert List(
          (enum1, Set(enum1, enum2, enum3)),
          (enum2, Set(enum2, enum3, enum4)),
          (enum3, Set(enum3, enum4, enum5))
        )

        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5)))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5)))

        _ <- inputMolecule(List(Set(enum1))).get.map(_ ==> List(Set(enum2, enum3, enum4, enum5)))
        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(Set(enum3, enum4, enum5)))
        _ <- inputMolecule(List(Set(enum3))).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(enum1, enum2))).get.map(_ ==> List(Set(enum2, enum3, enum4, enum5)))
        // nothing omitted
        _ <- inputMolecule(List(Set(enum1, enum3))).get.map(_ ==> List(Set(enum2, enum3, enum4, enum5)))

        _ <- inputMolecule(List(Set(enum1), Set(enum1))).get.map(_ ==> List(Set(enum2, enum3, enum4, enum5)))
        _ <- inputMolecule(List(Set(enum1), Set(enum2))).get.map(_ ==> List(Set(enum3, enum4, enum5)))
        _ <- inputMolecule(List(Set(enum1), Set(enum3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum1), Set(enum4))).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum1))).get.map(_ ==> List(Set(enum2, enum3, enum4, enum5)))
        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum2))).get.map(_ ==> List(Set(enum3, enum4, enum5)))
        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum2, enum3))).get.map(_ ==> List(Set(enum3, enum4, enum5)))
      } yield ()
    }

    ">" - core { implicit conn =>
      val inputMolecule = m(Ns.enums.>(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5, enum6)))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5, enum6)))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(Set(enum3, enum4, enum5, enum6)))

        _ <- inputMolecule(List(Set(enum2, enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    ">=" - core { implicit conn =>
      val inputMolecule = m(Ns.enums.>=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5, enum6)))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5, enum6)))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(Set(enum2, enum3, enum4, enum5, enum6)))

        _ <- inputMolecule(List(Set(enum2, enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    "<" - core { implicit conn =>
      val inputMolecule = m(Ns.enums.<(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5, enum6)))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5, enum6)))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(Set(enum1)))

        _ <- inputMolecule(List(Set(enum2, enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    "<=" - core { implicit conn =>
      val inputMolecule = m(Ns.enums.<=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5, enum6)))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(enum1, enum2, enum3, enum4, enum5, enum6)))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(Set(enum1, enum2)))

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
