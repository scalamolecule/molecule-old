package molecule.core.macros.cast

import scala.reflect.macros.blackbox

private[molecule] trait CastTypes extends CastAggr {
  val c: blackbox.Context

  import c.universe._

  lazy val y = InspectMacro("Cast", 1)


  val castOneAttr: String => Int => Tree = {
    case "String"     => ci = (0, 0); (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => ci = (1, 1); (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Int2"       => ci = (2, 1); (colIndex: Int) => q"castOneInt2(row, $colIndex)"
    case "Float"      => ci = (3, 2); (colIndex: Int) => q"castOneFloat(row, $colIndex)"
    case "Long"       => ci = (4, 3); (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Double"     => ci = (5, 4); (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => ci = (6, 5); (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => ci = (7, 6); (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => ci = (8, 7); (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => ci = (9, 8); (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => ci = (10, 9); (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => ci = (11, 10); (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
    case "Any"        => ci = (12, 0); (colIndex: Int) => q"row.get($colIndex)"
  }

  val castManyAttr: String => Int => Tree = {
    case "String"     => ci = (27, 22); (colIndex: Int) => q"castMany[String](row, $colIndex)"
    case "Int"        => ci = (28, 23); (colIndex: Int) => q"castManyInt(row, $colIndex)"
    case "Float"      => ci = (29, 24); (colIndex: Int) => q"castManyFloat(row, $colIndex)"
    case "Long"       => ci = (30, 25); (colIndex: Int) => q"castMany[Long](row, $colIndex)"
    case "Double"     => ci = (31, 26); (colIndex: Int) => q"castMany[Double](row, $colIndex)"
    case "Boolean"    => ci = (32, 27); (colIndex: Int) => q"castMany[Boolean](row, $colIndex)"
    case "Date"       => ci = (33, 28); (colIndex: Int) => q"castMany[Date](row, $colIndex)"
    case "UUID"       => ci = (34, 29); (colIndex: Int) => q"castMany[UUID](row, $colIndex)"
    case "URI"        => ci = (35, 30); (colIndex: Int) => q"castManyURI(row, $colIndex)"
    case "BigInt"     => ci = (36, 31); (colIndex: Int) => q"castManyBigInt(row, $colIndex)"
    case "BigDecimal" => ci = (37, 32); (colIndex: Int) => q"castManyBigDecimal(row, $colIndex)"
  }

  val castMandatoryAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOneAttr(t.tpeS) else castManyAttr(t.tpeS)

  val castOptionalAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => ci = (14, 11); (colIndex: Int) => q"castOptOne[String](row, $colIndex)"
        case "Int"        => ci = (15, 12); (colIndex: Int) => q"castOptOneInt(row, $colIndex)"
        case "Float"      => ci = (16, 13); (colIndex: Int) => q"castOptOneFloat(row, $colIndex)"
        case "Long"       => ci = (17, 14); (colIndex: Int) => q"castOptOneLong(row, $colIndex)"
        case "Double"     => ci = (18, 15); (colIndex: Int) => q"castOptOneDouble(row, $colIndex)"
        case "Boolean"    => ci = (19, 16); (colIndex: Int) => q"castOptOne[Boolean](row, $colIndex)"
        case "Date"       => ci = (20, 17); (colIndex: Int) => q"castOptOne[Date](row, $colIndex)"
        case "UUID"       => ci = (21, 18); (colIndex: Int) => q"castOptOne[UUID](row, $colIndex)"
        case "URI"        => ci = (22, 19); (colIndex: Int) => q"castOptOneURI(row, $colIndex)"
        case "BigInt"     => ci = (23, 20); (colIndex: Int) => q"castOptOneBigInt(row, $colIndex)"
        case "BigDecimal" => ci = (24, 21); (colIndex: Int) => q"castOptOneBigDecimal(row, $colIndex)"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => ci = (39, 33); (colIndex: Int) => q"castOptMany[String](row, $colIndex)"
        case "Int"        => ci = (40, 34); (colIndex: Int) => q"castOptManyInt(row, $colIndex)"
        case "Float"      => ci = (41, 35); (colIndex: Int) => q"castOptManyFloat(row, $colIndex)"
        case "Long"       => ci = (42, 36); (colIndex: Int) => q"castOptManyLong(row, $colIndex)"
        case "Double"     => ci = (43, 37); (colIndex: Int) => q"castOptManyDouble(row, $colIndex)"
        case "Boolean"    => ci = (44, 38); (colIndex: Int) => q"castOptMany[Boolean](row, $colIndex)"
        case "Date"       => ci = (45, 39); (colIndex: Int) => q"castOptMany[Date](row, $colIndex)"
        case "UUID"       => ci = (46, 40); (colIndex: Int) => q"castOptMany[UUID](row, $colIndex)"
        case "URI"        => ci = (47, 41); (colIndex: Int) => q"castOptManyURI(row, $colIndex)"
        case "BigInt"     => ci = (48, 42); (colIndex: Int) => q"castOptManyBigInt(row, $colIndex)"
        case "BigDecimal" => ci = (49, 43); (colIndex: Int) => q"castOptManyBigDecimal(row, $colIndex)"
      }
    }

  val castOptionalApplyAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    // Optional, card one
    t.tpeS match {
      case "String"     => ci = (73, 11); (colIndex: Int) => q"castOptApplyOne[String](row, $colIndex)"
      case "Int"        => ci = (74, 12); (colIndex: Int) => q"castOptApplyOneInt(row, $colIndex)"
      case "Float"      => ci = (75, 13); (colIndex: Int) => q"castOptApplyOneFloat(row, $colIndex)"
      case "Long"       => ci = (76, 14); (colIndex: Int) => q"castOptApplyOneLong(row, $colIndex)"
      case "Double"     => ci = (77, 15); (colIndex: Int) => q"castOptApplyOneDouble(row, $colIndex)"
      case "Boolean"    => ci = (78, 16); (colIndex: Int) => q"castOptApplyOne[Boolean](row, $colIndex)"
      case "Date"       => ci = (79, 17); (colIndex: Int) => q"castOptApplyOne[Date](row, $colIndex)"
      case "UUID"       => ci = (80, 18); (colIndex: Int) => q"castOptApplyOne[UUID](row, $colIndex)"
      case "URI"        => ci = (81, 19); (colIndex: Int) => q"castOptApplyOneURI(row, $colIndex)"
      case "BigInt"     => ci = (82, 20); (colIndex: Int) => q"castOptApplyOneBigInt(row, $colIndex)"
      case "BigDecimal" => ci = (83, 21); (colIndex: Int) => q"castOptApplyOneBigDecimal(row, $colIndex)"
    }
  } else {
    // Optional, card many
    t.tpeS match {
      case "String"     => ci = (84, 33); (colIndex: Int) => q"castOptApplyMany[String](row, $colIndex)"
      case "Int"        => ci = (85, 34); (colIndex: Int) => q"castOptApplyManyInt(row, $colIndex)"
      case "Float"      => ci = (86, 35); (colIndex: Int) => q"castOptApplyManyFloat(row, $colIndex)"
      case "Long"       => ci = (87, 36); (colIndex: Int) => q"castOptApplyManyLong(row, $colIndex)"
      case "Double"     => ci = (88, 37); (colIndex: Int) => q"castOptApplyManyDouble(row, $colIndex)"
      case "Boolean"    => ci = (89, 38); (colIndex: Int) => q"castOptApplyMany[Boolean](row, $colIndex)"
      case "Date"       => ci = (90, 39); (colIndex: Int) => q"castOptApplyMany[Date](row, $colIndex)"
      case "UUID"       => ci = (91, 40); (colIndex: Int) => q"castOptApplyMany[UUID](row, $colIndex)"
      case "URI"        => ci = (92, 41); (colIndex: Int) => q"castOptApplyManyURI(row, $colIndex)"
      case "BigInt"     => ci = (93, 42); (colIndex: Int) => q"castOptApplyManyBigInt(row, $colIndex)"
      case "BigDecimal" => ci = (94, 43); (colIndex: Int) => q"castOptApplyManyBigDecimal(row, $colIndex)"
    }
  }

  val castOptionalRefAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    ci = (25, 14)
    (colIndex: Int) => q"castOptOneRefAttr(row, $colIndex)"
  } else {
    ci = (50, 36)
    (colIndex: Int) => q"castOptManyRefAttr(row, $colIndex)"
  }

  val castEnum: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    ci = (0, 0)
    (colIndex: Int) => q"row.get($colIndex).asInstanceOf[String]"
  } else {
    ci = (26, 22)
    (colIndex: Int) => q"castMany[String](row, $colIndex)"
  }

  val castEnumOpt: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    ci = (13, 11)
    (colIndex: Int) => q"castOptOneEnum(row, $colIndex)"
  } else {
    ci = (38, 33)
    (colIndex: Int) => q"castOptManyEnum(row, $colIndex)"
  }

  val castMandatoryMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => ci = (51, 44); (colIndex: Int) => q"castMapString(row, $colIndex)"
    case "Int"        => ci = (52, 45); (colIndex: Int) => q"castMapInt(row, $colIndex)"
    case "Float"      => ci = (53, 46); (colIndex: Int) => q"castMapFloat(row, $colIndex)"
    case "Long"       => ci = (54, 47); (colIndex: Int) => q"castMapLong(row, $colIndex)"
    case "Double"     => ci = (55, 48); (colIndex: Int) => q"castMapDouble(row, $colIndex)"
    case "Boolean"    => ci = (56, 49); (colIndex: Int) => q"castMapBoolean(row, $colIndex)"
    case "Date"       => ci = (57, 50); (colIndex: Int) => q"castMapDate(row, $colIndex)"
    case "UUID"       => ci = (58, 51); (colIndex: Int) => q"castMapUUID(row, $colIndex)"
    case "URI"        => ci = (59, 52); (colIndex: Int) => q"castMapURI(row, $colIndex)"
    case "BigInt"     => ci = (60, 53); (colIndex: Int) => q"castMapBigInt(row, $colIndex)"
    case "BigDecimal" => ci = (61, 54); (colIndex: Int) => q"castMapBigDecimal(row, $colIndex)"
  }

  val castOptionalMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => ci = (62, 55); (colIndex: Int) => q"castOptMapString(row, $colIndex)"
    case "Int"        => ci = (63, 56); (colIndex: Int) => q"castOptMapInt(row, $colIndex)"
    case "Float"      => ci = (64, 57); (colIndex: Int) => q"castOptMapFloat(row, $colIndex)"
    case "Long"       => ci = (65, 58); (colIndex: Int) => q"castOptMapLong(row, $colIndex)"
    case "Double"     => ci = (66, 59); (colIndex: Int) => q"castOptMapDouble(row, $colIndex)"
    case "Boolean"    => ci = (67, 60); (colIndex: Int) => q"castOptMapBoolean(row, $colIndex)"
    case "Date"       => ci = (68, 61); (colIndex: Int) => q"castOptMapDate(row, $colIndex)"
    case "UUID"       => ci = (69, 62); (colIndex: Int) => q"castOptMapUUID(row, $colIndex)"
    case "URI"        => ci = (70, 63); (colIndex: Int) => q"castOptMapURI(row, $colIndex)"
    case "BigInt"     => ci = (71, 64); (colIndex: Int) => q"castOptMapBigInt(row, $colIndex)"
    case "BigDecimal" => ci = (72, 65); (colIndex: Int) => q"castOptMapBigDecimal(row, $colIndex)"
  }

  val castOptionalApplyMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => ci = (95, 55); (colIndex: Int) => q"castOptApplyMapString(row, $colIndex)"
    case "Int"        => ci = (96, 56); (colIndex: Int) => q"castOptApplyMapInt(row, $colIndex)"
    case "Float"      => ci = (97, 57); (colIndex: Int) => q"castOptApplyMapFloat(row, $colIndex)"
    case "Boolean"    => ci = (98, 58); (colIndex: Int) => q"castOptApplyMapBoolean(row, $colIndex)"
    case "Long"       => ci = (99, 59); (colIndex: Int) => q"castOptApplyMapLong(row, $colIndex)"
    case "Double"     => ci = (100, 60); (colIndex: Int) => q"castOptApplyMapDouble(row, $colIndex)"
    case "Date"       => ci = (101, 61); (colIndex: Int) => q"castOptApplyMapDate(row, $colIndex)"
    case "UUID"       => ci = (102, 62); (colIndex: Int) => q"castOptApplyMapUUID(row, $colIndex)"
    case "URI"        => ci = (103, 63); (colIndex: Int) => q"castOptApplyMapURI(row, $colIndex)"
    case "BigInt"     => ci = (104, 64); (colIndex: Int) => q"castOptApplyMapBigInt(row, $colIndex)"
    case "BigDecimal" => ci = (105, 65); (colIndex: Int) => q"castOptApplyMapBigDecimal(row, $colIndex)"
  }

  val castKeyedMapAttr: String => Int => Tree = {
    case "String"     => ci = (106, 0); (colIndex: Int) => q"row.get($colIndex).toString"
    case "Int"        => ci = (107, 1); (colIndex: Int) => q"row.get($colIndex).toString.toInt"
    case "Float"      => ci = (108, 2); (colIndex: Int) => q"row.get($colIndex).toString.toFloat"
    case "Long"       => ci = (109, 3); (colIndex: Int) => q"row.get($colIndex).toString.toLong"
    case "Double"     => ci = (110, 4); (colIndex: Int) => q"row.get($colIndex).toString.toDouble"
    case "Boolean"    => ci = (111, 5); (colIndex: Int) => q"row.get($colIndex).toString.toBoolean"
    case "Date"       => ci = (112, 6); (colIndex: Int) => q"molecule.core.util.fns.str2date(row.get($colIndex).toString)"
    case "UUID"       => ci = (113, 7); (colIndex: Int) => q"java.util.UUID.fromString(row.get($colIndex).toString)"
    case "URI"        => ci = (114, 8); (colIndex: Int) => q"new java.net.URI(row.get($colIndex).toString)"
    case "BigInt"     => ci = (115, 9); (colIndex: Int) => q"BigInt(row.get($colIndex).toString)"
    case "BigDecimal" => ci = (116, 10); (colIndex: Int) => q"BigDecimal(row.get($colIndex).toString)"
    case "Any"        => ci = (117, 0); (colIndex: Int) => q"row.get($colIndex)"
  }
}
