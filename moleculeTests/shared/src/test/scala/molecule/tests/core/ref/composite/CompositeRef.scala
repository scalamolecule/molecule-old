package molecule.tests.core.ref.composite

import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out4._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object CompositeRef extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one ref" - core { implicit conn =>
      for {
        tx <- Ref2.int2.str2 + Ns.str.Ref1.int1 insert List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        )
        List(e1, r1, e2, r2) = tx.eids

        //    // First entity (including referenced entity)
        //    e1.touchList === List(
        //      ":db/id" -> e1,
        //      ":Ns/ref1" -> List((":db/id", r1), (":Ref1/int1", 11)),
        //      ":Ns/str" -> "aa",
        //      ":Ref2/int2" -> 1,
        //      ":Ref2/str2" -> "a"
        //    )
        //    // First referenced entity (same as we see included above)
        //    r1.touchList === List(
        //      ":db/id" -> r1,
        //      ":Ref1/int1" -> 11
        //    )
        //
        //    // Second entity (including referenced entity)
        //    e2.touchList === List(
        //      ":db/id" -> e2,
        //      ":Ns/ref1" -> List((":db/id", r2), (":Ref1/int1", 22)),
        //      ":Ns/str" -> "bb",
        //      ":Ref2/int2" -> 2,
        //      ":Ref2/str2" -> "b"
        //    )
        //    // Second referenced entity (same as we see included above)
        //    r2.touchList === List(
        //      ":db/id" -> r2,
        //      ":Ref1/int1" -> 22
        //    )

        // Queries via each namespace
        _ <- Ref2.int2.str2.get.map(_.sorted ==> List(
          (1, "a"),
          (2, "b")
        ))
        _ <- Ns.str.Ref1.int1.get.map(_.sorted ==> List(
          ("aa", 11),
          ("bb", 22)
        ))

        // Composite query
        _ <- m(Ref2.int2.str2 + Ns.str.Ref1.int1).get.map(_.sorted ==> List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ))
      } yield ()
    }

    "Card-many ref - one value" - core { implicit conn =>
      for {
        tx <- Ref2.int2.str2 + Ns.str.Refs1.int1 insert List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        )
        List(e1, r1, e2, r2) = tx.eids

        //    // First entity (including referenced entity)
        //    e1.touchList === List(
        //      ":db/id" -> e1,
        //      ":Ns/refs1" -> List(List((":db/id", r1), (":Ref1/int1", 11))),
        //      ":Ns/str" -> "aa",
        //      ":Ref2/int2" -> 1,
        //      ":Ref2/str2" -> "a"
        //    )
        //
        //    // First referenced entity (same as we see included above)
        //    r1.touchList === List(
        //      ":db/id" -> r1,
        //      ":Ref1/int1" -> 11
        //    )
        //
        //    // Second entity (including referenced entity)
        //    e2.touchList === List(
        //      ":db/id" -> e2,
        //      ":Ns/refs1" -> List(List((":db/id", r2), (":Ref1/int1", 22))),
        //      ":Ns/str" -> "bb",
        //      ":Ref2/int2" -> 2,
        //      ":Ref2/str2" -> "b"
        //    )
        //    // Second referenced entity (same as we see included above)
        //    r2.touchList === List(
        //      ":db/id" -> r2,
        //      ":Ref1/int1" -> 22
        //    )

        // Queries via each namespace
        _ <- Ref2.int2.str2.get.map(_.sorted ==> List(
          (1, "a"),
          (2, "b")
        ))
        _ <- Ns.str.Refs1.int1.get.map(_.sorted ==> List(
          ("aa", 11),
          ("bb", 22)
        ))

        // Composite query
        _ <- m(Ref2.int2.str2 + Ns.str.Refs1.int1).get.map(_.sorted ==> List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ))
      } yield ()
    }

    "Card-many ref - one value 2" - core { implicit conn =>
      for {

        tx <- Ref2.int2.str2 + Ns.Refs1.int1 insert List(
          ((1, "a"), 11),
          ((2, "b"), 22)
        )
        List(e1, r1, e2, r2) = tx.eids

        //    // First entity (including referenced entity)
        //    e1.touchList === List(
        //      ":db/id" -> e1,
        //      ":Ns/refs1" -> List(List((":db/id", r1), (":Ref1/int1", 11))),
        //      ":Ref2/int2" -> 1,
        //      ":Ref2/str2" -> "a"
        //    )
        //
        //    // First referenced entity (same as we see included above)
        //    r1.touchList === List(
        //      ":db/id" -> r1,
        //      ":Ref1/int1" -> 11
        //    )
        //
        //    // Second entity (including referenced entity)
        //    e2.touchList === List(
        //      ":db/id" -> e2,
        //      ":Ns/refs1" -> List(List((":db/id", r2), (":Ref1/int1", 22))),
        //      ":Ref2/int2" -> 2,
        //      ":Ref2/str2" -> "b"
        //    )
        //    // Second referenced entity (same as we see included above)
        //    r2.touchList === List(
        //      ":db/id" -> r2,
        //      ":Ref1/int1" -> 22
        //    )

        // Queries via each namespace
        _ <- Ref2.int2.str2.get.map(_.sorted ==> List(
          (1, "a"),
          (2, "b")
        ))

        // Composite query
        _ <- m(Ref2.int2.str2 + Ns.Refs1.int1).get.map(_.sorted ==> List(
          ((1, "a"), 11),
          ((2, "b"), 22)
        ))
        _ <- m(Ref2.int2.str2 + Ns.refs1).get === List(
          ((1, "a"), Set(r1)),
          ((2, "b"), Set(r2))
        )
      } yield ()
    }
  }
}