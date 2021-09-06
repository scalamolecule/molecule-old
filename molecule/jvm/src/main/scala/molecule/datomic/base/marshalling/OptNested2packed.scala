package molecule.datomic.base.marshalling

import java.util.{Collection => jCollection, Iterator => jIterator, List => jList, Map => jMap}
import molecule.core.macros.rowAttr.JsonBase
import molecule.core.marshalling.nodes._
import molecule.datomic.base.marshalling.packers.ResolverOptNested

case class OptNested2packed(
  obj: Obj,
  rowCollection: jCollection[jList[AnyRef]],
  maxRows: Int = -1,
  refIndexes: List[List[Int]] = Nil,
  tacitIndexes: List[List[Int]] = Nil
) extends ResolverOptNested with JsonBase {

  def getList(nestedData: Any): jList[Any] = {
    if (nestedData.isInstanceOf[jList[_]])
      nestedData.asInstanceOf[jList[Any]]
    else
      nestedData.asInstanceOf[jMap[String, Any]].values().iterator.next.asInstanceOf[jList[Any]]
  }

  def getPacked: String = {
    if (!rowCollection.isEmpty) {
      // Recursively build lambda to process each row of nested data
      val rowLambda = packNested(obj.props, 0, true)

      // Process data with lambda
      rowLambda(rowCollection.iterator)
    }
    sb.toString
  }

  def getRelatedProps(nodes: List[Node]): List[Node] = nodes.flatMap {
    case p: Prop                   => Some(p)
    case nested@Obj(_, _, true, _) => Some(nested)
    case Obj(_, _, _, props)       => getRelatedProps(props)
  }

  def packNode(node: Node, level: Int): jIterator[_] => Unit = {
    node match {
      case Prop(_, _, baseTpe, _, group, _) =>
        packOptNestedAttr(group, baseTpe)

      case nested@Obj(_, _, true, props) =>
        val relatedProps = getRelatedProps(props)
        packNested(relatedProps, level + 1, isDeeper(nested))

      case Obj(_, _, _, props) if level == 0 =>
        packlevelRef(props, level)

      case Obj(_, _, _, props) =>
        packRef(props, level)
    }
  }

  def packNested(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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

  def packRef(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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

  def packlevelRef(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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


  def packNested1(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    if (level == 0) {
      if (maxRows == -1)
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            // Iterator on top level is immutable to not be affected by nested iterators assigned to mutable `it`
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            // Iterator on top level is immutable to not be affected by nested iterators assigned to mutable `it`
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(1, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested2(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    if (level == 0) {
      if (maxRows == -1)
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator()
            pack0(it)
            pack1(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(2, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" =>
            nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested3(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    if (level == 0) {
      if (maxRows == -1)
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(3, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" =>
            nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested4(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    if (level == 0) {
      if (maxRows == -1)
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(4, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" =>
            nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested5(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    if (level == 0) {
      if (maxRows == -1)
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(5, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested6(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    if (level == 0) {
      if (maxRows == -1)
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(6, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested7(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    if (level == 0) {
      if (maxRows == -1)
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(7, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested8(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(8, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested9(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(9, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested10(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(10, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested11(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(11, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested12(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(12, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested13(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(13, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested14(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(14, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested15(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(15, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
                pack14(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested16(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(16, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
                pack14(it)
                pack15(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested17(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(17, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
                pack14(it)
                pack15(it)
                pack16(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested18(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(18, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
                pack14(it)
                pack15(it)
                pack16(it)
                pack17(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested19(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            pack18(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            pack18(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(19, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
                pack14(it)
                pack15(it)
                pack16(it)
                pack17(it)
                pack18(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested20(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            pack18(it)
            pack19(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            pack18(it)
            pack19(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(20, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
                pack14(it)
                pack15(it)
                pack16(it)
                pack17(it)
                pack18(it)
                pack19(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested21(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            pack18(it)
            pack19(it)
            pack20(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            pack18(it)
            pack19(it)
            pack20(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(21, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
                pack14(it)
                pack15(it)
                pack16(it)
                pack17(it)
                pack18(it)
                pack19(it)
                pack20(it)
              }
              next()
            } else nil()
        }
    }
  }

  def packNested22(attrs: List[Node], level: Int, deeper: Boolean): jIterator[_] => Unit = {
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
        (rows: jIterator[_]) =>
          while (rows.hasNext) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            pack18(it)
            pack19(it)
            pack20(it)
            pack21(it)
          }
      else
        (rows: jIterator[_]) => {
          var i = 0
          while (rows.hasNext && i != maxRows) {
            val it = rows.next.asInstanceOf[jList[Any]].iterator
            pack0(it)
            pack1(it)
            pack2(it)
            pack3(it)
            pack4(it)
            pack5(it)
            pack6(it)
            pack7(it)
            pack8(it)
            pack9(it)
            pack10(it)
            pack11(it)
            pack12(it)
            pack13(it)
            pack14(it)
            pack15(it)
            pack16(it)
            pack17(it)
            pack18(it)
            pack19(it)
            pack20(it)
            pack21(it)
            i += 1
          }
        }
    } else {
      val flatValues = extractFlatValues(22, refIndexes(level), tacitIndexes(level), deeper)
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val it = flatValues(getList(nestedData))
            if (it.hasNext) {
              while (it.hasNext) {
                pack0(it)
                pack1(it)
                pack2(it)
                pack3(it)
                pack4(it)
                pack5(it)
                pack6(it)
                pack7(it)
                pack8(it)
                pack9(it)
                pack10(it)
                pack11(it)
                pack12(it)
                pack13(it)
                pack14(it)
                pack15(it)
                pack16(it)
                pack17(it)
                pack18(it)
                pack19(it)
                pack20(it)
                pack21(it)
              }
              next()
            } else nil()
        }
    }
  }


  def packRef1(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
  }

  def packRef2(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator()
      pack0(it)
      pack1(it)
  }

  def packRef3(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
  }

  def packRef4(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
  }

  def packRef5(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
  }

  def packRef6(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
  }

  def packRef7(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
  }

  def packRef8(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
  }

  def packRef9(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    val pack8 = packNode(attrs(8), level)
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
  }

  def packRef10(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
  }

  def packRef11(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
  }

  def packRef12(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
  }

  def packRef13(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
  }

  def packRef14(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
  }

  def packRef15(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
  }

  def packRef16(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
  }

  def packRef17(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
  }

  def packRef18(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
  }

  def packRef19(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
      pack18(it)
  }

  def packRef20(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
      pack18(it)
      pack19(it)
  }

  def packRef21(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
      pack18(it)
      pack19(it)
      pack20(it)
  }

  def packRef22(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (vs: jIterator[_]) =>
      val it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
      pack18(it)
      pack19(it)
      pack20(it)
      pack21(it)
  }


  def packlevelRef1(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    (it: jIterator[_]) =>
      pack0(it)
  }

  def packlevelRef2(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
  }

  def packlevelRef3(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
  }

  def packlevelRef4(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
  }

  def packlevelRef5(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
  }

  def packlevelRef6(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
  }

  def packlevelRef7(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
  }

  def packlevelRef8(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
  }

  def packlevelRef9(attrs: List[Node], level: Int): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, level)
    val pack1 = packNode(attrs(1), level)
    val pack2 = packNode(attrs(2), level)
    val pack3 = packNode(attrs(3), level)
    val pack4 = packNode(attrs(4), level)
    val pack5 = packNode(attrs(5), level)
    val pack6 = packNode(attrs(6), level)
    val pack7 = packNode(attrs(7), level)
    val pack8 = packNode(attrs(8), level)
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
  }

  def packlevelRef10(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
  }

  def packlevelRef11(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
  }

  def packlevelRef12(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
  }

  def packlevelRef13(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
  }

  def packlevelRef14(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
  }

  def packlevelRef15(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
  }

  def packlevelRef16(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
  }

  def packlevelRef17(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
  }

  def packlevelRef18(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
  }

  def packlevelRef19(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
      pack18(it)
  }

  def packlevelRef20(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
      pack18(it)
      pack19(it)
  }

  def packlevelRef21(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
      pack18(it)
      pack19(it)
      pack20(it)
  }

  def packlevelRef22(attrs: List[Node], level: Int): jIterator[_] => Unit = {
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
    (it: jIterator[_]) =>
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
      pack8(it)
      pack9(it)
      pack10(it)
      pack11(it)
      pack12(it)
      pack13(it)
      pack14(it)
      pack15(it)
      pack16(it)
      pack17(it)
      pack18(it)
      pack19(it)
      pack20(it)
      pack21(it)
  }
}
