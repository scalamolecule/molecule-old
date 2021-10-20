package molecule.datomic.peer.facade

import java.util.{Date, List => jList, Map => jMap}
import datomic.Connection.{DB_AFTER, DB_BEFORE, TEMPIDS, TX_DATA}
import datomic._
import datomic.db.Datum
import molecule.core.util.JavaConversions
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.util.Inspect
import scala.collection.mutable

/** Datomic TxReport facade for peer api.
 *
 * @param rawTxReport
 * @param scalaStmts
 */
case class TxReport_Peer(
  rawTxReport: jMap[_, _],
  scalaStmts: Seq[Statement] = Nil
) extends TxReport with JavaConversions {

  lazy val eids: List[Long] = {
    // Fast lookups with mutable Buffers
    // https://www.lihaoyi.com/post/BenchmarkingScalaCollections.html#lookup-performance
    var allIds       = mutable.Buffer.empty[Long]
    val datoms       = rawTxReport.get(TX_DATA).asInstanceOf[jList[_]].iterator
    val tempIds      = rawTxReport.get(TEMPIDS).asInstanceOf[jMap[_, _]].values().asScala.toBuffer
    val tx           = datoms.next().asInstanceOf[Datom].e().asInstanceOf[Long] // txInstant datom
    var txMetaData   = false
    var datom: Datom = null
    var e            = 0L
    // Filter out tx meta data assertions
    while (!txMetaData && datoms.hasNext) {
      datom = datoms.next.asInstanceOf[Datom]
      e = datom.e().asInstanceOf[Long]
      if (e == tx)
        txMetaData = true
      if (
        !txMetaData
          && datom.added()
          && !allIds.contains(e)
      ) {
        if (tempIds.contains(e)) {
          allIds = allIds :+ e
        }
      }
    }
    allIds.toList
  }

  private lazy val txDataRaw: List[Datum] =
    rawTxReport.get(Connection.TX_DATA)
      .asInstanceOf[jList[_]].asScala.toList.asInstanceOf[List[Datum]]

  private def datom2string(d: datomic.db.Datum): String = {
    val e = s"${d.e}" + " " * (14 - d.e.toString.length)
    val a = s"${d.a}" + " " * (4 - d.a.toString.length)
    val v = s"${d.v}" + " " * (33 - d.v.toString.length)
    s"[$e  $a  $v    ${d.tx}   ${d.added()}]"
  }

  def inspect: Unit = Inspect("TxReport", 1)(1, scalaStmts, txDataRaw)

  override def toString = {
    s"""TxReport {
       |  dbBefore  : $dbBefore
       |  dbBefore.t: ${dbBefore.basisT}
       |  dbAfter   : $dbAfter
       |  dbAfter.t : ${dbAfter.basisT}
       |  txData    : ${txDataRaw.map(d => datom2string(d)).mkString(",\n              ")}
       |  tempIds   : ${rawTxReport.get(TEMPIDS).asInstanceOf[AnyRef]}
       |  eids      : $eids
       |}""".stripMargin
  }

  /** Get database value before transaction. */
  lazy val dbBefore: Database = rawTxReport.get(DB_BEFORE).asInstanceOf[Database]

  /** Get database value after transaction. */
  lazy val dbAfter: Database = rawTxReport.get(DB_AFTER).asInstanceOf[Database]

  lazy val t: Long = dbAfter.basisT

  lazy val tx: Long = Peer.toTx(t).asInstanceOf[Long]

  lazy val inst: Date = {
    rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].get(0).asInstanceOf[Datum].v.asInstanceOf[Date]
  }
}
