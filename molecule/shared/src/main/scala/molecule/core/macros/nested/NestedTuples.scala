package molecule.core.macros.nested

import java.lang.{Long => jLong}
import java.util
import java.util.{List => jList}
import molecule.core.api.Molecule_0
import molecule.core.pagination.CursorTpl
import molecule.datomic.base.facade.Conn
import scala.concurrent.{ExecutionContext, Future}


/** Nested tuple builder classes of various levels. */
trait NestedTuples[Obj, Tpl] extends NestedBase[Obj, Tpl] with CursorTpl[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

  protected def tplBranch0(row: jList[AnyRef], leaf: List[Any]): Tpl = ???
  protected def tplBranch1(row: jList[AnyRef], leaf: List[Any]): Any = ???
  protected def tplBranch2(row: jList[AnyRef], leaf: List[Any]): Any = ???
  protected def tplBranch3(row: jList[AnyRef], leaf: List[Any]): Any = ???
  protected def tplBranch4(row: jList[AnyRef], leaf: List[Any]): Any = ???
  protected def tplBranch5(row: jList[AnyRef], leaf: List[Any]): Any = ???
  protected def tplBranch6(row: jList[AnyRef], leaf: List[Any]): Any = ???

  protected def tplLeaf1(row: jList[AnyRef]): Any = ???
  protected def tplLeaf2(row: jList[AnyRef]): Any = ???
  protected def tplLeaf3(row: jList[AnyRef]): Any = ???
  protected def tplLeaf4(row: jList[AnyRef]): Any = ???
  protected def tplLeaf5(row: jList[AnyRef]): Any = ???
  protected def tplLeaf6(row: jList[AnyRef]): Any = ???
  protected def tplLeaf7(row: jList[AnyRef]): Any = ???

  protected var acc0: List[Tpl] = List.empty[Tpl]
  protected var acc1: List[Any] = List.empty[Any]
  protected var acc2: List[Any] = List.empty[Any]
  protected var acc3: List[Any] = List.empty[Any]
  protected var acc4: List[Any] = List.empty[Any]
  protected var acc5: List[Any] = List.empty[Any]
  protected var acc6: List[Any] = List.empty[Any]
  protected var acc7: List[Any] = List.empty[Any]

  protected def resetCastVars(): Unit = {
    resetVars()

    acc0 = Nil
    acc1 = Nil
    acc2 = Nil
    acc3 = Nil
    acc4 = Nil
    acc5 = Nil
    acc6 = Nil
    acc7 = Nil

    // Traverse backwards through rows (default setting for tuples - json sorts ascending)
    isNestedTuples = true
    nextRow = false
    i = 0
  }

  // Nested objects api ...............................

  // Let tuples `get` do the heavy lifting of sorting, pagination and casting
  // `outerTpl2obj` is generated by macro
  final override def getObjs(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] = {
    get.map(_.map(outerTpl2obj))
  }

  final override def getObjs(limit: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] = {
    get(limit).map(_.map(outerTpl2obj))
  }

  final override def getObjs(limit: Int, offset: Int)
                            (implicit conn: Future[Conn], ec: ExecutionContext): Future[(List[Obj], Int)] = {
    get(limit, offset).map { case (rows, totalCount) => (rows.map(outerTpl2obj), totalCount) }
  }


  // Nested tuples api ................................

  final override def get(implicit futConn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    getNestedTpls(0, 0).map(_._1)
  }

  final override def get(limit: Int)(implicit futConn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    if (limit == 0) {
      limit0exception
    } else {
      getNestedTpls(limit, 0).map(_._1)
    }
  }

  final override def get(limit: Int, offset: Int)
                        (implicit futConn: Future[Conn], ec: ExecutionContext): Future[(List[Tpl], Int)] = {
    if (limit == 0) {
      limit0exception
    } else if (offset < 0) {
      offsetException(offset)
    } else {
      getNestedTpls(limit, offset)
    }
  }

  final override def get(limit: Int, cursor: String)
                        (implicit futConn: Future[Conn], ec: ExecutionContext): Future[(List[Tpl], String, Int)] = {
    if (limit == 0) {
      limit0exception
    } else if (!sortRows) {
      notSortedException
    } else {
      _inputThrowable.fold {
        resetCastVars()
        for {
          conn <- futConn
          (selectedRows, newCursor, totalCount) <- selectedNestedTplRows(conn, limit, cursor)
        } yield {
          val flatCount = selectedRows.size
          val tuples    = if (flatCount == 0) {
            List.empty[Tpl]
          } else {
            flat2nested(selectedRows, flatCount)
          }
          (tuples, newCursor, totalCount)
        }
      }(Future.failed) // Wrap exception from input failure in Future
    }
  }


  // Helpers .........................................

  // Generated by macro
  def outerTpl2obj(outerTpl: Tpl): Obj = ???

  final private def getNestedTpls(limit: Int, offset: Int)
                                 (implicit futConn: Future[Conn], ec: ExecutionContext): Future[(List[Tpl], Int)] = {
    for {
      conn <- futConn
      rows <- conn.jvmQuery(_model, _query)
    } yield {
      resetCastVars()
      val sortedRows: util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
      sortedRows.sort(this)
      val flatCount                  = sortedRows.size
      val (selectedRows, totalCount) = sortedRows2selectedRows(sortedRows, limit, offset)
      val tuples                     = if (flatCount == 0 || offset >= totalCount) {
        List.empty[Tpl]
      } else {
        flat2nested(selectedRows, flatCount)
      }
      (tuples, totalCount)
    }
  }
}


