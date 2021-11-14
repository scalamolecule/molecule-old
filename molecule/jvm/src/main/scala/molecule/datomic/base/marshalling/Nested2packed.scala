package molecule.datomic.base.marshalling

import java.lang.{Long => jLong}
import java.util.{ArrayList => jArrayList, Collection => jCollection, Comparator => jComparator, Iterator => jIterator, List => jList}
import molecule.core.marshalling.nodes._
import molecule.datomic.base.marshalling.packers.ResolverFlat

private[molecule] case class Nested2packed(
  obj: Obj,
  rowCollection: jCollection[jList[AnyRef]],
  nestedLevels: Int
) extends jComparator[jList[AnyRef]] with ResolverFlat {

  // Sort rows by entity ids for each level

  val sortedRows = new java.util.ArrayList(rowCollection)
  sortedRows.sort(this)

  def compare(a: jList[AnyRef], b: jList[AnyRef]): Int = {
    var sortIndex = 0
    var result    = 0
    do {
      result = a.get(sortIndex).asInstanceOf[jLong].compareTo(b.get(sortIndex).asInstanceOf[jLong])
      sortIndex += 1 // 1 level deeper
    } while (sortIndex <= nestedLevels && result == 0)
    result
  }


  // Mutable placeholders for fast iterations with minimal object allocation

  val last                           = rowCollection.size
  val rows: jIterator[jList[AnyRef]] = sortedRows.iterator

  protected var row   : jList[AnyRef] = new jArrayList[AnyRef]()
  protected var topRow: jList[AnyRef] = new jArrayList[AnyRef]()

  protected var e0: AnyRef = 0.asInstanceOf[AnyRef]
  protected var e1: AnyRef = 0.asInstanceOf[AnyRef]
  protected var e2: AnyRef = 0.asInstanceOf[AnyRef]
  protected var e3: AnyRef = 0.asInstanceOf[AnyRef]
  protected var e4: AnyRef = 0.asInstanceOf[AnyRef]
  protected var e5: AnyRef = 0.asInstanceOf[AnyRef]
  protected var e6: AnyRef = 0.asInstanceOf[AnyRef]

  protected var i = 0

  private var colIndex = nestedLevels

  def prevEid(level: Int) = level match {
    case 1 => () => e0
    case 2 => () => e1
    case 3 => () => e2
    case 4 => () => e3
    case 5 => () => e4
    case 6 => () => e5
    case 7 => () => e6
  }

  def setCurEid(level: Int) = level match {
    case 1 => (e: AnyRef) => e1 = e
    case 2 => (e: AnyRef) => e2 = e
    case 3 => (e: AnyRef) => e3 = e
    case 4 => (e: AnyRef) => e4 = e
    case 5 => (e: AnyRef) => e5 = e
    case 6 => (e: AnyRef) => e6 = e
  }

  val sb = new StringBuffer()

  def getPacked: String = {
    if (!rowCollection.isEmpty) {
      packRows(obj.props)
    }
    sb.toString
  }

  def packNode(node: Node, level: Int): jList[_] => StringBuffer = {
    node match {
      case Prop(_, _, baseTpe, _, group, optAggrTpe) =>
        colIndex += 1
        packFlatAttr(sb, group, baseTpe, colIndex, optAggrTpe)

      case Obj(_, _, true, props) =>
        packNested(props, level + 1)

      case Obj(_, _, _, props)    =>
        val populatedProps = props.flatMap {
          case Obj(_, _, _, Nil) => None // skip objects with only tacit attributes
          case node              => Some(node)
        }
        packRef(populatedProps, level)
    }
  }

  def packRows(attrs: List[Node]): Unit = {
    attrs.size match {
      case 1  => packRows1(attrs)
      case 2  => packRows2(attrs)
      case 3  => packRows3(attrs)
      case 4  => packRows4(attrs)
      case 5  => packRows5(attrs)
      case 6  => packRows6(attrs)
      case 7  => packRows7(attrs)
      case 8  => packRows8(attrs)
      case 9  => packRows9(attrs)
      case 10 => packRows10(attrs)
      case 11 => packRows11(attrs)
      case 12 => packRows12(attrs)
      case 13 => packRows13(attrs)
      case 14 => packRows14(attrs)
      case 15 => packRows15(attrs)
      case 16 => packRows16(attrs)
      case 17 => packRows17(attrs)
      case 18 => packRows18(attrs)
      case 19 => packRows19(attrs)
      case 20 => packRows20(attrs)
      case 21 => packRows21(attrs)
      case 22 => packRows22(attrs)
    }
  }

  def packNested(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    attrs.size match {
      case 1  => packNested1(attrs, level)
      case 2  => packNested2(attrs, level)
      case 3  => packNested3(attrs, level)
      case 4  => packNested4(attrs, level)
      case 5  => packNested5(attrs, level)
      case 6  => packNested6(attrs, level)
      case 7  => packNested7(attrs, level)
      case 8  => packNested8(attrs, level)
      case 9  => packNested9(attrs, level)
      case 10 => packNested10(attrs, level)
      case 11 => packNested11(attrs, level)
      case 12 => packNested12(attrs, level)
      case 13 => packNested13(attrs, level)
      case 14 => packNested14(attrs, level)
      case 15 => packNested15(attrs, level)
      case 16 => packNested16(attrs, level)
      case 17 => packNested17(attrs, level)
      case 18 => packNested18(attrs, level)
      case 19 => packNested19(attrs, level)
      case 20 => packNested20(attrs, level)
      case 21 => packNested21(attrs, level)
      case 22 => packNested22(attrs, level)
    }
  }

  def packRef(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    attrs.size match {
      case 1  => packRef1(attrs, level)
      case 2  => packRef2(attrs, level)
      case 3  => packRef3(attrs, level)
      case 4  => packRef4(attrs, level)
      case 5  => packRef5(attrs, level)
      case 6  => packRef6(attrs, level)
      case 7  => packRef7(attrs, level)
      case 8  => packRef8(attrs, level)
      case 9  => packRef9(attrs, level)
      case 10 => packRef10(attrs, level)
      case 11 => packRef11(attrs, level)
      case 12 => packRef12(attrs, level)
      case 13 => packRef13(attrs, level)
      case 14 => packRef14(attrs, level)
      case 15 => packRef15(attrs, level)
      case 16 => packRef16(attrs, level)
      case 17 => packRef17(attrs, level)
      case 18 => packRef18(attrs, level)
      case 19 => packRef19(attrs, level)
      case 20 => packRef20(attrs, level)
      case 21 => packRef21(attrs, level)
      case 22 => packRef22(attrs, level)
    }
  }

  def packRows1(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
    } while (i != last)
  }

  def packRows2(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
    } while (i != last)
  }

  def packRows3(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    val pack2 = packNode(attrs(2), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
    } while (i != last)
  }

  def packRows4(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    val pack2 = packNode(attrs(2), 0)
    val pack3 = packNode(attrs(3), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
    } while (i != last)
  }

  def packRows5(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    val pack2 = packNode(attrs(2), 0)
    val pack3 = packNode(attrs(3), 0)
    val pack4 = packNode(attrs(4), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
    } while (i != last)
  }

  def packRows6(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    val pack2 = packNode(attrs(2), 0)
    val pack3 = packNode(attrs(3), 0)
    val pack4 = packNode(attrs(4), 0)
    val pack5 = packNode(attrs(5), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
    } while (i != last)
  }

  def packRows7(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    val pack2 = packNode(attrs(2), 0)
    val pack3 = packNode(attrs(3), 0)
    val pack4 = packNode(attrs(4), 0)
    val pack5 = packNode(attrs(5), 0)
    val pack6 = packNode(attrs(6), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
    } while (i != last)
  }

  def packRows8(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    val pack2 = packNode(attrs(2), 0)
    val pack3 = packNode(attrs(3), 0)
    val pack4 = packNode(attrs(4), 0)
    val pack5 = packNode(attrs(5), 0)
    val pack6 = packNode(attrs(6), 0)
    val pack7 = packNode(attrs(7), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
    } while (i != last)
  }

  def packRows9(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    val pack2 = packNode(attrs(2), 0)
    val pack3 = packNode(attrs(3), 0)
    val pack4 = packNode(attrs(4), 0)
    val pack5 = packNode(attrs(5), 0)
    val pack6 = packNode(attrs(6), 0)
    val pack7 = packNode(attrs(7), 0)
    val pack8 = packNode(attrs(8), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
    } while (i != last)
  }

  def packRows10(attrs: List[Node]): Unit = {
    val pack0 = packNode(attrs.head, 0)
    val pack1 = packNode(attrs(1), 0)
    val pack2 = packNode(attrs(2), 0)
    val pack3 = packNode(attrs(3), 0)
    val pack4 = packNode(attrs(4), 0)
    val pack5 = packNode(attrs(5), 0)
    val pack6 = packNode(attrs(6), 0)
    val pack7 = packNode(attrs(7), 0)
    val pack8 = packNode(attrs(8), 0)
    val pack9 = packNode(attrs(9), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
    } while (i != last)
  }

  def packRows11(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
    } while (i != last)
  }

  def packRows12(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
    } while (i != last)
  }

  def packRows13(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
    } while (i != last)
  }

  def packRows14(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
    } while (i != last)
  }

  def packRows15(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    val pack14 = packNode(attrs(14), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
      pack14(topRow)
    } while (i != last)
  }

  def packRows16(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    val pack14 = packNode(attrs(14), 0)
    val pack15 = packNode(attrs(15), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
      pack14(topRow)
      pack15(topRow)
    } while (i != last)
  }

  def packRows17(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    val pack14 = packNode(attrs(14), 0)
    val pack15 = packNode(attrs(15), 0)
    val pack16 = packNode(attrs(16), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
      pack14(topRow)
      pack15(topRow)
      pack16(topRow)
    } while (i != last)
  }

  def packRows18(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    val pack14 = packNode(attrs(14), 0)
    val pack15 = packNode(attrs(15), 0)
    val pack16 = packNode(attrs(16), 0)
    val pack17 = packNode(attrs(17), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
      pack14(topRow)
      pack15(topRow)
      pack16(topRow)
      pack17(topRow)
    } while (i != last)
  }

  def packRows19(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    val pack14 = packNode(attrs(14), 0)
    val pack15 = packNode(attrs(15), 0)
    val pack16 = packNode(attrs(16), 0)
    val pack17 = packNode(attrs(17), 0)
    val pack18 = packNode(attrs(18), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
      pack14(topRow)
      pack15(topRow)
      pack16(topRow)
      pack17(topRow)
      pack18(topRow)
    } while (i != last)
  }

  def packRows20(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    val pack14 = packNode(attrs(14), 0)
    val pack15 = packNode(attrs(15), 0)
    val pack16 = packNode(attrs(16), 0)
    val pack17 = packNode(attrs(17), 0)
    val pack18 = packNode(attrs(18), 0)
    val pack19 = packNode(attrs(19), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
      pack14(topRow)
      pack15(topRow)
      pack16(topRow)
      pack17(topRow)
      pack18(topRow)
      pack19(topRow)
    } while (i != last)
  }

  def packRows21(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    val pack14 = packNode(attrs(14), 0)
    val pack15 = packNode(attrs(15), 0)
    val pack16 = packNode(attrs(16), 0)
    val pack17 = packNode(attrs(17), 0)
    val pack18 = packNode(attrs(18), 0)
    val pack19 = packNode(attrs(19), 0)
    val pack20 = packNode(attrs(20), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
      pack14(topRow)
      pack15(topRow)
      pack16(topRow)
      pack17(topRow)
      pack18(topRow)
      pack19(topRow)
      pack20(topRow)
    } while (i != last)
  }

  def packRows22(attrs: List[Node]): Unit = {
    val pack0  = packNode(attrs.head, 0)
    val pack1  = packNode(attrs(1), 0)
    val pack2  = packNode(attrs(2), 0)
    val pack3  = packNode(attrs(3), 0)
    val pack4  = packNode(attrs(4), 0)
    val pack5  = packNode(attrs(5), 0)
    val pack6  = packNode(attrs(6), 0)
    val pack7  = packNode(attrs(7), 0)
    val pack8  = packNode(attrs(8), 0)
    val pack9  = packNode(attrs(9), 0)
    val pack10 = packNode(attrs(10), 0)
    val pack11 = packNode(attrs(11), 0)
    val pack12 = packNode(attrs(12), 0)
    val pack13 = packNode(attrs(13), 0)
    val pack14 = packNode(attrs(14), 0)
    val pack15 = packNode(attrs(15), 0)
    val pack16 = packNode(attrs(16), 0)
    val pack17 = packNode(attrs(17), 0)
    val pack18 = packNode(attrs(18), 0)
    val pack19 = packNode(attrs(19), 0)
    val pack20 = packNode(attrs(20), 0)
    val pack21 = packNode(attrs(21), 0)
    row = rows.next
    do {
      e0 = row.get(0)
      topRow = row
      pack0(topRow)
      pack1(topRow)
      pack2(topRow)
      pack3(topRow)
      pack4(topRow)
      pack5(topRow)
      pack6(topRow)
      pack7(topRow)
      pack8(topRow)
      pack9(topRow)
      pack10(topRow)
      pack11(topRow)
      pack12(topRow)
      pack13(topRow)
      pack14(topRow)
      pack15(topRow)
      pack16(topRow)
      pack17(topRow)
      pack18(topRow)
      pack19(topRow)
      pack20(topRow)
      pack21(topRow)
    } while (i != last)
  }

  def packNested1(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested2(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested3(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested4(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested5(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested6(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested7(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested8(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested9(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested10(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested11(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested12(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested13(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested14(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested15(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val pack14    = packNode(attrs(14), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested16(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val pack14    = packNode(attrs(14), level)
    val pack15    = packNode(attrs(15), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested17(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val pack14    = packNode(attrs(14), level)
    val pack15    = packNode(attrs(15), level)
    val pack16    = packNode(attrs(16), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested18(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val pack14    = packNode(attrs(14), level)
    val pack15    = packNode(attrs(15), level)
    val pack16    = packNode(attrs(16), level)
    val pack17    = packNode(attrs(17), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested19(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val pack14    = packNode(attrs(14), level)
    val pack15    = packNode(attrs(15), level)
    val pack16    = packNode(attrs(16), level)
    val pack17    = packNode(attrs(17), level)
    val pack18    = packNode(attrs(18), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          pack18(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          pack18(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested20(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val pack14    = packNode(attrs(14), level)
    val pack15    = packNode(attrs(15), level)
    val pack16    = packNode(attrs(16), level)
    val pack17    = packNode(attrs(17), level)
    val pack18    = packNode(attrs(18), level)
    val pack19    = packNode(attrs(19), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          pack18(row)
          pack19(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          pack18(row)
          pack19(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested21(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val pack14    = packNode(attrs(14), level)
    val pack15    = packNode(attrs(15), level)
    val pack16    = packNode(attrs(16), level)
    val pack17    = packNode(attrs(17), level)
    val pack18    = packNode(attrs(18), level)
    val pack19    = packNode(attrs(19), level)
    val pack20    = packNode(attrs(20), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          pack18(row)
          pack19(row)
          pack20(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          pack18(row)
          pack19(row)
          pack20(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }

  def packNested22(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0     = packNode(attrs.head, level)
    val pack1     = packNode(attrs(1), level)
    val pack2     = packNode(attrs(2), level)
    val pack3     = packNode(attrs(3), level)
    val pack4     = packNode(attrs(4), level)
    val pack5     = packNode(attrs(5), level)
    val pack6     = packNode(attrs(6), level)
    val pack7     = packNode(attrs(7), level)
    val pack8     = packNode(attrs(8), level)
    val pack9     = packNode(attrs(9), level)
    val pack10    = packNode(attrs(10), level)
    val pack11    = packNode(attrs(11), level)
    val pack12    = packNode(attrs(12), level)
    val pack13    = packNode(attrs(13), level)
    val pack14    = packNode(attrs(14), level)
    val pack15    = packNode(attrs(15), level)
    val pack16    = packNode(attrs(16), level)
    val pack17    = packNode(attrs(17), level)
    val pack18    = packNode(attrs(18), level)
    val pack19    = packNode(attrs(19), level)
    val pack20    = packNode(attrs(20), level)
    val pack21    = packNode(attrs(21), level)
    val prevLevel = level - 1
    val setCurE   = setCurEid(level)
    val prevE     = prevEid(level)
    if (level == nestedLevels) {
      (_: jList[_]) =>
        do {
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          pack18(row)
          pack19(row)
          pack20(row)
          pack21(row)
          if (rows.hasNext)
            row = rows.next
          i += 1
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    } else {
      (_: jList[_]) =>
        do {
          setCurE(row.get(level))
          pack0(row)
          pack1(row)
          pack2(row)
          pack3(row)
          pack4(row)
          pack5(row)
          pack6(row)
          pack7(row)
          pack8(row)
          pack9(row)
          pack10(row)
          pack11(row)
          pack12(row)
          pack13(row)
          pack14(row)
          pack15(row)
          pack16(row)
          pack17(row)
          pack18(row)
          pack19(row)
          pack20(row)
          pack21(row)
        } while (i != last && row.get(prevLevel) == prevE())
        next(sb)
    }
  }


  def packRef1(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    (_: jList[_]) =>
      pack0(row)
  }

  def packRef2(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
  }

  def packRef3(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
  }

  def packRef4(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
  }

  def packRef5(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
  }

  def packRef6(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
  }

  def packRef7(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
  }

  def packRef8(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
  }

  def packRef9(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    val pack8 = packNode(attrs(8), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
  }

  def packRef10(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    val pack8 = packNode(attrs(8), level)
    val pack9 = packNode(attrs(9), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
  }

  def packRef11(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
  }

  def packRef12(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
  }

  def packRef13(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
  }

  def packRef14(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
  }

  def packRef15(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    val pack14 = packNode(attrs(14), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
      pack14(row)
  }

  def packRef16(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    val pack14 = packNode(attrs(14), level)
    val pack15 = packNode(attrs(15), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
      pack14(row)
      pack15(row)
  }

  def packRef17(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    val pack14 = packNode(attrs(14), level)
    val pack15 = packNode(attrs(15), level)
    val pack16 = packNode(attrs(16), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
      pack14(row)
      pack15(row)
      pack16(row)
  }

  def packRef18(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    val pack14 = packNode(attrs(14), level)
    val pack15 = packNode(attrs(15), level)
    val pack16 = packNode(attrs(16), level)
    val pack17 = packNode(attrs(17), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
      pack14(row)
      pack15(row)
      pack16(row)
      pack17(row)
  }

  def packRef19(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    val pack14 = packNode(attrs(14), level)
    val pack15 = packNode(attrs(15), level)
    val pack16 = packNode(attrs(16), level)
    val pack17 = packNode(attrs(17), level)
    val pack18 = packNode(attrs(18), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
      pack14(row)
      pack15(row)
      pack16(row)
      pack17(row)
      pack18(row)
  }

  def packRef20(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    val pack14 = packNode(attrs(14), level)
    val pack15 = packNode(attrs(15), level)
    val pack16 = packNode(attrs(16), level)
    val pack17 = packNode(attrs(17), level)
    val pack18 = packNode(attrs(18), level)
    val pack19 = packNode(attrs(19), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
      pack14(row)
      pack15(row)
      pack16(row)
      pack17(row)
      pack18(row)
      pack19(row)
  }

  def packRef21(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    val pack14 = packNode(attrs(14), level)
    val pack15 = packNode(attrs(15), level)
    val pack16 = packNode(attrs(16), level)
    val pack17 = packNode(attrs(17), level)
    val pack18 = packNode(attrs(18), level)
    val pack19 = packNode(attrs(19), level)
    val pack20 = packNode(attrs(20), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
      pack14(row)
      pack15(row)
      pack16(row)
      pack17(row)
      pack18(row)
      pack19(row)
      pack20(row)
  }

  def packRef22(attrs: List[Node], level: Int): jList[_] => StringBuffer = {
    val pack0  = packNode(attrs.head, level)
    val pack1  = packNode(attrs(1), level)
    val pack2  = packNode(attrs(2), level)
    val pack3  = packNode(attrs(3), level)
    val pack4  = packNode(attrs(4), level)
    val pack5  = packNode(attrs(5), level)
    val pack6  = packNode(attrs(6), level)
    val pack7  = packNode(attrs(7), level)
    val pack8  = packNode(attrs(8), level)
    val pack9  = packNode(attrs(9), level)
    val pack10 = packNode(attrs(10), level)
    val pack11 = packNode(attrs(11), level)
    val pack12 = packNode(attrs(12), level)
    val pack13 = packNode(attrs(13), level)
    val pack14 = packNode(attrs(14), level)
    val pack15 = packNode(attrs(15), level)
    val pack16 = packNode(attrs(16), level)
    val pack17 = packNode(attrs(17), level)
    val pack18 = packNode(attrs(18), level)
    val pack19 = packNode(attrs(19), level)
    val pack20 = packNode(attrs(20), level)
    val pack21 = packNode(attrs(21), level)
    (_: jList[_]) =>
      pack0(row)
      pack1(row)
      pack2(row)
      pack3(row)
      pack4(row)
      pack5(row)
      pack6(row)
      pack7(row)
      pack8(row)
      pack9(row)
      pack10(row)
      pack11(row)
      pack12(row)
      pack13(row)
      pack14(row)
      pack15(row)
      pack16(row)
      pack17(row)
      pack18(row)
      pack19(row)
      pack20(row)
      pack21(row)
  }
}
