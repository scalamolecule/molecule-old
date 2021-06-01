package moleculeTests.tests.core.input1.resolution

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object IntCard2coalesce extends AsyncTestSuite {

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.int.ints$ insert List(
      (int1, Some(Set(int1, int2))),
      (int2, Some(Set(int2, int3))),
      (int3, Some(Set(int3, int4))),
      (int4, Some(Set(int4, int5))),
      (int5, Some(Set(int4, int5, int6))),
      (int6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Eq" - core { implicit conn =>
      val inputMolecule = m(Ns.ints(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(int1))).get.map(_ ==> List(Set(int1, int2)))
        _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int1, int2, int3))) // (int1, int2) + (int2, int3)
        _ <- inputMolecule(List(Set(int3))).get.map(_ ==> List(Set(int2, int3, int4))) // (int2, int3) + (int3, int4)

        _ <- inputMolecule(List(Set(int1, int1))).get.map(_ ==> List(Set(int1, int2)))
        _ <- inputMolecule(List(Set(int1, int2))).get.map(_ ==> List(Set(int1, int2)))
        _ <- inputMolecule(List(Set(int1, int3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(int2, int3))).get.map(_ ==> List(Set(int2, int3)))
        _ <- inputMolecule(List(Set(int4, int5))).get.map(_ ==> List(Set(int4, int5, int6))) // (int4, int5) + (int4, int5, int6)

        _ <- inputMolecule(List(Set(int1, int2), Set[Int]())).get.map(_ ==> List(Set(int1, int2)))

        _ <- inputMolecule(List(Set(int1), Set(int1))).get.map(_ ==> List(Set(int1, int2)))
        _ <- inputMolecule(List(Set(int1), Set(int2))).get.map(_ ==> List(Set(int1, int2, int3))) // (int1, int2) + (int2, int3)
        _ <- inputMolecule(List(Set(int1), Set(int3))).get.map(_ ==> List(Set(int1, int4, int3, int2))) // (int1, int2) + (int2, int3) + (int3, int4)

        _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get.map(_ ==> List(Set(int1, int2, int3, int4))) // (int1, int2) + (int2, int3) + (int3, int4)
        _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get.map(_ ==> List(Set(int1, int2, int3))) // (int1, int2) + (int2, int3)
        _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get.map(_ ==> List(Set(int1, int2, int3, int4))) // (int1, int2) + (int2, int3)
        _ <- inputMolecule(List(Set(int1, int2), Set(int3, int4))).get.map(_ ==> List(Set(int1, int2, int3, int4))) // (int1, int2) + (int3, int4)
      } yield ()
    }

    "!=" - core { implicit conn =>
      val inputMolecule = m(Ns.ints.not(?)) // or m(Ns.ints.!=(?))
      for {
        _ <- Ns.int.ints insert List(
          (int1, Set(int1, int2, int3)),
          (int2, Set(int2, int3, int4)),
          (int3, Set(int3, int4, int5))
        )

        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int1, int2, int3, int4, int5)))

        _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int1, int2, int3, int4, int5)))

        _ <- inputMolecule(List(Set(int1))).get.map(_ ==> List(Set(int2, int3, int4, int5)))
        _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int3, int4, int5)))
        _ <- inputMolecule(List(Set(int3))).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(int1, int2))).get.map(_ ==> List(Set(int2, int3, int4, int5)))
        _ <- inputMolecule(List(Set(int1, int3))).get.map(_ ==> List(Set(int2, int3, int4, int5)))

        _ <- inputMolecule(List(Set(int1), Set(int1))).get.map(_ ==> List(Set(int2, int3, int4, int5)))
        _ <- inputMolecule(List(Set(int1), Set(int2))).get.map(_ ==> List(Set(int3, int4, int5)))
        _ <- inputMolecule(List(Set(int1), Set(int3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(int1), Set(int4))).get.map(_ ==> Nil)

        _ <- inputMolecule(List(Set(int1, int2), Set(int1))).get.map(_ ==> List(Set(int2, int3, int4, int5)))
        _ <- inputMolecule(List(Set(int1, int2), Set(int2))).get.map(_ ==> List(Set(int3, int4, int5)))
        _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get.map(_ ==> Nil)
        _ <- inputMolecule(List(Set(int1, int2), Set(int2, int3))).get.map(_ ==> List(Set(int3, int4, int5)))
      } yield ()
    }

    ">" - core { implicit conn =>
      val inputMolecule = m(Ns.ints.>(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int5, int1, int6, int2, int3, int4)))

        _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int5, int1, int6, int2, int3, int4)))

        // (int3, int4), (int4, int5), (int4, int5, int6)
        _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int4, int6, int3, int5)))
        _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
        err ==> "Can't apply multiple values to comparison function."
    }


        _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
        err ==> "Can't apply multiple values to comparison function."
    }
      } yield ()
    }

    ">=" - core { implicit conn =>
      val inputMolecule = m(Ns.ints.>=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int5, int1, int6, int2, int3, int4)))

        _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int5, int1, int6, int2, int3, int4)))

        // (int2, int4), (int3, int4), (int4, int5), (int4, int5, int6)
        _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int5, int6, int2, int3, int4)))
        _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
        err ==> "Can't apply multiple values to comparison function."
    }


        _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
        err ==> "Can't apply multiple values to comparison function."
    }
      } yield ()
    }

    "<" - core { implicit conn =>
      val inputMolecule = m(Ns.ints.<(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int5, int1, int6, int2, int3, int4)))

        _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int5, int1, int6, int2, int3, int4)))

        _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int1)))
        _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
        err ==> "Can't apply multiple values to comparison function."
    }


        _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
        err ==> "Can't apply multiple values to comparison function."
    }
      } yield ()
    }

    "<=" - core { implicit conn =>
      val inputMolecule = m(Ns.ints.<=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int5, int1, int6, int2, int3, int4)))

        _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int5, int1, int6, int2, int3, int4)))

        _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int1, int2)))
        _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
        err ==> "Can't apply multiple values to comparison function."
    }


        _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
        err ==> "Can't apply multiple values to comparison function."
    }
      } yield ()
    }
  }
}