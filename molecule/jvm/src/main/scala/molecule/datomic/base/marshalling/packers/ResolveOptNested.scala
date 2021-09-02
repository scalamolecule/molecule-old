package molecule.datomic.base.marshalling.packers

import java.util.{Iterator => jIterator}


trait ResolveOptNested extends PackOptNestedTypes with PackOptNestedAggr {

  def packOptNestedAttr2(group: String, baseTpe: String): jIterator[_] => Unit = group match {
    case "One"                  => packOneAttr(baseTpe)
    case "OptOne"               => packOptOneAttr(baseTpe)
    case "Many"                 => packManyAttr(baseTpe)
    case "OptMany"              => packOptManyAttr(baseTpe)
    case "Map"                  => packMapAttr(baseTpe)
    case "OptMap"               => packOptMapAttr(baseTpe)
    case "OptApplyOne"          => packOptApplyOneAtt(baseTpe)
    case "OptApplyMany"         => packOptApplyManyAtt(baseTpe)
    case "OptApplyMap"          => packOptApplyMapAttr(baseTpe)
    case "KeyedMap"             => packKeyedMapAtt(baseTpe)
    case "AggrOneList"          => packAggrOneList(baseTpe)
    case "AggrManyList"         => packAggrManyList(baseTpe)
    case "AggrOneListDistinct"  => packAggrOneListDistinct(baseTpe)
    case "AggrManyListDistinct" => packAggrManyListDistinct(baseTpe)
    case "AggrOneListRand"      => packAggrOneListRand(baseTpe)
    case "AggrManyListRand"     => packAggrManyListRand(baseTpe)
    case "AggrSingleSample"     => packAggrSingleSample(baseTpe)
    case "AggrOneSingle"        => packAggrOneSingle(baseTpe)
    case "AggrManySingle"       => packAggrManySingle(baseTpe)
  }

