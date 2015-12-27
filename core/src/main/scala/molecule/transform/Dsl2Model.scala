package molecule
package transform
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait Dsl2Model[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = DebugMacro("Dsl2Model", 29, 28)
  //  val x = Debug("Dsl2Model", 30, 32, true)

  def resolve(tree: Tree): Seq[Element] = dslStructure.applyOrElse(
    tree, (t: Tree) => abort(s"[Dsl2Model:resolve] Unexpected tree: $t\nRAW: ${showRaw(t)}"))

  def traverse(prev: Tree, element: Element): Seq[Element] = {
    //    x(1, prev, element)
    if (prev.isAttr || prev.symbol.isMethod) resolve(prev) :+ element else Seq(element)
  }

  def kw(kwTree: Tree): (String, String) = (kwTree.toString(): String) match {
    case r""""\:(\w*)$ns0/(\w*)$attr0"""" => (ns0, attr0)
    case otherKw                          => abort("[Dsl2Model:kw] Unrecognized attribute keyword: " + otherKw)
  }

  val dslStructure: PartialFunction[Tree, Seq[Element]] = {
    case q"TermValue.apply($ns)" => resolve(ns)

    // Namespace(eid).attr1...
    case q"$prev.$ns.apply($eid)" if q"$prev.$ns".isFirstNS => traverse(q"$prev", Meta(firstLow(ns), "", "e", NoValue, Eq(Seq(extract(eid)))))


    // Functions ---------------------------

    case q"$prev.$cur.length.apply(..$values)" => traverse(q"$prev", resolveOp(q"$prev", q"$cur", q"$prev.$cur", q"count", q"Seq(..$values)"))
    case q"$prev.$cur.length"                  => traverse(q"$prev", resolveOp(q"$prev", q"$cur", q"$prev.$cur", q"count", q"Seq()"))


    // EAV + ns -----------------------------

    case q"$prev.e" => traverse(q"$prev", Meta("", "", "e", NoValue, EntValue))
    case q"$prev.a" => traverse(q"$prev", Atom("?", "attr", "a", 1, NoValue))

    case q"$prev.ns.apply(..$values)"  => traverse(q"$prev", Atom("ns", "?", "ns", 1, modelValue("apply", null, q"Seq(..$values)")))
    case q"$prev.ns_.apply(..$values)" => traverse(q"$prev", Atom("ns_", "?", "ns", 1, modelValue("apply", null, q"Seq(..$values)")))
    case q"$prev.ns"                   => traverse(q"$prev", Atom("ns", "?", "ns", 1, NoValue))
    case q"$prev.ns_"                  => abort( s"""[Dsl2Model:dslStructure] Generic namespace value `ns_` can only be used with an applied value i.e. `ns_("someNamespace")`""")

    case q"$prev.v.apply(..$values)"  => traverse(q"$prev", Meta("", "", "v", AttrVar(""), modelValue("apply", null, q"Seq(..$values)")))
    case q"$prev.v_.apply(..$values)" => traverse(q"$prev", Meta("", "", "v", NoValue, modelValue("apply", null, q"Seq(..$values)")))
    case q"$prev.v"                   => traverse(q"$prev", Meta("", "", "v", AttrVar(""), NoValue))
    case q"$prev.v_"                  => abort( s"""[Dsl2Model:dslStructure] Generic attribute value `v_` can only be used with an applied value i.e. `v_("some value")`""")


    // Tx ----------------------------------

    // Db.txInstant etc.. (all database transactions)
    case q"$prev.Db.tx"        => traverse(q"$prev", Atom("db", "tx", "Long", 1, VarValue))
    case q"$prev.Db.txT"       => traverse(q"$prev", Atom("db", "txT", "Long", 1, VarValue))
    case q"$prev.Db.txInstant" => traverse(q"$prev", Atom("db", "txInstant", "Long", 1, VarValue))
    case q"$prev.Db.op"        => traverse(q"$prev", Atom("db", "op", "Boolean", 1, VarValue))

    case q"$prev.tx_.apply($txMolecule)"       => traverse(q"$prev", TxModel(resolve(q"$txMolecule")))
    case q"$prev.tx_.apply[..$t]($txMolecule)" => traverse(q"$prev", TxModel(resolve(q"$txMolecule")))

    // ns.txInstant.attr - `txInstant` doesn't relate to any previous attr
    case q"$prev.tx" if !q"$prev".isAttr        => abort(s"[Dsl2Model:dslStructure] Please add `tx` after an attribute or another transaction value")
    case q"$prev.txT" if !q"$prev".isAttr       => abort(s"[Dsl2Model:dslStructure] Please add `txT` after an attribute or another transaction value")
    case q"$prev.txInstant" if !q"$prev".isAttr => abort(s"[Dsl2Model:dslStructure] Please add `txInstant` after an attribute or another transaction value")
    case q"$prev.op" if !q"$prev".isAttr        => abort(s"[Dsl2Model:dslStructure] Please add `op` after an attribute or another transaction value")

    // ns.attr.txInstant etc.. (transaction related to previous attribute)
    case q"$prev.tx"        => traverse(q"$prev", Meta("db", "tx", "tx", TxValue, NoValue))
    case q"$prev.txT"       => traverse(q"$prev", Meta("db", "txT", "tx", TxTValue, NoValue))
    case q"$prev.txInstant" => traverse(q"$prev", Meta("db", "txInstant", "tx", TxInstantValue, NoValue))
    case q"$prev.op"        => traverse(q"$prev", Meta("db", "op", "tx", OpValue, NoValue))


    // Optional ----------------------

    case a@q"$prev.$refAttr" if a.isRefAttr$ => traverse(q"$prev", Atom(a.ns, a.name, "Long", a.card, VarValue))
    case a@q"$prev.$cur" if a.isEnum$        => traverse(q"$prev", Atom(a.ns, a.name, cast(a), a.card, EnumVal, Some(a.enumPrefix)))
    case a@q"$prev.$cur" if a.isMapAttr$     => walk(q"$prev", a.ns, q"$cur", Atom(a.ns, a.name, cast(a), 3, VarValue, Some("mapping")))
    case a@q"$prev.$cur" if a.isValueAttr$   => walk(q"$prev", a.ns, q"$cur", Atom(a.ns, a.name, cast(a), a.card, VarValue))


    // Generic -----------------------------

    case q"$prev.$ref.apply(..$values)" if q"$prev.$ref".isRef => abort(s"[Dsl2Model:dslStructure] Can't apply value to a reference (`$ref`)")

    case t@q"$prev.$cur.$op(..$values)" => walk(q"$prev", q"$prev.$cur".ns, q"$cur", resolveOp(q"$prev", q"$cur", q"$prev.$cur", q"$op", q"Seq(..$values)"))

    case r@q"$prev.$backRefAttr" if backRefAttr.toString.head == '_' =>
      val backRef = c.typecheck(q"$prev.$backRefAttr").tpe.typeSymbol.name.toString // "partition_Ns_<arity>"
      traverse(q"$prev", ReBond(firstLow(backRef.replaceFirst("_[0-9]+$", "")), "")) // "partition_Ns"

    case a@q"$prev.$refAttr" if a.isRef     => traverse(q"$prev", Bond(a.refThis, firstLow(refAttr.toString), a.refNext))
    case a@q"$prev.$refAttr" if a.isRefAttr => traverse(q"$prev", Atom(a.ns, a.name, "Long", a.card, VarValue))
    case a@q"$prev.$cur" if a.isEnum        => traverse(q"$prev", Atom(a.ns, a.name, cast(a), a.card, EnumVal, Some(a.enumPrefix)))
    case a@q"$prev.$cur" if a.isMapAttr     => walk(q"$prev", a.ns, q"$cur", Atom(a.ns, a.name, cast(a), 3, VarValue, Some("mapping")))
    case a@q"$prev.$cur" if a.isValueAttr   => walk(q"$prev", a.ns, q"$cur", Atom(a.ns, a.name, cast(a), a.card, VarValue))


    // Nested ------------------------

    case t@q"$prev.e.apply[..$types]($nested)" if !q"$prev".isRef  => Seq(Group(Bond("", "", ""), Meta("", "", "e", NoValue, EntValue) +: resolve(nested)))
    case t@q"$prev.e_.apply[..$types]($nested)" if !q"$prev".isRef => Seq(Group(Bond("", "", ""), resolve(nested)))

    case t@q"$prev.e.$manyRef.*[..$types]($nested)"                               => traverse(q"$prev.e", nested1(q"$prev", manyRef, nested))
    case t@q"$prev.$manyRef.*[..$types]($nested)"                                 => traverse(q"$prev", nested1(q"$prev", manyRef, nested))
    case t@q"$prev.$manyRef.apply[..$types]($nested)" if !q"$prev.$manyRef".isRef => Seq(Group(Bond("", "", ""), nestedElements(q"$prev.$manyRef", firstLow(manyRef.toString), nested)))
    case t@q"$prev.$manyRef.apply[..$types]($nested)"                             => traverse(q"$prev", nested1(prev, manyRef, nested))

    case other => abort(s"[Dsl2Model:dslStructure] Unexpected DSL structure: $other\n${showRaw(other)}")
  }

  def walk(prev: Tree, curNs: String, cur: Tree, thisElement: Element) = {
    val prevElements = if (q"$prev".isAttr || q"$prev".symbol.isMethod) resolve(prev) else Seq[Element]()
    val attr = cur.toString()
    x(2, prevElements, curNs, attr, thisElement)
    if (prevElements.isEmpty) {
      traverse(q"$prev", thisElement)
    } else {
      prevElements.last match {
        case Atom(prevNs0, prevAttr0, _, _, _, _, _) if prevNs0 == curNs && clean(prevAttr0) == clean(attr) =>
          val (_, similarAtoms, transitive) = {
            prevElements.foldRight(prevElements, Seq[Atom](), None: Option[Transitive]) {
              case (prevElement, (previous, similarAtoms1, trans)) =>
                //                x(6, previous, prevElement)
                prevElement match {
                  case prevAtom@Atom(prevNs, prevAttr, _, _, _, _, _) if prevNs == curNs && clean(prevAttr) == clean(attr) =>
                    val t = previous.init.reverse.collectFirst {
                      // Find first previous Bond (relating to this attribute)
                      case prevBond@Bond(ns2, refAttr, refNs) =>
                        //                        x(2, prevAtom, prevBond, ns2, refAttr, refNs)
                        Transitive(ns2, refAttr, refNs, 0)
                    } getOrElse {
                      //                      x(3, curNs, cur)
                      Transitive(prevNs, prevAttr, prevNs, 0)
                    }
                    //                    x(4, prevElements.last, prevNs, prevAttr)
                    (previous.init, similarAtoms1 :+ prevAtom, Some(t))
                  case _                                                                                                   =>
                    (previous.init, similarAtoms1, trans)
                }
            }
          }
          similarAtoms.size match {
            case 1 => prevElements :+ transitive.get.copy(depth = 1) :+ thisElement
            case 2 => prevElements :+ transitive.get.copy(depth = 2) :+ thisElement
            case 3 => prevElements :+ transitive.get.copy(depth = 3) :+ thisElement
            case n => abort(s"[Dsl2Model:walk] Unsupported transitive arity: $n")
          }

        case _ => traverse(q"$prev", thisElement)
      }
    }
  }

  def clean(a: String) = if (a.last == '_') firstLow(a.init) else firstLow(a)

  def nested1(prev: Tree, manyRef: TermName, nested: Tree) = {
    val refNext = q"$prev.$manyRef".refNext
    val parentNs = prev match {
      case q"$p.apply($value)" if p.isAttr         => p.ns
      case q"$p.apply($value)"                     => p.name
      case p if p.symbol.name.toString.head == '_' => firstLow(prev.tpe.typeSymbol.name.toString.replaceFirst("_[0-9]+$", ""))
      case p if p.isAttr                           => p.ns
      case p if p.isRef                            => p.refNext
      case p                                       => p.name
    }
    val nestedElems = nestedElements(q"$prev.$manyRef", refNext, nested)
    val group = Group(Bond(parentNs.toString, firstLow(manyRef), refNext), nestedElems)
    //    x(28, prev, parentNs, nestedElems, group, refNext)
    group
  }

  def nestedElements(manyRef: Tree, refNext: String, nested: Tree): Seq[Element] = {
    val nestedElements = resolve(nested)
    val nestedNs = curNs(nestedElements.head)
    if (refNext != nestedNs) {
      // Find refs in `manyRef` namespace and match the target type with the first namespace of the first nested element
      val refs = c.typecheck(manyRef).tpe.members.filter(e => e.isMethod && e.asMethod.returnType <:< weakTypeOf[Ref[_, _]])
      val refPairs = refs.map(r => r.name -> r.typeSignature.baseType(weakTypeOf[Ref[_, _]].typeSymbol).typeArgs.last.typeSymbol.name)
      val refPairsFiltered = refPairs.filter(_._2.toString == nestedNs.capitalize)
      if (refPairsFiltered.isEmpty) {
        nestedElements
      } else if (refPairsFiltered.size == 1) {
        val (refAttr, refNs) = refPairsFiltered.head
        Bond(refNext, firstLow(refAttr), firstLow(refNs)) +: nestedElements
      } else
        abort(s"[Dsl2Model:dslStructure(nested)] `$manyRef` has more than one ref pointing to `$nestedNs`:\n${refPairs.mkString("\n")}")
    } else {
      nestedElements
    }
  }

  def cast(atom: Tree) = atom.tpeS match {
    case "Int"   => "Long"
    case "Float" => "Double"
    case other   => other
  }

  def resolveOp(previous: Tree, curTree: Tree, attr: Tree, op: Tree, values0: Tree): Element = {
    val value: Value = modelValue(op.toString(), attr, values0)
    val enumPrefix = if (attr.isEnum) Some(attr.at.enumPrefix) else None
    val cur = curTree.toString()
    previous match {
      case prev if cur.head.isUpper          => Atom(attr.name, cur, cast(attr), attr.card, value, enumPrefix)
      case prev if cur == "e" && prev.isRef  => Meta(prev.name, prev.refNext, "e", NoValue, value)
      case prev if cur == "e" && prev.isAttr => Atom(prev.ns, cur, cast(attr), attr.card, value, enumPrefix)
      case prev if cur == "e"                => Meta(prev.name, cur, "e", NoValue, value)
      case prev if cur == "a"                => Atom("?", "attr", "a", 1, value)
      case prev if cur == "a_"               => Atom("?", "attr_", "a", 1, value)
      case prev if cur == "ns"               => Atom("ns", "?", "ns", 1, value)
      case prev if cur == "ns_"              => Atom("ns_", "?", "ns", 1, value)
      case prev if attr.isMapAttr            => Atom(attr.ns, attr.name, cast(attr), 3, value, Some("mapping"))
      case prev if attr.isAttr               => Atom(attr.ns, attr.name, cast(attr), attr.card, value, enumPrefix)
      case prev if prev.isAttr               => Atom(prev.ns, attr.name, cast(attr), attr.card, value, enumPrefix)
      case prev                              => Atom(attr.name, cur, "Int", attr.card, value, enumPrefix)
    }
  }


  // Values ================================================================================

  def modelValue(op: String, attr: Tree, values0: Tree) = {
    def errValue(v: Any) = abort(s"[Dsl2Model:modelValue] Unexpected resolved model value for `${attr.name}.$op`: $v")
    val values = getValues(values0, attr)
    op match {
      case "apply"       => values match {
        case resolved: Value => resolved
        case vs: Seq[_]      => if (vs.isEmpty) Remove(Seq()) else Eq(vs)
        case other           => errValue(other)
      }
      case "count"       => values match {case Fn("avg", i) => Length(Some(Fn("avg", i))); case other => errValue(other)}
      case "not"         => values match {case qm: Qm.type => Neq(Seq(Qm)); case vs: Seq[_] => Neq(vs)}
      case "$bang$eq"    => values match {case qm: Qm.type => Neq(Seq(Qm)); case vs: Seq[_] => Neq(vs)}
      case "$less"       => values match {case qm: Qm.type => Lt(Qm); case vs: Seq[_] => Lt(vs.head)}
      case "$greater"    => values match {case qm: Qm.type => Gt(Qm); case vs: Seq[_] => Gt(vs.head)}
      case "$less$eq"    => values match {case qm: Qm.type => Le(Qm); case vs: Seq[_] => Le(vs.head)}
      case "$greater$eq" => values match {case qm: Qm.type => Ge(Qm); case vs: Seq[_] => Ge(vs.head)}
      case "contains"    => values match {case qm: Qm.type => Fulltext(Seq(Qm)); case vs: Seq[_] => Fulltext(vs)}
      case "add"         => values match {case vs: Seq[_] => Eq(vs); case mapped: Value => mapped}
      case "remove"      => values match {case vs: Seq[_] => Remove(vs)}
      case unexpected    => abort(s"[Dsl2Model:atomOp] Unknown operator '$unexpected'\nattr: $attr \nvalue: $values0")
    }
  }

  def getAppliedValue(attr: Tree, values0: Tree): Value = getValues(values0, attr) match {
    case resolved: Value => resolved
    case vs: Seq[_]      => if (vs.isEmpty) Remove(Seq()) else Eq(vs)
    case other           => abort(s"[Dsl2Model:getAppliedModelValue] Unexpected applied value for `${attr.name}`: $other")
  }

  def getValues(values: Tree, attr: Tree = null): Any = {
    def aggr(fn: String, value: Option[Int] = None) = if (attr.name.last == '_')
      abort(s"[Dsl2Model:getValues] Aggregated values need to be returned. " +
        s"Please omit underscore from attribute `:${attr.ns}/${attr.name}`")
    else
      Fn(fn, value)

    values match {
      case q"Seq($pkg.?)"                                              => Qm
      case q"Seq($pkg.distinct)"                                       => Distinct
      case q"Seq($pkg.max.apply(${Literal(Constant(i: Int))}))"        => aggr("max", Some(i))
      case q"Seq($pkg.min.apply(${Literal(Constant(i: Int))}))"        => aggr("min", Some(i))
      case q"Seq($pkg.rand.apply(${Literal(Constant(i: Int))}))"       => aggr("rand", Some(i))
      case q"Seq($pkg.sample.apply(${Literal(Constant(i: Int))}))"     => aggr("sample", Some(i))
      case q"Seq($pkg.max)"                                            => aggr("max")
      case q"Seq($pkg.min)"                                            => aggr("min")
      case q"Seq($pkg.rand)"                                           => aggr("rand")
      case q"Seq($pkg.count)"                                          => aggr("count")
      case q"Seq($pkg.countDistinct)"                                  => aggr("count-distinct")
      case q"Seq($pkg.sum)"                                            => aggr("sum")
      case q"Seq($pkg.avg)"                                            => aggr("avg")
      case q"Seq($pkg.median)"                                         => aggr("median")
      case q"Seq($pkg.variance)"                                       => aggr("variance")
      case q"Seq($pkg.stddev)"                                         => aggr("stddev")
      case q"Seq($pkg.nil)" if attr.name.last == '_'                   => Fn("not")
      case q"Seq($pkg.nil)"                                            => abort(s"[Dsl2Model:getValues] Please add underscore to attribute: `${attr.name}_(nil)`")
      case q"Seq($a.and[$t]($b).and[$u]($c))" if attr.name.last == '_' => And(resolveValues(q"Seq($a, $b, $c)"))
      case q"Seq($a.and[$t]($b))" if attr.name.last == '_'             => And(resolveValues(q"Seq($a, $b)"))
      case q"Seq($a.and[$t]($b).and[$u]($c))"                          => abort(s"[Dsl2Model:getValues] Can't retrieve multiple values from AND expression. Please add underscore to attribute: `${attr.name}_([AND-expression])`")
      case q"Seq($a.and[$t]($b))"                                      => abort(s"[Dsl2Model:getValues] Can't retrieve multiple values from AND expression. Please add underscore to attribute: `${attr.name}_([AND-expression])`")
      case q"Seq(..$vs)"                                               => vs match {
        case pairs if pairs.nonEmpty && pairs.head.tpe <:< weakTypeOf[(_, _)] =>
          val keyValues = pairs.map {
            case q"scala.this.Predef.ArrowAssoc[$t1]($k).->[$t2]($v)" => (extract(k), extract(v))
          }
          //          x(6, keyValues)
          if (attr.isMapAttr) {
            val keys = keyValues.map(_._1).distinct
            if (keys.contains("_") && keys.size > 1)
              abort(s"[Dsl2Model:getValues] Searching for all keys with `_` can't be combined with other key-values.")
            Mapping(keyValues.map(kv => (kv._1.asInstanceOf[String], kv._2)))
          } else
            Replace(keyValues.toMap)
        case other if attr == null                                            => vs.flatMap(v => resolveValues(v))
        case other                                                            => vs.flatMap(v => resolveValues(v, att(q"$attr")))
      }
      case other if attr == null                                       => resolveValues(other)
      //      case other if attr.isOneURI || attr.isManyURI                => Fn("URI", resolveValues(other))
      case other => resolveValues(other, att(q"$attr"))
    }
  }

  def extract(t: Tree) = {
    //    x(31, t.raw)
    t match {
      case Constant(v: String)                            => v
      case Literal(Constant(v: String))                   => v
      case Ident(TermName(v: String))                     => "__ident__" + v
      case Select(This(TypeName(_)), TermName(v: String)) => "__ident__" + v
      case v                                              => v
    }
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
    values
  }
}

