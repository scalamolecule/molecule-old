package moleculeTests.tests.core.equality

import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object ApplyInt extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.str.int$ insert List(
            ("a", Some(1)),
            ("b", Some(2)),
            ("c", Some(3)),
            ("d", None)
          )

          // Varargs
          _ <- Ns.int.apply(1).get === List(1)
          _ <- Ns.int.apply(2).get === List(2)
          _ <- Ns.int.apply(1, 2).get === List(1, 2)

          // `or`
          _ <- Ns.int.apply(1 or 2).get === List(1, 2)
          _ <- Ns.int.apply(1 or 2 or 3).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.apply().get === Nil
          _ <- Ns.int.apply(Nil).get === Nil
          _ <- Ns.int.apply(List(1)).get === List(1)
          _ <- Ns.int.apply(List(2)).get === List(2)
          _ <- Ns.int.apply(List(1, 2)).get === List(1, 2)
          _ <- Ns.int.apply(List(1), List(2)).get === List(1, 2)
          _ <- Ns.int.apply(List(1, 2), List(3)).get === List(1, 2, 3)
          _ <- Ns.int.apply(List(1), List(2, 3)).get === List(1, 2, 3)
          _ <- Ns.int.apply(List(1, 2, 3)).get === List(1, 2, 3)
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.str.int$ insert List(
            ("a", Some(1)),
            ("b", Some(2)),
            ("c", Some(3)),
            ("d", None)
          )

          // Varargs
          _ <- Ns.str.int_.apply(1).get === List("a")
          _ <- Ns.str.int_.apply(2).get === List("b")
          _ <- Ns.str.int_.apply(1, 2).get === List("a", "b")

          // `or`
          _ <- Ns.str.int_.apply(1 or 2).get === List("a", "b")
          _ <- Ns.str.int_.apply(1 or 2 or 3).get === List("a", "b", "c")

          // Seq
          _ <- Ns.str.int_.apply().get === List("d")
          _ <- Ns.str.int_.apply(Nil).get === List("d")
          _ <- Ns.str.int_.apply(List(1)).get === List("a")
          _ <- Ns.str.int_.apply(List(2)).get === List("b")
          _ <- Ns.str.int_.apply(List(1, 2)).get === List("a", "b")
          _ <- Ns.str.int_.apply(List(1), List(2)).get === List("a", "b")
          _ <- Ns.str.int_.apply(List(1, 2), List(3)).get === List("a", "b", "c")
          _ <- Ns.str.int_.apply(List(1), List(2, 3)).get === List("a", "b", "c")
          _ <- Ns.str.int_.apply(List(1, 2, 3)).get === List("a", "b", "c")
        } yield ()
      }
    }

    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.ints$ insert List(
            (1, Some(Set(1, 2))),
            (2, Some(Set(2, 3))),
            (3, Some(Set(3, 4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.ints.apply(1).get === List((1, Set(1, 2)))
          _ <- Ns.int.ints.apply(2).get === List((1, Set(1, 2)), (2, Set(2, 3)))
          _ <- Ns.int.ints.apply(1, 2).get === List((1, Set(1, 2)), (2, Set(2, 3)))

          // `or`
          _ <- Ns.int.ints.apply(1 or 2).get === List((1, Set(1, 2)), (2, Set(2, 3)))
          _ <- Ns.int.ints.apply(1 or 2 or 3).get === List((1, Set(1, 2)), (2, Set(2, 3)), (3, Set(3, 4)))

          // Seq
          _ <- Ns.int.ints.apply().get === Nil
          _ <- Ns.int.ints.apply(Nil).get === Nil
          _ <- Ns.int.ints.apply(List(1)).get === List((1, Set(1, 2)))
          _ <- Ns.int.ints.apply(List(2)).get === List((1, Set(1, 2)), (2, Set(2, 3)))
          _ <- Ns.int.ints.apply(List(1, 2)).get === List((1, Set(1, 2)), (2, Set(2, 3)))
          _ <- Ns.int.ints.apply(List(1), List(2)).get === List((1, Set(1, 2)), (2, Set(2, 3)))
          _ <- Ns.int.ints.apply(List(1, 2), List(3)).get === List((1, Set(1, 2)), (2, Set(2, 3)), (3, Set(3, 4)))
          _ <- Ns.int.ints.apply(List(1), List(2, 3)).get === List((1, Set(1, 2)), (2, Set(2, 3)), (3, Set(3, 4)))
          _ <- Ns.int.ints.apply(List(1, 2, 3)).get === List((1, Set(1, 2)), (2, Set(2, 3)), (3, Set(3, 4)))


          // AND semantics

          // Set
          _ <- Ns.int.ints.apply(Set[Int]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.int.ints.apply(Set(1)).get === List((1, Set(1, 2)))
          _ <- Ns.int.ints.apply(Set(2)).get === List((1, Set(1, 2)), (2, Set(2, 3)))
          _ <- Ns.int.ints.apply(Set(1, 2)).get === List((1, Set(1, 2)))
          _ <- Ns.int.ints.apply(Set(1, 3)).get === Nil
          _ <- Ns.int.ints.apply(Set(2, 3)).get === List((2, Set(2, 3)))
          _ <- Ns.int.ints.apply(Set(1, 2, 3)).get === Nil

          _ <- Ns.int.ints.apply(Set(1, 2), Set[Int]()).get === List((1, Set(1, 2)))
          _ <- Ns.int.ints.apply(Set(1, 2), Set(2)).get === List((1, Set(1, 2)), (2, Set(2, 3)))
          _ <- Ns.int.ints.apply(Set(1, 2), Set(3)).get === List((1, Set(1, 2)), (2, Set(2, 3)), (3, Set(3, 4)))
          _ <- Ns.int.ints.apply(Set(1, 2), Set(4)).get === List((1, Set(1, 2)), (3, Set(3, 4)))
          _ <- Ns.int.ints.apply(Set(1, 2), Set(2), Set(3)).get === List((1, Set(1, 2)), (2, Set(2, 3)), (3, Set(3, 4)))

          _ <- Ns.int.ints.apply(Set(1, 2), Set(2, 3)).get === List((1, Set(1, 2)), (2, Set(2, 3)))
          _ <- Ns.int.ints.apply(Set(1, 2), Set(2, 4)).get === List((1, Set(1, 2)))
          _ <- Ns.int.ints.apply(Set(1, 2), Set(3, 4)).get === List((1, Set(1, 2)), (3, Set(3, 4)))

          // `and`
          _ <- Ns.int.ints.apply(1 and 2).get === List((1, Set(1, 2)))
          _ <- Ns.int.ints.apply(1 and 3).get === Nil
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.ints$ insert List(
            (1, Some(Set(1, 2))),
            (2, Some(Set(2, 3))),
            (3, Some(Set(3, 4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.ints.apply(1).get === List(Set(1, 2))
          _ <- Ns.ints.apply(2).get === List(Set(1, 3, 2))
          _ <- Ns.ints.apply(1, 2).get === List(Set(1, 3, 2))

          // `or`
          _ <- Ns.ints.apply(1 or 2).get === List(Set(1, 3, 2))
          _ <- Ns.ints.apply(1 or 2 or 3).get === List(Set(1, 4, 3, 2))

          // Seq
          _ <- Ns.ints.apply().get === Nil
          _ <- Ns.ints.apply(Nil).get === Nil
          _ <- Ns.ints.apply(List(1)).get === List(Set(1, 2))
          _ <- Ns.ints.apply(List(2)).get === List(Set(1, 3, 2))
          _ <- Ns.ints.apply(List(1, 2)).get === List(Set(1, 3, 2))
          _ <- Ns.ints.apply(List(1), List(2)).get === List(Set(1, 3, 2))
          _ <- Ns.ints.apply(List(1, 2), List(3)).get === List(Set(1, 4, 3, 2))
          _ <- Ns.ints.apply(List(1), List(2, 3)).get === List(Set(1, 4, 3, 2))
          _ <- Ns.ints.apply(List(1, 2, 3)).get === List(Set(1, 4, 3, 2))


          // AND semantics

          // Set
          _ <- Ns.ints.apply(Set[Int]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.ints.apply(Set(1)).get === List(Set(1, 2))
          _ <- Ns.ints.apply(Set(2)).get === List(Set(1, 3, 2))
          _ <- Ns.ints.apply(Set(1, 2)).get === List(Set(1, 2))
          _ <- Ns.ints.apply(Set(1, 3)).get === Nil
          _ <- Ns.ints.apply(Set(2, 3)).get === List(Set(2, 3))
          _ <- Ns.ints.apply(Set(1, 2, 3)).get === Nil

          _ <- Ns.ints.apply(Set(1, 2), Set(2)).get === List(Set(1, 2, 3))
          _ <- Ns.ints.apply(Set(1, 2), Set(3)).get === List(Set(1, 2, 3, 4))
          _ <- Ns.ints.apply(Set(1, 2), Set(4)).get === List(Set(1, 2, 3, 4))
          _ <- Ns.ints.apply(Set(1, 2), Set(2), Set(3)).get === List(Set(1, 2, 3, 4))

          _ <- Ns.ints.apply(Set(1, 2), Set(2, 3)).get === List(Set(1, 2, 3))
          _ <- Ns.ints.apply(Set(1, 2), Set(2, 4)).get === List(Set(1, 2))
          _ <- Ns.ints.apply(Set(1, 2), Set(3, 4)).get === List(Set(1, 2, 3, 4))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.ints.apply(1 and 2).get === List(Set(1, 2))
          _ <- Ns.ints.apply(1 and 3).get === Nil
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.ints$ insert List(
            (1, Some(Set(1, 2))),
            (2, Some(Set(2, 3))),
            (3, Some(Set(3, 4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.ints_.apply(1).get === List(1)
          _ <- Ns.int.ints_.apply(2).get === List(1, 2)
          _ <- Ns.int.ints_.apply(1, 2).get === List(1, 2)

          // `or`
          _ <- Ns.int.ints_.apply(1 or 2).get === List(1, 2)
          _ <- Ns.int.ints_.apply(1 or 2 or 3).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.ints_.apply().get === List(4) // entities with no card-many values asserted
          _ <- Ns.int.ints_.apply(Nil).get === List(4)
          _ <- Ns.int.ints_.apply(List(1)).get === List(1)
          _ <- Ns.int.ints_.apply(List(2)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(List(1, 2)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(List(1), List(2)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(List(1, 2), List(3)).get === List(1, 2, 3)
          _ <- Ns.int.ints_.apply(List(1), List(2, 3)).get === List(1, 2, 3)
          _ <- Ns.int.ints_.apply(List(1, 2, 3)).get === List(1, 2, 3)


          // AND semantics

          // Set
          _ <- Ns.int.ints_.apply(Set[Int]()).get === List(4)
          _ <- Ns.int.ints_.apply(Set(1)).get === List(1)
          _ <- Ns.int.ints_.apply(Set(2)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(Set(1, 2)).get === List(1)
          _ <- Ns.int.ints_.apply(Set(1, 3)).get === Nil
          _ <- Ns.int.ints_.apply(Set(2, 3)).get === List(2)
          _ <- Ns.int.ints_.apply(Set(1, 2, 3)).get === Nil

          _ <- Ns.int.ints_.apply(Set(1, 2), Set(2)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(Set(1, 2), Set(3)).get === List(1, 2, 3)
          _ <- Ns.int.ints_.apply(Set(1, 2), Set(4)).get === List(1, 3)
          _ <- Ns.int.ints_.apply(Set(1, 2), Set(2), Set(3)).get === List(1, 2, 3)

          _ <- Ns.int.ints_.apply(Set(1, 2), Set(2, 3)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(Set(1, 2), Set(2, 4)).get === List(1)
          _ <- Ns.int.ints_.apply(Set(1, 2), Set(3, 4)).get === List(1, 3)


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.ints_.apply(1 and 2).get === List(1)
          _ <- Ns.int.ints_.apply(1 and 3).get === Nil
        } yield ()
      }

      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[Int]()

        val l1 = List(1)
        val l2 = List(2)

        val s1 = Set(1)
        val s2 = Set(2)

        val l12 = List(1, 2)
        val l23 = List(2, 3)

        val s12 = Set(1, 2)
        val s23 = Set(2, 3)

        for {
          _ <- Ns.int.ints$ insert List(
            (1, Some(Set(1, 2))),
            (2, Some(Set(2, 3))),
            (3, Some(Set(3, 4))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.ints_.apply(int1, int2).get === List(1, 2)

          // `or`
          _ <- Ns.int.ints_.apply(int1 or int2).get === List(1, 2)

          // Seq
          _ <- Ns.int.ints_.apply(seq0).get === List(4)
          _ <- Ns.int.ints_.apply(List(int1), List(int2)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(l1, l2).get === List(1, 2)
          _ <- Ns.int.ints_.apply(List(int1, int2)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(l12).get === List(1, 2)


          // AND semantics

          // Set
          _ <- Ns.int.ints_.apply(set0).get === List(4)

          _ <- Ns.int.ints_.apply(Set(int1)).get === List(1)
          _ <- Ns.int.ints_.apply(s1).get === List(1)

          _ <- Ns.int.ints_.apply(Set(int2)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(s2).get === List(1, 2)

          _ <- Ns.int.ints_.apply(Set(int1, int2)).get === List(1)
          _ <- Ns.int.ints_.apply(s12).get === List(1)

          _ <- Ns.int.ints_.apply(Set(int2, int3)).get === List(2)
          _ <- Ns.int.ints_.apply(s23).get === List(2)

          _ <- Ns.int.ints_.apply(Set(int1, int2), Set(int2, int3)).get === List(1, 2)
          _ <- Ns.int.ints_.apply(s12, s23).get === List(1, 2)

          // `and`
          _ <- Ns.int.ints_.apply(int1 and int2).get === List(1)
        } yield ()
      }
    }
  }
}