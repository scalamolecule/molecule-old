package molecule.core.marshalling

import java.nio.ByteBuffer
import boopickle.Default._
import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}
import sloth._
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.typedarray.TypedArrayBufferOps.byteBufferOps
import scala.scalajs.js.typedarray._

case class WebTransportWebSocket(wsUri: String) extends RequestTransport[ByteBuffer, Future] {

  lazy val socket = {
    val ws = new WebSocket(wsUri)
    ws.binaryType = "arraybuffer"
    ws.onerror = { e: Event =>
      println(s"WebSocket error: $e!")
      ws.close(0, e.toString)
    }
    ws.onclose = { _: CloseEvent =>
      println("WebSocket closed")
    }
    ws
  }

  override def apply(req: Request[ByteBuffer]): Future[ByteBuffer] = {
    // Request
    socket.readyState match {
      case WebSocket.OPEN =>
        socket.send(
          Pickle.intoBytes((req.path, req.payload)).typedArray().buffer
        )

      case WebSocket.CONNECTING =>
        println("WebSocket connecting...")
        socket.onopen = { _: Event =>
          println("WebSocket connected")
          socket.send(
            Pickle.intoBytes((req.path, req.payload)).typedArray().buffer
          )
        }

      case _ =>
        throw new IllegalStateException("Unexpected close/closing WebSocket")
    }

    // Response
    val promise = Promise[ByteBuffer]()
    socket.onmessage = { e: MessageEvent =>
      promise.trySuccess {
        TypedArrayBuffer.wrap(e.data.asInstanceOf[ArrayBuffer])
      }
    }

    promise.future
  }
}
