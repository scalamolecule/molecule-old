package moleculeTests.tests.core.txMetaData

import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object MetaNested extends AsyncTestSuite {

  lazy val tests = Tests {

    "Basic" - core { implicit conn =>
      for {
        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3_(1)) insert List(
          (true, List("a", "b")),
          (false, Nil)
        )

        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3).get.map(_ ==> List(
          (true, List("a", "b"), 1)
        ))

        _ <- Ns.bool.Refs1.*?(Ref1.str1).Tx(Ref3.int3).get.map(_ ==> List(
          (true, List("a", "b"), 1),
          (false, Nil, 1)
        ))
      } yield ()
    }

    "Nested ref + flat tx meta data" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(1)) insert List(
          ("A", List((Some(11), Some(12), "a"))),
          ("B", List((Some(13), None, "b"))),
          ("C", List((None, Some(14), "c"))),
          ("D", List((None, None, "d"))),
          ("E", List())
        )

        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
          ("A", List((Some(11), Some(12), "a")), 1),
          ("B", List((Some(13), None, "b")), 1),
          ("C", List((None, Some(14), "c")), 1),
          ("D", List((None, None, "d")), 1),
          ("E", List(), 1)
        ))
      } yield ()
    }

    // todo
    //    "Nested ref + composite tx meta data" - core { implicit conn =>
    //      for {
    //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4_(7777) + Ref3.int3_(8888).str3_("meta")) insert List(
    //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa"))),
    //          ("B", List((Some(13), None, "b"))),
    //          ("C", List((None, Some(14), "c"))),
    //          ("D", List((None, None, "d"))),
    //          ("E", List())
    //        )
    //
    //        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4 + Ref3.int3.str3).get.map(_.sortBy(_._1) ==> List(
    //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, (8888, "meta")),
    //          ("B", List((Some(13), None, "b")), 7777, (8888, "meta")),
    //          ("C", List((None, Some(14), "c")), 7777, (8888, "meta")),
    //          ("D", List((None, None, "d")), 7777, (8888, "meta")),
    //          ("E", List(), 7777, (8888, "meta"))
    //        ))
    //
    //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref4.int4 + Ref3.int3.str3).get.map(_.sortBy(_._1) ==> List(
    //          ("A", List((Some(11), Some(12), "a"), (None, Some(120), "aa")), 7777, (8888, "meta")),
    //          ("B", List((Some(13), None, "b")), 7777, (8888, "meta")),
    //          ("C", List((None, Some(14), "c")), 7777, (8888, "meta")),
    //          ("D", List((None, None, "d")), 7777, (8888, "meta")),
    //        ))
    //      } yield ()
    //    }
  }
}