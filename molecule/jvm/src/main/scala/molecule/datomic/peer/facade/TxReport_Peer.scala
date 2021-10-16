package molecule.datomic.peer.facade

import java.util.{Date, List => jList, Map => jMap}
import datomic.Connection.TEMPIDS
import datomic._
import datomic.db.Datum
import molecule.core.util.JavaConversions
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.facade.exception.DatomicFacadeException
import molecule.datomic.base.util.Inspect

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
    var allIds = List.empty[Long]
    val datoms = rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].iterator
    datoms.next() // skip automatically created first transaction time datom
    while (datoms.hasNext) {
      val datom = datoms.next.asInstanceOf[Datom]
      if (datom.added()) // only asserted datoms
        allIds = allIds :+ datom.e().asInstanceOf[Long]
    }
    if (scalaStmts.isEmpty) {
      allIds.distinct
    } else {
      val assertStmts = scalaStmts.filterNot(_.isInstanceOf[RetractEntity])
      if (allIds.size != assertStmts.size)
        throw new DatomicFacadeException(
          s"Unexpected different counts of ${allIds.size} ids and ${assertStmts.size} stmts."
        )
      val resolvedIds = assertStmts.zip(allIds).collect {
        case (Add(_: TempId, _, _, _), eid) => eid
      }.distinct.toList
      resolvedIds
    }
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
       |  tempids   : ${rawTxReport.get(TEMPIDS).asInstanceOf[AnyRef]}
       |  eids      : $eids
       |}""".stripMargin
  }

  /** Get database value before transaction. */
  lazy val dbBefore: Database = rawTxReport.get(Connection.DB_BEFORE).asInstanceOf[Database]

  /** Get database value after transaction. */
  lazy val dbAfter: Database = rawTxReport.get(Connection.DB_AFTER).asInstanceOf[Database]

  lazy val t: Long = dbAfter.basisT

  lazy val tx: Long = Peer.toTx(t).asInstanceOf[Long]

  lazy val inst: Date = {
    rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].get(0).asInstanceOf[Datum].v.asInstanceOf[Date]
  }
}
