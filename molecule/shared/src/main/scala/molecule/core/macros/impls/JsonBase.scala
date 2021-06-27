package molecule.core.macros.impls

import molecule.core.util.Helpers

/** Core molecule interface defining actions that can be called on molecules.
  *
  * Generally we could often have made higher-order methods taking a jsoning function for
  * variating cases. But we prioritize minimizing the macro-generated code as much as possible
  * to lower compile time overhead.
  */
private[molecule] trait JsonBase extends Helpers {


  // Shamelessly adopted from lift-json:
  // https://github.com/lift/framework/blob/db05d863c290c5fd1081a7632263433153fc9fe3/core/json/src/main/scala/net/liftweb/json/JsonAST.scala#L813-L883

  protected val jsEscapeChars: Set[Char] =
    List(
      ('\u00ad', '\u00ad'),
      ('\u0600', '\u0604'),
      ('\u070f', '\u070f'),
      ('\u17b4', '\u17b5'),
      ('\u200c', '\u200f'),
      ('\u2028', '\u202f'),
      ('\u2060', '\u206f'),
      ('\ufeff', '\ufeff'),
      ('\ufff0', '\uffff')
    ).foldLeft(Set[Char]()) {
      case (set, (start, end)) =>
        set ++ (start to end).toSet
    }

  protected def appendEscapedString(sb: StringBuilder, s: String): Unit = {
    s.foreach { c =>
      val strReplacement = c match {
        case '"'  => "\\\""
        case '\\' => "\\\\"
        case '\b' => "\\b"
        case '\f' => "\\f"
        case '\n' => "\\n"
        case '\r' => "\\r"
        case '\t' => "\\t"
        // Set.contains will cause boxing of c to Character, try and avoid this
        case c if (c >= '\u0000' && c < '\u0020') || jsEscapeChars.contains(c) =>
          "\\u%04x".format(c: Int)

        case _ => ""
      }

      // Use Char version of append if we can, as it's cheaper.
      if (strReplacement.isEmpty) {
        sb.append(c)
      } else {
        sb.append(strReplacement)
      }
    }
  }

  protected def quote(sb: StringBuilder, s: String): StringBuilder = {
    sb.append('"') //open quote
    appendEscapedString(sb, s)
    sb.append('"') //close quote
  }

  protected def quotedPair(sb: StringBuilder, field: String, value: String): StringBuilder = {
    quote(sb, field)
    sb.append(": ")
    quote(sb, value)
  }

  protected def pair(sb: StringBuilder, field: String, value: Any): StringBuilder = {
    quote(sb, field)
    sb.append(": ")
    sb.append(value)
  }
}
