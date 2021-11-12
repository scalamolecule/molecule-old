package molecule.core.api

import molecule.core.ast.elements.Composite
import molecule.core.marshalling.Marshalling
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.util.JavaUtil
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}

/** Data getter methods on molecules that return data as a Json String.
 * <br><br>
 * Attributes names are used as Json field names.
 * */
trait GetJson[Obj, Tpl] extends JavaUtil { self: Marshalling[Obj, Tpl] =>


  // get ================================================================================================

  /** Get json data for all rows matching a molecule.
   * {{{
   * Person.name.age.getJson.map(_ ==>
   *   """[
   *     |{"person.name": "Ben", "person.age": 42},
   *     |{"person.name": "Liz", "person.age": 37}
   *     |]""".stripMargin)
   * }}}
   * Namespace.Attribute is used as json fields. Values are
   * quoted when necessary. Nested data becomes json objects etc.
   *
   * @group get
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJson(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(-1)(conn, ec)


  private def outerJson(json: String): String = _model.elements.head match {
    case _: Composite =>
      s"""{
         |  "data": {
         |    "composite": [$json]
         |  }
         |}""".stripMargin
    case _            =>
      s"""{
         |  "data": {
         |    "${firstNs(_model)}": [$json]
         |  }
         |}""".stripMargin
  }

  /** Get json data for n rows matching a molecule
   * {{{
   * Person.name.age.getJson(1).map(_ ==>
   *   """[
   *     |{"person.name": "Ben", "person.age": 42}
   *     |]""".stripMargin)
   * }}}
   * Namespace.Attribute is used as json fields. Values are
   * quoted when necessary. Nested data becomes json objects etc.
   *
   * @group get
   * @param n       Number of rows returned
   * @param futConn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJson(n: Int)(implicit futConn: Future[Conn], ec: ExecutionContext): Future[String] = {
    _inputThrowable.fold(
      futConn.flatMap { conn =>
        val sb   = new StringBuffer()
        var next = false
        if (conn.isJsPlatform) {
          conn.jsQueryJson(_model, _query, _datalog, n, obj, nestedLevels, isOptNested, refIndexes, tacitIndexes)
            .map { packed =>
              if (packed.isEmpty) {
                outerJson("")
              } else {
                val lines = packed.linesIterator
                lines.next() // skip first empty line
                while (lines.hasNext) {
                  if (next) sb.append(",") else next = true
                  packed2json(lines, sb)
                }
                sb.append("\n    ")
                outerJson(sb.toString)
              }
            }
        } else {
          conn.jvmQuery(_model, _query).map { jColl =>
            val count = jColl.size()
            val rows  = jColl.iterator()
            if (count == 0) {
              // Empty result set
            } else if (n == -1) {
              // All rows
              while (rows.hasNext) {
                if (next) sb.append(",") else next = true
                row2json(rows.next, sb)
              }
              sb.append("\n    ")
            } else {
              // n rows
              var i = 0
              while (rows.hasNext && i < n) {
                if (next) sb.append(",") else next = true
                row2json(rows.next, sb)
                i += 1
              }
              sb.append("\n    ")
            }
            outerJson(sb.toString)
          }
        }
      }
    )(Future.failed) // Pass on exception from input failure
  }


  // get as of ================================================================================================

  /** Get json data for all rows matching a molecule as of transaction time `t`.
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
   *     ("Liz", 37)
   *   ) eids
   *
   *   // Update (t 1031)
   *   _ <- Person(ben).age(43).update
   *
   *   // Retract (t 1032)
   *   ben.retract
   *
   *   // History of Ben
   *   _ <- Person(ben).age.t.op.getHistory.sortBy(r => (r._2, r._3)).map(_ ==> List(
   *     (42, 1028, true),  // Insert:  42 asserted
   *     (42, 1031, false), // Update:  42 retracted
   *     (43, 1031, true),  //          43 asserted
   *     (43, 1032, false)  // Retract: 43 retracted
   *   ))
   *
   *   // Get json for all rows as of transaction t 1028 (after insert)
   *   _ <- Person.name.age.getJsonAsOf(1028).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 42},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *   
   *   // Get json for all rows as of transaction t 1031 (after update) - Ben now 43
   *   _ <- Person.name.age.getJsonAsOf(1031).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *   
   *   // Get json for all rows as of transaction t 1032 (after retract) - Ben gone
   *   _ <- Person.name.age.getJsonAsOf(1032).map(_ ==>
   *     """[
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   *
   * @group getJsonAsOf
   * @param t    Transaction time t
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonAsOf(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(-1)(conn.map(_.usingAdhocDbView(AsOf(TxLong(t)))), ec)


  /** Get json data for n rows matching a molecule as of transaction time `t`.
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
   *     ("Liz", 37)
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
   *   // Get json for all rows as of transaction t 1031 (after update)
   *   _ <- Person.name.age.getJsonAsOf(1031).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *
   *   // Get json for n (1) rows as of transaction t 1031 (after update)
   *   _ <- Person.name.age.getJsonAsOf(1031, 1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   *
   * @group getJsonAsOf
   * @param t    Transaction time t
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonAsOf(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(n)(conn.map(_.usingAdhocDbView(AsOf(TxLong(t)))), ec)


  /** Get json data for all rows matching a molecule as of tx.
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
   *     ("Liz", 37)
   *   )
   *   List(ben, liz) = tx1.eids
   *
   *   // Update (tx report 2)
   *   tx2 <- Person(ben).age(43).update
   *
   *   // Retract (tx report 3)
   *   tx3 <- ben.retract
   *
   *   // Get json for all rows as of transaction tx1 (after insert)
   *   _ <- Person.name.age.getJsonAsOf(tx1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 42},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *
   *   // Get json for all rows as of transaction tx2 (after update) - Ben now 43
   *   _ <- Person.name.age.getJsonAsOf(tx2).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *
   *   // Get json for all rows as of transaction tx3 (after retract) - Ben gone
   *   _ <- Person.name.age.getJsonAsOf(tx3).map(_ ==>
   *     """[
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   *
   * @group getJsonAsOf
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonAsOf(tx: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(-1)(conn.map(_.usingAdhocDbView(AsOf(TxLong(tx.t)))), ec)


  /** Get json data for n rows matching a molecule as of tx.
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
   *     ("Liz", 37)
   *   )
   *   List(ben, liz) = tx1.eids
   *
   *   // Update (tx report 2)
   *   tx2 <- Person(ben).age(43).update
   *
   *   // Get json for all rows as of transaction tx2 (after update) - Ben now 43
   *   _ <- Person.name.age.getJsonAsOf(tx2).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *
   *   // Get json for n rows as of transaction tx2 (after update) - Ben now 43
   *   _ <- Person.name.age.getJsonAsOf(tx2, 1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   *
   * @group getJsonAsOf
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonAsOf(tx: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(n)(conn.map(_.usingAdhocDbView(AsOf(TxLong(tx.t)))), ec)


  /** Get json data for all rows matching a molecule as of date.
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
   *   _ <- Person.name.age.getJsonAsOf(beforeInsert).map(_ ==> "")
   *
   *   // Get List of all rows as of afterInsert
   *   _ <- Person.name.age.getJsonAsOf(afterInsert).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 42},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *
   *   // Get List of all rows as of afterUpdate
   *   _ <- Person.name.age.getJsonAsOf(afterUpdate).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *
   *   // Get List of all rows as of afterRetract
   *   _ <- Person.name.age.getJsonAsOf(afterRetract).map(_ ==>
   *     """[
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   *
   * @group getJsonAsOf
   * @param date java.util.Date
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonAsOf(date: java.util.Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(-1)(conn.map(_.usingAdhocDbView(AsOf(TxDate(date)))), ec)


  /** Get json data for n rows matching a molecule as of tx.
   * <br><br>
   * Get data at a human point in time (a java.util.Date).
   * {{{
   * for {
   *   beforeInsert = new java.util.Date
   *
   *   // Insert
   *   tx1 = Person.name.age insert List(
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
   *   _ <- Person.name.age.getJsonAsOf(afterUpdate).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43},
   *       |{"person.name": "Liz", "person.age": 37}
   *       |]""".stripMargin)
   *
   *   // Get List of n rows as of afterUpdate
   *   _ <- Person.name.age.getJsonAsOf(afterUpdate, 1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.age": 43}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   *
   * @group getJsonAsOf
   * @param date java.util.Date
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonAsOf(date: java.util.Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(n)(conn.map(_.usingAdhocDbView(AsOf(TxDate(date)))), ec)


  // get since ================================================================================================

  /** Get json data for all rows matching a molecule since transaction time `t`.
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
   *   // Ben and Cay added since transaction time t 1028
   *   _ <- Person.name.getJsonSince(t1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"},
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Cay added since transaction time t 1030
   *   _ <- Person.name.getJsonSince(t2).map(_ ==>
   *     """[
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Nothing added since transaction time t 1032
   *   _ <- Person.name.getJsonSince(t3).map(_ ==> "")
   * } yield ()
   * }}}
   *
   * @group getJsonSince
   * @param t    Transaction time t
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonSince(t: Long)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(-1)(conn.map(_.usingAdhocDbView(Since(TxLong(t)))), ec)


  /** Get json data for n rows matching a molecule since transaction time `t`.
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
   *   // Ben and Cay added since transaction time t 1028
   *   _ <- Person.name.getJsonSince(t1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"},
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Ben and Cay added since transaction time t 1028 - only n (1) rows returned
   *   _ <- Person.name.getJsonSince(t1, 1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   *
   * @group getJsonSince
   * @param t    Transaction time t
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonSince(t: Long, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(n)(conn.map(_.usingAdhocDbView(Since(TxLong(t)))), ec)


  /** Get json data for all rows matching a molecule since tx.
   * <br><br>
   * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
   * value since that transaction (excluding the transaction itself).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]] that contains
   * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
   * getAsync a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
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
   *   _ <- Person.name.getJsonSince(tx1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"},
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Cay added since tx2
   *   _ <- Person.name.getJsonSince(tx2).map(_ ==>
   *     """[
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Nothing added since tx3
   *   _ <- Person.name.getJsonSince(tx3).map(_ ==> "")
   * } yield ()
   * }}}
   *
   * @group getJsonSince
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonSince(tx: TxReport)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(-1)(conn.map(_.usingAdhocDbView(Since(TxLong(tx.t)))), ec)


  /** Get json data for n rows matching a molecule since tx.
   * <br><br>
   * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
   * value since that transaction (excluding the transaction itself).
   * <br><br>
   * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.datomic.base.facade.TxReport TxReport]] that contains
   * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
   * getAsync a [[molecule.datomic.base.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
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
   *   _ <- Person.name.getJsonSince(tx1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"},
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Ben and Cay added since tx1 - only n (1) rows returned
   *   _ <- Person.name.getJsonSince(tx1, 1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   *
   * @group getJsonSince
   * @param tx   [[molecule.datomic.base.facade.TxReport TxReport]] (returned from all molecule transaction operations)
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonSince(tx: TxReport, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(n)(conn.map(_.usingAdhocDbView(Since(TxLong(tx.t)))), ec)


  /** Get json data for all rows matching a molecule since date.
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
   *   _ <- Person.name.getJsonSince(date1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"},
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Cay added since date2
   *   _ <- Person.name.getJsonSince(date2).map(_ ==>
   *     """[
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Nothing added since date3
   *   _ <- Person.name.getJsonSince(date3).map(_ ==> ""
   * } yield ()
   * }}}
   *
   * @group getJsonSince
   * @param date java.util.Date
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonSince(date: java.util.Date)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(-1)(conn.map(_.usingAdhocDbView(Since(TxDate(date)))), ec)


  /** Get json data for n rows matching a molecule since date.
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
   *   _ <- Person.name.getJsonSince(date1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"},
   *       |{"person.name": "Cay"}
   *       |]""".stripMargin)
   *
   *   // Ben and Cay added since date1 - only n (1) rows returned
   *   _ <- Person.name.getJsonSince(date1, 1).map(_ ==>
   *     """[
   *       |{"person.name": "Ben"}
   *       |]""".stripMargin)
   * }       
   * }}}
   *
   * @group getJsonSince
   * @param date java.util.Date
   * @param n    Int Number of rows returned
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonSince(date: java.util.Date, n: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[String] =
    getJson(n)(conn.map(_.usingAdhocDbView(Since(TxDate(date)))), ec)


  // get with ================================================================================================

  /** Get json data for all rows matching a molecule with applied molecule transaction data.
   * <br><br>
   * Apply one or more molecule transactions to in-memory "branch" of db without affecting db to see how it would then look:
   * {{{
   * for {
   *   // Current state
   *   ben <- Person.name("Ben").likes("pasta").save.map(_.eid)
   *
   *   // Base data
   *   _ <- Person.name.likes.getJsonWith(
   *     // apply imaginary transaction data
   *     Person(ben).likes("sushi").getUpdateStmts
   *   ).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.likes": "sushi"}
   *       |]""".stripMargin)
   *
   *   // Current state is still the same
   *   _ <- Person.name.likes.get.map(_ ==> List(("Ben", "pasta")))
   * } yield ()
   * }}}
   * Multiple transactions can be applied to test more complex what-if scenarios!
   *
   * @group getJsonWith
   * @param txMolecules Transaction statements from applied Molecules with test data
   * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonWith(txMolecules: Future[Seq[Statement]]*)
                 (implicit conn: Future[Conn], ec: ExecutionContext): Future[String] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      val connWith = conn.map { conn =>
        val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, conn)
        conn.usingAdhocDbView(With(stmtsEdn, uriAttrs))
      }
      getJson(-1)(connWith, ec)
    }
  }


  /** Get json data for all rows matching a molecule with applied molecule transaction data.
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
   *   // Test multiple transactions
   *   _ <- Person.name.likes.getJsonWith(
   *     Person(ben).likes("sushi").getUpdateStmts,
   *     Person(liz).likes("cake").getUpdateStmts
   *   ).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.likes": "sushi"},
   *       |{"person.name": "Liz", "person.likes": "cake"}
   *       |]""".stripMargin)
   *
   *   // Same as above, but only n (1) rows returned:
   *   _ <- Person.name.likes.getJsonWith(
   *     1
   *     Person(ben).likes("sushi").getUpdateStmts,
   *     Person(liz).likes("cake").getUpdateStmts
   *   ).map(_ ==>
   *     """[
   *       |{"person.name": "Ben", "person.likes": "sushi"}
   *       |]""".stripMargin)
   * } yield ()
   * }}}
   * Multiple transactions can be applied to test more complex what-if scenarios!
   *
   * @group getJsonWith
   * @param txMolecules Transaction statements from applied Molecules with test data
   * @param conn        Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
   * @return String of json
   */
  def getJsonWith(n: Int, txMolecules: Future[Seq[Statement]]*)
                 (implicit conn: Future[Conn], ec: ExecutionContext): Future[String] = {
    Future.sequence(txMolecules).flatMap { stmtss =>
      val connWith = conn.map { conn =>
        val (stmtsEdn, uriAttrs) = Stmts2Edn(stmtss.flatten, conn)
        conn.usingAdhocDbView(With(stmtsEdn, uriAttrs))
      }
      getJson(n)(connWith, ec)
    }
  }


  // get history ================================================================================================

  // Only `getHistory` (returning a List) is implemented since it is only meaningful
  // to track the history of one attribute at a time and a sortable List is therefore preferred.
}
