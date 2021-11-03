package molecule.datomic.client.facade

import java.net.URI
import java.util.{Date, UUID, Collection => jCollection}
import clojure.lang.{Keyword, MapEntry, PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import datomic.Util
import datomicClient.anomaly.Fault
import molecule.core.api.exception.EntityException
import molecule.core.exceptions.MoleculeException
import molecule.core.util.{JavaConversions, RegexMatching}
import molecule.datomic.base.facade.DatomicEntity_Jvm
import scala.concurrent.{ExecutionContext, Future}
import scala.language.existentials
import scala.util.{Failure, Success}

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


  final private def mapOneLevel(implicit ec: ExecutionContext): Future[Map[String, Any]] = {
    conn.query(s"[:find ?a1 ?v :where [$eid ?a ?v][?a :db/ident ?a1]]").map(_
      .map(l => l.head.toString -> l(1))
      .toMap + (":db/id" -> eid)
    )
  }

  final private def entityMap(implicit ec: ExecutionContext): Future[Map[String, Any]] = {
    var buildMap = Map.empty[String, Any]
    for {
      db <- conn.db
      vs <- db.pull("[*]", eid)
    } yield {
      vs.forEach {
        case (k, v) => buildMap = buildMap + (k.toString -> v)
      }
      buildMap
    }
    conn.db.flatMap { db =>
      db.pull("[*]", eid)
        .map { vs =>
          vs.forEach {
            case (k, v) => buildMap = buildMap + (k.toString -> v)
          }
          buildMap
        }.recoverWith {
        // Fetch top level only for cyclic graph stack overflows
        case MoleculeException("stackoverflow", _)                    => mapOneLevel
        case Fault("java.lang.StackOverflowError with empty message") => mapOneLevel
        case unexpected                                               => throw unexpected
      }
    }
  }

  private[molecule] final override def rawValue(
    kw: String
  )(implicit ec: ExecutionContext): Future[Any] = {
    kw match {
      // Backref
      case r":[^/]+/_.+" => conn.db.flatMap { db =>
        db.pull(s"[$kw]", eid).map(raw =>
          Some(
            raw.asInstanceOf[PersistentArrayMap].values().iterator().next()
              .asInstanceOf[PersistentVector].asScala.toList
              .map(_.asInstanceOf[PersistentArrayMap].get(Util.read(":db/id")).asInstanceOf[Long])
              .toSet // Set of card-many back refs
          )
        )
      }

      case k => entityMap.map(_.apply(k))
    }
  }

  final def attrs(implicit ec: ExecutionContext): Future[List[String]] =
    entityMap.map(_.keySet.toList)


  def isAttrDef(attrs: List[String]): Boolean = {
    attrs.intersect(List(
      ":db/ident",
      ":db/valueType",
      ":db/cardinality",
      ":db/doc",
    )).nonEmpty
  }

  private[molecule] def toScala(
    attr: String,
    vOpt: Option[Any],
    depth: Int = 1,
    maxDepth: Int = 5,
    tpe: String = "Map"
  )(implicit ec: ExecutionContext): Future[Any] = {
    def retrieve(v: Any): Future[Any] =
      v match {
        case Some(v)                  => Future(v)
        case s: java.lang.String      => Future(s)
        case i: java.lang.Integer     => Future(i.toLong: Long)
        case l: java.lang.Long        =>
          if (depth < maxDepth && attr != ":db/id") {
            val ent = DatomicEntity_Client(conn, l)
            tpe match {
              case "Map"  => ent.asMap(depth + 1, maxDepth).map { entityMap =>
                // Return eid if unintended entity map is returned
                if (
                  entityMap.size == 1 // Empty entity (contains only :db/id)
                    || isAttrDef(entityMap.keys.toList) // Attribute definitions
                ) l else entityMap
              }
              case "List" =>
                ent.asList(depth + 1, maxDepth).map { entityList =>
                  // Return eid if unintended entity list is returned
                  if (
                    entityList.size == 1 // Empty entity (contains only :db/id)
                      || isAttrDef(entityList.map(_._1)) // Attribute definitions
                  ) l else entityList
                }
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
            conn.db.flatMap(_.entity(conn, kw).rawValue(":db/id"))

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

        case map0: clojure.lang.PersistentArrayMap =>
          val ident = Keyword.intern("db", "ident")
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
                    if (map.contains(":db/ident")) {
                      map(":db/ident") // enum
                    } else {
                      map
                    }
                  )

                case "List" =>
                  ent.asList(depth + 1, maxDepth).map {
                    case List((":db/id", _), (":db/ident", enum)) => enum
                    case other                                    => other
                  }
              }

            case m =>
              val id = m.iterator().next().asInstanceOf[MapEntry].getValue.asInstanceOf[Long]
              conn.db.flatMap { db =>
                db.entity(conn, id).apply[Long](":db/ident").map(_.getOrElse(id))
              }
          }

        case vec: clojure.lang.PersistentVector =>
          val (card, attrTpe) = attrDefinitions(attr)
          card match {
            case 3 => getTypedValue(card, attrTpe, vec.asScala.toList) match {
              case Success(v)   => Future(v)
              case Failure(exc) => Future.failed(exc)
            }

            case 2 => Future.sequence(vec.asScala.toSet.map(retrieve))
          }

        case col: jCollection[_] =>
          Future(
            new Iterable[Any] {
              override def iterator: Iterator[Any] = new Iterator[Any] {
                private val jIter = col.iterator.asInstanceOf[java.util.Iterator[AnyRef]]
                override def hasNext = jIter.hasNext
                override def next() = if (depth < maxDepth) {
                  retrieve(jIter.next())
                } else
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