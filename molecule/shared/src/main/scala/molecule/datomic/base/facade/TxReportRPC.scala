package molecule.datomic.base.facade

import java.util.Date
import molecule.datomic.base.api.Datom

/** Transaction Report Proxy for marshalling between Server/Client.
  *
  * @param t
  * @param tx
  * @param txInstant
  * @param eids
  * @param txData
  * @param asString
  */
case class TxReportRPC(
  t: Long,
  tx: Long,
  txInstant: Date,
  eids: List[Long],
  txData: List[Datom],
  asString: String
) extends TxReport {


  def inspect: Unit = println(asString)

  /** String representation of raw TxReport */
  override def toString = asString
}