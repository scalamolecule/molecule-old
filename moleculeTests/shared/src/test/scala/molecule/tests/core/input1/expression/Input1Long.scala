package molecule.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}


object Input1Long extends AsyncTestSuite {

  def oneData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.str.long$ insert List(
      (str1, Some(long1)),
      (str2, Some(long2)),
      (str3, Some(long3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.long.longs$ insert List(
      (long1, Some(Set(long1, long2))),
      (long2, Some(Set(long2, long3))),
      (long3, Some(Set(long3, long4))),
      (long4, Some(Set(long4, long5))),
      (long5, Some(Set(long4, long5, long6))),
      (long6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.long(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === Nil

            _ <- inputMolecule(List(long1)).get === List(long1)
            _ <- inputMolecule(List(long1, long1)).get === List(long1)
            _ <- inputMolecule(List(long1, long2)).get === List(long1, long2)

            // Varargs
            _ <- inputMolecule(long1).get === List(long1)
            _ <- inputMolecule(long1, long2).get === List(long1, long2)

            // `or`
            _ <- inputMolecule(long1 or long2).get === List(long1, long2)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3)

            _ <- inputMolecule(List(long1)).get === List(long2, long3)
            _ <- inputMolecule(List(long1, long1)).get === List(long2, long3)
            _ <- inputMolecule(List(long1, long2)).get === List(long3)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.long.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3)
            _ <- inputMolecule(List(long2)).get === List(long3)
            //(inputMolecule(List(long2, long3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3)
            _ <- inputMolecule(List(long2)).get === List(long2, long3)
            //(inputMolecule(List(long2, long3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.long.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3)
            _ <- inputMolecule(List(long2)).get === List(long1)
            //(inputMolecule(List(long2, long3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3)
            _ <- inputMolecule(List(long2)).get === List(long1, long2)
            //(inputMolecule(List(long2, long3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.long_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str4)
            _ <- inputMolecule(List(long1)).get === List(str1)
            _ <- inputMolecule(List(long1, long1)).get === List(str1)
            _ <- inputMolecule(List(long1, long2)).get === List(str1, str2)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.long_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(long1)).get === List(str2, str3)
            _ <- inputMolecule(List(long1, long1)).get === List(str2, str3)
            _ <- inputMolecule(List(long1, long2)).get === List(str3)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.long_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(long2)).get === List(str3)
            //(inputMolecule(List(long2, long3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.long_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(long2)).get === List(str2, str3)
            //(inputMolecule(List(long2, long3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.long_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(long2)).get === List(str1)
            //(inputMolecule(List(long2, long3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.long_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(long2)).get === List(str1, str2)
            //(inputMolecule(List(long2, long3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[Long]())).get === Nil


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(long1))).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(List(Set(long1, long1))).get === List((long1, Set(long1, long2)))

            _ <- inputMolecule(List(Set(long1, long2))).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(List(Set(long1, long3))).get === Nil
            _ <- inputMolecule(List(Set(long2, long3))).get === List((long2, Set(long3, long2)))
            _ <- inputMolecule(List(Set(long4, long5))).get === List((long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))

