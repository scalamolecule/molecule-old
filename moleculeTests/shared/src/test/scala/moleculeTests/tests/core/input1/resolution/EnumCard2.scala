package moleculeTests.tests.core.input1.resolution

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object EnumCard2 extends AsyncTestSuite {

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.enumm.enums$ insert List(
      (enum1, Some(Set(enum1, enum2))),
      (enum2, Some(Set(enum2, enum3))),
      (enum3, Some(Set(enum3, enum4))),
      (enum4, Some(Set(enum4, enum5))),
      (enum5, Some(Set(enum4, enum5, enum6))),
      (enum6, None)
    )
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Eq" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(enum1))).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(List(Set(enum1, enum1))).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(List(Set(enum1, enum2))).get.map(_ ==> List((enum1, Set(enum1, enum2))))

        _ <- inputMolecule(List(Set(enum1, enum3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum2, enum3))).get.map(_ ==> List((enum2, Set(enum3, enum2))))
        _ <- inputMolecule(List(Set(enum4, enum5))).get.map(_ ==> List((enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))

        // 1 arg
        _ <- inputMolecule(Set(enum1)).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(Set(enum1, enum1)).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(Set(enum1, enum2)).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(Set(enum1, enum3)).get.map(_ ==> Nil)
        _ <- inputMolecule(Set(enum2, enum3)).get.map(_ ==> List((enum2, Set(enum3, enum2))))
        _ <- inputMolecule(Set(enum4, enum5)).get.map(_ ==> List((enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))


        // Values of each Set matches values of 1 card-many attributes respectively

        _ <- inputMolecule(List(Set(enum1, enum2), Set[String]())).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(List(Set(enum1), Set(enum1))).get.map(_ ==> List((enum1, Set(enum1, enum2))))

        _ <- inputMolecule(List(Set(enum1), Set(enum2))).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2))))
        _ <- inputMolecule(List(Set(enum1), Set(enum3))).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(List(Set(enum1), Set(enum2, enum3))).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2))))
        _ <- inputMolecule(List(Set(enum1), Set(enum2), Set(enum3))).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3, enum4))).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum3, Set(enum4, enum3))))

        // Multiple varargs
        _ <- inputMolecule(Set(enum1, enum2), Set[String]()).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(Set(enum1), Set(enum1)).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(Set(enum1), Set(enum2)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2))))
        _ <- inputMolecule(Set(enum1), Set(enum3)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(Set(enum1, enum2), Set(enum3)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(Set(enum1), Set(enum2, enum3)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2))))
        _ <- inputMolecule(Set(enum1), Set(enum2), Set(enum3)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(Set(enum1, enum2), Set(enum3, enum4)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum3, Set(enum4, enum3))))

        // `or`
        _ <- inputMolecule(Set(enum1, enum2) or Set[String]()).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(Set(enum1) or Set(enum1)).get.map(_ ==> List((enum1, Set(enum1, enum2))))
        _ <- inputMolecule(Set(enum1) or Set(enum2)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2))))
        _ <- inputMolecule(Set(enum1) or Set(enum3)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(Set(enum1, enum2) or Set(enum3)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(Set(enum1) or Set(enum2, enum3)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2))))
        _ <- inputMolecule(Set(enum1) or Set(enum2) or Set(enum3)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3))))
        _ <- inputMolecule(Set(enum1, enum2) or Set(enum3, enum4)).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum3, Set(enum4, enum3))))
      } yield ()
    }

    "not" - core { implicit conn =>
      val inputMolecule = m(Ns.int.enums.not(?))
      val all           = List(
        (1, Set(enum1, enum2, enum3)),
        (2, Set(enum2, enum3, enum4)),
        (3, Set(enum3, enum4, enum5))
      )
      for {
        _ <- manyData
        _ <- Ns.int.enums insert all

        _ <- inputMolecule(Nil).get.map(_ ==> all)
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> all)

        _ <- inputMolecule(List(Set(enum1))).get.map(_ ==> List(
          // (1, Set(enum1, enum2, enum3)),  // enum1 match
          (2, Set(enum2, enum3, enum4)),
          (3, Set(enum3, enum4, enum5))
        ))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List(
          // (1, Set(enum1, enum2, enum3)),  // enum2 match
          // (2, Set(enum2, enum3, enum4)),  // enum2 match
          (3, Set(enum3, enum4, enum5))
        ))
        _ <- inputMolecule(List(Set(enum3))).get.map(_ ==> Nil) // enum3 match all

        _ <- inputMolecule(List(Set(enum1, enum2))).get.map(_ ==> List(
          // (1, Set(enum1, enum2, enum3)),  // enum1 AND enum2 match
          (2, Set(enum2, enum3, enum4)),
          (3, Set(enum3, enum4, enum5))
        ))
        _ <- inputMolecule(List(Set(enum1, enum3))).get.map(_ ==> List(
          // (1, Set(enum1, enum2, enum3)),  // enum1 AND enum3 match
          (2, Set(enum2, enum3, enum4)),
          (3, Set(enum3, enum4, enum5))
        ))
        _ <- inputMolecule(List(Set(enum2, enum3))).get.map(_ ==> List(
          (3, Set(enum3, enum4, enum5))
        ))
        _ <- inputMolecule(List(Set(enum1), Set(enum1))).get.map(_ ==> List(
          // (1, Set(enum1, enum2, enum3)),  // enum1 match
          (2, Set(enum2, enum3, enum4)),
          (3, Set(enum3, enum4, enum5))
        ))
        _ <- inputMolecule(List(Set(enum1), Set(enum2))).get.map(_ ==> List(
          // (1, Set(enum1, enum2, enum3)),  // enum1 match, enum2 match
          // (2, Set(enum2, enum3, enum4)),  // enum2 match
          (3, Set(enum3, enum4, enum5))
        ))
        _ <- inputMolecule(List(Set(enum1), Set(enum3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum1), Set(enum4))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(enum1, enum2), Set(enum2, enum3))).get.map(_ ==> List(
          (3, Set(enum3, enum4, enum5))
        ))
      } yield ()
    }

    ">" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums.>(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))

        // (enum3, enum4), (enum4, enum5), (enum4, enum5, enum6)
        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List((enum2, Set(enum3)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))

        _ <- inputMolecule(List(Set(enum2, enum3))).get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    ">=" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums.>=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))

        // (enum2, enum4), (enum3, enum4), (enum4, enum5), (enum4, enum5, enum6)
        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List((enum1, Set(enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))

        _ <- inputMolecule(List(Set(enum2, enum3))).get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    "<" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums.<(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List((enum1, Set(enum1))))

        _ <- inputMolecule(List(Set(enum2, enum3))).get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }

    "<=" - core { implicit conn =>
      val inputMolecule = m(Ns.enumm.enums.<=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))
        _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5))))

        _ <- inputMolecule(List(Set(enum2))).get.map(_ ==> List((enum1, Set(enum1, enum2)), (enum2, Set(enum2))))

        _ <- inputMolecule(List(Set(enum2, enum3))).get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }

        _ <- inputMolecule(List(Set(enum2), Set(enum3))).get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Can't apply multiple values to comparison function."
        }
      } yield ()
    }
  }
}
