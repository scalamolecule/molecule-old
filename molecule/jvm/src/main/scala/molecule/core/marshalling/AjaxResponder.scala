package molecule.core.marshalling

import java.nio.ByteBuffer
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
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
object AjaxResponder extends App with Picklers {
  implicit val system           = ActorSystem(Behaviors.empty, "moleculeAjax")
  implicit val executionContext = system.executionContext

  lazy val router = Router[ByteBuffer, Future]
    .route[MoleculeRpc](DatomicRpc)
  //    .route[MoleculeRpc](DatomicRpc)

  val MoleculeRpc = "MoleculeRpc"

  lazy val route: Route = cors() {
    path("ajax" / MoleculeRpc / Remaining) {
      case "queryAsync"    => queryAsync("queryAsync")
//      case "transactAsync" => transactAsync("transactAsync")
      case other           => throw new IllegalArgumentException(
        s"Route for `$other` not found."
      )
    }
  }

  val bindingFuture = Http()
    .newServerAt("localhost", 8080)
    .bind(route)
    .onComplete {
      case Success(b) => println(s"server is running ${b.localAddress} ")
      case Failure(e) => println(s"there was an error starting the server $e")
    }

  //   todo: system.terminate() - shutdown when done..
  //   todo: security, authentication, cors, log

  def transactAsync(method: String) = post {
    extractRequest { req =>
      req.entity match {
        case strict: HttpEntity.Strict =>
          val path       = List(MoleculeRpc, method)
          val pickler    = Unpickle.apply[ByteBuffer]
          val args       = pickler.fromBytes(strict.data.asByteBuffer)
          val callResult = router.apply(Request[ByteBuffer](path, args))
          val futResult: Future[Array[Byte]] = callResult.toEither match {
            case Right(byteBufferResultFuture) =>
              byteBufferResultFuture.map { byteBufferResult =>
                val dataAsByteArray = Array.ofDim[Byte](byteBufferResult.remaining())
                byteBufferResult.get(dataAsByteArray)
                dataAsByteArray
              }
            case Left(err)                     =>
              throw new RuntimeException("transactAsync error: " + err)
          }
          complete {
            HttpEntity(
              ContentTypes.`application/octet-stream`,
              Await.result(futResult, 10.seconds) // todo: doesn't seem right...
            )
          }

        case _ => complete("Ooops, request entity is not strict!")
      }
    }
  }

  def queryAsync(method: String) = post {
    extractRequest { req =>
      req.entity match {
        case strict: HttpEntity.Strict =>
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
              throw new RuntimeException("transactAsync error: " + err)
          }

          complete {
            HttpEntity(
              ContentTypes.`application/octet-stream`,
              Await.result(futResult, 10.seconds) // todo...
            )
          }

        case _ => complete("Ooops, request entity is not strict!")
      }
    }
  }

}
