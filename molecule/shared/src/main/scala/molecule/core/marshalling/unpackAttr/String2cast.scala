package molecule.core.marshalling.unpackAttr

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import scala.collection.mutable.ListBuffer

object String2cast extends Helpers {

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

  private var pair  = new Array[String](2)
  private var v     = ""
  private var first = true


  lazy val unpackOneString = (v0: String, vs: Iterator[String]) => {
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

  lazy val unpackOneEnum       = (v: String) => v
  lazy val unpackOneInt        = (v: String) => v.toInt
  lazy val unpackOneLong       = (v: String) => v.toLong
  lazy val unpackOneDouble     = (v: String) => v.toDouble
  lazy val unpackOneBoolean    = (v: String) => v.toBoolean
  lazy val unpackOneDate       = (v: String) => str2date(v)
  lazy val unpackOneUUID       = (v: String) => UUID.fromString(v)
  lazy val unpackOneURI        = (v: String) => new URI(v)
  lazy val unpackOneBigInt     = (v: String) => BigInt(v)
  lazy val unpackOneBigDecimal = (v: String) => BigDecimal(v)
  lazy val unpackOneAny        = (s: String, vs: Iterator[String]) => {
    val v = s.drop(10)
    s.take(10) match {
      case "String    " => unpackOneString(v, vs)
      case "Int       " => v.toInt
      case "Long      " => v.toLong
      case "ref       " => v.toLong
      case "Double    " => v.toDouble
      case "Boolean   " => v.toBoolean
      case "Date      " => str2date(v)
      case "UUID      " => UUID.fromString(v)
      case "URI       " => new URI(v)
      case "BigInt    " => BigInt(v)
      case "BigDecimal" => BigDecimal(v)
      case "enum      " => v // always single line
      case x            => throw MoleculeException(s"Unexpected unpackOneAny prefix `$x`.")
    }
  }


  lazy val unpackOptOneString = (v0: String, vs: Iterator[String]) => {
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

  lazy val unpackOptOneEnum       = (v: String) => if (v == "◄") Option.empty[String] else Some(unpackOneEnum(v))
  lazy val unpackOptOneInt        = (v: String) => if (v == "◄") Option.empty[Int] else Some(unpackOneInt(v))
  lazy val unpackOptOneLong       = (v: String) => if (v == "◄") Option.empty[Long] else Some(unpackOneLong(v))
  lazy val unpackOptOneDouble     = (v: String) => if (v == "◄") Option.empty[Double] else Some(unpackOneDouble(v))
  lazy val unpackOptOneBoolean    = (v: String) => if (v == "◄") Option.empty[Boolean] else Some(unpackOneBoolean(v))
  lazy val unpackOptOneDate       = (v: String) => if (v == "◄") Option.empty[Date] else Some(unpackOneDate(v))
  lazy val unpackOptOneUUID       = (v: String) => if (v == "◄") Option.empty[UUID] else Some(unpackOneUUID(v))
  lazy val unpackOptOneURI        = (v: String) => if (v == "◄") Option.empty[URI] else Some(unpackOneURI(v))
  lazy val unpackOptOneBigInt     = (v: String) => if (v == "◄") Option.empty[BigInt] else Some(unpackOneBigInt(v))
  lazy val unpackOptOneBigDecimal = (v: String) => if (v == "◄") Option.empty[BigDecimal] else Some(unpackOneBigDecimal(v))


  def unpackMany[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): Set[T] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    buf.toSet
  }

  lazy val unpackManyString = (v0: String, vs: Iterator[String]) => {
    listString.clear()
    v = v0
    do {
      listString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    listString.toSet
  }

  lazy val unpackManyEnum       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listString, unpackOneEnum)
  lazy val unpackManyInt        = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listInt, unpackOneInt)
  lazy val unpackManyLong       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listLong, unpackOneLong)
  lazy val unpackManyDouble     = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listDouble, unpackOneDouble)
  lazy val unpackManyBoolean    = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listBoolean, unpackOneBoolean)
  lazy val unpackManyDate       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listDate, unpackOneDate)
  lazy val unpackManyUUID       = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listUUID, unpackOneUUID)
  lazy val unpackManyURI        = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listURI, unpackOneURI)
  lazy val unpackManyBigInt     = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listBigInt, unpackOneBigInt)
  lazy val unpackManyBigDecimal = (v: String, vs: Iterator[String]) => unpackMany(v, vs, listBigDecimal, unpackOneBigDecimal)


  lazy val unpackOptManyString     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[String]] else Some(unpackManyString(v, vs))
  lazy val unpackOptManyEnum       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[String]] else Some(unpackManyEnum(v, vs))
  lazy val unpackOptManyInt        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Int]] else Some(unpackManyInt(v, vs))
  lazy val unpackOptManyLong       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Long]] else Some(unpackManyLong(v, vs))
  lazy val unpackOptManyDouble     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Double]] else Some(unpackManyDouble(v, vs))
  lazy val unpackOptManyBoolean    = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Boolean]] else Some(unpackManyBoolean(v, vs))
  lazy val unpackOptManyDate       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[Date]] else Some(unpackManyDate(v, vs))
  lazy val unpackOptManyUUID       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[UUID]] else Some(unpackManyUUID(v, vs))
  lazy val unpackOptManyURI        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[URI]] else Some(unpackManyURI(v, vs))
  lazy val unpackOptManyBigInt     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[BigInt]] else Some(unpackManyBigInt(v, vs))
  lazy val unpackOptManyBigDecimal = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Set[BigDecimal]] else Some(unpackManyBigDecimal(v, vs))


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

  lazy val unpackMapString = (v0: String, vs: Iterator[String]) => {
    mapString.clear()
    v = v0
    do {
      pair = v.split("@", 2)
      mapString.append(pair(0) -> unpackOneString(pair(1), vs))
      v = vs.next()
    } while (v != "◄")
    mapString.toMap
  }

  lazy val unpackMapInt        = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapInt, unpackOneInt)
  lazy val unpackMapLong       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapLong, unpackOneLong)
  lazy val unpackMapDouble     = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapDouble, unpackOneDouble)
  lazy val unpackMapBoolean    = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBoolean, unpackOneBoolean)
  lazy val unpackMapDate       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapDate, unpackOneDate)
  lazy val unpackMapUUID       = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapUUID, unpackOneUUID)
  lazy val unpackMapURI        = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapURI, unpackOneURI)
  lazy val unpackMapBigInt     = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBigInt, unpackOneBigInt)
  lazy val unpackMapBigDecimal = (v: String, vs: Iterator[String]) => unpackMap(v, vs, mapBigDecimal, unpackOneBigDecimal)


  lazy val unpackOptMapString     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, String]] else Some(unpackMapString(v, vs))
  lazy val unpackOptMapInt        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Int]] else Some(unpackMapInt(v, vs))
  lazy val unpackOptMapLong       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Long]] else Some(unpackMapLong(v, vs))
  lazy val unpackOptMapDouble     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Double]] else Some(unpackMapDouble(v, vs))
  lazy val unpackOptMapBoolean    = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Boolean]] else Some(unpackMapBoolean(v, vs))
  lazy val unpackOptMapDate       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, Date]] else Some(unpackMapDate(v, vs))
  lazy val unpackOptMapUUID       = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, UUID]] else Some(unpackMapUUID(v, vs))
  lazy val unpackOptMapURI        = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, URI]] else Some(unpackMapURI(v, vs))
  lazy val unpackOptMapBigInt     = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, BigInt]] else Some(unpackMapBigInt(v, vs))
  lazy val unpackOptMapBigDecimal = (v: String, vs: Iterator[String]) => if (v == "◄") Option.empty[Map[String, BigDecimal]] else Some(unpackMapBigDecimal(v, vs))


  def unpackList[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): List[T] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    buf.toList
  }

  lazy val unpackListString     = (v0: String, vs: Iterator[String]) => {
    listString.clear()
    v = v0
    do {
      listString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    listString.toList
  }
  lazy val unpackListInt        = (v: String, vs: Iterator[String]) => unpackList(v, vs, listInt, unpackOneInt)
  lazy val unpackListLong       = (v: String, vs: Iterator[String]) => unpackList(v, vs, listLong, unpackOneLong)
  lazy val unpackListDouble     = (v: String, vs: Iterator[String]) => unpackList(v, vs, listDouble, unpackOneDouble)
  lazy val unpackListBoolean    = (v: String, vs: Iterator[String]) => unpackList(v, vs, listBoolean, unpackOneBoolean)
  lazy val unpackListDate       = (v: String, vs: Iterator[String]) => unpackList(v, vs, listDate, unpackOneDate)
  lazy val unpackListUUID       = (v: String, vs: Iterator[String]) => unpackList(v, vs, listUUID, unpackOneUUID)
  lazy val unpackListURI        = (v: String, vs: Iterator[String]) => unpackList(v, vs, listURI, unpackOneURI)
  lazy val unpackListBigInt     = (v: String, vs: Iterator[String]) => unpackList(v, vs, listBigInt, unpackOneBigInt)
  lazy val unpackListBigDecimal = (v: String, vs: Iterator[String]) => unpackList(v, vs, listBigDecimal, unpackOneBigDecimal)


  def unpackListSet[T](v0: String, vs: Iterator[String], buf: ListBuffer[T], transform: String => T): List[Set[T]] = {
    buf.clear()
    v = v0
    do {
      buf.append(transform(v))
      v = vs.next()
    } while (v != "◄")
    List(buf.toSet)
  }

  lazy val unpackListSetString = (v0: String, vs: Iterator[String]) => {
    listString.clear()
    v = v0
    do {
      listString.append(unpackOneString(v, vs))
      v = vs.next()
    } while (v != "◄")
    List(listString.toSet)
  }

  lazy val unpackListSetInt        = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listInt, unpackOneInt)
  lazy val unpackListSetLong       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listLong, unpackOneLong)
  lazy val unpackListSetDouble     = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listDouble, unpackOneDouble)
  lazy val unpackListSetBoolean    = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listBoolean, unpackOneBoolean)
  lazy val unpackListSetDate       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listDate, unpackOneDate)
  lazy val unpackListSetUUID       = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listUUID, unpackOneUUID)
  lazy val unpackListSetURI        = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listURI, unpackOneURI)
  lazy val unpackListSetBigInt     = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listBigInt, unpackOneBigInt)
  lazy val unpackListSetBigDecimal = (v: String, vs: Iterator[String]) => unpackListSet(v, vs, listBigDecimal, unpackOneBigDecimal)
}
