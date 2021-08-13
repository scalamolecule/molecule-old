package molecule.core.marshalling.unpack

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.util.Helpers
import scala.collection.immutable
import scala.collection.mutable.ListBuffer

trait UnpackTypes extends Helpers {

  protected lazy val buf = new StringBuffer

  protected lazy val bufString     = new ListBuffer[String]
  protected lazy val bufInt        = new ListBuffer[Int]
  protected lazy val bufLong       = new ListBuffer[Long]
  protected lazy val bufDouble     = new ListBuffer[Double]
  protected lazy val bufBoolean    = new ListBuffer[Boolean]
  protected lazy val bufDate       = new ListBuffer[Date]
  protected lazy val bufUUID       = new ListBuffer[UUID]
  protected lazy val bufURI        = new ListBuffer[URI]
  protected lazy val bufBigInt     = new ListBuffer[BigInt]
  protected lazy val bufBigDecimal = new ListBuffer[BigDecimal]

  protected lazy val mapString     = new ListBuffer[(String, String)]
  protected lazy val mapInt        = new ListBuffer[(String, Int)]
  protected lazy val mapLong       = new ListBuffer[(String, Long)]
  protected lazy val mapDouble     = new ListBuffer[(String, Double)]
  protected lazy val mapBoolean    = new ListBuffer[(String, Boolean)]
  protected lazy val mapDate       = new ListBuffer[(String, Date)]
  protected lazy val mapUUID       = new ListBuffer[(String, UUID)]
  protected lazy val mapURI        = new ListBuffer[(String, URI)]
  protected lazy val mapBigInt     = new ListBuffer[(String, BigInt)]
  protected lazy val mapBigDecimal = new ListBuffer[(String, BigDecimal)]

  var pair = new Array[String](2)

  protected var first = true
  protected var v     = ""


  protected lazy val unpackOneString = (v0: String, vs: Iterator[String]) => {
    buf.setLength(0)
    first = true
    v = v0
    do {
      if (first) {
        buf.append(v)
        first = false
      } else {
        buf.append("\n")
        buf.append(v)
      }
      v = vs.next()
    } while (v != "◄")
    buf.toString
  }

  protected lazy val unpackOneInt        = (v: String) => v.toInt
  protected lazy val unpackOneLong       = (v: String) => v.toLong
  protected lazy val unpackOneDouble     = (v: String) => v.toDouble
  protected lazy val unpackOneBoolean    = (v: String) => v.toBoolean
  protected lazy val unpackOneDate       = (v: String) => str2date(v)
  protected lazy val unpackOneUUID       = (v: String) => UUID.fromString(v)
  protected lazy val unpackOneURI        = (v: String) => new URI(v)
  protected lazy val unpackOneBigInt     = (v: String) => BigInt(v)
  protected lazy val unpackOneBigDecimal = (v: String) => BigDecimal(v)
  protected lazy val unpackOneAny        = (s: String) => {
    val v = s.drop(10)
    s.take(10) match {
      case "String    " => v
      case "Integer   " => v.toInt
      case "Long      " => v.toLong
      case "Double    " => v.toDouble
      case "Boolean   " => v.toBoolean
      case "Date      " => str2date(v)
      case "UUID      " => UUID.fromString(v)
      case "URI       " => new URI(v)
      case "BigInteger" => BigInt(v)
      case "BigDecimal" => BigDecimal(v)
    }
  }


  protected lazy val unpackOptOneString = (v0: String, vs: Iterator[String]) => {
    if (v0 == "◄") {
      Option.empty[String]
    } else {
      buf.setLength(0)
      first = true
      v = v0
      do {
        if (first) {
          buf.append(v)
          first = false
        } else {
          buf.append("\n")
          buf.append(v)
        }
        v = vs.next()
      } while (v != "◄")
      Some(buf.toString)
    }
  }

  protected lazy val unpackOptOneEnum       = (v: String) => if (v == "◄") Option.empty[String] else Some(v)
  protected lazy val unpackOptOneInt        = (v: String) => if (v == "◄") Option.empty[Int] else Some(v.toInt)
  protected lazy val unpackOptOneLong       = (v: String) => if (v == "◄") Option.empty[Long] else Some(v.toLong)
  protected lazy val unpackOptOneDouble     = (v: String) => if (v == "◄") Option.empty[Double] else Some(v.toDouble)
  protected lazy val unpackOptOneBoolean    = (v: String) => if (v == "◄") Option.empty[Boolean] else Some(v.toBoolean)
  protected lazy val unpackOptOneDate       = (v: String) => if (v == "◄") Option.empty[Date] else Some(str2date(v))
  protected lazy val unpackOptOneUUID       = (v: String) => if (v == "◄") Option.empty[UUID] else Some(UUID.fromString(v))
  protected lazy val unpackOptOneURI        = (v: String) => if (v == "◄") Option.empty[URI] else Some(new URI(v))
  protected lazy val unpackOptOneBigInt     = (v: String) => if (v == "◄") Option.empty[BigInt] else Some(BigInt(v))
  protected lazy val unpackOptOneBigDecimal = (v: String) => if (v == "◄") Option.empty[BigDecimal] else Some(BigDecimal(v))


