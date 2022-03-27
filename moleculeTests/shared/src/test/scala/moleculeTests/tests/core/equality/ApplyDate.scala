package moleculeTests.tests.core.equality

import java.util.Date
import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object ApplyDate extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.date$ insert List(
            (1, Some(date1)),
            (2, Some(date2)),
            (3, Some(date3)),
            (4, None)
          )
          // Varargs
          _ <- Ns.date.apply(date1).get.map(_ ==> List(date1))
          _ <- Ns.date.apply(date2).get.map(_ ==> List(date2))
          _ <- Ns.date.apply(date1, date2).get.map(_ ==> List(date2, date1))

          // `or`
          _ <- Ns.date.apply(date1 or date2).get.map(_ ==> List(date2, date1))
          _ <- Ns.date.apply(date1 or date2 or date3).a1.get.map(_ ==> List(date1, date2, date3))

          // Seq
          _ <- Ns.date.apply().get.map(_ ==> Nil)
          _ <- Ns.date.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.date.apply(List(date1)).get.map(_ ==> List(date1))
          _ <- Ns.date.apply(List(date2)).get.map(_ ==> List(date2))
          _ <- Ns.date.apply(List(date1, date2)).get.map(_ ==> List(date2, date1))
          _ <- Ns.date.apply(List(date1), List(date2)).get.map(_ ==> List(date2, date1))
          _ <- Ns.date.apply(List(date1, date2), List(date3)).a1.get.map(_ ==> List(date1, date2, date3))
          _ <- Ns.date.apply(List(date1), List(date2, date3)).a1.get.map(_ ==> List(date1, date2, date3))
          _ <- Ns.date.apply(List(date1, date2, date3)).a1.get.map(_ ==> List(date1, date2, date3))
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for{
          _ <- Ns.int.date$ insert List(
            (1, Some(date1)),
            (2, Some(date2)),
            (3, Some(date3)),
            (4, None)
          )
          // Varargs
          _ <- Ns.int.date_.apply(date1).get.map(_ ==> List(1))
          _ <- Ns.int.date_.apply(date2).get.map(_ ==> List(2))
          _ <- Ns.int.date_.apply(date1, date2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.date_.apply(date1 or date2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.date_.apply(date1 or date2 or date3).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.date_.apply().get.map(_ ==> List(4))
          _ <- Ns.int.date_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.date_.apply(List(date1)).get.map(_ ==> List(1))
          _ <- Ns.int.date_.apply(List(date2)).get.map(_ ==> List(2))
          _ <- Ns.int.date_.apply(List(date1, date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.date_.apply(List(date1), List(date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.date_.apply(List(date1, date2), List(date3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.date_.apply(List(date1), List(date2, date3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.date_.apply(List(date1, date2, date3)).get.map(_ ==> List(1, 2, 3))
        } yield ()
      }
    }


    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.dates$ insert List(
            (1, Some(Set(date1, date2))),
            (2, Some(Set(date2, date3))),
            (3, Some(Set(date3, date4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.dates.apply(date1).get.map(_ ==> List((1, Set(date1, date2))))
          _ <- Ns.int.dates.apply(date2).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(date1, date2).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))

          // `or`
          _ <- Ns.int.dates.apply(date1 or date2).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(date1 or date2 or date3).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3)), (3, Set(date3, date4))))

          // Seq
          _ <- Ns.int.dates.apply().get.map(_ ==> Nil)
          _ <- Ns.int.dates.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.int.dates.apply(List(date1)).get.map(_ ==> List((1, Set(date1, date2))))
          _ <- Ns.int.dates.apply(List(date2)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(List(date1, date2)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(List(date1), List(date2)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(List(date1, date2), List(date3)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3)), (3, Set(date3, date4))))
          _ <- Ns.int.dates.apply(List(date1), List(date2, date3)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3)), (3, Set(date3, date4))))
          _ <- Ns.int.dates.apply(List(date1, date2, date3)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3)), (3, Set(date3, date4))))


          // AND semantics

          // Set
          _ <- Ns.int.dates.apply(Set[Date]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.int.dates.apply(Set(date1)).get.map(_ ==> List((1, Set(date1, date2))))
          _ <- Ns.int.dates.apply(Set(date2)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(Set(date1, date2)).get.map(_ ==> List((1, Set(date1, date2))))
          _ <- Ns.int.dates.apply(Set(date1, date3)).get.map(_ ==> Nil)
          _ <- Ns.int.dates.apply(Set(date2, date3)).get.map(_ ==> List((2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(Set(date1, date2, date3)).get.map(_ ==> Nil)

          _ <- Ns.int.dates.apply(Set(date1, date2), Set[Date]()).get.map(_ ==> List((1, Set(date1, date2))))
          _ <- Ns.int.dates.apply(Set(date1, date2), Set(date2)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(Set(date1, date2), Set(date3)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3)), (3, Set(date3, date4))))
          _ <- Ns.int.dates.apply(Set(date1, date2), Set(date4)).get.map(_ ==> List((1, Set(date1, date2)), (3, Set(date3, date4))))
          _ <- Ns.int.dates.apply(Set(date1, date2), Set(date2), Set(date3)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3)), (3, Set(date3, date4))))

          _ <- Ns.int.dates.apply(Set(date1, date2), Set(date2, date3)).get.map(_ ==> List((1, Set(date1, date2)), (2, Set(date2, date3))))
          _ <- Ns.int.dates.apply(Set(date1, date2), Set(date2, date4)).get.map(_ ==> List((1, Set(date1, date2))))
          _ <- Ns.int.dates.apply(Set(date1, date2), Set(date3, date4)).get.map(_ ==> List((1, Set(date1, date2)), (3, Set(date3, date4))))

          // `and`
          _ <- Ns.int.dates.apply(date1 and date2).get.map(_ ==> List((1, Set(date1, date2))))
          _ <- Ns.int.dates.apply(date1 and date3).get.map(_ ==> Nil)
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.dates$ insert List(
            (1, Some(Set(date1, date2))),
            (2, Some(Set(date2, date3))),
            (3, Some(Set(date3, date4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.dates.apply(date1).get.map(_ ==> List(Set(date1, date2)))
          _ <- Ns.dates.apply(date2).get.map(_ ==> List(Set(date1, date3, date2)))
          _ <- Ns.dates.apply(date1, date2).get.map(_ ==> List(Set(date1, date3, date2)))

          // `or`
          _ <- Ns.dates.apply(date1 or date2).get.map(_ ==> List(Set(date1, date3, date2)))
          _ <- Ns.dates.apply(date1 or date2 or date3).get.map(_ ==> List(Set(date1, date4, date3, date2)))

          // Seq
          _ <- Ns.dates.apply().get.map(_ ==> Nil)
          _ <- Ns.dates.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.dates.apply(List(date1)).get.map(_ ==> List(Set(date1, date2)))
          _ <- Ns.dates.apply(List(date2)).get.map(_ ==> List(Set(date1, date3, date2)))
          _ <- Ns.dates.apply(List(date1, date2)).get.map(_ ==> List(Set(date1, date3, date2)))
          _ <- Ns.dates.apply(List(date1), List(date2)).get.map(_ ==> List(Set(date1, date3, date2)))
          _ <- Ns.dates.apply(List(date1, date2), List(date3)).get.map(_ ==> List(Set(date1, date4, date3, date2)))
          _ <- Ns.dates.apply(List(date1), List(date2, date3)).get.map(_ ==> List(Set(date1, date4, date3, date2)))
          _ <- Ns.dates.apply(List(date1, date2, date3)).get.map(_ ==> List(Set(date1, date4, date3, date2)))


          // AND semantics

          // Set
          _ <- Ns.dates.apply(Set[Date]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.dates.apply(Set(date1)).get.map(_ ==> List(Set(date1, date2)))
          _ <- Ns.dates.apply(Set(date2)).get.map(_ ==> List(Set(date1, date3, date2)))
          _ <- Ns.dates.apply(Set(date1, date2)).get.map(_ ==> List(Set(date1, date2)))
          _ <- Ns.dates.apply(Set(date1, date3)).get.map(_ ==> Nil)
          _ <- Ns.dates.apply(Set(date2, date3)).get.map(_ ==> List(Set(date2, date3)))
          _ <- Ns.dates.apply(Set(date1, date2, date3)).get.map(_ ==> Nil)

          _ <- Ns.dates.apply(Set(date1, date2), Set(date2)).get.map(_ ==> List(Set(date1, date2, date3)))
          _ <- Ns.dates.apply(Set(date1, date2), Set(date3)).get.map(_ ==> List(Set(date1, date2, date3, date4)))
          _ <- Ns.dates.apply(Set(date1, date2), Set(date4)).get.map(_ ==> List(Set(date1, date2, date3, date4)))
          _ <- Ns.dates.apply(Set(date1, date2), Set(date2), Set(date3)).get.map(_ ==> List(Set(date1, date2, date3, date4)))

          _ <- Ns.dates.apply(Set(date1, date2), Set(date2, date3)).get.map(_ ==> List(Set(date1, date2, date3)))
          _ <- Ns.dates.apply(Set(date1, date2), Set(date2, date4)).get.map(_ ==> List(Set(date1, date2)))
          _ <- Ns.dates.apply(Set(date1, date2), Set(date3, date4)).get.map(_ ==> List(Set(date1, date2, date3, date4)))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.dates.apply(date1 and date2).get.map(_ ==> List(Set(date1, date2)))
          _ <- Ns.dates.apply(date1 and date3).get.map(_ ==> Nil)
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.dates$ insert List(
            (1, Some(Set(date1, date2))),
            (2, Some(Set(date2, date3))),
            (3, Some(Set(date3, date4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.dates_.apply(date1).get.map(_ ==> List(1))
          _ <- Ns.int.dates_.apply(date2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(date1, date2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.dates_.apply(date1 or date2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(date1 or date2 or date3).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.dates_.apply().get.map(_ ==> List(4)) // entities with no card-many values asserted
          _ <- Ns.int.dates_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.dates_.apply(List(date1)).get.map(_ ==> List(1))
          _ <- Ns.int.dates_.apply(List(date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(List(date1, date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(List(date1), List(date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(List(date1, date2), List(date3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.dates_.apply(List(date1), List(date2, date3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.dates_.apply(List(date1, date2, date3)).get.map(_ ==> List(1, 2, 3))


          // AND semantics

          // Set
          _ <- Ns.int.dates_.apply(Set[Date]()).get.map(_ ==> List(4))
          _ <- Ns.int.dates_.apply(Set(date1)).get.map(_ ==> List(1))
          _ <- Ns.int.dates_.apply(Set(date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(Set(date1, date2)).get.map(_ ==> List(1))
          _ <- Ns.int.dates_.apply(Set(date1, date3)).get.map(_ ==> Nil)
          _ <- Ns.int.dates_.apply(Set(date2, date3)).get.map(_ ==> List(2))
          _ <- Ns.int.dates_.apply(Set(date1, date2, date3)).get.map(_ ==> Nil)

          _ <- Ns.int.dates_.apply(Set(date1, date2), Set(date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(Set(date1, date2), Set(date3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.dates_.apply(Set(date1, date2), Set(date4)).get.map(_ ==> List(1, 3))
          _ <- Ns.int.dates_.apply(Set(date1, date2), Set(date2), Set(date3)).get.map(_ ==> List(1, 2, 3))

          _ <- Ns.int.dates_.apply(Set(date1, date2), Set(date2, date3)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(Set(date1, date2), Set(date2, date4)).get.map(_ ==> List(1))
          _ <- Ns.int.dates_.apply(Set(date1, date2), Set(date3, date4)).get.map(_ ==> List(1, 3))


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.dates_.apply(date1 and date2).get.map(_ ==> List(1))
          _ <- Ns.int.dates_.apply(date1 and date3).get.map(_ ==> Nil)
        } yield ()
      }


      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[Date]()

        val l1 = List(date1)
        val l2 = List(date2)

        val s1 = Set(date1)
        val s2 = Set(date2)

        val l12 = List(date1, date2)

        val s12 = Set(date1, date2)
        val s23 = Set(date2, date3)

        for {
          _ <- Ns.int.dates$ insert List(
            (1, Some(Set(date1, date2))),
            (2, Some(Set(date2, date3))),
            (3, Some(Set(date3, date4))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.dates_.apply(date1, date2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.dates_.apply(date1 or date2).get.map(_ ==> List(1, 2))

          // Seq
          _ <- Ns.int.dates_.apply(seq0).get.map(_ ==> List(4))
          _ <- Ns.int.dates_.apply(List(date1), List(date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(l1, l2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(List(date1, date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(l12).get.map(_ ==> List(1, 2))


          // AND semantics

          // Set
          _ <- Ns.int.dates_.apply(set0).get.map(_ ==> List(4))

          _ <- Ns.int.dates_.apply(Set(date1)).get.map(_ ==> List(1))
          _ <- Ns.int.dates_.apply(s1).get.map(_ ==> List(1))

          _ <- Ns.int.dates_.apply(Set(date2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(s2).get.map(_ ==> List(1, 2))

          _ <- Ns.int.dates_.apply(Set(date1, date2)).get.map(_ ==> List(1))
          _ <- Ns.int.dates_.apply(s12).get.map(_ ==> List(1))

          _ <- Ns.int.dates_.apply(Set(date2, date3)).get.map(_ ==> List(2))
          _ <- Ns.int.dates_.apply(s23).get.map(_ ==> List(2))

          _ <- Ns.int.dates_.apply(Set(date1, date2), Set(date2, date3)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.dates_.apply(s12, s23).get.map(_ ==> List(1, 2))

          // `and`
          _ <- Ns.int.dates_.apply(date1 and date2).get.map(_ ==> List(1))
        } yield ()
      }
    }
  }
}
