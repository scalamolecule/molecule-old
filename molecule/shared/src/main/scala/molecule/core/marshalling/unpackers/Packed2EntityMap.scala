package molecule.core.marshalling.unpackers

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.DateHandling
import molecule.datomic.base.facade.Conn
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Try}

class Packed2EntityMap(conn: Conn) extends DateHandling {

  protected val attrDefinitions: Map[String, (Int, String)] = {
    // Add backrefs
    conn.connProxy.attrMap.flatMap {
      case (ref, (card, "ref")) =>
        val backRef = ref.replace("/", "/_")
        Seq(
          (ref, (card, "ref")),
          (backRef, (2, "ref")) // Backrefs are cardinality-many
        )
      case (attr, (card, tpe))  => Seq((attr, (card, tpe)))
    } +
      // Recognise internal bidirectional edge ref attribute
      (":molecule_Meta/otherEdge" -> (1, "ref"))
  }

  def getTypedValue(card: Int, tpe: String, v: Any): Try[Any] = card match {
    case 1 => tpe match {
      case "String" | "enum" => Try(v.toString)
      case "Int"             => Try(v.toString.toInt)
      case "Long" | "ref"    => Try(v.toString.toLong)
      case "Double"          => Try(v.toString.toDouble)
      case "Boolean"         => Try(v.toString.toBoolean)
      case "Date"            => Try(str2date(v.toString))
      case "UUID"            => Try(UUID.fromString(v.toString))
      case "URI"             => Try(new URI(v.toString))
      case "BigInt"          => Try(BigInt(v.toString))
      case "BigDecimal"      => Try(BigDecimal(v.toString))
      case other             => Failure(MoleculeException("Unexpected other type: " + other))
    }
    case 2 =>
      def mkList[T](cast: String => T): Try[Set[T]] = Try(
        v.asInstanceOf[List[_]].map(v => cast(v.toString)).toSet
      )
      tpe match {
        case "String" | "enum" => mkList((s: String) => s)
        case "Int"             => mkList((s: String) => s.toInt)
        case "Long" | "ref"    => mkList((s: String) => s.toLong)
        case "Double"          => mkList((s: String) => s.toDouble)
        case "Boolean"         => mkList((s: String) => s.toBoolean)
        case "Date"            => mkList((s: String) => str2date(s))
        case "UUID"            => mkList((s: String) => UUID.fromString(s))
        case "URI"             => mkList((s: String) => new URI(s))
        case "BigInt"          => mkList((s: String) => BigInt(s))
        case "BigDecimal"      => mkList((s: String) => BigDecimal(s))
        case other             => Failure(MoleculeException("Unexpected other type: " + other))
      }
    case 3 =>
      def mkMap[T](cast: String => T): Try[Map[String, T]] = Try(
        v.asInstanceOf[List[_]].map { v =>
          val pair = v.toString.split("@", 2)
          pair(0) -> cast(pair(1))
        }.toMap
      )
      tpe match {
        case "String" | "enum" => mkMap((s: String) => s)
        case "Int"             => mkMap((s: String) => s.toInt)
        case "Long" | "ref"    => mkMap((s: String) => s.toLong)
        case "Double"          => mkMap((s: String) => s.toDouble)
        case "Boolean"         => mkMap((s: String) => s.toBoolean)
        case "Date"            => mkMap((s: String) => str2date(s))
        case "UUID"            => mkMap((s: String) => UUID.fromString(s))
        case "URI"             => mkMap((s: String) => new URI(s))
        case "BigInt"          => mkMap((s: String) => BigInt(s))
        case "BigDecimal"      => mkMap((s: String) => BigDecimal(s))
        case other             => Failure(MoleculeException("Unexpected other type: " + other))
      }
  }

  def getTypedNone(tpe: String): Option[Any] = tpe match {
    case "String" | "enum" => Option.empty[String]
    case "Int"             => Option.empty[Int]
    case "Long" | "ref"    => Option.empty[Long]
    case "Double"          => Option.empty[Double]
    case "Boolean"         => Option.empty[Boolean]
    case "Date"            => Option.empty[Date]
    case "UUID"            => Option.empty[UUID]
    case "URI"             => Option.empty[URI]
    case "BigInt"          => Option.empty[BigInt]
    case "BigDecimal"      => Option.empty[BigDecimal]
  }

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

  private val unpackOneString = (v0: String, vs: Iterator[String]) => {
    var v = v0
    buf.setLength(0)
    var first = true
    do {
      if (first) {
        buf.append(v)
        first = false
      } else {
        buf.append("\n")
        buf.append(v)
      }
      v = vs.next()
    } while (vs.hasNext && v != "◄")
    buf.toString
  }

  private lazy val unpackOneInt        = (v: String) => v.toInt
  private lazy val unpackOneLong       = (v: String) => v.toLong
  private lazy val unpackOneDouble     = (v: String) => v.toDouble
  private lazy val unpackOneBoolean    = (v: String) => v.toBoolean
  private lazy val unpackOneDate       = (v: String) => str2date(v)
  private lazy val unpackOneUUID       = (v: String) => UUID.fromString(v)
  private lazy val unpackOneURI        = (v: String) => new URI(v)
  private lazy val unpackOneBigInteger = (v: String) => BigInt(v)
  private lazy val unpackOneBigDecimal = (v: String) => BigDecimal(v)


  private def unpackMany[T](
    v0: String,
    vs: Iterator[String],
    buf: ListBuffer[T],
    unpack: String => T
  ) = {
    var v = v0
    buf.clear()
    do {
      buf.append(unpack(v))
      if (vs.hasNext) v = vs.next()
    } while (vs.hasNext && v != "►")
    buf.toList
  }

