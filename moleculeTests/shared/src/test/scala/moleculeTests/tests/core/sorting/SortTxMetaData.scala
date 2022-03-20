package moleculeTests.tests.core.sorting

import molecule.core.util.Executor._
import molecule.datomic.api.out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SortTxMetaData extends AsyncTestSuite {


  lazy val tests = Tests {

    "flat" - core { implicit conn =>
      for {
        // 2 transactions with different tx meta data
        _ <- Ns.int.Tx(Ref1.str1_("A")) insert List(1, 2)
        _ <- Ns.int.Tx(Ref1.str1_("B")) insert List(1, 2)

        _ <- Ns.int.a2.Tx(Ref1.str1.a1).get.map(_ ==> List(
          (1, "A"),
          (2, "A"),
          (1, "B"),
          (2, "B"),
        ))
        _ <- Ns.int.d2.Tx(Ref1.str1.a1).get.map(_ ==> List(
          (2, "A"),
          (1, "A"),
          (2, "B"),
          (1, "B"),
        ))
        _ <- Ns.int.a2.Tx(Ref1.str1.d1).get.map(_ ==> List(
          (1, "B"),
          (2, "B"),
          (1, "A"),
          (2, "A"),
        ))
        _ <- Ns.int.d2.Tx(Ref1.str1.d1).get.map(_ ==> List(
          (2, "B"),
          (1, "B"),
          (2, "A"),
          (1, "A"),
        ))

        _ <- Ns.int.a1.Tx(Ref1.str1.a2).get.map(_ ==> List(
          (1, "A"),
          (1, "B"),
          (2, "A"),
          (2, "B"),
        ))
        _ <- Ns.int.d1.Tx(Ref1.str1.a2).get.map(_ ==> List(
          (2, "A"),
          (2, "B"),
          (1, "A"),
          (1, "B"),
        ))
        _ <- Ns.int.a1.Tx(Ref1.str1.d2).get.map(_ ==> List(
          (1, "B"),
          (1, "A"),
          (2, "B"),
          (2, "A"),
        ))
        _ <- Ns.int.d1.Tx(Ref1.str1.d2).get.map(_ ==> List(
          (2, "B"),
          (2, "A"),
          (1, "B"),
          (1, "A"),
        ))
      } yield ()
    }


    "composites" - core { implicit conn =>
      for {
        _ <- (Ns.int.str + Ref1.str1.int1.Tx(Ref3.str3_("hello"))) insert Seq(
          ((1, "a"), ("aa", 11)),
          ((2, "b"), ("bb", 22))
        )
        _ <- (Ns.int.str + Ref1.str1.int1.Tx(Ref3.str3_("world"))) insert Seq(
          ((3, "a"), ("aa", 11)),
          ((4, "b"), ("bb", 22))
        )

        _ <- m(Ns.int.a2.str + Ref1.str1.int1.Tx(Ref3.str3.a1)).get.map(_ ==> List(
          ((1, "a"), ("aa", 11, "hello")),
          ((2, "b"), ("bb", 22, "hello")),
          ((3, "a"), ("aa", 11, "world")),
          ((4, "b"), ("bb", 22, "world")),
        ))
        _ <- m(Ns.int.d2.str + Ref1.str1.int1.Tx(Ref3.str3.a1)).get.map(_ ==> List(
          ((2, "b"), ("bb", 22, "hello")),
          ((1, "a"), ("aa", 11, "hello")),
          ((4, "b"), ("bb", 22, "world")),
          ((3, "a"), ("aa", 11, "world")),
        ))
        _ <- m(Ns.int.str + Ref1.str1.a2.int1.Tx(Ref3.str3.a1)).get.map(_ ==> List(
          ((1, "a"), ("aa", 11, "hello")),
          ((2, "b"), ("bb", 22, "hello")),
          ((3, "a"), ("aa", 11, "world")),
          ((4, "b"), ("bb", 22, "world")),
        ))
        _ <- m(Ns.int.str + Ref1.str1.d2.int1.Tx(Ref3.str3.a1)).get.map(_ ==> List(
          ((2, "b"), ("bb", 22, "hello")),
          ((1, "a"), ("aa", 11, "hello")),
          ((4, "b"), ("bb", 22, "world")),
          ((3, "a"), ("aa", 11, "world")),
        ))

        _ <- m(Ns.int.a2.str + Ref1.str1.int1.Tx(Ref3.str3.d1)).get.map(_ ==> List(
          ((3, "a"), ("aa", 11, "world")),
          ((4, "b"), ("bb", 22, "world")),
          ((1, "a"), ("aa", 11, "hello")),
          ((2, "b"), ("bb", 22, "hello")),
        ))
        _ <- m(Ns.int.d2.str + Ref1.str1.int1.Tx(Ref3.str3.d1)).get.map(_ ==> List(
          ((4, "b"), ("bb", 22, "world")),
          ((3, "a"), ("aa", 11, "world")),
          ((2, "b"), ("bb", 22, "hello")),
          ((1, "a"), ("aa", 11, "hello")),
        ))
        _ <- m(Ns.int.str + Ref1.str1.a2.int1.Tx(Ref3.str3.d1)).get.map(_ ==> List(
          ((3, "a"), ("aa", 11, "world")),
          ((4, "b"), ("bb", 22, "world")),
          ((1, "a"), ("aa", 11, "hello")),
          ((2, "b"), ("bb", 22, "hello")),
        ))
        _ <- m(Ns.int.str + Ref1.str1.d2.int1.Tx(Ref3.str3.d1)).get.map(_ ==> List(
          ((4, "b"), ("bb", 22, "world")),
          ((3, "a"), ("aa", 11, "world")),
          ((2, "b"), ("bb", 22, "hello")),
          ((1, "a"), ("aa", 11, "hello")),
        ))

        _ <- m(Ns.int.d4.str.d3 + Ref1.str1.d2.int1.d5.Tx(Ref3.str3.d1)).get.map(_ ==> List(
          ((4, "b"), ("bb", 22, "world")),
          ((3, "a"), ("aa", 11, "world")),
          ((2, "b"), ("bb", 22, "hello")),
          ((1, "a"), ("aa", 11, "hello")),
        ))
      } yield ()
    }


    "nested" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1).Tx(Ref3.str3_("X")) insert List(
          ("a", List(1, 2)),
          ("b", List(1, 2)),
          ("c", Nil),
        )
        _ <- Ns.str.Refs1.*(Ref1.int1).Tx(Ref3.str3_("Y")) insert List(
          ("a", List(1, 2)),
          ("b", List(1, 2)),
          ("c", Nil),
        )

        _ <- Ns.str.a2.Refs1.*(Ref1.int1.a1).Tx(Ref3.str3.a1).get.map(_ ==> List(
          ("a", List(1, 2), "X"),
          ("b", List(1, 2), "X"),
          ("a", List(1, 2), "Y"),
          ("b", List(1, 2), "Y"),
        ))

        _ <- Ns.str.d2.Refs1.*(Ref1.int1.a1).Tx(Ref3.str3.a1).get.map(_ ==> List(
          ("b", List(1, 2), "X"),
          ("a", List(1, 2), "X"),
          ("b", List(1, 2), "Y"),
          ("a", List(1, 2), "Y"),
        ))
        _ <- Ns.str.a2.Refs1.*(Ref1.int1.d1).Tx(Ref3.str3.d1).get.map(_ ==> List(
          ("a", List(2, 1), "Y"),
          ("b", List(2, 1), "Y"),
          ("a", List(2, 1), "X"),
          ("b", List(2, 1), "X"),
        ))
        _ <- Ns.str.d2.Refs1.*(Ref1.int1.d1).Tx(Ref3.str3.d1).get.map(_ ==> List(
          ("b", List(2, 1), "Y"),
          ("a", List(2, 1), "Y"),
          ("b", List(2, 1), "X"),
          ("a", List(2, 1), "X"),
        ))
        _ <- Ns.str.a1.Refs1.*(Ref1.int1.d1).Tx(Ref3.str3.d2).get.map(_ ==> List(
          ("a", List(2, 1), "Y"),
          ("a", List(2, 1), "X"),
          ("b", List(2, 1), "Y"),
          ("b", List(2, 1), "X"),
        ))

        _ <- Ns.str.a2.Refs1.*?(Ref1.int1.a1).Tx(Ref3.str3.a1).get.map(_ ==> List(
          ("a", List(1, 2), "X"),
          ("b", List(1, 2), "X"),
          ("c", Nil, "X"),
          ("a", List(1, 2), "Y"),
          ("b", List(1, 2), "Y"),
          ("c", Nil, "Y"),
        ))
        _ <- Ns.str.d2.Refs1.*?(Ref1.int1.a1).Tx(Ref3.str3.a1).get.map(_ ==> List(
          ("c", Nil, "X"),
          ("b", List(1, 2), "X"),
          ("a", List(1, 2), "X"),
          ("c", Nil, "Y"),
          ("b", List(1, 2), "Y"),
          ("a", List(1, 2), "Y"),
        ))
        _ <- Ns.str.a2.Refs1.*?(Ref1.int1.d1).Tx(Ref3.str3.d1).get.map(_ ==> List(
          ("a", List(2, 1), "Y"),
          ("b", List(2, 1), "Y"),
          ("c", Nil, "Y"),
          ("a", List(2, 1), "X"),
          ("b", List(2, 1), "X"),
          ("c", Nil, "X"),
        ))
        _ <- Ns.str.d2.Refs1.*?(Ref1.int1.d1).Tx(Ref3.str3.d1).get.map(_ ==> List(
          ("c", Nil, "Y"),
          ("b", List(2, 1), "Y"),
          ("a", List(2, 1), "Y"),
          ("c", Nil, "X"),
          ("b", List(2, 1), "X"),
          ("a", List(2, 1), "X"),
        ))
        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.d1).Tx(Ref3.str3.d2).get.map(_ ==> List(
          ("a", List(2, 1), "Y"),
          ("a", List(2, 1), "X"),
          ("b", List(2, 1), "Y"),
          ("b", List(2, 1), "X"),
          ("c", Nil, "Y"),
          ("c", Nil, "X"),
        ))
      } yield ()
    }


    "nested complex" - core { implicit conn =>
      for {

        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2_("x1").int2_(111).Ref3.str3_("x2") + Ns.int_(222).bool_(true))) insert List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil))),
          ("B", Nil)
        )
        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2_("y1").int2_(333).Ref3.str3_("y2") + Ns.int_(444).bool_(false))) insert List(
          ("C", List(
            (4, "enum20", "d", List(40, 41)))),
        )

        // Ns.str ...............................

        // asc
        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.a1.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // desc
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
        ))


        // Ref1.int1 ...............................

        // asc
        _ <- Ns.str.Refs1.*?(Ref1.int1.a1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.a1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // desc
        _ <- Ns.str.Refs1.*?(Ref1.int1.d1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.d1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // + previous
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.d1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
        ))


        // Ref2.enum2 ...............................

        // asc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.a1.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.a1.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // desc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.d1.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.d1.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // + previous
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.d2.Ref2.enum2.d1.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d2.Ref2.enum2.d1.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
        ))


        // Ref2.str2 ...............................

        // asc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.a1.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.a1.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // desc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.d1.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.d1.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // + previous
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (2, "enum21", "b", List(20, 21)),
            (1, "enum20", "a", List(10, 11))),
            ("x1", 111, "x2"), (222, true)),
        ))


        // Ref3.int3 ...............................

        // asc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3.a1))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3.a1))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // desc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(11, 10)),
            (2, "enum21", "b", List(21, 20)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(11, 10)),
            (2, "enum21", "b", List(21, 20))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // + previous
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*?(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(21, 20)),
            (1, "enum20", "a", List(11, 10))),
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (2, "enum21", "b", List(21, 20)),
            (1, "enum20", "a", List(11, 10))),
            ("x1", 111, "x2"), (222, true)),
        ))


        // Tx.Ref2.str1/int2..............................

        // asc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.a1.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.a1.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // desc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.d1.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.d1.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
        ))

        // + previous
        // Top level Ns.str is now asc and sorted after Tx.Ref2.int2 that is also considered on top level.
        _ <- Ns.str.a2.Refs1.*?(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*?(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.d1.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(21, 20)),
            (1, "enum20", "a", List(11, 10))),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.a2.Refs1.*(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.d1.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (2, "enum21", "b", List(21, 20)),
            (1, "enum20", "a", List(11, 10))),
            ("x1", 111, "x2"), (222, true)),
        ))


        // Tx.Ref2.Ref3.str3 ...............................

        // asc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3.a1 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3.a1 + Ns.int.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        // desc
        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3.d1 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3.d1 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
        ))

        // + previous
        _ <- Ns.str.a3.Refs1.*?(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*?(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.d2.Ref3.str3.d1 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(21, 20)),
            (1, "enum20", "a", List(11, 10))),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.a3.Refs1.*(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.d2.Ref3.str3.d1 + Ns.int.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (2, "enum21", "b", List(21, 20)),
            (1, "enum20", "a", List(11, 10))),
            ("x1", 111, "x2"), (222, true)),
        ))


        // Tx.Ns.int/bool ...............................

        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.a1.bool).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool.d1).get.map(_ ==> List(
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
        ))

        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.enum2.str2.Refs3.*?(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool.a1).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21)),
            (3, "enum22", "c", Nil)),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.enum2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.d1.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(40, 41))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (1, "enum20", "a", List(10, 11)),
            (2, "enum21", "b", List(20, 21))),
            ("x1", 111, "x2"), (222, true)),
        ))

        // + previous
        _ <- Ns.str.a4.Refs1.*?(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*?(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.d3.Ref3.str3.d2 + Ns.int.d1.bool).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (3, "enum22", "c", Nil),
            (2, "enum21", "b", List(21, 20)),
            (1, "enum20", "a", List(11, 10))),
            ("x1", 111, "x2"), (222, true)),
          ("B", Nil,
            ("x1", 111, "x2"), (222, true)),
        ))
        _ <- Ns.str.a4.Refs1.*(Ref1.int1.d3.Ref2.enum2.d2.str2.d1.Refs3.*(Ref3.int3.d1))
          .Tx(Ref2.str2.int2.d3.Ref3.str3.d2 + Ns.int.bool.a1).get.map(_ ==> List(
          ("C", List(
            (4, "enum20", "d", List(41, 40))),
            ("y1", 333, "y2"), (444, false)),
          ("A", List(
            (2, "enum21", "b", List(21, 20)),
            (1, "enum20", "a", List(11, 10))),
            ("x1", 111, "x2"), (222, true)),
        ))
      } yield ()
    }
  }
}
