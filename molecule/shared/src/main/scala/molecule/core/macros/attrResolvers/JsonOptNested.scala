package molecule.core.macros.attrResolvers

import java.lang.{Long => jLong}
import java.util.{Date, Iterator => jIterator, List => jList, Map => jMap}


private[molecule] trait JsonOptNested extends JsonBase {

  // One ===========================================================================================

  protected def jsonOptNestedOneQuoted(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer =
    quotedPair(sb, field, it.next.toString)

  protected def jsonOptNestedOne(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer =
    pair(sb, field, it.next)

  protected def jsonOptNestedOneToString(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer =
    pair(sb, field, it.next.toString)

  protected def jsonOptNestedOneDate(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer =
    quotedPair(sb, field, date2str(it.next.asInstanceOf[Date]))

  protected def jsonOptNestedOneAny(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = it.next match {
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

  protected def jsonOptNestedOneEnum(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    val enum = getKwName(it.next.asInstanceOf[jMap[_, _]].values().iterator().next.toString)
    quotedPair(sb, field, enum)
  }

  protected def jsonOptNestedOneRefAttr(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    val refAttr = it.next
      .asInstanceOf[jMap[_, _]].values().iterator().next
      .asInstanceOf[jLong].toLong
    pair(sb, field, refAttr)
  }


  // Many ===========================================================================================

  protected def jsonOptNestedManyQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedMany(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedManyToString(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedManyDate(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedManyEnum(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedManyRefAttr(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedOptOneQuoted(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, v.toString)
    }
  }

  protected def jsonOptNestedOptOne(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => pair(sb, field, v)
    }
  }

  protected def jsonOptNestedOptOneToString(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => pair(sb, field, v.toString)
    }
  }

  protected def jsonOptNestedOptOneDate(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, date2str(v.asInstanceOf[Date]))
    }
  }

  protected def jsonOptNestedOptOneEnum(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, getKwName(v.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
    }
  }

  protected def jsonOptNestedOptOneRefAttr(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
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

  protected def jsonOptNestedOptManyQuoted(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedOptMany(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedOptManyToString(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedOptManyDate(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedOptManyEnum(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedOptManyRefAttr(sb: StringBuffer, field: String, it0: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedMapQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedMap(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedOptMapQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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

  protected def jsonOptNestedOptMap(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
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
