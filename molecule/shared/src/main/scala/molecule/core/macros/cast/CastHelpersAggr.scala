package molecule.core.macros.cast

import java.lang.{Double => jDouble, Long => jLong}
import java.net.URI
import java.util.{List => jList, Set => jSet}

/** Core molecule interface defining actions that can be called on molecules.
  *
  * Generally we could often have made higher-order methods taking a casting function for
  * variating cases. But we prioritize minimizing the macro-generated code as much as possible
  * to lower compile time overhead.
  */
trait CastHelpersAggr extends CastHelpersOptNested {

  // card one

  protected def castAggrOneListInt(row: jList[_], colIndex: Int): List[Int] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[Int]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[jLong].toInt
    list
  }

  protected def castAggrOneListURI(row: jList[_], colIndex: Int): List[URI] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[URI]
    while (it.hasNext)
      list = list :+ (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    list
  }

  protected def castAggrOneListBigInt(row: jList[_], colIndex: Int): List[BigInt] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[BigInt]
    while (it.hasNext)
      list = list :+ BigInt(it.next.toString)
    list
  }

  protected def castAggrOneListBigDecimal(row: jList[_], colIndex: Int): List[BigDecimal] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[BigDecimal]
    while (it.hasNext)
      list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    list
  }

  protected def castAggrOneList[T](row: jList[_], colIndex: Int): List[T] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[T]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[T]
    list
  }

  // card many

  protected def castAggrManyListInt(row: jList[_], colIndex: Int): List[Set[Int]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[Int]
    while (it.hasNext)
      set += it.next.asInstanceOf[jLong].toInt
    List(set)
  }

  protected def castAggrManyListURI(row: jList[_], colIndex: Int): List[Set[URI]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[URI]
    while (it.hasNext)
      set += (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    List(set)
  }

  protected def castAggrManyListBigInt(row: jList[_], colIndex: Int): List[Set[BigInt]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigInt]
    while (it.hasNext)
      set += BigInt(it.next.toString)
    List(set)
  }

  protected def castAggrManyListBigDecimal(row: jList[_], colIndex: Int): List[Set[BigDecimal]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigDecimal]
    while (it.hasNext)
      set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    List(set)
  }

  protected def castAggrManyList[T](row: jList[_], colIndex: Int): List[Set[T]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[T]
    while (it.hasNext)
      set += it.next.asInstanceOf[T]
    List(set)
  }

  // ------------------------------------

  protected def castAggrOneListDistinctInt(row: jList[_], colIndex: Int): List[Int] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[Int]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[jLong].toInt
    list
  }

  protected def castAggrOneListDistinctURI(row: jList[_], colIndex: Int): List[URI] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[URI]
    while (it.hasNext)
      list = list :+ (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    list
  }

  protected def castAggrOneListDistinctBigInt(row: jList[_], colIndex: Int): List[BigInt] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[BigInt]
    while (it.hasNext)
      list = list :+ BigInt(it.next.toString)
    list
  }

  protected def castAggrOneListDistinctBigDecimal(row: jList[_], colIndex: Int): List[BigDecimal] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[BigDecimal]
    while (it.hasNext)
      list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    list
  }

  protected def castAggrOneListDistinct[T](row: jList[_], colIndex: Int): List[T] = {
    val it   = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var list = List.empty[T]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[T]
    list
  }

  // card many

  protected def castAggrManyListDistinctInt(row: jList[_], colIndex: Int): List[Set[Int]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[Int]
    while (it.hasNext)
      set += it.next.asInstanceOf[jLong].toInt
    List(set)
  }

  protected def castAggrManyListDistinctURI(row: jList[_], colIndex: Int): List[Set[URI]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[URI]
    while (it.hasNext)
      set += (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    List(set)
  }

  protected def castAggrManyListDistinctBigInt(row: jList[_], colIndex: Int): List[Set[BigInt]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[BigInt]
    while (it.hasNext)
      set += BigInt(it.next.toString)
    List(set)
  }

  protected def castAggrManyListDistinctBigDecimal(row: jList[_], colIndex: Int): List[Set[BigDecimal]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[BigDecimal]
    while (it.hasNext)
      set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    List(set)
  }

  protected def castAggrManyListDistinct[T](row: jList[_], colIndex: Int): List[Set[T]] = {
    val it  = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    var set = Set.empty[T]
    while (it.hasNext)
      set += it.next.asInstanceOf[T]
    List(set)
  }

  // ------------------------------------

  protected def castAggrOneListRandInt(row: jList[_], colIndex: Int): List[Int] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[Int]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[jLong].toInt
    list
  }

  protected def castAggrOneListRandURI(row: jList[_], colIndex: Int): List[URI] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[URI]
    while (it.hasNext)
      list = list :+ (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    list
  }

  protected def castAggrOneListRandBigInt(row: jList[_], colIndex: Int): List[BigInt] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[BigInt]
    while (it.hasNext)
      list = list :+ BigInt(it.next.toString)
    list
  }

  protected def castAggrOneListRandBigDecimal(row: jList[_], colIndex: Int): List[BigDecimal] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[BigDecimal]
    while (it.hasNext)
      list = list :+ BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    list
  }

  protected def castAggrOneListRand[T](row: jList[_], colIndex: Int): List[T] = {
    val it   = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var list = List.empty[T]
    while (it.hasNext)
      list = list :+ it.next.asInstanceOf[T]
    list
  }

  // card many

  protected def castAggrManyListRandInt(row: jList[_], colIndex: Int): List[Set[Int]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[Int]
    while (it.hasNext)
      set += it.next.asInstanceOf[jLong].toInt
    List(set)
  }

  protected def castAggrManyListRandURI(row: jList[_], colIndex: Int): List[Set[URI]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[URI]
    while (it.hasNext)
      set += (it.next match {
        case uri: URI => uri
        case uriImpl  => new URI(uriImpl.toString)
      })
    List(set)
  }

  protected def castAggrManyListRandBigInt(row: jList[_], colIndex: Int): List[Set[BigInt]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigInt]
    while (it.hasNext)
      set += BigInt(it.next.toString)
    List(set)
  }

  protected def castAggrManyListRandBigDecimal(row: jList[_], colIndex: Int): List[Set[BigDecimal]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[BigDecimal]
    while (it.hasNext)
      set += BigDecimal(it.next.asInstanceOf[java.math.BigDecimal].toString)
    List(set)
  }

  protected def castAggrManyListRand[T](row: jList[_], colIndex: Int): List[Set[T]] = {
    val it  = row.get(colIndex).asInstanceOf[jList[_]].iterator
    var set = Set.empty[T]
    while (it.hasNext)
      set += it.next.asInstanceOf[T]
    List(set)
  }

  // ------------------------------------

  protected def castAggrSingleSampleInt(row: jList[_], colIndex: Int): Int =
    row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[jLong].toInt

  protected def castAggrSingleSampleURI(row: jList[_], colIndex: Int): URI =
    row.get(colIndex).asInstanceOf[jList[_]].iterator.next match {
      case uri: URI => uri
      case uriImpl  => new URI(uriImpl.toString)
    }

  protected def castAggrSingleSampleBigInt(row: jList[_], colIndex: Int): BigInt =
    BigInt(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)

  protected def castAggrSingleSampleBigDecimal(row: jList[_], colIndex: Int): BigDecimal =
    BigDecimal(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[java.math.BigDecimal].toString)

  protected def castAggrSingleSample[T](row: jList[_], colIndex: Int): T =
    row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[T]

  // ------------------------------------

  protected def castAggrManySingleInt(row: jList[_], colIndex: Int): Set[Int] =
    Set(row.get(colIndex).asInstanceOf[jLong].toInt)

  protected def castAggrManySingleURI(row: jList[_], colIndex: Int): Set[URI] =
    Set(row.get(colIndex) match {
      case uri: URI => uri
      case uriImpl  => new URI(uriImpl.toString)
    })

  protected def castAggrManySingleBigInt(row: jList[_], colIndex: Int): Set[BigInt] =
    Set(BigInt(row.get(colIndex).toString))

  protected def castAggrManySingleBigDecimal(row: jList[_], colIndex: Int): Set[BigDecimal] =
    Set(BigDecimal(row.get(colIndex).asInstanceOf[java.math.BigDecimal].toString))

  protected def castAggrManySingle[T](row: jList[_], colIndex: Int): Set[T] =
    Set(row.get(colIndex).asInstanceOf[T])


}
