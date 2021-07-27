package molecule.core.macros.build.json

import molecule.core.macros.attrResolvers.JsonBase
import molecule.core.macros.build.BuildBase
import scala.reflect.macros.blackbox

trait BuildJson extends BuildBase with JsonBase {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildJson", 10)

  def jsonFlat(obj: BuilderObj, colIndex0: Int = -1, level: Int = 0): (Tree, Int) = {
    var colIndex    = colIndex0
    val tabs        = level + 1
    val newLineCode = Seq(
      q"""sb.append(${"," + indent(tabs)})"""
    )

    def properties(nodes: List[BuilderNode]): Seq[Tree] = {
      var next  = false
      var props = List.empty[String]
      nodes.flatMap { node =>
        val newLine = if (next) newLineCode else {
          next = true
          Nil
        }
        val trees   = node match {
          case BuilderProp(_, prop, _, _, json, _) =>
            colIndex += 1
            // Only generate 1 property, even if attribute is repeated in molecule
            if (props.contains(prop)) Nil else {
              props = props :+ prop
              newLine :+ json(colIndex, tabs)
            }

          case refObj@BuilderObj(_, ref, _, _) =>
            val (subObj, colIndexSub) = jsonFlat(refObj, colIndex, level + 1)
            colIndex = colIndexSub
            newLine ++ Seq(
              q"""quote(sb, $ref)""",
              q"""sb.append(${": {" + indent(tabs + 1)})""",
              q"""$subObj""",
              q"""sb.append(${indent(tabs) + "}"})""",
            )
        }
        trees
      }
    }

    val tree = if (hasSameNss(obj)) {
      q"""throw MoleculeException(
            "Please compose multiple same-name namespaces with `++` instead of `+` to access property values."
          )"""
    } else {
      q"{ ..${properties(obj.props)} }"
    }

    xx(1, obj, tree)
    (tree, colIndex)
  }
}
