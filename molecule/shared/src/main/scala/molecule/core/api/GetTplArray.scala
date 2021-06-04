package molecule.core.api

import java.util.{Date, List => jList}
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.ast.dbView.{Since, TxDate, With}
import scala.concurrent.ExecutionContext
import molecule.datomic.base.ast.dbView.{AsOf, TxLong}
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.reflect.ClassTag


/** Asynchronous data getter methods on molecules returning `Future[Array[Tpl]]`.
  * <br><br>
  * The fastest way of getting a large typed data set since data is applied to a super fast pre-allocated Array.
  * The Array can then be traversed with a fast `while` loop.
  * {{{
  *   Ns.int.insert(1, 2, 3)
  *
  *   // Map over Future
  *   Ns.int.getAsyncArray.map { result =>
  *     result === Array(1, 2, 3)
  *
  *     // Fast while loop
  *     var i = 0
  *     val length = result.length
  *     while(i < length) {
  *       println(result(i)) // Do stuff with row...
  *       i += 1
  *     }
  *   }
  * }}}
  * Each asynchronous getter in this package simply wraps the result of its equivalent synchronous getter (in the
  * `get` package) in a Future. `getAsyncArrayAsOf` thus wraps the result of `getArrayAsOf` in a Future and so on.
  * */
trait GetTplArray[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>


  // get ================================================================================================

  /** Get `Future` with `Array` of all rows as tuples matching molecule.
    * <br><br>
    * {{{
    *   Person.name.age.getArray.map(_ ==> Array(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getAsync
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArray(implicit
               conn: Future[Conn],
               ec: ExecutionContext,
               objType: ClassTag[Obj],
               tplType: ClassTag[Tpl]): Future[Array[Tpl]] = {
    conn.flatMap { conn =>
      if (conn.isJsPlatform) {
        Future.failed(new IllegalArgumentException("Please fetch `List`s of data with `get` instead."))
      } else {
        conn.query(_model, _query).map { jColl =>
          val it = jColl.iterator
          val a  = new Array[Tpl](jColl.size)
          var i  = 0
          while (it.hasNext) {
            a(i) = row2tpl(it.next)
            i += 1
          }
          a
        }
      }
    }
  }


  /** Get `Future` with `Array` of n rows as tuples matching molecule.
    * <br><br>
    * {{{
    *   Person.name.age.getArray(1).map(_ ==> Array(
    *     ("Ben", 42)
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples. Setting n to -1 fetches
    * all rows (same as calling `getArray` without any number of rows parameter).
    *
    * @group get
    * @param n       Number of rows
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArray(n: Int)
              (implicit
               conn: Future[Conn],
               ec: ExecutionContext,
               objType: ClassTag[Obj],
               tplType: ClassTag[Tpl]): Future[Array[Tpl]] = {
    conn.flatMap { conn2 =>
      if (conn2.isJsPlatform) {
        Future.failed(new IllegalArgumentException("Please fetch `List`s of data with `get` instead."))
      } else if (n == -1) {
        getArray(conn, ec, objType, tplType)
      } else {
        conn2.query(_model, _query).map { jColl =>
          val size = jColl.size
          val max  = if (size < n) size else n
          if (max == 0) {
            new Array[Tpl](0)
          } else {
            val it = jColl.iterator
            val a  = new Array[Tpl](max)
            var i  = 0
            while (it.hasNext && i < max) {
              a(i) = row2tpl(it.next)
              i += 1
            }
            a
          }
        }
      }
    }
  }

  // get as of ================================================================================================

  /** Get `Future` with `Array` of all rows as tuples matching molecule as of transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved in a getHistory call for an attribute and then be
    * used to get data as of that point in time (including that transaction):
    * {{{
    *   // Insert (t 1028)
    *   val List(ben, liz) = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37)
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
    *   // Get Array of data as of transaction t 1028 (after insert)
    *   Person.name.age.getArrayAsOf(1028).map(_ ==> Array(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of all rows as of transaction t 1031 (after update)
    *   Person.name.age.getArrayAsOf(1031).map(_ ==> Array(
    *     ("Ben", 43),
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of all rows as of transaction t 1032 (after retract)
    *   Person.name.age.getArrayAsOf(1032).map(_ ==> Array(
    *     ("Liz", 37)
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArrayAsOf
    * @param t       Transaction time t
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArrayAsOf(t: Long)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(conn.map(_.usingDbView(AsOf(TxLong(t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as tuples matching molecule as of transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved in a getHistory call for an attribute and then be
    * used to get data as of that point in time (including that transaction):
    * {{{
    *   // Insert (t 1028)
    *   val List(ben, liz) = Person.name.age insert List(
    *     ("Ben", 42),
    *     ("Liz", 37)
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
    *   // Get Array of all rows as of transaction t 1031 (after update)
    *   Person.name.age.getArrayAsOf(1031).map(_ ==> Array(
    *     ("Ben", 43),
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of n rows as of transaction t 1031 (after update)
    *   Person.name.age.getArrayAsOf(1031, 1).map(_ ==> Array(
    *     ("Ben", 43)
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    *
    * @group getArrayAsOf
    * @param t       Long Transaction time t
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArrayAsOf(t: Long, n: Int)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(n)(conn.map(_.usingDbView(AsOf(TxLong(t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of all rows as tuples matching molecule as of tx.
    * <br><br>
    * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve
    * a database value as of that transaction (including).
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
    *   // Retract (tx report 3)
    *   val tx3 = ben.retract
    *
    *   // Get Array of all rows as of tx1 (after insert)
    *   Person.name.age.getArrayAsOf(tx1).map(_ ==> Array(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of all rows as of tx2 (after update)
    *   Person.name.age.getArrayAsOf(tx2).map(_ ==> Array(
    *     ("Ben", 43), // Ben now 43
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of all rows as of tx3 (after retract)
    *   Person.name.age.getArrayAsOf(tx3).map(_ ==> Array(
    *     ("Liz", 37) // Ben gone
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArrayAsOf
    * @param tx      [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    * */
  def getArrayAsOf(tx: TxReport)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(conn.map(_.usingDbView(AsOf(TxLong(tx.t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as tuples matching molecule as of tx.
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
    *   // Get Array of all rows as of tx2 (after update)
    *   Person.name.age.getArrayAsOf(tx2).map(_ ==> Array(
    *     ("Ben", 43),
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of n rows as of tx2 (after update)
    *   Person.name.age.getArrayAsOf(tx2, 1).map(_ ==> Array(
    *     ("Ben", 43)
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    *
    * @group getArrayAsOf
    * @param tx      [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    * */
  def getArrayAsOf(tx: TxReport, n: Int)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(n)(conn.map(_.usingDbView(AsOf(TxLong(tx.t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of all rows as tuples matching molecule as of date.
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
    *   // Retract
    *   val tx3 = ben.retract
    *   val afterRetract = new java.util.Date
    *
    *   // No data yet before insert
    *   Person.name.age.getArrayAsOf(beforeInsert).map(_ ==> Array())
    *
    *   // Get Array of all rows as of afterInsert
    *   Person.name.age.getArrayAsOf(afterInsert).map(_ ==> Array(
    *     ("Ben", 42),
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of all rows as of afterUpdate
    *   Person.name.age.getArrayAsOf(afterUpdate).map(_ ==> Array(
    *     ("Ben", 43), // Ben now 43
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of all rows as of afterRetract
    *   Person.name.age.getArrayAsOf(afterRetract).map(_ ==> Array(
    *     ("Liz", 37) // Ben gone
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArrayAsOf
    * @param date    java.util.Date
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArrayAsOf(date: Date)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(conn.map(_.usingDbView(AsOf(TxDate(date)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as tuples matching molecule as of date.
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
    *   // Get Array of all rows as of afterUpdate
    *   Person.name.age.getArrayAsOf(afterUpdate).map(_ ==> Array(
    *     ("Ben", 43),
    *     ("Liz", 37)
    *   )
    *
    *   // Get Array of n rows as of afterUpdate
    *   Person.name.age.getArrayAsOf(afterUpdate, 1).map(_ ==> Array(
    *     ("Ben", 43)
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    *
    * @group getArrayAsOf
    * @param date    java.util.Date
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArrayAsOf(date: Date, n: Int)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(n)(conn.map(_.usingDbView(AsOf(TxDate(date)))), ec, objType, tplType)


  // get since ================================================================================================

  /** Get `Future` with `Array` of all rows as tuples matching molecule since transaction time `t`.
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
    *   Person.name.getArraySince(t1).map(_ ==> Array("Ben", "Cay"))
    *
    *   // Cay added since transaction time t2
    *   Person.name.getArraySince(t2).map(_ ==> Array("Cay"))
    *
    *   // Nothing added since transaction time t3
    *   Person.name.getArraySince(t3).map(_ ==> Nil)
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArraySince
    * @param t       Transaction time t
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArraySince(t: Long)
                   (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(conn.map(_.usingDbView(Since(TxLong(t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as tuples matching molecule since transaction time `t`.
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
    *   Person.name.getArraySince(t1).map(_ ==> Array("Ben", "Cay"))
    *
    *   // Ben and Cay added since transaction time t1 - only n (1) rows returned
    *   Person.name.getArraySince(t1, 1).map(_ ==> Array("Ben"))
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    *
    * @group getArraySince
    * @param t       Transaction time t
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArraySince(t: Long, n: Int)
                   (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(n)(conn.map(_.usingDbView(Since(TxLong(t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of all rows as tuples matching molecule since tx.
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
    *   Person.name.getArraySince(tx1).map(_ ==> Array("Ben", "Cay"))
    *
    *   // Cay added since tx2
    *   Person.name.getArraySince(tx2).map(_ ==> Array("Cay"))
    *
    *   // Nothing added since tx3
    *   Person.name.getArraySince(tx3).map(_ ==> Nil)
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArraySince
    * @param tx      [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArraySince(tx: TxReport)
                   (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(conn.map(_.usingDbView(Since(TxLong(tx.t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as tuples matching molecule since tx.
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
    *   Person.name.getArraySince(tx1).map(_ ==> Array("Ben", "Cay"))
    *
    *   // Ben and Cay added since tx1 - only n (1) rows returned
    *   Person.name.getArraySince(tx1, 1).map(_ ==> Array("Ben"))
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    *
    * @group getArraySince
    * @param tx      [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    * */
  def getArraySince(tx: TxReport, n: Int)
                   (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(n)(conn.map(_.usingDbView(Since(TxLong(tx.t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of all rows as tuples matching molecule since date.
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
    *   Person.name.getArraySince(date1).map(_ ==> Array("Ben", "Cay"))
    *
    *   // Cay added since date2
    *   Person.name.getArraySince(date2).map(_ ==> Array("Cay"))
    *
    *   // Nothing added since date3
    *   Person.name.getArraySince(date3).map(_ ==> Nil)
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArraySince
    * @param date    java.util.Date
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArraySince(date: Date)
                   (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(conn.map(_.usingDbView(Since(TxDate(date)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as tuples matching molecule since date.
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
    *   Person.name.getArraySince(date1).map(_ ==> Array("Ben", "Cay"))
    *
    *   // Ben and Cay added since date1 - only n (1) rows returned
    *   Person.name.getArraySince(date1, 1).map(_ ==> Array("Ben"))
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    *
    * @group getArraySince
    * @param date    java.util.Date
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArraySince(date: Date, n: Int)
                   (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(n)(conn.map(_.usingDbView(Since(TxDate(date)))), ec, objType, tplType)



  // get with ================================================================================================

  /** Get `Future` with `Array` of all rows as tuples matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Current state
    *   val ben = Person.name("Ben").likes("pasta").save.eid
    *
    *   // Test adding transaction data
    *   Person.name.likes.getArrayWith(
    *     // Additional transaction data
    *     Person(ben).likes("sushi").getUpdateTx
    *   ).map(_ ==> Array(
    *     // Effect: Ben would like sushi if tx was applied
    *     ("Ben", "sushi")
    *   )
    *
    *   // Current state is still the same
    *   Person.name.likes.get.map(_ ==> Array()
    *     ("Ben", "pasta")
    *   )
    * }}}
    * Multiple transactions can be applied to test more complex what-if scenarios!
    * <br><br>
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArrayWith
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType     Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArrayWith(txMolecules: Future[Seq[Statement]]*)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      getArray(conn.map(conn2 => conn2.usingDbView(With(conn2.stmts2java(stmtss.flatten)))), ec, objType, tplType)
    }
  }


  /** Get `Future` with `Array` of n rows as tuples matching molecule with applied molecule transaction data.
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
    *   Person.name.likes.getArrayWith(
    *     // Additional transaction data
    *     Person(ben).likes("sushi").getUpdateTx,
    *     Person(liz).likes("cake").getUpdateTx
    *   ).map(_ ==> Array(
    *     ("Ben", "sushi")
    *     ("Liz", "cake")
    *   )
    *
    *   // Same as above, but only n (1) rows returned:
    *   Person.name.likes.getArrayWith(
    *     1
    *     Person(ben).likes("sushi").getUpdateTx,
    *     Person(liz).likes("cake").getUpdateTx
    *   ).map(_ ==> Array(
    *     ("Ben", "sushi")
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    *
    * @group getArrayWith
    * @param n           Int Number of rows returned
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType     Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    * @note Note how the `n` parameter has to come before the `txMolecules` vararg.
    */
  def getArrayWith(n: Int, txMolecules: Future[Seq[Statement]]*)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      getArray(n)(conn.map(conn2 => conn2.usingDbView(With(conn2.stmts2java(stmtss.flatten)))), ec, objType, tplType)
    }
  }


  /** Get `Future` with `Array` of all rows as tuples matching molecule with applied raw transaction data.
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
    *   Person.name.getArrayWith(newDataTx).map(_.size ==> 250)
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArrayWith
    * @param txData  Raw transaction data as java.util.List[Object]
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArrayWith(txData: jList[_])
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(conn.map(_.usingDbView(With(txData.asInstanceOf[jList[jList[_]]]))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as tuples matching molecule with applied raw transaction data.
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
    *   Person.name.getArrayWith(newDataTx).map(_.size ==> 250)
    *
    *   // Imagine future db - Let's just take 10
    *   Person.name.getArrayWith(newDataTx, 10).map(_.size ==> 10)
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted tuples.
    *
    * @group getArrayWith
    * @param txData  Raw transaction data as java.util.List[Object]
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Tpl]` to capture Tuple type for Array
    * @return `Future[Array[Tpl]]` where Tpl is a tuple of types matching the attributes of the molecule
    */
  def getArrayWith(txData: jList[_], n: Int)
                  (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Tpl]] =
    getArray(n)(conn.map(_.usingDbView(With(txData.asInstanceOf[jList[jList[_]]]))), ec, objType, tplType)


  // get history ================================================================================================

  // Only `getHistory`/`getHistory` returning List/Future[List] are implemented since it is only meaningful
  // to track the history of one attribute of one entity at a time and a sortable List is therefore preferred.

}
