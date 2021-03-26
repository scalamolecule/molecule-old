package molecule.datomic.base.facade

import java.io.Reader
import java.util.{List => jList}
import molecule.core.ast.elements.Model
import molecule.core.transform.Model2Statements
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.Statement
import scala.concurrent.{ExecutionContext, Future}

/** Base class for Datomic connection facade.
  *
  */
trait Conn_Datomic extends Conn {

  // Temporary db for ad-hoc queries against time variation dbs
  // (takes precedence over test db)
  protected var _adhocDb: Option[TempDb] = None

  def transact(stmtsReader: Reader, scalaStmts: Seq[Seq[Statement]]): TxReport = ???

  def transact(edn: String, scalaStmts: Seq[Seq[Statement]]): TxReport = ???

  def transact(stmtsReader: Reader): TxReport = ???

  def transact(edn: String): TxReport = ???

  def transact(scalaStmts: Seq[Seq[Statement]]): TxReport = ???


  def transactAsync(stmtsReader: Reader, scalaStmts: Seq[Seq[Statement]])
                   (implicit ec: ExecutionContext): Future[TxReport] = ???

  def transactAsync(edn: String, scalaStmts: Seq[Seq[Statement]])
                   (implicit ec: ExecutionContext): Future[TxReport] = ???

  def transactAsync(stmtsReader: Reader)
                   (implicit ec: ExecutionContext): Future[TxReport] = ???

  def transactAsync(edn: String)
                   (implicit ec: ExecutionContext): Future[TxReport] = ???

  def transactAsync(scalaStmts: Seq[Seq[Statement]])
                   (implicit ec: ExecutionContext): Future[TxReport] = ???


  def q(db: DatomicDb, query: String, inputs: Seq[Any]): List[List[AnyRef]] = ???


  def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] = ???

  def model2stmts(model: Model): Model2Statements = ???

  def inspect(
    clazz: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit = ???
}
