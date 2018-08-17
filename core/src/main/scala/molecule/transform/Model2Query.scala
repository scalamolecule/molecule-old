package molecule
package transform
import molecule.ast.model._
import molecule.ast.query.{Val, _}
import molecule.ops.QueryOps._
import molecule.transform.exception.Model2QueryException
import molecule.util.{Debug, Helpers}


/** Model to Query transformation.
  * <br><br>
  * Second transformation in Molecules series of transformations from
  * custom boilerplate DSL constructs to Datomic queries:
  * <br><br>
  * Custom DSL molecule --> Model --> Query --> Datomic query string
  *
  * @see [[http://www.scalamolecule.org/dev/transformation/]]
  **/
object Model2Query extends Helpers {
  val x = Debug("Model2Query", 1, 19, false)
  def uri(t: String) = t.contains("java.net.URI")
  def u(t: String, v: String) = if (t.contains("java.net.URI")) v else ""

  def apply(model: Model): Query = {

    def resolve(q: Query, e: String, v: String, element: Element): Query = {
      val (v1, v2, v3) = (v + 1, v + 2, v + 3)
      element match {

        // Manipulation (not relevant to queries) ----------------------------

        case Atom(_, _, _, _, AssertValue(_), _, _, _)     => q
        case Atom(_, _, _, _, ReplaceValue(_), _, _, _)    => q
        case Atom(_, _, _, _, RetractValue(_), _, _, _)    => q
        case Atom(_, _, _, _, AssertMapPairs(_), _, _, _)  => q
        case Atom(_, _, _, _, ReplaceMapPairs(_), _, _, _) => q
        case Atom(_, _, _, _, RetractMapKeys(_), _, _, _)  => q


        // Generic =================================================================================

        case Atom("?", "attr_", _, _, value, _, gs, _) => value match {
          case Distinct                  => q.attr(e, Var(v), v1, v2, gs)
          case Fn(fn, Some(i))           => q.attr(e, Var(v), v1, v2, gs)
          case Fn(fn, _)                 => q.attr(e, Var(v), v1, v2, gs)
          case Length(Some(Fn(fn, _)))   => q.attr(e, Var(v), v1, v2, gs).func("count", Var(v2), v3)
          case Length(_)                 => q.attr(e, Var(v), v1, v2, gs)
          case Eq(args) if args.size > 1 => q.attr(e, Var(v), v1, v2, gs)
          case Eq((arg: String) :: Nil)  => q.attr(e, Var(v3), v1, v2, gs).func("=", Seq(Var(v3), Val(arg)))
          case _                         => q.attr(e, Var(v), v1, v2, gs)
        }

        case Atom("?", "attr", _, _, value, _, gs, _) => value match {
          case Distinct                  => q.attr(e, Var(v), v1, v2, gs).find("distinct", Nil, v2, gs, v).widh(e)
          case Fn(fn, Some(i))           => q.attr(e, Var(v), v1, v2, gs).find(fn, Seq(i), v2, gs)
          case Fn(fn, _)                 => q.attr(e, Var(v), v1, v2, gs).find(fn, Nil, v2, gs)
          case Length(Some(Fn(fn, _)))   => q.attr(e, Var(v), v1, v2, gs).func("count", Var(v2), v3).find(fn, Nil, v3, gs)
          case Length(_)                 => q.attr(e, Var(v), v1, v2, gs).find("count", Nil, v2, gs)
          case Eq(args) if args.size > 1 => q.attr(e, Var(v), v1, v2, gs).find(v2, gs, v)
          case Eq((arg: String) :: Nil)  => q.attr(e, Var(v3), v1, v2, gs).func("=", Seq(Var(v3), Val(arg))).find(v2, gs, v3)
          case _                         => q.attr(e, Var(v), v1, v2, gs).find(v2, gs, v)
        }

        case Atom("ns_", "?", _, _, value, _, gs, _) => value match {
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

        case Atom("ns", "?", _, _, value, _, gs, _) => value match {
          case Distinct                  => q.ns(e, Var(v), v1, v2, gs).find("distinct", Nil, v2, gs, v)
          case Fn(fn, Some(i))           => q.ns(e, Var(v), v1, v2, gs).find(fn, Seq(i), v2, gs)
          case Fn(fn, _)                 => q.ns(e, Var(v), v1, v2, gs).find(fn, Nil, v2, gs)
          case Length(Some(Fn(fn, _)))   => q.ns(e, Var(v), v1, v2, gs).func("count", Var(v2), v3).find(fn, Nil, v3, gs)
          case Length(_)                 => q.ns(e, Var(v), v1, v2, gs).find("count", Nil, v2, gs)
          case Eq(args) if args.size > 1 => q.ns(e, Var(v), v1, v2, gs).find(v2, gs, v)
          case Eq((arg: String) :: Nil)  => q.ns(e, Var(v), v1, v2, gs).func("=", Seq(Var(v2), Val(arg))).find(v2, gs, v3)
          case _                         => q.ns(e, Var(v), v1, v2, gs).find(v2, gs, v)
        }


        // Keyed mapped attributes ===============================================================

        // Keyed map Atom$ (optional)

        case a0@Atom(_, attr0, t, 4, value, _, gs, _) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.slice(0, attr0.length - 2))
          value match {
            case VarValue => q.pull(e, a)
            case other    => throw new Model2QueryException("Unresolved optional mapped Atom$:\nAtom$   : " + s"$a\nElement: $other")
          }
        }

        // Keyed map Atom_ (tacit)

        case a0@Atom(_, attr0, t, 4, value, _, gs, key :: Nil) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.slice(0, attr0.length - 2))
          value match {
            case Qm => q
              .in(v + "Value", a)
              .where(e, a, v, gs)
              .func("str", Seq(Val(s"($key)@("), Var(v + "Value"), Val(")")), ScalarBinding(Var(v1)))
              .func(".matches ^String", Seq(Var(v), Var(v1)))

            case Fulltext(Seq(Qm)) => q
              .in(v + "Value", a)
              .where(e, a, v, gs)
              .func("str", Seq(Val(s"($key)@("), Var(v + "Value"), Val(")")), ScalarBinding(Var(v1)))
              .func(".matches ^String", Seq(Var(v), Var(v1)))

            case Neq(Seq(Qm)) => q
              .in(v + "Value", a)
              .where(e, a, v, gs)
              .func("str", Seq(Val(s"(?!($key)@("), Var(v + "Value"), Val(")$).*")), ScalarBinding(Var(v1)))
              .func(".matches ^String", Seq(Var(v), Var(v1)))

            case Gt(Qm) => q.mapIn2(e, a, v, gs).mapInCompareToK(">", e, a, v, key, gs)
            case Ge(Qm) => q.mapIn2(e, a, v, gs).mapInCompareToK(">=", e, a, v, key, gs)
            case Lt(Qm) => q.mapIn2(e, a, v, gs).mapInCompareToK("<", e, a, v, key, gs)
            case Le(Qm) => q.mapIn2(e, a, v, gs).mapInCompareToK("<=", e, a, v, key, gs)

            case Gt(arg) => q.mapCompareTo(">", e, a, v, Seq(key), arg, gs)
            case Ge(arg) => q.mapCompareTo(">=", e, a, v, Seq(key), arg, gs)
            case Lt(arg) => q.mapCompareTo("<", e, a, v, Seq(key), arg, gs)
            case Le(arg) => q.mapCompareTo("<=", e, a, v, Seq(key), arg, gs)

            case VarValue => q
              .where(e, a, v, gs)
              .func(".startsWith ^String", Seq(Var(v), Val(key + "@")), NoBinding)
              .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v1)))
              .func("second", Seq(Var(v1)), ScalarBinding(Var(v2)))

            case Fulltext(args) => q
              .where(e, a, v, gs)
              .func(".matches ^String", Seq(Var(v), Val("(" + key + ")@.*(" + args.map(f).mkString("|") + ").*$")))

            case Eq(arg :: Nil) => q
              .where(e, a, v, gs)
              .func(".matches ^String", Seq(Var(v), Val(s"($key)@" + f(arg))))

            case Eq(args) => q
              .where(e, a, v, gs)
              .func(".matches ^String", Seq(Var(v), Val("(" + key + ")@(" + args.map(f).mkString("|") + ")$")))

            case other => throw new Model2QueryException(s"Unresolved tacit mapped Atom_:\nAtom_   : $a\nElement: $other")
          }
        }

        // Keyed map Atom (mandatory)

        case a0@Atom(_, attr0, t, 4, value, _, gs, key :: Nil) => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Qm => q
              .find(v3, gs)
              .in(v + "Value", a)
              .where(e, a, v, gs)
              .func("str", Seq(Val(s"($key)@("), Var(v + "Value"), Val(")")), ScalarBinding(Var(v1)))
              .func(".matches ^String", Seq(Var(v), Var(v1)))
              .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 2)))
              .func("second", Seq(Var(v + 2)), ScalarBinding(Var(v + 3)))

            case Fulltext(Seq(Qm)) => q
              .find(v3, gs)
              .in(v + "Value", a)
              .where(e, a, v, gs)
              .func("str", Seq(Val(s"($key)@("), Var(v + "Value"), Val(")")), ScalarBinding(Var(v1)))
              .func(".matches ^String", Seq(Var(v), Var(v1)))
              .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 2)))
              .func("second", Seq(Var(v + 2)), ScalarBinding(Var(v + 3)))

            case Neq(Seq(Qm)) => q
              .find(v3, gs)
              .in(v + "Value", a)
              .where(e, a, v, gs)
              .func("str", Seq(Val(s"(?!($key)@("), Var(v + "Value"), Val(")$).*")), ScalarBinding(Var(v1)))
              .func(".matches ^String", Seq(Var(v), Var(v1)))
              .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 2)))
              .func("second", Seq(Var(v + 2)), ScalarBinding(Var(v + 3)))

            case Gt(Qm) => q.find(v2, gs).mapIn2(e, a, v, gs).mapInCompareToK(">", e, a, v, key, gs)
            case Ge(Qm) => q.find(v2, gs).mapIn2(e, a, v, gs).mapInCompareToK(">=", e, a, v, key, gs)
            case Lt(Qm) => q.find(v2, gs).mapIn2(e, a, v, gs).mapInCompareToK("<", e, a, v, key, gs)
            case Le(Qm) => q.find(v2, gs).mapIn2(e, a, v, gs).mapInCompareToK("<=", e, a, v, key, gs)

            case Gt(arg) => q.find(v2, gs).mapCompareTo(">", e, a, v, Seq(key), arg, gs)
            case Ge(arg) => q.find(v2, gs).mapCompareTo(">=", e, a, v, Seq(key), arg, gs)
            case Lt(arg) => q.find(v2, gs).mapCompareTo("<", e, a, v, Seq(key), arg, gs)
            case Le(arg) => q.find(v2, gs).mapCompareTo("<=", e, a, v, Seq(key), arg, gs)

            case VarValue => q
              .find(v2, gs)
              .where(e, a, v, gs)
              .func(".matches ^String", Seq(Var(v), Val(s"($key)@.*")))
              .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v1)))
              .func("second", Seq(Var(v1)), ScalarBinding(Var(v2)))

            case Fulltext(args) => q
              .find(v2, gs)
              .where(e, a, v, gs)
              .func(".matches ^String", Seq(Var(v), Val("(" + key + ")@.*(" + args.map(f).mkString("|") + ").*$")))
              .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 1)))
              .func("second", Seq(Var(v + 1)), ScalarBinding(Var(v + 2)))

            case Eq(arg :: Nil) => q
              .find(v2, gs)
              .where(e, a, v, gs)
              .func(".matches ^String", Seq(Var(v), Val(s"($key)@" + f(arg))))
              .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 1)))
              .func("second", Seq(Var(v + 1)), ScalarBinding(Var(v + 2)))

            case Eq(args) => q
              .find(v2, gs)
              .where(e, a, v, gs)
              .func(".matches ^String", Seq(Var(v), Val("(" + key + ")@(" + args.map(f).mkString("|") + ")$")))
              .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 1)))
              .func("second", Seq(Var(v + 1)), ScalarBinding(Var(v + 2)))

            case Neq(args) => q.find(v, gs).where(e, a, v, gs).matches(v, Seq(key), "(?!(" + args.map(f).mkString("|") + ")$).*")

            case other => throw new Model2QueryException(s"Unresolved mapped Atom:\nAtom   : $a\nElement: $other")
          }
        }


        // Mapped attributes ===============================================================

        // Map Atom$ (optional)

        case a0@Atom(_, attr0, t, 3, value, _, gs, _) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case VarValue                        => q.pull(e, a)
            case Fn("not", _)                    => q.not(e, a, v, gs)
            case MapEq(pairs) if pairs.size == 1 => q.findD(v, gs).where(e, a, v, gs).matches(v, "(" + pairs.head._1 + ")@(" + pairs.head._2 + ")$")
            case MapEq(pairs)                    => q.findD(v, gs).where(e, a, v, gs).mappings(v, a, pairs)
            case other                           => throw new Model2QueryException("Unresolved optional mapped Atom$:\nAtom$   : " + s"$a\nElement: $other")
          }
        }

        // Map Atom_ (tacit)

        case a0@Atom(_, attr0, t, 3, value, _, gs, keys) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Qm                              => q.mapIn(e, a, v, gs).matchRegEx(v, Seq(Val("("), Var(v + "Key"), Val(")@("), Var(v + "Value"), Val(")")))
            case Fulltext(Seq(Qm))               => q.mapIn(e, a, v, gs).matchRegEx(v, Seq(Val("("), Var(v + "Key"), Val(")@("), Var(v + "Value"), Val(")")))
            case Neq(Seq(Qm))                    => q.mapIn(e, a, v, gs).matchRegEx(v, Seq(Val("(?!("), Var(v + "Key"), Val(")@("), Var(v + "Value"), Val(")$).*")))
            case Gt(Qm)                          => q.mapIn(e, a, v, gs).mapInCompareTo(">", e, a, v, gs)
            case Ge(Qm)                          => q.mapIn(e, a, v, gs).mapInCompareTo(">=", e, a, v, gs)
            case Lt(Qm)                          => q.mapIn(e, a, v, gs).mapInCompareTo("<", e, a, v, gs)
            case Le(Qm)                          => q.mapIn(e, a, v, gs).mapInCompareTo("<=", e, a, v, gs)
            case Gt(arg)                         => q.mapCompareTo(">", e, a, v, keys, arg, gs)
            case Ge(arg)                         => q.mapCompareTo(">=", e, a, v, keys, arg, gs)
            case Lt(arg)                         => q.mapCompareTo("<", e, a, v, keys, arg, gs)
            case Le(arg)                         => q.mapCompareTo("<=", e, a, v, keys, arg, gs)
            case VarValue                        => q.where(e, a, v, gs)
            case Fulltext(arg :: Nil)            => q.where(e, a, v, gs).matches(v, keys, ".*" + f(arg) + ".*")
            case Fulltext(args)                  => q.where(e, a, v, gs).matches(v, keys, ".*(" + args.map(f).mkString("|") + ").*")
            case Eq((set: Set[_]) :: Nil)        => q.where(e, a, v, gs).matches(v, keys, "(" + set.toSeq.map(f).mkString("|") + ")$")
            case Eq(arg :: Nil)                  => q.where(e, a, v, gs).matches(v, keys, "(" + f(arg) + ")")
            case Eq(args)                        => q.where(e, a, v, gs).matches(v, keys, "(" + args.map(f).mkString("|") + ")$")
            case Neq(args)                       => q.where(e, a, v, gs).matches(v, keys, "(?!(" + args.map(f).mkString("|") + ")$).*")
            case MapKeys(arg :: Nil)             => q.where(e, a, v, gs).func(".startsWith ^String", Seq(Var(v), Val(arg + "@")), NoBinding)
            case MapKeys(args)                   => q.where(e, a, v, gs).matches(v, "(" + args.mkString("|") + ")@.*")
            case MapEq(pairs) if pairs.size == 1 => q.where(e, a, v, gs).matches(v, "(" + pairs.head._1 + ")@(" + pairs.head._2 + ")")
            case MapEq(pairs)                    => q.where(e, a, v, gs).mappings(v, a, pairs.toSeq)
            case And(args)                       => q.where(e, a, v, gs).matches(v, keys, "(" + args.head + ")$") // (post-processed)
            case Fn("not", _)                    => q.not(e, a, v, gs)
            case other                           => throw new Model2QueryException(s"Unresolved tacit mapped Atom_:\nAtom_   : $a\nElement: $other")
          }
        }

        // Map Atom (mandatory)

        case a@Atom(_, _, t, 3, value, _, gs, keys) => value match {
          case Qm                              => q.findD(v, gs).mapIn(e, a, v, gs).matchRegEx(v, Seq(Val("("), Var(v + "Key"), Val(")@("), Var(v + "Value"), Val(")")))
          case Fulltext(Seq(Qm))               => q.findD(v, gs).mapIn(e, a, v, gs).matchRegEx(v, Seq(Val(".+@("), Var(v + "Value"), Val(")")))
          case Neq(Seq(Qm))                    => q.findD(v, gs).mapIn(e, a, v, gs).matchRegEx(v, Seq(Val("(?!("), Var(v + "Key"), Val(")@("), Var(v + "Value"), Val(")$).*")))
          case Gt(Qm)                          => q.findD(v, gs).mapIn(e, a, v, gs).mapInCompareTo(">", e, a, v, gs)
          case Ge(Qm)                          => q.findD(v, gs).mapIn(e, a, v, gs).mapInCompareTo(">=", e, a, v, gs)
          case Lt(Qm)                          => q.findD(v, gs).mapIn(e, a, v, gs).mapInCompareTo("<", e, a, v, gs)
          case Le(Qm)                          => q.findD(v, gs).mapIn(e, a, v, gs).mapInCompareTo("<=", e, a, v, gs)
          case Gt(arg)                         => q.findD(v, gs).mapCompareTo(">", e, a, v, keys, arg, gs)
          case Ge(arg)                         => q.findD(v, gs).mapCompareTo(">=", e, a, v, keys, arg, gs)
          case Lt(arg)                         => q.findD(v, gs).mapCompareTo("<", e, a, v, keys, arg, gs)
          case Le(arg)                         => q.findD(v, gs).mapCompareTo("<=", e, a, v, keys, arg, gs)
          case VarValue                        => q.findD(v, gs).where(e, a, v, gs)
          case Fulltext(arg :: Nil)            => q.findD(v, gs).where(e, a, v, gs).matches(v, keys, ".*" + f(arg) + ".*")
          case Fulltext(args)                  => q.findD(v, gs).where(e, a, v, gs).matches(v, keys, ".*(" + args.map(f).mkString("|") + ").*")
          case Eq((set: Set[_]) :: Nil)        => q.findD(v, gs).where(e, a, v, gs).matches(v, keys, "(" + set.toSeq.map(f).mkString("|") + ")$")
          case Eq(arg :: Nil)                  => q.findD(v, gs).where(e, a, v, gs).matches(v, keys, "(" + f(arg) + ")")
          case Eq(args)                        => q.findD(v, gs).where(e, a, v, gs).matches(v, keys, "(" + args.map(f).mkString("|") + ")$")
          case Neq(args)                       => q.findD(v, gs).where(e, a, v, gs).matches(v, keys, "(?!(" + args.map(f).mkString("|") + ")$).*")
          case MapKeys(arg :: Nil)             => q.findD(v, gs).where(e, a, v, gs).func(".startsWith ^String", Seq(Var(v), Val(arg + "@")), NoBinding)
          case MapKeys(args)                   => q.findD(v, gs).where(e, a, v, gs).matches(v, "(" + args.mkString("|") + ")@.*")
          case MapEq(pairs) if pairs.size == 1 => q.findD(v, gs).where(e, a, v, gs).matches(v, "(" + pairs.head._1 + ")@(" + pairs.head._2 + ")$")
          case MapEq(pairs)                    => q.findD(v, gs).where(e, a, v, gs).mappings(v, a, pairs)
          case And(args)                       => q.findD(v, gs).whereAnd(e, a, v, args)
          case other                           => throw new Model2QueryException(s"Unresolved mapped Atom:\nAtom   : $a\nElement: $other")
        }


        // Enum ===================================================================================

        // Enum Atom$ (optional)

        case a0@Atom(_, attr0, _, 2, value, Some(prefix), gs, _) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case EnumVal      => q.pullEnum(e, a)
            case Fn("not", _) => q.not(e, a, v, gs) // None
            case Eq(args)     => q.findD(v2, gs).enum(e, a, v, gs).orRules(e, a, args.map(prefix + _), gs)
            case other        => throw new Model2QueryException("Unresolved optional cardinality-many enum Atom$:\nAtom$   : " + s"$a\nElement: $other")
          }
        }

        case a0@Atom(_, attr0, _, 1, value, Some(prefix), gs, _) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case EnumVal        => q.pullEnum(e, a)
            case Fn("not", _)   => q.not(e, a, v, gs) // None
            case Eq(arg :: Nil) => q.find(v2, gs).enum(e, a, v, gs).where(e, a, Val(prefix + arg), gs)
            case Eq(args)       => q.find(v2, gs).enum(e, a, v, gs).orRules(e, a, args.map(prefix + _), gs)
            case other          => throw new Model2QueryException("Unresolved optional cardinality-one enum Atom$:\nAtom$   : " + s"$a\nElement: $other")
          }
        }

        // Enum Atom_ (tacit) - in where clause but not in output

        case a0@Atom(_, attr0, _, card, value, Some(prefix), gs, _) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Qm                                         => q.where(e, a, v, gs).in(v, a, Some(prefix), e)
            case Neq(Seq(Qm))                               => q.enum(e, a, v, gs).compareTo("!=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
            case Gt(Qm)                                     => q.enum(e, a, v, gs).compareTo(">", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
            case Ge(Qm)                                     => q.enum(e, a, v, gs).compareTo(">=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
            case Lt(Qm)                                     => q.enum(e, a, v, gs).compareTo("<", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
            case Le(Qm)                                     => q.enum(e, a, v, gs).compareTo("<=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
            case EnumVal                                    => q.enum(e, a, v, gs)
            case Eq((set: Set[_]) :: Nil)                   => q.enum(e, a, v, gs).whereAnd(e, a, v, set.toSeq.map(prefix + _))
            case And(args) if card == 2                     => q.enum(e, a, v, gs).whereAnd(e, a, v, args.map(prefix + _))
            case Eq(arg :: Nil)                             => q.where(e, a, Val(prefix + arg), gs)
            case Eq(args) if args.head.isInstanceOf[Set[_]] => throw new Model2QueryException(s"[Enum Atom_ (tacit)] Can only apply a single Set of values for enum attribute :${a.ns}.${a.name}_")
            case Eq(args)                                   => q.orRules(e, a, args.map(prefix + _), gs)
            case Neq(args)                                  => q.enum(e, a, v, gs).compareToMany("!=", a, v2, args)
            case Gt(arg)                                    => q.enum(e, a, v, gs).compareTo(">", a, v2, Val(arg), 1)
            case Ge(arg)                                    => q.enum(e, a, v, gs).compareTo(">=", a, v2, Val(arg), 1)
            case Lt(arg)                                    => q.enum(e, a, v, gs).compareTo("<", a, v2, Val(arg), 1)
            case Le(arg)                                    => q.enum(e, a, v, gs).compareTo("<=", a, v2, Val(arg), 1)
            case Fn("not", _)                               => q.not(e, a, v, gs)
            case other                                      => throw new Model2QueryException(s"Unresolved tacit enum Atom_:\nAtom_  : $a\nElement: $other")
          }
        }

        // Enum Atom (mandatory)

        case a@Atom(_, _, _, 2, value, Some(prefix), gs, _) => value match {
          case Qm                                         => q.findD(v2, gs).enum(e, a, v, gs).in(v, a, Some(prefix), e)
          case Neq(Seq(Qm))                               => q.findD(v2, gs).enum(e, a, v, gs).compareTo("!=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case Gt(Qm)                                     => q.findD(v2, gs).enum(e, a, v, gs).compareTo(">", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case Ge(Qm)                                     => q.findD(v2, gs).enum(e, a, v, gs).compareTo(">=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case Lt(Qm)                                     => q.findD(v2, gs).enum(e, a, v, gs).compareTo("<", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case Le(Qm)                                     => q.findD(v2, gs).enum(e, a, v, gs).compareTo("<=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case EnumVal                                    => q.findD(v2, gs).enum(e, a, v, gs)
          case Eq((set: Set[_]) :: Nil)                   => q.findD(v2, gs).enum(e, a, v, gs).whereAnd(e, a, v, set.toSeq.map(prefix + _))
          case Eq(args) if args.head.isInstanceOf[Set[_]] => throw new Model2QueryException(s"[Enum Atom (mandatory)] Can only apply a single Set of values for enum attribute :${a.ns}.${a.name}")
          case Eq(args)                                   => q.findD(v2, gs).enum(e, a, v, gs).orRules(e, a, args.map(prefix + _), gs)
          case And(args)                                  => q.findD(v2, gs).whereAndEnum(e, a, v, prefix, args)
          case Neq(args)                                  => q.findD(v2, gs).enum(e, a, v, gs).compareToMany("!=", a, v2, args.map(prefix + _))
          case Gt(arg)                                    => q.findD(v2, gs).enum(e, a, v, gs).compareTo(">", a, v2, Val(arg), 1)
          case Ge(arg)                                    => q.findD(v2, gs).enum(e, a, v, gs).compareTo(">=", a, v2, Val(arg), 1)
          case Lt(arg)                                    => q.findD(v2, gs).enum(e, a, v, gs).compareTo("<", a, v2, Val(arg), 1)
          case Le(arg)                                    => q.findD(v2, gs).enum(e, a, v, gs).compareTo("<=", a, v2, Val(arg), 1)
          case other                                      => throw new Model2QueryException(s"Unresolved cardinality-many enum Atom:\nAtom   : $a\nElement: $other")
        }

        case a@Atom(_, _, _, 1, value, Some(prefix), gs, _) => value match {
          case Qm                       => q.find(v2, gs).enum(e, a, v, gs).in(v, a, Some(prefix), e)
          case Gt(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo(">", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case Ge(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo(">=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case Lt(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo("<", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case Le(Qm)                   => q.find(v2, gs).enum(e, a, v, gs).compareTo("<=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case Neq(Seq(Qm))             => q.find(v2, gs).enum(e, a, v, gs).compareTo("!=", a, v2, Var(v3), 1).in(v3, a, Some(prefix), e)
          case EnumVal                  => q.find(v2, gs).enum(e, a, v, gs)
          case Eq((seq: Seq[_]) :: Nil) => q.find(v2, gs).enum(e, a, v, gs).orRules(e, a, seq.map(prefix + _), gs)
          case Eq(arg :: Nil)           => q.find(v2, gs).enum(e, a, v, gs).where(e, a, Val(prefix + arg), gs)
          case Eq(args)                 => q.find(v2, gs).enum(e, a, v, gs).orRules(e, a, args.map(prefix + _), gs)
          case Neq(args)                => q.find(v2, gs).enum(e, a, v, gs).compareToMany("!=", a, v2, args)
          case Gt(arg)                  => q.find(v2, gs).enum(e, a, v, gs).compareTo(">", a, v2, Val(arg), 1)
          case Ge(arg)                  => q.find(v2, gs).enum(e, a, v, gs).compareTo(">=", a, v2, Val(arg), 1)
          case Lt(arg)                  => q.find(v2, gs).enum(e, a, v, gs).compareTo("<", a, v2, Val(arg), 1)
          case Le(arg)                  => q.find(v2, gs).enum(e, a, v, gs).compareTo("<=", a, v2, Val(arg), 1)
          case other                    => throw new Model2QueryException(s"Unresolved cardinality-one enum Atom:\nAtom   : $a\nElement: $other")
        }


        // Atom ===================================================================================

        // Atom$ (optional)

        case a0@Atom(_, attr0, t, 2, value, _, gs, _) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case VarValue                 => q.pull(e, a)
            case Fn("not", _)             => q.not(e, a, v, gs) // None
            case Eq(arg :: Nil) if uri(t) => q.findD(v, gs).func( s"""ground (java.net.URI. "$arg")""", Empty, v).where(e, a, v, Nil)
            case Eq(arg :: Nil)           => q.findD(v, gs).where(e, a, Val(arg), gs).where(e, a, v, Nil)
            case Eq(args)                 => q.findD(v, gs).where(e, a, v, gs).orRules(e, a, args, Nil, u(t, v))
            case other                    => throw new Model2QueryException("Unresolved optional cardinality-many Atom$:\nAtom$   : " + s"$a0\nElement: $other")
          }
        }

        case a0@Atom(_, attr0, t, 1, value, _, gs, _) if attr0.last == '$' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case VarValue                 => q.pull(e, a)
            case Fn("not", _)             => q.not(e, a, v, gs) // None
            case Eq(arg :: Nil) if uri(t) => q.find(v, gs).func( s"""ground (java.net.URI. "$arg")""", Empty, v).where(e, a, v, Nil)
            case Eq(arg :: Nil)           => q.find(v, gs).where(e, a, Val(arg), gs).where(e, a, v, Nil)
            case Eq(args)                 => q.find(v, gs).where(e, a, v, gs).orRules(e, a, args, gs, u(t, v))
            case other                    => throw new Model2QueryException("Unresolved optional cardinality-one Atom$:\nAtom$   : " + s"$a0\nElement: $other")
          }
        }

        // Atom_ (tacit)

        case a0@Atom(_, attr0, t, card, value, _, gs, _) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          value match {
            case Qm                                         => q.where(e, a, v, gs).in(v, a, None, e)
            case Neq(Seq(Qm))                               => q.where(e, a, v, gs).compareTo("!=", a, v, Var(v1)).in(v1, a, None, e)
            case Gt(Qm)                                     => q.where(e, a, v, gs).compareTo(">", a, v, Var(v1)).in(v1, a, None, e)
            case Ge(Qm)                                     => q.where(e, a, v, gs).compareTo(">=", a, v, Var(v1)).in(v1, a, None, e)
            case Lt(Qm)                                     => q.where(e, a, v, gs).compareTo("<", a, v, Var(v1)).in(v1, a, None, e)
            case Le(Qm)                                     => q.where(e, a, v, gs).compareTo("<=", a, v, Var(v1)).in(v1, a, None, e)
            case Fulltext(Seq(Qm))                          => q.fulltext(e, a, v, Var(v1)).in(v1, a, None, e)
            case VarValue                                   => q.where(e, a, v, gs).find(gs)
            case Eq(Nil)                                    => q.not(e, a, v, gs)
            case Eq((set: Set[_]) :: Nil)                   => q.whereAnd(e, a, v, set.toSeq, u(t, v))
            case Eq(arg :: Nil) if uri(t)                   => q.where(e, a, v, gs).func( s"""ground (java.net.URI. "$arg")""", Empty, v).find(gs)
            case Eq(arg :: Nil)                             => q.where(e, a, Val(arg), gs).find(gs)
            case Eq(args) if args.head.isInstanceOf[Set[_]] => throw new Model2QueryException(s"[Atom_ (tacit)] Can only apply a single Set of values for attribute :${a.ns}/${a.name}_")
            case Eq(args)                                   => q.orRules(e, a, args, gs, u(t, v))
            case Neq(args)                                  => q.where(e, a, v, gs).compareToMany("!=", a, v, args)
            case Gt(arg)                                    => q.where(e, a, v, gs).compareTo(">", a, v, Val(arg))
            case Ge(arg)                                    => q.where(e, a, v, gs).compareTo(">=", a, v, Val(arg))
            case Lt(arg)                                    => q.where(e, a, v, gs).compareTo("<", a, v, Val(arg))
            case Le(arg)                                    => q.where(e, a, v, gs).compareTo("<=", a, v, Val(arg))
            case And(args) if card == 2                     => q.whereAnd(e, a, v, args, u(t, v))
            case And(args)                                  => q.where(e, a, Val(args.head), gs)
            case Fn("not", _)                               => q.not(e, a, v, gs)
            case Fn("unify", _)                             => q.where(e, a, v, gs)
            case Fulltext(qv :: Nil)                        => q.fulltext(e, a, v, Val(qv))
            case Fulltext(qvs)                              => q.orRules(v1, a, qvs, gs).fulltext(e, a, v, Var(v1))
            case other                                      => throw new Model2QueryException(s"Unresolved tacit Atom_:\nAtom_  : $a\nElement: $other")
          }
        }

        // Atom (mandatory)

        case a@Atom(_, _, t, 2, value, _, gs, _) => value match {
          case Qm                                         => q.findD(v, gs).where(e, a, v, gs).in(v, a, None, e)
          case Neq(Seq(Qm))                               => q.findD(v, gs).where(e, a, v, gs).compareTo("!=", a, v, Var(v1)).in(v1, a, None, e)
          case Gt(Qm)                                     => q.findD(v, gs).where(e, a, v, gs).compareTo(">", a, v, Var(v1)).in(v1, a, None, e)
          case Ge(Qm)                                     => q.findD(v, gs).where(e, a, v, gs).compareTo(">=", a, v, Var(v1)).in(v1, a, None, e)
          case Lt(Qm)                                     => q.findD(v, gs).where(e, a, v, gs).compareTo("<", a, v, Var(v1)).in(v1, a, None, e)
          case Le(Qm)                                     => q.findD(v, gs).where(e, a, v, gs).compareTo("<=", a, v, Var(v1)).in(v1, a, None, e)
          case Fulltext(Seq(Qm))                          => q.findD(v, gs).fulltext(e, a, v, Var(v1)).in(v1, a, None, e)
          case VarValue                                   => q.findD(v, gs).where(e, a, v, gs)
          case Eq(Nil)                                    => q.findD(v, gs).where(e, a, v, gs).not(e, a, v, gs)
          case Eq((set: Set[_]) :: Nil)                   => q.findD(v, gs).whereAnd(e, a, v, set.toSeq, u(t, v))
          case Eq(arg :: Nil) if uri(t)                   => q.findD(v, gs).where(e, a, v, Nil).where(e, a, v + "_uri", Nil).func( s"""ground (java.net.URI. "$arg")""", Empty, v + "_uri")
          case Eq(arg :: Nil)                             => q.findD(v, gs).where(e, a, Val(arg), gs).where(e, a, v, Nil)
          case Eq(args) if args.head.isInstanceOf[Set[_]] => throw new Model2QueryException(s"[Atom (mandatory)] Can only apply a single Set of values for attribute :${a.ns}/${a.name}")
          case Eq(args)                                   => q.findD(v, gs).where(e, a, v, gs).orRules(e, a, args, Nil, u(t, v))
          case Neq(args)                                  => q.findD(v, gs).where(e, a, v, gs).compareToMany("!=", a, v, args)
          case Gt(arg)                                    => q.findD(v, gs).where(e, a, v, gs).compareTo(">", a, v, Val(arg))
          case Ge(arg)                                    => q.findD(v, gs).where(e, a, v, gs).compareTo(">=", a, v, Val(arg))
          case Lt(arg)                                    => q.findD(v, gs).where(e, a, v, gs).compareTo("<", a, v, Val(arg))
          case Le(arg)                                    => q.findD(v, gs).where(e, a, v, gs).compareTo("<=", a, v, Val(arg))
          case And(args)                                  => q.findD(v, gs).whereAnd(e, a, v, args, u(t, v))
          case Fn(fn, _)                                  => q.find(fn, Nil, v, gs).where(e, a, v, gs)
          case Fulltext(arg :: Nil)                       => q.findD(v, gs).fulltext(e, a, v, Val(arg))
          case other                                      => throw new Model2QueryException(s"Unresolved cardinality-many Atom:\nAtom   : $a\nElement: $other")
        }

        case a@Atom(_, _, t, 1, value, _, gs, _) => value match {
          case Qm                            => q.find(v, gs).where(e, a, v, gs).in(v, a, None, e)
          case Neq(Seq(Qm))                  => q.find(v, gs).where(e, a, v, gs).compareTo("!=", a, v, Var(v1)).in(v1, a, None, e)
          case Gt(Qm)                        => q.find(v, gs).where(e, a, v, gs).compareTo(">", a, v, Var(v1)).in(v1, a, None, e)
          case Ge(Qm)                        => q.find(v, gs).where(e, a, v, gs).compareTo(">=", a, v, Var(v1)).in(v1, a, None, e)
          case Lt(Qm)                        => q.find(v, gs).where(e, a, v, gs).compareTo("<", a, v, Var(v1)).in(v1, a, None, e)
          case Le(Qm)                        => q.find(v, gs).where(e, a, v, gs).compareTo("<=", a, v, Var(v1)).in(v1, a, None, e)
          case Fulltext(Seq(Qm))             => q.find(v, gs).fulltext(e, a, v, Var(v1)).in(v1, a, None, e)
          case EntValue                      => q.find(e, gs)
          case VarValue                      => q.find(v, gs).where(e, a, v, gs)
          case NoValue                       => q.find(NoVal, gs).where(e, a, v, gs)
          case Distinct                      => q.find("distinct", Nil, v, gs).where(e, a, v, gs).widh(e)
          case BackValue(backNs)             => q.find(e, gs).where(v, a.ns, a.name, Var(e), backNs, gs)
          case Eq(Nil)                       => q.find(v, gs).where(e, a, v, gs).not(e, a, v, gs)
          case Eq((seq: Seq[_]) :: Nil)      => q.find(v, gs).where(e, a, v, gs).orRules(e, a, seq, gs, u(t, v))
          case Eq(arg :: Nil) if uri(t)      => q.find(v, gs).func( s"""ground (java.net.URI. "$arg")""", Empty, v).where(e, a, v, Nil)
          case Eq(arg :: Nil)                => q.find(v, gs).where(e, a, v, gs).compareTo("=", a, v, Val(arg))
          case Eq(args)                      => q.find(v, gs).where(e, a, v, gs).orRules(e, a, args, gs, u(t, v))
          case Neq(args)                     => q.find(v, gs).where(e, a, v, gs).compareToMany("!=", a, v, args)
          case Gt(arg)                       => q.find(v, gs).where(e, a, v, gs).compareTo(">", a, v, Val(arg))
          case Ge(arg)                       => q.find(v, gs).where(e, a, v, gs).compareTo(">=", a, v, Val(arg))
          case Lt(arg)                       => q.find(v, gs).where(e, a, v, gs).compareTo("<", a, v, Val(arg))
          case Le(arg)                       => q.find(v, gs).where(e, a, v, gs).compareTo("<=", a, v, Val(arg))
          case Fn(fn, Some(i))               => q.find(fn, Seq(i), v, gs).where(e, a, v, gs)
          case Fn(fn, _) if coalesce(fn)     => q.find(fn, Nil, v, gs).where(e, a, v, gs).widh(e)
          case Fn(fn, _)                     => q.find(fn, Nil, v, gs).where(e, a, v, gs)
          case Fulltext(arg :: Nil)          => q.find(v, gs).fulltext(e, a, v, Val(arg))
          case Fulltext(args)                => q.find(v, gs).fulltext(e, a, v, Var(v1)).orRules(v1, a, args)
          case Length(Some(Fn(fn, Some(i)))) => q.find(v2, gs).where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2)
          case Length(Some(Fn(fn, _)))       => q.find(fn, Nil, v2, gs).where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2)
          case Length(_)                     => q.find(v2, gs).where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2)
          case other                         => throw new Model2QueryException(s"Unresolved cardinality-one Atom:\nAtom   : $a\nElement: $other")
        }


        // Bond ===================================================================================

        case Bond(ns, refAttr, refNs, _, _) => q.ref(e, ns, refAttr, v, refNs)

        case Transitive(backRef, refAttr, refNs, depth, prevVar) => q.transitive(backRef, refAttr, prevVar, v, depth)

        case ReBond(backRef, refAttr, refNs, _, _) => q.ref(e, backRef, refAttr, v, refNs)


        // Meta ===================================================================================

        case Meta(_, _, "e", _, Fn("count", Some(i)))                 => q.find("count", Seq(i), e, Nil)
        case Meta(_, _, "e", _, Fn("count", _))                       => q.find("count", Nil, e, Nil)
        case Meta(_, _, "e", _, Length(Some(Fn(_, _))))               => q.find(e, Nil)
        case Meta(_, attr, "e", _, Eq(Seq(Qm))) if attr.endsWith("_") => q.in(e)
        case Meta(_, _, "e", _, Eq(Seq(Qm)))                          => q.find(e, Nil).in(e)
        case Meta(_, attr, "e", _, Eq(eids)) if attr.endsWith("_")    => q.in(eids, e)
        case Meta(_, _, "e", _, Eq(eids))                             => q.find(e, Nil).in(eids, e)
        case Meta(_, _, "r", _, IndexVal)                             => q.find(v, Nil).func("molecule.util.JavaFunctions/bind", Seq(Var(e)), ScalarBinding(Var(v)))
        case Meta(_, _, _, Id(eid), IndexVal)                         => q.find(v, Nil).func("molecule.util.JavaFunctions/bind", Seq(Val(eid)), ScalarBinding(Var(v)))
        case Meta(_, _, _, _, IndexVal)                               => q.find(v, Nil).func("molecule.util.JavaFunctions/bind", Seq(Var(e)), ScalarBinding(Var(v)))
        case Meta(_, attr, _, _, EntValue) if attr.endsWith("_")      => q
        case Meta(_, _, _, _, EntValue)                               => q.find(e, Nil)
        case Meta(_, _, _, _, _)                                      => q

        case unresolved => throw new Model2QueryException("Unresolved model: " + unresolved)
      }
    }

    def coalesce(fn: String) = Seq("sum", "count", "count-distinct", "median", "avg", "variance", "stddev").contains(fn)

    def nextChar(str: String, inc: Int): String = {
      val chars = str.toCharArray
      val (pre, cur) = if (chars.size == 2) (chars.head, chars.last) else ('-', chars.head)
      (pre, cur, inc) match {
        case (_, _, i) if i > 2 => throw new Model2QueryException("Can't increment more than 2")
        case ('-', 'y', 2)      => "aa"
        case ('-', 'z', 2)      => "ab"
        case ('-', 'z', 1)      => "aa"
        case ('-', c, i)        => (c + i).toChar.toString
        case ('z', _, _)        => throw new Model2QueryException("Ran out of vars...")
        case (p, 'y', 2)        => (p + 1).toChar.toString + "a"
        case (p, 'z', 2)        => (p + 1).toChar.toString + "b"
        case (p, 'z', 1)        => (p + 1).toChar.toString + "a"
        case (p, c, i)          => p.toString + (c + i).toChar
      }
    }

    def make(query: Query, element: Element, e: String, v: String, prevNs: String, prevAttr: String, prevRefNs: String)
    : (Query, String, String, String, String, String) = {
      val w = nextChar(v, 1)
      val y = nextChar(v, 2)
      //      x(1, query, element, e, v, prevNs, prevAttr, prevRefNs)
      element match {
        case uni@Atom(ns, attr, _, _, Fn("unify", _), _, _, _)           => {
          val attr1 = if (attr.last == '_') attr.init else attr
          // Find previous matching value that we want to unify with (from an identical attribute)
          query.wh.clauses.reverse.collectFirst {
            // Having a value var to unify with
            case dc@DataClause(_, Var(e0), a@KW(ns0, attr0, _), Var(v0), _, _) if ns0 == ns && attr0 == attr1 => ns match {
              case s if s == prevNs => (resolve(query, e, v0, element), e, v, ns, attr, "")
              case s                => (resolve(query, v, v0, element), v, v, ns, attr, "")
            }

            // Missing value var to unify with
            case dc@DataClause(_, Var(e0), a@KW(ns0, attr0, _), _, _, _) if ns0 == ns && attr0 == attr1 =>
              // Add initial clause to have a var to unify with
              val initialClause = dc.copy(v = Var(w))
              val newWhere = query.wh.copy(clauses = query.wh.clauses :+ initialClause)
              (resolve(query.copy(wh = newWhere), v, w, element), v, w, ns, attr, "")
          } getOrElse {
            throw new Model2QueryException(s"Can't find previous attribute matching unifying attribute `$ns.$attr` in query so far:\n$query\nATOM: $uni")
          }
        }
        case Atom(ns, attr, "a", _, _, _, _, _)                          => (resolve(query, e, v, element), e, w, ns, attr, "")
        case Atom(ns, attr, "ns", _, _, _, _, _)                         => (resolve(query, e, v, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _, _) if prevRefNs == "IndexVal" => (resolve(query, e, w, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _, _) if ns == prevRefNs         => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _, _) if ns == prevAttr          => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _, _) if ns == prevNs            => (resolve(query, e, w, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _, _)                            => (resolve(query, e, v, element), e, v, ns, attr, "")

        case Bond(ns, refAttr, refNs, _, bi: Bidirectional) if ns == prevNs && refAttr == prevAttr => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs, _, _) if ns == prevNs && refAttr == prevAttr                 => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs, _, _) if ns == prevNs                                        => (resolve(query, e, w, element), e, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs, _, _) if ns == prevAttr                                      => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs, _, _) if ns == prevRefNs                                     => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs, _, _)                                                        => (resolve(query, e, v, element), e, v, ns, refAttr, refNs)

        case transitive@Transitive(backRef, refAttr, refNs, _, _) => {
          val (backRefE, backRefV) = query.wh.clauses.reverse.collectFirst {
            case DataClause(_, backE, a, Var(backV), _, _) if a.ns == backRef => (backE.v, backV)
          } getOrElse {
            throw new Model2QueryException(s"Can't find back reference namespace `$backRef` in query so far:\n$query")
          }
          val backRefElement = transitive.copy(prevVar = backRefV)
          (resolve(query, backRefE, w, backRefElement), v, w, backRef, refAttr, refNs)
        }

        case rbe@ReBond(backRef, _, _, _, _) => {
          val backRefE = query.wh.clauses.reverse.collectFirst {
            case DataClause(_, backE, a, Var(backV), _, _) if a.ns == backRef => backE.v
          } getOrElse {
            throw new Model2QueryException(s"Can't find back reference namespace `$backRef` in query so far:\n$model\n---------\n$query\n---------\n$rbe")
          }
          (query, backRefE, v, backRef, "", "")
        }

        case Self =>
          // Self-reference should be distinct
          //          val distinctQuery = query.func("!=", Seq(Var(e), Var(w)))
          //          (distinctQuery, w, y, prevNs, prevAttr, prevRefNs)
          (query, w, y, prevNs, prevAttr, prevRefNs)

        case Nested(b@Bond(ns, refAttr, refNs, _, _), elements) =>
          val (e2, elements2) = if (ns == "") (e, elements) else (w, b +: elements)
          val (q2, _, v2, ns2, attr2, refNs2) = elements2.foldLeft((query, e, v, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, nextChar(v2, 1), ns2, attr2, refNs2)

        case Meta(ns, attr, "e", NoValue, Eq(Seq(Qm)))                  => (resolve(query, e, v, element), e, v, ns, attr, prevRefNs)
        case Meta(ns, attr, "e", NoValue, Eq(eids))                     => (resolve(query, e, v, element), e, v, ns, attr, prevRefNs)
        case Meta(ns, attr, "e", _, IndexVal) if prevRefNs == ""        => (resolve(query, e, v, element), e, w, ns, attr, "")
        case Meta(ns, attr, "e", _, IndexVal)                           => (resolve(query, v, w, element), v, y, ns, attr, "IndexVal")
        case Meta(ns, attr, "r", _, IndexVal)                           => (resolve(query, w, v, element), e, w, ns, attr, "IndexVal")
        case Meta(ns, attr, "e", NoValue, _) if prevRefNs == ""         => (resolve(query, e, v, element), e, w, ns, attr, "")
        case Meta(ns, attr, "e", NoValue, _) if prevRefNs == "IndexVal" => (resolve(query, e, y, element), e, y, ns, attr, "")
        case Meta(ns, attr, "e", NoValue, EntValue)                     => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Meta(ns, attr, "e", NoValue, _)                            => (resolve(query, v, w, element), e, w, ns, attr, "")
        case Meta(ns, attr, _, _, _)                                    => (resolve(query, e, v, element), e, v, ns, attr, "")

        case TxMetaData(elements) =>
          val (q2, e2, v2, prevNs2, prevAttr2, prevRefNs2) = elements.foldLeft((query, "tx", w, prevNs, prevAttr, prevRefNs)) {
            case ((q1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element) => make(q1, element, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, nextChar(v2, 1), prevNs2, prevAttr2, prevRefNs2)

        case Composite(elements) =>
          val eid = if (query.wh.clauses.isEmpty) e
          else query.wh.clauses.reverse.collectFirst {
            case DataClause(_, Var(lastE), KW(ns, _, _), _, _, _) if ns != "db" => lastE
          } getOrElse query.wh.clauses.reverse.collectFirst {
            case Funct(_, Seq(Var(lastE)), _) => lastE
          }.getOrElse(throw new Model2QueryException(s"Couldn't find `e` from last data clause"))

          val (q2, e2, v2, prevNs2, prevAttr2, prevRefNs2) = elements.foldLeft((query, eid, v, prevNs, prevAttr, prevRefNs)) {
            case ((q1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element) => make(q1, element, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, nextChar(v2, 1), prevNs2, prevAttr2, prevRefNs2)

        case other => throw new Model2QueryException("Unresolved query variables from model: " + (other, e, v, prevNs, prevAttr, prevRefNs))
      }
    }

    // Process And-semantics (self-joins)
    def postProcess(q: Query) = {
      def getAndAtoms(elements: Seq[Element]): Seq[Atom] = elements flatMap {
        case a@Atom(_, _, _, 2, And(andValues), _, _, _) => Seq(a)
        case Nested(_, elements2)                        => getAndAtoms(elements2)
        case _                                           => Nil
      }

      val andAtoms: Seq[Atom] = model.elements.collect {
        case a@Atom(_, attr0, _, card, And(_), _, _, _) if card == 1 || card == 3 => a
      }

      if (andAtoms.size > 1)
        throw new Model2QueryException("For now, only 1 And-expression can be used. Found: " + andAtoms)

      if (andAtoms.size == 1) {
        val clauses = q.wh.clauses
        val andAtom = andAtoms.head
        val Atom(ns, attr0, _, card, And(andValues), _, _, _) = andAtom
        val attr = if (attr0.last == '_') attr0.init else attr0
        val unifyAttrs = model.elements.collect {
          case a@Atom(ns1, attr1, _, _, _, _, _, _) if a != andAtom => (ns1, if (attr1.last == '_') attr1.init else attr1)
        }

        // The first arg is already modelled in the query
        val selfJoinClauses = andValues.zipWithIndex.tail.flatMap { case (andValue, i) =>

          // Todo: complete matches...
          def vi(v0: Var) = Var(v0.v + "_" + i)
          def queryValue(qv: QueryValue): QueryValue = qv match {
            case Var(v) => vi(Var(v))
            case _      => qv
          }
          def queryTerm(qt: QueryTerm): QueryTerm = qt match {
            case Rule(name, args, cls) => Rule(name, args map queryValue, cls flatMap clause)
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
          def clause(cl: Clause): Seq[Clause] = cl match {
            case dc: DataClause => dataClauses(dc)
            case other          => makeSelfJoinClauses(other)
          }
          def dataClauses(dc: DataClause): Seq[Clause] = dc match {
            case DataClause(ds, e, a@KW(ns2, attr2, _), Var(v), tx, op) if (ns, attr) == (ns2, attr2) && card == 3 =>
              // Add next And-value
              Seq(
                DataClause(ds, vi(e), a, Var(v + "_" + i), queryTerm(tx), queryTerm(op)),
                Funct(".matches ^String", List(Var(v + "_" + i), Val(".+@(" + andValue + ")$")), NoBinding)
              )

            case DataClause(ds, e, a@KW(ns2, attr2, _), _, tx, op) if (ns, attr) == (ns2, attr2) =>
              // Add next And-value
              Seq(DataClause(ds, vi(e), a, Val(andValue), queryTerm(tx), queryTerm(op)))

            case DataClause(ds, e, a@KW(ns2, attr2, _), v, tx, op) if unifyAttrs.contains((ns2, attr2)) =>
              // Keep value-position value to unify
              Seq(DataClause(ds, vi(e), a, v, queryTerm(tx), queryTerm(op)))

            case DataClause(ds, e, a, v, tx, op) =>
              // Add i to variables
              Seq(DataClause(ds, vi(e), a, queryValue(v), queryTerm(tx), queryTerm(op)))
          }
          def makeSelfJoinClauses(expr: QueryExpr): Seq[Clause] = expr match {
            case dc@DataClause(ds, e, a, v, tx, op)                    => dataClauses(dc)
            case RuleInvocation(name, args)                            => Seq(RuleInvocation(name, args map queryTerm))
            case Funct(".startsWith ^String", List(v, key), NoBinding) => Nil
            case Funct(".matches ^String", List(v, key), NoBinding)    => Nil
            case Funct("second", ins, outSame)                         => Seq(Funct("second", ins map queryTerm, outSame))
            case Funct(name, ins, outs)                                => Seq(Funct(name, ins map queryTerm, binding(outs)))
          }
          clauses flatMap makeSelfJoinClauses
        }
        q.copy(wh = Where(q.wh.clauses ++ selfJoinClauses))
      } else q
    }

    val query = model.elements.foldLeft((Query(), "a", "b", "", "", "")) { case ((query_, e, v, prevNs, prevAttr, prevRefNs), element) =>
      make(query_, element, e, v, prevNs, prevAttr, prevRefNs)
    }._1

    val query2 = postProcess(query)
    x(21, query, query2, query2.datalog)
    query2
  }
}