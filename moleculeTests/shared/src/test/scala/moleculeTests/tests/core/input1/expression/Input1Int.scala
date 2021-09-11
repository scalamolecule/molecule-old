package moleculeTests.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1Int extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.int$ insert List(
      (str1, Some(int1)),
      (str2, Some(int2)),
      (str3, Some(int3)),
      (str4, None)
    )
  }

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

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(List(int1)).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(int1, int1)).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(int1, int2)).get.map(_ ==> List(int1, int2))

            // Varargs
            _ <- inputMolecule(int1).get.map(_ ==> List(int1))
            _ <- inputMolecule(int1, int2).get.map(_ ==> List(int1, int2))

            // `or`
            _ <- inputMolecule(int1 or int2).get.map(_ ==> List(int1, int2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3))

            _ <- inputMolecule(List(int1)).get.map(_ ==> List(int2, int3))
            _ <- inputMolecule(List(int1, int1)).get.map(_ ==> List(int2, int3))
            _ <- inputMolecule(List(int1, int2)).get.map(_ ==> List(int3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.int.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3))
            _ <- inputMolecule(List(int2)).get.map(_ ==> List(int3))
            _ <- inputMolecule(List(int2, int3)).get.recover { case MoleculeException(err, _) =>
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
            _ <- inputMolecule(List(int2, int3)).get.recover { case MoleculeException(err, _) =>
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
            _ <- inputMolecule(List(int2, int3)).get.recover { case MoleculeException(err, _) =>
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
            _ <- inputMolecule(List(int2, int3)).get.recover { case MoleculeException(err, _) =>
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
            _ <- inputMolecule(Nil).get.map(_ ==> List(str4))
            _ <- inputMolecule(List(int1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(int1, int1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(int1, int2)).get.map(_ ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.int_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(int1)).get.map(_ ==> List(str2, str3))
            _ <- inputMolecule(List(int1, int1)).get.map(_ ==> List(str2, str3))
            _ <- inputMolecule(List(int1, int2)).get.map(_ ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.int_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(int2)).get.map(_ ==> List(str3))
            _ <- inputMolecule(List(int2, int3)).get.recover { case MoleculeException(err, _) =>
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
            _ <- inputMolecule(List(int2, int3)).get.recover { case MoleculeException(err, _) =>
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
            _ <- inputMolecule(List(int2, int3)).get.recover { case MoleculeException(err, _) =>
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
            _ <- inputMolecule(List(int2, int3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> Nil)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(int1))).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(List(Set(int1, int1))).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2))))

            _ <- inputMolecule(List(Set(int1, int2))).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(List(Set(int1, int3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(int2, int3))).get.map(_ ==> List((int2, Set(int3, int2))))
            _ <- inputMolecule(List(Set(int4, int5))).get.map(_ ==> List((int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))

            // 1 arg
            _ <- inputMolecule(Set(int1)).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(Set(int1, int1)).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(Set(int1, int2)).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(Set(int1, int3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(int2, int3)).get.map(_ ==> List((int2, Set(int3, int2))))
            _ <- inputMolecule(Set(int4, int5)).get.map(_ ==> List((int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(int1, int2), Set[Int]())).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(List(Set(int1), Set(int1))).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(List(Set(int1), Set(int2))).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2))))
            _ <- inputMolecule(List(Set(int1), Set(int3))).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2))))
            _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(List(Set(int1, int2), Set(int3, int4))).get.map(_ ==> List((int1, Set(int1, int2)), (int3, Set(int4, int3))))


            // Multiple varargs
            _ <- inputMolecule(Set(int1, int2), Set[Int]()).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(Set(int1), Set(int1)).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(Set(int1), Set(int2)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2))))
            _ <- inputMolecule(Set(int1), Set(int3)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(Set(int1, int2), Set(int3)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(Set(int1), Set(int2, int3)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2))))
            _ <- inputMolecule(Set(int1), Set(int2), Set(int3)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(Set(int1, int2), Set(int3, int4)).get.map(_ ==> List((int1, Set(int1, int2)), (int3, Set(int4, int3))))

            // `or`
            _ <- inputMolecule(Set(int1, int2) or Set[Int]()).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(Set(int1) or Set(int1)).get.map(_ ==> List((int1, Set(int1, int2))))
            _ <- inputMolecule(Set(int1) or Set(int2)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2))))
            _ <- inputMolecule(Set(int1) or Set(int3)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(Set(int1, int2) or Set(int3)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(Set(int1) or Set(int2, int3)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2))))
            _ <- inputMolecule(Set(int1) or Set(int2) or Set(int3)).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3))))
            _ <- inputMolecule(Set(int1, int2) or Set(int3, int4)).get.map(_ ==> List((int1, Set(int1, int2)), (int3, Set(int4, int3))))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.not(?)) // or m(Ns.int.ints.!=(?))
          val all           = List(
            (int1, Set(int1, int2, int3)),
            (int2, Set(int2, int3, int4)),
            (int3, Set(int3, int4, int5))
          )
          for {
            _ <- Ns.int.ints insert all

            _ <- inputMolecule(Nil).get.map(_ ==> all)
            _ <- inputMolecule(Set[Int]()).get.map(_ ==> all)

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(int1).get.map(_ ==> ...)
            // inputMolecule(List(int1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(int1)).get.map(_ ==> List(
              // (int1, Set(int1, int2, int3)),  // int1 match
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            ))
            // Same as
            _ <- inputMolecule(List(Set(int1))).get.map(_ ==> List(
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            ))

            _ <- inputMolecule(Set(int2)).get.map(_ ==> List(
              // (int1, Set(int1, int2, int3)),  // int2 match
              // (int2, Set(int2, int3, int4)),  // int2 match
              (int3, Set(int3, int4, int5))
            ))

            _ <- inputMolecule(Set(int3)).get.map(_ ==> Nil) // int3 match all


            _ <- inputMolecule(Set(int1), Set(int2)).get.map(_ ==> List(
              // (int1, Set(int1, int2, int3)),  // int1 match, int2 match
              // (int2, Set(int2, int3, int4)),  // int2 match
              (int3, Set(int3, int4, int5))
            ))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(int1, int2)).get.map(_ ==> List(
              // (int1, Set(int1, int2, int3)),  // int1 AND int2 match
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            ))


            _ <- inputMolecule(Set(int1), Set(int3)).get.map(_ ==> Nil) // int3 match all
            _ <- inputMolecule(Set(int1, int3)).get.map(_ ==> List(
              // (int1, Set(int1, int2, int3)),  // int1 AND int3 match
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            ))


            _ <- inputMolecule(Set(int1), Set(int2), Set(int3)).get.map(_ ==> Nil) // int3 match all
            _ <- inputMolecule(Set(int1, int2, int3)).get.map(_ ==> List(
              // (int1, Set(int1, int2, int3)),  // int1 AND int2 AND int3 match
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            ))


            _ <- inputMolecule(Set(int1, int2), Set(int1)).get.map(_ ==> List(
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            ))
            _ <- inputMolecule(Set(int1, int2), Set(int2)).get.map(_ ==> List(
              (int3, Set(int3, int4, int5))
            ))
            _ <- inputMolecule(Set(int1, int2), Set(int3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(int1, int2), Set(int4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(int1, int2), Set(int5)).get.map(_ ==> List(
              (int2, Set(int2, int3, int4))
            ))

            _ <- inputMolecule(Set(int1, int2), Set(int2, int3)).get.map(_ ==> List(
              (int3, Set(int3, int4, int5))
            ))
            _ <- inputMolecule(Set(int1, int2), Set(int4, int5)).get.map(_ ==> List(
              (int2, Set(int2, int3, int4))
            ))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))

            // (int3), (int3, int4), (int4, int5), (int4, int5, int6)
            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List((int2, Set(int3)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))

            _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))

            // (int2, int4), (int3, int4), (int4, int5), (int4, int5, int6)
            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List((int1, Set(int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))

            _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))

            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List((int1, Set(int1))))

            _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5))))

            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List((int1, Set(int1, int2)), (int2, Set(int2))))

            _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.ints(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> Nil)

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(int1))).get.map(_ ==> List(Set(int1, int2)))
            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int1, int2, int3))) // (int1, int2) + (int2, int3)
            _ <- inputMolecule(List(Set(int3))).get.map(_ ==> List(Set(int2, int3, int4))) // (int2, int3) + (int3, int4)

            _ <- inputMolecule(List(Set(int1, int2))).get.map(_ ==> List(Set(int1, int2)))
            _ <- inputMolecule(List(Set(int1, int3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(int2, int3))).get.map(_ ==> List(Set(int2, int3)))
            _ <- inputMolecule(List(Set(int4, int5))).get.map(_ ==> List(Set(int4, int5, int6))) // (int4, int5) + (int4, int5, int6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(int1), Set(int1))).get.map(_ ==> List(Set(int1, int2)))
            _ <- inputMolecule(List(Set(int1), Set(int2))).get.map(_ ==> List(Set(int1, int2, int3))) // (int1, int2) + (int2, int3)
            _ <- inputMolecule(List(Set(int1), Set(int3))).get.map(_ ==> List(Set(int1, int4, int3, int2))) // (int1, int2) + (int2, int3) + (int3, int4)
            _ <- inputMolecule(List(Set(int2), Set(int3))).get.map(_ ==> List(Set(int1, int2, int3, int4))) // (int1, int2) + (int2, int3) + (int3, int4)

            _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get.map(_ ==> List(Set(int1, int2, int3, int4))) // (int1, int2) + (int2, int3) + (int3, int4)
            _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get.map(_ ==> List(Set(int1, int3, int2))) // (int1, int2) + (int2, int3)
            _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get.map(_ ==> List(Set(int1, int2, int3, int4))) // (int1, int2) + (int2, int3) + (int3, int4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.ints.not(?)) // or m(Ns.int.ints.!=(?))
          for {
            _ <- Ns.int.ints insert List(
              (int1, Set(int1, int2, int3)),
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int1, int2, int3, int4, int5)))
            _ <- inputMolecule(Set[Int]()).get.map(_ ==> List(Set(int1, int2, int3, int4, int5)))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(int1).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(int1)).get.map(_ ==> List(Set(int2, int3, int4, int5)))
            // Same as
            _ <- inputMolecule(List(Set(int1))).get.map(_ ==> List(Set(int2, int3, int4, int5)))

            _ <- inputMolecule(Set(int2)).get.map(_ ==> List(Set(int3, int4, int5)))
            _ <- inputMolecule(Set(int3)).get.map(_ ==> Nil) // int3 match all

            _ <- inputMolecule(Set(int1), Set(int2)).get.map(_ ==> List(Set(int3, int4, int5)))
            _ <- inputMolecule(Set(int1), Set(int3)).get.map(_ ==> Nil) // int3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(int1, int2)).get.map(_ ==> List(Set(int2, int3, int4, int5)))
            _ <- inputMolecule(Set(int1, int3)).get.map(_ ==> List(Set(int2, int3, int4, int5)))

            _ <- inputMolecule(Set(int1), Set(int2), Set(int3)).get.map(_ ==> Nil) // int3 match all
            _ <- inputMolecule(Set(int1, int2, int3)).get.map(_ ==> List(Set(int2, int3, int4, int5)))

            _ <- inputMolecule(Set(int1, int2), Set(int1)).get.map(_ ==> List(Set(int2, int3, int4, int5)))
            _ <- inputMolecule(Set(int1, int2), Set(int2)).get.map(_ ==> List(Set(int3, int4, int5)))
            _ <- inputMolecule(Set(int1, int2), Set(int3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(int1, int2), Set(int4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(int1, int2), Set(int5)).get.map(_ ==> List(Set(int2, int3, int4)))

            _ <- inputMolecule(Set(int1, int2), Set(int2, int3)).get.map(_ ==> List(Set(int3, int4, int5)))
            _ <- inputMolecule(Set(int1, int2), Set(int4, int5)).get.map(_ ==> List(Set(int2, int3, int4)))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.ints.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int1, int2, int3, int4, int5, int6)))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int1, int2, int3, int4, int5, int6)))

            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int3, int4, int5, int6)))

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
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int1, int2, int3, int4, int5, int6)))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int1, int2, int3, int4, int5, int6)))

            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(Set(int2, int3, int4, int5, int6)))

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
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int1, int2, int3, int4, int5, int6)))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int1, int2, int3, int4, int5, int6)))

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
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(int1, int2, int3, int4, int5, int6)))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(Set(int1, int2, int3, int4, int5, int6)))

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


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(int6))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(int6))


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(int1))).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(int1, int2))
            _ <- inputMolecule(List(Set(int3))).get.map(_ ==> List(int2, int3))

            _ <- inputMolecule(List(Set(int1, int1))).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(Set(int1, int2))).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(Set(int1, int3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(int2, int3))).get.map(_ ==> List(int2))
            _ <- inputMolecule(List(Set(int4, int5))).get.map(_ ==> List(int4, int5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(int1, int2), Set[Int]())).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(Set(int1), Set(int1))).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(Set(int1), Set(int2))).get.map(_ ==> List(int1, int2))
            _ <- inputMolecule(List(Set(int1), Set(int3))).get.map(_ ==> List(int1, int2, int3))

            _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get.map(_ ==> List(int1, int2, int3))
            _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get.map(_ ==> List(int1, int2))
            _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get.map(_ ==> List(int1, int2, int3))

            _ <- inputMolecule(List(Set(int1, int2), Set(int3, int4))).get.map(_ ==> List(int1, int3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.not(?)) // or m(Ns.int.ints.!=(?))
          for {
            _ <- Ns.int.ints insert List(
              (int1, Set(int1, int2, int3)),
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(int1, int2, int3))
            _ <- inputMolecule(Set[Int]()).get.map(_.sorted ==> List(int1, int2, int3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(int1).get.map(_ ==> ...)
            // inputMolecule(List(int1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(int1)).get.map(_.sorted ==> List(int2, int3))
            // Same as
            _ <- inputMolecule(List(Set(int1))).get.map(_.sorted ==> List(int2, int3))

            _ <- inputMolecule(Set(int2)).get.map(_ ==> List(int3))
            _ <- inputMolecule(Set(int3)).get.map(_ ==> Nil) // int3 match all


            _ <- inputMolecule(Set(int1), Set(int2)).get.map(_ ==> List(int3))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(int1, int2)).get.map(_.sorted ==> List(int2, int3))

            _ <- inputMolecule(Set(int1), Set(int3)).get.map(_ ==> Nil) // int3 match all
            _ <- inputMolecule(Set(int1, int3)).get.map(_.sorted ==> List(int2, int3))

            _ <- inputMolecule(Set(int1), Set(int2), Set(int3)).get.map(_ ==> Nil) // int3 match all
            _ <- inputMolecule(Set(int1, int2, int3)).get.map(_.sorted ==> List(int2, int3))

            _ <- inputMolecule(Set(int1, int2), Set(int1)).get.map(_.sorted ==> List(int2, int3))
            _ <- inputMolecule(Set(int1, int2), Set(int2)).get.map(_ ==> List(int3))
            _ <- inputMolecule(Set(int1, int2), Set(int3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(int1, int2), Set(int4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(int1, int2), Set(int5)).get.map(_ ==> List(int2))

            _ <- inputMolecule(Set(int1, int2), Set(int2, int3)).get.map(_ ==> List(int3))
            _ <- inputMolecule(Set(int1, int2), Set(int4, int5)).get.map(_ ==> List(int2))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3, int4, int5))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(int1, int2, int3, int4, int5))

            // (int3, int4), (int4, int5), (int4, int5, int6)
            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(int2, int3, int4, int5))

            _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3, int4, int5))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(int1, int2, int3, int4, int5))

            // (int2, int4), (int3, int4), (int4, int5), (int4, int5, int6)
            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(int1, int2, int3, int4, int5))

            _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3, int4, int5))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(int1, int2, int3, int4, int5))

            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(int1))

            _ <- inputMolecule(List(Set(int2, int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(int2), Set(int3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(int1, int2, int3, int4, int5))
            _ <- inputMolecule(List(Set[Int]())).get.map(_ ==> List(int1, int2, int3, int4, int5))

            _ <- inputMolecule(List(Set(int2))).get.map(_ ==> List(int1, int2))

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
  }
}