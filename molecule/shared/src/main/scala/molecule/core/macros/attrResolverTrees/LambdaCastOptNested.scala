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
    case "String"     => ii = (25, 20); (_: Int) => q"castOptNestedMany[String](it)"
    case "Int"        => ii = (26, 21); (_: Int) => q"castOptNestedManyInt(it)"
    case "Long"       => ii = (27, 22); (_: Int) => q"castOptNestedMany[Long](it)"
    case "Double"     => ii = (28, 23); (_: Int) => q"castOptNestedMany[Double](it)"
    case "Boolean"    => ii = (29, 24); (_: Int) => q"castOptNestedMany[Boolean](it)"
    case "Date"       => ii = (30, 25); (_: Int) => q"castOptNestedMany[Date](it)"
    case "UUID"       => ii = (31, 26); (_: Int) => q"castOptNestedMany[UUID](it)"
    case "URI"        => ii = (32, 27); (_: Int) => q"castOptNestedMany[URI](it)"
    case "BigInt"     => ii = (33, 28); (_: Int) => q"castOptNestedManyBigInt(it)"
    case "BigDecimal" => ii = (34, 29); (_: Int) => q"castOptNestedManyBigDecimal(it)"
  }

  val castOptNestedAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOptNestedOneAttr(t.tpeS) else castOptNestedManyAttr(t.tpeS)

  val castOptNestedRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      ii = (3, 2)
      (_: Int) => q"castOptNestedOneRefAttr(it)"
    } else {
      ii = (27, 22)
      (_: Int) => q"castOptNestedManyRefAttr(it)"
    }

  val castOptNestedEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      ii = (0, 0)
      (_: Int) => q"castOptNestedOneEnum(it)"
    } else {
      ii = (24, 20)
      (_: Int) => q"castOptNestedManyEnum(it)"
    }


  val castOptNestedOptAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => ii = (13, 10); (_: Int) => q"castOptNestedOptOne[String](it)"
        case "Int"        => ii = (14, 11); (_: Int) => q"castOptNestedOptOneInt(it)"
        case "Long"       => ii = (15, 12); (_: Int) => q"castOptNestedOptOneLong(it)"
        case "Double"     => ii = (16, 13); (_: Int) => q"castOptNestedOptOneDouble(it)"
        case "Boolean"    => ii = (17, 14); (_: Int) => q"castOptNestedOptOne[Boolean](it)"
        case "Date"       => ii = (18, 15); (_: Int) => q"castOptNestedOptOne[Date](it)"
        case "UUID"       => ii = (19, 16); (_: Int) => q"castOptNestedOptOne[UUID](it)"
        case "URI"        => ii = (20, 17); (_: Int) => q"castOptNestedOptOneURI(it)"
        case "BigInt"     => ii = (21, 18); (_: Int) => q"castOptNestedOptOneBigInt(it)"
        case "BigDecimal" => ii = (22, 19); (_: Int) => q"castOptNestedOptOneBigDecimal(it)"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => ii = (36, 30); (_: Int) => q"castOptNestedOptMany[String](it)"
        case "Int"        => ii = (37, 31); (_: Int) => q"castOptNestedOptManyInt(it)"
        case "Long"       => ii = (38, 32); (_: Int) => q"castOptNestedOptManyLong(it)"
        case "Double"     => ii = (39, 33); (_: Int) => q"castOptNestedOptManyDouble(it)"
        case "Boolean"    => ii = (40, 34); (_: Int) => q"castOptNestedOptMany[Boolean](it)"
        case "Date"       => ii = (41, 35); (_: Int) => q"castOptNestedOptMany[Date](it)"
        case "UUID"       => ii = (42, 36); (_: Int) => q"castOptNestedOptMany[UUID](it)"
        case "URI"        => ii = (43, 37); (_: Int) => q"castOptNestedOptManyURI(it)"
        case "BigInt"     => ii = (44, 38); (_: Int) => q"castOptNestedOptManyBigInt(it)"
        case "BigDecimal" => ii = (45, 39); (_: Int) => q"castOptNestedOptManyBigDecimal(it)"
      }
    }


  val castOptNestedOptRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      ii = (15, 12)
      (_: Int) => q"castOptNestedOptOneRefAttr(it)"
    } else {
      ii = (38, 32)
      (_: Int) => q"castOptNestedOptManyRefAttr(it)"
    }


  val castOptNestedOptEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      ii = (12, 10)
      (_: Int) => q"castOptNestedOptOneEnum(it)"
    } else {
      ii = (35, 30)
      (_: Int) => q"castOptNestedOptManyEnum(it)"
    }


  val castOptNestedMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => ii = (47, 40); (_: Int) => q"castOptNestedMapString(it)"
    case "Int"        => ii = (48, 41); (_: Int) => q"castOptNestedMapInt(it)"
    case "Long"       => ii = (49, 42); (_: Int) => q"castOptNestedMapLong(it)"
    case "Double"     => ii = (50, 43); (_: Int) => q"castOptNestedMapDouble(it)"
    case "Boolean"    => ii = (51, 44); (_: Int) => q"castOptNestedMapBoolean(it)"
    case "Date"       => ii = (52, 45); (_: Int) => q"castOptNestedMapDate(it)"
    case "UUID"       => ii = (53, 46); (_: Int) => q"castOptNestedMapUUID(it)"
    case "URI"        => ii = (54, 47); (_: Int) => q"castOptNestedMapURI(it)"
    case "BigInt"     => ii = (55, 48); (_: Int) => q"castOptNestedMapBigInt(it)"
    case "BigDecimal" => ii = (56, 49); (_: Int) => q"castOptNestedMapBigDecimal(it)"
  }

  val castOptNestedOptMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => ii = (57, 50); (_: Int) => q"castOptNestedOptMapString(it)"
    case "Int"        => ii = (58, 51); (_: Int) => q"castOptNestedOptMapInt(it)"
    case "Long"       => ii = (59, 52); (_: Int) => q"castOptNestedOptMapLong(it)"
    case "Double"     => ii = (60, 53); (_: Int) => q"castOptNestedOptMapDouble(it)"
    case "Boolean"    => ii = (61, 54); (_: Int) => q"castOptNestedOptMapBoolean(it)"
    case "Date"       => ii = (62, 55); (_: Int) => q"castOptNestedOptMapDate(it)"
    case "UUID"       => ii = (63, 56); (_: Int) => q"castOptNestedOptMapUUID(it)"
    case "URI"        => ii = (64, 57); (_: Int) => q"castOptNestedOptMapURI(it)"
    case "BigInt"     => ii = (65, 58); (_: Int) => q"castOptNestedOptMapBigInt(it)"
    case "BigDecimal" => ii = (66, 59); (_: Int) => q"castOptNestedOptMapBigDecimal(it)"
  }
}
