package moleculeTests.tests.core.input1.expression

import java.util.UUID
import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1UUID extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.uuid$ insert List(
      (str1, Some(uuid1)),
      (str2, Some(uuid2)),
      (str3, Some(uuid3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.uuid.uuids$ insert List(
      (uuid1, Some(Set(uuid1, uuid2))),
      (uuid2, Some(Set(uuid2, uuid3))),
      (uuid3, Some(Set(uuid3, uuid4))),
      (uuid4, Some(Set(uuid4, uuid5))),
      (uuid5, Some(Set(uuid4, uuid5, uuid6))),
      (uuid6, None)
    )
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(List(uuid1)).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(List(uuid1, uuid1)).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(List(uuid1, uuid2)).get.map(_ ==> List(uuid1, uuid2))

            // Varargs
            _ <- inputMolecule(uuid1).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(uuid1, uuid2).get.map(_ ==> List(uuid1, uuid2))

            // `or`
            _ <- inputMolecule(uuid1 or uuid2).get.map(_ ==> List(uuid1, uuid2))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.not(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3))

            _ <- inputMolecule(List(uuid1)).get.map(_ ==> List(uuid2, uuid3))
            _ <- inputMolecule(List(uuid1, uuid1)).get.map(_ ==> List(uuid2, uuid3))
            _ <- inputMolecule(List(uuid1, uuid2)).get.map(_ ==> List(uuid3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.>(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(uuid2)).get.map(_ ==> List(uuid3))
            _ <- inputMolecule(List(uuid2, uuid3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.>=(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(uuid2)).get.map(_ ==> List(uuid2, uuid3))
            _ <- inputMolecule(List(uuid2, uuid3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.<(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(uuid2)).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(List(uuid2, uuid3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.<=(?).a1)
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(uuid2)).get.map(_ ==> List(uuid1, uuid2))
            _ <- inputMolecule(List(uuid2, uuid3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.uuid_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str4))
            _ <- inputMolecule(List(uuid1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(uuid1, uuid1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(uuid1, uuid2)).get.map(_ ==> List(str1, str2))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.uuid_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid1)).get.map(_ ==> List(str2, str3))
            _ <- inputMolecule(List(uuid1, uuid1)).get.map(_ ==> List(str2, str3))
            _ <- inputMolecule(List(uuid1, uuid2)).get.map(_ ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.uuid_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid2)).get.map(_ ==> List(str3))
            _ <- inputMolecule(List(uuid2, uuid3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.uuid_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid2)).get.map(_ ==> List(str2, str3))
            _ <- inputMolecule(List(uuid2, uuid3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.uuid_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid2)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(uuid2, uuid3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.a1.uuid_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid2)).get.map(_ ==> List(str1, str2))
            _ <- inputMolecule(List(uuid2, uuid3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> Nil)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uuid1))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(List(Set(uuid1, uuid1))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))

            _ <- inputMolecule(List(Set(uuid1, uuid2))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(List(Set(uuid1, uuid3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.map(_ ==> List((uuid2, Set(uuid3, uuid2))))
            _ <- inputMolecule(List(Set(uuid4, uuid5))).get.map(_ ==> List((uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            // 1 arg
            _ <- inputMolecule(Set(uuid1)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(Set(uuid1, uuid1)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(Set(uuid1, uuid2)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(Set(uuid1, uuid3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uuid2, uuid3)).get.map(_ ==> List((uuid2, Set(uuid3, uuid2))))
            _ <- inputMolecule(Set(uuid4, uuid5)).get.map(_ ==> List((uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set[UUID]())).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid1))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2))))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid3))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2, uuid3))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2))))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2), Set(uuid3))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3, uuid4))).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid3, Set(uuid4, uuid3))))


            // Multiple varargs
            _ <- inputMolecule(Set(uuid1, uuid2), Set[UUID]()).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(Set(uuid1), Set(uuid1)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(Set(uuid1), Set(uuid2)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2))))
            _ <- inputMolecule(Set(uuid1), Set(uuid3)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(Set(uuid1), Set(uuid2, uuid3)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2))))
            _ <- inputMolecule(Set(uuid1), Set(uuid2), Set(uuid3)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3, uuid4)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid3, Set(uuid4, uuid3))))

            // `or`
            _ <- inputMolecule(Set(uuid1, uuid2) or Set[UUID]()).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(Set(uuid1) or Set(uuid1)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2))))
            _ <- inputMolecule(Set(uuid1) or Set(uuid2)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2))))
            _ <- inputMolecule(Set(uuid1) or Set(uuid3)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(Set(uuid1, uuid2) or Set(uuid3)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(Set(uuid1) or Set(uuid2, uuid3)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2))))
            _ <- inputMolecule(Set(uuid1) or Set(uuid2) or Set(uuid3)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3))))
            _ <- inputMolecule(Set(uuid1, uuid2) or Set(uuid3, uuid4)).get.map(_ ==> List((uuid1, Set(uuid1, uuid2)), (uuid3, Set(uuid4, uuid3))))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids.not(?))
          val all           = List(
            (uuid1, Set(uuid1, uuid2, uuid3)),
            (uuid2, Set(uuid2, uuid3, uuid4)),
            (uuid3, Set(uuid3, uuid4, uuid5))
          )
          for {
            _ <- Ns.uuid.uuids insert all

            _ <- inputMolecule(Nil).get.map(_ ==> all)
            _ <- inputMolecule(Set[UUID]()).get.map(_ ==> all)

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uuid1).get.map(_ ==> ...)
            // inputMolecule(List(uuid1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(uuid1)).get.map(_ ==> List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 match
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))
            // Same as
            _ <- inputMolecule(List(Set(uuid1))).get.map(_ ==> List(
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))

            _ <- inputMolecule(Set(uuid2)).get.map(_ ==> List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid2 match
              // (uuid2, Set(uuid2, uuid3, uuid4)),  // uuid2 match
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))

            _ <- inputMolecule(Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all


            _ <- inputMolecule(Set(uuid1), Set(uuid2)).get.map(_ ==> List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 match, uuid2 match
              // (uuid2, Set(uuid2, uuid3, uuid4)),  // uuid2 match
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uuid1, uuid2)).get.map(_ ==> List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 AND uuid2 match
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))


            _ <- inputMolecule(Set(uuid1), Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid3)).get.map(_ ==> List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 AND uuid3 match
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))


            _ <- inputMolecule(Set(uuid1), Set(uuid2), Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid2, uuid3)).get.map(_ ==> List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 AND uuid2 AND uuid3 match
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))


            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid1)).get.map(_ ==> List(
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2)).get.map(_ ==> List(
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid5)).get.map(_ ==> List(
              (uuid2, Set(uuid2, uuid3, uuid4))
            ))

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2, uuid3)).get.map(_ ==> List(
              (uuid3, Set(uuid3, uuid4, uuid5))
            ))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4, uuid5)).get.map(_ ==> List(
              (uuid2, Set(uuid2, uuid3, uuid4))
            ))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            // (uuid3, uuid4), (uuid4, uuid5), (uuid4, uuid5, uuid6)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sortBy(_._1.toString) ==> List((uuid2, Set(uuid3)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            // (uuid2, uuid4), (uuid3, uuid4), (uuid4, uuid5), (uuid4, uuid5, uuid6)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List((uuid1, Set(uuid1))))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid2))))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> Nil)

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uuid1))).get.map(_ ==> List(Set(uuid1, uuid2)))
            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(Set(uuid1, uuid2, uuid3))) // (uuid1, uuid2) + (uuid2, uuid3)
            _ <- inputMolecule(List(Set(uuid3))).get.map(_ ==> List(Set(uuid2, uuid3, uuid4))) // (uuid2, uuid3) + (uuid3, uuid4)

            _ <- inputMolecule(List(Set(uuid1, uuid2))).get.map(_ ==> List(Set(uuid1, uuid2)))
            _ <- inputMolecule(List(Set(uuid1, uuid3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.map(_ ==> List(Set(uuid2, uuid3)))
            _ <- inputMolecule(List(Set(uuid4, uuid5))).get.map(_ ==> List(Set(uuid4, uuid5, uuid6))) // (uuid4, uuid5) + (uuid4, uuid5, uuid6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uuid1), Set(uuid1))).get.map(_ ==> List(Set(uuid1, uuid2)))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2))).get.map(_ ==> List(Set(uuid1, uuid2, uuid3))) // (uuid1, uuid2) + (uuid2, uuid3)
            _ <- inputMolecule(List(Set(uuid1), Set(uuid3))).get.map(_ ==> List(Set(uuid1, uuid4, uuid3, uuid2))) // (uuid1, uuid2) + (uuid2, uuid3) + (uuid3, uuid4)
            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4))) // (uuid1, uuid2) + (uuid2, uuid3) + (uuid3, uuid4)

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3))).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4))) // (uuid1, uuid2) + (uuid2, uuid3) + (uuid3, uuid4)
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2, uuid3))).get.map(_ ==> List(Set(uuid1, uuid3, uuid2))) // (uuid1, uuid2) + (uuid2, uuid3)
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2), Set(uuid3))).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4))) // (uuid1, uuid2) + (uuid2, uuid3) + (uuid3, uuid4)
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.not(?))
          val all           = List(
            (uuid1, Set(uuid1, uuid2, uuid3)),
            (uuid2, Set(uuid2, uuid3, uuid4)),
            (uuid3, Set(uuid3, uuid4, uuid5))
          )
          for {
            _ <- Ns.uuid.uuids insert all

            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5)))
            _ <- inputMolecule(Set[UUID]()).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5)))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uuid1).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(uuid1)).get.map(_ ==> List(Set(uuid2, uuid3, uuid4, uuid5)))
            // Same as
            _ <- inputMolecule(List(Set(uuid1))).get.map(_ ==> List(Set(uuid2, uuid3, uuid4, uuid5)))

            _ <- inputMolecule(Set(uuid2)).get.map(_ ==> List(Set(uuid3, uuid4, uuid5)))
            _ <- inputMolecule(Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all

            _ <- inputMolecule(Set(uuid1), Set(uuid2)).get.map(_ ==> List(Set(uuid3, uuid4, uuid5)))
            _ <- inputMolecule(Set(uuid1), Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uuid1, uuid2)).get.map(_ ==> List(Set(uuid2, uuid3, uuid4, uuid5)))
            _ <- inputMolecule(Set(uuid1, uuid3)).get.map(_ ==> List(Set(uuid2, uuid3, uuid4, uuid5)))

            _ <- inputMolecule(Set(uuid1), Set(uuid2), Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid2, uuid3)).get.map(_ ==> List(Set(uuid2, uuid3, uuid4, uuid5)))

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid1)).get.map(_ ==> List(Set(uuid2, uuid3, uuid4, uuid5)))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2)).get.map(_ ==> List(Set(uuid3, uuid4, uuid5)))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid5)).get.map(_ ==> List(Set(uuid2, uuid3, uuid4)))

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2, uuid3)).get.map(_ ==> List(Set(uuid3, uuid4, uuid5)))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4, uuid5)).get.map(_ ==> List(Set(uuid2, uuid3, uuid4)))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6)))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6)))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(Set(uuid3, uuid4, uuid5, uuid6)))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6)))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6)))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(Set(uuid2, uuid3, uuid4, uuid5, uuid6)))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6)))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6)))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(Set(uuid1)))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6)))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6)))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(Set(uuid1, uuid2)))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid6))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(uuid6))


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uuid1))).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid3))).get.map(_ ==> List(uuid2, uuid3))

            _ <- inputMolecule(List(Set(uuid1, uuid1))).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(List(Set(uuid1, uuid2))).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(List(Set(uuid1, uuid3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.map(_ ==> List(uuid2))
            _ <- inputMolecule(List(Set(uuid4, uuid5))).get.map(_ ==> List(uuid4, uuid5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set[UUID]())).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid1))).get.map(_ ==> List(uuid1))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2))).get.map(_ ==> List(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid3))).get.map(_ ==> List(uuid1, uuid2, uuid3))

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3))).get.map(_ ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2, uuid3))).get.map(_ ==> List(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2), Set(uuid3))).get.map(_ ==> List(uuid1, uuid2, uuid3))

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3, uuid4))).get.map(_ ==> List(uuid1, uuid3))
          } yield ()
        }

        "not" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids_.not(?))
          for {
            _ <- Ns.uuid.uuids insert List(
              (uuid1, Set(uuid1, uuid2, uuid3)),
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(Set[UUID]()).get.map(_ ==> List(uuid1, uuid2, uuid3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uuid1).get.map(_ ==> ...)
            // inputMolecule(List(uuid1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(uuid1)).get.map(_ ==> List(uuid2, uuid3))
            // Same as
            _ <- inputMolecule(List(Set(uuid1))).get.map(_ ==> List(uuid2, uuid3))

            _ <- inputMolecule(Set(uuid2)).get.map(_ ==> List(uuid3))
            _ <- inputMolecule(Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all


            _ <- inputMolecule(Set(uuid1), Set(uuid2)).get.map(_ ==> List(uuid3))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uuid1, uuid2)).get.map(_ ==> List(uuid2, uuid3))

            _ <- inputMolecule(Set(uuid1), Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid3)).get.map(_ ==> List(uuid2, uuid3))

            _ <- inputMolecule(Set(uuid1), Set(uuid2), Set(uuid3)).get.map(_ ==> Nil) // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid2, uuid3)).get.map(_ ==> List(uuid2, uuid3))

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid1)).get.map(_ ==> List(uuid2, uuid3))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2)).get.map(_ ==> List(uuid3))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid5)).get.map(_ ==> List(uuid2))

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2, uuid3)).get.map(_ ==> List(uuid3))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4, uuid5)).get.map(_ ==> List(uuid2))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            // (uuid3, uuid4), (uuid4, uuid5), (uuid4, uuid5, uuid6)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            // (uuid2, uuid4), (uuid3, uuid4), (uuid4, uuid5), (uuid4, uuid5, uuid6)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(uuid1))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.a1.uuids_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_ ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_ ==> List(uuid1, uuid2))

            _ <- inputMolecule(List(Set(uuid2, uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }
  }
}