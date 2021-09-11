package moleculeTests.jvm.core.transaction

import java.io.FileReader
import java.util.{List => jList}
import datomic.Util
import molecule.datomic.api.out1._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object TxRaw extends AsyncTestSuite {

  lazy val tests = Tests {

    "Transact external data from file" - core { implicit conn =>
      for {
        // Initial data
        _ <- Ns.int(1).save
        _ <- Ns.int.get.map(_ ==> List(1))

        _ <- if (system == SystemPeer) {
          val data = new FileReader("moleculeTests/jvm/resources/tests/core/time/save2-3.dtm")
          for{
            // Add raw data from external file with edn transactional data
            // contains: "[{:Ns/int 2} {:Ns/int 3}]"
            _ <- conn.map(_.transactRaw(Util.readAll(data).get(0).asInstanceOf[jList[jList[_]]]))

            // Raw data has been added
            _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
          } yield ()
        } else Future.unit
      } yield ()
    }


    "Transact local raw transactional data" - core { implicit conn =>
      for {
        // Initial data
        _ <- Ns.int(1).save
        _ <- Ns.int.get.map(_ ==> List(1))

        // Add raw transactional data
        // (Scala integers are internally stored as Longs)
        _ <- conn.map(_.transactRaw(Util.list(
          Util.map(Util.read(":Ns/int"), 2L.asInstanceOf[AnyRef]),
          Util.map(Util.read(":Ns/int"), 3L.asInstanceOf[AnyRef])
        ).asInstanceOf[java.util.List[AnyRef]]))

        // Raw data has been added
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
      } yield ()
    }
  }
}