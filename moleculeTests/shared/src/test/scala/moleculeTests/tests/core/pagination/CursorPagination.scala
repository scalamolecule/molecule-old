package moleculeTests.tests.core.pagination

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

object CursorPagination extends AsyncTestSuite {

  lazy val tests = Tests {


    "no changes" - core { implicit conn =>
      for {
        r <- Ns.int.insert(1, 2, 3, 4, 5)

        // Page 1 (empty cursor)
        next <- Ns.int.a1.get(2, "").map { case (page, prev, next) =>
          page ==> List(1, 2)
          prev ==> "first"
          next
        }

        // Page 2
        next <- Ns.int.a1.get(2, next).map { case (page, _, next) =>
          page ==> List(3, 4)
          next
        }

        // Page 3, last page
        prev <- Ns.int.a1.get(2, next).map { case (page, prev, next) =>
          page ==> List(5)
          next ==> "last"
          prev
        }

        // Page 2
        prev <- Ns.int.a1.get(2, prev).map { case (page, prev, _) =>
          page ==> List(3, 4)
          prev
        }

        // Page 1
        _ <- Ns.int.a1.get(2, prev).map { case (page, prev, _) =>
          page ==> List(1, 2)
          prev ==> "first"
        }
      } yield ()
    }


    "snapshot cursor" - core { implicit conn =>
      for {
        r <- Ns.int.insert(1, 3, 5, 7, 9)

        // Use empty string as cursor for initial fetch of page 1
        next <- Ns.int.a1.get(2, "snapshot").map { case (page, prev, next) =>
          page ==> List(1, 3)
          prev ==> "first"
          next
        }

        // All changes in-between page loads won't affect snapshot pagination
        // since we use a snapshot from the first page load

        // add 2
        _ <- Ns.int(2).save

        // update 5 to 50
        e5 = r.eids(2)
        _ <- Ns(e5).int(50).update

        // retract 7
        e7 = r.eids(3)
        _ <- e7.retract


        // Get next page of the snapshot, ignores changes
        next <- Ns.int.a1.get(2, next).map { case (page, prev, next) =>
          page ==> List(5, 7)
          next
        }
        // Equivalent to calling getAsOf
        _ <- Ns.int.a1.getAsOf(r.t, 2, 2).map(_._1 ==> List(5, 7))

        // Reaching the end
        prev <- Ns.int.a1.get(2, next).map { case (page, prev, next) =>
          page ==> List(9)
          next ==> "last"
          prev
        }

        // Go to previous page
        _ <- Ns.int.a1.get(2, prev).map { case (page, prev, next) =>
          page ==> List(1, 3)
          prev ==> "first"
          next
        }
      } yield ()
    }

    "Empty result set" - core { implicit conn =>
      for {
        // Use empty string as cursor for initial fetch of page 1
        next1 <- Ns.int.a1.get(2, "").map { case (page, prev, next) =>
          page ==> Nil
          prev ==> "first"
          next
        }

        // Data added before next page is fetched
        //        _ <- r1.eid.retract
        _ <- Ns.int(2).save
        //        b = r1.eids(1)
        //        _ <- Ns(b).int(50).update


        //        //        _ <- Ns.int.a1.getAsOf(t1, 2, 2).map { res =>
        //        _ <- Ns.int.a1.get(2, 2).map { res =>
        //          //        _ <- Ns.int.a1.t.>=(t1).op.get.map { res =>
        //          //        _ <- Ns.str.int.t.>(t1).op.getHistory.map { res =>
        //          //        _ <- Ns.str.int.t.>(t1).getHistory.map { res =>
        //
        //          println("-------")
        //          res._1 foreach println
        //
        //          //          res._1 ==> List(3, 5)
        //        }
        //
        //        _ <- Ns.int.a1.getSince(t1).map { res =>
        //          println("-------")
        //          res foreach println
        //
        //          //          res ==> List(2)
        //        }

        // Continue from where we were regardless of added 2
        //        _ <- Ns.int.a1.getAsOf(t1, 2, 2).map { case (page2, _) =>
        prev <- Ns.int.a1.get(2, next1).map { case (page, prev, next) =>
          page ==> List(5, 7)
          next ==> "last"
          prev
        }

        _ <- Ns.int.a1.get(2, next1).map { case (page, prev, next) =>
          page ==> List(5, 7)
          next ==> "last"
        }
      } yield ()
    }

    //    "Skipped data" - core { implicit conn =>
    //      for {
    //
    //        eids <- Ns.int.insert(1, 2, 3, 4).map(_.eids)
    //
    //        _ <- Ns.int.a1.get(2, 0).map(_._2 ==> List(1, 2))
    //
    //        // First row (1) retracted before next page is fetched
    //        _ <- eids.head.retract
    //
    //        // 3 is never shown!
    //        _ <- Ns.int.a1.get(2, 2).map(_._2 ==> List(4))
    //      } yield ()
    //    }
  }
}
