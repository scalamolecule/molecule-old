package molecule.core.macros.rowAttr

import java.lang.{Double => jDouble, Long => jLong}
import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
import java.net.URI
import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.util.Helpers


trait CastOptNested extends Helpers {

  // One =======================================================================

  protected def castOptNestedOneInt(it: jIterator[_]): Int =
    it.next.toString.toInt

  protected def castOptNestedOneBigInt(it: jIterator[_]): BigInt =
    BigInt(it.next.toString)

  protected def castOptNestedOneBigDecimal(it: jIterator[_]): BigDecimal =
    BigDecimal(it.next.asInstanceOf[jBigDec].toString)

  protected def castOptNestedOne[T](it: jIterator[_]): T =
    it.next.asInstanceOf[T]

  protected def castOptNestedOneEnum(it: jIterator[_]): String = {
    it.next match {
      case m: jMap[_, _] => getKwName(m.values.iterator.next.toString)
      case v             => getKwName(v.toString)
    }
  }

  protected def castOptNestedOneRefAttr(it: jIterator[_]): Long = {
    it.next match {
      case l: jLong => l.toLong
      case vs       => vs.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong].toLong
    }
  }


  // Many ======================================================================

  protected def castOptNestedManyInt(it: jIterator[_]): Set[Int] = {
    it.next match {
      case vs: jSet[_] =>
        val it1 = vs.iterator
        var set = Set.empty[Int]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toInt
        set

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Int]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toInt
        set
    }
  }

  protected def castOptNestedManyBigInt(it: jIterator[_]): Set[BigInt] = {
    it.next match {
      case vs: jSet[_] =>
        val it1 = vs.iterator
        var set = Set.empty[BigInt]
        while (it1.hasNext)
          set += BigInt(it1.next.toString)
        set

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigInt]
        while (it1.hasNext)
          set += BigInt(it1.next.toString)
        set
    }
  }

  protected def castOptNestedManyBigDecimal(it: jIterator[_]): Set[BigDecimal] = {
    it.next match {
      case vs: jSet[_] =>
        val it1 = vs.iterator
        var set = Set.empty[BigDecimal]
        while (it1.hasNext)
          set += BigDecimal(it1.next.asInstanceOf[java.math.BigDecimal].toString)
        set

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigDecimal]
        while (it1.hasNext)
          set += BigDecimal(it1.next.asInstanceOf[java.math.BigDecimal].toString)
        set
    }
  }

  protected def castOptNestedMany[T](it: jIterator[_]): Set[T] = {
    it.next match {
      case vs: jSet[_] =>
        val it1 = vs.iterator
        var set = Set.empty[T]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[T]
        set

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[T]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[T]
        set
    }
  }

  protected def castOptNestedManyEnum(it: jIterator[_]): Set[String] = {
    it.next match {
      case vs: jSet[_] =>
        val it1 = vs.iterator
        var set = Set.empty[String]
        while (it1.hasNext)
          set += getKwName(it1.next.toString)
        set

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[String]
        while (it1.hasNext)
          set += getKwName(it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)
        set
    }
  }

  protected def castOptNestedManyRefAttr(it: jIterator[_]): Set[Long] = {
    it.next match {
      case vs: jSet[_] =>
        val it1 = vs.iterator
        var set = Set.empty[Long]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toLong
        set

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Long]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong].toLong
        set
    }
  }


  // Optional card one =========================================================

  protected def castOptNestedOptOneInt(it: jIterator[_]): Option[Int] =
    it.next match {
      case null | "__none__" => Option.empty[Int]
      case v: jMap[_, _]     => Some(v.values.iterator.next.asInstanceOf[jLong].toInt)
      case v                 => Some(v.asInstanceOf[jLong].toInt)
    }

  protected def castOptNestedOptOneLong(it: jIterator[_]): Option[Long] =
    it.next match {
      case null | "__none__" => Option.empty[Long]
      case v: jMap[_, _]     => Some(v.values.iterator.next.asInstanceOf[jLong].toLong)
      case v                 => Some(v.asInstanceOf[jLong].toLong)
    }

  protected def castOptNestedOptOneDouble(it: jIterator[_]): Option[Double] =
    it.next match {
      case null | "__none__" => Option.empty[Double]
      case v: jMap[_, _]     => Some(v.values.iterator.next.asInstanceOf[jDouble].toDouble)
      case v                 => Some(v.asInstanceOf[jDouble].toDouble)
    }

  protected def castOptNestedOptOneURI(it: jIterator[_]): Option[URI] = it.next match {
    case null | "__none__" => Option.empty[URI]
    case v: jMap[_, _]     => v.values.iterator.next match {
      case uri: URI => Some(uri)
      case uriImpl  => Some(new URI(uriImpl.toString))
    }
    case v                 => Some(v.asInstanceOf[URI])
  }

  protected def castOptNestedOptOneBigInt(it: jIterator[_]): Option[BigInt] =
    it.next match {
      case null | "__none__" => Option.empty[BigInt]
      case v: jMap[_, _]     => Some(BigInt(v.values.iterator.next.toString))
      case v                 => Some(BigInt(v.asInstanceOf[jBigInt].toString))
    }

  protected def castOptNestedOptOneBigDecimal(it: jIterator[_]): Option[BigDecimal] =
    it.next match {
      case null | "__none__" => Option.empty[BigDecimal]
      case v: jMap[_, _]     => Some(BigDecimal(v.values.iterator.next.toString))
      case v                 => Some(BigDecimal(v.asInstanceOf[jBigDec].toString))
    }

  protected def castOptNestedOptOne[T](it: jIterator[_]): Option[T] =
    it.next match {
      case null | "__none__" => Option.empty[T]
      case v: jMap[_, _]     => Some(v.values.iterator.next.asInstanceOf[T])
      case v                 => Some(v.asInstanceOf[T])
    }

  protected def castOptNestedOptOneEnum(it: jIterator[_]): Option[String] =
    it.next match {
      case null | "__none__" => Option.empty[String]
      case vs                => vs.asInstanceOf[jMap[_, _]].values.iterator.next match {
        case m: jMap[_, _] => Some(getKwName(m.values.iterator.next.toString))
        case v             => Some(getKwName(v.toString))
      }
    }

  protected def castOptNestedOptOneRefAttr(it: jIterator[_]): Option[Long] =
    it.next match {
      case null | "__none__" => Option.empty[Long]
      case l: jLong          => Some(l.toLong)
      case vs                => vs.asInstanceOf[jMap[_, _]].values.iterator.next match {
        case l: jLong => Some(l.toLong)
        case m        => Some(m.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jLong].toLong)
      }
      //        Some(vs
      //          .asInstanceOf[jMap[_, _]].values.iterator.next
      //          .asInstanceOf[jMap[_, _]].values.iterator.next
      //          .asInstanceOf[jLong].toLong
      //        )
    }


  // Optional card many ========================================================

  protected def castOptNestedOptManyInt(it: jIterator[_]): Option[Set[Int]] =
    it.next match {
      case null | "__none__" => Option.empty[Set[Int]]
      case vs: jMap[_, _]    =>
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var set = Set.empty[Int]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toInt
        Some(set)

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Int]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toInt
        Some(set)
    }

  protected def castOptNestedOptManyLong(it: jIterator[_]): Option[Set[Long]] =
    it.next match {
      case null | "__none__" => Option.empty[Set[Long]]
      case vs: jMap[_, _]    =>
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var set = Set.empty[Long]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toLong
        Some(set)

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Long]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toLong
        Some(set)
    }

  protected def castOptNestedOptManyDouble(it: jIterator[_]): Option[Set[Double]] =
    it.next match {
      case null | "__none__" => Option.empty[Set[Double]]
      case vs: jMap[_, _]    =>
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var set = Set.empty[Double]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jDouble].toDouble
        Some(set)

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Double]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jDouble].toDouble
        Some(set)
    }

  protected def castOptNestedOptManyURI(it: jIterator[_]): Option[Set[URI]] = it.next match {
    case null | "__none__" => Option.empty[Set[URI]]
    case vs: jMap[_, _]    =>
      val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[URI]
      while (it1.hasNext)
        set += (it1.next match {
          case uri: URI => uri
          case uriImpl  => new URI(uriImpl.toString)
        })
      Some(set)

    case vs =>
      val it1 = vs.asInstanceOf[jList[_]].iterator
      var set = Set.empty[URI]
      while (it1.hasNext)
        set += (it1.next match {
          case uri: URI => uri
          case uriImpl  => new URI(uriImpl.toString)
        })
      Some(set)
  }

  protected def castOptNestedOptManyBigInt(it: jIterator[_]): Option[Set[BigInt]] =
    it.next match {
      case null | "__none__" => Option.empty[Set[BigInt]]
      case vs: jMap[_, _]    =>
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var set = Set.empty[BigInt]
        while (it1.hasNext)
          set += BigInt(it1.next.toString)
        Some(set)

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigInt]
        while (it1.hasNext)
          set += BigInt(it1.next.asInstanceOf[jBigInt].toString)
        Some(set)
    }

  protected def castOptNestedOptManyBigDecimal(it: jIterator[_]): Option[Set[BigDecimal]] =
    it.next match {
      case null | "__none__" => Option.empty[Set[BigDecimal]]
      case vs: jMap[_, _]    =>
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var set = Set.empty[BigDecimal]
        while (it1.hasNext)
          set += BigDecimal(it1.next.toString)
        Some(set)

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigDecimal]
        while (it1.hasNext)
          set += BigDecimal(it1.next.asInstanceOf[jBigDec].toString)
        Some(set)
    }

  protected def castOptNestedOptMany[T](it: jIterator[_]): Option[Set[T]] =
    it.next match {
      case null | "__none__" => Option.empty[Set[T]]
      case vs: jMap[_, _]    =>
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var set = Set.empty[T]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[T]
        Some(set)

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[T]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[T]
        Some(set)
    }

  protected def castOptNestedOptManyEnum(it: jIterator[_]): Option[Set[String]] =
    it.next match {
      case null | "__none__" => Option.empty[Set[String]]
      case vs: jMap[_, _]    =>
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var set = Set.empty[String]
        while (it1.hasNext)
          set += getKwName(it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)
        Some(set)

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[String]
        while (it1.hasNext)
          set += getKwName(it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)
        Some(set)
    }

  protected def castOptNestedOptManyRefAttr(it: jIterator[_]): Option[Set[Long]] =
    it.next match {
      case null | "__none__" => Option.empty[Set[Long]]
      case vs: jMap[_, _]    =>
        val it1 = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var set = Set.empty[Long]
        // Hack to avoid looking up map by clojure Keyword - there must be a better way...
        while (it1.hasNext) {
          var done = false
          it1.next.asInstanceOf[jMap[_, _]].forEach {
            case _ if done                        =>
            case (k, v) if k.toString == ":db/id" => done = true; set += v.asInstanceOf[jLong].toLong
            case _                                =>
          }
        }
        Some(set)

      case vs =>
        val it1 = vs.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Long]
        while (it1.hasNext)
          set += it1.next
            .asInstanceOf[jMap[_, _]].values.iterator.next
            .asInstanceOf[jLong].toLong
        Some(set)
    }


  // Map =======================================================================

  protected def castOptNestedMapString(it: jIterator[_]): Map[String, String] = {
    it.next match {
      case s: jSet[_] =>
        val it1  = s.iterator
        var map  = Map.empty[String, String]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1))
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, String]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1))
        }
        map
    }
  }

  protected def castOptNestedMapInt(it: jIterator[_]): Map[String, Int] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, Int]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toInt)
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Int]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toInt)
        }
        map
    }
  }

  protected def castOptNestedMapBoolean(it: jIterator[_]): Map[String, Boolean] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, Boolean]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toBoolean)
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Boolean]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toBoolean)
        }
        map
    }
  }

  protected def castOptNestedMapLong(it: jIterator[_]): Map[String, Long] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, Long]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toLong)
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Long]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toLong)
        }
        map
    }
  }

  protected def castOptNestedMapDouble(it: jIterator[_]): Map[String, Double] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, Double]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toDouble)
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Double]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toDouble)
        }
        map
    }
  }

  protected def castOptNestedMapDate(it: jIterator[_]): Map[String, Date] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, Date]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> str2date(pair(1)))
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Date]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> str2date(pair(1)))
        }
        map
    }
  }

  protected def castOptNestedMapUUID(it: jIterator[_]): Map[String, UUID] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, UUID]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> UUID.fromString(pair(1)))
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, UUID]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> UUID.fromString(pair(1)))
        }
        map
    }
  }

  protected def castOptNestedMapURI(it: jIterator[_]): Map[String, URI] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, URI]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> new URI(pair(1)))
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, URI]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> new URI(pair(1)))
        }
        map
    }
  }

  protected def castOptNestedMapBigInt(it: jIterator[_]): Map[String, BigInt] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, BigInt]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> BigInt(pair(1).toString))
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, BigInt]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> BigInt(pair(1).toString))
        }
        map
    }
  }

  protected def castOptNestedMapBigDecimal(it: jIterator[_]): Map[String, BigDecimal] = {
    it.next match {
      case vs: jSet[_] =>
        val it1  = vs.iterator
        var map  = Map.empty[String, BigDecimal]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> BigDecimal(pair(1).toString))
        }
        map

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, BigDecimal]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> BigDecimal(pair(1).toString))
        }
        map
    }
  }


  // Optional Map ==============================================================

  protected def castOptNestedOptMapString(it: jIterator[_]): Option[Map[String, String]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, String]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, String]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1))
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, String]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1))
        }
        Some(map)
    }

  protected def castOptNestedOptMapInt(it: jIterator[_]): Option[Map[String, Int]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, Int]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, Int]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toInt)
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Int]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toInt)
        }
        Some(map)
    }

  protected def castOptNestedOptMapLong(it: jIterator[_]): Option[Map[String, Long]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, Long]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, Long]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toLong)
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Long]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toLong)
        }
        Some(map)
    }

  protected def castOptNestedOptMapDouble(it: jIterator[_]): Option[Map[String, Double]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, Double]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, Double]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toDouble)
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Double]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toDouble)
        }
        Some(map)
    }

  protected def castOptNestedOptMapBoolean(it: jIterator[_]): Option[Map[String, Boolean]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, Boolean]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, Boolean]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toBoolean)
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Boolean]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> pair(1).toBoolean)
        }
        Some(map)
    }

  protected def castOptNestedOptMapDate(it: jIterator[_]): Option[Map[String, Date]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, Date]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, Date]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> str2date(pair(1)))
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, Date]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> str2date(pair(1)))
        }
        Some(map)
    }

  protected def castOptNestedOptMapUUID(it: jIterator[_]): Option[Map[String, UUID]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, UUID]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, UUID]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> UUID.fromString(pair(1)))
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, UUID]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> UUID.fromString(pair(1)))
        }
        Some(map)
    }

  protected def castOptNestedOptMapURI(it: jIterator[_]): Option[Map[String, URI]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, URI]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, URI]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> new URI(pair(1)))
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, URI]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> new URI(pair(1)))
        }
        Some(map)
    }

  protected def castOptNestedOptMapBigInt(it: jIterator[_]): Option[Map[String, BigInt]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, BigInt]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, BigInt]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> BigInt(pair(1)))
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, BigInt]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> BigInt(pair(1)))
        }
        Some(map)
    }

  protected def castOptNestedOptMapBigDecimal(it: jIterator[_]): Option[Map[String, BigDecimal]] =
    it.next match {
      case null | "__none__" => Option.empty[Map[String, BigDecimal]]
      case vs: jMap[_, _]    =>
        val it   = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        var map  = Map.empty[String, BigDecimal]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map += (pair(0) -> BigDecimal(pair(1)))
        }
        Some(map)

      case vs =>
        val it1  = vs.asInstanceOf[jList[_]].iterator
        var map  = Map.empty[String, BigDecimal]
        var pair = new Array[String](2)
        while (it1.hasNext) {
          pair = it1.next.toString.split("@", 2)
          map += (pair(0) -> BigDecimal(pair(1)))
        }
        Some(map)
    }
}
