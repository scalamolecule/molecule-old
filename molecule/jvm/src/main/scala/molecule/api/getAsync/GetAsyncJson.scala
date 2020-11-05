package molecule.api.getAsync

import molecule.api.get.GetJson
import molecule.ast.MoleculeBase
import molecule.ast.transactionModel.Statement
import molecule.facade.Conn
import molecule.transform.JsonBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions


/** Asynchronous data getter methods on molecules returning Future-wrapped Json String.
  * <br><br>
  * Molecule builds a Json String directly from the untyped raw Datomic data.
  * <br><br>
  * Attributes names are used as Json field names. In order to distinguish
  * fields from each other, all attribute names are prepended with the namespace
  * name (in lowercase). For a namespace `Person` with an attribute `name` we get:
  *
  *  - "person.name"
  *
  * To distinguis fields of multiple relationships to the same namespace like `friends` and
  * `enemies` pointing to other `Person`'s require us to add a relationship name prefix too:
  *
  *  - "friends.person.name"
  *  - "enemies.person.name"
  *
  * Furthermore, if the attribute is part of a transaction meta-data molecule, we prefix that with `tx` too:
  *
  *  - "tx.person.name"
  *  - "tx.friends.person.name"
  *
  * Example:
  * {{{
  *   val jsonFuture: Future[String] = Person.name.age.getAsyncJson
  *   for {
  *     json <- jsonFuture
  *   } yield {
  *     json ===
  *      """[
  *        |{"person.name": "Ben", "person.age": 42},
  *        |{"person.name": "Liz", "person.age": 37}
  *        |]""".stripMargin
  *   }
  * }}}
  * Each asynchronous getter in this package simply wraps the result of its equivalent synchronous getter (in the
  * `get` package) in a Future. `getAsyncIterableAsOf` thus wraps the result of `getIterableAsOf` in a Future and so on.
  *
  * @see [[https://github.com/scalamolecule/molecule/blob/master/coretests/src/test/scala/molecule/coretests/json Json tests]]
  **/
trait GetAsyncJson { self: MoleculeBase with GetJson with JsonBuilder =>


  // get ================================================================================================

  /** Get `Future` with json data for all rows matching molecule.
    * <br><br>
    * Namespace.Attribute is used as json fields. Values are
    * quoted when necessary. Nested data becomes json objects etc.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJson(implicit* getJson]] method.
    *
    * @see [[http://www.scalamolecule.org/manual/crud/getjson/ Manual on getJson]]
    * @group getAsync
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    */
  def getAsyncJson(implicit conn: Conn): Future[String] =
    Future(getJson(conn))


  /** Get `Future` with json data for n rows matching molecule.
    * <br><br>
    * Namespace.Attribute is used as json fields. Values are
    * quoted when necessary. Nested data becomes json objects etc.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJson(n:Int)* getJson]] method.
    *
    * @see [[http://www.scalamolecule.org/manual/crud/getjson/ Manual on getJson]]
    * @group getAsync
    * @param n    Number of rows returned
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    */
  def getAsyncJson(n: Int)(implicit conn: Conn): Future[String] =
    Future(getJson(n)(conn))


  // get as of ================================================================================================

