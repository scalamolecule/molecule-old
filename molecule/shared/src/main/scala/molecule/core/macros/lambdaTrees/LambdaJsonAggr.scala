package molecule.core.macros.lambdaTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonAggr extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val jsonAggrOneList: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, level: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex, $level)"
    case "Int"        => (colIndex: Int, level: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex, $level)"
    case "Long"       => (colIndex: Int, level: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex, $level)"
    case "Double"     => (colIndex: Int, level: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex, $level)"
    case "Boolean"    => (colIndex: Int, level: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex, $level)"
    case "Date"       => (colIndex: Int, level: Int) => q"jsonAggrOneListDate(sb, $field, row, $colIndex, $level)"
    case "UUID"       => (colIndex: Int, level: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex, $level)"
    case "URI"        => (colIndex: Int, level: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex, $level)"
    case "BigInt"     => (colIndex: Int, level: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex, $level)"
    case "BigDecimal" => (colIndex: Int, level: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex, $level)"
  }

  val jsonAggrManyList: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, level: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex, $level)"
    case "Int"        => (colIndex: Int, level: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex, $level)"
    case "Long"       => (colIndex: Int, level: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex, $level)"
    case "Double"     => (colIndex: Int, level: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex, $level)"
    case "Boolean"    => (colIndex: Int, level: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex, $level)"
    case "Date"       => (colIndex: Int, level: Int) => q"jsonAggrManyListDate(sb, $field, row, $colIndex, $level)"
    case "UUID"       => (colIndex: Int, level: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex, $level)"
    case "URI"        => (colIndex: Int, level: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex, $level)"
    case "BigInt"     => (colIndex: Int, level: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex, $level)"
    case "BigDecimal" => (colIndex: Int, level: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex, $level)"
  }


  val jsonAggrOneListDistinct: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex, $level)"
    case "Int"        => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex, $level)"
    case "Long"       => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex, $level)"
    case "Double"     => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex, $level)"
    case "Boolean"    => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex, $level)"
    case "Date"       => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinctDate(sb, $field, row, $colIndex, $level)"
    case "UUID"       => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex, $level)"
    case "URI"        => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex, $level)"
    case "BigInt"     => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex, $level)"
    case "BigDecimal" => (colIndex: Int, level: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex, $level)"
  }

  val jsonAggrManyListDistinct: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex, $level)"
    case "Int"        => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex, $level)"
    case "Long"       => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex, $level)"
    case "Double"     => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex, $level)"
    case "Boolean"    => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex, $level)"
    case "Date"       => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinctDate(sb, $field, row, $colIndex, $level)"
    case "UUID"       => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex, $level)"
    case "URI"        => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex, $level)"
    case "BigInt"     => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex, $level)"
    case "BigDecimal" => (colIndex: Int, level: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex, $level)"
  }


  val jsonAggrOneListRand: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, level: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex, $level)"
    case "Int"        => (colIndex: Int, level: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex, $level)"
    case "Long"       => (colIndex: Int, level: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex, $level)"
    case "Double"     => (colIndex: Int, level: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex, $level)"
    case "Boolean"    => (colIndex: Int, level: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex, $level)"
    case "Date"       => (colIndex: Int, level: Int) => q"jsonAggrOneListRandDate(sb, $field, row, $colIndex, $level)"
    case "UUID"       => (colIndex: Int, level: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex, $level)"
    case "URI"        => (colIndex: Int, level: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex, $level)"
    case "BigInt"     => (colIndex: Int, level: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex, $level)"
    case "BigDecimal" => (colIndex: Int, level: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex, $level)"
  }

  val jsonAggrManyListRand: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, level: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex, $level)"
    case "Int"        => (colIndex: Int, level: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex, $level)"
    case "Long"       => (colIndex: Int, level: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex, $level)"
    case "Double"     => (colIndex: Int, level: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex, $level)"
    case "Boolean"    => (colIndex: Int, level: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex, $level)"
    case "Date"       => (colIndex: Int, level: Int) => q"jsonAggrManyListRandDate(sb, $field, row, $colIndex, $level)"
    case "UUID"       => (colIndex: Int, level: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex, $level)"
    case "URI"        => (colIndex: Int, level: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex, $level)"
    case "BigInt"     => (colIndex: Int, level: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex, $level)"
    case "BigDecimal" => (colIndex: Int, level: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex, $level)"
  }


  val jsonAggrSingleSample: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, _: Int) => q"jsonAggrSingleSampleQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int, _: Int) => q"jsonAggrSingleSample(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int, _: Int) => q"jsonAggrSingleSample(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int, _: Int) => q"jsonAggrSingleSample(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int, _: Int) => q"jsonAggrSingleSampleToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int, _: Int) => q"jsonAggrSingleSampleDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int, _: Int) => q"jsonAggrSingleSampleQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int, _: Int) => q"jsonAggrSingleSampleQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int, _: Int) => q"jsonAggrSingleSampleToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonAggrSingleSampleToString(sb, $field, row, $colIndex)"
  }


  val jsonAggrOneSingle: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int, _: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int, _: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int, _: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int, _: Int) => q"jsonOneDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
  }


  val jsonAggrManySingle: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, _: Int) => q"jsonAggrManySingleQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int, _: Int) => q"jsonAggrManySingle(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int, _: Int) => q"jsonAggrManySingle(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int, _: Int) => q"jsonAggrManySingle(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int, _: Int) => q"jsonAggrManySingleToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int, _: Int) => q"jsonAggrManySingleDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int, _: Int) => q"jsonAggrManySingleQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int, _: Int) => q"jsonAggrManySingleQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int, _: Int) => q"jsonAggrManySingleToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonAggrManySingleToString(sb, $field, row, $colIndex)"
  }
}
