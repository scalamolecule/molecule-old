package moleculeTests.tests.core.equality

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object ApplyLong extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.long$ insert List(
            (1, Some(1L)),
            (2, Some(2L)),
            (3, Some(3L)),
            (4, None)
          )

          // Varargs
          _ <- Ns.long.apply(1L).get.map(_ ==> List(1L))
          _ <- Ns.long.apply(2L).get.map(_ ==> List(2L))
          _ <- Ns.long.apply(1L, 2L).get.map(_ ==> List(1L, 2L))

          // `or`
          _ <- Ns.long.apply(1L or 2L).get.map(_ ==> List(1L, 2L))
          _ <- Ns.long.apply(1L or 2L or 3L).get.map(_ ==> List(1L, 2L, 3L))

          // Seq
          _ <- Ns.long.apply().get.map(_ ==> Nil)
          _ <- Ns.long.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.long.apply(List(1L)).get.map(_ ==> List(1L))
          _ <- Ns.long.apply(List(2L)).get.map(_ ==> List(2L))
          _ <- Ns.long.apply(List(1L, 2L)).get.map(_ ==> List(1L, 2L))
          _ <- Ns.long.apply(List(1L), List(2L)).get.map(_ ==> List(1L, 2L))
          _ <- Ns.long.apply(List(1L, 2L), List(3L)).get.map(_ ==> List(1L, 2L, 3L))
          _ <- Ns.long.apply(List(1L), List(2L, 3L)).get.map(_ ==> List(1L, 2L, 3L))
          _ <- Ns.long.apply(List(1L, 2L, 3L)).get.map(_ ==> List(1L, 2L, 3L))
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.long$ insert List(
            (1, Some(1L)),
            (2, Some(2L)),
            (3, Some(3L)),
            (4, None)
          )

          // Varargs
          _ <- Ns.int.long_.apply(1L).get.map(_ ==> List(1))
          _ <- Ns.int.long_.apply(2L).get.map(_ ==> List(2))
          _ <- Ns.int.long_.apply(1L, 2L).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.long_.apply(1L or 2L).get.map(_ ==> List(1, 2))
          _ <- Ns.int.long_.apply(1L or 2L or 3L).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.long_.apply().get.map(_ ==> List(4))
          _ <- Ns.int.long_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.long_.apply(List(1L)).get.map(_ ==> List(1))
          _ <- Ns.int.long_.apply(List(2L)).get.map(_ ==> List(2))
          _ <- Ns.int.long_.apply(List(1L, 2L)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.long_.apply(List(1L), List(2L)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.long_.apply(List(1L, 2L), List(3L)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.long_.apply(List(1L), List(2L, 3L)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.long_.apply(List(1L, 2L, 3L)).get.map(_ ==> List(1, 2, 3))
        } yield ()
      }
    }

    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.longs$ insert List(
            (1, Some(Set(1L, 2L))),
            (2, Some(Set(2L, 3L))),
            (3, Some(Set(3L, 4L))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.longs.apply(1L).get.map(_ ==> List((1, Set(1L, 2L))))
          _ <- Ns.int.longs.apply(2L).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(1L, 2L).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))

          // `or`
          _ <- Ns.int.longs.apply(1L or 2L).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(1L or 2L or 3L).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (3, Set(3L, 4L))))

          // Seq
          _ <- Ns.int.longs.apply().get.map(_ ==> Nil)
          _ <- Ns.int.longs.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.int.longs.apply(List(1L)).get.map(_ ==> List((1, Set(1L, 2L))))
          _ <- Ns.int.longs.apply(List(2L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(List(1L, 2L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(List(1L), List(2L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(List(1L, 2L), List(3L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (3, Set(3L, 4L))))
          _ <- Ns.int.longs.apply(List(1L), List(2L, 3L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (3, Set(3L, 4L))))
          _ <- Ns.int.longs.apply(List(1L, 2L, 3L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (3, Set(3L, 4L))))


          // AND semantics

          // Set
          _ <- Ns.int.longs.apply(Set[Long]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.int.longs.apply(Set(1L)).get.map(_ ==> List((1, Set(1L, 2L))))
          _ <- Ns.int.longs.apply(Set(2L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(Set(1L, 2L)).get.map(_ ==> List((1, Set(1L, 2L))))
          _ <- Ns.int.longs.apply(Set(1L, 3L)).get.map(_ ==> Nil)
          _ <- Ns.int.longs.apply(Set(2L, 3L)).get.map(_ ==> List((2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(Set(1L, 2L, 3L)).get.map(_ ==> Nil)

          _ <- Ns.int.longs.apply(Set(1L, 2L), Set[Long]()).get.map(_ ==> List((1, Set(1L, 2L))))
          _ <- Ns.int.longs.apply(Set(1L, 2L), Set(2L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(Set(1L, 2L), Set(3L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (3, Set(3L, 4L))))
          _ <- Ns.int.longs.apply(Set(1L, 2L), Set(4L)).get.map(_ ==> List((1, Set(1L, 2L)), (3, Set(3L, 4L))))
          _ <- Ns.int.longs.apply(Set(1L, 2L), Set(2L), Set(3L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L)), (3, Set(3L, 4L))))

          _ <- Ns.int.longs.apply(Set(1L, 2L), Set(2L, 3L)).get.map(_ ==> List((1, Set(1L, 2L)), (2, Set(2L, 3L))))
          _ <- Ns.int.longs.apply(Set(1L, 2L), Set(2L, 4L)).get.map(_ ==> List((1, Set(1L, 2L))))
          _ <- Ns.int.longs.apply(Set(1L, 2L), Set(3L, 4L)).get.map(_ ==> List((1, Set(1L, 2L)), (3, Set(3L, 4L))))

          // `and`
          _ <- Ns.int.longs.apply(1L and 2L).get.map(_ ==> List((1, Set(1L, 2L))))
          _ <- Ns.int.longs.apply(1L and 3L).get.map(_ ==> Nil)
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.longs$ insert List(
            (1, Some(Set(1L, 2L))),
            (2, Some(Set(2L, 3L))),
            (3, Some(Set(3L, 4L))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.longs.apply(1L).get.map(_ ==> List(Set(1L, 2L)))
          _ <- Ns.longs.apply(2L).get.map(_ ==> List(Set(1L, 3L, 2L)))
          _ <- Ns.longs.apply(1L, 2L).get.map(_ ==> List(Set(1L, 3L, 2L)))

          // `or`
          _ <- Ns.longs.apply(1L or 2L).get.map(_ ==> List(Set(1L, 3L, 2L)))
          _ <- Ns.longs.apply(1L or 2L or 3L).get.map(_ ==> List(Set(1L, 4L, 3L, 2L)))

          // Seq
          _ <- Ns.longs.apply().get.map(_ ==> Nil)
          _ <- Ns.longs.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.longs.apply(List(1L)).get.map(_ ==> List(Set(1L, 2L)))
          _ <- Ns.longs.apply(List(2L)).get.map(_ ==> List(Set(1L, 3L, 2L)))
          _ <- Ns.longs.apply(List(1L, 2L)).get.map(_ ==> List(Set(1L, 3L, 2L)))
          _ <- Ns.longs.apply(List(1L), List(2L)).get.map(_ ==> List(Set(1L, 3L, 2L)))
          _ <- Ns.longs.apply(List(1L, 2L), List(3L)).get.map(_ ==> List(Set(1L, 4L, 3L, 2L)))
          _ <- Ns.longs.apply(List(1L), List(2L, 3L)).get.map(_ ==> List(Set(1L, 4L, 3L, 2L)))
          _ <- Ns.longs.apply(List(1L, 2L, 3L)).get.map(_ ==> List(Set(1L, 4L, 3L, 2L)))


          // AND semantics

          // Set
          _ <- Ns.longs.apply(Set[Long]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.longs.apply(Set(1L)).get.map(_ ==> List(Set(1L, 2L)))
          _ <- Ns.longs.apply(Set(2L)).get.map(_ ==> List(Set(1L, 3L, 2L)))
          _ <- Ns.longs.apply(Set(1L, 2L)).get.map(_ ==> List(Set(1L, 2L)))
          _ <- Ns.longs.apply(Set(1L, 3L)).get.map(_ ==> Nil)
          _ <- Ns.longs.apply(Set(2L, 3L)).get.map(_ ==> List(Set(2L, 3L)))
          _ <- Ns.longs.apply(Set(1L, 2L, 3L)).get.map(_ ==> Nil)

          _ <- Ns.longs.apply(Set(1L, 2L), Set(2L)).get.map(_ ==> List(Set(1L, 2L, 3L)))
          _ <- Ns.longs.apply(Set(1L, 2L), Set(3L)).get.map(_ ==> List(Set(1L, 2L, 3L, 4L)))
          _ <- Ns.longs.apply(Set(1L, 2L), Set(4L)).get.map(_ ==> List(Set(1L, 2L, 3L, 4L)))
          _ <- Ns.longs.apply(Set(1L, 2L), Set(2L), Set(3L)).get.map(_ ==> List(Set(1L, 2L, 3L, 4L)))

          _ <- Ns.longs.apply(Set(1L, 2L), Set(2L, 3L)).get.map(_ ==> List(Set(1L, 2L, 3L)))
          _ <- Ns.longs.apply(Set(1L, 2L), Set(2L, 4L)).get.map(_ ==> List(Set(1L, 2L)))
          _ <- Ns.longs.apply(Set(1L, 2L), Set(3L, 4L)).get.map(_ ==> List(Set(1L, 2L, 3L, 4L)))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.longs.apply(1L and 2L).get.map(_ ==> List(Set(1L, 2L)))
          _ <- Ns.longs.apply(1L and 3L).get.map(_ ==> Nil)
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.longs$ insert List(
            (1, Some(Set(1L, 2L))),
            (2, Some(Set(2L, 3L))),
            (3, Some(Set(3L, 4L))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.longs_.apply(1L).get.map(_ ==> List(1))
          _ <- Ns.int.longs_.apply(2L).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(1L, 2L).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.longs_.apply(1L or 2L).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(1L or 2L or 3L).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.longs_.apply().get.map(_ ==> List(4)) // entities with no card-many values asserted
          _ <- Ns.int.longs_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.longs_.apply(List(1L)).get.map(_ ==> List(1))
          _ <- Ns.int.longs_.apply(List(2L)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(List(1L, 2L)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(List(1L), List(2L)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(List(1L, 2L), List(3L)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.longs_.apply(List(1L), List(2L, 3L)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.longs_.apply(List(1L, 2L, 3L)).get.map(_ ==> List(1, 2, 3))


          // AND semantics

          // Set
          _ <- Ns.int.longs_.apply(Set[Long]()).get.map(_ ==> List(4))
          _ <- Ns.int.longs_.apply(Set(1L)).get.map(_ ==> List(1))
          _ <- Ns.int.longs_.apply(Set(2L)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(Set(1L, 2L)).get.map(_ ==> List(1))
          _ <- Ns.int.longs_.apply(Set(1L, 3L)).get.map(_ ==> Nil)
          _ <- Ns.int.longs_.apply(Set(2L, 3L)).get.map(_ ==> List(2))
          _ <- Ns.int.longs_.apply(Set(1L, 2L, 3L)).get.map(_ ==> Nil)

          _ <- Ns.int.longs_.apply(Set(1L, 2L), Set(2L)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(Set(1L, 2L), Set(3L)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.longs_.apply(Set(1L, 2L), Set(4L)).get.map(_ ==> List(1, 3))
          _ <- Ns.int.longs_.apply(Set(1L, 2L), Set(2L), Set(3L)).get.map(_ ==> List(1, 2, 3))

          _ <- Ns.int.longs_.apply(Set(1L, 2L), Set(2L, 3L)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(Set(1L, 2L), Set(2L, 4L)).get.map(_ ==> List(1))
          _ <- Ns.int.longs_.apply(Set(1L, 2L), Set(3L, 4L)).get.map(_ ==> List(1, 3))


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.longs_.apply(1L and 2L).get.map(_ ==> List(1))
          _ <- Ns.int.longs_.apply(1L and 3L).get.map(_ ==> Nil)
        } yield ()
      }


      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[Long]()

        val l1 = List(long1)
        val l2 = List(long2)

        val s1 = Set(long1)
        val s2 = Set(long2)

        val l12 = List(long1, long2)

        val s12 = Set(long1, long2)
        val s23 = Set(long2, long3)

        for {
          _ <- Ns.int.longs$ insert List(
            (1, Some(Set(1L, 2L))),
            (2, Some(Set(2L, 3L))),
            (3, Some(Set(3L, 4L))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.longs_.apply(long1, long2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.longs_.apply(long1 or long2).get.map(_ ==> List(1, 2))

          // Seq
          _ <- Ns.int.longs_.apply(seq0).get.map(_ ==> List(4))
          _ <- Ns.int.longs_.apply(List(long1), List(long2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(l1, l2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(List(long1, long2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(l12).get.map(_ ==> List(1, 2))


          // AND semantics

          // Set
          _ <- Ns.int.longs_.apply(set0).get.map(_ ==> List(4))

          _ <- Ns.int.longs_.apply(Set(long1)).get.map(_ ==> List(1))
          _ <- Ns.int.longs_.apply(s1).get.map(_ ==> List(1))

          _ <- Ns.int.longs_.apply(Set(long2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(s2).get.map(_ ==> List(1, 2))

          _ <- Ns.int.longs_.apply(Set(long1, long2)).get.map(_ ==> List(1))
          _ <- Ns.int.longs_.apply(s12).get.map(_ ==> List(1))

          _ <- Ns.int.longs_.apply(Set(long2, long3)).get.map(_ ==> List(2))
          _ <- Ns.int.longs_.apply(s23).get.map(_ ==> List(2))

          _ <- Ns.int.longs_.apply(Set(long1, long2), Set(long2, long3)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.longs_.apply(s12, s23).get.map(_ ==> List(1, 2))

          // `and`
          _ <- Ns.int.longs_.apply(long1 and long2).get.map(_ ==> List(1))
        } yield ()
      }
    }


    "Implicit conversions Int to Long, card one" - core { implicit conn =>
      val res1 = List(
        (1, 1L),
        (2, 1L),
      )
      val res2 = List((3, 2L))

      val res1t = List(1, 2)
      val res2t = List(3)

      for {
        _ <- Ns.int.long insert List(
          (1, 1),
          (2, 1L),
          (3, long2),
        )

        // Mandatory

        _ <- Ns.int.a1.long(1).get.map(_ ==> res1)
        _ <- Ns.int.a1.long.not(2).get.map(_ ==> res1)
        _ <- Ns.int.a1.long.<(2).get.map(_ ==> res1)
        _ <- Ns.int.a1.long.>(1).get.map(_ ==> res2)
        _ <- Ns.int.a1.long.>=(2).get.map(_ ==> res2)
        _ <- Ns.int.a1.long.<=(1).get.map(_ ==> res1)


        _ <- Ns.int.a1.long(int1).get.map(_ ==> res1)
        _ <- Ns.int.a1.long.not(int2).get.map(_ ==> res1)
        _ <- Ns.int.a1.long.<(int2).get.map(_ ==> res1)
        _ <- Ns.int.a1.long.>(int1).get.map(_ ==> res2)
        _ <- Ns.int.a1.long.>=(int2).get.map(_ ==> res2)
        _ <- Ns.int.a1.long.<=(int1).get.map(_ ==> res1)

        // Tacit

        _ <- Ns.int.a1.long_(1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.long_.not(2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.long_.<(2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.long_.>(1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.long_.>=(2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.long_.<=(1).get.map(_ ==> res1t)


        _ <- Ns.int.a1.long_(int1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.long_.not(int2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.long_.<(int2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.long_.>(int1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.long_.>=(int2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.long_.<=(int1).get.map(_ ==> res1t)
      } yield ()
    }


    "Implicit conversions Int to Long, card many" - core { implicit conn =>
      val res1 = List(
        (1, Set(1L)),
        (2, Set(1L)),
      )
      val res2 = List((3, Set(2L)))

      val res1t = List(1, 2)
      val res2t = List(3)

      for {
        _ <- Ns.int.longs insert List(
          (1, Set(1)),
          (2, Set(1L)),
          (3, Set(long2)),
        )

        // Mandatory

        _ <- Ns.int.a1.longs(1).get.map(_ ==> res1)
        _ <- Ns.int.a1.longs.not(2).get.map(_ ==> res1)
        _ <- Ns.int.a1.longs.<(2).get.map(_ ==> res1)
        _ <- Ns.int.a1.longs.>(1).get.map(_ ==> res2)
        _ <- Ns.int.a1.longs.>=(2).get.map(_ ==> res2)
        _ <- Ns.int.a1.longs.<=(1).get.map(_ ==> res1)


        _ <- Ns.int.a1.longs(int1).get.map(_ ==> res1)
        _ <- Ns.int.a1.longs.not(int2).get.map(_ ==> res1)
        _ <- Ns.int.a1.longs.<(int2).get.map(_ ==> res1)
        _ <- Ns.int.a1.longs.>(int1).get.map(_ ==> res2)
        _ <- Ns.int.a1.longs.>=(int2).get.map(_ ==> res2)
        _ <- Ns.int.a1.longs.<=(int1).get.map(_ ==> res1)

        // Tacit

        _ <- Ns.int.a1.longs_(1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.longs_.not(2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.longs_.<(2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.longs_.>(1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.longs_.>=(2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.longs_.<=(1).get.map(_ ==> res1t)


        _ <- Ns.int.a1.longs_(int1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.longs_.not(int2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.longs_.<(int2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.longs_.>(int1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.longs_.>=(int2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.longs_.<=(int1).get.map(_ ==> res1t)
      } yield ()
    }
  }
}