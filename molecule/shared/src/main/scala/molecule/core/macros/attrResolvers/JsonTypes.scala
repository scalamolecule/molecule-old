package molecule.core.macros.attrResolvers

import java.lang.{Long => jLong}
import java.util.{Date, List => jList, Map => jMap, Set => jSet}


private[molecule] trait JsonTypes extends JsonBase {

  // One ===========================================================================================

  protected def jsonOneQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    quotedPair(sb, field, row.get(colIndex).toString)
  }

  protected def jsonOne(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    pair(sb, field, row.get(colIndex))
  }

  protected def jsonOneToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    pair(sb, field, row.get(colIndex).toString)
  }

  protected def jsonOneDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    quotedPair(sb, field, date2str(row.get(colIndex).asInstanceOf[Date]))
  }

  protected def jsonOneAny(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = row.get(colIndex) match {
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


  // Many ===========================================================================================

  protected def jsonManyQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonMany(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonManyToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonManyDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(",") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(it.next.asInstanceOf[Date]))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }


  // Optional card one ===========================================================================================

  protected def jsonOptOneQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString)
    }
  }

  protected def jsonOptOne(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      pair(sb, field, value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next)
    }
  }

  protected def jsonOptOneToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      pair(sb, field, value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString)
    }
  }

  protected def jsonOptOneDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, date2str(value.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Date]))
    }
  }

  // ----------------------------------------------

  protected def jsonOptApplyOneQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, value.toString)
    }
  }

  protected def jsonOptApplyOne(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      pair(sb, field, value)
    }
  }

  protected def jsonOptApplyOneToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      pair(sb, field, value.toString)
    }
  }

  protected def jsonOptApplyOneDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    val value = row.get(colIndex)
    if (value == null) {
      pair(sb, field, "null")
    } else {
      quotedPair(sb, field, date2str(value.asInstanceOf[Date]))
    }
  }


  // Optional card many ===========================================================================================

  protected def jsonOptManyQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptMany(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptManyToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptManyDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  // ------------------------------


  protected def jsonOptApplyManyQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptApplyMany(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptApplyManyToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptApplyManyDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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


  // Optional ref attr ===========================================================================================

  protected def jsonOptOneRefAttr(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
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

  protected def jsonOptManyRefAttr(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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


  // Enum opt ===========================================================================================

  protected def jsonOptOneEnum(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
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

  protected def jsonOptManyEnum(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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


  // Map ===========================================================================================

  protected def jsonMapQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonMap(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptMapQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptMap(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptApplyMapQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonOptApplyMap(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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
