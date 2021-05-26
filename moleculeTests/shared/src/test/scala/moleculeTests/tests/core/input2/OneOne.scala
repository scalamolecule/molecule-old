package moleculeTests.tests.core.input2

import molecule.datomic.api.in2_out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object OneOne extends AsyncTestSuite {

  lazy val tests = Tests {

    "Pairs" - {

      // Cardinality one + one input molecule
      val im = m(Ns.int.str_(?).long_(?))

      val data = List(
        (1, Some("a"), Some(1L)),
        (2, Some("a"), Some(2L)),
        (3, Some("b"), Some(2L)),
        (4, None, None)
      )

      "4 syntaxes" - core { implicit conn =>
        for {
          _ <- Ns.int.str$.long$ insert data

          // 4 syntaxes for applying a pair of values
          _ <- im("b", 2L).get === List(3) // when only 1 pair
          _ <- im(("a", 3L), ("b", 1L)).get === Nil
          _ <- im(("a" and 3L) or ("b" and 1L)).get === Nil
          _ <- im(List(("a", 3L), ("b", 1L))).get === Nil
        } yield ()
      }

      "0 pairs" - core { implicit conn =>
        for {
          _ <- Ns.int.str$.long$ insert data

          // No input pairs match pairs of non-asserted attributes
          _ <- im(Nil).get === List(4)
        } yield ()
      }

      "1 pair" - core { implicit conn =>
        for {
          _ <- Ns.int.str$.long$ insert data

          _ <- im("a", 1L).get === List(1)
          _ <- im("a", 2L).get === List(2)

          _ <- im("b", 1L).get === Nil
          _ <- im("b", 2L).get === List(3)
        } yield ()
      }

      "Multiple pairs" - core { implicit conn =>
        val (p1, p2, p0, p3) = (
          ("a", 1L),
          ("a", 2L),
          ("b", 1L),
          ("b", 2L)
        )

        for {
          _ <- Ns.int.str$.long$ insert data

          // Redundant pairs are truncated
          _ <- im(p1, p1).get === List(1)
          // Becomes ame as
          _ <- im(p1).get === List(1)


          _ <- im(p1, p2).get === List(1, 2)
          _ <- im(p1, p0).get === List(1)
          _ <- im(p1, p3).get === List(1, 3)

          _ <- im(p2, p1).get === List(1, 2)
          _ <- im(p2, p0).get === List(2)
          _ <- im(p2, p3).get === List(2, 3)

          _ <- im(p0, p1).get === List(1)
          _ <- im(p0, p2).get === List(2)
          _ <- im(p0, p3).get === List(3)

          _ <- im(p3, p1).get === List(1, 3)
          _ <- im(p3, p2).get === List(2, 3)
          _ <- im(p3, p0).get === List(3)
        } yield ()
      }
    }


    "Groups" - {

      // Cardinality one + one input molecule
      val im = m(Ns.int.str_(?).long_(?))

      val data = List(
        (0, None, None),
        (1, Some("a"), Some(1L)),
        (2, Some("a"), Some(2L)),
        (3, Some("b"), Some(2L)),
        (4, Some("a"), None),
        (5, Some("b"), None),
        (6, Some("b"), None),
        (7, None, Some(1L)),
        (8, None, Some(2L)),
        (9, None, Some(2L))
      )

      "Empty group(s)" - core { implicit conn =>
        for {
          _ <- Ns.int.str$.long$ insert data

          // Empty group matches non-asserted attribute
          _ <- im(Nil, Nil).get === List(0)

          _ <- im(List("a"), Nil).get === List(4)
          _ <- im(List("b"), Nil).get === List(5, 6)
          _ <- im(List("a", "b"), Nil).get === List(4, 5, 6)

          _ <- im(Nil, List(1L)).get === List(7)
          _ <- im(Nil, List(2L)).get === List(8, 9)
          _ <- im(Nil, List(1L, 2L)).get === List(7, 8, 9)
        } yield ()
      }

      "AND" - core { implicit conn =>
        for {
          _ <- Ns.int.str$.long$ insert data

          // AND semantics between groups
          _ <- im(List("a"), List(1L)).get === List(1)
          _ <- im(List("a"), List(2L)).get === List(2)
          _ <- im(List("a"), List(1L, 2L)).get === List(1, 2)

          _ <- im(List("b"), List(1L)).get === Nil
          _ <- im(List("b"), List(2L)).get === List(3)
          _ <- im(List("b"), List(1L, 2L)).get === List(3)

          _ <- im(List("a", "b"), List(1L)).get === List(1)
          _ <- im(List("a", "b"), List(2L)).get === List(2, 3)
          _ <- im(List("a", "b"), List(1L, 2L)).get === List(1, 2, 3)

          // Same, with Logic syntax
          _ <- im("a" and 1L).get === List(1)
          _ <- im("a" and 2L).get === List(2)
          _ <- im("a" and (1L or 2L)).get === List(1, 2)
          _ <- im("b" and 1L).get === Nil
          _ <- im("b" and 2L).get === List(3)
          _ <- im("b" and (1L or 2L)).get === List(3)
          _ <- im(("a" or "b") and 1L).get === List(1)
          _ <- im(("a" or "b") and 2L).get === List(2, 3)
          _ <- im(("a" or "b") and (1L or 2L)).get === List(1, 2, 3)
        } yield ()
      }
    }
  }
}