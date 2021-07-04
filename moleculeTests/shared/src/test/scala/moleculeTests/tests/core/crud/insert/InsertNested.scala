package moleculeTests.tests.core.crud.insert

import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import scala.concurrent.ExecutionContext.Implicits.global


object InsertNested extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mandatory" - core { implicit conn =>
      for {
        // If we want to create two references from the same base entity we
        // can us "group" notation `*` after our cardinality-many reference
        // and then define what sub-attributes we want to add.

        // Note that the "sub-molecule" we apply is treated as a single type
        // - when more than 1 attribute, like a tuple.

        tx <- Ns.Refs1.*(Ref1.str1).insert(List("r1", "r2"))
        List(e, r1, r2) = tx.eids

        _ <- e.touch.map(_ ==> Map(
          ":db/id" -> e,
          ":Ns/refs1" -> List(
            Map(":db/id" -> r1, ":Ref1/str1" -> "r1"),
            Map(":db/id" -> r2, ":Ref1/str1" -> "r2")
          )))

        // Like the classical order/products example
        // Note how our "sub-molecule" `Ref1.int.str` is regarded as
        // one type `Seq[(Int, String)]` by the outer molecule
        tx2 <- m(Ns.str.Refs1 * Ref1.int1.str1).insert("order", List((4, "product1"), (7, "product2")))
        List(order, p1, p2) = tx2.eids

        _ <- order.touch.map(_ ==> Map(
          ":db/id" -> order,
          ":Ns/refs1" -> List(
            Map(":db/id" -> p1, ":Ref1/int1" -> 4, ":Ref1/str1" -> "product1"),
            Map(":db/id" -> p2, ":Ref1/int1" -> 7, ":Ref1/str1" -> "product2")
          ),
          ":Ns/str" -> "order"
        ))
      } yield ()
    }


    // todo?
    "Post attribute" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1).bool insert List(
          ("a", List(10, 11), true)
        )
        _ <- Ns.str.Refs1.*(Ref1.int1).bool.get.map(_ ==> List(
          ("a", true, List(10, 11))
        ))
      } yield ()
    }
  }
}