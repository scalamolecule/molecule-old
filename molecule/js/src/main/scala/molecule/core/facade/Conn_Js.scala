package molecule.core.facade

import java.io.Reader
import java.util.{Date, Collection => jCollection, List => jList}
//import boopickle.Default._
import molecule.core.ast.elements.Model
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.marshalling.{ConnProxy, MoleculeRpc, MoleculeWebClient}
import molecule.core.ops.ColOps
import molecule.core.util.Helpers
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReport}
import scala.concurrent.{ExecutionContext, Future}


/** Client db connection.
  *
  * Used to cary information enabling marshalling on both client and server side.
  *
  * Make a similar subclass of ConnProxy like this one in order to use an
  * alternative rpc implementation.
  *
  * @param defaultConnProxy Db coordinates to access db on server side
  */
case class Conn_Js(defaultConnProxy: ConnProxy) extends Conn with ColOps with Helpers {

  def ???(i: Int) : Nothing = throw MoleculeException(s"Unexpected method call ($i) on JS side in Conn_Js")

  val isJsPlatform: Boolean = true

  override lazy val rpc: MoleculeRpc = MoleculeWebClient.rpc

  def liveDbUsed: Boolean = ???(1)

  def testDb(db: DatomicDb): Unit = ???(2)

  def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(0))))
  }

  def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(t))))
    debug("js1")
  }

  def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxDate(d))))
  }

  def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(txR.t))))
  }

  def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxLong(t))))
  }

  def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxDate(d))))
  }

  def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxLong(txR.t))))
  }

  def testDbWith(txMolecules: Future[Seq[Statement]]*)
                (implicit ec: ExecutionContext): Future[Unit] = {
    Future.sequence(txMolecules).map { stmtss =>
      val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, this)
      updateTestDbView(Some(With(stmtsEdn, uriAttrs)))
    }
  }

  def useLiveDb(): Unit = updateTestDbView(None, -1)

  def db: DatomicDb = DatomicDb_Js(rpc, connProxy)

  def entity(id: Any): DatomicEntity = db.entity(this, id)


  def transactRaw(
    javaStmts: jList[_],
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = ???(3)

  def transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] = ???(3)

  def transact(edn: String, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] = ???(4)

  def transact(stmtsReader: Reader)
              (implicit ec: ExecutionContext): Future[TxReport] = ???(5)

  def transact(edn: String)
              (implicit ec: ExecutionContext): Future[TxReport] =
    rpc.transact(connProxy, (edn, Set.empty[String]))

  def transact(scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] = {
    for {
      stmts <- scalaStmts
      result <- rpc.transact(connProxy, Stmts2Edn(stmts, this))
    } yield result
  }

  private[molecule] def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] = ???(6)


  def q(query: String, inputs: Any*)
       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = ???(7)

  def q(db: DatomicDb, query: String, inputs: Seq[Any])
       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = ???(8)

  def qRaw(query: String, inputs: Any*)
          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(9)

  def qRaw(db: DatomicDb, query: String, inputs0: Seq[Any])
          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(10)

  def query(model: Model, query: Query)
           (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(11)

  def _query(model: Model, query: Query, _db: Option[DatomicDb])
            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(12)

  def _index(model: Model)
            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(13)

  def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = ???(14)

  def inspect(
    header: String,
    threshold: Int,
    max: Int,
    showStackTrace: Boolean,
    maxLevel: Int,
    showBi: Boolean
  )(id: Int, params: Any*): Unit = ???(15)
}
