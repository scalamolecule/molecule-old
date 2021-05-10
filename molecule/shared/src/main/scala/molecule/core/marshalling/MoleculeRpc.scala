package molecule.core.marshalling

import molecule.datomic.base.facade.TxReportRPC
import scala.concurrent.{ExecutionContext, Future}

trait MoleculeRpc {

  /** Clear server Conn/Db cache
    *
    * @return Boolean confirmation
    */
  def clearCache: Future[Boolean]


  def transactAsync(
    dbProxy: DbProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[Either[String, TxReportRPC]]


  def queryAsync(
    dbProxy: DbProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows: Int,
    indexes: List[(Int, Int, Int, Int)]
  ): Future[Either[String, QueryResult]]


  def getAttrValuesAsync(
    dbProxy: DbProxy,
    datalogQuery: String,
    card: Int,
    tpe: String
  ): Future[List[String]]


  def entityAttrKeys(
    dbProxy: DbProxy,
    eid: Long
  ): Future[List[String]]

}
