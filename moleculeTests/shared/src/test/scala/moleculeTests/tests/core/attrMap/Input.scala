package moleculeTests.tests.core.attrMap

import molecule.datomic.api.in1_out2._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object Input extends Base {

  lazy val tests = Tests {

    "Equality" - core { implicit conn =>
      for {
        _ <- testData

        // Input molecule waiting for a key/value pair
        inputMolecule = m(Ns.int.strMap(?))

        // We then apply a Map with a key/value pair
        _ <- inputMolecule.apply(Map("en" -> "Hello")).get.map(_ ==> List(
          (3, Map("en" -> "Hello"))
        ))

        // We can use a tacit attribute map (to only return the Int value):
        _ <- m(Ns.int.strMap_(?)).apply(Map("en" -> "Hello")).get.map(_ ==> List(3))

        // Note that text searches for attribute maps are case-sensitive
        _ <- m(Ns.int.strMap_(?))(Map("en" -> "hello")).get.map(_ ==> List())


        _ <- m(Ns.int.intMap(?))(Map("en" -> 10)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10))
        ))
        _ <- m(Ns.int.intMap_(?))(Map("en" -> 10)).get.map(_ ==> List(1, 2))
      } yield ()
    }


    "Regex/partial search" - core { implicit conn =>
      for {
        _ <- testData

        // Attribute maps allow us to search for a value needle without the key:
        _ <- Ns.int.strMap(".*He.*").get.map(_ ==> List(
          (1, Map("da" -> "Hejsa")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        // Attribute maps in input molecules though require Maps as needles.
        // So, to search for a regex value we could make the key part accepting anything:
        _ <- m(Ns.int.strMap(?))(Map(".*" -> ".*He.*")).get.map(_ ==> List(
          (1, Map("da" -> "Hejsa")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))
        _ <- m(Ns.int.strMap_(?))(Map(".*" -> ".*He.*")).get.map(_ ==> List(1, 3, 4))

        // Retrieving a specific value for any key
        _ <- m(Ns.int.strMap(?))(Map(".*" -> "Hello")).get.map(_ ==> List(
          (3, Map("en" -> "Hello"))
        ))

        // Since other types than String cannot use regexes we can only
        // search for values equal to our needle:
        _ <- m(Ns.int.intMap(?))(Map(".*" -> 10)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10))
        ))
        _ <- m(Ns.int.intMap_(?))(Map(".*" -> 10)).get.map(_ ==> List(1, 2))
      } yield ()
    }


    "Negation" - core { implicit conn =>
      for {
        _ <- testData

        _ <- m(Ns.int(2).strMap.!=(?))(Map(".*" -> "Bon giorno")).get.map(_ ==> List(
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour"))
        ))

        // Negate multiple values
        // OBS: each key/value pair is tried and generates a set of subresults where
        // that pair is not present. But now that we have two pairs, and each subresult
        // contains the other value, they will both be present in the final coalesced set!
        // So we were not expecting both values to be returned:
        _ <- m(Ns.int(2).strMap.!=(?))(Map("fr" -> "Bonjour", "it" -> "Bon giorno")).get.map(_ ==> List(
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno"))
        ))
        // Although not negating pair-wise, in most cases this would probably work instead:
        _ <- m(Ns.int(2).strMap.!=(?))(Map("fr|it" -> "Bonjour|Bon giorno")).get.map(_ ==> List(
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser"))
        ))
        _ <- m(Ns.int(2).strMap_.!=(?))(Map("fr|it" -> "Bonjour|Bon giorno")).get.map(_ ==> List(2))


        _ <- m(Ns.int.intMap.!=(?))(Map(".*" -> 30)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20))
        ))
        _ <- m(Ns.int.intMap_.!=(?))(Map(".*" -> 30)).get.map(_ ==> List(1, 2))

        // We can't negate multiple non-string values on input molecules
        // since we can't use regexes on other types than String.
      } yield ()
    }


    "Comparison" - core { implicit conn =>
      for {
        _ <- testData

        _ <- m(Ns.int.strMap.>(?))(Map(".*" -> "Hej")).get.map(_ ==> List(
          (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
          (3, Map("en" -> "Hello"))
        ))

        _ <- m(Ns.int.strMap.>=(?))(Map(".*" -> "Hej")).get.map(_ ==> List(
          (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        _ <- m(Ns.int.strMap.<=(?))(Map(".*" -> "Hej")).get.map(_ ==> List(
          (2, Map("fr" -> "Bonjour", "it" -> "Bon giorno")),
          (3, Map("da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        _ <- m(Ns.int.strMap.<(?))(Map(".*" -> "Hej")).get.map(_ ==> List(
          (2, Map("fr" -> "Bonjour", "it" -> "Bon giorno"))
        ))

        _ <- m(Ns.int.strMap_.>(?))(Map(".*" -> "Hej")).get.map(_.sorted ==> List(1, 2, 3))
        _ <- m(Ns.int.strMap_.>=(?))(Map(".*" -> "Hej")).get.map(_.sorted ==> List(1, 2, 3, 4))
        _ <- m(Ns.int.strMap_.<=(?))(Map(".*" -> "Hej")).get.map(_.sorted ==> List(2, 3, 4))
        _ <- m(Ns.int.strMap_.<(?))(Map(".*" -> "Hej")).get.map(_.sorted ==> List(2))


        // Int

        _ <- m(Ns.int.intMap.>(?))(Map(".*" -> 20)).get.map(_ ==> List(
          (1, Map("da" -> 30)),
          (2, Map("it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))
        _ <- m(Ns.int.intMap.>=(?))(Map(".*" -> 20)).get.map(_ ==> List(
          (1, Map("da" -> 30)),
          (2, Map("fr" -> 20, "it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))
        _ <- m(Ns.int.intMap.<=(?))(Map(".*" -> 20)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20))
        ))
        _ <- m(Ns.int.intMap.<(?))(Map(".*" -> 20)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10))
        ))

        _ <- m(Ns.int.intMap.>(?))(Map(".*" -> -10)).get.map(_ ==> List(
          (1, Map("en" -> 10, "da" -> 30)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20, "it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))

        _ <- m(Ns.int.intMap_.>(?))(Map(".*" -> 20)).get.map(_ ==> List(1, 2, 3, 4))
        _ <- m(Ns.int.intMap_.>=(?))(Map(".*" -> 20)).get.map(_ ==> List(1, 2, 3, 4))
        _ <- m(Ns.int.intMap_.<=(?))(Map(".*" -> 20)).get.map(_ ==> List(1, 2))
        _ <- m(Ns.int.intMap_.<(?))(Map(".*" -> 20)).get.map(_ ==> List(1, 2))


        // Date
        _ <- m(Ns.int.dateMap.>(?))(Map(".*" -> date2)).get.map(_ ==> List(
          (1, Map("da" -> date3)),
          (2, Map("it" -> date3)),
          (3, Map("en" -> date3, "da" -> date3)),
          (4, Map("da" -> date3))
        ))
        _ <- m(Ns.int.dateMap.>=(?))(Map(".*" -> date2)).get.map(_ ==> List(
          (1, Map("da" -> date3)),
          (2, Map("fr" -> date2, "it" -> date3)),
          (3, Map("en" -> date3, "da" -> date3)),
          (4, Map("da" -> date3))
        ))
        _ <- m(Ns.int.dateMap.<=(?))(Map(".*" -> date2)).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1, "fr" -> date2))
        ))
        _ <- m(Ns.int.dateMap.<(?))(Map(".*" -> date2)).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1))
        ))

        _ <- m(Ns.int.dateMap_.>(?))(Map(".*" -> date2)).get.map(_ ==> List(1, 2, 3, 4))
        _ <- m(Ns.int.dateMap_.>=(?))(Map(".*" -> date2)).get.map(_ ==> List(1, 2, 3, 4))
        _ <- m(Ns.int.dateMap_.<=(?))(Map(".*" -> date2)).get.map(_ ==> List(1, 2))
        _ <- m(Ns.int.dateMap_.<(?))(Map(".*" -> date2)).get.map(_ ==> List(1, 2))
      } yield ()
    }


    "Different keys, multiple values" - core { implicit conn =>
      for {
        _ <- testData

        // Retrieve multiple values
        _ <- m(Ns.int.strMap(?))(Map("en" -> "Hello", "da" -> "Hej")).get.map(_ ==> List(
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))
        // Or tie them together with a regex
        _ <- m(Ns.int.strMap(?))(Map(".*" -> "Hello|Hej")).get.map(_ ==> List(
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        // Using `contains` is redundant since it translates to the same as above
        _ <- m(Ns.int.strMap.contains(?))(Map(".*" -> "Hello|Hej")).get.map(_ ==> List(
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))
        _ <- m(Ns.int.strMap_.contains(?))(Map(".*" -> "Hello|Hej")).get.map(_ ==> List(3, 4))

        // We can also mix varying pairs with regexes
        _ <- m(Ns.int.strMap(?))(Map("en" -> ".*Hi.*|.*He.*", "fr" -> "Bo.*")).get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
          (3, Map("en" -> "Hello"))
        ))

        _ <- m(Ns.int.intMap(?))(Map("en" -> 10, "fr" -> 20)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("fr" -> 20, "en" -> 10))
        ))
        _ <- m(Ns.int.intMap_(?))(Map("en" -> 10, "fr" -> 20)).get.map(_ ==> List(1, 2))
      } yield ()
    }


    "One key, multiple values" - core { implicit conn =>
      val hi_he = List(
        (1, Map("en" -> "Hi there")),
        (2, Map("en" -> "Oh, Hi")),
        (3, Map("en" -> "Hello"))
      )

      val maps = Seq(Map("en" -> ".*Hi.*"), Map("en" -> ".*He.*"))

      // OBS: Can't use one map for same-key pairs since they will silently coalesce!
      // m(Ns.int.strMap(?)).apply(Map("en" -> "Hi", "en" -> "He"))

      for {
        _ <- testData

        // Instead we can supply 2 Maps
        _ <- m(Ns.int.strMap(?))(Map("en" -> ".*Hi.*"), Map("en" -> ".*He.*")).get.map(_ ==> hi_he)

        // or a Seq of Maps (useful if saved in a variable)
        _ <- m(Ns.int.strMap(?))(Seq(Map("en" -> ".*Hi.*"), Map("en" -> ".*He.*"))).get.map(_ ==> hi_he)
        _ <- m(Ns.int.strMap(?))(maps).get.map(_ ==> hi_he)

        // The easiest solution is though to use a regEx
        _ <- m(Ns.int.strMap(?))(Map("en" -> ".*Hi.*|.*He.*")).get.map(_ ==> hi_he)


        // With other types we need to apply multiple Maps if we have multiple
        // identical keys with varying values to lookup
        _ <- m(Ns.int.intMap(?))(Map("en" -> 10), Map("en" -> 30)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10)),
          (3, Map("en" -> 30))
        ))

        // OBS: Can't use one map for same-key pairs since they will silently coalesce!
        // m(Ns.int.intMap(?))(Map("en" -> 10, "en" -> 30))

        // 3 pairs
        _ <- m(Ns.int.intMap(?))(Map("en" -> 10), Map("en" -> 30), Map("fr" -> 20)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("fr" -> 20, "en" -> 10)),
          (3, Map("en" -> 30))
        ))
      } yield ()
    }
  }
}
