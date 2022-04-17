package molecule.core.pagination

import java.util
import java.util.{Base64, List => jList}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import scala.concurrent.{ExecutionContext, Future}
import java.util.{Collection => jCollection, List => jList}
import java.lang.{Long => jLong}

trait CursorTpl[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  final def selectTplRows(
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


  protected def cursorRows2nestedTuples(
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
      val firstSortValues = sortCoordinates.head.map(sc => first.get(sc.i).toString)
      val lastSortValues  = sortCoordinates.head.map(sc => last.get(sc.i).toString)
      val newCursor       = encode(from, until, firstRow, lastRow, firstSortValues, lastSortValues)

      log("firstRow: " + firstRow)
      log("lastRow : " + lastRow)
      //      log("firstSortValues: " + firstSortValues)
      //      log("lastSortValues : " + lastSortValues)
      log.print()

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

  protected def cursorRows2tuples(
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
      val firstSortValues = sortCoordinates.head.map(sc => first.get(sc.i).toString)
      val lastSortValues  = sortCoordinates.head.map(sc => last.get(sc.i).toString)
      val newCursor       = encode(from, until, firstRow, lastRow, firstSortValues, lastSortValues)

      log("firstRow: " + firstRow)
      log("lastRow : " + lastRow)
      //      log("firstSortValues: " + firstSortValues)
      //      log("lastSortValues : " + lastSortValues)
      log.print()

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


  // todo: let macro find value extraction indexes at compile time
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
    prevSortValues: List[String],
    nextSortValues: List[String]
  ): String = {
    val cleanStrings = Seq(firstRow, lastRow) ++ prevSortValues ++ nextSortValues
    Base64.getEncoder.encodeToString(
      (Seq(from.toString, to.toString) ++ cleanStrings).mkString("__~~__").getBytes
    )
  }

  private def decode(cursor: String): (Int, Int, String, String, List[String], List[String]) = {
    val values = new String(Base64.getDecoder.decode(cursor)).split("__~~__", -1).toList

    val (from, until, firstRow, lastRow) = (values.head.toInt, values(1).toInt, values(2), values(3))
    val size                             = sortCoordinates.head.size
    val prevSortValues                   = values.slice(4, 4 + size)
    val nextSortValues                   = values.takeRight(size)
    (from, until, firstRow, lastRow, prevSortValues, nextSortValues)
  }
}
