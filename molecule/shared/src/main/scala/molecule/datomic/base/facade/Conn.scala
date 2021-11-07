package molecule.datomic.base.facade

import java.io.Reader
import java.util.{Date, Collection => jCollection, List => jList}
import molecule.core.ast.elements.Model
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.nodes.Obj
import molecule.core.marshalling._
import molecule.core.ops.ColOps
import molecule.core.transform.Model2Stmts
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView.DbView
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.util.TempIdFactory
import scala.concurrent.{ExecutionContext, Future}


/** Facade to Datomic Connection.
 * */
trait Conn extends ColOps with Serializations {

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


  /** Use test database with temporary transaction data.
   * <br><br>
   * Transaction data can be supplied from any molecule:
   * {{{
   *   val benId = Person.name("Ben").save.eid
   *
   *   // Use temporary db with given transaction data applied
   *   conn.testDbWith(
   *     Person.name("liz").getSaveTx
   *   )
   *
   *   // Query using temporary database including Liz
   *   Person.name.get.map(_ ==> List("Ben", "Liz"))
   *
   *   // Multiple transactions can be applied
   *   conn.testDbWith(
   *     Person.name("Joe").getSaveTx,
   *     benId.getRetractTx
   *   )
   *   Person.name.get.map(_ ==> List("Liz", "Joe"))
   * }}}
   *
   * @param txMolecules List of List of transaction [[molecule.datomic.base.ast.transactionModel.Statement Statement]]'s
   */
  def testDbWith(txMolecules: Future[Seq[Statement]]*)
                (implicit ec: ExecutionContext): Future[Unit]


  /** Get out of test mode and back to live db. */
  def useLiveDb(): Unit


  // Datomic shared Peer/Client api --------------------------------------------

  /** Get current adhoc/test/live db. */
  def db(implicit ec: ExecutionContext): Future[DatomicDb]


  def transact(edn: String)(implicit ec: ExecutionContext): Future[TxReport]


  def transact(stmtsReader: Reader)(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("transact(stmtsReader: Reader)"))


  def transact(javaStmts: jList[_])(implicit ec: ExecutionContext): Future[TxReport] =
    Future.failed(jvmOnly("transact(javaStmts: jList[_])"))


  /** Query Datomic directly with optional Scala inputs.
   * {{{
   *   // Sample data
   *   Ns.str.int.get.map(_ ==> List(
   *     ("Liz", 37),
   *     ("Ben", 42),
   *   ))
   *
   *   // Start out easily with a Datomic query from inspect output
   *   Ns.str.int.inspectGet // shows datomic query...
   *
   *   // Paste Datomic query into `q` call
   *   conn.q("""[:find  ?b ?c
   *            | :where [?a :Ns/str ?b]
   *            |        [?a :Ns/int ?c]]""".stripMargin) === List(
   *     List("Liz", 37),
   *     List("Ben", 42)
   *   )
   *
   *   // Modify Datomic query to see result, for instance
   *   // by adding input to query and applying input value
   *   conn.q("""[:find  ?b ?c
   *            | :in    $ ?c
   *            | :where [?a :Ns/str ?b]
   *            |        [?a :Ns/int ?c]]""".stripMargin, 42) === List(
   *     List("Ben", 42)
   *   )
   * }}}
   *
   * @param datalogQuery Datomic query string
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
  def sync(implicit ec: ExecutionContext): Conn


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
  def sync(t: Long)(implicit ec: ExecutionContext): Conn



  // Tx fn helpers -------------------------------------------------------------

  // Needs to be public since tx functions use id
  def stmts2java(stmts: Seq[Statement]): jList[jList[_]] =
    throw jvmPeerOnly("stmts2java(stmts: Seq[Statement])")

  private[molecule] def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] =
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


  private[molecule] def usingAdhocDbView(dbView: DbView): Conn = {
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


  private[molecule] def jsQueryTpl[Tpl](
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    packed2tpl: Iterator[String] => Tpl,
  )(implicit ec: ExecutionContext): Future[List[Tpl]] = Future.failed(jsOnly("jsQueryTpl"))

  private[molecule] def jsQueryObj[Obj](
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: nodes.Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    packed2obj: Iterator[String] => Obj,
  )(implicit ec: ExecutionContext): Future[List[Obj]] = Future.failed(jsOnly("jsQueryObj"))

  private[molecule] def jsQueryJson(
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: nodes.Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]]
  )(implicit ec: ExecutionContext): Future[String] = Future.failed(jsOnly("jsQueryJson"))


  // Internal convenience method
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
