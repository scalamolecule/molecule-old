package moleculeTests

import java.nio.ByteBuffer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import boopickle.Default._
import cats.implicits._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import molecule.core.marshalling.{MoleculeRpc, MoleculeRpcResponse}
import molecule.core.util.Executor.global
import molecule.datomic.base.marshalling.DatomicRpc
import sloth._
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

/** Akka Http RPC server responding to molecule ajax requests */
object MoleculeRpcServer extends MoleculeRpcResponse("localhost", 8080) with App {

  lazy val router = Router[ByteBuffer, Future].route[MoleculeRpc](DatomicRpc()(global))

  Http()
    .newServerAt(interface, port)
    .bind(route)
    .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 120.seconds))
    .onComplete {
      case Success(b) => println(s"Ajax server is running ${b.localAddress} ")
      case Failure(e) => println(s"there was an error starting the server $e")
    }

  lazy val route: Route = cors() {
    // Remaining is the method name
    path("ajax" / MoleculeRpc / Remaining)(respond)
  }

  val respond: String => Route = (method: String) => {
    val pathStr = s"$MoleculeRpc/$method"
    post {
      extractRequest { req =>
        req.entity match {
          case HttpEntity.Strict(_, argsData) =>
            complete(moleculeRpcResponse(router, pathStr, argsData.asByteBuffer))

          case HttpEntity.Default(_, _, chunks) =>
            complete(
              chunks.reduce(_ ++ _)
                .runFoldAsync(Array.empty[Byte]) {
                  case (_, argsData) => moleculeRpcResponse(router, pathStr, argsData.asByteBuffer)
                }
            )

          case other => complete("Unexpected HttpEntity in MoleculeRpcServer: " + other)
        }
      }
    }
  }
}
