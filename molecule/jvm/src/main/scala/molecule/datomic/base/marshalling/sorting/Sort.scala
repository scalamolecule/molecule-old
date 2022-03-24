package molecule.datomic.base.marshalling.sorting

import java.lang.{Double => jDouble, Integer => jInteger, Long => jLong}
import java.math.{BigDecimal => jBigDec}
import java.net.URI
import java.util.{Comparator, Date, UUID, Collection => jCollection, List => jList, Map => jMap}
import clojure.lang.{Keyword, BigInt => clBigInt}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.ast.SortCoordinate


case class Sort(
  rows: jCollection[jList[AnyRef]],
  sortCoordinates: List[List[SortCoordinate]]
) extends Comparator[jList[AnyRef]] {

  val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(rows)

  def getSorter(sortCoordinate: SortCoordinate): (jList[AnyRef], jList[AnyRef]) => Int = {
    val SortCoordinate(i, asc, attr, opt, tpe, aggrFn, aggrLimit) = sortCoordinate

    def compare(
      x: jList[AnyRef],
      y: jList[AnyRef],
      compareMapValues: (jMap[_, _], jMap[_, _]) => Int
    ): Int = {
      (x.get(i), y.get(i)) match {
        case (null, null)                     => 0
        case (null, _)                        => -1
        case (_, null)                        => 1
        case (m1: jMap[_, _], m2: jMap[_, _]) => compareMapValues(m1, m2)
      }
    }

    if (opt) {
      val mapComparison = tpe match {
        case "String" =>
            (m1: jMap[_, _], m2: jMap[_, _]) =>
              m1.values.iterator.next.asInstanceOf[String].compareTo(m2.values.iterator.next.asInstanceOf[String])

        case "enum" =>
            (m1: jMap[_, _], m2: jMap[_, _]) =>
              m1.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[Keyword].getName.compareTo(
                m2.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[Keyword].getName)

        case "ref" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong].compareTo(
            m2.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong])

        case "Long" | "Int" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[jLong].compareTo(m2.values.iterator.next.asInstanceOf[jLong])

        case "Double" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[jDouble].compareTo(m2.values.iterator.next.asInstanceOf[jDouble])

        case "Boolean" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[Boolean].compareTo(m2.values.iterator.next.asInstanceOf[Boolean])

        case "Date" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[Date].compareTo(m2.values.iterator.next.asInstanceOf[Date])

        case "UUID" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[UUID].compareTo(m2.values.iterator.next.asInstanceOf[UUID])

        case "URI" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[URI].compareTo(m2.values.iterator.next.asInstanceOf[URI])

        case "BigInt" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[clBigInt].toBigInteger.compareTo(
            m2.values.iterator.next.asInstanceOf[clBigInt].toBigInteger)

        case "BigDecimal" => (m1: jMap[_, _], m2: jMap[_, _]) =>
          m1.values.iterator.next.asInstanceOf[jBigDec].compareTo(m2.values.iterator.next.asInstanceOf[jBigDec])

        case "schema" => attr match {
          case "unique$" => (m1: jMap[_, _], m2: jMap[_, _]) =>
            m1.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[Keyword].getName.compareTo(
              m2.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[Keyword].getName)

          case _ => throw MoleculeException(s"Unexpected optional schema attr: $attr")
        }
      }
      if (asc)
        (x: jList[AnyRef], y: jList[AnyRef]) => compare(x, y, mapComparison)
      else
        (x: jList[AnyRef], y: jList[AnyRef]) => compare(y, x, mapComparison)

    } else {

      def compareType: (jList[AnyRef], jList[AnyRef]) => Int = tpe match {
        case "String" | "enum" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[String].compareTo(y.get(i).asInstanceOf[String])

        case "Long" | "ref" | "Int" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[jLong].compareTo(y.get(i).asInstanceOf[jLong])

        case "Double" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[jDouble].compareTo(y.get(i).asInstanceOf[jDouble])

        case "Boolean" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[Boolean].compareTo(y.get(i).asInstanceOf[Boolean])

        case "Date" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[Date].compareTo(y.get(i).asInstanceOf[Date])

        case "UUID" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[UUID].compareTo(y.get(i).asInstanceOf[UUID])

        case "URI" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[URI].compareTo(y.get(i).asInstanceOf[URI])

        case "BigInt" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[clBigInt].toBigInteger.compareTo(y.get(i).asInstanceOf[clBigInt].toBigInteger)

        case "BigDecimal" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[jBigDec].compareTo(y.get(i).asInstanceOf[jBigDec])

        case "Integer" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[jInteger].compareTo(y.get(i).asInstanceOf[jInteger])

        case "Any" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).toString.compareTo(y.get(i).toString)

        case "schema" => attr match {
          case "unique" => (x: jList[AnyRef], y: jList[AnyRef]) =>
            x.get(i).asInstanceOf[String].compareTo(y.get(i).asInstanceOf[String])

          case _ => throw MoleculeException(s"Unexpected mandatory schema attr: $attr")
        }
      }

      val rowComparison = if (aggrFn.isEmpty) {
        compareType
      } else {
        (aggrFn, aggrLimit) match {
          case ("min" | "max" | "distinct" | "rand" | "sample", Some(limit)) => throw MoleculeException(
            s"Unexpectedly trying to sort aggregate with applied limit. Found: $attr($aggrFn($limit)).")

          case ("count" | "count-distinct", _) => (x: jList[AnyRef], y: jList[AnyRef]) =>
            x.get(i).asInstanceOf[jInteger].compareTo(y.get(i).asInstanceOf[jInteger])

          case ("avg" | "stddev" | "variance", _) => (x: jList[AnyRef], y: jList[AnyRef]) =>
            x.get(i).asInstanceOf[jDouble].compareTo(y.get(i).asInstanceOf[jDouble])

          case _ => compareType // min, max, rand, sample, sum, median
        }
      }

      if (asc)
        (x: jList[AnyRef], y: jList[AnyRef]) => rowComparison(x, y)
      else
        (x: jList[AnyRef], y: jList[AnyRef]) => rowComparison(y, x)
    }
  }

  val sorter: (jList[AnyRef], jList[AnyRef]) => Int = {
    val topSorts = sortCoordinates.head
    topSorts.size match {
      case 1 => getSorter(topSorts.head)

      case 2 =>
        val sort1 = getSorter(topSorts(0))
        val sort2 = getSorter(topSorts(1))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          result
        }

      case 3 =>
        val sort1 = getSorter(topSorts(0))
        val sort2 = getSorter(topSorts(1))
        val sort3 = getSorter(topSorts(2))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          result
        }

      case 4 =>
        val sort1 = getSorter(topSorts(0))
        val sort2 = getSorter(topSorts(1))
        val sort3 = getSorter(topSorts(2))
        val sort4 = getSorter(topSorts(3))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          result
        }

      case 5 =>
        val sort1 = getSorter(topSorts(0))
        val sort2 = getSorter(topSorts(1))
        val sort3 = getSorter(topSorts(2))
        val sort4 = getSorter(topSorts(3))
        val sort5 = getSorter(topSorts(4))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          result
        }

      case 6 =>
        val sort1 = getSorter(topSorts(0))
        val sort2 = getSorter(topSorts(1))
        val sort3 = getSorter(topSorts(2))
        val sort4 = getSorter(topSorts(3))
        val sort5 = getSorter(topSorts(4))
        val sort6 = getSorter(topSorts(5))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          if (result == 0) result = sort6(x, y)
          result
        }

      case 7 =>
        val sort1 = getSorter(topSorts(0))
        val sort2 = getSorter(topSorts(1))
        val sort3 = getSorter(topSorts(2))
        val sort4 = getSorter(topSorts(3))
        val sort5 = getSorter(topSorts(4))
        val sort6 = getSorter(topSorts(5))
        val sort7 = getSorter(topSorts(6))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          if (result == 0) result = sort6(x, y)
          if (result == 0) result = sort7(x, y)
          result
        }

      case 8 =>
        val sort1 = getSorter(topSorts(0))
        val sort2 = getSorter(topSorts(1))
        val sort3 = getSorter(topSorts(2))
        val sort4 = getSorter(topSorts(3))
        val sort5 = getSorter(topSorts(4))
        val sort6 = getSorter(topSorts(5))
        val sort7 = getSorter(topSorts(6))
        val sort8 = getSorter(topSorts(7))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          if (result == 0) result = sort6(x, y)
          if (result == 0) result = sort7(x, y)
          if (result == 0) result = sort8(x, y)
          result
        }

      case 9 =>
        val sort1 = getSorter(topSorts(0))
        val sort2 = getSorter(topSorts(1))
        val sort3 = getSorter(topSorts(2))
        val sort4 = getSorter(topSorts(3))
        val sort5 = getSorter(topSorts(4))
        val sort6 = getSorter(topSorts(5))
        val sort7 = getSorter(topSorts(6))
        val sort8 = getSorter(topSorts(7))
        val sort9 = getSorter(topSorts(8))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          if (result == 0) result = sort6(x, y)
          if (result == 0) result = sort7(x, y)
          if (result == 0) result = sort8(x, y)
          if (result == 0) result = sort9(x, y)
          result
        }

      case 10 =>
        val sort1  = getSorter(topSorts(0))
        val sort2  = getSorter(topSorts(1))
        val sort3  = getSorter(topSorts(2))
        val sort4  = getSorter(topSorts(3))
        val sort5  = getSorter(topSorts(4))
        val sort6  = getSorter(topSorts(5))
        val sort7  = getSorter(topSorts(6))
        val sort8  = getSorter(topSorts(7))
        val sort9  = getSorter(topSorts(8))
        val sort10 = getSorter(topSorts(9))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          if (result == 0) result = sort6(x, y)
          if (result == 0) result = sort7(x, y)
          if (result == 0) result = sort8(x, y)
          if (result == 0) result = sort9(x, y)
          if (result == 0) result = sort10(x, y)
          result
        }

      case 11 =>
        val sort1  = getSorter(topSorts(0))
        val sort2  = getSorter(topSorts(1))
        val sort3  = getSorter(topSorts(2))
        val sort4  = getSorter(topSorts(3))
        val sort5  = getSorter(topSorts(4))
        val sort6  = getSorter(topSorts(5))
        val sort7  = getSorter(topSorts(6))
        val sort8  = getSorter(topSorts(7))
        val sort9  = getSorter(topSorts(8))
        val sort10 = getSorter(topSorts(9))
        val sort11 = getSorter(topSorts(10))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          if (result == 0) result = sort6(x, y)
          if (result == 0) result = sort7(x, y)
          if (result == 0) result = sort8(x, y)
          if (result == 0) result = sort9(x, y)
          if (result == 0) result = sort10(x, y)
          if (result == 0) result = sort11(x, y)
          result
        }

      // Max of 7 nested levels and 5 sort markers on top level need 12 sort comparators
      case 12 =>
        val sort1  = getSorter(topSorts(0))
        val sort2  = getSorter(topSorts(1))
        val sort3  = getSorter(topSorts(2))
        val sort4  = getSorter(topSorts(3))
        val sort5  = getSorter(topSorts(4))
        val sort6  = getSorter(topSorts(5))
        val sort7  = getSorter(topSorts(6))
        val sort8  = getSorter(topSorts(7))
        val sort9  = getSorter(topSorts(8))
        val sort10 = getSorter(topSorts(9))
        val sort11 = getSorter(topSorts(10))
        val sort12 = getSorter(topSorts(11))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          if (result == 0) result = sort6(x, y)
          if (result == 0) result = sort7(x, y)
          if (result == 0) result = sort8(x, y)
          if (result == 0) result = sort9(x, y)
          if (result == 0) result = sort10(x, y)
          if (result == 0) result = sort11(x, y)
          if (result == 0) result = sort12(x, y)
          result
        }
    }
  }

  override def compare(x: jList[AnyRef], y: jList[AnyRef]): Int = {
    sorter(x, y)
  }

  def get: jCollection[jList[AnyRef]] = {
    sortedRows.sort(this)
    sortedRows
  }
}

