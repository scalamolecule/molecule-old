package molecule.core.marshalling

import java.util.{UUID, List => jList}
import molecule.core.api.Molecule
import molecule.core.ast.elements.Model
import molecule.core.macros.qr.CastArrays
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

  /** Indexes to resolve marshalling for each attribute value in a row:
    *
    * - colIndex, column index of the attribute (index in output row from Datomic result)
    * - castIndex, index for cast code in jvm: molecule.datomic.base.marshalling.cast.CastLambdas.castLambdas
    * - arrayType, index for data array type in [[CastArrays.dataArrays]]
    * - arrayIndex, index within Seq of a specific type of data array
    * (two String attributes will occupy two String arrays and then have index 0 and 1 for instance)
    */
  protected lazy val flatIndexes  : List[(Int, Int, Int, Int)] = ???
  protected lazy val nestedIndexes: List[Indexes]              = ???
  protected lazy val isOptNested  : Boolean                    = ???



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


  protected def json2obj(json: String): Obj = ???
  protected def json2tpl(json: String): Tpl = ???
  protected def json2list(json: String): jList[AnyRef] = ???


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
