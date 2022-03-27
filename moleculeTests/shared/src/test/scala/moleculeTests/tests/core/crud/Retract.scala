package moleculeTests.tests.core.crud

import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object Retract extends AsyncTestSuite {

  lazy val tests = Tests {

    "Implicit entity" - core { implicit conn =>
      for {
        e1 <- Ns.int.str insert List(
          (1, "a"),
          (2, "b")
        ) map(_.eid)

        _ <- Ns.int.a1.str.get.map(_ ==> List(
          (1, "a"),
          (2, "b")
        ))

        _ <- e1.retract

        _ <- Ns.int.str.get.map(_ ==> List(
          (2, "b")
        ))
      } yield ()
    }

    "Explicit entities" - core { implicit conn =>
      for {
        List(e1, e2, e3) <- Ns.int.insert(1, 2, 3).map(_.eids)

        _ <- Ns.int.apply(count).get.map(_ ==> List(3))

        _ <- retract(Seq(e1, e2))

        _ <- Ns.int(count).get.map(_ ==> List(1))
      } yield ()
    }

    "Explicit entities with tx data" - core { implicit conn =>
      for {
        List(e1, e2, e3) <- Ns.int.insert(1, 2, 3).map(_.eids)

        _ <- Ns.int(count).get.map(_ ==> List(3))

        _ <- retract(Seq(e1, e2), Ref1.str1("Some tx info"))

        _ <- Ns.int(count).get.map(_ ==> List(1))
      } yield ()
    }


    "Component" - {

      "Card-one" - core { implicit conn =>
        for {
          e1 <- Ns.int.RefSub1.int1 insert List(
            (1, 10),
            (2, 20)
          ) map(_.eid)

          _ <- Ns.int.a1.RefSub1.int1.get.map(_ ==> List(
            (1, 10),
            (2, 20)
          ))
          _ <- Ref1.int1.a1.get.map(_ ==> List(10, 20))

          _ <- e1.retract

          _ <- Ns.int.RefSub1.int1.get.map(_ ==> List(
            (2, 20)
          ))
          // Card-one sub-entity was retracted
          _ <- Ref1.int1.get.map(_ ==> List(20))
        } yield ()
      }

      "Card-many" - core { implicit conn =>
        for {
          e1 <- m(Ns.int.RefsSub1 * Ref1.int1).insert(List(
            (1, Seq(10, 11)),
            (2, Seq(20, 21))
          )).map(_.eid)

          _ <- m(Ns.int.a1.RefsSub1 * Ref1.int1).get.map(_ ==> List(
            (1, Seq(10, 11)),
            (2, Seq(20, 21))
          ))
          _ <- Ref1.int1.a1.get.map(_ ==> List(10, 11, 20, 21))

          _ <- e1.retract

          _ <- m(Ns.int.RefsSub1 * Ref1.int1).get.map(_ ==> List(
            (2, Seq(20, 21))
          ))
          // Card-many sub-entitities were retracted
          _ <- Ref1.int1.get.map(_ ==> List(20, 21))
        } yield ()
      }

      "Nested" - core { implicit conn =>
        for {
          e1 <- Ns.int.RefsSub1.*(Ref1.int1.RefsSub2.*(Ref2.int2)).insert(List(
            (1, Seq(
              (10, Seq(100, 101)),
              (11, Seq(110, 111)))),
            (2, Seq(
              (20, Seq(200, 201)),
              (21, Seq(210, 211))))
          )).map(_.eid)

          _ <- Ns.int.RefsSub1.*(Ref1.int1.RefsSub2.*(Ref2.int2)).get.map(_ ==> List(
            (1, Seq(
              (10, Seq(100, 101)),
              (11, Seq(110, 111)))),
            (2, Seq(
              (20, Seq(200, 201)),
              (21, Seq(210, 211))))
          ))
          _ <- Ref1.int1.a1.get.map(_ ==> List(10, 11, 20, 21))
          _ <- Ref2.int2.a1.get.map(_ ==> List(100, 101, 110, 111, 200, 201, 210, 211))

          _ <- e1.retract

          _ <- Ns.int.RefsSub1.*(Ref1.int1.RefsSub2.*(Ref2.int2)).get.map(_ ==> List(
            (2, Seq(
              (20, Seq(200, 201)),
              (21, Seq(210, 211))))
          ))
          _ <- Ref1.int1.a1.get.map(_ ==> List(20, 21))
          _ <- Ref2.int2.a1.get.map(_ ==> List(200, 201, 210, 211))
        } yield ()
      }
    }

    "Orphan references" - core { implicit conn =>
      for {
        // Create ref
        List(e1, r1) <- Ns.int(1).Ref1.int1(10).save.map(_.eids)
        _ <- r1.retract
        // Ref entity with attribute values is gone - no ref orphan exist
        _ <- Ns.int(1).ref1$.get.map(_.head ==> (1, None))


        // Create another ref
        List(e2, r2) <- Ns.int(2).Ref1.int1(20).save.map(_.eids)

        // Retract attribute value from ref entity - ref entity still exist
        _ <- Ref1(r2).int1().update

        // Ref entity r2 is now an orphan
        // Entity e2 still has a reference to r2
        _ <- Ns.int(2).ref1$.get.map(_.head ==> (2, Some(r2)))
        // r2 has no attribute values
        _ <- Ns.int(2).Ref1.int1$.get.map(_.head ==> (2, None))

        // Add attribute value to ref entity again
        _ <- Ref1(r2).int1(21).update

        // Ref entity is no longer an orphan
        _ <- Ns.int(2).Ref1.e.int1.get.map(_.head ==> (2, r2, 21))
      } yield ()
    }


    "Multiple entities" - core { implicit conn =>
      for {
        List(e1, e2, e3) <- Ns.int insert List(1, 2, 3) map(_.eids)

        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))

        // Retract multiple entities (without tx meta data)
        _ <- retract(Seq(e1, e2))

        _ <- Ns.int.get.map(_ ==> List(3))
      } yield ()
    }
  }
}