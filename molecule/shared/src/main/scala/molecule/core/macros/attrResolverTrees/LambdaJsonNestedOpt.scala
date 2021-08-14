package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonNestedOpt extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val jsonNestedOptOneAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (_: Int, _: Int) => q"jsonNestedOptOneQuoted(sb, $field, it)"
    case "Int"        => (_: Int, _: Int) => q"jsonNestedOptOne(sb, $field, it)"
    case "Long"       => (_: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, it)"
    case "Double"     => (_: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, it)"
    case "Boolean"    => (_: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, it)"
    case "Date"       => (_: Int, _: Int) => q"jsonNestedOptOneDate(sb, $field, it)"
    case "UUID"       => (_: Int, _: Int) => q"jsonNestedOptOneQuoted(sb, $field, it)"
    case "URI"        => (_: Int, _: Int) => q"jsonNestedOptOneQuoted(sb, $field, it)"
    case "BigInt"     => (_: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, it)"
    case "BigDecimal" => (_: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, it)"
    case "Any"        => (_: Int, _: Int) => q"jsonNestedOptOneAny(sb, $field, it)"
  }

  val jsonNestedOptManyAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (_: Int, tabs: Int) => q"jsonNestedOptManyQuoted(sb, $field, it, $tabs)"
    case "Int"        => (_: Int, tabs: Int) => q"jsonNestedOptMany(sb, $field, it, $tabs)"
    case "Long"       => (_: Int, tabs: Int) => q"jsonNestedOptManyToString(sb, $field, it, $tabs)"
    case "Double"     => (_: Int, tabs: Int) => q"jsonNestedOptManyToString(sb, $field, it, $tabs)"
    case "Boolean"    => (_: Int, tabs: Int) => q"jsonNestedOptManyToString(sb, $field, it, $tabs)"
    case "Date"       => (_: Int, tabs: Int) => q"jsonNestedOptManyDate(sb, $field, it, $tabs)"
    case "UUID"       => (_: Int, tabs: Int) => q"jsonNestedOptManyQuoted(sb, $field, it, $tabs)"
    case "URI"        => (_: Int, tabs: Int) => q"jsonNestedOptManyQuoted(sb, $field, it, $tabs)"
    case "BigInt"     => (_: Int, tabs: Int) => q"jsonNestedOptManyToString(sb, $field, it, $tabs)"
    case "BigDecimal" => (_: Int, tabs: Int) => q"jsonNestedOptManyToString(sb, $field, it, $tabs)"
  }

  val jsonNestedOptAttr: richTree => (Int, Int) => Tree = (t: richTree) =>
    if (t.card == 1) jsonNestedOptOneAttr(t.tpeS, t.nameClean) else jsonNestedOptManyAttr(t.tpeS, t.nameClean)

  val jsonNestedOptRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (_: Int, tabs: Int) => q"jsonNestedOptOneRefAttr(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonNestedOptManyRefAttr(sb, $field, it, $tabs)"
  }


  val jsonNestedOptOptAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (_: Int, _: Int) => q"jsonNestedOptOptOneQuoted(sb, $field, it)"
        case "Int"        => (_: Int, _: Int) => q"jsonNestedOptOptOne(sb, $field, it)"
        case "Long"       => (_: Int, _: Int) => q"jsonNestedOptOptOneToString(sb, $field, it)"
        case "Double"     => (_: Int, _: Int) => q"jsonNestedOptOptOneToString(sb, $field, it)"
        case "Boolean"    => (_: Int, _: Int) => q"jsonNestedOptOptOneToString(sb, $field, it)"
        case "Date"       => (_: Int, _: Int) => q"jsonNestedOptOptOneDate(sb, $field, it)"
        case "UUID"       => (_: Int, _: Int) => q"jsonNestedOptOptOneQuoted(sb, $field, it)"
        case "URI"        => (_: Int, _: Int) => q"jsonNestedOptOptOneQuoted(sb, $field, it)"
        case "BigInt"     => (_: Int, _: Int) => q"jsonNestedOptOptOneToString(sb, $field, it)"
        case "BigDecimal" => (_: Int, _: Int) => q"jsonNestedOptOptOneToString(sb, $field, it)"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (_: Int, tabs: Int) => q"jsonNestedOptOptManyQuoted(sb, $field, it, $tabs)"
        case "Int"        => (_: Int, tabs: Int) => q"jsonNestedOptOptMany(sb, $field, it, $tabs)"
        case "Long"       => (_: Int, tabs: Int) => q"jsonNestedOptOptManyToString(sb, $field, it, $tabs)"
        case "Double"     => (_: Int, tabs: Int) => q"jsonNestedOptOptManyToString(sb, $field, it, $tabs)"
        case "Boolean"    => (_: Int, tabs: Int) => q"jsonNestedOptOptManyToString(sb, $field, it, $tabs)"
        case "Date"       => (_: Int, tabs: Int) => q"jsonNestedOptOptManyDate(sb, $field, it, $tabs)"
        case "UUID"       => (_: Int, tabs: Int) => q"jsonNestedOptOptManyQuoted(sb, $field, it, $tabs)"
        case "URI"        => (_: Int, tabs: Int) => q"jsonNestedOptOptManyQuoted(sb, $field, it, $tabs)"
        case "BigInt"     => (_: Int, tabs: Int) => q"jsonNestedOptOptManyToString(sb, $field, it, $tabs)"
        case "BigDecimal" => (_: Int, tabs: Int) => q"jsonNestedOptOptManyToString(sb, $field, it, $tabs)"
      }
    }
  }


  val jsonNestedOptOptRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (_: Int, _: Int) => q"jsonNestedOptOptOneRefAttr(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonNestedOptOptManyRefAttr(sb, $field, it, $tabs)"
  }

  val jsonNestedOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      (_: Int, _: Int) => q"jsonNestedOptOneEnum(sb, $field, it)"
    } else
      (_: Int, tabs: Int) => q"jsonNestedOptManyEnum(sb, $field, it, $tabs)"
  }


  val jsonNestedOptOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (_: Int, _: Int) => q"jsonNestedOptOptOneEnum(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonNestedOptOptManyEnum(sb, $field, it, $tabs)"
  }

  val jsonNestedOptMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (_: Int, tabs: Int) => q"jsonNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "Int"        => (_: Int, tabs: Int) => q"jsonNestedOptMap(sb, $field, it, $tabs)"
      case "Long"       => (_: Int, tabs: Int) => q"jsonNestedOptMap(sb, $field, it, $tabs)"
      case "Double"     => (_: Int, tabs: Int) => q"jsonNestedOptMap(sb, $field, it, $tabs)"
      case "Boolean"    => (_: Int, tabs: Int) => q"jsonNestedOptMap(sb, $field, it, $tabs)"
      case "Date"       => (_: Int, tabs: Int) => q"jsonNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "UUID"       => (_: Int, tabs: Int) => q"jsonNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "URI"        => (_: Int, tabs: Int) => q"jsonNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "BigInt"     => (_: Int, tabs: Int) => q"jsonNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "BigDecimal" => (_: Int, tabs: Int) => q"jsonNestedOptMapQuoted(sb, $field, it, $tabs)"
    }
  }

  val jsonNestedOptOptMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (_: Int, tabs: Int) => q"jsonNestedOptOptMapQuoted(sb, $field, it, $tabs)"
      case "Int"        => (_: Int, tabs: Int) => q"jsonNestedOptOptMap(sb, $field, it, $tabs)"
      case "Long"       => (_: Int, tabs: Int) => q"jsonNestedOptOptMap(sb, $field, it, $tabs)"
      case "Double"     => (_: Int, tabs: Int) => q"jsonNestedOptOptMap(sb, $field, it, $tabs)"
      case "Boolean"    => (_: Int, tabs: Int) => q"jsonNestedOptOptMap(sb, $field, it, $tabs)"
      case "Date"       => (_: Int, tabs: Int) => q"jsonNestedOptOptMapQuoted(sb, $field, it, $tabs)"
      case "UUID"       => (_: Int, tabs: Int) => q"jsonNestedOptOptMapQuoted(sb, $field, it, $tabs)"
      case "URI"        => (_: Int, tabs: Int) => q"jsonNestedOptOptMapQuoted(sb, $field, it, $tabs)"
      case "BigInt"     => (_: Int, tabs: Int) => q"jsonNestedOptOptMapQuoted(sb, $field, it, $tabs)"
      case "BigDecimal" => (_: Int, tabs: Int) => q"jsonNestedOptOptMapQuoted(sb, $field, it, $tabs)"
    }
  }

  val jsonNestedOptOptApplyMapAttr: richTree => (Int, Int) => Tree = jsonNestedOptOptMapAttr

    val jsonNestedOptKeyedMapAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => {
    tpe match {
      case "String"     => (colIndex: Int, _: Int) => q"jsonNestedOptOneQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int, _: Int) => q"jsonNestedOptOne(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int, _: Int) => q"jsonNestedOptOneDate(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int, _: Int) => q"jsonNestedOptOneQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int, _: Int) => q"jsonNestedOptOneQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonNestedOptOneToString(sb, $field, row, $colIndex)"
      case "Any"        => (colIndex: Int, _: Int) => q"jsonNestedOptOneAny(sb, $field, row, $colIndex)"
    }
  }
}
