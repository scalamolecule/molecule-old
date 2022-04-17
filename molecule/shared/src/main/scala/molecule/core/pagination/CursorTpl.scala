package molecule.core.pagination

import java.util.{Base64, List => jList}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import scala.concurrent.{ExecutionContext, Future}


trait CursorTpl[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  protected def cursorRows2tuples(
    conn: Conn,
    limit: Int,
    cursor: String
  )(implicit ec: ExecutionContext): Future[(List[Tpl], String, Int)] = {
    val tuples = List.newBuilder[Tpl]
    val log    = new log
    log("=========================================================================")
    if (cursor.isEmpty) {

      conn.jvmQuery(_model, _query).map { rows =>
        val totalCount                                     = rows.size
        val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
        sortedRows.sort(this) // using macro-implemented `compare` method

        log(_model)
        sortedRows.forEach(row => log(row))
        log("----------------- first")

        var i     = 0
        val until = limit.min(totalCount)
        while (i != limit) {
          tuples += row2tpl(sortedRows.get(i))
          i += 1
        }
        val first           = sortedRows.get(0)
        val last            = sortedRows.get(i - 1)
        val firstSortValues = sortCoordinates.head.map(sc => first.get(sc.i).toString)
        val lastSortValues  = sortCoordinates.head.map(sc => last.get(sc.i).toString)
        val firstRow        = first.toString
        val lastRow         = last.toString
        val more            = totalCount - limit.min(totalCount)

        log("from    : 0")
        log("until   : " + until)
        log("firstRow: " + firstRow)
        log("lastRow : " + lastRow)
        //      log("firstSortValues: " + firstSortValues)
        //      log("lastSortValues : " + lastSortValues)
        log.print()

        val encodedCursor = encode(0, until, firstRow, lastRow, firstSortValues, lastSortValues)
        (tuples.result(), encodedCursor, more)
      }

    } else {
      val (from, until, prevFirstRow, prevLastRow, prevFirstSortValues, prevLastSortValues) = decode(cursor)

      val forward     = limit > 0
      val offsetModel = getOffsetModel(forward, prevFirstSortValues, prevLastSortValues)

      conn.jvmQuery(offsetModel, Model2Query(offsetModel).get._1).map { rows =>

        log(offsetModel)

        val totalCount = rows.size
        if (totalCount == 0) {
          (List.empty[Tpl], ",", 0)
        } else if (totalCount == 1) {
          (List(row2tpl(rows.iterator.next)), ",", 0)

        } else {
          val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
          sortedRows.sort(this) // using macro-implemented `compare` method

          sortedRows.forEach(row => log(row))

          val (newCursor, more) = if (forward) {
            log("----------------- A -->")

            // Walk forward to find row index of previous last row
            var i = 0
            while (sortedRows.get(i).toString != prevLastRow && i != limit) {
              i += 1
            }
            val from  = i + 1 // Go from the row after
            val until = (from + limit).min(totalCount)

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
            val more            = totalCount - i

            log("firstRow: " + firstRow)
            log("lastRow : " + lastRow)
            //      log("firstSortValues: " + firstSortValues)
            //      log("lastSortValues : " + lastSortValues)
            log.print()

            (newCursor, more)

          } else {
            log("----------------- A <--")

            // Walk backwards to find row index of previous first row
            var i     = totalCount - 1
            val lower = i + limit // (limit is negative)
            while (sortedRows.get(i).toString != prevFirstRow && i != lower) {
              i -= 1
            }
            val until = i // Go from the row before
            val from  = (until + limit).max(0) // (limit is negative)

            log("from    : " + from)
            log("until   : " + until)

            i = from
            while (i != until) {
              tuples += row2tpl(sortedRows.get(i))
              i += 1
            }
            val first           = sortedRows.get(from)
            val last            = sortedRows.get(until)
            val firstRow        = first.toString
            val lastRow         = last.toString
            val firstSortValues = sortCoordinates.head.map(sc => first.get(sc.i).toString)
            val lastSortValues  = sortCoordinates.head.map(sc => last.get(sc.i).toString)
            val newCursor       = encode(from, until, firstRow, lastRow, firstSortValues, lastSortValues)
            val more            = (totalCount - from).min(from)

            log("firstRow: " + firstRow)
            log("lastRow : " + lastRow)
            //      log("firstSortValues: " + firstSortValues)
            //      log("lastSortValues : " + lastSortValues)
            log.print()

            (newCursor, more)
          }
          (tuples.result(), newCursor, more)
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
