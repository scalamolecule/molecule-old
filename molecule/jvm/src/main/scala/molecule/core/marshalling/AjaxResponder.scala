package molecule.core.marshalling

import java.nio.ByteBuffer
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import boopickle.Default._
import cats.implicits._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import molecule.datomic.base.marshalling.DatomicRpc
import sloth._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
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
          try {
            val path       = List(MoleculeRpc, method)
            val pickler    = Unpickle.apply[ByteBuffer]
            val args       = pickler.fromBytes(strict.data.asByteBuffer)
            val callResult = router.apply(Request[ByteBuffer](path, args))
            val futResult  = callResult.toEither match {
              case Right(byteBufferResultFuture) =>
                byteBufferResultFuture.map { byteBufferResult =>
                  val dataAsByteArray = Array.ofDim[Byte](byteBufferResult.remaining())
                  byteBufferResult.get(dataAsByteArray)
                  dataAsByteArray
                }
              case Left(err)                     =>
                Future.failed(new RuntimeException(s"$method error: " + err))
            }
            complete {
              futResult.map { res =>
                HttpEntity(ContentTypes.`application/octet-stream`, res)
              }
            }

          } catch {
            case t: Throwable =>
              println("AjaxResponder error encoding response: " + t)
              complete("AjaxResponder error encoding response: " + t)
              // todo: make failed Future work
              val futResult: Future[Array[Byte]] = Future.failed(t)
              complete {
                futResult.map { res =>
                  HttpEntity(ContentTypes.`application/octet-stream`, res)
                }
              }
          }

        case _ =>
          complete("Ooops, request entity is not strict!")
          // todo: make failed Future work
          val futResult: Future[Array[Byte]] = Future.failed(new RuntimeException("Ooops, request entity is not strict!"))
          complete {
            futResult.map { res =>
              HttpEntity(ContentTypes.`application/octet-stream`, res)
            }
          }
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
}
