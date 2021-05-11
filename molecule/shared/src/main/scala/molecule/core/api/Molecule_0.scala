package molecule.core.api

import molecule.core.api.getAsyncObj.{GetAsyncObjArray, GetAsyncObjIterable, GetAsyncObjList}
import molecule.core.api.getAsyncTpl.{GetAsyncTplArray, GetAsyncTplIterable, GetAsyncTplList}
import molecule.core.api.getObj.{GetObjArray, GetObjIterable, GetObjList}
import molecule.core.api.getTpl.{GetTplArray, GetTplIterable, GetTplList}
import molecule.core.ast.Molecule
import molecule.core.ast.elements._
import molecule.core.macros.MakeMoleculeDynamic
import molecule.core.macros.cast.CastHelpersTypes
import molecule.core.marshalling.{Marshalling, Stmts2Edn}
import molecule.core.ops.VerifyModel
import molecule.core.transform.{DynamicMolecule, ModelTransformerAsync}
import molecule.datomic.base.api.ShowInspect
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.experimental.macros
import scala.concurrent.duration.DurationInt


/** Core molecule interface defining actions that can be called on molecules.
  *
  * Groups of interfaces:
  * <table>
  * <tr>
  * <td><b>get</b><td>
  * <td><b>getAsync</b><td>
  * <td>Get molecule data.</td>
  * </tr>
  * <tr>
  * <td><b>getAsOf</b><td>
  * <td><b>getAsyncAsOf</b><td>
  * <td>Get molecule data <i>asOf</i> point in time.</td>
  * </tr>
  * <tr>
  * <td><b>getSince</b><td>
  * <td><b>getAsyncSince</b><td>
  * <td>Get molecule data <i>since</i> point in time.</td>
  * </tr>
  * <tr>
  * <td><b>getWith</b><td>
  * <td><b>getAsyncWith</b><td>
  * <td>Get molecule data <i>with</i> given data set.</td>
  * </tr>
  * <tr>
  * <td><b>getHistory</b><td>
  * <td><b>getAsyncHistory</b> &nbsp;&nbsp;&nbsp;<td>
  * <td>Get molecule data from <i>history</i> of database.</td>
  * </tr>
  * <tr>
  * <td><b>save</b><td>
  * <td><b>saveAsync</b><td>
  * <td>Save molecule with applied data.</td>
  * </tr>
  * <tr>
  * <td><b>insert</b><td>
  * <td><b>insertAsync</b><td>
  * <td>Insert multiple rows of data matching molecule.</td>
  * </tr>
  * <tr>
  * <td><b>update</b><td>
  * <td><b>updateAsync</b><td>
  * <td>Update molecule with applied data.</td>
  * </tr>
  * <tr>
  * <tr>
  * <td><b>tx</b><td>
  * <td><td>
  * <td>Molecule transaction data (input to `getWith`).</td>
  * </tr>
  * <tr>
  * <td><b>inspect get</b><td>
  * <td><td>
  * <td>Inspect calling get method on molecule.</td>
  * </tr>
  * <tr>
  * <td><b>inspect operation</b> &nbsp;&nbsp;&nbsp;<td>
  * <td><td>
  * <td>Inspect calling save/insert/update method on molecule.</td>
  * </tr>
  * </table>
  *
  * @tparam Tpl Type of molecule (tuple of its attribute types)
  * @see For retract ("delete") methods, see molecule.datomic.base.api.EntityOps and molecule.datomic.base.api.DatomicEntity.
  * @groupname get
  * @groupprio get 10
  * @groupname getAsync
  * @groupprio getAsync 11
  * @groupname getAsOf
  * @groupprio getAsOf 110
  * @groupname getArrayAsOf
  * @groupprio getArrayAsOf 120
  * @groupname getIterableAsOf
  * @groupprio getIterableAsOf 130
  * @groupname getRawAsOf
  * @groupprio getRawAsOf 150
  * @groupname getAsyncAsOf
  * @groupprio getAsyncAsOf 111
  * @groupname getAsyncArrayAsOf
  * @groupprio getAsyncArrayAsOf 121
  * @groupname getAsyncIterableAsOf
  * @groupprio getAsyncIterableAsOf 131
  * @groupname getAsyncRawAsOf
  * @groupprio getAsyncRawAsOf 151
  * @groupname getSince
  * @groupprio getSince 210
  * @groupname getArraySince
  * @groupprio getArraySince 220
  * @groupname getIterableSince
  * @groupprio getIterableSince 230
  * @groupname getRawSince
  * @groupprio getRawSince 250
  * @groupname getAsyncSince
  * @groupprio getAsyncSince 211
  * @groupname getAsyncArraySince
  * @groupprio getAsyncArraySince 221
  * @groupname getAsyncIterableSince
  * @groupprio getAsyncIterableSince 231
  * @groupname getAsyncRawSince
  * @groupprio getAsyncRawSince 251
  * @groupname getWith
  * @groupprio getWith 310
  * @groupname getArrayWith
  * @groupprio getArrayWith 320
  * @groupname getIterableWith
  * @groupprio getIterableWith 330
  * @groupname getRawWith
  * @groupprio getRawWith 350
  * @groupname getAsyncWith
  * @groupprio getAsyncWith 311
  * @groupname getAsyncArrayWith
  * @groupprio getAsyncArrayWith 321
  * @groupname getAsyncIterableWith
  * @groupprio getAsyncIterableWith 331
  * @groupname getAsyncRawWith
  * @groupprio getAsyncRawWith 351
  * @groupname getHistory
  * @groupdesc getHistory (only implemented to return List of tuples)
  * @groupprio getHistory 410
  * @groupname save save
  * @groupprio save 510
  * @groupname insert insert
  * @groupprio insert 520
  * @groupname update update
  * @groupprio update 530
  * @groupname getTx Transaction data (input to getWith).
  * @groupprio getTx 610
  * @groupname inspectGet Inspect get
  * @groupdesc inspectGet Molecule getter inspecting methods.
  * @groupprio inspectGet 620
  * @groupname inspectOp Inspect operation
  * @groupdesc inspectOp Molecule operation inspecting methods (no effect on live db).
  * @groupprio inspectOp 630
  * @groupname internal Internal (but public) model/query representations
  * @groupprio internal 710
  * */
