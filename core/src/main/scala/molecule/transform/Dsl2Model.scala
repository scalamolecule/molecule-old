package molecule.transform
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait Dsl2Model[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = debug("Dsl2Model", 1, 10, true)

  def resolve(tree: Tree): Model = modelTreePF.applyOrElse(
    tree, (t: Tree) => abort(s"[Dsl2Model:resolve] Unexpected tree: $t\nRAW: ${showRaw(t)}"))

  val modelTreePF: PartialFunction[Tree, Model] = {
    case q"TermValue.apply($ns)"        => resolve(ns)
    case ent@q"$prev.eid"               => traverse(q"$prev", Atom(ent.ns, "eid", "Long", 1, EntValue))
    case t@q"$prev.$cur.$op(..$values)" => traverse(q"$prev", operation(q"$prev.$cur", q"$op", q"Seq(..$values)"))

    case ref@q"$prev.$refAttr" if ref.isRef && refAttr.toString.head.isUpper =>
      val typeArgs = ref.tpe.baseType(weakTypeOf[Ref[_, _]].typeSymbol).typeArgs
      val ns = firstLow(typeArgs.head.typeSymbol.name.toString)
      val refAttr = att(ref).toString
      val refNsRaw = firstLow(typeArgs.tail.head.typeSymbol.name.toString)
      val refNs = if (refNsRaw == refAttr) "" else refNsRaw
      traverse(q"$prev", Bond(ns, refAttr, refNs))

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

  def traverse(prev: Tree, element: Element): Model =
    if (prev.isAttr || prev.symbol.isMethod)
      Model(resolve(prev).elements :+ element)
    else
      Model(Seq(element))

  def operation(attr: Tree, op: Tree, valueTree: Tree) = {
    val values = getValues(attr, valueTree)
    val enumPrefix = if (attr.isEnum) Some(attr.at.enumPrefix) else None
    if (values.isEmpty) abort(s"[Dsl2Model:operation] Unexpected empty values for attribute `${attr.name}`")
    val value = op.toString() match {
      case "apply"           => Eq(values)
      case "$less" /* `<` */ => Lt(values.head)
      case "contains"        => Fulltext(values)
      case z                 => abort(s"[Dsl2Model:operation] Unknown operator '$z'\nattr: $attr \nvalue: $valueTree")
    }
    Atom(attr.ns, attr.name, attr.tpeS, attr.card, value, enumPrefix)
  }

  def atom(attr: Tree, value: Value = VarValue): Atom = attr match {
    case a if a.isEnum && value == VarValue => Atom(a.ns, a.name, a.tpeS, a.card, EnumVal, Some(a.enumPrefix))
    case a if a.isValueAttr                 => Atom(a.ns, a.name, a.tpeS, a.card, value)
    case a if a.isOneRef                    => Atom(a.ns, a.name, "Long", 1, value)
    case unknown                            => abortTree(unknown, "[Dsl2Model:atom] Unknown atom type")
  }

  // Values --------------------------------------------------------------------------

  def getValues(attr: Tree, value0: Tree): Seq[Any] = {
    value0 match {
      case q"Seq(?)"                          => Seq("?")
      case q"Seq(?!)"                         => Seq("?!")
      case q"Seq(molecule.this.`package`.?)"  => Seq("?")
      case q"Seq(molecule.this.`package`.?!)" => Seq("?!")
      case q"Seq(..$values)"                  => values.flatMap(v => resolveValues(v, att(q"$attr")))
      case value                              => resolveValues(value, att(q"$attr"))
    }
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
      ) abort(s"[Dsl2Model:resolveValues] '$value' is not among available enum values of attribute ${at.kwS}:\n  " +
        at.enumValues.sorted.mkString("\n  "))
      value
    }
    def resolve(tree: Tree, vs: Seq[Tree] = Seq()): Seq[Tree] = tree match {
      case q"$a.or($b)"               => resolve(b, resolve(a, vs))
      case q"${_}.string2Model($v)"   => vs :+ v
      case Apply(_, values) /* Set */ => values.flatMap(vs ++ resolve(_))
      case v                          => vs :+ v
    }
    resolve(tree) map extract map validateStaticEnums
  }
}

object Dsl2Model {
  def inst(c0: Context) = new {val c: c0.type = c0} with Dsl2Model[c0.type]
  def apply(c: Context)(model: c.Expr[NS]): Model = inst(c).resolve(model.tree)
}
