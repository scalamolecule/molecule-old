//package molecule
//
//import java.util.UUID._
//import java.util.{Date, UUID, Collection => jCollection, List => jList, Map => jMap}
//import datomic._
//import datomic.db.Db
//import molecule.ast.model._
//import molecule.ast.query._
//import molecule.ast.transaction.{Statement, _}
//import molecule.ops.QueryOps._
//import molecule.transform.{Model2Transaction, Query2String}
//import molecule.util.Debug
//import scala.collection.JavaConversions._
//import scala.collection.JavaConverters._
//import scala.language.{existentials, higherKinds}
//
//// From Datomisca...
//
//case class EntityFacade(entity: datomic.Entity, conn: Connection, id: Object) {
//
//  def touch: Map[String, Any] = toMap map { case (k, v) =>
//    val sortedValue = v match {
//      case vs: List[Any] => vs.sortBy(_.asInstanceOf[Map[String, Any]].head._2.asInstanceOf[Long])
//      case other         => other
//    }
//    k -> sortedValue
//  }
//
//  def apply(kw: String): Option[Any] = entity.get(kw) match {
//    case null                                    => None
//    case results: clojure.lang.PersistentHashSet => Some(results.toList.map(_.asInstanceOf[datomic.Entity].get(":db/id").asInstanceOf[Long]).sorted)
////    case results: datomic.query.EntityMap => Some(results.head.getClass)
//    case result                                  => Some(toScala(result))
//  }
//
//  def apply(kw1: String, kw2: String, kwx: String*): Seq[Option[Any]] = {
//    (kw1 +: kw2 +: kwx.toList) map apply
//  }
//
//  def retract = conn.transact(Util.list(Util.list(":db.fn/retractEntity", id))).get()
//
//
//  def toMap: Map[String, Any] = {
//    //  def toMap = {
//    val builder = Map.newBuilder[String, Any]
//    val iter = entity.keySet.toList.sorted.asJava.iterator()
//    //    val iter = entity.keySet.iterator
//
//    // Add id also
//    builder += ":db/id" -> entity.get(":db/id")
//    while (iter.hasNext) {
//      val key = iter.next()
//      builder += (key -> toScala(entity.get(key)))
//    }
//    builder.result()
//  }
//
//  private[molecule] def toScala(v: Any): Any = v match {
//    // :db.type/string
//    case s: java.lang.String => s
//    // :db.type/boolean
//    case b: java.lang.Boolean => b: Boolean
//    // :db.type/long
//    case l: java.lang.Long => l: Long
//    // attribute id
//    case i: java.lang.Integer => i.toLong: Long
//    // :db.type/float
//    case f: java.lang.Float => f: Float
//    // :db.type/double
//    case d: java.lang.Double => d: Double
//    // :db.type/bigint
//    case bi: java.math.BigInteger => BigInt(bi)
//    // :db.type/bigdec
//    case bd: java.math.BigDecimal => BigDecimal(bd)
//    // :db.type/instant
//    case d: Date => d
//    // :db.type/uuid
//    case u: UUID => u
//    // :db.type/uri
//    case u: java.net.URI => u
//    // :db.type/keyword
//    case kw: clojure.lang.Keyword => kw.toString // Clojure Keywords not used in Molecule
//    // :db.type/bytes
//    case bytes: Array[Byte] => bytes
//    // an entity map
//    case e: datomic.Entity => new EntityFacade(e, conn, e.get(":db/id")).toMap
//
//    // :db.type/keyword
//    case set: clojure.lang.PersistentHashSet =>
//      set.toList map toScala
//
//    // a collection
//    case coll: java.util.Collection[_] =>
//      new Iterable[Any] {
//        override def iterator = new Iterator[Any] {
//          private val jIter = coll.iterator.asInstanceOf[java.util.Iterator[AnyRef]]
//          override def hasNext = jIter.hasNext
//          override def next() = toScala(jIter.next())
//        }
//        override def isEmpty = coll.isEmpty
//        override def size = coll.size
//        override def toString = coll.toString
//      }
//    // otherwise
//    case v => throw new RuntimeException("[DatomicFacade:Convert:toScala] Unexpected Datalog type to convert: " + v.getClass.toString)
//  }
//}
//
