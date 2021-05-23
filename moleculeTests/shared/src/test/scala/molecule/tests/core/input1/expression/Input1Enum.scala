package molecule.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}


object Input1Enum extends AsyncTestSuite {

  def oneData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.str.enum$ insert List(
      (str1, Some(enum1)),
      (str2, Some(enum2)),
      (str3, Some(enum3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.enum.enums$ insert List(
      (enum1, Some(Set(enum1, enum2))),
      (enum2, Some(Set(enum2, enum3))),
      (enum3, Some(Set(enum3, enum4))),
      (enum4, Some(Set(enum4, enum5))),
      (enum5, Some(Set(enum4, enum5, enum6))),
      (enum6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.enum(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === Nil

            _ <- inputMolecule(List(enum1)).get === List(enum1)
            _ <- inputMolecule(List(enum1, enum1)).get === List(enum1)
            _ <- inputMolecule(List(enum1, enum2)).get.map(_.sorted ==> List(enum1, enum2))

            // Varargs
            _ <- inputMolecule(enum1).get === List(enum1)
            _ <- inputMolecule(enum1, enum2).get.map(_.sorted ==> List(enum1, enum2))

            // `or`
            _ <- inputMolecule(enum1 or enum2).get.map(_.sorted ==> List(enum1, enum2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))

            _ <- inputMolecule(List(enum1)).get.map(_.sorted ==> List(enum2, enum3))
            _ <- inputMolecule(List(enum1, enum1)).get.map(_.sorted ==> List(enum2, enum3))
            _ <- inputMolecule(List(enum1, enum2)).get.map(_.sorted ==> List(enum3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
            _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum3))
            //(inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
            _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum2, enum3))
            //(inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
            _ <- inputMolecule(List(enum2)).get === List(enum1)
            //(inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
            _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(enum1, enum2))
            //(inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.enum_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str4)
            _ <- inputMolecule(List(enum1)).get === List(str1)
            _ <- inputMolecule(List(enum1, enum1)).get === List(str1)
            _ <- inputMolecule(List(enum1, enum2)).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.enum_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(enum1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(enum1, enum1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(enum1, enum2)).get.map(_.sorted ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.enum_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(str3))
            //(inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.enum_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(str2, str3))
            //(inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.enum_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(enum2)).get === List(str1)
            //(inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.enum_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(enum2)).get.map(_.sorted ==> List(str1, str2))
            //(inputMolecule(List(enum2, enum3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }

    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[String]())).get === Nil


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(enum1))).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(List(Set(enum1, enum1))).get === List((enum1, Set(enum1, enum2)))

            _ <- inputMolecule(List(Set(enum1, enum2))).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(List(Set(enum1, enum3))).get === Nil
            _ <- inputMolecule(List(Set(enum2, enum3))).get === List((enum2, Set(enum3, enum2)))
            _ <- inputMolecule(List(Set(enum4, enum5))).get === List((enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))

