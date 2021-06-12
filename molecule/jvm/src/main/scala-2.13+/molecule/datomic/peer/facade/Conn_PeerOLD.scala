//package molecule.datomic.peer.facade
//
//import java.util
//import java.util.concurrent.atomic.AtomicInteger
//import java.util.{Date, Collection => jCollection, List => jList}
//import datomic.Connection.DB_AFTER
//import datomic.Peer._
//import datomic.Util._
//import datomic.{Database, Datom, ListenableFuture, Peer}
//import molecule.core.ast.elements._
//import molecule.core.exceptions._
//import molecule.core.util.QueryOpsClojure
//import molecule.datomic.base.api.DatomicEntity
//import molecule.datomic.base.ast.dbView._
//import molecule.datomic.base.ast.query.Query
//import molecule.datomic.base.ast.transactionModel._
//import molecule.datomic.base.facade.{Conn_Datomic213, DatomicDb, TxReport}
//import molecule.datomic.base.marshalling.DatomicRpc.getJavaStmts
//import molecule.datomic.base.transform.Query2String
//import scala.collection.mutable.ListBuffer
//import scala.concurrent.{ExecutionContext, Future, Promise, blocking}
//import scala.jdk.CollectionConverters._
//import scala.util.control.NonFatal
//
//
///** Facade to Datomic connection for peer api.
//  * */
//case class Conn_PeerOLD(
//  peerConn: datomic.Connection,
//  system: String = ""
//) extends Conn_Datomic213 {
//
//
//  // In-memory fixed test db for integration testing of domain model
//  // (takes precedence over live db)
//  private[molecule] var _testDb: Option[Database] = None
//
//
//  def liveDbUsed: Boolean = _adhocDbView.isEmpty && _testDb.isEmpty
//
//  def testDb(db: DatomicDb): Unit = {
//    _testDb = Some(db.asInstanceOf[DatomicDb_Peer].peerDb)
//  }
//
//  // Reset datoms of in-mem with-db from next timePoint after as-of t until end
//  override def cleanFrom(nextTimePoint: Any)
//                        (implicit ec: ExecutionContext): Future[Unit] = try {
//    def op(datom: Datom): String = if (datom.added) ":db/retract" else ":db/add"
//    for {
//      txInstants <- db.pull("[:db/id]", ":db/txInstant")
//      txInstId = txInstants.get(read(":db/id"))
//    } yield {
//      blocking {
//        _testDb = Some(peerConn.db)
//        val txs     = peerConn.log.txRange(nextTimePoint, null).iterator()
//        var txStmts = new util.ArrayList[jList[_]]()
//        while (txs.hasNext) {
//          val txDatoms = txs.next().get(datomic.Log.DATA).asInstanceOf[jList[Datom]]
//          txStmts = new util.ArrayList[jList[_]](txDatoms.size())
//          txDatoms.forEach { datom =>
//            // Don't reverse timestamps
//            if (datom.a != txInstId) {
////              println(s"${datom.e}   ${datom.a}   ${datom.v}   ${datom.added()}")
//              txStmts.add(list(op(datom), datom.e, datom.a, datom.v))
//            }
//          }
//          // Update in-memory with-db with datoms of this tx
//          val txReport = TxReport_Peer(_testDb.get.`with`(txStmts))
//          _testDb = Some(txReport.dbAfter.asOf(txReport.t))
//          debug("c" + testAsOfCount)
//        }
//      }
//    }
//  } catch {
//    case NonFatal(ex) => Future.failed(ex)
//  }
//
//  def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit] = Future {
//    _testDb = Some(peerConn.db)
//  }
//
//  def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
//    _testDb = Some(peerConn.db.since(t))
//  }
//
//  def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
//    _testDb = Some(peerConn.db.since(d))
//  }
//
//  def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
//    _testDb = Some(peerConn.db.since(txR.t))
//  }
//
//  def testDbWith(txMolecules: Future[Seq[Statement]]*)(implicit ec: ExecutionContext): Future[Unit] = {
//    Future.sequence(txMolecules).flatMap { stmtss =>
//      testDbWith(stmts2java(stmtss.flatten))
//    }
//  }
//
//  /** Use test database with temporary raw Java transaction data. */
//  def testDbWith(txDataJava: jList[jList[_]])(implicit ec: ExecutionContext): Future[Unit] = Future {
//    tempId.reset()
//    _testDb = Some(peerConn.db.`with`(txDataJava).get(DB_AFTER).asInstanceOf[Database])
//  }
//
//  def useLiveDb: Unit = {
//    _testDb = None
//  }
//
//  private def getAdhocDb: Database = {
//    val baseDb : Database = _testDb.getOrElse(peerConn.db)
//    val adhocDb: Database = _adhocDbView.get match {
//      case AsOf(TxLong(t))          => baseDb.asOf(t)
//      case AsOf(TxDate(d))          => baseDb.asOf(d)
//      case Since(TxLong(t))         => baseDb.since(t)
//      case Since(TxDate(d))         => baseDb.since(d)
//      case History                  => baseDb.history()
//      case With(stmtsEdn, uriAttrs) =>
//        val txData   = getJavaStmts(stmtsEdn, uriAttrs)
//        val txReport = TxReport_Peer(baseDb.`with`(txData))
//        txReport.dbAfter.asOf(txReport.t)
//    }
//    _adhocDbView = None
//    dbProxy = emptyDbProxy
//    adhocDb
//  }
//
//
//  private def debug(prefix: String, suffix: String = "") = {
//    val p      = prefix + " " * (4 - prefix.length)
//    //    val basisT = if(_testDb.isDefined) _testDb.get.basisT.toString else ""
//    val basisT = if (_testDb.isDefined && _testDb.get.asOfT != null) _testDb.get.asOfT.toString else ""
//    println(s"$p  ${dbProxy.adhocDbView}  ${dbProxy.testDbView}  //  ${_adhocDbView}  ${_testDb}  $basisT  " + suffix)
//  }
//
//  println("############################")
//  val testAsOfCount = new AtomicInteger(0)
//
//  def db: DatomicDb = {
//    if (_adhocDbView.isDefined) {
//      debug("d1")
//      DatomicDb_Peer(getAdhocDb)
//
//    } else if (_testDb.isDefined) {
//      debug("d2", peerConn.db.toString)
//
//      if (dbProxy.testDbView.contains(AsOf(TxLong(-1)))) {
//        testAsOfCount.set(0)
//        _testDb = None
//        debug("d2x")
//        DatomicDb_Peer(peerConn.db)
//
//      } else if (dbProxy.testDbView.isDefined) {
//        dbProxy.testDbView.get match {
//          case AsOf(TxLong(-1))         =>
//            testAsOfCount.set(0)
//            _testDb = None // use live db
//          case AsOf(TxLong(0))          =>
//          // Use _testDb as is
////            _testDb = Some(peerConn.db) // db as of now
//
//          case AsOf(TxLong(t))          =>
////            _testDb = Some(peerConn.db.asOf(t))
//          case AsOf(TxDate(d))          => _testDb = Some(peerConn.db.asOf(d))
//          case Since(TxLong(t))         => _testDb = Some(peerConn.db.since(t))
//          case Since(TxDate(d))         => _testDb = Some(peerConn.db.since(d))
//          case History                  => _testDb = Some(peerConn.db.history())
//          case With(stmtsEdn, uriAttrs) =>
//            val txData   = getJavaStmts(stmtsEdn, uriAttrs)
//            val txReport = TxReport_Peer(peerConn.db.`with`(txData))
//            _testDb = Some(txReport.dbAfter.asOf(txReport.t))
//        }
//
//        debug("d2_")
//        DatomicDb_Peer(_testDb.get)
//      } else {
//        DatomicDb_Peer(_testDb.get)
//      }
//
//    } else if (dbProxy.testDbView.isDefined) {
//      debug("d3")
//      dbProxy.testDbView.get match {
//        case AsOf(TxLong(-1))         =>
//          testAsOfCount.set(0)
//          _testDb = None // use live db
//        case AsOf(TxLong(0))          => _testDb = Some(peerConn.db) // db as of now
//        case AsOf(TxLong(t))          =>
//
////          testAsOfCount.incrementAndGet()
//
//          //          if (testAsOfCount == 1)
//          //            cleanFrom(t + 1)
//
//          _testDb = Some(peerConn.db.asOf(t))
//
//        case AsOf(TxDate(d))          => _testDb = Some(peerConn.db.asOf(d))
//        case Since(TxLong(t))         => _testDb = Some(peerConn.db.since(t))
//        case Since(TxDate(d))         => _testDb = Some(peerConn.db.since(d))
//        case History                  => _testDb = Some(peerConn.db.history())
//        case With(stmtsEdn, uriAttrs) =>
//          val txData   = getJavaStmts(stmtsEdn, uriAttrs)
//          val txReport = TxReport_Peer(peerConn.db.`with`(txData))
//          _testDb = Some(txReport.dbAfter.asOf(txReport.t))
//      }
//      debug("d3_")
//      // Test db
//      DatomicDb_Peer(_testDb.getOrElse(peerConn.db))
//
//    } else {
//      debug("d4")
//
//      // Live db
//      DatomicDb_Peer(peerConn.db)
//    }
//  }
//
//
//  def entity(id: Any): DatomicEntity = db.entity(this, id)
//
//  def transactRaw(
//    javaStmts: jList[_],
//    futScalaStmts: Future[Seq[Statement]] = Future.successful(Seq.empty[Statement])
//  )(implicit ec: ExecutionContext): Future[TxReport] = try {
//    if (_adhocDbView.isDefined) {
//      debug("T1")
//      futScalaStmts.map(scalaStmts =>
//        TxReport_Peer(getAdhocDb.`with`(javaStmts), scalaStmts)
//      )
//
//    } else if (_testDb.isDefined) {
//      debug("T2")
//
//      def transactWith: Future[TxReport_Peer] = futScalaStmts.map { scalaStmts =>
//        // In-memory "transaction"
//        val txReport = TxReport_Peer(_testDb.get.`with`(javaStmts), scalaStmts)
//
//        // Continue with updated in-memory db
//        // todo: why can't we just say this? Or: why are there 2 db-after db objects?
//        //      val dbAfter = txReport.dbAfter
//        val dbAfter = txReport.dbAfter.asOf(txReport.t)
//        _testDb = Some(dbAfter)
//        debug("T2_")
//        txReport
//      }
//
//      if (dbProxy.testDbView.isDefined) {
//        dbProxy.testDbView.get match {
//          case AsOf(TxLong(0)) =>
//            //            _testDb = Some(peerConn.db)
//            transactWith
//          case AsOf(TxLong(t)) =>
//            println(testAsOfCount.get())
//            if (testAsOfCount.incrementAndGet() == 1) {
//              println("A")
//              cleanFrom(t + 1).flatMap(_ => transactWith)
//            } else {
//              println("B")
//              transactWith
//            }
//          // We could have used asOfT here also, but that is not practical when using testDbAsOf(<date>)
//          // if(_testDb.get.asOfT == t) cleanFrom(t + 1).flatMap(_ => transactWith) else transactWith
//
//          case AsOf(TxDate(d)) =>
//            if (testAsOfCount.incrementAndGet() == 1) {
//              cleanFrom(new Date(d.toInstant.plusMillis(1).toEpochMilli)).flatMap(_ => transactWith)
//            } else {
//              transactWith
//            }
//
//          case other => transactWith // todo ??
//        }
//      } else {
//        transactWith
//      }
//
//    } else if (dbProxy.testDbView.isDefined) {
//      debug("T3")
//
//      // Set _testDb
//      if (_testDb.isEmpty) {
//        dbProxy.testDbView.get match {
//          case AsOf(TxLong(-1))         =>
//            testAsOfCount.set(0)
//            _testDb = None // use live db
//          case AsOf(TxLong(0))          => _testDb = Some(peerConn.db) // db as of now
//          case AsOf(TxLong(t))          => _testDb = Some(peerConn.db.asOf(t))
//          case AsOf(TxDate(d))          => _testDb = Some(peerConn.db.asOf(d))
//          case Since(TxLong(t))         => _testDb = Some(peerConn.db.since(t))
//          case Since(TxDate(d))         => _testDb = Some(peerConn.db.since(d))
//          case History                  => _testDb = Some(peerConn.db.history())
//          case With(stmtsEdn, uriAttrs) =>
//            val txData   = getJavaStmts(stmtsEdn, uriAttrs)
//            val txReport = TxReport_Peer(peerConn.db.`with`(txData))
//            _testDb = Some(txReport.dbAfter.asOf(txReport.t))
//        }
//        debug("T3_")
//      }
//
//      // Transact temporary data
//      futScalaStmts.map { scalaStmts =>
//        // In-memory "transaction"
//        val txReport = TxReport_Peer(_testDb.get.`with`(javaStmts), scalaStmts)
//
//        // Continue with updated in-memory db
//        // todo: why can't we just say this? Or: why are there 2 db-after db objects?
//        //      val dbAfter = txReport.dbAfter
//        val dbAfter = txReport.dbAfter.asOf(txReport.t)
//        _testDb = Some(dbAfter)
//        txReport
//      }
//
//
//    } else {
//      debug("T4")
//      // Live transaction
//      val listenableFuture: ListenableFuture[util.Map[_, _]] = peerConn.transactAsync(javaStmts)
//      val p                                                  = Promise[util.Map[_, _]]()
//      listenableFuture.addListener(
//        new java.lang.Runnable {
//          override def run: Unit = {
//            try {
//              p.success(listenableFuture.get())
//            } catch {
//              case e: java.util.concurrent.ExecutionException =>
//                println("---- Conn_Peer.transactRaw ExecutionException: -------------\n" + listenableFuture)
//                println("---- javaStmts:\n" + javaStmts.asScala.toList.mkString("\n"))
//                p.failure(e.getCause)
//
//              case NonFatal(e) =>
//                println("---- Conn_Peer.transactRaw NonFatal exc: -------------\n" + listenableFuture)
//                println("---- javaStmts:\n" + javaStmts.asScala.toList.mkString("\n"))
//                p.failure(e)
//            }
//            ()
//          }
//        },
//        (arg0: Runnable) => ec.execute(arg0)
//      )
//      for {
//        moleculeInvocationResult <- p.future
//        scalaStmts <- futScalaStmts
//      } yield {
//        TxReport_Peer(moleculeInvocationResult, scalaStmts)
//      }
//    }
//  } catch {
//    case NonFatal(ex) => Future.failed(ex)
//  }
//
//  def qRaw(
//    db: DatomicDb,
//    query: String,
//    inputs0: Seq[Any]
//  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = Future {
//    val inputs = inputs0.map {
//      case it: Iterable[_] => it.asJava
//      case v               => v
//    }
//    Peer.q(query, db.getDatomicDb +: inputs.asInstanceOf[Seq[AnyRef]]: _*)
//  }
//
//  def query(model: Model, query: Query)
//           (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
//    model.elements.head match {
//      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _) => _index(model)
//      case _                                                           => _query(model, query)
//    }
//  }
//
//  // Datalog query execution
//  private[molecule] def _query(
//    model: Model,
//    query: Query,
//    _db: Option[DatomicDb] = None
//  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
//    Future {
//      // Allow exceptions to be wrapped in QueryException
//      try {
//        val p               = Query2String(query).p
//        val rules           = "[" + (query.i.rules map p mkString " ") + "]"
//        val adhocDb         = _db.getOrElse(db).getDatomicDb
//        val first           = if (query.i.rules.isEmpty) Seq(adhocDb) else Seq(adhocDb, rules)
//        val inputsEvaluated = QueryOpsClojure(query).inputsWithKeyword
//        val allInputs       = first ++ inputsEvaluated
//        val result          = Peer.q(query.toMap, allInputs: _*)
//        Future(result)
//      } catch {
//        case NonFatal(exc) => Future.failed(QueryException(exc, model, query))
//      }
//    }.flatten
//  }
//
//  // Datoms API providing direct access to indexes
//  private[molecule] def _index(model: Model)
//                              (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
//    val (api, index, args) = model.elements.head match {
//      case Generic("EAVT", _, _, value) =>
//        ("datoms", datomic.Database.EAVT, value match {
//          case NoValue                   => Seq()
//          case Eq(Seq(e))                => Seq(e)
//          case Eq(Seq(e, a))             => Seq(e, a)
//          case Eq(Seq(e, a, v))          => Seq(e, a, v)
//          case Eq(Seq(e, a, v, d: Date)) => Seq(e, a, v, d)
//          case Eq(Seq(e, a, v, t))       => Seq(e, a, v, t)
//          case v                         => throw MoleculeException("Unexpected EAVT value: " + v)
//        })
//
//      case Generic("AEVT", _, _, value) =>
//        ("datoms", datomic.Database.AEVT, value match {
//          case NoValue                   => Seq()
//          case EntValue                  => Seq()
//          case Eq(Seq(a))                => Seq(a)
//          case Eq(Seq(a, e))             => Seq(a, e)
//          case Eq(Seq(a, e, v))          => Seq(a, e, v)
//          case Eq(Seq(a, e, v, d: Date)) => Seq(a, e, v, d)
//          case Eq(Seq(a, e, v, t))       => Seq(a, e, v, t)
//          case v                         => throw MoleculeException("Unexpected AEVT value: " + v)
//        })
//
//      case Generic("AVET", attr, _, value) =>
//        attr match {
//          case "range" =>
//            ("indexRange", "", value match {
//              case Eq(Seq(a, None, None))  => Seq(a, null, null)
//              case Eq(Seq(a, from, None))  => Seq(a, from, null)
//              case Eq(Seq(a, None, until)) => Seq(a, null, until)
//              case Eq(Seq(a, from, until)) =>
//                if (from.getClass != until.getClass)
//                  throw MoleculeException("Please supply range arguments of same type as attribute.")
//                Seq(a, from, until)
//              case v                       => throw MoleculeException("Unexpected AVET range value: " + v)
//            })
//          case _       =>
//            ("datoms", datomic.Database.AVET, value match {
//              case NoValue                   => Seq()
//              case Eq(Seq(a))                => Seq(a)
//              case Eq(Seq(a, v))             => Seq(a, v)
//              case Eq(Seq(a, v, e))          => Seq(a, v, e)
//              case Eq(Seq(a, v, e, d: Date)) => Seq(a, v, e, d)
//              case Eq(Seq(a, v, e, t))       => Seq(a, v, e, t)
//              case v                         => throw MoleculeException("Unexpected AVET datoms value: " + v)
//            })
//        }
//
//      case Generic("VAET", _, _, value) =>
//        ("datoms", datomic.Database.VAET, value match {
//          case NoValue                   => Seq()
//          case Eq(Seq(v))                => Seq(v)
//          case Eq(Seq(v, a))             => Seq(v, a)
//          case Eq(Seq(v, a, e))          => Seq(v, a, e)
//          case Eq(Seq(v, a, e, d: Date)) => Seq(v, a, e, d)
//          case Eq(Seq(v, a, e, t))       => Seq(v, a, e, t)
//          case v                         => throw MoleculeException("Unexpected VAET value: " + v)
//        })
//
//      case Generic("Log", _, _, value) =>
//        ("txRange", "", value match {
//          case Eq(Seq(from: Int, until: Int))   => Seq(from, until)
//          case Eq(Seq(from: Int, until: Long))  => Seq(from, until)
//          case Eq(Seq(from: Int, until: Date))  => Seq(from, until)
//          case Eq(Seq(from: Long, until: Int))  => Seq(from, until)
//          case Eq(Seq(from: Long, until: Long)) => Seq(from, until)
//          case Eq(Seq(from: Long, until: Date)) => Seq(from, until)
//          case Eq(Seq(from: Date, until: Int))  => Seq(from, until)
//          case Eq(Seq(from: Date, until: Long)) => Seq(from, until)
//          case Eq(Seq(from: Date, until: Date)) => Seq(from, until)
//
//          case Eq(Seq(from: Int, None))  => Seq(from, null)
//          case Eq(Seq(from: Long, None)) => Seq(from, null)
//          case Eq(Seq(from: Date, None)) => Seq(from, null)
//
//          case Eq(Seq(None, until: Int))  => Seq(null, until)
//          case Eq(Seq(None, until: Long)) => Seq(null, until)
//          case Eq(Seq(None, until: Date)) => Seq(null, until)
//
//          // All !!
//          case Eq(Seq(None, None)) => Seq(null, null)
//
//          // From until end
//          case Eq(Seq(from: Int))  => Seq(from, null)
//          case Eq(Seq(from: Long)) => Seq(from, null)
//          case Eq(Seq(from: Date)) => Seq(from, null)
//
//          // All !!
//          case Eq(Nil) => Seq(null, null)
//
//          case Eq(other) => throw MoleculeException(
//            "Args to Log can only be t, tx or txInstant of type Int/Long/Date. Found: " + other)
//
//          case v => throw MoleculeException("Unexpected Log value: " + v)
//        })
//
//      case other => throw MoleculeException(
//        s"Only Index queries accepted (EAVT, AEVT, AVET, VAET, Log). Found `$other`"
//      )
//    }
//
//    // This one is important for Peer to keep db stable when
//    // mixing filters with getHistory!
//    // todo:
//    val adhocDb = db
//
//    def datomElement(
//      tOpt: Option[Long],
//      attr: String
//    )(implicit ec: ExecutionContext): Datom => Future[Any] = attr match {
//      case "e"                   => (d: Datom) => Future(d.e)
//      case "a"                   => (d: Datom) => Future(adhocDb.getDatomicDb.asInstanceOf[Database].ident(d.a).toString)
//      case "v"                   => (d: Datom) => Future(d.v)
//      case "t" if tOpt.isDefined => (_: Datom) => Future(tOpt.get)
//      case "t"                   => (d: Datom) => Future(toT(d.tx))
//      case "tx"                  => (d: Datom) => Future(d.tx)
//      case "txInstant"           => (d: Datom) =>
//        adhocDb.entity(this, d.tx).rawValue(":db/txInstant").map(_.asInstanceOf[Date])
//
//      case "op" => (d: Datom) => Future(d.added)
//      case a    => throw MoleculeException("Unexpected generic attribute: " + a)
//    }
//
//    val attrs: Seq[String] = model.elements.collect {
//      case Generic(_, attr, _, _)
//        if attr != "args_" && attr != "range" => attr
//    }
//
//    def datom2row(tOpt: Option[Long])(implicit ec: ExecutionContext): Datom => Future[jList[AnyRef]] = attrs.length match {
//      case 1 =>
//        val x1 = datomElement(tOpt, attrs.head)
//        (d: Datom) =>
//          for {
//            v1 <- x1(d)
//          } yield list(v1).asInstanceOf[jList[AnyRef]]
//
//      case 2 =>
//        val x1 = datomElement(tOpt, attrs.head)
//        val x2 = datomElement(tOpt, attrs(1))
//        (d: Datom) =>
//          for {
//            v1 <- x1(d)
//            v2 <- x2(d)
//          } yield list(v1, v2).asInstanceOf[jList[AnyRef]]
//
//      case 3 =>
//        val x1 = datomElement(tOpt, attrs.head)
//        val x2 = datomElement(tOpt, attrs(1))
//        val x3 = datomElement(tOpt, attrs(2))
//        (d: Datom) =>
//          for {
//            v1 <- x1(d)
//            v2 <- x2(d)
//            v3 <- x3(d)
//          } yield list(v1, v2, v3).asInstanceOf[jList[AnyRef]]
//
//
//      case 4 =>
//        val x1 = datomElement(tOpt, attrs.head)
//        val x2 = datomElement(tOpt, attrs(1))
//        val x3 = datomElement(tOpt, attrs(2))
//        val x4 = datomElement(tOpt, attrs(3))
//        (d: Datom) =>
//          for {
//            v1 <- x1(d)
//            v2 <- x2(d)
//            v3 <- x3(d)
//            v4 <- x4(d)
//          } yield list(v1, v2, v3, v4).asInstanceOf[jList[AnyRef]]
//
//      case 5 =>
//        val x1 = datomElement(tOpt, attrs.head)
//        val x2 = datomElement(tOpt, attrs(1))
//        val x3 = datomElement(tOpt, attrs(2))
//        val x4 = datomElement(tOpt, attrs(3))
//        val x5 = datomElement(tOpt, attrs(4))
//        (d: Datom) =>
//          for {
//            v1 <- x1(d)
//            v2 <- x2(d)
//            v3 <- x3(d)
//            v4 <- x4(d)
//            v5 <- x5(d)
//          } yield list(v1, v2, v3, v4, v5).asInstanceOf[jList[AnyRef]]
//
//      case 6 =>
//        val x1 = datomElement(tOpt, attrs.head)
//        val x2 = datomElement(tOpt, attrs(1))
//        val x3 = datomElement(tOpt, attrs(2))
//        val x4 = datomElement(tOpt, attrs(3))
//        val x5 = datomElement(tOpt, attrs(4))
//        val x6 = datomElement(tOpt, attrs(5))
//        (d: Datom) =>
//          for {
//            v1 <- x1(d)
//            v2 <- x2(d)
//            v3 <- x3(d)
//            v4 <- x4(d)
//            v5 <- x5(d)
//            v6 <- x6(d)
//          } yield list(v1, v2, v3, v4, v5, v6).asInstanceOf[jList[AnyRef]]
//
//      case 7 =>
//        val x1 = datomElement(tOpt, attrs.head)
//        val x2 = datomElement(tOpt, attrs(1))
//        val x3 = datomElement(tOpt, attrs(2))
//        val x4 = datomElement(tOpt, attrs(3))
//        val x5 = datomElement(tOpt, attrs(4))
//        val x6 = datomElement(tOpt, attrs(5))
//        val x7 = datomElement(tOpt, attrs(6))
//        (d: Datom) =>
//          for {
//            v1 <- x1(d)
//            v2 <- x2(d)
//            v3 <- x3(d)
//            v4 <- x4(d)
//            v5 <- x5(d)
//            v6 <- x6(d)
//            v7 <- x7(d)
//          } yield list(v1, v2, v3, v4, v5, v6, v7).asInstanceOf[jList[AnyRef]]
//    }
//
//    // Convert Datoms to standard list of rows so that we can use the same Molecule query API
//    val rows = new ListBuffer[Future[jList[AnyRef]]]()
//    api match {
//      case "datoms" =>
//        val datom2row_ = datom2row(None)
//        adhocDb.asInstanceOf[DatomicDb_Peer].datoms(index, args: _*).flatMap { datoms =>
//          datoms.forEach(datom =>
//            rows.addOne(datom2row_(datom))
//          )
//          Future.sequence(rows).map(_.asJavaCollection)
//        }
//
//      case "indexRange" =>
//        val datom2row_ = datom2row(None)
//        val attrId     = args.head.toString
//        val startValue = args(1)
//        val endValue   = args(2)
//        adhocDb.asInstanceOf[DatomicDb_Peer].indexRange(attrId, startValue, endValue).flatMap { datoms =>
//          datoms.forEach(datom =>
//            rows.addOne(datom2row_(datom))
//          )
//          Future.sequence(rows).map(_.asJavaCollection)
//        }
//
//      case "txRange" =>
//        // Loop transactions
//        peerConn.log.txRange(args.head, args(1)).forEach { txMap =>
//          // Flatten transaction datoms to uniform tuples return type
//          val datom2row_ = datom2row(Some(txMap.get(datomic.Log.T).asInstanceOf[Long]))
//          txMap.get(datomic.Log.DATA).asInstanceOf[jList[Datom]].forEach(datom =>
//            rows.addOne(datom2row_(datom))
//          )
//        }
//        Future.sequence(rows).map(_.asJavaCollection)
//    }
//  }
//
//  def sync: ListenableFuture[Database] = peerConn.sync()
//
//  def sync(t: Long): ListenableFuture[Database] = peerConn.sync(t)
//
//  def syncIndex(t: Long): ListenableFuture[Database] = peerConn.syncIndex(t)
//}
