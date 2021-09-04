package molecule.datomic.base.marshalling.packers

import java.util.{List => jList}


trait ResolveFlat extends PackFlatTypes with PackFlatAggr {


  def packFlatAttr(group: String, baseTpe: String, colIndex: Int): jList[_] => Unit = group match {
    case "One"                  => packOneAttr(baseTpe, colIndex)
    case "OptOne"               => packOptOneAttr(baseTpe, colIndex)
    case "Many"                 => packManyAttr(baseTpe, colIndex)
    case "OptMany"              => packOptManyAttr(baseTpe, colIndex)
    case "Map"                  => packMapAttr(baseTpe, colIndex)
    case "OptMap"               => packOptMapAttr(baseTpe, colIndex)
    case "OptApplyOne"          => packOptApplyOneAtt(baseTpe, colIndex)
    case "OptApplyMany"         => packOptApplyManyAtt(baseTpe, colIndex)
    case "OptApplyMap"          => packOptApplyMapAttr(baseTpe, colIndex)
    case "KeyedMap"             => packKeyedMapAtt(baseTpe, colIndex)
    case "AggrOneList"          => packAggrOneList(baseTpe, colIndex)
    case "AggrManyList"         => packAggrManyList(baseTpe, colIndex)
    case "AggrOneListDistinct"  => packAggrOneListDistinct(baseTpe, colIndex)
    case "AggrManyListDistinct" => packAggrManyListDistinct(baseTpe, colIndex)
    case "AggrOneListRand"      => packAggrOneListRand(baseTpe, colIndex)
    case "AggrManyListRand"     => packAggrManyListRand(baseTpe, colIndex)
    case "AggrSingleSample"     => packAggrSingleSample(baseTpe, colIndex)
    case "AggrOneSingle"        => packAggrOneSingle(baseTpe, colIndex)
    case "AggrManySingle"       => packAggrManySingle(baseTpe, colIndex)
  }

  def packOneAttr(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packOneString(colIndex)
    case "Date"   => packOneDate(colIndex)
    case "Any"    => packOneAny(colIndex)
    case _        => packOne(colIndex)
  }

  def packOptOneAttr(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packOptOneString(colIndex)
    case "Date"   => packOptOneDate(colIndex)
    case "enum"   => packOptOneEnum(colIndex)
    case "ref"    => packOptOneRefAttr(colIndex)
    case _        => packOptOne(colIndex)
  }

  def packManyAttr(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packManyString(colIndex)
    case "Date"   => packManyDate(colIndex)
    case "enum"   => packManyEnum(colIndex)
    case _        => packMany(colIndex)
  }

  def packOptManyAttr(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packOptManyString(colIndex)
    case "Date"   => packOptManyDate(colIndex)
    case "enum"   => packOptManyEnum(colIndex)
    case "ref"    => packOptManyRefAttr(colIndex)
    case _        => packOptMany(colIndex)
  }

  def packMapAttr(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packMapString(colIndex)
    case _        => packMap(colIndex)
  }

  def packOptMapAttr(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packOptMapString(colIndex)
    case _        => packOptMap(colIndex)
  }

  def packOptApplyOneAtt(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packOptApplyOneString(colIndex)
    case "Date"   => packOptApplyOneDate(colIndex)
    case _        => packOptApplyOne(colIndex)
  }

  def packOptApplyManyAtt(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packOptApplyManyString(colIndex)
    case "Date"   => packOptApplyManyDate(colIndex)
    case _        => packOptApplyMany(colIndex)
  }

  def packOptApplyMapAttr(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packOptApplyMapString(colIndex)
    case "Date"   => packOptApplyMapDate(colIndex)
    case _        => packOptApplyMap(colIndex)
  }

  def packKeyedMapAtt(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packKeyedMapString(colIndex)
    case "Date"   => packKeyedMapDate(colIndex)
    case _        => packKeyedMap(colIndex)
  }

  def packAggrOneList(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrOneListString(colIndex)
    case "Date"   => packAggrOneListDate(colIndex)
    case _        => packAggrOneList(colIndex)
  }

  def packAggrManyList(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrManyListString(colIndex)
    case "Date"   => packAggrManyListDate(colIndex)
    case _        => packAggrManyList(colIndex)
  }

  def packAggrOneListDistinct(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrOneListDistinctString(colIndex)
    case "Date"   => packAggrOneListDistinctDate(colIndex)
    case _        => packAggrOneListDistinct(colIndex)
  }

  def packAggrManyListDistinct(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrManyListDistinctString(colIndex)
    case "Date"   => packAggrManyListDistinctDate(colIndex)
    case _        => packAggrManyListDistinct(colIndex)
  }

  def packAggrOneListRand(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrOneListRandString(colIndex)
    case "Date"   => packAggrOneListRandDate(colIndex)
    case _        => packAggrOneListRand(colIndex)
  }

  def packAggrManyListRand(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrManyListRandString(colIndex)
    case "Date"   => packAggrManyListRandDate(colIndex)
    case _        => packAggrManyListRand(colIndex)
  }

  def packAggrSingleSample(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrSingleSampleString(colIndex)
    case "Date"   => packAggrSingleSampleDate(colIndex)
    case _        => packAggrSingleSample(colIndex)
  }

  def packAggrOneSingle(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrOneSingleString(colIndex)
    case "Date"   => packAggrOneSingleDate(colIndex)
    case _        => packAggrOneSingle(colIndex)
  }

  def packAggrManySingle(baseTpe: String, colIndex: Int): jList[_] => Unit = baseTpe match {
    case "String" => packAggrManySingleString(colIndex)
    case "Date"   => packAggrManySingleDate(colIndex)
    case _        => packAggrManySingle(colIndex)
  }
}
