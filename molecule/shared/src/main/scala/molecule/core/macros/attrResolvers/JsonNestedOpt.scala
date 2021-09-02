package molecule.core.macros.attrResolvers

import java.lang.{Long => jLong}
import java.util.{Date, Iterator => jIterator, List => jList, Map => jMap}


private[molecule] trait JsonNestedOpt extends JsonBase {

  // One ===========================================================================================

  protected def jsonNestedOptOneQuoted(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer =
    quotedPair(sb, field, it.next.toString)

  protected def jsonNestedOptOne(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer =
    pair(sb, field, it.next)

  protected def jsonNestedOptOneToString(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer =
    pair(sb, field, it.next.toString)

  protected def jsonNestedOptOneDate(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer =
    quotedPair(sb, field, date2str(it.next.asInstanceOf[Date]))

  protected def jsonNestedOptOneAny(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = it.next match {
    case value: String         => quotedPair(sb, field, value)
    case value: Int            => pair(sb, field, value)
    case value: Float          => pair(sb, field, value)
    case value: Boolean        => pair(sb, field, value)
    case value: Long           => pair(sb, field, value)
    case value: Double         => pair(sb, field, value)
    case value: java.util.Date => quotedPair(sb, field, date2str(value))
    case value: java.util.UUID => quotedPair(sb, field, value.toString)
    case value: java.net.URI   => quotedPair(sb, field, value.toString)
    case value: BigInt         => pair(sb, field, value)
    case value: BigDecimal     => pair(sb, field, value)
    case valueOfUnknownType    => quotedPair(sb, field, valueOfUnknownType.toString)
  }

  protected def jsonNestedOptOneEnum(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    val enum = getKwName(it.next.asInstanceOf[jMap[_, _]].values().iterator().next.toString)
    quotedPair(sb, field, enum)
  }

  protected def jsonNestedOptOneRefAttr(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    val refAttr = it.next
      .asInstanceOf[jMap[_, _]].values().iterator().next
      .asInstanceOf[jLong].toLong
    pair(sb, field, refAttr)
  }


  // Many ===========================================================================================

  protected def jsonNestedOptManyQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val vs         = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, vs.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonNestedOptMany(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val vs         = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(vs.next)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonNestedOptManyToString(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val vs         = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(vs.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonNestedOptManyDate(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val vs         = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(vs.next.asInstanceOf[Date]))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonNestedOptManyEnum(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val vs         = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, getKwName(vs.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonNestedOptManyRefAttr(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val vs         = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      if (next) sb.append(", ") else next = true
      val refAttr = vs.next
        .asInstanceOf[jMap[_, _]].values().iterator().next
        .asInstanceOf[jLong].toLong
      sb.append(refAttr)
    }
    sb.append("]")
  }


  // Optional card one ===========================================================================================

  protected def jsonNestedOptOptOneQuoted(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, v.toString)
    }
  }

  protected def jsonNestedOptOptOne(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => pair(sb, field, v)
    }
  }

  protected def jsonNestedOptOptOneToString(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => pair(sb, field, v.toString)
    }
  }

  protected def jsonNestedOptOptOneDate(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, date2str(v.asInstanceOf[Date]))
    }
  }

  protected def jsonNestedOptOptOneEnum(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, getKwName(v.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
    }
  }

  protected def jsonNestedOptOptOneRefAttr(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        val refAttr = v
          .asInstanceOf[jMap[_, _]].values.iterator.next
          .asInstanceOf[jLong].toLong
        pair(sb, field, refAttr)
    }
  }


  // Optional card many ===========================================================================================

  protected def jsonNestedOptOptManyQuoted(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
    it0.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val vs         = v.asInstanceOf[jList[_]].iterator
        while (vs.hasNext) {
          if (next) sb.append(", ") else next = true
          sb.append(indent(tabs + 1))
          quote(sb, vs.next.toString)
        }
        if (next) sb.append(indent(tabs))
        sb.append("]")
    }
  }

  protected def jsonNestedOptOptMany(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
    it0.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val vs         = v.asInstanceOf[jList[_]].iterator
        while (vs.hasNext) {
          if (next) sb.append(", ") else next = true
          sb.append(indent(tabs + 1))
          sb.append(vs.next)
        }
        if (next) sb.append(indent(tabs))
        sb.append("]")
    }
  }

  protected def jsonNestedOptOptManyToString(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
    it0.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val vs         = v.asInstanceOf[jList[_]].iterator
        while (vs.hasNext) {
          if (next) sb.append(", ") else next = true
          sb.append(indent(tabs + 1))
          sb.append(vs.next.toString)
        }
        if (next) sb.append(indent(tabs))
        sb.append("]")
    }
  }

  protected def jsonNestedOptOptManyDate(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
    it0.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val vs         = v.asInstanceOf[jList[_]].iterator
        while (vs.hasNext) {
          if (next) sb.append(", ") else next = true
          sb.append(indent(tabs + 1))
          quote(sb, date2str(vs.next.asInstanceOf[Date]))
        }
        if (next) sb.append(indent(tabs))
        sb.append("]")
    }
  }

  protected def jsonNestedOptOptManyEnum(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
    it0.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val vs         = v.asInstanceOf[jList[_]].iterator
        while (vs.hasNext) {
          if (next) sb.append(", ") else next = true
          sb.append(indent(tabs + 1))
          quote(sb, getKwName(vs.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
        }
        if (next) sb.append(indent(tabs))
        sb.append("]")
    }
  }

  protected def jsonNestedOptOptManyRefAttr(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
    it0.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val vs         = v.asInstanceOf[jList[_]].iterator
        while (vs.hasNext) {
          if (next) sb.append(", ") else next = true
          sb.append(indent(tabs + 1))
          val refAttr = vs.next
            .asInstanceOf[jMap[_, _]].values().iterator().next
            .asInstanceOf[jLong].toLong
          sb.append(refAttr)
        }
        if (next) sb.append(indent(tabs))
        sb.append("]")
    }
  }



  // Map ===========================================================================================

  protected def jsonNestedOptMapQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": {")
    var next = false
    val vs         = it.next.asInstanceOf[jList[_]].iterator
    var pair       = new Array[String](2)
    while (vs.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      pair = vs.next.toString.split("@", 2)
      quote(sb, pair(0))
      sb.append(": ")
      quote(sb, pair(1))
    }
    if (next) sb.append(indent(tabs))
    sb.append("}")
  }

  protected def jsonNestedOptMap(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": {")
    var next = false
    val vs         = it.next.asInstanceOf[jList[_]].iterator
    var pair       = new Array[String](2)
    while (vs.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      pair = vs.next.toString.split("@", 2)
      quote(sb, pair(0))
      sb.append(": ")
      sb.append(pair(1))
    }
    if (next) sb.append(indent(tabs))
    sb.append("}")
  }


  // Optional Map ===========================================================================================

  protected def jsonNestedOptOptMapQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it         = v.asInstanceOf[jList[_]].iterator
        var pair       = new Array[String](2)
        while (it.hasNext) {
          if (next) sb.append(", ") else next = true
          sb.append(indent(tabs + 1))
          pair = it.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          quote(sb, pair(1))
        }
        if (next) sb.append(indent(tabs))
        sb.append("}")
    }
  }

  protected def jsonNestedOptOptMap(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it         = v.asInstanceOf[jList[_]].iterator
        var pair       = new Array[String](2)
        while (it.hasNext) {
          if (next) sb.append(", ") else next = true
          sb.append(indent(tabs + 1))
          pair = it.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          sb.append(pair(1))
        }
        if (next) sb.append(indent(tabs))
        sb.append("}")
    }
  }

