package molecule.datomic.client.facade

import java.util
import java.util.Date
import java.util.stream.{Stream => jStream}
import datomic.{Peer, Util}
import datomicScala.client.api.sync.Db
import datomicScala.client.api.{Datom => ClientDatom}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.JavaConversions
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.concurrent.{ExecutionContext, Future}

/** Datomic Db facade for client api (peer-server/cloud/dev-local).
  *
  * @param clientDb
  */
case class DatomicDb_Client(clientDb: Db) extends DatomicDb with JavaConversions {

  def getDatomicDb: AnyRef = clientDb.datomicDb

  def t(implicit ec: ExecutionContext): Future[Long] = Future(clientDb.t)

  def tx(implicit ec: ExecutionContext): Future[Long] = t.map(t => Peer.toTx(t).asInstanceOf[Long])

  def txInstant(implicit ec: ExecutionContext): Future[Date] = tx.map(tx =>
    clientDb.pull("[:db/txInstant]", tx).get(Util.read(":db/txInstant")).asInstanceOf[Date]
  )

  def entity(conn: Conn, id: Any): DatomicEntity =
    DatomicEntity_Client(conn.asInstanceOf[Conn_Client], id)


  def pull(pattern: String, eid: Any)
          (implicit ec: ExecutionContext): Future[util.Map[_, _]] = Future {
    try {
      clientDb.pull(pattern, eid) // todo: add timeout?
    } catch {
      // Recover from cyclic graph stack overflows
      case _: StackOverflowError => throw MoleculeException("stackoverflow")
      case e: Throwable          => throw e
    }
  }

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
