package moleculeTests.tests.core.expression

import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.in1_out2._
import molecule.core.ops.exception.VerifyModelException
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object Eid extends AsyncTestSuite {

  lazy val tests = Tests {

    "Entity id" - core { implicit conn =>
      for {
        tx <- Ns.int insert List(1, 2, 3, 4)
        List(e1, e2, e3, e4) = tx.eids
        seq = Set(e1, e2)
        set = Seq(e3, e4)
        iterable = Iterable(e3, e4)

        _ <- Ns.int.get === List(1, 2, 3, 4)

        // Single eid
        _ <- Ns(e1).int.get === List(1)

        // Vararg
        _ <- Ns(e1, e2).int.get.map(_.sorted ==> List(1, 2))

        // Seq
        _ <- Ns(Seq(e1, e2)).int.get.map(_.sorted ==> List(1, 2))
        _ <- Ns(seq).int.get.map(_.sorted ==> List(1, 2))

        // Set
        _ <- Ns(Set(e3, e4)).int.get.map(_.sorted ==> List(3, 4))
        _ <- Ns(set).int.get.map(_.sorted ==> List(3, 4))

        // Iterable
        _ <- Ns(Iterable(e3, e4)).int.get.map(_.sorted ==> List(3, 4))
        _ <- Ns(iterable).int.get.map(_.sorted ==> List(3, 4))
      } yield ()
    }


    "Applied eid to namespace" - core { implicit conn =>
      for {
        tx <- Ns.int.insert(1, 2, 3)
        List(e1, e2, e3) = tx.eids

        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3))

        _ <- Ns(e1).int.get === List(1)

        _ <- Ns(e1, e2).int.get.map(_.sorted ==> List(1, 2))

        e23 = Seq(e2, e3)
        _ <- Ns(e23).int.get.map(_.sorted ==> List(2, 3))

        e23s = Set(e2, e3)
        _ <- Ns(e23s).int.get.map(_.sorted ==> List(2, 3))
      } yield ()
    }


    "Applied eid to `e`" - core { implicit conn =>
      for {
        tx <- Ns.int.insert(1, 2, 3)
        List(e1, e2, e3) = tx.eids

        _ <- Ns.int.get === List(1, 2, 3)

        _ <- Ns.e(e1).int.get === List((e1, 1))
        _ <- Ns.e_(e1).int.get === List(1)

        _ <- Ns.e(e1, e2).int.get.map(_.sorted ==> List((e1, 1), (e2, 2)))
        _ <- Ns.e_(e1, e2).int.get.map(_.sorted ==> List(1, 2))

        e23 = Seq(e2, e3)
        _ <- Ns.e(e23).int.get.map(_.sorted ==> List((e2, 2), (e3, 3)))
        _ <- Ns.e_(e23).int.get.map(_.sorted ==> List(2, 3))
      } yield ()
    }


    "Input molecule" - core { implicit conn =>
      for {
        tx <- Ns.int.insert(1, 2, 3)
        List(e1, e2, e3) = tx.eids

        ints = m(Ns(?).int)

        _ <- ints(e1).get === List(1)

        _ <- ints(e1, e2).get.map(_.sorted ==> List(1, 2))

        e23 = Seq(e2, e3)
        _ <- ints.apply(e23).get.map(_.sorted ==> List(2, 3))
      } yield ()
    }


    "e" - core { implicit conn =>
      for {
        tx <- Ns.int insert List(1, 2)
        List(e1, e2) = tx.eids

        _ <- Ns.e.int.get.map(_.sorted ==> List((e1, 1), (e2, 2)))

        // Applying attribute values

        _ <- Ns.e.int_(1).get === List(e1)
        _ <- Ns.e.int_(2).get === List(e2)

        _ <- Ns.e.int(1).get === List((e1, 1))
        _ <- Ns.e.int(2).get === List((e2, 2))

        _ <- Ns.e.int_(1, 2).get.map(_.sorted ==> List(e1, e2))
        _ <- Ns.e.int(1, 2).get.map(_.sorted ==> List((e1, 1), (e2, 2)))


        // Applying entity id values
        _ <- Ns.e_(e1).int.get === List(1)
        _ <- Ns.e_(e2).int.get === List(2)
        // Same semantics as
        _ <- Ns(e1).int.get === List(1)
        _ <- Ns(e2).int.get === List(2)

        _ <- Ns.e(e1).int.get.map(_.sorted ==> List((e1, 1)))
        _ <- Ns.e(e2).int.get.map(_.sorted ==> List((e2, 2)))

        _ <- Ns.e_(e1, e2).int.get.map(_.sorted ==> List(1, 2))
        _ <- Ns.e(e1, e2).int.get.map(_.sorted ==> List((e1, 1), (e2, 2)))
      } yield ()
    }


    "e count" - core { implicit conn =>
      for {
        _ <- Ns.int.Ref1.str1 insert List(
          (1, "a"),
          (2, "b"),
          (3, "c")
        )

        _ <- Ns.e(count).int_.>(1).get.map(_.head ==> 2)
        _ <- Ns.int_.>(1).e(count).get.map(_.head ==> 2)
      } yield ()
    }

    //"Saving generic `e` values not allowed" - core {   implicit conn =>
    //      for {
    //    (Ns(42L).str("man").save must throwA[VerifyModelException])
    //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
    //      s"[unexpectedAppliedId]  Applying an eid is only allowed for updates."
    //
    //    (Ns.e(42L).str("man").save must throwA[VerifyModelException])
    //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
    //      s"[unexpectedAppliedId]  Applying an eid is only allowed for updates."
    //  }yield()
    //  }
  }
}