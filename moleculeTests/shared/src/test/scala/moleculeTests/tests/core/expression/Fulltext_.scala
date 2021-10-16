package moleculeTests.tests.core.expression

import molecule.datomic.api.in1_out2._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object Fulltext_ extends AsyncTestSuite {

  // Fulltext search indexing only available for Peer systems.

  lazy val tests = Tests {

    "Card one" - corePeerOnly { implicit conn =>
      for {
        _ <- Ns.str insert List("The quick fox jumps", "Ten slow monkeys")

        // Trivial words like "The" not indexed
        _ <- Ns.str.contains("The").get.map(_ ==> List())
        _ <- Ns.str.contains("Ten").get.map(_ ==> List("Ten slow monkeys"))

        // Input
        _ <- m(Ns.str.contains(?))("Ten").get.map(_ ==> List("Ten slow monkeys"))

        /*
    Non-indexed words:

    "a", "an", "and", "are", "as", "at", "be", "but", "by",
    "for", "if", "in", "into", "is", "it",
    "no", "not", "of", "on", "or", "such",
    "that", "the", "their", "then", "there", "these",
    "they", "this", "to", "was", "will", "with"

    See https://docs.datomic.com/on-prem/schema.html
    */

        // Only full words counted
        _ <- Ns.str.contains("jumps").get.map(_ ==> List("The quick fox jumps"))
        _ <- Ns.str.contains("jump").get.map(_ ==> List())

        // Searches are case-insensitive
        _ <- Ns.str.contains("Jumps").get.map(_ ==> List("The quick fox jumps"))
        _ <- Ns.str.contains("JuMpS").get.map(_ ==> List("The quick fox jumps"))

        // Empty spaces ignored
        _ <- Ns.str.contains("slow ").get.map(_ ==> List("Ten slow monkeys"))
        _ <- Ns.str.contains(" slow").get.map(_ ==> List("Ten slow monkeys"))
        _ <- Ns.str.contains(" slow ").get.map(_ ==> List("Ten slow monkeys"))
        _ <- Ns.str.contains("  slow  ").get.map(_ ==> List("Ten slow monkeys"))

        // Words are searched individually - order and spaces ignored
        _ <- Ns.str.contains("slow     monkeys").get.map(_ ==> List("Ten slow monkeys"))
        _ <- Ns.str.contains("monkeys slow").get.map(_ ==> List("Ten slow monkeys"))
        _ <- Ns.str.contains("monkeys quick").get.map(_ ==> List("Ten slow monkeys", "The quick fox jumps"))
        _ <- Ns.str.contains("quick monkeys").get.map(_ ==> List("Ten slow monkeys", "The quick fox jumps"))
      } yield ()
    }

    "Quoting" - corePeerOnly { implicit conn =>
      for {
        _ <- Ns.int(1).str("""Hi "Ann"""").save

        _ <- Ns.str.contains("Hi").get.map(_ ==> List("""Hi "Ann""""))
        _ <- Ns.str.contains(""""Ann"""").get.map(_ ==> List("""Hi "Ann""""))

        needle1 = "Hi"
        needle2 = """"Ann""""
        _ <- Ns.str.contains(needle1).get.map(_ ==> List("""Hi "Ann""""))
        _ <- Ns.str.contains(needle2).get.map(_ ==> List("""Hi "Ann""""))

        _ <- Ns.int.str_.contains("""Hi "Ann"""").get.map(_ ==> List(1))
        _ <- Ns.int.str_.contains(needle1).get.map(_ ==> List(1))
        _ <- Ns.int.str_.contains(needle2).get.map(_ ==> List(1))
      } yield ()
    }

    "Card many" - corePeerOnly { implicit conn =>
      for {
        _ <- Ns.int.strs insert List(
          (1, Set("The quick fox jumps", "Ten slow monkeys")),
          (2, Set("lorem ipsum", "Going slow"))
        )

        _ <- Ns.int.strs.contains("fox").get.map(_ ==> List(
          (1, Set("The quick fox jumps", "Ten slow monkeys"))
        ))
        _ <- Ns.int.strs.contains("slow").get.map(_ ==> List(
          (1, Set("The quick fox jumps", "Ten slow monkeys")),
          (2, Set("lorem ipsum", "Going slow"))
        ))
        _ <- Ns.int.strs.contains("fox", "slow").get.map(_ ==> List(
          (1, Set("The quick fox jumps", "Ten slow monkeys"))
        ))

        _ <- Ns.strs.contains("fox").get.map(_ ==> List(Set("The quick fox jumps", "Ten slow monkeys")))
        _ <- Ns.strs.contains("slow").get.map(_ ==> List(Set("The quick fox jumps", "Ten slow monkeys", "lorem ipsum", "Going slow")))
        _ <- Ns.strs.contains("fox", "slow").get.map(_ ==> List(Set("The quick fox jumps", "Ten slow monkeys")))

        _ <- Ns.int.strs_.contains("fox").get.map(_ ==> List(1))
        _ <- Ns.int.strs_.contains("slow").get.map(_ ==> List(1, 2))
        _ <- Ns.int.strs_.contains("fox", "slow").get.map(_ ==> List(1))
      } yield ()
    }
  }
}