package molecule.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}


object Input1BigDecimal extends AsyncTestSuite {

  def oneData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.str.bigDec$ insert List(
      (str1, Some(bigDec1)),
      (str2, Some(bigDec2)),
      (str3, Some(bigDec3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
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
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.apply(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === Nil

            _ <- inputMolecule(List(bigDec1)).get === List(bigDec1)
            _ <- inputMolecule(List(bigDec1, bigDec1)).get === List(bigDec1)
            _ <- inputMolecule(List(bigDec1, bigDec2)).get.map(_.sorted ==> List(bigDec1, bigDec2))

            // Varargs
            _ <- inputMolecule(bigDec1).get === List(bigDec1)
            _ <- inputMolecule(bigDec1, bigDec2).get.map(_.sorted ==> List(bigDec1, bigDec2))

            // `or`
            _ <- inputMolecule(bigDec1 or bigDec2).get.map(_.sorted ==> List(bigDec1, bigDec2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))

            _ <- inputMolecule(List(bigDec1)).get.map(_.sorted ==> List(bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec1, bigDec1)).get.map(_.sorted ==> List(bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec1, bigDec2)).get.map(_.sorted ==> List(bigDec3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2)).get.map(_.sorted ==> List(bigDec3))
            //(inputMolecule(List(bigDec2, bigDec3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2)).get.map(_.sorted ==> List(bigDec2, bigDec3))
            //(inputMolecule(List(bigDec2, bigDec3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2)).get === List(bigDec1)
            //(inputMolecule(List(bigDec2, bigDec3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(bigDec2)).get.map(_.sorted ==> List(bigDec1, bigDec2))
            //(inputMolecule(List(bigDec2, bigDec3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigDec_(?))
          for {
            _ <- inputMolecule(Nil).get === List(str4)
            _ <- inputMolecule(List(bigDec1)).get === List(str1)
            _ <- inputMolecule(List(bigDec1, bigDec1)).get === List(str1)
            _ <- inputMolecule(List(bigDec1, bigDec2)).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigDec_.not(?))
          for {

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(bigDec1, bigDec1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(bigDec1, bigDec2)).get.map(_.sorted ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigDec_.>(?))
          for {

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec2)).get.map(_.sorted ==> List(str3))
            //(inputMolecule(List(bigDec2, bigDec3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigDec_.>=(?))
          for {

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec2)).get.map(_.sorted ==> List(str2, str3))
            //(inputMolecule(List(bigDec2, bigDec3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigDec_.<(?))
          for {

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec2)).get === List(str1)
            //(inputMolecule(List(bigDec2, bigDec3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.bigDec_.<=(?))
          for {

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(bigDec2)).get.map(_.sorted ==> List(str1, str2))
            //(inputMolecule(List(bigDec2, bigDec3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }

    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[BigDecimal]())).get === Nil


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigDec1))).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec1, bigDec1))).get === List((bigDec1, Set(bigDec1, bigDec2)))

            _ <- inputMolecule(List(Set(bigDec1, bigDec2))).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec1, bigDec3))).get === Nil
            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get === List((bigDec2, Set(bigDec3, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec4, bigDec5))).get === List((bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))

            // 1 arg
            _ <- inputMolecule(Set(bigDec1)).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(Set(bigDec1, bigDec1)).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(Set(bigDec1, bigDec2)).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(Set(bigDec1, bigDec3)).get === Nil
            _ <- inputMolecule(Set(bigDec2, bigDec3)).get === List((bigDec2, Set(bigDec3, bigDec2)))
            _ <- inputMolecule(Set(bigDec4, bigDec5)).get === List((bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set[BigDecimal]())).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec1))).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2))).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec3))).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3))).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2, bigDec3))).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2), Set(bigDec3))).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4))).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))


            // Multiple varargs
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set[BigDecimal]()).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec1)).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec2)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec3)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec2, bigDec3)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec2), Set(bigDec3)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))

            // `or`
            _ <- inputMolecule(Set(bigDec1, bigDec2) or Set[BigDecimal]()).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec1)).get === List((bigDec1, Set(bigDec1, bigDec2)))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec2)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec3)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(Set(bigDec1, bigDec2) or Set(bigDec3)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec2, bigDec3)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)))
            _ <- inputMolecule(Set(bigDec1) or Set(bigDec2) or Set(bigDec3)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
            _ <- inputMolecule(Set(bigDec1, bigDec2) or Set(bigDec3, bigDec4)).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs.not(?)) // or m(Ns.bigDec.bigDecs.!=(?))
          val all           = List(
            (bigDec1, Set(bigDec1, bigDec2, bigDec3)),
            (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
            (bigDec3, Set(bigDec3, bigDec4, bigDec5))
          )

          for {
            _ <- manyData
            _ <- Ns.bigDec.bigDecs insert all

            _ <- inputMolecule(Nil).get === all
            _ <- inputMolecule(Set[BigDecimal]()).get === all

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // _ <- inputMolecule(bigDec1).get === ...
            // _ <- inputMolecule(List(bigDec1)).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(bigDec1)).get === List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 match
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )
            // Same as
            _ <- inputMolecule(List(Set(bigDec1))).get === List(
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )

            _ <- inputMolecule(Set(bigDec2)).get === List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec2 match
              // (bigDec2, Set(bigDec2, bigDec3, bigDec4)),  // bigDec2 match
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )

            _ <- inputMolecule(Set(bigDec3)).get === Nil // bigDec3 match all


            _ <- inputMolecule(Set(bigDec1), Set(bigDec2)).get === List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 match, bigDec2 match
              // (bigDec2, Set(bigDec2, bigDec3, bigDec4)),  // bigDec2 match
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigDec1, bigDec2)).get === List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 AND bigDec2 match
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )


            _ <- inputMolecule(Set(bigDec1), Set(bigDec3)).get === Nil // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec3)).get === List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 AND bigDec3 match
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )


            _ <- inputMolecule(Set(bigDec1), Set(bigDec2), Set(bigDec3)).get === Nil // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec2, bigDec3)).get === List(
              // (bigDec1, Set(bigDec1, bigDec2, bigDec3)),  // bigDec1 AND bigDec2 AND bigDec3 match
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )


            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec1)).get === List(
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2)).get === List(
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3)).get === Nil
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4)).get === Nil
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec5)).get === List(
              (bigDec2, Set(bigDec2, bigDec3, bigDec4))
            )

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get === List(
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4, bigDec5)).get === List(
              (bigDec2, Set(bigDec2, bigDec3, bigDec4))
            )
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))

            // (bigDec3, bigDec4), (bigDec4, bigDec5), (bigDec4, bigDec5, bigDec6)
            _ <- inputMolecule(List(Set(bigDec2))).get === List((bigDec2, Set(bigDec3)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))

            // (bigDec2, bigDec4), (bigDec3, bigDec4), (bigDec4, bigDec5), (bigDec4, bigDec5, bigDec6)
            _ <- inputMolecule(List(Set(bigDec2))).get === List((bigDec1, Set(bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))

            _ <- inputMolecule(List(Set(bigDec2))).get === List((bigDec1, Set(bigDec1)))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec3, bigDec2)), (bigDec3, Set(bigDec4, bigDec3)), (bigDec4, Set(bigDec4, bigDec5)), (bigDec5, Set(bigDec4, bigDec6, bigDec5)))

            _ <- inputMolecule(List(Set(bigDec2))).get === List((bigDec1, Set(bigDec1, bigDec2)), (bigDec2, Set(bigDec2)))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[BigDecimal]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigDec1))).get === List(Set(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec2))).get === List(Set(bigDec1, bigDec2, bigDec3)) // (bigDec1, bigDec2) + (bigDec2, bigDec3)
            _ <- inputMolecule(List(Set(bigDec3))).get === List(Set(bigDec2, bigDec3, bigDec4)) // (bigDec2, bigDec3) + (bigDec3, bigDec4)

            _ <- inputMolecule(List(Set(bigDec1, bigDec2))).get === List(Set(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec1, bigDec3))).get === Nil
            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get === List(Set(bigDec2, bigDec3))
            _ <- inputMolecule(List(Set(bigDec4, bigDec5))).get === List(Set(bigDec4, bigDec5, bigDec6)) // (bigDec4, bigDec5) + (bigDec4, bigDec5, bigDec6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec1))).get === List(Set(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2))).get === List(Set(bigDec1, bigDec2, bigDec3)) // (bigDec1, bigDec2) + (bigDec2, bigDec3)
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec3))).get === List(Set(bigDec1, bigDec4, bigDec3, bigDec2)) // (bigDec1, bigDec2) + (bigDec2, bigDec3) + (bigDec3, bigDec4)
            _ <- inputMolecule(List(Set(bigDec2), Set(bigDec3))).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4)) // (bigDec1, bigDec2) + (bigDec2, bigDec3) + (bigDec3, bigDec4)

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3))).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4)) // (bigDec1, bigDec2) + (bigDec2, bigDec3) + (bigDec3, bigDec4)
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2, bigDec3))).get === List(Set(bigDec1, bigDec3, bigDec2)) // (bigDec1, bigDec2) + (bigDec2, bigDec3)
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2), Set(bigDec3))).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4)) // (bigDec1, bigDec2) + (bigDec2, bigDec3) + (bigDec3, bigDec4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.not(?)) // or m(Ns.bigDec.bigDecs.!=(?))
          for {
            _ <- manyData
            _ <- Ns.bigDec.bigDecs insert List(
              (bigDec1, Set(bigDec1, bigDec2, bigDec3)),
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )

            _ <- inputMolecule(Nil).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(Set[BigDecimal]()).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // _ <- inputMolecule(bigDec1).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(bigDec1)).get === List(Set(bigDec2, bigDec3, bigDec4, bigDec5))
            // Same as
            _ <- inputMolecule(List(Set(bigDec1))).get === List(Set(bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(Set(bigDec2)).get === List(Set(bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(Set(bigDec3)).get === Nil // bigDec3 match all

            _ <- inputMolecule(Set(bigDec1), Set(bigDec2)).get === List(Set(bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(Set(bigDec1), Set(bigDec3)).get === Nil // bigDec3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigDec1, bigDec2)).get === List(Set(bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(Set(bigDec1, bigDec3)).get === List(Set(bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(Set(bigDec1), Set(bigDec2), Set(bigDec3)).get === Nil // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec2, bigDec3)).get === List(Set(bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec1)).get === List(Set(bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2)).get === List(Set(bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3)).get === Nil
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4)).get === Nil
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec5)).get === List(Set(bigDec2, bigDec3, bigDec4))

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get === List(Set(bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4, bigDec5)).get === List(Set(bigDec2, bigDec3, bigDec4))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))

            _ <- inputMolecule(List(Set(bigDec2))).get === List(Set(bigDec3, bigDec4, bigDec5, bigDec6))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))

            _ <- inputMolecule(List(Set(bigDec2))).get === List(Set(bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))

            _ <- inputMolecule(List(Set(bigDec2))).get === List(Set(bigDec1))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDecs.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List(Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6))

            _ <- inputMolecule(List(Set(bigDec2))).get === List(Set(bigDec1, bigDec2))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(bigDec6)
            _ <- inputMolecule(List(Set[BigDecimal]())).get === List(bigDec6)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(bigDec1))).get === List(bigDec1)
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_.sorted ==> List(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec3))).get.map(_.sorted ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(List(Set(bigDec1, bigDec1))).get === List(bigDec1)
            _ <- inputMolecule(List(Set(bigDec1, bigDec2))).get === List(bigDec1)
            _ <- inputMolecule(List(Set(bigDec1, bigDec3))).get === Nil
            _ <- inputMolecule(List(Set(bigDec2, bigDec3))).get === List(bigDec2)
            _ <- inputMolecule(List(Set(bigDec4, bigDec5))).get.map(_.sorted ==> List(bigDec4, bigDec5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set[BigDecimal]())).get === List(bigDec1)
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec1))).get === List(bigDec1)
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2))).get.map(_.sorted ==> List(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec3))).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3))).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2, bigDec3))).get.map(_.sorted ==> List(bigDec1, bigDec2))
            _ <- inputMolecule(List(Set(bigDec1), Set(bigDec2), Set(bigDec3))).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))

            _ <- inputMolecule(List(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4))).get.map(_.sorted ==> List(bigDec1, bigDec3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs_.not(?)) // or m(Ns.bigDec.bigDecs.!=(?))
          for {
            _ <- manyData
            _ <- Ns.bigDec.bigDecs insert List(
              (bigDec1, Set(bigDec1, bigDec2, bigDec3)),
              (bigDec2, Set(bigDec2, bigDec3, bigDec4)),
              (bigDec3, Set(bigDec3, bigDec4, bigDec5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))
            _ <- inputMolecule(Set[BigDecimal]()).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // _ <- inputMolecule(bigDec1).get === ...
            // _ <- inputMolecule(List(bigDec1)).get === ...

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(bigDec1)).get.map(_.sorted ==> List(bigDec2, bigDec3))
            // Same as
            _ <- inputMolecule(List(Set(bigDec1))).get.map(_.sorted ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(Set(bigDec2)).get === List(bigDec3)
            _ <- inputMolecule(Set(bigDec3)).get === Nil // bigDec3 match all


            _ <- inputMolecule(Set(bigDec1), Set(bigDec2)).get === List(bigDec3)
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(bigDec1, bigDec2)).get.map(_.sorted ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(Set(bigDec1), Set(bigDec3)).get === Nil // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec3)).get.map(_.sorted ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(Set(bigDec1), Set(bigDec2), Set(bigDec3)).get === Nil // bigDec3 match all
            _ <- inputMolecule(Set(bigDec1, bigDec2, bigDec3)).get.map(_.sorted ==> List(bigDec2, bigDec3))

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec1)).get.map(_.sorted ==> List(bigDec2, bigDec3))
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2)).get === List(bigDec3)
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec3)).get === Nil
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4)).get === Nil
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec5)).get === List(bigDec2)

            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get === List(bigDec3)
            _ <- inputMolecule(Set(bigDec1, bigDec2), Set(bigDec4, bigDec5)).get === List(bigDec2)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            // (bigDec3, bigDec4), (bigDec4, bigDec5), (bigDec4, bigDec5, bigDec6)
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_.sorted ==> List(bigDec2, bigDec3, bigDec4, bigDec5))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            // (bigDec2, bigDec4), (bigDec3, bigDec4), (bigDec4, bigDec5), (bigDec4, bigDec5, bigDec6)
            _ <- inputMolecule(List(Set(bigDec2))).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(List(Set(bigDec2))).get === List(bigDec1)

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.bigDec.bigDecs_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))
            _ <- inputMolecule(List(Set[BigDecimal]())).get.map(_.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

            _ <- inputMolecule(List(Set(bigDec2))).get.map(_.sorted ==> List(bigDec1, bigDec2))

            //(inputMolecule(List(Set(bigDec2, bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(bigDec2), Set(bigDec3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }
  }
}