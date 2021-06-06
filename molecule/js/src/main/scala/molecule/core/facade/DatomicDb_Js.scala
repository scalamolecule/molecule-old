package molecule.core.facade

import java.util
import java.util.Date
import molecule.core.marshalling.{DbProxy, MoleculeRpc}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.concurrent.{ExecutionContext, Future}

case class DatomicDb_Js(rpc: MoleculeRpc, dbProxy: DbProxy) extends DatomicDb {

  def getDatomicDb: AnyRef = dbProxy

  def t(implicit ec: ExecutionContext): Future[Long] = {
    rpc.t(dbProxy)
  }

  def tx(implicit ec: ExecutionContext): Future[Long] = {
    rpc.tx(dbProxy)
  }

  def txInstant(implicit ec: ExecutionContext): Future[Date] = {
    rpc.txInstant(dbProxy)
  }

  def entity(conn: Conn, eid: Any): DatomicEntity = {
    DatomicEntity_Js(conn, dbProxy, eid)
  }

  def pull(pattern: String, eid: Any)(implicit ec: ExecutionContext): Future[util.Map[_, _]] = ???

}
