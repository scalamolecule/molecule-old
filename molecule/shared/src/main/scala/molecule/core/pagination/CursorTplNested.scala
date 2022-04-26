package molecule.core.pagination

import java.lang.{Long => jLong}
import java.util
import java.util.{List => jList}
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.facade.Conn
import scala.concurrent.{ExecutionContext, Future}

trait CursorTplNested[Obj, Tpl] extends CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  protected def selectedNestedTplRows(
    conn: Conn,
    limit: Int,
    cursor: String
  )(implicit ec: ExecutionContext): Future[(util.ArrayList[jList[AnyRef]], String, Int)] = {
    val log = new log
    log("============================================================= NESTED")

    val forward                                   = limit > 0
//    val (model, query, prevT, prevFirstRow, prevLastRow) = extract(cursor, forward)
    val (model, query, t0,
    firstIndex, lastIndex, firstRow, lastRow,
    sortIndexes, firstSortValues, lastSortValues) = extract(cursor, forward)

    val selectedRows          = new util.ArrayList[jList[AnyRef]]()
    var curId : jLong         = 0
    var prevId: jLong         = 0
    var row   : jList[AnyRef] = null
    var current               = true
    var more                  = 0

    conn.jvmQueryT(model, query).map { case (rows, t) =>
      val totalCount = rows.size
      if (totalCount == 0) {
        (new java.util.ArrayList[jList[AnyRef]](0), ",", 0)

      } else if (totalCount == 1) {
        (new java.util.ArrayList(rows), ",", 0)

      } else {
        val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
        sortedRows.sort(this) // using macro-implemented `compare` method

        log(model)
        sortedRows.forEach(row => log(row))

        val (flatFrom, flatUntil) = (forward, cursor) match {
          case (true, "") => (0, totalCount)
          case (true, _)  => (getLastIndex(totalCount, sortedRows, lastRow, limit) + 1, totalCount)
          case (_, "")    => (totalCount - 1, -1)
          case (_, _)     => (getFirstIndex(totalCount, sortedRows, firstRow, limit) - 1, -1)
        }
        log("flatFrom : " + flatFrom)
        log("flatUntil: " + flatUntil)

        var topRowIndex = -1
        if (forward) {
          var i = flatFrom
          while (i != flatUntil) {
            row = sortedRows.get(i)
            curId = row.get(0).asInstanceOf[jLong]
            if (curId != prevId) {
              if (current) {
                topRowIndex += 1
                if (topRowIndex == limit) {
                  more += 1
                }
              } else {
                more += 1
              }
            }
            if (topRowIndex >= 0 && topRowIndex < limit) {
              // prepend rows from end
              selectedRows.add(row)
            } else if (topRowIndex == limit) {
              current = false
            }
            prevId = curId
            i += 1
          }

        } else {
          var i   = flatFrom
          var acc = List.empty[jList[AnyRef]] // for backwards accumulation
          while (i != flatUntil) {
            row = sortedRows.get(i)
            curId = row.get(0).asInstanceOf[jLong]
            if (curId != prevId) {
              if (current) {
                topRowIndex += 1
                if (topRowIndex == -limit) {
                  more += 1
                }
              } else {
                more += 1
              }
            }
            if (topRowIndex >= 0 && topRowIndex < -limit) {
              // prepend rows from end
              acc = row :: acc
            } else if (topRowIndex == -limit) {
              current = false
            }
            prevId = curId
            i -= 1
          }
          // Add selected rows in order
          acc.foreach(row => selectedRows.add(row))
        }

        log("selectedRows")
        selectedRows.forEach(r => log(r))

        val from = 0
        val to = selectedRows.size - 1
        val newFirst           = selectedRows.get(0)
        val newLast            = selectedRows.get(selectedRows.size - 1)
        val newFirstRow        = newFirst.toString
        val newlastRow         = newLast.toString
        val newFirstSortValues = extractSortValues(newFirst)
        val newLastSortValues  = extractSortValues(newLast)
        val newCursor       = encode(
          t, from, to, newFirstRow, newlastRow, sortIndexes, newFirstSortValues, newLastSortValues
        )

        log("more    : " + more)
        log("firstRow: " + newFirstRow)
        log("lastRow : " + newlastRow)
        //        log("firstSortValues: " + firstSortValues)
        //        log("lastSortValues : " + lastSortValues)
        //        log.print()

        (selectedRows, newCursor, more)
      }
    }
  }
}
