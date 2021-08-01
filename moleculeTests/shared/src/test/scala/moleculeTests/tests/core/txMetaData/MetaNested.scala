package moleculeTests.tests.core.txMetaData

import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object MetaNested extends AsyncTestSuite {

  lazy val tests = Tests {

    "Nested" - core { implicit conn =>
      for {
        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3_(1)) insert List(
          (true, List("a", "b")),
          (false, Nil)
        )

        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3).get.map(_ ==>  List(
          (true, List("a", "b"), 1)
        ))

        _ <- Ns.bool.Refs1.*?(Ref1.str1).Tx(Ref3.int3).get.map(_ ==>  List(
          (true, List("a", "b"), 1),
          (false, Nil, 1)
        ))
      } yield ()
    }
  }
}