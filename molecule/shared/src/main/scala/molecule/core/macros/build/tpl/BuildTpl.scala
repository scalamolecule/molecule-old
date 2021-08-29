package molecule.core.macros.build.tpl

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox


trait BuildTpl extends TreeOps {
  val c: blackbox.Context

  import c.universe._


  def topLevel(castss: List[List[Int => Tree]], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.head.map { cast =>
      i += 1
      cast(i)
    }
  }

  def compositeCasts(castss: List[List[Int => Tree]], offset: Int = 0): Seq[Tree] = {
    var i = -1 + offset
    castss.flatMap {
      case Nil   => None
      case casts => Some(q"(..${casts.map { cast => i += 1; cast(i) }})")
    }
  }

  def tplFlat(
    castss: List[List[Int => Tree]],
    txMetas: Int
  ): Tree = {
    if (txMetas == 0) {
      q"(..${topLevel(castss)})"

    } else {
      // Treat tx meta data as a composite
      val first      = topLevel(List(castss.head))
      val metaOffset = castss.head.length
      val last       = if (txMetas == 1) {
        castss.last.zipWithIndex.map { case (cast, i) => q"${cast(i + metaOffset)}" }
      } else {
        compositeCasts(castss.tail, castss.head.length)
      }
      q"(..$first, ..$last)"
    }
  }
}