  /** Get `Future` with json data for all rows matching molecule as of transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved in a getHistory call for an attribute and then be
    * used to get data as of that point in time (including that transaction).
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonAsOf(t:Long)* getJsonAsOf]] method.
    *
    * @group getAsyncJsonAsOf
    * @param t    Transaction time t
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonAsOf(t: Long)(implicit conn: Conn): Future[String] =
    Future(getJsonAsOf(t)(conn))


  /** Get `Future` with json data for n rows matching molecule as of transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * `t` can for instance be retrieved in a getHistory call for an attribute and then be
    * used to get data as of that point in time (including that transaction).
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonAsOf(t:Long,n:Int)* getJsonAsOf]] method.
    *
    * @group getAsyncJsonAsOf
    * @param t    Transaction time t
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonAsOf(t: Long, n: Int)(implicit conn: Conn): Future[String] =
    Future(getJsonAsOf(t, n)(conn))


  /** Get `Future` with json data for all rows matching molecule as of tx.
    * <br><br>
    * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a
    * database value as of that transaction (including).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.facade.TxReport TxReport]]
    * that contains the transaction entity id (which is used as argument to Datomic internally). This is more
    * convenient when using Molecule since we get a [[molecule.facade.TxReport TxReport]] from transaction
    * operations like `get`, `update`, `retract` etc.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonAsOf(tx:molecule\.facade\.TxReport)* getJsonAsOf]] method.
    *
    * @group getAsyncJsonAsOf
    * @param tx   [[molecule.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonAsOf(tx: molecule.facade.TxReport)(implicit conn: Conn): Future[String] =
    Future(getJsonAsOf(tx.t)(conn))


  /** Get `Future` with json data for n rows matching molecule as of tx.
    * <br><br>
    * Datomic's internal `asOf` method can take a transaction entity id as argument to retrieve a
    * database value as of that transaction (including).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.facade.TxReport TxReport]]
    * that contains the transaction entity id (which is used as argument to Datomic internally). This is more
    * convenient when using Molecule since we get a [[molecule.facade.TxReport TxReport]] from transaction
    * operations like `get`, `update`, `retract` etc.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonAsOf(tx:molecule\.facade\.TxReport,n:Int)* getJsonAsOf]] method.
    *
    * @group getAsyncJsonAsOf
    * @param tx   [[molecule.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonAsOf(tx: molecule.facade.TxReport, n: Int)(implicit conn: Conn): Future[String] =
    Future(getJsonAsOf(tx.t, n)(conn))


  /** Get `Future` with json data for all rows matching molecule as of date.
    * <br><br>
    * Get data at a human point in time (a java.util.Date).
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonAsOf(date:java\.util\.Date)* getJsonAsOf]] method.
    *
    * @group getAsyncJsonAsOf
    * @param date java.util.Date
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonAsOf(date: java.util.Date)(implicit conn: Conn): Future[String] =
    Future(getJsonAsOf(date)(conn))


  /** Get `Future` with json data for n rows matching molecule as of tx.
    * <br><br>
    * Get data at a human point in time (a java.util.Date).
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonAsOf(date:java\.util\.Date,n:Int)* getJsonAsOf]] method.
    *
    * @group getAsyncJsonAsOf
    * @param date java.util.Date
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonAsOf(date: java.util.Date, n: Int)(implicit conn: Conn): Future[String] =
    Future(getJsonAsOf(date, n)(conn))


  // get since ================================================================================================

  /** Get `Future` with json data for all rows matching molecule since transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonSince(t:Long)* getJsonSince]] method.
    *
    * @group getAsyncJsonSince
    * @param t    Transaction time t
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonSince(t: Long)(implicit conn: Conn): Future[String] =
    Future(getJsonSince(t)(conn))


  /** Get `Future` with json data for n rows matching molecule since transaction time `t`.
    * <br><br>
    * Transaction time `t` is an auto-incremented transaction number assigned internally by Datomic.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonSince(t:Long,n:Int)* getJsonSince]] method.
    *
    * @group getAsyncJsonSince
    * @param t    Transaction time t
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonSince(t: Long, n: Int)(implicit conn: Conn): Future[String] =
    Future(getJsonSince(t, n)(conn))


  /** Get `Future` with json data for all rows matching molecule since tx.
    * <br><br>
    * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
    * value since that transaction (excluding the transaction itself).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.facade.TxReport TxReport]] that contains
    * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
    * get a [[molecule.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonSince(tx:molecule\.facade\.TxReport)* getJsonSince]] method.
    *
    * @group getAsyncJsonSince
    * @param tx   [[molecule.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonSince(tx: molecule.facade.TxReport)(implicit conn: Conn): Future[String] =
    Future(getJsonSince(tx.t)(conn))


  /** Get `Future` with json data for n rows matching molecule since tx.
    * <br><br>
    * Datomic's internal `since` can take a transaction entity id as argument to retrieve a database
    * value since that transaction (excluding the transaction itself).
    * <br><br>
    * Instead of supplying the transaction entity id, in Molecule we supply a [[molecule.facade.TxReport TxReport]] that contains
    * the transaction entity id (which is used as argument to Datomic internally). This is more convenient when using Molecule since we
    * get a [[molecule.facade.TxReport TxReport]] from transaction operations like `get`, `update`, `retract` etc.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonSince(tx:molecule\.facade\.TxReport,n:Int)* getJsonSince]] method.
    *
    * @group getAsyncJsonSince
    * @param tx   [[molecule.facade.TxReport TxReport]] (returned from all molecule transaction operations)
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonSince(tx: molecule.facade.TxReport, n: Int)(implicit conn: Conn): Future[String] =
    Future(getJsonSince(tx.t, n)(conn))


  /** Get `Future` with json data for all rows matching molecule since date.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonSince(date:java\.util\.Date)* getJsonSince]] method.
    *
    * @group getAsyncJsonSince
    * @param date java.util.Date
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonSince(date: java.util.Date)(implicit conn: Conn): Future[String] =
    Future(getJsonSince(date)(conn))


  /** Get `Future` with json data for n rows matching molecule since date.
    * <br><br>
    * Get data added/retracted since a human point in time (a java.util.Date).
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonSince(date:java\.util\.Date,n:Int)* getJsonSince]] method.
    *
    * @group getAsyncJsonSince
    * @param date java.util.Date
    * @param n    Int Number of rows returned
    * @param conn Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/asof-since/ Manual]] on `asof`/`since`
    */
  def getAsyncJsonSince(date: java.util.Date, n: Int)(implicit conn: Conn): Future[String] =
    Future(getJsonSince(date, n)(conn))


