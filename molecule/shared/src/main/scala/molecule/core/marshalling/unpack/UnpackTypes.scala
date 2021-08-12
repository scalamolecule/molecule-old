package molecule.core.marshalling.unpack

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.util.Helpers

trait UnpackTypes extends Helpers {

  protected lazy val buf = new StringBuffer
  protected var first    = true
  protected var v        = ""


  protected val unpackOneString = (v0: String, vs: Iterator[String]) => {
    buf.setLength(0)
    first = true
    v = v0
    do {
      if (first) {
        buf.append(v)
      } else {
        buf.append("\n")
        buf.append(v)
      }
      v = vs.next()
    } while (v != "◄")
    buf.toString
  }

  protected val unpackOneInt        = (v: String) => v.toInt
  protected val unpackOneLong       = (v: String) => v.toLong
  protected val unpackOneDouble     = (v: String) => v.toDouble
  protected val unpackOneBoolean    = (v: String) => v.toBoolean
  protected val unpackOneDate       = (v: String) => str2date(v)
  protected val unpackOneUUID       = (v: String) => UUID.fromString(v)
  protected val unpackOneURI        = (v: String) => new URI(v)
  protected val unpackOneBigInt     = (v: String) => BigInt(v)
  protected val unpackOneBigDecimal = (v: String) => BigDecimal(v)


  protected val unpackOptOneString = (v0: String, vs: Iterator[String]) => {
    if (v0 == "◄") {
      Option.empty[String]
    } else {
      buf.setLength(0)
      first = true
      v = v0
      do {
        if (first) {
          buf.append(v)
        } else {
          buf.append("\n")
          buf.append(v)
        }
        v = vs.next()
      } while (v != "◄")
      Some(buf.toString)
    }
  }


  protected val unpackOptOneInt        = (v: String) => if (v == "◄") Option.empty[Int] else Some(v.toInt)
  protected val unpackOptOneLong       = (v: String) => if (v == "◄") Option.empty[Long] else Some(v.toLong)
  protected val unpackOptOneDouble     = (v: String) => if (v == "◄") Option.empty[Double] else Some(v.toDouble)
  protected val unpackOptOneBoolean    = (v: String) => if (v == "◄") Option.empty[Boolean] else Some(v.toBoolean)
  protected val unpackOptOneDate       = (v: String) => if (v == "◄") Option.empty[Date] else Some(str2date(v))
  protected val unpackOptOneUUID       = (v: String) => if (v == "◄") Option.empty[UUID] else Some(UUID.fromString(v))
  protected val unpackOptOneURI        = (v: String) => if (v == "◄") Option.empty[URI] else Some(new URI(v))
  protected val unpackOptOneBigInt     = (v: String) => if (v == "◄") Option.empty[BigInt] else Some(BigInt(v))
  protected val unpackOptOneBigDecimal = (v: String) => if (v == "◄") Option.empty[BigDecimal] else Some(BigDecimal(v))
}
