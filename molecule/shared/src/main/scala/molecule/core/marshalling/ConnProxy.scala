package molecule.core.marshalling

import java.io.Reader
import java.util
import java.util.{Date, List => jList}
import molecule.core.ast.elements.Model
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReport}
import scala.concurrent.{ExecutionContext, Future}

/** Dummy connection to satisfy implicit parameter of molecule calls on client side.
  *
  * Used to cary information enabling marshalling between client and server.
  */
trait ConnProxy extends Conn {

  override def usingTempDb(tempDb: TempDb): Conn = ???

  override def liveDbUsed: Boolean = ???

  override def testDb(db: DatomicDb): Unit = ???

  override def testDbAsOfNow: Unit = ???

  override def testDbAsOf(t: Long): Unit = ???

  override def testDbAsOf(d: Date): Unit = ???

  override def testDbAsOf(txR: TxReport): Unit = ???

  override def testDbSince(tOrTx: Long): Unit = ???

  override def testDbSince(d: Date): Unit = ???

  override def testDbSince(txR: TxReport): Unit = ???

  override def testDbWith(txData: Future[Seq[Statement]]*): Future[Unit] = ???

  override def testDbWith(txDataJava: util.List[util.List[_]]): Unit = ???

  override def useLiveDb: Unit = ???

  override def db: DatomicDb = ???

  override def entity(id: Any): DatomicEntity = ???

//  override def transactRaw(javaStmts: util.List[_], scalaStmts: Seq[Statement]): TxReport = ???
//
//  override def transact(stmtsReader: Reader, scalaStmts: Seq[Statement]): TxReport = ???
//
//  override def transact(edn: String, scalaStmts: Seq[Statement]): TxReport = ???
//
//  override def transact(stmtsReader: Reader): TxReport = ???
//
//  override def transact(edn: String): TxReport = ???
//
//  override def transact(scalaStmts: Seq[Statement]): TxReport = ???

  override def transactRaw(javaStmts: util.List[_], scalaStmts: Future[Seq[Statement]])(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(edn: String, scalaStmts: Future[Seq[Statement]])(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(stmtsReader: Reader)(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(edn: String)(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(scalaStmts: Future[Seq[Statement]])(implicit ec: ExecutionContext): Future[TxReport] = ???

  private[molecule] override def buildTxFnInstall(
    txFn: String,
    args: Seq[Any]): util.List[_] = ???


  // MoleculeRpc api ------------------------------------------------------

  private[molecule] def qAsync[Tpl](
    query: Query,
    n: Int,
    indexes: List[(Int, Int, Int, Int)],
    qr2tpl: QueryResult => Int => Tpl
  )(implicit ec: ExecutionContext): Future[List[Tpl]]

  private[molecule] override def getAttrValuesAsync(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    moleculeRpc.getAttrValuesAsync(dbProxy, datalogQuery, card, tpe)

  private[molecule] override def entityAttrKeys(
    eid: Long
  )(implicit ec: ExecutionContext): Future[List[String]] =
    moleculeRpc.entityAttrKeys(dbProxy, eid)

  override def q(query: String, inputs: Any*): List[List[AnyRef]] = ???

  override def q(db: DatomicDb, query: String, inputs: Seq[Any]): List[List[AnyRef]] = ???

  override def qRaw(query: String, inputs: Any*): util.Collection[util.List[AnyRef]] = ???

  override def qRaw(db: DatomicDb, query: String, inputs0: Seq[Any]): util.Collection[util.List[AnyRef]] = ???

  override def query(model: Model, query: Query): util.Collection[util.List[AnyRef]] = ???

  override def _query(model: Model, query: Query, _db: Option[DatomicDb]): util.Collection[util.List[AnyRef]] = ???

  override def _index(model: Model): util.Collection[util.List[AnyRef]] = ???

  private[molecule] override def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = ???

  override def inspect(clazz: String, threshold: Int, max: Int, showStackTrace: Boolean, maxLevel: Int, showBi: Boolean)(id: Int, params: Any*): Unit = ???
}