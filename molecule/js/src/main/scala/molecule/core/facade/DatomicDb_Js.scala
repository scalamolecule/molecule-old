package molecule.core.facade

import java.util
import java.util.Date
import molecule.core.marshalling.{ConnProxy, MoleculeRpc}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.concurrent.{ExecutionContext, Future}

case class DatomicDb_Js(rpc: MoleculeRpc, connProxy: ConnProxy) extends DatomicDb {

  def getDatomicDb: AnyRef = connProxy

  def t(implicit ec: ExecutionContext): Future[Long] = {
    rpc.t(connProxy)
  }

  def tx(implicit ec: ExecutionContext): Future[Long] = {
    rpc.tx(connProxy)
  }

  def txInstant(implicit ec: ExecutionContext): Future[Date] = {
    rpc.txInstant(connProxy)
  }

  def entity(conn: Conn, eid: Any): DatomicEntity = {
    DatomicEntity_Js(conn, connProxy, eid)
  }

  def pull(pattern: String, eid: Any)(implicit ec: ExecutionContext): Future[util.Map[_, _]] = ???

}
