package molecule.core.macros.build.tpl

import molecule.core.macros.build.BuildBase
import molecule.core.transform.Dsl2Model
import scala.reflect.macros.blackbox


private[molecule] trait BuildTplNested extends BuildBase {
  val c: blackbox.Context

  import c.universe._

  case class buildTplNested(
    castss: List[List[Int => Tree]],
    typess: List[List[Tree]],
    OutTypes: Seq[Type],
    postTypes: List[Tree],
    postCasts: List[Int => Tree]
  ) {
    val levelCount = castss.size
    lazy val t1: Tree = tq"List[(..${if (levelCount == 2) typess(1) else typess(1) :+ t2})]"
    lazy val t2: Tree = tq"List[(..${if (levelCount == 3) typess(2) else typess(2) :+ t3})]"
    lazy val t3: Tree = tq"List[(..${if (levelCount == 4) typess(3) else typess(3) :+ t4})]"
    lazy val t4: Tree = tq"List[(..${if (levelCount == 5) typess(4) else typess(4) :+ t5})]"
    lazy val t5: Tree = tq"List[(..${if (levelCount == 6) typess(5) else typess(5) :+ t6})]"
    lazy val t6: Tree = tq"List[(..${if (levelCount == 7) typess(6) else typess(6) :+ t7})]"
    lazy val t7: Tree = tq"List[(..${typess(7)})]"

    var colIndex = levelCount - 1

    def castLevel(level: Int): List[Tree] = castss(level).map { castLambda =>
      colIndex += 1
      castLambda(colIndex)
    }

    def branch0until(subLevels: () => Tree): Tree = if (postCasts.isEmpty) {
      q"""
         final override def tplBranch0(row: java.util.List[AnyRef], subBranches: List[Any]): (..$OutTypes) = (..${castLevel(0)}, subBranches.asInstanceOf[$t1])
         ..${subLevels()}
       """
    } else {
      // Ensuring that post fields are last
      val pre        = castLevel(0)
      val subCastes  = subLevels()
      val postFields = postCasts.map { postCastLambda =>
        colIndex += 1
        postCastLambda(colIndex)
      }
      q"""
         final override def tplBranch0(row: java.util.List[AnyRef], subBranches: List[Any]): (..$OutTypes) = (..$pre, subBranches.asInstanceOf[$t1], ..$postFields)
         ..$subCastes
       """
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

    def get: Tree = levelCount match {
      case 2 => branch0until(level1)
      case 3 => branch0until(level2)
      case 4 => branch0until(level3)
      case 5 => branch0until(level4)
      case 6 => branch0until(level5)
      case 7 => branch0until(level6)
      case 8 => branch0until(level7)
    }
  }
}
