package moleculeTests.tests.core.nested

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object NestedRef extends AsyncTestSuite {

  lazy val tests = Tests {

    "ref + opt attr" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2$.str2) insert List(
          ("a", List((Some(11), Some(12), "aa"))),
          ("b", List((Some(13), None, "bb"))),
          ("c", List((None, Some(14), "cc"))),
          ("d", List())
        )

        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2$.str2).get.map(_ ==> List(
          ("a", List((Some(11), Some(12), "aa"))),
          ("b", List((Some(13), None, "bb"))),
          ("c", List((None, Some(14), "cc"))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2.str2).get.map(_ ==> List(
          ("a", List((Some(11), 12, "aa"))),
          ("c", List((None, 14, "cc"))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.int1.Ref2.int2$.str2).get.map(_ ==> List(
          ("a", List((11, Some(12), "aa"))),
          ("b", List((13, None, "bb"))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.int1.Ref2.int2.str2).get.map(_ ==> List(
          ("a", List((11, 12, "aa"))),
        ))

        _ <- m(Ns.str.Refs1 *? Ref1.int1$.Ref2.int2$.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List((Some(11), Some(12), "aa"))),
          ("b", List((Some(13), None, "bb"))),
          ("c", List((None, Some(14), "cc"))),
          ("d", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.int1$.Ref2.int2.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List((Some(11), 12, "aa"))),
          ("b", List()),
          ("c", List((None, 14, "cc"))),
          ("d", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.int1.Ref2.int2$.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List((11, Some(12), "aa"))),
          ("b", List((13, None, "bb"))),
          ("c", List()),
          ("d", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.int1.Ref2.int2.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List((11, 12, "aa"))),
          ("b", List()),
          ("c", List()),
          ("d", List())
        ))
      } yield ()
    }


    "ref + tacit attr" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2$.str2) insert List(
          ("a", List((Some(11), Some(12), "aa"))),
          ("b", List((Some(13), None, "bb"))),
          ("c", List((None, Some(14), "cc"))),
          ("d", List())
        )

        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2_.str2).get.map(_ ==> List(
          ("a", List((Some(11), "aa"))),
          ("c", List((None, "cc"))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.int1.Ref2.int2_.str2).get.map(_ ==> List(
          ("a", List((11, "aa"))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.int1_.Ref2.int2$.str2).get.map(_ ==> List(
          ("a", List((Some(12), "aa"))),
          ("b", List((None, "bb"))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.int1_.Ref2.int2.str2).get.map(_ ==> List(
          ("a", List((12, "aa"))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.int1_.Ref2.int2_.str2).get.map(_ ==> List(
          ("a", List("aa")),
        ))

        _ <- m(Ns.str.Refs1 *? Ref1.int1$.Ref2.int2_.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List((Some(11), "aa"))),
          ("b", List()),
          ("c", List((None, "cc"))),
          ("d", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.int1.Ref2.int2_.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List((11, "aa"))),
          ("b", List()),
          ("c", List()),
          ("d", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.int1_.Ref2.int2$.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List((Some(12), "aa"))),
          ("b", List((None, "bb"))),
          ("c", List()),
          ("d", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.int1_.Ref2.int2.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List((12, "aa"))),
          ("b", List()),
          ("c", List()),
          ("d", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.int1_.Ref2.int2_.str2).get.map(_.sortBy(_._1) ==> List(
          ("a", List("aa")),
          ("b", List()),
          ("c", List()),
          ("d", List())
        ))
      } yield ()
    }


    "Intermediate references without attributes" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2.str2$) insert List(
          ("A", List((10, Some("a")), (20, None))),
          ("B", List())
        )

        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2.str2$).get.map(_ ==> List(
          ("A", List((10, Some("a")), (20, None))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2.str2).get.map(_ ==> List(
          ("A", List((10, "a"))),
        ))
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2.str2_).get.map(_ ==> List(
          ("A", List(10)),
        ))

        _ <- m(Ns.str.Refs1 *? Ref1.Ref2.int2.str2$).get.map(_.sortBy(_._1) ==> List(
          ("A", List((10, Some("a")), (20, None))),
          ("B", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.Ref2.int2.str2).get.map(_.sortBy(_._1) ==> List(
          ("A", List((10, "a"))),
          ("B", List())
        ))
        _ <- m(Ns.str.Refs1 *? Ref1.Ref2.int2.str2_).get.map(_.sortBy(_._1) ==> List(
          ("A", List(10)),
          ("B", List())
        ))
      } yield ()
    }


    "Flat card many refs" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)) insert List(
          ("a", List(
            (1, List(11, 12)),
            (2, List()),
          )),
          ("b", List())
        )

        // Flat card many ref allowed in mandatory nested structure
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.int2).get.map(_.map(p => (p._1, p._2.sorted)) ==> List(
          ("a", List(
            (1, 11),
            (1, 12),
          ))
        ))

        // Flat card many refs not allowed in optional nested structure
        _ = expectCompileError("m(Ns.str.Refs1.*?(Ref1.int1.Refs2.int2))",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Flat card many ref not allowed with optional nesting. " +
            """Found: Bond("Ref1", "refs2", "Ref2", 2, Seq())""")


        // Flat card many refs before nested structure
        _ <- Ns.str.Refs1.int1.Refs2.*(Ref2.int2).get.map(_ ==> List(
          ("a", 1, List(11, 12))
        ))
        _ <- Ns.str.Refs1.int1.Refs2.*?(Ref2.int2).get.map(_ ==> List(
          ("a", 1, List(11, 12)),
          ("a", 2, List()),
        ))

        // Implicit ref
        _ <- Ns.str.Refs1.Refs2.*?(Ref2.int2).get.map(_.sortBy(_._2.size) ==> List(
          ("a", List()),
          ("a", List(11, 12))
        ))
        _ <- Ns.str.Refs1.Refs2.*(Ref2.int2).get.map(_ ==> List(
          ("a", List(11, 12))
        ))
      } yield ()
    }


    "Back ref" - {

      "Nested" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1) insert List(("book", "John", List("Marc")))

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1).get.map(_ ==> List(("book", "John", List("Marc"))))
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1).get.map(_ ==> List(("book", "John", "Marc")))
        } yield ()
      }

      "Nested + adjacent" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1.Refs2.str2) insert List(("book", "John", List(("Marc", "Musician"))))

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1.Refs2.str2).get.map(_ ==> List(("book", "John", List(("Marc", "Musician")))))
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1.Refs2.str2).get.map(_ ==> List(("book", "John", "Marc", "Musician")))
        } yield ()
      }

      "Nested + nested" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * (Ref1.str1.Refs2 * Ref2.str2)) insert List(("book", "John", List(("Marc", List("Musician")))))

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * (Ref1.str1.Refs2 * Ref2.str2)).get.map(_ ==> List(("book", "John", List(("Marc", List("Musician"))))))
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1.Refs2.str2).get.map(_ ==> List(("book", "John", "Marc", "Musician")))
        } yield ()
      }
    }

    "Flat ref before nested" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.int1$.Refs2 * Ref2.int2) insert List(
          ("a", Some(1), List(1)),
          ("b", None, List(2)),
          ("c", Some(3), List()),
          ("d", None, List()),
        )

        _ <- m(Ns.str.Refs1.int1$.Refs2 * Ref2.int2).get.map(_ ==> List(
          ("a", Some(1), List(1)),
          ("b", None, List(2)),
        ))
        _ <- m(Ns.str.Refs1.int1.Refs2 * Ref2.int2).get.map(_ ==> List(
          ("a", 1, List(1)),
        ))
        _ <- m(Ns.str.Refs1.int1_.Refs2 * Ref2.int2).get.map(_ ==> List(
          ("a", List(1)),
        ))
        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).get.map(_ ==> List(
          ("a", List(1)),
          ("b", List(2)),
        ))

        _ <- m(Ns.str.Refs1.int1$.Refs2 *? Ref2.int2).get.map(_.sortBy(_._1) ==> List(
          ("a", Some(1), List(1)),
          ("b", None, List(2)),
          ("c", Some(3), List())
        ))
        _ <- m(Ns.str.Refs1.int1.Refs2 *? Ref2.int2).get.map(_.sortBy(_._1) ==> List(
          ("a", 1, List(1)),
          ("c", 3, List()),
        ))
        _ <- m(Ns.str.Refs1.int1_.Refs2 *? Ref2.int2).get.map(_.sortBy(_._1) ==> List(
          ("a", List(1)),
          ("c", List()),
        ))
        _ <- m(Ns.str.Refs1.Refs2 *? Ref2.int2).get.map(_.sortBy(_._1) ==> List(
          ("a", List(1)),
          ("b", List(2)),
          ("c", List()),
        ))
      } yield ()
    }


    "Opt Date before nested" - core { implicit conn =>
      for {
        _ <- m(Ns.date$.Refs1.int1.Refs2 * Ref2.int2) insert List(
          (Some(date1), 10, List(1, 2)),
          (Some(date2), 20, List(3)),
          (None, 30, List()),
          (Some(date4), 40, List()),
        )

        _ <- m(Ns.date$.Refs1.int1.Refs2 *? Ref2.int2).get.map(_.sortBy(_._2) ==> List(
          (Some(date1), 10, List(1, 2)),
          (Some(date2), 20, List(3)),
          (None, 30, List()),
          (Some(date4), 40, List())
        ))
      } yield ()
    }


    "Implicit initial namespace" - core { implicit conn =>
      for {
        List(ref1a, _, _, _, _, _) <- Ref1.str1.Refs2.*(Ref2.str2) insert List(
          ("r1a", List("r2a", "r2b")),
          ("r1b", List("r2c", "r2d")) // <-- will not be referenced from Ns
        ) map(_.eids)

        // Both Ns entities reference the same Ref1 entity
        _ <- Ns.str.refs1 insert List(
          ("a", Set(ref1a)),
          ("b", Set(ref1a))
        )

        // Without Ns
        _ <- Ref1.str1.Refs2.*(Ref2.str2).get.map(_ ==> List(
          ("r1a", List("r2a", "r2b")),
          ("r1b", List("r2c", "r2d"))
        ))

        // With Ns
        // "Implicit" reference from Ns to Ref1 (without any attributes) implies that
        // some Ns entity is referencing some Ref1 entity.
        // This excludes "r1b" since no Ns entities reference it.
        _ <- Ns.Refs1.str1.Refs2.*(Ref2.str2).get.map(_ ==> List(
          ("r1a", List("r2a", "r2b"))
        ))
      } yield ()
    }

    "Applied eid" - core { implicit conn =>
      for {
        eid <- Ns.str.Refs1.*(Ref1.int1).insert("a", List(1, 2)).map(_.eid)
        _ <- Ns(eid).str.get.map(_.head ==> "a")
        _ <- Ns(eid).Refs1.*(Ref1.int1).get.map(_.head ==> List(1, 2))
      } yield ()
    }


    "Unrelated nested" - core { implicit conn =>
      expectCompileError("m(Ns.int.Refs1 * Ref2.int2)",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "`Refs1` can only nest to `Ref1`. Found: `Ref2`"
      )
    }


    "Post attributes not allowed" - core { implicit conn =>
      expectCompileError("m(Ns.int.Refs1.*(Ref1.int1).str)",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "Attributes after nested structure not allowed (only Tx meta data is allowed)."
      )
    }

    // todo
//    "No post attributes except tx meta data" - core { implicit conn =>
//      expectCompileError("m(Ns.double.Refs1.*(Ref1.int1).str)",
//        "molecule.core.transform.exception.Dsl2ModelException: " +
//          "Only Tx meta data allowed after nested structure. Found: `str`"
//      )
//    }
  }
}