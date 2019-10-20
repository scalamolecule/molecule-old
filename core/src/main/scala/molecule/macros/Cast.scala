package molecule.macros
import molecule.ops.TreeOps
import scala.language.higherKinds
import scala.reflect.macros.blackbox


private[molecule] trait Cast extends TreeOps {
  val c: blackbox.Context
  import c.universe._

  val y = DebugMacro("Cast", 1)


  def castOneAttr(tpe: String): Int => Tree = tpe match {
    case "String"         => (i: Int) => q"castOne[String](row, $i)"
    case "Int"            => (i: Int) => q"castOneInt(row, $i)"
    case "Int2"           => (i: Int) => q"castOneInt2(row, $i)"
    case "Float"          => (i: Int) => q"castOneFloat(row, $i)"
    case "Boolean"        => (i: Int) => q"castOne[Boolean](row, $i)"
    case "Long"           => (i: Int) => q"castOne[Long](row, $i)"
    case "Double"         => (i: Int) => q"castOne[Double](row, $i)"
    case "java.util.Date" => (i: Int) => q"castOne[java.util.Date](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castOne[java.util.UUID](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castOne[java.net.URI](row, $i)"
    case "BigInt"         => (i: Int) => q"castOneBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castOneBigDecimal(row, $i)"
    case "Any"            => (i: Int) => q"row.get($i)"
  }

  def castManyAttr(tpe: String): Int => Tree = tpe match {
    case "String"         => (i: Int) => q"castMany[String](row, $i)"
    case "Int"            => (i: Int) => q"castManyInt(row, $i)"
    case "Float"          => (i: Int) => q"castManyFloat(row, $i)"
    case "Boolean"        => (i: Int) => q"castMany[Boolean](row, $i)"
    case "Long"           => (i: Int) => q"castMany[Long](row, $i)"
    case "Double"         => (i: Int) => q"castMany[Double](row, $i)"
    case "java.util.Date" => (i: Int) => q"castMany[java.util.Date](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castMany[java.util.UUID](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castMany[java.net.URI](row, $i)"
    case "BigInt"         => (i: Int) => q"castManyBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castManyBigDecimal(row, $i)"
  }

  val castMandatoryAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOneAttr(t.tpeS) else castManyAttr(t.tpeS)


  // Aggregates ===================================================================

  def castAggrInt: Int => Tree = (i: Int) => q"row.get($i).asInstanceOf[Int]"
  def castAggrDouble: Int => Tree = (i: Int) => q"row.get($i).asInstanceOf[Double]"

  def castAggrOneList(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castAggrOneListInt(row, $i)"
    case "Long"           => (i: Int) => q"castAggrOneList[Long](row, $i)"
    case "Float"          => (i: Int) => q"castAggrOneListFloat(row, $i)"
    case "Double"         => (i: Int) => q"castAggrOneList[Double](row, $i)"
    case "String"         => (i: Int) => q"castAggrOneList[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castAggrOneListBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castAggrOneListBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castAggrOneList[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castAggrOneList[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castAggrOneList[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castAggrOneList[java.util.UUID](row, $i)"
  }
  def castAggrManyList(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castAggrManyListInt(row, $i)"
    case "Long"           => (i: Int) => q"castAggrManyList[Long](row, $i)"
    case "Float"          => (i: Int) => q"castAggrManyListFloat(row, $i)"
    case "Double"         => (i: Int) => q"castAggrManyList[Double](row, $i)"
    case "String"         => (i: Int) => q"castAggrManyList[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castAggrManyListBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castAggrManyListBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castAggrManyList[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castAggrManyList[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castAggrManyList[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castAggrManyList[java.util.UUID](row, $i)"
  }

  def castAggrOneListDistinct(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castAggrOneListDistinctInt(row, $i)"
    case "Long"           => (i: Int) => q"castAggrOneListDistinct[Long](row, $i)"
    case "Float"          => (i: Int) => q"castAggrOneListDistinctFloat(row, $i)"
    case "Double"         => (i: Int) => q"castAggrOneListDistinct[Double](row, $i)"
    case "String"         => (i: Int) => q"castAggrOneListDistinct[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castAggrOneListDistinctBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castAggrOneListDistinctBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castAggrOneListDistinct[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castAggrOneListDistinct[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castAggrOneListDistinct[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castAggrOneListDistinct[java.util.UUID](row, $i)"
  }
  def castAggrManyListDistinct(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castAggrManyListDistinctInt(row, $i)"
    case "Long"           => (i: Int) => q"castAggrManyListDistinct[Long](row, $i)"
    case "Float"          => (i: Int) => q"castAggrManyListDistinctFloat(row, $i)"
    case "Double"         => (i: Int) => q"castAggrManyListDistinct[Double](row, $i)"
    case "String"         => (i: Int) => q"castAggrManyListDistinct[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castAggrManyListDistinctBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castAggrManyListDistinctBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castAggrManyListDistinct[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castAggrManyListDistinct[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castAggrManyListDistinct[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castAggrManyListDistinct[java.util.UUID](row, $i)"
  }

  def castAggrOneListRand(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castAggrOneListRandInt(row, $i)"
    case "Long"           => (i: Int) => q"castAggrOneListRand[Long](row, $i)"
    case "Float"          => (i: Int) => q"castAggrOneListRandFloat(row, $i)"
    case "Double"         => (i: Int) => q"castAggrOneListRand[Double](row, $i)"
    case "String"         => (i: Int) => q"castAggrOneListRand[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castAggrOneListRandBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castAggrOneListRandBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castAggrOneListRand[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castAggrOneListRand[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castAggrOneListRand[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castAggrOneListRand[java.util.UUID](row, $i)"
  }
  def castAggrManyListRand(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castAggrManyListRandInt(row, $i)"
    case "Long"           => (i: Int) => q"castAggrManyListRand[Long](row, $i)"
    case "Float"          => (i: Int) => q"castAggrManyListRandFloat(row, $i)"
    case "Double"         => (i: Int) => q"castAggrManyListRand[Double](row, $i)"
    case "String"         => (i: Int) => q"castAggrManyListRand[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castAggrManyListRandBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castAggrManyListRandBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castAggrManyListRand[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castAggrManyListRand[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castAggrManyListRand[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castAggrManyListRand[java.util.UUID](row, $i)"
  }


  def castAggrSingleSample(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castAggrSingleSampleInt(row, $i)"
    case "Long"           => (i: Int) => q"castAggrSingleSample[Long](row, $i)"
    case "Float"          => (i: Int) => q"castAggrSingleSampleFloat(row, $i)"
    case "Double"         => (i: Int) => q"castAggrSingleSample[Double](row, $i)"
    case "String"         => (i: Int) => q"castAggrSingleSample[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castAggrSingleSampleBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castAggrSingleSampleBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castAggrSingleSample[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castAggrSingleSample[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castAggrSingleSample[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castAggrSingleSample[java.util.UUID](row, $i)"
  }

  def castAggrOneSingle(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castOneInt(row, $i)"
    case "Long"           => (i: Int) => q"castOne[Long](row, $i)"
    case "Float"          => (i: Int) => q"castOneFloat(row, $i)"
    case "Double"         => (i: Int) => q"castOne[Double](row, $i)"
    case "String"         => (i: Int) => q"castOne[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castOneBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castOneBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castOne[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castOne[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castOne[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castOne[java.util.UUID](row, $i)"
  }
  def castAggrManySingle(tpe: String): Int => Tree = tpe match {
    case "Int"            => (i: Int) => q"castAggrManySingleInt(row, $i)"
    case "Long"           => (i: Int) => q"castAggrManySingle[Long](row, $i)"
    case "Float"          => (i: Int) => q"castAggrManySingleFloat(row, $i)"
    case "Double"         => (i: Int) => q"castAggrManySingle[Double](row, $i)"
    case "String"         => (i: Int) => q"castAggrManySingle[String](row, $i)"
    case "BigInt"         => (i: Int) => q"castAggrManySingleBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castAggrManySingleBigDecimal(row, $i)"
    case "java.util.Date" => (i: Int) => q"castAggrManySingle[java.util.Date](row, $i)"
    case "Boolean"        => (i: Int) => q"castAggrManySingle[Boolean](row, $i)"
    case "java.net.URI"   => (i: Int) => q"castAggrManySingle[java.net.URI](row, $i)"
    case "java.util.UUID" => (i: Int) => q"castAggrManySingle[java.util.UUID](row, $i)"
  }


  val castOptionalAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"         => (i: Int) => q"castOptOne[String](row, $i)"
        case "Int"            => (i: Int) => q"castOptOneInt(row, $i)"
        case "Float"          => (i: Int) => q"castOptOneFloat(row, $i)"
        case "Boolean"        => (i: Int) => q"castOptOne[Boolean](row, $i)"
        case "Long"           => (i: Int) => q"castOptOneLong(row, $i)"
        case "Double"         => (i: Int) => q"castOptOneDouble(row, $i)"
        case "java.util.Date" => (i: Int) => q"castOptOne[java.util.Date](row, $i)"
        case "java.util.UUID" => (i: Int) => q"castOptOne[java.util.UUID](row, $i)"
        case "java.net.URI"   => (i: Int) => q"castOptOne[java.net.URI](row, $i)"
        case "BigInt"         => (i: Int) => q"castOptOneBigInt(row, $i)"
        case "BigDecimal"     => (i: Int) => q"castOptOneBigDecimal(row, $i)"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"         => (i: Int) => q"castOptMany[String](row, $i)"
        case "Int"            => (i: Int) => q"castOptManyInt(row, $i)"
        case "Float"          => (i: Int) => q"castOptManyFloat(row, $i)"
        case "Boolean"        => (i: Int) => q"castOptMany[Boolean](row, $i)"
        case "Long"           => (i: Int) => q"castOptManyLong(row, $i)"
        case "Double"         => (i: Int) => q"castOptManyDouble(row, $i)"
        case "java.util.Date" => (i: Int) => q"castOptMany[java.util.Date](row, $i)"
        case "java.util.UUID" => (i: Int) => q"castOptMany[java.util.UUID](row, $i)"
        case "java.net.URI"   => (i: Int) => q"castOptMany[java.net.URI](row, $i)"
        case "BigInt"         => (i: Int) => q"castOptManyBigInt(row, $i)"
        case "BigDecimal"     => (i: Int) => q"castOptManyBigDecimal(row, $i)"
      }
    }

  val castOptionalApplyAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1) {
    // Optional, card one
    t.tpeS match {
      case "String"         => (i: Int) => q"castOptOneApply[String](row, $i)"
      case "Int"            => (i: Int) => q"castOptOneApplyInt(row, $i)"
      case "Float"          => (i: Int) => q"castOptOneApplyFloat(row, $i)"
      case "Boolean"        => (i: Int) => q"castOptOneApply[Boolean](row, $i)"
      case "Long"           => (i: Int) => q"castOptOneApplyLong(row, $i)"
      case "Double"         => (i: Int) => q"castOptOneApplyDouble(row, $i)"
      case "java.util.Date" => (i: Int) => q"castOptOneApply[java.util.Date](row, $i)"
      case "java.util.UUID" => (i: Int) => q"castOptOneApply[java.util.UUID](row, $i)"
      case "java.net.URI"   => (i: Int) => q"castOptOneApply[java.net.URI](row, $i)"
      case "BigInt"         => (i: Int) => q"castOptOneApplyBigInt(row, $i)"
      case "BigDecimal"     => (i: Int) => q"castOptOneApplyBigDecimal(row, $i)"
    }
  } else {
    // Optional, card many
    t.tpeS match {
      case "String"         => (i: Int) => q"castOptManyApply[String](row, $i)"
      case "Int"            => (i: Int) => q"castOptManyApplyInt(row, $i)"
      case "Float"          => (i: Int) => q"castOptManyApplyFloat(row, $i)"
      case "Boolean"        => (i: Int) => q"castOptManyApply[Boolean](row, $i)"
      case "Long"           => (i: Int) => q"castOptManyApplyLong(row, $i)"
      case "Double"         => (i: Int) => q"castOptManyApplyDouble(row, $i)"
      case "java.util.Date" => (i: Int) => q"castOptManyApply[java.util.Date](row, $i)"
      case "java.util.UUID" => (i: Int) => q"castOptManyApply[java.util.UUID](row, $i)"
      case "java.net.URI"   => (i: Int) => q"castOptManyApply[java.net.URI](row, $i)"
      case "BigInt"         => (i: Int) => q"castOptManyApplyBigInt(row, $i)"
      case "BigDecimal"     => (i: Int) => q"castOptManyApplyBigDecimal(row, $i)"
    }
  }

  val castOptionalRefAttr: richTree => Int => Tree = (t: richTree) => if (t.card == 1)
    (i: Int) => q"castOptOneRefAttr(row, $i)"
  else
    (i: Int) => q"castOptManyRefAttr(row, $i)"

  def castEnum(t: richTree): Int => Tree = if (t.card == 1)
    (i: Int) => q"row.get($i).asInstanceOf[String]"
  else
    (i: Int) => q"castMany[String](row, $i)"

  val castEnumOpt: richTree => Int => Tree = (t: richTree) => if (t.card == 1)
    (i: Int) => q"castOptOneEnum(row, $i)"
  else
    (i: Int) => q"castOptManyEnum(row, $i)"

  val castMandatoryMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"         => (i: Int) => q"castMapString(row, $i)"
    case "Int"            => (i: Int) => q"castMapInt(row, $i)"
    case "Float"          => (i: Int) => q"castMapFloat(row, $i)"
    case "Boolean"        => (i: Int) => q"castMapBoolean(row, $i)"
    case "Long"           => (i: Int) => q"castMapLong(row, $i)"
    case "Double"         => (i: Int) => q"castMapDouble(row, $i)"
    case "java.util.Date" => (i: Int) => q"castMapDate(row, $i)"
    case "java.util.UUID" => (i: Int) => q"castMapUUID(row, $i)"
    case "java.net.URI"   => (i: Int) => q"castMapURI(row, $i)"
    case "BigInt"         => (i: Int) => q"castMapBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castMapBigDecimal(row, $i)"
  }

  val castOptionalMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"         => (i: Int) => q"castOptMapString(row, $i)"
    case "Int"            => (i: Int) => q"castOptMapInt(row, $i)"
    case "Float"          => (i: Int) => q"castOptMapFloat(row, $i)"
    case "Boolean"        => (i: Int) => q"castOptMapBoolean(row, $i)"
    case "Long"           => (i: Int) => q"castOptMapLong(row, $i)"
    case "Double"         => (i: Int) => q"castOptMapDouble(row, $i)"
    case "java.util.Date" => (i: Int) => q"castOptMapDate(row, $i)"
    case "java.util.UUID" => (i: Int) => q"castOptMapUUID(row, $i)"
    case "java.net.URI"   => (i: Int) => q"castOptMapURI(row, $i)"
    case "BigInt"         => (i: Int) => q"castOptMapBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castOptMapBigDecimal(row, $i)"
  }

  val castOptionalMapApplyAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"         => (i: Int) => q"castOptMapApplyString(row, $i)"
    case "Int"            => (i: Int) => q"castOptMapApplyInt(row, $i)"
    case "Float"          => (i: Int) => q"castOptMapApplyFloat(row, $i)"
    case "Boolean"        => (i: Int) => q"castOptMapApplyBoolean(row, $i)"
    case "Long"           => (i: Int) => q"castOptMapApplyLong(row, $i)"
    case "Double"         => (i: Int) => q"castOptMapApplyDouble(row, $i)"
    case "java.util.Date" => (i: Int) => q"castOptMapApplyDate(row, $i)"
    case "java.util.UUID" => (i: Int) => q"castOptMapApplyUUID(row, $i)"
    case "java.net.URI"   => (i: Int) => q"castOptMapApplyURI(row, $i)"
    case "BigInt"         => (i: Int) => q"castOptMapApplyBigInt(row, $i)"
    case "BigDecimal"     => (i: Int) => q"castOptMapApplyBigDecimal(row, $i)"
  }

  def castKeyedMapAttr(tpe: String): Int => Tree = tpe match {
    case "String"         => (i: Int) => q"row.get($i).toString"
    case "Int"            => (i: Int) => q"row.get($i).toString.toInt"
    case "Long"           => (i: Int) => q"row.get($i).toString.toLong"
    case "Float"          => (i: Int) => q"row.get($i).toString.toFloat"
    case "Double"         => (i: Int) => q"row.get($i).toString.toDouble"
    case "Boolean"        => (i: Int) => q"row.get($i).toString.toBoolean"
    case "java.util.Date" => (i: Int) => q"molecule.util.fns.str2date(row.get($i).toString)"
    case "java.util.UUID" => (i: Int) => q"java.util.UUID.fromString(row.get($i).toString)"
    case "java.net.URI"   => (i: Int) => q"new java.net.URI(row.get($i).toString)"
    case "BigInt"         => (i: Int) => q"BigInt(row.get($i).toString)"
    case "BigDecimal"     => (i: Int) => q"BigDecimal(row.get($i).toString)"
    case "Any"            => (i: Int) => q"row.get($i)"
  }
}