  protected lazy val unpackManyString = (v0: String, vs: Iterator[String]) => {
    bufString.clear()
    v = v0
    do {
      bufString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    bufString.toSet
  }

  def unpackMany[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): Set[T] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    buf.toSet
  }

  protected lazy val unpackManyInt        = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufInt, unpackOneInt)
  protected lazy val unpackManyLong       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufLong, unpackOneLong)
  protected lazy val unpackManyDouble     = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufDouble, unpackOneDouble)
  protected lazy val unpackManyBoolean    = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufBoolean, unpackOneBoolean)
  protected lazy val unpackManyDate       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufDate, unpackOneDate)
  protected lazy val unpackManyUUID       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufUUID, unpackOneUUID)
  protected lazy val unpackManyURI        = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufURI, unpackOneURI)
  protected lazy val unpackManyBigInt     = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufBigInt, unpackOneBigInt)
  protected lazy val unpackManyBigDecimal = (v: String, vs: Iterator[String]) => unpackMany(v, vs, bufBigDecimal, unpackOneBigDecimal)


  protected lazy val unpackOptManyString = (v0: String, vs: Iterator[String]) => {
    if (v0 == "◄") {
      Option.empty[Set[String]]
    } else {
      buf.setLength(0)
      v = v0
      do {
        bufString.append(unpackOneString(v, vs))
        v = vs.next()
      } while (v != "◄")
      Some(buf.toString)
    }
  }

  protected lazy val unpackOptManyInt        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Int]] else Some(unpackMany(v, vs, bufInt, unpackOneInt))
  protected lazy val unpackOptManyLong       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Long]] else Some(unpackMany(v, vs, bufLong, unpackOneLong))
  protected lazy val unpackOptManyDouble     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Double]] else Some(unpackMany(v, vs, bufDouble, unpackOneDouble))
  protected lazy val unpackOptManyBoolean    = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Boolean]] else Some(unpackMany(v, vs, bufBoolean, unpackOneBoolean))
  protected lazy val unpackOptManyDate       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Date]] else Some(unpackMany(v, vs, bufDate, unpackOneDate))
  protected lazy val unpackOptManyUUID       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[UUID]] else Some(unpackMany(v, vs, bufUUID, unpackOneUUID))
  protected lazy val unpackOptManyURI        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[URI]] else Some(unpackMany(v, vs, bufURI, unpackOneURI))
  protected lazy val unpackOptManyBigInt     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[BigInt]] else Some(unpackMany(v, vs, bufBigInt, unpackOneBigInt))
  protected lazy val unpackOptManyBigDecimal = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[BigDecimal]] else Some(unpackMany(v, vs, bufBigDecimal, unpackOneBigDecimal))


  protected lazy val unpackMapString = (v0: String, vs: Iterator[String]) => {
    mapString.clear()
    v = v0
    do {
      pair = v.split("@", 2)
      mapString.append(pair(0) -> unpackOneString(pair(1), vs))
      v = vs.next()
    } while (v != "◄")
    mapString.toMap
  }


  def unpackMap[T](v0: String, vs: Iterator[String], buf: ListBuffer[(String, T)], transform: String => T): Map[String, T] = {
    buf.clear()
    v = v0
    do {
      pair = v.split("@", 2)
      buf.append(pair(0) -> transform(pair(1)))
      v = vs.next()
    } while (v != "◄")
    buf.toMap
  }

  protected lazy val unpackMapInt        = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapInt, unpackOneInt)
  protected lazy val unpackMapLong       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapLong, unpackOneLong)
  protected lazy val unpackMapDouble     = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapDouble, unpackOneDouble)
  protected lazy val unpackMapBoolean    = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBoolean, unpackOneBoolean)
  protected lazy val unpackMapDate       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapDate, unpackOneDate)
  protected lazy val unpackMapUUID       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapUUID, unpackOneUUID)
  protected lazy val unpackMapURI        = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapURI, unpackOneURI)
  protected lazy val unpackMapBigInt     = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBigInt, unpackOneBigInt)
  protected lazy val unpackMapBigDecimal = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBigDecimal, unpackOneBigDecimal)


  protected lazy val unpackOptMapString: (String, Iterator[String]) => Option[Map[String, String]] = (v0: String, vs: Iterator[String]) => {
    if (v0 == "◄") {
      Option.empty[Map[String, String]]
    } else {
      mapString.clear()
      v = v0
      do {
        pair = v.split("@", 2)
        mapString.append(pair(0) -> unpackOneString(pair(1), vs))
        v = vs.next()
      } while (v != "◄")
      Some(mapString.toMap)
    }
  }

  protected lazy val unpackOptMapInt        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Int]] else Some(unpackMap(v, vs, mapInt, unpackOneInt))
  protected lazy val unpackOptMapLong       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Long]] else Some(unpackMap(v, vs, mapLong, unpackOneLong))
  protected lazy val unpackOptMapDouble     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Double]] else Some(unpackMap(v, vs, mapDouble, unpackOneDouble))
  protected lazy val unpackOptMapBoolean    = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Boolean]] else Some(unpackMap(v, vs, mapBoolean, unpackOneBoolean))
  protected lazy val unpackOptMapDate       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Date]] else Some(unpackMap(v, vs, mapDate, unpackOneDate))
  protected lazy val unpackOptMapUUID       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, UUID]] else Some(unpackMap(v, vs, mapUUID, unpackOneUUID))
  protected lazy val unpackOptMapURI        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, URI]] else Some(unpackMap(v, vs, mapURI, unpackOneURI))
  protected lazy val unpackOptMapBigInt     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, BigInt]] else Some(unpackMap(v, vs, mapBigInt, unpackOneBigInt))
  protected lazy val unpackOptMapBigDecimal = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, BigDecimal]] else Some(unpackMap(v, vs, mapBigDecimal, unpackOneBigDecimal))


  protected lazy val unpackListString = (v0: String, vs: Iterator[String]) => {
    bufString.clear()
    v = v0
    do {
      bufString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    bufString.toList
  }

  def unpackList[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): List[T] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    buf.toList
  }

  protected lazy val unpackListInt        = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufInt, unpackOneInt)
  protected lazy val unpackListLong       = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufLong, unpackOneLong)
  protected lazy val unpackListDouble     = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufDouble, unpackOneDouble)
  protected lazy val unpackListBoolean    = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufBoolean, unpackOneBoolean)
  protected lazy val unpackListDate       = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufDate, unpackOneDate)
  protected lazy val unpackListUUID       = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufUUID, unpackOneUUID)
  protected lazy val unpackListURI        = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufURI, unpackOneURI)
  protected lazy val unpackListBigInt     = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufBigInt, unpackOneBigInt)
  protected lazy val unpackListBigDecimal = (v: String, vs: Iterator[String]) => unpackList(v, vs, bufBigDecimal, unpackOneBigDecimal)


  protected lazy val unpackListSetString = (v0: String, vs: Iterator[String]) => {
    bufString.clear()
    v = v0
    do {
      bufString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    List(bufString.toSet)
  }

  def unpackListSet[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): List[Set[T]] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    List(buf.toSet)
  }

  protected lazy val unpackListSetInt        = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufInt, unpackOneInt)
  protected lazy val unpackListSetLong       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufLong, unpackOneLong)
  protected lazy val unpackListSetDouble     = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufDouble, unpackOneDouble)
  protected lazy val unpackListSetBoolean    = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufBoolean, unpackOneBoolean)
  protected lazy val unpackListSetDate       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufDate, unpackOneDate)
  protected lazy val unpackListSetUUID       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufUUID, unpackOneUUID)
  protected lazy val unpackListSetURI        = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufURI, unpackOneURI)
  protected lazy val unpackListSetBigInt     = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufBigInt, unpackOneBigInt)
  protected lazy val unpackListSetBigDecimal = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, bufBigDecimal, unpackOneBigDecimal)


  protected lazy val unpackSetOneString = (v0: String, vs: Iterator[String]) => {
    buf.setLength(0)
    first = true
    v = v0
    do {
      if (first) {
        buf.append(v)
        first = false
      } else {
        buf.append("\n")
        buf.append(v)
      }
      v = vs.next()
    } while (v != "◄")
    Set(buf.toString)
  }

  protected lazy val unpackSetOneInt        = (v: String) => Set(v.toInt)
  protected lazy val unpackSetOneLong       = (v: String) => Set(v.toLong)
  protected lazy val unpackSetOneDouble     = (v: String) => Set(v.toDouble)
  protected lazy val unpackSetOneBoolean    = (v: String) => Set(v.toBoolean)
  protected lazy val unpackSetOneDate       = (v: String) => Set(str2date(v))
  protected lazy val unpackSetOneUUID       = (v: String) => Set(UUID.fromString(v))
  protected lazy val unpackSetOneURI        = (v: String) => Set(new URI(v))
  protected lazy val unpackSetOneBigInt     = (v: String) => Set(BigInt(v))
  protected lazy val unpackSetOneBigDecimal = (v: String) => Set(BigDecimal(v))
}
