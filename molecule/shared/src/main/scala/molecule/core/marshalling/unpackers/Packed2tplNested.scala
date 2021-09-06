package molecule.core.marshalling.unpackers

import molecule.core.macros.rowExtractors.Row2tplComposite
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.PackedValue2cast
import scala.collection.mutable
import scala.reflect.macros.blackbox

trait Packed2tplNested extends PackedValue2cast { self: Row2tplComposite =>
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Packed2tplNested", 1, mkError = true)
  private lazy val xx = InspectMacro("Packed2tplNested", 7, 6)

  def packed2tplNested(
    typess: List[List[Tree]],
    obj: Obj,
    txMetas: Int
  ): Tree = {
    val v          = q"v"
    val next       = q"vs.next()"
    val levels     = typess.size - txMetas
    val unpackerss = mutable.Seq.fill(typess.size)(List.empty[Tree])

    def compositeTypes(typess: List[List[Tree]]): Seq[Tree] = typess.flatMap {
      case Nil   => None
      case types => Some(q"(..$types)")
    }

    val typess1              = typess.take(levels)
    val txMetaTypes          = typess.takeRight(txMetas)
    val txMetaCompositeTypes = compositeTypes(txMetaTypes)
    val nestedTypes          = typess1.init.foldRight(List(tq"(..${typess1.last})"), levels - 2) {
      case (types, (acc, 0)) if txMetas != 0 => (tq"(..$types, List[${acc.head}], ..$txMetaCompositeTypes)" +: acc, -1)
      case (types, (acc, level))             => (tq"(..$types, List[${acc.head}])" +: acc, level - 1)
    }._1

    def resolveTxGroups(txCompositeGroups: Seq[Node]): Seq[Tree] = {
      def resolve(nodes: Seq[Node], acc: Seq[Tree]): Seq[Tree] = nodes.flatMap {
        case Prop(_, _, baseTpe, _, group, _) => acc :+ getPackedValue2cast(group, baseTpe, next)
        case Obj(_, _, _, nodes)              => resolve(nodes, acc)
      }
      txCompositeGroups.collect {
        case Obj(_, _, _, nodes) => resolve(nodes, Nil)
      }.flatMap {
        case Nil     => None
        case txGroup => Some(q"(..$txGroup)")
      }
    }

    def setUnpacker(node: Node, level: Int, i: Int): Unit = {
      node match {
        case Prop(prop, _, baseTpe, _, group, _) if level > 0 && i == 0 =>
          unpackerss(level) = unpackerss(level) :+ getPackedValue2cast(group, baseTpe, v)

        case Prop(prop, _, baseTpe, _, group, _) =>
          unpackerss(level) = unpackerss(level) :+ getPackedValue2cast(group, baseTpe, next)

        case Obj(_, _, true, props) =>
          unpackerss(level) = unpackerss(level) :+ q"${TermName("nested" + (level + 1))}"
          setUnpackers(props, level + 1, 0)

        case Obj("Tx_", _, _, txGroups) if txMetas != 0 =>
          unpackerss(level) = unpackerss(level) ++ resolveTxGroups(txGroups)

        case Obj(_, _, _, props) =>
          //          setUnpackers(props, level, i + 1) // todo - revert to this?
          setUnpackers(props, level, i)
      }
    }

    def setUnpackers(nodes: Seq[Node], level: Int, i: Int): Unit = {
      nodes.zipWithIndex.foreach { case (node, j) => setUnpacker(node, level, i + j) }
    }

    // Recursively set unpackers
    setUnpackers(obj.props, 0, 0)


    def nested = {
      def mkNested(level: Int, unpackers: Seq[Tree]): c.universe.Tree = {
        q"""
          def ${TermName("nested" + level)} = {
            v = vs.next()
            if (v == "◄◄") {
              Nil
            } else {
              val buf = new ListBuffer[${nestedTypes(level)}]
              do {
                buf.append((..$unpackers))
                v = vs.next()
              } while (v != "►")
              buf.toList
            }
          }
        """
      }
      val nested = unpackerss.take(levels).zipWithIndex.tail.map {
        case (unpackers, level) => mkNested(level, unpackers)
      }
      val tree   =
        q"""{
            ..$nested
            (..${unpackerss.head})
          }
        """
      xx(6, typess, typess1, obj, unpackerss, tree)

      tree
    }


    def nestedTxComposite: Tree = {

      q""
    }


    //    val tree = if (txMetas <= 1) nested else nestedTxComposite
    val tree = if (txMetas <= 1) nested else nested

    xx(7
      , levels
      , txMetas
      , obj
      , typess
      , typess1
      , "----------"
      , nestedTypes
      , unpackerss.toList
      , txMetaCompositeTypes
      , tree
    )
    tree
  }
}
