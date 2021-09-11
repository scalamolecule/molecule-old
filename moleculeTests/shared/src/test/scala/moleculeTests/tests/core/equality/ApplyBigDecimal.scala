package moleculeTests.tests.core.equality

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object ApplyBigDecimal extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.bigDec$ insert List(
            (1, Some(bigDec1)),
            (2, Some(bigDec2)),
            (3, Some(bigDec3)),
            (4, None)
          )

          // Varargs
          _ <- Ns.bigDec.apply(bigDec1).get.map(_ ==> List(bigDec1))
          _ <- Ns.bigDec.apply(bigDec2).get.map(_ ==> List(bigDec2))
          _ <- Ns.bigDec.apply(bigDec1, bigDec2).get.map(_ ==> List(bigDec1, bigDec2))

          // `or`
          _ <- Ns.bigDec.apply(bigDec1 or bigDec2).get.map(_ ==> List(bigDec1, bigDec2))
          _ <- Ns.bigDec.apply(bigDec1 or bigDec2 or bigDec3).get.map(_ ==> List(bigDec3, bigDec1, bigDec2))

          // Seq
          _ <- Ns.bigDec.apply().get.map(_ ==> Nil)
          _ <- Ns.bigDec.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.bigDec.apply(List(bigDec1)).get.map(_ ==> List(bigDec1))
          _ <- Ns.bigDec.apply(List(bigDec2)).get.map(_ ==> List(bigDec2))
          _ <- Ns.bigDec.apply(List(bigDec1, bigDec2)).get.map(_ ==> List(bigDec1, bigDec2))
          _ <- Ns.bigDec.apply(List(bigDec1), List(bigDec2)).get.map(_ ==> List(bigDec1, bigDec2))
          _ <- Ns.bigDec.apply(List(bigDec1, bigDec2), List(bigDec3)).get.map(_ ==> List(bigDec3, bigDec1, bigDec2))
          _ <- Ns.bigDec.apply(List(bigDec1), List(bigDec2, bigDec3)).get.map(_ ==> List(bigDec3, bigDec1, bigDec2))
          _ <- Ns.bigDec.apply(List(bigDec1, bigDec2, bigDec3)).get.map(_ ==> List(bigDec3, bigDec1, bigDec2))
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.bigDec$ insert List(
            (1, Some(bigDec1)),
            (2, Some(bigDec2)),
            (3, Some(bigDec3)),
            (4, None)
          )
          // Varargs
          _ <- Ns.int.bigDec_.apply(bigDec1).get.map(_ ==> List(1))
          _ <- Ns.int.bigDec_.apply(bigDec2).get.map(_ ==> List(2))
          _ <- Ns.int.bigDec_.apply(bigDec1, bigDec2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.bigDec_.apply(bigDec1 or bigDec2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.bigDec_.apply(bigDec1 or bigDec2 or bigDec3).get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.bigDec_.apply().get.map(_ ==> List(4))
          _ <- Ns.int.bigDec_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.bigDec_.apply(List(bigDec1)).get.map(_ ==> List(1))
          _ <- Ns.int.bigDec_.apply(List(bigDec2)).get.map(_ ==> List(2))
          _ <- Ns.int.bigDec_.apply(List(bigDec1, bigDec2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.bigDec_.apply(List(bigDec1), List(bigDec2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.bigDec_.apply(List(bigDec1, bigDec2), List(bigDec3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.bigDec_.apply(List(bigDec1), List(bigDec2, bigDec3)).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.bigDec_.apply(List(bigDec1, bigDec2, bigDec3)).get.map(_ ==> List(1, 2, 3))
        } yield ()
      }


      "Card many" - {

        "Mandatory" - core { implicit conn =>
          for {
            _ <- Ns.int.bigDecs$ insert List(
              (1, Some(Set(bigDec1, bigDec2))),
              (2, Some(Set(bigDec2, bigDec3))),
              (3, Some(Set(bigDec3, bigDec4))),
              (4, None)
            )

            // OR semantics

            // Varargs
            _ <- Ns.int.bigDecs.apply(bigDec1).get.map(_ ==> List((1, Set(bigDec1, bigDec2))))
            _ <- Ns.int.bigDecs.apply(bigDec2).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(bigDec1, bigDec2).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))

            // `or`
            _ <- Ns.int.bigDecs.apply(bigDec1 or bigDec2).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(bigDec1 or bigDec2 or bigDec3).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3)), (3, Set(bigDec3, bigDec4))))

            // Seq
            _ <- Ns.int.bigDecs.apply().get.map(_ ==> Nil)
            _ <- Ns.int.bigDecs.apply(Nil).get.map(_ ==> Nil)
            _ <- Ns.int.bigDecs.apply(List(bigDec1)).get.map(_ ==> List((1, Set(bigDec1, bigDec2))))
            _ <- Ns.int.bigDecs.apply(List(bigDec2)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(List(bigDec1, bigDec2)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(List(bigDec1), List(bigDec2)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(List(bigDec1, bigDec2), List(bigDec3)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3)), (3, Set(bigDec3, bigDec4))))
            _ <- Ns.int.bigDecs.apply(List(bigDec1), List(bigDec2, bigDec3)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3)), (3, Set(bigDec3, bigDec4))))
            _ <- Ns.int.bigDecs.apply(List(bigDec1, bigDec2, bigDec3)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3)), (3, Set(bigDec3, bigDec4))))


            // AND semantics

            // Set
            _ <- Ns.int.bigDecs.apply(Set[BigDecimal]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
            _ <- Ns.int.bigDecs.apply(Set(bigDec1)).get.map(_ ==> List((1, Set(bigDec1, bigDec2))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec2)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2)).get.map(_ ==> List((1, Set(bigDec1, bigDec2))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec3)).get.map(_ ==> Nil)
            _ <- Ns.int.bigDecs.apply(Set(bigDec2, bigDec3)).get.map(_ ==> List((2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2, bigDec3)).get.map(_ ==> Nil)

            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2), Set[BigDecimal]()).get.map(_ ==> List((1, Set(bigDec1, bigDec2))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec2)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec3)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3)), (3, Set(bigDec3, bigDec4))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec4)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (3, Set(bigDec3, bigDec4))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec2), Set(bigDec3)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3)), (3, Set(bigDec3, bigDec4))))

            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (2, Set(bigDec2, bigDec3))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec2, bigDec4)).get.map(_ ==> List((1, Set(bigDec1, bigDec2))))
            _ <- Ns.int.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4)).get.map(_ ==> List((1, Set(bigDec1, bigDec2)), (3, Set(bigDec3, bigDec4))))

            // `and`
            _ <- Ns.int.bigDecs.apply(bigDec1 and bigDec2).get.map(_ ==> List((1, Set(bigDec1, bigDec2))))
            _ <- Ns.int.bigDecs.apply(bigDec1 and bigDec3).get.map(_ ==> Nil)
          } yield ()
        }

        "Mandatory, single attr coalesce" - core { implicit conn =>
          for {
            _ <- Ns.int.bigDecs$ insert List(
              (1, Some(Set(bigDec1, bigDec2))),
              (2, Some(Set(bigDec2, bigDec3))),
              (3, Some(Set(bigDec3, bigDec4))),
              (4, None)
            )

            // OR semantics

            // Varargs
            _ <- Ns.bigDecs.apply(bigDec1).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- Ns.bigDecs.apply(bigDec2).get.map(_ ==> List(Set(bigDec1, bigDec3, bigDec2)))
            _ <- Ns.bigDecs.apply(bigDec1, bigDec2).get.map(_ ==> List(Set(bigDec1, bigDec3, bigDec2)))

            // `or`
            _ <- Ns.bigDecs.apply(bigDec1 or bigDec2).get.map(_ ==> List(Set(bigDec1, bigDec3, bigDec2)))
            _ <- Ns.bigDecs.apply(bigDec1 or bigDec2 or bigDec3).get.map(_ ==> List(Set(bigDec1, bigDec4, bigDec3, bigDec2)))

            // Seq
            _ <- Ns.bigDecs.apply().get.map(_ ==> Nil)
            _ <- Ns.bigDecs.apply(Nil).get.map(_ ==> Nil)
            _ <- Ns.bigDecs.apply(List(bigDec1)).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- Ns.bigDecs.apply(List(bigDec2)).get.map(_ ==> List(Set(bigDec1, bigDec3, bigDec2)))
            _ <- Ns.bigDecs.apply(List(bigDec1, bigDec2)).get.map(_ ==> List(Set(bigDec1, bigDec3, bigDec2)))
            _ <- Ns.bigDecs.apply(List(bigDec1), List(bigDec2)).get.map(_ ==> List(Set(bigDec1, bigDec3, bigDec2)))
            _ <- Ns.bigDecs.apply(List(bigDec1, bigDec2), List(bigDec3)).get.map(_ ==> List(Set(bigDec1, bigDec4, bigDec3, bigDec2)))
            _ <- Ns.bigDecs.apply(List(bigDec1), List(bigDec2, bigDec3)).get.map(_ ==> List(Set(bigDec1, bigDec4, bigDec3, bigDec2)))
            _ <- Ns.bigDecs.apply(List(bigDec1, bigDec2, bigDec3)).get.map(_ ==> List(Set(bigDec1, bigDec4, bigDec3, bigDec2)))


            // AND semantics

            // Set
            _ <- Ns.bigDecs.apply(Set[BigDecimal]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
            _ <- Ns.bigDecs.apply(Set(bigDec1)).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- Ns.bigDecs.apply(Set(bigDec2)).get.map(_ ==> List(Set(bigDec1, bigDec3, bigDec2)))
            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2)).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec3)).get.map(_ ==> Nil)
            _ <- Ns.bigDecs.apply(Set(bigDec2, bigDec3)).get.map(_ ==> List(Set(bigDec2, bigDec3)))
            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2, bigDec3)).get.map(_ ==> Nil)

            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec2)).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3)))
            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec3)).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4)))
            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec4)).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4)))
            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec2), Set(bigDec3)).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4)))

            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3)))
            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec2, bigDec4)).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- Ns.bigDecs.apply(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4)).get.map(_ ==> List(Set(bigDec1, bigDec2, bigDec3, bigDec4)))


            // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
            _ <- Ns.bigDecs.apply(bigDec1 and bigDec2).get.map(_ ==> List(Set(bigDec1, bigDec2)))
            _ <- Ns.bigDecs.apply(bigDec1 and bigDec3).get.map(_ ==> Nil)
          } yield ()
        }

        "Tacit" - core { implicit conn =>
          for {
            _ <- Ns.int.bigDecs$ insert List(
              (1, Some(Set(bigDec1, bigDec2))),
              (2, Some(Set(bigDec2, bigDec3))),
              (3, Some(Set(bigDec3, bigDec4))),
              (4, None)
            )

            // OR semantics

            // Varargs
            _ <- Ns.int.bigDecs_.apply(bigDec1).get.map(_ ==> List(1))
            _ <- Ns.int.bigDecs_.apply(bigDec2).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(bigDec1, bigDec2).get.map(_ ==> List(1, 2))

            // `or`
            _ <- Ns.int.bigDecs_.apply(bigDec1 or bigDec2).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(bigDec1 or bigDec2 or bigDec3).get.map(_ ==> List(1, 2, 3))

            // Seq
            _ <- Ns.int.bigDecs_.apply().get.map(_ ==> List(4)) // entities with no card-many values asserted
            _ <- Ns.int.bigDecs_.apply(Nil).get.map(_ ==> List(4))
            _ <- Ns.int.bigDecs_.apply(List(bigDec1)).get.map(_ ==> List(1))
            _ <- Ns.int.bigDecs_.apply(List(bigDec2)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(List(bigDec1, bigDec2)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(List(bigDec1), List(bigDec2)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(List(bigDec1, bigDec2), List(bigDec3)).get.map(_ ==> List(1, 2, 3))
            _ <- Ns.int.bigDecs_.apply(List(bigDec1), List(bigDec2, bigDec3)).get.map(_ ==> List(1, 2, 3))
            _ <- Ns.int.bigDecs_.apply(List(bigDec1, bigDec2, bigDec3)).get.map(_ ==> List(1, 2, 3))


            // AND semantics

            // Set
            _ <- Ns.int.bigDecs_.apply(Set[BigDecimal]()).get.map(_ ==> List(4))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1)).get.map(_ ==> List(1))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec2)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2)).get.map(_ ==> List(1))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec3)).get.map(_ ==> Nil)
            _ <- Ns.int.bigDecs_.apply(Set(bigDec2, bigDec3)).get.map(_ ==> List(2))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2, bigDec3)).get.map(_ ==> Nil)

            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2), Set(bigDec2)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2), Set(bigDec3)).get.map(_ ==> List(1, 2, 3))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2), Set(bigDec4)).get.map(_ ==> List(1, 3))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2), Set(bigDec2), Set(bigDec3)).get.map(_ ==> List(1, 2, 3))

            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2), Set(bigDec2, bigDec4)).get.map(_ ==> List(1))
            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2), Set(bigDec3, bigDec4)).get.map(_ ==> List(1, 3))


            // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
            _ <- Ns.int.bigDecs_.apply(bigDec1 and bigDec2).get.map(_ ==> List(1))
            _ <- Ns.int.bigDecs_.apply(bigDec1 and bigDec3).get.map(_ ==> Nil)
          } yield ()
        }

        "Variable resolution" - core { implicit conn =>
          val seq0 = Nil
          val set0 = Set[BigDecimal]()

          val l1 = List(bigDec1)
          val l2 = List(bigDec2)

          val s1 = Set(bigDec1)
          val s2 = Set(bigDec2)

          val l12 = List(bigDec1, bigDec2)
          val l23 = List(bigDec2, bigDec3)

          val s12 = Set(bigDec1, bigDec2)
          val s23 = Set(bigDec2, bigDec3)

          for {
            _ <- Ns.int.bigDecs$ insert List(
              (1, Some(Set(bigDec1, bigDec2))),
              (2, Some(Set(bigDec2, bigDec3))),
              (3, Some(Set(bigDec3, bigDec4))),
              (4, None)
            )

            // OR semantics

            // Vararg
            _ <- Ns.int.bigDecs_.apply(bigDec1, bigDec2).get.map(_ ==> List(1, 2))

            // `or`
            _ <- Ns.int.bigDecs_.apply(bigDec1 or bigDec2).get.map(_ ==> List(1, 2))

            // Seq
            _ <- Ns.int.bigDecs_.apply(seq0).get.map(_ ==> List(4))
            _ <- Ns.int.bigDecs_.apply(List(bigDec1), List(bigDec2)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(l1, l2).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(List(bigDec1, bigDec2)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(l12).get.map(_ ==> List(1, 2))


            // AND semantics

            // Set
            _ <- Ns.int.bigDecs_.apply(set0).get.map(_ ==> List(4))

            _ <- Ns.int.bigDecs_.apply(Set(bigDec1)).get.map(_ ==> List(1))
            _ <- Ns.int.bigDecs_.apply(s1).get.map(_ ==> List(1))

            _ <- Ns.int.bigDecs_.apply(Set(bigDec2)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(s2).get.map(_ ==> List(1, 2))

            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2)).get.map(_ ==> List(1))
            _ <- Ns.int.bigDecs_.apply(s12).get.map(_ ==> List(1))

            _ <- Ns.int.bigDecs_.apply(Set(bigDec2, bigDec3)).get.map(_ ==> List(2))
            _ <- Ns.int.bigDecs_.apply(s23).get.map(_ ==> List(2))

            _ <- Ns.int.bigDecs_.apply(Set(bigDec1, bigDec2), Set(bigDec2, bigDec3)).get.map(_ ==> List(1, 2))
            _ <- Ns.int.bigDecs_.apply(s12, s23).get.map(_ ==> List(1, 2))

            // `and`
            _ <- Ns.int.bigDecs_.apply(bigDec1 and bigDec2).get.map(_ ==> List(1))
          } yield ()
        }
      }

      "Implicit widening conversions, card one" - core { implicit conn =>
        val res1 = List(
          (1, BigDecimal(1)),
          (2, BigDecimal(1)),
          (3, BigDecimal(1)),
          (4, BigDecimal(1)),
          (5, BigDecimal(1)),
          (6, BigDecimal(1)),
        )
        val res2 = List((7, BigDecimal(2)))

        val i1 = 1
        val i2 = 2
        val d1 = 1.0
        val x1 = BigDecimal(1)
        val y1 = BigDecimal(1.0)

        val res1t = List(1, 2, 3, 4, 5, 6)
        val res2t = List(7)

        for {
          _ <- Ns.int.bigDec insert List(
            (1, 1),
            (2, 1L),
            (3, 1f),
            (4, 1.0),
            (5, BigDecimal(1)),
            (6, BigDecimal(1.0)),
            (7, bigDec2),
          )

          _ <- Ns.int.bigDec(1).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec(1L).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec(1f).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec(1.0).get.map(_.sortBy(_._1) ==> res1)

          _ <- Ns.int.bigDec(i1).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec(d1).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec(x1).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec(y1).get.map(_.sortBy(_._1) ==> res1)

          _ <- Ns.int.bigDec.not(i2).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec.<(i2).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec.>(i1).get.map(_ ==> res2)
          _ <- Ns.int.bigDec.>=(i2).get.map(_ ==> res2)
          _ <- Ns.int.bigDec.<=(i1).get.map(_.sortBy(_._1) ==> res1)

          _ <- Ns.int.bigDec.not(2).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec.<(2).get.map(_.sortBy(_._1) ==> res1)
          _ <- Ns.int.bigDec.>(1).get.map(_ ==> res2)
          _ <- Ns.int.bigDec.>=(2).get.map(_ ==> res2)
          _ <- Ns.int.bigDec.<=(1).get.map(_.sortBy(_._1) ==> res1)


          _ <- Ns.int.bigDec_(1).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_(1L).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_(1f).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_(1.0).get.map(_.sorted ==> res1t)

          _ <- Ns.int.bigDec_(i1).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_(d1).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_(x1).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_(y1).get.map(_.sorted ==> res1t)

          _ <- Ns.int.bigDec_.not(i2).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_.<(i2).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_.>(i1).get.map(_ ==> res2t)
          _ <- Ns.int.bigDec_.>=(i2).get.map(_ ==> res2t)
          _ <- Ns.int.bigDec_.<=(i1).get.map(_.sorted ==> res1t)

          _ <- Ns.int.bigDec_.not(2).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_.<(2).get.map(_.sorted ==> res1t)
          _ <- Ns.int.bigDec_.>(1).get.map(_ ==> res2t)
          _ <- Ns.int.bigDec_.>=(2).get.map(_ ==> res2t)
          _ <- Ns.int.bigDec_.<=(1).get.map(_.sorted ==> res1t)
        } yield ()
      }
    }

    "Implicit widening conversions, card many" - core { implicit conn =>
      val res1 = List(
        (1, Set(BigDecimal(1))),
        (2, Set(BigDecimal(1))),
        (3, Set(BigDecimal(1))),
        (4, Set(BigDecimal(1))),
        (5, Set(BigDecimal(1))),
        (6, Set(BigDecimal(1))),
      )
      val res2 = List((7, Set(BigDecimal(2))))

      val i1 = 1
      val i2 = 2
      val d1 = 1.0
      val x1 = BigDecimal(1)
      val y1 = BigDecimal(1.0)

      val res1t = List(1, 2, 3, 4, 5, 6)
      val res2t = List(7)

      for {
        _ <- Ns.int.bigDecs insert List(
          (1, Set(1)),
          (2, Set(1L)),
          (3, Set(1f)),
          (4, Set(1.0)),
          (5, Set(BigDecimal(1))),
          (6, Set(BigDecimal(1.0))),
          (7, Set(bigDec2)),
        )

        _ <- Ns.int.bigDecs(1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs(1L).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs(1f).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs(1.0).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.bigDecs(i1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs(d1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs(x1).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs(y1).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.bigDecs.not(i2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs.<(i2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs.>(i1).get.map(_ ==> res2)
        _ <- Ns.int.bigDecs.>=(i2).get.map(_ ==> res2)
        _ <- Ns.int.bigDecs.<=(i1).get.map(_.sortBy(_._1) ==> res1)

        _ <- Ns.int.bigDecs.not(2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs.<(2).get.map(_.sortBy(_._1) ==> res1)
        _ <- Ns.int.bigDecs.>(1).get.map(_ ==> res2)
        _ <- Ns.int.bigDecs.>=(2).get.map(_ ==> res2)
        _ <- Ns.int.bigDecs.<=(1).get.map(_.sortBy(_._1) ==> res1)


        _ <- Ns.int.bigDecs_(1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_(1L).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_(1f).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_(1.0).get.map(_.sorted ==> res1t)

        _ <- Ns.int.bigDecs_(i1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_(d1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_(x1).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_(y1).get.map(_.sorted ==> res1t)

        _ <- Ns.int.bigDecs_.not(i2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_.<(i2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_.>(i1).get.map(_ ==> res2t)
        _ <- Ns.int.bigDecs_.>=(i2).get.map(_ ==> res2t)
        _ <- Ns.int.bigDecs_.<=(i1).get.map(_.sorted ==> res1t)

        _ <- Ns.int.bigDecs_.not(2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_.<(2).get.map(_.sorted ==> res1t)
        _ <- Ns.int.bigDecs_.>(1).get.map(_ ==> res2t)
        _ <- Ns.int.bigDecs_.>=(2).get.map(_ ==> res2t)
        _ <- Ns.int.bigDecs_.<=(1).get.map(_.sorted ==> res1t)
      } yield ()
    }
  }
}