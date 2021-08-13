package molecule.datomic.base.marshalling.cast

import java.util

class CastLambdas(maxRows: Int) extends CastTypes(maxRows) {

  def castLambdas(i: Int): Int => (util.List[AnyRef], Int) => Unit = i match {
    case 0  => castOneString
    case 1  => castOneInt
    case 2  => castOneInt
    case 3  => castOneLong
    case 4  => castOneDouble
    case 5  => castOneBoolean
    case 6  => castOneDate
    case 7  => castOneUUID
    case 8  => castOneURI
    case 9  => castOneBigInt
    case 10 => castOneBigDecimal
    case 11 => castOneAny

    case 12 => castOptOneEnum
    case 13 => castOptOneString
    case 14 => castOptOneInt
    case 15 => castOptOneLong
    case 16 => castOptOneDouble
    case 17 => castOptOneBoolean
    case 18 => castOptOneDate
    case 19 => castOptOneUUID
    case 20 => castOptOneURI
    case 21 => castOptOneBigInt
    case 22 => castOptOneBigDecimal
    case 23 => castOptOneRefAttr

    case 24 => castManyEnum
    case 25 => castManyString
    case 26 => castManyInt
    case 27 => castManyLong
    case 28 => castManyDouble
    case 29 => castManyBoolean
    case 30 => castManyDate
    case 31 => castManyUUID
    case 32 => castManyURI
    case 33 => castManyBigInt
    case 34 => castManyBigDecimal

    case 35 => castOptManyEnum
    case 36 => castOptManyString
    case 37 => castOptManyInt
    case 38 => castOptManyLong
    case 39 => castOptManyDouble
    case 40 => castOptManyBoolean
    case 41 => castOptManyDate
    case 42 => castOptManyUUID
    case 43 => castOptManyURI
    case 44 => castOptManyBigInt
    case 45 => castOptManyBigDecimal
    case 46 => castOptManyRefAttr

    case 47 => castMapString
    case 48 => castMapInt
    case 49 => castMapLong
    case 50 => castMapDouble
    case 51 => castMapBoolean
    case 52 => castMapDate
    case 53 => castMapUUID
    case 54 => castMapURI
    case 55 => castMapBigInt
    case 56 => castMapBigDecimal

    case 57 => castOptMapString
    case 58 => castOptMapInt
    case 59 => castOptMapLong
    case 60 => castOptMapDouble
    case 61 => castOptMapBoolean
    case 62 => castOptMapDate
    case 63 => castOptMapUUID
    case 64 => castOptMapURI
    case 65 => castOptMapBigInt
    case 66 => castOptMapBigDecimal

    case 67 => castOptApplyOneString
    case 68 => castOptApplyOneInt
    case 69 => castOptApplyOneLong
    case 70 => castOptApplyOneDouble
    case 71 => castOptApplyOneBoolean
    case 72 => castOptApplyOneDate
    case 73 => castOptApplyOneUUID
    case 74 => castOptApplyOneURI
    case 75 => castOptApplyOneBigInt
    case 76 => castOptApplyOneBigDecimal

    case 77 => castOptApplyManyString
    case 78 => castOptApplyManyInt
    case 79 => castOptApplyManyLong
    case 80 => castOptApplyManyDouble
    case 81 => castOptApplyManyBoolean
    case 82 => castOptApplyManyDate
    case 83 => castOptApplyManyUUID
    case 84 => castOptApplyManyURI
    case 85 => castOptApplyManyBigInt
    case 86 => castOptApplyManyBigDecimal

    case 87 => castOptApplyMapString
    case 88 => castOptApplyMapInt
    case 89 => castOptApplyMapLong
    case 90 => castOptApplyMapDouble
    case 91 => castOptApplyMapBoolean
    case 92 => castOptApplyMapDate
    case 93 => castOptApplyMapUUID
    case 94 => castOptApplyMapURI
    case 95 => castOptApplyMapBigInt
    case 96 => castOptApplyMapBigDecimal

    case 97  => castKeyedMapString
    case 98  => castKeyedMapInt
    case 99  => castKeyedMapLong
    case 100 => castKeyedMapDouble
    case 101 => castKeyedMapBoolean
    case 102 => castKeyedMapDate
    case 103 => castKeyedMapUUID
    case 104 => castKeyedMapURI
    case 105 => castKeyedMapBigInt
    case 106 => castKeyedMapBigDecimal
    case 107 => castKeyedMapAny

    case 108 => castAggrInt
    case 109 => castAggrDouble

    case 110 => castAggrOneListString
    case 111 => castAggrOneListInt
    case 112 => castAggrOneListLong
    case 113 => castAggrOneListDouble
    case 114 => castAggrOneListBoolean
    case 115 => castAggrOneListDate
    case 116 => castAggrOneListUUID
    case 117 => castAggrOneListURI
    case 118 => castAggrOneListBigInt
    case 119 => castAggrOneListBigDecimal

    case 120 => castAggrManyListString
    case 121 => castAggrManyListInt
    case 122 => castAggrManyListLong
    case 123 => castAggrManyListDouble
    case 124 => castAggrManyListBoolean
    case 125 => castAggrManyListDate
    case 126 => castAggrManyListUUID
    case 127 => castAggrManyListURI
    case 128 => castAggrManyListBigInt
    case 129 => castAggrManyListBigDecimal

    case 130 => castAggrOneListDistinctString
    case 131 => castAggrOneListDistinctInt
    case 132 => castAggrOneListDistinctLong
    case 133 => castAggrOneListDistinctDouble
    case 134 => castAggrOneListDistinctBoolean
    case 135 => castAggrOneListDistinctDate
    case 136 => castAggrOneListDistinctUUID
    case 137 => castAggrOneListDistinctURI
    case 138 => castAggrOneListDistinctBigInt
    case 139 => castAggrOneListDistinctBigDecimal

    case 140 => castAggrManyListDistinctString
    case 141 => castAggrManyListDistinctInt
    case 142 => castAggrManyListDistinctLong
    case 143 => castAggrManyListDistinctDouble
    case 144 => castAggrManyListDistinctBoolean
    case 145 => castAggrManyListDistinctDate
    case 146 => castAggrManyListDistinctUUID
    case 147 => castAggrManyListDistinctURI
    case 148 => castAggrManyListDistinctBigInt
    case 149 => castAggrManyListDistinctBigDecimal

    case 150 => castAggrOneListRandString
    case 151 => castAggrOneListRandInt
    case 152 => castAggrOneListRandLong
    case 153 => castAggrOneListRandDouble
    case 154 => castAggrOneListRandBoolean
    case 155 => castAggrOneListRandDate
    case 156 => castAggrOneListRandUUID
    case 157 => castAggrOneListRandURI
    case 158 => castAggrOneListRandBigInt
    case 159 => castAggrOneListRandBigDecimal

    case 160 => castAggrManyListRandString
    case 161 => castAggrManyListRandInt
    case 162 => castAggrManyListRandLong
    case 163 => castAggrManyListRandDouble
    case 164 => castAggrManyListRandBoolean
    case 165 => castAggrManyListRandDate
    case 166 => castAggrManyListRandUUID
    case 167 => castAggrManyListRandURI
    case 168 => castAggrManyListRandBigInt
    case 169 => castAggrManyListRandBigDecimal

    case 170 => castAggrSingleSampleString
    case 171 => castAggrSingleSampleInt
    case 172 => castAggrSingleSampleLong
    case 173 => castAggrSingleSampleDouble
    case 174 => castAggrSingleSampleBoolean
    case 175 => castAggrSingleSampleDate
    case 176 => castAggrSingleSampleUUID
    case 177 => castAggrSingleSampleURI
    case 178 => castAggrSingleSampleBigInt
    case 179 => castAggrSingleSampleBigDecimal

    case 180 => castAggrOneSingleString
    case 181 => castAggrOneSingleInt
    case 182 => castAggrOneSingleLong
    case 183 => castAggrOneSingleDouble
    case 184 => castAggrOneSingleBoolean
    case 185 => castAggrOneSingleDate
    case 186 => castAggrOneSingleUUID
    case 187 => castAggrOneSingleURI
    case 188 => castAggrOneSingleBigInt
    case 189 => castAggrOneSingleBigDecimal

    case 190 => castAggrManySingleString
    case 191 => castAggrManySingleInt
    case 192 => castAggrManySingleLong
    case 193 => castAggrManySingleDouble
    case 194 => castAggrManySingleBoolean
    case 195 => castAggrManySingleDate
    case 196 => castAggrManySingleUUID
    case 197 => castAggrManySingleURI
    case 198 => castAggrManySingleBigInt
    case 199 => castAggrManySingleBigDecimal
  }
}
