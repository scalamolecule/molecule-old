package molecule
package transform
import molecule.ast.model._
import molecule.ast.query._
import molecule.ops.QueryOps._
import molecule.util.Debug

object Model2Query {
  val x = Debug("Model2Query", 1, 60, false)

  def apply(model: Model): Query = {

    def resolve(q: Query, e: String, v: String, element: Element) = {
      val (v1, v2) = (v + 1, v + 2)
      element match {
        case Atom(_, _, _, _, Replace(_), _) => q
        case Atom(_, _, _, _, Remove(_), _)  => q

        case a0@Atom(ns, attr0, tpe, card, value, enumPrefix) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          val (isEnum, prefix) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {
            case (_, Qm) if isEnum                    => q.in(v, a, enumPrefix, e).where(e, a, v)
            case (_, Qm)                              => q.where(e, a, v).in(v, a)
            case (_, Lt(Qm))                          => q.where(e, a, v).compareTo("<", a, v, Var(v1))
            case (2, Fulltext(Seq(Qm)))               => q.in(v1, a).fulltext(e, a, v, Var(v1))
            case (_, Fulltext(Seq(Qm)))               => q.in(v1, a).fulltext(e, a, v, Var(v1))
            case (2, VarValue)                        => q.where(e, a, v)
            case (_, VarValue)                        => q.where(e, a, v)
            case (2, EnumVal)                         => q.enum(e, a, v)
            case (_, EnumVal)                         => q.enum(e, a, v)
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(prefix + _))
            case (_, Eq(ss)) if ss.size > 1           => q.orRules(e, a, ss)
            case (_, Eq(s :: Nil)) if isEnum          => q.where(e, a, Val(prefix + s))
            case (_, Eq(s :: Nil))                    => q.where(e, a, Val(s))
            case (_, Lt(arg))                         => q.where(e, a, v).compareTo("<", a, v, Val(arg))
            case (_, Fn("count"))                     => q.where(e, a, v).find("count", Seq(), e)
            case (2, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qvs))                   => q.orRules(v1, a, qvs).fulltext(e, a, v, Var(v1))
            case (c, va)                              => sys.error(s"[Model2Query] Unresolved Atom_ with cardinality/value: $c / $va")
          }
        }

