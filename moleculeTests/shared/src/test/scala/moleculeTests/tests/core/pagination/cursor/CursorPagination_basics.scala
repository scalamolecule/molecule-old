package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.annotation.nowarn

object CursorPagination_basics extends AsyncTestSuite {
  val x = ""

  // Allow pattern matching the result only
  @nowarn lazy val tests = Tests {

    "No change" - {

      "Unique sort values" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2, 3, 4, 5)

          // Page 1 (empty cursor)
          cursor <- Ns.int.a1.get(2, "").map { case (page, cursor, more) =>
            page ==> List(1, 2)
            more ==> 3
            cursor
          }

          // Page 2
          cursor <- Ns.int.a1.get(2, cursor).map { case (page, cursor, more) =>
            page ==> List(3, 4)
            more ==> 1
            cursor
          }

          // Page 3, last page
          cursor <- Ns.int.a1.get(2, cursor).map { case (page, cursor, more) =>
            page ==> List(5)
            more ==> 0 // currently no further rows
            cursor
          }

          // Page 2, go backwards with negative limit
          cursor <- Ns.int.a1.get(-2, cursor).map { case (page, cursor, more) =>
            page ==> List(3, 4)
            more ==> 2
            cursor
          }

          // Page 1
          _ <- Ns.int.a1.get(-2, cursor).map { case (page, _, more) =>
            page ==> List(1, 2)
            more ==> 0
          }
        } yield ()
      }


      "Redundant sort values" - core { implicit conn =>
        for {
          _ <- Ns.str.int insert List(
            ("a", 1),
            ("b", 1),
            ("c", 1),
            ("d", 2),
            ("e", 2),
          )

          // Page 1
          cursor <- Ns.str.int.a1.get(2, "").map { case (page, cursor, more) =>
            page ==> List(
              ("b", 1),
              ("a", 1),
            )
            more ==> 3
            cursor
          }

          // Page 2
          cursor <- Ns.str.int.a1.get(2, cursor).map { case (page, cursor, more) =>
            page ==> List(
              ("c", 1),
              ("e", 2),
            )
            more ==> 1
            cursor
          }

          // Page 3
          cursor <- Ns.str.int.a1.get(2, cursor).map { case (page, cursor, more) =>
            page ==> List(
              ("d", 2),
            )
            more ==> 0
            cursor
          }

          // Page 2
          cursor <- Ns.str.int.a1.get(-2, cursor).map { case (page, cursor, more) =>
            page ==> List(
              ("c", 1),
              ("e", 2),
            )
            more ==> 2
            cursor
          }

          // Page 1
          _ <- Ns.str.int.a1.get(-2, cursor).map { case (page, _, more) =>
            page ==> List(
              ("b", 1),
              ("a", 1),
            )
            more ==> 0
          }
        } yield ()
      }
    }


    "Repeated call on first" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c }
        _ <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c ==> c1 }
        _ <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c ==> c1 }
      } yield ()
    }

    "Repeated call on intermediary" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c }
        c2 <- Ns.int.a1.get(2, c1).map { case (List(3), c, 0) => c }
        _ <- Ns.int.a1.get(2, c1).map { case (List(3), c, 0) => c ==> c2 }

        // Adding new last row withing this page will change the next cursor
        _ <- Ns.int(4).save
        c3 <- Ns.int.a1.get(2, c1).map { case (List(3, 4), c, 0) => c }
        c3 <- Ns.int.a1.get(2, c2).map { case (List(4), c, 0) => c }
      } yield ()
    }

    "Re-using previous cursor" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3, 4, 5)

        c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 3) => c }
        c2 <- Ns.int.a1.get(2, c1).map { case (List(3, 4), c, 1) => c }
        c3 <- Ns.int.a1.get(2, c2).map { case (List(5), c, 0) => c }

        c2 <- Ns.int.a1.get(2, c1).map { case (List(3, 4), c, 1) => c }

      } yield ()
    }

    "Repeated call on last (forward)" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        c1 <- Ns.int.a1.get(2, x).map { case (List(1, 2), c, 1) => c }
        c2 <- Ns.int.a1.get(2, c1).map { case (List(3), c, 0) => c }

        // Repeated call on end gives same cursor so that we can catch new data
        _ <- Ns.int.a1.get(2, c2).map { case (List(3), c, 0) => c ==> c2}
        _ <- Ns.int(4).save
        _ <- Ns.int.a1.get(2, c2).map { case (List(3, 4), c, 0) => c ==> c2}

        // New cursor if new data goes beyond old cursor point
        _ <- Ns.int(5).save
        c3 <- Ns.int.a1.get(2, c2).map { case (List(3, 4), c, 1) => c}
        _ <- Ns.int.a1.get(2, c3).map { case (List(5), c, 1) => c ==> c3}

        // We can go back still
        c4 <- Ns.int.a1.get(-2, c3).map { case (List(3, 4), c, 2) => c }
        c5 <- Ns.int.a1.get(-2, c4).map { case (List(1, 2), c, 0) => c }
      } yield ()
    }

    "Repeated call on last (backward)" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        c1 <- Ns.int.a1.get(-2, x).map { case (List(2, 3), c, 1) => c }
        c2 <- Ns.int.a1.get(-2, c1).map { case (List(1), c, 0) => c }
        _ <- Ns.int.a1.get(-2, c2).map { case (List(1), c, 0) => c ==> c2}
      } yield ()
    }

    "Row added before cursor" - {

      "no change" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 3, 5)

          cursor <- Ns.int.a1.get(2, x).map { case (List(1, 3), cursor, 1) => cursor }
          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5), cursor, 0) => cursor }
          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1, 3), cursor, 0) => cursor }
          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1, 3), cursor, 0) => cursor }
        } yield ()
      }

      "row added" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 3, 5)

          cursor <- Ns.int.a1.get(2, x).map { case (List(1, 3), cursor, 1) => cursor }
          _ <- Ns.int(2).save

          // Correctly continuing from 5
          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5), cursor, 0) => cursor }

          // 2 shows on the way back
          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(2, 3), cursor, 1) => cursor }
          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 0) => cursor }
        } yield ()
      }

      "row added 2" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 3, 5)

          cursor <- Ns.int.a1.get(2, x).map { case (List(1, 3), cursor, 1) => cursor }
          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5), cursor, 0) => cursor }
          _ <- Ns.int(2).save

          // 2 shows on the way back
          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(2, 3), cursor, 1) => cursor }
          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 0) => cursor }
        } yield ()
      }

      "row added 3" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 3, 5)

          cursor <- Ns.int.a1.get(2, x).map { case (List(1, 3), cursor, 1) => cursor }
          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5), cursor, 0) => cursor }
          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1, 3), cursor, 1) => cursor }
          _ <- Ns.int(2).save
          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 0) => cursor }
        } yield ()
      }




