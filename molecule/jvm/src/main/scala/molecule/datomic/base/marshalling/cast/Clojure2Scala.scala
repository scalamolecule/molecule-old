package molecule.datomic.base.marshalling.cast

import java.lang.{Double => jDouble, Long => jLong}
import java.net.URI
import java.util.{Date, UUID, List => jList, Map => jMap, Set => jSet}
import clojure.lang.{Keyword, LazySeq, PersistentHashSet, PersistentVector}
import molecule.core.marshalling.Column
import molecule.core.util.{DateHandling, Helpers}
import molecule.datomic.base.marshalling.DateStrLocal
import scala.collection.mutable.ListBuffer

class Clojure2Scala(maxRows: Int) extends DateHandling with DateStrLocal with Helpers {

  protected var oneStringArrays         = Seq.empty[Array[String]]
  protected var oneIntArrays            = Seq.empty[Array[Int]]
  protected var oneLongArrays           = Seq.empty[Array[Long]]
  protected var oneFloatArrays          = Seq.empty[Array[Float]]
  protected var oneDoubleArrays         = Seq.empty[Array[Double]]
  protected var oneBooleanArrays        = Seq.empty[Array[Boolean]]
  protected var oneDateArrays           = Seq.empty[Array[Date]]
  protected var oneUUIDArrays           = Seq.empty[Array[UUID]]
  protected var oneURIArrays            = Seq.empty[Array[URI]]
  protected var oneBigIntArrays         = Seq.empty[Array[BigInt]]
  protected var oneBigDecimalArrays     = Seq.empty[Array[BigDecimal]]

  protected var optOneStringArrays      = Seq.empty[Array[Option[String]]]
  protected var optOneIntArrays         = Seq.empty[Array[Option[Int]]]
  protected var optOneLongArrays        = Seq.empty[Array[Option[Long]]]
  protected var optOneFloatArrays       = Seq.empty[Array[Option[Float]]]
  protected var optOneDoubleArrays      = Seq.empty[Array[Option[Double]]]
  protected var optOneBooleanArrays     = Seq.empty[Array[Option[Boolean]]]
  protected var optOneDateArrays        = Seq.empty[Array[Option[Date]]]
  protected var optOneUUIDArrays        = Seq.empty[Array[Option[UUID]]]
  protected var optOneURIArrays         = Seq.empty[Array[Option[URI]]]
  protected var optOneBigIntArrays      = Seq.empty[Array[Option[BigInt]]]
  protected var optOneBigDecimalArrays  = Seq.empty[Array[Option[BigDecimal]]]

  protected var manyStringArrays        = Seq.empty[Array[Set[String]]]
  protected var manyIntArrays           = Seq.empty[Array[Set[Int]]]
  protected var manyLongArrays          = Seq.empty[Array[Set[Long]]]
  protected var manyFloatArrays         = Seq.empty[Array[Set[Float]]]
  protected var manyDoubleArrays        = Seq.empty[Array[Set[Double]]]
  protected var manyBooleanArrays       = Seq.empty[Array[Set[Boolean]]]
  protected var manyDateArrays          = Seq.empty[Array[Set[Date]]]
  protected var manyUUIDArrays          = Seq.empty[Array[Set[UUID]]]
  protected var manyURIArrays           = Seq.empty[Array[Set[URI]]]
  protected var manyBigIntArrays        = Seq.empty[Array[Set[BigInt]]]
  protected var manyBigDecimalArrays    = Seq.empty[Array[Set[BigDecimal]]]

