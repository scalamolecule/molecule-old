package moleculeTests.tests.core.nested

import molecule.datomic.api.out6._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object NestedAttrs3 extends AsyncTestSuite {

  lazy val tests = Tests {

    // todo
//    "strMap" - core { implicit conn =>
//      for {
//        _ <- m(Ns.int.Refs1 * Ref1.int1.strMap1$) insert List(
//          (1, List((1, Some(Map("a" -> "aa", "b" -> "bb\nBB"))), (2, None))),
//          (2, List())
//        )
//
//        _ <- m(Ns.int.Refs1 * Ref1.int1.strMap1$).get.map(_ ==> List(
//          (1, List((1, Some(Map("a" -> "aa", "b" -> "bb\nBB"))), (2, None))),
//        ))
//        _ <- m(Ns.int.Refs1 * Ref1.int1.strMap1).get.map(_ ==> List(
//          (1, List((1, Map("a" -> 1, "b" -> "bb\nBB")))),
//        ))
//        _ <- m(Ns.int.Refs1 * Ref1.int1.strMap1_).get.map(_ ==> List(
//          (1, List(1)),
//        ))
//
//        _ <- m(Ns.int.Refs1 *? Ref1.int1.strMap1$).get.map(_.sortBy(_._1) ==> List(
//          (1, List((1, Some(Map("a" -> "aa", "b" -> "bb\nBB"))), (2, None))),
//          (2, List())
//        ))
//        _ <- m(Ns.int.Refs1 *? Ref1.int1.strMap1).get.map(_.sortBy(_._1) ==> List(
//          (1, List((1, Map("a" -> "aa", "b" -> "bb\nBB")))),
//          (2, List())
//        ))
//        _ <- m(Ns.int.Refs1 *? Ref1.int1.strMap1_).get.map(_.sortBy(_._1) ==> List(
//          (1, List(1)),
//          (2, List())
//        ))
//      } yield ()
//    }


    "intMap" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.intMap1$) insert List(
          (1, List((1, Some(Map("a" -> 1, "b" -> 2))), (2, None))),
          (2, List())
        )

        _ <- m(Ns.int.Refs1 * Ref1.int1.intMap1$).get.map(_ ==> List(
          (1, List((1, Some(Map("a" -> 1, "b" -> 2))), (2, None))),
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.intMap1).get.map(_ ==> List(
          (1, List((1, Map("a" -> 1, "b" -> 2)))),
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.intMap1_).get.map(_ ==> List(
          (1, List(1)),
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.intMap1$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(Map("a" -> 1, "b" -> 2))), (2, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.intMap1).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Map("a" -> 1, "b" -> 2)))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.intMap1_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }
  }
}