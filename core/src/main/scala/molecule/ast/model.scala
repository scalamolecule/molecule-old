package molecule.ast

object model {

  case class Model(elements: Seq[Element])

  trait Element
  case class Bond(ns1: String, ns2: String) extends Element
  case class Atom(ns: String, name: String, tpeS: String, card: Int, value: Value, enumPrefix: Option[String] = None) extends Element

  sealed trait Value
  case object NoValue extends Value
  case object Blank extends Value
  case object EntValue extends Value
  case object VarValue extends Value
  case object EnumVal extends Value

  // Expressions
  case class Eq(values: Seq[String]) extends Value
  case class Lt(value: String) extends Value
  case class Fulltext(search: Seq[String]) extends Value

  // Actions
  case class Replace(oldNew: Map[Any, Any]) extends Value
  case class Remove(value: Seq[Any]) extends Value

  trait Expression

  trait Exp1[T1] extends Expression {
    def or(b: Exp1[T1]) = Or(this, b)
    def and[T2](b: Exp1[T2]) = And2(this, b)
  }
  case class TermValue[T1](v: T1) extends Exp1[T1]
  case class Not[T1](e: Exp1[T1]) extends Exp1[T1]
  case class Or[T1](e1: Exp1[T1], e2: Exp1[T1]) extends Exp1[T1]

  trait Exp2[T1, T2] extends Expression
  case class And2[T1, T2](e1: Exp1[T1], e2: Exp1[T2]) extends Exp2[T1, T2] {
    def and[T3](e3: Exp1[T3]) = And3(e1, e2, e3)
    def or(that: And2[T1, T2]) = Or2(this, that)
  }
  case class Or2[T1, T2](e1: Exp2[T1, T2], e2: Exp2[T1, T2]) extends Exp2[T1, T2] {
    def or(e3: Exp2[T1, T2]) = Or2(e1, Or2(e2, e3))
  }

  trait Exp3[T1, T2, T3] extends Expression
  case class And3[T1, T2, T3](e1: Exp1[T1], e2: Exp1[T2], e3: Exp1[T3]) extends Exp3[T1, T2, T3]



  // From sqltyped...

  //  sealed trait Comparison[T] extends Expression
  //  case class Comparison1[T](t: TermValue[T], op: Operator1) extends Comparison[T]
  //  case class Comparison2[T](t1: TermValue[T], op: Operator2, t2: TermValue[T]) extends Comparison[T]
  //  case class Comparison3[T](t1: TermValue[T], op: Operator3, t2: TermValue[T], t3: TermValue[T]) extends Comparison[T]
  //
  //  sealed trait Operator1
  //  case object IsNull extends Operator1
  //  case object IsNotNull extends Operator1
  //  case object Exists extends Operator1
  //  case object NotExists extends Operator1
  //
  //  sealed trait Operator2
  //  case object Eq extends Operator2
  //  case object Neq extends Operator2
  //  case object Lt extends Operator2
  //  case object Gt extends Operator2
  //  case object Le extends Operator2
  //  case object Ge extends Operator2
  //  case object Inx extends Operator2
  //  case object NotIn extends Operator2
  //  case object Like extends Operator2
  //
  //  sealed trait Operator3
  //  case object Between extends Operator3
  //  case object NotBetween extends Operator3
}