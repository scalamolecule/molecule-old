package moleculeTests.tests.core.attrMap

import molecule.datomic.api.out2._
import moleculeTests.tests.core.attrMap.Pairs.testData
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object Values extends Base {

  lazy val tests = Tests {

    "Type checks" - core { implicit conn =>
      for {
        _ <- testData
        // All mapped values
        _ <- Ns.int.strMap.get.map(_ ==> List(
          (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        // Tacit attribute map
        _ <- Ns.int.strMap_.get.map(_ ==> List(1, 2, 3, 4))

        _ <- Ns.int.intMap.get.map(_ ==> List(
          (1, Map("en" -> 10, "da" -> 30)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20, "it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))
        _ <- Ns.int.longMap.get.map(_ ==> List(
          (1, Map("en" -> 10L, "da" -> 30L)),
          (2, Map("en" -> 10L, "da" -> 10L, "fr" -> 20L, "it" -> 30L)),
          (3, Map("en" -> 30L, "da" -> 30L)),
          (4, Map("da" -> 30L))
        ))
        _ <- Ns.int.doubleMap.get.map(_ ==> List(
          (1, Map("en" -> 10.0, "da" -> 30.0)),
          (2, Map("en" -> 10.0, "da" -> 10.0, "fr" -> 20.0, "it" -> 30.0)),
          (3, Map("en" -> 30.0, "da" -> 30.0)),
          (4, Map("da" -> 30.0))
        ))
        _ <- Ns.int.boolMap.get.map(_ ==> List(
          (1, Map("en" -> true, "da" -> false)),
          (2, Map("en" -> true, "da" -> true, "fr" -> false, "it" -> false)),
          (3, Map("en" -> false, "en" -> false)),
          (4, Map("da" -> false))
        ))
        _ <- Ns.int.dateMap.get.map(_ ==> List(
          (1, Map("en" -> date1, "da" -> date3)),
          (2, Map("en" -> date1, "da" -> date1, "fr" -> date2, "it" -> date3)),
          (3, Map("en" -> date3, "da" -> date3)),
          (4, Map("da" -> date3))
        ))
        _ <- Ns.int.uuidMap.get.map(_ ==> List(
          (1, Map("en" -> uuid1, "da" -> uuid3)),
          (2, Map("en" -> uuid1, "da" -> uuid1, "fr" -> uuid2, "it" -> uuid3)),
          (3, Map("en" -> uuid3, "da" -> uuid3)),
          (4, Map("da" -> uuid3))
        ))
        _ <- Ns.int.uriMap.get.map(_ ==> List(
          (1, Map("en" -> uri1, "da" -> uri3)),
          (2, Map("en" -> uri1, "da" -> uri1, "fr" -> uri2, "it" -> uri3)),
          (3, Map("en" -> uri3, "da" -> uri3)),
          (4, Map("da" -> uri3))
        ))
      } yield ()
    }


    "Equality" - core { implicit conn =>
      for {
        _ <- testData
        // As with normal attributes we can search for values by applying an argument.
        // All asserted keyed values are searched to find a match and matched
        // key/value pairs are returned
        _ <- Ns.int.strMap("Hello").get.map(_ ==> List(
          (3, Map("en" -> "Hello"))
        ))
        // Tacit value in attribute map
        _ <- Ns.int.strMap_("Hello").get.map(_ ==> List(3))

        // Note that text searches for attribute maps are case-sensitive
        _ <- Ns.int.strMap("hello").get.map(_ ==> List())


        _ <- Ns.int.intMap(10).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10))
        ))
        _ <- Ns.int.intMap_(10).get.map(_ ==> List(1, 2))

        _ <- Ns.int.longMap(10L).get.map(_ ==> List(
          (1, Map("en" -> 10L)),
          (2, Map("en" -> 10L, "da" -> 10L))
        ))
        _ <- Ns.int.longMap_(10L).get.map(_ ==> List(1, 2))

        _ <- Ns.int.doubleMap(10.0).get.map(_ ==> List(
          (1, Map("en" -> 10.0)),
          (2, Map("en" -> 10.0, "da" -> 10.0))
        ))
        _ <- Ns.int.doubleMap_(10.0).get.map(_ ==> List(1, 2))

        _ <- Ns.int.boolMap(true).get.map(_ ==> List(
          (1, Map("en" -> true)),
          (2, Map("en" -> true, "da" -> true))
        ))
        _ <- Ns.int.boolMap_(true).get.map(_ ==> List(1, 2))

        _ <- Ns.int.dateMap(date1).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1))
        ))
        _ <- Ns.int.dateMap_(date1).get.map(_ ==> List(1, 2))

        _ <- Ns.int.uuidMap(uuid1).get.map(_ ==> List(
          (1, Map("en" -> uuid1)),
          (2, Map("en" -> uuid1, "da" -> uuid1))
        ))
        _ <- Ns.int.uuidMap_(uuid1).get.map(_ ==> List(1, 2))

        _ <- Ns.int.uriMap(uri1).get.map(_ ==> List(
          (1, Map("en" -> uri1)),
          (2, Map("en" -> uri1, "da" -> uri1))
        ))
        _ <- Ns.int.uriMap_(uri1).get.map(_ ==> List(1, 2))
      } yield ()
    }


    "Regex/partial search" - core { implicit conn =>
      for {
        _ <- testData
        // We can search text strings with regular expressions
        // Note that searches are case-sensitive ("there" not included)
        _ <- Ns.int.strMap(".*He.*").get.map(_ ==> List(
          (1, Map("da" -> "Hejsa")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))
        _ <- Ns.int.strMap_(".*He.*").get.map(_ ==> List(1, 3, 4))

        // The above search is identical to using contains
        _ <- Ns.int.strMap.contains("He").get.map(_ ==> List(
          (1, Map("da" -> "Hejsa")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))
        _ <- Ns.int.strMap_.contains("He").get.map(_ ==> List(1, 3, 4))

        // Multiple needles
        _ <- Ns.int.strMap.contains("He", "jour").get.map(_ ==> List(
          (1, Map("da" -> "Hejsa")),
          (2, Map("fr" -> "Bonjour")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))
        _ <- Ns.int.strMap_.contains("He", "jour").get.map(_ ==> List(1, 2, 3, 4))
        // When used with normal attributes, `contains` only looks up
        // full words in freetext searches!
      } yield ()
    }


    "Negation" - core { implicit conn =>
      for {
        _ <- testData
        // String

        // Intellij wrongly indicates an error when using `!=`
        // As an alternative, `not` can be used instead
        _ <- Ns.int(2).strMap.!=("Bon giorno").get.map(_ ==> List(
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour"))
        ))
        // Same as
        _ <- Ns.int(2).strMap.not("Bon giorno").get.map(_ ==> List(
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour"))
        ))
        // Omit multiple values
        // (here Intellij doesn't show an error when using `!=`
        _ <- Ns.int(2).strMap.!=("Bonjour", "Bon giorno").get.map(_ ==> List(
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser"))
        ))
        _ <- Ns.int(2).strMap_.not("Bonjour", "Bon giorno").get.map(_ ==> List(2))


        // Int

        _ <- Ns.int.intMap.not(10).get.map(_ ==> List(
          (1, Map("da" -> 30)),
          (2, Map("fr" -> 20, "it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))
        _ <- Ns.int.intMap.not(10, 30).get.map(_ ==> List(
          (2, Map("fr" -> 20))
        ))
        // Tacit omitted attribute map values
        _ <- Ns.int.intMap_.!=(10, 30).get.map(_ ==> List(2))


        // Boolean

        _ <- Ns.int.boolMap.not(true).get.map(_ ==> List(
          (1, Map("da" -> false)),
          (2, Map("fr" -> false, "it" -> false)),
          (3, Map("en" -> false, "en" -> false)),
          (4, Map("da" -> false))
        ))
        // Should be same as
        _ <- Ns.int.boolMap(false).get.map(_ ==> List(
          (1, Map("da" -> false)),
          (2, Map("fr" -> false, "it" -> false)),
          (3, Map("en" -> false, "en" -> false)),
          (4, Map("da" -> false))
        ))
        _ <- Ns.int.boolMap_.not(true).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.boolMap_(false).get.map(_ ==> List(1, 2, 3, 4))

        _ <- Ns.int.boolMap.not(false).get.map(_ ==> List(
          (1, Map("en" -> true)),
          (2, Map("en" -> true, "da" -> true))
        ))
        // Should be same as
        _ <- Ns.int.boolMap(true).get.map(_ ==> List(
          (1, Map("en" -> true)),
          (2, Map("en" -> true, "da" -> true))
        ))
        _ <- Ns.int.boolMap_.not(false).get.map(_ ==> List(1, 2))
        _ <- Ns.int.boolMap_(true).get.map(_ ==> List(1, 2))

        // Well...
        _ <- Ns.int.boolMap_.!=(true, false).get.map(_ ==> List())
        _ <- Ns.int.boolMap_(true, false).get.map(_ ==> List(1, 2, 3, 4))


        // Date

        _ <- Ns.int.dateMap.not(date1).get.map(_ ==> List(
          (1, Map("da" -> date3)),
          (2, Map("fr" -> date2, "it" -> date3)),
          (3, Map("en" -> date3, "da" -> date3)),
          (4, Map("da" -> date3))
        ))
        _ <- Ns.int.dateMap.not(date1, date3).get.map(_ ==> List(
          (2, Map("fr" -> date2))
        ))
        // Tacit omitted attribute map values
        _ <- Ns.int.dateMap_.!=(date1, date3).get.map(_ ==> List(2))


        // UUID

        _ <- Ns.int.uuidMap.not(uuid1).get.map(_ ==> List(
          (1, Map("da" -> uuid3)),
          (2, Map("fr" -> uuid2, "it" -> uuid3)),
          (3, Map("en" -> uuid3, "da" -> uuid3)),
          (4, Map("da" -> uuid3))
        ))
        _ <- Ns.int.uuidMap.not(uuid1, uuid3).get.map(_ ==> List(
          (2, Map("fr" -> uuid2))
        ))
        // Tacit omitted attribute map values
        _ <- Ns.int.uuidMap_.not(uuid1, uuid3).get.map(_ ==> List(2))


        // URI

        _ <- Ns.int.uriMap.not(uri1).get.map(_ ==> List(
          (1, Map("da" -> uri3)),
          (2, Map("fr" -> uri2, "it" -> uri3)),
          (3, Map("en" -> uri3, "da" -> uri3)),
          (4, Map("da" -> uri3))
        ))
        _ <- Ns.int.uriMap.not(uri1, uri3).get.map(_ ==> List(
          (2, Map("fr" -> uri2))
        ))
        // Tacit omitted attribute map values
        _ <- Ns.int.uriMap_.!=(uri1, uri3).get.map(_ ==> List(2))
      } yield ()
    }


    "Comparison" - core { implicit conn =>
      for {
        _ <- testData
        // String

        // Values lexically after 'Hej'
        // (See java.util.String.compareTo)
        _ <- Ns.int.strMap.>("Hej").get.map(_ ==> List(
          (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
          (3, Map("en" -> "Hello"))
        ))

        // Note that String comparisons are case-sensitive!
        // (lower-case letters come after upper-case letters)
        _ <- Ns.int.strMap.>("hej").get.map(_ ==> List())

        // Values lexically equal to or after 'Hej'
        _ <- Ns.int.strMap.>=("Hej").get.map(_ ==> List(
          (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        // Values lexically equal to or before 'Hej'
        _ <- Ns.int.strMap.<=("Hej").get.map(_ ==> List(
          (2, Map("fr" -> "Bonjour", "it" -> "Bon giorno")),
          (3, Map("da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        // Values lexically before 'Hej'
        _ <- Ns.int.strMap.<("Hej").get.map(_ ==> List(
          (2, Map("fr" -> "Bonjour", "it" -> "Bon giorno"))
        ))

        // To find values starting with 'H' or before
        // we need to "go below the next character":
        _ <- Ns.int.strMap.<("I").get.map(_ ==> List(
          (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
          (2, Map("da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        // Values before 'H'
        _ <- Ns.int.strMap.<("H").get.map(_ ==> List(
          (2, Map("fr" -> "Bonjour", "it" -> "Bon giorno"))
        ))

        _ <- Ns.int.strMap_.>("Hej").get.map(_.sorted ==> List(1, 2, 3))
        _ <- Ns.int.strMap_.>=("Hej").get.map(_.sorted ==> List(1, 2, 3, 4))
        _ <- Ns.int.strMap_.<=("Hej").get.map(_.sorted ==> List(2, 3, 4))
        _ <- Ns.int.strMap_.<("Hej").get.map(_.sorted ==> List(2))


        // Int

        _ <- Ns.int.intMap.>(20).get.map(_ ==> List(
          (1, Map("da" -> 30)),
          (2, Map("it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))
        _ <- Ns.int.intMap.>=(20).get.map(_ ==> List(
          (1, Map("da" -> 30)),
          (2, Map("fr" -> 20, "it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))
        _ <- Ns.int.intMap.<=(20).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20))
        ))
        _ <- Ns.int.intMap.<(20).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10))
        ))

        _ <- Ns.int.intMap.>(-10).get.map(_ ==> List(
          (1, Map("en" -> 10, "da" -> 30)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20, "it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))

        _ <- Ns.int.intMap_.>(20).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.intMap_.>=(20).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.intMap_.<=(20).get.map(_ ==> List(1, 2))
        _ <- Ns.int.intMap_.<(20).get.map(_ ==> List(1, 2))


        // Long

        _ <- Ns.int.longMap.>(20L).get.map(_ ==> List(
          (1, Map("da" -> 30L)),
          (2, Map("it" -> 30L)),
          (3, Map("en" -> 30L, "da" -> 30L)),
          (4, Map("da" -> 30L))
        ))
        _ <- Ns.int.longMap.>=(20L).get.map(_ ==> List(
          (1, Map("da" -> 30L)),
          (2, Map("fr" -> 20L, "it" -> 30L)),
          (3, Map("en" -> 30L, "da" -> 30L)),
          (4, Map("da" -> 30L))
        ))
        _ <- Ns.int.longMap.<=(20L).get.map(_ ==> List(
          (1, Map("en" -> 10L)),
          (2, Map("en" -> 10L, "da" -> 10L, "fr" -> 20L))
        ))
        _ <- Ns.int.longMap.<(20L).get.map(_ ==> List(
          (1, Map("en" -> 10L)),
          (2, Map("en" -> 10L, "da" -> 10L))
        ))

        _ <- Ns.int.longMap.>(-10L).get.map(_ ==> List(
          (1, Map("en" -> 10L, "da" -> 30L)),
          (2, Map("en" -> 10L, "da" -> 10L, "fr" -> 20L, "it" -> 30L)),
          (3, Map("en" -> 30L, "da" -> 30L)),
          (4, Map("da" -> 30L))
        ))

        _ <- Ns.int.longMap_.>(20L).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.longMap_.>=(20L).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.longMap_.<=(20L).get.map(_ ==> List(1, 2))
        _ <- Ns.int.longMap_.<(20L).get.map(_ ==> List(1, 2))


        // Double

        _ <- Ns.int.doubleMap.>(20.0).get.map(_ ==> List(
          (1, Map("da" -> 30.0)),
          (2, Map("it" -> 30.0)),
          (3, Map("en" -> 30.0, "da" -> 30.0)),
          (4, Map("da" -> 30.0))
        ))
        _ <- Ns.int.doubleMap.>=(20.0).get.map(_ ==> List(
          (1, Map("da" -> 30.0)),
          (2, Map("fr" -> 20.0, "it" -> 30.0)),
          (3, Map("en" -> 30.0, "da" -> 30.0)),
          (4, Map("da" -> 30.0))
        ))
        _ <- Ns.int.doubleMap.<=(20.0).get.map(_ ==> List(
          (1, Map("en" -> 10.0)),
          (2, Map("en" -> 10.0, "da" -> 10.0, "fr" -> 20.0))
        ))
        _ <- Ns.int.doubleMap.<(20.0).get.map(_ ==> List(
          (1, Map("en" -> 10.0)),
          (2, Map("en" -> 10.0, "da" -> 10.0))
        ))

        _ <- Ns.int.doubleMap.>(-10.0).get.map(_ ==> List(
          (1, Map("en" -> 10.0, "da" -> 30.0)),
          (2, Map("en" -> 10.0, "da" -> 10.0, "fr" -> 20.0, "it" -> 30.0)),
          (3, Map("en" -> 30.0, "da" -> 30.0)),
          (4, Map("da" -> 30.0))
        ))

        _ <- Ns.int.doubleMap_.>(20.0).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.doubleMap_.>=(20.0).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.doubleMap_.<=(20.0).get.map(_ ==> List(1, 2))
        _ <- Ns.int.doubleMap_.<(20.0).get.map(_ ==> List(1, 2))


        // Date

        _ <- Ns.int.dateMap.>(date2).get.map(_ ==> List(
          (1, Map("da" -> date3)),
          (2, Map("it" -> date3)),
          (3, Map("en" -> date3, "da" -> date3)),
          (4, Map("da" -> date3))
        ))

        _ <- Ns.int.dateMap.>=(date2).get.map(_ ==> List(
          (1, Map("da" -> date3)),
          (2, Map("fr" -> date2, "it" -> date3)),
          (3, Map("en" -> date3, "da" -> date3)),
          (4, Map("da" -> date3))
        ))
        _ <- Ns.int.dateMap.<=(date2).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1, "fr" -> date2))
        ))
        _ <- Ns.int.dateMap.<(date2).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1))
        ))

        _ <- Ns.int.dateMap_.>(date2).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.dateMap_.>=(date2).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.dateMap_.<=(date2).get.map(_ ==> List(1, 2))
        _ <- Ns.int.dateMap_.<(date2).get.map(_ ==> List(1, 2))


        // Comparing Boolean, UUID and URI doesn't make sense
      } yield ()
    }


    "Multiple values (OR semantics)" - core { implicit conn =>
      for {
        _ <- testData
        // String

        // We can look for multiple strings in various ways

        // 1. Comma-separated list
        _ <- Ns.int.strMap("Hello", "Hej").get.map(_ ==> List(
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        // 'or'-separated list not allowed (reserved for key/value pairs)
        // Ns.int.strMap("Hello" or "Hej")

        // 2. Seq
        _ <- Ns.int.strMap.apply(Seq("Hello", "Hej")).get.map(_ ==> List(
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))
        // Supplied as variable
        seq = Seq("Hello", "Hej")
        _ <- Ns.int.strMap(seq).get.map(_ ==> List(
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        // 3. Regex
        _ <- Ns.int.strMap("Hello|Hej").get.map(_ ==> List(
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        _ <- Ns.int.strMap_("Hello", "Hej").get.map(_ ==> List(3, 4))
        _ <- Ns.int.strMap_(Seq("Hello", "Hej")).get.map(_ ==> List(3, 4))
        _ <- Ns.int.strMap_(seq).get.map(_ ==> List(3, 4))
        _ <- Ns.int.strMap_("Hello|Hej").get.map(_ ==> List(3, 4))


        // Int

        _ <- Ns.int.intMap(10, 20).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20))
        ))
        _ <- Ns.int.intMap(Seq(10, 20)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20))
        ))
        // We can even apply multiple seqs
        _ <- Ns.int.intMap(Seq(10), Seq(20)).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20))
        ))
        intSeq = Seq(10, 20)
        _ <- Ns.int.intMap(intSeq).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10, "fr" -> 20))
        ))

        _ <- Ns.int.intMap_(10, 20).get.map(_ ==> List(1, 2))
        _ <- Ns.int.intMap_(Seq(10, 20)).get.map(_ ==> List(1, 2))
        _ <- Ns.int.intMap_(Seq(10), Seq(20)).get.map(_ ==> List(1, 2))
        _ <- Ns.int.intMap_(intSeq).get.map(_ ==> List(1, 2))


        // Long

        _ <- Ns.int.longMap(10L, 20L).get.map(_ ==> List(
          (1, Map("en" -> 10L)),
          (2, Map("en" -> 10L, "da" -> 10L, "fr" -> 20L))
        ))
        _ <- Ns.int.longMap(Seq(10L, 20L)).get.map(_ ==> List(
          (1, Map("en" -> 10L)),
          (2, Map("en" -> 10L, "da" -> 10L, "fr" -> 20L))
        ))

        _ <- Ns.int.longMap_(10L, 20L).get.map(_ ==> List(1, 2))
        _ <- Ns.int.longMap_(Seq(10L, 20L)).get.map(_ ==> List(1, 2))


        // Double

        _ <- Ns.int.doubleMap(10.0, 20.0).get.map(_ ==> List(
          (1, Map("en" -> 10.0)),
          (2, Map("en" -> 10.0, "da" -> 10.0, "fr" -> 20.0))
        ))
        _ <- Ns.int.doubleMap(Seq(10.0, 20.0)).get.map(_ ==> List(
          (1, Map("en" -> 10.0)),
          (2, Map("en" -> 10.0, "da" -> 10.0, "fr" -> 20.0))
        ))

        _ <- Ns.int.doubleMap_(10.0, 20.0).get.map(_ ==> List(1, 2))
        _ <- Ns.int.doubleMap_(Seq(10.0, 20.0)).get.map(_ ==> List(1, 2))


        // Date

        _ <- Ns.int.dateMap(date1, date2).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1, "fr" -> date2))
        ))
        _ <- Ns.int.dateMap(Seq(date1, date2)).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1, "fr" -> date2))
        ))

        _ <- Ns.int.dateMap_(date1, date2).get.map(_ ==> List(1, 2))
        _ <- Ns.int.dateMap_(Seq(date1, date2)).get.map(_ ==> List(1, 2))


        // UUID

        _ <- Ns.int.uuidMap(uuid1, uuid2).get.map(_ ==> List(
          (1, Map("en" -> uuid1)),
          (2, Map("en" -> uuid1, "da" -> uuid1, "fr" -> uuid2))
        ))
        _ <- Ns.int.uuidMap(Seq(uuid1, uuid2)).get.map(_ ==> List(
          (1, Map("en" -> uuid1)),
          (2, Map("en" -> uuid1, "da" -> uuid1, "fr" -> uuid2))
        ))

        _ <- Ns.int.uuidMap_(uuid1, uuid2).get.map(_ ==> List(1, 2))
        _ <- Ns.int.uuidMap_(Seq(uuid1, uuid2)).get.map(_ ==> List(1, 2))


        // URI

        _ <- Ns.int.uriMap(uri1, uri2).get.map(_ ==> List(
          (1, Map("en" -> uri1)),
          (2, Map("en" -> uri1, "da" -> uri1, "fr" -> uri2))
        ))
        _ <- Ns.int.uriMap(Seq(uri1, uri2)).get.map(_ ==> List(
          (1, Map("en" -> uri1)),
          (2, Map("en" -> uri1, "da" -> uri1, "fr" -> uri2))
        ))

        _ <- Ns.int.uriMap_(uri1, uri2).get.map(_ ==> List(1, 2))
        _ <- Ns.int.uriMap_(Seq(uri1, uri2)).get.map(_ ==> List(1, 2))

        // Searching for multiple Boolean values doesn't make sense...
      } yield ()
    }
  }
}