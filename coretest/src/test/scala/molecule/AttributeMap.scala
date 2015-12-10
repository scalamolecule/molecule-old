package molecule

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}

class AttributeMap extends CoreSpec {


  "Manipulate" in new CoreSetup {

    // Insert
    val eid: Long = Ns.strMap insert List(Map("en" -> "Hi")) eid

    // Update + Add
    Ns(eid).strMap.add("en" -> "Hi there", "fr" -> "Bonjour").update
    Ns.strMap.one === Map("en" -> "Hi there", "fr" -> "Bonjour")

    // Remove pair (by key)
    Ns(eid).strMap.remove("en").update
    Ns.strMap.one === Map("fr" -> "Bonjour")

    // Applying nothing (empty parenthesises) finds and retract all values of an attribute
    Ns(eid).strMap().update
    Ns.strMap.one === Map()
  }


  "Key" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    Ns.int.strMap.get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // Key
    Ns.int.strMap("en").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Hi")),
      (3, Map("en" -> "Hello"))
    )

    // Keys
    Ns.int.strMap("en", "fr").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Hi")),
      (3, Map("en" -> "Hello"))
    )
  }


  "Value" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // One key, one value
    Ns.int.strMap("en" -> "Hi").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi"))
    )

    // One key, multiple values (OR semantics)
    Ns.int.strMap("en" -> "Hi", "en" -> "He").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )

    // Multiple keys
    Ns.int.strMap("en" -> "Hi", "fr" -> "Bon").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
    )

    // Multiple keys with multiple values for some keys (OR semantics)
    Ns.int.strMap("en" -> "Hi", "en" -> "He", "fr" -> "Bon").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )

    // All keys
    Ns.int.strMap("_" -> "He").get === List(
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // All keys with multiple values (OR semantics)
    Ns.int.strMap("_" -> "He", "_" -> "Bon").get === List(
      (2, Map("fr" -> "Bonjour")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // Results are coalesced to one Map when no other attributes are present in the molecule
    Ns.strMap("_" -> "He", "_" -> "Bon").get === List(
      Map("en" -> "Hello", "fr" -> "Bonjour", "da" -> "Hej")
    )
  }


  "Variable" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // Dummy variables
    val en  = "en"
    val fr  = "fr"
    val Hi  = "Hi"
    val He  = "He"
    val Bon = "Bon"

    // Key is variable
    Ns.int.strMap(en -> "Hi").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi"))
    )

    // Value is variable
    Ns.int.strMap("en" -> Hi).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi"))
    )

    // Key and value are variables
    Ns.int.strMap(en -> Hi).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi"))
    )

    // One key, multiple values (OR semantics)
    Ns.int.strMap(en -> Hi, en -> He).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )

    // Multiple keys
    Ns.int.strMap(en -> Hi, fr -> Bon).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
    )

    // Multiple keys with multiple values for some keys (OR semantics)
    Ns.int.strMap(en -> Hi, en -> He, fr -> Bon).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )

    // All keys
    Ns.int.strMap("_" -> He).get === List(
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // All keys with multiple values (OR semantics)
    Ns.int.strMap("_" -> He, "_" -> Bon).get === List(
      (2, Map("fr" -> "Bonjour")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // Results are coalesced to one Map when no other attributes are present in the molecule
    Ns.strMap("_" -> He, "_" -> Bon).get === List(
      Map("en" -> "Hello", "fr" -> "Bonjour", "da" -> "Hej")
    )
  }


  "Input" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )
    // One key, one value
    m(Ns.int.strMap(?))(Map("en" -> "Hi")).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi"))
    )


    // One key, multiple values (using OR regex)
    m(Ns.int.strMap(?))(Map("en" -> "Hi|He")).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )

    // Multiple keys
    m(Ns.int.strMap(?))(Map("en" -> "Hi", "fr" -> "Bon")).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
    )

    // Multiple keys with multiple values for some keys (using OR regex)
    m(Ns.int.strMap(?))(Map("en" -> "Hi|He", "fr" -> "Bon")).get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )

    // All keys
    m(Ns.int.strMap(?))(Map("_" -> "He")).get === List(
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // All keys with multiple values (using OR regex)
    m(Ns.int.strMap(?))(Map("_" -> "He|Bon")).get === List(
      (2, Map("fr" -> "Bonjour")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej"))
    )

    // Results are coalesced to one Map when no other attributes are present in the molecule
    m(Ns.strMap(?))(Map("_" -> "He|Bon")).get === List(
      Map("en" -> "Hello", "fr" -> "Bonjour", "da" -> "Hej")
    )
  }
}


//    datomic.Peer.q(
//      """
//        |[:find  ?b (distinct ?c)
//        | :in    $ [[ ?cKey ?cValue ]]
//        | :where [?a :ns/int ?b]
//        |        [?a :ns/strMap ?c]
//        |        [(.startsWith ^String ?c ?cKey)]
//        |        [(.split ^String ?c "@" 2) ?c1]
//        |        [(second ?c1) ?c2]
//        |        [(.matches ^String ?c2 ?cValue)]]
//      """.stripMargin, conn.db, datomic.Util.list(
//        datomic.Util.list("en", ".*Hi.*"),
//        datomic.Util.list("en", ".*He.*")
//      )) === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )