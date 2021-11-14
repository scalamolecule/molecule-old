package moleculeTests.tests.core.equality

import java.util.UUID
import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object ApplyUUID extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.uuid$ insert List(
            (1, Some(uuid1)),
            (2, Some(uuid2)),
            (3, Some(uuid3)),
            (4, None)
          )
          // Varargs
          _ <- Ns.uuid.apply(uuid1).get.map(_ ==> List(uuid1))
          _ <- Ns.uuid.apply(uuid2).get.map(_ ==> List(uuid2))
          _ <- Ns.uuid.apply(uuid1, uuid2).get.map(_.sorted ==> List(uuid1, uuid2))

          // `or`
          _ <- Ns.uuid.apply(uuid1 or uuid2).get.map(_.sorted ==> List(uuid1, uuid2))
          _ <- Ns.uuid.apply(uuid1 or uuid2 or uuid3).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))

          // Seq
          _ <- Ns.uuid.apply().get.map(_ ==> Nil)
          _ <- Ns.uuid.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.uuid.apply(List(uuid1)).get.map(_ ==> List(uuid1))
          _ <- Ns.uuid.apply(List(uuid2)).get.map(_ ==> List(uuid2))
          _ <- Ns.uuid.apply(List(uuid1, uuid2)).get.map(_.sorted ==> List(uuid1, uuid2))
          _ <- Ns.uuid.apply(List(uuid1), List(uuid2)).get.map(_.sorted ==> List(uuid1, uuid2))
          _ <- Ns.uuid.apply(List(uuid1, uuid2), List(uuid3)).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
          _ <- Ns.uuid.apply(List(uuid1), List(uuid2, uuid3)).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
          _ <- Ns.uuid.apply(List(uuid1, uuid2, uuid3)).get.map(_.sorted ==> List(uuid1, uuid2, uuid3))
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.uuid$ insert List(
            (1, Some(uuid1)),
            (2, Some(uuid2)),
            (3, Some(uuid3)),
            (4, None)
          )
          // Varargs
          _ <- Ns.int.uuid_.apply(uuid1).get.map(_ ==> List(1))
          _ <- Ns.int.uuid_.apply(uuid2).get.map(_ ==> List(2))
          _ <- Ns.int.uuid_.apply(uuid1, uuid2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.uuid_.apply(uuid1 or uuid2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuid_.apply(uuid1 or uuid2 or uuid3).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.uuid_.apply().get.map(_ ==> List(4))
          _ <- Ns.int.uuid_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.uuid_.apply(List(uuid1)).get.map(_ ==> List(1))
          _ <- Ns.int.uuid_.apply(List(uuid2)).get.map(_ ==> List(2))
          _ <- Ns.int.uuid_.apply(List(uuid1, uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuid_.apply(List(uuid1), List(uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuid_.apply(List(uuid1, uuid2), List(uuid3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.uuid_.apply(List(uuid1), List(uuid2, uuid3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.uuid_.apply(List(uuid1, uuid2, uuid3)).get.map(_ ==> List(1, 2, 3))
        } yield ()
      }
    }


    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.uuids$ insert List(
            (1, Some(Set(uuid1, uuid2))),
            (2, Some(Set(uuid2, uuid3))),
            (3, Some(Set(uuid3, uuid4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.uuids.apply(uuid1).get.map(_ ==> List((1, Set(uuid1, uuid2))))
          _ <- Ns.int.uuids.apply(uuid2).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(uuid1, uuid2).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))

          // `or`
          _ <- Ns.int.uuids.apply(uuid1 or uuid2).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(uuid1 or uuid2 or uuid3).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3)), (3, Set(uuid3, uuid4))))

          // Seq
          _ <- Ns.int.uuids.apply().get.map(_ ==> Nil)
          _ <- Ns.int.uuids.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.int.uuids.apply(List(uuid1)).get.map(_ ==> List((1, Set(uuid1, uuid2))))
          _ <- Ns.int.uuids.apply(List(uuid2)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(List(uuid1, uuid2)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(List(uuid1), List(uuid2)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(List(uuid1, uuid2), List(uuid3)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3)), (3, Set(uuid3, uuid4))))
          _ <- Ns.int.uuids.apply(List(uuid1), List(uuid2, uuid3)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3)), (3, Set(uuid3, uuid4))))
          _ <- Ns.int.uuids.apply(List(uuid1, uuid2, uuid3)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3)), (3, Set(uuid3, uuid4))))


          // AND semantics

          // Set
          _ <- Ns.int.uuids.apply(Set[UUID]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.int.uuids.apply(Set(uuid1)).get.map(_ ==> List((1, Set(uuid1, uuid2))))
          _ <- Ns.int.uuids.apply(Set(uuid2)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2)).get.map(_ ==> List((1, Set(uuid1, uuid2))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid3)).get.map(_ ==> Nil)
          _ <- Ns.int.uuids.apply(Set(uuid2, uuid3)).get.map(_ ==> List((2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2, uuid3)).get.map(_ ==> Nil)

          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2), Set[UUID]()).get.map(_ ==> List((1, Set(uuid1, uuid2))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2), Set(uuid2)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2), Set(uuid3)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3)), (3, Set(uuid3, uuid4))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2), Set(uuid4)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (3, Set(uuid3, uuid4))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2), Set(uuid2), Set(uuid3)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3)), (3, Set(uuid3, uuid4))))

          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2), Set(uuid2, uuid3)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (2, Set(uuid2, uuid3))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2), Set(uuid2, uuid4)).get.map(_ ==> List((1, Set(uuid1, uuid2))))
          _ <- Ns.int.uuids.apply(Set(uuid1, uuid2), Set(uuid3, uuid4)).get.map(_ ==> List((1, Set(uuid1, uuid2)), (3, Set(uuid3, uuid4))))

          // `and`
          _ <- Ns.int.uuids.apply(uuid1 and uuid2).get.map(_ ==> List((1, Set(uuid1, uuid2))))
          _ <- Ns.int.uuids.apply(uuid1 and uuid3).get.map(_ ==> Nil)
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.uuids$ insert List(
            (1, Some(Set(uuid1, uuid2))),
            (2, Some(Set(uuid2, uuid3))),
            (3, Some(Set(uuid3, uuid4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.uuids.apply(uuid1).get.map(_ ==> List(Set(uuid1, uuid2)))
          _ <- Ns.uuids.apply(uuid2).get.map(_ ==> List(Set(uuid1, uuid3, uuid2)))
          _ <- Ns.uuids.apply(uuid1, uuid2).get.map(_ ==> List(Set(uuid1, uuid3, uuid2)))

          // `or`
          _ <- Ns.uuids.apply(uuid1 or uuid2).get.map(_ ==> List(Set(uuid1, uuid3, uuid2)))
          _ <- Ns.uuids.apply(uuid1 or uuid2 or uuid3).get.map(_ ==> List(Set(uuid1, uuid4, uuid3, uuid2)))

          // Seq
          _ <- Ns.uuids.apply().get.map(_ ==> Nil)
          _ <- Ns.uuids.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.uuids.apply(List(uuid1)).get.map(_ ==> List(Set(uuid1, uuid2)))
          _ <- Ns.uuids.apply(List(uuid2)).get.map(_ ==> List(Set(uuid1, uuid3, uuid2)))
          _ <- Ns.uuids.apply(List(uuid1, uuid2)).get.map(_ ==> List(Set(uuid1, uuid3, uuid2)))
          _ <- Ns.uuids.apply(List(uuid1), List(uuid2)).get.map(_ ==> List(Set(uuid1, uuid3, uuid2)))
          _ <- Ns.uuids.apply(List(uuid1, uuid2), List(uuid3)).get.map(_ ==> List(Set(uuid1, uuid4, uuid3, uuid2)))
          _ <- Ns.uuids.apply(List(uuid1), List(uuid2, uuid3)).get.map(_ ==> List(Set(uuid1, uuid4, uuid3, uuid2)))
          _ <- Ns.uuids.apply(List(uuid1, uuid2, uuid3)).get.map(_ ==> List(Set(uuid1, uuid4, uuid3, uuid2)))


          // AND semantics

          // Set
          _ <- Ns.uuids.apply(Set[UUID]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.uuids.apply(Set(uuid1)).get.map(_ ==> List(Set(uuid1, uuid2)))
          _ <- Ns.uuids.apply(Set(uuid2)).get.map(_ ==> List(Set(uuid1, uuid3, uuid2)))
          _ <- Ns.uuids.apply(Set(uuid1, uuid2)).get.map(_ ==> List(Set(uuid1, uuid2)))
          _ <- Ns.uuids.apply(Set(uuid1, uuid3)).get.map(_ ==> Nil)
          _ <- Ns.uuids.apply(Set(uuid2, uuid3)).get.map(_ ==> List(Set(uuid2, uuid3)))
          _ <- Ns.uuids.apply(Set(uuid1, uuid2, uuid3)).get.map(_ ==> Nil)

          _ <- Ns.uuids.apply(Set(uuid1, uuid2), Set(uuid2)).get.map(_ ==> List(Set(uuid1, uuid2, uuid3)))
          _ <- Ns.uuids.apply(Set(uuid1, uuid2), Set(uuid3)).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4)))
          _ <- Ns.uuids.apply(Set(uuid1, uuid2), Set(uuid4)).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4)))
          _ <- Ns.uuids.apply(Set(uuid1, uuid2), Set(uuid2), Set(uuid3)).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4)))

          _ <- Ns.uuids.apply(Set(uuid1, uuid2), Set(uuid2, uuid3)).get.map(_ ==> List(Set(uuid1, uuid2, uuid3)))
          _ <- Ns.uuids.apply(Set(uuid1, uuid2), Set(uuid2, uuid4)).get.map(_ ==> List(Set(uuid1, uuid2)))
          _ <- Ns.uuids.apply(Set(uuid1, uuid2), Set(uuid3, uuid4)).get.map(_ ==> List(Set(uuid1, uuid2, uuid3, uuid4)))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.uuids.apply(uuid1 and uuid2).get.map(_ ==> List(Set(uuid1, uuid2)))
          _ <- Ns.uuids.apply(uuid1 and uuid3).get.map(_ ==> Nil)
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.uuids$ insert List(
            (1, Some(Set(uuid1, uuid2))),
            (2, Some(Set(uuid2, uuid3))),
            (3, Some(Set(uuid3, uuid4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.uuids_.apply(uuid1).get.map(_ ==> List(1))
          _ <- Ns.int.uuids_.apply(uuid2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(uuid1, uuid2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.uuids_.apply(uuid1 or uuid2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(uuid1 or uuid2 or uuid3).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.uuids_.apply().get.map(_ ==> List(4)) // entities with no card-many values asserted
          _ <- Ns.int.uuids_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.uuids_.apply(List(uuid1)).get.map(_ ==> List(1))
          _ <- Ns.int.uuids_.apply(List(uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(List(uuid1, uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(List(uuid1), List(uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(List(uuid1, uuid2), List(uuid3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.uuids_.apply(List(uuid1), List(uuid2, uuid3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.uuids_.apply(List(uuid1, uuid2, uuid3)).get.map(_ ==> List(1, 2, 3))


          // AND semantics

          // Set
          _ <- Ns.int.uuids_.apply(Set[UUID]()).get.map(_ ==> List(4))
          _ <- Ns.int.uuids_.apply(Set(uuid1)).get.map(_ ==> List(1))
          _ <- Ns.int.uuids_.apply(Set(uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2)).get.map(_ ==> List(1))
          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid3)).get.map(_ ==> Nil)
          _ <- Ns.int.uuids_.apply(Set(uuid2, uuid3)).get.map(_ ==> List(2))
          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2, uuid3)).get.map(_ ==> Nil)

          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2), Set(uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2), Set(uuid3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2), Set(uuid4)).get.map(_ ==> List(1, 3))
          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2), Set(uuid2), Set(uuid3)).get.map(_ ==> List(1, 2, 3))

          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2), Set(uuid2, uuid3)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2), Set(uuid2, uuid4)).get.map(_ ==> List(1))
          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2), Set(uuid3, uuid4)).get.map(_ ==> List(1, 3))


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.uuids_.apply(uuid1 and uuid2).get.map(_ ==> List(1))
          _ <- Ns.int.uuids_.apply(uuid1 and uuid3).get.map(_ ==> Nil)
        } yield ()
      }


      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[UUID]()

        val l1 = List(uuid1)
        val l2 = List(uuid2)

        val s1 = Set(uuid1)
        val s2 = Set(uuid2)

        val l12 = List(uuid1, uuid2)

        val s12 = Set(uuid1, uuid2)
        val s23 = Set(uuid2, uuid3)

        for {
          _ <- Ns.int.uuids$ insert List(
            (1, Some(Set(uuid1, uuid2))),
            (2, Some(Set(uuid2, uuid3))),
            (3, Some(Set(uuid3, uuid4))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.uuids_.apply(uuid1, uuid2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.uuids_.apply(uuid1 or uuid2).get.map(_ ==> List(1, 2))

          // Seq
          _ <- Ns.int.uuids_.apply(seq0).get.map(_ ==> List(4))
          _ <- Ns.int.uuids_.apply(List(uuid1), List(uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(l1, l2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(List(uuid1, uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(l12).get.map(_ ==> List(1, 2))


          // AND semantics

          // Set
          _ <- Ns.int.uuids_.apply(set0).get.map(_ ==> List(4))

          _ <- Ns.int.uuids_.apply(Set(uuid1)).get.map(_ ==> List(1))
          _ <- Ns.int.uuids_.apply(s1).get.map(_ ==> List(1))

          _ <- Ns.int.uuids_.apply(Set(uuid2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(s2).get.map(_ ==> List(1, 2))

          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2)).get.map(_ ==> List(1))
          _ <- Ns.int.uuids_.apply(s12).get.map(_ ==> List(1))

          _ <- Ns.int.uuids_.apply(Set(uuid2, uuid3)).get.map(_ ==> List(2))
          _ <- Ns.int.uuids_.apply(s23).get.map(_ ==> List(2))

          _ <- Ns.int.uuids_.apply(Set(uuid1, uuid2), Set(uuid2, uuid3)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.uuids_.apply(s12, s23).get.map(_ ==> List(1, 2))

          // `and`
          _ <- Ns.int.uuids_.apply(uuid1 and uuid2).get.map(_ ==> List(1))
        } yield ()
      }
    }
  }
}