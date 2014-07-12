package molecule.transform
import molecule.ast.model._
import molecule.ast.query._
import molecule.ops.QueryOps._
import molecule.util.Debug

object Model2Query extends Debug {

  def apply(model: Model): Query = {

    def resolve(q: Query, e: String, v: String, element: Element) = {
      val (v1, v2) = (v + 1, v + 2)

      element match {
        case a@Atom(ns, attr, t, cardinality, value, enumPrefix) => (cardinality, value, enumPrefix) match {
          case (_, NoValue, _)  => q.data(e, a)
          case (_, EntValue, _) => q.data(e, a).output(e, t)
          case (2, VarValue, _) => q.data(e, a, v).output("distinct", Seq(), v, t)
          case (_, VarValue, _) => q.data(e, a, v).output(v, t)
          case (_, EnumVal, _)  => q.enum(e, a, v).output(v2, t)

          // Equals
          case (_, Eq(Seq("?")), p@Some(_))             => q.enum(e, a, v, 0).input(v, a, p, e)
          case (_, Eq(Seq("?!")), p@Some(_))            => q.enum(e, a, v).input(v, a, p).output(v2, t)
          case (_, Eq(Seq("?")), _)                     => q.data(e, a, v).input(v, a)
          case (2, Eq(Seq("?!")), _)                    => q.data(e, a, v).input(v, a).output("distinct", Seq(), v, t)
          case (_, Eq(Seq("?!")), _)                    => q.data(e, a, v).input(v, a).output(v, t)
          case (_, Eq(ss), Some(prefix)) if ss.size > 1 => q.orRules(e, a, ss.map(prefix + _))
          case (_, Eq(ss), _) if ss.size > 1            => q.orRules(e, a, ss)
          case (_, Eq(s :: Nil), Some(prefix))          => q.data(e, a, Val(prefix + s))
          case (_, Eq(s :: Nil), _)                     => q.data(e, a, Val(s))

          // Compare
          case (_, Lt("?"), _) => q.compare("<", e, a, v, Var(v1), v2).input(v1, a)
          case (_, Lt(st), _)  => q.compare("<", e, a, v, Val(st), v1)

          // Fulltext search
          case (2, Fulltext(Seq("?")), _)  => q.fulltext(e, a, v, Var(v1)).output("distinct", Seq(), v, t).input(v1, a)
          case (_, Fulltext(Seq("?")), _)  => q.fulltext(e, a, v, Var(v1)).output(v, t).input(v1, a)
          case (2, Fulltext(qv :: Nil), _) => q.fulltext(e, a, v, Val(qv)).output("distinct", Seq(), v, t)
          case (_, Fulltext(qv :: Nil), _) => q.fulltext(e, a, v, Val(qv)).output(v, t)
          case (_, Fulltext(qvs), _)       => q.fulltext(e, a, v, Var(v1)).output(v, t).orRules(v1, a, qvs)
        }

        case Bond(ns1, ns2) => q.ref(e, ns1, ns2, v)

        case unresolved => sys.error("[Model2Query] Unresolved model (we should never get here): " + unresolved)
      }
    }

    model.elements.foldLeft((Query(), "ent", "a")) { case ((q1, e1, v1), element) =>
      val ns = element match {
        case Atom(ns1, _, _, _, _, _) => ns1
        case Bond(ns1, _)             => ns1
      }

      val (prevNS, prevAttr) = if (q1.where.clauses.isEmpty)
        ("NoValueYet", "NoValueYet")
      else
        q1.where.clauses.reverse.collectFirst {
          case DataClause(_, _, KW(kwNS, name), _, _)          => (kwNS, name)
          case Funct("fulltext", Seq(_, KW(kwNS, name), _), _) => (kwNS, name)
        }.getOrElse(sys.error("[Model2Query] Missing NS and Attribute\nmodel: " + model + "\nquery: " + q1.format))

      val nextV = (v1.toCharArray.head + 1).toChar.toString
      val (e2, v2) = {
        if (ns == prevNS)
          (e1, nextV) // :community/name -> :community/url
        else if (ns == prevAttr)
          (v1, nextV) // :community/neighborhood -> :neighborhood/name
        else if ((prevNS, prevAttr) ==("db", "ident"))
          (e1, nextV) // :db/ident -> :someNS/someAttr
        else
          (e1, v1) // First clause starts with e = "?ent" and v = "?a"
      }
      // Add new clause(s) to query
      val q2 = resolve(q1, e2, v2, element)
      // Take next
      (q2, e2, v2)
    }._1 // Get Query
  }
}
