package molecule.datomic.client.facade

import java.util
import java.util.{Date, Collection => jCollection, List => jList}
import datomic.Peer
import datomic.Util._
import datomic.db.DbId
import datomicClient.anomaly.CognitectAnomaly
import datomicScala.client.api.async.{AsyncClient, AsyncConnection}
import datomicScala.client.api.sync.{Client, Db, Datomic => clientDatomic}
import datomicScala.client.api.{Datom, sync}
import molecule.core.ast.elements._
import molecule.core.exceptions._
import molecule.core.util.QueryOpsClojure
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.{Conn, Conn_Datomic213, DatomicDb, TxReport}
import molecule.datomic.base.marshalling.DatomicRpc.getJavaStmts
import molecule.datomic.base.transform.Query2String
import scala.concurrent.{ExecutionContext, Future, blocking}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal


/** Facade to Datomic connection for client api (peer-server/cloud/dev-local).
  * */
case class Conn_Client(
  client: Client,
  clientAsync: AsyncClient,
  dbName: String,
  system: String = ""
) extends Conn_Datomic213 {

  lazy val clientConn: sync.Connection = client.connect(dbName)

  lazy val clientConnAsync: Future[Either[CognitectAnomaly, AsyncConnection]] =
    clientAsync.connect(dbName)

  // In-memory fixed test db for integration testing of domain model
  // (takes precedence over live db)
  protected var _testDb: Option[Db] = None

  // Flag to indicate if special withDb is in use for testDb
  protected var withDbInUse = false


  def liveDbUsed: Boolean = _adhocDbView.isEmpty && _testDb.isEmpty

  def testDb(db: DatomicDb): Unit = {
    _testDb = Some(db.asInstanceOf[DatomicDb_Client].clientDb)
  }


  // Reset datoms of in-mem with-db from next timePoint after as-of t until end
  override def cleanFrom(nextTimePoint: Any)
                        (implicit ec: ExecutionContext): Future[Unit] = {
    for {
      txInstants <- db.pull("[:db/id]", ":db/txInstant")
      txInstId = txInstants.get(read(":db/id"))
    } yield {
      blocking {
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
              txStmts.add(list(op(datom), datom.e, datom.a, datom.v))
            }
          }
          // Update in-memory with-db with datoms of this tx
          _testDb = Some(_testDb.get.`with`(_testDb.get, txStmts).dbAfter)
          i -= 1
        }
        withDbInUse = true
      }
    }
  }


  def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit] = Future {
    _testDb = Some(clientConn.db)
  }

  // Temporary since time points - needs to be applied later to queries in order
  // to maintain special withdb
  private var sinceT = Option.empty[Long]
  private var sinceD = Option.empty[Date]

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
    Future.sequence(txMolecules).map { stmtss =>
      testDbWith(stmts2java(stmtss.flatten))
    }
  }

  def testDbWith(txDataJava: jList[jList[_]])(implicit ec: ExecutionContext): Future[Unit] = Future {
    tempId.reset()
    _testDb = Some(clientConn.db.`with`(clientConn.withDb, txDataJava).dbAfter)
    withDbInUse = true
  }

  def useLiveDb: Unit = {
    sinceT = None
    sinceD = None
    withDbInUse = false
    _testDb = None
  }


  private def getAdhocDb: Db = {
    val baseDb : Db = _testDb.getOrElse(clientConn.db)
    val adhocDb: Db = _adhocDbView.get match {
      case AsOf(TxLong(t))          => baseDb.asOf(t)
      case AsOf(TxDate(d))          => baseDb.asOf(d)
      case Since(TxLong(t))         => baseDb.since(t)
      case Since(TxDate(d))         => baseDb.since(d)
      case History                  => baseDb.history
      case With(stmtsEdn, uriAttrs) =>
        val txData = getJavaStmts(stmtsEdn, uriAttrs)
        baseDb.`with`(clientConn.withDb, txData).dbAfter
    }
    _adhocDbView = None
    adhocDb
  }


