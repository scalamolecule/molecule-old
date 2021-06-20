package molecule.core.marshalling

import java.nio.ByteBuffer
import boopickle.Default._
import org.scalajs.dom.ext.{Ajax, AjaxException}
import sloth._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.typedarray._

case class WebTransportAjax(baseAjaxUri: String)
  extends RequestTransport[ByteBuffer, Future] with Serializations {

  override def apply(req: Request[ByteBuffer]): Future[ByteBuffer] = {
    Ajax.post(
      url = baseAjaxUri + "/" + req.path.mkString("/"),
      data = Pickle.intoBytes(req.payload), // Param values
      responseType = "arraybuffer",
      headers = Map("Content-Type" -> "application/octet-stream"),
    ).recover {
      // Failed Future
      // Catch ajax exception and alert user
      case AjaxException(xhr) =>
        val advice = "\nNew api methods might not be visible as js-code upon a refresh " +
          "since the sloth macro needs to generate them first. " +
          "\nCached js-code can also become out of date. " +
          "\nSo you can try to stop the server from the terminal (ctrl-c twice), " +
          "run `sbt`, `clean` and then `run`. " +
          "\nThis should allow new signatures to propagate to js-code."
        val msg    = xhr.status match {
          case 0 => s"Ajax call failed: server not responding. $advice"
          case n => s"Ajax call failed: XMLHttpRequest.status = $n. $advice"
        }
        println(msg)
        xhr
    }.flatMap { req =>
      val raw       = req.response.asInstanceOf[ArrayBuffer]
      val dataBytes = TypedArrayBuffer.wrap(raw.slice(1))

      // Check first byte as flag of whether exception has been thrown or not
      if (TypedArrayBuffer.wrap(raw.slice(0, 1)).get(0) == 1) {
        // Failed future with exception
        Future.failed(Unpickle.apply[Throwable].fromBytes(dataBytes))
      } else {
        // Successful future with data
        Future.successful(dataBytes)
      }

      // todo: use exception flag from response header - need to find way to get it via akka-http
      //        val dataBytes: ByteBuffer = TypedArrayBuffer.wrap(req.response.asInstanceOf[ArrayBuffer])
      //        if (req.getResponseHeader("throwable") == "yes") {
      //          Future.failed(Unpickle.apply[Throwable].fromBytes(dataBytes))
      //        } else {
      //          Future.successful(dataBytes)
      //        }
    }
  }
}
