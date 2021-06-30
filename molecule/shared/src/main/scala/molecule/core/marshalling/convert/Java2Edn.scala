//package molecule.core.marshalling.convert
//
//import java.util.{Date, List => jList}
//import molecule.core.util.Helpers
//import molecule.datomic.base.ast.transactionModel._
//import molecule.datomic.base.facade.Conn
//
//object Java2Edn extends Helpers {
//
//  val buf      = new StringBuilder()
//  var uriAttrs = Set.empty[String]
//  var attrMap  = Map.empty[String, (Int, String)]
//
//  def apply(rows: jList[jList[_]], conn: Conn): (String, Set[String]) = {
//    attrMap = conn.connProxy.attrMap
//
//    buf.clear()
//    var following = false
//    rows.forEach { row =>
//      if (following)
//        buf.append("\n ") // Indentation for next clauses to align all
//      else
//        following = true
//      addRow(row)
////      buf.append(row)
//    }
//    (s"[${buf.toString}]", uriAttrs)
//  }
//
//  def addRow(row: jList[_]): Unit = {
//    row.forEach{v =>
//
//    }
//
//    row match {
//      case Add(e, a, v, _) =>
//        buf.append(s"[:db/add ${eid(e)} $a ")
//        value(a, v)
//        buf.append("]")
//
//      case Retract(e, a, v, _) =>
//        buf.append(s"[:db/retract ${eid(e)} $a ")
//        value(a, v)
//        buf.append("]")
//
//      case RetractEntity(e) =>
//        buf.append(s"[:db/retractEntity ${eid(e)}]")
//
//      case Cas(e, a, oldV, v, _) =>
//        buf.append(s"[:db.fn/cas ${eid(e)} $a ")
//        value(a, oldV)
//        buf.append(" ")
//        value(a, v)
//        buf.append("]")
//    }
//  }
//
//  def eid(e: Any): String = e match {
//    case TempId(part, i) => s"#db/id[$part $i]"
//    case "datomic.tx"    => "datomic.tx"
//    case e               => s"$e"
//  }
//
//  def value(attr: String, v: Any): Unit = {
//    val s = v.toString
//    (attrMap(attr)._2, v) match {
//      case ("String", Enum(prefix, enum))          => buf.append(prefix + enum)
//      case ("String", _)                           => quote(s)
//      case ("Long", TempId(part, i))               => buf.append(s"#db/id[$part $i]")
//      case ("Int" | "Long" | "ref" | "Boolean", _) => buf.append(s)
//      case ("Double", _)                           => buf.append(s + (if (s.contains('.')) "" else ".0"))
//      case ("Date", d: Date)                       => buf.append("#inst \"" + date2datomicStr(d) + "\"")
//      case ("UUID", _)                             => buf.append("#uuid \"" + v + "\"")
//      case ("URI", _)                              => uriAttrs = uriAttrs + attr; buf.append(s)
//      case ("BigInt", _)                           => buf.append(s + "N")
//      case ("BigDecimal", _)                       => buf.append(s + (if (s.contains('.')) "M" else ".0M"))
//      case (tpe, _)                                => throw new IllegalArgumentException(
//        s"Unexpected $tpe value of type ${v.getClass}: " + v
//      )
//    }
//  }
//
//  protected def quote(s: String): Unit = {
//    buf.append('"')
//    appendEscapedString(s)
//    buf.append('"')
//  }
//
//  // Shamelessly copied from lift-json:
//  // https://github.com/lift/framework/blob/db05d863c290c5fd1081a7632263433153fc9fe3/core/json/src/main/scala/net/liftweb/json/JsonAST.scala#L813-L883
//
//  private val jsEscapeChars: Set[Char] =
//    List(
//      ('\u00ad', '\u00ad'),
//      ('\u0600', '\u0604'),
//      ('\u070f', '\u070f'),
//      ('\u17b4', '\u17b5'),
//      ('\u200c', '\u200f'),
//      ('\u2028', '\u202f'),
//      ('\u2060', '\u206f'),
//      ('\ufeff', '\ufeff'),
//      ('\ufff0', '\uffff')
//    ).foldLeft(Set.empty[Char]) {
//      case (set, (start, end)) =>
//        set ++ (start to end).toSet
//    }
//
//  private def appendEscapedString(s: String): Unit = {
//    s.foreach { c =>
//      val strReplacement = c match {
//        case '"'  => "\\\""
//        case '\\' => "\\\\"
//        case '\b' => "\\b"
//        case '\f' => "\\f"
//        case '\n' => "\\n"
//        case '\r' => "\\r"
//        case '\t' => "\\t"
//        // Set.contains will cause boxing of c to Character, try and avoid this
//        case c if (c >= '\u0000' && c < '\u0020') || jsEscapeChars.contains(c) =>
//          "\\u%04x".format(c: Int)
//
//        case _ => ""
//      }
//
//      // Use Char version of append if we can, as it's cheaper.
//      if (strReplacement.isEmpty) {
//        buf.append(c)
//      } else {
//        buf.append(strReplacement)
//      }
//    }
//  }
//}
