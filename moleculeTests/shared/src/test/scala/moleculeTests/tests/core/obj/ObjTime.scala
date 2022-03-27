package moleculeTests.tests.core.obj

import molecule.core.util.Helpers
import molecule.datomic.api.out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object ObjTime extends AsyncTestSuite with Helpers {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      tx1 <- Ns.int(1).save
      tx2 <- Ns.int(2).save
      tx3 <- Ns.int(3).save
      t1 = tx1.t
      t2 = tx2.t
      t3 = tx3.t
      d1 = tx1.txInstant
      d2 = tx2.txInstant
      d3 = tx3.txInstant
    } yield {
      (tx1, tx2, tx3, t1, t2, t3, d1, d2, d3)
    }
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "AsOf" - core { implicit conn =>
      for {
        (tx1, tx2, tx3, t1, t2, t3, d1, d2, d3) <- testData

        _ <- Ns.int.getObjsAsOf(t1).map(_.map(_.int) ==> List(1))
        _ <- Ns.int.getObjsAsOf(t2).map(_.map(_.int) ==> List(1, 2))
        _ <- Ns.int.getObjsAsOf(t3).map(_.map(_.int) ==> List(1, 2, 3))
        _ <- Ns.int.getObjsAsOf(t3, 2).map(_.map(_.int) ==> List(1, 2))

        _ <- Ns.int.getObjsAsOf(tx1).map(_.map(_.int) ==> List(1))
        _ <- Ns.int.getObjsAsOf(tx2).map(_.map(_.int) ==> List(1, 2))
        _ <- Ns.int.getObjsAsOf(tx3).map(_.map(_.int) ==> List(1, 2, 3))
        _ <- Ns.int.getObjsAsOf(tx3, 2).map(_.map(_.int) ==> List(1, 2))

        _ <- Ns.int.getObjsAsOf(d1).map(_.map(_.int) ==> List(1))
        _ <- Ns.int.getObjsAsOf(d2).map(_.map(_.int) ==> List(1, 2))
        _ <- Ns.int.getObjsAsOf(d3).map(_.map(_.int) ==> List(1, 2, 3))
        _ <- Ns.int.getObjsAsOf(d3, 2).map(_.map(_.int) ==> List(1, 2))
      } yield ()
    }


    "Since" - core { implicit conn =>
      for {
        (tx1, tx2, tx3, t1, t2, t3, d1, d2, d3) <- testData

        _ <- Ns.int.getObjsSince(t3).map(_.map(_.int) ==> List())
        _ <- Ns.int.getObjsSince(t2).map(_.map(_.int) ==> List(3))
        _ <- Ns.int.getObjsSince(t1).map(_.map(_.int) ==> List(2, 3))
        _ <- Ns.int.getObjsSince(t1, 1).map(_.map(_.int) ==> List(2))

        _ <- Ns.int.getObjsSince(tx3).map(_.map(_.int) ==> List())
        _ <- Ns.int.getObjsSince(tx2).map(_.map(_.int) ==> List(3))
        _ <- Ns.int.getObjsSince(tx1).map(_.map(_.int) ==> List(2, 3))
        _ <- Ns.int.getObjsSince(tx1, 1).map(_.map(_.int) ==> List(2))

        // transactions can happen within a millisecond which is the
        // minimum time unit of a Date. So the following tests can be overlapping
        //        _ <- Ns.int.getObjListSince(d3).map(_.map(_.int) ==> List())
        //        _ <- Ns.int.getObjListSince(d2).map(_.map(_.int) ==> List(3))
        //        _ <- Ns.int.getObjListSince(d1).map(_.map(_.int) ==> List(2, 3))
        //        _ <- Ns.int.getObjListSince(d1, 1).map(_.map(_.int) ==> List(2))
      } yield ()
    }


    "With" - core { implicit conn =>
      val saveStmts2 = Ns.int(2).getSaveStmts
      val saveStmts3 = Ns.int(3).getSaveStmts

      // See raw data api on jvm platform: jvm.obj.Time

      for {
        _ <- Ns.int(1).save

        _ <- Ns.int.getObjsWith(saveStmts2).map(_.map(_.int) ==> List(1, 2))
        _ <- Ns.int.getObjsWith(saveStmts2, saveStmts3).map(_.map(_.int) ==> List(1, 2, 3))
        // Note how the parameter for number of rows returned is first (since we
        // need the vararg for tx molecules last)
        _ <- Ns.int.getObjsWith(2, saveStmts2, saveStmts3).map(_.map(_.int) ==> List(1, 2))
      } yield ()
    }


    "History" - core { implicit conn =>
      for {
        tx1 <- Ns.int(1).save
        e = tx1.eid
        tx2 <- Ns(e).int(2).update

        _ <- Ns(e).int.t.a1.op.a2.getObjsHistory.map(_.map(o => Vector(o.int, o.t, o.op)) ==> List(
          Vector(1, tx1.t, true),
          Vector(1, tx2.t, false),
          Vector(2, tx2.t, true)
        ))
      } yield ()
    }
  }
}
