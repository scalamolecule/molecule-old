package molecule.ops
import molecule.ast.model._
import molecule.ast.query._

object QueryOps {
  type KeepQueryOps = Int

  implicit class QueryOps(q: Query) {

    // Find ..........................................

    def find(fn: String, args: Seq[Any], v: String, gs: Seq[Generic]): Query =
      find(AggrExpr(fn, args, Var(v)), gs)

    def find(v: String, gs: Seq[Generic]): Query =
      find(Var(v), gs)

    def find(gs: Seq[Generic]): Query =
      find(NoVal, gs, "")

    def find(fn: String, args: Seq[String], v: String, gs: Seq[Generic], attrV: String): Query =
      find(AggrExpr(fn, args, Var(v)), gs, attrV)

    def find(v: String, gs: Seq[Generic], attrV: String): Query =
      find(Var(v), gs, attrV)

    def find(o: Output, gs: Seq[Generic], attrV: String = ""): Query = {
      val genericVars = gs.flatMap {
        case AttrVar(v)     => Some(Var(attrV))
        case TxValue        => Some(Var("tx"))
        case TxTValue       => Some(Var("txT"))
        case TxInstantValue => Some(Var("txInst"))
        case OpValue        => Some(Var("op"))
        case other          => None
      }.distinct
      val moreOutputs = o match {
        case NoVal                         => genericVars
        case _ if !q.f.outputs.contains(o) => o +: genericVars
        case _                             => genericVars
      }
      q.copy(f = Find(q.f.outputs ++ moreOutputs))
    }


    // In ..........................................

    def in(v: String, a: Atom, enumPrefix: Option[String] = None, e: String = ""): Query =
      q.copy(i = q.i.copy(inputs = q.i.inputs :+ Placeholder(v, KW(a.ns, a.name), enumPrefix, e)))

    def in(v: String, ns: String, attr: String, e: String): Query =
      q.copy(i = q.i.copy(inputs = q.i.inputs :+ Placeholder(v, KW(ns, attr), None, e)))

    //    def placeholder(v: String, a: Atom, enumPrefix: Option[String] = None, e: String = ""): Query =
    //      q.copy(i = q.i.copy(inputs = q.i.inputs :+ Placeholder(v, KW(a.ns, a.name), enumPrefix, e)))


    // With ...........................................

    def widh(v: String): Query = q.copy(wi = With(q.wi.variables :+ v))


    // Where ..........................................

    def where(e: String, ns: String, attr: String, v: QueryValue, refNs: String, gs: Seq[Generic]): Query = {
      val attrClauses = if (gs.isEmpty)
        Seq(DataClause(ImplDS, Var(e), KW(ns, attr, refNs), v, Empty))
      else {
        val extendedClause = if (gs.contains(OpValue))
          DataClause(ImplDS, Var(e), KW(ns, attr, refNs), v, Var("tx"), Var("op"))
        else if (gs.contains(TxValue) || gs.contains(TxTValue) || gs.contains(TxInstantValue))
          DataClause(ImplDS, Var(e), KW(ns, attr, refNs), v, Var("tx"), NoBinding)
        else
          DataClause(ImplDS, Var(e), KW(ns, attr, refNs), v, NoBinding, NoBinding)

        val extraClauses = gs.flatMap {
          case TxTValue       => Seq(Funct("datomic.Peer/toT ^Long", Seq(Var("tx")), ScalarBinding(Var("txT"))))
          case TxInstantValue => Seq(DataClause(ImplDS, Var("tx"), KW("db", "txInstant", ""), Var("txInst"), Empty))
          case _              => Nil
        }
        extendedClause +: extraClauses
      }
      q.copy(wh = Where(q.wh.clauses ++ attrClauses))
    }

    def where(e: String, a: Atom, v: String, gs: Seq[Generic]): Query =
      where(e, a.ns, a.name, Var(v), "", gs)

    def where(e: String, a: Atom, qv: Val, gs: Seq[Generic]): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e), KW(a.ns, a.name), qv, Empty)))


    // Meta ..........................................

    def attr(e: String, v: QueryValue, v1: String, v2: String, gs: Seq[Generic]) = {
      // Build on from `ns` ident if it is already there
      q.wh.clauses.collectFirst {
        case DataClause(ImplDS, Var("ns"), KW("db", "ident", _), Var(i), _, _) =>
          q.func(".toString ^clojure.lang.Keyword", Seq(Var(i)), ScalarBinding(Var(v2)))
      } getOrElse
        q.where(e, "?", "attr", v, "", gs)
          .ident("attr", v1)
          .func(".toString ^clojure.lang.Keyword", Seq(Var(v1)), ScalarBinding(Var(v2)))
    }

    def ns(e: String, v: QueryValue, v1: String, v2: String, gs: Seq[Generic]) = {
      // Build on from `attr` ident if it is already there
      q.wh.clauses.collectFirst {
        case DataClause(ImplDS, Var("attr"), KW("db", "ident", _), Var(i), _, _) =>
          q.func(".getNamespace ^clojure.lang.Keyword", Seq(Var(i)), ScalarBinding(Var(v2)))
      } getOrElse
        q.where(e, "?", "ns", v, "", gs)
          .ident("ns", v1)
          .func(".getNamespace ^clojure.lang.Keyword", Seq(Var(v1)), ScalarBinding(Var(v2)))
    }


    // Extra ..........................................

    def enum(e: String, a: Atom, v: String, gs: Seq[Generic] = Seq()): Query =
      q.where(e, a, v, gs).ident(v, v + 1).kw(v + 1, v + 2)

    def ident(v: String, v1: String, gs: Seq[Generic] = Seq()) =
      q.where(v, "db", "ident", Var(v1), "", gs)

    def kw(v1: String, v2: String) =
      q.func(".getName ^clojure.lang.Keyword", Seq(Var(v1)), ScalarBinding(Var(v2)))

    def cast(v1: String, v2: String) =
      q.func(".toString", Seq(Var(v1)), ScalarBinding(Var(v2)))

    def compareTo(op: String, a: Atom, v: String, qvs: Seq[QueryValue]): Query =
      qvs.zipWithIndex.foldLeft(q) { case (q1, (qv, i)) => q1.compareTo(op, a, v, qv, i + 1)}

    def compareTo(op: String, a: Atom, v: String, qv: QueryValue, i: Int = 0): Query = {
      val w = if (i > 0) v + "_" + i else v + 2
      q.func(".compareTo ^" + a.tpeS, Seq(Var(v), qv), ScalarBinding(Var(w)))
        .func(op, Seq(Var(w), Val(0)))
    }

    def fulltext(e: String, a: Atom, v: String, qv: QueryValue): Query =
      q.func("fulltext", Seq(DS(), KW(a.ns, a.name), qv), RelationBinding(Seq(Var(e), Var(v))))

    def orRules(e: String, a: Atom, args: Seq[Any], gs: Seq[Generic] = Seq(), v: String = ""): Query = {
      val ruleName = "rule" + (q.i.rules.map(_.name).distinct.size + 1)
      val newRules = args.foldLeft(q.i.rules) { case (rules, arg) =>
        val arg1 = arg match {
          case s: String => s.replaceAll("\"", "\\\\\"")
          case other     => other
        }
        val dataClauses = if (v.nonEmpty)
          Seq(
            Funct(s"""ground (java.net.URI. "$arg1")""", Nil, ScalarBinding(Var(v))),
            DataClause(ImplDS, Var(e), KW(a.ns, a.name), Var(v), Empty))
        else
          Seq(DataClause(ImplDS, Var(e), KW(a.ns, a.name), Val(arg1), Empty))
        val rule = Rule(ruleName, Seq(Var(e)), dataClauses)
        rules :+ rule
      }
      val newIn = q.i.copy(ds = (q.i.ds :+ DS).distinct, rules = newRules)
      val newWhere = Where(q.wh.clauses :+ RuleInvocation(ruleName, Seq(Var(e))))
      q.copy(i = newIn, wh = newWhere)
    }

    def transitive(ns: String, attr: String, v1: String, v2: String, depth: Int): Query = {
      val ruleName = "transitive" + depth
      val newRules: Seq[Rule] = 1 to depth map {
        case 1 => Rule("transitive1", Seq(Var("attr"), Var("v1"), Var("v2")), Seq(
          DataClause(ImplDS, Var("x"), KW("?", "attr"), Var("v1"), Empty),
          DataClause(ImplDS, Var("x"), KW("?", "attr"), Var("v2"), Empty),
          Funct("!=", Seq(Var("v1"), Var("v2")), NoBinding)))
        case n => Rule(s"transitive$n", Seq(Var("attr"), Var("v1"), Var("v2")), Seq(
          RuleInvocation(s"transitive${n - 1}", Seq(KW("?", "attr"), Var("v1"), Var("x"))),
          RuleInvocation(s"transitive${n - 1}", Seq(KW("?", "attr"), Var("x"), Var("v2"))),
          Funct("!=", Seq(Var("v1"), Var("v2")), NoBinding)))
      }

      val newIn = q.i.copy(ds = (q.i.ds :+ DS).distinct, rules = (q.i.rules ++ newRules).distinct)
      val newWhere = Where(q.wh.clauses :+ RuleInvocation(ruleName, Seq(KW(ns, attr), Var(v1), Var(v2))))
      q.copy(i = newIn, wh = newWhere)
    }

    def func(name: String, qt: QueryTerm, v: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ Funct(name, Seq(qt), ScalarBinding(Var(v)))))

    def func(name: String, ins: Seq[QueryTerm], outs: Binding = NoBinding): Query =
      q.copy(wh = Where(q.wh.clauses :+ Funct(name, ins, outs)))

    def ref(e: String, ns: String, refAttr: String, v: String, refNs: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e), KW(ns, refAttr, refNs), Var(v), Empty)))
  }
}
