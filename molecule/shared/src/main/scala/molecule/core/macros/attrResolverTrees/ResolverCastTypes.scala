package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait ResolverCastTypes extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  def getResolverCastTypes(group: String, baseType: String): Int => Tree = group match {
    case "One"                  => castOneAttr(baseType)
    case "OptOne"               => castOptOneAttr(baseType)
    case "Many"                 => castManyAttr(baseType)
    case "OptMany"              => castOptManyAttr(baseType)
    case "Map"                  => castMapAttr_(baseType)
    case "OptMap"               => castOptMapAttr_(baseType)
    case "OptApplyOne"          => castOptApplyOneAttr(baseType)
    case "OptApplyMany"         => castOptApplyManyAttr(baseType)
    case "OptApplyMap"          => castOptApplyMapAttr_(baseType)
    case "KeyedMap"             => castKeyedMapAttr(baseType)
    case "AggrOneList"          => castAggrOneList(baseType)
    case "AggrManyList"         => castAggrManyList(baseType)
    case "AggrOneListDistinct"  => castAggrOneListDistinct(baseType)
    case "AggrManyListDistinct" => castAggrManyListDistinct(baseType)
    case "AggrOneListRand"      => castAggrOneListRand(baseType)
    case "AggrManyListRand"     => castAggrManyListRand(baseType)
    case "AggrSingleSample"     => castAggrSingleSample(baseType)
    case "AggrOneSingle"        => castAggrOneSingle(baseType)
    case "AggrManySingle"       => castAggrManySingle(baseType)
  }

  lazy val castAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOneAttr(t.tpeS) else castManyAttr(t.tpeS)

  lazy val castOneAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 0; (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 1; (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 3; (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 4; (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 5; (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 6; (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 7; (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 8; (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 9; (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 10; (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
    case "Any"        => lambdaIndex = 11; (colIndex: Int) => q"row.get($colIndex)"
    case "enum"       => lambdaIndex = -1; (colIndex: Int) => q"row.get($colIndex).asInstanceOf[String]"
    case "ref"        => lambdaIndex = -2; (colIndex: Int) => q"castOne[Long](row, $colIndex)"
  }

  lazy val castManyAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 25; (colIndex: Int) => q"castMany[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 26; (colIndex: Int) => q"castManyInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 27; (colIndex: Int) => q"castMany[Long](row, $colIndex)"
    case "Double"     => lambdaIndex = 28; (colIndex: Int) => q"castMany[Double](row, $colIndex)"
    case "Boolean"    => lambdaIndex = 29; (colIndex: Int) => q"castMany[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 30; (colIndex: Int) => q"castMany[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 31; (colIndex: Int) => q"castMany[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 32; (colIndex: Int) => q"castManyURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 33; (colIndex: Int) => q"castManyBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 34; (colIndex: Int) => q"castManyBigDecimal(row, $colIndex)"
    case "enum"       => lambdaIndex = 24; (colIndex: Int) => q"castMany[String](row, $colIndex)"
    case "ref"        => lambdaIndex = -3; (colIndex: Int) => q"castMany[Long](row, $colIndex)"
  }

  lazy val castEnum: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    lambdaIndex = -1
    (colIndex: Int) => q"row.get($colIndex).asInstanceOf[String]"
  } else {
    lambdaIndex = 24
    (colIndex: Int) => q"castMany[String](row, $colIndex)"
  }


  lazy val castOptAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    castOptOneAttr(t.tpeS)
  } else {
    castOptManyAttr(t.tpeS)
  }

  lazy val castOptOneAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 13; (colIndex: Int) => q"castOptOne[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 14; (colIndex: Int) => q"castOptOneInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 15; (colIndex: Int) => q"castOptOneLong(row, $colIndex)"
    case "Double"     => lambdaIndex = 16; (colIndex: Int) => q"castOptOneDouble(row, $colIndex)"
    case "Boolean"    => lambdaIndex = 17; (colIndex: Int) => q"castOptOne[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 18; (colIndex: Int) => q"castOptOne[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 19; (colIndex: Int) => q"castOptOne[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 20; (colIndex: Int) => q"castOptOneURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 21; (colIndex: Int) => q"castOptOneBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 22; (colIndex: Int) => q"castOptOneBigDecimal(row, $colIndex)"
    case "enum"       => lambdaIndex = 12; (colIndex: Int) => q"castOptOneEnum(row, $colIndex)"
    case "ref"        => lambdaIndex = 23; (colIndex: Int) => q"castOptOneRefAttr(row, $colIndex)"
  }

  lazy val castOptManyAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 36; (colIndex: Int) => q"castOptMany[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 37; (colIndex: Int) => q"castOptManyInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 38; (colIndex: Int) => q"castOptManyLong(row, $colIndex)"
    case "Double"     => lambdaIndex = 39; (colIndex: Int) => q"castOptManyDouble(row, $colIndex)"
    case "Boolean"    => lambdaIndex = 40; (colIndex: Int) => q"castOptMany[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 41; (colIndex: Int) => q"castOptMany[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 42; (colIndex: Int) => q"castOptMany[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 43; (colIndex: Int) => q"castOptManyURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 44; (colIndex: Int) => q"castOptManyBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 45; (colIndex: Int) => q"castOptManyBigDecimal(row, $colIndex)"
    case "enum"       => lambdaIndex = 35; (colIndex: Int) => q"castOptManyEnum(row, $colIndex)"
    case "ref"        => lambdaIndex = 46; (colIndex: Int) => q"castOptManyRefAttr(row, $colIndex)"
  }

  lazy val castOptEnum: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    lambdaIndex = 12
    (colIndex: Int) => q"castOptOneEnum(row, $colIndex)"
  } else {
    lambdaIndex = 35
    (colIndex: Int) => q"castOptManyEnum(row, $colIndex)"
  }

  lazy val castOptRefAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    lambdaIndex = 23
    (colIndex: Int) => q"castOptOneRefAttr(row, $colIndex)"
  } else {
    lambdaIndex = 46
    (colIndex: Int) => q"castOptManyRefAttr(row, $colIndex)"
  }


  lazy val castMapAttr: richTree => Int => Tree = (t: richTree) => castMapAttr_(t.tpeS)

  lazy val castMapAttr_ : String => Int => Tree = {
    case "String"     => lambdaIndex = 47; (colIndex: Int) => q"castMapString(row, $colIndex)"
    case "Int"        => lambdaIndex = 48; (colIndex: Int) => q"castMapInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 49; (colIndex: Int) => q"castMapLong(row, $colIndex)"
    case "Double"     => lambdaIndex = 50; (colIndex: Int) => q"castMapDouble(row, $colIndex)"
    case "Boolean"    => lambdaIndex = 51; (colIndex: Int) => q"castMapBoolean(row, $colIndex)"
    case "Date"       => lambdaIndex = 52; (colIndex: Int) => q"castMapDate(row, $colIndex)"
    case "UUID"       => lambdaIndex = 53; (colIndex: Int) => q"castMapUUID(row, $colIndex)"
    case "URI"        => lambdaIndex = 54; (colIndex: Int) => q"castMapURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 55; (colIndex: Int) => q"castMapBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 56; (colIndex: Int) => q"castMapBigDecimal(row, $colIndex)"
  }

  lazy val castOptMapAttr: richTree => Int => Tree = (t: richTree) => castOptMapAttr_(t.tpeS)

  lazy val castOptMapAttr_ : String => Int => Tree = {
    case "String"     => lambdaIndex = 57; (colIndex: Int) => q"castOptMapString(row, $colIndex)"
    case "Int"        => lambdaIndex = 58; (colIndex: Int) => q"castOptMapInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 59; (colIndex: Int) => q"castOptMapLong(row, $colIndex)"
    case "Double"     => lambdaIndex = 60; (colIndex: Int) => q"castOptMapDouble(row, $colIndex)"
    case "Boolean"    => lambdaIndex = 61; (colIndex: Int) => q"castOptMapBoolean(row, $colIndex)"
    case "Date"       => lambdaIndex = 62; (colIndex: Int) => q"castOptMapDate(row, $colIndex)"
    case "UUID"       => lambdaIndex = 63; (colIndex: Int) => q"castOptMapUUID(row, $colIndex)"
    case "URI"        => lambdaIndex = 64; (colIndex: Int) => q"castOptMapURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 65; (colIndex: Int) => q"castOptMapBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 66; (colIndex: Int) => q"castOptMapBigDecimal(row, $colIndex)"
  }


  lazy val castOptApplyAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    castOptApplyOneAttr(t.tpeS)
  } else {
    castOptApplyManyAttr(t.tpeS)
  }

  lazy val castOptApplyOneAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 67; (colIndex: Int) => q"castOptApplyOne[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 68; (colIndex: Int) => q"castOptApplyOneInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 69; (colIndex: Int) => q"castOptApplyOneLong(row, $colIndex)"
    case "Double"     => lambdaIndex = 70; (colIndex: Int) => q"castOptApplyOneDouble(row, $colIndex)"
    case "Boolean"    => lambdaIndex = 71; (colIndex: Int) => q"castOptApplyOne[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 72; (colIndex: Int) => q"castOptApplyOne[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 73; (colIndex: Int) => q"castOptApplyOne[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 74; (colIndex: Int) => q"castOptApplyOneURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 75; (colIndex: Int) => q"castOptApplyOneBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 76; (colIndex: Int) => q"castOptApplyOneBigDecimal(row, $colIndex)"
    case "ref"        => lambdaIndex = 69; (colIndex: Int) => q"castOptApplyOneLong(row, $colIndex)"
  }

  lazy val castOptApplyManyAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 77; (colIndex: Int) => q"castOptApplyMany[String](row, $colIndex)"
    case "Int"        => lambdaIndex = 78; (colIndex: Int) => q"castOptApplyManyInt(row, $colIndex)"
    case "Long"       => lambdaIndex = 79; (colIndex: Int) => q"castOptApplyManyLong(row, $colIndex)"
    case "Double"     => lambdaIndex = 80; (colIndex: Int) => q"castOptApplyManyDouble(row, $colIndex)"
    case "Boolean"    => lambdaIndex = 81; (colIndex: Int) => q"castOptApplyMany[Boolean](row, $colIndex)"
    case "Date"       => lambdaIndex = 82; (colIndex: Int) => q"castOptApplyMany[Date](row, $colIndex)"
    case "UUID"       => lambdaIndex = 83; (colIndex: Int) => q"castOptApplyMany[UUID](row, $colIndex)"
    case "URI"        => lambdaIndex = 84; (colIndex: Int) => q"castOptApplyManyURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 85; (colIndex: Int) => q"castOptApplyManyBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 86; (colIndex: Int) => q"castOptApplyManyBigDecimal(row, $colIndex)"
    case "ref"        => lambdaIndex = 79; (colIndex: Int) => q"castOptApplyManyLong(row, $colIndex)"
  }

  lazy val castOptApplyMapAttr: richTree => Int => Tree = (t: richTree) => castOptApplyMapAttr_(t.tpeS)

  lazy val castOptApplyMapAttr_ : String => Int => Tree = {
    case "String"     => lambdaIndex = 87; (colIndex: Int) => q"castOptApplyMapString(row, $colIndex)"
    case "Int"        => lambdaIndex = 88; (colIndex: Int) => q"castOptApplyMapInt(row, $colIndex)"
    case "Boolean"    => lambdaIndex = 89; (colIndex: Int) => q"castOptApplyMapBoolean(row, $colIndex)"
    case "Long"       => lambdaIndex = 90; (colIndex: Int) => q"castOptApplyMapLong(row, $colIndex)"
    case "Double"     => lambdaIndex = 91; (colIndex: Int) => q"castOptApplyMapDouble(row, $colIndex)"
    case "Date"       => lambdaIndex = 92; (colIndex: Int) => q"castOptApplyMapDate(row, $colIndex)"
    case "UUID"       => lambdaIndex = 93; (colIndex: Int) => q"castOptApplyMapUUID(row, $colIndex)"
    case "URI"        => lambdaIndex = 94; (colIndex: Int) => q"castOptApplyMapURI(row, $colIndex)"
    case "BigInt"     => lambdaIndex = 95; (colIndex: Int) => q"castOptApplyMapBigInt(row, $colIndex)"
    case "BigDecimal" => lambdaIndex = 96; (colIndex: Int) => q"castOptApplyMapBigDecimal(row, $colIndex)"
  }

  lazy val castKeyedMapAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 97; (colIndex: Int) => q"row.get($colIndex).toString"
    case "Int"        => lambdaIndex = 98; (colIndex: Int) => q"row.get($colIndex).toString.toInt"
    case "Long"       => lambdaIndex = 99; (colIndex: Int) => q"row.get($colIndex).toString.toLong"
    case "Double"     => lambdaIndex = 100; (colIndex: Int) => q"row.get($colIndex).toString.toDouble"
    case "Boolean"    => lambdaIndex = 101; (colIndex: Int) => q"row.get($colIndex).toString.toBoolean"
    case "Date"       => lambdaIndex = 102; (colIndex: Int) => q"molecule.core.util.fns.str2date(row.get($colIndex).toString)"
    case "UUID"       => lambdaIndex = 103; (colIndex: Int) => q"java.util.UUID.fromString(row.get($colIndex).toString)"
    case "URI"        => lambdaIndex = 104; (colIndex: Int) => q"new java.net.URI(row.get($colIndex).toString)"
    case "BigInt"     => lambdaIndex = 105; (colIndex: Int) => q"BigInt(row.get($colIndex).toString)"
    case "BigDecimal" => lambdaIndex = 106; (colIndex: Int) => q"BigDecimal(row.get($colIndex).toString)"
    case "Any"        => lambdaIndex = 107; (colIndex: Int) => q"row.get($colIndex)"
  }


  // Aggregates..............

  lazy val castAggrOneList: String => Int => Tree = {
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

  lazy val castAggrManyList: String => Int => Tree = {
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

  lazy val castAggrOneListDistinct: String => Int => Tree = {
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

  lazy val castAggrManyListDistinct: String => Int => Tree = {
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

  lazy val castAggrOneListRand: String => Int => Tree = {
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

  lazy val castAggrManyListRand: String => Int => Tree = {
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

  lazy val castAggrSingleSample: String => Int => Tree = {
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

  lazy val castAggrOneSingle: String => Int => Tree = {
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

  lazy val castAggrManySingle: String => Int => Tree = {
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
