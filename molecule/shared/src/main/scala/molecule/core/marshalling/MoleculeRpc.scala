package molecule.core.marshalling

import molecule.datomic.base.facade.TxReportRPC
import scala.concurrent.Future

trait MoleculeRpc {


  def transactAsync(
    dbProxy: DbProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[Either[Throwable, TxReportRPC]]


  def queryAsync(
    dbProxy: DbProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows: Int,
    indexes: List[(Int, Int, Int, Int)]
  ): Future[Either[Throwable, QueryResult]]


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


  def ping(i: Int): Future[Int]

  def pong(i: Int): Future[Either[Throwable, Int]]

}
