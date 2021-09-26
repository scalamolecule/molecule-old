package molecule.core.marshalling

import java.util.Date
import molecule.core.ast.elements.Model
import molecule.core.marshalling.nodes.Obj
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReportRPC}
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

trait MoleculeRpc {

  def clearConnPool(): Future[Unit]


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
    l: Seq[(Int, String, String)],
    ll: Seq[(Int, String, Seq[String])],
    lll: Seq[(Int, String, Seq[Seq[String]])],
    maxRows: Int,
    obj : Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]]
  ): Future[String]

  def index2packed(
    connProxy: ConnProxy,
    api: String,
    index: String,
    indexArgs: IndexArgs,
    attrs: Seq[String]
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


  def t(connProxy: ConnProxy): Future[Long]

  def tx(connProxy: ConnProxy): Future[Long]

  def txInstant(connProxy: ConnProxy): Future[Date]

  //  def entity(connProxy: DbProxy): Future[DatomicEntity] = ???

  //  def pull(pattern: String, eid: Any): Future[]


  // Entity api

  def retract(connProxy: ConnProxy, eid: Long) = ???

  def touchMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchQuotedMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchListMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchListQuotedMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def asMap(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String]

  def asList(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String]

  def sortList(connProxy: ConnProxy, eid: Long, l: String): Future[String]


//  def q(query: String, inputs: Any*)
//       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = ???(7)
//
//  def q(db: DatomicDb, query: String, inputs: Seq[Any])
//       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = ???(8)
//
//  def qRaw(query: String, inputs: Any*)
//          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(9)
//
//  def qRaw(db: DatomicDb, query: String, inputs0: Seq[Any])
//          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(10)
//
//  def query(model: Model, query: Query)
//           (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(11)
//
//  def _query(model: Model, query: Query, _db: Option[DatomicDb])
//            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(12)
//
//  def _index(model: Model)
//            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(13)
//
//  def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = ???(14)
//
//  def inspect(
//    header: String,
//    threshold: Int,
//    max: Int,
//    showStackTrace: Boolean,
//    maxLevel: Int,
//    showBi: Boolean
//  )(id: Int, params: Any*): Unit = ???

}
