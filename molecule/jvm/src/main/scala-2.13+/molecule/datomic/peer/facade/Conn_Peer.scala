package molecule.datomic.peer.facade

import java.util.{Date, Collection => jCollection, List => jList}
import java.{lang, util}
import datomic.Connection.DB_AFTER
import datomic.Peer._
import datomic.Util._
import datomic.{Database, Datom, ListenableFuture, Peer}
import molecule.core.ast.elements._
import molecule.core.exceptions._
import molecule.core.util.QueryOpsClojure
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.tempDb._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.{Conn, Conn_Datomic213, DatomicDb, TxReport}
import molecule.datomic.base.transform.Query2String
import scala.concurrent.{ExecutionContext, Future, Promise, blocking}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal
//import scala.concurrent.ExecutionContext.Implicits.global


/** Factory methods to create facade to Datomic Connection. */
object Conn_Peer {

  def apply(uri: String): Conn_Peer = new Conn_Peer(datomic.Peer.connect(uri), uri)

  //  def apply(datomicConn: datomic.Connection): Conn_Peer = new Conn_Peer(datomicConn)

  // Constructor for transaction functions where db is supplied inside transaction by transactor
  def apply(txDb: AnyRef): Conn_Peer = new Conn_Peer(null) {
    testDb(DatomicDb_Peer(txDb.asInstanceOf[Database]))
  }
}


/** Facade to Datomic connection for peer api.
  * */
