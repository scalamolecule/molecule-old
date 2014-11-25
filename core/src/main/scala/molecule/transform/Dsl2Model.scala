package molecule
package transform
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait Dsl2Model[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = Debug("Dsl2Model", 12, 12, false)

  def resolve(tree: Tree): Model = dslStructure.applyOrElse(
    tree, (t: Tree) => abort(s"[Dsl2Model:resolve] Unexpected tree: $t\nRAW: ${showRaw(t)}"))

  def traverse(prev: Tree, element: Element): Model =
    if (prev.isAttr || prev.symbol.isMethod)
      Model(resolve(prev).elements :+ element)
    else
      Model(Seq(element))

  def traverse(prev: Tree, elements: Seq[Element]): Model = Model(elements)

  val dslStructure: PartialFunction[Tree, Model] = {
    case q"TermValue.apply($ns)" => resolve(ns)

    case q"$prev.$ref.apply(..$values)" if q"$prev.$ref".isRef =>
      abort(s"[Dsl2Model:dslStructure] Can't apply value to a reference (`$ref`)")

    case q"$prev.$ns.apply($eid)" if ns.toString.head.isUpper =>
      //      x(13, prev, ns, id, showRaw(q"$id"))
      traverse(q"$prev", Meta(firstLow(ns), "", "e", Eq(Seq(extract(eid)))))

    case q"$prev.$cur.$op(..$values)" =>
      traverse(q"$prev", atomOp(q"$prev", q"$cur", q"$prev.$cur", q"$op", q"Seq(..$values)"))

    case t@q"$prev.$refAttr" if t.isRef => //x(15, refAttr)
      traverse(q"$prev", Bond(t.refThis, t.name, t.refNext))

    case q"$prev.e"  => traverse(q"$prev", meta(prev, "e"))
    case q"$prev.a"  => traverse(q"$prev", meta(prev, "a"))
    case q"$prev.v"  => traverse(q"$prev", meta(prev, "v"))
    case q"$prev.ns" => traverse(q"$prev", meta(prev, "ns"))

    // Db.txInstant etc.. (all database transactions)
    case q"$prev.Db.tx"        => traverse(q"$prev", Atom("db", "tx", "Long", 1, VarValue))
    case q"$prev.Db.txT"       => traverse(q"$prev", Atom("db", "txT", "Long", 1, VarValue))
    case q"$prev.Db.txInstant" => traverse(q"$prev", Atom("db", "txInstant", "Long", 1, VarValue))
    case q"$prev.Db.txAdded"   => traverse(q"$prev", Atom("db", "txAdded", "Boolean", 1, VarValue))

    // ns.txInstant.attr - `txInstant` doesn't relate to any previous attr
    case q"$prev.tx" if !q"$prev".isAttr        => abort(s"[Dsl2Model:dslStructure] Please add `tx` after an attribute or another transaction value")
    case q"$prev.txT" if !q"$prev".isAttr       => abort(s"[Dsl2Model:dslStructure] Please add `txT` after an attribute or another transaction value")
    case q"$prev.txInstant" if !q"$prev".isAttr => abort(s"[Dsl2Model:dslStructure] Please add `txInstant` after an attribute or another transaction value")
    case q"$prev.txAdded" if !q"$prev".isAttr   => abort(s"[Dsl2Model:dslStructure] Please add `txAdded` after an attribute or another transaction value")

    // ns.attr.txInstant etc.. (transaction related to current attribute)
    case q"$prev.tx"        => traverse(q"$prev", Meta("db", "tx", "tx", TxValue))
    case q"$prev.txT"       => traverse(q"$prev", Meta("db", "txT", "tx", TxTValue))
    case q"$prev.txInstant" => traverse(q"$prev", Meta("db", "txInstant", "tx", TxInstantValue))
    case q"$prev.txAdded"   => traverse(q"$prev", Meta("db", "txAdded", "tx", TxAddedValue))

    case a@q"$prev.$cur" if a.isEnum               => traverse(q"$prev", Atom(a.ns, a.name, a.tpeS, a.card, EnumVal, Some(a.enumPrefix)))
    case a@q"$prev.$cur" if a.isValueAttr          => traverse(q"$prev", Atom(a.ns, a.name, a.tpeS, a.card, VarValue))
    case a@q"$prev.$cur" if a.isRef || a.isRefAttr => traverse(q"$prev", Atom(a.ns, a.name, "Long", a.card, VarValue))

    // Nested group (ManyRef)
    case t@q"$prev.$manyRef.apply[..$types]($nestedMolecule)" =>
      val nestedModel = resolve(q"$nestedMolecule")
      val refNs = curNs(nestedModel.elements.head)
      val group = Group(Bond(prev.name, firstLow(manyRef), refNs.capitalize), nestedModel.elements)
      //      x(2, t, nestedModel, group, traverse(q"$prev", group))
      traverse(q"$prev", group)

    case other => abort(s"[Dsl2Model:dslStructure] Unexpected dslStructure: $other")
  }

  def meta(prev: Tree, kind: String) = {
    // Try to find previous non-meta element
    val (ns1, attr1, value1) = traverse(q"$prev", EmptyElement).elements.collectFirst {
      case Atom(ns, attr, _, _, value, _, _) => (ns, attr, value)
      case Bond(ns, attr, _)                 => (ns, attr, "")
      case Group(Bond(ns, attr, _), _)       => (ns, attr, "")
    } getOrElse(prev.name, "", "")

    val meta1 = kind match {
      case "e"  => Meta(ns1, attr1, "e", EntValue)
      case "a"  => Meta(ns1, attr1, "a", EntValue)
      case "v"  => Meta(ns1, attr1, "v", EntValue)
      case "ns" => Meta(ns1, attr1, "ns", EntValue)
    }
    x(10, prev, kind, ns1, attr1, value1, meta1)
    meta1
  }

  def atomOp(previous: Tree, curTree: Tree, attr: Tree, op: Tree, values0: Tree) = {
    def errValue(v: Any) = abort(s"[Dsl2Model:atomOp] Unexpected resolved value for `${attr.name}.$op`: $v")


    val values = getValues(attr, values0)
    x(11, curTree, attr, op, values0, values)

    val modelValue: Value = op.toString() match {
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

    x(120
      , previous
      , curTree
      , op
      //    , attr
      //      , attr.isAttr
      //      , previous.isAttr
      //    , prev.toString()
      //    , prev.tpe
      , previous.name
      , previous.ns
      //    , prev.tpe.toString
      , attr.ns
      , attr.at.attrType
      , attr.at.ns
      , attr.at.ns.toString
    )
    val cur = curTree.toString
    previous match {
      case prev if cur.head.isUpper          => x(1, prev, cur, curTree, modelValue); Atom(attr.name, cur, attr.tpeS, attr.card, modelValue, enumPrefix)
      case prev if cur == "e" && prev.isRef  => x(2, prev, op, cur, curTree, modelValue); Meta(prev.name, prev.refNext, "e", modelValue)
      case prev if cur == "e" && prev.isAttr => x(3, prev, curTree); Atom(prev.ns, cur, attr.tpeS, attr.card, modelValue, enumPrefix)
      case prev if cur == "e"                => x(4, prev, op, cur, curTree); Atom(prev.name, cur, attr.tpeS, attr.card, modelValue, enumPrefix)
      case prev if attr.isAttr               => x(5, prev, curTree, modelValue); Atom(attr.ns, attr.name, attr.tpeS, attr.card, modelValue, enumPrefix)
      case prev if prev.isAttr               => x(6, prev, curTree, modelValue); Atom(prev.ns, attr.name, attr.tpeS, attr.card, modelValue, enumPrefix)
      case prev                              => x(7, prev, curTree, modelValue); Atom(attr.name, cur, "Int", attr.card, modelValue, enumPrefix)
      //      case prev if cur == "e" && prev.isAttr => x(5, prev, curTree, modelValue); Meta(prev.name, attr.name, "e", modelValue)
      //      case prev if cur == "e" && attr.isAttr => x(6, prev, curTree, modelValue); Meta(prev.name, attr.name, "e", modelValue)
      //      case prev if cur == "e" && prev.isAttr => x(7, prev, curTree, modelValue); Meta(prev.name, "", "e", modelValue)
      //      case prev if cur == "e"                => x(8, prev, curTree, modelValue); Meta(prev.name, "", "e", modelValue)
    }
  }


  // Values --------------------------------------------------------------------------

  def getValues(attr: Tree, values: Tree): Any = values match {
    case q"Seq($pkg.?)"     => Qm
    case q"Seq($pkg.count)" => Fn("count")
    case q"Seq(..$vs)"      =>
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
    case Constant(v: String)                                  => v
    case Literal(Constant(v: String))                         => v
    case Ident(TermName(v: String))                           => "__ident__" + v
    case Select(This(TypeName("$anon")), TermName(v: String)) => "__ident__" + v
    case v                                                    => v
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
  def apply(c: Context)(model: c.Expr[NS]): Model = {
    val model1 = inst(c).resolve(model.tree)

    // Sanity check
    model1.elements.collectFirst {
      case a: Atom                      => a
      case b: Bond                      => b
      case m@Meta(_, "txInstant", _, _) => m
      //      case m: Meta => m
    } getOrElse
      c.abort(c.enclosingPosition, s"[Dsl2Model:apply] Molecule is empty or has only meta attributes. Please add one or more attributes.\n$model1")

    model1
  }
}
