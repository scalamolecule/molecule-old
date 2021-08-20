package molecule.datomic.base.marshalling.nestedOpt

import java.util.{Iterator => jIterator}


trait PackLambdas extends PackTypes with PackAggr {

  def packAttr(lambdaIndex: Int): jIterator[_] => Unit = lambdaIndex match {
    case 0   => packOneString //                  castOneString
    case 1   => packOne //                        castOneInt
    case 2   => packOne //                        castOneInt
    case 3   => packOne //                        castOneLong
    case 4   => packOne //                        castOneDouble
    case 5   => packOne //                        castOneBoolean
    case 6   => packOneDate //                    castOneDate
    case 7   => packOne //                        castOneUUID
    case 8   => packOne //                        castOneURI
    case 9   => packOne //                        castOneBigInt
    case 10  => packOne //                        castOneBigDecimal
    case 11  => packOneAny //                     castOneAny

    case 12  => packOptOneEnum //                 castOptOneEnum
    case 13  => packOptOneString //               castOptOneString
    case 14  => packOptOne //                     castOptOneInt
    case 15  => packOptOne //                     castOptOneLong
    case 16  => packOptOne //                     castOptOneDouble
    case 17  => packOptOne //                     castOptOneBoolean
    case 18  => packOptOneDate //                 castOptOneDate
    case 19  => packOptOne //                     castOptOneUUID
    case 20  => packOptOne //                     castOptOneURI
    case 21  => packOptOne //                     castOptOneBigInt
    case 22  => packOptOne //                     castOptOneBigDecimal
    case 23  => packOptOneRefAttr //              castOptOneRefAttr

    case 24  => packManyEnum //                   castManyEnum
    case 25  => packManyString //                 castManyString
    case 26  => packMany //                       castManyInt
    case 27  => packMany //                       castManyLong
    case 28  => packMany //                       castManyDouble
    case 29  => packMany //                       castManyBoolean
    case 30  => packManyDate //                   castManyDate
    case 31  => packMany //                       castManyUUID
    case 32  => packMany //                       castManyURI
    case 33  => packMany //                       castManyBigInt
    case 34  => packMany //                       castManyBigDecimal

    case 35  => packOptManyEnum //                castOptManyEnum
    case 36  => packOptManyString //              castOptManyString
    case 37  => packOptMany //                    castOptManyInt
    case 38  => packOptMany //                    castOptManyLong
    case 39  => packOptMany //                    castOptManyDouble
    case 40  => packOptMany //                    castOptManyBoolean
    case 41  => packOptManyDate //                castOptManyDate
    case 42  => packOptMany //                    castOptManyUUID
    case 43  => packOptMany //                    castOptManyURI
    case 44  => packOptMany //                    castOptManyBigInt
    case 45  => packOptMany //                    castOptManyBigDecimal
    case 46  => packOptManyRefAttr //             castOptManyRefAttr

    case 47  => packMapString //                  castMapString
    case 48  => packMap //                        castMapInt
    case 49  => packMap //                        castMapLong
    case 50  => packMap //                        castMapDouble
    case 51  => packMap //                        castMapBoolean
    case 52  => packMap //                        castMapDate
    case 53  => packMap //                        castMapUUID
    case 54  => packMap //                        castMapURI
    case 55  => packMap //                        castMapBigInt
    case 56  => packMap //                        castMapBigDecimal

    case 57  => packOptMapString //               castOptMapString
    case 58  => packOptMap //                     castOptMapInt
    case 59  => packOptMap //                     castOptMapLong
    case 60  => packOptMap //                     castOptMapDouble
    case 61  => packOptMap //                     castOptMapBoolean
    case 62  => packOptMap //                     castOptMapDate
    case 63  => packOptMap //                     castOptMapUUID
    case 64  => packOptMap //                     castOptMapURI
    case 65  => packOptMap //                     castOptMapBigInt
    case 66  => packOptMap //                     castOptMapBigDecimal

    case 67  => packOptApplyOneString //          castOptApplyOneString
    case 68  => packOptApplyOne //                castOptApplyOneInt
    case 69  => packOptApplyOne //                castOptApplyOneLong
    case 70  => packOptApplyOne //                castOptApplyOneDouble
    case 71  => packOptApplyOne //                castOptApplyOneBoolean
    case 72  => packOptApplyOneDate //            castOptApplyOneDate
    case 73  => packOptApplyOne //                castOptApplyOneUUID
    case 74  => packOptApplyOne //                castOptApplyOneURI
    case 75  => packOptApplyOne //                castOptApplyOneBigInt
    case 76  => packOptApplyOne //                castOptApplyOneBigDecimal

    case 77  => packOptApplyManyString //         castOptApplyManyString
    case 78  => packOptApplyMany //               castOptApplyManyInt
    case 79  => packOptApplyMany //               castOptApplyManyLong
    case 80  => packOptApplyMany //               castOptApplyManyDouble
    case 81  => packOptApplyMany //               castOptApplyManyBoolean
    case 82  => packOptApplyManyDate //           castOptApplyManyDate
    case 83  => packOptApplyMany //               castOptApplyManyUUID
    case 84  => packOptApplyMany //               castOptApplyManyURI
    case 85  => packOptApplyMany //               castOptApplyManyBigInt
    case 86  => packOptApplyMany //               castOptApplyManyBigDecimal

    case 87  => packOptApplyMapString //          castOptApplyMapString
    case 88  => packOptApplyMap //                castOptApplyMapInt
    case 89  => packOptApplyMap //                castOptApplyMapLong
    case 90  => packOptApplyMap //                castOptApplyMapDouble
    case 91  => packOptApplyMap //                castOptApplyMapBoolean
    case 92  => packOptApplyMapDate //            castOptApplyMapDate
    case 93  => packOptApplyMap //                castOptApplyMapUUID
    case 94  => packOptApplyMap //                castOptApplyMapURI
    case 95  => packOptApplyMap //                castOptApplyMapBigInt
    case 96  => packOptApplyMap //                castOptApplyMapBigDecimal

    case 97  => packKeyedMapString //             castKeyedMapString
    case 98  => packKeyedMap //                   castKeyedMapInt
    case 99  => packKeyedMap //                   castKeyedMapLong
    case 100 => packKeyedMap //                   castKeyedMapDouble
    case 101 => packKeyedMap //                   castKeyedMapBoolean
    case 102 => packKeyedMapDate //               castKeyedMapDate
    case 103 => packKeyedMap //                   castKeyedMapUUID
    case 104 => packKeyedMap //                   castKeyedMapURI
    case 105 => packKeyedMap //                   castKeyedMapBigInt
    case 106 => packKeyedMap //                   castKeyedMapBigDecimal
    case 107 => packKeyedMapAny //                castKeyedMapAny

    case 108 => packAggrInt //                    castAggrInt
    case 109 => packAggrDouble //                 castAggrDouble

    case 110 => packAggrOneListString //          castAggrOneListString
    case 111 => packAggrOneList //                castAggrOneListInt
    case 112 => packAggrOneList //                castAggrOneListLong
    case 113 => packAggrOneList //                castAggrOneListDouble
    case 114 => packAggrOneList //                castAggrOneListBoolean
    case 115 => packAggrOneListDate //            castAggrOneListDate
    case 116 => packAggrOneList //                castAggrOneListUUID
    case 117 => packAggrOneList //                castAggrOneListURI
    case 118 => packAggrOneList //                castAggrOneListBigInt
    case 119 => packAggrOneList //                castAggrOneListBigDecimal

    case 120 => packAggrManyListString //         castAggrManyListString
    case 121 => packAggrManyList //               castAggrManyListInt
    case 122 => packAggrManyList //               castAggrManyListLong
    case 123 => packAggrManyList //               castAggrManyListDouble
    case 124 => packAggrManyList //               castAggrManyListBoolean
    case 125 => packAggrManyListDate //           castAggrManyListDate
    case 126 => packAggrManyList //               castAggrManyListUUID
    case 127 => packAggrManyList //               castAggrManyListURI
    case 128 => packAggrManyList //               castAggrManyListBigInt
    case 129 => packAggrManyList //               castAggrManyListBigDecimal

    case 130 => packAggrOneListDistinctString //  castAggrOneListDistinctString
    case 131 => packAggrOneListDistinct //        castAggrOneListDistinctInt
    case 132 => packAggrOneListDistinct //        castAggrOneListDistinctLong
    case 133 => packAggrOneListDistinct //        castAggrOneListDistinctDouble
    case 134 => packAggrOneListDistinct //        castAggrOneListDistinctBoolean
    case 135 => packAggrOneListDistinctDate //    castAggrOneListDistinctDate
    case 136 => packAggrOneListDistinct //        castAggrOneListDistinctUUID
    case 137 => packAggrOneListDistinct //        castAggrOneListDistinctURI
    case 138 => packAggrOneListDistinct //        castAggrOneListDistinctBigInt
    case 139 => packAggrOneListDistinct //        castAggrOneListDistinctBigDecimal

    case 140 => packAggrManyListDistinctString // castAggrManyListDistinctString
    case 141 => packAggrManyListDistinct //       castAggrManyListDistinctInt
    case 142 => packAggrManyListDistinct //       castAggrManyListDistinctLong
    case 143 => packAggrManyListDistinct //       castAggrManyListDistinctDouble
    case 144 => packAggrManyListDistinct //       castAggrManyListDistinctBoolean
    case 145 => packAggrManyListDistinctDate //   castAggrManyListDistinctDate
    case 146 => packAggrManyListDistinct //       castAggrManyListDistinctUUID
    case 147 => packAggrManyListDistinct //       castAggrManyListDistinctURI
    case 148 => packAggrManyListDistinct //       castAggrManyListDistinctBigInt
    case 149 => packAggrManyListDistinct //       castAggrManyListDistinctBigDecimal

    case 150 => packAggrOneListRandString //      castAggrOneListRandString
    case 151 => packAggrOneListRand //            castAggrOneListRandInt
    case 152 => packAggrOneListRand //            castAggrOneListRandLong
    case 153 => packAggrOneListRand //            castAggrOneListRandDouble
    case 154 => packAggrOneListRand //            castAggrOneListRandBoolean
    case 155 => packAggrOneListRandDate //        castAggrOneListRandDate
    case 156 => packAggrOneListRand //            castAggrOneListRandUUID
    case 157 => packAggrOneListRand //            castAggrOneListRandURI
    case 158 => packAggrOneListRand //            castAggrOneListRandBigInt
    case 159 => packAggrOneListRand //            castAggrOneListRandBigDecimal

    case 160 => packAggrManyListRandString //     castAggrManyListRandString
    case 161 => packAggrManyListRand //           castAggrManyListRandInt
    case 162 => packAggrManyListRand //           castAggrManyListRandLong
    case 163 => packAggrManyListRand //           castAggrManyListRandDouble
    case 164 => packAggrManyListRand //           castAggrManyListRandBoolean
    case 165 => packAggrManyListRandDate //       castAggrManyListRandDate
    case 166 => packAggrManyListRand //           castAggrManyListRandUUID
    case 167 => packAggrManyListRand //           castAggrManyListRandURI
    case 168 => packAggrManyListRand //           castAggrManyListRandBigInt
    case 169 => packAggrManyListRand //           castAggrManyListRandBigDecimal

    case 170 => packAggrSingleSampleString //     castAggrSingleSampleString
    case 171 => packAggrSingleSample //           castAggrSingleSampleInt
    case 172 => packAggrSingleSample //           castAggrSingleSampleLong
    case 173 => packAggrSingleSample //           castAggrSingleSampleDouble
    case 174 => packAggrSingleSample //           castAggrSingleSampleBoolean
    case 175 => packAggrSingleSampleDate //       castAggrSingleSampleDate
    case 176 => packAggrSingleSample //           castAggrSingleSampleUUID
    case 177 => packAggrSingleSample //           castAggrSingleSampleURI
    case 178 => packAggrSingleSample //           castAggrSingleSampleBigInt
    case 179 => packAggrSingleSample //           castAggrSingleSampleBigDecimal

    case 180 => packAggrOneSingleString //        castAggrOneSingleString
    case 181 => packAggrOneSingle //              castAggrOneSingleInt
    case 182 => packAggrOneSingle //              castAggrOneSingleLong
    case 183 => packAggrOneSingle //              castAggrOneSingleDouble
    case 184 => packAggrOneSingle //              castAggrOneSingleBoolean
    case 185 => packAggrOneSingleDate //          castAggrOneSingleDate
    case 186 => packAggrOneSingle //              castAggrOneSingleUUID
    case 187 => packAggrOneSingle //              castAggrOneSingleURI
    case 188 => packAggrOneSingle //              castAggrOneSingleBigInt
    case 189 => packAggrOneSingle //              castAggrOneSingleBigDecimal

    case 190 => packAggrManySingleString //       castAggrManySingleString
    case 191 => packAggrManySingle //             castAggrManySingleInt
    case 192 => packAggrManySingle //             castAggrManySingleLong
    case 193 => packAggrManySingle //             castAggrManySingleDouble
    case 194 => packAggrManySingle //             castAggrManySingleBoolean
    case 195 => packAggrManySingleDate //         castAggrManySingleDate
    case 196 => packAggrManySingle //             castAggrManySingleUUID
    case 197 => packAggrManySingle //             castAggrManySingleURI
    case 198 => packAggrManySingle //             castAggrManySingleBigInt
    case 199 => packAggrManySingle //             castAggrManySingleBigDecimal
  }
}
