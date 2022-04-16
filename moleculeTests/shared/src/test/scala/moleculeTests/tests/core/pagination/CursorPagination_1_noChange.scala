package moleculeTests.tests.core.pagination

import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

object CursorPagination_1_noChange extends AsyncTestSuite {

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

    "Desc" - core { implicit conn =>
      for {
        _ <- Ref2.int2.insert(1, 2, 3, 4, 5)

        // Page 1 (empty cursor)
        cursor <- Ref2.int2.d1.get(2, "").map { case (page, cursor, more) =>
          page ==> List(5, 4)
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ref2.int2.d1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(3, 2)
          more ==> 1
          cursor
        }


        // Page 3, last page
        cursor <- Ref2.int2.d1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(1)
          more ==> 0 // currently no further rows
          cursor
        }

        // Page 2, go backwards with negative limit
        cursor <- Ref2.int2.d1.get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(3, 2)
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ref2.int2.d1.get(-2, cursor).map { case (page, _, more) =>
          page ==> List(5, 4)
          more ==> 0
        }
      } yield ()
    }


    "Expression" - core { implicit conn =>
      for {
        _ <- Ref2.int2.insert(0, 1, 2, 3, 4, 5)

        // Page 1
        cursor <- Ref2.int2.>(0).a1.get(2, "").map { case (page, cursor, more) =>
          page ==> List(1, 2)
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ref2.int2.>(0).a1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(3, 4)
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ref2.int2.>(0).a1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(5)
          more ==> 0 // currently no further rows
          cursor
        }

        // Page 2
        cursor <- Ref2.int2.>(0).a1.get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(3, 4)
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ref2.int2.>(0).a1.get(-2, cursor).map { case (page, _, more) =>
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

    "Redundant sort values 2" - core { implicit conn =>
      for {
        _ <- Ns.str.int insert List(
          ("a", 1),
          ("b", 1),
          ("c", 1),
          ("d", 1),
          ("e", 2),
        )

        // Page 1
        cursor <- Ns.str.int.a1.get(3, "").map { case (page, cursor, more) =>
          page ==> List(
            ("b", 1),
            ("a", 1),
            ("d", 1),
          )
          more ==> 2
          cursor
        }

        // Page 2
        cursor <- Ns.str.int.a1.get(3, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", 1),
            ("e", 2),
          )
          more ==> 0
          cursor
        }

        // Page 1
        _ <- Ns.str.int.a1.get(-3, cursor).map { case (page, _, more) =>
          page ==> List(
            ("b", 1),
            ("a", 1),
            ("d", 1),
          )
          more ==> 0
        }
      } yield ()
    }

    "Redundant sort values 3" - core { implicit conn =>
      for {
        _ <- Ns.str.int insert List(
          ("a", 1),
          ("b", 2),
          ("c", 2),
          ("d", 2),
          ("e", 2),
        )

        // Page 1
        cursor <- Ns.str.int.a1.get(3, "").map { case (page, cursor, more) =>
          page ==> List(
            ("a", 1),
            ("c", 2),
            ("b", 2),
          )
          more ==> 2
          cursor
        }

        // Page 2
        cursor <- Ns.str.int.a1.get(3, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("e", 2),
            ("d", 2),
          )
          more ==> 0
          cursor
        }

        // Page 1
        _ <- Ns.str.int.a1.get(-3, cursor).map { case (page, _, more) =>
          page ==> List(
            ("a", 1),
            ("c", 2),
            ("b", 2),
          )
          more ==> 0
        }
      } yield ()
    }


    "Ref" - core { implicit conn =>
      for {
        _ <- Ref1.str1.Ref2.int2 insert List(
          ("a", 1),
          ("b", 2),
          ("c", 3),
          ("d", 4),
          ("e", 5),
        )

        // Page 1
        cursor <- Ref1.str1.Ref2.int2.a1.get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            ("a", 1),
            ("b", 2)
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ref1.str1.Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", 3),
            ("d", 4),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ref1.str1.Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("e", 5),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ref1.str1.Ref2.int2.a1.get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", 3),
            ("d", 4),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ref1.str1.Ref2.int2.a1.get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            ("a", 1),
            ("b", 2),
          )
          more ==> 0
        }
      } yield ()
    }


    "Composite i + _" - core { implicit conn =>
      for {
        _ <- Ref2.str2 + Ns.int insert List(
          ("a", 1),
          ("b", 2),
          ("c", 3),
          ("d", 4),
          ("e", 5),
        )

        // Page 1
        cursor <- (Ref2.str2.a1 + Ns.int).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            ("a", 1),
            ("b", 2)
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.a1 + Ns.int).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", 3),
            ("d", 4),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ref2.str2.a1 + Ns.int).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("e", 5),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.a1 + Ns.int).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", 3),
            ("d", 4),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ref2.str2.a1 + Ns.int).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            ("a", 1),
            ("b", 2),
          )
          more ==> 0
        }
      } yield ()
    }


    "Composite _ + i" - core { implicit conn =>
      for {
        _ <- Ns.str + Ref2.int2 insert List(
          ("a", 1),
          ("b", 2),
          ("c", 3),
          ("d", 4),
          ("e", 5),
        )

        // Page 1
        cursor <- (Ns.str + Ref2.int2.a1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            ("a", 1),
            ("b", 2)
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str + Ref2.int2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", 3),
            ("d", 4),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str + Ref2.int2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("e", 5),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str + Ref2.int2.a1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", 3),
            ("d", 4),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str + Ref2.int2.a1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            ("a", 1),
            ("b", 2),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite i + (_, _)" - core { implicit conn =>
      for {
        _ <- Ref2.str2 + Ns.int.str insert List(
          ("a", (1, "x1")),
          ("b", (2, "x2")),
          ("c", (3, "x3")),
          ("d", (4, "x4")),
          ("e", (5, "x5")),
        )

        // Page 1
        cursor <- (Ref2.str2.a1 + Ns.int.str).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            ("a", (1, "x1")),
            ("b", (2, "x2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.a1 + Ns.int.str).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", (3, "x3")),
            ("d", (4, "x4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ref2.str2.a1 + Ns.int.str).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("e", (5, "x5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.a1 + Ns.int.str).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", (3, "x3")),
            ("d", (4, "x4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ref2.str2.a1 + Ns.int.str).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            ("a", (1, "x1")),
            ("b", (2, "x2")),
          )
          more ==> 0
        }
      } yield ()
    }


    "Composite _ + (i, _)" - core { implicit conn =>
      for {
        _ <- Ns.str + Ref2.int2.str2 insert List(
          ("a", (1, "x1")),
          ("b", (2, "x2")),
          ("c", (3, "x3")),
          ("d", (4, "x4")),
          ("e", (5, "x5")),
        )

        // Page 1
        cursor <- (Ns.str + Ref2.int2.a1.str2).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            ("a", (1, "x1")),
            ("b", (2, "x2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str + Ref2.int2.a1.str2).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", (3, "x3")),
            ("d", (4, "x4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str + Ref2.int2.a1.str2).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("e", (5, "x5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str + Ref2.int2.a1.str2).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", (3, "x3")),
            ("d", (4, "x4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str + Ref2.int2.a1.str2).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            ("a", (1, "x1")),
            ("b", (2, "x2")),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite _ + (_, i)" - core { implicit conn =>
      for {
        _ <- Ns.str + Ref2.str2.int2 insert List(
          ("a", ("x1", 1)),
          ("b", ("x2", 2)),
          ("c", ("x3", 3)),
          ("d", ("x4", 4)),
          ("e", ("x5", 5)),
        )

        // Page 1
        cursor <- (Ns.str + Ref2.str2.int2.a1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            ("a", ("x1", 1)),
            ("b", ("x2", 2)),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str + Ref2.str2.int2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", ("x3", 3)),
            ("d", ("x4", 4)),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str + Ref2.str2.int2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("e", ("x5", 5)),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str + Ref2.str2.int2.a1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            ("c", ("x3", 3)),
            ("d", ("x4", 4)),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str + Ref2.str2.int2.a1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            ("a", ("x1", 1)),
            ("b", ("x2", 2)),
          )
          more ==> 0
        }
      } yield ()
    }


    "Composite (i, _) + _" - core { implicit conn =>
      for {
        _ <- Ref2.str2.int2 + Ns.int insert List(
          (("a", 11), 1),
          (("b", 12), 2),
          (("c", 13), 3),
          (("d", 14), 4),
          (("e", 15), 5),
        )

        // Page 1
        cursor <- (Ref2.str2.a1.int2 + Ns.int).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), 1),
            (("b", 12), 2),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.a1.int2 + Ns.int).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), 3),
            (("d", 14), 4),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ref2.str2.a1.int2 + Ns.int).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), 5),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.a1.int2 + Ns.int).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), 3),
            (("d", 14), 4),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ref2.str2.a1.int2 + Ns.int).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), 1),
            (("b", 12), 2),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, i) + _" - core { implicit conn =>
      for {
        _ <- Ref2.str2.int2 + Ns.int insert List(
          (("a", 11), 1),
          (("b", 12), 2),
          (("c", 13), 3),
          (("d", 14), 4),
          (("e", 15), 5),
        )

        // Page 1
        cursor <- (Ref2.str2.int2.a1 + Ns.int).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), 1),
            (("b", 12), 2),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.int2.a1 + Ns.int).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), 3),
            (("d", 14), 4),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ref2.str2.int2.a1 + Ns.int).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), 5),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.int2.a1 + Ns.int).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), 3),
            (("d", 14), 4),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ref2.str2.int2.a1 + Ns.int).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), 1),
            (("b", 12), 2),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, _) + i" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref2.int2 insert List(
          (("a", 7), 1),
          (("b", 7), 2),
          (("c", 7), 3),
          (("d", 7), 4),
          (("e", 7), 5),
        )

        // Page 1
        cursor <- (Ns.str.int + Ref2.int2.a1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 7), 1),
            (("b", 7), 2),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref2.int2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 7), 3),
            (("d", 7), 4),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str.int + Ref2.int2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 7), 5),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref2.int2.a1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 7), 3),
            (("d", 7), 4),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str.int + Ref2.int2.a1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 7), 1),
            (("b", 7), 2),
          )
          more ==> 0
        }
      } yield ()
    }


    "Composite (i, _) + (_, _)" - core { implicit conn =>
      for {
        _ <- Ref2.str2.int2 + Ns.int.str insert List(
          (("a", 11), (1, "x1")),
          (("b", 12), (2, "x2")),
          (("c", 13), (3, "x3")),
          (("d", 14), (4, "x4")),
          (("e", 15), (5, "x5")),
        )

        // Page 1
        cursor <- (Ref2.str2.a1.int2 + Ns.int.str).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), (1, "x1")),
            (("b", 12), (2, "x2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.a1.int2 + Ns.int.str).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3")),
            (("d", 14), (4, "x4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ref2.str2.a1.int2 + Ns.int.str).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), (5, "x5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.a1.int2 + Ns.int.str).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3")),
            (("d", 14), (4, "x4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ref2.str2.a1.int2 + Ns.int.str).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), (1, "x1")),
            (("b", 12), (2, "x2")),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, i) + (_, _)" - core { implicit conn =>
      for {
        _ <- Ref2.str2.int2 + Ns.int.str insert List(
          (("a", 11), (1, "x1")),
          (("b", 12), (2, "x2")),
          (("c", 13), (3, "x3")),
          (("d", 14), (4, "x4")),
          (("e", 15), (5, "x5")),
        )

        // Page 1
        cursor <- (Ref2.str2.int2.a1 + Ns.int.str).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), (1, "x1")),
            (("b", 12), (2, "x2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.int2.a1 + Ns.int.str).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3")),
            (("d", 14), (4, "x4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ref2.str2.int2.a1 + Ns.int.str).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), (5, "x5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ref2.str2.int2.a1 + Ns.int.str).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3")),
            (("d", 14), (4, "x4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ref2.str2.int2.a1 + Ns.int.str).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), (1, "x1")),
            (("b", 12), (2, "x2")),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, _) + (i, _)" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref2.int2.str2 insert List(
          (("a", 11), (1, "x1")),
          (("b", 12), (2, "x2")),
          (("c", 13), (3, "x3")),
          (("d", 14), (4, "x4")),
          (("e", 15), (5, "x5")),
        )

        // Page 1
        cursor <- (Ns.str.int + Ref2.int2.a1.str2).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), (1, "x1")),
            (("b", 12), (2, "x2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref2.int2.a1.str2).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3")),
            (("d", 14), (4, "x4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str.int + Ref2.int2.a1.str2).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), (5, "x5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref2.int2.a1.str2).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3")),
            (("d", 14), (4, "x4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str.int + Ref2.int2.a1.str2).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), (1, "x1")),
            (("b", 12), (2, "x2")),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, _) + (_, i)" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref2.int2.str2 insert List(
          (("a", 11), (1, "x1")),
          (("b", 12), (2, "x2")),
          (("c", 13), (3, "x3")),
          (("d", 14), (4, "x4")),
          (("e", 15), (5, "x5")),
        )

        // Page 1
        cursor <- (Ns.str.int + Ref2.int2.str2.a1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), (1, "x1")),
            (("b", 12), (2, "x2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref2.int2.str2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3")),
            (("d", 14), (4, "x4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str.int + Ref2.int2.str2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), (5, "x5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref2.int2.str2.a1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3")),
            (("d", 14), (4, "x4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str.int + Ref2.int2.str2.a1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), (1, "x1")),
            (("b", 12), (2, "x2")),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, _) + i + (_, _)" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref2.int2 + Ref1.int1.str1 insert List(
          (("a", 11), 21, (1, "x1")),
          (("b", 12), 22, (2, "x2")),
          (("c", 13), 23, (3, "x3")),
          (("d", 14), 24, (4, "x4")),
          (("e", 15), 25, (5, "x5")),
        )

        // Page 1
        cursor <- (Ns.str.int + Ref2.int2.a1 + Ref1.int1.str1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), 21, (1, "x1")),
            (("b", 12), 22, (2, "x2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref2.int2.a1 + Ref1.int1.str1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), 23, (3, "x3")),
            (("d", 14), 24, (4, "x4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str.int + Ref2.int2.a1 + Ref1.int1.str1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), 25, (5, "x5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref2.int2.a1 + Ref1.int1.str1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), 23, (3, "x3")),
            (("d", 14), 24, (4, "x4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str.int + Ref2.int2.a1 + Ref1.int1.str1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), 21, (1, "x1")),
            (("b", 12), 22, (2, "x2")),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, _) + (_, _) + i" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref1.int1.str1 + Ref2.int2 insert List(
          (("a", 11), (1, "x1"), 21),
          (("b", 12), (2, "x2"), 22),
          (("c", 13), (3, "x3"), 23),
          (("d", 14), (4, "x4"), 24),
          (("e", 15), (5, "x5"), 25),
        )

        // Page 1
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), (1, "x1"), 21),
            (("b", 12), (2, "x2"), 22),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3"), 23),
            (("d", 14), (4, "x4"), 24),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), (5, "x5"), 25),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3"), 23),
            (("d", 14), (4, "x4"), 24),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), (1, "x1"), 21),
            (("b", 12), (2, "x2"), 22),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, _) + (_, _) + (i, _)" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref1.int1.str1 + Ref2.int2.str2 insert List(
          (("a", 11), (1, "x1"), (21, "z1")),
          (("b", 12), (2, "x2"), (22, "z2")),
          (("c", 13), (3, "x3"), (23, "z3")),
          (("d", 14), (4, "x4"), (24, "z4")),
          (("e", 15), (5, "x5"), (25, "z5")),
        )

        // Page 1
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1.str2).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), (1, "x1"), (21, "z1")),
            (("b", 12), (2, "x2"), (22, "z2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1.str2).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3"), (23, "z3")),
            (("d", 14), (4, "x4"), (24, "z4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1.str2).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), (5, "x5"), (25, "z5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1.str2).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3"), (23, "z3")),
            (("d", 14), (4, "x4"), (24, "z4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.a1.str2).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), (1, "x1"), (21, "z1")),
            (("b", 12), (2, "x2"), (22, "z2")),
          )
          more ==> 0
        }
      } yield ()
    }

    "Composite (_, _) + (_, _) + (_, i)" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref1.int1.str1 + Ref2.int2.str2 insert List(
          (("a", 11), (1, "x1"), (21, "z1")),
          (("b", 12), (2, "x2"), (22, "z2")),
          (("c", 13), (3, "x3"), (23, "z3")),
          (("d", 14), (4, "x4"), (24, "z4")),
          (("e", 15), (5, "x5"), (25, "z5")),
        )

        // Page 1
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.str2.a1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (("a", 11), (1, "x1"), (21, "z1")),
            (("b", 12), (2, "x2"), (22, "z2")),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.str2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3"), (23, "z3")),
            (("d", 14), (4, "x4"), (24, "z4")),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.str2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("e", 15), (5, "x5"), (25, "z5")),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.str2.a1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (("c", 13), (3, "x3"), (23, "z3")),
            (("d", 14), (4, "x4"), (24, "z4")),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- (Ns.str.int + Ref1.int1.str1 + Ref2.int2.str2.a1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (("a", 11), (1, "x1"), (21, "z1")),
            (("b", 12), (2, "x2"), (22, "z2")),
          )
          more ==> 0
        }
      } yield ()
    }


    "Re-use" - core { implicit conn =>
      for {
        List(a, b, c) <- Ref2.str2.insert("a", "b", "c").map(_.eids)
        _ <- Ref1.int1.ref2 insert List((1, a))
        _ <- Ref1.int1.ref2 insert List((2, b), (3, b), (4, b))
        _ <- Ref1.int1.ref2 insert List((5, c))

        // Page 1
        cursor <- Ref1.int1.a2.Ref2.str2.a1.get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (1, "a"),
            (2, "b"),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ref1.int1.a2.Ref2.str2.a1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, "b"),
            (4, "b"),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ref1.int1.a2.Ref2.str2.a1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (5, "c"),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ref1.int1.a2.Ref2.str2.a1.get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, "b"),
            (4, "b"),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ref1.int1.a2.Ref2.str2.a1.get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (1, "a"),
            (2, "b"),
          )
          more ==> 0
        }
      } yield ()
    }


    "TxMetaData" - core { implicit conn =>
      for {
        _ <- Ns.int.Tx(Ref2.str2_("a")).insert(1)
        _ <- Ns.int.Tx(Ref2.str2_("b")).insert(2, 3, 4)
        _ <- Ns.int.Tx(Ref2.str2_("c")).insert(5)

        // Page 1
        cursor <- Ns.int.a2.Tx(Ref2.str2.a1).get(2, "").map { case (page, cursor, more) =>
          page ==> List(
            (1, "a"),
            (2, "b"),
          )
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ns.int.a2.Tx(Ref2.str2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, "b"),
            (4, "b"),
          )
          more ==> 1
          cursor
        }

        // Page 3
        cursor <- Ns.int.a2.Tx(Ref2.str2.a1).get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (5, "c"),
          )
          more ==> 0
          cursor
        }

        // Page 2
        cursor <- Ns.int.a2.Tx(Ref2.str2.a1).get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(
            (3, "b"),
            (4, "b"),
          )
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ns.int.a2.Tx(Ref2.str2.a1).get(-2, cursor).map { case (page, _, more) =>
          page ==> List(
            (1, "a"),
            (2, "b"),
          )
          more ==> 0
        }
      } yield ()
    }


//    "Nested" - core { implicit conn =>
//      for {
//        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
//          (1, List(11, 12)),
//          (2, List(21, 22)),
//          (3, List(31, 32)),
//          (4, List(41, 42)),
//          (5, List(51, 52)),
//        )
//
//        // Page 1
//        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, "").map { case (page, cursor, more) =>
//          page ==> List(
//            (1, List(11, 12)),
//            (2, List(21, 22)),
//          )
//          more ==> 3
//          cursor
//        }
//
//        // Page 2
//        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
//          page ==> List(
//            (3, List(31, 32)),
//            (4, List(41, 42)),
//          )
//          more ==> 1
//          cursor
//        }
//
//        // Page 3
//        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
//          page ==> List(
//            (5, List(51, 52)),
//          )
//          more ==> 0
//          cursor
//        }
//
//        // Page 2
//        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
//          page ==> List(
//            (3, List(31, 32)),
//            (4, List(41, 42)),
//          )
//          more ==> 2
//          cursor
//        }
//
//        // Page 1
//        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, _, more) =>
//          page ==> List(
//            (1, List(11, 12)),
//            (2, List(21, 22)),
//          )
//          more ==> 0
//        }
//      } yield ()
//    }
  }
}

