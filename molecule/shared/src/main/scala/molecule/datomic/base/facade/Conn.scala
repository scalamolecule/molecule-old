package molecule.datomic.base.facade

import java.io.Reader
import java.util.{Date, Collection => jCollection, List => jList}
import molecule.core.ast.elements.Model
import molecule.core.marshalling.{DbProxy, MoleculeRpc, QueryResult}
import molecule.core.ops.ColOps
import molecule.core.transform.ModelTransformer
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.transform.Query2String
import molecule.datomic.base.util.TempIdFactory
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}


/** Facade to Datomic Connection.
  * */
trait Conn extends ColOps {

  /** Flag to indicate if we are on the JS or JVM platform */
  val isJsPlatform: Boolean

  val tempId = TempIdFactory

  lazy val dbProxy: DbProxy = ???

  /** */
  lazy val moleculeRpc: MoleculeRpc = ???

  def usingTempDb(tempDb: TempDb): Conn

  /** Flag to indicate if live database is used */
  def liveDbUsed: Boolean

  /** Manually apply a database to use.
    *
    * @param db
    */
  def testDb(db: DatomicDb): Unit

  /** Use test database as of now. */
  def testDbAsOfNow: Unit

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
    *   Person.name.get === List("Ben", "Liz")
    *
    *   // Multiple transactions can be applied
    *   conn.testDbWith(
    *     Person.name("Joe").getSaveTx,
    *     benId.getRetractTx
    *   )
    *   Person.name.get === List("Liz", "Joe")
    * }}}
    *
    * @param txData List of List of transaction [[molecule.datomic.base.ast.transactionModel.Statement Statement]]'s
    */
  def testDbWith(txData: Future[Seq[Statement]]*)
                (implicit ec: ExecutionContext): Future[Unit]

  /** Use test database with temporary raw Java transaction data. */
  def testDbWith(txDataJava: jList[jList[_]])(implicit ec: ExecutionContext): Future[Unit]

  /* testDbHistory not implemented.
   * Instead, use `testDbAsOfNow`, make changes and get historic data with getHistory calls.
   * */

  /** Get out of test mode and back to live db. */
  def useLiveDb: Unit

  /** Get current test/live db. Test db has preference. */
  def db: DatomicDb

  /** Convenience method to retrieve entity. */
  def entity(id: Any): DatomicEntity


  /** Transact edn files or other raw transaction data.
    * {{{
    *   val data_rdr2 = new FileReader("examples/resources/seattle/seattle-data1a.dtm")
    *   val rawTxStmts = Util.readAll(data_rdr2).get(0).asInstanceOf[java.util.List[Object]]
    *
    *   // transact
    *   val result: TxReport = conn.transact(rawTxStmts)
    * }}}
    *
    * @param javaStmts Raw transaction data, typically from edn file.
    * @return [[molecule.datomic.base.facade.TxReport TxReport]]
    *         // */
  //  def transactRaw(javaStmts: jList[_], scalaStmts: Seq[Statement] = Nil): TxReport
  //
  //  def transact(stmtsReader: Reader, scalaStmts: Seq[Statement]): TxReport
  //
  //  def transact(edn: String, scalaStmts: Seq[Statement]): TxReport
  //
  //  def transact(stmtsReader: Reader): TxReport
  //
  //  def transact(edn: String): TxReport
  //
  //  /** Transact Seq of Seqs of [[molecule.datomic.base.ast.transactionModel.Statement Statement]]s
  //    *
  //    * @param scalaStmts
  //    * @return [[molecule.datomic.base.facade.TxReport TxReport]]
  //    */
  //  def transact(scalaStmts: Seq[Statement]): TxReport

  /** Asynchronously transact edn files or other raw transaction data.
    * {{{
    *   val data_rdr2 = new FileReader("examples/resources/seattle/seattle-data1a.dtm")
    *   val rawTxStmts = Util.readAll(data_rdr2).get(0).asInstanceOf[java.util.List[Object]]
    *
    *   // transact
    *   val result: Future[TxReport] = conn.transactAsync(rawTxStmts)
    * }}}
    *
    * @param javaStmts Raw transaction data, typically from edn file.
    * @return Future with [[molecule.datomic.base.facade.TxReport TxReport]] with result of transaction
    */
  def transactRaw(
    javaStmts: jList[_],
    scalaStmts: Future[Seq[Statement]] = Future.successful(Seq.empty[Statement])
  )(implicit ec: ExecutionContext): Future[TxReport]

  def transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport]

  def transact(edn: String, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport]

  def transact(stmtsReader: Reader)
              (implicit ec: ExecutionContext): Future[TxReport]

  def transact(edn: String)
              (implicit ec: ExecutionContext): Future[TxReport]

  /** Asynchronously transact Seq of Seqs of [[molecule.datomic.base.ast.transactionModel.Statement Statement]]s
    *
    * @param scalaStmts
    * @return [[molecule.datomic.base.facade.TxReport TxReport]]
    */
  def transact(scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport]


  private[molecule] def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_]


  // Query api ....................................................

  /** Query Datomic directly with optional Scala inputs.
    * {{{
    *   // Sample data
    *   Ns.str.int.get === List(
    *     ("Liz", 37),
    *     ("Ben", 42),
    *   )
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
    * @param query  Datomic query string
    * @param inputs Optional input(s) to query
    * @return List[List[AnyRef]]
    * */
  def q(query: String, inputs: Any*)
       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]]

  /** Query Datomic directly with db value and optional Scala inputs.
    * {{{
    *   // Sample data
    *   Ns.str.int.get === List(
    *     ("Liz", 37),
    *     ("Ben", 42),
    *   )
    *
    *   // Start out easily with a Datomic query from inspect output
    *   Ns.str.int.inspectGet // shows datomic query...
    *
    *   // Paste Datomic query into `q` call and use some db value
    *   conn.q(conn.db,
    *          """[:find  ?b ?c
    *            | :where [?a :Ns/str ?b]
    *            |        [?a :Ns/int ?c]]""".stripMargin) === List(
    *     List("Liz", 37),
    *     List("Ben", 42)
    *   )
    *
    *   // Modify Datomic query to see result, for instance
    *   // by adding input to query and applying input value
    *   conn.q(conn.db,
    *          """[:find  ?b ?c
    *            | :in    $ ?c
    *            | :where [?a :Ns/str ?b]
    *            |        [?a :Ns/int ?c]]""".stripMargin,
    *          Seq(42) // input values in list
    *    ) === List(
    *     List("Ben", 42)
    *   )
    * }}}
    *
    * @param db     Any Datomic Database value (could be asOf(x) etc)
    * @param query  Datomic query string
    * @param inputs Seq of optional input(s) to query
    * @return List[List[AnyRef]]
    * */
  def q(db: DatomicDb, query: String, inputs: Seq[Any])
       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]]


  /** Query Datomic directly with optional Scala inputs and get raw Java result.
    * {{{
    *   // Sample data
    *   Ns.str.int.get === List(
    *     ("Liz", 37),
    *     ("Ben", 42),
    *   )
    *
    *   // Start out easily with a Datomic query from inspect output
    *   Ns.str.int.inspectGet // shows datomic query...
    *
    *   // Paste Datomic query into `q` call
    *   conn.q("""[:find  ?b ?c
    *            | :where [?a :Ns/str ?b]
    *            |        [?a :Ns/int ?c]]""".stripMargin)
    *       .toString === """[["Liz" 37], ["Ben" 42]]"""
    *
    *   // Modify Datomic query to see result, for instance
    *   // by adding input to query and applying input value
    *   conn.q("""[:find  ?b ?c
    *            | :in    $ ?c
    *            | :where [?a :Ns/str ?b]
    *            |        [?a :Ns/int ?c]]""".stripMargin, 42).toString === """[["Ben" 42]]"""
    * }}}
    *
    * @param query  Datomic query string
    * @param inputs Optional input(s) to query
    * @return java.util.Collection[java.util.List[AnyRef]]
    * */
  def qRaw(query: String, inputs: Any*)
          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]]

  /** Query Datomic directly with db value and optional Scala inputs and get raw Java result.
    * {{{
    *   // Sample data
    *   Ns.str.int.get === List(
    *     ("Liz", 37),
    *     ("Ben", 42),
    *   )
    *
    *   // Get some Datomic query from inspect output
    *   Ns.str.int.inspectGet // shows datomic query...
    *
    *   // Paste Datomic query into `q` call and use some db value
    *   conn.q(conn.db,
    *          """[:find  ?b ?c
    *            | :where [?a :Ns/str ?b]
    *            |        [?a :Ns/int ?c]]""".stripMargin)
    *       .toString === """[["Liz" 37], ["Ben" 42]]"""
    *
    *   // Modify Datomic query to see result, for instance
    *   // by adding input to query and applying input value
    *   conn.q(conn.db,
    *          """[:find  ?b ?c
    *            | :in    $ ?c
    *            | :where [?a :Ns/str ?b]
    *            |        [?a :Ns/int ?c]]""".stripMargin,
    *          Seq(42) // input values in list
    *    ).toString === """[["Ben" 42]]"""
    * }}}
    *
    * @param db      Any Datomic Database value (could be asOf(x) etc)
    * @param query   Datomic query string
    * @param inputs0 Seq of optional input(s) to query
    * @return java.util.Collection[java.util.List[AnyRef]]
    * */
  def qRaw(db: DatomicDb, query: String, inputs0: Seq[Any])
          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]]


  /** Query Datomic with Model and Query to get raw Java data.
    * <br><br>
    * Will transparently relegate query depending on Model to:
    *
    * - Datalog query execution
    * - Datoms API accessing index
    * - Log API accessing log
    *
    * Return type (tuple matching the molecule) is the same for all 3 APIs so that
    * application code can query and access data of all molecules the same way.
    *
    * @param model Model instance
    * @param query Query instance
    * @return java.util.Collection[java.util.List[AnyRef]]
    * */
  def query(model: Model, query: Query)
           (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]]

  private[molecule] def _query(
    model: Model,
    query: Query,
    _db: Option[DatomicDb] = None
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]]

  private[molecule] def _index(model: Model)
                              (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]]


  def modelTransformer(model: Model): ModelTransformer = ModelTransformer(this, model)

  private[molecule] def stmts2java(stmts: Seq[Statement]): jList[jList[_]]

  def inspect(
    clazz: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit


  // JS query rpc api .........................................

  private[molecule] def jsQuery[Tpl](
    query: Query,
    n: Int,
    indexes: List[(Int, Int, Int, Int)],
    qr2tpl: QueryResult => Int => Tpl
  )(implicit ec: ExecutionContext): Future[List[Tpl]] = Future {
    val q2s          = Query2String(query)
    val datalogQuery = q2s.multiLine(60)
    val p            = q2s.p
    val rules        = if (query.i.rules.isEmpty) Nil else Seq("[" + (query.i.rules map p mkString " ") + "]")
    val (l, ll, lll) = marshallInputs(query)
    // Fetch QueryResult with Ajax call via typed Sloth wire
    moleculeRpc.query(dbProxy, datalogQuery, rules, l, ll, lll, n, indexes)
      .map {
        qr =>
          val maxRows    = if (n == -1) qr.maxRows else n
          val tplsBuffer = new ListBuffer[Tpl]
          val columns    = qr2tpl(qr) // macro generated extractor
          var rowIndex   = 0
          while (rowIndex < maxRows) {
            tplsBuffer += columns(rowIndex)
            rowIndex += 1
          }
          tplsBuffer.toList
      }
  }.flatten

  private[molecule] def jsGetAttrValues(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    moleculeRpc.getAttrValues(dbProxy, datalogQuery, card, tpe)

  private[molecule] def jsEntityAttrKeys(
    eid: Long
  )(implicit ec: ExecutionContext): Future[List[String]] =
    moleculeRpc.entityAttrKeys(dbProxy, eid)
}
