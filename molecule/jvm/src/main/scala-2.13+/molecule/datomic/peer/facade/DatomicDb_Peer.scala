package molecule.datomic.peer.facade

import java.util.Date
import java.{lang, util}
import datomic.{Database, Peer, Util, Datom => PeerDatom}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.concurrent.{ExecutionContext, Future}

/** Datomic Db facade for peer api.
  *
  * @param peerDb
  */
case class DatomicDb_Peer(peerDb: Database) extends DatomicDb {

  def getDatomicDb: AnyRef = peerDb

  def t(implicit ec: ExecutionContext): Future[Long] = Future(peerDb.basisT())

  def tx(implicit ec: ExecutionContext): Future[Long] = t.map(t => Peer.toTx(t).asInstanceOf[Long])

  def txInstant(implicit ec: ExecutionContext): Future[Date] = tx.map(tx =>
    peerDb.pull("[:db/txInstant]", tx).get(Util.read(":db/txInstant")).asInstanceOf[Date]
  )

  def entity(conn: Conn, id: Any): DatomicEntity =
    DatomicEntity_Peer(
      peerDb.entity(id), // Datomic Entity is lazy
      conn.asInstanceOf[Conn_Peer],
      id
    )


  def pull(pattern: String, eid: Any)(implicit ec: ExecutionContext): Future[util.Map[_, _]] = Future(
    peerDb.pull(pattern, eid)
  )

  def datoms(index: Any, objects: Any*)
            (implicit ec: ExecutionContext): Future[lang.Iterable[PeerDatom]] = Future(
    peerDb.datoms(index, objects: _*)
  )

  def indexRange(
    attrId: String,
    start: Any,
    end: Any
  )(implicit ec: ExecutionContext): Future[lang.Iterable[PeerDatom]] = Future(
    peerDb.indexRange(attrId, start, end)
  )
}
