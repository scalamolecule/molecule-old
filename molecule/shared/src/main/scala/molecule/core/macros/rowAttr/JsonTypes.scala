package molecule.core.macros.rowAttr

import java.lang.{Long => jLong}
import java.util.{Date, List => jList, Map => jMap, Set => jSet}


private[molecule] trait JsonTypes extends JsonBase {

  // One ===========================================================================================

  protected def jsonOneQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    quotedPair(sb, field, row.get(colIndex).toString)
  }

  protected def jsonOne(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    pair(sb, field, row.get(colIndex))
  }

  protected def jsonOneToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    pair(sb, field, row.get(colIndex).toString)
  }

  protected def jsonOneDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    quotedPair(sb, field, date2str(row.get(colIndex).asInstanceOf[Date]))
  }

  protected def jsonOneAny(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = row.get(colIndex) match {
    case value: String         => quotedPair(sb, field, value)
    case value: Int            => pair(sb, field, value)
    case value: Float          => pair(sb, field, value)
    case value: Boolean        => pair(sb, field, value)
    case value: Long           => pair(sb, field, value)
    case value: Double         => pair(sb, field, value)
    case value: java.util.Date => quotedPair(sb, field, date2str(value))
    case value: java.util.UUID => quotedPair(sb, field, value.toString)
    case value: java.net.URI   => quotedPair(sb, field, value.toString)
    case value: BigInt         => quotedPair(sb, field, value.toString)
    case value: BigDecimal     => quotedPair(sb, field, value.toString)
    case valueOfUnknownType    => quotedPair(sb, field, valueOfUnknownType.toString)
  }


  // Many ===========================================================================================

  protected def jsonManyQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, it.next.toString)
    }
//    if (next)
      sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonMany(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next)
    }
//    if (next)
      sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonManyToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next.toString)
    }
//    if (next)
      sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonManyDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(it.next.asInstanceOf[Date]))
    }
//    if (next)
      sb.append(indent(tabs))
    sb.append("]")
  }


  // Optional card one ===========================================================================================

  protected def jsonOptOneQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString)
    }
  }

  protected def jsonOptOne(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      pair(sb, field, value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next)
    }
  }

  protected def jsonOptOneToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      pair(sb, field, value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString)
    }
  }

  protected def jsonOptOneDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, date2str(value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Date]))
    }
  }

  protected def jsonOptOneEnum(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field,
        getKwName(value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
          .asInstanceOf[jMap[_, _]].values.iterator.next.toString)
      )
    }
  }

  protected def jsonOptOneRefAttr(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      var done = false
      // Hack to avoid looking up map by clojure Keyword - there must be a better way...
      value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.asInstanceOf[jMap[_, _]].forEach {
        case _ if done                        =>
        case (k, v) if k.toString == ":db/id" =>
          done = true
          pair(sb, field, v.asInstanceOf[jLong].toLong)
        case _                                =>
      }
      sb
    }
  }

  // ----------------------------------------------

  protected def jsonOptApplyOneQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, value.toString)
    }
  }

  protected def jsonOptApplyOne(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      pair(sb, field, value)
    }
  }

  protected def jsonOptApplyOneToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      pair(sb, field, value.toString)
    }
  }

  protected def jsonOptApplyOneDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, date2str(value.asInstanceOf[Date]))
    }
  }


  // Optional card many ===========================================================================================

  protected def jsonOptManyQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        quote(sb, it.next.toString)
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  protected def jsonOptMany(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        sb.append(it.next)
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  protected def jsonOptManyToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        sb.append(it.next.toString)
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  protected def jsonOptManyDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        quote(sb, date2str(it.next.asInstanceOf[Date]))
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  protected def jsonOptManyEnum(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        quote(sb, getKwName(it.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  protected def jsonOptManyRefAttr(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      // Hack to avoid looking up map by clojure Keyword (not available on JS platform)
      // - there must be a better way...
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        var done = false
        it.next.asInstanceOf[jMap[_, _]].forEach {
          case _ if done                        =>
          case (k, v) if k.toString == ":db/id" =>
            done = true
            sb.append(v.asInstanceOf[jLong].toLong)
          case _                                =>
        }
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  // ------------------------------

  protected def jsonOptApplyManyQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jSet[_]].iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        quote(sb, it.next.toString)
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  protected def jsonOptApplyMany(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jSet[_]].iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        sb.append(it.next)
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  protected def jsonOptApplyManyToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jSet[_]].iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        sb.append(it.next.toString)
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }

  protected def jsonOptApplyManyDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": [")
      var next = false
      val it         = value.asInstanceOf[jSet[_]].iterator
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
        sb.append(indent(tabs + 1))
        quote(sb, date2str(it.next.asInstanceOf[Date]))
      }
      if (next) sb.append(indent(tabs))
      sb.append("]")
    }
  }


  // Map ===========================================================================================

  protected def jsonMapQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": {")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var pair       = new Array[String](2)
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      pair = it.next.toString.split("@", 2)
      quote(sb, pair(0))
      sb.append(": ")
      quote(sb, pair(1))
    }
    if (next) sb.append(indent(tabs))
    sb.append("}")
  }

  protected def jsonMap(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": {")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var pair       = new Array[String](2)
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      pair = it.next.toString.split("@", 2)
      quote(sb, pair(0))
      sb.append(": ")
      sb.append(pair(1))
    }
    if (next) sb.append(indent(tabs))
    sb.append("}")
  }

  // Optional Map ===========================================================================================

  protected def jsonOptMapQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": {")
      var next = false
      val it         = value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var pair       = new Array[String](2)
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
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

  protected def jsonOptMap(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": {")
      var next = false
      val it         = value.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var pair       = new Array[String](2)
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
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

  // Optional Map apply ===========================================================================================

  protected def jsonOptApplyMapQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": {")
      var next = false
      val it         = value.asInstanceOf[jSet[_]].iterator
      var pair       = new Array[String](2)
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
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

  protected def jsonOptApplyMap(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quote(sb, field)
      sb.append(": {")
      var next = false
      val it         = value.asInstanceOf[jSet[_]].iterator
      var pair       = new Array[String](2)
      while (it.hasNext) {
        if (next) sb.append(",") else next = true
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
}
