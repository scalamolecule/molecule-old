package molecule.tests.core.equality

import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out4._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

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
          _ <- Ns.double.apply(1.0).get === List(1.0)
          _ <- Ns.double.apply(2.0).get === List(2.0)
          _ <- Ns.double.apply(1.0, 2.0).get.map(_.sorted ==> List(1.0, 2.0))

          // `or`
          _ <- Ns.double.apply(1.0 or 2.0).get.map(_.sorted ==> List(1.0, 2.0))
          _ <- Ns.double.apply(1.0 or 2.0 or 3.0).get.map(_.sorted ==> List(1.0, 2.0, 3.0))

          // Seq
          _ <- Ns.double.apply().get === Nil
          _ <- Ns.double.apply(Nil).get === Nil
          _ <- Ns.double.apply(List(1.0)).get === List(1.0)
          _ <- Ns.double.apply(List(2.0)).get === List(2.0)
          _ <- Ns.double.apply(List(1.0, 2.0)).get.map(_.sorted ==> List(1.0, 2.0))
          _ <- Ns.double.apply(List(1.0), List(2.0)).get.map(_.sorted ==> List(1.0, 2.0))
          _ <- Ns.double.apply(List(1.0, 2.0), List(3.0)).get.map(_.sorted ==> List(1.0, 2.0, 3.0))
          _ <- Ns.double.apply(List(1.0), List(2.0, 3.0)).get.map(_.sorted ==> List(1.0, 2.0, 3.0))
          _ <- Ns.double.apply(List(1.0, 2.0, 3.0)).get.map(_.sorted ==> List(1.0, 2.0, 3.0))
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
          _ <- Ns.int.double_.apply(1.0).get === List(1)
          _ <- Ns.int.double_.apply(2.0).get === List(2)
          _ <- Ns.int.double_.apply(1.0, 2.0).get === List(1, 2)

          // `or`
          _ <- Ns.int.double_.apply(1.0 or 2.0).get === List(1, 2)
          _ <- Ns.int.double_.apply(1.0 or 2.0 or 3.0).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.double_.apply().get === List(4)
          _ <- Ns.int.double_.apply(Nil).get === List(4)
          _ <- Ns.int.double_.apply(List(1.0)).get === List(1)
          _ <- Ns.int.double_.apply(List(2.0)).get === List(2)
          _ <- Ns.int.double_.apply(List(1.0, 2.0)).get === List(1, 2)
          _ <- Ns.int.double_.apply(List(1.0), List(2.0)).get === List(1, 2)
          _ <- Ns.int.double_.apply(List(1.0, 2.0), List(3.0)).get === List(1, 2, 3)
          _ <- Ns.int.double_.apply(List(1.0), List(2.0, 3.0)).get === List(1, 2, 3)
          _ <- Ns.int.double_.apply(List(1.0, 2.0, 3.0)).get === List(1, 2, 3)
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
          _ <- Ns.int.doubles.apply(1.0).get === List((1, Set(1.0, 2.0)))
          _ <- Ns.int.doubles.apply(2.0).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(1.0, 2.0).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))

          // `or`
          _ <- Ns.int.doubles.apply(1.0 or 2.0).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(1.0 or 2.0 or 3.0).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0)))

          // Seq
          _ <- Ns.int.doubles.apply().get === Nil
          _ <- Ns.int.doubles.apply(Nil).get === Nil
          _ <- Ns.int.doubles.apply(List(1.0)).get === List((1, Set(1.0, 2.0)))
          _ <- Ns.int.doubles.apply(List(2.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(List(1.0, 2.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(List(1.0), List(2.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(List(1.0, 2.0), List(3.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0)))
          _ <- Ns.int.doubles.apply(List(1.0), List(2.0, 3.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0)))
          _ <- Ns.int.doubles.apply(List(1.0, 2.0, 3.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0)))


          // AND semantics

          // Set
          _ <- Ns.int.doubles.apply(Set[Double]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.int.doubles.apply(Set(1.0)).get === List((1, Set(1.0, 2.0)))
          _ <- Ns.int.doubles.apply(Set(2.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0)).get === List((1, Set(1.0, 2.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 3.0)).get === Nil
          _ <- Ns.int.doubles.apply(Set(2.0, 3.0)).get === List((2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0, 3.0)).get === Nil

          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set[Double]()).get === List((1, Set(1.0, 2.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(2.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(3.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(4.0)).get === List((1, Set(1.0, 2.0)), (3, Set(3.0, 4.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(2.0), Set(3.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)), (3, Set(3.0, 4.0)))

          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(2.0, 3.0)).get === List((1, Set(1.0, 2.0)), (2, Set(2.0, 3.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(2.0, 4.0)).get === List((1, Set(1.0, 2.0)))
          _ <- Ns.int.doubles.apply(Set(1.0, 2.0), Set(3.0, 4.0)).get === List((1, Set(1.0, 2.0)), (3, Set(3.0, 4.0)))

          // `and`
          _ <- Ns.int.doubles.apply(1.0 and 2.0).get === List((1, Set(1.0, 2.0)))
          _ <- Ns.int.doubles.apply(1.0 and 3.0).get === Nil
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
          _ <- Ns.doubles.apply(1.0).get === List(Set(1.0, 2.0))
          _ <- Ns.doubles.apply(2.0).get === List(Set(1.0, 3.0, 2.0))
          _ <- Ns.doubles.apply(1.0, 2.0).get === List(Set(1.0, 3.0, 2.0))

          // `or`
          _ <- Ns.doubles.apply(1.0 or 2.0).get === List(Set(1.0, 3.0, 2.0))
          _ <- Ns.doubles.apply(1.0 or 2.0 or 3.0).get === List(Set(1.0, 4.0, 3.0, 2.0))

          // Seq
          _ <- Ns.doubles.apply().get === Nil
          _ <- Ns.doubles.apply(Nil).get === Nil
          _ <- Ns.doubles.apply(List(1.0)).get === List(Set(1.0, 2.0))
          _ <- Ns.doubles.apply(List(2.0)).get === List(Set(1.0, 3.0, 2.0))
          _ <- Ns.doubles.apply(List(1.0, 2.0)).get === List(Set(1.0, 3.0, 2.0))
          _ <- Ns.doubles.apply(List(1.0), List(2.0)).get === List(Set(1.0, 3.0, 2.0))
          _ <- Ns.doubles.apply(List(1.0, 2.0), List(3.0)).get === List(Set(1.0, 4.0, 3.0, 2.0))
          _ <- Ns.doubles.apply(List(1.0), List(2.0, 3.0)).get === List(Set(1.0, 4.0, 3.0, 2.0))
          _ <- Ns.doubles.apply(List(1.0, 2.0, 3.0)).get === List(Set(1.0, 4.0, 3.0, 2.0))


          // AND semantics

          // Set
          _ <- Ns.doubles.apply(Set[Double]()).get === Nil // entities with no card-many values asserted can't also return values
          _ <- Ns.doubles.apply(Set(1.0)).get === List(Set(1.0, 2.0))
          _ <- Ns.doubles.apply(Set(2.0)).get === List(Set(1.0, 3.0, 2.0))
          _ <- Ns.doubles.apply(Set(1.0, 2.0)).get === List(Set(1.0, 2.0))
          _ <- Ns.doubles.apply(Set(1.0, 3.0)).get === Nil
          _ <- Ns.doubles.apply(Set(2.0, 3.0)).get === List(Set(2.0, 3.0))
          _ <- Ns.doubles.apply(Set(1.0, 2.0, 3.0)).get === Nil

          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(2.0)).get === List(Set(1.0, 2.0, 3.0))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(3.0)).get === List(Set(1.0, 2.0, 3.0, 4.0))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(4.0)).get === List(Set(1.0, 2.0, 3.0, 4.0))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(2.0), Set(3.0)).get === List(Set(1.0, 2.0, 3.0, 4.0))

          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(2.0, 3.0)).get === List(Set(1.0, 2.0, 3.0))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(2.0, 4.0)).get === List(Set(1.0, 2.0))
          _ <- Ns.doubles.apply(Set(1.0, 2.0), Set(3.0, 4.0)).get === List(Set(1.0, 2.0, 3.0, 4.0))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.doubles.apply(1.0 and 2.0).get === List(Set(1.0, 2.0))
          _ <- Ns.doubles.apply(1.0 and 3.0).get === Nil
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
          _ <- Ns.int.doubles_.apply(1.0).get === List(1)
          _ <- Ns.int.doubles_.apply(2.0).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(1.0, 2.0).get === List(1, 2)

          // `or`
          _ <- Ns.int.doubles_.apply(1.0 or 2.0).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(1.0 or 2.0 or 3.0).get === List(1, 2, 3)

          // Seq
          _ <- Ns.int.doubles_.apply().get === List(4) // entities with no card-many values asserted
          _ <- Ns.int.doubles_.apply(Nil).get === List(4)
          _ <- Ns.int.doubles_.apply(List(1.0)).get === List(1)
          _ <- Ns.int.doubles_.apply(List(2.0)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(List(1.0, 2.0)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(List(1.0), List(2.0)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(List(1.0, 2.0), List(3.0)).get === List(1, 2, 3)
          _ <- Ns.int.doubles_.apply(List(1.0), List(2.0, 3.0)).get === List(1, 2, 3)
          _ <- Ns.int.doubles_.apply(List(1.0, 2.0, 3.0)).get === List(1, 2, 3)


          // AND semantics

          // Set
          _ <- Ns.int.doubles_.apply(Set[Double]()).get === List(4)
          _ <- Ns.int.doubles_.apply(Set(1.0)).get === List(1)
          _ <- Ns.int.doubles_.apply(Set(2.0)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0)).get === List(1)
          _ <- Ns.int.doubles_.apply(Set(1.0, 3.0)).get === Nil
          _ <- Ns.int.doubles_.apply(Set(2.0, 3.0)).get === List(2)
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0, 3.0)).get === Nil

          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(2.0)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(3.0)).get === List(1, 2, 3)
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(4.0)).get === List(1, 3)
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(2.0), Set(3.0)).get === List(1, 2, 3)

          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(2.0, 3.0)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(2.0, 4.0)).get === List(1)
          _ <- Ns.int.doubles_.apply(Set(1.0, 2.0), Set(3.0, 4.0)).get === List(1, 3)


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.doubles_.apply(1.0 and 2.0).get === List(1)
          _ <- Ns.int.doubles_.apply(1.0 and 3.0).get === Nil
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
        val l23 = List(double2, double3)

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
          _ <- Ns.int.doubles_.apply(double1, double2).get === List(1, 2)

          // `or`
          _ <- Ns.int.doubles_.apply(double1 or double2).get === List(1, 2)

          // Seq
          _ <- Ns.int.doubles_.apply(seq0).get === List(4)
          _ <- Ns.int.doubles_.apply(List(double1), List(double2)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(l1, l2).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(List(double1, double2)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(l12).get === List(1, 2)


          // AND semantics

          // Set
          _ <- Ns.int.doubles_.apply(set0).get === List(4)

          _ <- Ns.int.doubles_.apply(Set(double1)).get === List(1)
          _ <- Ns.int.doubles_.apply(s1).get === List(1)

          _ <- Ns.int.doubles_.apply(Set(double2)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(s2).get === List(1, 2)

          _ <- Ns.int.doubles_.apply(Set(double1, double2)).get === List(1)
          _ <- Ns.int.doubles_.apply(s12).get === List(1)

          _ <- Ns.int.doubles_.apply(Set(double2, double3)).get === List(2)
          _ <- Ns.int.doubles_.apply(s23).get === List(2)

          _ <- Ns.int.doubles_.apply(Set(double1, double2), Set(double2, double3)).get === List(1, 2)
          _ <- Ns.int.doubles_.apply(s12, s23).get === List(1, 2)

          // `and`
          _ <- Ns.int.doubles_.apply(double1 and double2).get === List(1)
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

        _ <- Ns.int.double(1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.not(2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.<(2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.>(1).get === res2
        _ <- Ns.int.double.>=(2).get === res2
        _ <- Ns.int.double.<=(1).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.double(1L).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.not(2L).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.<(2L).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.>(1L).get === res2
        _ <- Ns.int.double.>=(2L).get === res2
        _ <- Ns.int.double.<=(1L).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.double(1f).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.not(2f).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.<(2f).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.>(1f).get === res2
        _ <- Ns.int.double.>=(2f).get === res2
        _ <- Ns.int.double.<=(1f).get.map(_.sortBy(_._1) ==> res1)


        _ <- Ns.int.double(int1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.not(int2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.<(int2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.>(int1).get === res2
        _ <- Ns.int.double.>=(int2).get === res2
        _ <- Ns.int.double.<=(int1).get.map(_.sortBy(_._1) ==> res1)

        // Widening conversion from Long to Double is deprecated because it loses precision
        _ <- Ns.int.double(long1.toDouble).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.not(long2.toDouble).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.<(long2.toDouble).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.>(long1.toDouble).get === res2
        _ <- Ns.int.double.>=(long2.toDouble).get === res2
        _ <- Ns.int.double.<=(long1.toDouble).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.double(float1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.not(float2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.<(float2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.double.>(float1).get === res2
        _ <- Ns.int.double.>=(float2).get === res2
        _ <- Ns.int.double.<=(float1).get.map(_.sortBy(_._1) ==> res1)

        // Tacit

        _ <- Ns.int.double_(1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.not(2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.<(2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.>(1).get === res2t
        _ <- Ns.int.double_.>=(2).get === res2t
        _ <- Ns.int.double_.<=(1).get.map(_.sorted ==> res1t)

        _ <- Ns.int.double_(1L).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.not(2L).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.<(2L).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.>(1L).get === res2t
        _ <- Ns.int.double_.>=(2L).get === res2t
        _ <- Ns.int.double_.<=(1L).get.map(_.sorted ==> res1t)

        _ <- Ns.int.double_(1f).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.not(2f).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.<(2f).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.>(1f).get === res2t
        _ <- Ns.int.double_.>=(2f).get === res2t
        _ <- Ns.int.double_.<=(1f).get.map(_.sorted ==> res1t)


        _ <- Ns.int.double_(int1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.not(int2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.<(int2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.>(int1).get === res2t
        _ <- Ns.int.double_.>=(int2).get === res2t
        _ <- Ns.int.double_.<=(int1).get.map(_.sorted ==> res1t)

        _ <- Ns.int.double_(long1.toDouble).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.not(long2.toDouble).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.<(long2.toDouble).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.>(long1.toDouble).get === res2t
        _ <- Ns.int.double_.>=(long2.toDouble).get === res2t
        _ <- Ns.int.double_.<=(long1.toDouble).get.map(_.sorted ==> res1t)

        _ <- Ns.int.double_(float1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.not(float2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.<(float2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.double_.>(float1).get === res2t
        _ <- Ns.int.double_.>=(float2).get === res2t
        _ <- Ns.int.double_.<=(float1).get.map(_.sorted ==> res1t)
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
          (5, Set(double2)),
        )

        // Mandatory

        _ <- Ns.int.doubles(1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.not(2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.<(2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.>(1).get === res2
        _ <- Ns.int.doubles.>=(2).get === res2
        _ <- Ns.int.doubles.<=(1).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.doubles(1L).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.not(2L).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.<(2L).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.>(1L).get === res2
        _ <- Ns.int.doubles.>=(2L).get === res2
        _ <- Ns.int.doubles.<=(1L).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.doubles(1f).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.not(2f).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.<(2f).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.>(1f).get === res2
        _ <- Ns.int.doubles.>=(2f).get === res2
        _ <- Ns.int.doubles.<=(1f).get.map(_.sortBy(_._1) ==> res1)


        _ <- Ns.int.doubles(int1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.not(int2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.<(int2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.>(int1).get === res2
        _ <- Ns.int.doubles.>=(int2).get === res2
        _ <- Ns.int.doubles.<=(int1).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.doubles(long1.toDouble).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.not(long2.toDouble).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.<(long2.toDouble).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.>(long1.toDouble).get === res2
        _ <- Ns.int.doubles.>=(long2.toDouble).get === res2
        _ <- Ns.int.doubles.<=(long1.toDouble).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.doubles(float1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.not(float2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.<(float2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.doubles.>(float1).get === res2
        _ <- Ns.int.doubles.>=(float2).get === res2
        _ <- Ns.int.doubles.<=(float1).get.map(_.sortBy(_._1) ==> res1)

        // Tacit

        _ <- Ns.int.doubles_(1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.not(2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.<(2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.>(1).get === res2t
        _ <- Ns.int.doubles_.>=(2).get === res2t
        _ <- Ns.int.doubles_.<=(1).get.map(_.sorted ==> res1t)

        _ <- Ns.int.doubles_(1L).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.not(2L).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.<(2L).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.>(1L).get === res2t
        _ <- Ns.int.doubles_.>=(2L).get === res2t
        _ <- Ns.int.doubles_.<=(1L).get.map(_.sorted ==> res1t)

        _ <- Ns.int.doubles_(1f).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.not(2f).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.<(2f).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.>(1f).get === res2t
        _ <- Ns.int.doubles_.>=(2f).get === res2t
        _ <- Ns.int.doubles_.<=(1f).get.map(_.sorted ==> res1t)


        _ <- Ns.int.doubles_(int1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.not(int2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.<(int2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.>(int1).get === res2t
        _ <- Ns.int.doubles_.>=(int2).get === res2t
        _ <- Ns.int.doubles_.<=(int1).get.map(_.sorted ==> res1t)

        _ <- Ns.int.doubles_(long1.toDouble).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.not(long2.toDouble).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.<(long2.toDouble).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.>(long1.toDouble).get === res2t
        _ <- Ns.int.doubles_.>=(long2.toDouble).get === res2t
        _ <- Ns.int.doubles_.<=(long1.toDouble).get.map(_.sorted ==> res1t)

        _ <- Ns.int.doubles_(float1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.not(float2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.<(float2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.doubles_.>(float1).get === res2t
        _ <- Ns.int.doubles_.>=(float2).get === res2t
        _ <- Ns.int.doubles_.<=(float1).get.map(_.sorted ==> res1t)
      } yield ()
    }
  }
}