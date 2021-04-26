package molecule.datomic.base.facade

import java.util.Date

/** Transaction Report Proxy for marshalling between Server/Client.
  *
  * @param eids
  * @param t
  * @param tx
  * @param inst
  * @param asString
  */
case class TxReportProxy(
  eids: List[Long],
  t: Long,
  tx: Long,
  inst: Date,
  asString: String
) {

  /** Get Set of affected entity ids from transaction. */
  def eidSet: Set[Long] = eids.toSet

  /** Convenience method to get single affected entity id from transaction.
    *
    * Often useful when you know only one entity was affected:
    * {{{
    *   val benId = Person.name("Ben").eid
    *
    *   // We could have said
    *   val lizId = Person.name("Liz").eids.head
    * }}}
    *
    * @return
    */
  def eid: Long = eids.head

  /** String representation of raw TxReport */
  override def toString = asString
}
