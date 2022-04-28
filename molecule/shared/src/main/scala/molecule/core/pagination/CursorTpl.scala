package molecule.core.pagination

import java.util
import java.util.{List => jList}
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.facade.Conn
import scala.concurrent.{ExecutionContext, Future}

trait CursorTpl[Obj, Tpl] extends CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  protected def selectTplRows(
    conn: Conn,
    limit: Int,
    cursor: String,
  )(implicit ec: ExecutionContext): Future[(List[Tpl], String, Int)] = {
    val log = new log
    log("=============================================================")
    val forward = limit > 0

    val (model, query, prevT,
    firstIndex, lastIndex, firstRow, lastRow,
    sortIndexes, firstSortValues, lastSortValues) = extract(cursor, forward)

    conn.jvmQueryT(model, query).flatMap { case (rowsUnsorted, t) =>
      val totalCount = rowsUnsorted.size
      totalCount match {
        case 0 => Future((List.empty[Tpl], cursor, 0))
        case 1 => Future((List(row2tpl(rowsUnsorted.iterator.next)), cursor, 0))
        case _ =>
          val rows: util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rowsUnsorted)
          rows.sort(this) // using macro-implemented `compare` method

          log(_model)
          rows.forEach(row => log(row))

          for {
            (from, until, more) <- (forward, cursor) match {
              case (true, "") => Future((0, limit.min(totalCount), (totalCount - limit).max(0)))
              case (true, _)  => getUpdatedLastIndex(
                conn, rows, totalCount, sortIndexes, lastIndex, lastRow, prevT, lastSortValues
              ).map { updatedLastIndex =>
                val from  = updatedLastIndex + 1
                val until = (from + limit).min(totalCount)
                (from, until, totalCount - until)
              }

              case (_, "") => Future(((totalCount + limit).max(0), totalCount, (totalCount + limit).max(0)))
              case (_, _)  => getUpdatedFirstIndex(
                conn, rows, totalCount, sortIndexes, firstIndex, firstRow, prevT, firstSortValues
              ).map { updatedFirstIndex =>
                val from  = (updatedFirstIndex + limit).max(0) // (limit is negative)
                val until = updatedFirstIndex
                (from, until, from)
              }
            }
          } yield {
            log("from    : " + from)
            log("until   : " + until)

            val tuples = List.newBuilder[Tpl]
            var i      = from
            while (i != until) {
              tuples += row2tpl(rows.get(i))
              i += 1
            }
            val newFirst           = rows.get(from)
            val newLast            = rows.get(until - 1)
            val newFirstRow        = newFirst.toString
            val newLastRow         = newLast.toString
            val newFirstSortValues = sortValueStrings(newFirst)
            val newLastSortValues  = sortValueStrings(newLast)
            val newCursor          = encode(
              t, from, until - 1, newFirstRow, newLastRow, sortIndexes, newFirstSortValues, newLastSortValues
            )

            log("more    : " + more)
            log("firstRow: " + newFirstRow)
            log("lastRow : " + newLastRow)
            //            log("firstSortValues: " + firstSortValues)
            //            log("lastSortValues : " + lastSortValues)
//            log.print()

            (tuples.result(), newCursor, more)
          }
      }
    }
  }
}
