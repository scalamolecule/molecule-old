package moleculeTests.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1BigDecimal extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.bigDec$ insert List(
      (str1, Some(bigDec1)),
      (str2, Some(bigDec2)),
      (str3, Some(bigDec3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.bigDec.bigDecs$ insert List(
      (bigDec1, Some(Set(bigDec1, bigDec2))),
      (bigDec2, Some(Set(bigDec2, bigDec3))),
      (bigDec3, Some(Set(bigDec3, bigDec4))),
      (bigDec4, Some(Set(bigDec4, bigDec5))),
      (bigDec5, Some(Set(bigDec4, bigDec5, bigDec6))),
      (bigDec6, None)
    )
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.apply(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(List(bigDec1)).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(List(bigDec1, bigDec1)).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(List(bigDec1, bigDec2)).get.map(_ ==> List(bigDec1, bigDec2))

            // Varargs
            _ <- inputMolecule(bigDec1).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(bigDec1, bigDec2).get.map(_ ==> List(bigDec1, bigDec2))

            // `or`
            _ <- inputMolecule(bigDec1 or bigDec2).get.map(_ ==> List(bigDec1, bigDec2))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.not(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))

            _ <- inputMolecule(List(bigDec1)).get.map(_ ==> List(bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec1, bigDec1)).get.map(_ ==> List(bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec1, bigDec2)).get.map(_ ==> List(bigDec3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.>(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2)).get.map(_ ==> List(bigDec3))
            _ <- inputMolecule(List(bigDec2, bigDec3)).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.>=(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2)).get.map(_ ==> List(bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2, bigDec3)).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.<(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2)).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(List(bigDec2, bigDec3)).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.<=(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2)).get.map(_ ==> List(bigDec1, bigDec2))
            _ <- inputMolecule(List(bigDec2, bigDec3)).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.bigDec_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str4))
            _ <- inputMolecule(List(bigDec1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(bigDec1, bigDec1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(bigDec1, bigDec2)).get.map(_ ==> List(str1, str2))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.bigDec_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec1)).get.map(_ ==> List(str2, str3))
            _ <- inputMolecule(List(bigDec1, bigDec1)).get.map(_ ==> List(str2, str3))
            _ <- inputMolecule(List(bigDec1, bigDec2)).get.map(_ ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.bigDec_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec2)).get.map(_ ==> List(str3))
            _ <- inputMolecule(List(bigDec2, bigDec3)).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.bigDec_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec2)).get.map(_ ==> List(str2, str3))
            _ <- inputMolecule(List(bigDec2, bigDec3)).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.bigDec_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec2)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(bigDec2, bigDec3)).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.bigDec_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec2)).get.map(_ ==> List(str1, str2))
            _ <- inputMolecule(List(bigDec2, bigDec3)).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }

    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> Nil)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigDec1))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(List(Set(bigDec1, bigDec1))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))

            _ <- inputMolecule(List(Set(bigDec1, bigDec2))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(List(Set(bigDec1, bigDec3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.map(_ ==> List((bigDec2, Set(bigDec3, bigDec2))))
            _ <- inputMolecule(List(Set(bigDec4, bigDec5))).get.map(_ ==> List((bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))

            // 1 arg
            _ <- inputMolecule(Set(bigDec1)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(Set(bigDec1, bigDec1)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(Set(bigDec1, bigDec2)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(Set(bigDec1, bigDec3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigDec2, bigDec3)).get.map(_ ==> List((bigDec2, Set(bigDec3, bigDec2))))
            _ <- inputMolecule(Set(bigDec4, bigDec5)).get.map(_ ==> List((bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set[BigDecimal]())).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec1))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2))))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec3))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2, bigDec3))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2))))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2), Set(bigDec3))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))


            // Multiple varargs
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set[BigDecimal]()).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec1)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec2)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2))))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec3)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec2, bigDec3)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2))))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec2), Set(bigDec3)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))

            // `or`
            _ <- inputMolecule(Set(bigDec1, bigDec2) or Set[BigDecimal]()).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec1)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2))))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec2)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2))))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec3)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(Set(bigDec1, bigDec2) or Set(bigDec3)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec2, bigDec3)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2))))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec2) or Set(bigDec3)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
            _ <- inputMolecule(Set(bigDec1, bigDec2) or Set(bigDec3, bigDec4)).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec3, Set(bigDec4, bigDec3))))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs.not(?))
          val all           = List(
            (bigDec1, Set(bigDec1, bigDec2, bigDec3)),
            (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
            (bigDec3, Set(bigDec3, bigDec4, bigDec5))
          )

          for {
            _ <- Ns.bigDec.bigDecs insert all

            _ <- inputMolecule(Nil).get.map(_ ==> all)
            _ <- inputMolecule(Set[BigDecimal]()).get.map(_ ==> all)

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // _ <- inputMolecule(bigDec1).get.map(_ ==> ...)
            // _ <- inputMolecule(List(bigDec1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(bigDec1)).get.map(_ ==> List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 match
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))
            // Same as
            _ <- inputMolecule(List(Set(bigDec1))).get.map(_ ==> List(
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))

            _ <- inputMolecule(Set(bigDec2)).get.map(_ ==> List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec2 match
              // (bigDec2, Set(bigDec2, bigDec3, bigDec4)),  // bigDec2 match
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))

            _ <- inputMolecule(Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all


            _ <- inputMolecule(Set(bigDec1), Set(bigDec2)).get.map(_ ==> List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 match, bigDec2 match
              // (bigDec2, Set(bigDec2, bigDec3, bigDec4)),  // bigDec2 match
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigDec1, bigDec2)).get.map(_ ==> List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 AND bigDec2 match
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))


            _ <- inputMolecule(Set(bigDec1), Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec3)).get.map(_ ==> List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 AND bigDec3 match
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))


            _ <- inputMolecule(Set(bigDec1), Set(bigDec2), Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec2, bigDec3)).get.map(_ ==> List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 AND bigDec2 AND bigDec3 match
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))


            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec1)).get.map(_ ==> List(
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2)).get.map(_ ==> List(
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec5)).get.map(_ ==> List(
              (bigDec2, Set(bigDec2, bigDec3, bigDec4))
            ))

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get.map(_ ==> List(
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            ))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4, bigDec5)).get.map(_ ==> List(
              (bigDec2, Set(bigDec2, bigDec3, bigDec4))
            ))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))

            // (bigDec3, bigDec4), (bigDec4, bigDec5), (bigDec4, bigDec5, bigDec6)
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List((bigDec2, Set(bigDec3)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))

            // (bigDec2, bigDec4), (bigDec3, bigDec4), (bigDec4, bigDec5), (bigDec4, bigDec5, bigDec6)
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List((bigDec1, Set(bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get
              .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List((bigDec1, Set(bigDec1))))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5))))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec2))))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> Nil)

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigDec1))).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3))) // (bigDec1, bigDec2) + (bigDec2, bigDec3)
            _ <- inputMolecule(List(Set(bigDec3))).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4))) // (bigDec2, bigDec3) + (bigDec3, bigDec4)

            _ <- inputMolecule(List(Set(bigDec1, bigDec2))).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec1, bigDec3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.map(_ ==> List(Set(bigDec2, bigDec3)))
            _ <- inputMolecule(List(Set(bigDec4, bigDec5))).get.map(_ ==> List(Set(bigDec4, bigDec5, bigDec6))) // (bigDec4, bigDec5) + (bigDec4, bigDec5, bigDec6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec1))).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2))).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3))) // (bigDec1, bigDec2) + (bigDec2, bigDec3)
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec3))).get.map(_ ==> List(Set(bigDec1, bigDec4, bigDec3, bigDec2))) // (bigDec1, bigDec2) + (bigDec2, bigDec3) + (bigDec3, bigDec4)
            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4))) // (bigDec1, bigDec2) + (bigDec2, bigDec3) + (bigDec3, bigDec4)

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3))).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4))) // (bigDec1, bigDec2) + (bigDec2, bigDec3) + (bigDec3, bigDec4)
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2, bigDec3))).get.map(_ ==> List(Set(bigDec1, bigDec3, bigDec2))) // (bigDec1, bigDec2) + (bigDec2, bigDec3)
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2), Set(bigDec3))).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4))) // (bigDec1, bigDec2) + (bigDec2, bigDec3) + (bigDec3, bigDec4)
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.not(?))
          for {
            _ <- Ns.bigDec.bigDecs insert List(
              (bigDec1, Set(bigDec1, bigDec2, bigDec3)),
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5)))
            _ <- inputMolecule(Set[BigDecimal]()).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5)))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // _ <- inputMolecule(bigDec1).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(bigDec1)).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4, bigDec5)))
            // Same as
            _ <- inputMolecule(List(Set(bigDec1))).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4, bigDec5)))

            _ <- inputMolecule(Set(bigDec2)).get.map(_ ==> List(Set(bigDec3, bigDec4, bigDec5)))
            _ <- inputMolecule(Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all

            _ <- inputMolecule(Set(bigDec1), Set(bigDec2)).get.map(_ ==> List(Set(bigDec3, bigDec4, bigDec5)))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigDec1, bigDec2)).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4, bigDec5)))
            _ <- inputMolecule(Set(bigDec1, bigDec3)).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4, bigDec5)))

            _ <- inputMolecule(Set(bigDec1), Set(bigDec2), Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec2, bigDec3)).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4, bigDec5)))

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec1)).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4, bigDec5)))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2)).get.map(_ ==> List(Set(bigDec3, bigDec4, bigDec5)))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec5)).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4)))

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get.map(_ ==> List(Set(bigDec3, bigDec4, bigDec5)))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4, bigDec5)).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4)))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(Set(bigDec3, bigDec4, bigDec5, bigDec6)))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(Set(bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(Set(bigDec1)))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6)))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(Set(bigDec1, bigDec2)))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec6))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(bigDec6))


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigDec1))).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec3))).get.map(_ ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(List(Set(bigDec1, bigDec1))).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(List(Set(bigDec1, bigDec2))).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(List(Set(bigDec1, bigDec3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.map(_ ==> List(bigDec2))
            _ <- inputMolecule(List(Set(bigDec4, bigDec5))).get.map(_ ==> List(bigDec4, bigDec5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set[BigDecimal]())).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec1))).get.map(_ ==> List(bigDec1))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2))).get.map(_ ==> List(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec3))).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3))).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2, bigDec3))).get.map(_ ==> List(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2), Set(bigDec3))).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4))).get.map(_ ==> List(bigDec1, bigDec3))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs_.not(?))
          for {
            _ <- Ns.bigDec.bigDecs insert List(
              (bigDec1, Set(bigDec1, bigDec2, bigDec3)),
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(Set[BigDecimal]()).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // _ <- inputMolecule(bigDec1).get.map(_ ==> ...)
            // _ <- inputMolecule(List(bigDec1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(bigDec1)).get.map(_ ==> List(bigDec2, bigDec3))
            // Same as
            _ <- inputMolecule(List(Set(bigDec1))).get.map(_ ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(Set(bigDec2)).get.map(_ ==> List(bigDec3))
            _ <- inputMolecule(Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all


            _ <- inputMolecule(Set(bigDec1), Set(bigDec2)).get.map(_ ==> List(bigDec3))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigDec1, bigDec2)).get.map(_ ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(Set(bigDec1), Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec3)).get.map(_ ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(Set(bigDec1), Set(bigDec2), Set(bigDec3)).get.map(_ ==> Nil) // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec2, bigDec3)).get.map(_ ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec1)).get.map(_ ==> List(bigDec2, bigDec3))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2)).get.map(_ ==> List(bigDec3))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec5)).get.map(_ ==> List(bigDec2))

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get.map(_ ==> List(bigDec3))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4, bigDec5)).get.map(_ ==> List(bigDec2))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            // (bigDec3, bigDec4), (bigDec4, bigDec5), (bigDec4, bigDec5, bigDec6)
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            // (bigDec2, bigDec4), (bigDec3, bigDec4), (bigDec4, bigDec5), (bigDec4, bigDec5, bigDec6)
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(bigDec1))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.a1.bigDecs_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_ ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_ ==> List(bigDec1, bigDec2))

            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }
  }
}