package molecule.core.macros.trees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonOptNested extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val jsonOptNestedOneAttr: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonOptNestedOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"jsonOptNestedOne(sb, $field, ${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"jsonOptNestedOneDate(sb, $field, ${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"jsonOptNestedOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"jsonOptNestedOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"jsonOptNestedOneToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Any"        => (colIndex: Int) => q"jsonOptNestedOneAny(sb, $field, ${TermName("it" + colIndex)})"
  }

  val jsonOptNestedManyAttr: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonOptNestedManyQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"jsonOptNestedMany(sb, $field, ${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"jsonOptNestedManyDate(sb, $field, ${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"jsonOptNestedManyQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"jsonOptNestedManyQuoted(sb, $field, ${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"jsonOptNestedManyToString(sb, $field, ${TermName("it" + colIndex)})"
  }

  val jsonOptNestedMandatoryAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) jsonOptNestedOneAttr(t.tpeS, t.nameClean) else jsonOptNestedManyAttr(t.tpeS, t.nameClean)

  val jsonOptNestedMandatoryRefAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (colIndex: Int) => q"jsonOptNestedOneRefAttr(sb, $field, ${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"jsonOptNestedManyRefAttr(sb, $field, ${TermName("it" + colIndex)})"
  }


  val jsonOptNestedOptAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "Int"        => (colIndex: Int) => q"jsonOptNestedOptOne(sb, $field, ${TermName("it" + colIndex)})"
        case "Long"       => (colIndex: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Double"     => (colIndex: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Boolean"    => (colIndex: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Date"       => (colIndex: Int) => q"jsonOptNestedOptOneDate(sb, $field, ${TermName("it" + colIndex)})"
        case "UUID"       => (colIndex: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "URI"        => (colIndex: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "BigInt"     => (colIndex: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
        case "BigDecimal" => (colIndex: Int) => q"jsonOptNestedOptOneToString(sb, $field, ${TermName("it" + colIndex)})"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "Int"        => (colIndex: Int) => q"jsonOptNestedOptMany(sb, $field, ${TermName("it" + colIndex)})"
        case "Long"       => (colIndex: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Double"     => (colIndex: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Boolean"    => (colIndex: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)})"
        case "Date"       => (colIndex: Int) => q"jsonOptNestedOptManyDate(sb, $field, ${TermName("it" + colIndex)})"
        case "UUID"       => (colIndex: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "URI"        => (colIndex: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, ${TermName("it" + colIndex)})"
        case "BigInt"     => (colIndex: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)})"
        case "BigDecimal" => (colIndex: Int) => q"jsonOptNestedOptManyToString(sb, $field, ${TermName("it" + colIndex)})"
      }
    }
  }


  val jsonOptNestedOptRefAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (colIndex: Int) => q"jsonOptNestedOptOneRefAttr(sb, $field, ${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"jsonOptNestedOptManyRefAttr(sb, $field, ${TermName("it" + colIndex)})"
  }

  val jsonOptNestedEnum: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1) {
      (colIndex: Int) => q"jsonOptNestedOneEnum(sb, $field, ${TermName("it" + colIndex)})"
    } else
      (colIndex: Int) => q"jsonOptNestedManyEnum(sb, $field, ${TermName("it" + colIndex)})"
  }


  val jsonOptNestedOptEnum: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    if (t.card == 1)
      (colIndex: Int) => q"jsonOptNestedOptOneEnum(sb, $field, ${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"jsonOptNestedOptManyEnum(sb, $field, ${TermName("it" + colIndex)})"
  }

  val jsonOptNestedMapAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "Int"        => (colIndex: Int) => q"jsonOptNestedMap(sb, $field, ${TermName("it" + colIndex)})"
      case "Long"       => (colIndex: Int) => q"jsonOptNestedMap(sb, $field, ${TermName("it" + colIndex)})"
      case "Double"     => (colIndex: Int) => q"jsonOptNestedMap(sb, $field, ${TermName("it" + colIndex)})"
      case "Boolean"    => (colIndex: Int) => q"jsonOptNestedMap(sb, $field, ${TermName("it" + colIndex)})"
      case "Date"       => (colIndex: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "UUID"       => (colIndex: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "URI"        => (colIndex: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "BigInt"     => (colIndex: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "BigDecimal" => (colIndex: Int) => q"jsonOptNestedMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
    }
  }

  val jsonOptNestedOptMapAttr: richTree => Int => Tree = (t: richTree) => {
    val field = t.nameClean
    t.tpeS match {
      case "String"     => (colIndex: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "Int"        => (colIndex: Int) => q"jsonOptNestedOptMap(sb, $field, ${TermName("it" + colIndex)})"
      case "Long"       => (colIndex: Int) => q"jsonOptNestedOptMap(sb, $field, ${TermName("it" + colIndex)})"
      case "Double"     => (colIndex: Int) => q"jsonOptNestedOptMap(sb, $field, ${TermName("it" + colIndex)})"
      case "Boolean"    => (colIndex: Int) => q"jsonOptNestedOptMap(sb, $field, ${TermName("it" + colIndex)})"
      case "Date"       => (colIndex: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "UUID"       => (colIndex: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "URI"        => (colIndex: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "BigInt"     => (colIndex: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
      case "BigDecimal" => (colIndex: Int) => q"jsonOptNestedOptMapQuoted(sb, $field, ${TermName("it" + colIndex)})"
    }
  }
}
