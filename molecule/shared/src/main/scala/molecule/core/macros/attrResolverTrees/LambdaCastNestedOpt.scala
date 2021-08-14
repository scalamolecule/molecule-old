package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaCastNestedOpt extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val castNestedOptOneAttr: String => Int => Tree = {
    case "String"     => ii = (0, 0); (_: Int) => q"castNestedOptOne[String](it)"
    case "Int"        => ii = (1, 1); (_: Int) => q"castNestedOptOneInt(it)"
    case "Long"       => ii = (3, 2); (_: Int) => q"castNestedOptOne[Long](it)"
    case "Double"     => ii = (4, 3); (_: Int) => q"castNestedOptOne[Double](it)"
    case "Boolean"    => ii = (5, 4); (_: Int) => q"castNestedOptOne[Boolean](it)"
    case "Date"       => ii = (6, 5); (_: Int) => q"castNestedOptOne[Date](it)"
    case "UUID"       => ii = (7, 6); (_: Int) => q"castNestedOptOne[UUID](it)"
    case "URI"        => ii = (8, 7); (_: Int) => q"castNestedOptOne[URI](it)"
    case "BigInt"     => ii = (9, 8); (_: Int) => q"castNestedOptOneBigInt(it)"
    case "BigDecimal" => ii = (10, 9); (_: Int) => q"castNestedOptOneBigDecimal(it)"
    case "Any"        => ii = (11, 0); (colIndex: Int) => q"row.get($colIndex)" // todo?
  }

  val castNestedOptManyAttr: String => Int => Tree = {
    case "String"     => ii = (25, 20); (_: Int) => q"castNestedOptMany[String](it)"
    case "Int"        => ii = (26, 21); (_: Int) => q"castNestedOptManyInt(it)"
    case "Long"       => ii = (27, 22); (_: Int) => q"castNestedOptMany[Long](it)"
    case "Double"     => ii = (28, 23); (_: Int) => q"castNestedOptMany[Double](it)"
    case "Boolean"    => ii = (29, 24); (_: Int) => q"castNestedOptMany[Boolean](it)"
    case "Date"       => ii = (30, 25); (_: Int) => q"castNestedOptMany[Date](it)"
    case "UUID"       => ii = (31, 26); (_: Int) => q"castNestedOptMany[UUID](it)"
    case "URI"        => ii = (32, 27); (_: Int) => q"castNestedOptMany[URI](it)"
    case "BigInt"     => ii = (33, 28); (_: Int) => q"castNestedOptManyBigInt(it)"
    case "BigDecimal" => ii = (34, 29); (_: Int) => q"castNestedOptManyBigDecimal(it)"
  }

  val castNestedOptAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castNestedOptOneAttr(t.tpeS) else castNestedOptManyAttr(t.tpeS)

  val castNestedOptRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      ii = (3, 2)
      (_: Int) => q"castNestedOptOneRefAttr(it)"
    } else {
      ii = (27, 22)
      (_: Int) => q"castNestedOptManyRefAttr(it)"
    }

  val castNestedOptEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      ii = (0, 0)
      (_: Int) => q"castNestedOptOneEnum(it)"
    } else {
      ii = (24, 20)
      (_: Int) => q"castNestedOptManyEnum(it)"
    }


  val castNestedOptOptAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => ii = (13, 10); (_: Int) => q"castNestedOptOptOne[String](it)"
        case "Int"        => ii = (14, 11); (_: Int) => q"castNestedOptOptOneInt(it)"
        case "Long"       => ii = (15, 12); (_: Int) => q"castNestedOptOptOneLong(it)"
        case "Double"     => ii = (16, 13); (_: Int) => q"castNestedOptOptOneDouble(it)"
        case "Boolean"    => ii = (17, 14); (_: Int) => q"castNestedOptOptOne[Boolean](it)"
        case "Date"       => ii = (18, 15); (_: Int) => q"castNestedOptOptOne[Date](it)"
        case "UUID"       => ii = (19, 16); (_: Int) => q"castNestedOptOptOne[UUID](it)"
        case "URI"        => ii = (20, 17); (_: Int) => q"castNestedOptOptOneURI(it)"
        case "BigInt"     => ii = (21, 18); (_: Int) => q"castNestedOptOptOneBigInt(it)"
        case "BigDecimal" => ii = (22, 19); (_: Int) => q"castNestedOptOptOneBigDecimal(it)"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => ii = (36, 30); (_: Int) => q"castNestedOptOptMany[String](it)"
        case "Int"        => ii = (37, 31); (_: Int) => q"castNestedOptOptManyInt(it)"
        case "Long"       => ii = (38, 32); (_: Int) => q"castNestedOptOptManyLong(it)"
        case "Double"     => ii = (39, 33); (_: Int) => q"castNestedOptOptManyDouble(it)"
        case "Boolean"    => ii = (40, 34); (_: Int) => q"castNestedOptOptMany[Boolean](it)"
        case "Date"       => ii = (41, 35); (_: Int) => q"castNestedOptOptMany[Date](it)"
        case "UUID"       => ii = (42, 36); (_: Int) => q"castNestedOptOptMany[UUID](it)"
        case "URI"        => ii = (43, 37); (_: Int) => q"castNestedOptOptManyURI(it)"
        case "BigInt"     => ii = (44, 38); (_: Int) => q"castNestedOptOptManyBigInt(it)"
        case "BigDecimal" => ii = (45, 39); (_: Int) => q"castNestedOptOptManyBigDecimal(it)"
      }
    }


  val castNestedOptOptRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      ii = (15, 12)
      (_: Int) => q"castNestedOptOptOneRefAttr(it)"
    } else {
      ii = (38, 32)
      (_: Int) => q"castNestedOptOptManyRefAttr(it)"
    }


  val castNestedOptOptEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      ii = (12, 10)
      (_: Int) => q"castNestedOptOptOneEnum(it)"
    } else {
      ii = (35, 30)
      (_: Int) => q"castNestedOptOptManyEnum(it)"
    }


  val castNestedOptMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => ii = (47, 40); (_: Int) => q"castNestedOptMapString(it)"
    case "Int"        => ii = (48, 41); (_: Int) => q"castNestedOptMapInt(it)"
    case "Long"       => ii = (49, 42); (_: Int) => q"castNestedOptMapLong(it)"
    case "Double"     => ii = (50, 43); (_: Int) => q"castNestedOptMapDouble(it)"
    case "Boolean"    => ii = (51, 44); (_: Int) => q"castNestedOptMapBoolean(it)"
    case "Date"       => ii = (52, 45); (_: Int) => q"castNestedOptMapDate(it)"
    case "UUID"       => ii = (53, 46); (_: Int) => q"castNestedOptMapUUID(it)"
    case "URI"        => ii = (54, 47); (_: Int) => q"castNestedOptMapURI(it)"
    case "BigInt"     => ii = (55, 48); (_: Int) => q"castNestedOptMapBigInt(it)"
    case "BigDecimal" => ii = (56, 49); (_: Int) => q"castNestedOptMapBigDecimal(it)"
  }

  val castNestedOptOptMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => ii = (57, 50); (_: Int) => q"castNestedOptOptMapString(it)"
    case "Int"        => ii = (58, 51); (_: Int) => q"castNestedOptOptMapInt(it)"
    case "Long"       => ii = (59, 52); (_: Int) => q"castNestedOptOptMapLong(it)"
    case "Double"     => ii = (60, 53); (_: Int) => q"castNestedOptOptMapDouble(it)"
    case "Boolean"    => ii = (61, 54); (_: Int) => q"castNestedOptOptMapBoolean(it)"
    case "Date"       => ii = (62, 55); (_: Int) => q"castNestedOptOptMapDate(it)"
    case "UUID"       => ii = (63, 56); (_: Int) => q"castNestedOptOptMapUUID(it)"
    case "URI"        => ii = (64, 57); (_: Int) => q"castNestedOptOptMapURI(it)"
    case "BigInt"     => ii = (65, 58); (_: Int) => q"castNestedOptOptMapBigInt(it)"
    case "BigDecimal" => ii = (66, 59); (_: Int) => q"castNestedOptOptMapBigDecimal(it)"
  }
}
