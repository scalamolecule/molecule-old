package molecule.core.macros.build.tpl

import molecule.core.macros.lambdaTrees.LambdaCastOptNested
import scala.reflect.macros.blackbox

private[molecule] trait BuildTplOptNested extends LambdaCastOptNested {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildTplOptNested", 2, 1)

  def castOptNestedRows(
    levelCasts: List[List[Int => Tree]],
    OutTypes: Seq[Type],
    optNestedRefIndexes: Map[Int, List[Int]],
    optNestedTacitIndexes: Map[Int, List[Int]],
  ): Tree = {
    def getLambdas(casts: List[Int => Tree], level: Int): List[Tree] = {
      casts.zipWithIndex.foldLeft(0, List.empty[Tree], 0) {
        case ((0, acc, _), (cast, i))
          if optNestedRefIndexes.keySet.contains(level) &&
            optNestedRefIndexes(level).contains(i)  => (1, acc :+ cast(level * 100 + i), i)
        case ((0, acc, 0), (cast, i)) if level == 0 => (0, acc :+ cast(i), 0)
        case ((0, acc, 0), (cast, _))               => (0, acc :+ cast(0), 0)
        case ((1, acc, refIndex), (cast, _))        => (1, acc :+ cast(level * 100 + refIndex), refIndex)
        case cast                                   => abort("Unexpected cast data: " + cast)
      }._2
    }

    val lastLevel = levelCasts.length - 1
    val lambdas   = levelCasts.zipWithIndex.foldLeft(List.empty[Tree]) {
      case (acc, (_, 0))         => acc
      case (acc, (casts, level)) => {
        val nested_x = TermName("nested_" + level)
        val nested_y = TermName("nested_" + (level + 1))

        val iterators = if (!optNestedRefIndexes.keySet.contains(level)) {
          q"val it0 = sub.next.asInstanceOf[jMap[_, _]].values().iterator()"
        } else {
          val extraIterators = optNestedRefIndexes(level).map { refIndex =>
            val itX = TermName("it" + (level * 100 + refIndex))
            q"""
               lazy val $itX = {
                 val valueMap = it0.next.asInstanceOf[jMap[_, _]]
                 if (${casts.size} != $refIndex + valueMap.size)
                   throw new RuntimeException("Missing value in: " + valueMap)
                 valueMap.values().iterator()
               }
             """
          }
          q"""
            val vs0 = sub.next.asInstanceOf[jMap[_, _]]
            val it0 = vs0.values().iterator()
            ..$extraIterators
           """
        }

        val subTuple = if (lastLevel == level)
          q"(..${getLambdas(casts, level)})"
        else
          q"(..${getLambdas(casts, level)}, $nested_y(it0.next))"

        val addTuple = {
          if (!optNestedTacitIndexes.keySet.contains(level)) {
            q"subTuples :+ $subTuple"
          } else {
            val tacitIndexes: List[Int] = optNestedTacitIndexes(level)
            val (checks, outputs)       = casts.zipWithIndex.foldLeft(
              Seq.empty[Tree], Seq.empty[Tree]
            ) {
              case ((checks, outputs), (_, i)) if tacitIndexes.contains(i) =>
                (checks :+ q"t.${TermName(s"_${i + 1}")}", outputs)

              case ((checks, outputs), (_, i)) =>
                (checks, outputs :+ q"t.${TermName(s"_${i + 1}")}")
            }

            val outputs2 = if (lastLevel == level) outputs else
              outputs :+ q"t.${TermName(s"_${casts.length + 1}")}"

            q"""
              val t = $subTuple
              if (Seq(..$checks).forall(_.nonEmpty))
                subTuples :+ (..$outputs2)
              else
                subTuples
             """
          }
        }

        val (none, subIterator) = if (level == 1)
          (q"null",
            q"""
              v.asInstanceOf[jMap[_,_]].values().iterator().next
               .asInstanceOf[jList[_]].iterator()
             """)
        else
          (q""""__none__"""",
            q"v.asInstanceOf[jList[_]].iterator()")

        q"""
          private val $nested_x = (subData: Any) => subData match {
            case $none => List.empty[Any]
            case v     => {
              val sub = $subIterator
              var subTuples = List.empty[Any]
              while (sub.hasNext) {
                ..$iterators
                subTuples = try {
                  ..$addTuple
                } catch {
                  case e: Throwable =>
                    // Discard non-matching tuple on this level
                    subTuples
                }
              }
              subTuples
            }
          }
         """ :: acc
      }
    }

    val tree =
      q"""
        import java.net.URI
        import java.util.{Date, UUID, List => jList, Map => jMap, Iterator => jIterator, Set => jSet}

        ..$lambdas

        // Attributes before nested processed with normal lambdas using jList data
        private val nested_0 = (row: jList[AnyRef]) => (
          ..${getLambdas(levelCasts.head, 0)},
          nested_1(row.get(${levelCasts.head.length}))
        )

        final override def row2tpl(row: jList[AnyRef]): (..$OutTypes) =
          nested_0(row).asInstanceOf[(..$OutTypes)]
       """
    xx(1, tree)
    tree
  }
}
