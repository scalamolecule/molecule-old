package molecule.core.marshalling

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.ast.elements._
import molecule.core.util.Helpers
import molecule.datomic.base.ast.transactionModel._

object Stmts2Edn extends Helpers {

  val buf      = new StringBuilder()
  var uriAttrs = Set.empty[String]

  def apply(stmts: Seq[Statement]): (String, Set[String]) = {
    buf.clear()
    var following = false
    stmts.foreach { stmt =>
      if (following)
        buf.append("\n")
      else
        following = true
      addStmt(stmt)
    }
    (s"[${buf.toString}]", uriAttrs)
  }

  def addStmt(stmt: Statement): Unit = stmt match {
    //    case Add(e, a, Values(Eq(Seq(enum)), Some(prefix)), _) =>
    //      buf.append(s"[:db/add ${eid(e)} $a $prefix$enum]")

    case Add(e, a, v, _) =>
      buf.append(s"[:db/add ${eid(e)} $a ")
      value(a, v)
      buf.append("]")

    case Retract(e, a, v, _) =>
      buf.append(s"[:db/retract ${eid(e)} $a ")
      value(a, v)
      buf.append("]")

    case RetractEntity(e) =>
      buf.append(s"[:db/retractEntity ${eid(e)}]")

    case Cas(e, a, oldV, v, _) =>
      buf.append(s"[:db.fn/cas ${eid(e)} $a ")
      value(a, oldV)
      buf.append(" ")
      value(a, v)
      buf.append("]")
  }

  def eid(e: Any): String = e match {
    case TempId(part, i) => s"#db/id[$part $i]"
    case "datomic.tx"    => "datomic.tx"
    case e               => s"$e"
  }

  def value(attr: String, value: Any): Unit = value match {
    case s: String                                 => quote(s)
    case TempId(part, i)                           => buf.append(s"#db/id[$part $i]")
    case v: Int                                    => buf.append(v.toString)
    case v: Float                                  => buf.append(v.toString)
    case v: Long                                   => buf.append(v.toString)
    case v: Double                                 => buf.append(v.toString)
    case v: Boolean                                => buf.append(v.toString)
    case v: Date                                   => buf.append("#inst \"" + date2datomicStr(v) + "\"")
    case v: UUID                                   => buf.append("#uuid \"" + v + "\"")
    case v: URI                                    => uriAttrs = uriAttrs + attr; buf.append(v.toString)
    case v: BigInt                                 => buf.append(v.toString + "N")
    case v: BigDecimal if v.toString.contains(".") => buf.append(v.toString + "M")
    case v: BigDecimal                             => buf.append(v.toString + ".0M")
    case Enum(prefix, enum)                        => buf.append(prefix + enum)
    case other                                     => throw new IllegalArgumentException(
      s"Unexpected value of type `${other.getClass}`: " + other
    )
  }

  protected def quote(s: String): Unit = {
    buf.append('"')
    appendEscapedString(s)
    buf.append('"')
  }

  // Shamelessly adopted from lift-json:
  // https://github.com/lift/framework/blob/db05d863c290c5fd1081a7632263433153fc9fe3/core/json/src/main/scala/net/liftweb/json/JsonAST.scala#L813-L883

  private val jsEscapeChars: Set[Char] =
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

  private def appendEscapedString(s: String): Unit = {
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
        buf.append(c)
      } else {
        buf.append(strReplacement)
      }
    }
  }
}
