package molecule.ops
import molecule.ast.model.Atom
import molecule.ast.query._

object QueryOps {
  type KeepQueryOps = Int

  implicit class QueryOps(q: Query) {
    val ent = "ent"
    def format = {
      val sep = "\n    "
      val widh = if (q.wi.variables.isEmpty) "" else q.wi.variables.mkString("\n  With(List(\n    ", sep, "))")
      val in = if (q.i.inputs.isEmpty) "" else q.i.inputs.mkString("\n  In(List(\n    ", sep, "))")
      s"""
          |Query(
          |  Find(List(${q.f.outputs.map(sep + _)}))$widh$in
          |  Where(List(${q.wh.clauses.map(sep + _)}))
          |)""".stripMargin
    }


    // Find ..........................................

    def find(fn: String, args: Seq[String], v: String, t: String): Query =
      find(AggrExpr(fn, args, Var(v, t), t))

    def find(v: String, t: String): Query =
      find(Var(v, t))

    def find(o: Output): Query = if (!q.f.outputs.contains(o))
      q.copy(f = Find(q.f.outputs :+ o))
    else q


    // In ..........................................

    def in(v: String, a: Atom, enumPrefix: Option[String] = None, e: String = ""): Query =
      q.copy(i = q.i.copy(inputs = q.i.inputs :+ Placeholder(v, KW(a.ns, a.name), a.tpeS, enumPrefix, e)))


    // With ..........................................


    // Where ..........................................


    def where(e: String, ns: String, attr: String, t: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e, t), KW(ns, attr), NoVal, Empty)))

    def where(e: String, ns: String, attr: String, t: String, v: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e, t), KW(ns, attr), Var(v, t), Empty)))

//    def where(e: String, a: Atom): Query = where(e, a.ns, a.name, a.tpeS)
    def where(e: String, a: Atom, v: String): Query = where(e, a.ns, a.name, a.tpeS, v)
    def where(e: String, a: Atom, qv: Val): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e, a.tpeS), KW(a.ns, a.name), qv, Empty)))


    // Other..........................................

    def enum(e: String, a: Atom, v: String, output: Int = 1): Query = q.where(e, a, v).ident(v, v + 1).kw(v + 1, v + 2)
    def ident(v: String, v1: String) = q.where(v, "db", "ident", "String", v1)
    def kw(v1: String, v2: String) = q.func(".getName ^clojure.lang.Keyword", Seq(Var(v1)), ScalarBinding(Var(v2)))

    def compareTo(op: String, a: Atom, v: String, qv: QueryValue): Query =
      q.func(".compareTo ^" + a.tpeS, Seq(Var(v), qv), ScalarBinding(Var(v + 2)))
        .func(op, Seq(Var(v + 2), Val(0, "Int")))

    def fulltext(e: String, a: Atom, v: String, qv: QueryValue): Query =
      q.func("fulltext", Seq(DS(), KW(a.ns, a.name), qv), RelationBinding(Seq(Var("ent"), Var(v))))
    //      fulltext(e, a.ns, a.name, a.tpeS, v, qv)

    //    def fulltext(e: String, ns: String, attr: String, t: String, v: String, qv: QueryValue): Query =
    //      q.func("fulltext", Seq(DS(), KW(ns, attr), qv), RelationBinding(Seq(Var("ent"), Var(v))))


    def orRules(e: String, a: Atom, args: Seq[Any]): Query = {
      val ruleName = "rule" + (q.i.rules.map(_.name).distinct.size + 1)
      val newRules = args.foldLeft(q.i.rules) { case (rules, arg) =>
        val dataClause = DataClause(ImplDS, Var(e, a.tpeS), KW(a.ns, a.name), Val(arg), Empty)
        val rule = Rule(ruleName, Seq(Var(e)), Seq(dataClause))
        rules :+ rule
      }
      val newIn = q.i.copy(ds = (q.i.ds :+ DS).distinct, rules = newRules)
      val newWhere = Where(q.wh.clauses :+ RuleInvocation(ruleName, Seq(Var(e))))
      q.copy(i = newIn, wh = newWhere)
    }

    def func(name: String, ins: Seq[QueryTerm], outs: Binding = NoBinding): Query =
      q.copy(wh = Where(q.wh.clauses :+ Funct(name, ins, outs)))

    def ref(e: String, ns: String, refAttr: String, v: String, refNs: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e, "String"), KW(ns, refAttr, refNs), Var(v, "String"), Empty)))
  }
}
