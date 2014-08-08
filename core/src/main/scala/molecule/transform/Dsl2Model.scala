package molecule
package transform
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait Dsl2Model[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = debug("Dsl2Model", 1, 10, true)

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

    case t@q"$prev.$manyRef[..$types]($subModel)" =>
      val refAttr: String = manyRef.toString match {
        // Name of `*` method's first parameter
        case "$times"   => t.symbol.asMethod.paramLists.head.head.name.toString
        case methodName => methodName.toString
      }
      val typeArgs = t.tpe.baseType(weakTypeOf[Ref[_, _]].typeSymbol).typeArgs
      val refNs = firstLow(typeArgs.tail.head.typeSymbol.name.toString)
      traverse(q"$prev", Group(Bond(prev.name, refAttr, refNs), resolve(q"$subModel").elements))
  }

  def atomOp(attr: Tree, op: Tree, values0: Tree) = {
    val values = getValues(attr, values0)
    val modelValue = op.toString() match {
      case "apply"    => values match {
        case Fn(name)     => Fn(name)
        case vs: Seq[_]   => Eq(vs)
        case qm: ?.type   => Qm
        case qmr: ?!.type => QmR
      }
      case "$less"    => values match {
        case qm: ?.type => Lt(Qm)
        case vs: Seq[_] => Lt(vs.head)
      }
      case "contains" => values match {
        case qm: ?.type => Fulltext(Seq(Qm))
        case vs: Seq[_] => Fulltext(vs)
      }
      case unexpected => abort(s"[Dsl2Model:operation] Unknown operator '$unexpected'\nattr: $attr \nvalue: $values0")
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

  val pkg   = q"molecule.this.`package`"
  val ident = "__ident__"

  def getValues(attr: Tree, values: Tree): Any = values match {
    case q"Seq($pkg.?)"     => ?
    case q"Seq($pkg.?!)"    => ?!
    case q"Seq($pkg.count)" => Fn("count")
    case q"Seq(..$vs)"      => vs.flatMap(v => resolveValues(v, att(q"$attr")))
    case v                  => resolveValues(v, att(q"$attr"))
  }

  def extract(t: Tree) = t match {
    case Constant(v: String)          => v
    case Literal(Constant(v: String)) => v
    case Ident(TermName(v: String))   => ident + v
    case v                            => v
  }

  def resolveValues(tree: Tree, at: att) = {
    val emptyErr = s"[Dsl2Model:resolveValues] Unexpected empty values for attribute `${at.name}`"

    def validateStaticEnums(value: Any) = {
      if (at.isEnum
        && value != "?"
        && !value.toString.startsWith(ident)
        && !at.enumValues.contains(value.toString)
      ) abort(s"[Dsl2Model:resolveValues] '$value' is not among available enum values of attribute ${at.kwS}:\n  " +
        at.enumValues.sorted.mkString("\n  "))
      value
    }
    def resolve(tree: Tree, values: Seq[Tree] = Seq()): Seq[Tree] = tree match {
      case q"$a.or($b)"             => resolve(b, resolve(a, values))
      case q"${_}.string2Model($v)" => values :+ v
      case Apply(_, vs) /* Set */   => if (vs.isEmpty) abort(emptyErr) else vs.flatMap(vs ++ resolve(_))
      case v                        => values :+ v
    }
    val values = resolve(tree) map extract map validateStaticEnums
    if (values.isEmpty) abort(emptyErr)
    values
  }
}

object Dsl2Model {
  def inst(c0: Context) = new {val c: c0.type = c0} with Dsl2Model[c0.type]
  def apply(c: Context)(model: c.Expr[NS]): Model = inst(c).resolve(model.tree)
}
