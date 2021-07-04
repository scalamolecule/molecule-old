package molecule.core.macros.build.json

import molecule.core.macros.build.BuildBase
import scala.collection.mutable.ListBuffer
import scala.reflect.macros.blackbox


trait BuildJsonComposite extends BuildBase {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildJson", 1, 900)

  def compositeJsons(jsonss: List[List[(Int, Int) => Tree]]): ListBuffer[Tree] = {
    var fieldIndex = -1
    var firstGroup = true
    var firstPair  = true
    val buf        = new ListBuffer[Tree]
    jsonss.foreach { jsonLambdas =>
      if (firstGroup) firstGroup = false else buf.append(q"""sb.append(", ")""")
      buf.append(q"""sb.append("{")""")
      firstPair = true
      jsonLambdas.foreach { jsonLambda =>
        fieldIndex += 1
        if (firstPair) firstPair = false else buf.append(q"""sb.append(", ")""")
        buf.append(jsonLambda(fieldIndex, 0)) // level 0 ok?
      }
      buf.append(q"""sb.append("}")""")
    }
    buf
  }

  def jsonComposite(jsonss:  List[List[(Int, Int) => Tree]]): Tree = {


    q"""
        {
            ..${compositeJsons(jsonss)}
          }
       """
  }

}
