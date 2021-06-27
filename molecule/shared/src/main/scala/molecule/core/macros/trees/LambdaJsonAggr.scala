package molecule.core.macros.trees

import scala.reflect.macros.blackbox

private[molecule] trait LambdaJsonAggr extends LambdaJsonOptNested {
  val c: blackbox.Context

  import c.universe._

  val jsonAggrOneList: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonAggrOneList(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonAggrOneListDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonAggrOneListQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonAggrOneListToString(sb, $field, row, $colIndex)"
  }

  val jsonAggrManyList: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonAggrManyList(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonAggrManyListDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonAggrManyListQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonAggrManyListToString(sb, $field, row, $colIndex)"
  }


  val jsonAggrOneListDistinct: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonAggrOneListDistinct(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonAggrOneListDistinctDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonAggrOneListDistinctQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonAggrOneListDistinctToString(sb, $field, row, $colIndex)"
  }

  val jsonAggrManyListDistinct: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonAggrManyListDistinct(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonAggrManyListDistinctDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonAggrManyListDistinctQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonAggrManyListDistinctToString(sb, $field, row, $colIndex)"
  }


  val jsonAggrOneListRand: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonAggrOneListRand(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonAggrOneListRandDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonAggrOneListRandQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonAggrOneListRandToString(sb, $field, row, $colIndex)"
  }

  val jsonAggrManyListRand: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonAggrManyListRand(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonAggrManyListRandDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonAggrManyListRandQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonAggrManyListRandToString(sb, $field, row, $colIndex)"
  }


  val jsonAggrSingleSample: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonAggrSingleSampleQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonAggrSingleSample(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonAggrSingleSample(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonAggrSingleSample(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonAggrSingleSampleToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonAggrSingleSampleDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonAggrSingleSampleQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonAggrSingleSampleQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonAggrSingleSampleToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonAggrSingleSampleToString(sb, $field, row, $colIndex)"
  }


  val jsonAggrOneSingle: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonOneDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
  }


  val jsonAggrManySingle: (String, String) => Int => Tree = (tpe: String, field: String) => tpe match {
    case "String"     => (colIndex: Int) => q"jsonAggrManySingleQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int) => q"jsonAggrManySingle(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int) => q"jsonAggrManySingle(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int) => q"jsonAggrManySingle(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int) => q"jsonAggrManySingleToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int) => q"jsonAggrManySingleDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int) => q"jsonAggrManySingleQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int) => q"jsonAggrManySingleQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int) => q"jsonAggrManySingleToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int) => q"jsonAggrManySingleToString(sb, $field, row, $colIndex)"
  }
}
