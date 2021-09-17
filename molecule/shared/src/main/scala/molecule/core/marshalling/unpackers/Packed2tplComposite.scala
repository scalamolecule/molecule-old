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
//    val obj = if (hasSameNss(obj0)) {
//      val mergedObjs = obj0.props.foldLeft(Seq.empty[String], mutable.Map.empty[String, Obj]) {
//        case ((Nil, _), o@Obj(cls, _, _, _)) =>
//          (Seq(cls), mutable.Map(cls -> o))
//
//        case ((clss, objs), o@Obj(cls, _, _, props)) if clss.contains(cls) =>
//          val mergedProps = objs(cls).props ++ props
//          val newObj      = o.copy(props = mergedProps)
//          objs.update(cls, newObj)
//          (clss, objs)
//
//        case ((clss, objs), o@Obj(cls, _, _, _)) =>
//          (clss :+ cls, objs += cls -> o)
//
//        case ((_, _), node) => throw MoleculeException("Unexpected property in composite object: " + node)
//      }._2.values.toList
//      obj0.copy(props = mergedObjs)
//    } else obj0


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