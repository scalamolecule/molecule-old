package molecule.datomic.base.marshalling

import java.util.{Collection => jCollection, Iterator => jIterator, List => jList, Map => jMap}
import molecule.core.marshalling.attrIndexes._
import molecule.datomic.base.marshalling.pack.PackLambdas

case class OptNestedRows2packed(
  rowCollection: jCollection[jList[AnyRef]],
  rowCountAll: Int,
  rowCount: Int,
  queryMs: Long,
  refIndexes: List[List[Int]],
  tacitIndexes: List[List[Int]],
  indexes: Indexes
) extends PackLambdas {

  var i                  = 0
  var it: jIterator[Any] = null


  def getPacked: String = {
    sb.append(
      s"""$rowCountAll
         |$rowCount
         |$queryMs
         |-----------""".stripMargin
    )

    // Recursively build lambda to process each row of nested data
    val rowLambda = getNested(indexes.attrs, true)

    // Process data with lambda
    rowLambda(rowCollection.iterator)
    sb.toString
  }

  def getPackingLambda(indexNode: IndexNode, top: Boolean = false): jIterator[_] => Unit = {
    indexNode match {
      case AttrIndex(_, castIndex, _, _, _) =>
        packLambdas(castIndex)

      case Indexes(_, 2, attrs) =>
        getNested(attrs)

      case Indexes("Tx", _, List(Indexes(_, _, attrs))) =>
        // Apply top level row to all tx meta attributes
        (rows: jIterator[_]) =>
          attrs.foreach(attr =>
            getPackingLambda(attr, top)(rows))

      case Indexes(_, _, attrs) =>
        getRef(attrs)
    }
  }

  def getRef(attrs: List[IndexNode]): jIterator[_] => Unit = {
    attrs.size match {
      case 1  => ref1(attrs)
      case 2  => ref2(attrs)
      case 3  => ref3(attrs)
      case 4  => ref4(attrs)
      case 5  => ref5(attrs)
      case 6  => ref6(attrs)
      case 7  => ref7(attrs)
      case 8  => ref8(attrs)
      case 9  => ref9(attrs)
      case 10 => ref10(attrs)
      case 11 => ref11(attrs)
      case 12 => ref12(attrs)
      case 13 => ref13(attrs)
      case 14 => ref14(attrs)
      case 15 => ref15(attrs)
      case 16 => ref16(attrs)
      case 17 => ref17(attrs)
      case 18 => ref18(attrs)
      case 19 => ref19(attrs)
      case 20 => ref20(attrs)
      case 21 => ref21(attrs)
      case 22 => ref22(attrs)
    }
  }
  def getNested(attrs: List[IndexNode], top: Boolean = false): jIterator[_] => Unit = {
    attrs.size match {
      case 1  => nested1(attrs, top)
      case 2  => nested2(attrs, top)
      case 3  => nested3(attrs, top)
      case 4  => nested4(attrs, top)
      case 5  => nested5(attrs, top)
      case 6  => nested6(attrs, top)
      case 7  => nested7(attrs, top)
      case 8  => nested8(attrs, top)
      case 9  => nested9(attrs, top)
      case 10 => nested10(attrs, top)
      case 11 => nested11(attrs, top)
      case 12 => nested12(attrs, top)
      case 13 => nested13(attrs, top)
      case 14 => nested14(attrs, top)
      case 15 => nested15(attrs, top)
      case 16 => nested16(attrs, top)
      case 17 => nested17(attrs, top)
      case 18 => nested18(attrs, top)
      case 19 => nested19(attrs, top)
      case 20 => nested20(attrs, top)
      case 21 => nested21(attrs, top)
      case 22 => nested22(attrs, top)
    }
  }


  def getList(nestedData: Any): jList[Any] = {
    if (nestedData.isInstanceOf[jList[_]])
      nestedData.asInstanceOf[jList[Any]]
    else
      nestedData.asInstanceOf[jMap[String, Any]].values().iterator.next.asInstanceOf[jList[Any]]
  }

  def nested1(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested2(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
          pack0(it)
          pack1(it)
          i += 1
          //          next()
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
            }
            end()
        }
  }

  def nested3(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    val pack2 = getPackingLambda(attrs(2), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
          pack0(it)
          pack1(it)
          pack2(it)
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
            }
            end()
        }
  }

  def nested4(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    val pack2 = getPackingLambda(attrs(2), top)
    val pack3 = getPackingLambda(attrs(3), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested5(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    val pack2 = getPackingLambda(attrs(2), top)
    val pack3 = getPackingLambda(attrs(3), top)
    val pack4 = getPackingLambda(attrs(4), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested6(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    val pack2 = getPackingLambda(attrs(2), top)
    val pack3 = getPackingLambda(attrs(3), top)
    val pack4 = getPackingLambda(attrs(4), top)
    val pack5 = getPackingLambda(attrs(5), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested7(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    val pack2 = getPackingLambda(attrs(2), top)
    val pack3 = getPackingLambda(attrs(3), top)
    val pack4 = getPackingLambda(attrs(4), top)
    val pack5 = getPackingLambda(attrs(5), top)
    val pack6 = getPackingLambda(attrs(6), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested8(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    val pack2 = getPackingLambda(attrs(2), top)
    val pack3 = getPackingLambda(attrs(3), top)
    val pack4 = getPackingLambda(attrs(4), top)
    val pack5 = getPackingLambda(attrs(5), top)
    val pack6 = getPackingLambda(attrs(6), top)
    val pack7 = getPackingLambda(attrs(7), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested9(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    val pack2 = getPackingLambda(attrs(2), top)
    val pack3 = getPackingLambda(attrs(3), top)
    val pack4 = getPackingLambda(attrs(4), top)
    val pack5 = getPackingLambda(attrs(5), top)
    val pack6 = getPackingLambda(attrs(6), top)
    val pack7 = getPackingLambda(attrs(7), top)
    val pack8 = getPackingLambda(attrs(8), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested10(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head, top)
    val pack1 = getPackingLambda(attrs(1), top)
    val pack2 = getPackingLambda(attrs(2), top)
    val pack3 = getPackingLambda(attrs(3), top)
    val pack4 = getPackingLambda(attrs(4), top)
    val pack5 = getPackingLambda(attrs(5), top)
    val pack6 = getPackingLambda(attrs(6), top)
    val pack7 = getPackingLambda(attrs(7), top)
    val pack8 = getPackingLambda(attrs(8), top)
    val pack9 = getPackingLambda(attrs(9), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested11(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested12(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested13(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested14(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested15(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    val pack14 = getPackingLambda(attrs(14), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested16(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    val pack14 = getPackingLambda(attrs(14), top)
    val pack15 = getPackingLambda(attrs(15), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested17(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    val pack14 = getPackingLambda(attrs(14), top)
    val pack15 = getPackingLambda(attrs(15), top)
    val pack16 = getPackingLambda(attrs(16), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested18(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    val pack14 = getPackingLambda(attrs(14), top)
    val pack15 = getPackingLambda(attrs(15), top)
    val pack16 = getPackingLambda(attrs(16), top)
    val pack17 = getPackingLambda(attrs(17), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested19(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    val pack14 = getPackingLambda(attrs(14), top)
    val pack15 = getPackingLambda(attrs(15), top)
    val pack16 = getPackingLambda(attrs(16), top)
    val pack17 = getPackingLambda(attrs(17), top)
    val pack18 = getPackingLambda(attrs(18), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested20(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    val pack14 = getPackingLambda(attrs(14), top)
    val pack15 = getPackingLambda(attrs(15), top)
    val pack16 = getPackingLambda(attrs(16), top)
    val pack17 = getPackingLambda(attrs(17), top)
    val pack18 = getPackingLambda(attrs(18), top)
    val pack19 = getPackingLambda(attrs(19), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested21(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    val pack14 = getPackingLambda(attrs(14), top)
    val pack15 = getPackingLambda(attrs(15), top)
    val pack16 = getPackingLambda(attrs(16), top)
    val pack17 = getPackingLambda(attrs(17), top)
    val pack18 = getPackingLambda(attrs(18), top)
    val pack19 = getPackingLambda(attrs(19), top)
    val pack20 = getPackingLambda(attrs(20), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }

  def nested22(attrs: List[IndexNode], top: Boolean): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head, top)
    val pack1  = getPackingLambda(attrs(1), top)
    val pack2  = getPackingLambda(attrs(2), top)
    val pack3  = getPackingLambda(attrs(3), top)
    val pack4  = getPackingLambda(attrs(4), top)
    val pack5  = getPackingLambda(attrs(5), top)
    val pack6  = getPackingLambda(attrs(6), top)
    val pack7  = getPackingLambda(attrs(7), top)
    val pack8  = getPackingLambda(attrs(8), top)
    val pack9  = getPackingLambda(attrs(9), top)
    val pack10 = getPackingLambda(attrs(10), top)
    val pack11 = getPackingLambda(attrs(11), top)
    val pack12 = getPackingLambda(attrs(12), top)
    val pack13 = getPackingLambda(attrs(13), top)
    val pack14 = getPackingLambda(attrs(14), top)
    val pack15 = getPackingLambda(attrs(15), top)
    val pack16 = getPackingLambda(attrs(16), top)
    val pack17 = getPackingLambda(attrs(17), top)
    val pack18 = getPackingLambda(attrs(18), top)
    val pack19 = getPackingLambda(attrs(19), top)
    val pack20 = getPackingLambda(attrs(20), top)
    val pack21 = getPackingLambda(attrs(21), top)
    if (top)
      (rows: jIterator[_]) =>
        while (rows.hasNext && i < rowCount) {
          it = rows.next.asInstanceOf[jList[Any]].iterator
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
            end()
        }
  }


  def ref1(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
  }

  def ref2(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
  }

  def ref3(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    val pack2 = getPackingLambda(attrs(2))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
  }

  def ref4(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    val pack2 = getPackingLambda(attrs(2))
    val pack3 = getPackingLambda(attrs(3))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
  }

  def ref5(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    val pack2 = getPackingLambda(attrs(2))
    val pack3 = getPackingLambda(attrs(3))
    val pack4 = getPackingLambda(attrs(4))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
  }

  def ref6(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    val pack2 = getPackingLambda(attrs(2))
    val pack3 = getPackingLambda(attrs(3))
    val pack4 = getPackingLambda(attrs(4))
    val pack5 = getPackingLambda(attrs(5))
    (vs: jIterator[_]) =>
      it = vs.next.asInstanceOf[jMap[String, Any]].values().iterator
      pack0(it)
      pack1(it)
      pack2(it)
      pack3(it)
      pack4(it)
      pack5(it)
  }

  def ref7(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    val pack2 = getPackingLambda(attrs(2))
    val pack3 = getPackingLambda(attrs(3))
    val pack4 = getPackingLambda(attrs(4))
    val pack5 = getPackingLambda(attrs(5))
    val pack6 = getPackingLambda(attrs(6))
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

  def ref8(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    val pack2 = getPackingLambda(attrs(2))
    val pack3 = getPackingLambda(attrs(3))
    val pack4 = getPackingLambda(attrs(4))
    val pack5 = getPackingLambda(attrs(5))
    val pack6 = getPackingLambda(attrs(6))
    val pack7 = getPackingLambda(attrs(7))
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

  def ref9(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    val pack2 = getPackingLambda(attrs(2))
    val pack3 = getPackingLambda(attrs(3))
    val pack4 = getPackingLambda(attrs(4))
    val pack5 = getPackingLambda(attrs(5))
    val pack6 = getPackingLambda(attrs(6))
    val pack7 = getPackingLambda(attrs(7))
    val pack8 = getPackingLambda(attrs(8))
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

  def ref10(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0 = getPackingLambda(attrs.head)
    val pack1 = getPackingLambda(attrs(1))
    val pack2 = getPackingLambda(attrs(2))
    val pack3 = getPackingLambda(attrs(3))
    val pack4 = getPackingLambda(attrs(4))
    val pack5 = getPackingLambda(attrs(5))
    val pack6 = getPackingLambda(attrs(6))
    val pack7 = getPackingLambda(attrs(7))
    val pack8 = getPackingLambda(attrs(8))
    val pack9 = getPackingLambda(attrs(9))
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

  def ref11(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
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

  def ref12(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
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

  def ref13(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
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

  def ref14(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
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

  def ref15(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
    val pack14 = getPackingLambda(attrs(14))
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

  def ref16(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
    val pack14 = getPackingLambda(attrs(14))
    val pack15 = getPackingLambda(attrs(15))
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

  def ref17(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
    val pack14 = getPackingLambda(attrs(14))
    val pack15 = getPackingLambda(attrs(15))
    val pack16 = getPackingLambda(attrs(16))
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

  def ref18(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
    val pack14 = getPackingLambda(attrs(14))
    val pack15 = getPackingLambda(attrs(15))
    val pack16 = getPackingLambda(attrs(16))
    val pack17 = getPackingLambda(attrs(17))
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

  def ref19(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
    val pack14 = getPackingLambda(attrs(14))
    val pack15 = getPackingLambda(attrs(15))
    val pack16 = getPackingLambda(attrs(16))
    val pack17 = getPackingLambda(attrs(17))
    val pack18 = getPackingLambda(attrs(18))
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

  def ref20(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
    val pack14 = getPackingLambda(attrs(14))
    val pack15 = getPackingLambda(attrs(15))
    val pack16 = getPackingLambda(attrs(16))
    val pack17 = getPackingLambda(attrs(17))
    val pack18 = getPackingLambda(attrs(18))
    val pack19 = getPackingLambda(attrs(19))
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

  def ref21(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
    val pack14 = getPackingLambda(attrs(14))
    val pack15 = getPackingLambda(attrs(15))
    val pack16 = getPackingLambda(attrs(16))
    val pack17 = getPackingLambda(attrs(17))
    val pack18 = getPackingLambda(attrs(18))
    val pack19 = getPackingLambda(attrs(19))
    val pack20 = getPackingLambda(attrs(20))
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

  def ref22(attrs: List[IndexNode]): jIterator[_] => Unit = {
    val pack0  = getPackingLambda(attrs.head)
    val pack1  = getPackingLambda(attrs(1))
    val pack2  = getPackingLambda(attrs(2))
    val pack3  = getPackingLambda(attrs(3))
    val pack4  = getPackingLambda(attrs(4))
    val pack5  = getPackingLambda(attrs(5))
    val pack6  = getPackingLambda(attrs(6))
    val pack7  = getPackingLambda(attrs(7))
    val pack8  = getPackingLambda(attrs(8))
    val pack9  = getPackingLambda(attrs(9))
    val pack10 = getPackingLambda(attrs(10))
    val pack11 = getPackingLambda(attrs(11))
    val pack12 = getPackingLambda(attrs(12))
    val pack13 = getPackingLambda(attrs(13))
    val pack14 = getPackingLambda(attrs(14))
    val pack15 = getPackingLambda(attrs(15))
    val pack16 = getPackingLambda(attrs(16))
    val pack17 = getPackingLambda(attrs(17))
    val pack18 = getPackingLambda(attrs(18))
    val pack19 = getPackingLambda(attrs(19))
    val pack20 = getPackingLambda(attrs(20))
    val pack21 = getPackingLambda(attrs(21))
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
}
