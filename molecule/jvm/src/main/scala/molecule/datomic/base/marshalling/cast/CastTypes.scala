package molecule.datomic.base.marshalling.cast

import java.lang.{Double => jDouble, Long => jLong}
import java.net.URI
import java.util.{Date, UUID, List => jList, Map => jMap, Set => jSet, Collection => jCollection}
import molecule.core.exceptions.MoleculeException

class CastTypes(maxRows: Int) extends CastAggr(maxRows) {


  // castOne -------------------------------------------------------

  protected lazy val castOneString = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[String]
  }

  protected lazy val castOneInt = (colIndex: Int) => {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toInt
  }

  protected lazy val castOneLong = (colIndex: Int) => {
    val array = new Array[Long](maxRows)
    oneLongArrays = oneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Long]
  }

  protected lazy val castOneDouble = (colIndex: Int) => {
    val array = new Array[Double](maxRows)
    oneDoubleArrays = oneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Double]
  }

  protected lazy val castOneBoolean = (colIndex: Int) => {
    val array = new Array[Boolean](maxRows)
    oneBooleanArrays = oneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Boolean]
  }

  protected lazy val castOneDate = (colIndex: Int) => {
    val array = new Array[Date](maxRows)
    oneDateArrays = oneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Date]
  }

  protected lazy val castOneUUID = (colIndex: Int) => {
    val array = new Array[UUID](maxRows)
    oneUUIDArrays = oneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[UUID]
  }

  protected lazy val castOneURI = (colIndex: Int) => {
    val array = new Array[URI](maxRows)
    oneURIArrays = oneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      }
  }

  protected lazy val castOneBigInt = (colIndex: Int) => {
    val array = new Array[BigInt](maxRows)
    oneBigIntArrays = oneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigInt(row.get(colIndex).toString)
  }

  protected lazy val castOneBigDecimal = (colIndex: Int) => {
    val array = new Array[BigDecimal](maxRows)
    oneBigDecimalArrays = oneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigDecimal(row.get(colIndex).toString)
  }

  // Generic `v` attribute value converted to String with appended type to be casted on JS side
  protected lazy val castOneAny = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case s: java.lang.String      => "String    " + s
        case i: java.lang.Integer     => "Integer   " + i.toString
        case l: java.lang.Long        => "Long      " + l.toString
        case d: java.lang.Double      => "Double    " + d.toString
        case b: java.lang.Boolean     => "Boolean   " + b.toString
        case d: Date                  => "Date      " + date2strLocal(d)
        case u: UUID                  => "UUID      " + u.toString
        case u: java.net.URI          => "URI       " + u.toString
        case bi: java.math.BigInteger => "BigInteger" + bi.toString
        case bi: clojure.lang.BigInt  => "BigInteger" + bi.toString
        case bd: java.math.BigDecimal => "BigDecimal" + bd.toString
        case other                    =>
          throw MoleculeException(s"Unexpected generic `v` $other of type " + other.getClass)

        // Float abandoned
        //        case f: java.lang.Float       => "Float     " + f.toString
      }
  }


  // castOptOne -------------------------------------------------------

  protected lazy val castOptOneEnum = (colIndex: Int) => {
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

  protected lazy val castOptOneString = (colIndex: Int) => {
    val array = new Array[Option[String]](maxRows)
    optOneStringArrays = optOneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[String]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString)
      }
  }

  protected lazy val castOptOneInt = (colIndex: Int) => {
    val array = new Array[Option[Int]](maxRows)
    optOneIntArrays = optOneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Int]
        case v    => Some(v.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong].toInt)
      }
  }

  protected lazy val castOptOneLong = (colIndex: Int) => {
    val array = new Array[Option[Long]](maxRows)
    optOneLongArrays = optOneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Long]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jLong].toLong)
      }
  }

  protected lazy val castOptOneDouble = (colIndex: Int) => {
    val array = new Array[Option[Double]](maxRows)
    optOneDoubleArrays = optOneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Double]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jDouble].toDouble)
      }
  }

  protected lazy val castOptOneBoolean = (colIndex: Int) => {
    val array = new Array[Option[Boolean]](maxRows)
    optOneBooleanArrays = optOneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Boolean]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Boolean])
      }
  }

  protected lazy val castOptOneDate = (colIndex: Int) => {
    val array = new Array[Option[Date]](maxRows)
    optOneDateArrays = optOneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Date]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Date])
      }
  }

  protected lazy val castOptOneUUID = (colIndex: Int) => {
    val array = new Array[Option[UUID]](maxRows)
    optOneUUIDArrays = optOneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[UUID]
        case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[UUID])
      }
  }

  protected lazy val castOptOneURI = (colIndex: Int) => {
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

  protected lazy val castOptOneBigInt = (colIndex: Int) => {
    val array = new Array[Option[BigInt]](maxRows)
    optOneBigIntArrays = optOneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[BigInt]
        case v    => Some(BigInt(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
      }
  }

  protected lazy val castOptOneBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[BigDecimal]](maxRows)
    optOneBigDecimalArrays = optOneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[BigDecimal]
        case v    => Some(BigDecimal(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
      }
  }

  protected lazy val castOptOneRefAttr = (colIndex: Int) => {
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

  protected lazy val castManyEnum = (colIndex: Int) => {
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

  protected lazy val castManyString = (colIndex: Int) => {
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

  protected lazy val castManyInt = (colIndex: Int) => {
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

  protected lazy val castManyLong = (colIndex: Int) => {
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

  protected lazy val castManyDouble = (colIndex: Int) => {
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

  protected lazy val castManyBoolean = (colIndex: Int) => {
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

  protected lazy val castManyDate = (colIndex: Int) => {
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

  protected lazy val castManyUUID = (colIndex: Int) => {
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

  protected lazy val castManyURI = (colIndex: Int) => {
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

  protected lazy val castManyBigInt = (colIndex: Int) => {
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

  protected lazy val castManyBigDecimal = (colIndex: Int) => {
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

  protected lazy val castOptManyEnum = (colIndex: Int) => {
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

  protected lazy val castOptManyString = (colIndex: Int) => {
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

  protected lazy val castOptManyInt = (colIndex: Int) => {
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

  protected lazy val castOptManyLong = (colIndex: Int) => {
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

  protected lazy val castOptManyDouble = (colIndex: Int) => {
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

  protected lazy val castOptManyBoolean = (colIndex: Int) => {
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

  protected lazy val castOptManyDate = (colIndex: Int) => {
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

  protected lazy val castOptManyUUID = (colIndex: Int) => {
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

  protected lazy val castOptManyURI = (colIndex: Int) => {
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

  protected lazy val castOptManyBigInt = (colIndex: Int) => {
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

  protected lazy val castOptManyBigDecimal = (colIndex: Int) => {
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

  protected lazy val castOptManyRefAttr = (colIndex: Int) => {
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

  protected lazy val castMapString = (colIndex: Int) => {
    val array = new Array[Map[String, String]](maxRows)
    mapStringArrays = mapStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, String]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1))
        }
        map
      }
  }

  protected lazy val castMapInt = (colIndex: Int) => {
    val array = new Array[Map[String, Int]](maxRows)
    mapIntArrays = mapIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Int]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toInt)
        }
        map
      }
  }

  protected lazy val castMapLong = (colIndex: Int) => {
    val array = new Array[Map[String, Long]](maxRows)
    mapLongArrays = mapLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Long]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toLong)
        }
        map
      }
  }

  protected lazy val castMapDouble = (colIndex: Int) => {
    val array = new Array[Map[String, Double]](maxRows)
    mapDoubleArrays = mapDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Double]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toDouble)
        }
        map
      }
  }

  protected lazy val castMapBoolean = (colIndex: Int) => {
    val array = new Array[Map[String, Boolean]](maxRows)
    mapBooleanArrays = mapBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Boolean]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toBoolean)
        }
        map
      }
  }

  protected lazy val castMapDate = (colIndex: Int) => {
    val array = new Array[Map[String, Date]](maxRows)
    mapDateArrays = mapDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, Date]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> str2date(pair(1)))
        }
        map
      }
  }

  protected lazy val castMapUUID = (colIndex: Int) => {
    val array = new Array[Map[String, UUID]](maxRows)
    mapUUIDArrays = mapUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, UUID]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> UUID.fromString(pair(1)))
        }
        map
      }
  }

  protected lazy val castMapURI = (colIndex: Int) => {
    val array = new Array[Map[String, URI]](maxRows)
    mapURIArrays = mapURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, URI]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> new URI(pair(1)))
        }
        map
      }
  }

  protected lazy val castMapBigInt = (colIndex: Int) => {
    val array = new Array[Map[String, BigInt]](maxRows)
    mapBigIntArrays = mapBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, BigInt]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> BigInt(pair(1)))
        }
        map
      }
  }

  protected lazy val castMapBigDecimal = (colIndex: Int) => {
    val array = new Array[Map[String, BigDecimal]](maxRows)
    mapBigDecimalArrays = mapBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = {
        val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
        var map = Map.empty[String, BigDecimal]
        var pair  = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> BigDecimal(pair(1)))
        }
        map
      }
  }


  // castOptMap -------------------------------------------------------

  protected lazy val castOptMapString = (colIndex: Int) => {
    val array = new Array[Option[Map[String, String]]](maxRows)
    optMapStringArrays = optMapStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, String]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, String]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1))
          }
          Some(map)
      }
  }

  protected lazy val castOptMapInt = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Int]]](maxRows)
    optMapIntArrays = optMapIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Int]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Int]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1).toInt)
          }
          Some(map)
      }
  }

  protected lazy val castOptMapLong = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Long]]](maxRows)
    optMapLongArrays = optMapLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Long]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Long]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1).toLong)
          }
          Some(map)
      }
  }

  protected lazy val castOptMapDouble = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Double]]](maxRows)
    optMapDoubleArrays = optMapDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Double]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Double]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1).toDouble)
          }
          Some(map)
      }
  }

  protected lazy val castOptMapBoolean = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Boolean]]](maxRows)
    optMapBooleanArrays = optMapBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Boolean]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Boolean]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1).toBoolean)
          }
          Some(map)
      }
  }

  protected lazy val castOptMapDate = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Date]]](maxRows)
    optMapDateArrays = optMapDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Date]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, Date]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> str2date(pair(1)))
          }
          Some(map)
      }
  }

  protected lazy val castOptMapUUID = (colIndex: Int) => {
    val array = new Array[Option[Map[String, UUID]]](maxRows)
    optMapUUIDArrays = optMapUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, UUID]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, UUID]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> UUID.fromString(pair(1)))
          }
          Some(map)
      }
  }

  protected lazy val castOptMapURI = (colIndex: Int) => {
    val array = new Array[Option[Map[String, URI]]](maxRows)
    optMapURIArrays = optMapURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, URI]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, URI]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> new URI(pair(1)))
          }
          Some(map)
      }
  }

  protected lazy val castOptMapBigInt = (colIndex: Int) => {
    val array = new Array[Option[Map[String, BigInt]]](maxRows)
    optMapBigIntArrays = optMapBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, BigInt]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, BigInt]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> BigInt(pair(1)))
          }
          Some(map)
      }
  }

  protected lazy val castOptMapBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[Map[String, BigDecimal]]](maxRows)
    optMapBigDecimalArrays = optMapBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, BigDecimal]]
        case v    =>
          val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map = Map.empty[String, BigDecimal]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> BigDecimal(pair(1)))
          }
          Some(map)
      }
  }


  // castOptApplyOne -------------------------------------------------------

  protected lazy val castOptApplyOneString = (colIndex: Int) => {
    val array = new Array[Option[String]](maxRows)
    optOneStringArrays = optOneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[String]
        case v    => Some(v.asInstanceOf[String])
      }
  }

  protected lazy val castOptApplyOneInt = (colIndex: Int) => {
    val array = new Array[Option[Int]](maxRows)
    optOneIntArrays = optOneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Int]
        case v    => Some(v.asInstanceOf[jLong].toInt)
      }
  }

  protected lazy val castOptApplyOneLong = (colIndex: Int) => {
    val array = new Array[Option[Long]](maxRows)
    optOneLongArrays = optOneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Long]
        case v    => Some(v.asInstanceOf[jLong].toLong)
      }
  }

  protected lazy val castOptApplyOneDouble = (colIndex: Int) => {
    val array = new Array[Option[Double]](maxRows)
    optOneDoubleArrays = optOneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Double]
        case v    => Some(v.asInstanceOf[jDouble].toDouble)
      }
  }

  protected lazy val castOptApplyOneBoolean = (colIndex: Int) => {
    val array = new Array[Option[Boolean]](maxRows)
    optOneBooleanArrays = optOneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Boolean]
        case v    => Some(v.asInstanceOf[Boolean])
      }
  }

  protected lazy val castOptApplyOneDate = (colIndex: Int) => {
    val array = new Array[Option[Date]](maxRows)
    optOneDateArrays = optOneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Date]
        case v    => Some(v.asInstanceOf[Date])
      }
  }

  protected lazy val castOptApplyOneUUID = (colIndex: Int) => {
    val array = new Array[Option[UUID]](maxRows)
    optOneUUIDArrays = optOneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[UUID]
        case v    => Some(v.asInstanceOf[UUID])
      }
  }

  protected lazy val castOptApplyOneURI = (colIndex: Int) => {
    val array = new Array[Option[URI]](maxRows)
    optOneURIArrays = optOneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null     => Option.empty[URI]
        case uri: URI => Some(uri)
        case uriImpl  => Some(new URI(uriImpl.toString))
      }
  }

  protected lazy val castOptApplyOneBigInt = (colIndex: Int) => {
    val array = new Array[Option[BigInt]](maxRows)
    optOneBigIntArrays = optOneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[BigInt]
        case v    => Some(BigInt(v.toString))
      }
  }

  protected lazy val castOptApplyOneBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[BigDecimal]](maxRows)
    optOneBigDecimalArrays = optOneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[BigDecimal]
        case v    => Some(BigDecimal(v.toString))
      }
  }


  // castOptApplyMany -------------------------------------------------------

  protected lazy val castOptApplyManyString = (colIndex: Int) => {
    val array = new Array[Option[Set[String]]](maxRows)
    optManyStringArrays = optManyStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[String]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[String]
          while (it.hasNext)
            set += it.next.asInstanceOf[String]
          Some(set)
      }
  }

  protected lazy val castOptApplyManyInt = (colIndex: Int) => {
    val array = new Array[Option[Set[Int]]](maxRows)
    optManyIntArrays = optManyIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Int]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[Int]
          while (it.hasNext)
            set += it.next.asInstanceOf[jLong].toInt
          Some(set)
      }
  }

  protected lazy val castOptApplyManyLong = (colIndex: Int) => {
    val array = new Array[Option[Set[Long]]](maxRows)
    optManyLongArrays = optManyLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Long]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[Long]
          while (it.hasNext)
            set += it.next.asInstanceOf[jLong].toLong
          Some(set)
      }
  }

  protected lazy val castOptApplyManyDouble = (colIndex: Int) => {
    val array = new Array[Option[Set[Double]]](maxRows)
    optManyDoubleArrays = optManyDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Double]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[Double]
          while (it.hasNext)
            set += it.next.asInstanceOf[jDouble].toDouble
          Some(set)
      }
  }

  protected lazy val castOptApplyManyBoolean = (colIndex: Int) => {
    val array = new Array[Option[Set[Boolean]]](maxRows)
    optManyBooleanArrays = optManyBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Boolean]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[Boolean]
          while (it.hasNext)
            set += it.next.asInstanceOf[Boolean]
          Some(set)
      }
  }

  protected lazy val castOptApplyManyDate = (colIndex: Int) => {
    val array = new Array[Option[Set[Date]]](maxRows)
    optManyDateArrays = optManyDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[Date]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[Date]
          while (it.hasNext)
            set += it.next.asInstanceOf[Date]
          Some(set)
      }
  }

  protected lazy val castOptApplyManyUUID = (colIndex: Int) => {
    val array = new Array[Option[Set[UUID]]](maxRows)
    optManyUUIDArrays = optManyUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[UUID]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[UUID]
          while (it.hasNext)
            set += it.next.asInstanceOf[UUID]
          Some(set)
      }
  }

  protected lazy val castOptApplyManyURI = (colIndex: Int) => {
    val array = new Array[Option[Set[URI]]](maxRows)
    optManyURIArrays = optManyURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[URI]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[URI]
          while (it.hasNext)
            set += (it.next match {
              case uri: URI => uri
              case uriImpl  => new URI(uriImpl.toString)
            })
          Some(set)
      }
  }

  protected lazy val castOptApplyManyBigInt = (colIndex: Int) => {
    val array = new Array[Option[Set[BigInt]]](maxRows)
    optManyBigIntArrays = optManyBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[BigInt]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[BigInt]
          while (it.hasNext)
            set += BigInt(it.next.toString)
          Some(set)
      }
  }

  protected lazy val castOptApplyManyBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[Set[BigDecimal]]](maxRows)
    optManyBigDecimalArrays = optManyBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Set[BigDecimal]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var set = Set.empty[BigDecimal]
          while (it.hasNext)
            set += BigDecimal(it.next.toString)
          Some(set)
      }
  }



  //  castOptApplyMap -------------------------------------------------------

  protected lazy val castOptApplyMapString = (colIndex: Int) => {
    val array = new Array[Option[Map[String, String]]](maxRows)
    optMapStringArrays = optMapStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, String]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, String]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1))
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapInt = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Int]]](maxRows)
    optMapIntArrays = optMapIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Int]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Int]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1).toInt)
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapLong = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Long]]](maxRows)
    optMapLongArrays = optMapLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Long]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Long]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1).toLong)
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapDouble = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Double]]](maxRows)
    optMapDoubleArrays = optMapDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Double]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Double]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1).toDouble)
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapBoolean = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Boolean]]](maxRows)
    optMapBooleanArrays = optMapBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Boolean]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Boolean]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> pair(1).toBoolean)
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapDate = (colIndex: Int) => {
    val array = new Array[Option[Map[String, Date]]](maxRows)
    optMapDateArrays = optMapDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Date]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, Date]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> str2date(pair(1)))
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapUUID = (colIndex: Int) => {
    val array = new Array[Option[Map[String, UUID]]](maxRows)
    optMapUUIDArrays = optMapUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, UUID]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, UUID]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> UUID.fromString(pair(1)))
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapURI = (colIndex: Int) => {
    val array = new Array[Option[Map[String, URI]]](maxRows)
    optMapURIArrays = optMapURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, URI]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, URI]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> new URI(pair(1)))
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapBigInt = (colIndex: Int) => {
    val array = new Array[Option[Map[String, BigInt]]](maxRows)
    optMapBigIntArrays = optMapBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, BigInt]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, BigInt]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> BigInt(pair(1)))
          }
          Some(map)
      }
  }

  protected lazy val castOptApplyMapBigDecimal = (colIndex: Int) => {
    val array = new Array[Option[Map[String, BigDecimal]]](maxRows)
    optMapBigDecimalArrays = optMapBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, BigDecimal]]
        case v    =>
          val it  = v.asInstanceOf[jSet[_]].iterator
          var map = Map.empty[String, BigDecimal]
          var pair  = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map += (pair(0) -> BigDecimal(pair(1)))
          }
          Some(map)
      }
  }


  //  castKeyedMap -------------------------------------------------------

  protected lazy val castKeyedMapString = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString
  }

  protected lazy val castKeyedMapInt = (colIndex: Int) => {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toInt
  }

  // todo: Long" | "ref" | " datom
  protected lazy val castKeyedMapLong = (colIndex: Int) => {
    val array = new Array[Long](maxRows)
    oneLongArrays = oneLongArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toLong
  }

  protected lazy val castKeyedMapDouble = (colIndex: Int) => {
    val array = new Array[Double](maxRows)
    oneDoubleArrays = oneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toDouble
  }

  protected lazy val castKeyedMapBoolean = (colIndex: Int) => {
    val array = new Array[Boolean](maxRows)
    oneBooleanArrays = oneBooleanArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).toString.toBoolean
  }

  protected lazy val castKeyedMapDate = (colIndex: Int) => {
    val array = new Array[Date](maxRows)
    oneDateArrays = oneDateArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = str2date(row.get(colIndex).toString)
  }

  protected lazy val castKeyedMapUUID = (colIndex: Int) => {
    val array = new Array[UUID](maxRows)
    oneUUIDArrays = oneUUIDArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = UUID.fromString(row.get(colIndex).toString)
  }

  protected lazy val castKeyedMapURI = (colIndex: Int) => {
    val array = new Array[URI](maxRows)
    oneURIArrays = oneURIArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = new URI(row.get(colIndex).toString)
  }

  protected lazy val castKeyedMapBigInt = (colIndex: Int) => {
    val array = new Array[BigInt](maxRows)
    oneBigIntArrays = oneBigIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigInt(row.get(colIndex).toString)
  }

  protected lazy val castKeyedMapBigDecimal = (colIndex: Int) => {
    val array = new Array[BigDecimal](maxRows)
    oneBigDecimalArrays = oneBigDecimalArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = BigDecimal(row.get(colIndex).toString)
  }

  // Generic `v` attribute value converted to String}
  protected lazy val castKeyedMapAny = (colIndex: Int) => {
    val array = new Array[String](maxRows)
    oneStringArrays = oneStringArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case d: Date => date2strLocal(d)
        case other   => other.toString
      }
  }
}
