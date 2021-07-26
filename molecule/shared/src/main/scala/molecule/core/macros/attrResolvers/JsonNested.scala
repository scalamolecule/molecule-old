package molecule.core.macros.attrResolvers

import java.lang.{Long => jLong}
import java.util.{Date, List => jList, Map => jMap, Set => jSet}


private[molecule] trait JsonNested extends JsonBase {

  // Opt One ===========================================================================================

  protected def jsonOptOneQuoted(sb: StringBuffer, value: Option[String]): StringBuffer = {
    value match {
      case Some(s) => quote(sb, s)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptOne(sb: StringBuffer, value: Option[Any]): StringBuffer = {
    value match {
      case Some(v) => sb.append(v)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptOneDate(sb: StringBuffer, value: Option[Date]): StringBuffer = {
    value match {
      case Some(d) => quote(sb, date2str(d))
      case None    => sb.append("null")
    }
  }


  // Set ===========================================================================================

  protected def jsonSetQuoted(sb: StringBuffer, set: Set[String], tabs: Int): StringBuffer = {
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

  protected def jsonSet(sb: StringBuffer, set: Set[Any], tabs: Int): StringBuffer = {
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

  protected def jsonSetDate(sb: StringBuffer, set: Set[Date], tabs: Int): StringBuffer = {
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

  protected def jsonOptSetQuoted(sb: StringBuffer, value: Option[Set[String]], tabs: Int): StringBuffer = {
    value match {
      case Some(s) => jsonSetQuoted(sb, s, tabs)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptSet(sb: StringBuffer, value: Option[Set[Any]], tabs: Int): StringBuffer = {
    value match {
      case Some(v) => jsonSet(sb, v, tabs)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptSetDate(sb: StringBuffer, value: Option[Set[Date]], tabs: Int): StringBuffer = {
    value match {
      case Some(d) => jsonSetDate(sb, d, tabs)
      case None    => sb.append("null")
    }
  }


  // Map ===========================================================================================

  protected def jsonMapQuoted(sb: StringBuffer, set: Map[String, String], tabs: Int): StringBuffer = {
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

  protected def jsonMap(sb: StringBuffer, set: Map[String, Any], tabs: Int): StringBuffer = {
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

  protected def jsonMapDate(sb: StringBuffer, set: Map[String, Date], tabs: Int): StringBuffer = {
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

  protected def jsonOptMapQuoted(sb: StringBuffer, value: Option[Map[String, String]], tabs: Int): StringBuffer = {
    value match {
      case Some(s) => jsonMapQuoted(sb, s, tabs)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptMap(sb: StringBuffer, value: Option[Map[String, Any]], tabs: Int): StringBuffer = {
    value match {
      case Some(v) => jsonMap(sb, v, tabs)
      case None    => sb.append("null")
    }
  }

  protected def jsonOptMapDate(sb: StringBuffer, value: Option[Map[String, Date]], tabs: Int): StringBuffer = {
    value match {
      case Some(d) => jsonMapDate(sb, d, tabs)
      case None    => sb.append("null")
    }
  }
}
