package molecule.datomic.base.util

import molecule.core.exceptions.MoleculeException
import molecule.core.util.DateHandling

trait JavaHelpers extends DateHandling {


  protected def isEnum(rawValue: Any) = {
    // Check enum prefix on any of 3 possible levels
    rawValue match {
      case l: Seq[_] => l.headOption.fold(false) {
        case l2: Seq[_] => l2.headOption.fold(false)(v => v.toString.startsWith("__enum__"))
        case v: String  => v.startsWith("__enum__")
      }
      case v: String => v.startsWith("__enum__")
      case _         => false
    }
  }

  protected def getEnum(s: String): AnyRef = s match {
    case r"__enum__:([A-Za-z0-9\._]+)$ns/([A-Za-z0-9_]+)$enum" => clojure.lang.Keyword.intern(ns, enum)
    case other                                                 =>
      throw MoleculeException(s"Unexpected enum input: `$other`")
  }

  def castTpeV(tpe: String, v: String): Object = {
    (tpe, v) match {
      case ("String", v)     => if (isEnum(v)) getEnum(v) else v
      case ("Int", v)        => v.toInt.asInstanceOf[Object]
      case ("Long", v)       => v.toLong.asInstanceOf[Object]
      case ("Double", v)     => v.toDouble.asInstanceOf[Object]
      case ("Boolean", v)    => v.toBoolean.asInstanceOf[Object]
      case ("Date", v)       => str2date(v).asInstanceOf[Object]
      case ("UUID", v)       => java.util.UUID.fromString(v).asInstanceOf[Object]
      case ("URI", v)        => new java.net.URI(v).asInstanceOf[Object]
      case ("BigInt", v)     => new java.math.BigInteger(v).asInstanceOf[Object]
      case ("BigDecimal", v) =>
        val v1 = if (v.contains(".")) v else s"$v.0"
        new java.math.BigDecimal(v1).asInstanceOf[Object]
      case _                 => throw MoleculeException(s"Unexpected input pair to cast: ($tpe, $v)")
    }
  }
}
