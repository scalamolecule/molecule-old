package molecule
package attrMap
import molecule.util.dsl.coreTest._

class KeyValues extends Base {

  // Attribute maps offer to work with pairs of key/values in various ways.

  "(Key -> value)" in new Setup {

    // Find a specific key/value pair
    Ns.int.strMap("en" -> "Hi there").get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(("en", "Hi there")).get === List((1, Map("en" -> "Hi there")))


    // Multiple pairs
    Ns.int.strMap("en" -> "Hi there" or "fr" -> "Bonjour").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour"))
    )
    // Same as
    Ns.int.strMap("en" -> "Hi there", "fr" -> "Bonjour").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour"))
    )


    // Variables sanity checks

    val hiThere = "Hi there"
    Ns.int.strMap("en" -> hiThere).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(en -> "Hi there").get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(en -> hiThere).get === List((1, Map("en" -> "Hi there")))

    // Alternative tuple notation
    Ns.int.strMap(("en", hiThere)).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap((en, "Hi there")).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap((en, hiThere)).get === List((1, Map("en" -> "Hi there")))

    val pair1 = "en" -> "Hi there"
    val pair2 = "en" -> hiThere
    val pair3 = en -> "Hi there"
    val pair4 = en -> hiThere
    val pair5 = ("en", "Hi there")
    val pair6 = ("en", hiThere)
    val pair7 = (en, "Hi there")
    val pair8 = (en, hiThere)
    Ns.int.strMap(pair1).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(pair2).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(pair3).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(pair4).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(pair5).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(pair6).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(pair7).get === List((1, Map("en" -> "Hi there")))
    Ns.int.strMap(pair8).get === List((1, Map("en" -> "Hi there")))

    val pair9 = (fr, "Bonjour")

    Ns.int.strMap("en" -> hiThere, fr -> "Bonjour").get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap("en" -> hiThere, pair9).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap(pair2, fr -> "Bonjour").get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap(pair2, pair9).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))

    Ns.int.strMap(Seq("en" -> hiThere, fr -> "Bonjour")).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap(Seq("en" -> hiThere, pair9)).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap(Seq(pair2, fr -> "Bonjour")).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap(Seq(pair2, pair9)).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))

    val pairs1 = Seq("en" -> hiThere, fr -> "Bonjour")
    val pairs2 = Seq("en" -> hiThere, pair9)
    val pairs3 = Seq(pair2, fr -> "Bonjour")
    val pairs4 = Seq(pair2, pair9)

    //      Ns.int.strMap(pairs1).debug


    Ns.int.strMap(pairs1).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap(pairs2).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap(pairs3).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
    Ns.int.strMap(pairs4).get === List((1, Map("en" -> "Hi there")), (2, Map("fr" -> "Bonjour")))
  }

