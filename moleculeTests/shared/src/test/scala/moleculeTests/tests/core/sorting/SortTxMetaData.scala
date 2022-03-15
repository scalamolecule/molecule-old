package moleculeTests.tests.core.sorting

import molecule.core.util.Executor._
import molecule.datomic.api.out6._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SortTxMetaData extends AsyncTestSuite {


  lazy val tests = Tests {

    "composites" - core { implicit conn =>
      for {
        // 2 transactions with different tx meta data
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
  }
}
