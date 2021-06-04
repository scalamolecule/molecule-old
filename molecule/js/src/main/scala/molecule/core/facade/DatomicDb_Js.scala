package molecule.core.facade

import java.util
import java.util.Date
import molecule.core.marshalling.DbProxy
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.concurrent.{ExecutionContext, Future}

case class DatomicDb_Js(dbProxy: DbProxy) extends DatomicDb {

  def getDatomicDb: AnyRef = dbProxy

  def t(implicit ec: ExecutionContext): Future[Long] = ???

  def tx(implicit ec: ExecutionContext): Future[Long] = ???

  def txInstant(implicit ec: ExecutionContext): Future[Date] = ???

  def entity(conn: Conn, id: Any): DatomicEntity = ???

  def pull(pattern: String, eid: Any)(implicit ec: ExecutionContext): Future[util.Map[_, _]] = ???
}
