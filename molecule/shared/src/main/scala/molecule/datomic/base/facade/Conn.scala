package molecule.datomic.base.facade

import java.io.Reader
import java.util.{Date, Collection => jCollection, List => jList}
import molecule.core.ast.elements.Model
import molecule.core.data.SchemaTransaction
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling._
import molecule.core.marshalling.ast.nodes.Obj
import molecule.core.marshalling.ast._
import molecule.core.ops.Conversions
import molecule.core.transform.Model2Stmts
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView.DbView
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.util.TempIdFactory
import scala.concurrent.{ExecutionContext, Future}


/** Facade to Datomic Connection.
 *
 * Has additional internal state to manage using adhoc and test databases.
 * */
trait Conn extends Conversions with BooPicklers {

  // Molecule api --------------------------------------------------------------

  /** Use test database as of now. */
  def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit]

  /** Use test database as of time t / tx id.
   *
   * @param t Long Time t or tx id
   */
  def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit]

  /** Use test database as of date.
   *
   * @param d Date
   */
  def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit]

  /** Use test database as of transaction report.
   *
   * @param txR Transaction report
   */
  def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit]

  /** Use test database since time t.
   *
   * @param t Long
   */
  def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit]

  /** Use test database since date.
   *
   * @param d Date
   */
  def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit]

  /** Use test database since transaction report.
   *
   * @param txR Transaction report
   */
  def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit]


  /** Use test database with temporary transaction statements.
   * <br><br>
   * Transaction statements can be supplied from a molecule:
   * {{{
   * for {
   *   conn <- futConn // implicit Future[Conn] instance in scope
   *
   *   // Live data
   *   benId = Person.name("Ben").save.map(_.eid)
   *
   *   // Use temporary db with statements of one or more tested transactions
   *   _ <- conn.testDbWith(
   *     Person.name("liz").getSaveStmts,
   *     benId.getRetractStmts
   *     // more...
   *   )
   *
   *   // Query using temporary database
   *   _ <- Person.name.get.map(_ ==> List("Liz"))
   *
   *   // Discard test db and go back to live db
   *   _ = conn.useLiveDb()
   *
   *   // Query using unchanged live data
   *   _ <- Person.name.get.map(_ ==> List("Ben"))
   * } yield ()
   * }}}
   *
   * @param txMolecules List of List of transaction
   *                    [[molecule.datomic.base.ast.transactionModel.Statement Statement]]'s
   */
  def testDbWith(txMolecules: Future[Seq[Statement]]*)
                (implicit ec: ExecutionContext): Future[Unit]


  /** Get out of test mode and back to live db. */
  def useLiveDb(): Unit


  // Datomic shared Peer/Client api --------------------------------------------

  /** Get current adhoc/test/live db. */
  def db(implicit ec: ExecutionContext): Future[DatomicDb]


  /** Transact EDN data string
   *
   * @param edn EDN transaction data string
   * @param ec  ExecutionContext for Future
   * @return Future with [[TxReport]]
   */
  def transact(edn: String)(implicit ec: ExecutionContext): Future[TxReport]


  /** Transact statements from a java.util.Reader
   *
   * Only works on jvm platform.
   *
   * @param stmtsReader [[Reader]]
   * @param ec
   * @return Future with [[TxReport]]
   */
  def transact(stmtsReader: Reader)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("transact(stmtsReader: Reader)"))


  /** Transact java statements
   *
   * Only works on jvm platform.
   *
   * @param javaStmts
   * @param ec
   * @return Future with [[TxReport]]
   */
  def transact(javaStmts: jList[_])(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("transact(javaStmts: jList[_])"))


  /** Query Datomic directly with Datalog query and optional Scala inputs.
   * {{{
   * for {
   *   conn <- futConn // implicit Future[Conn] instance in scope
   *
   *   // Typed tuple result from molecule
   *   _ <- Ns.str.int.get.map(_ ==> List(
   *     ("Liz", 37),
   *     ("Ben", 42),
   *   ))
   *
   *   // Any-type result from query
   *   _ <- conn.query(
   *     "[:find  ?b ?c :where [?a :Ns/str ?b][?a :Ns/int ?c]]"
   *   ).map(_ ==> List(
   *     List("Liz", 37),
   *     List("Ben", 42)
   *   ))
   *
   *   // Any-type result from query with input(s)
   *   _ <- conn.query(
   *     "[:find  ?b ?c :in $ ?c :where [?a :Ns/str ?b][?a :Ns/int ?c]]",
   *     42
   *   ).map(_ ==> List(
   *     List("Ben", 42)
   *   ))
   *
   *   // See datalog queries of molecules with `inspectGet`
   *   _ <- Ns.str.int.inspectGet // shows Datalog query...
   * } yield ()
   * }}}
   *
   * @param datalogQuery Datalog query string
   * @param inputs       Optional input(s) to query
   * @return Future[List[List[AnyRef]]]
   * */
  def query(
    datalogQuery: String,
    inputs: Any*
  )(implicit ec: ExecutionContext): Future[List[List[AnyRef]]] =
    Future.failed(jvmOnly("query"))


  /** Synchronize database to have all transactions completed up until now.
   *
   * Sets a flag on the connection to do the synchronization on the first
   * subsequent query. Hereafter the flag is removed.
   *
   * The synchronization guarantees to include all transactions that are
   * complete when the synchronization query is made. Before the query is executed,
   * the connection communicates with the transactor to do the synchronization.
   *
   * A Future with the synchronized database is returned for the query to use. The future
   * can take arbitrarily long to complete. Waiting code should specify a timeout.
   *
   * Only use `sync` when the following two conditions hold:
   *
   *   1. coordination of multiple peer/client processes is required
   *      2. peers/clients have no way to agree on a basis t for coordination
   *
   * @param ec an implicit execution context.
   * @return Connection with synchronization flag set
   */
  def sync: Conn


  /** Synchronize database to have all transactions completed up to and including time t.
   *
   * Sets a flag with a time t on the connection to do the synchronization
   * on the first subsequent query. Hereafter the flag is removed.
   *
   * The synchronization guarantees to include all transactions that are complete
   * up to and including time t. `sync` does not communicate with the transactor,
   * but it can block (within the Future) if the peer/client has not yet been notified
   * of transactions up to time t.
   *
   * A Future with the synchronized database is returned for the query to use. The future
   * can take arbitrarily long to complete. Waiting code should specify a timeout.
   *
   * Only use `sync(t)` when coordination of multiple peer/client processes is required.
   *
   * If peers/clients do not share a basis t, prefer `sync`.
   *
   * @param ec an implicit execution context.
   * @return Connection with synchronization flag set
   */
  def sync(t: Long): Conn


  // Schema change -------------------------------------------------------------

  def changeAttrName(curName: String, newName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("changeAttrName"))

  def retireAttr(attrName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("retireAttr"))

  def changeNamespaceName(curName: String, newName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("changeNamespaceName"))

  def retireNamespace(nsName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("retireNamespace"))

  def changePartitionName(curName: String, newName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("changePartitionName"))

  def retirePartition(partName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("retirePartition"))

  def retractSchemaOption(attr: String, option: String)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("retractSchemaOption"))

  def retractEnum(enumString: String)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("retractEnum"))



  def getEnumHistory(implicit ec: ExecutionContext): Future[List[(String, Int, Long, Date, String, Boolean)]]


  // Tx fn helpers -------------------------------------------------------------

  // Needs to be public since tx functions use id
  def stmts2java(stmts: Seq[Statement]): jList[jList[_]] =
    throw jvmPeerOnly("stmts2java(stmts: Seq[Statement])")

  private[molecule] def buildTxFnInvoker(txFn: String, args: Seq[Any]): jList[_] =
    throw jvmPeerOnly("buildTxFnInstall(txFn: String, args: Seq[Any])")


  // Internal ------------------------------------------------------------------

  private[molecule] val isJsPlatform: Boolean

  private[molecule] val tempId = TempIdFactory

  private[molecule] val defaultConnProxy: ConnProxy = null

  private[molecule] var connProxy: ConnProxy = defaultConnProxy

  // Temporary db for ad-hoc queries against time variation dbs
  // (takes precedence over _testDb in Conn_Peer and Conn_Client)
  private[molecule] var _adhocDbView: Option[DbView] = None

  private[molecule] lazy val rpc: MoleculeRpc = throw jsOnly("rpc")

  def updateConnProxy(schema: SchemaTransaction): Unit = {
    connProxy match {
      case proxy: DatomicPeerProxy =>
        connProxy = proxy.copy(
          // Don't set on initialization to avoid redundant schema transactions
          schema = if (proxy.nsMap.isEmpty) Nil else schema.datomicPeer,
          nsMap = schema.nsMap,
          attrMap = schema.attrMap,
        )

      case proxy: DatomicDevLocalProxy =>
        connProxy = proxy.copy(
          schema = if (proxy.nsMap.isEmpty) Nil else schema.datomicClient,
          nsMap = schema.nsMap,
          attrMap = schema.attrMap,
        )

      case proxy: DatomicPeerServerProxy =>
        connProxy = proxy.copy(
          schema = if (proxy.nsMap.isEmpty) Nil else schema.datomicClient,
          nsMap = schema.nsMap,
          attrMap = schema.attrMap,
        )
    }
  }

  def usingAdhocDbView(dbView: DbView): Conn = {
    updateAdhocDbView(Some(dbView))
    this
  }

  private[molecule] def updateAdhocDbView(adhocDbView: Option[DbView]): Unit = {
    _adhocDbView = adhocDbView
    connProxy = connProxy match {
      case p: DatomicPeerProxy       => p.copy(adhocDbView = adhocDbView)
      case p: DatomicDevLocalProxy   => p.copy(adhocDbView = adhocDbView)
      case p: DatomicPeerServerProxy => p.copy(adhocDbView = adhocDbView)
    }
  }

  private[molecule] def updateTestDbView(testDbView: Option[DbView], status: Int = 1): Unit = {
    connProxy = connProxy match {
      case p: DatomicPeerProxy       => p.copy(testDbView = testDbView, testDbStatus = status)
      case p: DatomicDevLocalProxy   => p.copy(testDbView = testDbView, testDbStatus = status)
      case p: DatomicPeerServerProxy => p.copy(testDbView = testDbView, testDbStatus = status)
    }
  }


  private[molecule] def model2stmts(model: Model): Model2Stmts =
    Model2Stmts(isJsPlatform, this, model)


  private[molecule] def transact(
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport]

  private[molecule] def transact(
    edn: String,
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("transact(edn: String, scalaStmts: Future[Seq[Statement]])"))

  private[molecule] def transact(
    stmtsReader: Reader,
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])"))

  private[molecule] def transactRaw(
    javaStmts: jList[_],
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("transactRaw(javaStmts: jList[_], scalaStmts: Future[Seq[Statement]])"))


  private[molecule] def rawQuery(
    query: String,
    inputs: Seq[AnyRef] = Nil
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    Future.failed(jvmOnly("def rawQuery(query: String, inputs: Seq[AnyRef] = Nil)"))

  private[molecule] def jvmQuery(
    model: Model,
    query: Query
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    Future.failed(jvmOnly("jvmQuery(model: Model, query: Query)"))

  private[molecule] def indexQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    Future.failed(jvmOnly("indexQuery(model: Model)"))

  private[molecule] def datalogQuery(
    model: Model,
    query: Query,
    _db: Option[DatomicDb] = None
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    Future.failed(jvmOnly("datalogQuery(model: Model, query: Query, _db: Option[DatomicDb] = None)"))

  private[molecule] def jvmSchemaHistoryQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    Future.failed(jvmOnly("jvmSchemaHistoryQueryTpl(model: Model, queryString: String)"))

  private[molecule] def jsQuery[T](
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    sortCoordinates: List[List[SortCoordinate]],
    unpacker: Iterator[String] => T
  )(implicit ec: ExecutionContext): Future[List[T]] = Future.failed(jsOnly("jsQuery"))

  private[molecule] def jsQueryJson(
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: nodes.Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    sortCoordinates: List[List[SortCoordinate]]
  )(implicit ec: ExecutionContext): Future[String] = Future.failed(jsOnly("jsQueryJson"))

  private[molecule] def jsSchemaHistoryQuery[T](
    model: Model,
    obj: Obj,
    sortCoordinates: List[List[SortCoordinate]],
    unpacker: Iterator[String] => T
  )(implicit ec: ExecutionContext): Future[List[T]] = Future.failed(jsOnly("jsSchemaHistoryQuery"))

  private[molecule] def jsSchemaHistoryQueryJson(
    model: Model,
    obj: Obj,
    sortCoordinates: List[List[SortCoordinate]]
  )(implicit ec: ExecutionContext): Future[String] = Future.failed(jsOnly("jsSchemaHistoryQuery"))


  private[molecule] def entity(id: Any)(implicit ec: ExecutionContext): Future[DatomicEntity]

  private[molecule] def inspect(
    header: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit


  private[molecule] def getAttrValues(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]]


  private[molecule] def getEntityAttrKeys(
    query: String
  )(implicit ec: ExecutionContext): Future[List[String]]

  protected def jsOnly(method: String): MoleculeException =
    MoleculeException(s"`$method` only implemented on JS platform.")

  protected def jvmOnly(method: String): MoleculeException =
    MoleculeException(s"`$method` only implemented on JVM platform.")

  protected def jvmPeerOnly(method: String): MoleculeException =
    MoleculeException(s"`$method` only implemented on JVM platform for Peer api.")

  protected def peerOnly(method: String): MoleculeException =
    MoleculeException(s"`$method` only implemented for Peer api.")

  protected def debug(prefix: String, suffix: String = "") = {
    val p = prefix + " " * (4 - prefix.length)
    println(s"$p  ${connProxy.testDbStatus}  ${connProxy.testDbView}   " + suffix)
  }
}
