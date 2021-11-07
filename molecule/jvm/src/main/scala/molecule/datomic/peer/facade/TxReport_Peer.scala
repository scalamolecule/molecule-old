package molecule.datomic.peer.facade

import java.util.{Date, List => jList, Map => jMap}
import datomic.Connection.{DB_AFTER, DB_BEFORE, TEMPIDS, TX_DATA}
import datomic.db.{Datum => PeerDatom}
import datomic.{Datom => _, _}
import molecule.core.util.JavaConversions
import molecule.datomic.base.api.Datom
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

  def t: Long = dbAfter.basisT

  def tx: Long = Peer.toTx(t).asInstanceOf[Long]

  def txInstant: Date = {
    rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[PeerDatom]].get(0).v().asInstanceOf[Date]
  }

  def eids: List[Long] = {
    // Fast lookups with mutable Buffers
    // https://www.lihaoyi.com/post/BenchmarkingScalaCollections.html#lookup-performance
    var allIds           = mutable.Buffer.empty[Long]
    val datoms           = rawTxReport.get(TX_DATA).asInstanceOf[jList[PeerDatom]].iterator
    val tempIds          = rawTxReport.get(TEMPIDS).asInstanceOf[jMap[_, _]].values().asScala.toBuffer
    val tx               = datoms.next().e().asInstanceOf[Long] // Initial txInstant datom
    var txMetaData       = false
    var datom: PeerDatom = null
    var e                = 0L
    // Filter out tx meta data assertions
    while (!txMetaData && datoms.hasNext) {
      datom = datoms.next
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

  lazy val txData: List[Datom] = {
    val list = List.newBuilder[Datom]
    rawTxReport.get(TX_DATA).asInstanceOf[jList[PeerDatom]].forEach { datom =>
      list += new Datom_Peer(datom)
    }
    list.result()
  }

  def inspect: Unit = Inspect("TxReport", 1)(1, scalaStmts, txDataRaw)


  /** Get database value before transaction. */
  def dbBefore: Database = rawTxReport.get(DB_BEFORE).asInstanceOf[Database]

  /** Get database value after transaction. */
  def dbAfter: Database = rawTxReport.get(DB_AFTER).asInstanceOf[Database]


  private def txDataRaw: List[PeerDatom] = {
    val list = List.newBuilder[PeerDatom]
    rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[PeerDatom]].forEach(datom =>
      list += datom
    )
    list.result()
  }

  private def datom2string(d: datomic.db.Datum): String = {
    val e = s"${d.e}" + " " * (14 - d.e.toString.length)
    val a = s"${d.a}" + " " * (4 - d.a.toString.length)
    val v = s"${d.v}" + " " * (33 - d.v.toString.length)
    s"[$e  $a  $v    ${d.tx}   ${d.added()}]"
  }

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
}
