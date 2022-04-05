package molecule.datomic.base.util

import java.net.URI
import com.cognitect.transit.impl.URIImpl
import molecule.core.util.JavaConversions

private[molecule] object Convert extends Convert
private[molecule] trait Convert extends JavaConversions {

  private[molecule] def datomValue2Scala(v: Any): Any = v match {
    case s: java.lang.String      => s
    case i: java.lang.Integer     => i: Int
    case l: java.lang.Long        => l: Long
    case d: java.lang.Double      => d: Double
    case b: java.lang.Boolean     => b: Boolean
    case d: java.util.Date        => d
    case u: java.util.UUID        => u
    case u: java.net.URI          => u
    case uriImpl: URIImpl         => new URI(uriImpl.toString)
    case kw: clojure.lang.Keyword => kw.toString
    case bi: clojure.lang.BigInt  => BigInt(bi.toBigInteger)
    case bi: java.math.BigInteger => BigInt(bi)
    case bd: java.math.BigDecimal => BigDecimal(bd)
    case other                    => other
  }
}