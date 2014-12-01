package molecule.ops
import molecule.ast.model._
import molecule.ast.query._

object QueryOps {
  type KeepQueryOps = Int

  implicit class QueryOps(q: Query) {

    // Find ..........................................

    def find(fn: String, args: Seq[String], v: String, tx: Seq[Generic]): Query =
      find(AggrExpr(fn, args, Var(v)), tx)

    def find(v: String, tx: Seq[Generic]): Query =
      find(Var(v), tx)

    def find(tx: Seq[Generic]): Query =
      find(NoVal, tx, "")

    def find(fn: String, args: Seq[String], v: String, tx: Seq[Generic], attrV: String): Query =
      find(AggrExpr(fn, args, Var(v)), tx, attrV)

    def find(v: String, tx: Seq[Generic], attrV: String): Query =
      find(Var(v), tx, attrV)

    def find(o: Output, g: Seq[Generic], attrV: String = ""): Query = {
      val genericVars = g.map {
        case TxValue        => Var("tx")
        case TxTValue       => Var("txT")
        case TxInstantValue => Var("txInst")
        case OpValue        => Var("op")
        case AttrVar(v)     => Var(attrV)
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

    def placeholder(v: String, a: Atom, enumPrefix: Option[String] = None, e: String = ""): Query =
      q.copy(i = q.i.copy(inputs = q.i.inputs :+ Placeholder(v, KW(a.ns, a.name), enumPrefix, e)))


    // Where ..........................................

    def where(e: String, ns: String, attr: String, v: String, refNs: String, tx: Seq[Generic]): Query = {
      val attrClauses = if (tx.isEmpty)
        Seq(DataClause(ImplDS, Var(e), KW(ns, attr, refNs), Var(v), Empty))
      else {
        val extendedClause = if (tx.contains(OpValue))
          DataClause(ImplDS, Var(e), KW(ns, attr, refNs), Var(v), Var("tx"), Var("op"))
        else
          DataClause(ImplDS, Var(e), KW(ns, attr, refNs), Var(v), Var("tx"), NoBinding)

        val extraClauses = tx.flatMap {
          case TxTValue       => Seq(Funct("datomic.Peer/toT ^Long", Seq(Var("tx")), ScalarBinding(Var("txT"))))
          case TxInstantValue => Seq(DataClause(ImplDS, Var("tx"), KW("db", "txInstant", ""), Var("txInst"), Empty))
          case _              => Nil
        }
        extendedClause +: extraClauses
      }
      q.copy(wh = Where(q.wh.clauses ++ attrClauses))
    }

    def where(e: String, a: Atom, v: String, tx: Seq[Generic]): Query =
      where(e, a.ns, a.name, v, "", tx)

    def where(e: String, a: Atom, qv: Val, tx: Seq[Generic]): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e), KW(a.ns, a.name), qv, Empty)))

    //    def where(e: String, v: String): Query =
    //      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e), KW("?", v), NoVal, Empty)))


    // Extra ..........................................

    def attr(e: String, v: String, v1: String, v2: String, tx: Seq[Generic]) =
      q.where(e, "?", "attr", v, "", tx)
        .ident("attr", v1)
        .func(".toString ^clojure.lang.Keyword", Seq(Var(v1)), ScalarBinding(Var(v2)))

    def enum(e: String, a: Atom, v: String, tx: Seq[Generic] = Seq()): Query =
      q.where(e, a, v, tx).ident(v, v + 1).kw(v + 1, v + 2)

    def ident(v: String, v1: String, tx: Seq[Generic] = Seq()) =
      q.where(v, "db", "ident", v1, "", tx)

    def kw(v1: String, v2: String) =
      q.func(".getName ^clojure.lang.Keyword", Seq(Var(v1)), ScalarBinding(Var(v2)))

    def compareTo(op: String, a: Atom, v: String, qv: QueryValue): Query =
      q.func(".compareTo ^" + a.tpeS, Seq(Var(v), qv), ScalarBinding(Var(v + 2)))
        .func(op, Seq(Var(v + 2), Val(0)))

    def fulltext(e: String, a: Atom, v: String, qv: QueryValue): Query =
    // todo: Var("a") ??
      q.func("fulltext", Seq(DS(), KW(a.ns, a.name), qv), RelationBinding(Seq(Var("a"), Var(v))))

    def orRules(e: String, a: Atom, args: Seq[Any], tx: Seq[Generic] = Seq()): Query = {
      val ruleName = "rule" + (q.i.rules.map(_.name).distinct.size + 1)
      val newRules = args.foldLeft(q.i.rules) { case (rules, arg) =>
        val dataClause = DataClause(ImplDS, Var(e), KW(a.ns, a.name), Val(arg), Empty)
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
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e), KW(ns, refAttr, refNs), Var(v), Empty)))
  }
}
