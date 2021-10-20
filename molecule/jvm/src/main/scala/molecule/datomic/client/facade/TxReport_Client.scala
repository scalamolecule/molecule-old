package molecule.datomic.client.facade

import java.util.Date
import datomic.Peer
import datomicScala.client.api.Datom
import datomicScala.client.api.sync.{Db, TxReport => clientTxReport}
import molecule.core.util.JavaConversions
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.util.Inspect
import scala.collection.mutable

/** Datomic TxReport facade for client api (peer-server/cloud/dev-local).
 *
 * @param clientTxReport
 * @param scalaStmts
 */
case class TxReport_Client(
  clientTxReport: clientTxReport,
  scalaStmts: Seq[Statement] = Nil
) extends TxReport with JavaConversions {

  lazy val eids: List[Long] = {
    // Fast lookups with mutable Buffers
    // https://www.lihaoyi.com/post/BenchmarkingScalaCollections.html#lookup-performance
    var allIds       = mutable.Buffer.empty[Long]
    val datoms       = clientTxReport.txData.iterator
    val tempIds      = clientTxReport.tempIds.values().asScala.toBuffer
    val tx           = datoms.next().e // txInstant datom
    var txMetaData   = false
    var datom: Datom = null
    var e            = 0L
    // Filter out tx meta data assertions
    while (!txMetaData && datoms.hasNext) {
      datom = datoms.next
      e = datom.e
      if (e == tx)
        txMetaData = true
      if (
        !txMetaData
          && datom.added
          && !allIds.contains(e)
      ) {
        if (tempIds.contains(e)) {
          allIds = allIds :+ e
        }
      }
    }
    allIds.toList
  }

  private lazy val txDataRaw: List[Datom] = clientTxReport.txData.iterator().asScala.toList

  private def datom2string(d: Datom): String = {
    val e = s"${d.e}" + " " * (14 - d.e.toString.length)
    val a = s"${d.a}" + " " * (4 - d.a.toString.length)
    val v = s"${d.v}" + " " * (33 - d.v.toString.length)
    s"[$e  $a  $v    ${d.tx}   ${d.added}]"
  }


  def inspect: Unit = Inspect("TxReport", 1)(1, scalaStmts, this)

  override def toString =
    s"""TxReport {
       |  dbBefore  : $dbBefore
       |  dbBefore.t: ${dbBefore.t}
       |  dbAfter   : $dbAfter
       |  dbAfter.t : ${dbAfter.t}
       |  txData    : ${txDataRaw.map(datom2string).mkString("\n              ")}
       |  tempIds   : ${clientTxReport.tempIds}
       |  eids      : $eids
       |}""".stripMargin

  def printEidStats(): String = {
    s"""
       |
       |  txData    : ${txDataRaw.map(datom2string).mkString("\n              ")}
       |  tempids   : ${clientTxReport.tempIds}""".stripMargin
  }

  /** Get database value before transaction. */
  lazy val dbBefore: Db = clientTxReport.dbBefore

  /** Get database value after transaction. */
  lazy val dbAfter: Db = clientTxReport.dbAfter

  lazy val t: Long = dbAfter.t

  lazy val tx: Long = Peer.toTx(t).asInstanceOf[Long]

  lazy val inst: Date = clientTxReport.txData.iterator().next().v.asInstanceOf[Date]
}
