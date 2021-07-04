package molecule.core.macros.lambdaTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonOptNested extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val jsonOptNestedOneAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedOne(sb, $field, ${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneDate(sb, $field, ${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Any"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedOneAny(sb, $field, ${TermName("it" + colIndex)})"
  }

  val jsonOptNestedManyAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedMany(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyDate(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
  }

  val jsonOptNestedMandatoryAttr: richTree => (Int, Int) => Tree = (t: richTree) =>
    if (t.card == 1) jsonOptNestedOneAttr(t.tpeS, t.nameClean) else jsonOptNestedManyAttr(t.tpeS, t.nameClean)

  val jsonOptNestedMandatoryRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (colIndex: Int, tabs: Int) => q"jsonOptNestedOneRefAttr(sb, $field, ${TermName("it" + colIndex)})"
    else
      (colIndex: Int, tabs: Int) => q"jsonOptNestedManyRefAttr(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
  }


  val jsonOptNestedOptAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "Int"        => (colIndex: Int, _: Int) => q"jsonOptNestedOptOne(sb, $field, ${TermName("it" + colIndex)})"
        case "Long"       => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Double"     => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Date"       => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneDate(sb, $field, ${TermName("it" + colIndex)})"
        case "UUID"       => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "URI"        => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
        case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMany(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyDate(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
        case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      }
    }
  }


  val jsonOptNestedOptRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (colIndex: Int, _: Int) => q"jsonOptNestedOptOneRefAttr(sb, $field, ${TermName("it" + colIndex)})"
    else
      (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyRefAttr(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
  }

  val jsonOptNestedEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      (colIndex: Int, _: Int) => q"jsonOptNestedOneEnum(sb, $field, ${TermName("it" + colIndex)})"
    } else
      (colIndex: Int, tabs: Int) => q"jsonOptNestedManyEnum(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
  }


  val jsonOptNestedOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (colIndex: Int, _: Int) => q"jsonOptNestedOptOneEnum(sb, $field, ${TermName("it" + colIndex)})"
    else
      (colIndex: Int, tabs: Int) => q"jsonOptNestedOptManyEnum(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
  }

  val jsonOptNestedMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedMap(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedMap(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedMap(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptNestedMap(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    }
  }

  val jsonOptNestedOptMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMap(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMap(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMap(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMap(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)}, $tabs)"
    }
  }
}
