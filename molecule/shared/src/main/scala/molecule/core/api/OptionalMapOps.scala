package molecule.core.api

/** Container of implicit for optional Map operations.
 *
 * @groupname optMapOps Number aggregation keywords
 * @groupdesc optMapOps Keywords applied to number attributes that return aggregated value(s).
 * @groupprio optMapOps 45
 */
trait OptionalMapOps {

  /** Implicit convenience ops on `Option[Map[String, T]]`
   *
   * @group entityOps
   */
  implicit class OptMapAttr2MapOps[T](mapAttr: Option[Map[String, T]]) {


    /** Extract optional value at key.
     * {{{
     * for {
     *   _ <- Ns.int.strMap$ insert List(
     *     (1, Some(Map("en" -> "Hi there"))),
     *     (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
     *     (3, Some(Map("da" -> "Hejsa")))
     *   )
     *
     *   // Value from key with flatMap and get
     *   _ <- Ns.int.strMap$.get.map(_.map { case (i, s) => (i, s.flatMap(_.get("en"))) } ==> List(
     *     (1, Some("Hi there")),
     *     (2, Some("Oh, Hi")),
     *     (3, None)
     *   ))
     *
     *   // Value `at` key convenience method
     *   _ <- Ns.int.strMap$.get.map(_.map { case (i, s) => (i, s.at("en")) } ==> List(
     *     (1, Some("Hi there")),
     *     (2, Some("Oh, Hi")),
     *     (3, None)
     *   ))
     * } yield ()
     * }}}
     *
     * @param key Key of key/value pairs to look for
     * @return Optional value
     */
    def at(key: String): Option[T] = mapAttr.flatMap(_.get(key))


    /** Extract value at key or a default value.
     * {{{
     * for {
     *   _ <- Ns.int.strMap$ insert List(
     *     (1, Some(Map("en" -> "Hi there"))),
     *     (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
     *     (3, Some(Map("da" -> "Hejsa")))
     *   ))
     *
     *   // Value `at` key or default value
     *   _ <- Ns.int.strMap$.get.map(_.map { case (i, s) => (i, s.atOrElse("en", "Default")) } ==> List(
     *     (1, "Hi there"),
     *     (2, "Oh, Hi"),
     *     (3, "Default")
     *   ))
     * } yield ()
     * }}}
     *
     * @param key     Key to match
     * @param default Default value if key is not found
     * @return Value if present, otherwise default value supplied
     */
    def atOrElse(key: String, default: T): T = at(key).getOrElse(default)


    /** Maps filtered by keys.
     * {{{
     * for {
     *   _ <- Ns.int.strMap$ insert List(
     *     (1, Some(Map("en" -> "Hi there"))),
     *     (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
     *     (3, Some(Map("da" -> "Hejsa")))
     *   )
     *
     *   // Map values filtered by keys
     *   _ <- Ns.int.strMap$.get.map(_.map { case (i, s) => (i, s.mapAt("fr", "da")) } ==> List(
     *     (1, Map()),
     *     (2, Map("fr" -> "Bonjour")),
     *     (3, Map("da" -> "Hejsa"))
     *   ))
     * } yield ()
     * }}}
     *
     * @param key1 Key to match
     * @param keyN Optional further keys to match
     * @return Maps filtered with pairs matching keys
     */
    def mapAt(key1: String, keyN: String*): Map[String, T] = {
      val keys = key1 +: keyN
      mapAttr match {
        case None    => Map[String, T]()
        case Some(m) => m.toList.foldLeft(Map[String, T]()) {
          case (acc, (k, v)) if keys.contains(k) => acc + (k -> v)
          case (acc, _)                          => acc
        }
      }
    }


    /** Maps filtered by regexes on values.
     * {{{
     * for {
     *   _ <- Ns.int.strMap$ insert List(
     *     (1, Some(Map("en" -> "Hi there"))),
     *     (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
     *     (3, Some(Map("da" -> "Hejsa")))
     *   )
     *
     *   // Maps filtered by regex on values
     *   _ <- Ns.int.strMap$.get.map(_.map { case (i, s) => (i, s.values("jou")) } ==> List(
     *     (1, Map()),
     *     (2, Map("fr" -> "Bonjour")),
     *     (3, Map())
     *   ))
     *
     *   // Maps filtered by regexes on value
     *   _ <- Ns.int.strMap$.get.map(_.map { case (i, s) => (i, s.values("jou", "hej")) } ==> List(
     *     (1, Map()),
     *     (2, Map("fr" -> "Bonjour")),
     *     (3, Map("da" -> "Hejsa"))
     *   ))
     * } yield ()
     * }}}
     *
     * @param valueRegEx  Regex on values
     * @param valueRegExs Optional further regexes on values
     * @return Map with pairs having values matching regexes
     */
    def values(valueRegEx: T, valueRegExs: T*): Map[String, T] = {
      val needles = valueRegEx +: valueRegExs
      mapAttr match {
        case None    => Map[String, T]()
        case Some(m) => valueRegEx match {
          case _: String => {
            val pattern = s"(?i).*(${needles.mkString("|")}).*".r.pattern
            m.toList.foldLeft(Map[String, T]()) {
              case (acc, (k, v)) if pattern.matcher(v.toString).matches => acc + (k -> v)
              case (acc, _)                                             => acc
            }
          }
          case _         => m.toList.foldLeft(Map[String, T]()) {
            case (acc, (k, v)) if needles.contains(v) => acc + (k -> v)
            case (acc, _)                             => acc
          }
        }
      }
    }


    /** Maps filtered by pairs of (key -> value-regex).
     * {{{
     * for {
     *   _ <- Ns.int.strMap$ insert List(
     *     (1, Some(Map("en" -> "Hi there"))),
     *     (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
     *     (4, Some(Map("da" -> "Hejsa")))
     *   )
     *
     *   // Maps filtered by key and value
     *   _ <- Ns.int.strMap$.get.map(_.map { case (i, s) => (i, s.keyValue("en" -> "he")) } ==> List(
     *     (1, Map("en" -> "Hi there")),
     *     (2, Map()),
     *     (3, Map())
     *   ))
     * } yield ()
     * }}}
     *
     * @param keyValueRegExs One or more (key -> value-regex) pairs
     * @return Maps filtered by keys/regexes
     */
    def keyValue(keyValueRegExs: (String, T)*): Map[String, T] = {
      val subpatterns = keyValueRegExs.map { case (k, v) => s"$k.*$v.*" }.mkString("|")
      val pattern     = s"(?i)($subpatterns)".r.pattern
      mapAttr match {
        case None    => Map[String, T]()
        case Some(m) => m.toList.foldLeft(Map[String, T]()) {
          case (acc, (k, v)) if pattern.matcher(k + v).matches => acc + (k -> v)
          case (acc, _)                                        => acc
        }
      }
    }


    /** Maps filtered by pairs of (key -> Seq(value-regexes)).
     * {{{
     * for {
     *   _ <- Ns.int.strMap$ insert List(
     *     (1, Some(Map("en" -> "Hi there"))),
     *     (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
     *     (3, Some(Map("da" -> "Hejsa")))
     *   )
     *
     *   // Maps filtered by key and value
     *   _ <- Ns.int.strMap$.get.map(_.map { case (i, s) => (i, s.keyValues("en" -> Seq("he", "hi"))) } ==> List(
     *     (1, Map("en" -> "Hi there")),
     *     (2, Map("en" -> "Oh, Hi")),
     *     (3, Map())
     *   ))
     * } yield ()
     * }}}
     *
     * @param keyValuesRegEx  (key -> Seq(value-regex)) pair
     * @param keyValuesRegExs Optional further (key -> Seq(value-regex)) pairs
     * @return Map filtered by keys/regexes
     */
    def keyValues(keyValuesRegEx: (String, Seq[T]), keyValuesRegExs: (String, Seq[T])*): Map[String, T] = {
      val keyNeedles  = keyValuesRegEx +: keyValuesRegExs
      val subpatterns = keyNeedles.map { case (k, vs) => s"$k.*(${vs.mkString("|")}).*" }.mkString("|")
      val pattern     = s"(?i)($subpatterns)".r.pattern
      mapAttr match {
        case None    => Map[String, T]()
        case Some(m) => m.toList.foldLeft(Map[String, T]()) {
          case (acc, (k, v)) if pattern.matcher(k + v).matches => acc + (k -> v)
          case (acc, _)                                        => acc
        }
      }
    }
  }
}


/** Optional implicit operations for optional Map attributes
 *
 * Is not imported in the default Molecule api imports since they
 * are rather specialized. If needed, they can be made available with
 * the following aditional import:
 * {{{
 * import molecule.api.optionalMapOps._
 * import molecule.datomic.api._ // Standard api import with any arity
 * }}}
 * Since this is a rather specialized
 * */
object OptionalMapOps extends OptionalMapOps
