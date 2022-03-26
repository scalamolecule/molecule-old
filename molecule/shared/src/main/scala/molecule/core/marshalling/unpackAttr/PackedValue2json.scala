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
      case "One" | "KeyedMap" | "AggrSingleSample" | "AggrOneSingle"    => unpackJsonOneAttr(field, tpe, v)
      case "OptOne" | "OptApplyOne"                                     => unpackJsonOptOneAttr(field, tpe, v)
      case "Many"                                                       => unpackJsonManyAttr(field, tpe, v, tabs)
      case "OptMany" | "OptApplyMany"                                   => unpackJsonOptManyAttr(field, tpe, v, tabs)
      case "Map"                                                        => unpackJsonMapAttr(field, tpe, v, tabs)
      case "OptMap" | "OptApplyMap"                                     => unpackJsonOptMapAttr(field, tpe, v, tabs)
      case "AggrOneList" | "AggrOneListDistinct" | "AggrOneListRand"    => unpackJsonAggrOneList(field, tpe, v, tabs)
      case "AggrManyList" | "AggrManyListDistinct" | "AggrManyListRand" => unpackJsonAggrManyList(field, tpe, v, tabs)
      case "AggrManySingle"                                             => unpackJsonAggrManySingleAttr(field, tpe, v, tabs)
    }
  }

  def unpackJsonOneAttr(field: String, tpe: String, v: Tree): Tree = tpe match {
    case "String"     => q"unpackJsonOneString(sb, $field, $v, vs)"
    case "Int"        => q"unpackJsonOne(sb, $field, $v)"
    case "Long"       => q"unpackJsonOne(sb, $field, $v)"
    case "Double"     => q"unpackJsonOne(sb, $field, $v)"
    case "Boolean"    => q"unpackJsonOne(sb, $field, $v)"
    case "Date"       => q"unpackJsonOneDate(sb, $field, $v)"
    case "UUID"       => q"unpackJsonOneQuoted(sb, $field, $v)"
    case "URI"        => q"unpackJsonOneQuoted(sb, $field, $v)"
    case "BigInt"     => q"unpackJsonOneQuoted(sb, $field, $v)"
    case "BigDecimal" => q"unpackJsonOneQuoted(sb, $field, $v)"
    case "Any"        => q"unpackJsonOneAny(sb, $field, $v, vs)"
    case "enum"       => q"unpackJsonOneQuoted(sb, $field, $v)"
    case "ref"        => q"unpackJsonOne(sb, $field, $v)"
  }

  def unpackJsonOptOneAttr(field: String, tpe: String, v: Tree): Tree = tpe match {
    case "String"     => q"unpackJsonOptOneString(sb, $field, $v, vs)"
    case "Int"        => q"unpackJsonOptOne(sb, $field, $v)"
    case "Long"       => q"unpackJsonOptOne(sb, $field, $v)"
    case "Double"     => q"unpackJsonOptOne(sb, $field, $v)"
    case "Boolean"    => q"unpackJsonOptOne(sb, $field, $v)"
    case "Date"       => q"unpackJsonOptOneDate(sb, $field, $v)"
    case "UUID"       => q"unpackJsonOptOneQuoted(sb, $field, $v)"
    case "URI"        => q"unpackJsonOptOneQuoted(sb, $field, $v)"
    case "BigInt"     => q"unpackJsonOptOneQuoted(sb, $field, $v)"
    case "BigDecimal" => q"unpackJsonOptOneQuoted(sb, $field, $v)"
    case "enum"       => q"unpackJsonOptOneQuoted(sb, $field, $v)"
    case "ref"        => q"unpackJsonOptOne(sb, $field, $v)"
  }

  def unpackJsonManyAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpackJsonManyString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpackJsonManyDate(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "enum"       => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "ref"        => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
  }

  def unpackJsonOptManyAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpackJsonOptManyString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpackJsonOptMany(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpackJsonOptMany(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpackJsonOptMany(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpackJsonOptMany(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpackJsonOptManyDate(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpackJsonOptManyQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpackJsonOptManyQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpackJsonOptManyQuoted(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpackJsonOptManyQuoted(sb, $field, $v, vs, $tabs)"
    case "enum"       => q"unpackJsonOptManyQuoted(sb, $field, $v, vs, $tabs)"
    case "ref"        => q"unpackJsonOptMany(sb, $field, $v, vs, $tabs)"
  }

  def unpackJsonMapAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpackJsonMapString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpackJsonMap(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpackJsonMap(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpackJsonMap(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpackJsonMap(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpackJsonMapQuoted(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpackJsonMapQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpackJsonMapQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpackJsonMapQuoted(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpackJsonMapQuoted(sb, $field, $v, vs, $tabs)"
  }

  def unpackJsonOptMapAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "String"     => q"unpackJsonOptMapString(sb, $field, $v, vs, $tabs)"
    case "Int"        => q"unpackJsonOptMap(sb, $field, $v, vs, $tabs)"
    case "Long"       => q"unpackJsonOptMap(sb, $field, $v, vs, $tabs)"
    case "Double"     => q"unpackJsonOptMap(sb, $field, $v, vs, $tabs)"
    case "Boolean"    => q"unpackJsonOptMap(sb, $field, $v, vs, $tabs)"
    case "Date"       => q"unpackJsonOptMapQuoted(sb, $field, $v, vs, $tabs)"
    case "UUID"       => q"unpackJsonOptMapQuoted(sb, $field, $v, vs, $tabs)"
    case "URI"        => q"unpackJsonOptMapQuoted(sb, $field, $v, vs, $tabs)"
    case "BigInt"     => q"unpackJsonOptMapQuoted(sb, $field, $v, vs, $tabs)"
    case "BigDecimal" => q"unpackJsonOptMapQuoted(sb, $field, $v, vs, $tabs)"
  }

  def unpackJsonAggrOneList(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "List[String]"     => q"unpackJsonListString(sb, $field, $v, vs, $tabs)"
    case "List[Int]"        => q"unpackJsonList(sb, $field, $v, vs, $tabs)"
    case "List[Long]"       => q"unpackJsonList(sb, $field, $v, vs, $tabs)"
    case "List[Double]"     => q"unpackJsonList(sb, $field, $v, vs, $tabs)"
    case "List[Boolean]"    => q"unpackJsonList(sb, $field, $v, vs, $tabs)"
    case "List[Date]"       => q"unpackJsonListQuoted(sb, $field, $v, vs, $tabs)"
    case "List[UUID]"       => q"unpackJsonListQuoted(sb, $field, $v, vs, $tabs)"
    case "List[URI]"        => q"unpackJsonListQuoted(sb, $field, $v, vs, $tabs)"
    case "List[BigInt]"     => q"unpackJsonListQuoted(sb, $field, $v, vs, $tabs)"
    case "List[BigDecimal]" => q"unpackJsonListQuoted(sb, $field, $v, vs, $tabs)"
  }

  def unpackJsonAggrManyList(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "List[String]"     => q"unpackJsonListSetString(sb, $field, $v, vs, $tabs)"
    case "List[Int]"        => q"unpackJsonListSet(sb, $field, $v, vs, $tabs)"
    case "List[Long]"       => q"unpackJsonListSet(sb, $field, $v, vs, $tabs)"
    case "List[Double]"     => q"unpackJsonListSet(sb, $field, $v, vs, $tabs)"
    case "List[Boolean]"    => q"unpackJsonListSet(sb, $field, $v, vs, $tabs)"
    case "List[Date]"       => q"unpackJsonListSetQuoted(sb, $field, $v, vs, $tabs)"
    case "List[UUID]"       => q"unpackJsonListSetQuoted(sb, $field, $v, vs, $tabs)"
    case "List[URI]"        => q"unpackJsonListSetQuoted(sb, $field, $v, vs, $tabs)"
    case "List[BigInt]"     => q"unpackJsonListSetQuoted(sb, $field, $v, vs, $tabs)"
    case "List[BigDecimal]" => q"unpackJsonListSetQuoted(sb, $field, $v, vs, $tabs)"
  }


  def unpackJsonAggrManySingleAttr(field: String, tpe: String, v: Tree, tabs: Int): Tree = tpe match {
    case "Set[String]"     => q"unpackJsonManyString(sb, $field, $v, vs, $tabs)"
    case "Set[Int]"        => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
    case "Set[Long]"       => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
    case "Set[Double]"     => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
    case "Set[Boolean]"    => q"unpackJsonMany(sb, $field, $v, vs, $tabs)"
    case "Set[Date]"       => q"unpackJsonManyDate(sb, $field, $v, vs, $tabs)"
    case "Set[UUID]"       => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "Set[URI]"        => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "Set[BigInt]"     => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "Set[BigDecimal]" => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
    case "Set[enum]"       => q"unpackJsonManyQuoted(sb, $field, $v, vs, $tabs)"
  }
}
