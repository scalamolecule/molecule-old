package molecule.datomic.base.util

import molecule.core.exceptions.MoleculeException
import molecule.core.util.JavaConversions

private[molecule] object Convert extends JavaConversions {

  private[molecule] def datomValue2Scala(v: Any): Any = v match {
    case s: java.lang.String      => s
    case i: java.lang.Integer     => i: Int
    case l: java.lang.Long        => l: Long
    case d: java.lang.Double      => d: Double
    case b: java.lang.Boolean     => b: Boolean
    case d: java.util.Date        => d
    case u: java.util.UUID        => u
    case u: java.net.URI          => u
    case bi: clojure.lang.BigInt  => BigInt(bi.toBigInteger)
    case bi: java.math.BigInteger => BigInt(bi)
    case bd: java.math.BigDecimal => BigDecimal(bd)
    case other                    => throw MoleculeException(
      "Unexpected Datom java value type to convert: " + other.getClass)
  }
}