package moleculeTests.tests.core.api

import molecule.core.util.JavaUtil
import molecule.datomic.api.out1._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object AsOf extends AsyncTestSuite with JavaUtil {

  lazy val tests = Tests {

    "AsOf" - core { implicit conn =>
      for {
        tx1 <- Ns.int(1).save
        // Avoid date ms overlaps (use tx or t instead for precision)
        //                                        Thread.sleep(100)
        tx2 <- Ns.int(2).save
        //        Thread.sleep(100)
        tx3 <- Ns.int(3).save

        t1 = tx1.t
        t2 = tx2.t
        t3 = tx3.t

        d1 = tx1.inst
        d2 = tx2.inst
        d3 = tx3.inst

        _ <- Ns.int.getAsOf(t1) === List(1)
        _ <- Ns.int.getAsOf(t2) === List(1, 2)
        _ <- Ns.int.getAsOf(t3) === List(1, 2, 3)
        _ <- Ns.int.getAsOf(t3, 2) === List(1, 2)

        _ <- Ns.int.getAsOf(tx1) === List(1)
        _ <- Ns.int.getAsOf(tx2) === List(1, 2)
        _ <- Ns.int.getAsOf(tx3) === List(1, 2, 3)
        _ <- Ns.int.getAsOf(tx3, 2) === List(1, 2)

        _ <- Ns.int.getAsOf(d1) === List(1)
        _ <- Ns.int.getAsOf(d2) === List(1, 2)
        _ <- Ns.int.getAsOf(d3) === List(1, 2, 3)
        _ <- Ns.int.getAsOf(d3, 2) === List(1, 2)


        _ <- Ns.int.getArrayAsOf(t1) === Array(1)
        _ <- Ns.int.getArrayAsOf(t2) === Array(1, 2)
        _ <- Ns.int.getArrayAsOf(t3) === Array(1, 2, 3)
        _ <- Ns.int.getArrayAsOf(t3, 2) === Array(1, 2)

        _ <- Ns.int.getArrayAsOf(tx1) === Array(1)
        _ <- Ns.int.getArrayAsOf(tx2) === Array(1, 2)
        _ <- Ns.int.getArrayAsOf(tx3) === Array(1, 2, 3)
        _ <- Ns.int.getArrayAsOf(tx3, 2) === Array(1, 2)

        _ <- Ns.int.getArrayAsOf(d1) === Array(1)
        _ <- Ns.int.getArrayAsOf(d2) === Array(1, 2)
        _ <- Ns.int.getArrayAsOf(d3) === Array(1, 2, 3)
        _ <- Ns.int.getArrayAsOf(d3, 2) === Array(1, 2)


        _ <- Ns.int.getIterableAsOf(t1).map(_.iterator.toList ==> Iterator(1).toList)
        _ <- Ns.int.getIterableAsOf(t2).map(_.iterator.toList ==> Iterator(1, 2).toList)
        _ <- Ns.int.getIterableAsOf(t3).map(_.iterator.toList ==> Iterator(1, 2, 3).toList)

        _ <- Ns.int.getIterableAsOf(tx1).map(_.iterator.toList ==> Iterator(1).toList)
        _ <- Ns.int.getIterableAsOf(tx2).map(_.iterator.toList ==> Iterator(1, 2).toList)
        _ <- Ns.int.getIterableAsOf(tx3).map(_.iterator.toList ==> Iterator(1, 2, 3).toList)

        _ <- Ns.int.getIterableAsOf(d1).map(_.iterator.toList ==> Iterator(1).toList)
        _ <- Ns.int.getIterableAsOf(d2).map(_.iterator.toList ==> Iterator(1, 2).toList)
        _ <- Ns.int.getIterableAsOf(d3).map(_.iterator.toList ==> Iterator(1, 2, 3).toList)


        _ <- Ns.int.getRawAsOf(t1).map(_.ints ==> List(1))
        _ <- Ns.int.getRawAsOf(t2).map(_.ints ==> List(1, 2))
        _ <- Ns.int.getRawAsOf(t3).map(_.ints ==> List(1, 2, 3))
        _ <- Ns.int.getRawAsOf(t3, 2).map(_.ints ==> List(1, 2))

        _ <- Ns.int.getRawAsOf(tx1).map(_.ints ==> List(1))
        _ <- Ns.int.getRawAsOf(tx2).map(_.ints ==> List(1, 2))
        _ <- Ns.int.getRawAsOf(tx3).map(_.ints ==> List(1, 2, 3))
        _ <- Ns.int.getRawAsOf(tx3, 2).map(_.ints ==> List(1, 2))

        _ <- Ns.int.getRawAsOf(d1).map(_.ints ==> List(1))
        _ <- Ns.int.getRawAsOf(d2).map(_.ints ==> List(1, 2))
        _ <- Ns.int.getRawAsOf(d3).map(_.ints ==> List(1, 2, 3))
        _ <- Ns.int.getRawAsOf(d3, 2).map(_.ints ==> List(1, 2))
      } yield ()
    }
  }
}
