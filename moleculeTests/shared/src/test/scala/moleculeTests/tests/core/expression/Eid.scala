package moleculeTests.tests.core.expression

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out2._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object Eid extends AsyncTestSuite {

  lazy val tests = Tests {

    "Entity id" - core { implicit conn =>
      for {
        List(e1, e2, e3, e4) <- Ns.int insert List(1, 2, 3, 4) map (_.eids)
        seq = Set(e1, e2)
        set = Seq(e3, e4)
        iterable = Iterable(e3, e4)

        _ <- Ns.int.get.map(_ ==> List(1, 2, 3, 4))

        // Single eid
        _ <- Ns(e1).int.get.map(_ ==> List(1))

        // Vararg
        _ <- Ns(e1, e2).int.a1.get.map(_ ==> List(1, 2))

        // Seq
        _ <- Ns(Seq(e1, e2)).int.a1.get.map(_ ==> List(1, 2))
        _ <- Ns(seq).int.a1.get.map(_ ==> List(1, 2))

        // Set
        _ <- Ns(Set(e3, e4)).int.a1.get.map(_ ==> List(3, 4))
        _ <- Ns(set).int.a1.get.map(_ ==> List(3, 4))

        // Iterable
        _ <- Ns(Iterable(e3, e4)).int.a1.get.map(_ ==> List(3, 4))
        _ <- Ns(iterable).int.a1.get.map(_ ==> List(3, 4))
      } yield ()
    }


    "Applied eid to namespace" - core { implicit conn =>
      for {
        List(e1, e2, e3) <- Ns.int.insert(1, 2, 3).map(_.eids)

        _ <- Ns.int.a1.get.map(_ ==> List(1, 2, 3))

        _ <- Ns(e1).int.get.map(_ ==> List(1))

        _ <- Ns(e1, e2).int.a1.get.map(_ ==> List(1, 2))

        e23 = Seq(e2, e3)
        _ <- Ns(e23).int.a1.get.map(_ ==> List(2, 3))

        e23s = Set(e2, e3)
        _ <- Ns(e23s).int.a1.get.map(_ ==> List(2, 3))
      } yield ()
    }


    "Applied eid to `e`" - core { implicit conn =>
      for {
        List(e1, e2, e3) <- Ns.int.insert(1, 2, 3).map(_.eids)

        _ <- Ns.int.a1.get.map(_ ==> List(1, 2, 3))

        _ <- Ns.e(e1).int.get.map(_ ==> List((e1, 1)))
        _ <- Ns.e_(e1).int.get.map(_ ==> List(1))

        _ <- Ns.e(e1, e2).int.a1.get.map(_ ==> List((e1, 1), (e2, 2)))
        _ <- Ns.e_(e1, e2).int.a1.get.map(_ ==> List(1, 2))

        e23 = Seq(e2, e3)
        _ <- Ns.e(e23).int.a1.get.map(_ ==> List((e2, 2), (e3, 3)))
        _ <- Ns.e_(e23).int.a1.get.map(_ ==> List(2, 3))
      } yield ()
    }


    "Input molecule" - core { implicit conn =>
      for {
        List(e1, e2, e3) <- Ns.int.insert(1, 2, 3).map(_.eids)

        ints = m(Ns(?).int.a1)

        _ <- ints(e1).get.map(_ ==> List(1))

        _ <- ints(e1, e2).get.map(_ ==> List(1, 2))

        e23 = Seq(e2, e3)
        _ <- ints.apply(e23).get.map(_ ==> List(2, 3))
      } yield ()
    }


    "e" - core { implicit conn =>
      for {
        List(e1, e2) <- Ns.int insert List(1, 2) map (_.eids)

        _ <- Ns.e.int.a1.get.map(_ ==> List((e1, 1), (e2, 2)))

        // Applying attribute values

        _ <- Ns.e.int_(1).get.map(_ ==> List(e1))
        _ <- Ns.e.int_(2).get.map(_ ==> List(e2))

        _ <- Ns.e.int(1).get.map(_ ==> List((e1, 1)))
        _ <- Ns.e.int(2).get.map(_ ==> List((e2, 2)))

        _ <- Ns.e.a1.int_(1, 2).get.map(_ ==> List(e1, e2))
        _ <- Ns.e.int(1, 2).a1.get.map(_ ==> List((e1, 1), (e2, 2)))


        // Applying entity id values
        _ <- Ns.e_(e1).int.get.map(_ ==> List(1))
        _ <- Ns.e_(e2).int.get.map(_ ==> List(2))
        // Same semantics as
        _ <- Ns(e1).int.get.map(_ ==> List(1))
        _ <- Ns(e2).int.get.map(_ ==> List(2))

        _ <- Ns.e(e1).int.a1.get.map(_ ==> List((e1, 1)))
        _ <- Ns.e(e2).int.a1.get.map(_ ==> List((e2, 2)))

        _ <- Ns.e_(e1, e2).int.a1.get.map(_ ==> List(1, 2))
        _ <- Ns.e(e1, e2).int.a1.get.map(_ ==> List((e1, 1), (e2, 2)))
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

    "Saving generic `e` values not allowed" - core { implicit conn =>
      for {
        _ <- Ns(42L).str("man").save
          .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
          err ==> s"[unexpectedAppliedId]  Applying an eid is only allowed for updates."
        }

        _ <- Ns.e(42L).str("man").save
          .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
          err ==> s"[unexpectedAppliedId]  Applying an eid is only allowed for updates."
        }
      } yield ()
    }
  }
}