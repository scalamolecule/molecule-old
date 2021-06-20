package molecule.core.marshalling

import java.nio.ByteBuffer
import cats.implicits._
import sloth._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait WebClient extends Serializations {

  protected def clientAjax(baseAjaxUri: String): Client[ByteBuffer, Future, ClientException] =
    Client[ByteBuffer, Future, ClientException](WebTransportAjax(baseAjaxUri))

  protected def clientWs(wsUri: String): Client[ByteBuffer, Future, ClientException] =
    Client[ByteBuffer, Future, ClientException](WebTransportWebSocket(wsUri))
}