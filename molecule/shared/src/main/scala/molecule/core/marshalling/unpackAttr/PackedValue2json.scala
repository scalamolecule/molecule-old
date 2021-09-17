package molecule.core.marshalling.unpackAttr

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait PackedValue2json extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  def getPackedValue2json(
    group: String,
    baseTpe: String,
    field: String, 
    v: Tree, 
    tabs: Int,
    optAggrTpe: Option[String]
  ): Tree = {
    val tpe = optAggrTpe.getOrElse(baseTpe)
    group match {
      case "One" | "KeyedMap" | "AggrSingleSample" | "AggrOneSingle"    => unpack2jsonOneAttr(field, tpe, v)
      case "OptOne" | "OptApplyOne"                                     => unpack2jsonOptOneAttr(field, tpe, v)
      case "Many" | "AggrManySingle"                                    => unpack2jsonManyAttr(field, tpe, v, tabs)
      case "OptMany" | "OptApplyMany"                                   => unpack2jsonOptManyAttr(field, tpe, v, tabs)
      case "Map"                                                        => unpack2jsonMapAttr(field, tpe, v, tabs)
      case "OptMap" | "OptApplyMap"                                     => unpack2jsonOptMapAttr(field, tpe, v, tabs)
      case "AggrOneList" | "AggrOneListDistinct" | "AggrOneListRand"    => unpack2jsonAggrOneList(field, tpe, v, tabs)
      case "AggrManyList" | "AggrManyListDistinct" | "AggrManyListRand" => unpack2jsonAggrManyList(field, tpe, v, tabs)
    }
  }

  def unpack2jsonOneAttr(field: String, tpe: String, v: Tree): Tree = tpe match {
    case "String"     => q"unpack2jsonOneString(sb, $field, $v, vs)"
    case "Int"        => q"unpack2jsonOne(sb, $field, $v)"
    case "Long"       => q"unpack2jsonOne(sb, $field, $v)"
    case "Double"     => q"unpack2jsonOne(sb, $field, $v)"
    case "Boolean"    => q"unpack2jsonOne(sb, $field, $v)"
    case "Date"       => q"unpack2jsonOneDate(sb, $field, $v)"
    case "UUID"       => q"unpack2jsonOneQuoted(sb, $field, $v)"
    case "URI"        => q"unpack2jsonOneQuoted(sb, $field, $v)"
    case "BigInt"     => q"unpack2jsonOne(sb, $field, $v)"
    case "BigDecimal" => q"unpack2jsonOne(sb, $field, $v)"
    case "Any"        => q"unpack2jsonOneAny(sb, $field, $v, vs)"
    case "enum"       => q"unpack2jsonOneQuoted(sb, $field, $v)"
    case "ref"        => q"unpack2jsonOne(sb, $field, $v)"
  }

  def unpack2jsonOptOneAttr(field: String, tpe: String, v: Tree): Tree = tpe match {
    case "String"     => q"unpack2jsonOptOneString(sb, $field, $v, vs)"
    case "Int"        => q"unpack2jsonOptOne(sb, $field, $v)"
    case "Long"       => q"unpack2jsonOptOne(sb, $field, $v)"
    case "Double"     => q"unpack2jsonOptOne(sb, $field, $v)"
    case "Boolean"    => q"unpack2jsonOptOne(sb, $field, $v)"
    case "Date"       => q"unpack2jsonOptOneDate(sb, $field, $v)"
    case "UUID"       => q"unpack2jsonOptOneQuoted(sb, $field, $v)"
    case "URI"        => q"unpack2jsonOptOneQuoted(sb, $field, $v)"
    case "BigInt"     => q"unpack2jsonOptOne(sb, $field, $v)"
    case "BigDecimal" => q"unpack2jsonOptOne(sb, $field, $v)"
    case "enum"       => q"unpack2jsonOptOneQuoted(sb, $field, $v)"
    case "ref"        => q"unpack2jsonOptOne(sb, $field, $v)"
  }

  def unpack2jsonManyAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpack2jsonManyString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpack2jsonMany(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpack2jsonMany(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpack2jsonMany(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpack2jsonMany(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpack2jsonManyDate(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpack2jsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpack2jsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpack2jsonMany(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpack2jsonMany(sb, $field, $v, vs, $tabs)"
    case "enum"       => q"unpack2jsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "ref"        => q"unpack2jsonMany(sb, $field, $v, vs, $tabs)"
  }

  def unpack2jsonOptManyAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpack2jsonOptManyString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpack2jsonOptMany(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpack2jsonOptMany(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpack2jsonOptMany(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpack2jsonOptMany(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpack2jsonOptManyDate(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpack2jsonOptManyQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpack2jsonOptManyQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpack2jsonOptMany(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpack2jsonOptMany(sb, $field, $v, vs, $tabs)"
    case "enum"       => q"unpack2jsonOptManyQuoted(sb, $field, $v, vs, $tabs)"
    case "ref"        => q"unpack2jsonOptMany(sb, $field, $v, vs, $tabs)"
  }

  def unpack2jsonMapAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpack2jsonMapString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpack2jsonMap(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpack2jsonMap(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpack2jsonMap(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpack2jsonMap(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpack2jsonMapQuoted(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpack2jsonMapQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpack2jsonMapQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpack2jsonMap(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpack2jsonMap(sb, $field, $v, vs, $tabs)"
  }

  def unpack2jsonOptMapAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpack2jsonOptMapString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpack2jsonOptMap(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpack2jsonOptMap(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpack2jsonOptMap(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpack2jsonOptMap(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpack2jsonOptMapQuoted(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpack2jsonOptMapQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpack2jsonOptMapQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpack2jsonOptMap(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpack2jsonOptMap(sb, $field, $v, vs, $tabs)"
  }

  def unpack2jsonAggrOneList(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpack2jsonListString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpack2jsonList(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpack2jsonList(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpack2jsonList(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpack2jsonList(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpack2jsonListQuoted(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpack2jsonListQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpack2jsonListQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpack2jsonList(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpack2jsonList(sb, $field, $v, vs, $tabs)"
  }

  def unpack2jsonAggrManyList(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpack2jsonListSetString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpack2jsonListSet(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpack2jsonListSet(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpack2jsonListSet(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpack2jsonListSet(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpack2jsonListSetQuoted(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpack2jsonListSetQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpack2jsonListSetQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpack2jsonListSet(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpack2jsonListSet(sb, $field, $v, vs, $tabs)"
  }
}
