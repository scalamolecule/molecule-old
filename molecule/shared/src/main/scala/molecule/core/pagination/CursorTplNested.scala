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
    log("============================================================ NESTED")
    val forward                                   = limit > 0
    val (model, query, prevT,
    firstIndex, lastIndex, firstRow, lastRow,
    sortIndexes, firstSortValues, lastSortValues) = extract(cursor, forward)

    conn.jvmQueryT(model, query).flatMap { case (rowsUnsorted, t) =>
      val totalCount = rowsUnsorted.size
      totalCount match {
        case 0 => Future((new java.util.ArrayList[jList[AnyRef]](0), cursor, 0))
        case 1 => Future((new java.util.ArrayList(rowsUnsorted), cursor, 0))
        case _ =>
          val rows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rowsUnsorted)
          rows.sort(this) // using macro-implemented `compare` method

          rows.forEach(row => log(row))
          //          log.print()

          for {
            (flatStart, flatStop) <- (forward, cursor) match {
              case (true, "") => Future((0, totalCount))
              case (true, _)  => getCursorIndex(
                conn, rows, totalCount, sortIndexes, lastIndex, lastRow, prevT, lastSortValues, 0
              ).map(flatStartIndex => (flatStartIndex + 1, totalCount))
              case (_, "")    => Future((totalCount - 1, -1))
              case (_, _)     => getCursorIndex(
                conn, rows, totalCount, sortIndexes, firstIndex, firstRow, prevT, firstSortValues, totalCount - 1
              ).map(flatCursorIndex => (flatCursorIndex - 1, -1))
            }
          } yield {
            log("flatStart: " + flatStart)
            log("flatStop : " + flatStop)
            //            log.print()

            val (selectedRows, more) = allFlatRows2selectedFlatRows(rows, limit, flatStart, flatStop)

            log("--- selected rows: ---")
            selectedRows.forEach(r => log(r))

            val from               = 0
            val to                 = selectedRows.size - 1
            val newFirst           = selectedRows.get(from)
            val newLast            = selectedRows.get(to)
            val newFirstRow        = newFirst.toString
            val newLastRow         = newLast.toString
            val newFirstSortValues = sortValueStrings(newFirst)
            val newLastSortValues  = sortValueStrings(newLast)
            val newCursor          = encode(
              t, from, to, newFirstRow, newLastRow, sortIndexes, newFirstSortValues, newLastSortValues
            )

            log("newFirstIndex: " + from)
            log("newLastIndex : " + to)
            log("more         : " + more)
            log("newFirstRow  : " + newFirstRow)
            log("newLastRow   : " + newLastRow)
            //            log.print()

            (selectedRows, newCursor, more)
          }
      }
    }
  }

  private def allFlatRows2selectedFlatRows(
    rows: util.ArrayList[jList[AnyRef]],
    limit: Int,
    flatStart: Int,
    flatStop: Int,
  ): (util.ArrayList[jList[AnyRef]], Int) = {
    val selectedRows          = new util.ArrayList[jList[AnyRef]]()
    var curId : jLong         = 0
    var prevId: jLong         = 0
    var row   : jList[AnyRef] = null
    var current               = true
    var more                  = 0
    var topRowIndex           = -1
    if (limit > 0) {
      var i = flatStart
      while (i != flatStop) {
        row = rows.get(i)
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
        if (current && topRowIndex >= 0 && topRowIndex < limit) {
          // append row
          selectedRows.add(row)
        } else if (topRowIndex == limit) {
          current = false
        }
        prevId = curId
        i += 1
      }

    } else {
      var i = flatStart
      while (i != flatStop) {
        row = rows.get(i)
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
        if (current && topRowIndex >= 0 && topRowIndex < -limit) {
          // prepend row
          selectedRows.add(0, row)
        } else if (topRowIndex == -limit) {
          current = false
        }
        prevId = curId
        i -= 1
      }
    }
    (selectedRows, more)
  }
}