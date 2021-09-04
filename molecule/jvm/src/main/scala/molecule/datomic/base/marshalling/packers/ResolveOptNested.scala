package molecule.datomic.base.marshalling.packers

import java.util.{Iterator => jIterator}


trait ResolveOptNested extends PackOptNestedTypes with PackOptNestedAggr {

  def packOptNestedAttr(group: String, baseTpe: String): jIterator[_] => Unit = group match {
    case "One"                  => packOneAttr(baseTpe)
    case "OptOne"               => packOptOneAttr(baseTpe)
    case "Many"                 => packManyAttr(baseTpe)
    case "OptMany"              => packOptManyAttr(baseTpe)
    case "Map"                  => packMapAttr(baseTpe)
    case "OptMap"               => packOptMapAttr(baseTpe)
    case "OptApplyOne"          => packOptApplyOneAtt(baseTpe)
    case "OptApplyMany"         => packOptApplyManyAtt(baseTpe)
    case "OptApplyMap"          => packOptApplyMapAttr(baseTpe)
    case "KeyedMap"             => packKeyedMapAtt(baseTpe)
    case "AggrOneList"          => packAggrOneList(baseTpe)
    case "AggrManyList"         => packAggrManyList(baseTpe)
    case "AggrOneListDistinct"  => packAggrOneListDistinct(baseTpe)
    case "AggrManyListDistinct" => packAggrManyListDistinct(baseTpe)
    case "AggrOneListRand"      => packAggrOneListRand(baseTpe)
    case "AggrManyListRand"     => packAggrManyListRand(baseTpe)
    case "AggrSingleSample"     => packAggrSingleSample(baseTpe)
    case "AggrOneSingle"        => packAggrOneSingle(baseTpe)
    case "AggrManySingle"       => packAggrManySingle(baseTpe)
  }

  def packOneAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOneString
    case "Date"   => packOptNestedOneDate
    case "Any"    => packOptNestedOneAny
    case _        => packOptNestedOne_
  }

  def packOptOneAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptOneString
    case "Date"   => packOptNestedOptOneDate
    case "enum"   => packOptNestedOptOneEnum
    case "ref"    => packOptNestedOptOneRefAttr
    case _        => packOptNestedOptOne_
  }

  def packManyAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedManyString
    case "Date"   => packOptNestedManyDate
//    case "enum"   => packOptNestedManyEnum
    case _        => packOptNestedMany_
  }

  def packOptManyAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptManyString
    case "Date"   => packOptNestedOptManyDate
//    case "enum"   => packOptNestedOptManyEnum
    case "ref"    => packOptNestedOptManyRefAttr
    case _        => packOptNestedOptMany_
  }

  def packMapAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedMapString
    case _        => packOptNestedMap_
  }

  def packOptMapAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptMapString
    case _        => packOptMap_
  }

  def packOptApplyOneAtt(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptApplyOneString
    case "Date"   => packOptNestedOptApplyOneDate
    case _        => packOptNestedOptApplyOne_
  }

  def packOptApplyManyAtt(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptApplyManyString
    case "Date"   => packOptNestedOptApplyManyDate
    case _        => packOptNestedOptApplyMany_
  }

  def packOptApplyMapAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptApplyMapString
    case "Date"   => packOptNestedOptApplyMapDate
    case _        => packOptNestedOptApplyMap_
  }

  def packKeyedMapAtt(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedKeyedMapString
    case "Date"   => packOptNestedKeyedMapDate
    case _        => packOptNestedKeyedMap_
  }

  def packAggrOneList(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrOneListString
    case "Date"   => packOptNestedAggrOneListDate
    case _        => packOptNestedAggrOneList_
  }

  def packAggrManyList(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrManyListString
    case "Date"   => packOptNestedAggrManyListDate
    case _        => packOptNestedAggrManyList_
  }

  def packAggrOneListDistinct(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrOneListDistinctString
    case "Date"   => packOptNestedAggrOneListDistinctDate
    case _        => packOptNestedAggrOneListDistinct_
  }

  def packAggrManyListDistinct(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrManyListDistinctString
    case "Date"   => packOptNestedAggrManyListDistinctDate
    case _        => packOptNestedAggrManyListDistinct_
  }

  def packAggrOneListRand(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrOneListRandString
    case "Date"   => packOptNestedAggrOneListRandDate
    case _        => packOptNestedAggrOneListRand_
  }

  def packAggrManyListRand(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrManyListRandString
    case "Date"   => packOptNestedAggrManyListRandDate
    case _        => packOptNestedAggrManyListRand_
  }

  def packAggrSingleSample(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrSingleSampleString
    case "Date"   => packOptNestedAggrSingleSampleDate
    case _        => packOptNestedAggrSingleSample_
  }

  def packAggrOneSingle(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrOneSingleString
    case "Date"   => packOptNestedAggrOneSingleDate
    case _        => packOptNestedAggrOneSingle_
  }

  def packAggrManySingle(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrManySingleString
    case "Date"   => packOptNestedAggrManySingleDate
    case _        => packOptNestedAggrManySingle_
  }
}
