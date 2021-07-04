package molecule.core.macros.build.json

import molecule.core.macros.build.BuildBase
import scala.collection.mutable.ListBuffer
import scala.reflect.macros.blackbox


trait BuildJson extends BuildBase {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildJson", 1, 900)

  def jsonFlat(obj: BuilderObj, i0: Int = -1, isOptNested: Boolean = false, level: Int = 0): (Tree, Int) = {
    // Property index of row/tuple
    var i           = if (isOptNested && i0 == -1) -1 else i0
    val tabs        = level + 1
    val newLineCode = Seq(
      q"""sb.append(",")""",
      q"""sb.append(indent($tabs))"""
    )

    def properties(nodes: List[BuilderNode]): Seq[Tree] = {
      var next       = false
      var fieldNames = List.empty[String]
      nodes.flatMap { node =>
        val newLine = if (next) newLineCode else {
          next = true
          Nil
        }
        val trees   = node match {
          case BuilderProp(_, fieldName, tpe, _, json, optAggr) =>
            i += 1
            // Only generate 1 property, even if attribute is repeated in molecule
            if (fieldNames.contains(fieldName)) Nil else {
              fieldNames = fieldNames :+ fieldName
              newLine ++ (optAggr match {
                case None                                       => Seq(json(i, tabs))
                case Some(aggrTpe) if aggrTpe == tpe.toString() => Seq(json(i, tabs))
                case Some(aggrTpe)                              => aggrErr(fieldName, aggrTpe)
              })
            }

          case o@BuilderObj(_, ref, 2, props) =>
            i += 1
            val productTpe = if (props.length == 1) {
              props.head match {
                case BuilderProp(_, _, tpe, _, _, _) => tq"Tuple1[$tpe]"
                case BuilderObj(_, _, _, _)          => tq"Tuple1[Product]"
              }
            } else {
              tq"Product"
            }
            newLine ++ Seq(
              q"""quote(sb, $ref)""",
              q"""sb.append(": {")""",
              q"""sb.append(indent(${tabs + 1}))""",
              q"""tpl.productElement($i).asInstanceOf[Seq[$productTpe]].map( tpl => ${jsonFlat(o, -1, isOptNested, level + 1)._1} )""",
              q"""sb.append(indent($tabs))""",
              q"""sb.append("}")""",
            )

          case o@BuilderObj(_, ref, _, _) =>
            val (subObj, j) = jsonFlat(o, i, isOptNested, level + 1)
            i = j
            newLine ++ Seq(
              q"""quote(sb, $ref)""",
              q"""sb.append(": {")""",
              q"""sb.append(indent(${tabs + 1}))""",
              q"""$subObj""",
              q"""sb.append(indent($tabs))""",
              q"""sb.append("}")""",
            )
        }
        trees
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
