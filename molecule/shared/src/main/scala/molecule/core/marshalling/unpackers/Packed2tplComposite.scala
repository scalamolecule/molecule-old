package molecule.core.marshalling.unpackers

import molecule.core.exceptions.MoleculeException
import molecule.core.macros.rowExtractors.Row2tplComposite
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.PackedValue2cast
import scala.collection.mutable
import scala.reflect.macros.blackbox

trait Packed2tplComposite extends PackedValue2cast { self: Packed2tplFlat =>
  val c: blackbox.Context

  import c.universe._

//    private lazy val xx = InspectMacro("Packed2tplComposite", 1, 2, mkError = true)
  private lazy val xx = InspectMacro("Packed2tplComposite", 10)

  def packed2tplComposite(obj: Obj, txMetas: Int): Tree = {
    val groups = obj.props.collect {
      case Obj(_, _, _, props) => resolveGroups(props, List(List.empty[Tree]))
    }

    val composites = groups.init.flatten.filter(_.nonEmpty)
    val txGroup    = groups.last

    if (txMetas == 0) {
      val tree = q"(..${tuples(groups.flatten)})"
      xx(1, txMetas, obj, groups, composites, txGroup, tree)
      tree

    } else {
      val init               = tuples(composites)
      val lastCompositeAttrs = txGroup.head
      val txUnpackers        = if (txMetas == 1) txGroup.last else tuples(txGroup.tail)
      val lastWithTx         = lastCompositeAttrs ++ txUnpackers
      val tree               = (init, lastWithTx) match {
        case (Nil, lastWithTx)  => q"(..$lastWithTx)"
        case (init, Nil)        => q"(..$init)"
        case (init, lastWithTx) => q"(..$init, (..$lastWithTx))"
      }

      xx(2, txMetas, obj, groups, init, lastCompositeAttrs, txUnpackers, lastWithTx, tree)
      tree
    }
  }
}