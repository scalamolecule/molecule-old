package molecule.core.api

import java.util.Date
import molecule.core.marshalling.Marshalling
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}


/** Data getter methods on molecules that return data as Lists of objects with attribute properties.
 * <br><br>
 * Attributes names are used as object property names.
 * */
trait GetObjs[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  // get ================================================================================================

  /** Get Future with List of all rows as objects matching a molecule.
   * <br><br>
   * {{{
   * for {
   *   List(p1, p2) <- Person.name.age.getObjs
   *   _ = p1.name ==> "Ben"
   *   _ = p1.age  ==> 42
   *   _ = p2.name ==> "Liz"
   *   _ = p2.age  ==> 37
   * } yield ()
   * }}}
   *
   * @group getAsync
   * @param futConn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjs(implicit futConn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] = {
    _inputThrowable.fold(
      futConn.flatMap { conn =>
        if (conn.isJsPlatform) {
          conn.jsQueryObj(_model, _query, _datalog, -1, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, packed2obj)
        } else {
          conn.jvmQuery(_model, _query).map { jColl =>
            val it   = jColl.iterator
            val list = List.newBuilder[Obj]
            while (it.hasNext) {
              list += row2obj(it.next)
            }
            list.result()
          }
        }
      }
    )(Future.failed) // Pass on exception from input failure
  }

  /** Get Future with List of n rows as objects matching a molecule.
   * {{{
   * for {
   *   List(p1) <- Person.name.age.getObjs(1)
   *   _ = p1.name ==> "Ben"
   *   _ = p1.age  ==> 42
   * } yield ()
   * }}}
   *
   * @group get
   * @param n       Int Number of rows returned
   * @param futConn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjs(n: Int)(implicit futConn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] = {
    _inputThrowable.fold(
      futConn.flatMap { conn =>
        if (conn.isJsPlatform) {
          conn.jsQueryObj(_model, _query, _datalog, n, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes, packed2obj)
        } else {
          if (n == -1) {
            getObjs(futConn, ec)
          } else {
            conn.jvmQuery(_model, _query).map { jColl =>
              val size = jColl.size
              val max  = if (size < n) size else n
              if (max == 0) {
                List.empty[Obj]
              } else {
                val it   = jColl.iterator
                val list = List.newBuilder[Obj]
                var i    = 0
                while (it.hasNext && i < max) {
                  list += row2obj(it.next)
                  i += 1
                }
                list.result()
              }
            }
          }
        }
      }
    )(Future.failed) // Pass on exception from input failure
  }

  /** Convenience method to get head of list of objects matching a molecule.
   * {{{
   * for {
   *   person <- Person.name.age.getObj
   *   _ = person.name ==> "Ben"
   *   _ = person.age  ==> 42
   * } yield ()
   * }}}
   *
   * @group get
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return List[Obj] where Obj is an object with properties matching the attributes of the molecule
   */
  def getObj(implicit conn: Future[Conn], ec: ExecutionContext): Future[Obj] =
    getObjs(conn, ec).flatMap(
      _.headOption.fold[Future[Obj]](
        Future.failed(new RuntimeException("Empty result set."))
      )(Future(_))
    )

  // get as of ================================================================================================

  /** Get Future with List of all rows as objects matching a molecule as of transaction time `t`.
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
   *   _ <- Person(ben).age.t.op.getHistory.sortBy(r => (r._2, r._3)).map(_ ==> List(
   *     (42, 1028, true),  // Insert:  42 asserted
   *     (42, 1031, false), // Update:  42 retracted
   *     (43, 1031, true),  //          43 asserted
   *     (43, 1032, false)  // Retract: 43 retracted
   *   ))
   *
   *   // Get List of data as of transaction t 1028 (after insert)
   *   List(a1, a2) <- Person.name.age.getObjsAsOf(1028)
   *   _ = a1.name ==> "Ben"
   *   _ = a1.age  ==> 42
   *   _ = a2.name ==> "Liz"
   *   _ = a2.age  ==> 37
   *
   *   // Get List of data as of transaction t 1031 (after update)
   *   List(b1, b2) <- Person.name.age.getObjsAsOf(1031)
   *   _ = b1.name ==> "Ben"
   *   _ = b1.age  ==> 43 // <-- updated
   *   _ = b2.name ==> "Liz"
   *   _ = b2.age  ==> 37
   *
   *   // Get List of all rows as of transaction t 1032 (after retract)
   *   List(c1) <- Person.name.age.getObjsAsOf(1032)
   *   _ = c1.name ==> "Liz"
   *   _ = c1.age  ==> 37
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param t    Transaction time t
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsAsOf(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(conn.map(_.usingAdhocDbView(AsOf(TxLong(t)))), ec)


  /** Get Future with List of n rows as objects matching a molecule as of transaction time `t`.
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
   *   _ <- Person(ben).age.t.op.getHistory.sortBy(r => (r._2, r._3)).map(_ ==> List(
   *     (42, 1028, true),  // Insert:  42 asserted
   *     (42, 1031, false), // Update:  42 retracted
   *     (43, 1031, true),  //          43 asserted
   *   ))
   *
   *   // Get List of all rows as of transaction t 1031 (after update)
   *   List(a1, a2) <- Person.name.age.getObjsAsOf(1031)
   *   _ = a1.name ==> "Ben"
   *   _ = a1.age  ==> 43 // <-- updated
   *   _ = a2.name ==> "Liz"
   *   _ = a2.age  ==> 37
   *
   *   // Get List of n rows as of transaction t 1031 (after update)
   *   List(b1) <- Person.name.age.getObjsAsOf(1031)
   *   _ = b1.name ==> "Ben"
   *   _ = b1.age  ==> 43 // <-- updated
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param t    Long Transaction time t
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsAsOf(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(n)(conn.map(_.usingAdhocDbView(AsOf(TxLong(t)))), ec)


  /** Get Future with List of all rows as objects matching a molecule as of tx.
   * <br><br>
   * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a
   * database value as of that transaction (including).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]]
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
   *   tx3 = ben.retract
   *
   *   // Get List of all rows as of tx1 (after insert)
   *   List(a1, a2) <- Person.name.age.getObjsAsOf(tx1)
   *   _ = a1.name ==> "Ben"
   *   _ = a1.age  ==> 42
   *   _ = a2.name ==> "Liz"
   *   _ = a2.age  ==> 37
   *
   *   // Get List of all rows as of tx2 (after update)
   *   List(b1, b2) <- Person.name.age.getObjsAsOf(tx2)
   *   _ = b1.name ==> "Ben"
   *   _ = b1.age  ==> 43 // <-- updated
   *   _ = b2.name ==> "Liz"
   *   _ = b2.age  ==> 37
   *
   *   // Get List of all rows as of tx3 (after retract)
   *   List(c1) <- Person.name.age.getObjsAsOf(tx3)
   *   _ = c1.name ==> "Liz"
   *   _ = c1.age  ==> 37
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   * */
  def getObjsAsOf(tx: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(conn.map(_.usingAdhocDbView(AsOf(TxLong(tx.t)))), ec)


  /** Get Future with List of n rows as objects matching a molecule as of tx.
   * <br><br>
   * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a database
   * value as of that transaction (including).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]]
   * that contains the transaction entity id (which is used as argument to Datomic internally). This is more
   * convenient when using Molecule since we get a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction
   * operations like `get`, `update`, `retract` etc.
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
   *   List(a1, a2) <- Person.name.age.getObjsAsOf(tx2)
   *   _ = a1.name ==> "Ben"
   *   _ = a1.age  ==> 43 // <-- updated
   *   _ = a2.name ==> "Liz"
   *   _ = a2.age  ==> 37
   *
   *   // Get List of n rows as of tx2 (after update)
   *   List(b1) <- Person.name.age.getObjsAsOf(tx2, 1)
   *   _ = b1.name ==> "Ben"
   *   _ = b1.age  ==> 43 // <-- updated
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   * */
  def getObjsAsOf(tx: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(n)(conn.map(_.usingAdhocDbView(AsOf(TxLong(tx.t)))), ec)


  /** Get Future with List of all rows as objects matching a molecule as of date.
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
   *
   *   // No data yet before insert
   *   _ <- Person.name.age.getObjsAsOf(beforeInsert).map(_ ==> Nil)
   *
   *   // Get List of all rows as of afterInsert
   *   List(a1, a2) <- Person.name.age.getObjsAsOf(afterInsert)
   *   _ = a1.name ==> "Ben"
   *   _ = a1.age  ==> 42
   *   _ = a2.name ==> "Liz"
   *   _ = a2.age  ==> 37
   *
   *   // Get List of all rows as of afterUpdate
   *   List(b1, b2) <- Person.name.age.getObjsAsOf(afterUpdate)
   *   _ = b1.name ==> "Ben"
   *   _ = b1.age  ==> 43 // <-- updated
   *   _ = b2.name ==> "Liz"
   *   _ = b2.age  ==> 37
   *
   *   // Get List of all rows as of afterRetract
   *   List(c1) <- Person.name.age.getObjsAsOf(afterRetract)
   *   _ = c1.name ==> "Liz"
   *   _ = c1.age  ==> 37
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param date java.util.Date
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsAsOf(date: Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(conn.map(_.usingAdhocDbView(AsOf(TxDate(date)))), ec)


  /** Get Future with List of n rows as objects matching a molecule as of date.
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
   *   List(a1, a2) <- Person.name.age.getObjsAsOf(afterUpdate)
   *   _ = a1.name ==> "Ben"
   *   _ = a1.age  ==> 43 // <-- updated
   *   _ = a2.name ==> "Liz"
   *   _ = a2.age  ==> 37
   *
   *   // Get List of n rows as of afterUpdate
   *   List(b1) <- Person.name.age.getObjsAsOf(afterUpdate, 1)
   *   _ = b1.name ==> "Ben"
   *   _ = b1.age  ==> 43 // <-- updated
   * } yield ()
   * }}}
   *
   * @group getAsOf
   * @param date java.util.Date
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsAsOf(date: Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(n)(conn.map(_.usingAdhocDbView(AsOf(TxDate(date)))), ec)


  // get since ================================================================================================

  /** Get Future with List of all rows as objects matching a molecule since transaction time `t`.
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
   *   List(a1, a2, a3) <- Person.name.getObjs
   *   _ = a1.name ==> "Ann"
   *   _ = a2.name ==> "Ben"
   *   _ = a3.name ==> "Cay"
   *
   *   // Ben and Cay added since transaction time t1
   *   List(b1, b2) <- Person.name.getObjsSince(t1)
   *   _ = b1.name ==> "Ben"
   *   _ = b2.name ==> "Cay"
   *
   *   // Cay added since transaction time t2
   *   List(c1) <- Person.name.getObjsSince(t2)
   *   _ = c1.name ==> "Cay"
   *
   *   // Nothing added since transaction time t3
   *   _ <- Person.name.getObjsSince(t3).map(_ ==> Nil)
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param t    Transaction time t
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsSince(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(conn.map(_.usingAdhocDbView(Since(TxLong(t)))), ec)


  /** Get Future with List of n rows as objects matching a molecule since transaction time `t`.
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
   *   List(a1, a2, a3) <- Person.name.getObjs
   *   _ = a1.name ==> "Ann"
   *   _ = a2.name ==> "Ben"
   *   _ = a3.name ==> "Cay"
   *
   *   // Ben and Cay added since transaction time t1
   *   List(b1, b2) <- Person.name.getObjsSince(t1)
   *   _ = b1.name ==> "Ben"
   *   _ = b2.name ==> "Cay"
   *
   *   // Ben and Cay added since transaction time t1 - only n (1) rows returned
   *   List(c1) <- Person.name.getObjsSince(t1, 1)
   *   _ = c1.name ==> "Ben"
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param t    Transaction time t
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsSince(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(n)(conn.map(_.usingAdhocDbView(Since(TxLong(t)))), ec)


  /** Get Future with List of all rows as objects matching a molecule since tx.
   * <br><br>
   * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
   * value since that transaction (excluding the transaction itself).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]] that contains
   * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
   * getAsyncObjList a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
   * {{{
   * for {
   *   // Get tx reports for 3 transactions
   *   tx1 <- Person.name("Ann").save
   *   tx2 <- Person.name("Ben").save
   *   tx3 <- Person.name("Cay").save
   *
   *   // Current values
   *   List(a1, a2, a3) <- Person.name.getObjs
   *   _ = a1.name ==> "Ann"
   *   _ = a2.name ==> "Ben"
   *   _ = a3.name ==> "Cay"
   *
   *   // Ben and Cay added since transaction time tx1
   *   List(b1, b2) <- Person.name.getObjsSince(tx1)
   *   _ = b1.name ==> "Ben"
   *   _ = b2.name ==> "Cay"
   *
   *   // Cay added since transaction time tx2
   *   List(c1) <- Person.name.getObjsSince(tx2)
   *   _ = c1.name ==> "Cay"
   *
   *   // Nothing added since transaction time tx3
   *   _ <- Person.name.getObjsSince(tx3).map(_ ==> Nil)
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsSince(tx: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(conn.map(_.usingAdhocDbView(Since(TxLong(tx.t)))), ec)


  /** Get Future with List of n rows as objects matching a molecule since tx.
   * <br><br>
   * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
   * value since that transaction (excluding the transaction itself).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]] that contains
   * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
   * getAsyncObjList a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
   * {{{
   * for {
   *   // Get tx reports for 3 transactions
   *   tx1 <- Person.name("Ann").save
   *   tx2 <- Person.name("Ben").save
   *   tx3 <- Person.name("Cay").save
   *
   *   // Current values
   *   List(a1, a2, a3) <- Person.name.getObjs
   *   _ = a1.name ==> "Ann"
   *   _ = a2.name ==> "Ben"
   *   _ = a3.name ==> "Cay"
   *
   *   // Ben and Cay added since tx1
   *   List(b1, b2) <- Person.name.getObjsSince(tx1)
   *   _ = b1.name ==> "Ben"
   *   _ = b2.name ==> "Cay"
   *
   *   // Ben and Cay added since tx1 - only n (1) rows returned
   *   List(c1) <- Person.name.getObjsSince(tx1, 1)
   *   _ = c1.name ==> "Ben"
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]]
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsSince(tx: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(n)(conn.map(_.usingAdhocDbView(Since(TxLong(tx.t)))), ec)


  /** Get Future with List of all rows as objects matching a molecule since date.
   * <br><br>
   * Get data added/retracted since a human point in time (a java.util.Date).
   * {{{
   * for {
   *   // Transact 3 times (`inst` retrieves transaction time/Date from tx report)
   *   date1 <- Person.name("Ann").save.inst
   *   date2 <- Person.name("Ben").save.inst
   *   date3 <- Person.name("Cay").save.inst
   *
   *   // Current values
   *   List(a1, a2, a3) <- Person.name.getObjs
   *   _ = a1.name ==> "Ann"
   *   _ = a2.name ==> "Ben"
   *   _ = a3.name ==> "Cay"
   *
   *   // Ben and Cay added since date1
   *   List(b1, b2) <- Person.name.getObjsSince(date1)
   *   _ = b1.name ==> "Ben"
   *   _ = b2.name ==> "Cay"
   *
   *   // Cay added since transaction time date2
   *   List(c1) <- Person.name.getObjsSince(date2)
   *   _ = c1.name ==> "Cay"
   *
   *   // Nothing added since transaction time date3
   *   _ <- Person.name.getObjsSince(date3).map(_ ==> Nil)
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param date java.util.Date
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsSince(date: Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(conn.map(_.usingAdhocDbView(Since(TxDate(date)))), ec)


  /** Get Future with List of n rows as objects matching a molecule since date.
   * <br><br>
   * Get data added/retracted since a human point in time (a java.util.Date).
   * {{{
   * for {
   *   // Transact 3 times (`inst` retrieves transaction time/Date from tx report)
   *   date1 <- Person.name("Ann").save.inst
   *   date2 <- Person.name("Ben").save.inst
   *   date3 <- Person.name("Cay").save.inst
   *
   *   // Current values
   *   List(a1, a2, a3) <- Person.name.getObjs
   *   _ = a1.name ==> "Ann"
   *   _ = a2.name ==> "Ben"
   *   _ = a3.name ==> "Cay"
   *
   *   // Ben and Cay added since date1
   *   List(b1, b2) <- Person.name.getObjsSince(date1)
   *   _ = b1.name ==> "Ben"
   *   _ = b2.name ==> "Cay"
   *
   *   // Ben and Cay added since date1 - only n (1) rows returned
   *   List(c1) <- Person.name.getObjsSince(date1, 1)
   *   _ = c1.name ==> "Ben"
   * } yield ()
   * }}}
   *
   * @group getSince
   * @param date java.util.Date
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsSince(date: Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(n)(conn.map(_.usingAdhocDbView(Since(TxDate(date)))), ec)


  // get with ================================================================================================

  /** Get Future with List of all rows as objects matching a molecule with applied molecule transaction data.
   * <br><br>
   * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
   * {{{
   * for {
   *   // Current state
   *   ben <- Person.name("Ben").likes("pasta").save.map(_.eid)
   *
   *   List(p0) <- Person.name.likes.getObjs
   *   _ = p0.name  ==> "Ben"
   *   _ = p0.likes ==> "pasta"
   *
   *   // Test adding transaction data
   *   List(pTest) <- Person.name.likes.getObjsWith(
   *     // Additional transaction data
   *     Person(ben).likes("sushi").getUpdateStmts
   *   )
   *   // Effect: Ben would like sushi if tx was applied
   *   _ = pTest.name  ==> "Ben"
   *   _ = pTest.likes ==> "sushi"
   *
   *   // Current state is still the same
   *   List(pAfter) <- Person.name.likes.getObjs
   *   _ = pAfter.name  ==> "Ben"
   *   _ = pAfter.likes ==> "pasta"
   * } yield ()
   * }}}
   * Multiple transactions can be applied to test more complex what-if scenarios!
   *
   * @group getWith
   * @param txMolecules Transaction statements from applied Molecules with test data
   * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsWith(txMolecules: Future[Seq[Statement]]*)
                 (implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      val connWith = conn.map { conn =>
        val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, conn)
        conn.usingAdhocDbView(With(stmtsEdn, uriAttrs))
      }
      getObjs(connWith, ec)
    }
  }


  /** Get Future with List of n rows as objects matching a molecule with applied molecule transaction data.
   * <br><br>
   * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
   * {{{
   * for {
   *   // Current state
   *   List(ben, liz) <- Person.name.likes.insert(
   *     ("Ben", "pasta"),
   *     ("Liz", "pizza")
   *   ).map(_.eids)
   *
   *   List(p1, p2) <- Person.name.likes.getObjs
   *   _ = p1.name  ==> "Ben"
   *   _ = p1.likes ==> "pasta"
   *   _ = p2.name  ==> "Liz"
   *   _ = p2.likes ==> "pizza"
   *
   *   // Test multiple transactions
   *   List(testP1, testP2) <- Person.name.likes.getObjsWith(
   *     Person(ben).likes("sushi").getUpdateStmts,
   *     Person(liz).likes("cake").getUpdateStmts
   *   )
   *   // Effect: sushi and cake now liked
   *   _ = testP1.name  ==> "Ben"
   *   _ = testP1.likes ==> "sushi"
   *   _ = testP2.name  ==> "Liz"
   *   _ = testP2.likes ==> "cake"
   *
   *   // Same as above, but only n (1) rows returned:
   *   List(oneTestP) <- Person.name.likes.getObjsWith(
   *     1
   *     Person(ben).likes("sushi").getUpdateStmts,
   *     Person(liz).likes("cake").getUpdateStmts
   *   )
   *   // Effect: sushi and cake now liked (but only Ben returned)
   *   _ = oneTestP.name  ==> "Ben"
   *   _ = oneTestP.likes ==> "sushi"
   *
   *   // Current state is still the same
   *   List(p3, p4) <- Person.name.likes.getObjs
   *   _ = p3.name  ==> "Ben"
   *   _ = p3.likes ==> "pasta"
   *   _ = p4.name  ==> "Liz"
   *   _ = p4.likes ==> "pizza"
   * } yield ()
   * }}}
   *
   * @group getWith
   * @param n           Int Number of rows returned
   * @param txMolecules Transaction statements from applied Molecules with test data
   * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   * @note Note how the `n` parameter has to come before the `txMolecules` vararg.
   */
  def getObjsWith(n: Int, txMolecules: Future[Seq[Statement]]*)
                 (implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      val connWith = conn.map { conn =>
        val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, conn)
        conn.usingAdhocDbView(With(stmtsEdn, uriAttrs))
      }
      getObjs(n)(connWith, ec)
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
   *   _ <- Person(ben).age.t.op.getObjsHistory.map(_
   *     .sortBy(o => (o.t, o.op))
   *     .map(o => s"${o.age} ${o.t} ${o.op}") ==> List(
   *     "42 1028 true",  // Insert:  42 asserted
   *     "42 1031 false", // Update:  42 retracted
   *     "43 1031 true",  //          43 asserted
   *     "43 1032 false"  // Retract: 43 retracted
   *   ))
   * } yield ()
   * }}}
   *
   * @group getHistory
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return `Future[List[Obj]]` where Obj is an object type having property types matching the attributes of the molecule
   */
  def getObjsHistory(implicit conn: Future[Conn], ec: ExecutionContext): Future[List[Obj]] =
    getObjs(conn.map(_.usingAdhocDbView(History)), ec)


  // `getHistory(n: Int)` is not implemented since the whole data set normally needs to be sorted
  // to give chronological meaningful information.
}
