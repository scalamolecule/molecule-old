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
        case a0@Atom(ns, attr0, tpe, card, value, enumPrefix) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          val (isEnum, eP) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {

            // Input
            case (_, Qm) if isEnum      => q.in(v, a, enumPrefix, e).where(e, a, v)
            case (_, Qm)                => q.where(e, a, v).in(v, a)
            case (_, Lt(Qm))            => q.where(e, a, v).compareTo("<", a, v, Var(v1, a.tpeS))
            case (2, Fulltext(Seq(Qm))) => q.in(v1, a).fulltext(e, a, v, Var(v1, a.tpeS))
            case (_, Fulltext(Seq(Qm))) => q.in(v1, a).fulltext(e, a, v, Var(v1, a.tpeS))

            // Output
//            case (_, EntValue) => q.find(e, tpe)
            case (2, VarValue) => q.where(e, a, v)
            case (_, VarValue) => q.where(e, a, v)
            case (2, EnumVal)  => q.enum(e, a, v)
            case (_, EnumVal)  => q.enum(e, a, v)

            // Expressions
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(eP + _))
            case (_, Eq(ss)) if ss.size > 1           => q.orRules(e, a, ss)
            case (_, Eq(s :: Nil)) if isEnum          => q.where(e, a, Val(eP + s, a.tpeS))
            case (_, Eq(s :: Nil))                    => q.where(e, a, Val(s, a.tpeS))
            case (_, Lt(arg))                         => q.where(e, a, v).compareTo("<", a, v, Val(arg, a.tpeS))
            case (_, Fn("count")) if a.name == "e"    => q
            case (_, Fn("count"))                     => q.where(e, a, v).find("count", Seq(), e, "Int")
            case (2, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv, a.tpeS))
            case (_, Fulltext(qv :: Nil))             => q.fulltext(e, a, v, Val(qv, a.tpeS))
            case (_, Fulltext(qvs))                   => q.orRules(v1, a, qvs).fulltext(e, a, v, Var(v1, a.tpeS))

            // Manipulate
            case (_, Replace(_)) => q
            case (_, Remove(_))  => q

            case (c, va) => sys.error(s"[Model2Query] Unresolved Atom_ with cardinality/value: $c / $va")
          }
        }

        case a0@Atom(ns, attr, tpe, card, value, enumPrefix) => {
          val a = a0
          val (isEnum, eP) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {

            // Input
            case (_, Qm) if isEnum      => q.find(v2, tpe).in(v, a, enumPrefix).enum(e, a, v)
//            case (_, Qm) if isEnum      => q.find(v2, tpe).in(v, a, enumPrefix, e).enum(e, a, v)
            case (_, Qm)                => q.find(v, tpe).in(v, a).where(e, a, v)
            case (_, Lt(Qm))            => q.find(v, tpe).in(v1, a).where(e, a, v).compareTo("<", a, v, Var(v1, a.tpeS))
            case (2, Fulltext(Seq(Qm))) => q.find("distinct", Seq(), v, tpe).in(v1, a).fulltext(e, a, v, Var(v1, a.tpeS))
            case (_, Fulltext(Seq(Qm))) => q.find(v, tpe).in(v1, a).fulltext(e, a, v, Var(v1, a.tpeS))

            // Output
            case (_, EntValue) => q.find(e, tpe)
            case (2, VarValue) => q.find("distinct", Seq(), v, tpe).where(e, a, v)
            case (_, VarValue) => q.find(v, tpe).where(e, a, v)
            case (2, EnumVal)  => q.find("distinct", Seq(), v2, tpe).enum(e, a, v)
            case (_, EnumVal)  => q.find(v2, tpe).enum(e, a, v)

            // Expressions
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(eP + _))
            case (2, Eq(ss)) if ss.size > 1           => q.find("distinct", Seq(), v, tpe).orRules(e, a, ss).where(e, a, v)
            case (_, Eq(ss)) if ss.size > 1           => q.find(v, tpe).orRules(e, a, ss)
            case (_, Eq(s :: Nil)) if isEnum          => q.find(v2, tpe).where(e, a, Val(eP + s, a.tpeS)).enum(e, a, v) // todo: can we output a constant value instead?
            case (_, Eq(s :: Nil))                    => q.find(v, tpe).where(e, a, Val(s, a.tpeS)).where(e, a, v) // todo: can we output a constant value instead?
            case (_, Lt(arg))                         => q.find(v, tpe).where(e, a, v).compareTo("<", a, v, Val(arg, a.tpeS))
            case (_, Fn("count")) if a.name == "e"    => q.find("count", Seq(), e, "Int")
            case (_, Fn("count"))                     => q.find("count", Seq(), e, "Int").where(e, a, v)
            case (2, Fulltext(qv :: Nil))             => q.find("distinct", Seq(), v, tpe).fulltext(e, a, v, Val(qv, a.tpeS))
            case (_, Fulltext(qv :: Nil))             => q.find(v, tpe).fulltext(e, a, v, Val(qv, a.tpeS))
            case (_, Fulltext(qvs))                   => q.find(v, tpe).fulltext(e, a, v, Var(v1, a.tpeS)).orRules(v1, a, qvs)


            // Manipulate
            case (_, Replace(_)) => q
            case (_, Remove(_))  => q

            case (c, va) => sys.error(s"[Model2Query] Unresolved Atom with cardinality/value: $c / $va")
          }
        }

        case Bond(ns, refAttr, refNs) => q.ref(e, ns, refAttr, v, refNs)

        case Group(ref, elements) => q

        case unresolved => sys.error("[Model2Query] Unresolved model (we should never get here): " + unresolved)
      }
    }

    val query = new Query(Find(Seq()), With(Seq()), In(Seq()), Where(List()))
    model.elements.foldLeft((query, "ent", "a")) { case ((q1, e1, v1), element) =>

      val ns = element match {
        case Atom(ns1, _, _, _, _, _) => ns1
        case Bond(ns1, _, _)          => ns1
        case Group(ref, elements)     => ref.ns
      }

      val (prevNS, prevAttr, prevRefNs) = if (q1.wh.clauses.isEmpty)
        ("NoValueYet", "NoValueYet", "")
      else
        q1.wh.clauses.reverse.collectFirst {
          case DataClause(_, _, KW(kwNS, attr, refNs), _, _)      => (kwNS, attr, refNs)
          case Funct("fulltext", Seq(_, KW(kwNS, attr, _), _), _) => (kwNS, attr, "")
        }.getOrElse(sys.error("[Model2Query] Missing NS and Attribute\nmodel: " + model + "\nquery: " + q1.toString))

      val nextV = (v1.toCharArray.head + 1).toChar.toString
      val (e2, v2) = {
        if (ns == prevNS) {
          // Same entity (e1 re-used)
          // :community/name -> :community/url
          (e1, nextV)
        } else if (ns == prevAttr || ns == prevRefNs) {
          // Referenced entity
          // :community/neighborhood -> :neighborhood/name
          // :order/lineItems -> :lineItem/product
          (v1, nextV)
        } else if ((prevNS, prevAttr) ==("db", "ident")) {
          // Previous attribute is an enumerated value
          // :db/ident -> :someNS/someAttr
          (e1, nextV)
        } else {
          // First clause
          // e = "?ent" and v = "?a"
          (e1, v1)
        }
      }
      // Add new clause(s) to query
      val q2 = resolve(q1, e2, v2, element)
      // Take next
      (q2, e2, v2)
    }._1 // Get Query
  }
}
