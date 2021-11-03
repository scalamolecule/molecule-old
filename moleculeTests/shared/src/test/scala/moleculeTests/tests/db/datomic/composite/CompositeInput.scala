package moleculeTests.tests.db.datomic.composite

import molecule.datomic.api.in3_out9._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object CompositeInput extends AsyncTestSuite {

  lazy val tests = Tests {

    "1" - core { implicit conn =>
      for {
        _ <- Ref2.int2 + Ref1.int1 + Ns.int insert List(
          (1, 11, 111),
          (2, 22, 222)
        )

        _ <- m(Ref2.int2 + Ref1.int1 + Ns.int).get.map(_.sorted ==> List(
          (1, 11, 111),
          (2, 22, 222)
        ))

        // 1 + 0 + 0
        _ <- m(Ref2.int2(?) + Ref1.int1 + Ns.int).apply(1).get.map(_ ==> List(
          (1, 11, 111)
        ))
        // 0 + 1 + 0
        _ <- m(Ref2.int2 + Ref1.int1(?) + Ns.int).apply(11).get.map(_ ==> List(
          (1, 11, 111)
        ))
        // 0 + 0 + 1
        _ <- m(Ref2.int2 + Ref1.int1 + Ns.int(?)).apply(111).get.map(_ ==> List(
          (1, 11, 111)
        ))

        // 1 + 1 + 0
        _ <- m(Ref2.int2(?) + Ref1.int1(?) + Ns.int).apply(1, 11).get.map(_ ==> List(
          (1, 11, 111)
        ))
        // 1 + 0 + 1
        _ <- m(Ref2.int2(?) + Ref1.int1 + Ns.int(?)).apply(1, 111).get.map(_ ==> List(
          (1, 11, 111)
        ))
        // 0 + 1 + 1
        _ <- m(Ref2.int2 + Ref1.int1(?) + Ns.int(?)).apply(11, 111).get.map(_ ==> List(
          (1, 11, 111)
        ))

        // 1 + 1 + 1
        _ <- m(Ref2.int2(?) + Ref1.int1(?) + Ns.int(?)).apply(1, 11, 111).get.map(_ ==> List(
          (1, 11, 111)
        ))
      } yield ()
    }


    "2" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2 + Ref1.int1.str1 + Ns.int.str insert List(
          ((1, "a"), (11, "aa"), (111, "aaa")),
          ((2, "b"), (22, "bb"), (222, "bbb"))
        )

        // 2 + 0 + 0
        _ <- m(Ref2.int2.str2 + Ref1.int1.str1 + Ns.int.str).get.map(_.sorted ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa")),
          ((2, "b"), (22, "bb"), (222, "bbb"))
        ))

        // 2 + 0 + 0
        _ <- m(Ref2.int2(?).str2(?) + Ref1.int1.str1 + Ns.int.str).apply(1, "a").get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))

        // 0 + 2 + 0
        _ <- m(Ref2.int2.str2 + Ref1.int1(?).str1(?) + Ns.int.str).apply(11, "aa").get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))

        // 0 + 0 + 2
        _ <- m(Ref2.int2.str2 + Ref1.int1.str1 + Ns.int(?).str(?)).apply(111, "aaa").get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))

        // 2 + 1 + 0
        _ <- m(Ref2.int2(?).str2(?) + Ref1.int1(?).str1 + Ns.int.str).apply(1, "a", 11).get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))
        // 2 + 0 + 1
        _ <- m(Ref2.int2(?).str2(?) + Ref1.int1.str1 + Ns.int(?).str).apply(1, "a", 111).get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))

        // 1 + 2 + 0
        _ <- m(Ref2.int2(?).str2 + Ref1.int1(?).str1(?) + Ns.int.str).apply(1, 11, "aa").get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))
        // 0 + 2 + 1
        _ <- m(Ref2.int2.str2 + Ref1.int1(?).str1(?) + Ns.int(?).str).apply(11, "aa", 111).get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))

        // 1 + 0 + 2
        _ <- m(Ref2.int2(?).str2 + Ref1.int1.str1 + Ns.int(?).str(?)).apply(1, 111, "aaa").get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))
        // 0 + 1 + 2
        _ <- m(Ref2.int2.str2 + Ref1.int1(?).str1 + Ns.int(?).str(?)).apply(11, 111, "aaa").get.map(_ ==> List(
          ((1, "a"), (11, "aa"), (111, "aaa"))
        ))
      } yield ()
    }


    "3" - core { implicit conn =>
      for {
        _ <- Ref2.int2.str2.enum2 + Ref1.int1.str1.enum1 + Ns.int.str.enumm insert List(
          ((1, "a", "enum21"), (11, "aa", "enum11"), (111, "aaa", "enum1")),
          ((2, "b", "enum22"), (22, "bb", "enum11"), (222, "bbb", "enum2"))
        )

        _ <- m(Ref2.int2.str2.enum2 + Ref1.int1.str1.enum1 + Ns.int.str.enumm).get.map(_.sorted ==> List(
          ((1, "a", "enum21"), (11, "aa", "enum11"), (111, "aaa", "enum1")),
          ((2, "b", "enum22"), (22, "bb", "enum11"), (222, "bbb", "enum2"))
        ))

        // 3 + 0 + 0
        _ <- m(Ref2.int2(?).str2(?).enum2(?) + Ref1.int1.str1.enum1 + Ns.int.str.enumm).apply(1, "a", "enum21").get.map(_ ==> List(
          ((1, "a", "enum21"), (11, "aa", "enum11"), (111, "aaa", "enum1"))
        ))
        // 0 + 3 + 0
        _ <- m(Ref2.int2.str2.enum2 + Ref1.int1(?).str1(?).enum1(?) + Ns.int.str.enumm).apply(11, "aa", "enum11").get.map(_ ==> List(
          ((1, "a", "enum21"), (11, "aa", "enum11"), (111, "aaa", "enum1"))
        ))
        // 0 + 0 + 3
        _ <- m(Ref2.int2.str2.enum2 + Ref1.int1.str1.enum1 + Ns.int(?).str(?).enumm(?)).apply(111, "aaa", "enum1").get.map(_ ==> List(
          ((1, "a", "enum21"), (11, "aa", "enum11"), (111, "aaa", "enum1"))
        ))
      } yield ()
    }
  }
}