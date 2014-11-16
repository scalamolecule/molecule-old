package molecule
package ops
import java.net.URI
import java.util.{Date, UUID}
import molecule.ast.model._
import molecule.ast.query._
import molecule.util.MacroHelpers
import scala.reflect.macros.whitebox.Context

trait Liftables[Ctx <: Context] extends MacroHelpers[Ctx] {
  import c.universe._
  val z = Debug("Liftables", 1, 10, true)


  // General liftables --------------------------------------------------------------

  def mkDate(date: Date) = q"new Date(${date.getTime})"
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
    case uuid: UUID                    => mkUUID(uuid)
    case uri: URI                      => mkURI(uri)
    case qm: Qm.type                   => q"Qm"
    case entValue: EntValue.type       => q"EntValue"
    case varValue: VarValue.type       => q"VarValue"
    case Fn(value)                     => q"Fn($value)"
//    case Eq(value)                     => q"Eq($value)"
    case other                         => abort("[Liftables:liftAny] Can't lift unexpected Any type: " + other.getClass)
  }

//  implicit val liftEq = Liftable[Eq] { Eq => q"Eq(Seq(..${Eq.values}))"}


  implicit val liftTuple2 = Liftable[Product] {
    case (k: String, v: String) => q"($k, $v)"
    case (k: Int, v: Int)       => q"($k, $v)"
    case (k: Long, v: Long)     => q"($k, $v)"
    case (k: Float, v: Float)   => q"($k, $v)"
    case (k: Double, v: Double) => q"($k, $v)"
    case (k: Date, v: Date)     => q"(${mkDate(k)}, ${mkDate(v)})"
    case (k: UUID, v: UUID)     => q"(${mkUUID(k)}, ${mkUUID(v)})"
    case (k: URI, v: URI)       => q"(${mkURI(k)}, ${mkURI(v)})"
    case (a, b)                 => abort(s"[Liftables:liftTuple2] Can't lift unexpected Tuple2: ($a, $b)")
    case other                  => abort(s"[Liftables:liftTuple2] Can't lift unexpected product type: $other")
  }


  // Liftables for Query --------------------------------------------------------------

  implicit val liftVar    = Liftable[Var] { v => q"Var(${v.v})"}
  implicit val liftVal    = Liftable[Val] { value => q"Val(${value.v})"}
  implicit val liftAttrKW = Liftable[KW] { kw => q"KW(${kw.ns}, ${kw.attr}, ${kw.refNs})"}
  implicit val liftWith   = Liftable[With] { widh => q"With(Seq(..${widh.variables}))"}

  implicit val liftQueryValue = Liftable[QueryValue] {
    case Var(sym) => q"Var($sym)"
    case Val(v)   => q"Val($v)"
    case Dummy(v) => q"Dummy($v)"
    case NoVal    => q"NoVal"
  }

  implicit val liftDataSource = Liftable[DataSource] {
    case DS(name) => q"DS($name)"
    case DS       => q"DS"
    case ImplDS   => q"ImplDS"
  }

  implicit val liftQueryTerm = Liftable[QueryTerm] {
    case KW(ns, attr, refNs) => q"KW($ns, $attr, $refNs)"
    case Empty               => q"Empty"
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
  implicit val liftFind   = Liftable[Find] { find => q"Find(Seq(..${find.outputs}))"}

  implicit val liftBinding = Liftable[Binding] {
    case NoBinding               => q"NoBinding"
    case ScalarBinding(name)     => q"ScalarBinding($name)"
    case CollectionBinding(name) => q"CollectionBinding($name)"
    case TupleBinding(names)     => q"TupleBinding(Seq(..$names))"
    case RelationBinding(names)  => q"RelationBinding(Seq(..$names))"
  }

  implicit val liftDataClause = Liftable[DataClause] { cl =>
    q"DataClause(${cl.ds}, ${cl.e}, ${cl.a}, ${cl.v}, ${cl.tx})"
  }

  implicit val liftRule = Liftable[Rule] { rd =>
    q"Rule(${rd.name}, Seq(..${rd.args}), Seq(..${rd.clauses}))"
  }

  implicit val liftInput = Liftable[Input] {
    case InDataSource(ds, argss)           => q"InDataSource($ds, Seq(...$argss))"
    case InVar(binding, argss)             => q"InVar($binding, Seq(...$argss))"
    case Placeholder(v, kw, enumPrefix, e) => q"Placeholder($v, $kw, $enumPrefix, $e)"
  }

  implicit val liftClause = Liftable[Clause] {
    case DataClause(ds, entity, attr, value, tx) => q"DataClause($ds, $entity, $attr, $value, $tx)"
    case RuleInvocation(name, args)              => q"RuleInvocation($name, Seq(..$args))"
    case Predicate(name, args)                   => q"Predicate($name, Seq(..$args))"
    case Funct(name, ins, outs)                  => q"Funct($name, Seq(..$ins), $outs)"
  }

  implicit val liftIn    = Liftable[In] { in => q"In(Seq(..${in.inputs}), Seq(..${in.rules}), Seq(..${in.ds}))"}
  implicit val liftWhere = Liftable[Where] { where => q"Where(Seq(..${where.clauses}))"}
  implicit val liftQuery = Liftable[Query] { q =>
    q"import molecule.ast.query._; Query(${q.f}, ${q.wi}, ${q.i}, ${q.wh})"
  }


  // Liftables for Model --------------------------------------------------------------

  implicit val liftTerm = Liftable[Value] {
    //    case NoValue          => q"NoValue"
    //    case Blank            => q"Blank"
    case EntValue         => q"EntValue"
    case VarValue         => q"VarValue"
    case BackValue(value) => q"BackValue($value)"
    case EnumVal          => q"EnumVal"
    case Eq(values)       => q"Eq(Seq(..$values))"
    case Lt(value)        => q"Lt($value)"
    case Fn(value)        => q"Fn($value)"
    case Qm               => q"Qm"
    //    case QmR              => q"QmR"
    case Fulltext(search) => q"Fulltext(Seq(..$search))"
    case Replace(values)  => q"Replace($values)"
    case Remove(values)   => q"Remove(Seq(..$values))"
  }

  implicit val liftAtom  = Liftable[Atom] { a => q"Atom(${a.ns}, ${a.name}, ${a.tpeS}, ${a.card}, ${a.value}, ${a.enumPrefix})"}
  implicit val liftBond  = Liftable[Bond] { b => q"Bond(${b.ns}, ${b.refAttr}, ${b.refNs})"}
  implicit val liftGroup = Liftable[Group] { g =>
    val es = g.elements map { case q"$e" => e}
    q"Group(${g.ref}, Seq(..$es))"
  }
  implicit val liftMeta  = Liftable[Meta] { a => q"Meta(${a.ns}, ${a.attr}, ${a.kind}, ${a.tpe}, ${a.value})"}
  //  implicit val liftEmptyElement = Liftable[EmptyElement] { a => q"EmptyElement"}
  //  implicit val liftSubComponent = Liftable[SubComponent] { b => q"SubComponent(${b.ns}, ${b.parentEid})"}

  implicit val liftListOfElements = Liftable[Seq[Element]] { elements =>
    val es = elements map {
      case a: Atom  => q"$a"
      case b: Bond  => q"$b"
      case g: Group => q"$g"
      case m: Meta  => q"$m"
      //      case m: EmptyElement => q"$m"
    }
    q"Seq(..$es)"
  }
  //      case n: SubComponent => q"$n"

  implicit val liftElement = Liftable[Element] {
    case Atom(ns, name, tpeS, card, value, enumPrefix) => q"Atom($ns, $name, $tpeS, $card, $value, $enumPrefix)"
    case Bond(ns, refAttr, refNs)                      => q"Bond($ns, $refAttr, $refNs)"
    case Group(ref, elements)                          => q"Group($ref, $elements)"
    case Meta(ns, attr, kind, tpe, value)              => q"Meta($ns, $attr, $kind, $tpe, $value)"
    case EmptyElement                                  => q"EmptyElement"
  }
  //    case SubComponent(ns, parentEid)                   => q"SubComponent($ns, $parentEid)"

  implicit val liftModel = Liftable[Model] { model => q"Model(Seq(..${model.elements}))"}
}
