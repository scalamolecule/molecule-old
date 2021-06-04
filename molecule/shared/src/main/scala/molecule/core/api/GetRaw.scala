package molecule.core.api

import java.util.{Date, Collection => jCollection, List => jList}
import molecule.datomic.base.ast.dbView.{AsOf, Since, TxDate, TxLong, With}
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}

/** Asynchronous data getter methods on molecules returning raw untyped Datomic data.
  * <br><br>
  * Returns a `Future` with raw untyped `java.util.Collection[java.util.List[Object]]` directly from Datomic and is
  * therefore the fastest (but untyped) way of retrieving data. Can be useful where typed data is
  * not needed.
  * {{{
  *   val rawDataFuture: Future[java.util.Colleciton[java.util.List[Object]] = Person.name.age.getAsyncRaw
  *   for {
  *     rawData <- rawDataFuture
  *   } yield {
  *     rawData.toString === """[["Ben" 42]["Liz" 37]]"""
  *   }
  * }}}
  * Each asynchronous getter in this package simply wraps the result of its equivalent synchronous getter (in the
  * `get` package) in a Future. `getAsyncRawAsOf` thus wraps the result of `getRawAsOf` in a Future and so on.
  * */
trait GetRaw { self: Molecule =>


  // get ================================================================================================

  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule.
    * {{{
    *   Person.name.age.getRaw.map(_.toString ==> """[["Ben" 42], ["Liz" 37]]""")
    * }}}
    *
    * Peer returns java.util.HashSet
    * Client returns clojure.lang.PersistentVector
    * We unify the type by copying to an ArrayList
    *
    * @group getAsync
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRaw(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    conn.flatMap { conn =>
      if (conn.isJsPlatform) {
        Future.failed(new IllegalArgumentException("Please fetch `List`s of data with `get` instead."))
      } else {
        conn.query(_model, _query)
      }
    }
  }


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule.
    * {{{
    *   Person.name.age.getRaw(1).map(_.toString ==> """[["Ben" 42]]""")
    * }}}
    *
    * @group getAsync
    * @param n    Number of rows
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRaw(n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    conn.flatMap { conn2 =>
      if (conn2.isJsPlatform) {
        Future.failed(new IllegalArgumentException("Please fetch `List`s of data with `get` instead."))
      } else if (n == -1) {
        getRaw(conn, ec)
      } else {
        conn2.query(_model, _query).map { jColl =>
          val size = jColl.size
          val max  = if (size < n) size else n
          val it   = jColl.iterator
          val a    = new java.util.ArrayList[jList[AnyRef]](max)
          var i    = 0
          while (it.hasNext && i < max) {
            a.add(it.next)
            i += 1
          }
          a
        }
      }
    }
  }

