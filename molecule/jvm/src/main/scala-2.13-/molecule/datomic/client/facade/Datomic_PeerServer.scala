package molecule.datomic.client.facade

import datomicScala.client.api.async.AsyncDatomic
import datomicScala.client.api.sync.Datomic
import molecule.core.marshalling.{ConnProxy, DatomicInMemProxy}
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._
import scala.util.control.NonFatal


/** Datomic facade for peer-server.
  *
  * @param accessKey
  * @param secret
  * @param endpoint
  * @param validateHostnames
  */
case class Datomic_PeerServer(
  accessKey: String,
  secret: String,
  endpoint: String,
  validateHostnames: Boolean = false
) extends Datomic_Client(
  Datomic.clientPeerServer(accessKey, secret, endpoint, validateHostnames),
  AsyncDatomic.clientPeerServer(accessKey, secret, endpoint, validateHostnames)
) {

  private val inMemConnProxy: ConnProxy = DatomicInMemProxy(Nil, Map.empty[String, (Int, String)])

  def connectionError(msg: String) = throw new RuntimeException(
    "\nPeer Server not running. Please start it with something like" +
      "\nbin/run -m datomic.peer-server -h localhost -p 8998 -a key,secret -d db-name,datomic:mem://db-name" +
      "\nhttps://docs.datomic.com/on-prem/peer-server.html\n" + msg
  )


  def connect(dbName: String, connProxy: ConnProxy = inMemConnProxy)
             (implicit ec: ExecutionContext): Future[Conn_Client] = try {
    s"curl -k -S -s https://$endpoint/health".!!.trim match {
      case "ok" =>
        getServedDatabases().map { servedDbs =>
          if (servedDbs.isEmpty)
            throw new RuntimeException("Found no database served by the Peer Server")

          if (!servedDbs.contains(dbName))
            throw new RuntimeException(
              s"Couldn't find db `$dbName` among databases currently served by the Peer Server:\n" +
                servedDbs.mkString("\n")
            )

          Conn_Client(client, clientAsync, dbName, endpoint, connProxy)
        }
      case err  => Future.failed(connectionError(err))
    }

  } catch {
    case NonFatal(exc) => Future.failed(connectionError(exc.toString))
  }

  def getServedDatabases(timeout: Int = 0)
                        (implicit ec: ExecutionContext): Future[List[String]] = Future {
    client.listDatabases(timeout).asScala.toList.sorted
  }
}

