package molecule.core.pagination

import java.lang.{Long => jLong}
import java.util
import java.util.{Base64, Collection => jCollection, List => jList}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import scala.concurrent.{ExecutionContext, Future}

trait CursorJson[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>


  final def sortedRows2selectedRowsJson(
    sortedRows: util.ArrayList[jList[AnyRef]],
    limit: Int,
    offset: Int
  ): (jCollection[jList[AnyRef]], Int) = {
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
      var acc   = List.empty[jList[AnyRef]]
      val until = offset - limit // (limit is negative)
      var i     = totalCount - 1
      while (continue && i != -1) {
        row = sortedRows.get(i)
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        if (topRowIndex >= offset && topRowIndex < until) {
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

//  // todo: let macro find value extraction indexes at compile time
//  private def getOffsetModel(forward: Boolean, prevTokens: List[String], nextTokens: List[String]): Model = {
//    def addLimit(e: GenericAtom): Seq[Element] = {
//      val (asc, i)   = (e.sort.head == 'a', e.sort.last.toString.toInt - 1)
//      val token      = if (forward) nextTokens(i) else prevTokens(i)
//      val typedValue = e.tpe match {
//        case "Int"    => token.toInt
//        case "String" => token
//        case other    => throw MoleculeException("Unexpected sort attribute type: " + other)
//      }
//      val limitExpr  = if (forward && asc || !forward && !asc) Ge(typedValue) else Le(typedValue)
//      e match {
//        case a: Atom    => Seq(a, a.copy(attr = a.attr + "_", value = limitExpr, sort = ""))
//        case g: Generic => Seq(g, g.copy(attr = g.attr + "_", value = limitExpr, sort = ""))
//      }
//    }
//    def resolve(elements: Seq[Element]): Seq[Element] = elements.flatMap {
//      case e: GenericAtom if e.sort.nonEmpty => addLimit(e)
//      case e: GenericAtom                    => Seq(e)
//      case Composite(elements)               => Seq(Composite(resolve(elements)))
//      case TxMetaData(elements)              => Seq(TxMetaData(resolve(elements)))
//      case e                                 => Seq(e)
//    }
//    Model(resolve(_model.elements))
//  }
//
//  private def encode(
//    from: Int,
//    to: Int,
//    firstRow: String,
//    lastRow: String,
//    prevSortValues: List[String],
//    nextSortValues: List[String]
//  ): String = {
//    val cleanStrings = Seq(firstRow, lastRow) ++ prevSortValues ++ nextSortValues
//    Base64.getEncoder.encodeToString(
//      (Seq(from.toString, to.toString) ++ cleanStrings).mkString("__~~__").getBytes
//    )
//  }
//
//  private def decode(cursor: String): (Int, Int, String, String, List[String], List[String]) = {
//    val values = new String(Base64.getDecoder.decode(cursor)).split("__~~__", -1).toList
//
//    val (from, until, firstRow, lastRow) = (values.head.toInt, values(1).toInt, values(2), values(3))
//    val size                             = sortCoordinates.head.size
//    val prevSortValues                   = values.slice(4, 4 + size)
//    val nextSortValues                   = values.takeRight(size)
//    (from, until, firstRow, lastRow, prevSortValues, nextSortValues)
//  }
}
