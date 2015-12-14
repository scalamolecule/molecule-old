package molecule

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}

class AttributeMap extends CoreSpec {


  "Manipulate" in new CoreSetup {

    // Insert
    val eid: Long = Ns.strMap.insert(Map("en" -> "Hi")).eid
    Ns.strMap.one === Map("en" -> "Hi")

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


  "Types" in new CoreSetup {
    Ns.strMap.insert(Map("en" -> "Hi")).eid
    Ns.strMap.one === Map("en" -> "Hi")

    Ns.intMap.insert(Map("Meaning of life" -> 42)).eid
    Ns.intMap.one === Map("Meaning of life" -> 42)

    Ns.longMap.insert(Map("pluto" -> 123456789012345L)).eid
    Ns.longMap.one === Map("pluto" -> 123456789012345L)

    Ns.floatMap.insert(Map("USD" -> 35.50f)).eid
    Ns.floatMap.one === Map("USD" -> 35.50f)

    Ns.doubleMap.insert(Map("Apple" -> 435.3547)).eid
    Ns.doubleMap.one === Map("Apple" -> 435.3547)

    Ns.boolMap.insert(Map("Sanders" -> true)).eid
    Ns.boolMap.one === Map("Sanders" -> true)

    Ns.dateMap.insert(Map("today" -> date1)).eid
    Ns.dateMap.one === Map("today" -> date1)

    Ns.uuidMap.insert(Map("id1" -> uuid1)).eid
    Ns.uuidMap.one === Map("id1" -> uuid1)

    Ns.uriMap.insert(Map("uri1" -> uri1)).eid
    Ns.uriMap.one === Map("uri1" -> uri1)
  }


  "Key" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej")),
      (5, Map[String, String]())
    )

    // With mapped values
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


  "Key (optional)" in new CoreSetup {

    Ns.int.strMap$ insert List(
      (1, Some(Map("en" -> "Hi there"))),
      (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
      (3, Some(Map("en" -> "Hello"))),
      (4, Some(Map("da" -> "Hej"))),
      (5, None)
    )

    // All (5th entity has no strMap)
    Ns.int.strMap$.get === List(
      (1, Some(Map("en" -> "Hi there"))),
      (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
      (3, Some(Map("en" -> "Hello"))),
      (4, Some(Map("da" -> "Hej"))),
      (5, None)
    )

    // Values with "en" key
    // We can't apply values to optional values (like `Ns.int.strMap$("en")`)
    // Instead we process the results:
    Ns.int.strMap$.get.map { case (i, s) => (i, s.flatMap(_.get("en"))) } === List(
      (1, Some("Hi there")),
      (2, Some("Oh, Hi")),
      (3, Some("Hello")),
      (4, None),
      (5, None)
    )

    // Or we can use implicit functions for convenience
    Ns.int.strMap$.get.map { case (i, s) => (i, s.at("en")) } === List(
      (1, Some("Hi there")),
      (2, Some("Oh, Hi")),
      (3, Some("Hello")),
      (4, None),
      (5, None)
    )

    // Other key
    Ns.int.strMap$.get.map { case (i, s) => (i, s.at("fr")) } === List(
      (1, None),
      (2, Some("Bonjour")),
      (3, None),
      (4, None),
      (5, None)
    )

    // Get filtered map with certain keys
    Ns.int.strMap$.get.map { case (i, s) => (i, s.keys("fr", "da")) } === List(
      (1, Map()),
      (2, Map("fr" -> "Bonjour")),
      (3, Map()),
      (4, Map("da" -> "Hej")),
      (5, Map())
    )
  }


  "Key (tacet)" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej")),
      (5, Map[String, String]())
    )

    // Int values of entities with mapped values
    Ns.int.strMap_.get === List(1, 2, 3, 4)

    // Int values of entities with an english value
    Ns.int.strMap_("en").get === List(1, 2, 3)

    // Int values of entities with an english or french value
    Ns.int.strMap_("fr", "da").get === List(2, 4)
  }


  "Value" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej")),
      (5, Map[String, String]())
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


