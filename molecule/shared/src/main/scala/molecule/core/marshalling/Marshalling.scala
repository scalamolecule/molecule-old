package molecule.core.marshalling

import java.util.{UUID, List => jList}
import molecule.core.api.Molecule
import molecule.core.ast.elements.Model
import molecule.core.macros.qr.CastArrays
import molecule.core.marshalling.attrIndexes.Indexes
import molecule.core.util.Helpers
import molecule.datomic.base.ast.query.Query


/** Marshalling methods for casting raw row (server) / QueryResult (client) data.
  *
  * Methods are implemented by macros for either JS or JVM platform
  */
abstract class Marshalling[Obj, Tpl](
  model: Model,
  queryData: (Query, Option[Query], Query, Option[Query], Option[Throwable])
) extends Molecule(model, queryData) with Helpers {

  /** Indexes to resolve marshalling for each attribute value in a row. */
  protected lazy val indexes    : Indexes = ???
  protected lazy val isOptNested: Boolean = false


  // JVM ......................

  /** Adds row as json to a mutable StringBuffer for fast build-up. Built by macro */
  protected def row2json(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???


  /** Row to object cast interface to be materialized by macro */
  protected def row2obj(row: jList[AnyRef]): Obj = ???


  /** Row to tuple cast interface to be materialized by macro */
  protected def row2tpl(row: jList[AnyRef]): Tpl = ???


  // JS ......................

  /** QueryResult to object cast interface to be materialized by macro
    *
    * @param qr
    * @return rowIndex => Obj
    */
  protected def qr2obj(qr: QueryResult): Int => Obj = ???


  /** QueryResult to tuple cast interface to be materialized by macro
    *
    * @param qr
    * @return rowIndex => Tpl
    */
  protected def qr2tpl(qr: QueryResult): Int => Tpl = ???


  protected def qr2list(qr: QueryResult): Int => jList[Any] = ???


  protected def packed2tpl(vs: Iterator[String]): Tpl = ???
  protected def packed2obj(vs: Iterator[String]): Obj = ???
  protected def packed2json(vs: Iterator[String]): String = ???


  // Generic `v` of type Any needs to be cast on JS side
  protected def castV(s: String): Any = {
    val v = s.drop(10)
    s.take(10) match {
      case "String    " => v
      case "Integer   " => v.toInt
      case "Long      " => v.toLong
      case "Double    " => v.toDouble
      case "Boolean   " => v.toBoolean
      case "Date      " => str2date(v)
      case "UUID      " => UUID.fromString(v)
      case "URI       " => new java.net.URI(v)
      case "BigInteger" => BigInt(v)
      case "BigDecimal" => BigDecimal(v)
    }
  }
}