//  // Optional Map apply ===========================================================================================
//
//  protected def jsonOptApplyMapQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
//    it.next match {
//      case "__none__" => pair(sb, field, "null")
//      case v          =>
//        quote(sb, field)
//        sb.append(": {")
//        var next = false
//        val it         = v.asInstanceOf[jList[_]].iterator
//        var pair       = new Array[String](2)
//        while (it.hasNext) {
//          if (next) sb.append(", ") else next = true
//          sb.append(indent(tabs + 1))
//          pair = it.next.toString.split("@", 2)
//          quote(sb, pair(0))
//          sb.append(": ")
//          quote(sb, pair(1))
//        }
//        if (next) sb.append(indent(tabs))
//        sb.append("}")
//    }
//  }
//
//  protected def jsonOptApplyMap(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
//    it.next match {
//      case "__none__" => pair(sb, field, "null")
//      case v          =>
//        quote(sb, field)
//        sb.append(": {")
//        var next = false
//        val it         = v.asInstanceOf[jList[_]].iterator
//        var pair       = new Array[String](2)
//        while (it.hasNext) {
//          if (next) sb.append(", ") else next = true
//          sb.append(indent(tabs + 1))
//          pair = it.next.toString.split("@", 2)
//          quote(sb, pair(0))
//          sb.append(": ")
//          sb.append(pair(1))
//        }
//        if (next) sb.append(indent(tabs))
//        sb.append("}")
//    }
//  }
}
