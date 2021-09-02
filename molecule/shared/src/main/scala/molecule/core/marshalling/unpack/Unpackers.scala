package molecule.core.marshalling.unpack

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

trait Unpackers extends TreeOps with UnpackTypes {
  val c: blackbox.Context

  import c.universe._

  def unpackLambdas2(group: String, baseTpe: String, v: Tree): Tree = group match {
    case "One" | "KeyedMap" | "AggrSingleSample" | "AggrOneSingle"    => unpackOneAttr(baseTpe, v)
    case "OptOne" | "OptApplyOne"                                     => unpackOptOneAttr(baseTpe, v)
    case "Many" | "AggrManySingle"                                    => unpackManyAttr(baseTpe, v)
    case "OptMany" | "OptApplyMany"                                   => unpackOptManyAttr(baseTpe, v)
    case "Map"                                                        => unpackMapAttr(baseTpe, v)
    case "OptMap" | "OptApplyMap"                                     => unpackOptMapAttr(baseTpe, v)
    case "AggrOneList" | "AggrOneListDistinct" | "AggrOneListRand"    => unpackAggrOneList(baseTpe, v)
    case "AggrManyList" | "AggrManyListDistinct" | "AggrManyListRand" => unpackAggrManyList(baseTpe, v)
  }

  def unpackOneAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackOneString($v, vs)"
    case "Int"        => q"unpackOneInt($v)"
    case "Long"       => q"unpackOneLong($v)"
    case "Double"     => q"unpackOneDouble($v)"
    case "Boolean"    => q"unpackOneBoolean($v)"
    case "Date"       => q"unpackOneDate($v)"
    case "UUID"       => q"unpackOneUUID($v)"
    case "URI"        => q"unpackOneURI($v)"
    case "BigInt"     => q"unpackOneBigInt($v)"
    case "BigDecimal" => q"unpackOneBigDecimal($v)"
    case "Any"        => q"unpackOneAny($v)"
  }

  def unpackOptOneAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackOptOneString($v, vs)"
    case "Int"        => q"unpackOptOneInt($v)"
    case "Long"       => q"unpackOptOneLong($v)"
    case "Double"     => q"unpackOptOneDouble($v)"
    case "Boolean"    => q"unpackOptOneBoolean($v)"
    case "Date"       => q"unpackOptOneDate($v)"
    case "UUID"       => q"unpackOptOneUUID($v)"
    case "URI"        => q"unpackOptOneURI($v)"
    case "BigInt"     => q"unpackOptOneBigInt($v)"
    case "BigDecimal" => q"unpackOptOneBigDecimal($v)"
    case "enum"       => q"unpackOptOneEnum($v)"
    case "ref"        => q"unpackOptOneLong($v)"
  }

  def unpackManyAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackManyString($v, vs)"
    case "Int"        => q"unpackManyInt($v, vs)"
    case "Long"       => q"unpackManyLong($v, vs)"
    case "Double"     => q"unpackManyDouble($v, vs)"
    case "Boolean"    => q"unpackManyBoolean($v, vs)"
    case "Date"       => q"unpackManyDate($v, vs)"
    case "UUID"       => q"unpackManyUUID($v, vs)"
    case "URI"        => q"unpackManyURI($v, vs)"
    case "BigInt"     => q"unpackManyBigInt($v, vs)"
    case "BigDecimal" => q"unpackManyBigDecimal($v, vs)"
    case "enum"       => q"unpackManyString($v, vs)"
  }

  def unpackOptManyAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackOptManyString($v, vs)"
    case "Int"        => q"unpackOptManyInt($v, vs)"
    case "Long"       => q"unpackOptManyLong($v, vs)"
    case "Double"     => q"unpackOptManyDouble($v, vs)"
    case "Boolean"    => q"unpackOptManyBoolean($v, vs)"
    case "Date"       => q"unpackOptManyDate($v, vs)"
    case "UUID"       => q"unpackOptManyUUID($v, vs)"
    case "URI"        => q"unpackOptManyURI($v, vs)"
    case "BigInt"     => q"unpackOptManyBigInt($v, vs)"
    case "BigDecimal" => q"unpackOptManyBigDecimal($v, vs)"
    case "enum"       => q"unpackOptManyString($v, vs)"
    case "ref"        => q"unpackOptManyRefAttr($v, vs)"
  }

  def unpackMapAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackMapString($v, vs)"
    case "Int"        => q"unpackMapInt($v, vs)"
    case "Long"       => q"unpackMapLong($v, vs)"
    case "Double"     => q"unpackMapDouble($v, vs)"
    case "Boolean"    => q"unpackMapBoolean($v, vs)"
    case "Date"       => q"unpackMapDate($v, vs)"
    case "UUID"       => q"unpackMapUUID($v, vs)"
    case "URI"        => q"unpackMapURI($v, vs)"
    case "BigInt"     => q"unpackMapBigInt($v, vs)"
    case "BigDecimal" => q"unpackMapBigDecimal($v, vs)"
  }

  def unpackOptMapAttr(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackOptMapString($v, vs)"
    case "Int"        => q"unpackOptMapInt($v, vs)"
    case "Long"       => q"unpackOptMapLong($v, vs)"
    case "Double"     => q"unpackOptMapDouble($v, vs)"
    case "Boolean"    => q"unpackOptMapBoolean($v, vs)"
    case "Date"       => q"unpackOptMapDate($v, vs)"
    case "UUID"       => q"unpackOptMapUUID($v, vs)"
    case "URI"        => q"unpackOptMapURI($v, vs)"
    case "BigInt"     => q"unpackOptMapBigInt($v, vs)"
    case "BigDecimal" => q"unpackOptMapBigDecimal($v, vs)"
  }

  def unpackAggrOneList(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackListString($v, vs)"
    case "Int"        => q"unpackListInt($v, vs)"
    case "Long"       => q"unpackListLong($v, vs)"
    case "Double"     => q"unpackListDouble($v, vs)"
    case "Boolean"    => q"unpackListBoolean($v, vs)"
    case "Date"       => q"unpackListDate($v, vs)"
    case "UUID"       => q"unpackListUUID($v, vs)"
    case "URI"        => q"unpackListURI($v, vs)"
    case "BigInt"     => q"unpackListBigInt($v, vs)"
    case "BigDecimal" => q"unpackListBigDecimal($v, vs)"
  }

  def unpackAggrManyList(baseTpe: String, v: Tree): Tree = baseTpe match {
    case "String"     => q"unpackListSetString($v, vs)"
    case "Int"        => q"unpackListSetInt($v, vs)"
    case "Long"       => q"unpackListSetLong($v, vs)"
    case "Double"     => q"unpackListSetDouble($v, vs)"
    case "Boolean"    => q"unpackListSetBoolean($v, vs)"
    case "Date"       => q"unpackListSetDate($v, vs)"
    case "UUID"       => q"unpackListSetUUID($v, vs)"
    case "URI"        => q"unpackListSetURI($v, vs)"
    case "BigInt"     => q"unpackListSetBigInt($v, vs)"
    case "BigDecimal" => q"unpackListSetBigDecimal($v, vs)"
  }


