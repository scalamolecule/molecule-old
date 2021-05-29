package molecule.core.marshalling

import java.util.{List => jList}
import molecule.core.api.Molecule
import molecule.core.ast.elements.Model
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.TxReport
import scala.concurrent.{ExecutionContext, Future}


/** Marshalling methods for casting raw row (server) / QueryResult (client) data.
  */
abstract class Marshalling[Obj, Tpl](
  model: Model,
  queryData: (Query, Option[Query], Query, Option[Query], Option[Throwable])
) extends Molecule(model, queryData) {

  /** Indexes to resolve marshalling for each attribute value in a row:
    *
    * - colIndex, column index of the attribute (index in output row from Datomic result)
    * - castIndex, index for cast code in jvm: molecule.datomic.base.marshalling.cast.CastLambdas.castLambdas
    * - arrayType, index for data array type in [[molecule.core.macros.cast.CastArrays.dataArrays]]
    * - arrayIndex, index within Seq of a specific type of data array
    * (two String attributes will occupy two String arrays and then have index 0 and 1 for instance)
    */
  protected lazy val indexes: List[(Int, Int, Int, Int)] = ???


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


  // JVM ......................

  /** Row to object cast interface to be materialized by macro */
  protected def row2obj(row: jList[AnyRef]): Obj = ???


  /** Row to tuple cast interface to be materialized by macro */
  protected def row2tpl(row: jList[AnyRef]): Tpl = ???

}
