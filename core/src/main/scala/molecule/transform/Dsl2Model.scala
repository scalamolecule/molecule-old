package molecule
package transform
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait Dsl2Model[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = Debug("Dsl2Model", 1, 10, false)

  def resolve(tree: Tree): Model = dslStructure.applyOrElse(
    tree, (t: Tree) => abort(s"[Dsl2Model:resolve] Unexpected tree: $t\nRAW: ${showRaw(t)}"))

  def traverse(prev: Tree, element: Element): Model =
    if (prev.isAttr || prev.symbol.isMethod)
      Model(resolve(prev).elements :+ element)
    else
      Model(Seq(element))

  val dslStructure: PartialFunction[Tree, Model] = {
    case q"TermValue.apply($ns)" => resolve(ns)
    //        case t@q"$prev.e.$op(..$values)"  => traverse(q"$prev", atomOp(q"$prev.e", q"$op", q"Seq(..$values)"))
    case t@q"$prev.$cur.$op(..$values)" => traverse(q"$prev", atomOp(q"$prev.$cur", q"$op", q"Seq(..$values)"))

    case ref@q"$prev.$refAttr" if ref.isRef && refAttr.toString.head.isUpper =>
      val typeArgs = ref.tpe.baseType(weakTypeOf[Ref[_, _]].typeSymbol).typeArgs
      val ns = firstLow(typeArgs.head.typeSymbol.name.toString)
      val refAttr = att(ref).toString
      val refNsRaw = firstLow(typeArgs.tail.head.typeSymbol.name.toString)
      val refNs = if (refNsRaw == refAttr) "" else refNsRaw
      traverse(q"$prev", Bond(ns, refAttr, refNs))

    case e@q"$prev.e"       => traverse(q"$prev", Atom(prev.name, "e", "Long", 1, EntValue))
    case q"$prev.txInstant" => traverse(q"$prev", Atom("db", "txInstant", "Date", 1, VarValue))
    case q"$prev.txT"       => traverse(q"$prev", Atom("db", "txT", "Long", 1, VarValue))
    case q"$prev.txAdded"   => traverse(q"$prev", Atom("db", "txAdded", "Boolean", 1, VarValue))
    case attr@q"$prev.$cur" =>
//      x(4, attr, prev, cur)
      traverse(q"$prev", atom(attr))

    // Nested group (ManyRef)
    case t@q"$prev.$manyRef.apply[..$types]($nestedMolecule)" =>
      val nestedModel = resolve(q"$nestedMolecule")
      val refNs = curNs(nestedModel.elements.head)
//      val group = Group(Bond(prev.name, firstLow(manyRef.toString), refNs.capitalize), nestedModel.elements.reverse)
      val group = Group(Bond(prev.name, firstLow(manyRef.toString), refNs.capitalize), nestedModel.elements)
//      x(2, t, nestedModel, group, traverse(q"$prev", group))
      traverse(q"$prev", group)
  }

  def atomOp(attr: Tree, op: Tree, values0: Tree) = {
    def errValue(v: Any) = abort(s"[Dsl2Model:atomOp] Unexpected resolved value for `${attr.name}.$op`: $v")

    val values = getValues(attr, values0)
    //    x(1, attr, op, values0, values)

    val modelValue = op.toString() match {
      case "apply"    => values match {
        case resolved: Value => resolved
        case vs: Seq[_]      => if (vs.isEmpty) Remove(Seq()) else Eq(vs)
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
      case "remove"   => values match {
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
    case a if a.isOneRefAttr                => Atom(a.ns, a.name, "Long", 1, value)
    //    case a if a.isOneRef                    => Atom(a.ns, a.name, "Long", 1, value)
    //    case a if a.isManyRef                    => Atom(a.ns, a.name, "Long", 1, value)
    case unknown => abortTree(unknown, "[Dsl2Model:atom] Unknown atom type")
  }


  // Values --------------------------------------------------------------------------

  def getValues(attr: Tree, values: Tree): Any = values match {
    case q"Seq($pkg.?)"     => Qm
    case q"Seq($pkg.count)" => Fn("count")
    case q"Seq(..$vs)"      =>
      //      if (vs.isEmpty) abort(s"[Dsl2Model:getValues] Unexpected empty values for attribute `${attr.name}`")
      vs match {
        case get if get.nonEmpty && get.head.tpe <:< weakTypeOf[(_, _)] =>
          val oldNew: Map[Any, Any] = get.map {
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