        case a0@Atom(ns, attr, tpe, card, value, enumPrefix) => {
          val a = a0
          val (isEnum, prefix) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {
            case (_, Qm) if isEnum                    => q.find(v2).in(v, a, enumPrefix).enum(e, a, v)
            case (_, Qm)                              => q.find(v).in(v, a).where(e, a, v)
            case (_, Lt(Qm))                          => q.find(v).in(v1, a).where(e, a, v).compareTo("<", a, v, Var(v1))
            case (2, Fulltext(Seq(Qm)))               => q.find("distinct", Seq(), v).in(v1, a).fulltext(e, a, v, Var(v1))
            case (_, Fulltext(Seq(Qm)))               => q.find(v).in(v1, a).fulltext(e, a, v, Var(v1))
            case (_, EntValue)                        => q.find(e)
            case (2, VarValue)                        => q.find("distinct", Seq(), v).where(e, a, v)
            case (_, VarValue)                        => q.find(v).where(e, a, v)
            case (_, BackValue(backNs))               => q.find(e).where(v, a.ns, a.name, e, backNs)
            case (2, EnumVal)                         => q.find("distinct", Seq(), v2).enum(e, a, v)
            case (_, EnumVal)                         => q.find(v2).enum(e, a, v)
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(prefix + _))
            case (2, Eq(ss)) if ss.size > 1           => q.find("distinct", Seq(), v).orRules(e, a, ss).where(e, a, v)
            case (_, Eq(ss)) if ss.size > 1           => q.find(v).orRules(e, a, ss)
            case (_, Eq(s :: Nil)) if isEnum          => q.find(v2).where(e, a, Val(prefix + s)).enum(e, a, v) // todo: can we output a constant value instead?
            case (_, Eq(s :: Nil))                    => q.find(v).where(e, a, Val(s)).where(e, a, v) // todo: can we output a constant value instead?
            case (_, Lt(arg))                         => q.find(v).where(e, a, v).compareTo("<", a, v, Val(arg))
            case (_, Fn("count"))                     => q.find("count", Seq(), e) //.where(e, a, v)
            case (2, Fulltext(qv :: Nil))             => q.find("distinct", Seq(), v).fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qv :: Nil))             => q.find(v).fulltext(e, a, v, Val(qv))
            case (_, Fulltext(qvs))                   => q.find(v).fulltext(e, a, v, Var(v1)).orRules(v1, a, qvs)
            case (c, va)                              => sys.error(s"[Model2Query] Unresolved Atom with cardinality/value: $c / $va")
          }
        }

        //        case Bond(ns, refAttr, refNs) if ns.head.isUpper => q.ref(v, ns, refAttr, e, refNs)
        case Bond(ns, refAttr, refNs) => q.ref(e, ns, refAttr, v, refNs)

        //        case Group(ref, elements) => q
        //        case Group(ref, elements) => elements.resolve(q, e, v)

        case Meta(ns, attr, kind, tpe, value) => value match {
          case EntValue => q.find(e)
          case _        => q
        }

        case unresolved => sys.error("[Model2Query] Unresolved model: " + unresolved)
      }
    }

    def make(query: Query, element: Element, e: String, v: String, prevNs: String, prevAttr: String, prevRefNs: String): (Query, String, String, String, String, String) = {
      val w = (v.toCharArray.head + 1).toChar.toString
      element match {
        case Atom(ns, attr, _, _, _, _) if ns == prevNs    => (resolve(query, e, w, element), e, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _) if ns == prevAttr  => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _) if ns == prevRefNs => (resolve(query, v, w, element), v, w, ns, attr, "")
        case Atom(ns, attr, _, _, _, _)                    => (resolve(query, e, v, element), e, v, ns, attr, "")

        case Bond(ns, refAttr, refNs) if ns == prevNs    => (resolve(query, e, w, element), e, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs) if ns == prevAttr  => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs) if ns == prevRefNs => (resolve(query, v, w, element), v, w, ns, refAttr, refNs)
        case Bond(ns, refAttr, refNs)                    => (resolve(query, e, v, element), e, v, ns, refAttr, refNs)

        case Group(Bond(ns, refAttr, refNs), elements) =>
          //          val (bond, nested) = (elements.head, elements.tail)
          //          nested.foldLeft(make(query, bond, e, v, prevNs, prevAttr, prevRefNs)) { case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
          //          elements.foldLeft((query, e, v, prevNs, prevAttr, prevRefNs)) { case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
          val (q2, e2, v2, ns2, attr2, refNs2) = elements.foldLeft((query, e, v, prevNs, prevAttr, prevRefNs)) {
            case ((query1, e1, v1, prevNs1, prevAttr1, prevRefNs1), element1) =>
              make(query1, element1, e1, v1, prevNs1, prevAttr1, prevRefNs1)
          }
          (q2, e2, (v2.toCharArray.head + 1).toChar.toString, ns2, attr2, refNs2)

        case Meta(ns, attr, kind, tpe, value) => (resolve(query, e, v, element), e, v, ns, attr, "")

        case other => sys.error("[Model2Query] Unresolved query variables from model: " +(other, e, v, prevNs, prevAttr, prevRefNs))
        //        case other                                         => (e, v, prevNs, prevAttr, prevRefNs)
      }
    }

    model.elements.foldLeft((Query(), "a", "b", "", "", "")) { case ((query, e, v, prevNs, prevAttr, prevRefNs), element) =>
      //      val w = (v.toCharArray.head + 1).toChar.toString
      //      val (e1, v1, ns1, attr1, refNs1) = element match {
      //        case Atom(ns, attr, _, _, _, _) if ns == prevNs    => (e, w, ns, attr, "")
      //        case Atom(ns, attr, _, _, _, _) if ns == prevAttr  => (v, w, ns, attr, "")
      //        case Atom(ns, attr, _, _, _, _) if ns == prevRefNs => (v, w, ns, attr, "")
      //        case Atom(ns, attr, _, _, _, _)                    => (e, v, ns, attr, "")
      //
      //        case Bond(ns, refAttr, refNs) if ns == prevNs    => (e, w, ns, refAttr, refNs)
      //        case Bond(ns, refAttr, refNs) if ns == prevAttr  => (v, w, ns, refAttr, refNs)
      //        case Bond(ns, refAttr, refNs) if ns == prevRefNs => (v, w, ns, refAttr, refNs)
      //        case Bond(ns, refAttr, refNs)                    => (e, v, ns, refAttr, refNs)
      //
      ////        case Group(Bond(ns, refAttr, refNs), _) if ns == prevNs    => x(1, ns, refAttr, refNs); (e, w, ns, refAttr, refNs)
      ////        case Group(Bond(ns, refAttr, refNs), _) if ns == prevAttr  => x(2, ns, refAttr, refNs); (v, w, ns, refAttr, refNs)
      ////        case Group(Bond(ns, refAttr, refNs), _) if ns == prevRefNs => x(3, ns, refAttr, refNs); (v, w, ns, refAttr, refNs)
      ////        case Group(Bond(ns, refAttr, refNs), _)                    => x(4, ns, refAttr, refNs); (e, v, ns, refAttr, refNs)
      //        case Group(Bond(ns, refAttr, refNs), elements)                    =>
      //
      //
      //          (e, v, ns, refAttr, refNs)
      //
      //        case Meta(ns, attr, kind, tpe, value) => (e, v, ns, attr, "")
      //
      //        case other => sys.error("[Model2Query] Unresolved query variables from model: " +(other, e, v, prevNs, prevAttr, prevRefNs))
      //        //        case other                                         => (e, v, prevNs, prevAttr, prevRefNs)
      //      }
      //
      //
      //      val query1 = resolve(query, e1, v1, element)
      //      (query1, e1, v1, ns1, attr1, refNs1)

      make(query, element, e, v, prevNs, prevAttr, prevRefNs)

    }._1
  }
}


