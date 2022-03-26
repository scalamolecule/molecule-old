package molecule.datomic.base.marshalling.sorting

import java.lang.{Double => jDouble, Integer => jInteger, Long => jLong}
import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
import java.net.URI
import java.util
import java.util.{Comparator, Date, UUID, Collection => jCollection, Iterator => jIterator, List => jList, Map => jMap}
import clojure.lang.{Keyword, BigInt => clBigInt}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.ast.SortCoordinate


object ExtractFlatValues {

  def getSorter(sortCoordinate: SortCoordinate): (jList[AnyRef], jList[AnyRef]) => Int = {
    val SortCoordinate(i, asc, attr, opt, tpe, aggrFn, aggrLimit) = sortCoordinate

    if (opt) {
      def compare(
        x: jList[AnyRef],
        y: jList[AnyRef],
        comparison: (AnyRef, AnyRef) => Int
      ): Int = {
        (x.get(i), y.get(i)) match {
          case ("__none__", "__none__") => 0
          case ("__none__", _)          => -1
          case (_, "__none__")          => 1
          case (a: AnyRef, b: AnyRef)   => comparison(a, b)
        }
      }

      val comparison = tpe match {
        case "String" => (a: AnyRef, b: AnyRef) => a.asInstanceOf[String].compareTo(b.asInstanceOf[String])
        case "enum"   => (a: AnyRef, b: AnyRef) =>
          a.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[Keyword].getName
            .compareTo(b.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[Keyword].getName)

        case "Long" => (a: AnyRef, b: AnyRef) => a.asInstanceOf[jLong].compareTo(b.asInstanceOf[jLong])
        case "ref"  =>
          (a: AnyRef, b: AnyRef) =>
            a.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong]
              .compareTo(b.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong])

        case "Double"     => (a: AnyRef, b: AnyRef) => a.asInstanceOf[jDouble].compareTo(b.asInstanceOf[jDouble])
        case "Boolean"    => (a: AnyRef, b: AnyRef) => a.asInstanceOf[Boolean].compareTo(b.asInstanceOf[Boolean])
        case "Date"       => (a: AnyRef, b: AnyRef) => a.asInstanceOf[Date].compareTo(b.asInstanceOf[Date])
        case "UUID"       => (a: AnyRef, b: AnyRef) => a.asInstanceOf[UUID].compareTo(b.asInstanceOf[UUID])
        case "URI"        => (a: AnyRef, b: AnyRef) => a.asInstanceOf[URI].compareTo(b.asInstanceOf[URI])
        case "BigInt"     => (a: AnyRef, b: AnyRef) =>
          (a, b) match {
            // Client
            case (a: jBigInt, b: jBigInt) => a.compareTo(b)
            // Peer
            case (a: clBigInt, b: clBigInt) => a.toBigInteger.compareTo(b.toBigInteger)
          }
        case "BigDecimal" => (a: AnyRef, b: AnyRef) => a.asInstanceOf[jBigDec].compareTo(b.asInstanceOf[jBigDec])
      }
      if (asc)
        (x: jList[AnyRef], y: jList[AnyRef]) => compare(x, y, comparison)
      else
        (x: jList[AnyRef], y: jList[AnyRef]) => compare(y, x, comparison)

    } else {

      def compareType: (jList[AnyRef], jList[AnyRef]) => Int = tpe match {
        case "String" =>
          (x: jList[AnyRef], y: jList[AnyRef]) =>
            (x.get(i), y.get(i)) match {
              case (s1: String, s2: String)         => s1.compareTo(s2)
              case (m1: jMap[_, _], m2: jMap[_, _]) =>
                (m1.values.iterator.next, m2.values.iterator.next) match {
                  case (s1: String, s2: String)         => s1.compareTo(s2)
                  case (m1: jMap[_, _], m2: jMap[_, _]) =>
                    m1.values.iterator.next.asInstanceOf[String]
                      .compareTo(m2.values.iterator.next.asInstanceOf[String])
                }
            }

        case "enum" =>
          (x: jList[AnyRef], y: jList[AnyRef]) =>
            (x.get(i), y.get(i)) match {
              case (s1: Keyword, s2: Keyword)       => s1.getName.compareTo(s2.getName)
              case (m1: jMap[_, _], m2: jMap[_, _]) =>
                (m1.values.iterator.next, m2.values.iterator.next) match {
                  case (s1: Keyword, s2: Keyword)       => s1.getName.compareTo(s2.getName)
                  case (m1: jMap[_, _], m2: jMap[_, _]) =>
                    m1.values.iterator.next.asInstanceOf[Keyword].getName
                      .compareTo(m2.values.iterator.next.asInstanceOf[Keyword].getName)
                }
            }

        case "Long" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[jLong].compareTo(y.get(i).asInstanceOf[jLong])

        case "ref" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong]
            .compareTo(y.get(i).asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong])

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
          (x.get(i), y.get(i)) match {
            case (a: jBigInt, b: jBigInt)   => a.compareTo(b)
            case (a: clBigInt, b: clBigInt) => a.toBigInteger.compareTo(b.toBigInteger)
          }

        case "BigDecimal" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[jBigDec].compareTo(y.get(i).asInstanceOf[jBigDec])

        case "Integer" => (x: jList[AnyRef], y: jList[AnyRef]) =>
          x.get(i).asInstanceOf[jInteger].compareTo(y.get(i).asInstanceOf[jInteger])
      }

      val compare = if (aggrFn.isEmpty) {
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
        (x: jList[AnyRef], y: jList[AnyRef]) => compare(x, y)
      else
        (x: jList[AnyRef], y: jList[AnyRef]) => compare(y, x)
    }
  }


  def comparator(sortCoordinates: List[SortCoordinate]): Comparator[jList[AnyRef]] = {
    sortCoordinates.size match {
      case 1 =>
        val sort1 = getSorter(sortCoordinates(0))
        (x: jList[AnyRef], y: jList[AnyRef]) => sort1(x, y)

      case 2 =>
        val sort1 = getSorter(sortCoordinates(0))
        val sort2 = getSorter(sortCoordinates(1))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          result
        }

      case 3 =>
        val sort1 = getSorter(sortCoordinates(0))
        val sort2 = getSorter(sortCoordinates(1))
        val sort3 = getSorter(sortCoordinates(2))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          result
        }

      case 4 =>
        val sort1 = getSorter(sortCoordinates(0))
        val sort2 = getSorter(sortCoordinates(1))
        val sort3 = getSorter(sortCoordinates(2))
        val sort4 = getSorter(sortCoordinates(3))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          result
        }

      case 5 =>
        val sort1 = getSorter(sortCoordinates(0))
        val sort2 = getSorter(sortCoordinates(1))
        val sort3 = getSorter(sortCoordinates(2))
        val sort4 = getSorter(sortCoordinates(3))
        val sort5 = getSorter(sortCoordinates(4))
        (x: jList[AnyRef], y: jList[AnyRef]) => {
          var result = sort1(x, y)
          if (result == 0) result = sort2(x, y)
          if (result == 0) result = sort3(x, y)
          if (result == 0) result = sort4(x, y)
          if (result == 0) result = sort5(x, y)
          result
        }
    }
  }


  def apply(
    propCount: Int,
    refIndexes: List[Int],
    tacitIndexes: List[Int],
    deeper: Boolean = false,
    sortCoordinates: List[List[SortCoordinate]] = Nil,
    level: Int = 1
  ): jCollection[_] => jIterator[_] = {
    //    println("================================")
    //    println("level           : " + level)
    //    println("propCount       : " + propCount)
    //    println("refIndexes      : " + refIndexes)
    //    println("tacitIndexes    : " + tacitIndexes)
    //    println("deeper          : " + deeper)
    //    println("sortCoordinates : " + sortCoordinates)

    val doSort = sortCoordinates.isDefinedAt(level) && sortCoordinates(level).nonEmpty

    def sort(nestedRows: jCollection[_]): util.ArrayList[jList[AnyRef]] = {
      val sortedRows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(nestedRows.size)
      if (nestedRows.size == 0) {
        sortedRows // (empty array list)
      } else {
        nestedRows.iterator.next match {
          case _: jMap[_, _] =>
            // Isolate values of maps
            nestedRows.forEach(row =>
              sortedRows.add(new java.util.ArrayList[AnyRef](row.asInstanceOf[jMap[_, AnyRef]].values))
            )
          case _: jList[_]   =>
            // Use list as is
            nestedRows.forEach(row =>
              sortedRows.add(new java.util.ArrayList[AnyRef](row.asInstanceOf[jList[AnyRef]]))
            )
        }
        if (doSort) {
          sortedRows.sort(comparator(sortCoordinates(level)))
        }
        sortedRows
      }
    }

    (refIndexes.isEmpty, tacitIndexes.isEmpty) match {
      case (true, true) =>
        (nestedRows: jCollection[_]) =>
          //          println(s"==================== A level: $level, sort: $doSort ")
          //          nestedRows.forEach(v => println(v))

          val flatValues = new java.util.ArrayList[AnyRef](nestedRows.size * propCount)
          sort(nestedRows).forEach { row =>
            if (row.size == propCount) {
              flatValues.addAll(row)
            }
          }
          //          println("------ sort(nestedRows)")
          //          sort(nestedRows).forEach(v => println(v))
          //          println("------ flatValues")
          //          flatValues.forEach(v => println(v))

          flatValues.iterator

      case (true, false) =>
        (nestedRows: jCollection[_]) =>
          //          println(s"==================== B level: $level, sort: $doSort ")
          //          nestedRows.forEach(v => println(v))

          val resolvedList            = new java.util.ArrayList[jList[AnyRef]](nestedRows.size)
          val flatValues              = new java.util.ArrayList[AnyRef](nestedRows.size * propCount)
          val nonTacitIndexes         = (0 until propCount).diff(tacitIndexes)
          var vs: jCollection[AnyRef] = null
          var valueArray              = new Array[AnyRef](propCount)
          nestedRows.forEach { row =>
            vs = row.asInstanceOf[jMap[AnyRef, AnyRef]].values
            val testList = new java.util.ArrayList[AnyRef](propCount)
            valueArray = vs.toArray
            // Skip all values on this level if some tacit value is missing
            val valid: Boolean = tacitIndexes.collectFirst {
              case i if valueArray(i) == "__none__" => true
            }.isEmpty
            if (valid) {
              // Get non-tacit values only
              nonTacitIndexes.foreach { j =>
                testList.add(valueArray(j))
              }
              if (deeper) {
                // add last
                testList.add(valueArray(vs.size - 1))
              }
              resolvedList.add(testList)
            }
          }
          //          println("------ resolvedList")
          //          resolvedList.forEach(v => println(v))
          //          println("------ sort(resolvedList)")
          //          sort(resolvedList).forEach(v => println(v))

          sort(resolvedList).forEach { row =>
            flatValues.addAll(row)
          }

          //          println("------ flatValues")
          //          flatValues.forEach(v => println(v))

          flatValues.iterator

      case (false, true) =>
        (nestedRows: jCollection[_]) =>
          //          println(s"==================== C level: $level, sort: $doSort ")
          //          nestedRows.forEach(v => println(v))

          val resolvedList            = new java.util.ArrayList[jList[AnyRef]](nestedRows.size)
          val flatValues              = new java.util.ArrayList[AnyRef](nestedRows.size * propCount)
          var vs: jCollection[AnyRef] = null
          nestedRows.forEach { row =>
            vs = row.asInstanceOf[jMap[AnyRef, AnyRef]].values
            val testList = new java.util.ArrayList[AnyRef](propCount)
            var i        = 0
            def addValues(vs: jCollection[AnyRef]): Unit = vs.forEach {
              case ref: jMap[_, _]  =>
                addValues(ref.asInstanceOf[jMap[AnyRef, AnyRef]].values)
              case "__none__"       =>
                i += 1;
                testList.add("__none__")
              case nested: jList[_] =>
                i += 1;
                testList.add(nested)
              case v                =>
                i += 1;
                testList.add(v)
            }
            addValues(vs)
            if (i == propCount) {
              resolvedList.add(testList)
            }
          }

          //          println("------ resolvedList")
          //          resolvedList.forEach(v => println(v))
          //          println("------ sort(resolvedList)")
          //          sort(resolvedList).forEach(v => println(v))

          sort(resolvedList).forEach { row =>
            flatValues.addAll(row)
          }

          //          println("------ flatValues")
          //          flatValues.forEach(v => println(v))

          flatValues.iterator

      case (false, false) =>
        (nestedRows: jCollection[_]) =>
          //          println(s"==================== D level: $level, sort: $doSort ")
          //          nestedRows.forEach(v => println(v))

          val resolvedList            = new java.util.ArrayList[jList[AnyRef]](nestedRows.size)
          val flatValues              = new java.util.ArrayList[AnyRef](nestedRows.size * propCount)
          var vs: jCollection[AnyRef] = null
          val ok                      = (presentValues: Int) => presentValues == propCount + tacitIndexes.size
          nestedRows.forEach { row =>
            vs = row.asInstanceOf[jMap[AnyRef, AnyRef]].values
            val testList      = new java.util.ArrayList[AnyRef](propCount)
            var presentValues = 0
            var i             = 0
            def addValues(vs: jCollection[AnyRef]): Unit = vs.forEach {
              case ref: jMap[_, _]                        =>
                addValues(ref.asInstanceOf[jMap[AnyRef, AnyRef]].values)
              case "__none__" if tacitIndexes.contains(i) =>
                // tacit value missing
                i += 1
              case _ if tacitIndexes.contains(i)          =>
                // tacit value exists
                i += 1
                presentValues += 1
              case v                                      =>
                i += 1
                presentValues += 1
                testList.add(v)
            }
            addValues(vs)
            val ok1 = ok(presentValues)
            if (ok1) {
              resolvedList.add(testList)
            }
          }

          //          println("------ resolvedList")
          //          resolvedList.forEach(v => println(v))
          //          println("------ sort(resolvedList)")
          //          sort(resolvedList).forEach(v => println(v))

          sort(resolvedList).forEach { row =>
            flatValues.addAll(row)
          }

          //          println("------ flatValues")
          //          flatValues.forEach(v => println(v))

          flatValues.iterator
    }
  }
}
