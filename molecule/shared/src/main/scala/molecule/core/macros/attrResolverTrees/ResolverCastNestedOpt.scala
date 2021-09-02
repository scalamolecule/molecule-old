package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait ResolverCastNestedOpt extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  def getResolverCastNestedOpt(group: String, baseType: String): Int => Tree = group match {
    case "One"          => castNestedOptOneAttr(baseType)
    case "OptOne"       => castNestedOptOptOneAttr(baseType)
    case "Many"         => castNestedOptManyAttr(baseType)
    case "OptMany"      => castNestedOptOptManyAttr(baseType)
    case "Map"          => castNestedOptMapAttr_(baseType)
    case "OptMap"       => castNestedOptOptMapAttr_(baseType)
    case "OptApplyOne"  => castNestedOptOptOneAttr(baseType)
    case "OptApplyMany" => castNestedOptOptManyAttr(baseType)
    case "OptApplyMap"  => castNestedOptOptMapAttr_(baseType)
  }


  lazy val castNestedOptAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castNestedOptOneAttr(t.tpeS) else castNestedOptManyAttr(t.tpeS)

  lazy val castNestedOptOneAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 0; (_: Int) => q"castNestedOptOne[String](it)"
    case "Int"        => lambdaIndex = 1; (_: Int) => q"castNestedOptOneInt(it)"
    case "Long"       => lambdaIndex = 3; (_: Int) => q"castNestedOptOne[Long](it)"
    case "Double"     => lambdaIndex = 4; (_: Int) => q"castNestedOptOne[Double](it)"
    case "Boolean"    => lambdaIndex = 5; (_: Int) => q"castNestedOptOne[Boolean](it)"
    case "Date"       => lambdaIndex = 6; (_: Int) => q"castNestedOptOne[Date](it)"
    case "UUID"       => lambdaIndex = 7; (_: Int) => q"castNestedOptOne[UUID](it)"
    case "URI"        => lambdaIndex = 8; (_: Int) => q"castNestedOptOne[URI](it)"
    case "BigInt"     => lambdaIndex = 9; (_: Int) => q"castNestedOptOneBigInt(it)"
    case "BigDecimal" => lambdaIndex = 10; (_: Int) => q"castNestedOptOneBigDecimal(it)"
    case "enum"       => lambdaIndex = -1; (_: Int) => q"castNestedOptOneEnum(it)"
    case "ref"        => lambdaIndex = -2; (_: Int) => q"castNestedOptOneRefAttr(it)"
  }

  lazy val castNestedOptManyAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 25; (_: Int) => q"castNestedOptMany[String](it)"
    case "Int"        => lambdaIndex = 26; (_: Int) => q"castNestedOptManyInt(it)"
    case "Long"       => lambdaIndex = 27; (_: Int) => q"castNestedOptMany[Long](it)"
    case "Double"     => lambdaIndex = 28; (_: Int) => q"castNestedOptMany[Double](it)"
    case "Boolean"    => lambdaIndex = 29; (_: Int) => q"castNestedOptMany[Boolean](it)"
    case "Date"       => lambdaIndex = 30; (_: Int) => q"castNestedOptMany[Date](it)"
    case "UUID"       => lambdaIndex = 31; (_: Int) => q"castNestedOptMany[UUID](it)"
    case "URI"        => lambdaIndex = 32; (_: Int) => q"castNestedOptMany[URI](it)"
    case "BigInt"     => lambdaIndex = 33; (_: Int) => q"castNestedOptManyBigInt(it)"
    case "BigDecimal" => lambdaIndex = 34; (_: Int) => q"castNestedOptManyBigDecimal(it)"
    case "enum"       => lambdaIndex = 24; (_: Int) => q"castNestedOptManyEnum(it)"
    case "ref"        => lambdaIndex = -3; (_: Int) => q"castNestedOptManyRefAttr(it)"
  }

  lazy val castNestedOptEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      lambdaIndex = -1
      (_: Int) => q"castNestedOptOneEnum(it)"
    } else {
      lambdaIndex = 24
      (_: Int) => q"castNestedOptManyEnum(it)"
    }

  lazy val castNestedOptRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      lambdaIndex = -2
      (_: Int) => q"castNestedOptOneRefAttr(it)"
    } else {
      lambdaIndex = -3
      (_: Int) => q"castNestedOptManyRefAttr(it)"
    }


  lazy val castNestedOptOptAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    castNestedOptOptOneAttr(t.tpeS)
  } else {
    castNestedOptOptManyAttr(t.tpeS)
  }

  lazy val castNestedOptOptOneAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 13; (_: Int) => q"castNestedOptOptOne[String](it)"
    case "Int"        => lambdaIndex = 14; (_: Int) => q"castNestedOptOptOneInt(it)"
    case "Long"       => lambdaIndex = 15; (_: Int) => q"castNestedOptOptOneLong(it)"
    case "Double"     => lambdaIndex = 16; (_: Int) => q"castNestedOptOptOneDouble(it)"
    case "Boolean"    => lambdaIndex = 17; (_: Int) => q"castNestedOptOptOne[Boolean](it)"
    case "Date"       => lambdaIndex = 18; (_: Int) => q"castNestedOptOptOne[Date](it)"
    case "UUID"       => lambdaIndex = 19; (_: Int) => q"castNestedOptOptOne[UUID](it)"
    case "URI"        => lambdaIndex = 20; (_: Int) => q"castNestedOptOptOneURI(it)"
    case "BigInt"     => lambdaIndex = 21; (_: Int) => q"castNestedOptOptOneBigInt(it)"
    case "BigDecimal" => lambdaIndex = 22; (_: Int) => q"castNestedOptOptOneBigDecimal(it)"
    case "enum"       => lambdaIndex = 12; (_: Int) => q"castNestedOptOptOneEnum(it)"
    case "ref"        => lambdaIndex = 23; (_: Int) => q"castNestedOptOptOneRefAttr(it)"
  }

  lazy val castNestedOptOptManyAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 36; (_: Int) => q"castNestedOptOptMany[String](it)"
    case "Int"        => lambdaIndex = 37; (_: Int) => q"castNestedOptOptManyInt(it)"
    case "Long"       => lambdaIndex = 38; (_: Int) => q"castNestedOptOptManyLong(it)"
    case "Double"     => lambdaIndex = 39; (_: Int) => q"castNestedOptOptManyDouble(it)"
    case "Boolean"    => lambdaIndex = 40; (_: Int) => q"castNestedOptOptMany[Boolean](it)"
    case "Date"       => lambdaIndex = 41; (_: Int) => q"castNestedOptOptMany[Date](it)"
    case "UUID"       => lambdaIndex = 42; (_: Int) => q"castNestedOptOptMany[UUID](it)"
    case "URI"        => lambdaIndex = 43; (_: Int) => q"castNestedOptOptManyURI(it)"
    case "BigInt"     => lambdaIndex = 44; (_: Int) => q"castNestedOptOptManyBigInt(it)"
    case "BigDecimal" => lambdaIndex = 45; (_: Int) => q"castNestedOptOptManyBigDecimal(it)"
    case "enum"       => lambdaIndex = 35; (_: Int) => q"castNestedOptOptManyEnum(it)"
    case "ref"        => lambdaIndex = 46; (_: Int) => q"castNestedOptOptManyRefAttr(it)"
  }

  lazy val castNestedOptOptEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      lambdaIndex = 12
      (_: Int) => q"castNestedOptOptOneEnum(it)"
    } else {
      lambdaIndex = 35
      (_: Int) => q"castNestedOptOptManyEnum(it)"
    }

  lazy val castNestedOptOptRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      lambdaIndex = 23
      (_: Int) => q"castNestedOptOptOneRefAttr(it)"
    } else {
      lambdaIndex = 46
      (_: Int) => q"castNestedOptOptManyRefAttr(it)"
    }

  lazy val castNestedOptMapAttr: richTree => Int => Tree = (t: richTree) => castNestedOptMapAttr_(t.tpeS)

  lazy val castNestedOptMapAttr_ : String => Int => Tree = {
    case "String"     => lambdaIndex = 47; (_: Int) => q"castNestedOptMapString(it)"
    case "Int"        => lambdaIndex = 48; (_: Int) => q"castNestedOptMapInt(it)"
    case "Long"       => lambdaIndex = 49; (_: Int) => q"castNestedOptMapLong(it)"
    case "Double"     => lambdaIndex = 50; (_: Int) => q"castNestedOptMapDouble(it)"
    case "Boolean"    => lambdaIndex = 51; (_: Int) => q"castNestedOptMapBoolean(it)"
    case "Date"       => lambdaIndex = 52; (_: Int) => q"castNestedOptMapDate(it)"
    case "UUID"       => lambdaIndex = 53; (_: Int) => q"castNestedOptMapUUID(it)"
    case "URI"        => lambdaIndex = 54; (_: Int) => q"castNestedOptMapURI(it)"
    case "BigInt"     => lambdaIndex = 55; (_: Int) => q"castNestedOptMapBigInt(it)"
    case "BigDecimal" => lambdaIndex = 56; (_: Int) => q"castNestedOptMapBigDecimal(it)"
  }

  lazy val castNestedOptOptMapAttr: richTree => Int => Tree = (t: richTree) => castNestedOptOptMapAttr_(t.tpeS)

  lazy val castNestedOptOptMapAttr_ : String => Int => Tree = {
    case "String"     => lambdaIndex = 57; (_: Int) => q"castNestedOptOptMapString(it)"
    case "Int"        => lambdaIndex = 58; (_: Int) => q"castNestedOptOptMapInt(it)"
    case "Long"       => lambdaIndex = 59; (_: Int) => q"castNestedOptOptMapLong(it)"
    case "Double"     => lambdaIndex = 60; (_: Int) => q"castNestedOptOptMapDouble(it)"
    case "Boolean"    => lambdaIndex = 61; (_: Int) => q"castNestedOptOptMapBoolean(it)"
    case "Date"       => lambdaIndex = 62; (_: Int) => q"castNestedOptOptMapDate(it)"
    case "UUID"       => lambdaIndex = 63; (_: Int) => q"castNestedOptOptMapUUID(it)"
    case "URI"        => lambdaIndex = 64; (_: Int) => q"castNestedOptOptMapURI(it)"
    case "BigInt"     => lambdaIndex = 65; (_: Int) => q"castNestedOptOptMapBigInt(it)"
    case "BigDecimal" => lambdaIndex = 66; (_: Int) => q"castNestedOptOptMapBigDecimal(it)"
  }
}