//      "(Key -> value) with regex" in new Setup {
//
//        // We can widen the values with a regex
//        Ns.int.strMap("en|da" -> "He.*").get === List(
//          (1, Map("en" -> "Hi there")),
//          (2, Map("en" -> "Oh, Hi"))
//        )
//
//        Ns.int.strMap("en" -> ".*Hi.*").get === List(
//          (1, Map("en" -> "Hi there")),
//          (2, Map("en" -> "Oh, Hi"))
//        )
//
//        // ??
//        Ns.int.strMap().get === List()
//
//      }
//
//    "(key)(value)" in new Setup {
//
//      // As an alternative to tuple notation we can use k/apply
//      Ns.int.strMap("en" -> "Hi there").get === List((1, Map("en" -> "Hi there")))
//      Ns.int.strMap.k("en")("Hi there").get === List((1, Map("en" -> "Hi there")))
//
//      // The k/apply syntax is suitable when we want to treat
//      // key and value independently. So, instead of repeating the key:
//      Ns.int.strMap("en" -> "Hi there", "en" -> "Hello").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello"))
//      )
//      // ..we can say
//      Ns.int.strMap.k("en")("Hi there" or "Hello").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello"))
//      )
//
//      Ns.int.strMap.k("en")(".*Hi.*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (2, Map("en" -> "Oh, Hi"))
//      )
//
//      Ns.int.intMap("en" -> 10).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("en" -> 10))
//      )
//      Ns.int.intMap.k("en")(10).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("en" -> 10))
//      )
//      // We can't use regex on other types than String
//      // Ns.int.intMap("en" -> ".*1.*") // doesn't type check
//
//    }
//
//
//    "One key / multiple values" in new Setup {
//
//      Ns.int.strMap.k("en")("Hi there" or "Hello").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello"))
//      )
//      Ns.int.strMap.k("en")("Hi there", "Hello").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello"))
//      )
//      // For String values we can even use a regEx in the value part
//      Ns.int.strMap.k("en")(".*(Hi there|Hello).*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello"))
//      )
//      // And then we might as well use a pair
//      Ns.int.strMap("en" -> ".*(Hi there|Hello).*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello"))
//      )
//
//      // Regex for subpattern (only for type String)
//      Ns.int.strMap.k("en")(".*(Hi|He).*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (2, Map("en" -> "Oh, Hi")),
//        (3, Map("en" -> "Hello"))
//      )
//      Ns.int.strMap("en" -> ".*(Hi|He).*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (2, Map("en" -> "Oh, Hi")),
//        (3, Map("en" -> "Hello"))
//      )
//      Ns.int.strMap("en" -> ".*Hi.*", "en" -> ".*He.*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (2, Map("en" -> "Oh, Hi")),
//        (3, Map("en" -> "Hello"))
//      )
//
//      // Other types can only search for full values (no regex)
//      Ns.int.intMap.k("en")(10 or 30).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("en" -> 10)),
//        (3, Map("en" -> 30))
//      )
//      Ns.int.intMap.k("en")(10, 30).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("en" -> 10)),
//        (3, Map("en" -> 30))
//      )
//      Ns.int.intMap("en" -> 10, "en" -> 30).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("en" -> 10)),
//        (3, Map("en" -> 30))
//      )
//    }
//
//
//    "Multiple keys / one value" in new Setup {
//      val keys = Seq("en", "da")
//
//      // Search value across multiple keys
//      // (we don't necessarily need a regex - we just don't have equal values in the current data set)
//      // (Note that search value is Case insensitive - "there" is included)
//      Ns.int.strMap.k("en" or "da")(".*he.*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello")),
//        (4, Map("da" -> "Hej"))
//      )
//      // Comma-separated keys have OR-semantics
//      Ns.int.strMap.k("en", "da")(".*he.*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello")),
//        (4, Map("da" -> "Hej"))
//      )
//      // Sequence of keys
//      Ns.int.strMap.k(keys)(".*he.*").get === List(
//        (1, Map("en" -> "Hi there")),
//        (3, Map("en" -> "Hello")),
//        (4, Map("da" -> "Hej"))
//      )
//
//      Ns.int.intMap.k("en" or "da")(30).get === List(
//        (3, Map("en" -> 30)),
//        (4, Map("da" -> 30))
//      )
//      Ns.int.intMap.k("en", "da")(30).get === List(
//        (3, Map("en" -> 30)),
//        (4, Map("da" -> 30))
//      )
//      Ns.int.intMap.k(keys)(30).get === List(
//        (3, Map("en" -> 30)),
//        (4, Map("da" -> 30))
//      )
//    }
//
//
//    "Multiple keys / multiple values" >> {
//
//      "Some keys, some values" in new Setup {
//
//        Ns.int.strMap.k("en" or "da")(".*hi.*" or ".*he.*").get === List(
//          (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
//          (3, Map("en" -> "Hello", "da" -> "Hejhej")),
//          (4, Map("da" -> "Hej"))
//        )
//
//        Ns.int.intMap.k("en" or "da")(10 or 30).get === List(
//          (1, Map("en" -> 10, "da" -> 30)),
//          (2, Map("en" -> 10, "da" -> 10)),
//          (3, Map("en" -> 30, "da" -> 30)),
//          (4, Map("da" -> 30))
//        )
//        Ns.int.intMap.k("en" or "da" or "it")(10 or 30).get === List(
//          (1, Map("en" -> 10, "da" -> 30)),
//          (2, Map("en" -> 10, "da" -> 10, "it" -> 30)),
//          (3, Map("en" -> 30, "da" -> 30)),
//          (4, Map("da" -> 30))
//        )
//      }
//    }
//
//
//    "Multiple key/value pairs" in new Setup {
//
//      // To have full control of matching keys and values we
//      // can use multiple pairs
//
//      // OR semantics
//
//      // Comma-separated pairs have OR-semantics (can't use 'or'-notation for pairs)
//      Ns.int.strMap("en" -> "Hi there", "da" -> "Hej").get === List(
//        (1, Map("en" -> "Hi there")),
//        (4, Map("da" -> "Hej"))
//      )
//
//      Ns.int.intMap("en" -> 10, "fr" -> 20).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("fr" -> 20, "en" -> 10))
//      )
//
//      // Multiple keys with multiple values for some keys (OR semantics)
//      Ns.int.strMap("en" -> "Hi", "en" -> "He", "fr" -> "Bo").get === List(
//        (1, Map("en" -> "Hi there")),
//        (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//        (3, Map("en" -> "Hello"))
//      )
//      // or
//      Ns.int.strMap("en" -> "Hi|He", "fr" -> "Bo").get === List(
//        (1, Map("en" -> "Hi there")),
//        (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//        (3, Map("en" -> "Hello"))
//      )
//
//    }
//
//    "Expressions" in new Setup {
//
//      Ns.int.strMap.k("en" or "da").contains("He").get === List(
//        (1, Map("da" -> "Hejsa")),
//        (3, Map("en" -> "Hello", "da" -> "Hejhej")),
//        (4, Map("da" -> "Hej"))
//      )
//
//      Ns.int.intMap.k("en" or "da").>(10).get === List(
//        (1, Map("da" -> 30)),
//        (2, Map("fr" -> 20, "it" -> 30)),
//        (3, Map("en" -> 30, "da" -> 30)),
//        (4, Map("da" -> 30))
//      )
//      Ns.int.intMap.k("en" or "da" or "it")(10 or 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10, "it" -> 30)),
//        (3, Map("en" -> 30, "da" -> 30)),
//        (4, Map("da" -> 30))
//      )
//
//
//
//      Ns.int.intMap.k("en" or "da").>(10).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10)),
//        (3, Map("en" -> 30, "da" -> 30)),
//        (4, Map("da" -> 30))
//      )
//    }
}