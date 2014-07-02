package molecule.ast
import molecule.transform.Query2String

object query {

  trait QueryExpr

  case class Query(find: Find, widh: With, in: In, where: Where) extends QueryExpr {
    lazy val print = Query2String(this)
    def toList: String = print.toList
    def toMap: String = print.toMap
    def pretty(maxLength: Int): String = print.pretty(maxLength)
    def pretty: String = pretty(30)
  }

  case class Find(outputs: Seq[Output]) extends QueryExpr
  case class With(variables: Seq[String]) extends QueryExpr
  case class In(inputs: Seq[Input], rules: Seq[Rule] = Seq(), ds: Seq[DataSource] = Seq(DS)) extends QueryExpr
  case class Where(clauses: Seq[Clause]) extends QueryExpr

  trait QueryTerm extends QueryExpr
  case object Empty extends QueryTerm

  trait Output extends QueryExpr {val tpeS: String}
  case class AggrExpr(fn: String, args: Seq[String], v: Var, tpeS: String) extends Output

  case class KW(ns: String, name: String) extends QueryTerm

  sealed trait QueryValue extends QueryTerm
  case class Var(v: String, tpeS: String = "") extends QueryValue with Output
  case class Val(v: String, tpeS: String = "String") extends QueryValue
  case class Dummy(v: String = "") extends QueryValue
  case class NoVal(v: String = "") extends QueryValue


  sealed trait DataSource extends QueryTerm
  case class DS(name: String = "") extends DataSource
  case object DS extends DataSource
  case object ImplDS extends DataSource

  case class Rule(name: String, args: Seq[QueryValue], clauses: Seq[DataClause]) extends QueryTerm

  trait Input extends QueryTerm
  case class InDataSource(ds: DataSource, argss: Seq[Seq[String]] = Seq(Seq())) extends Input
  case class InVar(binding: Binding, argss: Seq[Seq[String]] = Seq(Seq())) extends Input
  case class Placeholder(v: String, kw: KW, tpeS: String, enumPrefix: Option[String] = None, e: String = "") extends Input


  sealed trait Binding extends QueryTerm
  case object NoBinding extends Binding
  case class ScalarBinding(v: Var) extends Binding
  case class CollectionBinding(v: Var) extends Binding
  case class TupleBinding(vs: Seq[Var]) extends Binding
  case class RelationBinding(vs: Seq[Var]) extends Binding

  sealed trait Clause extends QueryExpr

  case class DataClause(ds: DataSource, entity: Var, attr: KW, value: QueryValue, tx: QueryTerm) extends Clause
  case class RuleInvocation(name: String, args: Seq[QueryValue]) extends Clause

  sealed trait ExpressionClause extends Clause
  case class Predicate(name: String, args: Seq[QueryTerm]) extends ExpressionClause
  case class Funct(name: String, ins: Seq[QueryTerm], outs: Binding) extends ExpressionClause


  // Convenience constructors ........................................

  object Query {
    def apply(find: Find, where: Where) = new Query(find, With(Seq()), In(Seq()), where)
    def apply(find: Find, in: In, where: Where) = new Query(find, With(Seq()), in, where)
    def apply(find: Find) = new Query(find, With(Seq()), In(Seq()), Where(List()))
    def apply() = new Query(Find(Seq()), With(Seq()), In(Seq()), Where(List()))
  }
  object DataClause {
//    def apply(e: String, attr: KW, tpeS: String, sym: Symbol) = new DataClause(ImplicitDS, Var(e, tpeS), attr, Var(sym.name, tpeS), Empty)
    def apply(e: String, attr: KW, tpeS: String, v: String) = new DataClause(ImplDS, Var(e, tpeS), attr, Var(v, tpeS), Empty)
    def apply(e: String, attr: KW, tpeS: String, value: Val) = new DataClause(ImplDS, Var(e, tpeS), attr, value, Empty)

    // For simplifying tests
//    def apply(e: Symbol, attr: KW, tpeS: String = "String", v: String = "") = new DataClause(ImplicitDS, Var(e.name, tpeS), attr, Val(v), Empty)
//    def apply(e: Symbol, attr: KW, v: Symbol, tpeS: String = "String") = new DataClause(ImplicitDS, Var(e.name, tpeS), attr, Var(v.name, tpeS), Empty)
//    def apply(e: Symbol, attr: KW, tpeS: String, v: Var) = new DataClause(ImplicitDS, Var(e.name, tpeS), attr, v, Empty)
  }
  object KW {
    // For simplifying tests
//    def apply(ns: Symbol, name: Symbol) = new KW(ns.name, name.name)
  }
  object Find {
    // For simplifying tests
//    def apply(sym: Symbol, tpeS: String = "String"): Find = new Find(Seq(Var(sym.name, tpeS)))
  }
  object Where {
//    def apply(dc: DataClause): Where = new Where(List(dc))
  }
}