  protected var optManyStringArrays     = Seq.empty[Array[Option[Set[String]]]]
  protected var optManyIntArrays        = Seq.empty[Array[Option[Set[Int]]]]
  protected var optManyLongArrays       = Seq.empty[Array[Option[Set[Long]]]]
  protected var optManyFloatArrays      = Seq.empty[Array[Option[Set[Float]]]]
  protected var optManyDoubleArrays     = Seq.empty[Array[Option[Set[Double]]]]
  protected var optManyBooleanArrays    = Seq.empty[Array[Option[Set[Boolean]]]]
  protected var optManyDateArrays       = Seq.empty[Array[Option[Set[Date]]]]
  protected var optManyUUIDArrays       = Seq.empty[Array[Option[Set[UUID]]]]
  protected var optManyURIArrays        = Seq.empty[Array[Option[Set[URI]]]]
  protected var optManyBigIntArrays     = Seq.empty[Array[Option[Set[BigInt]]]]
  protected var optManyBigDecimalArrays = Seq.empty[Array[Option[Set[BigDecimal]]]]

  protected var mapStringArrays         = Seq.empty[Array[Map[String, String]]]
  protected var mapIntArrays            = Seq.empty[Array[Map[String, Int]]]
  protected var mapLongArrays           = Seq.empty[Array[Map[String, Long]]]
  protected var mapFloatArrays          = Seq.empty[Array[Map[String, Float]]]
  protected var mapDoubleArrays         = Seq.empty[Array[Map[String, Double]]]
  protected var mapBooleanArrays        = Seq.empty[Array[Map[String, Boolean]]]
  protected var mapDateArrays           = Seq.empty[Array[Map[String, Date]]]
  protected var mapUUIDArrays           = Seq.empty[Array[Map[String, UUID]]]
  protected var mapURIArrays            = Seq.empty[Array[Map[String, URI]]]
  protected var mapBigIntArrays         = Seq.empty[Array[Map[String, BigInt]]]
  protected var mapBigDecimalArrays     = Seq.empty[Array[Map[String, BigDecimal]]]

  protected var optMapStringArrays      = Seq.empty[Array[Option[Map[String, String]]]]
  protected var optMapIntArrays         = Seq.empty[Array[Option[Map[String, Int]]]]
  protected var optMapLongArrays        = Seq.empty[Array[Option[Map[String, Long]]]]
  protected var optMapFloatArrays       = Seq.empty[Array[Option[Map[String, Float]]]]
  protected var optMapDoubleArrays      = Seq.empty[Array[Option[Map[String, Double]]]]
  protected var optMapBooleanArrays     = Seq.empty[Array[Option[Map[String, Boolean]]]]
  protected var optMapDateArrays        = Seq.empty[Array[Option[Map[String, Date]]]]
  protected var optMapUUIDArrays        = Seq.empty[Array[Option[Map[String, UUID]]]]
  protected var optMapURIArrays         = Seq.empty[Array[Option[Map[String, URI]]]]
  protected var optMapBigIntArrays      = Seq.empty[Array[Option[Map[String, BigInt]]]]
  protected var optMapBigDecimalArrays  = Seq.empty[Array[Option[Map[String, BigDecimal]]]]

  protected var listOneStringArrays      = Seq.empty[Array[List[String]]]
  protected var listOneIntArrays         = Seq.empty[Array[List[Int]]]
  protected var listOneLongArrays        = Seq.empty[Array[List[Long]]]
  protected var listOneFloatArrays       = Seq.empty[Array[List[Float]]]
  protected var listOneDoubleArrays      = Seq.empty[Array[List[Double]]]
  protected var listOneBooleanArrays     = Seq.empty[Array[List[Boolean]]]
  protected var listOneDateArrays        = Seq.empty[Array[List[Date]]]
  protected var listOneUUIDArrays        = Seq.empty[Array[List[UUID]]]
  protected var listOneURIArrays         = Seq.empty[Array[List[URI]]]
  protected var listOneBigIntArrays      = Seq.empty[Array[List[BigInt]]]
  protected var listOneBigDecimalArrays  = Seq.empty[Array[List[BigDecimal]]]

