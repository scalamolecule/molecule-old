package molecule
package transform
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait Dsl2Model[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = Debug("Dsl2Model", 10, 30, false)

  def resolve(tree: Tree): Seq[Element] = dslStructure.applyOrElse(
    tree, (t: Tree) => abort(s"[Dsl2Model:resolve] Unexpected tree: $t\nRAW: ${showRaw(t)}"))

  def traverse(prev: Tree, element: Element): Seq[Element] =
    if (prev.isAttr || prev.symbol.isMethod) resolve(prev) :+ element else Seq(element)

  def kw(kwTree: Tree): (String, String) = (kwTree.toString: String) match {
    case r""""\:(\w*)$ns0/(\w*)$attr0"""" => (ns0, attr0)
    case otherKw                          => abort("[Dsl2Model:kw] Unrecognized attribute keyword: " + otherKw)
  }

  val dslStructure: PartialFunction[Tree, Seq[Element]] = {
    case q"TermValue.apply($ns)" => resolve(ns)

    // Namespace(eid).attr1...
    case q"$prev.$ns.apply($eid)" if ns.toString.head.isUpper => traverse(q"$prev", Meta(firstLow(ns), "", "e", Eq(Seq(extract(eid)))))


    // Functions ---------------------------

    case q"$prev.$cur.length.apply(..$values)" => traverse(q"$prev", resolveOp(q"$prev", q"$cur", q"$prev.$cur", q"count", q"Seq(..$values)"))
    case q"$prev.$cur.length"                  => traverse(q"$prev", resolveOp(q"$prev", q"$cur", q"$prev.$cur", q"count", q"Seq()"))


    // EAV -----------------------------

    case q"$prev.e" => traverse(q"$prev", Meta("", "", "e", EntValue))

    //    case q"$prev.a.apply(..$values)" => traverse(q"$prev", Atom("?", "attr", "String", 1, getAppliedValue(q"$prev.a", q"Seq(..$values)")))
    case q"$prev.a" => traverse(q"$prev", Atom("?", "attr", "String", 1, NoValue))

    // Only allow `v` to attach to a generic attribute `a`
    case q"$prev.a.v.apply(..$values)"  => traverse(q"$prev", Atom("?", "attr", "String", 1, modelValue("apply", null, q"Seq(..$values)"), None, List(AttrVar(""))))
    case q"$prev.a.v_.apply(..$values)" => traverse(q"$prev", Atom("?", "attr", "String", 1, modelValue("apply", null, q"Seq(..$values)"), None, List(NoValue)))
    case q"$prev.a.v"                   => traverse(q"$prev", Atom("?", "attr", "String", 1, VarValue, None, List(AttrVar(""))))
    case q"$prev.a.v_"                  => traverse(q"$prev", Atom("?", "attr", "String", 1, NoValue, None, List(NoValue)))
    case q"$prev.$other.v"              => abort(s"[Dsl2Model:dslStructure] `v` is only allowed right after a generic `a` attribute")
    case q"$prev.$other.v_"             => abort(s"[Dsl2Model:dslStructure] `v_` is only allowed right after a generic `a` attribute")


    // Tx ----------------------------------

    // Db.txInstant etc.. (all database transactions)
    case q"$prev.Db.tx"        => traverse(q"$prev", Atom("db", "tx", "Long", 1, VarValue))
    case q"$prev.Db.txT"       => traverse(q"$prev", Atom("db", "txT", "Long", 1, VarValue))
    case q"$prev.Db.txInstant" => traverse(q"$prev", Atom("db", "txInstant", "Long", 1, VarValue))
    case q"$prev.Db.op"        => traverse(q"$prev", Atom("db", "op", "Boolean", 1, VarValue))

    case q"$prev.tx[..$t]($txMolecule)" => traverse(q"$prev", TxModel(resolve(q"$txMolecule")))

    // ns.txInstant.attr - `txInstant` doesn't relate to any previous attr
    case q"$prev.tx" if !q"$prev".isAttr        => abort(s"[Dsl2Model:dslStructure] Please add `tx` after an attribute or another transaction value")
    case q"$prev.txT" if !q"$prev".isAttr       => abort(s"[Dsl2Model:dslStructure] Please add `txT` after an attribute or another transaction value")
    case q"$prev.txInstant" if !q"$prev".isAttr => abort(s"[Dsl2Model:dslStructure] Please add `txInstant` after an attribute or another transaction value")
    case q"$prev.op" if !q"$prev".isAttr        => abort(s"[Dsl2Model:dslStructure] Please add `op` after an attribute or another transaction value")

    // ns.attr.txInstant etc.. (transaction related to previous attribute)
    case q"$prev.tx"        => traverse(q"$prev", Meta("db", "tx", "tx", TxValue))
    case q"$prev.txT"       => traverse(q"$prev", Meta("db", "txT", "tx", TxTValue))
    case q"$prev.txInstant" => traverse(q"$prev", Meta("db", "txInstant", "tx", TxInstantValue))
    case q"$prev.op"        => traverse(q"$prev", Meta("db", "op", "tx", OpValue))


    // Generic -----------------------------

    case q"$prev.$ref.apply(..$values)" if q"$prev.$ref".isRef => abort(s"[Dsl2Model:dslStructure] Can't apply value to a reference (`$ref`)")

    case q"$prev.$cur.$op(..$values)"   => traverse(q"$prev", resolveOp(q"$prev", q"$cur", q"$prev.$cur", q"$op", q"Seq(..$values)"))
    case t@q"$prev.$refAttr" if t.isRef => traverse(q"$prev", Bond(t.refThis, t.name, t.refNext))

    case a@q"$prev.$cur" if a.isEnum               => traverse(q"$prev", Atom(a.ns, a.name, a.tpeS, a.card, EnumVal, Some(a.enumPrefix)))
    case a@q"$prev.$cur" if a.isValueAttr          => traverse(q"$prev", Atom(a.ns, a.name, a.tpeS, a.card, VarValue))
    case a@q"$prev.$cur" if a.isRef || a.isRefAttr => traverse(q"$prev", Atom(a.ns, a.name, "Long", a.card, VarValue))


    // Nested group (ManyRef) --------------

    case t@q"$prev.$manyRef.apply[..$types]($nestedMolecule)" =>
      val nestedElements = resolve(q"$nestedMolecule")
      val refNs = curNs(nestedElements.head)
      val group = Group(Bond(prev.name, firstLow(manyRef), refNs.capitalize), nestedElements)
      //      x(2, t, nestedModel, group, traverse(q"$prev", group))
      traverse(q"$prev", group)

    case other => abort(s"[Dsl2Model:dslStructure] Unexpected DSL structure: $other")
  }

  def resolveOp(previous: Tree, curTree: Tree, attr: Tree, op: Tree, values0: Tree): Element = {
    val value: Value = modelValue(op.toString(), attr, values0)
    val enumPrefix = if (attr.isEnum) Some(attr.at.enumPrefix) else None
    val cur = curTree.toString
    previous match {
      case prev if cur.head.isUpper          => x(1, prev, cur, curTree, value)
      case prev if cur == "e" && prev.isRef  => x(2, prev, op, cur, curTree, value)
      case prev if cur == "e" && prev.isAttr => x(3, prev, curTree)
      case prev if cur == "e"                => x(4, prev, op, cur, curTree)
      case prev if cur == "a"                => x(5, prev, op, cur, curTree)
      case prev if attr.isAttr               => x(6, prev, curTree, value)
      case prev if prev.isAttr               => x(7, prev, curTree, value)
      case prev                              => x(8, prev, curTree, value)
    }
    previous match {
      case prev if cur.head.isUpper          => Atom(attr.name, cur, attr.tpeS, attr.card, value, enumPrefix)
      case prev if cur == "e" && prev.isRef  => Meta(prev.name, prev.refNext, "e", value)
      case prev if cur == "e" && prev.isAttr => Atom(prev.ns, cur, attr.tpeS, attr.card, value, enumPrefix)
      case prev if cur == "e"                => Meta(prev.name, cur, "e", value)
      case prev if cur == "a"                => Atom("?", "attr", "String", 1, value)
      case prev if attr.isAttr               => Atom(attr.ns, attr.name, attr.tpeS, attr.card, value, enumPrefix)
      case prev if prev.isAttr               => Atom(prev.ns, attr.name, attr.tpeS, attr.card, value, enumPrefix)
      case prev                              => Atom(attr.name, cur, "Int", attr.card, value, enumPrefix)
    }
  }


  // Values ================================================================================

  def modelValue(op: String, attr: Tree, values0: Tree) = {
    def errValue(v: Any) = abort(s"[Dsl2Model:modelValue] Unexpected resolved model value for `${attr.name}.$op`: $v")
    val values = getValues(values0, attr)
    op match {
      case "apply"    => values match {
        case resolved: Value => resolved
        case vs: Seq[_]      => if (vs.isEmpty) Remove(Seq()) else Eq(vs)
        case other           => errValue(other)
      }
      case "count"    => values match {case Fn("avg", i) => Length(Some(Fn("avg", i))); case other => errValue(other)}
      case "$less"    => values match {case qm: Qm.type => Lt(Qm); case vs: Seq[_] => Lt(vs.head)}
      case "contains" => values match {case qm: Qm.type => Fulltext(Seq(Qm)); case vs: Seq[_] => Fulltext(vs)}
      case "add"      => values match {case vs: Seq[_] => Eq(vs)}
      case "remove"   => values match {case vs: Seq[_] => Remove(vs)}
      case unexpected => abort(s"[Dsl2Model:atomOp] Unknown operator '$unexpected'\nattr: $attr \nvalue: $values0")
    }
  }

  def getAppliedValue(attr: Tree, values0: Tree): Value = getValues(values0, attr) match {
    case resolved: Value => resolved
    case vs: Seq[_]      => if (vs.isEmpty) Remove(Seq()) else Eq(vs)
    case other           => abort(s"[Dsl2Model:getAppliedModelValue] Unexpected applied value for `${attr.name}`: $other")
  }

  def getValues(values: Tree, attr: Tree = null): Any = values match {
    case q"Seq($pkg.?)"                                          => Qm
    case q"Seq($pkg.distinct)"                                   => Distinct
    case q"Seq($pkg.max.apply(${Literal(Constant(i: Int))}))"    => Fn("max", Some(i))
    case q"Seq($pkg.min.apply(${Literal(Constant(i: Int))}))"    => Fn("min", Some(i))
    case q"Seq($pkg.rand.apply(${Literal(Constant(i: Int))}))"   => Fn("rand", Some(i))
    case q"Seq($pkg.sample.apply(${Literal(Constant(i: Int))}))" => Fn("sample", Some(i))
    case q"Seq($pkg.max)"                                        => Fn("max")
    case q"Seq($pkg.min)"                                        => Fn("min")
    case q"Seq($pkg.rand)"                                       => Fn("rand")
    case q"Seq($pkg.count)"                                      => Fn("count")
    case q"Seq($pkg.countDistinct)"                              => Fn("count-distinct")
    case q"Seq($pkg.sum)"                                        => Fn("sum")
    case q"Seq($pkg.avg)"                                        => Fn("avg")
    case q"Seq($pkg.median)"                                     => Fn("median")
    case q"Seq($pkg.variance)"                                   => Fn("variance")
    case q"Seq($pkg.stddev)"                                     => Fn("stddev")
    case q"Seq(..$vs)"                                           =>
      vs match {
        case get if get.nonEmpty && get.head.tpe <:< weakTypeOf[(_, _)] =>
          val oldNew: Map[Any, Any] = get.map {
            case q"scala.this.Predef.ArrowAssoc[$t1]($k).->[$t2]($v)" => (extract(k), extract(v))
          }.toMap
          Replace(oldNew)

        case other if attr == null => vs.flatMap(v => resolveValues(v))
        case other                 => vs.flatMap(v => resolveValues(v, att(q"$attr")))
      }

    case other if attr == null => resolveValues(other)
    case other                 => resolveValues(other, att(q"$attr"))
  }

  def extract(t: Tree) = t match {
    case Constant(v: String)                            => v
    case Literal(Constant(v: String))                   => v
    case Ident(TermName(v: String))                     => "__ident__" + v
    case Select(This(TypeName(_)), TermName(v: String)) => "__ident__" + v
    case v                                              => v
  }

  def resolveValues(tree: Tree, at: att = null) = {
    def resolve(tree0: Tree, values: Seq[Tree] = Seq()): Seq[Tree] = tree0 match {
      case q"$a.or($b)"             => resolve(b, resolve(a, values))
      case q"${_}.string2Model($v)" => values :+ v
      case Apply(_, vs)             => values ++ vs.flatMap(resolve(_))
      case v                        => values :+ v
    }
    def validateStaticEnums(value: Any) = {
      if (at.isEnum
        && value != "?"
        && !value.toString.startsWith("__ident__")
        && !at.enumValues.contains(value.toString)
      ) abort(s"[Dsl2Model:validateStaticEnums] '$value' is not among available enum values of attribute ${at.kwS}:\n  " +
        at.enumValues.sorted.mkString("\n  "))
      value
    }
    val values = if (at == null)
      resolve(tree) map extract
    else
      resolve(tree) map extract map validateStaticEnums
    if (values.isEmpty) abort(s"[Dsl2Model:resolveValues] Unexpected empty values for attribute `$at`")
    //    if (values.isEmpty) abort(s"[Dsl2Model:resolveValues] Unexpected empty values for attribute `${at.name}`")
    values
  }
}

