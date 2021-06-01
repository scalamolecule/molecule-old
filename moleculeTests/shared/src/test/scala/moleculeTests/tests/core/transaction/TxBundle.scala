package moleculeTests.tests.core.transaction

import java.util.concurrent.ExecutionException
import molecule.datomic.api.out3._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object TxBundle extends AsyncTestSuite {

  lazy val tests = Tests {

    "Transact multiple molecules" - core { implicit conn =>
      for {
        // Initial data
        tx <- Ns.int insert List(1, 2, 3)
        List(e1, e2, e3) = tx.eids

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
        // (different systems throws different exceptions)
        _ <- if (system == SystemPeer) {
          transactBundle(
            Ns(e3).int(31).getUpdateStmts,
            Ns(e3).int(32).getUpdateStmts
          ).recover(_.getMessage ==>
            ":db.error/datoms-conflict Two datoms in the same transaction conflict\n" +
              "{:d1 [17592186045455 :Ns/int 31 13194139534356 true],\n" +
              " :d2 [17592186045455 :Ns/int 32 13194139534356 true]}\n"
          )
        } else Future.unit
      } yield ()
    }


    "Inspect" - core { implicit conn =>
      for {
        // Initial data
        tx <- Ns.int insert List(1, 2, 3)
        List(e1, e2, e3) = tx.eids

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
        // 1st group: List of generic statements
        // 2nd group: tx timestamp
        //            tx datoms from dbAfter (op - tx id - entity - attribute - value)
        //            (minus added to help indicate retractions)
        /*
          ## 1 ## TxReport
          ================================================================================================================
          list(
            :db/retractEntity        17592186045453,
            Add(TempId(":db.part/user", -1000001),:Ns/int,4,Card(1)),
            Add(TempId(":db.part/user", -1000002),:Ns/int,5,Card(1)),
            Add(TempId(":db.part/user", -1000003),:Ns/int,6,Card(1)),
            Add(17592186045454,:Ns/int,20,Card(1)))
          ----------------------------------------------------------------------------------------------------------------
          List(
            1    1     added: true ,   t: 13194139534352,   e: 13194139534352,   a: 50,   v: Mon May 31 17:47:47 CEST 2021,

            2    2     added: false,  -t: 13194139534352,  -e: 17592186045453,  -a: 73,  -v: 1,

            3    3     added: true ,   t: 13194139534352,   e: 17592186045457,   a: 73,   v: 4,

            4    4     added: true ,   t: 13194139534352,   e: 17592186045458,   a: 73,   v: 5,

            5    5     added: true ,   t: 13194139534352,   e: 17592186045459,   a: 73,   v: 6,

            6    6     added: true ,   t: 13194139534352,   e: 17592186045454,   a: 73,   v: 20,
                 7     added: false,  -t: 13194139534352,  -e: 17592186045454,  -a: 73,  -v: 2)
          ================================================================================================================
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

        // Will print the same as calling `inspectTransact(...)`
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
