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
            case Right(byteBufferResultFuture) =>
              byteBufferResultFuture
                .map { byteBufferResult =>
                  val dataAsByteArray = Array.ofDim[Byte](byteBufferResult.remaining())
                  byteBufferResult.get(dataAsByteArray)
                  dataAsByteArray
                }
                .recoverWith { t =>
                  println("-------------------------------------------")
                  println("Unexpected exception in future: " + t)
                  println(t.getStackTrace.mkString("\n"))
                  val t2: Throwable   = t
                  val tb              = Pickle.intoBytes(t2)
                  val dataAsByteArray = Array.ofDim[Byte](tb.remaining())
                  tb.get(dataAsByteArray)
                  // Disrupt ajax call
                  Future.failed(t)
                }
            //  .recover { t =>
            //    println("Unexpected exception in future: " + t)
            //    println(t.getStackTrace.mkString("\n"))
            //    val t2: Throwable   = t
            //    val tb              = Pickle.intoBytes(t2)
            //    val dataAsByteArray = Array.ofDim[Byte](tb.remaining())
            //    tb.get(dataAsByteArray)
            //    dataAsByteArray
            //  }

            case Left(err) => {
              println(s"ServerFailure: " + err)
              err match {
                case PathNotFound(path: List[String]) => Future.failed(new RuntimeException(s"PathNotFound($path)"))
                case HandlerError(ex: Throwable)      => Future.failed(ex)
                case DeserializerError(ex: Throwable) => Future.failed(ex)
              }
            }
          }
          complete(futResult)

        case _ =>
          println("Ooops, request entity is not strict!")
          complete("Ooops, request entity is not strict!")
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
