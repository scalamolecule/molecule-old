package molecule.core.pagination

import java.util
import java.util.{List => jList}
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.facade.Conn
import scala.concurrent.{ExecutionContext, Future}

trait CursorJson[Obj, Tpl] extends CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  protected def cursorPageResultJson(
    conn: Conn,
    limit: Int,
    cursor: String,
  )(implicit ec: ExecutionContext): Future[(StringBuffer, String, Int)] = {
    val sb      = new StringBuffer()
    val forward = limit > 0

    val (model, query, prevT,
    firstIndex, lastIndex, firstRow, lastRow,
    sortIndexes, firstSortValues, lastSortValues) = extract(cursor, forward)

    conn.jvmQueryT(model, query).flatMap { case (rowsUnsorted, t) =>
      val totalCount = rowsUnsorted.size
      totalCount match {
        case 0 => Future((sb, cursor, 0))
        case 1 => Future((row2json(rowsUnsorted.iterator.next, sb), cursor, 0))
        case _ => resolveSubstituteCursor(
          conn, limit, cursor, rowsUnsorted, forward, totalCount,
          sortIndexes, lastIndex, firstIndex, lastRow, firstRow, lastSortValues, firstSortValues, prevT, t,
          (rows: util.ArrayList[jList[AnyRef]], from: Int, until: Int) => {
            val sb   = new StringBuffer()
            var next = false
            var i    = from
            while (i != until) {
              if (next) sb.append(",") else next = true
              row2json(rows.get(i), sb)
              i += 1
            }
            sb.append("\n    ")
          }
        )
      }
    }
  }
}
