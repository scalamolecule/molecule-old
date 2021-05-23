package molecule.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}


object Input1BigInt extends AsyncTestSuite {

  def oneData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.str.bigInt$ insert List(
      (str1, Some(bigInt1)),
      (str2, Some(bigInt2)),
      (str3, Some(bigInt3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
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
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === Nil

            _ <- inputMolecule(List(bigInt1)).get === List(bigInt1)
            _ <- inputMolecule(List(bigInt1, bigInt1)).get === List(bigInt1)
            _ <- inputMolecule(List(bigInt1, bigInt2)).get.map(_.sorted ==> List(bigInt1, bigInt2))

            // Varargs
            _ <- inputMolecule(bigInt1).get === List(bigInt1)
            _ <- inputMolecule(bigInt1, bigInt2).get.map(_.sorted ==> List(bigInt1, bigInt2))

            // `or`
            _ <- inputMolecule(bigInt1 or bigInt2).get.map(_.sorted ==> List(bigInt1, bigInt2))
          } yield ()
        }

        "!=" - core { implicit conn =>
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
            //(inputMolecule(List(bigInt2, bigInt3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(bigInt2, bigInt3))
            //(inputMolecule(List(bigInt2, bigInt3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt2)).get === List(bigInt1)
            //(inputMolecule(List(bigInt2, bigInt3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(bigInt1, bigInt2))
            //(inputMolecule(List(bigInt2, bigInt3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str4)
            _ <- inputMolecule(List(bigInt1)).get === List(str1)
            _ <- inputMolecule(List(bigInt1, bigInt1)).get === List(str1)
            _ <- inputMolecule(List(bigInt1, bigInt2)).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
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
            //(inputMolecule(List(bigInt2, bigInt3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(str2, str3))
            //(inputMolecule(List(bigInt2, bigInt3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigInt2)).get === List(str1)
            //(inputMolecule(List(bigInt2, bigInt3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigInt_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigInt2)).get.map(_.sorted ==> List(str1, str2))
            //(inputMolecule(List(bigInt2, bigInt3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
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
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[BigInt]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigInt1))).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt1, bigInt1))).get === List((bigInt1, Set(bigInt1, bigInt2)))

            _ <- inputMolecule(List(Set(bigInt1, bigInt2))).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt1, bigInt3))).get === Nil
            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get === List((bigInt2, Set(bigInt3, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt4, bigInt5))).get === List((bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))

            // 1 arg
            _ <- inputMolecule(Set(bigInt1)).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(Set(bigInt1, bigInt1)).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(Set(bigInt1, bigInt2)).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(Set(bigInt1, bigInt3)).get === Nil
            _ <- inputMolecule(Set(bigInt2, bigInt3)).get === List((bigInt2, Set(bigInt3, bigInt2)))
            _ <- inputMolecule(Set(bigInt4, bigInt5)).get === List((bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set[BigInt]())).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt1))).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2))).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt3))).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3))).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2, bigInt3))).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2), Set(bigInt3))).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4))).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))


            // Multiple varargs
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set[BigInt]()).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt1)).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt2)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt3)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt2, bigInt3)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt2), Set(bigInt3)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))

            // `or`
            _ <- inputMolecule(Set(bigInt1, bigInt2) or Set[BigInt]()).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt1)).get === List((bigInt1, Set(bigInt1, bigInt2)))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt2)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt3)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(Set(bigInt1, bigInt2) or Set(bigInt3)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt2, bigInt3)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)))
            _ <- inputMolecule(Set(bigInt1) or Set(bigInt2) or Set(bigInt3)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
            _ <- inputMolecule(Set(bigInt1, bigInt2) or Set(bigInt3, bigInt4)).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.not(?)) // or m(Ns.bigInt.bigInts.!=(?))
          val all           = List(
            (bigInt1, Set(bigInt1, bigInt2, bigInt3)),
            (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
            (bigInt3, Set(bigInt3, bigInt4, bigInt5))
          )
          for {
            _ <- manyData
            _ <- Ns.bigInt.bigInts insert all

            _ <- inputMolecule(Nil).get === all
            _ <- inputMolecule(Set[BigInt]()).get === all

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(bigInt1).get === ...
            // inputMolecule(List(bigInt1)).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(bigInt1)).get === List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 match
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )
            // Same as
            _ <- inputMolecule(List(Set(bigInt1))).get === List(
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )

            _ <- inputMolecule(Set(bigInt2)).get === List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt2 match
              // (bigInt2, Set(bigInt2, bigInt3, bigInt4)),  // bigInt2 match
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )

            _ <- inputMolecule(Set(bigInt3)).get === Nil // bigInt3 match all


            _ <- inputMolecule(Set(bigInt1), Set(bigInt2)).get === List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 match, bigInt2 match
              // (bigInt2, Set(bigInt2, bigInt3, bigInt4)),  // bigInt2 match
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigInt1, bigInt2)).get === List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 AND bigInt2 match
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )


            _ <- inputMolecule(Set(bigInt1), Set(bigInt3)).get === Nil // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt3)).get === List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 AND bigInt3 match
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )


            _ <- inputMolecule(Set(bigInt1), Set(bigInt2), Set(bigInt3)).get === Nil // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt2, bigInt3)).get === List(
              // (bigInt1, Set(bigInt1, bigInt2, bigInt3)),  // bigInt1 AND bigInt2 AND bigInt3 match
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )


            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt1)).get === List(
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2)).get === List(
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3)).get === Nil
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4)).get === Nil
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt5)).get === List(
              (bigInt2, Set(bigInt2, bigInt3, bigInt4))
            )

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get === List(
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4, bigInt5)).get === List(
              (bigInt2, Set(bigInt2, bigInt3, bigInt4))
            )
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))
            _ <- inputMolecule(List(Set[BigInt]())).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))

            // (bigInt3, bigInt4), (bigInt4, bigInt5), (bigInt4, bigInt5, bigInt6)
            _ <- inputMolecule(List(Set(bigInt2))).get === List((bigInt2, Set(bigInt3)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))
            _ <- inputMolecule(List(Set[BigInt]())).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))

            // (bigInt2, bigInt4), (bigInt3, bigInt4), (bigInt4, bigInt5), (bigInt4, bigInt5, bigInt6)
            _ <- inputMolecule(List(Set(bigInt2))).get === List((bigInt1, Set(bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))
            _ <- inputMolecule(List(Set[BigInt]())).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))

            _ <- inputMolecule(List(Set(bigInt2))).get === List((bigInt1, Set(bigInt1)))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))
            _ <- inputMolecule(List(Set[BigInt]())).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt3, bigInt2)), (bigInt3, Set(bigInt4, bigInt3)), (bigInt4, Set(bigInt4, bigInt5)), (bigInt5, Set(bigInt4, bigInt6, bigInt5)))

            _ <- inputMolecule(List(Set(bigInt2))).get === List((bigInt1, Set(bigInt1, bigInt2)), (bigInt2, Set(bigInt2)))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[BigInt]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigInt1))).get === List(Set(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt2))).get === List(Set(bigInt1, bigInt2, bigInt3)) // (bigInt1, bigInt2) + (bigInt2, bigInt3)
            _ <- inputMolecule(List(Set(bigInt3))).get === List(Set(bigInt2, bigInt3, bigInt4)) // (bigInt2, bigInt3) + (bigInt3, bigInt4)

            _ <- inputMolecule(List(Set(bigInt1, bigInt2))).get === List(Set(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt1, bigInt3))).get === Nil
            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get === List(Set(bigInt2, bigInt3))
            _ <- inputMolecule(List(Set(bigInt4, bigInt5))).get === List(Set(bigInt4, bigInt5, bigInt6)) // (bigInt4, bigInt5) + (bigInt4, bigInt5, bigInt6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt1))).get === List(Set(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2))).get === List(Set(bigInt1, bigInt2, bigInt3)) // (bigInt1, bigInt2) + (bigInt2, bigInt3)
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt3))).get === List(Set(bigInt1, bigInt4, bigInt3, bigInt2)) // (bigInt1, bigInt2) + (bigInt2, bigInt3) + (bigInt3, bigInt4)
            _ <- inputMolecule(List(Set(bigInt2), Set(bigInt3))).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4)) // (bigInt1, bigInt2) + (bigInt2, bigInt3) + (bigInt3, bigInt4)

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3))).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4)) // (bigInt1, bigInt2) + (bigInt2, bigInt3) + (bigInt3, bigInt4)
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2, bigInt3))).get === List(Set(bigInt1, bigInt3, bigInt2)) // (bigInt1, bigInt2) + (bigInt2, bigInt3)
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2), Set(bigInt3))).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4)) // (bigInt1, bigInt2) + (bigInt2, bigInt3) + (bigInt3, bigInt4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.not(?)) // or m(Ns.bigInt.bigInts.!=(?))
          for {
            _ <- manyData
            _ <- Ns.bigInt.bigInts insert List(
              (bigInt1, Set(bigInt1, bigInt2, bigInt3)),
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )

            _ <- inputMolecule(Nil).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(Set[BigInt]()).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(bigInt1).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(bigInt1)).get === List(Set(bigInt2, bigInt3, bigInt4, bigInt5))
            // Same as
            _ <- inputMolecule(List(Set(bigInt1))).get === List(Set(bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(Set(bigInt2)).get === List(Set(bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(Set(bigInt3)).get === Nil // bigInt3 match all

            _ <- inputMolecule(Set(bigInt1), Set(bigInt2)).get === List(Set(bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(Set(bigInt1), Set(bigInt3)).get === Nil // bigInt3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigInt1, bigInt2)).get === List(Set(bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(Set(bigInt1, bigInt3)).get === List(Set(bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(Set(bigInt1), Set(bigInt2), Set(bigInt3)).get === Nil // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt2, bigInt3)).get === List(Set(bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt1)).get === List(Set(bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2)).get === List(Set(bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3)).get === Nil
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4)).get === Nil
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt5)).get === List(Set(bigInt2, bigInt3, bigInt4))

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get === List(Set(bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4, bigInt5)).get === List(Set(bigInt2, bigInt3, bigInt4))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))
            _ <- inputMolecule(List(Set[BigInt]())).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))

            _ <- inputMolecule(List(Set(bigInt2))).get === List(Set(bigInt3, bigInt4, bigInt5, bigInt6))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))
            _ <- inputMolecule(List(Set[BigInt]())).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))

            _ <- inputMolecule(List(Set(bigInt2))).get === List(Set(bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))
            _ <- inputMolecule(List(Set[BigInt]())).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))

            _ <- inputMolecule(List(Set(bigInt2))).get === List(Set(bigInt1))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInts.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))
            _ <- inputMolecule(List(Set[BigInt]())).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6))

            _ <- inputMolecule(List(Set(bigInt2))).get === List(Set(bigInt1, bigInt2))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(bigInt6)
            _ <- inputMolecule(List(Set[BigInt]())).get === List(bigInt6)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigInt1))).get === List(bigInt1)
            _ <- inputMolecule(List(Set(bigInt2))).get.map(_.sorted ==> List(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt3))).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(List(Set(bigInt1, bigInt1))).get === List(bigInt1)
            _ <- inputMolecule(List(Set(bigInt1, bigInt2))).get === List(bigInt1)
            _ <- inputMolecule(List(Set(bigInt1, bigInt3))).get === Nil
            _ <- inputMolecule(List(Set(bigInt2, bigInt3))).get === List(bigInt2)
            _ <- inputMolecule(List(Set(bigInt4, bigInt5))).get.map(_.sorted ==> List(bigInt4, bigInt5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set[BigInt]())).get === List(bigInt1)
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt1))).get === List(bigInt1)
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2))).get.map(_.sorted ==> List(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt3))).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3))).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2, bigInt3))).get.map(_.sorted ==> List(bigInt1, bigInt2))
            _ <- inputMolecule(List(Set(bigInt1), Set(bigInt2), Set(bigInt3))).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))

            _ <- inputMolecule(List(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4))).get.map(_.sorted ==> List(bigInt1, bigInt3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_.not(?)) // or m(Ns.bigInt.bigInts.!=(?))
          for {
            _ <- manyData
            _ <- Ns.bigInt.bigInts insert List(
              (bigInt1, Set(bigInt1, bigInt2, bigInt3)),
              (bigInt2, Set(bigInt2, bigInt3, bigInt4)),
              (bigInt3, Set(bigInt3, bigInt4, bigInt5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))
            _ <- inputMolecule(Set[BigInt]()).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(bigInt1).get === ...
            // inputMolecule(List(bigInt1)).get === ...

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(bigInt1)).get.map(_.sorted ==> List(bigInt2, bigInt3))
            // Same as
            _ <- inputMolecule(List(Set(bigInt1))).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(Set(bigInt2)).get === List(bigInt3)
            _ <- inputMolecule(Set(bigInt3)).get === Nil // bigInt3 match all


            _ <- inputMolecule(Set(bigInt1), Set(bigInt2)).get === List(bigInt3)
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigInt1, bigInt2)).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(Set(bigInt1), Set(bigInt3)).get === Nil // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt3)).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(Set(bigInt1), Set(bigInt2), Set(bigInt3)).get === Nil // bigInt3 match all
            _ <- inputMolecule(Set(bigInt1, bigInt2, bigInt3)).get.map(_.sorted ==> List(bigInt2, bigInt3))

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt1)).get.map(_.sorted ==> List(bigInt2, bigInt3))
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2)).get === List(bigInt3)
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt3)).get === Nil
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4)).get === Nil
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt5)).get === List(bigInt2)

            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get === List(bigInt3)
            _ <- inputMolecule(Set(bigInt1, bigInt2), Set(bigInt4, bigInt5)).get === List(bigInt2)
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

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
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

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(List(Set(bigInt2))).get === List(bigInt1)

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigInt.bigInts_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))
            _ <- inputMolecule(List(Set[BigInt]())).get.map(_.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

            _ <- inputMolecule(List(Set(bigInt2))).get.map(_.sorted ==> List(bigInt1, bigInt2))

            //(inputMolecule(List(Set(bigInt2, bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigInt2), Set(bigInt3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }
  }
}