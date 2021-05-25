package molecule.core.api

import java.util.{Date, Collection => jCollection, Iterator => jIterator, List => jList}
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.ast.tempDb._
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions


/** Asynchronous data getter methods on molecules returning `Future[Iterable[Obj]]`.
  * <br><br>
  * Suitable for data sets that are lazily consumed.
  * {{{
  *   for {
  *     personsIterable <- Person.name.age.getAsyncObjIterable
  *   } yield {
  *     val firstPerson = personsIterable.iterator.next
  *     firstPerson.name === "Ben"
  *     firstPerson.age  === 42
  *   }
  * }}}
  * Each asynchronous getter in this package simply wraps the result of its equivalent synchronous getter in a Future. 
  * `getAsyncObjIterableAsOf` thus wraps the result of `getObjIterableAsOf` in a Future and so on.
  * */
trait GetObjIterable[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>


  // get ================================================================================================

  /** Get `Future` with `Iterable` of all rows as objects matching the molecule.
    * <br><br>
    * Objects are lazily type-casted on each call to iterator.next().
    * {{{
    *   val person = Person.name.age.getObjIterable.next
    *   person.name === "Ben"
    *   person.age === 42
    * }}}
    *
    * @group getAsync
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[Iterable[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjIterable(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] = {
    if (conn.isJsPlatform) {
      Future.failed(new IllegalArgumentException("Please fetch `List`s of data with `get` instead."))
    } else {
      conn.query(_model, _query).map(res =>
        new Iterable[Obj] {
          private val jColl: jCollection[jList[AnyRef]] = res
          override def isEmpty: Boolean = jColl.isEmpty
          override def size: Int = jColl.size
          override def iterator: Iterator[Obj] = new Iterator[Obj] {
            private val jIter: jIterator[jList[AnyRef]] = jColl.iterator
            override def hasNext: Boolean = jIter.hasNext
            override def next(): Obj = row2obj(jIter.next())
          }
        }
      )
    }
  }


  // get as of ================================================================================================

  /** Get `Future` with `Iterable` of all rows as objects matching molecule as of transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * Call `getObjIterableAsOf` for large result sets to maximize runtime performance.
    * Data is lazily type-casted on each call to `next` on the iterator.
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
    *   Person(ben).age.t.op.getHistory.sortBy(r => (r._2, r._3)) === List(
    *     (42, 1028, true),  // Insert:  42 asserted
    *     (42, 1031, false), // Update:  42 retracted
    *     (43, 1031, true),  //          43 asserted
    *     (43, 1032, false)  // Retract: 43 retracted
    *   )
    *
    *   // Get Iterable of all rows as of transaction t 1028 (after insert)
    *   val iterable1: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(1028)
    *   val iterator1: Iterator[(String, Int)] = iterable1.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator1.next === ("Liz", 37)
    *   iterator1.next === ("Ben", 42)
    *
    *   // Get Iterable of all rows as of transaction t 1031 (after update)
    *   val iterable2: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(1031)
    *   val iterator2: Iterator[(String, Int)] = iterable2.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator2.next === ("Liz", 37)
    *   iterator2.next === ("Ben", 43) // Ben now 43
    *
    *   // Get Iterable of all rows as of transaction t 1032 (after retract)
    *   val iterable3: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(1032)
    *   val iterator3: Iterator[(String, Int)] = iterable3.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator3.next === ("Liz", 37)
    *   iterator3.hasNext === false // Ben gone
    * }}}
    *
    * @group getIterableAsOf
    * @param t    Transaction time t
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[Iterable[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjIterableAsOf(t: Long)(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] =
    getObjIterable(conn.usingTempDb(AsOf(TxLong(t))), ec)


  /** Get `Future` with `Iterable` of all rows as objects matching molecule as of tx.
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
    *   // Get Iterable of all rows as of tx1 (after insert)
    *   val iterable1: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(tx1)
    *   val iterator1: Iterator[(String, Int)] = iterable1.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator1.next === ("Ben", 42)
    *   iterator1.next === ("Liz", 37)
    *
    *   // Get Iterable of all rows as of tx2 (after update)
    *   val iterable2: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(tx2)
    *   val iterator2: Iterator[(String, Int)] = iterable2.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator2.next === ("Ben", 43) // Ben now 43
    *   iterator2.next === ("Liz", 37)
    *
    *   // Get Iterable of all rows as of tx3 (after retract)
    *   val iterable3: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(tx3)
    *   val iterator3: Iterator[(String, Int)] = iterable3.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator3.next === ("Liz", 37)
    *   iterator3.hasNext === false // Ben gone
    * }}}
    *
    * @group getIterableAsOf
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[Iterable[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjIterableAsOf(tx: TxReport)(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] =
    getObjIterable(conn.usingTempDb(AsOf(TxLong(tx.t))), ec)


  /** Get `Future` with `Iterable` of all rows as objects matching molecule as of date.
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
    *   // Get Iterable of all rows as of beforeInsert
    *   val iterable0: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(beforeInsert)
    *   val iterator0: Iterator[(String, Int)] = iterable0.iterator
    *   iterator0.hasNext === false // Nothing yet
    *
    *   // Get Iterable of all rows as of afterInsert
    *   val iterable1: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(afterInsert)
    *   val iterator1: Iterator[(String, Int)] = iterable1.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator1.next === ("Ben", 42)
    *   iterator1.next === ("Liz", 37)
    *
    *   // Get Iterable of all rows as of afterUpdate
    *   val iterable2: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(afterUpdate)
    *   val iterator2: Iterator[(String, Int)] = iterable2.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator2.next === ("Ben", 43) // Ben now 43
    *   iterator2.next === ("Liz", 37)
    *
    *   // Get Iterable of all rows as of afterRetract
    *   val iterable3: Iterable[(String, Int)] = Person.name.age.getObjIterableAsOf(afterRetract)
    *   val iterator3: Iterator[(String, Int)] = iterable3.iterator
    *
    *   // Type casting lazily performed with each call to `next`
    *   iterator3.next === ("Liz", 37)
    *   iterator3.hasNext === false // Ben gone
    * }}}
    *
    * @group getIterableAsOf
    * @param date java.util.Date
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[Iterable[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjIterableAsOf(date: Date)(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] =
    getObjIterable(conn.usingTempDb(AsOf(TxDate(date))), ec)


  // get since ================================================================================================

  /** Get `Future` with `Iterable` of all rows as objects matching molecule since transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * Call `getObjIterableSince` for large result sets to maximize runtime performance.
    * Data is lazily type-casted on each call to `next` on the iterator.
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
    *   Person.name.getObjIterable.iterator.toList === List("Ann", "Ben", "Cay")
    *
    *   // Ben and Cay added since transaction time t1
    *   Person.name.getObjIterableSince(t1).iterator.toList === List("Ben", "Cay")
    *
    *   // Cay added since transaction time t2
    *   Person.name.getObjIterableSince(t2).iterator.toList === List("Cay")
    *
    *   // Nothing added since transaction time t3
    *   Person.name.getObjIterableSince(t3).iterator.toList === Nil
    * }}}
    *
    * @group getIterableSince
    * @param t    Transaction time t
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[Iterable[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjIterableSince(t: Long)(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] =
    getObjIterable(conn.usingTempDb(Since(TxLong(t))), ec)


  /** Get `Future` with `Iterable` of all rows as objects matching molecule since tx.
    * <br><br>
    * Datomic's internal `since` method can take a transaction entity id as argument to retrieve a database
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
    *   Person.name.getObjIterable.iterator.toList === List("Ann", "Ben", "Cay")
    *
    *   // Ben and Cay added since tx1
    *   Person.name.getObjIterableSince(tx1).iterator.toList === List("Ben", "Cay")
    *
    *   // Cay added since tx2
    *   Person.name.getObjIterableSince(tx2).iterator.toList === List("Cay")
    *
    *   // Nothing added since tx3
    *   Person.name.getObjIterableSince(tx3).iterator.toList === Nil
    * }}}
    *
    * @group getIterableSince
    * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[Iterable[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjIterableSince(tx: TxReport)(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] =
    getObjIterable(conn.usingTempDb(Since(TxLong(tx.t))), ec)


  /** Get `Future` with `Iterable` of all rows as objects matching molecule since date.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * {{{
    *   // Transact 3 times (`inst` retrieves transaction time from tx report)
    *   val date1 = Person.name("Ann").save.inst
    *   val date2 = Person.name("Ben").save.inst
    *   val date3 = Person.name("Cay").save.inst
    *
    *   // Current values
    *   Person.name.getObjIterable.iterator.toList === List("Ann", "Ben", "Cay")
    *
    *   // Ben and Cay added since date1
    *   Person.name.getObjIterableSince(date1).iterator.toList === List("Ben", "Cay")
    *
    *   // Cay added since date2
    *   Person.name.getObjIterableSince(date2).iterator.toList === List("Cay")
    *
    *   // Nothing added since date3
    *   Person.name.getObjIterableSince(date3).iterator.toList === Nil
    * }}}
    *
    * @group getIterableSince
    * @param date java.util.Date
    * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return `Future[Iterable[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
    */
  def getObjIterableSince(date: Date)(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] =
    getObjIterable(conn.usingTempDb(Since(TxDate(date))), ec)


  // get with ================================================================================================

  /** Get `Future` with `Iterable` of all rows as objects matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Current state
    *   val ben = Person.name("Ben").likes("pasta").save.eid
    *
    *   // Base data
    *   Person.name.likes.getObjIterableWith(
    *     // apply imaginary transaction data
    *     Person(ben).likes("sushi").getUpdateTx
    *   ).iterator.toList === List(
    *     // Effect: Ben would like sushi if tx was applied
    *     ("Ben", "sushi")
    *   )
    *
    *   // Current state is still the same
    *   Person.name.likes.get === List(
    *     ("Ben", "pasta")
    *   )
    * }}}
    * Multiple transactions can be applied to test more complex what-if scenarios!
    *
    * @group getIterableWith
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return Iterable of molecule data
    */
  def getObjIterableWith(txMolecules: Future[Seq[Statement]]*)(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      getObjIterable(conn.usingTempDb(With(conn.stmts2java(stmtss.flatten))), ec)
    }
  }


  /** Get `Future` with `Iterable` of all rows as objects matching molecule with applied raw transaction data.
    * <br><br>
    * Apply raw transaction data to in-memory "branch" of db without affecting db to see how it would then look:
    * {{{
    *   // Live size of Person db
    *   Person.name.get.size === 150
    *
    *   // Read some transaction data from file
    *   val data_rdr2 = new FileReader("examples/resources/seattle/seattle-data1a.dtm")
    *   val newDataTx = Util.readAll(data_rdr2).get(0).asInstanceOf[java.util.List[Object]]
    *
    *   // Imagine future db - 100 persons would be added, apparently
    *   Person.name.getObjIterableWith(newDataTx).size === 250
    * }}}
    *
    * @group getIterableWith
    * @param txData Raw transaction data as java.util.List[Object]
    * @param conn   Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return Iterable of molecule data
    */
  def getObjIterableWith(txData: jList[_])(implicit conn: Conn, ec: ExecutionContext): Future[Iterable[Obj]] =
    getObjIterable(conn.usingTempDb(With(txData.asInstanceOf[jList[jList[_]]])), ec)


  // get history ================================================================================================

  // Only `getHistory`/`getHistory` returning List/Future[List] are implemented since it is only meaningful
  // to track the history of one attribute of one entity at a time and a sortable List is therefore preferred.

}
