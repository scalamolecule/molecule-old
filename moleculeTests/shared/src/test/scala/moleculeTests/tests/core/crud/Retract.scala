package moleculeTests.tests.core.crud

import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Retract extends AsyncTestSuite {

  lazy val tests = Tests {

    "Implicit entity" - core { implicit conn =>
      for {
        tx <- Ns.int.str insert List(
          (1, "a"),
          (2, "b")
        )
        e1 = tx.eid

        _ <- Ns.int.str.get.map(_.sorted ==> List(
          (1, "a"),
          (2, "b")
        ))

        _ <- e1.map(_.retract)

        _ <- Ns.int.str.get === List(
          (2, "b")
        )
      } yield ()
    }

    "Explicit entities" - core { implicit conn =>
      for {
        tx <- Ns.int.insert(1, 2, 3)
        List(e1, e2, e3) = tx.eids

        _ <- Ns.int.apply(count).get === List(3)

        _ <- retract(Seq(e1, e2))

        _ <- Ns.int(count).get === List(1)
      } yield ()
    }

    "Explicit entities with tx data" - core { implicit conn =>
      for {
        tx <- Ns.int.insert(1, 2, 3)
        List(e1, e2, e3) = tx.eids

        _ <- Ns.int(count).get === List(3)

        _ <- retract(Seq(e1, e2), Ref1.str1("Some tx info"))

        _ <- Ns.int(count).get === List(1)
      } yield ()
    }


    "Component" - {

      "Card-one" - core { implicit conn =>
        for {
          tx <- Ns.int.RefSub1.int1 insert List(
            (1, 10),
            (2, 20)
          )
          e1 = tx.eid

          _ <- Ns.int.RefSub1.int1.get.map(_.sorted ==> List(
            (1, 10),
            (2, 20)
          ))
          _ <- Ref1.int1.get.map(_.sorted ==> List(10, 20))

          _ <- e1.map(_.retract)

          _ <- Ns.int.RefSub1.int1.get === List(
            (2, 20)
          )
          // Card-one sub-entity was retracted
          _ <- Ref1.int1.get === List(20)
        } yield ()
      }

      "Card-many" - core { implicit conn =>
        for {
          tx <- m(Ns.int.RefsSub1 * Ref1.int1).insert(List(
            (1, Seq(10, 11)),
            (2, Seq(20, 21))
          ))
          e1 = tx.eid

          _ <- m(Ns.int.RefsSub1 * Ref1.int1).get.map(_.sortBy(_._1) ==> List(
            (1, Seq(10, 11)),
            (2, Seq(20, 21))
          ))
          _ <- Ref1.int1.get.map(_.sorted ==> List(10, 11, 20, 21))

          _ <- e1.map(_.retract)

          _ <- m(Ns.int.RefsSub1 * Ref1.int1).get === List(
            (2, Seq(20, 21))
          )
          // Card-many sub-entitities were retracted
          _ <- Ref1.int1.get === List(20, 21)
        } yield ()
      }

      "Nested" - core { implicit conn =>
        for {
          tx <- Ns.int.RefsSub1.*(Ref1.int1.RefsSub2.*(Ref2.int2)).insert(List(
            (1, Seq(
              (10, Seq(100, 101)),
              (11, Seq(110, 111)))),
            (2, Seq(
              (20, Seq(200, 201)),
              (21, Seq(210, 211))))
          ))
          e1 = tx.eid

          _ <- Ns.int.RefsSub1.*(Ref1.int1.RefsSub2.*(Ref2.int2)).get === List(
            (1, Seq(
              (10, Seq(100, 101)),
              (11, Seq(110, 111)))),
            (2, Seq(
              (20, Seq(200, 201)),
              (21, Seq(210, 211))))
          )
          _ <- Ref1.int1.get.map(_.sorted ==> List(10, 11, 20, 21))
          _ <- Ref2.int2.get.map(_.sorted ==> List(100, 101, 110, 111, 200, 201, 210, 211))

          _ <- e1.map(_.retract)

          _ <- Ns.int.RefsSub1.*(Ref1.int1.RefsSub2.*(Ref2.int2)).get === List(
            (2, Seq(
              (20, Seq(200, 201)),
              (21, Seq(210, 211))))
          )
          _ <- Ref1.int1.get.map(_.sorted ==> List(20, 21))
          _ <- Ref2.int2.get.map(_.sorted ==> List(200, 201, 210, 211))
        } yield ()
      }
    }

    "Orphan references" - core { implicit conn =>
      for {
        // Create ref
        tx <- Ns.int(1).Ref1.int1(10).save
        List(e1, r1) = tx.eids
        _ <- r1.map(_.retract)
        // Ref entity with attribute values is gone - no ref orphan exist
        _ <- Ns.int(1).ref1$.get.map(_.head ==> (1, None))


        // Create another ref
        tx2 <- Ns.int(2).Ref1.int1(20).save
        List(e2, r2) = tx.eids

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
  }
}