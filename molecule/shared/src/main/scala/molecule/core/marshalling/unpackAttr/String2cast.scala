package molecule.core.marshalling.unpackAttr

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.util.Helpers
import scala.collection.mutable.ListBuffer

trait String2cast extends Helpers {

  private lazy val buf = new StringBuffer

  private lazy val listString     = new ListBuffer[String]
  private lazy val listInt        = new ListBuffer[Int]
  private lazy val listLong       = new ListBuffer[Long]
  private lazy val listDouble     = new ListBuffer[Double]
  private lazy val listBoolean    = new ListBuffer[Boolean]
  private lazy val listDate       = new ListBuffer[Date]
  private lazy val listUUID       = new ListBuffer[UUID]
  private lazy val listURI        = new ListBuffer[URI]
  private lazy val listBigInt     = new ListBuffer[BigInt]
  private lazy val listBigDecimal = new ListBuffer[BigDecimal]

  private lazy val mapString     = new ListBuffer[(String, String)]
  private lazy val mapInt        = new ListBuffer[(String, Int)]
  private lazy val mapLong       = new ListBuffer[(String, Long)]
  private lazy val mapDouble     = new ListBuffer[(String, Double)]
  private lazy val mapBoolean    = new ListBuffer[(String, Boolean)]
  private lazy val mapDate       = new ListBuffer[(String, Date)]
  private lazy val mapUUID       = new ListBuffer[(String, UUID)]
  private lazy val mapURI        = new ListBuffer[(String, URI)]
  private lazy val mapBigInt     = new ListBuffer[(String, BigInt)]
  private lazy val mapBigDecimal = new ListBuffer[(String, BigDecimal)]

  protected var pair  = new Array[String](2)
  protected var v     = ""
  protected var first = true


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

  protected lazy val unpackOneEnum       = (v: String) => v
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
      case "Int       " => v.toInt
      case "Long      " => v.toLong
      case "Double    " => v.toDouble
      case "Boolean   " => v.toBoolean
      case "Date      " => str2date(v)
      case "UUID      " => UUID.fromString(v)
      case "URI       " => new URI(v)
      case "BigInt    " => BigInt(v)
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

  protected lazy val unpackOptOneEnum       = (v: String) => if (v == "◄") Option.empty[String] else Some(unpackOneEnum(v))
  protected lazy val unpackOptOneInt        = (v: String) => if (v == "◄") Option.empty[Int] else Some(unpackOneInt(v))
  protected lazy val unpackOptOneLong       = (v: String) => if (v == "◄") Option.empty[Long] else Some(unpackOneLong(v))
  protected lazy val unpackOptOneDouble     = (v: String) => if (v == "◄") Option.empty[Double] else Some(unpackOneDouble(v))
  protected lazy val unpackOptOneBoolean    = (v: String) => if (v == "◄") Option.empty[Boolean] else Some(unpackOneBoolean(v))
  protected lazy val unpackOptOneDate       = (v: String) => if (v == "◄") Option.empty[Date] else Some(unpackOneDate(v))
  protected lazy val unpackOptOneUUID       = (v: String) => if (v == "◄") Option.empty[UUID] else Some(unpackOneUUID(v))
  protected lazy val unpackOptOneURI        = (v: String) => if (v == "◄") Option.empty[URI] else Some(unpackOneURI(v))
  protected lazy val unpackOptOneBigInt     = (v: String) => if (v == "◄") Option.empty[BigInt] else Some(unpackOneBigInt(v))
  protected lazy val unpackOptOneBigDecimal = (v: String) => if (v == "◄") Option.empty[BigDecimal] else Some(unpackOneBigDecimal(v))


