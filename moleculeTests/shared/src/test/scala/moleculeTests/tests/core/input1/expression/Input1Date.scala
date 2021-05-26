package moleculeTests.tests.core.input1.expression

import java.util.Date
import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import utest._
import moleculeTests.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}


object Input1Date extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.date$ insert List(
      (str1, Some(date1)),
      (str2, Some(date2)),
      (str3, Some(date3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.date.dates$ insert List(
      (date1, Some(Set(date1, date2))),
      (date2, Some(Set(date2, date3))),
      (date3, Some(Set(date3, date4))),
      (date4, Some(Set(date4, date5))),
      (date5, Some(Set(date4, date5, date6))),
      (date6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.date(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === Nil

            _ <- inputMolecule(List(date1)).get === List(date1)
            _ <- inputMolecule(List(date1, date1)).get === List(date1)
            _ <- inputMolecule(List(date1, date2)).get.map(_.sorted ==> List(date1, date2))

            // Varargs
            _ <- inputMolecule(date1).get === List(date1)
            _ <- inputMolecule(date1, date2).get.map(_.sorted ==> List(date1, date2))

            // `or`
            _ <- inputMolecule(date1 or date2).get.map(_.sorted ==> List(date1, date2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3))

            _ <- inputMolecule(List(date1)).get.map(_.sorted ==> List(date2, date3))
            _ <- inputMolecule(List(date1, date1)).get.map(_.sorted ==> List(date2, date3))
            _ <- inputMolecule(List(date1, date2)).get.map(_.sorted ==> List(date3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.date.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3))
            _ <- inputMolecule(List(date2)).get.map(_.sorted ==> List(date3))
            //(inputMolecule(List(date2, date3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3))
            _ <- inputMolecule(List(date2)).get.map(_.sorted ==> List(date2, date3))
            //(inputMolecule(List(date2, date3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.date.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3))
            _ <- inputMolecule(List(date2)).get === List(date1)
            //(inputMolecule(List(date2, date3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3))
            _ <- inputMolecule(List(date2)).get.map(_.sorted ==> List(date1, date2))
            //(inputMolecule(List(date2, date3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.date_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str4)
            _ <- inputMolecule(List(date1)).get === List(str1)
            _ <- inputMolecule(List(date1, date1)).get === List(str1)
            _ <- inputMolecule(List(date1, date2)).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.date_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(date1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(date1, date1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(date1, date2)).get.map(_.sorted ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.date_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(date2)).get.map(_.sorted ==> List(str3))
            //(inputMolecule(List(date2, date3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.date_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(date2)).get.map(_.sorted ==> List(str2, str3))
            //(inputMolecule(List(date2, date3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.date_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(date2)).get === List(str1)
            //(inputMolecule(List(date2, date3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.date_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(date2)).get.map(_.sorted ==> List(str1, str2))
            //(inputMolecule(List(date2, date3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[Date]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(date1))).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(List(Set(date1, date1))).get === List((date1, Set(date1, date2)))

            _ <- inputMolecule(List(Set(date1, date2))).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(List(Set(date1, date3))).get === Nil
            _ <- inputMolecule(List(Set(date2, date3))).get === List((date2, Set(date3, date2)))
            _ <- inputMolecule(List(Set(date4, date5))).get === List((date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))

            // 1 arg
            _ <- inputMolecule(Set(date1)).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(Set(date1, date1)).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(Set(date1, date2)).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(Set(date1, date3)).get === Nil
            _ <- inputMolecule(Set(date2, date3)).get === List((date2, Set(date3, date2)))
            _ <- inputMolecule(Set(date4, date5)).get === List((date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(date1, date2), Set[Date]())).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(List(Set(date1), Set(date1))).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(List(Set(date1), Set(date2))).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)))
            _ <- inputMolecule(List(Set(date1), Set(date3))).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(List(Set(date1, date2), Set(date3))).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(List(Set(date1), Set(date2, date3))).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)))
            _ <- inputMolecule(List(Set(date1), Set(date2), Set(date3))).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(List(Set(date1, date2), Set(date3, date4))).get === List((date1, Set(date1, date2)), (date3, Set(date4, date3)))


            // Multiple varargs
            _ <- inputMolecule(Set(date1, date2), Set[Date]()).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(Set(date1), Set(date1)).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(Set(date1), Set(date2)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)))
            _ <- inputMolecule(Set(date1), Set(date3)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(Set(date1, date2), Set(date3)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(Set(date1), Set(date2, date3)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)))
            _ <- inputMolecule(Set(date1), Set(date2), Set(date3)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(Set(date1, date2), Set(date3, date4)).get === List((date1, Set(date1, date2)), (date3, Set(date4, date3)))

            // `or`
            _ <- inputMolecule(Set(date1, date2) or Set[Date]()).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(Set(date1) or Set(date1)).get === List((date1, Set(date1, date2)))
            _ <- inputMolecule(Set(date1) or Set(date2)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)))
            _ <- inputMolecule(Set(date1) or Set(date3)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(Set(date1, date2) or Set(date3)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(Set(date1) or Set(date2, date3)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)))
            _ <- inputMolecule(Set(date1) or Set(date2) or Set(date3)).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)))
            _ <- inputMolecule(Set(date1, date2) or Set(date3, date4)).get === List((date1, Set(date1, date2)), (date3, Set(date4, date3)))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates.not(?)) // or m(Ns.date.dates.!=(?))
          val all           = List(
            (date1, Set(date1, date2, date3)),
            (date2, Set(date2, date3, date4)),
            (date3, Set(date3, date4, date5))
          )
          for {
            _ <- manyData
            _ <- Ns.date.dates insert all

            _ <- inputMolecule(Nil).get === all
            _ <- inputMolecule(Set[Date]()).get === all

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(date1).get === ...
            // inputMolecule(List(date1)).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(date1)).get === List(
              // (date1, Set(date1, date2, date3)),  // date1 match
              (date2, Set(date2, date3, date4)),
              (date3, Set(date3, date4, date5))
            )
            // Same as
            _ <- inputMolecule(List(Set(date1))).get === List(
              (date2, Set(date2, date3, date4)),
              (date3, Set(date3, date4, date5))
            )

            _ <- inputMolecule(Set(date2)).get === List(
              // (date1, Set(date1, date2, date3)),  // date2 match
              // (date2, Set(date2, date3, date4)),  // date2 match
              (date3, Set(date3, date4, date5))
            )

            _ <- inputMolecule(Set(date3)).get === Nil // date3 match all


            _ <- inputMolecule(Set(date1), Set(date2)).get === List(
              // (date1, Set(date1, date2, date3)),  // date1 match, date2 match
              // (date2, Set(date2, date3, date4)),  // date2 match
              (date3, Set(date3, date4, date5))
            )
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(date1, date2)).get === List(
              // (date1, Set(date1, date2, date3)),  // date1 AND date2 match
              (date2, Set(date2, date3, date4)),
              (date3, Set(date3, date4, date5))
            )


            _ <- inputMolecule(Set(date1), Set(date3)).get === Nil // date3 match all
            _ <- inputMolecule(Set(date1, date3)).get === List(
              // (date1, Set(date1, date2, date3)),  // date1 AND date3 match
              (date2, Set(date2, date3, date4)),
              (date3, Set(date3, date4, date5))
            )


            _ <- inputMolecule(Set(date1), Set(date2), Set(date3)).get === Nil // date3 match all
            _ <- inputMolecule(Set(date1, date2, date3)).get === List(
              // (date1, Set(date1, date2, date3)),  // date1 AND date2 AND date3 match
              (date2, Set(date2, date3, date4)),
              (date3, Set(date3, date4, date5))
            )


            _ <- inputMolecule(Set(date1, date2), Set(date1)).get === List(
              (date2, Set(date2, date3, date4)),
              (date3, Set(date3, date4, date5))
            )
            _ <- inputMolecule(Set(date1, date2), Set(date2)).get === List(
              (date3, Set(date3, date4, date5))
            )
            _ <- inputMolecule(Set(date1, date2), Set(date3)).get === Nil
            _ <- inputMolecule(Set(date1, date2), Set(date4)).get === Nil
            _ <- inputMolecule(Set(date1, date2), Set(date5)).get === List(
              (date2, Set(date2, date3, date4))
            )

            _ <- inputMolecule(Set(date1, date2), Set(date2, date3)).get === List(
              (date3, Set(date3, date4, date5))
            )
            _ <- inputMolecule(Set(date1, date2), Set(date4, date5)).get === List(
              (date2, Set(date2, date3, date4))
            )
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))
            _ <- inputMolecule(List(Set[Date]())).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))

            // (date3, date4), (date4, date5), (date4, date5, date6)
            _ <- inputMolecule(List(Set(date2))).get === List((date2, Set(date3)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))
            _ <- inputMolecule(List(Set[Date]())).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))

            // (date2, date4), (date3, date4), (date4, date5), (date4, date5, date6)
            _ <- inputMolecule(List(Set(date2))).get === List((date1, Set(date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))
            _ <- inputMolecule(List(Set[Date]())).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))

            _ <- inputMolecule(List(Set(date2))).get === List((date1, Set(date1)))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))
            _ <- inputMolecule(List(Set[Date]())).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date3)), (date4, Set(date4, date5)), (date5, Set(date4, date6, date5)))

            _ <- inputMolecule(List(Set(date2))).get === List((date1, Set(date1, date2)), (date2, Set(date2)))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.dates(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[Date]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(date1))).get === List(Set(date1, date2))
            _ <- inputMolecule(List(Set(date2))).get === List(Set(date1, date2, date3)) // (date1, date2) + (date2, date3)
            _ <- inputMolecule(List(Set(date3))).get === List(Set(date2, date3, date4)) // (date2, date3) + (date3, date4)

            _ <- inputMolecule(List(Set(date1, date2))).get === List(Set(date1, date2))
            _ <- inputMolecule(List(Set(date1, date3))).get === Nil
            _ <- inputMolecule(List(Set(date2, date3))).get === List(Set(date2, date3))
            _ <- inputMolecule(List(Set(date4, date5))).get === List(Set(date4, date5, date6)) // (date4, date5) + (date4, date5, date6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(date1), Set(date1))).get === List(Set(date1, date2))
            _ <- inputMolecule(List(Set(date1), Set(date2))).get === List(Set(date1, date2, date3)) // (date1, date2) + (date2, date3)
            _ <- inputMolecule(List(Set(date1), Set(date3))).get === List(Set(date1, date4, date3, date2)) // (date1, date2) + (date2, date3) + (date3, date4)
            _ <- inputMolecule(List(Set(date2), Set(date3))).get === List(Set(date1, date2, date3, date4)) // (date1, date2) + (date2, date3) + (date3, date4)

            _ <- inputMolecule(List(Set(date1, date2), Set(date3))).get === List(Set(date1, date2, date3, date4)) // (date1, date2) + (date2, date3) + (date3, date4)
            _ <- inputMolecule(List(Set(date1), Set(date2, date3))).get === List(Set(date1, date3, date2)) // (date1, date2) + (date2, date3)
            _ <- inputMolecule(List(Set(date1), Set(date2), Set(date3))).get === List(Set(date1, date2, date3, date4)) // (date1, date2) + (date2, date3) + (date3, date4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.dates.not(?)) // or m(Ns.date.dates.!=(?))
          for {
            _ <- manyData
            _ <- Ns.date.dates insert List(
              (date1, Set(date1, date2, date3)),
              (date2, Set(date2, date3, date4)),
              (date3, Set(date3, date4, date5))
            )

            _ <- inputMolecule(Nil).get === List(Set(date1, date2, date3, date4, date5))
            _ <- inputMolecule(Set[Date]()).get === List(Set(date1, date2, date3, date4, date5))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(date1).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(date1)).get === List(Set(date2, date3, date4, date5))
            // Same as
            _ <- inputMolecule(List(Set(date1))).get === List(Set(date2, date3, date4, date5))

            _ <- inputMolecule(Set(date2)).get === List(Set(date3, date4, date5))
            _ <- inputMolecule(Set(date3)).get === Nil // date3 match all

            _ <- inputMolecule(Set(date1), Set(date2)).get === List(Set(date3, date4, date5))
            _ <- inputMolecule(Set(date1), Set(date3)).get === Nil // date3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(date1, date2)).get === List(Set(date2, date3, date4, date5))
            _ <- inputMolecule(Set(date1, date3)).get === List(Set(date2, date3, date4, date5))

            _ <- inputMolecule(Set(date1), Set(date2), Set(date3)).get === Nil // date3 match all
            _ <- inputMolecule(Set(date1, date2, date3)).get === List(Set(date2, date3, date4, date5))

            _ <- inputMolecule(Set(date1, date2), Set(date1)).get === List(Set(date2, date3, date4, date5))
            _ <- inputMolecule(Set(date1, date2), Set(date2)).get === List(Set(date3, date4, date5))
            _ <- inputMolecule(Set(date1, date2), Set(date3)).get === Nil
            _ <- inputMolecule(Set(date1, date2), Set(date4)).get === Nil
            _ <- inputMolecule(Set(date1, date2), Set(date5)).get === List(Set(date2, date3, date4))

            _ <- inputMolecule(Set(date1, date2), Set(date2, date3)).get === List(Set(date3, date4, date5))
            _ <- inputMolecule(Set(date1, date2), Set(date4, date5)).get === List(Set(date2, date3, date4))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.dates.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(date1, date2, date3, date4, date5, date6))
            _ <- inputMolecule(List(Set[Date]())).get === List(Set(date1, date2, date3, date4, date5, date6))

            _ <- inputMolecule(List(Set(date2))).get === List(Set(date3, date4, date5, date6))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.dates.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(date1, date2, date3, date4, date5, date6))
            _ <- inputMolecule(List(Set[Date]())).get === List(Set(date1, date2, date3, date4, date5, date6))

            _ <- inputMolecule(List(Set(date2))).get === List(Set(date2, date3, date4, date5, date6))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.dates.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(date1, date2, date3, date4, date5, date6))
            _ <- inputMolecule(List(Set[Date]())).get === List(Set(date1, date2, date3, date4, date5, date6))

            _ <- inputMolecule(List(Set(date2))).get === List(Set(date1))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.dates.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(date1, date2, date3, date4, date5, date6))
            _ <- inputMolecule(List(Set[Date]())).get === List(Set(date1, date2, date3, date4, date5, date6))

            _ <- inputMolecule(List(Set(date2))).get === List(Set(date1, date2))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(date6)
            _ <- inputMolecule(List(Set[Date]())).get === List(date6)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(date1))).get === List(date1)
            _ <- inputMolecule(List(Set(date2))).get.map(_.sorted ==> List(date1, date2))
            _ <- inputMolecule(List(Set(date3))).get.map(_.sorted ==> List(date2, date3))

            _ <- inputMolecule(List(Set(date1, date1))).get === List(date1)
            _ <- inputMolecule(List(Set(date1, date2))).get === List(date1)
            _ <- inputMolecule(List(Set(date1, date3))).get === Nil
            _ <- inputMolecule(List(Set(date2, date3))).get === List(date2)
            _ <- inputMolecule(List(Set(date4, date5))).get.map(_.sorted ==> List(date4, date5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(date1, date2), Set[Date]())).get === List(date1)
            _ <- inputMolecule(List(Set(date1), Set(date1))).get === List(date1)
            _ <- inputMolecule(List(Set(date1), Set(date2))).get.map(_.sorted ==> List(date1, date2))
            _ <- inputMolecule(List(Set(date1), Set(date3))).get.map(_.sorted ==> List(date1, date2, date3))

            _ <- inputMolecule(List(Set(date1, date2), Set(date3))).get.map(_.sorted ==> List(date1, date2, date3))
            _ <- inputMolecule(List(Set(date1), Set(date2, date3))).get.map(_.sorted ==> List(date1, date2))
            _ <- inputMolecule(List(Set(date1), Set(date2), Set(date3))).get.map(_.sorted ==> List(date1, date2, date3))

            _ <- inputMolecule(List(Set(date1, date2), Set(date3, date4))).get.map(_.sorted ==> List(date1, date3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates_.not(?)) // or m(Ns.date.dates.!=(?))
          for {
            _ <- manyData
            _ <- Ns.date.dates insert List(
              (date1, Set(date1, date2, date3)),
              (date2, Set(date2, date3, date4)),
              (date3, Set(date3, date4, date5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3))
            _ <- inputMolecule(Set[Date]()).get.map(_.sorted ==> List(date1, date2, date3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(date1).get === ...
            // inputMolecule(List(date1)).get === ...

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(date1)).get.map(_.sorted ==> List(date2, date3))
            // Same as
            _ <- inputMolecule(List(Set(date1))).get.map(_.sorted ==> List(date2, date3))

            _ <- inputMolecule(Set(date2)).get === List(date3)
            _ <- inputMolecule(Set(date3)).get === Nil // date3 match all


            _ <- inputMolecule(Set(date1), Set(date2)).get === List(date3)
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(date1, date2)).get.map(_.sorted ==> List(date2, date3))

            _ <- inputMolecule(Set(date1), Set(date3)).get === Nil // date3 match all
            _ <- inputMolecule(Set(date1, date3)).get.map(_.sorted ==> List(date2, date3))

            _ <- inputMolecule(Set(date1), Set(date2), Set(date3)).get === Nil // date3 match all
            _ <- inputMolecule(Set(date1, date2, date3)).get.map(_.sorted ==> List(date2, date3))

            _ <- inputMolecule(Set(date1, date2), Set(date1)).get.map(_.sorted ==> List(date2, date3))
            _ <- inputMolecule(Set(date1, date2), Set(date2)).get === List(date3)
            _ <- inputMolecule(Set(date1, date2), Set(date3)).get === Nil
            _ <- inputMolecule(Set(date1, date2), Set(date4)).get === Nil
            _ <- inputMolecule(Set(date1, date2), Set(date5)).get === List(date2)

            _ <- inputMolecule(Set(date1, date2), Set(date2, date3)).get === List(date3)
            _ <- inputMolecule(Set(date1, date2), Set(date4, date5)).get === List(date2)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))
            _ <- inputMolecule(List(Set[Date]())).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))

            // (date3, date4), (date4, date5), (date4, date5, date6)
            _ <- inputMolecule(List(Set(date2))).get.map(_.sorted ==> List(date2, date3, date4, date5))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))
            _ <- inputMolecule(List(Set[Date]())).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))

            // (date2, date4), (date3, date4), (date4, date5), (date4, date5, date6)
            _ <- inputMolecule(List(Set(date2))).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))
            _ <- inputMolecule(List(Set[Date]())).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))

            _ <- inputMolecule(List(Set(date2))).get === List(date1)

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.date.dates_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))
            _ <- inputMolecule(List(Set[Date]())).get.map(_.sorted ==> List(date1, date2, date3, date4, date5))

            _ <- inputMolecule(List(Set(date2))).get.map(_.sorted ==> List(date1, date2))

            //(inputMolecule(List(Set(date2, date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(date2), Set(date3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }
  }
}