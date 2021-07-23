package molecule.core.macros.qr

import java.lang.{Long => jLong}
import java.net.URI
import java.util.{List => jList, Set => jSet}
import molecule.core.macros.attrResolvers.CastAggr

/** Core molecule interface override defining actions that can be called on molecules.
  *
  * Generally we could often have made higher-order methods taking a casting function for
  * variating cases. But we prioritize minimizing the macro-generated code as much as possible
  * to lower compile time overhead.
  */
trait TypedCastHelpersAggr extends CastAggr with TypedCastHelpersOptNested { 

  // card one

  protected override def castAggrOneListInt(row: jList[_], colIndex: Int): List[Int] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[Int]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[jLong].toInt
    list
  }

  protected override def castAggrOneListURI(row: jList[_], colIndex: Int): List[URI] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[URI]
    while (it.hasNext)
      list = list :+ (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    list
  }

  protected override def castAggrOneListBigInt(row: jList[_], colIndex: Int): List[BigInt] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[BigInt]
    while (it.hasNext)
      list = list :+ BigInt(it.next.toString)
    list
  }

  protected override def castAggrOneListBigDecimal(row: jList[_], colIndex: Int): List[BigDecimal] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[BigDecimal]
    while (it.hasNext)
      list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    list
  }

  protected override def castAggrOneList[T](row: jList[_], colIndex: Int): List[T] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[T]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[T]
    list
  }

  // card many

  protected override def castAggrManyListInt(row: jList[_], colIndex: Int): List[Set[Int]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[Int]
    while (it.hasNext)
      set += it.next.asInstanceOf[jLong].toInt
    List(set)
  }

  protected override def castAggrManyListURI(row: jList[_], colIndex: Int): List[Set[URI]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[URI]
    while (it.hasNext)
      set += (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    List(set)
  }

  protected override def castAggrManyListBigInt(row: jList[_], colIndex: Int): List[Set[BigInt]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigInt]
    while (it.hasNext)
      set += BigInt(it.next.toString)
    List(set)
  }

  protected override def castAggrManyListBigDecimal(row: jList[_], colIndex: Int): List[Set[BigDecimal]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigDecimal]
    while (it.hasNext)
      set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    List(set)
  }

  protected override def castAggrManyList[T](row: jList[_], colIndex: Int): List[Set[T]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[T]
    while (it.hasNext)
      set += it.next.asInstanceOf[T]
    List(set)
  }

  // ------------------------------------

  protected override def castAggrOneListDistinctInt(row: jList[_], colIndex: Int): List[Int] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[Int]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[jLong].toInt
    list
  }

  protected override def castAggrOneListDistinctURI(row: jList[_], colIndex: Int): List[URI] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[URI]
    while (it.hasNext)
      list = list :+ (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    list
  }

  protected override def castAggrOneListDistinctBigInt(row: jList[_], colIndex: Int): List[BigInt] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[BigInt]
    while (it.hasNext)
      list = list :+ BigInt(it.next.toString)
    list
  }

  protected override def castAggrOneListDistinctBigDecimal(row: jList[_], colIndex: Int): List[BigDecimal] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[BigDecimal]
    while (it.hasNext)
      list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    list
  }

  protected override def castAggrOneListDistinct[T](row: jList[_], colIndex: Int): List[T] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[T]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[T]
    list
  }

  // card many

  protected override def castAggrManyListDistinctInt(row: jList[_], colIndex: Int): List[Set[Int]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[Int]
    while (it.hasNext)
      set += it.next.asInstanceOf[jLong].toInt
    List(set)
  }

  protected override def castAggrManyListDistinctURI(row: jList[_], colIndex: Int): List[Set[URI]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[URI]
    while (it.hasNext)
      set += (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    List(set)
  }

  protected override def castAggrManyListDistinctBigInt(row: jList[_], colIndex: Int): List[Set[BigInt]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[BigInt]
    while (it.hasNext)
      set += BigInt(it.next.toString)
    List(set)
  }

  protected override def castAggrManyListDistinctBigDecimal(row: jList[_], colIndex: Int): List[Set[BigDecimal]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[BigDecimal]
    while (it.hasNext)
      set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    List(set)
  }

  protected override def castAggrManyListDistinct[T](row: jList[_], colIndex: Int): List[Set[T]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[T]
    while (it.hasNext)
      set += it.next.asInstanceOf[T]
    List(set)
  }

  // ------------------------------------

  protected override def castAggrOneListRandInt(row: jList[_], colIndex: Int): List[Int] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[Int]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[jLong].toInt
    list
  }

  protected override def castAggrOneListRandURI(row: jList[_], colIndex: Int): List[URI] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[URI]
    while (it.hasNext)
      list = list :+ (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    list
  }

  protected override def castAggrOneListRandBigInt(row: jList[_], colIndex: Int): List[BigInt] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[BigInt]
    while (it.hasNext)
      list = list :+ BigInt(it.next.toString)
    list
  }

  protected override def castAggrOneListRandBigDecimal(row: jList[_], colIndex: Int): List[BigDecimal] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[BigDecimal]
    while (it.hasNext)
      list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    list
  }

  protected override def castAggrOneListRand[T](row: jList[_], colIndex: Int): List[T] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[T]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[T]
    list
  }

  // card many

  protected override def castAggrManyListRandInt(row: jList[_], colIndex: Int): List[Set[Int]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[Int]
    while (it.hasNext)
      set += it.next.asInstanceOf[jLong].toInt
    List(set)
  }

  protected override def castAggrManyListRandURI(row: jList[_], colIndex: Int): List[Set[URI]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[URI]
    while (it.hasNext)
      set += (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    List(set)
  }

  protected override def castAggrManyListRandBigInt(row: jList[_], colIndex: Int): List[Set[BigInt]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigInt]
    while (it.hasNext)
      set += BigInt(it.next.toString)
    List(set)
  }

  protected override def castAggrManyListRandBigDecimal(row: jList[_], colIndex: Int): List[Set[BigDecimal]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigDecimal]
    while (it.hasNext)
      set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    List(set)
  }

  protected override def castAggrManyListRand[T](row: jList[_], colIndex: Int): List[Set[T]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[T]
    while (it.hasNext)
      set += it.next.asInstanceOf[T]
    List(set)
  }

  // ------------------------------------

  protected override def castAggrSingleSampleInt(row: jList[_], colIndex: Int): Int =
    row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[jLong].toInt

  protected override def castAggrSingleSampleURI(row: jList[_], colIndex: Int): URI =
    row.get(colIndex).asInstanceOf[jList[_]].iterator.next match {
      case uri: URI => uri
      case uriImpl  => new URI(uriImpl.toString)
    }

  protected override def castAggrSingleSampleBigInt(row: jList[_], colIndex: Int): BigInt =
    BigInt(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)

  protected override def castAggrSingleSampleBigDecimal(row: jList[_], colIndex: Int): BigDecimal =
    BigDecimal(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[java.math.BigDecimal].toString)

  protected override def castAggrSingleSample[T](row: jList[_], colIndex: Int): T =
    row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[T]

  // ------------------------------------

  protected override def castAggrManySingleInt(row: jList[_], colIndex: Int): Set[Int] =
    Set(row.get(colIndex).asInstanceOf[jLong].toInt)

  protected override def castAggrManySingleURI(row: jList[_], colIndex: Int): Set[URI] =
    Set(row.get(colIndex) match {
      case uri: URI => uri
      case uriImpl  => new URI(uriImpl.toString)
    })

  protected override def castAggrManySingleBigInt(row: jList[_], colIndex: Int): Set[BigInt] =
    Set(BigInt(row.get(colIndex).toString))

  protected override def castAggrManySingleBigDecimal(row: jList[_], colIndex: Int): Set[BigDecimal] =
    Set(BigDecimal(row.get(colIndex).asInstanceOf[java.math.BigDecimal].toString))

  protected override def castAggrManySingle[T](row: jList[_], colIndex: Int): Set[T] =
    Set(row.get(colIndex).asInstanceOf[T])


}