//      "no change" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 3, 5, 7, 9)
//          cursor <- Ns.int.a1.get(2, emptyS).map { case (List(1, 3), cursor, 3) => cursor }
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5, 7), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(9), cursor, 0) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(5, 7), cursor, 2) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1, 3), cursor, 0) => cursor }
//        } yield ()
//      }
//
//      "row added" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 3, 5, 7, 9)
//          cursor <- Ns.int.a1.get(2, emptyS).map { case (List(1, 3), cursor, 3) => cursor }
//          _ <- Ns.int(2).save
//
//          // Correctly continuing from 5
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5, 7), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(9), cursor, 0) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(5, 7), cursor, 3) => cursor }
//
//          // 2 shows on the way back
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(2, 3), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 1) => cursor }
//        } yield ()
//      }
//
//      "row added" - core { implicit conn =>
//        for {
//          _ <- Ns.int.insert(1, 3, 5, 7, 9)
//          cursor <- Ns.int.a1.get(2, emptyS).map { case (List(1, 3), cursor, 3) => cursor }
//          _ <- Ns.int(2).save
//
//          // Correctly continuing from 5
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(5, 7), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(2, cursor).map { case (List(9), cursor, 0) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(5, 7), cursor, 3) => cursor }
//
//          // 2 shows on the way back
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(2, 3), cursor, 1) => cursor }
//          cursor <- Ns.int.a1.get(-2, cursor).map { case (List(1), cursor, 1) => cursor }
//        } yield ()
//      }
    }
  }
}
