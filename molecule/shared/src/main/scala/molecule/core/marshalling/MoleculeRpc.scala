package molecule.core.marshalling

import java.util.Date
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.TxReportRPC
import scala.concurrent.Future

trait MoleculeRpc {

  def transact(
    dbProxy: DbProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReportRPC]

  def transact(
    dbProxy: DbProxy,
    stmtsData: (String,Set[String])
  ): Future[TxReportRPC] = transact(dbProxy, stmtsData._1, stmtsData._2)


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

  def t(dbProxy: DbProxy): Future[Long] = ???
  def tx(dbProxy: DbProxy): Future[Long] = ???
  def txInstant(dbProxy: DbProxy): Future[Date] = ???

//  def entity(dbProxy: DbProxy): Future[DatomicEntity] = ???

//  def pull(pattern: String, eid: Any): Future[]

  def retract(dbProxy: DbProxy, eid: Long) = ???

}
