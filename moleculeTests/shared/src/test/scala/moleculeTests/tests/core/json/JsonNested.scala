package moleculeTests.tests.core.json

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.out11._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object JsonNested extends AsyncTestSuite {

  lazy val tests = Tests {

    "1 nested attr" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1 * Ref1.int1 insert List(
          ("a", List(1)),
          ("b", List(2, 3))
        )

        // Flat
        _ <- Ns.str.Refs1.int1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": {
            |          "int1": 1
            |        }
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": {
            |          "int1": 2
            |        }
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": {
            |          "int1": 3
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // Nested
        _ <- Ns.str.Refs1.*(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Refs1": [
            |          {
            |            "int1": 1
            |          }
            |        ]
            |      },
            |      {
            |        "str": "b",
            |        "Refs1": [
            |          {
            |            "int1": 2
            |          },
            |          {
            |            "int1": 3
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Nested enum after ref" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.enum1) insert List(("a", List("enum11")))
        _ <- m(Ns.str.Refs1 * Ref1.enum1).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ns.refs1": [
            |   {"Ref1.enum1": "enum11"}]}
            |]""".stripMargin)
      } yield ()
    }


    "Nested ref without attribute" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.int1.Ref2.int2) insert List(
          ("a", List((11, 12))),
          ("b", List((21, 22))))


        _ <- m(Ns.str.Refs1 * Ref1.int1_.Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ns.refs1": [
            |   {"ref2.Ref2.int2": 12}]},
            |{"Ns.str": "b", "Ns.refs1": [
            |   {"ref2.Ref2.int2": 22}]}
            |]""".stripMargin)

        // We can omit tacit attribute between Ref1 and Ref2
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ns.refs1": [
            |   {"ref2.Ref2.int2": 12}]},
            |{"Ns.str": "b", "Ns.refs1": [
            |   {"ref2.Ref2.int2": 22}]}
            |]""".stripMargin)
      } yield ()
    }


    "Intermediate references without attributes" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2) insert List(
          ("a", List(10, 20)),
          ("b", List(30))
        )
        _ <- m(Ns.str.Refs1 * Ref1.Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ns.refs1": [
            |   {"ref2.Ref2.int2": 10},
            |   {"ref2.Ref2.int2": 20}]},
            |{"Ns.str": "b", "Ns.refs1": [
            |   {"ref2.Ref2.int2": 30}]}
            |]""".stripMargin)
      } yield ()
    }


    "Intermediate references with optional attributes" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2) insert List(
          ("a", List((Some(1), 10), (None, 20))),
          ("b", List((Some(3), 30)))
        )

        _ <- m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ns.refs1": [
            |   {"Ref1.int1": 1, "ref2.Ref2.int2": 10},
            |   {"Ref1.int1": null, "ref2.Ref2.int2": 20}]},
            |{"Ns.str": "b", "Ns.refs1": [
            |   {"Ref1.int1": 3, "ref2.Ref2.int2": 30}]}
            |]""".stripMargin)
      } yield ()
    }


    "Optional attribute" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.int1$.Refs2 * Ref2.int2) insert List(
          ("a", Some(2), List(20)),
          ("b", None, List(10, 11))
        )

        _ <- m(Ns.str.Refs1.int1$.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "refs1.Ref1.int1": 2, "Ref1.refs2": [
            |   {"Ref2.int2": 20}]},
            |{"Ns.str": "b", "refs1.Ref1.int1": null, "Ref1.refs2": [
            |   {"Ref2.int2": 10},
            |   {"Ref2.int2": 11}]}
            |]""".stripMargin)

        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ref1.refs2": [
            |   {"Ref2.int2": 20}]},
            |{"Ns.str": "b", "Ref1.refs2": [
            |   {"Ref2.int2": 10},
            |   {"Ref2.int2": 11}]}
            |]""".stripMargin)
      } yield ()
    }


    "One - one" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.int2) insert List(("a", 1, List(2)))

        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "ref1.Ref1.int1": 1, "Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)

        _ <- m(Ns.str.Ref1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)
      } yield ()
    }


    "One - one - many" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.ints2) insert List(("a", 1, List(Set(2, 3))))

        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.ints2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "ref1.Ref1.int1": 1, "Ref1.refs2": [
            |   {"Ref2.ints2": [3, 2]}]}
            |]""".stripMargin)
      } yield ()
    }


    "Many - one" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.int1.Refs2 * Ref2.int2) insert List(("a", 1, List(2)))

        _ <- m(Ns.str.Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "refs1.Ref1.int1": 1, "Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)

        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)
      } yield ()
    }


    "Flat ManyRef simple" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.int1.Refs2.int2.insert("a", 1, 2)

        _ <- Ns.str.Refs1.int1.Refs2.int2.getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "refs1.Ref1.int1": 1, "refs2.Ref2.int2": 2}
            |]""".stripMargin)

        _ <- m(Ns.str.Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "refs1.Ref1.int1": 1, "Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)

        _ <- m(Ns.str_("a").Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"refs1.Ref1.int1": 1, "Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)

        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)

        _ <- m(Ns.str_("a").Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)
      } yield ()
    }


    "Flat ManyRef + many with extra attrs" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.*(Ref1.int1.str1.Refs2 * Ref2.int2.str2)) insert List(
          ("a", List(
            (1, "x", List((11, "xx"), (12, "xxx"))),
            (2, "y", List((21, "yy"), (22, "yyy")))))
        )

        _ <- m(Ns.str.Refs1.int1.str1.Refs2 * Ref2.int2.str2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "refs1.Ref1.int1": 1, "refs1.Ref1.str1": "x", "Ref1.refs2": [
            |   {"Ref2.int2": 11, "Ref2.str2": "xx"},
            |   {"Ref2.int2": 12, "Ref2.str2": "xxx"}]},
            |{"Ns.str": "a", "refs1.Ref1.int1": 2, "refs1.Ref1.str1": "y", "Ref1.refs2": [
            |   {"Ref2.int2": 21, "Ref2.str2": "yy"},
            |   {"Ref2.int2": 22, "Ref2.str2": "yyy"}]}
            |]""".stripMargin)
      } yield ()
    }


    "None - one" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2) insert List(("a", List(2)))

        _ <- m(Ns.str.Refs1.Refs2 * Ref2.int2).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ref1.refs2": [
            |   {"Ref2.int2": 2}]}
            |]""".stripMargin)
      } yield ()
    }


    "Flat ManyRef + many" - core { implicit conn =>
      for {
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)) insert List(
          ("a", List(
            (1, List(11, 12)),
            (2, List(21, 22)))),
          ("b", List(
            (3, List(31, 32)),
            (4, List(41, 42))))
        )

        // Fully nested
        _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.*(Ref2.int2)).getJson.map(_ ==>
          """[
            |{"Ns.str": "a", "Ns.refs1": [
            |   {"Ref1.int1": 1, "Ref1.refs2": [
            |      {"Ref2.int2": 11},
            |      {"Ref2.int2": 12}]},
            |   {"Ref1.int1": 2, "Ref1.refs2": [
            |      {"Ref2.int2": 21},
            |      {"Ref2.int2": 22}]}]},
            |{"Ns.str": "b", "Ns.refs1": [
            |   {"Ref1.int1": 3, "Ref1.refs2": [
            |      {"Ref2.int2": 31},
            |      {"Ref2.int2": 32}]},
            |   {"Ref1.int1": 4, "Ref1.refs2": [
            |      {"Ref2.int2": 41},
            |      {"Ref2.int2": 42}]}]}
            |]""".stripMargin)


        // Ordering only stable with Peer
        _ <- if (system == SystemPeer) {
          for {
            // Semi-nested A
            _ <- Ns.str.Refs1.*(Ref1.int1.Refs2.int2).getJson.map(_ ==>
              """[
                |{"Ns.str": "a", "Ns.refs1": [
                |   {"Ref1.int1": 1, "refs2.Ref2.int2": 11},
                |   {"Ref1.int1": 1, "refs2.Ref2.int2": 12},
                |   {"Ref1.int1": 2, "refs2.Ref2.int2": 22},
                |   {"Ref1.int1": 2, "refs2.Ref2.int2": 21}]},
                |{"Ns.str": "b", "Ns.refs1": [
                |   {"Ref1.int1": 3, "refs2.Ref2.int2": 32},
                |   {"Ref1.int1": 3, "refs2.Ref2.int2": 31},
                |   {"Ref1.int1": 4, "refs2.Ref2.int2": 41},
                |   {"Ref1.int1": 4, "refs2.Ref2.int2": 42}]}
                |]""".stripMargin)

            // Semi-nested A without intermediary attr `int1`
            _ <- Ns.str.Refs1.*(Ref1.Refs2.int2).getJson.map(_ ==>
              """[
                |{"Ns.str": "a", "Ns.refs1": [
                |   {"refs2.Ref2.int2": 11},
                |   {"refs2.Ref2.int2": 12},
                |   {"refs2.Ref2.int2": 22},
                |   {"refs2.Ref2.int2": 21}]},
                |{"Ns.str": "b", "Ns.refs1": [
                |   {"refs2.Ref2.int2": 31},
                |   {"refs2.Ref2.int2": 32},
                |   {"refs2.Ref2.int2": 41},
                |   {"refs2.Ref2.int2": 42}]}
                |]""".stripMargin)


            // Semi-nested B
            _ <- Ns.str.Refs1.int1.Refs2.*(Ref2.int2).getJson.map(_ ==>
              """[
                |{"Ns.str": "a", "refs1.Ref1.int1": 1, "Ref1.refs2": [
                |   {"Ref2.int2": 11},
                |   {"Ref2.int2": 12}]},
                |{"Ns.str": "a", "refs1.Ref1.int1": 2, "Ref1.refs2": [
                |   {"Ref2.int2": 21},
                |   {"Ref2.int2": 22}]},
                |{"Ns.str": "b", "refs1.Ref1.int1": 3, "Ref1.refs2": [
                |   {"Ref2.int2": 31},
                |   {"Ref2.int2": 32}]},
                |{"Ns.str": "b", "refs1.Ref1.int1": 4, "Ref1.refs2": [
                |   {"Ref2.int2": 41},
                |   {"Ref2.int2": 42}]}
                |]""".stripMargin)


            // Semi-nested B without intermediary attr `int1`
            _ <- Ns.str.Refs1.Refs2.*(Ref2.int2).getJson.map(_ ==>
              """[
                |{"Ns.str": "a", "Ref1.refs2": [
                |   {"Ref2.int2": 11},
                |   {"Ref2.int2": 12},
                |   {"Ref2.int2": 21},
                |   {"Ref2.int2": 22}]},
                |{"Ns.str": "b", "Ref1.refs2": [
                |   {"Ref2.int2": 31},
                |   {"Ref2.int2": 32},
                |   {"Ref2.int2": 41},
                |   {"Ref2.int2": 42}]}
                |]""".stripMargin)


            // Tacit filter
            _ <- m(Ns.str_("a").Refs1.int1.Refs2 * Ref2.int2).getJson.map(_ ==>
              """[
                |{"refs1.Ref1.int1": 1, "Ref1.refs2": [
                |   {"Ref2.int2": 11},
                |   {"Ref2.int2": 12}]},
                |{"refs1.Ref1.int1": 2, "Ref1.refs2": [
                |   {"Ref2.int2": 21},
                |   {"Ref2.int2": 22}]}
                |]""".stripMargin)

            // Tacit filters
            _ <- m(Ns.str_("a").Refs1.int1_(2).Refs2 * Ref2.int2).getJson.map(_ ==>
              """[
                |{"Ref1.refs2": [
                |   {"Ref2.int2": 21},
                |   {"Ref2.int2": 22}]}
                |]""".stripMargin)


            // Flat
            _ <- m(Ns.str.Refs1.int1.Refs2.int2).getJson.map(_ ==>
              """[
                |{"Ns.str": "a", "refs1.Ref1.int1": 2, "refs2.Ref2.int2": 21},
                |{"Ns.str": "a", "refs1.Ref1.int1": 2, "refs2.Ref2.int2": 22},
                |{"Ns.str": "b", "refs1.Ref1.int1": 4, "refs2.Ref2.int2": 42},
                |{"Ns.str": "b", "refs1.Ref1.int1": 4, "refs2.Ref2.int2": 41},
                |{"Ns.str": "a", "refs1.Ref1.int1": 1, "refs2.Ref2.int2": 12},
                |{"Ns.str": "a", "refs1.Ref1.int1": 1, "refs2.Ref2.int2": 11},
                |{"Ns.str": "b", "refs1.Ref1.int1": 3, "refs2.Ref2.int2": 31},
                |{"Ns.str": "b", "refs1.Ref1.int1": 3, "refs2.Ref2.int2": 32}
                |]""".stripMargin)


            // Flat without intermediary attr `int1`
            res <- m(Ns.str.Refs1.Refs2.int2).getJson.map(_ ==>
              """[
                |{"Ns.str": "a", "refs2.Ref2.int2": 21},
                |{"Ns.str": "a", "refs2.Ref2.int2": 22},
                |{"Ns.str": "b", "refs2.Ref2.int2": 41},
                |{"Ns.str": "b", "refs2.Ref2.int2": 42},
                |{"Ns.str": "a", "refs2.Ref2.int2": 11},
                |{"Ns.str": "a", "refs2.Ref2.int2": 12},
                |{"Ns.str": "b", "refs2.Ref2.int2": 31},
                |{"Ns.str": "b", "refs2.Ref2.int2": 32}
                |]""".stripMargin)
          } yield res
        } else Future.unit
      } yield ()
    }


    "Back ref" - {

      "Nested" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1) insert List(("book", "John", List("Marc")))

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1).getJson.map(_ ==>
            """[
              |{"Ns.str": "book", "ref1.Ref1.str1": "John", "Ns.refs1": [
              |   {"Ref1.str1": "Marc"}]}
              |]""".stripMargin)

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1).getJson.map(_ ==>
            """[
              |{"Ns.str": "book", "ref1.Ref1.str1": "John", "refs1.Ref1.str1": "Marc"}
              |]""".stripMargin)
        } yield ()
      }

      "Nested + adjacent" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1.Refs2.str2) insert List(("book", "John", List(("Marc", "Musician"))))

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1.Refs2.str2).getJson.map(_ ==>
            """[
              |{"Ns.str": "book", "ref1.Ref1.str1": "John", "Ns.refs1": [
              |   {"Ref1.str1": "Marc", "refs2.Ref2.str2": "Musician"}]}
              |]""".stripMargin)

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1.Refs2.str2).getJson.map(_ ==>
            """[
              |{"Ns.str": "book", "ref1.Ref1.str1": "John", "refs1.Ref1.str1": "Marc", "refs2.Ref2.str2": "Musician"}
              |]""".stripMargin)
        } yield ()
      }

      "Nested + nested" - core { implicit conn =>
        for {
          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * (Ref1.str1.Refs2 * Ref2.str2)) insert List(("book", "John", List(("Marc", List("Musician")))))

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * (Ref1.str1.Refs2 * Ref2.str2)).getJson.map(_ ==>
            """[
              |{"Ns.str": "book", "ref1.Ref1.str1": "John", "Ns.refs1": [
              |   {"Ref1.str1": "Marc", "Ref1.refs2": [
              |      {"Ref2.str2": "Musician"}]}]}
              |]""".stripMargin)

          _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1.Refs2.str2).getJson.map(_ ==>
            """[
              |{"Ns.str": "book", "ref1.Ref1.str1": "John", "refs1.Ref1.str1": "Marc", "refs2.Ref2.str2": "Musician"}
              |]""".stripMargin)
        } yield ()
      }
    }


    "Applied eid" - core { implicit conn =>
      for {
        eid <- Ns.str.Refs1.*(Ref1.int1).insert("a", List(1, 2)).map(_.eid)
        _ <- Ns(eid).Refs1.*(Ref1.int1).getJson.map(_ ==>
          """[
            |{"Ns.refs1": [
            |   {"Ref1.int1": 1},
            |   {"Ref1.int1": 2}]}
            |]""".stripMargin)
      } yield ()
    }


    "Post attributes after nested" - core { implicit conn =>
      for {
        _ <- Ns.int.str.Refs1.*(Ref1.int1).insert(1, "a", Seq(11, 12))

        _ <- Ns.int.str.Refs1.*(Ref1.int1).getJson.map(_ ==>
          """[
            |{"Ns.int": 1, "Ns.str": "a", "Ns.refs1": [
            |   {"Ref1.int1": 11},
            |   {"Ref1.int1": 12}]}
            |]""".stripMargin)

        _ <- Ns.int.Refs1.*(Ref1.int1).str.getJson.map(_ ==>
          """[
            |{"Ns.int": 1, "Ns.refs1": [
            |   {"Ref1.int1": 11},
            |   {"Ref1.int1": 12}], "Ns.str": "a"}
            |]""".stripMargin)
      } yield ()
    }


    "Implicit initial namespace" - core { implicit conn =>
      for {
        List(ref1a, _, _, _, _, _) <- Ref1.str1.Refs2.*(Ref2.str2).insert(List(
          ("r1a", List("r2a", "r2b")),
          ("r1b", List("r2c", "r2d")) // <-- will not be referenced from Ns
        )).map(_.eids)

        // Both Ns entities reference the same Ref1 entity
        _ <- Ns.str.refs1 insert List(
          ("a", Set(ref1a)),
          ("b", Set(ref1a))
        )

        // Without Ns
        _ <- Ref1.str1.Refs2.*(Ref2.str2).getJson.map(_ ==>
          """[
            |{"Ref1.str1": "r1a", "Ref1.refs2": [
            |   {"Ref2.str2": "r2a"},
            |   {"Ref2.str2": "r2b"}]},
            |{"Ref1.str1": "r1b", "Ref1.refs2": [
            |   {"Ref2.str2": "r2c"},
            |   {"Ref2.str2": "r2d"}]}
            |]""".stripMargin)

        // With Ns
        // "Implicit" reference from Ns to Ref1 (without any attributes) implies that
        // some Ns entity is referencing some Ref1 entity.
        // This excludes "r1b" since no Ns entities reference it.
        _ <- Ns.Refs1.str1.Refs2.*(Ref2.str2).getJson.map(_ ==>
          """[
            |{"refs1.Ref1.str1": "r1a", "Ref1.refs2": [
            |   {"Ref2.str2": "r2a"},
            |   {"Ref2.str2": "r2b"}]}
            |]""".stripMargin)
      } yield ()
    }
  }
}