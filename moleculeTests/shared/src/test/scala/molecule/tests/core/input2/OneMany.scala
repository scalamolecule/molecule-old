package molecule.tests.core.input2

import molecule.core.api.exception.Molecule_2_Exception
import molecule.core.exceptions.MoleculeException
import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.in2_out3._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global


object OneMany extends AsyncTestSuite {

  def pairsData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.int.str$.longs$ insert List(
      (1, Some("a"), Some(Set(1L, 2L))),
      (2, Some("a"), Some(Set(2L, 3L))),
      (3, Some("a"), Some(Set(3L, 4L))),

      (4, Some("b"), Some(Set(1L, 2L))),
      (5, Some("b"), Some(Set(2L, 3L))),
      (6, Some("b"), Some(Set(3L, 4L))),

      (7, Some("a"), None),
      (8, None, None)
    )
  }

  lazy val tests = Tests {

    "Pairs" - {

      // Cardinality one + many input molecule
      val im = m(Ns.int.str_(?).longs_(?))

      "Equality" - {

        "4 syntaxes" - core { implicit conn =>
          for {
            _ <- pairsData

            // 1 pair
            _ <- im("a", Set(1L)).get === List(1) // only for 1 pair
            _ <- im(("a", Set(1L))).get === List(1)
            _ <- im(List(("a", Set(1L)))).get === List(1)
            _ <- im("a" and Set(1L)).get === List(1)

            // 2 pairs
            _ <- im(("a", Set(1L)), ("b", Set(1L))).get === List(1, 4)
            _ <- im(List(("a", Set(1L)), ("b", Set(1L)))).get === List(1, 4)
            _ <- im(("a" and Set(1L)) or ("b" and Set(1L))).get === List(1, 4)

            // 3 pairs etc..
            _ <- im(("a", Set(1L)), ("b", Set(1L)), ("b", Set(2L))).get === List(1, 4, 5)
            _ <- im(List(("a", Set(1L)), ("b", Set(1L)), ("b", Set(2L)))).get === List(1, 4, 5)
            _ <- im(("a" and Set(1L)) or ("b" and Set(1L)) or ("b" and Set(2L))).get === List(1, 4, 5)
          } yield ()
        }

        "Set semantics" - core { implicit conn =>
          for {
            _ <- pairsData

            // All Set values returned in equality matches
            _ <- m(Ns.int.str_(?).longs(?)).apply("a", Set(1L)).get === List((1, Set(1L, 2L)))
          } yield ()
        }

        "0 pairs" - core { implicit conn =>
          for {
            _ <- pairsData

            // If both attributes are tacit, entities with pairs of non-asserted attributes can be returned
            _ <- m(Ns.int.str_(?).longs_(?)).apply(Nil).get === List(8)

            //            // Mandatory attribute will not match Nil
            //            (m(Ns.int.str_(?).longs(?)).apply(Nil).get must throwA[Molecule_2_Exception])
            //            .message === "Got the exception molecule.core.api.exception.Molecule_2_Exception: " +
            //              "Can only apply empty list of pairs (Nil) to two tacit attributes"
          } yield ()
        }

        "1 pair" - core { implicit conn =>
          for {
            _ <- pairsData

            // Single Set value

            _ <- im("a", Set(1L)).get === List(1)
            _ <- im("a", Set(2L)).get === List(1, 2)
            _ <- im("a", Set(3L)).get === List(2, 3)
            _ <- im("a", Set(4L)).get === List(3)

            // Multiple Set values

            _ <- im("a", Set(1L, 2L)).get === List(1)
            _ <- im("a", Set(1L, 3L)).get === Nil
            _ <- im("a", Set(2L, 3L)).get === List(2)

            // Empty Set matches missing assertions
            _ <- im("a", Set[Long]()).get === List(7)

            // Non-matching value
            _ <- im("a", Set(5L)).get === Nil

            // Must match all values in Set
            _ <- im("a", Set(1L, 5L)).get === Nil
          } yield ()
        }

        "Multiple pairs with non-matching pair(s)" - core { implicit conn =>
          for {
            _ <- pairsData

            _ <- im(("x", Set(0L)), ("a", Set(1L))).get === List(1) //     () + (1)
            _ <- im(("x", Set(0L)), ("a", Set(2L))).get === List(1, 2) //  () + (1, 2)
            _ <- im(("x", Set(0L)), ("a", Set(3L))).get === List(2, 3) //  () + (2, 3)
            _ <- im(("x", Set(0L)), ("a", Set(4L))).get === List(3) //     () + (3)
            _ <- im(("x", Set(0L)), ("a", Set(5L))).get === Nil //         () + ()

            _ <- im(("x", Set(0L)), ("a", Set(1L, 2L))).get === List(1) // () + (1)
            _ <- im(("x", Set(0L)), ("a", Set(1L, 3L))).get === Nil //     () + ()
            _ <- im(("x", Set(0L)), ("a", Set(2L, 3L))).get === List(2) // () + (2)
          } yield ()
        }

        "Multiple pairs with shared card-one value (a)" - core { implicit conn =>
          for {
            _ <- pairsData

            _ <- im(("a", Set(1L)), ("a", Set(2L))).get === List(1, 2) //         (1) + (1, 2)
            _ <- im(("a", Set(1L)), ("a", Set(3L))).get === List(1, 2, 3) //      (1) + (2, 3)
            _ <- im(("a", Set(1L)), ("a", Set(4L))).get === List(1, 3) //         (1) + (3)
            _ <- im(("a", Set(1L)), ("a", Set(5L))).get === List(1) //            (1) + ()
            _ <- im(("a", Set(1L)), ("a", Set[Long]())).get === List(1, 7) //     (1) + (7)

            _ <- im(("a", Set(1L, 2L)), ("a", Set(1L))).get === List(1) //        (1) + (1)
            _ <- im(("a", Set(1L, 2L)), ("a", Set(2L))).get === List(1, 2) //     (1) + (1, 2)
            _ <- im(("a", Set(1L, 2L)), ("a", Set(3L))).get === List(1, 2, 3) //  (1) + (2, 3)
            _ <- im(("a", Set(1L, 2L)), ("a", Set(4L))).get === List(1, 3) //     (1) + (3)
            _ <- im(("a", Set(1L, 2L)), ("a", Set(5L))).get === List(1) //        (1) + ()

            _ <- im(("a", Set(2L)), ("a", Set(1L))).get === List(1, 2) //         (1, 2) + (1)
            _ <- im(("a", Set(2L)), ("a", Set(3L))).get === List(1, 2, 3) //      (1, 2) + (2, 3)
            _ <- im(("a", Set(2L)), ("a", Set(4L))).get === List(1, 2, 3) //      (1, 2) + (3)
            _ <- im(("a", Set(2L)), ("a", Set(5L))).get === List(1, 2) //         (1, 2) + ()


            _ <- im(("a", Set(1L)), ("a", Set(1L, 2L))).get === List(1) //        (1) + (1)
            _ <- im(("a", Set(1L)), ("a", Set(1L, 3L))).get === List(1) //        (1) + ()
            _ <- im(("a", Set(1L)), ("a", Set(2L, 3L))).get === List(1, 2) //     (1) + (2)
            _ <- im(("a", Set(1L)), ("a", Set(3L, 4L))).get === List(1, 3) //     (1) + (3)

            _ <- im(("a", Set(1L, 2L)), ("a", Set(1L, 3L))).get === List(1) //    (1) + ()
            _ <- im(("a", Set(1L, 2L)), ("a", Set(2L, 3L))).get === List(1, 2) // (1) + (2)
            _ <- im(("a", Set(1L, 2L)), ("a", Set(3L, 4L))).get === List(1, 3) // (1) + (3)

            _ <- im(("a", Set(2L)), ("a", Set(1L, 2L))).get === List(1, 2) //     (1, 2) + (1)
            _ <- im(("a", Set(2L)), ("a", Set(1L, 3L))).get === List(1, 2) //     (1, 2) + ()
            _ <- im(("a", Set(2L)), ("a", Set(2L, 3L))).get === List(1, 2) //     (1, 2) + (2)
            _ <- im(("a", Set(2L)), ("a", Set(3L, 4L))).get === List(1, 2, 3) //  (1, 2) + (3)
          } yield ()
        }

        "Multiple pairs" - core { implicit conn =>
          for {
            _ <- pairsData

            _ <- im(("a", Set(1L)), ("b", Set(2L))).get === List(1, 4, 5) //      (1) + (4, 5)
            _ <- im(("a", Set(1L)), ("b", Set(3L))).get === List(1, 5, 6) //      (1) + (5, 6)
            _ <- im(("a", Set(1L)), ("b", Set(4L))).get === List(1, 6) //         (1) + (6)
            _ <- im(("a", Set(1L)), ("b", Set(5L))).get === List(1) //            (1) + ()

            _ <- im(("a", Set(1L, 2L)), ("b", Set(1L))).get === List(1, 4) //     (1) + (4)
            _ <- im(("a", Set(1L, 2L)), ("b", Set(2L))).get === List(1, 4, 5) //  (1) + (4, 5)
            _ <- im(("a", Set(1L, 2L)), ("b", Set(3L))).get === List(1, 5, 6) //  (1) + (5, 6)
            _ <- im(("a", Set(1L, 2L)), ("b", Set(4L))).get === List(1, 6) //     (1) + (6)
            _ <- im(("a", Set(1L, 2L)), ("b", Set(5L))).get === List(1) //        (1) + ()

            _ <- im(("a", Set(2L)), ("b", Set(1L))).get === List(1, 2, 4) //      (1, 2) + (4)
            _ <- im(("a", Set(2L)), ("b", Set(3L))).get === List(1, 2, 5, 6) //   (1, 2) + (5, 6)
            _ <- im(("a", Set(2L)), ("b", Set(4L))).get === List(1, 2, 6) //      (1, 2) + (6)
            _ <- im(("a", Set(2L)), ("b", Set(5L))).get === List(1, 2) //         (1, 2) + ()


            _ <- im(("a", Set(1L)), ("b", Set(1L, 2L))).get === List(1, 4) //     (1) + (4)
            _ <- im(("a", Set(1L)), ("b", Set(1L, 3L))).get === List(1) //        (1) + ()
            _ <- im(("a", Set(1L)), ("b", Set(2L, 3L))).get === List(1, 5) //     (1) + (5)
            _ <- im(("a", Set(1L)), ("b", Set(3L, 4L))).get === List(1, 6) //     (1) + (6)

            _ <- im(("a", Set(1L, 2L)), ("b", Set(1L, 3L))).get === List(1) //    (1) + ()
            _ <- im(("a", Set(1L, 2L)), ("b", Set(2L, 3L))).get === List(1, 5) // (1) + (5)
            _ <- im(("a", Set(1L, 2L)), ("b", Set(3L, 4L))).get === List(1, 6) // (1) + (6)

            _ <- im(("a", Set(2L)), ("b", Set(1L, 2L))).get === List(1, 2, 4) //  (1, 2) + (4)
            _ <- im(("a", Set(2L)), ("b", Set(1L, 3L))).get === List(1, 2) //     (1, 2) + ()
            _ <- im(("a", Set(2L)), ("b", Set(2L, 3L))).get === List(1, 2, 5) //  (1, 2) + (5)
            _ <- im(("a", Set(2L)), ("b", Set(3L, 4L))).get === List(1, 2, 6) //  (1, 2) + (6)
          } yield ()
        }

        "Pairs in different namespaces" - core { implicit conn =>
          // Cardinality one + many input molecule
          val im = m(Ns.int.str_(?).Refs1.ints1_(?))
          for {
            _ <- Ns.int.str.Refs1.ints1 insert List(
              (1, "a", Set(1, 2)),
              (2, "a", Set(2, 3)),
              (3, "a", Set(3, 4)),

              (4, "b", Set(1, 2)),
              (5, "b", Set(2, 3)),
              (6, "b", Set(3, 4))
            )

            _ <- Ns.int.str$ insert List(
              (7, Some("a")),
              (8, None)
            )

            _ <- im(("a", Set(1)), ("b", Set(2))).get === List(1, 4, 5) //      (1) + (4, 5)
            _ <- im(("a", Set(1)), ("b", Set(3))).get === List(1, 5, 6) //      (1) + (5, 6)
            _ <- im(("a", Set(1)), ("b", Set(4))).get === List(1, 6) //         (1) + (6)
            _ <- im(("a", Set(1)), ("b", Set(5))).get === List(1) //            (1) + ()

            _ <- im(("a", Set(1, 2)), ("b", Set(1))).get === List(1, 4) //     (1) + (4)
            _ <- im(("a", Set(1, 2)), ("b", Set(2))).get === List(1, 4, 5) //  (1) + (4, 5)
            _ <- im(("a", Set(1, 2)), ("b", Set(3))).get === List(1, 5, 6) //  (1) + (5, 6)
            _ <- im(("a", Set(1, 2)), ("b", Set(4))).get === List(1, 6) //     (1) + (6)
            _ <- im(("a", Set(1, 2)), ("b", Set(5))).get === List(1) //        (1) + ()

            _ <- im(("a", Set(2)), ("b", Set(1))).get === List(1, 2, 4) //      (1, 2) + (4)
            _ <- im(("a", Set(2)), ("b", Set(3))).get === List(1, 2, 5, 6) //   (1, 2) + (5, 6)
            _ <- im(("a", Set(2)), ("b", Set(4))).get === List(1, 2, 6) //      (1, 2) + (6)
            _ <- im(("a", Set(2)), ("b", Set(5))).get === List(1, 2) //         (1, 2) + ()


            _ <- im(("a", Set(1)), ("b", Set(1, 2))).get === List(1, 4) //     (1) + (4)
            _ <- im(("a", Set(1)), ("b", Set(1, 3))).get === List(1) //        (1) + ()
            _ <- im(("a", Set(1)), ("b", Set(2, 3))).get === List(1, 5) //     (1) + (5)
            _ <- im(("a", Set(1)), ("b", Set(3, 4))).get === List(1, 6) //     (1) + (6)

            _ <- im(("a", Set(1, 2)), ("b", Set(1, 3))).get === List(1) //    (1) + ()
            _ <- im(("a", Set(1, 2)), ("b", Set(2, 3))).get === List(1, 5) // (1) + (5)
            _ <- im(("a", Set(1, 2)), ("b", Set(3, 4))).get === List(1, 6) // (1) + (6)

            _ <- im(("a", Set(2)), ("b", Set(1, 2))).get === List(1, 2, 4) //  (1, 2) + (4)
            _ <- im(("a", Set(2)), ("b", Set(1, 3))).get === List(1, 2) //     (1, 2) + ()
            _ <- im(("a", Set(2)), ("b", Set(2, 3))).get === List(1, 2, 5) //  (1, 2) + (5)
            _ <- im(("a", Set(2)), ("b", Set(3, 4))).get === List(1, 2, 6) //  (1, 2) + (6)
          } yield ()
        }
      }


      "Expressions" - {

        "Negation" - core { implicit conn =>
          for {
            _ <- pairsData
            _ <- m(Ns.int.str_(?).longs.not(?))("a", Set(1L)).get === List((2, Set(2L, 3L)), (3, Set(3L, 4L)))
            _ <- m(Ns.int.str_(?).longs.not(?))("a", Set(2L)).get === List((3, Set(3L, 4L)))
            _ <- m(Ns.int.str_(?).longs.not(?))("a", Set(1L, 2L)).get === List((2, Set(2L, 3L)), (3, Set(3L, 4L)))
            _ <- m(Ns.int.str_(?).longs.not(?))("a", Set(1L, 3L)).get === List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (3, Set(3L, 4L)))
            // Same as
            _ <- m(Ns.int.str_.not(?).longs.not(?))("b", Set(1L)).get === List((2, Set(2L, 3L)), (3, Set(3L, 4L)))
            _ <- m(Ns.int.str_.not(?).longs.not(?))("b", Set(2L)).get === List((3, Set(3L, 4L)))
            _ <- m(Ns.int.str_.not(?).longs.not(?))("b", Set(1L, 2L)).get === List((2, Set(2L, 3L)), (3, Set(3L, 4L)))
            _ <- m(Ns.int.str_.not(?).longs.not(?))("b", Set(1L, 3L)).get === List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (3, Set(3L, 4L)))

            _ <- m(Ns.int.str_.not(?).longs(?))("b", Set(1L)).get === List((1, Set(1L, 2L)))
            _ <- m(Ns.int.str_.not(?).longs(?))("b", Set(2L)).get === List((1, Set(1L, 2L)), (2, Set(2L, 3L)))
            _ <- m(Ns.int.str_.not(?).longs(?))("b", Set(1L, 2L)).get === List((1, Set(1L, 2L)))
            _ <- m(Ns.int.str_.not(?).longs(?))("b", Set(1L, 3L)).get === Nil


            // Applying multiple pairs to input molecules with expression not allowed

            //            // Card one
            //            (m(Ns.int.str_.not(?).longs(?)) (Seq(("a", Set(1L)), ("b", Set(2L)))).get must throwA[Molecule_2_Exception])
            //            .message === "Got the exception molecule.core.api.exception.Molecule_2_Exception: " +
            //              "Can't apply multiple pairs to input attributes with one or more expressions (<, >, <=, >=, !=)"
            //
            //               // Card many
            //               (m(Ns.int.str_(?).longs.not(?))(Seq(("a", Set(1L)), ("b", Set(2L)))).get must throwA[Molecule_2_Exception])
            //                 .message === "Got the exception molecule.core.api.exception.Molecule_2_Exception: " +
            //                 "Can't apply multiple pairs to input attributes with one or more expressions (<, >, <=, >=, !=)"
            //
            //            // Card one + many
            //            (m(Ns.int.str_.not(?).longs.not(?)) (Seq(("a", Set(1L)), ("b", Set(2L)))).get must throwA[Molecule_2_Exception])
            //            .message === "Got the exception molecule.core.api.exception.Molecule_2_Exception: " +
            //              "Can't apply multiple pairs to input attributes with one or more expressions (<, >, <=, >=, !=)"
          } yield ()
        }

        "Comparison" - core { implicit conn =>
          for {
            _ <- pairsData

            // 1 comparison on card-one attribute

            // "a"
            _ <- m(Ns.int.str_.<(?).longs(?))("b", Set(1L)).get === List((1, Set(1L, 2L)))
            _ <- m(Ns.int.str_.<(?).longs(?))("b", Set(2L)).get === List((1, Set(1L, 2L)), (2, Set(2L, 3L)))
            _ <- m(Ns.int.str_.<(?).longs(?))("b", Set(1L, 2L)).get === List((1, Set(1L, 2L)))

            // "a" + "b"
            _ <- m(Ns.int.str_.<=(?).longs(?))("b", Set(1L)).get === List((1, Set(1L, 2L)), (4, Set(1L, 2L)))
            _ <- m(Ns.int.str_.<=(?).longs(?))("b", Set(2L)).get === List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (4, Set(1L, 2L)), (5, Set(2L, 3L)))
            _ <- m(Ns.int.str_.<=(?).longs(?))("b", Set(1L, 2L)).get === List((1, Set(1L, 2L)), (4, Set(1L, 2L)))


            // 1 comparison on card-many attribute

            _ <- m(Ns.int.str_(?).longs.<(?))("a", Set(1L)).get === Nil
            _ <- m(Ns.int.str_(?).longs.<(?))("a", Set(2L)).get === List((1, Set(1L))) // Note that 2L is filtered out
            _ <- m(Ns.int.str_(?).longs.<(?))("a", Set(3L)).get === List((1, Set(1L, 2L)), (2, Set(2L)))

            //            // Can't apply multiple values to comparison function
            //            (m(Ns.int.str_(?).longs.<(?)) ("a", Set(2L, 3L)).get must throwA[MoleculeException])
            //            .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
            //              "Can't apply multiple values to comparison function."
          } yield ()
        }

        "Multiple expressions" - core { implicit conn =>
          for {
            _ <- pairsData
            _ <- m(Ns.int.str_.>(?).longs.>=(?))("a", Set(1L)).get === List((4, Set(1L, 2L)), (5, Set(2L, 3L)), (6, Set(3L, 4L)))
            _ <- m(Ns.int.str_.>(?).longs.>=(?))("a", Set(2L)).get === List((4, Set(2L)), (5, Set(2L, 3L)), (6, Set(3L, 4L)))
            _ <- m(Ns.int.str_.>(?).longs.>=(?))("a", Set(3L)).get === List((5, Set(3L)), (6, Set(3L, 4L)))
            _ <- m(Ns.int.str_.>("a").longs.>=(3L)).get === List((5, Set(3L)), (6, Set(3L, 4L)))

            // Filtered values returned in comparison matches
            _ <- m(Ns.int.str_(?).longs.<(?))("a", Set(2L)).get === List((1, Set(1L))) // 2 not returned
          } yield ()
        }
      }
    }


    "Groups" - {

      // Cardinality one + many input molecule
      val im = m(Ns.int.str_(?).longs_(?))

      val data = List(
        (1, Some("a"), Some(Set(1L))),
        (2, Some("a"), Some(Set(2L))),
        (3, Some("b"), Some(Set(2L))),
        (4, Some("a"), None),
        (5, Some("b"), None),
        (6, Some("b"), None),
        (7, None, Some(Set(1L))),
        (8, None, Some(Set(2L))),
        (9, None, Some(Set(2L))),
        (10, None, None)
      )

      "Empty group(s)" - core { implicit conn =>
        for {
          _ <- Ns.int.str$.longs$ insert data

          // Empty group matches non-asserted attribute

          _ <- im(Nil, Nil).get === List(10)

          _ <- im(List("a"), Nil).get === List(4)
          _ <- im(List("b"), Nil).get === List(5, 6)
          _ <- im(List("a", "b"), Nil).get === List(4, 5, 6)

          _ <- im(Nil, List(Set(1L))).get === List(7)
          _ <- im(Nil, List(Set(2L))).get === List(8, 9)
          _ <- im(Nil, List(Set(1L), Set(2L))).get === List(7, 8, 9)
        } yield ()
      }

      "AND" - core { implicit conn =>
        for {
          _ <- Ns.int.str$.longs$ insert data

          // AND semantics between groups

          _ <- im(List("a"), List(Set(1L))).get === List(1)
          _ <- im(List("a"), List(Set(2L))).get === List(2)
          _ <- im(List("a"), List(Set(1L, 2L))).get === Nil
          _ <- im(List("a"), List(Set(1L), Set(2L))).get === List(1, 2)

          _ <- im(List("b"), List(Set(1L))).get === Nil
          _ <- im(List("b"), List(Set(2L))).get === List(3)
          _ <- im(List("b"), List(Set(1L, 2L))).get === Nil

          _ <- im(List("b"), List(Set(1L), Set(2L))).get === List(3)

          _ <- im(List("a", "b"), List(Set(1L))).get === List(1)
          _ <- im(List("a", "b"), List(Set(2L))).get === List(2, 3)
          _ <- im(List("a", "b"), List(Set(1L), Set(2L))).get === List(1, 2, 3)


          // Same, with Logic syntax

          _ <- im("a" and Set(1L)).get === List(1)
          _ <- im("a" and Set(2L)).get === List(2)
          _ <- im("a" and (Set(1L) or Set(2L))).get === List(1, 2)
          _ <- im("b" and Set(1L)).get === Nil
          _ <- im("b" and Set(2L)).get === List(3)
          _ <- im("b" and (Set(1L) or Set(2L))).get === List(3)
          _ <- im(("a" or "b") and Set(1L)).get === List(1)
          _ <- im(("a" or "b") and Set(2L)).get === List(2, 3)
          _ <- im(("a" or "b") and (Set(1L) or Set(2L))).get === List(1, 2, 3)
        } yield ()
      }
    }
  }
}