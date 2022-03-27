package moleculeTests.tests.db.datomic.composite

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object CompositeExpression extends AsyncTestSuite {

  lazy val tests = Tests {

    "Constraints apply to merged model" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ns.str.int insert List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        )

        // Without constraints
        _ <- (Ref2.int2.a1.str2 + Ns.str.int).get.map(_ ==> List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ))

        // One constraint
        _ <- (Ref2.int2.>(1).str2 + Ns.str.int).get.map(_ ==> List(
          ((2, "b"), ("bb", 22))
        ))
        // Other constraint
        _ <- (Ref2.int2.str2 + Ns.str.int.<(20)).get.map(_ ==> List(
          ((1, "a"), ("aa", 11))
        ))

        // Mutually exclusive constraints
        _ <- (Ref2.int2.>(1).str2 + Ns.str.int.<(20)).get.map(_ ==> Nil)

        // Constraints with all-matching data
        _ <- (Ref2.int2.<(2).str2 + Ns.str.int.<(20)).get.map(_ ==> List(
          ((1, "a"), ("aa", 11))
        ))

        _ <- (Ref2.int2_.<(2).str2 + Ns.int_.<(20)).get.map(_ ==> List(
          "a"
        ))

        _ <- (Ref2.int2_.<(2) + Ns.str.int_.<(20)).get.map(_ ==> List(
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
        _ <- (Ref2.int2.a1.str2).get.map(_ ==> List(
          (1, "a"),
          (2, "b"),
          (3, "c")
        ))

        // Composite query gets composite data only that has a `Ns.str` value too
        _ <- (Ref2.int2.a1.str2 + Ns.str.int).get.map(_ ==> List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ))
        _ <- (Ref2.int2.a1.str2 + Ns.str).get.map(_ ==> List(
          ((1, "a"), "aa"),
          ((2, "b"), "bb")
        ))

        // Composite data with `Ns.int` values retrieve all 3 entities
        _ <- (Ref2.int2.a1.str2 + Ns.int).get.map(_ ==> List(
          ((1, "a"), 11),
          ((2, "b"), 22),
          ((3, "c"), 33)
        ))

        // Composite data that _doesn't_ have a `Ns.str` value retrieve only the last entity
        _ <- (Ref2.int2.str2 + Ns.str_(Nil).int).get.map(_ ==> List(
          ((3, "c"), 33)
        ))
      } yield ()
    }
  }
}