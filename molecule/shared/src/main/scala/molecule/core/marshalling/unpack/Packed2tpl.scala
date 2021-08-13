package molecule.core.marshalling.unpack

import molecule.core.marshalling.attrIndexes._
import molecule.core.ops.TreeOps
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.macros.blackbox

trait Packed2tpl extends Unpackers {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("Packed2tpl", 1)


  def packed2tpl(
    typess: List[List[Tree]],
    postTypes: List[Tree],
    indexes: Indexes
  ): Tree = {
    val v           = q"v"
    val next        = q"vs.next()"
    val levels      = typess.size
    val unpackerss  = mutable.Seq.fill(levels)(Seq.empty[Tree])
    val nestedTypes = typess.init.foldRight(List(tq"(..${typess.last})"), levels - 2) {
      case (types, (acc, 0)) if postTypes.nonEmpty => (tq"(..$types, List[${acc.head}], ..$postTypes)" +: acc, -1)
      case (types, (acc, level))                   => (tq"(..$types, List[${acc.head}])" +: acc, level - 1)
    }._1

    def setUnpacker(node: IndexNode, level: Int, i: Int): Unit = {
      node match {
        case AttrIndex(_, castIndex, _, _, _) if level > 0 && i == 0 =>
          unpackerss(level) = unpackerss(level) :+ unpackLambdas(v)(castIndex)

        case AttrIndex(_, castIndex, _, _, _) =>
          unpackerss(level) = unpackerss(level) :+ unpackLambdas(next)(castIndex)

        case Indexes(_, 2, attrs) =>
          unpackerss(level) = unpackerss(level) :+ q"${TermName("nested" + (level + 1))}"
          setUnpackers(attrs, level + 1, 0)

        case Indexes(_, _, attrs) =>
          setUnpackers(attrs, level, i + 1)
      }
    }

    def setUnpackers(nodes: Seq[IndexNode], level: Int, i: Int): Unit = {
      nodes.zipWithIndex.foreach { case (node, j) => setUnpacker(node, level, i + j) }
    }

    def mkNested(level: Int, unpackers: Seq[Tree]): c.universe.Tree = {
      q"""
          def ${TermName("nested" + level)} = {
            v = vs.next()
            if (v == "◄◄") {
              Nil
            } else {
              val buf = new ListBuffer[${nestedTypes(level)}]
              first = true
              do {
                buf.append((..$unpackers))
                v = vs.next()
              } while (v != "◄")
              buf.toList
            }
          }
       """
    }

    setUnpackers(indexes.attrs, 0, 0)

    val nested = unpackerss.zipWithIndex.tail.map {
      case (unpackers, level) => mkNested(level, unpackers)
    }

    val tree =
      q"""{
          ..$nested
          (..${unpackerss.head})
         }
       """

    xx(1, indexes, tree)
    tree
  }
}
