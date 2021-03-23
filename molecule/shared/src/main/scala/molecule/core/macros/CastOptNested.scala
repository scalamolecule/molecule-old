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
    case "String"     => (i: Int) => q"castOptNestedOne[String](${TermName("it" + i)})"
    case "Int"        => (i: Int) => q"castOptNestedOneInt(${TermName("it" + i)})"
    case "Int2"       => (i: Int) => q"castOptNestedOneInt2(${TermName("it" + i)})"
    case "Float"      => (i: Int) => q"castOptNestedOneFloat(${TermName("it" + i)})"
    case "Boolean"    => (i: Int) => q"castOptNestedOne[Boolean](${TermName("it" + i)})"
    case "Long"       => (i: Int) => q"castOptNestedOne[Long](${TermName("it" + i)})"
    case "Double"     => (i: Int) => q"castOptNestedOne[Double](${TermName("it" + i)})"
    case "Date"       => (i: Int) => q"castOptNestedOne[Date](${TermName("it" + i)})"
    case "UUID"       => (i: Int) => q"castOptNestedOne[UUID](${TermName("it" + i)})"
    case "URI"        => (i: Int) => q"castOptNestedOne[URI](${TermName("it" + i)})"
    case "BigInt"     => (i: Int) => q"castOptNestedOneBigInt(${TermName("it" + i)})"
    case "BigDecimal" => (i: Int) => q"castOptNestedOneBigDecimal(${TermName("it" + i)})"
    case "Any"        => (i: Int) => q"row.get($i)"
  }

  val castOptNestedManyAttr: String => Int => Tree = {
    case "String"     => (i: Int) => q"castOptNestedMany[String](${TermName("it" + i)})"
    case "Int"        => (i: Int) => q"castOptNestedManyInt(${TermName("it" + i)})"
    case "Float"      => (i: Int) => q"castOptNestedManyFloat(${TermName("it" + i)})"
    case "Boolean"    => (i: Int) => q"castOptNestedMany[Boolean](${TermName("it" + i)})"
    case "Long"       => (i: Int) => q"castOptNestedMany[Long](${TermName("it" + i)})"
    case "Double"     => (i: Int) => q"castOptNestedMany[Double](${TermName("it" + i)})"
    case "Date"       => (i: Int) => q"castOptNestedMany[Date](${TermName("it" + i)})"
    case "UUID"       => (i: Int) => q"castOptNestedMany[UUID](${TermName("it" + i)})"
    case "URI"        => (i: Int) => q"castOptNestedMany[URI](${TermName("it" + i)})"
    case "BigInt"     => (i: Int) => q"castOptNestedManyBigInt(${TermName("it" + i)})"
    case "BigDecimal" => (i: Int) => q"castOptNestedManyBigDecimal(${TermName("it" + i)})"
  }

  val castOptNestedMandatoryAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) castOptNestedOneAttr(t.tpeS) else castOptNestedManyAttr(t.tpeS)

  val castOptNestedMandatoryRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (i: Int) => q"castOptNestedOneRefAttr(${TermName("it" + i)})"
    else
      (i: Int) => q"castOptNestedManyRefAttr(${TermName("it" + i)})"


  val castOptNestedOptionalAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1) {
      // Optional, card one
      t.tpeS match {
        case "String"     => (i: Int) => q"castOptNestedOptOne[String](${TermName("it" + i)})"
        case "Int"        => (i: Int) => q"castOptNestedOptOneInt(${TermName("it" + i)})"
        case "Float"      => (i: Int) => q"castOptNestedOptOneFloat(${TermName("it" + i)})"
        case "Boolean"    => (i: Int) => q"castOptNestedOptOne[Boolean](${TermName("it" + i)})"
        case "Long"       => (i: Int) => q"castOptNestedOptOneLong(${TermName("it" + i)})"
        case "Double"     => (i: Int) => q"castOptNestedOptOneDouble(${TermName("it" + i)})"
        case "Date"       => (i: Int) => q"castOptNestedOptOne[Date](${TermName("it" + i)})"
        case "UUID"       => (i: Int) => q"castOptNestedOptOne[UUID](${TermName("it" + i)})"
        case "URI"        => (i: Int) => q"castOptNestedOptOne[URI](${TermName("it" + i)})"
        case "BigInt"     => (i: Int) => q"castOptNestedOptOneBigInt(${TermName("it" + i)})"
        case "BigDecimal" => (i: Int) => q"castOptNestedOptOneBigDecimal(${TermName("it" + i)})"
      }
    } else {
      // Optional, card many
      t.tpeS match {
        case "String"     => (i: Int) => q"castOptNestedOptMany[String](${TermName("it" + i)})"
        case "Int"        => (i: Int) => q"castOptNestedOptManyInt(${TermName("it" + i)})"
        case "Float"      => (i: Int) => q"castOptNestedOptManyFloat(${TermName("it" + i)})"
        case "Boolean"    => (i: Int) => q"castOptNestedOptMany[Boolean](${TermName("it" + i)})"
        case "Long"       => (i: Int) => q"castOptNestedOptManyLong(${TermName("it" + i)})"
        case "Double"     => (i: Int) => q"castOptNestedOptManyDouble(${TermName("it" + i)})"
        case "Date"       => (i: Int) => q"castOptNestedOptMany[Date](${TermName("it" + i)})"
        case "UUID"       => (i: Int) => q"castOptNestedOptMany[UUID](${TermName("it" + i)})"
        case "URI"        => (i: Int) => q"castOptNestedOptMany[URI](${TermName("it" + i)})"
        case "BigInt"     => (i: Int) => q"castOptNestedOptManyBigInt(${TermName("it" + i)})"
        case "BigDecimal" => (i: Int) => q"castOptNestedOptManyBigDecimal(${TermName("it" + i)})"
      }
    }


  val castOptNestedOptionalRefAttr: richTree => Int => Tree = (t: richTree) =>
    if (t.card == 1)
      (i: Int) => q"castOptNestedOptOneRefAttr(${TermName("it" + i)})"
    else
      (i: Int) => q"castOptNestedOptManyRefAttr(${TermName("it" + i)})"

  val castOptNestedEnum: richTree => Int => Tree =
    (t: richTree) =>
      if (t.card == 1) {
        (i: Int) => q"castOptNestedOneEnum(${TermName("it" + i)})"
      } else
        (i: Int) => q"castOptNestedManyEnum(${TermName("it" + i)})"


  val castOptNestedEnumOpt: richTree => Int => Tree = (t: richTree) => {
    if (t.card == 1)
      (i: Int) => q"castOptNestedOptOneEnum(${TermName("it" + i)})"
    else
      (i: Int) => q"castOptNestedOptManyEnum(${TermName("it" + i)})"
  }

  val castOptNestedMandatoryMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (i: Int) => q"castOptNestedMapString(${TermName("it" + i)})"
    case "Int"        => (i: Int) => q"castOptNestedMapInt(${TermName("it" + i)})"
    case "Float"      => (i: Int) => q"castOptNestedMapFloat(${TermName("it" + i)})"
    case "Boolean"    => (i: Int) => q"castOptNestedMapBoolean(${TermName("it" + i)})"
    case "Long"       => (i: Int) => q"castOptNestedMapLong(${TermName("it" + i)})"
    case "Double"     => (i: Int) => q"castOptNestedMapDouble(${TermName("it" + i)})"
    case "Date"       => (i: Int) => q"castOptNestedMapDate(${TermName("it" + i)})"
    case "UUID"       => (i: Int) => q"castOptNestedMapUUID(${TermName("it" + i)})"
    case "URI"        => (i: Int) => q"castOptNestedMapURI(${TermName("it" + i)})"
    case "BigInt"     => (i: Int) => q"castOptNestedMapBigInt(${TermName("it" + i)})"
    case "BigDecimal" => (i: Int) => q"castOptNestedMapBigDecimal(${TermName("it" + i)})"
  }

  val castOptNestedOptionalMapAttr: richTree => Int => Tree = (t: richTree) => t.tpeS match {
    case "String"     => (i: Int) => q"castOptNestedOptMapString(${TermName("it" + i)})"
    case "Int"        => (i: Int) => q"castOptNestedOptMapInt(${TermName("it" + i)})"
    case "Float"      => (i: Int) => q"castOptNestedOptMapFloat(${TermName("it" + i)})"
    case "Boolean"    => (i: Int) => q"castOptNestedOptMapBoolean(${TermName("it" + i)})"
    case "Long"       => (i: Int) => q"castOptNestedOptMapLong(${TermName("it" + i)})"
    case "Double"     => (i: Int) => q"castOptNestedOptMapDouble(${TermName("it" + i)})"
    case "Date"       => (i: Int) => q"castOptNestedOptMapDate(${TermName("it" + i)})"
    case "UUID"       => (i: Int) => q"castOptNestedOptMapUUID(${TermName("it" + i)})"
    case "URI"        => (i: Int) => q"castOptNestedOptMapURI(${TermName("it" + i)})"
    case "BigInt"     => (i: Int) => q"castOptNestedOptMapBigInt(${TermName("it" + i)})"
    case "BigDecimal" => (i: Int) => q"castOptNestedOptMapBigDecimal(${TermName("it" + i)})"
  }
}
