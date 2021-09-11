package molecule.datomic.base.marshalling.packers

import java.util.{List => jList}


trait ResolverFlat extends PackFlatTypes with PackFlatAggr {

  def packFlatAttr(sb: StringBuffer, group: String, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = group match {
    case "One"                  => packOneAttr(sb, baseTpe, colIndex)
    case "OptOne"               => packOptOneAttr(sb, baseTpe, colIndex)
    case "Many"                 => packManyAttr(sb, baseTpe, colIndex)
    case "OptMany"              => packOptManyAttr(sb, baseTpe, colIndex)
    case "Map"                  => packMapAttr(sb, baseTpe, colIndex)
    case "OptMap"               => packOptMapAttr(sb, baseTpe, colIndex)
    case "OptApplyOne"          => packOptApplyOneAtt(sb, baseTpe, colIndex)
    case "OptApplyMany"         => packOptApplyManyAtt(sb, baseTpe, colIndex)
    case "OptApplyMap"          => packOptApplyMapAttr(sb, baseTpe, colIndex)
    case "KeyedMap"             => packKeyedMapAtt(sb, baseTpe, colIndex)
    case "AggrOneList"          => packAggrOneList(sb, baseTpe, colIndex)
    case "AggrManyList"         => packAggrManyList(sb, baseTpe, colIndex)
    case "AggrOneListDistinct"  => packAggrOneListDistinct(sb, baseTpe, colIndex)
    case "AggrManyListDistinct" => packAggrManyListDistinct(sb, baseTpe, colIndex)
    case "AggrOneListRand"      => packAggrOneListRand(sb, baseTpe, colIndex)
    case "AggrManyListRand"     => packAggrManyListRand(sb, baseTpe, colIndex)
    case "AggrSingleSample"     => packAggrSingleSample(sb, baseTpe, colIndex)
    case "AggrOneSingle"        => packAggrOneSingle(sb, baseTpe, colIndex)
    case "AggrManySingle"       => packAggrManySingle(sb, baseTpe, colIndex)
  }

  def packOneAttr(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packOneString(sb, colIndex)
    case "Date"   => packOneDate(sb, colIndex)
    case "Any"    => packOneAny(sb, colIndex)
    case "ref"    => packOneRefAttr(sb, colIndex)
    case _        => packOne(sb, colIndex)
  }

  def packOptOneAttr(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packOptOneString(sb, colIndex)
    case "Date"   => packOptOneDate(sb, colIndex)
    case "enum"   => packOptOneEnum(sb, colIndex)
    case "ref"    => packOptOneRefAttr(sb, colIndex)
    case _        => packOptOne(sb, colIndex)
  }

  def packManyAttr(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packManyString(sb, colIndex)
    case "Date"   => packManyDate(sb, colIndex)
    case "enum"   => packManyEnum(sb, colIndex)
    case "ref"    => packManyRefAttr(sb, colIndex)
    case _        => packMany(sb, colIndex)
  }

  def packOptManyAttr(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packOptManyString(sb, colIndex)
    case "Date"   => packOptManyDate(sb, colIndex)
    case "enum"   => packOptManyEnum(sb, colIndex)
    case "ref"    => packOptManyRefAttr(sb, colIndex)
    case _        => packOptMany(sb, colIndex)
  }

  def packMapAttr(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packMapString(sb, colIndex)
    case _        => packMap(sb, colIndex)
  }

  def packOptMapAttr(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packOptMapString(sb, colIndex)
    case _        => packOptMap(sb, colIndex)
  }

  def packOptApplyOneAtt(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packOptApplyOneString(sb, colIndex)
    case "Date"   => packOptApplyOneDate(sb, colIndex)
    case _        => packOptApplyOne(sb, colIndex)
  }

  def packOptApplyManyAtt(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packOptApplyManyString(sb, colIndex)
    case "Date"   => packOptApplyManyDate(sb, colIndex)
    case _        => packOptApplyMany(sb, colIndex)
  }

  def packOptApplyMapAttr(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packOptApplyMapString(sb, colIndex)
    case "Date"   => packOptApplyMapDate(sb, colIndex)
    case _        => packOptApplyMap(sb, colIndex)
  }

  def packKeyedMapAtt(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packKeyedMapString(sb, colIndex)
    case "Date"   => packKeyedMapDate(sb, colIndex)
    case _        => packKeyedMap(sb, colIndex)
  }

  def packAggrOneList(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrOneListString(sb, colIndex)
    case "Date"   => packAggrOneListDate(sb, colIndex)
    case _        => packAggrOneList(sb, colIndex)
  }

  def packAggrManyList(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrManyListString(sb, colIndex)
    case "Date"   => packAggrManyListDate(sb, colIndex)
    case _        => packAggrManyList(sb, colIndex)
  }

  def packAggrOneListDistinct(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrOneListDistinctString(sb, colIndex)
    case "Date"   => packAggrOneListDistinctDate(sb, colIndex)
    case _        => packAggrOneListDistinct(sb, colIndex)
  }

  def packAggrManyListDistinct(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrManyListDistinctString(sb, colIndex)
    case "Date"   => packAggrManyListDistinctDate(sb, colIndex)
    case _        => packAggrManyListDistinct(sb, colIndex)
  }

  def packAggrOneListRand(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrOneListRandString(sb, colIndex)
    case "Date"   => packAggrOneListRandDate(sb, colIndex)
    case _        => packAggrOneListRand(sb, colIndex)
  }

  def packAggrManyListRand(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrManyListRandString(sb, colIndex)
    case "Date"   => packAggrManyListRandDate(sb, colIndex)
    case _        => packAggrManyListRand(sb, colIndex)
  }

  def packAggrSingleSample(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrSingleSampleString(sb, colIndex)
    case "Date"   => packAggrSingleSampleDate(sb, colIndex)
    case _        => packAggrSingleSample(sb, colIndex)
  }

  def packAggrOneSingle(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrOneSingleString(sb, colIndex)
    case "Date"   => packAggrOneSingleDate(sb, colIndex)
    case _        => packAggrOneSingle(sb, colIndex)
  }

  def packAggrManySingle(sb: StringBuffer, baseTpe: String, colIndex: Int): jList[_] => StringBuffer = baseTpe match {
    case "String" => packAggrManySingleString(sb, colIndex)
    case "Date"   => packAggrManySingleDate(sb, colIndex)
    case _        => packAggrManySingle(sb, colIndex)
  }
}
