package molecule.core.macros.build.tpl

import molecule.core.macros.attrResolverTrees.LambdaCastNestedOpt
import molecule.core.macros.build.BuildBase
import scala.reflect.macros.blackbox

private[molecule] trait BuildTplNestedOpt extends LambdaCastNestedOpt with BuildBase {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildTplNestedOpt", 2, 2)

  def tplNestedOpt(
    current: Obj,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    level: Int = 0
  ): Tree = {

    def properties(nodes: List[Node]): Seq[Tree] = {
      nodes.flatMap {
        case attr: Prop                       => Seq(attr.cast(42)) // colIndex not used here
        case nested@Obj(_, _, 2, nestedProps) =>
          val propCount = getPropCount(nestedProps)
          val deeper = isDeeper(nested)
          Seq(
            q"""
              it.next match {
                case null => Nil
                case last =>
                  val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]]
                  val it = extractFlatValues(list, $propCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper)
                  ..${tplNestedOpt(nested, refIndexes, tacitIndexes, level + 1)}
              }
            """
          )

        case ref: Obj => properties(ref.props)
      }
    }

    val tree = if (level == 0) {
      q"""
         val it = row.iterator()
         (..${properties(current.props)})
       """

    } else {
      current.props.last match {
        case last@Obj(_, _, 2, nestedProps) =>
          val propCount = getPropCount(nestedProps)
          val deeper = isDeeper(last)
          q"""
            val buf = new scala.collection.mutable.ListBuffer[Any]()
            while (it.hasNext) {
              buf.addOne(
                (
                  ..${properties(current.props.init)},
                  it.next match {
                    case "__none__" => Nil
                    case last       =>
                      val list = last.asInstanceOf[jList[Any]]
                      val it = extractFlatValues(list, $propCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper)
                      ..${tplNestedOpt(last, refIndexes, tacitIndexes, level + 1)}
                  }
                )
              )
            }
            buf.toList
           """
        case _                      =>
          q"""
            val buf = new scala.collection.mutable.ListBuffer[Any]()
            while (it.hasNext) {
              buf.addOne((..${properties(current.props)}))
            }
            buf.toList
          """
      }
    }

    xx(1, current, refIndexes, tacitIndexes, level, tree)
    tree
  }
}