  protected var listManyStringArrays     = Seq.empty[Array[List[Set[String]]]]
  protected var listManyIntArrays        = Seq.empty[Array[List[Set[Int]]]]
  protected var listManyLongArrays       = Seq.empty[Array[List[Set[Long]]]]
  protected var listManyFloatArrays      = Seq.empty[Array[List[Set[Float]]]]
  protected var listManyDoubleArrays     = Seq.empty[Array[List[Set[Double]]]]
  protected var listManyBooleanArrays    = Seq.empty[Array[List[Set[Boolean]]]]
  protected var listManyDateArrays       = Seq.empty[Array[List[Set[Date]]]]
  protected var listManyUUIDArrays       = Seq.empty[Array[List[Set[UUID]]]]
  protected var listManyURIArrays        = Seq.empty[Array[List[Set[URI]]]]
  protected var listManyBigIntArrays     = Seq.empty[Array[List[Set[BigInt]]]]
  protected var listManyBigDecimalArrays = Seq.empty[Array[List[Set[BigDecimal]]]]

  protected var strArrays     = List.empty[Array[Option[String]]]
  protected var numArrays     = List.empty[Array[Option[Double]]]
  protected var listStrArrays = List.empty[Array[Option[List[String]]]]
  protected var listNumArrays = List.empty[Array[Option[List[Double]]]]
  protected var mapStrArrays  = List.empty[Array[Option[Map[String, String]]]]
  protected var mapNumArrays  = List.empty[Array[Option[Map[String, Double]]]]
  protected var arrayIndexes  = Map.empty[Int, Int] // colIndex -> arrayIndex

  // uses 1-based indexes (slot 1-6, not 0-5)
  private val indexCounts = new Array[Int](7)

  def updateColTypes(colIndex: Int, arrayNo: Int): Unit = {
    arrayIndexes = arrayIndexes + (colIndex -> indexCounts(arrayNo))
    indexCounts(arrayNo) = indexCounts(arrayNo) + 1
  }


  protected def castOne(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
        val array = new Array[String](maxRows)
        oneStringArrays = oneStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[String]

      case "Int" =>
        val array = new Array[Int](maxRows)
        oneIntArrays = oneIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jLong].toInt

