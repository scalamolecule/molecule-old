package molecule.ast

object model {

  case class Model(elements: Seq[Element]) {
    override def toString = {
      def draw(elements: Seq[Element], indent: Int): Seq[String] = {
        val s = "  " * indent
        elements map {
          case Nested(bond, nestedElements) =>
            s"""|Nested(
                |$s  $bond,
                |$s  List(
                |$s    ${draw(nestedElements, indent + 2).mkString(s",\n$s    ")}))""".stripMargin
          case TxMetaData(nestedElements)   =>
            s"""|TxMetaData(List(
                |$s  ${draw(nestedElements, indent + 2).mkString(s",\n$s  ")}))""".stripMargin
          case TxMetaData_(nestedElements)  =>
            s"""|TxMetaData_(List(
                |$s  ${draw(nestedElements, indent + 2).mkString(s",\n$s  ")}))""".stripMargin
          case Composite(elements)          =>
            s"""|Composite(List(
                |$s  ${draw(elements, indent + 2).mkString(s",\n$s    ")}))""".stripMargin
          case other                        => s"$other"
        }
      }
      "Model(List(\n  " + draw(elements, 1).mkString(",\n  ") + "))"
    }
  }

  trait Element

  case class Atom(
    ns: String,
    name: String,
    tpeS: String,
    card: Int,
    value: Value,
    enumPrefix: Option[String] = None,
    gs: Seq[Generic] = Nil,
    keys: Seq[String] = Nil) extends Element

  case class Bond(
    ns: String,
    refAttr: String,
    refNs: String = "",
    card: Int,
    gs: Seq[Generic] = Nil) extends Element

  case class ReBond(
    backRef: String,
    refAttr: String,
    refNs: String = "",
    distinct: Boolean = false,
    prevVar: String = "") extends Element

  case class Transitive(
    backRef: String,
    refAttr: String,
    refNs: String,
    depth: Int = 1,
    prevVar: String = "") extends Element

  case class Nested(
    bond: Bond,
    elements: Seq[Element]) extends Element

  case class Meta(
    ns: String,
    attr: String,
    kind: String,
    generic: Generic,
    value: Value) extends Element

  case class TxMetaData(elements: Seq[Element]) extends Element
  case class TxMetaData_(elements: Seq[Element]) extends Element
  case class Composite(elements: Seq[Element]) extends Element
  case object Self extends Element
  case object EmptyElement extends Element


  sealed trait Value

  // Value
  case object EntValue extends Value
  case object VarValue extends Value
  case class BackValue(backNs: String) extends Value
  case object EnumVal extends Value
  case object IndexVal extends Value

  // Function
  case class Fulltext(search: Seq[Any]) extends Value
  case class Fn(name: String, value: Option[Int] = None) extends Value
  case class Length(fn: Option[Fn] = None) extends Value

  // Logic
  case class And(values: Seq[Any]) extends Value

  // Comparison (== != < > <= >=)
  case class Eq(values: Seq[Any]) extends Value
  case class Neq(values: Seq[Any]) extends Value
  case class Lt(value: Any) extends Value
  case class Gt(value: Any) extends Value
  case class Le(value: Any) extends Value
  case class Ge(value: Any) extends Value

  case object Qm extends Value
  case object Distinct extends Value

  // Action
  case class Add_(values: Seq[Any]) extends Value
  case class Replace(oldNew: Seq[(Any, Any)]) extends Value
  case class Remove(values: Seq[Any]) extends Value

  // Attribute Maps
  case class MapAdd(pairs: Seq[(String, Any)]) extends Value
  case class MapReplace(pairs: Seq[(String, Any)]) extends Value
  case class MapRemove(keys: Seq[String]) extends Value
  case class MapEq(pairs: Seq[(String, Any)]) extends Value
  case class MapKeys(ks: Seq[String]) extends Value


  sealed trait Generic extends Value

  case class AttrVar(v: String) extends Generic
  case object TxValue extends Generic
  case object TxValue_ extends Generic
  case object TxTValue extends Generic
  case object TxInstantValue extends Generic
  case object OpValue extends Generic
  case class NsValue(values: Seq[String]) extends Generic
  case object NoValue extends Generic
  case class Card(card: Int) extends Generic


  sealed trait Bidirectional  extends Generic

  case class BiSelfRef(card: Int) extends Bidirectional
  case class BiSelfRefAttr(card: Int) extends Bidirectional

  case class BiOtherRef(card: Int, attr: String) extends Bidirectional
  case class BiOtherRefAttr(card: Int, attr: String) extends Bidirectional

  case object BiEdge extends Bidirectional
  case class BiEdgeRef(card: Int, attr: String) extends Bidirectional
  case class BiEdgeRefAttr(card: Int, attr: String) extends Bidirectional

  case class BiEdgePropAttr(card: Int) extends Bidirectional
  case class BiEdgePropRefAttr(card: Int) extends Bidirectional
  case class BiEdgePropRef(card: Int) extends Bidirectional

  case class BiTargetRef(card: Int, attr: String) extends Bidirectional
  case class BiTargetRefAttr(card: Int, attr: String) extends Bidirectional

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
  case class Or3[T1, T2, T3](e1: Exp1[T1], e2: Exp1[T2], e3: Exp1[T3]) extends Exp3[T1, T2, T3]

  // Convenience methods .........................

  def curNs(e: Element) = e match {
    case Atom(ns, _, _, _, _, _, _, _)   => ns
    case Bond(ns, _, _, _, _)            => ns
    case Nested(Bond(ns, _, _, _, _), _) => ns
    case Meta(ns, _, _, _, _)            => ns
    case unexpected                      => sys.error("[model:curNs] Unexpected element: " + unexpected)
  }
}

