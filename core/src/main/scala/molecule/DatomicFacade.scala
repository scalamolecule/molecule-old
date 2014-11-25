package molecule
import java.util.UUID._
import java.util.{Date, UUID, Collection => jCollection, List => jList, Map => jMap}
import datomic._
import datomic.db.Db
import molecule.ast.model._
import molecule.ast.query._
import molecule.ast.transaction.{Statement, _}
import molecule.ops.QueryOps._
import molecule.transform.{Model2Transaction, Query2String}
import molecule.util.Debug
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.language.{existentials, higherKinds}

trait DatomicFacade {
  private val x = Debug("DatomicFacade", 1, 99, false, 3)
  type KeepQueryOpsWhenFormatting = KeepQueryOps

  // Create database and load schema ========================================

  def load(tx: java.util.List[_], identifier: String = "test"): Connection = {
    val uri = "datomic:mem://" + randomUUID()
    //    val uri = "datomic:mem://" + identifier
    //    Peer.deleteDatabase(uri)
    //    Peer.createDatabase(uri)
    //    val conn = Peer.connect(uri)
    //    conn.transact(tx).get()
    //    conn
    //    val conn = try {
    try {
      Peer.deleteDatabase(uri)
      Peer.createDatabase(uri)
      val conn = Peer.connect(uri)
      conn.transact(tx).get()
      conn
    } catch {
      case e: Throwable => sys.error("@@@@@@@@@@ " + e.getCause)
    }
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
    "[" + (query.i.rules map p mkString " ") + "]"
  }

  def inputs(query: Query) = query.i.inputs.map {
    case InVar(RelationBinding(_), argss)   => Util.list(argss.map(args => Util.list(args.map(_.asInstanceOf[Object]): _*)).asJava: _*)
    case InVar(CollectionBinding(_), argss) => Util.list(argss.head.map(_.asInstanceOf[Object]): _*)
    case InVar(_, argss)                    => argss.head.head
    case InDataSource(_, argss)             => argss.head.head
    case args                               => sys.error(s"[DatomicFacade] UNRESOLVED input args: $args")
  }

  def results(query: Query, conn: Connection): jCollection[jList[AnyRef]] = {
    val p = (expr: QueryExpr) => Query2String(query).p(expr)
    val rules = "[" + (query.i.rules map p mkString " ") + "]"
    val db = dbOp match {
      case AsOf(txDate(txInstant)) => conn.db.asOf(txInstant)
      case AsOf(txLong(t))         => conn.db.asOf(t)
      case AsOf(txlObj(tx))        => conn.db.asOf(tx)
      case Since(date)             => conn.db.since(date)
      case Imagine(tx)             => conn.db.`with`(tx).get(Connection.DB_AFTER).asInstanceOf[AnyRef]
      case History                 => conn.db.history()
      case _                       => conn.db
    }

    // reset db settings
    dbOp = null

    val first = if (query.i.rules.isEmpty) Seq(db) else Seq(db, rules)
    val allInputs = first ++ inputs(query)

    //    println(query)
    //    println("##############################################################################")
    //    println(query.datalog)
    //    println("------------------------------------------------ ")
    //    println("RULES: " + (if (query.i.rules.isEmpty) "none" else query.i.rules map p mkString("[\n ", "\n ", "\n]")))
    //    println("------------------------------------------------ ")
    //    println("INPUTS: " + allInputs.zipWithIndex.map(e => "\n" + (e._2 + 1) + " " + e._1) + "\n")
    //    println("###########################################################################################\n")

    //    Peer.q(s"""
    //       [:find ?a ?b
    //        :where
    //          [?ent :ns/str ?a]
    //          [?ent :ns/int 28]
    //          [?ent :ns/int ?b]]
    //       """, conn.db)

    try {
      Peer.q(query.toMap, allInputs.map(_.asInstanceOf[Object]): _*)
    } catch {
      case e: Throwable => throw new RuntimeException(
        s"""
           |#############################################################################
           |$e
           |
           |$query
           |
           |${query.datalog}
           |
           |RULES: ${if (query.i.rules.isEmpty) "none" else query.i.rules map p mkString("[\n ", "\n ", "\n]")}
           |
           |INPUTS: ${allInputs.zipWithIndex.map(e => "\n" + (e._2 + 1) + " " + e._1)}
           |#############################################################################
         """.stripMargin)
    }
  }

