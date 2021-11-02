package molecule.datomic.base.facade

import java.io.Reader
import java.util.{Date, List => jList}
import molecule.core.ast.elements.Model
import molecule.core.transform.Model2Stmts
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView.DbView
import molecule.datomic.base.ast.transactionModel.Statement
import scala.concurrent.{ExecutionContext, Future}


/** Facade to Datomic Connection.
 * */
trait Conn extends ConnBase {


  def transact(edn: String)(implicit ec: ExecutionContext): Future[TxReport]

  // jvm only
  def transact(stmtsReader: Reader)(implicit ec: ExecutionContext): Future[TxReport] = ???
  def transact(javaStmts: jList[_])(implicit ec: ExecutionContext): Future[TxReport] = ???


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
  )(implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = ???


  // Test db ........................................................

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


  // Facades .....................................................

  /** Get current test/live db. */
  def db: DatomicDb

  /** Convenience method conn.entity(id) for conn.db.entity(conn, id) */
  def entity(id: Any): DatomicEntity


  // Internal methods using `this` need to be here ...............

  private[molecule] def usingAdhocDbView(dbView: DbView): Conn = {
    updateAdhocDbView(Some(dbView))
    this
  }

  private[molecule] def model2stmts(model: Model): Model2Stmts =
    Model2Stmts(isJsPlatform, this, model)
}
