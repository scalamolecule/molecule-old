package molecule.datomic.base.facade

import java.io.{Reader, StringReader}
import java.net.URI
import java.util.{Collection => jCollection, List => jList, Map => jMap}
import clojure.lang.{PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import datomic.Peer.function
import datomic.Util
import datomic.Util.{list, read, readAll}
import molecule.core.ast.elements.Model
import molecule.core.transform.Model2Statements
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.{Statement, toJava}
import molecule.datomic.base.transform.Model2DatomicStmts
import molecule.datomic.base.util.Inspect
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

/** Base class for Datomic connection facade.
  *
  */
trait ConnBase extends Conn {

  // Temporary db for ad-hoc queries against time variation dbs
  // (takes precedence over test db)
  protected var _adhocDb: Option[TempDb] = None

  def transact(stmtsReader: Reader, scalaStmts: Seq[Statement]): TxReport =
    transactRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transact(edn: String, scalaStmts: Seq[Statement]): TxReport =
    transactRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transact(stmtsReader: Reader): TxReport =
    transactRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]])

  def transact(edn: String): TxReport =
    transactRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]])

  def transact(scalaStmts: Seq[Statement]): TxReport =
    transactRaw(toJava(scalaStmts), scalaStmts)


  def transactAsync(stmtsReader: Reader, scalaStmts: Seq[Statement])
                   (implicit ec: ExecutionContext): Future[TxReport] =
    transactAsyncRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transactAsync(edn: String, scalaStmts: Seq[Statement])
                   (implicit ec: ExecutionContext): Future[TxReport] =
    transactAsyncRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transactAsync(stmtsReader: Reader)
                   (implicit ec: ExecutionContext): Future[TxReport] =
    transactAsyncRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]])

  def transactAsync(edn: String)
                   (implicit ec: ExecutionContext): Future[TxReport] =
    transactAsyncRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]])

  def transactAsync(scalaStmts: Seq[Statement])
                   (implicit ec: ExecutionContext): Future[TxReport] =
    transactAsyncRaw(toJava(scalaStmts), scalaStmts)

  def q(query: String, inputs: Any*): List[List[AnyRef]] =
    q(db, query, inputs.toSeq)

  def q(db: DatomicDb, query: String, inputs: Seq[Any]): List[List[AnyRef]] = {
    val raw = qRaw(db, query, inputs)
    if (raw.isInstanceOf[PersistentVector]
      && !raw.asInstanceOf[PersistentVector].isEmpty
      && raw.asInstanceOf[PersistentVector].nth(0).isInstanceOf[PersistentArrayMap]) {
      raw.asInstanceOf[jCollection[jMap[_, _]]].asScala.toList.map { rows =>
        rows.asScala.toList.map { case (k, v) => k.toString -> v }
      }
    } else {
      raw.asScala.toList
        .map(_.asScala.toList
          .map {
            case set: clojure.lang.PersistentHashSet => set.asScala.toSet
            case uriImpl: URIImpl                    => new URI(uriImpl.toString)
            case bi: clojure.lang.BigInt             => BigInt(bi.toString)
            case other                               => other
          }
        )
    }
  }

  def qRaw(query: String, inputs: Any*): jCollection[jList[AnyRef]] =
    qRaw(db, query, inputs)

  def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] = {
    val params = args.indices.map(i => ('a' + i).toChar.toString)
    Util.list(Util.map(
      read(":db/ident"), read(s":$txFn"),
      read(":db/fn"), function(Util.map(
        read(":lang"), "java",
        read(":params"), list(read("txDb") +: read("txMetaData") +: params.map(read): _*),
        read(":code"), s"return $txFn(txDb, txMetaData, ${params.mkString(", ")});"
      ))
    ))
  }

  def model2stmts(model: Model): Model2Statements = Model2DatomicStmts(this, model)

  def inspect(
    clazz: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit =
    Inspect(clazz, threshold, max, showStackTrace, maxLevel, showBi)(id, params: _*)
}
