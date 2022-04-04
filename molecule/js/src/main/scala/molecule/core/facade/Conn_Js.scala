package molecule.core.facade

import java.net.URI
import java.util.{Date, UUID}
import boopickle.Default._
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling._
import molecule.core.marshalling.ast.nodes.Obj
import molecule.core.marshalling.ast.{ConnProxy, IndexArgs, SortCoordinate, nodes}
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.ops.ModelOps
import molecule.core.util.{Helpers, Inspect}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReport}
import molecule.datomic.base.transform.{Model2Query, Query2String}
import scala.concurrent.{ExecutionContext, Future}


/** Client connection.
 *
 * Used to cary information enabling marshalling on both client and server side.
 *
 * Make a similar subclass of ConnProxy like this one in order to use an
 * alternative rpc implementation.
 *
 * @param defaultConnProxy Db coordinates to access db on server side
 */
case class Conn_Js(
  override val defaultConnProxy: ConnProxy,
  interface: String,
  port: Int
)(implicit ec: ExecutionContext) extends WebClient() with Conn with Helpers with ModelOps {

  // Molecule api --------------------------------------------------------------

  final def testDbAsOfNow(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(0))))
  }

  final def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(t))))
  }

  final def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxDate(d))))
  }

  final def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(AsOf(TxLong(txR.t))))
  }

  final def testDbSince(t: Long)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxLong(t))))
  }

  final def testDbSince(d: Date)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxDate(d))))
  }

  final def testDbSince(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = Future {
    updateTestDbView(Some(Since(TxLong(txR.t))))
  }

  final def testDbWith(txMolecules: Future[Seq[Statement]]*)
                      (implicit ec: ExecutionContext): Future[Unit] = {
    Future.sequence(txMolecules).map { stmtss =>
      val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, this)
      updateTestDbView(Some(With(stmtsEdn, uriAttrs)))
    }
  }

  final def useLiveDb(): Unit = updateTestDbView(None, -1)


  // Datomic shared Peer/Client api --------------------------------------------

  final def db(implicit ec: ExecutionContext): Future[DatomicDb] =
    Future(DatomicDb_Js(rpc, connProxy))


  final def transact(edn: String)
                    (implicit ec: ExecutionContext): Future[TxReport] =
    rpc.transact(connProxy, edn, Set.empty[String])


  final def sync: Conn =
    usingAdhocDbView(Sync(0))

  final def sync(t: Long): Conn =
    usingAdhocDbView(Sync(t))


  // Schema --------------------------------------------------------------------

  final def getEnumHistory(implicit ec: ExecutionContext)
  : Future[List[(String, Int, Long, Date, String, Boolean)]] =
    rpc.getEnumHistory(connProxy)


  // Internal ------------------------------------------------------------------

  private[molecule] final val isJsPlatform: Boolean = true

  private[molecule] final override lazy val rpc: MoleculeRpc =
    moleculeAjax(interface, port).wire[MoleculeRpc]


  private[molecule] def transact(scalaStmts: Future[Seq[Statement]])
                                (implicit ec: ExecutionContext): Future[TxReport] = for {
    stmts <- scalaStmts
    txReport <- rpc.transact(connProxy, Stmts2Edn(stmts, this))
  } yield txReport


  private[molecule] final override def jsQuery[T](
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    sortCoordinates: List[List[SortCoordinate]],
    unpacker: Iterator[String] => T
  )(implicit ec: ExecutionContext): Future[List[T]] = withDbView(
    jsMoleculeQuery(
      model, query, datalog, n, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, sortCoordinates
    ).map(packed => unpack(packed, unpacker))
  )

  private def unpack[T](packed: String, unpacker: Iterator[String] => T): List[T] = {
    if (packed.isEmpty) {
      List.empty[T]
    } else {
      val lines = packed.linesIterator
      lines.next() // skip initial newline
      var rows = List.empty[T]
      while (lines.hasNext) {
        rows = rows :+ unpacker(lines)
      }
      rows
    }
  }

  private[molecule] final override def jsQueryJson(
    model: Model,
    query: Query,
    datalog: String,
    n: Int,
    obj: nodes.Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    sortCoordinates: List[List[SortCoordinate]]
  )(implicit ec: ExecutionContext): Future[String] = withDbView(
    jsMoleculeQuery(model, query, datalog, n, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, sortCoordinates)
  )

  private[molecule] final override def jsSchemaHistoryQuery[T](
    model: Model,
    obj: Obj,
    sortCoordinates: List[List[SortCoordinate]],
    unpacker: Iterator[String] => T
  )(implicit ec: ExecutionContext): Future[List[T]] = {
    val queryString = Model2Query(model, schemaHistory0 = true, optimize = false)._2
    val schemaAttrs = model2schemaAttrs(model)
    rpc.schemaHistoryQuery2packed(connProxy, queryString, obj, schemaAttrs, sortCoordinates)
      .map(packed => unpack(packed, unpacker))
  }
  private[molecule] final override def jsSchemaHistoryQueryJson(
    model: Model,
    obj: Obj,
    sortCoordinates: List[List[SortCoordinate]]
  )(implicit ec: ExecutionContext): Future[String] = {
    val queryString = Model2Query(model, schemaHistory0 = true, optimize = false)._2
    val schemaAttrs = model2schemaAttrs(model)
    rpc.schemaHistoryQuery2packed(connProxy, queryString, obj, schemaAttrs, sortCoordinates)
  }

  private final def jsMoleculeQuery(
    model: Model,
    query: Query,
    datalog: String,
    maxRows: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[String] = {
    model.elements.head match {
      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _, _) => indexQuery(model, sortCoordinates)
      case _                                                              => datalogQuery(
        query, datalog, maxRows, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, sortCoordinates
      )
    }
  }

  private final def indexQuery(
    model: Model,
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[String] = {
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
      case Generic("EAVT", _, _, value, _) =>
        ("datoms", "EAVT", value match {
          case NoValue                   => IndexArgs()
          case Eq(Seq(e))                => IndexArgs(l(e))
          case Eq(Seq(e, a))             => IndexArgs(l(e), a.toString)
          case Eq(Seq(e, a, v))          => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe)
          case Eq(Seq(e, a, v, d: Date)) => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, -1L, d.getTime)
          case Eq(Seq(e, a, v, t))       => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, l(t))
          case v                         => throw MoleculeException("Unexpected EAVT value: " + v)
        })

      case Generic("AEVT", _, _, value, _) =>
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

      case Generic("AVET", attr, _, value, _) =>
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

      case Generic("VAET", _, _, value, _) =>
        ("datoms", "VAET", value match {
          case NoValue                   => IndexArgs()
          case Eq(Seq(v))                => val (s, tpe) = p(v); IndexArgs(v = s, tpe = tpe)
          case Eq(Seq(v, a))             => val (s, tpe) = p(v); IndexArgs(a = a.toString, v = s, tpe = tpe)
          case Eq(Seq(v, a, e))          => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe)
          case Eq(Seq(v, a, e, d: Date)) => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, -1L, d.getTime)
          case Eq(Seq(v, a, e, t))       => val (s, tpe) = p(v); IndexArgs(l(e), a.toString, s, tpe, l(t))
          case v                         => throw MoleculeException("Unexpected VAET value: " + v)
        })

      case Generic("Log", _, _, value, _) =>
        def err(v: Any) = throw MoleculeException(
          s"Args to Log can only be t, tx or txInstant of type Int/Long/Date. Found `$v` of type " + v.getClass)

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
      case Generic(_, attr, _, _, _) if attr != "args_" && attr != "range" => attr
    }

    rpc.index2packed(connProxy, api, index, indexArgs, attrs, sortCoordinates)
  }


  private final def datalogQuery(
    query: Query,
    datalog: String,
    maxRows: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    sortCoordinates: List[List[SortCoordinate]]
  ): Future[String] = {
    val q2s          = Query2String(query)
    val p            = q2s.p
    val rules        = if (query.i.rules.isEmpty) Nil else Seq("[" + (query.i.rules map p mkString "\n ") + "]")
    val (l, ll, lll) = marshallInputs(query)

    //    //    println("================================================================================")
    ////    //    println(query)
    ////    //    println(datalog)
    //    if (rules.nonEmpty) {
    //      println("Rules:")
    //      rules foreach println
    //    }

    //    println("--------------------------")
    //    println("l  : " + l)
    //    println("ll : " + ll)
    //    println("lll: " + lll)

    rpc.query2packed(
      connProxy, datalog, rules, l, ll, lll, maxRows,
      obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, sortCoordinates
    )
  }


  // Internal convenience method conn.entity(id) for conn.db.entity(conn, id)
  private[molecule] final def entity(
    id: Any
  )(implicit ec: ExecutionContext): Future[DatomicEntity] = db.map(_.entity(this, id))


  def inspect(
    header: String,
    threshold: Int,
    max: Int,
    showStackTrace: Boolean,
    maxLevel: Int,
    showBi: Boolean
  )(id: Int, params: Any*): Unit = Inspect(
    header, threshold, max, showStackTrace, maxLevel, showBi
  )(id, params: _*)


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


  private[molecule] def getAttrValues(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    rpc.getAttrValues(connProxy, datalogQuery, card, tpe)

  private[molecule] def getEntityAttrKeys(
    query: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    rpc.getEntityAttrKeys(connProxy, query)
}
