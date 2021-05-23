package molecule.tests.core.txMetaData

//import datomic.Peer

import molecule.datomic.api.in3_out10._
import molecule.datomic.base.ops.QueryOps
import molecule.datomic.base.util.{SystemPeer, SystemPeerServer}
import molecule.setup.AsyncTestSuite
import molecule.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MetaRetract extends AsyncTestSuite {

  val basisTx = QueryOps.txBase

  lazy val tests = Tests {

    "1 entity" - core { implicit conn =>
      for {
        txR <- Ns.int(1).save
        e = txR.eid

        // Retract entity with tx meta data
        txR2 <- e.Tx(Ns.str("meta")).retract
        tx2 = txR2.tx

        // What was retracted and with what tx meta data
        _ <- if (system == SystemPeerServer) {
          Ns.e.int.tx.op.Tx(Ns.str).getHistory.map(_.filter(_._3 >= basisTx) ==> List(
            // 1 was retracted with tx meta data "meta"
            (e, 1, tx2, false, "meta")
          ))
        } else {
          Ns.e.int.tx.op.Tx(Ns.str).getHistory === List(
            // 1 was retracted with tx meta data "meta"
            (e, 1, tx2, false, "meta")
          )
        }
      } yield ()
    }

    "Multiple entities without tx meta data" - core { implicit conn =>
      for {
        tx <- Ns.int insert List(1, 2, 3)
        List(e1, e2, e3) = tx.eids

        _ <- Ns.int.get === List(1, 2, 3)

        // Retract multiple entities (without tx meta data)
        _ <- retract(Seq(e1, e2))

        _ <- Ns.int.get === List(3)
      } yield ()
    }

    "Multiple entities" - core { implicit conn =>
      for {
        // Insert multiple entities with tx meta data
        txR1 <- Ns.int.Tx(Ns.str_("a")) insert List(1, 2, 3)
        List(e1, e2, e3, tx1) = txR1.eids
        t1 = txR1.t

        // Retract multiple entities with tx meta data
        txR2 <- retract(Seq(e1, e2), Ns.str("b"))
        tx2 = txR2.tx
        t2 = txR2.t

        _ <- if (system == SystemPeerServer) {
          for {
            // History with transaction data
            _ <- Ns.int.tx.t.op.Tx(Ns.str).getHistory.map(
              _.filter(_._2 >= basisTx) // Allow accumulating peer-server tests too
                .sortBy(r => (r._2, r._1, r._4)) ==> List(
                (1, tx1, t1, true, "a"),
                (2, tx1, t1, true, "a"),
                (3, tx1, t1, true, "a"),

                // 1 and 2 were retracted with tx meta data "b"
                (1, tx2, t2, false, "b"),
                (2, tx2, t2, false, "b")
              ))

            // Entities and int values that were retracted with tx meta data "b"
            _ <- Ns.e.int.tx.op(false).Tx(Ns.str("b")).getHistory.map(_.filter(_._3 >= basisTx).sortBy(_._2) ==> List(
              (e1, 1, tx2, false, "b"),
              (e2, 2, tx2, false, "b")
            ))

            // Or: What int values were retracted with tx meta data "b"?
            res <- Ns.int.tx.op_(false).Tx(Ns.str_("b")).getHistory.map(_.filter(_._2 >= basisTx).sortBy(_._1) ==> List(
              (1, tx2),
              (2, tx2)
            ))
          } yield res

        } else {
          for {
            // History with transaction data
            _ <- Ns.int.tx.op.Tx(Ns.str).getHistory.map(_.sortBy(r => (r._2, r._1, r._3)) ==> List(
              (1, tx1, true, "a"),
              (2, tx1, true, "a"),
              (3, tx1, true, "a"),

              // 1 and 2 were retracted with tx meta data "b"
              (1, tx2, false, "b"),
              (2, tx2, false, "b")
            ))

            // Entities and int values that were retracted with tx meta data "b"
            _ <- Ns.e.int.op(false).Tx(Ns.str("b")).getHistory.map(_.sortBy(r => (r._2, r._1, r._3)) ==> List(
              (e1, 1, false, "b"),
              (e2, 2, false, "b")
            ))

            // Or: What int values were retracted with tx meta data "b"?
            res <- Ns.int.op_(false).Tx(Ns.str_("b")).getHistory === List(1, 2)
          } yield res
        }
      } yield ()
    }


    "Multiple entities with tx meta data including ref" - core { implicit conn =>
      for {
        // Insert multiple entities with tx meta data including ref
        txR1 <- Ns.int.Tx(Ns.str_("a").Ref1.int1_(7)) insert List(1, 2, 3)
        List(e1, e2, e3, tx1, r1) = txR1.eids

        // Add tx meta data to retracting multiple entities
        txR2 <- retract(Seq(e1, e2), Ns.str("b").Ref1.int1(8))
        tx2 = txR2.tx

        _ <- if (system == SystemPeerServer) {
          for {
            // History with transaction data
            _ <- Ns.int.tx.op.Tx(Ns.str.Ref1.int1).getHistory.map(
              _.filter(_._2 >= basisTx).sortBy(r => (r._2, r._1, r._3)) ==> List(
                (1, tx1, true, "a", 7),
                (2, tx1, true, "a", 7),
                (3, tx1, true, "a", 7),

                // 1 and 2 were retracted with tx meta data "b"
                (1, tx2, false, "b", 8),
                (2, tx2, false, "b", 8)
              ))

            // Entities and int values that was retracted in tx "b"
            _ <- Ns.e.int.tx.op(false).Tx(Ns.str("b").Ref1.int1(8)).getHistory.map(_.filter(_._3 >= basisTx).sortBy(_._2) ==> List(
              (e1, 1, tx2, false, "b", 8),
              (e2, 2, tx2, false, "b", 8)
            ))

            // Or: What int values where retracted in tx "b"?
            _ <- Ns.int.tx.op_(false).Tx(Ns.str_("b").Ref1.int1_(8)).getHistory.map(_.filter(_._2 >= basisTx).sortBy(_._1) ==> List(
              (1, tx2),
              (2, tx2)
            ))

            // OBS: Note how referenced tx meta data is not asserted directly with the tx entity:
            _ <- Ns.e.int.tx.op(false).Tx(Ref1.int1(8)).getHistory.map(_.filter(_._3 >= basisTx) ==> Nil)
            // While Ns.str is:
            res <- Ns.e.int.tx.op(false).Tx(Ns.str("b")).getHistory.map(_.filter(_._3 >= basisTx).sortBy(_._2) ==> List(
              (e1, 1, tx2, false, "b"),
              (e2, 2, tx2, false, "b")
            ))
          } yield res

        } else {
          for {
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
            _ <- Ns.e.int.op(false).Tx(Ns.str("b").Ref1.int1(8)).getHistory.map(_.sortBy(r => (r._2, r._1, r._3)) ==> List(
              (e1, 1, false, "b", 8),
              (e2, 2, false, "b", 8)
            ))

            // Or: What int values where retracted in tx "b"?
            _ <- Ns.int.op_(false).Tx(Ns.str_("b").Ref1.int1_(8)).getHistory === List(1, 2)

            // OBS: Note how referenced tx meta data is not asserted directly with the tx entity:
            _ <- Ns.e.int.op(false).Tx(Ref1.int1(8)).getHistory === Nil
            // While Ns.str is:
            res <- Ns.e.int.op(false).Tx(Ns.str("b")).getHistory.map(_.sortBy(_._2) ==> List(
              (e1, 1, false, "b"),
              (e2, 2, false, "b")
            ))
          } yield res
        }
      } yield ()
    }
  }
}