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
//    lookupThreshold: Int
  )(implicit ec: ExecutionContext): Future[(List[Tpl], String, Int)] = {
    val log = new log
    log("=============================================================")
    val forward = limit > 0

    val (model, query, t0,
    firstIndex, lastIndex, firstRow, lastRow,
    sortIndexes, firstSortValues, lastSortValues) = extract(cursor, forward)

    conn.jvmQueryT(model, query).flatMap { case (rowsUnsorted, t) =>
      val totalCount = rowsUnsorted.size
      if (totalCount == 0) {
        Future((List.empty[Tpl], ",", 0))

      } else if (totalCount == 1) {
        Future((List(row2tpl(rowsUnsorted.iterator.next)), ",", 0))

      } else {
        val rows: util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rowsUnsorted)
        rows.sort(this) // using macro-implemented `compare` method

        log(_model)
        rows.forEach(row => log(row))

        for {
          (from, until, more) <- (forward, cursor) match {
            case (true, "") => Future((0, limit.min(totalCount), (totalCount - limit).max(0)))
            case (true, _)  =>
              lastIndex2(conn, rows, totalCount, sortIndexes, lastIndex, lastRow, t0, lastSortValues).map { i =>
                val from  = i + 1
                val until = (from + limit).min(totalCount)
                (from, until, totalCount - until)
              }

            case (_, "") => Future(((totalCount + limit).max(0), totalCount, (totalCount + limit).max(0)))
            case (_, _)  =>
              firstIndex2(conn, rows, totalCount, sortIndexes, lastIndex, lastRow, t0, lastSortValues, limit).map { i =>
                val from  = (i + limit).max(0) // (limit is negative)
                val until = i
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
          val newFirstSortValues = extractSortValues(newFirst)
          val newLastSortValues  = extractSortValues(newLast)
          val newCursor          = encode(
            t, from, until - 1, newFirstRow, newLastRow, sortIndexes, newFirstSortValues, newLastSortValues
          )

          log("more    : " + more)
          log("firstRow: " + newFirstRow)
          log("lastRow : " + newLastRow)
          //      log("firstSortValues: " + firstSortValues)
          //      log("lastSortValues : " + lastSortValues)
          log.print()

          (tuples.result(), newCursor, more)
        }
      }
    }
  }

//  protected def selectTplRowsOLD(
//    conn: Conn,
//    limit: Int,
//    cursor: String,
//    lookupThreshold: Int
//  )(implicit ec: ExecutionContext): Future[(List[Tpl], String, Int)] = {
//    val log = new log
//    log("=============================================================")
//
//    val forward                                          = limit > 0
//    val (model, query, prevT, prevFirstRow, prevLastRow) = extract(cursor, forward)
//
//    conn.jvmQueryT(model, query).map { case (rows, t) =>
//      val totalCount = rows.size
//      if (totalCount == 0) {
//        (List.empty[Tpl], ",", 0)
//
//      } else if (totalCount == 1) {
//        (List(row2tpl(rows.iterator.next)), ",", 0)
//
//      } else {
//        val sortedRows: util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
//        sortedRows.sort(this) // using macro-implemented `compare` method
//
//        log(_model)
//        sortedRows.forEach(row => log(row))
//
//        val (from, until, more) = (forward, cursor) match {
//          case (true, "") => (0, limit.min(totalCount), (totalCount - limit).max(0))
//          case (true, _)  =>
//            val i     = lastIndex(totalCount, sortedRows, prevLastRow, lookupThreshold)
//            //            val i     = {
//            //              try {
//            //                lastIndex(totalCount, sortedRows, prevLastRow, lookup)
//            //              } catch {
//            //                case e: Exception =>
//            //                  println("AAA " + e)
//            //                  return Future.failed(e)
//            //              }
//            //            }
//            val from  = i + 1
//            val until = (from + limit).min(totalCount)
//            (from, until, totalCount - until)
//
//          case (_, "") => ((totalCount + limit).max(0), totalCount, (totalCount + limit).max(0))
//          case (_, _)  =>
//            val i     = firstIndex(totalCount, sortedRows, prevFirstRow, lookupThreshold)
//            val from  = (i + limit).max(0) // (limit is negative)
//            val until = i
//            (from, until, from)
//        }
//        log("from    : " + from)
//        log("until   : " + until)
//
//        val tuples = List.newBuilder[Tpl]
//        var i      = from
//        while (i != until) {
//          tuples += row2tpl(sortedRows.get(i))
//          i += 1
//        }
//        val newFirst           = sortedRows.get(from)
//        val newLast            = sortedRows.get(until - 1)
//        val newFirstRow        = newFirst.toString
//        val newLastRow         = newLast.toString
//        val newFirstSortValues = extractSortValues(newFirst)
//        val newLastSortValues  = extractSortValues(newLast)
//        val newCursor          = encode(t, newFirstRow, newLastRow, newFirstSortValues, newLastSortValues)
//
//        log("more    : " + more)
//        log("firstRow: " + newFirstRow)
//        log("lastRow : " + newLastRow)
//        //      log("firstSortValues: " + firstSortValues)
//        //      log("lastSortValues : " + lastSortValues)
//        log.print()
//
//        (tuples.result(), newCursor, more)
//      }
//    }
//  }
}