  "Values (optional)" in new CoreSetup {

    Ns.int.strMap$ insert List(
      (1, Some(Map("en" -> "Hi there"))),
      (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
      (3, Some(Map("en" -> "Hello"))),
      (4, Some(Map("da" -> "Hej"))),
      (5, None)
    )

    // Optional maps with values containing "Hi"
    Ns.int.strMap$.get.map { case (i, s) => (i, s.find("Hi")) } === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi")),
      (3, Map()),
      (4, Map()),
      (5, Map())
    )

    // Search value is Case insensitive ("there" is included)
    Ns.int.strMap$.get.map { case (i, s) => (i, s.find("He")) } === List(
      (1, Map("en" -> "Hi there")),
      (2, Map()),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej")),
      (5, Map())
    )

    // Search combination of key and value (Danish now excluded). A key -> value pair has AND semantics:
    // key("en") AND value("He")
    Ns.int.strMap$.get.map { case (i, s) => (i, s.find("en" -> "He")) } === List(
      (1, Map("en" -> "Hi there")),
      (2, Map()),
      (3, Map("en" -> "Hello")),
      (4, Map()),
      (5, Map())
    )

    // Search multiple values. Multiple values have OR semantics:
    // value("hi" OR "he")
    Ns.int.strMap$.get.map { case (i, s) => (i, s.find("hi", "he")) } === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej")),
      (5, Map())
    )

    // Search combination of key and multiple values. AND and OR semantics combined:
    // key("en") AND value("he" OR "hi")
    Ns.int.strMap$.get.map { case (i, s) => (i, s.find("en" -> Seq("he", "hi"))) } === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map()),
      (5, Map())
    )

    // Search combination of multiple keys and values. OR semantics between key/value pairs:
    // (key("en") AND value("he" OR "hi)) OR
    // (key("fr") AND value("bo"))
    Ns.int.strMap$.get.map { case (i, s) => (i, s.find("en" -> Seq("he", "hi"), "fr" -> Seq("bo"))) } === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map()),
      (5, Map())
    )
  }


  "Value (tacet)" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej")),
      (5, Map[String, String]())
    )

    // One key, one value
    Ns.int.strMap_("en" -> "Hi").get === List(1, 2)

    // One key, multiple values (OR semantics)
    Ns.int.strMap_("en" -> "Hi", "en" -> "He").get === List(1, 2, 3)

    // Multiple keys
    Ns.int.strMap_("en" -> "Hi", "fr" -> "Bon").get === List(1, 2)

    // Multiple keys with multiple values for some keys (OR semantics)
    Ns.int.strMap_("en" -> "Hi", "en" -> "He", "fr" -> "Bon").get === List(1, 2, 3)

    // All keys
    Ns.int.strMap_("_" -> "He").get === List(3, 4)

    // All keys with multiple values (OR semantics)
    Ns.int.strMap_("_" -> "He", "_" -> "Bon").get === List(2, 3, 4)
  }


  "Variable" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej")),
      (5, Map[String, String]())
    )
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
      (4, Map("da" -> "Hej")),
      (5, Map[String, String]())
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


  "Input (tacet)" in new CoreSetup {

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello")),
      (4, Map("da" -> "Hej")),
      (5, Map[String, String]())
    )
    // One key, one value
    m(Ns.int.strMap_(?))(Map("en" -> "Hi")).get === List(1, 2)


    // One key, multiple values (using OR regex)
    m(Ns.int.strMap_(?))(Map("en" -> "Hi|He")).get === List(1, 2, 3)

    // Multiple keys
    m(Ns.int.strMap_(?))(Map("en" -> "Hi", "fr" -> "Bon")).get === List(1, 2)

    // Multiple keys with multiple values for some keys (using OR regex)
    m(Ns.int.strMap_(?))(Map("en" -> "Hi|He", "fr" -> "Bon")).get === List(1, 2, 3)

    // All keys
    m(Ns.int.strMap_(?))(Map("_" -> "He")).get === List(3, 4)

    // All keys with multiple values (using OR regex)
    m(Ns.int.strMap_(?))(Map("_" -> "He|Bon")).get === List(2, 3, 4)
  }
}