package moleculeTests.tests.core.obj

import molecule.core.util.Helpers
import molecule.datomic.api.out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Time extends AsyncTestSuite with Helpers {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      tx1 <- Ns.int(1).save
      tx2 <- Ns.int(2).save
      tx3 <- Ns.int(3).save
      t1 = tx1.t
      t2 = tx2.t
      t3 = tx3.t
      d1 = tx1.inst
      d2 = tx2.inst
      d3 = tx3.inst
    } yield {
      (tx1, tx2, tx3, t1, t2, t3, d1, d2, d3)
    }
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "AsOf" - core { implicit conn =>
      for {
        (tx1, tx2, tx3, t1, t2, t3, d1, d2, d3) <- testData

        _ <- Ns.int.getObjListAsOf(t1).map(_.map(_.int) ==> List(1))
        _ <- Ns.int.getObjListAsOf(t2).map(_.map(_.int) ==> List(1, 2))
        _ <- Ns.int.getObjListAsOf(t3).map(_.map(_.int) ==> List(1, 2, 3))
        _ <- Ns.int.getObjListAsOf(t3, 2).map(_.map(_.int) ==> List(1, 2))

        _ <- Ns.int.getObjListAsOf(tx1).map(_.map(_.int) ==> List(1))
        _ <- Ns.int.getObjListAsOf(tx2).map(_.map(_.int) ==> List(1, 2))
        _ <- Ns.int.getObjListAsOf(tx3).map(_.map(_.int) ==> List(1, 2, 3))
        _ <- Ns.int.getObjListAsOf(tx3, 2).map(_.map(_.int) ==> List(1, 2))

        _ <- Ns.int.getObjListAsOf(d1).map(_.map(_.int) ==> List(1))
        _ <- Ns.int.getObjListAsOf(d2).map(_.map(_.int) ==> List(1, 2))
        _ <- Ns.int.getObjListAsOf(d3).map(_.map(_.int) ==> List(1, 2, 3))
        _ <- Ns.int.getObjListAsOf(d3, 2).map(_.map(_.int) ==> List(1, 2))
      } yield ()
    }


    "Since" - core { implicit conn =>
      for {
        (tx1, tx2, tx3, t1, t2, t3, d1, d2, d3) <- testData

        _ <- Ns.int.getObjListSince(t3).map(_.map(_.int) ==> List())
        _ <- Ns.int.getObjListSince(t2).map(_.map(_.int) ==> List(3))
        _ <- Ns.int.getObjListSince(t1).map(_.map(_.int) ==> List(2, 3))
        _ <- Ns.int.getObjListSince(t1, 1).map(_.map(_.int) ==> List(2))

        _ <- Ns.int.getObjListSince(tx3).map(_.map(_.int) ==> List())
        _ <- Ns.int.getObjListSince(tx2).map(_.map(_.int) ==> List(3))
        _ <- Ns.int.getObjListSince(tx1).map(_.map(_.int) ==> List(2, 3))
        _ <- Ns.int.getObjListSince(tx1, 1).map(_.map(_.int) ==> List(2))

        // transactions can happen within a millisecond which is the
        // minimum time unit of a Date. So the following tests can be overlapping
        //        _ <- Ns.int.getObjListSince(d3).map(_.map(_.int) ==> List())
        //        _ <- Ns.int.getObjListSince(d2).map(_.map(_.int) ==> List(3))
        //        _ <- Ns.int.getObjListSince(d1).map(_.map(_.int) ==> List(2, 3))
        //        _ <- Ns.int.getObjListSince(d1, 1).map(_.map(_.int) ==> List(2))
      } yield ()
    }


    "With" - core { implicit conn =>
      val saveTx2 = Ns.int(2).getSaveStmts
      val saveTx3 = Ns.int(3).getSaveStmts

      // See raw data api on jvm platform: jvm.obj.Time

      for {
        _ <- Ns.int(1).save

        _ <- Ns.int.getObjListWith(saveTx2).map(_.map(_.int) ==> List(1, 2))
        _ <- Ns.int.getObjListWith(saveTx2, saveTx3).map(_.map(_.int) ==> List(1, 2, 3))
        // Note how the parameter for number of rows returned is first (since we
        // need the vararg for tx molecules last)
        _ <- Ns.int.getObjListWith(2, saveTx2, saveTx3).map(_.map(_.int) ==> List(1, 2))
      } yield ()
    }


    "History" - core { implicit conn =>
      for {
        tx1 <- Ns.int(1).save
        e = tx1.eid
        tx2 <- Ns(e).int(2).update

        _ <- Ns(e).int.t.op.getObjListHistory.map(_.sortBy(o => (o.t, o.op)).map(o => Vector(o.int, o.t, o.op)) ==> List(
          Vector(1, tx1.t, true),
          Vector(1, tx2.t, false),
          Vector(2, tx2.t, true)
        ))
      } yield ()
    }
  }
}
