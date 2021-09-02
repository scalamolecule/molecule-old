package molecule.core.macros.build.tpl

import molecule.core.macros.attrResolverTrees.ResolverCastNestedOpt
import molecule.core.marshalling.nodes._
import scala.reflect.macros.blackbox

private[molecule] trait BuildTplNestedOpt extends ResolverCastNestedOpt {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildTplNestedOpt", 10)

  def tplNestedOpt(
    current: Obj,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    level: Int = 0
  ): Tree = {

    def properties(nodes: List[Node]): Seq[Tree] = {
      nodes.flatMap {
        case Prop(_, _, baseTpe, _, group, _) => Seq(getResolverCastNestedOpt(group, baseTpe)(-10)) // colIndex not used with iterator
        case nested@Obj(_, _, true, nestedProps)       =>
          val propCount = getPropCount(nestedProps)
          val deeper    = isDeeper(nested)
          xx(2, level, current, nested, deeper)
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
      val flatProps      = properties(current.props)
      val metaPropCounts = current.props.last match {
        case Obj("Tx_", _, _, metaProps) =>
          metaProps.collect {
            case Obj(_, _, _, metaProps) => getPropCount(metaProps)
          }
        case _                           => Nil
      }
      val metaPropCount  = metaPropCounts.sum
      val isComposite    = metaPropCounts.size > 1
      val topLevelTuple  = if (metaPropCount == 0) {
        q"(..$flatProps)"
      } else {
        val dataPropsCount = flatProps.size - metaPropCount
        val dataProps      = flatProps.take(dataPropsCount)
        val metaProps      = metaPropCounts.foldLeft(Seq.empty[Tree], dataPropsCount) {
          case ((acc, start), 0)                => (acc, start)
          case ((acc, start), n) if isComposite => (acc :+ q"(..${flatProps.slice(start, start + n)})", start + n)
          case ((acc, start), n) /* flat */     => (acc ++ flatProps.slice(start, start + n), start + n)
        }._1
        q"(..$dataProps, ..$metaProps)"
      }
      xx(1, level, current, flatProps, topLevelTuple, metaPropCounts)
      q"""
         val it = row.iterator()
         $topLevelTuple
       """

    } else {
      current.props.last match {
        case last@Obj(_, _, nested, _) if nested || isDeeper(last) =>
          val (props, nestedObj) = if (nested) {
            (current.props.init, last)
          } else {
            def flatten(props: List[Node], acc: List[Node] = Nil): List[Node] = {
              props.flatMap {
                case nested@Obj(_, _, true, _) => acc :+ nested
                case Obj(_, _, _, refs)        => flatten(refs, acc)
                case prop                      => acc :+ prop
              }
            }
            val flatProps = flatten(current.props)
            (flatProps.init, flatProps.last.asInstanceOf[Obj])
          }
          val nestedPropCount    = getPropCount(nestedObj.props)
          val deeper             = isDeeper(nestedObj)
          xx(3, level, current, props, nestedObj, nestedPropCount, deeper)
          q"""
            val buf = new scala.collection.mutable.ListBuffer[Any]()
            while (it.hasNext) {
              buf.addOne(
                (
                  ..${properties(props)},
                  it.next match {
                    case "__none__" => Nil
                    case last       =>
                      val list = last.asInstanceOf[jList[Any]]
                      val it = extractFlatValues(list, $nestedPropCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper)
                      ..${tplNestedOpt(nestedObj, refIndexes, tacitIndexes, level + 1)}
                  }
                )
              )
            }
            buf.toList
           """

        case last =>
          xx(5, level, current, last, properties(current.props))
          q"""
            val buf = new scala.collection.mutable.ListBuffer[Any]()
            while (it.hasNext) {
              buf.addOne((..${properties(current.props)}))
            }
            buf.toList
          """
      }
    }

    xx(6, level, current, refIndexes, tacitIndexes, tree)
    tree
  }
}
