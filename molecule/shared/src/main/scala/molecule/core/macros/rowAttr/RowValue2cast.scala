package molecule.core.macros.rowAttr

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait RowValue2cast extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  def getRowValue2castLambda(group: String, baseTpe: String): Int => Tree = group match {
    case "One"                  => castOneAttr(baseTpe)
    case "OptOne"               => castOptOneAttr(baseTpe)
    case "Many"                 => castManyAttr(baseTpe)
    case "OptMany"              => castOptManyAttr(baseTpe)
    case "Map"                  => castMapAttr_(baseTpe)
    case "OptMap"               => castOptMapAttr_(baseTpe)
    case "OptApplyOne"          => castOptApplyOneAttr(baseTpe)
    case "OptApplyMany"         => castOptApplyManyAttr(baseTpe)
    case "OptApplyMap"          => castOptApplyMapAttr_(baseTpe)
    case "KeyedMap"             => castKeyedMapAttr(baseTpe)
    case "AggrOneList"          => castAggrOneList(baseTpe)
    case "AggrManyList"         => castAggrManyList(baseTpe)
    case "AggrOneListDistinct"  => castAggrOneListDistinct(baseTpe)
    case "AggrManyListDistinct" => castAggrManyListDistinct(baseTpe)
    case "AggrOneListRand"      => castAggrOneListRand(baseTpe)
    case "AggrManyListRand"     => castAggrManyListRand(baseTpe)
    case "AggrSingleSample"     => castAggrSingleSample(baseTpe)
    case "AggrOneSingle"        => castAggrOneSingle(baseTpe)
    case "AggrManySingle"       => castAggrManySingle(baseTpe)
  }

  lazy val castAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOneAttr(t.tpeS) else castManyAttr(t.tpeS)

  lazy val castOneAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
    case "Any"        => (colIndex: Int) => q"row.get($colIndex)"
    case "enum"       => (colIndex: Int) => q"row.get($colIndex).asInstanceOf[String]"
    case "ref"        => (colIndex: Int) => q"castOne[Long](row, $colIndex)"
  }

  lazy val castManyAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castMany[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castManyInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castMany[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castMany[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castMany[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castMany[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castMany[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castManyURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castManyBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castManyBigDecimal(row, $colIndex)"
    case "enum"       => (colIndex: Int) => q"castMany[String](row, $colIndex)"
    case "ref"        => (colIndex: Int) => q"castMany[Long](row, $colIndex)"
  }

  lazy val castEnum: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    (colIndex: Int) => q"row.get($colIndex).asInstanceOf[String]"
  } else {
    (colIndex: Int) => q"castMany[String](row, $colIndex)"
  }


  lazy val castOptAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    castOptOneAttr(t.tpeS)
  } else {
    castOptManyAttr(t.tpeS)
  }

  lazy val castOptOneAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptOne[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOptOneInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOptOneLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOptOneDouble(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOptOne[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOptOne[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOptOne[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOptOneURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOptOneBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOptOneBigDecimal(row, $colIndex)"
    case "enum"       => (colIndex: Int) => q"castOptOneEnum(row, $colIndex)"
    case "ref"        => (colIndex: Int) => q"castOptOneRefAttr(row, $colIndex)"
  }

  lazy val castOptManyAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptMany[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOptManyInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOptManyLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOptManyDouble(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOptMany[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOptMany[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOptMany[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOptManyURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOptManyBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOptManyBigDecimal(row, $colIndex)"
    case "enum"       => (colIndex: Int) => q"castOptManyEnum(row, $colIndex)"
    case "ref"        => (colIndex: Int) => q"castOptManyRefAttr(row, $colIndex)"
  }

  lazy val castOptEnum: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    (colIndex: Int) => q"castOptOneEnum(row, $colIndex)"
  } else {
    (colIndex: Int) => q"castOptManyEnum(row, $colIndex)"
  }

  lazy val castOptRefAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    (colIndex: Int) => q"castOptOneRefAttr(row, $colIndex)"
  } else {
    (colIndex: Int) => q"castOptManyRefAttr(row, $colIndex)"
  }


  lazy val castMapAttr: richTree => Int => Tree = (t: richTree) => castMapAttr_(t.tpeS)

  lazy val castMapAttr_ : String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castMapString(row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castMapInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castMapLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castMapDouble(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castMapBoolean(row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castMapDate(row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castMapUUID(row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castMapURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castMapBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castMapBigDecimal(row, $colIndex)"
  }

  lazy val castOptMapAttr: richTree => Int => Tree = (t: richTree) => castOptMapAttr_(t.tpeS)

  lazy val castOptMapAttr_ : String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptMapString(row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOptMapInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOptMapLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOptMapDouble(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOptMapBoolean(row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOptMapDate(row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOptMapUUID(row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOptMapURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOptMapBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOptMapBigDecimal(row, $colIndex)"
  }


  lazy val castOptApplyAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    castOptApplyOneAttr(t.tpeS)
  } else {
    castOptApplyManyAttr(t.tpeS)
  }

  lazy val castOptApplyOneAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptApplyOne[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOptApplyOneInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOptApplyOneLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOptApplyOneDouble(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOptApplyOne[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOptApplyOne[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOptApplyOne[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOptApplyOneURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOptApplyOneBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOptApplyOneBigDecimal(row, $colIndex)"
    case "ref"        => (colIndex: Int) => q"castOptApplyOneLong(row, $colIndex)"
  }

  lazy val castOptApplyManyAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptApplyMany[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOptApplyManyInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOptApplyManyLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOptApplyManyDouble(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOptApplyMany[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOptApplyMany[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOptApplyMany[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOptApplyManyURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOptApplyManyBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOptApplyManyBigDecimal(row, $colIndex)"
    case "ref"        => (colIndex: Int) => q"castOptApplyManyLong(row, $colIndex)"
  }

  lazy val castOptApplyMapAttr: richTree => Int => Tree = (t: richTree) => castOptApplyMapAttr_(t.tpeS)

  lazy val castOptApplyMapAttr_ : String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptApplyMapString(row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOptApplyMapInt(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOptApplyMapBoolean(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOptApplyMapLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOptApplyMapDouble(row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOptApplyMapDate(row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOptApplyMapUUID(row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOptApplyMapURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOptApplyMapBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOptApplyMapBigDecimal(row, $colIndex)"
  }

  lazy val castKeyedMapAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"row.get($colIndex).toString"
    case "Int"        => (colIndex: Int) => q"row.get($colIndex).toString.toInt"
    case "Long"       => (colIndex: Int) => q"row.get($colIndex).toString.toLong"
    case "Double"     => (colIndex: Int) => q"row.get($colIndex).toString.toDouble"
    case "Boolean"    => (colIndex: Int) => q"row.get($colIndex).toString.toBoolean"
    case "Date"       => (colIndex: Int) => q"molecule.core.util.fns.str2date(row.get($colIndex).toString)"
    case "UUID"       => (colIndex: Int) => q"java.util.UUID.fromString(row.get($colIndex).toString)"
    case "URI"        => (colIndex: Int) => q"new java.net.URI(row.get($colIndex).toString)"
    case "BigInt"     => (colIndex: Int) => q"BigInt(row.get($colIndex).toString)"
    case "BigDecimal" => (colIndex: Int) => q"BigDecimal(row.get($colIndex).toString)"
    case "Any"        => (colIndex: Int) => q"row.get($colIndex)"
  }


  // Aggregates..............

  lazy val castAggrOneList: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrOneList[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrOneListInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrOneList[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrOneList[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrOneList[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrOneList[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrOneList[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrOneListURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrOneListBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrOneListBigDecimal(row, $colIndex)"
  }

  lazy val castAggrManyList: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrManyList[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrManyListInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrManyList[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrManyList[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrManyList[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrManyList[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrManyList[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrManyListURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrManyListBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrManyListBigDecimal(row, $colIndex)"
  }

  lazy val castAggrOneListDistinct: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrOneListDistinct[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrOneListDistinctInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrOneListDistinct[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrOneListDistinct[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrOneListDistinct[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrOneListDistinct[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrOneListDistinct[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrOneListDistinctURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrOneListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrOneListDistinctBigDecimal(row, $colIndex)"
  }

  lazy val castAggrManyListDistinct: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrManyListDistinct[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrManyListDistinctInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrManyListDistinct[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrManyListDistinct[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrManyListDistinct[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrManyListDistinct[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrManyListDistinct[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrManyListDistinctURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrManyListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrManyListDistinctBigDecimal(row, $colIndex)"
  }

  lazy val castAggrOneListRand: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrOneListRand[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrOneListRandInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrOneListRand[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrOneListRand[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrOneListRand[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrOneListRand[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrOneListRand[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrOneListRandURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrOneListRandBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrOneListRandBigDecimal(row, $colIndex)"
  }

  lazy val castAggrManyListRand: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrManyListRand[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrManyListRandInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrManyListRand[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrManyListRand[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrManyListRand[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrManyListRand[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrManyListRand[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrManyListRandURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrManyListRandBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrManyListRandBigDecimal(row, $colIndex)"
  }

  lazy val castAggrSingleSample: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrSingleSample[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrSingleSampleInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrSingleSample[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrSingleSample[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrSingleSample[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrSingleSample[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrSingleSample[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrSingleSampleURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrSingleSampleBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrSingleSampleBigDecimal(row, $colIndex)"
  }

  lazy val castAggrOneSingle: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
  }

  lazy val castAggrManySingle: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castAggrManySingle[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castAggrManySingleInt(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castAggrManySingle[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castAggrManySingle[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castAggrManySingle[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castAggrManySingle[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castAggrManySingle[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castAggrManySingleURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castAggrManySingleBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castAggrManySingleBigDecimal(row, $colIndex)"
  }
}
