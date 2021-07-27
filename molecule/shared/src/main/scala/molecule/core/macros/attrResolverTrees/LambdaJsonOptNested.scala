package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonOptNested extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val jsonOptNestedOneAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    case "Int"        => (_: Int, _: Int) => q"jsonOptNestedOne(sb, $field, it)"
    case "Long"       => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
    case "Double"     => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
    case "Boolean"    => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
    case "Date"       => (_: Int, _: Int) => q"jsonOptNestedOneDate(sb, $field, it)"
    case "UUID"       => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    case "URI"        => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    case "BigInt"     => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
    case "BigDecimal" => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
    case "Any"        => (_: Int, _: Int) => q"jsonOptNestedOneAny(sb, $field, it)"
  }

  val jsonOptNestedManyAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (_: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, it, $tabs)"
    case "Int"        => (_: Int, tabs: Int) => q"jsonOptNestedMany(sb, $field, it, $tabs)"
    case "Long"       => (_: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, it, $tabs)"
    case "Double"     => (_: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, it, $tabs)"
    case "Boolean"    => (_: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, it, $tabs)"
    case "Date"       => (_: Int, tabs: Int) => q"jsonOptNestedManyDate(sb, $field, it, $tabs)"
    case "UUID"       => (_: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, it, $tabs)"
    case "URI"        => (_: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, it, $tabs)"
    case "BigInt"     => (_: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, it, $tabs)"
    case "BigDecimal" => (_: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, it, $tabs)"
  }

  val jsonOptNestedAttr: richTree => (Int, Int) => Tree = (t: richTree) =>
    if (t.card == 1) jsonOptNestedOneAttr(t.tpeS, t.nameClean) else jsonOptNestedManyAttr(t.tpeS, t.nameClean)

  val jsonOptNestedRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (_: Int, tabs: Int) => q"jsonOptNestedOneRefAttr(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonOptNestedManyRefAttr(sb, $field, it, $tabs)"
  }


  val jsonOptNestedOptAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (_: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, it)"
        case "Int"        => (_: Int, _: Int) => q"jsonOptNestedOptOne(sb, $field, it)"
        case "Long"       => (_: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, it)"
        case "Double"     => (_: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, it)"
        case "Boolean"    => (_: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, it)"
        case "Date"       => (_: Int, _: Int) => q"jsonOptNestedOptOneDate(sb, $field, it)"
        case "UUID"       => (_: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, it)"
        case "URI"        => (_: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, it)"
        case "BigInt"     => (_: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, it)"
        case "BigDecimal" => (_: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, it)"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (_: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, it, $tabs)"
        case "Int"        => (_: Int, tabs: Int) => q"jsonOptNestedOptMany(sb, $field, it, $tabs)"
        case "Long"       => (_: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, it, $tabs)"
        case "Double"     => (_: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, it, $tabs)"
        case "Boolean"    => (_: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, it, $tabs)"
        case "Date"       => (_: Int, tabs: Int) => q"jsonOptNestedOptManyDate(sb, $field, it, $tabs)"
        case "UUID"       => (_: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, it, $tabs)"
        case "URI"        => (_: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, it, $tabs)"
        case "BigInt"     => (_: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, it, $tabs)"
        case "BigDecimal" => (_: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, it, $tabs)"
      }
    }
  }


  val jsonOptNestedOptRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (_: Int, _: Int) => q"jsonOptNestedOptOneRefAttr(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonOptNestedOptManyRefAttr(sb, $field, it, $tabs)"
  }

  val jsonOptNestedEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      (_: Int, _: Int) => q"jsonOptNestedOneEnum(sb, $field, it)"
    } else
      (_: Int, tabs: Int) => q"jsonOptNestedManyEnum(sb, $field, it, $tabs)"
  }


  val jsonOptNestedOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (_: Int, _: Int) => q"jsonOptNestedOptOneEnum(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonOptNestedOptManyEnum(sb, $field, it, $tabs)"
  }

  val jsonOptNestedMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (_: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, it, $tabs)"
      case "Int"        => (_: Int, tabs: Int) => q"jsonOptNestedMap(sb, $field, it, $tabs)"
      case "Long"       => (_: Int, tabs: Int) => q"jsonOptNestedMap(sb, $field, it, $tabs)"
      case "Double"     => (_: Int, tabs: Int) => q"jsonOptNestedMap(sb, $field, it, $tabs)"
      case "Boolean"    => (_: Int, tabs: Int) => q"jsonOptNestedMap(sb, $field, it, $tabs)"
      case "Date"       => (_: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, it, $tabs)"
      case "UUID"       => (_: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, it, $tabs)"
      case "URI"        => (_: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, it, $tabs)"
      case "BigInt"     => (_: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, it, $tabs)"
      case "BigDecimal" => (_: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, it, $tabs)"
    }
  }

  val jsonOptNestedOptMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (_: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "Int"        => (_: Int, tabs: Int) => q"jsonOptNestedOptMap(sb, $field, it, $tabs)"
      case "Long"       => (_: Int, tabs: Int) => q"jsonOptNestedOptMap(sb, $field, it, $tabs)"
      case "Double"     => (_: Int, tabs: Int) => q"jsonOptNestedOptMap(sb, $field, it, $tabs)"
      case "Boolean"    => (_: Int, tabs: Int) => q"jsonOptNestedOptMap(sb, $field, it, $tabs)"
      case "Date"       => (_: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "UUID"       => (_: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "URI"        => (_: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "BigInt"     => (_: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, it, $tabs)"
      case "BigDecimal" => (_: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, it, $tabs)"
    }
  }

  val jsonOptNestedOptApplyMapAttr: richTree => (Int, Int) => Tree = jsonOptNestedOptMapAttr

    val jsonOptNestedKeyedMapAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => {
    tpe match {
      case "String"     => (colIndex: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int, _: Int) => q"jsonOptNestedOne(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int, _: Int) => q"jsonOptNestedOneDate(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, row, $colIndex)"
      case "Any"        => (colIndex: Int, _: Int) => q"jsonOptNestedOneAny(sb, $field, row, $colIndex)"
    }
  }
}
