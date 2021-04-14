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
import molecule.datomic.base.marshalling.DatomicPeerQueryExecutor
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

  // todo: refactor out Peer dependency here
  val router = Router[ByteBuffer, Future].route[QueryExecutor](DatomicPeerQueryExecutor)

  lazy val route: Route =
    path("ajax" / "QueryExecutor" / "query") {
      respondWithHeaders(`Access-Control-Allow-Origin`.*) {
        post {
          extractRequest { req =>
            req.entity match {
              case strict: HttpEntity.Strict =>
                // The last two segments of the ajax path are used to identify
                // api and method to call.
                val path = List("QueryExecutor", "query")

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

                  case Left(_) => ??? // error with router result
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
      }
    }


  val bindingFuture = Http()
    .newServerAt("localhost", 8080)
    .bind(route)
    .onComplete {
      case Success(b) => println(s"server is running ${b.localAddress} ")
      case Failure(e) => println(s"there was an error starting the server $e")
    }


  // todo: use https://github.com/lomigmegard/akka-http-cors ??
  lazy val routeCors: Route = cors() {
    respondWithHeaders(`Access-Control-Allow-Origin`.*) {
      path("ajax" / "QueryExecutor" / Remaining) {


        case "query" =>
          println("query")
          post {
            extractRequest { req =>
              req.entity match {
                case strict: HttpEntity.Strict =>
                  // The last two segments of the ajax path are used to identify
                  // api and method to call.
                  val path = List("QueryExecutor", "query")

                  // Unpickle param values
                  val pickler = Unpickle.apply[ByteBuffer]
                  val args    = pickler.fromBytes(strict.data.asByteBuffer)


                  println("ARGS: " + args)


                  // Execute api method with args
                  val routerResult: RouterResult[ByteBuffer, Future] =
                    router.apply(Request[ByteBuffer](path, args))

                  val resultFuture: Future[Array[Byte]] = routerResult.toEither match {
                    case Right(byteBufferResultFuture) =>
                      byteBufferResultFuture.map { byteBufferResult =>
                        val aa: ByteBuffer  = byteBufferResult
                        // Convert ByteBuffer to Array of Bytes
                        val dataAsByteArray = Array.ofDim[Byte](byteBufferResult.remaining())
                        byteBufferResult.get(dataAsByteArray)
                        // Send byte Array to HTTP response to Client that can received it as an ArrayBuffer
                        dataAsByteArray
                      }

                    case Left(_) => ??? // error with router result
                  }
                  val result                            = Await.result(resultFuture, 10.seconds) // Can we avoid blocking here?...

                  complete {
                    HttpEntity(
                      ContentTypes.`application/octet-stream`,
                      //                    Await.result(resultFuture, 10.seconds) // Can we avoid blocking here?...
                      result
                    )
                  }

                case _ =>
                  complete("Ooops, request entity is not strict!")
              }
            }
          }

        case "transact" => ??? // todo...

        case "hello" =>
          println("HELLO!")
          val a1 = ByteString("xx").asByteBuffer
          val a2 = Array.ofDim[Byte](a1.remaining())
          a1.get(a2)

          complete {
            HttpEntity(
              ContentTypes.`application/octet-stream`,
              a2
            )
          }
      }

    }
  }


  // todo: system.terminate() - shutdown when done..?
  // todo: security / authentication / cors ??
  // todo: log
}
