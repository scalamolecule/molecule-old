package molecule.core.macros

import scala.reflect.macros.blackbox

private[molecule] trait CastArrays extends Cast {
  val c: blackbox.Context

  import c.universe._

  val castsArray = Array(
    // one
    /*  0 */ (i: Int) => q"castOne[String](row, $i)",
    /*  1 */ (i: Int) => q"castOneInt(row, $i)",
    /*  2 */ (i: Int) => q"castOneInt2(row, $i)",
    /*  3 */ (i: Int) => q"castOneFloat(row, $i)",
    /*  4 */ (i: Int) => q"castOne[Boolean](row, $i)",
    /*  5 */ (i: Int) => q"castOne[Long](row, $i)",
    /*  6 */ (i: Int) => q"castOne[Double](row, $i)",
    /*  7 */ (i: Int) => q"castOne[Date](row, $i)",
    /*  8 */ (i: Int) => q"castOne[UUID](row, $i)",
    /*  9 */ (i: Int) => q"castOneURI(row, $i)",
    /* 10 */ (i: Int) => q"castOneBigInt(row, $i)",
    /* 11 */ (i: Int) => q"castOneBigDecimal(row, $i)",
    /* 12 */ (i: Int) => q"row.get($i)",

    // many
    /* 13 */ (i: Int) => q"castMany[String](row, $i)",
    /* 14 */ (i: Int) => q"castManyInt(row, $i)",
    /* 15 */ (i: Int) => q"castManyFloat(row, $i)",
    /* 16 */ (i: Int) => q"castMany[Boolean](row, $i)",
    /* 17 */ (i: Int) => q"castMany[Long](row, $i)",
    /* 18 */ (i: Int) => q"castMany[Double](row, $i)",
    /* 19 */ (i: Int) => q"castMany[Date](row, $i)",
    /* 20 */ (i: Int) => q"castMany[UUID](row, $i)",
    /* 21 */ (i: Int) => q"castManyURI(row, $i)",
    /* 22 */ (i: Int) => q"castManyBigInt(row, $i)",
    /* 23 */ (i: Int) => q"castManyBigDecimal(row, $i)",

    // opt one
    /* 24 */ (i: Int) => q"castOptOne[String](row, $i)",
    /* 25 */ (i: Int) => q"castOptOneInt(row, $i)",
    /* 26 */ (i: Int) => q"castOptOneFloat(row, $i)",
    /* 27 */ (i: Int) => q"castOptOne[Boolean](row, $i)",
    /* 28 */ (i: Int) => q"castOptOneLong(row, $i)",
    /* 29 */ (i: Int) => q"castOptOneDouble(row, $i)",
    /* 30 */ (i: Int) => q"castOptOne[Date](row, $i)",
    /* 31 */ (i: Int) => q"castOptOne[UUID](row, $i)",
    /* 32 */ (i: Int) => q"castOptOneURI(row, $i)",
    /* 33 */ (i: Int) => q"castOptOneBigInt(row, $i)",
    /* 34 */ (i: Int) => q"castOptOneBigDecimal(row, $i)",

    // opt many
    /* 35 */ (i: Int) => q"castOptMany[String](row, $i)",
    /* 36 */ (i: Int) => q"castOptManyInt(row, $i)",
    /* 37 */ (i: Int) => q"castOptManyFloat(row, $i)",
    /* 38 */ (i: Int) => q"castOptMany[Boolean](row, $i)",
    /* 39 */ (i: Int) => q"castOptManyLong(row, $i)",
    /* 40 */ (i: Int) => q"castOptManyDouble(row, $i)",
    /* 41 */ (i: Int) => q"castOptMany[Date](row, $i)",
    /* 42 */ (i: Int) => q"castOptMany[UUID](row, $i)",
    /* 43 */ (i: Int) => q"castOptManyURI(row, $i)",
    /* 44 */ (i: Int) => q"castOptManyBigInt(row, $i)",
    /* 45 */ (i: Int) => q"castOptManyBigDecimal(row, $i)",

    // opt one apply
    /* 46 */ (i: Int) => q"castOptOneApply[String](row, $i)",
    /* 47 */ (i: Int) => q"castOptOneApplyInt(row, $i)",
    /* 48 */ (i: Int) => q"castOptOneApplyFloat(row, $i)",
    /* 49 */ (i: Int) => q"castOptOneApply[Boolean](row, $i)",
    /* 50 */ (i: Int) => q"castOptOneApplyLong(row, $i)",
    /* 51 */ (i: Int) => q"castOptOneApplyDouble(row, $i)",
    /* 52 */ (i: Int) => q"castOptOneApply[Date](row, $i)",
    /* 53 */ (i: Int) => q"castOptOneApply[UUID](row, $i)",
    /* 54 */ (i: Int) => q"castOptOneApplyURI(row, $i)",
    /* 55 */ (i: Int) => q"castOptOneApplyBigInt(row, $i)",
    /* 56 */ (i: Int) => q"castOptOneApplyBigDecimal(row, $i)",

    // opt many apply
    /* 57 */ (i: Int) => q"castOptManyApply[String](row, $i)",
    /* 58 */ (i: Int) => q"castOptManyApplyInt(row, $i)",
    /* 59 */ (i: Int) => q"castOptManyApplyFloat(row, $i)",
    /* 60 */ (i: Int) => q"castOptManyApply[Boolean](row, $i)",
    /* 61 */ (i: Int) => q"castOptManyApplyLong(row, $i)",
    /* 62 */ (i: Int) => q"castOptManyApplyDouble(row, $i)",
    /* 63 */ (i: Int) => q"castOptManyApply[Date](row, $i)",
    /* 64 */ (i: Int) => q"castOptManyApply[UUID](row, $i)",
    /* 65 */ (i: Int) => q"castOptManyApplyURI(row, $i)",
    /* 66 */ (i: Int) => q"castOptManyApplyBigInt(row, $i)",
    /* 67 */ (i: Int) => q"castOptManyApplyBigDecimal(row, $i)",

    // opt one ref
    // opt many ref
    /* 68 */ (i: Int) => q"castOptOneRefAttr(row, $i)",
    /* 69 */ (i: Int) => q"castOptManyRefAttr(row, $i)",

    // one enum
    // many enum
    /* 70 */ (i: Int) => q"row.get($i).asInstanceOf[String]",
    /* 71 */ (i: Int) => q"castMany[String](row, $i)",

    // opt one enum
    // opt many enum
    /* 72 */ (i: Int) => q"castOptOneEnum(row, $i)",
    /* 73 */ (i: Int) => q"castOptManyEnum(row, $i)",

    // map
    /* 74 */ (i: Int) => q"castMapString(row, $i)",
    /* 75 */ (i: Int) => q"castMapInt(row, $i)",
    /* 76 */ (i: Int) => q"castMapFloat(row, $i)",
    /* 77 */ (i: Int) => q"castMapBoolean(row, $i)",
    /* 78 */ (i: Int) => q"castMapLong(row, $i)",
    /* 79 */ (i: Int) => q"castMapDouble(row, $i)",
    /* 80 */ (i: Int) => q"castMapDate(row, $i)",
    /* 81 */ (i: Int) => q"castMapUUID(row, $i)",
    /* 82 */ (i: Int) => q"castMapURI(row, $i)",
    /* 83 */ (i: Int) => q"castMapBigInt(row, $i)",
    /* 84 */ (i: Int) => q"castMapBigDecimal(row, $i)",

    // opt map
    /* 85 */ (i: Int) => q"castOptMapString(row, $i)",
    /* 86 */ (i: Int) => q"castOptMapInt(row, $i)",
    /* 87 */ (i: Int) => q"castOptMapFloat(row, $i)",
    /* 88 */ (i: Int) => q"castOptMapBoolean(row, $i)",
    /* 89 */ (i: Int) => q"castOptMapLong(row, $i)",
    /* 90 */ (i: Int) => q"castOptMapDouble(row, $i)",
    /* 91 */ (i: Int) => q"castOptMapDate(row, $i)",
    /* 92 */ (i: Int) => q"castOptMapUUID(row, $i)",
    /* 93 */ (i: Int) => q"castOptMapURI(row, $i)",
    /* 94 */ (i: Int) => q"castOptMapBigInt(row, $i)",
    /* 95 */ (i: Int) => q"castOptMapBigDecimal(row, $i)",

    // opt map apply
    /*  96 */ (i: Int) => q"castOptMapApplyString(row, $i)",
    /*  97 */ (i: Int) => q"castOptMapApplyInt(row, $i)",
    /*  98 */ (i: Int) => q"castOptMapApplyFloat(row, $i)",
    /*  99 */ (i: Int) => q"castOptMapApplyBoolean(row, $i)",
    /* 100 */ (i: Int) => q"castOptMapApplyLong(row, $i)",
    /* 101 */ (i: Int) => q"castOptMapApplyDouble(row, $i)",
    /* 102 */ (i: Int) => q"castOptMapApplyDate(row, $i)",
    /* 103 */ (i: Int) => q"castOptMapApplyUUID(row, $i)",
    /* 104 */ (i: Int) => q"castOptMapApplyURI(row, $i)",
    /* 105 */ (i: Int) => q"castOptMapApplyBigInt(row, $i)",
    /* 106 */ (i: Int) => q"castOptMapApplyBigDecimal(row, $i)",

    // keyed map
    /* 107 */ (i: Int) => q"row.get($i).toString",
    /* 108 */ (i: Int) => q"row.get($i).toString.toInt",
    /* 109 */ (i: Int) => q"row.get($i).toString.toLong",
    /* 110 */ (i: Int) => q"row.get($i).toString.toFloat",
    /* 111 */ (i: Int) => q"row.get($i).toString.toDouble",
    /* 112 */ (i: Int) => q"row.get($i).toString.toBoolean",
    /* 113 */ (i: Int) => q"molecule.core.util.fns.str2date(row.get($i).toString)",
    /* 114 */ (i: Int) => q"java.util.UUID.fromString(row.get($i).toString)",
    /* 115 */ (i: Int) => q"new java.net.URI(row.get($i).toString)",
    /* 116 */ (i: Int) => q"BigInt(row.get($i).toString)",
    /* 117 */ (i: Int) => q"BigDecimal(row.get($i).toString)",
    /* 118 */ (i: Int) => q"row.get($i)"
  )
}
