package molecule.core.pagination

import java.lang.{Long => jLong}
import java.util
import java.util.{Base64, List => jList}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.ast.dbView.{AsOf, TxLong}
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import utest.test
import scala.annotation.{nowarn, tailrec}
import scala.concurrent.{ExecutionContext, Future}

trait CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  final protected def sortedRows2selectedRows(
    sortedRows: util.ArrayList[jList[AnyRef]],
    limit: Int,
    offset: Int
  ): (util.ArrayList[jList[AnyRef]], Int) = {
    var topRowIndex   = -1
    var curId : jLong = 0
    var prevId: jLong = 0
    if (limit == 0) {
      // All rows (offset is 0)
      sortedRows.forEach { row =>
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        prevId = curId
      }
      return (sortedRows, topRowIndex + 1)
    }

    val totalCount         = sortedRows.size
    val selectedRows       = new util.ArrayList[jList[AnyRef]]()
    var row: jList[AnyRef] = null
    var continue           = true

    if (limit > 0) {
      // From start ..........
      val until = offset + limit
      var i     = 0
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

    } else {
      // From end ..........
      var acc   = List.empty[jList[AnyRef]] // for backwards accumulation
      val until = offset - limit // (limit is negative)
      var i     = totalCount - 1
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
    }
  }

  final protected def extractSortValues(row: jList[AnyRef]): List[String] = {
    sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").map(sc => row.get(sc.i).toString)
  }

  final protected def getModelGe(
    forward: Boolean,
    prevTokens: List[String],
    nextTokens: List[String]
  ): Model = {
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

  final protected def getModelEq(sortValues: List[String]): Model = {
    def addEq(e: GenericAtom): Seq[Element] = {
      val sortValue  = sortValues(e.sort.last.toString.toInt - 1)
      val typedValue = e.tpe match {
        case "Int"    => sortValue.toInt
        case "String" => sortValue
        case other    => throw MoleculeException("Unexpected sort attribute type: " + other)
      }
      val limitExpr  = Eq(Seq(typedValue))
      e match {
        case a: Atom    => Seq(a, a.copy(attr = a.attr + "_", value = limitExpr, sort = ""))
        case g: Generic => Seq(g, g.copy(attr = g.attr + "_", value = limitExpr, sort = ""))
      }
    }
    def resolve(elements: Seq[Element]): Seq[Element] = elements.flatMap {
      case e: GenericAtom if e.sort.nonEmpty => addEq(e)
      case e: GenericAtom                    => Seq(e)
      case Composite(elements)               => Seq(Composite(resolve(elements)))
      case TxMetaData(elements)              => Seq(TxMetaData(resolve(elements)))
      case e                                 => Seq(e)
    }
    Model(resolve(_model.elements))
  }


  protected def getFirstIndex(
    totalCount: Int,
    sortedRows: util.ArrayList[jList[AnyRef]],
    prevFirstRow: String,
    lookupThreshold: Int
  ): Int = {
    // Walk backward to find row index of previous first row
    var i     = totalCount - 1
    val lower = i - lookupThreshold
    while (sortedRows.get(i).toString != prevFirstRow && i != lower) {
      i -= 1
    }
    i
  }

  final protected def encode(
    t: Long,
    firstIndex: Int,
    lastIndex: Int,
    firstRow: String,
    lastRow: String,
    sortIndexes: List[Int],
    firstSortValues: List[String],
    lastSortValues: List[String]
  ): String = {
    val cleanStrings = Seq(t, firstIndex, lastIndex, firstRow, lastRow) ++ sortIndexes ++ firstSortValues ++ lastSortValues
    //    println(cleanStrings.mkString("  ", "\n  ", ""))
    Base64.getEncoder.encodeToString(cleanStrings.mkString("__~~__").getBytes)
  }

  final protected def decode(cursor: String): (Long, Int, Int, String, String, List[Int], List[String], List[String]) = {
    //    println("XXX " + new String(Base64.getDecoder.decode(cursor)))
    val values                                        = new String(Base64.getDecoder.decode(cursor)).split("__~~__", -1).toList
    //    println(values.mkString("  ", "\n  ", ""))
    val (t, firstIndex, lastIndex, firstRow, lastRow) = (values.head, values(1), values(2), values(3), values(4))
    val n                                             = 5
    val s                                             = sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").size
    val (n0, n1, n2, n3)                              = (n, n + s, n + s * 2, n + s * 3)
    val sortIndexes                                   = values.slice(n0, n1).map(_.toInt)
    val firstSortValues                               = values.slice(n1, n2)
    val lastSortValues                                = values.slice(n2, n3)
    (t.toLong, firstIndex.toInt, lastIndex.toInt, firstRow, lastRow, sortIndexes, firstSortValues, lastSortValues)
  }

  protected def getLastIndex(
    totalCount: Int,
    sortedRows: util.ArrayList[jList[AnyRef]],
    lastRow: String,
    lookupThreshold: Int
  ): Int = {
    val last = totalCount - 1
    // Walk forward to find row index of previous last row
    var i    = 0
    println("totalCount: " + totalCount)
    println("threshold : " + lookupThreshold)
    println("last      : " + last)
    while (sortedRows.get(i).toString != lastRow) {
      i += 1
      println(i)
      if (i == lookupThreshold) {
        println(s"Couldn't find previous last row `$lastRow` within first $lookupThreshold rows. " +
          s"Now trying to compare all rows from previous page.")
      }
      if (i == last) {
        println(s"Couldn't find previous last row within all $totalCount rows.")
      }
    }
    i
  }

  @nowarn private def sortValuesLambda(
    sortIndexes: List[Int],
    row: jList[AnyRef]
  ): (List[AnyRef], jList[AnyRef] => List[AnyRef]) = {
    sortIndexes match {
      case List(a) => (
        List(row.get(a)),
        (row: jList[AnyRef]) => List(row.get(a)))

      case List(a, b) => (
        List(row.get(a), row.get(b)),
        (row: jList[AnyRef]) => List(row.get(a), row.get(b)))

      case List(a, b, c) => (
        List(row.get(a), row.get(b), row.get(c)),
        (row: jList[AnyRef]) => List(row.get(a), row.get(b), row.get(c)))

      case List(a, b, c, d) => (
        List(row.get(a), row.get(b), row.get(c), row.get(d)),
        (row: jList[AnyRef]) => List(row.get(a), row.get(b), row.get(c), row.get(d)))

      case List(a, b, c, d, e) => (
        List(row.get(a), row.get(b), row.get(c), row.get(d), row.get(e)),
        (row: jList[AnyRef]) => List(row.get(a), row.get(b), row.get(c), row.get(d), row.get(e)))
    }
  }

  protected def lastIndex2(
    conn: Conn,
    rowsGe: util.ArrayList[jList[AnyRef]], // Rows equal to or greater than sort values
    totalCount: Int,
    sortIndexes: List[Int],
    lastIndex: Int,
    lastRow: String,
    prevT: Long,
    lastSortValues: List[String],
  )(implicit ec: ExecutionContext): Future[Int] = {
    if (lastIndex < totalCount && rowsGe.get(lastIndex).toString == lastRow) {
      // 1. Found last cursor row at last index (nothing changed)
      Future(lastIndex)

    } else {
      // Try to find cursor row within same sort values window
      val curWindow                          = new util.ArrayList[AnyRef]()
      val (initialSortValues, getSortValues) = sortValuesLambda(sortIndexes, rowsGe.get(0))

      @tailrec
      def collectSameSortValues(i: Int, row: jList[AnyRef], prevSortValues: List[AnyRef]): Int = getSortValues(row) match {
        case _ if row.toString == lastRow => i // cursor row was found at index i
        case `prevSortValues`             => // same sort values
          curWindow.add(row)
          collectSameSortValues(i + 1, rowsGe.get(i + 1), prevSortValues)
        case _                            => -1 // cursor row was not found
      }
      val curIndex = collectSameSortValues(0, rowsGe.get(0), initialSortValues)

      if (curIndex != -1) {
        // 2. Found last cursor row at curIndex
        Future(curIndex)

      } else {
        // Cursor row was retracted (delete/update)
        // Now try to find nearest previous row before cursor row in new window

        // Access rows before cursor row at time prevT when cursor row existed
        val model = getModelEq(lastSortValues)
        val query = Model2Query(model).get._1
        conn.usingAdhocDbView(AsOf(TxLong(prevT))).jvmQuery(model, query).map { prevWindowUnsorted =>
          val prevWindow = new util.ArrayList(prevWindowUnsorted)
          prevWindow.sort(this)
          val prevCursorIndex = {
            var i = 0
            while (prevWindow.get(i).toString != lastRow) {
              i += 1
            }
            i
          }

          val curLast = curWindow.size - 1

          def findPrevRowInCurrentWindow(prevRow: jList[AnyRef]): Int = {
            @tailrec
            def checkBackwards(curIndex: Int): Int = {
              if (curWindow.get(curIndex) == prevRow) {
                // 3. Previous row before cursor row was found in current window at curIndex
                curIndex
              } else if (curIndex == -1) {
                // No match
                -1
              } else {
                // Continue checking
                checkBackwards(curIndex - 1)
              }
            }
            checkBackwards(curLast)
          }

          @tailrec
          def loopPrevRowsBeforeCursorRow(prevIndex: Int): Int = {
            if (prevIndex == -1) {
              -1
            } else {
              findPrevRowInCurrentWindow(prevWindow.get(prevIndex)) match {
                case -1 =>
                  loopPrevRowsBeforeCursorRow(prevIndex - 1)
                case k  =>
                  k
              }
            }
          }

          loopPrevRowsBeforeCursorRow(prevCursorIndex - 1)
        }
      }
    }
  }


  protected def firstIndex2(
    conn: Conn,
    rows: util.ArrayList[jList[AnyRef]],
    totalCount: Int,
    sortIndexes: List[Int],
    firstIndex: Int,
    firstRow: String,
    prevT: Long,
    firstSortValues: List[String],
    limit: Int
  )(implicit ec: ExecutionContext): Future[Int] = {
    if (rows.get(firstIndex).toString == firstRow) {
      // 1. No change, continuing from where we left
      return Future(firstIndex)
    }

    // Window of rows we can look for before sort values change
    val sameSortValuesWindow = new util.ArrayList[AnyRef]()

    val (initialSortValues, sortValues) = sortValuesLambda(sortIndexes, rows.get(totalCount - 1))

    var prevSortValues = initialSortValues
    var curSortValues  = List.empty[AnyRef]

    // Check rows starting from rows with same sort values as last cursor row
    var row: jList[AnyRef] = null
    var i                  = totalCount - 1
    while (rows.get(i).toString != firstRow) {
      row = rows.get(i)
      sameSortValuesWindow.add(row)

      curSortValues = sortValues(row)
      if (prevSortValues == curSortValues) {
        // Continue within window of same sort values
        prevSortValues = curSortValues
      } else {
        // We didn't find cursor row within same sort values window
        // Now search for rows after previous last row as of previous t
        val model = getModelGe(true, Nil, firstSortValues)
        val query = Model2Query(model).get._1
        conn.usingAdhocDbView(AsOf(TxLong(prevT))).jvmQueryT(model, query).map { case (prevRowsAfter, t) =>
          val prevRowsAfterSorted: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(prevRowsAfter)
          prevRowsAfterSorted.sort(this)
          val last = sameSortValuesWindow.size
          var j    = 0
          while (j != limit) {
            val prevRowAfter = prevRowsAfterSorted.get(j)
            i = 0
            while (i != last) {
              if (sameSortValuesWindow.get(i) == prevRowAfter) {
                // 3. Found row after cursor from previous page in new rows
                return Future(i)
              }
              i += 1
            }
            j += 1
          }
          i
        }
        // Nothing matched. What then?

        return Future(i)
      }
      i += 1
    }
    // 2. Found last cursor row at index i
    Future(i)
  }

  protected def firstIndex3(
    conn: Conn,
    model: Model,
    query: Query,
    prevT: Long,
    totalCount: Int,
    sortedRows: util.ArrayList[jList[AnyRef]],
    prevFirstRow: String,
    lookupThreshold: Int
  )(implicit ec: ExecutionContext): Future[Int] = {
    val threshold = lookupThreshold.min(totalCount)
    val window    = new util.ArrayList[String](threshold)

    // Walk backward to find row index of previous first row
    var i     = totalCount - 1
    val lower = i - lookupThreshold
    while (sortedRows.get(i).toString != prevFirstRow && i != lower) {
      i -= 1
    }

    if (i == threshold) {
      conn.usingAdhocDbView(AsOf(TxLong(prevT))).jvmQueryT(model, query).map { case (prevRows, t) =>
        val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(prevRows)
        sortedRows.sort(this)
        prevRows.forEach { prevRow =>
          val prevRowStr = prevRow.toString
          i = 0
          while (window.get(1) != prevRowStr && i != threshold) {
            i += 1
          }
          if (i != threshold) {
            return Future(i)
          }
        }
        i
      }
    } else {
      Future(i)
    }
  }

  protected def extract(cursor: String, forward: Boolean)
  : (Model, Query, Long, Int, Int, String, String, List[Int], List[String], List[String]) = {
    if (cursor.isEmpty) {
      val sortIndexes = sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").map(_.i)
      (_model, _query, 0, 0, 0, "", "", sortIndexes, Nil, Nil)
    } else {
      val (prevT, firstIndex, lastIndex, firstRow, lastRow, sortIndexes, firstSortValues, lastSortValues) = decode(cursor)

      val model = getModelGe(forward, firstSortValues, lastSortValues)
      val query = Model2Query(model).get._1
      (model, query, prevT, firstIndex, lastIndex, firstRow, lastRow, sortIndexes, firstSortValues, lastSortValues)
    }
  }

  def oscillate(size: Int, i: Int): List[Int] = {
    val l = (0 until size).toList
    i match {
      case 0                  => l
      case _ if i + 1 == size => l.reverse
      case _                  => List(i) ++ l.tail.flatMap(j => List(i - j, i + j)).filter(v => 0 <= v && v < size)
    }
  }

}
