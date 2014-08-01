package molecule.transform
import molecule.ast.query._

case class Query2String(q: Query) {

  def p(expr: QueryExpr): String = expr match {
    case Query(find, widh, in, where) => pp(find, widh, in, where)
    case Find(outputs)                => ":find " + (outputs map p mkString " ")
    case With(variables)              => if (variables.isEmpty) "" else ":with " + (variables map s mkString " ")
    case in@In(_, _, _)               => mkIn(in, false)
    case Where(clauses)               => ":where " + (clauses map p mkString " ")
    case KW(ns, attr, _)              => s":$ns/$attr"
    case AggrExpr(fn, args, v, tpeS)  => s"($fn " + ((args :+ p(v)) mkString " ") + ")"
    case Var("?", _)                  => "?"
    case Var(v, _)                    => "?" + v
    case Val(v, "Int")                => v.toString
    case Val(v, _)                    => "\"" + v + "\""
    case Dummy(_)                     => "_"
    case NoVal                        => ""
    case DS(name)                     => "$" + name
    case DS                           => "$"
    case Empty                        => ""
    case ImplDS                       => ""
    case InDataSource(ds, _)          => p(ds)
    case InVar(binding, _)            => p(binding)
    case Placeholder(v, _, _, _, _)   => "?" + v
    case NoBinding                    => ""
    case ScalarBinding(v)             => p(v)
    case CollectionBinding(v)         => "[" + p(v) + " ...]"
    case TupleBinding(vs)             => "[ " + (vs map p mkString " ") + " ]"
    case RelationBinding(vs)          => "[[ " + (vs map p mkString " ") + " ]]"
    case DataClause(ds, e, a, v, tx)  => pp(ds, e, a, v, tx)
    case Funct(name, ins, outs)       => s"[($name " + (ins map p mkString " ") + ") " + p(outs) + "]"
    case Rule(name, args, clauses)    => s"[[$name " + (args map p mkString " ") + "] " + (clauses map p mkString " ") + "]"
    case RuleInvocation(name, args)   => s"($name " + (args map p mkString " ") + ")"
    case unresolvedQuery              => sys.error(s"\n[Query2String] UNRESOLVED query expression: $unresolvedQuery")
  }

  def pp(es: QueryExpr*): String = es.toList.map(p).filter(_.trim.nonEmpty).mkString("[", " ", "]")

  def s(str: String) = "?" + str

  def mkIn(in: In, bracket: Boolean) = {
    val (l, r) = if (bracket) (":in [ ", " ]") else (":in ", "")
    if (in.inputs.isEmpty && in.rules.isEmpty) ""
    else
      l + ((in.ds map p mkString ("", " ", " "))
        + (if (in.rules.isEmpty) "" else "% ")
        + (in.inputs map p mkString " ")).trim + r
  }

  def toList: String = p(q)

  def toMap: String = {
    s"""{ :find [ ${q.find.outputs map p mkString " "} ]""" +
      (if (q.widh.variables.isEmpty) "" else s" :with [ ${q.widh.variables map s mkString " "} ]") +
      mkIn(q.in, true) +
      s""" :where [ ${q.where.clauses map p mkString " "} ] }"""
  }

  def pretty(maxLength: Int = 80): String = {
    val queryString = p(q)
    val (firstParts, where) = (List(p(q.find), p(q.widh), p(q.in)).filter(_.trim.nonEmpty), p(q.where))
    if (queryString.length > maxLength) {
      firstParts.mkString("[", "\n ", "\n ") +
        (if (where.length > maxLength)
          q.where.clauses.map(p).mkString(":where\n   ", "\n   ", "")
        else where) + "]"
    } else {
      queryString
    }
  }
}