package moleculeTests.tests.core.input3

import molecule.core.api.exception.Molecule_3_Exception
import molecule.datomic.api.in3_out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object Input3examples extends AsyncTestSuite {

  lazy val tests = Tests {

    "6 ways to apply input to arity-3 input molecule" - core { implicit conn =>
      val inputMolecule = m(Ns.str.int_(?).long_(?).double_(?))
      for {
        _ <- Ns.str.int.long.double insert List(
          ("Ann", 37, 5L, 1.0),
          ("Ben", 28, 5L, 1.0),
          ("Joe", 28, 4L, 1.0),
          ("Liz", 28, 3L, 2.0)
        )

        // Triples of input ..............................................................

        // Varargs (single triple)
        _ <- inputMolecule(37, 5L, 1.0).get.map(_ ==> List("Ann"))

        // One or more triples
        _ <- inputMolecule((37, 5L, 1.0)).get.map(_ ==> List("Ann"))
        _ <- inputMolecule((37, 5L, 1.0), (28, 5L, 1.0)).get.map(_.sorted ==> List("Ann", "Ben"))

        // One or more logical triples
        _ <- inputMolecule((37 and 5L and 1.0) or (28 and 4L and 1.0)).get.map(_.sorted ==> List("Ann", "Joe"))
        _ <- inputMolecule((37 and 5L and 1.0) or (28 and 4L and 1.0) or (28 and 3L and 2.0)).get.map(_.sorted ==> List("Ann", "Joe", "Liz"))

        // List of triples
        _ <- inputMolecule(Seq((37, 5L, 1.0))).get.map(_ ==> List("Ann"))
        _ <- inputMolecule(Seq((37, 5L, 1.0), (28, 5L, 1.0))).get.map(_.sorted ==> List("Ann", "Ben"))


        // 3 groups of input, each group matches an input attribute ......................

        _ <- inputMolecule(Seq(37), Seq(5L), Seq(1.0)).get.map(_ ==> List("Ann"))

        _ <- inputMolecule(Seq(37, 28), Seq(5L), Seq(1.0)).get.map(_.sorted ==> List("Ann", "Ben"))
        _ <- inputMolecule(Seq(37), Seq(5L, 4L), Seq(1.0)).get.map(_.sorted ==> List("Ann"))
        _ <- inputMolecule(Seq(37), Seq(5L), Seq(1.0, 2.0)).get.map(_.sorted ==> List("Ann"))

        _ <- inputMolecule(Seq(37, 28), Seq(5L, 4L), Seq(1.0)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
        _ <- inputMolecule(Seq(37, 28), Seq(5L), Seq(1.0, 2.0)).get.map(_.sorted ==> List("Ann", "Ben"))
        _ <- inputMolecule(Seq(28), Seq(5L, 4L), Seq(1.0, 2.0)).get.map(_.sorted ==> List("Ben", "Joe"))

        _ <- inputMolecule(Seq(37, 28), Seq(5L, 4L), Seq(1.0, 2.0)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))

        // Nil as any input returns Nil
        _ <- inputMolecule(Seq(37), Seq(5L), Nil).get.map(_ ==> Nil)
        _ <- inputMolecule(Seq(37), Nil, Seq(1.0)).get.map(_ ==> Nil)
        _ <- inputMolecule(Nil, Seq(5L), Seq(1.0)).get.map(_ ==> Nil)


        // 3 groups all with 1 value
        _ <- inputMolecule(37 and 5L and 1.0).get.map(_ ==> List("Ann"))

        // 1 group with 2 values
        _ <- inputMolecule((37 or 28) and 5L and 1.0).get.map(_.sorted ==> List("Ann", "Ben"))
        _ <- inputMolecule(37 and (5L or 4L) and 1.0).get.map(_.sorted ==> List("Ann"))
        _ <- inputMolecule(37 and 5L and (1.0 or 2.0)).get.map(_.sorted ==> List("Ann"))

        // 2 groups with 2 values
        _ <- inputMolecule((37 or 28) and (5L or 4L) and 1.0).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
        _ <- inputMolecule((37 or 28) and 5L and (1.0 or 2.0)).get.map(_.sorted ==> List("Ann", "Ben"))
        _ <- inputMolecule(37 and (5L or 4L) and (1.0 or 2.0)).get.map(_.sorted ==> List("Ann"))

        // 3 groups with 2 values
        _ <- inputMolecule((37 or 28) and (5L or 4L) and (1.0 or 2.0)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
      } yield ()
    }


    "expressions" - core { implicit conn =>
      val inputExpression = m(Ns.str.int_.not(?).long_.>(?).double_.<=(?))
      for {
        _ <- Ns.str.int.long.double insert List(
          ("a", 1, 1L, 1.0),
          ("b", 1, 1L, 1.0),
          ("c", 2, 1L, 1.0),
          ("d", 3, 2L, 1.0),
          ("e", 4, 3L, 2.0)
        )

        // Individual args matching input attributes
        _ <- inputExpression(1, 0L, 1.0).get.map(_ ==> List("c", "d"))
        _ <- inputExpression(1, 1L, 2.0).get.map(_ ==> List("d", "e"))

        // Triple matching input attributes
        _ <- inputExpression((1, 0L, 1.0)).get.map(_ ==> List("c", "d"))
        _ <- inputExpression((1, 1L, 2.0)).get.map(_ ==> List("d", "e"))

        // Seq of values matching input attributes
        _ <- inputExpression(Seq((1, 0L, 1.0))).get.map(_ ==> List("c", "d"))
        _ <- inputExpression(Seq((1, 1L, 2.0))).get.map(_ ==> List("d", "e"))


        // <expression1> and <expression2> and <expression3>
        _ <- inputExpression(1 and 0L and 1.0).get.map(_ ==> List("c", "d"))
        _ <- inputExpression(1 and 1L and 2.0).get.map(_ ==> List("d", "e"))

        // Two sequences, each matching corresponding input attribute
        _ <- inputExpression(Seq(1), Seq(0L), Seq(1.0)).get.map(_ ==> List("c", "d"))
        _ <- inputExpression(Seq(1), Seq(1L), Seq(2.0)).get.map(_ ==> List("d", "e"))
      } yield ()
    }


    "2 inputs" - core { implicit conn =>
      val inputMolecule = m(Ns.int.longs_(?).enums_(?).uris_(?))
      for {
        _ <- Ns.int.longs$.enums$.uris$ insert List(
          (1, Some(Set(long1, long2, long3)), Some(Set(enum1, enum2, enum3)), Some(Set(uri1, uri2, uri3))),
          (2, Some(Set(long2, long3, long4)), Some(Set(enum2, enum3, enum4)), Some(Set(uri2, uri3, uri4))),
          (3, Some(Set(long3, long4, long5)), Some(Set(enum3, enum4, enum5)), Some(Set(uri3, uri4, uri5))),

          (4, Some(Set(long1, long2, long3)), None, None),
          (5, None, Some(Set(enum1, enum2, enum3)), None),
          (6, None, None, Some(Set(uri1, uri2, uri3))),
          (7, None, None, None)
        )
        _ <- inputMolecule(
          List(Set(1L, 2L), Set(5L)), // 1, 3, 4
          List(Set("enum1")), // 1
          List(Set(uri2)) // 1, 2
        ).get.map(_ ==> List(1))

        _ <- inputMolecule(
          List(Set(1L, 2L), Set(5L)), // 1, 3, 4
          List(Set("enum4")), // 2, 3
          List(Set(uri3)) // 1, 2, 3, 6
        ).get.map(_ ==> List(3))

        _ <- m(Ns.int.ints(?).longs(?).strs(?)).apply(Nil, List(Set(1L)), List(Set("a"))).get
          .map(_ ==> "Unexpected success").recover { case Molecule_3_Exception(err) =>
          err ==> "Can only apply empty list (Nil) to a tacit input attribute. Please make input attr tacit: `ints` --> `ints_`"
        }

        _ <- m(Ns.int.ints_.<=(?).longs_(?).strs_(?)).apply(
          List(Set(1), Set(2)), List(Set(1L)), List(Set("a"))
        ).get.map(_ ==> "Unexpected success").recover { case Molecule_3_Exception(err) =>
          err ==> s"Can't apply multiple values to input attribute `:Ns/ints` having expression (<, >, <=, >=, !=)"
        }
      } yield ()
    }
  }
}
  