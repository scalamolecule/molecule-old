package moleculeTests.tests.db.datomic.txMetaData

import molecule.datomic.api.in3_out10._
import molecule.datomic.base.ops.QueryOps
import molecule.datomic.base.util.SystemPeerServer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._

object MetaRetract extends AsyncTestSuite {


//  val basisTx = QueryOps.txBase

  lazy val tests = Tests {

    "1 entity, 1 tx meta" - core { implicit conn =>
      for {
        eid <- Ns.int(1).save.map(_.eid)

        // Retract entity with tx meta data
        tx2 <- eid.retract(Ref2.str2("meta")).map(_.tx)

        // What was retracted and with what tx meta data
        // todo
//        _ <- if (system == SystemPeerServer) {
//          Ns.e.int.tx.op.Tx(Ref2.str2).getHistory.map(_.filter(_._3 >= basisTx) ==> List(
//            // 1 was retracted with tx meta data "meta"
//            (eid, 1, tx2, false, "meta")
//          ))
//        } else {
//          Ns.e.int.tx.op.Tx(Ref2.str2).getHistory.map(_ ==> List(
//            // 1 was retracted with tx meta data "meta"
//            (eid, 1, tx2, false, "meta")
//          ))
//        }
        _ <- Ns.e.int.tx.op.Tx(Ref2.str2).getHistory.map(_ ==> List(
          // 1 was retracted with tx meta data "meta"
          (eid, 1, tx2, false, "meta")
        ))
      } yield ()
    }

    "1 entity, 2 tx meta, vararg" - core { implicit conn =>
      for {
        eid <- Ns.int(1).save.map(_.eid)

        // Retract entity with two tx meta data molecules applied as separate arguments
        tx2 <- eid.retract(Ref2.str2("meta2"), Ref1.str1("meta1")).map(_.tx)

        // What was retracted and with what tx meta data
//        _ <- if (system == SystemPeerServer) {
//          Ns.e.int.tx.op.Tx(Ref2.str2 + Ref1.str1).getHistory.map(_.filter(_._3 >= basisTx) ==> List(
//            // 1 was retracted with tx meta data "meta2" and "meta1"
//            (eid, 1, tx2, false, "meta2", "meta1")
//          ))
//        } else {
//          Ns.e.int.tx.op.Tx(Ref2.str2 + Ref1.str1).getHistory.map(_ ==> List(
//            // 1 was retracted with tx meta data "meta2" and "meta1"
//            (eid, 1, tx2, false, "meta2", "meta1")
//          ))
//        }
        _ <- Ns.e.int.tx.op.Tx(Ref2.str2 + Ref1.str1).getHistory.map(_ ==> List(
          // 1 was retracted with tx meta data "meta2" and "meta1"
          (eid, 1, tx2, false, "meta2", "meta1")
        ))
      } yield ()
    }

    "1 entity, 2 tx meta, composite" - core { implicit conn =>
      for {
        eid <- Ns.int(1).save.map(_.eid)

        // Retract entity with tx meta data as composite
        tx2 <- eid.retract(Ref2.str2("meta2") + Ref1.str1("meta1")).map(_.tx)

        // What was retracted and with what tx meta data
//        _ <- if (system == SystemPeerServer) {
//          Ns.e.int.tx.op.Tx(Ref2.str2 + Ref1.str1).getHistory.map(_.filter(_._3 >= basisTx) ==> List(
//            // 1 was retracted with tx meta data "meta2" and "meta1"
//            (eid, 1, tx2, false, "meta2", "meta1")
//          ))
//        } else {
//          Ns.e.int.tx.op.Tx(Ref2.str2 + Ref1.str1).getHistory.map(_ ==> List(
//            // 1 was retracted with tx meta data "meta2" and "meta1"
//            (eid, 1, tx2, false, "meta2", "meta1")
//          ))
//        }
        _ <- Ns.e.int.tx.op.Tx(Ref2.str2 + Ref1.str1).getHistory.map(_ ==> List(
            // 1 was retracted with tx meta data "meta2" and "meta1"
            (eid, 1, tx2, false, "meta2", "meta1")
          ))
      } yield ()
    }


    "Multiple entities with tx meta data" - core { implicit conn =>
      for {
        // Insert multiple entities with tx meta data
        txR1 <- Ns.int.Tx(Ref2.str2_("a")) insert List(1, 2, 3)
        tx1 = txR1.tx
        t1 = txR1.t
        List(e1, e2, e3) = txR1.eids

        // Retract multiple entities with tx meta data
        txR2 <- retract(Seq(e1, e2), Ref2.str2("b"))
        tx2 = txR2.tx
        t2 = txR2.t

//        _ <- if (system == SystemPeerServer) {
//          for {
//            // History with transaction data
//            _ <- Ns.int.tx.t.op.Tx(Ref2.str2).getHistory.map(
//              _.filter(_._2 >= basisTx) // Allow accumulating peer-server tests too
//                .sortBy(r => (r._2, r._1, r._4)) ==> List(
//                (1, tx1, t1, true, "a"),
//                (2, tx1, t1, true, "a"),
//                (3, tx1, t1, true, "a"),
//
//                // 1 and 2 were retracted with tx meta data "b"
//                (1, tx2, t2, false, "b"),
//                (2, tx2, t2, false, "b")
//              ))
//
//            // Entities and int values that were retracted with tx meta data "b"
//            _ <- Ns.e.int.tx.op(false).Tx(Ref2.str2("b")).getHistory
//              .map(_.filter(_._3 >= basisTx).sortBy(_._2) ==> List(
//                (e1, 1, tx2, false, "b"),
//                (e2, 2, tx2, false, "b")
//              ))
//
//            // Or: What int values were retracted with tx meta data "b"?
//            res <- Ns.int.tx.op_(false).Tx(Ref2.str2_("b")).getHistory
//              .map(_.filter(_._2 >= basisTx).sortBy(_._1) ==> List(
//                (1, tx2),
//                (2, tx2)
//              ))
//          } yield res
//
//        } else {
//        _ <-  for {
            // History with transaction data
            _ <- Ns.int.tx.op.Tx(Ref2.str2).getHistory.map(_.sortBy(r => (r._2, r._1, r._3)) ==> List(
              (1, tx1, true, "a"),
              (2, tx1, true, "a"),
              (3, tx1, true, "a"),

              // 1 and 2 were retracted with tx meta data "b"
              (1, tx2, false, "b"),
              (2, tx2, false, "b")
            ))

            // Entities and int values that were retracted with tx meta data "b"
            _ <- Ns.e.int.op(false).Tx(Ref2.str2("b")).getHistory.map(_.sortBy(r => (r._2, r._1, r._3)) ==> List(
              (e1, 1, false, "b"),
              (e2, 2, false, "b")
            ))

            // Or: What int values were retracted with tx meta data "b"?
            _ <- Ns.int.op_(false).Tx(Ref2.str2_("b")).getHistory.map(_ ==> List(1, 2))
//          } yield res
//        }
      } yield ()
    }


    "Multiple entities with tx meta data including ref" - core { implicit conn =>
      for {
        // Insert multiple entities with tx meta data including ref
        txR1 <- Ns.int.Tx(Ns.str_("a").Ref1.int1_(7)) insert List(1, 2, 3)
        List(e1, e2, e3) = txR1.eids
        tx1 = txR1.tx

        // Add tx meta data to retracting multiple entities
        tx2 <- retract(Seq(e1, e2), Ns.str("b").Ref1.int1(8)).map(_.tx)

//        _ <- if (system == SystemPeerServer) {
//          for {
//            // History with transaction data
//            _ <- Ns.int.tx.op.Tx(Ns.str.Ref1.int1).getHistory.map(
//              _.filter(_._2 >= basisTx).sortBy(r => (r._2, r._1, r._3)) ==> List(
//                (1, tx1, true, "a", 7),
//                (2, tx1, true, "a", 7),
//                (3, tx1, true, "a", 7),
//
//                // 1 and 2 were retracted with tx meta data "b"
//                (1, tx2, false, "b", 8),
//                (2, tx2, false, "b", 8)
//              ))
//
//            // Entities and int values that was retracted in tx "b"
//            _ <- Ns.e.int.tx.op(false).Tx(Ns.str("b").Ref1.int1(8)).getHistory
//              .map(_.filter(_._3 >= basisTx).sortBy(_._2) ==> List(
//                (e1, 1, tx2, false, "b", 8),
//                (e2, 2, tx2, false, "b", 8)
//              ))
//
//            // Or: What int values where retracted in tx "b"?
//            _ <- Ns.int.tx.op_(false).Tx(Ns.str_("b").Ref1.int1_(8)).getHistory
//              .map(_.filter(_._2 >= basisTx).sortBy(_._1) ==> List(
//                (1, tx2),
//                (2, tx2)
//              ))
//
//            // OBS: Note how referenced tx meta data is not asserted directly with the tx entity:
//            _ <- Ns.e.int.tx.op(false).Tx(Ref1.int1(8)).getHistory.map(_.filter(_._3 >= basisTx) ==> Nil)
//            // While Ns.str is:
//            res <- Ns.e.int.tx.op(false).Tx(Ns.str("b")).getHistory
//              .map(_.filter(_._3 >= basisTx).sortBy(_._2) ==> List(
//                (e1, 1, tx2, false, "b"),
//                (e2, 2, tx2, false, "b")
//              ))
//          } yield res
//
//        } else {
//          for {
            // History with transaction data
            _ <- Ns.int.tx.op.Tx(Ns.str.Ref1.int1).getHistory.map(_.sortBy(r => (r._2, r._1, r._3)) ==> List(
              (1, tx1, true, "a", 7),
              (2, tx1, true, "a", 7),
              (3, tx1, true, "a", 7),

              // 1 and 2 were retracted with tx meta data "b"
              (1, tx2, false, "b", 8),
              (2, tx2, false, "b", 8)
            ))

            // Entities and int values that was retracted in tx "b"
            _ <- Ns.e.int.op(false).Tx(Ns.str("b").Ref1.int1(8)).getHistory
              .map(_.sortBy(r => (r._2, r._1, r._3)) ==> List(
                (e1, 1, false, "b", 8),
                (e2, 2, false, "b", 8)
              ))

            // Or: What int values where retracted in tx "b"?
            _ <- Ns.int.op_(false).Tx(Ns.str_("b").Ref1.int1_(8)).getHistory.map(_ ==> List(1, 2))

            // OBS: Note how referenced tx meta data is not asserted directly with the tx entity:
            _ <- Ns.e.int.op(false).Tx(Ref1.int1(8)).getHistory.map(_ ==> Nil)
            // While Ns.str is:
            _ <- Ns.e.int.op(false).Tx(Ns.str("b")).getHistory.map(_.sortBy(_._2) ==> List(
              (e1, 1, false, "b"),
              (e2, 2, false, "b")
            ))
//          } yield res
//        }
      } yield ()
    }


    "Inspect retracting 1 entity" - core { implicit conn =>
      for {
        eid <- Ns.int(1).save.map(_.eid)

        // Inspect retraction statements
        _ <- eid.inspectRetract
        /*
          ## 1 ## Inspect `retract` on entity
          =============================================================================
          list(
            RetractEntity(17592186045453))
          =============================================================================
         */

        // Inspect retraction statements with tx meta data
        _ <- eid.inspectRetract(Ref2.str2("meta2") + Ref1.str1("meta1"))
        /*
          ## 1 ## Inspect `retract` on entity with tx meta data
          =============================================================================
          list(
            RetractEntity(17592186045453),
            Add(datomic.tx,:Ref2/str2,meta2,Card(1)),
            Add(datomic.tx,:Ref1/str1,meta1,Card(1)))
          =============================================================================
         */
      } yield ()
    }

    "Inspect retracting multiple entities" - core { implicit conn =>
      for {
        eids <- Ns.int.insert(1, 2).map(_.eids)

        // Inspect retraction statements
        _ <- inspectRetract(eids)
        /*
          ## 1 ## molecule.core.Datomic.inspectRetract
          =============================================================================
          Model()
          -----------------------------------------------------------------------------
          List()
          -----------------------------------------------------------------------------
          List(
            list(
              RetractEntity(17592186045453),
              RetractEntity(17592186045454)))
          =============================================================================
         */

        // Inspect retraction statements with tx meta data
        _ <- inspectRetract(eids, Ref2.str2("meta2"), Ref1.str1("meta1"))
        /*
          ## 1 ## molecule.core.Datomic.inspectRetract
          =============================================================================
          Model(
            TxMetaData(
              Atom("Ref2", "str2", "String", 1, Eq(Seq("meta2")), None, Seq(), Seq()))
            TxMetaData(
              Atom("Ref1", "str1", "String", 1, Eq(Seq("meta1")), None, Seq(), Seq())))
          -----------------------------------------------------------------------------
          List(
            Add(tx,:Ref2/str2,Values(Eq(Seq("meta2")),None),Card(1)),
            Add(tx,:Ref1/str1,Values(Eq(Seq("meta1")),None),Card(1)))
          -----------------------------------------------------------------------------
          List(
            list(
              RetractEntity(17592186045453),
              RetractEntity(17592186045454),
              Add(datomic.tx,:Ref2/str2,meta2,Card(1)),
              Add(datomic.tx,:Ref1/str1,meta1,Card(1))))
          =============================================================================
         */
      } yield ()
    }
  }
}