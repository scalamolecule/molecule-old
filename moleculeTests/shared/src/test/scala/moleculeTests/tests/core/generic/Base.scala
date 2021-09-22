package moleculeTests.tests.core.generic

import molecule.datomic.api.out5._
import molecule.datomic.base.facade.Conn
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.generic.LogTest.delay
import scala.concurrent.{ExecutionContext, Future}


trait Base extends AsyncTestSuite {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      // Generally use `t` or `tx` to identify transaction and `txInstant` only to get
      // the wall clock time since Date's are a bit unreliable for precision.

      // First entity

      txR1 <- Ns.str("a").int(1).save
      tx1 = txR1.tx
      e1 = txR1.eid
      t1 = txR1.t
      d1 = txR1.inst
      _ = delay


      txR2 <- Ns(e1).str("b").update
      tx2 = txR2.tx
      t2 = txR2.t
      d2 = txR2.inst
      _ = delay

      txR3 <- Ns(e1).int(2).update
      tx3 = txR3.tx
      t3 = txR3.t
      d3 = txR3.inst
      _ = delay


      // Second entity

      txR4 <- Ns.str("x").int(4).save
      tx4 = txR4.tx
      e2 = txR4.eid
      t4 = txR4.t
      d4 = txR4.inst
      _ = delay

      txR5 <- Ns(e2).int(5).update
      tx5 = txR5.tx
      t5 = txR5.t
      d5 = txR5.inst
      _ = delay


      // Relationship

      txR6 <- Ref1.str1("hello").save
      tx6 = txR6.tx
      t6 = txR6.t
      d6 = txR6.inst
      e3 = txR6.eid
      _ = delay

      // e2 points to e3
      txR7 <- Ns(e2).ref1(e3).update
      tx7 = txR7.tx
      t7 = txR7.t
      d7 = txR7.inst
      _ = delay


      // Cardinality-many attributes

      // 6, 7, 8
      txR8 <- Ns.ints(6, 7, 8).save
      t8 = txR8.t
      e4 = txR8.eid
      _ = delay

      // 6, 70, 80
      txR9 <- Ns(e4).ints.replace(7 -> 70, 8 -> 80).update
      t9 = txR9.t
      _ = delay

      // 70, 80
      txR10 <- Ns(e4).ints.retract(6).update
      t10 = txR10.t
      _ = delay

      // 70, 80, 90
      txR11 <- Ns(e4).ints.assert(60).update
      t11 = txR11.t
      _ = delay

      // e2 now points to e4
      txR12 <- Ns(e2).ref1(e4).update
      tx12 = txR12.tx
      t12 = txR12.t
      d12 = txR12.inst
      _ = delay

      // e1 also points to e4
      txR13 <- Ns(e2).refs1(e4).update
      tx13 = txR13.tx
      t13 = txR13.t
      d13 = txR13.inst

    } yield {
      (
        (tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5),
        (tx6, t6, d6, e3, tx7, t7, d7),
        (t8, e4, t9, t10, t11, tx12, t12, d12, tx13, t13, d13)
      )
    }
  }
}