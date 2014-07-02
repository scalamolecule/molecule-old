package molecule.ops
import molecule.ast.model.Atom
import molecule.ast.query._

object QueryOps {
  type KeepQueryOps = Int

  implicit class QueryOps(q: Query) {
    val ent = "ent"
    def format = {
      val sep = "\n    "
      val widh = if (q.widh.variables.isEmpty) "" else q.widh.variables.mkString("\n  With(List(\n    ", sep, "))")
      val in = if (q.in.inputs.isEmpty) "" else q.in.inputs.mkString("\n  In(List(\n    ", sep, "))")
      s"""
          |Query(
          |  Find(List(${q.find.outputs.map(sep + _)}))$widh$in
          |  Where(List(${q.where.clauses.map(sep + _)}))
          |)""".stripMargin
    }


    def output(fn: String, args: Seq[String], v: String, t: String): Query =
      out(AggrExpr(fn, args, Var(v, t), t))

    def output(v: String, t: String): Query =
      out(Var(v, t))

    def out(o: Output): Query = if (!q.find.outputs.contains(o))
      q.copy(find = Find(q.find.outputs :+ o))
    else q


    def input(v: String, a: Atom, enumPrefix: Option[String] = None, e: String = ""): Query = {
      val newIn = In
      q.copy(in = q.in.copy(inputs = q.in.inputs :+ Placeholder(v, KW(a.ns, a.name), a.tpeS, enumPrefix, e)))
    }

    def data(e: String, a: Atom): Query = data(e, a.ns, a.name, a.tpeS)
    def data(e: String, a: Atom, v: String): Query = data(e, a.ns, a.name, a.tpeS, v)

    def data(e: String, ns: String, a: String, t: String): Query =
      q.copy(where = Where(q.where.clauses :+ DataClause(ImplDS, Var(e, t), KW(ns, a), NoVal(), Empty)))

    def data(e: String, ns: String, a: String, t: String, qvs: String): Query =
      q.copy(where = Where(q.where.clauses :+ DataClause(e, KW(ns, a), t, qvs)))

    def data(e: String, a: Atom, qv: Val): Query =
      q.copy(where = Where(q.where.clauses :+ DataClause(e, KW(a.ns, a.name), a.tpeS, qv)))


    def enum(e: String, a: Atom, v: String, output: Int = 1): Query = {
      val q1 = q.data(e, a.ns, a.name, a.tpeS, v)
      if (output == 0)
        q1
      else
        q1.data(v, "db", "ident", "String", v + 1)
          .func(".getName ^clojure.lang.Keyword", Seq(Var(v + 1)), ScalarBinding(Var(v + 2)))
    }

    def compare(op: String, e: String, a: Atom, v: String, qv: QueryValue, v2: String): Query =
      compare(op, e, a.ns, a.name, a.tpeS, v, qv, v2)

    def compare(op: String, e: String, ns: String, a: String, t: String, v: String, qv: QueryValue, v2: String): Query =
      q.output(v, t)
        .data(e, ns, a, t, v)
        .func(".compareTo ^" + t, Seq(Var(v), qv), ScalarBinding(Var(v2)))
        .func(op, Seq(Var(v2), Val("0", "Int")))


    def fulltext(e: String, a: Atom, v: String, qv: QueryValue): Query =
      fulltext(e, a.ns, a.name, a.tpeS, v, qv)

    def fulltext(e: String, ns: String, a: String, t: String, v: String, qv: QueryValue): Query =
      q.func("fulltext", Seq(DS(), KW(ns, a), qv), RelationBinding(Seq(Var("ent"), Var(v))))


    def orRules(e: String, a: Atom, qvs: Seq[String]): Query = {
      val ruleName = "rule" + (q.in.rules.map(_.name).distinct.size + 1)
      val newRules = qvs.foldLeft(q.in.rules) { case (rules, s) =>
        val dataClause = DataClause(e, KW(a.ns, a.name), a.tpeS, Val(s))
        val rule = Rule(ruleName, Seq(Var(e)), Seq(dataClause))
        rules :+ rule
      }
      val newIn = q.in.copy(ds = (q.in.ds :+ DS).distinct, rules = newRules)
      val newWhere = Where(q.where.clauses :+ RuleInvocation(ruleName, Seq(Var(e))))
      q.copy(in = newIn, where = newWhere)
    }

    def func(name: String, ins: Seq[QueryTerm], outs: Binding = NoBinding): Query = {
      q.copy(where = Where(q.where.clauses :+ Funct(name, ins, outs)))
    }

    def ref(e: String, ns: String, a: String, s: String): Query =
      q.copy(where = Where(q.where.clauses :+ DataClause(e, KW(ns, a), "String", s)))
  }
}
