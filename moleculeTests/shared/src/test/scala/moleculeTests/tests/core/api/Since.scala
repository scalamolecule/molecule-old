package moleculeTests.tests.core.api

import java.util.Date
import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out1._
import molecule.core.util.JavaUtil
import molecule.datomic.base.facade.TxReport
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Since extends AsyncTestSuite with JavaUtil {

  lazy val tests = Tests {

    "Since" - core { implicit conn =>
      for {
        tx1 <- Ns.int(1).save
        tx2 <- Ns.int(2).save
        tx3 <- Ns.int(3).save

        t1: Long = tx1.t
        t2: Long = tx2.t
        t3: Long = tx3.t

        d1: Date = tx1.inst
        d2: Date = tx2.inst
        d3: Date = tx3.inst

        _ <- Ns.int.getSince(t3) === List()
        _ <- Ns.int.getSince(t2) === List(3)
        _ <- Ns.int.getSince(t1) === List(2, 3)
        _ <- Ns.int.getSince(t1, 1) === List(2)

        _ <- Ns.int.getSince(tx3) === List()
        _ <- Ns.int.getSince(tx2) === List(3)
        _ <- Ns.int.getSince(tx1) === List(2, 3)
        _ <- Ns.int.getSince(tx1, 1) === List(2)

        _ <- Ns.int.getSince(d3) === List()
        _ <- Ns.int.getSince(d2) === List(3)
        _ <- Ns.int.getSince(d1) === List(2, 3)
        _ <- Ns.int.getSince(d1, 1) === List(2)


        _ <- Ns.int.getArraySince(t3) === Array()
        _ <- Ns.int.getArraySince(t2) === Array(3)
        _ <- Ns.int.getArraySince(t1) === Array(2, 3)
        _ <- Ns.int.getArraySince(t1, 1) === Array(2)

        _ <- Ns.int.getArraySince(tx3) === Array()
        _ <- Ns.int.getArraySince(tx2) === Array(3)
        _ <- Ns.int.getArraySince(tx1) === Array(2, 3)
        _ <- Ns.int.getArraySince(tx1, 1) === Array(2)

        _ <- Ns.int.getArraySince(d3) === Array()
        _ <- Ns.int.getArraySince(d2) === Array(3)
        _ <- Ns.int.getArraySince(d1) === Array(2, 3)
        _ <- Ns.int.getArraySince(d1, 1) === Array(2)


        _ <- Ns.int.getIterableSince(t3).map(_.iterator.toList ==> Iterator().toList)
        _ <- Ns.int.getIterableSince(t2).map(_.iterator.toList ==> Iterator(3).toList)
        _ <- Ns.int.getIterableSince(t1).map(_.iterator.toList ==> Iterator(2, 3).toList)

        _ <- Ns.int.getIterableSince(tx3).map(_.iterator.toList ==> Iterator().toList)
        _ <- Ns.int.getIterableSince(tx2).map(_.iterator.toList ==> Iterator(3).toList)
        _ <- Ns.int.getIterableSince(tx1).map(_.iterator.toList ==> Iterator(2, 3).toList)

        _ <- Ns.int.getIterableSince(d3).map(_.iterator.toList ==> Iterator().toList)
        _ <- Ns.int.getIterableSince(d2).map(_.iterator.toList ==> Iterator(3).toList)
        _ <- Ns.int.getIterableSince(d1).map(_.iterator.toList ==> Iterator(2, 3).toList)

        _ <- Ns.int.getRawSince(t3).map(_.ints ==> List())
        _ <- Ns.int.getRawSince(t2).map(_.ints ==> List(3))
        _ <- Ns.int.getRawSince(t1).map(_.ints ==> List(2, 3))
        _ <- Ns.int.getRawSince(t1, 1).map(_.ints ==> List(2))

        _ <- Ns.int.getRawSince(tx3).map(_.ints ==> List())
        _ <- Ns.int.getRawSince(tx2).map(_.ints ==> List(3))
        _ <- Ns.int.getRawSince(tx1).map(_.ints ==> List(2, 3))
        _ <- Ns.int.getRawSince(tx1, 1).map(_.ints ==> List(2))

        _ <- Ns.int.getRawSince(d3).map(_.ints ==> List())
        _ <- Ns.int.getRawSince(d2).map(_.ints ==> List(3))
        _ <- Ns.int.getRawSince(d1).map(_.ints ==> List(2, 3))
        _ <- Ns.int.getRawSince(d1, 1).map(_.ints ==> List(2))
      } yield ()
    }
  }
}