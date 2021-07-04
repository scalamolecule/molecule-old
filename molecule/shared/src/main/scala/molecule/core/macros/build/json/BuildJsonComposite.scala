//package molecule.core.macros.build.json
//
//import molecule.core.macros.build.BuildBase
//import scala.collection.mutable.ListBuffer
//import scala.reflect.macros.blackbox
//
//
//trait BuildJsonComposite extends BuildBase {
//  val c: blackbox.Context
//
//  import c.universe._
//
//  private lazy val xx = InspectMacro("BuildJsonComposite", 1)
//
//  def compositeJsons(jsonss: List[List[(Int, Int) => Tree]]): ListBuffer[Tree] = {
//    var fieldIndex = -1
//    var firstGroup = true
//    var firstPair  = true
//    val buf        = new ListBuffer[Tree]
//    jsonss.foreach { jsonLambdas =>
//      if (firstGroup) firstGroup = false else buf.append(q"""sb.append(", ")""")
//      buf.append(q"""sb.append("{")""")
//      firstPair = true
//      jsonLambdas.foreach { jsonLambda =>
//        fieldIndex += 1
//        if (firstPair) firstPair = false else buf.append(q"""sb.append(", ")""")
//        buf.append(jsonLambda(fieldIndex, 0)) // level 0 ok?
//      }
//      buf.append(q"""sb.append("}")""")
//    }
//    buf
//  }
//
//
////  def jsonComposite(obj: BuilderObj, jsonss:  List[List[(Int, Int) => Tree]], colIndex0: Int = -1, level: Int = 0): Tree = {
//  def jsonComposite(obj: BuilderObj, colIndex0: Int = -1, level: Int = 0): (Tree, Int) = {
//    var colIndex    = colIndex0
//    val tabs        = level + 1
//    val newLineCode = Seq(
//      q"""sb.append(",")""",
//      q"""sb.append(indent($tabs))"""
//    )
//
//    def properties(nodes: List[BuilderNode]): Seq[Tree] = {
//      var next       = false
//      var fieldNames = List.empty[String]
//      nodes.flatMap { node =>
//        val newLine = if (next) newLineCode else {
//          next = true
//          Nil
//        }
//        val trees   = node match {
//          case BuilderProp(_, fieldName, tpe, _, json, optAggr) =>
//            colIndex += 1
//            // Only generate 1 property, even if attribute is repeated in molecule
//            if (fieldNames.contains(fieldName)) Nil else {
//              fieldNames = fieldNames :+ fieldName
//              newLine ++ (optAggr match {
//                case None                                       => Seq(json(colIndex, tabs))
//                case Some(aggrTpe) if aggrTpe == tpe.toString() => Seq(json(colIndex, tabs))
//                case Some(aggrTpe)                              => abort(
//                  s"Field `$fieldName` not available since the aggregate changes its type to `$aggrTpe`. " +
//                    s"Please use tuple output instead to access aggregate value."
//                )
//              })
//            }
//
//          case refObj@BuilderObj(_, ref, _, _) =>
//            val (subObj, colIndex2) = jsonComposite(refObj, colIndex, level + 1)
//            colIndex = colIndex2
//            newLine ++ Seq(
//              q"""quote(sb, $ref)""",
//              q"""sb.append(": {")""",
//              q"""sb.append(indent(${tabs + 1}))""",
//              q"""$subObj""",
//              q"""sb.append(indent($tabs))""",
//              q"""sb.append("}")""",
//            )
//        }
//        trees
//      }
//    }
//
////    val tree = q"{..${compositeJsons(jsonss)}}"
//    val tree = q"{..${properties(obj.props)}}"
//
//    xx(1, obj, tree)
//    (tree, colIndex)
//  }
//}
