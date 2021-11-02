package moleculeTests.tests.core.crud

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out2._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateRef extends AsyncTestSuite {

  lazy val tests = Tests {

    "Related" - {

      "Card many" - core { implicit conn =>
        // Molecule doesn't allow to update across namespaces.

        // A referenced entity can be referenced from many other entities and it
        // therefore doesn't make sense to allow any one of those to update it.
        // Instead you need to expressively change the referenced entity itself with
        // its own entity id.
        for {
          _ <- Ns(42L).str("b").Ref1.int1(2).update
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't span multiple namespaces like `Ref1`."
          }

          _ <- Ns(42L).str("b").Refs1.int1(2).update
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't span multiple namespaces like `Ref1`."
          }

          _ <- Ns(42L).str("b").Refs1.*(Ref1.int1(2)).update
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Ref1`."
          }

          _ <- m(Ns(42L).str("b") + Ref2.int2(2)).update
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't be composites."
          }
        } yield ()
      }
    }
  }
}