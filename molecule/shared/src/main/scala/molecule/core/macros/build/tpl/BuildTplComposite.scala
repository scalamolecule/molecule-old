package molecule.core.macros.build.tpl

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox


trait BuildTplComposite extends TreeOps { self: BuildTpl =>
  val c: blackbox.Context

  import c.universe._


//  def topLevel(castss: List[List[Int => Tree]], offset: Int = 0): List[Tree] = {
//    var i = -1 + offset
//    castss.head.map { cast =>
//      i += 1
//      cast(i)
//    }
//  }

  def compositeCasts(castss: List[List[Int => Tree]], offset: Int = 0): Seq[Tree] = {
    var i              = -1 + offset
    var subTupleFields = Seq.empty[Tree]
    val subTuples      = castss.flatMap {
      case Nil   => None
      case casts =>
        subTupleFields = casts.map { c =>
          i += 1
          c(i)
        }
        Some(q"(..$subTupleFields)")
    }
    subTuples
  }

  def tplComposite(
    castss: List[List[Int => Tree]],
    txMetaCompositesCount: Int
  ): Tree = {

    val ordinaryComposites = castss.take(castss.length - txMetaCompositesCount)
    val txMetaComposites   = castss.takeRight(txMetaCompositesCount)
    val firstComposites    = ordinaryComposites.init
    val lastComposite      = ordinaryComposites.last
    val lastOffset         = firstComposites.flatten.length
    val metaOffset         = ordinaryComposites.flatten.length

    if (txMetaCompositesCount > 0) {
      val first = compositeCasts(firstComposites)
      val last  = topLevel(List(lastComposite), lastOffset) ++ compositeCasts(txMetaComposites, metaOffset)

      //      z(1, model0, types, castss, first, last)
      (first, last) match {
        case (Nil, last)   => q"(..$last)"
        case (first, Nil)  => q"(..$first)"
        case (first, last) => q"(..$first, (..$last))"
      }
    } else {
      q"(..${compositeCasts(castss)})"
    }
  }
}
