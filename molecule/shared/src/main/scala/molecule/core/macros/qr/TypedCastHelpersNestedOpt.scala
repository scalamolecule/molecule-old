package molecule.core.macros.qr

import java.lang.{Double => jDouble, Long => jLong}
import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
import java.net.URI
import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap}
import molecule.core.macros.attrResolvers.CastNestedOpt
import molecule.core.util.Helpers

/** Core molecule interface override defining actions that can be called on molecules.
  *
  * Generally we could often have made higher-order methods taking a casting function for
  * variating cases. But we prioritize minimizing the macro-generated code as much as possible
  * to lower compile time overhead.
  */
trait TypedCastHelpersNestedOpt extends CastNestedOpt with Helpers { 

  // card one

  protected override def castNestedOptOneInt(it: jIterator[_]): Int =
    it.next.toString.toInt

  protected override def castNestedOptOneBigInt(it: jIterator[_]): BigInt =
    BigInt(it.next.toString)

  protected override def castNestedOptOneBigDecimal(it: jIterator[_]): BigDecimal =
    BigDecimal(it.next.asInstanceOf[jBigDec].toString)

  protected override def castNestedOptOne[T](it: jIterator[_]): T =
    it.next.asInstanceOf[T]

  protected override def castNestedOptOneEnum(it: jIterator[_]): String =
    getKwName(it.next.asInstanceOf[jMap[_, _]].values().iterator().next.toString)

  protected override def castNestedOptOneRefAttr(it: jIterator[_]): Long =
    it.next
      .asInstanceOf[jMap[_, _]].values().iterator().next
      .asInstanceOf[jLong].toLong


  // Many ===========================================================================================

