package molecule.datomic.client.facade

import datomicClient.ClojureBridge
import datomicScala.client.api.sync.Client
import molecule.core.util.JavaConversions

/** Base Datomic facade for client api (peer-server/cloud/dev-local).
 *
 * @groupname database  Database operations
 * @groupprio 10
 *
 * @param client
 */
abstract class Datomic_Client(val client: Client) extends ClojureBridge with JavaConversions