package moleculeTests.tests.core.api

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

        saveTx2 = Ns.int(2).getSaveStmts
        saveTx3 = Ns.int(3).getSaveStmts

        // For transaction of raw data, please se test in jvm: tests.api.jvm.With_

        _ <- Ns.int.getWith(saveTx2).map(_ ==> List(1, 2))
        _ <- Ns.int.getWith(saveTx2, saveTx3).map(_ ==> List(1, 2, 3))
        // Note how the parameter for number of rows returned is first (since we
        // need the vararg for tx molecules last)
        _ <- Ns.int.getWith(2, saveTx2, saveTx3).map(_ ==> List(1, 2))

        _ <- Ns.int.getArrayWith(saveTx2).map(_ ==> Array(1, 2))
        _ <- Ns.int.getArrayWith(saveTx2, saveTx3).map(_ ==> Array(1, 2, 3))
        _ <- Ns.int.getArrayWith(2, saveTx2, saveTx3).map(_ ==> Array(1, 2))

        _ <- Ns.int.getIterableWith(saveTx2).map(_.iterator.toList ==> Iterator(1, 2).toList)
        _ <- Ns.int.getIterableWith(saveTx2, saveTx3).map(_.iterator.toList ==> Iterator(1, 2, 3).toList)

        // `ints` helper method retrieve numbers from raw data
        _ <- Ns.int.getRawWith(saveTx2).map(_.ints ==> List(1, 2))
        _ <- Ns.int.getRawWith(saveTx2, saveTx3).map(_.ints ==> List(1, 2, 3))
        _ <- Ns.int.getRawWith(2, saveTx2, saveTx3).map(_.ints ==> List(1, 2))
      } yield ()
    }
  }
}