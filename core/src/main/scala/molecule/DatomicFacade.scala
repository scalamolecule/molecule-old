package molecule
import java.util.UUID._
import java.util.{Date, List => jList, Map => jMap}

import datomic._
import datomic.db.Db
import molecule.ast.model._
import molecule.ast.query._
import molecule.ast.transaction.{Statement, _}
import molecule.dsl.Transaction
import molecule.ops.QueryOps._
import molecule.transform.{Model2Transaction, Query2String}
import molecule.util.Debug
import org.specs2.main.ArgProperties

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

// ArgProperties for some reason makes FactoryBase happy when creating nested molecules
// outside the Specs2 framework (in non-test code). Todo: Do without ArgProperties



trait DatomicFacade extends ArgProperties {
  private val x = Debug("DatomicFacade", 1, 99, false, 3)
  type KeepQueryOpsWhenFormatting = KeepQueryOps

  // Create database and load schema ========================================

  def recreateDbFrom(tx: Transaction, identifier: String = "", protocol: String = "mem"): Connection = {
    val id = if (identifier == "") randomUUID() else identifier
    val uri = s"datomic:$protocol://$id"
    try {
      Peer.deleteDatabase(uri)
      Peer.createDatabase(uri)
      val conn = Peer.connect(uri)
      conn.transact(tx.partitions)
      conn.transact(tx.namespaces)
      conn
    } catch {
      case e: Throwable => sys.error("@@@@@@@@@@ " + e.getCause)
    }
  }

