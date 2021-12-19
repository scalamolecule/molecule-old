package moleculeTests.tests.core.equality

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object ApplyString extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - {

      "Special characters" - core { implicit conn =>
        for {
          _ <- Ns.str insert List("", " ", ",", ".", "?", "A", "B", "a", "b")

          // Empty string can be saved
          _ <- Ns.str("").get.map(_ ==> List(""))

          // White space only can be saved
          _ <- Ns.str(" ").get.map(_ ==> List(" "))

          // Characters and symbols
          _ <- Ns.str(",").get.map(_ ==> List(","))
          _ <- Ns.str(".").get.map(_ ==> List("."))
          _ <- Ns.str("?").get.map(_ ==> List("?"))
          _ <- Ns.str("A").get.map(_ ==> List("A"))
          _ <- Ns.str("B").get.map(_ ==> List("B"))
          _ <- Ns.str("a").get.map(_ ==> List("a"))
          _ <- Ns.str("b").get.map(_ ==> List("b"))
        } yield ()
      }

      "Quoting" - core { implicit conn =>
        val some = Some("""Hi "Ann"""")

        for {
          _ <- Ns.int(1).str("""Hi "Ann"""").save

          _ <- Ns.str.get.map(_ ==> List("""Hi "Ann""""))
          _ <- Ns.str("""Hi "Ann"""").get.map(_ ==> List("""Hi "Ann""""))

          str: String = """Hi "Ann""""
          _ <- Ns.str(str).get.map(_ ==> List("""Hi "Ann""""))

          _ <- Ns.int.str_("""Hi "Ann"""").get.map(_ ==> List(1))
          _ <- Ns.int.str_(str).get.map(_ ==> List(1))

          _ <- Ns.int.str$(Some("""Hi "Ann"""")).get.map(_ ==> List((1, Some("""Hi "Ann""""))))

          _ <- Ns.int.str$(some).get.map(_ ==> List((1, Some("""Hi "Ann""""))))
        } yield ()
      }

      "Escaping 1" - core { implicit conn =>
        val expr = """Hello "\d" expression"""
        for {
          _ <- Ns.str(expr).save
          _ <- Ns.str.get.map(_ ==> List(expr))
          _ <- Ns.str(expr).get.map(_ ==> List(expr))
        } yield ()
      }

      "Escaping 2" - core { implicit conn =>
        val expr = "Hello \"\\d\" expression"
        for {
          _ <- Ns.str(expr).save
          _ <- Ns.str.get.map(_ ==> List(expr))
          _ <- Ns.str(expr).get.map(_ ==> List(expr))
        } yield ()
      }

      // OR semantics only for card-one attributes

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.str$ insert List(
            (1, Some("a")),
            (2, Some("b")),
            (3, Some("c")),
            (4, None)
          )

          // Varargs
          _ <- Ns.str.apply("a").get.map(_ ==> List("a"))
          _ <- Ns.str.apply("b").get.map(_ ==> List("b"))
          _ <- Ns.str.apply("a", "b").get.map(_ ==> List("a", "b"))

          // `or`
          _ <- Ns.str.apply("a" or "b").get.map(_ ==> List("a", "b"))
          _ <- Ns.str.apply("a" or "b" or "c").get.map(_ ==> List("a", "b", "c"))

          // Seq
          _ <- Ns.str.apply().get.map(_ ==> Nil)
          _ <- Ns.str.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.str.apply(List("a")).get.map(_ ==> List("a"))
          _ <- Ns.str.apply(List("b")).get.map(_ ==> List("b"))
          _ <- Ns.str.apply(List("a", "b")).get.map(_ ==> List("a", "b"))
          _ <- Ns.str.apply(List("a"), List("b")).get.map(_ ==> List("a", "b"))
          _ <- Ns.str.apply(List("a", "b"), List("c")).get.map(_ ==> List("a", "b", "c"))
          _ <- Ns.str.apply(List("a"), List("b", "c")).get.map(_ ==> List("a", "b", "c"))
          _ <- Ns.str.apply(List("a", "b", "c")).get.map(_ ==> List("a", "b", "c"))
        } yield ()
      }

      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.str$ insert List(
            (1, Some("a")),
            (2, Some("b")),
            (3, Some("c")),
            (4, None)
          )

          // Varargs
          _ <- Ns.int.str_.apply("a").get.map(_ ==> List(1))
          _ <- Ns.int.str_.apply("b").get.map(_ ==> List(2))
          _ <- Ns.int.str_.apply("a", "b").get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.str_.apply("a" or "b").get.map(_ ==> List(1, 2))
          _ <- Ns.int.str_.apply("a" or "b" or "c").get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.str_.apply().get.map(_ ==> List(4))
          _ <- Ns.int.str_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.str_.apply(List("a")).get.map(_ ==> List(1))
          _ <- Ns.int.str_.apply(List("b")).get.map(_ ==> List(2))
          _ <- Ns.int.str_.apply(List("a", "b")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.str_.apply(List("a"), List("b")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.str_.apply(List("a", "b"), List("c")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.str_.apply(List("a"), List("b", "c")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.str_.apply(List("a", "b", "c")).get.map(_ ==> List(1, 2, 3))
        } yield ()
      }
    }


    "Card many" - {

      "Mandatory" - core { implicit conn =>
        for {
          _ <- Ns.int.strs$ insert List(
            (1, Some(Set("a", "b"))),
            (2, Some(Set("b", "c"))),
            (3, Some(Set("c", "d"))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.strs.apply("a").get.map(_ ==> List((1, Set("a", "b"))))
          _ <- Ns.int.strs.apply("b").get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))
          _ <- Ns.int.strs.apply("a", "b").get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))

          // `or`
          _ <- Ns.int.strs.apply("a" or "b").get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))
          _ <- Ns.int.strs.apply("a" or "b" or "c").get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c")), (3, Set("c", "d"))))

          // Seq
          _ <- Ns.int.strs.apply().get.map(_ ==> Nil)
          _ <- Ns.int.strs.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.int.strs.apply(List("a")).get.map(_ ==> List((1, Set("a", "b"))))
          _ <- Ns.int.strs.apply(List("b")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))
          _ <- Ns.int.strs.apply(List("a", "b")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))
          _ <- Ns.int.strs.apply(List("a"), List("b")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))
          _ <- Ns.int.strs.apply(List("a", "b"), List("c")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c")), (3, Set("c", "d"))))
          _ <- Ns.int.strs.apply(List("a"), List("b", "c")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c")), (3, Set("c", "d"))))
          _ <- Ns.int.strs.apply(List("a", "b", "c")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c")), (3, Set("c", "d"))))


          // AND semantics

          // Set
          _ <- Ns.int.strs.apply(Set[String]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.int.strs.apply(Set("a")).get.map(_ ==> List((1, Set("a", "b"))))
          _ <- Ns.int.strs.apply(Set("b")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))
          _ <- Ns.int.strs.apply(Set("a", "b")).get.map(_ ==> List((1, Set("a", "b"))))
          _ <- Ns.int.strs.apply(Set("a", "c")).get.map(_ ==> Nil)
          _ <- Ns.int.strs.apply(Set("b", "c")).get.map(_ ==> List((2, Set("b", "c"))))
          _ <- Ns.int.strs.apply(Set("a", "b", "c")).get.map(_ ==> Nil)

          _ <- Ns.int.strs.apply(Set("a", "b"), Set[String]()).get.map(_ ==> List((1, Set("a", "b"))))
          _ <- Ns.int.strs.apply(Set("a", "b"), Set("b")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))
          _ <- Ns.int.strs.apply(Set("a", "b"), Set("c")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c")), (3, Set("c", "d"))))
          _ <- Ns.int.strs.apply(Set("a", "b"), Set("d")).get.map(_ ==> List((1, Set("a", "b")), (3, Set("c", "d"))))
          _ <- Ns.int.strs.apply(Set("a", "b"), Set("b"), Set("c")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c")), (3, Set("c", "d"))))

          _ <- Ns.int.strs.apply(Set("a", "b"), Set("b", "c")).get.map(_ ==> List((1, Set("a", "b")), (2, Set("b", "c"))))
          _ <- Ns.int.strs.apply(Set("a", "b"), Set("b", "d")).get.map(_ ==> List((1, Set("a", "b"))))
          _ <- Ns.int.strs.apply(Set("a", "b"), Set("c", "d")).get.map(_ ==> List((1, Set("a", "b")), (3, Set("c", "d"))))

          // `and`
          _ <- Ns.int.strs.apply("a" and "b").get.map(_ ==> List((1, Set("a", "b"))))
          _ <- Ns.int.strs.apply("a" and "c").get.map(_ ==> Nil)
        } yield ()
      }


      "Mandatory, single attr coalesce" - core { implicit conn =>
        for {
          _ <- Ns.int.strs$ insert List(
            (1, Some(Set("a", "b"))),
            (2, Some(Set("b", "c"))),
            (3, Some(Set("c", "d"))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.strs.apply("a").get.map(_ ==> List(Set("a", "b")))
          _ <- Ns.strs.apply("b").get.map(_ ==> List(Set("a", "c", "b")))
          _ <- Ns.strs.apply("a", "b").get.map(_ ==> List(Set("a", "c", "b")))

          // `or`
          _ <- Ns.strs.apply("a" or "b").get.map(_ ==> List(Set("a", "c", "b")))
          _ <- Ns.strs.apply("a" or "b" or "c").get.map(_ ==> List(Set("a", "d", "c", "b")))

          // Seq
          _ <- Ns.strs.apply().get.map(_ ==> Nil)
          _ <- Ns.strs.apply(Nil).get.map(_ ==> Nil)
          _ <- Ns.strs.apply(List("a")).get.map(_ ==> List(Set("a", "b")))
          _ <- Ns.strs.apply(List("b")).get.map(_ ==> List(Set("a", "c", "b")))
          _ <- Ns.strs.apply(List("a", "b")).get.map(_ ==> List(Set("a", "c", "b")))
          _ <- Ns.strs.apply(List("a"), List("b")).get.map(_ ==> List(Set("a", "c", "b")))
          _ <- Ns.strs.apply(List("a", "b"), List("c")).get.map(_ ==> List(Set("a", "d", "c", "b")))
          _ <- Ns.strs.apply(List("a"), List("b", "c")).get.map(_ ==> List(Set("a", "d", "c", "b")))
          _ <- Ns.strs.apply(List("a", "b", "c")).get.map(_ ==> List(Set("a", "d", "c", "b")))


          // AND semantics

          // Set
          _ <- Ns.strs.apply(Set[String]()).get.map(_ ==> Nil) // entities with no card-many values asserted can't also return values
          _ <- Ns.strs.apply(Set("a")).get.map(_ ==> List(Set("a", "b")))
          _ <- Ns.strs.apply(Set("b")).get.map(_ ==> List(Set("a", "c", "b")))
          _ <- Ns.strs.apply(Set("a", "b")).get.map(_ ==> List(Set("a", "b")))
          _ <- Ns.strs.apply(Set("a", "c")).get.map(_ ==> Nil)
          _ <- Ns.strs.apply(Set("b", "c")).get.map(_ ==> List(Set("b", "c")))
          _ <- Ns.strs.apply(Set("a", "b", "c")).get.map(_ ==> Nil)

          _ <- Ns.strs.apply(Set("a", "b"), Set("b")).get.map(_ ==> List(Set("a", "b", "c")))
          _ <- Ns.strs.apply(Set("a", "b"), Set("c")).get.map(_ ==> List(Set("a", "b", "c", "d")))
          _ <- Ns.strs.apply(Set("a", "b"), Set("d")).get.map(_ ==> List(Set("a", "b", "c", "d")))
          _ <- Ns.strs.apply(Set("a", "b"), Set("b"), Set("c")).get.map(_ ==> List(Set("a", "b", "c", "d")))

          _ <- Ns.strs.apply(Set("a", "b"), Set("b", "c")).get.map(_ ==> List(Set("a", "b", "c")))
          _ <- Ns.strs.apply(Set("a", "b"), Set("b", "d")).get.map(_ ==> List(Set("a", "b")))
          _ <- Ns.strs.apply(Set("a", "b"), Set("c", "d")).get.map(_ ==> List(Set("a", "b", "c", "d")))


          // Explicit `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.strs.apply("a" and "b").get.map(_ ==> List(Set("a", "b")))
          _ <- Ns.strs.apply("a" and "c").get.map(_ ==> Nil)
        } yield ()
      }


      "Tacit" - core { implicit conn =>
        for {
          _ <- Ns.int.strs$ insert List(
            (1, Some(Set("a", "b"))),
            (2, Some(Set("b", "c"))),
            (3, Some(Set("c", "d"))),
            (4, None)
          )

          // OR semantics

          // Varargs
          _ <- Ns.int.strs_.apply("a").get.map(_ ==> List(1))
          _ <- Ns.int.strs_.apply("b").get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply("a", "b").get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.strs_.apply("a" or "b").get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply("a" or "b" or "c").get.map(_ ==> List(1, 2, 3))

          // Seq
          _ <- Ns.int.strs_.apply().get.map(_ ==> List(4)) // entities with no card-many values asserted
          _ <- Ns.int.strs_.apply(Nil).get.map(_ ==> List(4))
          _ <- Ns.int.strs_.apply(List("a")).get.map(_ ==> List(1))
          _ <- Ns.int.strs_.apply(List("b")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(List("a", "b")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(List("a"), List("b")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(List("a", "b"), List("c")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.strs_.apply(List("a"), List("b", "c")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.strs_.apply(List("a", "b", "c")).get.map(_ ==> List(1, 2, 3))


          // AND semantics

          // Set
          _ <- Ns.int.strs_.apply(Set[String]()).get.map(_ ==> List(4))
          _ <- Ns.int.strs_.apply(Set("a")).get.map(_ ==> List(1))
          _ <- Ns.int.strs_.apply(Set("b")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(Set("a", "b")).get.map(_ ==> List(1))
          _ <- Ns.int.strs_.apply(Set("a", "c")).get.map(_ ==> Nil)
          _ <- Ns.int.strs_.apply(Set("b", "c")).get.map(_ ==> List(2))
          _ <- Ns.int.strs_.apply(Set("a", "b", "c")).get.map(_ ==> Nil)

          _ <- Ns.int.strs_.apply(Set("a", "b"), Set("b")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(Set("a", "b"), Set("c")).get.map(_ ==> List(1, 2, 3))
          _ <- Ns.int.strs_.apply(Set("a", "b"), Set("d")).get.map(_ ==> List(1, 3))
          _ <- Ns.int.strs_.apply(Set("a", "b"), Set("b"), Set("c")).get.map(_ ==> List(1, 2, 3))

          _ <- Ns.int.strs_.apply(Set("a", "b"), Set("b", "c")).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(Set("a", "b"), Set("b", "d")).get.map(_ ==> List(1))
          _ <- Ns.int.strs_.apply(Set("a", "b"), Set("c", "d")).get.map(_ ==> List(1, 3))


          // `and` (maximum 2 `and` implemented: `v1 and v2 and v3`)
          _ <- Ns.int.strs_.apply("a" and "b").get.map(_ ==> List(1))
          _ <- Ns.int.strs_.apply("a" and "c").get.map(_ ==> Nil)
        } yield ()
      }


      "Variable resolution" - core { implicit conn =>
        val seq0 = Nil
        val set0 = Set[String]()

        val l1 = List(str1)
        val l2 = List(str2)

        val s1 = Set(str1)
        val s2 = Set(str2)

        val l12 = List(str1, str2)

        val s12 = Set(str1, str2)
        val s23 = Set(str2, str3)

        for {
          _ <- Ns.int.strs$ insert List(
            (1, Some(Set("a", "b"))),
            (2, Some(Set("b", "c"))),
            (3, Some(Set("c", "d"))),
            (4, None)
          )

          // OR semantics

          // Vararg
          _ <- Ns.int.strs_.apply(str1, str2).get.map(_ ==> List(1, 2))

          // `or`
          _ <- Ns.int.strs_.apply(str1 or str2).get.map(_ ==> List(1, 2))

          // Seq
          _ <- Ns.int.strs_.apply(seq0).get.map(_ ==> List(4))
          _ <- Ns.int.strs_.apply(List(str1), List(str2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(l1, l2).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(List(str1, str2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(l12).get.map(_ ==> List(1, 2))


          // AND semantics

          // Set
          _ <- Ns.int.strs_.apply(set0).get.map(_ ==> List(4))

          _ <- Ns.int.strs_.apply(Set(str1)).get.map(_ ==> List(1))
          _ <- Ns.int.strs_.apply(s1).get.map(_ ==> List(1))

          _ <- Ns.int.strs_.apply(Set(str2)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(s2).get.map(_ ==> List(1, 2))

          _ <- Ns.int.strs_.apply(Set(str1, str2)).get.map(_ ==> List(1))
          _ <- Ns.int.strs_.apply(s12).get.map(_ ==> List(1))

          _ <- Ns.int.strs_.apply(Set(str2, str3)).get.map(_ ==> List(2))
          _ <- Ns.int.strs_.apply(s23).get.map(_ ==> List(2))

          _ <- Ns.int.strs_.apply(Set(str1, str2), Set(str2, str3)).get.map(_ ==> List(1, 2))
          _ <- Ns.int.strs_.apply(s12, s23).get.map(_ ==> List(1, 2))

          // `and`
          _ <- Ns.int.strs_.apply(str1 and str2).get.map(_ ==> List(1))
        } yield ()
      }

      "String interpolation" - core { implicit conn =>
        val world = "world"
        // Applying string-interpolated value not allowed to compile
        // Ns.str(s"hello $world").get
      }
    }
  }
}