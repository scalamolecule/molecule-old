package moleculeTests.tests.core.crud

import molecule.datomic.api.out3._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._
import scala.concurrent.Future


object TxBundle extends AsyncTestSuite {

  lazy val tests = Tests {

    "Transact multiple molecules" - core { implicit conn =>
      for {
        // Initial data
        List(e1, e2, e3) <- Ns.int insert List(1, 2, 3) map (_.eids)

        // State before
        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))

        // Transact multiple molecule statements in one bundled transaction
        _ <- transactBundle(
          // retract
          e1.getRetractStmts,
          // save
          Ns.int(4).getSaveStmts,
          // insert
          Ns.int.getInsertStmts(List(5, 6)),
          // update
          Ns(e2).int(20).getUpdateStmts
        )

        // State after transaction bundle
        _ <- Ns.int.get.map(_.sorted ==> List(
          // 1 retracted
          3, // unchanged
          4, // saved
          5, 6, // inserted
          20 // 2 updated
        ))

        // Can't transact conflicting datoms
        // (different systems throw different exceptions)
        _ <- if (system == SystemPeer) {
          transactBundle(
            Ns(e3).int(31).getUpdateStmts,
            Ns(e3).int(32).getUpdateStmts
          ).map(_ ==> "Unexpected success").recover { case e: RuntimeException => e.getMessage.contains(
            ":db.error/datoms-conflict Two datoms in the same transaction conflict") ==> true
          }
        }
        else Future.unit
      }
      yield ()
    }


    "Inspect" - core { implicit conn =>
      for {
        // Initial data
        List(e1, e2, e3) <- Ns.int insert List(1, 2, 3) map (_.eids)

        // Print inspect info for group transaction without affecting live db
        _ <- inspectTransactBundle(
          // retract
          e1.getRetractStmts,
          // save
          Ns.int(4).getSaveStmts,
          // insert
          Ns.int.getInsertStmts(List(5, 6)),
          // update
          Ns(e2).int(20).getUpdateStmts
        )

        // Prints something like this:
        /*
          TxReport {
            dbBefore  : datomic.db.Db@f8a53a3f
            dbBefore.t: 1037
            dbAfter   : datomic.db.Db@74578a9f
            dbAfter.t : 1038
            txData    : [13194139534350   50   Sun Jun 20 23:39:07 CEST 2021       13194139534350  true],
                        [17592186044418   73   1       13194139534350  false],
                        [17592186044423   73   4       13194139534350  true],
                        [17592186044424   73   5       13194139534350  true],
                        [17592186044425   73   6       13194139534350  true],
                        [17592186044420   73   20       13194139534350  true],
                        [17592186044420   73   2       13194139534350  false]
            tempids   : {}
            eids      : List(17592186044423, 17592186044424, 17592186044425, 17592186044420)
          }
        */

        // Live data unchanged
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // If a real group transaction is invoked, the resulting tx report can also be inspected
        tx <- transactBundle(
          // retract
          e1.getRetractStmts,
          // save
          Ns.int(4).getSaveStmts,
          // insert
          Ns.int.getInsertStmts(List(5, 6)),
          // update
          Ns(e2).int(20).getUpdateStmts
        )

        // Will print the same as calling `inspectTransactBundle(...)`
        _ = tx.inspect

        // Live data has now changed
        _ <- Ns.int.get.map(_.sorted ==> List(
          // 1 retracted
          3, // unchanged
          4, // saved
          5, 6, // inserted
          20 // 2 updated
        ))
      } yield ()
    }
  }
}
