package molecule
package transform
import molecule.ast.model._
import molecule.ast.query._
import molecule.ops.QueryOps._
import molecule.util.Debug

object Model2Query extends Debug {

  def apply(model: Model): Query = {

    def resolve(q: Query, e: String, v: String, element: Element) = {
      val (v1, v2) = (v + 1, v + 2)

      element match {
        case a@Atom(ns, attr, t, cardinality, value, enumPrefix) =>
          val eid = attr == "eid"
          (cardinality, value, enumPrefix) match {
            case (_, NoValue, _)  => q.data(e, a)
            case (_, EntValue, _) => q.output(e, t)
            case (2, VarValue, _) => q.data(e, a, v).output("distinct", Seq(), v, t)
            case (_, VarValue, _) => q.data(e, a, v).output(v, t)
            case (_, EnumVal, _)  => q.enum(e, a, v).output(v2, t)

            // Input
            case (_, Qm, p@Some(_))  => q.enum(e, a, v, 0).input(v, a, p, e)
            case (_, QmR, p@Some(_)) => q.enum(e, a, v).input(v, a, p).output(v2, t)
            case (_, Qm, _)          => q.data(e, a, v).input(v, a)
            case (2, QmR, _)         => q.data(e, a, v).input(v, a).output("distinct", Seq(), v, t)
            case (_, QmR, _)         => q.data(e, a, v).input(v, a).output(v, t)

            // Values
            case (_, Eq(ss), Some(prefix)) if ss.size > 1 => q.orRules(e, a, ss.map(prefix + _))
            case (_, Eq(ss), _) if ss.size > 1            => q.orRules(e, a, ss)
            case (_, Eq(s :: Nil), Some(prefix))          => q.data(e, a, Val(prefix + s))
            case (_, Eq(s :: Nil), _)                     => q.data(e, a, Val(s))

            // Compare
            case (_, Lt(Qm), _)  => q.compare("<", e, a, v, Var(v1), v2).input(v1, a)
            case (_, Lt(arg), _) => q.compare("<", e, a, v, Val(arg), v1)

            // Functions
            case (_, Fn("count"), _) if eid => q.output("count", Seq(), e, "Int")
            case (_, Fn("count"), _)        => q.data(e, a, v).output("count", Seq(), e, "Int")

            // Fulltext search
            case (2, Fulltext(Seq(Qm)), _)   => q.fulltext(e, a, v, Var(v1)).output("distinct", Seq(), v, t).input(v1, a)
            case (_, Fulltext(Seq(Qm)), _)   => q.fulltext(e, a, v, Var(v1)).output(v, t).input(v1, a)
            case (2, Fulltext(qv :: Nil), _) => q.fulltext(e, a, v, Val(qv)).output("distinct", Seq(), v, t)
            case (_, Fulltext(qv :: Nil), _) => q.fulltext(e, a, v, Val(qv)).output(v, t)
            case (_, Fulltext(qvs), _)       => q.fulltext(e, a, v, Var(v1)).output(v, t).orRules(v1, a, qvs)

            // Manipulation operatins are only processed in Model2Transaction, so here we just pass on an un-modified Query object
            case (_, Replace(_), _) => q
            case (_, Remove(_), _)  => q
          }

        case Bond(ns, refAttr, refNs) => q.ref(e, ns, refAttr, v, refNs)

        case Group(ref, elements) => q

        case unresolved => sys.error("[Model2Query] Unresolved model (we should never get here): " + unresolved)
      }
    }

    model.elements.foldLeft((Query(), "ent", "a")) { case ((q1, e1, v1), element) =>

      val ns = element match {
        case Atom(ns1, _, _, _, _, _) => ns1
        case Bond(ns1, _, _)          => ns1
        case Group(ref, elements)     => ref.ns
      }

      val (prevNS, prevAttr, prevRefNs) = if (q1.where.clauses.isEmpty)
        ("NoValueYet", "NoValueYet", "")
      else
        q1.where.clauses.reverse.collectFirst {
          case DataClause(_, _, KW(kwNS, attr, refNs), _, _)      => (kwNS, attr, refNs)
          case Funct("fulltext", Seq(_, KW(kwNS, attr, _), _), _) => (kwNS, attr, "")
        }.getOrElse(sys.error("[Model2Query] Missing NS and Attribute\nmodel: " + model + "\nquery: " + q1.format))

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
        } else if ((prevNS, prevAttr) == ("db", "ident")) {
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
