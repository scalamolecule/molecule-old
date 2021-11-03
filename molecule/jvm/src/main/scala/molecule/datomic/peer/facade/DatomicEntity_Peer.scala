package molecule.datomic.peer.facade

import java.util.{Date, UUID, Collection => jCollection}
import molecule.core.api.exception.EntityException
import molecule.datomic.base.facade.DatomicEntity_Jvm
import scala.concurrent.{ExecutionContext, Future}
import scala.language.existentials
import scala.util.{Failure, Success}


/** Datomic Entity facade for the Datomic Peer api.
 *
 * @param datomicEntity
 * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
 * @param eid  Entity id of type Object
 * @param showKW
 */
case class DatomicEntity_Peer(
  datomicEntity: datomic.Entity,
  conn: Conn_Peer,
  eid: Any,
  showKW: Boolean = true
) extends DatomicEntity_Jvm(conn, eid) {


  private[molecule] final override def rawValue(
    kw: String
  )(implicit ec: ExecutionContext): Future[Any] =
    Future(datomicEntity.get(kw))

  final def attrs(implicit ec: ExecutionContext): Future[List[String]] =
    Future(datomicEntity.keySet().asScala.toList)

  private[molecule] final def toScala(
    attr: String,
    vOpt: Option[Any],
    depth: Int = 1,
    maxDepth: Int = 5,
    tpe: String = "Map"
  )(implicit ec: ExecutionContext): Future[Any] = {
    def retrieve(v: Any): Future[Any] = v match {
      case s: java.lang.String      => Future(s)
      case i: java.lang.Integer     => Future(i.toLong: Long)
      case l: java.lang.Long        => Future(l: Long)
      case d: java.lang.Double      => Future(d: Double)
      case b: java.lang.Boolean     => Future(b: Boolean)
      case d: Date                  => Future(d)
      case u: UUID                  => Future(u)
      case u: java.net.URI          => Future(u)
      case bi: clojure.lang.BigInt  => Future(BigInt(bi.toString))
      case bi: java.math.BigInteger => Future(BigInt(bi))
      case bd: java.math.BigDecimal => Future(BigDecimal(bd))
      case bytes: Array[Byte]       => Future(bytes)
      case kw: clojure.lang.Keyword =>
        if (showKW)
          Future(kw.toString)
        else
          conn.db.flatMap(_.entity(conn, kw).rawValue(":db/id"))

      case e: datomic.Entity =>
        if (depth < maxDepth) {
          Future(e.get(":db/id")).flatMap { eid =>
            val ent = DatomicEntity_Peer(e, conn, eid)
            tpe match {
              case "Map"  => ent.asMap(depth + 1, maxDepth)
              case "List" => ent.asList(depth + 1, maxDepth)
            }
          }
        } else {
          Future(e.get(":db/id").asInstanceOf[Long])
        }

      case set: clojure.lang.PersistentHashSet =>
        val (card, attrTpe) = attrDefinitions(attr)
        card match {
          case 3 =>
            getTypedValue(card, attrTpe, set.asScala.toList) match {
              case Success(v)   => Future(v)
              case Failure(exc) => Future.failed(exc)
            }

          case _ => Future.sequence(set.asScala.toSet.map(retrieve))
        }

      case vec: clojure.lang.PersistentVector =>
        Future.sequence(
          vec.asScala.toList.map(retrieve)
        )

      case col: jCollection[_] =>
        Future(
          new Iterable[Any] {
            override def iterator: Iterator[Any] = new Iterator[Any] {
              private val jIter = col.iterator.asInstanceOf[java.util.Iterator[AnyRef]]
              override def hasNext = jIter.hasNext
              override def next() = if (depth < maxDepth)
                retrieve(jIter.next())
              else
                jIter.next()
            }
            override def isEmpty = col.isEmpty
            override def size = col.size
            override def toString = col.toString
          }
        )

      case None       => Future.failed(EntityException("Unexpectedly received null"))
      case null       => Future.failed(EntityException("Unexpectedly received null"))
      case unexpected => Future.failed(EntityException(
        "Unexpected Datalog type to convert: " + unexpected.getClass.toString
      ))
    }

    vOpt match {
      case Some(v) => retrieve(v)
      case _       => rawValue(attr).flatMap(retrieve)
    }
  }
}