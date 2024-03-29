package moleculeTests.jvm.db.datomic

import java.io.FileReader
import datomic.Util
import molecule.datomic.api.out1._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._


object DatomicTransact extends AsyncTestSuite {

  lazy val tests = Tests {

    "Edn string" - core { implicit futConn =>
      for {
        conn <- futConn

        // Initial data
        _ <- Ns.int(1).save
        _ <- Ns.int.get.map(_ ==> List(1))

        _ <- conn.transact("[{:Ns/int 2} {:Ns/int 3}]")

        // Raw data has been added
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
      } yield ()
    }


    "Edn in file" - core { implicit futConn =>
      for {
        conn <- futConn

        // Initial data
        _ <- Ns.int(1).save
        _ <- Ns.int.get.map(_ ==> List(1))

        // Add raw data from external file with edn transactional data
        // contains: "[{:Ns/int 2} {:Ns/int 3}]"
        _ <- conn.transact(new FileReader("moleculeTests/jvm/resources/tests/core/time/save2-3.dtm"))

        // Raw data has been added
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
      } yield ()
    }


    "Java data" - core { implicit futConn =>
      for {
        conn <- futConn

        // Initial data
        _ <- Ns.int(1).save
        _ <- Ns.int.get.map(_ ==> List(1))

        // Add raw transactional data
        // (Scala integers are internally stored as Longs)
        _ <- conn.transact(Util.list(
          Util.map(Util.read(":Ns/int"), 2L.asInstanceOf[AnyRef]),
          Util.map(Util.read(":Ns/int"), 3L.asInstanceOf[AnyRef])
        ).asInstanceOf[java.util.List[AnyRef]])

        // Raw data has been added
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
      } yield ()
    }
  }
}