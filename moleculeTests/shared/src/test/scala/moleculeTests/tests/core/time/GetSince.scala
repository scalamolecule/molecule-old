package moleculeTests.tests.core.time

import molecule.core.util.JavaUtil
import molecule.datomic.api.out1._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object GetSince extends AsyncTestSuite with JavaUtil {

  lazy val tests = Tests {

    "Appended" - core { implicit conn =>
      for {
        tx1 <- Ns.int(1).save
        tx2 <- Ns.int(2).save
        tx3 <- Ns.int(3).save

        // Current values
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // Exclussive tx1 value
        _ <- Ns.int.getSince(tx1).map(_ ==> List(2, 3))
        _ <- Ns.int.getSince(tx2).map(_ ==> List(3))
        _ <- Ns.int.getSince(tx3).map(_ ==> List())
      } yield ()
    }


    "Appended and updated" - core { implicit conn =>
      for {
        tx1 <- Ns.int(1).save
        tx2 <- Ns.int(2).save
        tx3 <- Ns.int(3).save

        e2 = tx2.eid
        tx4 <- Ns(e2).int(4).update

        // Current values
        _ <- Ns.int.get.map(_ ==> List(1, 3, 4))

        _ <- Ns.int.getSince(tx1).map(_ ==> List(3, 4))
        _ <- Ns.int.getSince(tx2).map(_ ==> List(3, 4))
        _ <- Ns.int.getSince(tx3).map(_ ==> List(4))
        _ <- Ns.int.getSince(tx4).map(_ ==> List())
      } yield ()
    }


    "Appended and retracted" - core { implicit conn =>
      for {
        tx1 <- Ns.int(1).save
        tx2 <- Ns.int(2).save
        tx3 <- Ns.int(3).save
        tx4 <- Ns.int(4).save
        tx5 <- tx2.eid.retract

        // Current values
        _ <- Ns.int.get.map(_ ==> List(1, 3, 4))

        _ <- Ns.int.getSince(tx1).map(_ ==> List(3, 4))
        _ <- Ns.int.getSince(tx2).map(_ ==> List(3, 4))
        _ <- Ns.int.getSince(tx3).map(_ ==> List(4))
        _ <- Ns.int.getSince(tx4).map(_ ==> List())
      } yield ()
    }
  }
}