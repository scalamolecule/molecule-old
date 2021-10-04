package molecule.datomic.peer.facade

import java.util.{Date, List => jList, Map => jMap}
import datomic.Connection.TEMPIDS
import datomic.db.Datum
import datomic._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.util.Inspect
import scala.collection.JavaConverters._

/** Datomic TxReport facade for peer api.
  *
  * @param rawTxReport
  * @param stmts
  */
case class TxReport_Peer(
  rawTxReport: jMap[_, _],
  scalaStmts: Seq[Statement] = Nil
) extends TxReport {

  lazy val eids: List[Long] = {
    var ids = List.empty[Long]
    val tx = rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].get(0).asInstanceOf[Datom].tx().asInstanceOf[Long]
    rawTxReport.get(TEMPIDS).asInstanceOf[jMap[Any, Any]].values().forEach { tempId =>
      val eid = tempId.asInstanceOf[Long]
      if (eid != tx) {
        ids = ids :+ eid
      }
    }
    ids.distinct.sorted // newest entities last
  }

//  lazy val eidsOLD: List[Long] = {
//    val allIds = {
//      val datoms = rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].iterator
//      var ids    = Array.empty[Long]
//      var i      = 0
//      datoms.next() // skip first transaction time datom
//      while (datoms.hasNext) {
//        val datom = datoms.next.asInstanceOf[Datom]
//        if (datom.added()) // only asserted datoms
//          ids = ids :+ datom.e().asInstanceOf[Long]
//        i += 1
//      }
//      ids.toList
//    }
//    if (stmts.isEmpty) {
//      allIds.distinct
//    } else {
//      val assertStmts = stmts.filterNot(_.isInstanceOf[RetractEntity])
//
//      //      println("-------------------------------------------")
//      //      txDataRaw.map(datom2string) foreach println
//      //      println("--------")
//      //      allIds foreach println
//      //      println("--------")
//      //      stmtss foreach println
//      //      println("--------")
//      //      assertStmts foreach println
//
//      if (allIds.size != assertStmts.size)
//        throw DatomicFacadeException(
//          s"Unexpected different counts of ${allIds.size} ids and ${assertStmts.size} stmts."
//        )
//      val resolvedIds = assertStmts.zip(allIds).collect {
//        case (Add(_: TempId, _, _, _), eid)    => eid
//        case (Add("datomic.tx", _, _, _), eid) => eid
//      }.distinct.toList
//      resolvedIds
//    }
//  }

  private lazy val txDataRaw: List[Datum] =
    rawTxReport.get(Connection.TX_DATA)
      .asInstanceOf[jList[_]].asScala.toList.asInstanceOf[List[Datum]]

  private def datom2string(d: datomic.db.Datum, vMax: Int) = {
    val e = s"${d.e}" + " " * (14 - d.e.toString.length)
    val a = s"${d.a}" + " " * (4 - d.a.toString.length)
    val v = s"${d.v}" + " " * (33 - d.v.toString.length)
    s"[$e  $a  $v    ${d.tx}   ${d.added()}]"
  }

  def inspect: Unit = Inspect("TxReport", 1)(1, scalaStmts, txDataRaw)

  override def toString = {
    val vMax = txDataRaw.map(_.v.toString.length).max
    s"""TxReport {
       |  dbBefore  : $dbBefore
       |  dbBefore.t: ${dbBefore.basisT}
       |  dbAfter   : $dbAfter
       |  dbAfter.t : ${dbAfter.basisT}
       |  txData    : ${txDataRaw.map(d => datom2string(d, vMax)).mkString(",\n              ")}
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
//  lazy val instOLD: Date = dbAfter.entity(tx).get(":db/txInstant").asInstanceOf[Date]
}
