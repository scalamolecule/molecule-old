package molecule
package transform
import molecule.ast.model._
import molecule.ast.query._
import molecule.ops.QueryOps._
import molecule.util.Debug

object Model2Query {
  val x = Debug("Model2Query", 20, 9, false)
  def uri(t: String) = t == "java.net.URI"
  def u(t: String, v: String) = if (t == "java.net.URI") v else ""

  def apply(model: Model): Query = {

    def resolve(q: Query, e: String, v: String, element: Element): Query = {
      val (v1, v2, v3) = (v + 1, v + 2, v + 3)
      element match {

        // Manipulate ------------------------------------------------------------

        case Atom(_, _, _, _, Replace(_), _, _) => q
        case Atom(_, _, _, _, Remove(_), _, _)  => q


        // Schema ------------------------------------------------------------

        case Atom("?", "attr_", _, _, value, _, gs) => value match {
          case Distinct                  => q.attr(e, Var(v), v1, v2, gs)
          case Fn(fn, Some(i))           => q.attr(e, Var(v), v1, v2, gs)
          case Fn(fn, _)                 => q.attr(e, Var(v), v1, v2, gs)
          case Length(Some(Fn(fn, _)))   => q.attr(e, Var(v), v1, v2, gs).func("count", Var(v2), v3)
          case Length(_)                 => q.attr(e, Var(v), v1, v2, gs)
          case Eq(args) if args.size > 1 => q.attr(e, Var(v), v1, v2, gs)
          case Eq((arg: String) :: Nil)  => q.attr(e, Var(v3), v1, v2, gs).where(e, "?", "attr", Val(arg), "", Seq())
          case _                         => q.attr(e, Var(v), v1, v2, gs)
        }

        case Atom("?", "attr", _, _, value, _, gs) => value match {
          case Distinct                  => q.attr(e, Var(v), v1, v2, gs).find("distinct", Seq(), v2, gs, v)
          case Fn(fn, Some(i))           => q.attr(e, Var(v), v1, v2, gs).find(fn, Seq(i), v2, gs)
          case Fn(fn, _)                 => q.attr(e, Var(v), v1, v2, gs).find(fn, Seq(), v2, gs)
          case Length(Some(Fn(fn, _)))   => q.attr(e, Var(v), v1, v2, gs).func("count", Var(v2), v3).find(fn, Seq(), v3, gs)
          case Length(_)                 => q.attr(e, Var(v), v1, v2, gs).find("count", Seq(), v2, gs)
          case Eq(args) if args.size > 1 => q.attr(e, Var(v), v1, v2, gs).find(v2, gs, v)
          case Eq((arg: String) :: Nil)  => q.attr(e, Var(v3), v1, v2, gs).where(e, "?", "attr", Val(arg), "", Seq()).find(v2, gs, v3)
          case _                         => q.attr(e, Var(v), v1, v2, gs).find(v2, gs, v)
        }

        case Atom("ns_", "?", _, _, value, _, gs) => value match {
          case Qm                        => q.ns(e, Var(v), v1, v2, gs).in(v2, "ns", "?", v2)
          case Distinct                  => q.ns(e, Var(v), v1, v2, gs)
          case Fn(fn, Some(i))           => q.ns(e, Var(v), v1, v2, gs)
          case Fn(fn, _)                 => q.ns(e, Var(v), v1, v2, gs)
          case Length(Some(Fn(fn, _)))   => q.ns(e, Var(v), v1, v2, gs).func("count", Var(v2), v3)
          case Length(_)                 => q.ns(e, Var(v), v1, v2, gs)
          case Eq(args) if args.size > 1 => q.ns(e, Var(v), v1, v2, gs)
          case Eq((arg: String) :: Nil)  => q.ns(e, Var(v), v1, v2, gs).func("=", Seq(Var(v2), Val(arg)))
          case _                         => q.ns(e, Var(v), v1, v2, gs)
        }

        case Atom("ns", "?", _, _, value, _, gs) => value match {
          case Distinct                  => q.ns(e, Var(v), v1, v2, gs).find("distinct", Seq(), v2, gs, v)
          case Fn(fn, Some(i))           => q.ns(e, Var(v), v1, v2, gs).find(fn, Seq(i), v2, gs)
          case Fn(fn, _)                 => q.ns(e, Var(v), v1, v2, gs).find(fn, Seq(), v2, gs)
          case Length(Some(Fn(fn, _)))   => q.ns(e, Var(v), v1, v2, gs).func("count", Var(v2), v3).find(fn, Seq(), v3, gs)
          case Length(_)                 => q.ns(e, Var(v), v1, v2, gs).find("count", Seq(), v2, gs)
          case Eq(args) if args.size > 1 => q.ns(e, Var(v), v1, v2, gs).find(v2, gs, v)
          case Eq((arg: String) :: Nil)  => q.ns(e, Var(v), v1, v2, gs).func("=", Seq(Var(v2), Val(arg))).find(v2, gs, v3)
          case _                         => q.ns(e, Var(v), v1, v2, gs).find(v2, gs, v)
        }


        // Enum Atom_ (in where clause but not in output) --------------------------------------------

        case a0@Atom(_, attr0, _, _, value, Some(prefix), gs) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Qm                        => q.where(e, a, v, gs).in(v, a, Some(prefix), e)
            case Neq(Seq(Qm))              => q.enum(e, a, v, gs).compareTo("!=", a, v2, Var(v3), 1).in(v3, a)
            case Lt(Qm)                    => q.enum(e, a, v, gs).compareTo("<", a, v2, Var(v3), 1).in(v3, a)
            case Gt(Qm)                    => q.enum(e, a, v, gs).compareTo(">", a, v2, Var(v3), 1).in(v3, a)
            case Le(Qm)                    => q.enum(e, a, v, gs).compareTo("<=", a, v2, Var(v3), 1).in(v3, a)
            case Ge(Qm)                    => q.enum(e, a, v, gs).compareTo(">=", a, v2, Var(v3), 1).in(v3, a)
            case EnumVal                   => q.enum(e, a, v, gs)
            case Eq(args) if args.size > 1 => q.orRules(e, a, args.map(prefix + _), gs)
            case Eq(arg :: Nil)            => q.where(e, a, Val(prefix + arg), gs)
            case Neq(args)                 => q.enum(e, a, v, gs).compareTo("!=", a, v2, args map Val)
            case Fn("not", _)              => q.not(e, a, v, gs)
            case other                     =>
              sys.error(s"[Model2Query:resolve[Atom_]] Unresolved enum Atom_:\nAtom_  : $a\nElement: $other")
          }
        }

        // Atom_ (in where clause but not in output) --------------------------------------------

        case a0@Atom(_, attr0, _, _, value, _, gs) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Qm                       => q.where(e, a, v, gs).in(v, a)
            case Neq(Seq(Qm))             => q.where(e, a, v, gs).compareTo("!=", a, v, Var(v1)).in(v1, a)
            case Lt(Qm)                   => q.where(e, a, v, gs).compareTo("<", a, v, Var(v1)).in(v1, a)
            case Gt(Qm)                   => q.where(e, a, v, gs).compareTo(">", a, v, Var(v1)).in(v1, a)
            case Le(Qm)                   => q.where(e, a, v, gs).compareTo("<=", a, v, Var(v1)).in(v1, a)
            case Ge(Qm)                   => q.where(e, a, v, gs).compareTo(">=", a, v, Var(v1)).in(v1, a)
            case Fulltext(Seq(Qm))        => q.fulltext(e, a, v, Var(v1)).in(v1, a)
            case VarValue                 => q.where(e, a, v, gs).find(gs)
            case Eq((seq: Seq[_]) :: Nil) => q.orRules(e, a, seq, gs)
            case Eq(arg :: Nil)           => q.where(e, a, Val(arg), gs)
            case Eq(args)                 => q.orRules(e, a, args, gs)
            case And(args)                => q.where(e, a, Val(args.head), gs)
            case Neq(args)                => q.where(e, a, v, gs).compareTo("!=", a, v, args map Val)
            case Fn("not", _)             => q.not(e, a, v, gs)
            case Lt(arg)                  => q.where(e, a, v, gs).compareTo("<", a, v, Val(arg))
            case Gt(arg)                  => q.where(e, a, v, gs).compareTo(">", a, v, Val(arg))
            case Le(arg)                  => q.where(e, a, v, gs).compareTo("<=", a, v, Val(arg))
            case Ge(arg)                  => q.where(e, a, v, gs).compareTo(">=", a, v, Val(arg))
            case Fulltext(qv :: Nil)      => q.fulltext(e, a, v, Val(qv))
            case Fulltext(qvs)            => q.orRules(v1, a, qvs, gs).fulltext(e, a, v, Var(v1))
            case other                    =>
              sys.error(s"[Model2Query:resolve[Atom_]] Unresolved Atom_:\nAtom_  : $a\nElement: $other")
          }
        }


        // Enum Atom$ (optional) ------------------------------------------------------------

        case a0@Atom(_, attr0, _, 2, value, Some(prefix), gs) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case EnumVal => q.pullEnum(e, a)
            case other   => sys.error(s"[Model2Query:resolve[Enum Atom 2 optional]] Unresolved cardinality-many enum Atom:\nAtom   : $a\nElement: $other")
          }
        }

        case a0@Atom(_, attr0, _, 1, value, Some(prefix), gs) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case EnumVal => q.pullEnum(e, a)
            case other   => sys.error(s"[Model2Query:resolve[Enum Atom 1 optional]] Unresolved cardinality-one enum Atom:\nAtom   : $a\nElement: $other")
          }
        }


        // Atom$ (optional) ------------------------------------------------------------

        case a0@Atom(_, attr0, t, 2, value, _, gs) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case VarValue => q.pull(e, a)
            case other    => sys.error(s"[Model2Query:resolve[Atom 2 optional]] Unresolved cardinality-many Atom:\nAtom   : $a\nElement: $other")
          }
        }

        case a0@Atom(_, attr0, t, 1, value, _, gs) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case VarValue => q.pull(e, a)
            case other    => sys.error(s"[Model2Query:resolve[Atom 1 optional]] Unresolved cardinality-one Atom:\nAtom   : $a\nElement: $other")
          }
        }


        // Enum Atom (mandatory) ------------------------------------------------------------

        case a@Atom(_, _, _, 2, value, Some(prefix), gs) => value match {
          case Qm                       => q.find(v2, gs).enum(e, a, v, gs).in(v, a, Some(prefix))
          case Neq(Seq(Qm))             => q.find(v2, gs).enum(e, a, v, gs).compareTo("!=", a, v2, Var(v3), 1).in(v3, a)
          case Lt(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo("<", a, v2, Var(v3), 1).in(v3, a)
          case Gt(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo(">", a, v2, Var(v3), 1).in(v3, a)
          case Le(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo("<=", a, v2, Var(v3), 1).in(v3, a)
          case Ge(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo(">=", a, v2, Var(v3), 1).in(v3, a)
          case EnumVal                  => q.find("distinct", Seq(), v2, gs).enum(e, a, v, gs)
          case Eq((set: Set[_]) :: Nil) => q.find("distinct", Seq(), v2, gs).enum(e, a, v, gs).orRules(e, a, set.toSeq.map(prefix + _), gs)
          case Eq(args)                 => q.find("distinct", Seq(), v2, gs).enum(e, a, v, gs).orRules(e, a, args.map(prefix + _), gs)
          case other                    => sys.error(s"[Model2Query:resolve[Enum Atom 2]] Unresolved cardinality-many enum Atom:\nAtom   : $a\nElement: $other")
        }

        case a@Atom(_, _, _, 1, value, Some(prefix), gs) => value match {
          case Qm                       => q.find(v2, gs).enum(e, a, v, gs).in(v, a, Some(prefix))
          case Lt(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo("<", a, v2, Var(v3), 1).in(v3, a)
          case Gt(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo(">", a, v2, Var(v3), 1).in(v3, a)
          case Le(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo("<=", a, v2, Var(v3), 1).in(v3, a)
          case Ge(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo(">=", a, v2, Var(v3), 1).in(v3, a)
          case Neq(Seq(Qm))             => q.find(v2, gs).enum(e, a, v, gs).compareTo("!=", a, v2, Var(v3), 1).in(v3, a)
          case EnumVal                  => q.find(v2, gs).enum(e, a, v, gs)
          case Eq((seq: Seq[_]) :: Nil) => q.find(v2, gs).enum(e, a, v, gs).orRules(e, a, seq.map(prefix + _), gs)
          case Eq(arg :: Nil)           => q.find(v2, gs).enum(e, a, v, gs).where(e, a, Val(prefix + arg), gs)
          case Eq(args)                 => q.find(v2, gs).enum(e, a, v, gs).orRules(e, a, args.map(prefix + _), gs)
          case Neq(args)                => q.find(v2, gs).enum(e, a, v, gs).compareTo("!=", a, v2, args map Val)
          case Lt(arg)                  => q.find(v2, gs).enum(e, a, v, gs).compareTo("<", a, v2, Val(arg), 1)
          case Gt(arg)                  => q.find(v2, gs).enum(e, a, v, gs).compareTo(">", a, v2, Val(arg), 1)
          case Le(arg)                  => q.find(v2, gs).enum(e, a, v, gs).compareTo("<=", a, v2, Val(arg), 1)
          case Ge(arg)                  => q.find(v2, gs).enum(e, a, v, gs).compareTo(">=", a, v2, Val(arg), 1)
          case other                    => sys.error(s"[Model2Query:resolve[Enum Atom 1]] Unresolved cardinality-one enum Atom:\nAtom   : $a\nElement: $other")
        }


        // Atom (mandatory) -----------------------------------------------------------------

        case a@Atom(_, _, t, 2, value, _, gs) => value match {
          case Qm                       => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).in(v, a)
          case Neq(Seq(Qm))             => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).compareTo("!=", a, v, Var(v1)).in(v1, a)
          case Lt(Qm)                   => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).compareTo("<", a, v, Var(v1)).in(v1, a)
          case Gt(Qm)                   => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).compareTo(">", a, v, Var(v1)).in(v1, a)
          case Le(Qm)                   => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).compareTo("<=", a, v, Var(v1)).in(v1, a)
          case Ge(Qm)                   => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).compareTo(">=", a, v, Var(v1)).in(v1, a)
          case Fulltext(Seq(Qm))        => q.find("distinct", Seq(), v, gs).fulltext(e, a, v, Var(v1)).in(v1, a)
          case VarValue                 => q.find("distinct", Seq(), v, gs).where(e, a, v, gs)
          case Eq((set: Set[_]) :: Nil) => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).orRules(e, a, set.toSeq, gs, u(t, v))
          case Eq(arg :: Nil)           => q.find("distinct", Seq(), v, gs).where(e, a, Val(arg), gs).where(e, a, v, Seq())
          case Eq(args)                 => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).orRules(e, a, args, Nil, u(t, v))
          case Neq(args)                => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).compareTo("!=", a, v, args map Val)
          case Gt(arg)                  => q.find("distinct", Seq(), v, gs).where(e, a, v, gs).compareTo(">", a, v, Val(arg))
          case Fulltext(arg :: Nil)     => q.find("distinct", Seq(), v, gs).fulltext(e, a, v, Val(arg))
          case other                    => sys.error(s"[Model2Query:resolve[Atom 2]] Unresolved cardinality-many Atom:\nAtom   : $a\nElement: $other")
        }

        case a@Atom(_, _, t, 1, value, _, gs) => value match {
          case Qm                            => q.find(v, gs).where(e, a, v, gs).in(v, a)
          case Neq(Seq(Qm))                  => q.find(v, gs).where(e, a, v, gs).compareTo("!=", a, v, Var(v1)).in(v1, a)
          case Lt(Qm)                        => q.find(v, gs).where(e, a, v, gs).compareTo("<", a, v, Var(v1)).in(v1, a)
          case Gt(Qm)                        => q.find(v, gs).where(e, a, v, gs).compareTo(">", a, v, Var(v1)).in(v1, a)
          case Le(Qm)                        => q.find(v, gs).where(e, a, v, gs).compareTo("<=", a, v, Var(v1)).in(v1, a)
          case Ge(Qm)                        => q.find(v, gs).where(e, a, v, gs).compareTo(">=", a, v, Var(v1)).in(v1, a)
          case Fulltext(Seq(Qm))             => q.find(v, gs).fulltext(e, a, v, Var(v1)).in(v1, a)
          case EntValue                      => q.find(e, gs)
          case VarValue                      => q.find(v, gs).where(e, a, v, gs)
          case NoValue                       => q.find(NoVal, gs).where(e, a, v, gs)
          case BackValue(backNs)             => q.find(e, gs).where(v, a.ns, a.name, Var(e), backNs, gs)
          case Eq((seq: Seq[_]) :: Nil)      => q.find(v, gs).where(e, a, v, gs).orRules(e, a, seq, gs, u(t, v))
          case Eq(arg :: Nil) if uri(t)      => q.find(v, gs).func( s"""ground (java.net.URI. "$arg")""", Empty, v).where(e, a, v, Seq())
          case Eq(arg :: Nil)                => q.find(v, gs).where(e, a, Val(arg), gs).where(e, a, v, Seq())
          case Eq(args)                      => q.find(v, gs).where(e, a, v, gs).orRules(e, a, args, gs, u(t, v))
          case Neq(args)                     => q.find(v, gs).where(e, a, v, gs).compareTo("!=", a, v, args map Val)
          case Lt(arg)                       => q.find(v, gs).where(e, a, v, gs).compareTo("<", a, v, Val(arg))
          case Gt(arg)                       => q.find(v, gs).where(e, a, v, gs).compareTo(">", a, v, Val(arg))
          case Le(arg)                       => q.find(v, gs).where(e, a, v, gs).compareTo("<=", a, v, Val(arg))
          case Ge(arg)                       => q.find(v, gs).where(e, a, v, gs).compareTo(">=", a, v, Val(arg))
          case Fn("sum", _)                  => q.find("sum", Seq(), v, gs).where(e, a, v, gs).widh(e)
          case Fn("avg", _)                  => q.find("avg", Seq(), v, gs).where(e, a, v, gs).widh(e)
          case Fn(fn, Some(i))               => q.find(fn, Seq(i), v, gs).where(e, a, v, gs)
          case Fn(fn, _)                     => q.find(fn, Seq(), v, gs).where(e, a, v, gs)
          case Fulltext(arg :: Nil)          => q.find(v, gs).fulltext(e, a, v, Val(arg))
          case Fulltext(args)                => q.find(v, gs).fulltext(e, a, v, Var(v1)).orRules(v1, a, args)
          case Length(Some(Fn(fn, Some(i)))) => q.find(v2, gs).where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2)
          case Length(Some(Fn(fn, _)))       => q.find(fn, Seq(), v2, gs).where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2)
          case Length(_)                     => q.find(v2, gs).where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2)
          case other                         => sys.error(s"[Model2Query:resolve[Atom 1]] Unresolved cardinality-one Atom:\nAtom   : $a\nElement: $other")
        }


        // Graph -----------------------------------------------------------------

        case Bond(ns, refAttr, refNs) => q.ref(e, ns, refAttr, v, refNs)

        case Transitive(backRef, refAttr, refNs, depth, prevVar) => q.transitive(backRef, refAttr, prevVar, v, depth)

        case ReBond(backRef, refAttr, refNs, _, _) => q.ref(e, backRef, refAttr, v, refNs)


        // Meta -----------------------------------------------------------------

        case Meta(_, _, "e", _, Fn("count", Some(i)))   => q.find("count", Seq(i), e, Seq())
        case Meta(_, _, "e", _, Fn("count", _))         => q.find("count", Seq(), e, Seq())
        case Meta(_, _, "e", _, Length(Some(Fn(_, _)))) => q.find(e, Seq())
        case Meta(_, _, _, _, IndexVal)                 => q.find(v, Seq()).func("+", Seq(Var(e)), ScalarBinding(Var(v)))
        case Meta(_, _, _, _, EntValue)                 => q.find(e, Seq())
        case Meta(_, _, _, _, _)                        => q

        case unresolved => sys.error("[Model2Query:resolve] Unresolved model: " + unresolved)
      }
    }

    def make(query: Query, element: Element, e: String, v: String, prevNs: String, prevAttr: String, prevRefNs: String)
    : (Query, String, String, String, String, String) = {
      val w = (v.toCharArray.head + 1).toChar.toString
      val y = (v.toCharArray.head + 2).toChar.toString
      x(1, query, element, e, v, prevNs, prevAttr, prevRefNs)
      element match {
        case Atom(ns, attr, "a", _, _, _, _)                          => (resolve(query, e, v, element), e, w, ns, attr, "")
        case Atom(ns, attr, "ns", _, _, _, _)                         => (resolve(query, e, v, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if prevRefNs == "IndexVal" => (resolve(query, e, w, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if ns == prevNs            => (resolve(query, e, w, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if ns == prevAttr          => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if ns == prevRefNs         => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _)                            => (resolve(query, e, v, element), e, v, ns, attr, "")

        case Bond(ns, refAttr, refNs) if ns == prevNs    => (resolve(query, e, w, element), e, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs) if ns == prevAttr  => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs) if ns == prevRefNs => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs)                    => (resolve(query, e, v, element), e, v, ns, refAttr, refNs)

        case transitive@Transitive(backRef, refAttr, refNs, _, _) => {
          val (backRefE, backRefV) = query.wh.clauses.reverse.collectFirst {
            case DataClause(_, backE, a, Var(backV), _, _) if a.ns == backRef => (backE.v, backV)
          } getOrElse sys.error(s"[Model2Query:make(Transitive)] Can't find back reference namespace `$backRef` in query so far:\n$query")
          val backRefElement = transitive.copy(prevVar = backRefV)
          (resolve(query, backRefE, w, backRefElement), v, w, backRef, refAttr, refNs)
        }

        case rbe@ReBond(backRef, _, _, _, _) => {
          val backRefE = query.wh.clauses.reverse.collectFirst {
            case DataClause(_, backE, a, Var(backV), _, _) if a.ns == backRef => backE.v
          } getOrElse sys.error(s"[Model2Query:make(ReBond)] Can't find back reference namespace `$backRef` in query so far:\n$query\n$rbe")
          (query, backRefE, v, backRef, "", "")
        }

        case Group(b@Bond(ns, refAttr, refNs), elements) =>
          val (e2, elements2) = if (ns == "") (e, elements) else (w, b +: elements)
          val (q2, _, v2, ns2, attr2, refNs2) = elements2.foldLeft((query, e, v, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, (v2.toCharArray.head + 1).toChar.toString, ns2, attr2, refNs2)

        case Meta(ns, attr, "e", NoValue, Eq(Seq(id: Long)))            => x(2, query, element, prevRefNs, e, v, w, y); (resolve(query, id.toString, v, element), id.toString, v, ns, attr, prevRefNs)
        case Meta(ns, attr, "e", NoValue, IndexVal) if prevRefNs == ""  => x(3, query, element, prevRefNs, e, v, w, y); (resolve(query, e, v, element), e, w, ns, attr, "")
        case Meta(ns, attr, "e", NoValue, IndexVal)                     => x(4, query, element, prevRefNs, e, v, w, y); (resolve(query, v, w, element), v, w, ns, attr, "IndexVal")
        case Meta(ns, attr, "e", NoValue, _) if prevRefNs == ""         => x(5, query, element, prevRefNs, e, v, w, y); (resolve(query, e, v, element), e, w, ns, attr, "")
        case Meta(ns, attr, "e", NoValue, _) if prevRefNs == "IndexVal" => x(6, query, element, prevRefNs, e, v, w, y); (resolve(query, e, y, element), e, y, ns, attr, "")

        case Meta(ns, attr, "e", NoValue, EntValue)                     => x(7, query, element, prevRefNs, e, v, w, y); (resolve(query, v, w, element), v, w, ns, attr, "")
        case Meta(ns, attr, "e", NoValue, _)                            => x(8, query, element, prevRefNs, e, v, w, y); (resolve(query, v, w, element), e, w, ns, attr, "")
        case Meta(ns, attr, _, _, _)                                    => x(9, query, element, prevRefNs, e, v, w, y); (resolve(query, e, v, element), e, v, ns, attr, "")

        //        case Meta(ns, attr, "e", NoValue, Eq(Seq(id: Long)))    => x(2, query, element); (resolve(query, id.toString, v, element), id.toString, v, ns, attr, "")
        //        case Meta(ns, attr, "e", NoValue, _) if prevRefNs == "" => x(3, query, element); (resolve(query, e, v, element), e, v, ns, attr, "")
        //        case Meta(ns, attr, "e", NoValue, _)                    => x(4, query, element); (resolve(query, v, w, element), v, w, ns, attr, "")
        //        case Meta(ns, attr, _, _, _)                            => x(5, query, element); (resolve(query, e, v, element), e, v, ns, attr, "")

        case TxModel(elements) =>
          val (q2, e2, v2, ns2, attr2, refNs2) = elements.foldLeft((query, "tx", w, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, (v2.toCharArray.head + 1).toChar.toString, ns2, attr2, refNs2)

        case other => sys.error("[Model2Query:make] Unresolved query variables from model: " +(other, e, v, prevNs, prevAttr, prevRefNs))
      }
    }

    // Process And-semantics (self-joins)
    def postProcess(q: Query) = {
      val andAtoms: Seq[Atom] = model.elements.collect { case a@Atom(_, _, _, _, And(andValues), _, _) => a }
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

    val query = model.elements.foldLeft((Query(), "a", "b", "", "", "")) { case ((query_, e, v, prevNs, prevAttr, prevRefNs), element) =>
      make(query_, element, e, v, prevNs, prevAttr, prevRefNs)
    }._1

    x(20, query, query.datalog)

    postProcess(query)
  }
}