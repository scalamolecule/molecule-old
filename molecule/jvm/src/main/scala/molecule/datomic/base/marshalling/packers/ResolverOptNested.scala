package molecule.datomic.base.marshalling.packers

import java.util.{Iterator => jIterator}


trait ResolverOptNested extends PackOptNestedTypes with PackOptNestedAggr {

  def packOptNestedAttr(
    group: String,
    baseTpe: String,
    optAggrTpe: Option[String]
  ): (StringBuffer, jIterator[_]) => StringBuffer = {
    val tpe = optAggrTpe.getOrElse(baseTpe)
    group match {
      case "One"                  => packOneAttr(tpe)
      case "OptOne"               => packOptOneAttr(tpe)
      case "Many"                 => packManyAttr(tpe)
      case "OptMany"              => packOptManyAttr(tpe)
      case "Map"                  => packMapAttr(tpe)
      case "OptMap"               => packOptMapAttr(tpe)
      case "OptApplyOne"          => packOptApplyOneAtt(tpe)
      case "OptApplyMany"         => packOptApplyManyAtt(tpe)
      case "OptApplyMap"          => packOptApplyMapAttr(tpe)
      case "KeyedMap"             => packKeyedMapAtt(tpe)
      case "AggrOneList"          => packAggrOneList(tpe)
      case "AggrManyList"         => packAggrManyList(tpe)
      case "AggrOneListDistinct"  => packAggrOneListDistinct(tpe)
      case "AggrManyListDistinct" => packAggrManyListDistinct(tpe)
      case "AggrOneListRand"      => packAggrOneListRand(tpe)
      case "AggrManyListRand"     => packAggrManyListRand(tpe)
      case "AggrSingleSample"     => packAggrSingleSample(tpe)
      case "AggrOneSingle"        => packAggrOneSingle(tpe)
      case "AggrManySingle"       => packAggrManySingle(tpe)
    }
  }

  def packOneAttr(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedOneString
    case "Date"   => packOptNestedOneDate
    case "Any"    => packOptNestedOneAny
    case "enum"   => packOptNestedOneEnum
    case "ref"    => packOptNestedOneRefAttr
    case _        => packOptNestedOne_
  }

  def packOptOneAttr(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedOptOneString
    case "Date"   => packOptNestedOptOneDate
    case "enum"   => packOptNestedOptOneEnum
    case "ref"    => packOptNestedOptOneRefAttr
    case _        => packOptNestedOptOne_
  }

  def packManyAttr(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedManyString
    case "Date"   => packOptNestedManyDate
    case "enum"   => packOptNestedManyEnum
    case "ref"    => packOptNestedManyRefAttr
    case _        => packOptNestedMany_
  }

  def packOptManyAttr(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedOptManyString
    case "Date"   => packOptNestedOptManyDate
    case "enum"   => packOptNestedOptManyEnum
    case "ref"    => packOptNestedOptManyRefAttr
    case _        => packOptNestedOptMany_
  }

  def packMapAttr(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedMapString
    case _        => packOptNestedMap_
  }

  def packOptMapAttr(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedOptMapString
    case _        => packOptNestedOptMap_
  }

  def packOptApplyOneAtt(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedOptApplyOneString
    case "Date"   => packOptNestedOptApplyOneDate
    case _        => packOptNestedOptApplyOne_
  }

  def packOptApplyManyAtt(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedOptApplyManyString
    case "Date"   => packOptNestedOptApplyManyDate
    case _        => packOptNestedOptApplyMany_
  }

  def packOptApplyMapAttr(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedOptApplyMapString
    case "Date"   => packOptNestedOptApplyMapDate
    case _        => packOptNestedOptApplyMap_
  }

  def packKeyedMapAtt(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedKeyedMapString
    case "Date"   => packOptNestedKeyedMapDate
    case _        => packOptNestedKeyedMap_
  }


  // Aggregates

  def packAggrOneList(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "List[String]" => packOptNestedAggrOneListString
    case "List[Date]"   => packOptNestedAggrOneListDate
    case _              => packOptNestedAggrOneList_
  }

  def packAggrManyList(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "List[String]" => packOptNestedAggrManyListString
    case "List[Date]"   => packOptNestedAggrManyListDate
    case _              => packOptNestedAggrManyList_
  }

  def packAggrOneListDistinct(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "List[String]" => packOptNestedAggrOneListDistinctString
    case "List[Date]"   => packOptNestedAggrOneListDistinctDate
    case _              => packOptNestedAggrOneListDistinct_
  }

  def packAggrManyListDistinct(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "List[String]" => packOptNestedAggrManyListDistinctString
    case "List[Date]"   => packOptNestedAggrManyListDistinctDate
    case _              => packOptNestedAggrManyListDistinct_
  }

  def packAggrOneListRand(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "List[String]" => packOptNestedAggrOneListRandString
    case "List[Date]"   => packOptNestedAggrOneListRandDate
    case _              => packOptNestedAggrOneListRand_
  }

  def packAggrManyListRand(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "List[String]" => packOptNestedAggrManyListRandString
    case "List[Date]"   => packOptNestedAggrManyListRandDate
    case _              => packOptNestedAggrManyListRand_
  }

  def packAggrSingleSample(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedAggrSingleSampleString
    case "Date"   => packOptNestedAggrSingleSampleDate
    case _        => packOptNestedAggrSingleSample_
  }

  def packAggrOneSingle(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "String" => packOptNestedAggrOneSingleString
    case "Date"   => packOptNestedAggrOneSingleDate
    case _        => packOptNestedAggrOneSingle_
  }

  def packAggrManySingle(tpe: String): (StringBuffer, jIterator[_]) => StringBuffer = tpe match {
    case "Set[String]" => packOptNestedAggrManySingleString
    case "Set[Date]"   => packOptNestedAggrManySingleDate
    case _             => packOptNestedAggrManySingle_
  }
}
