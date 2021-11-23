package molecule.core.marshalling

import java.nio.ByteBuffer
import cats.implicits._
import sloth._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait WebClient extends BooPicklers {

  def moleculeAjax(interface: String, port: Int): Client[ByteBuffer, Future, ClientException] =
    Client[ByteBuffer, Future, ClientException](MoleculeAjax(interface, port))

  def moleculeWs(wsUri: String): Client[ByteBuffer, Future, ClientException] =
    Client[ByteBuffer, Future, ClientException](MoleculeWebSocket(wsUri))
}