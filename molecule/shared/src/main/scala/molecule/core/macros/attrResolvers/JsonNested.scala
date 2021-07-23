package molecule.core.macros.attrResolvers

import java.lang.{Long => jLong}
import java.util.{Date, List => jList, Map => jMap, Set => jSet}


private[molecule] trait JsonNested extends JsonBase {

  // Opt One ===========================================================================================

  protected def jsonOptOneQuoted(sb: StringBuilder, value: Option[String]): StringBuilder = {
    value match {
      case Some(s) => quote(sb, s)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptOne(sb: StringBuilder, value: Option[Any]): StringBuilder = {
    value match {
      case Some(v) => sb.append(v)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptOneDate(sb: StringBuilder, value: Option[Date]): StringBuilder = {
    value match {
      case Some(d) => quote(sb, date2str(d))
      case None    => sb.append("null")
    }
  }


  // Set ===========================================================================================

  protected def jsonSetQuoted(sb: StringBuilder, set: Set[String], tabs: Int): StringBuilder = {
    sb.append("[")
    var next = false
    set.foreach { s =>
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, s)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonSet(sb: StringBuilder, set: Set[Any], tabs: Int): StringBuilder = {
    sb.append("[")
    var next = false
    set.foreach { v =>
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      sb.append(v)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonSetDate(sb: StringBuilder, set: Set[Date], tabs: Int): StringBuilder = {
    sb.append("[")
    var next = false
    set.foreach { d =>
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      sb.append(date2str(d))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }


  // Opt Set ===========================================================================================

  protected def jsonOptSetQuoted(sb: StringBuilder, value: Option[Set[String]], tabs: Int): StringBuilder = {
    value match {
      case Some(s) => jsonSetQuoted(sb, s, tabs)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptSet(sb: StringBuilder, value: Option[Set[Any]], tabs: Int): StringBuilder = {
    value match {
      case Some(v) => jsonSet(sb, v, tabs)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptSetDate(sb: StringBuilder, value: Option[Set[Date]], tabs: Int): StringBuilder = {
    value match {
      case Some(d) => jsonSetDate(sb, d, tabs)
      case None    => sb.append("null")
    }
  }


  // Map ===========================================================================================

  protected def jsonMapQuoted(sb: StringBuilder, set: Map[String, String], tabs: Int): StringBuilder = {
    sb.append("{")
    var next = false
    set.foreach { case (k, v) =>
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, k)
      sb.append(": ")
      quote(sb, v)
    }
    if (next) sb.append(indent(tabs))
    sb.append("}")
  }

  protected def jsonMap(sb: StringBuilder, set: Map[String, Any], tabs: Int): StringBuilder = {
    sb.append("{")
    var next = false
    set.foreach { case (k, v) =>
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, k)
      sb.append(": ")
      sb.append(v)
    }
    if (next) sb.append(indent(tabs))
    sb.append("}")
  }

  protected def jsonMapDate(sb: StringBuilder, set: Map[String, Date], tabs: Int): StringBuilder = {
    sb.append("{")
    var next = false
    set.foreach { case (k, d) =>
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, k)
      sb.append(": ")
      quote(sb, date2str(d))
    }
    if (next) sb.append(indent(tabs))
    sb.append("}")
  }


  // Opt Map ===========================================================================================

  protected def jsonOptMapQuoted(sb: StringBuilder, value: Option[Map[String, String]], tabs: Int): StringBuilder = {
    value match {
      case Some(s) => jsonMapQuoted(sb, s, tabs)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptMap(sb: StringBuilder, value: Option[Map[String, Any]], tabs: Int): StringBuilder = {
    value match {
      case Some(v) => jsonMap(sb, v, tabs)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptMapDate(sb: StringBuilder, value: Option[Map[String, Date]], tabs: Int): StringBuilder = {
    value match {
      case Some(d) => jsonMapDate(sb, d, tabs)
      case None    => sb.append("null")
    }
  }
}