package molecule.datomic.base.facade

import java.io.Reader
import java.util
import java.util.{Collection => jCollection, List => jList}
import molecule.core.ast.elements.Model
import molecule.core.marshalling._
import molecule.core.marshalling.nodes.Obj
import molecule.core.ops.ColOps
import molecule.datomic.base.ast.dbView.DbView
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.util.TempIdFactory
import scala.concurrent.{ExecutionContext, Future}


private[molecule] trait ConnBase extends ColOps with Serializations {

  private[molecule] val isJsPlatform: Boolean = false

  private[molecule] val tempId = TempIdFactory

  private[molecule] val defaultConnProxy: ConnProxy = null
  private[molecule] var connProxy       : ConnProxy = defaultConnProxy

  // Temporary db for ad-hoc queries against time variation dbs
  // (takes precedence over _testDb in Conn_Peer and Conn_Client)
  private[molecule] var _adhocDbView: Option[DbView] = None

  private[molecule] def liveDbUsed: Boolean = ???

  private[molecule] lazy val rpc: MoleculeRpc = ???

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

  protected def debug(prefix: String, suffix: String = "") = {
    val p = prefix + " " * (4 - prefix.length)
    println(s"$p  ${connProxy.testDbStatus}  ${connProxy.testDbView}   " + suffix)
  }


  private[molecule] def transact(
    edn: String,
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = ???

  private[molecule] def transact(
    stmtsReader: Reader,
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = ???

  private[molecule] def transact(
    javaStmts: jList[_],
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = ???

  private[molecule] def transact(
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport]


  private[molecule] def rawQuery(
    query: String,
    inputs: Any*
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???

  private[molecule] def jvmQuery(
    model: Model,
    query: Query
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???

  private[molecule] def indexQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???

  private[molecule] def datalogQuery(
    model: Model,
    query: Query,
    _db: Option[DatomicDb] = None
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???



  private[molecule] def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] =
    new util.ArrayList[Any](0)

  // used by txFns. todo: can we make this private to avoid public exposure?
  def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = ???

  private[molecule] def inspect(
    header: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit


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
  )(implicit ec: ExecutionContext): Future[List[Tpl]] = ???

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
  )(implicit ec: ExecutionContext): Future[List[Obj]] = ???

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
  )(implicit ec: ExecutionContext): Future[String] = ???


  private[molecule] def getAttrValues(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]]


  private[molecule] def getEntityAttrKeys(
    query: String
  )(implicit ec: ExecutionContext): Future[List[String]]
}
