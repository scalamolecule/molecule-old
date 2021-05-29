package moleculeTests.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1Double extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.double$ insert List(
      (str1, Some(double1)),
      (str2, Some(double2)),
      (str3, Some(double3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.double.doubles$ insert List(
      (double1, Some(Set(double1, double2))),
      (double2, Some(Set(double2, double3))),
      (double3, Some(Set(double3, double4))),
      (double4, Some(Set(double4, double5))),
      (double5, Some(Set(double4, double5, double6))),
      (double6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.double(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(List(double1)).get.map(_ ==> List(double1))
            _ <- inputMolecule(List(double1, double1)).get.map(_ ==> List(double1))
            _ <- inputMolecule(List(double1, double2)).get.map(_.sorted ==> List(double1, double2))

            // Varargs
            _ <- inputMolecule(double1).get.map(_ ==> List(double1))
            _ <- inputMolecule(double1, double2).get.map(_.sorted ==> List(double1, double2))

            // `or`
            _ <- inputMolecule(double1 or double2).get.map(_.sorted ==> List(double1, double2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3))

            _ <- inputMolecule(List(double1)).get.map(_.sorted ==> List(double2, double3))
            _ <- inputMolecule(List(double1, double1)).get.map(_.sorted ==> List(double2, double3))
            _ <- inputMolecule(List(double1, double2)).get.map(_.sorted ==> List(double3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.double.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3))
            _ <- inputMolecule(List(double2)).get.map(_.sorted ==> List(double3))
            _ <- inputMolecule(List(double2, double3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3))
            _ <- inputMolecule(List(double2)).get.map(_.sorted ==> List(double2, double3))
            _ <- inputMolecule(List(double2, double3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.double.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3))
            _ <- inputMolecule(List(double2)).get.map(_ ==> List(double1))
            _ <- inputMolecule(List(double2, double3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3))
            _ <- inputMolecule(List(double2)).get.map(_.sorted ==> List(double1, double2))
            _ <- inputMolecule(List(double2, double3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.double_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str4))
            _ <- inputMolecule(List(double1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(double1, double1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(double1, double2)).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.double_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(double1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(double1, double1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(double1, double2)).get.map(_.sorted ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.double_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(double2)).get.map(_.sorted ==> List(str3))
            _ <- inputMolecule(List(double2, double3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.double_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(double2)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(double2, double3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.double_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(double2)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(double2, double3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.double_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(double2)).get.map(_.sorted ==> List(str1, str2))
            _ <- inputMolecule(List(double2, double3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }

    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> Nil)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(double1))).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(List(Set(double1, double1))).get.map(_ ==> List((double1, Set(double1, double2))))

            _ <- inputMolecule(List(Set(double1, double2))).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(List(Set(double1, double3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(double2, double3))).get.map(_ ==> List((double2, Set(double3, double2))))
            _ <- inputMolecule(List(Set(double4, double5))).get.map(_ ==> List((double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))

            // 1 arg
            _ <- inputMolecule(Set(double1)).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(Set(double1, double1)).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(Set(double1, double2)).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(Set(double1, double3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(double2, double3)).get.map(_ ==> List((double2, Set(double3, double2))))
            _ <- inputMolecule(Set(double4, double5)).get.map(_ ==> List((double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(double1, double2), Set[Double]())).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(List(Set(double1), Set(double1))).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(List(Set(double1), Set(double2))).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2))))
            _ <- inputMolecule(List(Set(double1), Set(double3))).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(List(Set(double1, double2), Set(double3))).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(List(Set(double1), Set(double2, double3))).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2))))
            _ <- inputMolecule(List(Set(double1), Set(double2), Set(double3))).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(List(Set(double1, double2), Set(double3, double4))).get.map(_ ==> List((double1, Set(double1, double2)), (double3, Set(double4, double3))))


            // Multiple varargs
            _ <- inputMolecule(Set(double1, double2), Set[Double]()).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(Set(double1), Set(double1)).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(Set(double1), Set(double2)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2))))
            _ <- inputMolecule(Set(double1), Set(double3)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(Set(double1, double2), Set(double3)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(Set(double1), Set(double2, double3)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2))))
            _ <- inputMolecule(Set(double1), Set(double2), Set(double3)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(Set(double1, double2), Set(double3, double4)).get.map(_ ==> List((double1, Set(double1, double2)), (double3, Set(double4, double3))))

            // `or`
            _ <- inputMolecule(Set(double1, double2) or Set[Double]()).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(Set(double1) or Set(double1)).get.map(_ ==> List((double1, Set(double1, double2))))
            _ <- inputMolecule(Set(double1) or Set(double2)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2))))
            _ <- inputMolecule(Set(double1) or Set(double3)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(Set(double1, double2) or Set(double3)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(Set(double1) or Set(double2, double3)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2))))
            _ <- inputMolecule(Set(double1) or Set(double2) or Set(double3)).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3))))
            _ <- inputMolecule(Set(double1, double2) or Set(double3, double4)).get.map(_ ==> List((double1, Set(double1, double2)), (double3, Set(double4, double3))))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles.not(?)) // or m(Ns.double.doubles.!=(?))
          val all           = List(
            (double1, Set(double1, double2, double3)),
            (double2, Set(double2, double3, double4)),
            (double3, Set(double3, double4, double5))
          )
          for {
            _ <- manyData
            _ <- Ns.double.doubles insert all

            _ <- inputMolecule(Nil).get.map(_ ==> all)
            _ <- inputMolecule(Set[Double]()).get.map(_ ==> all)

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(double1).get.map(_ ==> ...)
            // inputMolecule(List(double1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(double1)).get.map(_ ==> List(
              // (double1, Set(double1, double2, double3)),  // double1 match
              (double2, Set(double2, double3, double4)),
              (double3, Set(double3, double4, double5))
            ))
            // Same as
            _ <- inputMolecule(List(Set(double1))).get.map(_ ==> List(
              (double2, Set(double2, double3, double4)),
              (double3, Set(double3, double4, double5))
            ))

            _ <- inputMolecule(Set(double2)).get.map(_ ==> List(
              // (double1, Set(double1, double2, double3)),  // double2 match
              // (double2, Set(double2, double3, double4)),  // double2 match
              (double3, Set(double3, double4, double5))
            ))

            _ <- inputMolecule(Set(double3)).get.map(_ ==> Nil) // double3 match all


            _ <- inputMolecule(Set(double1), Set(double2)).get.map(_ ==> List(
              // (double1, Set(double1, double2, double3)),  // double1 match, double2 match
              // (double2, Set(double2, double3, double4)),  // double2 match
              (double3, Set(double3, double4, double5))
            ))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(double1, double2)).get.map(_ ==> List(
              // (double1, Set(double1, double2, double3)),  // double1 AND double2 match
              (double2, Set(double2, double3, double4)),
              (double3, Set(double3, double4, double5))
            ))


            _ <- inputMolecule(Set(double1), Set(double3)).get.map(_ ==> Nil) // double3 match all
            _ <- inputMolecule(Set(double1, double3)).get.map(_ ==> List(
              // (double1, Set(double1, double2, double3)),  // double1 AND double3 match
              (double2, Set(double2, double3, double4)),
              (double3, Set(double3, double4, double5))
            ))


            _ <- inputMolecule(Set(double1), Set(double2), Set(double3)).get.map(_ ==> Nil) // double3 match all
            _ <- inputMolecule(Set(double1, double2, double3)).get.map(_ ==> List(
              // (double1, Set(double1, double2, double3)),  // double1 AND double2 AND double3 match
              (double2, Set(double2, double3, double4)),
              (double3, Set(double3, double4, double5))
            ))


            _ <- inputMolecule(Set(double1, double2), Set(double1)).get.map(_ ==> List(
              (double2, Set(double2, double3, double4)),
              (double3, Set(double3, double4, double5))
            ))
            _ <- inputMolecule(Set(double1, double2), Set(double2)).get.map(_ ==> List(
              (double3, Set(double3, double4, double5))
            ))
            _ <- inputMolecule(Set(double1, double2), Set(double3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(double1, double2), Set(double4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(double1, double2), Set(double5)).get.map(_ ==> List(
              (double2, Set(double2, double3, double4))
            ))

            _ <- inputMolecule(Set(double1, double2), Set(double2, double3)).get.map(_ ==> List(
              (double3, Set(double3, double4, double5))
            ))
            _ <- inputMolecule(Set(double1, double2), Set(double4, double5)).get.map(_ ==> List(
              (double2, Set(double2, double3, double4))
            ))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))

            // (double3, double4), (double4, double5), (double4, double5, double6)
            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List((double2, Set(double3)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))

            // (double2, double4), (double3, double4), (double4, double5), (double4, double5, double6)
            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List((double1, Set(double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))

            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List((double1, Set(double1))))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double3, double2)), (double3, Set(double4, double3)), (double4, Set(double4, double5)), (double5, Set(double4, double6, double5))))

            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List((double1, Set(double1, double2)), (double2, Set(double2))))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.doubles(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> Nil)

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(double1))).get.map(_ ==> List(Set(double1, double2)))
            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List(Set(double1, double2, double3))) // (double1, double2) + (double2, double3)
            _ <- inputMolecule(List(Set(double3))).get.map(_ ==> List(Set(double2, double3, double4))) // (double2, double3) + (double3, double4)

            _ <- inputMolecule(List(Set(double1, double2))).get.map(_ ==> List(Set(double1, double2)))
            _ <- inputMolecule(List(Set(double1, double3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(double2, double3))).get.map(_ ==> List(Set(double2, double3)))
            _ <- inputMolecule(List(Set(double4, double5))).get.map(_ ==> List(Set(double4, double5, double6))) // (double4, double5) + (double4, double5, double6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(double1), Set(double1))).get.map(_ ==> List(Set(double1, double2)))
            _ <- inputMolecule(List(Set(double1), Set(double2))).get.map(_ ==> List(Set(double1, double2, double3))) // (double1, double2) + (double2, double3)
            _ <- inputMolecule(List(Set(double1), Set(double3))).get.map(_ ==> List(Set(double1, double4, double3, double2))) // (double1, double2) + (double2, double3) + (double3, double4)
            _ <- inputMolecule(List(Set(double2), Set(double3))).get.map(_ ==> List(Set(double1, double2, double3, double4))) // (double1, double2) + (double2, double3) + (double3, double4)

            _ <- inputMolecule(List(Set(double1, double2), Set(double3))).get.map(_ ==> List(Set(double1, double2, double3, double4))) // (double1, double2) + (double2, double3) + (double3, double4)
            _ <- inputMolecule(List(Set(double1), Set(double2, double3))).get.map(_ ==> List(Set(double1, double3, double2))) // (double1, double2) + (double2, double3)
            _ <- inputMolecule(List(Set(double1), Set(double2), Set(double3))).get.map(_ ==> List(Set(double1, double2, double3, double4))) // (double1, double2) + (double2, double3) + (double3, double4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.doubles.not(?)) // or m(Ns.double.doubles.!=(?))
          for {
            _ <- manyData
            _ <- Ns.double.doubles insert List(
              (double1, Set(double1, double2, double3)),
              (double2, Set(double2, double3, double4)),
              (double3, Set(double3, double4, double5))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(double1, double2, double3, double4, double5)))
            _ <- inputMolecule(Set[Double]()).get.map(_ ==> List(Set(double1, double2, double3, double4, double5)))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(double1).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(double1)).get.map(_ ==> List(Set(double2, double3, double4, double5)))
            // Same as
            _ <- inputMolecule(List(Set(double1))).get.map(_ ==> List(Set(double2, double3, double4, double5)))

            _ <- inputMolecule(Set(double2)).get.map(_ ==> List(Set(double3, double4, double5)))
            _ <- inputMolecule(Set(double3)).get.map(_ ==> Nil) // double3 match all

            _ <- inputMolecule(Set(double1), Set(double2)).get.map(_ ==> List(Set(double3, double4, double5)))
            _ <- inputMolecule(Set(double1), Set(double3)).get.map(_ ==> Nil) // double3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(double1, double2)).get.map(_ ==> List(Set(double2, double3, double4, double5)))
            _ <- inputMolecule(Set(double1, double3)).get.map(_ ==> List(Set(double2, double3, double4, double5)))

            _ <- inputMolecule(Set(double1), Set(double2), Set(double3)).get.map(_ ==> Nil) // double3 match all
            _ <- inputMolecule(Set(double1, double2, double3)).get.map(_ ==> List(Set(double2, double3, double4, double5)))

            _ <- inputMolecule(Set(double1, double2), Set(double1)).get.map(_ ==> List(Set(double2, double3, double4, double5)))
            _ <- inputMolecule(Set(double1, double2), Set(double2)).get.map(_ ==> List(Set(double3, double4, double5)))
            _ <- inputMolecule(Set(double1, double2), Set(double3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(double1, double2), Set(double4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(double1, double2), Set(double5)).get.map(_ ==> List(Set(double2, double3, double4)))

            _ <- inputMolecule(Set(double1, double2), Set(double2, double3)).get.map(_ ==> List(Set(double3, double4, double5)))
            _ <- inputMolecule(Set(double1, double2), Set(double4, double5)).get.map(_ ==> List(Set(double2, double3, double4)))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.doubles.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(double1, double2, double3, double4, double5, double6)))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List(Set(double1, double2, double3, double4, double5, double6)))

            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List(Set(double3, double4, double5, double6)))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.doubles.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(double1, double2, double3, double4, double5, double6)))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List(Set(double1, double2, double3, double4, double5, double6)))

            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List(Set(double2, double3, double4, double5, double6)))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.doubles.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(double1, double2, double3, double4, double5, double6)))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List(Set(double1, double2, double3, double4, double5, double6)))

            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List(Set(double1)))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.doubles.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(double1, double2, double3, double4, double5, double6)))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List(Set(double1, double2, double3, double4, double5, double6)))

            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List(Set(double1, double2)))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(double6))
            _ <- inputMolecule(List(Set[Double]())).get.map(_ ==> List(double6))


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(double1))).get.map(_ ==> List(double1))
            _ <- inputMolecule(List(Set(double2))).get.map(_.sorted ==> List(double1, double2))
            _ <- inputMolecule(List(Set(double3))).get.map(_.sorted ==> List(double2, double3))

            _ <- inputMolecule(List(Set(double1, double1))).get.map(_ ==> List(double1))
            _ <- inputMolecule(List(Set(double1, double2))).get.map(_ ==> List(double1))
            _ <- inputMolecule(List(Set(double1, double3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(double2, double3))).get.map(_ ==> List(double2))
            _ <- inputMolecule(List(Set(double4, double5))).get.map(_.sorted ==> List(double4, double5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(double1, double2), Set[Double]())).get.map(_ ==> List(double1))
            _ <- inputMolecule(List(Set(double1), Set(double1))).get.map(_ ==> List(double1))
            _ <- inputMolecule(List(Set(double1), Set(double2))).get.map(_.sorted ==> List(double1, double2))
            _ <- inputMolecule(List(Set(double1), Set(double3))).get.map(_.sorted ==> List(double1, double2, double3))

            _ <- inputMolecule(List(Set(double1, double2), Set(double3))).get.map(_.sorted ==> List(double1, double2, double3))
            _ <- inputMolecule(List(Set(double1), Set(double2, double3))).get.map(_.sorted ==> List(double1, double2))
            _ <- inputMolecule(List(Set(double1), Set(double2), Set(double3))).get.map(_.sorted ==> List(double1, double2, double3))

            _ <- inputMolecule(List(Set(double1, double2), Set(double3, double4))).get.map(_.sorted ==> List(double1, double3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles_.not(?)) // or m(Ns.double.doubles.!=(?))
          for {
            _ <- manyData
            _ <- Ns.double.doubles insert List(
              (double1, Set(double1, double2, double3)),
              (double2, Set(double2, double3, double4)),
              (double3, Set(double3, double4, double5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3))
            _ <- inputMolecule(Set[Double]()).get.map(_.sorted ==> List(double1, double2, double3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(double1).get.map(_ ==> ...)
            // inputMolecule(List(double1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(double1)).get.map(_.sorted ==> List(double2, double3))
            // Same as
            _ <- inputMolecule(List(Set(double1))).get.map(_.sorted ==> List(double2, double3))

            _ <- inputMolecule(Set(double2)).get.map(_ ==> List(double3))
            _ <- inputMolecule(Set(double3)).get.map(_ ==> Nil) // double3 match all


            _ <- inputMolecule(Set(double1), Set(double2)).get.map(_ ==> List(double3))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(double1, double2)).get.map(_.sorted ==> List(double2, double3))

            _ <- inputMolecule(Set(double1), Set(double3)).get.map(_ ==> Nil) // double3 match all
            _ <- inputMolecule(Set(double1, double3)).get.map(_.sorted ==> List(double2, double3))

            _ <- inputMolecule(Set(double1), Set(double2), Set(double3)).get.map(_ ==> Nil) // double3 match all
            _ <- inputMolecule(Set(double1, double2, double3)).get.map(_.sorted ==> List(double2, double3))

            _ <- inputMolecule(Set(double1, double2), Set(double1)).get.map(_.sorted ==> List(double2, double3))
            _ <- inputMolecule(Set(double1, double2), Set(double2)).get.map(_ ==> List(double3))
            _ <- inputMolecule(Set(double1, double2), Set(double3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(double1, double2), Set(double4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(double1, double2), Set(double5)).get.map(_ ==> List(double2))

            _ <- inputMolecule(Set(double1, double2), Set(double2, double3)).get.map(_ ==> List(double3))
            _ <- inputMolecule(Set(double1, double2), Set(double4, double5)).get.map(_ ==> List(double2))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))
            _ <- inputMolecule(List(Set[Double]())).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))

            // (double3, double4), (double4, double5), (double4, double5, double6)
            _ <- inputMolecule(List(Set(double2))).get.map(_.sorted ==> List(double2, double3, double4, double5))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))
            _ <- inputMolecule(List(Set[Double]())).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))

            // (double2, double4), (double3, double4), (double4, double5), (double4, double5, double6)
            _ <- inputMolecule(List(Set(double2))).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))
            _ <- inputMolecule(List(Set[Double]())).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))

            _ <- inputMolecule(List(Set(double2))).get.map(_ ==> List(double1))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.double.doubles_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))
            _ <- inputMolecule(List(Set[Double]())).get.map(_.sorted ==> List(double1, double2, double3, double4, double5))

            _ <- inputMolecule(List(Set(double2))).get.map(_.sorted ==> List(double1, double2))

            _ <- inputMolecule(List(Set(double2, double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(double2), Set(double3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }
  }
}