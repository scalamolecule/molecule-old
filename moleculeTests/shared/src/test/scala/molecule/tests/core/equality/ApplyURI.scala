package molecule.tests.core.equality

import java.net.URI
import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out4._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object ApplyURI extends AsyncTestSuite {

  lazy val tests = Tests {


    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.uri$ insert List(
            (1, Some(uri1)),
            (2, Some(uri2)),
            (3, Some(uri3)),
            (4, None)
          )
          // Varargs
          _ <- Ns.uri.apply(uri1).get === List(uri1)
          _ <- Ns.uri.apply(uri2).get === List(uri2)
          _ <- Ns.uri.apply(uri1, uri2).get === List(uri1, uri2)

          // `or`
          _ <- Ns.uri.apply(uri1 or uri2).get === List(uri1, uri2)
          _ <- Ns.uri.apply(uri1 or uri2 or uri3).get === List(uri3, uri1, uri2)

          // Seq
          _ <- Ns.uri.apply().get === Nil
          _ <- Ns.uri.apply(Nil).get === Nil
          _ <- Ns.uri.apply(List(uri1)).get === List(uri1)
          _ <- Ns.uri.apply(List(uri2)).get === List(uri2)
          _ <- Ns.uri.apply(List(uri1, uri2)).get === List(uri1, uri2)
          _ <- Ns.uri.apply(List(uri1), List(uri2)).get === List(uri1, uri2)
          _ <- Ns.uri.apply(List(uri1, uri2), List(uri3)).get === List(uri3, uri1, uri2)
          _ <- Ns.uri.apply(List(uri1), List(uri2, uri3)).get === List(uri3, uri1, uri2)
          _ <- Ns.uri.apply(List(uri1, uri2, uri3)).get === List(uri3, uri1, uri2)
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.uri$ insert List(
            (1, Some(uri1)),
            (2, Some(uri2)),
            (3, Some(uri3)),
            (4, None)
          )

          // Varargs
          _ <- Ns.int.uri_.apply(uri1).get === List(1)
          _ <- Ns.int.uri_.apply(uri2).get === List(2)
          _ <- Ns.int.uri_.apply(uri1, uri2).get === List(1, 2)

          // `or`
          _ <- Ns.int.uri_.apply(uri1 or uri2).get === List(1, 2)
          _ <- Ns.int.uri_.apply(uri1 or uri2 or uri3).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.uri_.apply().get === List(4)
          _ <- Ns.int.uri_.apply(Nil).get === List(4)
          _ <- Ns.int.uri_.apply(List(uri1)).get === List(1)
          _ <- Ns.int.uri_.apply(List(uri2)).get === List(2)
          _ <- Ns.int.uri_.apply(List(uri1, uri2)).get === List(1, 2)
          _ <- Ns.int.uri_.apply(List(uri1), List(uri2)).get === List(1, 2)
          _ <- Ns.int.uri_.apply(List(uri1, uri2), List(uri3)).get === List(1, 2, 3)
          _ <- Ns.int.uri_.apply(List(uri1), List(uri2, uri3)).get === List(1, 2, 3)
          _ <- Ns.int.uri_.apply(List(uri1, uri2, uri3)).get === List(1, 2, 3)
        } yield ()
      }
    }

    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.uris$ insert List(
            (1, Some(Set(uri1, uri2))),
            (2, Some(Set(uri2, uri3))),
            (3, Some(Set(uri3, uri4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.uris.apply(uri1).get === List((1, Set(uri1, uri2)))
          _ <- Ns.int.uris.apply(uri2).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(uri1, uri2).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))

          // `or`
          _ <- Ns.int.uris.apply(uri1 or uri2).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(uri1 or uri2 or uri3).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)), (3, Set(uri3, uri4)))

          // Seq
          _ <- Ns.int.uris.apply().get === Nil
          _ <- Ns.int.uris.apply(Nil).get === Nil
          _ <- Ns.int.uris.apply(List(uri1)).get === List((1, Set(uri1, uri2)))
          _ <- Ns.int.uris.apply(List(uri2)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(List(uri1, uri2)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(List(uri1), List(uri2)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(List(uri1, uri2), List(uri3)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)), (3, Set(uri3, uri4)))
          _ <- Ns.int.uris.apply(List(uri1), List(uri2, uri3)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)), (3, Set(uri3, uri4)))
          _ <- Ns.int.uris.apply(List(uri1, uri2, uri3)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)), (3, Set(uri3, uri4)))


          // AND semantics

          // Set
          _ <- Ns.int.uris.apply(Set[URI]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.int.uris.apply(Set(uri1)).get === List((1, Set(uri1, uri2)))
          _ <- Ns.int.uris.apply(Set(uri2)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(Set(uri1, uri2)).get === List((1, Set(uri1, uri2)))
          _ <- Ns.int.uris.apply(Set(uri1, uri3)).get === Nil
          _ <- Ns.int.uris.apply(Set(uri2, uri3)).get === List((2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(Set(uri1, uri2, uri3)).get === Nil

          _ <- Ns.int.uris.apply(Set(uri1, uri2), Set[URI]()).get === List((1, Set(uri1, uri2)))
          _ <- Ns.int.uris.apply(Set(uri1, uri2), Set(uri2)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(Set(uri1, uri2), Set(uri3)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)), (3, Set(uri3, uri4)))
          _ <- Ns.int.uris.apply(Set(uri1, uri2), Set(uri4)).get === List((1, Set(uri1, uri2)), (3, Set(uri3, uri4)))
          _ <- Ns.int.uris.apply(Set(uri1, uri2), Set(uri2), Set(uri3)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)), (3, Set(uri3, uri4)))

          _ <- Ns.int.uris.apply(Set(uri1, uri2), Set(uri2, uri3)).get === List((1, Set(uri1, uri2)), (2, Set(uri2, uri3)))
          _ <- Ns.int.uris.apply(Set(uri1, uri2), Set(uri2, uri4)).get === List((1, Set(uri1, uri2)))
          _ <- Ns.int.uris.apply(Set(uri1, uri2), Set(uri3, uri4)).get === List((1, Set(uri1, uri2)), (3, Set(uri3, uri4)))

          // `and`
          _ <- Ns.int.uris.apply(uri1 and uri2).get === List((1, Set(uri1, uri2)))
          _ <- Ns.int.uris.apply(uri1 and uri3).get === Nil
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.uris$ insert List(
            (1, Some(Set(uri1, uri2))),
            (2, Some(Set(uri2, uri3))),
            (3, Some(Set(uri3, uri4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.uris.apply(uri1).get === List(Set(uri1, uri2))
          _ <- Ns.uris.apply(uri2).get === List(Set(uri1, uri3, uri2))
          _ <- Ns.uris.apply(uri1, uri2).get === List(Set(uri1, uri3, uri2))

          // `or`
          _ <- Ns.uris.apply(uri1 or uri2).get === List(Set(uri1, uri3, uri2))
          _ <- Ns.uris.apply(uri1 or uri2 or uri3).get === List(Set(uri1, uri4, uri3, uri2))

          // Seq
          _ <- Ns.uris.apply().get === Nil
          _ <- Ns.uris.apply(Nil).get === Nil
          _ <- Ns.uris.apply(List(uri1)).get === List(Set(uri1, uri2))
          _ <- Ns.uris.apply(List(uri2)).get === List(Set(uri1, uri3, uri2))
          _ <- Ns.uris.apply(List(uri1, uri2)).get === List(Set(uri1, uri3, uri2))
          _ <- Ns.uris.apply(List(uri1), List(uri2)).get === List(Set(uri1, uri3, uri2))
          _ <- Ns.uris.apply(List(uri1, uri2), List(uri3)).get === List(Set(uri1, uri4, uri3, uri2))
          _ <- Ns.uris.apply(List(uri1), List(uri2, uri3)).get === List(Set(uri1, uri4, uri3, uri2))
          _ <- Ns.uris.apply(List(uri1, uri2, uri3)).get === List(Set(uri1, uri4, uri3, uri2))


          // AND semantics

          // Set
          _ <- Ns.uris.apply(Set[URI]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.uris.apply(Set(uri1)).get === List(Set(uri1, uri2))
          _ <- Ns.uris.apply(Set(uri2)).get === List(Set(uri1, uri3, uri2))
          _ <- Ns.uris.apply(Set(uri1, uri2)).get === List(Set(uri1, uri2))
          _ <- Ns.uris.apply(Set(uri1, uri3)).get === Nil
          _ <- Ns.uris.apply(Set(uri2, uri3)).get === List(Set(uri2, uri3))
          _ <- Ns.uris.apply(Set(uri1, uri2, uri3)).get === Nil

          _ <- Ns.uris.apply(Set(uri1, uri2), Set(uri2)).get === List(Set(uri1, uri2, uri3))
          _ <- Ns.uris.apply(Set(uri1, uri2), Set(uri3)).get === List(Set(uri1, uri2, uri3, uri4))
          _ <- Ns.uris.apply(Set(uri1, uri2), Set(uri4)).get === List(Set(uri1, uri2, uri3, uri4))
          _ <- Ns.uris.apply(Set(uri1, uri2), Set(uri2), Set(uri3)).get === List(Set(uri1, uri2, uri3, uri4))

          _ <- Ns.uris.apply(Set(uri1, uri2), Set(uri2, uri3)).get === List(Set(uri1, uri2, uri3))
          _ <- Ns.uris.apply(Set(uri1, uri2), Set(uri2, uri4)).get === List(Set(uri1, uri2))
          _ <- Ns.uris.apply(Set(uri1, uri2), Set(uri3, uri4)).get === List(Set(uri1, uri2, uri3, uri4))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.uris.apply(uri1 and uri2).get === List(Set(uri1, uri2))
          _ <- Ns.uris.apply(uri1 and uri3).get === Nil
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.uris$ insert List(
            (1, Some(Set(uri1, uri2))),
            (2, Some(Set(uri2, uri3))),
            (3, Some(Set(uri3, uri4))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.uris_.apply(uri1).get === List(1)
          _ <- Ns.int.uris_.apply(uri2).get === List(1, 2)
          _ <- Ns.int.uris_.apply(uri1, uri2).get === List(1, 2)

          // `or`
          _ <- Ns.int.uris_.apply(uri1 or uri2).get === List(1, 2)
          _ <- Ns.int.uris_.apply(uri1 or uri2 or uri3).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.uris_.apply().get === List(4) // entities with no card-many values asserted
          _ <- Ns.int.uris_.apply(Nil).get === List(4)
          _ <- Ns.int.uris_.apply(List(uri1)).get === List(1)
          _ <- Ns.int.uris_.apply(List(uri2)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(List(uri1, uri2)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(List(uri1), List(uri2)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(List(uri1, uri2), List(uri3)).get === List(1, 2, 3)
          _ <- Ns.int.uris_.apply(List(uri1), List(uri2, uri3)).get === List(1, 2, 3)
          _ <- Ns.int.uris_.apply(List(uri1, uri2, uri3)).get === List(1, 2, 3)


          // AND semantics

          // Set
          _ <- Ns.int.uris_.apply(Set[URI]()).get === List(4)
          _ <- Ns.int.uris_.apply(Set(uri1)).get === List(1)
          _ <- Ns.int.uris_.apply(Set(uri2)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(Set(uri1, uri2)).get === List(1)
          _ <- Ns.int.uris_.apply(Set(uri1, uri3)).get === Nil
          _ <- Ns.int.uris_.apply(Set(uri2, uri3)).get === List(2)
          _ <- Ns.int.uris_.apply(Set(uri1, uri2, uri3)).get === Nil

          _ <- Ns.int.uris_.apply(Set(uri1, uri2), Set(uri2)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(Set(uri1, uri2), Set(uri3)).get === List(1, 2, 3)
          _ <- Ns.int.uris_.apply(Set(uri1, uri2), Set(uri4)).get === List(1, 3)
          _ <- Ns.int.uris_.apply(Set(uri1, uri2), Set(uri2), Set(uri3)).get === List(1, 2, 3)

          _ <- Ns.int.uris_.apply(Set(uri1, uri2), Set(uri2, uri3)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(Set(uri1, uri2), Set(uri2, uri4)).get === List(1)
          _ <- Ns.int.uris_.apply(Set(uri1, uri2), Set(uri3, uri4)).get === List(1, 3)


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.uris_.apply(uri1 and uri2).get === List(1)
          _ <- Ns.int.uris_.apply(uri1 and uri3).get === Nil
        } yield ()
      }


      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[URI]()

        val l1 = List(uri1)
        val l2 = List(uri2)

        val s1 = Set(uri1)
        val s2 = Set(uri2)

        val l12 = List(uri1, uri2)
        val l23 = List(uri2, uri3)

        val s12 = Set(uri1, uri2)
        val s23 = Set(uri2, uri3)

        for {
          _ <- Ns.int.uris$ insert List(
            (1, Some(Set(uri1, uri2))),
            (2, Some(Set(uri2, uri3))),
            (3, Some(Set(uri3, uri4))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.uris_.apply(uri1, uri2).get === List(1, 2)

          // `or`
          _ <- Ns.int.uris_.apply(uri1 or uri2).get === List(1, 2)

          // Seq
          _ <- Ns.int.uris_.apply(seq0).get === List(4)
          _ <- Ns.int.uris_.apply(List(uri1), List(uri2)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(l1, l2).get === List(1, 2)
          _ <- Ns.int.uris_.apply(List(uri1, uri2)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(l12).get === List(1, 2)


          // AND semantics

          // Set
          _ <- Ns.int.uris_.apply(set0).get === List(4)

          _ <- Ns.int.uris_.apply(Set(uri1)).get === List(1)
          _ <- Ns.int.uris_.apply(s1).get === List(1)

          _ <- Ns.int.uris_.apply(Set(uri2)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(s2).get === List(1, 2)

          _ <- Ns.int.uris_.apply(Set(uri1, uri2)).get === List(1)
          _ <- Ns.int.uris_.apply(s12).get === List(1)

          _ <- Ns.int.uris_.apply(Set(uri2, uri3)).get === List(2)
          _ <- Ns.int.uris_.apply(s23).get === List(2)

          _ <- Ns.int.uris_.apply(Set(uri1, uri2), Set(uri2, uri3)).get === List(1, 2)
          _ <- Ns.int.uris_.apply(s12, s23).get === List(1, 2)

          // `and`
          _ <- Ns.int.uris_.apply(uri1 and uri2).get === List(1)
        } yield ()
      }
    }
  }
}