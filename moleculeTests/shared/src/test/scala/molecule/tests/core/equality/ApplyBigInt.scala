package molecule.tests.core.equality

import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out4._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object ApplyBigInt extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.bigInt$ insert List(
            (1, Some(bigInt1)),
            (2, Some(bigInt2)),
            (3, Some(bigInt3)),
            (4, None)
          )

          // Varargs
          _ <- Ns.bigInt.apply(bigInt1).get === List(bigInt1)
          _ <- Ns.bigInt.apply(bigInt2).get === List(bigInt2)
          _ <- Ns.bigInt.apply(bigInt1, bigInt2).get === List(bigInt1, bigInt2)

          // `or`
          _ <- Ns.bigInt.apply(bigInt1 or bigInt2).get === List(bigInt1, bigInt2)
          _ <- Ns.bigInt.apply(bigInt1 or bigInt2 or bigInt3).get === List(bigInt1, bigInt2, bigInt3)

          // Seq
          _ <- Ns.bigInt.apply().get === Nil
          _ <- Ns.bigInt.apply(Nil).get === Nil
          _ <- Ns.bigInt.apply(List(bigInt1)).get === List(bigInt1)
          _ <- Ns.bigInt.apply(List(bigInt2)).get === List(bigInt2)
          _ <- Ns.bigInt.apply(List(bigInt1, bigInt2)).get === List(bigInt1, bigInt2)
          _ <- Ns.bigInt.apply(List(bigInt1), List(bigInt2)).get === List(bigInt1, bigInt2)
          _ <- Ns.bigInt.apply(List(bigInt1, bigInt2), List(bigInt3)).get === List(bigInt1, bigInt2, bigInt3)
          _ <- Ns.bigInt.apply(List(bigInt1), List(bigInt2, bigInt3)).get === List(bigInt1, bigInt2, bigInt3)
          _ <- Ns.bigInt.apply(List(bigInt1, bigInt2, bigInt3)).get === List(bigInt1, bigInt2, bigInt3)
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.bigInt$ insert List(
            (1, Some(bigInt1)),
            (2, Some(bigInt2)),
            (3, Some(bigInt3)),
            (4, None)
          )

          // Varargs
          _ <- Ns.int.bigInt_.apply(bigInt1).get === List(1)
          _ <- Ns.int.bigInt_.apply(bigInt2).get === List(2)
          _ <- Ns.int.bigInt_.apply(bigInt1, bigInt2).get === List(1, 2)

          // `or`
          _ <- Ns.int.bigInt_.apply(bigInt1 or bigInt2).get === List(1, 2)
          _ <- Ns.int.bigInt_.apply(bigInt1 or bigInt2 or bigInt3).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.bigInt_.apply().get === List(4)
          _ <- Ns.int.bigInt_.apply(Nil).get === List(4)
          _ <- Ns.int.bigInt_.apply(List(bigInt1)).get === List(1)
          _ <- Ns.int.bigInt_.apply(List(bigInt2)).get === List(2)
          _ <- Ns.int.bigInt_.apply(List(bigInt1, bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInt_.apply(List(bigInt1), List(bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInt_.apply(List(bigInt1, bigInt2), List(bigInt3)).get === List(1, 2, 3)
          _ <- Ns.int.bigInt_.apply(List(bigInt1), List(bigInt2, bigInt3)).get === List(1, 2, 3)
          _ <- Ns.int.bigInt_.apply(List(bigInt1, bigInt2, bigInt3)).get === List(1, 2, 3)
        } yield ()
      }
    }

    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.bigInts$ insert List(
            (1, Some(Set(bigInt1, bigInt2))),
            (2, Some(Set(bigInt2, bigInt3))),
            (3, Some(Set(bigInt3, bigInt4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.bigInts.apply(bigInt1).get === List((1, Set(bigInt1, bigInt2)))
          _ <- Ns.int.bigInts.apply(bigInt2).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(bigInt1, bigInt2).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))

          // `or`
          _ <- Ns.int.bigInts.apply(bigInt1 or bigInt2).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(bigInt1 or bigInt2 or bigInt3).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)), (3, Set(bigInt3, bigInt4)))

          // Seq
          _ <- Ns.int.bigInts.apply().get === Nil
          _ <- Ns.int.bigInts.apply(Nil).get === Nil
          _ <- Ns.int.bigInts.apply(List(bigInt1)).get === List((1, Set(bigInt1, bigInt2)))
          _ <- Ns.int.bigInts.apply(List(bigInt2)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(List(bigInt1, bigInt2)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(List(bigInt1), List(bigInt2)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(List(bigInt1, bigInt2), List(bigInt3)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)), (3, Set(bigInt3, bigInt4)))
          _ <- Ns.int.bigInts.apply(List(bigInt1), List(bigInt2, bigInt3)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)), (3, Set(bigInt3, bigInt4)))
          _ <- Ns.int.bigInts.apply(List(bigInt1, bigInt2, bigInt3)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)), (3, Set(bigInt3, bigInt4)))


          // AND semantics

          // Set
          _ <- Ns.int.bigInts.apply(Set[BigInt]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.int.bigInts.apply(Set(bigInt1)).get === List((1, Set(bigInt1, bigInt2)))
          _ <- Ns.int.bigInts.apply(Set(bigInt2)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2)).get === List((1, Set(bigInt1, bigInt2)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt3)).get === Nil
          _ <- Ns.int.bigInts.apply(Set(bigInt2, bigInt3)).get === List((2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2, bigInt3)).get === Nil

          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2), Set[BigInt]()).get === List((1, Set(bigInt1, bigInt2)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt2)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt3)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)), (3, Set(bigInt3, bigInt4)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt4)).get === List((1, Set(bigInt1, bigInt2)), (3, Set(bigInt3, bigInt4)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt2), Set(bigInt3)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)), (3, Set(bigInt3, bigInt4)))

          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get === List((1, Set(bigInt1, bigInt2)), (2, Set(bigInt2, bigInt3)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt2, bigInt4)).get === List((1, Set(bigInt1, bigInt2)))
          _ <- Ns.int.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4)).get === List((1, Set(bigInt1, bigInt2)), (3, Set(bigInt3, bigInt4)))

          // `and`
          _ <- Ns.int.bigInts.apply(bigInt1 and bigInt2).get === List((1, Set(bigInt1, bigInt2)))
          _ <- Ns.int.bigInts.apply(bigInt1 and bigInt3).get === Nil
        } yield ()
      }

      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.bigInts$ insert List(
            (1, Some(Set(bigInt1, bigInt2))),
            (2, Some(Set(bigInt2, bigInt3))),
            (3, Some(Set(bigInt3, bigInt4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.bigInts.apply(bigInt1).get === List(Set(bigInt1, bigInt2))
          _ <- Ns.bigInts.apply(bigInt2).get === List(Set(bigInt1, bigInt3, bigInt2))
          _ <- Ns.bigInts.apply(bigInt1, bigInt2).get === List(Set(bigInt1, bigInt3, bigInt2))

          // `or`
          _ <- Ns.bigInts.apply(bigInt1 or bigInt2).get === List(Set(bigInt1, bigInt3, bigInt2))
          _ <- Ns.bigInts.apply(bigInt1 or bigInt2 or bigInt3).get === List(Set(bigInt1, bigInt4, bigInt3, bigInt2))

          // Seq
          _ <- Ns.bigInts.apply().get === Nil
          _ <- Ns.bigInts.apply(Nil).get === Nil
          _ <- Ns.bigInts.apply(List(bigInt1)).get === List(Set(bigInt1, bigInt2))
          _ <- Ns.bigInts.apply(List(bigInt2)).get === List(Set(bigInt1, bigInt3, bigInt2))
          _ <- Ns.bigInts.apply(List(bigInt1, bigInt2)).get === List(Set(bigInt1, bigInt3, bigInt2))
          _ <- Ns.bigInts.apply(List(bigInt1), List(bigInt2)).get === List(Set(bigInt1, bigInt3, bigInt2))
          _ <- Ns.bigInts.apply(List(bigInt1, bigInt2), List(bigInt3)).get === List(Set(bigInt1, bigInt4, bigInt3, bigInt2))
          _ <- Ns.bigInts.apply(List(bigInt1), List(bigInt2, bigInt3)).get === List(Set(bigInt1, bigInt4, bigInt3, bigInt2))
          _ <- Ns.bigInts.apply(List(bigInt1, bigInt2, bigInt3)).get === List(Set(bigInt1, bigInt4, bigInt3, bigInt2))


          // AND semantics

          // Set
          _ <- Ns.bigInts.apply(Set[BigInt]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.bigInts.apply(Set(bigInt1)).get === List(Set(bigInt1, bigInt2))
          _ <- Ns.bigInts.apply(Set(bigInt2)).get === List(Set(bigInt1, bigInt3, bigInt2))
          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2)).get === List(Set(bigInt1, bigInt2))
          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt3)).get === Nil
          _ <- Ns.bigInts.apply(Set(bigInt2, bigInt3)).get === List(Set(bigInt2, bigInt3))
          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2, bigInt3)).get === Nil

          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt2)).get === List(Set(bigInt1, bigInt2, bigInt3))
          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt3)).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4))
          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt4)).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4))
          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt2), Set(bigInt3)).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4))

          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get === List(Set(bigInt1, bigInt2, bigInt3))
          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt2, bigInt4)).get === List(Set(bigInt1, bigInt2))
          _ <- Ns.bigInts.apply(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4)).get === List(Set(bigInt1, bigInt2, bigInt3, bigInt4))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.bigInts.apply(bigInt1 and bigInt2).get === List(Set(bigInt1, bigInt2))
          _ <- Ns.bigInts.apply(bigInt1 and bigInt3).get === Nil
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.bigInts$ insert List(
            (1, Some(Set(bigInt1, bigInt2))),
            (2, Some(Set(bigInt2, bigInt3))),
            (3, Some(Set(bigInt3, bigInt4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.bigInts_.apply(bigInt1).get === List(1)
          _ <- Ns.int.bigInts_.apply(bigInt2).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(bigInt1, bigInt2).get === List(1, 2)

          // `or`
          _ <- Ns.int.bigInts_.apply(bigInt1 or bigInt2).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(bigInt1 or bigInt2 or bigInt3).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.bigInts_.apply().get === List(4) // entities with no card-many values asserted
          _ <- Ns.int.bigInts_.apply(Nil).get === List(4)
          _ <- Ns.int.bigInts_.apply(List(bigInt1)).get === List(1)
          _ <- Ns.int.bigInts_.apply(List(bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(List(bigInt1, bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(List(bigInt1), List(bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(List(bigInt1, bigInt2), List(bigInt3)).get === List(1, 2, 3)
          _ <- Ns.int.bigInts_.apply(List(bigInt1), List(bigInt2, bigInt3)).get === List(1, 2, 3)
          _ <- Ns.int.bigInts_.apply(List(bigInt1, bigInt2, bigInt3)).get === List(1, 2, 3)


          // AND semantics

          // Set
          _ <- Ns.int.bigInts_.apply(Set[BigInt]()).get === List(4)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1)).get === List(1)
          _ <- Ns.int.bigInts_.apply(Set(bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2)).get === List(1)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt3)).get === Nil
          _ <- Ns.int.bigInts_.apply(Set(bigInt2, bigInt3)).get === List(2)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2, bigInt3)).get === Nil

          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2), Set(bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2), Set(bigInt3)).get === List(1, 2, 3)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2), Set(bigInt4)).get === List(1, 3)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2), Set(bigInt2), Set(bigInt3)).get === List(1, 2, 3)

          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2), Set(bigInt2, bigInt4)).get === List(1)
          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2), Set(bigInt3, bigInt4)).get === List(1, 3)


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.bigInts_.apply(bigInt1 and bigInt2).get === List(1)
          _ <- Ns.int.bigInts_.apply(bigInt1 and bigInt3).get === Nil
        } yield ()
      }

      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[BigInt]()

        val l1 = List(bigInt1)
        val l2 = List(bigInt2)

        val s1 = Set(bigInt1)
        val s2 = Set(bigInt2)

        val l12 = List(bigInt1, bigInt2)
        val l23 = List(bigInt2, bigInt3)

        val s12 = Set(bigInt1, bigInt2)
        val s23 = Set(bigInt2, bigInt3)

        for {
          _ <- Ns.int.bigInts$ insert List(
            (1, Some(Set(bigInt1, bigInt2))),
            (2, Some(Set(bigInt2, bigInt3))),
            (3, Some(Set(bigInt3, bigInt4))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.bigInts_.apply(bigInt1, bigInt2).get === List(1, 2)

          // `or`
          _ <- Ns.int.bigInts_.apply(bigInt1 or bigInt2).get === List(1, 2)

          // Seq
          _ <- Ns.int.bigInts_.apply(seq0).get === List(4)
          _ <- Ns.int.bigInts_.apply(List(bigInt1), List(bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(l1, l2).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(List(bigInt1, bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(l12).get === List(1, 2)


          // AND semantics

          // Set
          _ <- Ns.int.bigInts_.apply(set0).get === List(4)

          _ <- Ns.int.bigInts_.apply(Set(bigInt1)).get === List(1)
          _ <- Ns.int.bigInts_.apply(s1).get === List(1)

          _ <- Ns.int.bigInts_.apply(Set(bigInt2)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(s2).get === List(1, 2)

          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2)).get === List(1)
          _ <- Ns.int.bigInts_.apply(s12).get === List(1)

          _ <- Ns.int.bigInts_.apply(Set(bigInt2, bigInt3)).get === List(2)
          _ <- Ns.int.bigInts_.apply(s23).get === List(2)

          _ <- Ns.int.bigInts_.apply(Set(bigInt1, bigInt2), Set(bigInt2, bigInt3)).get === List(1, 2)
          _ <- Ns.int.bigInts_.apply(s12, s23).get === List(1, 2)

          // `and`
          _ <- Ns.int.bigInts_.apply(bigInt1 and bigInt2).get === List(1)
        } yield ()
      }
    }
  }
}