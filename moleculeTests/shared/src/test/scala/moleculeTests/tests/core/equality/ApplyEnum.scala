package moleculeTests.tests.core.equality

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object ApplyEnum extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.enumm$ insert List(
            (1, Some("enum1")),
            (2, Some("enum2")),
            (3, Some("enum3")),
            (4, None)
          )

          // Varargs
          _ <- Ns.enumm.apply("enum1").get.map(_ ==> List("enum1"))
          _ <- Ns.enumm.apply("enum2").get.map(_ ==> List("enum2"))
          _ <- Ns.enumm.apply("enum1", "enum2").get.map(_ ==> List("enum2", "enum1"))

          // `or`
          _ <- Ns.enumm.apply("enum1" or "enum2").get.map(_ ==> List("enum2", "enum1"))
          _ <- Ns.enumm.apply("enum1" or "enum2" or "enum3").get.map(_ ==> List("enum3", "enum2", "enum1"))

          // Seq
          _ <- Ns.enumm.apply().get.map(_ ==> Nil)
          _ <- Ns.enumm.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.enumm.apply(List("enum1")).get.map(_ ==> List("enum1"))
          _ <- Ns.enumm.apply(List("enum2")).get.map(_ ==> List("enum2"))
          _ <- Ns.enumm.apply(List("enum1", "enum2")).get.map(_ ==> List("enum2", "enum1"))
          _ <- Ns.enumm.apply(List("enum1"), List("enum2")).get.map(_ ==> List("enum2", "enum1"))
          _ <- Ns.enumm.apply(List("enum1", "enum2"), List("enum3")).get.map(_ ==> List("enum3", "enum2", "enum1"))
          _ <- Ns.enumm.apply(List("enum1"), List("enum2", "enum3")).get.map(_ ==> List("enum3", "enum2", "enum1"))
          _ <- Ns.enumm.apply(List("enum1", "enum2", "enum3")).get.map(_ ==> List("enum3", "enum2", "enum1"))

          // Applying a non-existing enum value ("zzz") won't compile!
          _ = expectCompileError("""m(Ns.enumm("zzz"))""",
            """molecule.core.transform.exception.Dsl2ModelException: 'zzz' is not among available enum values of attribute :Ns/enumm:
              |  enum0
              |  enum1
              |  enum2
              |  enum3
              |  enum4
              |  enum5
              |  enum6
              |  enum7
              |  enum8
              |  enum9""")
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.enumm$ insert List(
            (1, Some("enum1")),
            (2, Some("enum2")),
            (3, Some("enum3")),
            (4, None)
          )

          // Varargs
          _ <- Ns.int.enumm_.apply("enum1").get.map(_ ==> List(1))
          _ <- Ns.int.enumm_.apply("enum2").get.map(_ ==> List(2))
          _ <- Ns.int.enumm_.apply("enum1", "enum2").get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.enumm_.apply("enum1" or "enum2").get.map(_ ==> List(1, 2))
          _ <- Ns.int.enumm_.apply("enum1" or "enum2" or "enum3").get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.enumm_.apply().get.map(_ ==> List(4))
          _ <- Ns.int.enumm_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.enumm_.apply(List("enum1")).get.map(_ ==> List(1))
          _ <- Ns.int.enumm_.apply(List("enum2")).get.map(_ ==> List(2))
          _ <- Ns.int.enumm_.apply(List("enum1", "enum2")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enumm_.apply(List("enum1"), List("enum2")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enumm_.apply(List("enum1", "enum2"), List("enum3")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.enumm_.apply(List("enum1"), List("enum2", "enum3")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.enumm_.apply(List("enum1", "enum2", "enum3")).get.map(_ ==> List(1, 2, 3))
        } yield ()
      }
    }


    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.enums$ insert List(
            (1, Some(Set("enum1", "enum2"))),
            (2, Some(Set("enum2", "enum3"))),
            (3, Some(Set("enum3", "enum4"))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.enums.apply("enum1").get.map(_ ==> List((1, Set("enum1", "enum2"))))
          _ <- Ns.int.enums.apply("enum2").get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply("enum1", "enum2").get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))

          // `or`
          _ <- Ns.int.enums.apply("enum1" or "enum2").get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply("enum1" or "enum2" or "enum3").get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3")), (3, Set("enum3", "enum4"))))

          // Seq
          _ <- Ns.int.enums.apply().get.map(_ ==> Nil)
          _ <- Ns.int.enums.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.int.enums.apply(List("enum1")).get.map(_ ==> List((1, Set("enum1", "enum2"))))
          _ <- Ns.int.enums.apply(List("enum2")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply(List("enum1", "enum2")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply(List("enum1"), List("enum2")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply(List("enum1", "enum2"), List("enum3")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3")), (3, Set("enum3", "enum4"))))
          _ <- Ns.int.enums.apply(List("enum1"), List("enum2", "enum3")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3")), (3, Set("enum3", "enum4"))))
          _ <- Ns.int.enums.apply(List("enum1", "enum2", "enum3")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3")), (3, Set("enum3", "enum4"))))


          // AND semantics

          // Set
          _ <- Ns.int.enums.apply(Set[String]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.int.enums.apply(Set("enum1")).get.map(_ ==> List((1, Set("enum1", "enum2"))))
          _ <- Ns.int.enums.apply(Set("enum2")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum2")).get.map(_ ==> List((1, Set("enum1", "enum2"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum3")).get.map(_ ==> Nil)
          _ <- Ns.int.enums.apply(Set("enum2", "enum3")).get.map(_ ==> List((2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum2", "enum3")).get.map(_ ==> Nil)

          _ <- Ns.int.enums.apply(Set("enum1", "enum2"), Set[String]()).get.map(_ ==> List((1, Set("enum1", "enum2"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum2"), Set("enum2")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum2"), Set("enum3")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3")), (3, Set("enum3", "enum4"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum2"), Set("enum4")).get.map(_ ==> List((1, Set("enum1", "enum2")), (3, Set("enum3", "enum4"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum2"), Set("enum2"), Set("enum3")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3")), (3, Set("enum3", "enum4"))))

          _ <- Ns.int.enums.apply(Set("enum1", "enum2"), Set("enum2", "enum3")).get.map(_ ==> List((1, Set("enum1", "enum2")), (2, Set("enum2", "enum3"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum2"), Set("enum2", "enum4")).get.map(_ ==> List((1, Set("enum1", "enum2"))))
          _ <- Ns.int.enums.apply(Set("enum1", "enum2"), Set("enum3", "enum4")).get.map(_ ==> List((1, Set("enum1", "enum2")), (3, Set("enum3", "enum4"))))

          // `and`
          _ <- Ns.int.enums.apply("enum1" and "enum2").get.map(_ ==> List((1, Set("enum1", "enum2"))))
          _ <- Ns.int.enums.apply("enum1" and "enum3").get.map(_ ==> Nil)
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.enums$ insert List(
            (1, Some(Set("enum1", "enum2"))),
            (2, Some(Set("enum2", "enum3"))),
            (3, Some(Set("enum3", "enum4"))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.enums.apply("enum1").get.map(_ ==> List(Set("enum1", "enum2")))
          _ <- Ns.enums.apply("enum2").get.map(_ ==> List(Set("enum1", "enum3", "enum2")))
          _ <- Ns.enums.apply("enum1", "enum2").get.map(_ ==> List(Set("enum1", "enum3", "enum2")))

          // `or`
          _ <- Ns.enums.apply("enum1" or "enum2").get.map(_ ==> List(Set("enum1", "enum3", "enum2")))
          _ <- Ns.enums.apply("enum1" or "enum2" or "enum3").get.map(_ ==> List(Set("enum1", "enum4", "enum3", "enum2")))

          // Seq
          _ <- Ns.enums.apply().get.map(_ ==> Nil)
          _ <- Ns.enums.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.enums.apply(List("enum1")).get.map(_ ==> List(Set("enum1", "enum2")))
          _ <- Ns.enums.apply(List("enum2")).get.map(_ ==> List(Set("enum1", "enum3", "enum2")))
          _ <- Ns.enums.apply(List("enum1", "enum2")).get.map(_ ==> List(Set("enum1", "enum3", "enum2")))
          _ <- Ns.enums.apply(List("enum1"), List("enum2")).get.map(_ ==> List(Set("enum1", "enum3", "enum2")))
          _ <- Ns.enums.apply(List("enum1", "enum2"), List("enum3")).get.map(_ ==> List(Set("enum1", "enum4", "enum3", "enum2")))
          _ <- Ns.enums.apply(List("enum1"), List("enum2", "enum3")).get.map(_ ==> List(Set("enum1", "enum4", "enum3", "enum2")))
          _ <- Ns.enums.apply(List("enum1", "enum2", "enum3")).get.map(_ ==> List(Set("enum1", "enum4", "enum3", "enum2")))


          // AND semantics

          // Set
          _ <- Ns.enums.apply(Set[String]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.enums.apply(Set("enum1")).get.map(_ ==> List(Set("enum1", "enum2")))
          _ <- Ns.enums.apply(Set("enum2")).get.map(_ ==> List(Set("enum1", "enum3", "enum2")))
          _ <- Ns.enums.apply(Set("enum1", "enum2")).get.map(_ ==> List(Set("enum1", "enum2")))
          _ <- Ns.enums.apply(Set("enum1", "enum3")).get.map(_ ==> Nil)
          _ <- Ns.enums.apply(Set("enum2", "enum3")).get.map(_ ==> List(Set("enum2", "enum3")))
          _ <- Ns.enums.apply(Set("enum1", "enum2", "enum3")).get.map(_ ==> Nil)

          _ <- Ns.enums.apply(Set("enum1", "enum2"), Set("enum2")).get.map(_ ==> List(Set("enum1", "enum2", "enum3")))
          _ <- Ns.enums.apply(Set("enum1", "enum2"), Set("enum3")).get.map(_ ==> List(Set("enum1", "enum2", "enum3", "enum4")))
          _ <- Ns.enums.apply(Set("enum1", "enum2"), Set("enum4")).get.map(_ ==> List(Set("enum1", "enum2", "enum3", "enum4")))
          _ <- Ns.enums.apply(Set("enum1", "enum2"), Set("enum2"), Set("enum3")).get.map(_ ==> List(Set("enum1", "enum2", "enum3", "enum4")))

          _ <- Ns.enums.apply(Set("enum1", "enum2"), Set("enum2", "enum3")).get.map(_ ==> List(Set("enum1", "enum2", "enum3")))
          _ <- Ns.enums.apply(Set("enum1", "enum2"), Set("enum2", "enum4")).get.map(_ ==> List(Set("enum1", "enum2")))
          _ <- Ns.enums.apply(Set("enum1", "enum2"), Set("enum3", "enum4")).get.map(_ ==> List(Set("enum1", "enum2", "enum3", "enum4")))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.enums.apply("enum1" and "enum2").get.map(_ ==> List(Set("enum1", "enum2")))
          _ <- Ns.enums.apply("enum1" and "enum3").get.map(_ ==> Nil)
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.enums$ insert List(
            (1, Some(Set("enum1", "enum2"))),
            (2, Some(Set("enum2", "enum3"))),
            (3, Some(Set("enum3", "enum4"))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.enums_.apply("enum1").get.map(_ ==> List(1))
          _ <- Ns.int.enums_.apply("enum2").get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply("enum1", "enum2").get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.enums_.apply("enum1" or "enum2").get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply("enum1" or "enum2" or "enum3").get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.enums_.apply().get.map(_ ==> List(4)) // entities with no card-many values asserted
          _ <- Ns.int.enums_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.enums_.apply(List("enum1")).get.map(_ ==> List(1))
          _ <- Ns.int.enums_.apply(List("enum2")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(List("enum1", "enum2")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(List("enum1"), List("enum2")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(List("enum1", "enum2"), List("enum3")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.enums_.apply(List("enum1"), List("enum2", "enum3")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.enums_.apply(List("enum1", "enum2", "enum3")).get.map(_ ==> List(1, 2, 3))


          // AND semantics

          // Set
          _ <- Ns.int.enums_.apply(Set[String]()).get.map(_ ==> List(4))
          _ <- Ns.int.enums_.apply(Set("enum1")).get.map(_ ==> List(1))
          _ <- Ns.int.enums_.apply(Set("enum2")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(Set("enum1", "enum2")).get.map(_ ==> List(1))
          _ <- Ns.int.enums_.apply(Set("enum1", "enum3")).get.map(_ ==> Nil)
          _ <- Ns.int.enums_.apply(Set("enum2", "enum3")).get.map(_ ==> List(2))
          _ <- Ns.int.enums_.apply(Set("enum1", "enum2", "enum3")).get.map(_ ==> Nil)

          _ <- Ns.int.enums_.apply(Set("enum1", "enum2"), Set("enum2")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(Set("enum1", "enum2"), Set("enum3")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.enums_.apply(Set("enum1", "enum2"), Set("enum4")).get.map(_ ==> List(1, 3))
          _ <- Ns.int.enums_.apply(Set("enum1", "enum2"), Set("enum2"), Set("enum3")).get.map(_ ==> List(1, 2, 3))

          _ <- Ns.int.enums_.apply(Set("enum1", "enum2"), Set("enum2", "enum3")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(Set("enum1", "enum2"), Set("enum2", "enum4")).get.map(_ ==> List(1))
          _ <- Ns.int.enums_.apply(Set("enum1", "enum2"), Set("enum3", "enum4")).get.map(_ ==> List(1, 3))


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.enums_.apply("enum1" and "enum2").get.map(_ ==> List(1))
          _ <- Ns.int.enums_.apply("enum1" and "enum3").get.map(_ ==> Nil)
        } yield ()
      }


      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[String]()

        val l1 = List(enum1)
        val l2 = List(enum2)

        val s1 = Set(enum1)
        val s2 = Set(enum2)

        val l12 = List(enum1, enum2)

        val s12 = Set(enum1, enum2)
        val s23 = Set(enum2, enum3)

        for {
          _ <- Ns.int.enums$ insert List(
            (1, Some(Set("enum1", "enum2"))),
            (2, Some(Set("enum2", "enum3"))),
            (3, Some(Set("enum3", "enum4"))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.enums_.apply(enum1, enum2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.enums_.apply(enum1 or enum2).get.map(_ ==> List(1, 2))

          // Seq
          _ <- Ns.int.enums_.apply(seq0).get.map(_ ==> List(4))
          _ <- Ns.int.enums_.apply(List(enum1), List(enum2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(l1, l2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(List(enum1, enum2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(l12).get.map(_ ==> List(1, 2))


          // AND semantics

          // Set
          _ <- Ns.int.enums_.apply(set0).get.map(_ ==> List(4))

          _ <- Ns.int.enums_.apply(Set(enum1)).get.map(_ ==> List(1))
          _ <- Ns.int.enums_.apply(s1).get.map(_ ==> List(1))

          _ <- Ns.int.enums_.apply(Set(enum2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(s2).get.map(_ ==> List(1, 2))

          _ <- Ns.int.enums_.apply(Set(enum1, enum2)).get.map(_ ==> List(1))
          _ <- Ns.int.enums_.apply(s12).get.map(_ ==> List(1))

          _ <- Ns.int.enums_.apply(Set(enum2, enum3)).get.map(_ ==> List(2))
          _ <- Ns.int.enums_.apply(s23).get.map(_ ==> List(2))

          _ <- Ns.int.enums_.apply(Set(enum1, enum2), Set(enum2, enum3)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.enums_.apply(s12, s23).get.map(_ ==> List(1, 2))

          // `and`
          _ <- Ns.int.enums_.apply(enum1 and enum2).get.map(_ ==> List(1))
        } yield ()
      }
    }
  }
}