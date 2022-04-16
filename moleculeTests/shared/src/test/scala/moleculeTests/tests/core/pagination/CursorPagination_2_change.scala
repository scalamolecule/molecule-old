package moleculeTests.tests.core.pagination

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

object CursorPagination_2_change extends AsyncTestSuite {

  lazy val tests = Tests {

        "Asc" - core { implicit conn =>
          for {
            _ <- Ref2.int2.insert(1, 2, 3, 4, 5)

            // Page 1 (empty cursor)
            cursor <- Ref2.int2.a1.get(2, "").map { case (page, cursor, more) =>
              page ==> List(1, 2)
              more ==> 3
              cursor
            }

            // Page 2
            cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
              page ==> List(3, 4)
              more ==> 1
              cursor
            }

            // Page 3, last page
            cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
              page ==> List(5)
              more ==> 0 // currently no further rows
              cursor
            }

            // Page 2, go backwards with negative limit
            cursor <- Ref2.int2.a1.get(-2, cursor).map { case (page, cursor, more) =>
              page ==> List(3, 4)
              more ==> 2
              cursor
            }

            // Page 1
            _ <- Ref2.int2.a1.get(-2, cursor).map { case (page, _, more) =>
              page ==> List(1, 2)
              more ==> 0
            }
          } yield ()
        }
    //
    //
    //
    //
    //    "Beyond first/last" - core { implicit conn =>
    //      for {
    //        _ <- Ref2.int2.insert(1, 2, 3, 4, 5)
    //        _ = println("a,,".split(",", -1).length)
    //
    //        // Page 1 (empty cursor)
    //        cursor <- Ref2.int2.a1.get(2, "").map { case (page, cursor, more) =>
    //          page ==> List(1, 2)
    //          more ==> 3
    //          cursor
    //        }
    //
    //        // Page 2
    //        cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
    //          page ==> List(3, 4)
    //          more ==> 1
    //          cursor
    //        }
    //
    //        // Page 3, last page
    //        cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
    //          page ==> List(5)
    //          more ==> 0 // currently no further rows
    //          cursor
    //        }
    //
    //        // Page 4, doesn't exist
    //        cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
    //          page ==> Nil
    //          more ==> 0
    //          cursor
    //        }
    //
    //        // Page 2
    //        cursor <- Ref2.int2.a1.get(-2, cursor).map { case (page, cursor, more) =>
    //          page ==> List(3, 4)
    //          more ==> 2
    //          cursor
    //        }
    //
    //        // Page 1
    //        _ <- Ref2.int2.a1.get(-2, cursor).map { case (page, _, more) =>
    //          page ==> List(1, 2)
    //          more ==> 0
    //        }
    //      } yield ()
    //    }
    //
    //
    //    "Data added at end" - core { implicit conn =>
    //      for {
    //        _ <- Ref2.int2.insert(1, 2,3)
    //
    //        // Page 1
    //        next <- Ref2.int2.a1.get(2, "").map { case (page, prev, next) =>
    //          page ==> List(1, 2)
    //          prev ==> "first"
    //          next
    //        }
    //
    //        // Page 2
    //        _ <- Ref2.int2.a1.get(2, next).map { case (page, _, _) =>
    //          page ==> List(3)
    //          next ==> "last"
    //        }
    //
    //        _ <- Ref2.int2(4).save
    //
    //        // Page 2 reloaded, 4 added
    //        _ <- Ref2.int2.a1.get(2, next).map { case (page, _, _) =>
    //          page ==> List(3, 4)
    //          next
    //        }
    //
    //        _ <- Ref2.int2(4).save
    //
    //        // Page 2 reloaded, 4 added
    //        _ <- Ref2.int2.a1.get(2, next).map { case (page, _, _) =>
    //          page ==> List(3, 4)
    //          next
    //        }
    //      } yield ()
    //    }
    //
    //
    //    "Empty result set" - core { implicit conn =>
    //      for {
    //        // Use empty string as cursor for initial fetch of page 1
    //        next1 <- Ref2.int2.a1.get(2, "").map { case (page, prev, next) =>
    //          page ==> Nil
    //          prev ==> "first"
    //          next
    //        }
    //
    //        // Data added before next page is fetched
    //        //        _ <- r1.eid.retract
    //        _ <- Ref2.int2(2).save
    //        //        b = r1.eids(1)
    //        //        _ <- Ns(b).int(50).update
    //
    //
    //        //        //        _ <- Ref2.int2.a1.getAsOf(t1, 2, 2).map { res =>
    //        //        _ <- Ref2.int2.a1.get(2, 2).map { res =>
    //        //          //        _ <- Ref2.int2.a1.t.>=(t1).op.get.map { res =>
    //        //          //        _ <- Ns.str.int.t.>(t1).op.getHistory.map { res =>
    //        //          //        _ <- Ns.str.int.t.>(t1).getHistory.map { res =>
    //        //
    //        //          println("-------")
    //        //          res._1 foreach println
    //        //
    //        //          //          res._1 ==> List(3, 5)
    //        //        }
    //        //
    //        //        _ <- Ref2.int2.a1.getSince(t1).map { res =>
    //        //          println("-------")
    //        //          res foreach println
    //        //
    //        //          //          res ==> List(2)
    //        //        }
    //
    //        // Continue from where we were regardless of added 2
    //        //        _ <- Ref2.int2.a1.getAsOf(t1, 2, 2).map { case (page2, _) =>
    //        prev <- Ref2.int2.a1.get(2, next1).map { case (page, prev, next) =>
    //          page ==> List(5, 7)
    //          next ==> "last"
    //          prev
    //        }
    //
    //        _ <- Ref2.int2.a1.get(2, next1).map { case (page, prev, next) =>
    //          page ==> List(5, 7)
    //          next ==> "last"
    //        }
    //      } yield ()
    //    }
    //
    //    "Skipped data" - core { implicit conn =>
    //      for {
    //
    //        eids <- Ref2.int2.insert(1, 2, 3, 4).map(_.eids)
    //
    //        _ <- Ref2.int2.a1.get(2, 0).map(_._2 ==> List(1, 2))
    //
    //        // First row (1) retracted before next page is fetched
    //        _ <- eids.head.retract
    //
    //        // 3 is never shown!
    //        _ <- Ref2.int2.a1.get(2, 2).map(_._2 ==> List(4))
    //      } yield ()
    //    }


    //    "Types"
    //    "Data added before cursor"
    //    "Data added after cursor"
    //
    //    "Data retracted before cursor"
    //    "Data retracted after cursor"
    //
    //    "Cursor changed"
    //    "Cursor retracted"
  }
}