  // get as of ================================================================================================

  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule as of transaction time `t`.
    * <br><br>
    * Call `getRawAsOf` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
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
    *   Person(ben).age.t.op.getHistory.sortBy(r => (r._2, r._3)).map(_ ==> List(
    *     (42, 1028, true),  // Insert:  42 asserted
    *     (42, 1031, false), // Update:  42 retracted
    *     (43, 1031, true),  //          43 asserted
    *     (43, 1032, false)  // Retract: 43 retracted
    *   )
    *
    *   // Get raw data as of transaction t 1028 (after insert)
    *   Person.name.age.getRawAsOf(1028).map(_.toString ==> """[["Ben" 42], ["Liz" 37]]""")
    *
    *   // Get raw data as of transaction t 1031 (after update)
    *   Person.name.age.getRawAsOf(1031).map(_.toString ==> """[["Ben" 43], ["Liz" 37]]""") // Ben now 43
    *
    *   // Get raw data as of transaction t 1032 (after retract)
    *   Person.name.age.getRawAsOf(1032).map(_.toString ==> """[["Liz" 37]]""") // Ben gone
    * }}}
    *
    * @group getRawAsOf
    * @param t    Transaction time t
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawAsOf(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(conn.map(_.usingDbView(AsOf(TxLong(t)))), ec)


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule as of transaction time `t`.
    * <br><br>
    * Call `getRawAsOf` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
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
    *   // Get all rows of raw data as of transaction t 1031 (after update)
    *   Person.name.age.getRawAsOf(1028).map(_.toString ==> """[["Ben" 43], ["Liz" 37]]""")
    *
    *   // Get n (1) rows of raw data as of transaction t 1031 (after update)
    *   Person.name.age.getRawAsOf(1031, 1).map(_.toString ==> """[["Ben" 43]]""")
    * }}}
    *
    * @group getRawAsOf
    * @param t    Transaction time t
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawAsOf(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(n)(conn.map(_.usingDbView(AsOf(TxLong(t)))), ec)


  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule as of tx.
    * <br><br>
    * Call `getRawAsOf` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
    * <br><br>
    * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a database value as of that transaction (including).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]] that contains
    * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
    * getAsync a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
    * {{{
    *   // Insert (tx report 1)
    *   val tx1 = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37),
    *   )
    *   val ben = tx1.eid
    *
    *   // Update (tx report 2)
    *   val tx2 = Person(ben).age(43).update
    *
    *   // Retract (tx report 3)
    *   val tx3 = ben.retract
    *
    *   // Get raw data as of tx1 (after insert)
    *   Person.name.age.getRawAsOf(tx1).map(_.toString ==> """[["Liz" 37], ["Ben" 42]]""")
    *
    *   // Get raw data as of tx2 (after update)
    *   Person.name.age.getRawAsOf(tx2).map(_.toString ==> """[["Liz" 37], ["Ben" 43]]""") // Ben now 43
    *
    *   // Get raw data as of tx3 (after retract)
    *   Person.name.age.getRawAsOf(tx3).map(_.toString ==> """[["Liz" 37]]""") // Ben gone
    * }}}
    *
    * @group getRawAsOf
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawAsOf(tx: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(conn.map(_.usingDbView(AsOf(TxLong(tx.t)))), ec)


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule as of tx.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    * Call `getRawAsOf` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
    * <br><br>
    * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a database value as of that transaction (including).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]] that contains
    * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
    * getAsync a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
    * {{{
    *   // Insert (tx report 1)
    *   val tx1 = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37),
    *   )
    *   val ben = tx1.eid
    *
    *   // Update (tx report 2)
    *   val tx2 = Person(ben).age(43).update
    *
    *   // Retract (tx report 3)
    *   val tx3 = ben.retract
    *
    *   // Get all rows of raw data as of tx1 (after insert)
    *   Person.name.age.getRawAsOf(tx1).map(_.toString ==> """[["Liz" 37], ["Ben" 42]]""")
    *
    *   // Get n (1) rows of raw data as of tx1 (after insert)
    *   Person.name.age.getRawAsOf(tx1, 1).map(_.toString ==> """[["Liz" 37]]""")
    * }}}
    *
    * @group getRawAsOf
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawAsOf(tx: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(n)(conn.map(_.usingDbView(AsOf(TxLong(tx.t)))), ec)


  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule as of date.
    * <br><br>
    * Call getRawAsOf when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
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
    *   val ben = tx1.eid
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
    *   // Get raw data as of beforeInsert
    *   Person.name.age.getRawAsOf(beforeInsert).map(_.toString ==> """[]""")
    *
    *   // Get raw data as of afterInsert
    *   Person.name.age.getRawAsOf(afterInsert).map(_.toString ==> """[["Liz" 37], ["Ben" 42]]""")
    *
    *   // Get raw data as of afterUpdate
    *   Person.name.age.getRawAsOf(afterUpdate).map(_.toString ==> """[["Liz" 37], ["Ben" 43]]""") // Ben now 43
    *
    *   // Get raw data as of afterRetract
    *   Person.name.age.getRawAsOf(afterRetract).map(_.toString ==> """[["Liz" 37]]""") // Ben gone
    * }}}
    *
    * @group getRawAsOf
    * @param date java.util.Date
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawAsOf(date: Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(conn.map(_.usingDbView(AsOf(TxDate(date)))), ec)


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule as of date.
    * <br><br>
    * Call getRawAsOf when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
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
    *   val ben = tx1.eid
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
    *   // Get all rows of raw data as of beforeInsert
    *   Person.name.age.getRawAsOf(beforeInsert).map(_.toString ==> """[]""")
    *
    *   // Get all rows of raw data as of afterInsert
    *   Person.name.age.getRawAsOf(afterInsert).map(_.toString ==> """[["Liz" 37], ["Ben" 42]]""")
    *
    *   // Get n (1) rows of raw data as of afterInsert
    *   Person.name.age.getRawAsOf(afterInsert, 1).map(_.toString ==> """[["Liz" 37]]""")
    * }}}
    *
    * @group getRawAsOf
    * @param date java.util.Date
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawAsOf(date: Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(n)(conn.map(_.usingDbView(AsOf(TxDate(date)))), ec)


  // get since ================================================================================================

  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule since transaction time `t`.
    * <br><br>
    * Call `getRawSince` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved calling `t` on the tx report returned from transactional operations
    * and then be used to get data since that point in time (excluding that transaction):
    * {{{
    *   // 3 transaction times `t`
    *   val t1 = Person.name("Ann").save.t
    *   val t2 = Person.name("Ben").save.t
    *   val t3 = Person.name("Cay").save.t
    *
    *   // Current values as Iterable
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since transaction time t1
    *   Person.name.getRawSince(t1).map(_.toString ==> """[["Ben"], ["Cay"]]""")
    *
    *   // Cay added since transaction time t2
    *   Person.name.getRawSince(t2).map(_.toString ==> """[["Cay"]]""")
    *
    *   // Nothing added since transaction time t3
    *   Person.name.getRawSince(t3).map(_.toString ==> """[]""")
    * }}}
    *
    * @group getRawSince
    * @param t    Transaction time t
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawSince(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(conn.map(_.usingDbView(Since(TxLong(t)))), ec)


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule since transaction time `t`.
    * <br><br>
    * Call `getRawSince` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved calling `t` on the tx report returned from transactional operations
    * and then be used to get data since that point in time (excluding that transaction):
    * {{{
    *   // 3 transaction times `t`
    *   val t1 = Person.name("Ann").save.t
    *   val t2 = Person.name("Ben").save.t
    *   val t3 = Person.name("Cay").save.t
    *
    *   // Current values as Iterable
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since transaction time t1
    *   Person.name.getRawSince(t1).map(_.toString ==> """[["Ben"], ["Cay"]]""")
    *
    *   // Ben and Cay added since transaction time t1 - only n (1) rows returned
    *   Person.name.getRawSince(t1).map(_.toString ==> """[["Ben"]]""")
    * }}}
    *
    * @group getRawSince
    * @param t    Transaction time t
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawSince(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(n)(conn.map(_.usingDbView(Since(TxLong(t)))), ec)


  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule since tx.
    * <br><br>
    * Call `getRawSince` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
    * <br><br>
    * Datomic's internal `since` method can take a transaction entity id as argument to retrieve a database value since that transaction (excluding).
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
    *   Person.name.getRawSince(tx1).map(_.toString ==> """[["Ben"], ["Cay"]]""")
    *
    *   // Cay added since tx2
    *   Person.name.getRawSince(tx2).map(_.toString ==> """[["Cay"]]""")
    *
    *   // Nothing added since tx3
    *   Person.name.getRawSince(tx3).map(_.toString ==> """[]""")
    * }}}
    *
    * @group getRawSince
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawSince(tx: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(conn.map(_.usingDbView(Since(TxLong(tx.t)))), ec)


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule since tx.
    * <br><br>
    * Call `getRawSince` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
    * <br><br>
    * Datomic's internal `since` method can take a transaction entity id as argument to retrieve a database value since that transaction (excluding).
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
    *   Person.name.getRawSince(tx1).map(_.toString ==> """[["Ben"], ["Cay"]]""")
    *
    *   // Ben and Cay added since tx1 - only n (1) rows returned
    *   Person.name.getRawSince(tx1, 1).map(_.toString ==> """[["Ben"]]""")
    * }}}
    *
    * @group getRawSince
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawSince(tx: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(n)(conn.map(_.usingDbView(Since(TxLong(tx.t)))), ec)


  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule since date.
    * <br><br>
    * Call `getRawSince` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * {{{
    *   // Transact 3 times (`inst` retrieves transaction time from tx report)
    *   val date1 = Person.name("Ann").save.inst
    *   val date2 = Person.name("Ben").save.inst
    *   val date3 = Person.name("Cay").save.inst
    *
    *   // Current values
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since date1
    *   Person.name.getRawSince(date1).map(_.toString ==> """[["Ben"], ["Cay"]]""")
    *
    *   // Cay added since date2
    *   Person.name.getRawSince(date2).map(_.toString ==> """[["Cay"]]""")
    *
    *   // Nothing added since date3
    *   Person.name.getRawSince(date3).map(_.toString ==> """[]""")
    * }}}
    *
    * @group getRawSince
    * @param date java.util.Date
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawSince(date: Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(conn.map(_.usingDbView(Since(TxDate(date)))), ec)


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule since date.
    * <br><br>
    * Call `getRawSince` when data doesn't need to be type-casted. Datomic's raw data is returned as-is.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * {{{
    *   // Transact 3 times (`inst` retrieves transaction time from tx report)
    *   val date1 = Person.name("Ann").save.inst
    *   val date2 = Person.name("Ben").save.inst
    *   val date3 = Person.name("Cay").save.inst
    *
    *   // Current values
    *   Person.name.get.map(_ ==> List("Ann", "Ben", "Cay"))
    *
    *   // Ben and Cay added since date1
    *   Person.name.getRawSince(date1).map(_.toString ==> """[["Ben"], ["Cay"]]""")
    *
    *   // Ben and Cay added since date1 - only n (1) rows returned
    *   Person.name.getRawSince(date1, 1).map(_.toString ==> """[["Ben"]]""")
    * }}}
    *
    * @group getRawSince
    * @param date java.util.Date
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawSince(date: Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(n)(conn.map(_.usingDbView(Since(TxDate(date)))), ec)


  // get with ================================================================================================

  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Current state
    *   val ben = Person.name("Ben").likes("pasta").save.eid
    *
    *   // Base data
    *   Person.name.likes.getRawWith(
    *     Person(ben).likes("sushi").getUpdateTx
    *   ).toString ==== """[["Ben" "sushi"]]"""
    *
    *   // Current state is still the same
    *   Person.name.likes.get.map(_ ==> List(("Ben", "pasta")))
    * }}}
    * Multiple transactions can be applied to test more complex what-if scenarios!
    *
    * @group getRawWith
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawWith(txMolecules: Future[Seq[Statement]]*)
                (implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      getRaw(conn.map(conn2 => conn2.usingDbView(With(conn2.stmts2java(stmtss.flatten)))), ec)
    }
  }


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule with applied molecule transaction data.
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
    *   Person.name.likes.getRawWith(
    *     Person(ben).likes("sushi").getUpdateTx,
    *     Person(liz).likes("cake").getUpdateTx
    *   ).toString === """[["Ben" "sushi"], ["Liz" "cake"]]"""
    *
    *   // Same as above, but only n (1) rows returned:
    *   Person.name.likes.getRawWith(
    *     1
    *     Person(ben).likes("sushi").getUpdateTx,
    *     Person(liz).likes("cake").getUpdateTx
    *   ).toString === """[["Ben" "sushi"]]"""
    * }}}
    * Multiple transactions can be applied to test more complex what-if scenarios!
    *
    * @group getRawWith
    * @param n           Int Number of rows returned
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    * @note Note how the `n` parameter has to come before the `txMolecules` vararg.
    */
  def getRawWith(n: Int, txMolecules: Future[Seq[Statement]]*)
                (implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      getRaw(n)(conn.map(conn2 => conn2.usingDbView(With(conn2.stmts2java(stmtss.flatten)))), ec)
    }
  }


  /** Get `Future` with `java.util.Collection` of all untyped rows matching molecule with applied raw transaction data.
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
    *   Person.name.getRawWith(newDataTx).map(_.size ==> 250)
    * }}}
    *
    * @group getRawWith
    * @param txData Raw transaction data as java.util.List[Object]
    * @param conn   Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
//  def getRawWith(txData: jList[_])
//                (implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
//    getRaw(conn.map(_.usingDbView(With(txData.asInstanceOf[jList[jList[_]]]))), ec)


  /** Get `Future` with `java.util.Collection` of n untyped rows matching molecule with applied raw transaction data.
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
    *   Person.name.getRawWith(newDataTx).map(_.size ==> 250)
    *
    *   // Imagine future db - Let's just take 10
    *   Person.name.getRawWith(newDataTx, 10).map(_.size ==> 10)
    * }}}
    *
    * @group getRawWith
    * @param txData Raw transaction data as java.util.List[Object]
    * @param n      Int Number of rows returned
    * @param conn   Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `java.util.Collection[java.util.List[AnyRef]]`
    */
  def getRawWith(txData: jList[_], n: Int)
                (implicit conn: Future[Conn], ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    getRaw(n)(conn.map(_.usingDbView(With(txData.asInstanceOf[jList[jList[_]]]))), ec)


  // get history ================================================================================================

  // Only `getHistory`/`getHistory` returning List/Future[List] are implemented since it is only meaningful
  // to track the history of one attribute of one entity at a time and a sortable List is therefore preferred.

}
