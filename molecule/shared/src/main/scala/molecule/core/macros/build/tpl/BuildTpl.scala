package molecule.core.macros.build.tpl

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox


trait BuildTpl extends TreeOps { self: BuildTplComposite =>
  val c: blackbox.Context

  import c.universe._


  def topLevel(castss: List[List[Int => Tree]], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.head.map { cast =>
      i += 1
      cast(i)
    }
  }

  def tplFlat(
    castss: List[List[Int => Tree]],
    txMetaCompositesCount: Int
  ): Tree = {
    if (txMetaCompositesCount > 0) {
      // Treat tx meta data as composite
      val first = topLevel(List(castss.head))
      val last  = compositeCasts(castss.tail, castss.head.length)
      q"(..$first, ..$last)"
    } else {
      q"(..${topLevel(castss)})"
    }
  }
}
