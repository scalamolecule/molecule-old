package molecule.core.macros.build.json

import molecule.core.macros.attrResolvers.JsonBase
import molecule.core.macros.build.BuildBase
import scala.reflect.macros.blackbox

private[molecule] trait BuildJsonOptNested extends BuildBase with JsonBase {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildJsonOptNested", 2, 1)

  def jsonOptNested(
    current: BuilderObj,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    level: Int = 0,
    tabs: Int = 0
  ): Tree = {
    val newLineCode = Seq(
      q"""sb.append(${"," + indent(tabs + 1)})"""
    )

    def properties(nodes: List[BuilderNode]): Seq[Tree] = {
      var next       = false
      var fieldNames = List.empty[String]
      nodes.flatMap { node =>
        val newLine = if (next) newLineCode else {
          next = true
          Nil
        }
        node match {
          case BuilderProp(_, fieldName, tpe, _, json, optAggr) =>
            // Only generate 1 property, even if attribute is repeated in molecule
            if (fieldNames.contains(fieldName)) Nil else {
              fieldNames = fieldNames :+ fieldName
              newLine ++ (optAggr match {
                case None                                       => Seq(json(42, tabs))
                case Some(aggrTpe) if aggrTpe == tpe.toString() => Seq(json(42, tabs))
                case Some(aggrTpe)                              => abort(
                  s"Field `$fieldName` not available since the aggregate changes its type to `$aggrTpe`. " +
                    s"Please use tuple output instead to access aggregate value."
                )
              })
            }

          case nested@BuilderObj(_, ref, 2, nestedProps) =>
            val propCount = getPropCount(nestedProps)
            val deeper = isDeeper(nested)
            newLine ++ Seq(
              q"""quote(sb, $ref)""",
              q"""sb.append(": [")""",
              q"""
                it.next match {
                  case null => sb.append("]")
                  case last =>
                    val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]]
                    val it = extractFlatValues(list, $propCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper)
                    ..${jsonOptNested(nested, refIndexes, tacitIndexes, level + 1, tabs + 2)}
                    sb.append(${indent(tabs + 1) + "]"})
                }
              """
            )

          case ref: BuilderObj => properties(ref.props)
        }
      }
    }

    val tree = if (hasSameNss(current)) {
      q"""
        throw MoleculeException(
          "Please compose multiple same-name namespaces with `++` instead of `+` to access field values."
        )
      """

    } else if (level == 0) {
      q"""
        val it = row.iterator()
        sb.append(${indent(tabs) + "{" + indent(tabs + 1)})
        ..${properties(current.props)}
        sb.append(${indent(tabs) + "}"})
      """

    } else {
      current.props.last match {
        case last@BuilderObj(_, ref, 2, nestedProps) =>
          val propCount = getPropCount(nestedProps)
          val deeper = isDeeper(last)
          q"""
            var next = false
            while (it.hasNext) {
              if (next) sb.append(",") else next = true
              sb.append(${indent(tabs) + "{" + indent(tabs + 1)})
              ..${properties(current.props.init)}
              sb.append(${"," + indent(tabs + 1)})
              quote(sb, $ref)
              sb.append(": [")
              it.next match {
                case "__none__" => sb.append("]")
                case last       =>
                  val list = last.asInstanceOf[jList[Any]]
                  val it = extractFlatValues(list, $propCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper)
                  ..${jsonOptNested(last, refIndexes, tacitIndexes, level + 1, tabs + 2)}
                  sb.append(${indent(tabs + 1) + "]"})
              }
              sb.append(${indent(tabs) + "}"})
            }
          """
        case _                      =>
          q"""
            var next = false
            while (it.hasNext) {
              if (next) sb.append(",") else next = true
              sb.append(${indent(tabs) + "{" + indent(tabs + 1)})
              ..${properties(current.props)}
              sb.append(${indent(tabs) + "}"})
            }
          """
      }
    }

    xx(1, current, tree)
    tree
  }
}