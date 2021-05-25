package molecule.datomic.client.facade

import java.util
import java.util.Date
import java.util.stream.{Stream => jStream}
import datomic.{Peer, Util}
import datomicScala.client.api.sync.Db
import datomicScala.client.api.{Datom => ClientDatom}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/** Datomic Db facade for client api (peer-server/cloud/dev-local).
 *
 * @param clientDb
 */
case class DatomicDb_Client(clientDb: Db) extends DatomicDb {

  def getDatomicDb: AnyRef = clientDb.datomicDb

  def t(implicit ec: ExecutionContext): Future[Long] = Future(clientDb.t)

  def tx(implicit ec: ExecutionContext): Future[Long] = t.map(t => Peer.toTx(t).asInstanceOf[Long])

  def txInstant(implicit ec: ExecutionContext): Future[Date] = tx.map(tx =>
    clientDb.pull("[:db/txInstant]", tx).get(Util.read(":db/txInstant")).asInstanceOf[Date]
  )

  def entity(conn: Conn, id: Any): DatomicEntity =
    DatomicEntity_Client(conn.asInstanceOf[Conn_Client], id)


  def pull(pattern: String, eid: Any)
          (implicit ec: ExecutionContext): Future[util.Map[_, _]] = Future(
    clientDb.pull(pattern, eid)
  )

  def datoms(
    index: String,
    components: Seq[Any] = Nil,
    timeout: Int = 0,
    offset: Int = 0,
    limit: Int = 1000
  )(implicit ec: ExecutionContext): Future[jStream[ClientDatom]] = Future(
    clientDb.datoms(index, components.asJava, timeout, offset, limit)
  )

  def indexRange(
    attrId: String,
    startValue: Option[Any], // inclusive
    endValue: Option[Any], // exclusive
    timeout: Int = 0,
    offset: Int = 0,
    limit: Int = 1000
  )(implicit ec: ExecutionContext): Future[jStream[ClientDatom]] = Future(
    clientDb.indexRange(attrId, startValue, endValue, timeout, offset, limit)
  )
}
