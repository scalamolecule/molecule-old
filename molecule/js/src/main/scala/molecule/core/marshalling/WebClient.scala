package molecule.core.marshalling

import java.nio.ByteBuffer
import cats.implicits._
import molecule.core.util.Executor._
import sloth._
import scala.concurrent.Future

trait WebClient extends BooPicklers {

  def moleculeAjax(interface: String, port: Int): ClientCo[ByteBuffer, Future] =
    Client[ByteBuffer, Future](MoleculeRpcRequest(interface, port))
}