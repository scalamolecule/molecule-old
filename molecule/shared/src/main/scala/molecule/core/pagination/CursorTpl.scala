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
    cursor: String
  )(implicit ec: ExecutionContext): Future[(List[Tpl], String, Int)] = {
    val log = new log
    log("=============================================================")

    val forward                                   = limit > 0
    val (model, query, prevFirstRow, prevLastRow) = extract(cursor, forward)

    conn.jvmQuery(model, query).map { rows =>
      val totalCount = rows.size
      if (totalCount == 0) {
        (List.empty[Tpl], ",", 0)

      } else if (totalCount == 1) {
        (List(row2tpl(rows.iterator.next)), ",", 0)

      } else {
        val sortedRows: util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
        sortedRows.sort(this) // using macro-implemented `compare` method

        log(_model)
        sortedRows.forEach(row => log(row))

        val (from, until, more) = (forward, cursor) match {
          case (true, "") => (0, limit.min(totalCount), (totalCount - limit).max(0))
          case (true, _)  =>
            val i     = lastIndex(sortedRows, prevLastRow, limit)
            val from  = i + 1
            val until = (from + limit).min(totalCount)
            (from, until, totalCount - until)

          case (_, "") => ((totalCount + limit).max(0), totalCount, (totalCount + limit).max(0))
          case (_, _)  =>
            val i     = firstIndex(totalCount, sortedRows, prevFirstRow, limit)
            val from  = (i + limit).max(0) // (limit is negative)
            val until = i
            (from, until, from)
        }
        log("from    : " + from)
        log("until   : " + until)
        log("more    : " + more)

        val tuples = List.newBuilder[Tpl]
        var i      = from
        while (i != until) {
          tuples += row2tpl(sortedRows.get(i))
          i += 1
        }
        val first           = sortedRows.get(from)
        val last            = sortedRows.get(until - 1)
        val firstRow        = first.toString
        val lastRow         = last.toString
        val firstSortValues = extractSortValues(first)
        val lastSortValues  = extractSortValues(last)
        val newCursor       = encode(firstRow, lastRow, firstSortValues, lastSortValues)

        log("firstRow: " + firstRow)
        log("lastRow : " + lastRow)
        //      log("firstSortValues: " + firstSortValues)
        //      log("lastSortValues : " + lastSortValues)
        //        log.print()

        (tuples.result(), newCursor, more)
      }
    }
  }
}
