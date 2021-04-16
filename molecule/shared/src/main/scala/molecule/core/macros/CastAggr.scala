package molecule.core.macros

import scala.reflect.macros.blackbox

private[molecule] trait CastAggr extends CastOptNested {
  val c: blackbox.Context

  import c.universe._

  val castAggrInt   : Int => Tree = (colIndex: Int) => q"row.get($colIndex).toString.toInt"
  val castAggrDouble: Int => Tree = (colIndex: Int) => q"row.get($colIndex).asInstanceOf[Double]"

  val castAggrOneList : String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrOneList[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrOneListInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrOneList[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castAggrOneListFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrOneList[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrOneList[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrOneList[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrOneList[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrOneListURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrOneListBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrOneListBigDecimal(row, $colIndex)"
  }
  val castAggrManyList: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrManyList[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrManyListInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrManyList[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castAggrManyListFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrManyList[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrManyList[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrManyList[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrManyList[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrManyListURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrManyListBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrManyListBigDecimal(row, $colIndex)"
  }

  val castAggrOneListDistinct : String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrOneListDistinct[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrOneListDistinctInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrOneListDistinct[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castAggrOneListDistinctFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrOneListDistinct[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrOneListDistinct[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrOneListDistinct[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrOneListDistinct[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrOneListDistinctURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrOneListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrOneListDistinctBigDecimal(row, $colIndex)"
  }
  val castAggrManyListDistinct: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrManyListDistinct[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrManyListDistinctInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrManyListDistinct[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castAggrManyListDistinctFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrManyListDistinct[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrManyListDistinct[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrManyListDistinct[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrManyListDistinct[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrManyListDistinctURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrManyListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrManyListDistinctBigDecimal(row, $colIndex)"
  }

  val castAggrOneListRand : String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrOneListRand[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrOneListRandInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrOneListRand[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castAggrOneListRandFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrOneListRand[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrOneListRand[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrOneListRand[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrOneListRand[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrOneListRandURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrOneListRandBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrOneListRandBigDecimal(row, $colIndex)"
  }
  val castAggrManyListRand: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrManyListRand[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrManyListRandInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrManyListRand[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castAggrManyListRandFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrManyListRand[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrManyListRand[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrManyListRand[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrManyListRand[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrManyListRandURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrManyListRandBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrManyListRandBigDecimal(row, $colIndex)"
  }


  val castAggrSingleSample: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrSingleSample[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrSingleSampleInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrSingleSample[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castAggrSingleSampleFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrSingleSample[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrSingleSample[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrSingleSample[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrSingleSample[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrSingleSampleURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrSingleSampleBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrSingleSampleBigDecimal(row, $colIndex)"
  }

  val castAggrOneSingle : String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castOneFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
  }
  val castAggrManySingle: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrManySingle[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrManySingleInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrManySingle[Long](row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castAggrManySingleFloat(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrManySingle[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrManySingle[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrManySingle[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrManySingle[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrManySingleURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrManySingleBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrManySingleBigDecimal(row, $colIndex)"
  }
}
