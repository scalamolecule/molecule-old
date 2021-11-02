package molecule.datomic.base.util

import java.util
import java.util.{Collections, Date, Collection => jCollection, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.exceptions.{MoleculeException, QueryException}
import molecule.core.marshalling.Marshalling
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.ops.VerifyModel
import molecule.core.util.JavaConversions
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.base.ops.QueryOps._
import molecule.datomic.base.transform._
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.control.NonFatal


/** Inspect methods
  *
  * Call a inspect method on a molecule to see the internal transformations and
  * produced transaction statements or sample data.
  * */
trait ShowInspect[Obj, Tpl] extends JavaConversions { self: Marshalling[Obj, Tpl] =>
  val maxRows = 500

  /** Inspect call to `get` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    * OBS: printing raw Date's (Clojure Instant) will miss the time-zone
    *
    * @group inspectGet
    * @param futConn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectGet(implicit futConn: Future[Conn]): Future[Unit] = {

    def renderIndex(): Future[Unit] = {
      def render(rows: jCollection[_ <: jList[_]]): Unit = {
        val pE = 14
        val pA = 20
        val pV = 50
        val pT = 6
        val pD = 28

        print(
          s"""
             |${_model.toString}
             |     """.stripMargin)

        var pad = 0
        def p(v: Any, i: Int): String = v.toString.padTo(i, ' ') + "   "
        val n = {
          val dataElements: Seq[Generic] = _model.elements.
            collect {
              case e@Generic(_, attr, _, _) if attr != "args_" && attr != "range" => e
            }
          dataElements.zipWithIndex.map {
            case (Generic(_, "e", _, _), i)         => print(p("E", pE)); pad += pE + 3; i -> pE
            case (Generic(_, "a", _, _), i)         => print(p("A", pA)); pad += pA + 3; i -> pA
            case (Generic(_, "v", _, _), i)         => print(p("V", pV)); pad += pV + 3; i -> pV
            case (Generic(_, "t", _, _), i)         => print(p("T", pT)); pad += pT + 3; i -> pT
            case (Generic(_, "tx", _, _), i)        => print(p("Tx", pE)); pad += pE + 3; i -> pE
            case (Generic(_, "txInstant", _, _), i) => print(p("TxInstant", pD)); pad += pD + 3; i -> pD
            case (Generic(_, "op", _, _), i)        => print(p("Op", pT)); pad += pT + 3; i -> pT
            case other                              => throw MoleculeException("Unexpected element: " + other)
          }.toMap
        }

        println()
        println("-----" + "-" * pad)

        var i = 0
        n.size match {
          case 0 => rows forEach { row =>
            i += 1;
            if (i <= maxRows) {
              println(p(i, 2))
            }
          }
          case 1 => rows.forEach { row =>
            i += 1;
            if (i <= maxRows) {
              println(p(i, 2) +
                p(row.get(0), n(0)))
            }
          }
          case 2 => rows.forEach { row =>
            i += 1;
            if (i <= maxRows) {
              println(p(i, 2) +
                p(row.get(0), n(0)) +
                p(row.get(1), n(1)))
            }
          }
          case 3 => rows.forEach { row =>
            i += 1;
            if (i <= maxRows) {
              println(p(i, 2) +
                p(row.get(0), n(0)) +
                p(row.get(1), n(1)) +
                p(row.get(2), n(2)))
            }
          }
          case 4 => rows.forEach { row =>
            i += 1;
            if (i <= maxRows) {
              println(p(i, 2) +
                p(row.get(0), n(0)) +
                p(row.get(1), n(1)) +
                p(row.get(2), n(2)) +
                p(row.get(3), n(3)))
            }
          }
          case 5 => rows.forEach { row =>
            i += 1;
            if (i <= maxRows) {
              println(p(i, 2) +
                p(row.get(0), n(0)) +
                p(row.get(1), n(1)) +
                p(row.get(2), n(2)) +
                p(row.get(3), n(3)) +
                p(row.get(4), n(4)))
            }
          }
          case 6 => rows.forEach { row =>
            i += 1;
            if (i <= maxRows) {
              println(p(i, 2) +
                p(row.get(0), n(0)) +
                p(row.get(1), n(1)) +
                p(row.get(2), n(2)) +
                p(row.get(3), n(3)) +
                p(row.get(4), n(4)) +
                p(row.get(5), n(5)))
            }
          }
          case 7 => rows.forEach { row =>
            i += 1;
            if (i <= maxRows) {
              println(p(i, 2) +
                p(row.get(0), n(0)) +
                p(row.get(1), n(1)) +
                p(row.get(2), n(2)) +
                p(row.get(3), n(3)) +
                p(row.get(4), n(4)) +
                p(row.get(5), n(5)) +
                p(row.get(6), n(6)))
            }
          }
        }

        println("-----" + "-" * pad)
        if (rows.size > maxRows)
          println(s"(showing $maxRows out of ${rows.size} rows)")
        println()
      }

      for {
        conn <- futConn
        rows <- if (conn.isJsPlatform) jsRows(conn) else conn.indexQuery(_model)
      } yield render(rows)
    }

    def jsRows(conn: Conn): Future[jCollection[_ <: jList[_]]] = {
      // Converting tuples to java list (for now - not critical for inspection of 500 rows)
      conn.jsQueryTpl(
        _model, _query, _datalog, -1, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, packed2tpl
      ).map { listOfTuples =>
        listOfTuples.map {
          case tpl: Product => Collections.list(tpl.productIterator.asJavaEnumeration)
          case v            =>
            val list = new util.ArrayList[Any](1)
            list.add(v)
            Collections.list(Collections.enumeration(list))
        }.asJava
      }
    }

    def isAggr(fn: String) = Seq("count", "count-distinct", "avg", "variance", "stddev").contains(fn)

    // level, (card, isOptional, isDate, isAggr)
    def recurse(level: Int, acc: Seq[(Int, Boolean, Boolean, Boolean)], elements: Seq[Element])
    : (Int, Seq[(Int, Boolean, Boolean, Boolean)]) = {
      elements.foldLeft(level, acc) {
        case ((level, acc), Generic(_, "txInstant", _, _))                      => (level, acc :+ (1, false, true, false))
        case ((level, acc), Generic(_, _, "datom" | "schema", _))               => (level, acc :+ (1, false, false, false))
        case ((level, acc), ga: GenericAtom) if ga.attr.last == '_'             => (level, acc)
        case ((level, acc), Atom(_, _, _, _, Fn(fn, _), _, _, _)) if isAggr(fn) => (level, acc :+ (1, false, false, true))
        case ((level, acc), Atom(_, attr, "Date", card, _, _, _, _))            => (level, acc :+ (card, attr.last == '$', true, false))
        case ((level, acc), Atom(_, attr, _, card, _, _, _, _))                 => (level, acc :+ (card, attr.last == '$', false, false))
        case ((level, acc), Nested(_, nestedElements))                          => recurse(level + 1, acc, nestedElements)
        case ((level, acc), Composite(compositeElements))                       => recurse(level, acc, compositeElements)
        case ((level, acc), TxMetaData(txElements))                             => recurse(level, acc, txElements)
        case ((level, acc), _)                                                  => (level, acc)
      }
    }

    val outputMatrix: Seq[(Int, Boolean, Boolean, Boolean)] = {
      val (levels, outMatrix) = recurse(0, Seq.empty[(Int, Boolean, Boolean, Boolean)], _model.elements)
      (0 to levels).toList.map(level => (1, false, false, false)) ++ outMatrix
    }

    def resolve(rawRows: Iterable[_ <: jList[_]]): Seq[ListBuffer[Any]] = {
      def cardOneOpt(v: Any, isDate: Boolean): Option[String] = {
        if (v == null) {
          Option.empty[String]
        } else if (isDate) {
          if (v.isInstanceOf[jMap[_, _]]) {
            Some(date2str(v.asInstanceOf[jMap[String, Date]].values.iterator.next))
          } else {
            Some(date2str(v.asInstanceOf[Date]))
          }
        } else {
          if (v.isInstanceOf[jMap[_, _]]) {
            Some(v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
          } else {
            Some(v.toString)
          }
        }
      }

      def cardMany(v: Any, isDate: Boolean): Set[String] = {
        val it  = v.asInstanceOf[jSet[_]].iterator
        var set = Set.empty[String]
        if (isDate) {
          while (it.hasNext) {
            set = set + date2str(it.next.asInstanceOf[Date])
          }
        } else {
          while (it.hasNext) {
            set = set + it.next.toString
          }
        }
        set
      }
      def cardManyOpt(v: Any, isDate: Boolean): Option[Set[String]] = {
        if (v == null) {
          Option.empty[Set[String]]
        } else v match {
          case set1: jSet[_] =>
            val it  = set1.iterator
            var set = Set.empty[String]
            if (isDate) {
              while (it.hasNext)
                set = set + date2str(it.next.asInstanceOf[Date])
            } else {
              while (it.hasNext)
                set = set + it.next.toString
            }
            Some(set)
          case _             =>
            val it  = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
            var set = Set.empty[String]
            if (isDate) {
              while (it.hasNext)
                set = set + date2str(it.next.asInstanceOf[Date])
            } else {
              while (it.hasNext)
                set = set + it.next.toString
            }
            Some(set)
        }
      }

      def cardMap(v: Any): Map[String, String] = {
        val it   = v.asInstanceOf[jSet[_]].iterator
        var map  = Map.empty[String, String]
        var pair = new Array[String](2)
        while (it.hasNext) {
          pair = it.next.toString.split("@", 2)
          map = map + (pair(0) -> pair(1))
        }
        map
      }
      def cardMapOpt(v: Any): Option[Map[String, String]] = {
        if (v == null) {
          Option.empty[Map[String, String]]
        } else {
          val it   = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
          var map  = Map.empty[String, String]
          var pair = new Array[String](2)
          while (it.hasNext) {
            pair = it.next.toString.split("@", 2)
            map = map + (pair(0) -> pair(1))
          }
          Some(map)
        }
      }

      var rowLength = 0
      val rows1     = new ListBuffer[ListBuffer[Any]]
      var i         = 0
      var j         = 0
      val it        = rawRows.iterator
      while (it.hasNext) {
        j = 0
        val rawRow = it.next()
        if (i == 0)
          rowLength = rawRow.size()
        val newRow = new ListBuffer[Any]
        while (j < rowLength) {
          val v = rawRow.get(j)
          newRow += (outputMatrix(j) match {
            case (_, _, _, true)       => v
            case (1, false, true, _)   => date2str(v.asInstanceOf[Date])
            case (1, true, isDate, _)  => cardOneOpt(v, isDate)
            case (2, false, isDate, _) => cardMany(v, isDate)
            case (2, true, isDate, _)  => cardManyOpt(v, isDate)
            case (3, false, _, _)      => cardMap(v)
            case (3, true, _, _)       => cardMapOpt(v)
            case (_, _, _, _)          => v
          })
          j += 1
        }
        rows1 += newRow
        i += 1
      }
      rows1.toList
    }


    def renderQuery(): Future[Unit] = futConn.flatMap { conn =>
      try {
        // Also do Model2Query transformation at runtime to be able to debug it.
        // Note though that input variables are bound in the macro at compile
        // time and are therefore not present in this runtime process.
        Model2Query(_model)

        val ins = QueryOps(_query).inputs
        val db  = conn.db

        for{
          rawRows <- if (conn.isJsPlatform) jsRows(conn) else conn.datalogQuery(_model, _query, Some(db))
          rows = resolve(rawRows.asScala.take(maxRows))
        } yield {
          val rulesOut: String = if (_query.i.rules.isEmpty)
            "none\n\n"
          else
            "[\n " + _query.i.rules.map(Query2String(_query).p(_)).mkString("\n ") + "\n]\n\n"

          val inputs: String = if (ins.isEmpty)
            "none\n\n"
          else
            "\n" + ins.zipWithIndex.map(r => s"${r._2 + 1}: ${r._1}").mkString("\n") + "\n\n"

          val outs = rows.zipWithIndex.map(r => s"${r._2 + 1}: ${r._1.mkString("[", "  ", "]")}").mkString("\n")

          // print both raw and optimized
          //      val query    = _nestedQuery.getOrElse(_query)
          //          println(
          //            "\n--------------------------------------------------------------------------\n" +
          //              _model + "\n\n\n" +
          //              "RAW -------------------\n\n" +
          //              rawQuery + "\n\n" +
          //              rawQuery.datalog + "\n\n\n" +
          //              "OPTIMIZED -------------\n\n" +
          //              query + "\n\n" +
          //              query.datalog + "\n\n" +
          //              "RULES: " + rulesOut +
          //              "INPUTS: " + inputs +
          //              "OUTPUTS:\n" + outs + "\n(showing up to 500 rows)" +
          //              "\n--------------------------------------------------------------------------\n"
          //          )

          // Usually we want to analyse the raw query only
          println(
            "\n--------------------------------------------------------------------------\n" +
              _model + "\n\n" +
              _query + "\n\n" +
              _query.datalog + "\n\n" +
              "RULES: " + rulesOut +
              "INPUTS: " + inputs +
              "OUTPUTS:\n" + outs + "\n(showing up to 500 rows)" +
              "\n--------------------------------------------------------------------------\n"
          )
        }
      } catch {
        case NonFatal(exc) => Future.failed(QueryException(exc, _model, _query))
      }
    }

    _model.elements.head match {
      case Generic("Log" | "EAVT" | "AEVT" | "AVET" | "VAET", _, _, _) => renderIndex()
      case _                                                           => renderQuery()
    }
  }


  /** Inspect call to `getAsOf(t)` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    *
    * @group inspectGet
    * @param t
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectGetAsOf(t: Long)(implicit conn: Future[Conn]): Future[Unit] =
    inspectGet(conn.map(_.usingAdhocDbView(AsOf(TxLong(t)))))


  /** Inspect call to `getAsOf(tx)` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    *
    * @group inspectGet
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectGetAsOf(tx: TxReport)(implicit conn: Future[Conn]): Future[Unit] =
    inspectGet(conn.map(_.usingAdhocDbView(AsOf(TxLong(tx.t)))))


  /** Inspect call to `getAsOf(date)` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    *
    * @group inspectGet
    * @param date
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scsope
    */
  def inspectGetAsOf(date: Date)(implicit conn: Future[Conn]): Future[Unit] =
    inspectGet(conn.map(_.usingAdhocDbView(AsOf(TxDate(date)))))


  /** Inspect call to `getSince(t)` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    *
    * @group inspectGet
    * @param t
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectGetSince(t: Long)(implicit conn: Future[Conn]): Future[Unit] =
    inspectGet(conn.map(_.usingAdhocDbView(Since(TxLong(t)))))


  /** Inspect call to `getSince(tx)` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    *
    * @group inspectGet
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectGetSince(tx: TxReport)(implicit conn: Future[Conn]): Future[Unit] =
    inspectGet(conn.map(_.usingAdhocDbView(Since(TxLong(tx.t)))))


  /** Inspect call to `getSince(date)` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    *
    * @group inspectGet
    * @param date
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectGetSince(date: Date)(implicit conn: Future[Conn]): Future[Unit] =
    inspectGet(conn.map(_.usingAdhocDbView(Since(TxDate(date)))))


  /** Inspect call to `getWith(txMolecules)` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    * <br><br>
    * 3. Transactions of applied transaction molecules.
    *
    * @group inspectGet
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectGetWith(txMolecules: Seq[Statement]*)(implicit conn: Future[Conn]): Future[Unit] = {
    inspectGet(
      conn.map { conn2 =>
        val (stmtsEdn, uriAttrs) = Stmts2Edn(txMolecules.flatten, conn2)
        conn2.usingAdhocDbView(With(stmtsEdn, uriAttrs))
        //        println("Transaction data:\n========================================================================")
        //        txMolecules.zipWithIndex foreach { case (stmts, i) =>
        //          conn2.inspect(s"Statements, transaction molecule ${i + 1}:", 1)(i + 1, stmts)
        //        }
        //        conn2
      }
    )
      .flatMap { _ =>
        conn.map { conn2 =>
          println("Transaction data:\n========================================================================")
          txMolecules.zipWithIndex foreach { case (stmts, i) =>
            conn2.inspect(s"Statements, transaction molecule ${i + 1}:", 1)(i + 1, stmts)
          }
        }
      }
  }


  /** Inspect call to `getHistory` on a molecule (without affecting the db).
    * <br><br>
    * Prints the following to output:
    * <br><br>
    * 1. Internal molecule transformation representations:
    * <br>Molecule DSL --> Model --> Query --> Datomic query
    * <br><br>
    * 2. Data returned from get query (max 500 rows).
    *
    * @group inspectGet
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectGetHistory(implicit conn: Future[Conn]): Future[Unit] = inspectGet(conn.map(_.usingAdhocDbView(History)))


  /** Inspect call to `save` on a molecule (without affecting the db).
    * <br><br>
    * Prints internal molecule transformation representations to output:
    * <br><br>
    * Model --> Generic statements --> Datomic statements
    *
    * @group inspectOp
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return Unit
    */
  def inspectSave(implicit conn: Future[Conn]): Future[Unit] = conn.flatMap { conn =>
    val transformer = conn.model2stmts(_model)
    try {
      VerifyModel(_model, "save")
      transformer.saveStmts.map(stmts =>
        conn.inspect("output.Molecule.inspectSave", 1)(1, _model, transformer.genericStmts, stmts)
      )
    } catch {
      case NonFatal(exc) =>
        println("@@@@@@@@@@@@@@@@@  Error - data processed so far:  @@@@@@@@@@@@@@@@@\n")
        conn.inspect("output.Molecule.inspectSave", 1)(1, _model, transformer.genericStmts)
        Future.failed(exc)
    }
  }


  protected def _inspectInsert(conn: Future[Conn], dataRows: Iterable[Seq[Any]]): Future[Unit] = {
    conn.flatMap { conn =>
      val transformer = conn.model2stmts(_model)
      val data        = untupled(dataRows)
      try {
        // Separate each row so that we can distinguish each insert row
        Future.sequence(data.map(row => transformer.insertStmts(Iterable(row)))).map { stmtss =>
          conn.inspect("output.Molecule._inspectInsert", 1)(
            1, _model, transformer.genericStmts, dataRows, data, stmtss)
        }
      } catch {
        case NonFatal(exc) =>
          println("@@@@@@@@@@@@@@@@@  Error - data processed so far:  @@@@@@@@@@@@@@@@@\n")
          conn.inspect("output.Molecule._inspectInsert", 1)(1, _model, transformer.genericStmts, dataRows, data)
          Future.failed(exc)
      }
    }
  }


  /** Inspect call to `update` on a molecule (without affecting the db).
    * <br><br>
    * Prints internal molecule transformation representations to output:
    * <br><br>
    * Model --> Generic statements --> Datomic statements
    *
    * @group inspectOp
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return
    */
  def inspectUpdate(implicit conn: Future[Conn]): Future[Unit] = conn.flatMap { conn =>
    val transformer = conn.model2stmts(_model)
    try {
      VerifyModel(_model, "update")
      transformer.updateStmts.map(stmts =>
        conn.inspect("output.Molecule.inspectUpdate", 1)(1, _model, transformer.genericStmts, stmts)
      )
    } catch {
      case NonFatal(exc) =>
        println("@@@@@@@@@@@@@@@@@  Error - data processed so far:  @@@@@@@@@@@@@@@@@\n")
        conn.inspect("output.Molecule.inspectUpdate", 1)(1, _model, transformer.genericStmts)
        Future.failed(exc)
    }
  }
}