package molecule
package transform
import molecule.ast.model._
import molecule.ast.query._
import molecule.ops.QueryOps._
import molecule.util.Debug

object Model2Query {
  val x = Debug("Model2Query", 5, 60, false)

  def apply(model: Model): Query = {

    def resolve(q: Query, e: String, v: String, element: Element) = {
      val (v1, v2, v3) = (v + 1, v + 2, v + 3)
      element match {

        // Manipulate ------------------------------------------------------------

        case Atom(_, _, _, _, Replace(_), _, _) => q
        case Atom(_, _, _, _, Remove(_), _, _)  => q


        // Schema ------------------------------------------------------------

        case Atom("?", "attr_", _, _, value, _, gs) => value match {
          case Distinct                => q.attr(e, Var(v), v1, v2, gs)
          case Fn(fn, Some(i))         => q.attr(e, Var(v), v1, v2, gs)
          case Fn(fn, _)               => q.attr(e, Var(v), v1, v2, gs)
          case Length(Some(Fn(fn, _))) => q.attr(e, Var(v), v1, v2, gs).func("count", Var(v2), v3)
          case Length(_)               => q.attr(e, Var(v), v1, v2, gs)
          case Eq(ss) if ss.size > 1   => q.attr(e, Var(v), v1, v2, gs)
          case Eq((s: String) :: Nil)  => q.attr(e, Var(v3), v1, v2, gs).where(e, "?", "attr", Val(s), "", Seq())
          case _                       => q.attr(e, Var(v), v1, v2, gs)
        }

        case Atom("?", "attr", _, _, value, _, gs) => value match {
          case Distinct                => q.attr(e, Var(v), v1, v2, gs).find("distinct", Seq(), v2, gs, v)
          case Fn(fn, Some(i))         => q.attr(e, Var(v), v1, v2, gs).find(fn, Seq(i), v2, gs)
          case Fn(fn, _)               => q.attr(e, Var(v), v1, v2, gs).find(fn, Seq(), v2, gs)
          case Length(Some(Fn(fn, _))) => q.attr(e, Var(v), v1, v2, gs).func("count", Var(v2), v3).find(fn, Seq(), v3, gs)
          case Length(_)               => q.attr(e, Var(v), v1, v2, gs).find("count", Seq(), v2, gs)
          case Eq(ss) if ss.size > 1   => q.attr(e, Var(v), v1, v2, gs).find(v2, gs, v)
          case Eq((s: String) :: Nil)  => q.attr(e, Var(v3), v1, v2, gs).where(e, "?", "attr", Val(s), "", Seq()).find(v2, gs, v3)
          case _                       => q.attr(e, Var(v), v1, v2, gs).find(v2, gs, v)
        }

        case Atom("ns_", "?", _, _, value, _, gs) => value match {
          case Qm                      => q.ns(e, Var(v), v1, v2, gs).in(v2, "ns", "?", v2)
          case Distinct                => q.ns(e, Var(v), v1, v2, gs)
          case Fn(fn, Some(i))         => q.ns(e, Var(v), v1, v2, gs)
          case Fn(fn, _)               => q.ns(e, Var(v), v1, v2, gs)
          case Length(Some(Fn(fn, _))) => q.ns(e, Var(v), v1, v2, gs).func("count", Var(v2), v3)
          case Length(_)               => q.ns(e, Var(v), v1, v2, gs)
          case Eq(ss) if ss.size > 1   => q.ns(e, Var(v), v1, v2, gs)
          case Eq((s: String) :: Nil)  => q.ns(e, Var(v), v1, v2, gs).func("=", Seq(Var(v2), Val(s)))
          case _                       => q.ns(e, Var(v), v1, v2, gs)
        }

        case Atom("ns", "?", _, _, value, _, gs) => value match {
          case Distinct                => q.ns(e, Var(v), v1, v2, gs).find("distinct", Seq(), v2, gs, v)
          case Fn(fn, Some(i))         => q.ns(e, Var(v), v1, v2, gs).find(fn, Seq(i), v2, gs)
          case Fn(fn, _)               => q.ns(e, Var(v), v1, v2, gs).find(fn, Seq(), v2, gs)
          case Length(Some(Fn(fn, _))) => q.ns(e, Var(v), v1, v2, gs).func("count", Var(v2), v3).find(fn, Seq(), v3, gs)
          case Length(_)               => q.ns(e, Var(v), v1, v2, gs).find("count", Seq(), v2, gs)
          case Eq(ss) if ss.size > 1   => q.ns(e, Var(v), v1, v2, gs).find(v2, gs, v)
          case Eq((s: String) :: Nil)  => q.ns(e, Var(v), v1, v2, gs).func("=", Seq(Var(v2), Val(s))).find(v2, gs, v3)
          case _                       => q.ns(e, Var(v), v1, v2, gs).find(v2, gs, v)
        }


        // Enum Atom_ ------------------------------------------------------------

        case a0@Atom(_, attr0, _, 2, value, Some(prefix), gs) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case EnumVal => q.enum(e, a, v, gs)
            case other   =>
              sys.error(s"[Model2Query:resolve[Atom_]] Unresolved cardinality 2 enum Atom_:\nAtom_  : $a\nElement: $other")
          }
        }

        case a0@Atom(_, attr0, _, 1, value, Some(prefix), gs) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Qm                    => q.where(e, a, v, gs).in(v, a, Some(prefix), e)
            case EnumVal               => q.enum(e, a, v, gs)
            case Eq(ss) if ss.size > 1 => q.orRules(e, a, ss.map(prefix + _), gs)
            case Eq(s :: Nil)          => q.where(e, a, Val(prefix + s), gs)
            case other                 =>
              sys.error(s"[Model2Query:resolve[Atom_]] Unresolved cardinality 1 enum Atom_:\nAtom_  : $a\nElement: $other")
          }
        }


        // Atom_ -----------------------------------------------------------------

        case a0@Atom(_, attr0, _, 2, value, _Prefix, gs) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Fulltext(Seq(Qm))     => q.fulltext(e, a, v, Var(v1)).in(v1, a)
            case Fulltext(qv :: Nil)   => q.fulltext(e, a, v, Val(qv))
            case VarValue              => q.where(e, a, v, gs)
            case Eq(ss) if ss.size > 1 => q.orRules(e, a, ss, gs)
            case other                 =>
              sys.error(s"[Model2Query:resolve[Atom_]] Unresolved cardinality 2 Atom_:\nAtom_  : $a\nElement: $other")
          }
        }

        case a0@Atom(_, attr0, _, 1, value, _, gs) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Qm                      => q.where(e, a, v, gs).in(v, a)
            case Fulltext(Seq(Qm))       => q.fulltext(e, a, v, Var(v1)).in(v1, a)
            case Neq(Seq(Qm))            => q.where(e, a, v, gs).compareTo("!=", a, v, Var(v1))
            case Lt(Qm)                  => q.where(e, a, v, gs).compareTo("<", a, v, Var(v1))
            case Gt(Qm)                  => q.where(e, a, v, gs).compareTo(">", a, v, Var(v1))
            case Le(Qm)                  => q.where(e, a, v, gs).compareTo("<=", a, v, Var(v1))
            case Ge(Qm)                  => q.where(e, a, v, gs).compareTo(">=", a, v, Var(v1))
            case VarValue                => q.find(gs).where(e, a, v, gs)
            case Eq(ss) if ss.size > 1   => q.orRules(e, a, ss, gs)
            case Eq((ss: Seq[_]) :: Nil) => q.orRules(e, a, ss, gs)
            case Eq(s :: Nil)            => q.where(e, a, Val(s), gs)
            case And(vs)                 => q.where(e, a, Val(vs.head), gs)
            case Neq(args)               => q.where(e, a, v, gs).compareTo("!=", a, v, args map Val)
            case Lt(arg)                 => q.where(e, a, v, gs).compareTo("<", a, v, Val(arg))
            case Gt(arg)                 => q.where(e, a, v, gs).compareTo(">", a, v, Val(arg))
            case Le(arg)                 => q.where(e, a, v, gs).compareTo("<=", a, v, Val(arg))
            case Ge(arg)                 => q.where(e, a, v, gs).compareTo(">=", a, v, Val(arg))
            case Fulltext(qv :: Nil)     => q.fulltext(e, a, v, Val(qv))
            case Fulltext(qvs)           => q.orRules(v1, a, qvs, gs).fulltext(e, a, v, Var(v1))
            case other                   =>
              sys.error(s"[Model2Query:resolve[Atom_]] Unresolved cardinality 1 Atom_:\nAtom_  : $a\nElement: $other")
          }
        }


        // Enum Atom ------------------------------------------------------------

        case a@Atom(_, _, _, 2, value, Some(prefix), gs) => value match {
          case EnumVal => q.enum(e, a, v, gs).find("distinct", Seq(), v2, gs)
          case other   => sys.error(s"[Model2Query:resolve[Atom]] Unresolved cardinality 2 enum Atom:\nAtom   : $a\nElement: $other")
        }

        case a@Atom(_, _, _, 1, value, Some(prefix), gs) => value match {
          case Qm                    => q.enum(e, a, v, gs).in(v, a, Some(prefix)).find(v2, gs)
          case Lt(Qm)                => q.enum(e, a, v, gs).where(e, a, v, gs).find(v2, gs).compareTo("<", a, v, Var(v1)).find(v, gs).in(v1, a)
          case Gt(Qm)                => q.enum(e, a, v, gs).where(e, a, v, gs).compareTo(">", a, v, Var(v1)).find(v, gs).in(v1, a)
          case EnumVal               => q.enum(e, a, v, gs).find(v2, gs)
          case Eq(ss) if ss.size > 1 => q.enum(e, a, v, gs).orRules(e, a, ss.map(prefix + _), gs).find(v2, gs)
          case Eq(s :: Nil)          => q.enum(e, a, v, gs).where(e, a, Val(prefix + s), gs).find(v2, gs)
          case Neq(args)             => q.enum(e, a, v, gs).compareTo("!=", a, v2, args map Val).find(v2, gs)
          case Lt(arg)               => q.enum(e, a, v, gs).compareTo("<", a, v2, Val(arg), 1).find(v2, gs)
          case Gt(arg)               => q.enum(e, a, v, gs).compareTo(">", a, v2, Val(arg), 1).find(v2, gs)
          case Le(arg)               => q.enum(e, a, v, gs).compareTo("<=", a, v2, Val(arg), 1).find(v2, gs)
          case Ge(arg)               => q.enum(e, a, v, gs).compareTo(">=", a, v2, Val(arg), 1).find(v2, gs)
          case other                 => sys.error(s"[Model2Query:resolve[Atom]] Unresolved cardinality 1 enum Atom:\nAtom   : $a\nElement: $other")
        }


        // Atom -----------------------------------------------------------------

        case a@Atom(_, _, _, 2, value, _, gs) => value match {
          case Fulltext(Seq(Qm))       => q.fulltext(e, a, v, Var(v1)).in(v1, a).find("distinct", Seq(), v, gs)
          case VarValue                => q.where(e, a, v, gs).find("distinct", Seq(), v, gs)
          case EnumVal                 => q.enum(e, a, v, gs).find("distinct", Seq(), v2, gs)
          case Eq(ss) if ss.size > 1   => q.orRules(e, a, ss).where(e, a, v, gs).find("distinct", Seq(), v, gs)
          case Eq((ss: Seq[_]) :: Nil) => q.orRules(e, a, ss, gs).where(e, a, v, gs).find(v, gs)
          case Eq(s :: Nil)            => q.where(e, a, Val(s), gs).where(e, a, v, Seq()).find(v, gs)
          case Fulltext(qv :: Nil)     => q.fulltext(e, a, v, Val(qv)).find("distinct", Seq(), v, gs)
          case other                   => sys.error(s"[Model2Query:resolve[Atom]] Unresolved cardinality 2 Atom:\nAtom   : $a\nElement: $other")
        }

        case a@Atom(_, _, _, 1, value, _, gs) => value match {
          case Qm                            => q.where(e, a, v, gs).in(v, a).find(v, gs)
          case Fulltext(Seq(Qm))             => q.fulltext(e, a, v, Var(v1)).in(v1, a).find(v, gs)
          case EntValue                      => q.find(e, gs)
          case Neq(Seq(Qm))                  => q.where(e, a, v, gs).compareTo("!=", a, v, Var(v1)).find(v, gs).in(v1, a)
          case Lt(Qm)                        => q.where(e, a, v, gs).compareTo("<", a, v, Var(v1)).find(v, gs).in(v1, a)
          case Gt(Qm)                        => q.where(e, a, v, gs).compareTo(">", a, v, Var(v1)).find(v, gs).in(v1, a)
          case Le(Qm)                        => q.where(e, a, v, gs).compareTo("<=", a, v, Var(v1)).find(v, gs).in(v1, a)
          case Ge(Qm)                        => q.where(e, a, v, gs).compareTo(">=", a, v, Var(v1)).find(v, gs).in(v1, a)
          case VarValue                      => q.where(e, a, v, gs).find(v, gs)
          case NoValue                       => q.where(e, a, v, gs).find(NoVal, gs)
          case BackValue(backNs)             => q.where(v, a.ns, a.name, Var(e), backNs, gs).find(e, gs)
          case Eq(ss) if ss.size > 1         => q.where(e, a, v, gs).orRules(e, a, ss, gs).find(v, gs)
          case Eq((ss: Seq[_]) :: Nil)       => q.where(e, a, v, gs).orRules(e, a, ss, gs).find(v, gs)
          case Eq(s :: Nil)                  => q.where(e, a, Val(s), gs).where(e, a, v, Seq()).find(v, gs)
          case Neq(args)                     => q.where(e, a, v, gs).compareTo("!=", a, v, args map Val).find(v, gs)
          case Lt(arg)                       => q.where(e, a, v, gs).compareTo("<", a, v, Val(arg)).find(v, gs)
          case Gt(arg)                       => q.where(e, a, v, gs).compareTo(">", a, v, Val(arg)).find(v, gs)
          case Le(arg)                       => q.where(e, a, v, gs).compareTo("<=", a, v, Val(arg)).find(v, gs)
          case Ge(arg)                       => q.where(e, a, v, gs).compareTo(">=", a, v, Val(arg)).find(v, gs)
          case Fn("sum", _)                  => q.where(e, a, v, gs).find("sum", Seq(), v, gs).widh(e)
          case Fn("avg", _)                  => q.where(e, a, v, gs).find("avg", Seq(), v, gs).widh(e)
          case Fn(fn, Some(i))               => q.where(e, a, v, gs).find(fn, Seq(i), v, gs)
          case Fn(fn, _)                     => q.where(e, a, v, gs).find(fn, Seq(), v, gs)
          case Fulltext(qv :: Nil)           => q.fulltext(e, a, v, Val(qv)).find(v, gs)
          case Fulltext(qvs)                 => q.fulltext(e, a, v, Var(v1)).orRules(v1, a, qvs).find(v, gs)
          case Length(Some(Fn(fn, Some(i)))) => q.where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2).find(v2, gs)
          case Length(Some(Fn(fn, _)))       => q.where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2).find(fn, Seq(), v2, gs)
          case Length(_)                     => q.where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2).find(v2, gs)
          case other                         => sys.error(s"[Model2Query:resolve[Atom]] Unresolved cardinality 1 Atom:\nAtom   : $a\nElement: $other")
        }


        // Graph -----------------------------------------------------------------

        case Bond(ns, refAttr, refNs) => q.ref(e, ns, refAttr, v, refNs)

        case Transitive(backRef, refAttr, refNs, depth, prevVar) => q.transitive(backRef, refAttr, prevVar, v, depth)

        case ReBond(backRef, refAttr, refNs, _, _) => q.ref(e, backRef, refAttr, v, refNs)


        // Meta -----------------------------------------------------------------

        case Meta(_, _, "e", _, Fn("count", Some(i)))   => q.find("count", Seq(i), e, Seq())
        case Meta(_, _, "e", _, Fn("count", _))         => q.find("count", Seq(), e, Seq())
        case Meta(_, _, "e", _, Length(Some(Fn(_, _)))) => q.find(e, Seq())
        case Meta(_, _, _, _, EntValue)                 => q.find(e, Seq())
        case Meta(_, _, _, _, _)                        => q

        case unresolved => sys.error("[Model2Query:resolve] Unresolved model: " + unresolved)
      }
    }

    def make(query: Query, element: Element, e: String, v: String, prevNs: String, prevAttr: String, prevRefNs: String)
    : (Query, String, String, String, String, String) = {
      val w = (v.toCharArray.head + 1).toChar.toString
      element match {
        case Atom(ns, attr, "a", _, _, _, _)                  => (resolve(query, e, v, element), e, w, ns, attr, "")
        case Atom(ns, attr, "ns", _, _, _, _)                 => (resolve(query, e, v, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if ns == prevNs    => (resolve(query, e, w, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if ns == prevAttr  => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if ns == prevRefNs => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _)                    => (resolve(query, e, v, element), e, v, ns, attr, "")

        case Bond(ns, refAttr, refNs) if ns == prevNs    => (resolve(query, e, w, element), e, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs) if ns == prevAttr  => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs) if ns == prevRefNs => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs)                    => (resolve(query, e, v, element), e, v, ns, refAttr, refNs)

        case transitive@Transitive(backRef, refAttr, refNs, _, _) => {
          val (backRefE, backRefV) = query.wh.clauses.reverse.collectFirst {
            case DataClause(_, backE, a, Var(backV), _, _) if a.ns == backRef => (backE.v, backV)
          } getOrElse sys.error(s"[Model2Query:make(Transitive)] Can't find back reference `$backRef` in query so far:\n$query")
          val backRefElement = transitive.copy(prevVar = backRefV)
          (resolve(query, backRefE, w, backRefElement), v, w, backRef, refAttr, refNs)
        }

        case rbe@ReBond(backRef, refAttr, refNs, _, _) => {
          val (backRefE, backRefV) = query.wh.clauses.reverse.collectFirst {
            case DataClause(_, backE, a, Var(backV), _, _) if a.ns == backRef => (backE.v, backV)
          } getOrElse sys.error(s"[Model2Query:make(ReBond)] Can't find back reference `$backRef` in query so far:\n$query")
          val backRefElement = rbe.copy(prevVar = backRefV)
          (resolve(query, backRefE, w, backRefElement), v, w, backRef, refAttr, refNs)
        }

        case Group(b@Bond(ns, refAttr, refNs), elements) =>
          val (e2, elements2) = if (ns == "") (e, elements) else (w, b +: elements)
          val (q2, _, v2, ns2, attr2, refNs2) = elements2.foldLeft((query, e, v, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, (v2.toCharArray.head + 1).toChar.toString, ns2, attr2, refNs2)

        case Meta(ns, attr, "e", NoValue, Eq(Seq(id: Long))) => x(2, element); (resolve(query, id.toString, v, element), id.toString, v, ns, attr, "")
        case Meta(ns, attr, _, _, _)                         => x(3, element); (resolve(query, e, v, element), e, v, ns, attr, "")

        case TxModel(elements) =>
          val (q2, e2, v2, ns2, attr2, refNs2) = elements.foldLeft((query, "tx", w, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, (v2.toCharArray.head + 1).toChar.toString, ns2, attr2, refNs2)

        case other => sys.error("[Model2Query:make] Unresolved query variables from model: " +(other, e, v, prevNs, prevAttr, prevRefNs))
      }
    }

    // Consider And-semantics (self-joins)
    def postProcess(q: Query) = {
      val andAtoms: Seq[Atom] = model.elements.collect { case a@Atom(_, _, _, _, And(andValues), _, _) => a}
      if (andAtoms.size > 1) sys.error("[Model2Query:postProcess] For now, only 1 And-expression can be used. Found: " + andAtoms)
      if (andAtoms.size == 1) {
        val clauses = q.wh.clauses
        val andAtom = andAtoms.head
        val Atom(ns, attr0, _, _, And(andValues), _, _) = andAtom
        val attr = if (attr0.last == '_') attr0.init else attr0
        val unifyAttrs = model.elements.collect {
          case a@Atom(ns1, attr1, _, _, _, _, _) if a != andAtom => (ns1, if (attr1.last == '_') attr1.init else attr1)
        }
        val selfJoinClauses = andValues.zipWithIndex.tail.flatMap { case (andValue, i) =>

          // Todo: complete matches...
          def vi(v0: Var) = Var(v0.v + "_" + i)
          def queryValue(qv: QueryValue): QueryValue = qv match {
            case Var(v) => vi(Var(v))
            case _      => qv
          }
          def queryTerm(qt: QueryTerm): QueryTerm = qt match {
            case Rule(name, args, cls) => Rule(name, args map queryValue, cls map clause)
            case InVar(b, argss)       => InVar(binding(b), argss)
            case qv: QueryValue        => queryValue(qv)
            case other                 => qt
          }
          def binding(b: Binding) = b match {
            case ScalarBinding(v)     => ScalarBinding(vi(v))
            case CollectionBinding(v) => CollectionBinding(vi(v))
            case TupleBinding(vs)     => TupleBinding(vs map vi)
            case RelationBinding(vs)  => RelationBinding(vs map vi)
            case _                    => b
          }
          def clause(cl: Clause) = cl match {
            case dc: DataClause => dataCls(dc)
            case other          => resolve(other)
          }
          def dataCls(dc: DataClause) = dc match {
            case DataClause(ds, e, a@KW(ns2, attr2, _), v, tx, op) if (ns, attr) ==(ns2, attr2) =>
              // Add next And-value
              DataClause(ds, vi(e), a, Val(andValue), queryTerm(tx), queryTerm(op))

            case DataClause(ds, e, a@KW(ns2, attr2, _), v, tx, op) if unifyAttrs.contains((ns2, attr2)) =>
              // Keep value-position value to unify
              DataClause(ds, vi(e), a, v, queryTerm(tx), queryTerm(op))

            case DataClause(ds, e, a, v, tx, op) =>
              // Add i to variables
              DataClause(ds, vi(e), a, queryValue(v), queryTerm(tx), queryTerm(op))
          }
          def resolve(expr: QueryExpr): Clause = expr match {
            case dc@DataClause(ds, e, a, v, tx, op) => dataCls(dc)
            case RuleInvocation(name, args)         => RuleInvocation(name, args map queryTerm)
            case Funct(name, ins, outs)             => Funct(name, ins map queryTerm, binding(outs))
          }
          clauses map resolve
        }
        q.copy(wh = Where(q.wh.clauses ++ selfJoinClauses))
      } else q
    }

    val query = model.elements.foldLeft((Query(), "a", "b", "", "", "")) { case ((query, e, v, prevNs, prevAttr, prevRefNs), element) =>
      make(query, element, e, v, prevNs, prevAttr, prevRefNs)
    }._1

    postProcess(query)
  }
}