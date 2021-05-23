package molecule.tests.core.api

import molecule.core.util.JavaUtil
import molecule.datomic.api.out1._
import molecule.setup.AsyncTestSuite
import molecule.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object With extends AsyncTestSuite with JavaUtil {

  lazy val tests = Tests {

    "With" - core { implicit conn =>
      for {
        _ <- Ns.int(1).save

        saveTx2 = Ns.int(2).getSaveStmts
        saveTx3 = Ns.int(3).getSaveStmts

        // Tx data from edn file
        // contains: "[{:Ns/int 2} {:Ns/int 3}]"
        //             data      = new FileReader("moleculeTests/jvm/resources/tests/core/time/save2-3.dtm")
        //             txData2_3 <- datomic.Util.readAll(data).get(0).asInstanceOf[java.util.List[Object]]

        _ <- Ns.int.getWith(saveTx2) === List(1, 2)
        _ <- Ns.int.getWith(saveTx2, saveTx3) === List(1, 2, 3)
        // Note how the parameter for number of rows returned is first (since we
        // need the vararg for tx molecules last)
        _ <- Ns.int.getWith(2, saveTx2, saveTx3) === List(1, 2)
        //        _ <- Ns.int.getWith(txData2_3) === List(1, 2, 3)
        //        _ <- Ns.int.getWith(txData2_3, 2) === List(1, 2)

        _ <- Ns.int.getArrayWith(saveTx2) === Array(1, 2)
        _ <- Ns.int.getArrayWith(saveTx2, saveTx3) === Array(1, 2, 3)
        _ <- Ns.int.getArrayWith(2, saveTx2, saveTx3) === Array(1, 2)
        //        _ <- Ns.int.getArrayWith(txData2_3) === Array(1, 2, 3)
        //        _ <- Ns.int.getArrayWith(txData2_3, 2) === Array(1, 2)

        _ <- Ns.int.getIterableWith(saveTx2).map(_.iterator.toList ==> Iterator(1, 2).toList)
        _ <- Ns.int.getIterableWith(saveTx2, saveTx3).map(_.iterator.toList ==> Iterator(1, 2, 3).toList)
        //        _ <- Ns.int.getIterableWith(txData2_3).iterator.toList === Iterator(1, 2, 3).toList

        _ <- Ns.int.getRawWith(saveTx2).map(_.ints ==> List(1, 2))
        _ <- Ns.int.getRawWith(saveTx2, saveTx3).map(_.ints ==> List(1, 2, 3))
        _ <- Ns.int.getRawWith(2, saveTx2, saveTx3).map(_.ints ==> List(1, 2))

        //        _ <- Ns.int.getRawWith(txData2_3).ints === List(1, 2, 3)
        //        _ <- Ns.int.getRawWith(txData2_3, 2).ints === List(1, 2)
      } yield ()
    }
  }
}