            // 1 arg
            _ <- inputMolecule(Set(long1)).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(Set(long1, long1)).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(Set(long1, long2)).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(Set(long1, long3)).get === Nil
            _ <- inputMolecule(Set(long2, long3)).get === List((long2, Set(long3, long2)))
            _ <- inputMolecule(Set(long4, long5)).get === List((long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(long1, long2), Set[Long]())).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(List(Set(long1), Set(long1))).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(List(Set(long1), Set(long2))).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)))
            _ <- inputMolecule(List(Set(long1), Set(long3))).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(List(Set(long1, long2), Set(long3))).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(List(Set(long1), Set(long2, long3))).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)))
            _ <- inputMolecule(List(Set(long1), Set(long2), Set(long3))).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(List(Set(long1, long2), Set(long3, long4))).get === List((long1, Set(long1, long2)), (long3, Set(long4, long3)))


            // Multiple varargs
            _ <- inputMolecule(Set(long1, long2), Set[Long]()).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(Set(long1), Set(long1)).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(Set(long1), Set(long2)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)))
            _ <- inputMolecule(Set(long1), Set(long3)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(Set(long1, long2), Set(long3)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(Set(long1), Set(long2, long3)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)))
            _ <- inputMolecule(Set(long1), Set(long2), Set(long3)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(Set(long1, long2), Set(long3, long4)).get === List((long1, Set(long1, long2)), (long3, Set(long4, long3)))

            // `or`
            _ <- inputMolecule(Set(long1, long2) or Set[Long]()).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(Set(long1) or Set(long1)).get === List((long1, Set(long1, long2)))
            _ <- inputMolecule(Set(long1) or Set(long2)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)))
            _ <- inputMolecule(Set(long1) or Set(long3)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(Set(long1, long2) or Set(long3)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(Set(long1) or Set(long2, long3)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)))
            _ <- inputMolecule(Set(long1) or Set(long2) or Set(long3)).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)))
            _ <- inputMolecule(Set(long1, long2) or Set(long3, long4)).get === List((long1, Set(long1, long2)), (long3, Set(long4, long3)))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs.not(?)) // or m(Ns.long.longs.!=(?))
          val all           = List(
            (long1, Set(long1, long2, long3)),
            (long2, Set(long2, long3, long4)),
            (long3, Set(long3, long4, long5))
          )
          for {
            _ <- manyData
            _ <- Ns.long.longs insert all

            _ <- inputMolecule(Nil).get === all
            _ <- inputMolecule(Set[Long]()).get === all

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(long1).get === ...
            // inputMolecule(List(long1)).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(long1)).get === List(
              // (long1, Set(long1, long2, long3)),  // long1 match
              (long2, Set(long2, long3, long4)),
              (long3, Set(long3, long4, long5))
            )
            // Same as
            _ <- inputMolecule(List(Set(long1))).get === List(
              (long2, Set(long2, long3, long4)),
              (long3, Set(long3, long4, long5))
            )

            _ <- inputMolecule(Set(long2)).get === List(
              // (long1, Set(long1, long2, long3)),  // long2 match
              // (long2, Set(long2, long3, long4)),  // long2 match
              (long3, Set(long3, long4, long5))
            )

            _ <- inputMolecule(Set(long3)).get === Nil // long3 match all


            _ <- inputMolecule(Set(long1), Set(long2)).get === List(
              // (long1, Set(long1, long2, long3)),  // long1 match, long2 match
              // (long2, Set(long2, long3, long4)),  // long2 match
              (long3, Set(long3, long4, long5))
            )
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(long1, long2)).get === List(
              // (long1, Set(long1, long2, long3)),  // long1 AND long2 match
              (long2, Set(long2, long3, long4)),
              (long3, Set(long3, long4, long5))
            )


            _ <- inputMolecule(Set(long1), Set(long3)).get === Nil // long3 match all
            _ <- inputMolecule(Set(long1, long3)).get === List(
              // (long1, Set(long1, long2, long3)),  // long1 AND long3 match
              (long2, Set(long2, long3, long4)),
              (long3, Set(long3, long4, long5))
            )


            _ <- inputMolecule(Set(long1), Set(long2), Set(long3)).get === Nil // long3 match all
            _ <- inputMolecule(Set(long1, long2, long3)).get === List(
              // (long1, Set(long1, long2, long3)),  // long1 AND long2 AND long3 match
              (long2, Set(long2, long3, long4)),
              (long3, Set(long3, long4, long5))
            )


            _ <- inputMolecule(Set(long1, long2), Set(long1)).get === List(
              (long2, Set(long2, long3, long4)),
              (long3, Set(long3, long4, long5))
            )
            _ <- inputMolecule(Set(long1, long2), Set(long2)).get === List(
              (long3, Set(long3, long4, long5))
            )
            _ <- inputMolecule(Set(long1, long2), Set(long3)).get === Nil
            _ <- inputMolecule(Set(long1, long2), Set(long4)).get === Nil
            _ <- inputMolecule(Set(long1, long2), Set(long5)).get === List(
              (long2, Set(long2, long3, long4))
            )

            _ <- inputMolecule(Set(long1, long2), Set(long2, long3)).get === List(
              (long3, Set(long3, long4, long5))
            )
            _ <- inputMolecule(Set(long1, long2), Set(long4, long5)).get === List(
              (long2, Set(long2, long3, long4))
            )
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))
            _ <- inputMolecule(List(Set[Long]())).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))

            // (long3, long4), (long4, long5), (long4, long5, long6)
            _ <- inputMolecule(List(Set(long2))).get === List((long2, Set(long3)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))
            _ <- inputMolecule(List(Set[Long]())).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))

            // (long2, long4), (long3, long4), (long4, long5), (long4, long5, long6)
            _ <- inputMolecule(List(Set(long2))).get === List((long1, Set(long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))
            _ <- inputMolecule(List(Set[Long]())).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))

            _ <- inputMolecule(List(Set(long2))).get === List((long1, Set(long1)))

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))
            _ <- inputMolecule(List(Set[Long]())).get === List((long1, Set(long1, long2)), (long2, Set(long3, long2)), (long3, Set(long4, long3)), (long4, Set(long4, long5)), (long5, Set(long4, long6, long5)))

            _ <- inputMolecule(List(Set(long2))).get === List((long1, Set(long1, long2)), (long2, Set(long2)))

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.longs(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[Long]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(long1))).get === List(Set(long1, long2))
            _ <- inputMolecule(List(Set(long2))).get === List(Set(long1, long2, long3)) // (long1, long2) + (long2, long3)
            _ <- inputMolecule(List(Set(long3))).get === List(Set(long2, long3, long4)) // (long2, long3) + (long3, long4)

            _ <- inputMolecule(List(Set(long1, long2))).get === List(Set(long1, long2))
            _ <- inputMolecule(List(Set(long1, long3))).get === Nil
            _ <- inputMolecule(List(Set(long2, long3))).get === List(Set(long2, long3))
            _ <- inputMolecule(List(Set(long4, long5))).get === List(Set(long4, long5, long6)) // (long4, long5) + (long4, long5, long6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(long1), Set(long1))).get === List(Set(long1, long2))
            _ <- inputMolecule(List(Set(long1), Set(long2))).get === List(Set(long1, long2, long3)) // (long1, long2) + (long2, long3)
            _ <- inputMolecule(List(Set(long1), Set(long3))).get === List(Set(long1, long4, long3, long2)) // (long1, long2) + (long2, long3) + (long3, long4)
            _ <- inputMolecule(List(Set(long2), Set(long3))).get === List(Set(long1, long2, long3, long4)) // (long1, long2) + (long2, long3) + (long3, long4)

            _ <- inputMolecule(List(Set(long1, long2), Set(long3))).get === List(Set(long1, long2, long3, long4)) // (long1, long2) + (long2, long3) + (long3, long4)
            _ <- inputMolecule(List(Set(long1), Set(long2, long3))).get === List(Set(long1, long3, long2)) // (long1, long2) + (long2, long3)
            _ <- inputMolecule(List(Set(long1), Set(long2), Set(long3))).get === List(Set(long1, long2, long3, long4)) // (long1, long2) + (long2, long3) + (long3, long4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.longs.not(?)) // or m(Ns.long.longs.!=(?))
          for {
            _ <- manyData
            _ <- Ns.long.longs insert List(
              (long1, Set(long1, long2, long3)),
              (long2, Set(long2, long3, long4)),
              (long3, Set(long3, long4, long5))
            )

            _ <- inputMolecule(Nil).get === List(Set(long1, long2, long3, long4, long5))
            _ <- inputMolecule(Set[Long]()).get === List(Set(long1, long2, long3, long4, long5))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(long1).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(long1)).get === List(Set(long2, long3, long4, long5))
            // Same as
            _ <- inputMolecule(List(Set(long1))).get === List(Set(long2, long3, long4, long5))

            _ <- inputMolecule(Set(long2)).get === List(Set(long3, long4, long5))
            _ <- inputMolecule(Set(long3)).get === Nil // long3 match all

            _ <- inputMolecule(Set(long1), Set(long2)).get === List(Set(long3, long4, long5))
            _ <- inputMolecule(Set(long1), Set(long3)).get === Nil // long3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(long1, long2)).get === List(Set(long2, long3, long4, long5))
            _ <- inputMolecule(Set(long1, long3)).get === List(Set(long2, long3, long4, long5))

            _ <- inputMolecule(Set(long1), Set(long2), Set(long3)).get === Nil // long3 match all
            _ <- inputMolecule(Set(long1, long2, long3)).get === List(Set(long2, long3, long4, long5))

            _ <- inputMolecule(Set(long1, long2), Set(long1)).get === List(Set(long2, long3, long4, long5))
            _ <- inputMolecule(Set(long1, long2), Set(long2)).get === List(Set(long3, long4, long5))
            _ <- inputMolecule(Set(long1, long2), Set(long3)).get === Nil
            _ <- inputMolecule(Set(long1, long2), Set(long4)).get === Nil
            _ <- inputMolecule(Set(long1, long2), Set(long5)).get === List(Set(long2, long3, long4))

            _ <- inputMolecule(Set(long1, long2), Set(long2, long3)).get === List(Set(long3, long4, long5))
            _ <- inputMolecule(Set(long1, long2), Set(long4, long5)).get === List(Set(long2, long3, long4))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.longs.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(long1, long2, long3, long4, long5, long6))
            _ <- inputMolecule(List(Set[Long]())).get === List(Set(long1, long2, long3, long4, long5, long6))

            _ <- inputMolecule(List(Set(long2))).get === List(Set(long3, long4, long5, long6))

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.longs.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(long1, long2, long3, long4, long5, long6))
            _ <- inputMolecule(List(Set[Long]())).get === List(Set(long1, long2, long3, long4, long5, long6))

            _ <- inputMolecule(List(Set(long2))).get === List(Set(long2, long3, long4, long5, long6))

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.longs.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(long1, long2, long3, long4, long5, long6))
            _ <- inputMolecule(List(Set[Long]())).get === List(Set(long1, long2, long3, long4, long5, long6))

            _ <- inputMolecule(List(Set(long2))).get === List(Set(long1))

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.longs.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(long1, long2, long3, long4, long5, long6))
            _ <- inputMolecule(List(Set[Long]())).get === List(Set(long1, long2, long3, long4, long5, long6))

            _ <- inputMolecule(List(Set(long2))).get === List(Set(long1, long2))

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(long6)
            _ <- inputMolecule(List(Set[Long]())).get === List(long6)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(long1))).get === List(long1)
            _ <- inputMolecule(List(Set(long2))).get === List(long1, long2)
            _ <- inputMolecule(List(Set(long3))).get === List(long2, long3)

            _ <- inputMolecule(List(Set(long1, long1))).get === List(long1)
            _ <- inputMolecule(List(Set(long1, long2))).get === List(long1)
            _ <- inputMolecule(List(Set(long1, long3))).get === Nil
            _ <- inputMolecule(List(Set(long2, long3))).get === List(long2)
            _ <- inputMolecule(List(Set(long4, long5))).get === List(long4, long5)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(long1, long2), Set[Long]())).get === List(long1)
            _ <- inputMolecule(List(Set(long1), Set(long1))).get === List(long1)
            _ <- inputMolecule(List(Set(long1), Set(long2))).get === List(long1, long2)
            _ <- inputMolecule(List(Set(long1), Set(long3))).get === List(long1, long2, long3)

            _ <- inputMolecule(List(Set(long1, long2), Set(long3))).get === List(long1, long2, long3)
            _ <- inputMolecule(List(Set(long1), Set(long2, long3))).get === List(long1, long2)
            _ <- inputMolecule(List(Set(long1), Set(long2), Set(long3))).get === List(long1, long2, long3)

            _ <- inputMolecule(List(Set(long1, long2), Set(long3, long4))).get === List(long1, long3)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs_.not(?)) // or m(Ns.long.longs.!=(?))
          for {
            _ <- manyData
            _ <- Ns.long.longs insert List(
              (long1, Set(long1, long2, long3)),
              (long2, Set(long2, long3, long4)),
              (long3, Set(long3, long4, long5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(long1, long2, long3))
            _ <- inputMolecule(Set[Long]()).get.map(_.sorted ==> List(long1, long2, long3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(long1).get === ...
            // inputMolecule(List(long1)).get === ...

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(long1)).get.map(_.sorted ==> List(long2, long3))
            // Same as
            _ <- inputMolecule(List(Set(long1))).get.map(_.sorted ==> List(long2, long3))

            _ <- inputMolecule(Set(long2)).get === List(long3)
            _ <- inputMolecule(Set(long3)).get === Nil // long3 match all


            _ <- inputMolecule(Set(long1), Set(long2)).get === List(long3)
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(long1, long2)).get.map(_.sorted ==> List(long2, long3))

            _ <- inputMolecule(Set(long1), Set(long3)).get === Nil // long3 match all
            _ <- inputMolecule(Set(long1, long3)).get.map(_.sorted ==> List(long2, long3))

            _ <- inputMolecule(Set(long1), Set(long2), Set(long3)).get === Nil // long3 match all
            _ <- inputMolecule(Set(long1, long2, long3)).get.map(_.sorted ==> List(long2, long3))

            _ <- inputMolecule(Set(long1, long2), Set(long1)).get.map(_.sorted ==> List(long2, long3))
            _ <- inputMolecule(Set(long1, long2), Set(long2)).get === List(long3)
            _ <- inputMolecule(Set(long1, long2), Set(long3)).get === Nil
            _ <- inputMolecule(Set(long1, long2), Set(long4)).get === Nil
            _ <- inputMolecule(Set(long1, long2), Set(long5)).get === List(long2)

            _ <- inputMolecule(Set(long1, long2), Set(long2, long3)).get === List(long3)
            _ <- inputMolecule(Set(long1, long2), Set(long4, long5)).get === List(long2)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3, long4, long5)
            _ <- inputMolecule(List(Set[Long]())).get === List(long1, long2, long3, long4, long5)

            // (long3, long4), (long4, long5), (long4, long5, long6)
            _ <- inputMolecule(List(Set(long2))).get === List(long2, long3, long4, long5)

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3, long4, long5)
            _ <- inputMolecule(List(Set[Long]())).get === List(long1, long2, long3, long4, long5)

            // (long2, long4), (long3, long4), (long4, long5), (long4, long5, long6)
            _ <- inputMolecule(List(Set(long2))).get === List(long1, long2, long3, long4, long5)

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3, long4, long5)
            _ <- inputMolecule(List(Set[Long]())).get === List(long1, long2, long3, long4, long5)

            _ <- inputMolecule(List(Set(long2))).get === List(long1)

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.long.longs_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(long1, long2, long3, long4, long5)
            _ <- inputMolecule(List(Set[Long]())).get === List(long1, long2, long3, long4, long5)

            _ <- inputMolecule(List(Set(long2))).get === List(long1, long2)

            //(inputMolecule(List(Set(long2, long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(long2), Set(long3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }
  }
}