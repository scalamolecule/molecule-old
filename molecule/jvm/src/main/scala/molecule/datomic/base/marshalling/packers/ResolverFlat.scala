package molecule.datomic.base.marshalling.packers

import java.util.{List => jList}


trait ResolverFlat extends PackFlatTypes with PackFlatAggr {

  def packFlatAttr(
    sb: StringBuffer,
    group: String,
    baseTpe: String,
    colIndex: Int,
    optAggrTpe: Option[String]
  ): jList[_] => StringBuffer = {
    val tpe = optAggrTpe.getOrElse(baseTpe)
    group match {
      case "One"                  => packOneAttr(sb, tpe, colIndex)
      case "OptOne"               => packOptOneAttr(sb, tpe, colIndex)
      case "Many"                 => packManyAttr(sb, tpe, colIndex)
      case "OptMany"              => packOptManyAttr(sb, tpe, colIndex)
      case "Map"                  => packMapAttr(sb, tpe, colIndex)
      case "OptMap"               => packOptMapAttr(sb, tpe, colIndex)
      case "OptApplyOne"          => packOptApplyOneAtt(sb, tpe, colIndex)
      case "OptApplyMany"         => packOptApplyManyAtt(sb, tpe, colIndex)
      case "OptApplyMap"          => packOptApplyMapAttr(sb, tpe, colIndex)
      case "KeyedMap"             => packKeyedMapAtt(sb, colIndex)
      case "AggrOneList"          => packAggrOneList(sb, tpe, colIndex)
      case "AggrManyList"         => packAggrManyList(sb, tpe, colIndex)
      case "AggrOneListDistinct"  => packAggrOneListDistinct(sb, tpe, colIndex)
      case "AggrManyListDistinct" => packAggrManyListDistinct(sb, tpe, colIndex)
      case "AggrOneListRand"      => packAggrOneListRand(sb, tpe, colIndex)
      case "AggrManyListRand"     => packAggrManyListRand(sb, tpe, colIndex)
      case "AggrSingleSample"     => packAggrSingleSample(sb, tpe, colIndex)
      case "AggrOneSingle"        => packAggrOneSingle(sb, tpe, colIndex)
      case "AggrManySingle"       => packAggrManySingle(sb, tpe, colIndex)
    }
  }

  def packOneAttr(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packOneString(sb, colIndex)
    case "Date"   => packOneDate(sb, colIndex)
    case "Any"    => packOneAny(sb, colIndex)
    case "ref"    => packOneRefAttr(sb, colIndex)
    case _        => packOne(sb, colIndex)
  }

  def packOptOneAttr(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packOptOneString(sb, colIndex)
    case "Date"   => packOptOneDate(sb, colIndex)
    case "enum"   => packOptOneEnum(sb, colIndex)
    case "ref"    => packOptOneRefAttr(sb, colIndex)
    case _        => packOptOne(sb, colIndex)
  }

  def packManyAttr(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packManyString(sb, colIndex)
    case "Date"   => packManyDate(sb, colIndex)
    case "enum"   => packManyEnum(sb, colIndex)
    case "ref"    => packManyRefAttr(sb, colIndex)
    case _        => packMany(sb, colIndex)
  }

  def packOptManyAttr(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packOptManyString(sb, colIndex)
    case "Date"   => packOptManyDate(sb, colIndex)
    case "enum"   => packOptManyEnum(sb, colIndex)
    case "ref"    => packOptManyRefAttr(sb, colIndex)
    case _        => packOptMany(sb, colIndex)
  }

  def packMapAttr(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packMapString(sb, colIndex)
    case _        => packMap(sb, colIndex)
  }

  def packOptMapAttr(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packOptMapString(sb, colIndex)
    case _        => packOptMap(sb, colIndex)
  }

  def packOptApplyOneAtt(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packOptApplyOneString(sb, colIndex)
    case "Date"   => packOptApplyOneDate(sb, colIndex)
    case _        => packOptApplyOne(sb, colIndex)
  }

  def packOptApplyManyAtt(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packOptApplyManyString(sb, colIndex)
    case "Date"   => packOptApplyManyDate(sb, colIndex)
    case _        => packOptApplyMany(sb, colIndex)
  }

  def packOptApplyMapAttr(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packOptApplyMapString(sb, colIndex)
    case "Date"   => packOptApplyMapDate(sb, colIndex)
    case _        => packOptApplyMap(sb, colIndex)
  }

  def packKeyedMapAtt(sb: StringBuffer, colIndex: Int): jList[_] => StringBuffer =
    packKeyedMapString(sb, colIndex)

  def packAggrOneList(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String"       => packAggrOneListString(sb, colIndex)
    case "Date"         => packAggrOneListDate(sb, colIndex)
    case "List[String]" => packAggrOneListString(sb, colIndex)
    case "List[Date]"   => packAggrOneListDate(sb, colIndex)
    case _              => packAggrOneList(sb, colIndex)
  }

  def packAggrManyList(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packAggrManyListString(sb, colIndex)
    case "Date"   => packAggrManyListDate(sb, colIndex)
    case _        => packAggrManyList(sb, colIndex)
  }

  def packAggrOneListDistinct(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String"       => packAggrOneListDistinctString(sb, colIndex)
    case "Date"         => packAggrOneListDistinctDate(sb, colIndex)
    case "List[String]" => packAggrOneListDistinctString(sb, colIndex)
    case "List[Date]"   => packAggrOneListDistinctDate(sb, colIndex)
    case _              => packAggrOneListDistinct(sb, colIndex)
  }

  def packAggrManyListDistinct(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packAggrManyListDistinctString(sb, colIndex)
    case "Date"   => packAggrManyListDistinctDate(sb, colIndex)
    case _        => packAggrManyListDistinct(sb, colIndex)
  }

  def packAggrOneListRand(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String"       => packAggrOneListRandString(sb, colIndex)
    case "Date"         => packAggrOneListRandDate(sb, colIndex)
    case "List[String]" => packAggrOneListRandString(sb, colIndex)
    case "List[Date]"   => packAggrOneListRandDate(sb, colIndex)
    case _              => packAggrOneListRand(sb, colIndex)
  }

  def packAggrManyListRand(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packAggrManyListRandString(sb, colIndex)
    case "Date"   => packAggrManyListRandDate(sb, colIndex)
    case _        => packAggrManyListRand(sb, colIndex)
  }

  def packAggrSingleSample(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String"       => packAggrSingleSampleString(sb, colIndex)
    case "Date"         => packAggrSingleSampleDate(sb, colIndex)
    case "List[String]" => packAggrSingleSampleString(sb, colIndex)
    case "List[Date]"   => packAggrSingleSampleDate(sb, colIndex)
    case _              => packAggrSingleSample(sb, colIndex)
  }

  def packAggrOneSingle(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packAggrOneSingleString(sb, colIndex)
    case "Date"   => packAggrOneSingleDate(sb, colIndex)
    case _        => packAggrOneSingle(sb, colIndex)
  }

  def packAggrManySingle(sb: StringBuffer, tpe: String, colIndex: Int): jList[_] => StringBuffer = tpe match {
    case "String" => packAggrManySingleString(sb, colIndex)
    case "Date"   => packAggrManySingleDate(sb, colIndex)
    case _        => packAggrManySingle(sb, colIndex)
  }
}
