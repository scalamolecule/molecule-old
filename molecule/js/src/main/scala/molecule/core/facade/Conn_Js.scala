package molecule.core.facade

import java.io.Reader
import java.net.URI
import java.util.{Date, UUID, Collection => jCollection, List => jList}
import molecule.core.ast.elements.{EntValue, Eq, Generic, NoValue}
import molecule.core.marshalling.{IndexArgs, nodes}
import molecule.core.marshalling.nodes.Obj
import molecule.datomic.base.transform.Query2String
import scala.collection.mutable.ListBuffer
//import boopickle.Default._
import molecule.core.ast.elements.Model
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.marshalling.{ConnProxy, MoleculeRpc, MoleculeWebClient}
import molecule.core.ops.ColOps
import molecule.core.util.Helpers
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReport}
import scala.concurrent.{ExecutionContext, Future}


/** Client db connection.
  *
  * Used to cary information enabling marshalling on both client and server side.
  *
  * Make a similar subclass of ConnProxy like this one in order to use an
  * alternative rpc implementation.
  *
  * @param defaultConnProxy Db coordinates to access db on server side
  */
case class Conn_Js(defaultConnProxy: ConnProxy) extends Conn with ColOps with Helpers {

  def ???(i: Int): Nothing = throw MoleculeException(s"Unexpected method call ($i) on JS side in Conn_Js")

  val isJsPlatform: Boolean = true

  override lazy val rpc: MoleculeRpc = MoleculeWebClient.rpc

  def liveDbUsed: Boolean = ???(1)

  def testDb(db: DatomicDb): Unit = ???(2)

  def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(0))))
  }

  def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(t))))
    debug("js1")
  }

  def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxDate(d))))
  }

  def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(txR.t))))
  }

  def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxLong(t))))
  }

  def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxDate(d))))
  }

  def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxLong(txR.t))))
  }

  def testDbWith(txMolecules: Future[Seq[Statement]]*)
                (implicit ec: ExecutionContext): Future[Unit] = {
    Future.sequence(txMolecules).map { stmtss =>
      val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, this)
      updateTestDbView(Some(With(stmtsEdn, uriAttrs)))
    }
  }

  def useLiveDb(): Unit = updateTestDbView(None, -1)

  def db: DatomicDb = DatomicDb_Js(rpc, connProxy)

  def entity(id: Any): DatomicEntity = db.entity(this, id)


  def transactRaw(
    javaStmts: jList[_],
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = ???(3)

  def transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] = ???(3)

  def transact(edn: String, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] = ???(4)

  def transact(stmtsReader: Reader)
              (implicit ec: ExecutionContext): Future[TxReport] = ???(5)

  def transact(edn: String)
              (implicit ec: ExecutionContext): Future[TxReport] =
    rpc.transact(connProxy, (edn, Set.empty[String]))

  def transact(scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] = {
    for {
      stmts <- scalaStmts
      result <- rpc.transact(connProxy, Stmts2Edn(stmts, this))
    } yield result
  }

  private[molecule] def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] = ???(6)


  private[molecule] def _index(model: Model, maxRows: Int): Future[String] = {
    def p(v: Any): (String, String) = v match {
      case _: String     => (v.toString, "String")
      case _: Int        => (v.toString, "Int")
      case _: Long       => (v.toString, "Long")
      case _: Float      => (v.toString, "Float")
      case _: Double     => (v.toString, "Double")
      case _: Boolean    => (v.toString, "Boolean")
      case d: Date       => (date2str(d), "Date")
      case _: UUID       => (v.toString, "UUID")
      case _: URI        => (v.toString, "URI")
      case _: BigInt     => (v.toString, "BigInt")
      case _: BigDecimal => (v.toString, "BigDecimal")
    }
    def l(v: Any) = v.toString.toLong

    val (api, index, indexArgs) = model.elements.head match {
      case Generic("EAVT", _, _, value) =>
        ("datoms", "EAVT", value match {
          case NoValue                   => IndexArgs()
          case Eq(Seq(e))                => IndexArgs(l(e))
          case Eq(Seq(e, a))             => IndexArgs(l(e), a.toString)
          case Eq(Seq(e, a, v))          => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe)
          case Eq(Seq(e, a, v, d: Date)) => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, -1L, d.getTime)
          case Eq(Seq(e, a, v, t))       => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, l(t))
          case v                         => throw MoleculeException("Unexpected EAVT value: " + v)
        })

      case Generic("AEVT", _, _, value) =>
        ("datoms", "AEVT", value match {
          case NoValue                   => IndexArgs()
          case EntValue                  => IndexArgs()
          case Eq(Seq(a))                => IndexArgs(a = a.toString)
          case Eq(Seq(a, e))             => IndexArgs(l(e), a.toString)
          case Eq(Seq(a, e, v))          => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe)
          case Eq(Seq(a, e, v, d: Date)) => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, -1L, d.getTime)
          case Eq(Seq(a, e, v, t))       => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, l(t))
          case v                         => throw MoleculeException("Unexpected AEVT value: " + v)
        })

      case Generic("AVET", attr, _, value) =>
        attr match {
          case "range" =>
            ("indexRange", "", value match {
              case Eq(Seq(a, None, None)) => IndexArgs(a = a.toString)

              case Eq(Seq(a, from, None)) =>
                val (s, tpe) = p(from)
                IndexArgs(a = a.toString, v = s, tpe = tpe)

              case Eq(Seq(a, None, until)) =>
                val (s, tpe2) = p(until)
                IndexArgs(a = a.toString, v2 = s, tpe2 = tpe2)

              case Eq(Seq(a, from, until)) =>
                if (from.getClass != until.getClass)
                  throw MoleculeException("Please supply range arguments of same type as attribute.")
                val (s1, tpe1) = p(from)
                val (s2, tpe2) = p(until)
                IndexArgs(a = a.toString, v = s1, tpe = tpe1, v2 = s2, tpe2 = tpe2)
              case v                       => throw MoleculeException("Unexpected AVET range value: " + v)
            })
          case _       =>
            ("datoms", "AVET", value match {
              case NoValue                   => IndexArgs()
              case Eq(Seq(a))                => IndexArgs(a = a.toString)
              case Eq(Seq(a, v))             => val (s, tpe) = p(v); IndexArgs(-1L, a.toString, s, tpe)
              case Eq(Seq(a, v, e))          => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe)
              case Eq(Seq(a, v, e, d: Date)) => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, -1L, d.getTime)
              case Eq(Seq(a, v, e, t))       => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, l(t))
              case v                         => throw MoleculeException("Unexpected AVET datoms value: " + v)
            })
        }

      case Generic("VAET", _, _, value) =>
        ("datoms", "VAET", value match {
          case NoValue                   => IndexArgs()
          case Eq(Seq(v))                => val (s, tpe) = p(v); IndexArgs(v = s, tpe = tpe)
          case Eq(Seq(v, a))             => val (s, tpe) = p(v); IndexArgs(a = a.toString, v = s, tpe = tpe)
          case Eq(Seq(v, a, e))          => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe)
          case Eq(Seq(v, a, e, d: Date)) => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, -1L, d.getTime)
          case Eq(Seq(v, a, e, t))       => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, l(t))
          case v                         => throw MoleculeException("Unexpected VAET value: " + v)
        })

      case Generic("Log", _, _, value) =>
        def err(v: Any) = throw MoleculeException(
          s"Args to Log can only be t, tx or txInstant of type Int/Long/Date. Found `$v`")

        ("txRange", "", value match {
          case Eq(Seq(a, b)) =>
            // Get valid from/until values
            val from  = a match {
              case None                              => null
              case from@(_: Int | _: Long | _: Date) => from
              case other                             => err(other)
            }
            val until = b match {
              case None                               => null
              case until@(_: Int | _: Long | _: Date) => until
              case other                              => err(other)
            }

            // Make IndexArgs
            (from, until) match {
              case (null, null) => IndexArgs()

              case (from, null) =>
                val (s, tpe) = p(from)
                IndexArgs(v = s, tpe = tpe)

              case (null, until) =>
                val (s, tpe2) = p(until)
                IndexArgs(v2 = s, tpe2 = tpe2)

              case (from, until) =>
                val (s1, tpe1) = p(from)
                val (s2, tpe2) = p(until)
                IndexArgs(v = s1, tpe = tpe1, v2 = s2, tpe2 = tpe2)
            }

          case Eq(Seq(from)) => from match {
            case None => IndexArgs()

            case from@(_: Int | _: Long | _: Date) =>
              val (s, tpe) = p(from)
              IndexArgs(v = s, tpe = tpe)

            case other => err(other)
          }

          case Eq(Nil) => IndexArgs()

          case Eq(other) => err(other)

          case v => throw MoleculeException("Unexpected Log value: " + v)
        })

      case other => throw MoleculeException(
        s"Only Index queries accepted (EAVT, AEVT, AVET, VAET, Log). Found `$other`"
      )
    }
    val attrs: Seq[String]      = model.elements.collect {
      case Generic(_, attr, _, _) if attr != "args_" && attr != "range" => attr
    }

    //    println(indexArgs)

    rpc.index2packed(connProxy, api, index, indexArgs, attrs)
  }

  private def jsGetRaw(
    model: Model,
    query: Query,
    datalog: String,
    maxRows: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]]
  ): Future[String] = {
    model.elements.head match {
      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _) =>
        //        println(obj)
        _index(model, maxRows)
      case _                                                           =>
        val q2s          = Query2String(query)
        val p            = q2s.p
        val rules        = if (query.i.rules.isEmpty) Nil else Seq("[" + (query.i.rules map p mkString "\n ") + "]")
        val (l, ll, lll) = marshallInputs(query)
        //        println("@@@@@@@@@@@@@@@@@@@@@@@@@'")
        //        println(query)
        //        println(datalog)
        //        println("Rules:")
        //        rules foreach println
        //
        //        println("l  : " + l)
        //        println("ll : " + ll)
        //        println("lll: " + lll)
        rpc.query2packed(
          connProxy, datalog, rules, l, ll, lll, maxRows, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes
        )
    }
  }

  private def withDbView[T](futResult: Future[T])(implicit ec: ExecutionContext): Future[T] = Future {
    if (connProxy.adhocDbView.isDefined) {
      // Reset adhoc db view
      updateAdhocDbView(None)
    }
    if (connProxy.testDbStatus == -1) {
      // Reset test db view
      updateTestDbView(None, 0)
    }
    futResult
  }.flatten

  private def queryJs[T](
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    packed2T: Iterator[String] => T,
  )(implicit ec: ExecutionContext): Future[List[T]] = withDbView(
    jsGetRaw(model, query, datalog, n, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes).map { packed =>
      if (packed.isEmpty) {
        List.empty[T]
      } else {
        val lines = packed.linesIterator
        lines.next() // skip initial newline
        val rows = new ListBuffer[T]
        while (lines.hasNext) {
          rows.addOne(packed2T(lines))
        }
        rows.toList
      }
    }
  )

  override def queryJsTpl[Tpl](
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
  )(implicit ec: ExecutionContext): Future[List[Tpl]] = queryJs(
    model, query, datalog, n, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, packed2tpl
  )

  override def queryJsObj[Obj](
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
  )(implicit ec: ExecutionContext): Future[List[Obj]] = queryJs(
    model, query, datalog, n, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, packed2obj
  )

  override def queryJsJson(
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: nodes.Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]]
  )(implicit ec: ExecutionContext): Future[String] = withDbView(
    jsGetRaw(model, query, datalog, n, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes)
  )

  def q(query: String, inputs: Any*)
       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = ???(7)

  def q(db: DatomicDb, query: String, inputs: Seq[Any])
       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = ???(8)

  def qRaw(query: String, inputs: Any*)
          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(9)

  def qRaw(db: DatomicDb, query: String, inputs0: Seq[Any])
          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(10)

  def query(model: Model, query: Query)
           (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(11)

  def _query(model: Model, query: Query, _db: Option[DatomicDb])
            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(12)

  def _index(model: Model)
            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = ???(13)

  def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = ???(14)

  def inspect(
    header: String,
    threshold: Int,
    max: Int,
    showStackTrace: Boolean,
    maxLevel: Int,
    showBi: Boolean
  )(id: Int, params: Any*): Unit = ???(15)
}
