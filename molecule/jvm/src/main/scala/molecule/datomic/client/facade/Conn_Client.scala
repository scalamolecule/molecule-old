package molecule.datomic.client.facade

import java.util
import java.util.{Date, Collection => jCollection, List => jList}
import datomic.{Database, Peer, Util}
import datomicScala.client.api.Datom
import datomicScala.client.api.sync.{Client, Db, Connection => ClientConnection, Datomic => clientDatomic}
import molecule.core.ast.elements._
import molecule.core.exceptions._
import molecule.core.marshalling.ConnProxy
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.{Conn, Conn_Jvm, DatomicDb, QuerySchemaHistory, TxReport}
import molecule.datomic.base.marshalling.DatomicRpc
import molecule.datomic.base.transform.Query2String
import molecule.datomic.base.util.QueryOpsClojure
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


/** Facade to Datomic connection for client api (peer-server/cloud/dev-local).
 * */
case class Conn_Client(
  client: Client,
  override val defaultConnProxy: ConnProxy,
  dbName: String,
  system: String = "",
) extends Conn_Jvm with QueryIndex with QuerySchemaHistory {

  // Molecule api --------------------------------------------------------------

  def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(clientConn.db)
  }


  def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    sinceT = Some(t)
    sinceD = None
    _testDb = Some(clientConn.db.`with`(clientConn.withDb, javaList()).dbAfter)
    withDbInUse = true
  }

  def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    sinceT = Some(txR.t)
    sinceD = None
    _testDb = Some(clientConn.db.`with`(clientConn.withDb, javaList()).dbAfter)
    withDbInUse = true
  }

  def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    sinceT = None
    sinceD = Some(d)
    _testDb = Some(clientConn.db.`with`(clientConn.withDb, javaList()).dbAfter)
    withDbInUse = true
  }


  def testDbWith(txMolecules: Future[Seq[Statement]]*)(implicit ec: ExecutionContext): Future[Unit] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      testDbWith(stmts2java(stmtss.flatten))
    }
  }

  def testDbWith(txDataJava: jList[jList[_]])(implicit ec: ExecutionContext): Future[Unit] = Future {
    tempId.reset()
    _testDb = Some(clientConn.db.`with`(clientConn.withDb, txDataJava).dbAfter)
    withDbInUse = true
  }

  def useLiveDb(): Unit = {
    sinceT = None
    sinceD = None
    withDbInUse = false
    _testDb = None
  }


  // Datomic client facade -----------------------------------------------------

  def db(implicit ec: ExecutionContext): Future[DatomicDb] = {
    if (_adhocDbView.isDefined) {
      // Adhoc db
      getAdhocDb.map(DatomicDb_Client)

    } else if (connProxy.testDbStatus == 1 && _testDb.isEmpty) {
      // Test db
      try {
        val tempDb = connProxy.testDbView.get match {
          case AsOf(TxLong(0))          => Some(clientConn.db) // db as of now
          case AsOf(TxLong(t))          => Some(clientConn.db.asOf(t))
          case AsOf(TxDate(d))          => Some(clientConn.db.asOf(d))
          case Since(TxLong(t))         => Some(clientConn.db.since(t))
          case Since(TxDate(d))         => Some(clientConn.db.since(d))
          case History                  => Some(clientConn.db.history)
          case With(stmtsEdn, uriAttrs) =>
            val txData   = DatomicRpc().getJavaStmts(stmtsEdn, uriAttrs)
            val txReport = TxReport_Client(clientConn.db.`with`(clientConn.withDb, txData))
            Some(txReport.dbAfter)

          case other => throw MoleculeException("Unexpected db DbView: " + other)
        }
        Future(DatomicDb_Client(tempDb.get))
      } catch {
        case NonFatal(exc) => Future.failed(exc)
      }

    } else if (connProxy.testDbStatus == -1) {
      // Return to live db
      Future {
        _testDb = None
        updateTestDbView(None, 0)
        DatomicDb_Client(clientConn.db)
      }

    } else if (_testDb.isDefined) {
      // Test db
      Future {
        if (sinceT.isDefined) {
          DatomicDb_Client(_testDb.get.since(sinceT.get))
        } else if (sinceD.isDefined) {
          DatomicDb_Client(_testDb.get.since(sinceD.get))
        } else {
          DatomicDb_Client(_testDb.get)
        }
      }

    } else {
      // Live db
      Future(DatomicDb_Client(clientConn.db))
    }
  }

  final def sync: Conn =
    usingAdhocDbView(Sync(0))

  final def sync(t: Long): Conn =
    usingAdhocDbView(Sync(t))


  // Schema --------------------------------------------------------------------

  protected def historyQuery(query: String)
                            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    db.map { db =>
      clientDatomic.q(
        query,
        db.asInstanceOf[DatomicDb_Client].clientDb.history,
        db.asInstanceOf[DatomicDb_Client].getDatomicDb
      )
    }
  }


  // Internal ------------------------------------------------------------------

  // In-memory fixed test db for integration testing of domain model
  // (takes precedence over live db)
  protected var _testDb: Option[Db] = None

  // Flag to indicate if special withDb is in use for testDb
  protected var withDbInUse = false


  private[molecule] lazy val clientConn: ClientConnection = client.connect(dbName)

  // Temporary `since` time points - needs to be applied later to queries in order
  // to maintain special withdb
  private var sinceT = Option.empty[Long]
  private var sinceD = Option.empty[Date]


  private def getAdhocDb(implicit ec: ExecutionContext): Future[Db] = {
    lazy val baseDb: Db = _testDb.getOrElse(clientConn.db)
    (_adhocDbView.get match {
      case AsOf(TxLong(t))          => Future(baseDb.asOf(t))
      case AsOf(TxDate(d))          => Future(baseDb.asOf(d))
      case Since(TxLong(t))         => Future(baseDb.since(t))
      case Since(TxDate(d))         => Future(baseDb.since(d))
      case History                  => Future(baseDb.history)
      case With(stmtsEdn, uriAttrs) => Future {
        val txData = DatomicRpc().getJavaStmts(stmtsEdn, uriAttrs)
        baseDb.`with`(clientConn.withDb, txData).dbAfter
      }
      case Sync(0)                  => Future(clientConn.sync(clientConn.db.t))
      case Sync(t: Long)            => Future(clientConn.sync(t))
      case SyncIndex(_)             => Future.failed(peerOnly("SyncIndex"))
      case SyncSchema(_)            => Future.failed(peerOnly("SyncSchema"))
      case SyncExcise(_)            => Future.failed(peerOnly("SyncExcise"))
      case other                    =>
        Future.failed(MoleculeException("Unexpected getAdhocDbDbView: " + other))
    }).map { db =>
      _adhocDbView = None
      connProxy = defaultConnProxy
      db
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

      } yield TxReport_Client(db.`with`(clientConn.withDb, javaStmts), scalaStmts)

    } else if (_testDb.isDefined && connProxy.testDbStatus != -1) {
      futScalaStmts.map { scalaStmts =>
        val withDb = if (withDbInUse) {
          _testDb.get.`with`(_testDb.get, javaStmts)
        } else {
          _testDb.get.`with`(clientConn.withDb, javaStmts)
        }
        withDbInUse = true
        val txReport = TxReport_Client(withDb, scalaStmts)
        _testDb = Some(txReport.dbAfter) // don't add .asOf(txReport.t) - destroys with-db!
        txReport
      }


    } else if (connProxy.testDbStatus == 1 && _testDb.isEmpty) {
      def transactWith: Future[TxReport_Client] = futScalaStmts.map { scalaStmts =>
        val testDb = _testDb.getOrElse(clientConn.db)
        val withDb = if (withDbInUse && _testDb.nonEmpty) {
          testDb.`with`(testDb, javaStmts)
        } else {
          testDb.`with`(clientConn.withDb, javaStmts)
        }
        withDbInUse = true
        val txReport = TxReport_Client(withDb, scalaStmts)
        _testDb = Some(txReport.dbAfter) // don't add .asOf(txReport.t) - destroys with-db!
        txReport
      }

      val res = connProxy.testDbView.get match {
        case AsOf(TxLong(0))          => transactWith
        case AsOf(TxLong(t))          => cleanFrom(t + 1).flatMap(_ => transactWith)
        case AsOf(TxDate(d))          => cleanFrom(nextDateMs(d)).flatMap(_ => transactWith)
        case Since(TxLong(t))         =>
          sinceT = Some(t)
          _testDb = Some(clientConn.db.since(t))
          transactWith
        case Since(TxDate(d))         =>
          sinceD = Some(d)
          _testDb = Some(clientConn.db.since(d))
          transactWith
        case History                  => _testDb = Some(clientConn.db.history); transactWith
        case With(stmtsEdn, uriAttrs) =>
          val txData   = DatomicRpc().getJavaStmts(stmtsEdn, uriAttrs)
          val txReport = TxReport_Client(clientConn.db.`with`(clientConn.withDb, txData))
          withDbInUse = true
          _testDb = Some(txReport.dbAfter)
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
      futScalaStmts.flatMap { scalaStmts =>
        val futRawTxReport: Future[datomicScala.client.api.sync.TxReport] = try {
          // Catch exceptions before wrapping in inner Future
          val rawTxReport = clientConn.transact(javaStmts)
          Future(rawTxReport)
        } catch {
          case e: java.util.concurrent.ExecutionException =>
            println("---- Conn_Client.transactRaw ExecutionException: -------------")
            println(javaStmts.asScala.toList.mkString("\n"))

            println("---- Conn_Client.transactRaw ExecutionException: -------------\n" + e +
              "\n---- javaStmts: ----\n" + javaStmts.asScala.toList.mkString("\n")
            )

            // White list of exceptions that can be pickled by BooPickle
            Future.failed(
              e.getCause match {
                case e: TxFnException     => e
                case e: MoleculeException => e
                case e                    => MoleculeException(e.getMessage.trim)
              }
            )

          case NonFatal(e) =>
            println("---- Conn_Client.transactRaw NonFatal exc: -------------")
            println(javaStmts.asScala.toList.mkString("\n"))
            Future.failed(MoleculeException(e.getMessage))
        }

        futRawTxReport.map(rawTxReport => TxReport_Client(rawTxReport, scalaStmts))
      }
    }
  } catch {
    case NonFatal(ex) => Future.failed(ex)
  }


  private[molecule] final override def rawQuery(
    query: String,
    inputs: Seq[AnyRef]
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    db.flatMap { db =>
      try {
        val result = clientDatomic.q(
          query,
          db.asInstanceOf[DatomicDb_Client].clientDb,
          inputs: _*
        )
        Future(result)
      } catch {
        case NonFatal(e) => Future.failed(MoleculeException(e.getMessage))
      }
    }
  }


  private[molecule] final override def jvmQuery(
    model: Model,
    query: Query
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    model.elements.head match {
      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _, _) => indexQuery(model)
      case _                                                              => datalogQuery(model, query)
    }
  }


  private[molecule] override def datalogQuery(
    model: Model,
    query: Query,
    _db: Option[DatomicDb]
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    db.flatMap { db =>
      try {
        val p               = Query2String(query).p
        val rules           = if (query.i.rules.isEmpty) Nil else Seq("[" + (query.i.rules map p mkString " ") + "]")
        val inputsEvaluated = QueryOpsClojure(query).inputsWithKeyword
        val allInputs       = rules ++ inputsEvaluated
        val clientDb        = _db.getOrElse(db).asInstanceOf[DatomicDb_Client].clientDb
        val result          = clientDatomic.q(query.toMap, clientDb, allInputs: _*)
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


  // Reset datoms of in-mem with-db from next timePoint after as-of t until end
  protected final override def cleanFrom(nextTimePoint: Any)
                                        (implicit ec: ExecutionContext): Future[Unit] = {
    for {
      db <- db
      txInstants <- db.pull("[:db/id]", ":db/txInstant")
      txInstId = txInstants.get(Util.read(":db/id"))
    } yield {
      try {
        _testDb = Some(clientConn.db.`with`(clientConn.withDb, javaList()).dbAfter)
        val txs            = clientConn.txRangeArray(Some(nextTimePoint))
        val (retract, add) = (Util.read(":db/retract"), Util.read(":db/add"))
        def op(datom: Datom) = if (datom.added) retract else add
        var txStmts = new util.ArrayList[jList[_]]()
        val size    = txs.length
        var i       = size - 1

        // Reverse datoms backwards from last to timePoint right after as-of t
        while (i >= 0) {
          val txDatoms = txs(i)._2
          txStmts = new util.ArrayList[jList[_]](txDatoms.length)
          txDatoms.foreach { datom =>
            // Don't reverse timestamps
            if (datom.a != txInstId) {
              txStmts.add(
                javaList(
                  op(datom),
                  datom.e.asInstanceOf[Object],
                  datom.a.asInstanceOf[Object],
                  datom.v.asInstanceOf[Object]
                )
              )
            }
          }
          // Update in-memory with-db with datoms of this tx
          _testDb = Some(_testDb.get.`with`(_testDb.get, txStmts).dbAfter)
          i -= 1
        }
        withDbInUse = true
      } catch {
        case NonFatal(ex) => Future.failed(ex)
      }
    }
  }

}