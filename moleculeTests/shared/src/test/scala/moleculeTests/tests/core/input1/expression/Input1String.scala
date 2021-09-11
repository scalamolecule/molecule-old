package moleculeTests.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1String extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.int.str$ insert List(
      (int1, Some(str1)),
      (int2, Some(str2)),
      (int3, Some(str3)),
      (int4, None)
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.strs$ insert List(
      (str1, Some(Set(str1, str2))),
      (str2, Some(Set(str2, str3))),
      (str3, Some(Set(str3, str4))),
      (str4, Some(Set(str4, str5))),
      (str5, Some(Set(str4, str5, str6))),
      (str6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(List(str1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(str1, str1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(str1, str2)).get.map(_.sorted ==> List(str1, str2))

            // Varargs
            _ <- inputMolecule(str1).get.map(_ ==> List(str1))
            _ <- inputMolecule(str1, str2).get.map(_.sorted ==> List(str1, str2))

            // `or`
            _ <- inputMolecule(str1 or str2).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))

            _ <- inputMolecule(List(str1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(str1, str1)).get.map(_.sorted ==> List(str2, str3))

            _ <- inputMolecule(List(str1, str2)).get.map(_.sorted ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(str2)).get.map(_.sorted ==> List(str3))
            _ <- inputMolecule(List(str2, str3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(str2)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(str2, str3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(str2)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(str2, str3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(str2)).get.map(_.sorted ==> List(str1, str2))
            _ <- inputMolecule(List(str2, str3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.str_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(int4))
            _ <- inputMolecule(List(str1)).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(str1, str1)).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(str1, str2)).get.map(_.sorted ==> List(int1, int2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.str_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(int1, int2, int3))
            _ <- inputMolecule(List(str1)).get.map(_.sorted ==> List(int2, int3))
            _ <- inputMolecule(List(str1, str1)).get.map(_.sorted ==> List(int2, int3))
            _ <- inputMolecule(List(str1, str2)).get.map(_.sorted ==> List(int3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.int.str_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(int1, int2, int3))
            _ <- inputMolecule(List(str2)).get.map(_.sorted ==> List(int3))
            _ <- inputMolecule(List(str2, str3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.str_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(int1, int2, int3))
            _ <- inputMolecule(List(str2)).get.map(_.sorted ==> List(int2, int3))
            _ <- inputMolecule(List(str2, str3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.int.str_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(int1, int2, int3))
            _ <- inputMolecule(List(str2)).get.map(_ ==> List(int1))
            _ <- inputMolecule(List(str2, str3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.str_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(int1, int2, int3))
            _ <- inputMolecule(List(str2)).get.map(_.sorted ==> List(int1, int2))
            _ <- inputMolecule(List(str2, str3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> Nil)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(str1))).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(List(Set(str1, str1))).get.map(_ ==> List((str1, Set(str1, str2))))

            _ <- inputMolecule(List(Set(str1, str2))).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(List(Set(str1, str3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(str2, str3))).get.map(_ ==> List((str2, Set(str3, str2))))
            _ <- inputMolecule(List(Set(str4, str5))).get.map(_ ==> List((str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))

            // 1 arg
            _ <- inputMolecule(Set(str1)).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(Set(str1, str1)).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(Set(str1, str2)).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(Set(str1, str3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(str2, str3)).get.map(_ ==> List((str2, Set(str3, str2))))
            _ <- inputMolecule(Set(str4, str5)).get.map(_ ==> List((str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(str1, str2), Set[String]())).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(List(Set(str1), Set(str1))).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(List(Set(str1), Set(str2))).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2))))
            _ <- inputMolecule(List(Set(str1), Set(str3))).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(List(Set(str1, str2), Set(str3))).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(List(Set(str1), Set(str2, str3))).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2))))
            _ <- inputMolecule(List(Set(str1), Set(str2), Set(str3))).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(List(Set(str1, str2), Set(str3, str4))).get.map(_ ==> List((str1, Set(str1, str2)), (str3, Set(str4, str3))))


            // Multiple varargs
            _ <- inputMolecule(Set(str1, str2), Set[String]()).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(Set(str1), Set(str1)).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(Set(str1), Set(str2)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2))))
            _ <- inputMolecule(Set(str1), Set(str3)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(Set(str1, str2), Set(str3)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(Set(str1), Set(str2, str3)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2))))
            _ <- inputMolecule(Set(str1), Set(str2), Set(str3)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(Set(str1, str2), Set(str3, str4)).get.map(_ ==> List((str1, Set(str1, str2)), (str3, Set(str4, str3))))

            // `or`
            _ <- inputMolecule(Set(str1, str2) or Set[String]()).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(Set(str1) or Set(str1)).get.map(_ ==> List((str1, Set(str1, str2))))
            _ <- inputMolecule(Set(str1) or Set(str2)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2))))
            _ <- inputMolecule(Set(str1) or Set(str3)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(Set(str1, str2) or Set(str3)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(Set(str1) or Set(str2, str3)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2))))
            _ <- inputMolecule(Set(str1) or Set(str2) or Set(str3)).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3))))
            _ <- inputMolecule(Set(str1, str2) or Set(str3, str4)).get.map(_ ==> List((str1, Set(str1, str2)), (str3, Set(str4, str3))))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs.not(?)) // or m(Ns.str.strs.!=(?))
          val all           = List(
            (str1, Set(str1, str2, str3)),
            (str2, Set(str2, str3, str4)),
            (str3, Set(str3, str4, str5))
          )
          for {
            _ <- Ns.str.strs insert all

            _ <- inputMolecule(Nil).get.map(_ ==> all)
            _ <- inputMolecule(Set[String]()).get.map(_ ==> all)

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(str1).get.map(_ ==> ...)
            // inputMolecule(List(str1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(str1)).get.map(_ ==> List(
              // (str1, Set(str1, str2, str3)),  // str1 match
              (str2, Set(str2, str3, str4)),
              (str3, Set(str3, str4, str5))
            ))
            // Same as
            _ <- inputMolecule(List(Set(str1))).get.map(_ ==> List(
              (str2, Set(str2, str3, str4)),
              (str3, Set(str3, str4, str5))
            ))

            _ <- inputMolecule(Set(str2)).get.map(_ ==> List(
              // (str1, Set(str1, str2, str3)),  // str2 match
              // (str2, Set(str2, str3, str4)),  // str2 match
              (str3, Set(str3, str4, str5))
            ))

            _ <- inputMolecule(Set(str3)).get.map(_ ==> Nil) // str3 match all


            _ <- inputMolecule(Set(str1), Set(str2)).get.map(_ ==> List(
              // (str1, Set(str1, str2, str3)),  // str1 match, str2 match
              // (str2, Set(str2, str3, str4)),  // str2 match
              (str3, Set(str3, str4, str5))
            ))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(str1, str2)).get.map(_ ==> List(
              // (str1, Set(str1, str2, str3)),  // str1 AND str2 match
              (str2, Set(str2, str3, str4)),
              (str3, Set(str3, str4, str5))
            ))


            _ <- inputMolecule(Set(str1), Set(str3)).get.map(_ ==> Nil) // str3 match all
            _ <- inputMolecule(Set(str1, str3)).get.map(_ ==> List(
              // (str1, Set(str1, str2, str3)),  // str1 AND str3 match
              (str2, Set(str2, str3, str4)),
              (str3, Set(str3, str4, str5))
            ))


            _ <- inputMolecule(Set(str1), Set(str2), Set(str3)).get.map(_ ==> Nil) // str3 match all
            _ <- inputMolecule(Set(str1, str2, str3)).get.map(_ ==> List(
              // (str1, Set(str1, str2, str3)),  // str1 AND str2 AND str3 match
              (str2, Set(str2, str3, str4)),
              (str3, Set(str3, str4, str5))
            ))


            _ <- inputMolecule(Set(str1, str2), Set(str1)).get.map(_ ==> List(
              (str2, Set(str2, str3, str4)),
              (str3, Set(str3, str4, str5))
            ))
            _ <- inputMolecule(Set(str1, str2), Set(str2)).get.map(_ ==> List(
              (str3, Set(str3, str4, str5))
            ))
            _ <- inputMolecule(Set(str1, str2), Set(str3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(str1, str2), Set(str4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(str1, str2), Set(str5)).get.map(_ ==> List(
              (str2, Set(str2, str3, str4))
            ))

            _ <- inputMolecule(Set(str1, str2), Set(str2, str3)).get.map(_ ==> List(
              (str3, Set(str3, str4, str5))
            ))
            _ <- inputMolecule(Set(str1, str2), Set(str4, str5)).get.map(_ ==> List(
              (str2, Set(str2, str3, str4))
            ))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))

            // (str3, str4), (str4, str5), (str4, str5, str6)
            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List((str2, Set(str3)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))

            // (str2, str4), (str3, str4), (str4, str5), (str4, str5, str6)
            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List((str1, Set(str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))

            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List((str1, Set(str1))))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str3, str2)), (str3, Set(str4, str3)), (str4, Set(str4, str5)), (str5, Set(str4, str6, str5))))

            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List((str1, Set(str1, str2)), (str2, Set(str2))))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.strs(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> Nil)

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(str1))).get.map(_ ==> List(Set(str1, str2)))
            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List(Set(str1, str2, str3))) // (str1, str2) + (str2, str3)
            _ <- inputMolecule(List(Set(str3))).get.map(_ ==> List(Set(str2, str3, str4))) // (str2, str3) + (str3, str4)

            _ <- inputMolecule(List(Set(str1, str2))).get.map(_ ==> List(Set(str1, str2)))
            _ <- inputMolecule(List(Set(str1, str3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(str2, str3))).get.map(_ ==> List(Set(str2, str3)))
            _ <- inputMolecule(List(Set(str4, str5))).get.map(_ ==> List(Set(str4, str5, str6))) // (str4, str5) + (str4, str5, str6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(str1), Set(str1))).get.map(_ ==> List(Set(str1, str2)))
            _ <- inputMolecule(List(Set(str1), Set(str2))).get.map(_ ==> List(Set(str1, str2, str3))) // (str1, str2) + (str2, str3)
            _ <- inputMolecule(List(Set(str1), Set(str3))).get.map(_ ==> List(Set(str1, str4, str3, str2))) // (str1, str2) + (str2, str3) + (str3, str4)
            _ <- inputMolecule(List(Set(str2), Set(str3))).get.map(_ ==> List(Set(str1, str2, str3, str4))) // (str1, str2) + (str2, str3) + (str3, str4)

            _ <- inputMolecule(List(Set(str1, str2), Set(str3))).get.map(_ ==> List(Set(str1, str2, str3, str4))) // (str1, str2) + (str2, str3) + (str3, str4)
            _ <- inputMolecule(List(Set(str1), Set(str2, str3))).get.map(_ ==> List(Set(str1, str3, str2))) // (str1, str2) + (str2, str3)
            _ <- inputMolecule(List(Set(str1), Set(str2), Set(str3))).get.map(_ ==> List(Set(str1, str2, str3, str4))) // (str1, str2) + (str2, str3) + (str3, str4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.strs.not(?)) // or m(Ns.str.strs.!=(?))
          for {
            _ <- Ns.str.strs insert List(
              (str1, Set(str1, str2, str3)),
              (str2, Set(str2, str3, str4)),
              (str3, Set(str3, str4, str5))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(str1, str2, str3, str4, str5)))
            _ <- inputMolecule(Set[String]()).get.map(_ ==> List(Set(str1, str2, str3, str4, str5)))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(str1).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(str1)).get.map(_ ==> List(Set(str2, str3, str4, str5)))
            // Same as
            _ <- inputMolecule(List(Set(str1))).get.map(_ ==> List(Set(str2, str3, str4, str5)))

            _ <- inputMolecule(Set(str2)).get.map(_ ==> List(Set(str3, str4, str5)))
            _ <- inputMolecule(Set(str3)).get.map(_ ==> Nil) // str3 match all

            _ <- inputMolecule(Set(str1), Set(str2)).get.map(_ ==> List(Set(str3, str4, str5)))
            _ <- inputMolecule(Set(str1), Set(str3)).get.map(_ ==> Nil) // str3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(str1, str2)).get.map(_ ==> List(Set(str2, str3, str4, str5)))
            _ <- inputMolecule(Set(str1, str3)).get.map(_ ==> List(Set(str2, str3, str4, str5)))

            _ <- inputMolecule(Set(str1), Set(str2), Set(str3)).get.map(_ ==> Nil) // str3 match all
            _ <- inputMolecule(Set(str1, str2, str3)).get.map(_ ==> List(Set(str2, str3, str4, str5)))

            _ <- inputMolecule(Set(str1, str2), Set(str1)).get.map(_ ==> List(Set(str2, str3, str4, str5)))
            _ <- inputMolecule(Set(str1, str2), Set(str2)).get.map(_ ==> List(Set(str3, str4, str5)))
            _ <- inputMolecule(Set(str1, str2), Set(str3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(str1, str2), Set(str4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(str1, str2), Set(str5)).get.map(_ ==> List(Set(str2, str3, str4)))

            _ <- inputMolecule(Set(str1, str2), Set(str2, str3)).get.map(_ ==> List(Set(str3, str4, str5)))
            _ <- inputMolecule(Set(str1, str2), Set(str4, str5)).get.map(_ ==> List(Set(str2, str3, str4)))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.strs.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(str1, str2, str3, str4, str5, str6)))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(str1, str2, str3, str4, str5, str6)))

            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List(Set(str3, str4, str5, str6)))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.strs.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(str1, str2, str3, str4, str5, str6)))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(str1, str2, str3, str4, str5, str6)))

            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List(Set(str2, str3, str4, str5, str6)))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.strs.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(str1, str2, str3, str4, str5, str6)))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(str1, str2, str3, str4, str5, str6)))

            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List(Set(str1)))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.strs.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(str1, str2, str3, str4, str5, str6)))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(Set(str1, str2, str3, str4, str5, str6)))

            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List(Set(str1, str2)))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str6))
            _ <- inputMolecule(List(Set[String]())).get.map(_ ==> List(str6))


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(str1))).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(Set(str2))).get.map(_.sorted ==> List(str1, str2))
            _ <- inputMolecule(List(Set(str3))).get.map(_.sorted ==> List(str2, str3))

            _ <- inputMolecule(List(Set(str1, str1))).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(Set(str1, str2))).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(Set(str1, str3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(str2, str3))).get.map(_ ==> List(str2))
            _ <- inputMolecule(List(Set(str4, str5))).get.map(_.sorted ==> List(str4, str5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(str1, str2), Set[String]())).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(Set(str1), Set(str1))).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(Set(str1), Set(str2))).get.map(_.sorted ==> List(str1, str2))
            _ <- inputMolecule(List(Set(str1), Set(str3))).get.map(_.sorted ==> List(str1, str2, str3))

            _ <- inputMolecule(List(Set(str1, str2), Set(str3))).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(Set(str1), Set(str2, str3))).get.map(_.sorted ==> List(str1, str2))
            _ <- inputMolecule(List(Set(str1), Set(str2), Set(str3))).get.map(_.sorted ==> List(str1, str2, str3))

            _ <- inputMolecule(List(Set(str1, str2), Set(str3, str4))).get.map(_.sorted ==> List(str1, str3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs_.not(?)) // or m(Ns.str.strs.!=(?))
          for {
            _ <- Ns.str.strs insert List(
              (str1, Set(str1, str2, str3)),
              (str2, Set(str2, str3, str4)),
              (str3, Set(str3, str4, str5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(Set[String]()).get.map(_.sorted ==> List(str1, str2, str3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(str1).get.map(_ ==> ...)
            // inputMolecule(List(str1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(str1)).get.map(_.sorted ==> List(str2, str3))
            // Same as
            _ <- inputMolecule(List(Set(str1))).get.map(_.sorted ==> List(str2, str3))

            _ <- inputMolecule(Set(str2)).get.map(_ ==> List(str3))
            _ <- inputMolecule(Set(str3)).get.map(_ ==> Nil) // str3 match all


            _ <- inputMolecule(Set(str1), Set(str2)).get.map(_ ==> List(str3))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(str1, str2)).get.map(_.sorted ==> List(str2, str3))

            _ <- inputMolecule(Set(str1), Set(str3)).get.map(_ ==> Nil) // str3 match all
            _ <- inputMolecule(Set(str1, str3)).get.map(_.sorted ==> List(str2, str3))

            _ <- inputMolecule(Set(str1), Set(str2), Set(str3)).get.map(_ ==> Nil) // str3 match all
            _ <- inputMolecule(Set(str1, str2, str3)).get.map(_.sorted ==> List(str2, str3))

            _ <- inputMolecule(Set(str1, str2), Set(str1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(Set(str1, str2), Set(str2)).get.map(_ ==> List(str3))
            _ <- inputMolecule(Set(str1, str2), Set(str3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(str1, str2), Set(str4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(str1, str2), Set(str5)).get.map(_ ==> List(str2))

            _ <- inputMolecule(Set(str1, str2), Set(str2, str3)).get.map(_ ==> List(str3))
            _ <- inputMolecule(Set(str1, str2), Set(str4, str5)).get.map(_ ==> List(str2))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))
            _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))

            // (str3, str4), (str4, str5), (str4, str5, str6)
            _ <- inputMolecule(List(Set(str2))).get.map(_.sorted ==> List(str2, str3, str4, str5))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))
            _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))

            // (str2, str4), (str3, str4), (str4, str5), (str4, str5, str6)
            _ <- inputMolecule(List(Set(str2))).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))
            _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))

            _ <- inputMolecule(List(Set(str2))).get.map(_ ==> List(str1))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.strs_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))
            _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(str1, str2, str3, str4, str5))

            _ <- inputMolecule(List(Set(str2))).get.map(_.sorted ==> List(str1, str2))

            _ <- inputMolecule(List(Set(str2, str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(str2), Set(str3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }
  }
}