package molecule.core.marshalling.unpack

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

trait Unpackers extends TreeOps with UnpackTypes {
  val c: blackbox.Context

  import c.universe._

  def unpackLambdas(v: Tree): Int => Tree = {
    case 0  => q"unpackOneString($v, vs)" //                   castOneString
    case 1  => q"unpackOneInt($v)" //                          castOneInt
    case 2  => q"unpackOneInt($v)" // (slot for int2 cast )    castOneInt
    case 3  => q"unpackOneLong($v)" //                         castOneLong
    case 4  => q"unpackOneDouble($v)" //                       castOneDouble
    case 5  => q"unpackOneBoolean($v)" //                      castOneBoolean
    case 6  => q"unpackOneDate($v)" //                         castOneDate
    case 7  => q"unpackOneUUID($v)" //                         castOneUUID
    case 8  => q"unpackOneURI($v)" //                          castOneURI
    case 9  => q"unpackOneBigInt($v)" //                       castOneBigInt
    case 10 => q"unpackOneBigDecimal($v)" //                   castOneBigDecimal
    case 11 => q"unpackOneAny($v)" //                          castOneAny

    case 12 => q"unpackOptOneEnum($v)" //                      castOptOneEnum
    case 13 => q"unpackOptOneString($v, vs)" //                castOptOneString
    case 14 => q"unpackOptOneInt($v)" //                       castOptOneInt
    case 15 => q"unpackOptOneLong($v)" //                      castOptOneLong
    case 16 => q"unpackOptOneDouble($v)" //                    castOptOneDouble
    case 17 => q"unpackOptOneBoolean($v)" //                   castOptOneBoolean
    case 18 => q"unpackOptOneDate($v)" //                      castOptOneDate
    case 19 => q"unpackOptOneUUID($v)" //                      castOptOneUUID
    case 20 => q"unpackOptOneURI($v)" //                       castOptOneURI
    case 21 => q"unpackOptOneBigInt($v)" //                    castOptOneBigInt
    case 22 => q"unpackOptOneBigDecimal($v)" //                castOptOneBigDecimal
    case 23 => q"unpackOptOneLong($v)" //                      castOptOneRefAttr

    case 24 => q"unpackManyString($v, vs)" //                  castManyEnum
    case 25 => q"unpackManyString($v, vs)" //                  castManyString
    case 26 => q"unpackManyInt($v, vs)" //                     castManyInt
    case 27 => q"unpackManyLong($v, vs)" //                    castManyLong
    case 28 => q"unpackManyDouble($v, vs)" //                  castManyDouble
    case 29 => q"unpackManyBoolean($v, vs)" //                 castManyBoolean
    case 30 => q"unpackManyDate($v, vs)" //                    castManyDate
    case 31 => q"unpackManyUUID($v, vs)" //                    castManyUUID
    case 32 => q"unpackManyURI($v, vs)" //                     castManyURI
    case 33 => q"unpackManyBigInt($v, vs)" //                  castManyBigInt
    case 34 => q"unpackManyBigDecimal($v, vs)" //              castManyBigDecimal

    case 35 => q"unpackOptManyString($v)" //                   castOptManyEnum
    case 36 => q"unpackOptManyString($v, vs)" //               castOptManyString
    case 37 => q"unpackOptManyInt($v)" //                      castOptManyInt
    case 38 => q"unpackOptManyLong($v)" //                     castOptManyLong
    case 39 => q"unpackOptManyDouble($v)" //                   castOptManyDouble
    case 40 => q"unpackOptManyBoolean($v)" //                  castOptManyBoolean
    case 41 => q"unpackOptManyDate($v)" //                     castOptManyDate
    case 42 => q"unpackOptManyUUID($v)" //                     castOptManyUUID
    case 43 => q"unpackOptManyURI($v)" //                      castOptManyURI
    case 44 => q"unpackOptManyBigInt($v)" //                   castOptManyBigInt
    case 45 => q"unpackOptManyBigDecimal($v)" //               castOptManyBigDecimal
    case 46 => q"unpackOptManyRefAttr($v)" //                  castOptManyRefAttr

    case 47 => q"unpackMapString($v, vs)" //                   castMapString
    case 48 => q"unpackMapInt($v)" //                          castMapInt
    case 49 => q"unpackMapLong($v)" //                         castMapLong
    case 50 => q"unpackMapDouble($v)" //                       castMapDouble
    case 51 => q"unpackMapBoolean($v)" //                      castMapBoolean
    case 52 => q"unpackMapDate($v)" //                         castMapDate
    case 53 => q"unpackMapUUID($v)" //                         castMapUUID
    case 54 => q"unpackMapURI($v)" //                          castMapURI
    case 55 => q"unpackMapBigInt($v)" //                       castMapBigInt
    case 56 => q"unpackMapBigDecimal($v)" //                   castMapBigDecimal

    case 57 => q"unpackOptMapString($v, vs)" //                castOptMapString
    case 58 => q"unpackOptMapInt($v)" //                       castOptMapInt
    case 59 => q"unpackOptMapLong($v)" //                      castOptMapLong
    case 60 => q"unpackOptMapDouble($v)" //                    castOptMapDouble
    case 61 => q"unpackOptMapBoolean($v)" //                   castOptMapBoolean
    case 62 => q"unpackOptMapDate($v)" //                      castOptMapDate
    case 63 => q"unpackOptMapUUID($v)" //                      castOptMapUUID
    case 64 => q"unpackOptMapURI($v)" //                       castOptMapURI
    case 65 => q"unpackOptMapBigInt($v)" //                    castOptMapBigInt
    case 66 => q"unpackOptMapBigDecimal($v)" //                castOptMapBigDecimal

    case 67 => q"unpackOptOneString($v, vs)" //           castOptApplyOneString
    case 68 => q"unpackOptOneInt($v)" //                  castOptApplyOneInt
    case 69 => q"unpackOptOneLong($v)" //                 castOptApplyOneLong
    case 70 => q"unpackOptOneDouble($v)" //               castOptApplyOneDouble
    case 71 => q"unpackOptOneBoolean($v)" //              castOptApplyOneBoolean
    case 72 => q"unpackOptOneDate($v)" //                 castOptApplyOneDate
    case 73 => q"unpackOptOneUUID($v)" //                 castOptApplyOneUUID
    case 74 => q"unpackOptOneURI($v)" //                  castOptApplyOneURI
    case 75 => q"unpackOptOneBigInt($v)" //               castOptApplyOneBigInt
    case 76 => q"unpackOptOneBigDecimal($v)" //           castOptApplyOneBigDecimal

    case 77 => q"unpackOptManyString($v, vs)" //          castOptApplyManyString
    case 78 => q"unpackOptManyInt($v)" //                 castOptApplyManyInt
    case 79 => q"unpackOptManyLong($v)" //                castOptApplyManyLong
    case 80 => q"unpackOptManyDouble($v)" //              castOptApplyManyDouble
    case 81 => q"unpackOptManyBoolean($v)" //             castOptApplyManyBoolean
    case 82 => q"unpackOptManyDate($v)" //                castOptApplyManyDate
    case 83 => q"unpackOptManyUUID($v)" //                castOptApplyManyUUID
    case 84 => q"unpackOptManyURI($v)" //                 castOptApplyManyURI
    case 85 => q"unpackOptManyBigInt($v)" //              castOptApplyManyBigInt
    case 86 => q"unpackOptManyBigDecimal($v)" //          castOptApplyManyBigDecimal

    case 87 => q"unpackOptMapString($v, vs)" //           castOptApplyMapString
    case 88 => q"unpackOptMapInt($v)" //                  castOptApplyMapInt
    case 89 => q"unpackOptMapLong($v)" //                 castOptApplyMapLong
    case 90 => q"unpackOptMapDouble($v)" //               castOptApplyMapDouble
    case 91 => q"unpackOptMapBoolean($v)" //              castOptApplyMapBoolean
    case 92 => q"unpackOptMapDate($v)" //                 castOptApplyMapDate
    case 93 => q"unpackOptMapUUID($v)" //                 castOptApplyMapUUID
    case 94 => q"unpackOptMapURI($v)" //                  castOptApplyMapURI
    case 95 => q"unpackOptMapBigInt($v)" //               castOptApplyMapBigInt
    case 96 => q"unpackOptMapBigDecimal($v)" //           castOptApplyMapBigDecimal

    case 97  => q"unpackString($v, vs)" //              castKeyedMapString
    case 98  => q"unpackInt($v)" //                     castKeyedMapInt
    case 99  => q"unpackLong($v)" //                    castKeyedMapLong
    case 100 => q"unpackDouble($v)" //                  castKeyedMapDouble
    case 101 => q"unpackBoolean($v)" //                 castKeyedMapBoolean
    case 102 => q"unpackDate($v)" //                    castKeyedMapDate
    case 103 => q"unpackUUID($v)" //                    castKeyedMapUUID
    case 104 => q"unpackURI($v)" //                     castKeyedMapURI
    case 105 => q"unpackBigInt($v)" //                  castKeyedMapBigInt
    case 106 => q"unpackBigDecimal($v)" //              castKeyedMapBigDecimal
    case 107 => q"unpackAny($v)" //                     castKeyedMapAny

    case 108 => q"unpackInt($v)" //                         castAggrInt
    case 109 => q"unpackDouble($v)" //                      castAggrDouble

    case 110 => q"unpackListString($v, vs)" //           castAggrOneListString
    case 111 => q"unpackListInt($v)" //                  castAggrOneListInt
    case 112 => q"unpackListLong($v)" //                 castAggrOneListLong
    case 113 => q"unpackListDouble($v)" //               castAggrOneListDouble
    case 114 => q"unpackListBoolean($v)" //              castAggrOneListBoolean
    case 115 => q"unpackListDate($v)" //                 castAggrOneListDate
    case 116 => q"unpackListUUID($v)" //                 castAggrOneListUUID
    case 117 => q"unpackListURI($v)" //                  castAggrOneListURI
    case 118 => q"unpackListBigInt($v)" //               castAggrOneListBigInt
    case 119 => q"unpackListBigDecimal($v)" //           castAggrOneListBigDecimal

    case 120 => q"unpackListSetString($v, vs)" //          castAggrManyListString
    case 121 => q"unpackListSetInt($v)" //                 castAggrManyListInt
    case 122 => q"unpackListSetLong($v)" //                castAggrManyListLong
    case 123 => q"unpackListSetDouble($v)" //              castAggrManyListDouble
    case 124 => q"unpackListSetBoolean($v)" //             castAggrManyListBoolean
    case 125 => q"unpackListSetDate($v)" //                castAggrManyListDate
    case 126 => q"unpackListSetUUID($v)" //                castAggrManyListUUID
    case 127 => q"unpackListSetURI($v)" //                 castAggrManyListURI
    case 128 => q"unpackListSetBigInt($v)" //              castAggrManyListBigInt
    case 129 => q"unpackListSetBigDecimal($v)" //          castAggrManyListBigDecimal

    case 130 => q"unpackListString($v, vs)" //   castAggrOneListDistinctString
    case 131 => q"unpackListInt($v)" //          castAggrOneListDistinctInt
    case 132 => q"unpackListLong($v)" //         castAggrOneListDistinctLong
    case 133 => q"unpackListDouble($v)" //       castAggrOneListDistinctDouble
    case 134 => q"unpackListBoolean($v)" //      castAggrOneListDistinctBoolean
    case 135 => q"unpackListDate($v)" //         castAggrOneListDistinctDate
    case 136 => q"unpackListUUID($v)" //         castAggrOneListDistinctUUID
    case 137 => q"unpackListURI($v)" //          castAggrOneListDistinctURI
    case 138 => q"unpackListBigInt($v)" //       castAggrOneListDistinctBigInt
    case 139 => q"unpackListBigDecimal($v)" //   castAggrOneListDistinctBigDecimal

    case 140 => q"unpackListSetString($v, vs)" //  castAggrManyListDistinctString
    case 141 => q"unpackListSetInt($v)" //         castAggrManyListDistinctInt
    case 142 => q"unpackListSetLong($v)" //        castAggrManyListDistinctLong
    case 143 => q"unpackListSetDouble($v)" //      castAggrManyListDistinctDouble
    case 144 => q"unpackListSetBoolean($v)" //     castAggrManyListDistinctBoolean
    case 145 => q"unpackListSetDate($v)" //        castAggrManyListDistinctDate
    case 146 => q"unpackListSetUUID($v)" //        castAggrManyListDistinctUUID
    case 147 => q"unpackListSetURI($v)" //         castAggrManyListDistinctURI
    case 148 => q"unpackListSetBigInt($v)" //      castAggrManyListDistinctBigInt
    case 149 => q"unpackListSetBigDecimal($v)" //  castAggrManyListDistinctBigDecimal

    case 150 => q"unpackListString($v, vs)" //       castAggrOneListRandString
    case 151 => q"unpackListInt($v)" //              castAggrOneListRandInt
    case 152 => q"unpackListLong($v)" //             castAggrOneListRandLong
    case 153 => q"unpackListDouble($v)" //           castAggrOneListRandDouble
    case 154 => q"unpackListBoolean($v)" //          castAggrOneListRandBoolean
    case 155 => q"unpackListDate($v)" //             castAggrOneListRandDate
    case 156 => q"unpackListUUID($v)" //             castAggrOneListRandUUID
    case 157 => q"unpackListURI($v)" //              castAggrOneListRandURI
    case 158 => q"unpackListBigInt($v)" //           castAggrOneListRandBigInt
    case 159 => q"unpackListBigDecimal($v)" //       castAggrOneListRandBigDecimal

    case 160 => q"unpackListSetString($v, vs)" //      castAggrManyListRandString
    case 161 => q"unpackListSetInt($v)" //             castAggrManyListRandInt
    case 162 => q"unpackListSetLong($v)" //            castAggrManyListRandLong
    case 163 => q"unpackListSetDouble($v)" //          castAggrManyListRandDouble
    case 164 => q"unpackListSetBoolean($v)" //         castAggrManyListRandBoolean
    case 165 => q"unpackListSetDate($v)" //            castAggrManyListRandDate
    case 166 => q"unpackListSetUUID($v)" //            castAggrManyListRandUUID
    case 167 => q"unpackListSetURI($v)" //             castAggrManyListRandURI
    case 168 => q"unpackListSetBigInt($v)" //          castAggrManyListRandBigInt
    case 169 => q"unpackListSetBigDecimal($v)" //      castAggrManyListRandBigDecimal

    case 170 => q"unpackOneString($v, vs)" //      castAggrSingleSampleString
    case 171 => q"unpackOneInt($v)" //             castAggrSingleSampleInt
    case 172 => q"unpackOneLong($v)" //            castAggrSingleSampleLong
    case 173 => q"unpackOneDouble($v)" //          castAggrSingleSampleDouble
    case 174 => q"unpackOneBoolean($v)" //         castAggrSingleSampleBoolean
    case 175 => q"unpackOneDate($v)" //            castAggrSingleSampleDate
    case 176 => q"unpackOneUUID($v)" //            castAggrSingleSampleUUID
    case 177 => q"unpackOneURI($v)" //             castAggrSingleSampleURI
    case 178 => q"unpackOneBigInt($v)" //          castAggrSingleSampleBigInt
    case 179 => q"unpackOneBigDecimal($v)" //      castAggrSingleSampleBigDecimal

    case 180 => q"unpackOneString($v, vs)" //         castAggrOneSingleString
    case 181 => q"unpackOneInt($v)" //                castAggrOneSingleInt
    case 182 => q"unpackOneLong($v)" //               castAggrOneSingleLong
    case 183 => q"unpackOneDouble($v)" //             castAggrOneSingleDouble
    case 184 => q"unpackOneBoolean($v)" //            castAggrOneSingleBoolean
    case 185 => q"unpackOneDate($v)" //               castAggrOneSingleDate
    case 186 => q"unpackOneUUID($v)" //               castAggrOneSingleUUID
    case 187 => q"unpackOneURI($v)" //                castAggrOneSingleURI
    case 188 => q"unpackOneBigInt($v)" //             castAggrOneSingleBigInt
    case 189 => q"unpackOneBigDecimal($v)" //         castAggrOneSingleBigDecimal

    case 190 => q"unpackSetOneString($v, vs)" //        castAggrManySingleString
    case 191 => q"unpackSetOneInt($v)" //               castAggrManySingleInt
    case 192 => q"unpackSetOneLong($v)" //              castAggrManySingleLong
    case 193 => q"unpackSetOneDouble($v)" //            castAggrManySingleDouble
    case 194 => q"unpackSetOneBoolean($v)" //           castAggrManySingleBoolean
    case 195 => q"unpackSetOneDate($v)" //              castAggrManySingleDate
    case 196 => q"unpackSetOneUUID($v)" //              castAggrManySingleUUID
    case 197 => q"unpackSetOneURI($v)" //               castAggrManySingleURI
    case 198 => q"unpackSetOneBigInt($v)" //            castAggrManySingleBigInt
    case 199 => q"unpackSetOneBigDecimal($v)" //        castAggrManySingleBigDecimal
  }
}
