//package molecule
//package attrMap
//import molecule.util.dsl.coreTest._
//import molecule.util.{CoreSetup, CoreSpec, expectCompileError}
//
//class Tacet extends Base {
//
//
//
//  "Key (tacet)" in new Setup {
//
//    // Int values of entities with mapped values
//    Ns.int.strMap_.get === List(1, 2, 3, 4)
//    Ns.int.intMap_.get === List(1, 2, 3, 4)
//
//    // Int values of entities with an english value
//    Ns.int.strMap_.k("en").get === List(1, 2, 3)
//    Ns.int.intMap_.k("en").get === List(1, 2, 3)
//
//    // Int values of entities with an english or french value
//    Ns.int.strMap_.k("fr", "da").get === List(2, 4)
//    Ns.int.intMap_.k("fr", "da").get === List(2, 4)
//  }
//
//
//  "Value (tacet)" in new Setup {
//
//    // One key, one value
//    Ns.int.strMap_("en" -> "Hi").get === List(1, 2)
//    Ns.int.intMap_("en" -> 10).get === List(1, 2)
//
//    // One key, multiple values (OR semantics)
//    Ns.int.strMap_("en" -> "Hi", "en" -> "He").get === List(1, 2, 3)
//    Ns.int.strMap_("en" -> "Hi|He").get === List(1, 2, 3)
//
//    Ns.int.intMap_("en" -> 10, "en" -> 30).get === List(1, 2, 3)
//
//    // Multiple keys
//    Ns.int.strMap_("en" -> "Hi", "fr" -> "Bon").get === List(1, 2)
//    Ns.int.intMap_("en" -> 10, "fr" -> 20).get === List(1, 2)
//
//    // Multiple keys with multiple values for some keys (OR semantics)
//    Ns.int.strMap_("en" -> "Hi", "en" -> "He", "fr" -> "Bon").get === List(1, 2, 3)
//    Ns.int.intMap_("en" -> 10, "en" -> 30, "fr" -> 20).get === List(1, 2, 3)
//
//    // All keys
//    Ns.int.strMap_("_" -> "He").get === List(3, 4)
//    Ns.int.intMap_("_" -> 30).get === List(3, 4)
//
//    // All keys with multiple values (OR semantics)
//    Ns.int.strMap_("_" -> "He", "_" -> "Bon").get === List(2, 3, 4)
//    Ns.int.intMap_("_" -> 20, "_" -> 30).get === List(2, 3, 4)
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