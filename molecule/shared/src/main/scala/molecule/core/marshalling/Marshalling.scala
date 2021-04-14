package molecule.core.marshalling

import java.util.{List => jList}


/** Marshalling methods for casting raw row (server) / QueryResult (client) data.
  */
trait Marshalling[Obj, Tpl] {

  lazy val isJsPlatform: Boolean = ???


  // Client side ......................

  lazy val moleculeWire: QueryExecutor = ???

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


  // Server side ......................

  /** Row to object cast interface to be materialized by macro */
  protected def row2obj(row: jList[AnyRef]): Obj = ???

  /** Row to tuple cast interface to be materialized by macro */
  protected def row2tpl(row: jList[AnyRef]): Tpl = ???

  //  // Row to json build-up with fast StringBuilder to be materialized by macro
  //  protected def row2json(sb: StringBuilder, row: jList[AnyRef]): StringBuilder = ???

}
