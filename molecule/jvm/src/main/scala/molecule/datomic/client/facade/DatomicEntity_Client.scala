package molecule.datomic.client.facade

import java.net.URI
import java.util.{Date, UUID, Collection => jCollection}
import clojure.lang.{MapEntry, PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import datomic.Util
import molecule.core.api.exception.EntityException
import molecule.core.util.{JavaConversions, RegexMatching}
import molecule.datomic.base.facade.DatomicEntity_Jvm
import scala.concurrent.{ExecutionContext, Future}
import scala.language.existentials

/** Datomic Entity facade for client api (peer-server/cloud/dev-local).
  *
  * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
  * @param eid  Entity id of type Object
  * @param showKW
  */
case class DatomicEntity_Client(
  conn: Conn_Client,
  eid: Any,
  showKW: Boolean = true
) extends DatomicEntity_Jvm(conn, eid) with RegexMatching with JavaConversions {

  final override def keySet(implicit ec: ExecutionContext): Future[Set[String]] =
    entityMap.map(_.keySet)

  final override def keys(implicit ec: ExecutionContext): Future[List[String]] =
    entityMap.map(_.keySet.toList)

  final override def rawValue(key: String)(implicit ec: ExecutionContext): Future[Any] = {
    key match {
      case r":[^/]+/_.+" =>
        conn.db.pull(s"[$key]", eid).map(raw =>
          Some(
            raw.asInstanceOf[PersistentArrayMap].values().iterator().next()
              .asInstanceOf[PersistentVector].asScala.toList
              .map(_.asInstanceOf[PersistentArrayMap].get(Util.read(":db/id")).asInstanceOf[Long])
          )
        )

      case k => entityMap.map(_.apply(k))
    }
  }

  def isAttrDef(entityMap: Map[String, Any]): Boolean = {
    entityMap.keys.toList.intersect(List(
      ":db/ident",
      ":db/valueType",
      ":db/cardinality",
      ":db/doc",
    )).nonEmpty
  }

  private[molecule] def toScala(
    key: String,
    vOpt: Option[Any],
    depth: Int = 1,
    maxDepth: Int = 5,
    tpe: String = "Map"
  )(implicit ec: ExecutionContext): Future[Any] = {
    def retrieve(v: Any): Future[Any] = v match {
      case Some(v)                  => Future(v)
      case s: java.lang.String      => Future(s)
      case i: java.lang.Integer     => Future(i.toLong: Long)
      case l: java.lang.Long        =>
        if (depth < maxDepth && key != ":db/id") {
          DatomicEntity_Client(conn, l)
            .asMap(depth + 1, maxDepth).map { entityMap =>
            // Return number if unintended entity map is returned
            if (
              entityMap.size == 1 // Empty entity (contains only :db/id)
                || isAttrDef(entityMap) // Attribute definitions
            ) l else entityMap
          }

        } else {
          Future(l: Long)
        }
      case f: java.lang.Float       => Future(f: Float)
      case d: java.lang.Double      => Future(d: Double)
      case b: java.lang.Boolean     => Future(b: Boolean)
      case d: Date                  => Future(d)
      case u: UUID                  => Future(u)
      case u: java.net.URI          => Future(u)
      case u: URIImpl               => Future(new URI(u.toString))
      case bi: clojure.lang.BigInt  => Future(BigInt(bi.toString))
      case bi: java.math.BigInteger => Future(BigInt(bi))
      case bd: java.math.BigDecimal => Future(BigDecimal(bd))
      case bytes: Array[Byte]       => Future(bytes)

      case kw: clojure.lang.Keyword =>
        if (showKW)
          Future(kw.toString)
        else
          conn.db.entity(conn, kw).rawValue(":db/id")

      case set: clojure.lang.PersistentHashSet =>
        Future.sequence(
          set.asScala.toList.map(v1 =>
            toScala(key, Some(v1), depth, maxDepth, tpe)
          )
        ).flatMap(sortList)

      case map0: clojure.lang.PersistentArrayMap => {
        map0 match {
          case m if m.size() == 2 && m.containsKey(ident) =>
            Future(m.get(ident).toString)

          case m if depth < maxDepth =>
            val ent = DatomicEntity_Client(
              conn,
              m.iterator().next().asInstanceOf[MapEntry].getValue
            )
            tpe match {
              case "Map" =>
                ent.asMap(depth + 1, maxDepth).map(map =>
                  if (map.contains(":db/ident"))
                    map(":db/ident") // enum
                  else
                    map
                )

              case "List" =>
                ent.asList(depth + 1, maxDepth).map {
                  case List((":db/id", _), (":db/ident", enum)) => enum
                  case other                                    => other
                }
            }

          case m =>
            val id = m.iterator().next().asInstanceOf[MapEntry].getValue.asInstanceOf[Long]
            conn.db.entity(conn, id).apply[Long](":db/ident").map(_.getOrElse(id))
        }
      }

      case vec: clojure.lang.PersistentVector =>
        Future.sequence(
          vec.asScala.toList.map(v1 =>
            toScala(key, Some(v1), depth, maxDepth, tpe)
          )
        ).flatMap(sortList)

      case col: jCollection[_] =>
        Future(
          new Iterable[Any] {
            override def iterator = new Iterator[Any] {
              private val jIter = col.iterator.asInstanceOf[java.util.Iterator[AnyRef]]
              override def hasNext = jIter.hasNext
              override def next() = if (depth < maxDepth)
                toScala(key, Some(jIter.next()), depth, maxDepth, tpe)
              else
                jIter.next()
            }
            override def isEmpty = col.isEmpty
            override def size = col.size
            override def toString = col.toString
          }
        )

      case None       => Future.failed(new EntityException("Unexpectedly received null"))
      case null       => Future.failed(new EntityException("Unexpectedly received null"))
      case unexpected => Future.failed(new EntityException(
        "Unexpected Datalog type to convert: " + unexpected.getClass.toString
      ))
    }

    vOpt match {
      case Some(v) => retrieve(v)
      case _       => rawValue(key).flatMap(retrieve)
    }
  }
}