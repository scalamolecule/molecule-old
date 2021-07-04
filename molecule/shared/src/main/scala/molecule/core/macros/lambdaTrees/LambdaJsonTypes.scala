package molecule.core.macros.lambdaTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonTypes extends TreeOps {
  val c: blackbox.Context

  import c.universe._


  val jsonOneAttr : (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int, _: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int, _: Int) => q"jsonOneDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Any"        => (colIndex: Int, _: Int) => q"jsonOneAny(sb, $field, row, $colIndex)"
  }
  val jsonManyAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonMany(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonManyDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
  }

  val jsonMandatoryAttr: richTree => (Int, Int) => Tree = (t: richTree) =>
    if (t.card == 1) jsonOneAttr(t.tpeS, t.nameClean) else jsonManyAttr(t.tpeS, t.nameClean)


  val jsonOptionalAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      t.tpeS match {
        case "String"     => (colIndex: Int, _: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
        case "Int"        => (colIndex: Int, _: Int) => q"jsonOptOne(sb, $field, row, $colIndex)"
        case "Long"       => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
        case "Double"     => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
        case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
        case "Date"       => (colIndex: Int, _: Int) => q"jsonOptOneDate(sb, $field, row, $colIndex)"
        case "UUID"       => (colIndex: Int, _: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
        case "URI"        => (colIndex: Int, _: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
        case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
        case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
      }
    } else {
      t.tpeS match {
        case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex, $tabs)"
        case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptMany(sb, $field, row, $colIndex, $tabs)"
        case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
        case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
        case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
        case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptManyDate(sb, $field, row, $colIndex, $tabs)"
        case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex, $tabs)"
        case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex, $tabs)"
        case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
        case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
      }
    }
  }

  val jsonOptionalApplyAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      t.tpeS match {
        case "String"     => (colIndex: Int, _: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
        case "Int"        => (colIndex: Int, _: Int) => q"jsonOptApplyOne(sb, $field, row, $colIndex)"
        case "Long"       => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
        case "Double"     => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
        case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
        case "Date"       => (colIndex: Int, _: Int) => q"jsonOptApplyOneDate(sb, $field, row, $colIndex)"
        case "UUID"       => (colIndex: Int, _: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
        case "URI"        => (colIndex: Int, _: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
        case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
        case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
      }
    } else {
      t.tpeS match {
        case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex, $tabs)"
        case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyMany(sb, $field, row, $colIndex, $tabs)"
        case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
        case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
        case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
        case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyDate(sb, $field, row, $colIndex, $tabs)"
        case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex, $tabs)"
        case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex, $tabs)"
        case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
        case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
      }
    }
  }

  val jsonOptionalRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      (colIndex: Int, _: Int) => q"jsonOptOneRefAttr(sb, $field, row, $colIndex)"
    } else {
      (colIndex: Int, tabs: Int) => q"jsonOptManyRefAttr(sb, $field, row, $colIndex, $tabs)"
    }
  }


  val jsonEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      ii = (0, 0)
      (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    } else {
      ii = (24, 20)
      (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    }
  }

  val jsonOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      ii = (12, 10)
      (colIndex: Int, _: Int) => q"jsonOptOneEnum(sb, $field, row, $colIndex)"
    } else {
      ii = (35, 30)
      (colIndex: Int, tabs: Int) => q"jsonOptManyEnum(sb, $field, row, $colIndex, $tabs)"
    }
  }


  val jsonMandatoryMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
    }
  }

  val jsonOptionalMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
    }
  }

  val jsonOptionalApplyMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
    }
  }

  val jsonKeyedMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int, _: Int) => q"jsonOne(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int, _: Int) => q"jsonOneDate(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Any"        => (colIndex: Int, _: Int) => q"jsonOneAny(sb, $field, row, $colIndex)"
    }
  }
}