object NestedTuples {

  trait NestedTuples1[Obj, Tpl] extends NestedTuples[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2nested(rows: util.ArrayList[jList[AnyRef]], flatCount: Int): List[Tpl] = {
      if (flatCount == 1) {
        row = rows.get(0)
        List(tplBranch0(row,
          List(tplLeaf1(row))))

      } else {
        // Building Lists backwards for speed
        // https://www.lihaoyi.com/post/BenchmarkingScalaCollections.html#construction-performance
        i = rows.size - 1
        while (i != -1) {
          row = rows.get(i) // Fast array lookup
          e0 = row.get(0).asInstanceOf[jLong]
          if (nextRow) {
            if (i == 0) {
              if (e0 != p0) {
                acc0 = tplBranch0(prevRow, acc1) :: acc0

                acc1 = List(tplLeaf1(row))
                acc0 = tplBranch0(row, acc1) :: acc0

              } else /* e1 != p1 */ {
                acc1 = tplLeaf1(row) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0
              }

            } else if (e0 != p0) {
              acc0 = tplBranch0(prevRow, acc1) :: acc0

              acc1 = List(tplLeaf1(row))

            } else /* e1 != p1 */ {
              acc1 = tplLeaf1(row) :: acc1
            }
          } else {
            acc1 = List(tplLeaf1(row))
            nextRow = true
          }
          prevRow = row
          p0 = e0
          i -= 1
        }
        acc0
      }
    }
  }


