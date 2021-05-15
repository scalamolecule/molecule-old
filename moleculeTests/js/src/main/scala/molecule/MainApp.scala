package molecule

import boopickle.Default.Pickle
import org.scalajs.dom
import org.scalajs.dom.ext.{Ajax, AjaxException}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise


object MainApp extends App {


  // testing..
  Ajax.get("https://httpbin.org/get").foreach(req => println(req.status))


}
