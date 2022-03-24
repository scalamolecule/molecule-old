package molecule.datomic.base.marshalling

import java.util.{Collection => jCollection, Iterator => jIterator, List => jList, Map => jMap}
import molecule.core.macros.rowAttr.JsonBase
import molecule.core.marshalling.ast.SortCoordinate
import molecule.core.marshalling.ast.nodes._
import molecule.datomic.base.marshalling.packers.ResolverOptNested
import molecule.datomic.base.marshalling.sorting.ExtractFlatValues

private[molecule] case class OptNested2packed(
  obj: Obj,
  sortedRows: jCollection[jList[AnyRef]],
  maxRows: Int,
  refIndexes: List[List[Int]],
  tacitIndexes: List[List[Int]],
  sortCoordinates: List[List[SortCoordinate]]
) extends ResolverOptNested with JsonBase {

  def getList(nestedData: Any): jList[AnyRef] = {
    if (nestedData.isInstanceOf[jList[_]])
      nestedData.asInstanceOf[jList[AnyRef]]
    else
      nestedData.asInstanceOf[jMap[String, AnyRef]].values().iterator.next.asInstanceOf[jList[AnyRef]]
  }

  def getPacked: String = {
    if (sortedRows.isEmpty) {
      return ""
    }
    // Recursively build lambda to process each row of nested data
    val rowLambda: (StringBuffer, jIterator[_]) => StringBuffer = packNested(obj.props, 0, deeper = true)

    // Process data with lambda
    val sb = new StringBuffer()
    rowLambda(sb, sortedRows.iterator)
    sb.toString
  }

  def getRelatedProps(nodes: List[Node]): List[Node] = nodes.flatMap {
    case p: Prop                   => Some(p)
    case nested@Obj(_, _, true, _) => Some(nested)
    case Obj(_, _, _, props)       => getRelatedProps(props)
  }

  def packNode(node: Node, level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    node match {
      case Prop(_, _, baseTpe, _, group, optAggrTpe) => packOptNestedAttr(group, baseTpe, optAggrTpe)
      case nested@Obj(_, _, true, props)             => packNested(getRelatedProps(props), level + 1, isDeeper(nested))
      case Obj(_, _, _, props)                       =>
        val populatedProps = props.flatMap {
          case Obj(_, _, _, Nil) => None // skip objects with only tacit attributes
          case node              => Some(node)
        }
        if (level == 0)
          packlevelRef(populatedProps, level)
        else
          packRef(populatedProps, level)
    }
  }

  def packNested(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    attrs.size match {
      case 1  => packNested1(attrs, level, deeper)
      case 2  => packNested2(attrs, level, deeper)
      case 3  => packNested3(attrs, level, deeper)
      case 4  => packNested4(attrs, level, deeper)
      case 5  => packNested5(attrs, level, deeper)
      case 6  => packNested6(attrs, level, deeper)
      case 7  => packNested7(attrs, level, deeper)
      case 8  => packNested8(attrs, level, deeper)
      case 9  => packNested9(attrs, level, deeper)
      case 10 => packNested10(attrs, level, deeper)
      case 11 => packNested11(attrs, level, deeper)
      case 12 => packNested12(attrs, level, deeper)
      case 13 => packNested13(attrs, level, deeper)
      case 14 => packNested14(attrs, level, deeper)
      case 15 => packNested15(attrs, level, deeper)
      case 16 => packNested16(attrs, level, deeper)
      case 17 => packNested17(attrs, level, deeper)
      case 18 => packNested18(attrs, level, deeper)
      case 19 => packNested19(attrs, level, deeper)
      case 20 => packNested20(attrs, level, deeper)
      case 21 => packNested21(attrs, level, deeper)
      case 22 => packNested22(attrs, level, deeper)
    }
  }

  def packRef(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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

  def packlevelRef(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    attrs.size match {
      case 1  => packlevelRef1(attrs, level)
      case 2  => packlevelRef2(attrs, level)
      case 3  => packlevelRef3(attrs, level)
      case 4  => packlevelRef4(attrs, level)
      case 5  => packlevelRef5(attrs, level)
      case 6  => packlevelRef6(attrs, level)
      case 7  => packlevelRef7(attrs, level)
      case 8  => packlevelRef8(attrs, level)
      case 9  => packlevelRef9(attrs, level)
      case 10 => packlevelRef10(attrs, level)
      case 11 => packlevelRef11(attrs, level)
      case 12 => packlevelRef12(attrs, level)
      case 13 => packlevelRef13(attrs, level)
      case 14 => packlevelRef14(attrs, level)
      case 15 => packlevelRef15(attrs, level)
      case 16 => packlevelRef16(attrs, level)
      case 17 => packlevelRef17(attrs, level)
      case 18 => packlevelRef18(attrs, level)
      case 19 => packlevelRef19(attrs, level)
      case 20 => packlevelRef20(attrs, level)
      case 21 => packlevelRef21(attrs, level)
      case 22 => packlevelRef22(attrs, level)
    }
  }


  def packNested1(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            // Iterator on top level is immutable to not be affected by nested iterators assigned to mutable `it`
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
          }
          sb
        }

      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            // Iterator on top level is immutable to not be affected by nested iterators assigned to mutable `it`
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(1, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested2(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator()
            pack0(sb, it)
            pack1(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(2, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested3(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(3, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested4(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(4, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested5(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(5, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested6(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(6, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested7(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(7, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested8(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(8, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested9(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    val pack8 = packNode(attrs(8), level)
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(9, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested10(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(10, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested11(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(11, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested12(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(12, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested13(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(13, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested14(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(14, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested15(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(15, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
                pack14(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested16(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(16, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
                pack14(sb, it)
                pack15(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested17(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(17, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
                pack14(sb, it)
                pack15(sb, it)
                pack16(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested18(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(18, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
                pack14(sb, it)
                pack15(sb, it)
                pack16(sb, it)
                pack17(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested19(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            pack18(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            pack18(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(19, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
                pack14(sb, it)
                pack15(sb, it)
                pack16(sb, it)
                pack17(sb, it)
                pack18(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested20(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            pack18(sb, it)
            pack19(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            pack18(sb, it)
            pack19(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(20, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
                pack14(sb, it)
                pack15(sb, it)
                pack16(sb, it)
                pack17(sb, it)
                pack18(sb, it)
                pack19(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested21(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            pack18(sb, it)
            pack19(sb, it)
            pack20(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            pack18(sb, it)
            pack19(sb, it)
            pack20(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(21, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
                pack14(sb, it)
                pack15(sb, it)
                pack16(sb, it)
                pack17(sb, it)
                pack18(sb, it)
                pack19(sb, it)
                pack20(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }

  def packNested22(attrs: List[Node], level: Int, deeper: Boolean): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    if (level == 0) {
      if (maxRows == -1)
        (sb: StringBuffer, rows: jIterator[_]) => {
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            pack18(sb, it)
            pack19(sb, it)
            pack20(sb, it)
            pack21(sb, it)
          }
          sb
        }
      else
        (sb: StringBuffer, rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(sb, it)
            pack1(sb, it)
            pack2(sb, it)
            pack3(sb, it)
            pack4(sb, it)
            pack5(sb, it)
            pack6(sb, it)
            pack7(sb, it)
            pack8(sb, it)
            pack9(sb, it)
            pack10(sb, it)
            pack11(sb, it)
            pack12(sb, it)
            pack13(sb, it)
            pack14(sb, it)
            pack15(sb, it)
            pack16(sb, it)
            pack17(sb, it)
            pack18(sb, it)
            pack19(sb, it)
            pack20(sb, it)
            pack21(sb, it)
            i += 1
          }
          sb
        }
    } else {
      val flatValues = ExtractFlatValues(22, refIndexes(level), tacitIndexes(level), deeper, sortCoordinates, level)
      (sb: StringBuffer, vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil(sb)
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(sb, it)
                pack1(sb, it)
                pack2(sb, it)
                pack3(sb, it)
                pack4(sb, it)
                pack5(sb, it)
                pack6(sb, it)
                pack7(sb, it)
                pack8(sb, it)
                pack9(sb, it)
                pack10(sb, it)
                pack11(sb, it)
                pack12(sb, it)
                pack13(sb, it)
                pack14(sb, it)
                pack15(sb, it)
                pack16(sb, it)
                pack17(sb, it)
                pack18(sb, it)
                pack19(sb, it)
                pack20(sb, it)
                pack21(sb, it)
              }
              next(sb)
            } else nil(sb)
        }
    }
  }


  def packRef1(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
  }

  def packRef2(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator()
      pack0(sb, it)
      pack1(sb, it)
  }

  def packRef3(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
  }

  def packRef4(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
  }

  def packRef5(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
  }

  def packRef6(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
  }

  def packRef7(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
  }

  def packRef8(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
  }

  def packRef9(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    val pack8 = packNode(attrs(8), level)
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
  }

  def packRef10(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
  }

  def packRef11(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
  }

  def packRef12(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
  }

  def packRef13(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
  }

  def packRef14(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
  }

  def packRef15(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
  }

  def packRef16(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
  }

  def packRef17(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
  }

  def packRef18(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
  }

  def packRef19(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
      pack18(sb, it)
  }

  def packRef20(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
      pack18(sb, it)
      pack19(sb, it)
  }

  def packRef21(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
      pack18(sb, it)
      pack19(sb, it)
      pack20(sb, it)
  }

  def packRef22(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
      pack18(sb, it)
      pack19(sb, it)
      pack20(sb, it)
      pack21(sb, it)
  }


  def packlevelRef1(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
  }

  def packlevelRef2(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
  }

  def packlevelRef3(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
  }

  def packlevelRef4(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
  }

  def packlevelRef5(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
  }

  def packlevelRef6(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
  }

  def packlevelRef7(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
  }

  def packlevelRef8(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
  }

  def packlevelRef9(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    val pack8 = packNode(attrs(8), level)
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
  }

  def packlevelRef10(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
  }

  def packlevelRef11(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
  }

  def packlevelRef12(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
  }

  def packlevelRef13(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
  }

  def packlevelRef14(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
  }

  def packlevelRef15(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
  }

  def packlevelRef16(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
  }

  def packlevelRef17(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
  }

  def packlevelRef18(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
  }

  def packlevelRef19(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
      pack18(sb, it)
  }

  def packlevelRef20(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
      pack18(sb, it)
      pack19(sb, it)
  }

  def packlevelRef21(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
      pack18(sb, it)
      pack19(sb, it)
      pack20(sb, it)
  }

  def packlevelRef22(attrs: List[Node], level: Int): (StringBuffer, jIterator[_]) => StringBuffer = {
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
    (sb: StringBuffer, it: jIterator[_]) =>
      pack0(sb, it)
      pack1(sb, it)
      pack2(sb, it)
      pack3(sb, it)
      pack4(sb, it)
      pack5(sb, it)
      pack6(sb, it)
      pack7(sb, it)
      pack8(sb, it)
      pack9(sb, it)
      pack10(sb, it)
      pack11(sb, it)
      pack12(sb, it)
      pack13(sb, it)
      pack14(sb, it)
      pack15(sb, it)
      pack16(sb, it)
      pack17(sb, it)
      pack18(sb, it)
      pack19(sb, it)
      pack20(sb, it)
      pack21(sb, it)
  }
}
