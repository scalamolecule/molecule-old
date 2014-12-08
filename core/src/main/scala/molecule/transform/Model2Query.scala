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
      val (v1, v2, v3, v4, v5) = (v + 1, v + 2, v + 3, v + 4, v + 5)
      element match {
        case Atom(_, _, _, _, Replace(_), _, _) => q
        case Atom(_, _, _, _, Remove(_), _, _)  => q

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

        case a0@Atom(_, attr0, _, card, value, enumPrefix, gs) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          val (isEnum, prefix) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {
            case (_, Qm) if isEnum                    => q.where(e, a, v, gs).in(v, a, enumPrefix, e)
            case (_, Qm)                              => q.where(e, a, v, gs).in(v, a)
            case (2, Fulltext(Seq(Qm)))               => q.fulltext(e, a, v, Var(v1)).in(v1, a)
            case (_, Fulltext(Seq(Qm)))               => q.fulltext(e, a, v, Var(v1)).in(v1, a)
            case (_, Lt(Qm))                          => q.where(e, a, v, gs).compareTo("<", a, v, Var(v1))
            case (2, VarValue)                        => q.where(e, a, v, gs)
            case (_, VarValue)                        => q.find(gs).where(e, a, v, gs)
            case (2, EnumVal)                         => q.enum(e, a, v, gs)
            case (_, EnumVal)                         => q.enum(e, a, v, gs)
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(prefix + _), gs)
            case (_, Eq(ss)) if ss.size > 1           => q.orRules(e, a, ss, gs)
            case (_, Eq(s :: Nil)) if isEnum          => q.where(e, a, Val(prefix + s), gs)
            case (_, Eq(s :: Nil))                    => q.where(e, a, Val(s), gs)
            case (_, Lt(arg))                         => q.where(e, a, v, gs).compareTo("<", a, v, Val(arg))
            case (_, Fn("count", _))                  => q.where(e, a, v, gs)
            case (2, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qvs))                   => q.orRules(v1, a, qvs, gs).fulltext(e, a, v, Var(v1))
            case (c, va)                              => sys.error(s"[Model2Query:resolve[Atom_]] Unresolved Atom_ with cardinality/value: $c / $va")
          }
        }

        case a0@Atom(_, _, _, card, value, enumPrefix, gs) => {
          val a = a0
          val (isEnum, prefix) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {
            case (_, Qm) if isEnum                    => q.enum(e, a, v, gs).in(v, a, enumPrefix).find(v2, gs)
            case (_, Qm)                              => q.where(e, a, v, gs).in(v, a).find(v, gs)
            case (2, Fulltext(Seq(Qm)))               => q.fulltext(e, a, v, Var(v1)).in(v1, a).find("distinct", Seq(), v, gs)
            case (_, Fulltext(Seq(Qm)))               => q.fulltext(e, a, v, Var(v1)).in(v1, a).find(v, gs)
            case (_, EntValue)                        => q.find(e, gs)
            case (_, Lt(Qm))                          => q.where(e, a, v, gs).compareTo("<", a, v, Var(v1)).find(v, gs).in(v1, a)
            case (2, VarValue)                        => q.where(e, a, v, gs).find("distinct", Seq(), v, gs)
            case (_, VarValue)                        => q.where(e, a, v, gs).find(v, gs)
            case (_, NoValue)                         => q.where(e, a, v, gs).find(NoVal, gs)
            case (_, BackValue(backNs))               => q.where(v, a.ns, a.name, Var(e), backNs, gs).find(e, gs)
            case (2, EnumVal)                         => q.enum(e, a, v, gs).find("distinct", Seq(), v2, gs)
            case (_, EnumVal)                         => q.enum(e, a, v, gs).find(v2, gs)
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(prefix + _), gs)
            case (2, Eq(ss)) if ss.size > 1           => q.orRules(e, a, ss).where(e, a, v, gs).find("distinct", Seq(), v, gs)
            case (_, Eq(ss)) if ss.size > 1           => q.orRules(e, a, ss, gs).find(e, gs)
            case (_, Eq(s :: Nil)) if isEnum          => q.where(e, a, Val(prefix + s), gs).enum(e, a, v).find(v2, gs) // todo: can we output a constant value instead?
            case (_, Eq(s :: Nil))                    => q.where(e, a, Val(s), gs).where(e, a, v, Seq()).find(v, gs) // todo: can we output a constant value instead?
            case (_, Lt(arg))                         => q.where(e, a, v, gs).compareTo("<", a, v, Val(arg)).find(v, gs)
            case (_, Fn(fn, Some(i)))                 => q.where(e, a, v, gs).find(fn, Seq(i), v, gs)
            case (_, Fn(fn, _))                       => q.where(e, a, v, gs).find(fn, Seq(), v, gs)
            case (2, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv)).find("distinct", Seq(), v, gs)
            case (_, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv)).find(v, gs)
            case (_, Fulltext(qvs))                   => q.fulltext(e, a, v, Var(v1)).orRules(v1, a, qvs).find(v, gs)
            case (_, Length(Some(Fn(fn, Some(i)))))   => q.where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2).find(v2, gs)
            case (_, Length(Some(Fn(fn, _))))         => q.where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2).find(fn, Seq(), v2, gs)
            case (_, Length(_))                       => q.where(e, a, v, gs).cast(v, v1).func("count", Var(v1), v2).find(v2, gs)
            case (c, va)                              => sys.error(s"[Model2Query:resolve[Atom]] Unresolved Atom with cardinality/value: $c / $va")
          }
        }

        case Bond(ns, refAttr, refNs) => q.ref(e, ns, refAttr, v, refNs)

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

        case Group(b@Bond(ns, refAttr, refNs), elements) =>
          val (e2, elements2) = if(ns == "") (e, elements) else (w, b +: elements)
          val (q2, _, v2, ns2, attr2, refNs2) = elements2.foldLeft((query, e, v, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
//          val e2 = if(ns == "") e else w
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

    model.elements.foldLeft((Query(), "a", "b", "", "", "")) { case ((query, e, v, prevNs, prevAttr, prevRefNs), element) =>
      make(query, element, e, v, prevNs, prevAttr, prevRefNs)
    }._1
  }
}