  def packOneAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOneString
    case "Date"   => packOptNestedOneDate
    case "Any"    => packOptNestedOneAny
    case _        => packOptNestedOne_
  }

  def packOptOneAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptOneString
    case "Date"   => packOptNestedOptOneDate
    case "enum"   => packOptNestedOptOneEnum
    case "ref"    => packOptNestedOptOneRefAttr
    case _        => packOptNestedOptOne_
  }

  def packManyAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedManyString
    case "Date"   => packOptNestedManyDate
    case "enum"   => packOptNestedManyEnum
    case _        => packOptNestedMany_
  }

  def packOptManyAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptManyString
    case "Date"   => packOptNestedOptManyDate
    case "enum"   => packOptNestedOptManyEnum
    case "ref"    => packOptNestedOptManyRefAttr
    case _        => packOptNestedOptMany_
  }

  def packMapAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedMapString
    case _        => packOptNestedMap_
  }

  def packOptMapAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptMapString
    case _        => packOptMap_
  }

  def packOptApplyOneAtt(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptApplyOneString
    case "Date"   => packOptNestedOptApplyOneDate
    case _        => packOptNestedOptApplyOne_
  }

  def packOptApplyManyAtt(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptApplyManyString
    case "Date"   => packOptNestedOptApplyManyDate
    case _        => packOptNestedOptApplyMany_
  }

  def packOptApplyMapAttr(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedOptApplyMapString
    case "Date"   => packOptNestedOptApplyMapDate
    case _        => packOptNestedOptApplyMap_
  }

  def packKeyedMapAtt(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedKeyedMapString
    case "Date"   => packOptNestedKeyedMapDate
    case _        => packOptNestedKeyedMap_
  }

  def packAggrOneList(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrOneListString
    case "Date"   => packOptNestedAggrOneListDate
    case _        => packOptNestedAggrOneList_
  }

  def packAggrManyList(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrManyListString
    case "Date"   => packOptNestedAggrManyListDate
    case _        => packOptNestedAggrManyList_
  }

  def packAggrOneListDistinct(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrOneListDistinctString
    case "Date"   => packOptNestedAggrOneListDistinctDate
    case _        => packOptNestedAggrOneListDistinct_
  }

  def packAggrManyListDistinct(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrManyListDistinctString
    case "Date"   => packOptNestedAggrManyListDistinctDate
    case _        => packOptNestedAggrManyListDistinct_
  }

  def packAggrOneListRand(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrOneListRandString
    case "Date"   => packOptNestedAggrOneListRandDate
    case _        => packOptNestedAggrOneListRand_
  }

  def packAggrManyListRand(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrManyListRandString
    case "Date"   => packOptNestedAggrManyListRandDate
    case _        => packOptNestedAggrManyListRand_
  }

  def packAggrSingleSample(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrSingleSampleString
    case "Date"   => packOptNestedAggrSingleSampleDate
    case _        => packOptNestedAggrSingleSample_
  }

  def packAggrOneSingle(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrOneSingleString
    case "Date"   => packOptNestedAggrOneSingleDate
    case _        => packOptNestedAggrOneSingle_
  }

  def packAggrManySingle(baseTpe: String): jIterator[_] => Unit = baseTpe match {
    case "String" => packOptNestedAggrManySingleString
    case "Date"   => packOptNestedAggrManySingleDate
    case _        => packOptNestedAggrManySingle_
  }

  def packOptNestedAttr(lambdaIndex: Int): jIterator[_] => Unit = lambdaIndex match {
    case 1  => packOptNestedOne_ //                        castOneInt
    case 0  => packOptNestedOneString //                  castOneString
    case 2  => packOptNestedOne_ //                        castOneInt
    case 3  => packOptNestedOne_ //                        castOneLong
    case 4  => packOptNestedOne_ //                        castOneDouble
    case 5  => packOptNestedOne_ //                        castOneBoolean
    case 6  => packOptNestedOneDate //                    castOneDate
    case 7  => packOptNestedOne_ //                        castOneUUID
    case 8  => packOptNestedOne_ //                        castOneURI
    case 9  => packOptNestedOne_ //                        castOneBigInt
    case 10 => packOptNestedOne_ //                        castOneBigDecimal
    case 11 => packOptNestedOneAny //                     castOneAny

    case 12 => packOptNestedOptOneEnum //                 castOptOneEnum
    case 13 => packOptNestedOptOneString //               castOptOneString
    case 14 => packOptNestedOptOne_ //                     castOptOneInt
    case 15 => packOptNestedOptOne_ //                     castOptOneLong
    case 16 => packOptNestedOptOne_ //                     castOptOneDouble
    case 17 => packOptNestedOptOne_ //                     castOptOneBoolean
    case 18 => packOptNestedOptOneDate //                 castOptOneDate
    case 19 => packOptNestedOptOne_ //                     castOptOneUUID
    case 20 => packOptNestedOptOne_ //                     castOptOneURI
    case 21 => packOptNestedOptOne_ //                     castOptOneBigInt
    case 22 => packOptNestedOptOne_ //                     castOptOneBigDecimal
    case 23 => packOptNestedOptOneRefAttr //              castOptOneRefAttr

    case 24 => packOptNestedManyEnum //                   castManyEnum
    case 25 => packOptNestedManyString //                 castManyString
    case 26 => packOptNestedMany_ //                       castManyInt
    case 27 => packOptNestedMany_ //                       castManyLong
    case 28 => packOptNestedMany_ //                       castManyDouble
    case 29 => packOptNestedMany_ //                       castManyBoolean
    case 30 => packOptNestedManyDate //                   castManyDate
    case 31 => packOptNestedMany_ //                       castManyUUID
    case 32 => packOptNestedMany_ //                       castManyURI
    case 33 => packOptNestedMany_ //                       castManyBigInt
    case 34 => packOptNestedMany_ //                       castManyBigDecimal

    case 35 => packOptNestedOptManyEnum //                castOptManyEnum
    case 36 => packOptNestedOptManyString //              castOptManyString
    case 37 => packOptNestedOptMany_ //                    castOptManyInt
    case 38 => packOptNestedOptMany_ //                    castOptManyLong
    case 39 => packOptNestedOptMany_ //                    castOptManyDouble
    case 40 => packOptNestedOptMany_ //                    castOptManyBoolean
    case 41 => packOptNestedOptManyDate //                castOptManyDate
    case 42 => packOptNestedOptMany_ //                    castOptManyUUID
    case 43 => packOptNestedOptMany_ //                    castOptManyURI
    case 44 => packOptNestedOptMany_ //                    castOptManyBigInt
    case 45 => packOptNestedOptMany_ //                    castOptManyBigDecimal
    case 46 => packOptNestedOptManyRefAttr //             castOptManyRefAttr

    case 47 => packOptNestedMapString //                  castMapString
    case 48 => packOptNestedMap_ //                        castMapInt
    case 49 => packOptNestedMap_ //                        castMapLong
    case 50 => packOptNestedMap_ //                        castMapDouble
    case 51 => packOptNestedMap_ //                        castMapBoolean
    case 52 => packOptNestedMap_ //                        castMapDate
    case 53 => packOptNestedMap_ //                        castMapUUID
    case 54 => packOptNestedMap_ //                        castMapURI
    case 55 => packOptNestedMap_ //                        castMapBigInt
    case 56 => packOptNestedMap_ //                        castMapBigDecimal

    case 57 => packOptNestedOptMapString //               castOptMapString
    case 58 => packOptMap_ //                     castOptMapInt
    case 59 => packOptMap_ //                     castOptMapLong
    case 60 => packOptMap_ //                     castOptMapDouble
    case 61 => packOptMap_ //                     castOptMapBoolean
    case 62 => packOptMap_ //                     castOptMapDate
    case 63 => packOptMap_ //                     castOptMapUUID
    case 64 => packOptMap_ //                     castOptMapURI
    case 65 => packOptMap_ //                     castOptMapBigInt
    case 66 => packOptMap_ //                     castOptMapBigDecimal

    case 67 => packOptNestedOptApplyOneString //          castOptApplyOneString
    case 68 => packOptNestedOptApplyOne_ //                castOptApplyOneInt
    case 69 => packOptNestedOptApplyOne_ //                castOptApplyOneLong
    case 70 => packOptNestedOptApplyOne_ //                castOptApplyOneDouble
    case 71 => packOptNestedOptApplyOne_ //                castOptApplyOneBoolean
    case 72 => packOptNestedOptApplyOneDate //            castOptApplyOneDate
    case 73 => packOptNestedOptApplyOne_ //                castOptApplyOneUUID
    case 74 => packOptNestedOptApplyOne_ //                castOptApplyOneURI
    case 75 => packOptNestedOptApplyOne_ //                castOptApplyOneBigInt
    case 76 => packOptNestedOptApplyOne_ //                castOptApplyOneBigDecimal

    case 77 => packOptNestedOptApplyManyString //         castOptApplyManyString
    case 78 => packOptNestedOptApplyMany_ //               castOptApplyManyInt
    case 79 => packOptNestedOptApplyMany_ //               castOptApplyManyLong
    case 80 => packOptNestedOptApplyMany_ //               castOptApplyManyDouble
    case 81 => packOptNestedOptApplyMany_ //               castOptApplyManyBoolean
    case 82 => packOptNestedOptApplyManyDate //           castOptApplyManyDate
    case 83 => packOptNestedOptApplyMany_ //               castOptApplyManyUUID
    case 84 => packOptNestedOptApplyMany_ //               castOptApplyManyURI
    case 85 => packOptNestedOptApplyMany_ //               castOptApplyManyBigInt
    case 86 => packOptNestedOptApplyMany_ //               castOptApplyManyBigDecimal

    case 87 => packOptNestedOptApplyMapString //          castOptApplyMapString
    case 88 => packOptNestedOptApplyMap_ //                castOptApplyMapInt
    case 89 => packOptNestedOptApplyMap_ //                castOptApplyMapLong
    case 90 => packOptNestedOptApplyMap_ //                castOptApplyMapDouble
    case 91 => packOptNestedOptApplyMap_ //                castOptApplyMapBoolean
    case 92 => packOptNestedOptApplyMapDate //            castOptApplyMapDate
    case 93 => packOptNestedOptApplyMap_ //                castOptApplyMapUUID
    case 94 => packOptNestedOptApplyMap_ //                castOptApplyMapURI
    case 95 => packOptNestedOptApplyMap_ //                castOptApplyMapBigInt
    case 96 => packOptNestedOptApplyMap_ //                castOptApplyMapBigDecimal

    case 97  => packOptNestedKeyedMapString //             castKeyedMapString
    case 98  => packOptNestedKeyedMap_ //                   castKeyedMapInt
    case 99  => packOptNestedKeyedMap_ //                   castKeyedMapLong
    case 100 => packOptNestedKeyedMap_ //                   castKeyedMapDouble
    case 101 => packOptNestedKeyedMap_ //                   castKeyedMapBoolean
    case 102 => packOptNestedKeyedMapDate //               castKeyedMapDate
    case 103 => packOptNestedKeyedMap_ //                   castKeyedMapUUID
    case 104 => packOptNestedKeyedMap_ //                   castKeyedMapURI
    case 105 => packOptNestedKeyedMap_ //                   castKeyedMapBigInt
    case 106 => packOptNestedKeyedMap_ //                   castKeyedMapBigDecimal
    case 107 => packKeyedMapAny //                castKeyedMapAny

    case 108 => packAggrInt //                    castAggrInt
    case 109 => packAggrDouble //                 castAggrDouble

    case 110 => packOptNestedAggrOneListString //          castAggrOneListString
    case 111 => packOptNestedAggrOneList_ //                castAggrOneListInt
    case 112 => packOptNestedAggrOneList_ //                castAggrOneListLong
    case 113 => packOptNestedAggrOneList_ //                castAggrOneListDouble
    case 114 => packOptNestedAggrOneList_ //                castAggrOneListBoolean
    case 115 => packOptNestedAggrOneListDate //            castAggrOneListDate
    case 116 => packOptNestedAggrOneList_ //                castAggrOneListUUID
    case 117 => packOptNestedAggrOneList_ //                castAggrOneListURI
    case 118 => packOptNestedAggrOneList_ //                castAggrOneListBigInt
    case 119 => packOptNestedAggrOneList_ //                castAggrOneListBigDecimal

    case 120 => packOptNestedAggrManyListString //         castAggrManyListString
    case 121 => packOptNestedAggrManyList_ //               castAggrManyListInt
    case 122 => packOptNestedAggrManyList_ //               castAggrManyListLong
    case 123 => packOptNestedAggrManyList_ //               castAggrManyListDouble
    case 124 => packOptNestedAggrManyList_ //               castAggrManyListBoolean
    case 125 => packOptNestedAggrManyListDate //           castAggrManyListDate
    case 126 => packOptNestedAggrManyList_ //               castAggrManyListUUID
    case 127 => packOptNestedAggrManyList_ //               castAggrManyListURI
    case 128 => packOptNestedAggrManyList_ //               castAggrManyListBigInt
    case 129 => packOptNestedAggrManyList_ //               castAggrManyListBigDecimal

    case 130 => packOptNestedAggrOneListDistinctString //  castAggrOneListDistinctString
    case 131 => packOptNestedAggrOneListDistinct_ //        castAggrOneListDistinctInt
    case 132 => packOptNestedAggrOneListDistinct_ //        castAggrOneListDistinctLong
    case 133 => packOptNestedAggrOneListDistinct_ //        castAggrOneListDistinctDouble
    case 134 => packOptNestedAggrOneListDistinct_ //        castAggrOneListDistinctBoolean
    case 135 => packOptNestedAggrOneListDistinctDate //    castAggrOneListDistinctDate
    case 136 => packOptNestedAggrOneListDistinct_ //        castAggrOneListDistinctUUID
    case 137 => packOptNestedAggrOneListDistinct_ //        castAggrOneListDistinctURI
    case 138 => packOptNestedAggrOneListDistinct_ //        castAggrOneListDistinctBigInt
    case 139 => packOptNestedAggrOneListDistinct_ //        castAggrOneListDistinctBigDecimal

    case 140 => packOptNestedAggrManyListDistinctString // castAggrManyListDistinctString
    case 141 => packOptNestedAggrManyListDistinct_ //       castAggrManyListDistinctInt
    case 142 => packOptNestedAggrManyListDistinct_ //       castAggrManyListDistinctLong
    case 143 => packOptNestedAggrManyListDistinct_ //       castAggrManyListDistinctDouble
    case 144 => packOptNestedAggrManyListDistinct_ //       castAggrManyListDistinctBoolean
    case 145 => packOptNestedAggrManyListDistinctDate //   castAggrManyListDistinctDate
    case 146 => packOptNestedAggrManyListDistinct_ //       castAggrManyListDistinctUUID
    case 147 => packOptNestedAggrManyListDistinct_ //       castAggrManyListDistinctURI
    case 148 => packOptNestedAggrManyListDistinct_ //       castAggrManyListDistinctBigInt
    case 149 => packOptNestedAggrManyListDistinct_ //       castAggrManyListDistinctBigDecimal

    case 150 => packOptNestedAggrOneListRandString //      castAggrOneListRandString
    case 151 => packOptNestedAggrOneListRand_ //            castAggrOneListRandInt
    case 152 => packOptNestedAggrOneListRand_ //            castAggrOneListRandLong
    case 153 => packOptNestedAggrOneListRand_ //            castAggrOneListRandDouble
    case 154 => packOptNestedAggrOneListRand_ //            castAggrOneListRandBoolean
    case 155 => packOptNestedAggrOneListRandDate //        castAggrOneListRandDate
    case 156 => packOptNestedAggrOneListRand_ //            castAggrOneListRandUUID
    case 157 => packOptNestedAggrOneListRand_ //            castAggrOneListRandURI
    case 158 => packOptNestedAggrOneListRand_ //            castAggrOneListRandBigInt
    case 159 => packOptNestedAggrOneListRand_ //            castAggrOneListRandBigDecimal

    case 160 => packOptNestedAggrManyListRandString //     castAggrManyListRandString
    case 161 => packOptNestedAggrManyListRand_ //           castAggrManyListRandInt
    case 162 => packOptNestedAggrManyListRand_ //           castAggrManyListRandLong
    case 163 => packOptNestedAggrManyListRand_ //           castAggrManyListRandDouble
    case 164 => packOptNestedAggrManyListRand_ //           castAggrManyListRandBoolean
    case 165 => packOptNestedAggrManyListRandDate //       castAggrManyListRandDate
    case 166 => packOptNestedAggrManyListRand_ //           castAggrManyListRandUUID
    case 167 => packOptNestedAggrManyListRand_ //           castAggrManyListRandURI
    case 168 => packOptNestedAggrManyListRand_ //           castAggrManyListRandBigInt
    case 169 => packOptNestedAggrManyListRand_ //           castAggrManyListRandBigDecimal

    case 170 => packOptNestedAggrSingleSampleString //     castAggrSingleSampleString
    case 171 => packOptNestedAggrSingleSample_ //           castAggrSingleSampleInt
    case 172 => packOptNestedAggrSingleSample_ //           castAggrSingleSampleLong
    case 173 => packOptNestedAggrSingleSample_ //           castAggrSingleSampleDouble
    case 174 => packOptNestedAggrSingleSample_ //           castAggrSingleSampleBoolean
    case 175 => packOptNestedAggrSingleSampleDate //       castAggrSingleSampleDate
    case 176 => packOptNestedAggrSingleSample_ //           castAggrSingleSampleUUID
    case 177 => packOptNestedAggrSingleSample_ //           castAggrSingleSampleURI
    case 178 => packOptNestedAggrSingleSample_ //           castAggrSingleSampleBigInt
    case 179 => packOptNestedAggrSingleSample_ //           castAggrSingleSampleBigDecimal

    case 180 => packOptNestedAggrOneSingleString //        castAggrOneSingleString
    case 181 => packOptNestedAggrOneSingle_ //              castAggrOneSingleInt
    case 182 => packOptNestedAggrOneSingle_ //              castAggrOneSingleLong
    case 183 => packOptNestedAggrOneSingle_ //              castAggrOneSingleDouble
    case 184 => packOptNestedAggrOneSingle_ //              castAggrOneSingleBoolean
    case 185 => packOptNestedAggrOneSingleDate //          castAggrOneSingleDate
    case 186 => packOptNestedAggrOneSingle_ //              castAggrOneSingleUUID
    case 187 => packOptNestedAggrOneSingle_ //              castAggrOneSingleURI
    case 188 => packOptNestedAggrOneSingle_ //              castAggrOneSingleBigInt
    case 189 => packOptNestedAggrOneSingle_ //              castAggrOneSingleBigDecimal

    case 190 => packOptNestedAggrManySingleString //       castAggrManySingleString
    case 191 => packOptNestedAggrManySingle_ //             castAggrManySingleInt
    case 192 => packOptNestedAggrManySingle_ //             castAggrManySingleLong
    case 193 => packOptNestedAggrManySingle_ //             castAggrManySingleDouble
    case 194 => packOptNestedAggrManySingle_ //             castAggrManySingleBoolean
    case 195 => packOptNestedAggrManySingleDate //         castAggrManySingleDate
    case 196 => packOptNestedAggrManySingle_ //             castAggrManySingleUUID
    case 197 => packOptNestedAggrManySingle_ //             castAggrManySingleURI
    case 198 => packOptNestedAggrManySingle_ //             castAggrManySingleBigInt
    case 199 => packOptNestedAggrManySingle_ //             castAggrManySingleBigDecimal
  }
}
