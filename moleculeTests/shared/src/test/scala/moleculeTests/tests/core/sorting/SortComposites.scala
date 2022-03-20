package moleculeTests.tests.core.sorting

import molecule.core.util.Executor._
import molecule.datomic.api.in1_out4._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SortComposites extends AsyncTestSuite {


  lazy val tests = Tests {

    "Input" - core { implicit conn =>
      for {
        _ <- Ns.int + Ref1.int1 insert List((1, 10), (2, 20))
        _ <- m(Ns.int(?).a1 + Ref1.int1)(List(1, 2)).get.map(_ ==> List((1, 10), (2, 20)))
        _ <- m(Ns.int(?).d1 + Ref1.int1)(List(1, 2)).get.map(_ ==> List((2, 20), (1, 10)))
      } yield ()
    }


    "1 + 1" - core { implicit conn =>
      for {
        _ <- Ns.str + Ref1.int1 insert List(
          ("A", 1),
          ("A", 2),
          ("B", 1),
          ("B", 2),
        )

        _ <- (Ns.str.a1 + Ref1.int1.a2).get.map(_ ==> List(
          ("A", 1),
          ("A", 2),
          ("B", 1),
          ("B", 2),
        ))
        _ <- (Ns.str.a1 + Ref1.int1.d2).get.map(_ ==> List(
          ("A", 2),
          ("A", 1),
          ("B", 2),
          ("B", 1),
        ))
        _ <- (Ns.str.d1 + Ref1.int1.a2).get.map(_ ==> List(
          ("B", 1),
          ("B", 2),
          ("A", 1),
          ("A", 2),
        ))
        _ <- (Ns.str.d1 + Ref1.int1.d2).get.map(_ ==> List(
          ("B", 2),
          ("B", 1),
          ("A", 2),
          ("A", 1),
        ))

        _ <- (Ns.str.a2 + Ref1.int1.a1).get.map(_ ==> List(
          ("A", 1),
          ("B", 1),
          ("A", 2),
          ("B", 2),
        ))
        _ <- (Ns.str.d2 + Ref1.int1.a1).get.map(_ ==> List(
          ("B", 1),
          ("A", 1),
          ("B", 2),
          ("A", 2),
        ))
        _ <- (Ns.str.a2 + Ref1.int1.d1).get.map(_ ==> List(
          ("A", 2),
          ("B", 2),
          ("A", 1),
          ("B", 1),
        ))
        _ <- (Ns.str.d2 + Ref1.int1.d1).get.map(_ ==> List(
          ("B", 2),
          ("A", 2),
          ("B", 1),
          ("A", 1),
        ))
      } yield ()
    }


    "1 + 2" - core { implicit conn =>
      for {
        _ <- Ns.str + Ref1.int1.str1 insert List(
          ("A", (1, "x")),
          ("A", (2, "y")),
          ("B", (1, "x")),
          ("B", (2, "y")),
        )

        _ <- (Ns.str.a1 + Ref1.int1.a2.str1).get.map(_ ==> List(
          ("A", (1, "x")),
          ("A", (2, "y")),
          ("B", (1, "x")),
          ("B", (2, "y")),
        ))
        _ <- (Ns.str.a1 + Ref1.int1.str1.d2).get.map(_ ==> List(
          ("A", (2, "y")),
          ("A", (1, "x")),
          ("B", (2, "y")),
          ("B", (1, "x")),
        ))
        _ <- (Ns.str.d1 + Ref1.int1.str1.a2).get.map(_ ==> List(
          ("B", (1, "x")),
          ("B", (2, "y")),
          ("A", (1, "x")),
          ("A", (2, "y")),
        ))
        _ <- (Ns.str.d1 + Ref1.int1.d2.str1).get.map(_ ==> List(
          ("B", (2, "y")),
          ("B", (1, "x")),
          ("A", (2, "y")),
          ("A", (1, "x")),
        ))

        _ <- (Ns.str.a2 + Ref1.int1.a1.str1).get.map(_ ==> List(
          ("A", (1, "x")),
          ("B", (1, "x")),
          ("A", (2, "y")),
          ("B", (2, "y")),
        ))
        _ <- (Ns.str.d2 + Ref1.int1.str1.a1).get.map(_ ==> List(
          ("B", (1, "x")),
          ("A", (1, "x")),
          ("B", (2, "y")),
          ("A", (2, "y")),
        ))
        _ <- (Ns.str.a2 + Ref1.int1.str1.d1).get.map(_ ==> List(
          ("A", (2, "y")),
          ("B", (2, "y")),
          ("A", (1, "x")),
          ("B", (1, "x")),
        ))
        _ <- (Ns.str.d2 + Ref1.int1.d1.str1).get.map(_ ==> List(
          ("B", (2, "y")),
          ("A", (2, "y")),
          ("B", (1, "x")),
          ("A", (1, "x")),
        ))
      } yield ()
    }


    "2 + 2" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref1.int1.str1 insert List(
          (("A", 3), (1, "x")),
          (("A", 3), (2, "y")),
          (("B", 4), (1, "x")),
          (("B", 4), (2, "y")),
        )

        _ <- (Ns.str.a1.int + Ref1.int1.a2.str1).get.map(_ ==> List(
          (("A", 3), (1, "x")),
          (("A", 3), (2, "y")),
          (("B", 4), (1, "x")),
          (("B", 4), (2, "y")),
        ))
        _ <- (Ns.str.int.a1 + Ref1.int1.str1.d2).get.map(_ ==> List(
          (("A", 3), (2, "y")),
          (("A", 3), (1, "x")),
          (("B", 4), (2, "y")),
          (("B", 4), (1, "x")),
        ))
        _ <- (Ns.str.int.d1 + Ref1.int1.str1.a2).get.map(_ ==> List(
          (("B", 4), (1, "x")),
          (("B", 4), (2, "y")),
          (("A", 3), (1, "x")),
          (("A", 3), (2, "y")),
        ))
        _ <- (Ns.str.d1.int + Ref1.int1.d2.str1).get.map(_ ==> List(
          (("B", 4), (2, "y")),
          (("B", 4), (1, "x")),
          (("A", 3), (2, "y")),
          (("A", 3), (1, "x")),
        ))

        _ <- (Ns.str.a2.int + Ref1.int1.a1.str1).get.map(_ ==> List(
          (("A", 3), (1, "x")),
          (("B", 4), (1, "x")),
          (("A", 3), (2, "y")),
          (("B", 4), (2, "y")),
        ))
        _ <- (Ns.str.int.d2 + Ref1.int1.str1.a1).get.map(_ ==> List(
          (("B", 4), (1, "x")),
          (("A", 3), (1, "x")),
          (("B", 4), (2, "y")),
          (("A", 3), (2, "y")),
        ))
        _ <- (Ns.str.int.a2 + Ref1.int1.str1.d1).get.map(_ ==> List(
          (("A", 3), (2, "y")),
          (("B", 4), (2, "y")),
          (("A", 3), (1, "x")),
          (("B", 4), (1, "x")),
        ))
        _ <- (Ns.str.d2.int + Ref1.int1.d1.str1).get.map(_ ==> List(
          (("B", 4), (2, "y")),
          (("A", 3), (2, "y")),
          (("B", 4), (1, "x")),
          (("A", 3), (1, "x")),
        ))
      } yield ()
    }


    "2 + 2.tx(2)" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref1.int1.str1.Tx(Ref3.int3_(7).str3_("hello")) insert List(
          (("A", 3), (1, "x")),
          (("A", 3), (2, "y")),
          (("B", 4), (1, "x")),
        )
        _ <- Ns.str.int + Ref1.int1.str1.Tx(Ref3.int3_(8).str3_("world")) insert List(
          (("B", 4), (2, "y")),
        )

        _ <- (Ns.str.a2.int + Ref1.int1.a3.str1.Tx(Ref3.int3.a1.str3)).get.map(_ ==> List(
          (("A", 3), (1, "x", 7, "hello")),
          (("A", 3), (2, "y", 7, "hello")),
          (("B", 4), (1, "x", 7, "hello")),
          (("B", 4), (2, "y", 8, "world")),
        ))
        _ <- (Ns.str.int.a2 + Ref1.int1.str1.d3.Tx(Ref3.int3.str3.a1)).get.map(_ ==> List(
          (("A", 3), (2, "y", 7, "hello")),
          (("A", 3), (1, "x", 7, "hello")),
          (("B", 4), (1, "x", 7, "hello")),
          (("B", 4), (2, "y", 8, "world")),
        ))
        _ <- (Ns.str.int.d2 + Ref1.int1.str1.a3.Tx(Ref3.int3.str3.a1)).get.map(_ ==> List(
          (("B", 4), (1, "x", 7, "hello")),
          (("A", 3), (1, "x", 7, "hello")),
          (("A", 3), (2, "y", 7, "hello")),
          (("B", 4), (2, "y", 8, "world")),
        ))
        _ <- (Ns.str.d2.int + Ref1.int1.d3.str1.Tx(Ref3.int3.a1.str3)).get.map(_ ==> List(
          (("B", 4), (1, "x", 7, "hello")),
          (("A", 3), (2, "y", 7, "hello")),
          (("A", 3), (1, "x", 7, "hello")),
          (("B", 4), (2, "y", 8, "world")),
        ))

        _ <- (Ns.str.a3.int + Ref1.int1.a2.str1.Tx(Ref3.int3.d1.str3)).get.map(_ ==> List(
          (("B", 4), (2, "y", 8, "world")),
          (("A", 3), (1, "x", 7, "hello")),
          (("B", 4), (1, "x", 7, "hello")),
          (("A", 3), (2, "y", 7, "hello")),
        ))
        _ <- (Ns.str.int.d3 + Ref1.int1.str1.a2.Tx(Ref3.int3.str3.d1)).get.map(_ ==> List(
          (("B", 4), (2, "y", 8, "world")),
          (("B", 4), (1, "x", 7, "hello")),
          (("A", 3), (1, "x", 7, "hello")),
          (("A", 3), (2, "y", 7, "hello")),
        ))
        _ <- (Ns.str.int.a3 + Ref1.int1.str1.d2.Tx(Ref3.int3.str3.d1)).get.map(_ ==> List(
          (("B", 4), (2, "y", 8, "world")),
          (("A", 3), (2, "y", 7, "hello")),
          (("A", 3), (1, "x", 7, "hello")),
          (("B", 4), (1, "x", 7, "hello")),
        ))
        _ <- (Ns.str.d3.int + Ref1.int1.d2.str1.Tx(Ref3.int3.d1.str3)).get.map(_ ==> List(
          (("B", 4), (2, "y", 8, "world")),
          (("A", 3), (2, "y", 7, "hello")),
          (("B", 4), (1, "x", 7, "hello")),
          (("A", 3), (1, "x", 7, "hello")),
        ))
      } yield ()
    }


    "2 + 2.tx(2 + 1)" - core { implicit conn =>
      for {
        _ <- Ns.str.int + Ref1.int1.str1.Tx(Ref3.int3_(7).str3_("hello") + Ref4.int4_(30)) insert List(
          (("A", 3), (1, "x")),
          (("A", 3), (2, "y")),
        )
        _ <- Ns.str.int + Ref1.int1.str1.Tx(Ref3.int3_(7).str3_("hello") + Ref4.int4_(40)) insert List(
          (("B", 4), (1, "x")),
        )
        _ <- Ns.str.int + Ref1.int1.str1.Tx(Ref3.int3_(8).str3_("world") + Ref4.int4_(40)) insert List(
          (("B", 4), (2, "y")),
        )

        _ <- (Ns.str.a3.int + Ref1.int1.a4.str1.Tx(Ref3.int3.a2.str3 + Ref4.int4.a1)).get.map(_ ==> List(
          (("A", 3), (1, "x", (7, "hello"), 30)),
          (("A", 3), (2, "y", (7, "hello"), 30)),
          (("B", 4), (1, "x", (7, "hello"), 40)),
          (("B", 4), (2, "y", (8, "world"), 40)),
        ))
        _ <- (Ns.str.int.a3 + Ref1.int1.str1.d4.Tx(Ref3.int3.str3.a2 + Ref4.int4.a1)).get.map(_ ==> List(
          (("A", 3), (2, "y", (7, "hello"), 30)),
          (("A", 3), (1, "x", (7, "hello"), 30)),
          (("B", 4), (1, "x", (7, "hello"), 40)),
          (("B", 4), (2, "y", (8, "world"), 40)),
        ))
        _ <- (Ns.str.int.d3 + Ref1.int1.str1.a4.Tx(Ref3.int3.str3.a2 + Ref4.int4.a1)).get.map(_ ==> List(
          (("A", 3), (1, "x", (7, "hello"), 30)),
          (("A", 3), (2, "y", (7, "hello"), 30)),
          (("B", 4), (1, "x", (7, "hello"), 40)),
          (("B", 4), (2, "y", (8, "world"), 40)),
        ))
        _ <- (Ns.str.d3.int + Ref1.int1.d4.str1.Tx(Ref3.int3.a2.str3 + Ref4.int4.a1)).get.map(_ ==> List(
          (("A", 3), (2, "y", (7, "hello"), 30)),
          (("A", 3), (1, "x", (7, "hello"), 30)),
          (("B", 4), (1, "x", (7, "hello"), 40)),
          (("B", 4), (2, "y", (8, "world"), 40)),
        ))

        _ <- (Ns.str.a4.int + Ref1.int1.a3.str1.Tx(Ref3.int3.d2.str3 + Ref4.int4.d1)).get.map(_ ==> List(
          (("B", 4), (2, "y", (8, "world"), 40)),
          (("B", 4), (1, "x", (7, "hello"), 40)),
          (("A", 3), (1, "x", (7, "hello"), 30)),
          (("A", 3), (2, "y", (7, "hello"), 30)),
        ))
        _ <- (Ns.str.int.d4 + Ref1.int1.str1.a3.Tx(Ref3.int3.str3.d2 + Ref4.int4.d1)).get.map(_ ==> List(
          (("B", 4), (2, "y", (8, "world"), 40)),
          (("B", 4), (1, "x", (7, "hello"), 40)),
          (("A", 3), (1, "x", (7, "hello"), 30)),
          (("A", 3), (2, "y", (7, "hello"), 30)),
        ))
        _ <- (Ns.str.int.a4 + Ref1.int1.str1.d3.Tx(Ref3.int3.str3.d2 + Ref4.int4.d1)).get.map(_ ==> List(
          (("B", 4), (2, "y", (8, "world"), 40)),
          (("B", 4), (1, "x", (7, "hello"), 40)),
          (("A", 3), (2, "y", (7, "hello"), 30)),
          (("A", 3), (1, "x", (7, "hello"), 30)),
        ))
        _ <- (Ns.str.d4.int + Ref1.int1.d3.str1.Tx(Ref3.int3.d2.str3 + Ref4.int4.d1)).get.map(_ ==> List(
          (("B", 4), (2, "y", (8, "world"), 40)),
          (("B", 4), (1, "x", (7, "hello"), 40)),
          (("A", 3), (2, "y", (7, "hello"), 30)),
          (("A", 3), (1, "x", (7, "hello"), 30)),
        ))
      } yield ()
    }
  }
}
