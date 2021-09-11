package moleculeTests.tests.core.ref

import molecule.datomic.api.out2._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object TwoStepQueries extends AsyncTestSuite {

  lazy val tests = Tests {

    "AND, unify attributes" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.str1) insert List(
          ("a", List("r1", "r2", "r3")),
          ("b", List("r1", "r2", "r4")),
          ("c", List("r1", "r4", "r5"))
        )

        // 1. Ns strings with "r2" references
        // "a" and "b"
        _ <- Ns.str.Refs1.str1_("r2").get.map { strs_with_r2s =>
          // 2. Ns strings having but omitting "r2" reference
          Ns.str(strs_with_r2s).Refs1.*(Ref1.str1.not("r2")).get.map(_ ==> List(
            ("a", List("r1", "r3")),
            ("b", List("r1", "r4"))
          ))
        }

        // Using eid

        // 1. Entities with "r2" references
        _ <- Ns.e.Refs1.str1_("r2").get.map { eids_with_r2s =>
          // Todo
          // 2. Entities having but omitting "r2" reference
          Ns(eids_with_r2s).str.Refs1.*(Ref1.str1.not("r2")).get.map(_ ==> List(
            ("a", List("r1", "r3")),
            ("b", List("r1", "r4"))
          ))
        }
      } yield ()
    }
  }
}