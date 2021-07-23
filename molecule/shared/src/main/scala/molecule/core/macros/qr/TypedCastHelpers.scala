package molecule.core.macros.qr

import java.lang.{Double => jDouble, Long => jLong}
import java.net.URI
import java.util.{Date, UUID, List => jList, Map => jMap, Set => jSet}
import molecule.core.macros.attrResolvers.CastTypes


/** Cast methods for converting raw data to Obj or Tpl
  */
trait TypedCastHelpers extends CastTypes with TypedCastHelpersAggr {


  // One ===========================================================================================

  protected override def castOneInt(row: jList[_], colIndex: Int): Int =
    row.get(colIndex).toString.toInt

  protected override def castOneURI(row: jList[_], colIndex: Int): URI = {
    row.get(colIndex) match {
      case uri: URI => uri
      case uriImpl  => new URI(uriImpl.toString)
    }
  }

  protected override def castOneBigInt(row: jList[_], colIndex: Int): BigInt =
    BigInt(row.get(colIndex).toString)

  protected override def castOneBigDecimal(row: jList[_], colIndex: Int): BigDecimal =
    BigDecimal(row.get(colIndex).toString)

  protected override def castOne[T](row: jList[_], colIndex: Int): T =
    row.get(colIndex).asInstanceOf[T]


  // Many ===========================================================================================

  protected override def castManyInt(row: jList[_], colIndex: Int): Set[Int] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[Int]
    while (it.hasNext)
      set += it.next.asInstanceOf[jLong].toInt
    set
  }

  protected override def castManyURI(row: jList[_], colIndex: Int): Set[URI] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[URI]
    while (it.hasNext) {
      set += (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    }
    set
  }

  protected override def castManyBigInt(row: jList[_], colIndex: Int): Set[BigInt] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[BigInt]
    while (it.hasNext)
      set += BigInt(it.next.toString)
    set
  }

  protected override def castManyBigDecimal(row: jList[_], colIndex: Int): Set[BigDecimal] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[BigDecimal]
    while (it.hasNext)
      set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    set
  }

  protected override def castManyEnum(row: jList[_], colIndex: Int): Set[String] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[String]
    while (it.hasNext)
      set += it.next.toString
    set
  }

  protected override def castMany[T](row: jList[_], colIndex: Int): Set[T] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[T]
    while (it.hasNext)
      set += it.next.asInstanceOf[T]
    set
  }


  // Optional card one ===========================================================================================

  // Datomic pull requests for optional value either returns `null` or a `jMap[<ident>, <value>]`

  protected override def castOptOneInt(row: jList[_], colIndex: Int): Option[Int] = row.get(colIndex).asInstanceOf[Option[Int]]
  //  match {
  //    case None => Option.empty[Int]
  //    case Some(v) => Some(v.toString.toInt)
  //  }

  protected override def castOptOneLong(row: jList[_], colIndex: Int): Option[Long] = row.get(colIndex).asInstanceOf[Option[Long]]
//  match {
//    case null => Option.empty[Long]
//    case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jLong].toLong)
//  }

  protected override def castOptOneDouble(row: jList[_], colIndex: Int): Option[Double] = row.get(colIndex).asInstanceOf[Option[Double]]
//  match {
//    case null => Option.empty[Double]
//    case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jDouble].toDouble)
//  }

  protected override def castOptOneURI(row: jList[_], colIndex: Int): Option[URI] = row.get(colIndex).asInstanceOf[Option[URI]]
//  match {
//    case null => Option.empty[URI]
//    case v    => v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next match {
//      case uri: URI => Some(uri)
//      case uriImpl  => Some(new URI(uriImpl.toString))
//    }
//  }

  protected override def castOptOneBigInt(row: jList[_], colIndex: Int): Option[BigInt] = row.get(colIndex).asInstanceOf[Option[BigInt]]
//  match {
//    case null => Option.empty[BigInt]
//    case v    => Some(BigInt(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
//  }

  protected override def castOptOneBigDecimal(row: jList[_], colIndex: Int): Option[BigDecimal] = row.get(colIndex).asInstanceOf[Option[BigDecimal]]
//  match {
//    case null => Option.empty[BigDecimal]
//    case v    => Some(BigDecimal(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString))
//  }

  protected override def castOptOne[T](row: jList[_], colIndex: Int): Option[T] = row.get(colIndex).asInstanceOf[Option[T]]
//  match {
//    case null => Option.empty[T]
//    case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[T])
//  }

  // ----------------------------------------------

  protected override def castOptApplyOneInt(row: jList[_], colIndex: Int): Option[Int] = row.get(colIndex).asInstanceOf[Option[Int]]
//  match {
//    case null => Option.empty[Int]
//    case v    => Some(v.asInstanceOf[jLong].toInt)
//  }

  protected override def castOptApplyOneLong(row: jList[_], colIndex: Int): Option[Long] = row.get(colIndex).asInstanceOf[Option[Long]]