  // get with ================================================================================================

  /** Get `Future` with json data for all rows matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonWith(txMolecules* getJsonWith]] method.
    * <br><br>
    * Multiple transactions can be applied to test more complex what-if scenarios!
    *
    * @group getAsyncJsonWith
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/with/ Manual]] on `with`
    */
  def getAsyncJsonWith(txMolecules: Seq[Seq[Statement]]*)(implicit conn: Conn): Future[String] =
    Future(getJsonWith(txMolecules: _*)(conn))


  /** Get `Future` with json data for all rows matching molecule with applied molecule transaction data.
    * <br><br>
    * Apply one or more molecule transactions to in-memory "branch" of db without affecting db.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonWith(n:Int,txMolecules* getJsonWith]] method.
    * <br><br>
    * Multiple transactions can be applied to test more complex what-if scenarios!
    *
    * @group getAsyncJsonWith
    * @param txMolecules Transaction statements from applied Molecules with test data
    * @param conn        Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/with/ Manual]] on `with`
    */
  def getAsyncJsonWith(n: Int, txMolecules: Seq[Seq[Statement]]*)(implicit conn: Conn): Future[String] =
    Future(getJsonWith(n, txMolecules: _*)(conn))


  /** Get `Future` with json data for all rows matching molecule with applied raw transaction data.
    * <br><br>
    * Apply raw transaction data to in-memory "branch" of db without affecting db.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonWith(txData:java\.util\.List[_])* getJsonWith]] method.
    *
    * @group getAsyncJsonWith
    * @param txData Raw transaction data as java.util.List[Object]
    * @param conn   Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/with/ Manual]] on `with`
    */
  def getAsyncJsonWith(txData: java.util.List[_])(implicit conn: Conn): Future[String] =
    Future(getJsonWith(txData)(conn))


  /** Get `Future` with json data for n rows matching molecule with applied raw transaction data.
    * <br><br>
    * Apply raw transaction data to in-memory "branch" of db without affecting db.
    * <br><br>
    * For more info and code examples see equivalent synchronous
    * [[molecule.api.get.GetJson.getJsonWith(txData:java\.util\.List[_],n:Int)* getJsonWith]] method.
    *
    * @group getAsyncJsonWith
    * @param txData Raw transaction data as java.util.List[Object]
    * @param n      Int Number of rows returned
    * @param conn   Implicit [[molecule.facade.Conn Conn]] value in scope
    * @return String of json
    * @see [[http://www.scalamolecule.org/manual/time/with/ Manual]] on `with`
    */
  def getAsyncJsonWith(txData: java.util.List[_], n: Int)(implicit conn: Conn): Future[String] =
    Future(getJsonWith(txData, n)(conn))


  // get history ================================================================================================

  // Only `getHistory`/`getAsyncHistory` returning List/Future[List] are implemented since it is only meaningful
  // to track the history of one attribute of one entity at a time and a sortable List is therefore preferred.
}