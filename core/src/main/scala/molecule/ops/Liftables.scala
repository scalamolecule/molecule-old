package molecule.ops
import molecule.ast.model._
import molecule.ast.query._
import molecule.util.MacroHelpers
import scala.reflect.macros.whitebox.Context

trait Liftables[Ctx <: Context] extends MacroHelpers[Ctx] {
  import c.universe._
  val z = debug("Liftables", 1, 10, true)


  // Lift allowed base types
  implicit val liftAny = Liftable[Any] {
    case s: String            => q"$s"
    case i: Int               => q"$i"
    case l: Long              => q"$l"
    case f: Float             => q"$f"
    case d: Double            => q"$d"
    case b: Boolean           => q"$b"
    case date: java.util.Date => q"new Date(${date.getTime})"
    case uuid: java.util.UUID => q"java.util.UUID.fromString(${uuid.toString})"
    case uri: java.net.URI    => q"new java.net.URI(${uri.getScheme}, ${uri.getUserInfo}, ${uri.getHost}, ${uri.getPort}, ${uri.getPath}, ${uri.getQuery}, ${uri.getFragment})"
    case other                => abort("[Liftables:liftAny] Can't lift unexpected base type: " + other.getClass)
  }

  // Liftables for Query --------------------------------------------------------------

  implicit val liftVar    = Liftable[Var] { v => q"Var(${v.v}, ${v.tpeS})"}
  implicit val liftVal    = Liftable[Val] { value => q"Val(${value.v}, ${value.tpeS})"}
  implicit val liftAttrKW = Liftable[KW] { kw => q"KW(${kw.ns}, ${kw.attr}, ${kw.refNs})"}
  implicit val liftWith   = Liftable[With] { widh => q"With(Seq(..${widh.variables}))"}

  implicit val liftQueryValue = Liftable[QueryValue] {
    case Var(sym, tpeS) => q"Var($sym, $tpeS)"
    case Val(v, tpeS)   => q"Val($v, $tpeS)"
    case Dummy(v)       => q"Dummy($v)"
    case NoVal          => q"NoVal"
  }

  implicit val liftDataSource = Liftable[DataSource] {
    case DS(name) => q"DS($name)"
    case DS       => q"DS"
    case ImplDS   => q"ImplDS"
  }

  implicit val liftQueryTerm = Liftable[QueryTerm] {
    case KW(ns, attr, refNs) => q"KW($ns, $attr, $refNs)"
    case Empty               => q"Empty"
    case Var(sym, tpeS)      => q"Var($sym, $tpeS)"
    case Val(v, tpeS)        => q"Val($v, $tpeS)"
    case DS(name)            => q"DS($name)"
    case DS                  => q"DS"
    case ImplDS              => q"ImplDS"
    case t                   => abort("[Liftables:liftQueryTerm] Can't lift query term: " + t)
  }

  implicit val liftOutput = Liftable[Output] {
    case AggrExpr(fn, args, v, tpeS) => q"AggrExpr($fn, Seq(..$args), $v, $tpeS)"
    case Var(sym, tpeS)              => q"Var($sym, $tpeS)"
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
    q"DataClause(${cl.ds}, ${cl.entity}, ${cl.attr}, ${cl.value}, ${cl.tx})"
  }

  implicit val liftRule = Liftable[Rule] { rd =>
    q"Rule(${rd.name}, Seq(..${rd.args}), Seq(..${rd.clauses}))"
  }

  implicit val liftInput = Liftable[Input] {
    case InDataSource(ds, argss)                 => q"InDataSource($ds, Seq(...$argss))"
    case InVar(binding, argss)                   => q"InVar($binding, Seq(...$argss))"
    case Placeholder(v, kw, tpeS, enumPrefix, e) => q"Placeholder($v, $kw, $tpeS, $enumPrefix, $e)"
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
    q"import molecule.ast.query._; Query(${q.find}, ${q.widh}, ${q.in}, ${q.where})"
  }

  // Liftables for Model --------------------------------------------------------------

  implicit val liftTerm = Liftable[Value] {
    case NoValue          => q"NoValue"
    case Blank            => q"Blank"
    case EntValue         => q"EntValue"
    case VarValue         => q"VarValue"
    case EnumVal          => q"EnumVal"
    case Eq(values)       => q"Eq(Seq(..$values))"
    case Lt(value)        => q"Lt($value)"
    case Fulltext(search) => q"Fulltext(Seq(..$search))"
    case Replace(values)  => {
      val v2 = values.map(v => (v._1, v._2) match {
        case (q"$a", q"$b") => a -> b
      }).toMap
      q"Replace($v2)"
    }
    case Remove(values)   => {
      val v2 = values match {case q"$a" => a}
      q"Remove(Seq(..$v2))"
    }
  }

  implicit val liftAtom  = Liftable[Atom] { a => q"Atom(${a.ns}, ${a.name}, ${a.tpeS}, ${a.card}, ${a.value}, ${a.enumPrefix})"}
  implicit val liftBond  = Liftable[Bond] { b => q"Bond(${b.ns}, ${b.refAttr}, ${b.refNs})"}
  implicit val liftNode  = Liftable[Node] { b => q"Node(${b.ns}, ${b.parentId})"}
  implicit val liftGroup = Liftable[Group] { g =>
    val es = g.elements map { case q"$e" => e}
    q"Group(${g.ref}, Seq(..$es))"
  }

  implicit val liftListOfElements = Liftable[Seq[Element]] { elements =>
    val es = elements map {
      case a: Atom  => q"$a"
      case b: Bond  => q"$b"
      case n: Node  => q"$n"
      case g: Group => q"$g"
    }
    q"Seq(..$es)"
  }

  implicit val liftElement = Liftable[Element] {
    case Atom(ns, name, tpeS, card, value, enumPrefix) => q"Atom($ns, $name, $tpeS, $card, $value, $enumPrefix)"
    case Bond(ns, refAttr, refNs)                      => q"Bond($ns, $refAttr, $refNs)"
    case Node(ns, parentId)                            => q"Node($ns, $parentId)"
    case Group(ref, elements)                          => q"Group($ref, $elements)"
  }

  implicit val liftModel = Liftable[Model] { model => q"Model(Seq(..${model.elements}))"}
}
