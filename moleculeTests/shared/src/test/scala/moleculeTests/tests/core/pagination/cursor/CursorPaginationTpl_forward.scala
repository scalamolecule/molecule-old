package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.annotation.nowarn

object CursorPaginationTpl_forward extends AsyncTestSuite {
  val x = ""


  // Non-sorted values (str) have non-deterministic order
  // "Window" is rows sharing same sorted value(s) - here rows with int value 1

  // Allow pattern matching the result only without warnings
  @nowarn lazy val tests = Tests {

    "Basics (no change)" - {
      "Forward, asc" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2, 3, 4, 5)
          c <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 3) => c }
          c <- Ns.int.a1.get(2, c).map { case (List(3, 4), c, 1) => c }
          c <- Ns.int.a1.get(2, c).map { case (List(5), c, 0) => c }
          c <- Ns.int.a1.get(-2, c).map { case (List(3, 4), c, 2) => c }
          _ <- Ns.int.a1.get(-2, c).map { case (List(1, 2), _, 0) => () }
        } yield ()
      }

      "Forward, desc" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2, 3, 4, 5)
          c <- Ns.int.d1.get(2, x).map { case (List(5, 4), c, 3) => c }
          c <- Ns.int.d1.get(2, c).map { case (List(3, 2), c, 1) => c }
          c <- Ns.int.d1.get(2, c).map { case (List(1), c, 0) => c }
          c <- Ns.int.d1.get(-2, c).map { case (List(3, 2), c, 2) => c }
          _ <- Ns.int.d1.get(-2, c).map { case (List(5, 4), _, 0) => () }
        } yield ()
      }

      "Backward, asc" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2, 3, 4, 5)
          c <- Ns.int.a1.get(-2, x).map { case (List(4, 5), c, 3) => c }
          c <- Ns.int.a1.get(-2, c).map { case (List(2, 3), c, 1) => c }
          c <- Ns.int.a1.get(-2, c).map { case (List(1), c, 0) => c }
          c <- Ns.int.a1.get(2, c).map { case (List(2, 3), c, 2) => c }
          _ <- Ns.int.a1.get(2, c).map { case (List(4, 5), _, 0) => () }
        } yield ()
      }

      "Backward, desc" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2, 3, 4, 5)
          c <- Ns.int.d1.get(-2, x).map { case (List(2, 1), c, 3) => c }
          c <- Ns.int.d1.get(-2, c).map { case (List(4, 3), c, 1) => c }
          c <- Ns.int.d1.get(-2, c).map { case (List(5), c, 0) => c }
          c <- Ns.int.d1.get(2, c).map { case (List(4, 3), c, 2) => c }
          _ <- Ns.int.d1.get(2, c).map { case (List(2, 1), _, 0) => () }
        } yield ()
      }

      "Sort value types" - core { implicit conn =>
        for {
          _ <- Ns.str.int.long.ref1.double.bool.date.uuid.uri.bigInt.bigDec.enumm insert List(
            ("a", 1, 1L, 1L, 1.1, true, date1, uuid1, uri1, bigInt1, bigDec1, enum1),
            ("b", 2, 2L, 2L, 2.2, false, date2, uuid2, uri2, bigInt2, bigDec2, enum2),
            ("c", 3, 3L, 3L, 3.3, false, date3, uuid3, uri3, bigInt3, bigDec3, enum3)
          )

          c <- Ns.str.a1.get(2, x).map { case (List("a", "b"), c, 1) => c }
          _ <- Ns.str.a1.get(2, c).map { case (List("c"), _, 0) => () }

          c <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c }
          _ <- Ns.int.a1.get(2, c).map { case (List(3), _, 0) => () }

          c <- Ns.long.a1.get(2, x).map { case (List(1L, 2L), c, 1) => c }
          _ <- Ns.long.a1.get(2, c).map { case (List(3L), _, 0) => () }

          c <- Ns.ref1.a1.get(2, x).map { case (List(1L, 2L), c, 1) => c }
          _ <- Ns.ref1.a1.get(2, c).map { case (List(3L), _, 0) => () }

          c <- Ns.double.a1.get(2, x).map { case (List(1.1, 2.2), c, 1) => c }
          _ <- Ns.double.a1.get(2, c).map { case (List(3.3), _, 0) => () }

          c <- Ns.bool.a1.get(1, x).map { case (List(false), c, 1) => c }
          _ <- Ns.bool.a1.get(1, c).map { case (List(true), _, 0) => () }

          c <- Ns.date.a1.get(2, x).map { case (List(`date1`, `date2`), c, 1) => c }
          _ <- Ns.date.a1.get(2, c).map { case (List(`date3`), _, 0) => () }

          c <- Ns.uuid.a1.get(2, x).map { case (List(`uuid1`, `uuid2`), c, 1) => c }
          _ <- Ns.uuid.a1.get(2, c).map { case (List(`uuid3`), _, 0) => () }

          c <- Ns.uri.a1.get(2, x).map { case (List(`uri1`, `uri2`), c, 1) => c }
          _ <- Ns.uri.a1.get(2, c).map { case (List(`uri3`), _, 0) => () }

          c <- Ns.bigInt.a1.get(2, x).map { case (List(`bigInt1`, `bigInt2`), c, 1) => c }
          _ <- Ns.bigInt.a1.get(2, c).map { case (List(`bigInt3`), _, 0) => () }

          c <- Ns.bigDec.a1.get(2, x).map { case (List(`bigDec1`, `bigDec2`), c, 1) => c }
          _ <- Ns.bigDec.a1.get(2, c).map { case (List(`bigDec3`), _, 0) => () }

          c <- Ns.enumm.a1.get(2, x).map { case (List(`enum1`, `enum2`), c, 1) => c }
          _ <- Ns.enumm.a1.get(2, c).map { case (List(`enum3`), _, 0) => () }
        } yield ()
      }

      "Sort value can have expression too" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(0, 1, 2, 3, 4, 5)
          c <- Ns.int.>(0).a1.get(2, x).map { case (List(1, 2), c, 3) => c }
          c <- Ns.int.>(0).a1.get(2, c).map { case (List(3, 4), c, 1) => c }
          c <- Ns.int.>(0).a1.get(2, c).map { case (List(5), c, 0) => c }
          c <- Ns.int.>(0).a1.get(-2, c).map { case (List(3, 4), c, 2) => c }
          _ <- Ns.int.>(0).a1.get(-2, c).map { case (List(1, 2), _, 0) => () }
        } yield ()
      }
    }


    "Add data" - {

      "Add rows (outside same sort value window)" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2, 3)

          c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c }

          // Current last page
          c2 <- Ns.int.a1.get(2, c1).map { case (List(3), c, 0) => c }

          // New data will show on new page
          // (regardless if it could have fitted on the old page)
          _ <- Ns.int(4).save
          c3 <- Ns.int.a1.get(2, c2).map { case (List(4), c, 0) => c }

          // Adding data beyond next page
          _ <- Ns.int.insert(5, 6, 7)
          c4 <- Ns.int.a1.get(2, c3).map { case (List(5, 6), c, 1) => c }
          _ <- Ns.int.a1.get(2, c4).map { case (List(7), _, 0) => () }
        } yield ()
      }

      "Add row after cursor within window" - core { implicit conn =>
        for {
          // Window of rows with same sort value (1)
          _ <- Ns.str.int insert List(
            ("a", 1),
            ("ba", 1),
            ("c", 1),
          )

          // Order of non-sorted attributes is non-deterministic
          // (but will likely not vary in-between internal indexing jobs)
          _ <- Ns.str.int.get.map(_ ==> List(
            ("a", 1),
            ("ba", 1),
            ("c", 1),
          ))

          c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 1), ("ba", 1)), c, 1) => c }

          _ <- Ns.str("bb").int(1).save
          _ <- Ns.str.int.get.map(_ ==> List(
            ("a", 1),
            ("ba", 1), // cursor row
            ("bb", 1), // added row after cursor row
            ("c", 1),
          ))

          // Page 2 including new bb row since it was after cursor row
          _ <- Ns.str.int.a1.get(2, c).map { case (List(("bb", 1), ("c", 1)), _, 0) => () }
        } yield ()
      }


      "Add row before cursor within window" - core { implicit conn =>
        for {
          _ <- Ns.str.int insert List(
            ("a", 1),
            ("bb", 1),
            ("c", 1),
          )
          _ <- Ns.str.int.get.map(_ ==> List(
            ("a", 1),
            ("bb", 1),
            ("c", 1),
          ))

          c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 1), ("bb", 1)), c, 1) => c }

          _ <- Ns.str("ba").int(1).save
          _ <- Ns.str.int.get.map(_ ==> List(
            ("a", 1),
            ("ba", 1), // added row before cursor row
            ("bb", 1), // cursor row
            ("c", 1),
          ))

          // Page 2 excluding new ba row since it was before cursor row
          _ <- Ns.str.int.a1.get(2, c).map { case (List(("c", 1)), _, 0) => () }
        } yield ()
      }
    }


    "Retract non-cursor row" - {

      "After cursor row" - core { implicit conn =>
        for {
          after <- Ns.str.int insert List(
            ("x", 0),
            ("a", 1), // (will be after cursor row in window when sorted)
            ("b", 1),
            ("y", 2),
          ) map (_.eids(1))

          // Sorted rows
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1), // cursor row
            ("a", 1), // after cursor row
            ("y", 2),
          ))

          c <- Ns.str.int.a1.get(2, x).map { case (List(("x", 0), ("b", 1)), c, 2) => c }

          _ <- after.retract
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("y", 2),
          ))

          // Continue after b
          _ <- Ns.str.int.a1.get(2, c).map { case (List(("y", 2)), _, 0) => () }
        } yield ()
      }

      "Before cursor row" - core { implicit conn =>
        for {
          before <- Ns.str.int insert List(
            ("x", 0),
            ("a", 1),
            ("b", 1), // (will be before cursor row in window when sorted)
            ("y", 2),
          ) map (_.eids(2))

          // Sorted rows
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1), // before cursor row
            ("a", 1), // cursor row
            ("y", 2),
          ))

          c <- Ns.str.int.a1.get(3, x).map { case (List(("x", 0), ("b", 1), ("a", 1)), c, 1) => c }

          _ <- before.retract
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("a", 1),
            ("y", 2),
          ))

          // Continue after a
          _ <- Ns.str.int.a1.get(3, c).map { case (List(("y", 2)), _, 0) => () }
        } yield ()
      }
    }


    "Retract cursor row, first in window" - {

      "Retraction only" - core { implicit conn =>
        for {
          cursorRowAsFirst <- Ns.str.int insert List(
            ("x", 0),
            ("a", 1),
            ("b", 1), // (will be first in window when sorted)
            ("y", 2),
          ) map (_.eids(2))

          // Sorted rows
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1), // cursor row (first in window)
            ("a", 1),
            ("y", 2),
          ))

          c <- Ns.str.int.a1.get(2, x).map { case (List(("x", 0), ("b", 1)), c, 2) => c }

          _ <- cursorRowAsFirst.retract
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("a", 1),
            ("y", 2),
          ))

          // Continue after x
          _ <- Ns.str.int.a1.get(2, c).map { case (List(("a", 1), ("y", 2)), _, 0) => () }
        } yield ()
      }

      "Retraction + added data" - core { implicit conn =>
        for {
          first <- Ns.str.int insert List(
            ("x", 0),
            ("a", 1),
            ("b", 1), // (will be first in window when sorted)
            ("y", 2),
          ) map (_.eids(2))

          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1), // cursor row (first in window)
            ("a", 1),
            ("y", 2),
          ))

          c <- Ns.str.int.a1.get(2, x).map { case (List(("x", 0), ("b", 1)), c, 2) => c }

          _ <- first.retract
          // Add data in window
          _ <- Ns.str("c").int(1).save

          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("a", 1),
            ("c", 1),
            ("y", 2),
          ))

          // Continue after x
          _ <- Ns.str.int.a1.get(2, c).map { case (List(("a", 1), ("c", 1)), _, 1) => () }
        } yield ()
      }
    }


    "Retract cursor row, last in window" - {

      "Retraction only" - core { implicit conn =>
        for {
          cursorRowAsLast <- Ns.str.int insert List(
            ("x", 0),
            ("a", 1), // (will be last in window when sorted)
            ("b", 1),
            ("y", 2),
          ) map (_.eids(1))

          // Sorted rows
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("a", 1), // cursor row (last in window)
            ("y", 2),
          ))

          c <- Ns.str.int.a1.get(3, x).map { case (List(("x", 0), ("b", 1), ("a", 1)), c, 1) => c }

          _ <- cursorRowAsLast.retract
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("y", 2),
          ))

          // Continue after b
          _ <- Ns.str.int.a1.get(3, c).map { case (List(("y", 2)), _, 0) => () }
        } yield ()
      }

      "Retraction + added data" - core { implicit conn =>
        for {
          cursorRowAsLast <- Ns.str.int insert List(
            ("x", 0),
            ("a", 1), // (will be last in window when sorted)
            ("b", 1),
            ("y", 2),
          ) map (_.eids(1))

          _ <- Ns(cursorRowAsLast).str.get.map(_ ==> List("a"))

          // Sorted rows
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("a", 1), // cursor row (last in window)
            ("y", 2),
          ))

          c <- Ns.str.int.a1.get(3, x).map { case (List(("x", 0), ("b", 1), ("a", 1)), c, 1) => c }

          _ <- cursorRowAsLast.retract
          // Add data in window
          _ <- Ns.str("c").int(1).save
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("c", 1),
            ("y", 2),
          ))

          // Continue after a
          _ <- Ns.str.int.a1.get(3, c).map { case (List(("c", 1), ("y", 2)), _, 0) => () }
        } yield ()
      }
    }


    "Retract cursor row, inside window" - {

      "Retraction only" - core { implicit conn =>
        for {
          cursorRow <- Ns.str.int insert List(
            ("x", 0),
            ("a", 1), // (will be cursor row when sorted)
            ("b", 1),
            ("c", 1),
            ("y", 2),
          ) map (_.eids(1))

          // Sorted rows
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("a", 1), // cursor row
            ("c", 1),
            ("y", 2),
          ))

          c <- Ns.str.int.a1.get(3, x).map { case (List(("x", 0), ("b", 1), ("a", 1)), c, 2) => c }

          _ <- cursorRow.retract
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("c", 1),
            ("y", 2),
          ))

          // Continue after b
          _ <- Ns.str.int.a1.get(3, c).map { case (List(("c", 1), ("y", 2)), _, 0) => () }
        } yield ()
      }

      "Retraction + added data" - core { implicit conn =>
        for {
          cursorRow <- Ns.str.int insert List(
            ("x", 0),
            ("a", 1), // (will be cursor row when sorted)
            ("b", 1),
            ("c", 1),
            ("y", 2),
          ) map (_.eids(1))

          // Sorted rows
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("a", 1), // cursor row
            ("c", 1),
            ("y", 2),
          ))

          c <- Ns.str.int.a1.get(3, x).map { case (List(("x", 0), ("b", 1), ("a", 1)), c, 2) => c }

          _ <- cursorRow.retract
          // Add data in window
          _ <- Ns.str("d").int(1).save
          _ <- Ns.str.int.a1.get.map(_ ==> List(
            ("x", 0),
            ("b", 1),
            ("d", 1), // (note that added data not automatically append as last)
            ("c", 1),
            ("y", 2),
          ))

          // Continue after b
          _ <- Ns.str.int.a1.get(3, c).map { case (List(("d", 1), ("c", 1), ("y", 2)), _, 0) => () }
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
