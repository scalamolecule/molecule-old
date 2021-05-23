package molecule.tests.core.ref.composite

import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out4._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object CompositeExpression extends AsyncTestSuite {

  lazy val tests = Tests {

    "Constraints apply to merged model" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ns.str.int insert List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        )

        // Without constraints
        _ <- m(Ref2.int2.str2 + Ns.str.int).get.map(_.sorted ==> List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ))

        // One constraint
        _ <- m(Ref2.int2.>(1).str2 + Ns.str.int).get.map(_.sorted ==> List(
          ((2, "b"), ("bb", 22))
        ))
        // Other constraint
        _ <- m(Ref2.int2.str2 + Ns.str.int.<(20)).get.map(_.sorted ==> List(
          ((1, "a"), ("aa", 11))
        ))

        // Mutually exclusive constraints
        _ <- m(Ref2.int2.>(1).str2 + Ns.str.int.<(20)).get.map(_.sorted ==> Nil)

        // Constraints with all-matching data
        _ <- m(Ref2.int2.<(2).str2 + Ns.str.int.<(20)).get.map(_.sorted ==> List(
          ((1, "a"), ("aa", 11))
        ))

        _ <- m(Ref2.int2_.<(2).str2 + Ns.int_.<(20)).get.map(_.sorted ==> List(
          "a"
        ))

        _ <- m(Ref2.int2_.<(2) + Ns.str.int_.<(20)).get.map(_.sorted ==> List(
          "aa"
        ))
      } yield ()
    }

    "Null values" - core { implicit conn =>
      for {

        // Composite data
        _ <- Ref2.int2.str2 + Ns.str.int insert List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        )

        // Add partial composite data
        _ <- Ref2.int2.str2 + Ns.int insert List(
          ((3, "c"), 33)
        )

        // Non-composite query gets all data
        _ <- m(Ref2.int2.str2).get.map(_.sorted ==> List(
          (1, "a"),
          (2, "b"),
          (3, "c")
        ))

        // Composite query gets composite data only that has a `Ns.str` value too
        _ <- m(Ref2.int2.str2 + Ns.str.int).get.map(_.sorted ==> List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ))
        _ <- m(Ref2.int2.str2 + Ns.str).get.map(_.sorted ==> List(
          ((1, "a"), "aa"),
          ((2, "b"), "bb")
        ))

        // Composite data with `Ns.int` values retrieve all 3 entities
        _ <- m(Ref2.int2.str2 + Ns.int).get.map(_.sorted ==> List(
          ((1, "a"), 11),
          ((2, "b"), 22),
          ((3, "c"), 33)
        ))

        // Composite data that _doesn't_ have a `Ns.str` value retrieve only the last entity
        _ <- m(Ref2.int2.str2 + Ns.str_(Nil).int).get.map(_.sorted ==> List(
          ((3, "c"), 33)
        ))
      } yield ()
    }
  }
}