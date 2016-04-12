//package molecule
//package attrMap
//import molecule.util.dsl.coreTest._
//import molecule.util.expectCompileError
//
//class Values extends Base {
//
//
//  "Types" in new Setup {
//
//    // All mapped values
//    Ns.int.strMap.get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    Ns.int.intMap.get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10)),
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30))
//    )
//    Ns.int.longMap.get === List(
//      (1, Map("en" -> 10L)),
//      (2, Map("fr" -> 20L, "en" -> 10L)),
//      (3, Map("en" -> 30L)),
//      (4, Map("da" -> 40L))
//    )
//    Ns.int.floatMap.get === List(
//      (1, Map("en" -> 10f)),
//      (2, Map("fr" -> 20f, "en" -> 10f)),
//      (3, Map("en" -> 30f)),
//      (4, Map("da" -> 30f))
//    )
//    Ns.int.doubleMap.get === List(
//      (1, Map("en" -> 10.0)),
//      (2, Map("fr" -> 20.0, "en" -> 10.0)),
//      (3, Map("en" -> 30.0)),
//      (4, Map("da" -> 30.0))
//    )
//    Ns.int.boolMap.get === List(
//      (1, Map("en" -> true)),
//      (2, Map("fr" -> false, "en" -> true)),
//      (3, Map("en" -> false)),
//      (4, Map("da" -> false))
//    )
//    Ns.int.dateMap.get === List(
//      (1, Map("en" -> date1)),
//      (2, Map("fr" -> date2, "en" -> date1)),
//      (3, Map("en" -> date3)),
//      (4, Map("da" -> date3))
//    )
//    Ns.int.uuidMap.get === List(
//      (1, Map("en" -> uuid1)),
//      (2, Map("fr" -> uuid2, "en" -> uuid1)),
//      (3, Map("en" -> uuid3)),
//      (4, Map("da" -> uuid3))
//    )
//    Ns.int.uriMap.get === List(
//      (1, Map("en" -> uri1)),
//      (2, Map("fr" -> uri2, "en" -> uri1)),
//      (3, Map("en" -> uri3)),
//      (4, Map("da" -> uri3))
//    )
//  }
//
//
//  "Any keys /  values" in new Setup {
//
//    // All keys with a value
//    Ns.int.strMap("_" -> "He").get === List(
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    Ns.int.intMap("_" -> 10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // All keys with multiple values (OR semantics)
//    Ns.int.strMap("_" -> "He", "_" -> "Bo").get === List(
//      (2, Map("fr" -> "Bonjour")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    Ns.int.strMap("_" -> "He|Bo").get === List(
//      (2, Map("fr" -> "Bonjour")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//
//    // Note that we can't combine searching for all keys and a specific key
//    expectCompileError(
//      """m(Ns.int.strMap("_" -> "He", "fr" -> "Bo"))""",
//      "[Dsl2Model:getValues] Searching for all keys with `_` can't be combined with other key-values.")
//
//
//    // Results are coalesced to one Map when no other attributes are present in the molecule
//    Ns.strMap("_" -> "He", "_" -> "Bo").get === List(
//      Map("en" -> "Hello", "fr" -> "Bonjour", "da" -> "Hej")
//    )
//  }
//}