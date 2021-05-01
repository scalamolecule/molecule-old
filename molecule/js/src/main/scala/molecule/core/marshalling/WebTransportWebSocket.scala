package molecule.core.marshalling

import java.nio.ByteBuffer
import boopickle.Default._
import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}
import sloth._
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.typedarray.TypedArrayBufferOps.byteBufferOps
import scala.scalajs.js.typedarray._

case class WebTransportWebSocket(wsUri: String) extends RequestTransport[ByteBuffer, Future] {

  val socket = new WebSocket(wsUri)
  socket.binaryType = "arraybuffer"
  socket.onerror = { e: Event =>
    println(s"WebSocket error: $e! ")
    socket.close(1000, e.toString)
  }
  socket.onclose = { _: CloseEvent =>
    println("WebSocket closed")
  }

  override def apply(req: Request[ByteBuffer]): Future[ByteBuffer] = {
    // Request
    socket.readyState match {
      case WebSocket.OPEN =>
        println("WebSocket OPEN      : " + req.path + "   ")
        socket.send(
          Pickle.intoBytes((req.path, req.payload)).typedArray().buffer
        )

      case WebSocket.CONNECTING =>
        println("WebSocket connecting: " + req.path + "   ")
        socket.onopen = { _: Event =>
          println("WebSocket connected : " + req.path + "   ")
          socket.send(
            Pickle.intoBytes((req.path, req.payload)).typedArray().buffer
          )
        }

      case other =>
        println("Unexpected close/closing WebSocket " + other)
        throw new IllegalStateException("Unexpected close/closing WebSocket")
    }

    // Response
    val promise = Promise[ByteBuffer]()
    socket.onmessage = { e: MessageEvent =>
      println("onmessage... ")
      promise.trySuccess {
        TypedArrayBuffer.wrap(e.data.asInstanceOf[ArrayBuffer])
      }
    }

    promise.future
  }
}