//  match {
//    case null => Option.empty[Long]
//    case v    => Some(v.asInstanceOf[jLong].toLong)
//  }

  protected override def castOptApplyOneDouble(row: jList[_], colIndex: Int): Option[Double] = row.get(colIndex).asInstanceOf[Option[Double]]
//  match {
//    case null => Option.empty[Double]
//    case v    => Some(v.asInstanceOf[jDouble].toDouble)
//  }

  protected override def castOptApplyOneURI(row: jList[_], colIndex: Int): Option[URI] = row.get(colIndex).asInstanceOf[Option[URI]]
//  match {
//    case null     => Option.empty[URI]
//    case uri: URI => Some(uri)
//    case uriImpl  => Some(new URI(uriImpl.toString))
//  }

  protected override def castOptApplyOneBigInt(row: jList[_], colIndex: Int): Option[BigInt] = row.get(colIndex).asInstanceOf[Option[BigInt]]
//  match {
//    case null => Option.empty[BigInt]
//    case v    => Some(BigInt(v.toString))
//  }

  protected override def castOptApplyOneBigDecimal(row: jList[_], colIndex: Int): Option[BigDecimal] = row.get(colIndex).asInstanceOf[Option[BigDecimal]]
//  match {
//    case null => Option.empty[BigDecimal]
//    case v    => Some(BigDecimal(v.toString))
//  }

  // Datomic pull requests for optional value either returns `null` or a `jMap[<ident>, <value>]`
  protected override def castOptApplyOne[T](row: jList[_], colIndex: Int): Option[T] = row.get(colIndex).asInstanceOf[Option[T]]
