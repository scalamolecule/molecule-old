package molecule.core.api

import molecule.core.ast.elements._
import molecule.core.macros.MakeMoleculeDynamic
import molecule.core.macros.rowAttr.{CastAggr, CastOptNested, CastTypes, JsonAggr, JsonOptNested, JsonTypes}
import molecule.core.marshalling.Marshalling
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.marshalling.unpackAttr.{String2cast, String2json}
import molecule.core.ops.VerifyModel
import molecule.core.transform.DynamicMolecule
import molecule.core.util.Helpers
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.{Add, Statement, TempId}
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.base.util.ShowInspect
import scala.concurrent.{ExecutionContext, Future}
import scala.language.experimental.macros
import scala.util.control.NonFatal

/** Core molecule interface defining actions that can be called on molecules.
 *
 * Groups of interfaces:
 * <table>
 * <tr>
 * <td><b>get</b><td>
 * <td>Get molecule data.</td>
 * </tr>
 * <tr>
 * <td><b>getAsOf</b><td>
 * <td>Get molecule data <i>asOf</i> point in time.</td>
 * </tr>
 * <tr>
 * <td><b>getSince</b><td>
 * <td>Get molecule data <i>since</i> point in time.</td>
 * </tr>
 * <tr>
 * <td><b>getWith</b><td>
 * <td>Get molecule data <i>with</i> given data set.</td>
 * </tr>
 * <tr>
 * <td><b>getHistory</b><td>
 * <td>Get molecule data from <i>history</i> of database.</td>
 * </tr>
 * <tr>
 * <td><b>save</b><td>
 * <td>Save molecule with applied data.</td>
 * </tr>
 * <tr>
 * <td><b>insert</b><td>
 * <td>Insert multiple rows of data matching a molecule.</td>
 * </tr>
 * <tr>
 * <td><b>update</b><td>
 * <td>Update molecule with applied data.</td>
 * </tr>
 * <tr>
 * <tr>
 * <td><b>tx</b><td>
 * <td>Molecule transaction data (input to `getWith`).</td>
 * </tr>
 * <tr>
 * <td><b>inspect get</b><td>
 * <td>Inspect calling get method on molecule.</td>
 * </tr>
 * <tr>
 * <td><b>inspect operation</b> &nbsp;&nbsp;&nbsp;<td>
 * <td>Inspect calling save/insert/update method on molecule.</td>
 * </tr>
 * </table>
 *
 * @tparam Tpl Type of molecule (tuple of its attribute types)
 * @see For retract ("delete") methods, see molecule.datomic.base.api.EntityOps and molecule.datomic.base.api.DatomicEntity.
 * @groupname get
 * @groupprio get 10
 * @groupname getAsOf
 * @groupprio getAsOf 110
 * @groupname getSince
 * @groupprio getSince 210
 * @groupname getWith
 * @groupprio getWith 310
 * @groupname getHistory
 * @groupprio getHistory 410
 * @groupname save save
 * @groupprio save 510
 * @groupname insert insert
 * @groupprio insert 520
 * @groupname update update
 * @groupprio update 530
 * @groupname getStmts Transaction data (input to getWith).
 * @groupprio getStmts 610
 * @groupname inspectGet Inspect get
 * @groupdesc inspectGet Molecule getter inspecting methods.
 * @groupprio inspectGet 620
 * @groupname inspectOp Inspect operation
 * @groupdesc inspectOp Molecule operation inspecting methods (no effect on live db).
 * @groupprio inspectOp 630
 * @groupname internal Internal (but public) model/query representations
 * @groupprio internal 710
 * */
