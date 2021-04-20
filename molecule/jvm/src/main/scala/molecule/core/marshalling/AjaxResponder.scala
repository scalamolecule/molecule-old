package molecule.core.marshalling

import java.nio.ByteBuffer
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.`Access-Control-Allow-Origin`
import akka.http.scaladsl.server.Directives.{path, _}
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import boopickle.Default._
import cats.implicits._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import molecule.datomic.base.marshalling.DatomicRpc
import playing.sloth.Serializations
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import sloth._

/** Akka Http Ajax responder implementation
  *
  * Any server solution that respond on
  *
  */
object AjaxResponder extends App with Serializations {
  implicit val system           = ActorSystem(Behaviors.empty, "moleculeAjax")
  implicit val executionContext = system.executionContext

  val router = Router[ByteBuffer, Future].route[MoleculeRpc](DatomicRpc)

  lazy val routeCors: Route = cors() {
    path("ajax" / "MoleculeRpc" / Remaining) {
      case "queryAsync" =>
        post {
          extractRequest { req =>
            req.entity match {
              case strict: HttpEntity.Strict =>
                // The last two segments of the ajax path are used to identify
                // api and method to call.
                val path = List("MoleculeRpc", "queryAsync")

                // Unpickle param values
                val pickler = Unpickle.apply[ByteBuffer]
                val args    = pickler.fromBytes(strict.data.asByteBuffer)

                // Execute DatomicPeerQueryExecutor.query with args
                val routerResult: RouterResult[ByteBuffer, Future] =
                  router.apply(Request[ByteBuffer](path, args))

                val resultFuture: Future[Array[Byte]] = routerResult.toEither match {
                  case Right(byteBufferResultFuture) =>
                    byteBufferResultFuture.map { byteBufferResult =>
                      // Convert ByteBuffer to Array of Bytes
                      val dataAsByteArray = Array.ofDim[Byte](byteBufferResult.remaining())
                      byteBufferResult.get(dataAsByteArray)
                      // Send byte Array to HTTP response to Client that can received it as an ArrayBuffer
                      dataAsByteArray
                    }

                  case Left(err) =>
                    println("routerResult error: " + err)
                    ??? // error with router result
                }

                complete {
                  HttpEntity(
                    ContentTypes.`application/octet-stream`,
                    Await.result(resultFuture, 10.seconds) // todo: avoid blocking
                  )
                }

              case _ =>
                complete("Ooops, request entity is not strict!")
            }
          }
        }

      //        case "transact" => ??? // todo...
      //
      //        case "hello" =>
      //          println("HELLO!")
      //          val a1 = ByteString("xx").asByteBuffer
      //          val a2 = Array.ofDim[Byte](a1.remaining())
      //          a1.get(a2)
      //
      //          complete {
      //            HttpEntity(
      //              ContentTypes.`application/octet-stream`,
      //              a2
      //            )
      //          }
    }
  }


  val bindingFuture = Http()
    .newServerAt("localhost", 8080)
    .bind(routeCors)
    .onComplete {
      case Success(b) => println(s"server is running ${b.localAddress} ")
      case Failure(e) => println(s"there was an error starting the server $e")
    }

  //   todo: system.terminate() - shutdown when done..
  //   todo: security, authentication, cors, log
}