  def unpackMany[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): Set[T] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    buf.toSet
  }

  protected lazy val unpackManyString = (v0: String, vs: Iterator[String]) => {
    listString.clear()
    v = v0
    do {
      listString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    listString.toSet
  }

  protected lazy val unpackManyEnum       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listString, unpackOneEnum)
  protected lazy val unpackManyInt        = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listInt, unpackOneInt)
  protected lazy val unpackManyLong       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listLong, unpackOneLong)
  protected lazy val unpackManyDouble     = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listDouble, unpackOneDouble)
  protected lazy val unpackManyBoolean    = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listBoolean, unpackOneBoolean)
  protected lazy val unpackManyDate       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listDate, unpackOneDate)
  protected lazy val unpackManyUUID       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listUUID, unpackOneUUID)
  protected lazy val unpackManyURI        = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listURI, unpackOneURI)
  protected lazy val unpackManyBigInt     = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listBigInt, unpackOneBigInt)
  protected lazy val unpackManyBigDecimal = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listBigDecimal, unpackOneBigDecimal)


  protected lazy val unpackOptManyString     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[String]] else Some(unpackManyString(v, vs))
  protected lazy val unpackOptManyEnum       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[String]] else Some(unpackManyEnum(v, vs))
  protected lazy val unpackOptManyInt        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Int]] else Some(unpackManyInt(v, vs))
  protected lazy val unpackOptManyLong       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Long]] else Some(unpackManyLong(v, vs))
  protected lazy val unpackOptManyDouble     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Double]] else Some(unpackManyDouble(v, vs))
  protected lazy val unpackOptManyBoolean    = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Boolean]] else Some(unpackManyBoolean(v, vs))
  protected lazy val unpackOptManyDate       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Date]] else Some(unpackManyDate(v, vs))
  protected lazy val unpackOptManyUUID       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[UUID]] else Some(unpackManyUUID(v, vs))
  protected lazy val unpackOptManyURI        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[URI]] else Some(unpackManyURI(v, vs))
  protected lazy val unpackOptManyBigInt     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[BigInt]] else Some(unpackManyBigInt(v, vs))
  protected lazy val unpackOptManyBigDecimal = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[BigDecimal]] else Some(unpackManyBigDecimal(v, vs))


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

  protected lazy val unpackMapInt        = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapInt, unpackOneInt)
  protected lazy val unpackMapLong       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapLong, unpackOneLong)
  protected lazy val unpackMapDouble     = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapDouble, unpackOneDouble)
  protected lazy val unpackMapBoolean    = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBoolean, unpackOneBoolean)
  protected lazy val unpackMapDate       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapDate, unpackOneDate)
  protected lazy val unpackMapUUID       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapUUID, unpackOneUUID)
  protected lazy val unpackMapURI        = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapURI, unpackOneURI)
  protected lazy val unpackMapBigInt     = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBigInt, unpackOneBigInt)
  protected lazy val unpackMapBigDecimal = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBigDecimal, unpackOneBigDecimal)


  protected lazy val unpackOptMapString     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, String]] else Some(unpackMapString(v, vs))
  protected lazy val unpackOptMapInt        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Int]] else Some(unpackMapInt(v, vs))
  protected lazy val unpackOptMapLong       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Long]] else Some(unpackMapLong(v, vs))
  protected lazy val unpackOptMapDouble     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Double]] else Some(unpackMapDouble(v, vs))
  protected lazy val unpackOptMapBoolean    = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Boolean]] else Some(unpackMapBoolean(v, vs))
  protected lazy val unpackOptMapDate       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Date]] else Some(unpackMapDate(v, vs))
  protected lazy val unpackOptMapUUID       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, UUID]] else Some(unpackMapUUID(v, vs))
  protected lazy val unpackOptMapURI        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, URI]] else Some(unpackMapURI(v, vs))
  protected lazy val unpackOptMapBigInt     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, BigInt]] else Some(unpackMapBigInt(v, vs))
  protected lazy val unpackOptMapBigDecimal = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, BigDecimal]] else Some(unpackMapBigDecimal(v, vs))


  def unpackList[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): List[T] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    buf.toList
  }

  protected lazy val unpackListString     = (v0: String, vs: Iterator[String]) => {
    listString.clear()
    v = v0
    do {
      listString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    listString.toList
  }
  protected lazy val unpackListInt        = (v: String, vs: Iterator[String]) => unpackList(v, vs, listInt, unpackOneInt)
  protected lazy val unpackListLong       = (v: String, vs: Iterator[String]) => unpackList(v, vs, listLong, unpackOneLong)
  protected lazy val unpackListDouble     = (v: String, vs: Iterator[String]) => unpackList(v, vs, listDouble, unpackOneDouble)
  protected lazy val unpackListBoolean    = (v: String, vs: Iterator[String]) => unpackList(v, vs, listBoolean, unpackOneBoolean)
  protected lazy val unpackListDate       = (v: String, vs: Iterator[String]) => unpackList(v, vs, listDate, unpackOneDate)
  protected lazy val unpackListUUID       = (v: String, vs: Iterator[String]) => unpackList(v, vs, listUUID, unpackOneUUID)
  protected lazy val unpackListURI        = (v: String, vs: Iterator[String]) => unpackList(v, vs, listURI, unpackOneURI)
  protected lazy val unpackListBigInt     = (v: String, vs: Iterator[String]) => unpackList(v, vs, listBigInt, unpackOneBigInt)
  protected lazy val unpackListBigDecimal = (v: String, vs: Iterator[String]) => unpackList(v, vs, listBigDecimal, unpackOneBigDecimal)


  def unpackListSet[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): List[Set[T]] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    List(buf.toSet)
  }

  protected lazy val unpackListSetString = (v0: String, vs: Iterator[String]) => {
    listString.clear()
    v = v0
    do {
      listString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    List(listString.toSet)
  }

  protected lazy val unpackListSetInt        = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listInt, unpackOneInt)
  protected lazy val unpackListSetLong       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listLong, unpackOneLong)
  protected lazy val unpackListSetDouble     = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listDouble, unpackOneDouble)
  protected lazy val unpackListSetBoolean    = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listBoolean, unpackOneBoolean)
  protected lazy val unpackListSetDate       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listDate, unpackOneDate)
  protected lazy val unpackListSetUUID       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listUUID, unpackOneUUID)
  protected lazy val unpackListSetURI        = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listURI, unpackOneURI)
  protected lazy val unpackListSetBigInt     = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listBigInt, unpackOneBigInt)
  protected lazy val unpackListSetBigDecimal = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listBigDecimal, unpackOneBigDecimal)
}
