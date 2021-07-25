package molecule.core.macros.build.json

import molecule.core.macros.attrResolvers.JsonBase
import molecule.core.macros.build.BuildBase
import scala.reflect.macros.blackbox

trait BuildJson extends BuildBase with JsonBase {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildJson", 10)

  def jsonFlat(obj: BuilderObj, colIndex0: Int = -1, level: Int = 0): (Tree, Int) = {
    var colIndex    = colIndex0
    val tabs        = level + 1
    val newLineCode = Seq(
      q"""sb.append(${"," + indent(tabs)})"""
    )

    def properties(nodes: List[BuilderNode]): Seq[Tree] = {
      var next  = false
      var props = List.empty[String]
      nodes.flatMap { node =>
        val newLine = if (next) newLineCode else {
          next = true
          Nil
        }
        val trees   = node match {
          case BuilderProp(_, prop, _, _, json, _) =>
            colIndex += 1
            // Only generate 1 property, even if attribute is repeated in molecule
            if (props.contains(prop)) Nil else {
              props = props :+ prop
              newLine :+ json(colIndex, tabs)
            }

          case refObj@BuilderObj(_, ref, _, _) =>
            val (subObj, colIndexSub) = jsonFlat(refObj, colIndex, level + 1)
            colIndex = colIndexSub
            newLine ++ Seq(
              q"""quote(sb, $ref)""",
              q"""sb.append(${": {" + indent(tabs + 1)})""",
              q"""$subObj""",
              q"""sb.append(${indent(tabs) + "}"})""",
            )
        }
        trees
      }
    }
    //    def valueFromTpl(i: Int, tpe: Tree, level: Int): Tree = {
    //      val v = q"""tpl.productElement($i).asInstanceOf[$tpe]"""
    //      tpe.toString match {
    //        case "String"     => q"quote(sb, $v)"
    //        case "Int"        => q"sb.append($v)"
    //        case "Long"       => q"sb.append($v)"
    //        case "Double"     => q"sb.append($v)"
    //        case "Boolean"    => q"sb.append($v)"
    //        case "Date"       => q"quote(sb, date2str($v))"
    //        case "UUID"       => q"quote(sb, $v)"
    //        case "URI"        => q"quote(sb, $v)"
    //        case "BigInt"     => q"quote(sb, $v)"
    //        case "BigDecimal" => q"quote(sb, $v)"
    //        case "Any"        => q"jsonAnyValue(sb, $v)"
    //
    //        case "Option[String]"     => q"jsonOptOneQuoted(sb, $v)"
    //        case "Option[Int]"        => q"jsonOptOne(sb, $v)"
    //        case "Option[Long]"       => q"jsonOptOne(sb, $v)"
    //        case "Option[Double]"     => q"jsonOptOne(sb, $v)"
    //        case "Option[Boolean]"    => q"jsonOptOne(sb, $v)"
    //        case "Option[Date]"       => q"jsonOptOneDate(sb, $v)"
    //        case "Option[UUID]"       => q"jsonOptOne(sb, $v)"
    //        case "Option[URI]"        => q"jsonOptOne(sb, $v)"
    //        case "Option[BigInt]"     => q"jsonOptOne(sb, $v)"
    //        case "Option[BigDecimal]" => q"jsonOptOne(sb, $v)"
    //
    //        case "Set[String]"     => q"jsonSetQuoted(sb, $v, $level)"
    //        case "Set[Int]"        => q"jsonSet(sb, $v, $level)"
    //        case "Set[Long]"       => q"jsonSet(sb, $v, $level)"
    //        case "Set[Double]"     => q"jsonSet(sb, $v, $level)"
    //        case "Set[Boolean]"    => q"jsonSet(sb, $v, $level)"
    //        case "Set[Date]"       => q"jsonSetDate(sb, $v, $level)"
    //        case "Set[UUID]"       => q"jsonSet(sb, $v, $level)"
    //        case "Set[URI]"        => q"jsonSet(sb, $v, $level)"
    //        case "Set[BigInt]"     => q"jsonSet(sb, $v, $level)"
    //        case "Set[BigDecimal]" => q"jsonSet(sb, $v, $level)"
    //
    //        case "Option[Set[String]]"     => q"jsonOptSetQuoted(sb, $v, $level)"
    //        case "Option[Set[Int]]"        => q"jsonOptSet(sb, $v, $level)"
    //        case "Option[Set[Long]]"       => q"jsonOptSet(sb, $v, $level)"
    //        case "Option[Set[Double]]"     => q"jsonOptSet(sb, $v, $level)"
    //        case "Option[Set[Boolean]]"    => q"jsonOptSet(sb, $v, $level)"
    //        case "Option[Set[Date]]"       => q"jsonOptSetDate(sb, $v, $level)"
    //        case "Option[Set[UUID]]"       => q"jsonOptSet(sb, $v, $level)"
    //        case "Option[Set[URI]]"        => q"jsonOptSet(sb, $v, $level)"
    //        case "Option[Set[BigInt]]"     => q"jsonOptSet(sb, $v, $level)"
    //        case "Option[Set[BigDecimal]]" => q"jsonOptSet(sb, $v, $level)"
    //
    //        case "Map[String, String]"     => q"jsonMapQuoted(sb, $v, $level)"
    //        case "Map[String, Int]"        => q"jsonMap(sb, $v, $level)"
    //        case "Map[String, Long]"       => q"jsonMap(sb, $v, $level)"
    //        case "Map[String, Double]"     => q"jsonMap(sb, $v, $level)"
    //        case "Map[String, Boolean]"    => q"jsonMap(sb, $v, $level)"
    //        case "Map[String, Date]"       => q"jsonMapDate(sb, $v, $level)"
    //        case "Map[String, UUID]"       => q"jsonMap(sb, $v, $level)"
    //        case "Map[String, URI]"        => q"jsonMap(sb, $v, $level)"
    //        case "Map[String, BigInt]"     => q"jsonMap(sb, $v, $level)"
    //        case "Map[String, BigDecimal]" => q"jsonMap(sb, $v, $level)"
    //
    //        case "Option[Map[String, String]]"     => q"jsonOptMapQuoted(sb, $v, $level)"
    //        case "Option[Map[String, Int]]"        => q"jsonOptMap(sb, $v, $level)"
    //        case "Option[Map[String, Long]]"       => q"jsonOptMap(sb, $v, $level)"
    //        case "Option[Map[String, Double]]"     => q"jsonOptMap(sb, $v, $level)"
    //        case "Option[Map[String, Boolean]]"    => q"jsonOptMap(sb, $v, $level)"
    //        case "Option[Map[String, Date]]"       => q"jsonOptMapDate(sb, $v, $level)"
    //        case "Option[Map[String, UUID]]"       => q"jsonOptMap(sb, $v, $level)"
    //        case "Option[Map[String, URI]]"        => q"jsonOptMap(sb, $v, $level)"
    //        case "Option[Map[String, BigInt]]"     => q"jsonOptMap(sb, $v, $level)"
    //        case "Option[Map[String, BigDecimal]]" => q"jsonOptMap(sb, $v, $level)"
    //      }
    //    }
    val tree = if (hasSameNss(obj)) {
      q"""throw MoleculeException(
            "Please compose multiple same-name namespaces with `++` instead of `+` to access property values."
          )"""
    } else {
      q"{ ..${properties(obj.props)} }"
    }

    xx(1, obj, tree)
    (tree, colIndex)
  }
}
