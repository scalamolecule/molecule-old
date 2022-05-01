package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.annotation.nowarn

object CursorPaginationTplNested extends AsyncTestSuite {
  val x = ""

  @nowarn lazy val tests = Tests {

    "Basics (no change)" - {

      "Forward asc" - core { implicit conn =>
        for {
          _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
            (1, List(11, 12)),
            (2, List(21, 22)),
            (3, List(31, 32)),
            (4, List(41, 42)),
            (5, List(51, 52)),
          )
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, x).map { case (List((1, List(11, 12)), (2, List(21, 22))), c, 3) => c }
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, x).map { case (List((1, List(11, 12)), (2, List(21, 22))), c, 3) => c }
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, c).map { case (List((3, List(31, 32)), (4, List(41, 42))), c, 1) => c }
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, c).map { case (List((5, List(51, 52))), c, 0) => c }
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, c).map { case (List((3, List(31, 32)), (4, List(41, 42))), c, 2) => c }
          _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, c).map { case (List((1, List(11, 12)), (2, List(21, 22))), _, 0) => () }
        } yield ()
      }

      "Forward desc" - core { implicit conn =>
        for {
          _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
            (1, List(11, 12)),
            (2, List(21, 22)),
            (3, List(31, 32)),
            (4, List(41, 42)),
            (5, List(51, 52)),
          )
          c <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, x).map { case (List((5, List(51, 52)), (4, List(41, 42))), c, 3) => c }
          c <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, c).map { case (List((3, List(31, 32)), (2, List(21, 22))), c, 1) => c }
          c <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, c).map { case (List((1, List(11, 12))), c, 0) => c }
          c <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, c).map { case (List((3, List(31, 32)), (2, List(21, 22))), c, 2) => c }
          _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, c).map { case (List((5, List(51, 52)), (4, List(41, 42))), _, 0) => () }
        } yield ()
      }

      "Backward asc" - core { implicit conn =>
        for {
          _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
            (1, List(11, 12)),
            (2, List(21, 22)),
            (3, List(31, 32)),
            (4, List(41, 42)),
            (5, List(51, 52)),
          )
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, x).map { case (List((4, List(41, 42)), (5, List(51, 52))), c, 3) => c }
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, c).map { case (List((2, List(21, 22)), (3, List(31, 32))), c, 1) => c }
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, c).map { case (List((1, List(11, 12))), c, 0) => c }
          c <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, c).map { case (List((2, List(21, 22)), (3, List(31, 32))), c, 2) => c }
          _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, c).map { case (List((4, List(41, 42)), (5, List(51, 52))), _, 0) => () }
        } yield ()
      }

      "Backward desc" - core { implicit conn =>
        for {
          _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
            (1, List(11, 12)),
            (2, List(21, 22)),
            (3, List(31, 32)),
            (4, List(41, 42)),
            (5, List(51, 52)),
          )
          c <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, x).map { case (List((2, List(21, 22)), (1, List(11, 12))), c, 3) => c }
          c <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, c).map { case (List((4, List(41, 42)), (3, List(31, 32))), c, 1) => c }
          c <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, c).map { case (List((5, List(51, 52))), c, 0) => c }
          c <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, c).map { case (List((4, List(41, 42)), (3, List(31, 32))), c, 2) => c }
          _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, c).map { case (List((2, List(21, 22)), (1, List(11, 12))), _, 0) => () }
        } yield ()
      }
    }


    "Forward" - {

      "Add data" - {

        "Add rows (outside same sort value window)" - core { implicit conn =>
          for {
            _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
              (1, List(11, 12)),
              (2, List(21, 22)),
              (3, List(31, 32)),
            )

            c1 <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, x).map {
              case (List(
              (1, List(11, 12)),
              (2, List(21, 22))), c, 1) => c
            }

            // Current last page
            c2 <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, c1).map {
              case (List(
              (3, List(31, 32))), c, 0) => c
            }

            // New data will show on new page
            // (regardless if it could have fitted on the old page)
            _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
              (4, List(41, 42)),
            )
            c3 <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, c2).map {
              case (List(
              (4, List(41, 42))), c, 0) => c
            }

            // Adding data beyond next page
            _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
              (5, List(51, 52)),
              (6, List(61, 62)),
              (7, List(71, 72)),
            )
            c4 <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, c3).map {
              case (List(
              (5, List(51, 52)),
              (6, List(61, 62))), c, 1) => c
            }

            _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, c4).map {
              case (List(
              (7, List(71, 72))), _, 0) => ()
            }
          } yield ()
        }

        "Add row after cursor within window" - core { implicit conn =>
          for {
            // Window of rows with same sort value (1)
            _ <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
            )

            // Order of non-sorted attributes is non-deterministic
            // (but will likely not vary in-between internal indexing jobs)
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("c", 1, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, x).map {
              case (List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2))), c, 1) => c
            }

            _ <- Ns.str.int.Refs1.*(Ref1.int1).insert("d", 1, List(1, 2))
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2)), // added row after cursor row
            ))

            // Page 2 including new d row since it's after cursor row
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, c).map {
              case (List(
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }


        "Add row before cursor within window" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("a", 1, List(1, 2)),
              ("b", 1, Nil),
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2)),
            ) map (_.eids(3))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("a", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, x).map {
              case (List(
              ("a", 1, List(1, 2)),
              ("c", 1, List(1, 2))), c, 1) => c
            }

            // Update row b so that it shows up
            r12 <- Ref1.int1.insert(1, 2).map(_.eids)
            _ <- Ns(b1).refs1(r12).update

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // updated row before cursor row
              ("c", 1, List(1, 2)), // cursor row
              ("d", 1, List(1, 2)),
            ))

            // Page 2 excluding updated b row since it's before cursor row
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, c).map {
              case (List(
              ("d", 1, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }
      }


      "Retract non-cursor row" - {

        "outside page" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // cursor row
              ("b", 1, List(1, 2)), // outside page 1
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, x).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2))), c, 2) => c
            }

            _ <- b1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // cursor row
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, c).map {
              case (List(
              ("y", 2, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }

        "inside page" - core { implicit conn =>
          for {
            a1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(3))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // inside page 1
              ("b", 1, List(1, 2)), // cursor row
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, x).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2))), c, 1) => c
            }

            _ <- a1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, c).map {
              case (List(
              ("y", 2, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }
      }


      "Retract cursor row, first in window" - {

        "Retraction only" - core { implicit conn =>
          for {
            a1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(3))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // cursor row first in window
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, x).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2))), c, 2) => c
            }

            _ <- a1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)), // substitute cursor row (first before a on previous page)
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, c).map {
              case (List(
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }

        "Retraction + added data" - core { implicit conn =>
          for {
            a1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(3))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // cursor row first in window
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, x).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2))), c, 2) => c
            }

            _ <- a1.retract
            // Add data in window
            _ <- Ns.str.int.Refs1.*(Ref1.int1).insert("c", 1, List(1, 2))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)), // substitute cursor row (first before a on previous page)
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(2, c).map {
              case (List(
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2))), _, 1) => ()
            }
          } yield ()
        }
      }


      "Retract cursor row, last in window" - {

        "Retraction only" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row last in window
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, x).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2))), c, 1) => c
            }

            _ <- b1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // substitute cursor row (first before b on previous page)
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, c).map {
              case (List(
              ("y", 2, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }

        "Retraction + added data" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row last in window
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, x).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2))), c, 1) => c
            }

            _ <- b1.retract
            // Add data in window
            _ <- Ns.str.int.Refs1.*(Ref1.int1).insert("c", 1, List(1, 2))
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // substitute cursor row (first before b on previous page)
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, c).map {
              case (List(
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }
      }


      "Retract cursor row, inside window" - {

        "Retraction only" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, x).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2))), c, 2) => c
            }

            _ <- b1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // substitute cursor row (first before b on previous page)
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, c).map {
              case (List(
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }

        "Retraction + added data" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, x).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2))), c, 2) => c
            }

            _ <- b1.retract
            // Add data in window
            _ <- Ns.str.int.Refs1.*(Ref1.int1).insert("d", 1, List(1, 2))
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // substitute cursor row (first before b on previous page)
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(3, c).map {
              case (List(
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2)),
              ("y", 2, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }
      }
    }


    "Backward" - {

      "Add data" - {

        "Add rows (outside same sort value window)" - core { implicit conn =>
          for {
            _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
              (5, List(51, 52)),
              (6, List(61, 62)),
              (7, List(71, 72)),
            )

            c1 <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, x).map {
              case (List(
              (6, List(61, 62)),
              (7, List(71, 72))), c, 1) => c
            }

            // Current last page
            c2 <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, c1).map {
              case (List(
              (5, List(51, 52))), c, 0) => c
            }

            // New data will show on new page
            // (regardless if it could have fitted on the old page)
            _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
              (4, List(41, 42)),
            )
            c3 <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, c2).map {
              case (List(
              (4, List(41, 42))), c, 0) => c
            }

            // Adding data beyond next page
            _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
              (1, List(11, 12)),
              (2, List(21, 22)),
              (3, List(31, 32)),
            )
            c4 <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, c3).map {
              case (List(
              (2, List(21, 22)),
              (3, List(31, 32))), c, 1) => c
            }

            _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, c4).map {
              case (List(
              (1, List(11, 12))), _, 0) => ()
            }
          } yield ()
        }

        "Add row after cursor within window" - core { implicit conn =>
          for {
            // Window of rows with same sort value (1)
            _ <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
            )

            // Order of non-sorted attributes is non-deterministic
            // (but will likely not vary in-between internal indexing jobs)
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, x).map {
              case (List(
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2))), c, 1) => c
            }

            _ <- Ns.str.int.Refs1.*(Ref1.int1).insert("d", 1, List(1, 2))
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2)), // added row after cursor row
            ))

            // Page 2 excluding new d row since it's after cursor row and we are going backwards
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, c).map {
              case (List(
              ("a", 1, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }


        "Add row before cursor within window" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("a", 1, List(1, 2)),
              ("b", 1, Nil),
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2)),
            ) map (_.eids(3))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("a", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, x).map {
              case (List(
              ("c", 1, List(1, 2)),
              ("d", 1, List(1, 2))), c, 1) => c
            }

            // Update row b so that it shows up
            r12 <- Ref1.int1.insert(1, 2).map(_.eids)
            _ <- Ns(b1).refs1(r12).update

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // updated row before cursor row
              ("c", 1, List(1, 2)), // cursor row
              ("d", 1, List(1, 2)),
            ))

            // Page 2 including updated b row since it's before cursor row and we go backwards
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, c).map {
              case (List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }
      }


      "Retract non-cursor row" - {

        "outside page" - core { implicit conn =>
          for {
            a1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(3))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // outside page 1
              ("b", 1, List(1, 2)), // cursor row
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, x).map {
              case (List(
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2))), c, 2) => c
            }

            _ <- a1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, c).map {
              case (List(
              ("x", 0, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }

        "inside page" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // cursor row
              ("b", 1, List(1, 2)), // inside page 1
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, x).map {
              case (List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2))), c, 1) => c
            }

            _ <- b1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // cursor row
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, c).map {
              case (List(
              ("x", 0, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }
      }


      "Retract cursor row, first in window" - {

        "Retraction only" - core { implicit conn =>
          for {
            a1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(3))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // cursor row first in window
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, x).map {
              case (List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2))), c, 1) => c
            }

            _ <- a1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("b", 1, List(1, 2)), // substitute cursor row (first after a on previous page)
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, c).map {
              case (List(
              ("x", 0, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }

        "Retraction + added data" - core { implicit conn =>
          for {
            a1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(3))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)), // cursor row first in window
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, x).map {
              case (List(
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2))), c, 1) => c
            }

            _ <- a1.retract
            // Add data in window
            _ <- Ns.str.int.Refs1.*(Ref1.int1).insert("c", 1, List(1, 2))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("b", 1, List(1, 2)), // substitute cursor row (first after a on previous page)
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, c).map {
              case (List(
              ("x", 0, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }
      }


      "Retract cursor row, last in window" - {

        "Retraction only" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row last in window
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, x).map {
              case (List(
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2))), c, 2) => c
            }

            _ <- b1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("y", 2, List(1, 2)), // substitute cursor row (first after b on previous page)
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, c).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }

        "Retraction + added data" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row last in window
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, x).map {
              case (List(
              ("b", 1, List(1, 2)),
              ("y", 2, List(1, 2))), c, 2) => c
            }

            _ <- b1.retract
            // Add data in window
            _ <- Ns.str.int.Refs1.*(Ref1.int1).insert("c", 1, List(1, 2))
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)), // substitute cursor row (first after b on previous page)
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-2, c).map {
              case (List(
              ("a", 1, List(1, 2)),
              ("c", 1, List(1, 2))), _, 1) => ()
            }
          } yield ()
        }
      }


      "Retract cursor row, inside window" - {

        "Retraction only" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, x).map {
              case (List(
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2))), c, 2) => c
            }

            _ <- b1.retract
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("c", 1, List(1, 2)), // substitute cursor row (first after b on previous page)
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, c).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }

        "Retraction + added data" - core { implicit conn =>
          for {
            b1 <- Ns.str.int.Refs1.*(Ref1.int1) insert List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ) map (_.eids(6))

            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("b", 1, List(1, 2)), // cursor row
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 1
            c <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, x).map {
              case (List(
              ("b", 1, List(1, 2)),
              ("c", 1, List(1, 2)),
              ("y", 2, List(1, 2))), c, 2) => c
            }

            _ <- b1.retract
            // Add data in window
            _ <- Ns.str.int.Refs1.*(Ref1.int1).insert("d", 1, List(1, 2))
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get.map(_ ==> List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2)),
              ("c", 1, List(1, 2)), // substitute cursor row (first after b on previous page)
              ("d", 1, List(1, 2)),
              ("y", 2, List(1, 2)),
            ))

            // Page 2
            _ <- Ns.str.int.a1.Refs1.*(Ref1.int1).get(-3, c).map {
              case (List(
              ("x", 0, List(1, 2)),
              ("a", 1, List(1, 2))), _, 0) => ()
            }
          } yield ()
        }
      }
    }
  }
}