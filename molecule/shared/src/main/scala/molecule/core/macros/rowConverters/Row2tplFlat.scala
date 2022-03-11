package molecule.core.macros.rowConverters

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox


trait Row2tplFlat extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Row2tplFlat", 1, mkError = true)
  private lazy val xx = InspectMacro("Row2tplFlat", 20)

  def topLevel(castss: List[List[Int => Tree]], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.head.map { cast =>
      i += 1
      cast(i)
    }
  }

  def compositeCasts(castss: List[List[Int => Tree]], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.flatMap {
      case Nil   => None
      case casts => Some(q"(..${casts.map { cast => i += 1; cast(i) }})")
    }
  }

  def tplFlat(castss: List[List[Int => Tree]], txMetas: Int): Tree =
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
      val tree       = q"(..$first, ..$last)"
      xx(2, txMetas, castss, first, last, tree)
      tree
    }
}
