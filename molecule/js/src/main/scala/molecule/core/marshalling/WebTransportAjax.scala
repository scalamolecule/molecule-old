package molecule.core.marshalling

import java.nio.ByteBuffer
import boopickle.Default._
import org.scalajs.dom
import org.scalajs.dom.ext.AjaxException
import sloth._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.typedarray._

case class WebTransportAjax(baseAjaxUri: String) extends RequestTransport[ByteBuffer, Future] {

  override def apply(req: Request[ByteBuffer]): Future[ByteBuffer] = {

    // Request
    dom.ext.Ajax.post(
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

        // Pass XMLHttpRequest causing the exception (in Future) to client
        xhr

      case err =>
        println("Ajax post error: " + err)
        throw new RuntimeException(err)

    }.map { r =>
      // Success Future with response
      TypedArrayBuffer.wrap(r.response.asInstanceOf[ArrayBuffer])
    }
  }
}
