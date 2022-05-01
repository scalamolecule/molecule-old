package molecule.core.pagination

import java.util
import java.util.{List => jList}
import molecule.core.marshalling.Marshalling
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.{ExecutionContext, Future}

trait CursorFlat[Obj, Tpl] extends CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  protected def cursorPageResult[T](
    conn: Conn,
    limit: Int,
    cursor: String,
    transformer: jList[AnyRef] => T
  )(implicit ec: ExecutionContext): Future[(List[T], String, Int)] = {
    val log = new log
    log("=========================================")

    val forward                                   = limit > 0
    val (model, query, prevT,
    firstIndex, lastIndex, firstRow, lastRow,
    sortIndexes, firstSortValues, lastSortValues) = extract(cursor, forward)

    conn.jvmQueryT(model, query).flatMap { case (rowsUnsorted, t) =>
      val totalCount = rowsUnsorted.size
      totalCount match {
        case 0 => Future((List.empty[T], cursor, 0))
        case 1 => Future((List(transformer(rowsUnsorted.iterator.next)), cursor, 0))
        case _ =>
          val rows: util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rowsUnsorted)
          rows.sort(this) // using macro-implemented `compare` method

          rows.forEach(row => log(row))
          //          log.print()

          for {
            (from, until, more) <- (forward, cursor) match {
              case (true, "") => Future((0, limit.min(totalCount), (totalCount - limit).max(0)))
              case (true, _)  => getCursorIndex(
                conn, rows, totalCount, sortIndexes, lastIndex, lastRow, prevT, lastSortValues, 0
              ).map { cursorIndex =>
                val from  = cursorIndex + 1
                val until = (from + limit).min(totalCount)
                val more  = totalCount - until
                (from, until, more)
              }

              case (_, "") => Future(((totalCount + limit).max(0), totalCount, (totalCount + limit).max(0)))
              case (_, _)  => getCursorIndex(
                conn, rows, totalCount, sortIndexes, firstIndex, firstRow, prevT, firstSortValues, totalCount - 1
              ).map { cursorIndex =>
                val from  = (cursorIndex + limit).max(0) // (limit is negative)
                val until = cursorIndex
                val more  = from
                (from, until, more)
              }
            }
          } yield {
            log("from    : " + from)
            log("until   : " + until)

            val acc = List.newBuilder[T]
            var i      = from
            while (i != until) {
              acc += transformer(rows.get(i))
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
            //            log.print()

            (acc.result(), newCursor, more)
          }
      }
    }
  }
}
