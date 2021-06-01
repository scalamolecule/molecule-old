package molecule.core.api

import java.util.{Date, List => jList}
import molecule.core.marshalling.Marshalling
import molecule.core.ops.ColOps
import molecule.datomic.base.ast.tempDb._
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.language.implicitConversions


/** Default asynchronous data getter methods on molecules returning `Future[List[Tpl]]`.
  * <br><br>
  * For expected smaller result sets it's convenient to return Lists of tuples of data.
  * Considered as the default getter, no postfix has been added (`getAsync` instead of `getAsyncList`).
  * {{{
  *   val futureList: Future[List[(String, Int)]] = Person.name.age.getAsync
  *   for {
  *     list <- futureList
  *   } yield {
  *     list === List(
  *       ("Ben", 42),
  *       ("Liz", 37)
  *     )
  *   }
  * }}}
  * Each asynchronous getter in this package simply wraps the result of its equivalent synchronous getter (in the
  * `get` package) in a Future. `getAsyncAsOf` thus wraps the result of `getAsOf` in a Future and so on.
  * */
trait GetTplList[Obj, Tpl] extends ColOps { self: Marshalling[Obj, Tpl] =>

  // get ================================================================================================

  /** Get `Future` with `List` of all rows as tuples matching molecule.
    * <br><br>
    * {{{
    *   Person.name.age.get.map(_ ==> List(
    *     ("Ben", 42),
    *     ("Liz", 37),
    *   ))
    * }}}
    * <br><br>
    * Since retrieving a List is considered the default fetch format, the getter method is
    * simply named `get` (and not `getList`).
    *
    * @group get
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def get(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    _inputThrowable.fold(
      conn.flatMap { conn =>
        if (conn.isJsPlatform) {
          conn.jsQuery(_nestedQuery.getOrElse(_query), -1, indexes, qr2tpl)
        } else {
          conn.query(_model, _query).map { jColl =>
            val it  = jColl.iterator
            val buf = new ListBuffer[Tpl]
            while (it.hasNext) {
              buf += row2tpl(it.next)
            }
            buf.toList
          }
        }
      }
    )(Future.failed) // Pass on exception from input failure
  }

  /** Get `Future` with `List` of n rows as tuples matching molecule.
    * <br><br>
    * Only n rows are type-casted.
    * {{{
    *   Person.name.age.get(1).map(_ ==> List(
    *     ("Ben", 42)
    *   )
    * }}}
    * <br><br>
    * Since retrieving a List is considered the default fetch format, the getter method is
    * simply named `get` (and not `getList`).
    *
    * @group get
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def get(n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    _inputThrowable.fold(
      conn.flatMap { conn2 =>
        if (conn2.isJsPlatform) {
          conn2.jsQuery(_nestedQuery.getOrElse(_query), n, indexes, qr2tpl)
        } else {
          if (n == -1) {
            get(conn, ec)
          } else {
            conn2.query(_model, _query).map { jColl =>
              val size = jColl.size
              val max  = if (size < n) size else n
              if (max == 0) {
                List.empty[Tpl]
              } else {
                val it  = jColl.iterator
                val buf = new ListBuffer[Tpl]
                var i   = 0
                while (it.hasNext && i < max) {
                  buf += row2tpl(it.next)
                  i += 1
                }
                buf.toList
              }
            }
          }
        }
      }
    )(Future.failed) // Pass on exception from input failure
  }


  // get as of ================================================================================================

  /** Get `Future` with `List` of all rows as tuples matching molecule as of transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved in a getHistory call for an attribute and then be
    * used to get data as of that point in time (including that transaction):
    * {{{
    *   // Insert (t 1028)
    *   val List(ben, liz) = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37),
    *   ) eids
    *
    *   // Update (t 1031)
    *   Person(ben).age(43).update
    *
    *   // Retract (t 1032)
    *   ben.retract
    *
    *   // History of Ben
    *   Person(ben).age.t.op..get(History.sortBy(r => )(r._2, r._3)).map(_ ==> List(
    *     (42, 1028, true),  // Insert:  42 asserted
    *     (42, 1031, false), // Update:  42 retracted
    *     (43, 1031, true),  //          43 asserted
    *     (43, 1032, false)  // Retract: 43 retracted
    *   )
    *
    *   // Get List of all rows as of transaction t 1028 (after insert)
    *   Person.name.age.getAsOf(1028).map(_ ==> List(
    *     ("Liz", 37),
    *     ("Ben", 42)
    *   )
    *
    *   // Get List of all rows as of transaction t 1031 (after update)
    *   Person.name.age.getAsOf(1031).map(_ ==> List(
    *     ("Liz", 37),
    *     ("Ben", 43)
    *   )
    *
    *   // Get List of all rows as of transaction t 1032 (after retract)
    *   Person.name.age.getAsOf(1032).map(_ ==> List(
    *     ("Liz", 37)
    *   )
    * }}}
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.core.api.GetTplList.getAsOf(t:Long)* getAsOf]] method.
    *
    * @group getAsOf
    * @param t    Transaction time t
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getAsOf(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingTempDb(AsOf(TxLong(t)))), ec)


  /** Get `Future` with `List` of n rows as tuples matching molecule as of transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved in a getHistory call for an attribute and then be
    * used to get data as of that point in time (including that transaction):
    * {{{
    *   // Insert (t 1028)
    *   val List(ben, liz) = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37),
    *   ) eids
    *
    *   // Update (t 1031)
    *   Person(ben).age(43).update
    *
    *   // History of Ben
    *   Person(ben).age.t.op.getHistory.sortBy(r => (r._2, r._3)).map(_ ==> List(
    *     (42, 1028, true),  // Insert:  42 asserted
    *     (42, 1031, false), // Update:  42 retracted
    *     (43, 1031, true),  //          43 asserted
    *   )
    *
    *   // Get List of all all rows as of transaction t 1031 (after update)
    *   Person.name.age.getAsOf(1031).map(_ ==> List(
    *     ("Ben", 43),
    *     ("Liz", 37)
    *   )
    *
    *   // Get List of n rows as of transaction t 1031 (after update)
    *   Person.name.age.getAsOf(1031, 1).map(_ ==> List(
    *     ("Ben", 43)
    *   )
    * }}}
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.core.api.GetTplList.getAsOf(t:Long,n:Int)* getAsOf]] method.
    *
    * @group getAsOf
    * @param t    Long Transaction time t
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getAsOf(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingTempDb(AsOf(TxLong(t)))), ec)


  /** Get `Future` with `List` of all rows as tuples matching molecule as of tx.
    * <br><br>
    * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a
    * database value as of that transaction (including).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]]
    * that contains the transaction entity id (which is used as argument to Datomic internally). This is more
    * convenient when using Molecule since we get a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction
    * operations like `get`, `update`, `retract` etc.
    * {{{
    *   // Insert (tx report 1)
    *   val tx1 = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37),
    *   )
    *   val List(ben, liz) = tx1.eids
    *
    *   // Update (tx report 2)
    *   val tx2 = Person(ben).age(43).update
    *
    *   // Retract (tx report 3)
    *   val tx3 = ben.retract
    *
    *   // Get List of all rows as of tx1 (after insert)
    *   Person.name.age.getAsOf(tx1).map(_ ==> List(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    *
    *   // Get List of all rows as of tx2 (after update)
    *   Person.name.age.getAsOf(tx2).map(_ ==> List(
    *     ("Ben", 43), // Ben now 43
    *     ("Liz", 37)
    *   )
    *
    *   // Get List of all rows as of tx3 (after retract)
    *   Person.name.age.getAsOf(tx3).map(_ ==> List(
    *     ("Liz", 37) // Ben gone
    *   )
    * }}}
    *
    * @group getAsOf
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    * */
  def getAsOf(tx: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingTempDb(AsOf(TxLong(tx.t)))), ec)


  /** Get `Future` with `List` of n rows as tuples matching molecule as of tx.
    * <br><br>
    * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a database
    * value as of that transaction (including).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]]
    * that contains the transaction entity id (which is used as argument to Datomic internally). This is more
    * convenient when using Molecule since we get a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction
    * operations like `get`, `update`, `retract` etc.
    * {{{
    *   // Insert (tx report 1)
    *   val tx1 = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    *   val List(ben, liz) = tx1.eids
    *
    *   // Update (tx report 2)
    *   val tx2 = Person(ben).age(43).update
    *
    *
    *   // Get List of all rows as of tx2 (after update)
    *   Person.name.age.getAsOf(tx2).map(_ ==> List(
    *     ("Ben", 43),
    *     ("Liz", 37)
    *   )
    *
    *   // Get List of n rows as of tx2 (after update)
    *   Person.name.age.getAsOf(tx2, 1).map(_ ==> List(
    *     ("Ben", 43)
    *   )
    * }}}
    *
    * @group getAsOf
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    * */
  def getAsOf(tx: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingTempDb(AsOf(TxLong(tx.t)))), ec)


  /** Get `Future` with `List` of all rows as tuples matching molecule as of date.
    * <br><br>
    * Get data at a human point in time (a java.util.Date).
    * {{{
    *   val beforeInsert = new java.util.Date
    *
    *   // Insert
    *   val tx1 = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37),
    *   )
    *   val List(ben, liz) = tx1.eids
    *   val afterInsert = new java.util.Date
    *
    *   // Update
    *   val tx2 = Person(ben).age(43).update
    *   val afterUpdate = new java.util.Date
    *
    *   // Retract
    *   val tx3 = ben.retract
    *   val afterRetract = new java.util.Date
    *
    *   // No data yet before insert
    *   Person.name.age.getAsOf(beforeInsert).map(_ ==> Nil)
    *
    *   // Get List of all rows as of afterInsert
    *   Person.name.age.getAsOf(afterInsert).map(_ ==> List(
    *     ("Ben", 42),
    *     ("Liz", 37)´
    *   )
    *
    *   // Get List of all rows as of afterUpdate
    *   Person.name.age.getAsOf(afterUpdate).map(_ ==> List(
    *     ("Ben", 43), // Ben now 43
    *     ("Liz", 37)
    *   )
    *
    *   // Get List of all rows as of afterRetract
    *   Person.name.age.getAsOf(afterRetract).map(_ ==> List(
    *     ("Liz", 37) // Ben gone
    *   )
    * }}}
    *
    * @group getAsOf
    * @param date java.util.Date
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getAsOf(date: Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingTempDb(AsOf(TxDate(date)))), ec)


  /** Get `Future` with `List` of n rows as tuples matching molecule as of date.
    * <br><br>
    * Get data at a human point in time (a java.util.Date).
    * {{{
    *   val beforeInsert = new java.util.Date
    *
    *   // Insert
    *   val tx1 = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    *   val List(ben, liz) = tx1.eids
    *   val afterInsert = new java.util.Date
    *
    *   // Update
    *   val tx2 = Person(ben).age(43).update
    *   val afterUpdate = new java.util.Date
    *
    *   // Get List of all rows as of afterUpdate
    *   Person.name.age.getAsOf(afterUpdate).map(_ ==> List(
    *     ("Ben", 43),
    *     ("Liz", 37)
    *   )
    *
    *   // Get List of n rows as of afterUpdate
    *   Person.name.age.getAsOf(afterUpdate, 1).map(_ ==> List(
    *     ("Ben", 43)
    *   )
    * }}}
    *
    * @group getAsOf
    * @param date java.util.Date
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getAsOf(date: Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingTempDb(AsOf(TxDate(date)))), ec)


  // get since ================================================================================================

  /** Get `Future` with `List` of all rows as tuples matching molecule since transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved by calling `t` on the tx report returned from transactional operations
    * and then be used to get data since that point in time (excluding that transaction):
    * {{{
    *   // 3 transaction times `t`
    *   val t1 = Person.name("Ann").save.t
    *   val t2 = Person.name("Ben").save.t
    *   val t3 = Person.name("Cay").save.t
    *
    *   // Current values
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since transaction time t1
    *   Person.name.getSince(t1).map(_ ==> List("Ben", "Cay"))
    *
    *   // Cay added since transaction time t2
    *   Person.name.getSince(t2).map(_ ==> List("Cay"))
    *
    *   // Nothing added since transaction time t3
    *   Person.name.getSince(t3).map(_ ==> Nil)
    * }}}
    *
    * @group getSince
    * @param t    Transaction time t
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getSince(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingTempDb(Since(TxLong(t)))), ec)


  /** Get `Future` with `List` of n rows as tuples matching molecule since transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved by calling `t` on the tx report returned from transactional operations
    * and then be used to get data since that point in time (excluding that transaction):
    * {{{
    *   // 3 transaction times `t`
    *   val t1 = Person.name("Ann").save.t
    *   val t2 = Person.name("Ben").save.t
    *   val t3 = Person.name("Cay").save.t
    *
    *   // Current values
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since transaction time t1
    *   Person.name.getSince(t1).map(_ ==> List("Ben", "Cay"))
    *
    *   // Ben and Cay added since transaction time t1 - only n (1) rows returned
    *   Person.name.getSince(t1, 1).map(_ ==> List("Ben"))
    * }}}
    *
    * @group getSince
    * @param t    Transaction time t
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getSince(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingTempDb(Since(TxLong(t)))), ec)


  /** Get `Future` with `List` of all rows as tuples matching molecule since tx.
    * <br><br>
    * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
    * value since that transaction (excluding the transaction itself).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]] that contains
    * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
    * getAsync a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
    * {{{
    *   // Get tx reports for 3 transactions
    *   val tx1 = Person.name("Ann").save
    *   val tx2 = Person.name("Ben").save
    *   val tx3 = Person.name("Cay").save
    *
    *   // Current values
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since tx1
    *   Person.name.getSince(tx1).map(_ ==> List("Ben", "Cay"))
    *
    *   // Cay added since tx2
    *   Person.name.getSince(tx2).map(_ ==> List("Cay"))
    *
    *   // Nothing added since tx3
    *   Person.name.getSince(tx3).map(_ ==> Nil)
    * }}}
    *
    * @group getSince
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getSince(tx: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingTempDb(Since(TxLong(tx.t)))), ec)


  /** Get `Future` with `List` of n rows as tuples matching molecule since tx.
    * <br><br>
    * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
    * value since that transaction (excluding the transaction itself).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]] that contains
    * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
    * getAsync a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
    * {{{
    *   // Get tx reports for 3 transactions
    *   val tx1 = Person.name("Ann").save
    *   val tx2 = Person.name("Ben").save
    *   val tx3 = Person.name("Cay").save
    *
    *   // Current values
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since tx1
    *   Person.name.getSince(tx1).map(_ ==> List("Ben", "Cay"))
    *
    *   // Ben and Cay added since tx1 - only n (1) rows returned
    *   Person.name.getSince(tx1, 1).map(_ ==> List("Ben"))
    * }}}
    *
    * @group getSince
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getSince(tx: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingTempDb(Since(TxLong(tx.t)))), ec)


  /** Get `Future` with `List` of all rows as tuples matching molecule since date.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * {{{
    *   // Transact 3 times (`inst` retrieves transaction time/Date from tx report)
    *   val date1 = Person.name("Ann").save.inst
    *   val date2 = Person.name("Ben").save.inst
    *   val date3 = Person.name("Cay").save.inst
    *
    *   // Current values
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since date1
    *   Person.name.getSince(date1).map(_ ==> List("Ben", "Cay"))
    *
    *   // Cay added since date2
    *   Person.name.getSince(date2).map(_ ==> List("Cay"))
    *
    *   // Nothing added since date3
    *   Person.name.getSince(date3).map(_ ==> Nil)
    * }}}
    *
    * @group getSince
    * @param date java.util.Date
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getSince(date: Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingTempDb(Since(TxDate(date)))), ec)


  /** Get `Future` with `List` of n rows as tuples matching molecule since date.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * {{{
    *   // Transact 3 times (`inst` retrieves transaction time/Date from tx report)
    *   val date1 = Person.name("Ann").save.inst
    *   val date2 = Person.name("Ben").save.inst
    *   val date3 = Person.name("Cay").save.inst
    *
    *   // Current values
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since date1
    *   Person.name.getSince(date1).map(_ ==> List("Ben", "Cay"))
    *
    *   // Ben and Cay added since date1 - only n (1) rows returned
    *   Person.name.getSince(date1, 1).map(_ ==> List("Ben"))
    * }}}
    *
    * @group getSince
    * @param date java.util.Date
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getSince(date: Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingTempDb(Since(TxDate(date)))), ec)


  // get with ================================================================================================

  /** Get `Future` with `List` of all rows as tuples matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Current state
    *   val ben = Person.name("Ben").likes("pasta").save.eid
    *
    *   // Base data
    *   Person.name.likes.getWith(
    *     // apply imaginary transaction data
    *     Person(ben).likes("sushi").getUpdateTx
    *   ).map(_ ==> List(
    *     // Effect: Ben would like sushi if tx was applied
    *     ("Ben", "sushi")
    *   )
    *
    *   // Current state is still the same
    *   Person.name.likes.get.map(_ ==> List(("Ben", "pasta")))
    * }}}
    * Multiple transactions can be applied to test more complex what-if scenarios!
    *
    * @group getWith
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getWith(txMolecules: Future[Seq[Statement]]*)
             (implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      get(conn.map(conn2 => conn2.usingTempDb(With(conn2.stmts2java(stmtss.flatten)))), ec)
    }
  }


  /** Get `Future` with `List` of n rows as tuples matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Current state
    *   val List(ben, liz) = Person.name.likes.insert(
    *     ("Ben", "pasta"),
    *     ("Liz", "pizza")
    *   ).eids
    *
    *   // Test multiple transactions
    *   Person.name.likes.getWith(
    *     Person(ben).likes("sushi").getUpdateTx,
    *     Person(liz).likes("cake").getUpdateTx
    *   ).map(_ ==> List(
    *     ("Ben", "sushi")
    *     ("Liz", "cake")
    *   )
    *
    *   // Same as above, but only n (1) rows returned:
    *   Person.name.likes.getWith(
    *     1
    *     Person(ben).likes("sushi").getUpdateTx,
    *     Person(liz).likes("cake").getUpdateTx
    *   ).map(_ ==> List(
    *     ("Ben", "sushi")
    *   )
    * }}}
    *
    * @group getWith
    * @param n           Int Number of rows returned
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    * @note Note how the `n` parameter has to come before the `txMolecules` vararg.
    */
  def getWith(n: Int, txMolecules: Future[Seq[Statement]]*)
             (implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      get(n)(conn.map(conn2 => conn2.usingTempDb(With(conn2.stmts2java(stmtss.flatten)))), ec)
    }
  }


  /** Get `Future` with `List` of all rows as tuples matching molecule with applied raw transaction data.
    * <br><br>
    * Apply raw transaction data to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Live size of Person db
    *   Person.name.get.map(_.size ==> 150)
    *
    *   // Read some transaction data from file
    *   val data_rdr2 = new FileReader("examples/resources/seattle/seattle-data1a.dtm")
    *   val newDataTx = Util.readAll(data_rdr2).get(0).asInstanceOf[java.util.List[Object]]
    *
    *   // Imagine future db - 100 persons would be added, apparently
    *   Person.name.getWith(newDataTx).map(_.size ==> 250
    * }}}
    *
    * @group getWith
    * @param txData Raw transaction data as java.util.List[Object]
    * @param conn   Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getWith(txData: jList[_])(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] = {
    get(conn.map(_.usingTempDb(With(txData.asInstanceOf[jList[jList[_]]]))), ec)
  }

  /** Get `Future` with `List` of n rows as tuples matching molecule with applied raw transaction data.
    * <br><br>
    * Apply raw transaction data to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Live size of Person db
    *   Person.name.get.map(_.size ==> 150)
    *
    *   // Read some transaction data from file
    *   val data_rdr2 = new FileReader("examples/resources/seattle/seattle-data1a.dtm")
    *   val newDataTx = Util.readAll(data_rdr2).get(0).asInstanceOf[java.util.List[Object]]
    *
    *   // Imagine future db - 100 persons would be added, apparently
    *   Person.name.getWith(newDataTx).map(_.size ==> 250)
    *
    *   // Imagine future db - Let's just take 10
    *   Person.name.getWith(newDataTx, 10).map(_.size ==> 10)
    * }}}
    *
    * @group getWith
    * @param txData Raw transaction data as java.util.List[Object]
    * @param n      Int Number of rows returned
    * @param conn   Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[List[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getWith(txData: jList[_], n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(n)(conn.map(_.usingTempDb(With(txData.asInstanceOf[jList[jList[_]]]))), ec)


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
    *   // Insert (t 1028)
    *   val List(ben, liz) = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37),
    *   ) eids
    *
    *   // Update (t 1031)
    *   Person(ben).age(43).update
    *
    *   // Retract (t 1032)
    *   ben.retract
    *
    *   // History of Ben
    *   Person(ben).age.t.op.getHistory.sortBy(r => (r._2, r._3)).map(_ ==> List(
    *     (42, 1028, true),  // Insert:  42 asserted
    *     (42, 1031, false), // Update:  42 retracted
    *     (43, 1031, true),  //          43 asserted
    *     (43, 1032, false)  // Retract: 43 retracted
    *   )
    * }}}
    *
    * @group getHistory
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return List[Tpl] where Tpl is tuple of data matching molecule
    */
  def getHistory(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Tpl]] =
    get(conn.map(_.usingTempDb(History)), ec)


  // `getHistory(n: Int)` is not implemented since the whole data set normally needs to be sorted
  // to give chronological meaningful information.
}
