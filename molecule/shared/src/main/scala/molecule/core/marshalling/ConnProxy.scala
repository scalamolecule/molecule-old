package molecule.core.marshalling

import java.util.UUID
import molecule.core.data.SchemaTransaction
import molecule.datomic.base.ast.dbView.{AsOf, DbView, History, TxLong}

/** Proxy connection on the client side.
 *
 * To create molecules on the client side, an implicit Conn_Js object holding a
 * proxy connection object needs to be in scope. Depending on which db system is used, one of the
 * three available proxy connections can be supplied.
 *
 * Here's an example of how to create an in-mem Peer proxy connection for a custom `Person` schema:
 * {{{
 * implicit val conn = Future(Conn_Js(
 *   DatomicPeerProxy("mem", "", PersonSchema.datomicPeer, PersonSchema.attrMap), "localhost", 9000
 * ))
 * }}}
 */
sealed trait ConnProxy {
  /** Seq of edn strings to transact schema. Supplied from generated boilerplate code. */
  val schema: Seq[String]

  /** Map of attribute meta data. Supplied from generated boilerplate code. */
  val attrMap: Map[String, (Int, String)]

  /** Internal setting for test db status
   *
   * 0 inactive
   * 1 active
   * -1 de-activate signal
   */
  val testDbStatus: Int

  /** Internal holder of optional current test DbView object */
  val testDbView: Option[DbView]

  /** Internal holder of optional current ad-hoc DbView object */
  val adhocDbView: Option[DbView]

  /** Unique internal identifier for cached proxy connection on server side */
  val uuid: String
}


/** Datomic Peer proxy connection
 *
 * @param protocol     Datomic protocol: mem, dev or pro
 * @param dbIdentifier Datomic db identifier, like "localhost:4334/mbrainz-1968-1973"
 * @param schema       Seq of schema transaction data from generated boilerplate code
 * @param attrMap      Map of attribute data from generated boilerplate code
 * @param testDbStatus Internally applied setting, not intended to be set by user
 * @param testDbView   Internally applied setting, not intended to be set by user
 * @param adhocDbView  Internally applied setting, not intended to be set by user
 * @param uuid         Internally applied setting, not intended to be set by user
 */
case class DatomicPeerProxy(
  protocol: String,
  dbIdentifier: String,
  schema: Seq[String],
  attrMap: Map[String, (Int, String)],

  // Internal settings, not intended to be set by user
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends ConnProxy


/** Datomic dev-local proxy connection for locally testing against the Datomic Client api
 *
 * @param protocol     Datomic protocol: mem, dev or pro
 * @param system       Name of Datomic system which translates into a directory in the datomic distributions folder where databases will be saved
 * @param storageDir   Path to the downloaded dev-local directory
 * @param dbName       Name of database, like "mbrainz-1968-1973"
 * @param schema       Seq of schema transaction data from generated boilerplate code
 * @param attrMap      Map of attribute data from generated boilerplate code
 * @param testDbStatus Internally applied setting, not intended to be set by user
 * @param testDbView   Internally applied setting, not intended to be set by user
 * @param adhocDbView  Internally applied setting, not intended to be set by user
 * @param uuid         Internally applied setting, not intended to be set by user
 */
case class DatomicDevLocalProxy(
  protocol: String,
  system: String,
  storageDir: String,
  dbName: String,
  schema: Seq[String],
  attrMap: Map[String, (Int, String)],

  // Internal settings, not intended to be set by user
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends ConnProxy


/** Datomic Peer Server proxy connection
 *
 * @param accessKey    Peer Server access key
 * @param secret       Peer Server secret password
 * @param endpoint     Peer Server network endpoint, like "localhost:8998"
 * @param dbName       Name of database, like "mbrainz-1968-1973"
 * @param schema       Seq of schema transaction data from generated boilerplate code
 * @param attrMap      Map of attribute data from generated boilerplate code
 * @param testDbStatus Internally applied setting, not intended to be set by user
 * @param testDbView   Internally applied setting, not intended to be set by user
 * @param adhocDbView  Internally applied setting, not intended to be set by user
 * @param uuid         Internally applied setting, not intended to be set by user
 */
case class DatomicPeerServerProxy(
  accessKey: String,
  secret: String,
  endpoint: String,
  dbName: String,
  schema: Seq[String],
  attrMap: Map[String, (Int, String)],

  // Internal settings, not intended to be set by user
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends ConnProxy
