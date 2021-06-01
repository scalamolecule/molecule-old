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


        _ <- Ns.int.getObjArrayAsOf(t1).map(_.map(_.int) ==> Array(1))
        _ <- Ns.int.getObjArrayAsOf(t2).map(_.map(_.int) ==> Array(1, 2))
        _ <- Ns.int.getObjArrayAsOf(t3).map(_.map(_.int) ==> Array(1, 2, 3))
        _ <- Ns.int.getObjArrayAsOf(t3, 2).map(_.map(_.int) ==> Array(1, 2))

        _ <- Ns.int.getObjArrayAsOf(tx1).map(_.map(_.int) ==> Array(1))
        _ <- Ns.int.getObjArrayAsOf(tx2).map(_.map(_.int) ==> Array(1, 2))
        _ <- Ns.int.getObjArrayAsOf(tx3).map(_.map(_.int) ==> Array(1, 2, 3))
        _ <- Ns.int.getObjArrayAsOf(tx3, 2).map(_.map(_.int) ==> Array(1, 2))

        _ <- Ns.int.getObjArrayAsOf(d1).map(_.map(_.int) ==> Array(1))
        _ <- Ns.int.getObjArrayAsOf(d2).map(_.map(_.int) ==> Array(1, 2))
        _ <- Ns.int.getObjArrayAsOf(d3).map(_.map(_.int) ==> Array(1, 2, 3))
        _ <- Ns.int.getObjArrayAsOf(d3, 2).map(_.map(_.int) ==> Array(1, 2))


        _ <- Ns.int.getObjIterableAsOf(t1).map(_.iterator.next().int ==> 1)
        _ <- Ns.int.getObjIterableAsOf(t1).map(_.iterator.toList.map(_.int) ==> Iterator(1).toList)
        _ <- Ns.int.getObjIterableAsOf(t2).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2).toList)
        _ <- Ns.int.getObjIterableAsOf(t3).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2, 3).toList)

        _ <- Ns.int.getObjIterableAsOf(tx1).map(_.iterator.toList.map(_.int) ==> Iterator(1).toList)
        _ <- Ns.int.getObjIterableAsOf(tx2).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2).toList)
        _ <- Ns.int.getObjIterableAsOf(tx3).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2, 3).toList)

        _ <- Ns.int.getObjIterableAsOf(d1).map(_.iterator.toList.map(_.int) ==> Iterator(1).toList)
        _ <- Ns.int.getObjIterableAsOf(d2).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2).toList)
        _ <- Ns.int.getObjIterableAsOf(d3).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2, 3).toList)
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


        _ <- Ns.int.getObjArraySince(t3).map(_.map(_.int) ==> Array())
        _ <- Ns.int.getObjArraySince(t2).map(_.map(_.int) ==> Array(3))
        _ <- Ns.int.getObjArraySince(t1).map(_.map(_.int) ==> Array(2, 3))
        _ <- Ns.int.getObjArraySince(t1, 1).map(_.map(_.int) ==> Array(2))

        _ <- Ns.int.getObjArraySince(tx3).map(_.map(_.int) ==> Array())
        _ <- Ns.int.getObjArraySince(tx2).map(_.map(_.int) ==> Array(3))
        _ <- Ns.int.getObjArraySince(tx1).map(_.map(_.int) ==> Array(2, 3))
        _ <- Ns.int.getObjArraySince(tx1, 1).map(_.map(_.int) ==> Array(2))

        // Same about Date precision as above
        //        _ <- Ns.int.getObjArraySince(d3).map(_.map(_.int) ==> Array())
        //        _ <- Ns.int.getObjArraySince(d2).map(_.map(_.int) ==> Array(3))
        //        _ <- Ns.int.getObjArraySince(d1).map(_.map(_.int) ==> Array(2, 3))
        //        _ <- Ns.int.getObjArraySince(d1, 1).map(_.map(_.int) ==> Array(2))


        _ <- Ns.int.getObjIterableSince(t3).map(_.iterator.toList.map(_.int) ==> Iterator().toList)
        _ <- Ns.int.getObjIterableSince(t2).map(_.iterator.toList.map(_.int) ==> Iterator(3).toList)
        _ <- Ns.int.getObjIterableSince(t1).map(_.iterator.toList.map(_.int) ==> Iterator(2, 3).toList)
        _ <- Ns.int.getObjIterableSince(tx3).map(_.iterator.toList.map(_.int) ==> Iterator().toList)
        _ <- Ns.int.getObjIterableSince(tx2).map(_.iterator.toList.map(_.int) ==> Iterator(3).toList)
        _ <- Ns.int.getObjIterableSince(tx1).map(_.iterator.toList.map(_.int) ==> Iterator(2, 3).toList)


        // Same about Date precision as above
        //        _ <- Ns.int.getObjIterableSince(d3).map(_.iterator.toList.map(_.int) ==> Iterator().toList)
        //        _ <- Ns.int.getObjIterableSince(d2).map(_.iterator.toList.map(_.int) ==> Iterator(3).toList)
        //        _ <- Ns.int.getObjIterableSince(d1).map(_.iterator.toList.map(_.int) ==> Iterator(2, 3).toList)
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

        _ <- Ns.int.getObjArrayWith(saveTx2).map(_.map(_.int) ==> Array(1, 2))
        _ <- Ns.int.getObjArrayWith(saveTx2, saveTx3).map(_.map(_.int) ==> Array(1, 2, 3))
        _ <- Ns.int.getObjArrayWith(2, saveTx2, saveTx3).map(_.map(_.int) ==> Array(1, 2))

        _ <- Ns.int.getObjIterableWith(saveTx2).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2).toList)
        _ <- Ns.int.getObjIterableWith(saveTx2, saveTx3).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2, 3).toList)
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
