package moleculeTests.tests.core.attr

import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out5._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object OptionalValues extends AsyncTestSuite {

  lazy val tests = Tests {

    "Correct card-one types returned" - {

      "String (no assertion)" - core { implicit conn =>
        for {
          _ <- Ns.int.str.insert(1, "a")
          _ <- Ns.int.insert(2)

          // Int mandatory, String optional
          _ <- Ns.int.str$.get.map(_.sortBy(_._1) ==> List((1, Some("a")), (2, None)))

          // Int and String mandatory
          _ <- Ns.int.str.get.map(_ ==> List((1, "a")))
        } yield ()
      }

      "String (optional assertion)" - core { implicit conn =>
        for {
          _ <- Ns.int.str$ insert List((1, Some("a")), (2, None))

          _ <- Ns.int.str$.get.map(_.sortBy(_._1) ==> List((1, Some("a")), (2, None)))
          _ <- Ns.int.str.get.map(_ ==> List((1, "a")))
        } yield ()
      }

      "Int" - core { implicit conn =>
        for {
          _ <- Ns.str.int$ insert List(("a", Some(1)), ("b", None))

          _ <- Ns.str.int$.get.map(_.sortBy(_._1) ==> List(("a", Some(1)), ("b", None)))
          _ <- Ns.str.int.get.map(_ ==> List(("a", 1)))
        } yield ()
      }

      "Long" - core { implicit conn =>
        for {
          _ <- Ns.int.long$ insert List((1, Some(3L)), (2, None))

          _ <- Ns.int.long$.get.map(_.sortBy(_._1) ==> List((1, Some(3L)), (2, None)))
          _ <- Ns.int.long.get.map(_ ==> List((1, 3L)))
        } yield ()
      }

      "Boolean" - core { implicit conn =>
        for {
          _ <- Ns.int.bool$ insert List((1, Some(true)), (2, None))

          _ <- Ns.int.bool$.get.map(_.sortBy(_._1) ==> List((1, Some(true)), (2, None)))
          _ <- Ns.int.bool.get.map(_ ==> List((1, true)))
        } yield ()
      }

      "Date" - core { implicit conn =>
        for {
          _ <- Ns.int.date$ insert List((1, Some(date1)), (2, None))

          _ <- Ns.int.date$.get.map(_.sortBy(_._1) ==> List((1, Some(date1)), (2, None)))
          _ <- Ns.int.date.get.map(_ ==> List((1, date1)))
        } yield ()
      }

      "UUID" - core { implicit conn =>
        for {
          _ <- Ns.int.uuid$ insert List((1, Some(uuid1)), (2, None))

          _ <- Ns.int.uuid$.get.map(_.sortBy(_._1) ==> List((1, Some(uuid1)), (2, None)))
          _ <- Ns.int.uuid.get.map(_ ==> List((1, uuid1)))
        } yield ()
      }

      "URI" - core { implicit conn =>
        for {
          _ <- Ns.int.uri$ insert List((1, Some(uri1)), (2, None))

          _ <- Ns.int.uri$.get.map(_.sortBy(_._1) ==> List((1, Some(uri1)), (2, None)))
          _ <- Ns.int.uri.get.map(_ ==> List((1, uri1)))
        } yield ()
      }

      "Enum" - core { implicit conn =>
        for {
          _ <- Ns.int.enumm$ insert List((1, Some("enum1")), (2, None))

          _ <- Ns.int.enumm$.get.map(_.sortBy(_._1) ==> List((1, Some("enum1")), (2, None)))
          _ <- Ns.int.enumm.get.map(_ ==> List((1, "enum1")))
        } yield ()
      }

      "Ref Long" - core { implicit conn =>
        for {
          r3 <- Ref1.int1(3).save.map(_.eid)
          _ <- Ns.int.ref1$ insert List((1, Some(r3)), (2, None))

          _ <- Ns.int.ref1$.get.map(_.sortBy(_._1) ==> List((1, Some(r3)), (2, None)))
          _ <- Ns.int.ref1.get.map(_ ==> List((1, r3)))
        } yield ()
      }
    }


    "Correct card-many types returned" - {

      "String (no assertion)" - core { implicit conn =>
        for {
          _ <- Ns.int.strs.insert(1, Set("a", "b"))
          _ <- Ns.int.insert(2)

          _ <- Ns.int.strs$.get.map(_.sortBy(_._1) ==> List((1, Some(Set("a", "b"))), (2, None)))
          _ <- Ns.int.strs.get.map(_ ==> List((1, Set("a", "b"))))
        } yield ()
      }

      "String (empty Set asserted)" - core { implicit conn =>
        for {
          _ <- Ns.int.strs.insert(1, Set("a", "b"))
          // No strings asserted from empty Set
          _ <- Ns.int.strs.insert(2, Set[String]())

          _ <- Ns.int.strs$.get.map(_.sortBy(_._1) ==> List((1, Some(Set("a", "b"))), (2, None)))
          _ <- Ns.int.strs.get.map(_ ==> List((1, Set("a", "b"))))
        } yield ()
      }

      "String (optional assertion)" - core { implicit conn =>
        for {
          _ <- Ns.int.strs$ insert Seq((1, Some(Set("a", "b"))), (2, None))

          _ <- Ns.int.strs$.get.map(_.sortBy(_._1) ==> List((1, Some(Set("a", "b"))), (2, None)))
          _ <- Ns.int.strs.get.map(_ ==> List((1, Set("a", "b"))))
        } yield ()
      }

      "Int" - core { implicit conn =>
        for {
          _ <- Ns.str.ints$ insert List(("a", Some(Set(1, 2))), ("b", None))

          _ <- Ns.str.ints$.get.map(_.sortBy(_._1) ==> List(("a", Some(Set(1, 2))), ("b", None)))
          _ <- Ns.str.ints.get.map(_ ==> List(("a", Set(1, 2))))
        } yield ()
      }

      "Long" - core { implicit conn =>
        for {
          _ <- Ns.int.longs$ insert Seq((1, Some(Set(3L, 4L))), (2, None))

          _ <- Ns.int.longs$.get.map(_.sortBy(_._1) ==> List((1, Some(Set(3L, 4L))), (2, None)))
          _ <- Ns.int.longs.get.map(_ ==> List((1, Set(3L, 4L))))
        } yield ()
      }

      // (Boolean Sets not implemented)

      "Date" - core { implicit conn =>
        for {
          _ <- Ns.int.dates$ insert Seq((1, Some(Set(date1, date2))), (2, None))

          _ <- Ns.int.dates$.get.map(_.sortBy(_._1) ==> List((1, Some(Set(date1, date2))), (2, None)))
          _ <- Ns.int.dates.get.map(_ ==> List((1, Set(date1, date2))))
        } yield ()
      }

      "UUID" - core { implicit conn =>
        for {
          _ <- Ns.int.uuids$ insert Seq((1, Some(Set(uuid1, uuid2))), (2, None))

          _ <- Ns.int.uuids$.get.map(_.sortBy(_._1) ==> List((1, Some(Set(uuid1, uuid2))), (2, None)))
          _ <- Ns.int.uuids.get.map(_ ==> List((1, Set(uuid1, uuid2))))
        } yield ()
      }

      "URI" - core { implicit conn =>
        for {
          _ <- Ns.int.uris$ insert Seq((1, Some(Set(uri1, uri2))), (2, None))

          _ <- Ns.int.uris$.get.map(_.sortBy(_._1) ==> List((1, Some(Set(uri1, uri2))), (2, None)))
          _ <- Ns.int.uris.get.map(_ ==> List((1, Set(uri1, uri2))))
        } yield ()
      }

      "Enum" - core { implicit conn =>
        for {
          _ <- Ns.int.enums$ insert Seq((1, Some(Set("enum1", "enum2"))), (2, None))

          _ <- Ns.int.enums$.get.map(_.sortBy(_._1) ==> List((1, Some(Set("enum1", "enum2"))), (2, None)))
          _ <- Ns.int.enums.get.map(_ ==> List((1, Set("enum1", "enum2"))))
        } yield ()
      }

      "Ref" - core { implicit conn =>
        for {
          List(r3, r4) <- Ref1.int1.insert(3, 4).map(_.eids)
          _ <- Ns.int.refs1$ insert Seq((1, Some(Set(r3, r4))), (2, None))

          _ <- Ns.int.refs1$.get.map(_.sortBy(_._1) ==> List((1, Some(Set(r3, r4))), (2, None)))
          _ <- Ns.int.refs1.get.map(_ ==> List((1, Set(r3, r4))))
        } yield ()
      }

      "Ref with sub components" - core { implicit conn =>
        for {
          List(r3, r4) <- Ref1.int1.insert(3, 4).map(_.eids)
          _ <- Ns.int.refsSub1$ insert Seq((1, Some(Set(r3, r4))), (2, None))

          _ <- Ns.int.refsSub1$.get.map(_.sortBy(_._1) ==> List((1, Some(Set(r3, r4))), (2, None)))
          _ <- Ns.int.refsSub1.get.map(_ ==> List((1, Set(r3, r4))))
        } yield ()
      }
    }


    "Multiple optional attributes" - {

      "One namespace" - core { implicit conn =>
        for {
          _ <- Ns.str.int$.long$ insert List(
            ("a", Some(1), Some(10L)),
            ("b", None, Some(20L)),
            ("c", Some(3), None),
            ("d", None, None))

          _ <- Ns.str.int$.long$.get.map(_.sortBy(_._1) ==> List(
            ("a", Some(1), Some(10L)),
            ("b", None, Some(20L)),
            ("c", Some(3), None),
            ("d", None, None)))

          // We don't have to retrieve the attribute values in the same order as inserted
          _ <- Ns.int$.str.long$.get.map(_.sortBy(_._2) ==> List(
            (Some(1), "a", Some(10L)),
            (None, "b", Some(20L)),
            (Some(3), "c", None),
            (None, "d", None),
          ))
        } yield ()
      }
    }

    "Ref optionals" - {

      "Ref attribute can be optional (1)" - core { implicit conn =>
        for {
          _ <- Ns.str.Ref1.str1.int1$ insert List(
            ("a", "a1", Some(11)),
            ("b", "b1", None))

          // Now there's a ref from entity with "b" to entity with "b1"
          _ <- Ns.str.Ref1.str1.int1$.get.map(_.sortBy(_._1) ==> List(
            ("a", "a1", Some(11)),
            ("b", "b1", None),
          ))
        } yield ()
      }

      "Ref attribute can be optional (2)" - core { implicit conn =>
        for {
          _ <- Ns.str.Ref1.str1$.int1 insert List(
            ("a", None, 11),
            ("b", Some("b1"), 21),
          )

          _ <- Ns.str.Ref1.str1$.int1.get.map(_.sortBy(_._1) ==> List(
            ("a", None, 11),
            ("b", Some("b1"), 21),
          ))
        } yield ()
      }

      "Nested attribute can be optional" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Refs1 * Ref1.str1.int1$) insert List(
            ("a", List(("a1", Some(11)))),
            ("b", List(("b1", None))))

          // Now there's a ref from entity with "b" to entity with "b1"
          _ <- m(Ns.str.Refs1 * Ref1.str1.int1$).get.map(_.sortBy(_._1) ==> List(
            ("a", List(("a1", Some(11)))),
            ("b", List(("b1", None)))))
        } yield ()
      }

      "Ref enum" - core { implicit conn =>
        for {
          _ <- Ns.str.Ref1.str1.enum1$ insert List(
            ("a", "a1", Some("enum10")),
            ("b", "b1", None)
          )

          _ <- Ns.str.Ref1.str1.enum1$.get.map(_.sortBy(_._1) ==> List(
            ("a", "a1", Some("enum10")),
            ("b", "b1", None),
          ))
        } yield ()
      }

      "Nested enum" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Refs1 * Ref1.str1.int1$.enum1$) insert List(
            ("a", List(("a1", Some(11), None))),
            ("b", List(("b1", None, Some("enum12")))))

          _ <- m(Ns.str.Refs1 * Ref1.str1.int1$.enum1$).get.map(_.sortBy(_._1) ==> List(
            ("a", List(("a1", Some(11), None))),
            ("b", List(("b1", None, Some("enum12"))))))
        } yield ()
      }
    }


    "Ref optionals, 2 levels" - {

      "Adjacent" - core { implicit conn =>
        for {
          _ <- Ns.str.Ref1.str1$.int1.Ref2.str2.int2$ insert List(
            ("a", None, 11, "a2", Some(12)),
            ("b", Some("b1"), 21, "b2", None))

          _ <- Ns.str.Ref1.str1$.int1.Ref2.str2.int2$.get.map(_.sortBy(_._1) ==> List(
            ("a", None, 11, "a2", Some(12)),
            ("b", Some("b1"), 21, "b2", None)
          ))
        } yield ()
      }

      "Nested" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Refs1 * (Ref1.str1$.int1.Refs2 * Ref2.str2.int2$)) insert List(
            ("a", List(
              (None, 11, List(
                ("a2", Some(12)))))),
            ("b", List(
              (Some("b1"), 21, List(
                ("b2", None))))))

          _ <- m(Ns.str.Refs1 * (Ref1.str1$.int1.Refs2 * Ref2.str2.int2$)).get.map(_.sortBy(_._1) ==> List(
            ("a", List(
              (None, 11, List(
                ("a2", Some(12)))))),
            ("b", List(
              (Some("b1"), 21, List(
                ("b2", None)))))))
        } yield ()
      }
    }


    "Mixing optional and tacit attributes" - {

      "Ok in query" - core { implicit conn =>
        for {
          _ <- Ns.str.int$ insert List(
            ("a", Some(1)),
            ("b", None))

          _ <- m(Ns.str_.int$).get.map(_ ==> List(
            Some(1),
            None))
        } yield ()
      }

      "No tacit attributes in insert molecule" - core { implicit conn =>
        m(Ns.str_.int$).insert(Some(1))
          .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
          err ==> "[noTacitAttrs]  Tacit attributes like `str_` not allowed in insert molecules."
        }
      }
    }

    "Optional insert before ref" - core { implicit conn =>
      for {
        // Optional before ref
        _ <- Ns.str$.Ref1.int1 insert List(
          (Some("a"), 1),
          (None, 2)
        )
        _ <- Ns.str$.Ref1.int1.get.map(_.sortBy(_._2) ==> List((Some("a"), 1), (None, 2)))
      } yield ()
    }

    "Optional insert after ref" - core { implicit conn =>
      for {
        List(e1, r1, e2) <- Ref2.str2.Ref3.int3$ insert List(
          ("c", Some(3)),
          ("d", None) // asserts "d", but no relationship is created!
        ) map (_.eids)

        // Entities created for both "c" and "d"
        _ <- Ref2.e.str2.get.map(_.sortBy(_._2) ==> List((e1, "c"), (e2, "d")))

        // No relationship created for "d"
        _ <- Ref2.e.str2.ref3.get.map(_ ==> List((e1, "c", r1)))
        _ <- Ref2.str2.Ref3.int3$.get.map(_ ==> List(("c", Some(3))))
      } yield ()
    }


    "Optional save before ref" - core { implicit conn =>
      for {
        _ <- Ns.str$(Some("a")).Ref1.int1(1).save
        _ <- Ns.str$(None).Ref1.int1(2).save

        _ <- Ns.str$.Ref1.int1.get.map(_.sortBy(_._2) ==> List((Some("a"), 1), (None, 2)))
      } yield ()
    }

    "Optional save after ref" - core { implicit conn =>
      for {
        List(e1, r1) <- Ref2.str2("c").Ref3.int3$(Some(3)).save.map(_.eids)
        // asserts "d", but no relationship is created!
        List(e2) <- Ref2.str2("d").Ref3.int3$(None).save.map(_.eids)

        // Entities created for both "c" and "d"
        _ <- Ref2.e.str2.get.map(_.sortBy(_._2) ==> List((e1, "c"), (e2, "d")))

        // No relationship created for "d"
        _ <- Ref2.e.str2.ref3.get.map(_ ==> List((e1, "c", r1)))
        _ <- Ref2.str2.Ref3.int3$.get.map(_ ==> List(("c", Some(3))))
      } yield ()
    }


    "Only optional attributes" - core { implicit conn =>
      expectCompileError("m(Ns.str$)",
        "molecule.core.ops.exception.VerifyRawModelException: " +
          "Molecule has only optional attributes. Please add one or more mandatory/tacit attributes.")

      expectCompileError("m(Ns.str$.int$)",
        "molecule.core.ops.exception.VerifyRawModelException: " +
          "Molecule has only optional attributes. Please add one or more mandatory/tacit attributes.")
    }


    "Apply optional value" - core { implicit conn =>
      for {
        _ <- Ns.str.int$ insert List(("Ann", Some(37)), ("Ben", None))
        _ <- m(Ns.str.int$(Some(37))).get.map(_ ==> List(("Ann", Some(37))))
        _ <- m(Ns.str.int$(None)).get.map(_ ==> List(("Ben", None)))


        _ <- Ns.int.enumm$ insert List((1, Some("enum1")), (2, None))
        _ <- m(Ns.int(1).enumm$(Some("enum1"))).get.map(_ ==> List((1, Some("enum1"))))
        _ <- m(Ns.int(2).enumm$(None)).get.map(_ ==> List((2, None)))
        noEnum = Option.empty[String]
        _ <- m(Ns.int(2).enumm$(noEnum)).get.map(_ ==> List((2, None)))

        _ <- Ns.int.enums$ insert List((3, Some(Set("enum1"))), (4, None))
        _ <- m(Ns.int(3).enums$(Some(Set("enum1")))).get.map(_ ==> List((3, Some(Set("enum1")))))
        _ <- m(Ns.int(4).enums$(None)).get.map(_ ==> List((4, None)))

        noEnums = Option.empty[Set[String]]
        _ <- m(Ns.int(4).enums$(noEnums)).get.map(_ ==> List((4, None)))
      } yield ()
    }


    "Allowing duplicate optional attributes" - core { implicit conn =>
      for {
        _ <- Ns.str.int$ insert List(
          ("a", Some(1)),
          ("b", None)
        )

        // Normally this would not make sense but we use it in MoleculeAdmin
        // to retrieve a duplicate column of values to edit.
        _ <- Ns.str.int$.int$.get.map(_.sortBy(_._1) ==> List(
          ("a", Some(1), Some(1)),
          ("b", None, None)
        ))
        _ <- Ns.str.int$.int.get.map(_ ==> List(
          ("a", Some(1), 1)
        ))
        _ <- Ns.str.int.int$.get.map(_ ==> List(
          ("a", 1, Some(1))
        ))
        _ <- Ns.str.int.int.get.map(_ ==> List(
          ("a", 1, 1)
        ))
      } yield ()
    }


    "All optional card-many values returned" - core { implicit conn =>
      // Datomic by default returns max 1000 values from a pull expression.
      // Molecule returns all values:

      val ints = (1 to 1111).toSet

      for {
        _ <- Ns.str.ints$ insert List(
          ("a", Some(ints)),
          ("b", None),
        )

        _ <- Ns.str("a").ints$.get.map(_.head._2.get.size ==> 1111)
      } yield ()
    }


    "Fulltext search on optional card-one attribute" - core { implicit conn =>
      for {
        _ <- Ns.int.str$ insert List(
          (0, Some("hello world")),
          (1, Some("hi there")),
          (2, None),
        )

        // Equality matching full search string
        _ <- Ns.int.str("hi there").get.map(_ ==> List(
          (1, "hi there"),
        ))
        _ <- Ns.int.str$(Some("hi there")).get.map(_ ==> List(
          (1, Some("hi there")),
        ))
        _ <- Ns.int.str$(Some("hi")).get.map(_ ==> List())

        _ <- if (system == SystemPeer) {
          // Fulltext matching a single full word
          Ns.int.str$.contains("hi").get.map(_ ==> List(
            (1, Some("hi there")),
          ))
        } else Future.unit
      } yield ()
    }


    "Fulltext search on optional card-many attribute" - core { implicit conn =>
      for {
        _ <- Ns.int.strs$ insert List(
          (0, Some(Set("hello world"))),
          (1, Some(Set("hi there", "hi five"))),
          (2, None),
        )

        // Equality matching full search string
        _ <- Ns.int.strs(Set("hi there")).get.map(_ ==> List(
          (1, Set("hi there", "hi five")),
        ))
        _ <- Ns.int.strs$(Some(Set("hi there"))).get.map(_ ==> List(
          (1, Some(Set("hi there", "hi five"))),
        ))
        _ <- Ns.int.strs(Set("hi")).get.map(_ ==> List())
        _ <- Ns.int.strs$(Some(Set("hi"))).get.map(_ ==> List())

        _ <- if (system == SystemPeer) {
          // Fulltext matching a single full word
          Ns.int.strs$.contains("hi").get.map(_ ==> List(
            (1, Some(Set("hi there", "hi five"))),
          ))
        } else Future.unit
      } yield ()
    }
  }
}
