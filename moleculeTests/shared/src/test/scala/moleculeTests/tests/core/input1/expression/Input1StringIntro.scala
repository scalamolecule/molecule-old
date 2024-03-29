package moleculeTests.tests.core.input1.expression

import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Input1StringIntro extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.int.str insert List(
      (1, "a"),
      (2, "b"),
      (3, "c")
    )
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Ns.int.strs insert List(
      (1, Set("a", "b")),
      (2, Set("b", "c")),
      (3, Set("c", "d"))
    )
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._
    // Parameterized molecules have a `?` placeholder for an expected input value
    // and we call them "Input molecules".

    // Input molecules have the benefit that you can assign them to a variable
    // and then re-use them with various input values. This also allows Datomic
    // to cache and optimize the query and thereby improve runtime performance.

    "Introduction" - core { implicit conn =>
      // "Input-molecule" with a `?` placeholder for an expected input value
      // Often we are not interested in returning the input value so
      // we make the integer attribute tacit by adding an underscore
      val personOfAge        = m(Ns.str.int_(?))
      val personsYoungerThan = m(Ns.str.int_.<(?))

      for {
        _ <- Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28), ("Ann", 14))

        // Equality
        _ <- personOfAge(37).get.map(_ ==> List("John"))
        _ <- personOfAge(28).get.map(_ ==> List("Ben", "Lisa"))
        _ <- personOfAge(10).get.map(_ ==> Nil)

        // Apply expression value
        _ <- personsYoungerThan.apply(30).get.map(_ ==> List("Ann", "Ben", "Lisa"))
        // or just
        _ <- personsYoungerThan(30).get.map(_ ==> List("Ann", "Ben", "Lisa"))

        // We don't have to assign an input-molecule to a variable
        _ <- m(Ns.str.int_.<(?))(30).get.map(_ ==> List("Ann", "Ben", "Lisa"))
        // ...or saving a bit of typing with method `m` being implicit
        _ <- m(Ns.str.int_.<(?))(30).get.map(_ ==> List("Ann", "Ben", "Lisa"))

        // Although then it would be easier to just say
        _ <- Ns.str.int_.<(30).get.map(_ ==> List("Ann", "Ben", "Lisa"))

        // For brevity we test some more expressions in the short form
        _ <- m(Ns.str.int_.>(?))(30).get.map(_ ==> List("John"))
        _ <- m(Ns.str.int_.<=(?))(28).get.map(_ ==> List("Ann", "Ben", "Lisa"))
        _ <- m(Ns.str.int_.>=(?))(28).get.map(_ ==> List("John", "Ben", "Lisa"))
        _ <- m(Ns.str.int_.not(?))(30).get.map(_ ==> List("Ann", "John", "Ben", "Lisa"))
        _ <- m(Ns.str.int_.not(?))(28).get.map(_ ==> List("Ann", "John"))
      } yield ()
    }


    "Cardinality one" - {

      "Expressions" - core { implicit conn =>
        for {
          _ <- oneData
          _ <- m(Ns.int_.str(?)).apply("b").get.map(_ ==> List("b"))
          _ <- m(Ns.int_.str.<(?))("b").get.map(_ ==> List("a"))
          _ <- m(Ns.int_.str.>(?))("b").get.map(_ ==> List("c"))
          _ <- m(Ns.int_.str.<=(?))("b").get.map(_ ==> List("a", "b"))
          _ <- m(Ns.int_.str.>=(?))("b").get.map(_ ==> List("b", "c"))
          _ <- m(Ns.int_.str.not(?))("b").get.map(_ ==> List("a", "c"))
        } yield ()
      }

      "Tacit expressions" - core { implicit conn =>
        for {
          _ <- oneData
          _ <- m(Ns.int.str_(?))("b").get.map(_ ==> List(2))
          _ <- m(Ns.int.str_.<(?))("b").get.map(_ ==> List(1))
          _ <- m(Ns.int.str_.>(?))("b").get.map(_ ==> List(3))
          _ <- m(Ns.int.a1.str_.<=(?))("b").get.map(_ ==> List(1, 2))
          _ <- m(Ns.int.a1.str_.>=(?))("b").get.map(_ ==> List(2, 3))
          _ <- m(Ns.int.a1.str_.not(?))("b").get.map(_ ==> List(1, 3))
        } yield ()
      }


      // Optional attributes are not supported in input molecules.

      "OR-logic" - core { implicit conn =>
        for {
          _ <- oneData
          // `or`-separated values
          _ <- m(Ns.int.a1.str_(?)).apply("a" or "b").get.map(_ ==> List(1, 2))
          _ <- m(Ns.int.a1.str_(?)).apply("a" or "b" or "c").get.map(_ ==> List(1, 2, 3))

          // Comma-separated values
          _ <- m(Ns.int.a1.str_(?)).apply("a", "b").get.map(_ ==> List(1, 2))

          // Seq of values
          _ <- m(Ns.int.a1.str_(?)).apply(Seq("a", "b")).get.map(_ ==> List(1, 2))

          // Only distinct values are matched
          _ <- m(Ns.int.a1.str_(?)).apply(Seq("a", "b", "b")).get.map(_ ==> List(1, 2))

          // No input elements returns Nil
          _ <- m(Ns.int.a1.str_(?)).apply(Nil).get.map(_ ==> Nil)


          // Values assigned to variables
          _ <- m(Ns.int.a1.str_(?))(str1 or str2).get.map(_ ==> List(1, 2))
          _ <- m(Ns.int.a1.str_(?))(str1, str2).get.map(_ ==> List(1, 2))
          _ <- m(Ns.int.a1.str_(?))(Seq(str1, str2)).get.map(_ ==> List(1, 2))

          seq = Seq(str1, str2)
          _ <- m(Ns.int.a1.str_(?))(seq).get.map(_ ==> List(1, 2))

          // Order of input of no importance
          _ <- m(Ns.int.a1.str_(?))("b", "a").get.map(_ ==> List(1, 2))
        } yield ()
      }


      // Only for String attributes having fulltext option defined in schema.
      "Fulltext search" - core { implicit conn =>
        // fulltext only implemented in Datomic Peer
        if (system == SystemPeer) {
          // https://groups.google.com/forum/#!searchin/datomic/fulltext$20rule|sort:date/datomic/tOm__ftT27c/uTfU_ZsnJiIJ
          val inputMolecule = m(Ns.str.contains(?))
          for {
            _ <- Ns.str insert List("The quick fox jumps", "Ten slow monkeys")

            _ <- inputMolecule(Nil).get.map(_ ==> Nil)
            _ <- inputMolecule("jumps").get.map(_ ==> List("The quick fox jumps"))
            _ <- inputMolecule("jumps", "fox").get.map(_ ==> List("The quick fox jumps"))
            _ <- inputMolecule("jumps" or "fox").get.map(_ ==> List("The quick fox jumps"))
            _ <- inputMolecule(Seq("jumps", "fox")).get.map(_ ==> List("The quick fox jumps"))

            // Obs: only whole words are matched
            _ <- inputMolecule("jump").get.map(_ ==> Nil)
          } yield ()
        }
      }
    }


    "Cardinality many" - {

      // See expression.equality.Apply<type> test for equality/OR/AND examples

      "Comparison" - core { implicit conn =>
        for {
          _ <- manyData
          // If we want the full sets containing the matching value we can't use an
          // input-molecule anymore but will have to map a full result set
          _ <- Ns.int.a1.strs.get.map(_.filter(_._2.contains("b")) ==> List((1, Set("a", "b")), (2, Set("b", "c"))))

          // Input-molecules with tacit expression
          _ <- m(Ns.int.a1.strs_(?))(Set("a")).get.map(_ ==> List(1))
          _ <- m(Ns.int.a1.strs_(?))(Set("b")).get.map(_ ==> List(1, 2))
          _ <- m(Ns.int.a1.strs_(?))(Set("c")).get.map(_ ==> List(2, 3))
          _ <- m(Ns.int.a1.strs_(?))(Set("d")).get.map(_ ==> List(3))

          _ <- m(Ns.int.a1.strs_.<(?))(Set("b")).get.map(_ ==> List(1))
          _ <- m(Ns.int.a1.strs_.>(?))(Set("b")).get.map(_ ==> List(2, 3))
          _ <- m(Ns.int.a1.strs_.<=(?))(Set("b")).get.map(_ ==> List(1, 2))
          _ <- m(Ns.int.a1.strs_.>=(?))(Set("b")).get.map(_ ==> List(1, 2, 3))
          _ <- m(Ns.int.a1.strs_.not(?))(Set("b")).get.map(_ ==> List(3))
        } yield ()
      }

      // Only for String attributes having fulltext option defined in schema.
      "Fulltext search" - core { implicit conn =>
        // fulltext only implemented in Datomic Peer
        if (system == SystemPeer) {
          val inputMolecule = m(Ns.int.a1.strs.contains(?))
          for {
            _ <- Ns.int.strs insert List(
              (1, Set("The quick fox jumps", "Ten slow monkeys")),
              (2, Set("lorem ipsum", "Going slow"))
            )

            _ <- inputMolecule(Nil).get.map(_ ==> Nil)

            _ <- inputMolecule(Set("slow")).get.map(_ ==> List(
              (1, Set("The quick fox jumps", "Ten slow monkeys")),
              (2, Set("lorem ipsum", "Going slow"))
            ))

            _ <- inputMolecule(Set("ipsum")).get.map(_ ==> List(
              (2, Set("lorem ipsum", "Going slow"))
            ))

            // Only 1 entity has a `strs` attribute with both values
            _ <- inputMolecule(Set("fox", "slow")).get.map(_ ==> List(
              (1, Set("The quick fox jumps", "Ten slow monkeys"))
            ))

            _ <- inputMolecule(Set("fox"), Set("slow")).get.map(_ ==> List(
              (1, Set("The quick fox jumps", "Ten slow monkeys")),
              (2, Set("lorem ipsum", "Going slow"))
            ))

            _ <- inputMolecule(Set("fox") or Set("slow")).get.map(_ ==> List(
              (1, Set("The quick fox jumps", "Ten slow monkeys")),
              (2, Set("lorem ipsum", "Going slow"))
            ))

            _ <- inputMolecule(List(Set("fox"), Set("slow"))).get.map(_ ==> List(
              (1, Set("The quick fox jumps", "Ten slow monkeys")),
              (2, Set("lorem ipsum", "Going slow"))
            ))
          } yield ()
        }
      }
    }
  }
}