  def loadList(txlist: java.util.List[_], identifier: String = "", protocol: String = "mem"): Connection = {
    val id = if (identifier == "") randomUUID() else identifier
    val uri = s"datomic:$protocol://$id"
    try {
      Peer.deleteDatabase(uri)
      Peer.createDatabase(uri)
      val conn = Peer.connect(uri)
      conn.transact(txlist).get()
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
  case class Since(date: Date) extends DbOp
  case class Imagine(tx: java.util.List[Object]) extends DbOp
  case object History extends DbOp

  private[molecule] var dbOp: DbOp = null

  private def cast(a: Any) = a match {
    case i: Int   => i.toLong.asInstanceOf[Object]
    case f: Float => f.toDouble.asInstanceOf[Object]
    case other    => other.asInstanceOf[Object]
  }

  def inputs(query: Query) = query.i.inputs.map {
    case InVar(RelationBinding(_), argss)   => Util.list(argss.map(args => Util.list(args map cast: _*)).asJava: _*)
    case InVar(CollectionBinding(_), argss) => Util.list(argss.head map cast: _*)
    case InVar(_, argss)                    => cast(argss.head.head)
    case InDataSource(_, argss)             => cast(argss.head.head)
    case other                              => sys.error(s"[DatomicFacade] UNEXPECTED inputs: $other")
  }

  def results(conn: Connection, model: Model, query: Query): List[jList[AnyRef]] = {
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

    try {
      Peer.q(query.toMap, allInputs: _*).toList
    } catch {
      case e: Throwable => throw new RuntimeException(
        s"""
           |#############################################################################
           |$e
           |
           |$model
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


  // Manipulation

  protected[molecule] def insert(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq()): Tx = {
    val transformer = Model2Transaction(conn, model)
    val stmtss = transformer.insertStmts(dataRows)
//        x(2, model, transformer.stmtsModel, dataRows, stmtss)
//        x(2, model, transformer.stmtsModel, stmtss)
//        x(2, transformer.stmtsModel, stmtss)
    Tx(conn, stmtss)
  }

  protected[molecule] def save(conn: Connection, model: Model): Tx = {
    val transformer = Model2Transaction(conn, model)
    val stmts = transformer.saveStmts()
    //        x(2, model, transformer.stmtsModel, stmts)
    Tx(conn, Seq(stmts))
  }

  protected[molecule] def update(conn: Connection, model: Model): Tx = {
    val transformer = Model2Transaction(conn, model)
    val stmts = transformer.updateStmts()
//            x(3, model, transformer.stmtsModel, stmts)
//            x(3, transformer.stmtsModel, stmts)
    Tx(conn, Seq(stmts))
  }
}

object DatomicFacade extends DatomicFacade


case class Tx(conn: Connection, stmtss: Seq[Seq[Statement]]) {
  private val x = Debug("Tx", 1, 99, false, 3)

  val flatStmts = stmtss.flatten.map {
    case Add(e, a, i: Int)   => Add(e, a, i.toLong: java.lang.Long).toJava
    case Add(e, a, f: Float) => Add(e, a, f.toDouble: java.lang.Double).toJava
    //    case Add(e, a, d: Date)  => Add(e, a, d).toJava
    //    case Add(e, a, v: UUID)  => Add(e, a, v).toJava
    //    case Add(e, a, v: URI)   => Add(e, a, v).toJava
    case other => other.toJava
  }.asJava
  //      x(7, stmtss, flatStmts)

  val txResult: jMap[_, _] = conn.transact(flatStmts).get

  def eids: List[Long] = {
    val txData = txResult.get(Connection.TX_DATA)

    // Omit first transaction datom
    val datoms = txData.asInstanceOf[java.util.Collection[Datom]].toList.tail

    val tempIds = stmtss.flatten.collect {
      case Add(e, _, _) if e.toString.take(6) == "#db/id" => e
      case Add(_, _, v) if v.toString.take(6) == "#db/id" => v
    }.distinct

    val txTtempIds = txResult.get(Connection.TEMPIDS)
    val dbAfter = txResult.get(Connection.DB_AFTER).asInstanceOf[Db]
    val ids: Seq[Long] = tempIds.map(tempId => datomic.Peer.resolveTempid(dbAfter, txTtempIds, tempId).asInstanceOf[Long]).distinct
    //    x(1, transformer.stmtsModel, stmtss, datoms, ids)
    ids.toList
  }

  def eidSet: Set[Long] = eids.toSet
  def eid: Long = eids.head
  def db: Db = txResult.get(Connection.DB_AFTER).asInstanceOf[Db]
  def t: Long = db.basisT()
  def tx: datomic.Entity = db.entity(Peer.toTx(t))
  def inst: Date = tx.get(":db/txInstant").asInstanceOf[Date]
}

//import scala.language.existentials
//  private def cast(a: Any) = a match {
//    case i: Int   => i.toLong.asInstanceOf[Object]
//    case f: Float => f.toDouble.asInstanceOf[Object]
//    case other    => other.asInstanceOf[Object]
//  }
//  val argssRaw     = Seq(Seq("(.*)", "(?i)(H)"))
//  val argssJava = Seq(datomic.Util.list(argssRaw.map(args => datomic.Util.list(args map cast: _*)).asJava: _*))
//  val argss1    = conn.db +: argssJava
//
//  datomic.Peer.q(
//    """
//      |[:find  ?b (distinct ?c)
//      | :where [?a :ns/int ?b]
//      |        [(fulltext $ :ns/strMap "da@Hej") [[ ?a ?c ]]]]
//    """.stripMargin, conn.db) === 987
//
//  datomic.Peer.q(
//    """
//      |[:find  ?b (distinct ?c)
//      | :in    $ [[ ?cKey ?cValue ]]
//      | :where [?a :ns/int ?b]
//      |        [?a :ns/strMap ?c]
//      |        [(str "(?i)(" ?cKey ")@.*") ?c1]
//      |        [(.matches ^String ?c ?c1)]
//      |        [(.split ^String ?c "@" 2) ?c2]
//      |        [(second ?c2) ?c3]
//      |        [(.compareTo ^String ?c3 ?cValue) ?c3_1]
//      |        [(> ?c3_1 0)]]
//    """.stripMargin, argss1: _*) === 77