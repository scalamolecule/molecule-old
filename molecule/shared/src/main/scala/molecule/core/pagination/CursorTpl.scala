package molecule.core.pagination

import java.lang.{Long => jLong}
import java.util
import java.util.{List => jList}
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import scala.concurrent.{ExecutionContext, Future}

trait CursorTpl[Obj, Tpl] extends CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  protected def selectTplRows(
    conn: Conn,
    limit: Int,
    cursor: String
  )(implicit ec: ExecutionContext): Future[(List[Tpl], String, Int)] = {
    val tuples = List.newBuilder[Tpl]
    var i      = 0
    val log    = new log

    def resolve(
      sortedRows: java.util.ArrayList[jList[AnyRef]],
      from: Int,
      until: Int,
      more: Int
    ): (List[Tpl], String, Int) = {
      log("from    : " + from)
      log("until   : " + until)

      i = from
      while (i != until) {
        tuples += row2tpl(sortedRows.get(i))
        i += 1
      }
      val first           = sortedRows.get(from)
      val last            = sortedRows.get(until - 1)
      val firstRow        = first.toString
      val lastRow         = last.toString
      val firstSortValues = exractSortValues(first)
      val lastSortValues  = exractSortValues(last)
      val newCursor       = encode(from, until, firstRow, lastRow, firstSortValues, lastSortValues)

      log("firstRow: " + firstRow)
      log("lastRow : " + lastRow)
      //      log("firstSortValues: " + firstSortValues)
      //      log("lastSortValues : " + lastSortValues)
      //      log.print()

      (tuples.result(), newCursor, more)
    }

    log("=========================================================================")
    if (cursor.isEmpty) {
      conn.jvmQuery(_model, _query).map { rows =>
        val totalCount = rows.size
        if (totalCount == 0) {
          (List.empty[Tpl], ",", 0)
        } else if (totalCount == 1) {
          (List(row2tpl(rows.iterator.next)), ",", 0)
        } else {

          val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
          sortedRows.sort(this) // using macro-implemented `compare` method

          log(_model)
          sortedRows.forEach(row => log(row))

          if (limit > 0) {
            log("----------------- _ -->")
            val from  = 0
            val until = limit.min(totalCount)
            resolve(sortedRows, from, until, totalCount - until)

          } else {
            log("----------------- _ <--")
            val until = totalCount
            val from  = (until + limit).max(0) // (limit is negative)
            resolve(sortedRows, from, until, from)
          }
        }
      }

    } else {
      val (from, until, prevFirstRow, prevLastRow, prevFirstSortValues, prevLastSortValues) = decode(cursor)

      val forward     = limit > 0
      val offsetModel = getOffsetModel(forward, prevFirstSortValues, prevLastSortValues)
      conn.jvmQuery(offsetModel, Model2Query(offsetModel).get._1).map { rows =>
        val totalCount = rows.size
        if (totalCount == 0) {
          (List.empty[Tpl], ",", 0)
        } else if (totalCount == 1) {
          (List(row2tpl(rows.iterator.next)), ",", 0)

        } else {
          val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
          sortedRows.sort(this) // using macro-implemented `compare` method

          log(offsetModel)
          sortedRows.forEach(row => log(row))

          if (forward) {
            log("----------------- A -->")
            // Walk forward to find row index of previous last row
            i = 0
            while (sortedRows.get(i).toString != prevLastRow && i != limit) {
              i += 1
            }
            val from  = i + 1 // Go from the row after
            val until = (from + limit).min(totalCount)
            resolve(sortedRows, from, until, totalCount - until)

          } else {
            log("----------------- A <--")
            // Walk backwards to find row index of previous first row
            i = totalCount - 1
            val lower = i + limit // (limit is negative)
            while (sortedRows.get(i).toString != prevFirstRow && i != lower) {
              i -= 1
            }
            val until = i // Go from the row before
            val from  = (until + limit).max(0) // (limit is negative)
            resolve(sortedRows, from, until, from)
          }
        }
      }
    }
  }


  protected def selectedNestedTplRows(
    conn: Conn,
    limit: Int,
    cursor: String
  )(implicit ec: ExecutionContext): Future[(util.ArrayList[jList[AnyRef]], String, Int)] = {
    val log                   = new log
    val forward               = limit > 0
    var curId : jLong         = 0
    var prevId: jLong         = 0
    val selectedRows          = new util.ArrayList[jList[AnyRef]]()
    var row   : jList[AnyRef] = null
    var current               = true
    var more                  = 0

    def resolve(selectedRows: util.ArrayList[jList[AnyRef]]): (util.ArrayList[jList[AnyRef]], String, Int) = {
      val from  = 0
      val until = selectedRows.size
      log("from    : 0")
      log("until   : " + until)

      val first           = selectedRows.get(from)
      val last            = selectedRows.get(until - 1)
      val firstRow        = first.toString
      val lastRow         = last.toString
      val firstSortValues = exractSortValues(first)
      val lastSortValues  = exractSortValues(last)
      val newCursor       = encode(from, until, firstRow, lastRow, firstSortValues, lastSortValues)

      log("firstRow: " + firstRow)
      log("lastRow : " + lastRow)
      log("firstSortValues: " + firstSortValues)
      log("lastSortValues : " + lastSortValues)
      //      log.print()

      (selectedRows, newCursor, more)
    }

    def collectForwards(
      sortedRows: java.util.ArrayList[jList[AnyRef]],
      totalCount: Int,
      from: Int
    ) = {

      log("sortedRows")
      sortedRows.forEach(r => log(r))

      var topRowIndex = -1
      var i           = from
      while (i != totalCount) {
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
      log("selectedRows")
      selectedRows.forEach(r => log(r))
      selectedRows
    }

    def collectBackwards(
      sortedRows: java.util.ArrayList[jList[AnyRef]],
      from: Int
    ) = {

      log("sortedRows")
      sortedRows.forEach(r => log(r))

      var topRowIndex = -1
      var i           = from
      var acc         = List.empty[jList[AnyRef]]
      while (i != -1) {
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
      // Make java list with selected reversed rows
      acc.foreach(row => selectedRows.add(row))

      log("selectedRows")
      selectedRows.forEach(r => log(r))

      selectedRows
    }

    log("=========================================================================")
    if (cursor.isEmpty) {
      conn.jvmQuery(_model, _query).map { rows =>
        val totalCount = rows.size
        if (totalCount == 0) {
          (new java.util.ArrayList[jList[AnyRef]](0), ",", 0)
        } else if (totalCount == 1) {
          (new java.util.ArrayList(rows), ",", 0)
        } else {
          val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
          sortedRows.sort(this) // using macro-implemented `compare` method
          log(_model)
          sortedRows.forEach(row => log(row))

          if (forward) {
            log("----------------- _ -->")
            resolve(collectForwards(sortedRows, totalCount, 0))

          } else {
            log("----------------- _ <--")
            resolve(collectBackwards(sortedRows, totalCount - 1))
          }
        }
      }

    } else {
      val (from, until, prevFirstRow, prevLastRow, prevFirstSortValues, prevLastSortValues) = decode(cursor)

      val offsetModel = getOffsetModel(forward, prevFirstSortValues, prevLastSortValues)
      conn.jvmQuery(offsetModel, Model2Query(offsetModel).get._1).map { rows =>
        val totalCount = rows.size
        if (totalCount == 0) {
          (new java.util.ArrayList[jList[AnyRef]](0), ",", 0)
        } else if (totalCount == 1) {
          (new java.util.ArrayList(rows), ",", 0)
        } else {
          val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
          sortedRows.sort(this) // using macro-implemented `compare` method
          log(offsetModel)
          sortedRows.forEach(row => log(row))

          if (forward) {
            log("----------------- A -->")
            var i = 0
            while (sortedRows.get(i).toString != prevLastRow && i != limit) {
              i += 1
            }
            i += 1 // Go from the row after match
            resolve(collectForwards(sortedRows, totalCount, i))

          } else {
            log("----------------- A <--")
            var i = totalCount - 1
            while (sortedRows.get(i).toString != prevFirstRow && i != -1) {
              i -= 1
            }
            i -= 1 // Go from the row before match
            resolve(collectBackwards(sortedRows, i))
          }
        }
      }
    }
  }
}
