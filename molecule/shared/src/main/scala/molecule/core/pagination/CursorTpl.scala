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

trait CursorTpl[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  final def sortedRows2selectedRowsTpl(
    sortedRows: util.ArrayList[jList[AnyRef]],
    limit: Int,
    offset: Int
  ): (jCollection[jList[AnyRef]], Int) = {
    val totalCount = sortedRows.size
    if (limit == 0) {
      return (sortedRows, totalCount)
    }
    var curId : jLong         = 0
    var prevId: jLong         = 0
    val selectedRows          = new util.ArrayList[jList[AnyRef]]()
    var row   : jList[AnyRef] = null
    var continue              = true

    if (limit > 0) {
      // From start ..........
      var acc         = List.empty[jList[AnyRef]]
      var topRowIndex = -1
      val until       = offset + limit
      var i           = totalCount - 1 // Start from back
      while (continue && i != -1) {
        row = sortedRows.get(i)
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        if (topRowIndex >= offset && topRowIndex < until) {
          // prepend rows from end
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

    } else {
      // From end ..........
      var topRowIndex = -1
      val until       = offset - limit // (limit is negative)
      var i           = 0 // Start from beginning
      while (continue && i != totalCount) {
        row = sortedRows.get(i)
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        if (topRowIndex >= offset && topRowIndex < until) {
          // append rows from start
          selectedRows.add(row)
        } else if (topRowIndex == until) {
          continue = false
        }
        prevId = curId
        i += 1
      }
      (selectedRows, topRowIndex + 1)
    }
  }

  private def exractSortValues(row: jList[AnyRef]): List[String] = {
    sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").map(sc => row.get(sc.i).toString)
  }

  protected def selectedNestedTplRows(
    conn: Conn,
    limit: Int,
    cursor: String
  )(implicit ec: ExecutionContext): Future[(jCollection[jList[AnyRef]], String, Int)] = {
    val log                   = new log
    val forward               = limit > 0
    var curId : jLong         = 0
    var prevId: jLong         = 0
    val selectedRows          = new util.ArrayList[jList[AnyRef]]()
    var row   : jList[AnyRef] = null
    var current               = true
    var more                  = 0

    def resolve(selectedRows: java.util.ArrayList[jList[AnyRef]]): (jCollection[jList[AnyRef]], String, Int) = {
      val from  = 0
      val until = selectedRows.size
      log("from    : 0")
      log("until   : " + until)

      val first           = selectedRows.get(until - 1) // first from back
      val last            = selectedRows.get(from)
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

    def collectBackwards(
      sortedRows: java.util.ArrayList[jList[AnyRef]],
      from: Int
    ) = {
      var topRowIndex = -1
      var i           = from
      var acc         = List.empty[jList[AnyRef]]
      while (i != -1) {
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
          acc = row :: acc
        } else if (topRowIndex == limit) {
          current = false
        }
        prevId = curId
        i -= 1
      }
      // Make java list with selected reversed rows
      acc.foreach(row => selectedRows.add(row))
      selectedRows
    }

    def collectForwards(
      sortedRows: java.util.ArrayList[jList[AnyRef]],
      totalCount: Int,
      from: Int
    ) = {
      var topRowIndex = -1
      var i           = from
      while (i != totalCount) {
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
          selectedRows.add(row)
        } else if (topRowIndex == -limit) {
          current = false
        }
        prevId = curId
        i += 1
      }
      selectedRows
    }

    log("=========================================================================")
    if (cursor.isEmpty) {
      conn.jvmQuery(_model, _query).map { rows =>
        val totalCount = rows.size
        if (totalCount == 0) {
          (new java.util.ArrayList[jList[AnyRef]](0), ",", 0)
        } else if (totalCount == 1) {
          (rows, ",", 0)
        } else {
          val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
          sortedRows.sort(this) // using macro-implemented `compare` method
          log(_model)
          sortedRows.forEach(row => log(row))

          if (forward) { // (going backwards since nested raw rows are sorted in reverse)
            log("----------------- _ -->")
            resolve(collectBackwards(sortedRows, totalCount - 1))

          } else {
            log("----------------- _ <--")
            resolve(collectForwards(sortedRows, totalCount, 0))
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
          (rows, ",", 0)
        } else {
          val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
          sortedRows.sort(this) // using macro-implemented `compare` method
          log(offsetModel)
          sortedRows.forEach(row => log(row))

          if (forward) { // (going backwards since nested raw rows are sorted in reverse)
            log("----------------- A -->")
            var i = totalCount - 1 // Start from last
            while (sortedRows.get(i).toString != prevLastRow && i != -1) {
              i -= 1
            }
            i -= 1 // Go from the row before
            resolve(collectBackwards(sortedRows, i))

          } else {
            log("----------------- A <--")
            var i = 0 // Start from first
            while (sortedRows.get(i).toString != prevFirstRow && i != -limit) {
              i += 1
            }
            i += 1 // Go from the row after
            resolve(collectForwards(sortedRows, totalCount, i))
          }
        }
      }
    }
  }

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


  private def getOffsetModel(forward: Boolean, prevTokens: List[String], nextTokens: List[String]): Model = {
    def addLimit(e: GenericAtom): Seq[Element] = {
      val (asc, i)   = (e.sort.head == 'a', e.sort.last.toString.toInt - 1)
      val token      = if (forward) nextTokens(i) else prevTokens(i)
      val typedValue = e.tpe match {
        case "Int"    => token.toInt
        case "String" => token
        case other    => throw MoleculeException("Unexpected sort attribute type: " + other)
      }
      val limitExpr  = if (forward && asc || !forward && !asc) Ge(typedValue) else Le(typedValue)
      e match {
        case a: Atom    => Seq(a, a.copy(attr = a.attr + "_", value = limitExpr, sort = ""))
        case g: Generic => Seq(g, g.copy(attr = g.attr + "_", value = limitExpr, sort = ""))
      }
    }
    def resolve(elements: Seq[Element]): Seq[Element] = elements.flatMap {
      case e: GenericAtom if e.sort.nonEmpty => addLimit(e)
      case e: GenericAtom                    => Seq(e)
      case Composite(elements)               => Seq(Composite(resolve(elements)))
      case TxMetaData(elements)              => Seq(TxMetaData(resolve(elements)))
      case e                                 => Seq(e)
    }
    Model(resolve(_model.elements))
  }

  private def encode(
    from: Int,
    to: Int,
    firstRow: String,
    lastRow: String,
    firstSortValues: List[String],
    lastSortValues: List[String]
  ): String = {
    val cleanStrings = Seq(firstRow, lastRow) ++ firstSortValues ++ lastSortValues
    //    println(cleanStrings.mkString("  ", "\n  ", ""))
    Base64.getEncoder.encodeToString(
      (Seq(from.toString, to.toString) ++ cleanStrings).mkString("__~~__").getBytes
    )
  }

  private def decode(cursor: String): (Int, Int, String, String, List[String], List[String]) = {
    //    println("XXX " + new String(Base64.getDecoder.decode(cursor)))
    val values                           = new String(Base64.getDecoder.decode(cursor)).split("__~~__", -1).toList
    //    println(values.mkString("  ", "\n  ", ""))
    val (from, until, firstRow, lastRow) = (values.head.toInt, values(1).toInt, values(2), values(3))
    val size                             = sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").size
    val firstSortValues                  = values.slice(4, 4 + size)
    val lastSortValues                   = values.takeRight(size)
    (from, until, firstRow, lastRow, firstSortValues, lastSortValues)
  }
}
