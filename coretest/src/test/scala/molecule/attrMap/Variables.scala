//package molecule
//package attrMap
//import molecule.util.dsl.coreTest._
//
//class Variables extends Base {
//
//  "Variable" in new Setup {
//
//    // Key is variable
//    Ns.int.strMap(en -> "Hi").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    Ns.int.intMap(en -> 10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // Value is variable
//    Ns.int.strMap("en" -> Hi).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    Ns.int.intMap("en" -> i10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // Key and value are variables
//    Ns.int.strMap(en -> Hi).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    Ns.int.intMap(en -> i10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // One key, multiple values (OR semantics)
//    Ns.int.strMap(en -> Hi, en -> He).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap(en -> regEx).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    Ns.int.intMap(en -> i10, en -> 30).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//
//    // Multiple keys
//    Ns.int.strMap(en -> Hi, fr -> Bo).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
//    )
//    Ns.int.intMap(en -> i10, fr -> i20).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10))
//    )
//
//    // Multiple keys with multiple values for some keys (OR semantics)
//    Ns.int.strMap(en -> Hi, en -> He, fr -> Bo).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap(en -> regEx, fr -> Bo).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    // All keys
//    Ns.int.strMap("_" -> He).get === List(
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    Ns.int.intMap("_" -> i10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // All keys with multiple values (OR semantics)
//    Ns.int.strMap("_" -> He, "_" -> Bo).get === List(
//      (2, Map("fr" -> "Bonjour")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//  }
//}