package molecule.core.facade

import java.util
import java.util.Date
import molecule.core.marshalling.{ConnProxy, MoleculeRpc}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.concurrent.{ExecutionContext, Future}

case class DatomicDb_Js(rpc: MoleculeRpc, connProxy: ConnProxy) extends DatomicDb {

  def getDatomicDb: AnyRef = connProxy

  def basisT(implicit ec: ExecutionContext): Future[Long] = {
    rpc.basisT(connProxy)
  }

  def entity(conn: Conn, eid: Any): DatomicEntity = {
    DatomicEntity_Js(conn, eid)
  }

  def pull(pattern: String, eid: Any)(implicit ec: ExecutionContext): Future[util.Map[_, _]] = ???

}
