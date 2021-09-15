package molecule.core.marshalling.unpackAttr

import java.net.URI
import java.util.UUID
import molecule.core.macros.rowAttr.JsonBase
import molecule.core.util.Helpers

trait String2json extends JsonBase with Helpers {

  private lazy val buf = new StringBuffer
  private var v        = ""
  private var first    = true


  protected lazy val unpack2jsonOneString_ = (v0: String, vs: Iterator[String]) => {
    buf.setLength(0)
    first = true
    v = v0
    do {
      if (first) {
        buf.append(v)
        first = false
      } else {
        buf.append("\n")
        buf.append(v)
      }
      v = vs.next()
    } while (v != "◄")
    buf.toString
  }

  protected lazy val unpack2jsonOneString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String]) =>
    quotedPair(sb, field, unpack2jsonOneString_(v, vs))

  protected lazy val unpack2jsonOne       = (sb: StringBuffer, field: String, v: String) => pair(sb, field, v)
  protected lazy val unpack2jsonOneDate   = (sb: StringBuffer, field: String, v: String) => quotedPair(sb, field, truncateDateStr(v))
  protected lazy val unpack2jsonOneQuoted = (sb: StringBuffer, field: String, v: String) => quotedPair(sb, field, v)
  protected lazy val unpack2jsonOneAny    = (sb: StringBuffer, field: String, s: String, vs: Iterator[String]) => {
    val v = s.drop(10)
    s.take(10) match {
      case "String    " => unpack2jsonOneString(sb, field, v, vs)
      case "Int       " => pair(sb, field, v)
      case "Long      " => pair(sb, field, v)
      case "Double    " => pair(sb, field, v)
      case "Boolean   " => pair(sb, field, v)
      case "Date      " => quotedPair(sb, field, v)
      case "UUID      " => quotedPair(sb, field, v)
      case "URI       " => quotedPair(sb, field, v)
      case "BigInt    " => pair(sb, field, v)
      case "BigDecimal" => pair(sb, field, v)
    }
  }


  protected lazy val unpack2jsonOptOneString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String]) => {
    if (v == "◄") {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, unpack2jsonOneString_(v, vs))
    }
  }

  protected lazy val unpack2jsonOptOne = (sb: StringBuffer, field: String, v: String) =>
    if (v == "◄") pair(sb, field, "null") else unpack2jsonOne(sb, field, v)

  protected lazy val unpack2jsonOptOneDate = (sb: StringBuffer, field: String, v: String) =>
    if (v == "◄") pair(sb, field, "null") else unpack2jsonOneDate(sb, field, v)

  protected lazy val unpack2jsonOptOneQuoted = (sb: StringBuffer, field: String, v: String) =>
    if (v == "◄") pair(sb, field, "null") else unpack2jsonOneQuoted(sb, field, v)


  def unpack2jsonMany_(
    sb: StringBuffer,
    field: String,
    v0: String,
    vs: Iterator[String],
    tabs: Int,
    transform: (StringBuffer, String, Iterator[String]) => StringBuffer
  ): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    v = v0
    var next = false
    do {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      transform(sb, v, vs)
      v = vs.next()
    } while (v != "◄")
    sb.append(indent(tabs))
    sb.append("]")
  }

  protected lazy val unpack2jsonManyString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
    unpack2jsonMany_(sb, field, v, vs, tabs,
      (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpack2jsonOneString_(v, vs))
    )
  }

  protected lazy val unpack2jsonMany = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  protected lazy val unpack2jsonManyDate = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, truncateDateStr(v)))

  protected lazy val unpack2jsonManyQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  protected lazy val unpack2jsonOptManyString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
    if (v == "◄") {
      pair(sb, field, "null")
    } else {
      unpack2jsonMany_(sb, field, v, vs, tabs,
        (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpack2jsonOneString_(v, vs))
      )
    }
  }

  protected lazy val unpack2jsonOptMany = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpack2jsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  protected lazy val unpack2jsonOptManyDate = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpack2jsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, truncateDateStr(v)))

  protected lazy val unpack2jsonOptManyQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpack2jsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  def unpack2jsonMap_(
    sb: StringBuffer,
    field: String,
    v0: String,
    vs: Iterator[String],
    tabs: Int,
    transform: (StringBuffer, String, Iterator[String]) => StringBuffer
  ): StringBuffer = {
    quote(sb, field)
    sb.append(": {")
    v = v0
    var next = false
    var pair = new Array[String](2)
    do {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      pair = v.split("@", 2)
      quote(sb, pair(0))
      sb.append(": ")
      transform(sb, pair(1), vs)
      v = vs.next()
    } while (v != "◄")
    sb.append(indent(tabs))
    sb.append("}")
  }

  protected lazy val unpack2jsonMapString =
    (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
      unpack2jsonMap_(sb, field, v, vs, tabs,
        (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpack2jsonOneString_(v, vs))
      )
    }

  protected lazy val unpack2jsonMap = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonMap_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  protected lazy val unpack2jsonMapQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonMap_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  protected lazy val unpack2jsonOptMapString =
    (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
      if (v == "◄") {
        pair(sb, field, "null")
      } else {
        unpack2jsonMap_(sb, field, v, vs, tabs,
          (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpack2jsonOneString_(v, vs))
        )
      }
    }

  protected lazy val unpack2jsonOptMap = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpack2jsonMap_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  protected lazy val unpack2jsonOptMapQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpack2jsonMap_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  protected lazy val unpack2jsonListString =
    (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
      unpack2jsonMany_(sb, field, v, vs, tabs,
        (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpack2jsonOneString_(v, vs))
      )
    }

  protected lazy val unpack2jsonList = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  protected lazy val unpack2jsonListQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  def unpack2jsonListSet_(
    sb: StringBuffer,
    field: String,
    v0: String,
    vs: Iterator[String],
    tabs: Int,
    transform: (StringBuffer, String, Iterator[String]) => StringBuffer
  ): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    v = v0
    var next = false
    do {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      transform(sb, v, vs)
      v = vs.next()
    } while (v != "◄")
    sb.append(indent(tabs))
    sb.append("]]")
  }

  protected lazy val unpack2jsonListSetString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
    unpack2jsonListSet_(sb, field, v, vs, tabs,
      (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpack2jsonOneString_(v, vs))
    )
  }

  protected lazy val unpack2jsonListSet = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonListSet_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  protected lazy val unpack2jsonListSetQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpack2jsonListSet_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))
}
