package molecule.core.macros.rowAttr

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait RowValue2jsonOptNested extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  def getRowValue2jsonOptNestedLambda(group: String, baseTpe: String, field: String): (Int, Int) => Tree = group match {
    case "One"          => jsonOptNestedOneAttr(baseTpe, field)
    case "OptOne"       => jsonOptNestedOptOneAttr(baseTpe, field)
    case "Many"         => jsonOptNestedManyAttr(baseTpe, field)
    case "OptMany"      => jsonOptNestedOptManyAttr(baseTpe, field)
    case "Map"          => jsonOptNestedMapAttr_(baseTpe, field)
    case "OptMap"       => jsonOptNestedOptMapAttr_(baseTpe, field)
    case "OptApplyOne"  => jsonOptNestedOptOneAttr(baseTpe, field)
    case "OptApplyMany" => jsonOptNestedOptManyAttr(baseTpe, field)
    case "OptApplyMap"  => jsonOptNestedOptMapAttr_(baseTpe, field)
  }


  lazy val jsonOptNestedAttr: richTree => (Int, Int) => Tree = (t: richTree) =>
    if (t.card == 1) jsonOptNestedOneAttr(t.tpeS, t.name) else jsonOptNestedManyAttr(t.tpeS, t.name)

  lazy val jsonOptNestedOneAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    case "Int"        => (_: Int, _: Int) => q"jsonOptNestedOne(sb, $field, it)"
    case "Long"       => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
    case "Double"     => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
    case "Boolean"    => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
    case "Date"       => (_: Int, _: Int) => q"jsonOptNestedOneDate(sb, $field, it)"
    case "UUID"       => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    case "URI"        => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    case "BigInt"     => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    case "BigDecimal" => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    case "enum"       => (_: Int, _: Int) => q"jsonOptNestedOneEnum(sb, $field, it)"
    case "ref"        => (_: Int, _: Int) => q"jsonOptNestedOneRefAttr(sb, $field, it)"
  }

  lazy val jsonOptNestedManyAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (_: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, it, $tabs)"
    case "Int"        => (_: Int, tabs: Int) => q"jsonOptNestedMany(sb, $field, it, $tabs)"
    case "Long"       => (_: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, it, $tabs)"
    case "Double"     => (_: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, it, $tabs)"
    case "Boolean"    => (_: Int, tabs: Int) => q"jsonOptNestedManyToString(sb, $field, it, $tabs)"
    case "Date"       => (_: Int, tabs: Int) => q"jsonOptNestedManyDate(sb, $field, it, $tabs)"
    case "UUID"       => (_: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, it, $tabs)"
    case "URI"        => (_: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, it, $tabs)"
    case "BigInt"     => (_: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, it, $tabs)"
    case "BigDecimal" => (_: Int, tabs: Int) => q"jsonOptNestedManyQuoted(sb, $field, it, $tabs)"
    case "enum"       => (_: Int, tabs: Int) => q"jsonOptNestedManyEnum(sb, $field, it, $tabs)"
    case "ref"        => (_: Int, tabs: Int) => q"jsonOptNestedManyRefAttr(sb, $field, it, $tabs)"
  }

  lazy val jsonOptNestedEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1) {
      (_: Int, _: Int) => q"jsonOptNestedOneEnum(sb, $field, it)"
    } else
      (_: Int, tabs: Int) => q"jsonOptNestedManyEnum(sb, $field, it, $tabs)"
  }

  lazy val jsonOptNestedRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1)
      (_: Int, tabs: Int) => q"jsonOptNestedOneRefAttr(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonOptNestedManyRefAttr(sb, $field, it, $tabs)"
  }


  lazy val jsonOptNestedOptAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    if (t.card == 1) {
      jsonOptNestedOptOneAttr(t.tpeS, t.name)
    } else {
      jsonOptNestedOptManyAttr(t.tpeS, t.name)
    }
  }

  lazy val jsonOptNestedOptOneAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
      case "String"     => (_: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, it)"
      case "Int"        => (_: Int, _: Int) => q"jsonOptNestedOptOne(sb, $field, it)"
      case "Long"       => (_: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, it)"
      case "Double"     => (_: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, it)"
      case "Boolean"    => (_: Int, _: Int) => q"jsonOptNestedOptOneToString(sb, $field, it)"
      case "Date"       => (_: Int, _: Int) => q"jsonOptNestedOptOneDate(sb, $field, it)"
      case "UUID"       => (_: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, it)"
      case "URI"        => (_: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, it)"
      case "BigInt"     => (_: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, it)"
      case "BigDecimal" => (_: Int, _: Int) => q"jsonOptNestedOptOneQuoted(sb, $field, it)"
      case "enum"       => (_: Int, _: Int) => q"jsonOptNestedOptOneEnum(sb, $field, it)"
      case "ref"        => (_: Int, _: Int) => q"jsonOptNestedOptOneRefAttr(sb, $field, it)"
    }
  }

  lazy val jsonOptNestedOptManyAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
      case "String"     => (_: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, it, $tabs)"
      case "Int"        => (_: Int, tabs: Int) => q"jsonOptNestedOptMany(sb, $field, it, $tabs)"
      case "Long"       => (_: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, it, $tabs)"
      case "Double"     => (_: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, it, $tabs)"
      case "Boolean"    => (_: Int, tabs: Int) => q"jsonOptNestedOptManyToString(sb, $field, it, $tabs)"
      case "Date"       => (_: Int, tabs: Int) => q"jsonOptNestedOptManyDate(sb, $field, it, $tabs)"
      case "UUID"       => (_: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, it, $tabs)"
      case "URI"        => (_: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, it, $tabs)"
      case "BigInt"     => (_: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, it, $tabs)"
      case "BigDecimal" => (_: Int, tabs: Int) => q"jsonOptNestedOptManyQuoted(sb, $field, it, $tabs)"
      case "enum"       => (_: Int, tabs: Int) => q"jsonOptNestedOptManyEnum(sb, $field, it, $tabs)"
      case "ref"        => (_: Int, tabs: Int) => q"jsonOptNestedOptManyRefAttr(sb, $field, it, $tabs)"
    }
  }

  lazy val jsonOptNestedOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1)
      (_: Int, _: Int) => q"jsonOptNestedOptOneEnum(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonOptNestedOptManyEnum(sb, $field, it, $tabs)"
  }

  lazy val jsonOptNestedOptRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1)
      (_: Int, _: Int) => q"jsonOptNestedOptOneRefAttr(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonOptNestedOptManyRefAttr(sb, $field, it, $tabs)"
  }


  lazy val jsonOptNestedMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => jsonOptNestedMapAttr_(t.tpeS, t.name)

  lazy val jsonOptNestedMapAttr_ : (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
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

  lazy val jsonOptNestedOptMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => jsonOptNestedOptMapAttr_(t.tpeS, t.name)

  lazy val jsonOptNestedOptMapAttr_ : (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
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


  lazy val jsonOptNestedOptApplyMapAttr: richTree => (Int, Int) => Tree = jsonOptNestedOptMapAttr

  lazy val jsonOptNestedKeyedMapAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => {
    tpe match {
      case "String"     => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
      case "Int"        => (_: Int, _: Int) => q"jsonOptNestedOne(sb, $field, it)"
      case "Long"       => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
      case "Double"     => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
      case "Boolean"    => (_: Int, _: Int) => q"jsonOptNestedOneToString(sb, $field, it)"
      case "Date"       => (_: Int, _: Int) => q"jsonOptNestedOneDate(sb, $field, it)"
      case "UUID"       => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
      case "URI"        => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
      case "BigInt"     => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
      case "BigDecimal" => (_: Int, _: Int) => q"jsonOptNestedOneQuoted(sb, $field, it)"
    }
  }
}
