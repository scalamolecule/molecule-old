package moleculeTests.tests.core.equality

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object ApplyDouble extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.double$ insert List(
            (1, Some(1.0)),
            (2, Some(2.0)),
            (3, Some(3.0)),
            (4, None)
          )
          // Varargs
          _ <- Ns.double.apply(1.0).get.map(_ ==> List(1.0))
          _ <- Ns.double.apply(2.0).get.map(_ ==> List(2.0))
          _ <- Ns.double.apply(1.0, 2.0).a1.get.map(_ ==> List(1.0, 2.0))

          // `or`
          _ <- Ns.double.apply(1.0 or 2.0).a1.get.map(_ ==> List(1.0, 2.0))
          _ <- Ns.double.apply(1.0 or 2.0 or 3.0).a1.get.map(_ ==> List(1.0, 2.0, 3.0))

          // Seq
          _ <- Ns.double.apply().get.map(_ ==> Nil)
          _ <- Ns.double.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.double.apply(List(1.0)).get.map(_ ==> List(1.0))
          _ <- Ns.double.apply(List(2.0)).get.map(_ ==> List(2.0))
          _ <- Ns.double.apply(List(1.0, 2.0)).a1.get.map(_ ==> List(1.0, 2.0))
          _ <- Ns.double.apply(List(1.0), List(2.0)).a1.get.map(_ ==> List(1.0, 2.0))
          _ <- Ns.double.apply(List(1.0, 2.0), List(3.0)).a1.get.map(_ ==> List(1.0, 2.0, 3.0))
          _ <- Ns.double.apply(List(1.0), List(2.0, 3.0)).a1.get.map(_ ==> List(1.0, 2.0, 3.0))
          _ <- Ns.double.apply(List(1.0, 2.0, 3.0)).a1.get.map(_ ==> List(1.0, 2.0, 3.0))
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.double$ insert List(
            (1, Some(1.0)),
            (2, Some(2.0)),
            (3, Some(3.0)),
            (4, None)
          )

          // Varargs
          _ <- Ns.int.double_.apply(1.0).get.map(_ ==> List(1))
          _ <- Ns.int.double_.apply(2.0).get.map(_ ==> List(2))
          _ <- Ns.int.double_.apply(1.0, 2.0).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.double_.apply(1.0 or 2.0).get.map(_ ==> List(1, 2))
          _ <- Ns.int.double_.apply(1.0 or 2.0 or 3.0).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.double_.apply().get.map(_ ==> List(4))
          _ <- Ns.int.double_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.double_.apply(List(1.0)).get.map(_ ==> List(1))
          _ <- Ns.int.double_.apply(List(2.0)).get.map(_ ==> List(2))
          _ <- Ns.int.double_.apply(List(1.0, 2.0)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.double_.apply(List(1.0), List(2.0)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.double_.apply(List(1.0, 2.0), List(3.0)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.double_.apply(List(1.0), List(2.0, 3.0)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.double_.apply(List(1.0, 2.0, 3.0)).get.map(_ ==> List(1, 2, 3))
        } yield ()
      }
    }


    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.doubles$ insert List(
            (1, Some(Set(1.0, 2.0))),
            (2, Some(Set(2.0, 3.0))),
            (3, Some(Set(3.0, 4.0))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.doubles.apply(1.0).get.map(_ ==> List((1, Set(1.0, 2.0))))
          _ <- Ns.int.doubles.apply(2.0).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(1.0, 2.0).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))

          // `or`
          _ <- Ns.int.doubles.apply(1.0 or 2.0).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(1.0 or 2.0 or 3.0).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0))))

          // Seq
          _ <- Ns.int.doubles.apply().get.map(_ ==> Nil)
          _ <- Ns.int.doubles.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.int.doubles.apply(List(1.0)).get.map(_ ==> List((1, Set(1.0, 2.0))))
          _ <- Ns.int.doubles.apply(List(2.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(List(1.0, 2.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(List(1.0), List(2.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(List(1.0, 2.0), List(3.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0))))
          _ <- Ns.int.doubles.apply(List(1.0), List(2.0, 3.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0))))
          _ <- Ns.int.doubles.apply(List(1.0, 2.0, 3.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0))))


          // AND semantics

          // Set
          _ <- Ns.int.doubles.apply(Set[Double]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.int.doubles.apply(Set(1.0)).get.map(_ ==> List((1, Set(1.0, 2.0))))
          _ <- Ns.int.doubles.apply(Set(2.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0)).get.map(_ ==> List((1, Set(1.0, 2.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 3.0)).get.map(_ ==> Nil)
          _ <- Ns.int.doubles.apply(Set(2.0, 3.0)).get.map(_ ==> List((2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0, 3.0)).get.map(_ ==> Nil)

          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set[Double]()).get.map(_ ==> List((1, Set(1.0, 2.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(2.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(3.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(4.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (3, Set(3.0, 4.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(2.0), Set(3.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0))))

          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(2.0, 3.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(2.0, 4.0)).get.map(_ ==> List((1, Set(1.0, 2.0))))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(3.0, 4.0)).get.map(_ ==> List((1, Set(1.0, 2.0)), (3, Set(3.0, 4.0))))

          // `and`
          _ <- Ns.int.doubles.apply(1.0 and 2.0).get.map(_ ==> List((1, Set(1.0, 2.0))))
          _ <- Ns.int.doubles.apply(1.0 and 3.0).get.map(_ ==> Nil)
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.doubles$ insert List(
            (1, Some(Set(1.0, 2.0))),
            (2, Some(Set(2.0, 3.0))),
            (3, Some(Set(3.0, 4.0))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.doubles.apply(1.0).get.map(_ ==> List(Set(1.0, 2.0)))
          _ <- Ns.doubles.apply(2.0).get.map(_ ==> List(Set(1.0, 3.0, 2.0)))
          _ <- Ns.doubles.apply(1.0, 2.0).get.map(_ ==> List(Set(1.0, 3.0, 2.0)))

          // `or`
          _ <- Ns.doubles.apply(1.0 or 2.0).get.map(_ ==> List(Set(1.0, 3.0, 2.0)))
          _ <- Ns.doubles.apply(1.0 or 2.0 or 3.0).get.map(_ ==> List(Set(1.0, 4.0, 3.0, 2.0)))

          // Seq
          _ <- Ns.doubles.apply().get.map(_ ==> Nil)
          _ <- Ns.doubles.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.doubles.apply(List(1.0)).get.map(_ ==> List(Set(1.0, 2.0)))
          _ <- Ns.doubles.apply(List(2.0)).get.map(_ ==> List(Set(1.0, 3.0, 2.0)))
          _ <- Ns.doubles.apply(List(1.0, 2.0)).get.map(_ ==> List(Set(1.0, 3.0, 2.0)))
          _ <- Ns.doubles.apply(List(1.0), List(2.0)).get.map(_ ==> List(Set(1.0, 3.0, 2.0)))
          _ <- Ns.doubles.apply(List(1.0, 2.0), List(3.0)).get.map(_ ==> List(Set(1.0, 4.0, 3.0, 2.0)))
          _ <- Ns.doubles.apply(List(1.0), List(2.0, 3.0)).get.map(_ ==> List(Set(1.0, 4.0, 3.0, 2.0)))
          _ <- Ns.doubles.apply(List(1.0, 2.0, 3.0)).get.map(_ ==> List(Set(1.0, 4.0, 3.0, 2.0)))


          // AND semantics

          // Set
          _ <- Ns.doubles.apply(Set[Double]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.doubles.apply(Set(1.0)).get.map(_ ==> List(Set(1.0, 2.0)))
          _ <- Ns.doubles.apply(Set(2.0)).get.map(_ ==> List(Set(1.0, 3.0, 2.0)))
          _ <- Ns.doubles.apply(Set(1.0, 2.0)).get.map(_ ==> List(Set(1.0, 2.0)))
          _ <- Ns.doubles.apply(Set(1.0, 3.0)).get.map(_ ==> Nil)
          _ <- Ns.doubles.apply(Set(2.0, 3.0)).get.map(_ ==> List(Set(2.0, 3.0)))
          _ <- Ns.doubles.apply(Set(1.0, 2.0, 3.0)).get.map(_ ==> Nil)

          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(2.0)).get.map(_ ==> List(Set(1.0, 2.0, 3.0)))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(3.0)).get.map(_ ==> List(Set(1.0, 2.0, 3.0, 4.0)))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(4.0)).get.map(_ ==> List(Set(1.0, 2.0, 3.0, 4.0)))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(2.0), Set(3.0)).get.map(_ ==> List(Set(1.0, 2.0, 3.0, 4.0)))

          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(2.0, 3.0)).get.map(_ ==> List(Set(1.0, 2.0, 3.0)))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(2.0, 4.0)).get.map(_ ==> List(Set(1.0, 2.0)))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(3.0, 4.0)).get.map(_ ==> List(Set(1.0, 2.0, 3.0, 4.0)))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.doubles.apply(1.0 and 2.0).get.map(_ ==> List(Set(1.0, 2.0)))
          _ <- Ns.doubles.apply(1.0 and 3.0).get.map(_ ==> Nil)
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.doubles$ insert List(
            (1, Some(Set(1.0, 2.0))),
            (2, Some(Set(2.0, 3.0))),
            (3, Some(Set(3.0, 4.0))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.doubles_.apply(1.0).get.map(_ ==> List(1))
          _ <- Ns.int.doubles_.apply(2.0).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(1.0, 2.0).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.doubles_.apply(1.0 or 2.0).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(1.0 or 2.0 or 3.0).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.doubles_.apply().get.map(_ ==> List(4)) // entities with no card-many values asserted
          _ <- Ns.int.doubles_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.doubles_.apply(List(1.0)).get.map(_ ==> List(1))
          _ <- Ns.int.doubles_.apply(List(2.0)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(List(1.0, 2.0)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(List(1.0), List(2.0)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(List(1.0, 2.0), List(3.0)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.doubles_.apply(List(1.0), List(2.0, 3.0)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.doubles_.apply(List(1.0, 2.0, 3.0)).get.map(_ ==> List(1, 2, 3))


          // AND semantics

          // Set
          _ <- Ns.int.doubles_.apply(Set[Double]()).get.map(_ ==> List(4))
          _ <- Ns.int.doubles_.apply(Set(1.0)).get.map(_ ==> List(1))
          _ <- Ns.int.doubles_.apply(Set(2.0)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0)).get.map(_ ==> List(1))
          _ <- Ns.int.doubles_.apply(Set(1.0, 3.0)).get.map(_ ==> Nil)
          _ <- Ns.int.doubles_.apply(Set(2.0, 3.0)).get.map(_ ==> List(2))
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0, 3.0)).get.map(_ ==> Nil)

          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(2.0)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(3.0)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(4.0)).get.map(_ ==> List(1, 3))
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(2.0), Set(3.0)).get.map(_ ==> List(1, 2, 3))

          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(2.0, 3.0)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(2.0, 4.0)).get.map(_ ==> List(1))
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(3.0, 4.0)).get.map(_ ==> List(1, 3))


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.doubles_.apply(1.0 and 2.0).get.map(_ ==> List(1))
          _ <- Ns.int.doubles_.apply(1.0 and 3.0).get.map(_ ==> Nil)
        } yield ()
      }


      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[Double]()

        val l1 = List(double1)
        val l2 = List(double2)

        val s1 = Set(double1)
        val s2 = Set(double2)

        val l12 = List(double1, double2)

        val s12 = Set(double1, double2)
        val s23 = Set(double2, double3)

        for {
          _ <- Ns.int.doubles$ insert List(
            (1, Some(Set(1.0, 2.0))),
            (2, Some(Set(2.0, 3.0))),
            (3, Some(Set(3.0, 4.0))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.doubles_.apply(double1, double2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.doubles_.apply(double1 or double2).get.map(_ ==> List(1, 2))

          // Seq
          _ <- Ns.int.doubles_.apply(seq0).get.map(_ ==> List(4))
          _ <- Ns.int.doubles_.apply(List(double1), List(double2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(l1, l2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(List(double1, double2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(l12).get.map(_ ==> List(1, 2))


          // AND semantics

          // Set
          _ <- Ns.int.doubles_.apply(set0).get.map(_ ==> List(4))

          _ <- Ns.int.doubles_.apply(Set(double1)).get.map(_ ==> List(1))
          _ <- Ns.int.doubles_.apply(s1).get.map(_ ==> List(1))

          _ <- Ns.int.doubles_.apply(Set(double2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(s2).get.map(_ ==> List(1, 2))

          _ <- Ns.int.doubles_.apply(Set(double1, double2)).get.map(_ ==> List(1))
          _ <- Ns.int.doubles_.apply(s12).get.map(_ ==> List(1))

          _ <- Ns.int.doubles_.apply(Set(double2, double3)).get.map(_ ==> List(2))
          _ <- Ns.int.doubles_.apply(s23).get.map(_ ==> List(2))

          _ <- Ns.int.doubles_.apply(Set(double1, double2), Set(double2, double3)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.doubles_.apply(s12, s23).get.map(_ ==> List(1, 2))

          // `and`
          _ <- Ns.int.doubles_.apply(double1 and double2).get.map(_ ==> List(1))
        } yield ()
      }
    }

    "Implicit widening conversions, card one" - core { implicit conn =>
      val res1 = List(
        (1, 1.0),
        (2, 1.0),
        (3, 1.0),
        (4, 1.0),
      )
      val res2 = List((5, 2.0))

      val res1t = List(1, 2, 3, 4)
      val res2t = List(5)

      for {
        _ <- Ns.int.double insert List(
          (1, 1),
          (2, 1L),
          (3, 1f),
          (4, 1.0),
          (5, double2),
        )

        // Mandatory

        _ <- Ns.int.a1.double(1).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.not(2).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.<(2).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.>(1).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.>=(2).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.<=(1).get.map(_ ==> res1)

        _ <- Ns.int.a1.double(1L).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.not(2L).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.<(2L).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.>(1L).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.>=(2L).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.<=(1L).get.map(_ ==> res1)

        _ <- Ns.int.a1.double(1f).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.not(2f).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.<(2f).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.>(1f).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.>=(2f).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.<=(1f).get.map(_ ==> res1)


        _ <- Ns.int.a1.double(int1).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.not(int2).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.<(int2).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.>(int1).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.>=(int2).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.<=(int1).get.map(_ ==> res1)

        // Widening conversion from Long to Double is deprecated because it loses precision.
        // So, we add explicit conversion
        _ <- Ns.int.a1.double(long1.toDouble).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.not(long2.toDouble).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.<(long2.toDouble).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.>(long1.toDouble).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.>=(long2.toDouble).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.<=(long1.toDouble).get.map(_ ==> res1)

        _ <- Ns.int.a1.double(float1).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.not(float2).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.<(float2).get.map(_ ==> res1)
        _ <- Ns.int.a1.double.>(float1).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.>=(float2).get.map(_ ==> res2)
        _ <- Ns.int.a1.double.<=(float1).get.map(_ ==> res1)

        // Tacit

        _ <- Ns.int.a1.double_(1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.not(2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.<(2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.>(1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.>=(2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.<=(1).get.map(_ ==> res1t)

        _ <- Ns.int.a1.double_(1L).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.not(2L).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.<(2L).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.>(1L).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.>=(2L).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.<=(1L).get.map(_ ==> res1t)

        _ <- Ns.int.a1.double_(1f).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.not(2f).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.<(2f).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.>(1f).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.>=(2f).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.<=(1f).get.map(_ ==> res1t)


        _ <- Ns.int.a1.double_(int1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.not(int2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.<(int2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.>(int1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.>=(int2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.<=(int1).get.map(_ ==> res1t)

        _ <- Ns.int.a1.double_(long1.toDouble).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.not(long2.toDouble).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.<(long2.toDouble).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.>(long1.toDouble).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.>=(long2.toDouble).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.<=(long1.toDouble).get.map(_ ==> res1t)

        _ <- Ns.int.a1.double_(float1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.not(float2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.<(float2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.double_.>(float1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.>=(float2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.double_.<=(float1).get.map(_ ==> res1t)
      } yield ()
    }

    "Implicit widening conversions, card many" - core { implicit conn =>
      val res1 = List(
        (1, Set(1.0)),
        (2, Set(1.0)),
        (3, Set(1.0)),
        (4, Set(1.0)),
      )
      val res2 = List((5, Set(2.0)))

      val res1t = List(1, 2, 3, 4)
      val res2t = List(5)

      for {
        _ <- Ns.int.doubles insert List(
          (1, Set(1)),
          (2, Set(1L)),
          (3, Set(1f)),
          (4, Set(1.0)),
          (5, Set(double2))
        )

        // Mandatory

        _ <- Ns.int.a1.doubles(1).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.not(2).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.<(2).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.>(1).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.>=(2).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.<=(1).get.map(_ ==> res1)

        _ <- Ns.int.a1.doubles(1L).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.not(2L).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.<(2L).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.>(1L).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.>=(2L).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.<=(1L).get.map(_ ==> res1)

        _ <- Ns.int.a1.doubles(1f).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.not(2f).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.<(2f).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.>(1f).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.>=(2f).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.<=(1f).get.map(_ ==> res1)


        _ <- Ns.int.a1.doubles(int1).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.not(int2).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.<(int2).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.>(int1).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.>=(int2).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.<=(int1).get.map(_ ==> res1)

        _ <- Ns.int.a1.doubles(long1.toDouble).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.not(long2.toDouble).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.<(long2.toDouble).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.>(long1.toDouble).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.>=(long2.toDouble).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.<=(long1.toDouble).get.map(_ ==> res1)

        _ <- Ns.int.a1.doubles(float1).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.not(float2).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.<(float2).get.map(_ ==> res1)
        _ <- Ns.int.a1.doubles.>(float1).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.>=(float2).get.map(_ ==> res2)
        _ <- Ns.int.a1.doubles.<=(float1).get.map(_ ==> res1)

        // Tacit

        _ <- Ns.int.a1.doubles_(1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.not(2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.<(2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.>(1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.>=(2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.<=(1).get.map(_ ==> res1t)

        _ <- Ns.int.a1.doubles_(1L).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.not(2L).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.<(2L).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.>(1L).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.>=(2L).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.<=(1L).get.map(_ ==> res1t)

        _ <- Ns.int.a1.doubles_(1f).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.not(2f).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.<(2f).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.>(1f).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.>=(2f).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.<=(1f).get.map(_ ==> res1t)


        _ <- Ns.int.a1.doubles_(int1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.not(int2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.<(int2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.>(int1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.>=(int2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.<=(int1).get.map(_ ==> res1t)

        _ <- Ns.int.a1.doubles_(long1.toDouble).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.not(long2.toDouble).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.<(long2.toDouble).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.>(long1.toDouble).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.>=(long2.toDouble).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.<=(long1.toDouble).get.map(_ ==> res1t)

        _ <- Ns.int.a1.doubles_(float1).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.not(float2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.<(float2).get.map(_ ==> res1t)
        _ <- Ns.int.a1.doubles_.>(float1).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.>=(float2).get.map(_ ==> res2t)
        _ <- Ns.int.a1.doubles_.<=(float1).get.map(_ ==> res1t)
      } yield ()
    }
  }
}