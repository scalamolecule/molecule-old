package moleculeTests.tests.core.input2

import molecule.core.api.exception.Molecule_2_Exception
import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in2_out3._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input2syntax extends AsyncTestSuite {

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.int$.long$ insert List(
      ("Ann", Some(37), Some(5L)),
      ("Ben", Some(28), Some(5L)),
      ("Joe", Some(28), Some(4L)),
      ("Liz", Some(28), Some(3L)),
      ("Stu", Some(28), None),
      ("Tim", None, Some(3L)),
      ("Uma", None, None)
    )
  }

  def oneManyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.str.long$.ints$ insert List(
      ("a", Some(1L), Some(Set(1, 2))),
      ("b", Some(1L), Some(Set(2, 3))),
      ("c", Some(1L), Some(Set(3, 4))),
      ("d", Some(1L), Some(Set(3, 4, 5))),
      ("e", Some(1L), None),
      ("f", Some(2L), Some(Set(1, 2))),
      ("g", Some(2L), Some(Set(2, 3))),
      ("h", Some(2L), Some(Set(3, 4))),
      ("i", Some(2L), Some(Set(3, 4, 5))),
      ("j", Some(2L), None),
      ("k", None, Some(Set(6, 7))),
      ("l", None, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Card one + one" - {

      val personOfAgeAndStatus = m(Ns.str.int_(?).long_(?))
      "Pairs" - core { implicit conn =>
        for {
          _ <- manyData
          // Match specific pairs of input

          // 0 pairs (both input attributes non-asserted)
          _ <- personOfAgeAndStatus(Nil).get.map(_ ==> List("Uma"))

          // 1 Pair
          _ <- personOfAgeAndStatus(37, 5L).get.map(_ ==> List("Ann"))
          _ <- personOfAgeAndStatus((37, 5L)).get.map(_ ==> List("Ann"))
          _ <- personOfAgeAndStatus(Seq((37, 5L))).get.map(_ ==> List("Ann"))

          // 2 pairs
          _ <- personOfAgeAndStatus((37, 5L), (28, 5L)).get.map(_.sorted ==> List("Ann", "Ben"))
          _ <- personOfAgeAndStatus(Seq((37, 5L), (28, 5L))).get.map(_.sorted ==> List("Ann", "Ben"))
          _ <- personOfAgeAndStatus((37 and 5L) or (28 and 5L)).get.map(_.sorted ==> List("Ann", "Ben"))

          // 3 pairs
          _ <- personOfAgeAndStatus((37, 5L), (28, 5L), (28, 4L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
          _ <- personOfAgeAndStatus(Seq((37, 5L), (28, 5L), (28, 4L))).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
          _ <- personOfAgeAndStatus((37 and 5L) or (28 and 5L) or (28 and 4L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))

          // etc..
        } yield ()
      }

      "2 groups - one way to see it" - core { implicit conn =>
        for {
          _ <- manyData

          // Explicit `and` semantics between 2 logical groups, respectively matching each input attribute independently
          // Each group matches value1 `or` value2 `or` etc..

          _ <- personOfAgeAndStatus(28 and 5L).get.map(_ ==> List("Ben"))
          _ <- personOfAgeAndStatus(28 and (5L or 4L)).get.map(_ ==> List("Ben", "Joe"))
          _ <- personOfAgeAndStatus(28 and (5L or 4L or 3L)).get.map(_ ==> List("Ben", "Liz", "Joe"))

          _ <- personOfAgeAndStatus(37 and 5L).get.map(_ ==> List("Ann"))
          _ <- personOfAgeAndStatus((37 or 28) and 5L).get.map(_.sorted ==> List("Ann", "Ben"))
          _ <- personOfAgeAndStatus((37 or 28) and 4L).get.map(_.sorted ==> List("Joe"))
          _ <- personOfAgeAndStatus((37 or 28) and 3L).get.map(_.sorted ==> List("Liz"))

          _ <- personOfAgeAndStatus((37 or 28) and (5L or 4L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
          _ <- personOfAgeAndStatus((37 or 28) and (5L or 4L or 3L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe", "Liz"))


          // 2 lists of values, respectively matching each input attribute

          _ <- personOfAgeAndStatus(Seq(28), Seq(5L)).get.map(_ ==> List("Ben"))
          _ <- personOfAgeAndStatus(Seq(28), Seq(5L, 4L)).get.map(_ ==> List("Ben", "Joe"))
          _ <- personOfAgeAndStatus(Seq(28), Seq(5L, 4L, 3L)).get.map(_ ==> List("Ben", "Liz", "Joe"))

          _ <- personOfAgeAndStatus(Seq(37), Seq(5L)).get.map(_ ==> List("Ann"))
          _ <- personOfAgeAndStatus(Seq(37, 28), Seq(5L)).get.map(_.sorted ==> List("Ann", "Ben"))
          _ <- personOfAgeAndStatus(Seq(37, 28), Seq(4L)).get.map(_.sorted ==> List("Joe"))
          _ <- personOfAgeAndStatus(Seq(37, 28), Seq(3L)).get.map(_.sorted ==> List("Liz"))

          _ <- personOfAgeAndStatus(Seq(37, 28), Seq(5L, 4L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
          _ <- personOfAgeAndStatus(Seq(37, 28), Seq(5L, 4L, 3L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe", "Liz"))


          // No input returns Nil
          _ <- personOfAgeAndStatus(Seq(28), Nil).get.map(_ ==> List("Stu"))
          _ <- personOfAgeAndStatus(Nil, Seq(3L)).get.map(_ ==> List("Tim"))
          _ <- personOfAgeAndStatus(Nil, Nil).get.map(_ ==> List("Uma"))
        } yield ()
      }

      "2 groups - another way to see it" - core { implicit conn =>
        for {
          _ <- manyData

          // Match any combination of first and second values

          // 1 + 1
          // --------
          // 37-5 Ann
          _ <- personOfAgeAndStatus(Seq(37), Seq(5L)).get.map(_ ==> List("Ann"))
          _ <- personOfAgeAndStatus(37 and 5L).get.map(_ ==> List("Ann"))

          // 2 + 1
          // --------
          // 37-5 Ann
          // 28-5 Ben
          _ <- personOfAgeAndStatus(Seq(37, 28), Seq(5L)).get.map(_.sorted ==> List("Ann", "Ben"))
          _ <- personOfAgeAndStatus((37 or 28) and 5L).get.map(_.sorted ==> List("Ann", "Ben"))

          // 1 + 2
          // --------
          // 28-5 Ben
          // 28-4 Joe
          _ <- personOfAgeAndStatus(Seq(28), Seq(5L, 4L)).get.map(_.sorted ==> List("Ben", "Joe"))
          _ <- personOfAgeAndStatus(28 and (5L or 4L)).get.map(_ ==> List("Ben", "Joe"))

          // 2 + 2
          // --------
          // 37-5 Ann
          // 37-4
          // 28-5 Ben
          // 28-4 Joe
          _ <- personOfAgeAndStatus(Seq(37, 28), Seq(5L, 4L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
          _ <- personOfAgeAndStatus((37 or 28) and (5L or 4L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))

          // 2 + 3
          // --------
          // 37-5 Ann
          // 37-4
          // 37-3
          // 28-5 Ben
          // 28-4 Joe
          // 28-3 Liz
          _ <- personOfAgeAndStatus(Seq(37, 28), Seq(5L, 4L, 3L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe", "Liz"))
          _ <- personOfAgeAndStatus((37 or 28) and (5L or 4L or 3L)).get.map(_.sorted ==> List("Ann", "Ben", "Joe", "Liz"))


          // No input returns Nil
          _ <- personOfAgeAndStatus(Seq(28), Nil).get.map(_ ==> List("Stu"))
          _ <- personOfAgeAndStatus(Nil, Seq(3L)).get.map(_ ==> List("Tim"))
          _ <- personOfAgeAndStatus(Nil, Nil).get.map(_ ==> List("Uma"))
        } yield ()
      }

      "expressions" - core { implicit conn =>
        val inputExpression = m(Ns.str.int_.>(?).long_.<=(?))
        for {
          _ <- Ns.str.int.long insert List(
            ("a", 1, 1L),
            ("b", 1, 1L),
            ("c", 2, 1L),
            ("d", 3, 2L),
            ("e", 4, 3L)
          )

          // 1 pair

          _ <- inputExpression(1, 2L).get.map(_ ==> List("c", "d"))
          _ <- inputExpression(2, 3L).get.map(_ ==> List("d", "e"))

          _ <- inputExpression((1, 2L)).get.map(_ ==> List("c", "d"))
          _ <- inputExpression((2, 3L)).get.map(_ ==> List("d", "e"))

          _ <- inputExpression(Seq((1, 2L))).get.map(_ ==> List("c", "d"))
          _ <- inputExpression(Seq((2, 3L))).get.map(_ ==> List("d", "e"))

          // Applying no pairs returns empty result
          _ <- inputExpression(Nil).get.map(_ ==> Nil)

          // Multiple pairs
          // Compare functions expects only one argument, so multiple input pairs are not allowed
          _ <- inputExpression((1, 2L), (1, 3L)).get.recover { case Molecule_2_Exception(err) =>
            err ==> "Can't apply multiple pairs to input attributes with one or more expressions (<, >, <=, >=, !=)"
          }
        } yield ()
      }
    }


    "Card one + many" - {

      val inputMolecule = m(Ns.str.long_(?).ints_(?))

      "Pairs" - core { implicit conn =>
        for {
          _ <- oneManyData

          // 0 pairs ...................................................

          // Both input attributes non-asserted)
          _ <- inputMolecule(Nil).get.map(_ ==> List("l"))


          // 1 pair ...................................................

          _ <- inputMolecule(1L, Set(1)).get.map(_ ==> List("a"))
          _ <- inputMolecule(1L, Set(2)).get.map(_ ==> List("a", "b"))
          _ <- inputMolecule(1L, Set(3)).get.map(_ ==> List("b", "c", "d"))
          _ <- inputMolecule(1L, Set(4)).get.map(_ ==> List("c", "d"))
          _ <- inputMolecule(1L, Set(5)).get.map(_ ==> List("d"))

          // Empty Set matches non-asserted card-many attribute
          _ <- inputMolecule(1L, Set[Int]()).get.map(_ ==> List("e"))

          _ <- inputMolecule(1L, Set(1, 2)).get.map(_ ==> List("a"))
          _ <- inputMolecule(1L, Set(1, 3)).get.map(_ ==> Nil)
          _ <- inputMolecule(1L, Set(2, 3)).get.map(_ ==> List("b"))
          _ <- inputMolecule(1L, Set(3, 4)).get.map(_ ==> List("c", "d"))
          _ <- inputMolecule(1L, Set(3, 4, 5)).get.map(_ ==> List("d"))


          // Multiple pairs ...................................................

          // Duplicate pairs coalesce
          _ <- inputMolecule((1L, Set(1, 2)), (1L, Set(1, 2))).get.map(_ ==> List("a"))

          _ <- inputMolecule((1L, Set(2)), (1L, Set(2, 3))).get.map(_ ==> List("a", "b"))
          _ <- inputMolecule((1L, Set(2)), (1L, Set(3, 4))).get.map(_ ==> List("a", "b", "c", "d"))
          _ <- inputMolecule((1L, Set(2)), (1L, Set(3, 4, 5))).get.map(_ ==> List("a", "b", "d"))

          _ <- inputMolecule((1L, Set(1, 2)), (1L, Set(3, 4, 5))).get.map(_ ==> List("a", "d"))
          _ <- inputMolecule((1L, Set(1, 2)), (1L, Set(3, 4))).get.map(_ ==> List("a", "c", "d"))
          _ <- inputMolecule((1L, Set(1, 2)), (1L, Set(2, 3))).get.map(_ ==> List("a", "b"))
          _ <- inputMolecule((1L, Set(1, 2)), (1L, Set[Int]())).get.map(_ ==> List("a", "e"))
          _ <- inputMolecule((1L, Set(1, 3)), (1L, Set[Int]())).get.map(_ ==> List("e"))
          _ <- inputMolecule((1L, Set(1, 3)), (2L, Set[Int]())).get.map(_ ==> List("j"))
          _ <- inputMolecule((1L, Set(1, 3)), (3L, Set[Int]())).get.map(_ ==> Nil)


          // Explicit `or` semantics between pairs (each pair matches value1 `and` value2)
          _ <- inputMolecule((1L and Set(2)) or (1L and Set(3, 4, 5))).get.map(_ ==> List("a", "b", "d"))

          // Seq of pairs
          _ <- inputMolecule(Seq((1L, Set(2)), (1L, Set(3, 4, 5)))).get.map(_ ==> List("a", "b", "d"))
        } yield ()
      }


      "Groups" - core { implicit conn =>
        for {
          _ <- oneManyData

          // Explicit `and` semantics between 2 logical groups, respectively matching each input attribute
          // Each group matches value1 `or` value2 `or` etc..

          _ <- inputMolecule(1L and Set(1)).get.map(_ ==> List("a"))

          _ <- inputMolecule(1L and Set(1, 2)).get.map(_ ==> List("a"))
          _ <- inputMolecule(1L and (Set(1) or Set(2))).get.map(_ ==> List("a", "b"))

          _ <- inputMolecule(1L and Set(1, 2, 3)).get.map(_ ==> Nil)
          _ <- inputMolecule(1L and (Set(1, 2) or Set(3))).get.map(_ ==> List("a", "b", "c", "d"))
          _ <- inputMolecule(1L and (Set(1) or Set(2, 3))).get.map(_ ==> List("a", "b"))
          _ <- inputMolecule(1L and (Set(1) or Set(2) or Set(3))).get.map(_ ==> List("a", "b", "c", "d"))


          _ <- inputMolecule((1L or 2L) and Set(1)).get.map(_ ==> List("a", "f"))

          _ <- inputMolecule((1L or 2L) and Set(1, 2)).get.map(_ ==> List("a", "f"))
          _ <- inputMolecule((1L or 2L) and (Set(1) or Set(2))).get.map(_ ==> List("a", "b", "f", "g"))

          _ <- inputMolecule((1L or 2L) and Set(1, 2, 3)).get.map(_ ==> Nil)
          _ <- inputMolecule((1L or 2L) and (Set(1, 2) or Set(3))).get.map(_ ==> List("a", "b", "c", "d", "f", "g", "h", "i"))
          _ <- inputMolecule((1L or 2L) and (Set(1) or Set(2, 3))).get.map(_ ==> List("a", "b", "f", "g"))
          _ <- inputMolecule((1L or 2L) and (Set(1) or Set(2) or Set(3))).get.map(_ ==> List("a", "b", "c", "d", "f", "g", "h", "i"))


          // 2 lists of values, respectively matching each input attribute

          _ <- inputMolecule(Seq(1L), Seq(Set(1))).get.map(_ ==> List("a"))
          _ <- inputMolecule(Seq(1L), Seq(Set(1, 2))).get.map(_ ==> List("a"))

          // Nil matches non-asserted attributes
          _ <- inputMolecule(Seq(1L), Nil).get.map(_ ==> List("e"))
          _ <- inputMolecule(Nil, Seq(Set(6, 7))).get.map(_ ==> List("k"))
          _ <- inputMolecule(Nil, Nil).get.map(_ ==> List("l"))
        } yield ()
      }


      "expressions" - core { implicit conn =>
        val inputExpression  = m(Ns.str.long_.>=(?).ints_.<(?))
        val inputExpression2 = m(Ns.str.long_.>=(?).ints_(?))
        for {
          _ <- oneManyData

          // 1 pair of input values allowed

          _ <- inputExpression(2L, Set(1)).get.map(_ ==> Nil)
          _ <- inputExpression(2L, Set(2)).get.map(_ ==> List("f"))


          // Comparison functions set limit on inputs

          _ <- inputExpression(2L, Set(1, 2)).get.recover { case MoleculeException(err, _) =>
            err ==> "Can't apply multiple values to comparison function."
          }

          _ <- inputExpression((1L, Set(1)), (2L, Set(2))).get.recover { case Molecule_2_Exception(err) =>
            err ==> "Can't apply multiple pairs to input attributes with one or more expressions (<, >, <=, >=, !=)"
          }

          // Mixing comparison/equality

          _ <- inputExpression2(1L, Set(1, 2)).get.map(_ ==> List("a", "f"))
        } yield ()
      }
    }
  }
}