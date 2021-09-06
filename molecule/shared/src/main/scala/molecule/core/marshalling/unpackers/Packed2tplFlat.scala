package molecule.core.marshalling.unpackers

import molecule.core.macros.rowExtractors.Row2tplComposite
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.PackedValue2cast
import scala.collection.mutable
import scala.reflect.macros.blackbox

trait Packed2tplFlat extends PackedValue2cast { self: Row2tplComposite =>
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Packed2tplFlat", 1, mkError = true)
  private lazy val xx = InspectMacro("Packed2tplFlat", 10)

  private val nextValue = q"vs.next()"

  def packed2tplFlat(obj: Obj, txMetas: Int): Tree = {
    def resolveTxComposites(txCompositeGroups: Seq[Node]): Seq[Tree] = {
      def resolve(nodes: Seq[Node], acc: Seq[Tree]): Seq[Tree] = nodes.flatMap {
        case Prop(_, _, baseTpe, _, group, _) => acc :+ getPackedValue2cast(group, baseTpe, nextValue)
        case Obj(_, _, _, nodes)              => resolve(nodes, acc)
      }
      txCompositeGroups.collect {
        case Obj(_, _, _, nodes) => resolve(nodes, Nil)
      }.flatMap {
        case Nil     => None
        case txGroup => Some(q"(..$txGroup)")
      }
    }

    def resolve(node: Node, acc: Seq[Tree]): Seq[Tree] = node match {
      case Prop(_, _, baseTpe, _, group, _) => acc :+ getPackedValue2cast(group, baseTpe, nextValue)
      case Obj("Tx_", _, _, props)          => acc ++ resolveTxComposites(props)
      case Obj(_, _, _, props)              => props.flatMap(prop => resolve(prop, acc))
    }

    val unpackers = resolve(obj, Nil)
    xx(1, obj, txMetas, unpackers)

    q"(..$unpackers)"
  }

  //  def packed2tplFlatOLD(obj: Obj, txMetas: Int): Tree = {
  //    val next = q"vs.next()"
  //
  //    def flat: Seq[Tree] = {
  //      def resolve(node: Node, acc: Seq[Tree]): Seq[Tree] = node match {
  //        case Prop(_, _, baseTpe, _, group, _) => acc :+ unpackLambdas(group, baseTpe, next)
  //        case Obj(_, _, _, props)              => props.flatMap(prop => resolve(prop, acc))
  //      }
  //      resolve(obj, Nil)
  //    }
  //
  //    def flatTxComposite: Seq[Tree] = {
  //      def resolveTxComposites(txCompositeGroups: Seq[Node]): Seq[Tree] = {
  //        def resolve(nodes: Seq[Node], acc: Seq[Tree]): Seq[Tree] = nodes.flatMap {
  //          case Prop(_, _, baseTpe, _, group, _) => acc :+ unpackLambdas(group, baseTpe, next)
  //          case Obj(_, _, _, nodes)              => resolve(nodes, acc)
  //        }
  //        txCompositeGroups.collect {
  //          case Obj(_, _, _, nodes) => resolve(nodes, Nil)
  //        }.flatMap {
  //          case Nil     => None
  //          case txGroup => Some(q"(..$txGroup)")
  //        }
  //      }
  //
  //      def resolve(node: Node, acc: Seq[Tree]): Seq[Tree] = node match {
  //        case Prop(_, _, baseTpe, _, group, _) => acc :+ unpackLambdas(group, baseTpe, next)
  //        case Obj("Tx_", _, _, props)          => acc ++ resolveTxComposites(props)
  //        case Obj(_, _, _, props)              => props.flatMap(prop => resolve(prop, acc))
  //      }
  //      resolve(obj, Nil)
  //    }
  //
  //    val unpackers = if (txMetas <= 1) flat else flatTxComposite
  //    xx(1, obj, txMetas, unpackers)
  //
  //    q"(..$unpackers)"
  //  }
}
