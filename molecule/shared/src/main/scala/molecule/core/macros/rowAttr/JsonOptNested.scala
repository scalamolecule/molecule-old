package molecule.core.macros.rowAttr

import java.lang.{Long => jLong}
import java.util.{Date, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}


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

  protected def jsonOptNestedOneAny(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
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
  }

  protected def jsonOptNestedOneEnum(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case s: String => quotedPair(sb, field, getKwName(s))
      case vs        => quotedPair(sb, field, getKwName(vs.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
    }
  }

  protected def jsonOptNestedOneRefAttr(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case l: jLong => pair(sb, field, l.toLong)
      case vs       =>
        val refAttr = vs
          .asInstanceOf[jMap[_, _]].values.iterator.next
          .asInstanceOf[jLong].toLong
        pair(sb, field, refAttr)
    }
  }


  // Many ===========================================================================================

  protected def jsonOptNestedManyQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case vs: jSet[_] =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedMany(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case vs: jSet[_] =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          sb.append(it1.next)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          sb.append(it1.next)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedManyToString(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case vs: jSet[_] =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          sb.append(it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          sb.append(it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedManyDate(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case vs: jSet[_] =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, date2str(it1.next.asInstanceOf[Date]))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, date2str(it1.next.asInstanceOf[Date]))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedManyEnum(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case vs: jSet[_] =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, getKwName(it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedManyRefAttr(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case vs: jSet[_] =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          val refAttr = it1.next
//            .asInstanceOf[jMap[_, _]].values.iterator.next
            .asInstanceOf[jLong].toLong
          sb.append(refAttr)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          val refAttr = it1.next
            .asInstanceOf[jMap[_, _]].values.iterator.next
            .asInstanceOf[jLong].toLong
          sb.append(refAttr)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }


  // Optional card one ===========================================================================================

  protected def jsonOptNestedOptOneQuoted(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case m: jMap[_, _]     => quotedPair(sb, field, m.values.iterator.next.toString)
      case v                 => quotedPair(sb, field, v.toString)
    }
  }

  protected def jsonOptNestedOptOne(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case m: jMap[_, _]     => pair(sb, field, m.values.iterator.next.toString)
      case v                 => pair(sb, field, v)
    }
  }

  protected def jsonOptNestedOptOneToString(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case m: jMap[_, _]     => pair(sb, field, m.values.iterator.next.toString)
      case v                 => pair(sb, field, v.toString)
    }
  }

  protected def jsonOptNestedOptOneDate(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case m: jMap[_, _]     => quotedPair(sb, field, date2str(m.values.iterator.next.asInstanceOf[Date]))
      case v                 => quotedPair(sb, field, date2str(v.asInstanceOf[Date]))
    }
  }

  protected def jsonOptNestedOptOneEnum(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case v                 => v.asInstanceOf[jMap[_, _]].values.iterator.next match {
        case m: jMap[_, _] => quotedPair(sb, field, getKwName(m.values.iterator.next.toString))
        case v             => quotedPair(sb, field, getKwName(v.toString))
      }
    }
  }

  protected def jsonOptNestedOptOneRefAttr(sb: StringBuffer, field: String, it: jIterator[_]): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      //      case m: jMap[_, _]     => quotedPair(sb, field, m.values.iterator.next.toString)
      case l: jLong => pair(sb, field, l)
      case v =>
        val refAttr = v
          .asInstanceOf[jMap[_, _]].values.iterator.next
          .asInstanceOf[jMap[_, _]].values.iterator.next
          .asInstanceOf[jLong].toLong
        pair(sb, field, refAttr)
    }
  }


  // Optional card many ===========================================================================================

  protected def jsonOptNestedOptManyQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case vs: jMap[_, _]    =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.values.iterator.next.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedOptMany(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case vs: jMap[_, _]    =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.values.iterator.next.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          sb.append(it1.next)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          sb.append(it1.next)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedOptManyToString(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case vs: jMap[_, _]    =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.values.iterator.next.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          sb.append(it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          sb.append(it1.next.toString)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedOptManyDate(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case vs: jMap[_, _]    =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.values.iterator.next.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, date2str(it1.next.asInstanceOf[Date]))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, date2str(it1.next.asInstanceOf[Date]))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedOptManyEnum(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case vs: jMap[_, _]    =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.values.iterator.next.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, getKwName(it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          quote(sb, getKwName(it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }

  protected def jsonOptNestedOptManyRefAttr(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case vs: jMap[_, _]    =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          val refAttr = it1.next
            .asInstanceOf[jMap[_, _]].values.iterator.next
            .asInstanceOf[jLong].toLong
          sb.append(refAttr)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")

      case vs =>
        quote(sb, field)
        sb.append(": [")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          val refAttr = it1.next
            .asInstanceOf[jMap[_, _]].values.iterator.next
            .asInstanceOf[jLong].toLong
          sb.append(refAttr)
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("]")
    }
  }


  // Map ===========================================================================================

  protected def jsonOptNestedMapQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case vs: jSet[_] =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it1  = vs.iterator
        var pair = new Array[String](2)
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          pair = it1.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          quote(sb, pair(1))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("}")

      case vs =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var pair = new Array[String](2)
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          pair = it1.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          quote(sb, pair(1))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("}")
    }
  }

  protected def jsonOptNestedMap(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case vs: jSet[_] =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it1  = vs.iterator
        var pair = new Array[String](2)
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          pair = it1.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          sb.append(pair(1))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("}")

      case vs =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var pair = new Array[String](2)
        while (it1.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          pair = it1.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          sb.append(pair(1))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("}")
    }
  }


  // Optional Map ===========================================================================================

  protected def jsonOptNestedOptMapQuoted(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case m: jMap[_, _]     =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it   = m.values.iterator.next.asInstanceOf[jList[_]].iterator
        var pair = new Array[String](2)
        while (it.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          pair = it.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          quote(sb, pair(1))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("}")

      case v =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it   = v.asInstanceOf[jList[_]].iterator
        var pair = new Array[String](2)
        while (it.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          pair = it.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          quote(sb, pair(1))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("}")
    }
  }

  protected def jsonOptNestedOptMap(sb: StringBuffer, field: String, it: jIterator[_], tabs: Int): StringBuffer = {
    it.next match {
      case "__none__" | null => pair(sb, field, "null")
      case m: jMap[_, _]     =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it   = m.values.iterator.next.asInstanceOf[jList[_]].iterator
        var pair = new Array[String](2)
        while (it.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          pair = it.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          sb.append(pair(1))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("}")

      case v =>
        quote(sb, field)
        sb.append(": {")
        var next = false
        val it   = v.asInstanceOf[jList[_]].iterator
        var pair = new Array[String](2)
        while (it.hasNext) {
          if (next) sb.append(",") else next = true
          sb.append(indent(tabs + 2))
          pair = it.next.toString.split("@", 2)
          quote(sb, pair(0))
          sb.append(": ")
          sb.append(pair(1))
        }
        if (next) sb.append(indent(tabs + 1))
        sb.append("}")
    }
  }
}
