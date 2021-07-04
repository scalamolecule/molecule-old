package molecule.core.macros.lambdaTrees

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonAggr extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  val jsonAggrOneList: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex, $tabs)"
  }

  val jsonAggrManyList: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex, $tabs)"
  }


  val jsonAggrOneListDistinct: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinctDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex, $tabs)"
  }

  val jsonAggrManyListDistinct: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinctDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex, $tabs)"
  }


  val jsonAggrOneListRand: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRandDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex, $tabs)"
  }

  val jsonAggrManyListRand: (String, String) => (Int, Int) => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRandDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex, $tabs)"
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
