package molecule.core.marshalling.unpack

import molecule.core.macros.build.tpl.BuildTplComposite
import molecule.core.marshalling.nodes._
import scala.collection.mutable
import scala.reflect.macros.blackbox

trait Packed2tplComposite extends Unpackers { self: BuildTplComposite =>
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Packed2tplComposite", 1, mkError = true)
  private lazy val xx = InspectMacro("Packed2tplComposite", 10)

  def packed2tplComposite(obj: Obj, txMetas: Int): Tree = {
    val next       = q"vs.next()"

    def composite: Tree = {
      val compositeTuples = {
        def resolve(node: Node, acc: Seq[Tree]): Seq[Tree] = node match {
          case Prop(_, _, baseTpe, _, group, _) => acc :+ unpackLambdas(group, baseTpe, next)
          case Obj(_, _, _, props)              => props.flatMap(prop => resolve(prop, acc))
        }
        obj.props.map(compositeGroup => q"(..${resolve(compositeGroup, Nil)})")
      }
      xx(1, obj, txMetas, compositeTuples)
      q"(..$compositeTuples)"
    }


    def compositeTxComposite: Tree = {
      def resolveTxComposites(txCompositeGroups: Seq[Node]): Seq[Tree] = {
        def resolve(nodes: Seq[Node], acc: Seq[Tree]): Seq[Tree] = nodes.flatMap {
          case Prop(_, _, baseTpe, _, group, _) => acc :+ unpackLambdas(group, baseTpe, next)
          case Obj(_, _, _, nodes)              => resolve(nodes, acc)
        }
        txCompositeGroups.collect {
          case Obj(_, _, _, nodes) => resolve(nodes, Nil)
        }.flatMap {
          case Nil     => None
          case txGroup => Some(q"(..$txGroup)")
        }
      }

      val compositeTuples = {
        def resolve(node: Node, acc: Seq[Tree]): Seq[Tree] = node match {
          case Prop(_, _, baseTpe, _, group, _) => acc :+ unpackLambdas(group, baseTpe, next)
          case Obj("Tx_", _, _, props)          => acc ++ resolveTxComposites(props)
          case Obj(_, _, _, props)              => props.flatMap(prop => resolve(prop, acc))
        }
        obj.props.map(compositeGroup => q"(..${resolve(compositeGroup, Nil)})")
      }

      xx(2, obj, txMetas, compositeTuples)
      q"(..$compositeTuples)"
    }

    if (txMetas <= 1) composite else compositeTxComposite
  }
}