object Dsl2Model {
  def inst(c0: Context) = new {val c: c0.type = c0} with Dsl2Model[c0.type]
  def apply(c: Context)(dsl: c.Expr[NS]): Model = {
    val rawElements = inst(c).resolve(dsl.tree)

    // Sanity check
    rawElements.collectFirst {
      case a: Atom                      => a
      case b: Bond                      => b
      case g: Group                     => g
      case m@Meta(_, "txInstant", _, _) => m
      //      case m: Meta => m
    } getOrElse
      c.abort(c.enclosingPosition, s"[Dsl2Model:apply] Molecule is empty or has only meta attributes. Please add one or more attributes.\n$rawElements")

    // Transfer generic values from Meta elements to Atoms and skip Meta elements
    val condensedElements = rawElements.foldRight(Seq[Element](), Seq[Generic]()) { case (element, (es, gs)) =>
      element match {
        case atom: Atom if gs.isEmpty  => (atom +: es, Nil)
        case atom: Atom                => (atom.copy(gs = atom.gs ++ gs) +: es, Nil)
        case Meta(_, _, _, g: Generic) => (es, g +: gs)
        case other                     => (other +: es, gs)
      }
    }._1
    val model = Model(condensedElements)
//    inst(c).x(30, dsl, rawElements, condensedElements)
    model
  }
}