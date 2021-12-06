package molecule.core.marshalling.unpackers

import molecule.core.macros.rowExtractors.Row2tplComposite
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.PackedValue2cast
import scala.collection.mutable
import scala.reflect.macros.blackbox

trait Packed2tplNested extends PackedValue2cast {
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Packed2tplNested", 1, mkError = true)
  private lazy val xx = InspectMacro("Packed2tplNested", 7, 6)


  def packed2tplNested(typess: List[List[Tree]], obj: Obj, txMetas: Int): Tree = {
    val v                    = q"v"
    val next                 = q"vs.next()"
    val levels               = typess.size - txMetas
    val unpackerss           = mutable.Seq.fill(typess.size)(List.empty[Tree])
    val typess1              = typess.take(levels)
    val txMetaTypes          = typess.takeRight(txMetas)
    val txMetaCompositeTypes = if (txMetaTypes.size == 1) {
      txMetaTypes.head
    } else {
      txMetaTypes.filter(_.nonEmpty).map(compositeTypes => q"(..$compositeTypes)")
    }
    val nestedTypes          = typess1.init.foldRight(Seq(tq"(..${typess1.last})"), levels - 2) {
      case (types, (acc, 0)) if txMetas != 0 => (tq"(..$types, Seq[${acc.head}], ..$txMetaCompositeTypes)" +: acc, -1)
      case (types, (acc, level))             => (tq"(..$types, Seq[${acc.head}])" +: acc, level - 1)
    }._1

    def resolveTxGroups(txCompositeGroups: Seq[Node]): Seq[Tree] = {
      def resolve(nodes: Seq[Node], acc: Seq[Tree]): Seq[Tree] = nodes.flatMap {
        case Prop(_, _, baseTpe, _, group, optAggrTpe) =>
          acc :+ getPackedValue2cast(group, baseTpe, next, optAggrTpe)
        case Obj(_, _, _, nodes)              => resolve(nodes, acc)
      }
      val txGroups = txCompositeGroups.collect {
        case Obj(_, _, _, nodes) => resolve(nodes, Nil)
      }
      if (txMetas == 1) {
        // attributes (no tuple)
        txGroups.flatten
      } else {
        // tuples
        txGroups.filter(_.nonEmpty).map(attrs => q"(..$attrs)")
      }
    }

    def setUnpacker(node: Node, level: Int, i: Int): Unit = {
      node match {
        case Prop(_, _, baseTpe, _, group, optAggrTpe) if level > 0 && i == 0 =>
          // First prop on each sub level takes the `v` from before the loop
          unpackerss(level) = unpackerss(level) :+ getPackedValue2cast(group, baseTpe, v, optAggrTpe)

        case Prop(_, _, baseTpe, _, group, optAggrTpe) =>
          unpackerss(level) = unpackerss(level) :+ getPackedValue2cast(group, baseTpe, next, optAggrTpe)

        case Obj(_, _, true, props) =>
          unpackerss(level) = unpackerss(level) :+ q"${TermName("nested" + (level + 1))}"
          setUnpackers(props, level + 1, 0)

        case Obj("Tx_", _, _, txGroups) if txMetas != 0 =>
          unpackerss(level) = unpackerss(level) ++ resolveTxGroups(txGroups)

        case Obj(_, _, _, props) =>
          setUnpackers(props, level, i)
      }
    }

    def setUnpackers(nodes: Seq[Node], level: Int, i: Int): Unit = {
      nodes.zipWithIndex.foreach { case (node, j) => setUnpacker(node, level, i + j) }
    }

    // Recursively set unpackers
    setUnpackers(obj.props, 0, 0)

    val tree = {
      def mkNested(level: Int, unpackers: Seq[Tree]): Tree = {
        q"""
          def ${TermName("nested" + level)} = {
            var v = vs.next()
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
      val nestedResolvers = unpackerss.take(levels).zipWithIndex.tail.map {
        case (unpackers, level) => mkNested(level, unpackers)
      }

      val tree =
        q"""{
            ..$nestedResolvers
            (..${unpackerss.head})
          }
        """
      //      xx(6, typess, typess1, obj, unpackerss, tree)
      tree
    }

    xx(7
      , levels
      , txMetas
      , obj
      , "----------"
      , typess
      , typess1
      , txMetaTypes
      , txMetaCompositeTypes
      , nestedTypes
      , "----------"
      , unpackerss.toList
      , tree
    )
    tree
  }
}
