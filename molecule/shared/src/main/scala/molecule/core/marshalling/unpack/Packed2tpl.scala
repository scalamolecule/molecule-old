package molecule.core.marshalling.unpack

import molecule.core.macros.build.tpl.BuildTplComposite
import molecule.core.marshalling.attrIndexes._
import molecule.core.ops.TreeOps
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.macros.blackbox

trait Packed2tpl extends Unpackers { self: BuildTplComposite =>
  val c: blackbox.Context

  import c.universe._

//  private lazy val xx = InspectMacro("Packed2tpl", 1, mkError = true)
    private lazy val xx = InspectMacro("Packed2tpl", 5)

  def packed2tpl(
    typess: List[List[Tree]],
    postTypes: List[Tree],
    indexes: Indexes,
    composite: Boolean = false,
    txMetas: Int = 0
  ): Tree = {
    val v          = q"v"
    val next       = q"vs.next()"
    val levels     = typess.size - txMetas
    val unpackerss = mutable.Seq.fill(typess.size)(List.empty[Tree])
    //    val unpackerss = mutable.Seq.fill(levels)(List.empty[Tree])


    def compositeCasts(typess: List[List[Tree]], offset: Int = 0): Seq[Tree] = {
      typess.flatMap {
        case Nil   => None
        case types => Some(q"(..$types)")
      }
    }

    val txMetaComposites = typess.takeRight(txMetas)
    val metaOffset       = typess.take(levels).flatten.length
    val txMetaData       = compositeCasts(txMetaComposites, levels + metaOffset)

    val typess1 = typess.take(levels)

    val nestedTypes = typess1.init.foldRight(List(tq"(..${typess1.last})"), levels - 2) {
      case (types, (acc, 0)) if txMetas != 0 => (tq"(..$types, List[${acc.head}], ..$txMetaData)" +: acc, -1)
      case (types, (acc, level))             => (tq"(..$types, List[${acc.head}])" +: acc, level - 1)
    }._1

    def resolveTxGroups(txCompositeGroups: Seq[IndexNode]): Seq[Seq[Tree]] = {
      def resolve(nodes: Seq[IndexNode], acc: Seq[Tree]): Seq[Tree] = nodes.flatMap {
        case AttrIndex(_, _, lambdaIndex, _) => acc :+ unpackLambdas(next)(lambdaIndex)
        case Indexes(_, _, _, nodes)         => resolve(nodes, acc)
      }
      txCompositeGroups.collect {
        case Indexes(_, _, _, nodes) => resolve(nodes, Nil)
      }
    }

    def setUnpacker(node: IndexNode, level: Int, i: Int): Unit = {
      node match {
        case AttrIndex(_, _, lambdaIndex, _) if level > 0 && i == 0 =>
          unpackerss(level) = unpackerss(level) :+ unpackLambdas(v)(lambdaIndex)

        case AttrIndex(_, _, lambdaIndex, _) =>
          unpackerss(level) = unpackerss(level) :+ unpackLambdas(next)(lambdaIndex)

        case Indexes(_, _, true, attrs) =>
          unpackerss(level) = unpackerss(level) :+ q"${TermName("nested" + (level + 1))}"
          setUnpackers(attrs, level + 1, 0)

        case Indexes("Tx_", _, _, txGroups) if txMetas != 0 =>
          val txMetaLambas = resolveTxGroups(txGroups).map(txGroup => q"(..$txGroup)")
          unpackerss(level) = unpackerss(level) ++ txMetaLambas

        case Indexes(_, _, _, attrs) =>
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
      val nested = unpackerss.take(levels).zipWithIndex.tail.map {
        case (unpackers, level) => mkNested(level, unpackers)
      }
      q"""{
          ..$nested
          (..${unpackerss.head})
         }
       """
    }

    def unpackComposite = {
      var i                   = -1
      val unpackers           = unpackerss.head
      val compositeUnpackerss = typess.map { types =>
        types.map { _ =>
          i += 1
          unpackers(i)
        }
      }
      val ordinaryComposites  = compositeUnpackerss.take(compositeUnpackerss.length - txMetas)
      val txMetaComposites    = compositeUnpackerss.takeRight(txMetas)
      val firstComposites     = ordinaryComposites.init
      val lastComposite       = ordinaryComposites.last

      def compositeTuples(unpackerss: List[List[Tree]]): List[Tree] = unpackerss.flatMap {
        case Nil       => None
        case unpackers => Some(q"(..$unpackers)")
      }

      if (txMetas > 0) {
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
      , levels
      , txMetas
      , indexes
      , typess
      , typess1
      , "----------"
      , nestedTypes
      , unpackerss.toList
      , txMetaData
      , tree
    )
    tree
  }
}
