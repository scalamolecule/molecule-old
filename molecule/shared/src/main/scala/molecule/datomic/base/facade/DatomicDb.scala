package molecule.datomic.base.facade

import java.util
import java.util.Date
import molecule.datomic.base.api.DatomicEntity
import scala.concurrent.{ExecutionContext, Future}

trait DatomicDb {

  def getDatomicDb: AnyRef

  def basisT(implicit ec: ExecutionContext): Future[Long]

  def entity(conn: Conn, id: Any): DatomicEntity

  def pull(pattern: String, eid: Any)(implicit ec: ExecutionContext): Future[util.Map[_, _]]

}