class Conn_Peer(
  val peerConn: datomic.Connection,
  val system: String = ""
) extends Conn_Datomic213 {

  // In-memory fixed test db for integration testing of domain model
  // (takes precedence over live db)
  private var _testDb: Option[Database] = None


  def usingTempDb(tempDb: TempDb): Conn = {
    _adhocDb = Some(tempDb)
    this
  }

  def liveDbUsed: Boolean = _adhocDb.isEmpty && _testDb.isEmpty

  def testDb(db: DatomicDb): Unit = {
    _testDb = Some(db.asInstanceOf[DatomicDb_Peer].peerDb)
  }

  // Reset datoms of in-mem with-db from next timePoint after as-of t until end
  override def cleanFrom(nextTimePoint: Any)
                        (implicit ec: ExecutionContext): Future[Unit] = {
    def op(datom: Datom): String = if (datom.added) ":db/retract" else ":db/add"
    for {
      txInstants <- db.pull("[:db/id]", ":db/txInstant")
      txInstId = txInstants.get(read(":db/id"))
    } yield {
      blocking {
        _testDb = Some(peerConn.db)
        val txs     = peerConn.log.txRange(nextTimePoint, null).iterator()
        var txStmts = new util.ArrayList[jList[_]]()
        while (txs.hasNext) {
          val txDatoms = txs.next().get(datomic.Log.DATA).asInstanceOf[jList[Datom]]
          txStmts = new util.ArrayList[jList[_]](txDatoms.size())
          txDatoms.forEach { datom =>
            // Don't reverse timestamps
            if (datom.a != txInstId) {
              txStmts.add(list(op(datom), datom.e, datom.a, datom.v))
            }
          }
          // Update in-memory with-db with datoms of this tx
          val txReport = TxReport_Peer(_testDb.get.`with`(txStmts))
          _testDb = Some(txReport.dbAfter.asOf(txReport.t))
        }
      }
    }
    //    Future {
    //      _testDb = Some(peerConn.db)
    //      val array                  = peerConn.log.txRange(nextTimePoint, null).iterator().asScala.toArray
    //      val txInstId               = db.pull("[:db/id]", ":db/txInstant").get(read(":db/id"))
    //      //      def op(datom: Datom) = if (datom.added) ":db/retract" else ":db/add"
    //      var stmts: jList[jList[_]] = new util.ArrayList[jList[_]]()
    //      val size                   = array.length
    //      var i                      = size - 1
    //      // Reverse datoms backwards from last to timePoint right after as-of t
    //      while (i >= 0) {
    //        val datoms = array(i).get(datomic.Log.DATA).asInstanceOf[jList[Datom]]
    //        stmts = new util.ArrayList[jList[_]](datoms.size())
    //        datoms.forEach { datom =>
    //          // Don't reverse timestamps
    //          if (datom.a != txInstId) {
    //            stmts.add(list(op(datom), datom.e, datom.a, datom.v))
    //          }
    //        }
    //        // Update in-memory with-db
    //        val txReport = TxReport_Peer(_testDb.get.`with`(stmts))
    //        _testDb = Some(txReport.dbAfter.asOf(txReport.t))
    //        i -= 1
    //      }
    //    }
  }

  def testDbAsOfNow: Unit = {
    _testDb = Some(peerConn.db)
  }

  def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(peerConn.db.since(t))
  }

  def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(peerConn.db.since(d))
  }

  def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(peerConn.db.since(txR.t))
  }

  def testDbWith(txMolecules: Future[Seq[Statement]]*)(implicit ec: ExecutionContext): Future[Unit] = {
    Future.sequence(txMolecules).map { stmtss =>
      testDbWith(stmts2java(stmtss.flatten))
    }
  }

  /** Use test database with temporary raw Java transaction data. */
  def testDbWith(txDataJava: jList[jList[_]])(implicit ec: ExecutionContext): Future[Unit] = Future {
    tempId.reset()
    _testDb = Some(peerConn.db.`with`(txDataJava).get(DB_AFTER).asInstanceOf[Database])
  }

  def useLiveDb: Unit = {
    _testDb = None
  }

  private def getAdhocDb: Database = {
    val baseDb : Database = _testDb.getOrElse(peerConn.db)
    val adhocDb: Database = _adhocDb.get match {
      case AsOf(TxLong(t))  => baseDb.asOf(t)
      case AsOf(TxDate(d))  => baseDb.asOf(d)
      case Since(TxLong(t)) => baseDb.since(t)
      case Since(TxDate(d)) => baseDb.since(d)
      case With(tx)         => {
        val txReport = TxReport_Peer(baseDb.`with`(tx))
        val db       = txReport.dbAfter.asOf(txReport.t)
        db
      }
      case History          => baseDb.history()
    }
    _adhocDb = None
    adhocDb
  }

  def db: DatomicDb = {
    if (_adhocDb.isDefined) {
      // Return singleton adhoc db
      DatomicDb_Peer(getAdhocDb)
    } else if (_testDb.isDefined) {
      // Test db
      DatomicDb_Peer(_testDb.get)
    } else {
      // Live db
      DatomicDb_Peer(peerConn.db)
    }
  }

  //  def entity(id: Any)(implicit ec: ExecutionContext): Future[DatomicEntity] =
  def entity(id: Any): DatomicEntity =
    db.entity(this, id)



  def transactRaw(javaStmts: jList[_], futScalaStmts: Future[Seq[Statement]] = Future.successful(Seq.empty[Statement]))
                 (implicit ec: ExecutionContext): Future[TxReport] = try {
    if (_adhocDb.isDefined) {
      futScalaStmts.map(scalaStmts =>
        TxReport_Peer(getAdhocDb.`with`(javaStmts), scalaStmts)
      )

    } else if (_testDb.isDefined) {
      futScalaStmts.map { scalaStmts =>
        // In-memory "transaction"
        val txReport = TxReport_Peer(_testDb.get.`with`(javaStmts), scalaStmts)

        // Continue with updated in-memory db
        // todo: why can't we just say this? Or: why are there 2 db-after db objects?
        //      val dbAfter = txReport.dbAfter
        val dbAfter = txReport.dbAfter.asOf(txReport.t)
        _testDb = Some(dbAfter)
        txReport
      }

    } else {
      // Live transaction
      val listenableFuture: ListenableFuture[util.Map[_, _]] = peerConn.transactAsync(javaStmts)
      val p                                                  = Promise[util.Map[_, _]]()
      listenableFuture.addListener(
        new java.lang.Runnable {
          override def run: Unit = {
            try {
              p.success(listenableFuture.get())
            } catch {
              case e: Throwable =>
                e match {
                  case e: java.util.concurrent.ExecutionException =>
                    println("---- Conn_Peer.transactAsyncRaw ExecutionException: -------------\n" + listenableFuture)
                    //                    println("---- javaStmts:\n" + javaStmts.asScala.toList.mkString("\n"))
                    p.failure(e.getCause)

                  case NonFatal(e) =>
                    println("---- Conn_Peer.transactAsyncRaw NonFatal exc: -------------\n" + listenableFuture)
                    //                    println("---- javaStmts:\n" + javaStmts)
                    p.failure(e)
                }
            }
            ()
          }
        },
        (arg0: Runnable) => ec.execute(arg0)
      )
      for {
        moleculeInvocationResult <- p.future
        scalaStmts <- futScalaStmts
      } yield {
        TxReport_Peer(moleculeInvocationResult, scalaStmts)
      }
    }
  } catch {
    case NonFatal(ex) => Future.failed(ex)
  }

  def qRaw(
    db: DatomicDb,
    query: String,
    inputs0: Seq[Any]
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = Future {
    val inputs = inputs0.map {
      case it: Iterable[_] => it.asJava
      case v               => v
    }
    blocking(
      Peer.q(query, db.getDatomicDb +: inputs.asInstanceOf[Seq[AnyRef]]: _*)
    )
  }

  def query(model: Model, query: Query)
           (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    model.elements.head match {
      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _) => _index(model)
      case _                                                           => _query(model, query)
    }
  }

  // Datalog query execution
  private[molecule] def _query(
    model: Model,
    query: Query,
    _db: Option[DatomicDb] = None
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    try {
      Future {
        val p               = Query2String(query).p
        val rules           = "[" + (query.i.rules map p mkString " ") + "]"
        val adhocDb         = _db.getOrElse(db).getDatomicDb
        val first           = if (query.i.rules.isEmpty) Seq(adhocDb) else Seq(adhocDb, rules)
        val inputsEvaluated = QueryOpsClojure(query).inputsWithKeyword
        val allInputs       = first ++ inputsEvaluated
        Peer.q(query.toMap, allInputs: _*)
      }
    } catch {
      case NonFatal(exc) =>
        Future.failed(new QueryException(exc, model, query))
    }
  }

  // Datoms API providing direct access to indexes
  private[molecule] def _index(model: Model)
                              (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    val (api, index, args) = model.elements.head match {
      case Generic("EAVT", _, _, value) =>
        ("datoms", datomic.Database.EAVT, value match {
          case NoValue                   => Seq()
          case Eq(Seq(e))                => Seq(e)
          case Eq(Seq(e, a))             => Seq(e, a)
          case Eq(Seq(e, a, v))          => Seq(e, a, v)
          case Eq(Seq(e, a, v, d: Date)) => Seq(e, a, v, d)
          case Eq(Seq(e, a, v, t))       => Seq(e, a, v, t)
          case v                         => throw MoleculeException("Unexpected EAVT value: " + v)
        })

      case Generic("AEVT", _, _, value) =>
        ("datoms", datomic.Database.AEVT, value match {
          case NoValue                   => Seq()
          case EntValue                  => Seq()
          case Eq(Seq(a))                => Seq(a)
          case Eq(Seq(a, e))             => Seq(a, e)
          case Eq(Seq(a, e, v))          => Seq(a, e, v)
          case Eq(Seq(a, e, v, d: Date)) => Seq(a, e, v, d)
          case Eq(Seq(a, e, v, t))       => Seq(a, e, v, t)
          case v                         => throw MoleculeException("Unexpected AEVT value: " + v)
        })

      case Generic("AVET", attr, _, value) =>
        attr match {
          case "range" =>
            ("indexRange", "", value match {
              case Eq(Seq(a, None, None))  => Seq(a, null, null)
              case Eq(Seq(a, from, None))  => Seq(a, from, null)
              case Eq(Seq(a, None, until)) => Seq(a, null, until)
              case Eq(Seq(a, from, until)) =>
                if (from.getClass != until.getClass)
                  throw MoleculeException("Please supply range arguments of same type as attribute.")
                Seq(a, from, until)
              case v                       => throw MoleculeException("Unexpected AVET range value: " + v)
            })
          case _       =>
            ("datoms", datomic.Database.AVET, value match {
              case NoValue                   => Seq()
              case Eq(Seq(a))                => Seq(a)
              case Eq(Seq(a, v))             => Seq(a, v)
              case Eq(Seq(a, v, e))          => Seq(a, v, e)
              case Eq(Seq(a, v, e, d: Date)) => Seq(a, v, e, d)
              case Eq(Seq(a, v, e, t))       => Seq(a, v, e, t)
              case v                         => throw MoleculeException("Unexpected AVET datoms value: " + v)
            })
        }

      case Generic("VAET", _, _, value) =>
        ("datoms", datomic.Database.VAET, value match {
          case NoValue                   => Seq()
          case Eq(Seq(v))                => Seq(v)
          case Eq(Seq(v, a))             => Seq(v, a)
          case Eq(Seq(v, a, e))          => Seq(v, a, e)
          case Eq(Seq(v, a, e, d: Date)) => Seq(v, a, e, d)
          case Eq(Seq(v, a, e, t))       => Seq(v, a, e, t)
          case v                         => throw MoleculeException("Unexpected VAET value: " + v)
        })

      case Generic("Log", _, _, value) =>
        ("txRange", "", value match {
          case Eq(Seq(from: Int, until: Int))   => Seq(from, until)
          case Eq(Seq(from: Int, until: Long))  => Seq(from, until)
          case Eq(Seq(from: Int, until: Date))  => Seq(from, until)
          case Eq(Seq(from: Long, until: Int))  => Seq(from, until)
          case Eq(Seq(from: Long, until: Long)) => Seq(from, until)
          case Eq(Seq(from: Long, until: Date)) => Seq(from, until)
          case Eq(Seq(from: Date, until: Int))  => Seq(from, until)
          case Eq(Seq(from: Date, until: Long)) => Seq(from, until)
          case Eq(Seq(from: Date, until: Date)) => Seq(from, until)

          case Eq(Seq(from: Int, None))  => Seq(from, null)
          case Eq(Seq(from: Long, None)) => Seq(from, null)
          case Eq(Seq(from: Date, None)) => Seq(from, null)

          case Eq(Seq(None, until: Int))  => Seq(null, until)
          case Eq(Seq(None, until: Long)) => Seq(null, until)
          case Eq(Seq(None, until: Date)) => Seq(null, until)

          // All !!
          case Eq(Seq(None, None)) => Seq(null, null)

          // From until end
          case Eq(Seq(from: Int))  => Seq(from, null)
          case Eq(Seq(from: Long)) => Seq(from, null)
          case Eq(Seq(from: Date)) => Seq(from, null)

          // All !!
          case Eq(Nil) => Seq(null, null)

          case Eq(other) => throw MoleculeException(
            "Args to Log can only be t, tx or txInstant of type Int/Long/Date. Found: " + other)

          case v => throw MoleculeException("Unexpected Log value: " + v)
        })

      case other => throw MoleculeException(
        s"Only Index queries accepted (EAVT, AEVT, AVET, VAET, Log). Found `$other`"
      )
    }

    // This one is important for Peer to keep db stable when
    // mixing filters with getHistory!
    // todo:
    val adhocDb = db

    def datomElement(
      tOpt: Option[Long],
      attr: String
    )(implicit ec: ExecutionContext): Datom => Future[Any] = attr match {
      case "e"                   => (d: Datom) => Future(d.e)
      case "a"                   => (d: Datom) => Future(adhocDb.getDatomicDb.asInstanceOf[Database].ident(d.a).toString)
      case "v"                   => (d: Datom) => Future(d.v)
      case "t" if tOpt.isDefined => (_: Datom) => Future(tOpt.get)
      case "t"                   => (d: Datom) => Future(toT(d.tx))
      case "tx"                  => (d: Datom) => Future(d.tx)
      case "txInstant"           => (d: Datom) =>
        adhocDb.entity(this, d.tx).rawValue(":db/txInstant").map(_.asInstanceOf[Date])

      case "op" => (d: Datom) => Future(d.added)
      case a    => throw MoleculeException("Unexpected generic attribute: " + a)
    }

    val attrs: Seq[String] = model.elements.collect {
      case Generic(_, attr, _, _)
        if attr != "args_" && attr != "range" => attr
    }

    def datom2row(tOpt: Option[Long])(implicit ec: ExecutionContext): Datom => Future[jList[AnyRef]] = attrs.length match {
      case 1 =>
        val x1 = datomElement(tOpt, attrs.head)
        (d: Datom) =>
          for {
            v1 <- x1(d)
          } yield list(v1).asInstanceOf[jList[AnyRef]]

      case 2 =>
        val x1 = datomElement(tOpt, attrs.head)
        val x2 = datomElement(tOpt, attrs(1))
        (d: Datom) =>
          for {
            v1 <- x1(d)
            v2 <- x2(d)
          } yield list(v1, v2).asInstanceOf[jList[AnyRef]]

      case 3 =>
        val x1 = datomElement(tOpt, attrs.head)
        val x2 = datomElement(tOpt, attrs(1))
        val x3 = datomElement(tOpt, attrs(2))
        (d: Datom) =>
          for {
            v1 <- x1(d)
            v2 <- x2(d)
            v3 <- x3(d)
          } yield list(v1, v2, v3).asInstanceOf[jList[AnyRef]]


      case 4 =>
        val x1 = datomElement(tOpt, attrs.head)
        val x2 = datomElement(tOpt, attrs(1))
        val x3 = datomElement(tOpt, attrs(2))
        val x4 = datomElement(tOpt, attrs(3))
        (d: Datom) =>
          for {
            v1 <- x1(d)
            v2 <- x2(d)
            v3 <- x3(d)
            v4 <- x4(d)
          } yield list(v1, v2, v3, v4).asInstanceOf[jList[AnyRef]]

      case 5 =>
        val x1 = datomElement(tOpt, attrs.head)
        val x2 = datomElement(tOpt, attrs(1))
        val x3 = datomElement(tOpt, attrs(2))
        val x4 = datomElement(tOpt, attrs(3))
        val x5 = datomElement(tOpt, attrs(4))
        (d: Datom) =>
          for {
            v1 <- x1(d)
            v2 <- x2(d)
            v3 <- x3(d)
            v4 <- x4(d)
            v5 <- x5(d)
          } yield list(v1, v2, v3, v4, v5).asInstanceOf[jList[AnyRef]]

      case 6 =>
        val x1 = datomElement(tOpt, attrs.head)
        val x2 = datomElement(tOpt, attrs(1))
        val x3 = datomElement(tOpt, attrs(2))
        val x4 = datomElement(tOpt, attrs(3))
        val x5 = datomElement(tOpt, attrs(4))
        val x6 = datomElement(tOpt, attrs(5))
        (d: Datom) =>
          for {
            v1 <- x1(d)
            v2 <- x2(d)
            v3 <- x3(d)
            v4 <- x4(d)
            v5 <- x5(d)
            v6 <- x6(d)
          } yield list(v1, v2, v3, v4, v5, v6).asInstanceOf[jList[AnyRef]]

      case 7 =>
        val x1 = datomElement(tOpt, attrs.head)
        val x2 = datomElement(tOpt, attrs(1))
        val x3 = datomElement(tOpt, attrs(2))
        val x4 = datomElement(tOpt, attrs(3))
        val x5 = datomElement(tOpt, attrs(4))
        val x6 = datomElement(tOpt, attrs(5))
        val x7 = datomElement(tOpt, attrs(6))
        (d: Datom) =>
          for {
            v1 <- x1(d)
            v2 <- x2(d)
            v3 <- x3(d)
            v4 <- x4(d)
            v5 <- x5(d)
            v6 <- x6(d)
            v7 <- x7(d)
          } yield list(v1, v2, v3, v4, v5, v6, v7).asInstanceOf[jList[AnyRef]]
    }

    // Convert Datoms to standard list of rows so that we can use the same Molecule query API
    val jColl: jCollection[jList[AnyRef]] = new util.ArrayList[jList[AnyRef]]()
    api match {
      case "datoms" =>
        val datom2row_ = datom2row(None)
        val raw        = adhocDb.asInstanceOf[DatomicDb_Peer].datoms(index, args: _*)
        raw.map { datoms =>
          datoms.forEach(datom => datom2row_(datom).map(row => jColl.add(row)))
          jColl
        }

      case "indexRange" =>
        val datom2row_ = datom2row(None)
        val attrId     = args.head.toString
        val startValue = args(1)
        val endValue   = args(2)
        val raw        = adhocDb.asInstanceOf[DatomicDb_Peer].indexRange(attrId, startValue, endValue)
        raw.map { datoms =>
          datoms.forEach(datom => datom2row_(datom).map(row => jColl.add(row)))
          jColl
        }

      case "txRange" =>
        val raw = Future(peerConn.log.txRange(args.head, args(1)))
        raw.map { datoms =>
          datoms.forEach { txMap =>
            // Flatten transaction datoms to unified tuples return type
            txMap.get(datomic.Log.DATA).asInstanceOf[jList[Datom]].forEach { datom =>
              val datom2row_ = datom2row(Some(txMap.get(datomic.Log.T).asInstanceOf[Long]))
              //            jColl.add(datom2row_(datom))
              datom2row_(datom).map(row => jColl.add(row))
            }
          }
          jColl
        }
    }
  }

  def sync: ListenableFuture[Database] = peerConn.sync()

  def sync(t: Long): ListenableFuture[Database] = peerConn.sync(t)

  def syncIndex(t: Long): ListenableFuture[Database] = peerConn.syncIndex(t)
}
