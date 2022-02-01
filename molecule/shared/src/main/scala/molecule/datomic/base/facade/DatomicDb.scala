package molecule.datomic.base.facade

import java.util
import java.util.Date
import molecule.datomic.base.api.DatomicEntity
import scala.concurrent.{ExecutionContext, Future}

/** Minimal facade to immutable Datomic database instance. */
trait DatomicDb {

  def getDatomicDb: AnyRef

  private[molecule] def entity(conn: Conn, id: Any): DatomicEntity


  /** Get basis time t of current Datomic database */
  def basisT(implicit ec: ExecutionContext): Future[Long]


  /** Query Datomic database for raw java data with pull pattern.
   *
   * Only implemented on jvm platform.
   *
   * @param pattern
   * @param eid
   * @param ec
   * @return java tx report Map
   */
  def pull(pattern: String, eid: Any)(implicit ec: ExecutionContext): Future[util.Map[_, _]]
}
