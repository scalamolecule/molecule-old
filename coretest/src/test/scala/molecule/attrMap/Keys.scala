package molecule
package attrMap
import molecule.util.dsl.coreTest._

class Keys extends Base {


  "One key" in new Setup {

    Ns.int.strMap.k("en").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )

    Ns.int.intMap.k("en").get === List(
      (1, Map("en" -> 10)),
      (2, Map("en" -> 10)),
      (3, Map("en" -> 30))
    )

    // OBS: Since a map attribute returns a map, we have to beware that
    // key/value pairs are coalesced if there are no other attributes and
    // we end up with one random pair:
    Ns.strMap.k("en").get === List(
      Map("en" -> "Oh, Hi") // random pair...
    )

    // A workaround to get all the mapped values only is to add the `e`
    // entity attribute (which always has a value) and then filter it out
    // from the result set:
    Ns.e.strMap.k("en").get.map(_._2) === List(
      Map("en" -> "Hi there"),
      Map("en" -> "Oh, Hi"),
      Map("en" -> "Hello")
    )
    Ns.e.intMap.k("en").get.map(_._2) === List(
      Map("en" -> 10),
      Map("en" -> 10),
      Map("en" -> 30)
    )

    // Then we might as well return only the values
    Ns.e.strMap.k("en").get.map(_._2("en")) === List(
      "Hi there",
      "Oh, Hi",
      "Hello"
    )
    Ns.e.intMap.k("en").get.map(_._2("en")) === List(
      10,
      10,
      30
    )
  }


  "Multiple keys (OR semantics)" in new Setup {

    // OR semantics with comma-separated keys
    Ns.int.strMap.k("en", "fr").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )
    Ns.int.strMap.k("en", "fr").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )
    Ns.int.intMap.k("en", "fr").get === List(
      (1, Map("en" -> 10)),
      (2, Map("fr" -> 20, "en" -> 10)),
      (3, Map("en" -> 30))
    )

    val en_fr = List(
      (1, Map("en" -> 10)),
      (2, Map("fr" -> 20, "en" -> 10)),
      (3, Map("en" -> 30))
    )
    Ns.int.intMap.k(Seq("en", "fr")).get === en_fr

    // Variables
    val keys  = Seq("en", "fr")
    val keys2 = Seq(en, fr)
    Ns.int.intMap.k(Seq(en, fr)).get === en_fr
    Ns.int.intMap.k(keys).get === en_fr
    Ns.int.intMap.k(keys2).get === en_fr


    // OR semantics with `or`-separated keys

    Ns.int.strMap.k("en" or "fr").get === List(
      (1, Map("en" -> "Hi there")),
      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
      (3, Map("en" -> "Hello"))
    )
    Ns.int.intMap.k("en" or "fr").get === List(
      (1, Map("en" -> 10)),
      (2, Map("fr" -> 20, "en" -> 10)),
      (3, Map("en" -> 30))
    )
  }

  // AND semantics aren't relevant for attribute maps since we should
  // consider them as card-one containers of one "value with variation".
  // When we need to work with those variations we can narrow those
  // by OR semantics.
}