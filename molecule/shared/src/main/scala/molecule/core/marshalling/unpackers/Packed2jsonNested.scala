package molecule.core.marshalling.unpackers

import molecule.core.macros.rowAttr.JsonBase
import molecule.core.marshalling.ast.nodes._
import molecule.core.marshalling.unpackAttr.PackedValue2json
import scala.collection.mutable
import scala.reflect.macros.blackbox

trait Packed2jsonNested extends PackedValue2json with JsonBase {
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Packed2jsonNested", 1, mkError = true)
  private lazy val xx = InspectMacro("Packed2jsonNested", 10)

  private lazy val v    = q"v"
  private lazy val next = q"vs.next()"

  def packed2jsonNested(levels: Int, obj: Obj, txMetas: Int): Tree = {
    // (unpackers, ref, tabs)
    val unpackerss = mutable.Seq.fill(levels + txMetas)((List.empty[Tree], "", 0))

    def resolveTxGroups(txCompositeGroups: Seq[Node], tabs: Int): Seq[Tree] = {
      def resolve(nodes: Seq[Node], acc: Seq[Tree]): Seq[Tree] = nodes.flatMap {
        case Prop(prop, _, baseTpe, _, group, optAggrTpe) =>
          acc :+ getPackedValue2json(group, baseTpe, prop, next, tabs + 1, optAggrTpe)

        case Obj(_, _, _, nodes)                 => resolve(nodes, acc)
      }
      txCompositeGroups.collect {
        case Obj(_, _, _, nodes) => resolve(nodes, Nil)
      }.flatMap {
        case Nil     => None
        case txGroup => Some(q"(..$txGroup)")
      }
    }

    def setUnpacker(node: Node, level: Int, levelIndex: Int, refIndex: Int, ref: String, tabs: Int): Unit = {
      node match {
        case Prop(_, prop, baseTpe, _, group, optAggrTpe) if level > 0 && levelIndex == 0 =>
          // First prop on each sub level takes the `v` from before the loop
          unpackerss(level) = (
            unpackerss(level)._1 :+ getPackedValue2json(group, baseTpe, prop, v, tabs + 1, optAggrTpe),
            ref, tabs
          )

        case Prop(_, prop, baseTpe, _, group, optAggrTpe) if refIndex == 0 =>
          // First prop after each new flat ref has no comma before
          unpackerss(level) = (
            unpackerss(level)._1 :+ getPackedValue2json(group, baseTpe, prop, next, tabs + 1, optAggrTpe),
            ref, tabs
          )

        case Prop(_, prop, baseTpe, _, group, optAggrTpe) =>
          unpackerss(level) = (unpackerss(level)._1 ++ Seq(
            q"""sb.append(${"," + indent(tabs + 1)})""",
            getPackedValue2json(group, baseTpe, prop, next, tabs + 1, optAggrTpe)
          ), ref, tabs)

        case Obj(_, nestedRef, true, props) =>
          val comma = if (refIndex == 0) Nil else Seq(q"""sb.append(${"," + indent(tabs + 1)})""")
          unpackerss(level) = (unpackerss(level)._1 ++ comma ++ Seq(
            q"${TermName("nested" + (level + 1))}"
          ), ref, tabs)
          setUnpackers(props, level + 1, 0, 0, nestedRef, tabs + 2)

        case Obj("Tx_", _, _, txGroups) if txMetas != 0 =>
          unpackerss(level) = (unpackerss(level)._1 ++ resolveTxGroups(txGroups, tabs), ref, tabs)

        case Obj(_, flatRef, _, props) =>
          val comma = if (refIndex == 0) Nil else Seq(q"""sb.append(${"," + indent(tabs + 1)})""")
          unpackerss(level) = (unpackerss(level)._1 ++ comma ++ Seq(
            q"""quote(sb, $flatRef)""",
            q"""sb.append(${": {" + indent(tabs + 2)})""",
          ), ref, tabs)
          setUnpackers(props, level, levelIndex, 0, ref, tabs + 1)
          unpackerss(level) = (unpackerss(level)._1 ++ Seq(
            q"""sb.append(${indent(tabs + 1)})""",
            q"""sb.append("}")""",
          ), ref, tabs)
      }
    }

    def setUnpackers(nodes: Seq[Node], level: Int, levelIndex: Int, refIndex: Int, ref: String, tabs: Int): Unit = {
      nodes.zipWithIndex.foreach { case (node, i) => setUnpacker(node, level, levelIndex + i, refIndex + i, ref, tabs) }
    }

    // Recursively set unpackers
    setUnpackers(obj.props, 0, 0, 0, "", 0)


    val tree = {
      def mkNested(level: Int, ref: String, tabs: Int): Tree = {
        q"""
          def ${TermName("nested" + level)} = {
            quote(sb, $ref)
            sb.append(": [")
            var next = false
            var v = vs.next()
            if (v != "◄◄") {
              do {
                if (next) sb.append(",") else next = true
                sb.append(${indent(tabs) + "{" + indent(tabs + 1)})
                ..${unpackerss(level)._1}
                sb.append(${indent(tabs) + "}"})
                v = vs.next()
              } while (v != "►")
            }
            if (next) sb.append(${indent(tabs - 1)})
            sb.append("]")
          }
        """
      }
      val nestedResolvers = unpackerss.take(levels).zipWithIndex.tail.map {
        case (unpackers, level) => mkNested(level, unpackers._2, unpackers._3)
      }

      val tree = if (hasSameNss(obj)) {
        q"""throw MoleculeException(
          "Please compose multiple same-name namespaces with `++` instead of `+` to access property values."
        )"""
      } else {
        q"""{
          ..$nestedResolvers
          sb.append("\n      {\n        ")
          ..${unpackerss.head._1}
          sb.append("\n      }")
        }"""
      }
      tree
    }

    xx(1, levels, obj
      , unpackerss.toList.head._1
      , unpackerss.toList(1)._1
      , if (unpackerss.length >= 3) unpackerss.toList(2)._1 else "(no unpackers on level 2)"
      , if (unpackerss.length >= 4) unpackerss.toList(3)._1 else "(no unpackers on level 3)"
      , tree
    )
    tree
  }
}
