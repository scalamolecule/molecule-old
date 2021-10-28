package molecule.core.marshalling

import java.util.Date
import molecule.core.ast.elements.Model
import molecule.core.marshalling.nodes.Obj
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReportRPC}
import sloth.PathName
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
    obj: Obj,
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


  def getEntityAttrKeys(
    connProxy: ConnProxy,
    query: String
  ): Future[List[String]]


  def t(connProxy: ConnProxy): Future[Long]

  def tx(connProxy: ConnProxy): Future[Long]

  def txInstant(connProxy: ConnProxy): Future[Date]


  // Entity api ....................................

  def rawValue(connProxy: ConnProxy, eid: Long, attr: String): Future[String]

  def attrs(connProxy: ConnProxy, eid: Long): Future[List[String]]

  def apply(connProxy: ConnProxy, eid: Long, attr: String): Future[String]

  @PathName("apply-many") // sloth wants this for overloaded method name
  def apply(connProxy: ConnProxy, eid: Long, attrs: List[String]): Future[List[String]]

  def touchMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchQuotedMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchListMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def touchListQuotedMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def asMap(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String]

  def asList(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String]
}
