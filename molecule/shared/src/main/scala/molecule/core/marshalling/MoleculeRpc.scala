package molecule.core.marshalling

import java.util.Date
import molecule.core.marshalling.nodes.Obj
import molecule.datomic.base.facade.TxReportRPC
import scala.concurrent.{ExecutionContext, Future}

trait MoleculeRpc {

  def transact(
    connProxy: ConnProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReportRPC]

  def transact(
    connProxy: ConnProxy,
    stmtsData: (String, Set[String])
  ): Future[TxReportRPC] = transact(connProxy, stmtsData._1, stmtsData._2)

  def query2packed(
    connProxy: ConnProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows: Int,
    obj : Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]]
  ): Future[String]


  def getAttrValues(
    connProxy: ConnProxy,
    datalogQuery: String,
    card: Int,
    tpe: String
  ): Future[List[String]]


  def entityAttrKeys(
    connProxy: ConnProxy,
    eid: Long
  ): Future[List[String]]


  def t(connProxy: ConnProxy): Future[Long] = ???
  def tx(connProxy: ConnProxy): Future[Long] = ???
  def txInstant(connProxy: ConnProxy): Future[Date] = ???

  //  def entity(connProxy: DbProxy): Future[DatomicEntity] = ???

  //  def pull(pattern: String, eid: Any): Future[]



  def retract(connProxy: ConnProxy, eid: Long) = ???



  def touchMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchQuotedMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchListMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchListQuotedMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def asMap(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String]

  def asList(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String]

  def sortList(connProxy: ConnProxy, eid: Long, l: String): Future[String]

}
