package molecule.core.marshalling

import java.nio.ByteBuffer
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import boopickle.Default._
import cats.implicits._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import molecule.datomic.base.marshalling.DatomicRpc
import sloth.ServerFailure._
import sloth._
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

/** Akka Http Ajax responder implementation
  *
  * Any server solution that respond on
  *
  */
object AjaxResponder extends App with Serializations {
  implicit val system           = ActorSystem(Behaviors.empty, "moleculeAjax")
  implicit val executionContext = system.executionContext

  lazy val router = Router[ByteBuffer, Future].route[MoleculeRpc](DatomicRpc)

  val MoleculeRpc = "MoleculeRpc"

  lazy val route: Route = cors() {
    path("ajax" / MoleculeRpc / Remaining)(respond)
  }

  val respond: String => Route = (method: String) => post {
    extractRequest { req =>
      req.entity match {
        case strict: HttpEntity.Strict =>
          val path       = List(MoleculeRpc, method)
          val args       = Unpickle.apply[ByteBuffer].fromBytes(strict.data.asByteBuffer)
          val callResult = router.apply(Request[ByteBuffer](path, args))
          val futResult  = callResult.toEither match {
            case Right(byteBufferResultFuture) => byteBufferResultFuture
              .map { bytes =>
                val dataLength                    = bytes.remaining()
                val bytesAsByteArray: Array[Byte] = Array.ofDim[Byte](dataLength + 1)

                // Reserve first byte for exception flag
                bytes.get(bytesAsByteArray, 1, dataLength)

                // Set first byte as a flag (0) for no exception thrown
                bytesAsByteArray.update(0, 0)
                bytesAsByteArray
              }
              .recover { exc =>
                println("---- Error in AjaxResponder ---------------------\n" + exc)
                println(exc.getStackTrace.mkString("\n"))
                try {
                  serializeException(exc)
                } catch {
                  case NonFatal(exceptionSerializationException) =>
                    println("Internal unexpected exception serialization error:\n" + exceptionSerializationException)
                    serializeException(exceptionSerializationException)
                }
              }

            case Left(err) => {
              println(s"ServerFailure: " + err)
              err match {
                case PathNotFound(path: List[String])  => Future.failed(new RuntimeException(s"PathNotFound($path)"))
                case HandlerError(exc: Throwable)      => Future.failed(exc)
                case DeserializerError(exc: Throwable) => Future.failed(exc)
              }
            }
          }
          complete(futResult)

        case _ => complete("Ooops, request entity is not strict!")
      }
    }
  }

  Http()
    .newServerAt("localhost", 8080)
    .bind(route)
    .onComplete {
      case Success(b) => println(s"Ajax server is running ${b.localAddress} ")
      case Failure(e) => println(s"there was an error starting the server $e")
    }


  def serializeException(exc: Throwable) = {
    val bytes            = Pickle.intoBytes(exc)
    val dataLength       = bytes.remaining()
    val bytesAsByteArray = Array.ofDim[Byte](dataLength + 1)

    // Reserve first byte for exception flag
    bytes.get(bytesAsByteArray, 1, dataLength)

    // Set first byte as a flag (1) for exception thrown
    bytesAsByteArray.update(0, 1)
    bytesAsByteArray
  }
}
