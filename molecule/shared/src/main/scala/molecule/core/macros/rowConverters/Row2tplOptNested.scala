package molecule.core.macros.rowConverters

import molecule.core.macros.rowAttr.RowValue2castOptNested
import molecule.core.marshalling.nodes._
import scala.reflect.macros.blackbox

private[molecule] trait Row2tplOptNested extends RowValue2castOptNested {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("Row2tplOptNested", 10)

  def tplOptNested(
    current: Obj,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]],
    level: Int = 0
  ): Tree = {

    def properties(nodes: List[Node]): Seq[Tree] = {
      nodes.flatMap {
        case Prop(_, _, baseTpe, _, group, _)    => Seq(getRowValue2castOptNestedLambda(group, baseTpe)(-10)) // colIndex not used with iterator
        case nested@Obj(_, _, true, nestedProps) =>
          val propCount = getPropCount(nestedProps)
          val deeper    = isDeeper(nested)
          xx(2, level, current, nested, deeper)
          Seq(
            q"""
              it.next match {
                case null => Nil
                case last =>
                  val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]]
                  val it = extractFlatValues($propCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper)(list)
                  ..${tplOptNested(nested, refIndexes, tacitIndexes, level + 1)}
              }
            """
          )

        case ref: Obj => properties(ref.props)
      }
    }

    val tree = if (level == 0) {
      val relatedProps      = properties(current.props)
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
        q"(..$relatedProps)"
      } else {
        val dataPropsCount = relatedProps.size - metaPropCount
        val dataProps      = relatedProps.take(dataPropsCount)
        val metaProps      = metaPropCounts.foldLeft(Seq.empty[Tree], dataPropsCount) {
          case ((acc, start), 0)                => (acc, start)
          case ((acc, start), n) if isComposite => (acc :+ q"(..${relatedProps.slice(start, start + n)})", start + n)
          case ((acc, start), n) /* flat */     => (acc ++ relatedProps.slice(start, start + n), start + n)
        }._1
        q"(..$dataProps, ..$metaProps)"
      }
      xx(1, level, current, relatedProps, topLevelTuple, metaPropCounts)
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
            def getRelatedProps(props: List[Node], acc: List[Node] = Nil): List[Node] = {
              props.flatMap {
                case nested@Obj(_, _, true, _) => acc :+ nested
                case Obj(_, _, _, refs)        => getRelatedProps(refs, acc)
                case prop                      => acc :+ prop
              }
            }
            val relatedProps = getRelatedProps(current.props)
            (relatedProps.init, relatedProps.last.asInstanceOf[Obj])
          }
          val nestedPropCount    = getPropCount(nestedObj.props)
          val deeper             = isDeeper(nestedObj)
          val flatValues         = TermName("flatValues" + level)
          xx(3, level, current, props, nestedObj, nestedPropCount, deeper)
          q"""
            var buf = List.empty[Any]
            val $flatValues = extractFlatValues($nestedPropCount, ${refIndexes(level + 1)}, ${tacitIndexes(level + 1)}, $deeper)
            while (it.hasNext) {
              buf = buf :+ (
                ..${properties(props)},
                it.next match {
                  case "__none__" => Nil
                  case last       =>
                    val it = $flatValues(last.asInstanceOf[jList[Any]])
                    ..${tplOptNested(nestedObj, refIndexes, tacitIndexes, level + 1)}
                }
              )
            }
            buf
           """

        case last =>
          xx(5, level, current, last, properties(current.props))
          q"""
            var buf = List.empty[Any]
            while (it.hasNext) {
              buf = buf :+ (..${properties(current.props)})
            }
            buf
          """
      }
    }

    xx(6, level, current, refIndexes, tacitIndexes, tree)
    tree
  }
}
