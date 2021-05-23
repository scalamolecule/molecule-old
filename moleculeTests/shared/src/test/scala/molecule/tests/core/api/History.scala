package molecule.tests.core.api

import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out3._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object History extends AsyncTestSuite {

  lazy val tests = Tests {

    "History" - core { implicit conn =>
      for {
        tx1 <- Ns.int(1).save
        e = tx1.eid
        tx2 <- Ns(e).int(2).update
        _ <- Ns(e).int.t.op.getHistory.map(_.sortBy(t => (t._2, t._3)) ==> List(
          (1, tx1.t, true),
          (1, tx2.t, false),
          (2, tx2.t, true)
        ))
      } yield ()
    }
  }
}