object Dsl2Model {
  def inst(c0: Context) = new {val c: c0.type = c0} with Dsl2Model[c0.type]
  def apply(c: Context)(dsl: c.Expr[NS]): Model = {
    val elements0 = inst(c).resolve(dsl.tree)

    // Sanity checks .......................................................................

    // Avoid ending with a ref
    elements0.last match {
      case b: Bond => c.abort(c.enclosingPosition,
        s"[Dsl2Model:apply (1)] Molecule not allowed to end with a reference. Please add a one or more attribute to the reference.")
      case _       => "ok"
    }

    // Molecule should at least have one mandatory attribute
    elements0.collectFirst {
      case a@Atom(_, name, _, _, _, _, _) if name.last != '$' => a
      case b: Bond                                            => b
      case g: Group                                           => g
      case m@Meta(_, "txInstant", _, _, _)                    => m
    } getOrElse
      c.abort(c.enclosingPosition, s"[Dsl2Model:apply] Molecule is empty or has only meta/optional attributes. Please add one or more attributes.")


    // Resolve generic elements ............................................................

    // Transfer generic values from Meta elements to Atoms and skip Meta elements
    val elements1 = elements0.foldRight(Seq[Element](), Seq[Generic](), NoValue: Value) { case (element, (es, gs, v)) =>
      element match {
        case a: Atom if a.name != "attr" && gs.contains(NsValue) && !gs.contains(AttrVar) =>
          c.abort(c.enclosingPosition, s"[Dsl2Model:apply (6)] `ns` needs to have a generic `a` before")

        case a: Atom if gs.isEmpty       => (a +: es, Nil, NoValue)
        case a: Atom if a.name == "attr" => (a.copy(gs = a.gs ++ gs, value = v) +: es, Nil, NoValue)
        case a: Atom                     => (a.copy(gs = a.gs ++ gs) +: es, Nil, NoValue)
        case m@Meta(_, _, "e", g, v1)    => (m +: es, g +: gs, v1)
        case Meta(_, _, _, g, v1)        => (es, g +: gs, v1)
        case other                       => (other +: es, gs, v)
      }
    }._1

    val model = Model(elements1)
    //        inst(c).x(30, dsl, elements0, elements1, model)
    //        inst(c).x(30, model)

    model
  }
}