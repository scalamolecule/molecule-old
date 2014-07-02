package molecule.db
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import datomic._
import molecule.ast.model._
import molecule.ast.query._
import molecule.ops.QueryOps._
import molecule.transform.{Model2Transaction, Query2String}
import molecule.util.Debug

trait DatomicFacade extends Debug {
  val x = debug("DatomicFacade", 1, 99, false, 2)
  type KeepQueryOpsWhenFormatting = KeepQueryOps


  // Schema =================================================================

  def init(uri: String, schemaTemplate: Seq[Seq[String]]): Connection = {
    Peer.deleteDatabase(uri)
    Peer.createDatabase(uri)
    val conn = Peer.connect(uri)
    val schemaTx = Util.list(schemaTemplate.map(transaction => Util.list(transaction.asJava: _*)).asJava: _*)

    // Create database schema
    conn.transact(schemaTx).get()
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
    //        println(query.format)
    //        println("---------------- ")
    //        println(query.pretty)
    //        println("---------------- ")
    //        println("RULES: " + (if (query.in.rules.isEmpty) "none" else query.in.rules map p mkString ("[\n ", "\n ", "\n]")))
    //        println("---------------- ")

    val first = if (query.in.rules.isEmpty) Seq(db) else Seq(db, rules)
    val allInputs = first ++ inputs(query)

    //        println("INPUTS: " + allInputs.zipWithIndex.map(e => "\n" + (e._2 + 1) + " " + e._1) + "\n")
    //        println("###########################################################################################\n")

    Peer.q(query.toMap, allInputs: _*)
  }

  def getValues(db: Database, id: Any, ns: Any, attr: Any) =
    Peer.q(s"[:find ?values :in $$ ?id :where [?id :$ns/$attr ?values]]", db, id.asInstanceOf[Object]).map(_.get(0))

  def entityIds(query: Query)(implicit conn: Connection) = results(query, conn).toList.map(_.get(0).asInstanceOf[Long])


  // Manipulate data ==================================================================

  private[molecule] def insertOne(conn: Connection, model: Model, args: Seq[Any]): Seq[Long] = insertMany(conn, model, Seq(args))

  def insertMany(conn: Connection, model: Model, argss: Seq[Seq[Any]]): Seq[Long] = {
    val tx = Model2Transaction(conn, model, argss)
    val javaTx = tx.map(stmt => Util.list(stmt.map(_.asInstanceOf[Object]): _*)).asJava
    val txResult = conn.transact(javaTx).get
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
    x(0 // change to 1 to print
      , model
      , args
      , rawMolecules
      , molecules
    )
    val tx: Seq[Seq[Any]] = t.upsertTransaction(conn.db, molecules, ids)
    val javaTx: java.util.List[_] = tx.map(stmt => Util.list(stmt.map(_.asInstanceOf[Object]): _*)).asJava
    val txResult = conn.transact(javaTx).get
    val txData = txResult.get(Connection.TX_DATA)
    x(0 // change to 1 to print
      , args
      , tx
      , javaTx
      , txResult
      , txData)

    val newDatoms = txData.asInstanceOf[java.util.Collection[Datom]].toList.tail
    val newIds = newDatoms.map(_.e.asInstanceOf[Long]).distinct
    newIds
  }
}

object DatomicFacade extends DatomicFacade