package molecule.datomic.base.marshalling.cast

import java.lang.{Double => jDouble, Long => jLong}
import java.net.URI
import java.util.{Date, UUID, List => jList, Map => jMap, Set => jSet}
import clojure.lang.{Keyword, LazySeq, PersistentHashSet, PersistentVector}
import molecule.core.marshalling.Column
import molecule.core.util.{DateHandling, Helpers}
import molecule.datomic.base.marshalling.DateStrLocal
import scala.collection.mutable.ListBuffer

trait DataArrays extends DateHandling with DateStrLocal with Helpers {

  protected var oneStringArrays         = Seq.empty[Array[String]]
  protected var oneIntArrays            = Seq.empty[Array[Int]]
  protected var oneLongArrays           = Seq.empty[Array[Long]]
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
  protected var listManyDoubleArrays     = Seq.empty[Array[List[Set[Double]]]]
  protected var listManyBooleanArrays    = Seq.empty[Array[List[Set[Boolean]]]]
  protected var listManyDateArrays       = Seq.empty[Array[List[Set[Date]]]]
  protected var listManyUUIDArrays       = Seq.empty[Array[List[Set[UUID]]]]
  protected var listManyURIArrays        = Seq.empty[Array[List[Set[URI]]]]
  protected var listManyBigIntArrays     = Seq.empty[Array[List[Set[BigInt]]]]
  protected var listManyBigDecimalArrays = Seq.empty[Array[List[Set[BigDecimal]]]]
}