//  def unpackLambdas(v: Tree): Int => Tree = {
//    case 0  => q"unpackOneString($v, vs)" //                  castOneString
//    case 1  => q"unpackOneInt($v)" //                         castOneInt
//    case 2  => q"unpackOneInt($v)" // (slot for int2 cast )   castOneInt
//    case 3  => q"unpackOneLong($v)" //                        castOneLong
//    case 4  => q"unpackOneDouble($v)" //                      castOneDouble
//    case 5  => q"unpackOneBoolean($v)" //                     castOneBoolean
//    case 6  => q"unpackOneDate($v)" //                        castOneDate
//    case 7  => q"unpackOneUUID($v)" //                        castOneUUID
//    case 8  => q"unpackOneURI($v)" //                         castOneURI
//    case 9  => q"unpackOneBigInt($v)" //                      castOneBigInt
//    case 10 => q"unpackOneBigDecimal($v)" //                  castOneBigDecimal
//    case 11 => q"unpackOneAny($v)" //                         castOneAny
//
//    case 13 => q"unpackOptOneString($v, vs)" //               castOptOneString
//    case 14 => q"unpackOptOneInt($v)" //                      castOptOneInt
//    case 15 => q"unpackOptOneLong($v)" //                     castOptOneLong
//    case 16 => q"unpackOptOneDouble($v)" //                   castOptOneDouble
//    case 17 => q"unpackOptOneBoolean($v)" //                  castOptOneBoolean
//    case 18 => q"unpackOptOneDate($v)" //                     castOptOneDate
//    case 19 => q"unpackOptOneUUID($v)" //                     castOptOneUUID
//    case 20 => q"unpackOptOneURI($v)" //                      castOptOneURI
//    case 21 => q"unpackOptOneBigInt($v)" //                   castOptOneBigInt
//    case 22 => q"unpackOptOneBigDecimal($v)" //               castOptOneBigDecimal
//    case 12 => q"unpackOptOneEnum($v)" //                     castOptOneEnum
//    case 23 => q"unpackOptOneLong($v)" //                     castOptOneRefAttr
//
//    case 25 => q"unpackManyString($v, vs)" //                 castManyString
//    case 26 => q"unpackManyInt($v, vs)" //                    castManyInt
//    case 27 => q"unpackManyLong($v, vs)" //                   castManyLong
//    case 28 => q"unpackManyDouble($v, vs)" //                 castManyDouble
//    case 29 => q"unpackManyBoolean($v, vs)" //                castManyBoolean
//    case 30 => q"unpackManyDate($v, vs)" //                   castManyDate
//    case 31 => q"unpackManyUUID($v, vs)" //                   castManyUUID
//    case 32 => q"unpackManyURI($v, vs)" //                    castManyURI
//    case 33 => q"unpackManyBigInt($v, vs)" //                 castManyBigInt
//    case 34 => q"unpackManyBigDecimal($v, vs)" //             castManyBigDecimal
//    case 24 => q"unpackManyString($v, vs)" //                 castManyEnum
//
//    case 36 => q"unpackOptManyString($v, vs)" //              castOptManyString
//    case 37 => q"unpackOptManyInt($v, vs)" //                 castOptManyInt
//    case 38 => q"unpackOptManyLong($v, vs)" //                castOptManyLong
//    case 39 => q"unpackOptManyDouble($v, vs)" //              castOptManyDouble
//    case 40 => q"unpackOptManyBoolean($v, vs)" //             castOptManyBoolean
//    case 41 => q"unpackOptManyDate($v, vs)" //                castOptManyDate
//    case 42 => q"unpackOptManyUUID($v, vs)" //                castOptManyUUID
//    case 43 => q"unpackOptManyURI($v, vs)" //                 castOptManyURI
//    case 44 => q"unpackOptManyBigInt($v, vs)" //              castOptManyBigInt
//    case 45 => q"unpackOptManyBigDecimal($v, vs)" //          castOptManyBigDecimal
//    case 35 => q"unpackOptManyString($v, vs)" //              castOptManyEnum
//    case 46 => q"unpackOptManyRefAttr($v, vs)" //             castOptManyRefAttr
//
//    case 47 => q"unpackMapString($v, vs)" //                  castMapString
//    case 48 => q"unpackMapInt($v, vs)" //                     castMapInt
//    case 49 => q"unpackMapLong($v, vs)" //                    castMapLong
//    case 50 => q"unpackMapDouble($v, vs)" //                  castMapDouble
//    case 51 => q"unpackMapBoolean($v, vs)" //                 castMapBoolean
//    case 52 => q"unpackMapDate($v, vs)" //                    castMapDate
//    case 53 => q"unpackMapUUID($v, vs)" //                    castMapUUID
//    case 54 => q"unpackMapURI($v, vs)" //                     castMapURI
//    case 55 => q"unpackMapBigInt($v, vs)" //                  castMapBigInt
//    case 56 => q"unpackMapBigDecimal($v, vs)" //              castMapBigDecimal
//
//    case 57 => q"unpackOptMapString($v, vs)" //               castOptMapString
//    case 58 => q"unpackOptMapInt($v, vs)" //                  castOptMapInt
//    case 59 => q"unpackOptMapLong($v, vs)" //                 castOptMapLong
//    case 60 => q"unpackOptMapDouble($v, vs)" //               castOptMapDouble
//    case 61 => q"unpackOptMapBoolean($v, vs)" //              castOptMapBoolean
//    case 62 => q"unpackOptMapDate($v, vs)" //                 castOptMapDate
//    case 63 => q"unpackOptMapUUID($v, vs)" //                 castOptMapUUID
//    case 64 => q"unpackOptMapURI($v, vs)" //                  castOptMapURI
//    case 65 => q"unpackOptMapBigInt($v, vs)" //               castOptMapBigInt
//    case 66 => q"unpackOptMapBigDecimal($v, vs)" //           castOptMapBigDecimal
//
//    case 67 => q"unpackOptOneString($v, vs)" //               castOptApplyOneString
//    case 68 => q"unpackOptOneInt($v)" //                      castOptApplyOneInt
//    case 69 => q"unpackOptOneLong($v)" //                     castOptApplyOneLong
//    case 70 => q"unpackOptOneDouble($v)" //                   castOptApplyOneDouble
//    case 71 => q"unpackOptOneBoolean($v)" //                  castOptApplyOneBoolean
//    case 72 => q"unpackOptOneDate($v)" //                     castOptApplyOneDate
//    case 73 => q"unpackOptOneUUID($v)" //                     castOptApplyOneUUID
//    case 74 => q"unpackOptOneURI($v)" //                      castOptApplyOneURI
//    case 75 => q"unpackOptOneBigInt($v)" //                   castOptApplyOneBigInt
//    case 76 => q"unpackOptOneBigDecimal($v)" //               castOptApplyOneBigDecimal
//
//    case 77 => q"unpackOptManyString($v, vs)" //              castOptApplyManyString
//    case 78 => q"unpackOptManyInt($v, vs)" //                 castOptApplyManyInt
//    case 79 => q"unpackOptManyLong($v, vs)" //                castOptApplyManyLong
//    case 80 => q"unpackOptManyDouble($v, vs)" //              castOptApplyManyDouble
//    case 81 => q"unpackOptManyBoolean($v, vs)" //             castOptApplyManyBoolean
//    case 82 => q"unpackOptManyDate($v, vs)" //                castOptApplyManyDate
//    case 83 => q"unpackOptManyUUID($v, vs)" //                castOptApplyManyUUID
//    case 84 => q"unpackOptManyURI($v, vs)" //                 castOptApplyManyURI
//    case 85 => q"unpackOptManyBigInt($v, vs)" //              castOptApplyManyBigInt
//    case 86 => q"unpackOptManyBigDecimal($v, vs)" //          castOptApplyManyBigDecimal
//
//    case 87 => q"unpackOptMapString($v, vs)" //               castOptApplyMapString
//    case 88 => q"unpackOptMapInt($v, vs)" //                  castOptApplyMapInt
//    case 89 => q"unpackOptMapLong($v, vs)" //                 castOptApplyMapLong
//    case 90 => q"unpackOptMapDouble($v, vs)" //               castOptApplyMapDouble
//    case 91 => q"unpackOptMapBoolean($v, vs)" //              castOptApplyMapBoolean
//    case 92 => q"unpackOptMapDate($v, vs)" //                 castOptApplyMapDate
//    case 93 => q"unpackOptMapUUID($v, vs)" //                 castOptApplyMapUUID
//    case 94 => q"unpackOptMapURI($v, vs)" //                  castOptApplyMapURI
//    case 95 => q"unpackOptMapBigInt($v, vs)" //               castOptApplyMapBigInt
//    case 96 => q"unpackOptMapBigDecimal($v, vs)" //           castOptApplyMapBigDecimal
//
//    case 97  => q"unpackOneString($v, vs)" //                    castKeyedMapString
//    case 98  => q"unpackOneInt($v)" //                           castKeyedMapInt
//    case 99  => q"unpackOneLong($v)" //                          castKeyedMapLong
//    case 100 => q"unpackOneDouble($v)" //                        castKeyedMapDouble
//    case 101 => q"unpackOneBoolean($v)" //                       castKeyedMapBoolean
//    case 102 => q"unpackOneDate($v)" //                          castKeyedMapDate
//    case 103 => q"unpackOneUUID($v)" //                          castKeyedMapUUID
//    case 104 => q"unpackOneURI($v)" //                           castKeyedMapURI
//    case 105 => q"unpackOneBigInt($v)" //                        castKeyedMapBigInt
//    case 106 => q"unpackOneBigDecimal($v)" //                    castKeyedMapBigDecimal
//    case 107 => q"unpackOneAny($v)" //                           castKeyedMapAny
//
//    case 108 => q"unpackOneInt($v)" //                           castAggrInt
//    case 109 => q"unpackOneDouble($v)" //                        castAggrDouble
//
//    case 110 => q"unpackListString($v, vs)" //                castAggrOneListString
//    case 111 => q"unpackListInt($v, vs)" //                   castAggrOneListInt
//    case 112 => q"unpackListLong($v, vs)" //                  castAggrOneListLong
//    case 113 => q"unpackListDouble($v, vs)" //                castAggrOneListDouble
//    case 114 => q"unpackListBoolean($v, vs)" //               castAggrOneListBoolean
//    case 115 => q"unpackListDate($v, vs)" //                  castAggrOneListDate
//    case 116 => q"unpackListUUID($v, vs)" //                  castAggrOneListUUID
//    case 117 => q"unpackListURI($v, vs)" //                   castAggrOneListURI
//    case 118 => q"unpackListBigInt($v, vs)" //                castAggrOneListBigInt
//    case 119 => q"unpackListBigDecimal($v, vs)" //            castAggrOneListBigDecimal
//
//    case 120 => q"unpackListSetString($v, vs)" //             castAggrManyListString
//    case 121 => q"unpackListSetInt($v, vs)" //                castAggrManyListInt
//    case 122 => q"unpackListSetLong($v, vs)" //               castAggrManyListLong
//    case 123 => q"unpackListSetDouble($v, vs)" //             castAggrManyListDouble
//    case 124 => q"unpackListSetBoolean($v, vs)" //            castAggrManyListBoolean
//    case 125 => q"unpackListSetDate($v, vs)" //               castAggrManyListDate
//    case 126 => q"unpackListSetUUID($v, vs)" //               castAggrManyListUUID
//    case 127 => q"unpackListSetURI($v, vs)" //                castAggrManyListURI
//    case 128 => q"unpackListSetBigInt($v, vs)" //             castAggrManyListBigInt
//    case 129 => q"unpackListSetBigDecimal($v, vs)" //         castAggrManyListBigDecimal
//
//    case 130 => q"unpackListString($v, vs)" //                castAggrOneListDistinctString
//    case 131 => q"unpackListInt($v, vs)" //                   castAggrOneListDistinctInt
//    case 132 => q"unpackListLong($v, vs)" //                  castAggrOneListDistinctLong
//    case 133 => q"unpackListDouble($v, vs)" //                castAggrOneListDistinctDouble
//    case 134 => q"unpackListBoolean($v, vs)" //               castAggrOneListDistinctBoolean
//    case 135 => q"unpackListDate($v, vs)" //                  castAggrOneListDistinctDate
//    case 136 => q"unpackListUUID($v, vs)" //                  castAggrOneListDistinctUUID
//    case 137 => q"unpackListURI($v, vs)" //                   castAggrOneListDistinctURI
//    case 138 => q"unpackListBigInt($v, vs)" //                castAggrOneListDistinctBigInt
//    case 139 => q"unpackListBigDecimal($v, vs)" //            castAggrOneListDistinctBigDecimal
//
//    case 140 => q"unpackListSetString($v, vs)" //             castAggrManyListDistinctString
//    case 141 => q"unpackListSetInt($v, vs)" //                castAggrManyListDistinctInt
//    case 142 => q"unpackListSetLong($v, vs)" //               castAggrManyListDistinctLong
//    case 143 => q"unpackListSetDouble($v, vs)" //             castAggrManyListDistinctDouble
//    case 144 => q"unpackListSetBoolean($v, vs)" //            castAggrManyListDistinctBoolean
//    case 145 => q"unpackListSetDate($v, vs)" //               castAggrManyListDistinctDate
//    case 146 => q"unpackListSetUUID($v, vs)" //               castAggrManyListDistinctUUID
//    case 147 => q"unpackListSetURI($v, vs)" //                castAggrManyListDistinctURI
//    case 148 => q"unpackListSetBigInt($v, vs)" //             castAggrManyListDistinctBigInt
//    case 149 => q"unpackListSetBigDecimal($v, vs)" //         castAggrManyListDistinctBigDecimal
//
//    case 150 => q"unpackListString($v, vs)" //                castAggrOneListRandString
//    case 151 => q"unpackListInt($v, vs)" //                   castAggrOneListRandInt
//    case 152 => q"unpackListLong($v, vs)" //                  castAggrOneListRandLong
//    case 153 => q"unpackListDouble($v, vs)" //                castAggrOneListRandDouble
//    case 154 => q"unpackListBoolean($v, vs)" //               castAggrOneListRandBoolean
//    case 155 => q"unpackListDate($v, vs)" //                  castAggrOneListRandDate
//    case 156 => q"unpackListUUID($v, vs)" //                  castAggrOneListRandUUID
//    case 157 => q"unpackListURI($v, vs)" //                   castAggrOneListRandURI
//    case 158 => q"unpackListBigInt($v, vs)" //                castAggrOneListRandBigInt
//    case 159 => q"unpackListBigDecimal($v, vs)" //            castAggrOneListRandBigDecimal
//
//    case 160 => q"unpackListSetString($v, vs)" //             castAggrManyListRandString
//    case 161 => q"unpackListSetInt($v, vs)" //                castAggrManyListRandInt
//    case 162 => q"unpackListSetLong($v, vs)" //               castAggrManyListRandLong
//    case 163 => q"unpackListSetDouble($v, vs)" //             castAggrManyListRandDouble
//    case 164 => q"unpackListSetBoolean($v, vs)" //            castAggrManyListRandBoolean
//    case 165 => q"unpackListSetDate($v, vs)" //               castAggrManyListRandDate
//    case 166 => q"unpackListSetUUID($v, vs)" //               castAggrManyListRandUUID
//    case 167 => q"unpackListSetURI($v, vs)" //                castAggrManyListRandURI
//    case 168 => q"unpackListSetBigInt($v, vs)" //             castAggrManyListRandBigInt
//    case 169 => q"unpackListSetBigDecimal($v, vs)" //         castAggrManyListRandBigDecimal
//
//    case 170 => q"unpackOneString($v, vs)" //                 castAggrSingleSampleString
//    case 171 => q"unpackOneInt($v)" //                        castAggrSingleSampleInt
//    case 172 => q"unpackOneLong($v)" //                       castAggrSingleSampleLong
//    case 173 => q"unpackOneDouble($v)" //                     castAggrSingleSampleDouble
//    case 174 => q"unpackOneBoolean($v)" //                    castAggrSingleSampleBoolean
//    case 175 => q"unpackOneDate($v)" //                       castAggrSingleSampleDate
//    case 176 => q"unpackOneUUID($v)" //                       castAggrSingleSampleUUID
//    case 177 => q"unpackOneURI($v)" //                        castAggrSingleSampleURI
//    case 178 => q"unpackOneBigInt($v)" //                     castAggrSingleSampleBigInt
//    case 179 => q"unpackOneBigDecimal($v)" //                 castAggrSingleSampleBigDecimal
//
//    case 180 => q"unpackOneString($v, vs)" //                 castAggrOneSingleString
//    case 181 => q"unpackOneInt($v)" //                        castAggrOneSingleInt
//    case 182 => q"unpackOneLong($v)" //                       castAggrOneSingleLong
//    case 183 => q"unpackOneDouble($v)" //                     castAggrOneSingleDouble
//    case 184 => q"unpackOneBoolean($v)" //                    castAggrOneSingleBoolean
//    case 185 => q"unpackOneDate($v)" //                       castAggrOneSingleDate
//    case 186 => q"unpackOneUUID($v)" //                       castAggrOneSingleUUID
//    case 187 => q"unpackOneURI($v)" //                        castAggrOneSingleURI
//    case 188 => q"unpackOneBigInt($v)" //                     castAggrOneSingleBigInt
//    case 189 => q"unpackOneBigDecimal($v)" //                 castAggrOneSingleBigDecimal
//
//    case 190 => q"unpackSetOneString($v, vs)" //              castAggrManySingleString
//    case 191 => q"unpackSetOneInt($v)" //                     castAggrManySingleInt
//    case 192 => q"unpackSetOneLong($v)" //                    castAggrManySingleLong
//    case 193 => q"unpackSetOneDouble($v)" //                  castAggrManySingleDouble
//    case 194 => q"unpackSetOneBoolean($v)" //                 castAggrManySingleBoolean
//    case 195 => q"unpackSetOneDate($v)" //                    castAggrManySingleDate
//    case 196 => q"unpackSetOneUUID($v)" //                    castAggrManySingleUUID
//    case 197 => q"unpackSetOneURI($v)" //                     castAggrManySingleURI
//    case 198 => q"unpackSetOneBigInt($v)" //                  castAggrManySingleBigInt
//    case 199 => q"unpackSetOneBigDecimal($v)" //              castAggrManySingleBigDecimal
//  }
}
