package molecule.core.marshalling

import molecule.datomic.base.facade.TxReportRPC
import scala.concurrent.Future

trait MoleculeRpc {

  def transact(
    dbProxy: DbProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReportRPC]


  def query(
    dbProxy: DbProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows: Int,
    indexes: List[(Int, Int, Int, Int)]
  ): Future[QueryResult]


  def getAttrValues(
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