            // 1 arg
            _ <- inputMolecule(Set(enum1)).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(Set(enum1, enum1)).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(Set(enum1, enum2)).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(Set(enum1, enum3)).get === Nil
            _ <- inputMolecule(Set(enum2, enum3)).get === List((enum2, Set(enum3, enum2)))
            _ <- inputMolecule(Set(enum4, enum5)).get === List((enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(enum1, enum2), Set[String]())).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(List(Set(enum1), Set(enum1))).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(List(Set(enum1), Set(enum2))).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)))
            _ <- inputMolecule(List(Set(enum1), Set(enum3))).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(List(Set(enum1), Set(enum2, enum3))).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)))
            _ <- inputMolecule(List(Set(enum1), Set(enum2), Set(enum3))).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3, enum4))).get === List((enum1, Set(enum1, enum2)), (enum3, Set(enum4, enum3)))


            // Multiple varargs
            _ <- inputMolecule(Set(enum1, enum2), Set[String]()).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(Set(enum1), Set(enum1)).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(Set(enum1), Set(enum2)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)))
            _ <- inputMolecule(Set(enum1), Set(enum3)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(Set(enum1, enum2), Set(enum3)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(Set(enum1), Set(enum2, enum3)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)))
            _ <- inputMolecule(Set(enum1), Set(enum2), Set(enum3)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(Set(enum1, enum2), Set(enum3, enum4)).get === List((enum1, Set(enum1, enum2)), (enum3, Set(enum4, enum3)))

            // `or`
            _ <- inputMolecule(Set(enum1, enum2) or Set[String]()).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(Set(enum1) or Set(enum1)).get === List((enum1, Set(enum1, enum2)))
            _ <- inputMolecule(Set(enum1) or Set(enum2)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)))
            _ <- inputMolecule(Set(enum1) or Set(enum3)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(Set(enum1, enum2) or Set(enum3)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(Set(enum1) or Set(enum2, enum3)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)))
            _ <- inputMolecule(Set(enum1) or Set(enum2) or Set(enum3)).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)))
            _ <- inputMolecule(Set(enum1, enum2) or Set(enum3, enum4)).get === List((enum1, Set(enum1, enum2)), (enum3, Set(enum4, enum3)))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums.not(?)) // or m(Ns.enum.enums.!=(?))
          val all           = List(
            (enum1, Set(enum1, enum2, enum3)),
            (enum2, Set(enum2, enum3, enum4)),
            (enum3, Set(enum3, enum4, enum5))
          )
          for {
            _ <- manyData
            _ <- Ns.enum.enums insert all

            _ <- inputMolecule(Nil).get === all
            _ <- inputMolecule(Set[String]()).get === all

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(enum1).get === ...
            // inputMolecule(List(enum1)).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(enum1)).get === List(
              // (enum1, Set(enum1, enum2, enum3)),  // enum1 match
              (enum2, Set(enum2, enum3, enum4)),
              (enum3, Set(enum3, enum4, enum5))
            )
            // Same as
            _ <- inputMolecule(List(Set(enum1))).get === List(
              (enum2, Set(enum2, enum3, enum4)),
              (enum3, Set(enum3, enum4, enum5))
            )

            _ <- inputMolecule(Set(enum2)).get === List(
              // (enum1, Set(enum1, enum2, enum3)),  // enum2 match
              // (enum2, Set(enum2, enum3, enum4)),  // enum2 match
              (enum3, Set(enum3, enum4, enum5))
            )

            _ <- inputMolecule(Set(enum3)).get === Nil // enum3 match all


            _ <- inputMolecule(Set(enum1), Set(enum2)).get === List(
              // (enum1, Set(enum1, enum2, enum3)),  // enum1 match, enum2 match
              // (enum2, Set(enum2, enum3, enum4)),  // enum2 match
              (enum3, Set(enum3, enum4, enum5))
            )
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(enum1, enum2)).get === List(
              // (enum1, Set(enum1, enum2, enum3)),  // enum1 AND enum2 match
              (enum2, Set(enum2, enum3, enum4)),
              (enum3, Set(enum3, enum4, enum5))
            )


            _ <- inputMolecule(Set(enum1), Set(enum3)).get === Nil // enum3 match all
            _ <- inputMolecule(Set(enum1, enum3)).get === List(
              // (enum1, Set(enum1, enum2, enum3)),  // enum1 AND enum3 match
              (enum2, Set(enum2, enum3, enum4)),
              (enum3, Set(enum3, enum4, enum5))
            )


            _ <- inputMolecule(Set(enum1), Set(enum2), Set(enum3)).get === Nil // enum3 match all
            _ <- inputMolecule(Set(enum1, enum2, enum3)).get === List(
              // (enum1, Set(enum1, enum2, enum3)),  // enum1 AND enum2 AND enum3 match
              (enum2, Set(enum2, enum3, enum4)),
              (enum3, Set(enum3, enum4, enum5))
            )


            _ <- inputMolecule(Set(enum1, enum2), Set(enum1)).get === List(
              (enum2, Set(enum2, enum3, enum4)),
              (enum3, Set(enum3, enum4, enum5))
            )
            _ <- inputMolecule(Set(enum1, enum2), Set(enum2)).get === List(
              (enum3, Set(enum3, enum4, enum5))
            )
            _ <- inputMolecule(Set(enum1, enum2), Set(enum3)).get === Nil
            _ <- inputMolecule(Set(enum1, enum2), Set(enum4)).get === Nil
            _ <- inputMolecule(Set(enum1, enum2), Set(enum5)).get === List(
              (enum2, Set(enum2, enum3, enum4))
            )

            _ <- inputMolecule(Set(enum1, enum2), Set(enum2, enum3)).get === List(
              (enum3, Set(enum3, enum4, enum5))
            )
            _ <- inputMolecule(Set(enum1, enum2), Set(enum4, enum5)).get === List(
              (enum2, Set(enum2, enum3, enum4))
            )
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))
            _ <- inputMolecule(List(Set[String]())).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))

            // (enum3, enum4), (enum4, enum5), (enum4, enum5, enum6)
            _ <- inputMolecule(List(Set(enum2))).get === List((enum2, Set(enum3)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))
            _ <- inputMolecule(List(Set[String]())).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))

            // (enum2, enum4), (enum3, enum4), (enum4, enum5), (enum4, enum5, enum6)
            _ <- inputMolecule(List(Set(enum2))).get === List((enum1, Set(enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))
            _ <- inputMolecule(List(Set[String]())).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))

            _ <- inputMolecule(List(Set(enum2))).get === List((enum1, Set(enum1)))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))
            _ <- inputMolecule(List(Set[String]())).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum3, enum2)), (enum3, Set(enum4, enum3)), (enum4, Set(enum4, enum5)), (enum5, Set(enum4, enum6, enum5)))

            _ <- inputMolecule(List(Set(enum2))).get === List((enum1, Set(enum1, enum2)), (enum2, Set(enum2)))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.enums(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[String]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(enum1))).get === List(Set(enum1, enum2))
            _ <- inputMolecule(List(Set(enum2))).get === List(Set(enum1, enum2, enum3)) // (enum1, enum2) + (enum2, enum3)
            _ <- inputMolecule(List(Set(enum3))).get === List(Set(enum2, enum3, enum4)) // (enum2, enum3) + (enum3, enum4)

            _ <- inputMolecule(List(Set(enum1, enum2))).get === List(Set(enum1, enum2))
            _ <- inputMolecule(List(Set(enum1, enum3))).get === Nil
            _ <- inputMolecule(List(Set(enum2, enum3))).get === List(Set(enum2, enum3))
            _ <- inputMolecule(List(Set(enum4, enum5))).get === List(Set(enum4, enum5, enum6)) // (enum4, enum5) + (enum4, enum5, enum6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(enum1), Set(enum1))).get === List(Set(enum1, enum2))
            _ <- inputMolecule(List(Set(enum1), Set(enum2))).get === List(Set(enum1, enum2, enum3)) // (enum1, enum2) + (enum2, enum3)
            _ <- inputMolecule(List(Set(enum1), Set(enum3))).get === List(Set(enum1, enum4, enum3, enum2)) // (enum1, enum2) + (enum2, enum3) + (enum3, enum4)
            _ <- inputMolecule(List(Set(enum2), Set(enum3))).get === List(Set(enum1, enum2, enum3, enum4)) // (enum1, enum2) + (enum2, enum3) + (enum3, enum4)

            _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get === List(Set(enum1, enum2, enum3, enum4)) // (enum1, enum2) + (enum2, enum3) + (enum3, enum4)
            _ <- inputMolecule(List(Set(enum1), Set(enum2, enum3))).get === List(Set(enum1, enum3, enum2)) // (enum1, enum2) + (enum2, enum3)
            _ <- inputMolecule(List(Set(enum1), Set(enum2), Set(enum3))).get === List(Set(enum1, enum2, enum3, enum4)) // (enum1, enum2) + (enum2, enum3) + (enum3, enum4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.enums.not(?)) // or m(Ns.enum.enums.!=(?))
          for {
            _ <- manyData
            _ <- Ns.enum.enums insert List(
              (enum1, Set(enum1, enum2, enum3)),
              (enum2, Set(enum2, enum3, enum4)),
              (enum3, Set(enum3, enum4, enum5))
            )

            _ <- inputMolecule(Nil).get === List(Set(enum1, enum2, enum3, enum4, enum5))
            _ <- inputMolecule(Set[String]()).get === List(Set(enum1, enum2, enum3, enum4, enum5))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(enum1).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(enum1)).get === List(Set(enum2, enum3, enum4, enum5))
            // Same as
            _ <- inputMolecule(List(Set(enum1))).get === List(Set(enum2, enum3, enum4, enum5))

            _ <- inputMolecule(Set(enum2)).get === List(Set(enum3, enum4, enum5))
            _ <- inputMolecule(Set(enum3)).get === Nil // enum3 match all

            _ <- inputMolecule(Set(enum1), Set(enum2)).get === List(Set(enum3, enum4, enum5))
            _ <- inputMolecule(Set(enum1), Set(enum3)).get === Nil // enum3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(enum1, enum2)).get === List(Set(enum2, enum3, enum4, enum5))
            _ <- inputMolecule(Set(enum1, enum3)).get === List(Set(enum2, enum3, enum4, enum5))

            _ <- inputMolecule(Set(enum1), Set(enum2), Set(enum3)).get === Nil // enum3 match all
            _ <- inputMolecule(Set(enum1, enum2, enum3)).get === List(Set(enum2, enum3, enum4, enum5))

            _ <- inputMolecule(Set(enum1, enum2), Set(enum1)).get === List(Set(enum2, enum3, enum4, enum5))
            _ <- inputMolecule(Set(enum1, enum2), Set(enum2)).get === List(Set(enum3, enum4, enum5))
            _ <- inputMolecule(Set(enum1, enum2), Set(enum3)).get === Nil
            _ <- inputMolecule(Set(enum1, enum2), Set(enum4)).get === Nil
            _ <- inputMolecule(Set(enum1, enum2), Set(enum5)).get === List(Set(enum2, enum3, enum4))

            _ <- inputMolecule(Set(enum1, enum2), Set(enum2, enum3)).get === List(Set(enum3, enum4, enum5))
            _ <- inputMolecule(Set(enum1, enum2), Set(enum4, enum5)).get === List(Set(enum2, enum3, enum4))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.enums.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(enum1, enum2, enum3, enum4, enum5, enum6))
            _ <- inputMolecule(List(Set[String]())).get === List(Set(enum1, enum2, enum3, enum4, enum5, enum6))

            _ <- inputMolecule(List(Set(enum2))).get === List(Set(enum3, enum4, enum5, enum6))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.enums.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(enum1, enum2, enum3, enum4, enum5, enum6))
            _ <- inputMolecule(List(Set[String]())).get === List(Set(enum1, enum2, enum3, enum4, enum5, enum6))

            _ <- inputMolecule(List(Set(enum2))).get === List(Set(enum2, enum3, enum4, enum5, enum6))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.enums.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(enum1, enum2, enum3, enum4, enum5, enum6))
            _ <- inputMolecule(List(Set[String]())).get === List(Set(enum1, enum2, enum3, enum4, enum5, enum6))

            _ <- inputMolecule(List(Set(enum2))).get === List(Set(enum1))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.enums.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(enum1, enum2, enum3, enum4, enum5, enum6))
            _ <- inputMolecule(List(Set[String]())).get === List(Set(enum1, enum2, enum3, enum4, enum5, enum6))

            _ <- inputMolecule(List(Set(enum2))).get === List(Set(enum1, enum2))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(enum6)
            _ <- inputMolecule(List(Set[String]())).get === List(enum6)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(enum1))).get === List(enum1)
            _ <- inputMolecule(List(Set(enum2))).get.map(_.sorted ==> List(enum1, enum2))
            _ <- inputMolecule(List(Set(enum3))).get.map(_.sorted ==> List(enum2, enum3))

            _ <- inputMolecule(List(Set(enum1, enum1))).get === List(enum1)
            _ <- inputMolecule(List(Set(enum1, enum2))).get === List(enum1)
            _ <- inputMolecule(List(Set(enum1, enum3))).get === Nil
            _ <- inputMolecule(List(Set(enum2, enum3))).get === List(enum2)
            _ <- inputMolecule(List(Set(enum4, enum5))).get.map(_.sorted ==> List(enum4, enum5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(enum1, enum2), Set[String]())).get === List(enum1)
            _ <- inputMolecule(List(Set(enum1), Set(enum1))).get === List(enum1)
            _ <- inputMolecule(List(Set(enum1), Set(enum2))).get.map(_.sorted ==> List(enum1, enum2))
            _ <- inputMolecule(List(Set(enum1), Set(enum3))).get.map(_.sorted ==> List(enum1, enum2, enum3))

            _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3))).get.map(_.sorted ==> List(enum1, enum2, enum3))
            _ <- inputMolecule(List(Set(enum1), Set(enum2, enum3))).get.map(_.sorted ==> List(enum1, enum2))
            _ <- inputMolecule(List(Set(enum1), Set(enum2), Set(enum3))).get.map(_.sorted ==> List(enum1, enum2, enum3))

            _ <- inputMolecule(List(Set(enum1, enum2), Set(enum3, enum4))).get.map(_.sorted ==> List(enum1, enum3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums_.not(?)) // or m(Ns.enum.enums.!=(?))
          for {
            _ <- manyData
            _ <- Ns.enum.enums insert List(
              (enum1, Set(enum1, enum2, enum3)),
              (enum2, Set(enum2, enum3, enum4)),
              (enum3, Set(enum3, enum4, enum5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3))
            _ <- inputMolecule(Set[String]()).get.map(_.sorted ==> List(enum1, enum2, enum3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(enum1).get === ...
            // inputMolecule(List(enum1)).get === ...

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(enum1)).get.map(_.sorted ==> List(enum2, enum3))
            // Same as
            _ <- inputMolecule(List(Set(enum1))).get.map(_.sorted ==> List(enum2, enum3))

            _ <- inputMolecule(Set(enum2)).get === List(enum3)
            _ <- inputMolecule(Set(enum3)).get === Nil // enum3 match all


            _ <- inputMolecule(Set(enum1), Set(enum2)).get === List(enum3)
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(enum1, enum2)).get.map(_.sorted ==> List(enum2, enum3))

            _ <- inputMolecule(Set(enum1), Set(enum3)).get === Nil // enum3 match all
            _ <- inputMolecule(Set(enum1, enum3)).get.map(_.sorted ==> List(enum2, enum3))

            _ <- inputMolecule(Set(enum1), Set(enum2), Set(enum3)).get === Nil // enum3 match all
            _ <- inputMolecule(Set(enum1, enum2, enum3)).get.map(_.sorted ==> List(enum2, enum3))

            _ <- inputMolecule(Set(enum1, enum2), Set(enum1)).get.map(_.sorted ==> List(enum2, enum3))
            _ <- inputMolecule(Set(enum1, enum2), Set(enum2)).get === List(enum3)
            _ <- inputMolecule(Set(enum1, enum2), Set(enum3)).get === Nil
            _ <- inputMolecule(Set(enum1, enum2), Set(enum4)).get === Nil
            _ <- inputMolecule(Set(enum1, enum2), Set(enum5)).get === List(enum2)

            _ <- inputMolecule(Set(enum1, enum2), Set(enum2, enum3)).get === List(enum3)
            _ <- inputMolecule(Set(enum1, enum2), Set(enum4, enum5)).get === List(enum2)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
            _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

            // (enum3, enum4), (enum4, enum5), (enum4, enum5, enum6)
            _ <- inputMolecule(List(Set(enum2))).get.map(_.sorted ==> List(enum2, enum3, enum4, enum5))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
            _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

            // (enum2, enum4), (enum3, enum4), (enum4, enum5), (enum4, enum5, enum6)
            _ <- inputMolecule(List(Set(enum2))).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
            _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

            _ <- inputMolecule(List(Set(enum2))).get === List(enum1)

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.enum.enums_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
            _ <- inputMolecule(List(Set[String]())).get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

            _ <- inputMolecule(List(Set(enum2))).get.map(_.sorted ==> List(enum1, enum2))

            //(inputMolecule(List(Set(enum2, enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(enum2), Set(enum3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }
  }
}