package molecule.core.marshalling.unpackAttr

import molecule.core.exceptions.MoleculeException
import molecule.core.macros.rowAttr.JsonBase
import molecule.core.util.Helpers

object String2json extends JsonBase with Helpers {

  private lazy val buf = new StringBuffer
  private var v        = ""
  private var first    = true


  lazy val unpackJsonOneString_ = (v0: String, vs: Iterator[String]) => {
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

  lazy val unpackJsonOneString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String]) =>
    quotedPair(sb, field, unpackJsonOneString_(v, vs))

  lazy val unpackJsonOne       = (sb: StringBuffer, field: String, v: String) => pair(sb, field, v)
  lazy val unpackJsonOneDate   = (sb: StringBuffer, field: String, v: String) => quotedPair(sb, field, truncateDateStr(v))
  lazy val unpackJsonOneQuoted = (sb: StringBuffer, field: String, v: String) => quotedPair(sb, field, v)
  lazy val unpackJsonOneAny    = (sb: StringBuffer, field: String, s: String, vs: Iterator[String]) => {
    val v = s.drop(10)
    s.take(10) match {
      case "String    " => unpackJsonOneString(sb, field, v, vs)
      case "Int       " => pair(sb, field, v)
      case "Long      " => pair(sb, field, v)
      case "ref       " => pair(sb, field, v)
      case "Double    " => pair(sb, field, v)
      case "Boolean   " => pair(sb, field, v)
      case "Date      " => quotedPair(sb, field, v)
      case "UUID      " => quotedPair(sb, field, v)
      case "URI       " => quotedPair(sb, field, v)
      case "BigInt    " => quotedPair(sb, field, v)
      case "BigDecimal" => quotedPair(sb, field, v)
      case "enum      " => quotedPair(sb, field, v) // always single line
      case x            => throw MoleculeException(s"Unexpected unpackJsonOneAny prefix `$x`.")
    }
  }


  lazy val unpackJsonOptOneString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String]) => {
    if (v == "◄") {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, unpackJsonOneString_(v, vs))
    }
  }

  lazy val unpackJsonOptOne = (sb: StringBuffer, field: String, v: String) =>
    if (v == "◄") pair(sb, field, "null") else unpackJsonOne(sb, field, v)

  lazy val unpackJsonOptOneDate = (sb: StringBuffer, field: String, v: String) =>
    if (v == "◄") pair(sb, field, "null") else unpackJsonOneDate(sb, field, v)

  lazy val unpackJsonOptOneQuoted = (sb: StringBuffer, field: String, v: String) =>
    if (v == "◄") pair(sb, field, "null") else unpackJsonOneQuoted(sb, field, v)


  def unpackJsonMany_(
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

  lazy val unpackJsonManyString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
    unpackJsonMany_(sb, field, v, vs, tabs,
      (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpackJsonOneString_(v, vs))
    )
  }

  lazy val unpackJsonMany = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  lazy val unpackJsonManyDate = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, truncateDateStr(v)))

  lazy val unpackJsonManyQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  lazy val unpackJsonOptManyString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
    if (v == "◄") {
      pair(sb, field, "null")
    } else {
      unpackJsonMany_(sb, field, v, vs, tabs,
        (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpackJsonOneString_(v, vs))
      )
    }
  }

  lazy val unpackJsonOptMany = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpackJsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  lazy val unpackJsonOptManyDate = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpackJsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, truncateDateStr(v)))

  lazy val unpackJsonOptManyQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpackJsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  def unpackJsonMap_(
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

  lazy val unpackJsonMapString =
    (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
      unpackJsonMap_(sb, field, v, vs, tabs,
        (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpackJsonOneString_(v, vs))
      )
    }

  lazy val unpackJsonMap = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonMap_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  lazy val unpackJsonMapQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonMap_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  lazy val unpackJsonOptMapString =
    (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
      if (v == "◄") {
        pair(sb, field, "null")
      } else {
        unpackJsonMap_(sb, field, v, vs, tabs,
          (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpackJsonOneString_(v, vs))
        )
      }
    }

  lazy val unpackJsonOptMap = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpackJsonMap_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  lazy val unpackJsonOptMapQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    if (v == "◄")
      pair(sb, field, "null")
    else
      unpackJsonMap_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  lazy val unpackJsonListString =
    (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
      unpackJsonMany_(sb, field, v, vs, tabs,
        (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpackJsonOneString_(v, vs))
      )
    }

  lazy val unpackJsonList = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  lazy val unpackJsonListQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonMany_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))


  def unpackJsonListSet_(
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

  lazy val unpackJsonListSetString = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) => {
    unpackJsonListSet_(sb, field, v, vs, tabs,
      (sb: StringBuffer, v: String, vs: Iterator[String]) => quote(sb, unpackJsonOneString_(v, vs))
    )
  }

  lazy val unpackJsonListSet = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonListSet_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => sb.append(v))

  lazy val unpackJsonListSetQuoted = (sb: StringBuffer, field: String, v: String, vs: Iterator[String], tabs: Int) =>
    unpackJsonListSet_(sb, field, v, vs, tabs, (sb: StringBuffer, v: String, _: Iterator[String]) => quote(sb, v))
}
