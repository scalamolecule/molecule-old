package molecule.core.macros.nested

import java.lang.{Long => jLong}
import java.util
import java.util.{Collection => jCollection, List => jList}
import molecule.core.api.Molecule_0
import molecule.core.macros.rowAttr.JsonBase
import molecule.datomic.base.facade.Conn
import scala.concurrent.{ExecutionContext, Future}

/** Nested json builder classes of various levels. */
trait NestedJson[Obj, Tpl] extends NestedBase[Obj, Tpl] with JsonBase { self: Molecule_0[Obj, Tpl] =>

  protected def jsonBranch0(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = ???
  protected def jsonBranch1(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = ???
  protected def jsonBranch2(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = ???
  protected def jsonBranch3(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = ???
  protected def jsonBranch4(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = ???
  protected def jsonBranch5(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = ???
  protected def jsonBranch6(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = ???

  protected def jsonLeaf1(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???
  protected def jsonLeaf2(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???
  protected def jsonLeaf3(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???
  protected def jsonLeaf4(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???
  protected def jsonLeaf5(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???
  protected def jsonLeaf6(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???
  protected def jsonLeaf7(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???

  protected val sb0: StringBuffer = new StringBuffer()
  protected val sb1: StringBuffer = new StringBuffer()
  protected val sb2: StringBuffer = new StringBuffer()
  protected val sb3: StringBuffer = new StringBuffer()
  protected val sb4: StringBuffer = new StringBuffer()
  protected val sb5: StringBuffer = new StringBuffer()
  protected val sb6: StringBuffer = new StringBuffer()
  protected val sb7: StringBuffer = new StringBuffer()

  protected var firstLevel0      = true
  protected val firstJsonObjects = new Array[Boolean](7)

  protected var totalCount = 1

  protected def resetJsonVars(): Unit = {
    resetVars()

    // Traverse forward through rows (tuples require traversing backwards)
    isNestedTuples = false
    firstLevel0 = true

    sb0.setLength(0)
    sb0.append("[")
    sb1.setLength(0)
    sb2.setLength(0)
    sb3.setLength(0)
    sb4.setLength(0)
    sb5.setLength(0)
    sb6.setLength(0)
    sb7.setLength(0)

    for (lvl <- 0 to 6) {
      firstJsonObjects(lvl) = true
    }
  }

  def branch(
    level: Int,
    tabs: Int,
    tabsNested: Int,
    sb: StringBuffer,
    branchPairs: StringBuffer => StringBuffer,
    ref: String,
    leaf: StringBuffer,
    post: => StringBuffer = new StringBuffer()
  ): StringBuffer = {
    // Reset sub levels
    for (lvl <- (level + 1) to 6) {
      firstJsonObjects(lvl) = true
    }
    if (firstJsonObjects(level)) {
      firstJsonObjects(level) = false
    } else {
      // Prepare next outer object
      sb.append(",")
      sb.append(indent(tabs))
    }

    // Outer object
    sb.append("{")
    sb.append(indent(tabs + 1))

    // Pairs before nested
    branchPairs {
      val sb = new StringBuffer()

      // Ref
      quote(sb, ref)

      // Array of inner field/value pairs
      sb.append(": [")
      sb.append(indent(tabsNested))

      // Inner pairs
      sb.append(leaf)
      leaf.setLength(0)

      // End of inner pair array
      sb.append(indent(tabsNested - 1))
      sb.append("]")
    }

    // Post fields
    post

    // End of outer object
    sb.append(indent(tabs))
    sb.append("}")
  }


  def leaf(tabs: Int, sb: StringBuffer, leafPairs: StringBuffer => StringBuffer): StringBuffer = {
    if (sb.length > 0) {
      // Prepare next object
      sb.append(",")
      sb.append(indent(tabs))
    }
    // Start of object
    sb.append("{")
    sb.append(indent(tabs + 1))

    // Inner pairs
    leafPairs(new StringBuffer())

    // End of object
    sb.append(indent(tabs))
    sb.append("}")
  }

  def outerJson(totalCount: Int, limit: Int, offset: Int, sb: StringBuffer): String = {
    s"""{
       |  "totalCount": $totalCount,
       |  "limit"     : $limit,
       |  "offset"    : $offset,
       |  "data": {
       |    "${firstNs(_model)}": ${sb.toString}
       |  }
       |}""".stripMargin
  }


  // Pagination .............................

  final def getCountAndSelectedRowsAsc(
    allSortedRows: jCollection[jList[AnyRef]],
    limit: Int,
    offset: Int
  ): (jCollection[jList[AnyRef]], Int) = {
    var topRowIndex   = -1
    var curId : jLong = 0
    var prevId: jLong = 0
    val sortedRows    = if (limit == 0) {
      // All rows (offset is 0)
      allSortedRows.forEach { row =>
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        prevId = curId
      }
      allSortedRows

    } else {
      val flatTotalCount     = allSortedRows.size
      val rowArray           = new util.ArrayList[jList[AnyRef]](allSortedRows)
      val selectedSortedRows = new util.ArrayList[jList[AnyRef]]()
      var flatRowIndex       = 0
      val until               = if (limit == 0) Int.MaxValue else offset + limit
      var row: jList[AnyRef] = null
      while (flatRowIndex != flatTotalCount) {
        row = rowArray.get(flatRowIndex)
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        if (topRowIndex >= offset && topRowIndex < until) {
          selectedSortedRows.add(row)
        }
        prevId = curId
        flatRowIndex += 1
      }
      selectedSortedRows
    }
    (sortedRows, topRowIndex + 1)
  }

  final override def getJson(implicit futConn: Future[Conn], ec: ExecutionContext): Future[String] = {
    getNestedJson(0, 0)
  }

  final override def getJson(limit: Int)(implicit futConn: Future[Conn], ec: ExecutionContext): Future[String] = {
    getNestedJson(limit, 0)
  }

  final override def getJson(limit: Int, offset: Int)
                            (implicit futConn: Future[Conn], ec: ExecutionContext): Future[String] = {
    getNestedJson(limit, offset)
  }

  final private def getNestedJson(limit: Int, offset: Int)
                                 (implicit futConn: Future[Conn], ec: ExecutionContext): Future[String] = {
    for {
      conn <- futConn
      rows <- conn.jvmQuery(_model, _query)
    } yield {
      resetJsonVars()
      val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)
      sortedRows.sort(this)
      val flatCount                  = sortedRows.size
      val (selectedRows, totalCount) = getCountAndSelectedRowsAsc(sortedRows, limit, offset)
      if (flatCount == 0 || offset >= totalCount) {
        sb0.append("]")
      } else {
        flat2json(selectedRows, flatCount)
      }
      outerJson(totalCount, if (limit == 0) totalCount else limit, offset, sb0)
    }
  }
}


object NestedJson {

  trait NestedJson1[Obj, Tpl] extends NestedJson[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2json(selectedRows: jCollection[jList[AnyRef]], flatCount: Int): Unit = {
      if (flatCount == 1) {
        row = selectedRows.iterator.next
        sb0.append("\n      ")
        jsonBranch0(sb0, row,
          jsonLeaf1(sb1, row))
        sb0.append("\n    ]")

      } else {
        val lastFlat = selectedRows.size
        val it       = selectedRows.iterator
        sb0.append("\n      ")
        while (it.hasNext) {
          i += 1
          row = it.next
          e0 = row.get(0).asInstanceOf[jLong]

          if (nextRow) {
            if (e0 != p0) {
              jsonBranch0(sb0, prevRow, sb1)
            }
            jsonLeaf1(sb1, row)
            if (i == lastFlat) {
              jsonBranch0(sb0, row, sb1)
            }
          } else {
            jsonLeaf1(sb1, row)
            nextRow = true
          }

          prevRow = row
          p0 = e0
        }
        sb0.append("\n    ]")
      }
    }
  }


  trait NestedJson2[Obj, Tpl] extends NestedJson[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2json(selectedRows: jCollection[jList[AnyRef]], flatCount: Int): Unit = {
      if (flatCount == 1) {
        row = selectedRows.iterator.next
        sb0.append("\n      ")
        jsonBranch0(sb0, row,
          jsonBranch1(sb1, row,
            jsonLeaf2(sb2, row)))
        sb0.append("\n    ]")

      } else {
        val lastFlat = selectedRows.size
        val it       = selectedRows.iterator
        sb0.append("\n      ")
        while (it.hasNext) {
          i += 1
          row = it.next
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]

          if (nextRow) {
            if (e0 != p0) {
              jsonBranch1(sb1, prevRow, sb2)
              jsonBranch0(sb0, prevRow, sb1)
            } else if (e1 != p1) {
              jsonBranch1(sb1, prevRow, sb2)
            }
            jsonLeaf2(sb2, row)
            if (i == lastFlat) {
              jsonBranch1(sb1, row, sb2)
              jsonBranch0(sb0, row, sb1)
            }
          } else {
            jsonLeaf2(sb2, row)
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
        }
        sb0.append("\n    ]")
      }
    }
  }


  trait NestedJson3[Obj, Tpl] extends NestedJson[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2json(selectedRows: jCollection[jList[AnyRef]], flatCount: Int): Unit = {
      if (flatCount == 1) {
        row = selectedRows.iterator.next
        sb0.append("\n      ")
        jsonBranch0(sb0, row,
          jsonBranch1(sb1, row,
            jsonBranch2(sb2, row,
              jsonLeaf3(sb3, row))))
        sb0.append("\n    ]")

      } else {
        val lastFlat = selectedRows.size
        val it       = selectedRows.iterator
        sb0.append("\n      ")
        while (it.hasNext) {
          i += 1
          row = it.next
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]

          if (nextRow) {
            if (e0 != p0) {
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
              jsonBranch0(sb0, prevRow, sb1)
            } else if (e1 != p1) {
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
            } else if (e2 != p2) {
              jsonBranch2(sb2, prevRow, sb3)
            }
            jsonLeaf3(sb3, row)
            if (i == lastFlat) {
              jsonBranch2(sb2, row, sb3)
              jsonBranch1(sb1, row, sb2)
              jsonBranch0(sb0, row, sb1)
            }
          } else {
            jsonLeaf3(sb3, row)
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
        }
        sb0.append("\n    ]")
      }
    }
  }


  trait NestedJson4[Obj, Tpl] extends NestedJson[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2json(selectedRows: jCollection[jList[AnyRef]], flatCount: Int): Unit = {
      if (flatCount == 1) {
        row = selectedRows.iterator.next
        sb0.append("\n      ")
        jsonBranch0(sb0, row,
          jsonBranch1(sb1, row,
            jsonBranch2(sb2, row,
              jsonBranch3(sb3, row,
                jsonLeaf4(sb4, row)))))
        sb0.append("\n    ]")

      } else {
        val lastFlat = selectedRows.size
        val it       = selectedRows.iterator
        sb0.append("\n      ")
        while (it.hasNext) {
          i += 1
          row = it.next
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]
          e3 = row.get(3).asInstanceOf[jLong]

          if (nextRow) {
            if (e0 != p0) {
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
              jsonBranch0(sb0, prevRow, sb1)
            } else if (e1 != p1) {
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
            } else if (e2 != p2) {
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
            } else if (e3 != p3) {
              jsonBranch3(sb3, prevRow, sb4)
            }
            jsonLeaf4(sb4, row)
            if (i == lastFlat) {
              jsonBranch3(sb3, row, sb4)
              jsonBranch2(sb2, row, sb3)
              jsonBranch1(sb1, row, sb2)
              jsonBranch0(sb0, row, sb1)
            }
          } else {
            jsonLeaf4(sb4, row)
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
          p3 = e3
        }
        sb0.append("\n    ]")
      }
    }
  }


  trait NestedJson5[Obj, Tpl] extends NestedJson[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2json(selectedRows: jCollection[jList[AnyRef]], flatCount: Int): Unit = {
      if (flatCount == 1) {
        row = selectedRows.iterator.next
        sb0.append("\n      ")
        jsonBranch0(sb0, row,
          jsonBranch1(sb1, row,
            jsonBranch2(sb2, row,
              jsonBranch3(sb3, row,
                jsonBranch4(sb4, row,
                  jsonLeaf5(sb5, row))))))
        sb0.append("\n    ]")

      } else {
        val lastFlat = selectedRows.size
        val it       = selectedRows.iterator
        sb0.append("\n      ")
        while (it.hasNext) {
          i += 1
          row = it.next
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]
          e3 = row.get(3).asInstanceOf[jLong]
          e4 = row.get(4).asInstanceOf[jLong]

          if (nextRow) {
            if (e0 != p0) {
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
              jsonBranch0(sb0, prevRow, sb1)
            } else if (e1 != p1) {
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
            } else if (e2 != p2) {
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
            } else if (e3 != p3) {
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
            } else if (e4 != p4) {
              jsonBranch4(sb4, prevRow, sb5)
            }
            jsonLeaf5(sb5, row)
            if (i == lastFlat) {
              jsonBranch4(sb4, row, sb5)
              jsonBranch3(sb3, row, sb4)
              jsonBranch2(sb2, row, sb3)
              jsonBranch1(sb1, row, sb2)
              jsonBranch0(sb0, row, sb1)
            }
          } else {
            jsonLeaf5(sb5, row)
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
          p3 = e3
          p4 = e4
        }
        sb0.append("\n    ]")
      }
    }
  }


  trait NestedJson6[Obj, Tpl] extends NestedJson[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2json(selectedRows: jCollection[jList[AnyRef]], flatCount: Int): Unit = {
      if (flatCount == 1) {
        row = selectedRows.iterator.next
        sb0.append("\n      ")
        jsonBranch0(sb0, row,
          jsonBranch1(sb1, row,
            jsonBranch2(sb2, row,
              jsonBranch3(sb3, row,
                jsonBranch4(sb4, row,
                  jsonBranch5(sb5, row,
                    jsonLeaf6(sb6, row)))))))
        sb0.append("\n    ]")

      } else {
        val lastFlat = selectedRows.size
        val it       = selectedRows.iterator
        sb0.append("\n      ")
        while (it.hasNext) {
          i += 1
          row = it.next
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]
          e3 = row.get(3).asInstanceOf[jLong]
          e4 = row.get(4).asInstanceOf[jLong]
          e5 = row.get(5).asInstanceOf[jLong]

          if (nextRow) {
            if (e0 != p0) {
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
              jsonBranch0(sb0, prevRow, sb1)
            } else if (e1 != p1) {
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
            } else if (e2 != p2) {
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
            } else if (e3 != p3) {
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
            } else if (e4 != p4) {
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
            } else if (e5 != p5) {
              jsonBranch5(sb5, prevRow, sb6)
            }
            jsonLeaf6(sb6, row)
            if (i == lastFlat) {
              jsonBranch5(sb5, row, sb6)
              jsonBranch4(sb4, row, sb5)
              jsonBranch3(sb3, row, sb4)
              jsonBranch2(sb2, row, sb3)
              jsonBranch1(sb1, row, sb2)
              jsonBranch0(sb0, row, sb1)
            }
          } else {
            jsonLeaf6(sb6, row)
            nextRow = true
          }

          prevRow = row
          p0 = e0
          p1 = e1
          p2 = e2
          p3 = e3
          p4 = e4
          p5 = e5
        }
        sb0.append("\n    ]")
      }
    }
  }


  trait NestedJson7[Obj, Tpl] extends NestedJson[Obj, Tpl] { self: Molecule_0[Obj, Tpl] =>

    final override def flat2json(selectedRows: jCollection[jList[AnyRef]], flatCount: Int): Unit = {
      if (flatCount == 1) {
        row = selectedRows.iterator.next
        sb0.append("\n      ")
        jsonBranch0(sb0, row,
          jsonBranch1(sb1, row,
            jsonBranch2(sb2, row,
              jsonBranch3(sb3, row,
                jsonBranch4(sb4, row,
                  jsonBranch5(sb5, row,
                    jsonBranch6(sb6, row,
                      jsonLeaf7(sb7, row))))))))
        sb0.append("\n    ]")

      } else {
        val lastFlat = selectedRows.size
        val it       = selectedRows.iterator
        sb0.append("\n      ")
        while (it.hasNext) {
          i += 1
          row = it.next
          e0 = row.get(0).asInstanceOf[jLong]
          e1 = row.get(1).asInstanceOf[jLong]
          e2 = row.get(2).asInstanceOf[jLong]
          e3 = row.get(3).asInstanceOf[jLong]
          e4 = row.get(4).asInstanceOf[jLong]
          e5 = row.get(5).asInstanceOf[jLong]
          e6 = row.get(6).asInstanceOf[jLong]

          if (nextRow) {
            if (e0 != p0) {
              jsonBranch6(sb6, prevRow, sb7)
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
              jsonBranch0(sb0, prevRow, sb1)
            } else if (e1 != p1) {
              jsonBranch6(sb6, prevRow, sb7)
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
              jsonBranch1(sb1, prevRow, sb2)
            } else if (e2 != p2) {
              jsonBranch6(sb6, prevRow, sb7)
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
              jsonBranch2(sb2, prevRow, sb3)
            } else if (e3 != p3) {
              jsonBranch6(sb6, prevRow, sb7)
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
              jsonBranch3(sb3, prevRow, sb4)
            } else if (e4 != p4) {
              jsonBranch6(sb6, prevRow, sb7)
              jsonBranch5(sb5, prevRow, sb6)
              jsonBranch4(sb4, prevRow, sb5)
            } else if (e5 != p5) {
              jsonBranch6(sb6, prevRow, sb7)
              jsonBranch5(sb5, prevRow, sb6)
            } else if (e6 != p6) {
              jsonBranch6(sb6, prevRow, sb7)
            }
            jsonLeaf7(sb7, row)
            if (i == lastFlat) {
              jsonBranch6(sb6, row, sb7)
              jsonBranch5(sb5, row, sb6)
              jsonBranch4(sb4, row, sb5)
              jsonBranch3(sb3, row, sb4)
              jsonBranch2(sb2, row, sb3)
              jsonBranch1(sb1, row, sb2)
              jsonBranch0(sb0, row, sb1)
            }
          } else {
            jsonLeaf7(sb7, row)
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
        }
        sb0.append("\n    ]")
      }
    }
  }
}