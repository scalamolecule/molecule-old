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
case class TxReportRPC(
  eids: List[Long],
  t: Long,
  tx: Long,
  inst: Date,
  asString: String
) extends TxReport {


  override def inspect: Unit = println(asString)

  /** String representation of raw TxReport */
  override def toString = asString
}