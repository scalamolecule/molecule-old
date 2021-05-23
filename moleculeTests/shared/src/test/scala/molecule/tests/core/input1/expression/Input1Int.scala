package molecule.tests.core.input1.expression

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}


object Input1Int extends AsyncTestSuite {

  def oneData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.str.int$ insert List(
      (str1, Some(int1)),
      (str2, Some(int2)),
      (str3, Some(int3)),
      (str4, None)
    )
  }

  def manyData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.int.ints$ insert List(
      (int1, Some(Set(int1, int2))),
      (int2, Some(Set(int2, int3))),
      (int3, Some(Set(int3, int4))),
      (int4, Some(Set(int4, int5))),
      (int5, Some(Set(int4, int5, int6))),
      (int6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === Nil

            _ <- inputMolecule(List(int1)).get === List(int1)
            _ <- inputMolecule(List(int1, int1)).get === List(int1)
            _ <- inputMolecule(List(int1, int2)).get === List(int1, int2)

            // Varargs
            _ <- inputMolecule(int1).get === List(int1)
            _ <- inputMolecule(int1, int2).get === List(int1, int2)

            // `or`
            _ <- inputMolecule(int1 or int2).get === List(int1, int2)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3)

            _ <- inputMolecule(List(int1)).get === List(int2, int3)
            _ <- inputMolecule(List(int1, int1)).get === List(int2, int3)
            _ <- inputMolecule(List(int1, int2)).get === List(int3)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.int.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3)
            _ <- inputMolecule(List(int2)).get === List(int3)
            //(inputMolecule(List(int2, int3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3)
            _ <- inputMolecule(List(int2)).get === List(int2, int3)
            //(inputMolecule(List(int2, int3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.int.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3)
            _ <- inputMolecule(List(int2)).get === List(int1)
            //(inputMolecule(List(int2, int3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3)
            _ <- inputMolecule(List(int2)).get === List(int1, int2)
            //(inputMolecule(List(int2, int3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }

      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.str.int_(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str4)
            _ <- inputMolecule(List(int1)).get === List(str1)
            _ <- inputMolecule(List(int1, int1)).get === List(str1)
            _ <- inputMolecule(List(int1, int2)).get === List(str1, str2)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.int_.not(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(int1)).get === List(str2, str3)
            _ <- inputMolecule(List(int1, int1)).get === List(str2, str3)
            _ <- inputMolecule(List(int1, int2)).get === List(str3)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.str.int_.>(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(int2)).get === List(str3)
            //(inputMolecule(List(int2, int3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.int_.>=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(int2)).get === List(str2, str3)
            //(inputMolecule(List(int2, int3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.str.int_.<(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(int2)).get === List(str1)
            //(inputMolecule(List(int2, int3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.str.int_.<=(?))
          for {
            _ <- oneData
            _ <- inputMolecule(Nil).get === List(str1, str2, str3)
            _ <- inputMolecule(List(int2)).get === List(str1, str2)
            //(inputMolecule(List(int2, int3)).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }


    "Card many" - {

      "Mandatory" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[Int]())).get === Nil


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(int1))).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(List(Set(int1, int1))).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(List(Set(int2))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))

            _ <- inputMolecule(List(Set(int1, int2))).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(List(Set(int1, int3))).get === Nil
            _ <- inputMolecule(List(Set(int2, int3))).get === List((int2, Set(int3, int2)))
            _ <- inputMolecule(List(Set(int4, int5))).get === List((int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

            // 1 arg
            _ <- inputMolecule(Set(int1)).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(Set(int1, int1)).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(Set(int1, int2)).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(Set(int1, int3)).get === Nil
            _ <- inputMolecule(Set(int2, int3)).get === List((int2, Set(int3, int2)))
            _ <- inputMolecule(Set(int4, int5)).get === List((int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(int1, int2), Set[Int]())).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(List(Set(int1), Set(int1))).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(List(Set(int1), Set(int2))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
            _ <- inputMolecule(List(Set(int1), Set(int3))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
            _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(List(Set(int1, int2), Set(int3, int4))).get === List((int1, Set(int1, int2)), (int3, Set(int4, int3)))


            // Multiple varargs
            _ <- inputMolecule(Set(int1, int2), Set[Int]()).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(Set(int1), Set(int1)).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(Set(int1), Set(int2)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
            _ <- inputMolecule(Set(int1), Set(int3)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(Set(int1, int2), Set(int3)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(Set(int1), Set(int2, int3)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
            _ <- inputMolecule(Set(int1), Set(int2), Set(int3)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(Set(int1, int2), Set(int3, int4)).get === List((int1, Set(int1, int2)), (int3, Set(int4, int3)))

            // `or`
            _ <- inputMolecule(Set(int1, int2) or Set[Int]()).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(Set(int1) or Set(int1)).get === List((int1, Set(int1, int2)))
            _ <- inputMolecule(Set(int1) or Set(int2)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
            _ <- inputMolecule(Set(int1) or Set(int3)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(Set(int1, int2) or Set(int3)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(Set(int1) or Set(int2, int3)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
            _ <- inputMolecule(Set(int1) or Set(int2) or Set(int3)).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
            _ <- inputMolecule(Set(int1, int2) or Set(int3, int4)).get === List((int1, Set(int1, int2)), (int3, Set(int4, int3)))
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.not(?)) // or m(Ns.int.ints.!=(?))
          val all           = List(
            (int1, Set(int1, int2, int3)),
            (int2, Set(int2, int3, int4)),
            (int3, Set(int3, int4, int5))
          )
          for {
            _ <- manyData
            _ <- Ns.int.ints insert all

            _ <- inputMolecule(Nil).get === all
            _ <- inputMolecule(Set[Int]()).get === all

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(int1).get === ...
            // inputMolecule(List(int1)).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(int1)).get === List(
              // (int1, Set(int1, int2, int3)),  // int1 match
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )
            // Same as
            _ <- inputMolecule(List(Set(int1))).get === List(
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )

            _ <- inputMolecule(Set(int2)).get === List(
              // (int1, Set(int1, int2, int3)),  // int2 match
              // (int2, Set(int2, int3, int4)),  // int2 match
              (int3, Set(int3, int4, int5))
            )

            _ <- inputMolecule(Set(int3)).get === Nil // int3 match all


            _ <- inputMolecule(Set(int1), Set(int2)).get === List(
              // (int1, Set(int1, int2, int3)),  // int1 match, int2 match
              // (int2, Set(int2, int3, int4)),  // int2 match
              (int3, Set(int3, int4, int5))
            )
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(int1, int2)).get === List(
              // (int1, Set(int1, int2, int3)),  // int1 AND int2 match
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )


            _ <- inputMolecule(Set(int1), Set(int3)).get === Nil // int3 match all
            _ <- inputMolecule(Set(int1, int3)).get === List(
              // (int1, Set(int1, int2, int3)),  // int1 AND int3 match
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )


            _ <- inputMolecule(Set(int1), Set(int2), Set(int3)).get === Nil // int3 match all
            _ <- inputMolecule(Set(int1, int2, int3)).get === List(
              // (int1, Set(int1, int2, int3)),  // int1 AND int2 AND int3 match
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )


            _ <- inputMolecule(Set(int1, int2), Set(int1)).get === List(
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )
            _ <- inputMolecule(Set(int1, int2), Set(int2)).get === List(
              (int3, Set(int3, int4, int5))
            )
            _ <- inputMolecule(Set(int1, int2), Set(int3)).get === Nil
            _ <- inputMolecule(Set(int1, int2), Set(int4)).get === Nil
            _ <- inputMolecule(Set(int1, int2), Set(int5)).get === List(
              (int2, Set(int2, int3, int4))
            )

            _ <- inputMolecule(Set(int1, int2), Set(int2, int3)).get === List(
              (int3, Set(int3, int4, int5))
            )
            _ <- inputMolecule(Set(int1, int2), Set(int4, int5)).get === List(
              (int2, Set(int2, int3, int4))
            )
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
            _ <- inputMolecule(List(Set[Int]())).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

            // (int3), (int3, int4), (int4, int5), (int4, int5, int6)
            _ <- inputMolecule(List(Set(int2))).get === List((int2, Set(int3)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
            _ <- inputMolecule(List(Set[Int]())).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

            // (int2, int4), (int3, int4), (int4, int5), (int4, int5, int6)
            _ <- inputMolecule(List(Set(int2))).get === List((int1, Set(int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
            _ <- inputMolecule(List(Set[Int]())).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

            _ <- inputMolecule(List(Set(int2))).get === List((int1, Set(int1)))

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
            _ <- inputMolecule(List(Set[Int]())).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

            _ <- inputMolecule(List(Set(int2))).get === List((int1, Set(int1, int2)), (int2, Set(int2)))

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Mandatory, single attr coalesce" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.ints(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === Nil
            _ <- inputMolecule(List(Set[Int]())).get === Nil

            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(int1))).get === List(Set(int1, int2))
            _ <- inputMolecule(List(Set(int2))).get === List(Set(int1, int2, int3)) // (int1, int2) + (int2, int3)
            _ <- inputMolecule(List(Set(int3))).get === List(Set(int2, int3, int4)) // (int2, int3) + (int3, int4)

            _ <- inputMolecule(List(Set(int1, int2))).get === List(Set(int1, int2))
            _ <- inputMolecule(List(Set(int1, int3))).get === Nil
            _ <- inputMolecule(List(Set(int2, int3))).get === List(Set(int2, int3))
            _ <- inputMolecule(List(Set(int4, int5))).get === List(Set(int4, int5, int6)) // (int4, int5) + (int4, int5, int6)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(int1), Set(int1))).get === List(Set(int1, int2))
            _ <- inputMolecule(List(Set(int1), Set(int2))).get === List(Set(int1, int2, int3)) // (int1, int2) + (int2, int3)
            _ <- inputMolecule(List(Set(int1), Set(int3))).get === List(Set(int1, int4, int3, int2)) // (int1, int2) + (int2, int3) + (int3, int4)
            _ <- inputMolecule(List(Set(int2), Set(int3))).get === List(Set(int1, int2, int3, int4)) // (int1, int2) + (int2, int3) + (int3, int4)

            _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get === List(Set(int1, int2, int3, int4)) // (int1, int2) + (int2, int3) + (int3, int4)
            _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get === List(Set(int1, int3, int2)) // (int1, int2) + (int2, int3)
            _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get === List(Set(int1, int2, int3, int4)) // (int1, int2) + (int2, int3) + (int3, int4)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.ints.not(?)) // or m(Ns.int.ints.!=(?))
          for {
            _ <- manyData
            _ <- Ns.int.ints insert List(
              (int1, Set(int1, int2, int3)),
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )

            _ <- inputMolecule(Nil).get === List(Set(int1, int2, int3, int4, int5))
            _ <- inputMolecule(Set[Int]()).get === List(Set(int1, int2, int3, int4, int5))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(int1).get === ...

            // Set semantics omit the whole set with one or more matching values
            _ <- inputMolecule(Set(int1)).get === List(Set(int2, int3, int4, int5))
            // Same as
            _ <- inputMolecule(List(Set(int1))).get === List(Set(int2, int3, int4, int5))

            _ <- inputMolecule(Set(int2)).get === List(Set(int3, int4, int5))
            _ <- inputMolecule(Set(int3)).get === Nil // int3 match all

            _ <- inputMolecule(Set(int1), Set(int2)).get === List(Set(int3, int4, int5))
            _ <- inputMolecule(Set(int1), Set(int3)).get === Nil // int3 match all

            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(int1, int2)).get === List(Set(int2, int3, int4, int5))
            _ <- inputMolecule(Set(int1, int3)).get === List(Set(int2, int3, int4, int5))

            _ <- inputMolecule(Set(int1), Set(int2), Set(int3)).get === Nil // int3 match all
            _ <- inputMolecule(Set(int1, int2, int3)).get === List(Set(int2, int3, int4, int5))

            _ <- inputMolecule(Set(int1, int2), Set(int1)).get === List(Set(int2, int3, int4, int5))
            _ <- inputMolecule(Set(int1, int2), Set(int2)).get === List(Set(int3, int4, int5))
            _ <- inputMolecule(Set(int1, int2), Set(int3)).get === Nil
            _ <- inputMolecule(Set(int1, int2), Set(int4)).get === Nil
            _ <- inputMolecule(Set(int1, int2), Set(int5)).get === List(Set(int2, int3, int4))

            _ <- inputMolecule(Set(int1, int2), Set(int2, int3)).get === List(Set(int3, int4, int5))
            _ <- inputMolecule(Set(int1, int2), Set(int4, int5)).get === List(Set(int2, int3, int4))
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.ints.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(int1, int2, int3, int4, int5, int6))
            _ <- inputMolecule(List(Set[Int]())).get === List(Set(int1, int2, int3, int4, int5, int6))

            _ <- inputMolecule(List(Set(int2))).get === List(Set(int3, int4, int5, int6))

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.ints.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(int1, int2, int3, int4, int5, int6))
            _ <- inputMolecule(List(Set[Int]())).get === List(Set(int1, int2, int3, int4, int5, int6))

            _ <- inputMolecule(List(Set(int2))).get === List(Set(int2, int3, int4, int5, int6))

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.ints.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(int1, int2, int3, int4, int5, int6))
            _ <- inputMolecule(List(Set[Int]())).get === List(Set(int1, int2, int3, int4, int5, int6))

            _ <- inputMolecule(List(Set(int2))).get === List(Set(int1))

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.ints.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(Set(int1, int2, int3, int4, int5, int6))
            _ <- inputMolecule(List(Set[Int]())).get === List(Set(int1, int2, int3, int4, int5, int6))

            _ <- inputMolecule(List(Set(int2))).get === List(Set(int1, int2))

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }


      "Tacit" - {

        "Eq" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(int6)
            _ <- inputMolecule(List(Set[Int]())).get === List(int6)


            // Values of 1 Set match values of 1 card-many attribute at a time

            _ <- inputMolecule(List(Set(int1))).get === List(int1)
            _ <- inputMolecule(List(Set(int2))).get === List(int1, int2)
            _ <- inputMolecule(List(Set(int3))).get === List(int2, int3)

            _ <- inputMolecule(List(Set(int1, int1))).get === List(int1)
            _ <- inputMolecule(List(Set(int1, int2))).get === List(int1)
            _ <- inputMolecule(List(Set(int1, int3))).get === Nil
            _ <- inputMolecule(List(Set(int2, int3))).get === List(int2)
            _ <- inputMolecule(List(Set(int4, int5))).get === List(int4, int5)


            // Values of each Set matches values of 1 card-many attributes respectively

            _ <- inputMolecule(List(Set(int1, int2), Set[Int]())).get === List(int1)
            _ <- inputMolecule(List(Set(int1), Set(int1))).get === List(int1)
            _ <- inputMolecule(List(Set(int1), Set(int2))).get === List(int1, int2)
            _ <- inputMolecule(List(Set(int1), Set(int3))).get === List(int1, int2, int3)

            _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get === List(int1, int2, int3)
            _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get === List(int1, int2)
            _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get === List(int1, int2, int3)

            _ <- inputMolecule(List(Set(int1, int2), Set(int3, int4))).get === List(int1, int3)
          } yield ()
        }

        "!=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.not(?)) // or m(Ns.int.ints.!=(?))
          for {
            _ <- manyData
            _ <- Ns.int.ints insert List(
              (int1, Set(int1, int2, int3)),
              (int2, Set(int2, int3, int4)),
              (int3, Set(int3, int4, int5))
            )

            _ <- inputMolecule(Nil).get.map(_.sorted ==> List(int1, int2, int3))
            _ <- inputMolecule(Set[Int]()).get.map(_.sorted ==> List(int1, int2, int3))

            // Vararg/List(args*) syntax/semantics not available for card-many attributes of input molecules
            // inputMolecule(int1).get === ...
            // inputMolecule(List(int1)).get === ...

            // Set semantics omit the whole set with one or more matching values

            _ <- inputMolecule(Set(int1)).get.map(_.sorted ==> List(int2, int3))
            // Same as
            _ <- inputMolecule(List(Set(int1))).get.map(_.sorted ==> List(int2, int3))

            _ <- inputMolecule(Set(int2)).get === List(int3)
            _ <- inputMolecule(Set(int3)).get === Nil // int3 match all


            _ <- inputMolecule(Set(int1), Set(int2)).get === List(int3)
            // Multiple values in a Set matches matches set-wise
            _ <- inputMolecule(Set(int1, int2)).get.map(_.sorted ==> List(int2, int3))

            _ <- inputMolecule(Set(int1), Set(int3)).get === Nil // int3 match all
            _ <- inputMolecule(Set(int1, int3)).get.map(_.sorted ==> List(int2, int3))

            _ <- inputMolecule(Set(int1), Set(int2), Set(int3)).get === Nil // int3 match all
            _ <- inputMolecule(Set(int1, int2, int3)).get.map(_.sorted ==> List(int2, int3))

            _ <- inputMolecule(Set(int1, int2), Set(int1)).get.map(_.sorted ==> List(int2, int3))
            _ <- inputMolecule(Set(int1, int2), Set(int2)).get === List(int3)
            _ <- inputMolecule(Set(int1, int2), Set(int3)).get === Nil
            _ <- inputMolecule(Set(int1, int2), Set(int4)).get === Nil
            _ <- inputMolecule(Set(int1, int2), Set(int5)).get === List(int2)

            _ <- inputMolecule(Set(int1, int2), Set(int2, int3)).get === List(int3)
            _ <- inputMolecule(Set(int1, int2), Set(int4, int5)).get === List(int2)
          } yield ()
        }

        ">" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.>(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3, int4, int5)
            _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3, int4, int5)

            // (int3, int4), (int4, int5), (int4, int5, int6)
            _ <- inputMolecule(List(Set(int2))).get === List(int2, int3, int4, int5)

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        ">=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.>=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3, int4, int5)
            _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3, int4, int5)

            // (int2, int4), (int3, int4), (int4, int5), (int4, int5, int6)
            _ <- inputMolecule(List(Set(int2))).get === List(int1, int2, int3, int4, int5)

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.<(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3, int4, int5)
            _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3, int4, int5)

            _ <- inputMolecule(List(Set(int2))).get === List(int1)

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }

        "<=" - core { implicit conn =>
          val inputMolecule = m(Ns.int.ints_.<=(?))
          for {
            _ <- manyData
            _ <- inputMolecule(Nil).get === List(int1, int2, int3, int4, int5)
            _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3, int4, int5)

            _ <- inputMolecule(List(Set(int2))).get === List(int1, int2)

            //(inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."

            //(inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
            // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            // "Can't apply multiple values to comparison function."
          } yield ()
        }
      }
    }
  }
}