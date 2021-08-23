package molecule.core.macros.build.tpl

import molecule.core.macros.build.BuildBase
import molecule.core.transform.Dsl2Model
import scala.reflect.macros.blackbox


private[molecule] trait BuildTplNested extends BuildBase { self: BuildTplComposite with BuildTpl =>
  val c: blackbox.Context

  import c.universe._

    private lazy val xx = InspectMacro("BuildTplNested", 5, mkError = true)
//  private lazy val xx = InspectMacro("BuildTplNested", 2)

  case class buildTplNested(
    castss: List[List[Int => Tree]],
    typess: List[List[Tree]],
    OutTypes: Seq[Type],
    postCasts: List[Int => Tree],
    txMetas: Int
  ) {
    val levels = castss.size - txMetas
    lazy val t1: Tree = tq"List[(..${if (levels == 2) typess(1) else typess(1) :+ t2})]"
    lazy val t2: Tree = tq"List[(..${if (levels == 3) typess(2) else typess(2) :+ t3})]"
    lazy val t3: Tree = tq"List[(..${if (levels == 4) typess(3) else typess(3) :+ t4})]"
    lazy val t4: Tree = tq"List[(..${if (levels == 5) typess(4) else typess(4) :+ t5})]"
    lazy val t5: Tree = tq"List[(..${if (levels == 6) typess(5) else typess(5) :+ t6})]"
    lazy val t6: Tree = tq"List[(..${if (levels == 7) typess(6) else typess(6) :+ t7})]"
    lazy val t7: Tree = tq"List[(..${typess(7)})]"

    // Skip prepended entity id columns for sorting nested data
    var colIndex = levels - 1

    def castLevel(level: Int): List[Tree] = castss(level).map { castLambda =>
      colIndex += 1
      castLambda(colIndex)
    }

    val preFields = castLevel(0)

    def branch0until(subLevels: () => Tree): Tree = {
      val subBranches = subLevels()
      if (txMetas == 0) {
        q"""
          final override def tplBranch0(row: java.util.List[AnyRef], subBranches: List[Any]): (..$OutTypes) = (..$preFields, subBranches.asInstanceOf[$t1])
          ..$subBranches
        """
      } else {
        val txMetaComposites = castss.takeRight(txMetas)
        val metaOffset       = castss.take(levels).flatten.length
        val txMetaData       = compositeCasts(txMetaComposites, levels + metaOffset)

        xx(1, txMetas, levels, metaOffset, typess, castss, txMetaData)

        if (txMetaData.isEmpty)
          q"""
            final override def tplBranch0(row: java.util.List[AnyRef], subBranches: List[Any]): (..$OutTypes) = (..$preFields, subBranches.asInstanceOf[$t1])
            ..$subBranches
          """
        else
          q"""
            final override def tplBranch0(row: java.util.List[AnyRef], subBranches: List[Any]): (..$OutTypes) = (..$preFields, subBranches.asInstanceOf[$t1], ..$txMetaData)
            ..$subBranches
          """
      }
    }

    lazy val level1: () => Tree = () =>
      q"final override def tplLeaf1(row: java.util.List[AnyRef]): Any = (..${castLevel(1)})"

    lazy val level2: () => Tree = () =>
      q"""
        final override def tplBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
        final override def tplLeaf2(row: java.util.List[AnyRef]): Any = (..${castLevel(2)})
      """

    lazy val level3: () => Tree = () =>
      q"""
        final override def tplBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
        final override def tplBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
        final override def tplLeaf3(row: java.util.List[AnyRef]): Any = (..${castLevel(3)})
      """
    lazy val level4: () => Tree = () =>
      q"""
        final override def tplBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
        final override def tplBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
        final override def tplBranch3(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(3)}, subBranches.asInstanceOf[$t4])
        final override def tplLeaf4(row: java.util.List[AnyRef]): Any = (..${castLevel(4)})
      """
    lazy val level5: () => Tree = () =>
      q"""
        final override def tplBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
        final override def tplBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
        final override def tplBranch3(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(3)}, subBranches.asInstanceOf[$t4])
        final override def tplBranch4(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(4)}, subBranches.asInstanceOf[$t5])
        final override def tplLeaf5(row: java.util.List[AnyRef]): Any = (..${castLevel(5)})
      """
    lazy val level6: () => Tree = () =>
      q"""
        final override def tplBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
        final override def tplBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
        final override def tplBranch3(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(3)}, subBranches.asInstanceOf[$t4])
        final override def tplBranch4(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(4)}, subBranches.asInstanceOf[$t5])
        final override def tplBranch5(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(5)}, subBranches.asInstanceOf[$t6])
        final override def tplLeaf6(row: java.util.List[AnyRef]): Any = (..${castLevel(6)})
      """
    lazy val level7: () => Tree = () =>
      q"""
        final override def tplBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
        final override def tplBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
        final override def tplBranch3(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(3)}, subBranches.asInstanceOf[$t4])
        final override def tplBranch4(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(4)}, subBranches.asInstanceOf[$t5])
        final override def tplBranch5(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(5)}, subBranches.asInstanceOf[$t6])
        final override def tplBranch6(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(6)}, subBranches.asInstanceOf[$t7])
        final override def tplLeaf7(row: java.util.List[AnyRef]): Any = (..${castLevel(7)})
      """

    def get: Tree = levels match {
      case 2 =>
        val t = branch0until(level1)
        xx(2, t)
        t
      case 3 =>
        val t = branch0until(level2)
        xx(3, t)
        t
      case 4 => branch0until(level3)
      case 5 => branch0until(level4)
      case 6 => branch0until(level5)
      case 7 => branch0until(level6)
      case 8 => branch0until(level7)
    }
  }
}
