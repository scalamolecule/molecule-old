package molecule
package attrMap
import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}
import java.util.{Date, UUID}
import java.net.URI

class Base extends CoreSpec {

  class Setup extends CoreSetup {

    val en    = "en"
    val fr    = "fr"
    val it    = "it"
    val Hi    = "Hi"
    val He    = "He"
    val Bo    = "Bo"
    val regEx = "Hi|He"

    val i10 = 10
    val i20 = 20
    val i30 = 30

    Ns.int.strMap insert List(
      (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
      (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
      (3, Map("en" -> "Hello", "da" -> "Hejhej")),
      (4, Map("da" -> "Hej")),
      (5, Map[String, String]())
    )
    Ns.int.intMap insert List(
      (1, Map("en" -> 10, "da" -> 30)),
      (2, Map("en" -> 10, "da" -> 10, "fr" -> 20, "it" -> 30)),
      (3, Map("en" -> 30, "da" -> 30)),
      (4, Map("da" -> 30)),
      (5, Map[String, Int]())
    )
    Ns.int.longMap insert List(
      (1, Map("en" -> 10L)),
      (2, Map("fr" -> 20L, "en" -> 10L)),
      (3, Map("en" -> 30L)),
      (4, Map("da" -> 30L)),
      (5, Map[String, Long]())
    )
    Ns.int.floatMap insert List(
      (1, Map("en" -> 10f)),
      (2, Map("fr" -> 20f, "en" -> 10f)),
      (3, Map("en" -> 30f)),
      (4, Map("da" -> 30f)),
      (5, Map[String, Float]())
    )
    Ns.int.doubleMap insert List(
      (1, Map("en" -> 10.0)),
      (2, Map("fr" -> 20.0, "en" -> 10.0)),
      (3, Map("en" -> 30.0)),
      (4, Map("da" -> 30.0)),
      (5, Map[String, Double]())
    )
    Ns.int.boolMap insert List(
      (1, Map("en" -> true)),
      (2, Map("fr" -> false, "en" -> true)),
      (3, Map("en" -> false)),
      (4, Map("da" -> true)),
      (5, Map[String, Boolean]())
    )
    Ns.int.dateMap insert List(
      (1, Map("en" -> date1)),
      (2, Map("fr" -> date2, "en" -> date1)),
      (3, Map("en" -> date3)),
      (4, Map("da" -> date3)),
      (5, Map[String, Date]())
    )
    Ns.int.uuidMap insert List(
      (1, Map("en" -> uuid1)),
      (2, Map("fr" -> uuid2, "en" -> uuid1)),
      (3, Map("en" -> uuid3)),
      (4, Map("da" -> uuid3)),
      (5, Map[String, UUID]())
    )
    Ns.int.uriMap insert List(
      (1, Map("en" -> uri1)),
      (2, Map("fr" -> uri2, "en" -> uri1)),
      (3, Map("en" -> uri3)),
      (4, Map("da" -> uri3)),
      (5, Map[String, URI]())
    )
  }
}