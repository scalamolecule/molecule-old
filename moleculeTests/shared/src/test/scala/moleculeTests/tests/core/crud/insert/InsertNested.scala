package moleculeTests.tests.core.crud.insert

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import scala.concurrent.ExecutionContext.Implicits.global
import utest._

object InsertNested extends AsyncTestSuite {

  lazy val tests = Tests {


    "Data inserted at runtime" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1) insert List(
          ("a", List(1, 2)),
          // Since data is inserted at runtime, empty nested data is simply not added
          ("b", Nil)
        )

        // Get optional nested data
        _ <- Ns.str.Refs1.*?(Ref1.int1).get.map(_ ==> List(
          ("a", List(1, 2)),
          ("b", Nil)
        ))

        // Get mandatory nested data
        _ <- Ns.str.Refs1.*(Ref1.int1).get.map(_ ==> List(
          ("a", List(1, 2))
        ))
      } yield ()
    }

    "With intermediary namespaces without attributes" - core { implicit conn =>
      for {
        tx1 <- Ns.Refs1.*(Ref1.str1).insert(List("a", "b"))
        _ <- Ns.Refs1.*(Ref1.str1).get.map(_.head ==> List("a", "b"))

        List(e, _, _) = tx1.eids
        _ <- Ns.e.Refs1.*(Ref1.str1).get.map(_.head ==> (e, List("a", "b")))
        _ <- Ns(e).Refs1.*(Ref1.str1).get.map(_.head ==> List("a", "b"))


        tx2 <- Ns.Ref1.Refs2.*(Ref2.str2).insert(List("c", "d"))
        _ <- Ns.Ref1.Refs2.*(Ref2.str2).get.map(_.head ==> List("c", "d"))

        List(eNs, eRef1, _, _) = tx2.eids
        _ <- Ns.e.Ref1.Refs2.*(Ref2.str2).get.map(_.head ==> (eNs, List("c", "d")))
        _ <- Ns(eNs).Ref1.Refs2.*(Ref2.str2).get.map(_.head ==> List("c", "d"))
        _ <- Ns.Ref1.e.Refs2.*(Ref2.str2).get.map(_.head ==> (eRef1, List("c", "d")))
        _ <- Ns.e.Ref1.e.Refs2.*(Ref2.str2).get.map(_.head ==> (eNs, eRef1, List("c", "d")))
      } yield ()
    }

    // on hold - does it make semantic sense?
    //    "Post attributes" - core { implicit conn =>
    //      for {
    //        _ <- Ns.str.Refs1.*(Ref1.int1).bool insert List(
    //          ("a", List(10, 11), true)
    //        )
    //        _ <- Ns.str.Refs1.*(Ref1.int1).bool.get.map(_ ==> List(
    //          ("a", List(10, 11), true)
    //        ))
    //
    //        // Post attributes allow nested and ref data to be inserted in one go
    //        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1 insert List(
    //          ("a", List(10, 11), 1, "aa")
    //        )
    //        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1.get.map(_ ==> List(
    //          ("a", List(10, 11), 1, "aa")
    //        ))
    //      } yield ()
    //    }
  }
}