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
      val (v1, v2) = (v + 1, v + 2)
      element match {
        case Atom(_, _, _, _, Replace(_), _, _) => q
        case Atom(_, _, _, _, Remove(_), _, _)  => q

        case a0@Atom(_, attr0, _, card, value, enumPrefix, tx) if attr0.last == '_' && tx.isEmpty => {
          val a = a0.copy(name = attr0.init)
          val (isEnum, prefix) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {
            case (_, Qm) if isEnum                    => q.in(v, a, enumPrefix, e).where(e, a, v, Seq())
            case (_, Qm)                              => q.where(e, a, v, Seq()).in(v, a)
            case (_, Lt(Qm))                          => q.where(e, a, v, Seq()).compareTo("<", a, v, Var(v1))
            case (2, Fulltext(Seq(Qm)))               => q.in(v1, a).fulltext(e, a, v, Var(v1))
            case (_, Fulltext(Seq(Qm)))               => q.in(v1, a).fulltext(e, a, v, Var(v1))
            case (2, VarValue)                        => q.where(e, a, v, Seq())
            case (_, VarValue)                        => q.where(e, a, v, Seq())
            case (2, EnumVal)                         => q.enum(e, a, v, Seq())
            case (_, EnumVal)                         => q.enum(e, a, v, Seq())
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(prefix + _), Seq())
            case (_, Eq(ss)) if ss.size > 1           => q.orRules(e, a, ss, Seq())
            case (_, Eq(s :: Nil)) if isEnum          => q.where(e, a, Val(prefix + s), Seq())
            case (_, Eq(s :: Nil))                    => q.where(e, a, Val(s), Seq())
            case (_, Lt(arg))                         => q.where(e, a, v, Seq()).compareTo("<", a, v, Val(arg))
            case (_, Fn("count"))                     => q.where(e, a, v, Seq()).find("count", Seq(), e, Seq())
            case (2, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qvs))                   => q.orRules(v1, a, qvs, Seq()).fulltext(e, a, v, Var(v1))
            case (c, va)                              => sys.error(s"[Model2Query:resolve[Atom_]] Unresolved Atom_ with cardinality/value: $c / $va")
          }
        }

        case a0@Atom(_, attr0, _, card, value, enumPrefix, tx) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          val (isEnum, prefix) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {
            case (_, Qm) if isEnum                    => q.in(v, a, enumPrefix, e).where(e, a, v, tx)
            case (_, Qm)                              => q.where(e, a, v, tx).in(v, a)
            case (_, Lt(Qm))                          => q.where(e, a, v, tx).compareTo("<", a, v, Var(v1))
            case (2, Fulltext(Seq(Qm)))               => q.in(v1, a).fulltext(e, a, v, Var(v1))
            case (_, Fulltext(Seq(Qm)))               => q.in(v1, a).fulltext(e, a, v, Var(v1))
            case (2, VarValue)                        => q.where(e, a, v, tx)
            case (_, VarValue)                        => q.find(tx).where(e, a, v, tx)
            case (2, EnumVal)                         => q.enum(e, a, v, tx)
            case (_, EnumVal)                         => q.enum(e, a, v, tx)
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(prefix + _), tx)
            case (_, Eq(ss)) if ss.size > 1           => q.orRules(e, a, ss, tx)
            case (_, Eq(s :: Nil)) if isEnum          => q.where(e, a, Val(prefix + s), tx)
            case (_, Eq(s :: Nil))                    => q.where(e, a, Val(s), tx)
            case (_, Lt(arg))                         => q.where(e, a, v, tx).compareTo("<", a, v, Val(arg))
            case (_, Fn("count"))                     => q.where(e, a, v, tx).find("count", Seq(), e, tx)
            case (2, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qvs))                   => q.orRules(v1, a, qvs, tx).fulltext(e, a, v, Var(v1))
            case (c, va)                              => sys.error(s"[Model2Query:resolve[Atom_]] Unresolved Atom_ with cardinality/value: $c / $va")
          }
        }

        case a0@Atom(_, _, _, card, value, enumPrefix, tx) => {
          val a = a0
          val (isEnum, prefix) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {
            case (_, Qm) if isEnum                    => q.find(v2, tx).in(v, a, enumPrefix).enum(e, a, v, tx)
            case (_, Qm)                              => q.find(v, tx).in(v, a).where(e, a, v, tx)
            case (_, Lt(Qm))                          => q.find(v, tx).in(v1, a).where(e, a, v, tx).compareTo("<", a, v, Var(v1))
            case (2, Fulltext(Seq(Qm)))               => q.find("distinct", Seq(), v, tx).in(v1, a).fulltext(e, a, v, Var(v1))
            case (_, Fulltext(Seq(Qm)))               => q.find(v, tx).in(v1, a).fulltext(e, a, v, Var(v1))
            case (_, EntValue)                        => q.find(e, tx)
            case (2, VarValue)                        => q.find("distinct", Seq(), v, tx).where(e, a, v, tx)
            case (_, VarValue)                        => q.find(v, tx).where(e, a, v, tx)
            case (_, NoValue)                         => q.find(NoVal, tx).where(e, a, v, tx)
            case (_, BackValue(backNs))               => q.find(e, tx).where(v, a.ns, a.name, e, backNs, tx)
            case (2, EnumVal)                         => q.find("distinct", Seq(), v2, tx).enum(e, a, v, tx)
            case (_, EnumVal)                         => q.find(v2, tx).enum(e, a, v, tx)
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(prefix + _), tx)
            case (2, Eq(ss)) if ss.size > 1           => q.find("distinct", Seq(), v, tx).orRules(e, a, ss).where(e, a, v, tx)
            case (_, Eq(ss)) if ss.size > 1           => q.find(e, tx).orRules(e, a, ss, tx)
            case (_, Eq(s :: Nil)) if isEnum          => q.find(v2, tx).where(e, a, Val(prefix + s), tx).enum(e, a, v) // todo: can we output a constant value instead?
            case (_, Eq(s :: Nil))                    => q.find(v, tx).where(e, a, Val(s), tx).where(e, a, v, Seq()) // todo: can we output a constant value instead?
            case (_, Lt(arg))                         => q.find(v, tx).where(e, a, v, tx).compareTo("<", a, v, Val(arg))
            case (_, Fn("count"))                     => q.find("count", Seq(), e, tx) //.where(e, a, v)
            case (2, Fulltext(qv :: Nil))             => q.find("distinct", Seq(), v, tx).fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qv :: Nil))             => q.find(v, tx).fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qvs))                   => q.find(v, tx).fulltext(e, a, v, Var(v1)).orRules(v1, a, qvs)
            case (c, va)                              => sys.error(s"[Model2Query:resolve[Atom]] Unresolved Atom with cardinality/value: $c / $va")
          }
        }

        case Bond(ns, refAttr, refNs) => q.ref(e, ns, refAttr, v, refNs)

        case Meta(_, _, "a", _)      => q.find(v2, Seq()).where(e, v).ident(v, v1).func(".toString ^clojure.lang.Keyword", Seq(Var(v1)), ScalarBinding(Var(v2)))
        case Meta(_, _, _, EntValue) => q.find(e, Seq())
        case Meta(_, _, _, _)        => q

        case unresolved => sys.error("[Model2Query:resolve] Unresolved model: " + unresolved)
      }
    }

    def make(query: Query, element: Element, e: String, v: String, prevNs: String, prevAttr: String, prevRefNs: String): (Query, String, String, String, String, String) = {
      val w = (v.toCharArray.head + 1).toChar.toString
      element match {
        case Atom(ns, attr, _, _, _, _, _) if ns == prevNs    => (resolve(query, e, w, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if ns == prevAttr  => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _) if ns == prevRefNs => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _, _)                    => (resolve(query, e, v, element), e, v, ns, attr, "")

        case Bond(ns, refAttr, refNs) if ns == prevNs    => (resolve(query, e, w, element), e, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs) if ns == prevAttr  => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs) if ns == prevRefNs => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs)                    => (resolve(query, e, v, element), e, v, ns, refAttr, refNs)

        case Group(Bond(ns, refAttr, refNs), elements) =>
          val (q2, e2, v2, ns2, attr2, refNs2) = elements.foldLeft((query, e, v, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, (v2.toCharArray.head + 1).toChar.toString, ns2, attr2, refNs2)

        case Meta(ns, attr, "e", Eq(Seq(id: Long))) => x(2, element); (resolve(query, id.toString, v, element), id.toString, v, ns, attr, "")
        case Meta(ns, attr, _, _)                   => x(3, element); (resolve(query, e, v, element), e, v, ns, attr, "")

        case TxModel(elements) => //query
          val (q2, e2, v2, ns2, attr2, refNs2) = elements.foldLeft((query, e, v, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, (v2.toCharArray.head + 1).toChar.toString, ns2, attr2, refNs2)

        case other => sys.error("[Model2Query] Unresolved query variables from model: " +(other, e, v, prevNs, prevAttr, prevRefNs))
      }
    }

    model.elements.foldLeft((Query(), "a", "b", "", "", "")) { case ((query, e, v, prevNs, prevAttr, prevRefNs), element) =>
      make(query, element, e, v, prevNs, prevAttr, prevRefNs)
    }._1
  }
}