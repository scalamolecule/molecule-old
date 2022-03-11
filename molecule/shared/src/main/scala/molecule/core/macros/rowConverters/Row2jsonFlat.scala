package molecule.core.macros.rowConverters

import molecule.core.macros.rowAttr.{JsonBase, RowValue2json}
import molecule.core.marshalling.nodes._
import scala.reflect.macros.blackbox

trait Row2jsonFlat extends JsonBase with RowValue2json {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("Row2jsonFlat", 2)

  def jsonFlat(obj: Obj): Tree = {
    val tree = if (hasSameNss(obj)) {
      q"""throw MoleculeException(
        "Please compose multiple same-name namespaces with `++` instead of `+` to access property values."
      )"""
    } else {
      q"""{
        sb.append("\n      {\n        ")
        ..${resolve(obj)._1}
        sb.append("\n      }")
      }"""
    }

    xx(1, obj, tree)
    tree
  }

  def resolve(obj: Obj, colIndex0: Int = -1, level: Int = 0): (Tree, Int) = {
    var colIndex    = colIndex0
    val tabs        = level + 1
    val newLineCode = Seq(
      q"""sb.append(${"," + indent(tabs)})"""
    )

    def properties(nodes: List[Node]): Seq[Tree] = {
      var next  = false
      var props = List.empty[String]
      nodes.flatMap { node =>
        val newLine = if (next) newLineCode else {
          next = true
          Nil
        }
        val trees   = node match {
          case Prop(_, prop, baseTpe, _, group, _) =>
            colIndex += 1
            // Only generate 1 property, even if attribute is repeated in molecule
            if (props.contains(prop)) Nil else {
              props = props :+ prop
              newLine :+ getRowValue2jsonLambda(group, baseTpe, prop)(colIndex, tabs)
            }

          case refObj@Obj(_, ref, _, _) =>
            val (subObj, colIndexSub) = resolve(refObj, colIndex, level + 1)
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

    val tree = q"{ ..${properties(obj.props)} }"

    xx(1, obj, tree)
    (tree, colIndex)
  }
}