  trait NestedTuples2[Obj, Tpl] extends NestedTuples[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2nested(rows: util.ArrayList[jList[AnyRef]], flatCount: Int): List[Tpl] = {
      if (flatCount == 1) {
        row = rows.get(0)
        List(tplBranch0(row,
          List(tplBranch1(row,
            List(tplLeaf2(row))))))

      } else {
        i = rows.size - 1
        while (i != -1) {
          row = rows.get(i)
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]

          if (nextRow) {
            if (i == 0) {
              if (e0 != p0) {
                acc1 = tplBranch1(prevRow, acc2) :: acc1
                acc0 = tplBranch0(prevRow, acc1) :: acc0

                acc2 = List(tplLeaf2(row))
                acc1 = List(tplBranch1(row, acc2))
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e1 != p1) {
                acc1 = tplBranch1(prevRow, acc2) :: acc1

                acc2 = List(tplLeaf2(row))
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else /* e2 != p2 */ {
                acc2 = tplLeaf2(row) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0
              }

            } else if (e0 != p0) {
              acc1 = tplBranch1(prevRow, acc2) :: acc1
              acc0 = tplBranch0(prevRow, acc1) :: acc0

              acc2 = List(tplLeaf2(row))
              acc1 = Nil

            } else if (e1 != p1) {
              acc1 = tplBranch1(prevRow, acc2) :: acc1

              acc2 = List(tplLeaf2(row))

            } else /* e2 != p2 */ {
              acc2 = tplLeaf2(row) :: acc2
            }
          } else {
            acc2 = List(tplLeaf2(row))
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          i -= 1
        }
        acc0
      }
    }
  }


  trait NestedTuples3[Obj, Tpl] extends NestedTuples[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2nested(rows: util.ArrayList[jList[AnyRef]], flatCount: Int): List[Tpl] = {
      if (flatCount == 1) {
        row = rows.get(0)
        List(tplBranch0(row,
          List(tplBranch1(row,
            List(tplBranch2(row,
              List(tplLeaf3(row))))))))

      } else {
        i = rows.size - 1
        while (i != -1) {
          row = rows.get(i)
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]

          if (nextRow) {
            if (i == 0) {
              if (e0 != p0) {
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1
                acc0 = tplBranch0(prevRow, acc1) :: acc0

                acc3 = List(tplLeaf3(row))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = List(tplBranch1(row, acc2))
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e1 != p1) {
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1

                acc3 = List(tplLeaf3(row))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e2 != p2) {
                acc2 = tplBranch2(prevRow, acc3) :: acc2

                acc3 = List(tplLeaf3(row))
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else /* e3 != p3 */ {
                acc3 = tplLeaf3(row) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0
              }

            } else if (e0 != p0) {
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1
              acc0 = tplBranch0(prevRow, acc1) :: acc0

              acc3 = List(tplLeaf3(row))
              acc2 = Nil
              acc1 = Nil

            } else if (e1 != p1) {
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1

              acc3 = List(tplLeaf3(row))
              acc2 = Nil

            } else if (e2 != p2) {
              acc2 = tplBranch2(prevRow, acc3) :: acc2

              acc3 = List(tplLeaf3(row))

            } else /* e3 != p3 */ {
              acc3 = tplLeaf3(row) :: acc3
            }
          } else {
            acc3 = List(tplLeaf3(row))
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
          i -= 1
        }
        acc0
      }
    }
  }


  trait NestedTuples4[Obj, Tpl] extends NestedTuples[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2nested(rows: util.ArrayList[jList[AnyRef]], flatCount: Int): List[Tpl] = {
      if (flatCount == 1) {
        row = rows.get(0)
        List(tplBranch0(row,
          List(tplBranch1(row,
            List(tplBranch2(row,
              List(tplBranch3(row,
                List(tplLeaf4(row))))))))))

      } else {
        i = rows.size - 1
        while (i != -1) {
          row = rows.get(i)
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]
          e3 = row.get(3).asInstanceOf[jLong]

          if (nextRow) {
            if (i == 0) {
              if (e0 != p0) {
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1
                acc0 = tplBranch0(prevRow, acc1) :: acc0

                acc4 = List(tplLeaf4(row))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = List(tplBranch1(row, acc2))
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e1 != p1) {
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1

                acc4 = List(tplLeaf4(row))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e2 != p2) {
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2

                acc4 = List(tplLeaf4(row))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e3 != p3) {
                acc3 = tplBranch3(prevRow, acc4) :: acc3

                acc4 = List(tplLeaf4(row))
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else /* e4 != p4 */ {
                acc4 = tplLeaf4(row) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0
              }

            } else if (e0 != p0) {
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1
              acc0 = tplBranch0(prevRow, acc1) :: acc0

              acc4 = List(tplLeaf4(row))
              acc3 = Nil
              acc2 = Nil
              acc1 = Nil

            } else if (e1 != p1) {
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1

              acc4 = List(tplLeaf4(row))
              acc3 = Nil
              acc2 = Nil

            } else if (e2 != p2) {
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2

              acc4 = List(tplLeaf4(row))
              acc3 = Nil

            } else if (e3 != p3) {
              acc3 = tplBranch3(prevRow, acc4) :: acc3

              acc4 = List(tplLeaf4(row))

            } else /* e4 != p4 */ {
              acc4 = tplLeaf4(row) :: acc4
            }
          } else {
            acc4 = List(tplLeaf4(row))
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
          p3 = e3
          i -= 1
        }
        acc0
      }
    }
  }


  trait NestedTuples5[Obj, Tpl] extends NestedTuples[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2nested(rows: util.ArrayList[jList[AnyRef]], flatCount: Int): List[Tpl] = {
      if (flatCount == 1) {
        row = rows.get(0)
        List(tplBranch0(row,
          List(tplBranch1(row,
            List(tplBranch2(row,
              List(tplBranch3(row,
                List(tplBranch4(row,
                  List(tplLeaf5(row))))))))))))

      } else {
        i = rows.size - 1
        while (i != -1) {
          row = rows.get(i)
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]
          e3 = row.get(3).asInstanceOf[jLong]
          e4 = row.get(4).asInstanceOf[jLong]

          if (nextRow) {
            if (i == 0) {
              if (e0 != p0) {
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1
                acc0 = tplBranch0(prevRow, acc1) :: acc0

                acc5 = List(tplLeaf5(row))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = List(tplBranch1(row, acc2))
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e1 != p1) {
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1

                acc5 = List(tplLeaf5(row))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e2 != p2) {
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2

                acc5 = List(tplLeaf5(row))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e3 != p3) {
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3

                acc5 = List(tplLeaf5(row))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e4 != p4) {
                acc4 = tplBranch4(prevRow, acc5) :: acc4

                acc5 = List(tplLeaf5(row))
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else /* e5 != p5 */ {
                acc5 = tplLeaf5(row) :: acc5
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0
              }

            } else if (e0 != p0) {
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1
              acc0 = tplBranch0(prevRow, acc1) :: acc0

              acc5 = List(tplLeaf5(row))
              acc4 = Nil
              acc3 = Nil
              acc2 = Nil
              acc1 = Nil

            } else if (e1 != p1) {
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1

              acc5 = List(tplLeaf5(row))
              acc4 = Nil
              acc3 = Nil
              acc2 = Nil

            } else if (e2 != p2) {
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2

              acc5 = List(tplLeaf5(row))
              acc4 = Nil
              acc3 = Nil

            } else if (e3 != p3) {
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3

              acc5 = List(tplLeaf5(row))
              acc4 = Nil

            } else if (e4 != p4) {
              acc4 = tplBranch4(prevRow, acc5) :: acc4

              acc5 = List(tplLeaf5(row))

            } else /* e5 != p5 */ {
              acc5 = tplLeaf5(row) :: acc5
            }
          } else {
            acc5 = List(tplLeaf5(row))
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
          p3 = e3
          p4 = e4
          i -= 1
        }
        acc0
      }
    }
  }


  trait NestedTuples6[Obj, Tpl] extends NestedTuples[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2nested(rows: util.ArrayList[jList[AnyRef]], flatCount: Int): List[Tpl] = {
      if (flatCount == 1) {
        row = rows.get(0)
        List(tplBranch0(row,
          List(tplBranch1(row,
            List(tplBranch2(row,
              List(tplBranch3(row,
                List(tplBranch4(row,
                  List(tplBranch5(row,
                    List(tplLeaf6(row))))))))))))))

      } else {
        i = rows.size - 1
        while (i != -1) {
          row = rows.get(i)
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]
          e3 = row.get(3).asInstanceOf[jLong]
          e4 = row.get(4).asInstanceOf[jLong]
          e5 = row.get(5).asInstanceOf[jLong]

          if (nextRow) {
            if (i == 0) {
              if (e0 != p0) {
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1
                acc0 = tplBranch0(prevRow, acc1) :: acc0

                acc6 = List(tplLeaf6(row))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = List(tplBranch1(row, acc2))
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e1 != p1) {
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1

                acc6 = List(tplLeaf6(row))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e2 != p2) {
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2

                acc6 = List(tplLeaf6(row))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e3 != p3) {
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3

                acc6 = List(tplLeaf6(row))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e4 != p4) {
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4

                acc6 = List(tplLeaf6(row))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e5 != p5) {
                acc5 = tplBranch5(prevRow, acc6) :: acc5

                acc6 = List(tplLeaf6(row))
                acc5 = tplBranch5(row, acc6) :: acc5
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else /* e6 != p6 */ {
                acc6 = tplLeaf6(row) :: acc6
                acc5 = tplBranch5(row, acc6) :: acc5
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0
              }

            } else if (e0 != p0) {
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1
              acc0 = tplBranch0(prevRow, acc1) :: acc0

              acc6 = List(tplLeaf6(row))
              acc5 = Nil
              acc4 = Nil
              acc3 = Nil
              acc2 = Nil
              acc1 = Nil

            } else if (e1 != p1) {
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1

              acc6 = List(tplLeaf6(row))
              acc5 = Nil
              acc4 = Nil
              acc3 = Nil
              acc2 = Nil

            } else if (e2 != p2) {
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2

              acc6 = List(tplLeaf6(row))
              acc5 = Nil
              acc4 = Nil
              acc3 = Nil

            } else if (e3 != p3) {
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3

              acc6 = List(tplLeaf6(row))
              acc5 = Nil
              acc4 = Nil

            } else if (e4 != p4) {
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4

              acc6 = List(tplLeaf6(row))
              acc5 = Nil

            } else if (e5 != p5) {
              acc5 = tplBranch5(prevRow, acc6) :: acc5

              acc6 = List(tplLeaf6(row))

            } else /* e6 != p6 */ {
              acc6 = tplLeaf6(row) :: acc6
            }
          } else {
            acc6 = List(tplLeaf6(row))
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
          p3 = e3
          p4 = e4
          p5 = e5
          i -= 1
        }
        acc0
      }
    }
  }


  trait NestedTuples7[Obj, Tpl] extends NestedTuples[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2nested(rows: util.ArrayList[jList[AnyRef]], flatCount: Int): List[Tpl] = {
      if (flatCount == 1) {
        row = rows.get(0)
        List(tplBranch0(row,
          List(tplBranch1(row,
            List(tplBranch2(row,
              List(tplBranch3(row,
                List(tplBranch4(row,
                  List(tplBranch5(row,
                    List(tplBranch6(row,
                      List(tplLeaf7(row))))))))))))))))

      } else {
        i = rows.size - 1
        while (i != -1) {
          row = rows.get(i)
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]
          e3 = row.get(3).asInstanceOf[jLong]
          e4 = row.get(4).asInstanceOf[jLong]
          e5 = row.get(5).asInstanceOf[jLong]
          e6 = row.get(6).asInstanceOf[jLong]

          if (nextRow) {
            if (i == 0) {
              if (e0 != p0) {
                acc6 = tplBranch6(prevRow, acc7) :: acc6
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1
                acc0 = tplBranch0(prevRow, acc1) :: acc0

                acc7 = List(tplLeaf7(row))
                acc6 = List(tplBranch6(row, acc7))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = List(tplBranch1(row, acc2))
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e1 != p1) {
                acc6 = tplBranch6(prevRow, acc7) :: acc6
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2
                acc1 = tplBranch1(prevRow, acc2) :: acc1

                acc7 = List(tplLeaf7(row))
                acc6 = List(tplBranch6(row, acc7))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = List(tplBranch2(row, acc3))
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e2 != p2) {
                acc6 = tplBranch6(prevRow, acc7) :: acc6
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3
                acc2 = tplBranch2(prevRow, acc3) :: acc2

                acc7 = List(tplLeaf7(row))
                acc6 = List(tplBranch6(row, acc7))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = List(tplBranch3(row, acc4))
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e3 != p3) {
                acc6 = tplBranch6(prevRow, acc7) :: acc6
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4
                acc3 = tplBranch3(prevRow, acc4) :: acc3

                acc7 = List(tplLeaf7(row))
                acc6 = List(tplBranch6(row, acc7))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = List(tplBranch4(row, acc5))
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e4 != p4) {
                acc6 = tplBranch6(prevRow, acc7) :: acc6
                acc5 = tplBranch5(prevRow, acc6) :: acc5
                acc4 = tplBranch4(prevRow, acc5) :: acc4

                acc7 = List(tplLeaf7(row))
                acc6 = List(tplBranch6(row, acc7))
                acc5 = List(tplBranch5(row, acc6))
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e5 != p5) {
                acc6 = tplBranch6(prevRow, acc7) :: acc6
                acc5 = tplBranch5(prevRow, acc6) :: acc5

                acc7 = List(tplLeaf7(row))
                acc6 = List(tplBranch6(row, acc7))
                acc5 = tplBranch5(row, acc6) :: acc5
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else if (e6 != p6) {
                acc6 = tplBranch6(prevRow, acc7) :: acc6

                acc7 = List(tplLeaf7(row))
                acc6 = tplBranch6(row, acc7) :: acc6
                acc5 = tplBranch5(row, acc6) :: acc5
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0

              } else /* e7 != p7 */ {
                acc7 = tplLeaf7(row) :: acc7
                acc6 = tplBranch6(row, acc7) :: acc6
                acc5 = tplBranch5(row, acc6) :: acc5
                acc4 = tplBranch4(row, acc5) :: acc4
                acc3 = tplBranch3(row, acc4) :: acc3
                acc2 = tplBranch2(row, acc3) :: acc2
                acc1 = tplBranch1(row, acc2) :: acc1
                acc0 = tplBranch0(row, acc1) :: acc0
              }

            } else if (e0 != p0) {
              acc6 = tplBranch6(prevRow, acc7) :: acc6
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1
              acc0 = tplBranch0(prevRow, acc1) :: acc0

              acc7 = List(tplLeaf7(row))
              acc6 = Nil
              acc5 = Nil
              acc4 = Nil
              acc3 = Nil
              acc2 = Nil
              acc1 = Nil

            } else if (e1 != p1) {
              acc6 = tplBranch6(prevRow, acc7) :: acc6
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2
              acc1 = tplBranch1(prevRow, acc2) :: acc1

              acc7 = List(tplLeaf7(row))
              acc6 = Nil
              acc5 = Nil
              acc4 = Nil
              acc3 = Nil
              acc2 = Nil

            } else if (e2 != p2) {
              acc6 = tplBranch6(prevRow, acc7) :: acc6
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3
              acc2 = tplBranch2(prevRow, acc3) :: acc2

              acc7 = List(tplLeaf7(row))
              acc6 = Nil
              acc5 = Nil
              acc4 = Nil
              acc3 = Nil

            } else if (e3 != p3) {
              acc6 = tplBranch6(prevRow, acc7) :: acc6
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4
              acc3 = tplBranch3(prevRow, acc4) :: acc3

              acc7 = List(tplLeaf7(row))
              acc6 = Nil
              acc5 = Nil
              acc4 = Nil

            } else if (e4 != p4) {
              acc6 = tplBranch6(prevRow, acc7) :: acc6
              acc5 = tplBranch5(prevRow, acc6) :: acc5
              acc4 = tplBranch4(prevRow, acc5) :: acc4

              acc7 = List(tplLeaf7(row))
              acc6 = Nil
              acc5 = Nil

            } else if (e5 != p5) {
              acc6 = tplBranch6(prevRow, acc7) :: acc6
              acc5 = tplBranch5(prevRow, acc6) :: acc5

              acc7 = List(tplLeaf7(row))
              acc6 = Nil

            } else if (e6 != p6) {
              acc6 = tplBranch6(prevRow, acc7) :: acc6

              acc7 = List(tplLeaf7(row))

            } else /* e7 != p7 */ {
              acc7 = tplLeaf7(row) :: acc7
            }
          } else {
            acc7 = List(tplLeaf7(row))
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
          p3 = e3
          p4 = e4
          p5 = e5
          p6 = e6
          i -= 1
        }
        acc0
      }
    }
  }
}
