package molecule.core.macros.cast

import scala.reflect.macros.blackbox

private[molecule] trait CastAggr extends CastOptNested {
  val c: blackbox.Context

  import c.universe._

  // (castIndex, arrayType) for looking up cast lambda and QueryResult array
  var ii = (-1, -1)

  val castAggrInt   : Int => Tree = {
    ii = (118 , 1)
    (colIndex: Int) => q"row.get($colIndex).toString.toInt"
  }
  val castAggrDouble: Int => Tree = {
    ii = (119, 4)
    (colIndex: Int) => q"row.get($colIndex).asInstanceOf[Double]"
  }

  val castAggrOneList : String => Int => Tree = {
    case "String"     => ii = (120, 66); (colIndex: Int) => q"castAggrOneList[String](row, $colIndex)"
    case "Int"        => ii = (121, 67); (colIndex: Int) => q"castAggrOneListInt(row, $colIndex)"
    case "Float"      => ii = (122, 68); (colIndex: Int) => q"castAggrOneListFloat(row, $colIndex)"
    case "Long"       => ii = (123, 69); (colIndex: Int) => q"castAggrOneList[Long](row, $colIndex)"
    case "Double"     => ii = (124, 70); (colIndex: Int) => q"castAggrOneList[Double](row, $colIndex)"
    case "Boolean"    => ii = (125, 71); (colIndex: Int) => q"castAggrOneList[Boolean](row, $colIndex)"
    case "Date"       => ii = (126, 72); (colIndex: Int) => q"castAggrOneList[Date](row, $colIndex)"
    case "UUID"       => ii = (127, 73); (colIndex: Int) => q"castAggrOneList[UUID](row, $colIndex)"
    case "URI"        => ii = (128, 74); (colIndex: Int) => q"castAggrOneListURI(row, $colIndex)"
    case "BigInt"     => ii = (129, 75); (colIndex: Int) => q"castAggrOneListBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (130, 76); (colIndex: Int) => q"castAggrOneListBigDecimal(row, $colIndex)"
  }
  val castAggrManyList: String => Int => Tree = {
    case "String"     => ii = (131, 77); (colIndex: Int) => q"castAggrManyList[String](row, $colIndex)"
    case "Int"        => ii = (132, 78); (colIndex: Int) => q"castAggrManyListInt(row, $colIndex)"
    case "Float"      => ii = (133, 79); (colIndex: Int) => q"castAggrManyListFloat(row, $colIndex)"
    case "Long"       => ii = (134, 80); (colIndex: Int) => q"castAggrManyList[Long](row, $colIndex)"
    case "Double"     => ii = (135, 81); (colIndex: Int) => q"castAggrManyList[Double](row, $colIndex)"
    case "Boolean"    => ii = (136, 82); (colIndex: Int) => q"castAggrManyList[Boolean](row, $colIndex)"
    case "Date"       => ii = (137, 83); (colIndex: Int) => q"castAggrManyList[Date](row, $colIndex)"
    case "UUID"       => ii = (138, 84); (colIndex: Int) => q"castAggrManyList[UUID](row, $colIndex)"
    case "URI"        => ii = (139, 85); (colIndex: Int) => q"castAggrManyListURI(row, $colIndex)"
    case "BigInt"     => ii = (140, 86); (colIndex: Int) => q"castAggrManyListBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (141, 87); (colIndex: Int) => q"castAggrManyListBigDecimal(row, $colIndex)"
  }

  val castAggrOneListDistinct : String => Int => Tree = {
    case "String"     => ii = (142, 66); (colIndex: Int) => q"castAggrOneListDistinct[String](row, $colIndex)"
    case "Int"        => ii = (143, 67); (colIndex: Int) => q"castAggrOneListDistinctInt(row, $colIndex)"
    case "Float"      => ii = (144, 68); (colIndex: Int) => q"castAggrOneListDistinctFloat(row, $colIndex)"
    case "Long"       => ii = (145, 69); (colIndex: Int) => q"castAggrOneListDistinct[Long](row, $colIndex)"
    case "Double"     => ii = (146, 70); (colIndex: Int) => q"castAggrOneListDistinct[Double](row, $colIndex)"
    case "Boolean"    => ii = (147, 71); (colIndex: Int) => q"castAggrOneListDistinct[Boolean](row, $colIndex)"
    case "Date"       => ii = (148, 72); (colIndex: Int) => q"castAggrOneListDistinct[Date](row, $colIndex)"
    case "UUID"       => ii = (149, 73); (colIndex: Int) => q"castAggrOneListDistinct[UUID](row, $colIndex)"
    case "URI"        => ii = (150, 74); (colIndex: Int) => q"castAggrOneListDistinctURI(row, $colIndex)"
    case "BigInt"     => ii = (151, 75); (colIndex: Int) => q"castAggrOneListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (152, 76); (colIndex: Int) => q"castAggrOneListDistinctBigDecimal(row, $colIndex)"
  }
  val castAggrManyListDistinct: String => Int => Tree = {
    case "String"     => ii = (153, 77); (colIndex: Int) => q"castAggrManyListDistinct[String](row, $colIndex)"
    case "Int"        => ii = (154, 78); (colIndex: Int) => q"castAggrManyListDistinctInt(row, $colIndex)"
    case "Float"      => ii = (155, 79); (colIndex: Int) => q"castAggrManyListDistinctFloat(row, $colIndex)"
    case "Long"       => ii = (156, 80); (colIndex: Int) => q"castAggrManyListDistinct[Long](row, $colIndex)"
    case "Double"     => ii = (157, 81); (colIndex: Int) => q"castAggrManyListDistinct[Double](row, $colIndex)"
    case "Boolean"    => ii = (158, 82); (colIndex: Int) => q"castAggrManyListDistinct[Boolean](row, $colIndex)"
    case "Date"       => ii = (159, 83); (colIndex: Int) => q"castAggrManyListDistinct[Date](row, $colIndex)"
    case "UUID"       => ii = (160, 84); (colIndex: Int) => q"castAggrManyListDistinct[UUID](row, $colIndex)"
    case "URI"        => ii = (161, 85); (colIndex: Int) => q"castAggrManyListDistinctURI(row, $colIndex)"
    case "BigInt"     => ii = (162, 86); (colIndex: Int) => q"castAggrManyListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (163, 87); (colIndex: Int) => q"castAggrManyListDistinctBigDecimal(row, $colIndex)"
  }

  val castAggrOneListRand : String => Int => Tree = {
    case "String"     => ii = (164, 66); (colIndex: Int) => q"castAggrOneListRand[String](row, $colIndex)"
    case "Int"        => ii = (165, 67); (colIndex: Int) => q"castAggrOneListRandInt(row, $colIndex)"
    case "Float"      => ii = (166, 68); (colIndex: Int) => q"castAggrOneListRandFloat(row, $colIndex)"
    case "Long"       => ii = (167, 69); (colIndex: Int) => q"castAggrOneListRand[Long](row, $colIndex)"
    case "Double"     => ii = (168, 70); (colIndex: Int) => q"castAggrOneListRand[Double](row, $colIndex)"
    case "Boolean"    => ii = (169, 71); (colIndex: Int) => q"castAggrOneListRand[Boolean](row, $colIndex)"
    case "Date"       => ii = (170, 72); (colIndex: Int) => q"castAggrOneListRand[Date](row, $colIndex)"
    case "UUID"       => ii = (171, 73); (colIndex: Int) => q"castAggrOneListRand[UUID](row, $colIndex)"
    case "URI"        => ii = (172, 74); (colIndex: Int) => q"castAggrOneListRandURI(row, $colIndex)"
    case "BigInt"     => ii = (173, 75); (colIndex: Int) => q"castAggrOneListRandBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (174, 76); (colIndex: Int) => q"castAggrOneListRandBigDecimal(row, $colIndex)"
  }
  val castAggrManyListRand: String => Int => Tree = {
    case "String"     => ii = (175, 77); (colIndex: Int) => q"castAggrManyListRand[String](row, $colIndex)"
    case "Int"        => ii = (176, 78); (colIndex: Int) => q"castAggrManyListRandInt(row, $colIndex)"
    case "Float"      => ii = (177, 79); (colIndex: Int) => q"castAggrManyListRandFloat(row, $colIndex)"
    case "Long"       => ii = (178, 80); (colIndex: Int) => q"castAggrManyListRand[Long](row, $colIndex)"
    case "Double"     => ii = (179, 81); (colIndex: Int) => q"castAggrManyListRand[Double](row, $colIndex)"
    case "Boolean"    => ii = (180, 82); (colIndex: Int) => q"castAggrManyListRand[Boolean](row, $colIndex)"
    case "Date"       => ii = (181, 83); (colIndex: Int) => q"castAggrManyListRand[Date](row, $colIndex)"
    case "UUID"       => ii = (182, 84); (colIndex: Int) => q"castAggrManyListRand[UUID](row, $colIndex)"
    case "URI"        => ii = (183, 85); (colIndex: Int) => q"castAggrManyListRandURI(row, $colIndex)"
    case "BigInt"     => ii = (184, 86); (colIndex: Int) => q"castAggrManyListRandBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (185, 87); (colIndex: Int) => q"castAggrManyListRandBigDecimal(row, $colIndex)"
  }


  val castAggrSingleSample: String => Int => Tree = {
    case "String"     => ii = (186,  0); (colIndex: Int) => q"castAggrSingleSample[String](row, $colIndex)"
    case "Int"        => ii = (187,  1); (colIndex: Int) => q"castAggrSingleSampleInt(row, $colIndex)"
    case "Float"      => ii = (188,  2); (colIndex: Int) => q"castAggrSingleSampleFloat(row, $colIndex)"
    case "Long"       => ii = (189,  3); (colIndex: Int) => q"castAggrSingleSample[Long](row, $colIndex)"
    case "Double"     => ii = (190,  4); (colIndex: Int) => q"castAggrSingleSample[Double](row, $colIndex)"
    case "Boolean"    => ii = (191,  5); (colIndex: Int) => q"castAggrSingleSample[Boolean](row, $colIndex)"
    case "Date"       => ii = (192,  6); (colIndex: Int) => q"castAggrSingleSample[Date](row, $colIndex)"
    case "UUID"       => ii = (193,  7); (colIndex: Int) => q"castAggrSingleSample[UUID](row, $colIndex)"
    case "URI"        => ii = (194,  8); (colIndex: Int) => q"castAggrSingleSampleURI(row, $colIndex)"
    case "BigInt"     => ii = (195,  9); (colIndex: Int) => q"castAggrSingleSampleBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (196, 10); (colIndex: Int) => q"castAggrSingleSampleBigDecimal(row, $colIndex)"
  }

  val castAggrOneSingle : String => Int => Tree = {
    case "String"     => ii = (197,  0); (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => ii = (198,  1); (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Float"      => ii = (199,  2); (colIndex: Int) => q"castOneFloat(row, $colIndex)"
    case "Long"       => ii = (200,  3); (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Double"     => ii = (201,  4); (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => ii = (202,  5); (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => ii = (203,  6); (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => ii = (204,  7); (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => ii = (205,  8); (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => ii = (206,  9); (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (207, 10); (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
  }
  val castAggrManySingle: String => Int => Tree = {
    case "String"     => ii = (208, 11); (colIndex: Int) => q"castAggrManySingle[String](row, $colIndex)"
    case "Int"        => ii = (209, 12); (colIndex: Int) => q"castAggrManySingleInt(row, $colIndex)"
    case "Float"      => ii = (210, 13); (colIndex: Int) => q"castAggrManySingleFloat(row, $colIndex)"
    case "Long"       => ii = (211, 14); (colIndex: Int) => q"castAggrManySingle[Long](row, $colIndex)"
    case "Double"     => ii = (212, 15); (colIndex: Int) => q"castAggrManySingle[Double](row, $colIndex)"
    case "Boolean"    => ii = (213, 16); (colIndex: Int) => q"castAggrManySingle[Boolean](row, $colIndex)"
    case "Date"       => ii = (214, 17); (colIndex: Int) => q"castAggrManySingle[Date](row, $colIndex)"
    case "UUID"       => ii = (215, 18); (colIndex: Int) => q"castAggrManySingle[UUID](row, $colIndex)"
    case "URI"        => ii = (216, 19); (colIndex: Int) => q"castAggrManySingleURI(row, $colIndex)"
    case "BigInt"     => ii = (217, 20); (colIndex: Int) => q"castAggrManySingleBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (218, 21); (colIndex: Int) => q"castAggrManySingleBigDecimal(row, $colIndex)"
  }
}
