package molecule.core.pagination

import java.net.URI
import java.util
import java.util.{Base64, Date, UUID, List => jList, Collection => jCollection}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.ast.dbView.{AsOf, TxLong}
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import scala.annotation.{nowarn, tailrec}
import scala.concurrent.{ExecutionContext, Future}


// Non-sorted values have non-deterministic order but will likely not change order between internal indexing jobs
// "Window" is rows sharing same sorted value(s) up until current cursor row (forwards or backwards)

trait CursorBase[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>


  final protected def getCursorIndex(
    conn: Conn,
    rows: util.ArrayList[jList[AnyRef]], // Rows less than or equal to sort values
    totalCount: Int,
    sortIndexes: List[Int],
    prevIndex: Int,
    cursorRow: String,
    t: Long,
    sortValues: List[String],
    initIndex: Int,
  )(implicit ec: ExecutionContext): Future[Int] = {
    if (prevIndex < totalCount && rows.get(prevIndex).toString == cursorRow) {
      // 1. Cursor row at same index
      Future(prevIndex)
    } else {
      val (otherIndex, curWindow) = getWindow(sortIndexes, rows, cursorRow, initIndex)
      if (otherIndex != -42) {
        // 2. Cursor row at other index
        Future(otherIndex)
      } else {
        // 3. Substitute cursor index (cursor row was retracted)
        if (initIndex == 0)
          substituteCursorIndexForward(conn, cursorRow, t, sortValues, curWindow)
        else
          substituteCursorIndexBackward(conn, cursorRow, t, sortValues, curWindow, totalCount)
      }
    }
  }

  private def getWindow(
    sortIndexes: List[Int],
    rows: util.ArrayList[jList[AnyRef]],
    cursorRow: String,
    initIndex: Int
  ): (Int, util.ArrayList[jList[AnyRef]]) = {
    // Try to find cursor row within same sort values window
    val curWindow                      = new util.ArrayList[jList[AnyRef]]()
    val (sortValuesRaw, getSortValues) = sortValuesLambda(sortIndexes, rows.get(initIndex))
    val forward                        = initIndex == 0

    @tailrec
    def collectSameSortValues(i: Int, row: jList[AnyRef]): Int = getSortValues(row) match {
      case _ if row.toString == cursorRow => i
      case `sortValuesRaw`                =>
        if (forward) {
          curWindow.add(row)
          collectSameSortValues(i + 1, rows.get(i + 1))
        } else {
          curWindow.add(0, row)
          collectSameSortValues(i - 1, rows.get(i - 1))
        }
      case _                              => -42 // cursor row was not found
    }
    val curIndex = collectSameSortValues(initIndex, rows.get(initIndex))
    (curIndex, curWindow)
  }

  private def substituteCursorIndexForward(
    conn: Conn,
    lastRow: String,
    t: Long,
    lastSortValues: List[String],
    curWindow: util.ArrayList[jList[AnyRef]]
  )(implicit ec: ExecutionContext): Future[Int] = {
    // Cursor row was retracted (delete/update)
    // Now try to find nearest previous row before cursor row in new window

    // Access rows before cursor row at time t when cursor row existed
    val model = getModelEq(lastSortValues)
    val query = Model2Query(model).get._1
    conn.usingAdhocDbView(AsOf(TxLong(t))).jvmQuery(model, query).map { prevWindowUnsorted =>
      val prevWindow = new util.ArrayList(prevWindowUnsorted)
      prevWindow.sort(this)
      val prevCursorIndex = {
        var i = -1
        do i += 1 while (prevWindow.get(i).toString != lastRow)
        i
      }

      def findPrevRowInCurrentWindow(prevRow: jList[AnyRef]): Int = {
        @tailrec
        def checkBackwards(indexInCurWindow: Int): Int = {
          if (indexInCurWindow == -1) {
            // No match
            -42
          } else if (curWindow.get(indexInCurWindow) == prevRow) {
            // 3. Previous row before cursor row was found in current window
            indexInCurWindow
          } else {
            // Continue checking
            checkBackwards(indexInCurWindow - 1)
          }
        }
        checkBackwards(curWindow.size - 1)
      }

      @tailrec
      def loopPrevRowsBeforeCursorRow(prevIndex: Int): Int = {
        if (prevIndex == -1) {
          -1
        } else {
          findPrevRowInCurrentWindow(prevWindow.get(prevIndex)) match {
            case -42              => loopPrevRowsBeforeCursorRow(prevIndex - 1)
            case indexInCurWindow => indexInCurWindow
          }
        }
      }
      loopPrevRowsBeforeCursorRow(prevCursorIndex - 1)
    }
  }


  final protected def substituteCursorIndexBackward(
    conn: Conn,
    firstRow: String,
    t: Long,
    firstSortValues: List[String],
    curWindow: util.ArrayList[jList[AnyRef]],
    totalCount: Int
  )(implicit ec: ExecutionContext): Future[Int] = {
    // Cursor row was retracted (delete/update)
    // Now try to find nearest previous row after cursor row in new window

    // Access rows after cursor row at time t when cursor row existed
    val model = getModelEq(firstSortValues)
    val query = Model2Query(model).get._1
    conn.usingAdhocDbView(AsOf(TxLong(t))).jvmQuery(model, query).map { prevWindowUnsorted =>
      val prevWindow = new util.ArrayList(prevWindowUnsorted)
      prevWindow.sort(this)
      val prevCursorIndex = {
        var i = -1
        do i += 1 while (prevWindow.get(i).toString != firstRow)
        i
      }

      val curWindowSize = curWindow.size
      def findPrevRowInCurrentWindow(prevRow: jList[AnyRef]): Int = {
        @tailrec
        def checkForwards(indexInCurWindow: Int): Int = {
          if (indexInCurWindow == curWindowSize) {
            // No match
            -42
          } else if (curWindow.get(indexInCurWindow) == prevRow) {
            // 3. Previous row after cursor row was found in current window
            indexInCurWindow
          } else {
            // Continue checking
            checkForwards(indexInCurWindow + 1)
          }
        }
        checkForwards(0)
      }

      val prevWindowSize = prevWindow.size
      @tailrec
      def loopPrevRowsAfterCursorRow(prevIndex: Int): Int = {
        if (prevIndex == prevWindowSize) {
          totalCount
        } else {
          findPrevRowInCurrentWindow(prevWindow.get(prevIndex)) match {
            case -42              => loopPrevRowsAfterCursorRow(prevIndex + 1)
            case indexInCurWindow => totalCount - curWindowSize + indexInCurWindow
          }
        }
      }
      loopPrevRowsAfterCursorRow(prevCursorIndex + 1)
    }
  }

  protected def resolveSubstituteCursor[U](
    conn: Conn,
    limit: Int,
    cursor: String,
    rowsUnsorted: jCollection[jList[AnyRef]],
    forward: Boolean,
    totalCount: Int,
    sortIndexes: List[Int],
    lastIndex: Int,
    firstIndex: Int,
    lastRow: String,
    firstRow: String,
    lastSortValues: List[String],
    firstSortValues: List[String],
    prevT: Long,
    t: Long,
    appender: (util.ArrayList[jList[AnyRef]], Int, Int) => U,
  )(implicit ec: ExecutionContext): Future[(U, String, Int)] = {
    val log = new log
    log("=========================================")

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

      val newFirst           = rows.get(from)
      val newLast            = rows.get(until - 1)
      val newFirstRow        = newFirst.toString
      val newLastRow         = newLast.toString
      val newFirstSortValues = sortValueStrings(newFirst)
      val newLastSortValues  = sortValueStrings(newLast)
      val result             = appender(rows, from, until)
      val newCursor          = encode(
        t, from, until - 1, newFirstRow, newLastRow, sortIndexes, newFirstSortValues, newLastSortValues
      )

      log("more    : " + more)
      log("firstRow: " + newFirstRow)
      log("lastRow : " + newLastRow)
      //            log.print()

      (result, newCursor, more)
    }
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
    val values = new String(Base64.getDecoder.decode(cursor)).split("__~~__", -1).toList

    //    println(values.mkString("  ", "\n  ", ""))
    val n                                                 = 5
    val List(t, firstIndex, lastIndex, firstRow, lastRow) = values.take(n)

    val s                = sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").size
    val (n0, n1, n2, n3) = (n, n + s, n + s * 2, n + s * 3)
    val sortIndexes      = values.slice(n0, n1).map(_.toInt)
    val firstSortValues  = values.slice(n1, n2)
    val lastSortValues   = values.slice(n2, n3)
    (t.toLong, firstIndex.toInt, lastIndex.toInt, firstRow, lastRow, sortIndexes, firstSortValues, lastSortValues)
  }


  final protected def sortValueStrings(row: jList[AnyRef]): List[String] = {
    sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").map { sc =>
      val v = row.get(sc.i)
      sc.tpe match {
        case "Date" => date2str(v.asInstanceOf[Date])
        case _      => v.toString
      }
    }
  }

  private def getTyped(tpe: String, v: String): Any = tpe match {
    case "String" | "enum" => v
    case "Int"             => v.toInt
    case "Long" | "ref"    => v.toLong
    case "Double"          => v.toDouble
    case "Boolean"         => v.toBoolean
    case "Date"            => str2date(v)
    case "UUID"            => UUID.fromString(v)
    case "URI"             => new URI(v)
    case "BigInt"          => BigInt(v)
    case "BigDecimal"      => BigDecimal(v)
    case other             => throw MoleculeException("Unexpected sort value type: " + other)
  }

  final protected def getModelGe(
    forward: Boolean,
    firstSortValues: List[String],
    lastSortValues: List[String]
  ): Model = {
    def setLimit(e: GenericAtom): Seq[Element] = {
      val (asc, i)   = (e.sort.head == 'a', e.sort.last.toString.toInt - 1)
      val sortValue  = if (forward) lastSortValues(i) else firstSortValues(i)
      val typedValue = getTyped(e.tpe, sortValue)
      val limitExpr  = if (forward && asc || !forward && !asc) Ge(typedValue) else Le(typedValue)
      e match {
        case a: Atom    => Seq(a, a.copy(attr = a.attr + "_", value = limitExpr, sort = ""))
        case g: Generic => Seq(g, g.copy(attr = g.attr + "_", value = limitExpr, sort = ""))
      }
    }
    def resolve(elements: Seq[Element]): Seq[Element] = elements.flatMap {
      case e: GenericAtom if e.sort.nonEmpty => setLimit(e)
      case e: GenericAtom                    => Seq(e)
      case Composite(elements)               => Seq(Composite(resolve(elements)))
      case TxMetaData(elements)              => Seq(TxMetaData(resolve(elements)))
      case e                                 => Seq(e)
    }
    Model(resolve(_model.elements))
  }

  final protected def getModelEq(sortValues: List[String]): Model = {
    def setEq(e: GenericAtom): Seq[Element] = {
      val sortValue  = sortValues(e.sort.last.toString.toInt - 1)
      val typedValue = getTyped(e.tpe, sortValue)
      val limitExpr  = Eq(Seq(typedValue))
      e match {
        case a: Atom    => Seq(a, a.copy(attr = a.attr + "_", value = limitExpr, sort = ""))
        case g: Generic => Seq(g, g.copy(attr = g.attr + "_", value = limitExpr, sort = ""))
      }
    }
    def resolve(elements: Seq[Element]): Seq[Element] = elements.flatMap {
      case e: GenericAtom if e.sort.nonEmpty => setEq(e)
      case e: GenericAtom                    => Seq(e)
      case Composite(elements)               => Seq(Composite(resolve(elements)))
      case TxMetaData(elements)              => Seq(TxMetaData(resolve(elements)))
      case e                                 => Seq(e)
    }
    Model(resolve(_model.elements))
  }

  final protected def extract(cursor: String, forward: Boolean)
  : (Model, Query, Long, Int, Int, String, String, List[Int], List[String], List[String]) = {
    if (cursor.isEmpty) {
      val sortIndexes = sortCoordinates.head.filterNot(_.attr == "NESTED INDEX").map(_.i)
      //      val sortIndexes = sortCoordinates.head.map(_.i)
      (_model, _query, 0, 0, 0, "", "", sortIndexes, Nil, Nil)
    } else {
      val (prevT, firstIndex, lastIndex, firstRow, lastRow, sortIndexes, firstSortValues, lastSortValues) = decode(cursor)

      val model = getModelGe(forward, firstSortValues, lastSortValues)
      val query = Model2Query(model).get._1
      (model, query, prevT, firstIndex, lastIndex, firstRow, lastRow, sortIndexes, firstSortValues, lastSortValues)
    }
  }
}
