package molecule.core.macros

import scala.reflect.macros.blackbox

private[molecule] trait Cast extends CastAggr {
  val c: blackbox.Context

  import c.universe._

  lazy val y = InspectMacro("Cast", 1)

  val castOneAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Int2"       => (colIndex: Int) => q"castOneInt2(row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castOneFloat(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
    case "Any"        => (colIndex: Int) => q"row.get($colIndex)"
  }

  val castManyAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castMany[String](row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castManyInt(row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castManyFloat(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castMany[Long](row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castMany[Double](row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castMany[Boolean](row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castMany[Date](row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castMany[UUID](row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castManyURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castManyBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castManyBigDecimal(row, $colIndex)"
  }

  val castMandatoryAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOneAttr(t.tpeS) else castManyAttr(t.tpeS)

  val castOptionalAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"castOptOne[String](row, $colIndex)"
        case "Int"        => (colIndex: Int) => q"castOptOneInt(row, $colIndex)"
        case "Float"      => (colIndex: Int) => q"castOptOneFloat(row, $colIndex)"
        case "Long"       => (colIndex: Int) => q"castOptOneLong(row, $colIndex)"
        case "Double"     => (colIndex: Int) => q"castOptOneDouble(row, $colIndex)"
        case "Boolean"    => (colIndex: Int) => q"castOptOne[Boolean](row, $colIndex)"
        case "Date"       => (colIndex: Int) => q"castOptOne[Date](row, $colIndex)"
        case "UUID"       => (colIndex: Int) => q"castOptOne[UUID](row, $colIndex)"
        case "URI"        => (colIndex: Int) => q"castOptOneURI(row, $colIndex)"
        case "BigInt"     => (colIndex: Int) => q"castOptOneBigInt(row, $colIndex)"
        case "BigDecimal" => (colIndex: Int) => q"castOptOneBigDecimal(row, $colIndex)"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"castOptMany[String](row, $colIndex)"
        case "Int"        => (colIndex: Int) => q"castOptManyInt(row, $colIndex)"
        case "Float"      => (colIndex: Int) => q"castOptManyFloat(row, $colIndex)"
        case "Long"       => (colIndex: Int) => q"castOptManyLong(row, $colIndex)"
        case "Double"     => (colIndex: Int) => q"castOptManyDouble(row, $colIndex)"
        case "Boolean"    => (colIndex: Int) => q"castOptMany[Boolean](row, $colIndex)"
        case "Date"       => (colIndex: Int) => q"castOptMany[Date](row, $colIndex)"
        case "UUID"       => (colIndex: Int) => q"castOptMany[UUID](row, $colIndex)"
        case "URI"        => (colIndex: Int) => q"castOptManyURI(row, $colIndex)"
        case "BigInt"     => (colIndex: Int) => q"castOptManyBigInt(row, $colIndex)"
        case "BigDecimal" => (colIndex: Int) => q"castOptManyBigDecimal(row, $colIndex)"
      }
    }

  val castOptionalApplyAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    // Optional, card one
    t.tpeS match {
      case "String"     => (colIndex: Int) => q"castOptApplyOne[String](row, $colIndex)"
      case "Int"        => (colIndex: Int) => q"castOptApplyOneInt(row, $colIndex)"
      case "Float"      => (colIndex: Int) => q"castOptApplyOneFloat(row, $colIndex)"
      case "Long"       => (colIndex: Int) => q"castOptApplyOneLong(row, $colIndex)"
      case "Double"     => (colIndex: Int) => q"castOptApplyOneDouble(row, $colIndex)"
      case "Boolean"    => (colIndex: Int) => q"castOptApplyOne[Boolean](row, $colIndex)"
      case "Date"       => (colIndex: Int) => q"castOptApplyOne[Date](row, $colIndex)"
      case "UUID"       => (colIndex: Int) => q"castOptApplyOne[UUID](row, $colIndex)"
      case "URI"        => (colIndex: Int) => q"castOptApplyOneURI(row, $colIndex)"
      case "BigInt"     => (colIndex: Int) => q"castOptApplyOneBigInt(row, $colIndex)"
      case "BigDecimal" => (colIndex: Int) => q"castOptApplyOneBigDecimal(row, $colIndex)"
    }
  } else {
    // Optional, card many
    t.tpeS match {
      case "String"     => (colIndex: Int) => q"castOptApplyMany[String](row, $colIndex)"
      case "Int"        => (colIndex: Int) => q"castOptApplyManyInt(row, $colIndex)"
      case "Float"      => (colIndex: Int) => q"castOptApplyManyFloat(row, $colIndex)"
      case "Long"       => (colIndex: Int) => q"castOptApplyManyLong(row, $colIndex)"
      case "Double"     => (colIndex: Int) => q"castOptApplyManyDouble(row, $colIndex)"
      case "Boolean"    => (colIndex: Int) => q"castOptApplyMany[Boolean](row, $colIndex)"
      case "Date"       => (colIndex: Int) => q"castOptApplyMany[Date](row, $colIndex)"
      case "UUID"       => (colIndex: Int) => q"castOptApplyMany[UUID](row, $colIndex)"
      case "URI"        => (colIndex: Int) => q"castOptApplyManyURI(row, $colIndex)"
      case "BigInt"     => (colIndex: Int) => q"castOptApplyManyBigInt(row, $colIndex)"
      case "BigDecimal" => (colIndex: Int) => q"castOptApplyManyBigDecimal(row, $colIndex)"
    }
  }

  val castOptionalRefAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1)
    (colIndex: Int) => q"castOptOneRefAttr(row, $colIndex)"
  else
    (colIndex: Int) => q"castOptManyRefAttr(row, $colIndex)"

  val castEnum: richTree => Int => Tree = (t: richTree) => if (t.card == 1)
    (colIndex: Int) => q"row.get($colIndex).asInstanceOf[String]"
  else
    (colIndex: Int) => q"castMany[String](row, $colIndex)"

  val castEnumOpt: richTree => Int => Tree = (t: richTree) => if (t.card == 1)
    (colIndex: Int) => q"castOptOneEnum(row, $colIndex)"
  else
    (colIndex: Int) => q"castOptManyEnum(row, $colIndex)"

  val castMandatoryMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (colIndex: Int) => q"castMapString(row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castMapInt(row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castMapFloat(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castMapLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castMapDouble(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castMapBoolean(row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castMapDate(row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castMapUUID(row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castMapURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castMapBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castMapBigDecimal(row, $colIndex)"
  }

  val castOptionalMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (colIndex: Int) => q"castOptMapString(row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOptMapInt(row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castOptMapFloat(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOptMapLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOptMapDouble(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOptMapBoolean(row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOptMapDate(row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOptMapUUID(row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOptMapURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOptMapBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOptMapBigDecimal(row, $colIndex)"
  }

  val castOptionalApplyMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (colIndex: Int) => q"castOptApplyMapString(row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"castOptApplyMapInt(row, $colIndex)"
    case "Float"      => (colIndex: Int) => q"castOptApplyMapFloat(row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"castOptApplyMapBoolean(row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"castOptApplyMapLong(row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"castOptApplyMapDouble(row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"castOptApplyMapDate(row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"castOptApplyMapUUID(row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"castOptApplyMapURI(row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"castOptApplyMapBigInt(row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"castOptApplyMapBigDecimal(row, $colIndex)"
  }

  val castKeyedMapAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"row.get($colIndex).toString"
    case "Int"        => (colIndex: Int) => q"row.get($colIndex).toString.toInt"
    case "Float"      => (colIndex: Int) => q"row.get($colIndex).toString.toFloat"
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
}
