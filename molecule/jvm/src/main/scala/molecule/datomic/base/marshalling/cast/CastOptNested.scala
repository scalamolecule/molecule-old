package molecule.datomic.base.marshalling.cast

import java.lang.{Double => jDouble, Long => jLong}
import java.net.URI
import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import clojure.lang.Keyword
import molecule.core.marshalling.Column
import molecule.core.util.{DateHandling, Helpers}
import molecule.datomic.base.marshalling.DateStrLocal

class CastOptNested(maxRows: Int) extends DataArrays
//{
//
//
//  // castOptNestedOne -----------------------------------------------------
////  protected def castOptNestedOneEnum(it: jIterator[_]): String =
////    getKwName(it.next.asInstanceOf[jMap[_, _]].values().iterator().next.toString)
//
//  protected val castOptNestedOneEnum = (colIndex: Int) => {
//    val array = new Array[String](maxRows)
//    oneStringArrays = oneStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = getKwName(it.next.asInstanceOf[jMap[_, _]].values().iterator().next.toString)
//  }
//
//  protected val castOptNestedOneString = (colIndex: Int) => {
//    val array = new Array[String](maxRows)
//    oneStringArrays = oneStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.asInstanceOf[String]
//  }
//
//  protected val castOptNestedOneInt    = (colIndex: Int) => {
//    val array = new Array[Int](maxRows)
//    oneIntArrays = oneIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.asInstanceOf[jLong].toInt
//  }
//
//  protected val castOptNestedOneInt2 = (colIndex: Int) => {
//    val array = new Array[Int](maxRows)
//    oneIntArrays = oneIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.toString.toInt
//  }
//
//  protected val castOptNestedOneFloat = (colIndex: Int) => {
//    val array = new Array[Float](maxRows)
//    oneFloatArrays = oneFloatArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.asInstanceOf[jDouble].toFloat
//  }
//
//  // todo: Long | ref | datom
//  protected val castOptNestedOneLong = (colIndex: Int) => {
//    val array = new Array[Long](maxRows)
//    oneLongArrays = oneLongArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.asInstanceOf[Long]
//  }
//
//  protected val castOptNestedOneDouble = (colIndex: Int) => {
//    val array = new Array[Double](maxRows)
//    oneDoubleArrays = oneDoubleArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.asInstanceOf[Double]
//  }
//
//  protected val castOptNestedOneBoolean = (colIndex: Int) => {
//    val array = new Array[Boolean](maxRows)
//    oneBooleanArrays = oneBooleanArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.asInstanceOf[Boolean]
//  }
//
//  protected val castOptNestedOneDate = (colIndex: Int) => {
//    val array = new Array[Date](maxRows)
//    oneDateArrays = oneDateArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.asInstanceOf[Date]
//  }
//
//  protected val castOptNestedOneUUID = (colIndex: Int) => {
//    val array = new Array[UUID](maxRows)
//    oneUUIDArrays = oneUUIDArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next.asInstanceOf[UUID]
//  }
//
//  protected val castOptNestedOneURI = (colIndex: Int) => {
//    val array = new Array[URI](maxRows)
//    oneURIArrays = oneURIArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case uri: URI => uri
//        case uriImpl  => new URI(uriImpl.toString)
//      }
//  }
//
//  protected val castOptNestedOneBigInt = (colIndex: Int) => {
//    val array = new Array[BigInt](maxRows)
//    oneBigIntArrays = oneBigIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = BigInt(it.next.toString)
//  }
//
//  protected val castOptNestedOneBigDecimal = (colIndex: Int) => {
//    val array = new Array[BigDecimal](maxRows)
//    oneBigDecimalArrays = oneBigDecimalArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = BigDecimal(it.next.toString)
//  }
//
//  protected val castOptNestedOneRefAttr = (colIndex: Int) => {
//    val array = new Array[BigDecimal](maxRows)
//    oneBigDecimalArrays = oneBigDecimalArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next
//        .asInstanceOf[jMap[_, _]].values().iterator().next
//        .asInstanceOf[jLong].toLong
//  }
//
//  // Generic `v` attribute value converted to String}
//  protected val castOptNestedOneAny = (colIndex: Int) => {
//    val array = new Array[String](maxRows)
//    oneStringArrays = oneStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        //            case d: Date if col.attrExpr == "txInstant" => date2strLocal(d)
//        //            case d: Date                                => date2str(d)
//        case d: Date => date2strLocal(d)
//        case other   => other.toString
//      }
//  }
//
//
//  // castOptNestedOptOne -----------------------------------------------------
//
//  protected val castOptNestedOptOneEnum = (colIndex: Int) => {
//    val array = new Array[Option[String]](maxRows)
//    optOneStringArrays = optOneStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[String]
//        case v    => Some(
//          getKwName(
//            v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
//              .asInstanceOf[jMap[_, _]].values.iterator.next.toString
//          )
//        )
//      }
//  }
//
//  protected val castOptNestedOptOneString = (colIndex: Int) => {
//    val array = new Array[Option[String]](maxRows)
//    optOneStringArrays = optOneStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[String]
//        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
//          .asInstanceOf[jMap[String, Keyword]].values.iterator.next.getName)
//      }
//  }
//
//  protected val castOptNestedOptOneInt = (colIndex: Int) => {
//    val array = new Array[Option[Int]](maxRows)
//    optOneIntArrays = optOneIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case "__none__" => Option.empty[Int]
//        case v          => Some(v.asInstanceOf[jLong].toInt)
//      }
//  }
//
//  protected val castOptNestedOptOneFloat = (colIndex: Int) => {
//    val array = new Array[Option[Float]](maxRows)
//    optOneFloatArrays = optOneFloatArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Float]
//        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jDouble].toFloat)
//      }
//  }
//
//  protected val castOptNestedOptOneLong = (colIndex: Int) => {
//    val array = new Array[Option[Long]](maxRows)
//    optOneLongArrays = optOneLongArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Long]
//        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jLong].toLong)
//      }
//  }
//
//  protected val castOptNestedOptOneDouble = (colIndex: Int) => {
//    val array = new Array[Option[Double]](maxRows)
//    optOneDoubleArrays = optOneDoubleArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Double]
//        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jDouble].toDouble)
//      }
//  }
//
//  protected val castOptNestedOptOneBoolean = (colIndex: Int) => {
//    val array = new Array[Option[Boolean]](maxRows)
//    optOneBooleanArrays = optOneBooleanArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Boolean]
//        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Boolean])
//      }
//  }
//
//  protected val castOptNestedOptOneDate = (colIndex: Int) => {
//    val array = new Array[Option[Date]](maxRows)
//    optOneDateArrays = optOneDateArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Date]
//        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Date])
//      }
//  }
//
//  protected val castOptNestedOptOneUUID = (colIndex: Int) => {
//    val array = new Array[Option[UUID]](maxRows)
//    optOneUUIDArrays = optOneUUIDArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[UUID]
//        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[UUID])
//      }
//  }
//
//  protected val castOptNestedOptOneURI = (colIndex: Int) => {
//    val array = new Array[Option[URI]](maxRows)
//    optOneURIArrays = optOneURIArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[URI]
//        case v    => v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next match {
//          case uri: URI => Some(uri)
//          case uriImpl  => Some(new URI(uriImpl.toString))
//        }
//      }
//  }
//
//  protected val castOptNestedOptOneBigInt = (colIndex: Int) => {
//    val array = new Array[Option[BigInt]](maxRows)
//    optOneBigIntArrays = optOneBigIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[BigInt]
//        case v    => Some(BigInt(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
//      }
//  }
//
//  protected val castOptNestedOptOneBigDecimal = (colIndex: Int) => {
//    val array = new Array[Option[BigDecimal]](maxRows)
//    optOneBigDecimalArrays = optOneBigDecimalArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[BigDecimal]
//        case v    => Some(BigDecimal(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
//      }
//  }
//
//
//  // castOptNestedMany -----------------------------------------------------
//
//  protected val castOpNestedManyEnum = (colIndex: Int) => {
//    val array = new Array[Set[String]](maxRows)
//    manyStringArrays = manyStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[String]
//        while (it.hasNext)
//          set += it.next.toString
//        set
//      }
//  }
//
//  protected val castOpNestedManyString = (colIndex: Int) => {
//    val array = new Array[Set[String]](maxRows)
//    manyStringArrays = manyStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[String]
//        while (it.hasNext)
//          set += it.next.asInstanceOf[String]
//        set
//      }
//  }
//
//  protected val castOpNestedManyInt = (colIndex: Int) => {
//    val array = new Array[Set[Int]](maxRows)
//    manyIntArrays = manyIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[Int]
//        while (it.hasNext)
//          set += it.next.asInstanceOf[jLong].toInt
//        set
//      }
//  }
//
//  protected val castOpNestedManyFloat = (colIndex: Int) => {
//    val array = new Array[Set[Float]](maxRows)
//    manyFloatArrays = manyFloatArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[Float]
//        while (it.hasNext)
//          set += it.next.asInstanceOf[jDouble].toFloat
//        set
//      }
//  }
//
//  protected val castOpNestedManyLong = (colIndex: Int) => {
//    val array = new Array[Set[Long]](maxRows)
//    manyLongArrays = manyLongArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[Long]
//        while (it.hasNext)
//          set += it.next.asInstanceOf[Long]
//        set
//      }
//  }
//
//  protected val castOpNestedManyDouble = (colIndex: Int) => {
//    val array = new Array[Set[Double]](maxRows)
//    manyDoubleArrays = manyDoubleArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[Double]
//        while (it.hasNext)
//          set += it.next.asInstanceOf[Double]
//        set
//      }
//  }
//
//  protected val castOpNestedManyBoolean = (colIndex: Int) => {
//    val array = new Array[Set[Boolean]](maxRows)
//    manyBooleanArrays = manyBooleanArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[Boolean]
//        while (it.hasNext)
//          set += it.next.asInstanceOf[Boolean]
//        set
//      }
//  }
//
//  protected val castOpNestedManyDate = (colIndex: Int) => {
//    val array = new Array[Set[Date]](maxRows)
//    manyDateArrays = manyDateArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[Date]
//        while (it.hasNext)
//          set += it.next.asInstanceOf[Date]
//        set
//      }
//  }
//
//  protected val castOpNestedManyUUID = (colIndex: Int) => {
//    val array = new Array[Set[UUID]](maxRows)
//    manyUUIDArrays = manyUUIDArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[UUID]
//        while (it.hasNext)
//          set += it.next.asInstanceOf[UUID]
//        set
//      }
//  }
//
//  protected val castOpNestedManyURI = (colIndex: Int) => {
//    val array = new Array[Set[URI]](maxRows)
//    manyURIArrays = manyURIArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[URI]
//        while (it.hasNext)
//          set += (it.next match {
//            case uri: URI => uri
//            case uriImpl  => new URI(uriImpl.toString)
//          })
//        set
//      }
//  }
//
//  protected val castOpNestedManyBigInt = (colIndex: Int) => {
//    val array = new Array[Set[BigInt]](maxRows)
//    manyBigIntArrays = manyBigIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[BigInt]
//        while (it.hasNext)
//          set += BigInt(it.next.toString)
//        set
//      }
//  }
//
//  protected val castOpNestedManyBigDecimal = (colIndex: Int) => {
//    val array = new Array[Set[BigDecimal]](maxRows)
//    manyBigDecimalArrays = manyBigDecimalArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var set = Set.empty[BigDecimal]
//        while (it.hasNext)
//          set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
//        set
//      }
//  }
//
//
//  // castOptNestedOptMany -----------------------------------------------------
//
//  protected val castOptNestedOptManyEnum = (colIndex: Int) => {
//    val array = new Array[Option[Set[String]]](maxRows)
//    optManyStringArrays = optManyStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[String]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[String]
//          while (it.hasNext)
//            set += getKwName(it.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyString = (colIndex: Int) => {
//    val array = new Array[Option[Set[String]]](maxRows)
//    optManyStringArrays = optManyStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[String]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[String]
//          while (it.hasNext)
//            set += it.next.asInstanceOf[String]
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyInt = (colIndex: Int) => {
//    val array = new Array[Option[Set[Int]]](maxRows)
//    optManyIntArrays = optManyIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[Int]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[Int]
//          while (it.hasNext)
//            set += it.next.asInstanceOf[jLong].toInt
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyFloat = (colIndex: Int) => {
//    val array = new Array[Option[Set[Float]]](maxRows)
//    optManyFloatArrays = optManyFloatArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[Float]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[Float]
//          while (it.hasNext)
//            set += it.next.asInstanceOf[jDouble].toFloat
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyLong = (colIndex: Int) => {
//    val array = new Array[Option[Set[Long]]](maxRows)
//    optManyLongArrays = optManyLongArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[Long]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[Long]
//          while (it.hasNext)
//            set += it.next.asInstanceOf[jLong].toLong
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyDouble = (colIndex: Int) => {
//    val array = new Array[Option[Set[Double]]](maxRows)
//    optManyDoubleArrays = optManyDoubleArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[Double]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[Double]
//          while (it.hasNext)
//            set += it.next.asInstanceOf[jDouble].toDouble
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyBoolean = (colIndex: Int) => {
//    val array = new Array[Option[Set[Boolean]]](maxRows)
//    optManyBooleanArrays = optManyBooleanArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[Boolean]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[Boolean]
//          while (it.hasNext)
//            set += it.next.asInstanceOf[Boolean]
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyDate = (colIndex: Int) => {
//    val array = new Array[Option[Set[Date]]](maxRows)
//    optManyDateArrays = optManyDateArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[Date]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[Date]
//          while (it.hasNext)
//            set += it.next.asInstanceOf[Date]
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyUUID = (colIndex: Int) => {
//    val array = new Array[Option[Set[UUID]]](maxRows)
//    optManyUUIDArrays = optManyUUIDArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[UUID]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[UUID]
//          while (it.hasNext)
//            set += it.next.asInstanceOf[UUID]
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyURI = (colIndex: Int) => {
//    val array = new Array[Option[Set[URI]]](maxRows)
//    optManyURIArrays = optManyURIArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[URI]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[URI]
//          while (it.hasNext)
//            set += (it.next match {
//              case uri: URI => uri
//              case uriImpl  => new URI(uriImpl.toString)
//            })
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyBigInt = (colIndex: Int) => {
//    val array = new Array[Option[Set[BigInt]]](maxRows)
//    optManyBigIntArrays = optManyBigIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[BigInt]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[BigInt]
//          while (it.hasNext)
//            set += BigInt(it.next.toString)
//          Some(set)
//      }
//  }
//
//  protected val castOptNestedOptManyBigDecimal = (colIndex: Int) => {
//    val array = new Array[Option[Set[BigDecimal]]](maxRows)
//    optManyBigDecimalArrays = optManyBigDecimalArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Set[BigDecimal]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var set = Set.empty[BigDecimal]
//          while (it.hasNext)
//            set += BigDecimal(it.next.toString)
//          Some(set)
//      }
//  }
//
//
//  // castOptNestedMap -----------------------------------------------------
//
//  protected val castOptNestedMapString = (colIndex: Int) => {
//    val array = new Array[Map[String, String]](maxRows)
//    mapStringArrays = mapStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, String]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> vs(1))
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapInt = (colIndex: Int) => {
//    val array = new Array[Map[String, Int]](maxRows)
//    mapIntArrays = mapIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, Int]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> vs(1).toInt)
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapFloat = (colIndex: Int) => {
//    val array = new Array[Map[String, Float]](maxRows)
//    mapFloatArrays = mapFloatArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, Float]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> vs(1).toFloat)
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapLong = (colIndex: Int) => {
//    val array = new Array[Map[String, Long]](maxRows)
//    mapLongArrays = mapLongArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, Long]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> vs(1).toLong)
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapDouble = (colIndex: Int) => {
//    val array = new Array[Map[String, Double]](maxRows)
//    mapDoubleArrays = mapDoubleArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, Double]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> vs(1).toDouble)
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapBoolean = (colIndex: Int) => {
//    val array = new Array[Map[String, Boolean]](maxRows)
//    mapBooleanArrays = mapBooleanArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, Boolean]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> vs(1).toBoolean)
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapDate = (colIndex: Int) => {
//    val array = new Array[Map[String, Date]](maxRows)
//    mapDateArrays = mapDateArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, Date]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> str2date(vs(1)))
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapUUID = (colIndex: Int) => {
//    val array = new Array[Map[String, UUID]](maxRows)
//    mapUUIDArrays = mapUUIDArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, UUID]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> UUID.fromString(vs(1)))
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapURI = (colIndex: Int) => {
//    val array = new Array[Map[String, URI]](maxRows)
//    mapURIArrays = mapURIArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, URI]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> new URI(vs(1)))
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapBigInt = (colIndex: Int) => {
//    val array = new Array[Map[String, BigInt]](maxRows)
//    mapBigIntArrays = mapBigIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, BigInt]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> BigInt(vs(1)))
//        }
//        map
//      }
//  }
//
//  protected val castOptNestedMapBigDecimal = (colIndex: Int) => {
//    val array = new Array[Map[String, BigDecimal]](maxRows)
//    mapBigDecimalArrays = mapBigDecimalArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = {
//        val it  = it.next.asInstanceOf[jSet[_]].iterator
//        var map = Map.empty[String, BigDecimal]
//        var vs  = new Array[String](2)
//        while (it.hasNext) {
//          vs = it.next.toString.split("@", 2)
//          map += (vs(0) -> BigDecimal(vs(1)))
//        }
//        map
//      }
//  }
//
//
//  // castOptNestedOptMap -----------------------------------------------------
//
//  protected val castOptNestedOptMapString = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, String]]](maxRows)
//    optMapStringArrays = optMapStringArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, String]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, String]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> vs(1))
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapInt = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, Int]]](maxRows)
//    optMapIntArrays = optMapIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, Int]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, Int]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> vs(1).toInt)
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapFloat = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, Float]]](maxRows)
//    optMapFloatArrays = optMapFloatArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, Float]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, Float]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> vs(1).toFloat)
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapLong = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, Long]]](maxRows)
//    optMapLongArrays = optMapLongArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, Long]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, Long]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> vs(1).toLong)
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapDouble = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, Double]]](maxRows)
//    optMapDoubleArrays = optMapDoubleArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, Double]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, Double]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> vs(1).toDouble)
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapBoolean = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, Boolean]]](maxRows)
//    optMapBooleanArrays = optMapBooleanArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, Boolean]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, Boolean]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> vs(1).toBoolean)
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapDate = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, Date]]](maxRows)
//    optMapDateArrays = optMapDateArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, Date]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, Date]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> str2date(vs(1)))
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapUUID = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, UUID]]](maxRows)
//    optMapUUIDArrays = optMapUUIDArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, UUID]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, UUID]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> UUID.fromString(vs(1)))
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapURI = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, URI]]](maxRows)
//    optMapURIArrays = optMapURIArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, URI]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, URI]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> new URI(vs(1)))
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapBigInt = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, BigInt]]](maxRows)
//    optMapBigIntArrays = optMapBigIntArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, BigInt]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, BigInt]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> BigInt(vs(1)))
//          }
//          Some(map)
//      }
//  }
//
//  protected val castOptNestedOptMapBigDecimal = (colIndex: Int) => {
//    val array = new Array[Option[Map[String, BigDecimal]]](maxRows)
//    optMapBigDecimalArrays = optMapBigDecimalArrays :+ array
//    (it: jIterator[_], i: Int) =>
//      array(i) = it.next match {
//        case null => Option.empty[Map[String, BigDecimal]]
//        case v    =>
//          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//          var map = Map.empty[String, BigDecimal]
//          var vs  = new Array[String](2)
//          while (it.hasNext) {
//            vs = it.next.toString.split("@", 2)
//            map += (vs(0) -> BigDecimal(vs(1)))
//          }
//          Some(map)
//      }
//  }
//
//
//
//
//}