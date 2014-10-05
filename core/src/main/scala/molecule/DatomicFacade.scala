package molecule
import java.util.{Date, UUID, Map => jMap}
import datomic._
import datomic.db.Db
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

  sealed trait TxType
  case class txDate(txInstant: Date) extends TxType
  case class txLong(t: Long) extends TxType
  case class txlObj(tx: java.util.List[Object]) extends TxType


  sealed trait DbOp
  case class AsOf(tx: TxType) extends DbOp
  //  case class AsOf(date: Date) extends DbOp
  case class Since(date: Date) extends DbOp
  case class Imagine(tx: java.util.List[Object]) extends DbOp
  case object History extends DbOp

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
      case AsOf(txDate(txInstant)) => conn.db.asOf(txInstant)
      case AsOf(txLong(t))         => conn.db.asOf(t)
      case AsOf(txlObj(tx))        => conn.db.asOf(tx)
      case Since(date)             => conn.db.since(date)
      case Imagine(tx)             => conn.db.`with`(tx).get(Connection.DB_AFTER).asInstanceOf[AnyRef]
      case History                 => conn.db.history()
      case _                       => conn.db
      //      case AsOf(date)  => conn.db.asOf(date)
    }

    // reset db settings
    dbOp = null

    //    println(conn)
    //    println(conn.db)
    //    println(query.format)
    //    println("##############################################################################")
    //    println(query.pretty)
    //    println("------------------------------------------------ ")
    //    println("RULES: " + (if (query.in.rules.isEmpty) "none" else query.in.rules map p mkString ("[\n ", "\n ", "\n]")))

    val first = if (query.in.rules.isEmpty) Seq(db) else Seq(db, rules)
    val allInputs = first ++ inputs(query)

    //    println("------------------------------------------------ ")
    //    println("INPUTS: " + allInputs.zipWithIndex.map(e => "\n" + (e._2 + 1) + " " + e._1) + "\n")
    //    println("###########################################################################################\n")

    Peer.q(query.toMap, allInputs: _*)
  }

  def getValues(db: Database, id: Any, ns: Any, attr: Any) =
    Peer.q(s"[:find ?values :in $$ ?id :where [?id :$ns/$attr ?values]]", db, id.asInstanceOf[Object]).map(_.get(0))

  def entityIds(query: Query)(implicit conn: Connection) = results(query, conn).toList.map(_.get(0).asInstanceOf[Long])

  protected[molecule] def upsertTx(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq(), ids: Seq[Long] = Seq()): jMap[_, _] = {
    val (javaTx, tempIds) = Model2Transaction(conn, model, dataRows, ids).javaTx
    // Get value from Future
    conn.transact(javaTx).get
  }

  protected[molecule] def upsert_OLD(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq(), ids0: Seq[Long] = Seq()): Seq[Long] = {
    //    val (javaTx, tempIds) = Model2Transaction(conn, model, dataRows, ids).javaTx
    //    val txResult = conn.transact(javaTx).get
    val txResult = upsertTx(conn, model, dataRows, ids0)
    val txData = txResult.get(Connection.TX_DATA)

    // We omit the first transaction datom
    val datoms = txData.asInstanceOf[java.util.Collection[Datom]].toList.tail
    val ids = datoms.map(_.e.asInstanceOf[Long]).distinct

    // Alternatively we can resolve fro the temp ids - but we don't get all, hmm...
    //    val txTtempIds = txResult.get(Connection.TEMPIDS)
    //    val dbAfter = txResult.get(Connection.DB_AFTER).asInstanceOf[Db]
    //    val insertedIds = tempIds.map(tempId => datomic.Peer.resolveTempid(dbAfter, txTtempIds, tempId).asInstanceOf[Long]).distinct

    x(0, txResult)
    ids
  }
  protected[molecule] def upsert(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq(), ids0: Seq[Long] = Seq()): Tx = {
    //    val (javaTx, tempIds) = Model2Transaction(conn, model, dataRows, ids).javaTx
    //    val txResult = conn.transact(javaTx).get
    Tx(upsertTx(conn, model, dataRows, ids0))
  }


  def tempId(partition: String = "user") = Peer.tempid(s":db.part/$partition")
}

object DatomicFacade extends DatomicFacade

case class Tx(txResult: jMap[_, _]) {
  def ids: Seq[Long] = {
    val txData = txResult.get(Connection.TX_DATA)
    // We omit the first transaction datom
    val datoms = txData.asInstanceOf[java.util.Collection[Datom]].toList.tail
    val ids = datoms.map(_.e.asInstanceOf[Long]).distinct
    ids
  }
  def id = ids.head
  def db = txResult.get(Connection.DB_AFTER).asInstanceOf[Db]
  def t = db.basisT()
  def tx = db.entity(Peer.toTx(t))
  def inst: Date = tx.get(":db/txInstant").asInstanceOf[Date]
}


// From Datomisca...

case class EntityFacade(entity: datomic.Entity, conn: Connection, id: Object) {

  def touch: Map[String, Any] = toMap

  def retract = conn.transact(Util.list(Util.list(":db.fn/retractEntity", id))).get()

  def apply(attr: String) = 42
  // macro?
  def --: (attr: String) = this

  def toMap: Map[String, Any] = {
    //  def toMap = {
    val builder = Map.newBuilder[String, Any]
    val iter = entity.keySet.iterator

    // Add id also
    builder += ":db/id" -> entity.get(":db/id")
    while (iter.hasNext) {
      val key = iter.next()
      builder += (key -> toScala(entity.get(key)))
    }
    builder.result()
  }

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
    case d: Date => d
    // :db.type/uuid
    case u: UUID => u
    // :db.type/uri
    case u: java.net.URI => u
    // :db.type/keyword
    case kw: clojure.lang.Keyword => kw.toString // Clojure Keywords not used in Molecule
    // :db.type/bytes
    case bytes: Array[Byte] => bytes
    // an entity map
    case e: datomic.Entity => new EntityFacade(e, conn, e.get(":db/id")).toMap

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