//  // Temporary since time points - needs to be applied later to queries in order
//  // to maintain special withdb
//  private var sinceT = Option.empty[Long]
//  private var sinceD = Option.empty[Date]

  def db: DatomicDb = {
    if (_adhocDbView.isDefined) {
      // Adhoc db
      DatomicDb_Client(getAdhocDb)

    } else if (_testDb.isDefined) {
      // Test db
      if (sinceT.isDefined) {
        DatomicDb_Client(_testDb.get.since(sinceT.get))
      } else if (sinceD.isDefined) {
        DatomicDb_Client(_testDb.get.since(sinceD.get))
      } else {
        DatomicDb_Client(_testDb.get)
      }

    } else {
      // Live db
      DatomicDb_Client(clientConn.db)
    }
  }

  def entity(id: Any): DatomicEntity =
    db.entity(this, id)


  def transactRaw(
    javaStmts: jList[_],
    futScalaStmts: Future[Seq[Statement]] = Future.successful(Seq.empty[Statement])
  )(implicit ec: ExecutionContext): Future[TxReport] = try {
    futScalaStmts.map { scalaStmts =>
      if (_adhocDbView.isDefined) {
        TxReport_Client(getAdhocDb.`with`(clientConn.withDb, javaStmts), scalaStmts)

      } else if (_testDb.isDefined) {
        // In-memory "transaction"
        val txReport = TxReport_Client(_testDb.get.`with`(clientConn.withDb, javaStmts), scalaStmts)

        // Continue with updated in-memory db
        // todo: why can't we just say this? Or: why are there 2 db-after db objects?
        //      val dbAfter = txReport.dbAfter
        val dbAfter = txReport.dbAfter.asOf(txReport.t)
        _testDb = Some(dbAfter)
        txReport

      } else {
        // Live transaction
        TxReport_Client(clientConn.transact(javaStmts), scalaStmts)
      }
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }


  def qRaw(
    db: DatomicDb,
    query: String,
    inputs0: Seq[Any]
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = Future {
    val inputs = inputs0.map {
      case it: Iterable[_] => it.toList.asJava
      case dbId: DbId      => dbId.idx.toString
      case bi: BigInt      => new java.math.BigInteger(bi.toString)
      case v               => v
    }
    blocking(
      clientDatomic.q(
        query,
        db.asInstanceOf[DatomicDb_Client].clientDb,
        inputs.asInstanceOf[Seq[AnyRef]]: _*
      )
    )
  }

  def query(model: Model, query: Query)
           (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    model.elements.head match {
      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _) => _index(model)
      case _                                                           => _query(model, query)
    }
  }

  private[molecule] def _query(
    model: Model,
    query: Query,
    _db: Option[DatomicDb]
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    try {
      Future {
        val p               = Query2String(query).p
        val rules           = if (query.i.rules.isEmpty) Nil else Seq("[" + (query.i.rules map p mkString " ") + "]")
        val inputsEvaluated = QueryOpsClojure(query).inputsWithKeyword
        val allInputs       = rules ++ inputsEvaluated
        val adhocDb         = _db.getOrElse(db).asInstanceOf[DatomicDb_Client].clientDb
        clientDatomic.q(query.toMap, adhocDb, allInputs: _*)
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
              case Eq(Seq(a, None, None))  => Seq(a, None, None)
              case Eq(Seq(a, from, None))  => Seq(a, Some(from), None)
              case Eq(Seq(a, None, until)) => Seq(a, None, Some(until))
              case Eq(Seq(a, from, until)) =>
                if (from.getClass != until.getClass)
                  throw MoleculeException("Please supply range arguments of same type as attribute.")
                Seq(a, Some(from), Some(until))
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
        ("txRange", "", value match {
          case Eq(Seq(from: Int, until: Int))   => Seq(Some(from), Some(until))
          case Eq(Seq(from: Int, until: Long))  => Seq(Some(from), Some(until))
          case Eq(Seq(from: Int, until: Date))  => Seq(Some(from), Some(until))
          case Eq(Seq(from: Long, until: Int))  => Seq(Some(from), Some(until))
          case Eq(Seq(from: Long, until: Long)) => Seq(Some(from), Some(until))
          case Eq(Seq(from: Long, until: Date)) => Seq(Some(from), Some(until))
          case Eq(Seq(from: Date, until: Int))  => Seq(Some(from), Some(until))
          case Eq(Seq(from: Date, until: Long)) => Seq(Some(from), Some(until))
          case Eq(Seq(from: Date, until: Date)) => Seq(Some(from), Some(until))

          case Eq(Seq(from: Int, None))  => Seq(Some(from), None)
          case Eq(Seq(from: Long, None)) => Seq(Some(from), None)
          case Eq(Seq(from: Date, None)) => Seq(Some(from), None)

          case Eq(Seq(None, until: Int))  => Seq(None, Some(until))
          case Eq(Seq(None, until: Long)) => Seq(None, Some(until))
          case Eq(Seq(None, until: Date)) => Seq(None, Some(until))

          // All !!
          case Eq(Seq(None, None)) => Seq(None, None)

          // From until end
          case Eq(Seq(from: Int))  => Seq(Some(from), None)
          case Eq(Seq(from: Long)) => Seq(Some(from), None)
          case Eq(Seq(from: Date)) => Seq(Some(from), None)

          // All !!
          case Eq(Nil) => Seq(None, None)

          case Eq(other) => throw MoleculeException(
            "Args to Log can only be t, tx or txInstant of type Int/Long/Date. Found: " + other)

          case v => throw MoleculeException("Unexpected Log value: " + v)
        })

      case other => throw MoleculeException(s"Only Index queries accepted (EAVT, AEVT, AVET, VAET, Log). Found `$other`")
    }


    lazy val attrNames: Future[Array[String]] = {
      // Since the number of definitions is limited we can quickly collect them
      // for fast lookups in an array by index = attr id.
      // 5000 slots should satisfy any schema.
      val array = new Array[String](5000)
      qRaw(
        """[:find  ?id ?idIdent
          | :where [_ :db.install/attribute ?id]
          |        [?id :db/ident ?idIdent]
          |        ]""".stripMargin, db.getDatomicDb).map { rows =>
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
      // Some initial transactions lack tx time it seems, so there we default to time 0 (Thu Jan 01 01:00:00 CET 1970)
      db.pull("[:db/txInstant]", tx).map {
        case null => defaultDate
        case res  => res.get(txInstant).asInstanceOf[Date]
      }
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
        val raw        = db.asInstanceOf[DatomicDb_Client].datoms(index, args, limit = -1)
        raw.map { datoms =>
          datoms.forEach(datom => datom2row_(datom).map(row => jColl.add(row)))
          jColl
        }

      case "indexRange" =>
        val datom2row_ = datom2row(None)
        val attrId     = args.head.toString
        val startValue = args(1).asInstanceOf[Option[Any]]
        val endValue   = args(2).asInstanceOf[Option[Any]]
        val raw        = db.asInstanceOf[DatomicDb_Client].indexRange(attrId, startValue, endValue, limit = -1)
        raw.map { datoms =>
          datoms.forEach(datom => datom2row_(datom).map(row => jColl.add(row)))
          jColl
        }

      case "txRange" =>
        val from  = args.head.asInstanceOf[Option[Any]]
        val until = args(1).asInstanceOf[Option[Any]]
        // Flatten transaction datoms to unified tuples return type
        val raw   = Future(clientConn.txRangeArray(from, until, limit = -1))
        raw.map { datoms =>
          datoms.foreach {
            case (t, datoms) =>
              // Use t from txRange result
              val datom2row_ = datom2row(Some(t))
              datoms.foreach { datom =>
                datom2row_(datom).map(row => jColl.add(row))
              }
          }
          jColl
        }
    }
  }
}