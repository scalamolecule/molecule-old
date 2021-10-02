package molecule.datomic.peer.facade

import java.util.{Date, List => jList, Map => jMap}
import datomic.Connection.TEMPIDS
import datomic.db.{Datum, DbId}
import datomic._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.facade.exception.DatomicFacadeException
import molecule.datomic.base.ops.QueryOps
import molecule.datomic.base.util.Inspect
import scala.concurrent.Future
import scala.jdk.CollectionConverters._

/** Datomic TxReport facade for peer api.
  *
  * @param rawTxReport
  * @param scalaStmts
  */
case class TxReport_Peer(
  rawTxReport: jMap[_, _],
  scalaStmts: Seq[Statement] = Nil
) extends TxReport {

  lazy val eids: List[Long] = {
    var ids = List.empty[Long]
    val tx = rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].get(0).asInstanceOf[Datom].tx().asInstanceOf[Long]

//    println("================= tx " + tx)
//    println(rawTxReport.get(TEMPIDS).asInstanceOf[jMap[Any, Any]].values())


    rawTxReport.get(TEMPIDS).asInstanceOf[jMap[Any, Any]].values().forEach { tempId =>
      val eid = tempId.asInstanceOf[Long]
//      println("eid " + eid)
      if (eid != tx) {
        ids = ids :+ eid
      }
    }

//    println(ids.distinct.sorted)

    ids.distinct.sorted // newest entities last

  }

//  lazy val eids2: List[Long] = if (scalaStmts.isEmpty) {
//    var ids = List.empty[Long]
//    val tx = rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].get(0).asInstanceOf[Datom].tx().asInstanceOf[Long]
//
//
//    println("================= tx " + tx)
//
//    println(rawTxReport.get(TEMPIDS).asInstanceOf[jMap[Any, Any]].values())
//
//
//    rawTxReport.get(TEMPIDS).asInstanceOf[jMap[Any, Any]].values().forEach { tempId =>
//      val eid = tempId.asInstanceOf[Long]
//      println("eid " + eid)
//      if (eid != tx) {
//        ids = ids :+ eid
//      }
//    }
//
//    println(ids.distinct.sorted)
//
//    ids.distinct.sorted // newest entities last
//
//  } else {
//
//    println(rawTxReport.get(TEMPIDS).asInstanceOf[jMap[Any, Any]].values())
//
//
//    val datoms = rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].iterator
//    var ids    = List.empty[Long]
//    var i      = 0
//    // skip first transaction time datom
//    val tx = datoms.next().asInstanceOf[Datom].tx().asInstanceOf[Long]
//    while (datoms.hasNext) {
//      val datom = datoms.next.asInstanceOf[Datom]
//      val eid   = datom.e().asInstanceOf[Long]
//      if (datom.added() && eid != tx) {
//        // Only asserted datoms and not the tx entity itself
//        ids = ids :+ eid
//      }
//      i += 1
//    }
//
//    // Filter out retractions and the transaction entity itself
//    val assertStmts = scalaStmts.filter {
//      case _: RetractEntity           => false
//      case Add("datomic.tx", _, _, _) => false
//      case _                          => true
//    }
//
//    if (ids.length != assertStmts.size) {
//      throw DatomicFacadeException(
//        s"Unexpected different counts of ${ids.length} ids and ${assertStmts.length} stmts."
//      )
//    }
//
//    ids.distinct.sorted // newest entities last
//  }

  private lazy val txDataRaw: List[Datum] =
    rawTxReport.get(Connection.TX_DATA).asInstanceOf[jList[_]].asScala.toList.asInstanceOf[List[Datum]]

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
}
