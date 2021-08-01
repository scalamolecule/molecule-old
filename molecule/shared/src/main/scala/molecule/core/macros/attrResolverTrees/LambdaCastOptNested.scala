package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaCastOptNested extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val castOptNestedOneAttr: String => Int => Tree = {
    case "String"     => ii = (0, 0); (_: Int) => q"castOptNestedOne[String](it)"
    case "Int"        => ii = (1, 1); (_: Int) => q"castOptNestedOneInt(it)"
    case "Long"       => ii = (3, 2); (_: Int) => q"castOptNestedOne[Long](it)"
    case "Double"     => ii = (4, 3); (_: Int) => q"castOptNestedOne[Double](it)"
    case "Boolean"    => ii = (5, 4); (_: Int) => q"castOptNestedOne[Boolean](it)"
    case "Date"       => ii = (6, 5); (_: Int) => q"castOptNestedOne[Date](it)"
    case "UUID"       => ii = (7, 6); (_: Int) => q"castOptNestedOne[UUID](it)"
    case "URI"        => ii = (8, 7); (_: Int) => q"castOptNestedOne[URI](it)"
    case "BigInt"     => ii = (9, 8); (_: Int) => q"castOptNestedOneBigInt(it)"
    case "BigDecimal" => ii = (10, 9); (_: Int) => q"castOptNestedOneBigDecimal(it)"
    case "Any"        => ii = (11, 0); (colIndex: Int) => q"row.get($colIndex)" // todo?
  }

  val castOptNestedManyAttr: String => Int => Tree = {
    case "String"     => (_: Int) => q"castOptNestedMany[String](it)"
    case "Int"        => (_: Int) => q"castOptNestedManyInt(it)"
    case "Long"       => (_: Int) => q"castOptNestedMany[Long](it)"
    case "Double"     => (_: Int) => q"castOptNestedMany[Double](it)"
    case "Boolean"    => (_: Int) => q"castOptNestedMany[Boolean](it)"
    case "Date"       => (_: Int) => q"castOptNestedMany[Date](it)"
    case "UUID"       => (_: Int) => q"castOptNestedMany[UUID](it)"
    case "URI"        => (_: Int) => q"castOptNestedMany[URI](it)"
    case "BigInt"     => (_: Int) => q"castOptNestedManyBigInt(it)"
    case "BigDecimal" => (_: Int) => q"castOptNestedManyBigDecimal(it)"
  }

  val castOptNestedAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOptNestedOneAttr(t.tpeS) else castOptNestedManyAttr(t.tpeS)

  val castOptNestedRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (_: Int) => q"castOptNestedOneRefAttr(it)"
    else
      (_: Int) => q"castOptNestedManyRefAttr(it)"

  val castOptNestedEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      (_: Int) => q"castOptNestedOneEnum(it)"
    } else
      (_: Int) => q"castOptNestedManyEnum(it)"


  val castOptNestedOptAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (_: Int) => q"castOptNestedOptOne[String](it)"
        case "Int"        => (_: Int) => q"castOptNestedOptOneInt(it)"
        case "Long"       => (_: Int) => q"castOptNestedOptOneLong(it)"
        case "Double"     => (_: Int) => q"castOptNestedOptOneDouble(it)"
        case "Boolean"    => (_: Int) => q"castOptNestedOptOne[Boolean](it)"
        case "Date"       => (_: Int) => q"castOptNestedOptOne[Date](it)"
        case "UUID"       => (_: Int) => q"castOptNestedOptOne[UUID](it)"
        case "URI"        => (_: Int) => q"castOptNestedOptOneURI(it)"
        case "BigInt"     => (_: Int) => q"castOptNestedOptOneBigInt(it)"
        case "BigDecimal" => (_: Int) => q"castOptNestedOptOneBigDecimal(it)"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (_: Int) => q"castOptNestedOptMany[String](it)"
        case "Int"        => (_: Int) => q"castOptNestedOptManyInt(it)"
        case "Long"       => (_: Int) => q"castOptNestedOptManyLong(it)"
        case "Double"     => (_: Int) => q"castOptNestedOptManyDouble(it)"
        case "Boolean"    => (_: Int) => q"castOptNestedOptMany[Boolean](it)"
        case "Date"       => (_: Int) => q"castOptNestedOptMany[Date](it)"
        case "UUID"       => (_: Int) => q"castOptNestedOptMany[UUID](it)"
        case "URI"        => (_: Int) => q"castOptNestedOptManyURI(it)"
        case "BigInt"     => (_: Int) => q"castOptNestedOptManyBigInt(it)"
        case "BigDecimal" => (_: Int) => q"castOptNestedOptManyBigDecimal(it)"
      }
    }


  val castOptNestedOptRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (_: Int) => q"castOptNestedOptOneRefAttr(it)"
    else
      (_: Int) => q"castOptNestedOptManyRefAttr(it)"


  val castOptNestedOptEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (_: Int) => q"castOptNestedOptOneEnum(it)"
    else
      (_: Int) => q"castOptNestedOptManyEnum(it)"


  val castOptNestedMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (_: Int) => q"castOptNestedMapString(it)"
    case "Int"        => (_: Int) => q"castOptNestedMapInt(it)"
    case "Long"       => (_: Int) => q"castOptNestedMapLong(it)"
    case "Double"     => (_: Int) => q"castOptNestedMapDouble(it)"
    case "Boolean"    => (_: Int) => q"castOptNestedMapBoolean(it)"
    case "Date"       => (_: Int) => q"castOptNestedMapDate(it)"
    case "UUID"       => (_: Int) => q"castOptNestedMapUUID(it)"
    case "URI"        => (_: Int) => q"castOptNestedMapURI(it)"
    case "BigInt"     => (_: Int) => q"castOptNestedMapBigInt(it)"
    case "BigDecimal" => (_: Int) => q"castOptNestedMapBigDecimal(it)"
  }

  val castOptNestedOptMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (_: Int) => q"castOptNestedOptMapString(it)"
    case "Int"        => (_: Int) => q"castOptNestedOptMapInt(it)"
    case "Long"       => (_: Int) => q"castOptNestedOptMapLong(it)"
    case "Double"     => (_: Int) => q"castOptNestedOptMapDouble(it)"
    case "Boolean"    => (_: Int) => q"castOptNestedOptMapBoolean(it)"
    case "Date"       => (_: Int) => q"castOptNestedOptMapDate(it)"
    case "UUID"       => (_: Int) => q"castOptNestedOptMapUUID(it)"
    case "URI"        => (_: Int) => q"castOptNestedOptMapURI(it)"
    case "BigInt"     => (_: Int) => q"castOptNestedOptMapBigInt(it)"
    case "BigDecimal" => (_: Int) => q"castOptNestedOptMapBigDecimal(it)"
  }
}
