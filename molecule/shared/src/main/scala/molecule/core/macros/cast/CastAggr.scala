package molecule.core.macros.cast

import scala.reflect.macros.blackbox

private[molecule] trait CastAggr extends CastOptNested {
  val c: blackbox.Context

  import c.universe._

  // (castIndex, arrayType) for looking up
  // - cast lambda array index in CastLamdas and
  // - data arrays index in CastArrays
  var ii = (-1, -1)

  val castAggrInt   : Int => Tree = {
    ii = (108 , 1)
    (colIndex: Int) => q"row.get($colIndex).toString.toInt"
  }
  val castAggrDouble: Int => Tree = {
    ii = (109, 3)
    (colIndex: Int) => q"row.get($colIndex).asInstanceOf[Double]"
  }

  val castAggrOneList : String => Int => Tree = {
    case "String"     => ii = (110, 60); (colIndex: Int) => q"castAggrOneList[String](row, $colIndex)"
    case "Int"        => ii = (111, 61); (colIndex: Int) => q"castAggrOneListInt(row, $colIndex)"
    case "Long"       => ii = (112, 62); (colIndex: Int) => q"castAggrOneList[Long](row, $colIndex)"
    case "Double"     => ii = (113, 63); (colIndex: Int) => q"castAggrOneList[Double](row, $colIndex)"
    case "Boolean"    => ii = (114, 64); (colIndex: Int) => q"castAggrOneList[Boolean](row, $colIndex)"
    case "Date"       => ii = (115, 65); (colIndex: Int) => q"castAggrOneList[Date](row, $colIndex)"
    case "UUID"       => ii = (116, 66); (colIndex: Int) => q"castAggrOneList[UUID](row, $colIndex)"
    case "URI"        => ii = (117, 67); (colIndex: Int) => q"castAggrOneListURI(row, $colIndex)"
    case "BigInt"     => ii = (118, 68); (colIndex: Int) => q"castAggrOneListBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (119, 69); (colIndex: Int) => q"castAggrOneListBigDecimal(row, $colIndex)"
  }
  val castAggrManyList: String => Int => Tree = {
    case "String"     => ii = (120, 70); (colIndex: Int) => q"castAggrManyList[String](row, $colIndex)"
    case "Int"        => ii = (121, 71); (colIndex: Int) => q"castAggrManyListInt(row, $colIndex)"
    case "Long"       => ii = (122, 72); (colIndex: Int) => q"castAggrManyList[Long](row, $colIndex)"
    case "Double"     => ii = (123, 73); (colIndex: Int) => q"castAggrManyList[Double](row, $colIndex)"
    case "Boolean"    => ii = (124, 74); (colIndex: Int) => q"castAggrManyList[Boolean](row, $colIndex)"
    case "Date"       => ii = (125, 75); (colIndex: Int) => q"castAggrManyList[Date](row, $colIndex)"
    case "UUID"       => ii = (126, 76); (colIndex: Int) => q"castAggrManyList[UUID](row, $colIndex)"
    case "URI"        => ii = (127, 77); (colIndex: Int) => q"castAggrManyListURI(row, $colIndex)"
    case "BigInt"     => ii = (128, 78); (colIndex: Int) => q"castAggrManyListBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (129, 79); (colIndex: Int) => q"castAggrManyListBigDecimal(row, $colIndex)"
  }

  val castAggrOneListDistinct : String => Int => Tree = {
    case "String"     => ii = (130, 60); (colIndex: Int) => q"castAggrOneListDistinct[String](row, $colIndex)"
    case "Int"        => ii = (131, 61); (colIndex: Int) => q"castAggrOneListDistinctInt(row, $colIndex)"
    case "Long"       => ii = (132, 62); (colIndex: Int) => q"castAggrOneListDistinct[Long](row, $colIndex)"
    case "Double"     => ii = (133, 63); (colIndex: Int) => q"castAggrOneListDistinct[Double](row, $colIndex)"
    case "Boolean"    => ii = (134, 64); (colIndex: Int) => q"castAggrOneListDistinct[Boolean](row, $colIndex)"
    case "Date"       => ii = (135, 65); (colIndex: Int) => q"castAggrOneListDistinct[Date](row, $colIndex)"
    case "UUID"       => ii = (136, 66); (colIndex: Int) => q"castAggrOneListDistinct[UUID](row, $colIndex)"
    case "URI"        => ii = (137, 67); (colIndex: Int) => q"castAggrOneListDistinctURI(row, $colIndex)"
    case "BigInt"     => ii = (138, 68); (colIndex: Int) => q"castAggrOneListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (139, 69); (colIndex: Int) => q"castAggrOneListDistinctBigDecimal(row, $colIndex)"
  }
  val castAggrManyListDistinct: String => Int => Tree = {
    case "String"     => ii = (140, 77); (colIndex: Int) => q"castAggrManyListDistinct[String](row, $colIndex)"
    case "Int"        => ii = (141, 78); (colIndex: Int) => q"castAggrManyListDistinctInt(row, $colIndex)"
    case "Long"       => ii = (142, 80); (colIndex: Int) => q"castAggrManyListDistinct[Long](row, $colIndex)"
    case "Double"     => ii = (143, 81); (colIndex: Int) => q"castAggrManyListDistinct[Double](row, $colIndex)"
    case "Boolean"    => ii = (144, 82); (colIndex: Int) => q"castAggrManyListDistinct[Boolean](row, $colIndex)"
    case "Date"       => ii = (145, 83); (colIndex: Int) => q"castAggrManyListDistinct[Date](row, $colIndex)"
    case "UUID"       => ii = (146, 84); (colIndex: Int) => q"castAggrManyListDistinct[UUID](row, $colIndex)"
    case "URI"        => ii = (147, 85); (colIndex: Int) => q"castAggrManyListDistinctURI(row, $colIndex)"
    case "BigInt"     => ii = (148, 86); (colIndex: Int) => q"castAggrManyListDistinctBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (149, 87); (colIndex: Int) => q"castAggrManyListDistinctBigDecimal(row, $colIndex)"
  }

  val castAggrOneListRand : String => Int => Tree = {
    case "String"     => ii = (150, 60); (colIndex: Int) => q"castAggrOneListRand[String](row, $colIndex)"
    case "Int"        => ii = (151, 61); (colIndex: Int) => q"castAggrOneListRandInt(row, $colIndex)"
    case "Long"       => ii = (152, 62); (colIndex: Int) => q"castAggrOneListRand[Long](row, $colIndex)"
    case "Double"     => ii = (153, 63); (colIndex: Int) => q"castAggrOneListRand[Double](row, $colIndex)"
    case "Boolean"    => ii = (154, 64); (colIndex: Int) => q"castAggrOneListRand[Boolean](row, $colIndex)"
    case "Date"       => ii = (155, 65); (colIndex: Int) => q"castAggrOneListRand[Date](row, $colIndex)"
    case "UUID"       => ii = (156, 66); (colIndex: Int) => q"castAggrOneListRand[UUID](row, $colIndex)"
    case "URI"        => ii = (157, 67); (colIndex: Int) => q"castAggrOneListRandURI(row, $colIndex)"
    case "BigInt"     => ii = (158, 68); (colIndex: Int) => q"castAggrOneListRandBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (159, 69); (colIndex: Int) => q"castAggrOneListRandBigDecimal(row, $colIndex)"
  }
  val castAggrManyListRand: String => Int => Tree = {
    case "String"     => ii = (160, 77); (colIndex: Int) => q"castAggrManyListRand[String](row, $colIndex)"
    case "Int"        => ii = (161, 78); (colIndex: Int) => q"castAggrManyListRandInt(row, $colIndex)"
    case "Long"       => ii = (162, 80); (colIndex: Int) => q"castAggrManyListRand[Long](row, $colIndex)"
    case "Double"     => ii = (163, 81); (colIndex: Int) => q"castAggrManyListRand[Double](row, $colIndex)"
    case "Boolean"    => ii = (164, 82); (colIndex: Int) => q"castAggrManyListRand[Boolean](row, $colIndex)"
    case "Date"       => ii = (165, 83); (colIndex: Int) => q"castAggrManyListRand[Date](row, $colIndex)"
    case "UUID"       => ii = (166, 84); (colIndex: Int) => q"castAggrManyListRand[UUID](row, $colIndex)"
    case "URI"        => ii = (167, 85); (colIndex: Int) => q"castAggrManyListRandURI(row, $colIndex)"
    case "BigInt"     => ii = (168, 86); (colIndex: Int) => q"castAggrManyListRandBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (169, 87); (colIndex: Int) => q"castAggrManyListRandBigDecimal(row, $colIndex)"
  }


  val castAggrSingleSample: String => Int => Tree = {
    case "String"     => ii = (170, 0); (colIndex: Int) => q"castAggrSingleSample[String](row, $colIndex)"
    case "Int"        => ii = (171, 1); (colIndex: Int) => q"castAggrSingleSampleInt(row, $colIndex)"
    case "Long"       => ii = (172, 2); (colIndex: Int) => q"castAggrSingleSample[Long](row, $colIndex)"
    case "Double"     => ii = (173, 3); (colIndex: Int) => q"castAggrSingleSample[Double](row, $colIndex)"
    case "Boolean"    => ii = (174, 4); (colIndex: Int) => q"castAggrSingleSample[Boolean](row, $colIndex)"
    case "Date"       => ii = (175, 5); (colIndex: Int) => q"castAggrSingleSample[Date](row, $colIndex)"
    case "UUID"       => ii = (176, 6); (colIndex: Int) => q"castAggrSingleSample[UUID](row, $colIndex)"
    case "URI"        => ii = (177, 7); (colIndex: Int) => q"castAggrSingleSampleURI(row, $colIndex)"
    case "BigInt"     => ii = (178, 8); (colIndex: Int) => q"castAggrSingleSampleBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (179, 9); (colIndex: Int) => q"castAggrSingleSampleBigDecimal(row, $colIndex)"
  }

  val castAggrOneSingle : String => Int => Tree = {
    case "String"     => ii = (180, 0); (colIndex: Int) => q"castOne[String](row, $colIndex)"
    case "Int"        => ii = (181, 1); (colIndex: Int) => q"castOneInt(row, $colIndex)"
    case "Long"       => ii = (182, 2); (colIndex: Int) => q"castOne[Long](row, $colIndex)"
    case "Double"     => ii = (183, 3); (colIndex: Int) => q"castOne[Double](row, $colIndex)"
    case "Boolean"    => ii = (184, 4); (colIndex: Int) => q"castOne[Boolean](row, $colIndex)"
    case "Date"       => ii = (185, 5); (colIndex: Int) => q"castOne[Date](row, $colIndex)"
    case "UUID"       => ii = (186, 6); (colIndex: Int) => q"castOne[UUID](row, $colIndex)"
    case "URI"        => ii = (187, 7); (colIndex: Int) => q"castOneURI(row, $colIndex)"
    case "BigInt"     => ii = (188, 8); (colIndex: Int) => q"castOneBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (189, 9); (colIndex: Int) => q"castOneBigDecimal(row, $colIndex)"
  }
  val castAggrManySingle: String => Int => Tree = {
    case "String"     => ii = (190, 10); (colIndex: Int) => q"castAggrManySingle[String](row, $colIndex)"
    case "Int"        => ii = (191, 11); (colIndex: Int) => q"castAggrManySingleInt(row, $colIndex)"
    case "Long"       => ii = (192, 12); (colIndex: Int) => q"castAggrManySingle[Long](row, $colIndex)"
    case "Double"     => ii = (193, 13); (colIndex: Int) => q"castAggrManySingle[Double](row, $colIndex)"
    case "Boolean"    => ii = (194, 14); (colIndex: Int) => q"castAggrManySingle[Boolean](row, $colIndex)"
    case "Date"       => ii = (195, 15); (colIndex: Int) => q"castAggrManySingle[Date](row, $colIndex)"
    case "UUID"       => ii = (196, 16); (colIndex: Int) => q"castAggrManySingle[UUID](row, $colIndex)"
    case "URI"        => ii = (197, 17); (colIndex: Int) => q"castAggrManySingleURI(row, $colIndex)"
    case "BigInt"     => ii = (198, 18); (colIndex: Int) => q"castAggrManySingleBigInt(row, $colIndex)"
    case "BigDecimal" => ii = (199, 19); (colIndex: Int) => q"castAggrManySingleBigDecimal(row, $colIndex)"
  }
}
