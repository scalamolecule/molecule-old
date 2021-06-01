package moleculeTests.tests.core.equality

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
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
          _ <- Ns.bool.apply(true).get.map(_ ==> List(true))
          _ <- Ns.bool.apply(false).get.map(_ ==> List(false))
          _ <- Ns.bool.apply(true, false).get.map(_ ==> List(false, true))

          // `or`
          _ <- Ns.bool.apply(true or false).get.map(_ ==> List(false, true))

          // Seq
          _ <- Ns.bool.apply().get.map(_ ==> Nil)
          _ <- Ns.bool.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.bool.apply(List(true)).get.map(_ ==> List(true))
          _ <- Ns.bool.apply(List(false)).get.map(_ ==> List(false))
          _ <- Ns.bool.apply(List(true, false)).get.map(_ ==> List(false, true))
          _ <- Ns.bool.apply(List(true), List(false)).get.map(_ ==> List(false, true))
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
          _ <- Ns.int.bool_.apply(true).get.map(_ ==> List(1))
          _ <- Ns.int.bool_.apply(false).get.map(_ ==> List(2))
          _ <- Ns.int.bool_.apply(true, false).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.bool_.apply(true or false).get.map(_ ==> List(1, 2))

          // Seq
          _ <- Ns.int.bool_.apply(List(true)).get.map(_ ==> List(1))
          _ <- Ns.int.bool_.apply(List(false)).get.map(_ ==> List(2))
          _ <- Ns.int.bool_.apply(List(true, false)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.bool_.apply(List(true), List(false)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.bool_.apply(List(true, false), List(true)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.bool_.apply(List(true), List(false, true)).get.map(_ ==> List(1, 2))
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
          _ <- Ns.int.bools.apply(true).get.map(_ ==> List((1, Set(true)), (3, Set(true, false))))
          _ <- Ns.int.bools.apply(false).get.map(_ ==> List((2, Set(false)), (3, Set(true, false))))
          _ <- Ns.int.bools.apply(true, false).get.map(_ ==> List((1, Set(true)), (2, Set(false)), (3, Set(true, false))))

          // `or`
          _ <- Ns.int.bools.apply(true or false).get.map(_ ==> List((1, Set(true)), (2, Set(false)), (3, Set(true, false))))

          // Seq
          _ <- Ns.int.bools.apply().get.map(_ ==> Nil)
          _ <- Ns.int.bools.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.int.bools.apply(List(true)).get.map(_ ==> List((1, Set(true)), (3, Set(true, false))))
          _ <- Ns.int.bools.apply(List(false)).get.map(_ ==> List((2, Set(false)), (3, Set(true, false))))
          _ <- Ns.int.bools.apply(List(true, false)).get.map(_ ==> List((1, Set(true)), (2, Set(false)), (3, Set(true, false))))
          _ <- Ns.int.bools.apply(List(true), List(false)).get.map(_ ==> List((1, Set(true)), (2, Set(false)), (3, Set(true, false))))


          // AND semantics

          // Set
          _ <- Ns.int.bools.apply(Set[Boolean]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.int.bools.apply(Set(true)).get.map(_ ==> List((1, Set(true)), (3, Set(true, false))))
          _ <- Ns.int.bools.apply(Set(false)).get.map(_ ==> List((2, Set(false)), (3, Set(true, false))))
          _ <- Ns.int.bools.apply(Set(true, false)).get.map(_ ==> List((3, Set(true, false))))

          _ <- Ns.int.bools.apply(Set(true, false), Set[Boolean]()).get.map(_ ==> List((3, Set(true, false))))
          _ <- Ns.int.bools.apply(Set(true, false), Set(false)).get.map(_ ==> List((2, Set(false)), (3, Set(true, false))))

          // `and`
          _ <- Ns.int.bools.apply(true and false).get.map(_ ==> List((3, Set(true, false))))
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.bools$ insert List(
            (1, Some(Set(true))),
            (2, Some(Set(false))),
            (3, Some(Set(false, true))),
            (4, None)
          )
          // OR semantics

          // Varargs
          _ <- Ns.bools.apply(true).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(false).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(true, false).get.map(_ ==> List(Set(true, false)))

          // `or`
          _ <- Ns.bools.apply(true or false).get.map(_ ==> List(Set(true, false)))

          // Seq
          _ <- Ns.bools.apply().get.map(_ ==> Nil)
          _ <- Ns.bools.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.bools.apply(List(true)).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(List(false)).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(List(true, false)).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(List(true), List(false)).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(List(true, false), List(true)).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(List(true), List(false, true)).get.map(_ ==> List(Set(true, false)))


          // AND semantics

          // Set
          _ <- Ns.bools.apply(Set[Boolean]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.bools.apply(Set(true)).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(Set(false)).get.map(_ ==> List(Set(true, false)))
          _ <- Ns.bools.apply(Set(true, false)).get.map(_ ==> List(Set(true, false)))

          _ <- Ns.bools.apply(Set(true, false), Set(false)).get.map(_ ==> List(Set(true, false)))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.bools.apply(true and false).get.map(_ ==> List(Set(true, false)))
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
          _ <- Ns.int.bools_.apply(true).get.map(_ ==> List(1, 3))
          _ <- Ns.int.bools_.apply(false).get.map(_ ==> List(2, 3))
          _ <- Ns.int.bools_.apply(true, false).get.map(_ ==> List(1, 2, 3))

          // `or`
          _ <- Ns.int.bools_.apply(true or false).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.bools_.apply().get.map(_ ==> List(4)) // entities with no card-many values asserted
          _ <- Ns.int.bools_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.bools_.apply(List(true)).get.map(_ ==> List(1, 3))
          _ <- Ns.int.bools_.apply(List(false)).get.map(_ ==> List(2, 3))
          _ <- Ns.int.bools_.apply(List(true, false)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.bools_.apply(List(true), List(false)).get.map(_ ==> List(1, 2, 3))


          // AND semantics

          // Set
          _ <- Ns.int.bools_.apply(Set[Boolean]()).get.map(_ ==> List(4))
          _ <- Ns.int.bools_.apply(Set(true)).get.map(_ ==> List(1, 3))
          _ <- Ns.int.bools_.apply(Set(false)).get.map(_ ==> List(2, 3))
          _ <- Ns.int.bools_.apply(Set(true, false)).get.map(_ ==> List(3))

          _ <- Ns.int.bools_.apply(Set(true, false), Set(true)).get.map(_ ==> List(1, 3))
          _ <- Ns.int.bools_.apply(Set(true, false), Set(false)).get.map(_ ==> List(2, 3))


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.bools_.apply(true and false).get.map(_ ==> List(3))
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
          _ <- Ns.int.bools_.apply(bool1, bool2).get.map(_ ==> List(1, 2, 3))

          // `or`
          _ <- Ns.int.bools_.apply(bool1 or bool2).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.bools_.apply(seq0).get.map(_ ==> List(4))
          _ <- Ns.int.bools_.apply(List(bool1), List(bool2)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.bools_.apply(l1, l2).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.bools_.apply(List(bool1, bool2)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.bools_.apply(l12).get.map(_ ==> List(1, 2, 3))


          // AND semantics

          // Set
          _ <- Ns.int.bools_.apply(set0).get.map(_ ==> List(4))

          _ <- Ns.int.bools_.apply(Set(bool1)).get.map(_ ==> List(1, 3))
          _ <- Ns.int.bools_.apply(s1).get.map(_ ==> List(1, 3))

          _ <- Ns.int.bools_.apply(Set(bool2)).get.map(_ ==> List(2, 3))
          _ <- Ns.int.bools_.apply(s2).get.map(_ ==> List(2, 3))

          _ <- Ns.int.bools_.apply(Set(bool1, bool2)).get.map(_ ==> List(3))
          _ <- Ns.int.bools_.apply(s12).get.map(_ ==> List(3))

          // `and`
          _ <- Ns.int.bools_.apply(bool1 and bool2).get.map(_ ==> List(3))
        } yield ()
      }
    }
  }
}