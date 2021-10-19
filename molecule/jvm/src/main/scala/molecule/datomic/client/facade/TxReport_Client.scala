package molecule.datomic.client.facade

import java.util.{Date, ArrayList => jArrayList, List => jList}
import clojure.lang.Keyword
import datomic.Peer
import datomic.db.DbId
import datomicScala.client.api.Datom
import datomicScala.client.api.sync.{Db, TxReport => clientTxReport}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.JavaConversions
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.facade.exception.DatomicFacadeException
import molecule.datomic.base.util.Inspect

/** Datomic TxReport facade for client api (peer-server/cloud/dev-local).
  *
  * @param clientTxReport
  * @param scalaStmts
  */
case class TxReport_Client(
  clientTxReport: clientTxReport,
  scalaStmts: Seq[Statement] = Nil,
  javaStmts: jList[_] = new jArrayList[Any](0)
) extends TxReport with JavaConversions {


  lazy val eids: List[Long] = {
    var allIds = List.empty[Long]
    val datoms = clientTxReport.txData.iterator
    datoms.next() // skip automatically created first transaction time datom
    while (datoms.hasNext) {
      val datom = datoms.next
      if (datom.added) // only asserted datoms
        allIds = allIds :+ datom.e
    }
    if (scalaStmts.nonEmpty) {
      val addStmts = scalaStmts.filter(_.isInstanceOf[Add])
      if (allIds.size != addStmts.size)
        throw DatomicFacadeException(
          s"Unexpected different counts of ${allIds.size} ids and ${addStmts.size} scala stmts."
        )
      val resolvedIds = addStmts.zip(allIds).foldLeft(Seq.empty[Long], false) {
        case ((acc, true), _) /* ignore tx meta data */     => (acc, true)
        case ((acc, _), (Add("datomic.tx", _, _, _), _))    => (acc, true)
        case ((acc, false), (Add(_: TempId, _, _, _), eid)) => (acc :+ eid, false)
        case ((acc, isTxMetaData), _)                       => (acc, isTxMetaData)
      }._1.distinct.toList

      //      println("------------------------------------------")
      //      txDataRaw.map(datom2string) foreach println
      //      println("--------")
      //      allIds foreach println
      //      println("--------")
      //      scalaStmts foreach println
      //      println("--------")
      //      addStmts foreach println
      //      println("--------")
      //      resolvedIds foreach println

      resolvedIds
    } else if (!javaStmts.isEmpty) {
      val addKw = Keyword.intern("db", "add")
      val addEs = javaStmts.asScala.toList.map(_.asInstanceOf[jList[Any]]).filter(_.get(0) == addKw).map(_.get(1))
      if (allIds.size != addEs.size)
        throw DatomicFacadeException(
          s"Unexpected different counts of ${allIds.size} ids and ${addEs.size} java stmts."
        )
      addEs.zip(allIds).foldLeft(Seq.empty[Long], false) {
        case ((acc, true), _)              => (acc, true) // ignore tx meta data
        case ((acc, _), ("datomic.tx", _)) => (acc, true)
        case ((acc, false), (_: DbId, eid))      => (acc :+ eid, false)
        case ((acc, isTxMetaData), _)      => (acc, isTxMetaData)
      }._1.distinct.toList
    } else {
      throw DatomicFacadeException(
        s"Can't extract eids from tx report without Scala/Java statements."
      )
    }
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
       |  tempids   : ${clientTxReport.tempIds}
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
