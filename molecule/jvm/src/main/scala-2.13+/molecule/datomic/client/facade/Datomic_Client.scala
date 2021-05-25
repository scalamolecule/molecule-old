package molecule.datomic.client.facade

import datomicClient.ClojureBridge
import datomicScala.client.api.async.AsyncClient
import datomicScala.client.api.sync.Client
import scala.concurrent.{ExecutionContext, Future}

/** Base Datomic facade for client api (peer-server/cloud/dev-local).
 *
 * @groupname database  Database operations
 * @groupprio 10
 *
 * @param client
 * @param clientAsync
 */
abstract class Datomic_Client(val client: Client, val clientAsync: AsyncClient)
  extends ClojureBridge {

  def connect(dbName: String)(implicit ec: ExecutionContext): Future[Conn_Client]
}

