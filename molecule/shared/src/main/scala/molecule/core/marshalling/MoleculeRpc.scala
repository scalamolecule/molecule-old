package molecule.core.marshalling

import molecule.datomic.base.facade.{TxReport, TxReportProxy}
import scala.concurrent.Future

trait MoleculeRpc {

  def transactAsync(
    dbProxy: DbProxy,
    stmtsEdn: String
  ): Future[Either[String, TxReportProxy]]


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

}
