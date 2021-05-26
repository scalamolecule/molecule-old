package moleculeTests.tests.core.txMetaData

import molecule.datomic.api.in3_out10._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object MetaSave extends AsyncTestSuite {

  lazy val tests = Tests {

    "Basic" - core { implicit conn =>
      for {
        // `tx` being tacit or mandatory has same effect
        // tx meta attributes can be in any mode
        txR <- Ns.int(1).Tx(Ns.str_("str tacit")).save
        tx1 = txR.tx
        t1 = txR.t
        //      tx1 = Ns.int(1).Tx(Ns.str_("str tacit")).save.tx
        txR2 <- Ns.int(2).Tx(Ns.str("str mandatory")).save
        txR3 <- Ns.int(3).Tx(Ns.str("attr mandatory")).save
        txR4 <- Ns.int(4).Tx(Ns.str_("attr tacit")).save
        txR5 <- Ns.int(5).Tx(Ns.str$(Some("attr optional with value"))).save
        txR6 <- Ns.int(6).Tx(Ns.str$(None)).save // attr optional without value

        tx2 = txR2.tx
        tx3 = txR3.tx
        tx4 = txR4.tx
        tx5 = txR5.tx
        tx6 = txR6.tx

        // Optional tx meta data
        _ <- Ns.int.Tx(Ns.str$).get.map(_.sortBy(_._1) ==> List(
          (1, Some("str tacit")),
          (2, Some("str mandatory")),
          (3, Some("attr mandatory")),
          (4, Some("attr tacit")),
          (5, Some("attr optional with value")),
          (6, None) // attr optional without value
        ))


        // Mandatory tx meta data
        _ <- Ns.int.Tx(Ns.str).get.map(_.sortBy(_._1) ==> List(
          (1, "str tacit"),
          (2, "str mandatory"),
          (3, "attr mandatory"),
          (4, "attr tacit"),
          (5, "attr optional with value")
        ))

        // Transactions without tx meta data
        _ <- Ns.int.Tx(Ns.str_(Nil)).get === List(6)

        // Transaction meta data expressions
        _ <- Ns.int.<(3).Tx(Ns.str("str mandatory")).get === List(
          (2, "str mandatory")
        )
        _ <- Ns.int.<(3).Tx(Ns.str.not("attr mandatory")).get === List(
          (1, "str tacit"),
          (2, "str mandatory")
        )

        // Fulltext search only available for Peer
        _ <- if (system == SystemPeer) {
          for {
            _ <- Ns.int.Tx(Ns.str.contains("mandatory")).get.map(_.sortBy(_._1) ==> List(
              (2, "str mandatory"),
              (3, "attr mandatory")
            ))
            res <- Ns.int.<(3).Tx(Ns.str.contains("mandatory")).get === List(
              (2, "str mandatory")
            )
          } yield res
        } else Future.unit

        // tx entity id can be returned too
        _ <- Ns.int.tx.Tx(Ns.str$).get.map(_.sortBy(_._1) ==> List(
          (1, tx1, Some("str tacit")),
          (2, tx2, Some("str mandatory")),
          (3, tx3, Some("attr mandatory")),
          (4, tx4, Some("attr tacit")),
          (5, tx5, Some("attr optional with value")),
          (6, tx6, None) // attr optional without value
        ))
      } yield ()
    }


    "Multiple tx attributes with mixed modes" - core { implicit conn =>
      for {
        // Modes mixed in different transactions
        _ <- Ns.int(1).Tx(Ns.str("a").long_(10L).bool$(Some(false))).save
        _ <- Ns.int(2).Tx(Ns.str_("b").long$(None).bool_(true)).save

        // Expected tx meta data
        _ <- Ns.int.Tx(Ns.str.long$.bool$).get.map(_.sortBy(_._1) ==> List(
          (1, "a", Some(10L), Some(false)),
          (2, "b", None, Some(true))
        ))
      } yield ()
    }


    "Tx refs" - core { implicit conn =>
      for {

        // Saving tx meta data (Ns.str) that references another namespace attribute (Ref1.int1)
        _ <- Ns.int(1).Tx(Ns.str("a").Ref1.int1(10)).save

        // Tx meta data with ref attr
        _ <- Ns.int.Tx(Ns.str.Ref1.int1).get === List(
          (1, "a", 10)
        )

        // Tx meta data
        _ <- Ns.int.Tx(Ns.str).get === List(
          (1, "a")
        )

        // OBS: Ref1.int1 is not asserted with tx entity, but with ref from tx entity!
        _ <- Ns.int.Tx(Ref1.int1).get === Nil

        // Saving multiple tx refs
        _ <- Ns.int(2).Tx(Ns.str("b").Ref1.int1(20).Ref2.int2(200)).save

        // Getting multiple tx refs
        _ <- Ns.int.Tx(Ns.str.Ref1.int1.Ref2.int2).get === List(
          (2, "b", 20, 200)
        )
        _ <- Ns.int.Tx(Ns.str.Ref1.int1).get.map(_.sorted ==> List(
          (1, "a", 10), // First insert matches too
          (2, "b", 20)
        ))
        _ <- Ns.int.Tx(Ns.str).get.map(_.sorted ==> List(
          (1, "a"),
          (2, "b")
        ))
      } yield ()
    }


    "Ref + tx meta data" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Ref1.int1(1).Tx(Ref2.int2(2)).save
        _ <- Ns.int.Ref1.int1.Tx.apply(Ref2.int2).get.map(_.head ==> (0, 1, 2))
      } yield ()
    }


    "Refs + tx meta data" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Ref1.int1(1).Ref2.int2(2).Tx(Ref3.int3(3)).save
        _ <- Ns.int(0).Ref1.int1(1).Ref2.int2(2).Tx(Ref3.int3(3)).get.map(_.head ==> (0, 1, 2, 3))
      } yield ()
    }


    "Ref + tx meta data composites" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Ref1.int1(1).Tx(Ref2.int2(2) + Ref3.int3(3)).save
        _ <- Ns.int(0).Ref1.int1(1).Tx(Ref2.int2(2) + Ref3.int3(3)).get.map(_.head ==> (0, 1, 2, 3))
      } yield ()
    }


    "Refs + tx meta data composites" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Ref1.int1(1).Ref2.int2(2).Tx(Ref3.int3(3) + Ref4.int4(4)).save
        _ <- Ns.int(0).Ref1.int1(1).Ref2.int2(2).Tx(Ref3.int3(3) + Ref4.int4(4)).get.map(_.head ==> (0, 1, 2, 3, 4))
      } yield ()
    }


    "Save tx meta ref with multiple attrs" - core { implicit conn =>
      for {
        _ <- Ns.int(1).Tx(Ns.str("a").Ref1.str1("b").int1(2)).save

        _ <- Ns.int.Tx(Ns.str.Ref1.str1.int1).get === List(
          (1, "a", "b", 2)
        )
      } yield ()
    }


    "Save multiple tx meta refs with multiple attrs" - core { implicit conn =>
      for {
        _ <- Ns.int(1).Tx(Ns.str("a").Ref1.str1("b").int1(2).Ref2.str2("c").int2(3)).save

        _ <- Ns.int.Tx(Ns.str.Ref1.str1.int1.Ref2.str2.int2).get === List(
          (1, "a", "b", 2, "c", 3)
        )
      } yield ()
    }
  }
}