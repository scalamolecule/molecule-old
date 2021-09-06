package molecule.core.macros.rowAttr

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait ResolverJsonTypes extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  def getResolverJsonTypes(group: String, baseTpe: String, field: String): (Int, Int) => Tree = group match {
    case "One"                  => jsonOneAttr(baseTpe, field)
    case "OptOne"               => jsonOptOneAttr(baseTpe, field)
    case "Many"                 => jsonManyAttr(baseTpe, field)
    case "OptMany"              => jsonOptManyAttr(baseTpe, field)
    case "Map"                  => jsonMapAttr_(baseTpe, field)
    case "OptMap"               => jsonOptMapAttr_(baseTpe, field)
    case "OptApplyOne"          => jsonOptApplyOneAttr(baseTpe, field)
    case "OptApplyMany"         => jsonOptApplyManyAttr(baseTpe, field)
    case "OptApplyMap"          => jsonOptApplyMapAttr_(baseTpe, field)
    case "KeyedMap"             => jsonKeyedMapAttr(baseTpe, field)
    case "AggrOneList"          => jsonAggrOneList(baseTpe, field)
    case "AggrManyList"         => jsonAggrManyList(baseTpe, field)
    case "AggrOneListDistinct"  => jsonAggrOneListDistinct(baseTpe, field)
    case "AggrManyListDistinct" => jsonAggrManyListDistinct(baseTpe, field)
    case "AggrOneListRand"      => jsonAggrOneListRand(baseTpe, field)
    case "AggrManyListRand"     => jsonAggrManyListRand(baseTpe, field)
    case "AggrSingleSample"     => jsonAggrSingleSample(baseTpe, field)
    case "AggrOneSingle"        => jsonAggrOneSingle(baseTpe, field)
    case "AggrManySingle"       => jsonAggrManySingle(baseTpe, field)
  }


  lazy val jsonAttr: richTree => (Int, Int) => Tree = (t: richTree) =>
    if (t.card == 1) jsonOneAttr(t.tpeS, t.name) else jsonManyAttr(t.tpeS, t.name)

  lazy val jsonOneAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
    case "String"     => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int, _: Int) => q"jsonOne(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int, _: Int) => q"jsonOneDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
    case "Any"        => (colIndex: Int, _: Int) => q"jsonOneAny(sb, $field, row, $colIndex)"
    case "enum"       => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    case "ref"        => (colIndex: Int, _: Int) => q"jsonOne(sb, $field, row, $colIndex)"
  }

  lazy val jsonManyAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonMany(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonManyDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
    case "enum"       => (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "ref"        => (colIndex: Int, tabs: Int) => q"jsonManyToString(sb, $field, row, $colIndex, $tabs)"
  }

  lazy val jsonEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1) {
      (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
    } else {
      (colIndex: Int, tabs: Int) => q"jsonManyQuoted(sb, $field, row, $colIndex, $tabs)"
    }
  }


  lazy val jsonOptAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1) {
      jsonOptOneAttr(t.tpeS, field)
    } else {
      jsonOptManyAttr(t.tpeS, field)
    }
  }

  lazy val jsonOptOneAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
    case "String"     => (colIndex: Int, _: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
    case "Int"        => (colIndex: Int, _: Int) => q"jsonOptOne(sb, $field, row, $colIndex)"
    case "Long"       => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
    case "Double"     => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
    case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
    case "Date"       => (colIndex: Int, _: Int) => q"jsonOptOneDate(sb, $field, row, $colIndex)"
    case "UUID"       => (colIndex: Int, _: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
    case "URI"        => (colIndex: Int, _: Int) => q"jsonOptOneQuoted(sb, $field, row, $colIndex)"
    case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
    case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOptOneToString(sb, $field, row, $colIndex)"
    case "enum"       => (colIndex: Int, _: Int) => q"jsonOptOneEnum(sb, $field, row, $colIndex)"
    case "ref"        => (colIndex: Int, _: Int) => q"jsonOptOneRefAttr(sb, $field, row, $colIndex)"
  }

  lazy val jsonOptManyAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
    case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptMany(sb, $field, row, $colIndex, $tabs)"
    case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
    case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptManyDate(sb, $field, row, $colIndex, $tabs)"
    case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptManyQuoted(sb, $field, row, $colIndex, $tabs)"
    case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
    case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptManyToString(sb, $field, row, $colIndex, $tabs)"
    case "enum"       => (colIndex: Int, tabs: Int) => q"jsonOptManyEnum(sb, $field, row, $colIndex, $tabs)"
    case "ref"        => (colIndex: Int, tabs: Int) => q"jsonOptManyRefAttr(sb, $field, row, $colIndex, $tabs)"
  }

  lazy val jsonOptEnum: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1) {
      (colIndex: Int, _: Int) => q"jsonOptOneEnum(sb, $field, row, $colIndex)"
    } else {
      (colIndex: Int, tabs: Int) => q"jsonOptManyEnum(sb, $field, row, $colIndex, $tabs)"
    }
  }

  lazy val jsonOptRefAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1) {
      (colIndex: Int, _: Int) => q"jsonOptOneRefAttr(sb, $field, row, $colIndex)"
    } else {
      (colIndex: Int, tabs: Int) => q"jsonOptManyRefAttr(sb, $field, row, $colIndex, $tabs)"
    }
  }


  lazy val jsonMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => jsonMapAttr_(t.tpeS, t.name)

  lazy val jsonMapAttr_ : (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonMap(sb, $field, row, $colIndex, $tabs)"
    }
  }

  lazy val jsonOptMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => jsonOptMapAttr_(t.tpeS, t.name)

  lazy val jsonOptMapAttr_ : (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptMap(sb, $field, row, $colIndex, $tabs)"
    }
  }

  lazy val jsonOptApplyAttr: richTree => (Int, Int) => Tree = (t: richTree) => {
    val field = t.name
    if (t.card == 1) {
      jsonOptApplyOneAttr(t.tpeS, field)
    } else {
      jsonOptApplyManyAttr(t.tpeS, field)
    }
  }

  lazy val jsonOptApplyOneAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
      case "String"     => (colIndex: Int, _: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int, _: Int) => q"jsonOptApplyOne(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int, _: Int) => q"jsonOptApplyOneDate(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int, _: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int, _: Int) => q"jsonOptApplyOneQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
      case "ref"        => (colIndex: Int, _: Int) => q"jsonOptApplyOneToString(sb, $field, row, $colIndex)"
    }
  }

  lazy val jsonOptApplyManyAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyMany(sb, $field, row, $colIndex, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyDate(sb, $field, row, $colIndex, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyQuoted(sb, $field, row, $colIndex, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
      case "ref"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyManyToString(sb, $field, row, $colIndex, $tabs)"
    }
  }

  lazy val jsonOptApplyMapAttr: richTree => (Int, Int) => Tree = (t: richTree) => jsonOptApplyMapAttr_(t.tpeS, t.name)

  lazy val jsonOptApplyMapAttr_ : (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
      case "String"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "Int"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "Long"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "Double"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "Boolean"    => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "Date"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "UUID"       => (colIndex: Int, tabs: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "URI"        => (colIndex: Int, tabs: Int) => q"jsonOptApplyMapQuoted(sb, $field, row, $colIndex, $tabs)"
      case "BigInt"     => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
      case "BigDecimal" => (colIndex: Int, tabs: Int) => q"jsonOptApplyMap(sb, $field, row, $colIndex, $tabs)"
    }
  }

  lazy val jsonKeyedMapAttr: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => {
    baseTpe match {
      case "String"     => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "Int"        => (colIndex: Int, _: Int) => q"jsonOne(sb, $field, row, $colIndex)"
      case "Long"       => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Double"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Boolean"    => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Date"       => (colIndex: Int, _: Int) => q"jsonOneDate(sb, $field, row, $colIndex)"
      case "UUID"       => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "URI"        => (colIndex: Int, _: Int) => q"jsonOneQuoted(sb, $field, row, $colIndex)"
      case "BigInt"     => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "BigDecimal" => (colIndex: Int, _: Int) => q"jsonOneToString(sb, $field, row, $colIndex)"
      case "Any"        => (colIndex: Int, _: Int) => q"jsonOneAny(sb, $field, row, $colIndex)"
    }
  }


  // Aggregates.........................................


  lazy val jsonAggrOneList: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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

  lazy val jsonAggrManyList: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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

  lazy val jsonAggrOneListDistinct: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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

  lazy val jsonAggrManyListDistinct: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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

  lazy val jsonAggrOneListRand: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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

  lazy val jsonAggrManyListRand: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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

  lazy val jsonAggrSingleSample: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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

  lazy val jsonAggrOneSingle: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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

  lazy val jsonAggrManySingle: (String, String) => (Int, Int) => Tree = (baseTpe: String, field: String) => baseTpe match {
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
