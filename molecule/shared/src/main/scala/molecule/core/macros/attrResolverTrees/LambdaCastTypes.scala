package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaCastTypes extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("LambdaCast", 1)

  val castOneAttr: String => Int => Tree = {
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
  }

  val castManyAttr: String => Int => Tree = {
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
  }

  val castAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOneAttr(t.tpeS) else castManyAttr(t.tpeS)


  val castOptAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    // Optional, card one
    t.tpeS match {
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
    }
  } else {
    // Optional, card many
    t.tpeS match {
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
    }
  }

  val castOptApplyAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    // Optional, card one
    t.tpeS match {
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
    }
  } else {
    // Optional, card many
    t.tpeS match {
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
    }
  }

  val castOptRefAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    lambdaIndex = 23
    (colIndex: Int) => q"castOptOneRefAttr(row, $colIndex)"
  } else {
    lambdaIndex = 46
    (colIndex: Int) => q"castOptManyRefAttr(row, $colIndex)"
  }

  val castEnum: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    lambdaIndex = 0
    (colIndex: Int) => q"row.get($colIndex).asInstanceOf[String]"
  } else {
    lambdaIndex = 24
    (colIndex: Int) => q"castMany[String](row, $colIndex)"
  }

  val castOptEnum: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    lambdaIndex = 12
    (colIndex: Int) => q"castOptOneEnum(row, $colIndex)"
  } else {
    lambdaIndex = 35
    (colIndex: Int) => q"castOptManyEnum(row, $colIndex)"
  }

  val castMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
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

  val castOptMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
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

  val castOptApplyMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
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

  val castKeyedMapAttr: String => Int => Tree = {
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
}
