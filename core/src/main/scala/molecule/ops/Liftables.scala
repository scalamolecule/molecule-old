package molecule.ops

import java.net.URI
import java.util.{Date, UUID}
import molecule.ast.model._
import molecule.ast.query._
import molecule.util.MacroHelpers
import scala.collection.immutable.HashSet
import scala.collection.immutable.Set.{Set1, Set2, Set3, Set4}
import scala.reflect.macros.whitebox.Context

private[molecule] trait Liftables[Ctx <: Context] extends MacroHelpers[Ctx] {
  import c.universe._
  val z = DebugMacro("Liftables", 1, 10, true)


  // General liftables --------------------------------------------------------------

  def mkDate(date: Date) = q"new Date(${date.getTime})"
  def mkBigInt(bigInt: BigInt) = q"BigInt.apply(${bigInt.toString()})"
  def mkBigDecimal(bigDec: BigDecimal) = q"BigDecimal.apply(${bigDec.toString()})"
  def mkUUID(uuid: UUID) = q"java.util.UUID.fromString(${uuid.toString})"
  def mkURI(uri: URI) = q"new java.net.URI(${uri.getScheme}, ${uri.getUserInfo}, ${uri.getHost}, ${uri.getPort}, ${uri.getPath}, ${uri.getQuery}, ${uri.getFragment})"

  implicit val liftAny = Liftable[Any] {
    case Literal(Constant(s: String))  => q"$s"
    case Literal(Constant(i: Int))     => q"$i"
    case Literal(Constant(l: Long))    => q"$l"
    case Literal(Constant(f: Float))   => q"$f"
    case Literal(Constant(d: Double))  => q"$d"
    case Literal(Constant(b: Boolean)) => q"$b"
    case s: String                     => q"$s"
    case i: Int                        => q"$i"
    case l: Long                       => q"$l"
    case f: Float                      => q"$f"
    case d: Double                     => q"$d"
    case b: Boolean                    => q"$b"
    case date: Date                    => mkDate(date)
    case bigInt: BigInt                => mkBigInt(bigInt)
    case bigDec: BigDecimal            => mkBigDecimal(bigDec)
    case uuid: UUID                    => mkUUID(uuid)
    case uri: URI                      => mkURI(uri)
    case qm: Qm.type                   => q"Qm"
    case maybe: Distinct.type          => q"Distinct"
    case entValue: EntValue.type       => q"EntValue"
    case varValue: VarValue.type       => q"VarValue"
    case set: Set[_]                   => set match {
      case s1: Set1[_]   => q"Set(${any(s1.head)})"
      case s2: Set2[_]   => q"Set(..${s2 map any})"
      case s3: Set3[_]   => q"Set(..${s3 map any})"
      case s4: Set4[_]   => q"Set(..${s4 map any})"
      case s: HashSet[_] => q"Set(..${s map any})"
      case emptySet      => q"Set()"
    }
    case other                         =>
      abort("[Liftables:liftAny] Can't lift unexpected Any type: " + other.getClass +
        "\nMaybe you are applying some Scala expression to a molecule attribute?" +
        "\nTry to assign the expression to a variable and apply the variable instead.")
  }

  def any(v: Any) = v match {
    case Literal(Constant(s: String))  => q"$s"
    case Literal(Constant(i: Int))     => q"$i"
    case Literal(Constant(l: Long))    => q"$l"
    case Literal(Constant(f: Float))   => q"$f"
    case Literal(Constant(d: Double))  => q"$d"
    case Literal(Constant(b: Boolean)) => q"$b"
    case s: String                     => q"$s"
    case i: Int                        => q"$i"
    case l: Long                       => q"$l"
    case f: Float                      => q"$f"
    case d: Double                     => q"$d"
    case b: Boolean                    => q"$b"
    case date: Date                    => mkDate(date)
    case bigInt: BigInt                => mkBigInt(bigInt)
    case bigDec: BigDecimal            => mkBigDecimal(bigDec)
    case uuid: UUID                    => mkUUID(uuid)
    case uri: URI                      => mkURI(uri)
  }

  implicit val liftTuple2 = Liftable[Product] {
    case (k: String, v: String)         => q"($k, $v)"
    case (k: Int, v: Int)               => q"($k, $v)"
    case (k: Long, v: Long)             => q"($k, $v)"
    case (k: Float, v: Float)           => q"($k, $v)"
    case (k: Double, v: Double)         => q"($k, $v)"
    case (k: Date, v: Date)             => q"(${mkDate(k)}, ${mkDate(v)})"
    case (k: BigInt, v: BigInt)         => q"(${mkBigInt(k)}, ${mkBigInt(v)})"
    case (k: BigDecimal, v: BigDecimal) => q"(${mkBigDecimal(k)}, ${mkBigDecimal(v)})"
    case (k: UUID, v: UUID)             => q"(${mkUUID(k)}, ${mkUUID(v)})"
    case (k: URI, v: URI)               => q"(${mkURI(k)}, ${mkURI(v)})"
    case (a, b)                         => abort(s"[Liftables:liftTuple2] Can't lift unexpected Tuple2: ($a, $b)")
    case other                          => abort(s"[Liftables:Product] Can't lift unexpected product type: $other")
  }


  // Liftables for Query --------------------------------------------------------------

  implicit val liftVar    = Liftable[Var] { v => q"Var(${v.v})" }
  implicit val liftVal    = Liftable[Val] { value => q"Val(${value.v})" }
  implicit val liftAttrKW = Liftable[KW] { kw => q"KW(${kw.ns}, ${kw.attr}, ${kw.refNs})" }
  implicit val liftWith   = Liftable[With] { widh => q"With(Seq(..${widh.variables}))" }

  implicit val liftQueryValue = Liftable[QueryValue] {
    case Var(sym)                      => q"Var($sym)"
    case Val(v)                        => q"Val($v)"
    case Pull(e, ns, attr, enumPrefix) => q"Pull($e, $ns, $attr, $enumPrefix)"
    case Dummy                         => q"Dummy"
    case NoVal                         => q"NoVal"
  }

  implicit val liftDataSource = Liftable[DataSource] {
    case DS(name) => q"DS($name)"
    case DS       => q"DS"
    case ImplDS   => q"ImplDS"
  }

  implicit val liftQueryTerm = Liftable[QueryTerm] {
    case KW(ns, attr, refNs) => q"KW($ns, $attr, $refNs)"
    case Empty               => q"Empty"
    case NoBinding           => q"NoBinding"
    case Var(sym)            => q"Var($sym)"
    case Val(v)              => q"Val($v)"
    case DS(name)            => q"DS($name)"
    case DS                  => q"DS"
    case ImplDS              => q"ImplDS"
    case t                   => abort("[Liftables:liftQueryTerm] Can't lift query term: " + t)
  }

  implicit val liftOutput = Liftable[Output] {
    case AggrExpr(fn, args, v) => q"AggrExpr($fn, Seq(..$args), $v)"
    case Var(sym)              => q"Var($sym)"
  }
  implicit val liftFind   = Liftable[Find] { find => q"Find(Seq(..${find.outputs}))" }

  implicit val liftBinding = Liftable[Binding] {
    case NoBinding               => q"NoBinding"
    case ScalarBinding(name)     => q"ScalarBinding($name)"
    case CollectionBinding(name) => q"CollectionBinding($name)"
    case TupleBinding(names)     => q"TupleBinding(Seq(..$names))"
    case RelationBinding(names)  => q"RelationBinding(Seq(..$names))"
  }

  implicit val liftInput = Liftable[Input] {
    case InDataSource(ds, argss)           => q"InDataSource($ds, Seq(...$argss))"
    case InVar(binding, argss)             => q"InVar($binding, Seq(...$argss))"
    case Placeholder(e, kw, v, enumPrefix) => q"Placeholder($e, $kw, $v, $enumPrefix)"
  }

  implicit val liftDataClause     = Liftable[DataClause] { dc => q"DataClause(${dc.ds}, ${dc.e}, ${dc.a}, ${dc.v}, ${dc.tx}, ${dc.op})" }
  implicit val liftNotClause      = Liftable[NotClause] { nc => q"NotClause(${nc.e}, ${nc.a})" }
  implicit val liftRuleInvocation = Liftable[RuleInvocation] { ri => q"RuleInvocation(${ri.name}, Seq(..${ri.args}))" }
  implicit val liftFunct          = Liftable[Funct] { f => q"Funct(${f.name}, Seq(..${f.ins}), ${f.outs})" }

  implicit val liftNotClauses = Liftable[NotClauses] { notClauses =>
    val clauses = notClauses.clauses map {
      case cl: DataClause     => q"$cl"
      case cl: NotClause      => q"$cl"
      case cl: RuleInvocation => q"$cl"
      case cl: Funct          => q"$cl"
      case q"$e"              => e
    }
    q"NotClauses(Seq(..$clauses))"
  }


  implicit val liftListOfClauses = Liftable[Seq[Clause]] { clauses =>
    val cls = clauses map {
      case cl: DataClause     => q"$cl"
      case cl: NotClause      => q"$cl"
      case cl: NotClauses     => q"$cl"
      case cl: RuleInvocation => q"$cl"
      case cl: Funct          => q"$cl"
    }
    q"Seq(..$cls)"
  }

  implicit val liftClause = Liftable[Clause] {
    case DataClause(ds, e, a, v, tx, op) => q"DataClause($ds, $e, $a, $v, $tx, $op)"
    case NotClause(e, a)                 => q"NotClause($e, $a)"
    case NotClauses(clauses)             => q"NotClauses($clauses)"
    case RuleInvocation(name, args)      => q"RuleInvocation($name, Seq(..$args))"
    case Funct(name, ins, outs)          => q"Funct($name, Seq(..$ins), $outs)"
  }

  implicit val liftRule  = Liftable[Rule] { rd => q"Rule(${rd.name}, Seq(..${rd.args}), Seq(..${rd.clauses}))" }
  implicit val liftIn    = Liftable[In] { in => q"In(Seq(..${in.inputs}), Seq(..${in.rules}), Seq(..${in.ds}))" }
  implicit val liftWhere = Liftable[Where] { where => q"Where(Seq(..${where.clauses}))" }
  implicit val liftQuery = Liftable[Query] { q => q"import molecule.ast.query._; Query(${q.f}, ${q.wi}, ${q.i}, ${q.wh})" }


  // Liftables for Model --------------------------------------------------------------


  implicit val liftBidirectional = Liftable[Bidirectional] {
    case BiSelfRef(card)             => q"BiSelfRef($card)"
    case BiSelfRefAttr(card)         => q"BiSelfRefAttr($card)"
    case BiOtherRef(card, attr)      => q"BiOtherRef($card, $attr)"
    case BiOtherRefAttr(card, attr)  => q"BiOtherRefAttr($card, $attr)"
    case BiEdge                      => q"BiEdge"
    case BiEdgeRef(card, attr)       => q"BiEdgeRef($card, $attr)"
    case BiEdgeRefAttr(card, attr)   => q"BiEdgeRefAttr($card, $attr)"
    case BiEdgePropRef(card)         => q"BiEdgePropRef($card)"
    case BiEdgePropAttr(card)        => q"BiEdgePropAttr($card)"
    case BiEdgePropRefAttr(card)     => q"BiEdgePropRefAttr($card)"
    case BiTargetRef(card, attr)     => q"BiTargetRef($card, $attr)"
    case BiTargetRefAttr(card, attr) => q"BiTargetRefAttr($card, $attr)"
  }

  implicit val liftGeneric = Liftable[Generic] {
    case NsValue(values)             => q"NsValue(Seq(..$values))"
    case AttrVar(v)                  => q"AttrVar($v)"
    case TxValue(t)                  => q"TxValue($t)"
    case TxValue_(t)                 => q"TxValue_($t)"
    case TxTValue(t)                 => q"TxTValue($t)"
    case TxTValue_(t)                => q"TxTValue_($t)"
    case TxInstantValue(tx)          => q"TxInstantValue($tx)"
    case TxInstantValue_(tx)         => q"TxInstantValue_($tx)"
    case OpValue(added)              => q"OpValue($added)"
    case OpValue_(added)             => q"OpValue_($added)"
    case NoValue                     => q"NoValue"
    case Id(eid)                     => q"Id($eid)"
    case Card(card)                  => q"Card($card)"
    case BiSelfRef(card)             => q"BiSelfRef($card)"
    case BiSelfRefAttr(card)         => q"BiSelfRefAttr($card)"
    case BiOtherRef(card, attr)      => q"BiOtherRef($card, $attr)"
    case BiOtherRefAttr(card, attr)  => q"BiOtherRefAttr($card, $attr)"
    case BiEdge                      => q"BiEdge"
    case BiEdgeRef(card, attr)       => q"BiEdgeRef($card, $attr)"
    case BiEdgeRefAttr(card, attr)   => q"BiEdgeRefAttr($card, $attr)"
    case BiEdgePropRef(card)         => q"BiEdgePropRef($card)"
    case BiEdgePropAttr(card)        => q"BiEdgePropAttr($card)"
    case BiEdgePropRefAttr(card)     => q"BiEdgePropRefAttr($card)"
    case BiTargetRef(card, attr)     => q"BiTargetRef($card, $attr)"
    case BiTargetRefAttr(card, attr) => q"BiTargetRefAttr($card, $attr)"
  }

  implicit val liftFn    = Liftable[Fn] { fn => q"Fn(${fn.name}, ${fn.value})" }
  implicit val liftValue = Liftable[Value] {
    case EntValue                     => q"EntValue"
    case NsValue(values)              => q"NsValue(Seq(..$values))"
    case VarValue                     => q"VarValue"
    case AttrVar(v)                   => q"AttrVar($v)"
    case TxValue(t)                   => q"TxValue($t)"
    case TxValue_(t)                  => q"TxValue_($t)"
    case TxTValue(t)                  => q"TxTValue($t)"
    case TxTValue_(t)                 => q"TxTValue_($t)"
    case TxInstantValue(tx)           => q"TxInstantValue($tx)"
    case TxInstantValue_(tx)          => q"TxInstantValue_($tx)"
    case OpValue(added)               => q"OpValue($added)"
    case OpValue_(added)              => q"OpValue_($added)"
    case NoValue                      => q"NoValue"
    case Id(eid)                      => q"Id($eid)"
    case Card(card)                   => q"Card($card)"
    case BiSelfRef(card)              => q"BiSelfRef($card)"
    case BiSelfRefAttr(card)          => q"BiSelfRefAttr($card)"
    case BiOtherRef(card, attr)       => q"BiOtherRef($card, $attr)"
    case BiOtherRefAttr(card, attr)   => q"BiOtherRefAttr($card, $attr)"
    case BiEdge                       => q"BiEdge"
    case BiEdgeRef(card, attr)        => q"BiEdgeRef($card, $attr)"
    case BiEdgeRefAttr(card, attr)    => q"BiEdgeRefAttr($card, $attr)"
    case BiEdgePropRef(card)          => q"BiEdgePropRef($card)"
    case BiEdgePropAttr(card)         => q"BiEdgePropAttr($card)"
    case BiEdgePropRefAttr(card)      => q"BiEdgePropRefAttr($card)"
    case BiTargetRef(card, attr)      => q"BiTargetRef($card, $attr)"
    case BiTargetRefAttr(card, attr)  => q"BiTargetRefAttr($card, $attr)"
    case BackValue(value)             => q"BackValue($value)"
    case EnumVal                      => q"EnumVal"
    case IndexVal                     => q"IndexVal"
    case And(values)                  => q"And(Seq(..$values))"
    case Eq(values) if values.isEmpty => q"Eq(Nil)"
    case Eq(values)                   => q"Eq(Seq(..$values))"
    case Neq(values)                  => q"Neq(Seq(..$values))"
    case Lt(value)                    => q"Lt($value)"
    case Gt(value)                    => q"Gt($value)"
    case Le(value)                    => q"Le($value)"
    case Ge(value)                    => q"Ge($value)"
    case Fn(fn, value)                => q"Fn($fn, $value)"
    case Length(fn)                   => q"Length($fn)"
    case Qm                           => q"Qm"
    case Distinct                     => q"Distinct"
    case Fulltext(search)             => q"Fulltext(Seq(..$search))"
    case AssertValue(values)          => q"AssertValue(Seq(..$values))"
    case ReplaceValue(oldNew)         => q"ReplaceValue(Seq(..$oldNew))"
    case RetractValue(values)         => q"RetractValue(Seq(..$values))"
    case AssertMapPairs(pairs)        => q"AssertMapPairs(Seq(..$pairs))"
    case ReplaceMapPairs(pairs)       => q"ReplaceMapPairs(Seq(..$pairs))"
    case RetractMapKeys(keys)         => q"RetractMapKeys(Seq(..$keys))"
    case MapEq(pairs)                 => q"MapEq(Seq(..$pairs))"
    case MapKeys(keys)                => q"MapKeys(Seq(..$keys))"
  }

  implicit val liftAtom       = Liftable[Atom] { a => q"Atom(${a.ns}, ${a.name}, ${a.tpeS}, ${a.card}, ${a.value}, ${a.enumPrefix}, Seq(..${a.gs}), Seq(..${a.keys}))" }
  implicit val liftBond       = Liftable[Bond] { b => q"Bond(${b.ns}, ${b.refAttr}, ${b.refNs}, ${b.card}, Seq(..${b.gs}))" }
  implicit val liftReBond     = Liftable[ReBond] { r => q"ReBond(${r.backRef}, ${r.refAttr}, ${r.refNs}, ${r.distinct}, ${r.prevVar})" }
  implicit val liftTransitive = Liftable[Transitive] { r => q"Transitive(${r.backRef}, ${r.refAttr}, ${r.refNs}, ${r.depth}, ${r.prevVar})" }
  implicit val liftMeta       = Liftable[Meta] { m => q"Meta(${m.ns}, ${m.attr}, ${m.kind}, ${m.generic}, ${m.value})" }
  implicit val liftGroup      = Liftable[Nested] { g0 =>
    val es0 = g0.elements map {
      case a: Atom       => q"$a"
      case b: Bond       => q"$b"
      case r: ReBond     => q"$r"
      case r: Transitive => q"$r"
      case Self          => q"Self"
      case m: Meta       => q"$m"
      case g1: Nested    => {
        val es1 = g1.elements map {
          case a: Atom       => q"$a"
          case b: Bond       => q"$b"
          case r: ReBond     => q"$r"
          case r: Transitive => q"$r"
          case Self          => q"Self"
          case m: Meta       => q"$m"
        }
        q"Nested(${g1.bond}, Seq(..$es1))"
      }
    }
    q"Nested(${g0.bond}, Seq(..$es0))"
  }

  implicit val liftTxMetaData = Liftable[TxMetaData] { tm =>
    val es = tm.elements map {
      case a: Atom       => q"$a"
      case b: Bond       => q"$b"
      case r: ReBond     => q"$r"
      case r: Transitive => q"$r"
      case Self          => q"Self"
      case m: Meta       => q"$m"
      case q"$e"         => e
    }
    q"TxMetaData(Seq(..$es))"
  }

  implicit val liftComposite = Liftable[Composite] { fm =>
    val es = fm.elements map {
      case a: Atom       => q"$a"
      case b: Bond       => q"$b"
      case r: ReBond     => q"$r"
      case r: Transitive => q"$r"
      case Self          => q"Self"
      case m: Meta       => q"$m"
      case q"$e"         => e
    }
    q"Composite(Seq(..$es))"
  }

  implicit val liftListOfElements = Liftable[Seq[Element]] { elements =>
    val es = elements map {
      case a: Atom       => q"$a"
      case b: Bond       => q"$b"
      case r: ReBond     => q"$r"
      case r: Transitive => q"$r"
      case Self          => q"Self"
      case g: Nested     => q"$g"
      case m: Meta       => q"$m"
      case t: TxMetaData => q"$t"
      case c: Composite  => q"$c"
    }
    q"Seq(..$es)"
  }

  implicit val liftElement = Liftable[Element] {
    case Atom(ns, name, tpeS, card, value, enumPrefix, gs, keys) => q"Atom($ns, $name, $tpeS, $card, $value, $enumPrefix, Seq(..$gs), Seq(..$keys))"
    case Bond(ns, refAttr, refNs, card, gs)                      => q"Bond($ns, $refAttr, $refNs, $card, Seq(..$gs))"
    case ReBond(backRef, refAttr, refNs, distinct, prevVar)      => q"ReBond($backRef, $refAttr, $refNs, $distinct, $prevVar)"
    case Transitive(backRef, refAttr, refNs, level, prevVar)     => q"Transitive($backRef, $refAttr, $refNs, $level, $prevVar)"
    case Self                                                    => q"Self"
    case Nested(ref, elements)                                   => q"Nested($ref, $elements)"
    case Meta(ns, attr, kind, generic, value)                    => q"Meta($ns, $attr, $kind, $generic, $value)"
    case TxMetaData(elements)                                    => q"TxMetaData($elements)"
    case Composite(elements)                                     => q"Composite($elements)"
    case EmptyElement                                            => q"EmptyElement"
  }

  implicit val liftModel = Liftable[Model] { model => q"Model(Seq(..${model.elements}))" }
}
