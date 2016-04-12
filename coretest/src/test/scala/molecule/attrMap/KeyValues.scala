//package molecule
//package attrMap
//import molecule.util.dsl.coreTest._
//import molecule.util.{CoreSetup, CoreSpec, expectCompileError}
//
//class KeyValues extends Base {
//
//
//  "One key / one value" in new Setup {
//
//    // One key, one value
//
//    // After asking for a key we can apply a value
//    Ns.int.strMap.k("en")("Hi there").get === List(
//      (1, Map("en" -> "Hi there"))
//    )
//    // Or we can apply a pair
//    Ns.int.strMap("en" -> "Hi there").get === List(
//      (1, Map("en" -> "Hi there"))
//    )
//    Ns.int.strMap("en" -> ".*Hi.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    Ns.int.strMap.k("en")(".*Hi.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//
//    Ns.int.intMap("en" -> 10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//    Ns.int.intMap.k("en")(10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//    // We can't use regex on other types than String
//    // Ns.int.intMap("en" -> ".*1.*") // doesn't type check
//
//  }
//
//
//  "One key / multiple values" in new Setup {
//
//    // OR semantics
//
//    Ns.int.strMap.k("en")("Hi there" or "Hello").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap.k("en")("Hi there", "Hello").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//    // Pairs with same key can be used too, although more clumsy
//    Ns.int.strMap("en" -> "Hi there", "en" -> "Hello").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//    // For String values we can even use a regEx in the value part
//    Ns.int.strMap.k("en")(".*(Hi there|Hello).*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//    // And then we might as well use a pair
//    Ns.int.strMap("en" -> ".*(Hi there|Hello).*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    // Regex for subpattern (only for type String)
//    Ns.int.strMap.k("en")(".*(Hi|He).*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap("en" -> ".*(Hi|He).*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap("en" -> ".*Hi.*", "en" -> ".*He.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    // Other types can only search for full values (no regex)
//    Ns.int.intMap.k("en")(10 or 30).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//    Ns.int.intMap.k("en")(10, 30).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//    Ns.int.intMap("en" -> 10, "en" -> 30).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//
//    // AND semantics are not relevant with a single key
//    // Ns.int.strMap.k("en")("Hi there" and "Hello") // can't exist
//  }
//
//
//  "Multiple keys / one value" in new Setup {
//    val keys = Seq("en", "da")
//
//    // OR semantics
//
//    // Search value across multiple keys
//    // (we don't necessarily need a regex - we just don't have equal values in the current data set)
//    // (Note that search value is Case insensitive - "there" is included)
//    Ns.int.strMap.k("en" or "da")(".*he.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    // Comma-separated keys have OR-semantics
//    Ns.int.strMap.k("en", "da")(".*he.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    // Sequence of keys
//    Ns.int.strMap.k(keys)(".*he.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//
//    Ns.int.intMap.k("en" or "da")(30).get === List(
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30))
//    )
//    Ns.int.intMap.k("en", "da")(30).get === List(
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30))
//    )
//    Ns.int.intMap.k(keys)(30).get === List(
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30))
//    )
//
//    // AND semantics - multiple keys with the same value
//
//    Ns.int.strMap.k("en" and "da")(".*he.*").get === List(
//      (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//      (3, Map("en" -> "Hello", "da" -> "Hejhej"))
//    )
//
//    Ns.int.intMap.k("en" and "da")(30).get === List(
//      (3, Map("en" -> 30, "da" -> 30))
//    )
//  }
//
//
//  "Multiple keys / multiple values" >> {
//
//
//    "Some keys, some values" in new Setup {
//
//      Ns.int.strMap.k("en" or "da")(".*hi.*" or ".*he.*").get === List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//        (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
//        (3, Map("en" -> "Hello", "da" -> "Hejhej")),
//        (4, Map("da" -> "Hej"))
//      )
//
//      Ns.int.intMap.k("en" or "da")(10 or 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10)),
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
//    }
//
//
//    "Some keys, certain values" in new Setup {
//
//      Ns.int.strMap.k("en" or "da")(".*hi.*" and ".*he.*").get === List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa"))
//      )
//
//      Ns.int.intMap.k("en" or "da")(10 and 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30))
//      )
//      Ns.int.intMap.k("en" or "da" or "it")(10 and 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10, "it" -> 30))
//      )
//    }
//
//
//    "Certain keys, some values" in new Setup {
//
//      Ns.int.strMap.k("en" and "da")(".*hi.*" or ".*he.*").get === List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//        (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
//        (3, Map("en" -> "Hello", "da" -> "Hejhej"))
//      )
//
//      Ns.int.intMap.k("en" and "da")(10 or 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10)),
//        (3, Map("en" -> 30, "da" -> 30))
//      )
//      Ns.int.intMap.k("en" and "da" and "it")(10 or 30).get === List(
//        (2, Map("en" -> 10, "da" -> 10, "it" -> 30))
//      )
//    }
//
//
//    "Certain keys, certain values" in new Setup {
//
//      Ns.int.strMap.k("en" and "da")(".*hi.*" and ".*he.*").get === List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa"))
//      )
//
//      Ns.int.intMap.k("en" and "da")(10 and 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30))
//      )
//      Ns.int.intMap.k("en" and "da" and "it")(10 and 30).get === List(
//        (2, Map("en" -> 10, "da" -> 10, "it" -> 30))
//      )
//    }
//
//    // AND semantics - multiple pairs sharing multiple values ???
//  }
//
//
//  "Multiple key/value pairs" in new Setup {
//
//    // To have full control of matching keys and values we
//    // can use multiple pairs
//
//    // OR semantics
//
//    Ns.int.strMap("en" -> "Hi there" or "da" -> "Hej").get === List(
//      (1, Map("en" -> "Hi there")),
//      (4, Map("da" -> "Hej"))
//    )
//    // Comma-separated pairs have OR-semantics
//    Ns.int.strMap("en" -> "Hi there", "da" -> "Hej").get === List(
//      (1, Map("en" -> "Hi there")),
//      (4, Map("da" -> "Hej"))
//    )
//
//    Ns.int.intMap("en" -> 10 or "fr" -> 20).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10))
//    )
//    Ns.int.intMap("en" -> 10, "fr" -> 20).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10))
//    )
//
//    // Multiple keys with multiple values for some keys (OR semantics)
//    Ns.int.strMap("en" -> "Hi", "en" -> "He", "fr" -> "Bo").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    // or
//    Ns.int.strMap("en" -> "Hi|He", "fr" -> "Bo").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//  }
//
//  "Key(s) / value expression" in new Setup {
//
//    Ns.int.strMap.k("en" or "da").contains(".*hi.*").get === List(
//      (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//      (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
//      (3, Map("en" -> "Hello", "da" -> "Hejhej")),
//      (4, Map("da" -> "Hej"))
//    )
//
//    Ns.int.intMap.k("en" or "da")(10 or 30).get === List(
//      (1, Map("en" -> 10, "da" -> 30)),
//      (2, Map("en" -> 10, "da" -> 10)),
//      (3, Map("en" -> 30, "da" -> 30)),
//      (4, Map("da" -> 30))
//    )
//    Ns.int.intMap.k("en" or "da" or "it")(10 or 30).get === List(
//      (1, Map("en" -> 10, "da" -> 30)),
//      (2, Map("en" -> 10, "da" -> 10, "it" -> 30)),
//      (3, Map("en" -> 30, "da" -> 30)),
//      (4, Map("da" -> 30))
//    )
//
//
//
//    Ns.int.intMap.k("en" or "da").>(10).get === List(
//      (1, Map("en" -> 10, "da" -> 30)),
//      (2, Map("en" -> 10, "da" -> 10)),
//      (3, Map("en" -> 30, "da" -> 30)),
//      (4, Map("da" -> 30))
//    )
//  }
//}