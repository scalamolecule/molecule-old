package molecule.core.pagination

import java.lang.{Long => jLong}
import java.util
import java.util.{Base64, List => jList}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import scala.concurrent.{ExecutionContext, Future}

trait CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  final protected def sortedRows2selectedRows(
    sortedRows: util.ArrayList[jList[AnyRef]],
    limit: Int,
    offset: Int
  ): (util.ArrayList[jList[AnyRef]], Int) = {
    var topRowIndex   = -1
    var curId : jLong = 0
    var prevId: jLong = 0
    if (limit == 0) {
      // All rows (offset is 0)
      sortedRows.forEach { row =>
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        prevId = curId
      }
      return (sortedRows, topRowIndex + 1)
    }

    val totalCount         = sortedRows.size
    val selectedRows       = new util.ArrayList[jList[AnyRef]]()
    var row: jList[AnyRef] = null
    var continue           = true

    if (limit > 0) {
      // From start ..........
      val until = offset + limit
      var i     = 0
      while (continue && i != totalCount) {
        row = sortedRows.get(i)
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        if (topRowIndex >= offset && topRowIndex < until) {
          // append rows from start
          selectedRows.add(row)
        } else if (topRowIndex == until) {
          continue = false
        }
        prevId = curId
        i += 1
      }
      (selectedRows, topRowIndex + 1)

    } else {
      // From end ..........
      var acc   = List.empty[jList[AnyRef]] // for backwards accumulation
      val until = offset - limit // (limit is negative)
      var i     = totalCount - 1
      while (continue && i != -1) {
        row = sortedRows.get(i)
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        if (topRowIndex >= offset && topRowIndex < until) {
          // prepend rows from end
          acc = row :: acc
        } else if (topRowIndex == until) {
          continue = false
        }
        prevId = curId
        i -= 1
      }
      // Make java list with selected reversed rows
      acc.foreach(row => selectedRows.add(row))
      (selectedRows, topRowIndex + 1)
    }
  }

  final protected def extractSortValues(row: jList[AnyRef]): List[String] = {
    sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").map(sc => row.get(sc.i).toString)
  }

  final protected def getOffsetModel(forward: Boolean, prevTokens: List[String], nextTokens: List[String]): Model = {
    def addLimit(e: GenericAtom): Seq[Element] = {
      val (asc, i)   = (e.sort.head == 'a', e.sort.last.toString.toInt - 1)
      val token      = if (forward) nextTokens(i) else prevTokens(i)
      val typedValue = e.tpe match {
        case "Int"    => token.toInt
        case "String" => token
        case other    => throw MoleculeException("Unexpected sort attribute type: " + other)
      }
      val limitExpr  = if (forward && asc || !forward && !asc) Ge(typedValue) else Le(typedValue)
      e match {
        case a: Atom    => Seq(a, a.copy(attr = a.attr + "_", value = limitExpr, sort = ""))
        case g: Generic => Seq(g, g.copy(attr = g.attr + "_", value = limitExpr, sort = ""))
      }
    }
    def resolve(elements: Seq[Element]): Seq[Element] = elements.flatMap {
      case e: GenericAtom if e.sort.nonEmpty => addLimit(e)
      case e: GenericAtom                    => Seq(e)
      case Composite(elements)               => Seq(Composite(resolve(elements)))
      case TxMetaData(elements)              => Seq(TxMetaData(resolve(elements)))
      case e                                 => Seq(e)
    }
    Model(resolve(_model.elements))
  }

  final protected def encode(
    firstRow: String,
    lastRow: String,
    firstSortValues: List[String],
    lastSortValues: List[String]
  ): String = {
    val cleanStrings = Seq(firstRow, lastRow) ++ firstSortValues ++ lastSortValues
    //    println(cleanStrings.mkString("  ", "\n  ", ""))
    Base64.getEncoder.encodeToString(cleanStrings.mkString("__~~__").getBytes)
  }

  //  final protected def decode(cursor: String): (Int, Int, String, String, List[String], List[String]) = {
  final protected def decode(cursor: String): (String, String, List[String], List[String]) = {
    //    println("XXX " + new String(Base64.getDecoder.decode(cursor)))
    val values              = new String(Base64.getDecoder.decode(cursor)).split("__~~__", -1).toList
    //    println(values.mkString("  ", "\n  ", ""))
    val (firstRow, lastRow) = (values.head, values(1))
    val size                = sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").size
    val firstSortValues     = values.slice(2, 2 + size)
    val lastSortValues      = values.takeRight(size)
    (firstRow, lastRow, firstSortValues, lastSortValues)
  }

  protected def lastIndex(sortedRows: util.ArrayList[jList[AnyRef]], prevLastRow: String, limit: Int) = {
    // Walk forward to find row index of previous last row
    var i = 0
    while (sortedRows.get(i).toString != prevLastRow && i != limit) {
      i += 1
    }
    i
  }

  protected def firstIndex(
    totalCount: Int,
    sortedRows: util.ArrayList[jList[AnyRef]],
    prevFirstRow: String,
    negLimit: Int
  ) = {
    // Walk backward to find row index of previous first row
    var i     = totalCount - 1
    val lower = i + negLimit // (limit is negative)
    while (sortedRows.get(i).toString != prevFirstRow && i != lower) {
      i -= 1
    }
    i
  }

  protected def extract(cursor: String, forward: Boolean): (Model, Query, String, String) = {
    if (cursor.isEmpty) {
      (_model, _query, "", "")
    } else {
      val (prevFirstRow, prevLastRow, prevFirstSortValues, prevLastSortValues) = decode(cursor)

      val offsetModel = getOffsetModel(forward, prevFirstSortValues, prevLastSortValues)
      (offsetModel, Model2Query(offsetModel).get._1, prevFirstRow, prevLastRow)
    }
  }
}
