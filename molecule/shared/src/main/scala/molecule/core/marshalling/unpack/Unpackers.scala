package molecule.core.marshalling.unpack

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

trait Unpackers extends TreeOps with UnpackTypes {
  val c: blackbox.Context

  import c.universe._

  def unpackLambdas(group: String, baseTpe: String, v: Tree): Tree = group match {
    case "One" | "KeyedMap" | "AggrSingleSample" | "AggrOneSingle"    => unpackOneAttr(baseTpe, v)
    case "OptOne" | "OptApplyOne"                                     => unpackOptOneAttr(baseTpe, v)
    case "Many" | "AggrManySingle"                                    => unpackManyAttr(baseTpe, v)
    case "OptMany" | "OptApplyMany"                                   => unpackOptManyAttr(baseTpe, v)
    case "Map"                                                        => unpackMapAttr(baseTpe, v)
    case "OptMap" | "OptApplyMap"                                     => unpackOptMapAttr(baseTpe, v)
    case "AggrOneList" | "AggrOneListDistinct" | "AggrOneListRand"    => unpackAggrOneList(baseTpe, v)
    case "AggrManyList" | "AggrManyListDistinct" | "AggrManyListRand" => unpackAggrManyList(baseTpe, v)
  }

  def unpackOneAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackOneString($v, vs)"
    case "Int"        => q"unpackOneInt($v)"
    case "Long"       => q"unpackOneLong($v)"
    case "Double"     => q"unpackOneDouble($v)"
    case "Boolean"    => q"unpackOneBoolean($v)"
    case "Date"       => q"unpackOneDate($v)"
    case "UUID"       => q"unpackOneUUID($v)"
    case "URI"        => q"unpackOneURI($v)"
    case "BigInt"     => q"unpackOneBigInt($v)"
    case "BigDecimal" => q"unpackOneBigDecimal($v)"
    case "Any"        => q"unpackOneAny($v)"
    case "enum"       => q"unpackOneEnum($v)"
    case "ref"        => q"unpackOneLong($v)"
  }

  def unpackOptOneAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackOptOneString($v, vs)"
    case "Int"        => q"unpackOptOneInt($v)"
    case "Long"       => q"unpackOptOneLong($v)"
    case "Double"     => q"unpackOptOneDouble($v)"
    case "Boolean"    => q"unpackOptOneBoolean($v)"
    case "Date"       => q"unpackOptOneDate($v)"
    case "UUID"       => q"unpackOptOneUUID($v)"
    case "URI"        => q"unpackOptOneURI($v)"
    case "BigInt"     => q"unpackOptOneBigInt($v)"
    case "BigDecimal" => q"unpackOptOneBigDecimal($v)"
    case "enum"       => q"unpackOptOneEnum($v)"
    case "ref"        => q"unpackOptOneLong($v)"
  }

  def unpackManyAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackManyString($v, vs)"
    case "Int"        => q"unpackManyInt($v, vs)"
    case "Long"       => q"unpackManyLong($v, vs)"
    case "Double"     => q"unpackManyDouble($v, vs)"
    case "Boolean"    => q"unpackManyBoolean($v, vs)"
    case "Date"       => q"unpackManyDate($v, vs)"
    case "UUID"       => q"unpackManyUUID($v, vs)"
    case "URI"        => q"unpackManyURI($v, vs)"
    case "BigInt"     => q"unpackManyBigInt($v, vs)"
    case "BigDecimal" => q"unpackManyBigDecimal($v, vs)"
    case "enum"       => q"unpackManyEnum($v, vs)"
    case "ref"        => q"unpackManyLong($v, vs)"
  }

  def unpackOptManyAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackOptManyString($v, vs)"
    case "Int"        => q"unpackOptManyInt($v, vs)"
    case "Long"       => q"unpackOptManyLong($v, vs)"
    case "Double"     => q"unpackOptManyDouble($v, vs)"
    case "Boolean"    => q"unpackOptManyBoolean($v, vs)"
    case "Date"       => q"unpackOptManyDate($v, vs)"
    case "UUID"       => q"unpackOptManyUUID($v, vs)"
    case "URI"        => q"unpackOptManyURI($v, vs)"
    case "BigInt"     => q"unpackOptManyBigInt($v, vs)"
    case "BigDecimal" => q"unpackOptManyBigDecimal($v, vs)"
    case "enum"       => q"unpackOptManyEnum($v, vs)"
    case "ref"        => q"unpackOptManyLong($v, vs)"
  }

  def unpackMapAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackMapString($v, vs)"
    case "Int"        => q"unpackMapInt($v, vs)"
    case "Long"       => q"unpackMapLong($v, vs)"
    case "Double"     => q"unpackMapDouble($v, vs)"
    case "Boolean"    => q"unpackMapBoolean($v, vs)"
    case "Date"       => q"unpackMapDate($v, vs)"
    case "UUID"       => q"unpackMapUUID($v, vs)"
    case "URI"        => q"unpackMapURI($v, vs)"
    case "BigInt"     => q"unpackMapBigInt($v, vs)"
    case "BigDecimal" => q"unpackMapBigDecimal($v, vs)"
  }

  def unpackOptMapAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackOptMapString($v, vs)"
    case "Int"        => q"unpackOptMapInt($v, vs)"
    case "Long"       => q"unpackOptMapLong($v, vs)"
    case "Double"     => q"unpackOptMapDouble($v, vs)"
    case "Boolean"    => q"unpackOptMapBoolean($v, vs)"
    case "Date"       => q"unpackOptMapDate($v, vs)"
    case "UUID"       => q"unpackOptMapUUID($v, vs)"
    case "URI"        => q"unpackOptMapURI($v, vs)"
    case "BigInt"     => q"unpackOptMapBigInt($v, vs)"
    case "BigDecimal" => q"unpackOptMapBigDecimal($v, vs)"
  }

  def unpackAggrOneList(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackListString($v, vs)"
    case "Int"        => q"unpackListInt($v, vs)"
    case "Long"       => q"unpackListLong($v, vs)"
    case "Double"     => q"unpackListDouble($v, vs)"
    case "Boolean"    => q"unpackListBoolean($v, vs)"
    case "Date"       => q"unpackListDate($v, vs)"
    case "UUID"       => q"unpackListUUID($v, vs)"
    case "URI"        => q"unpackListURI($v, vs)"
    case "BigInt"     => q"unpackListBigInt($v, vs)"
    case "BigDecimal" => q"unpackListBigDecimal($v, vs)"
  }

  def unpackAggrManyList(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackListSetString($v, vs)"
    case "Int"        => q"unpackListSetInt($v, vs)"
    case "Long"       => q"unpackListSetLong($v, vs)"
    case "Double"     => q"unpackListSetDouble($v, vs)"
    case "Boolean"    => q"unpackListSetBoolean($v, vs)"
    case "Date"       => q"unpackListSetDate($v, vs)"
    case "UUID"       => q"unpackListSetUUID($v, vs)"
    case "URI"        => q"unpackListSetURI($v, vs)"
    case "BigInt"     => q"unpackListSetBigInt($v, vs)"
    case "BigDecimal" => q"unpackListSetBigDecimal($v, vs)"
  }
}