//        case Atom(ns, attr, _, _, _, _) if ns == prevNs    => x(1, s"[Prev] $a :$prevNs/$prevAttr($prevRefNs) $b", s"[Atom] $a :$ns/$attr $c"); (a, c, ns, attr, "")
//        case Atom(ns, attr, _, _, _, _) if ns == prevAttr  => x(2, s"[Prev] $a :$prevNs/$prevAttr($prevRefNs) $b", s"[Atom] $b :$ns/$attr $c"); (b, c, ns, attr, "")
//        case Atom(ns, attr, _, _, _, _) if ns == prevRefNs => x(3, s"[Prev] $a :$prevNs/$prevAttr($prevRefNs) $b", s"[Atom] $b :$ns/$attr $c"); (b, c, ns, attr, "")
//        case Atom(ns, attr, _, _, _, _)                    => x(6, s"[Prev] $a :$prevNs/$prevAttr($prevRefNs) $b", s"[Atom] $a :$ns/$attr $b"); (a, b, ns, attr, "")
//        case Bond(ns, refAttr, refNs) if ns == prevNs      => x(4, s"[Prev] $a :$prevNs/$prevAttr($prevRefNs) $b", s"[Bond] $a :$ns/$refAttr($refNs) $c"); (a, c, ns, refAttr, refNs)
//        case Bond(ns, refAttr, refNs) if ns == prevAttr    => x(4, s"[Prev] $a :$prevNs/$prevAttr($prevRefNs) $b", s"[Bond] $b :$ns/$refAttr($refNs) $c"); (b, c, ns, refAttr, refNs)
//        case Bond(ns, refAttr, refNs) if ns == prevRefNs   => x(4, s"[Prev] $a :$prevNs/$prevAttr($prevRefNs) $b", s"[Bond] $b :$ns/$refAttr($refNs) $c"); (b, c, ns, refAttr, refNs)
//        case Bond(ns, refAttr, refNs)                      => x(5, s"[Prev] $a :$prevNs/$prevAttr($prevRefNs) $b", s"[Bond] $a :$ns/$refAttr($refNs) $b"); (a, b, ns, refAttr, refNs)
//        case z                                             => x(33, z); (a, b, prevNs, prevAttr, prevRefNs)