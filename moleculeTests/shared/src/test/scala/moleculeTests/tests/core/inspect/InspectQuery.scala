package moleculeTests.tests.core.inspect

import molecule.datomic.api.out2._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object InspectQuery extends AsyncTestSuite {

  val one = 1

  lazy val tests = Tests {

    "Before nested" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1) insert List(
          (1, List(1, 2)),
          (2, List(2, 3)),
          (3, List())
        )

        _ <- m(Ns.int.>(1).Refs1 * Ref1.int1).get.map(_ ==> List(
          (2, List(2, 3)),
        ))
        _ <- m(Ns.int.>(1).Refs1 *? Ref1.int1).get.map(_.sortBy(_._1) ==> List(
          (2, List(2, 3)),
          (3, List())
        ))

        // Using variable
        _ <- m(Ns.int.>(one).Refs1 * Ref1.int1).get.map(_ ==> List(
          (2, List(2, 3))
        ))
        _ <- m(Ns.int.>(one).Refs1 *? Ref1.int1).get.map(_.sortBy(_._1) ==> List(
          (2, List(2, 3)),
          (3, List())
        ))
      } yield ()
    }
  }
}