  private lazy val unpackManyString = (v0: String, vs: Iterator[String]) => {
    var v = v0
    listString.clear()
    do {
      listString.append(unpackOneString(v, vs))
      if (vs.hasNext) v = vs.next()
    } while (vs.hasNext && v != "►")
    listString.toList
  }


  private def unpackOneRef[Col](
    v0: String,
    vs: Iterator[String],
    unpackColl: (String, Iterator[String]) => (String, Col)
  ) = v0 match {
    case "►"                  => unpackColl(v0, vs)._2
    case kw if kw.head == ':' => kw
    case ref                  => ref.toLong
  }

  private def unpackManyRef[Col](
    v0: String,
    vs: Iterator[String],
    unpackColl: (String, Iterator[String]) => (String, Col)
  ) = {
    var v = v0
    v match {
      case "►" =>
        // Sub data
        val listCol = new ListBuffer[Col]
        do {
          val (v1, col) = unpackColl(v, vs)
          listCol.append(col)
          v = v1
        } while (vs.hasNext && v != "◄◄")
        listCol.toList

      case ref =>
        listLong.clear()
        do {
          listLong.append(unpackOneLong(v))
          if (vs.hasNext) v = vs.next()
        } while (vs.hasNext && v != "►")
        listLong.toList
    }
  }


  private def pair[Col](
    v0: String,
    vs: Iterator[String],
    unpackCol: (String, Iterator[String]) => (String, Col)
  ): (String, Any) = {
    val attr  = v0 match {
      case "►" => vs.next()
      case v   => v
    }
    //    if (attr != ":db/id") {
    //      val (card, tpe) = attrDefinitions(attr)
    //      println("ATTR: " + attr + "   CARD: " + card + "   TPE: " + tpe)
    //    }
    val v     = vs.next()
    val value = attr match {
      case ":db/id"        => unpackOneLong(v)
      case "e"             => unpackOneLong(v)
      case "a"             => unpackOneString(v, vs)
      case "v"             => unpackOneString(v, vs)
      case ":db/t"         => unpackOneLong(v)
      case ":db/tx"        => unpackOneLong(v)
      case ":db/txInstant" => unpackOneDate(v)
      case "op"            => unpackOneBoolean(v)

      case attr =>
        attrDefinitions(attr) match {
          case (1, tpe) => tpe match {
            case "String" | "enum" => unpackOneString(v, vs)
            case "ref"             => unpackOneRef(v, vs, unpackCol)
            case "Int"             => unpackOneInt(v)
            case "Long"            => unpackOneLong(v)
            case "Double"          => unpackOneDouble(v)
            case "Boolean"         => unpackOneBoolean(v)
            case "Date"            => unpackOneDate(v)
            case "UUID"            => unpackOneUUID(v)
            case "URI"             => unpackOneURI(v)
            case "BigInt"          => unpackOneBigInteger(v)
            case "BigDecimal"      => unpackOneBigDecimal(v)
          }

          case (2, tpe) => tpe match {
            case "String" | "enum" => unpackManyString(v, vs)
            case "ref"             => unpackManyRef(v, vs, unpackCol)
            case "Int"             => unpackMany(v, vs, listInt, unpackOneInt)
            case "Long"            => unpackMany(v, vs, listLong, unpackOneLong)
            case "Double"          => unpackMany(v, vs, listDouble, unpackOneDouble)
            case "Boolean"         => unpackMany(v, vs, listBoolean, unpackOneBoolean)
            case "Date"            => unpackMany(v, vs, listDate, unpackOneDate)
            case "UUID"            => unpackMany(v, vs, listUUID, unpackOneUUID)
            case "URI"             => unpackMany(v, vs, listURI, unpackOneURI)
            case "BigInt"          => unpackMany(v, vs, listBigInt, unpackOneBigInteger)
            case "BigDecimal"      => unpackMany(v, vs, listBigDecimal, unpackOneBigDecimal)
          }

          // Map attributes
          case _ => unpackManyString(v, vs)
        }
    }
    //    println(s"PAIR: $attr -> $value")
    attr -> value
  }

  private def unpackList[Col]: (String, Iterator[String]) => (String, Col) =
    (v0: String, vs: Iterator[String]) => {
      var entityList = List.empty[(String, Any)]
      var v          = v0
      do {
        entityList = entityList :+ pair(v, vs, unpackList)
        if (vs.hasNext) v = vs.next()
      } while (vs.hasNext && v != "►" && v != "◄◄")
      (v, entityList.asInstanceOf[Col])
    }

  private def unpackMap[Col]: (String, Iterator[String]) => (String, Col) =
    (v0: String, vs: Iterator[String]) => {
      var entityMap = Map.empty[String, Any]
      var v         = v0
      do {
        entityMap = entityMap + pair(v, vs, unpackMap)
        if (vs.hasNext) v = vs.next()
      } while (vs.hasNext && v != "►" && v != "◄◄")
      (v, entityMap.asInstanceOf[Col])
    }

  def packed2entityList(packed: String): List[(String, Any)] = {
    //    println("\n---------- packed2EntityList -------------\n" + packed + "\n-----------------------------")
    val vs = packed.linesIterator
    unpackList[List[(String, Any)]](vs.next(), vs)._2
  }

  def packed2entityMap(packed: String): Map[String, Any] = {
    //    println("\n---------- packed2entityMap   -------------\n" + packed + "\n-----------------------------")
    val vs = packed.linesIterator
    unpackMap[Map[String, Any]](vs.next(), vs)._2
  }
}
