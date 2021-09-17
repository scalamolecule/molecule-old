package molecule.core.marshalling.unpackers

import molecule.core.macros.rowExtractors.Row2tplComposite
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.PackedValue2cast
import scala.annotation.tailrec
import scala.reflect.macros.blackbox

trait Packed2tplFlat extends PackedValue2cast { //self: Row2tplComposite =>
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Packed2tplFlat", 1, mkError = true)
  private lazy val xx = InspectMacro("Packed2tplFlat", 10)

  private val nextValue = q"vs.next()"

  def resolveGroups(nodes: List[Node], acc: List[List[Tree]]): List[List[Tree]] = {
    nodes.foldLeft(acc: List[List[Tree]]) {
      case (List(Nil), Prop(_, _, baseTpe, _, group, optAggrTpe)) =>
        acc.init :+ List(getPackedValue2cast(group, baseTpe, nextValue, optAggrTpe))

      case (acc, Prop(_, _, baseTpe, _, group, optAggrTpe)) =>
        acc.init :+ (acc.last :+ getPackedValue2cast(group, baseTpe, nextValue, optAggrTpe))

      case (acc, Obj("Tx_", _, _, compositeObjects)) =>
        val txGroups = compositeObjects.collect {
          case Obj(_, _, _, props) => resolveGroups(props, List(List.empty[Tree]))
        }.flatten
        acc ++ txGroups

      case (acc, Obj(_, _, _, props)) => resolveGroups(props, acc)
    }
  }

  def tuples(unpackerss: List[List[Tree]]): List[Tree] =
    unpackerss.filter(_.nonEmpty).map(unpackers => q"(..$unpackers)")

  def packed2tplFlat(obj: Obj, txMetas: Int): Tree = {
    val groups = resolveGroups(obj.props, List(List.empty[Tree]))
    if (txMetas == 0) {
      q"(..${groups.head})"
    } else {
      val first = groups.head
      val last  = if (txMetas == 1) groups.last else tuples(groups.tail)
      val tree  = q"(..$first, ..$last)"
      xx(2, txMetas, obj, groups, first, last, tree)
      tree
    }
  }
}
