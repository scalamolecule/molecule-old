package molecule.core.marshalling

import java.io.Reader
import java.util
import java.util.{Date, List => jList}
import boopickle.Default._
import molecule.core.ast.elements.Model
import molecule.core.data.SchemaTransaction
import molecule.core.ops.ColOps
import molecule.core.util.Helpers
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReport}
import scala.concurrent.{ExecutionContext, Future}

/** Client db connection.
  *
  * Used to cary information enabling marshalling on both client and server side.
  *
  * Make a similar subclass of ConnProxy like this one in order to use an
  * alternative moleculeRpc implementation.
  *
  * @param dbProxy0 Db coordinates to access db on server side
  */
trait Conn_Js extends Conn with ColOps with Helpers {

  val isJsPlatform: Boolean = true

  override lazy val moleculeRpc: MoleculeRpc = MoleculeWebClient.moleculeRpc

  override def usingTempDb(tempDb: TempDb): Conn = ???

  override def liveDbUsed: Boolean = ???

  override def testDb(db: DatomicDb): Unit = ???

  override def testDbAsOfNow: Unit = ???

  override def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def testDbSince(tOrTx: Long)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def testDbWith(txData: Future[Seq[Statement]]*)
                         (implicit ec: ExecutionContext): Future[Unit] = ???

  override def testDbWith(txDataJava: util.List[util.List[_]])
                         (implicit ec: ExecutionContext): Future[Unit] = ???

  override def useLiveDb: Unit = ???

  override def db: DatomicDb = ???

  override def entity(id: Any): DatomicEntity = ???


  override def transactRaw(
    javaStmts: util.List[_],
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])
                       (implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(edn: String, scalaStmts: Future[Seq[Statement]])
                       (implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(stmtsReader: Reader)
                       (implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(edn: String)
                       (implicit ec: ExecutionContext): Future[TxReport] = ???

  override def transact(scalaStmts: Future[Seq[Statement]])
                       (implicit ec: ExecutionContext): Future[TxReport] = ???

  private[molecule] override def buildTxFnInstall(
    txFn: String,
    args: Seq[Any]): util.List[_] = ???


  override def q(query: String, inputs: Any*)
                (implicit ec: ExecutionContext): Future[ List[List[AnyRef]]] = ???

  override def q(db: DatomicDb, query: String, inputs: Seq[Any])
                (implicit ec: ExecutionContext): Future[ List[List[AnyRef]]] = ???

  override def qRaw(query: String, inputs: Any*)
                   (implicit ec: ExecutionContext): Future[ util.Collection[util.List[AnyRef]]] = ???

  override def qRaw(db: DatomicDb, query: String, inputs0: Seq[Any])
                   (implicit ec: ExecutionContext): Future[ util.Collection[util.List[AnyRef]]] = ???

  override def query(model: Model, query: Query)
                    (implicit ec: ExecutionContext): Future[ util.Collection[util.List[AnyRef]]] = ???

  override def _query(model: Model, query: Query, _db: Option[DatomicDb])
                     (implicit ec: ExecutionContext): Future[ util.Collection[util.List[AnyRef]]] = ???

  override def _index(model: Model)
                     (implicit ec: ExecutionContext): Future[ util.Collection[util.List[AnyRef]] ]= ???

  override def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = ???

  override def inspect(
    clazz: String,
    threshold: Int,
    max: Int,
    showStackTrace: Boolean,
    maxLevel: Int,
    showBi: Boolean
  )(id: Int, params: Any*): Unit = ???
}


object Conn_Js {
  def apply(dbProxy0: DbProxy): Conn_Js = new Conn_Js {
    override lazy val dbProxy = dbProxy0
  }

  def inMem(schemaTransaction: SchemaTransaction)
           (implicit ec: ExecutionContext): Future[Conn_Js] = Future(apply(
    DatomicInMemProxy(
      schemaTransaction.datomicPeer,
      schemaTransaction.attrMap
    )
  ))
}