package molecule.core.api

import java.util.{Date, List => jList}
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.ast.tempDb._
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.reflect.ClassTag


/** Asynchronous data getter methods on molecules returning `Future[Array[Obj]]`.
  * <br><br>
  * The fastest way of getting a large typed data set since data is applied to a super fast pre-allocated Array.
  * The Array can then be traversed with a fast `while` loop.
  * {{{
  *   Ns.int.insert(1, 2, 3)
  *
  *   // Map over Future
  *   Ns.int.getAsyncObjArray.map { rowObjects =>
  *     rowObjects.map(_.int) === Array(1, 2, 3)
  *
  *     // Fast while loop
  *     var i = 0
  *     val length = rowObjects.length
  *     while(i < length) {
  *       println(rowObjects(i).int) // Do stuff with row object...
  *       i += 1
  *     }
  *   }
  * }}}
  * Each asynchronous getter in this package simply wraps the result of its equivalent synchronous getter (in the
  * `get` package) in a Future. `getAsyncObjArrayAsOf` thus wraps the result of `getObjArrayAsOf` in a Future and so on.
  * */
trait GetObjArray[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>


  // get ================================================================================================

  /** Get `Future` with `Array` of all rows as objects matching molecule.
    * {{{
    *   val persons = Person.name.age.getObjArray
    *
    *   // Fast while loop
    *   var i = 0
    *   val length = persons.length
    *   while(i < length) {
    *     val p = persons(i)
    *     println(s"${p.name} is ${p.age} years old") // Do stuff with row object...
    *     i += 1
    *   }
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getAsync
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArray(implicit 
                  conn: Future[Conn], 
                  ec: ExecutionContext,
                  objType: ClassTag[Obj],
                  tplType: ClassTag[Tpl]): Future[Array[Obj]] = {
    conn.flatMap { conn =>
      if (conn.isJsPlatform) {
        Future.failed(new IllegalArgumentException("Please fetch `List`s of data with `get` instead."))
      } else {
        conn.query(_model, _query).map { jColl =>
          val it = jColl.iterator
          val a  = new Array[Obj](jColl.size)
          var i  = 0
          while (it.hasNext) {
            a(i) = row2obj(it.next)
            i += 1
          }
          a
        }
      }
    }
  }
  

  /** Get `Future` with `Array` of n rows as objects matching molecule.
    * {{{
    *   Person.name.age.getObjArray(1).map(p => s"${p.name} is ${p.age} years old")).map(_ ==> List(
    *     "Ben is 42 years old"
    *   )
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects. Setting n to -1 fetches
    * all rows (same as calling `getObjArray` without any number of rows parameter).
    *
    * @group get
    * @param n       Number of rows
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArray(n: Int)
                 (implicit conn: Future[Conn], 
                  ec: ExecutionContext,
                  objType: ClassTag[Obj],
                  tplType: ClassTag[Tpl]): Future[Array[Obj]] = {
    conn.flatMap { conn2 =>
      if (conn2.isJsPlatform) {
        Future.failed(new IllegalArgumentException("Please fetch `List`s of data with `get` instead."))
      } else {
        if (n == -1) {
          getObjArray(conn, ec, objType, tplType)
        } else {
          conn2.query(_model, _query).map { jColl =>
            val size = jColl.size
            val max  = if (size < n) size else n
            if (max == 0) {
              new Array[Obj](0)
            } else {
              val it = jColl.iterator
              val a  = new Array[Obj](max)
              var i  = 0
              while (it.hasNext && i < max) {
                a(i) = row2obj(it.next)
                i += 1
              }
              a
            }
          }
        }
      }
    }
  }
  

  // get as of ================================================================================================

  /** Get `Future` with `Array` of all rows as objects matching molecule as of transaction time `t`.
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
    *   val persons = Person.name.age.getObjArrayAsOf(1028)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 42
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of data as of transaction t 1031 (after update)
    *   val persons = Person.name.age.getObjArrayAsOf(1031)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of all rows as of transaction t 1032 (after retract)
    *   val persons = Person.name.age.getObjArrayAsOf(1032)
    *   persons(0).name === "Liz"
    *   persons(0).age  === 37
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the typed data set.
    *
    * @group getArrayAsOf
    * @param t       Transaction time t
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArrayAsOf(t: Long)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(conn.map(_.usingTempDb(AsOf(TxLong(t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as objects matching molecule as of transaction time `t`.
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
    *   val persons = Person.name.age.getObjArrayAsOf(1031)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of n rows as of transaction t 1031 (after update)
    *   val persons = Person.name.age.getObjArrayAsOf(1031)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects.
    *
    * @group getArrayAsOf
    * @param t       Long Transaction time t
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArrayAsOf(t: Long, n: Int)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(n)(conn.map(_.usingTempDb(AsOf(TxLong(t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of all rows as objects matching molecule as of tx.
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
    *   val persons = Person.name.age.getObjArrayAsOf(tx1)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 42
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of all rows as of tx2 (after update)
    *   val persons = Person.name.age.getObjArrayAsOf(tx2)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of all rows as of tx3 (after retract)
    *   val persons = Person.name.age.getObjArrayAsOf(tx3)
    *   persons(0).name === "Liz"
    *   persons(0).age  === 37
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArrayAsOf
    * @param tx      [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    * */
  def getObjArrayAsOf(tx: TxReport)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(conn.map(_.usingTempDb(AsOf(TxLong(tx.t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as objects matching molecule as of tx.
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
    *   val persons = Person.name.age.getObjArrayAsOf(tx2)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of n rows as of tx2 (after update)
    *   val persons = Person.name.age.getObjArrayAsOf(tx2, 1)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects.
    *
    * @group getArrayAsOf
    * @param tx      [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    * */
  def getObjArrayAsOf(tx: TxReport, n: Int)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(n)(conn.map(_.usingTempDb(AsOf(TxLong(tx.t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of all rows as objects matching molecule as of date.
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
    *   Person.name.age.getObjArrayAsOf(beforeInsert).map(_ ==> Array())
    *
    *   // Get Array of all rows as of afterInsert
    *   val persons = Person.name.age.getObjArrayAsOf(afterInsert)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 42
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of all rows as of afterUpdate
    *   val persons = Person.name.age.getObjArrayAsOf(afterUpdate)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of all rows as of afterRetract
    *   val persons = Person.name.age.getObjArrayAsOf(afterRetract)
    *   persons(0).name === "Liz"
    *   persons(0).age  === 37
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArrayAsOf
    * @param date    java.util.Date
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArrayAsOf(date: Date)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(conn.map(_.usingTempDb(AsOf(TxDate(date)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as objects matching molecule as of date.
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
    *   val persons = Person.name.age.getObjArrayAsOf(afterUpdate)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    *   persons(1).name === "Liz"
    *   persons(1).age  === 37
    *
    *   // Get Array of n rows as of afterUpdate
    *   val persons = Person.name.age.getObjArrayAsOf(afterUpdate, 1)
    *   persons(0).name === "Ben"
    *   persons(0).age  === 43 // <-- updated
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects.
    *
    * @group getArrayAsOf
    * @param date    java.util.Date
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArrayAsOf(date: Date, n: Int)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(n)(conn.map(_.usingTempDb(AsOf(TxDate(date)))), ec, objType, tplType)


  // get since ================================================================================================

  /** Get `Future` with `Array` of all rows as objects matching molecule since transaction time `t`.
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
    *   val persons = Person.name.getObjArray
    *   persons(0).name === "Ann"
    *   persons(1).name === "Ben"
    *   persons(2).name === "Cay"
    *
    *   // Ben and Cay added since transaction time t1
    *   val persons = Person.name.getObjArraySince(t1)
    *   persons(0).name === "Ben"
    *   persons(1).name === "Cay"
    *
    *   // Cay added since transaction time t2
    *   val persons = Person.name.getObjArraySince(t2)
    *   persons(0).name === "Cay"
    *
    *   // Nothing added since transaction time t3
    *   Person.name.getObjArraySince(t3).map(_ ==> Array())
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArraySince
    * @param t       Transaction time t
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArraySince(t: Long)
                      (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(conn.map(_.usingTempDb(Since(TxLong(t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as objects matching molecule since transaction time `t`.
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
    *   val persons = Person.name.getObjArray
    *   persons(0).name === "Ann"
    *   persons(1).name === "Ben"
    *   persons(2).name === "Cay"
    *
    *   // Ben and Cay added since transaction time t1
    *   val persons = Person.name.getObjArraySince(t1)
    *   persons(0).name === "Ben"
    *   persons(1).name === "Cay"
    *
    *   // Ben and Cay added since transaction time t1 - only n (1) rows returned
    *   val persons = Person.name.getObjArraySince(t1, 1)
    *   persons(0).name === "Ben"
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects.
    *
    * @group getArraySince
    * @param t       Transaction time t
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArraySince(t: Long, n: Int)
                      (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(n)(conn.map(_.usingTempDb(Since(TxLong(t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of all rows as objects matching molecule since tx.
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
    *   val persons = Person.name.getObjArray
    *   persons(0).name === "Ann"
    *   persons(1).name === "Ben"
    *   persons(2).name === "Cay"
    *
    *   // Ben and Cay added since transaction time tx1
    *   val persons = Person.name.getObjArraySince(tx1)
    *   persons(0).name === "Ben"
    *   persons(1).name === "Cay"
    *
    *   // Cay added since transaction time tx2
    *   val persons = Person.name.getObjArraySince(tx2)
    *   persons(0).name === "Cay"
    *
    *   // Nothing added since transaction time tx3
    *   Person.name.getObjArraySince(tx3).map(_ ==> Array())
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArraySince
    * @param tx      [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArraySince(tx: TxReport)
                      (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(conn.map(_.usingTempDb(Since(TxLong(tx.t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as objects matching molecule since tx.
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
    *   val persons = Person.name.getObjArray
    *   persons(0).name === "Ann"
    *   persons(1).name === "Ben"
    *   persons(2).name === "Cay"
    *
    *   // Ben and Cay added since tx1
    *   val persons = Person.name.getObjArraySince(tx1)
    *   persons(0).name === "Ben"
    *   persons(1).name === "Cay"
    *
    *   // Ben and Cay added since tx1 - only n (1) rows returned
    *   val persons = Person.name.getObjArraySince(tx1, 1)
    *   persons(0).name === "Ben"
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects.
    *
    * @group getArraySince
    * @param tx      [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    * */
  def getObjArraySince(tx: TxReport, n: Int)
                      (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(n)(conn.map(_.usingTempDb(Since(TxLong(tx.t)))), ec, objType, tplType)


  /** Get `Future` with `Array` of all rows as objects matching molecule since date.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * {{{
    *   // Transact 3 times (`inst` retrieves transaction time/Date from tx report)
    *   val date1 = Person.name("Ann").save.inst
    *   val date2 = Person.name("Ben").save.inst
    *   val date3 = Person.name("Cay").save.inst
    *
    *   // Current values
    *   val persons = Person.name.getObjArray
    *   persons(0).name === "Ann"
    *   persons(1).name === "Ben"
    *   persons(2).name === "Cay"
    *
    *   // Ben and Cay added since date1
    *   val persons = Person.name.getObjArraySince(date1)
    *   persons(0).name === "Ben"
    *   persons(1).name === "Cay"
    *
    *   // Cay added since transaction time date2
    *   val persons = Person.name.getObjArraySince(date2)
    *   persons(0).name === "Cay"
    *
    *   // Nothing added since transaction time date3
    *   Person.name.getObjArraySince(date3).map(_ ==> Array())
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArraySince
    * @param date    java.util.Date
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArraySince(date: Date)
                      (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(conn.map(_.usingTempDb(Since(TxDate(date)))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as objects matching molecule since date.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * {{{
    *   // Transact 3 times (`inst` retrieves transaction time/Date from tx report)
    *   val date1 = Person.name("Ann").save.inst
    *   val date2 = Person.name("Ben").save.inst
    *   val date3 = Person.name("Cay").save.inst
    *
    *   // Current values
    *   val persons = Person.name.getObjArray
    *   persons(0).name === "Ann"
    *   persons(1).name === "Ben"
    *   persons(2).name === "Cay"
    *
    *   // Ben and Cay added since date1
    *   val persons = Person.name.getObjArraySince(date1)
    *   persons(0).name === "Ben"
    *   persons(1).name === "Cay"
    *
    *   // Ben and Cay added since date1 - only n (1) rows returned
    *   val persons = Person.name.getObjArraySince(date1, 1)
    *   persons(0).name === "Ben"
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects.
    *
    * @group getArraySince
    * @param date    java.util.Date
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArraySince(date: Date, n: Int)
                      (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(n)(conn.map(_.usingTempDb(Since(TxDate(date)))), ec, objType, tplType)


  // get with ================================================================================================

  /** Get `Future` with `Array` of all rows as objects matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Current state
    *   val ben = Person.name("Ben").likes("pasta").save.eid
    *
    *   val persons = Person.name.likes.getObjArray
    *   persons(0).name  === "Ben"
    *   persons(0).likes === "pasta"
    *
    *   // Test adding transaction data
    *   val personsTest = Person.name.likes.getObjArrayWith(
    *     // Additional transaction data
    *     Person(ben).likes("sushi").getUpdateTx
    *   )
    *
    *   // Effect: Ben would like sushi if tx was applied
    *   personTest(0).name  === "Ben"
    *   personTest(0).likes === "sushi"
    *
    *   // Current state is still the same
    *   val personsAfter = Person.name.likes.getObjArray
    *   personsAfter(0).name  === "Ben"
    *   personsAfter(0).likes === "pasta"
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
    * @param tplType     Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArrayWith(txMolecules: Future[Seq[Statement]]*)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      getObjArray(conn.map(conn2 => conn2.usingTempDb(With(conn2.stmts2java(stmtss.flatten)))), ec, objType, tplType)
    }
  }


  /** Get `Future` with `Array` of n rows as objects matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Current state
    *   val List(ben, liz) = Person.name.likes.insert(
    *     ("Ben", "pasta"),
    *     ("Liz", "pizza")
    *   ).eids
    *
    *   // Current state
    *   val persons = Person.name.likes.getObjArray
    *   persons(0).name  === "Ben"
    *   persons(0).likes === "pasta"
    *   persons(1).name  === "Liz"
    *   persons(1).likes === "pizza"
    *
    *   // Test adding transaction data
    *   val personsTest = Person.name.likes.getObjArrayWith(
    *     Person(ben).likes("sushi").getUpdateTx,
    *     Person(liz).likes("cake").getUpdateTx
    *   )
    *   // Effect: sushi and cake now liked
    *   personsTest(0).name  === "Ben"
    *   personsTest(0).likes === "sushi"
    *   personsTest(1).name  === "Liz"
    *   personsTest(1).likes === "cake"
    *
    *   // Same as above, but only n (1) rows returned:
    *   val personsTest1 = Person.name.likes.getObjArrayWith(
    *     1
    *     Person(ben).likes("sushi").getUpdateTx,
    *     Person(liz).likes("cake").getUpdateTx
    *   )
    *   // Effect: sushi and cake now liked (but only Ben returned)
    *   personsTest1(0).name  === "Ben"
    *   personsTest1(0).likes === "sushi"
    *
    *   // Current state is still the same
    *   val personsAfter = Person.name.likes.getObjArray
    *   personsAfter(0).name  === "Ben"
    *   personsAfter(0).likes === "pasta"
    *   personsAfter(1).name  === "Liz"
    *   personsAfter(1).likes === "pizza"
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects.
    *
    * @group getArrayWith
    * @param n           Int Number of rows returned
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType     Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    * @note Note how the `n` parameter has to come before the `txMolecules` vararg.
    */
  def getObjArrayWith(n: Int, txMolecules: Future[Seq[Statement]]*)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      getObjArray(n)(conn.map(conn2 => conn2.usingTempDb(With(conn2.stmts2java(stmtss.flatten)))), ec, objType, tplType)
    }
  }


  /** Get `Future` with `Array` of all rows as objects matching molecule with applied raw transaction data.
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
    *   Person.name.getObjArrayWith(newDataTx).map(_.size ==> 250)
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    *
    * @group getArrayWith
    * @param txData  Raw transaction data as java.util.List[Object]
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArrayWith(txData: jList[_])
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(conn.map(_.usingTempDb(With(txData.asInstanceOf[jList[jList[_]]]))), ec, objType, tplType)


  /** Get `Future` with `Array` of n rows as objects matching molecule with applied raw transaction data.
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
    *   Person.name.getObjArrayWith(newDataTx).map(_.size ==> 250)
    *
    *   // Imagine future db - Let's just take 10
    *   Person.name.getObjArrayWith(newDataTx, 10).map(_.size ==> 10)
    * }}}
    * Getting a pre-allocated Array populated with typed data is the fastest way to query
    * Datomic with Molecule. Looping the Array in a while loop with a mutable index pointer will
    * also be the fastest way to traverse the data set.
    * <br><br>
    * The Array is only populated with n rows of type-casted objects.
    *
    * @group getArrayWith
    * @param txData  Raw transaction data as java.util.List[Object]
    * @param n       Int Number of rows returned
    * @param conn    Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @param tplType Implicit `ClassTag[Obj]` to capture the object type for Array
    * @return `Future[Array[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjArrayWith(txData: jList[_], n: Int)
                     (implicit conn: Future[Conn], ec: ExecutionContext, objType: ClassTag[Obj], tplType: ClassTag[Tpl]): Future[Array[Obj]] =
    getObjArray(n)(conn.map(_.usingTempDb(With(txData.asInstanceOf[jList[jList[_]]]))), ec, objType, tplType)


  // get history ================================================================================================

  // Only `getHistory`/`getHistory` returning List/Future[List] are implemented since it is only meaningful
  // to track the history of one attribute of one entity at a time and a sortable List is therefore preferred.

}
