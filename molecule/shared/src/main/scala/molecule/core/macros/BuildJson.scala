package molecule.core.macros

import scala.reflect.macros.blackbox


trait BuildJson extends BuildBase {
  val c: blackbox.Context

  import c.universe._

  lazy override val p = InspectMacro("BuildJson", 1, 900)

  def jsonCode(obj: Obj, i0: Int = -1, isNested: Boolean = false, level: Int = 0): (Tree, Int) = {
    // Property index of row/tuple
    var i       = if (isNested && i0 == -1) -1 else i0
    val newLine = q"""sb.append(","); sb.append(indent($level))"""

    def properties(nodes: List[Node]): List[Tree] = {
      var next = false
      var fieldNames = List.empty[String]
      if (isNested) {
        nodes.flatMap { node =>
          val nl    = if (next) Seq(newLine) else Nil
          val trees = node match {
            case Prop(_, fieldName, tpe, _, json, optAggr) =>
              i += 1
              val field = Seq(q"""quote(sb, $fieldName)""", q"""sb.append(": ")""")
              // Only generate 1 property, even if attribute is repeated in molecule
              if (fieldNames.contains(fieldName)) Nil else {
                fieldNames = fieldNames :+ fieldName
                nl ++ (optAggr match {
                  case None                                       => field :+ valueFromTpl(i, tpe, level)
                  case Some(aggrTpe) if aggrTpe == tpe.toString() => field :+ valueFromTpl(i, tpe, level)
                  case Some(aggrTpe)                              => aggrErr(fieldName, aggrTpe)
                })
              }

            case o@Obj(_, _, 2, props) =>
              i += 1
              val productTpe = if (props.length == 1) {
                props.head match {
                  case Prop(_, _, tpe, _, _, _) => tq"Tuple1[$tpe]"
                  case Obj(_, _, _, _)          => tq"Tuple1[Product]"
                }
              } else {
                tq"Product"
              }
              Seq(q"tpl.productElement($i).asInstanceOf[Seq[$productTpe]].map( tpl => ${jsonCode(o, -1, isNested, level + 1)._1} )")

            case o: Obj =>
              val (subObj, j) = jsonCode(o, i, isNested, level + 1)
              i = j
              Seq(subObj)
          }
          next = true
          trees
        }

      } else {

        nodes.flatMap { node =>
          val nl         = if (next) Seq(newLine) else Nil
          val trees      = node match {
            case Prop(_, fieldName, tpe, _, json, optAggr) =>
              i += 1
              // Only generate 1 property, even if attribute is repeated in molecule
              if (fieldNames.contains(fieldName)) Nil else {
                fieldNames = fieldNames :+ fieldName
                nl ++ (optAggr match {
                  case None                                       => Seq(json(i, level))
                  case Some(aggrTpe) if aggrTpe == tpe.toString() => Seq(json(i, level))
                  case Some(aggrTpe)                              => aggrErr(fieldName, aggrTpe)
                })
              }

            case o@Obj(_, ref, 2, props) =>
              i += 1
              val productTpe = if (props.length == 1) {
                props.head match {
                  case Prop(_, _, tpe, _, _, _) => tq"Tuple1[$tpe]"
                  case Obj(_, _, _, _)          => tq"Tuple1[Product]"
                }
              } else {
                tq"Product"
              }
              nl ++ Seq(
                q"""
                   quote(sb, $ref)
                   sb.append(": {")
                   sb.append(indent(${level + 1}))
                   tpl.productElement($i).asInstanceOf[Seq[$productTpe]].map( tpl => ${jsonCode(o, -1, isNested, level + 1)._1} )
                   sb.append(indent($level))
                   sb.append("}")
                   """
              )

            case o@Obj(_, ref, _, _) =>
              val (subObj, j) = jsonCode(o, i, isNested, level + 1)
              i = j
              nl ++ Seq(
                q"""
                   quote(sb, $ref)
                   sb.append(": {")
                   sb.append(indent(${level + 1}))
                   $subObj
                   sb.append(indent($level))
                   sb.append("}")
                   """
              )
          }
          next = true
          trees
        }
      }
    }

    def aggrErr(fieldName: String, aggrTpe: String) = abort(
      s"Field `$fieldName` not available since the aggregate changes its type to `$aggrTpe`. " +
        s"Please use tuple output instead to access aggregate value."
    )

    def valueFromTpl(i: Int, tpe: Tree, level: Int): Tree = {
      val v = q"""tpl.productElement($i).asInstanceOf[$tpe]"""
      tpe.toString match {
        case "String"     => q"quote(sb, $v)"
        case "Int"        => q"sb.append($v)"
        case "Long"       => q"sb.append($v)"
        case "Double"     => q"sb.append($v)"
        case "Boolean"    => q"sb.append($v)"
        case "Date"       => q"quote(sb, date2str($v))"
        case "UUID"       => q"quote(sb, $v)"
        case "URI"        => q"quote(sb, $v)"
        case "BigInt"     => q"quote(sb, $v)"
        case "BigDecimal" => q"quote(sb, $v)"
        case "Any"        => q"jsonAnyValue(sb, $v)"

        case "Option[String]"     => q"jsonOptOneQuoted(sb, $v)"
        case "Option[Int]"        => q"jsonOptOne(sb, $v)"
        case "Option[Long]"       => q"jsonOptOne(sb, $v)"
        case "Option[Double]"     => q"jsonOptOne(sb, $v)"
        case "Option[Boolean]"    => q"jsonOptOne(sb, $v)"
        case "Option[Date]"       => q"jsonOptOneDate(sb, $v)"
        case "Option[UUID]"       => q"jsonOptOne(sb, $v)"
        case "Option[URI]"        => q"jsonOptOne(sb, $v)"
        case "Option[BigInt]"     => q"jsonOptOne(sb, $v)"
        case "Option[BigDecimal]" => q"jsonOptOne(sb, $v)"

        case "Set[String]"     => q"jsonSetQuoted(sb, $v, $level)"
        case "Set[Int]"        => q"jsonSet(sb, $v, $level)"
        case "Set[Long]"       => q"jsonSet(sb, $v, $level)"
        case "Set[Double]"     => q"jsonSet(sb, $v, $level)"
        case "Set[Boolean]"    => q"jsonSet(sb, $v, $level)"
        case "Set[Date]"       => q"jsonSetDate(sb, $v, $level)"
        case "Set[UUID]"       => q"jsonSet(sb, $v, $level)"
        case "Set[URI]"        => q"jsonSet(sb, $v, $level)"
        case "Set[BigInt]"     => q"jsonSet(sb, $v, $level)"
        case "Set[BigDecimal]" => q"jsonSet(sb, $v, $level)"

        case "Option[Set[String]]"     => q"jsonOptSetQuoted(sb, $v, $level)"
        case "Option[Set[Int]]"        => q"jsonOptSet(sb, $v, $level)"
        case "Option[Set[Long]]"       => q"jsonOptSet(sb, $v, $level)"
        case "Option[Set[Double]]"     => q"jsonOptSet(sb, $v, $level)"
        case "Option[Set[Boolean]]"    => q"jsonOptSet(sb, $v, $level)"
        case "Option[Set[Date]]"       => q"jsonOptSetDate(sb, $v, $level)"
        case "Option[Set[UUID]]"       => q"jsonOptSet(sb, $v, $level)"
        case "Option[Set[URI]]"        => q"jsonOptSet(sb, $v, $level)"
        case "Option[Set[BigInt]]"     => q"jsonOptSet(sb, $v, $level)"
        case "Option[Set[BigDecimal]]" => q"jsonOptSet(sb, $v, $level)"

        case "Map[String, String]"     => q"jsonMapQuoted(sb, $v, $level)"
        case "Map[String, Int]"        => q"jsonMap(sb, $v, $level)"
        case "Map[String, Long]"       => q"jsonMap(sb, $v, $level)"
        case "Map[String, Double]"     => q"jsonMap(sb, $v, $level)"
        case "Map[String, Boolean]"    => q"jsonMap(sb, $v, $level)"
        case "Map[String, Date]"       => q"jsonMapDate(sb, $v, $level)"
        case "Map[String, UUID]"       => q"jsonMap(sb, $v, $level)"
        case "Map[String, URI]"        => q"jsonMap(sb, $v, $level)"
        case "Map[String, BigInt]"     => q"jsonMap(sb, $v, $level)"
        case "Map[String, BigDecimal]" => q"jsonMap(sb, $v, $level)"

        case "Option[Map[String, String]]"     => q"jsonOptMapQuoted(sb, $v, $level)"
        case "Option[Map[String, Int]]"        => q"jsonOptMap(sb, $v, $level)"
        case "Option[Map[String, Long]]"       => q"jsonOptMap(sb, $v, $level)"
        case "Option[Map[String, Double]]"     => q"jsonOptMap(sb, $v, $level)"
        case "Option[Map[String, Boolean]]"    => q"jsonOptMap(sb, $v, $level)"
        case "Option[Map[String, Date]]"       => q"jsonOptMapDate(sb, $v, $level)"
        case "Option[Map[String, UUID]]"       => q"jsonOptMap(sb, $v, $level)"
        case "Option[Map[String, URI]]"        => q"jsonOptMap(sb, $v, $level)"
        case "Option[Map[String, BigInt]]"     => q"jsonOptMap(sb, $v, $level)"
        case "Option[Map[String, BigDecimal]]" => q"jsonOptMap(sb, $v, $level)"
      }
    }

    val tree = if (hasSameNss(obj)) {
      q"""throw MoleculeException(
            "Please compose multiple same-name namespaces with `++` instead of `+` to access field values."
          )"""
    } else {
      q"{ ..${properties(obj.props)} }"
    }
    (tree, i)
  }
}
