package moleculeTests.tests.core.attrMap

import molecule.datomic.api.out2._
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object Pairs extends Base {

  // (Variables for testing resolving identifiers)
  val hiThere = "Hi there"
  val pair1   = "en" -> "Hi there"
  val pair2   = "en" -> hiThere
  val pair3   = en -> "Hi there"
  val pair4   = en -> hiThere
  val pair5   = ("en", "Hi there")
  val pair6   = ("en", hiThere)
  val pair7   = (en, "Hi there")
  val pair8   = (en, hiThere)
  val pair9   = (fr, "Bonjour")

  lazy val tests = Tests {

    // Attribute maps offer to work with pairs of key/values in various ways.

    "Key -> value" - core { implicit conn =>
      val en_hi_there = List((1, Map("en" -> "Hi there")))

      for {
        _ <- testData

        // Find a specific key/value pair
        _ <- Ns.int.strMap("en" -> "Hi there").get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(("en", "Hi there")).get.map(_ ==> en_hi_there)


        // Variables sanity checks

        _ <- Ns.int.strMap("en" -> hiThere).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(en -> "Hi there").get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(en -> hiThere).get.map(_ ==> en_hi_there)

        // Alternative tuple notation
        _ <- Ns.int.strMap(("en", hiThere)).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap((en, "Hi there")).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap((en, hiThere)).get.map(_ ==> en_hi_there)

        _ <- Ns.int.strMap(pair1).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(pair2).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(pair3).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(pair4).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(pair5).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(pair6).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(pair7).get.map(_ ==> en_hi_there)
        _ <- Ns.int.strMap(pair8).get.map(_ ==> en_hi_there)


        _ <- Ns.int.intMap("en" -> 10).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10))
        ))
      } yield ()
    }


    "Key -> value with regex" - core { implicit conn =>
      for {
        _ <- testData

        // We can widen the values search with a regex
        _ <- Ns.int.strMap("en" -> ".*Hi.*").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("en" -> "Oh, Hi"))
        ))
        // Even for the key
        _ <- Ns.int.strMap("en|da" -> ".*Hi.*").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser"))
        ))
      } yield ()
    }


    "Multiple key/value pairs" - core { implicit conn =>
      val en_hi_fr_bonjour = List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))

      val pairs1 = Seq("en" -> hiThere, fr -> "Bonjour")
      val pairs2 = Seq("en" -> hiThere, pair9)
      val pairs3 = Seq(pair2, fr -> "Bonjour")
      val pairs4 = Seq(pair2, pair9)

      for {
        _ <- testData

        // Multiple pairs
        _ <- Ns.int.strMap("en" -> "Hi there" or "fr" -> "Bonjour").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("fr" -> "Bonjour"))
        ))
        // Same as
        _ <- Ns.int.strMap("en" -> "Hi there", "fr" -> "Bonjour").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("fr" -> "Bonjour"))
        ))

        // To have full control of matching keys and values we
        // can use multiple pairs

        // OR semantics

        // Comma-separated pairs have OR-semantics (can't use 'or'-notation for pairs)
        _ <- Ns.int.strMap("en" -> "Hi there", "da" -> "Hej").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (3, Map("da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        _ <- Ns.int.intMap("en" -> 10, "fr" -> 20).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("fr" -> 20, "en" -> 10))
        ))

        // Variables sanity checks
        _ <- Ns.int.strMap("en" -> hiThere, fr -> "Bonjour").get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap("en" -> hiThere, pair9).get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap(pair2, fr -> "Bonjour").get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap(pair2, pair9).get.map(_ ==> en_hi_fr_bonjour)

        _ <- Ns.int.strMap(Seq("en" -> hiThere, fr -> "Bonjour")).get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap(Seq("en" -> hiThere, pair9)).get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap(Seq(pair2, fr -> "Bonjour")).get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap(Seq(pair2, pair9)).get.map(_ ==> en_hi_fr_bonjour)

        _ <- Ns.int.strMap(pairs1).get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap(pairs2).get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap(pairs3).get.map(_ ==> en_hi_fr_bonjour)
        _ <- Ns.int.strMap(pairs4).get.map(_ ==> en_hi_fr_bonjour)
      } yield ()
    }
  }
}