package molecule.core.macros.nested

import java.lang.{Long => jLong}
import java.util.{ArrayList => jArrayList, Comparator => jComparator, List => jList}
import molecule.core.api.Molecule_0


/** Builder classes of various arity of nested tuples. */
trait NestedBase[Obj, Tpl]
//  extends jComparator[jList[AnyRef]] { self: Molecule_0[Obj, Tpl] =>
   { self: Molecule_0[Obj, Tpl] =>

  val levels = nestedLevels + 1

  protected var row    : jList[AnyRef] = new jArrayList[AnyRef]()
  protected var prevRow: jList[AnyRef] = new jArrayList[AnyRef]()

  protected var p0: jLong = 0L
  protected var p1: jLong = 0L
  protected var p2: jLong = 0L
  protected var p3: jLong = 0L
  protected var p4: jLong = 0L
  protected var p5: jLong = 0L
  protected var p6: jLong = 0L

  protected var e0: jLong = 0L
  protected var e1: jLong = 0L
  protected var e2: jLong = 0L
  protected var e3: jLong = 0L
  protected var e4: jLong = 0L
  protected var e5: jLong = 0L
  protected var e6: jLong = 0L

  protected var nextRow: Boolean = false
  protected var i                = 0
  protected var sortIndex        = 0
  protected var result           = 0
  protected var descending       = true

  protected def resetVars(): Unit = {
    p0 = 0L
    p1 = 0L
    p2 = 0L
    p3 = 0L
    p4 = 0L
    p5 = 0L
    p6 = 0L

    e0 = 0L
    e1 = 0L
    e2 = 0L
    e3 = 0L
    e4 = 0L
    e5 = 0L
    e6 = 0L

    i = 0
  }


  // java.util.Comparator sorting interface implemented by NestedTuples subclasses (`rows.sort(this)`)
  override def compare(a: jList[AnyRef], b: jList[AnyRef]): Int = {
    sortIndex = 0
    result = 0
    if (descending) {
      // Tuples: Sorting backwards, building from leaf to branches
      do {
        result = (-a.get(sortIndex).asInstanceOf[jLong]).compareTo(-b.get(sortIndex).asInstanceOf[jLong])
        sortIndex += 1 // 1 level deeper
      } while (sortIndex < levels && result == 0)
    } else {
      // Json: Sorting from outer to nested levels, level by level
      do {
        result = a.get(sortIndex).asInstanceOf[jLong].compareTo(b.get(sortIndex).asInstanceOf[jLong])
        sortIndex += 1 // 1 level deeper
      } while (sortIndex < levels && result == 0)
    }
    result
  }
}