  //  def tempId(partition: String = "user") = Peer.tempid(s":db.part/$partition")

  //  def getValues(db: Database, id: Any, ns: Any, attr: Any) =
  //    Peer.q(s"[:find ?values :in $$ ?id :where [?id :$ns/$attr ?values]]", db, id.asInstanceOf[Object]).map(_.get(0))
  //
  def getValues1(db: Database, id: Any, attr: String) =
    Peer.q(s"[:find ?values :in $$ ?id :where [?id :$attr ?values]]", db, id.asInstanceOf[Object]).map(_.get(0))

  def entityIds(query: Query)(implicit conn: Connection) = results(query, conn).toList.map(_.get(0).asInstanceOf[Long])


  // Manipulation

  protected[molecule] def insert(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq()): Tx = {
    val transformer = Model2Transaction(conn, model)
    val stmtss = transformer.insertStmts(dataRows)
    //    x(1, model, transformer.stmtsModel, stmtss)
    Tx(conn, transformer, stmtss)
  }

  protected[molecule] def save(conn: Connection, model: Model): Tx = {
    val transformer = Model2Transaction(conn, model)
    val stmts = transformer.saveStmts
    //    x(2, model, transformer.stmtsModel, stmts)
    Tx(conn, transformer, Seq(stmts))
  }

  protected[molecule] def update(conn: Connection, model: Model): Tx = {
    val transformer = Model2Transaction(conn, model)
    val stmts = transformer.updateStmts
    //    x(3, model, transformer.stmtsModel, stmts)
    Tx(conn, transformer, Seq(stmts))
  }
}

object DatomicFacade extends DatomicFacade


case class Tx(conn: Connection, transformer: Model2Transaction, stmtss: Seq[Seq[Statement]]) {
  private val x = Debug("Tx", 1, 99, false, 3)

  val txResult: jMap[_, _] = conn.transact(stmtss.flatten.map(_.toJava).asJava).get

  def ids: Seq[Long] = {
    val txData = txResult.get(Connection.TX_DATA)

    // Omit first transaction datom
    val datoms = txData.asInstanceOf[java.util.Collection[Datom]].toList.tail

    val tempIds = stmtss.flatten.collect {
      case Add(e, _, _) if e.toString.take(6) == "#db/id" => e
      case Add(_, _, v) if v.toString.take(6) == "#db/id" => v
    }.distinct

    val txTtempIds = txResult.get(Connection.TEMPIDS)
    val dbAfter = txResult.get(Connection.DB_AFTER).asInstanceOf[Db]
    val ids = tempIds.map(tempId => datomic.Peer.resolveTempid(dbAfter, txTtempIds, tempId).asInstanceOf[Long]).distinct

    //    x(1, transformer.stmtsModel, stmtss, datoms, ids)
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

  def touch: Map[String, Any] = toMap map { case (k, v) =>
    val sortedValue = v match {
      // Todo: presuming
      case vs: List[Any] => vs.sortBy(_.asInstanceOf[Map[String, Any]].head._2.asInstanceOf[Long])
      case other         => other
    }
    k -> sortedValue
  }

  def apply(kw: String): Option[Any] = entity.get(kw) match {
    case null                                    => None
    case results: clojure.lang.PersistentHashSet => Some(results.toList.map(_.asInstanceOf[datomic.Entity].get(":db/id").asInstanceOf[Long]).sorted)
//    case results: datomic.query.EntityMap => Some(results.head.getClass)
    case result                                  => Some(toScala(result))
  }

  def apply(kw1: String, kw2: String, kwx: String*): Seq[Option[Any]] = {
    (kw1 +: kw2 +: kwx.toList) map apply
  }



  def retract = conn.transact(Util.list(Util.list(":db.fn/retractEntity", id))).get()


  def toMap: Map[String, Any] = {
    //  def toMap = {
    val builder = Map.newBuilder[String, Any]
    val iter = entity.keySet.toList.sorted.asJava.iterator()
    //    val iter = entity.keySet.iterator

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

