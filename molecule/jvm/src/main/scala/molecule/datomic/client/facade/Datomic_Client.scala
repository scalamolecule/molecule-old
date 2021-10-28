package molecule.datomic.client.facade

import datomicClient.ClojureBridge
import datomicScala.client.api.async.AsyncClient
import datomicScala.client.api.sync.Client
import molecule.core.marshalling.ConnProxy
import molecule.core.util.JavaConversions
import scala.concurrent.{ExecutionContext, Future}

/** Base Datomic facade for client api (peer-server/cloud/dev-local).
 *
 * @groupname database  Database operations
 * @groupprio 10
 *
 * @param client
 * @param clientAsync
 */
abstract class Datomic_Client(val client: Client)
  extends ClojureBridge with JavaConversions {

  def connect(dbName: String, connProxy: ConnProxy)
             (implicit ec: ExecutionContext): Future[Conn_Client]
}

