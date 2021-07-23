package molecule.core.macros.attrResolvers

import java.lang.{Long => jLong}
import java.util.{Date, Iterator => jIterator, List => jList, Map => jMap}


private[molecule] trait JsonOptNested extends JsonBase {

  // One ===========================================================================================

  protected def jsonOptNestedOneQuoted(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    quotedPair(sb, field, it.next.toString)
//    if(it.hasNext)
//      quotedPair(sb, field, it.next.toString)
//    else
//      throw new RuntimeException("unexpected empty iterator (jsonOptNestedOneQuoted)")
  }

  protected def jsonOptNestedOne(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    pair(sb, field, it.next)
//    if(it.hasNext)
//      pair(sb, field, it.next)
//    else
//      throw new RuntimeException("unexpected empty iterator (jsonOptNestedOne)")
  }

  protected def jsonOptNestedOneToString(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    pair(sb, field, it.next.toString)
  }

  protected def jsonOptNestedOneDate(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    quotedPair(sb, field, date2str(it.next.asInstanceOf[Date]))
  }

  protected def jsonOptNestedOneAny(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = it.next match {
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

  protected def jsonOptNestedOneEnum(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    val enum = getKwName(it.next.asInstanceOf[jMap[_, _]].values().iterator().next.toString)
    quotedPair(sb, field, enum)
  }

  protected def jsonOptNestedOneRefAttr(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    val refAttr = it.next
      .asInstanceOf[jMap[_, _]].values().iterator().next
      .asInstanceOf[jLong].toLong
    pair(sb, field, refAttr)
//    pair(sb, field, it.next.asInstanceOf[jLong].toLong)
  }


  // Many ===========================================================================================

  protected def jsonOptNestedManyQuoted(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedMany(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedManyToString(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedManyDate(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedManyEnum(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedManyRefAttr(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedOptOneQuoted(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, v.toString)
    }
  }

  protected def jsonOptNestedOptOne(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => pair(sb, field, v)
    }
  }

  protected def jsonOptNestedOptOneToString(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => pair(sb, field, v.toString)
    }
  }

  protected def jsonOptNestedOptOneDate(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, date2str(v.asInstanceOf[Date]))
    }
  }

  protected def jsonOptNestedOptOneEnum(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
    it.next match {
      case "__none__" => pair(sb, field, "null")
      case v          => quotedPair(sb, field, getKwName(v.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
    }
  }

  protected def jsonOptNestedOptOneRefAttr(sb: StringBuilder, field: String, it: jIterator[_]): StringBuilder = {
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

  protected def jsonOptNestedOptManyQuoted(sb: StringBuilder, field: String, it0: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedOptMany(sb: StringBuilder, field: String, it0: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedOptManyToString(sb: StringBuilder, field: String, it0: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedOptManyDate(sb: StringBuilder, field: String, it0: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedOptManyRefAttr(sb: StringBuilder, field: String, it0: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedOptManyEnum(sb: StringBuilder, field: String, it0: jIterator[_], tabs: Int): StringBuilder = {
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



  // Map ===========================================================================================

  protected def jsonOptNestedMapQuoted(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedMap(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedOptMapQuoted(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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

  protected def jsonOptNestedOptMap(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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
//  protected def jsonOptApplyMapQuoted(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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
//  protected def jsonOptApplyMap(sb: StringBuilder, field: String, it: jIterator[_], tabs: Int): StringBuilder = {
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
