package moleculeTests.tests.core.equality

import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object ApplyBoolean extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.bool$ insert List(
            (1, Some(true)),
            (2, Some(false)),
            (3, None)
          )
          // Varargs
          _ <- Ns.bool.apply(true).get === List(true)
          _ <- Ns.bool.apply(false).get === List(false)
          _ <- Ns.bool.apply(true, false).get === List(false, true)

          // `or`
          _ <- Ns.bool.apply(true or false).get === List(false, true)

          // Seq
          _ <- Ns.bool.apply().get === Nil
          _ <- Ns.bool.apply(Nil).get === Nil
          _ <- Ns.bool.apply(List(true)).get === List(true)
          _ <- Ns.bool.apply(List(false)).get === List(false)
          _ <- Ns.bool.apply(List(true, false)).get === List(false, true)
          _ <- Ns.bool.apply(List(true), List(false)).get === List(false, true)
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.bool$ insert List(
            (1, Some(true)),
            (2, Some(false)),
            (3, None)
          )

          // Varargs
          _ <- Ns.int.bool_.apply(true).get === List(1)
          _ <- Ns.int.bool_.apply(false).get === List(2)
          _ <- Ns.int.bool_.apply(true, false).get === List(1, 2)

          // `or`
          _ <- Ns.int.bool_.apply(true or false).get === List(1, 2)

          // Seq
          _ <- Ns.int.bool_.apply(List(true)).get === List(1)
          _ <- Ns.int.bool_.apply(List(false)).get === List(2)
          _ <- Ns.int.bool_.apply(List(true, false)).get === List(1, 2)
          _ <- Ns.int.bool_.apply(List(true), List(false)).get === List(1, 2)
          _ <- Ns.int.bool_.apply(List(true, false), List(true)).get === List(1, 2)
          _ <- Ns.int.bool_.apply(List(true), List(false, true)).get === List(1, 2)
        } yield ()
      }
    }

    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.bools$ insert List(
            (1, Some(Set(true))),
            (2, Some(Set(false))),
            (3, Some(Set(false, true))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.bools.apply(true).get === List((1, Set(true)), (3, Set(true, false)))
          _ <- Ns.int.bools.apply(false).get === List((2, Set(false)), (3, Set(true, false)))
          _ <- Ns.int.bools.apply(true, false).get === List((1, Set(true)), (2, Set(false)), (3, Set(true, false)))

          // `or`
          _ <- Ns.int.bools.apply(true or false).get === List((1, Set(true)), (2, Set(false)), (3, Set(true, false)))

          // Seq
          _ <- Ns.int.bools.apply().get === Nil
          _ <- Ns.int.bools.apply(Nil).get === Nil
          _ <- Ns.int.bools.apply(List(true)).get === List((1, Set(true)), (3, Set(true, false)))
          _ <- Ns.int.bools.apply(List(false)).get === List((2, Set(false)), (3, Set(true, false)))
          _ <- Ns.int.bools.apply(List(true, false)).get === List((1, Set(true)), (2, Set(false)), (3, Set(true, false)))
          _ <- Ns.int.bools.apply(List(true), List(false)).get === List((1, Set(true)), (2, Set(false)), (3, Set(true, false)))


          // AND semantics

          // Set
          _ <- Ns.int.bools.apply(Set[Boolean]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.int.bools.apply(Set(true)).get === List((1, Set(true)), (3, Set(true, false)))
          _ <- Ns.int.bools.apply(Set(false)).get === List((2, Set(false)), (3, Set(true, false)))
          _ <- Ns.int.bools.apply(Set(true, false)).get === List((3, Set(true, false)))

          _ <- Ns.int.bools.apply(Set(true, false), Set[Boolean]()).get === List((3, Set(true, false)))
          _ <- Ns.int.bools.apply(Set(true, false), Set(false)).get === List((2, Set(false)), (3, Set(true, false)))

          // `and`
          _ <- Ns.int.bools.apply(true and false).get === List((3, Set(true, false)))
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          // OR semantics

          // Varargs
          _ <- Ns.bools.apply(true).get === List(Set(true, false))
          _ <- Ns.bools.apply(false).get === List(Set(true, false))
          _ <- Ns.bools.apply(true, false).get === List(Set(true, false))

          // `or`
          _ <- Ns.bools.apply(true or false).get === List(Set(true, false))

          // Seq
          _ <- Ns.bools.apply().get === Nil
          _ <- Ns.bools.apply(Nil).get === Nil
          _ <- Ns.bools.apply(List(true)).get === List(Set(true, false))
          _ <- Ns.bools.apply(List(false)).get === List(Set(true, false))
          _ <- Ns.bools.apply(List(true, false)).get === List(Set(true, false))
          _ <- Ns.bools.apply(List(true), List(false)).get === List(Set(true, false))
          _ <- Ns.bools.apply(List(true, false), List(true)).get === List(Set(true, false))
          _ <- Ns.bools.apply(List(true), List(false, true)).get === List(Set(true, false))


          // AND semantics

          // Set
          _ <- Ns.bools.apply(Set[Boolean]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.bools.apply(Set(true)).get === List(Set(true, false))
          _ <- Ns.bools.apply(Set(false)).get === List(Set(true, false))
          _ <- Ns.bools.apply(Set(true, false)).get === List(Set(true, false))

          _ <- Ns.bools.apply(Set(true, false), Set(false)).get === List(Set(true, false))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.bools.apply(true and false).get === List(Set(true, false))
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.bools$ insert List(
            (1, Some(Set(true))),
            (2, Some(Set(false))),
            (3, Some(Set(false, true))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.bools_.apply(true).get === List(1, 3)
          _ <- Ns.int.bools_.apply(false).get === List(2, 3)
          _ <- Ns.int.bools_.apply(true, false).get === List(1, 2, 3)

          // `or`
          _ <- Ns.int.bools_.apply(true or false).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.bools_.apply().get === List(4) // entities with no card-many values asserted
          _ <- Ns.int.bools_.apply(Nil).get === List(4)
          _ <- Ns.int.bools_.apply(List(true)).get === List(1, 3)
          _ <- Ns.int.bools_.apply(List(false)).get === List(2, 3)
          _ <- Ns.int.bools_.apply(List(true, false)).get === List(1, 2, 3)
          _ <- Ns.int.bools_.apply(List(true), List(false)).get === List(1, 2, 3)


          // AND semantics

          // Set
          _ <- Ns.int.bools_.apply(Set[Boolean]()).get === List(4)
          _ <- Ns.int.bools_.apply(Set(true)).get === List(1, 3)
          _ <- Ns.int.bools_.apply(Set(false)).get === List(2, 3)
          _ <- Ns.int.bools_.apply(Set(true, false)).get === List(3)

          _ <- Ns.int.bools_.apply(Set(true, false), Set(true)).get === List(1, 3)
          _ <- Ns.int.bools_.apply(Set(true, false), Set(false)).get === List(2, 3)


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.bools_.apply(true and false).get === List(3)
        } yield ()
      }


      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[Boolean]()

        val l1 = List(true)
        val l2 = List(false)

        val s1 = Set(true)
        val s2 = Set(false)

        val l12 = List(true, false)

        val s12 = Set(true, false)

        for {
          _ <- Ns.int.bools$ insert List(
            (1, Some(Set(true))),
            (2, Some(Set(false))),
            (3, Some(Set(false, true))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.bools_.apply(bool1, bool2).get === List(1, 2, 3)

          // `or`
          _ <- Ns.int.bools_.apply(bool1 or bool2).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.bools_.apply(seq0).get === List(4)
          _ <- Ns.int.bools_.apply(List(bool1), List(bool2)).get === List(1, 2, 3)
          _ <- Ns.int.bools_.apply(l1, l2).get === List(1, 2, 3)
          _ <- Ns.int.bools_.apply(List(bool1, bool2)).get === List(1, 2, 3)
          _ <- Ns.int.bools_.apply(l12).get === List(1, 2, 3)


          // AND semantics

          // Set
          _ <- Ns.int.bools_.apply(set0).get === List(4)

          _ <- Ns.int.bools_.apply(Set(bool1)).get === List(1, 3)
          _ <- Ns.int.bools_.apply(s1).get === List(1, 3)

          _ <- Ns.int.bools_.apply(Set(bool2)).get === List(2, 3)
          _ <- Ns.int.bools_.apply(s2).get === List(2, 3)

          _ <- Ns.int.bools_.apply(Set(bool1, bool2)).get === List(3)
          _ <- Ns.int.bools_.apply(s12).get === List(3)

          // `and`
          _ <- Ns.int.bools_.apply(bool1 and bool2).get === List(3)
        } yield ()
      }
    }
  }
}