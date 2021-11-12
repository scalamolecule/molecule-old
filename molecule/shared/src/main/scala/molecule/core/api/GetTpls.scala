package molecule.core.api

import java.util.Date
import molecule.core.marshalling.Marshalling
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.ops.ColOps
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}


/** Default data getter methods on molecules that return data as lists of tuples. */
trait GetTpls[Obj, Tpl] extends ColOps { self: Marshalling[Obj, Tpl] =>

  // get ================================================================================================

  /** Get Future with List of all rows as tuples matching a molecule.
   * <br><br>
   * {{{
   * Person.name.age.get.map(_ ==> List(
   *   ("Ben", 42),
   *   ("Liz", 37),
   * ))
   * }}}
   * <br><br>
   * Since retrieving a List is considered the default fetch format, the getter method is
   * simply named `get` (and not `getList`).
   *
   * @group get
   * @param futConn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def get(implicit futConn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    _inputThrowable.fold(
      futConn.flatMap { conn =>
        if (conn.isJsPlatform) {

          //          println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
          //          println(_model)
          //          _model.elements.collect {
          //            case Generic(_, _, _, Eq(Seq(v1)))           =>
          //              println(v1)
          //              println(v1.getClass)
          //            case Atom(_, _, _, _, Eq(Seq(v1)), _, _, _)  =>
          //              println(v1)
          //              println(v1.getClass)
          //            case Atom(_, _, _, _, Neq(Seq(v1)), _, _, _) =>
          //              println(v1)
          //              println(v1.getClass)
          //          }
          //          println("----------------")
          //          println(_query)
          //          println("----------------")
          //          println(_datalog)

          conn.jsQueryTpl(
            _model, _query, _datalog, -1, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, packed2tpl
          )
        } else {
          conn.jvmQuery(_model, _query).map { jColl =>
            val it   = jColl.iterator
            val list = List.newBuilder[Tpl]
            while (it.hasNext) {
              list += row2tpl(it.next)
            }
            list.result()
          }
        }
      }
    )(Future.failed) // Pass on exception from input failure
  }

  /** Get Future with List of n rows as tuples matching a molecule.
   * {{{
   * Person.name.age.get(1).map(_ ==> List(
   *   ("Ben", 42)
   * )
   * }}}
   * <br><br>
   * Since retrieving a List is considered the default fetch format, the getter method is
   * simply named `get` (and not `getList`).
   *
   * @group get
   * @param maxRows Int Number of rows returned
   * @param futConn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def get(maxRows: Int)(implicit futConn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    _inputThrowable.fold(
      futConn.flatMap { conn =>
        if (conn.isJsPlatform) {
          conn.jsQueryTpl(
            _model, _query, _datalog, maxRows, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, packed2tpl
          )
        } else {
          if (maxRows == -1) {
            get(futConn, ec)
          } else {
            conn.jvmQuery(_model, _query).map { jColl =>
              val size = jColl.size
              val max  = if (size < maxRows) size else maxRows
              if (max == 0) {
                List.empty[Tpl]
              } else {
                val it   = jColl.iterator
                val list = List.newBuilder[Tpl]
                var i    = 0
                while (it.hasNext && i < max) {
                  list += row2tpl(it.next)
                  i += 1
                }
                list.result()
              }
            }
          }
        }
      }
    )(Future.failed) // Wrap exception from input failure in Future
  }


  // get as of ================================================================================================

  /** Get Future with List of all rows as tuples matching molecule as of transaction time `t`.
   * <br><br>
   * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
   * <br><br>
   * `t` can for instance be retrieved in a getHistory call for an attribute and then be
   * used to get data as of that point in time (including that transaction):
   * {{{
   * for {
   *   // Insert (t 1028)
   *   List(ben, liz) <- Person.name.age insert List(
   *     ("Ben", 42),
   *     ("Liz", 37),
   *   ) eids
   *
   *   // Update (t 1031)
   *   _ <- Person(ben).age(43).update
   *
   *   // Retract (t 1032)
   *   _ <- ben.retract
   *
   *   // History of Ben
   *   _ <- Person(ben).age.t.op.getHistory.map(_.sortBy(r => (r._2, r._3)) ==> List(
   *     (42, 1028, true),  // Insert:  42 asserted
   *     (42, 1031, false), // Update:  42 retracted
   *     (43, 1031, true),  //          43 asserted
   *     (43, 1032, false)  // Retract: 43 retracted
   *   ))
   *
   *   // Get List of all rows as of transaction t 1028 (after insert)
   *   _ <- Person.name.age.getAsOf(1028).map(_ ==> List(
   *     ("Liz", 37),
   *     ("Ben", 42)
   *   ))
   *
   *   // Get List of all rows as of transaction t 1031 (after update)
   *   _ <- Person.name.age.getAsOf(1031).map(_ ==> List(
   *     ("Liz", 37),
   *     ("Ben", 43)
   *   ))
   *
   *   // Get List of all rows as of transaction t 1032 (after retract)
   *   _ <- Person.name.age.getAsOf(1032).map(_ ==> List(
   *     ("Liz", 37)
   *   ))
   * } yield ()
   * }}}
   * <br><br>
   * For more info and code examples see equivalent synchronous
   * [[molecule.core.api.GetTpls.getAsOf(t:Long)* getAsOf]] method.
   *
   * @group getAsOf
   * @param t    Transaction time t
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getAsOf(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingAdhocDbView(AsOf(TxLong(t)))), ec)


  /** Get Future with List of n rows as tuples matching molecule as of transaction time `t`.
   * <br><br>
   * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
   * <br><br>
   * `t` can for instance be retrieved in a getHistory call for an attribute and then be
   * used to get data as of that point in time (including that transaction):
   * {{{
   * for {
   *   // Insert (t 1028)
   *   List(ben, liz) <- Person.name.age insert List(
   *     ("Ben", 42),
   *     ("Liz", 37),
   *   ) eids
   *
   *   // Update (t 1031)
   *   _ <- Person(ben).age(43).update
   *
   *   // History of Ben
   *   _ <- Person(ben).age.t.op.getHistory.map(_.sortBy(r => (r._2, r._3)) ==> List(
   *     (42, 1028, true),  // Insert:  42 asserted
   *     (42, 1031, false), // Update:  42 retracted
   *     (43, 1031, true),  //          43 asserted
   *   ))
   *
   *   // Get List of all all rows as of transaction t 1031 (after update)
   *   _ <- Person.name.age.getAsOf(1031).map(_ ==> List(
   *     ("Ben", 43),
   *     ("Liz", 37)
   *   ))
   *
   *   // Get List of n rows as of transaction t 1031 (after update)
   *   _ <- Person.name.age.getAsOf(1031, 1).map(_ ==> List(
   *     ("Ben", 43)
   *   ))
   * } yield ()
   * }}}
   * <br><br>
   * For more info and code examples see equivalent synchronous
   * [[molecule.core.api.GetTpls.getAsOf(t:Long,n:Int)* getAsOf]] method.
   *
   * @group getAsOf
   * @param t    Long Transaction time t
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getAsOf(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingAdhocDbView(AsOf(TxLong(t)))), ec)


  /** Get Future with List of all rows as tuples matching molecule as of tx.
   * <br><br>
   * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a
   * database value as of that transaction (including).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a
   * [[molecule.datomic.base.facade.TxReport TxReport]]
   * that contains the transaction entity id (which is used as argument to Datomic internally). This is more
   * convenient when using Molecule since we get a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction
   * operations like `get`, `update`, `retract` etc.
   * {{{
   * for {
   *   // Insert (tx report 1)
   *   tx1 <- Person.name.age insert List(
   *     ("Ben", 42),
   *     ("Liz", 37),
   *   )
   *   List(ben, liz) = tx1.eids
   *
   *   // Update (tx report 2)
   *   tx2 <- Person(ben).age(43).update
   *
   *   // Retract (tx report 3)
   *   tx3 <- ben.retract
   *
   *   // Get List of all rows as of tx1 (after insert)
   *   _ <- Person.name.age.getAsOf(tx1).map(_ ==> List(
   *     ("Ben", 42),
   *     ("Liz", 37)
   *   ))
   *
   *   // Get List of all rows as of tx2 (after update)
   *   _ <- Person.name.age.getAsOf(tx2).map(_ ==> List(
   *     ("Ben", 43), // Ben now 43
   *     ("Liz", 37)
   *   ))
   *
   *   // Get List of all rows as of tx3 (after retract)
   *   _ <- Person.name.age.getAsOf(tx3).map(_ ==> List(
   *     ("Liz", 37) // Ben gone
   *   ))
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   * */
  def getAsOf(txR: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingAdhocDbView(AsOf(TxLong(txR.t)))), ec)


  /** Get Future with List of n rows as tuples matching molecule as of tx.
   * <br><br>
   * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a database
   * value as of that transaction (including).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a
   * [[molecule.datomic.base.facade.TxReport TxReport]]
   * that contains the transaction entity id (which is used as argument to Datomic internally).
   * This is more convenient when using Molecule since we get a [[molecule.datomic.base.facade.TxReport TxReport]]
   * from transaction operations like `get`, `update`, `retract` etc.
   * {{{
   * for {
   *   // Insert (tx report 1)
   *   tx1 <- Person.name.age insert List(
   *     ("Ben", 42),
   *     ("Liz", 37)
   *   )
   *   List(ben, liz) = tx1.eids
   *
   *   // Update (tx report 2)
   *   tx2 <- Person(ben).age(43).update
   *
   *   // Get List of all rows as of tx2 (after update)
   *   _ <- Person.name.age.getAsOf(tx2).map(_ ==> List(
   *     ("Ben", 43),
   *     ("Liz", 37)
   *   ))
   *
   *   // Get List of n rows as of tx2 (after update)
   *   _ <- Person.name.age.getAsOf(tx2, 1).map(_ ==> List(
   *     ("Ben", 43)
   *   ))
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   * */
  def getAsOf(txR: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingAdhocDbView(AsOf(TxLong(txR.t)))), ec)


  /** Get Future with List of all rows as tuples matching molecule as of date.
   * <br><br>
   * Get data at a human point in time (a java.util.Date).
   * {{{
   * for {
   *   beforeInsert = new java.util.Date
   *
   *   // Insert
   *   tx1 <- Person.name.age insert List(
   *     ("Ben", 42),
   *     ("Liz", 37),
   *   )
   *   List(ben, liz) = tx1.eids
   *   afterInsert = new java.util.Date
   *
   *   // Update
   *   tx2 <- Person(ben).age(43).update
   *   afterUpdate = new java.util.Date
   *
   *   // Retract
   *   tx3 <- ben.retract
   *   afterRetract = new java.util.Date
   *
   *   // No data yet before insert
   *   _ <- Person.name.age.getAsOf(beforeInsert).map(_ ==> Nil)
   *
   *   // Get List of all rows as of afterInsert
   *   _ <- Person.name.age.getAsOf(afterInsert).map(_ ==> List(
   *     ("Ben", 42),
   *     ("Liz", 37)´
   *   ))
   *
   *   // Get List of all rows as of afterUpdate
   *   _ <- Person.name.age.getAsOf(afterUpdate).map(_ ==> List(
   *     ("Ben", 43), // Ben now 43
   *     ("Liz", 37)
   *   ))
   *
   *   // Get List of all rows as of afterRetract
   *   _ <- Person.name.age.getAsOf(afterRetract).map(_ ==> List(
   *     ("Liz", 37) // Ben gone
   *   ))
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param date java.util.Date
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getAsOf(date: Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingAdhocDbView(AsOf(TxDate(date)))), ec)


  /** Get Future with List of n rows as tuples matching molecule as of date.
   * <br><br>
   * Get data at a human point in time (a java.util.Date).
   * {{{
   * for {
   *   beforeInsert = new java.util.Date
   *
   *   // Insert
   *   tx1 <- Person.name.age insert List(
   *     ("Ben", 42),
   *     ("Liz", 37)
   *   )
   *   List(ben, liz) = tx1.eids
   *   afterInsert = new java.util.Date
   *
   *   // Update
   *   tx2 <- Person(ben).age(43).update
   *   afterUpdate = new java.util.Date
   *
   *   // Get List of all rows as of afterUpdate
   *   _ <- Person.name.age.getAsOf(afterUpdate).map(_ ==> List(
   *     ("Ben", 43),
   *     ("Liz", 37)
   *   ))
   *
   *   // Get List of n rows as of afterUpdate
   *   _ <- Person.name.age.getAsOf(afterUpdate, 1).map(_ ==> List(
   *     ("Ben", 43)
   *   ))
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param date java.util.Date
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getAsOf(date: Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingAdhocDbView(AsOf(TxDate(date)))), ec)


  // get since ================================================================================================

  /** Get Future with List of all rows as tuples matching molecule since transaction time `t`.
   * <br><br>
   * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
   * <br><br>
   * `t` can for instance be retrieved by calling `t` on the tx report returned from transactional operations
   * and then be used to get data since that point in time (excluding that transaction):
   * {{{
   * for {
   *   // 3 transaction times `t`
   *   t1 <- Person.name("Ann").save.map(_.t)
   *   t2 <- Person.name("Ben").save.map(_.t)
   *   t3 <- Person.name("Cay").save.map(_.t)
   *
   *   // Current values
   *   _ <- Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
   *
   *   // Ben and Cay added since transaction time t1
   *   _ <- Person.name.getSince(t1).map(_ ==> List("Ben", "Cay"))
   *
   *   // Cay added since transaction time t2
   *   _ <- Person.name.getSince(t2).map(_ ==> List("Cay"))
   *
   *   // Nothing added since transaction time t3
   *   _ <- Person.name.getSince(t3).map(_ ==> Nil)
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param t    Transaction time t
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getSince(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingAdhocDbView(Since(TxLong(t)))), ec)


  /** Get Future with List of n rows as tuples matching molecule since transaction time `t`.
   * <br><br>
   * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
   * <br><br>
   * `t` can for instance be retrieved by calling `t` on the tx report returned from transactional operations
   * and then be used to get data since that point in time (excluding that transaction):
   * {{{
   * for {
   *   // 3 transaction times `t`
   *   t1 <- Person.name("Ann").save.map(_.t)
   *   t2 <- Person.name("Ben").save.map(_.t)
   *   t3 <- Person.name("Cay").save.map(_.t)
   *
   *   // Current values
   *   _ <- Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
   *
   *   // Ben and Cay added since transaction time t1
   *   _ <- Person.name.getSince(t1).map(_ ==> List("Ben", "Cay"))
   *
   *   // Ben and Cay added since transaction time t1 - only n (1) rows returned
   *   _ <- Person.name.getSince(t1, 1).map(_ ==> List("Ben"))
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param t    Transaction time t
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getSince(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingAdhocDbView(Since(TxLong(t)))), ec)


  /** Get Future with List of all rows as tuples matching molecule since tx.
   * <br><br>
   * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
   * value since that transaction (excluding the transaction itself).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a
   * [[molecule.datomic.base.facade.TxReport TxReport]] that contains
   * the transaction entity id (which is used as argument to Datomic internally).
   * This is more convenient when using Molecule since we
   * getAsync a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction
   * operations like `get`, `update`, `retract` etc.
   * {{{
   * for {
   *   // Get tx reports for 3 transactions
   *   tx1 <- Person.name("Ann").save
   *   tx2 <- Person.name("Ben").save
   *   tx3 <- Person.name("Cay").save
   *
   *   // Current values
   *   _ <- Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
   *
   *   // Ben and Cay added since tx1
   *   _ <- Person.name.getSince(tx1).map(_ ==> List("Ben", "Cay"))
   *
   *   // Cay added since tx2
   *   _ <- Person.name.getSince(tx2).map(_ ==> List("Cay"))
   *
   *   // Nothing added since tx3
   *   _ <- Person.name.getSince(tx3).map(_ ==> Nil)
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getSince(txR: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingAdhocDbView(Since(TxLong(txR.t)))), ec)


  /** Get Future with List of n rows as tuples matching molecule since tx.
   * <br><br>
   * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
   * value since that transaction (excluding the transaction itself).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a
   * [[molecule.datomic.base.facade.TxReport TxReport]] that contains
   * the transaction entity id (which is used as argument to Datomic internally).
   * This is more convenient when using Molecule since we
   * getAsync a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction
   * operations like `get`, `update`, `retract` etc.
   * {{{
   * for {
   *   // Get tx reports for 3 transactions
   *   tx1 <- Person.name("Ann").save
   *   tx2 <- Person.name("Ben").save
   *   tx3 <- Person.name("Cay").save
   *
   *   // Current values
   *   _ <- Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
   *
   *   // Ben and Cay added since tx1
   *   _ <- Person.name.getSince(tx1).map(_ ==> List("Ben", "Cay"))
   *
   *   // Ben and Cay added since tx1 - only n (1) rows returned
   *   _ <- Person.name.getSince(tx1, 1).map(_ ==> List("Ben"))
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getSince(txR: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingAdhocDbView(Since(TxLong(txR.t)))), ec)


  /** Get Future with List of all rows as tuples matching molecule since date.
   * <br><br>
   * Get data added/retracted since a human point in time (a java.util.Date).
   * {{{
   * for {
   *   // Transact 3 times (`inst` retrieves transaction time/Date from tx report)
   *   date1 <- Person.name("Ann").save.map(_.inst)
   *   date2 <- Person.name("Ben").save.map(_.inst)
   *   date3 <- Person.name("Cay").save.map(_.inst)
   *
   *   // Current values
   *   _ <- Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
   *
   *   // Ben and Cay added since date1
   *   _ <- Person.name.getSince(date1).map(_ ==> List("Ben", "Cay"))
   *
   *   // Cay added since date2
   *   _ <- Person.name.getSince(date2).map(_ ==> List("Cay"))
   *
   *   // Nothing added since date3
   *   _ <- Person.name.getSince(date3).map(_ ==> Nil)
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param date java.util.Date
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getSince(date: Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingAdhocDbView(Since(TxDate(date)))), ec)


  /** Get Future with List of n rows as tuples matching molecule since date.
   * <br><br>
   * Get data added/retracted since a human point in time (a java.util.Date).
   * {{{
   * for {
   *   // Transact 3 times (`inst` retrieves transaction time/Date from tx report)
   *   date1 <- Person.name("Ann").save.map(_.inst)
   *   date2 <- Person.name("Ben").save.map(_.inst)
   *   date3 <- Person.name("Cay").save.map(_.inst)
   *
   *   // Current values
   *   _ <- Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
   *
   *   // Ben and Cay added since date1
   *   _ <- Person.name.getSince(date1).map(_ ==> List("Ben", "Cay"))
   *
   *   // Ben and Cay added since date1 - only n (1) rows returned
   *   _ <- Person.name.getSince(date1, 1).map(_ ==> List("Ben"))
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param date java.util.Date
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getSince(date: Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingAdhocDbView(Since(TxDate(date)))), ec)


  // get with ================================================================================================

  /** Get Future with List of all rows as tuples matching molecule with applied molecule transaction data.
   * <br><br>
   * Apply one or more molecule transactions to in-memory "branch" of db without
   * affecting db to see how it would then look:
   * {{{
   * for {
   *   // Current state
   *   ben <- Person.name("Ben").likes("pasta").save.map(_.eid)
   *
   *   // Base data
   *   _ <- Person.name.likes.getWith(
   *     // apply imaginary transaction data
   *     Person(ben).likes("sushi").getUpdateStmts
   *   ).map(_ ==> List(
   *     // Effect: Ben would like sushi if tx was applied
   *     ("Ben", "sushi")
   *   ))
   *
   *   // Current state is still the same
   *   _ <- Person.name.likes.get.map(_ ==> List(("Ben", "pasta")))
   * } yield ()
   * }}}
   * Multiple transactions can be applied to test more complex what-if scenarios!
   *
   * @group getWith
   * @param txMolecules Transaction statements from applied Molecules with test data
   * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   */
  def getWith(txData: Future[Seq[Statement]]*)
             (implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    Future.sequence(txData).flatMap { stmtss =>
      val connWith = conn.map { conn =>
        val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, conn)
        conn.usingAdhocDbView(With(stmtsEdn, uriAttrs))
      }
      get(connWith, ec)
    }
  }


  /** Get Future with List of n rows as tuples matching molecule with applied molecule transaction data.
   * <br><br>
   * Apply one or more molecule transactions to in-memory "branch" of db without
   * affecting db to see how it would then look:
   * {{{
   * for {
   *   // Current state
   *   List(ben, liz) <- Person.name.likes.insert(
   *     ("Ben", "pasta"),
   *     ("Liz", "pizza")
   *   ).eids
   *
   *   // Test multiple transactions
   *   _ <- Person.name.likes.getWith(
   *     Person(ben).likes("sushi").getUpdateStmts,
   *     Person(liz).likes("cake").getUpdateStmts
   *   ).map(_ ==> List(
   *     ("Ben", "sushi")
   *     ("Liz", "cake")
   *   ))
   *
   *   // Same as above, but only n (1) rows returned:
   *   _ <- Person.name.likes.getWith(
   *     1
   *     Person(ben).likes("sushi").getUpdateStmts,
   *     Person(liz).likes("cake").getUpdateStmts
   *   ).map(_ ==> List(
   *     ("Ben", "sushi")
   *   ))
   * } yield ()
   * }}}
   *
   * @group getWith
   * @param n           Int Number of rows returned
   * @param txMolecules Transaction statements from applied Molecules with test data
   * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
   * @note Note how the `n` parameter has to come before the `txMolecules` vararg.
   */
  def getWith(n: Int, txData: Future[Seq[Statement]]*)
             (implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    Future.sequence(txData).flatMap { stmtss =>
      val connWith = conn.map { conn =>
        val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, conn)
        conn.usingAdhocDbView(With(stmtsEdn, uriAttrs))
      }
      get(n)(connWith, ec)
    }
  }


  // get history ================================================================================================

  /** Get `Future` with history of operations as `List` on an attribute in the db.
   * <br><br>
   * Generic datom attributes that can be called when `getHistory` is called:
   * <br>
   * <br> `e` - Entity id
   * <br> `a` - Attribute name
   * <br> `v` - Attribute value
   * <br> `ns` - Namespace name
   * <br> `tx` - [[molecule.datomic.base.facade.TxReport TxReport]]
   * <br> `t` - Transaction time t
   * <br> `txInstant` - Transaction time as java.util.Date
   * <br> `op` - Operation: true (add) or false (retract)
   * <br><br>
   * Example:
   * {{{
   * for {
   *   // Insert (t 1028)
   *   List(ben, liz) <- Person.name.age insert List(
   *     ("Ben", 42),
   *     ("Liz", 37),
   *   ) eids
   *
   *   // Update (t 1031)
   *   _ <- Person(ben).age(43).update
   *
   *   // Retract (t 1032)
   *   _ <- ben.retract
   *
   *   // History of Ben
   *   _ <- Person(ben).age.t.op.getHistory.map(_.sortBy(r => (r._2, r._3)) ==> List(
   *     (42, 1028, true),  // Insert:  42 asserted
   *     (42, 1031, false), // Update:  42 retracted
   *     (43, 1031, true),  //          43 asserted
   *     (43, 1032, false)  // Retract: 43 retracted
   *   ))
   * } yield ()
   * }}}
   *
   * @group getHistory
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return List[Tpl] where Tpl is tuple of data matching molecule
   */
  def getHistory(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    get(conn.map(_.usingAdhocDbView(History)), ec)
  }


  // `getHistory(n: Int)` is not implemented since the whole data set normally needs to be sorted
  // to give chronologically meaningful information.
}
