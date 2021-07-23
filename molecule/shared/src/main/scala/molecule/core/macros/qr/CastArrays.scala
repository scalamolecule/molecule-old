package molecule.core.macros.qr

import molecule.core.macros.build.tpl.BuildTplOptNested
import molecule.core.macros.attrResolverTrees.LambdaCastTypes
import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait CastArrays extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  lazy val dataArrays: Array[(Int, Int) => c.universe.Tree] = Array(
    /*   0 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneString($arrayIndex)",
    /*   1 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneInt($arrayIndex)",
    /*   2 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneLong($arrayIndex)",
    /*   3 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneDouble($arrayIndex)",
    /*   4 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneBoolean($arrayIndex)",
    /*   5 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneDate($arrayIndex)",
    /*   6 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneUUID($arrayIndex)",
    /*   7 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneURI($arrayIndex)",
    /*   8 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneBigInt($arrayIndex)",
    /*   9 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.oneBigDecimal($arrayIndex)",
    /*  10 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneString($arrayIndex)",
    /*  11 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneInt($arrayIndex)",
    /*  12 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneLong($arrayIndex)",
    /*  13 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneDouble($arrayIndex)",
    /*  14 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneBoolean($arrayIndex)",
    /*  15 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneDate($arrayIndex)",
    /*  16 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneUUID($arrayIndex)",
    /*  17 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneURI($arrayIndex)",
    /*  18 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneBigInt($arrayIndex)",
    /*  19 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optOneBigDecimal($arrayIndex)",
    /*  20 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyString($arrayIndex)",
    /*  21 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyInt($arrayIndex)",
    /*  22 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyLong($arrayIndex)",
    /*  23 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyDouble($arrayIndex)",
    /*  24 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyBoolean($arrayIndex)",
    /*  25 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyDate($arrayIndex)",
    /*  26 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyUUID($arrayIndex)",
    /*  27 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyURI($arrayIndex)",
    /*  28 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyBigInt($arrayIndex)",
    /*  29 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.manyBigDecimal($arrayIndex)",
    /*  30 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyString($arrayIndex)",
    /*  31 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyInt($arrayIndex)",
    /*  32 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyLong($arrayIndex)",
    /*  33 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyDouble($arrayIndex)",
    /*  34 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyBoolean($arrayIndex)",
    /*  35 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyDate($arrayIndex)",
    /*  36 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyUUID($arrayIndex)",
    /*  37 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyURI($arrayIndex)",
    /*  38 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyBigInt($arrayIndex)",
    /*  39 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optManyBigDecimal($arrayIndex)",
    /*  40 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapString($arrayIndex)",
    /*  41 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapInt($arrayIndex)",
    /*  42 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapLong($arrayIndex)",
    /*  43 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapDouble($arrayIndex)",
    /*  44 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapBoolean($arrayIndex)",
    /*  45 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapDate($arrayIndex)",
    /*  46 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapUUID($arrayIndex)",
    /*  47 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapURI($arrayIndex)",
    /*  48 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapBigInt($arrayIndex)",
    /*  49 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.mapBigDecimal($arrayIndex)",
    /*  50 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapString($arrayIndex)",
    /*  51 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapInt($arrayIndex)",
    /*  52 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapLong($arrayIndex)",
    /*  53 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapDouble($arrayIndex)",
    /*  54 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapBoolean($arrayIndex)",
    /*  55 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapDate($arrayIndex)",
    /*  56 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapUUID($arrayIndex)",
    /*  57 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapURI($arrayIndex)",
    /*  58 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapBigInt($arrayIndex)",
    /*  59 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.optMapBigDecimal($arrayIndex)",
    /*  60 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneString($arrayIndex)",
    /*  61 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneInt($arrayIndex)",
    /*  62 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneLong($arrayIndex)",
    /*  63 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneDouble($arrayIndex)",
    /*  64 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneBoolean($arrayIndex)",
    /*  65 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneDate($arrayIndex)",
    /*  66 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneUUID($arrayIndex)",
    /*  67 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneURI($arrayIndex)",
    /*  68 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneBigInt($arrayIndex)",
    /*  69 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listOneBigDecimal($arrayIndex)",
    /*  70 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyString($arrayIndex)",
    /*  71 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyInt($arrayIndex)",
    /*  72 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyLong($arrayIndex)",
    /*  73 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyDouble($arrayIndex)",
    /*  74 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyBoolean($arrayIndex)",
    /*  75 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyDate($arrayIndex)",
    /*  76 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyUUID($arrayIndex)",
    /*  77 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyURI($arrayIndex)",
    /*  78 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyBigInt($arrayIndex)",
    /*  79 */ (colIndex: Int, arrayIndex: Int) => q"val ${TermName("a" + colIndex)} = qr.listManyBigDecimal($arrayIndex)"
  )
}
