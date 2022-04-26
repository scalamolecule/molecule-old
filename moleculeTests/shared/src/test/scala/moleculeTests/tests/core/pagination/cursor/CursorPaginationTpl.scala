package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.annotation.nowarn

object CursorPaginationTpl extends AsyncTestSuite {
  val x = ""

  // Allow pattern matching the result only without warnings
  @nowarn lazy val tests = Tests {

//    "Basics (no change)" - {
//      "Forward, asc" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3, 4, 5)
//          c <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 3) => c }
//          c <- Ns.int.a1.get(2, c).map { case (List(3, 4), c, 1) => c }
//          c <- Ns.int.a1.get(2, c).map { case (List(5), c, 0) => c }
//          c <- Ns.int.a1.get(-2, c).map { case (List(3, 4), c, 2) => c }
//          _ <- Ns.int.a1.get(-2, c).map { case (List(1, 2), _, 0) => () }
//        } yield ()
//      }
//
//      "Forward, desc" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3, 4, 5)
//          c <- Ns.int.d1.get(2, x).map { case (List(5, 4), c, 3) => c }
//          c <- Ns.int.d1.get(2, c).map { case (List(3, 2), c, 1) => c }
//          c <- Ns.int.d1.get(2, c).map { case (List(1), c, 0) => c }
//          c <- Ns.int.d1.get(-2, c).map { case (List(3, 2), c, 2) => c }
//          _ <- Ns.int.d1.get(-2, c).map { case (List(5, 4), _, 0) => () }
//        } yield ()
//      }
//
//      "Backward, asc" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3, 4, 5)
//          c <- Ns.int.a1.get(-2, x).map { case (List(4, 5), c, 3) => c }
//          c <- Ns.int.a1.get(-2, c).map { case (List(2, 3), c, 1) => c }
//          c <- Ns.int.a1.get(-2, c).map { case (List(1), c, 0) => c }
//          c <- Ns.int.a1.get(2, c).map { case (List(2, 3), c, 2) => c }
//          _ <- Ns.int.a1.get(2, c).map { case (List(4, 5), _, 0) => () }
//        } yield ()
//      }
//
//      "Backward, desc" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3, 4, 5)
//          c <- Ns.int.d1.get(-2, x).map { case (List(2, 1), c, 3) => c }
//          c <- Ns.int.d1.get(-2, c).map { case (List(4, 3), c, 1) => c }
//          c <- Ns.int.d1.get(-2, c).map { case (List(5), c, 0) => c }
//          c <- Ns.int.d1.get(2, c).map { case (List(4, 3), c, 2) => c }
//          _ <- Ns.int.d1.get(2, c).map { case (List(2, 1), _, 0) => () }
//        } yield ()
//      }
//
//      "Expressions allowed" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(0, 1, 2, 3, 4, 5)
//          c <- Ns.int.>(0).a1.get(2, x).map { case (List(1, 2), c, 3) => c }
//          c <- Ns.int.>(0).a1.get(2, c).map { case (List(3, 4), c, 1) => c }
//          c <- Ns.int.>(0).a1.get(2, c).map { case (List(5), c, 0) => c }
//          c <- Ns.int.>(0).a1.get(-2, c).map { case (List(3, 4), c, 2) => c }
//          _ <- Ns.int.>(0).a1.get(-2, c).map { case (List(1, 2), _, 0) => () }
//        } yield ()
//      }
//
//      "Repeated calls on first" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3)
//
//          c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c }
//          _ <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c ==> c1 }
//          _ <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c ==> c1 }
//        } yield ()
//      }
//
//      "Repeated calls on intermediary" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3)
//
//          c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c }
//          c2 <- Ns.int.a1.get(2, c1).map { case (List(3), c, 0) => c }
//          _ <- Ns.int.a1.get(2, c1).map { case (List(3), c, 0) => c ==> c2 }
//
//          // Adding new last row withing this page will change the next cursor
//          _ <- Ns.int(4).save
//          c3 <- Ns.int.a1.get(2, c1).map { case (List(3, 4), c, 0) => c }
//          c3 <- Ns.int.a1.get(2, c2).map { case (List(4), c, 0) => c }
//        } yield ()
//      }
//
//      "Re-using previous cursor" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3, 4, 5)
//
//          c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 3) => c }
//          c2 <- Ns.int.a1.get(2, c1).map { case (List(3, 4), c, 1) => c }
//          c3 <- Ns.int.a1.get(2, c2).map { case (List(5), c, 0) => c }
//
//          c2 <- Ns.int.a1.get(2, c1).map { case (List(3, 4), c, 1) => c }
//
//        } yield ()
//      }
//
//      "Repeated calls on last (forward)" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3)
//
//          c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c }
//          c2 <- Ns.int.a1.get(2, c1).map { case (List(3), c, 0) => c }
//
//          // Repeated call on end gives same cursor so that we can catch new data
//          _ <- Ns.int.a1.get(2, c2).map { case (List(3), c, 0) => c ==> c2 }
//          _ <- Ns.int(4).save
//          _ <- Ns.int.a1.get(2, c2).map { case (List(3, 4), c, 0) => c ==> c2 }
//
//          // New cursor if new data goes beyond old cursor point
//          _ <- Ns.int(5).save
//          c3 <- Ns.int.a1.get(2, c2).map { case (List(3, 4), c, 1) => c }
//          _ <- Ns.int.a1.get(2, c3).map { case (List(5), c, 1) => c ==> c3 }
//
//          // We can go back still
//          c4 <- Ns.int.a1.get(-2, c3).map { case (List(3, 4), c, 2) => c }
//          c5 <- Ns.int.a1.get(-2, c4).map { case (List(1, 2), c, 0) => c }
//        } yield ()
//      }
//
//
//      "Repeated calls on last (backward)" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 2, 3)
//
//          c1 <- Ns.int.a1.get(-2, x).map { case (List(2, 3), c, 1) => c }
//          c2 <- Ns.int.a1.get(-2, c1).map { case (List(1), c, 0) => c }
//          _ <- Ns.int.a1.get(-2, c2).map { case (List(1), c, 0) => c ==> c2 }
//        } yield ()
//      }
//    }
//
//
//    "Add row after cursor" - {
//      "Forward" - core { implicit conn =>
//        for {
//          _ <- Ns.str.int insert List(
//            ("a", 1),
//            ("ba", 1),
//            ("c", 1),
//          )
//
//          // Order of non-sorted attributes is non-deterministic
//          // (but will likely not vary in-between internal indexing jobs)
//          _ <- Ns.str.int.get.map(_ ==> List(
//            ("a", 1),
//            ("ba", 1),
//            ("c", 1),
//          ))
//
//          c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 1), ("ba", 1)), c, 1) => c }
//
//          _ <- Ns.str("bb").int(1).save
//          _ <- Ns.str.int.get.map(_ ==> List(
//            ("a", 1),
//            ("ba", 1), // cursor row
//            ("bb", 1), // added row after cursor row
//            ("c", 1),
//          ))
//
//          // Page 2 including new bb row since it was after cursor row
//          _ <- Ns.str.int.a1.get(2, c).map { case (List(("bb", 1), ("c", 1)), _, 0) => () }
//        } yield ()
//      }
//    }
//
//
//    "Add row before cursor" - {
//      "Forward" - core { implicit conn =>
//        for {
//          _ <- Ns.str.int insert List(
//            ("a", 1),
//            ("bb", 1),
//            ("c", 1),
//          )
//          _ <- Ns.str.int.get.map(_ ==> List(
//            ("a", 1),
//            ("bb", 1),
//            ("c", 1),
//          ))
//
//          c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 1), ("bb", 1)), c, 1) => c }
//
//          _ <- Ns.str("ba").int(1).save
//          _ <- Ns.str.int.get.map(_ ==> List(
//            ("a", 1),
//            ("ba", 1), // added row before cursor row
//            ("bb", 1), // cursor row
//            ("c", 1),
//          ))
//
//          // Page 2 excluding new ba row since it was before cursor row
//          _ <- Ns.str.int.a1.get(2, c).map { case (List(("c", 1)), _, 0) => () }
//        } yield ()
//      }
//    }





    "Cursor row retracted (or updated)" - {

      "First in window" - core { implicit conn =>
        for {
          b <- Ns.str.int insert List(
            ("a", 0),
            ("b", 1),
            ("c", 1),
            ("d", 2),
          ) map (_.eids(1))

          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("a", 0),
            ("b", 1),
            ("c", 1),
            ("d", 2),
          ))

          c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 0), ("b", 1)), c, 2) => c }

          _ <- b.retract
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("a", 0),
            ("c", 1),
            ("d", 2),
          ))

          // Page 2 excluding new ba row since it was before cursor row
          _ <- Ns.str.int.a1.get(2, c).map {
            case (List(("c", 1), ("d", 2)), _, 0) => ()
          }
        } yield ()
      }


      "Last in window" - core { implicit conn =>
        for {
          c <- Ns.str.int insert List(
            ("a", 0),
            ("b", 1),
            ("c", 1),
            ("d", 2),
          ) map (_.eids(2))

          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("a", 0),
            ("b", 1),
            ("c", 1),
            ("d", 2),
          ))

          c1 <- Ns.str.int.a1.get(3, x).map { case (List(("a", 0), ("b", 1), ("c", 1)), c, 1) => c }

          _ <- c.retract
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("a", 0),
            ("b", 1),
            ("d", 2),
          ))

          // Page 2 excluding new ba row since it was before cursor row
          _ <- Ns.str.int.a1.get(3, c1).map { case (List(("d", 2)), _, 0) => () }
        } yield ()
      }




    }


