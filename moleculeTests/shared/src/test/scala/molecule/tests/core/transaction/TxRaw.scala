package molecule.tests.core.transaction

import java.io.FileReader
import datomic.Util
import molecule.datomic.api.out1._
import molecule.datomic.base.util.SystemPeer
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object TxRaw extends AsyncTestSuite {

  lazy val tests = Tests {

  "Transact external data from file" - core { implicit conn =>

    // Initial data
    _ <- Ns.int(1).save
    _ <- Ns.int.get === List(1)

    if (system == SystemPeer) {
      // Add raw data from external file with edn transactional data
      val data = new FileReader("moleculeTests/jvm/resources/tests/core/time/save2-3.dtm")
      // contains: "[{:Ns/int 2} {:Ns/int 3}]"
      conn.transactRaw(Util.readAll(data).get(0).asInstanceOf[java.util.List[AnyRef]])

      // Raw data has been added
      _ <- Ns.int.get === List(1, 2, 3)
    }
    } yield ()
    }

"Transact local raw transactional data" - core {   implicit conn =>
      for {

    // Initial data
    _ <- Ns.int(1).save
    _ <- Ns.int.get === List(1)

    // Add raw transactional data
    // (Scala integers are internally stored as Longs)
    conn.transactRaw(Util.list(
      Util.map(Util.read(":Ns/int"), 2L.asInstanceOf[AnyRef]),
      Util.map(Util.read(":Ns/int"), 3L.asInstanceOf[AnyRef])
    ).asInstanceOf[java.util.List[AnyRef]])

    // Raw data has been added
    _ <- Ns.int.get === List(1, 2, 3)
    } yield ()
    }

"Async transaction of raw data" - core {   implicit conn =>
      for {

    // Initial data
    _ <- Ns.int(1).save
    _ <- Ns.int.get === List(1)

    // todo: remove once peer-server allows async
    if (system == SystemPeer) {
      Await.result(
        // Add raw transactional data
        // (Scala integers are internally stored as Longs)
        conn.transactAsyncRaw(
          Util.list(
            Util.map(Util.read(":Ns/int"), 2L.asInstanceOf[AnyRef]),
            Util.map(Util.read(":Ns/int"), 3L.asInstanceOf[AnyRef])
          ).asInstanceOf[java.util.List[AnyRef]]
        ) map { txReport =>
          // Raw data has been added
          _ <- Ns.int.get === List(1, 2, 3)
        },
        2.seconds
      )
    }
  }
  }
}