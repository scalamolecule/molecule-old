package molecule.datomic.base.marshalling.sorting

import java.lang.{Long => jLong}
import java.util
import java.util.{Comparator, Date, Iterator => jIterator, List => jList}
import clojure.lang.Keyword
import datomic.Peer.toT
import datomic.{Database => PeerDb, Datom => PeerDatom}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.ast.SortCoordinate
import molecule.core.util.JavaConversions
import molecule.datomic.base.facade.{Conn, DatomicDb}
import molecule.datomic.base.marshalling.PackBase
import molecule.datomic.base.util.JavaHelpers
import scala.collection.StringOps
import scala.concurrent.{ExecutionContext, Future}

case class SortDatoms_Peer(
  conn: Conn,
  adhocDb: DatomicDb,
  attrs: Seq[String],
  sortCoordinates: List[List[SortCoordinate]],
  datoms: jIterator[PeerDatom],
  tOpt: Option[Long] = None
)(implicit ec: ExecutionContext) extends Comparator[jList[AnyRef]]
  with JavaHelpers with PackBase with JavaConversions {

  def getPacked: Future[(Int, StringBuffer)] = {
    val datom2row  = getDatom2row
    val rows       = new util.ArrayList[jList[AnyRef]]()
    var totalCount = 0
    datoms.asScala
      .foldLeft(Future(rows))((futRows, datom) => futRows.flatMap { rows =>
        totalCount += 1
        datom2row(rows, datom)
      })
      .map { rows =>
        val sb         = new StringBuffer()
        val row2packed = getRow2Packed
        sortRows(rows).forEach(row =>
          row2packed(sb, row)
        )
        (totalCount, sb)
      }
  }

  lazy val attrMap: Map[String, (Int, String)] = conn.connProxy.attrMap ++ Seq(":db/txInstant" -> (1, "Date"))

  private def fut(row: jList[AnyRef], v: AnyRef): Future[jList[AnyRef]] = Future {
    row.add(v)
    row
  }

  def getValue(attr: String): (jList[AnyRef], PeerDatom) => Future[jList[AnyRef]] = {
    attr match {
      case "e" => (row: jList[AnyRef], d: PeerDatom) => fut(row, d.e)
      case "a" => (row: jList[AnyRef], d: PeerDatom) => fut(row, adhocDb.getDatomicDb.asInstanceOf[PeerDb].ident(d.a))
      case "v" => (row: jList[AnyRef], d: PeerDatom) =>
        Future {
          val a         = adhocDb.getDatomicDb.asInstanceOf[PeerDb].ident(d.a).toString
          val (_, tpe)  = attrMap.getOrElse(a,
            throw MoleculeException(s"Unexpected attribute `$a` not found in attrMap.")
          )
          val tpePrefix = tpe + " " * (10 - tpe.length)
          row.add(tpe match {
            case "String" => tpePrefix + d.v.toString
            case "Date"   => tpePrefix + date2str(d.v.asInstanceOf[Date])
            case _        => tpePrefix + d.v.toString
          })
          row
        }

      case "t" if tOpt.isDefined => (row: jList[AnyRef], _: PeerDatom) => fut(row, tOpt.get.asInstanceOf[AnyRef])
      case "t"                   => (row: jList[AnyRef], d: PeerDatom) => fut(row, toT(d.tx).asInstanceOf[AnyRef])
      case "tx"                  => (row: jList[AnyRef], d: PeerDatom) => fut(row, d.tx)
      case "txInstant"           => (row: jList[AnyRef], d: PeerDatom) =>
        adhocDb.entity(conn, d.tx).rawValue(":db/txInstant").map { v =>
          row.add(v.asInstanceOf[Date])
          row
        }
      case "op"                  => (row: jList[AnyRef], d: PeerDatom) => fut(row, d.added.asInstanceOf[AnyRef])
    }
  }

  def getDatom2row: (util.ArrayList[jList[AnyRef]], PeerDatom) => Future[util.ArrayList[jList[AnyRef]]] = attrs.size match {
    case 1 =>
      val get1 = getValue(attrs.head)
      (rows: util.ArrayList[jList[AnyRef]], d: PeerDatom) =>
        for {
          row1 <- get1(new util.ArrayList[AnyRef](1), d)
        } yield {
          rows.add(row1)
          rows
        }

    case 2 =>
      val get1 = getValue(attrs.head)
      val get2 = getValue(attrs(1))
      (rows: util.ArrayList[jList[AnyRef]], d: PeerDatom) =>
        for {
          row1 <- get1(new util.ArrayList[AnyRef](2), d)
          row2 <- get2(row1, d)
        } yield {
          rows.add(row2)
          rows
        }

    case 3 =>
      val get1 = getValue(attrs.head)
      val get2 = getValue(attrs(1))
      val get3 = getValue(attrs(2))
      (rows: util.ArrayList[jList[AnyRef]], d: PeerDatom) =>
        for {
          row1 <- get1(new util.ArrayList[AnyRef](3), d)
          row2 <- get2(row1, d)
          row3 <- get3(row2, d)
        } yield {
          rows.add(row3)
          rows
        }

    case 4 =>
      val get1 = getValue(attrs.head)
      val get2 = getValue(attrs(1))
      val get3 = getValue(attrs(2))
      val get4 = getValue(attrs(3))
      (rows: util.ArrayList[jList[AnyRef]], d: PeerDatom) =>
        for {
          row1 <- get1(new util.ArrayList[AnyRef](4), d)
          row2 <- get2(row1, d)
          row3 <- get3(row2, d)
          row4 <- get4(row3, d)
        } yield {
          rows.add(row4)
          rows
        }

    case 5 =>
      val get1 = getValue(attrs.head)
      val get2 = getValue(attrs(1))
      val get3 = getValue(attrs(2))
      val get4 = getValue(attrs(3))
      val get5 = getValue(attrs(4))
      (rows: util.ArrayList[jList[AnyRef]], d: PeerDatom) =>
        for {
          row1 <- get1(new util.ArrayList[AnyRef](5), d)
          row2 <- get2(row1, d)
          row3 <- get3(row2, d)
          row4 <- get4(row3, d)
          row5 <- get5(row4, d)
        } yield {
          rows.add(row5)
          rows
        }

    case 6 =>
      val get1 = getValue(attrs.head)
      val get2 = getValue(attrs(1))
      val get3 = getValue(attrs(2))
      val get4 = getValue(attrs(3))
      val get5 = getValue(attrs(4))
      val get6 = getValue(attrs(5))
      (rows: util.ArrayList[jList[AnyRef]], d: PeerDatom) =>
        for {
          row1 <- get1(new util.ArrayList[AnyRef](6), d)
          row2 <- get2(row1, d)
          row3 <- get3(row2, d)
          row4 <- get4(row3, d)
          row5 <- get5(row4, d)
          row6 <- get6(row5, d)
        } yield {
          rows.add(row6)
          rows
        }

    case 7 =>
      val get1 = getValue(attrs.head)
      val get2 = getValue(attrs(1))
      val get3 = getValue(attrs(2))
      val get4 = getValue(attrs(3))
      val get5 = getValue(attrs(4))
      val get6 = getValue(attrs(5))
      val get7 = getValue(attrs(6))
      (rows: util.ArrayList[jList[AnyRef]], d: PeerDatom) =>
        for {
          row1 <- get1(new util.ArrayList[AnyRef](7), d)
          row2 <- get2(row1, d)
          row3 <- get3(row2, d)
          row4 <- get4(row3, d)
          row5 <- get5(row4, d)
          row6 <- get6(row5, d)
          row7 <- get7(row6, d)
        } yield {
          rows.add(row7)
          rows
        }
  }


  def getSorter(sortCoordinate: SortCoordinate): (jList[AnyRef], jList[AnyRef]) => Int = {
    val SortCoordinate(i, asc, attr, _, _, _, _)          = sortCoordinate
    val comparison: (jList[AnyRef], jList[AnyRef]) => Int = attr match {
      case "a" => (x: jList[AnyRef], y: jList[AnyRef]) =>
        x.get(i).asInstanceOf[Keyword].getName.compareTo(y.get(i).asInstanceOf[Keyword].getName)

      case "txInstant" => (x: jList[AnyRef], y: jList[AnyRef]) =>
        x.get(i).asInstanceOf[Date].compareTo(y.get(i).asInstanceOf[Date])

      case "op" => (x: jList[AnyRef], y: jList[AnyRef]) =>
        x.get(i).asInstanceOf[Boolean].compareTo(y.get(i).asInstanceOf[Boolean])

      case "v" => (x: jList[AnyRef], y: jList[AnyRef]) =>
        // compare string values without type prefix
        new StringOps(x.get(i).asInstanceOf[String]).drop(10)
          .compareTo(
            new StringOps(y.get(i).asInstanceOf[String]).drop(10)
          )

      // e, t, tx
      case _ => (x: jList[AnyRef], y: jList[AnyRef]) =>
        x.get(i).asInstanceOf[jLong].compareTo(y.get(i).asInstanceOf[jLong])
    }
    if (asc)
      (x: jList[AnyRef], y: jList[AnyRef]) => comparison(x, y)
    else
      (x: jList[AnyRef], y: jList[AnyRef]) => comparison(y, x)
  }

  def pack(i: Int, attr: String): (StringBuffer, jList[AnyRef]) => StringBuffer = {
    attr match {
      case "txInstant" => (sb: StringBuffer, row: jList[AnyRef]) =>
        add(sb, date2str(row.get(i).asInstanceOf[Date]))

      case "op" => (sb: StringBuffer, row: jList[AnyRef]) =>
        add(sb, row.get(i).toString)

      case "v" => (sb: StringBuffer, row: jList[AnyRef]) =>
        val s = row.get(i).toString
        new StringOps(s).take(10) match {
          case "String    " => add(sb, s); end(sb)
          case _            => add(sb, s)
        }

      case "a" => (sb: StringBuffer, row: jList[AnyRef]) =>
        add(sb, row.get(i).toString)
        end(sb)

      // e, t, tx
      case _ => (sb: StringBuffer, row: jList[AnyRef]) =>
        add(sb, row.get(i).toString)
    }
  }

  def getRow2Packed: (StringBuffer, jList[AnyRef]) => StringBuffer = {
    attrs.size match {
      case 1 =>
        val pack0 = pack(0, attrs.head)
        (sb: StringBuffer, row: jList[AnyRef]) =>
          pack0(sb, row)

      case 2 =>
        val pack0 = pack(0, attrs.head)
        val pack1 = pack(1, attrs.head)
        (sb: StringBuffer, row: jList[AnyRef]) =>
          pack1(pack0(sb, row), row)

      case 3 =>
        val pack0 = pack(0, attrs.head)
        val pack1 = pack(1, attrs(1))
        val pack2 = pack(2, attrs(2))
        (sb: StringBuffer, row: jList[AnyRef]) =>
          pack2(pack1(pack0(sb, row), row), row)

      case 4 =>
        val pack0 = pack(0, attrs.head)
        val pack1 = pack(1, attrs(1))
        val pack2 = pack(2, attrs(2))
        val pack3 = pack(3, attrs(3))
        (sb: StringBuffer, row: jList[AnyRef]) =>
          pack3(pack2(pack1(pack0(sb, row), row), row), row)

      case 5 =>
        val pack0 = pack(0, attrs.head)
        val pack1 = pack(1, attrs(1))
        val pack2 = pack(2, attrs(2))
        val pack3 = pack(3, attrs(3))
        val pack4 = pack(4, attrs(4))
        (sb: StringBuffer, row: jList[AnyRef]) =>
          pack4(pack3(pack2(pack1(pack0(sb, row), row), row), row), row)

      case 6 =>
        val pack0 = pack(0, attrs.head)
        val pack1 = pack(1, attrs(1))
        val pack2 = pack(2, attrs(2))
        val pack3 = pack(3, attrs(3))
        val pack4 = pack(4, attrs(4))
        val pack5 = pack(5, attrs(5))
        (sb: StringBuffer, row: jList[AnyRef]) =>
          pack5(pack4(pack3(pack2(pack1(pack0(sb, row), row), row), row), row), row)

      case 7 =>
        val pack0 = pack(0, attrs.head)
        val pack1 = pack(1, attrs(1))
        val pack2 = pack(2, attrs(2))
        val pack3 = pack(3, attrs(3))
        val pack4 = pack(4, attrs(4))
        val pack5 = pack(5, attrs(5))
        val pack6 = pack(6, attrs(6))
        (sb: StringBuffer, row: jList[AnyRef]) =>
          pack6(pack5(pack4(pack3(pack2(pack1(pack0(sb, row), row), row), row), row), row), row)
    }
  }

  override def compare(x: jList[AnyRef], y: jList[AnyRef]): Int = {
    sorter(x, y)
  }

  def sortRows(rows: util.ArrayList[jList[AnyRef]]): jList[jList[AnyRef]] = {
    rows.sort(this)
    rows
  }

  // Maximum 5 sort markers
  val sorter: (jList[AnyRef], jList[AnyRef]) => Int = {
    val topSorts = sortCoordinates.head
    topSorts.size match {
      case 1 => getSorter(topSorts.head)

      case 2 =>
        val sort0 = getSorter(topSorts.head)
        val sort1 = getSorter(topSorts(1))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort0(x, y)
          if (result == 0) result = sort1(x, y)
          result
        }

      case 3 =>
        val sort0 = getSorter(topSorts.head)
        val sort1 = getSorter(topSorts(1))
        val sort2 = getSorter(topSorts(2))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort0(x, y)
          if (result == 0) result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          result
        }

      case 4 =>
        val sort0 = getSorter(topSorts.head)
        val sort1 = getSorter(topSorts(1))
        val sort2 = getSorter(topSorts(2))
        val sort3 = getSorter(topSorts(3))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort0(x, y)
          if (result == 0) result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          result
        }

      case 5 =>
        val sort0 = getSorter(topSorts.head)
        val sort1 = getSorter(topSorts(1))
        val sort2 = getSorter(topSorts(2))
        val sort3 = getSorter(topSorts(3))
        val sort4 = getSorter(topSorts(4))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort0(x, y)
          if (result == 0) result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          result
        }

      case 6 =>
        val sort0 = getSorter(topSorts.head)
        val sort1 = getSorter(topSorts(1))
        val sort2 = getSorter(topSorts(2))
        val sort3 = getSorter(topSorts(3))
        val sort4 = getSorter(topSorts(4))
        val sort5 = getSorter(topSorts(5))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort0(x, y)
          if (result == 0) result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          result
        }

      case 7 =>
        val sort0 = getSorter(topSorts.head)
        val sort1 = getSorter(topSorts(1))
        val sort2 = getSorter(topSorts(2))
        val sort3 = getSorter(topSorts(3))
        val sort4 = getSorter(topSorts(4))
        val sort5 = getSorter(topSorts(5))
        val sort6 = getSorter(topSorts(6))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort0(x, y)
          if (result == 0) result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          if (result == 0) result = sort6(x, y)
          result
        }
    }
  }
}

