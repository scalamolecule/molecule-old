package molecule.datomic.peer.facade

import java.util.{Date, ArrayList => jArrayList, List => jList, Map => jMap}
import clojure.lang.Keyword
import datomic.Connection.TEMPIDS
import datomic._
import datomic.db.{Datum, DbId}
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
  scalaStmts: Seq[Statement] = Nil,
  javaStmts: jList[_] = new jArrayList[Any](0)
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
    if (scalaStmts.nonEmpty) {
      val addStmts = scalaStmts.filter(_.isInstanceOf[Add])
      if (allIds.size != addStmts.size)
        throw DatomicFacadeException(
          s"Unexpected different counts of ${allIds.size} ids and ${addStmts.size} scala stmts."
        )
      val resolvedIds = addStmts.zip(allIds).foldLeft(Seq.empty[Long], false) {
        case ((acc, true), _)                               => (acc, true) // ignore tx meta data
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
        case ((acc, true), _)               => (acc, true) // ignore tx meta data
        case ((acc, _), ("datomic.tx", _))  => (acc, true)
        case ((acc, false), (_: DbId, eid)) => (acc :+ eid, false)
        case ((acc, isTxMetaData), _)       => (acc, isTxMetaData)
      }._1.distinct.toList
    } else {
      throw DatomicFacadeException(
        s"Can't extract eids from tx report without Scala/Java statements."
      )
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
