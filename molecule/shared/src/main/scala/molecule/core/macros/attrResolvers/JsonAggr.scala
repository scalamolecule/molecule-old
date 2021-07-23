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

  protected def jsonAggrOneListQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneList(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyList(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListDistinctQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListDistinct(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListDistinctToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListDistinctDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListDistinctQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListDistinct(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListDistinctToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListDistinctDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListRandQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListRand(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListRandToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrOneListRandDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListRandQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListRand(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListRandToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrManyListRandDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int, tabs: Int): StringBuilder = {
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

  protected def jsonAggrSingleSampleQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    quotedPair(sb, field, row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected def jsonAggrSingleSample(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    pair(sb, field, row.get(colIndex).asInstanceOf[jList[_]].iterator.next)
  }

  protected def jsonAggrSingleSampleToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    pair(sb, field, row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected def jsonAggrSingleSampleDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    quotedPair(sb, field, date2str(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }

  // Many Single --------------------------------------

  protected def jsonAggrManySingleQuoted(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    quote(sb, field)
    sb.append(": [")
    quote(sb, row.get(colIndex).toString)
    sb.append("]")
  }

  protected def jsonAggrManySingle(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    quote(sb, field)
    sb.append(": [")
    sb.append(row.get(colIndex))
    sb.append("]")
  }

  protected def jsonAggrManySingleToString(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    quote(sb, field)
    sb.append(": [")
    sb.append(row.get(colIndex).toString)
    sb.append("]")
  }

  protected def jsonAggrManySingleDate(sb: StringBuilder, field: String, row: jList[_], colIndex: Int): StringBuilder = {
    quote(sb, field)
    sb.append(": [")
    quote(sb, date2str(row.get(colIndex).asInstanceOf[Date]))
    sb.append("]")
  }
}
