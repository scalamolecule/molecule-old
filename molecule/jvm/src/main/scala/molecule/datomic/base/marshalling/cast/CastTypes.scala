package molecule.datomic.base.marshalling.cast

import java.lang.{Double => jDouble, Long => jLong}
import java.net.URI
import java.util.{Date, UUID, List => jList, Map => jMap, Set => jSet}
import clojure.lang.Keyword

class CastTypes(maxRows: Int) extends CastAggr(maxRows) {


  // castOne -------------------------------------------------------

  protected val castOneString = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[String]
  }

  protected val castOneInt = (colIndex: Int) => {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jLong].toInt
  }

  protected val castOneInt2 = (colIndex: Int) => {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toInt
  }

  // todo: Long | ref | datom
  protected val castOneLong = (colIndex: Int) => {
    val array = new Array[Long](maxRows)
    oneLongArrays = oneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Long]
  }

  protected val castOneDouble = (colIndex: Int) => {
    val array = new Array[Double](maxRows)
    oneDoubleArrays = oneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Double]
  }

  protected val castOneBoolean = (colIndex: Int) => {
    val array = new Array[Boolean](maxRows)
    oneBooleanArrays = oneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Boolean]
  }

  protected val castOneDate = (colIndex: Int) => {
    val array = new Array[Date](maxRows)
    oneDateArrays = oneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Date]
  }

  protected val castOneUUID = (colIndex: Int) => {
    val array = new Array[UUID](maxRows)
    oneUUIDArrays = oneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[UUID]
  }

  protected val castOneURI = (colIndex: Int) => {
    val array = new Array[URI](maxRows)
    oneURIArrays = oneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      }
  }

  protected val castOneBigInt = (colIndex: Int) => {
    val array = new Array[BigInt](maxRows)
    oneBigIntArrays = oneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigInt(row.get(colIndex).toString)
  }

  protected val castOneBigDecimal = (colIndex: Int) => {
    val array = new Array[BigDecimal](maxRows)
    oneBigDecimalArrays = oneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigDecimal(row.get(colIndex).toString)
  }

  // Generic `v` attribute value converted to String}
  protected val castOneAny = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case d: Date => date2strLocal(d)
        case other   => other.toString
      }
  }


  // castOptOne -------------------------------------------------------

  protected val castOptOneEnum = (colIndex: Int) => {
    val array = new Array[Option[String]](maxRows)
    optOneStringArrays = optOneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[String]
        case v    => Some(
          getKwName(
            v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
              .asInstanceOf[jMap[_, _]].values.iterator.next.toString
          )
        )
      }
  }

  protected val castOptOneString = (colIndex: Int) => {
    val array = new Array[Option[String]](maxRows)
    optOneStringArrays = optOneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[String]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
          .asInstanceOf[jMap[String, Keyword]].values.iterator.next.getName)
      }
  }

  protected val castOptOneInt = (colIndex: Int) => {
    val array = new Array[Option[Int]](maxRows)
    optOneIntArrays = optOneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Int]
        case v    => Some(v.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong].toInt)
      }
  }

  protected val castOptOneLong = (colIndex: Int) => {
    val array = new Array[Option[Long]](maxRows)
    optOneLongArrays = optOneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Long]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jLong].toLong)
      }
  }

  protected val castOptOneDouble = (colIndex: Int) => {
    val array = new Array[Option[Double]](maxRows)
    optOneDoubleArrays = optOneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Double]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jDouble].toDouble)
      }
  }

  protected val castOptOneBoolean = (colIndex: Int) => {
    val array = new Array[Option[Boolean]](maxRows)
    optOneBooleanArrays = optOneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Boolean]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Boolean])
      }
  }

  protected val castOptOneDate = (colIndex: Int) => {
    val array = new Array[Option[Date]](maxRows)
    optOneDateArrays = optOneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Date]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Date])
      }
  }

  protected val castOptOneUUID = (colIndex: Int) => {
    val array = new Array[Option[UUID]](maxRows)
    optOneUUIDArrays = optOneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[UUID]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[UUID])
      }
  }

  protected val castOptOneURI = (colIndex: Int) => {
    val array = new Array[Option[URI]](maxRows)
    optOneURIArrays = optOneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[URI]
        case v    => v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next match {
          case uri: URI => Some(uri)
          case uriImpl  => Some(new URI(uriImpl.toString))
        }
      }
  }

  protected val castOptOneBigInt = (colIndex: Int) => {
    val array = new Array[Option[BigInt]](maxRows)
    optOneBigIntArrays = optOneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[BigInt]
        case v    => Some(BigInt(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
      }
  }

  protected val castOptOneBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[BigDecimal]](maxRows)
    optOneBigDecimalArrays = optOneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[BigDecimal]
        case v    => Some(BigDecimal(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
      }
  }

  protected val castOptOneRefAttr = (colIndex: Int) => {
    val array = new Array[Option[Long]](maxRows)
    optOneLongArrays = optOneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Long]
        case v    =>
          var id   = 0L
          var done = false
          // Hack to avoid looking up map by clojure Keyword - there must be a better way...
          v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.asInstanceOf[jMap[_, _]].forEach {
            case _ if done                        =>
            case (k, v) if k.toString == ":db/id" => done = true; id = v.asInstanceOf[jLong].toLong
            case _                                =>
          }
          Some(id)
      }
  }


  // castMany -------------------------------------------------------

  protected val castManyEnum = (colIndex: Int) => {
    val array = new Array[Set[String]](maxRows)
    manyStringArrays = manyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[String]
        while (it.hasNext)
          set += it.next.toString
        set
      }
  }

  protected val castManyString = (colIndex: Int) => {
    val array = new Array[Set[String]](maxRows)
    manyStringArrays = manyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[String]
        while (it.hasNext)
          set += it.next.asInstanceOf[String]
        set
      }
  }

  protected val castManyInt = (colIndex: Int) => {
    val array = new Array[Set[Int]](maxRows)
    manyIntArrays = manyIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Int]
        while (it.hasNext)
          set += it.next.asInstanceOf[jLong].toInt
        set
      }
  }

  protected val castManyLong = (colIndex: Int) => {
    val array = new Array[Set[Long]](maxRows)
    manyLongArrays = manyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Long]
        while (it.hasNext)
          set += it.next.asInstanceOf[Long]
        set
      }
  }

  protected val castManyDouble = (colIndex: Int) => {
    val array = new Array[Set[Double]](maxRows)
    manyDoubleArrays = manyDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Double]
        while (it.hasNext)
          set += it.next.asInstanceOf[Double]
        set
      }
  }

  protected val castManyBoolean = (colIndex: Int) => {
    val array = new Array[Set[Boolean]](maxRows)
    manyBooleanArrays = manyBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Boolean]
        while (it.hasNext)
          set += it.next.asInstanceOf[Boolean]
        set
      }
  }

  protected val castManyDate = (colIndex: Int) => {
    val array = new Array[Set[Date]](maxRows)
    manyDateArrays = manyDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[Date]
        while (it.hasNext)
          set += it.next.asInstanceOf[Date]
        set
      }
  }

  protected val castManyUUID = (colIndex: Int) => {
    val array = new Array[Set[UUID]](maxRows)
    manyUUIDArrays = manyUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[UUID]
        while (it.hasNext)
          set += it.next.asInstanceOf[UUID]
        set
      }
  }

  protected val castManyURI = (colIndex: Int) => {
    val array = new Array[Set[URI]](maxRows)
    manyURIArrays = manyURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[URI]
        while (it.hasNext)
          set += (it.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          })
        set
      }
  }

  protected val castManyBigInt = (colIndex: Int) => {
    val array = new Array[Set[BigInt]](maxRows)
    manyBigIntArrays = manyBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[BigInt]
        while (it.hasNext)
          set += BigInt(it.next.toString)
        set
      }
  }

  protected val castManyBigDecimal = (colIndex: Int) => {
    val array = new Array[Set[BigDecimal]](maxRows)
    manyBigDecimalArrays = manyBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var set = Set.empty[BigDecimal]
        while (it.hasNext)
          set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
        set
      }
  }


  // castOptMany -------------------------------------------------------

  protected val castOptManyEnum = (colIndex: Int) => {
    val array = new Array[Option[Set[String]]](maxRows)
    optManyStringArrays = optManyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[String]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[String]
          while (it.hasNext)
            set += getKwName(it.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)
          Some(set)
      }
  }

  protected val castOptManyString = (colIndex: Int) => {
    val array = new Array[Option[Set[String]]](maxRows)
    optManyStringArrays = optManyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[String]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[String]
          while (it.hasNext)
            set += it.next.asInstanceOf[String]
          Some(set)
      }
  }

  protected val castOptManyInt = (colIndex: Int) => {
    val array = new Array[Option[Set[Int]]](maxRows)
    optManyIntArrays = optManyIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Int]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Int]
          while (it.hasNext)
            set += it.next.asInstanceOf[jLong].toInt
          Some(set)
      }
  }

  protected val castOptManyLong = (colIndex: Int) => {
    val array = new Array[Option[Set[Long]]](maxRows)
    optManyLongArrays = optManyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Long]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Long]
          while (it.hasNext)
            set += it.next.asInstanceOf[jLong].toLong
          Some(set)
      }
  }

  protected val castOptManyDouble = (colIndex: Int) => {
    val array = new Array[Option[Set[Double]]](maxRows)
    optManyDoubleArrays = optManyDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Double]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Double]
          while (it.hasNext)
            set += it.next.asInstanceOf[jDouble].toDouble
          Some(set)
      }
  }

  protected val castOptManyBoolean = (colIndex: Int) => {
    val array = new Array[Option[Set[Boolean]]](maxRows)
    optManyBooleanArrays = optManyBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Boolean]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Boolean]
          while (it.hasNext)
            set += it.next.asInstanceOf[Boolean]
          Some(set)
      }
  }

  protected val castOptManyDate = (colIndex: Int) => {
    val array = new Array[Option[Set[Date]]](maxRows)
    optManyDateArrays = optManyDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Date]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Date]
          while (it.hasNext)
            set += it.next.asInstanceOf[Date]
          Some(set)
      }
  }

  protected val castOptManyUUID = (colIndex: Int) => {
    val array = new Array[Option[Set[UUID]]](maxRows)
    optManyUUIDArrays = optManyUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[UUID]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[UUID]
          while (it.hasNext)
            set += it.next.asInstanceOf[UUID]
          Some(set)
      }
  }

  protected val castOptManyURI = (colIndex: Int) => {
    val array = new Array[Option[Set[URI]]](maxRows)
    optManyURIArrays = optManyURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[URI]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[URI]
          while (it.hasNext)
            set += (it.next match {
              case uri: URI => uri
              case uriImpl  => new URI(uriImpl.toString)
            })
          Some(set)
      }
  }

  protected val castOptManyBigInt = (colIndex: Int) => {
    val array = new Array[Option[Set[BigInt]]](maxRows)
    optManyBigIntArrays = optManyBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[BigInt]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[BigInt]
          while (it.hasNext)
            set += BigInt(it.next.toString)
          Some(set)
      }
  }

  protected val castOptManyBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[Set[BigDecimal]]](maxRows)
    optManyBigDecimalArrays = optManyBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[BigDecimal]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[BigDecimal]
          while (it.hasNext)
            set += BigDecimal(it.next.toString)
          Some(set)
      }
  }

  protected val castOptManyRefAttr = (colIndex: Int) => {
    val array = new Array[Option[Set[Long]]](maxRows)
    optManyLongArrays = optManyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Long]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Long]
          // Hack to avoid looking up map by clojure Keyword - there must be a better way...
          while (it.hasNext) {
            var done = false
            it.next.asInstanceOf[jMap[_, _]].forEach {
              case _ if done                        =>
              case (k, v) if k.toString == ":db/id" => done = true; set += v.asInstanceOf[jLong].toLong
              case _                                =>
            }
          }
          Some(set)
      }
  }


  // castMap -------------------------------------------------------

  protected val castMapString = (colIndex: Int) => {
    val array = new Array[Map[String, String]](maxRows)
    mapStringArrays = mapStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, String]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> vs(1))
        }
        map
      }
  }

  protected val castMapInt = (colIndex: Int) => {
    val array = new Array[Map[String, Int]](maxRows)
    mapIntArrays = mapIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Int]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> vs(1).toInt)
        }
        map
      }
  }

  protected val castMapLong = (colIndex: Int) => {
    val array = new Array[Map[String, Long]](maxRows)
    mapLongArrays = mapLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Long]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> vs(1).toLong)
        }
        map
      }
  }

  protected val castMapDouble = (colIndex: Int) => {
    val array = new Array[Map[String, Double]](maxRows)
    mapDoubleArrays = mapDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Double]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> vs(1).toDouble)
        }
        map
      }
  }

  protected val castMapBoolean = (colIndex: Int) => {
    val array = new Array[Map[String, Boolean]](maxRows)
    mapBooleanArrays = mapBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Boolean]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> vs(1).toBoolean)
        }
        map
      }
  }

  protected val castMapDate = (colIndex: Int) => {
    val array = new Array[Map[String, Date]](maxRows)
    mapDateArrays = mapDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Date]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> str2date(vs(1)))
        }
        map
      }
  }

  protected val castMapUUID = (colIndex: Int) => {
    val array = new Array[Map[String, UUID]](maxRows)
    mapUUIDArrays = mapUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, UUID]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> UUID.fromString(vs(1)))
        }
        map
      }
  }

  protected val castMapURI = (colIndex: Int) => {
    val array = new Array[Map[String, URI]](maxRows)
    mapURIArrays = mapURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, URI]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> new URI(vs(1)))
        }
        map
      }
  }

  protected val castMapBigInt = (colIndex: Int) => {
    val array = new Array[Map[String, BigInt]](maxRows)
    mapBigIntArrays = mapBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, BigInt]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> BigInt(vs(1)))
        }
        map
      }
  }

  protected val castMapBigDecimal = (colIndex: Int) => {
    val array = new Array[Map[String, BigDecimal]](maxRows)
    mapBigDecimalArrays = mapBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, BigDecimal]
        var vs  = new Array[String](2)
        while (it.hasNext) {
          vs = it.next.toString.split("@", 2)
          map += (vs(0) -> BigDecimal(vs(1)))
        }
        map
      }
  }


  // castOptMap -------------------------------------------------------

  protected val castOptMapString = (colIndex: Int) => {
    val array = new Array[Option[Map[String, String]]](maxRows)
    optMapStringArrays = optMapStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, String]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, String]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1))
          }
          Some(map)
      }
  }

  protected val castOptMapInt = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Int]]](maxRows)
    optMapIntArrays = optMapIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Int]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Int]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1).toInt)
          }
          Some(map)
      }
  }

  protected val castOptMapLong = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Long]]](maxRows)
    optMapLongArrays = optMapLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Long]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Long]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1).toLong)
          }
          Some(map)
      }
  }

  protected val castOptMapDouble = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Double]]](maxRows)
    optMapDoubleArrays = optMapDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Double]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Double]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1).toDouble)
          }
          Some(map)
      }
  }

  protected val castOptMapBoolean = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Boolean]]](maxRows)
    optMapBooleanArrays = optMapBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Boolean]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Boolean]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1).toBoolean)
          }
          Some(map)
      }
  }

  protected val castOptMapDate = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Date]]](maxRows)
    optMapDateArrays = optMapDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Date]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Date]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> str2date(vs(1)))
          }
          Some(map)
      }
  }

  protected val castOptMapUUID = (colIndex: Int) => {
    val array = new Array[Option[Map[String, UUID]]](maxRows)
    optMapUUIDArrays = optMapUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, UUID]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, UUID]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> UUID.fromString(vs(1)))
          }
          Some(map)
      }
  }

  protected val castOptMapURI = (colIndex: Int) => {
    val array = new Array[Option[Map[String, URI]]](maxRows)
    optMapURIArrays = optMapURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, URI]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, URI]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> new URI(vs(1)))
          }
          Some(map)
      }
  }

  protected val castOptMapBigInt = (colIndex: Int) => {
    val array = new Array[Option[Map[String, BigInt]]](maxRows)
    optMapBigIntArrays = optMapBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, BigInt]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, BigInt]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> BigInt(vs(1)))
          }
          Some(map)
      }
  }

  protected val castOptMapBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[Map[String, BigDecimal]]](maxRows)
    optMapBigDecimalArrays = optMapBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, BigDecimal]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, BigDecimal]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> BigDecimal(vs(1)))
          }
          Some(map)
      }
  }


  // castOptApplyOne -------------------------------------------------------

  protected val castOptApplyOneString = (colIndex: Int) => {
    val array = new Array[Option[String]](maxRows)
    optOneStringArrays = optOneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[String]
        case v    => Some(v.asInstanceOf[String])
      }
  }

  protected val castOptApplyOneInt = (colIndex: Int) => {
    val array = new Array[Option[Int]](maxRows)
    optOneIntArrays = optOneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Int]
        case v    => Some(v.asInstanceOf[jLong].toInt)
      }
  }

  protected val castOptApplyOneLong = (colIndex: Int) => {
    val array = new Array[Option[Long]](maxRows)
    optOneLongArrays = optOneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Long]
        case v    => Some(v.asInstanceOf[jLong].toLong)
      }
  }

  protected val castOptApplyOneDouble = (colIndex: Int) => {
    val array = new Array[Option[Double]](maxRows)
    optOneDoubleArrays = optOneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Double]
        case v    => Some(v.asInstanceOf[jDouble].toDouble)
      }
  }

  protected val castOptApplyOneBoolean = (colIndex: Int) => {
    val array = new Array[Option[Boolean]](maxRows)
    optOneBooleanArrays = optOneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Boolean]
        case v    => Some(v.asInstanceOf[Boolean])
      }
  }

  protected val castOptApplyOneDate = (colIndex: Int) => {
    val array = new Array[Option[Date]](maxRows)
    optOneDateArrays = optOneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Date]
        case v    => Some(v.asInstanceOf[Date])
      }
  }

  protected val castOptApplyOneUUID = (colIndex: Int) => {
    val array = new Array[Option[UUID]](maxRows)
    optOneUUIDArrays = optOneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[UUID]
        case v    => Some(v.asInstanceOf[UUID])
      }
  }

  protected val castOptApplyOneURI = (colIndex: Int) => {
    val array = new Array[Option[URI]](maxRows)
    optOneURIArrays = optOneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null     => Option.empty[URI]
        case uri: URI => Some(uri)
        case uriImpl  => Some(new URI(uriImpl.toString))
      }
  }

  protected val castOptApplyOneBigInt = (colIndex: Int) => {
    val array = new Array[Option[BigInt]](maxRows)
    optOneBigIntArrays = optOneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[BigInt]
        case v    => Some(BigInt(v.toString))
      }
  }

  protected val castOptApplyOneBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[BigDecimal]](maxRows)
    optOneBigDecimalArrays = optOneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[BigDecimal]
        case v    => Some(BigDecimal(v.toString))
      }
  }


  // castOptApplyMany -------------------------------------------------------

  protected val castOptApplyManyString = (colIndex: Int) => {
    val array = new Array[Option[Set[String]]](maxRows)
    optManyStringArrays = optManyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[String]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[String]
          while (it.hasNext)
            set += it.next.asInstanceOf[String]
          Some(set)
      }
  }

  protected val castOptApplyManyInt = (colIndex: Int) => {
    val array = new Array[Option[Set[Int]]](maxRows)
    optManyIntArrays = optManyIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Int]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Int]
          while (it.hasNext)
            set += it.next.asInstanceOf[jLong].toInt
          Some(set)
      }
  }

  protected val castOptApplyManyLong = (colIndex: Int) => {
    val array = new Array[Option[Set[Long]]](maxRows)
    optManyLongArrays = optManyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Long]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Long]
          while (it.hasNext)
            set += it.next.asInstanceOf[jLong].toLong
          Some(set)
      }
  }

  protected val castOptApplyManyDouble = (colIndex: Int) => {
    val array = new Array[Option[Set[Double]]](maxRows)
    optManyDoubleArrays = optManyDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Double]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Double]
          while (it.hasNext)
            set += it.next.asInstanceOf[jDouble].toDouble
          Some(set)
      }
  }

  protected val castOptApplyManyBoolean = (colIndex: Int) => {
    val array = new Array[Option[Set[Boolean]]](maxRows)
    optManyBooleanArrays = optManyBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Boolean]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Boolean]
          while (it.hasNext)
            set += it.next.asInstanceOf[Boolean]
          Some(set)
      }
  }

  protected val castOptApplyManyDate = (colIndex: Int) => {
    val array = new Array[Option[Set[Date]]](maxRows)
    optManyDateArrays = optManyDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Date]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[Date]
          while (it.hasNext)
            set += it.next.asInstanceOf[Date]
          Some(set)
      }
  }

  protected val castOptApplyManyUUID = (colIndex: Int) => {
    val array = new Array[Option[Set[UUID]]](maxRows)
    optManyUUIDArrays = optManyUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[UUID]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[UUID]
          while (it.hasNext)
            set += it.next.asInstanceOf[UUID]
          Some(set)
      }
  }

  protected val castOptApplyManyURI = (colIndex: Int) => {
    val array = new Array[Option[Set[URI]]](maxRows)
    optManyURIArrays = optManyURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[URI]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[URI]
          while (it.hasNext)
            set += (it.next match {
              case uri: URI => uri
              case uriImpl  => new URI(uriImpl.toString)
            })
          Some(set)
      }
  }

  protected val castOptApplyManyBigInt = (colIndex: Int) => {
    val array = new Array[Option[Set[BigInt]]](maxRows)
    optManyBigIntArrays = optManyBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[BigInt]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[BigInt]
          while (it.hasNext)
            set += BigInt(it.next.toString)
          Some(set)
      }
  }

  protected val castOptApplyManyBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[Set[BigDecimal]]](maxRows)
    optManyBigDecimalArrays = optManyBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[BigDecimal]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var set = Set.empty[BigDecimal]
          while (it.hasNext)
            set += BigDecimal(it.next.toString)
          Some(set)
      }
  }



  //  castOptApplyMap -------------------------------------------------------

  protected val castOptApplyMapString = (colIndex: Int) => {
    val array = new Array[Option[Map[String, String]]](maxRows)
    optMapStringArrays = optMapStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, String]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, String]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1))
          }
          Some(map)
      }
  }

  protected val castOptApplyMapInt = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Int]]](maxRows)
    optMapIntArrays = optMapIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Int]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Int]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1).toInt)
          }
          Some(map)
      }
  }

  protected val castOptApplyMapLong = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Long]]](maxRows)
    optMapLongArrays = optMapLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Long]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Long]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1).toLong)
          }
          Some(map)
      }
  }

  protected val castOptApplyMapDouble = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Double]]](maxRows)
    optMapDoubleArrays = optMapDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Double]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Double]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1).toDouble)
          }
          Some(map)
      }
  }

  protected val castOptApplyMapBoolean = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Boolean]]](maxRows)
    optMapBooleanArrays = optMapBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Boolean]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Boolean]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> vs(1).toBoolean)
          }
          Some(map)
      }
  }

  protected val castOptApplyMapDate = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Date]]](maxRows)
    optMapDateArrays = optMapDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Date]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Date]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> str2date(vs(1)))
          }
          Some(map)
      }
  }

  protected val castOptApplyMapUUID = (colIndex: Int) => {
    val array = new Array[Option[Map[String, UUID]]](maxRows)
    optMapUUIDArrays = optMapUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, UUID]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, UUID]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> UUID.fromString(vs(1)))
          }
          Some(map)
      }
  }

  protected val castOptApplyMapURI = (colIndex: Int) => {
    val array = new Array[Option[Map[String, URI]]](maxRows)
    optMapURIArrays = optMapURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, URI]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, URI]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> new URI(vs(1)))
          }
          Some(map)
      }
  }

  protected val castOptApplyMapBigInt = (colIndex: Int) => {
    val array = new Array[Option[Map[String, BigInt]]](maxRows)
    optMapBigIntArrays = optMapBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, BigInt]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, BigInt]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> BigInt(vs(1)))
          }
          Some(map)
      }
  }

  protected val castOptApplyMapBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[Map[String, BigDecimal]]](maxRows)
    optMapBigDecimalArrays = optMapBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, BigDecimal]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, BigDecimal]
          var vs  = new Array[String](2)
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map += (vs(0) -> BigDecimal(vs(1)))
          }
          Some(map)
      }
  }


  //  castKeyedMap -------------------------------------------------------

  protected val castKeyedMapString = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString
  }

  protected val castKeyedMapInt = (colIndex: Int) => {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toInt
  }

  // todo: Long" | "ref" | " datom
  protected val castKeyedMapLong = (colIndex: Int) => {
    val array = new Array[Long](maxRows)
    oneLongArrays = oneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toLong
  }

  protected val castKeyedMapDouble = (colIndex: Int) => {
    val array = new Array[Double](maxRows)
    oneDoubleArrays = oneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toDouble
  }

  protected val castKeyedMapBoolean = (colIndex: Int) => {
    val array = new Array[Boolean](maxRows)
    oneBooleanArrays = oneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toBoolean
  }

  protected val castKeyedMapDate = (colIndex: Int) => {
    val array = new Array[Date](maxRows)
    oneDateArrays = oneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = str2date(row.get(colIndex).toString)
  }

  protected val castKeyedMapUUID = (colIndex: Int) => {
    val array = new Array[UUID](maxRows)
    oneUUIDArrays = oneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = UUID.fromString(row.get(colIndex).toString)
  }

  protected val castKeyedMapURI = (colIndex: Int) => {
    val array = new Array[URI](maxRows)
    oneURIArrays = oneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = new URI(row.get(colIndex).toString)
  }

  protected val castKeyedMapBigInt = (colIndex: Int) => {
    val array = new Array[BigInt](maxRows)
    oneBigIntArrays = oneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigInt(row.get(colIndex).toString)
  }

  protected val castKeyedMapBigDecimal = (colIndex: Int) => {
    val array = new Array[BigDecimal](maxRows)
    oneBigDecimalArrays = oneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigDecimal(row.get(colIndex).toString)
  }

  // Generic `v` attribute value converted to String}
  protected val castKeyedMapAny = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case d: Date => date2strLocal(d)
        case other   => other.toString
      }
  }
}
