package molecule
import datomic._
import molecule.ast.model._
import molecule.ast.query._
import molecule.ops.QueryOps._
import molecule.transform.{Model2Transaction, Query2String}
import molecule.util.Debug
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.language.existentials

trait DatomicFacade extends Debug {
  private val x = debug("DatomicFacade", 1, 99, false, 2)
  type KeepQueryOpsWhenFormatting = KeepQueryOps

  // Create database and load schema ========================================

  def load(tx: java.util.List[_], identifier: String = "test"): Connection = {
    val uri = "datomic:mem://" + identifier
    Peer.deleteDatabase(uri)
    Peer.createDatabase(uri)
    val conn = Peer.connect(uri)
    conn.transact(tx).get()
    conn
  }

  // Query ==================================================================

  trait DbOp
  case class AsOf(date: java.util.Date) extends DbOp
  case class Since(date: java.util.Date) extends DbOp
  case class Imagine(tx: java.util.List[Object]) extends DbOp

  private[molecule] var dbOp: DbOp = null

  def rule(query: Query) = {
    val p = (expr: QueryExpr) => Query2String(query).p(expr)
    "[" + (query.in.rules map p mkString " ") + "]"
  }

  def inputs(query: Query) = query.in.inputs.map {
    case InVar(RelationBinding(_), argss)   => Util.list(argss.map(args => Util.list(args.asJava: _*)).asJava: _*)
    case InVar(CollectionBinding(_), argss) => Util.list(argss.head.asJava: _*)
    case InVar(_, argss)                    => argss.head.head
    case InDataSource(_, argss)             => argss.head.head
    case args                               => sys.error(s"[DatomicFacade] UNRESOLVED input args: $args")
  }

  def results(query: Query, conn: Connection) = {
    val p = (expr: QueryExpr) => Query2String(query).p(expr)
    val rules = "[" + (query.in.rules map p mkString " ") + "]"
    val db = dbOp match {
      case AsOf(date)  => conn.db.asOf(date)
      case Since(date) => conn.db.since(date)
      case Imagine(tx) => conn.db.`with`(tx).get(Connection.DB_AFTER).asInstanceOf[AnyRef]
      case _           => conn.db
    }

    // reset db settings
    dbOp = null

    //    println(conn)
    //    println(conn.db)
    //                println(query.format)
    //                println("---------------- ")
    //                println(query.pretty)
    //                println("---------------- ")
    //                println("RULES: " + (if (query.in.rules.isEmpty) "none" else query.in.rules map p mkString ("[\n ", "\n ", "\n]")))
    //                println("---------------- ")

    val first = if (query.in.rules.isEmpty) Seq(db) else Seq(db, rules)
    val allInputs = first ++ inputs(query)

    //            println("INPUTS: " + allInputs.zipWithIndex.map(e => "\n" + (e._2 + 1) + " " + e._1) + "\n")
    //            println("###########################################################################################\n")

    Peer.q(query.toMap, allInputs: _*)
  }

  def getValues(db: Database, id: Any, ns: Any, attr: Any) =
    Peer.q(s"[:find ?values :in $$ ?id :where [?id :$ns/$attr ?values]]", db, id.asInstanceOf[Object]).map(_.get(0))

  def entityIds(query: Query)(implicit conn: Connection) = results(query, conn).toList.map(_.get(0).asInstanceOf[Long])

  private[molecule] def upsert(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq(), ids: Seq[Long] = Seq()): Seq[Long] = {
    val javaTx = Model2Transaction(conn, model, dataRows, ids).javaTx
    x(0
      , model.elements
      , dataRows
      , ids
      , javaTx
    )
    val txResult = conn.transact(javaTx).get
    val txData = txResult.get(Connection.TX_DATA)
    val newDatoms = txData.asInstanceOf[java.util.Collection[Datom]].toList.tail
    val newIds = newDatoms.map(_.e.asInstanceOf[Long]).distinct
    newIds
  }
}

object DatomicFacade extends DatomicFacade


// From Datomisca...

case class EntityFacade(entity: datomic.Entity) extends AnyVal {

  def touch: Map[String, Any] = toMap

  def toMap: Map[String, Any] = {
    //  def toMap = {
    val builder = Map.newBuilder[String, Any]
    val iter = entity.keySet.iterator

    // Add id also
    builder += ":db/id" -> entity.get(":db/id")
    while (iter.hasNext) {
      val key = iter.next()
      builder += (key -> Convert.toScala(entity.get(key)))
    }
    builder.result()
  }
}

private[molecule] object Convert {

  private[molecule] def toScala(v: Any): Any = v match {
    // :db.type/string
    case s: java.lang.String => s
    // :db.type/boolean
    case b: java.lang.Boolean => b: Boolean
    // :db.type/long
    case l: java.lang.Long => l: Long
    // attribute id
    case i: java.lang.Integer => i.toLong: Long
    // :db.type/float
    case f: java.lang.Float => f: Float
    // :db.type/double
    case d: java.lang.Double => d: Double
    // :db.type/bigint
    case bi: java.math.BigInteger => BigInt(bi)
    // :db.type/bigdec
    case bd: java.math.BigDecimal => BigDecimal(bd)
    // :db.type/instant
    case d: java.util.Date => d
    // :db.type/uuid
    case u: java.util.UUID => u
    // :db.type/uri
    case u: java.net.URI => u
    // :db.type/keyword
    case kw: clojure.lang.Keyword => kw.toString // Molecule doesn't work with Clojure Keywords
    // :db.type/bytes
    case bytes: Array[Byte] => bytes
    // an entity map
    case e: datomic.Entity => new EntityFacade(e).toMap

    // :db.type/keyword
    case set: clojure.lang.PersistentHashSet =>
      set.toList map toScala

    // a collection
    case coll: java.util.Collection[_] =>
      new Iterable[Any] {
        override def iterator = new Iterator[Any] {
          private val jIter = coll.iterator.asInstanceOf[java.util.Iterator[AnyRef]]
          override def hasNext = jIter.hasNext
          override def next() = toScala(jIter.next())
        }
        override def isEmpty = coll.isEmpty
        override def size = coll.size
        override def toString = coll.toString
      }
    // otherwise
    case v => throw new RuntimeException("[DatomicFacade:Convert:toScala] Unexpected Datalog type to convert: " + v.getClass.toString)
  }

}
