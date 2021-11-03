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


  // Datomic facade ------------------------------------------------------------

  /** Get current test/live db. */
  def db: DatomicDb


  def transact(edn: String)(implicit ec: ExecutionContext): Future[TxReport]


  def transact(stmtsReader: Reader)(implicit ec: ExecutionContext): Future[TxReport] = jvmOnly


  def transact(javaStmts: jList[_])(implicit ec: ExecutionContext): Future[TxReport] = jvmOnly


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
  )(implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = jvmOnly


//  def sync: ListenableFuture[Database]
//
//  def sync(t: Long): ListenableFuture[Database]
//
//  def syncIndex(t: Long): ListenableFuture[Database]

  // Tx fn helpers -------------------------------------------------------------

  // Used by txFns. todo: can we avoid public exposure?
  def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = jvmPeerOnly

  private[molecule] def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] = jvmPeerOnly



  // Internal ------------------------------------------------------------------

  private[molecule] val isJsPlatform: Boolean

  private[molecule] val tempId = TempIdFactory

  private[molecule] val defaultConnProxy: ConnProxy = null

  private[molecule] var connProxy: ConnProxy = defaultConnProxy

  // Temporary db for ad-hoc queries against time variation dbs
  // (takes precedence over _testDb in Conn_Peer and Conn_Client)
  private[molecule] var _adhocDbView: Option[DbView] = None

  private[molecule] lazy val rpc: MoleculeRpc = jsOnly


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
  )(implicit ec: ExecutionContext): Future[TxReport] = jvmOnly

  private[molecule] def transact(
    stmtsReader: Reader,
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = jvmOnly

  private[molecule] def transactRaw(
    javaStmts: jList[_],
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = jvmOnly


  private[molecule] def rawQuery(
    query: String,
    inputs: Seq[AnyRef] = Nil
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = jvmOnly

  private[molecule] def jvmQuery(
    model: Model,
    query: Query
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = jvmOnly

  private[molecule] def indexQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = jvmOnly

  private[molecule] def datalogQuery(
    model: Model,
    query: Query,
    _db: Option[DatomicDb] = None
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = jvmOnly


  // JS query rpc api .........................................
  // (only used on js side)

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
  )(implicit ec: ExecutionContext): Future[List[Tpl]] = jsOnly

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
  )(implicit ec: ExecutionContext): Future[List[Obj]] = jsOnly

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
  )(implicit ec: ExecutionContext): Future[String] = jsOnly


  // Internal convenience method conn.entity(id) for conn.db.entity(conn, id)
  private[molecule] def entity(id: Any): DatomicEntity


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

  private def jvmOnly = throw MoleculeException("Method only implemented on JVM platform.")
  private def jvmPeerOnly = throw MoleculeException("Method only implemented on JVM platform for Peer api.")
  private def jsOnly = throw MoleculeException("Method only implemented for JS platform.")

  protected def debug(prefix: String, suffix: String = "") = {
    val p = prefix + " " * (4 - prefix.length)
    println(s"$p  ${connProxy.testDbStatus}  ${connProxy.testDbView}   " + suffix)
  }
}