  protected override def castNestedOptManyInt(it: jIterator[_]): Set[Int] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var set = Set.empty[Int]
    while (it1.hasNext)
      set += it1.next.asInstanceOf[jLong].toInt
    set
  }

  protected override def castNestedOptManyBigInt(it: jIterator[_]): Set[BigInt] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigInt]
    while (it1.hasNext)
      set += BigInt(it1.next.toString)
    set
  }

  protected override def castNestedOptManyBigDecimal(it: jIterator[_]): Set[BigDecimal] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigDecimal]
    while (it1.hasNext)
      set += BigDecimal(it1.next.asInstanceOf[java.math.BigDecimal].toString)
    set
  }

  protected override def castNestedOptMany[T](it: jIterator[_]): Set[T] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var set = Set.empty[T]
    while (it1.hasNext)
      set += it1.next.asInstanceOf[T]
    set
  }

  protected override def castNestedOptManyEnum(it: jIterator[_]): Set[String] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var set = Set.empty[String]
    while (it1.hasNext)
      set += getKwName(it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)
    set
  }

  protected override def castNestedOptManyRefAttr(it: jIterator[_]): Set[Long] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var set = Set.empty[Long]
    while (it1.hasNext)
      set += it1.next
        .asInstanceOf[jMap[_, _]].values().iterator().next
        .asInstanceOf[jLong].toLong
    set
  }


  // Optional card one ===========================================================================================

  protected override def castNestedOptOptOneInt(it: jIterator[_]): Option[Int] =
    it.next match {
      case "__none__" => Option.empty[Int]
      case v          => Some(v.asInstanceOf[jLong].toInt)
    }

  protected override def castNestedOptOptOneLong(it: jIterator[_]): Option[Long] =
    it.next match {
      case "__none__" => Option.empty[Long]
      case v          => Some(v.asInstanceOf[jLong].toLong)
    }

  protected override def castNestedOptOptOneDouble(it: jIterator[_]): Option[Double] =
    it.next match {
      case "__none__" => Option.empty[Double]
      case v          => Some(v.asInstanceOf[jDouble].toDouble)
    }

  protected override def castNestedOptOptOneBigInt(it: jIterator[_]): Option[BigInt] =
    it.next match {
      case "__none__" => Option.empty[BigInt]
      case v          => Some(BigInt(v.asInstanceOf[jBigInt].toString))
    }

  protected override def castNestedOptOptOneBigDecimal(it: jIterator[_]): Option[BigDecimal] =
    it.next match {
      case "__none__" => Option.empty[BigDecimal]
      case v          => Some(BigDecimal(v.asInstanceOf[jBigDec].toString))
    }

  protected override def castNestedOptOptOne[T](it: jIterator[_]): Option[T] =
    it.next match {
      case "__none__" => Option.empty[T]
      case v          => Some(v.asInstanceOf[T])
    }

  protected override def castNestedOptOptOneEnum(it: jIterator[_]): Option[String] =
    it.next match {
      case "__none__" => Option.empty[String]
      case v          => Some(
        getKwName(v.asInstanceOf[jMap[_, _]].values().iterator().next.toString)
      )
    }

  protected override def castNestedOptOptOneRefAttr(it: jIterator[_]): Option[Long] =
    it.next match {
      case "__none__" => Option.empty[Long]
      case v          =>
        Some(v
          .asInstanceOf[jMap[_, _]].values.iterator.next
          .asInstanceOf[jLong].toLong
        )
    }

  // Optional card many ===========================================================================================


  protected override def castNestedOptOptManyInt(it: jIterator[_]): Option[Set[Int]] =
    it.next match {
      case "__none__" => Option.empty[Set[Int]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Int]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toInt
        Some(set)
    }

  protected override def castNestedOptOptManyLong(it: jIterator[_]): Option[Set[Long]] =
    it.next match {
      case "__none__" => Option.empty[Set[Long]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Long]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jLong].toLong
        Some(set)
    }

  protected override def castNestedOptOptManyDouble(it: jIterator[_]): Option[Set[Double]] =
    it.next match {
      case "__none__" => Option.empty[Set[Double]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Double]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[jDouble].toDouble
        Some(set)
    }

  protected override def castNestedOptOptManyBigInt(it: jIterator[_]): Option[Set[BigInt]] =
    it.next match {
      case "__none__" => Option.empty[Set[BigInt]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigInt]
        while (it1.hasNext)
          set += BigInt(it1.next.asInstanceOf[jBigInt].toString)
        Some(set)
    }

  protected override def castNestedOptOptManyBigDecimal(it: jIterator[_]): Option[Set[BigDecimal]] =
    it.next match {
      case "__none__" => Option.empty[Set[BigDecimal]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var set = Set.empty[BigDecimal]
        while (it1.hasNext)
          set += BigDecimal(it1.next.asInstanceOf[jBigDec].toString)
        Some(set)
    }

  protected override def castNestedOptOptMany[T](it: jIterator[_]): Option[Set[T]] =
    it.next match {
      case "__none__" => Option.empty[Set[T]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var set = Set.empty[T]
        while (it1.hasNext)
          set += it1.next.asInstanceOf[T]
        Some(set)
    }

  protected override def castNestedOptOptManyEnum(it: jIterator[_]): Option[Set[String]] =
    it.next match {
      case "__none__" => Option.empty[Set[String]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var set = Set.empty[String]
        while (it1.hasNext)
          set += getKwName(it1.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)
        Some(set)
    }

  protected override def castNestedOptOptManyRefAttr(it: jIterator[_]): Option[Set[Long]] =
    it.next match {
      case "__none__" => Option.empty[Set[Long]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var set = Set.empty[Long]
        while (it1.hasNext)
          set += it1.next
            .asInstanceOf[jMap[_, _]].values().iterator().next
            .asInstanceOf[jLong].toLong
        Some(set)
    }


  // Map ===========================================================================================

  protected override def castNestedOptMapString(it: jIterator[_]): Map[String, String] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, String]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> pair(1))
    }
    map
  }

  protected override def castNestedOptMapInt(it: jIterator[_]): Map[String, Int] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, Int]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> pair(1).toInt)
    }
    map
  }

  protected override def castNestedOptMapBoolean(it: jIterator[_]): Map[String, Boolean] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, Boolean]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> pair(1).toBoolean)
    }
    map
  }

  protected override def castNestedOptMapLong(it: jIterator[_]): Map[String, Long] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, Long]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> pair(1).toLong)
    }
    map
  }

  protected override def castNestedOptMapDouble(it: jIterator[_]): Map[String, Double] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, Double]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> pair(1).toDouble)
    }
    map
  }

  protected override def castNestedOptMapDate(it: jIterator[_]): Map[String, Date] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, Date]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> str2date(pair(1)))
    }
    map
  }

  protected override def castNestedOptMapUUID(it: jIterator[_]): Map[String, UUID] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, UUID]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> UUID.fromString(pair(1)))
    }
    map
  }

  protected override def castNestedOptMapURI(it: jIterator[_]): Map[String, URI] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, URI]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> new URI(pair(1)))
    }
    map
  }

  protected override def castNestedOptMapBigInt(it: jIterator[_]): Map[String, BigInt] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, BigInt]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> BigInt(pair(1).toString))
    }
    map
  }

  protected override def castNestedOptMapBigDecimal(it: jIterator[_]): Map[String, BigDecimal] = {
    val it1 = it.next.asInstanceOf[jList[_]].iterator
    var map = Map.empty[String, BigDecimal]
    var pair  = new Array[String](2)
    while (it1.hasNext) {
      pair = it1.next.toString.split("@", 2)
      map += (pair(0) -> BigDecimal(pair(1).toString))
    }
    map
  }


  // Optional Map ===========================================================================================

  protected override def castNestedOptOptMapString(it: jIterator[_]): Option[Map[String, String]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, String]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, String]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> vs(1))
        }
        Some(map)
    }

  protected override def castNestedOptOptMapInt(it: jIterator[_]): Option[Map[String, Int]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, Int]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, Int]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> vs(1).toInt)
        }
        Some(map)
    }

  protected override def castNestedOptOptMapBoolean(it: jIterator[_]): Option[Map[String, Boolean]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, Boolean]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, Boolean]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> vs(1).toBoolean)
        }
        Some(map)
    }

  protected override def castNestedOptOptMapLong(it: jIterator[_]): Option[Map[String, Long]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, Long]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, Long]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> vs(1).toLong)
        }
        Some(map)
    }

  protected override def castNestedOptOptMapDouble(it: jIterator[_]): Option[Map[String, Double]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, Double]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, Double]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> vs(1).toDouble)
        }
        Some(map)
    }

  protected override def castNestedOptOptMapDate(it: jIterator[_]): Option[Map[String, Date]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, Date]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, Date]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> str2date(vs(1)))
        }
        Some(map)
    }

  protected override def castNestedOptOptMapUUID(it: jIterator[_]): Option[Map[String, UUID]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, UUID]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, UUID]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> UUID.fromString(vs(1)))
        }
        Some(map)
    }

  protected override def castNestedOptOptMapURI(it: jIterator[_]): Option[Map[String, URI]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, URI]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, URI]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> new URI(vs(1)))
        }
        Some(map)
    }

  protected override def castNestedOptOptMapBigInt(it: jIterator[_]): Option[Map[String, BigInt]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, BigInt]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, BigInt]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> BigInt(vs(1)))
        }
        Some(map)
    }

  protected override def castNestedOptOptMapBigDecimal(it: jIterator[_]): Option[Map[String, BigDecimal]] =
    it.next match {
      case "__none__" => Option.empty[Map[String, BigDecimal]]
      case v          =>
        val it1 = v.asInstanceOf[jList[_]].iterator
        var map = Map.empty[String, BigDecimal]
        var vs  = new Array[String](2)
        while (it1.hasNext) {
          vs = it1.next.toString.split("@", 2)
          map += (vs(0) -> BigDecimal(vs(1)))
        }
        Some(map)
    }
}
