package molecule.datomic.client.facade

import java.util
import java.util.{Date, Collection => jCollection, List => jList}
import datomic.Peer
import datomic.Util._
import datomicScala.client.api.Datom
import datomicScala.client.api.sync.{Client, Db, Connection => ClientConnection, Datomic => clientDatomic}
import molecule.core.ast.elements._
import molecule.core.exceptions._
import molecule.core.marshalling.ConnProxy
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.{Conn, Conn_Jvm, DatomicDb, TxReport, TxReportQueue}
import molecule.datomic.base.marshalling.DatomicRpc.getJavaStmts
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
) extends Conn_Jvm {

  // Molecule api --------------------------------------------------------------

  def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(clientConn.db)
  }


  def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    sinceT = Some(t)
    sinceD = None
    _testDb = Some(clientConn.db.`with`(clientConn.withDb, list()).dbAfter)
    withDbInUse = true
  }

  def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    sinceT = Some(txR.t)
    sinceD = None
    _testDb = Some(clientConn.db.`with`(clientConn.withDb, list()).dbAfter)
    withDbInUse = true
  }

  def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    sinceT = None
    sinceD = Some(d)
    _testDb = Some(clientConn.db.`with`(clientConn.withDb, list()).dbAfter)
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


  // Datomic facade ------------------------------------------------------------

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
            val txData   = getJavaStmts(stmtsEdn, uriAttrs)
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

  final def sync(implicit ec: ExecutionContext): Conn =
    usingAdhocDbView(Sync(0))

  final def sync(t: Long)(implicit ec: ExecutionContext): Conn =
    usingAdhocDbView(Sync(t))


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
        val txData = getJavaStmts(stmtsEdn, uriAttrs)
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
          val txData   = getJavaStmts(stmtsEdn, uriAttrs)
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
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = Future {
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
  }.flatten


  private[molecule] final override def jvmQuery(
    model: Model,
    query: Query
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    model.elements.head match {
      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _) =>
        indexQuery(model)

      case _ =>
        datalogQuery(model, query)
    }
  }


  // Datoms API providing direct access to indexes
  private[molecule] final override def indexQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = Future {
    try {
      val (api, index, args) = model.elements.head match {
        case Generic("EAVT", _, _, value) =>
          ("datoms", ":eavt", value match {
            case NoValue                   => Seq()
            case Eq(Seq(e))                => Seq(e)
            case Eq(Seq(e, a))             => Seq(e, read(a.toString))
            case Eq(Seq(e, a, v))          => Seq(e, read(a.toString), v)
            case Eq(Seq(e, a, v, d: Date)) => Seq(e, read(a.toString), v, d)
            case Eq(Seq(e, a, v, t))       => Seq(e, read(a.toString), v, t)
            case v                         => throw MoleculeException("Unexpected EAVT value: " + v)
          })

        case Generic("AEVT", _, _, value) =>
          ("datoms", ":aevt", value match {
            case NoValue                   => Seq()
            case EntValue                  => Seq()
            case Eq(Seq(a))                => Seq(read(a.toString))
            case Eq(Seq(a, e))             => Seq(read(a.toString), e)
            case Eq(Seq(a, e, v))          => Seq(read(a.toString), e, v)
            case Eq(Seq(a, e, v, d: Date)) => Seq(read(a.toString), e, v, d)
            case Eq(Seq(a, e, v, t))       => Seq(read(a.toString), e, v, t)
            case v                         => throw MoleculeException("Unexpected AEVT value: " + v)
          })

        case Generic("AVET", attr, _, value) =>
          attr match {
            case "range" =>
              ("indexRange", "", value match {
                case Eq(Seq(a, None, None))  => Seq(read(a.toString), None, None)
                case Eq(Seq(a, from, None))  => Seq(read(a.toString), Some(from), None)
                case Eq(Seq(a, None, until)) => Seq(read(a.toString), None, Some(until))
                case Eq(Seq(a, from, until)) =>
                  if (from.getClass != until.getClass)
                    throw MoleculeException("Please supply range arguments of same type as attribute.")
                  Seq(read(a.toString), Some(from), Some(until))
                case v                       => throw MoleculeException("Unexpected AVET range value: " + v)
              })
            case _       =>
              ("datoms", ":avet", value match {
                case NoValue                   => Seq()
                case Eq(Seq(a))                => Seq(read(a.toString))
                case Eq(Seq(a, v))             => Seq(read(a.toString), v)
                case Eq(Seq(a, v, e))          => Seq(read(a.toString), v, e)
                case Eq(Seq(a, v, e, d: Date)) => Seq(read(a.toString), v, e, d)
                case Eq(Seq(a, v, e, t))       => Seq(read(a.toString), v, e, t)
                case v                         => throw MoleculeException("Unexpected AVET datoms value: " + v)
              })
          }

        case Generic("VAET", _, _, value) =>
          ("datoms", ":vaet", value match {
            case NoValue                   => Seq()
            case Eq(Seq(v))                => Seq(v)
            case Eq(Seq(v, a))             => Seq(v, read(a.toString))
            case Eq(Seq(v, a, e))          => Seq(v, read(a.toString), e)
            case Eq(Seq(v, a, e, d: Date)) => Seq(v, read(a.toString), e, d)
            case Eq(Seq(v, a, e, t))       => Seq(v, read(a.toString), e, t)
            case v                         => throw MoleculeException("Unexpected VAET value: " + v)
          })

        case Generic("Log", _, _, value) =>
          def err(v: Any) = throw MoleculeException(
            s"Args to Log can only be t, tx or txInstant of type Int/Long/Date. Found `$v` of type " + v.getClass)

          ("txRange", "", value match {
            case Eq(Seq(a, b)) =>
              // Get valid from/until values
              val from  = a match {
                case None                              => None
                case from@(_: Int | _: Long | _: Date) => Some(from)
                case other                             => err(other)
              }
              val until = b match {
                case None                               => None
                case until@(_: Int | _: Long | _: Date) => Some(until)
                case other                              => err(other)
              }
              Seq(from, until)


            case Eq(Seq(from)) => from match {
              case None                              => Seq(None, None)
              case from@(_: Int | _: Long | _: Date) => Seq(Some(from), None)
              case other                             => err(other)
            }

            // All !!
            case Eq(Nil) => Seq(None, None)

            case Eq(other) => err(other)

            case v => throw MoleculeException("Unexpected Log value: " + v)
          })

        case other => throw MoleculeException(
          s"Only Index queries accepted (EAVT, AEVT, AVET, VAET, Log). Found `$other`")
      }


      lazy val attrNames: Future[Array[String]] = {
        // Since the number of definitions is limited we can quickly collect them
        // for fast lookups in an array by index = attr id.
        // 5000 slots should satisfy any schema.
        val array = new Array[String](5000)
        rawQuery(
          """[:find  ?id ?idIdent
            | :where [_ :db.install/attribute ?id]
            |        [?id :db/ident ?idIdent]
            |        ]""".stripMargin).map { rows =>
          rows.forEach { row =>
            array(row.get(0).asInstanceOf[Long].toInt) = row.get(1).toString
          }
          array
        }
      }

      lazy val defaultDate = new Date(0)
      lazy val txInstant   = read(":db/txInstant")

      def date(tx: Long): Future[Date] = {
        // We can't index all txInstants
        // Some initial transactions lack tx time it seems, so there we default
        // to time 0 (Thu Jan 01 01:00:00 CET 1970)
        db.flatMap(db => db.pull("[:db/txInstant]", tx).map {
          case null => defaultDate
          case res  => res.get(txInstant).asInstanceOf[Date]
        })
      }

      def datomElement(
        tOpt: Option[Long],
        attr: String
      )(implicit ec: ExecutionContext): Datom => Future[Any] = attr match {
        case "e"                   => (d: Datom) => Future(d.e)
        case "a"                   => (d: Datom) => attrNames.map(_ (d.a.toString.toInt))
        case "v"                   => (d: Datom) => Future(d.v)
        case "t" if tOpt.isDefined => (_: Datom) => Future(tOpt.get) // use provided t
        case "t"                   => (d: Datom) => Future(Peer.toT(d.tx))
        case "tx"                  => (d: Datom) => Future(d.tx)
        case "txInstant"           => (d: Datom) => date(d.tx)
        case "op"                  => (d: Datom) => Future(d.added)
        case a                     => throw MoleculeException("Unexpected generic attribute: " + a)
      }

      val attrs: Seq[String] = model.elements.collect {
        case Generic(_, attr, _, _)
          if attr != "args_" && attr != "range" => attr
      }

      def datom2row(tOpt: Option[Long]): Datom => Future[jList[AnyRef]] = attrs.length match {
        case 1 =>
          val x1 = datomElement(tOpt, attrs.head)
          (d: Datom) =>
            for {
              v1 <- x1(d)
            } yield list(
              v1.asInstanceOf[Object],
            ).asInstanceOf[jList[AnyRef]]

        case 2 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
            } yield list(
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
            ).asInstanceOf[jList[AnyRef]]

        case 3 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          val x3 = datomElement(tOpt, attrs(2))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
              v3 <- x3(d)
            } yield list(
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
            ).asInstanceOf[jList[AnyRef]]

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
            } yield list(
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
              v4.asInstanceOf[Object],
            ).asInstanceOf[jList[AnyRef]]

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
            } yield list(
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
              v4.asInstanceOf[Object],
              v5.asInstanceOf[Object],
            ).asInstanceOf[jList[AnyRef]]

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
            } yield list(
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
              v4.asInstanceOf[Object],
              v5.asInstanceOf[Object],
              v6.asInstanceOf[Object],
            ).asInstanceOf[jList[AnyRef]]

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
            } yield list(
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
              v4.asInstanceOf[Object],
              v5.asInstanceOf[Object],
              v6.asInstanceOf[Object],
              v7.asInstanceOf[Object]
            ).asInstanceOf[jList[AnyRef]]
      }

      // Convert Datoms to standard list of rows so that we can use the same Molecule query API
      var rows = List.empty[Future[jList[AnyRef]]]
      api match {
        case "datoms" =>
          val datom2row_ = datom2row(None)
          db.flatMap(db =>
            db.asInstanceOf[DatomicDb_Client].datoms(index, args, limit = -1).flatMap { datoms =>
              datoms.forEach(datom =>
                rows = rows :+ datom2row_(datom)
              )
              Future.sequence(rows).map(_.asJavaCollection)
            }
          )

        case "indexRange" =>
          val datom2row_ = datom2row(None)
          val attrId     = args.head.toString
          val startValue = args(1).asInstanceOf[Option[Any]]
          val endValue   = args(2).asInstanceOf[Option[Any]]
          db.flatMap(db =>
            db.asInstanceOf[DatomicDb_Client].indexRange(attrId, startValue, endValue, limit = -1).flatMap { datoms =>
              datoms.forEach(datom =>
                rows = rows :+ datom2row_(datom)
              )
              Future.sequence(rows).map(_.asJavaCollection)
            }
          )

        case "txRange" =>
          val from  = args.head.asInstanceOf[Option[Any]]
          val until = args(1).asInstanceOf[Option[Any]]
          // Flatten transaction maps
          clientConn.txRangeArray(from, until, limit = -1).foreach {
            case (t, datoms) =>
              val datom2row_ = datom2row(Some(t))
              datoms.foreach(datom =>
                rows = rows :+ datom2row_(datom)
              )
          }
          Future.sequence(rows).map(_.asJavaCollection)
      }
    } catch {
      case NonFatal(ex) => Future.failed(ex)
    }
  }.flatten


  private[molecule] override def datalogQuery(
    model: Model,
    query: Query,
    _db: Option[DatomicDb]
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = Future {
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
  }.flatten


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
      txInstId = txInstants.get(read(":db/id"))
    } yield {
      try {
        _testDb = Some(clientConn.db.`with`(clientConn.withDb, list()).dbAfter)
        val txs            = clientConn.txRangeArray(Some(nextTimePoint))
        val (retract, add) = (read(":db/retract"), read(":db/add"))
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
                list(
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