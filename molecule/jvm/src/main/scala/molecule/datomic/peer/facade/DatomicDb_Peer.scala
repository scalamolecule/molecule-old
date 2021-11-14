package molecule.datomic.peer.facade

import java.{lang, util}
import datomic.{Database, Datom => PeerDatom}
import molecule.core.util.JavaConversions
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.concurrent.{ExecutionContext, Future}

/** Datomic Db facade for peer api.
  *
  * @param peerDb
  */
case class DatomicDb_Peer(peerDb: Database) extends DatomicDb with JavaConversions {

  def getDatomicDb: AnyRef = peerDb

  def basisT(implicit ec: ExecutionContext): Future[Long] = Future(peerDb.basisT())

  def entity(conn: Conn, id: Any): DatomicEntity =
    DatomicEntity_Peer(
      peerDb.entity(id), // lazy
      conn.asInstanceOf[Conn_Peer],
      id
    )

  def pull(pattern: String, eid: Any)(implicit ec: ExecutionContext): Future[util.Map[_, _]] = Future(
    peerDb.pull(pattern, eid)
  )

  def datoms(index: Any, objects: Any*)
            (implicit ec: ExecutionContext): Future[lang.Iterable[PeerDatom]] = Future {
    val objects1 = objects.toSeq.asInstanceOf[Seq[Object]]
    peerDb.datoms(index, objects1: _*)
  }

  def indexRange(
    attrId: String,
    start: Any,
    end: Any
  )(implicit ec: ExecutionContext): Future[lang.Iterable[PeerDatom]] = Future(
    peerDb.indexRange(attrId, start, end)
  )
}