//  match {
//    case null => Option.empty[T]
//    case v    => Some(v.asInstanceOf[T])
//  }


  // Optional card many ===========================================================================================

  protected override def castOptManyInt(row: jList[_], colIndex: Int): Option[Set[Int]] = row.get(colIndex) match {
    case null => Option.empty[Set[Int]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[Int]
      while (it.hasNext)
        set += it.next.asInstanceOf[jLong].toInt
      Some(set)
  }

  protected override def castOptManyLong(row: jList[_], colIndex: Int): Option[Set[Long]] = row.get(colIndex) match {
    case null => Option.empty[Set[Long]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[Long]
      while (it.hasNext)
        set += it.next.asInstanceOf[jLong].toLong
      Some(set)
  }

  protected override def castOptManyDouble(row: jList[_], colIndex: Int): Option[Set[Double]] = row.get(colIndex) match {
    case null => Option.empty[Set[Double]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[Double]
      while (it.hasNext)
        set += it.next.asInstanceOf[jDouble].toDouble
      Some(set)
  }

  protected override def castOptManyURI(row: jList[_], colIndex: Int): Option[Set[URI]] = row.get(colIndex) match {
    case null => Option.empty[Set[URI]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[URI]
      while (it.hasNext)
        set += (it.next match {
          case uri: URI => uri
          case uriImpl  => new URI(uriImpl.toString)
        })
      Some(set)
  }

  protected override def castOptManyBigInt(row: jList[_], colIndex: Int): Option[Set[BigInt]] = row.get(colIndex) match {
    case null => Option.empty[Set[BigInt]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[BigInt]
      while (it.hasNext)
        set += BigInt(it.next.toString)
      Some(set)
  }

  protected override def castOptManyBigDecimal(row: jList[_], colIndex: Int): Option[Set[BigDecimal]] = row.get(colIndex) match {
    case null => Option.empty[Set[BigDecimal]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[BigDecimal]
      while (it.hasNext)
        set += BigDecimal(it.next.toString)
      Some(set)
  }

  protected override def castOptMany[T](row: jList[_], colIndex: Int): Option[Set[T]] = row.get(colIndex) match {
    case null => Option.empty[Set[T]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[T]
      while (it.hasNext)
        set += it.next.asInstanceOf[T]
      Some(set)
  }

  // ------------------------------

  protected override def castOptApplyManyInt(row: jList[_], colIndex: Int): Option[Set[Int]] = row.get(colIndex) match {
    case null => Option.empty[Set[Int]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var set = Set.empty[Int]
      while (it.hasNext)
        set += it.next.asInstanceOf[jLong].toInt
      Some(set)
  }

  protected override def castOptApplyManyLong(row: jList[_], colIndex: Int): Option[Set[Long]] = row.get(colIndex) match {
    case null => Option.empty[Set[Long]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var set = Set.empty[Long]
      while (it.hasNext)
        set += it.next.asInstanceOf[jLong].toLong
      Some(set)
  }

  protected override def castOptApplyManyDouble(row: jList[_], colIndex: Int): Option[Set[Double]] = row.get(colIndex) match {
    case null => Option.empty[Set[Double]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var set = Set.empty[Double]
      while (it.hasNext)
        set += it.next.asInstanceOf[jDouble].toDouble
      Some(set)
  }

  protected override def castOptApplyManyURI(row: jList[_], colIndex: Int): Option[Set[URI]] = row.get(colIndex) match {
    case null => Option.empty[Set[URI]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var set = Set.empty[URI]
      while (it.hasNext)
        set += (it.next match {
          case uri: URI => uri
          case uriImpl  => new URI(uriImpl.toString)
        })
      Some(set)
  }

  protected override def castOptApplyManyBigInt(row: jList[_], colIndex: Int): Option[Set[BigInt]] = row.get(colIndex) match {
    case null => Option.empty[Set[BigInt]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var set = Set.empty[BigInt]
      while (it.hasNext)
        set += BigInt(it.next.toString)
      Some(set)
  }

  protected override def castOptApplyManyBigDecimal(row: jList[_], colIndex: Int): Option[Set[BigDecimal]] = row.get(colIndex) match {
    case null => Option.empty[Set[BigDecimal]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var set = Set.empty[BigDecimal]
      while (it.hasNext)
        set += BigDecimal(it.next.toString)
      Some(set)
  }

  protected override def castOptApplyMany[T](row: jList[_], colIndex: Int): Option[Set[T]] = row.get(colIndex) match {
    case null => Option.empty[Set[T]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var set = Set.empty[T]
      while (it.hasNext)
        set += it.next.asInstanceOf[T]
      Some(set)
  }


  // Optional ref attr ===========================================================================================

  protected override def castOptOneRefAttr(row: jList[_], colIndex: Int): Option[Long] = row.get(colIndex) match {
    case null => Option.empty[Long]
    case v    =>
      var id   = 0L
      var done = false
      // Hack to avoid looking up map by clojure Keyword - there must be a better way...
      v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.asInstanceOf[jMap[_, _]].forEach {
        case _ if done                        =>
        case (k, v) if k.toString == ":db/id" => done = true; id = v.asInstanceOf[jLong].toLong
        case _                                =>
      }
      Some(id)
  }

  protected override def castOptManyRefAttr(row: jList[_], colIndex: Int): Option[Set[Long]] = row.get(colIndex) match {
    case null => Option.empty[Set[Long]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var set = Set.empty[Long]
      // Hack to avoid looking up map by clojure Keyword - there must be a better way...
      while (it.hasNext) {
        var done = false
        it.next.asInstanceOf[jMap[_, _]].forEach {
          case _ if done                        =>
          case (k, v) if k.toString == ":db/id" => done = true; set += v.asInstanceOf[jLong].toLong
          case _                                =>
        }
      }
      Some(set)
  }


  // Enum ===========================================================================================

  protected override def castOptOneEnum(row: jList[_], colIndex: Int): Option[String] = row.get(colIndex).asInstanceOf[Option[String]]
//  match {
//    case null => Option.empty[String]
//    case v    =>
//      Some(
//        getKwName(
//          v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
//            .asInstanceOf[jMap[_, _]].values.iterator.next.toString
//        )
//      )
//  }

  protected override def castOptManyEnum(row: jList[_], colIndex: Int): Option[Set[String]] = row.get(colIndex).asInstanceOf[Option[Set[String]]]
//  match {
//    case null => Option.empty[Set[String]]
//    case v    =>
//      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
//      var set = Set.empty[String]
//      while (it.hasNext)
//        set += getKwName(it.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)
//      Some(set)
//  }


  // Map ===========================================================================================

  protected override def castMapString(row: jList[_], colIndex: Int): Map[String, String] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, String]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> vs(1))
    }
    map
  }

  protected override def castMapInt(row: jList[_], colIndex: Int): Map[String, Int] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, Int]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> vs(1).toInt)
    }
    map
  }

  protected override def castMapLong(row: jList[_], colIndex: Int): Map[String, Long] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, Long]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> vs(1).toLong)
    }
    map
  }

  protected override def castMapDouble(row: jList[_], colIndex: Int): Map[String, Double] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, Double]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> vs(1).toDouble)
    }
    map
  }

  protected override def castMapBoolean(row: jList[_], colIndex: Int): Map[String, Boolean] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, Boolean]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> vs(1).toBoolean)
    }
    map
  }

  protected override def castMapDate(row: jList[_], colIndex: Int): Map[String, Date] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, Date]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> str2date(vs(1)))
    }
    map
  }

  protected override def castMapUUID(row: jList[_], colIndex: Int): Map[String, UUID] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, UUID]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> UUID.fromString(vs(1)))
    }
    map
  }

  protected override def castMapURI(row: jList[_], colIndex: Int): Map[String, URI] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, URI]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> new URI(vs(1)))
    }
    map
  }

  protected override def castMapBigInt(row: jList[_], colIndex: Int): Map[String, BigInt] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, BigInt]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> BigInt(vs(1)))
    }
    map
  }

  protected override def castMapBigDecimal(row: jList[_], colIndex: Int): Map[String, BigDecimal] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var map = Map.empty[String, BigDecimal]
    var vs  = new Array[String](2)
    while (it.hasNext) {
      vs = it.next.toString.split("@", 2)
      map += (vs(0) -> BigDecimal(vs(1)))
    }
    map
  }


  // Optional Map ===========================================================================================

  protected override def castOptMapString(row: jList[_], colIndex: Int): Option[Map[String, String]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, String]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, String]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1))
      }
      Some(map)
  }

  protected override def castOptMapInt(row: jList[_], colIndex: Int): Option[Map[String, Int]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Int]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, Int]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1).toInt)
      }
      Some(map)
  }

  protected override def castOptMapLong(row: jList[_], colIndex: Int): Option[Map[String, Long]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Long]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, Long]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1).toLong)
      }
      Some(map)
  }

  protected override def castOptMapDouble(row: jList[_], colIndex: Int): Option[Map[String, Double]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Double]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, Double]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1).toDouble)
      }
      Some(map)
  }

  protected override def castOptMapBoolean(row: jList[_], colIndex: Int): Option[Map[String, Boolean]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Boolean]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, Boolean]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1).toBoolean)
      }
      Some(map)
  }

  protected override def castOptMapDate(row: jList[_], colIndex: Int): Option[Map[String, Date]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Date]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, Date]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> str2date(vs(1)))
      }
      Some(map)
  }

  protected override def castOptMapUUID(row: jList[_], colIndex: Int): Option[Map[String, UUID]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, UUID]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, UUID]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> UUID.fromString(vs(1)))
      }
      Some(map)
  }

  protected override def castOptMapURI(row: jList[_], colIndex: Int): Option[Map[String, URI]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, URI]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, URI]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> new URI(vs(1)))
      }
      Some(map)
  }

  protected override def castOptMapBigInt(row: jList[_], colIndex: Int): Option[Map[String, BigInt]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, BigInt]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, BigInt]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> BigInt(vs(1)))
      }
      Some(map)
  }

  protected override def castOptMapBigDecimal(row: jList[_], colIndex: Int): Option[Map[String, BigDecimal]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, BigDecimal]]
    case v    =>
      val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
      var map = Map.empty[String, BigDecimal]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> BigDecimal(vs(1)))
      }
      Some(map)
  }


  // Optional Map apply ===========================================================================================

  protected override def castOptApplyMapString(row: jList[_], colIndex: Int): Option[Map[String, String]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, String]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, String]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1))
      }
      Some(map)
  }

  protected override def castOptApplyMapInt(row: jList[_], colIndex: Int): Option[Map[String, Int]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Int]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, Int]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1).toInt)
      }
      Some(map)
  }

  protected override def castOptApplyMapLong(row: jList[_], colIndex: Int): Option[Map[String, Long]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Long]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, Long]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1).toLong)
      }
      Some(map)
  }

  protected override def castOptApplyMapDouble(row: jList[_], colIndex: Int): Option[Map[String, Double]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Double]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, Double]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1).toDouble)
      }
      Some(map)
  }

  protected override def castOptApplyMapBoolean(row: jList[_], colIndex: Int): Option[Map[String, Boolean]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Boolean]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, Boolean]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> vs(1).toBoolean)
      }
      Some(map)
  }

  protected override def castOptApplyMapDate(row: jList[_], colIndex: Int): Option[Map[String, Date]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, Date]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, Date]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> str2date(vs(1)))
      }
      Some(map)
  }

  protected override def castOptApplyMapUUID(row: jList[_], colIndex: Int): Option[Map[String, UUID]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, UUID]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, UUID]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> UUID.fromString(vs(1)))
      }
      Some(map)
  }

  protected override def castOptApplyMapURI(row: jList[_], colIndex: Int): Option[Map[String, URI]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, URI]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, URI]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> new URI(vs(1)))
      }
      Some(map)
  }

  protected override def castOptApplyMapBigInt(row: jList[_], colIndex: Int): Option[Map[String, BigInt]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, BigInt]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, BigInt]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> BigInt(vs(1)))
      }
      Some(map)
  }

  protected override def castOptApplyMapBigDecimal(row: jList[_], colIndex: Int): Option[Map[String, BigDecimal]] = row.get(colIndex) match {
    case null => Option.empty[Map[String, BigDecimal]]
    case v    =>
      val it  = v.asInstanceOf[jSet[_]].iterator
      var map = Map.empty[String, BigDecimal]
      var vs  = new Array[String](2)
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map += (vs(0) -> BigDecimal(vs(1)))
      }
      Some(map)
  }
}
