package molecule.datomic.base.marshalling.cast

import java.util

class CastLambdas(maxRows: Int) extends CastTypes(maxRows) {

  // Array with all cast lambdas for fast lookup
  // Array[colIndex => (row, rowIndex) => <dataArray(rowIndex) = row(colIndex)>]
  val castLambdas: Array[Int => (util.List[AnyRef], Int) => Unit] = Array(
    /*   0 */ castOneString,
    /*   1 */ castOneInt,
    /*   2 */ castOneInt2,
    /*   3 */ castOneFloat,
    /*   4 */ castOneLong,
    /*   5 */ castOneDouble,
    /*   6 */ castOneBoolean,
    /*   7 */ castOneDate,
    /*   8 */ castOneUUID,
    /*   9 */ castOneURI,
    /*  10 */ castOneBigInt,
    /*  11 */ castOneBigDecimal,
    /*  12 */ castOneAny,
    /*  13 */ castOptOneEnum,
    /*  14 */ castOptOneString,
    /*  15 */ castOptOneInt,
    /*  16 */ castOptOneFloat,
    /*  17 */ castOptOneLong,
    /*  18 */ castOptOneDouble,
    /*  19 */ castOptOneBoolean,
    /*  20 */ castOptOneDate,
    /*  21 */ castOptOneUUID,
    /*  22 */ castOptOneURI,
    /*  23 */ castOptOneBigInt,
    /*  24 */ castOptOneBigDecimal,
    /*  25 */ castOptOneRefAttr,
    /*  26 */ castManyEnum,
    /*  27 */ castManyString,
    /*  28 */ castManyInt,
    /*  29 */ castManyFloat,
    /*  30 */ castManyLong,
    /*  31 */ castManyDouble,
    /*  32 */ castManyBoolean,
    /*  33 */ castManyDate,
    /*  34 */ castManyUUID,
    /*  35 */ castManyURI,
    /*  36 */ castManyBigInt,
    /*  37 */ castManyBigDecimal,
    /*  38 */ castOptManyEnum,
    /*  39 */ castOptManyString,
    /*  40 */ castOptManyInt,
    /*  41 */ castOptManyFloat,
    /*  42 */ castOptManyLong,
    /*  43 */ castOptManyDouble,
    /*  44 */ castOptManyBoolean,
    /*  45 */ castOptManyDate,
    /*  46 */ castOptManyUUID,
    /*  47 */ castOptManyURI,
    /*  48 */ castOptManyBigInt,
    /*  49 */ castOptManyBigDecimal,
    /*  50 */ castOptManyRefAttr,
    /*  51 */ castMapString,
    /*  52 */ castMapInt,
    /*  53 */ castMapFloat,
    /*  54 */ castMapLong,
    /*  55 */ castMapDouble,
    /*  56 */ castMapBoolean,
    /*  57 */ castMapDate,
    /*  58 */ castMapUUID,
    /*  59 */ castMapURI,
    /*  60 */ castMapBigInt,
    /*  61 */ castMapBigDecimal,
    /*  62 */ castOptMapString,
    /*  63 */ castOptMapInt,
    /*  64 */ castOptMapFloat,
    /*  65 */ castOptMapLong,
    /*  66 */ castOptMapDouble,
    /*  67 */ castOptMapBoolean,
    /*  68 */ castOptMapDate,
    /*  69 */ castOptMapUUID,
    /*  70 */ castOptMapURI,
    /*  71 */ castOptMapBigInt,
    /*  72 */ castOptMapBigDecimal,
    /*  73 */ castOptApplyOneString,
    /*  74 */ castOptApplyOneInt,
    /*  75 */ castOptApplyOneFloat,
    /*  76 */ castOptApplyOneLong,
    /*  77 */ castOptApplyOneDouble,
    /*  78 */ castOptApplyOneBoolean,
    /*  79 */ castOptApplyOneDate,
    /*  80 */ castOptApplyOneUUID,
    /*  81 */ castOptApplyOneURI,
    /*  82 */ castOptApplyOneBigInt,
    /*  83 */ castOptApplyOneBigDecimal,
    /*  84 */ castOptApplyManyString,
    /*  85 */ castOptApplyManyInt,
    /*  86 */ castOptApplyManyFloat,
    /*  87 */ castOptApplyManyLong,
    /*  88 */ castOptApplyManyDouble,
    /*  89 */ castOptApplyManyBoolean,
    /*  90 */ castOptApplyManyDate,
    /*  91 */ castOptApplyManyUUID,
    /*  92 */ castOptApplyManyURI,
    /*  93 */ castOptApplyManyBigInt,
    /*  94 */ castOptApplyManyBigDecimal,
    /*  95 */ castOptApplyMapString,
    /*  96 */ castOptApplyMapInt,
    /*  97 */ castOptApplyMapFloat,
    /*  98 */ castOptApplyMapLong,
    /*  99 */ castOptApplyMapDouble,
    /* 100 */ castOptApplyMapBoolean,
    /* 101 */ castOptApplyMapDate,
    /* 102 */ castOptApplyMapUUID,
    /* 103 */ castOptApplyMapURI,
    /* 104 */ castOptApplyMapBigInt,
    /* 105 */ castOptApplyMapBigDecimal,
    /* 106 */ castKeyedMapString,
    /* 107 */ castKeyedMapInt,
    /* 108 */ castKeyedMapFloat,
    /* 109 */ castKeyedMapLong,
    /* 110 */ castKeyedMapDouble,
    /* 111 */ castKeyedMapBoolean,
    /* 112 */ castKeyedMapDate,
    /* 113 */ castKeyedMapUUID,
    /* 114 */ castKeyedMapURI,
    /* 115 */ castKeyedMapBigInt,
    /* 116 */ castKeyedMapBigDecimal,
    /* 117 */ castKeyedMapAny,
    /* 118 */ castAggrInt,
    /* 119 */ castAggrDouble,
    /* 120 */ castAggrOneListString,
    /* 121 */ castAggrOneListInt,
    /* 122 */ castAggrOneListFloat,
    /* 123 */ castAggrOneListLong,
    /* 124 */ castAggrOneListDouble,
    /* 125 */ castAggrOneListBoolean,
    /* 126 */ castAggrOneListDate,
    /* 127 */ castAggrOneListUUID,
    /* 128 */ castAggrOneListURI,
    /* 129 */ castAggrOneListBigInt,
    /* 130 */ castAggrOneListBigDecimal,
    /* 131 */ castAggrManyListString,
    /* 132 */ castAggrManyListInt,
    /* 133 */ castAggrManyListFloat,
    /* 134 */ castAggrManyListLong,
    /* 135 */ castAggrManyListDouble,
    /* 136 */ castAggrManyListBoolean,
    /* 137 */ castAggrManyListDate,
    /* 138 */ castAggrManyListUUID,
    /* 139 */ castAggrManyListURI,
    /* 140 */ castAggrManyListBigInt,
    /* 141 */ castAggrManyListBigDecimal,
    /* 142 */ castAggrOneListDistinctString,
    /* 143 */ castAggrOneListDistinctInt,
    /* 144 */ castAggrOneListDistinctFloat,
    /* 145 */ castAggrOneListDistinctLong,
    /* 146 */ castAggrOneListDistinctDouble,
    /* 147 */ castAggrOneListDistinctBoolean,
    /* 148 */ castAggrOneListDistinctDate,
    /* 149 */ castAggrOneListDistinctUUID,
    /* 150 */ castAggrOneListDistinctURI,
    /* 151 */ castAggrOneListDistinctBigInt,
    /* 152 */ castAggrOneListDistinctBigDecimal,
    /* 153 */ castAggrManyListDistinctString,
    /* 154 */ castAggrManyListDistinctInt,
    /* 155 */ castAggrManyListDistinctFloat,
    /* 156 */ castAggrManyListDistinctLong,
    /* 157 */ castAggrManyListDistinctDouble,
    /* 158 */ castAggrManyListDistinctBoolean,
    /* 159 */ castAggrManyListDistinctDate,
    /* 160 */ castAggrManyListDistinctUUID,
    /* 161 */ castAggrManyListDistinctURI,
    /* 162 */ castAggrManyListDistinctBigInt,
    /* 163 */ castAggrManyListDistinctBigDecimal,
    /* 164 */ castAggrOneListRandString,
    /* 165 */ castAggrOneListRandInt,
    /* 166 */ castAggrOneListRandFloat,
    /* 167 */ castAggrOneListRandLong,
    /* 168 */ castAggrOneListRandDouble,
    /* 169 */ castAggrOneListRandBoolean,
    /* 170 */ castAggrOneListRandDate,
    /* 171 */ castAggrOneListRandUUID,
    /* 172 */ castAggrOneListRandURI,
    /* 173 */ castAggrOneListRandBigInt,
    /* 174 */ castAggrOneListRandBigDecimal,
    /* 175 */ castAggrManyListRandString,
    /* 176 */ castAggrManyListRandInt,
    /* 177 */ castAggrManyListRandFloat,
    /* 178 */ castAggrManyListRandLong,
    /* 179 */ castAggrManyListRandDouble,
    /* 180 */ castAggrManyListRandBoolean,
    /* 181 */ castAggrManyListRandDate,
    /* 182 */ castAggrManyListRandUUID,
    /* 183 */ castAggrManyListRandURI,
    /* 184 */ castAggrManyListRandBigInt,
    /* 185 */ castAggrManyListRandBigDecimal,
    /* 186 */ castAggrSingleSampleString,
    /* 187 */ castAggrSingleSampleInt,
    /* 188 */ castAggrSingleSampleFloat,
    /* 189 */ castAggrSingleSampleLong,
    /* 190 */ castAggrSingleSampleDouble,
    /* 191 */ castAggrSingleSampleBoolean,
    /* 192 */ castAggrSingleSampleDate,
    /* 193 */ castAggrSingleSampleUUID,
    /* 194 */ castAggrSingleSampleURI,
    /* 195 */ castAggrSingleSampleBigInt,
    /* 196 */ castAggrSingleSampleBigDecimal,
    /* 197 */ castAggrOneSingleString,
    /* 198 */ castAggrOneSingleInt,
    /* 199 */ castAggrOneSingleFloat,
    /* 200 */ castAggrOneSingleLong,
    /* 201 */ castAggrOneSingleDouble,
    /* 202 */ castAggrOneSingleBoolean,
    /* 203 */ castAggrOneSingleDate,
    /* 204 */ castAggrOneSingleUUID,
    /* 205 */ castAggrOneSingleURI,
    /* 206 */ castAggrOneSingleBigInt,
    /* 207 */ castAggrOneSingleBigDecimal,
    /* 208 */ castAggrManySingleString,
    /* 209 */ castAggrManySingleInt,
    /* 210 */ castAggrManySingleFloat,
    /* 211 */ castAggrManySingleLong,
    /* 212 */ castAggrManySingleDouble,
    /* 213 */ castAggrManySingleBoolean,
    /* 214 */ castAggrManySingleDate,
    /* 215 */ castAggrManySingleUUID,
    /* 216 */ castAggrManySingleURI,
    /* 217 */ castAggrManySingleBigInt,
    /* 218 */ castAggrManySingleBigDecimal

    // todo: Opt nested casts...
  )
}