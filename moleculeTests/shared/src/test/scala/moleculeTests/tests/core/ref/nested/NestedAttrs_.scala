package moleculeTests.tests.core.ref.nested

import molecule.datomic.api.out6._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object NestedAttrs_ extends AsyncTestSuite {

  lazy val tests = Tests {

    "card 1" - {

      "attr" - core { implicit conn =>
        for {
          _ <- m(Ns.int.Refs1 * Ref1.int1.str1$) insert List(
            (1, List((1, Some("a")), (2, None))),
            (2, List())
          )

          _ <- m(Ns.int.Refs1 * Ref1.int1.str1$).get.map(_ ==> List(
            (1, List((1, Some("a")), (2, None)))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.str1).get.map(_ ==> List(
            (1, List((1, "a")))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.str1_).get.map(_ ==> List(
            (1, List(1))
          ))

          // Optional nested
          _ <- m(Ns.int.Refs1 *? Ref1.int1.str1$).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Some("a")), (2, None))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.str1).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, "a"))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.str1_).get.map(_.sortBy(_._1) ==> List(
            (1, List(1)),
            (2, List())
          ))
        } yield ()
      }

      "enum" - core { implicit conn =>
        for {
          _ <- m(Ns.int.Refs1 * Ref1.int1.enum1$) insert List(
            (1, List((1, Some("enum10")), (2, None))),
            (2, List())
          )

          _ <- m(Ns.int.Refs1 * Ref1.int1.enum1$).get.map(_ ==> List(
            (1, List((1, Some("enum10")), (2, None)))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.enum1).get.map(_ ==> List(
            (1, List((1, "enum10")))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.enum1_).get.map(_ ==> List(
            (1, List(1))
          ))

          _ <- m(Ns.int.Refs1 *? Ref1.int1.enum1$).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Some("enum10")), (2, None))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.enum1).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, "enum10"))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.enum1_).get.map(_.sortBy(_._1) ==> List(
            (1, List(1)),
            (2, List())
          ))
        } yield ()
      }

      "ref" - core { implicit conn =>
        for {
          _ <- m(Ns.int.Refs1 * Ref1.int1.ref2$) insert List(
            (1, List((1, Some(42L)), (2, None))),
            (2, List())
          )

          _ <- m(Ns.int.Refs1 * Ref1.int1.ref2$).get.map(_ ==> List(
            (1, List((1, Some(42L)), (2, None)))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.ref2).get.map(_ ==> List(
            (1, List((1, 42L)))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.ref2_).get.map(_ ==> List(
            (1, List(1))
          ))

          _ <- m(Ns.int.Refs1 *? Ref1.int1.ref2$).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Some(42L)), (2, None))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.ref2).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, 42L))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.ref2_).get.map(_.sortBy(_._1) ==> List(
            (1, List(1)),
            (2, List())
          ))
        } yield ()
      }
    }

    "card 2" - {

      "attr" - core { implicit conn =>
        for {
          _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$) insert List(
            (1, List((1, Some(Set("a", "b"))), (2, None))),
            (2, List())
          )

          _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$).get.map(_ ==> List(
            (1, List((1, Some(Set("a", "b"))), (2, None))),
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.strs1).get.map(_ ==> List(
            (1, List((1, Set("a", "b")))),
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.strs1_).get.map(_ ==> List(
            (1, List(1)),
          ))

          _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1$).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Some(Set("a", "b"))), (2, None))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Set("a", "b")))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1_).get.map(_.sortBy(_._1) ==> List(
            (1, List(1)),
            (2, List())
          ))
        } yield ()
      }

      "enum" - core { implicit conn =>
        for {
          _ <- m(Ns.int.Refs1 * Ref1.int1.enums1$) insert List(
            (1, List((1, Some(Set("enum10", "enum11"))), (2, None))),
            (2, List())
          )

          _ <- m(Ns.int.Refs1 * Ref1.int1.enums1$).get.map(_ ==> List(
            (1, List((1, Some(Set("enum10", "enum11"))), (2, None)))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.enums1).get.map(_ ==> List(
            (1, List((1, Set("enum10", "enum11"))))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.enums1_).get.map(_ ==> List(
            (1, List(1))
          ))

          _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1$).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Some(Set("enum10", "enum11"))), (2, None))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Set("enum10", "enum11")))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.enums1_).get.map(_.sortBy(_._1) ==> List(
            (1, List(1)),
            (2, List())
          ))
        } yield ()
      }

      "ref" - core { implicit conn =>
        for {
          _ <- m(Ns.int.Refs1 * Ref1.int1.refs2$) insert List(
            (1, List((1, Some(Set(42L, 43L))), (2, None))),
            (2, List())
          )

          _ <- m(Ns.int.Refs1 * Ref1.int1.refs2$).get.map(_ ==> List(
            (1, List((1, Some(Set(42L, 43L))), (2, None)))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.refs2).get.map(_ ==> List(
            (1, List((1, Set(42L, 43L))))
          ))
          _ <- m(Ns.int.Refs1 * Ref1.int1.refs2_).get.map(_ ==> List(
            (1, List(1))
          ))

          _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2$).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Some(Set(42L, 43L))), (2, None))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2).get.map(_.sortBy(_._1) ==> List(
            (1, List((1, Set(42L, 43L)))),
            (2, List())
          ))
          _ <- m(Ns.int.Refs1 *? Ref1.int1.refs2_).get.map(_.sortBy(_._1) ==> List(
            (1, List(1)),
            (2, List())
          ))
        } yield ()
      }
    }

    "card 3 (map)" - core { implicit conn =>
      for {
        _ <- m(Ns.int.Refs1 * Ref1.int1.intMap1$) insert List(
          (1, List((1, Some(Map("a" -> 1, "b" -> 2))), (2, None))),
          (2, List())
        )

        _ <- m(Ns.int.Refs1 * Ref1.int1.intMap1$).get.map(_ ==> List(
          (1, List((1, Some(Map("a" -> 1, "b" -> 2))), (2, None))),
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.intMap1).get.map(_ ==> List(
          (1, List((1, Map("a" -> 1, "b" -> 2)))),
        ))
        _ <- m(Ns.int.Refs1 * Ref1.int1.intMap1_).get.map(_ ==> List(
          (1, List(1)),
        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.intMap1$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(Map("a" -> 1, "b" -> 2))), (2, None))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.intMap1).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Map("a" -> 1, "b" -> 2)))),
          (2, List())
        ))
        _ <- m(Ns.int.Refs1 *? Ref1.int1.intMap1_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "Post attributes after nested" - core { implicit conn =>
      for {
        _ <- Ns.double.str.Refs1.*(Ref1.int1).insert(1.1, "a", Seq(11, 12))

        _ <- Ns.double.str.Refs1.*(Ref1.int1).get.map(_.head ==> (1.1, "a", Seq(11, 12)))

        // Note how we jump back to the namespace (`Ns`) _before_ the nested ns (`Ref1`)
        _ <- Ns.double.Refs1.*(Ref1.int1).str.get.map(_.head ==> (1.1, Seq(11, 12), "a"))
      } yield ()

      // todo?
      //  "Multiple nested?" - core { implicit conn =>
      //
      //    Ns.str.Refs1.*(Ref1.int1).Parents.*(Ref1.int1).insert("a", Seq(11, 12), Seq(21, 22))
      //
      //    Ns.str.Refs1.*(Ref1.int1).Parents.*(Ref1.int1).get.map(_.head ==> ("a", Seq(11, 12), Seq(21, 22)))
      //    } yield ()
    }

    "Applied eid" - core { implicit conn =>
      for {
        eid <- Ns.str.Refs1.*(Ref1.int1).insert("a", List(1, 2)).map(_.eid)
        _ <- Ns(eid).str.get.map(_.head ==> "a")
        _ <- Ns(eid).Refs1.*(Ref1.int1).get.map(_.head ==> List(1, 2))
      } yield ()
    }
  }
}