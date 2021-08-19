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
    indexes: Indexes,
    composite: Boolean = false,
    txMetaCompositesCount: Int = 0
  ): Tree = {
    val v           = q"v"
    val next        = q"vs.next()"
    val levels      = typess.size
    val unpackerss  = mutable.Seq.fill(levels)(List.empty[Tree])
    val nestedTypes = typess.init.foldRight(List(tq"(..${typess.last})"), levels - 2) {
      case (types, (acc, 0)) if postTypes.nonEmpty => (tq"(..$types, List[${acc.head}], ..$postTypes)" +: acc, -1)
      case (types, (acc, level))                   => (tq"(..$types, List[${acc.head}])" +: acc, level - 1)
    }._1

    def setUnpacker(node: IndexNode, level: Int, i: Int): Unit = {
      node match {
        case AttrIndex(_, _, castIndex, _, _, _) if level > 0 && i == 0 =>
          unpackerss(level) = unpackerss(level) :+ unpackLambdas(v)(castIndex)

        case AttrIndex(_, _, castIndex, _, _, _) =>
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

    // Recursively set unpackers
    setUnpackers(indexes.attrs, 0, 0)


    def unpackNested = {
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
      val nested = unpackerss.zipWithIndex.tail.map {
        case (unpackers, level) => mkNested(level, unpackers)
      }
      q"""{
          ..$nested
          (..${unpackerss.head})
         }
       """
    }

    def unpackComposite = {
      var i                                     = -1
      val unpackers          : List[Tree]       = unpackerss.head
      val compositeUnpackerss: List[List[Tree]] = typess.map { types =>
        types.map { _ =>
          i += 1
          unpackers(i)
        }
      }

      val ordinaryComposites = compositeUnpackerss.take(compositeUnpackerss.length - txMetaCompositesCount)
      val txMetaComposites   = compositeUnpackerss.takeRight(txMetaCompositesCount)
      val firstComposites    = ordinaryComposites.init
      val lastComposite      = ordinaryComposites.last

      def compositeTuples(unpackerss: List[List[Tree]]): List[Tree] = unpackerss.flatMap {
        case Nil       => None
        case unpackers => Some(q"(..$unpackers)")
      }

      if (txMetaCompositesCount > 0) {
        val first = compositeTuples(firstComposites)
        val last  = lastComposite ++ compositeTuples(txMetaComposites)

        //      z(1, model0, types, castss, first, last)
        (first, last) match {
          case (Nil, last)   => q"(..$last)"
          case (first, Nil)  => q"(..$first)"
          case (first, last) => q"(..$first, (..$last))"
        }
      } else {
        q"(..${compositeTuples(compositeUnpackerss)})"
      }
    }

    val tree = if (composite) unpackComposite else unpackNested

    xx(1
      , indexes
      , tree
    )
    tree
  }
}
