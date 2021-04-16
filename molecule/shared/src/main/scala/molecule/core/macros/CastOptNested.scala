package molecule.core.macros

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox

private[molecule] trait CastOptNested extends TreeOps {
  val c: blackbox.Context

  import c.universe._

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
      case (acc, (casts, level)) =>
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
    //    println(tree)
    tree
  }


  val castOptNestedOneAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptNestedOne[String](${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"castOptNestedOneInt(${TermName("it" + colIndex)})"
    case "Int2"       => (colIndex: Int) => q"castOptNestedOneInt2(${TermName("it" + colIndex)})"
    case "Float"      => (colIndex: Int) => q"castOptNestedOneFloat(${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"castOptNestedOne[Long](${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"castOptNestedOne[Double](${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"castOptNestedOne[Boolean](${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"castOptNestedOne[Date](${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"castOptNestedOne[UUID](${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"castOptNestedOne[URI](${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"castOptNestedOneBigInt(${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"castOptNestedOneBigDecimal(${TermName("it" + colIndex)})"
    case "Any"        => (colIndex: Int) => q"row.get($colIndex)"
  }

  val castOptNestedManyAttr: String => Int => Tree = {
    case "String"     => (colIndex: Int) => q"castOptNestedMany[String](${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"castOptNestedManyInt(${TermName("it" + colIndex)})"
    case "Float"      => (colIndex: Int) => q"castOptNestedManyFloat(${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"castOptNestedMany[Long](${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"castOptNestedMany[Double](${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"castOptNestedMany[Boolean](${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"castOptNestedMany[Date](${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"castOptNestedMany[UUID](${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"castOptNestedMany[URI](${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"castOptNestedManyBigInt(${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"castOptNestedManyBigDecimal(${TermName("it" + colIndex)})"
  }

  val castOptNestedMandatoryAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOptNestedOneAttr(t.tpeS) else castOptNestedManyAttr(t.tpeS)

  val castOptNestedMandatoryRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (colIndex: Int) => q"castOptNestedOneRefAttr(${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"castOptNestedManyRefAttr(${TermName("it" + colIndex)})"


  val castOptNestedOptionalAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"castOptNestedOptOne[String](${TermName("it" + colIndex)})"
        case "Int"        => (colIndex: Int) => q"castOptNestedOptOneInt(${TermName("it" + colIndex)})"
        case "Float"      => (colIndex: Int) => q"castOptNestedOptOneFloat(${TermName("it" + colIndex)})"
        case "Long"       => (colIndex: Int) => q"castOptNestedOptOneLong(${TermName("it" + colIndex)})"
        case "Double"     => (colIndex: Int) => q"castOptNestedOptOneDouble(${TermName("it" + colIndex)})"
        case "Boolean"    => (colIndex: Int) => q"castOptNestedOptOne[Boolean](${TermName("it" + colIndex)})"
        case "Date"       => (colIndex: Int) => q"castOptNestedOptOne[Date](${TermName("it" + colIndex)})"
        case "UUID"       => (colIndex: Int) => q"castOptNestedOptOne[UUID](${TermName("it" + colIndex)})"
        case "URI"        => (colIndex: Int) => q"castOptNestedOptOne[URI](${TermName("it" + colIndex)})"
        case "BigInt"     => (colIndex: Int) => q"castOptNestedOptOneBigInt(${TermName("it" + colIndex)})"
        case "BigDecimal" => (colIndex: Int) => q"castOptNestedOptOneBigDecimal(${TermName("it" + colIndex)})"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (colIndex: Int) => q"castOptNestedOptMany[String](${TermName("it" + colIndex)})"
        case "Int"        => (colIndex: Int) => q"castOptNestedOptManyInt(${TermName("it" + colIndex)})"
        case "Float"      => (colIndex: Int) => q"castOptNestedOptManyFloat(${TermName("it" + colIndex)})"
        case "Long"       => (colIndex: Int) => q"castOptNestedOptManyLong(${TermName("it" + colIndex)})"
        case "Double"     => (colIndex: Int) => q"castOptNestedOptManyDouble(${TermName("it" + colIndex)})"
        case "Boolean"    => (colIndex: Int) => q"castOptNestedOptMany[Boolean](${TermName("it" + colIndex)})"
        case "Date"       => (colIndex: Int) => q"castOptNestedOptMany[Date](${TermName("it" + colIndex)})"
        case "UUID"       => (colIndex: Int) => q"castOptNestedOptMany[UUID](${TermName("it" + colIndex)})"
        case "URI"        => (colIndex: Int) => q"castOptNestedOptMany[URI](${TermName("it" + colIndex)})"
        case "BigInt"     => (colIndex: Int) => q"castOptNestedOptManyBigInt(${TermName("it" + colIndex)})"
        case "BigDecimal" => (colIndex: Int) => q"castOptNestedOptManyBigDecimal(${TermName("it" + colIndex)})"
      }
    }


  val castOptNestedOptionalRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (colIndex: Int) => q"castOptNestedOptOneRefAttr(${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"castOptNestedOptManyRefAttr(${TermName("it" + colIndex)})"

  val castOptNestedEnum: richTree => Int => Tree =
    (t: richTree) =>
      if (t.card == 1) {
        (colIndex: Int) => q"castOptNestedOneEnum(${TermName("it" + colIndex)})"
      } else
        (colIndex: Int) => q"castOptNestedManyEnum(${TermName("it" + colIndex)})"


  val castOptNestedEnumOpt: richTree => Int => Tree = (t: richTree) => {
    if (t.card == 1)
      (colIndex: Int) => q"castOptNestedOptOneEnum(${TermName("it" + colIndex)})"
    else
      (colIndex: Int) => q"castOptNestedOptManyEnum(${TermName("it" + colIndex)})"
  }

  val castOptNestedMandatoryMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (colIndex: Int) => q"castOptNestedMapString(${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"castOptNestedMapInt(${TermName("it" + colIndex)})"
    case "Float"      => (colIndex: Int) => q"castOptNestedMapFloat(${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"castOptNestedMapLong(${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"castOptNestedMapDouble(${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"castOptNestedMapBoolean(${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"castOptNestedMapDate(${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"castOptNestedMapUUID(${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"castOptNestedMapURI(${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"castOptNestedMapBigInt(${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"castOptNestedMapBigDecimal(${TermName("it" + colIndex)})"
  }

  val castOptNestedOptionalMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (colIndex: Int) => q"castOptNestedOptMapString(${TermName("it" + colIndex)})"
    case "Int"        => (colIndex: Int) => q"castOptNestedOptMapInt(${TermName("it" + colIndex)})"
    case "Float"      => (colIndex: Int) => q"castOptNestedOptMapFloat(${TermName("it" + colIndex)})"
    case "Long"       => (colIndex: Int) => q"castOptNestedOptMapLong(${TermName("it" + colIndex)})"
    case "Double"     => (colIndex: Int) => q"castOptNestedOptMapDouble(${TermName("it" + colIndex)})"
    case "Boolean"    => (colIndex: Int) => q"castOptNestedOptMapBoolean(${TermName("it" + colIndex)})"
    case "Date"       => (colIndex: Int) => q"castOptNestedOptMapDate(${TermName("it" + colIndex)})"
    case "UUID"       => (colIndex: Int) => q"castOptNestedOptMapUUID(${TermName("it" + colIndex)})"
    case "URI"        => (colIndex: Int) => q"castOptNestedOptMapURI(${TermName("it" + colIndex)})"
    case "BigInt"     => (colIndex: Int) => q"castOptNestedOptMapBigInt(${TermName("it" + colIndex)})"
    case "BigDecimal" => (colIndex: Int) => q"castOptNestedOptMapBigDecimal(${TermName("it" + colIndex)})"
  }
}
