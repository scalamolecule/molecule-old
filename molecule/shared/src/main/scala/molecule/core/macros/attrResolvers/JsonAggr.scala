package molecule.core.macros.attrResolvers

import java.util.{Date, List => jList, Set => jSet}

/** Core molecule interface defining actions that can be called on molecules.
  *
  * Generally we could often have made higher-order methods taking a jsoning function for
  * variating cases. But we prioritize minimizing the macro-generated code as much as possible
  * to lower compile time overhead.
  */
private[molecule] trait JsonAggr extends JsonBase {

  // One List --------------------------------------

  protected def jsonAggrOneListQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneList(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneListToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneListDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(it.next.asInstanceOf[Date]))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  // Many List --------------------------------------

  protected def jsonAggrManyListQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]]")
  }

  protected def jsonAggrManyList(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]]")
  }

  protected def jsonAggrManyListToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]]")
  }

  protected def jsonAggrManyListDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(it.next.asInstanceOf[Date]))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]]")
  }


  // One List Distinct --------------------------------------

  protected def jsonAggrOneListDistinctQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneListDistinct(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneListDistinctToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneListDistinctDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(it.next.asInstanceOf[Date]))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  // Many List Distinct --------------------------------------

  protected def jsonAggrManyListDistinctQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]]")
  }

  protected def jsonAggrManyListDistinct(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]]")
  }

  protected def jsonAggrManyListDistinctToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]]")
  }

  protected def jsonAggrManyListDistinctDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [[")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(it.next.asInstanceOf[Date]))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]]")
  }


  // One List Rand --------------------------------------

  protected def jsonAggrOneListRandQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneListRand(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneListRandToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrOneListRandDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(it.next.asInstanceOf[Date]))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  // Many List Rand --------------------------------------

  protected def jsonAggrManyListRandQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrManyListRand(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrManyListRandToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      sb.append(it.next.toString)
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }

  protected def jsonAggrManyListRandDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    var next = false
    val it         = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (it.hasNext) {
      if (next) sb.append(", ") else next = true
      sb.append(indent(tabs + 1))
      quote(sb, date2str(it.next.asInstanceOf[Date]))
    }
    if (next) sb.append(indent(tabs))
    sb.append("]")
  }


  // Single Sample --------------------------------------

  protected def jsonAggrSingleSampleQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    quotedPair(sb, field, row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected def jsonAggrSingleSample(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    pair(sb, field, row.get(colIndex).asInstanceOf[jList[_]].iterator.next)
  }

  protected def jsonAggrSingleSampleToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    pair(sb, field, row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected def jsonAggrSingleSampleDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    quotedPair(sb, field, date2str(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }

  // Many Single --------------------------------------

  protected def jsonAggrManySingleQuoted(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    quote(sb, row.get(colIndex).toString)
    sb.append("]")
  }

  protected def jsonAggrManySingle(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    sb.append(row.get(colIndex))
    sb.append("]")
  }

  protected def jsonAggrManySingleToString(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    sb.append(row.get(colIndex).toString)
    sb.append("]")
  }

  protected def jsonAggrManySingleDate(sb: StringBuffer, field: String, row: jList[_], colIndex: Int): StringBuffer = {
    quote(sb, field)
    sb.append(": [")
    quote(sb, date2str(row.get(colIndex).asInstanceOf[Date]))
    sb.append("]")
  }
}
