package molecule.datomic.peer.facade

import java.util.{Date, Collection => jCollection, List => jList}
import molecule.core.ast.elements._
import molecule.core.exceptions._
import molecule.core.util.{Helpers, QueryOpsClojure}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.query.{Query, QueryExpr}
import molecule.datomic.base.ast.tempDb._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.{Conn, Conn_Datomic, DatomicDb, TxReport}
import molecule.datomic.base.transform.{Query2String, QueryOptimizer}
import scala.concurrent.{ExecutionContext, Future, blocking}
import scala.util.control.NonFatal

/** Factory methods to create facade to Datomic Connection. */
object Conn_Peer {
  def apply(uri: String): Conn_Peer = ???

//  def apply(datomicConn: datomic.Connection): Conn_Peer = new Conn_Peer(datomicConn)

  // Constructor for transaction functions where db is supplied inside transaction by transactor
  def apply(txDb: AnyRef): Conn_Peer = ???
}


/** Facade to Datomic connection for peer api.
  * */
class Conn_Peer()
  extends Conn_Datomic with Helpers {

  // In-memory fixed test db for integration testing of domain model
  // (takes precedence over live db)
//  protected var _testDb: Option[Database] = None


  def usingTempDb(tempDb: TempDb): Conn = {
    _adhocDb = Some(tempDb)
    this
  }

  def liveDbUsed: Boolean = ???

  def testDb(db: DatomicDb): Unit = ???

  // Reverse datoms from next timePoint after as-of t until end
  private def cleanFrom(nextTimePoint: Any): Unit =  ???

  def testDbAsOf(tOrTx: Long): Unit = cleanFrom(tOrTx + 1)

  def testDbAsOf(txR: TxReport): Unit = cleanFrom(txR.t + 1)

  def testDbAsOf(d: Date): Unit = {
    // Cleanup everything 1 ms after this date/time
    cleanFrom(new Date(d.toInstant.plusMillis(1).toEpochMilli))
  }

  def testDbAsOfNow: Unit =  ???

  def testDbSince(t: Long): Unit =  ???

  def testDbSince(d: Date): Unit =  ???

  def testDbSince(txR: TxReport): Unit =  ???

  def testDbWith(txData: Seq[Seq[Statement]]*): Unit =  ???

  /** Use test database with temporary raw Java transaction data. */
  def testDbWith(txDataJava: jList[jList[AnyRef]]): Unit =  ???

  def useLiveDb: Unit =  ???

//  private def getAdhocDb: Database = {
//    val baseDb : Database = _testDb.getOrElse(peerConn.db)
//    val adhocDb: Database = _adhocDb.get match {
//      case AsOf(TxLong(t))  => baseDb.asOf(t)
//      case AsOf(TxDate(d))  => baseDb.asOf(d)
//      case Since(TxLong(t)) => baseDb.since(t)
//      case Since(TxDate(d)) => baseDb.since(d)
//      case With(tx)         => {
//        val txReport = TxReport_Peer(baseDb.`with`(tx))
//        val db       = txReport.dbAfter.asOf(txReport.t)
//        db
//      }
//      case History          => baseDb.history()
//    }
//    _adhocDb = None
//    adhocDb
//  }

  def db: DatomicDb =  ???

  def entity(id: Any): DatomicEntity = db.entity(this, id)


  def transact(javaStmts: jList[_], scalaStmts: Seq[Seq[Statement]] = Nil): TxReport =  ???

  def transactAsync(javaStmts: jList[_], scalaStmts: Seq[Seq[Statement]] = Nil)
                   (implicit ec: ExecutionContext): Future[TxReport] =  ???

  def qRaw(db: DatomicDb, query: String, inputs0: Seq[Any]): jCollection[jList[AnyRef]] =  ???

  def query(model: Model, query: Query): jCollection[jList[AnyRef]] = model.elements.head match {
    case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _) => _index(model)
    case _                                                           => _query(model, query)
  }

  // Datalog query execution
  def _query(model: Model, query: Query, _db: Option[DatomicDb] = None): jCollection[jList[AnyRef]] = ???
//  {
//    val optimizedQuery  = QueryOptimizer(query)
//    val p               = (expr: QueryExpr) => Query2String(optimizedQuery).p(expr)
//    val rules           = "[" + (query.i.rules map p mkString " ") + "]"
//    val adhocDb         = _db.getOrElse(db).getDatomicDb
//    val first           = if (query.i.rules.isEmpty) Seq(adhocDb) else Seq(adhocDb, rules)
//    val inputsEvaluated = QueryOpsClojure(query).inputsWithKeyword
//    val allInputs       = first ++ inputsEvaluated
//    try {
//      blocking {
//        Peer.q(query.toMap, allInputs: _*)
//      }
//    } catch {
//      case ex: Throwable if ex.getMessage startsWith "processing" =>
//        val builder = Seq.newBuilder[String]
//        var e       = ex
//        while (e.getCause != null) {
//          builder += e.getMessage
//          e = e.getCause
//        }
//        throw new QueryException(e, model, query, allInputs, p, builder.result())
//      case NonFatal(ex)                                           =>
//        throw new QueryException(ex, model, query, allInputs, p)
//    }
//  }

  // Datoms API providing direct access to indexes
  def _index(model: Model): jCollection[jList[AnyRef]] =  ???

//  def sync: ListenableFuture[Database] = peerConn.sync()
//
//  def sync(t: Long): ListenableFuture[Database] = peerConn.sync(t)
//
//  def syncIndex(t: Long): ListenableFuture[Database] = peerConn.syncIndex(t)
}
