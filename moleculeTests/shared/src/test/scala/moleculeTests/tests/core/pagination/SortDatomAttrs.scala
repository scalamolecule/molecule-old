package moleculeTests.tests.core.pagination

import molecule.core.util.Executor._
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.in1_out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SortDatomAttrs extends AsyncTestSuite {

  lazy val tests = Tests {

    "Generic datom attributes" - core { implicit conn =>
      for {
        r1 <- Ns.int(3).save
        e1_ = r1.eid
        t1_ = r1.t
        tx1_ = r1.tx
        d1_ = r1.txInstant

        r2 <- Ns.int(1).save
        e2_ = r2.eid
        t2_ = r2.t
        tx2_ = r2.tx
        d2_ = r2.txInstant

        r3 <- Ns.int(2).save
        e3_ = r3.eid
        t3_ = r3.t
        tx3_ = r3.tx
        d3_ = r3.txInstant

        // ids on client side are not deterministic
        List(e1, e2, e3) = List(e1_, e2_, e3_).sorted
        List(t1, t2, t3) = List(t1_, t2_, t3_).sorted
        List(tx1, tx2, tx3) = List(tx1_, tx2_, tx3_).sorted
        List(d1, d2, d3) = List(d1_, d2_, d3_).sorted

        // e, t, tx, txInstant
        _ <- Ns.e.a1.int_.get.map(_ ==> List(e1, e2, e3))
        _ <- Ns.e.d1.int_.get.map(_ ==> List(e3, e2, e1))

        _ <- Ns.int_.t.a1.get.map(_ ==> List(t1, t2, t3))
        _ <- Ns.int_.t.d1.get.map(_ ==> List(t3, t2, t1))

        _ <- Ns.int_.tx.a1.get.map(_ ==> List(tx1, tx2, tx3))
        _ <- Ns.int_.tx.d1.get.map(_ ==> List(tx3, tx2, tx1))

        _ <- Ns.int_.txInstant.a1.get.map(_ ==> List(d1, d2, d3))
        _ <- Ns.int_.txInstant.d1.get.map(_ ==> List(d3, d2, d1))

        r4 <- Ns.int(2).long(1).str("a").save
        e4 = r4.eid
        t4 = r4.t
        tx4 = r4.tx
        d4 = r4.txInstant

        // a, v
        _ <- Ns(e4).e.a.a1.v.t.tx.txInstant.op.get.map(_ ==> List(
          (e4, ":Ns/int", 2, t4, tx4, d4, true),
          (e4, ":Ns/long", 1, t4, tx4, d4, true),
          (e4, ":Ns/str", "a", t4, tx4, d4, true),
        ))
        _ <- Ns(e4).e.a.d1.v.t.tx.txInstant.op.get.map(_ ==> List(
          (e4, ":Ns/str", "a", t4, tx4, d4, true),
          (e4, ":Ns/long", 1, t4, tx4, d4, true),
          (e4, ":Ns/int", 2, t4, tx4, d4, true),
        ))
        _ <- Ns(e4).e.a.v.a1.t.tx.txInstant.op.get.map(_ ==> List(
          (e4, ":Ns/long", 1, t4, tx4, d4, true),
          (e4, ":Ns/int", 2, t4, tx4, d4, true),
          (e4, ":Ns/str", "a", t4, tx4, d4, true),
        ))
        _ <- Ns(e4).e.a.v.d1.t.tx.txInstant.op.get.map(_ ==> List(
          (e4, ":Ns/str", "a", t4, tx4, d4, true),
          (e4, ":Ns/int", 2, t4, tx4, d4, true),
          (e4, ":Ns/long", 1, t4, tx4, d4, true),
        ))

        r5 <- Ns(e4).long(3).update
        t5 = r5.t

        // op
        _ <- Ns.long.t.a1.op.a2.getHistory.map(_ ==> List(
          (1, t4, true),
          (1, t5, false),
          (3, t5, true),
        ))
        _ <- Ns.long.t.d1.op.d2.getHistory.map(_ ==> List(
          (3, t5, true),
          (1, t5, false),
          (1, t4, true),
        ))
      } yield ()
    }
  }
}
