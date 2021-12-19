package moleculeTests.tests.core.obj

import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.api.out10._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.dataModels.core.ref.dsl.SelfJoin._
import utest._
import molecule.core.util.Executor._

object ObjRef extends AsyncTestSuite with Helpers {

  lazy val tests = Tests {

    "ref/backref" - core { implicit conn =>
      for {
        _ <- Ns.int(0).str("a")
          .Ref1.int1(1).str1("b")
          .Refs2.int2(22)._Ref1
          .Ref2.int2(2).str2("c")._Ref1._Ns
          .Refs1.int1(11)
          .save

        _ <- Ns.int.Ref1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
        }
        _ <- Ns.int.Ref1.int1._Ns.str.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.str ==> "a"
        }
        _ <- Ns.int.Ref1.int1._Ns.Refs1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Refs1.int1 ==> 11
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.str1 ==> "b"
        }
        _ <- Ns.int.Ref1.Ref2.int2._Ref1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.int1 ==> 1
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.Refs2.int2.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.Refs2.int2 ==> 22
        }
        _ <- Ns.int.Ref1.Ref2.int2._Ref1.Refs2.int2.getObj.map { o =>
          o.int ==> 0
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.Refs2.int2 ==> 22
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.str.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.str1 ==> "b"
          o.str ==> "a"
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1._Ns.str.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.str ==> "a"
        }
        _ <- Ns.int.Ref1.Ref2.int2._Ref1._Ns.str.getObj.map { o =>
          o.int ==> 0
          o.Ref1.Ref2.int2 ==> 2
          o.str ==> "a"
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.Refs1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.str1 ==> "b"
          o.Refs1.int1 ==> 11
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1._Ns.Refs1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Refs1.int1 ==> 11
        }
        _ <- Ns.int.Ref1.Ref2.int2._Ref1._Ns.Refs1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.Ref2.int2 ==> 2
          o.Refs1.int1 ==> 11
        }
        _ <- Ns.Ref1.Ref2.int2._Ref1._Ns.Refs1.int1.getObj.map { o =>
          o.Ref1.Ref2.int2 ==> 2
          o.Refs1.int1 ==> 11
        }
      } yield ()
    }

    "Tacit orphans" - core { implicit conn =>
      for {
        // Only add ref if there are any props to ref
        // (we can't be selective in the molecule type buildup, so the "empty refs" have to remain)
        _ <- Ns.int(0).Ref1.int1(1).save
        _ <- Ns.int.Ref1.int1_.getObj.map { o =>
          o.int ==> 0
          o.Ref1 // tacit int1_ returns no value and thus has no object property
        }
      } yield ()
    }

    "Nested" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.*(Ref1.int1)) insert List(
          ("a", List(1)),
          ("b", List(2, 3))
        )

        _ <- Ns.str.Refs1.*(Ref1.int1).getObjs.collect { case List(o1, o2) =>
          o1.str ==> "a"
          val List(r1) = o1.Refs1
          r1.int1 ==> 1

          o2.str ==> "b"
          val List(r2, r3) = o2.Refs1
          r2.int1 ==> 2
          r3.int1 ==> 3
        }
      } yield ()
    }

    "Nested optional" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.str1$) insert List(
          (1, List((11, Some("a")), (12, None))),
          (2, List())
        )
        _ <- (Ns.int.Refs1 *? Ref1.int1.str1$).getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.Refs1.head.int1 ==> 11
          o1.Refs1.head.str1$ ==> Some("a")
          o1.Refs1.last.int1 ==> 12
          o1.Refs1.last.str1$ ==> None

          o2.int ==> 2
          o2.Refs1 ==> Nil
        }
      } yield ()
    }


    "Composites" - core { implicit conn =>
      for {
        // All properties are namespaced
        _ <- (Ns.int(1) + Ref1.int1(1) + Ref2.int2(1)).save
        _ <- m(Ns.int + Ref1.int1 + Ref2.int2).getObj.map { o =>
          o.Ns.int ==> 1
          o.Ref1.int1 ==> 1
          o.Ref2.int2 ==> 1
        }
        _ <- (Ns.int(2).Ref1.int1(1) + Ref2.int2(2)).save
        _ <- m(Ns.int.Ref1.int1 + Ref2.int2).getObj.map { o =>
          o.Ns.int ==> 2
          o.Ns.Ref1.int1 ==> 1
          o.Ref2.int2 ==> 2
        }
        _ <- (Ns.int(3) + Ref1.int1(3).Ref2.int2(3)).save
        _ <- m(Ns.int + Ref1.int1.Ref2.int2).getObj.map { o =>
          o.Ns.int ==> 3
          o.Ref1.int1 ==> 3
          o.Ref1.Ref2.int2 ==> 3
        }
      } yield ()
    }


    "Multiple same-name ns composites" - core { implicit conn =>
      for {
        _ <- (Ns.int + Ns.double.str + Ref1.int1.str1).insert(1, (2.2, "a"), (3, "b"))

        // Multiple same-name namespace composites not allowed for object output
        _ <- m(Ns.int + Ns.double.str + Ref1.int1.str1).getObj
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Molecule objects are not allowed to have multiple same-named namespaces. Please use tuples instead that allow this."
        }

        // Multiple same-name namespace composites allowed with tuple output
        _ <- m(Ns.int + Ns.double.str + Ref1.int1.str1).get.map(_.head ==> (1, (2.2, "a"), (3, "b")))
      } yield ()
    }


    "Self-joins" - selfJoin { implicit conn =>
      for {
        _ <- m(Person.age.name.Likes * Score.beverage.rating) insert List(
          (23, "Joe", List(("Coffee", 3), ("Cola", 2), ("Pepsi", 3))),
          (25, "Ben", List(("Coffee", 2), ("Tea", 3))),
          (23, "Liz", List(("Coffee", 1), ("Tea", 3), ("Pepsi", 1))))

        _ <- Person
          .age_(23).name.Likes.beverage._Person.Self
          .age_(25).name.Likes.beverage_(unify).getObjs.collect { case persons =>

          val List(p1, p2, p3) = persons.sortBy(person => (person.name, person.Likes.beverage))

          p1.name ==> "Joe"
          p1.Likes.beverage ==> "Coffee"
          p1.Person.name ==> "Ben"

          p2.name ==> "Liz"
          p2.Likes.beverage ==> "Coffee"
          // Self-join to other Person (same namespace)
          p2.Person.name ==> "Ben"

          p3.name ==> "Liz"
          p3.Likes.beverage ==> "Tea"
          p3.Person.name ==> "Ben"
        }

        _ <- Person
          .name("Joe").Likes.beverage._Person.Self
          .name("Ben").Likes.beverage_(unify)._Person.Self
          .name("Liz").Likes.beverage_(unify).getObj.map { p =>

          p.name ==> "Joe"
          p.Likes.beverage ==> "Coffee"
          // Self-join to other Person (Ben) in same namespace
          p.Person.name ==> "Ben"
          // Self-join from self-joined person (Ben) to other Person (Liz) in same namespace
          p.Person.Person.name ==> "Liz"
        }
      } yield ()
    }
  }
}
