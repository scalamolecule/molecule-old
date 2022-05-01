package moleculeTests.tests.core.pagination.cursor

import molecule.core.util.Executor._
import molecule.datomic.api.out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.annotation.nowarn

// Pagination for the object api is implemented as for tuples. So we only test a few basic things below.
object CursorPaginationObj extends AsyncTestSuite {
  val x = ""

  @nowarn lazy val tests = Tests {

    "Basic" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3, 4, 5)

        _ <- Ns.int.a1.getObjs.collect { case List(o1, o2, o3, o4, o5) =>
          o1.int ==> 1
          o2.int ==> 2
          o3.int ==> 3
          o4.int ==> 4
          o5.int ==> 5
        }

        c <- Ns.int.a1.getObjs(2, x).collect { case (List(o1, o2), c, 3) =>
          o1.int ==> 1
          o2.int ==> 2
          c
        }
        c <- Ns.int.a1.getObjs(2, c).collect { case (List(o3, o4), c, 1) =>
          o3.int ==> 3
          o4.int ==> 4
          c
        }
        c <- Ns.int.a1.getObjs(2, c).collect { case (List(o5), c, 0) =>
          o5.int ==> 5
          c
        }
        c <- Ns.int.a1.getObjs(-2, c).collect { case (List(o3, o4), c, 2) =>
          o3.int ==> 3
          o4.int ==> 4
          c
        }
        _ <- Ns.int.a1.getObjs(-2, c).collect { case (List(o1, o2), _, 0) =>
          o1.int ==> 1
          o2.int ==> 2
          ()
        }
      } yield ()
    }


    "Time" - {

      "AsOf" - core { implicit conn =>
        for {
          t <- Ns.int.insert(1, 2, 3, 4, 5).map(_.t)
          _ <- Ns.int(6).save

          _ <- Ns.int.a1.getObjsAsOf(t).collect { case List(o1, o2, o3, o4, o5) =>
            o1.int ==> 1
            o2.int ==> 2
            o3.int ==> 3
            o4.int ==> 4
            o5.int ==> 5
          }

          c <- Ns.int.a1.getObjsAsOf(t, 2, x).collect { case (List(o1, o2), c, 3) =>
            o1.int ==> 1
            o2.int ==> 2
            c
          }
          c <- Ns.int.a1.getObjsAsOf(t, 2, c).collect { case (List(o3, o4), c, 1) =>
            o3.int ==> 3
            o4.int ==> 4
            c
          }
          c <- Ns.int.a1.getObjsAsOf(t, 2, c).collect { case (List(o5), c, 0) =>
            o5.int ==> 5
            c
          }
          c <- Ns.int.a1.getObjsAsOf(t, -2, c).collect { case (List(o3, o4), c, 2) =>
            o3.int ==> 3
            o4.int ==> 4
            c
          }
          _ <- Ns.int.a1.getObjsAsOf(t, -2, c).collect { case (List(o1, o2), _, 0) =>
            o1.int ==> 1
            o2.int ==> 2
            ()
          }
        } yield ()
      }

      "Since" - core { implicit conn =>
        for {
          t <- Ns.int(6).save.map(_.t)
          _ <- Ns.int.insert(1, 2, 3, 4, 5)

          _ <- Ns.int.a1.getObjsSince(t).collect { case List(o1, o2, o3, o4, o5) =>
            o1.int ==> 1
            o2.int ==> 2
            o3.int ==> 3
            o4.int ==> 4
            o5.int ==> 5
          }

          c <- Ns.int.a1.getObjsSince(t, 2, x).collect { case (List(o1, o2), c, 3) =>
            o1.int ==> 1
            o2.int ==> 2
            c
          }
          c <- Ns.int.a1.getObjsSince(t, 2, c).collect { case (List(o3, o4), c, 1) =>
            o3.int ==> 3
            o4.int ==> 4
            c
          }
          c <- Ns.int.a1.getObjsSince(t, 2, c).collect { case (List(o5), c, 0) =>
            o5.int ==> 5
            c
          }
          c <- Ns.int.a1.getObjsSince(t, -2, c).collect { case (List(o3, o4), c, 2) =>
            o3.int ==> 3
            o4.int ==> 4
            c
          }
          _ <- Ns.int.a1.getObjsSince(t, -2, c).collect { case (List(o1, o2), _, 0) =>
            o1.int ==> 1
            o2.int ==> 2
            ()
          }
        } yield ()
      }

      "With" - core { implicit conn =>
        for {
          _ <- Ns.int.insert(1, 2, 3)

          moreData = Ns.int.getInsertStmts(4, 5)

          _ <- Ns.int.a1.getObjsWith(moreData).collect { case List(o1, o2, o3, o4, o5) =>
            o1.int ==> 1
            o2.int ==> 2
            o3.int ==> 3
            o4.int ==> 4
            o5.int ==> 5
          }

          c <- Ns.int.a1.getObjsWith(2, x, moreData).collect { case (List(o1, o2), c, 3) =>
            o1.int ==> 1
            o2.int ==> 2
            c
          }
          c <- Ns.int.a1.getObjsWith(2, c, moreData).collect { case (List(o3, o4), c, 1) =>
            o3.int ==> 3
            o4.int ==> 4
            c
          }
          c <- Ns.int.a1.getObjsWith(2, c, moreData).collect { case (List(o5), c, 0) =>
            o5.int ==> 5
            c
          }
          c <- Ns.int.a1.getObjsWith(-2, c, moreData).collect { case (List(o3, o4), c, 2) =>
            o3.int ==> 3
            o4.int ==> 4
            c
          }
          _ <- Ns.int.a1.getObjsWith(-2, c, moreData).collect { case (List(o1, o2), _, 0) =>
            o1.int ==> 1
            o2.int ==> 2
            ()
          }
        } yield ()
      }

      // Pagination for getHistory is not implemented.
    }
  }
}