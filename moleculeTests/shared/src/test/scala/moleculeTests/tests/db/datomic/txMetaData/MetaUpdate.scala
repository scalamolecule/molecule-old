package moleculeTests.tests.db.datomic.txMetaData

import molecule.datomic.api.in3_out10._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._


object MetaUpdate extends AsyncTestSuite {

  lazy val tests = Tests {

    "Update" - core { implicit conn =>
      for {
        // tx 1: save
        txR1 <- Ns.int(1).Tx(Ns.str("a")).save
        e = txR1.eid
        tx1 = txR1.tx

        // tx2: Update without tx meta data
        txR2 <- Ns(e).int(2).update
        tx2 = txR2.tx

        // tx3: Update with tx meta data
        txR3 <- Ns(e).int(3).Tx(Ns.str("b")).update
        tx3 = txR3.tx


        // History without tx meta data
        _ <- Ns(e).int.tx.a1.op.a2.getHistory.map(_ ==> List(
          // tx 1
          (1, tx1, true), // 1 asserted (save)

          // tx 2
          (1, tx2, false), // 1 retracted
          (2, tx2, true), // 2 asserted (update)

          // tx 3
          (2, tx3, false), // 2 retracted
          (3, tx3, true) // 3 asserted (update)
        ))

        // History with tx meta data
        _ <- Ns(e).int.tx.a1.op.a2.Tx(Ns.str).getHistory.map(_ ==> List(
          // tx 1
          (1, tx1, true, "a"), // 1 asserted (save)

          // (tx2 has no tx meta data)

          // tx 3
          (2, tx3, false, "b"), // 2 retracted
          (3, tx3, true, "b") // 3 asserted (update)
        ))
      } yield ()
    }
  }
}