package moleculeTests.tests.core.crud.update

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out1._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateBoolean extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one values" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.bool(true).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).bool(false).update
          _ <- Ns.bool.get.map(_.head ==> false)

          // Delete value (apply no value)
          _ <- Ns(eid).bool().update
          _ <- Ns.bool.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).bool(true, false).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... bool(true, false)"
          }
        } yield ()
      }
    }

    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.bool(bool2).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).bool(bool1).update
          _ <- Ns.bool.get.map(_.head ==> bool1)

          // Delete value (apply no value)
          _ <- Ns(eid).bool().update
          _ <- Ns.bool.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).bool(bool2, bool3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... bool($bool2, $bool3)"
          }
        } yield ()
      }
    }

    // Using card-many attributes with Sets of boolean values doesn't make sense since Sets will
    // coalesce any input to sets of only true/false values. Use boolean map attributes if you want
    // to keep track of multiple true/false values for one parameter.
  }
}