abstract class Molecule_0[Obj, Tpl](model: Model, queryData: (Query, String, Option[Throwable]))
  extends Marshalling[Obj, Tpl](model, queryData)
    with CastTypes
    with CastAggr
    with CastOptNested

    with JsonTypes
    with JsonAggr
    with JsonOptNested

    with String2cast
    with String2json

    with GetTpls[Obj, Tpl]
    with GetObjs[Obj, Tpl]
    with GetJson[Obj, Tpl]
    with ShowInspect[Obj, Tpl]
    with Helpers {


  // Dynamic molecule ==========================================================

  def apply(body: Obj => Unit): Future[List[DynamicMolecule with Obj]] = macro MakeMoleculeDynamic.apply[Obj]
  def getDynObjs(body: Obj => Unit): Future[List[DynamicMolecule with Obj]] = macro MakeMoleculeDynamic.apply[Obj]


  // Save ======================================================================

  /** Save data applied to molecule attributes.
   * <br><br>
   * Returns `Future` with [[molecule.datomic.base.facade.TxReport TxReport]] having info about
   * the result of the save transaction.
   * {{{
   * for {
   *   _ <- Person.name("Ben").age(42).save
   *   _ <- Person.name.age.get.map(_.head ==> ("Ben", 42))
   * } yield ()
   * }}}
   * The save operation is asynchronous and non-blocking. Internally calls Datomic's asynchronous API.
   *
   * @group save
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return Future with [[molecule.datomic.base.facade.TxReport TxReport]] with info about the result of the `save` transaction.
   */
  def save(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = try {
    VerifyModel(_model, "save")
    val res = conn.flatMap { conn =>
      if (conn.isJsPlatform) {
        for {
          saveStmts <- conn.model2stmts(_model).saveStmts
          result <- {
            //            println(_model)
            //            saveStmts foreach println

            conn.rpc.transact(conn.connProxy, Stmts2Edn(saveStmts, conn))
          }
        } yield result
      } else {
        conn.transact(
          conn.model2stmts(_model).saveStmts
        )
      }
    }
    //    res.foreach(r => println("@@@@@@@@@@@@ SAVE  " + r))
    res
  } catch {
    // Catch failed model verification
    case NonFatal(exc) => Future.failed(exc)
  }

  /** Get transaction statements of a call to `save` on a molecule (without affecting the db).
   *
   * @group getStmts
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return Transaction statements
   */
  def getSaveStmts(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = try {
    VerifyModel(_model, "save")
    conn.flatMap { conn =>
      conn.model2stmts(_model).saveStmts
    }
  } catch {
    // Catch failed model verification
    case NonFatal(exc) => Future.failed(exc)
  }


  // Insert ====================================================================


  /** Insert one or more rows of data matching a molecule.
   * <br><br>
   * Returns `Future` with [[molecule.datomic.base.facade.TxReport TxReport]] having info about
   * the result of the insert transaction.
   * <br><br>
   * Data matching the types of the molecule can be inserted either as individual args
   * or an Iterable (List, Set etc) of tuples:
   * {{{
   * val singleInsertFuture: Future[TxReport] = Person.name.age.insert("Ann", 28)
   *
   * // Insert multiple rows of data. Accepts Iterable[Tpl]
   * val multipleInsertFuture: Future[TxReport] = Person.name.age insert List(
   *   ("Ben", 42),
   *   ("Liz", 37)
   * )
   *
   * for {
   *   // Insert single row of data with individual args
   *   _ <- Person.name.age.insert("Ann", 28)
   *
   *   // Insert multiple rows of data. Accepts Iterable[Tpl]
   *   _ <- Person.name.age insert List(
   *     ("Ben", 42),
   *     ("Liz", 37)
   *   )
   *   _ <- Person.name.age.get.map(_ ==> List(
   *     ("Ann", 28),
   *     ("Ben", 42),
   *     ("Liz", 37)
   *   ))
   * } yield ()
   * }}}
   *
   * The insert operation is asynchronous and non-blocking. Internally calls Datomic's asynchronous API.
   *
   * @group insert
   */
  trait insert


  /** Get transaction statements of a call to `insert` on a molecule (without affecting the db).
   *
   * @group getStmts
   * @return Transaction statements
   */
  trait getInsertStmts


  /** Inspect call to `insert` on a molecule (without affecting the db).
   * <br><br>
   * Prints internal molecule transformation representations to output:
   * <br><br>
   * Model --> Generic statements --> Datomic statements
   *
   * @group inspectOp
   */
  trait inspectInsert


  protected def _insert(conn: Future[Conn], dataRows: Iterable[Seq[Any]])
                       (implicit ec: ExecutionContext): Future[TxReport] = try {
    VerifyModel(_model, "insert")
    conn.flatMap { conn =>
      if (conn.isJsPlatform) {
        for {
          insertStmts <- conn.model2stmts(_model).insertStmts(untupled(dataRows))
          (stmtsEdn, uriAttrs) = Stmts2Edn(insertStmts, conn)
          result <- conn.rpc.transact(conn.connProxy, stmtsEdn, uriAttrs)
        } yield result
      } else {
        conn.transact(
          conn.model2stmts(_model).insertStmts(untupled(dataRows))
        )
      }
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  protected def _getInsertStmts(
    conn: Future[Conn],
    dataRows: Iterable[Seq[Any]]
  )(implicit ec: ExecutionContext): Future[Seq[Statement]] = try {
    VerifyModel(_model, "insert")
    conn.flatMap { conn =>
      conn.model2stmts(_model).insertStmts(untupled(dataRows))
    }
  } catch {
    // Catch failed model verification
    case NonFatal(exc) => Future.failed(exc)
  }


  // Update ====================================================================

  /** Update entity with data applied to molecule attributes.
   * Returns `Future` with [[molecule.datomic.base.facade.TxReport TxReport]] having info about
   * the result of the update transaction.
   * {{{
   *
   * for {
   *   _ <- Person.name("Ben").age(42).save
   *   benId = saveStmts.map(_.eid)
   *   _ <- Person(benId).age(43).update
   *   _ <- Person.name.age.get.map(_.head ==> ("Ben", 43))
   * } yield ()
   * }}}
   * The update operation is asynchronous and non-blocking. Internally calls Datomic's asynchronous API.
   *
   * @group update
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return [[molecule.datomic.base.facade.TxReport TxReport]]
   */
  def update(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = try {
    VerifyModel(_model, "update")
    conn.flatMap { conn =>
      if (conn.isJsPlatform) {
        for {
          updateStmts <- conn.model2stmts(_model).updateStmts
          result <- conn.rpc.transact(conn.connProxy, Stmts2Edn(updateStmts, conn))
        } yield result
      } else {
        conn.transact(
          try {
            conn.model2stmts(_model).updateStmts
          } catch {
            case NonFatal(exc) => Future.failed(exc)
          }
        )
      }
    }
  } catch {
    // Catch failed model verification
    case NonFatal(exc) => Future.failed(exc)
  }


  /** Get transaction statements of a call to `update` on a molecule (without affecting the db).
   *
   * @group getStmts
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return
   */
  def getUpdateStmts(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = try {
    VerifyModel(_model, "update")
    conn.flatMap { conn =>
      conn.model2stmts(_model).updateStmts
    }
  } catch {
    // Catch failed model verification
    case NonFatal(exc) => Future.failed(exc)
  }
}


/** Arity 1-22 molecule implementation interfaces. */
object Molecule_0 {

  abstract class Molecule_0_01[Obj, A](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, A](model, queryData) {
    object insert extends insert {
      def apply(a: A, ax: A*)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, (a +: ax.toList).map(Seq(_)))
      def apply(data: Iterable[A])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(Seq(_)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, ax: A*)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, (a +: ax.toList).map(Seq(_)))
      def apply(data: Iterable[A])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(Seq(_)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, ax: A*)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, (a +: ax.toList).map(Seq(_)))
      def apply(data: Iterable[A])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(Seq(_)))
    }
  }

  abstract class Molecule_0_02[Obj, A, B](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b)))
      def apply(data: Iterable[(A, B)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b)))
      def apply(data: Iterable[(A, B)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b)))
      def apply(data: Iterable[(A, B)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2)))
    }
  }


  abstract class Molecule_0_03[Obj, A, B, C](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c)))
      def apply(data: Iterable[(A, B, C)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c)))
      def apply(data: Iterable[(A, B, C)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c)))
      def apply(data: Iterable[(A, B, C)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3)))
    }
  }


  abstract class Molecule_0_04[Obj, A, B, C, D](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d)))
      def apply(data: Iterable[(A, B, C, D)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d)))
      def apply(data: Iterable[(A, B, C, D)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d)))
      def apply(data: Iterable[(A, B, C, D)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    }
  }


  abstract class Molecule_0_05[Obj, A, B, C, D, E](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e)))
      def apply(data: Iterable[(A, B, C, D, E)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e)))
      def apply(data: Iterable[(A, B, C, D, E)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e)))
      def apply(data: Iterable[(A, B, C, D, E)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    }
  }


  abstract class Molecule_0_06[Obj, A, B, C, D, E, F](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f)))
      def apply(data: Iterable[(A, B, C, D, E, F)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f)))
      def apply(data: Iterable[(A, B, C, D, E, F)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f)))
      def apply(data: Iterable[(A, B, C, D, E, F)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    }
  }

  abstract class Molecule_0_07[Obj, A, B, C, D, E, F, G](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g)))
      def apply(data: Iterable[(A, B, C, D, E, F, G)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g)))
      def apply(data: Iterable[(A, B, C, D, E, F, G)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g)))
      def apply(data: Iterable[(A, B, C, D, E, F, G)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    }
  }


  abstract class Molecule_0_08[Obj, A, B, C, D, E, F, G, H](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    }
  }


  abstract class Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    }
  }


  abstract class Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    }
  }


  abstract class Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    }
  }


  abstract class Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    }
  }

  abstract class Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    }
  }


  abstract class Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    }
  }


  abstract class Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    }
  }


  abstract class Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    }
  }


  abstract class Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    }
  }


  abstract class Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    }
  }


  abstract class Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    }
  }


  abstract class Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    }
  }


  abstract class Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    }
  }


  abstract class Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)](model, queryData) {
    object insert extends insert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = _insert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    }

    object inspectInsert extends inspectInsert {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)])(implicit conn: Future[Conn]): Future[Unit] = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    }

    object getInsertStmts extends getInsertStmts {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)])(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    }
  }
}