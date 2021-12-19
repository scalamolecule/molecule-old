package moleculeTests.tests.db.datomic.composite

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object CompositeRef extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one ref" - core { implicit conn =>
      for {
        List(e1, r1, e2, r2) <- Ref2.int2.str2 + Ns.str.Ref1.int1 insert List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ) map (_.eids)

        // First entity (including referenced entity)
        _ <- e1.graph.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/ref1" -> Map(":db/id" -> r1, ":Ref1/int1" -> 11),
          ":Ns/str" -> "aa",
          ":Ref2/int2" -> 1,
          ":Ref2/str2" -> "a"
        ))
        // First referenced entity (same as we see included above)
        _ <- r1.graph.map(_ ==> Map(
          ":db/id" -> r1,
          ":Ref1/int1" -> 11
        ))

        // Second entity (including referenced entity)
        _ <- e2.graph.map(_ ==> Map(
          ":db/id" -> e2,
          ":Ns/ref1" -> Map(":db/id" -> r2, ":Ref1/int1" -> 22),
          ":Ns/str" -> "bb",
          ":Ref2/int2" -> 2,
          ":Ref2/str2" -> "b"
        ))
        // Second referenced entity (same as we see included above)
        _ <- r2.graph.map(_ ==> Map(
          ":db/id" -> r2,
          ":Ref1/int1" -> 22
        ))

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
        List(e1, r1, e2, r2) <- Ref2.int2.str2 + Ns.str.Refs1.int1 insert List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ) map (_.eids)

        // First entity (including referenced entity)
        _ <- e1.graph.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/refs1" -> Set(
            Map(":db/id" -> r1, ":Ref1/int1" -> 11)),
          ":Ns/str" -> "aa",
          ":Ref2/int2" -> 1,
          ":Ref2/str2" -> "a"
        ))

        // First referenced entity (same as we see included above)
        _ <- r1.graph.map(_ ==> Map(
          ":db/id" -> r1,
          ":Ref1/int1" -> 11
        ))

        // Second entity (including referenced entity)
        _ <- e2.graph.map(_ ==> Map(
          ":db/id" -> e2,
          ":Ns/refs1" -> Set(
            Map(":db/id" -> r2, ":Ref1/int1" -> 22)),
          ":Ns/str" -> "bb",
          ":Ref2/int2" -> 2,
          ":Ref2/str2" -> "b"
        ))
        // Second referenced entity (same as we see included above)
        _ <- r2.graph.map(_ ==> Map(
          ":db/id" -> r2,
          ":Ref1/int1" -> 22
        ))

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
        List(e1, r1, e2, r2) <- Ref2.int2.str2 + Ns.Refs1.int1 insert List(
          ((1, "a"), 11),
          ((2, "b"), 22)
        ) map (_.eids)

        // First entity (including referenced entity)
        _ <- e1.graph.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/refs1" -> Set(
            Map(":db/id" -> r1, ":Ref1/int1" -> 11)),
          ":Ref2/int2" -> 1,
          ":Ref2/str2" -> "a"
        ))

        // First referenced entity (same as we see included above)
        _ <- r1.graph.map(_ ==> Map(
          ":db/id" -> r1,
          ":Ref1/int1" -> 11
        ))

        // Second entity (including referenced entity)
        _ <- e2.graph.map(_ ==> Map(
          ":db/id" -> e2,
          ":Ns/refs1" -> Set(
            Map(":db/id" -> r2, ":Ref1/int1" -> 22)),
          ":Ref2/int2" -> 2,
          ":Ref2/str2" -> "b"
        ))
        // Second referenced entity (same as we see included above)
        _ <- r2.graph.map(_ ==> Map(
          ":db/id" -> r2,
          ":Ref1/int1" -> 22
        ))

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
        _ <- m(Ref2.int2.str2 + Ns.refs1).get.map(_ ==> List(
          ((1, "a"), Set(r1)),
          ((2, "b"), Set(r2))
        ))
      } yield ()
    }


    "Ref in first group" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Ref1.int1 + Ref2.int2.str2) insert List(
          (("A", 1), (11, "a")),
          (("B", 2), (22, "b"))
        )

        _ <- m(Ns.str.Ref1.int1 + Ref2.int2.str2).get.map(_.sortBy(_._1) ==> List(
          (("A", 1), (11, "a")),
          (("B", 2), (22, "b"))
        ))
      } yield ()
    }
  }
}