package moleculeTests.jvm.db.datomic.peer

import molecule.datomic.api.out1._
import molecule.datomic.peer.facade.Conn_Peer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object TxReportQueue extends AsyncTestSuite {

  lazy val tests = Tests {

    "Take one tx report" - corePeerOnly { implicit futConn =>
      for {
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])

        // Data before initialising the tx report queue
        _ <- Ns.int(0).save

        // Initialise tx report queue
        queue = conn.txReportQueue

        // The txReportQueue is empty when initialized
        _ = queue.isEmpty ==> true

        // Each transaction hereafter is added to the queue
        _ <- Ns.int(1).save
        _ <- Ns.int(2).save
        _ <- Ns.int(3).save

        // Queue has been populated
        _ = queue.isEmpty ==> false

        // Take head of tx report, one at a time (on a FIFO basis)
        _ = queue.take // head of queue is a TxReport instance
          .txData // get tx data of tx report - a list of Datoms
          .last // get last Datom of tx data
          .v ==> 1 // get value of Datom
        _ = queue.take.txData.last.v ==> 2
        _ = queue.take.txData.last.v ==> 3

        // Queue has been emptied
        _ = queue.isEmpty ==> true

        // Remove queue
        _ = conn.removeTxReportQueue()

        // Data from before and after calling txReportQueue not affected
        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 2, 3))
      } yield ()
    }


    "Watch tx reports" - corePeerOnly { implicit futConn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      for {
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])

        // Data before initialising the tx report queue
        _ <- Ns.int(0).save

        // Initialise tx report queue
        queue = conn.txReportQueue

        // Let queue listen in a separate thread
        _ = Future {
          // Listening to the first 3 transactions (could be an infinite loop too)
          (1 to 3).foreach(_ =>
            // Do stuff with tx report when it becomes available.
            // `take` blocks until a tx report is available on the queue
            println(queue.take)
          )
        }

        // Each transaction is added to the queue and printed by the above listener
        _ <- Ns.int(1).save
        _ <- Ns.int(2).save
        _ <- Ns.int(3).save

        /*
        Tx reports for the last 3 transactions are printed by the queue listener:

        TxReport {
          dbBefore  : datomic.db.Db@7dced5ae
          dbBefore.t: 1036
          dbAfter   : datomic.db.Db@d8bcefb3
          dbAfter.t : 1038
          txData    : [13194139534350  50    Sun Nov 07 17:57:06 CET 2021         13194139534350   true],
                      [17592186045455  73    1                                    13194139534350   true]
          tempIds   : {-9223350046623220403 17592186045455}
          eids      : List(17592186045455)
        }
        TxReport {
          dbBefore  : datomic.db.Db@d8bcefb3
          dbBefore.t: 1038
          dbAfter   : datomic.db.Db@84609abb
          dbAfter.t : 1040
          txData    : [13194139534352  50    Sun Nov 07 17:57:06 CET 2021         13194139534352   true],
                      [17592186045457  73    2                                    13194139534352   true]
          tempIds   : {-9223350046623220404 17592186045457}
          eids      : List(17592186045457)
        }
        TxReport {
          dbBefore  : datomic.db.Db@84609abb
          dbBefore.t: 1040
          dbAfter   : datomic.db.Db@b2cbb92e
          dbAfter.t : 1042
          txData    : [13194139534354  50    Sun Nov 07 17:57:06 CET 2021         13194139534354   true],
                      [17592186045459  73    3                                    13194139534354   true]
          tempIds   : {-9223350046623220405 17592186045459}
          eids      : List(17592186045459)
        }
        */

        // Inactivate and remove queue from connection
        _ = conn.removeTxReportQueue()

        // Data from before and after calling txReportQueue is not affected
        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 2, 3))
      } yield ()
    }
  }
}