trait Molecule_0[Obj, Tpl] extends Molecule
  with Marshalling[Obj, Tpl]
  with CastHelpersTypes
  with GetTplArray[Obj, Tpl]
  with GetTplIterable[Obj, Tpl]
  with GetTplList[Obj, Tpl]
  with GetObjArray[Obj, Tpl]
  with GetObjIterable[Obj, Tpl]
  with GetObjList[Obj, Tpl]
  with GetAsyncTplArray[Obj, Tpl]
  with GetAsyncTplIterable[Obj, Tpl]
  with GetAsyncTplList[Obj, Tpl]
  with GetAsyncObjArray[Obj, Tpl]
  with GetAsyncObjIterable[Obj, Tpl]
  with GetAsyncObjList[Obj, Tpl]
  with GetRaw
  with GetAsyncRaw
  with ShowInspect[Obj, Tpl] {


  // Dynamic molecule ==========================================================

  def apply(body: Obj => Unit): DynamicMolecule with Obj = macro MakeMoleculeDynamic.apply[Obj]


  // Save ======================================================================

  /** Save data applied to molecule attributes.
    * <br><br>
    * Returns [[molecule.datomic.base.facade.TxReport TxReport]] having info about
    * the result of the save transaction.
    * {{{
    *   val txReport = Person.name("Ben").age(42).save
    *
    *   // Data has been saved in db
    *   Person.name.age.get === List(("Ben", 42))
    * }}}
    * The save operation is synchronous and blocking. Use `saveAsync` for non-blocking asynchronous saves.
    *
    * @group save
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return [[molecule.datomic.base.facade.TxReport TxReport]] with info about the result of the `save` transaction.
    */
  def save(implicit conn: Conn): TxReport = {
    VerifyModel(_model, "save")
//    conn.tempId.reset()
    val stmts = conn.modelTransformer(_model).saveStmts
    if (false) {
//      conn.tempId.reset()
      val asyncStmts = Await.result(ModelTransformerAsync(conn, _model).saveStmts, 10.seconds)
      if (asyncStmts != stmts) {
        println("@@@@@ Save @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        println(_model)
        println(stmts.mkString("\n"))
        println("----------")
        println(asyncStmts.mkString("\n"))
        println("-----------------------------------")
      }
      //    assert(asyncStmts == stmts)
    }

    conn.transact(stmts)
    //    conn.transact(conn.modelTransformer(_model).saveStmts)
  }


  /** Asynchronously save data applied to molecule attributes.
    * <br><br>
    * Returns `Future` with [[molecule.datomic.base.facade.TxReport TxReport]] having info about
    * the result of the save transaction.
    * {{{
    *   val futureSave: Future[Either[String, TxReport]] = Person.name("Ben").age(42).saveAsync
    *
    *   for {
    *     _ <- futureSave
    *     result <- Person.name.age.getAsync
    *   } yield {
    *     // Data was saved
    *     result.head === ("Ben", 42)
    *   }
    * }}}
    * The save operation is asynchronous and non-blocking. Internally calls Datomic's asynchronous API.
    *
    * @group save
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return [[molecule.datomic.base.facade.TxReport TxReport]] with info about the result of the `save` transaction.
    */
  def saveAsync(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = {
    VerifyModel(_model, "save")
    if (isJsPlatform) {
      for {
        saveStmts <- conn.modelTransformerAsync(_model).saveStmts
        (stmtsEdn, uriAttrs) = Stmts2Edn(saveStmts)
        result <- moleculeRpc.transactAsync(conn.dbProxy, stmtsEdn, uriAttrs)
      } yield result
    } else {
      conn.transactAsync(conn.modelTransformer(_model).saveStmts)
    }
  }


  /** Get transaction statements of a call to `save` on a molecule (without affecting the db).
    *
    * @group getTx
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return Transaction statements
    */
  def getSaveStmts(implicit conn: Conn): Seq[Statement] = {
    VerifyModel(_model, "save")
    val transformer = conn.modelTransformer(_model)
    val stmts       = try {
      transformer.saveStmts
    } catch {
      case e: Throwable =>
        println("@@@@@@@@@@@@@@@@@  Error - data processed so far:  @@@@@@@@@@@@@@@@@\n")
        conn.inspect("output.Molecule.getSaveTx", 1)(1, _model, transformer.genericStmts)
        throw e
    }
    stmts
  }


  // Insert ====================================================================

  /** Insert one or more rows of data matching molecule.
    * <br><br>
    * Returns `Future` with [[molecule.datomic.base.facade.TxReport TxReport]] having info about
    * the result of the insert transaction.
    * <br><br>
    * Data matching the types of the molecule can be inserted either as individual args
    * or an Iterable (List, Set etc) of tuples:
    * {{{
    *   // Insert single row of data with individual args
    *   Person.name.age.insert("Ann", 28)
    *
    *   // Insert multiple rows of data. Accepts Iterable[Tpl]
    *   Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    *
    *   // Data was inserted
    *   Person.name.age.get.sorted === List(
    *     ("Ann", 28),
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    * }}}
    * Each insert apply method returns a [[molecule.datomic.base.facade.TxReport TxReport]] with info about the result of the transaction.
    * <br><br>
    * Since `insert` is an object of each arity 1-22 Molecule implementation, we can make an "insert-molecule" at compile time
    * that we can re-use for inserting data at runtime matching the molecule type:
    * {{{
    *   // At compiletime:
    *
    *   // Make insert-molecule
    *   val insertPersonsWithAge = Person.name.age.insert
    *
    *   // At runtime:
    *
    *   // Apply individual args matching insert-molecule to insert single row
    *   insertPersonsWithAge("Ann", 28)
    *
    *   // .. or apply Iterable of tuples matching insert-molecule to insert multiple rows
    *   insertPersonsWithAge(
    *     List(
    *       ("Ben", 42),
    *       ("Liz", 37)
    *     )
    *   )
    *
    *   // Data was inserted
    *   Person.name.age.get.sorted === List(
    *     ("Ann", 28),
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    * }}}
    * The insert operation is synchronous and blocking. Use `insertAsync` for non-blocking asynchronous inserts.
    *
    * @group insert
    */
  trait insert


  /** Asynchronously insert one or more rows of data matching molecule.
    * <br><br>
    * Returns `Future` with [[molecule.datomic.base.facade.TxReport TxReport]] having info about
    * the result of the insert transaction.
    * <br><br>
    * Data matching the types of the molecule can be inserted either as individual args
    * or an Iterable (List, Set etc) of tuples:
    * {{{
    *   // Insert single row of data with individual args
    *   val singleInsertFuture: Future[Either[String, TxReport]] = Person.name.age.insertAsync("Ann", 28)
    *
    *   // Insert multiple rows of data. Accepts Iterable[Tpl]
    *   val multipleInsertFuture: Future[Either[String, TxReport]] = Person.name.age insertAsync List(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    *
    *   for {
    *     _ <- singleInsertFuture
    *     _ <- multipleInsertFuture
    *     result <- Person.name.age.getAsync
    *   } yield {
    *     // Both inserts applied
    *     result === List(
    *       ("Ann", 28),
    *       ("Ben", 42),
    *       ("Liz", 37)
    *     )
    *   }
    * }}}
    *
    * The insert operation is asynchronous and non-blocking. Internally calls Datomic's asynchronous API.
    *
    * @group insert
    */
  trait insertAsync


  /** Get transaction statements of a call to `insert` on a molecule (without affecting the db).
    *
    * @group getTx
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


  /** Insert data is verified on instantiation of `insert` object in each arity molecule */
  protected trait checkInsertModel {
    VerifyModel(_model, "insert")
  }

  protected def untupled(rawData: Iterable[Seq[Any]]): Iterable[Seq[Any]] = {
    if (this.toString.contains("compositOutMolecule")) {
      rawData.map(_ flatMap tupleToSeq)
    } else {
      rawData
    }
  }


  protected def _insert(conn: Conn, model: Model, dataRows: Iterable[Seq[Any]]): TxReport = {
//    conn.tempId.reset()
    val stmts = conn.modelTransformer(model).insertStmts(untupled(dataRows))

//    println("_insert:")
//    stmts foreach println

    if (false) {

//      conn.tempId.reset()
      val asyncStmts = Await.result(ModelTransformerAsync(conn, model).insertStmts(untupled(dataRows)), 10.seconds)
      if (asyncStmts != stmts) {
        println("@@@@@ Insert @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        println(model)
        println(stmts.mkString("\n"))
        println("----------")
        println(asyncStmts.mkString("\n"))
        println("-----------------------------------")
      }
      //    assert(asyncStmts == stmts)
    }
    conn.transact(stmts)
    //    conn.transact(conn.modelTransformer(model).insertStmts(untupled(dataRows)))
  }

  protected def _insertAsync(
    conn: Conn,
    model: Model,
    dataRows: Iterable[Seq[Any]]
  )(implicit ec: ExecutionContext): Future[Either[String, TxReport]] = {
    if (isJsPlatform) {
      for {
        insertStmts <- conn.modelTransformerAsync(model).insertStmts(untupled(dataRows))
        (stmtsEdn, uriAttrs) = Stmts2Edn(insertStmts)
        result <- moleculeRpc.transactAsync(conn.dbProxy, stmtsEdn, uriAttrs)
      } yield result
    } else {
      //      conn.transactAsync(conn.modelTransformerAsync(model).insertStmts(untupled(dataRows)))
      for {
        updateStmts <- conn.modelTransformerAsync(model).insertStmts(untupled(dataRows))
        result <- conn.transactAsync(updateStmts)
      } yield result
    }
  }

  protected def _getInsertStmts(conn: Conn, dataRows: Iterable[Seq[Any]]): Seq[Statement] = {
    val transformer = conn.modelTransformer(_model)
    try {
      transformer.insertStmts(untupled(dataRows))
    } catch {
      case e: Throwable =>
        println("@@@@@@@@@@@@@@@@@  Error - data processed so far:  @@@@@@@@@@@@@@@@@\n")
        conn.inspect(
          "output.Molecule._inspectInsert", 1)(
          1, _model, transformer.genericStmts, dataRows, untupled(dataRows))
        throw e
    }
  }

  // Update ====================================================================

  /** Update entity with data applied to molecule attributes.
    * <br><br>
    * Returns [[molecule.datomic.base.facade.TxReport TxReport]] with info about the result of the update transaction.
    * {{{
    *   // Current data
    *   val ben = Person.name("Ben").age(42).save.eid
    *
    *   // Update entity of of Ben with new age value
    *   Person(ben).age(43).update
    *
    *   // Ben is now 43
    *   Person.name.age.get === List(("ben", 43))
    * }}}
    * The update operation is synchronous and blocking. Use `updateAsync` for non-blocking asynchronous updates.
    *
    * @group update
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return [[molecule.datomic.base.facade.TxReport TxReport]]
    */
  def update(implicit conn: Conn): TxReport = {
    VerifyModel(_model, "update")
//    conn.tempId.reset()
    val stmts = conn.modelTransformer(_model).updateStmts
    if (false) {

//      conn.tempId.reset()
      val asyncStmts = Await.result(ModelTransformerAsync(conn, _model).updateStmts, 10.seconds)

      if (asyncStmts != stmts) {
        println("@@@@@ Update @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        println(_model)
        println(stmts.mkString("\n"))
        println("----------")
        println(asyncStmts.mkString("\n"))
        println("-----------------------------------")
        //      throw new RuntimeException("damn")
      }
      //    assert(asyncStmts == stmts)
    }

    conn.transact(stmts)
    //    conn.transact(conn.modelTransformer(_model).updateStmts)
  }

  /** Asynchronously update entity with data applied to molecule attributes.
    * Returns `Future` with [[molecule.datomic.base.facade.TxReport TxReport]] having info about
    * the result of the update transaction.
    * {{{
    *   for {
    *     saveTx <- Person.name("Ben").age(42).saveAsync
    *     benId = saveTx.eid
    *     updateTx <- Person(benId).age(43).updateAsync
    *     result <- Person.name.age.getAsync
    *   } yield {
    *     // Ben is now 43
    *     result.head === ("ben", 43)
    *   }
    * }}}
    * The update operation is asynchronous and non-blocking. Internally calls Datomic's asynchronous API.
    *
    * @group update
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return [[molecule.datomic.base.facade.TxReport TxReport]]
    */
  def updateAsync(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = {
    VerifyModel(_model, "update")
    if (isJsPlatform) {
      for {
        updateStmts <- conn.modelTransformerAsync(_model).updateStmts
        (stmtsEdn, uriAttrs) = Stmts2Edn(updateStmts)
        result <- moleculeRpc.transactAsync(conn.dbProxy, stmtsEdn, uriAttrs)
      } yield result
    } else {
      //      conn.transactAsync(conn.modelTransformer(_model).updateStmts)
      for {
        updateStmts <- conn.modelTransformerAsync(_model).updateStmts
        result <- conn.transactAsync(updateStmts)
      } yield result
    }
  }


  /** Get transaction statements of a call to `update` on a molecule (without affecting the db).
    *
    * @group getTx
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return
    */
  def getUpdateStmts(implicit conn: Conn): Seq[Statement] = {
    VerifyModel(_model, "update")
    val transformer = conn.modelTransformer(_model)
    try {
      transformer.updateStmts
    } catch {
      case e: Throwable =>
        println("@@@@@@@@@@@@@@@@@  Error - data processed so far:  @@@@@@@@@@@@@@@@@\n")
        conn.inspect("output.Molecule.inspectUpdate", 1)(1, _model, transformer.genericStmts)
        throw e
    }
  }
}


/** Arity 1-22 molecule implementation interfaces. */
object Molecule_0 {

  abstract class Molecule_0_01[Obj, A](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, A] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, ax: A*)(implicit conn: Conn): TxReport = _insert(conn, _model, (a +: ax.toList).map(Seq(_)))
      def apply(data: Iterable[A])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(Seq(_)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, ax: A*)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, (a +: ax.toList).map(Seq(_)))
      def apply(data: Iterable[A])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(Seq(_)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, ax: A*)(implicit conn: Conn): Unit = _inspectInsert(conn, (a +: ax.toList).map(Seq(_)))
      def apply(data: Iterable[A])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(Seq(_)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, ax: A*)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, (a +: ax.toList).map(Seq(_)))
      def apply(data: Iterable[A])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(Seq(_)))
    }
  }

  abstract class Molecule_0_02[Obj, A, B](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b)))
      def apply(data: Iterable[(A, B)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b)))
      def apply(data: Iterable[(A, B)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b)))
      def apply(data: Iterable[(A, B)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b)))
      def apply(data: Iterable[(A, B)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2)))
    }
  }


  abstract class Molecule_0_03[Obj, A, B, C](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c)))
      def apply(data: Iterable[(A, B, C)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c)))
      def apply(data: Iterable[(A, B, C)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c)))
      def apply(data: Iterable[(A, B, C)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c)))
      def apply(data: Iterable[(A, B, C)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3)))
    }
  }


  abstract class Molecule_0_04[Obj, A, B, C, D](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d)))
      def apply(data: Iterable[(A, B, C, D)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d)))
      def apply(data: Iterable[(A, B, C, D)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d)))
      def apply(data: Iterable[(A, B, C, D)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d)))
      def apply(data: Iterable[(A, B, C, D)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    }
  }


  abstract class Molecule_0_05[Obj, A, B, C, D, E](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e)))
      def apply(data: Iterable[(A, B, C, D, E)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e)))
      def apply(data: Iterable[(A, B, C, D, E)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e)))
      def apply(data: Iterable[(A, B, C, D, E)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e)))
      def apply(data: Iterable[(A, B, C, D, E)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    }
  }


  abstract class Molecule_0_06[Obj, A, B, C, D, E, F](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f)))
      def apply(data: Iterable[(A, B, C, D, E, F)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f)))
      def apply(data: Iterable[(A, B, C, D, E, F)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f)))
      def apply(data: Iterable[(A, B, C, D, E, F)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f)))
      def apply(data: Iterable[(A, B, C, D, E, F)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    }
  }

  abstract class Molecule_0_07[Obj, A, B, C, D, E, F, G](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g)))
      def apply(data: Iterable[(A, B, C, D, E, F, G)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g)))
      def apply(data: Iterable[(A, B, C, D, E, F, G)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g)))
      def apply(data: Iterable[(A, B, C, D, E, F, G)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g)))
      def apply(data: Iterable[(A, B, C, D, E, F, G)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    }
  }


  abstract class Molecule_0_08[Obj, A, B, C, D, E, F, G, H](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    }
  }


  abstract class Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    }
  }


  abstract class Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    }
  }


  abstract class Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    }
  }


  abstract class Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    }
  }

  abstract class Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    }
  }


  abstract class Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    }
  }


  abstract class Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    }
  }


  abstract class Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    }
  }


  abstract class Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    }
  }


  abstract class Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    }
  }


  abstract class Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    }
  }


  abstract class Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    }
  }


  abstract class Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    }
  }


  abstract class Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](val _model: Model, queryData: (Query, Option[Query], Query, Option[Query])) extends Molecule_0[Obj, (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] {
    val (_query, _nestedQuery, _rawQuery, _rawNestedQuery) = queryData

    object insert extends insert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Conn): TxReport = _insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)])(implicit conn: Conn): TxReport = _insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    }

    object insertAsync extends insertAsync with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)])(implicit conn: Conn, ec: ExecutionContext): Future[Either[String, TxReport]] = _insertAsync(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    }

    object inspectInsert extends inspectInsert with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Conn): Unit = _inspectInsert(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)])(implicit conn: Conn): Unit = _inspectInsert(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    }

    object getInsertStmts extends getInsertStmts with checkInsertModel {
      def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
      def apply(data: Iterable[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)])(implicit conn: Conn): Seq[Statement] = _getInsertStmts(conn, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    }
  }
}