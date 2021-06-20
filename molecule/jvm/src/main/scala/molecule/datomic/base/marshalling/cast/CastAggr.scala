package molecule.datomic.base.marshalling.cast

import java.lang.{Long => jLong}
import java.net.URI
import java.util.{Date, UUID, List => jList, Set => jSet}

class CastAggr(maxRows: Int) extends CastOptNested(maxRows) {


  protected val castAggrInt = (colIndex: Int) => {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toInt
  }

  protected val castAggrDouble = (colIndex: Int) => {
    val array = new Array[Double](maxRows)
    oneDoubleArrays = oneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Double]
  }


  // castAggrOneList -----------------------------------------------------

  protected val castAggrOneListString = (colIndex: Int) => {
    val array = new Array[List[String]](maxRows)
    listOneStringArrays = listOneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[String]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[String]
        list
      }
  }

  protected val castAggrOneListInt = (colIndex: Int) => {
    val array = new Array[List[Int]](maxRows)
    listOneIntArrays = listOneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Int]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[jLong].toInt
        list
      }
  }

  protected val castAggrOneListLong = (colIndex: Int) => {
    val array = new Array[List[Long]](maxRows)
    listOneLongArrays = listOneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Long]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Long]
        list
      }
  }

  protected val castAggrOneListDouble = (colIndex: Int) => {
    val array = new Array[List[Double]](maxRows)
    listOneDoubleArrays = listOneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Double]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Double]
        list
      }
  }

  protected val castAggrOneListBoolean = (colIndex: Int) => {
    val array = new Array[List[Boolean]](maxRows)
    listOneBooleanArrays = listOneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Boolean]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Boolean]
        list
      }
  }

  protected val castAggrOneListDate = (colIndex: Int) => {
    val array = new Array[List[Date]](maxRows)
    listOneDateArrays = listOneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Date]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Date]
        list
      }
  }

  protected val castAggrOneListUUID = (colIndex: Int) => {
    val array = new Array[List[UUID]](maxRows)
    listOneUUIDArrays = listOneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[UUID]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[UUID]
        list
      }
  }

  protected val castAggrOneListURI = (colIndex: Int) => {
    val array = new Array[List[URI]](maxRows)
    listOneURIArrays = listOneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[URI]
        while (it.hasNext)
          list = list :+ (it.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          })
        list
      }
  }

  protected val castAggrOneListBigInt = (colIndex: Int) => {
    val array = new Array[List[BigInt]](maxRows)
    listOneBigIntArrays = listOneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[BigInt]
        while (it.hasNext)
          list = list :+ BigInt(it.next.toString)
        list
      }
  }

  protected val castAggrOneListBigDecimal = (colIndex: Int) => {
    val array = new Array[List[BigDecimal]](maxRows)
    listOneBigDecimalArrays = listOneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[BigDecimal]
        while (it.hasNext)
          list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
        list
      }
  }


  // castAggrManyList -----------------------------------------------------

  protected val castAggrManyListString = (colIndex: Int) => {
    val array = new Array[List[Set[String]]](maxRows)
    listManyStringArrays = listManyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[String]
        while (it.hasNext)
          set += it.next.asInstanceOf[String]
        List(set)
      }
  }

  protected val castAggrManyListInt = (colIndex: Int) => {
    val array = new Array[List[Set[Int]]](maxRows)
    listManyIntArrays = listManyIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Int]
        while (it.hasNext)
          set += it.next.asInstanceOf[jLong].toInt
        List(set)
      }
  }

  protected val castAggrManyListLong = (colIndex: Int) => {
    val array = new Array[List[Set[Long]]](maxRows)
    listManyLongArrays = listManyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Long]
        while (it.hasNext)
          set += it.next.asInstanceOf[Long]
        List(set)
      }
  }

  protected val castAggrManyListDouble = (colIndex: Int) => {
    val array = new Array[List[Set[Double]]](maxRows)
    listManyDoubleArrays = listManyDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Double]
        while (it.hasNext)
          set += it.next.asInstanceOf[Double]
        List(set)
      }
  }

  protected val castAggrManyListBoolean = (colIndex: Int) => {
    val array = new Array[List[Set[Boolean]]](maxRows)
    listManyBooleanArrays = listManyBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Boolean]
        while (it.hasNext)
          set += it.next.asInstanceOf[Boolean]
        List(set)
      }
  }

  protected val castAggrManyListDate = (colIndex: Int) => {
    val array = new Array[List[Set[Date]]](maxRows)
    listManyDateArrays = listManyDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Date]
        while (it.hasNext)
          set += it.next.asInstanceOf[Date]
        List(set)
      }
  }

  protected val castAggrManyListUUID = (colIndex: Int) => {
    val array = new Array[List[Set[UUID]]](maxRows)
    listManyUUIDArrays = listManyUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[UUID]
        while (it.hasNext)
          set += it.next.asInstanceOf[UUID]
        List(set)
      }
  }

  protected val castAggrManyListURI = (colIndex: Int) => {
    val array = new Array[List[Set[URI]]](maxRows)
    listManyURIArrays = listManyURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[URI]
        while (it.hasNext)
          set += (it.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          })
        List(set)
      }
  }

  protected val castAggrManyListBigInt = (colIndex: Int) => {
    val array = new Array[List[Set[BigInt]]](maxRows)
    listManyBigIntArrays = listManyBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigInt]
        while (it.hasNext)
          set += BigInt(it.next.toString)
        List(set)
      }
  }

  protected val castAggrManyListBigDecimal = (colIndex: Int) => {
    val array = new Array[List[Set[BigDecimal]]](maxRows)
    listManyBigDecimalArrays = listManyBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigDecimal]
        while (it.hasNext)
          set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
        List(set)
      }
  }


  // castAggrOneListDistinct -----------------------------------------------------

  protected val castAggrOneListDistinctString = (colIndex: Int) => {
    val array = new Array[List[String]](maxRows)
    listOneStringArrays = listOneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[String]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[String]
        list
      }
  }

  protected val castAggrOneListDistinctInt = (colIndex: Int) => {
    val array = new Array[List[Int]](maxRows)
    listOneIntArrays = listOneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[Int]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[jLong].toInt
        list
      }
  }

  protected val castAggrOneListDistinctLong = (colIndex: Int) => {
    val array = new Array[List[Long]](maxRows)
    listOneLongArrays = listOneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[Long]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Long]
        list
      }
  }

  protected val castAggrOneListDistinctDouble = (colIndex: Int) => {
    val array = new Array[List[Double]](maxRows)
    listOneDoubleArrays = listOneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[Double]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Double]
        list
      }
  }

  protected val castAggrOneListDistinctBoolean = (colIndex: Int) => {
    val array = new Array[List[Boolean]](maxRows)
    listOneBooleanArrays = listOneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[Boolean]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Boolean]
        list
      }
  }

  protected val castAggrOneListDistinctDate = (colIndex: Int) => {
    val array = new Array[List[Date]](maxRows)
    listOneDateArrays = listOneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[Date]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Date]
        list
      }
  }

  protected val castAggrOneListDistinctUUID = (colIndex: Int) => {
    val array = new Array[List[UUID]](maxRows)
    listOneUUIDArrays = listOneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[UUID]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[UUID]
        list
      }
  }

  protected val castAggrOneListDistinctURI = (colIndex: Int) => {
    val array = new Array[List[URI]](maxRows)
    listOneURIArrays = listOneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[URI]
        while (it.hasNext)
          list = list :+ (it.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          })
        list
      }
  }

  protected val castAggrOneListDistinctBigInt = (colIndex: Int) => {
    val array = new Array[List[BigInt]](maxRows)
    listOneBigIntArrays = listOneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[BigInt]
        while (it.hasNext)
          list = list :+ BigInt(it.next.toString)
        list
      }
  }

  protected val castAggrOneListDistinctBigDecimal = (colIndex: Int) => {
    val array = new Array[List[BigDecimal]](maxRows)
    listOneBigDecimalArrays = listOneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var list = List.empty[BigDecimal]
        while (it.hasNext)
          list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
        list
      }
  }


  // castAggrManyListDistinct -----------------------------------------------------

  protected val castAggrManyListDistinctString = (colIndex: Int) => {
    val array = new Array[List[Set[String]]](maxRows)
    listManyStringArrays = listManyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[String]
        while (it.hasNext)
          set += it.next.asInstanceOf[String]
        List(set)
      }
  }

  protected val castAggrManyListDistinctInt = (colIndex: Int) => {
    val array = new Array[List[Set[Int]]](maxRows)
    listManyIntArrays = listManyIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Int]
        while (it.hasNext)
          set += it.next.asInstanceOf[jLong].toInt
        List(set)
      }
  }

  protected val castAggrManyListDistinctLong = (colIndex: Int) => {
    val array = new Array[List[Set[Long]]](maxRows)
    listManyLongArrays = listManyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Long]
        while (it.hasNext)
          set += it.next.asInstanceOf[Long]
        List(set)
      }
  }

  protected val castAggrManyListDistinctDouble = (colIndex: Int) => {
    val array = new Array[List[Set[Double]]](maxRows)
    listManyDoubleArrays = listManyDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Double]
        while (it.hasNext)
          set += it.next.asInstanceOf[Double]
        List(set)
      }
  }

  protected val castAggrManyListDistinctBoolean = (colIndex: Int) => {
    val array = new Array[List[Set[Boolean]]](maxRows)
    listManyBooleanArrays = listManyBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Boolean]
        while (it.hasNext)
          set += it.next.asInstanceOf[Boolean]
        List(set)
      }
  }

  protected val castAggrManyListDistinctDate = (colIndex: Int) => {
    val array = new Array[List[Set[Date]]](maxRows)
    listManyDateArrays = listManyDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Date]
        while (it.hasNext)
          set += it.next.asInstanceOf[Date]
        List(set)
      }
  }

  protected val castAggrManyListDistinctUUID = (colIndex: Int) => {
    val array = new Array[List[Set[UUID]]](maxRows)
    listManyUUIDArrays = listManyUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[UUID]
        while (it.hasNext)
          set += it.next.asInstanceOf[UUID]
        List(set)
      }
  }

  protected val castAggrManyListDistinctURI = (colIndex: Int) => {
    val array = new Array[List[Set[URI]]](maxRows)
    listManyURIArrays = listManyURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[URI]
        while (it.hasNext)
          set += (it.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          })
        List(set)
      }
  }

  protected val castAggrManyListDistinctBigInt = (colIndex: Int) => {
    val array = new Array[List[Set[BigInt]]](maxRows)
    listManyBigIntArrays = listManyBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[BigInt]
        while (it.hasNext)
          set += BigInt(it.next.toString)
        List(set)
      }
  }

  protected val castAggrManyListDistinctBigDecimal = (colIndex: Int) => {
    val array = new Array[List[Set[BigDecimal]]](maxRows)
    listManyBigDecimalArrays = listManyBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[BigDecimal]
        while (it.hasNext)
          set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
        List(set)
      }
  }


  // castAggrOneListRand -----------------------------------------------------

  protected val castAggrOneListRandString = (colIndex: Int) => {
    val array = new Array[List[String]](maxRows)
    listOneStringArrays = listOneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[String]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[String]
        list
      }
  }

  protected val castAggrOneListRandInt = (colIndex: Int) => {
    val array = new Array[List[Int]](maxRows)
    listOneIntArrays = listOneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Int]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[jLong].toInt
        list
      }
  }

  protected val castAggrOneListRandLong = (colIndex: Int) => {
    val array = new Array[List[Long]](maxRows)
    listOneLongArrays = listOneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Long]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Long]
        list
      }
  }

  protected val castAggrOneListRandDouble = (colIndex: Int) => {
    val array = new Array[List[Double]](maxRows)
    listOneDoubleArrays = listOneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Double]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Double]
        list
      }
  }

  protected val castAggrOneListRandBoolean = (colIndex: Int) => {
    val array = new Array[List[Boolean]](maxRows)
    listOneBooleanArrays = listOneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Boolean]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Boolean]
        list
      }
  }

  protected val castAggrOneListRandDate = (colIndex: Int) => {
    val array = new Array[List[Date]](maxRows)
    listOneDateArrays = listOneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[Date]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[Date]
        list
      }
  }

  protected val castAggrOneListRandUUID = (colIndex: Int) => {
    val array = new Array[List[UUID]](maxRows)
    listOneUUIDArrays = listOneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[UUID]
        while (it.hasNext)
          list = list :+ it.next.asInstanceOf[UUID]
        list
      }
  }

  protected val castAggrOneListRandURI = (colIndex: Int) => {
    val array = new Array[List[URI]](maxRows)
    listOneURIArrays = listOneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[URI]
        while (it.hasNext)
          list = list :+ (it.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          })
        list
      }
  }

  protected val castAggrOneListRandBigInt = (colIndex: Int) => {
    val array = new Array[List[BigInt]](maxRows)
    listOneBigIntArrays = listOneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[BigInt]
        while (it.hasNext)
          list = list :+ BigInt(it.next.toString)
        list
      }
  }

  protected val castAggrOneListRandBigDecimal = (colIndex: Int) => {
    val array = new Array[List[BigDecimal]](maxRows)
    listOneBigDecimalArrays = listOneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var list = List.empty[BigDecimal]
        while (it.hasNext)
          list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
        list
      }
  }


  // castAggrManyListRand -----------------------------------------------------

  protected val castAggrManyListRandString = (colIndex: Int) => {
    val array = new Array[List[Set[String]]](maxRows)
    listManyStringArrays = listManyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[String]
        while (it.hasNext)
          set += it.next.asInstanceOf[String]
        List(set)
      }
  }

  protected val castAggrManyListRandInt = (colIndex: Int) => {
    val array = new Array[List[Set[Int]]](maxRows)
    listManyIntArrays = listManyIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Int]
        while (it.hasNext)
          set += it.next.asInstanceOf[jLong].toInt
        List(set)
      }
  }

  protected val castAggrManyListRandLong = (colIndex: Int) => {
    val array = new Array[List[Set[Long]]](maxRows)
    listManyLongArrays = listManyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Long]
        while (it.hasNext)
          set += it.next.asInstanceOf[Long]
        List(set)
      }
  }

  protected val castAggrManyListRandDouble = (colIndex: Int) => {
    val array = new Array[List[Set[Double]]](maxRows)
    listManyDoubleArrays = listManyDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Double]
        while (it.hasNext)
          set += it.next.asInstanceOf[Double]
        List(set)
      }
  }

  protected val castAggrManyListRandBoolean = (colIndex: Int) => {
    val array = new Array[List[Set[Boolean]]](maxRows)
    listManyBooleanArrays = listManyBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Boolean]
        while (it.hasNext)
          set += it.next.asInstanceOf[Boolean]
        List(set)
      }
  }

  protected val castAggrManyListRandDate = (colIndex: Int) => {
    val array = new Array[List[Set[Date]]](maxRows)
    listManyDateArrays = listManyDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[Date]
        while (it.hasNext)
          set += it.next.asInstanceOf[Date]
        List(set)
      }
  }

  protected val castAggrManyListRandUUID = (colIndex: Int) => {
    val array = new Array[List[Set[UUID]]](maxRows)
    listManyUUIDArrays = listManyUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[UUID]
        while (it.hasNext)
          set += it.next.asInstanceOf[UUID]
        List(set)
      }
  }

  protected val castAggrManyListRandURI = (colIndex: Int) => {
    val array = new Array[List[Set[URI]]](maxRows)
    listManyURIArrays = listManyURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[URI]
        while (it.hasNext)
          set += (it.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          })
        List(set)
      }
  }

  protected val castAggrManyListRandBigInt = (colIndex: Int) => {
    val array = new Array[List[Set[BigInt]]](maxRows)
    listManyBigIntArrays = listManyBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigInt]
        while (it.hasNext)
          set += BigInt(it.next.toString)
        List(set)
      }
  }

  protected val castAggrManyListRandBigDecimal = (colIndex: Int) => {
    val array = new Array[List[Set[BigDecimal]]](maxRows)
    listManyBigDecimalArrays = listManyBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigDecimal]
        while (it.hasNext)
          set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
        List(set)
      }
  }


  // castAggrSingleSample -----------------------------------------------------

  protected val castAggrSingleSampleString = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[String]
  }

  protected val castAggrSingleSampleInt = (colIndex: Int) => {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[jLong].toInt
  }

  // todo Long" | "ref" | "datom
  protected val castAggrSingleSampleLong  = (colIndex: Int) => {
    val array = new Array[Long](maxRows)
    oneLongArrays = oneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Long]
  }

  protected val castAggrSingleSampleDouble = (colIndex: Int) => {
    val array = new Array[Double](maxRows)
    oneDoubleArrays = oneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Double]
  }

  protected val castAggrSingleSampleBoolean = (colIndex: Int) => {
    val array = new Array[Boolean](maxRows)
    oneBooleanArrays = oneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Boolean]
  }

  protected val castAggrSingleSampleDate = (colIndex: Int) => {
    val array = new Array[Date](maxRows)
    oneDateArrays = oneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]
  }

  protected val castAggrSingleSampleUUID = (colIndex: Int) => {
    val array = new Array[UUID](maxRows)
    oneUUIDArrays = oneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[UUID]
  }

  protected val castAggrSingleSampleURI = (colIndex: Int) => {
    val array = new Array[URI](maxRows)
    oneURIArrays = oneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      }
  }

  protected val castAggrSingleSampleBigInt = (colIndex: Int) => {
    val array = new Array[BigInt](maxRows)
    oneBigIntArrays = oneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigInt(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val castAggrSingleSampleBigDecimal = (colIndex: Int) => {
    val array = new Array[BigDecimal](maxRows)
    oneBigDecimalArrays = oneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigDecimal(
        row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[java.math.BigDecimal].toString
      )
  }


  // castAggrOneSingle -----------------------------------------------------

  protected val castAggrOneSingleString = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[String]
  }

  protected val castAggrOneSingleInt = (colIndex: Int) => {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[jLong].toInt
  }

  // todo Long | "ref" | "datom
  protected val castAggrOneSingleLong = (colIndex: Int) => {
    val array = new Array[Long](maxRows)
    oneLongArrays = oneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Long]
  }

  protected val castAggrOneSingleDouble = (colIndex: Int) => {
    val array = new Array[Double](maxRows)
    oneDoubleArrays = oneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Double]
  }

  protected val castAggrOneSingleBoolean = (colIndex: Int) => {
    val array = new Array[Boolean](maxRows)
    oneBooleanArrays = oneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Boolean]
  }

  protected val castAggrOneSingleDate = (colIndex: Int) => {
    val array = new Array[Date](maxRows)
    oneDateArrays = oneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]
  }

  protected val castAggrOneSingleUUID = (colIndex: Int) => {
    val array = new Array[UUID](maxRows)
    oneUUIDArrays = oneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[UUID]
  }

  protected val castAggrOneSingleURI = (colIndex: Int) => {
    val array = new Array[URI](maxRows)
    oneURIArrays = oneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      }
  }

  protected val castAggrOneSingleBigInt = (colIndex: Int) => {
    val array = new Array[BigInt](maxRows)
    oneBigIntArrays = oneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigInt(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val castAggrOneSingleBigDecimal = (colIndex: Int) => {
    val array = new Array[BigDecimal](maxRows)
    oneBigDecimalArrays = oneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigDecimal(
        row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[java.math.BigDecimal].toString
      )
  }


  // castAggrManySingle -----------------------------------------------------

  protected val castAggrManySingleString = (colIndex: Int) => {
    val array = new Array[Set[String]](maxRows)
    manyStringArrays = manyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(row.get(colIndex).asInstanceOf[String])
  }

  protected val castAggrManySingleInt = (colIndex: Int) => {
    val array = new Array[Set[Int]](maxRows)
    manyIntArrays = manyIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(row.get(colIndex).asInstanceOf[jLong].toInt)
  }

  protected val castAggrManySingleLong = (colIndex: Int) => {
    val array = new Array[Set[Long]](maxRows)
    manyLongArrays = manyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(row.get(colIndex).asInstanceOf[Long])
  }

  protected val castAggrManySingleDouble = (colIndex: Int) => {
    val array = new Array[Set[Double]](maxRows)
    manyDoubleArrays = manyDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(row.get(colIndex).asInstanceOf[Double])
  }

  protected val castAggrManySingleBoolean = (colIndex: Int) => {
    val array = new Array[Set[Boolean]](maxRows)
    manyBooleanArrays = manyBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(row.get(colIndex).asInstanceOf[Boolean])
  }

  protected val castAggrManySingleDate = (colIndex: Int) => {
    val array = new Array[Set[Date]](maxRows)
    manyDateArrays = manyDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(row.get(colIndex).asInstanceOf[Date])
  }

  protected val castAggrManySingleUUID = (colIndex: Int) => {
    val array = new Array[Set[UUID]](maxRows)
    manyUUIDArrays = manyUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(row.get(colIndex).asInstanceOf[UUID])
  }

  protected val castAggrManySingleURI = (colIndex: Int) => {
    val array = new Array[Set[URI]](maxRows)
    manyURIArrays = manyURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(row.get(colIndex) match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
  }

  protected val castAggrManySingleBigInt = (colIndex: Int) => {
    val array = new Array[Set[BigInt]](maxRows)
    manyBigIntArrays = manyBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(BigInt(row.get(colIndex).toString))
  }

  protected val castAggrManySingleBigDecimal = (colIndex: Int) => {
    val array = new Array[Set[BigDecimal]](maxRows)
    manyBigDecimalArrays = manyBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = Set(BigDecimal(row.get(colIndex).asInstanceOf[java.math.BigDecimal].toString))
  }
}
