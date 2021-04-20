package molecule.core.marshalling

import java.io.Reader
import java.util
import java.util.Date
import molecule.core.ast.elements.Model
import molecule.core.data.SchemaTransaction
import molecule.core.transform.Model2Statements
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReport}
import scala.concurrent.{ExecutionContext, Future}

/** Dummy connection to satisfy implicit parameter of api calls on client side.
  *
  * Used to cary information enabling marshalling on both client and server side.
  *
  * @param dbProxy  Db coordinates to access db on server side
  */
trait ConnProxy extends Conn {

  override lazy val dbProxy: DbProxy = ???

  override lazy val moleculeRpc: MoleculeRpc = ???

  override def usingTempDb(tempDb: TempDb): Conn = ???

  override def liveDbUsed: Boolean = ???

  override def testDb(db: DatomicDb): Unit = ???

  override def testDbAsOfNow: Unit = ???

  override def testDbAsOf(t: Long): Unit = ???

  override def testDbAsOf(d: Date): Unit = ???

  override def testDbAsOf(txR: TxReport): Unit = ???

  override def testDbSince(t: Long): Unit = ???

  override def testDbSince(d: Date): Unit = ???

  override def testDbSince(txR: TxReport): Unit = ???

  override def testDbWith(txData: Seq[Seq[Statement]]*): Unit = ???

  override def testDbWith(txDataJava: util.List[util.List[AnyRef]]): Unit = ???

  override def useLiveDb: Unit = ???

  override def db: DatomicDb = ???

  override def entity(id: Any): DatomicEntity = ???

  override def transact(javaStmts: util.List[_], scalaStmts: Seq[Seq[Statement]]): TxReport = ???

  override def transact(stmtsReader: Reader, scalaStmts: Seq[Seq[Statement]]): TxReport = ???

  override def transact(edn: String, scalaStmts: Seq[Seq[Statement]]): TxReport = ???

  override def transact(stmtsReader: Reader): TxReport = ???

  override def transact(edn: String): TxReport = ???

  override def transact(scalaStmts: Seq[Seq[Statement]]): TxReport = ???

  override def transactAsync(javaStmts: util.List[_], scalaStmts: Seq[Seq[Statement]])(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transactAsync(stmtsReader: Reader, scalaStmts: Seq[Seq[Statement]])(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transactAsync(edn: String, scalaStmts: Seq[Seq[Statement]])(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transactAsync(stmtsReader: Reader)(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transactAsync(edn: String)(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transactAsync(scalaStmts: Seq[Seq[Statement]])(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def buildTxFnInstall(txFn: String, args: Seq[Any]): util.List[_] = ???

  override def q(query: String, inputs: Any*): List[List[AnyRef]] = ???

  override def q(db: DatomicDb, query: String, inputs: Seq[Any]): List[List[AnyRef]] = ???

  override def qRaw(query: String, inputs: Any*): util.Collection[util.List[AnyRef]] = ???

  override def qRaw(db: DatomicDb, query: String, inputs0: Seq[Any]): util.Collection[util.List[AnyRef]] = ???

  override def query(model: Model, query: Query): util.Collection[util.List[AnyRef]] = ???

  override def _query(model: Model, query: Query, _db: Option[DatomicDb]): util.Collection[util.List[AnyRef]] = ???

  override def _index(model: Model): util.Collection[util.List[AnyRef]] = ???

  override def model2stmts(model: Model): Model2Statements = ???

  override def inspect(clazz: String, threshold: Int, max: Int, showStackTrace: Boolean, maxLevel: Int, showBi: Boolean)(id: Int, params: Any*): Unit = ???
}