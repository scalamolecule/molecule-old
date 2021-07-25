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
        _ <- Ns.Refs1.*(Ref1.str1).insert(List("a", "b"))
        _ <- Ns.Refs1.*(Ref1.str1).get.map(_.head ==> List("a", "b"))

        _ <- Ns.Ref1.Refs2.*(Ref2.str2).insert(List("c", "d"))
        _ <- Ns.Ref1.Refs2.*(Ref2.str2).get.map(_.head ==> List("c", "d"))
      } yield ()
    }


    "Post attributes" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1).bool insert List(
          ("a", List(10, 11), true)
        )
        _ <- Ns.str.Refs1.*(Ref1.int1).bool.get.map(_ ==> List(
          ("a", List(10, 11), true)
        ))

        // Post attributes allow nested and ref data to be inserted in one go
        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1 insert List(
          ("a", List(10, 11), 1, "aa")
        )
        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1.get.map(_ ==> List(
          ("a", List(10, 11), 1, "aa")
        ))
      } yield ()
    }
  }
}