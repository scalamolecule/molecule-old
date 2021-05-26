package moleculeTests.tests.core.crud.update

import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out1._
import molecule.core.ops.exception.VerifyModelException
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateBoolean extends AsyncTestSuite {

  lazy val tests = Tests {

  "Card-one values" - {

    "apply" - core { implicit conn =>
      for {
        tx1 <- Ns.bool(true).save
        eid = tx1.eid

        // Apply value (retracts current value)
        _ <- Ns(eid).bool(false).update
        _ <- Ns.bool.get.map(_.head ==> false)

        // Delete value (apply no value)
        _ <- Ns(eid).bool().update
        _ <- Ns.bool.get === List()


        // Applying multiple values to card-one attribute not allowed

        //      (Ns(eid).bool(true, false).update must throwA[VerifyModelException])
        //        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
        //        "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
        //        s"\n  Ns ... bool(true, false)"
      } yield ()
    }
  }

  "Card-one variables" - {

    "apply" - core { implicit conn =>
      for {
        tx1 <- Ns.bool(bool2).save
        eid = tx1.eid

        // Apply value (retracts current value)
        _ <- Ns(eid).bool(bool1).update
        _ <- Ns.bool.get.map(_.head ==> bool1)

        // Delete value (apply no value)
        _ <- Ns(eid).bool().update
        _ <- Ns.bool.get === List()


        // Applying multiple values to card-one attribute not allowed

        //      (Ns(eid).bool(bool2, bool3).update must throwA[VerifyModelException])
        //        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
        //        "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
        //        s"\n  Ns ... bool($bool2, $bool3)"
      } yield ()
    }
  }

  // Using card-many attributes with Sets of boolean values doesn't make sense since Sets will
  // coalesce any input to sets of only true/false values. Use boolean map attributes if you want
  // to keep track of multiple true/false values for one parameter.
  }
}
