package molecule.core.marshalling.unpackAttr

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

trait PackedValue2cast extends TreeOps with String2cast {
  val c: blackbox.Context

  import c.universe._

  def getPackedValue2cast(group: String, baseTpe: String, v: Tree, optAggrTpe: Option[String]): Tree = {
    val tpe = optAggrTpe.getOrElse(baseTpe)
    group match {
      case "One" | "KeyedMap" | "AggrSingleSample" | "AggrOneSingle"    => unpackOneAttr(tpe, v)
      case "OptOne" | "OptApplyOne"                                     => unpackOptOneAttr(tpe, v)
      case "Many" | "AggrManySingle"                                    => unpackManyAttr(tpe, v)
      case "OptMany" | "OptApplyMany"                                   => unpackOptManyAttr(tpe, v)
      case "Map"                                                        => unpackMapAttr(tpe, v)
      case "OptMap" | "OptApplyMap"                                     => unpackOptMapAttr(tpe, v)
      case "AggrOneList" | "AggrOneListDistinct" | "AggrOneListRand"    => unpackAggrOneList(tpe, v)
      case "AggrManyList" | "AggrManyListDistinct" | "AggrManyListRand" => unpackAggrManyList(tpe, v)
    }
  }

  def unpackOneAttr(tpe: String, v: Tree): Tree = tpe match {
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

  def unpackOptOneAttr(tpe: String, v: Tree): Tree = tpe match {
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

  def unpackManyAttr(tpe: String, v: Tree): Tree = tpe match {
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

  def unpackOptManyAttr(tpe: String, v: Tree): Tree = tpe match {
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

  def unpackMapAttr(tpe: String, v: Tree): Tree = tpe match {
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

  def unpackOptMapAttr(tpe: String, v: Tree): Tree = tpe match {
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

  def unpackAggrOneList(tpe: String, v: Tree): Tree = tpe match {
    case "String"           => q"unpackListString($v, vs)"
    case "Int"              => q"unpackListInt($v, vs)"
    case "Long"             => q"unpackListLong($v, vs)"
    case "Double"           => q"unpackListDouble($v, vs)"
    case "Boolean"          => q"unpackListBoolean($v, vs)"
    case "Date"             => q"unpackListDate($v, vs)"
    case "UUID"             => q"unpackListUUID($v, vs)"
    case "URI"              => q"unpackListURI($v, vs)"
    case "BigInt"           => q"unpackListBigInt($v, vs)"
    case "BigDecimal"       => q"unpackListBigDecimal($v, vs)"
    case "List[String]"     => q"unpackListString($v, vs)"
    case "List[Int]"        => q"unpackListInt($v, vs)"
    case "List[Long]"       => q"unpackListLong($v, vs)"
    case "List[Double]"     => q"unpackListDouble($v, vs)"
    case "List[Boolean]"    => q"unpackListBoolean($v, vs)"
    case "List[Date]"       => q"unpackListDate($v, vs)"
    case "List[UUID]"       => q"unpackListUUID($v, vs)"
    case "List[URI]"        => q"unpackListURI($v, vs)"
    case "List[BigInt]"     => q"unpackListBigInt($v, vs)"
    case "List[BigDecimal]" => q"unpackListBigDecimal($v, vs)"
  }

  def unpackAggrManyList(tpe: String, v: Tree): Tree = tpe match {
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
