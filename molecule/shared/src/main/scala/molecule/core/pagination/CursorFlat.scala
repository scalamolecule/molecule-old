package molecule.core.pagination

import java.util
import java.util.{List => jList}
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.facade.Conn
import scala.concurrent.{ExecutionContext, Future}

trait CursorFlat[Obj, Tpl] extends CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  protected def cursorPageResult[T](
    conn: Conn,
    limit: Int,
    cursor: String,
    transformer: jList[AnyRef] => T // row2tpl or row2obj
  )(implicit ec: ExecutionContext): Future[(List[T], String, Int)] = {
    val forward = limit > 0

    val (model, query, prevT,
    firstIndex, lastIndex, firstRow, lastRow,
    sortIndexes, firstSortValues, lastSortValues) = extract(cursor, forward)

    conn.jvmQueryT(model, query).flatMap { case (rowsUnsorted, t) =>
      val totalCount = rowsUnsorted.size
      totalCount match {
        case 0 => Future((List.empty[T], cursor, 0))
        case 1 => Future((List(transformer(rowsUnsorted.iterator.next)), cursor, 0))
        case _ => resolveSubstituteCursor(
          conn, limit, cursor, rowsUnsorted, forward, totalCount,
          sortIndexes, lastIndex, firstIndex, lastRow, firstRow, lastSortValues, firstSortValues, prevT, t,
          (rows: util.ArrayList[jList[AnyRef]], from: Int, until: Int) => {
            val acc = List.newBuilder[T]
            var i   = from
            while (i != until) {
              acc += transformer(rows.get(i))
              i += 1
            }
            acc.result()
          }
        )
      }
    }
  }
}
