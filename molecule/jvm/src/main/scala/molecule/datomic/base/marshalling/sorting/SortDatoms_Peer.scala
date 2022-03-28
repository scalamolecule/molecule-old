package molecule.datomic.base.marshalling.sorting

import java.lang.{Double => jDouble, Integer => jInteger, Long => jLong, Iterable => jIterable}
import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
import java.net.URI
import java.util.{Comparator, Date, UUID, Collection => jCollection, List => jList, Map => jMap}
import clojure.lang.{Keyword, BigInt => clBigInt}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.ast.SortCoordinate
import datomic.{Util, Database => PeerDb, Datom => PeerDatom}
import datomicClient.ClojureBridge
import datomicScala.client.api.{Datom => ClientDatom}

case class SortDatoms_Peer(
  datoms: jIterable[PeerDatom],
  sortCoordinates: List[List[SortCoordinate]]
) extends Comparator[PeerDatom] {

  val sortedRows: java.util.ArrayList[PeerDatom] = {
    val sorted = new java.util.ArrayList[PeerDatom]()
    datoms.forEach(datom => sorted.add(datom))
    sorted
  }

  def getSorter(sortCoordinate: SortCoordinate): (PeerDatom, PeerDatom) => Int = {
    val SortCoordinate(i, asc, attr, _, tpe, aggrFn, aggrLimit) = sortCoordinate

    def compareType: (PeerDatom, PeerDatom) => Int = tpe match {
      case "String" | "enum" => (x: PeerDatom, y: PeerDatom) =>
        x.get(i).asInstanceOf[String].compareTo(y.get(i).asInstanceOf[String])

      case "Long" | "ref" | "Int" => (x: PeerDatom, y: PeerDatom) =>
        x.get(i).asInstanceOf[jLong].compareTo(y.get(i).asInstanceOf[jLong])

      case "Double" => (x: PeerDatom, y: PeerDatom) =>
        x.get(i).asInstanceOf[jDouble].compareTo(y.get(i).asInstanceOf[jDouble])

      case "Boolean" => (x: PeerDatom, y: PeerDatom) =>
        x.get(i).asInstanceOf[Boolean].compareTo(y.get(i).asInstanceOf[Boolean])

      case "Date" => (x: PeerDatom, y: PeerDatom) =>
        x.get(i).asInstanceOf[Date].compareTo(y.get(i).asInstanceOf[Date])

      case "UUID" => (x: PeerDatom, y: PeerDatom) =>
        x.get(i).asInstanceOf[UUID].compareTo(y.get(i).asInstanceOf[UUID])

      case "URI" => (x: PeerDatom, y: PeerDatom) =>
        x.get(i).asInstanceOf[URI].compareTo(y.get(i).asInstanceOf[URI])


        case _ => throw MoleculeException(s"Unexpected mandatory schema attr: $attr")

    }

    val comparison = if (aggrFn.isEmpty) {
      compareType
    } else {
      (aggrFn, aggrLimit) match {
        case ("min" | "max" | "distinct" | "rand" | "sample", Some(limit)) => throw MoleculeException(
          s"Unexpectedly trying to sort aggregate with applied limit. Found: $attr($aggrFn($limit)).")

        case ("count" | "count-distinct", _) => (x: PeerDatom, y: PeerDatom) =>
          x.get(i).asInstanceOf[jInteger].compareTo(y.get(i).asInstanceOf[jInteger])

        case ("avg" | "stddev" | "variance", _) => (x: PeerDatom, y: PeerDatom) =>
          x.get(i).asInstanceOf[jDouble].compareTo(y.get(i).asInstanceOf[jDouble])

        case _ => compareType // min, max, rand, sample, sum, median
      }
    }

    if (asc)
      (x: PeerDatom, y: PeerDatom) => comparison(x, y)
    else
      (x: PeerDatom, y: PeerDatom) => comparison(y, x)

  }


  override def compare(x: PeerDatom, y: PeerDatom): Int = {
    sorter(x, y)
  }

  def get: jList[PeerDatom] = {
    sortedRows.sort(this)
    sortedRows
  }

  // Up to maximum of 7 sub levels (8 including top level) * 5 sort markers per level = 40
  val sorter: (PeerDatom, PeerDatom) => Int = {
    val topSorts = sortCoordinates.head
    topSorts.size match {
      case 1 => getSorter(topSorts.head)

      case 2 =>
        val sort0 = getSorter(topSorts.head)
        val sort1 = getSorter(topSorts(1))
        (x: PeerDatom, y: PeerDatom) => {
          var result = sort0(x, y)
          if (result == 0) result = sort1(x, y)
          result
        }

      //      case 3 =>
      //        val sort0 = getSorter(topSorts.head)
      //        val sort1 = getSorter(topSorts(1))
      //        val sort2 = getSorter(topSorts(2))
      //        (x: PeerDatom, y: PeerDatom) => {
      //          var result = sort0(x, y)
      //          if (result == 0) result = sort1(x, y)
      //          if (result == 0) result = sort2(x, y)
      //          result
      //        }
      //
      //      case 4 =>
      //        val sort0 = getSorter(topSorts.head)
      //        val sort1 = getSorter(topSorts(1))
      //        val sort2 = getSorter(topSorts(2))
      //        val sort3 = getSorter(topSorts(3))
      //        (x: PeerDatom, y: PeerDatom) => {
      //          var result = sort0(x, y)
      //          if (result == 0) result = sort1(x, y)
      //          if (result == 0) result = sort2(x, y)
      //          if (result == 0) result = sort3(x, y)
      //          result
      //        }
      //
      //      case 5 =>
      //        val sort0 = getSorter(topSorts.head)
      //        val sort1 = getSorter(topSorts(1))
      //        val sort2 = getSorter(topSorts(2))
      //        val sort3 = getSorter(topSorts(3))
      //        val sort4 = getSorter(topSorts(4))
      //        (x: PeerDatom, y: PeerDatom) => {
      //          var result = sort0(x, y)
      //          if (result == 0) result = sort1(x, y)
      //          if (result == 0) result = sort2(x, y)
      //          if (result == 0) result = sort3(x, y)
      //          if (result == 0) result = sort4(x, y)
      //          result
      //        }
      //
      //      case 6 =>
      //        val sort0 = getSorter(topSorts.head)
      //        val sort1 = getSorter(topSorts(1))
      //        val sort2 = getSorter(topSorts(2))
      //        val sort3 = getSorter(topSorts(3))
      //        val sort4 = getSorter(topSorts(4))
      //        val sort5 = getSorter(topSorts(5))
      //        (x: PeerDatom, y: PeerDatom) => {
      //          var result = sort0(x, y)
      //          if (result == 0) result = sort1(x, y)
      //          if (result == 0) result = sort2(x, y)
      //          if (result == 0) result = sort3(x, y)
      //          if (result == 0) result = sort4(x, y)
      //          if (result == 0) result = sort5(x, y)
      //          result
      //        }
      //
      //      case 7 =>
      //        val sort0 = getSorter(topSorts.head)
      //        val sort1 = getSorter(topSorts(1))
      //        val sort2 = getSorter(topSorts(2))
      //        val sort3 = getSorter(topSorts(3))
      //        val sort4 = getSorter(topSorts(4))
      //        val sort5 = getSorter(topSorts(5))
      //        val sort6 = getSorter(topSorts(6))
      //        (x: PeerDatom, y: PeerDatom) => {
      //          var result = sort0(x, y)
      //          if (result == 0) result = sort1(x, y)
      //          if (result == 0) result = sort2(x, y)
      //          if (result == 0) result = sort3(x, y)
      //          if (result == 0) result = sort4(x, y)
      //          if (result == 0) result = sort5(x, y)
      //          if (result == 0) result = sort6(x, y)
      //          result
      //        }
    }
  }
}

