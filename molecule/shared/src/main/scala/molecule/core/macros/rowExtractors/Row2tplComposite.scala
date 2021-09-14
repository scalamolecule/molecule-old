package molecule.core.macros.rowExtractors

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox


trait Row2tplComposite extends TreeOps { self: Row2tplFlat =>
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Row2tplComposite", 1, mkError = true)
  private lazy val xx = InspectMacro("Row2tplComposite", 10)

  def tplComposite(castss: List[List[Int => Tree]], txMetas: Int): Tree =
    if (txMetas == 0) {
      val res = q"(..${compositeCasts(castss)})"
      xx(1, txMetas, castss, res)
      res

    } else {
      val ordinaryComposites = castss.take(castss.length - txMetas)
      val txMetaComposites   = castss.takeRight(txMetas)
      val firstComposites    = ordinaryComposites.init
      val lastComposite      = ordinaryComposites.last
      val lastOffset         = firstComposites.flatten.length
      val metaOffset         = ordinaryComposites.flatten.length
      val init               = compositeCasts(firstComposites)
      val txCasts            = if (txMetas == 1) {
        txMetaComposites.head.zipWithIndex.map { case (cast, i) => q"${cast(i + metaOffset)}" }
      } else {
        compositeCasts(txMetaComposites, metaOffset)
      }
      val lastWithTx         = topLevel(List(lastComposite), lastOffset) ++ txCasts

      val tree = (init, lastWithTx) match {
        case (Nil, lastWithTx)  => q"(..$lastWithTx)"
        case (init, Nil)        => q"(..$init)"
        case (init, lastWithTx) => q"(..$init, (..$lastWithTx))"
      }

      xx(2, txMetas, castss, init, lastComposite, txCasts, lastWithTx, tree)
      tree
    }
}
