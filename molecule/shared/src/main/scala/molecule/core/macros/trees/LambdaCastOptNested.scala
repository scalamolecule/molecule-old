package molecule.core.macros.trees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaCastOptNested extends LambdaJsonTypes {
  val c: blackbox.Context

  import c.universe._

  val castOptNestedOneAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptNestedOne[String](${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"castOptNestedOneInt(${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"castOptNestedOne[Long](${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"castOptNestedOne[Double](${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"castOptNestedOne[Boolean](${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"castOptNestedOne[Date](${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"castOptNestedOne[UUID](${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"castOptNestedOne[URI](${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"castOptNestedOneBigInt(${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"castOptNestedOneBigDecimal(${TermName("it" + colIndex)})"
    case "Any"        => (colIndex: Int) => q"row.get($colIndex)"
  }

  val castOptNestedManyAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptNestedMany[String](${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"castOptNestedManyInt(${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"castOptNestedMany[Long](${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"castOptNestedMany[Double](${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"castOptNestedMany[Boolean](${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"castOptNestedMany[Date](${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"castOptNestedMany[UUID](${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"castOptNestedMany[URI](${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"castOptNestedManyBigInt(${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"castOptNestedManyBigDecimal(${TermName("it" + colIndex)})"
  }

  val castOptNestedMandatoryAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOptNestedOneAttr(t.tpeS) else castOptNestedManyAttr(t.tpeS)

  val castOptNestedMandatoryRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (colIndex: Int) => q"castOptNestedOneRefAttr(${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"castOptNestedManyRefAttr(${TermName("it" + colIndex)})"

  val castOptNestedEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      (colIndex: Int) => q"castOptNestedOneEnum(${TermName("it" + colIndex)})"
    } else
      (colIndex: Int) => q"castOptNestedManyEnum(${TermName("it" + colIndex)})"


  val castOptNestedOptAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"castOptNestedOptOne[String](${TermName("it" + colIndex)})"
        case "Int"        => (colIndex: Int) => q"castOptNestedOptOneInt(${TermName("it" + colIndex)})"
        case "Long"       => (colIndex: Int) => q"castOptNestedOptOneLong(${TermName("it" + colIndex)})"
        case "Double"     => (colIndex: Int) => q"castOptNestedOptOneDouble(${TermName("it" + colIndex)})"
        case "Boolean"    => (colIndex: Int) => q"castOptNestedOptOne[Boolean](${TermName("it" + colIndex)})"
        case "Date"       => (colIndex: Int) => q"castOptNestedOptOne[Date](${TermName("it" + colIndex)})"
        case "UUID"       => (colIndex: Int) => q"castOptNestedOptOne[UUID](${TermName("it" + colIndex)})"
        case "URI"        => (colIndex: Int) => q"castOptNestedOptOne[URI](${TermName("it" + colIndex)})"
        case "BigInt"     => (colIndex: Int) => q"castOptNestedOptOneBigInt(${TermName("it" + colIndex)})"
        case "BigDecimal" => (colIndex: Int) => q"castOptNestedOptOneBigDecimal(${TermName("it" + colIndex)})"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"castOptNestedOptMany[String](${TermName("it" + colIndex)})"
        case "Int"        => (colIndex: Int) => q"castOptNestedOptManyInt(${TermName("it" + colIndex)})"
        case "Long"       => (colIndex: Int) => q"castOptNestedOptManyLong(${TermName("it" + colIndex)})"
        case "Double"     => (colIndex: Int) => q"castOptNestedOptManyDouble(${TermName("it" + colIndex)})"
        case "Boolean"    => (colIndex: Int) => q"castOptNestedOptMany[Boolean](${TermName("it" + colIndex)})"
        case "Date"       => (colIndex: Int) => q"castOptNestedOptMany[Date](${TermName("it" + colIndex)})"
        case "UUID"       => (colIndex: Int) => q"castOptNestedOptMany[UUID](${TermName("it" + colIndex)})"
        case "URI"        => (colIndex: Int) => q"castOptNestedOptMany[URI](${TermName("it" + colIndex)})"
        case "BigInt"     => (colIndex: Int) => q"castOptNestedOptManyBigInt(${TermName("it" + colIndex)})"
        case "BigDecimal" => (colIndex: Int) => q"castOptNestedOptManyBigDecimal(${TermName("it" + colIndex)})"
      }
    }


  val castOptNestedOptRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (colIndex: Int) => q"castOptNestedOptOneRefAttr(${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"castOptNestedOptManyRefAttr(${TermName("it" + colIndex)})"


  val castOptNestedOptEnum: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (colIndex: Int) => q"castOptNestedOptOneEnum(${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"castOptNestedOptManyEnum(${TermName("it" + colIndex)})"


  val castOptNestedMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (colIndex: Int) => q"castOptNestedMapString(${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"castOptNestedMapInt(${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"castOptNestedMapLong(${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"castOptNestedMapDouble(${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"castOptNestedMapBoolean(${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"castOptNestedMapDate(${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"castOptNestedMapUUID(${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"castOptNestedMapURI(${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"castOptNestedMapBigInt(${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"castOptNestedMapBigDecimal(${TermName("it" + colIndex)})"
  }

  val castOptNestedOptMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (colIndex: Int) => q"castOptNestedOptMapString(${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"castOptNestedOptMapInt(${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"castOptNestedOptMapLong(${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"castOptNestedOptMapDouble(${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"castOptNestedOptMapBoolean(${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"castOptNestedOptMapDate(${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"castOptNestedOptMapUUID(${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"castOptNestedOptMapURI(${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"castOptNestedOptMapBigInt(${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"castOptNestedOptMapBigDecimal(${TermName("it" + colIndex)})"
  }
}
