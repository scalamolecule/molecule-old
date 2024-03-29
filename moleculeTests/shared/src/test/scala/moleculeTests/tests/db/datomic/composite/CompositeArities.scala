package moleculeTests.tests.db.datomic.composite

import molecule.datomic.api.out4._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._

object CompositeArities extends AsyncTestSuite {

  lazy val tests = Tests {

    "1 + 1" - core { implicit conn =>
      for {
        // Composite of two single-value molecules
        List(e1, e2) <- Ref2.int2 + Ns.int insert Seq(
          // Two rows of data
          (1, 11),
          (2, 22)
        ) map (_.eids)

        // Two entities created
        _ <- e1.graph.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/int" -> 11,
          ":Ref2/int2" -> 1
        ))
        _ <- e2.graph.map(_ ==> Map(
          ":db/id" -> e2,
          ":Ns/int" -> 22,
          ":Ref2/int2" -> 2
        ))

        // Queries via each namespace
        _ <- Ref2.int2.a1.get.map(_ ==> Seq(1, 2))
        _ <- Ns.int.a1.get.map(_ ==> Seq(11, 22))

        // Composite query
        _ <- (Ref2.int2.a1 + Ns.int).get.map(_ ==> Seq(
          (1, 11),
          (2, 22)
        ))
      } yield ()
    }

    "1 + 2" - core { implicit conn =>
      for {
        // Composite of Molecule1 + Molecule2
        List(e1, e2) <- (Ref2.int2 + Ns.int.str).insert(Seq(
          // Two rows of data
          (1, (11, "aa")),
          (2, (22, "bb"))
        )).map(_.eids)

        // Two entities created
        _ <- e1.graph.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/int" -> 11,
          ":Ns/str" -> "aa",
          ":Ref2/int2" -> 1
        ))
        _ <- e2.graph.map(_ ==> Map(
          ":db/id" -> e2,
          ":Ns/int" -> 22,
          ":Ns/str" -> "bb",
          ":Ref2/int2" -> 2
        ))

        // Queries via each namespace
        _ <- Ref2.int2.a1.get.map(_ ==> Seq(
          1,
          2
        ))
        _ <- Ns.int.a1.str.get.map(_ ==> Seq(
          (11, "aa"),
          (22, "bb")
        ))

        // Composite query
        _ <- (Ref2.int2.a1 + Ns.int.str).get.map(_ ==> Seq(
          (1, (11, "aa")),
          (2, (22, "bb"))
        ))
      } yield ()
    }

    "2 + 1" - core { implicit conn =>
      for {
        // Composite of Molecule2 + Molecule1
        List(e1, e2) <- Ref2.int2.str2 + Ns.int insert Seq(
          // Two rows of data
          ((1, "a"), 11),
          ((2, "b"), 22)
        ) map (_.eids)

        // Two entities created
        _ <- e1.graph.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/int" -> 11,
          ":Ref2/int2" -> 1,
          ":Ref2/str2" -> "a"
        ))
        _ <- e2.graph.map(_ ==> Map(
          ":db/id" -> e2,
          ":Ns/int" -> 22,
          ":Ref2/int2" -> 2,
          ":Ref2/str2" -> "b"
        ))

        // Queries via each namespace
        _ <- Ref2.int2.a1.str2.get.map(_ ==> Seq(
          (1, "a"),
          (2, "b")
        ))
        _ <- Ns.int.a1.get.map(_ ==> Seq(
          11,
          22
        ))

        // Composite query
        _ <- (Ref2.int2.str2 + Ns.int.a1).get.map(_ ==> Seq(
          ((1, "a"), 11),
          ((2, "b"), 22)
        ))
      } yield ()
    }

    "2 + 2" - core { implicit conn =>
      for {
        // Composite of Molecule2 + Molecule2
        List(e1, e2) <- Ref2.int2.str2 + Ns.str.int insert Seq(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ) map (_.eids)

        // Two entities created
        _ <- e1.graph.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ns/int" -> 11,
          ":Ns/str" -> "aa",
          ":Ref2/int2" -> 1,
          ":Ref2/str2" -> "a"
        ))
        _ <- e2.graph.map(_ ==> Map(
          ":db/id" -> e2,
          ":Ns/int" -> 22,
          ":Ns/str" -> "bb",
          ":Ref2/int2" -> 2,
          ":Ref2/str2" -> "b"
        ))

        // Queries via each namespace
        _ <- Ref2.int2.a1.str2.get.map(_ ==> List(
          (1, "a"),
          (2, "b")
        ))
        _ <- Ns.str.int.a1.get.map(_ ==> List(
          ("aa", 11),
          ("bb", 22)
        ))

        // Composite query
        _ <- (Ref2.int2.a1.str2 + Ns.str.int).get.map(_ ==> List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ))
      } yield ()
    }


    "2 + 3 (2+1tx)" - core { implicit conn =>
      for {
        // Composite of Molecule2 + Molecule1 + Tx meta data
        // Note that tx meta attributes have underscore/are tacit in order not to affect the type of input
        tx <- (Ref2.int2.str2 + Ref1.str1.int1.Tx(Ns.str_("Tx meta data"))) insert Seq(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        )
        txId = tx.tx
        List(e1, e2) = tx.eids

        _ <- txId.graph.map(_ ==> Map(
          ":db/id" -> txId,
          ":db/txInstant" -> tx.txInstant,
          ":Ns/str" -> "Tx meta data"
        ))
        _ <- e1.graph.map(_ ==> Map(
          ":db/id" -> e1,
          ":Ref1/int1" -> 11,
          ":Ref1/str1" -> "aa",
          ":Ref2/int2" -> 1,
          ":Ref2/str2" -> "a"
        ))
        _ <- e2.graph.map(_ ==> Map(
          ":db/id" -> e2,
          ":Ref1/int1" -> 22,
          ":Ref1/str1" -> "bb",
          ":Ref2/int2" -> 2,
          ":Ref2/str2" -> "b"
        ))

        // Queries via one namespace
        _ <- Ref2.int2.a1.str2.get.map(_ ==> List(
          (1, "a"),
          (2, "b")
        ))
        // .. including transaction meta data
        // Note how transaction meta data is fetched for all entities ("rows") saved in the same transaction
        _ <- Ref2.int2.a1.str2.Tx(Ns.str).get.map(_ ==> List(
          (1, "a", "Tx meta data"),
          (2, "b", "Tx meta data")
        ))

        // Queries via other namespace
        _ <- Ref1.str1.int1.a1.get.map(_ ==> List(
          ("aa", 11),
          ("bb", 22)
        ))
        // .. including transaction meta data
        _ <- Ref1.str1.int1.a1.Tx(Ns.str).get.map(_ ==> List(
          ("aa", 11, "Tx meta data"),
          ("bb", 22, "Tx meta data")
        ))

        // Transaction meta data alone can be accessed through tacit attributes of namespaces
        _ <- Ref2.int2_.Tx(Ns.str).get.map(_ ==> List("Tx meta data"))
        _ <- Ref1.int1_.Tx(Ns.str).get.map(_ ==> List("Tx meta data"))


        // Composite query
        _ <- (Ref2.int2.a1.str2 + Ref1.str1.int1).get.map(_ ==> List(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        ))
        // .. including transaction meta data
        _ <- (Ref2.int2.a1.str2 + Ref1.str1.int1.Tx(Ns.str)).get.map(_ ==> List(
          ((1, "a"), ("aa", 11, "Tx meta data")),
          ((2, "b"), ("bb", 22, "Tx meta data"))
        ))
      } yield ()
    }
  }
}