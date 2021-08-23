package molecule.core.macros.build.tpl

import molecule.core.macros.build.BuildBase
import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox


trait BuildTplComposite extends TreeOps { self: BuildTpl with BuildBase =>
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildTplComposite", 1)


  def tplComposite(
    castss: List[List[Int => Tree]],
    txMetas: Int
  ): Tree = {

    if (txMetas == 0) {
      val t = q"(..${compositeCasts(castss)})"
      xx(1, txMetas, castss, t)
      t
    } else {
      val ordinaryComposites = castss.take(castss.length - txMetas)
      val txMetaComposites   = castss.takeRight(txMetas)
      val firstComposites    = ordinaryComposites.init
      val lastComposite      = ordinaryComposites.last
      val lastOffset         = firstComposites.flatten.length
      val metaOffset         = ordinaryComposites.flatten.length
      val init               = compositeCasts(firstComposites)
      val lastWithTx         = topLevel(List(lastComposite), lastOffset) ++ compositeCasts(txMetaComposites, metaOffset)

      val t = (init, lastWithTx) match {
        case (Nil, lastWithTx)  => q"(..$lastWithTx)"
        case (init, Nil)        => q"(..$init)"
        case (init, lastWithTx) => q"(..$init, (..$lastWithTx))"
      }

      xx(2, txMetas, castss, init, lastWithTx
        , t
        , q"(..${compositeCasts(castss)})"
      )
      t
    }
  }

  def compositeCasts(castss: List[List[Int => Tree]], offset: Int = 0): Seq[Tree] = {
    var i = -1 + offset
    castss.flatMap {
      case Nil   => None
      case casts => Some(q"(..${casts.map { cast => i += 1; cast(i) }})")
    }
  }
}
