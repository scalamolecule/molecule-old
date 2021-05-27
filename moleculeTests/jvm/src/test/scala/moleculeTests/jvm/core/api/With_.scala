package moleculeTests.jvm.core.api

import java.io.FileReader
import java.util.{List => jList}
import molecule.core.util.JavaUtil
import molecule.datomic.api.out1._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

// Adding underscore to avoid name collision with query.With class
object With_ extends AsyncTestSuite with JavaUtil {

  lazy val tests = Tests {

    "With" - core { implicit conn =>
      for {
        _ <- Ns.int(1).save

        // Tx data from edn file
        // contains: "[{:Ns/int 2} {:Ns/int 3}]"
        data = new FileReader("moleculeTests/shared/resources/tests/core/time/save2-3.dtm")
        txData2_3 = _root_.datomic.Util.readAll(data).get(0).asInstanceOf[jList[jList[_]]]

        _ <- Ns.int.getWith(txData2_3) === List(1, 2, 3)
        _ <- Ns.int.getWith(txData2_3, 2) === List(1, 2)

        _ <- Ns.int.getArrayWith(txData2_3) === Array(1, 2, 3)
        _ <- Ns.int.getArrayWith(txData2_3, 2) === Array(1, 2)

        _ <- Ns.int.getIterableWith(txData2_3).map(_.iterator.toList ==> Iterator(1, 2, 3).toList)

        // `ints` helper method retrieve numbers from raw data
        _ <- Ns.int.getRawWith(txData2_3).map(_.ints ==> List(1, 2, 3))
        _ <- Ns.int.getRawWith(txData2_3, 2).map(_.ints ==> List(1, 2))
      } yield ()
    }
  }
}