package molecule.tests.core.input1.expression

import java.util.UUID
import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}


object Input1UUID extends AsyncTestSuite {

  def oneData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.str.uuid$ insert List(
      (str1, Some(uuid1)),
      (str2, Some(uuid2)),
      (str3, Some(uuid3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
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
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === Nil

            _ <- inputMolecule(List(uuid1)).get === List(uuid1)
            _ <- inputMolecule(List(uuid1, uuid1)).get === List(uuid1)
            _ <- inputMolecule(List(uuid1, uuid2)).get.map(_.sorted ==> List(uuid1, uuid2))

            // Varargs
            _ <- inputMolecule(uuid1).get === List(uuid1)
            _ <- inputMolecule(uuid1, uuid2).get.map(_.sorted ==> List(uuid1, uuid2))

            // `or`
            _ <- inputMolecule(uuid1 or uuid2).get.map(_.sorted ==> List(uuid1, uuid2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))

            _ <- inputMolecule(List(uuid1)).get.map(_.sorted ==> List(uuid2, uuid3))
            _ <- inputMolecule(List(uuid1, uuid1)).get.map(_.sorted ==> List(uuid2, uuid3))
            _ <- inputMolecule(List(uuid1, uuid2)).get.map(_.sorted ==> List(uuid3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(uuid2)).get.map(_.sorted ==> List(uuid3))
            //(inputMolecule(List(uuid2, uuid3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(uuid2)).get.map(_.sorted ==> List(uuid2, uuid3))
            //(inputMolecule(List(uuid2, uuid3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(uuid2)).get === List(uuid1)
            //(inputMolecule(List(uuid2, uuid3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(uuid2)).get.map(_.sorted ==> List(uuid1, uuid2))
            //(inputMolecule(List(uuid2, uuid3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uuid_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str4)
            _ <- inputMolecule(List(uuid1)).get === List(str1)
            _ <- inputMolecule(List(uuid1, uuid1)).get === List(str1)
            _ <- inputMolecule(List(uuid1, uuid2)).get.map(_.sorted ==> List(str1, str2))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uuid_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(uuid1, uuid1)).get.map(_.sorted ==> List(str2, str3))
            _ <- inputMolecule(List(uuid1, uuid2)).get.map(_.sorted ==> List(str3))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uuid_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid2)).get.map(_.sorted ==> List(str3))
            //(inputMolecule(List(uuid2, uuid3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uuid_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid2)).get.map(_.sorted ==> List(str2, str3))
            //(inputMolecule(List(uuid2, uuid3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uuid_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid2)).get === List(str1)
            //(inputMolecule(List(uuid2, uuid3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.uuid_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(str1, str2, str3))
            _ <- inputMolecule(List(uuid2)).get.map(_.sorted ==> List(str1, str2))
            //(inputMolecule(List(uuid2, uuid3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[UUID]())).get === Nil


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uuid1))).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(List(Set(uuid1, uuid1))).get === List((uuid1, Set(uuid1, uuid2)))

            _ <- inputMolecule(List(Set(uuid1, uuid2))).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(List(Set(uuid1, uuid3))).get === Nil
            _ <- inputMolecule(List(Set(uuid2, uuid3))).get === List((uuid2, Set(uuid3, uuid2)))
            _ <- inputMolecule(List(Set(uuid4, uuid5))).get === List((uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5)))

            // 1 arg
            _ <- inputMolecule(Set(uuid1)).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(Set(uuid1, uuid1)).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(Set(uuid1, uuid2)).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(Set(uuid1, uuid3)).get === Nil
            _ <- inputMolecule(Set(uuid2, uuid3)).get === List((uuid2, Set(uuid3, uuid2)))
            _ <- inputMolecule(Set(uuid4, uuid5)).get === List((uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5)))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set[UUID]())).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid1))).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2))).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid3))).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3))).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2, uuid3))).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2), Set(uuid3))).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3, uuid4))).get === List((uuid1, Set(uuid1, uuid2)), (uuid3, Set(uuid4, uuid3)))


            // Multiple varargs
            _ <- inputMolecule(Set(uuid1, uuid2), Set[UUID]()).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(Set(uuid1), Set(uuid1)).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(Set(uuid1), Set(uuid2)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)))
            _ <- inputMolecule(Set(uuid1), Set(uuid3)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(Set(uuid1), Set(uuid2, uuid3)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)))
            _ <- inputMolecule(Set(uuid1), Set(uuid2), Set(uuid3)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3, uuid4)).get === List((uuid1, Set(uuid1, uuid2)), (uuid3, Set(uuid4, uuid3)))

            // `or`
            _ <- inputMolecule(Set(uuid1, uuid2) or Set[UUID]()).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(Set(uuid1) or Set(uuid1)).get === List((uuid1, Set(uuid1, uuid2)))
            _ <- inputMolecule(Set(uuid1) or Set(uuid2)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)))
            _ <- inputMolecule(Set(uuid1) or Set(uuid3)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(Set(uuid1, uuid2) or Set(uuid3)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(Set(uuid1) or Set(uuid2, uuid3)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)))
            _ <- inputMolecule(Set(uuid1) or Set(uuid2) or Set(uuid3)).get === List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)))
            _ <- inputMolecule(Set(uuid1, uuid2) or Set(uuid3, uuid4)).get === List((uuid1, Set(uuid1, uuid2)), (uuid3, Set(uuid4, uuid3)))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids.not(?)) // or m(Ns.uuid.uuids.!=(?))
          val all           = List(
            (uuid1, Set(uuid1, uuid2, uuid3)),
            (uuid2, Set(uuid2, uuid3, uuid4)),
            (uuid3, Set(uuid3, uuid4, uuid5))
          )
          for {
            _ <- manyData
            _ <- Ns.uuid.uuids insert all

            _ <- inputMolecule(Nil).get === all
            _ <- inputMolecule(Set[UUID]()).get === all

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uuid1).get === ...
            // inputMolecule(List(uuid1)).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(uuid1)).get === List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 match
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            )
            // Same as
            _ <- inputMolecule(List(Set(uuid1))).get === List(
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            )

            _ <- inputMolecule(Set(uuid2)).get === List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid2 match
              // (uuid2, Set(uuid2, uuid3, uuid4)),  // uuid2 match
              (uuid3, Set(uuid3, uuid4, uuid5))
            )

            _ <- inputMolecule(Set(uuid3)).get === Nil // uuid3 match all


            _ <- inputMolecule(Set(uuid1), Set(uuid2)).get === List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 match, uuid2 match
              // (uuid2, Set(uuid2, uuid3, uuid4)),  // uuid2 match
              (uuid3, Set(uuid3, uuid4, uuid5))
            )
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uuid1, uuid2)).get === List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 AND uuid2 match
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            )


            _ <- inputMolecule(Set(uuid1), Set(uuid3)).get === Nil // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid3)).get === List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 AND uuid3 match
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            )


            _ <- inputMolecule(Set(uuid1), Set(uuid2), Set(uuid3)).get === Nil // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid2, uuid3)).get === List(
              // (uuid1, Set(uuid1, uuid2, uuid3)),  // uuid1 AND uuid2 AND uuid3 match
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            )


            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid1)).get === List(
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            )
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2)).get === List(
              (uuid3, Set(uuid3, uuid4, uuid5))
            )
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3)).get === Nil
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4)).get === Nil
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid5)).get === List(
              (uuid2, Set(uuid2, uuid3, uuid4))
            )

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2, uuid3)).get === List(
              (uuid3, Set(uuid3, uuid4, uuid5))
            )
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4, uuid5)).get === List(
              (uuid2, Set(uuid2, uuid3, uuid4))
            )
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            // (uuid3, uuid4), (uuid4, uuid5), (uuid4, uuid5, uuid6)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sortBy(_._1.toString) ==> List((uuid2, Set(uuid3)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            // (uuid2, uuid4), (uuid3, uuid4), (uuid4, uuid5), (uuid4, uuid5, uuid6)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            _ <- inputMolecule(List(Set(uuid2))).get === List((uuid1, Set(uuid1)))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid3)), (uuid4, Set(uuid4, uuid5)), (uuid5, Set(uuid4, uuid6, uuid5))))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sortBy(_._1.toString) ==> List((uuid1, Set(uuid1, uuid2)), (uuid2, Set(uuid2))))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[UUID]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uuid1))).get === List(Set(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid2))).get === List(Set(uuid1, uuid2, uuid3)) // (uuid1, uuid2) + (uuid2, uuid3)
            _ <- inputMolecule(List(Set(uuid3))).get === List(Set(uuid2, uuid3, uuid4)) // (uuid2, uuid3) + (uuid3, uuid4)

            _ <- inputMolecule(List(Set(uuid1, uuid2))).get === List(Set(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid1, uuid3))).get === Nil
            _ <- inputMolecule(List(Set(uuid2, uuid3))).get === List(Set(uuid2, uuid3))
            _ <- inputMolecule(List(Set(uuid4, uuid5))).get === List(Set(uuid4, uuid5, uuid6)) // (uuid4, uuid5) + (uuid4, uuid5, uuid6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uuid1), Set(uuid1))).get === List(Set(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2))).get === List(Set(uuid1, uuid2, uuid3)) // (uuid1, uuid2) + (uuid2, uuid3)
            _ <- inputMolecule(List(Set(uuid1), Set(uuid3))).get === List(Set(uuid1, uuid4, uuid3, uuid2)) // (uuid1, uuid2) + (uuid2, uuid3) + (uuid3, uuid4)
            _ <- inputMolecule(List(Set(uuid2), Set(uuid3))).get === List(Set(uuid1, uuid2, uuid3, uuid4)) // (uuid1, uuid2) + (uuid2, uuid3) + (uuid3, uuid4)

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3))).get === List(Set(uuid1, uuid2, uuid3, uuid4)) // (uuid1, uuid2) + (uuid2, uuid3) + (uuid3, uuid4)
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2, uuid3))).get === List(Set(uuid1, uuid3, uuid2)) // (uuid1, uuid2) + (uuid2, uuid3)
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2), Set(uuid3))).get === List(Set(uuid1, uuid2, uuid3, uuid4)) // (uuid1, uuid2) + (uuid2, uuid3) + (uuid3, uuid4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.not(?)) // or m(Ns.uuid.uuids.!=(?))
          val all           = List(
            (uuid1, Set(uuid1, uuid2, uuid3)),
            (uuid2, Set(uuid2, uuid3, uuid4)),
            (uuid3, Set(uuid3, uuid4, uuid5))
          )
          for {
            _ <- manyData
            _ <- Ns.uuid.uuids insert all

            _ <- inputMolecule(Nil).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(Set[UUID]()).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uuid1).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(uuid1)).get === List(Set(uuid2, uuid3, uuid4, uuid5))
            // Same as
            _ <- inputMolecule(List(Set(uuid1))).get === List(Set(uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(Set(uuid2)).get === List(Set(uuid3, uuid4, uuid5))
            _ <- inputMolecule(Set(uuid3)).get === Nil // uuid3 match all

            _ <- inputMolecule(Set(uuid1), Set(uuid2)).get === List(Set(uuid3, uuid4, uuid5))
            _ <- inputMolecule(Set(uuid1), Set(uuid3)).get === Nil // uuid3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uuid1, uuid2)).get === List(Set(uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(Set(uuid1, uuid3)).get === List(Set(uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(Set(uuid1), Set(uuid2), Set(uuid3)).get === Nil // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid2, uuid3)).get === List(Set(uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid1)).get === List(Set(uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2)).get === List(Set(uuid3, uuid4, uuid5))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3)).get === Nil
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4)).get === Nil
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid5)).get === List(Set(uuid2, uuid3, uuid4))

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2, uuid3)).get === List(Set(uuid3, uuid4, uuid5))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4, uuid5)).get === List(Set(uuid2, uuid3, uuid4))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6))
            _ <- inputMolecule(List(Set[UUID]())).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6))

            _ <- inputMolecule(List(Set(uuid2))).get === List(Set(uuid3, uuid4, uuid5, uuid6))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6))
            _ <- inputMolecule(List(Set[UUID]())).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6))

            _ <- inputMolecule(List(Set(uuid2))).get === List(Set(uuid2, uuid3, uuid4, uuid5, uuid6))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6))
            _ <- inputMolecule(List(Set[UUID]())).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6))

            _ <- inputMolecule(List(Set(uuid2))).get === List(Set(uuid1))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuids.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6))
            _ <- inputMolecule(List(Set[UUID]())).get === List(Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6))

            _ <- inputMolecule(List(Set(uuid2))).get === List(Set(uuid1, uuid2))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(uuid6)
            _ <- inputMolecule(List(Set[UUID]())).get === List(uuid6)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(uuid1))).get === List(uuid1)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sorted ==> List(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid3))).get.map(_.sorted ==> List(uuid2, uuid3))

            _ <- inputMolecule(List(Set(uuid1, uuid1))).get === List(uuid1)
            _ <- inputMolecule(List(Set(uuid1, uuid2))).get === List(uuid1)
            _ <- inputMolecule(List(Set(uuid1, uuid3))).get === Nil
            _ <- inputMolecule(List(Set(uuid2, uuid3))).get === List(uuid2)
            _ <- inputMolecule(List(Set(uuid4, uuid5))).get.map(_.sorted ==> List(uuid4, uuid5))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set[UUID]())).get === List(uuid1)
            _ <- inputMolecule(List(Set(uuid1), Set(uuid1))).get === List(uuid1)
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2))).get.map(_.sorted ==> List(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid3))).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3))).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2, uuid3))).get.map(_.sorted ==> List(uuid1, uuid2))
            _ <- inputMolecule(List(Set(uuid1), Set(uuid2), Set(uuid3))).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))

            _ <- inputMolecule(List(Set(uuid1, uuid2), Set(uuid3, uuid4))).get.map(_.sorted ==> List(uuid1, uuid3))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids_.not(?)) // or m(Ns.uuid.uuids.!=(?))
          for {
            _ <- manyData
            _ <- Ns.uuid.uuids insert List(
              (uuid1, Set(uuid1, uuid2, uuid3)),
              (uuid2, Set(uuid2, uuid3, uuid4)),
              (uuid3, Set(uuid3, uuid4, uuid5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
            _ <- inputMolecule(Set[UUID]()).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(uuid1).get === ...
            // inputMolecule(List(uuid1)).get === ...

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(uuid1)).get.map(_.sorted ==> List(uuid2, uuid3))
            // Same as
            _ <- inputMolecule(List(Set(uuid1))).get.map(_.sorted ==> List(uuid2, uuid3))

            _ <- inputMolecule(Set(uuid2)).get === List(uuid3)
            _ <- inputMolecule(Set(uuid3)).get === Nil // uuid3 match all


            _ <- inputMolecule(Set(uuid1), Set(uuid2)).get === List(uuid3)
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(uuid1, uuid2)).get.map(_.sorted ==> List(uuid2, uuid3))

            _ <- inputMolecule(Set(uuid1), Set(uuid3)).get === Nil // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid3)).get.map(_.sorted ==> List(uuid2, uuid3))

            _ <- inputMolecule(Set(uuid1), Set(uuid2), Set(uuid3)).get === Nil // uuid3 match all
            _ <- inputMolecule(Set(uuid1, uuid2, uuid3)).get.map(_.sorted ==> List(uuid2, uuid3))

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid1)).get.map(_.sorted ==> List(uuid2, uuid3))
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2)).get === List(uuid3)
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid3)).get === Nil
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4)).get === Nil
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid5)).get === List(uuid2)

            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid2, uuid3)).get === List(uuid3)
            _ <- inputMolecule(Set(uuid1, uuid2), Set(uuid4, uuid5)).get === List(uuid2)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            // (uuid3, uuid4), (uuid4, uuid5), (uuid4, uuid5, uuid6)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sorted ==> List(uuid2, uuid3, uuid4, uuid5))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            // (uuid2, uuid4), (uuid3, uuid4), (uuid4, uuid5), (uuid4, uuid5, uuid6)
            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(List(Set(uuid2))).get === List(uuid1)

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.uuid.uuids_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))
            _ <- inputMolecule(List(Set[UUID]())).get.map(_.sorted ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

            _ <- inputMolecule(List(Set(uuid2))).get.map(_.sorted ==> List(uuid1, uuid2))

            //(inputMolecule(List(Set(uuid2, uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(uuid2), Set(uuid3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }
  }
}