package molecule.core.marshalling

import java.util.Date
import molecule.core.dto.SchemaAttr
import molecule.core.marshalling.ast.nodes.Obj
import molecule.core.marshalling.ast.{ConnProxy, IndexArgs, SortCoordinate}
import molecule.datomic.base.facade.{TxReport, TxReportRPC}
import sloth.PathName
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
    l: Seq[(Int, String, String)],
    ll: Seq[(Int, String, Seq[String])],
    lll: Seq[(Int, String, Seq[Seq[String]])],
    maxRows: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[String]

  def schemaHistoryQuery2packed(
    connProxy: ConnProxy,
    datalogQuery: String,
    obj: Obj,
    schemaAttrs: Seq[SchemaAttr],
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[String]

  def index2packed(
    connProxy: ConnProxy,
    api: String,
    index: String,
    indexArgs: IndexArgs,
    attrs: Seq[String],
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[String]


  // Schema ...............................

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


  def changeAttrName(connProxy: ConnProxy,curName: String, newName: String): Future[TxReportRPC]
  def retireAttr(connProxy: ConnProxy,attrName: String): Future[TxReportRPC]

  def changeNamespaceName(connProxy: ConnProxy,curName: String, newName: String): Future[TxReportRPC]
  def retireNamespace(connProxy: ConnProxy,nsName: String): Future[TxReportRPC]

  def changePartitionName(connProxy: ConnProxy,curName: String, newName: String): Future[TxReportRPC]
  def retirePartition(connProxy: ConnProxy,partName: String): Future[TxReportRPC]

  def retractSchemaOption(connProxy: ConnProxy, attr: String, option: String): Future[TxReportRPC]

  def getEnumHistory(connProxy: ConnProxy): Future[List[(String, Int, Long, Date, String, Boolean)]]
  def retractEnum(connProxy: ConnProxy, enumString: String): Future[TxReportRPC]

  // Entity api ....................................

  def rawValue(connProxy: ConnProxy, eid: Long, attr: String): Future[String]

  def asMap(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String]

  def asList(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String]

  def attrs(connProxy: ConnProxy, eid: Long): Future[List[String]]

  def apply(connProxy: ConnProxy, eid: Long, attr: String): Future[String]

  @PathName("apply-many") // sloth wants this for overloaded method name
  def apply(connProxy: ConnProxy, eid: Long, attrs: List[String]): Future[List[String]]

  def graphDepth(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]

  def graphCode(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String]


  // Connection pool ...............................

  def clearConnPool: Future[Unit]


  // Helpers ...............................

  def basisT(connProxy: ConnProxy): Future[Long]
}
