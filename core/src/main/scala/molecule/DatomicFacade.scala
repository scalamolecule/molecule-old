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


  // Manipulate data ==================================================================

  private[molecule] def insertOne(conn: Connection, model: Model, args: Seq[Any]): Seq[Long] = insertMany(conn, model, Seq(args))

  def insertMany(conn: Connection, model: Model, argss: Seq[Seq[Any]]): Seq[Long] = {
//    val tx = Model2Transaction.applyOLD(conn, model, argss)
//    val javaTx = tx.map(stmt => Util.list(stmt.map(_.asInstanceOf[Object]): _*)).asJava
    val tx1 = Model2Transaction(conn, model, argss)
    val javaTx1 = tx1.map(_.toJava).asJava

    x(0
//      , model.elements
//      , argss
//      , tx
//      , tx1
//      , javaTx
      , javaTx1
    )
//    val txResult = conn.transact(javaTx).get
    val txResult = conn.transact(javaTx1).get
    val txData = txResult.get(Connection.TX_DATA)
    val newDatoms = txData.asInstanceOf[java.util.Collection[Datom]].toList.tail
    val newIds = newDatoms.map(_.e.asInstanceOf[Long]).distinct
    newIds
  }

  def upsertMolecule(conn: Connection, model: Model, ids: Seq[Long] = Seq()): Seq[Long] = {
    val t = Model2Transaction
    val (attrs, args) = t.getNonEmptyAttrs(model).unzip
    val rawMolecules = t.chargeMolecules(attrs, Seq(args))
    val molecules = t.groupNamespaces(rawMolecules)
    val tx: Seq[Seq[Any]] = t.upsertTransaction(conn.db, molecules, ids)
    val javaTx: java.util.List[_] = tx.map(stmt => Util.list(stmt.map(_.asInstanceOf[Object]): _*)).asJava

    x(0
      , attrs
      , args
      , rawMolecules
      , molecules
      , tx
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


// Borrowed from Datomisca...

case class EntityFacade(val entity: datomic.Entity) extends AnyVal {
  //case class EntityFacade(val entity: datomic.Entity) extends Debug {
  //  private val x = debug("EntityFacade", 1, 99, false, 2)


  def touch: Map[String, Any] = toMap
  //  def touch = toTpl
  //{
  //      val builder = Map.newBuilder[String, Any]
  //      val iter = blocking { entity.keySet } .iterator
  //      while (iter.hasNext) {
  //        val key = iter.next()
  //        builder += (key -> Convert.toScala(entity.get(key)))
  //      }
  //      builder.result
  //    }

  //  def contains(keyword: Keyword): Boolean =
  //    entity.get(keyword) ne null
  //
  //  def apply(keyword: Keyword): Any = {
  //    val o = entity.get(keyword)
  //    if (o ne null)
  //      Convert.toScala(o)
  //    else
  //      throw new EntityKeyNotFoundException(keyword.toString)
  //  }
  //
  //  def get(keyword: Keyword): Option[Any] = {
  //    val o = entity.get(keyword)
  //    if (o ne null)
  //      Some(Convert.toScala(o))
  //    else
  //      None
  //  }
  //
  //  def as[T](keyword: Keyword)(implicit fdat: FromDatomicCast[T]): T = {
  //    val o = entity.get(keyword)
  //    if (o ne null)
  //      fdat.from(o)
  //    else
  //      throw new EntityKeyNotFoundException(keyword.toString)
  //  }
  //
  //  def getAs[T](keyword: Keyword)(implicit fdat: FromDatomicCast[T]): Option[T] = {
  //    val o = entity.get(keyword)
  //    if (o ne null)
  //      Some(fdat.from(o))
  //    else
  //      None
  //  }

  //  def keySet: Set[String] = {
  //    val builder = Set.newBuilder[String]
  //    val iter = blocking { entity.keySet } .iterator
  //    while (iter.hasNext) {
  //      builder += iter.next()
  //    }
  //    builder.result
  //  }
  //
  // Todo?
  //  def toTpl = {
  //    import shapeless.syntax.std.tuple._
  //    val tpl1 = Tuple1(":db/id" -> entity.get(":db/id"))
  //    def add[T <: Product](tpl: T, keys: List[String]): Product = keys match {
  //      case Nil         => tpl
  //      case key :: tail => add(tpl :+ (keys.head -> entity.get(key)), tail)
  //    }
  //    add(tpl1, entity.keySet.toList)
  //  }

  def toMap: Map[String, Any] = {
    //  def toMap = {
    val builder = Map.newBuilder[String, Any]
    val iter = entity.keySet.iterator

    //    x(1
    //    ,entity
    //    , entity.keySet()
    //    , entity.touch()
    //
    //    )

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
    //    case kw: clojure.lang.Keyword => kw
    // :db.type/bytes
    case bytes: Array[Byte] => bytes
    // an entity map
    //    case e: datomic.Entity => new EntityFacade(e)
    case e: datomic.Entity => new EntityFacade(e).toMap
    //    case e: datomic.Entity => new EntityFacade(e).toTpl

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
    case v => throw new RuntimeException(v.getClass.toString)
  }

}
