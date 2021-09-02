package molecule.core.macros.attrResolverTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait ResolverCastOptNested extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  def getResolverCastOptNested(group: String, baseTpe: String): Int => Tree = group match {
    case "One"          => castOptNestedOneAttr(baseTpe)
    case "OptOne"       => castOptNestedOptOneAttr(baseTpe)
    case "Many"         => castOptNestedManyAttr(baseTpe)
    case "OptMany"      => castOptNestedOptManyAttr(baseTpe)
    case "Map"          => castOptNestedMapAttr_(baseTpe)
    case "OptMap"       => castOptNestedOptMapAttr_(baseTpe)
    case "OptApplyOne"  => castOptNestedOptOneAttr(baseTpe)
    case "OptApplyMany" => castOptNestedOptManyAttr(baseTpe)
    case "OptApplyMap"  => castOptNestedOptMapAttr_(baseTpe)
  }


  lazy val castOptNestedAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOptNestedOneAttr(t.tpeS) else castOptNestedManyAttr(t.tpeS)

  lazy val castOptNestedOneAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 0; (_: Int) => q"castOptNestedOne[String](it)"
    case "Int"        => lambdaIndex = 1; (_: Int) => q"castOptNestedOneInt(it)"
    case "Long"       => lambdaIndex = 3; (_: Int) => q"castOptNestedOne[Long](it)"
    case "Double"     => lambdaIndex = 4; (_: Int) => q"castOptNestedOne[Double](it)"
    case "Boolean"    => lambdaIndex = 5; (_: Int) => q"castOptNestedOne[Boolean](it)"
    case "Date"       => lambdaIndex = 6; (_: Int) => q"castOptNestedOne[Date](it)"
    case "UUID"       => lambdaIndex = 7; (_: Int) => q"castOptNestedOne[UUID](it)"
    case "URI"        => lambdaIndex = 8; (_: Int) => q"castOptNestedOne[URI](it)"
    case "BigInt"     => lambdaIndex = 9; (_: Int) => q"castOptNestedOneBigInt(it)"
    case "BigDecimal" => lambdaIndex = 10; (_: Int) => q"castOptNestedOneBigDecimal(it)"
    case "enum"       => lambdaIndex = -1; (_: Int) => q"castOptNestedOneEnum(it)"
    case "ref"        => lambdaIndex = -2; (_: Int) => q"castOptNestedOneRefAttr(it)"
  }

  lazy val castOptNestedManyAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 25; (_: Int) => q"castOptNestedMany[String](it)"
    case "Int"        => lambdaIndex = 26; (_: Int) => q"castOptNestedManyInt(it)"
    case "Long"       => lambdaIndex = 27; (_: Int) => q"castOptNestedMany[Long](it)"
    case "Double"     => lambdaIndex = 28; (_: Int) => q"castOptNestedMany[Double](it)"
    case "Boolean"    => lambdaIndex = 29; (_: Int) => q"castOptNestedMany[Boolean](it)"
    case "Date"       => lambdaIndex = 30; (_: Int) => q"castOptNestedMany[Date](it)"
    case "UUID"       => lambdaIndex = 31; (_: Int) => q"castOptNestedMany[UUID](it)"
    case "URI"        => lambdaIndex = 32; (_: Int) => q"castOptNestedMany[URI](it)"
    case "BigInt"     => lambdaIndex = 33; (_: Int) => q"castOptNestedManyBigInt(it)"
    case "BigDecimal" => lambdaIndex = 34; (_: Int) => q"castOptNestedManyBigDecimal(it)"
    case "enum"       => lambdaIndex = 24; (_: Int) => q"castOptNestedManyEnum(it)"
    case "ref"        => lambdaIndex = -3; (_: Int) => q"castOptNestedManyRefAttr(it)"
  }

  lazy val castOptNestedEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      lambdaIndex = -1
      (_: Int) => q"castOptNestedOneEnum(it)"
    } else {
      lambdaIndex = 24
      (_: Int) => q"castOptNestedManyEnum(it)"
    }

  lazy val castOptNestedRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      lambdaIndex = -2
      (_: Int) => q"castOptNestedOneRefAttr(it)"
    } else {
      lambdaIndex = -3
      (_: Int) => q"castOptNestedManyRefAttr(it)"
    }


  lazy val castOptNestedOptAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    castOptNestedOptOneAttr(t.tpeS)
  } else {
    castOptNestedOptManyAttr(t.tpeS)
  }

  lazy val castOptNestedOptOneAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 13; (_: Int) => q"castOptNestedOptOne[String](it)"
    case "Int"        => lambdaIndex = 14; (_: Int) => q"castOptNestedOptOneInt(it)"
    case "Long"       => lambdaIndex = 15; (_: Int) => q"castOptNestedOptOneLong(it)"
    case "Double"     => lambdaIndex = 16; (_: Int) => q"castOptNestedOptOneDouble(it)"
    case "Boolean"    => lambdaIndex = 17; (_: Int) => q"castOptNestedOptOne[Boolean](it)"
    case "Date"       => lambdaIndex = 18; (_: Int) => q"castOptNestedOptOne[Date](it)"
    case "UUID"       => lambdaIndex = 19; (_: Int) => q"castOptNestedOptOne[UUID](it)"
    case "URI"        => lambdaIndex = 20; (_: Int) => q"castOptNestedOptOneURI(it)"
    case "BigInt"     => lambdaIndex = 21; (_: Int) => q"castOptNestedOptOneBigInt(it)"
    case "BigDecimal" => lambdaIndex = 22; (_: Int) => q"castOptNestedOptOneBigDecimal(it)"
    case "enum"       => lambdaIndex = 12; (_: Int) => q"castOptNestedOptOneEnum(it)"
    case "ref"        => lambdaIndex = 23; (_: Int) => q"castOptNestedOptOneRefAttr(it)"
  }

  lazy val castOptNestedOptManyAttr: String => Int => Tree = {
    case "String"     => lambdaIndex = 36; (_: Int) => q"castOptNestedOptMany[String](it)"
    case "Int"        => lambdaIndex = 37; (_: Int) => q"castOptNestedOptManyInt(it)"
    case "Long"       => lambdaIndex = 38; (_: Int) => q"castOptNestedOptManyLong(it)"
    case "Double"     => lambdaIndex = 39; (_: Int) => q"castOptNestedOptManyDouble(it)"
    case "Boolean"    => lambdaIndex = 40; (_: Int) => q"castOptNestedOptMany[Boolean](it)"
    case "Date"       => lambdaIndex = 41; (_: Int) => q"castOptNestedOptMany[Date](it)"
    case "UUID"       => lambdaIndex = 42; (_: Int) => q"castOptNestedOptMany[UUID](it)"
    case "URI"        => lambdaIndex = 43; (_: Int) => q"castOptNestedOptManyURI(it)"
    case "BigInt"     => lambdaIndex = 44; (_: Int) => q"castOptNestedOptManyBigInt(it)"
    case "BigDecimal" => lambdaIndex = 45; (_: Int) => q"castOptNestedOptManyBigDecimal(it)"
    case "enum"       => lambdaIndex = 35; (_: Int) => q"castOptNestedOptManyEnum(it)"
    case "ref"        => lambdaIndex = 46; (_: Int) => q"castOptNestedOptManyRefAttr(it)"
  }

  lazy val castOptNestedOptEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      lambdaIndex = 12
      (_: Int) => q"castOptNestedOptOneEnum(it)"
    } else {
      lambdaIndex = 35
      (_: Int) => q"castOptNestedOptManyEnum(it)"
    }

  lazy val castOptNestedOptRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      lambdaIndex = 23
      (_: Int) => q"castOptNestedOptOneRefAttr(it)"
    } else {
      lambdaIndex = 46
      (_: Int) => q"castOptNestedOptManyRefAttr(it)"
    }

  lazy val castOptNestedMapAttr: richTree => Int => Tree = (t: richTree) => castOptNestedMapAttr_(t.tpeS)

  lazy val castOptNestedMapAttr_ : String => Int => Tree = {
    case "String"     => lambdaIndex = 47; (_: Int) => q"castOptNestedMapString(it)"
    case "Int"        => lambdaIndex = 48; (_: Int) => q"castOptNestedMapInt(it)"
    case "Long"       => lambdaIndex = 49; (_: Int) => q"castOptNestedMapLong(it)"
    case "Double"     => lambdaIndex = 50; (_: Int) => q"castOptNestedMapDouble(it)"
    case "Boolean"    => lambdaIndex = 51; (_: Int) => q"castOptNestedMapBoolean(it)"
    case "Date"       => lambdaIndex = 52; (_: Int) => q"castOptNestedMapDate(it)"
    case "UUID"       => lambdaIndex = 53; (_: Int) => q"castOptNestedMapUUID(it)"
    case "URI"        => lambdaIndex = 54; (_: Int) => q"castOptNestedMapURI(it)"
    case "BigInt"     => lambdaIndex = 55; (_: Int) => q"castOptNestedMapBigInt(it)"
    case "BigDecimal" => lambdaIndex = 56; (_: Int) => q"castOptNestedMapBigDecimal(it)"
  }

  lazy val castOptNestedOptMapAttr: richTree => Int => Tree = (t: richTree) => castOptNestedOptMapAttr_(t.tpeS)

  lazy val castOptNestedOptMapAttr_ : String => Int => Tree = {
    case "String"     => lambdaIndex = 57; (_: Int) => q"castOptNestedOptMapString(it)"
    case "Int"        => lambdaIndex = 58; (_: Int) => q"castOptNestedOptMapInt(it)"
    case "Long"       => lambdaIndex = 59; (_: Int) => q"castOptNestedOptMapLong(it)"
    case "Double"     => lambdaIndex = 60; (_: Int) => q"castOptNestedOptMapDouble(it)"
    case "Boolean"    => lambdaIndex = 61; (_: Int) => q"castOptNestedOptMapBoolean(it)"
    case "Date"       => lambdaIndex = 62; (_: Int) => q"castOptNestedOptMapDate(it)"
    case "UUID"       => lambdaIndex = 63; (_: Int) => q"castOptNestedOptMapUUID(it)"
    case "URI"        => lambdaIndex = 64; (_: Int) => q"castOptNestedOptMapURI(it)"
    case "BigInt"     => lambdaIndex = 65; (_: Int) => q"castOptNestedOptMapBigInt(it)"
    case "BigDecimal" => lambdaIndex = 66; (_: Int) => q"castOptNestedOptMapBigDecimal(it)"
  }
}
