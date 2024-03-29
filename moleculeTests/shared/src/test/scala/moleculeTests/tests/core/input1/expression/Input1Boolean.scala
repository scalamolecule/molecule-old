package moleculeTests.tests.core.input1.expression

import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1Boolean extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.int.bool$ insert List(
      (1, Some(true)),
      (2, Some(false)),
      (3, Some(true)),
      (4, Some(false)),
      (5, None)
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.int.bools$ insert List(
      (1, Some(Set(true))),
      (2, Some(Set(false))),
      (3, Some(Set(true, false))),
      (4, None)
    )
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bool(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(List(true)).get.map(_ ==> List(true))
            _ <- inputMolecule(List(true, true)).get.map(_ ==> List(true))
            _ <- inputMolecule(List(true, false)).get.map(_ ==> List(false, true))

            // Varargs
            _ <- inputMolecule(true).get.map(_ ==> List(true))
            _ <- inputMolecule(true, false).get.map(_ ==> List(false, true))

            // `or`
            _ <- inputMolecule(true or false).get.map(_ ==> List(false, true))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bool.not(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(false, true))

            _ <- inputMolecule(List(true)).get.map(_ ==> List(false))
            _ <- inputMolecule(List(true, true)).get.map(_ ==> List(false))
            _ <- inputMolecule(List(true, false)).get.map(_ ==> Nil)
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.a1.bool_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(5))
            _ <- inputMolecule(List(true)).get.map(_ ==> List(1, 3))
            _ <- inputMolecule(List(true, true)).get.map(_ ==> List(1, 3))
            _ <- inputMolecule(List(true, false)).get.map(_ ==> List(1, 2, 3, 4))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.int.a1.bool_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(1, 2, 3, 4))
            _ <- inputMolecule(List(true)).get.map(_ ==> List(2, 4))
            _ <- inputMolecule(List(true, true)).get.map(_ ==> List(2, 4))
            _ <- inputMolecule(List(true, false)).get.map(_ ==> Nil) // No other values to negate
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.a1.bools(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[Boolean]())).get.map(_ ==> Nil)

            _ <- inputMolecule(List(Set(true))).get.map(_ ==> List((1, Set(true)), (3, Set(true, false))))
            _ <- inputMolecule(List(Set(false))).get.map(_ ==> List((2, Set(false)), (3, Set(true, false))))
            _ <- inputMolecule(List(Set(true, false))).get.map(_ ==> List((3, Set(true, false))))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.int.a1.bools.not(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((1, Set(true)), (2, Set(false)), (3, Set(true, false))))
            _ <- inputMolecule(List(Set[Boolean]())).get.map(_ ==> List((1, Set(true)), (2, Set(false)), (3, Set(true, false))))

            _ <- inputMolecule(List(Set(true))).get.map(_ ==> List((2, Set(false))))
            _ <- inputMolecule(List(Set(false))).get.map(_ ==> List((1, Set(true))))
            _ <- inputMolecule(List(Set(true, false))).get.map(_ ==> List((1, Set(true)), (2, Set(false))))
          } yield ()
        }
      }


      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bools(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[Boolean]())).get.map(_ ==> Nil)

            _ <- inputMolecule(List(Set(true))).get.map(_ ==> List(Set(true, false))) // (true) + (true, false)
            _ <- inputMolecule(List(Set(false))).get.map(_ ==> List(Set(true, false))) // (false) + (true, false)
            _ <- inputMolecule(List(Set(true, false))).get.map(_ ==> List(Set(true, false))) // (true, false)

          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bools.not(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(true, false)))
            _ <- inputMolecule(List(Set[Boolean]())).get.map(_ ==> List(Set(true, false)))

            _ <- inputMolecule(List(Set(true))).get.map(_ ==> List(Set(false)))
            _ <- inputMolecule(List(Set(false))).get.map(_ ==> List(Set(true)))
            _ <- inputMolecule(List(Set(true, false))).get.map(_ ==> List(Set(true, false)))
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.a1.bools_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(4))
            _ <- inputMolecule(List(Set[Boolean]())).get.map(_ ==> List(4))

            _ <- inputMolecule(List(Set(true))).get.map(_ ==> List(1, 3))
            _ <- inputMolecule(List(Set(false))).get.map(_ ==> List(2, 3))
            _ <- inputMolecule(List(Set(true, false))).get.map(_ ==> List(3))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.int.a1.bools_.not(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(1, 2, 3))
            _ <- inputMolecule(List(Set[Boolean]())).get.map(_ ==> List(1, 2, 3))

            _ <- inputMolecule(List(Set(true))).get.map(_ ==> List(2))
            _ <- inputMolecule(List(Set(false))).get.map(_ ==> List(1))
            _ <- inputMolecule(List(Set(true, false))).get.map(_ ==> List(1, 2))
          } yield ()
        }
      }
    }
  }
}