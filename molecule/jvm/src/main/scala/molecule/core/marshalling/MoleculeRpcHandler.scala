package molecule.core.marshalling

import java.nio.ByteBuffer
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.util.ByteString
import boopickle.Default._
import sloth.ServerFailure._
import sloth._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.control.NonFatal

case class MoleculeRpcHandler(interface: String, port: Int) extends BooPicklers {
  implicit val system          : ActorSystem[Nothing]     = ActorSystem(Behaviors.empty, "MoleculeAjaxSystem")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  val MoleculeRpc = "MoleculeRpc"


  def moleculeRpcResult(
    router: Router[ByteBuffer, Future],
    pathStr: String,
    data: ByteString
  ): Future[Array[Byte]] = Future {
    try {
      val path       = pathStr.split("/").toList
      val args       = Unpickle.apply[ByteBuffer].fromBytes(data.asByteBuffer)
      val callResult = router.apply(Request[ByteBuffer](path, args))
      callResult.toEither match {
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
          .recover {
            case exc: Throwable =>
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

        case Left(err) =>
          println(s"##### ServerFailure: " + err)
          err match {
            case PathNotFound(path: List[String])  => Future.failed(new RuntimeException(s"PathNotFound($path)"))
            case HandlerError(exc: Throwable)      => Future.failed(new RuntimeException(s"HandlerError(${exc.getMessage})"))
            case DeserializerError(exc: Throwable) => Future.failed(new RuntimeException(s"DeserializerError(${exc.getMessage})"))
          }
      }
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }.flatten


  private def serializeException(exc: Throwable): Array[Byte] = {
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
