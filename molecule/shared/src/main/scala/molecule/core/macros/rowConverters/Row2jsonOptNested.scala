package molecule.core.macros.rowConverters

import molecule.core.macros.rowAttr.{JsonBase, RowValue2jsonOptNested}
import molecule.core.marshalling.ast.nodes._
import scala.reflect.macros.blackbox

private[molecule] trait Row2jsonOptNested extends RowValue2jsonOptNested with JsonBase {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("Row2jsonOptNested", 2, 1)

  def jsonOptNested(
    current: Obj,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    level: Int = 0,
    tabs: Int = 0
  ): Tree = {
    val newLineCode = Seq(
      q"""sb.append(${"," + indent(tabs + 1)})"""
    )

    def properties(nodes: List[Node]): Seq[Tree] = {
      var next  = false
      var props = List.empty[String]
      nodes.flatMap { node =>
        val newLine = if (next) newLineCode else {
          next = true
          Nil
        }
        node match {
          case Prop(_, prop, baseTpe, _, group, _) =>
            // Only generate 1 property, even if attribute is repeated in molecule
            if (props.contains(prop)) Nil else {
              props = props :+ prop
              newLine :+ getRowValue2jsonOptNestedLambda(group, baseTpe, prop)(-10, tabs) // colIndex not used
            }

          case nested@Obj(_, ref, true, nestedProps) =>
            val propCount = getPropCount(nestedProps)
            val deeper    = isDeeper(nested)
            newLine ++ Seq(
              q"""quote(sb, $ref)""",
              q"""sb.append(": [")""",
              q"""
                it.next match {
                  case null => sb.append("]")
                  case last =>
                    val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]]
                    val it = ExtractFlatValues(
                      $propCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper, sortCoordinates
                    )(list)
                    ..${jsonOptNested(nested, refIndexes, tacitIndexes, level + 1, tabs + 2)}
                    sb.append(${indent(tabs + 1) + "]"})
                }
              """
            )

          case ref: Obj => properties(ref.props)
        }
      }
    }

    val tree = if (hasSameNss(current)) {
      q"""
        throw MoleculeException(
          "Please compose multiple same-name namespaces with `++` instead of `+` to access property values."
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
        case last@Obj(_, ref, true, nestedProps) =>
          val propCount  = nestedProps.length
          val deeper     = isDeeper(last)
          val flatValues = TermName("flatValues" + level)
          q"""
            var next = false
            val $flatValues = ExtractFlatValues(
              $propCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper, sortCoordinates
            )
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
                  val it = $flatValues(last.asInstanceOf[jList[Any]])
                  ..${jsonOptNested(last, refIndexes, tacitIndexes, level + 1, tabs + 2)}
                  sb.append(${indent(tabs + 1) + "]"})
              }
              sb.append(${indent(tabs) + "}"})
            }
          """
        case _                                   =>
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
