package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait ResolverJsonNestedOpt extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  def getResolverJsonNestedOpt(group: String, baseType: String, field: String): (Int, Int) => Tree = group match {
    case "One"          => jsonNestedOptOneAttr(baseType, field)
    case "OptOne"       => jsonNestedOptOptOneAttr(baseType, field)
    case "Many"         => jsonNestedOptManyAttr(baseType, field)
    case "OptMany"      => jsonNestedOptOptManyAttr(baseType, field)
    case "Map"          => jsonNestedOptMapAttr_(baseType, field)
    case "OptMap"       => jsonNestedOptOptMapAttr_(baseType, field)
    case "OptApplyOne"  => jsonNestedOptOptOneAttr(baseType, field)
    case "OptApplyMany" => jsonNestedOptOptManyAttr(baseType, field)
    case "OptApplyMap"  => jsonNestedOptOptMapAttr_(baseType, field)
    //    case "KeyedMap"     => jsonNestedOptKeyedMapAttr(baseType, field)
  }


  lazy val jsonNestedOptAttr: richTree => (Int, Int) => Tree = (t: richTree) =>
    if (t.card == 1) jsonNestedOptOneAttr(t.tpeS, t.name) else jsonNestedOptManyAttr(t.tpeS, t.name)

  lazy val jsonNestedOptOneAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
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
    case "enum"       => (_: Int, _: Int) => q"jsonNestedOptOneEnum(sb, $field, it)"
    case "ref"        => (_: Int, _: Int) => q"jsonNestedOptOneRefAttr(sb, $field, it)"
  }

  lazy val jsonNestedOptManyAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
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
    case "enum"       => (_: Int, tabs: Int) => q"jsonNestedOptManyEnum(sb, $field, it, $tabs)"
    case "ref"        => (_: Int, tabs: Int) => q"jsonNestedOptManyRefAttr(sb, $field, it, $tabs)"
  }

  lazy val jsonNestedOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1) {
      (_: Int, _: Int) => q"jsonNestedOptOneEnum(sb, $field, it)"
    } else
      (_: Int, tabs: Int) => q"jsonNestedOptManyEnum(sb, $field, it, $tabs)"
  }

  lazy val jsonNestedOptRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1)
      (_: Int, tabs: Int) => q"jsonNestedOptOneRefAttr(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonNestedOptManyRefAttr(sb, $field, it, $tabs)"
  }


  lazy val jsonNestedOptOptAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    if (t.card == 1) {
      jsonNestedOptOptOneAttr(t.tpeS, t.name)
    } else {
      jsonNestedOptOptManyAttr(t.tpeS, t.name)
    }
  }

  lazy val jsonNestedOptOptOneAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
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
      case "enum"       => (_: Int, _: Int) => q"jsonNestedOptOptOneEnum(sb, $field, it)"
      case "ref"        => (_: Int, _: Int) => q"jsonNestedOptOptOneRefAttr(sb, $field, it)"
    }
  }

  lazy val jsonNestedOptOptManyAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
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
      case "enum"       => (_: Int, tabs: Int) => q"jsonNestedOptOptManyEnum(sb, $field, it, $tabs)"
      case "ref"        => (_: Int, tabs: Int) => q"jsonNestedOptOptManyRefAttr(sb, $field, it, $tabs)"
    }
  }

  lazy val jsonNestedOptOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1)
      (_: Int, _: Int) => q"jsonNestedOptOptOneEnum(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonNestedOptOptManyEnum(sb, $field, it, $tabs)"
  }

  lazy val jsonNestedOptOptRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1)
      (_: Int, _: Int) => q"jsonNestedOptOptOneRefAttr(sb, $field, it)"
    else
      (_: Int, tabs: Int) => q"jsonNestedOptOptManyRefAttr(sb, $field, it, $tabs)"
  }


  lazy val jsonNestedOptMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => jsonNestedOptMapAttr_(t.tpeS, t.name)

  lazy val jsonNestedOptMapAttr_ : (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
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

  lazy val jsonNestedOptOptMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => jsonNestedOptOptMapAttr_(t.tpeS, t.name)

  lazy val jsonNestedOptOptMapAttr_ : (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
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


  lazy val jsonNestedOptOptApplyMapAttr: richTree => (Int, Int) => Tree = jsonNestedOptOptMapAttr

  lazy val jsonNestedOptKeyedMapAttr: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => {
    tpe match {
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
      //      case "Any"        => (_: Int, _: Int) => q"jsonNestedOptOneAny(sb, $field, it)"
    }
  }
}
