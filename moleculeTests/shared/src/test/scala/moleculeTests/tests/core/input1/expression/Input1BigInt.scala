package moleculeTests.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1BigInt extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.bigInt$ insert List(
      (str1, Some(bigInt1)),
      (str2, Some(bigInt2)),
      (str3, Some(bigInt3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.bigInt.bigInts$ insert List(
      (bigInt1, Some(Set(bigInt1, bigInt2))),
      (bigInt2, Some(Set(bigInt2, bigInt3))),
      (bigInt3, Some(Set(bigInt3, bigInt4))),
      (bigInt4, Some(Set(bigInt4, bigInt5))),
      (bigInt5, Some(Set(bigInt4, bigInt5, bigInt6))),
      (bigInt6, None)
    )
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(List(bigInt1)).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(List(bigInt1, bigInt1)).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(List(bigInt1, bigInt2)).get.map(_.sorted ==> List(bigInt1, bigInt2))

            // Varargs
            _ <- inputMolecule(bigInt1).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(bigInt1, bigInt2).get.map(_.sorted ==> List(bigInt1, bigInt2))

            // `or`
            _ <- inputMolecule(bigInt1 or bigInt2).get.map(_.sorted ==> List(bigInt1, bigInt2))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))

            _ <- inputMolecule(List(bigInt1)).get.map(_.sorted ==> List(bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt1, bigInt1)).get.map(_.sorted ==> List(bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt1, bigInt2)).get.map(_.sorted ==> List(bigInt3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(bigInt3))
            _ <- inputMolecule(List(bigInt2, bigInt3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt2, bigInt3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt2)).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(List(bigInt2, bigInt3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(bigInt1, bigInt2))
            _ <- inputMolecule(List(bigInt2, bigInt3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str4))
            _ <- inputMolecule(List(bigInt1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(bigInt1, bigInt1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(bigInt1, bigInt2)).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigInt1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(bigInt1, bigInt1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(bigInt1, bigInt2)).get.map(_.sorted ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(str3))
            _ <- inputMolecule(List(bigInt2, bigInt3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(bigInt2, bigInt3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigInt2)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(bigInt2, bigInt3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(str1, str2))
            _ <- inputMolecule(List(bigInt2, bigInt3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }

    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> Nil)

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigInt1))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(List(Set(bigInt1, bigInt1))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))

            _ <- inputMolecule(List(Set(bigInt1, bigInt2))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(List(Set(bigInt1, bigInt3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.map(_ ==> List((bigInt2, Set(bigInt3, bigInt2))))
            _ <- inputMolecule(List(Set(bigInt4, bigInt5))).get.map(_ ==> List((bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))

            // 1 arg
            _ <- inputMolecule(Set(bigInt1)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(Set(bigInt1, bigInt1)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(Set(bigInt1, bigInt2)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(Set(bigInt1, bigInt3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigInt2, bigInt3)).get.map(_ ==> List((bigInt2, Set(bigInt3, bigInt2))))
            _ <- inputMolecule(Set(bigInt4, bigInt5)).get.map(_ ==> List((bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set[BigInt]())).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt1))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2))))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt3))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2, bigInt3))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2))))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2), Set(bigInt3))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))


            // Multiple varargs
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set[BigInt]()).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt1)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt2)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2))))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt3)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt2, bigInt3)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2))))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt2), Set(bigInt3)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))

            // `or`
            _ <- inputMolecule(Set(bigInt1, bigInt2) or Set[BigInt]()).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt1)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2))))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt2)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2))))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt3)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(Set(bigInt1, bigInt2) or Set(bigInt3)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt2, bigInt3)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2))))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt2) or Set(bigInt3)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
            _ <- inputMolecule(Set(bigInt1, bigInt2) or Set(bigInt3, bigInt4)).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt3, Set(bigInt4, bigInt3))))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.not(?))
          val all           = List(
            (bigInt1, Set(bigInt1, bigInt2, bigInt3)),
            (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
            (bigInt3, Set(bigInt3, bigInt4, bigInt5))
          )
          for {
            _ <- Ns.bigInt.bigInts insert all

            _ <- inputMolecule(Nil).get.map(_ ==> all)
            _ <- inputMolecule(Set[BigInt]()).get.map(_ ==> all)

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(bigInt1).get.map(_ ==> ...)
            // inputMolecule(List(bigInt1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(bigInt1)).get.map(_ ==> List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 match
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))
            // Same as
            _ <- inputMolecule(List(Set(bigInt1))).get.map(_ ==> List(
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))

            _ <- inputMolecule(Set(bigInt2)).get.map(_ ==> List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt2 match
              // (bigInt2, Set(bigInt2, bigInt3, bigInt4)),  // bigInt2 match
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))

            _ <- inputMolecule(Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all


            _ <- inputMolecule(Set(bigInt1), Set(bigInt2)).get.map(_ ==> List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 match, bigInt2 match
              // (bigInt2, Set(bigInt2, bigInt3, bigInt4)),  // bigInt2 match
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigInt1, bigInt2)).get.map(_ ==> List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 AND bigInt2 match
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))


            _ <- inputMolecule(Set(bigInt1), Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt3)).get.map(_ ==> List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 AND bigInt3 match
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))


            _ <- inputMolecule(Set(bigInt1), Set(bigInt2), Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt2, bigInt3)).get.map(_ ==> List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 AND bigInt2 AND bigInt3 match
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))


            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt1)).get.map(_ ==> List(
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2)).get.map(_ ==> List(
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt5)).get.map(_ ==> List(
              (bigInt2, Set(bigInt2, bigInt3, bigInt4))
            ))

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get.map(_ ==> List(
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            ))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4, bigInt5)).get.map(_ ==> List(
              (bigInt2, Set(bigInt2, bigInt3, bigInt4))
            ))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))

            // (bigInt3, bigInt4), (bigInt4, bigInt5), (bigInt4, bigInt5, bigInt6)
            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List((bigInt2, Set(bigInt3)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))

            // (bigInt2, bigInt4), (bigInt3, bigInt4), (bigInt4, bigInt5), (bigInt4, bigInt5, bigInt6)
            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List((bigInt1, Set(bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List((bigInt1, Set(bigInt1))))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5))))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt2))))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> Nil)

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigInt1))).get.map(_ ==> List(Set(bigInt1, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3))) // (bigInt1, bigInt2) + (bigInt2, bigInt3)
            _ <- inputMolecule(List(Set(bigInt3))).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4))) // (bigInt2, bigInt3) + (bigInt3, bigInt4)

            _ <- inputMolecule(List(Set(bigInt1, bigInt2))).get.map(_ ==> List(Set(bigInt1, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt1, bigInt3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.map(_ ==> List(Set(bigInt2, bigInt3)))
            _ <- inputMolecule(List(Set(bigInt4, bigInt5))).get.map(_ ==> List(Set(bigInt4, bigInt5, bigInt6))) // (bigInt4, bigInt5) + (bigInt4, bigInt5, bigInt6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt1))).get.map(_ ==> List(Set(bigInt1, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2))).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3))) // (bigInt1, bigInt2) + (bigInt2, bigInt3)
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt3))).get.map(_ ==> List(Set(bigInt1, bigInt4, bigInt3, bigInt2))) // (bigInt1, bigInt2) + (bigInt2, bigInt3) + (bigInt3, bigInt4)
            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4))) // (bigInt1, bigInt2) + (bigInt2, bigInt3) + (bigInt3, bigInt4)

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3))).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4))) // (bigInt1, bigInt2) + (bigInt2, bigInt3) + (bigInt3, bigInt4)
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2, bigInt3))).get.map(_ ==> List(Set(bigInt1, bigInt3, bigInt2))) // (bigInt1, bigInt2) + (bigInt2, bigInt3)
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2), Set(bigInt3))).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4))) // (bigInt1, bigInt2) + (bigInt2, bigInt3) + (bigInt3, bigInt4)
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.not(?))
          for {
            _ <- Ns.bigInt.bigInts insert List(
              (bigInt1, Set(bigInt1, bigInt2, bigInt3)),
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5)))
            _ <- inputMolecule(Set[BigInt]()).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5)))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(bigInt1).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(bigInt1)).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4, bigInt5)))
            // Same as
            _ <- inputMolecule(List(Set(bigInt1))).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4, bigInt5)))

            _ <- inputMolecule(Set(bigInt2)).get.map(_ ==> List(Set(bigInt3, bigInt4, bigInt5)))
            _ <- inputMolecule(Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all

            _ <- inputMolecule(Set(bigInt1), Set(bigInt2)).get.map(_ ==> List(Set(bigInt3, bigInt4, bigInt5)))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigInt1, bigInt2)).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4, bigInt5)))
            _ <- inputMolecule(Set(bigInt1, bigInt3)).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4, bigInt5)))

            _ <- inputMolecule(Set(bigInt1), Set(bigInt2), Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt2, bigInt3)).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4, bigInt5)))

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt1)).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4, bigInt5)))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2)).get.map(_ ==> List(Set(bigInt3, bigInt4, bigInt5)))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt5)).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4)))

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get.map(_ ==> List(Set(bigInt3, bigInt4, bigInt5)))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4, bigInt5)).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4)))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List(Set(bigInt3, bigInt4, bigInt5, bigInt6)))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List(Set(bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List(Set(bigInt1)))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6)))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List(Set(bigInt1, bigInt2)))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigInt6))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_ ==> List(bigInt6))


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigInt1))).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(List(Set(bigInt2))).get.map(_.sorted ==> List(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt3))).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(List(Set(bigInt1, bigInt1))).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(List(Set(bigInt1, bigInt2))).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(List(Set(bigInt1, bigInt3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.map(_ ==> List(bigInt2))
            _ <- inputMolecule(List(Set(bigInt4, bigInt5))).get.map(_.sorted ==> List(bigInt4, bigInt5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set[BigInt]())).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt1))).get.map(_ ==> List(bigInt1))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2))).get.map(_.sorted ==> List(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt3))).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3))).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2, bigInt3))).get.map(_.sorted ==> List(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2), Set(bigInt3))).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4))).get.map(_.sorted ==> List(bigInt1, bigInt3))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_.not(?))
          for {
            _ <- Ns.bigInt.bigInts insert List(
              (bigInt1, Set(bigInt1, bigInt2, bigInt3)),
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(Set[BigInt]()).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(bigInt1).get.map(_ ==> ...)
            // inputMolecule(List(bigInt1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(bigInt1)).get.map(_.sorted ==> List(bigInt2, bigInt3))
            // Same as
            _ <- inputMolecule(List(Set(bigInt1))).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(Set(bigInt2)).get.map(_ ==> List(bigInt3))
            _ <- inputMolecule(Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all


            _ <- inputMolecule(Set(bigInt1), Set(bigInt2)).get.map(_ ==> List(bigInt3))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigInt1, bigInt2)).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(Set(bigInt1), Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt3)).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(Set(bigInt1), Set(bigInt2), Set(bigInt3)).get.map(_ ==> Nil) // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt2, bigInt3)).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt1)).get.map(_.sorted ==> List(bigInt2, bigInt3))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2)).get.map(_ ==> List(bigInt3))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt5)).get.map(_ ==> List(bigInt2))

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get.map(_ ==> List(bigInt3))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4, bigInt5)).get.map(_ ==> List(bigInt2))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

            // (bigInt3, bigInt4), (bigInt4, bigInt5), (bigInt4, bigInt5, bigInt6)
            _ <- inputMolecule(List(Set(bigInt2))).get.map(_.sorted ==> List(bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

            // (bigInt2, bigInt4), (bigInt3, bigInt4), (bigInt4, bigInt5), (bigInt4, bigInt5, bigInt6)
            _ <- inputMolecule(List(Set(bigInt2))).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_ ==> List(bigInt1))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_.sorted ==> List(bigInt1, bigInt2))

            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }
  }
}