package molecule
package transform
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait Dsl2Model[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = debug("Dsl2Model", 1, 10, false)

  def resolve(tree: Tree): Model = dslStructure.applyOrElse(
    tree, (t: Tree) => abort(s"[Dsl2Model:resolve] Unexpected tree: $t\nRAW: ${showRaw(t)}"))

  def traverse(prev: Tree, element: Element): Model =
    if (prev.isAttr || prev.symbol.isMethod)
      Model(resolve(prev).elements :+ element)
    else
      Model(Seq(element))

  val dslStructure: PartialFunction[Tree, Model] = {
    case q"TermValue.apply($ns)" => resolve(ns)
    //        case t@q"$prev.eid.$op(..$values)"  => traverse(q"$prev", atomOp(q"$prev.eid", q"$op", q"Seq(..$values)"))
    case t@q"$prev.$cur.$op(..$values)" => traverse(q"$prev", atomOp(q"$prev.$cur", q"$op", q"Seq(..$values)"))

    case ref@q"$prev.$refAttr" if ref.isRef && refAttr.toString.head.isUpper =>
      val typeArgs = ref.tpe.baseType(weakTypeOf[Ref[_, _]].typeSymbol).typeArgs
      val ns = firstLow(typeArgs.head.typeSymbol.name.toString)
      val refAttr = att(ref).toString
      val refNsRaw = firstLow(typeArgs.tail.head.typeSymbol.name.toString)
      val refNs = if (refNsRaw == refAttr) "" else refNsRaw
      traverse(q"$prev", Bond(ns, refAttr, refNs))

    case eid@q"$prev.eid"   => traverse(q"$prev", Atom(eid.ns, "eid", "Long", 1, EntValue))
    case attr@q"$prev.$cur" => traverse(q"$prev", atom(attr))

//    case t@q"$prev.$manyRef[..$types]($subModel)" =>
    case t@q"$prev.$manyRef[..$types]($subModel)" =>
//      val refAttr: String = manyRef.toString match {
//        // Name of `*` method's first parameter
//        case "$times"   => t.symbol.asMethod.paramLists.head.head.name.toString
//        case methodName => methodName.toString
//      }
      val typeArgs = t.tpe.baseType(weakTypeOf[Ref[_, _]].typeSymbol).typeArgs
      val refNs = firstLow(typeArgs.tail.head.typeSymbol.name.toString)
      traverse(q"$prev", Group(Bond(prev.name.toString, manyRef.toString, refNs), resolve(q"$subModel").elements))
  }

  def atomOp(attr: Tree, op: Tree, values0: Tree) = {
    def errValue(v: Any) = abort(s"[Dsl2Model:atomOp] Unexpected resolved value for `${attr.name}.$op`: $v")
    //    x(1, values0, getValues(attr, values0))

    val values = getValues(attr, values0)
    val modelValue = op.toString() match {
      case "apply"    => values match {
        case resolved: Value => resolved
        case vs: Seq[_]      => if(vs.isEmpty) Remove(Seq()) else Eq(vs)
        case other           => errValue(other)
      }
      case "$less"    => values match {
        case qm: Qm.type => Lt(Qm)
        case vs: Seq[_]  => Lt(vs.head)
      }
      case "contains" => values match {
        case qm: Qm.type => Fulltext(Seq(Qm))
        case vs: Seq[_]  => Fulltext(vs)
      }
      case "add"      => values match {
        case vs: Seq[_] => Eq(vs)
      }
      case "remove"      => values match {
        case vs: Seq[_] => Remove(vs)
      }
      case unexpected => abort(s"[Dsl2Model:atomOp] Unknown operator '$unexpected'\nattr: $attr \nvalue: $values0")
    }
    val enumPrefix = if (attr.isEnum) Some(attr.at.enumPrefix) else None
    Atom(attr.ns, attr.name, attr.tpeS, attr.card, modelValue, enumPrefix)
  }

  def atom(attr: Tree, value: Value = VarValue): Atom = attr match {
    case a if a.isEnum && value == VarValue => Atom(a.ns, a.name, a.tpeS, a.card, EnumVal, Some(a.enumPrefix))
    case a if a.isValueAttr                 => Atom(a.ns, a.name, a.tpeS, a.card, value)
    case a if a.isOneRef                    => Atom(a.ns, a.name, "Long", 1, value)
    case unknown                            => abortTree(unknown, "[Dsl2Model:atom] Unknown atom type")
  }


  // Values --------------------------------------------------------------------------

  def getValues(attr: Tree, values: Tree): Any = values match {
    case q"Seq($pkg.?)"     => Qm
    case q"Seq($pkg.?!)"    => QmR
    case q"Seq($pkg.count)" => Fn("count")
    case q"Seq(..$vs)"      =>
//      if (vs.isEmpty) abort(s"[Dsl2Model:getValues] Unexpected empty values for attribute `${attr.name}`")
      vs match {
        case tpls if tpls.nonEmpty && tpls.head.tpe <:< weakTypeOf[Tuple2[_, _]] =>
          val oldNew: Map[Any, Any] = tpls.map {
            case q"scala.this.Predef.ArrowAssoc[$t1]($k).->[$t2]($v)" => (extract(k), extract(v))
          }.toMap
          Replace(oldNew)

        case other => vs.flatMap(v => resolveValues(v, att(q"$attr")))
      }
    case v                  => resolveValues(v, att(q"$attr"))
  }

  def extract(t: Tree) = t match {
    case Constant(v: String)          => v
    case Literal(Constant(v: String)) => v
    case Ident(TermName(v: String))   => "__ident__" + v
    case v                            => v
  }

  def resolveValues(tree: Tree, at: att) = {
    def validateStaticEnums(value: Any) = {
      if (at.isEnum
        && value != "?"
        && !value.toString.startsWith("__ident__")
        && !at.enumValues.contains(value.toString)
      ) abort(s"[Dsl2Model:validateStaticEnums] '$value' is not among available enum values of attribute ${at.kwS}:\n  " +
        at.enumValues.sorted.mkString("\n  "))
      value
    }
    def resolve(tree: Tree, values: Seq[Tree] = Seq()): Seq[Tree] = tree match {
      case q"$a.or($b)"             => resolve(b, resolve(a, values))
      case q"${_}.string2Model($v)" => values :+ v
      case Apply(_, vs)             => values ++ vs.flatMap(resolve(_))
      case v                        => values :+ v
    }
    val values = resolve(tree) map extract map validateStaticEnums
    if (values.isEmpty) abort(s"[Dsl2Model:resolveValues] Unexpected empty values for attribute `${at.name}`")
    values
  }
}

object Dsl2Model {
  def inst(c0: Context) = new {val c: c0.type = c0} with Dsl2Model[c0.type]
  def apply(c: Context)(model: c.Expr[NS]): Model = inst(c).resolve(model.tree)
}
