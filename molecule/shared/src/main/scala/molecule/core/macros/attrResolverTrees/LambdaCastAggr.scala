package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaCastAggr extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val castAggrOneList : String => Int => Tree = {
    case "String"     => lambdaIndex = 110; (colIndex: Int) => q"castAggrOneList[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 111; (colIndex: Int) => q"castAggrOneListInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 112; (colIndex: Int) => q"castAggrOneList[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 113; (colIndex: Int) => q"castAggrOneList[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 114; (colIndex: Int) => q"castAggrOneList[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 115; (colIndex: Int) => q"castAggrOneList[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 116; (colIndex: Int) => q"castAggrOneList[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 117; (colIndex: Int) => q"castAggrOneListURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 118; (colIndex: Int) => q"castAggrOneListBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 119; (colIndex: Int) => q"castAggrOneListBigDecimal(row, $colIndex)"
  }
  val castAggrManyList: String => Int => Tree = {
    case "String"     => lambdaIndex = 120; (colIndex: Int) => q"castAggrManyList[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 121; (colIndex: Int) => q"castAggrManyListInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 122; (colIndex: Int) => q"castAggrManyList[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 123; (colIndex: Int) => q"castAggrManyList[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 124; (colIndex: Int) => q"castAggrManyList[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 125; (colIndex: Int) => q"castAggrManyList[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 126; (colIndex: Int) => q"castAggrManyList[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 127; (colIndex: Int) => q"castAggrManyListURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 128; (colIndex: Int) => q"castAggrManyListBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 129; (colIndex: Int) => q"castAggrManyListBigDecimal(row, $colIndex)"
  }


  val castAggrOneListDistinct : String => Int => Tree = {
    case "String"     => lambdaIndex = 130; (colIndex: Int) => q"castAggrOneListDistinct[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 131; (colIndex: Int) => q"castAggrOneListDistinctInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 132; (colIndex: Int) => q"castAggrOneListDistinct[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 133; (colIndex: Int) => q"castAggrOneListDistinct[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 134; (colIndex: Int) => q"castAggrOneListDistinct[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 135; (colIndex: Int) => q"castAggrOneListDistinct[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 136; (colIndex: Int) => q"castAggrOneListDistinct[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 137; (colIndex: Int) => q"castAggrOneListDistinctURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 138; (colIndex: Int) => q"castAggrOneListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 139; (colIndex: Int) => q"castAggrOneListDistinctBigDecimal(row, $colIndex)"
  }
  val castAggrManyListDistinct: String => Int => Tree = {
    case "String"     => lambdaIndex = 140; (colIndex: Int) => q"castAggrManyListDistinct[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 141; (colIndex: Int) => q"castAggrManyListDistinctInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 142; (colIndex: Int) => q"castAggrManyListDistinct[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 143; (colIndex: Int) => q"castAggrManyListDistinct[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 144; (colIndex: Int) => q"castAggrManyListDistinct[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 145; (colIndex: Int) => q"castAggrManyListDistinct[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 146; (colIndex: Int) => q"castAggrManyListDistinct[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 147; (colIndex: Int) => q"castAggrManyListDistinctURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 148; (colIndex: Int) => q"castAggrManyListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 149; (colIndex: Int) => q"castAggrManyListDistinctBigDecimal(row, $colIndex)"
  }


  val castAggrOneListRand : String => Int => Tree = {
    case "String"     => lambdaIndex = 150; (colIndex: Int) => q"castAggrOneListRand[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 151; (colIndex: Int) => q"castAggrOneListRandInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 152; (colIndex: Int) => q"castAggrOneListRand[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 153; (colIndex: Int) => q"castAggrOneListRand[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 154; (colIndex: Int) => q"castAggrOneListRand[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 155; (colIndex: Int) => q"castAggrOneListRand[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 156; (colIndex: Int) => q"castAggrOneListRand[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 157; (colIndex: Int) => q"castAggrOneListRandURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 158; (colIndex: Int) => q"castAggrOneListRandBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 159; (colIndex: Int) => q"castAggrOneListRandBigDecimal(row, $colIndex)"
  }
  val castAggrManyListRand: String => Int => Tree = {
    case "String"     => lambdaIndex = 160; (colIndex: Int) => q"castAggrManyListRand[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 161; (colIndex: Int) => q"castAggrManyListRandInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 162; (colIndex: Int) => q"castAggrManyListRand[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 163; (colIndex: Int) => q"castAggrManyListRand[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 164; (colIndex: Int) => q"castAggrManyListRand[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 165; (colIndex: Int) => q"castAggrManyListRand[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 166; (colIndex: Int) => q"castAggrManyListRand[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 167; (colIndex: Int) => q"castAggrManyListRandURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 168; (colIndex: Int) => q"castAggrManyListRandBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 169; (colIndex: Int) => q"castAggrManyListRandBigDecimal(row, $colIndex)"
  }


  val castAggrSingleSample: String => Int => Tree = {
    case "String"     => lambdaIndex = 170; (colIndex: Int) => q"castAggrSingleSample[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 171; (colIndex: Int) => q"castAggrSingleSampleInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 172; (colIndex: Int) => q"castAggrSingleSample[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 173; (colIndex: Int) => q"castAggrSingleSample[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 174; (colIndex: Int) => q"castAggrSingleSample[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 175; (colIndex: Int) => q"castAggrSingleSample[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 176; (colIndex: Int) => q"castAggrSingleSample[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 177; (colIndex: Int) => q"castAggrSingleSampleURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 178; (colIndex: Int) => q"castAggrSingleSampleBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 179; (colIndex: Int) => q"castAggrSingleSampleBigDecimal(row, $colIndex)"
  }

  val castAggrOneSingle: String => Int => Tree = {
    case "String"     => lambdaIndex = 180; (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 181; (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 182; (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 183; (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 184; (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 185; (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 186; (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 187; (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 188; (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 189; (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
  }

  val castAggrManySingle: String => Int => Tree = {
    case "String"     => lambdaIndex = 190; (colIndex: Int) => q"castAggrManySingle[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 191; (colIndex: Int) => q"castAggrManySingleInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 192; (colIndex: Int) => q"castAggrManySingle[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 193; (colIndex: Int) => q"castAggrManySingle[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 194; (colIndex: Int) => q"castAggrManySingle[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 195; (colIndex: Int) => q"castAggrManySingle[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 196; (colIndex: Int) => q"castAggrManySingle[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 197; (colIndex: Int) => q"castAggrManySingleURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 198; (colIndex: Int) => q"castAggrManySingleBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 199; (colIndex: Int) => q"castAggrManySingleBigDecimal(row, $colIndex)"
  }
}
