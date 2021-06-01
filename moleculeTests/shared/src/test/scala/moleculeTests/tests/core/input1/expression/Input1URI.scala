package moleculeTests.tests.core.input1.expression

import java.net.URI
import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1URI extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.uri$ insert List(
      (str1, Some(uri1)),
      (str2, Some(uri2)),
      (str3, Some(uri3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.uri.uris$ insert List(
      (uri1, Some(Set(uri1, uri2))),
      (uri2, Some(Set(uri2, uri3))),
      (uri3, Some(Set(uri3, uri4))),
      (uri4, Some(Set(uri4, uri5))),
      (uri5, Some(Set(uri4, uri5, uri6))),
      (uri6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uri(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(List(uri1)).get.map(_ ==> List(uri1))
            _ <- inputMolecule(List(uri1, uri1)).get.map(_ ==> List(uri1))
            _ <- inputMolecule(List(uri1, uri2)).get.map(_.sorted ==> List(uri1, uri2))

            // Varargs
            _ <- inputMolecule(uri1).get.map(_ ==> List(uri1))
            _ <- inputMolecule(uri1, uri2).get.map(_.sorted ==> List(uri1, uri2))

            // `or`
            _ <- inputMolecule(uri1 or uri2).get.map(_.sorted ==> List(uri1, uri2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3))
            _ <- inputMolecule(List(uri1)).get.map(_.sorted ==> List(uri2, uri3))
            _ <- inputMolecule(List(uri1, uri1)).get.map(_.sorted ==> List(uri2, uri3))
            _ <- inputMolecule(List(uri1, uri2)).get.map(_.sorted ==> List(uri3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3))
            _ <- inputMolecule(List(uri2)).get.map(_.sorted ==> List(uri3))
            _ <- inputMolecule(List(uri2, uri3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3))
            _ <- inputMolecule(List(uri2)).get.map(_.sorted ==> List(uri2, uri3))
            _ <- inputMolecule(List(uri2, uri3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3))
            _ <- inputMolecule(List(uri2)).get.map(_ ==> List(uri1))
            _ <- inputMolecule(List(uri2, uri3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3))
            _ <- inputMolecule(List(uri2)).get.map(_.sorted ==> List(uri1, uri2))
            _ <- inputMolecule(List(uri2, uri3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uri_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_ ==> List(str4))
            _ <- inputMolecule(List(uri1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(uri1, uri1)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(uri1, uri2)).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uri_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uri1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(uri1, uri1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(uri1, uri2)).get.map(_.sorted ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uri_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uri2)).get.map(_.sorted ==> List(str3))
            _ <- inputMolecule(List(uri2, uri3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uri_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uri2)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(uri2, uri3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uri_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uri2)).get.map(_ ==> List(str1))
            _ <- inputMolecule(List(uri2, uri3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uri_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uri2)).get.map(_.sorted ==> List(str1, str2))
            _ <- inputMolecule(List(uri2, uri3)).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }

    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> Nil)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uri1))).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(List(Set(uri1, uri1))).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(List(Set(uri1, uri2))).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(List(Set(uri1, uri3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(uri2, uri3))).get.map(_ ==> List((uri2, Set(uri3, uri2))))
            _ <- inputMolecule(List(Set(uri4, uri5))).get.map(_ ==> List((uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))

            // 1 arg
            _ <- inputMolecule(Set(uri1)).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(Set(uri1, uri1)).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(Set(uri1, uri2)).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(Set(uri1, uri3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uri2, uri3)).get.map(_ ==> List((uri2, Set(uri3, uri2))))
            _ <- inputMolecule(Set(uri4, uri5)).get.map(_ ==> List((uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uri1, uri2), Set[URI]())).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(List(Set(uri1), Set(uri1))).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(List(Set(uri1), Set(uri2))).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2))))
            _ <- inputMolecule(List(Set(uri1), Set(uri3))).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(List(Set(uri1, uri2), Set(uri3))).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(List(Set(uri1), Set(uri2, uri3))).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2))))
            _ <- inputMolecule(List(Set(uri1), Set(uri2), Set(uri3))).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(List(Set(uri1, uri2), Set(uri3, uri4))).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri3, Set(uri4, uri3))))


            // Multiple varargs
            _ <- inputMolecule(Set(uri1, uri2), Set[URI]()).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(Set(uri1), Set(uri1)).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(Set(uri1), Set(uri2)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2))))
            _ <- inputMolecule(Set(uri1), Set(uri3)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri3)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(Set(uri1), Set(uri2, uri3)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2))))
            _ <- inputMolecule(Set(uri1), Set(uri2), Set(uri3)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri3, uri4)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri3, Set(uri4, uri3))))

            // `or`
            _ <- inputMolecule(Set(uri1, uri2) or Set[URI]()).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(Set(uri1) or Set(uri1)).get.map(_ ==> List((uri1, Set(uri1, uri2))))
            _ <- inputMolecule(Set(uri1) or Set(uri2)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2))))
            _ <- inputMolecule(Set(uri1) or Set(uri3)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(Set(uri1, uri2) or Set(uri3)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(Set(uri1) or Set(uri2, uri3)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2))))
            _ <- inputMolecule(Set(uri1) or Set(uri2) or Set(uri3)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3))))
            _ <- inputMolecule(Set(uri1, uri2) or Set(uri3, uri4)).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri3, Set(uri4, uri3))))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris.not(?)) // or m(Ns.uri.uris.!=(?))
          val all           = List(
            (uri1, Set(uri1, uri2, uri3)),
            (uri2, Set(uri2, uri3, uri4)),
            (uri3, Set(uri3, uri4, uri5))
          )
          for {
            _ <- Ns.uri.uris insert all

            _ <- inputMolecule(Nil).get.map(_ ==> all)
            _ <- inputMolecule(Set[URI]()).get.map(_ ==> all)

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uri1).get.map(_ ==> ...)
            // inputMolecule(List(uri1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(uri1)).get.map(_ ==> List(
              // (uri1, Set(uri1, uri2, uri3)),  // uri1 match
              (uri2, Set(uri2, uri3, uri4)),
              (uri3, Set(uri3, uri4, uri5))
            ))
            // Same as
            _ <- inputMolecule(List(Set(uri1))).get.map(_ ==> List(
              (uri2, Set(uri2, uri3, uri4)),
              (uri3, Set(uri3, uri4, uri5))
            ))

            _ <- inputMolecule(Set(uri2)).get.map(_ ==> List(
              // (uri1, Set(uri1, uri2, uri3)),  // uri2 match
              // (uri2, Set(uri2, uri3, uri4)),  // uri2 match
              (uri3, Set(uri3, uri4, uri5))
            ))

            _ <- inputMolecule(Set(uri3)).get.map(_ ==> Nil) // uri3 match all


            _ <- inputMolecule(Set(uri1), Set(uri2)).get.map(_ ==> List(
              // (uri1, Set(uri1, uri2, uri3)),  // uri1 match, uri2 match
              // (uri2, Set(uri2, uri3, uri4)),  // uri2 match
              (uri3, Set(uri3, uri4, uri5))
            ))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uri1, uri2)).get.map(_ ==> List(
              // (uri1, Set(uri1, uri2, uri3)),  // uri1 AND uri2 match
              (uri2, Set(uri2, uri3, uri4)),
              (uri3, Set(uri3, uri4, uri5))
            ))


            _ <- inputMolecule(Set(uri1), Set(uri3)).get.map(_ ==> Nil) // uri3 match all
            _ <- inputMolecule(Set(uri1, uri3)).get.map(_ ==> List(
              // (uri1, Set(uri1, uri2, uri3)),  // uri1 AND uri3 match
              (uri2, Set(uri2, uri3, uri4)),
              (uri3, Set(uri3, uri4, uri5))
            ))


            _ <- inputMolecule(Set(uri1), Set(uri2), Set(uri3)).get.map(_ ==> Nil) // uri3 match all
            _ <- inputMolecule(Set(uri1, uri2, uri3)).get.map(_ ==> List(
              // (uri1, Set(uri1, uri2, uri3)),  // uri1 AND uri2 AND uri3 match
              (uri2, Set(uri2, uri3, uri4)),
              (uri3, Set(uri3, uri4, uri5))
            ))


            _ <- inputMolecule(Set(uri1, uri2), Set(uri1)).get.map(_ ==> List(
              (uri2, Set(uri2, uri3, uri4)),
              (uri3, Set(uri3, uri4, uri5))
            ))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri2)).get.map(_ ==> List(
              (uri3, Set(uri3, uri4, uri5))
            ))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uri1, uri2), Set(uri4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uri1, uri2), Set(uri5)).get.map(_ ==> List(
              (uri2, Set(uri2, uri3, uri4))
            ))

            _ <- inputMolecule(Set(uri1, uri2), Set(uri2, uri3)).get.map(_ ==> List(
              (uri3, Set(uri3, uri4, uri5))
            ))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri4, uri5)).get.map(_ ==> List(
              (uri2, Set(uri2, uri3, uri4))
            ))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))

            // (uri3, uri4), (uri4, uri5), (uri4, uri5, uri6)
            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List((uri2, Set(uri3)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))

            // (uri2, uri4), (uri3, uri4), (uri4, uri5), (uri4, uri5, uri6)
            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List((uri1, Set(uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))

            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List((uri1, Set(uri1))))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri3, uri2)), (uri3, Set(uri4, uri3)), (uri4, Set(uri4, uri5)), (uri5, Set(uri4, uri6, uri5))))

            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List((uri1, Set(uri1, uri2)), (uri2, Set(uri2))))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uris(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> Nil)

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uri1))).get.map(_ ==> List(Set(uri1, uri2)))
            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List(Set(uri1, uri2, uri3))) // (uri1, uri2) + (uri2, uri3)
            _ <- inputMolecule(List(Set(uri3))).get.map(_ ==> List(Set(uri2, uri3, uri4))) // (uri2, uri3) + (uri3, uri4)

            _ <- inputMolecule(List(Set(uri1, uri2))).get.map(_ ==> List(Set(uri1, uri2)))
            _ <- inputMolecule(List(Set(uri1, uri3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(uri2, uri3))).get.map(_ ==> List(Set(uri2, uri3)))
            _ <- inputMolecule(List(Set(uri4, uri5))).get.map(_ ==> List(Set(uri4, uri5, uri6))) // (uri4, uri5) + (uri4, uri5, uri6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uri1), Set(uri1))).get.map(_ ==> List(Set(uri1, uri2)))
            _ <- inputMolecule(List(Set(uri1), Set(uri2))).get.map(_ ==> List(Set(uri1, uri2, uri3))) // (uri1, uri2) + (uri2, uri3)
            _ <- inputMolecule(List(Set(uri1), Set(uri3))).get.map(_ ==> List(Set(uri1, uri4, uri3, uri2))) // (uri1, uri2) + (uri2, uri3) + (uri3, uri4)
            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4))) // (uri1, uri2) + (uri2, uri3) + (uri3, uri4)

            _ <- inputMolecule(List(Set(uri1, uri2), Set(uri3))).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4))) // (uri1, uri2) + (uri2, uri3) + (uri3, uri4)
            _ <- inputMolecule(List(Set(uri1), Set(uri2, uri3))).get.map(_ ==> List(Set(uri1, uri3, uri2))) // (uri1, uri2) + (uri2, uri3)
            _ <- inputMolecule(List(Set(uri1), Set(uri2), Set(uri3))).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4))) // (uri1, uri2) + (uri2, uri3) + (uri3, uri4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.uris.not(?)) // or m(Ns.uri.uris.!=(?))
          for {
            _ <- Ns.uri.uris insert List(
              (uri1, Set(uri1, uri2, uri3)),
              (uri2, Set(uri2, uri3, uri4)),
              (uri3, Set(uri3, uri4, uri5))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5)))
            _ <- inputMolecule(Set[URI]()).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5)))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uri1).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(uri1)).get.map(_ ==> List(Set(uri2, uri3, uri4, uri5)))
            // Same as
            _ <- inputMolecule(List(Set(uri1))).get.map(_ ==> List(Set(uri2, uri3, uri4, uri5)))

            _ <- inputMolecule(Set(uri2)).get.map(_ ==> List(Set(uri3, uri4, uri5)))
            _ <- inputMolecule(Set(uri3)).get.map(_ ==> Nil) // uri3 match all

            _ <- inputMolecule(Set(uri1), Set(uri2)).get.map(_ ==> List(Set(uri3, uri4, uri5)))
            _ <- inputMolecule(Set(uri1), Set(uri3)).get.map(_ ==> Nil) // uri3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uri1, uri2)).get.map(_ ==> List(Set(uri2, uri3, uri4, uri5)))
            _ <- inputMolecule(Set(uri1, uri3)).get.map(_ ==> List(Set(uri2, uri3, uri4, uri5)))

            _ <- inputMolecule(Set(uri1), Set(uri2), Set(uri3)).get.map(_ ==> Nil) // uri3 match all
            _ <- inputMolecule(Set(uri1, uri2, uri3)).get.map(_ ==> List(Set(uri2, uri3, uri4, uri5)))

            _ <- inputMolecule(Set(uri1, uri2), Set(uri1)).get.map(_ ==> List(Set(uri2, uri3, uri4, uri5)))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri2)).get.map(_ ==> List(Set(uri3, uri4, uri5)))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uri1, uri2), Set(uri4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uri1, uri2), Set(uri5)).get.map(_ ==> List(Set(uri2, uri3, uri4)))

            _ <- inputMolecule(Set(uri1, uri2), Set(uri2, uri3)).get.map(_ ==> List(Set(uri3, uri4, uri5)))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri4, uri5)).get.map(_ ==> List(Set(uri2, uri3, uri4)))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uris.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5, uri6)))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5, uri6)))

            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List(Set(uri3, uri4, uri5, uri6)))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uris.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5, uri6)))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5, uri6)))

            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List(Set(uri2, uri3, uri4, uri5, uri6)))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uris.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5, uri6)))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5, uri6)))

            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List(Set(uri1)))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uris.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5, uri6)))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List(Set(uri1, uri2, uri3, uri4, uri5, uri6)))

            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List(Set(uri1, uri2)))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_ ==> List(uri6))
            _ <- inputMolecule(List(Set[URI]())).get.map(_ ==> List(uri6))


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uri1))).get.map(_ ==> List(uri1))
            _ <- inputMolecule(List(Set(uri2))).get.map(_.sorted ==> List(uri1, uri2))
            _ <- inputMolecule(List(Set(uri3))).get.map(_.sorted ==> List(uri2, uri3))

            _ <- inputMolecule(List(Set(uri1, uri1))).get.map(_ ==> List(uri1))
            _ <- inputMolecule(List(Set(uri1, uri2))).get.map(_ ==> List(uri1))
            _ <- inputMolecule(List(Set(uri1, uri3))).get.map(_ ==> Nil)
            _ <- inputMolecule(List(Set(uri2, uri3))).get.map(_ ==> List(uri2))
            _ <- inputMolecule(List(Set(uri4, uri5))).get.map(_.sorted ==> List(uri4, uri5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uri1, uri2), Set[URI]())).get.map(_ ==> List(uri1))
            _ <- inputMolecule(List(Set(uri1), Set(uri1))).get.map(_ ==> List(uri1))
            _ <- inputMolecule(List(Set(uri1), Set(uri2))).get.map(_.sorted ==> List(uri1, uri2))
            _ <- inputMolecule(List(Set(uri1), Set(uri3))).get.map(_.sorted ==> List(uri1, uri2, uri3))

            _ <- inputMolecule(List(Set(uri1, uri2), Set(uri3))).get.map(_.sorted ==> List(uri1, uri2, uri3))
            _ <- inputMolecule(List(Set(uri1), Set(uri2, uri3))).get.map(_.sorted ==> List(uri1, uri2))
            _ <- inputMolecule(List(Set(uri1), Set(uri2), Set(uri3))).get.map(_.sorted ==> List(uri1, uri2, uri3))

            _ <- inputMolecule(List(Set(uri1, uri2), Set(uri3, uri4))).get.map(_.sorted ==> List(uri1, uri3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris_.not(?)) // or m(Ns.uri.uris.!=(?))
          for {
            _ <- Ns.uri.uris insert List(
              (uri1, Set(uri1, uri2, uri3)),
              (uri2, Set(uri2, uri3, uri4)),
              (uri3, Set(uri3, uri4, uri5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3))
            _ <- inputMolecule(Set[URI]()).get.map(_.sorted ==> List(uri1, uri2, uri3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uri1).get.map(_ ==> ...)
            // inputMolecule(List(uri1)).get.map(_ ==> ...)

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(uri1)).get.map(_.sorted ==> List(uri2, uri3))
            // Same as
            _ <- inputMolecule(List(Set(uri1))).get.map(_.sorted ==> List(uri2, uri3))

            _ <- inputMolecule(Set(uri2)).get.map(_ ==> List(uri3))
            _ <- inputMolecule(Set(uri3)).get.map(_ ==> Nil) // uri3 match all


            _ <- inputMolecule(Set(uri1), Set(uri2)).get.map(_ ==> List(uri3))
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uri1, uri2)).get.map(_.sorted ==> List(uri2, uri3))

            _ <- inputMolecule(Set(uri1), Set(uri3)).get.map(_ ==> Nil) // uri3 match all
            _ <- inputMolecule(Set(uri1, uri3)).get.map(_.sorted ==> List(uri2, uri3))

            _ <- inputMolecule(Set(uri1), Set(uri2), Set(uri3)).get.map(_ ==> Nil) // uri3 match all
            _ <- inputMolecule(Set(uri1, uri2, uri3)).get.map(_.sorted ==> List(uri2, uri3))

            _ <- inputMolecule(Set(uri1, uri2), Set(uri1)).get.map(_.sorted ==> List(uri2, uri3))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri2)).get.map(_ ==> List(uri3))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri3)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uri1, uri2), Set(uri4)).get.map(_ ==> Nil)
            _ <- inputMolecule(Set(uri1, uri2), Set(uri5)).get.map(_ ==> List(uri2))

            _ <- inputMolecule(Set(uri1, uri2), Set(uri2, uri3)).get.map(_ ==> List(uri3))
            _ <- inputMolecule(Set(uri1, uri2), Set(uri4, uri5)).get.map(_ ==> List(uri2))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))
            _ <- inputMolecule(List(Set[URI]())).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))

            // (uri3, uri4), (uri4, uri5), (uri4, uri5, uri6)
            _ <- inputMolecule(List(Set(uri2))).get.map(_.sorted ==> List(uri2, uri3, uri4, uri5))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))
            _ <- inputMolecule(List(Set[URI]())).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))

            // (uri2, uri4), (uri3, uri4), (uri4, uri5), (uri4, uri5, uri6)
            _ <- inputMolecule(List(Set(uri2))).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))
            _ <- inputMolecule(List(Set[URI]())).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))

            _ <- inputMolecule(List(Set(uri2))).get.map(_ ==> List(uri1))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uri.uris_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))
            _ <- inputMolecule(List(Set[URI]())).get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))

            _ <- inputMolecule(List(Set(uri2))).get.map(_.sorted ==> List(uri1, uri2))

            _ <- inputMolecule(List(Set(uri2, uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }

            _ <- inputMolecule(List(Set(uri2), Set(uri3))).get.recover { case MoleculeException(err, _) =>
              err ==> "Can't apply multiple values to comparison function."
            }
          } yield ()
        }
      }
    }
  }
}