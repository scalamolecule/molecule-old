package molecule.core.marshalling

import java.nio.ByteBuffer
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import boopickle.Default._
import cats.implicits._
import molecule.datomic.base.marshalling.DatomicRpc
import sloth._
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


/** Akka Http WebSocket responder implementation
  *
  * todo: doesn't work yet...
  *
  */
//object WsResponder extends App with Serializations { // un-comment this to run it
object WsResponder extends Serializations {
  implicit val system           = ActorSystem(Behaviors.empty, "moleculeWebSocket")
  implicit val executionContext = system.executionContext

  lazy val router = Router[ByteBuffer, Future].route[MoleculeRpc](DatomicRpc)

  lazy val route: Route =
    path("ws") {
      handleWebSocketMessages {
        Flow[Message]
          .mapAsync(parallelism = 2) {
            case BinaryMessage.Strict(byteString) =>
              val pickler      = Unpickle.apply[(List[String], ByteBuffer)]
              val (path, args) = pickler.fromBytes(byteString.asByteBuffer)

              println("path: " + path)

              val request      = Request[ByteBuffer](path, args)
              val routerResult = router.apply(request)
              routerResult.toEither match {
                case Right(byteBufferResultFuture) =>
                  byteBufferResultFuture.map { byteBufferResult =>
                    BinaryMessage(ByteString(byteBufferResult))
                  }

                case Left(serverFailure) =>
                  println("serverFailure: " + serverFailure)
                  Future(BinaryMessage(ByteString("Websocket server failure: " + serverFailure)))
              }

            case other =>
              println("OTHER: " + other)
              throw new IllegalArgumentException("Unexpected Websocket Message: " + other)
          }
          //          .withAttributes(supervisionStrategy(resumingDecider))
          .keepAlive(20.seconds, () => {
            println("keep alive")
            TextMessage("keepalive")
          })
          .via(reportErrorsFlow) // ... then log any processing errors on stdin
      }
    }


  def reportErrorsFlow[T]: Flow[T, T, Any] =
    Flow[T]
      .watchTermination()((_, f) => f.onComplete {
        case Failure(cause) =>
          println(s"WS stream failed with $cause")
          Future(BinaryMessage(ByteString("Websocket server failure: " + cause)))
        case _              => // ignore regular completion
      })

  Http()
    .newServerAt("localhost", 8080)
    .bind(route)
    .onComplete {
      case Success(b) => println(s"WebSocket server is running ${b.localAddress} ")
      case Failure(e) => println(s"there was an error starting the server $e")
    }
}
