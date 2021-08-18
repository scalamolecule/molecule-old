package molecule.datomic.base.marshalling

import java.util.{Collection => jCollection, Iterator => jIterator, List => jList, Map => jMap}
import molecule.core.marshalling.attrIndexes._
import molecule.datomic.base.marshalling.nestedOpt.PackLambdas

case class NestedOpt2packed(
  rowCollection: jCollection[jList[AnyRef]],
  rowCountAll: Int,
  rowCount: Int,
  queryMs: Long,
  refIndexes: List[List[Int]],
  tacitIndexes: List[List[Int]],
  indexes: Indexes
) extends PackLambdas {

  //  rowCollection.forEach(row => println(row))

  def getList(nestedData: Any): jList[Any] = {
    if (nestedData.isInstanceOf[jList[_]])
      nestedData.asInstanceOf[jList[Any]]
    else
      nestedData.asInstanceOf[jMap[String, Any]].values().iterator.next.asInstanceOf[jList[Any]]
  }

  // Mutable placeholders for fast iterations with minimal object allocation
  var i                  = 0
  var it: jIterator[Any] = null


  def getPacked: String = {
    sb.append(
      s"""$rowCountAll
         |$rowCount
         |$queryMs""".stripMargin
    )

    if (!rowCollection.isEmpty) {
      // Recursively build lambda to process each row of nested data
      val rowLambda = packNested(indexes.attrs, true)

      // Process data with lambda
      rowLambda(rowCollection.iterator)
    }
    sb.toString
  }

  def packNode(node: IndexNode, top: Boolean = false): jIterator[_] => Unit = {
    node match {
      case AttrIndex(_, castIndex, _, _, _) =>
        packAttr(castIndex)

      case Indexes(_, 2, attrs) =>
        packNested(attrs)

      case Indexes(_, _, attrs) if top =>
        packTopRef(attrs)

      case Indexes(_, _, attrs) =>
        packRef(attrs)
    }
  }

  def packNested(attrs: List[IndexNode], top: Boolean = false): jIterator[_] => Unit = {
    attrs.size match {
      case 1  => packNested1(attrs, top)
      case 2  => packNested2(attrs, top)
      case 3  => packNested3(attrs, top)
      case 4  => packNested4(attrs, top)
      case 5  => packNested5(attrs, top)
      case 6  => packNested6(attrs, top)
      case 7  => packNested7(attrs, top)
      case 8  => packNested8(attrs, top)
      case 9  => packNested9(attrs, top)
      case 10 => packNested10(attrs, top)
      case 11 => packNested11(attrs, top)
      case 12 => packNested12(attrs, top)
      case 13 => packNested13(attrs, top)
      case 14 => packNested14(attrs, top)
      case 15 => packNested15(attrs, top)
      case 16 => packNested16(attrs, top)
      case 17 => packNested17(attrs, top)
      case 18 => packNested18(attrs, top)
      case 19 => packNested19(attrs, top)
      case 20 => packNested20(attrs, top)
      case 21 => packNested21(attrs, top)
      case 22 => packNested22(attrs, top)
    }
  }

  def packRef(attrs: List[IndexNode]): jIterator[_] => Unit = {
    attrs.size match {
      case 1  => packRef1(attrs)
      case 2  => packRef2(attrs)
      case 3  => packRef3(attrs)
      case 4  => packRef4(attrs)
      case 5  => packRef5(attrs)
      case 6  => packRef6(attrs)
      case 7  => packRef7(attrs)
      case 8  => packRef8(attrs)
      case 9  => packRef9(attrs)
      case 10 => packRef10(attrs)
      case 11 => packRef11(attrs)
      case 12 => packRef12(attrs)
      case 13 => packRef13(attrs)
      case 14 => packRef14(attrs)
      case 15 => packRef15(attrs)
      case 16 => packRef16(attrs)
      case 17 => packRef17(attrs)
      case 18 => packRef18(attrs)
      case 19 => packRef19(attrs)
      case 20 => packRef20(attrs)
      case 21 => packRef21(attrs)
      case 22 => packRef22(attrs)
    }
  }

  def packTopRef(attrs: List[IndexNode]): jIterator[_] => Unit = {
    attrs.size match {
      case 1  => packTopRef1(attrs)
      case 2  => packTopRef2(attrs)
      case 3  => packTopRef3(attrs)
      case 4  => packTopRef4(attrs)
      case 5  => packTopRef5(attrs)
      case 6  => packTopRef6(attrs)
      case 7  => packTopRef7(attrs)
      case 8  => packTopRef8(attrs)
      case 9  => packTopRef9(attrs)
      case 10 => packTopRef10(attrs)
      case 11 => packTopRef11(attrs)
      case 12 => packTopRef12(attrs)
      case 13 => packTopRef13(attrs)
      case 14 => packTopRef14(attrs)
      case 15 => packTopRef15(attrs)
      case 16 => packTopRef16(attrs)
      case 17 => packTopRef17(attrs)
      case 18 => packTopRef18(attrs)
      case 19 => packTopRef19(attrs)
      case 20 => packTopRef20(attrs)
      case 21 => packTopRef21(attrs)
      case 22 => packTopRef22(attrs)
    }
  }



  def packNested1(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          val it = rows.next.asInstanceOf[jList[Any]].iterator
          pack0(it)
          i += 1
        }
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
              pack0(it)
            }
            next()
        }
  }

  def packNested2(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          // Iterator on top level is immutable to not be affected by nested iterators assigned to mutable `it`
          val it = rows.next.asInstanceOf[jList[Any]].iterator()
          pack0(it)
          pack1(it)
          i += 1
        }
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            val list = getList(nestedData)
            list.forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
              pack0(it)
              pack1(it)
            }
            next()
        }
  }

  def packNested3(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    val pack2 = packNode(attrs(2), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          val it = rows.next.asInstanceOf[jList[Any]].iterator
          pack0(it)
          pack1(it)
          pack2(it)
          i += 1
          //          rowSeparator()
        }
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
              pack0(it)
              pack1(it)
              pack2(it)
            }
            next()
        }
  }

  def packNested4(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    val pack2 = packNode(attrs(2), top)
    val pack3 = packNode(attrs(3), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          val it = rows.next.asInstanceOf[jList[Any]].iterator
          pack0(it)
          pack1(it)
          pack2(it)
          pack3(it)
          i += 1
        }
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
              pack0(it)
              pack1(it)
              pack2(it)
              pack3(it)
            }
            next()
        }
  }

  def packNested5(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    val pack2 = packNode(attrs(2), top)
    val pack3 = packNode(attrs(3), top)
    val pack4 = packNode(attrs(4), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          val it = rows.next.asInstanceOf[jList[Any]].iterator
          pack0(it)
          pack1(it)
          pack2(it)
          pack3(it)
          pack4(it)
          i += 1
        }
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
              pack0(it)
              pack1(it)
              pack2(it)
              pack3(it)
              pack4(it)
            }
            next()
        }
  }

  def packNested6(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    val pack2 = packNode(attrs(2), top)
    val pack3 = packNode(attrs(3), top)
    val pack4 = packNode(attrs(4), top)
    val pack5 = packNode(attrs(5), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          val it = rows.next.asInstanceOf[jList[Any]].iterator
          pack0(it)
          pack1(it)
          pack2(it)
          pack3(it)
          pack4(it)
          pack5(it)
          i += 1
        }
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
              pack0(it)
              pack1(it)
              pack2(it)
              pack3(it)
              pack4(it)
              pack5(it)
            }
            next()
        }
  }

  def packNested7(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    val pack2 = packNode(attrs(2), top)
    val pack3 = packNode(attrs(3), top)
    val pack4 = packNode(attrs(4), top)
    val pack5 = packNode(attrs(5), top)
    val pack6 = packNode(attrs(6), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
              pack0(it)
              pack1(it)
              pack2(it)
              pack3(it)
              pack4(it)
              pack5(it)
              pack6(it)
            }
            next()
        }
  }

  def packNested8(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    val pack2 = packNode(attrs(2), top)
    val pack3 = packNode(attrs(3), top)
    val pack4 = packNode(attrs(4), top)
    val pack5 = packNode(attrs(5), top)
    val pack6 = packNode(attrs(6), top)
    val pack7 = packNode(attrs(7), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested9(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    val pack2 = packNode(attrs(2), top)
    val pack3 = packNode(attrs(3), top)
    val pack4 = packNode(attrs(4), top)
    val pack5 = packNode(attrs(5), top)
    val pack6 = packNode(attrs(6), top)
    val pack7 = packNode(attrs(7), top)
    val pack8 = packNode(attrs(8), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested10(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, top)
    val pack1 = packNode(attrs(1), top)
    val pack2 = packNode(attrs(2), top)
    val pack3 = packNode(attrs(3), top)
    val pack4 = packNode(attrs(4), top)
    val pack5 = packNode(attrs(5), top)
    val pack6 = packNode(attrs(6), top)
    val pack7 = packNode(attrs(7), top)
    val pack8 = packNode(attrs(8), top)
    val pack9 = packNode(attrs(9), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested11(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested12(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested13(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested14(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested15(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    val pack14 = packNode(attrs(14), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested16(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    val pack14 = packNode(attrs(14), top)
    val pack15 = packNode(attrs(15), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested17(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    val pack14 = packNode(attrs(14), top)
    val pack15 = packNode(attrs(15), top)
    val pack16 = packNode(attrs(16), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested18(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    val pack14 = packNode(attrs(14), top)
    val pack15 = packNode(attrs(15), top)
    val pack16 = packNode(attrs(16), top)
    val pack17 = packNode(attrs(17), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested19(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    val pack14 = packNode(attrs(14), top)
    val pack15 = packNode(attrs(15), top)
    val pack16 = packNode(attrs(16), top)
    val pack17 = packNode(attrs(17), top)
    val pack18 = packNode(attrs(18), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested20(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    val pack14 = packNode(attrs(14), top)
    val pack15 = packNode(attrs(15), top)
    val pack16 = packNode(attrs(16), top)
    val pack17 = packNode(attrs(17), top)
    val pack18 = packNode(attrs(18), top)
    val pack19 = packNode(attrs(19), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested21(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    val pack14 = packNode(attrs(14), top)
    val pack15 = packNode(attrs(15), top)
    val pack16 = packNode(attrs(16), top)
    val pack17 = packNode(attrs(17), top)
    val pack18 = packNode(attrs(18), top)
    val pack19 = packNode(attrs(19), top)
    val pack20 = packNode(attrs(20), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }

  def packNested22(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, top)
    val pack1  = packNode(attrs(1), top)
    val pack2  = packNode(attrs(2), top)
    val pack3  = packNode(attrs(3), top)
    val pack4  = packNode(attrs(4), top)
    val pack5  = packNode(attrs(5), top)
    val pack6  = packNode(attrs(6), top)
    val pack7  = packNode(attrs(7), top)
    val pack8  = packNode(attrs(8), top)
    val pack9  = packNode(attrs(9), top)
    val pack10 = packNode(attrs(10), top)
    val pack11 = packNode(attrs(11), top)
    val pack12 = packNode(attrs(12), top)
    val pack13 = packNode(attrs(13), top)
    val pack14 = packNode(attrs(14), top)
    val pack15 = packNode(attrs(15), top)
    val pack16 = packNode(attrs(16), top)
    val pack17 = packNode(attrs(17), top)
    val pack18 = packNode(attrs(18), top)
    val pack19 = packNode(attrs(19), top)
    val pack20 = packNode(attrs(20), top)
    val pack21 = packNode(attrs(21), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
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
    else
      (vs: jIterator[_]) =>
        vs.next match {
          case null | "__none__" => nil()
          case nestedData        =>
            getList(nestedData).forEach { chunk =>
              it = chunk.asInstanceOf[jMap[String, Any]].values.iterator()
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
        }
  }


  def packRef1(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
  }

  def packRef2(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator()
      pack0(it)
      pack1(it)
  }

  def packRef3(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    val pack2 = packNode(attrs(2))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
  }

  def packRef4(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    val pack2 = packNode(attrs(2))
    val pack3 = packNode(attrs(3))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
  }

  def packRef5(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    val pack2 = packNode(attrs(2))
    val pack3 = packNode(attrs(3))
    val pack4 = packNode(attrs(4))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
  }

  def packRef6(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    val pack2 = packNode(attrs(2))
    val pack3 = packNode(attrs(3))
    val pack4 = packNode(attrs(4))
    val pack5 = packNode(attrs(5))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
  }

  def packRef7(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    val pack2 = packNode(attrs(2))
    val pack3 = packNode(attrs(3))
    val pack4 = packNode(attrs(4))
    val pack5 = packNode(attrs(5))
    val pack6 = packNode(attrs(6))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
  }

  def packRef8(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    val pack2 = packNode(attrs(2))
    val pack3 = packNode(attrs(3))
    val pack4 = packNode(attrs(4))
    val pack5 = packNode(attrs(5))
    val pack6 = packNode(attrs(6))
    val pack7 = packNode(attrs(7))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
      pack6(it)
      pack7(it)
  }

  def packRef9(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    val pack2 = packNode(attrs(2))
    val pack3 = packNode(attrs(3))
    val pack4 = packNode(attrs(4))
    val pack5 = packNode(attrs(5))
    val pack6 = packNode(attrs(6))
    val pack7 = packNode(attrs(7))
    val pack8 = packNode(attrs(8))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef10(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head)
    val pack1 = packNode(attrs(1))
    val pack2 = packNode(attrs(2))
    val pack3 = packNode(attrs(3))
    val pack4 = packNode(attrs(4))
    val pack5 = packNode(attrs(5))
    val pack6 = packNode(attrs(6))
    val pack7 = packNode(attrs(7))
    val pack8 = packNode(attrs(8))
    val pack9 = packNode(attrs(9))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef11(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef12(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef13(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef14(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef15(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    val pack14 = packNode(attrs(14))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef16(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    val pack14 = packNode(attrs(14))
    val pack15 = packNode(attrs(15))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef17(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    val pack14 = packNode(attrs(14))
    val pack15 = packNode(attrs(15))
    val pack16 = packNode(attrs(16))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef18(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    val pack14 = packNode(attrs(14))
    val pack15 = packNode(attrs(15))
    val pack16 = packNode(attrs(16))
    val pack17 = packNode(attrs(17))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef19(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    val pack14 = packNode(attrs(14))
    val pack15 = packNode(attrs(15))
    val pack16 = packNode(attrs(16))
    val pack17 = packNode(attrs(17))
    val pack18 = packNode(attrs(18))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef20(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    val pack14 = packNode(attrs(14))
    val pack15 = packNode(attrs(15))
    val pack16 = packNode(attrs(16))
    val pack17 = packNode(attrs(17))
    val pack18 = packNode(attrs(18))
    val pack19 = packNode(attrs(19))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef21(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    val pack14 = packNode(attrs(14))
    val pack15 = packNode(attrs(15))
    val pack16 = packNode(attrs(16))
    val pack17 = packNode(attrs(17))
    val pack18 = packNode(attrs(18))
    val pack19 = packNode(attrs(19))
    val pack20 = packNode(attrs(20))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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

  def packRef22(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head)
    val pack1  = packNode(attrs(1))
    val pack2  = packNode(attrs(2))
    val pack3  = packNode(attrs(3))
    val pack4  = packNode(attrs(4))
    val pack5  = packNode(attrs(5))
    val pack6  = packNode(attrs(6))
    val pack7  = packNode(attrs(7))
    val pack8  = packNode(attrs(8))
    val pack9  = packNode(attrs(9))
    val pack10 = packNode(attrs(10))
    val pack11 = packNode(attrs(11))
    val pack12 = packNode(attrs(12))
    val pack13 = packNode(attrs(13))
    val pack14 = packNode(attrs(14))
    val pack15 = packNode(attrs(15))
    val pack16 = packNode(attrs(16))
    val pack17 = packNode(attrs(17))
    val pack18 = packNode(attrs(18))
    val pack19 = packNode(attrs(19))
    val pack20 = packNode(attrs(20))
    val pack21 = packNode(attrs(21))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
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


  def packTopRef1(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    (vs: jIterator[_]) =>
      pack0(vs)
  }

  def packTopRef2(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
  }

  def packTopRef3(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    val pack2 = packNode(attrs(2), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
  }

  def packTopRef4(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    val pack2 = packNode(attrs(2), true)
    val pack3 = packNode(attrs(3), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
  }

  def packTopRef5(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    val pack2 = packNode(attrs(2), true)
    val pack3 = packNode(attrs(3), true)
    val pack4 = packNode(attrs(4), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
  }

  def packTopRef6(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    val pack2 = packNode(attrs(2), true)
    val pack3 = packNode(attrs(3), true)
    val pack4 = packNode(attrs(4), true)
    val pack5 = packNode(attrs(5), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
  }

  def packTopRef7(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    val pack2 = packNode(attrs(2), true)
    val pack3 = packNode(attrs(3), true)
    val pack4 = packNode(attrs(4), true)
    val pack5 = packNode(attrs(5), true)
    val pack6 = packNode(attrs(6), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
  }

  def packTopRef8(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    val pack2 = packNode(attrs(2), true)
    val pack3 = packNode(attrs(3), true)
    val pack4 = packNode(attrs(4), true)
    val pack5 = packNode(attrs(5), true)
    val pack6 = packNode(attrs(6), true)
    val pack7 = packNode(attrs(7), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
  }

  def packTopRef9(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    val pack2 = packNode(attrs(2), true)
    val pack3 = packNode(attrs(3), true)
    val pack4 = packNode(attrs(4), true)
    val pack5 = packNode(attrs(5), true)
    val pack6 = packNode(attrs(6), true)
    val pack7 = packNode(attrs(7), true)
    val pack8 = packNode(attrs(8), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
  }

  def packTopRef10(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = packNode(attrs.head, true)
    val pack1 = packNode(attrs(1), true)
    val pack2 = packNode(attrs(2), true)
    val pack3 = packNode(attrs(3), true)
    val pack4 = packNode(attrs(4), true)
    val pack5 = packNode(attrs(5), true)
    val pack6 = packNode(attrs(6), true)
    val pack7 = packNode(attrs(7), true)
    val pack8 = packNode(attrs(8), true)
    val pack9 = packNode(attrs(9), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
  }

  def packTopRef11(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
  }

  def packTopRef12(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
  }

  def packTopRef13(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
  }

  def packTopRef14(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
  }

  def packTopRef15(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    val pack14 = packNode(attrs(14), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
      pack14(vs)
  }

  def packTopRef16(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    val pack14 = packNode(attrs(14), true)
    val pack15 = packNode(attrs(15), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
      pack14(vs)
      pack15(vs)
  }

  def packTopRef17(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    val pack14 = packNode(attrs(14), true)
    val pack15 = packNode(attrs(15), true)
    val pack16 = packNode(attrs(16), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
      pack14(vs)
      pack15(vs)
      pack16(vs)
  }

  def packTopRef18(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    val pack14 = packNode(attrs(14), true)
    val pack15 = packNode(attrs(15), true)
    val pack16 = packNode(attrs(16), true)
    val pack17 = packNode(attrs(17), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
      pack14(vs)
      pack15(vs)
      pack16(vs)
      pack17(vs)
  }

  def packTopRef19(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    val pack14 = packNode(attrs(14), true)
    val pack15 = packNode(attrs(15), true)
    val pack16 = packNode(attrs(16), true)
    val pack17 = packNode(attrs(17), true)
    val pack18 = packNode(attrs(18), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
      pack14(vs)
      pack15(vs)
      pack16(vs)
      pack17(vs)
      pack18(vs)
  }

  def packTopRef20(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    val pack14 = packNode(attrs(14), true)
    val pack15 = packNode(attrs(15), true)
    val pack16 = packNode(attrs(16), true)
    val pack17 = packNode(attrs(17), true)
    val pack18 = packNode(attrs(18), true)
    val pack19 = packNode(attrs(19), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
      pack14(vs)
      pack15(vs)
      pack16(vs)
      pack17(vs)
      pack18(vs)
      pack19(vs)
  }

  def packTopRef21(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    val pack14 = packNode(attrs(14), true)
    val pack15 = packNode(attrs(15), true)
    val pack16 = packNode(attrs(16), true)
    val pack17 = packNode(attrs(17), true)
    val pack18 = packNode(attrs(18), true)
    val pack19 = packNode(attrs(19), true)
    val pack20 = packNode(attrs(20), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
      pack14(vs)
      pack15(vs)
      pack16(vs)
      pack17(vs)
      pack18(vs)
      pack19(vs)
      pack20(vs)
  }

  def packTopRef22(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = packNode(attrs.head, true)
    val pack1  = packNode(attrs(1), true)
    val pack2  = packNode(attrs(2), true)
    val pack3  = packNode(attrs(3), true)
    val pack4  = packNode(attrs(4), true)
    val pack5  = packNode(attrs(5), true)
    val pack6  = packNode(attrs(6), true)
    val pack7  = packNode(attrs(7), true)
    val pack8  = packNode(attrs(8), true)
    val pack9  = packNode(attrs(9), true)
    val pack10 = packNode(attrs(10), true)
    val pack11 = packNode(attrs(11), true)
    val pack12 = packNode(attrs(12), true)
    val pack13 = packNode(attrs(13), true)
    val pack14 = packNode(attrs(14), true)
    val pack15 = packNode(attrs(15), true)
    val pack16 = packNode(attrs(16), true)
    val pack17 = packNode(attrs(17), true)
    val pack18 = packNode(attrs(18), true)
    val pack19 = packNode(attrs(19), true)
    val pack20 = packNode(attrs(20), true)
    val pack21 = packNode(attrs(21), true)
    (vs: jIterator[_]) =>
      pack0(vs)
      pack1(vs)
      pack2(vs)
      pack3(vs)
      pack4(vs)
      pack5(vs)
      pack6(vs)
      pack7(vs)
      pack8(vs)
      pack9(vs)
      pack10(vs)
      pack11(vs)
      pack12(vs)
      pack13(vs)
      pack14(vs)
      pack15(vs)
      pack16(vs)
      pack17(vs)
      pack18(vs)
      pack19(vs)
      pack20(vs)
      pack21(vs)
  }
}
