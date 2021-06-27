package molecule.core.macros.trees

import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonTypes extends LambdaJsonAggr {
  val c: blackbox.Context

  import c.universe._


  val jsonOneAttr : (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonOneDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Any"        => (colIndex: Int) => q"jsonOneAny(sb, $field, row, $colIndex)"
  }
  val jsonManyAttr: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonMany(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonManyToString(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonManyToString(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonManyToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonManyDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonManyToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonManyToString(sb, $field, row, $colIndex)"
  }

  val jsonMandatoryAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) jsonOneAttr(t.tpeS, t.nameClean) else jsonManyAttr(t.tpeS, t.nameClean)


  val jsonOptionalAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
        case "Int"        => (colIndex: Int) => q"jsonOptOne(sb, $field, row, $colIndex)"
        case "Long"       => (colIndex: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
        case "Double"     => (colIndex: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
        case "Boolean"    => (colIndex: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
        case "Date"       => (colIndex: Int) => q"jsonOptOneDate(sb, $field, row, $colIndex)"
        case "UUID"       => (colIndex: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
        case "URI"        => (colIndex: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
        case "BigInt"     => (colIndex: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
        case "BigDecimal" => (colIndex: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
      }
    } else {
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex)"
        case "Int"        => (colIndex: Int) => q"jsonOptMany(sb, $field, row, $colIndex)"
        case "Long"       => (colIndex: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex)"
        case "Double"     => (colIndex: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex)"
        case "Boolean"    => (colIndex: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex)"
        case "Date"       => (colIndex: Int) => q"jsonOptManyDate(sb, $field, row, $colIndex)"
        case "UUID"       => (colIndex: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex)"
        case "URI"        => (colIndex: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex)"
        case "BigInt"     => (colIndex: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex)"
        case "BigDecimal" => (colIndex: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex)"
      }
    }
  }

  val jsonOptionalApplyAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
        case "Int"        => (colIndex: Int) => q"jsonOptApplyOne(sb, $field, row, $colIndex)"
        case "Long"       => (colIndex: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
        case "Double"     => (colIndex: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
        case "Boolean"    => (colIndex: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
        case "Date"       => (colIndex: Int) => q"jsonOptApplyOneDate(sb, $field, row, $colIndex)"
        case "UUID"       => (colIndex: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
        case "URI"        => (colIndex: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
        case "BigInt"     => (colIndex: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
        case "BigDecimal" => (colIndex: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
      }
    } else {
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex)"
        case "Int"        => (colIndex: Int) => q"jsonOptApplyMany(sb, $field, row, $colIndex)"
        case "Long"       => (colIndex: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex)"
        case "Double"     => (colIndex: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex)"
        case "Boolean"    => (colIndex: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex)"
        case "Date"       => (colIndex: Int) => q"jsonOptApplyManyDate(sb, $field, row, $colIndex)"
        case "UUID"       => (colIndex: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex)"
        case "URI"        => (colIndex: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex)"
        case "BigInt"     => (colIndex: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex)"
        case "BigDecimal" => (colIndex: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex)"
      }
    }
  }

  val jsonOptionalRefAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      (colIndex: Int) => q"jsonOptOneRefAttr(sb, $field, row, $colIndex)"
    } else {
      (colIndex: Int) => q"jsonOptManyRefAttr(sb, $field, row, $colIndex)"
    }
  }


  val jsonEnum: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      ii = (0, 0)
      (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    } else {
      ii = (24, 20)
      (colIndex: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex)"
    }
  }

  val jsonEnumOpt: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      ii = (12, 10)
      (colIndex: Int) => q"jsonOptOneEnum(sb, $field, row, $colIndex)"
    } else {
      ii = (35, 30)
      (colIndex: Int) => q"jsonOptManyEnum(sb, $field, row, $colIndex)"
    }
  }


  val jsonMandatoryMapAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int) => q"jsonMap(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int) => q"jsonMap(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int) => q"jsonMap(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int) => q"jsonMap(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int) => q"jsonMap(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int) => q"jsonMap(sb, $field, row, $colIndex)"
    }
  }

  val jsonOptionalMapAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int) => q"jsonOptMap(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int) => q"jsonOptMap(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int) => q"jsonOptMap(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int) => q"jsonOptMap(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int) => q"jsonOptMap(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int) => q"jsonOptMap(sb, $field, row, $colIndex)"
    }
  }

  val jsonOptionalApplyMapAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex)"
    }
  }

  val jsonKeyedMapAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int) => q"jsonOne(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int) => q"jsonOneDate(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Any"        => (colIndex: Int) => q"jsonOneAny(sb, $field, row, $colIndex)"
    }
  }
}
