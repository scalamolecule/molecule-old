package molecule.tests.core.input1.expression

import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}


object Input1Boolean extends AsyncTestSuite {

  def oneData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.int.bool$ insert List(
      (1, Some(true)),
      (2, Some(false)),
      (3, Some(true)),
      (4, Some(false)),
      (5, None)
    )
  }

  def manyData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.int.bools$ insert List(
      (1, Some(Set(true))),
      (2, Some(Set(false))),
      (3, Some(Set(true, false))),
      (4, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bool(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === Nil

            _ <- inputMolecule(List(true)).get === List(true)
            _ <- inputMolecule(List(true, true)).get === List(true)
            _ <- inputMolecule(List(true, false)).get === List(false, true)

            // Varargs
            _ <- inputMolecule(true).get === List(true)
            _ <- inputMolecule(true, false).get.map(_.sorted ==> List(false, true))

            // `or`
            _ <- inputMolecule(true or false).get.map(_.sorted ==> List(false, true))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bool.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(false, true))

            _ <- inputMolecule(List(true)).get.map(_.sorted ==> List(false))
            _ <- inputMolecule(List(true, true)).get.map(_.sorted ==> List(false))
            _ <- inputMolecule(List(true, false)).get.map(_.sorted ==> Nil)
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.bool_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(5)
            _ <- inputMolecule(List(true)).get === List(1, 3)
            _ <- inputMolecule(List(true, true)).get === List(1, 3)
            _ <- inputMolecule(List(true, false)).get.map(_.sorted ==> List(1, 2, 3, 4))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.bool_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(1, 2, 3, 4))
            _ <- inputMolecule(List(true)).get.map(_.sorted ==> List(2, 4))
            _ <- inputMolecule(List(true, true)).get.map(_.sorted ==> List(2, 4))
            _ <- inputMolecule(List(true, false)).get.map(_.sorted ==> Nil // No other values to negate)
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.bools(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[Boolean]())).get === Nil

            _ <- inputMolecule(List(Set(true))).get === List((1, Set(true)), (3, Set(true, false)))
            _ <- inputMolecule(List(Set(false))).get === List((2, Set(false)), (3, Set(true, false)))
            _ <- inputMolecule(List(Set(true, false))).get === List((3, Set(true, false)))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.bools.not(?)) // or m(Ns.int.bools.!=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((1, Set(true)), (2, Set(false)), (3, Set(true, false)))
            _ <- inputMolecule(List(Set[Boolean]())).get === List((1, Set(true)), (2, Set(false)), (3, Set(true, false)))

            _ <- inputMolecule(List(Set(true))).get === List((2, Set(false)))
            _ <- inputMolecule(List(Set(false))).get === List((1, Set(true)))
            _ <- inputMolecule(List(Set(true, false))).get === List((1, Set(true)), (2, Set(false)))
          } yield ()
        }
      }


      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bools(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[Boolean]())).get === Nil

            _ <- inputMolecule(List(Set(true))).get === List(Set(true, false)) // (true) + (true, false)
            _ <- inputMolecule(List(Set(false))).get === List(Set(true, false)) // (false) + (true, false)
            _ <- inputMolecule(List(Set(true, false))).get === List(Set(true, false)) // (true, false)

          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bools.not(?)) // or m(Ns.bools.!=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(true, false))
            _ <- inputMolecule(List(Set[Boolean]())).get === List(Set(true, false))

            _ <- inputMolecule(List(Set(true))).get === List(Set(false))
            _ <- inputMolecule(List(Set(false))).get === List(Set(true))
            _ <- inputMolecule(List(Set(true, false))).get === List(Set(true, false))
          } yield ()
        }
      }

$
      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.bools_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(4)
            _ <- inputMolecule(List(Set[Boolean]())).get === List(4)

            _ <- inputMolecule(List(Set(true))).get === List(1, 3)
            _ <- inputMolecule(List(Set(false))).get.map(_.sorted ==> List(2, 3))
            _ <- inputMolecule(List(Set(true, false))).get.map(_.sorted ==> List(3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.bools_.not(?)) // or m(Ns.int.bools_.!=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(1, 2, 3))
            _ <- inputMolecule(List(Set[Boolean]())).get.map(_.sorted ==> List(1, 2, 3))

            _ <- inputMolecule(List(Set(true))).get.map(_.sorted ==> List(2))
            _ <- inputMolecule(List(Set(false))).get.map(_.sorted ==> List(1))
            _ <- inputMolecule(List(Set(true, false))).get.map(_.sorted ==> List(1, 2))
          } yield ()
        }
      }
    }
  }
}