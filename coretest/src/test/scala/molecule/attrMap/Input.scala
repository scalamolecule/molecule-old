//package molecule
//package attrMap
//import molecule.util.dsl.coreTest._
//import molecule.util.{CoreSetup, CoreSpec, expectCompileError}
//
//class Input extends Base {
//
//
//
//  "Input" in new Setup {
//
//    // One key, one value
//    m(Ns.int.strMap(?))(Map("en" -> "Hi")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    m(Ns.int.intMap(?))(Map("en" -> 10)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // One key, multiple values
//    // Note that we can't apply a map with multiple identical keys to achieve OR semantics.
//    // m(Ns.int.strMap(?)).apply(Map("en" -> "Hi", "en" -> "He")) // no good
//
//    // Instead we can supply 2 Maps
//    m(Ns.int.strMap(?))(Map("en" -> "Hi"), Map("en" -> "He")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    // or a Seq of Maps (useful if saved in a variable)
//    m(Ns.int.strMap(?))(Seq(Map("en" -> "Hi"), Map("en" -> "He"))).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    // The easiest solution is though to use a regEx
//    m(Ns.int.strMap(?))(Map("en" -> "Hi|He")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    // With other types we need to apply multiple Maps if we
//    // have multiple identical keys with varying values to lookup
//    m(Ns.int.intMap(?))(Map("en" -> 10), Map("en" -> 30)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//    // OBS: Be aware not to use a single map since pairs with identical
//    // keys will silently coalesce!!
//    m(Ns.int.intMap(?))(Map("en" -> 10, "en" -> 30)) // keys coalesce!
//
//    // Multiple (different) keys
//    m(Ns.int.strMap(?))(Map("en" -> "Hi", "fr" -> "Bo")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
//    )
//    m(Ns.int.intMap(?))(Map("en" -> 10, "fr" -> 20)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10))
//    )
//
//    // Multiple keys with multiple values for some keys (using OR regex)
//    m(Ns.int.strMap(?))(Map("en" -> "Hi|He", "fr" -> "Bo")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    m(Ns.int.intMap(?))(Map("en" -> 10), Map("en" -> 30), Map("fr" -> 20)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//
//    // All keys
//    m(Ns.int.strMap(?))(Map("_" -> "He")).get === List(
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    m(Ns.int.intMap(?))(Map("_" -> 10)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // All keys with multiple values (using OR regex)
//    m(Ns.int.strMap(?))(Map("_" -> "He|Bo")).get === List(
//      (2, Map("fr" -> "Bonjour")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//
//    // Results are coalesced to one Map when no other attributes are present in the molecule
//    m(Ns.strMap(?))(Map("_" -> "He|Bo")).get === List(
//      Map("en" -> "Hello", "fr" -> "Bonjour", "da" -> "Hej")
//    )
//  }
//
//
//  "Input (tacet)" in new Setup {
//
//    // One key, one value
//    m(Ns.int.strMap_(?))(Map("en" -> "Hi")).get === List(1, 2)
//    m(Ns.int.intMap_(?))(Map("en" -> 10)).get === List(1, 2)
//
//
//    // One key, multiple values (using OR regex)
//    m(Ns.int.strMap_(?))(Map("en" -> "Hi|He")).get === List(1, 2, 3)
//
//    // Remember to apply multiple Maps if some keys are identical to avoid
//    // that they (silently) coalese
//    m(Ns.int.intMap_(?))(Map("en" -> 10), Map("en" -> 30)).get === List(1, 2, 3)
//
//    // Multiple keys
//    m(Ns.int.strMap_(?))(Map("en" -> "Hi", "fr" -> "Bo")).get === List(1, 2)
//    m(Ns.int.intMap_(?))(Map("en" -> 10, "fr" -> 20)).get === List(1, 2)
//
//    // Multiple keys with multiple values for some keys (using OR regex)
//    m(Ns.int.strMap_(?))(Map("en" -> "Hi|He", "fr" -> "Bo")).get === List(1, 2, 3)
//    m(Ns.int.intMap_(?))(Map("en" -> 10), Map("en" -> 30, "fr" -> 20)).get === List(1, 2, 3)
//
//    // All keys
//    m(Ns.int.strMap_(?))(Map("_" -> "He")).get === List(3, 4)
//    m(Ns.int.intMap_(?))(Map("_" -> 30)).get === List(3, 4)
//
//    // All keys with multiple values (using OR regex)
//    m(Ns.int.strMap_(?))(Map("_" -> "He"), Map("_" -> "Bo")).get === List(2, 3, 4)
//    m(Ns.int.intMap_(?))(Map("_" -> 20), Map("_" -> 30)).get === List(2, 3, 4)
//
//    // Note that we can't combine searching for all keys (_) and a specific key (fr)
//    (m(Ns.int.strMap_(?))(Map("_" -> "He"), Map("fr" -> "Bo")) must throwA[RuntimeException]).message === "Got the exception java.lang.RuntimeException: " +
//      "[InputMolecule_1:bindValues1] Searching for all keys (with `_`) can't be combined with other key(s): fr"
//  }
//}