//    "Row added before cursor" - {
//
//      "no change" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 3, 5)
//
//          cursor <- Ns.int.a1.get(2, x).map { case (List(1, 3), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5), cursor, 0) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1, 3), cursor, 0) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1, 3), cursor, 0) => cursor }
//        } yield ()
//      }
//
//      "row added" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 3, 5)
//
//          cursor <- Ns.int.a1.get(2, x).map { case (List(1, 3), cursor, 1) => cursor }
//          _ <- Ns.int(2).save
//
//          // Correctly continuing from 5
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5), cursor, 0) => cursor }
//
//          // 2 shows on the way back
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(2, 3), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 0) => cursor }
//        } yield ()
//      }
//
//      "row added 2" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 3, 5)
//
//          cursor <- Ns.int.a1.get(2, x).map { case (List(1, 3), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5), cursor, 0) => cursor }
//          _ <- Ns.int(2).save
//
//          // 2 shows on the way back
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(2, 3), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 0) => cursor }
//        } yield ()
//      }
//
//      "row added 3" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 3, 5)
//
//          cursor <- Ns.int.a1.get(2, x).map { case (List(1, 3), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5), cursor, 0) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1, 3), cursor, 1) => cursor }
//          _ <- Ns.int(2).save
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 0) => cursor }
//        } yield ()
//      }
//
//
//
//
//      //      "no change" - core { implicit conn =>
//      //        for {
//      //          _ <- Ns.int.insert(1, 3, 5, 7, 9)
//      //          cursor <- Ns.int.a1.get(2, emptyS).map { case (List(1, 3), cursor, 3) => cursor }
//      //          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5, 7), cursor, 1) => cursor }
//      //          cursor <- Ns.int.a1.get(2, cursor).map { case (List(9), cursor, 0) => cursor }
//      //          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(5, 7), cursor, 2) => cursor }
//      //          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1, 3), cursor, 0) => cursor }
//      //        } yield ()
//      //      }
//      //
//      //      "row added" - core { implicit conn =>
//      //        for {
//      //          _ <- Ns.int.insert(1, 3, 5, 7, 9)
//      //          cursor <- Ns.int.a1.get(2, emptyS).map { case (List(1, 3), cursor, 3) => cursor }
//      //          _ <- Ns.int(2).save
//      //
//      //          // Correctly continuing from 5
//      //          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5, 7), cursor, 1) => cursor }
//      //          cursor <- Ns.int.a1.get(2, cursor).map { case (List(9), cursor, 0) => cursor }
//      //          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(5, 7), cursor, 3) => cursor }
//      //
//      //          // 2 shows on the way back
//      //          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(2, 3), cursor, 1) => cursor }
//      //          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 1) => cursor }
//      //        } yield ()
//      //      }
//      //
//      //      "row added" - core { implicit conn =>
//      //        for {
//      //          _ <- Ns.int.insert(1, 3, 5, 7, 9)
//      //          cursor <- Ns.int.a1.get(2, emptyS).map { case (List(1, 3), cursor, 3) => cursor }
//      //          _ <- Ns.int(2).save
//      //
//      //          // Correctly continuing from 5
//      //          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5, 7), cursor, 1) => cursor }
//      //          cursor <- Ns.int.a1.get(2, cursor).map { case (List(9), cursor, 0) => cursor }
//      //          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(5, 7), cursor, 3) => cursor }
//      //
//      //          // 2 shows on the way back
//      //          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(2, 3), cursor, 1) => cursor }
//      //          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 1) => cursor }
//      //        } yield ()
//      //      }
//
//
//      //
//      //    "Beyond first/last" - core { implicit conn =>
//      //      for {
//      //        _ <- Ref2.int2.insert(1, 2, 3, 4, 5)
//      //        _ = println("a,,".split(",", -1).length)
//      //
//      //        // Page 1 (empty cursor)
//      //        cursor <- Ref2.int2.a1.get(2, "").map { case (page, cursor, more) =>
//      //          page ==> List(1, 2)
//      //          more ==> 3
//      //          cursor
//      //        }
//      //
//      //        // Page 2
//      //        cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
//      //          page ==> List(3, 4)
//      //          more ==> 1
//      //          cursor
//      //        }
//      //
//      //        // Page 3, last page
//      //        cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
//      //          page ==> List(5)
//      //          more ==> 0 // currently no further rows
//      //          cursor
//      //        }
//      //
//      //        // Page 4, doesn't exist
//      //        cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
//      //          page ==> Nil
//      //          more ==> 0
//      //          cursor
//      //        }
//      //
//      //        // Page 2
//      //        cursor <- Ref2.int2.a1.get(-2, cursor).map { case (page, cursor, more) =>
//      //          page ==> List(3, 4)
//      //          more ==> 2
//      //          cursor
//      //        }
//      //
//      //        // Page 1
//      //        _ <- Ref2.int2.a1.get(-2, cursor).map { case (page, _, more) =>
//      //          page ==> List(1, 2)
//      //          more ==> 0
//      //        }
//      //      } yield ()
//      //    }
//      //
//      //
//      //    "Data added at end" - core { implicit conn =>
//      //      for {
//      //        _ <- Ref2.int2.insert(1, 2,3)
//      //
//      //        // Page 1
//      //        next <- Ref2.int2.a1.get(2, "").map { case (page, prev, next) =>
//      //          page ==> List(1, 2)
//      //          prev ==> "first"
//      //          next
//      //        }
//      //
//      //        // Page 2
//      //        _ <- Ref2.int2.a1.get(2, next).map { case (page, _, _) =>
//      //          page ==> List(3)
//      //          next ==> "last"
//      //        }
//      //
//      //        _ <- Ref2.int2(4).save
//      //
//      //        // Page 2 reloaded, 4 added
//      //        _ <- Ref2.int2.a1.get(2, next).map { case (page, _, _) =>
//      //          page ==> List(3, 4)
//      //          next
//      //        }
//      //
//      //        _ <- Ref2.int2(4).save
//      //
//      //        // Page 2 reloaded, 4 added
//      //        _ <- Ref2.int2.a1.get(2, next).map { case (page, _, _) =>
//      //          page ==> List(3, 4)
//      //          next
//      //        }
//      //      } yield ()
//      //    }
//      //
//      //
//      //    "Empty result set" - core { implicit conn =>
//      //      for {
//      //        // Use empty string as cursor for initial fetch of page 1
//      //        next1 <- Ref2.int2.a1.get(2, "").map { case (page, prev, next) =>
//      //          page ==> Nil
//      //          prev ==> "first"
//      //          next
//      //        }
//      //
//      //        // Data added before next page is fetched
//      //        //        _ <- r1.eid.retract
//      //        _ <- Ref2.int2(2).save
//      //        //        b = r1.eids(1)
//      //        //        _ <- Ns(b).int(50).update
//      //
//      //
//      //        //        //        _ <- Ref2.int2.a1.getAsOf(t1, 2, 2).map { res =>
//      //        //        _ <- Ref2.int2.a1.get(2, 2).map { res =>
//      //        //          //        _ <- Ref2.int2.a1.t.>=(t1).op.get.map { res =>
//      //        //          //        _ <- Ns.str.int.t.>(t1).op.getHistory.map { res =>
//      //        //          //        _ <- Ns.str.int.t.>(t1).getHistory.map { res =>
//      //        //
//      //        //          println("-------")
//      //        //          res._1 foreach println
//      //        //
//      //        //          //          res._1 ==> List(3, 5)
//      //        //        }
//      //        //
//      //        //        _ <- Ref2.int2.a1.getSince(t1).map { res =>
//      //        //          println("-------")
//      //        //          res foreach println
//      //        //
//      //        //          //          res ==> List(2)
//      //        //        }
//      //
//      //        // Continue from where we were regardless of added 2
//      //        //        _ <- Ref2.int2.a1.getAsOf(t1, 2, 2).map { case (page2, _) =>
//      //        prev <- Ref2.int2.a1.get(2, next1).map { case (page, prev, next) =>
//      //          page ==> List(5, 7)
//      //          next ==> "last"
//      //          prev
//      //        }
//      //
//      //        _ <- Ref2.int2.a1.get(2, next1).map { case (page, prev, next) =>
//      //          page ==> List(5, 7)
//      //          next ==> "last"
//      //        }
//      //      } yield ()
//      //    }
//      //
//      //    "Skipped data" - core { implicit conn =>
//      //      for {
//      //
//      //        eids <- Ref2.int2.insert(1, 2, 3, 4).map(_.eids)
//      //
//      //        _ <- Ref2.int2.a1.get(2, 0).map(_._2 ==> List(1, 2))
//      //
//      //        // First row (1) retracted before next page is fetched
//      //        _ <- eids.head.retract
//      //
//      //        // 3 is never shown!
//      //        _ <- Ref2.int2.a1.get(2, 2).map(_._2 ==> List(4))
//      //      } yield ()
//      //    }
//
//
//      //    "Types"
//      //    "Data added before cursor"
//      //    "Data added after cursor"
//      //
//      //    "Data retracted before cursor"
//      //    "Data retracted after cursor"
//      //
//      //    "Cursor changed"
//      //    "Cursor retracted"
//    }
  }
}