      case "Int2" =>
        val array = new Array[Int](maxRows)
        oneIntArrays = oneIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).toString.toInt

      case "Float" =>
        val array = new Array[Float](maxRows)
        oneFloatArrays = oneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jDouble].toFloat

      case "Long" | "ref" | "datom" =>
        val array = new Array[Long](maxRows)
        oneLongArrays = oneLongArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[Long]

      case "Double" =>
        val array = new Array[Double](maxRows)
        oneDoubleArrays = oneDoubleArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[Double]

      case "Boolean" =>
        val array = new Array[Boolean](maxRows)
        oneBooleanArrays = oneBooleanArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[Boolean]

      case "Date" =>
        val array = new Array[Date](maxRows)
        oneDateArrays = oneDateArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[Date]

      case "UUID" =>
        val array = new Array[UUID](maxRows)
        oneUUIDArrays = oneUUIDArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[UUID]

      case "URI" =>
        val array = new Array[URI](maxRows)
        oneURIArrays = oneURIArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          }

      case "BigInt" =>
        val array = new Array[BigInt](maxRows)
        oneBigIntArrays = oneBigIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = BigInt(row.get(colIndex).toString)

      case "BigDecimal" =>
        val array = new Array[BigDecimal](maxRows)
        oneBigDecimalArrays = oneBigDecimalArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = BigDecimal(row.get(colIndex).toString)

      // Generic `v` attribute value converted to String
      case "Any" =>
        val array = new Array[String](maxRows)
        oneStringArrays = oneStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case d: Date if col.attrExpr == "txInstant" => date2strLocal(d)
            case d: Date                                => date2str(d)
            case other                                  => other.toString
          }
    }
  }

  protected def castOptOne(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" if col.enums.nonEmpty =>
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

      case "String" =>
        val array = new Array[Option[String]](maxRows)
        optOneStringArrays = optOneStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[String]
            case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
              .asInstanceOf[jMap[String, Keyword]].values.iterator.next.getName)
          }

      case "Int" =>
        val array = new Array[Option[Int]](maxRows)
        optOneIntArrays = optOneIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Int]
            case v    => Some(v.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong].toInt)
          }

      case "Float" =>
        val array = new Array[Option[Float]](maxRows)
        optOneFloatArrays = optOneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Float]
            case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jDouble].toFloat)
          }

      case "Long" =>
        val array = new Array[Option[Long]](maxRows)
        optOneLongArrays = optOneLongArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Long]
            case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jLong].toLong)
          }

      case "Double" =>
        val array = new Array[Option[Double]](maxRows)
        optOneDoubleArrays = optOneDoubleArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Double]
            case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jDouble].toDouble)
          }

      case "Boolean" =>
        val array = new Array[Option[Boolean]](maxRows)
        optOneBooleanArrays = optOneBooleanArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Boolean]
            case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Boolean])
          }

      case "Date" =>
        val array = new Array[Option[Date]](maxRows)
        optOneDateArrays = optOneDateArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Date]
            case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[Date])
          }

      case "UUID" =>
        val array = new Array[Option[UUID]](maxRows)
        optOneUUIDArrays = optOneUUIDArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[UUID]
            case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[UUID])
          }

      case "URI" =>
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

      case "BigInt" =>
        val array = new Array[Option[BigInt]](maxRows)
        optOneBigIntArrays = optOneBigIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[BigInt]
            case v    => Some(BigInt(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
          }

      case "BigDecimal" =>
        val array = new Array[Option[BigDecimal]](maxRows)
        optOneBigDecimalArrays = optOneBigDecimalArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[BigDecimal]
            case v    => Some(BigDecimal(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
          }
    }
  }


  protected def castMany(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" if col.enums.nonEmpty =>
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

      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[Set[Float]](maxRows)
        manyFloatArrays = manyFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = {
            val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
            var set = Set.empty[Float]
            while (it.hasNext)
              set += it.next.asInstanceOf[jDouble].toFloat
            set
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }


  protected def castOptMany(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" if col.enums.nonEmpty =>
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

      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[Option[Set[Float]]](maxRows)
        optManyFloatArrays = optManyFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Set[Float]]
            case v    =>
              val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
              var set = Set.empty[Float]
              while (it.hasNext)
                set += it.next.asInstanceOf[jDouble].toFloat
              Some(set)
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }


  protected def castMap(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[Map[String, Float]](maxRows)
        mapFloatArrays = mapFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = {
            val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
            var map = Map.empty[String, Float]
            var vs  = new Array[String](2)
            while (it.hasNext) {
              vs = it.next.toString.split("@", 2)
              map += (vs(0) -> vs(1).toFloat)
            }
            map
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }

  protected def castOptMap(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[Option[Map[String, Float]]](maxRows)
        optMapFloatArrays = optMapFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Map[String, Float]]
            case v    =>
              val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
              var map = Map.empty[String, Float]
              var vs  = new Array[String](2)
              while (it.hasNext) {
                vs = it.next.toString.split("@", 2)
                map += (vs(0) -> vs(1).toFloat)
              }
              Some(map)
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }


  protected def castOptApplyOne(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
        val array = new Array[Option[String]](maxRows)
        optOneStringArrays = optOneStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[String]
            case v    => Some(v.asInstanceOf[String])
          }

      case "Int" =>
        val array = new Array[Option[Int]](maxRows)
        optOneIntArrays = optOneIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Int]
            case v    => Some(v.asInstanceOf[jLong].toInt)
          }

      case "Float" =>
        val array = new Array[Option[Float]](maxRows)
        optOneFloatArrays = optOneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Float]
            case v    => Some(v.asInstanceOf[jDouble].toFloat)
          }

      case "Long" =>
        val array = new Array[Option[Long]](maxRows)
        optOneLongArrays = optOneLongArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Long]
            case v    => Some(v.asInstanceOf[jLong].toLong)
          }

      case "Double" =>
        val array = new Array[Option[Double]](maxRows)
        optOneDoubleArrays = optOneDoubleArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Double]
            case v    => Some(v.asInstanceOf[jDouble].toDouble)
          }

      case "Boolean" =>
        val array = new Array[Option[Boolean]](maxRows)
        optOneBooleanArrays = optOneBooleanArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Boolean]
            case v    => Some(v.asInstanceOf[Boolean])
          }

      case "Date" =>
        val array = new Array[Option[Date]](maxRows)
        optOneDateArrays = optOneDateArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Date]
            case v    => Some(v.asInstanceOf[Date])
          }

      case "UUID" =>
        val array = new Array[Option[UUID]](maxRows)
        optOneUUIDArrays = optOneUUIDArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[UUID]
            case v    => Some(v.asInstanceOf[UUID])
          }

      case "URI" =>
        val array = new Array[Option[URI]](maxRows)
        optOneURIArrays = optOneURIArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null     => Option.empty[URI]
            case uri: URI => Some(uri)
            case uriImpl  => Some(new URI(uriImpl.toString))
          }

      case "BigInt" =>
        val array = new Array[Option[BigInt]](maxRows)
        optOneBigIntArrays = optOneBigIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[BigInt]
            case v    => Some(BigInt(v.toString))
          }

      case "BigDecimal" =>
        val array = new Array[Option[BigDecimal]](maxRows)
        optOneBigDecimalArrays = optOneBigDecimalArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[BigDecimal]
            case v    => Some(BigDecimal(v.toString))
          }
    }
  }

  protected def castOptApplyMany(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" if col.enums.nonEmpty =>
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

      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[Option[Set[Float]]](maxRows)
        optManyFloatArrays = optManyFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Set[Float]]
            case v    =>
              val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
              var set = Set.empty[Float]
              while (it.hasNext)
                set += it.next.asInstanceOf[jDouble].toFloat
              Some(set)
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }


  protected def castOptOneRefAttr(colIndex: Int): (jList[AnyRef], Int) => Unit = {
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

  protected def castOptManyRefAttr(colIndex: Int): (jList[AnyRef], Int) => Unit = {
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

  protected def castOptApplyMap(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[Option[Map[String, Float]]](maxRows)
        optMapFloatArrays = optMapFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null => Option.empty[Map[String, Float]]
            case v    =>
              val it  = v.asInstanceOf[jSet[_]].iterator
              var map = Map.empty[String, Float]
              var vs  = new Array[String](2)
              while (it.hasNext) {
                vs = it.next.toString.split("@", 2)
                map += (vs(0) -> vs(1).toFloat)
              }
              Some(map)
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }


  protected def castKeyedMap(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
        val array = new Array[String](maxRows)
        oneStringArrays = oneStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).toString

      case "Int" =>
        val array = new Array[Int](maxRows)
        oneIntArrays = oneIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).toString.toInt

      case "Int2" =>
        val array = new Array[Int](maxRows)
        oneIntArrays = oneIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).toString.toInt

      case "Float" =>
        val array = new Array[Float](maxRows)
        oneFloatArrays = oneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).toString.toFloat

      case "Long" | "ref" | "datom" =>
        val array = new Array[Long](maxRows)
        oneLongArrays = oneLongArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).toString.toLong

      case "Double" =>
        val array = new Array[Double](maxRows)
        oneDoubleArrays = oneDoubleArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).toString.toDouble

      case "Boolean" =>
        val array = new Array[Boolean](maxRows)
        oneBooleanArrays = oneBooleanArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).toString.toBoolean

      case "Date" =>
        val array = new Array[Date](maxRows)
        oneDateArrays = oneDateArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = str2date(row.get(colIndex).toString)

      case "UUID" =>
        val array = new Array[UUID](maxRows)
        oneUUIDArrays = oneUUIDArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = UUID.fromString(row.get(colIndex).toString)

      case "URI" =>
        val array = new Array[URI](maxRows)
        oneURIArrays = oneURIArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = new URI(row.get(colIndex).toString)

      case "BigInt" =>
        val array = new Array[BigInt](maxRows)
        oneBigIntArrays = oneBigIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = BigInt(row.get(colIndex).toString)

      case "BigDecimal" =>
        val array = new Array[BigDecimal](maxRows)
        oneBigDecimalArrays = oneBigDecimalArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = BigDecimal(row.get(colIndex).toString)

      // Generic `v` attribute value converted to String
      case "Any" =>
        val array = new Array[String](maxRows)
        oneStringArrays = oneStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case d: Date if col.attrExpr == "txInstant" => date2strLocal(d)
            case d: Date                                => date2str(d)
            case other                                  => other.toString
          }
    }
  }


  protected def castAggrInt(colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Int](maxRows)
    oneIntArrays = oneIntArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[jLong].toInt
  }

  protected def castAggrDouble(colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Double](maxRows)
    oneDoubleArrays = oneDoubleArrays :+ array
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex).asInstanceOf[Double]
  }


  protected def castAggrOneList(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[List[Float]](maxRows)
        listOneFloatArrays = listOneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = {
            val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
            var list = List.empty[Float]
            while (it.hasNext)
              list = list :+ it.next.asInstanceOf[jDouble].toFloat
            list
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }

  protected def castAggrManyList(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[List[Set[Float]]](maxRows)
        listManyFloatArrays = listManyFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = {
            val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
            var set = Set.empty[Float]
            while (it.hasNext)
              set += it.next.asInstanceOf[jDouble].toFloat
            List(set)
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }


  protected def castAggrOneListDistinct(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[List[Float]](maxRows)
        listOneFloatArrays = listOneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = {
            val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
            var list = List.empty[Float]
            while (it.hasNext)
              list = list :+ it.next.asInstanceOf[jDouble].toFloat
            list
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }

  protected def castAggrManyListDistinct(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[List[Set[Float]]](maxRows)
        listManyFloatArrays = listManyFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = {
            val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
            var set = Set.empty[Float]
            while (it.hasNext)
              set += it.next.asInstanceOf[jDouble].toFloat
            List(set)
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }

  protected def castAggrOneListRand(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[List[Float]](maxRows)
        listOneFloatArrays = listOneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = {
            val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
            var list = List.empty[Float]
            while (it.hasNext)
              list = list :+ it.next.asInstanceOf[jDouble].toFloat
            list
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }

  protected def castAggrManyListRand(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
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

      case "Int" =>
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

      case "Float" =>
        val array = new Array[List[Set[Float]]](maxRows)
        listManyFloatArrays = listManyFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = {
            val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
            var set = Set.empty[Float]
            while (it.hasNext)
              set += it.next.asInstanceOf[jDouble].toFloat
            List(set)
          }

      case "Long" =>
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

      case "Double" =>
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

      case "Boolean" =>
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

      case "Date" =>
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

      case "UUID" =>
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

      case "URI" =>
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

      case "BigInt" =>
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

      case "BigDecimal" =>
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
  }


  protected def castAggrSingleSample(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
        val array = new Array[String](maxRows)
        oneStringArrays = oneStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[String]

      case "Int" =>
        val array = new Array[Int](maxRows)
        oneIntArrays = oneIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[jLong].toInt

      case "Float" =>
        val array = new Array[Float](maxRows)
        oneFloatArrays = oneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[jDouble].toFloat

      case "Long" | "ref" | "datom" =>
        val array = new Array[Long](maxRows)
        oneLongArrays = oneLongArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Long]

      case "Double" =>
        val array = new Array[Double](maxRows)
        oneDoubleArrays = oneDoubleArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Double]

      case "Boolean" =>
        val array = new Array[Boolean](maxRows)
        oneBooleanArrays = oneBooleanArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Boolean]

      case "Date" =>
        val array = new Array[Date](maxRows)
        oneDateArrays = oneDateArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]

      case "UUID" =>
        val array = new Array[UUID](maxRows)
        oneUUIDArrays = oneUUIDArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[UUID]

      case "URI" =>
        val array = new Array[URI](maxRows)
        oneURIArrays = oneURIArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          }

      case "BigInt" =>
        val array = new Array[BigInt](maxRows)
        oneBigIntArrays = oneBigIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = BigInt(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)

      case "BigDecimal" =>
        val array = new Array[BigDecimal](maxRows)
        oneBigDecimalArrays = oneBigDecimalArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = BigDecimal(
            row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[java.math.BigDecimal].toString
          )
    }
  }


  protected def castAggrOneSingle(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
        val array = new Array[String](maxRows)
        oneStringArrays = oneStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[String]

      case "Int" =>
        val array = new Array[Int](maxRows)
        oneIntArrays = oneIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[jLong].toInt

      case "Float" =>
        val array = new Array[Float](maxRows)
        oneFloatArrays = oneFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[jDouble].toFloat

      case "Long" | "ref" | "datom" =>
        val array = new Array[Long](maxRows)
        oneLongArrays = oneLongArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Long]

      case "Double" =>
        val array = new Array[Double](maxRows)
        oneDoubleArrays = oneDoubleArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Double]

      case "Boolean" =>
        val array = new Array[Boolean](maxRows)
        oneBooleanArrays = oneBooleanArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Boolean]

      case "Date" =>
        val array = new Array[Date](maxRows)
        oneDateArrays = oneDateArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]

      case "UUID" =>
        val array = new Array[UUID](maxRows)
        oneUUIDArrays = oneUUIDArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[UUID]

      case "URI" =>
        val array = new Array[URI](maxRows)
        oneURIArrays = oneURIArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex).asInstanceOf[jList[_]].iterator.next match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          }

      case "BigInt" =>
        val array = new Array[BigInt](maxRows)
        oneBigIntArrays = oneBigIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = BigInt(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)

      case "BigDecimal" =>
        val array = new Array[BigDecimal](maxRows)
        oneBigDecimalArrays = oneBigDecimalArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = BigDecimal(
            row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[java.math.BigDecimal].toString
          )
    }
  }


  protected def castAggrManySingle(col: Column, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    col.attrType match {
      case "String" =>
        val array = new Array[Set[String]](maxRows)
        manyStringArrays = manyStringArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex).asInstanceOf[String])

      case "Int" =>
        val array = new Array[Set[Int]](maxRows)
        manyIntArrays = manyIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex).asInstanceOf[jLong].toInt)

      case "Float" =>
        val array = new Array[Set[Float]](maxRows)
        manyFloatArrays = manyFloatArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex).asInstanceOf[jDouble].toFloat)

      case "Long" =>
        val array = new Array[Set[Long]](maxRows)
        manyLongArrays = manyLongArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex).asInstanceOf[Long])

      case "Double" =>
        val array = new Array[Set[Double]](maxRows)
        manyDoubleArrays = manyDoubleArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex).asInstanceOf[Double])

      case "Boolean" =>
        val array = new Array[Set[Boolean]](maxRows)
        manyBooleanArrays = manyBooleanArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex).asInstanceOf[Boolean])

      case "Date" =>
        val array = new Array[Set[Date]](maxRows)
        manyDateArrays = manyDateArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex).asInstanceOf[Date])

      case "UUID" =>
        val array = new Array[Set[UUID]](maxRows)
        manyUUIDArrays = manyUUIDArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex).asInstanceOf[UUID])

      case "URI" =>
        val array = new Array[Set[URI]](maxRows)
        manyURIArrays = manyURIArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(row.get(colIndex) match {
            case uri: URI => uri
            case uriImpl  => new URI(uriImpl.toString)
          })

      case "BigInt" =>
        val array = new Array[Set[BigInt]](maxRows)
        manyBigIntArrays = manyBigIntArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(BigInt(row.get(colIndex).toString))

      case "BigDecimal" =>
        val array = new Array[Set[BigDecimal]](maxRows)
        manyBigDecimalArrays = manyBigDecimalArrays :+ array
        (row: jList[AnyRef], i: Int) =>
          array(i) = Set(BigDecimal(row.get(colIndex).asInstanceOf[java.math.BigDecimal].toString))
    }
  }
}
