package molecule.core.macros.rowExtractors

import molecule.core.macros.rowAttr.{JsonBase, RowValue2json}
import molecule.core.marshalling.nodes._
import scala.reflect.macros.blackbox


trait Row2jsonNested extends JsonBase with RowValue2json {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildJsonNested", 10)

  case class buildJsonNested(
    obj: Obj,
    nestedRef: List[String],
    txMetas: Int,
    postJsons: List[(Int, Int) => Tree]
  ) {
    // Filter out post props since we handle those with postJsons
    val objWithoutPostProps = obj.copy(props = obj.props.foldLeft(List.empty[Node], false) {
      case ((acc, true), _)                 => (acc, true)
      case ((acc, _), o@Obj(_, _, true, _)) => (acc :+ o, true)
      case ((acc, _), node)                 => (acc :+ node, false)
    }._1)

    // Object for each level
    val (objs, exitDepths, initTabs): (Seq[Obj], Seq[Int], Seq[Int]) = {
      def recurse(obj: Obj, depth: Int): (Seq[Obj], Seq[Int], Seq[Int]) = obj.props.zipWithIndex.foldLeft(
        Seq(obj.copy(props = Nil)), Seq(0), Seq(0)
      ) {
        case ((objs, depths, tabs), (node, i)) =>
          val last = i == obj.props.size - 1
          node match {
            case p: Prop =>
              (
                objs.init :+ objs.last.copy(props = objs.last.props :+ p),
                depths.init :+ depth,
                tabs
              )

            case o@Obj(_, _, true, _) =>
              val (nestedObjs, nestedDepths, nestedTabs) = recurse(o, 0) // Start over for next level
              (
                objs ++ nestedObjs,
                depths ++ nestedDepths,
                tabs ++ nestedTabs.map(_ + tabs.last + 2)
              )

            // First namespace that has been back-reffed to
            case o: Obj if !last =>
              val (refObjs, refDepths, refTabs) = recurse(o, depth + 1)
              (
                (objs.init :+ objs.last.copy(props = objs.last.props :+ refObjs.head)) ++ refObjs.tail,
                depths.init :+ depth, // exit depth
                (tabs.init :+ tabs.last + refTabs.head) ++ refTabs.tail.map(_ + tabs.last + 1)
              )

            case o: Obj =>
              val (refObjs, refDepths, refTabs) = recurse(o, depth + 1)
              if (refObjs.head.props.nonEmpty) {
                (
                  (objs.init :+ objs.last.copy(props = objs.last.props :+ refObjs.head)) ++ refObjs.tail,
                  (depths.init :+ refDepths.head) ++ refDepths.tail,
                  (tabs.init :+ tabs.last + refTabs.head) ++ refTabs.tail.map(_ + tabs.last + 1)
                )
              } else {
                // Intermediary ref without attributes
                (
                  (objs.init :+ objs.last.copy(props = objs.last.props :+ refObjs.head)) ++ refObjs.tail,
                  (depths.init :+ refDepths.head + 1) ++ refDepths.tail,
                  tabs ++ refTabs.tail.map(_ + tabs.last + 1)
                )
              }
          }
      }
      recurse(objWithoutPostProps, 0)
    }
    xx(2
      , obj
      , objWithoutPostProps
      , objs.mkString("\n---\n")
      , exitDepths
      , initTabs
      , postJsons
    )

    val levelCount = objs.size

    // Skip initial entity id columns used for sorting rows
    var colIndex = levelCount - 1

    def branchPairs(level: Int): Tree = {
      val exitDepth = exitDepths(level)
      def recurse(obj: Obj, depth: Int, tabs: Int, nestedAdded: Boolean = false): Seq[Tree] = {
        var next  = false
        val nodes = obj.props
        if (nodes.isEmpty) {
          Seq(
            q"""sb.append(nested)"""
          )
        } else {
          val newLineCode = Seq(
            q"""sb.append(${"," + indent(tabs + 1)})""",
          )
          nodes.zipWithIndex.flatMap { case (node, i) =>
            val lastNode  = i + 1 == nodes.size
            val lastLevel = (level + 1) == levelCount
            val newLine   = if (next) newLineCode else {
              next = true
              Nil
            }

            node match {
              case Prop(_, prop, baseTpe, _, group, _) =>
                colIndex += 1
                val nested =
                  if (nestedAdded) {
                    Nil
                  } else if (lastNode && lastLevel) {
                    Seq(q"""sb.append(nested)""")
                  } else if (lastNode) {
                    Seq(
                      q"""sb.append(${"," + indent(tabs + 1)})""",
                      q"""sb.append(nested)"""
                    )
                  } else Nil
                newLine ++ Seq(getRowValue2jsonLambda(group, baseTpe, prop)(colIndex, tabs + 1)) ++ nested

              case o: Obj if depth == exitDepth =>
                newLine ++ Seq(
                  q"""quote(sb, ${o.ref})""",
                  q"""sb.append(${": {" + indent(tabs + 2)})""",
                  q"""..${recurse(o, depth + 1, tabs + 1, true)}""",
                  q"""sb.append(${indent(tabs + 1) + "}"})""",
                ) ++ (
                  if (lastNode) {
                    Seq(
                      q"""sb.append(${"," + indent(tabs + 1)})"""
                    )
                  } else Nil
                  ) ++ Seq(q"""sb.append(nested)""")

              case o: Obj =>
                newLine ++ Seq(
                  q"""quote(sb, ${o.ref})""",
                  q"""sb.append(${": {" + indent(tabs + 2)})""",
                  q"""..${recurse(o, depth + 1, tabs + 1, nestedAdded)}""",
                  q"""sb.append(${indent(tabs + 1) + "}"})""",
                )
            }
          }
        }
      }
      val resolved = recurse(objs(level), 0, initTabs(level))
      if (resolved.nonEmpty)
        q"""(nested: StringBuffer) => { ..$resolved }"""
      else
        q"""(nested: StringBuffer) => { sb.append(nested) }"""
    }

    def branch0untilXX(subLevels: () => Tree): Tree = {
      val subBranches = subLevels()
      if (txMetas == 0) {
        q"""
         final override def jsonBranch0(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(0, ${initTabs.head}, ${initTabs(1)}, sb, {..${branchPairs(0)}}, ${nestedRef.head}, leaf)
         ..$subBranches
       """
      } else {
        // Ensure that post fields are last (run branchPairs2 first so that mutable colIndex increments orderly)
        val pre       = branchPairs(0)
        val postProps = postJsons.flatMap { portJsonLambda =>
          colIndex += 1
          Seq(
            q"""sb.append(${"," + indent(1)})""",
            q"""${portJsonLambda(colIndex, 0)}"""
          )
        }

        //        val txMetaComposites = castss.takeRight(txMetas)
        //        val metaOffset       = castss.take(levels).flatten.length
        //        val txMetaData       = compositeCasts(txMetaComposites, levels + metaOffset)


        q"""
         final override def jsonBranch0(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(0, ${initTabs.head}, ${initTabs(1)}, sb, {..$pre}, ${nestedRef.head}, leaf, {..$postProps})
         ..$subBranches
       """
      }
    }
    def branch0until(subLevels: () => Tree): Tree = if (postJsons.isEmpty) {
      q"""
         final override def jsonBranch0(sb: StringBuffer, row: java.util.List[AnyRef], leaf: StringBuffer): StringBuffer = branch(0, ${initTabs.head}, ${initTabs(1)}, sb, {..${branchPairs(0)}}, ${nestedRef.head}, leaf)
         ..${subLevels()}
       """
    } else {
      // Ensure that post fields are last (run branchPairs2 first so that mutable colIndex increments orderly)
      val pre       = branchPairs(0)
      val subJsons  = subLevels()
      val postProps = postJsons.flatMap { portJsonLambda =>
        colIndex += 1
        Seq(
          q"""sb.append(${"," + indent(1)})""",
          q"""${portJsonLambda(colIndex, 0)}"""
        )
      }
      q"""
         final override def jsonBranch0(sb: StringBuffer, row: java.util.List[AnyRef], leaf: StringBuffer): StringBuffer = branch(0, ${initTabs.head}, ${initTabs(1)}, sb, {..$pre}, ${nestedRef.head}, leaf, {..$postProps})
         ..$subJsons
       """
    }

    lazy val level1: () => Tree = () =>
      q"""
         final override def jsonLeaf1(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = leaf(${initTabs(1)}, sb, {..${branchPairs(1)}})
       """
    lazy val level2: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(1, ${initTabs(1)}, ${initTabs(2)}, sb, ${branchPairs(1)}, ${nestedRef(1)}, leaf)
         final override def jsonLeaf2(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = leaf(${initTabs(2)}, sb, {..${branchPairs(2)}})
       """
    lazy val level3: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(1, ${initTabs(1)}, ${initTabs(2)}, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(2, ${initTabs(2)}, ${initTabs(3)}, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonLeaf3(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = leaf(${initTabs(3)}, sb, {..${branchPairs(3)}})
       """
    lazy val level4: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(1, ${initTabs(1)}, ${initTabs(2)}, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(2, ${initTabs(2)}, ${initTabs(3)}, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonBranch3(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(3, ${initTabs(3)}, ${initTabs(4)}, sb, {..${branchPairs(3)}}, ${nestedRef(3)}, leaf)
         final override def jsonLeaf4(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = leaf(${initTabs(4)}, sb, {..${branchPairs(4)}})
       """
    lazy val level5: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(1, ${initTabs(1)}, ${initTabs(2)}, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(2, ${initTabs(2)}, ${initTabs(3)}, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonBranch3(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(3, ${initTabs(3)}, ${initTabs(4)}, sb, {..${branchPairs(3)}}, ${nestedRef(3)}, leaf)
         final override def jsonBranch4(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(4, ${initTabs(4)}, ${initTabs(5)}, sb, {..${branchPairs(4)}}, ${nestedRef(4)}, leaf)
         final override def jsonLeaf5(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = leaf(${initTabs(5)}, sb, {..${branchPairs(5)}})
       """
    lazy val level6: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(1, ${initTabs(1)}, ${initTabs(2)}, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(2, ${initTabs(2)}, ${initTabs(3)}, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonBranch3(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(3, ${initTabs(3)}, ${initTabs(4)}, sb, {..${branchPairs(3)}}, ${nestedRef(3)}, leaf)
         final override def jsonBranch4(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(4, ${initTabs(4)}, ${initTabs(5)}, sb, {..${branchPairs(4)}}, ${nestedRef(4)}, leaf)
         final override def jsonBranch5(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(5, ${initTabs(5)}, ${initTabs(6)}, sb, {..${branchPairs(5)}}, ${nestedRef(5)}, leaf)
         final override def jsonLeaf6(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = leaf(${initTabs(6)}, sb, {..${branchPairs(6)}})
       """
    lazy val level7: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(1, ${initTabs(1)}, ${initTabs(2)}, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(2, ${initTabs(2)}, ${initTabs(3)}, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonBranch3(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(3, ${initTabs(3)}, ${initTabs(4)}, sb, {..${branchPairs(3)}}, ${nestedRef(3)}, leaf)
         final override def jsonBranch4(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(4, ${initTabs(4)}, ${initTabs(5)}, sb, {..${branchPairs(4)}}, ${nestedRef(4)}, leaf)
         final override def jsonBranch5(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(5, ${initTabs(5)}, ${initTabs(6)}, sb, {..${branchPairs(5)}}, ${nestedRef(5)}, leaf)
         final override def jsonBranch6(sb: StringBuffer, row: jList[AnyRef], leaf: StringBuffer): StringBuffer = branch(6, ${initTabs(6)}, ${initTabs(7)}, sb, {..${branchPairs(6)}}, ${nestedRef(6)}, leaf)
         final override def jsonLeaf7(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = leaf(${initTabs(7)}, sb, {..${branchPairs(7)}})
       """

    def get: Tree = objs.size match {
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
