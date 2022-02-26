package molecule.datomic.peer.facade

import java.util.{Date, Collection => jCollection, List => jList}
import java.{lang => jl, util => ju}
import datomic.Connection.DB_AFTER
import datomic._
import molecule.core.ast.elements._
import molecule.core.exceptions._
import molecule.core.marshalling._
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.metaSchema.MetaNs
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade._
import molecule.datomic.base.marshalling.DatomicRpc
import molecule.datomic.base.transform.Query2String
import molecule.datomic.base.util.QueryOpsClojure
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal


/** Facade to Datomic connection for peer api.
 * */
case class Conn_Peer(
  peerConn: datomic.Connection,
  override val defaultConnProxy: ConnProxy,
  system: String = ""
) extends Conn_Jvm with QueryIndex with QuerySchemaHistory {

  // Molecule api --------------------------------------------------------------

  final def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(peerConn.db)
  }

  final def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(peerConn.db.since(t))
  }

  final def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(peerConn.db.since(d))
  }

  final def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(peerConn.db.since(txR.t))
  }

  final def testDbWith(txMolecules: Future[Seq[Statement]]*)(implicit ec: ExecutionContext): Future[Unit] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      testDbWith(stmts2java(stmtss.flatten))
    }
  }

  /** Use test database with temporary raw Java transaction data. */
  final def testDbWith(txDataJava: jList[jList[_]])
                      (implicit ec: ExecutionContext): Future[Unit] = Future {
    tempId.reset()
    _testDb = Some(peerConn.db.`with`(txDataJava).get(DB_AFTER).asInstanceOf[Database])
  }

  final def useLiveDb(): Unit = {
    _testDb = None
  }


  // Datomic Peer api ----------------------------------------------------------

  final def db(implicit ec: ExecutionContext): Future[DatomicDb] = {
    if (_adhocDbView.isDefined) {
      // Adhoc db
      getAdhocDb.map(DatomicDb_Peer)

    } else if (connProxy.testDbStatus == 1 && _testDb.isEmpty) {
      // Test db
      try {
        val tempDb = connProxy.testDbView.get match {
          case AsOf(TxLong(0))          => Some(peerConn.db) // db as of now
          case AsOf(TxLong(t))          => Some(peerConn.db.asOf(t))
          case AsOf(TxDate(d))          => Some(peerConn.db.asOf(d))
          case Since(TxLong(t))         => Some(peerConn.db.since(t))
          case Since(TxDate(d))         => Some(peerConn.db.since(d))
          case History                  => Some(peerConn.db.history())
          case With(stmtsEdn, uriAttrs) =>
            val txData   = DatomicRpc().getJavaStmts(stmtsEdn, uriAttrs)
            val txReport = TxReport_Peer(peerConn.db.`with`(txData))
            Some(txReport.dbAfter)
          case other                    =>
            throw MoleculeException("Unexpected db DbView: " + other)
        }
        Future(DatomicDb_Peer(tempDb.get))
      } catch {
        case NonFatal(exc) => Future.failed(exc)
      }

    } else if (connProxy.testDbStatus == -1) {
      // Return to live db
      Future {
        _testDb = None
        updateTestDbView(None, 0)
        DatomicDb_Peer(peerConn.db)
      }

    } else if (_testDb.isDefined) {
      // Test db
      Future(DatomicDb_Peer(_testDb.get))

    } else {
      // Live db
      Future(DatomicDb_Peer(peerConn.db))
    }
  }

  final def sync: Conn = usingAdhocDbView(Sync(0))
  final def sync(t: Long): Conn = usingAdhocDbView(Sync(t))

  /**
   * Request that a background indexing job begin immediately.
   *
   * Background indexing will happen asynchronously. You can track indexing
   * completion with syncIndex(long).
   */
  final def requestIndex: Boolean =
    peerConn.requestIndex()

  /** Synchronize Peer database to have been indexed through time t.
   *
   * (only implemented for Peer api)
   *
   * Sets a flag with a time t on the connection to do the synchronization
   * on the first subsequent query. Hereafter the flag is removed.
   *
   * The synchronization guarantees a database that has been indexed through time t.
   * The synchronization does not involve calling the transactor.
   *
   * A Future with the synchronized database is returned for the query to use. The future
   * can take arbitrarily long to complete. Waiting code should specify a timeout.
   *
   * Only use `syncIndex` when coordination of multiple peer/client processes is required.
   *
   * @param ec an implicit execution context.
   * @return Peer Connection with synchronization flag set
   */
  final def syncIndex(t: Long): Conn_Peer =
    usingAdhocDbView(SyncIndex(t)).asInstanceOf[Conn_Peer]

  /** Synchronize Peer database to be aware of all schema changes up to time t.
   *
   * (only implemented for Peer api)
   *
   * Sets a flag with a time t on the connection to do the synchronization
   * on the first subsequent query. Hereafter the flag is removed.
   *
   * The synchronization guarantees a database aware of all schema changes up to
   * time t. The synchronization does not involve calling the transactor.
   *
   * A Future with the synchronized database is returned for the query to use. The future
   * can take arbitrarily long to complete. Waiting code should specify a timeout.
   *
   * Only use `syncSchema` when coordination of multiple peer/client processes is required.
   *
   * @param ec an implicit execution context.
   * @return Connection with synchronization flag set
   */
  final def syncSchema(t: Long): Conn_Peer =
    usingAdhocDbView(SyncSchema(t)).asInstanceOf[Conn_Peer]

  /** Synchronize Peer database to be aware of all excisions up to time t.
   *
   * (only implemented for Peer api)
   *
   * Sets a flag with a time t on the connection to do the synchronization
   * on the first subsequent query. Hereafter the flag is removed.
   *
   * The synchronization guarantees a database aware of all excisions up to a time t.
   * The synchronization does not involve calling the transactor.
   *
   * A Future with the synchronized database is returned for the query to use. The future
   * can take arbitrarily long to complete. Waiting code should specify a timeout.
   *
   * Only use `syncSchema` when coordination of multiple peer/client processes is required.
   *
   * @param ec an implicit execution context.
   * @return Connection with synchronization flag set
   */
  final def syncExcise(t: Long): Conn_Peer =
    usingAdhocDbView(SyncExcise(t)).asInstanceOf[Conn_Peer]


  /**
   * Gets the single transaction report queue associated with this connection,
   * creating it if necessary.
   *
   * The transaction report queue receives reports from all transactions in the
   * system. Objects on the queue have the same keys as returned by
   * transact(java.util.List).
   *
   * The returned queue may be consumed from more than one thread.
   *
   * Note that the returned queue does not block producers, and will consume
   * memory until you consume the elements from it. Reports will be added to the
   * queue at some point after the db has been updated.
   *
   * If this connection originated the transaction, the transaction future will
   * be notified first, before a report is placed on the queue.
   *
   * @return TxReportQueue
   */
  final def txReportQueue: TxReportQueue =
    TxReportQueue(peerConn.txReportQueue())

  /**
   * Removes the tx report queue associated with this connection.
   */
  final def removeTxReportQueue(): Unit =
    peerConn.removeTxReportQueue()

  /**
   * Reclaim storage garbage older than a certain age.
   *
   * As part of capacity planning for a Datomic system, you should schedule
   * regular (e.g daily, weekly) calls to gcStorage.
   *
   * @param olderThan
   */
  final def gcStorage(olderThan: Date): Unit =
    peerConn.gcStorage(olderThan)

  /**
   * Request the release of resources associated with this connection.
   *
   * Method returns immediately, resources will be released asynchronously.
   *
   * This method should only be called when the entire process is no longer
   * interested in the connection.
   *
   * Note that Datomic connections do not adhere to an acquire/use/release pattern.
   * They are thread-safe, cached, and long lived. Many processes (e.g. application
   * servers) will never call release.
   */
  final def release(): Unit =
    peerConn.release()


  // Schema --------------------------------------------------------------------

  protected def historyQuery(query: String)
                            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    db.map(db => Peer.q(
      query,
      db.getDatomicDb.asInstanceOf[Database].history(),
      db.getDatomicDb,
    ))
  }


  // Tx fn helpers -------------------------------------------------------------

  private[molecule] final override def buildTxFnInvoker(classpathTxFn: String, args: Seq[Any]): jList[_] = {
    val params = args.indices.map(i => ('a' + i).toChar.toString)
    Util.list(Util.map(
      Util.read(":db/ident"), Util.read(s":${classpathTxFn}_invoker"),
      Util.read(":db/fn"), Peer.function(Util.map(
        Util.read(":lang"), "java",
        Util.read(":params"), Util.list(
          Util.read("txDb") +: Util.read("txMetaData") +: params.map(Util.read): _*),
        Util.read(":code"), s"return $classpathTxFn(txDb, txMetaData, ${params.mkString(", ")});"
      ))
    ))
  }


  // Internal ------------------------------------------------------------------

  // In-memory fixed test db for integration testing
  // Takes precedence over live db (<molecule>.get etc).
  private var _testDb: Option[Database] = None


  // Internal setting of db for transaction functions.
  // Has to be public since tx fns are compiled in the user domain.
  protected def testDb(db: DatomicDb_Peer): Unit = {
    _testDb = Some(db.peerDb)
  }

  private def getAdhocDb(implicit ec: ExecutionContext): Future[Database] = {
    lazy val baseDb: Database = _testDb.getOrElse(peerConn.db)
    (_adhocDbView.get match {
      case AsOf(TxLong(t))          => Future(baseDb.asOf(t))
      case AsOf(TxDate(d))          => Future(baseDb.asOf(d))
      case Since(TxLong(t))         => Future(baseDb.since(t))
      case Since(TxDate(d))         => Future(baseDb.since(d))
      case History                  => Future(baseDb.history())
      case With(stmtsEdn, uriAttrs) => Future {
        val txData   = DatomicRpc().getJavaStmts(stmtsEdn, uriAttrs)
        val txReport = TxReport_Peer(baseDb.`with`(txData))
        txReport.dbAfter.asOf(txReport.t)
      }
      case Sync(0)                  => bridgeDatomicFuture(peerConn.sync(peerConn.db.basisT))
      case Sync(t: Long)            => bridgeDatomicFuture(peerConn.sync(t))
      case SyncIndex(t: Long)       => bridgeDatomicFuture(peerConn.syncIndex(t))
      case SyncSchema(t: Long)      => bridgeDatomicFuture(peerConn.syncSchema(t))
      case SyncExcise(t: Long)      => bridgeDatomicFuture(peerConn.syncExcise(t))
    }).map { db1 =>
      _adhocDbView = None
      connProxy = defaultConnProxy
      db1
    }
  }


  private[molecule] final override def transactRaw(
    javaStmts: jList[_],
    futScalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = try {
    def nextDateMs(d: Date): Date = new Date(d.toInstant.plusMillis(1).toEpochMilli)

    if (_adhocDbView.isDefined) {
      for {
        scalaStmts <- futScalaStmts
        db <- getAdhocDb
      } yield TxReport_Peer(db.`with`(javaStmts), scalaStmts)

    } else if (_testDb.isDefined && connProxy.testDbStatus != -1) {
      futScalaStmts.map { scalaStmts =>
        // In-memory "transaction"
        val txReport = TxReport_Peer(_testDb.get.`with`(javaStmts), scalaStmts)
        // Continue with updated in-memory db
        val dbAfter  = txReport.dbAfter.asOf(txReport.t)
        _testDb = Some(dbAfter)
        txReport
      }

    } else if (connProxy.testDbStatus == 1 && _testDb.isEmpty) {
      def transactWith: Future[TxReport_Peer] = futScalaStmts.map { scalaStmts =>
        // In-memory "transaction"
        val txReport = TxReport_Peer(_testDb.getOrElse(peerConn.db).`with`(javaStmts), scalaStmts)

        // Continue with updated in-memory db
        _testDb = Some(txReport.dbAfter.asOf(txReport.t))
        txReport
      }

      val res = connProxy.testDbView.get match {
        case AsOf(TxLong(0))          => transactWith
        case AsOf(TxLong(t))          => cleanFrom(t + 1).flatMap(_ => transactWith)
        case AsOf(TxDate(d))          => cleanFrom(nextDateMs(d)).flatMap(_ => transactWith)
        case Since(TxLong(t))         => _testDb = Some(peerConn.db.since(t)); transactWith
        case Since(TxDate(d))         => _testDb = Some(peerConn.db.since(d)); transactWith
        case History                  => _testDb = Some(peerConn.db.history()); transactWith
        case With(stmtsEdn, uriAttrs) =>
          val txData   = DatomicRpc().getJavaStmts(stmtsEdn, uriAttrs)
          val txReport = TxReport_Peer(peerConn.db.`with`(txData))
          _testDb = Some(txReport.dbAfter.asOf(txReport.t))
          transactWith

        case other => throw MoleculeException("Unexpected transactRaw DbView: " + other)
      }
      res

    } else {
      if (connProxy.testDbStatus == -1) {
        updateTestDbView(None, 0)
        _testDb = None
      }
      // Live transaction
      for {
        moleculeInvocationResult <- bridgeDatomicFuture(peerConn.transactAsync(javaStmts), Some(javaStmts))
        scalaStmts <- futScalaStmts
      } yield {
        TxReport_Peer(moleculeInvocationResult, scalaStmts)
      }
    }
  } catch {
    case NonFatal(ex) => Future.failed(ex)
  }


  // Query ---------------------------------------------------------------------

  private[molecule] final override def rawQuery(
    query: String,
    inputs: Seq[AnyRef]
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    db.flatMap { db =>
      try {
        Future(Peer.q(query, db.getDatomicDb +: inputs: _*))
      } catch {
        case e: ju.concurrent.ExecutionException =>
          // White list of exceptions that can be pickled by BooPickle
          Future.failed(
            e.getCause match {
              case e: TxFnException     => e
              case e: MoleculeException => e
              case e                    => MoleculeException(e.getMessage.trim)
            }
          )

        case NonFatal(e) => Future.failed(MoleculeException(e.getMessage))
      }
    }
  }

  private[molecule] final override def jvmQuery(
    model: Model,
    query: Query,
    history: Boolean = false
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    model.elements.head match {
      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _, _) => indexQuery(model)
      case Generic("Schema", _, _, _, _) if history                       => schemaHistoryQuery(model)
      case _                                                              => datalogQuery(model, query)
    }
  }


  private[molecule] final override def datalogQuery(
    model: Model,
    query: Query,
    _db: Option[DatomicDb] = None
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    db.flatMap { db =>
      try {
        val p               = Query2String(query).p
        val rules           = if (query.i.rules.isEmpty) Nil else Seq("[" + (query.i.rules map p mkString " ") + "]")
        val inputsEvaluated = QueryOpsClojure(query).inputsWithKeyword
        val peerDb          = _db.getOrElse(db).getDatomicDb
        val allInputs       = Seq(peerDb) ++ rules ++ inputsEvaluated
        val result          = Peer.q(query.toMap, allInputs: _*)
        Future(result)
      } catch {
        case NonFatal(exc) => Future.failed(QueryException(exc, model, query))
      }
    }
  }


  // Internal convenience method conn.entity(id) for conn.db.entity(conn, id)
  private[molecule] final def entity(
    id: Any
  )(implicit ec: ExecutionContext): Future[DatomicEntity] = db.map(_.entity(this, id))


  // Reset datoms of in-mem with-db from next timePoint after as-of t until end.
  // Otherwise, transactions on the _testDb will include datoms after the time point.
  protected final override def cleanFrom(nextTimePoint: Any)
                                        (implicit ec: ExecutionContext): Future[Unit] = {
    def op(datom: Datom): String = if (datom.added) ":db/retract" else ":db/add"
    for {
      db <- db
      txInstants <- db.pull("[:db/id]", ":db/txInstant")
      txInstId = txInstants.get(Util.read(":db/id"))
    } yield {
      try {
        _testDb = Some(peerConn.db)
        val txs     = peerConn.log.txRange(nextTimePoint, null).iterator()
        var txStmts = new ju.ArrayList[jList[_]]()
        while (txs.hasNext) {
          val txDatoms = txs.next().get(datomic.Log.DATA).asInstanceOf[jList[Datom]]
          txStmts = new ju.ArrayList[jList[_]](txDatoms.size())
          txDatoms.forEach { datom =>
            // Don't reverse timestamps
            if (datom.a != txInstId) {
              txStmts.add(javaList(op(datom), datom.e, datom.a, datom.v))
            }
          }
          // Update in-memory with-db with datoms of this tx
          val txReport = TxReport_Peer(_testDb.get.`with`(txStmts))
          _testDb = Some(txReport.dbAfter.asOf(txReport.t))
        }
      } catch {
        case NonFatal(ex) => Future.failed(ex)
      }
    }
  }

  private def bridgeDatomicFuture[T](
    listenF: ListenableFuture[T],
    javaStmts: Option[jList[_]] = None
  )(implicit ec: ExecutionContext): Future[T] = {
    val p = Promise[T]()
    listenF.addListener(
      new jl.Runnable {
        override def run: Unit = {
          try {
            p.success(listenF.get())
          } catch {
            case e: ju.concurrent.ExecutionException =>
              println(
                "---- ExecutionException: -------------\n" +
                  listenF +
                  javaStmts.fold("")(stmts => "\n---- javaStmts: ----\n" + stmts.asScala.toList.mkString("\n"))
              )
              // White list of exceptions that can be pickled by BooPickle
              p.failure(
                e.getCause match {
                  case e: TxFnException     => e
                  case e: MoleculeException => e
                  case e                    => MoleculeException(e.getMessage.trim)
                }
              )

            case NonFatal(e) =>
              println(
                "---- NonFatal exception: -------------\n" +
                  listenF +
                  javaStmts.fold("")(stmts => "\n---- javaStmts: ----\n" + stmts.asScala.toList.mkString("\n"))
              )
              p.failure(MoleculeException(e.getMessage))
          }
        }
      },
      (arg0: Runnable) => ec.execute(arg0)
    )
    p.future
  }
}


/** Connection factory methods used by tx functions.
 *
 * */
object Conn_Peer {

  def apply(
    uri: String,
    defaultConnProxy: ConnProxy
  ): Conn_Peer = Conn_Peer(datomic.Peer.connect(uri), defaultConnProxy, uri)

  // Dummy constructor for transaction functions where db is supplied inside transaction by transactor
  def apply(txDb: AnyRef): Conn_Peer = new Conn_Peer(
    null, DatomicPeerProxy("dummy", "", Nil, Map.empty[String, MetaNs], Map.empty[String, (Int, String)])
  ) {
    testDb(DatomicDb_Peer(txDb.asInstanceOf[Database]))
  }
}